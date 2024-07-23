package com.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import com.commons.MasterI;

public final class DateUtils {

	public DateUtils() {
	}

	static SimpleDateFormat sdf = null;

	public static void setDateformatAsPerDbConnection() {
		String dbConnectionType = _masterVO.getMasterValue(MasterI.DB_INTERFACE_TYPE);
		if (dbConnectionType.equalsIgnoreCase("Oracle")) {
			sdf = new SimpleDateFormat("dd/MM/yy");
		} else {
			sdf = new SimpleDateFormat("dd/MM/yyyy");
		}
	}

	public static String getDateAsPerFormat(String dateFormat) {
		if (Objects.isNull(dateFormat)) {
			throw new RuntimeException("The format entered is empty");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		return sdf.format(cal.getTime()).toString();
	}

	public static String getCurrentDate() {
		setDateformatAsPerDbConnection();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		return sdf.format(cal.getTime()).toString();
	}

	/* Integer number passed as negative return the previous date */

	public static String getPreviousDate(int num) {
		setDateformatAsPerDbConnection();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, num);
		return sdf.format(cal.getTime()).toString();
	}

	public static String getFutureDate(int num) {
		setDateformatAsPerDbConnection();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, num);
		return sdf.format(cal.getTime()).toString();
	}
}
