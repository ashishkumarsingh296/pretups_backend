package com.btsl.util;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.PersianCalendar;
import com.ibm.icu.util.ULocale;

/**
 * Utility to get locale date and corresponding values
 * @author tejeshvi.roy
 *
 */
public class BTSLDateUtil {

    private static final Log log = LogFactory.getLog(BTSLDateUtil.class.getName());
    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String METHOD_GET_CALENDAR = "getCalendar";
    public static final String METHOD_GET_SYSTEM_LOCALE_DATE = "getSystemLocaleDate";
    private static String[] _errorMsg = new String[3];
    /**
     * Default Constructor
     * 
     */
    private BTSLDateUtil() {
        super();
    }
    /**
     * This function will return Gregorian date for any Local Calendar.
     * As of now only implemented for Persian Calendar
     * @param date
     * @return date as String
     */
    public static String getGregorianDateInString(String date) {
    	final String methodName = "getGregorianDateInString";
    	String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	//Check for date format. Need to test
    	String format = dateFormatCalJava;
    	if(!BTSLUtil.isNullString(format) && !BTSLUtil.isNullString(date) && format.length() != date.length()) {
    		log.error(methodName, "invalid dateFormat");
    	}
    	String str;
    	String finalDate = getFinalDate(date);
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(!BTSLUtil.isNullString(calendarType) && 
				!PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
    		str = DateConverterFactory.getGregorianDateInString(finalDate);
    	} else {
    		str = finalDate;
    	}
    	return str;
    }
    /**
     * This function will return the timestamp in Gregorian when passed in other calendar
     * Ex- 1397/02/13 13:35:53 to gregorian timestamp 
     * @param date
     * @return
     */
    public static String getGregorianTimeStampInString(String date) {
    	//Check for date format. Need to test
    	String[] dateArr = null;
    	String tempDate;
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	if(!BTSLUtil.isNullString(date)) {
    		dateArr = date.split(PretupsI.SPACE);
    	}
    	if(dateArr!=null && dateArr.length > 1) {
    		tempDate = dateArr[0];
    	} else {
    		tempDate = date;
    	}
    	String str;
    	String finalDate = getFinalDate(tempDate);
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(!BTSLUtil.isNullString(calendarType) && 
				!PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
    		str = DateConverterFactory.getGregorianDateInString(finalDate);
    	} else {
    		str = finalDate;
    	}
    	if(dateArr !=null && dateArr.length > 1) {
    		str = str + PretupsI.SPACE + dateArr[1];
    	}
    	return str;
    }
    /**
     * This function will return Gregorian date for any Local Calendar.
     * As of now only implemented for Persian Calendar
     * @param date
     * @return date as String
     */
    /*public static String getGregorianDateInString1(String date, ActionMessages errors) {
    	final String methodName = "getGregorianDateInString";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED + PretupsI.LOGGER_DATE_PASSED + date);
        }
    	//Check for date format. Need to test
    	String format = dateFormatCalJava;
    	if(!BTSLUtil.isNullString(format) && !BTSLUtil.isNullString(date) && format.length() != date.length()) {
    		log.error(methodName, "invalid dateFormat");
    		errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
    	}
    	String str;
    	String finalDate = getFinalDate(date);
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(!BTSLUtil.isNullString(calendarType) && 
				!PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
    		str = DateConverterFactory.getGregorianDateInString(finalDate);
    	} else {
    		str = finalDate;
    	}
    	if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.EXITED + PretupsI.LOGGER_DATE_RETURNED + str);
        }
    	return str;
    }*/
    
    /**
     * This function will return Gregorian date for any Local Calendar.
     * This function must be used for external gateways
     * As of now only implemented for Persian Calendar
     * @param date
     * @return date as String
     */
    public static String getGregorianDateInStringExtGw(String date) {
    	String gregorianDate;
    	String finalDate = getFinalDate(date);
    	String extCalendarType = BTSLUtil.getTrimmedValue((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CALENDAR_TYPE));
    	if(PretupsI.GREGORIAN.equalsIgnoreCase(extCalendarType)) {
    		gregorianDate = finalDate;
    	} else {
    		gregorianDate = DateConverterFactory.getGregorianDateInString(finalDate);
    	}
    	return gregorianDate;
    }
    
    /**
     * This function will return Gregorian date for any Local Calendar.
     * As of now only implemented for Persian Calendar
     * @param date
     * @return date as in Date format
     * @throws ParseException 
     */
    public static Date getGregorianDate(String date) throws ParseException {//Handle null check for this methods
    	Date gregorianDate;
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(!BTSLUtil.isNullString(calendarType) && 
				!PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
    		gregorianDate = BTSLDateUtil.simpleDateForamtter(DateConverterFactory.getGregorianDateInString(date));
    	} else {
    		gregorianDate = BTSLDateUtil.simpleDateForamtter(date);//check this when IS_GREGORIAN is true
    	}
    	return gregorianDate;
    }
    
