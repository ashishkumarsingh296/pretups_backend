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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

public class LastTransferStatusSubscriberWiseController implements ServiceKeywordControllerI {
    private static final Log LOG = LogFactory.getLog(LastTransferStatusSubscriberWiseController.class.getName());

    public void process(RequestVO requestVO){
        final String methodName = "process";
        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, " Entered " + requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        C2STransferVO c2sTransferVO = null;
        C2STransferTxnDAO c2STransfertxnDAO = null;
        String receiverMsisdn = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            final String[] messageArr = requestVO.getRequestMessageArray();
            String transferID = null;
            final int messageLen = messageArr.length;
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, " messageLen=" + messageLen);
            }

            receiverMsisdn = messageArr[2];

            if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && requestVO.isPinValidationRequired()) {
                try {
                    ChannelUserBL.validatePIN(con, channelUserVO, messageArr[3]);
                } catch (BTSLBaseException be) {
                    LOG.errorTrace(methodName, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                       mcomCon.finalCommit();
                    }
                    throw be;
                }
            }

            requestVO.setReceiverMsisdn(receiverMsisdn);
            c2STransfertxnDAO = new C2STransferTxnDAO();

            int maxDurationAllowed = 7;
            try {
                maxDurationAllowed = Integer.parseInt(Constants.getProperty("LAST_TXN_SUBSCRIBER_DURATION_ALLOWED"));
			} catch (NullPointerException e) {
                if(LOG.isDebugEnabled()){
                    LOG.debug(methodName, "LAST_TXN_SUBSCRIBER_DURATION_ALLOWED is not defined in Constants.props" + requestVO);
                }
			} catch (NumberFormatException e) {
                if(LOG.isDebugEnabled()){
                    LOG.debug(methodName, "value of LAST_TXN_SUBSCRIBER_DURATION_ALLOWED is incorrect so setting them to default values");
                }
			}
            
            Date fromDate = BTSLUtil.getDifferenceDate(new Date(), -maxDurationAllowed);
            c2sTransferVO = c2STransfertxnDAO.loadLastC2STransfersBySubscriberMSISDN(con, channelUserVO.getMsisdn(), requestVO.getReceiverMsisdn(), fromDate);
            

            if (c2sTransferVO != null) {
                requestVO.setValueObject(c2sTransferVO);
                this.formatLastTransferForSMS(c2sTransferVO, requestVO);
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.RECHARGE_NOT_FOUND, 0, new String[] { transferID }, null);
            }
        } catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);
            try {
                OracleUtil.rollbackConnection(con, " LastTransferStatusSubscriberWiseController", methodName);
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
                OracleUtil.rollbackConnection(con, "LastTransferStatusSubscriberWiseController", methodName);
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            LOG.error(methodName, "BTSLBaseException " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"LastTransferStatusSubscriberWiseController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            try {
            	if(mcomCon != null)
            	{
            		mcomCon.close("LastTransferStatusSubscriberWiseController#process");
            		mcomCon=null;
            		}
            	
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, " Exited " + requestVO);
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
    private void formatLastTransferForSMS(C2STransferVO c2sTransferVO, RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "formatLastTransferForSMS";

        if(LOG.isDebugEnabled()){
            LOG.debug(methodName, "Entered: p_lastTransferStatusList:" + c2sTransferVO + ", requestVO=" + requestVO.toString() + requestVO);
        }
        try {
            // changed for last transfer requesthandler for CRE_INT_CR00030
            final String[] arr = new String[8];
            arr[0] = c2sTransferVO.getTransferID();
            arr[1] = BTSLUtil.getDateTimeStringFromDate(c2sTransferVO.getTransferDateTime());
            arr[2] = c2sTransferVO.getReceiverMsisdn();
            arr[3] = c2sTransferVO.getValue();
            arr[4] = c2sTransferVO.getServiceType();
            arr[5] = c2sTransferVO.getProductName();
            arr[6] = PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue());
            arr[7] = PretupsBL.getDisplayAmount(c2sTransferVO.getReceiverTransferValue());
            requestVO.setMessageArguments(arr);
            if (c2sTransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_SUCCESS);
            } else if (c2sTransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_FAIL);
            } else if (c2sTransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_AMBIGUOUS);
            } else if (c2sTransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS)) {
                requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_UNDER_PROCESS);
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_DEFAULT);
            }
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
            		"LastTransferStatusSubscriberWiseController[formatLastTransferForSMS]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LastTransferStatusSubscriberWiseController", "formatLastTransferForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if(LOG.isDebugEnabled()){
                LOG.debug(methodName, "Exited");
            }
        }

    }

}
