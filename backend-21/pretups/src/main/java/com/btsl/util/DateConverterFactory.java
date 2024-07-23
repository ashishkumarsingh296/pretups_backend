package com.btsl.util;

import java.text.ParseException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

/**
 * DateConverterFactory will provide the methods to convert any Date to Gregorian(ex- Persian to Gregorian)
 * Methods to get the Locale date for any calendar
 * As of now, Only Persian Calendar is implemented
 * @author tejeshvi.roy
 *
 */
public class DateConverterFactory {
	
	private static final Log log = LogFactory.getLog(DateConverterFactory.class.getName());
	
	private DateConverterFactory() {}
	/**
	 * Convert any Calendar date to Gregorian date
	 * As of now only implemented for Persian Calendar
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getGregorianDateInString(String date) {
		String dateStr = PretupsI.EMPTY;
		DateConverter dateConverter;
		String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
		if(!BTSLUtil.isNullString(date)) {
			String calendarType = BTSLUtil.getTrimmedValue(calenderType);
			if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)) {
				dateConverter = new PersianDateConverterImpl();
				dateStr = dateConverter.getGregorianDateInString(date);
			} else if(PretupsI.NEPALI.equalsIgnoreCase(calendarType)) {
				dateConverter = new NepaliDateConverterImpl();
				dateStr = dateConverter.getGregorianDateInString(date);
			} else {
				dateStr = date;
			}
		}
		return dateStr;
	}
	
	/**
	 * Get the locale date for any Calendar
	 * As of now only implemented for Persian Calendar
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	public static String getLocaleDate(String date) throws ParseException {
		String dateStr = PretupsI.EMPTY;
		DateConverter dateConverter;
		String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		if(!BTSLUtil.isNullString(date)) {
			String calendarType = BTSLUtil.getTrimmedValue(calenderType);
			if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)) {
				dateConverter = new PersianDateConverterImpl();
				dateStr = dateConverter.getLocaleDateFromDate(date);
			} else if(PretupsI.NEPALI.equalsIgnoreCase(calendarType)) {
				dateConverter = new NepaliDateConverterImpl();
				dateStr = dateConverter.getLocaleDateFromDate(date);
			} else {
				dateStr = BTSLUtil.getDateStringInFormat(date, dateFormatCalJava);
			}
		}
		return dateStr;
	}
	
	/**
	 * This function will return the date in String in passed format
	 * @param date
	 * @param dateInFormat
	 * @return
	 * @throws ParseException
	 */
	public static String getLocaleDate(String date, String dateInFormat) throws ParseException {
		String dateStr = PretupsI.EMPTY;
		DateConverter dateConverter;
		String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
		if(!BTSLUtil.isNullString(date)) {
			String dateFormat = BTSLUtil.getTrimmedValue(dateInFormat);
			String calendarType = BTSLUtil.getTrimmedValue(calenderType);
			if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)) {
				dateConverter = new PersianDateConverterImpl();
				dateStr = dateConverter.getLocaleDate(date, dateFormat);
			} else if(PretupsI.NEPALI.equalsIgnoreCase(calendarType)) {
				dateConverter = new NepaliDateConverterImpl();
				dateStr = dateConverter.getLocaleDate(date, dateFormat);
			} else {
				dateStr = BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(date, dateFormat));
			}			
		}
		//Rest of countries
		return dateStr;
	}
	
	/**
	 * Get the locale date for any Calendar. This will convert the passed date to the Locale date on the basis of 
	 * properties set in System Preferences/ properties(as of now mentioned in PretupsI.java)
	 * As of now only implemented for Persian Calendar
	 * @param date
	 * @return
	 */
	public static String getLocaleDate(Date date) throws ParseException {
		String dateStr = PretupsI.EMPTY;
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		if(date != null) {
			dateStr = getLocaleDate(date, dateFormatCalJava);
		}
		//Rest of countries
		return dateStr;
	}
	/**
	 * This function will return the date in String in passed format
	 * @param date
	 * @param dateInFormat
	 * @return
	 * @throws ParseException
	 */
	public static String getLocaleDate(Date date, String dateInFormat) throws ParseException {
		String dateStr = PretupsI.EMPTY;
		DateConverter dateConverter;
		String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
		if(date != null) {
			String calendarType = BTSLUtil.getTrimmedValue(calenderType);
			if(PretupsI.PERSIAN.equalsIgnoreCase(calendarType)) {
				dateConverter = new PersianDateConverterImpl();
				dateStr = dateConverter.getLocaleDate(date, dateInFormat);
			} else if(PretupsI.NEPALI.equalsIgnoreCase(calendarType)) {
				dateConverter = new NepaliDateConverterImpl();
				dateStr = dateConverter.getLocaleDate(date, dateInFormat);
			} else {
				dateStr = BTSLUtil.getDateStringFromDate(date, dateInFormat);
			}			
		}
		//Rest of countries
		return dateStr;
	}
	
	/**
	 * This function will return month name of the passed month in number
	 * @param month
	 * @return
	 * @throws ParseException
	 */
	public static String getMonthName(int month) {
		String monthName = PretupsI.EMPTY;
		DateConverter dateConverter;
		String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
		String calendarType = BTSLUtil.getTrimmedValue(calenderType);
		if(!PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
			dateConverter = new PersianDateConverterImpl();
			monthName = dateConverter.getMonthName(month);
		}
		return monthName;
	}
	
	/**
	 * This function will return the month in number for the passed month name
	 * @param month
	 * @return
	 * @throws ParseException
	 */
	public static int getMonthNumeric(String month) {
		int monthNumber = 0;
		DateConverter dateConverter;
		String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
		String calendarType = BTSLUtil.getTrimmedValue(calenderType);
		if(!PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
			dateConverter = new PersianDateConverterImpl();
			monthNumber = dateConverter.getMonthNumeric(month);
		}//check for Gregorian as well
		return monthNumber;
	}
}
