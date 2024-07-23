package com.btsl.util;

/**
 * @(#)BTSLUtil
 *              Copyright(c) 2014, Bharti Telesoft Int. Public Ltd.
 *              All Rights Reserved
 *              This class is an utility Class for Pretups System.
 *              ----------------------------------------------------------------
 *              ---------------------------------
 *              Author Date History
 *              ----------------------------------------------------------------
 *              ---------------------------------
 *              Diwakar Jan 08,2014 Initial Creation
 *              ----------------------------------------------------------------
 *              --------------------------------
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class BTSLUtil4MonthlyPOSReport {

    private static Log _log = LogFactory.getLog(BTSLUtil4MonthlyPOSReport.class.getName());

    /**
   	 * to ensure no class instantiation 
   	 */
       private BTSLUtil4MonthlyPOSReport() {
           
       }
       
    /**
     * Get DateTime String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getFileNameStringFromDate(Date date, String p_dateFormat) throws ParseException {
        String format = "ddMMyy_HHmmss";
        if (!BTSLUtil.isNullString(p_dateFormat)) {
            format = p_dateFormat;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

}