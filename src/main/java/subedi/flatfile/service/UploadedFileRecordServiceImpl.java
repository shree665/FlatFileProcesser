/**
 * 
 */
package subedi.flatfile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import subedi.flatfile.dao.UploadedFileRecordDao;
import subedi.flatfile.persistence.UploadedFileRecord;
import subedi.flatfile.persistence.enumerated.FileStatusEnum;
import subedi.flatfile.persistence.enumerated.FileTypeEnum;

/**
 * @author vivek.subedi
 *
 */

@Service
public class UploadedFileRecordServiceImpl implements UploadedFileRecordService {
	
	@Autowired
	private UploadedFileRecordDao uploadedFileRecordDao;
	
	private HashMap<Long, UploadedFileRecord> cachedFileRecords;
	
	@Override
	public List<UploadedFileRecord> getUploadedFiles(FileStatusEnum fileStat, FileTypeEnum fileType, String fileCat) {
		
		if(cachedFileRecords == null || cachedFileRecords.isEmpty()) {
			cachedFileRecords = new HashMap<Long, UploadedFileRecord>();
			List<UploadedFileRecord> records = uploadedFileRecordDao.getUploadedFiles(fileStat, fileType, fileCat);
			for(UploadedFileRecord record : records) {
				cachedFileRecords.put(record.getUploadedFileId(), record);
			}
		}
		
		List<UploadedFileRecord> recordsToBeReturned = new ArrayList<UploadedFileRecord>();
		for(UploadedFileRecord record : cachedFileRecords.values()) {
			if(fileStat.equals(record.getUploadedFileRecordWork().getProcessingStatus())
				&& fileType.equals(record.getUploadedFileRecordWork().getFileWorkType())
				&& fileCat.equals(record.getFileCat())) {
					recordsToBeReturned.add(record);
			}
		}	
		
		return recordsToBeReturned;
	}

	@Override
	public void updateUploadedFileRecord(Long uploadedFileId, FileStatusEnum status) {
		UploadedFileRecord fileRecord = cachedFileRecords.get(uploadedFileId);
		fileRecord.getUploadedFileRecordWork().setProcessingStatus(status);
		fileRecord.getUploadedFileRecordWork().setProcessedTime(new Date());
		uploadedFileRecordDao.updateUploadedFileRecord(fileRecord);
	}

	
	@Override
	public boolean moreFilesLeftToProcess(FileStatusEnum uploadedFileRecordStatus) {
		for(UploadedFileRecord record : cachedFileRecords.values()) {
			if(uploadedFileRecordStatus.equals(record.getUploadedFileRecordWork().getProcessingStatus())) {
				return true;
			}
		}
		return false;
	}
	
	public void setUploadedFileDao(UploadedFileRecordDao uploadedFileRecordDao) {
		this.uploadedFileRecordDao = uploadedFileRecordDao;
	}

}
