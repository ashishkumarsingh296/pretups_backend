package com.btsl.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.ibm.icu.text.SimpleDateFormat;
/**
 * PersianDateConverterImpl is used to convert Persian date to Gregorian date
 * and vice-versa
 * @author tejeshvi.roy
 *
 */
public class PersianDateConverterImpl implements DateConverter{
	
	private static final Log log = LogFactory.getLog(PersianDateConverterImpl.class.getName());
	
	/**
	 * This function will return Persian date when date is passed in dd/MM/yy or dd/MM/yyyy formats
	 * If dd/MM/yy used, then it will behave more than 2000. Ex- If we pass 06/03/90 then it will behave as 06/03/2090 not as 06/03/1990
	 * Will support 01/01/1900 onwards
	 * Return persian date in yyyy/MM/dd format
	 * @param date
	 * @return
	 */
	@Override
	public String getLocaleDateFromDate(String date) {
		String localeDate;
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		localeDate = getLocaleDate(date, dateFormatCalJava);
		return localeDate;
	}
	
	@Override
	public String getLocaleDate(String date, String dateInFormat) {
		String finalDate;
		if(date == null || date.isEmpty()) {
			return PretupsI.EMPTY;
		} 
		PersianCalendar pc = new PersianCalendar();
		String tempDate;
		tempDate = date.replace(PretupsI.HYPHEN, PretupsI.FORWARD_SLASH);
		String[] dateArr = tempDate.split(PretupsI.FORWARD_SLASH);
		int year = BTSLDateUtil.get(tempDate, BTSLDateUtil.YEAR);
		int add = year < 100 ? 2000 : year < 1900 && year >= 100 ? 1900 : 0;//This to support both (yyyy)2018 and (yy)18 format
		String dateFormat = BTSLUtil.getTrimmedValue(BTSLDateUtil.getDateFormat(tempDate));
		if(PretupsI.DATE_FORMAT_DDMMYY.equalsIgnoreCase(dateFormat) || 
				PretupsI.DATE_FORMAT_DDMMYYYY.equalsIgnoreCase(dateFormat) ||
				PretupsI.DATE_FORMAT_DDMMYY_HYPHEN.equalsIgnoreCase(dateFormat) ||
				PretupsI.DATE_FORMAT_DDMMYYYY_HYPHEN.equalsIgnoreCase(dateFormat)) {
			pc.setGregorianDate(add + year, Integer.parseInt(dateArr[1]), 
					Integer.parseInt(dateArr[0]));
		} else if(PretupsI.DATE_FORMAT_YYYYMMDD.equalsIgnoreCase(dateFormat) ||
				PretupsI.DATE_FORMAT_YYYYMMDD_HYPHEN.equalsIgnoreCase(dateFormat)) {
			pc.setGregorianDate(add + year, Integer.parseInt(dateArr[1]), 
					Integer.parseInt(dateArr[2]));
		}
		String monDateOnUi = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MON_DATE_ON_UI);
		String isMonDateOnUi = BTSLUtil.getTrimmedValue(monDateOnUi);
		if(PretupsI.YES.equalsIgnoreCase(isMonDateOnUi)) {
			finalDate = BTSLDateUtil.getMonDate(pc.getIranianDate(dateInFormat), dateInFormat);
		} else {
			finalDate = pc.getIranianDate(dateInFormat);
		}
		return finalDate;
	}
	
	@Override
	public String getLocaleDate(Date date) throws ParseException {
		String localeDate;
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		localeDate = getLocaleDate(date, dateFormatCalJava);
        return localeDate;
	}
	/**
	 * This function will return date in String in the passed dateInFormat
	 * This function can also return date with time format. For ex - dd/MM/yy HH or dd/MM/yy HH:mm
	 * @param date
	 * @param dateInFormat
	 */
	@Override
	public String getLocaleDate(Date date, String dateInFormat) throws ParseException {
		String finalDate;
		String[] dateFormatArr = null; 
		if(!BTSLUtil.isNullString(dateInFormat)) {
			dateFormatArr = dateInFormat.split(PretupsI.SPACE);
		}
		PersianCalendar pc = new PersianCalendar();
		if(date != null) {
			int year = date.getYear();
			int add = year < 100 ? 2000 : year < 1900 && year >= 100 ? 1900 : 0;//This to support both (yyyy)2018 and (yy)18 format
	        pc.setGregorianDate(add+year, date.getMonth() + 1,date.getDate());
		}
		String monDateOnUi = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_MON_DATE_ON_UI);
		String isMonDateOnUi = BTSLUtil.getTrimmedValue(monDateOnUi);
		if(dateFormatArr.length > 1) {
			if(PretupsI.YES.equalsIgnoreCase(isMonDateOnUi)) {
				finalDate = BTSLDateUtil.getMonDate(pc.getIranianDate(dateFormatArr[0]), dateFormatArr[0]) + 
						PretupsI.SPACE + getTime(date, dateFormatArr[1]);
			} else {
				finalDate = pc.getIranianDate(dateFormatArr[0]) + PretupsI.SPACE + getTime(date, dateFormatArr[1]);
			}
		} else {
			if(PretupsI.YES.equalsIgnoreCase(isMonDateOnUi)) {
				finalDate = BTSLDateUtil.getMonDate(pc.getIranianDate(dateInFormat), dateInFormat);
			} else {
				finalDate = pc.getIranianDate(dateInFormat);
			}
		}
        return finalDate;
	}
	/**
	 * This method will return the Gregorian date
	 * @param - Sting date - Persian date which needs to convert to Gregorian date
	 * @param - String format - format in which date has been passed
	 */
	@Override
	public String getGregorianDateInString(String date) {//add Remaining formats as well
		String dateStr = PretupsI.EMPTY;
		PersianCalendar pc = new PersianCalendar();
		String dateFormat = BTSLUtil.getTrimmedValue(BTSLDateUtil.getDateFormat(date));
		if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat)) {
			if(PretupsI.DATE_FORMAT_YYYYMMDD.equalsIgnoreCase(dateFormat) || PretupsI.DATE_FORMAT_YYYYMMDD_HYPHEN.equalsIgnoreCase(dateFormat)) {
				String[] part = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
				pc.setIranianDate(Integer.parseInt(part[0]), Integer.parseInt(part[1]),
						Integer.parseInt(part[2]));
				dateStr = pc.getGregorianDate();
			} else if(PretupsI.DATE_FORMAT_DDMMYYYY.equalsIgnoreCase(dateFormat) || PretupsI.DATE_FORMAT_DDMMYYYY_HYPHEN.equalsIgnoreCase(dateFormat)) {
				String[] part = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
				pc.setIranianDate(Integer.parseInt(part[2]), Integer.parseInt(part[1]),
						Integer.parseInt(part[0]));
				dateStr = pc.getGregorianDate();
			} else if(PretupsI.TIMESTAMP_YYYYMMDDHHMMSS.equalsIgnoreCase(dateFormat) || PretupsI.TIMESTAMP_YYYYMMDDHHMMSS_HYPHEN.equalsIgnoreCase(dateFormat)) {//Correct this part[1]
				String[] part = date.split(PretupsI.SPACE);
				String[] part1 = part[0].contains(PretupsI.FORWARD_SLASH) ? part[0].split(PretupsI.FORWARD_SLASH) : part[0].split(PretupsI.HYPHEN);
				pc.setIranianDate(Integer.parseInt(part1[0]), Integer.parseInt(part1[1]),
						Integer.parseInt(part1[2]));
				dateStr = pc.getGregorianDate() + " " + part[1];
			} else if(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS.equalsIgnoreCase(dateFormat) || PretupsI.TIMESTAMP_DDMMYYYYHHMMSS_HYPHEN.equalsIgnoreCase(dateFormat)) {//Correct this part[1]
				String[] part = date.split(PretupsI.SPACE);
				String[] part1 = part[0].contains(PretupsI.FORWARD_SLASH) ? part[0].split(PretupsI.FORWARD_SLASH) : part[0].split(PretupsI.HYPHEN);
				pc.setIranianDate(Integer.parseInt(part1[2]), Integer.parseInt(part1[1]),
						Integer.parseInt(part1[0]));
				dateStr = pc.getGregorianDate() + " " + part[1];
			} else if(PretupsI.DATE_FORMAT_DDMMYY.equalsIgnoreCase(dateFormat) || PretupsI.DATE_FORMAT_DDMMYY_HYPHEN.equalsIgnoreCase(dateFormat)) {
				String[] part = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH) : date.split(PretupsI.HYPHEN);
				String appendInYear = Integer.parseInt(part[2]) >= 97 ? "13" : "14";//implemented for 100 years. Current year is 1397 in persian
				pc.setIranianDate(Integer.parseInt(appendInYear + part[2]), Integer.parseInt(part[1]),
						Integer.parseInt(part[0]));
				dateStr = pc.getGregorianDate();
			}
		}
		return dateStr;
	}
	
	/**
	 * return the month in Words
	 * @param index
	 * @return
	 */
	@Override
	public String getMonthName(int index) {
		return PersianCalendar.monthNamesShort[index - 1];
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
	
	private static String getTime(Date date, String timeInFormat) {
		final SimpleDateFormat sdf = new SimpleDateFormat(timeInFormat);
		sdf.setLenient(false); // this is required else it will convert
		return sdf.format(date);
	}
}
