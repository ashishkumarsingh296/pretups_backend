package com.btsl.pretups.processes.businesslogic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

/**
 * @description : This class will be used for sorting using Comparator in
 *              accending order.
 * @author : diwakar
 * @date : 07-MAR-2014
 * 
 */
public class DateSorting implements Comparator<String> {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public int compare(String p_date1, String p_date2) {
        final String METHOD_NAME = "compare";
        int returnValue = 0;
        String dateFormat = "dd-MM-YYYY";
        if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT))))
            dateFormat = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            Date date1 = sdf.parse(p_date1);
            Date date2 = sdf.parse(p_date2);
            if (date1.compareTo(date2) > 0) {
                returnValue = 1;
            } else if (date1.compareTo(date2) < 0) {
                returnValue = -1;
            } else if (date1.compareTo(date2) == 0) {
                returnValue = 0;
            }
        } catch (ParseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        return returnValue;
    }

}
