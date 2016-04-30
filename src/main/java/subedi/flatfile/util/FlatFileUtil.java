package subedi.flatfile.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Utility class
 * 
 * @author vivek.subedi
 *
 */

public class FlatFileUtil {
	
	private static Logger logger = LoggerFactory.getLogger(FlatFileUtil.class);
	
	//Cisco Constants
	public static final String ORACLE_ICM_TABLE_PREFIX = "CCM_ICM_";
	public static final String ICM_DATABASECODE = "ICM";
	private static final String YEAR = "$YEAR";
	private static final String MONTH = "$MONTH";
	private static final String DAY = "$DAY";
	
	protected static final int BUFFER_SIZE = 2048;
	
	// Time stamp format for ICM
	public static final DateTimeFormatter ICM_FILE_TIMESTAMP_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
	public static final DateTimeFormatter ICM_FILE_TIMESTAMP_FORMAT_WITH_MILLISECOND = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SS");
	public static final DateTimeFormatter ICM_FILE_TIMESTAMP_FORMAT_WITH_MILLISECONDS = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSSSSS");
	public static final DateTimeFormatter ICM_FILE_DATE_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd");

	//Flat File Patterns
	public static final String FILE_FORMAT_REGEX = "dbo\\.(\\w+)\\.([\\d\\.]+)(\\.gz)?$";
	public static final String ICM_FILE_PREFIX_FORMAT_REGEX = "^inst(\\w+\\.+\\w+)";
	public static final DateTimeFormatter FLAT_FILE_TIMESTAMP_FORMAT = DateTimeFormat.forPattern("YYYYMMdd.HHmmss.SSSSSSS");
	
	//LOCAL TESTING VALUES (we should put these values in database and retrieve it from database)
	public static final String STAGING_PATH = "/apps/batch/staging/test/";
	public static final String WORKING_PATH = "/apps/batch/working/test/";
	public static final String ARCHIVE_PATH = "/apps/batch/archive/$YEAR/$MONTH/$DAY/test/";

