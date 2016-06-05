package subedi.flatfile.test.support;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Util class to print pertinent data during step and job tests.
 */
public class DataQueryUtils {

	@SuppressWarnings("unchecked")
	public static void printFiles(final File directory) throws IOException {

		final List<File> files = (List<File>) FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		for (final File file : files) {
			if (file.isDirectory()) {
				printFiles(file);
			}
			System.out.println("FILE: " + file.getName());
			System.out.println("PATH: " + file.getAbsolutePath());
			final List<String> fileLines = FileUtils.readLines(file);
			System.out.println("----------------------------------");
			for (final String line : fileLines) {
				System.out.println("'" + line + "'");
			}
			System.out.println("----------------------------------");
		}
	}

	public static void printFileInfo(final File directory) {
		final String[] files = directory.list();

		System.out.println("DIRECTORY: " + directory.getPath());
		System.out.println("----------------------------------");
		for (final String file : files) {
			System.out.println("FILE: " + file);

		}
		System.out.println("----------------------------------");
	}

	@SuppressWarnings("unchecked")
	public static int countFiles(final File directory) {
		final
		List<File> files = (List<File>) FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		return files.size();
	}

	public static List<String> skipColumns = Arrays.asList(new String[] { "MAINT_DTM", "CREATE_DTM", "MAINT_USER_ID",
			"MAINT_USERID" });
	
	public static void printSQLQuery(final JdbcTemplate jdbcTemplate, final String sql) {
		final List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		System.out.println("RETURN RESULTS FOR: " + sql);

		int cursor = 0;

		for (final Map<String, Object> entry : result) {
			System.out.print("  " + cursor + ":\t[");
			for (final Entry<String, Object> column : entry.entrySet()) {
				if ((column.getValue() != null) && !skipColumns.contains(column.getKey())) {
					System.out.print(column.getKey() + ": " + column.getValue() + ",");
				}
			}
			System.out.println("]");
			cursor++;
		}
		System.out.println();
	}

	public static void printSQLQueryInsertStatement(final JdbcTemplate jdbcTemplate, final String sql, final String schema, final String table) {
		final List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
		System.out.println("RETURN INSERT STATEMENT FOR: " + sql);

		int cursor = 0;

		for (final Map<String, Object> entry : result) {

			System.out.println("STATEMENT " + cursor + ":");

			final StringBuilder columns = new StringBuilder();
			for (final Entry<String, Object> column : entry.entrySet()) {
				if ((column.getValue() != null) && !skipColumns.contains(column.getKey())) {
				columns.append(column.getKey());
					columns.append(",");
				}
			}
			columns.delete(columns.length() - 1, columns.length());
			System.out.println("  INSERT INTO " + schema + "." + table + "(" + columns.toString() + ")");
			final StringBuilder values = new StringBuilder();
			for (final Entry<String, Object> column : entry.entrySet()) {
				final Object value = column.getValue();
				String valueStr;
				if ((column.getValue() != null) && !skipColumns.contains(column.getKey())) {
				if (value == null) {
					valueStr = "NULL";
				} else if ((value instanceof String) || (value instanceof Date) || (value instanceof Timestamp)) {
					valueStr = "'" + value + "'";
				} else {
					valueStr = value.toString();
				}

				values.append(valueStr);
					values.append(",");
				}
			}
			values.delete(values.length() - 1, values.length());
			System.out.println("  VALUES(" + values.toString() + ");");
			cursor++;
		}
		System.out.println();
	}

	public static void printJobLaunchData(final JdbcTemplate jdbcTemplate) {
		System.out.println("------PRINTING DATA FROM ALL BATCH EXECUTION TABLES:----------");
		printSQLQuery(jdbcTemplate, "SELECT * FROM BATCH_JOB_LAUNCH");
		printSQLQuery(jdbcTemplate, "SELECT * FROM BATCH_LAUNCH_PARAMS");
		System.out.println("-------------------------------------------------------------");
	}

	public static void printExecutionData(final JdbcTemplate jdbcTemplate) {
		System.out.println("------PRINTING DATA FROM ALL BATCH EXECUTION TABLES:----------");
		printSQLQuery(jdbcTemplate, "SELECT * FROM BATCH_JOB_EXECUTION");
		printSQLQuery(jdbcTemplate, "SELECT * FROM BATCH_JOB_INSTANCE");
		printSQLQuery(jdbcTemplate, "SELECT * FROM BATCH_JOB_EXECUTION_PARAMS");
		printSQLQuery(jdbcTemplate, "SELECT * FROM BATCH_STEP_EXECUTION ORDER BY START_TIME");
		System.out.println("-------------------------------------------------------------");
	}

	public static int queryForInt(final JdbcTemplate jdbcTemplate, final String sql) {
		return jdbcTemplate.queryForObject(sql, Integer.class).intValue();
	}

}
