package com.utils;

import java.text.ParseException;

import com.classes.CONSTANT;
import com.commons.PretupsI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;

/**
 * DateConverterFactory will provide the methods to convert any Date to Gregorian(ex- Persian to Gregorian)
 * Methods to get the Locale date for any calendar
 * As of now, only Persian Calendar is implemented
 * @author tejeshvi.roy
 *
 */
public class DateConverterFactory {
	
	public static final String PERSIAN = "persian";
	private DateConverterFactory() {}
	/**
	 * Get the locale date for any Calendar
	 * As of now only implemented for Persian Calendar
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	public static String getLocaleDate(String date) throws ParseException {
		final String methodName = "getLocaleDate";
		String dateStr = PretupsI.EMPTY;
		Log.debug("Entered " + methodName + "(" + date + ")");
		
		String CALENDAR_TYPE = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CALENDAR_TYPE);
		DateConverter dateConverter;
		if(!BTSLDateUtil.isNullString(date)) {
			if(PretupsI.PERSIAN.equalsIgnoreCase(CALENDAR_TYPE)) {
				dateConverter = new PersianDateConverterImpl();
				dateStr = dateConverter.getLocaleDateFromDate(date);
			} else {
				dateStr = BTSLDateUtil.getDateStringInFormat(date, SystemPreferences.DATE_FORMAT_CAL_JAVA);
			}
		}

		Log.debug("Exiting " + methodName + " with date=" + dateStr);
		return dateStr;
	}
	
	/**
	 * This function will return the month in number for the passed month name
	 * @param month
	 * @return
	 * @throws ParseException
	 */
	public static int getMonthNumeric(String month) {
		final String methodName = "getMonthNumeric";
		Log.debug("Entered " + methodName + "(" + month + ")");

		int monthNumber = 0;
		DateConverter dateConverter;
		String CALENDAR_TYPE = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CALENDAR_TYPE);
		if(!PretupsI.GREGORIAN.equalsIgnoreCase(CALENDAR_TYPE)) {
			dateConverter = new PersianDateConverterImpl();
			monthNumber = dateConverter.getMonthNumeric(month);
		}

		Log.debug("Exiting " + methodName + " with monthNumber=" + monthNumber);
		return monthNumber;
	}
	
	/**
	 * Convert any Calendar date to Gregorian date
	 * As of now only implemented for Persian Calendar
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getGregorianDateInString(String date) {
		final String methodName = "getGregorianDateInString";
		String dateStr = PretupsI.EMPTY;
		Log.debug("Entered " + methodName + "(" + date + ")");
		
		DateConverter dateConverter;
		if(!BTSLUtil.isNullString(date)) {
			String CALENDAR_TYPE = DBHandler.AccessHandler.getSystemPreference(CONSTANT.CALENDAR_TYPE);
			if(PretupsI.PERSIAN.equalsIgnoreCase(CALENDAR_TYPE)) {
				dateConverter = new PersianDateConverterImpl();
				dateStr = dateConverter.getGregorianDateInString(date);
			} else {
				dateStr = date;
			}
		}

		Log.debug("Exiting " + methodName + " with dateValue=" + dateStr);
		return dateStr;
	}
	
	/*
	 * Below method is for jar - Need to change the implementation for the client specific
	 * 
	 */
	/*public static String getLocaleDate(String date) throws ParseException {
		String dateStr = "";
		DateConverter dateConverter = new PersianDateConverterImpl();
		dateStr = dateConverter.getLocaleDateFromDate(date);
		return dateStr;
	}*/
}
