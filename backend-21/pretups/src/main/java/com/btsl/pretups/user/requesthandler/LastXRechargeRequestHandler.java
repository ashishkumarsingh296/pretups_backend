package com.btsl.pretups.user.requesthandler;

/**
 * @(#)LastXRechargeRequestHandler.java
 *                                      Copyright(c) 2011, Comviva tech. ltd.
 *                                      All Rights Reserved
 * 
 *                                      <this class is used to get last 'X'
 *                                      number of Recharge details of C2S user>
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Ravi kumar Jul,11,2011 Initital Creation
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.MComReportDBConnection;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.TransferEnquiryDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
public class LastXRechargeRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(LastXRechargeRequestHandler.class.getName());

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
		Connection con1 = null;MComConnectionI mcomCon1 = null;
        TransferEnquiryDAO transferEnquiryDAO = null;
        List<C2STransferVO> transfersList = null;
	String lastTransferType = null;
        UserPhoneVO userPhoneVO = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (messageArr.length < 1 || messageArr.length > 2) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
            
            mcomCon = new MComReportDBConnection();
			con=mcomCon.getConnection();
			mcomCon1 = new MComConnection();
            con1=mcomCon1.getConnection();

            if (messageArr.length == 1 && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType())) {
            	//bypass
            } else if (messageArr.length == 2) {
              //  mcomCon = new MComConnection();con=mcomCon.getConnection();
                // userPhoneVO=(UserPhoneVO)(((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO());
                // if((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                // &&
                // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                if (userPhoneVO.getPinRequired().equals(TypesI.YES) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType())) {
                    try {
                        ChannelUserBL.validatePIN(con1, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[1]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            con1.commit();
                        }
                        throw be;
                    }
                }
            }
             else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_LAST_TRSFER_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }
            
        	
        	
            	String receiverMsisdn = null, txnDateStr = null, txnDateFmt  = null;
            	java.sql.Date txnDate= null; 
                  
                final String[] arr = new String[1];
                int xLastTxn = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_RECHARGE_STATUS))).intValue();
                final String serviceType = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_FOR_LAST_X_RECHARGE));
                final int noDays = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DAYS_NO))).intValue(); // fetch
                                                                         // only
                try {
            		receiverMsisdn = (String)p_requestVO.getRequestMap().get("RECEIVER_MSISDN");
            	}catch(Exception e) {}
            	try {         		
            		txnDateStr = (String)p_requestVO.getRequestMap().get("TRANSACTION_DATE");
            		txnDateFmt = Constants.getProperty("LASTX_DATE_FORMAT");
            		if(BTSLUtil.isNullString(txnDateFmt))
            			txnDateFmt = PretupsI.DATE_FORMAT;
            		txnDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(txnDateStr, txnDateFmt));
            	}catch(Exception e) {}
            	try{
            		int sysPrefLastXTxn = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_RECHARGE_STATUS))).intValue();
            		xLastTxn = Integer.parseInt((String)p_requestVO.getRequestMap().get("NUMBER_OF_LAST_X_TXN"));
            		xLastTxn = (xLastTxn > sysPrefLastXTxn) ? 
            						sysPrefLastXTxn : xLastTxn;
            	}catch(Exception e) {}
            	p_requestVO.getRequestMap().put("NUMBER_OF_LAST_X_TXN",xLastTxn );
            	
                // data for
                // last these
                // days.
                lastTransferType = userPhoneVO.getLastTransferType();
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "xLastTxn: " + xLastTxn + " serviceType: " + serviceType + " noDays: " + noDays + " lastTransferType: " + lastTransferType
                    		+" recieverMsisdn: " + receiverMsisdn + " txnDate: " + txnDateStr);
                }
                if (!(BTSLUtil.isNullString(lastTransferType))) {
                    transferEnquiryDAO = new TransferEnquiryDAO();
                    /*index 
                     * c2s_transfers.receiver_msisdn + 
                     * channel_transfers.to_msisdn
                     */
                    transfersList = transferEnquiryDAO.loadLastXTransfers(con, channelUserVO.getActiveUserID(), xLastTxn, serviceType, noDays,
                    		receiverMsisdn, txnDate);
                    if (serviceType.contains(",")) {
                    	_log.info(METHOD_NAME, "Multiple service");
                        final ListSorterUtil sort = new ListSorterUtil();
                        transfersList = (ArrayList) sort.doSort("createdOn", "descending", transfersList);
                    }
                    if (transfersList != null && transfersList.size() > 0) {
                        this.formatLastTransferForSMS(transfersList, p_requestVO, xLastTxn);
                    } else {//noDays,receiverMsisdn, txnDate
                    	String dayOfTxn, recMsisdnStr;
                    	if(txnDate != null) {
                    		dayOfTxn = BTSLUtil.getDateStringFromDate(txnDate, PretupsI.DATE_FORMAT);
                    } else {
                    		dayOfTxn = Integer.toString(noDays);
                    	}
                    	if(!BTSLUtil.isNullString(receiverMsisdn)) {
                    		recMsisdnStr = receiverMsisdn;
                    	}else {
                    		recMsisdnStr = "";
                    	}
                    	final Integer temp = noDays;
                        final String[] arg = new String[] { dayOfTxn, recMsisdnStr };
                        p_requestVO.setMessageArguments(arg);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.LASTX_TRANSFER_NO_TRANSACTION_DONE);
                        /*if (noDays == 0) {
                            p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRANSFER_NO_TRANSACTION_DONE);
                        } else {
                            final Integer temp = noDays;
                            final String[] arg = new String[] { temp.toString() };
                            p_requestVO.setMessageArguments(arg);
                            p_requestVO.setMessageCode(PretupsErrorCodesI.LASTX_TRANSFER_NO_TRANSACTION_DONE);
                        }*/
                    }
                    HashMap map = p_requestVO.getRequestMap();
                    if (map == null) {
                        map = new HashMap();
                    }
                    map.put("TRANSFERLIST", transfersList);
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRANSFER_NO_TRANSACTION_DONE);
                }
                
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastXRechargeRequestHandler[process]", "", "", "",
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastXRechargeRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LastXRechargeRequestHandler#process");
        		mcomCon=null;
        		}
			if(mcomCon1 != null)
        	{
        		mcomCon1.close("LastXRechargeRequestHandler#process");
        		mcomCon1=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * This method basically prepares the message in the format to be pushed to
     * the user.
     * 
     * @author vikram.kumar
     * @param p_transferList
     *            ArrayList
     * @param p_requestVO
     *            RequestVO
     * @param int xLastTxn no. of details to be pushed in message.
     * @throws BTSLBaseException
     */
    private void formatLastTransferForSMS(List<C2STransferVO> p_transferList, RequestVO p_requestVO, int xLastTxn) throws BTSLBaseException {
        final String METHOD_NAME = "formatLastTransferForSMS";
        if (_log.isDebugEnabled()) {
            _log.debug("formatLastTransferForSMS", "Entered: p_transferList size:" + p_transferList.size(), "xLastTxn:" + xLastTxn);
        }
        try {
            final String[] arr = new String[1];
            final ArrayList argList = new ArrayList();
            KeyArgumentVO argumentVO = null;
            String[] messageArray = null;
            String sumMsgNo = null;
            int count = 1;
            final boolean multipleSms = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_TRF_MULTIPLE_SMS))).booleanValue();
            if (xLastTxn > p_transferList.size()) {
                xLastTxn = p_transferList.size();
            }
            // if multipleSms=false then send info of all transactions in one
            // sms otherwise send 1 sms for each transaction info.
            if (!multipleSms) {
                for (int i = 0; i < xLastTxn; i++) {
                    final C2STransferVO c2sTransferVO = (C2STransferVO) p_transferList.get(i);
                    sumMsgNo = Integer.valueOf(count).toString();
                    if (PretupsI.LOOKUP_CHANNEL_TRANSFER_TYPE_WITHDRAW.equals(c2sTransferVO.getServiceName())) {
                        c2sTransferVO.setReceiverMsisdn(c2sTransferVO.getSenderMsisdn()); // set
                        // the
                        // sender
                        // msisdn
                        // in
                        // the
                        // sender
                        // msisdn
                    }
                    // message format: 1- ID:[0], MSISDN:[1], Status:[2],
                    // Type:[3], Amount:[4], Post Balance [5]
                    messageArray = new String[] { sumMsgNo, c2sTransferVO.getTransferID(), c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getStatus(), c2sTransferVO
                                    .getType(), PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(c2sTransferVO.getSenderPostBalance()), c2sTransferVO
                                    .getTransferDateTime().toString(),c2sTransferVO.getProductName() };
                    argumentVO = new KeyArgumentVO();
                    argumentVO.setKey(PretupsErrorCodesI.LAST_XTRF_SUBKEY);
                    argumentVO.setArguments(messageArray);
                    argList.add(argumentVO);
                    count++;
                }
                arr[0] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argList);
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_XTRF_MAIN_KEY);
            } else {
                for (int i = 0; i < xLastTxn; i++) {
                    final C2STransferVO c2sTransferVO = (C2STransferVO) p_transferList.get(i);
                    sumMsgNo = Integer.valueOf(count).toString();
                    if (PretupsI.LOOKUP_CHANNEL_TRANSFER_TYPE_WITHDRAW.equals(c2sTransferVO.getServiceName())) {
                        c2sTransferVO.setReceiverMsisdn(c2sTransferVO.getSenderMsisdn()); // set
                        // the
                        // sender
                        // msisdn
                        // in
                        // the
                        // sender
                        // msisdn
                    }
                    // message format: 1- ID:[0], MSISDN:[1], Status:[2],
                    // Type:[3], Amount:[4], Post Balance [5]
                    messageArray = new String[] { sumMsgNo, c2sTransferVO.getTransferID(), c2sTransferVO.getReceiverMsisdn(), c2sTransferVO.getStatus(), c2sTransferVO
                                    .getType(), PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()), PretupsBL.getDisplayAmount(c2sTransferVO.getSenderPostBalance()), c2sTransferVO
                                    .getTransferDateTime().toString() };
                    final String senderMessage = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), PretupsErrorCodesI.LAST_TRF_DETAILS, messageArray);
                    // changed because in the case of stk request content type
                    // is null
                    if (i < xLastTxn - 1 && p_requestVO.isSuccessTxn() && (BTSLUtil.isNullString(p_requestVO.getReqContentType()) || p_requestVO.getReqContentType().indexOf(
                                    "xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1 || p_requestVO.getReqContentType().indexOf("plain") != -1 || p_requestVO
                                    .getReqContentType().indexOf("PLAIN") != -1) && p_requestVO.isSenderMessageRequired()) {
                        final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(p_requestVO);
                        if (!TypesI.YES.equals(serviceKeywordCacheVO.getExternalInterface())) {
                            final PushMessage pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO
                                            .getRequestGatewayCode(), p_requestVO.getSenderLocale());
                            pushMessage.push();
                        }
                    } else {
                        p_requestVO.setMessageArguments(messageArray);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRF_DETAILS);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("formatLastTransferForSMS", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LastXRechargeRequestHandler[formatLastTransferForSMS]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LastXRechargeRequestHandler", "formatLastTransferForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatLastTransferForSMS", "Exited:");
            }
        }
    }
}
