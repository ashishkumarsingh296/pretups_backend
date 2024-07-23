package com.btsl.util;

import java.text.ParseException;
import java.util.Date;

/**
 * Interface for Date conversion
 * @author tejeshvi.roy
 *
 */
public interface DateConverter{
	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	String getGregorianDateInString(String date);
	/**
	 * 
	 * @param date
	 * @return
	 */
	String getLocaleDateFromDate(String date);
	/**
	 * 
	 * @param date
	 * @param dateInFormat
	 * @return
	 * @throws ParseException
	 */
	String getLocaleDate(String date, String dateInFormat) throws ParseException;
	/**
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	String getLocaleDate(Date date) throws ParseException;
	/**
	 * 
	 * @param date
	 * @param dateInFormat
	 * @return
	 */
	String getLocaleDate(Date date, String dateInFormat) throws ParseException;
	/**
	 * 
	 * @param index
	 * @return
	 */
	String getMonthName(int index);
	/**
	 * 
	 * @param month
	 * @return
	 */
	int getMonthNumeric(String month);
}
