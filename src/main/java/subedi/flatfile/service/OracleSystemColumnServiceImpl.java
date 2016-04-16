package subedi.flatfile.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import subedi.flatfile.dao.OracleSystemColumnDao;
import subedi.flatfile.persistence.OracleSystemColumn;

/**
 * 
 * @author vivek.subedi
 *
 */

@Service
public class OracleSystemColumnServiceImpl implements OracleSystemColumnService {
	
	@Autowired
	private OracleSystemColumnDao oracleSystemColumnDao;
	
	private HashMap<String, List<OracleSystemColumn>> cachedColumnMetaData;
	
	@Override
	public List<OracleSystemColumn> getOracleColumnsForTable(String tableSchema, String tableName) {
		
		String key = tableSchema.toUpperCase() + "." + tableName.toUpperCase();
		
		if (cachedColumnMetaData == null) {
			cachedColumnMetaData = new HashMap<String, List<OracleSystemColumn>>();
		}
		
		if (cachedColumnMetaData.containsKey(key)) {
			return cachedColumnMetaData.get(key);
		} else {
			List<OracleSystemColumn> db2ColumnMetaData = oracleSystemColumnDao.getOracleColumnsForTable(tableSchema, tableName);
			cachedColumnMetaData.put(key, db2ColumnMetaData);
			return db2ColumnMetaData;
		}
	}
	
}
