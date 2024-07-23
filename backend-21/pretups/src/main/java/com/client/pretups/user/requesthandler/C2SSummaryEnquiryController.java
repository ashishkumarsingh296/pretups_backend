package com.client.pretups.user.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

public class C2SSummaryEnquiryController implements ServiceKeywordControllerI {
    private static final Log LOG = LogFactory.getLog(C2SSummaryEnquiryController.class.getName());

    public void process(RequestVO requestVO){
        final String methodName = "process";
        if(LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered " + requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        double totalSales = 0;
        C2STransferTxnDAO c2STransfertxnDAO = null;
        String fromDateString = null;
        String toDateString = null;
        String serviceType = null;
        String subService = null;
        Date fromDate = null;
        Date toDate = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            final String[] messageArr = requestVO.getRequestMessageArray();
            final int messageLen = messageArr.length;
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, " messageLen=" + messageLen);
            }

            fromDateString = messageArr[3];
            toDateString = messageArr[4];
            serviceType = messageArr[5];
            subService = messageArr[6];
            if("null".equals(subService)){
                subService=null;
            }
            
            if("null".equals(fromDateString) && toDateString.equals(toDateString)){
                try {
                    fromDateString = BTSLUtil.getDateStringFromDate(new Date(), "dd-MM-yyyy");
                    toDateString = BTSLUtil.getDateStringFromDate(new Date(), "dd-MM-yyyy");
				} catch (Exception e) {
					LOG.error(methodName, "SQLException : " + e.getMessage());
		        	LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException("C2SSummaryEnquiryController ", methodName, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
				} 
            }else if((!"null".equals(fromDateString) && "null".equals(toDateString)) || ("null".equals(fromDateString) && !"null".equals(toDateString))){
                throw new BTSLBaseException("C2SSummaryEnquiryController ", methodName, PretupsErrorCodesI.DATES_MISSING);
            }
            
            
            
            String curDateString = BTSLUtil.getDateStringFromDate(new Date(), "dd-MM-yyyy");
            
            if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && requestVO.isPinValidationRequired()) {
                try {
                    ChannelUserBL.validatePIN(con, channelUserVO, messageArr[2]);
                } catch (BTSLBaseException be) {
                    LOG.errorTrace(methodName, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        mcomCon.finalCommit();
                        
                    }
                    throw be;
                }
            }

            try {
                fromDate = BTSLUtil.getDateFromDateString(fromDateString, "dd-MM-yyyy");
                toDate = BTSLUtil.getDateFromDateString(toDateString, "dd-MM-yyyy");
			} catch (Exception e) {
				LOG.error(methodName, "Exception : " + e.getMessage());
	        	LOG.errorTrace(methodName, e);
                throw new BTSLBaseException(" C2SSummaryEnquiryController", methodName, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
			}
            
            int maxDiffAllowed = 10;
            int maxPastDaysLimit = 30;
            try {
                maxDiffAllowed = Integer.parseInt(Constants.getProperty("C2S_SUMMARY_MAX_DIFF_ALLOWED"));
                maxPastDaysLimit = Integer.parseInt(Constants.getProperty("C2S_SUMMARY_MAX_PAST_DAYS_LIMIT"));
			} catch (NullPointerException e) {
				if(LOG.isDebugEnabled()){
	                LOG.debug(methodName, "C2S_SUMMARY_MAX_DIFF_ALLOWED or C2S_SUMMARY_MAX_LAST_DAYS_ALLOWED or both are not defined in Constants.props");
	            }
			} catch (NumberFormatException e) {
                if(LOG.isDebugEnabled()){
                    LOG.debug(methodName, "value of C2S_SUMMARY_MAX_DIFF_ALLOWED or C2S_SUMMARY_MAX_LAST_DAYS_ALLOWED or both"
                    		+ " is incorrect so setting them to default values");
                }
			}
            
            if(BTSLUtil.getDifferenceInUtilDates(fromDate, toDate) > maxDiffAllowed){
                throw new BTSLBaseException(" C2SSummaryEnquiryController ", methodName, PretupsErrorCodesI.MAX_ALLOWED_INTERVAL_EXCEEDS, new String[] {""+maxDiffAllowed});
            }
            
            if(BTSLUtil.getDifferenceInUtilDates(fromDate, new Date()) > maxPastDaysLimit){
                throw new BTSLBaseException(" C2SSummaryEnquiryController ", methodName, PretupsErrorCodesI.MAX_PAST_DAYS_LIMIT_EXCEEDS, new String[] {""+maxPastDaysLimit});
            }
            
            c2STransfertxnDAO = new C2STransferTxnDAO();

            if(fromDateString.equals(toDateString) && fromDateString.equals(curDateString)){
                totalSales = c2STransfertxnDAO.loadTotalSalesToday(con, channelUserVO.getUserID(), fromDate, serviceType, subService);
            }else{
                totalSales = c2STransfertxnDAO.loadTotalSalesFromMIS(con, channelUserVO.getUserID(), fromDate, toDate, serviceType, subService);
            }   
            
            double balance = ((C2sBalanceQueryVO)(new UserBalancesDAO().loadUserBalances(con, channelUserVO.getUserID())).get(0)).getBalance();
        
            totalSales=BTSLUtil.getDisplayAmount(totalSales);
            balance=BTSLUtil.getDisplayAmount(balance);
            
            this.formatSummaryForSMS(requestVO,userPhoneVO.getMsisdn(),totalSales,balance,fromDateString,toDateString);
            
        } catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);
            try {
                OracleUtil.rollbackConnection(con, " C2SSummaryEnquiryController", methodName);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
            LOG.errorTrace(methodName, be);
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            try {
                OracleUtil.rollbackConnection(con, "C2SSummaryEnquiryController", methodName);
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            LOG.error(methodName, "BTSLBaseException " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"C2SSummaryEnquiryController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("C2SSummaryEnquiryController#process");
        		mcomCon=null;
        		}
            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, " Exited ");
            }
        }
    }

    /**
     * this method use for preparing and formating SMS Message for Last transfer
     * status of c2s
     * 
     * @param c2sTransferVO
     * @param requestVO
     * @throws BTSLBaseException
     */
    private void formatSummaryForSMS(RequestVO requestVO, String msisdn, double totalSales, double balance, String fromDateString,
    		String toDateString) throws BTSLBaseException {
        final String methodName = "formatSummaryForSMS";

        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, "Entered: msisdn=" + msisdn+", totalSales="+totalSales+", balance="+balance+", fromDateString="+fromDateString+", toDateString="+toDateString);
        }
        try {
            // changed for last transfer requesthandler for CRE_INT_CR00030
            final String[] arr = new String[5];
            arr[0] = msisdn;
            arr[1] = fromDateString;
            arr[2] = toDateString;
            arr[3] = ""+totalSales;
            arr[4] = ""+balance;
            
            requestVO.setMessageArguments(arr);
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_SUMMARY_ENQUIRY_SUCCESS);
            
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"C2SSummaryEnquiryController[formatSummaryForSMS]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2SSummaryEnquiryController", "formatSummaryForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, "Exited");
            }

        }

    }

}
