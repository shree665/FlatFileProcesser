package subedi.flatfile.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author vivek.subedi
 *
 */
@Entity
@Table(name="CCM_REF") 
public class RefData implements Serializable{
	
	private static final long serialVersionUID = 9063308343509167781L;
	
	@EmbeddedId
	private RefDataId id;
	
	@Column(name="FIELD_NAME1")
	private String db2TableName;
	
	@Column(name="FIELD_NAME2")
	private String db2ColumnName;	

	public RefData() {
		super();
	}
	
	public RefData(RefDataId id, String db2TableName, String db2ColumnName) {
		super();
		this.id = id;
		this.db2TableName = db2TableName;
		this.db2ColumnName = db2ColumnName;
	}

	public RefDataId getId() {
		return id;
	}

	public void setId(RefDataId id) {
		this.id = id;
	}


	public String getDb2TableName() {
		return db2TableName;
	}

	
	public void setDb2TableName(String db2TableName) {
		this.db2TableName = db2TableName;
	}


	public String getDb2ColumnName() {
		return db2ColumnName;
	}


	public void setDb2ColumnName(String db2ColumnName) {
		this.db2ColumnName = db2ColumnName;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((db2ColumnName == null) ? 0 : db2ColumnName.hashCode());
		result = prime * result
				+ ((db2TableName == null) ? 0 : db2TableName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		RefData other = (RefData) obj;
		if (db2ColumnName == null) {
			if (other.db2ColumnName != null)
				return false;
		} else if (!db2ColumnName.equals(other.db2ColumnName))
			return false;
		if (db2TableName == null) {
			if (other.db2TableName != null)
				return false;
		} else if (!db2TableName.equals(other.db2TableName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

