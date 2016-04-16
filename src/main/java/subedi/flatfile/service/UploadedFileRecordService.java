/**
 * 
 */
package subedi.flatfile.service;

import java.util.List;

import subedi.flatfile.persistence.UploadedFileRecord;
import subedi.flatfile.persistence.enumerated.FileStatusEnum;
import subedi.flatfile.persistence.enumerated.FileTypeEnum;

/**
 * @author vivek.subedi
 *
 */
public interface UploadedFileRecordService {
	
	/**
	 * Gets the list of uploaded files that meets the criteria.
	 *
	 * @param fileStat - the status of file at the FILE_UPLOAD table i.e. UPLOADED, PROCESSED, FAILED etc
	 * @param fileType - the type of file i.e. ICM or NICE
	 * @param fileCat -  the category of file i.e TELEPHONY_ANALYTICS
	 * @return the list of uploaded files
	 */
	public abstract List<UploadedFileRecord> getUploadedFiles(FileStatusEnum fileStat, FileTypeEnum fileType, String fileCat);

	/**
	 * Update the status of a file using it's ID.
	 *
	 * @param uploadedFileId - the uploaded file id
	 * @param status the status of a file
	 */
	public abstract void updateUploadedFileRecord(Long uploadedFileId, FileStatusEnum status);
	
	/**
	 * @param uploadedFileRecordStatus - status of the file
	 * @return booelan
	 */
	public abstract boolean moreFilesLeftToProcess(FileStatusEnum uploadedFileRecordStatus);
	
}
