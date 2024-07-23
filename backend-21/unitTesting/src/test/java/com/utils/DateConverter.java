package com.utils;


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
	 * @param month
	 * @return
	 */
	int getMonthNumeric(String month);
}
