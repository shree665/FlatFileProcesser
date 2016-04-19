/**
 *
 */
package subedi.flatfile.listener;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import subedi.flatfile.persistence.enumerated.FileTypeEnum;
import subedi.flatfile.util.FlatFileUtil;

/**
 * @author vivek.subedi
 *
 */
public class FlatFileMoveListener implements StepExecutionListener {
	
	private static Logger logger = LoggerFactory.getLogger(FlatFileMoveListener.class);
	
	// Injected
	private FileTypeEnum fileType;
	
	/**
	 * Moves/compresses files to archive location after a they have
	 * been processed
	 */
	@Override
	public ExitStatus afterStep(final StepExecution stepExecution) {
		
		//need to get folder path from database so that easy to update if necessary
		String archiveFolderPath = FlatFileUtil.ARCHIVE_PATH;
		
		if (ExitStatus.COMPLETED.equals(stepExecution.getExitStatus())) {
			if (archiveFolderPath == null) {
				throw new IllegalStateException("Unable to find archive directory");
			}
			
			//getting file and doc id from execution context
			File archiveFolder = new File(archiveFolderPath);
			
			//getting working file path by file id
			String workingFilePath = stepExecution.getExecutionContext().getString(FlatFileUtil.FILE_NAME_KEY);
			if (workingFilePath == null) {
				throw new IllegalStateException("Working file location is null");
			}
			
			//moving file to archive
			File workingFile = new File(workingFilePath);
			String archiveFilePath = FlatFileUtil.archive(workingFile, archiveFolder, fileType.toString(), true);
			logger.info("File has been archived to [{}]", archiveFilePath);
		}
		
		return ExitStatus.COMPLETED;
	}
	
	/**
	 * Moves a file from /staging/ to /working/ and then updates the path
	 * in the StepExecution context and FileTxn
	 */
	@Override
	public void beforeStep(final StepExecution stepExecution) {
		
		// Get relevant fileTxnId
		String stagingFolderPath = stepExecution.getExecutionContext().getString(FlatFileUtil.FILE_NAME_KEY);;
		File stagingFile = new File(stagingFolderPath);
		
		// Creating destination file path
		String workingFolderPath = FlatFileUtil.WORKING_PATH;
		
		if (workingFolderPath == null) {
			throw new IllegalStateException("Unable to find working directory");
		}
		
		File workingFolder = new File(workingFolderPath);
		FlatFileUtil.checkIntegrityOfDirectory(workingFolder);
		File workingFile = new File(workingFolder, stagingFile.getName());
		
		// Move the file to working directory
		/*try {
			Files.move(stagingFile.toPath(), workingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		//moving file to the working directory by renaming the file
		boolean moveCompleted = stagingFile.renameTo(workingFile);
		
		if (!moveCompleted) {
			if (workingFile.isFile()) {
				logger.info("File [" + workingFile.getPath() + "] already exists in working directory, deleting file from staging");
				stagingFile.delete();
			} else {
				throw new IllegalStateException("Unable to move file from [" + stagingFolderPath + "] to [" + workingFolderPath + "]");
			}
		}
		
		// Update file transaction & step execution context
		logger.info("File [" + stagingFile.getName() + "] now located (moved) to [" + workingFile.getPath() + "]");
		
		//updating file txn and putting back to the execution context
		stepExecution.getExecutionContext().put(FlatFileUtil.FILE_NAME_KEY, FlatFileUtil.FILE_PREFIX + workingFile.getPath());
	}
	
	public FileTypeEnum getFileType() {
		return fileType;
	}
	
	public void setFileType(FileTypeEnum fileType) {
		this.fileType = fileType;
	}
	
}
