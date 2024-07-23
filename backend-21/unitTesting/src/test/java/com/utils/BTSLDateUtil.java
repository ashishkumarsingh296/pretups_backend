package com.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.classes.CONSTANT;
import com.commons.PretupsI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;

/**
 * Utility to convert date to Locale date
 * @author tejeshvi.roy
 *
 */
public class BTSLDateUtil {

    /**
     * Default Constructor
     * 
     */
    private BTSLDateUtil() {
        super();
    }
    /**
     * This function will return date when milisec is passed on the basis of Locale
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleDate(String date) {
    	final String methodName = "getSystemLocaleDate";
    	String localeDate = "";
    	Log.debug("Entered " + methodName + "(" + date + ")");
    	try {
    		localeDate = DateConverterFactory.getLocaleDate(date);
		} catch (ParseException e) {
		}
    	Log.debug("Exiting " + methodName + "with localeDate=" + localeDate);
    	return localeDate;
    }
    
    public static String getTwoDigit(int p_no) {
        String digit = "";
        if (p_no <= 9) {
            digit = "0" + p_no;
        } else {
            digit = p_no + "";
        }
        return digit;
    }
    
    /**
     * Method will return the date of the month
     * @param date
     * @param datePart : which part is required : dateOfMonth, month or year
     * @return
     */
    public static int get(String date, String datePart) {
    	final String methodName = "get";
    	Log.debug("Entered " + methodName + "(" + date + ", " + datePart + ")");
    	
    	String[] dateArr = null;
    	int year;
    	int month;
    	int dateOfMonth;
    	String nonMonDate = getNonMonDate(date, getDateFormat(date));
    	final String regexDateMonth = "([0-9]{1}|[0-9]{2})";
    	final String regexYear = "([0-9]{4})";
    	final String regexYear2 = "([0-9]{2})";
    	if (nonMonDate.matches(regexDateMonth + PretupsI.FORWARD_SLASH + regexDateMonth + PretupsI.FORWARD_SLASH + regexYear) || 
    			nonMonDate.matches(regexDateMonth + PretupsI.FORWARD_SLASH + regexDateMonth + PretupsI.FORWARD_SLASH + regexYear2)) {
    		dateArr = nonMonDate.split(PretupsI.FORWARD_SLASH);
    		dateOfMonth = 0;
    		month = 1;
    		year = 2;
        } else if (nonMonDate.matches(regexDateMonth + PretupsI.HYPHEN + regexDateMonth + PretupsI.HYPHEN + regexYear)) {
        	dateArr = nonMonDate.split(PretupsI.HYPHEN);
        	dateOfMonth = 0;
    		month = 1;
    		year = 2;
        } else if (nonMonDate.matches(regexYear + PretupsI.HYPHEN + regexDateMonth + PretupsI.HYPHEN + regexDateMonth)) {
        	dateArr = nonMonDate.split(PretupsI.HYPHEN);
        	dateOfMonth = 2;
    		month = 1;
    		year = 0;
        } else if (nonMonDate.matches(regexYear + PretupsI.FORWARD_SLASH + regexDateMonth + PretupsI.FORWARD_SLASH + regexDateMonth)) {
        	dateArr = nonMonDate.split(PretupsI.FORWARD_SLASH);
        	dateOfMonth = 2;
    		month = 1;
    		year = 0;
        } else {
        	datePart = PretupsI.EMPTY;
        	dateOfMonth = 0;
    		month = 0;
    		year = 0;
        }
    	
    	Log.debug("Exiting " + methodName);
    	return datePart.equalsIgnoreCase(PretupsI.DATE) ? Integer.parseInt(dateArr[dateOfMonth]) :
    		datePart.equalsIgnoreCase(PretupsI.MONTH) ? Integer.parseInt(dateArr[month]) : 
    			datePart.equalsIgnoreCase(PretupsI.YEAR) ? Integer.parseInt(dateArr[year]) : -1; 
    	
    }
    
