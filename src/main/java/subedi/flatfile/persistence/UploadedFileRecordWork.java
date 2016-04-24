package subedi.flatfile.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import subedi.flatfile.persistence.enumerated.FileStatusEnum;
import subedi.flatfile.persistence.enumerated.FileTypeEnum;

/**
 * Child class of main UploadfileRecord table
 * 
 * @author vivek.subedi
 *
 */
@Entity
@Table(name="FILE_UPLOAD_WORK")
public class UploadedFileRecordWork implements Serializable {

	private static final long serialVersionUID = -1396570709579286525L;
	
	@Id
	@Column(name="FILE_UPLOAD_ID")
	private Long fileUploadId;
	
	@Enumerated(EnumType.STRING)
	@Column(name="FILE_WRK_TYPE")
	private FileTypeEnum fileWorkType;
	
	@Enumerated(EnumType.STRING)
	@Column(name="PROC_STATUS")
	private FileStatusEnum processingStatus;
	
	 @Temporal(TemporalType.TIMESTAMP)
	 @Column(name="PROC_DTM")
	 private Date processedTime;
	
	 public UploadedFileRecordWork() {
		super();
	}

	public UploadedFileRecordWork(Long fileUploadId, FileTypeEnum fileWorkType,
			FileStatusEnum processingStatus, Date processedTime) {
		super();
		this.fileUploadId = fileUploadId;
		this.fileWorkType = fileWorkType;
		this.processingStatus = processingStatus;
		this.processedTime = processedTime;
	}

	public Long getFileUploadId() {
		return fileUploadId;
	}

	public void setFileUploadId(Long fileUploadId) {
		this.fileUploadId = fileUploadId;
	}

	public FileTypeEnum getFileWorkType() {
		return fileWorkType;
	}

	public void setFileWorkType(FileTypeEnum fileWorkType) {
		this.fileWorkType = fileWorkType;
	}

	public FileStatusEnum getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(FileStatusEnum processingStatus) {
		this.processingStatus = processingStatus;
	}

	public Date getProcessedTime() {
		return processedTime;
	}

	public void setProcessedTime(Date processedTime) {
		this.processedTime = processedTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileUploadId == null) ? 0 : fileUploadId.hashCode());
		result = prime * result
				+ ((fileWorkType == null) ? 0 : fileWorkType.hashCode());
		result = prime * result
				+ ((processedTime == null) ? 0 : processedTime.hashCode());
		result = prime
				* result
				+ ((processingStatus == null) ? 0 : processingStatus.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadedFileRecordWork other = (UploadedFileRecordWork) obj;
		if (fileUploadId == null) {
			if (other.fileUploadId != null)
				return false;
		} else if (!fileUploadId.equals(other.fileUploadId))
			return false;
		if (fileWorkType != other.fileWorkType)
			return false;
		if (processedTime == null) {
			if (other.processedTime != null)
				return false;
		} else if (!processedTime.equals(other.processedTime))
			return false;
		if (processingStatus != other.processingStatus)
			return false;
		return true;
	}
}
