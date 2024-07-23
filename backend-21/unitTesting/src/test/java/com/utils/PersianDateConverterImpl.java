package com.utils;

import java.util.ArrayList;

import com.commons.PretupsI;
import com.pretupsControllers.BTSLUtil;

/**
 * PersianDateConverterImpl is used to convert Persian date to Gregorian date
 * and vice-versa
 * @author tejeshvi.roy
 *
 */
public class PersianDateConverterImpl implements DateConverter{
	public static final String DATE_FORMAT_DDMMYY = "dd/MM/yy";
	public static final String DATE_FORMAT_DDMMYYYY = "dd/MM/yyyy";
	public static final String DATE_FORMAT_YYYYMMDD = "yyyy/MM/dd";
	/**
	 * This function will return Persian date when date is passed in dd/MM/yy or dd/MM/yyyy formats
	 * If dd/MM/yy used, then it will behave more than 2000. Ex- If we pass 06/03/90 then it will behave as 06/03/2090 not as 06/03/1990
	 * Will support 01/01/1900 onwards
	 * Return persian date in yyyy/MM/dd format
	 * @param date
	 * @return
	 */
	public String getLocaleDateFromDate(String date) {
		if(date == null || date == "") {
			return "";
		}
		PersianCalendar pc = new PersianCalendar();
		String[] dateArr = date.split("/");
		int year = BTSLDateUtil.get(date, PretupsI.YEAR);
		int add = year < 100 ? 2000 : year < 1900 && year >= 100 ? 1900 : 0;//This to support both (yyyy)2018 and (yy)18 format
		if(DATE_FORMAT_DDMMYY.equalsIgnoreCase(BTSLDateUtil.getDateFormat(date)) || 
				DATE_FORMAT_DDMMYYYY.equalsIgnoreCase(BTSLDateUtil.getDateFormat(date))) {
			pc.setGregorianDate(add + year, Integer.parseInt(dateArr[1]), 
					Integer.parseInt(dateArr[0]));
		} else if(DATE_FORMAT_YYYYMMDD.equalsIgnoreCase(BTSLDateUtil.getDateFormat(date))) {
			pc.setGregorianDate(add + year, Integer.parseInt(dateArr[1]), 
					Integer.parseInt(dateArr[2]));
		}
		return pc.getIranianDate("yyyy/MM/dd");
	}
	
	/**
	 * Return the month in number
	 * @param month
	 * @return
	 */
	@Override
	public int getMonthNumeric(String month) {
		ArrayList<String> monthNames = new ArrayList<String>();
		int arraySize = PersianCalendar.monthNamesShort.length;
		for(int i=0; i<arraySize; i++) {
			monthNames.add(PersianCalendar.monthNamesShort[i]);
		}
		return monthNames.indexOf(month) > -1 ? monthNames.indexOf(month) + 1 : -1;
	}
	
	/**
	 * This method will return the Gregorian date
	 * @param - Sting date - Persian date which needs to convert to Gregorian date
	 * @param - String format - format in which date has been passed
	 */
	@Override
	public String getGregorianDateInString(String date) {//add Remaining formats as well
		final String methodName = "getGregorianDateInString";
		String dateStr = PretupsI.EMPTY;
		Log.debug("Entered " + methodName + "(" + date + ")");

		PersianCalendar pc = new PersianCalendar();
		String dateFormat = BTSLDateUtil.getDateFormat(date);
		if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				(PretupsI.DATE_FORMAT_YYYYMMDD.equalsIgnoreCase(dateFormat) || PretupsI.DATE_FORMAT_YYYYMMDD_HYPHEN.equalsIgnoreCase(dateFormat))) {
			String[] part = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
			pc.setIranianDate(Integer.parseInt(part[0]), Integer.parseInt(part[1]),
					Integer.parseInt(part[2]));
			dateStr = pc.getGregorianDate();
		} else if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				(PretupsI.DATE_FORMAT_DDMMYYYY.equalsIgnoreCase(dateFormat) || PretupsI.DATE_FORMAT_DDMMYYYY_HYPHEN.equalsIgnoreCase(dateFormat))) {
			String[] part = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
			pc.setIranianDate(Integer.parseInt(part[2]), Integer.parseInt(part[1]),
					Integer.parseInt(part[0]));
			dateStr = pc.getGregorianDate();
		} else if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				(PretupsI.TIMESTAMP_YYYYMMDDHHMMSS.equalsIgnoreCase(dateFormat) || PretupsI.TIMESTAMP_YYYYMMDDHHMMSS_HYPHEN.equalsIgnoreCase(dateFormat))) {//Correct this part[1]
			String[] part = date.split(PretupsI.SPACE);
			String[] part1 = part[0].contains(PretupsI.FORWARD_SLASH) ? part[0].split(PretupsI.FORWARD_SLASH) : part[0].split(PretupsI.HYPHEN);
			pc.setIranianDate(Integer.parseInt(part1[0]), Integer.parseInt(part1[1]),
					Integer.parseInt(part1[2]));
			dateStr = pc.getGregorianDate() + " " + part[1];
		} else if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS.equalsIgnoreCase(dateFormat) || PretupsI.TIMESTAMP_DDMMYYYYHHMMSS_HYPHEN.equalsIgnoreCase(dateFormat))) {//Correct this part[1]
			String[] part = date.split(PretupsI.SPACE);
			String[] part1 = part[0].contains(PretupsI.FORWARD_SLASH) ? part[0].split(PretupsI.FORWARD_SLASH) : part[0].split(PretupsI.HYPHEN);
			pc.setIranianDate(Integer.parseInt(part1[2]), Integer.parseInt(part1[1]),
					Integer.parseInt(part1[0]));
			dateStr = pc.getGregorianDate() + " " + part[1];
		} else if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				(PretupsI.DATE_FORMAT_DDMMYY.equalsIgnoreCase(dateFormat) || PretupsI.DATE_FORMAT_DDMMYY_HYPHEN.equalsIgnoreCase(dateFormat))) {
			String[] part = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
			String appendInYear = Integer.parseInt(part[2]) >= 97 ? "13" : "14";//implemented for 100 years. Current year is 1397 in persian
			pc.setIranianDate(Integer.parseInt(appendInYear + part[2]), Integer.parseInt(part[1]),
					Integer.parseInt(part[0]));
			dateStr = pc.getGregorianDate();
		}

		Log.debug("Exiting " + methodName + " with date=" + dateStr);
		return dateStr;
	}
}