    public static String getNonMonDate(String date, String passedDateInFormat) {
		final String methodName = "getNonMonDate";
		Log.debug("Entered " + methodName + "(" + date + ", " + passedDateInFormat + ")");

		String dateArr[];
		String finalDate = PretupsI.EMPTY;
		if(!isNullString(date)) {
			if(PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(passedDateInFormat) || 
					PretupsI.DATE_FORMAT_YYYYMMMDD_HYPHEN.equalsIgnoreCase(passedDateInFormat)) {
				dateArr = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
				int monthNumber = DateConverterFactory.getMonthNumeric(dateArr[1]);
				String monthNumberStr = monthNumber > -1 ? getTwoDigit(monthNumber) : dateArr[1];
				finalDate = dateArr[0] + PretupsI.FORWARD_SLASH + monthNumberStr + 
						PretupsI.FORWARD_SLASH + dateArr[2];
			} else {
				finalDate = date;
			}
		}

		Log.debug("Exiting " + methodName);
		return finalDate;
	}
    
    public static String getStringDateInDDMMYY(String date) throws ParseException {
    	final String methodName = "getStringDateInDDMMYY";
    	Log.debug("Entered " + methodName + "(" + date + ")");
    	
        Date actualDate;
        String format = getDateFormat(date);
        if(isNullString(format)) {
        	return PretupsI.EMPTY;
        }
        SimpleDateFormat inFormat = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYY);
        SimpleDateFormat fromFormat = new SimpleDateFormat(format);
        actualDate = fromFormat.parse(date);

