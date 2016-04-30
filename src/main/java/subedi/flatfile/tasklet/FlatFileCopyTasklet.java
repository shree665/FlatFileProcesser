package subedi.flatfile.tasklet;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import subedi.flatfile.persistence.UploadedFileRecord;
import subedi.flatfile.persistence.enumerated.FileStatusEnum;
import subedi.flatfile.persistence.enumerated.FileTypeEnum;
import subedi.flatfile.service.UploadedFileRecordService;
import subedi.flatfile.util.FlatFileUtil;


/**
 * Tasklet to move file from /staging directory to /working directory to read and process the file.
 * 
 * @author vivek.subedi
 *
 */
public class FlatFileCopyTasklet implements Tasklet {
	
	private final Logger logger = LoggerFactory.getLogger(FlatFileCopyTasklet.class);
	
	@Autowired
	private UploadedFileRecordService uploadedFileRecordService;
	
	//Injected
	private FileStatusEnum uploadedFileRecordStatus;
	private FileTypeEnum uploadedFileRecordType;
	private String uploadedFileRecordCategory;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext arg1) throws Exception {
		String stagingFolderPath = FlatFileUtil.STAGING_PATH;
		if (stagingFolderPath == null) {
			throw new IllegalStateException("Unable to find staging directory code = ["+FlatFileUtil.STAGING_PATH+"], please check configuration table");
		}

		File stagingDirectory = new File(stagingFolderPath);
		List<UploadedFileRecord> newUploadedFiles = uploadedFileRecordService
				.getUploadedFiles(uploadedFileRecordStatus, uploadedFileRecordType, uploadedFileRecordCategory);

		if (!newUploadedFiles.isEmpty()) {
			for(UploadedFileRecord fileRecord : newUploadedFiles) {

				contribution.incrementReadCount();
				logger.info("Found new uploaded ["+fileRecord.getUploadedFileRecordWork().getFileWorkType()+"-"+fileRecord.getFileCat()+"]"
						+ " file: ["+fileRecord.getUploadedFileName()+"]\nID: "+fileRecord.getUploadedFileId());

				try {

					File uploadedFile = new File(fileRecord.getServerPath());
					checkIntegrityOfFile(uploadedFile);
					File destinationFile = null;

					//checks for the compressed flag and adds .gz for the compressed files
					if (FlatFileUtil.YES_GZIP_FLG.equalsIgnoreCase(fileRecord.getCompressedFlg())) {
						destinationFile = new File(stagingDirectory, fileRecord.getUploadedFileName()+FlatFileUtil.GZIP_FILE_END_SUFFIX);
					}
					else if (FlatFileUtil.NO_GZIP_FLG.equalsIgnoreCase(fileRecord.getCompressedFlg())) {
						destinationFile = new File(stagingDirectory, fileRecord.getUploadedFileName());
					} else {
						logger.warn("Compression flag ["+fileRecord.getCompressedFlg()+"] is not recognized for ["+fileRecord.getUploadedFileName()+"] having id ["+fileRecord.getUploadedFileId()+"]");
						logger.info("Assuming file is not compressed.");
						destinationFile = new File(stagingDirectory, fileRecord.getUploadedFileName());
					}

					FileUtils.copyFile(uploadedFile, destinationFile, true);
					uploadedFileRecordService.updateUploadedFileRecord(fileRecord.getUploadedFileId(), FileStatusEnum.MOVED);
					logger.info("Coppied ["+fileRecord.getUploadedFileName()+"] of uploaded file ID ["+fileRecord.getUploadedFileId()+"] to staging directory");
					contribution.incrementWriteCount(1);

				} catch (Exception e) {
					logger.error("Unable to copy new uploaded file ["+fileRecord.getUploadedFileName()+"] having uploaded file ID ["+fileRecord.getUploadedFileId()+"] to staging directory", e);
					contribution.incrementWriteSkipCount();
				}
			}
		} else {
			logger.info("There are not any files to process in FILE_UPLOAD and FILE_UPLOAD_WRK tables that are in \nStatus: ["+uploadedFileRecordStatus+"]"
					+ "\nFile Type: ["+uploadedFileRecordType+"]\nFile Catagory: ["+uploadedFileRecordCategory+"]");
		}

		return null;
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
			throw new IllegalStateException("Can not read file ["+theFile.getPath()+"], check permissions");
		}
	}
	
	public void setUploadedFileRecordStatus(FileStatusEnum uploadedFileRecordStatus) {
		this.uploadedFileRecordStatus = uploadedFileRecordStatus;
	}

	public void setUploadedFileRecordType(FileTypeEnum uploadedFileRecordType) {
		this.uploadedFileRecordType = uploadedFileRecordType;
	}

	public void setUploadedFileRecordCategory(String uploadedFileRecordCategory) {
		this.uploadedFileRecordCategory = uploadedFileRecordCategory;
	}
}
