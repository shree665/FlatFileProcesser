/**
 *
 */
package subedi.flatfile.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author vivek.subedi
 *
 */
public class OracleCaster {

	private static Logger logger = LoggerFactory.getLogger(OracleCaster.class);

	/**
	 * This method casts any given value using sqlType of DB2 column and returns the casted value
	 * @param icmValue
	 * @param sqlType
	 * @param columnSize
	 * @param columnName
	 * @return casted icmValue
	 */
	public static Object castCCMValue(final String icmValue, final Integer sqlType, final Integer columnSize, final String columnName) {

		if (StringUtils.isBlank(icmValue)) {
			return null;
		}

		try {
			switch (sqlType.intValue()) {
			case Types.CHAR:
				return castString(icmValue, columnSize, columnName);
			case Types.DATE:
				return castDate(icmValue, columnName);
			case Types.INTEGER:
				return castInteger(icmValue, columnName);
			case Types.NUMERIC:
				return castDecimal(icmValue, columnName, columnSize);
			case Types.LONGNVARCHAR:
				return castString(icmValue, columnSize, columnName);
			case Types.TIME:
				throw new IllegalArgumentException("Unsupported sqlType [TIME] for data value [" + icmValue + "]");
			case Types.TIMESTAMP:
				return castTimestamp(icmValue, columnName, columnSize);
			case Types.VARCHAR:
				return castString(icmValue, columnSize, columnName);
			case Types.BLOB:
				throw new IllegalArgumentException("Unsupported sqlType [BLOB] for data value [" + icmValue + "]");
			case Types.CLOB:
				throw new IllegalArgumentException("Unsupported sqlType [CLOB] for data value [" + icmValue + "]");
			default:
				throw new IllegalArgumentException("Unsupported sqlType [" + sqlType + "] for data value [" + icmValue + "]");
			}

		} catch (final IllegalArgumentException e) {
			logger.error("Illegal argument while trying to cast value [" + icmValue + "] to sqlType [" + sqlType + "] for column [" + columnName + "]");
			throw e;
		}

	}
	
	private static Integer castInteger(String rightNowValue, String columnName) {
		try {
			return new Integer(Integer.parseInt(rightNowValue));
		} catch (NumberFormatException e) {
			logger.info("Unable to parse [" + rightNowValue + "] as a Integer for column [" + columnName + "], passing null");
			return null;
		}
	}

	/**
	 * This method casts a given value to BigDecimal and returns it
	 * @param icmValue
	 * @param columnName
	 * @param columnSize
	 * @return Bigdeciaml icmValue
	 * @exception NumberFormatException
	 */
	private static BigDecimal castDecimal(final String icmValue, final String columnName, final Integer columnSize) {
		BigDecimal decimal;
		try {
			decimal = new BigDecimal(icmValue);
		} catch (final NumberFormatException e) {
			logger.info("Unable to cast value ["+icmValue+"], for column ["+columnName+"] passing null instead");
			return null;
		}

		return decimal;

	}

	/**
	 * Cast string. casts any give value to the string
	 *
	 * @param ccmValue the ccm value
	 * @param columnSize the column size
	 * @return the string
	 */
	private static String castString(String icmValue, final Integer columnSize, final String columnName) {

		if (icmValue.length() > columnSize.intValue()) {
			icmValue = icmValue.substring(0, columnSize.intValue());
			logger.debug("Value's length for the column ["+columnName+"] is longer than the DB2 column size. Truncating to fit DB2 column length of ["+columnSize+"]");
		}

		return icmValue;
	}

	/**
	 * Cast date.castes any given value to the java.sql.date using specific format
	 *
	 * @param icmValue the ccm value
	 * @return the java.sql. date
	 */
	private static Date castDate(final String icmValue, final String columnName) {
		Date returnDate = null;
		try {
			returnDate = new Date(DateTime.parse(icmValue, FlatFileUtil.ICM_FILE_DATE_FORMAT).getMillis());
		} catch (final IllegalArgumentException e) {
			logger.info("Unable to parse [" + icmValue + "] as a Date for column [" + columnName + "], passing null");
			return null;
		}

		return returnDate;
	}

	/**
	 * Cast timestamp. casts any given value to the java.sql.Timestamp using specific timestamp format
	 *
	 * @param icmValue the icm value
	 * @param columnName the column name
	 * @param columnSize the column size
	 * @return the java.sql. timestamp
	 */
	private static Timestamp castTimestamp(final String icmValue, final String columnName, final Integer columnSize) {
		Timestamp returnTimestamp = null;
		try {
			returnTimestamp = new Timestamp(DateTime.parse(icmValue, FlatFileUtil.ICM_FILE_TIMESTAMP_FORMAT).getMillis());
		} catch (final IllegalArgumentException e) {
			try {
				returnTimestamp = new Timestamp(DateTime.parse(icmValue, FlatFileUtil.ICM_FILE_TIMESTAMP_FORMAT_WITH_MILLISECONDS).getMillis());
			} catch (final IllegalArgumentException e2) {
				try {
					returnTimestamp = new Timestamp(DateTime.parse(icmValue, FlatFileUtil.ICM_FILE_TIMESTAMP_FORMAT_WITH_MILLISECOND).getMillis());
				} catch (final IllegalArgumentException e3) {
					logger.info("Unable to parse [" + icmValue + "] as a Timestamp for column [" + columnName + "], passing null");
					return returnTimestamp;
				}

			}
		}
		return returnTimestamp;

	}

}