        Log.debug("Exiting " + methodName);
        return inFormat.format(actualDate);
    }
    
    /**
     * Returns the format type of the passed date
     * We can add more regex to return more formats
     * @param date
     * @return
     */
    public static String getDateFormat(String date) {
    	final String methodName = "getDateFormat";
    	Log.debug("Entered " + methodName + "(" + date + ")");
    	
    	String dateFormat = null;
    	if(isNullString(date)) {
        	return dateFormat;
        }
    	if (date.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
    		dateFormat = PretupsI.DATE_FORMAT_DDMMYYYY;
        } else if (date.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")) {
        	dateFormat = PretupsI.DATE_FORMAT_DDMMYYYY_HYPHEN;
        } else if (date.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT_YYYYMMDD_HYPHEN;
        } else if (date.matches("([0-9]{4})/([0-9]{2})/([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT_YYYYMMDD;
        } else if (date.matches("([0-9]{2})/([0-9]{2})/([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT;//Assuming date is in dd/MM/yy not in yy/MM/dd
        } else if (date.matches("([0-9]{2})-([0-9]{2})-([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT_HYPHEN;//Assuming date is in dd-MM-yy not in yy-MM-dd
        } else if (date.matches("([0-9]{4})/([a-zA-Z]{3})/([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT_YYYYMMMDD;
        } else if (date.matches("([0-9]{2})/([a-zA-Z]{3})/([0-9]{4})")) {
        	dateFormat = PretupsI.DATE_FORMAT_DDMMMYYYY;
        } else if (date.matches("([0-9]{2})/([a-zA-Z]{3})/([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT_DDMMMYY;//Assuming date is in dd/MMM/yy not in yy/MMM/dd
        } else if (date.matches("([0-9]{4})-([a-zA-Z]{3})-([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT_YYYYMMMDD_HYPHEN;
        } else if (date.matches("([0-9]{2})-([a-zA-Z]{3})-([0-9]{4})")) {
        	dateFormat = PretupsI.DATE_FORMAT_DDMMMYYYY_HYPHEN;
        } else if (date.matches("([0-9]{2})-([a-zA-Z]{3})-([0-9]{2})")) {
        	dateFormat = PretupsI.DATE_FORMAT_DDMMMYY_HYPHEN;//Assuming date is in dd/MMM/yy not in yy/MMM/dd
        } else {
        	dateFormat = getDateTimeFormat(date);
        }

    	Log.debug("Exiting " + methodName);
    	return dateFormat;
    }
    
    /**
     * Returns the format type of the passed date
     * We can add more regex to return more formats
     * @param date
     * @return
     */
    public static String getDateTimeFormat(String date) {
    	final String methodName = "getDateTimeFormat";
    	Log.debug("Entered " + methodName + "(" + date + ")");
    	
    	String dateFormat = null;
    	if(isNullString(date)) {
        	return dateFormat;
        }
    	if (date.matches("([0-9]{2})/([0-9]{2})/([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
    		dateFormat = PretupsI.TIMESTAMP_DDMMYYYYHHMMSS;
        } else if (date.matches("([0-9]{2})-([0-9]{2})-([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_DDMMYYYYHHMMSS_HYPHEN;
        } else if (date.matches("([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_YYYYMMDDHHMMSS_HYPHEN;
        } else if (date.matches("([0-9]{4})/([0-9]{2})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_YYYYMMDDHHMMSS;
        } else if (date.matches("([0-9]{2})/([0-9]{2})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_DATESPACEHHMMSS;//Assuming date is in dd/MM/yy not in yy/MM/dd
        } else if (date.matches("([0-9]{2})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_DATESPACEHHMMSS_HYPHEN;//Assuming date is in dd-MM-yy not in yy-MM-dd
        } else if (date.matches("([0-9]{4})/([a-zA-Z]{3})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_YYYYMMMDDHHMMSS;
        } else if (date.matches("([0-9]{2})/([a-zA-Z]{3})/([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_DDMMMYYYYHHMMSS;
        } else if (date.matches("([0-9]{2})/([a-zA-Z]{3})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_DDMMMYYHHMMSS;//Assuming date is in dd/MMM/yy not in yy/MMM/dd
        } else if (date.matches("([0-9]{4})-([a-zA-Z]{3})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_YYYYMMMDDHHMMSS_HYPHEN;
        } else if (date.matches("([0-9]{2})-([a-zA-Z]{3})-([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_DDMMMYYYYHHMMSS_HYPHEN;
        } else if (date.matches("([0-9]{2})-([a-zA-Z]{3})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})")) {
        	dateFormat = PretupsI.TIMESTAMP_DDMMMYYHHMMSS_HYPHEN;//Assuming date is in dd/MMM/yy not in yy/MMM/dd
        } else {
        	dateFormat = DBHandler.AccessHandler.getSystemPreference(CONSTANT.SYSTEM_DATE_FORMAT);
        }
    	
    	Log.debug("Exiting " + methodName);
    	return dateFormat;
    }
    
    public static boolean isNullString(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
	 * This function will return date in the passed format
	 * @param date
	 * @param dateInFormat
	 * @return
	 * @throws ParseException
	 */
	public static String getDateStringInFormat(String date, String dateInFormat) throws ParseException {
		final String methodName = "getDateStringInFormat";
		String finalDate = PretupsI.EMPTY;
		Log.debug("Entered " + methodName + "(" + date + ", " + dateInFormat + ")");

		if(!isNullString(date)) {
			String formatToUse;
			Date newDate = getDateFromDateString(date, BTSLDateUtil.getDateFormat(date));
			if(isNullString(dateInFormat)) {
				formatToUse = SystemPreferences.DATE_FORMAT_CAL_JAVA;
			} else {
				formatToUse = dateInFormat;
			}
			finalDate = getDateStringFromDate(newDate, formatToUse);
//			finalDate = BTSLDateUtil.getFinalDate(finalDate);

			Log.debug("Exiting " + methodName + "with date=" + finalDate);
		}
		return finalDate;
	}
	/**
     * Get Date From Date String
     * 
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date getDateFromDateString(String dateStr, String format) throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(dateStr);
    }
    

	 /**
* Converts Util date to Sql Date
* 
* @param utilDate
* @return
*/
public static java.sql.Date getSQLDateFromUtilDate(java.util.Date utilDate) {
   final String METHOD_NAME = "getSQLDateFromUtilDate";
   java.sql.Date sqlDate = null;

	 
	 if (utilDate != null) {
		final java.util.Date utilDateNew=(Date)utilDate.clone();
		try {
			utilDate.setHours(23);
			utilDate.setMinutes(59);
			utilDate.setSeconds(59);
			sqlDate = new java.sql.Date(utilDate.getTime());
			utilDate=utilDateNew;
		} catch (Exception e) {
			sqlDate = new java.sql.Date(utilDate.getTime());
			utilDate=utilDateNew;
		}
     }
       
    return sqlDate;
  }// end of UtilDateToSqlDate

    /**
     * Get Date String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateStringFromDate(Date date, String format) throws ParseException {
        if (isNullString(format)) {
            format = PretupsI.DATE_FORMAT;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }
    
    /**
     * This function will return Gregorian date for any Local Calendar.
     * As of now only implemented for Persian Calendar
     * @param date
     * @return date as String
     */
    public static String getGregorianDateInString(String date) {
    	final String methodName = "getGregorianDateInString";
    	Log.debug("Entered " + methodName + "(" + date + ")");
    	//Check for date format. Need to test
    	
    	/*String format = DBHandler.AccessHandler.getSystemPreference(CONSTANT.DATE_FORMAT_CAL_JAVA);
    	if(!BTSLUtil.isNullString(format) && format.length() != date.length()) {
    		Log.debug("invalid dateFormat error");
    	}*/
    	
    	String str;
    	String finalDate = getFinalDate(date);
    	String CALENDER_TYPE = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CALENDAR_TYPE);
    	if(!BTSLUtil.isNullString(CALENDER_TYPE) && 
				!PretupsI.GREGORIAN.equalsIgnoreCase(CALENDER_TYPE)) {
    		str = DateConverterFactory.getGregorianDateInString(finalDate);
    	} else {
    		str = finalDate;
    	}

    	Log.debug("Exiting " + methodName + " with dateValue=" + str);
    	return str;
    }
    
    /**
     * This function will return finally processed date
     * @param date
     * @return
     */
    public static String getFinalDate(String date) {
		final String methodName = "getFinalDate";
		Log.debug("Entered " + methodName + "(" + date + ")");
		
		String finalDate;
		HashMap<String, String> dateFormatMap = getMonFormatMap(date);
    	if(!BTSLUtil.isNullString(date) && dateFormatMap.get(PretupsI.IS_MON_FORMAT_LITERAL).equalsIgnoreCase(PretupsI.TRUE)) {
    		finalDate = getNonMonDate(date, dateFormatMap.get(PretupsI.DATE_FORMAT_LITERAL));
    	} else {
    		finalDate = date;
    	}

    	Log.debug("Exiting " + methodName + " with finalDate=" + finalDate);
		return finalDate;
	}
    
    /**
     * This function will check whether the passed
     * @param dateOrFormat
     * @return
     */
    public static HashMap<String, String> getMonFormatMap(String dateOrFormat) {
    	final String methodName = "getMonFormatMap";
    	boolean isMonFormat;
    	HashMap<String, String> dateFormatMap = new HashMap<String, String>();
    	dateFormatMap.put(PretupsI.IS_MON_FORMAT_LITERAL, PretupsI.FALSE);
    	Log.debug("Entered " + methodName + "(" + dateOrFormat + ")" );

    	if(!BTSLUtil.isNullString(dateOrFormat)) {
    		dateFormatMap.put(PretupsI.DATE_FORMAT_LITERAL, getDateFormat(dateOrFormat));
    		isMonFormat = checkIfMonFormat(dateFormatMap.get(PretupsI.DATE_FORMAT_LITERAL));
    		if(isMonFormat) {
    			dateFormatMap.put(PretupsI.IS_MON_FORMAT_LITERAL, PretupsI.TRUE);
    		}
    	}

    	Log.debug("Exiting " + methodName + "(" + Arrays.asList(dateFormatMap) + ")");
    	return dateFormatMap;
    }
    
    /**
     * This function will check whether the passed date or date format
     * is MMM(means date or date format contains MMM for month . Ex- dd/MMM/yyyy or yyyy/MMM/dd or dd/MMM/yy) format or not
     * @param dateOrFormat
     * @return
     */
    public static boolean checkIfMonFormat(String dateOrFormat) {
    	final String methodName = "checkIfMonFormat";
    	boolean isMonFormat = false;
    	Log.debug("Entered " + methodName + "(" + dateOrFormat + ")");
    	
    	if(!BTSLUtil.isNullString(dateOrFormat)) {
    		if(PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(dateOrFormat)) {//add other conditions
    			isMonFormat = true;
    		}
    	}

    	Log.debug("Exiting " + methodName);
    	return isMonFormat;
    }
    
    
    /**
     * Get java.sql.Timestamp from java.util.Date
     * 
     * @param date
     * @return
     */
    public static Timestamp getTimestampFromUtilDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Timestamp(date.getTime());
    }

}