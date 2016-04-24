package subedi.flatfile.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite primary key consisting of table name, schema, and column name
 * 
 * @author vivek.subedi
 */
@Embeddable
public class OracleSystemColumnId implements Serializable {
	
	private static final long serialVersionUID = -5455177490216040084L;
	
	@Column(name = "COLUMN_NAME")
	private String columnName;
	
	@Column(name = "TABLE_NAME")
	private String tableName;
	
	@Column(name = "OWNER")
	private String tableSchema;
	
	public OracleSystemColumnId() {
		super();
	}
	
	public OracleSystemColumnId(String columnName, String tableName, String tableSchema) {
		super();
		this.columnName = columnName;
		this.tableName = tableName;
		this.tableSchema = tableSchema;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableSchema() {
		return tableSchema;
	}
	
	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((tableSchema == null) ? 0 : tableSchema.hashCode());
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
		OracleSystemColumnId other = (OracleSystemColumnId) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (tableSchema == null) {
			if (other.tableSchema != null)
				return false;
		} else if (!tableSchema.equals(other.tableSchema))
			return false;
		return true;
	}
}