	// Keys
	public static final String JOB_CONTROL_KEY = "JOB.CONTROL";
	public static final String FILE_COLUMN_NAME_KEY = "FILE.COLUMN.NAMES";
	public static final String JOB_START_TIME_KEY = "JOB.START.TIME";
	public static final String JOB_END_TIME_KEY = "JOB.END.TIME";
	public static final String BEHAVIOR_KEY = "BEHAVIOR";
	public static final String FILE_NAME_KEY = "FILE.NAME";
	public static final String FILE_UPLOAD_ID = "FILE.UPLOAD.ID";
	public static final String MAPPING_NAME_KEY = "MAPPING.NAME";
	public static final String TABLE_ALIAS_1_KEY = "TBL_ALIAS_1";
	public static final String TABLE_ALIAS_2_KEY = "TBL_ALIAS_2";
	public static final String DATABASECODE_KEY = "DATABASE.CODE";
	public static final String STAGING_PATH_KEY = "STAGING.PATH";
	public static final String WORKING_PATH_KEY = "WORKING.PATH";
	public static final String ARCHIVE_PATH_KEY = "ARCHIVE.PATH";
	public static final String DOT = ".";
	public static final String FILE_PREFIX = "file:";
	public static final String BACKWARD_SLASH = "\\";
	public static final String FORWARD_SLASH = "/";
	public static final String RUN_COUNT_KEY = "RUN.COUNT";
	public static final String MAX_RUN_COUNT_KEY = "MAX.RUN.COUNT";
	public static final String YES_GZIP_FLG = "Y";
	public static final String NO_GZIP_FLG = "N";
	public static final String GZIP_FILE_END_SUFFIX = ".gz";
	public static final String MAINT_APP = "SUBEDI_TEST";
	
	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);

		return sw.getBuffer().toString();
	}
	
	/**
	 * Gets the column insert labels.
	 *
	 * @param columnLabels - list of DB2 column names
	 * @return the column insert labels
	 */
	public static String getColumUpdateLabels(final List<String> columnLabels) {

		String partialSql = "";

		for (final String columnLabel : columnLabels) {
			partialSql += TABLE_ALIAS_1_KEY + DOT + columnLabel + " = "+TABLE_ALIAS_2_KEY+DOT+columnLabel+",";
		}

		// eliminate last comma
		partialSql = partialSql.substring(0, partialSql.length() - 1);

		return partialSql;
	}

	/**
	 * Gets the column labels to insert the values. Labels will be replaced by the actual values at runtime
	 *
	 * @param columnLabels - list of DB2 column names
	 * @param columnLabels2 
	 * @return the column values labels
	 */
	public static String getColumValuesLabels(List<String> columnLabelsFromFile, List<String> tableColumnNames) {
		String partialSql = "";

		for (int i=0; i < columnLabelsFromFile.size(); i++) {
			partialSql += ":" + columnLabelsFromFile.get(i).toLowerCase() + " AS "+tableColumnNames.get(i).toUpperCase()+",";
		}

		// eliminate last comma
		partialSql = partialSql.substring(0, partialSql.length() - 1);
		return partialSql;
	}


	/**
	 * Gets the update where clause for a table.
	 *
	 * @param idColumn - primary key of a DB2 table
	 * @return the update where clause
	 */
	public static String getUpdateWhereClause(final List<String> idColumn) {
		String partialSql = "";

		for (final String string : idColumn) {
			partialSql += TABLE_ALIAS_1_KEY+"."+string + "=" + TABLE_ALIAS_2_KEY+"."+string +" AND ";
		}

		//eliminate the AND at the end
		partialSql = partialSql.substring(0, partialSql.length() - 5);

		return partialSql;
	}

	/**
	 * Gets the update columns.
	 *
	 * @param columnLabels - list of DB2 column names
	 * @return the update columns
	 */
	public static String getUpdateColumns(final List<String> columnLabels)	{
		String partialSql = "";

		for (final String columnLabel : columnLabels) {
			partialSql += TABLE_ALIAS_2_KEY+"."+columnLabel +",";
		}

		// eliminate last comma
		partialSql = partialSql.substring(0, partialSql.length() - 1);

		return partialSql;
	}

	/**
	 * Retrieves table name from file using provided Regex pattern.
	 *
	 * @param fileName - the file name
	 * @return the tbleName from file if regex matches
	 */
	public static String retrieveTableNameFromFile(final String fileName) {

		String fileTableName = null;

		final Pattern pattern = Pattern.compile(FILE_FORMAT_REGEX);
		final Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			fileTableName = matcher.group(1);
			return fileTableName;
		} else {
			throw new IllegalArgumentException("File ["+fileName+"] is not in expected format, can not parse table name. "
					+ "Expected pattern is: ["+pattern.toString()+"]");
		}
	}

	/**
	 * Replace ccm prefix to table name to match the table name with JobControl table.
	 *
	 * @param tableName - the DB2 table name
	 * @param databaseCode - Code of database i.e. ICM or NICE
	 * @return the new table name without prefix
	 */
	public static String replaceCcmPrefixToTableName(final String tableName, final String databaseCode) {
		String replacedTableName = null;
		if (databaseCode.equalsIgnoreCase(ICM_DATABASECODE)) {
			replacedTableName = tableName.replace(ORACLE_ICM_TABLE_PREFIX, "");
		}
		return replacedTableName;
	}

	/**
	 * Gets the timestamp from file name.
	 *
	 * @param fileName - the file name
	 * @return the timestamp from file name when the regex pattern matches
	 */
	public static Date getTimestampFromFileName(final String fileName) {
		String fileTimestampString = null;

		final Pattern pattern = Pattern.compile(FILE_FORMAT_REGEX);
		final Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			fileTimestampString = matcher.group(2);
			Date fileTimestamp = null;
			try {
				fileTimestamp = DateTime.parse(fileTimestampString, FLAT_FILE_TIMESTAMP_FORMAT).toDate();
			} catch (final Exception e) {
				throw new IllegalArgumentException("File ["+fileName+"] is not in expected format, can not parse timestamp. "
						+ "Expected pattern is: ["+pattern.toString()+"]");
			}
			return fileTimestamp;
		} else {
			throw new IllegalArgumentException("File ["+fileName+"] is not in expected format, can not parse table name. "
					+ "Expected pattern is: ["+pattern.toString()+"]");
		}
	}

	/**
	 * Match pattern for file prefix to process only ICM or NICE files from /working/ directory.
	 *
	 * @param fileList - the collection of file
	 * @param databasCode - the database code to match files on, must be ICM or NICE
	 * @return the collection of pattern matched files
	 */
	public static Collection<File> matchPatterFilePrefix(final Collection<File> fileList, final String databaseCode) {
		final Collection<File> filteredFiles = fileList;
		final Collection<File> ignoredFiles = new ArrayList<File>();
		Pattern pattern = null;

		if (FlatFileUtil.ICM_DATABASECODE.equalsIgnoreCase(databaseCode)) {
			pattern = Pattern.compile(FlatFileUtil.ICM_FILE_PREFIX_FORMAT_REGEX);
		} else {
			throw new IllegalStateException("Invalid databasecode ["+databaseCode+"]");
		}

		//Filter out files that are not related to job
		for (final File file : filteredFiles) {
			final Matcher matcher = pattern.matcher(file.getName());
			if (!matcher.find()) {
				ignoredFiles.add(file);
				logger.info("The file ["+file.getName()+"] is not part of ["+databaseCode+"] ETL, it will not be processed");
			}
		}
		filteredFiles.removeAll(ignoredFiles);
		return filteredFiles;
	}

	public static void checkIntegrityOfDirectory(final File file) {
		if(!file.exists()) {
			throw new IllegalStateException("The path ["+file.getName()+"] does not exist");
		} else if (!file.isDirectory()) {
			throw new IllegalStateException("The path ["+file.getName()+"] is not a directory");
		}
	}

	public static String archive(final File sourceFile, final File destinationRoot, final String category,
			final boolean deleteAfterArchive) {
		String archiveFilePath = null;

		if (sourceFile.getName().endsWith(FlatFileUtil.GZIP_FILE_END_SUFFIX)) {
			archiveFilePath = noCompressionArchive(sourceFile, destinationRoot, category, deleteAfterArchive);
		} else {
			archiveFilePath = compressArchive(sourceFile.getPath(), destinationRoot.getPath(), category, deleteAfterArchive);
		}

		//Force delete
		if(deleteAfterArchive && new File(archiveFilePath).exists() && sourceFile.exists()) {
			sourceFile.deleteOnExit();
		}

		return archiveFilePath;
	}

	private static String compressArchive(String sourceFilePath, String destRootDirPath, String docTypeSubDirName, boolean deleteSource) {
		
		String newDestRootDirPath = replacePlaceholderValues(destRootDirPath);
		logger.info("archiving from [{}] to [{}]", sourceFilePath, newDestRootDirPath);
		
		File sourceFile = new File(sourceFilePath);
		File destRootDir = new File(newDestRootDirPath);
		
		try {
			Assert.isTrue(sourceFile.exists() && sourceFile.isFile(), "source file [" + sourceFile.getCanonicalPath() + "] does not exist or is not a file");

			String archiveFilePath = null;
			if (!destRootDir.exists() && destRootDir.mkdirs()) {
				logger.debug("Creating new directory: {}", destRootDir);
			}

			// Checking directory was created and we are able to write to it
			if (!destRootDir.exists() || !destRootDir.canWrite()) {
				logger.warn("Unable to create directory or Unable to wiite to directory : {}", destRootDir.getCanonicalPath());
				return archiveFilePath;
			}

			File archiveDir = destRootDir;

			if (docTypeSubDirName != null) {
				archiveDir = new File(archiveDir, docTypeSubDirName);
				archiveDir.mkdirs();
			}

			String gzippedFileName = sourceFile.getName() + ".gz";
			File gzippedArchiveFile = new File(archiveDir, gzippedFileName);

			if (gzip(sourceFile, gzippedArchiveFile)) {
				logger.debug("archived from [" + sourceFile.getCanonicalPath() + "] to [" + gzippedArchiveFile.getCanonicalPath() + "]");
				archiveFilePath = gzippedArchiveFile.getCanonicalPath();
			} else {
				logger.error("failed to archive from [" + sourceFile.getCanonicalPath() + "] to [" + gzippedArchiveFile.getCanonicalPath()
						+ "]");
			}

			if (deleteSource) {
				sourceFile.delete();
			}

			return archiveFilePath;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static boolean gzip(File sourceFile, File archiveFile) {
		return gzip(sourceFile, archiveFile, false);
	}
	
	public static boolean gzip(File sourceFile, File archiveFile, boolean throwsException) {
		byte inputData[] = new byte[BUFFER_SIZE];
		int inputCount = 0;

		InputStream unzippedFileInputStream = null;
		OutputStream gzipOutputStream = null;
		try {
			unzippedFileInputStream = new BufferedInputStream(new FileInputStream(sourceFile), BUFFER_SIZE);
			gzipOutputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(archiveFile)));
			while ((inputCount = unzippedFileInputStream.read(inputData, 0, BUFFER_SIZE)) != -1) {
				gzipOutputStream.write(inputData, 0, inputCount);
			}
			gzipOutputStream.flush();
			return true;
		} catch (IOException e) {
			logger.error("Could not gzip file: " + sourceFile.getAbsolutePath());
			if (throwsException) {
				throw new IllegalStateException("Could not zip file!", e);
			}
			return false;
		} finally {
			try {
				gzipOutputStream.close();
			} catch (Throwable e) {
				// ignore all throwables
			}
			try {
				unzippedFileInputStream.close();
			} catch (Throwable e) {
				// ignore all throwables
			}
		}
	}


	private static String replacePlaceholderValues(String archiveRootDir) {
		final Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		archiveRootDir = archiveRootDir.replace(YEAR, String.valueOf(year));

		final int month = calendar.get(Calendar.MONTH) + 1; // month is zero-based
		archiveRootDir = archiveRootDir.replace(MONTH, String.format("%02d", month));

		final int date = calendar.get(Calendar.DATE);
		archiveRootDir = archiveRootDir.replace(DAY, String.format("%02d", date));

		return archiveRootDir;
	}

	private static String noCompressionArchive(File sourceFile, File destRootDir, String docTypeSubDirName, boolean deleteSource) {
		try {
			Assert.isTrue(sourceFile.exists() && sourceFile.isFile(), "source file [" + sourceFile.getCanonicalPath() + "] does not exist or is not a file");

			String archiveRootDir = destRootDir.toString();
			Calendar calendar = Calendar.getInstance();
			
			int year = calendar.get(Calendar.YEAR);
			archiveRootDir = archiveRootDir.replace(YEAR, String.valueOf(year));
			
			int month = calendar.get(Calendar.MONTH) + 1; // month is zero-based
			archiveRootDir = archiveRootDir.replace(MONTH, String.format("%02d", month));
			
			int date = calendar.get(Calendar.DATE);
			archiveRootDir = archiveRootDir.replace(DAY, String.format("%02d", date));

			//final directory
			File archiveDir = new File(archiveRootDir);
			if(docTypeSubDirName != null) {
				archiveDir = new File(archiveDir, docTypeSubDirName);
			}
			archiveDir.mkdirs();

			//checking directory exists first
			Assert.isTrue(archiveDir.exists() && archiveDir.isDirectory(), "archive root directory [" + archiveDir.getCanonicalPath() + "] does not exist or is not a directory");
			
			File archiveFile = new File(archiveDir, sourceFile.getName());
			FileUtils.copyFile(sourceFile, archiveFile);
			String archiveFilePath = archiveFile.getPath();

			if(deleteSource) {
				sourceFile.delete();
			}

			return archiveFilePath;

		} catch(final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static CharSequence getColumValuesInserLabels(List<String> columnLabelsFromFile) {
		String partialSql = "";

		for (int i=0; i <= columnLabelsFromFile.size(); i++) {
			partialSql += ":" + columnLabelsFromFile.get(i).toLowerCase() +",";
		}

		// eliminate last comma
		partialSql = partialSql.substring(0, partialSql.length() - 1);
		return partialSql;
	}

	public static CharSequence getColumInsertLabels(List<String> columnLabels) {
		String partialSql = "";

		for (final String columnLabel : columnLabels) {
			partialSql += TABLE_ALIAS_1_KEY + DOT + columnLabel +",";
		}

		// eliminate last comma
		partialSql = partialSql.substring(0, partialSql.length() - 1);

		return partialSql;
	}

}
