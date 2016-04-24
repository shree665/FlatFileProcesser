package subedi.flatfile.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This is POJO class to hold the uploaded file records. This object has one to one relationship
 * with UploadFileRecordWork class. 
 * 
 * @author vivek.subedi
 *
 */
@Entity
@Table(name="FILE_UPLOAD")
public class UploadedFileRecord implements Serializable{
	
	private static final long serialVersionUID = -16554412696230218L;
	
	@Id
	@Column(name="FILE_UPLOAD_ID")
	private Long uploadedFileId;
	
	@Column(name="FILE_NAME")
	private String uploadedFileName;
	
	@Column(name="FILE_EXT")
	private String uploadedFileExt;

	@Column(name="ABSOLUTE_PATH")
	private String serverPath;
	
	@Column(name="FILE_UPLOAD_SIZE")
	private Long fileUploadSize;
	
	@Column(name="FILE_CAT")
	private String fileCat;
	
	@Column(name="UPLOADER_USER_ROLE_ID")
	private Long uploaderUserRoleId;
	
	@Column(name="QUARANTINED_FLG")
	private String quarantinedFlg;
	
	@Column(name="COMPRESSED_FLG")
	private String compressedFlg;
	
	@OneToOne
	@JoinColumn(name="FILE_UPLOAD_ID")
	private UploadedFileRecordWork uploadedFileRecordWork;

	
	public UploadedFileRecord() {
		super();
	}

	public UploadedFileRecord(Long uploadedFileId, String uploadedFileName,
			String uploadedFileExt, String serverPath, Long fileUploadSize,
			String fileCat, Long uploaderUserRoleId, String quarantinedFlg,
			String compressedFlg, UploadedFileRecordWork uploadedFileRecordWork) {
		super();
		this.uploadedFileId = uploadedFileId;
		this.uploadedFileName = uploadedFileName;
		this.uploadedFileExt = uploadedFileExt;
		this.serverPath = serverPath;
		this.fileUploadSize = fileUploadSize;
		this.fileCat = fileCat;
		this.uploaderUserRoleId = uploaderUserRoleId;
		this.quarantinedFlg = quarantinedFlg;
		this.compressedFlg = compressedFlg;
		this.uploadedFileRecordWork = uploadedFileRecordWork;
	}

	public Long getUploadedFileId() {
		return uploadedFileId;
	}

	public void setUploadedFileId(Long uploadedFileId) {
		this.uploadedFileId = uploadedFileId;
	}

	public String getUploadedFileName() {
		return uploadedFileName;
	}

	public void setUploadedFileName(String uploadedFileName) {
		this.uploadedFileName = uploadedFileName;
	}

	public String getUploadedFileExt() {
		return uploadedFileExt;
	}

	public void setUploadedFileExt(String uploadedFileExt) {
		this.uploadedFileExt = uploadedFileExt;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public Long getFileUploadSize() {
		return fileUploadSize;
	}

	public void setFileUploadSize(Long fileUploadSize) {
		this.fileUploadSize = fileUploadSize;
	}

	public String getFileCat() {
		return fileCat;
	}

	public void setFileCat(String fileCat) {
		this.fileCat = fileCat;
	}

	public Long getUploaderUserRoleId() {
		return uploaderUserRoleId;
	}

	public void setUploaderUserRoleId(Long uploaderUserRoleId) {
		this.uploaderUserRoleId = uploaderUserRoleId;
	}

	public String getQuarantinedFlg() {
		return quarantinedFlg;
	}

	public void setQuarantinedFlg(String quarantinedFlg) {
		this.quarantinedFlg = quarantinedFlg;
	}

	public String getCompressedFlg() {
		return compressedFlg;
	}

	public void setCompressedFlg(String compressedFlg) {
		this.compressedFlg = compressedFlg;
	}

	public UploadedFileRecordWork getUploadedFileRecordWork() {
		return uploadedFileRecordWork;
	}

	public void setUploadedFileRecordWork(
			UploadedFileRecordWork uploadedFileRecordWork) {
		this.uploadedFileRecordWork = uploadedFileRecordWork;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compressedFlg == null) ? 0 : compressedFlg.hashCode());
		result = prime * result + ((fileCat == null) ? 0 : fileCat.hashCode());
		result = prime * result
				+ ((fileUploadSize == null) ? 0 : fileUploadSize.hashCode());
		result = prime * result
				+ ((quarantinedFlg == null) ? 0 : quarantinedFlg.hashCode());
		result = prime * result
				+ ((serverPath == null) ? 0 : serverPath.hashCode());
		result = prime * result
				+ ((uploadedFileExt == null) ? 0 : uploadedFileExt.hashCode());
		result = prime * result
				+ ((uploadedFileId == null) ? 0 : uploadedFileId.hashCode());
		result = prime
				* result
				+ ((uploadedFileName == null) ? 0 : uploadedFileName.hashCode());
		result = prime
				* result
				+ ((uploadedFileRecordWork == null) ? 0
						: uploadedFileRecordWork.hashCode());
		result = prime
				* result
				+ ((uploaderUserRoleId == null) ? 0 : uploaderUserRoleId
						.hashCode());
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
		UploadedFileRecord other = (UploadedFileRecord) obj;
		if (compressedFlg == null) {
			if (other.compressedFlg != null)
				return false;
		} else if (!compressedFlg.equals(other.compressedFlg))
			return false;
		if (fileCat == null) {
			if (other.fileCat != null)
				return false;
		} else if (!fileCat.equals(other.fileCat))
			return false;
		if (fileUploadSize == null) {
			if (other.fileUploadSize != null)
				return false;
		} else if (!fileUploadSize.equals(other.fileUploadSize))
			return false;
		if (quarantinedFlg == null) {
			if (other.quarantinedFlg != null)
				return false;
		} else if (!quarantinedFlg.equals(other.quarantinedFlg))
			return false;
		if (serverPath == null) {
			if (other.serverPath != null)
				return false;
		} else if (!serverPath.equals(other.serverPath))
			return false;
		if (uploadedFileExt == null) {
			if (other.uploadedFileExt != null)
				return false;
		} else if (!uploadedFileExt.equals(other.uploadedFileExt))
			return false;
		if (uploadedFileId == null) {
			if (other.uploadedFileId != null)
				return false;
		} else if (!uploadedFileId.equals(other.uploadedFileId))
			return false;
		if (uploadedFileName == null) {
			if (other.uploadedFileName != null)
				return false;
		} else if (!uploadedFileName.equals(other.uploadedFileName))
			return false;
		if (uploadedFileRecordWork == null) {
			if (other.uploadedFileRecordWork != null)
				return false;
		} else if (!uploadedFileRecordWork.equals(other.uploadedFileRecordWork))
			return false;
		if (uploaderUserRoleId == null) {
			if (other.uploaderUserRoleId != null)
				return false;
		} else if (!uploaderUserRoleId.equals(other.uploaderUserRoleId))
			return false;
		return true;
	}

}

