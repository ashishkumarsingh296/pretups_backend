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

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PSubscriberCounterVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.ibm.icu.util.Calendar;

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
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestID, "Entered p_requestVO: " + p_requestVO);
        }

        final String methodName = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        SenderVO senderVO = null;// added by Ashish S for Transaction Histry
        // Report Through Charged SMSC dated
        // 03-Aug-2007
        try {
            senderVO = (SenderVO) p_requestVO.getSenderVO();// changed by Ashish
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			SubscriberDAO subscriberDAO = new SubscriberDAO();
			ArrayList senderTransferCounterList = subscriberDAO.loadSubscriberTransferCounterList(con,senderVO.getMsisdn());
			senderVO.setTransferList(senderTransferCounterList);
			
			
            // S for Transaction
            // Histry Report
            // Through Charged
            // SMSC dated
            // 03-Aug-2007
            // <Key Word> <PIN>
            final String[] args = p_requestVO.getRequestMessageArray();
            boolean transferStatusFlag = false;
			
            final String actualPin = senderVO.getPin();
            final int messageLength = args.length;
            if (messageLength > 2) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_INVALID_TR_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
                switch (messageLength) {
                case 1: {
                    if (BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin)) {
                        transferStatusFlag = true;
                    } else {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_INVALID_TR_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO
                            .getActualMessageFormat() }, null);
                    }
                    break;
                }
                case 2: {
                    if (BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin)) {
                        transferStatusFlag = true;
                    } else {
                        try {
                            SubscriberBL.validatePIN(con, senderVO, p_requestVO.getRequestMessageArray()[1]);
                        } catch (BTSLBaseException be) {
                            if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                                .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            	mcomCon.finalCommit();
                            }
                            throw be;
                        }
                        transferStatusFlag = true;
                    }
                    break;
                }
                }
            } else {
                transferStatusFlag = true;
            }

            // to check whether user made any transaction or not
            if (transferStatusFlag) {
                if (senderVO.getTotalTransfers() == 0) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.NO_TRANSACTION);
                }
                // set the arguments for message
                final Date currentDate = new Date();
                p_requestVO.setMessageArguments(checkResetCountersAfterPeriodChange(senderVO, currentDate));
                p_requestVO.setMessageCode(PretupsErrorCodesI.TRANSFER_REPORT_SUCCESS);
                return;
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (!PretupsErrorCodesI.NO_TRANSACTION.equals(String.valueOf(be.getMessage()))) {
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
            }
            _log.error("process", _requestID, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.TRANSFER_REPORT_FAILED);
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", _requestID, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransfersReportController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.TRANSFER_REPORT_FAILED);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("TransfersReportController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestID, " Exited ");
            }
        }
    }

    /**
     * 
     * @param p_senderVO
     * @param p_newDate
     * @return
     */
    public String[] checkResetCountersAfterPeriodChange(SenderVO p_senderVO, Date p_newDate) {
        if (_log.isDebugEnabled()) {
            _log.debug("checkResetCountersAfterPeriodChange", "Entered  LastSuccessfulTransferDate=" + p_senderVO.getLastSuccessTransferDateStr() + " MSISDN=" + p_senderVO
                .getMsisdn() + " Current Date=" + p_newDate);
        }

        boolean isCounterChange = false;
        boolean isDayCounterChange = false;
        boolean isWeekCounterChange = false;
        boolean isMonthCounterChange = false;
		ArrayList<P2PSubscriberCounterVO> tranferCounterList = p_senderVO.getTransferList();
       
        final Date previousDate = p_senderVO.getLastSuccessTransferDate();

		String arr[] = new String[3];
        
		if (previousDate != null) {
            final Calendar cal = BTSLDateUtil.getInstance();
            cal.setTime(p_newDate);
            final int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            final int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int presentMonth = cal.get(Calendar.MONTH);
            final int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(previousDate);
            final int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            final int lastTrxMonth = cal.get(Calendar.MONTH);
            final int lastTrxYear = cal.get(Calendar.YEAR);
            if (presentDay != lastTrxDay) {
                isDayCounterChange = true;
            }
            if (presentWeek != lastWeek) {
                isWeekCounterChange = true;
            }
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

			final ArrayList<KeyArgumentVO> dailyTransferArrList = new ArrayList<KeyArgumentVO>();
			final ArrayList<KeyArgumentVO> weeklyTransferArrList = new ArrayList<KeyArgumentVO>();
			
			P2PSubscriberCounterVO p2PSubscriberCounterVO = null;
			final ArrayList<KeyArgumentVO> monthlyTransferArrList = new ArrayList<KeyArgumentVO>();
				
			KeyArgumentVO argumentVO = null;
            for (int i = 0; i<tranferCounterList.size(); i++ ){
            
				isCounterChange=false;
				p2PSubscriberCounterVO = (P2PSubscriberCounterVO)tranferCounterList.get(i);
			 
			 if (_log.isDebugEnabled())_log.debug("isResetCountersAfterPeriodChange","List Iterate for :"+p2PSubscriberCounterVO.getServiceName());
       
				if(isDayCounterChange)
				{
					p2PSubscriberCounterVO.setDailyTransferCount(0);
					p2PSubscriberCounterVO.setDailyTransferAmount(0);					
					isCounterChange=true;
				}
				
		        if(isWeekCounterChange)
				{
					p2PSubscriberCounterVO.setWeeklyTransferCount(0);
					p2PSubscriberCounterVO.setWeeklyTransferAmount(0);
					isCounterChange=true;
				}
		            
				if(isMonthCounterChange)
				{
					p2PSubscriberCounterVO.setMonthlyTransferCount(0);
					p2PSubscriberCounterVO.setMonthlyTransferAmount(0);
					isCounterChange=true;
				}
				if(isCounterChange)
					tranferCounterList.set(i,p2PSubscriberCounterVO);
				
				
				final String[] dailyTransferArr = new String[3];//
                argumentVO = new KeyArgumentVO();
				dailyTransferArr[0] = p2PSubscriberCounterVO.getServiceName();
                dailyTransferArr[1] = String.valueOf(p2PSubscriberCounterVO.getDailyTransferCount());
                dailyTransferArr[2] = PretupsBL.getDisplayAmount(p2PSubscriberCounterVO.getDailyTransferAmount());
                argumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_SELF_USR_PRODUCT_WITH_NETWORK_MSG);
                argumentVO.setArguments(dailyTransferArr);
                dailyTransferArrList.add(argumentVO);
                
                final String[] weeklyTransferArr = new String[3];//
                argumentVO = new KeyArgumentVO();
                weeklyTransferArr[0] = p2PSubscriberCounterVO.getServiceName();
                weeklyTransferArr[1] = String.valueOf(p2PSubscriberCounterVO.getWeeklyTransferCount());
                weeklyTransferArr[2] = PretupsBL.getDisplayAmount(p2PSubscriberCounterVO.getWeeklyTransferAmount());
                argumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_SELF_USR_PRODUCT_WITH_NETWORK_MSG);
                argumentVO.setArguments(weeklyTransferArr);
                weeklyTransferArrList.add(argumentVO);
				
				final String[] monthlyTransferArr = new String[3];//
                argumentVO = new KeyArgumentVO();
				monthlyTransferArr[0] = p2PSubscriberCounterVO.getServiceName();
                monthlyTransferArr[1] = String.valueOf(p2PSubscriberCounterVO.getMonthlyTransferCount());
                monthlyTransferArr[2] = PretupsBL.getDisplayAmount(p2PSubscriberCounterVO.getMonthlyTransferAmount());
                argumentVO.setKey(PretupsErrorCodesI.BAL_QUERY_SELF_USR_PRODUCT_WITH_NETWORK_MSG);
                argumentVO.setArguments(monthlyTransferArr);
                monthlyTransferArrList.add(argumentVO);
				
				
			}
			
			arr[0] = BTSLUtil.getMessage(p_senderVO.getLocale(), dailyTransferArrList);
			arr[1] = BTSLUtil.getMessage(p_senderVO.getLocale(), weeklyTransferArrList);
			arr[2] = BTSLUtil.getMessage(p_senderVO.getLocale(), monthlyTransferArrList);
      
        } else {
            isCounterChange = true;
        }

        if (_log.isDebugEnabled()) {
            _log.debug("isResetCountersAfterPeriodChange", "Exiting with isCounterChange=" + isCounterChange + " For " + p_senderVO.getMsisdn());
        }

        return arr;
    }

}
