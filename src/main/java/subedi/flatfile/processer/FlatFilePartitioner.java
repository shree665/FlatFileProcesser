/**
 *
 */
package subedi.flatfile.processer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;

import subedi.flatfile.persistence.JobControl;
import subedi.flatfile.persistence.RefData;
import subedi.flatfile.persistence.enumerated.FrequencyEnum;
import subedi.flatfile.reader.GzipBufferedReaderFactory;
import subedi.flatfile.service.JobCtrlService;
import subedi.flatfile.service.RefDataService;
import subedi.flatfile.util.FlatFileUtil;

/**
 * Partitioner to create a step for each file that job process. It will store 
 * all the file information in step execution context and creates a new thread for each step
 * to process a file. While creating a step, if the file is empty, a new step will not get created. 
 *
 * @author vivek.subedi
 */
public class FlatFilePartitioner implements Partitioner {

	private final Logger logger = LoggerFactory.getLogger(FlatFilePartitioner.class);

	@Autowired
	private JobCtrlService jobCtrlService;

	@Autowired
	private RefDataService refDataService;

	private String jobName;
	private String databaseCode;
	private String encoding;
	private String delimiterCharacter;
	private String mappingName;
	private String fileType;

	public static final String UTF8_BOM = "\uFEFF";

	/**
	 * Create a set of distinct ExecutionContext instances together with
	 * a unique identifier for each one. The identifiers is unique file name
	 *
	 * @param gridSize - the size of the map to return
	 * @return a map with file name and its executionContext
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		//Partitions store in map of execution contexts
		Map<String, ExecutionContext> map = new HashMap<String, ExecutionContext>();

		String stagingFolderPath = FlatFileUtil.STAGING_PATH;
		String archiveFolderPath = FlatFileUtil.ARCHIVE_PATH;

		if (archiveFolderPath == null) {
			throw new IllegalStateException("Unable to find archive directory");
		}
		if (stagingFolderPath == null) {
			throw new IllegalStateException("Unable to find staging directory");
		}

		File stagingDirectory = new File(stagingFolderPath);

		Collection<File> fileList = FileUtils.listFiles(stagingDirectory, null, false);
		Collection<File> filteredFiles = FlatFileUtil.matchPatterFilePrefix(fileList, databaseCode);
		checkIntegrityOfFiles(filteredFiles);

		if(filteredFiles.isEmpty()) {
			logger.info("There are not any files to process that are in the /staging/ directory");
			return map;
		}

		//Tokenizer for each file
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(delimiterCharacter);

		//Retrieve only once refDatas for a partition
		List<RefData> myRefDatas = refDataService.getRefDataForDatabase(mappingName);

		Map<String, String> fileToDB2tableMap = new HashMap<String, String>();
		for (RefData refData : myRefDatas) {
			fileToDB2tableMap.put(refData.getId().getTableNameFromFile(), refData.getDb2TableName());
		}

		//List of file records that will actually be processed
		List<File> toBeProcessedFileRecords = filterFilesToBeProcessed(filteredFiles, fileToDB2tableMap);

		//Stores the files that need to be ignored for the iteration and will be processed on next iteration
		List<File> ignored = removeOldFileForSameTable(toBeProcessedFileRecords);

		//removes all the files that are not being process on iteration
		toBeProcessedFileRecords.removeAll(ignored);


		//Processing each file for a table that is in active ETL
		for (File file : toBeProcessedFileRecords) {

			ExecutionContext context = new ExecutionContext();

			try {
				BufferedReader in = null;
				String[] columnNamesLine = null;

				//reading the first line of the file i.e. header. If we can't read we throw IOException
				try {
					//using GzipBufferedReader to read the .gz file. If the filen is not .gz, regurlar BufferedReader will be used
					in = new GzipBufferedReaderFactory().create(file, encoding);
					String firstLine = in.readLine();

					//Checking for the Byte Order Marker in file
					if(firstLine.startsWith(UTF8_BOM)) {
						firstLine = firstLine.substring(1);
					}
					columnNamesLine = tokenizer.tokenize(firstLine).getValues();

					//If no data we archive the file and update the Doc and File Txns
					if(!in.ready() || StringUtils.isBlank(in.readLine())) {
						logger.info("No data to process for file: ["+file.getPath()+"], it will be archived.");

						String archiveFilePath = FlatFileUtil.archive(file, new File(archiveFolderPath), fileType, true);
						logger.info("File archived to ["+archiveFilePath+"]");

						in.close();
						file.delete();
						continue;
					}

					in.close();

				} catch (IOException e) {
					try {
						in.close();
						} catch (Exception e1) {
							//nothing to do
							};
					throw new IOException("Couldn't read file ["+file.getPath()+"]. Check for the file path "+e);
				}

				//getting jobControl for a file i.e. for db2 table
				JobControl jobControl = getJobControlForTable(file.getName(), fileToDB2tableMap);

				//putting necessary values on step execution context
				context.put(FlatFileUtil.FILE_COLUMN_NAME_KEY, columnNamesLine);
				context.put(FlatFileUtil.BEHAVIOR_KEY, jobControl.getBehaviorCode());
				context.put(FlatFileUtil.JOB_CONTROL_KEY, jobControl.getJobControlId());
				context.put(FlatFileUtil.MAPPING_NAME_KEY, mappingName);
				context.put(FlatFileUtil.DATABASECODE_KEY, databaseCode);
				context.put(FlatFileUtil.FILE_NAME_KEY, file.getPath());

				logger.info("Located file to process:"
						+ "\n\tFile: ["+file.getPath()+"]"
						+ "\n\tJobControl ID: ["+jobControl.getJobControlId()+"]"
						+ "\n\tETL Behavior: ["+jobControl.getBehaviorCode()+"]"
						);

			} catch (Exception e) {
				throw new IllegalArgumentException("Error on Partitioning! File error for ["+file.getPath()+"]", e);
			}

			map.put(file.getName(), context);

		}
		return map;
	}

	/**
	 * Job control for table.
	 *
	 * @param uploadedFileName - the uploaded file name
	 * @param fileToDB2tableMap - map of ref data
	 * @return the job control
	 */
	private JobControl getJobControlForTable(String uploadedFileName, Map<String, String> fileToDB2tableMap) {

		//retrieving table name from the file name and mapping that name to the db2 table name
		String fileTableName = FlatFileUtil.retrieveTableNameFromFile(uploadedFileName);
		String db2TableName = fileToDB2tableMap.get(fileTableName);

		//clearing prefix of a table from mapped oracle table name because to match with jobContrl's table name
		String tableName = FlatFileUtil.replaceCcmPrefixToTableName(db2TableName, databaseCode);
		JobControl jobControl = jobCtrlService.getJobControlForTable(jobName, databaseCode, tableName);

		return jobControl;
	}

