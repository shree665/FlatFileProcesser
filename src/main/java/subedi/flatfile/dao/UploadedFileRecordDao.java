/**
 * 
 */
package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.UploadedFileRecord;
import subedi.flatfile.persistence.enumerated.FileStatusEnum;
import subedi.flatfile.persistence.enumerated.FileTypeEnum;

/**
 * Interface to get all the files that are being processed during the job run from the specific table. 
 * It also defines methods to update the files so that we do not have to process again.
 * 
 * @author vivek.subedi
 *
 */
public interface UploadedFileRecordDao {
	/**
	 * Method to get all the files that need to process on the specific job from a table
	 * 
	 * @param fileStat - status of the file i.e. new
	 * @param fileType - type of the file. This depends on job. i.e. icm
	 * @param fileCat - category of the file. This also depends on the job. This is the upper level file type than icm 
	 * @return List of the UploadFileRecords object when the criteria matches
	 */
	List<UploadedFileRecord> getUploadedFiles(FileStatusEnum fileStat, FileTypeEnum fileType, String fileCat);
	
	/**
	 * Method to update the status of the file in the database once the file has been copied to server
	 * 
	 * @param fileRecord - file upload object to update the status
	 */
	void updateUploadedFileRecord(UploadedFileRecord fileRecord);
	
	/**
	 * Method to retrieve a specific file using file upload id from the cache
	 * 
	 * @param fileId - file id
	 * @return a single instance of a file
	 */
	UploadedFileRecord getUploadedFileFromTable(Long fileId);
}