    /**
     * getCalendar will return the locale specific calendar
     * @param calendar
     * @return
     */
    @SuppressWarnings("deprecation")
	public static Calendar getCalendar(Calendar calendar) {
    	Calendar cal;
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)){
    		cal = new PersianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                    .get(Calendar.DAY_OF_MONTH));
    	} else {
    		cal = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
                    .get(Calendar.DAY_OF_MONTH));
    	}
    	return cal;
    }
    
    /**
     * getCalendar will return a calendar
     * @param year
     * @param month
     * @param date
     * @return
     */
    @SuppressWarnings("deprecation")
	public static Calendar getCalendar(int year, int month, int day) {//apply check for year value - 118, 18, 2018 - should support
    	Calendar cal;
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	int add = year < 100 ? 2000 : year < 1900 && year >= 100 ? 1900 : 0;//This to support both (yyyy)2018 and (yy)18 format
    	year = year + add;
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)){
    		String date = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getTwoDigit(day) + PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit((month + 1))
    				+ PretupsI.FORWARD_SLASH + year);
    		cal = new PersianCalendar(BTSLDateUtil.get(date, BTSLDateUtil.YEAR), BTSLDateUtil.get(date, BTSLDateUtil.MONTH) - 1,
    				BTSLDateUtil.get(date, BTSLDateUtil.DATE));
    	} else {
    		cal = new GregorianCalendar(year, month, day);
    	}
    	return cal;
    }

    /**
     * getCalendar will return a calendar
     * Need to correct the implementation
     * @param year
     * @param month
     * @param date
     * @param hour
     * @param min
     * @param sec
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Calendar getCalendar(int year, int month, int day, int hour, int min, int sec) {
    	Calendar cal;
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	int add = year < 100 ? 2000 : year < 1900 && year >= 100 ? 1900 : 0;//This to support both (yyyy)2018 and (yy)18 format.
    	year = year + add;
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)){
    		String date = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getTwoDigit(day) + PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit((month + 1))
    				+ PretupsI.FORWARD_SLASH + year);
    		cal = new PersianCalendar(BTSLDateUtil.get(date, BTSLDateUtil.YEAR), BTSLDateUtil.get(date, BTSLDateUtil.MONTH) - 1,
    				BTSLDateUtil.get(date, BTSLDateUtil.DATE), hour, min, sec);
    		
    	} else {
    		cal = new GregorianCalendar(year + add, month, day, hour, min, sec);
    	}
    	return cal;
    }
    
    
    
    /**
     * getInstance will return the instance of calendar
     * @return
     */
    @SuppressWarnings("deprecation")
	public static Calendar getInstance() {
    	Calendar cal;
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	String calendarType = BTSLUtil.getTrimmedValue(calenderType);
    	if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)){
    		ULocale locale = new ULocale(PretupsI.LOCALE_PERSIAN);
    		cal = PersianCalendar.getInstance(locale);
    	} else {
    		cal = GregorianCalendar.getInstance();
    	}
    	return cal;
    }
    /**
     * As Date in java does not support date before 1900. So, we need to perform the following
     * This method will return the date which is after/before by numberOfDays to the passed date
     * For implementation - If the passed date is a Persian date but in Gregorian format then we need to first
     * convert date into String. Then get the equivalent date in Gregorian format. Then add number of days. Then convert the date in Persian.
     * Then again create a date object in Gregorian format.  
     * @param date
     * @param numberOfDays
     * @return
     * @throws ParseException 
     */
    /*public static Date getDifferenceDate(Date date, int numberOfDays) throws ParseException {
    	final String methodName = "getDifferenceDate";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }
        String dateInLocale = BTSLUtil.getDateStringFromDate(date, dateFormatCalJava);
        Calendar calendar = BTSLDateUtil.getCalendar(BTSLDateUtil.get(dateInLocale, BTSLDateUtil.YEAR), 
        		BTSLDateUtil.get(dateInLocale, BTSLDateUtil.MONTH), BTSLDateUtil.get(dateInLocale, BTSLDateUtil.DATE));//We need to use parameter date or dateInLocale to get required Parameters
        if(calendar != null) {
        	calendar.add(Calendar.DATE, numberOfDays);        	
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.EXITED);
        }
        return new Date(BTSLDateUtil.getSystemLocaleDate(calendar.getTime()));
    }*/
    
    /**
     * This method will return the system locale date
     * For ex- When "new Date()" is being used. We need to convert that into Locale date(if it is Gregorian/English type)
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleCurrentDate() throws ParseException {
    	final String dateStr = DateConverterFactory.getLocaleDate(new Date());
    	return dateStr;
    }
    
    /**
     * This method will return the system locale date
     * For ex- When "new Date()" is being used. We need to convert that into Locale date(if it is Gregorian/English type)
     * @param date - Date type
     * @return
     * @throws ParseException
     */
    public static String getCalendarTypeDate(Date date) throws ParseException {//getCalendarTypeDate
    	final String dateStr = DateConverterFactory.getLocaleDate(date);
    	return dateStr;
    }
    /**
     * This function will return date when milisec is passed on the basis of Locale
     * @param milisec - new Date().getTime() or System.currentTimeMillis()
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleDate(long milisec) throws ParseException {
    	final String dateStr = DateConverterFactory.getLocaleDate(new Date(milisec));
    	return dateStr;
    }
    /**
     * This function will return date when milisec is passed on the basis of Locale
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleDate(String date) {
    	final String methodName = METHOD_GET_SYSTEM_LOCALE_DATE;
    	String localeDate = PretupsI.EMPTY;
    	try {
    		localeDate = DateConverterFactory.getLocaleDate(date);
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
	            log.error(methodName, e.getMessage());
	        }
		}
    	return localeDate;
    }
    /**
     * This function will return date in String in the passed format
     * @param date
     * @param dateInFormat
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleDate(Date date, String dateInFormat) {
    	final String methodName = METHOD_GET_SYSTEM_LOCALE_DATE;
    	String localeDate = PretupsI.EMPTY;
    	try {
    		localeDate = DateConverterFactory.getLocaleDate(date, dateInFormat);
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
	            log.error(methodName, e.getMessage());
	        }
		}
    	return localeDate;
    }
    
    /**
     * This function will return date in String in the passed format
     * @param date
     * @param dateInFormat
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleDate(String date, String dateInFormat) {
    	final String methodName = METHOD_GET_SYSTEM_LOCALE_DATE;
    	String localeDate = PretupsI.EMPTY;
    	try {
    		localeDate = DateConverterFactory.getLocaleDate(date, dateInFormat);
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
	            log.error(methodName, e.getMessage());
	        }
		}
    	return localeDate;
    }
    /**
     * This method will return the locale current Date/time of the system
     * @return
     */
    public static String getSystemLocaleDateTime() {
    	final String dateTimeStr = getLocaleDateTimeFromDate(new Date());
    	return dateTimeStr;
    }
    
    /**
     * This method will return the date and time of the passed date of timezone/locale SystemPreferences.TIMEZONE_ID
     * For ex- When "new Date()" is being used. We need to convert that into Locale date(if it is Gregorian/English type)
     * @param date
     * @return
     */
    public static String getLocaleDateTimeFromDate(Date date) {//check for date - dd/MM/yyyy or dd/MM/yy or dd/MM/yyyy HH:mm:SS or dd/MM/yy HH:mm:SS
    	final String methodName = "getLocaleDateTimeFromDate";
    	String localeDate = PretupsI.EMPTY;
    	String finalDate = PretupsI.EMPTY;
    	int hours;
    	int mins;
    	int secs;
    	if(date != null) {
    		Calendar calendar = getLocaleCalendar(date);
    		hours = calendar.get(Calendar.HOUR_OF_DAY);
    		mins = calendar.get(Calendar.MINUTE);
    		secs = calendar.get(Calendar.SECOND);
    		try {
    			localeDate = DateConverterFactory.getLocaleDate(new Date(date.getTime()));
    		} catch (ParseException e) {
    			if (log.isDebugEnabled()) {
    				log.debug(methodName, e.getMessage());
    	        }
    		}
    		finalDate = localeDate + PretupsI.SPACE + BTSLUtil.getTwoDigit(hours) + PretupsI.COLON + BTSLUtil.getTwoDigit(mins) + 
    				PretupsI.COLON + BTSLUtil.getTwoDigit(secs);
    	}
		return finalDate;
    }
    
    /**
     * This method will return the date and time of the passed date of timezone/locale SystemPreferences.TIMEZONE_ID
     * For ex- When "new Date()" is being used. We need to convert that into Locale date(if it is Gregorian/English type)
     * @param date
     * @return
     */
    public static String getLocaleDateTimeFromDateWithMili(Date date) {
    	int mili = 0;
    	if(date != null) {
    		Calendar calendar = getLocaleCalendar(date);
    		mili = calendar.get(Calendar.MILLISECOND);
    	}
    	String finalDate;
    	finalDate = getLocaleDateTimeFromDate(date) + "." + mili;
		return finalDate;
    }
    
    /**
     * This function will return the locale
     * @param date
     * @return
     */
    public static String getLocaleTimeStamp(String date) {
    	final String methodName = "getLocaleTimeStamp";
    	String modifiedTimeStamp = PretupsI.EMPTY;
    	String[] dateArr = null;
    	if(!BTSLUtil.isNullString(date)){
    		dateArr = date.split(PretupsI.SPACE);    		
    	}
    	try {
    		if(!BTSLUtil.isNullString(date)) {
    			if(!BTSLUtil.isNullArray(dateArr) && dateArr.length < 2) {
        			modifiedTimeStamp = DateConverterFactory.getLocaleDate(dateArr[0]) + PretupsI.SPACE + 
        					PretupsI.TIME_HHMMSS_VALUE;
        		} else {
        			modifiedTimeStamp = DateConverterFactory.getLocaleDate(dateArr[0]) + PretupsI.SPACE + (dateArr[1].trim().length() > 8 ? dateArr[1].trim().substring(0, 8) : dateArr[1].trim());
        		}	
    		}
    	} catch(Exception e) {
    		if (log.isDebugEnabled()) {
                log.debug(methodName, e);
            }
    	}
    	return modifiedTimeStamp;
    }
    
    /**
     * Returns the locale calendar instance with passed date
     * We can use this method to get the locale date and time
     * @param date
     * @return
     */
    public static Calendar getLocaleCalendar(Date date) {
    	String localeEnglish = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOCALE_ENGLISH);
    	ULocale locale = new ULocale(BTSLUtil.isNullString(localeEnglish) ? PretupsI.EMPTY : localeEnglish);
		Calendar calendar = Calendar.getInstance(locale);
		if(date != null) {
			calendar.setTime(date);			
		}
		return calendar;
    }
    /**
     * This method must be used for external gateway only
     * This method return the locale date
     * @param date
     * @return
     */
    public static String getLocaleDateTimeExtGw(Date date) {
    	final String methodName = "getLocaleDateTimeExtGw";
		Calendar calendar = getLocaleCalendar(date);
		String localeDate = PretupsI.EMPTY;
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int mins = calendar.get(Calendar.MINUTE);
		int secs = calendar.get(Calendar.SECOND);
		try {
			String extCalendarType = BTSLUtil.getTrimmedValue((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CALENDAR_TYPE));
			if(PretupsI.GREGORIAN.equalsIgnoreCase(extCalendarType)) {
				localeDate = BTSLUtil.getDateStringFromDate(date);
			} else {
				localeDate = DateConverterFactory.getLocaleDate(new Date(date.getTime()));				
			}
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, e.getMessage());
	        }
		}
		return localeDate + PretupsI.SPACE + BTSLUtil.getTwoDigit(hours) + PretupsI.COLON + BTSLUtil.getTwoDigit(mins) + 
				PretupsI.COLON + BTSLUtil.getTwoDigit(secs);
    }
    
    /**
     * This function will return date in String for Report
     * @param date
     * @param dateInFormat
     * @return
     * @throws ParseException
     */
    public static String getReportLocaleDateTime() {
    	final String methodName = "getReportLocaleDateTime";
    	String localeDate = PretupsI.EMPTY;
    	try {
    		localeDate = DateConverterFactory.getLocaleDate(new Date(), Constants.getProperty("report.systemdatetime.format"));
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
	            log.error(methodName, e.getMessage());
	        }
		}
    	return localeDate;
    }
    
    /**
     * This function will return date in String for Report
     * @param date
     * @param dateInFormat
     * @return
     * @throws ParseException
     */
    public static String getReportLocaleDate(String date, String dateInFormat) {
    	final String methodName = "getReportLocaleDate";
    	String localeDate = PretupsI.EMPTY;
    	try {
    		localeDate = DateConverterFactory.getLocaleDate(date, Constants.getProperty("report.onlydateformat"));
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
	            log.error(methodName, e.getMessage());
	        }
		}
    	return localeDate;
    }
    
    /**
     * Convert Asia/Kolkata timestamp to the passed timezone's timestamp
     * @param timstamp
     * @param timezone
     * @return
     */
    /*public static String convertTimeStamp(String timstamp, String timezone) {
    	final String methodName = "convertTimeStamp";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }
    	String modifiedTimeStamp = PretupsI.EMPTY;
    	SimpleDateFormat sdf;
    	SimpleDateFormat sdf1;
    	String[] dateArr = timstamp.split(PretupsI.SPACE);
    	if(dateArr.length < 2) {
    		sdf = new SimpleDateFormat(dateFormatCalJava);
    		sdf1 = new SimpleDateFormat("z");
    	} else {
    		sdf = new SimpleDateFormat(calenderType);
    		sdf1 = new SimpleDateFormat("HH:mm:SS z");//Add variable in PretupsI
    	}
    	try {
    		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));//Asia/Kolkata - Date passed in which timezone
    		Date date = sdf.parse(timstamp);
    		System.out.println(date);
    		sdf1.setTimeZone(TimeZone.getTimeZone(timezone));
    		if(dateArr.length < 2) {
    			modifiedTimeStamp = DateConverterFactory.getLocaleDate(dateArr[0]) + PretupsI.SPACE + 
    					PretupsI.TIME_HHMMSS_VALUE + PretupsI.SPACE + sdf1.format(date);
    		} else {
    			modifiedTimeStamp = DateConverterFactory.getLocaleDate(dateArr[0]) + PretupsI.SPACE + sdf1.format(date);
    		}
    	} catch(Exception e) {
    		if (log.isDebugEnabled()) {
                log.debug(methodName, e);
            }
    	}
    	if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.EXITED);
        }
    	return modifiedTimeStamp;
    }*/
    
    /*public static String convertTimeStamp(String timstamp, String timezone) {
    	TimeZone timeZone = TimeZone.getDefault();
//    	Calendar calendar = BTSLDateUtil.getInstance();
//    	Calendar calendar = new PersianCalendar(TimeZone.getTimeZone(timezone));
    	Calendar calendar = new PersianCalendar(
    	Calendar calendar1 = new GregorianCalendar();
    	System.out.println(calendar1.get(Calendar.HOUR_OF_DAY) + ":" + calendar1.get(Calendar.MINUTE) + ":" + calendar1.get(Calendar.SECOND));
    	System.out.println(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
    	
    	String[] dateArr = timstamp.split(PretupsI.SPACE);
    	String[] timeArr = dateArr[1].split(":");
    	
    	calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArr[0]));
     	calendar.set(Calendar.MINUTE, Integer.parseInt(timeArr[1]));
     	calendar.set(Calendar.SECOND, Integer.parseInt(timeArr[2]));
    	
    	TimeZone timeZone1 = TimeZone.getTimeZone(timezone);

    	calendar.setTimeZone(timeZone1);
    	return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
    }*/
    /**
     * Convert following formats to Date format : dd/MM/yyyy, dd-MM-yyyy, yyyy/MM/dd, yyyy-MM-dd
     * @param date
     * @return
     */
    public static Date simpleDateForamtter(String date) {
    	final String methodName = "simpleDateForamtter";
        DateFormat formatter;
        Date dateObj = null;
        String dateFormat = getDateFormat(date);
        if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat)) {
	        try {
	    		log.debug(methodName, PretupsI.LOGGER_DATE_PASSED + date + PretupsI.LOGGER_DATE_FORMAT + dateFormat);
	    		formatter = new SimpleDateFormat(dateFormat);
	    		dateObj =  formatter.parse(date);
	        } catch (Exception ex) {
	        	log.error(methodName, ex);
	        }
        }
    	return dateObj;
    }
    
    /**
     * Returns the format type of the passed date
     * We can add more regex to return more formats
     * @param date
     * @return
     */
    public static String getDateFormat(String date) {
    	String dateFormat = PretupsI.EMPTY;
    	if(BTSLUtil.isNullString(date)) {
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
    	return dateFormat;
    }
    
    /**
     * Returns the format type of the passed date
     * We can add more regex to return more formats
     * @param date
     * @return
     */
    public static String getDateTimeFormat(String date) {
    	String dateFormat = PretupsI.EMPTY;
    	if(BTSLUtil.isNullString(date)) {
        	return dateFormat;
        }
    	String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
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
        	dateFormat = systemDateFormat;
        }
    	return dateFormat;
    }
    
    /**
     * Method will return the date of the month
     * @param date
     * @param datePart : which part is required : dateOfMonth, month or year
     * @return
     */
    public static int get(String date, String datePart) {
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
    	return datePart.equalsIgnoreCase(DATE) ? Integer.parseInt(dateArr[dateOfMonth]) :
    		datePart.equalsIgnoreCase(MONTH) ? Integer.parseInt(dateArr[month]) : 
    			datePart.equalsIgnoreCase(YEAR) ? Integer.parseInt(dateArr[year]) : -1; 
    	
    }
    /**
     * This function will convert dd/MM/yyyy or yyyy/MM/dd to dd/MM/yy format
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getStringDateInDDMMYY(String date) throws ParseException {
        Date actualDate;
        String format = getDateFormat(date);
        if(BTSLUtil.isNullString(format)) {
        	return PretupsI.EMPTY;
        }
        SimpleDateFormat inFormat = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYY);
        SimpleDateFormat fromFormat = new SimpleDateFormat(format);
        actualDate = fromFormat.parse(date);
        return inFormat.format(actualDate);
    }
    /**
     * This method will return the date in yyMMdd format without separator if required
     * @param dateObj
     * @param isSeparatorRequired
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleDate(Date dateObj, Boolean isSeparatorRequired) throws ParseException {
    	final String date = BTSLDateUtil.getCalendarTypeDate(dateObj);
    	String[] dateArr = null;
    	String separator = PretupsI.EMPTY;
    	if(date.indexOf(PretupsI.FORWARD_SLASH) > -1) {
    		dateArr = date.split(PretupsI.FORWARD_SLASH);
    		separator = PretupsI.FORWARD_SLASH;
    	} else if(date.indexOf(PretupsI.HYPHEN) > -1) {
    		dateArr = date.split(PretupsI.HYPHEN);
    		separator = PretupsI.HYPHEN;
    	}
    	int arrSize = dateArr != null ? dateArr.length : 0;
    	String dateString = PretupsI.EMPTY;
    	String d;
    	for(int i = 0; i < arrSize; i++) {
    		if(dateArr[i].length() == 4) {
    			d = dateArr[i].substring(2);
    		} else {
    			d = dateArr[i];
    		}
    		dateString = BTSLUtil.isNullString(dateString) ? d : dateString + separator + d;
    	}
    	if(!isSeparatorRequired && !BTSLUtil.isNullString(date)){
    		dateString = dateString.replaceAll("/", PretupsI.EMPTY);
    	} else {
    		dateString = date;
    	}
        return dateString;
    }
    /**
     * This method will return the time in HHmm format without separator if required
     * @param date
     * @param isSeparatorRequired
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleTime(Date date, Boolean isSeparatorRequired) throws ParseException {
       
    	return getSystemLocaleTime(date, PretupsI.TIME_FORMAT_HHMM_WOSEPARATOR, isSeparatorRequired);
    }
    
    /**
     * This method will return the time in HHmm format without separator if required
     * @param date
     * @param timeInFormat
     * @param isSeparatorRequired
     * @return
     * @throws ParseException
     */
    public static String getSystemLocaleTime(Date date, String timeInFormat, Boolean isSeparatorRequired) throws ParseException {
    	final Calendar calendar = getLocaleCalendar(date);
    	String dateString = PretupsI.EMPTY;
    	if(date != null){
    		if(PretupsI.TIME_FORMAT_HHMM_WOSEPARATOR.equalsIgnoreCase(timeInFormat)) {
    			if(!isSeparatorRequired) {
    				dateString = BTSLUtil.getTwoDigit(calendar.get(Calendar.HOUR_OF_DAY)) + PretupsI.EMPTY + BTSLUtil.getTwoDigit(calendar.get(Calendar.MINUTE));	
    			} else {
    				dateString = BTSLUtil.getTwoDigit(calendar.get(Calendar.HOUR_OF_DAY)) + PretupsI.COLON + BTSLUtil.getTwoDigit(calendar.get(Calendar.MINUTE));
    			}
    		} else if(PretupsI.TIME_FORMAT_HHMMSS_WOSEPARATOR.equalsIgnoreCase(timeInFormat)) {
    			if(!isSeparatorRequired) {
    				dateString = BTSLUtil.getTwoDigit(calendar.get(Calendar.HOUR_OF_DAY)) + PretupsI.EMPTY + BTSLUtil.getTwoDigit(calendar.get(Calendar.MINUTE)) 
        					+ PretupsI.EMPTY + BTSLUtil.getTwoDigit(calendar.get(Calendar.SECOND));
    			} else {
    				dateString = BTSLUtil.getTwoDigit(calendar.get(Calendar.HOUR_OF_DAY)) + PretupsI.COLON + BTSLUtil.getTwoDigit(calendar.get(Calendar.MINUTE)) 
        					+ PretupsI.COLON + BTSLUtil.getTwoDigit(calendar.get(Calendar.SECOND));
    			}
    		}
    	}
        return dateString;
    }
    
    /**
     * This function will return next month's 1st date
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Calendar getNextMonthInitialDate(int year, int month, int day) {
    	Calendar cal = BTSLDateUtil.getCalendar(year, month, day);
    	cal.add(Calendar.MONTH, 1);
    	cal.set(Calendar.DAY_OF_MONTH, 1);
    	return cal;
    }
    
    /*private static void abc() {
    	final String methodName = "abc";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED);
        }
    	String slabGrgDate = BTSLDateUtil.getGregorianDateInString("1397/03/23");
    	String nextGrgDate = BTSLDateUtil.getGregorianDateInString("1397/03/25");
    	String slabArr[] = slabGrgDate.split("/");
    	String nextArr[] = nextGrgDate.split("/");
    	Calendar slabCal = BTSLDateUtil.getCalendar(Integer.parseInt(slabArr[2]), Integer.parseInt(slabArr[1]), Integer.parseInt(slabArr[0]));
		Calendar nextMont = BTSLDateUtil.getNextMonthInitialDate(Integer.parseInt(nextArr[2]), Integer.parseInt(nextArr[1]), Integer.parseInt(nextArr[0]));
		 
        System.out.println(slabCal.get(Calendar.YEAR) + "/" + slabCal.get(Calendar.MONTH) + "/" + slabCal.get(Calendar.DAY_OF_MONTH));
        System.out.println(nextMont.get(Calendar.YEAR) + "/" + nextMont.get(Calendar.MONTH) + "/" + nextMont.get(Calendar.DAY_OF_MONTH));
        System.out.println("compare : " + slabCal.compareTo(nextMont));
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.EXITED);
        }
    }*/
    
    /**
     * This function will check whether the passed
     * @param dateOrFormat
     * @return
     */
    public static HashMap<String, String> getMonFormatMap(String dateOrFormat) {
    	boolean isMonFormat;
    	HashMap<String, String> dateFormatMap = new HashMap<String, String>();
    	dateFormatMap.put(PretupsI.IS_MON_FORMAT_LITERAL, PretupsI.FALSE);
    	if(!BTSLUtil.isNullString(dateOrFormat)) {
    		dateFormatMap.put(PretupsI.DATE_FORMAT_LITERAL, getDateFormat(dateOrFormat));
    		isMonFormat = checkIfMonFormat(dateFormatMap.get(PretupsI.DATE_FORMAT_LITERAL));
    		if(isMonFormat) {
    			dateFormatMap.put(PretupsI.IS_MON_FORMAT_LITERAL, PretupsI.TRUE);
    		}
    	}
    	return dateFormatMap;
    }
    
    /**
     * This function will check whether the passed date or date format
     * is MMM(means date or date format contains MMM for month . Ex- dd/MMM/yyyy or yyyy/MMM/dd or dd/MMM/yy) format or not
     * @param dateOrFormatParam
     * @return
     */
    public static boolean checkIfMonFormat(String dateOrFormatParam) {
    	boolean isMonFormat = false;
    	if(!BTSLUtil.isNullString(dateOrFormatParam)) {
    		String dateOrFormat = BTSLUtil.getTrimmedValue(dateOrFormatParam);
    		if(PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(dateOrFormat)) {//add other conditions
    			isMonFormat = true;
    		}
    	}
    	return isMonFormat;
    }
    
    /**
     * This function will return the passed Mon date to non-Mon date
     * @param date
     * @param passedDateInFormat
     * @return
     */
    public static String getNonMonDate(String date, String passedDateInFormat) {
		String dateArr[];
		String finalDate = PretupsI.EMPTY;
		if(!BTSLUtil.isNullString(date)) {
			String passedDatesFormat = BTSLUtil.getTrimmedValue(passedDateInFormat);
			if(PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(passedDatesFormat) || 
					PretupsI.DATE_FORMAT_YYYYMMMDD_HYPHEN.equalsIgnoreCase(passedDatesFormat)) {
				dateArr = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
				int monthNumber = DateConverterFactory.getMonthNumeric(dateArr[1]);
				String monthNumberStr = monthNumber > -1 ? BTSLUtil.getTwoDigit(monthNumber) : dateArr[1];
				finalDate = dateArr[0] + PretupsI.FORWARD_SLASH + monthNumberStr + 
						PretupsI.FORWARD_SLASH + dateArr[2];
			} else {
				finalDate = date;
			}
		}
		return finalDate;
	}
    
    /**
     * This date will return Mon date for passed non-mon date in the format which is 
     * set in DATE_FORMAT_CAL_JAVA in Constants.props
     * @param date
     * @return
     */
    public static String getMonDate(String date) {
		String finalDate;
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		String monDateOnUi = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MON_DATE_ON_UI);
		String format = BTSLUtil.getTrimmedValue(dateFormatCalJava);
		if(PretupsI.YES.equalsIgnoreCase(monDateOnUi)) {
			finalDate = getMonDate(date, format);
		} else {
			finalDate = date;
		}
		return finalDate;
	}
    
    /**
     * This function will return Mon date for passed non-mon date in the passed format 
     * @param date
     * @param dateInFormat
     * @return
     */
    public static String getMonDate(String date, String dateInFormat) {
		String dateArr[];
		String finalDate = PretupsI.EMPTY;
		if(!BTSLUtil.isNullString(date)) {
			String dateFormat = BTSLUtil.getTrimmedValue(dateInFormat);
			if(PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(dateFormat)) {//add more conditions for remaining formats
				dateArr = date.split(PretupsI.FORWARD_SLASH);
				finalDate = dateArr[0] + PretupsI.FORWARD_SLASH + DateConverterFactory.getMonthName(Integer.parseInt(dateArr[1])) + 
						PretupsI.FORWARD_SLASH + dateArr[2];
			}
		} else {
			finalDate = date;
		}
		return finalDate;
	}
    
    /**
     * This function will return finally processed date
     * @param date
     * @return
     */
    public static String getFinalDate(String date) {
		String finalDate;
		HashMap<String, String> dateFormatMap = getMonFormatMap(date);
    	if(!BTSLUtil.isNullString(date) && PretupsI.TRUE.equalsIgnoreCase(dateFormatMap.get(PretupsI.IS_MON_FORMAT_LITERAL))) {
    		finalDate = getNonMonDate(date, dateFormatMap.get(PretupsI.DATE_FORMAT_LITERAL));
    	} else {
    		finalDate = date;
    	}
		return finalDate;
	}
    
    /**
     * This method will return the date object from the passed Id in yymmdd
     * @param transId
     * @return date
     */
    public static Date getDateFromId(String transId) throws ParseException {
    	Date date = new Date();
    	String dateString;
    	String dateStr;
    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
    	if(PretupsI.GREGORIAN.equalsIgnoreCase(calenderType)) {
    		dateStr = !BTSLUtil.isNullString(transId) ? transId.substring(4, 6) + PretupsI.FORWARD_SLASH
	    			+ transId.substring(2, 4) + PretupsI.FORWARD_SLASH + transId.substring(0, 2) : PretupsI.EMPTY;
    		date = BTSLUtil.getDateFromDateString(dateStr, PretupsI.DATE_FORMAT_DDMMYY);
    	} else if(PretupsI.PERSIAN.equalsIgnoreCase(calenderType)) {
    		dateStr = !BTSLUtil.isNullString(transId) ? transId.substring(0, 2) + PretupsI.FORWARD_SLASH
	    			+ transId.substring(2, 4) + PretupsI.FORWARD_SLASH + transId.substring(4, 6) : PretupsI.EMPTY;
			String appendInYear = Integer.parseInt(transId.substring(0, 2)) >= 90 ? "13" : "14";//implemented for 93 years. Current year is 1397 in persian
			dateString = BTSLDateUtil.getGregorianDateInString(appendInYear+dateStr);
			date = BTSLUtil.getDateFromDateString(dateString, BTSLDateUtil.getDateFormat(dateString));
    	}
        return date;
    }
    
    /**
     * This function will compare fromTime to compareToTime
     * If fromTime is greater than or equal to compareToTime then it will return false, else true
     * @param fromTime
     * @param compareToTime
     * @return
     */
    public static boolean isGreaterOrEqualTime(String fromTime, String compareToTime) {
    	
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date d1;
        Date d2;
        long elapsed = 0;
		try {
			d1 = sdf.parse(fromTime);
			d2 = sdf.parse(compareToTime);
			elapsed = d1.getTime() - d2.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(elapsed >= 0) {
			return true;
		}
		return false;
    }
    /**
     * This function will return time from date object/ or passed format
     * @param date
     * @param timeFormat
     * @return
     */
    public static String getTimeFromDate(Date date, String timeFormat) {
    	
    	if(BTSLUtil.isNullString(timeFormat)) {
    		timeFormat = "HH:mm";
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        return sdf.format(date);
    }
    
    public static String getSystemLocaleDateInFormat(String date, String dateInFormat) throws ParseException {
    	final String methodName = "getSystemLocaleDateInFormat";
    	String localeDate = PretupsI.EMPTY;
    	try {
    		localeDate = DateConverterFactory.getLocaleDate(date, dateInFormat);
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
	            log.error(methodName, e.getMessage());
	        }
			throw e;
		}
    	return localeDate;
    }
    
    public static String getDateInFormat(String date, String endDateFormat) throws ParseException {
    	final String methodName = "getSystemLocaleDateInFormat";
    	String parsedDate = PretupsI.EMPTY;
    	try {
    		if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(endDateFormat)) {
	    		String initDateFormat = getDateFormat(date);
	    		Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
	            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
	            parsedDate = formatter.format(initDate);
    		}
		} catch (ParseException e) {
			if (log.isDebugEnabled()) {
	            log.error(methodName, e.getMessage());
	        }
			throw e;
		}
        return parsedDate;
    }

    
