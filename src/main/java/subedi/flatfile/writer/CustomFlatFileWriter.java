package subedi.flatfile.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import subedi.flatfile.persistence.DataContainer;
import subedi.flatfile.persistence.JobControl;
import subedi.flatfile.persistence.OracleSystemColumn;
import subedi.flatfile.persistence.RefData;
import subedi.flatfile.persistence.enumerated.BehaviorEnum;
import subedi.flatfile.service.JobCtrlService;
import subedi.flatfile.service.OracleSystemColumnService;
import subedi.flatfile.service.RefDataService;
import subedi.flatfile.util.FlatFileUtil;

/**
 * @author vivek.subedi
 *
 */
public class CustomFlatFileWriter extends JdbcBatchItemWriter<DataContainer> implements InitializingBean{

	@Value("${batch.oracle.rds.schema}")
	private String schema;

	@Autowired
	private OracleSystemColumnService oracleSystemColumnService;

	@Autowired
	private RefDataService refDataService;

	@Autowired
	private JobCtrlService jobCtrlService;

	//variables
	private String[] columnNames;
	private String rowCount;
	private String sqlStatement = "";
	private JobControl jobControl;
	private Integer jobControlId;
	private String db2TableName;
	private BehaviorEnum behaviorCode;
	private String mappingName;

	//SQL constant for faster retrieve
	private static final String SCHEMA_TABLE = "<SCHEMA.TABLE>";
	private static final String COLUMN_INSERT_LABELS = "<COLUMN_INSERT_LABELS>";
	private static final String COLUMN_UPDATE_LABELS = "<COLUMN_UPDATE_LABELS>";
	private static final String VALUES = "<VALUES>";
	private static final String WHERE_CLAUSE="<WHERE_CLAUSE>";
	private static final String DOT = ".";
	private static final String UPDATE_VALUE = "<UPDATE_COLUMNS>";
	//private static final String COLUMN_UPDATE_VALUE = "<UPDATE_COLUMNS_VALUE>";
	private static final String MERGE_SQL = "MERGE INTO <SCHEMA.TABLE> TBL_ALIAS_1 USING (SELECT <VALUES> FROM DUAL) TBL_ALIAS_2 "
			+ " ON (<WHERE_CLAUSE>) WHEN MATCHED THEN UPDATE SET <COLUMN_UPDATE_LABELS> "
			+ "WHEN NOT MATCHED THEN INSERT ( <COLUMN_INSERT_LABELS> ) VALUES ( <UPDATE_COLUMNS> )";
	/*private static final String MERGE_SQL = "MERGE INTO <SCHEMA.TABLE> TBL_ALIAS_1 USING (SELECT <VALUES>) AS TBL_ALIAS_2 "
			+ " ( <COLUMN_INSERT_LABELS> ) ON <WHERE_CLAUSE> WHEN MATCHED THEN UPDATE SET ( <COLUMN_UPDATE_LABELS> ) = ( <UPDATE_COLUMNS_VALUE> )"
			+ "WHEN NOT MATCHED THEN INSERT ( <COLUMN_INSERT_LABELS> ) VALUES ( <UPDATE_COLUMNS> ) NOT ATOMIC CONTINUE ON SQLEXCEPTION;";*/
	private static final String INSERT_SQL = "INSERT INTO <SCHEMA.TABLE> (<COLUMN_INSERT_LABELS>) VALUES (<VALUES>);";

	/**
	 * Process the supplied data element by calling jdbcBatchItemWriter.
	 * Sets the <ROW_COUNT> to the number of records that are being used in batch
	 * More ({@link @see org.springframework.batch.item.ItemWriter#write(java.util.List)}
	 *
	 * @param List of item - CCMContainer
	 * @throws Exception if there are errors. The framework will catch the
	 * exception and convert or rethrow it as appropriate.
	 */

	@Override
	public void write(final List<? extends DataContainer> item) throws Exception {
		this.rowCount = String.valueOf(item.size());
		this.sqlStatement = sqlStatement.replace("<ROW_COUNT>", rowCount);
		super.setSql(sqlStatement);
		super.write(item);

	}

