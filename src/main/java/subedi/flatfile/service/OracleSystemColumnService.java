package subedi.flatfile.service;


import java.util.List;

import subedi.flatfile.persistence.OracleSystemColumn;
/**
 * 
 * @author vivek.subedi
 *
 */
public interface OracleSystemColumnService {
	/**
	 * Method to retrieve all the table properties from catalog for a table
	 * 
	 * @param tableSchema - schema
	 * @param tableName - table name
	 * @return list of OracleSystemColumn objects
	 */
	List<OracleSystemColumn> getOracleColumnsForTable(String tableSchema, String tableName);
	
}
