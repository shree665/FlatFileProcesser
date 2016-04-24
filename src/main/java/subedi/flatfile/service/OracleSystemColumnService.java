package subedi.flatfile.service;


import java.util.List;

import subedi.flatfile.persistence.OracleSystemColumn;
/**
 * 
 * @author vivek.subedi
 *
 */
public interface OracleSystemColumnService {
	
	List<OracleSystemColumn> getOracleColumnsForTable(String tableSchema, String tableName);
	
}
