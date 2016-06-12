package subedi.flatfile.test.support;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Listy of Java methods that will be brought into H2 as functions
 */
public class H2FunctionUtils {

	private static Integer rankInt = 0;

	public static Date toDate(final String dateTime, final String dateFormat) throws Exception {
		return new SimpleDateFormat(convertDateFormatToOracle(dateFormat)).parse(dateTime);
	}

	public static Integer datePart(final String part, final Date dateToPart) {

		if ((dateToPart == null) || (part == null)) {
			return null;
		}

		final Calendar cal = new GregorianCalendar();
		cal.setTime(dateToPart);

		if ("YEAR".equals(part.toUpperCase())) {
			return cal.get(Calendar.YEAR);
		}
		else if ("MONTH".equals(part.toUpperCase())) {
			return cal.get(Calendar.MONTH) + 1;
		}
		else if ("DAY".equals(part.toUpperCase())) {
			return cal.get(Calendar.DAY_OF_MONTH);
		}
		return null;
	}

	public static Timestamp toTimestamp(final String dateTime, final String dateFormat) throws ParseException {
		final SimpleDateFormat sdf = new SimpleDateFormat(convertDateFormatToOracle(dateFormat));
		final Date date = sdf.parse(dateTime);
		return new Timestamp(date.getTime());

	}

	public static Integer rank() throws ParseException {
		return rankInt++;
	}

	private static String convertDateFormatToOracle(String dateFormat) {
	    if (dateFormat.contains("MON")) {
	        dateFormat = dateFormat.replace("MON", "MMM");
	    }
		if (dateFormat.contains("RR")) {
			dateFormat = dateFormat.replace("RR", "YY");
		}
	    if (dateFormat.contains("Y")) {
	        dateFormat = dateFormat.replaceAll("Y", "y");
	    }
	    if (dateFormat.contains("D")) {
	        dateFormat = dateFormat.replaceAll("D", "d");
	    }
	    if (dateFormat.contains("HH")) {
	        dateFormat = dateFormat.replaceAll("HH", "hh");
	    }
	    if (dateFormat.contains("hh24")) {
	        dateFormat = dateFormat.replaceAll("hh24", "hh");
	    }
	    if (dateFormat.contains("MI") || dateFormat.contains("mi")) {
	        dateFormat = dateFormat.replaceAll("MI", "mi").replaceAll("mi", "mm");
	    }
	    if (dateFormat.contains("SS")) {
	        dateFormat = dateFormat.replaceAll("SS", "ss");
	    }
	    if (dateFormat.contains("XFF")) {
	        dateFormat = dateFormat.replaceAll("XFF", ".SS");
	    }
		if (dateFormat.contains("AM")) {
			dateFormat = dateFormat.replaceAll("AM", "a");
		}
		return dateFormat;
	}

	/**
	 * H2 function used to mimick Oracle's DATE_SUB function. Currently only works to subtract YEAR and MONTH interval,
	 * feel free to add more!
	 * @param date
	 * @param interval
	 * @param unit
	 * @return
	 */
	public static Date dateSubtract(Date date, final String interval, final String unitt) {

		final int unit = Integer.valueOf(unitt);

		if ("YEARS".equals(interval)) {
			date = DateUtils.addYears(date, unit);
		} else if ("MONTHS".equals(interval)) {
			date = DateUtils.addMonths(date, unit);
		}
		return date;
	}

}
