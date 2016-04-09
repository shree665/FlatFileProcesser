package subedi.flatfile.persistence.enumerated;

import java.sql.Types;

/**
 * 
 * @author vivek.subedi
 *
 */

public enum DbColumnTypeEnum {

	INTEGER(Types.INTEGER),
	NUMBER(Types.NUMERIC),
	
	CHAR(Types.CHAR),
	LONGVAR(Types.LONGVARCHAR),
	VARCHAR(Types.VARCHAR),
	NCHAR(Types.NCHAR),
	NVARCHAR2(Types.NVARCHAR),
	VARCHAR2(Types.VARCHAR),
	VARBIN(Types.VARBINARY),
	
	CLOB(Types.CLOB),
	BLOB(Types.BLOB),
	
	DATE(Types.DATE),
	TIME(Types.TIME),
	TIMESTMP(Types.TIMESTAMP),
	TIMESTAMP(Types.TIMESTAMP),
	TIMESTAMP6(Types.TIMESTAMP),
	
	XML(Types.SQLXML);
	
	private Integer sqlType;
	
	DbColumnTypeEnum(Integer sqlType) {
		this.sqlType = sqlType;
	}
	
	public Integer getSqlType() {
		return sqlType;
	}
	
	public void setSqlType(Integer sqlType) {
		this.sqlType = sqlType;
	}
}
