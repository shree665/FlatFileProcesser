package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.OracleSystemColumn;
/**
 * Interface to retrieve all the table properties from oracle system table
 * 
 * @author vivek.subedi
 *
 */
public interface OracleSystemColumnDao {
	/**
	 * Method to retrieve table properties for specific table from oracle catalog table
	 * 
	 * @param tableSchema - schema of table
	 * @param tableName - table name
	 * @return - list of OracleSystemColumn object
	 */
	List<OracleSystemColumn> getOracleColumnsForTable(String tableSchema, String tableName);
	
}
