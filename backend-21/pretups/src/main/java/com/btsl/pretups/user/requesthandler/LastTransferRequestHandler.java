package com.btsl.pretups.user.requesthandler;

/**
 * @(#)LastTransferRequestHandler.java
 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                     All Rights Reserved
 * 
 *                                     <description>
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     manoj kumar Sept 19, 2005 Initital
 *                                     Creation
 *                                     Gurjeet Singh Bedi Dec 03,2005 Modified
 *                                     for PIN position changes
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
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
import com.txn.pretups.channel.transfer.businesslogic.ChannelTransferTxnDAO;

public class LastTransferRequestHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(LastTransferRequestHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelTransferTxnDAO channelTransferTxnDAO = null;
        ChannelTransferVO channelTransferVO = null;
        C2STransferVO c2sTransferVO = null;
        C2STransferTxnDAO c2STransfertxnDAO = null;
        String lastTransferType = null;
        ArrayList channelTransferItemsList = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (messageArr.length == 2) {
                mcomCon = new MComConnection();con=mcomCon.getConnection();
                channelTransferTxnDAO = new ChannelTransferTxnDAO();
                c2STransfertxnDAO = new C2STransferTxnDAO();
                channelTransferItemsList = new ArrayList();
                // if((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)
                // &&
                // !PretupsI.DEFAULT_C2S_PIN.equals(BTSLUtil.decryptText((((ChannelUserVO)p_requestVO.getSenderVO()).getUserPhoneVO()).getSmsPin())))
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SMS_PIN_BYPASS_GATEWAY_TYPE)).contains(p_requestVO.getRequestGatewayType())) {
                    try {
                    	if(messageArr[1].equals("BlankPin"))
                    	{
                    		throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
                    	}
                     ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                           mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }

                lastTransferType = userPhoneVO.getLastTransferType();
                if (_log.isDebugEnabled()) {
                    _log.debug("process", " lastTransferType: " + lastTransferType);
                }
                if (BTSLUtil.isNullString(lastTransferType)) {
                    // no transaction has been till now
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_TRANSFER_NO_TRANSACTION_DONE);
                }
                HashMap map = p_requestVO.getRequestMap();
                if (map == null) {
                    map = new HashMap();
                }
                map.put("LASTTRFTYPE", lastTransferType.toUpperCase());

                if (PretupsI.LAST_TRANSACTION_C2S_TYPE.equalsIgnoreCase(lastTransferType)) {
                    // C2S_Transfers_Items behalf of lastTransfertID and
                    // lastTransferON
                    c2sTransferVO = c2STransfertxnDAO.loadLastTransfersStatusVOForC2S(con, userPhoneVO.getLastTransferID());
                    if (c2sTransferVO != null) {
                        this.formatLastTransferForSMS(c2sTransferVO, p_requestVO);
                    } else {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_TRANSFER_STATUS_NOT_FOUND);
                    }
                    map.put(PretupsI.LAST_TRANSACTION_C2S_TYPE, c2sTransferVO);
                } else if (PretupsI.LAST_TRANSACTION_C2C_TYPE.equalsIgnoreCase(lastTransferType) || PretupsI.LAST_TRANSACTION_O2C_TYPE.equalsIgnoreCase(lastTransferType)) {
                    // load the information from Channel_Transfers and
                    // Channel_Transfers_Items behalf of lastTransfertID and
                    // lastTransferON
                    channelTransferVO = new ChannelTransferVO();
                    channelTransferVO = channelTransferTxnDAO.loadLastTransfersStatusVO(con, userPhoneVO.getLastTransferID());
                    if (channelTransferVO != null) {
                        channelTransferItemsList = channelTransferTxnDAO.loadLastTransfersItemList(con, userPhoneVO.getLastTransferID());
                        channelTransferVO.setChannelTransferitemsVOList(channelTransferItemsList);
                        if (channelTransferItemsList != null && channelTransferItemsList.size() > 0) {
                            this.formatLastTransferListForSMS(channelTransferVO, p_requestVO);
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_TRANSFER_STATUS_NOT_FOUND);
                        }
                        map.put(lastTransferType, channelTransferVO);
                    } else {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_TRANSFER_STATUS_NOT_FOUND);
                    }
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_TRANSFER_NO_TRANSACTION_DONE);
                }
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_LAST_TRSFER_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                   mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LastTransferRequestHandler[process]","","","","BTSL Exception:"+be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                   mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastTransferRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LastTransferRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * this method use for preparing and formating SMS Message for Last transfer
     * status of the user
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @author manoj kumar
     */
    private void formatLastTransferListForSMS(ChannelTransferVO p_channelTransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatLastTransferListForSMS";
        if (_log.isDebugEnabled()) {
            _log.debug("formatLastTransferListForSMS", "Entered: p_lastTransferStatusList:" + p_channelTransferVO + ", p_requestVO=" + p_requestVO.toString());
        }
        try {
            ArrayList lastTransferStatusList = new ArrayList();
            final String[] arr = new String[7];
            lastTransferStatusList = p_channelTransferVO.getChannelTransferitemsVOList();
            final ArrayList argumentList = new ArrayList();
            final ArrayList argumentVOList = new ArrayList();
            KeyArgumentVO argumentVO = null, trfStatVO = null;
            arr[0] = p_channelTransferVO.getTransferID();
            arr[1] = BTSLUtil.getDateTimeStringFromDate(p_channelTransferVO.getCreatedOn());
            arr[2] = p_channelTransferVO.getTransferType();
            arr[3] = p_channelTransferVO.getStatus();
            if (lastTransferStatusList != null && lastTransferStatusList.size() > 0) {
                for (int i = 0, k = lastTransferStatusList.size(); i < k; i++) {
                    final ChannelTransferItemsVO transferItemsVO = (ChannelTransferItemsVO) lastTransferStatusList.get(i);
                    // sonali garg changes start
                    trfStatVO = new KeyArgumentVO();
                    trfStatVO.setKey(p_channelTransferVO.getStatus());
                    argumentList.add(trfStatVO);
                    // sonali garg changes end

                    argumentVO = new KeyArgumentVO();
                    final String[] transferStatusArr = new String[3];//
                    transferStatusArr[0] = transferItemsVO.getShortName();
                    transferStatusArr[1] = PretupsBL.getDisplayAmount(transferItemsVO.getApprovedQuantity());
                    // sonali
                    transferStatusArr[2] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentList);
                    // ------
                    argumentVO.setKey(PretupsErrorCodesI.LAST_TRANSFER_STATUS_MSG);
                    argumentVO.setArguments(transferStatusArr);
                    argumentVOList.add(argumentVO);
                }
                arr[4] = BTSLUtil.getMessage(p_requestVO.getSenderLocale(), argumentVOList);
                arr[5] = PretupsBL.getDisplayAmount(p_channelTransferVO.getNetPayableAmount());
                arr[6]=p_channelTransferVO.getToUserMsisdn();
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_TRANSFER_STATUS_LIST_SUCCESS);
            }
        } catch (Exception e) {
            _log.error("formatLastTransferListForSMS", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LastTransferRequestHandler[formatLastTransferListForSMS]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LastTransferRequestHandler", "formatLastTransferListForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatLastTransferListForSMS", "Exited: size =");
            }

        }

    }

    /**
     * this method use for preparing and formating SMS Message for Last transfer
     * status of c2s
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_requestVO
     * @throws BTSLBaseException
     * @author manoj kumar
     */
    private void formatLastTransferForSMS(C2STransferVO p_C2STransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatLastTransferForSMS";
        if (_log.isDebugEnabled()) {
            _log.debug("formatLastTransferForSMS", "Entered: p_lastTransferStatusList:" + p_C2STransferVO + ", p_requestVO=" + p_requestVO.toString());
        }
        try {
            // changed for last transfer requesthandler for CRE_INT_CR00030
            final String[] arr = new String[7];
            arr[0] = p_C2STransferVO.getTransferID();
            arr[1] = p_C2STransferVO.getTransferDateTimeAsString();
            arr[2] = (p_C2STransferVO.getSID()!=null)?p_C2STransferVO.getSID():p_C2STransferVO.getReceiverMsisdn();
            arr[3] = p_C2STransferVO.getValue();
            arr[4] = p_C2STransferVO.getServiceType();
            arr[5] = p_C2STransferVO.getProductName();
            arr[6] = PretupsBL.getDisplayAmount(p_C2STransferVO.getTransferValue());
            p_requestVO.setMessageArguments(arr);
            if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_TRANSFER_STATUS_SUCCESS);
            } else if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_TRANSFER_STATUS_FAIL);
            } else if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_TRANSFER_STATUS_AMBIGUOUS);
            } else if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_TRANSFER_STATUS_UNDER_PROCESS);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_TRANSFER_STATUS_DEFAULT);
            }
        } catch (Exception e) {
            _log.error("formatLastTransferForSMS", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastTransferRequestHandler[formatLastTransferForSMS]",
                            "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LastTransferRequestHandler", "formatLastTransferForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatLastTransferForSMS", "Exited: size =");
            }

        }

    }

}