	/**
	 * Checks integrity of file. if the checking is failed, it throw exception
	 *
	 * @param theFiles the the files
	 * @return void
	 */
	private void checkIntegrityOfFiles(Collection<File> theFiles) {
		for(File theFile : theFiles) {
			if(!theFile.exists()) {
				throw new IllegalStateException("The file denoted by server path ["+theFile.getPath()+"] does not exist");
			} else if (!theFile.isFile()) {
				throw new IllegalStateException("The file denoted by server path ["+theFile.getPath()+"] is not a file");
			} else if (!theFile.canRead()) {
				throw new IllegalStateException("Can not read file ["+theFile.getPath()+"]");
			}
		}
	}

	/**
	 * Removes the new files for same table that are to be processed. But those files will be processed on the next iteration
	 *
	 * @param toBeProcessedFiles - the list of to be processed files
	 * @return the list for files that need to be processed before other files and new files
	 */
	private List<File> removeOldFileForSameTable(List<File> toBeProcessedFiles) {

		//Checking that only one file per table, use the oldest one
		List<File> ignored = new ArrayList<File>();

		for(File outerLoopFile : toBeProcessedFiles) {
			String outlerLoopRecordName = FlatFileUtil.retrieveTableNameFromFile(outerLoopFile.getName());
			Date outlerLoopRecordDate = FlatFileUtil.getTimestampFromFileName(outerLoopFile.getName());

			for(File innerLoopFile : toBeProcessedFiles) {

				if(!outerLoopFile.equals(innerLoopFile)
					&& outlerLoopRecordName.equalsIgnoreCase(
					FlatFileUtil.retrieveTableNameFromFile(innerLoopFile.getName()))) {

					Date innerLoopDate = FlatFileUtil.getTimestampFromFileName(innerLoopFile.getName());

					if(outlerLoopRecordDate.after(innerLoopDate)) {
						ignored.add(outerLoopFile);
						logger.info("Ignoring file: ["+outerLoopFile.getName()+"] date: ["+outlerLoopRecordDate+"],"
								+ " newer than ["+innerLoopFile.getName()+"] date: ["+innerLoopDate+"]");
						logger.info("File will be processed on next round");
						break;
					}
				}
			}
		}

		return ignored;
	}


	/**
	 * Filter files to be processed by checking the active ETL from the uploaded files.
	 *
	 * @param files - collection of files
	 * @param fileToOracle2tableMap - mapping from ref_data table
	 * @return the list of files that needs to be processed
	 */
	private List<File> filterFilesToBeProcessed(Collection<File> files, Map<String, String> fileToOracletableMap) {

		//List of file records that will actually be processed
		List<File> toBeProcessedFileRecords = new ArrayList<File>();
		//checking each uploaded file for the ETL. If ETL is active we use that file
		for (File file : files) {

			//jobControl to check the ETL status
			JobControl jobControl = getJobControlForTable(file.getName(), fileToOracletableMap);

			//controlled by frequency code
			final boolean activateETL = determineIfEtlActive(jobControl.getFrequencyCode());

			if (activateETL) {
				toBeProcessedFileRecords.add(file);
			}
		}
		return toBeProcessedFileRecords;
	}

	/**
	 * Determine if ETL active or not.
	 *
	 * @param frequencyCode the frequency code
	 * @return true, if successful
	 */
	private boolean determineIfEtlActive(FrequencyEnum frequencyCode) {

		switch (frequencyCode) {
			case D:
				return true;
			case W:
				return true;
			case N:
				return false;
			default:
				logger.info("Unknown frequency code [" + frequencyCode + "]");
				throw new IllegalArgumentException("Unknown frequency code ["+frequencyCode+"]");
		}
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setDatabaseCode(String databaseCode) {
		this.databaseCode = databaseCode;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDelimiterCharacter(String delimiterCharacter) {
		this.delimiterCharacter = delimiterCharacter;
	}

	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
