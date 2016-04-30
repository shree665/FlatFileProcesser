package subedi.flatfile.tasklet;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import subedi.flatfile.util.FlatFileUtil;

/**
 * Tasklet to move failed files from /working directory to /staging directory to process again if the
 * file was not process on /working directory for some reason
 * 
 * @author vivek.subedi
 *
 */
public class MoveFailedFilesTasklet implements Tasklet {
	
	private final Logger logger = LoggerFactory.getLogger(MoveFailedFilesTasklet.class);

	//injected using context file
	private boolean processFailedFiles;
	private String databaseCode;
	private String stagingFolderPath;
	private String workingFolderPath;

	@SuppressWarnings("unchecked")
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		stagingFolderPath = FlatFileUtil.STAGING_PATH;
		workingFolderPath = FlatFileUtil.WORKING_PATH;
		if (workingFolderPath == null) {
			throw new IllegalStateException("Unable to find archive directory");
		}
		if (stagingFolderPath == null) {
			throw new IllegalStateException("Unable to find staging directory");
		}

		File workingDirectory = new File(workingFolderPath);
		File stagingDirectory = new File(stagingFolderPath);
		Collection<File> failedFiles = FileUtils.listFiles(workingDirectory, null, false);
		Collection<File> stagingFiles = FileUtils.listFiles(stagingDirectory, null, false);

		Collection<File> toBePrecessedFailedFiles = FlatFileUtil.matchPatterFilePrefix(failedFiles, databaseCode);

		if (!failedFiles.isEmpty()) {
			for(File failedFile : toBePrecessedFailedFiles) {

				contribution.incrementReadCount();
				logger.info("Found failed file ["+failedFile.getName()+"] from a previous job run.");

				if(processFailedFiles) {
					checkIntegrityOfFile(failedFile);
					File destinationFile = new File(stagingDirectory, failedFile.getName());
					Boolean moveCompleted = failedFile.renameTo(destinationFile);

					if(!moveCompleted) {
						boolean alreadyPresent = checkStagingDirectory(failedFile, stagingFiles);
						if (alreadyPresent) {
							failedFile.delete();
							logger.info("The file ["+failedFile.getName()+"] is already in staging directory. Deleting file from working directory");
						} else {
							throw new IllegalStateException("Unable to move failed file ["+failedFile.getName()+"] to staging directory");
						}
					}
					logger.info("Moved failed file ["+failedFile.getName()+"] to staging directory to be processed");
					contribution.incrementWriteCount(1);
				} else {
					logger.info("Ignoring the file due to job parameter");
					contribution.incrementWriteSkipCount();
				}
			}
		} else {
			logger.info("There are not any failed files left in working directory");
		}

		return null;
	}


	/**
	 * @param failedFile - file from the working directory
	 * @param stagingFiles - collection of files that are in staging directory
	 * @return true, if successful
	 */
	private boolean checkStagingDirectory(File failedFile, Collection<File> stagingFiles) {
		boolean found = false;
		for (File file : stagingFiles) {
			if (failedFile.getName().equalsIgnoreCase(file.getName())) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * Checks integrity of file. if the checking is failed, it throw exception
	 *
	 * @param theFile - the the file
	 * @return void
	 */
	private void checkIntegrityOfFile(File theFile) {
		if(!theFile.exists()) {
			throw new IllegalStateException("The file denoted by server path ["+theFile.getPath()+"] does not exist");
		} else if (!theFile.isFile()) {
			throw new IllegalStateException("The file denoted by server path ["+theFile.getPath()+"] is not a file");
		} else if (!theFile.canRead()) {
			throw new IllegalStateException("Can not read file ["+theFile.getPath()+"]");
		}
	}

	public void setProcessFailedFiles(boolean processFailedFiles) {
		this.processFailedFiles = processFailedFiles;
	}

	public void setDatabaseCode(String databaseCode) {
		this.databaseCode = databaseCode;
	}

}
