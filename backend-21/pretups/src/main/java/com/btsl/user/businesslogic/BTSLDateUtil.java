/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

package com.btsl.user.businesslogic;

/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.validator.routines.RegexValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btsl.common.ApplicationContextProvider;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.DateConverterFactory;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.PersianCalendar;
import com.ibm.icu.util.ULocale;

/**
 * Utility to get locale date and corresponding values
 * 
 * @author tejeshvi.roy
 *
 */
@SuppressWarnings("deprecation")
public class BTSLDateUtil {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BTSLDateUtil.class);

	public static final String DATE = "date";
	public static final String MONTH = "month";
	public static final String YEAR = "year";
	public static final String METHOD_GET_CALENDAR = "getCalendar";
	public static final String METHOD_GET_SYSTEM_LOCALE_DATE = "getSystemLocaleDate";
	private static VMSCacheRepository vmsCacheRepository;

	/**
	 * Default Constructor
	 * 
	 */

	static {
		initializeVMSCacheRepository();
	}

	private BTSLDateUtil() {
		super();
		initializeVMSCacheRepository();
	}

	private static void initializeVMSCacheRepository() {
		vmsCacheRepository = (VMSCacheRepository) ApplicationContextProvider.getApplicationContext("TEST")
				.getBean(VMSCacheRepository.class);
	}

	/**
	 * This method will return the locale current Date/time of the system
	 * 
	 * @return
	 */
	public static String getSystemLocaleDateTime() {
		final String dateTimeStr = getLocaleDateTimeFromDate(new Date());
		return dateTimeStr;
	}

	/**
	 * getInstance will return the instance of calendar
	 * 
	 * @return
	 */
	public static Calendar getInstance() {
		Calendar cal;
		initializeVMSCacheRepository();
		String calendarType = vmsCacheRepository.getSystemPreferenceValue(PretupsConst.CALENDAR_TYPE.getStrValue());
		if (PretupsConst.PERSIAN.getStrValue().equalsIgnoreCase(calendarType)) {
			ULocale locale = new ULocale(PretupsConst.LOCALE_PERSIAN.getStrValue());
			cal = PersianCalendar.getInstance(locale);
		} else {
			cal = GregorianCalendar.getInstance();
		}
		return cal;
	}

	/**
	 * This method will return the date in yyMMdd format without separator if
	 * required
	 * 
	 * @param dateObj
	 * @param isSeparatorRequired
	 * @return
	 * @throws ParseException
	 */
	public static String getSystemLocaleDate(Date dateObj, Boolean isSeparatorRequired) throws ParseException {
		final String date = BTSLDateUtil.getCalendarTypeDate(dateObj);
		String[] dateArr = null;
		String separator = PretupsI.EMPTY;
		if (date.indexOf(PretupsI.FORWARD_SLASH) > -1) {
			dateArr = date.split(PretupsI.FORWARD_SLASH);
			separator = PretupsI.FORWARD_SLASH;
		} else if (date.indexOf(PretupsI.HYPHEN) > -1) {
			dateArr = date.split(PretupsI.HYPHEN);
			separator = PretupsI.HYPHEN;
		}

		String dateString = getDateString(dateArr, isSeparatorRequired, separator, date);
		return dateString;
	}

	private static String getDateString(String[] dateArr, Boolean isSeparatorRequired, String separator, String date) {
		int arrSize = dateArr != null ? dateArr.length : NumberConstants.ZERO.getIntValue();
		String dateString = PretupsI.EMPTY;
		String d;
		for (int i = NumberConstants.ZERO.getIntValue(); i < arrSize; i++) {
			if (dateArr != null) {
				if (dateArr[i].length() == NumberConstants.FOUR.getIntValue()) {
					d = dateArr[i].substring(NumberConstants.TWO.getIntValue());
				} else {
					d = dateArr[i];
				}
				dateString = BTSLUtil.isNullString(dateString) ? d : (dateString + separator + d);
			}
		}
		if (!isSeparatorRequired && !BTSLUtil.isNullString(date)) {
			dateString = dateString.replaceAll("/", PretupsI.EMPTY);
		} else {
			dateString = date;
		}
		return dateString;
	}

	/**
	 * This method will return the system locale date For ex- When "new Date()" is
	 * being used. We need to convert that into Locale date(if it is
	 * Gregorian/English type)
	 * 
	 * @param date - Date type
	 * @return
	 * @throws ParseException
	 */
	public static String getCalendarTypeDate(Date date) throws ParseException {// getCalendarTypeDate
		final String dateStr = DateConverterFactory.getLocaleDate(date);
		return dateStr;
	}

	/**
	 * This function will convert dd/MM/yyyy or yyyy/MM/dd to dd/MM/yy format
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String getStringDateInDDMMYY(String date) throws ParseException {
		Date actualDate;
		String format = getDateFormat(date);
		if (CommonUtils.isNullorEmpty(format)) {
			return PretupsI.EMPTY;
		}
		SimpleDateFormat inFormat = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYY);
		SimpleDateFormat fromFormat = new SimpleDateFormat(format);
		actualDate = fromFormat.parse(date);
		return inFormat.format(actualDate);
	}

	/**
	 * Returns the format type of the passed date We can add more regex to return
	 * more formats
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFormat(String date) {
		String dateFormat = PretupsI.EMPTY;
		if (BTSLUtil.isNullString(date)) {
			return dateFormat;
		}
		if (date.matches(DateTypes.DATE_FORMAT_ONE.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMYYYY;
		} else if (date.matches(DateTypes.DATE_FORMAT_TWO.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMYYYY_HYPHEN;
		} else if (date.matches(DateTypes.DATE_FORMAT_THREE.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_YYYYMMDD_HYPHEN;
		} else if (date.matches(DateTypes.DATE_FORMAT_FOUR.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_YYYYMMDD;
		} else if (date.matches(DateTypes.DATE_FORMAT_FIVE.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT;
		} else if (date.matches(DateTypes.DATE_FORMAT_SIX.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_HYPHEN;
		} else if (date.matches(DateTypes.DATE_FORMAT_SEVEN.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_YYYYMMMDD;
		} else if (date.matches(DateTypes.DATE_FORMAT_EIGHT.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYYYY;
		} else if (date.matches(DateTypes.DATE_FORMAT_NINE.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYY;
		} else if (date.matches(DateTypes.DATE_FORMAT_TEN.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_YYYYMMMDD_HYPHEN;
		} else if (date.matches(DateTypes.DATE_FORMAT_ELEVEN.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYYYY_HYPHEN;
		} else if (date.matches(DateTypes.DATE_FORMAT_TWELVE.getFormat())) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYY_HYPHEN;
		} else {
			dateFormat = getDateTimeFormat(date);
		}

		return dateFormat;
	}

	/*private static String checkregexsFORMATDDMMMYYHYPHEN(String date, String[] regexsFORMATDDMMMYYHYPHEN) {
		String dateFormat = PretupsI.EMPTY;
		if (validateDate(date, regexsFORMATDDMMMYYHYPHEN)) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYY_HYPHEN;
		} else {
			dateFormat = getDateTimeFormat(date);
		}
		return dateFormat;
	}*/

	/*private static String checkregexsf1(String date, String[] regexsFORMAT, String[] regexsFORMATHYPHEN,
			String[] regexsFORMATYYYYMMMDD) {
		String dateFormat = PretupsI.EMPTY;
		if (validateDate(date, regexsFORMAT)) {
			dateFormat = PretupsI.DATE_FORMAT;
		} else if (validateDate(date, regexsFORMATHYPHEN)) {
			dateFormat = PretupsI.DATE_FORMAT_HYPHEN;
		} else if (validateDate(date, regexsFORMATYYYYMMMDD)) {
			dateFormat = PretupsI.DATE_FORMAT_YYYYMMMDD;
		}
		return dateFormat;

	}*/

	/*private static String checkregexsf2(String date, String[] regexsFORMATDDMMMYYYY, String[] regexsFORMATDDMMMYY,
			String[] regexsFORMATYYYYMMMDDHYPHEN, String[] regexsFORMATDDMMMYYYYHYPHEN) {
		String dateFormat = PretupsI.EMPTY;
		if (validateDate(date, regexsFORMATDDMMMYYYY)) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYYYY;
		} else if (validateDate(date, regexsFORMATDDMMMYY)) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYY;
		}
		if (validateDate(date, regexsFORMATYYYYMMMDDHYPHEN)) {
			dateFormat = PretupsI.DATE_FORMAT_YYYYMMMDD_HYPHEN;
		} else if (validateDate(date, regexsFORMATDDMMMYYYYHYPHEN)) {
			dateFormat = PretupsI.DATE_FORMAT_DDMMMYYYY_HYPHEN;
		}
		return dateFormat;
	}*/

	/**
	 * Returns the format type of the passed date We can add more regex to return
	 * more formats
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeFormat(String date) {
		String dateFormat = PretupsI.EMPTY;
		if (BTSLUtil.isNullString(date)) {
			return dateFormat;
		}

		String[] regexsDDMMYYYYHHMMSS = new String[] {
				"([0-9]{2})/([0-9]{2})/([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsDDMMYYYYHHMMSSHYPHEN = new String[] {
				"([0-9]{2})-([0-9]{2})-([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsYYYYMMDDHHMMSSHYPHEN = new String[] {
				"([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsYYYYMMDDHHMMSS = new String[] {
				"([0-9]{4})/([0-9]{2})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsDATESPACEHHMMSS = new String[] {
				"([0-9]{2})/([0-9]{2})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsDATESPACEHHMMSSHYPHEN = new String[] {
				"([0-9]{2})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsYYYYMMMDDHHMMSS = new String[] {
				"([0-9]{4})/([a-zA-Z]{3})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsDDMMMYYYYHHMMSS = new String[] {
				"([0-9]{2})/([a-zA-Z]{3})/([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsDDMMMYYHHMMSS = new String[] {
				"([0-9]{2})/([a-zA-Z]{3})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsYYYYMMMDDHHMMSSHYPHEN = new String[] {
				"([0-9]{4})-([a-zA-Z]{3})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsDDMMMYYYYHHMMSSHYPHEN = new String[] {
				"([0-9]{2})-([a-zA-Z]{3})-([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };
		String[] regexsDDMMMYYHHMMSSHYPHEN = new String[] {
				"([0-9]{2})-([a-zA-Z]{3})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})" };

		if (validateDate(date, regexsDDMMYYYYHHMMSS)) {
			dateFormat = PretupsI.TIMESTAMP_DDMMYYYYHHMMSS;
		} else if (validateDate(date, regexsDDMMYYYYHHMMSSHYPHEN)) {
			dateFormat = PretupsI.TIMESTAMP_DDMMYYYYHHMMSS_HYPHEN;
		} else if (validateDate(date, regexsYYYYMMDDHHMMSSHYPHEN)) {
			dateFormat = PretupsI.TIMESTAMP_YYYYMMDDHHMMSS_HYPHEN;
		} else if (validateDate(date, regexsYYYYMMDDHHMMSS)) {
			dateFormat = PretupsI.TIMESTAMP_YYYYMMDDHHMMSS;
		} else if (validateDate(date, regexsDATESPACEHHMMSS)) {
			dateFormat = PretupsI.TIMESTAMP_DATESPACEHHMMSS;
		} else if (validateDate(date, regexsDATESPACEHHMMSSHYPHEN)) {
			dateFormat = PretupsI.TIMESTAMP_DATESPACEHHMMSS_HYPHEN;
		} else if (validateDate(date, regexsYYYYMMMDDHHMMSS)) {
			dateFormat = PretupsI.TIMESTAMP_YYYYMMMDDHHMMSS;
		} else if (validateDate(date, regexsDDMMMYYYYHHMMSS)) {
			dateFormat = PretupsI.TIMESTAMP_DDMMMYYYYHHMMSS;
		} else if (validateDate(date, regexsDDMMMYYHHMMSS)) {
			dateFormat = PretupsI.TIMESTAMP_DDMMMYYHHMMSS;
		} else if (validateDate(date, regexsYYYYMMMDDHHMMSSHYPHEN)) {
			dateFormat = PretupsI.TIMESTAMP_YYYYMMMDDHHMMSS_HYPHEN;
		} else if (validateDate(date, regexsDDMMMYYYYHHMMSSHYPHEN)) {
			dateFormat = PretupsI.TIMESTAMP_DDMMMYYYYHHMMSS_HYPHEN;
		} else if (validateDate(date, regexsDDMMMYYHHMMSSHYPHEN)) {
			dateFormat = PretupsI.TIMESTAMP_DDMMMYYHHMMSS_HYPHEN;
		} else {
			dateFormat = vmsCacheRepository
					.getSystemPreferenceValue(SystemPreferenceConstants.SYSTEM_DATE_FORMAT.getType());
		}
		return dateFormat;
	}

	/**
	 * Method will return the date of the month
	 * 
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
		if (nonMonDate
				.matches(regexDateMonth + PretupsI.FORWARD_SLASH + regexDateMonth + PretupsI.FORWARD_SLASH + regexYear)
				|| nonMonDate.matches(regexDateMonth + PretupsI.FORWARD_SLASH + regexDateMonth + PretupsI.FORWARD_SLASH
						+ regexYear2)) {
			dateArr = nonMonDate.split(PretupsI.FORWARD_SLASH);
			dateOfMonth = 0;
			month = NumberConstants.ONE.getIntValue();
			year = NumberConstants.TWO.getIntValue();
		} else if (nonMonDate
				.matches(regexDateMonth + PretupsI.HYPHEN + regexDateMonth + PretupsI.HYPHEN + regexYear)) {
			dateArr = nonMonDate.split(PretupsI.HYPHEN);
			dateOfMonth = 0;
			month = NumberConstants.ONE.getIntValue();
			year = NumberConstants.TWO.getIntValue();
		} else if (nonMonDate
				.matches(regexYear + PretupsI.HYPHEN + regexDateMonth + PretupsI.HYPHEN + regexDateMonth)) {
			dateArr = nonMonDate.split(PretupsI.HYPHEN);
			dateOfMonth = NumberConstants.TWO.getIntValue();
			month = NumberConstants.ONE.getIntValue();
			year = 0;
		} else if (nonMonDate.matches(
				regexYear + PretupsI.FORWARD_SLASH + regexDateMonth + PretupsI.FORWARD_SLASH + regexDateMonth)) {
			dateArr = nonMonDate.split(PretupsI.FORWARD_SLASH);
			dateOfMonth = NumberConstants.TWO.getIntValue();
			month = NumberConstants.ONE.getIntValue();
			year = 0;
		} else {
			datePart = PretupsI.EMPTY;
			dateOfMonth = 0;
			month = 0;
			year = 0;
		}
		return construct(dateArr, datePart, year, month, dateOfMonth);
	}

	private static int construct(String[] dateArr, String datePart, int year, int month, int dateOfMonth) {
		int returnData = -1;
		if (!CommonUtils.isNullorEmpty(dateArr)) {
			if (datePart.equalsIgnoreCase(DATE)) {
				returnData = Integer.parseInt(dateArr[dateOfMonth]);
			} else if (datePart.equalsIgnoreCase(MONTH)) {
				returnData = Integer.parseInt(dateArr[month]);
			} else if (datePart.equalsIgnoreCase(YEAR)) {
				returnData = Integer.parseInt(dateArr[year]);
			}
		}
		return returnData;
	}

	/**
	 * This function will return the passed Mon date to non-Mon date
	 * 
	 * @param date
	 * @param passedDateInFormat
	 * @return
	 */
	public static String getNonMonDate(String date, String passedDateInFormat) {
		String dateArr[];
		String finalDate = PretupsI.EMPTY;
		if (!BTSLUtil.isNullString(date)) {
			String passedDatesFormat = BTSLUtil.getTrimmedValue(passedDateInFormat);
			if (PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(passedDatesFormat)
					|| PretupsI.DATE_FORMAT_YYYYMMMDD_HYPHEN.equalsIgnoreCase(passedDatesFormat)) {
				dateArr = date.contains(PretupsI.FORWARD_SLASH) ? date.split(PretupsI.FORWARD_SLASH)
						: date.split(PretupsI.HYPHEN);
				int monthNumber = DateConverterFactory.getMonthNumeric(dateArr[1]);
				String monthNumberStr = monthNumber > -1 ? BTSLUtil.getTwoDigit(monthNumber) : dateArr[1];
				finalDate = dateArr[0] + PretupsI.FORWARD_SLASH + monthNumberStr + PretupsI.FORWARD_SLASH
						+ dateArr[NumberConstants.TWO.getIntValue()];
			} else {
				finalDate = date;
			}
		}
		return finalDate;
	}

	/**
	 * This date will return Mon date for passed non-mon date in the format which is
	 * set in DATE_FORMAT_CAL_JAVA in Constants.props
	 * 
	 * @param date
	 * @return
	 */
	public static String getMonDate(String date) {
		String finalDate;
		String format = BTSLUtil.getTrimmedValue(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.DATE_FORMAT_CAL_JAVA.getType()));
		if (PretupsI.YES.equalsIgnoreCase(SystemPreferenceConstants.IS_MON_DATE_ON_UI.getType())) {
			finalDate = getMonDate(date, format);
		} else {
			finalDate = date;
		}
		return finalDate;
	}

	/**
	 * This function will return Mon date for passed non-mon date in the passed
	 * format
	 * 
	 * @param date
	 * @param dateInFormat
	 * @return
	 */
	public static String getMonDate(String date, String dateInFormat) {
		String dateArr[];
		String finalDate = PretupsI.EMPTY;
		if (!BTSLUtil.isNullString(date)) {
			String dateFormat = BTSLUtil.getTrimmedValue(dateInFormat);
			if (PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(dateFormat)) {
				dateArr = date.split(PretupsI.FORWARD_SLASH);
				finalDate = dateArr[0] + PretupsI.FORWARD_SLASH
						+ DateConverterFactory.getMonthName(Integer.parseInt(dateArr[1])) + PretupsI.FORWARD_SLASH
						+ dateArr[NumberConstants.TWO.getIntValue()];
			}
		} else {
			finalDate = date;
		}
		return finalDate;
	}

	public static boolean validateDate(String dateStr, String[] regexs) {
		RegexValidator validator = new RegexValidator(regexs, false);
		return validator.isValid(dateStr);
	}

	/**
	 * This method will return the date and time of the passed date of
	 * timezone/locale SystemPreferences.TIMEZONE_ID For ex- When "new Date()" is
	 * being used. We need to convert that into Locale date(if it is
	 * Gregorian/English type)
	 * 
	 * @param date
	 * @return
	 */
	public static String getLocaleDateTimeFromDate(Date date) {
		final String methodName = "getLocaleDateTimeFromDate";
		String localeDate = PretupsI.EMPTY;
		String finalDate = PretupsI.EMPTY;
		int hours;
		int mins;
		int secs;
		if (date != null) {
			Calendar calendar = getLocaleCalendar(date);
			hours = calendar.get(Calendar.HOUR_OF_DAY);
			mins = calendar.get(Calendar.MINUTE);
			secs = calendar.get(Calendar.SECOND);
			try {
				localeDate = DateConverterFactory.getLocaleDate(new Date(date.getTime()));
			} catch (ParseException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(methodName, e.getMessage());
				}
			}
			finalDate = localeDate + PretupsI.SPACE + BTSLUtil.getTwoDigit(hours) + PretupsI.COLON
					+ BTSLUtil.getTwoDigit(mins) + PretupsI.COLON + BTSLUtil.getTwoDigit(secs);
		}
		return finalDate;
	}

	/**
	 * Returns the locale calendar instance with passed date We can use this method
	 * to get the locale date and time
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar getLocaleCalendar(Date date) {
		ULocale locale = new ULocale(
				BTSLUtil.isNullString(PretupsConst.LOCALE_LANGAUGE_EN.getStrValue()) ? PretupsI.EMPTY
						: PretupsConst.LOCALE_LANGAUGE_EN.getStrValue());
		Calendar calendar = Calendar.getInstance(locale);
		if (date != null) {
			calendar.setTime(date);
		}
		return calendar;
	}

	/**
	 * This function will return the locale
	 * 
	 * @param date
	 * @return
	 */
	public static String getLocaleTimeStamp(String date) {
		final String methodName = "getLocaleTimeStamp";
		String modifiedTimeStamp = PretupsI.EMPTY;

		try {
			if (!BTSLUtil.isNullString(date)) {
				String[] dateArr = date.split(PretupsI.SPACE);
				if (!BTSLUtil.isNullArray(dateArr) && dateArr.length < NumberConstants.TWO.getIntValue()) {
					modifiedTimeStamp = DateConverterFactory.getLocaleDate(dateArr[0]) + PretupsI.SPACE
							+ PretupsI.TIME_HHMMSS_VALUE;
				} else {
					modifiedTimeStamp = DateConverterFactory.getLocaleDate(dateArr[0]) + PretupsI.SPACE
							+ (dateArr[1].trim().length() > NumberConstants.EIGHT.getIntValue()
									? dateArr[1].trim().substring(0, NumberConstants.EIGHT.getIntValue())
									: dateArr[1].trim());
				}
			}
		} catch (ParseException e) {
			LOGGER.error("Error occured while converting locale date", e);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(methodName, e);
			}
		}
		return modifiedTimeStamp;
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

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(METHOD_NAME, "Entered  utilDate=" + utilDate);
		}
		if (utilDate != null) {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(utilDate);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			sqlDate = new java.sql.Date(calendar.getTimeInMillis());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(METHOD_NAME, "Exiting message Final Unchanged utilDate: " + utilDate);
		}
		return sqlDate;
	}// end of UtilDateToSqlDate

	/**
	 * This function will return date in String in the passed format
	 * 
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error(methodName, e.getMessage());
			}
		}
		return localeDate;
	}

	/**
	 * This function will return Gregorian date for any Local Calendar. As of now
	 * only implemented for Persian Calendar
	 * 
	 * @param date
	 * @return date as in Date format
	 * @throws ParseException
	 */
	public static Date getGregorianDate(String date) throws ParseException {// Handle null check for this methods
		Date gregorianDate;
		String calendarType = BTSLUtil.getTrimmedValue(
				vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.CALENDAR_TYPE.getType()));
		if (!BTSLUtil.isNullString(calendarType) && !PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
			gregorianDate = BTSLDateUtil.simpleDateForamtter(DateConverterFactory.getGregorianDateInString(date));
		} else {
			gregorianDate = BTSLDateUtil.simpleDateForamtter(date);// check this when IS_GREGORIAN is true
		}
		return gregorianDate;
	}

	/**
	 * Convert following formats to Date format : dd/MM/yyyy, dd-MM-yyyy,
	 * yyyy/MM/dd, yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static Date simpleDateForamtter(String date) {
		final String methodName = "simpleDateForamtter";
		DateFormat formatter;
		Date dateObj = null;
		String dateFormat = getDateFormat(date);
		if (!BTSLUtil.isNullString(date) && !BTSLUtil.isNullString(dateFormat)) {
			try {
				LOGGER.debug(
						PretupsI.LOGGER_DATE_PASSED + date + PretupsI.LOGGER_DATE_FORMAT + dateFormat);
				formatter = new SimpleDateFormat(dateFormat);
				dateObj = formatter.parse(date);
			} catch (Exception ex) {
				LOGGER.error(methodName, ex);
			}
		}
		return dateObj;
	}

	/**
	 * This function will return the timestamp in Gregorian when passed in other
	 * calendar Ex- 1397/02/13 13:35:53 to gregorian timestamp
	 * 
	 * @param date
	 * @return
	 */
	public static String getGregorianTimeStampInString(String date) {
		// Check for date format. Need to test
		String[] dateArr = null;
		String tempDate;
		if (!BTSLUtil.isNullString(date)) {
			dateArr = date.split(PretupsI.SPACE);
		}
		if (dateArr != null && dateArr.length > 1) {
			tempDate = dateArr[0];
		} else {
			tempDate = date;
		}
		String str;
		String finalDate = getFinalDate(tempDate);
		String caltype = vmsCacheRepository.getSystemPreferenceValue(SystemPreferenceConstants.CALENDAR_TYPE.getType());
		String calendarType = BTSLUtil.getTrimmedValue(caltype);
		if (!BTSLUtil.isNullString(calendarType) && !PretupsI.GREGORIAN.equalsIgnoreCase(calendarType)) {
			str = DateConverterFactory.getGregorianDateInString(finalDate);
		} else {
			str = finalDate;
		}
		if (dateArr != null && dateArr.length > 1) {
			str = str + PretupsI.SPACE + dateArr[1];
		}
		return str;
	}

	/**
	 * This function will return finally processed date
	 * 
	 * @param date
	 * @return
	 */
	public static String getFinalDate(String date) {
		String finalDate;
		HashMap<String, String> dateFormatMap = (HashMap<String, String>) getMonFormatMap(date);
		if (!BTSLUtil.isNullString(date)
				&& PretupsI.TRUE.equalsIgnoreCase(dateFormatMap.get(PretupsI.IS_MON_FORMAT_LITERAL))) {
			finalDate = getNonMonDate(date, dateFormatMap.get(PretupsI.DATE_FORMAT_LITERAL));
		} else {
			finalDate = date;
		}
		return finalDate;
	}

	/**
	 * This function will check whether the passed
	 * 
	 * @param dateOrFormat
	 * @return
	 */
	public static Map<String, String> getMonFormatMap(String dateOrFormat) {
		boolean isMonFormat;
		HashMap<String, String> dateFormatMap = new HashMap<String, String>();
		dateFormatMap.put(PretupsI.IS_MON_FORMAT_LITERAL, PretupsI.FALSE);
		if (!BTSLUtil.isNullString(dateOrFormat)) {
			dateFormatMap.put(PretupsI.DATE_FORMAT_LITERAL, getDateFormat(dateOrFormat));
			isMonFormat = checkIfMonFormat(dateFormatMap.get(PretupsI.DATE_FORMAT_LITERAL));
			if (isMonFormat) {
				dateFormatMap.put(PretupsI.IS_MON_FORMAT_LITERAL, PretupsI.TRUE);
			}
		}
		return dateFormatMap;
	}

	/**
	 * This function will check whether the passed date or date format is MMM(means
	 * date or date format contains MMM for month . Ex- dd/MMM/yyyy or yyyy/MMM/dd
	 * or dd/MMM/yy) format or not
	 * 
	 * @param dateOrFormatParam
	 * @return
	 */
	public static boolean checkIfMonFormat(String dateOrFormatParam) {
		boolean isMonFormat = false;
		if (!BTSLUtil.isNullString(dateOrFormatParam)) {
			String dateOrFormat = BTSLUtil.getTrimmedValue(dateOrFormatParam);
			if (PretupsI.DATE_FORMAT_YYYYMMMDD.equalsIgnoreCase(dateOrFormat)) {// add other conditions
				isMonFormat = true;
			}
		}
		return isMonFormat;
	}

}
