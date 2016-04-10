package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.OracleSystemColumn;

public interface OracleSystemColumnDao {
	
	List<OracleSystemColumn> getOracleColumnsForTable(String tableSchema, String tableName);
	
}
