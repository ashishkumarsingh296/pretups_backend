package com.pretupsControllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dbrepository.DBHandler;

public class BTSLUtil {

	  /**
     * Validates the name of the file being uploaded
     * 
     * @param String
     *            and Date
     * @date : 14-08-2014
     * @return boolean
     */
    /**
     * @param value
     * @param date
     * @return
     */
    public static boolean timeRangeValidation(String value, Date date) {
        boolean validate = false;
        if (value == null || value.length() == 0) {
            return true;
        }

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            final String[] dateString = sdf.format(date).split(":");
            final int dm = Integer.parseInt(dateString[0]) * 60 + Integer.parseInt(dateString[1]);
            final String[] commaSepatated = value.split(","); // String []
            if (commaSepatated.length > 0) {
                for (int i = 0; i < commaSepatated.length; i++) {
                    final String[] hyphenSeparated = commaSepatated[i].split("-");
                    if (hyphenSeparated.length == 2) {
                        final String[] current1 = hyphenSeparated[0].split(":");
                        final String[] current2 = hyphenSeparated[1].split(":");
                        if (Integer.parseInt(current1[0]) * 60 + Integer.parseInt(current1[1]) <= dm && dm < Integer.parseInt(current2[0]) * 60 + Integer
                            .parseInt(current2[1])) {
                            validate = true;
                            break;
                        }
                    }
                }
            } else {
                validate = true;
            }
        } catch (Exception e) {
        }
        return validate;
    }
    
    /**
     * Get DateTime String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateTimeStringFromDate(Date date) {
        String format = DBHandler.AccessHandler.getSystemPreference("SYSTEM_DATETIME_FORMAT");
        if (isNullString(format)) {
            format = "dd/MM/yy HH:mm";
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }
    
    /**
     * Is Null String
     * 
     * @param str
     * @return
     */
    public static boolean isNullString(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
