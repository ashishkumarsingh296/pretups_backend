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
public class NepaliDateConverterImpl implements DateConverter{
	
	private static final Log log = LogFactory.getLog(NepaliDateConverterImpl.class.getName());
	private static final String METHOD_GET_LOCALE_DATE = "getLocaleDate";
	
	/**
	 * This function will return Nepali date when date is passed in dd/MM/yy or dd/MM/yyyy formats
	 * If dd/MM/yy used, then it will behave more than 2000. Ex- If we pass 06/03/90 then it will behave as 06/03/2090 not as 06/03/1990
	 * Will support 01/01/1900 onwards
	 * Return persian date in yyyy/MM/dd format
	 * @param date
	 * @return
	 */
	@Override
	public String getLocaleDateFromDate(String date) {
		final String methodName = METHOD_GET_LOCALE_DATE;
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED + PretupsI.LOGGER_DATE_PASSED + date);
        }
		String localeDate;
		localeDate = getLocaleDate(date, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)));
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED + PretupsI.LOGGER_DATE_RETURNED + localeDate);
        }
		return localeDate;
	}
	
	@Override
	public String getLocaleDate(String date, String dateInFormat) {
		final String methodName = METHOD_GET_LOCALE_DATE;
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED + PretupsI.LOGGER_DATE_PASSED + date + PretupsI.LOGGER_DATE_FORMAT_PASSED + dateInFormat);
        }
		String finalDate = PretupsI.EMPTY;
		if(date == null || date.isEmpty()) {
			return PretupsI.EMPTY;
		}
		NepaliCalendar nc = new NepaliCalendar();
		String[] dateArr = date.split(PretupsI.FORWARD_SLASH);
		int year = BTSLDateUtil.get(date, BTSLDateUtil.YEAR);
		int add = year < 100 ? 2000 : year < 1900 && year >= 100 ? 1900 : 0;//This to support both (yyyy)2018 and (yy)18 format
		if(PretupsI.DATE_FORMAT_DDMMYY.equalsIgnoreCase(BTSLDateUtil.getDateFormat(date)) || 
				PretupsI.DATE_FORMAT_DDMMYYYY.equalsIgnoreCase(BTSLDateUtil.getDateFormat(date))) {
			
		} else if(PretupsI.DATE_FORMAT_YYYYMMDD.equalsIgnoreCase(BTSLDateUtil.getDateFormat(date))) {
			
		}
		try {
			finalDate = nc.convertAdToBs(date, dateInFormat);//currently working only for dd/MM/yyyy. To run this convert other format to dd/MM/yyyy
		} catch (ParseException e) {
			log.error(methodName, "message = " + e.getMessage() + " cause = " + e.getCause());
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED + PretupsI.LOGGER_DATE_RETURNED + finalDate);
        }
		return finalDate;
	}
	
	@Override
	public String getLocaleDate(Date date) throws ParseException {
		final String methodName = METHOD_GET_LOCALE_DATE;
		String localeDate;
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED + PretupsI.LOGGER_DATE_PASSED + date);
        }
		localeDate = getLocaleDate(date, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)));
        if (log.isDebugEnabled()) {
        	log.debug(methodName, PretupsI.EXITED + PretupsI.LOGGER_DATE_RETURNED + localeDate);
        }
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
		final String methodName = METHOD_GET_LOCALE_DATE;
		String finalDate;
		String gregDate = PretupsI.EMPTY;
		String nepaliDate = PretupsI.EMPTY;
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED + PretupsI.LOGGER_DATE_PASSED + date);
        }
		String[] dateFormatArr = null; 
		if(!BTSLUtil.isNullString(dateInFormat)) {
			dateFormatArr = dateInFormat.split(PretupsI.SPACE);
		}
		NepaliCalendar nc = new NepaliCalendar();
		if(date != null) {
			int year = date.getYear();
			int add = year < 100 ? 2000 : year < 1900 && year >= 100 ? 1900 : 0;//This to support both (yyyy)2018 and (yy)18 format
			gregDate = date.getDate() + PretupsI.FORWARD_SLASH + (date.getMonth() + 1) + PretupsI.FORWARD_SLASH + (add+year);
			nepaliDate = nc.convertAdToBs(gregDate, dateInFormat);
		}
		if(!BTSLUtil.isNullArray(dateFormatArr) && dateFormatArr.length > 1) {
			finalDate = nepaliDate + PretupsI.SPACE + getTime(date, dateFormatArr[1]);
		} else {
			finalDate = nepaliDate;
		}
        if (log.isDebugEnabled()) {
        	log.debug(methodName, PretupsI.EXITED + PretupsI.LOGGER_DATE_RETURNED + finalDate);
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
		final String methodName = "getGregorianDateInString";
		String dateStr = PretupsI.EMPTY;
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED + PretupsI.LOGGER_DATE_PASSED + date);
        }
		NepaliCalendar nc = new NepaliCalendar();
		String dateFormat = BTSLDateUtil.getDateFormat(date);
		if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				PretupsI.DATE_FORMAT_YYYYMMDD.equalsIgnoreCase(dateFormat)) {
			dateStr = nc.convertBsToAd(date, "dd/MM/yyyy");//Change this to Constants.
		} else if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				PretupsI.DATE_FORMAT_DDMMYYYY.equalsIgnoreCase(dateFormat)) {
			dateStr = nc.convertBsToAd(date, "dd/MM/yyyy");//Change this to Constants.
		} else if(!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat) && 
				PretupsI.TIMESTAMP_DATESPACEHHMMSS.equalsIgnoreCase(dateFormat)) {//Correct this part[1]
			String[] part = date.split(PretupsI.SPACE);
			dateStr = nc.convertBsToAd(part[0], "dd/MM/yyyy") + " " + part[1];
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED + PretupsI.LOGGER_DATE_RETURNED + dateStr);
        }
		return dateStr;
	}
	
	private static String getTime(Date date, String timeInFormat) {
		final SimpleDateFormat sdf = new SimpleDateFormat(timeInFormat);
		sdf.setLenient(false); // this is required else it will convert
		return sdf.format(date);
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
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String args[]) throws Exception {
		NepaliCalendar nc = new NepaliCalendar();
		System.out.println(nc.convertAdToBs("15/06/2018", PretupsI.DATE_FORMAT_DDMMYYYY));
		System.out.println(nc.convertBsToAd("01/03/2075", PretupsI.DATE_FORMAT_YYMMDD));
		
		DateConverter dc = new NepaliDateConverterImpl();
		System.out.println(dc.getGregorianDateInString("01/03/2075"));
		System.out.println("passing String date : " + dc.getLocaleDate("15/06/2018", PretupsI.DATE_FORMAT_DDMMYYYY));
		System.out.println("passing date : " + dc.getLocaleDate(new Date(), PretupsI.DATE_FORMAT_DDMMYYYY));
		System.out.println("passing date : " + dc.getLocaleDate(new Date(), PretupsI.TIMESTAMP_DATESPACEHHMMSS));
//		System.out.println("getLocaleDate() " + dc.getLocaleDate(new Date()));
		System.out.println("getLocaleDateFromDate() : "+dc.getLocaleDateFromDate("15/06/2018"));
	}
	
}
