package com.btsl.pretups.requesthandler;

/**
 * * @(#)HandlerUtil.java
 * Copyright(c) 2005-2006, Bharti Telesoft Ltd.
 * All Rights Reserved
 * 
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Siddhartha Srivastava Dec 12, 2006 Initial Creation
 * 
 * This class contains the Utility methods which are common and often repeated
 * while handling of CCE XML request
 * 
 */
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HandlerUtil {

    private static Log _log = LogFactory.getLog(HandlerUtil.class.getName());
    public static final String FROM_DATE = "FROMDATE";
    public static final String TO_DATE = "TODATE";
    public static final String FROM_DATE_STR = "FROMDATESTR";
    public static final String TO_DATE_STR = "TODATESTR";

    /**
	 * to ensure no class instantiation 
	 */
    private HandlerUtil() {
        
    }
    /**
     * This method performs all the checks on the fromDate and toDate and also
     * checks the various condition
     * requiring both the dates together
     * 
     * @param p_fromDate
     * @param p_toDate
     * @throws BTSLBaseException
     */
    private static void dateRangeCheck(Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("dateRangeCheck", "Entered....p_fromDate:= " + p_fromDate + " p_toDate:= " + p_toDate);
        }

        Date currentDate = new Date();
        try {
            currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));

            // fromDate should not be greater than current date
            if (BTSLUtil.getDifferenceInUtilDates(currentDate, p_fromDate) > 0) {
                throw new BTSLBaseException("HandlerUtil", "dateRangeCheck", PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE);
            }

            // toDate should not be greater than current date
            if (BTSLUtil.getDifferenceInUtilDates(currentDate, p_toDate) > 0) {
                throw new BTSLBaseException("HandlerUtil", "dateRangeCheck", PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE);
            }

            // checking whether the from date is not greater than the to date
            if (BTSLUtil.getDifferenceInUtilDates(p_toDate, p_fromDate) > 0) {
                throw new BTSLBaseException("HandlerUtil", "dateRangeCheck", PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE);
            }

            // checking if the difference between the fromDate and toDate is not
            // more than the standard defined difference
            int noOfDays = BTSLUtil.getDifferenceInUtilDates(p_fromDate, p_toDate);
            if (noOfDays > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.XML_DATE_RANGE))).intValue()) {
                throw new BTSLBaseException("HandlerUtil", "dateRangeCheck", PretupsErrorCodesI.CCE_ERROR_DATE_DIFF_ERROR);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace("static", be);
            _log.error("dateRangeCheck", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("dateRangeCheck", "Exception " + e.getMessage());
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandlerUtil[dateRangeCheck]", "", "", "", "Exception:" + e.getMessage());
            // throw new BTSLBaseException("C2STransferHandler",
            // "dateRangeCheck", "error.general.processing");
            throw new BTSLBaseException("HandlerUtil", "dateRangeCheck", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("dateRangeCheck", "Exiting....");
        }
    }

    /**
     * @param p_fromDateStr
     * @param p_toDateStr
     * @param p_fromDate
     * @param p_toDate
     * @throws BTSLBaseException
     * @return ArrayList
     * @author ved prakash sharma
     */
    public static HashMap dateValidation(String p_fromDateStr, String p_toDateStr) throws BTSLBaseException {
        final String METHOD_NAME = "dateValidation";
        if (_log.isDebugEnabled()) {
            _log.debug("dateValidation", "Entered....p_fromDateStr=" + p_fromDateStr + "p_toDateStr=" + p_toDateStr);
        }

        HashMap dateMap = null;
        Date p_fromDate = null;
        Date p_toDate = null;
        try {
            String dateFormat = Constants.getProperty("CCE_XML_EXTERNAL_DATE_FORMAT");
            int daysDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.XML_DFT_DATE_RANGE))).intValue();
            boolean fromDateFlag = false;
            boolean toDateFlag = false;
            if (!BTSLUtil.isNullString(dateFormat)) {
                Date currentDate = new Date();
                currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));

                if (BTSLUtil.isNullString(p_fromDateStr) && BTSLUtil.isNullString(p_toDateStr)) {
                    p_fromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(BTSLUtil.getDifferenceDate(currentDate, -daysDiff)));
                    p_toDate = currentDate;
                }
                if (!BTSLUtil.isNullString(p_fromDateStr)) {
                    try {
                        p_fromDate = BTSLUtil.getDateFromDateString(p_fromDateStr, dateFormat);
                    } catch (ParseException pe) {
                        _log.errorTrace(METHOD_NAME, pe);
                        throw new BTSLBaseException("HandlerUtil", "validate", PretupsErrorCodesI.CCE_ERROR_FROMDATE_INVALID_FORMAT);
                    }
                    p_fromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(p_fromDate));
                    fromDateFlag = true;
                }
                if (!BTSLUtil.isNullString(p_toDateStr)) {
                    try {
                        p_toDate = BTSLUtil.getDateFromDateString(p_toDateStr, dateFormat);
                    } catch (ParseException pe) {
                        _log.errorTrace(METHOD_NAME, pe);
                        throw new BTSLBaseException("HandlerUtil", "validate", PretupsErrorCodesI.CCE_ERROR_TODATE_INVALID_FORMAT);
                    }
                    p_toDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(p_toDate));
                    toDateFlag = true;
                }
            } else {
                throw new BTSLBaseException("HandlerUtil", "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_EXTERNAL_DATE_FORMAT);
            }

            if (fromDateFlag && toDateFlag) {
                dateRangeCheck(p_fromDate, p_toDate);
            } else if (fromDateFlag && !toDateFlag) {
                p_toDate = p_fromDate;
            } else if (!fromDateFlag && toDateFlag) {
                p_fromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(BTSLUtil.getDifferenceDate(p_toDate, -daysDiff)));
            }
            // set the fromDate and toDate which are then retreived from the
            // calling method
            dateMap = new HashMap();
            dateMap.put(FROM_DATE, p_fromDate);
            dateMap.put(TO_DATE, p_toDate);
            dateMap.put(FROM_DATE_STR, BTSLUtil.getDateStringFromDate(p_fromDate));
            dateMap.put(TO_DATE_STR, BTSLUtil.getDateStringFromDate(p_toDate));
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("dateValidation", "BTSLBaseException " + e.getMessage());
            throw e;
        } catch (Exception e) {
            _log.error("dateValidation", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HandlerUtil[dateValidation]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("HandlerUtil", "dateValidation", PretupsErrorCodesI.REQ_NOT_PROCESS);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("dateValidation", "Exiting....p_fromDateStr=" + p_fromDateStr + "p_toDateStr=" + p_toDateStr);
        }
        return dateMap;
    }
}