	/**
	 * Checks mandatory properties i.e. generation of SQL using DB2 column names and file Column names
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() {

		//getting JobControl for the table
		jobControl = jobCtrlService.getJobControl(jobControlId);

		String sql = null;
		final String db2TableName = FlatFileUtil.DB2_ICM_TABLE_PREFIX+jobControl.getTableName();

		//Getting refData to map
		final List<RefData> allRefDatas = refDataService.getRefDataForDatabase(mappingName);
		final Map<String, String> idMap = new HashMap<>();
		final Map<String, String> db2ColumnMap = new HashMap<>();
		final Map<String, String> fileColumnMap = new HashMap<>();

		//retrieving only column names and column ids from refdata for a specific table and putting into the map
		for (final RefData refData : allRefDatas) {
			if (refData.getDb2TableName().equalsIgnoreCase(db2TableName.replace("_RP", ""))) {
				db2ColumnMap.put(refData.getDb2ColumnName(), refData.getId().getColumnNameFromFile());
				fileColumnMap.put(refData.getId().getColumnNameFromFile(), refData.getDb2ColumnName());
				if (refData.getId().getColumnIdOfFile().equalsIgnoreCase("Y")) {
					idMap.put(refData.getId().getColumnNameFromFile(), refData.getDb2ColumnName());
				}
			}
		}

		//Mapping column id's to the DB2 table
		final List<String> idColumn = new ArrayList<>();
		final Iterator<?> iterator = idMap.entrySet().iterator();
		while (iterator.hasNext()) {
			final Map.Entry pairs = (Map.Entry) iterator.next();
			idColumn.add((String) pairs.getValue());

		}

		//getting columns for the specific table
		final List<OracleSystemColumn> columns = oracleSystemColumnService.getOracleColumnsForTable(schema, db2TableName);

		final List<String> columnLabels = new ArrayList<>();
		final List<String> columnLabelsFromFile = new ArrayList<>();

		//Adds column names that we have in file into the columnLabelsFromFile
		for (int i = 0; i < columnNames.length; i++) {
			columnLabelsFromFile.add(columnNames[i]);
		}

		//Adds column names that we have in DB2 into the columnLabels
		for (final OracleSystemColumn oracleSystemColumn : columns) {
			columnLabels.add(oracleSystemColumn.getId().getColumnName());
		}

		//checking for null column names. If we found we throw exception
		for (final String string : columnLabelsFromFile) {
			if (fileColumnMap.get(string) == null) {
				throw new IllegalStateException("The mapping is missing from REF_DATA_TA for the ["+string+"] column of file for table ["+db2TableName+"]");
			}
		}

		final List<String> mapFileColumnLabels = new ArrayList<>();
		for (final String string : columnLabels) {
			if (db2ColumnMap.get(string) == null) {
				logger.warn("The mapping is missing from REF_DATA_TA for the ["+string+"] column for table ["+db2TableName+"]. This column might not "
						+ "be in the file that we receive");
			}else {
				mapFileColumnLabels.add(db2ColumnMap.get(string));
			}
		}

		final List<String> mappedDb2Columns = new ArrayList<>();
		for (final String string : mapFileColumnLabels) {
			mappedDb2Columns.add(fileColumnMap.get(string));
		}

		/**
		 * it generates the merge SQL statement and Insert SQL statements depending on the
		 * behavior of the DB2 table
		*/
		switch (jobControl.getBehaviorCode()) {
		case M:
			sql = getMergeSQL(schema+DOT+db2TableName, mappedDb2Columns, mapFileColumnLabels, idColumn);
			break;

			//clears the table first and inserts the data
		case P:
			//ccmOracleService.deleteFromTable(db2TableName);
			sql = getInsertSQL(schema+DOT+db2TableName, mappedDb2Columns, mapFileColumnLabels);
			break;
		default:
			throw new IllegalArgumentException("Illegal behavior code, [" + jobControl.getBehaviorCode()
					+ "] is not a valid status code for merge and load replace behavior.");
		}

		this.sqlStatement = sql;
		this.setSql(sql);
		this.setAssertUpdates(false);
		super.afterPropertiesSet();
	}

	/**
	 * Gets the merge sql.
	 *
	 * @param schemaTable - the table name with schema
	 * @param columnLabels - list of DB2 column names
	 * @param columnLabelsFromFile - the column names from file
	 * @param columnId - The primary key of table
	 * @return the merge sql as a String
	 */
	private String getMergeSQL(final String schemaTable, final List<String> columnLabels, final List<String> columnLabelsFromFile, final List<String> columnId ) {
		String sql = "";

		sql += MERGE_SQL;
		sql = sql.replace(SCHEMA_TABLE, schemaTable);
		sql = sql.replace(VALUES, FlatFileUtil.getColumValuesLabels(columnLabelsFromFile, columnLabels));
		sql = sql.replace(WHERE_CLAUSE, FlatFileUtil.getUpdateWhereClause(columnId));
		sql = sql.replace(COLUMN_INSERT_LABELS, FlatFileUtil.getColumInsertLabels(columnLabels));
		sql = sql.replace(UPDATE_VALUE, FlatFileUtil.getUpdateColumns(columnLabels));

		final List<String> columnLabelsForUpdate = columnLabels;
		columnLabelsForUpdate.removeAll(columnId);

		sql = sql.replace(COLUMN_UPDATE_LABELS, FlatFileUtil.getColumUpdateLabels(columnLabelsForUpdate));

		logger.info("The executing Merge SQL is: \n[" + new BasicFormatterImpl().format(sql) + "]");
		return sql;
	}

	/**
	 * Gets the insert sql.
	 *
	 * @param schemaTable - DB2 table name with schema
	 * @param columnLabels - list of DB2 column names
	 * @param columnLabelsFromFile - the column names from file
	 * @return the insert sql as a String
	 */
	private String getInsertSQL(final String schemaTable, final List<String> columnLabels, final List<String> columnLabelsFromFile) {
		String sql = "";

		sql += INSERT_SQL;
		sql = sql.replace(SCHEMA_TABLE, schemaTable);
		sql = sql.replace(COLUMN_INSERT_LABELS, FlatFileUtil.getColumInsertLabels(columnLabels));
		sql = sql.replace(VALUES, FlatFileUtil.getColumValuesInserLabels(columnLabelsFromFile));

		logger.info("The executing Insert SQL is: \n[" + new BasicFormatterImpl().format(sql) + "]");
		return sql;
	}

	public void setSchema(final String schema) {
		this.schema = schema;
	}


	public void setRefDataService(final RefDataService refDataService) {
		this.refDataService = refDataService;
	}

	public void setJobControlService(final JobCtrlService jobCtrlService) {
		this.jobCtrlService = jobCtrlService;
	}

	public void setColumnNames(final String[] columnNames) {
		this.columnNames = columnNames;
	}

	public void setJobControl(final Integer jobControlId) {
		this.jobControlId = jobControlId;
	}

	public void setDb2TableName(final String db2TableName) {
		this.db2TableName = db2TableName;
	}

	public String getDb2TableName() {
		return db2TableName;
	}

	public void setBehaviorCode(final BehaviorEnum behaviorCode) {
		this.behaviorCode = behaviorCode;
	}

	public BehaviorEnum getBehaviorCode() {
		return behaviorCode;
	}

	public void setMappingName(final String mappingName) {
		this.mappingName = mappingName;
	}

}
