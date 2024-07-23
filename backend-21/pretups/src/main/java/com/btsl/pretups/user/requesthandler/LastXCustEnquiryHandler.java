package com.btsl.pretups.user.requesthandler;

/**
 * @(#)LastXCustEnquiryHandler.java
 *                                  Copyright(c) 2009, Comviva tech. ltd.
 *                                  All Rights Reserved
 *                                  <this class is basically used to get the
 *                                  details of last 'N' transactions for a
 *                                  subscriber by user.
 *                                  <description>
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Vikram kumar Nov,27,2009 Initital Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

public class LastXCustEnquiryHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(LastXCustEnquiryHandler.class.getName());

    // this method will first validate the user details(PIN).
    // Load the transfer details depending upon the service type.
    // sorts them in the descending order with createdON time.
    // prepares the message to be pushed to user.

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        C2STransferTxnDAO c2STransfertxnDAO = null;
        ArrayList transfersList = null;
        UserPhoneVO userPhoneVO = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            // HashMap requestHashMap = p_requestVO.getRequestMap();
            // java.util.Date txnDate = (java.util.Date)
            // requestHashMap.get("TXNDATE");
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (messageArr.length == 3) {
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                // userPhoneVO=(UserPhoneVO)(((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO());
                // if((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                // &&
                // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[2]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                final String receiverMSISDN = PretupsBL.getFilteredMSISDN(messageArr[1]);
                if (!BTSLUtil.isNullString(receiverMSISDN)) {
                    if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "[process]", p_requestVO.getRequestIDStr(),
                                        receiverMSISDN, "", "Customer MSISDN Not valid");
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.INVALID_RECEIVER_MSISDN);
                    }
                }

                final int xLastTxn = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_CUSTENQ_STATUS))).intValue();
                final String lastTransferType = userPhoneVO.getLastTransferType();
                if (_log.isDebugEnabled()) {
                    _log.debug("process", " lastTransferType: " + lastTransferType);
                }
                if (!(BTSLUtil.isNullString(lastTransferType))) {
                    c2STransfertxnDAO = new C2STransferTxnDAO();
                    transfersList = c2STransfertxnDAO.loadLastXCustTransfers(con, channelUserVO.getActiveUserID(), xLastTxn, PretupsBL.getFilteredMSISDN(messageArr[1]));

                    if (transfersList != null && transfersList.size() > 0) {
                        this.formatCustEnqReportForSMS(transfersList, p_requestVO);

                    } else {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_TRANSFER_NO_TRANSACTION_DONE);
                    }
                    HashMap map = p_requestVO.getRequestMap();
                    if (map == null) {
                        map = new HashMap();
                    }
                    map.put("TRANSFERLIST", transfersList);
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_TRANSFER_STATUS_NOT_FOUND);
                }
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_LAST_TRSFER_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastXCustEnquiryHandler[process]", "", "", "",
                            "BTSL Exception:" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastXCustEnquiryHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LastXCustEnquiryHandler#process");
        		mcomCon=null;
        		}
            
        	if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * this method prepares the message to be pushed to the user.
     * 
     * @author vikram.kumar
     * @param p_transferList
     *            ArrayList
     * @param p_requestVO
     *            RequestVO
     * @throws BTSLBaseException
     */
    private void formatCustEnqReportForSMS(ArrayList p_transferList, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatCustEnqReportForSMS";
        if (_log.isDebugEnabled()) {
            _log.debug("formatCustEnqReportForSMS", "Entered: p_transferList size :" + p_transferList.size());
        }
        KeyArgumentVO argumentVO = null, trfStatVO = null;
        String[] messageArray = null;
        String sumMsgNo = null;
        try {
            final String[] arr = new String[1];
            final ArrayList argList = new ArrayList();
            ArrayList statusList = new ArrayList();
            int count = 1;
            for (int i = 0, k = p_transferList.size(); i < k; i++) {
                final C2STransferVO channelTransferVO = (C2STransferVO) p_transferList.get(i);
                // sonali garg
                trfStatVO = new KeyArgumentVO();
                trfStatVO.setKey(channelTransferVO.getTransferStatus());
                statusList.add(trfStatVO);
                // sonali garg changes end
                sumMsgNo = Integer.valueOf(count).toString();
                if (BTSLUtil.isNullString(channelTransferVO.getTransferStatus())) {
                    messageArray = new String[] { sumMsgNo, channelTransferVO.getTransferID(), channelTransferVO.getServiceType(), PretupsBL
                                    .getDisplayAmount(channelTransferVO.getTransferValue()), channelTransferVO.getStatus(), channelTransferVO.getTransferDateTime().toString(), PretupsI.LAST_TRANSACTION_C2S_TYPE };
                } else {
                    messageArray = new String[] { sumMsgNo, channelTransferVO.getTransferID(), channelTransferVO.getServiceType(), PretupsBL
                                    .getDisplayAmount(channelTransferVO.getTransferValue()), BTSLUtil.getMessage(p_requestVO.getSenderLocale(), statusList), channelTransferVO
                                    .getTransferDateTime().toString(), PretupsI.LAST_TRANSACTION_C2S_TYPE };
                    // sonali garg changes
                }

                statusList = new ArrayList();

                argumentVO = new KeyArgumentVO();
                argumentVO.setKey(PretupsErrorCodesI.LAST_XCUST_ENQ_SUBKEY);
                argumentVO.setArguments(messageArray);
                argList.add(argumentVO);
                count++;
            }
            arr[0] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argList);
            p_requestVO.setMessageArguments(arr);
            p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_XCUST_ENQ_MAIN_KEY);
        } catch (Exception e) {
            _log.error("formatCustEnqReportForSMS", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastXCustEnquiryHandler[formatCustEnqReportForSMS]",
                            "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LastXCustEnquiryHandler", "formatCustEnqReportForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatCustEnqReportForSMS", "Exited:");
            }
        }
    }
}