public static boolean checkDate(RequestVO p_requestVO,String currFrom ,String currTo) throws ParseException, BTSLBaseException{
		String tagName = "fromDate";
		String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
		String format = systemDateFormat;
		String p_errorKey = PretupsErrorCodesI.EXTSYS_DATE_INVALID_FORMAT;
		if (BTSLUtil.isNullString(format)) {
            format = "dd/MM/yy";
        }
		XMLTagValueValidation.validateFromToDate(currFrom, true, tagName);
		if (format.length() != currFrom.length()) {
       	 _errorMsg[0] = tagName;
            throw new BTSLBaseException("XMLTagValueValidation", "dateFormat", p_errorKey, _errorMsg);
       	}
		tagName = "toDate";
		XMLTagValueValidation.validateFromToDate(currTo,true,tagName);	
        if(format.length() != currTo.length()){
        	 _errorMsg[0] = tagName;
             throw new BTSLBaseException("XMLTagValueValidation", "dateFormat", p_errorKey, _errorMsg);
        }
		Date date = new Date();
		Date fromDate = BTSLUtil.getDateFromDateString(currFrom);
		Date toDate = BTSLUtil.getDateFromDateString(currTo);
		if(fromDate.compareTo(date)>0){
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
			return true;
			
		}
		else if(toDate.compareTo(date)>0){
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
			return true;
		}
		else if(fromDate.compareTo(toDate)>0){
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setMessageCode(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
			return true;
		}
		return false;
	}
/**
 * To convert time from seconds into days,hours,minutes and seconds
 * @param seconds
 * @return
 */
 public static String getTimeFromSeconds(int seconds){
	 StringBuilder convertedTime = new StringBuilder();
	 int days = (seconds / (24*3600));
	 seconds = seconds % (24*3600);
	 int hr = seconds/3600;
	 seconds = seconds%3600;
	 int min = seconds/60;
	 seconds=seconds%60;
	 if(days > 0)
		 convertedTime.append(days).append(" days ");
	 if(hr>0)
		 convertedTime.append(hr).append(" hours ");
	 if(min > 0)
		 convertedTime.append(min).append(" minutes ");
	 if(seconds > 0)
	 convertedTime.append(seconds).append(" seconds");
	 return convertedTime.toString();
 }
 

public static void validateDate(String currFrom ,String currTo) throws ParseException, BTSLBaseException{
		String tagName = "fromDate";
		String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
		String format = systemDateFormat;
		String p_errorKey = PretupsErrorCodesI.EXTSYS_DATE_INVALID_FORMAT;
		if (BTSLUtil.isNullString(format)) {
         format = "dd/MM/yy";
     }
		XMLTagValueValidation.validateFromToDate(currFrom, true, tagName);
		if (format.length() != currFrom.length()) {
    	 _errorMsg[0] = tagName;
         throw new BTSLBaseException("XMLTagValueValidation", "dateFormat", p_errorKey, _errorMsg);
    	}
		tagName = "toDate";
		XMLTagValueValidation.validateFromToDate(currTo,true,tagName);	
     if(format.length() != currTo.length()){
     	 _errorMsg[0] = tagName;
          throw new BTSLBaseException("XMLTagValueValidation", "dateFormat", p_errorKey, _errorMsg);
     }
		Date date = new Date();
		Date fromDate = BTSLUtil.getDateFromDateString(currFrom);
		Date toDate = BTSLUtil.getDateFromDateString(currTo);
		if(fromDate.compareTo(date)>0){
			throw new BTSLBaseException(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
		}
		else if(toDate.compareTo(date)>0){
			throw new BTSLBaseException(PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
		}
		else if(fromDate.compareTo(toDate)>0){
			throw new BTSLBaseException(PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
		}
	}
}