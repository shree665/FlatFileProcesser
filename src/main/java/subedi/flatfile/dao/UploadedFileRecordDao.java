/**
 * 
 */
package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.UploadedFileRecord;
import subedi.flatfile.persistence.enumerated.FileStatusEnum;
import subedi.flatfile.persistence.enumerated.FileTypeEnum;

/**
 * @author vivek.subedi
 *
 */
public interface UploadedFileRecordDao {
	
	List<UploadedFileRecord> getUploadedFiles(FileStatusEnum fileStat, FileTypeEnum fileType, String fileCat);
	
	void updateUploadedFileRecord(UploadedFileRecord fileRecord);
	
	UploadedFileRecord getUploadedFileFromTable(Long fileId);
}
