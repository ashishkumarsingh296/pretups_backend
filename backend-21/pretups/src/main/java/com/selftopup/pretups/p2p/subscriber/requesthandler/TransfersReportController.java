/**
 * @(#)TransfersReportController.java
 *                                    Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                    All Rights Reserved
 * 
 *                                    <description>
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    avinash.kamthan Mar 29, 2005 Initital
 *                                    Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 * 
 */

package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

/**
 * @author avinash.kamthan
 */
public class TransfersReportController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(TransfersReportController.class.getName());
    private String _requestID = null;

    /**
	 * 
	 */
    public void process(RequestVO p_requestVO) {
        _requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled())
            _log.debug("process", _requestID, "Entered p_requestVO: " + p_requestVO);

        Connection con = null;
        SenderVO senderVO = null;// added by Ashish S for Transaction Histry
                                 // Report Through Charged SMSC dated
                                 // 03-Aug-2007
        try {
            senderVO = (SenderVO) p_requestVO.getSenderVO();// changed by Ashish
                                                            // S for Transaction
                                                            // Histry Report
                                                            // Through Charged
                                                            // SMSC dated
                                                            // 03-Aug-2007
            // <Key Word> <PIN>
            String[] args = p_requestVO.getRequestMessageArray();
            boolean transferStatusFlag = false;
            con = OracleUtil.getConnection();
            String actualPin = senderVO.getPin();
            int messageLength = args.length;
            if (messageLength > 2)
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_TR_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            if (SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) {
                switch (messageLength) {
                case 1: {
                    // if(PretupsI.DEFAULT_P2P_PIN.equals(BTSLUtil.decryptText(actualPin)))
                    if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))
                        transferStatusFlag = true;
                    else
                        throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_TR_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                    break;
                }
                case 2: {
                    // if(PretupsI.DEFAULT_P2P_PIN.equals(BTSLUtil.decryptText(actualPin)))
                    if ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))
                        transferStatusFlag = true;
                    else {
                        try {
                            SubscriberBL.validatePIN(con, senderVO, p_requestVO.getRequestMessageArray()[1]);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                                con.commit();
                            throw be;
                        }
                        transferStatusFlag = true;
                    }
                    break;
                }
                }
            } else
                transferStatusFlag = true;

            // to check whether user made any transaction or not
            if (transferStatusFlag) {
                if (senderVO.getTotalTransfers() == 0)
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.NO_TRANSACTION);
                // set the arguments for message
                Date currentDate = new Date();
                p_requestVO.setMessageArguments(checkResetCountersAfterPeriodChange(senderVO, currentDate));
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.TRANSFER_REPORT_SUCCESS);
                return;
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
            }
            if (!SelfTopUpErrorCodesI.NO_TRANSACTION.equals(String.valueOf(be.getMessage())))// added
                                                                                             // &
                                                                                             // changed
                                                                                             // by
                                                                                             // Ashish
                                                                                             // S
                                                                                             // for
                                                                                             // Transaction
                                                                                             // Histry
                                                                                             // Report
                                                                                             // Through
                                                                                             // Charged
                                                                                             // SMSC
                                                                                             // dated
                                                                                             // 03-Aug-2007
                p_requestVO.setSuccessTxn(false);
            _log.error("process", _requestID, "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.TRANSFER_REPORT_FAILED);
            }
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ee) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", _requestID, "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransfersReportController[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.TRANSFER_REPORT_FAILED);
            return;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("process", _requestID, " Exited ");
        }
    }

    /**
     * 
     * @param p_senderVO
     * @param p_newDate
     * @return
     */
    public String[] checkResetCountersAfterPeriodChange(SenderVO p_senderVO, Date p_newDate) {
        if (_log.isDebugEnabled())
            _log.debug("checkResetCountersAfterPeriodChange", "Entered  LastSuccessfulTransferDate=" + p_senderVO.getLastSuccessTransferDateStr() + " MSISDN=" + p_senderVO.getMsisdn() + " Current Date=" + p_newDate);

        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;

        Date previousDate = p_senderVO.getLastSuccessTransferDate();

        String arr[] = { p_senderVO.getDailyTransferCountStr(), PretupsBL.getDisplayAmount(p_senderVO.getDailyTransferAmount()), p_senderVO.getMonthlyTransferCountStr(), PretupsBL.getDisplayAmount(p_senderVO.getMonthlyTransferAmount()) };

        if (previousDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(p_newDate);
            int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int presentMonth = cal.get(Calendar.MONTH);
            int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(previousDate);
            int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            int lastTrxMonth = cal.get(Calendar.MONTH);
            int lastTrxYear = cal.get(Calendar.YEAR);
            if (presentDay != lastTrxDay)
                isDayCounterChange = true;
            if (presentWeek != lastWeek)
                isWeekCounterChange = true;
            if (presentMonth != lastTrxMonth) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }
            if (presentYear != lastTrxYear) {
                isDayCounterChange = true;
                isWeekCounterChange = true;
                isMonthCounterChange = true;
            }

            //

            if (isDayCounterChange) {
                arr[0] = "0";
                arr[1] = "0";
                isCounterChange = true;
            }

            /*
             * if(isWeekCounterChange)
             * {
             * isCounterChange=true;
             * }
             */
            if (isMonthCounterChange) {
                arr[2] = "0";
                arr[3] = "0";
                isCounterChange = true;
            }
        } else
            isCounterChange = true;

        if (_log.isDebugEnabled())
            _log.debug("isResetCountersAfterPeriodChange", "Exiting with isCounterChange=" + isCounterChange + " For " + p_senderVO.getMsisdn());

        return arr;
    }

}
