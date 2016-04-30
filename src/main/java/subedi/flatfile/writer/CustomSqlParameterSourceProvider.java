/**
 * 
 */
package subedi.flatfile.writer;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import subedi.flatfile.persistence.DataContainer;
import subedi.flatfile.persistence.JobControl;
import subedi.flatfile.persistence.OracleSystemColumn;
import subedi.flatfile.persistence.RefData;
import subedi.flatfile.persistence.enumerated.OracleColumnTypeEnum;
import subedi.flatfile.service.OracleSystemColumnService;
import subedi.flatfile.service.RefDataService;
import subedi.flatfile.util.FlatFileUtil;
import subedi.flatfile.util.OracleCaster;

/**
 * provides values of an item to the SQL at runtime.
 *
 * @author vivek.subedi
 */
public class CustomSqlParameterSourceProvider implements ItemSqlParameterSourceProvider<DataContainer> {
	
	@Value("${batch.oracle.rds.schema}")
	private String schema;
	
	@Autowired
	private OracleSystemColumnService oracleSystemColumnService;

	@Autowired
	private RefDataService refDataService;
	
	private Map<Integer, Map<String, Integer>> jdbcDataTypes;
	private Map<Integer, Map<String, Integer>> columnLengths;
	private String mappingName;

	/**
	 * Provide parameter values in an {@link SqlParameterSource} based on values from
	 * the provided item.
	 *
	 * @param CCMContainer as an item to use for parameter values
	 * @return the sql parameter source
	 */
	@Override
	public SqlParameterSource createSqlParameterSource(DataContainer item) {
		
		if (jdbcDataTypes == null || columnLengths == null) {
			jdbcDataTypes = new HashMap<Integer, Map<String, Integer>>();
			columnLengths = new HashMap<Integer, Map<String, Integer>>();
		}
		
		if (!jdbcDataTypes.containsKey(item.getJobControl().getJobControlId()) || !columnLengths.containsKey(item.getJobControl().getJobControlId())) {
			setMetaData(item.getJobControl());
		}
		
		Map<String, SqlParameterValue> sqlParams = getParameters(item);
		MapSqlParameterSource mapSqlParamSource = new MapSqlParameterSource(sqlParams);
		
		return mapSqlParamSource;
	}

	/**
	 * Sets the meta data (i.e. Data type and the column length of DB2 columns) for an item using jobControl
	 *
	 * @param jobControl of a DB2 table
	 * @return void
	 */
	private void setMetaData(JobControl jobControl) {
		
		//retrieving db2TableName using jobControl and adding prefix and suffix into it
		String db2TableName = FlatFileUtil.ORACLE_ICM_TABLE_PREFIX + jobControl.getTableName();
		
		//getting column meta data so that we can have column type and its length
		List<OracleSystemColumn> columnMetaData = oracleSystemColumnService.getOracleColumnsForTable(schema, db2TableName);
		
		Map<String, Integer> subType = new HashMap<String, Integer>();
		Map<String, Integer> subLength = new HashMap<String, Integer>();
		
		//adding column type and it's length to the map
		for (OracleSystemColumn oracleSystemColumn : columnMetaData) {
			//holding special case for timestamp(6)
			if (oracleSystemColumn.getType().equalsIgnoreCase("TIMESTAMP(6)")) {
				subType.put(oracleSystemColumn.getId().getColumnName().trim().toUpperCase(), OracleColumnTypeEnum.valueOf("TIMESTAMP").getSqlType());
			} else {
				subType.put(oracleSystemColumn.getId().getColumnName().trim().toUpperCase(), OracleColumnTypeEnum.valueOf(oracleSystemColumn.getType()).getSqlType());
			}
			
			if (oracleSystemColumn.getType().equalsIgnoreCase("NUMBER")) {
				subLength.put(oracleSystemColumn.getId().getColumnName().trim().toUpperCase(), oracleSystemColumn.getNumberPrecision());
			} else if (oracleSystemColumn.getType().equalsIgnoreCase("VARCHAR") || oracleSystemColumn.getType().equalsIgnoreCase("VARCHAR2") 
					|| oracleSystemColumn.getType().equalsIgnoreCase("CHAR")) {
				subLength.put(oracleSystemColumn.getId().getColumnName().trim().toUpperCase(), oracleSystemColumn.getCharLength());
			} else {
				subLength.put(oracleSystemColumn.getId().getColumnName().trim().toUpperCase(), oracleSystemColumn.getCharLength());
			}
		}
		
		jdbcDataTypes.put(jobControl.getJobControlId(), subType);
		columnLengths.put(jobControl.getJobControlId(), subLength);
	}

	
	/**
	 * Returns the map with key of fileColumnName and the casted value. Casted value is the final value that have
	 * been casted using related DB2 column dataType and length
	 *
	 * @param item - the CCMContainer
	 * @return Map of column name and it's value
	 */
	private Map<String, SqlParameterValue> getParameters(DataContainer item) {
		
		Map<String, SqlParameterValue> paramaterMap = new HashMap<String, SqlParameterValue>();
		
		Map<String, Integer> db2ColumnType = jdbcDataTypes.get(item.getJobControl().getJobControlId());
		Map<String, Integer> db2ColumnLength = columnLengths.get(item.getJobControl().getJobControlId());
		
		//getting the refData for mapping
		List<RefData> myRefDatas = refDataService.getRefDataForDatabase(mappingName);
		Map<String, String> columnMap = new HashMap<String, String>();
		for (RefData refData : myRefDatas) {
			if (refData.getDb2TableName().equalsIgnoreCase(FlatFileUtil.ORACLE_ICM_TABLE_PREFIX+item.getJobControl().getTableName())) {
				columnMap.put(refData.getId().getColumnNameFromFile(), refData.getDb2ColumnName());
			}
		}
		
		//Mapping each entry to the db2 table name and casting each value of file to match the db2 type
		for (Entry<String, String> entry : item.getCcmData().entrySet()) {
			Integer columnTypeCode = db2ColumnType.get(columnMap.get(entry.getKey()));
			if (db2ColumnType != null) {
				Object ccmValue =OracleCaster.castCCMValue(entry.getValue(), columnTypeCode, db2ColumnLength.get(columnMap.get(entry.getKey())), columnMap.get(entry.getKey()));
				SqlParameterValue sqlParameterValue = new SqlParameterValue(columnTypeCode.intValue(), ccmValue);
				paramaterMap.put(entry.getKey().toLowerCase(), sqlParameterValue);
			}
		}
		return paramaterMap;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setDb2SystemColumnService( OracleSystemColumnService oracleSystemColumnService) {
		this.oracleSystemColumnService = oracleSystemColumnService;
	}
	
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

}
