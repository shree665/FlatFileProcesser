package subedi.flatfile.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author vivek.subedi
 *
 */
@Embeddable
public class RefDataId implements Serializable {
	
	private static final long serialVersionUID = -5880110997244158719L;

	@Column(name="REF_GROUP_NAME")
	private String ccmMappingName;
	
	@Column(name="REF_GROUP_KEY1")
	private String tableNameFromFile;
	
	@Column(name="REF_GROUP_KEY2")
	private String columnNameFromFile;
	
	@Column(name="REF_GROUP_KEY3")
	private String columnIdOfFile;
	

	public RefDataId() {
		super();
	}

	public RefDataId(String ccmMappingName, String tableNameFromFile,
			String columnNameFromFile, String columnIdOfFile) {
		super();
		this.ccmMappingName = ccmMappingName;
		this.tableNameFromFile = tableNameFromFile;
		this.columnNameFromFile = columnNameFromFile;
		this.columnIdOfFile = columnIdOfFile;
	}



	public String getCcmMappingName() {
		return ccmMappingName;
	}

	public void setCcmMappingName(String ccmMappingName) {
		this.ccmMappingName = ccmMappingName;
	}

	public String getTableNameFromFile() {
		return tableNameFromFile;
	}

	public void setTableNameFromFile(String tableNameFromFile) {
		this.tableNameFromFile = tableNameFromFile;
	}

	public String getColumnNameFromFile() {
		return columnNameFromFile;
	}

	public void setColumnNameFromFile(String columnNameFromFile) {
		this.columnNameFromFile = columnNameFromFile;
	}

	public String getColumnIdOfFile() {
		return columnIdOfFile;
	}

	public void setColumnIdOfFile(String columnIdOfFile) {
		this.columnIdOfFile = columnIdOfFile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnIdOfFile == null) ? 0 : columnIdOfFile.hashCode());
		result = prime * result + ((columnNameFromFile == null) ? 0 : columnNameFromFile.hashCode());
		result = prime * result + ((ccmMappingName == null) ? 0 : ccmMappingName.hashCode());
		result = prime * result + ((tableNameFromFile == null) ? 0 : tableNameFromFile.hashCode());
		
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
		RefDataId other = (RefDataId) obj;
		if (columnIdOfFile == null) {
			if (other.columnIdOfFile != null)
				return false;
		} else if (!columnIdOfFile.equals(other.columnIdOfFile))
			return false;
		if (columnNameFromFile == null) {
			if (other.columnNameFromFile != null)
				return false;
		} else if (!columnNameFromFile.equals(other.columnNameFromFile))
			return false;
		if (ccmMappingName == null) {
			if (other.ccmMappingName != null)
				return false;
		} else if (!ccmMappingName.equals(other.ccmMappingName))
			return false;
		if (tableNameFromFile == null) {
			if (other.tableNameFromFile != null)
				return false;
		} else if (!tableNameFromFile.equals(other.tableNameFromFile))
			return false;
		return true;
	}
}
