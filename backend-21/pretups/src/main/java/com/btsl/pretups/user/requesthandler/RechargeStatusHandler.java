package com.btsl.pretups.user.requesthandler;

/**
 * @(#)RechargeStatusHandler.java
 *                                Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Gurjeet Singh Bedi 16/01/07 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Controller for Last Recharge Status, This will
 *                                give the status of transfer ID that was
 *                                queried
 */

import java.sql.Connection;

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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

public class RechargeStatusHandler implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(RechargeStatusHandler.class.getName());

    public void process(RequestVO p_requestVO)// for AIRCEL changes::modified by
    // Vipul
    {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelTransferDAO channelTransferDAO = null;
        C2STransferVO c2sTransferVO = null;
        C2STransferTxnDAO c2STransfertxnDAO = null;
        String externalRefNum = null;
        String extRefNumORTxnID = null;
        try {
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
                // message Format
                // <Key word> <ExtRefNum> <EXT>
                // <Key Word> < TXNID> <TXN>
                // <Key word> <ExtRefNum> <EXT> <PIN>
                // <Key Word> < TXNID> <TXN> <PIN>
                // <Key word> <ExtRefNum> < TXNID> <BOTH>
                // <Key word> <ExtRefNum> < TXNID> <BOTH> <PIN>
            }

            if (p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_SMS_POS))
            // if(p_requestVO.getMessageGatewayVO().getGatewayType().equals(PretupsI.GATEWAY_TYPE_SMSC))
            {
                final String[] arr = p_requestVO.getRequestMessageArray();
                final String[] msg = new String[4];
                msg[0] = arr[0];
                msg[1] = arr[1];
                msg[2] = PretupsI.TRANSFER_TYPE_TXN;
                msg[3] = arr[2];
                p_requestVO.setRequestMessageArray(msg);

            }
            final String messageArr[] = p_requestVO.getRequestMessageArray();
            String transferID = null;
            final int messageLen = messageArr.length;
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            if (_log.isDebugEnabled()) {
                _log.debug("process", " messageLen=" + messageLen);
            }

            switch (messageLen) {

            // if message length is 3 then at the 2 index of array the value
            // could be extrefnum or transfer ID
            case PretupsI.C2S_MESSAGE_LENGTH_LAST_RECHARGE: {
                extRefNumORTxnID = messageArr[2];
                if ("EXT".equals(extRefNumORTxnID)) {
                    externalRefNum = messageArr[1];
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", "case3 externalRefNum=" + externalRefNum);
                    }
                    break;
                } else {
                    transferID = messageArr[1];
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", "case3 transferID=" + transferID);
                    }
                    break;
                }
            }
            // if message length is 4 then at the 2 index of array the value
            // could be extrefnum or transfer ID
            /*
             * If request comes from EXTGW then message doesnot have the PIN, so
             * it have transferid and only extregf num.
             */
            case PretupsI.C2S_MESSAGE_LENGTH_LAST_RECHARGE + 1: {

                if ("BOTH".equals(messageArr[3])) {
                    transferID = messageArr[1];
                    externalRefNum = messageArr[2];
                } else {
                    extRefNumORTxnID = messageArr[2];
                    if ("EXT".equals(extRefNumORTxnID)) {
                        externalRefNum = messageArr[1];
                    } else {
                        transferID = messageArr[1];
                    }

                    if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && p_requestVO.isPinValidationRequired()) {
                        try {
                            ChannelUserBL.validatePIN(con, channelUserVO, messageArr[3]);
                        } catch (BTSLBaseException be) {
                            _log.errorTrace(METHOD_NAME, be);
                            if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                            .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                               mcomCon.finalCommit();
                            }
                            throw be;
                        }
                    }

                }
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "case 4 transferID=" + transferID + " externalRefNum=" + externalRefNum);
                }

                break;
            }

            case PretupsI.C2S_MESSAGE_LENGTH_LAST_RECHARGE + 2: {
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && p_requestVO.isPinValidationRequired()) {
                    try {
                        ChannelUserBL.validatePIN(con, channelUserVO, messageArr[4]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            mcomCon.finalCommit();
                        }
                        throw be;
                    }

                }
                transferID = messageArr[1];
                // externalRefNum=messageArr[2];
                externalRefNum = p_requestVO.getExternalReferenceNum();
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "case 5 transferID=" + transferID + " externalRefNum=" + externalRefNum);
                }
                break;
            }

            case PretupsI.C2S_MESSAGE_LENGTH_LAST_RECHARGE + 3: {
                if (userPhoneVO.getPinRequired().equals(PretupsI.YES) && p_requestVO.isPinValidationRequired()) {
                    try {
                        ChannelUserBL.validatePIN(con, channelUserVO, messageArr[4]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            mcomCon.finalCommit();
                        }
                        throw be;
                    }

                }
                transferID = messageArr[1];
                // externalRefNum=messageArr[2];
                externalRefNum = p_requestVO.getExternalReferenceNum();
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "case 5 transferID=" + transferID + " externalRefNum=" + externalRefNum);
                }
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_LRCH_INVALIDMESSAGEFORMAT, 0,
                                new String[] { p_requestVO.getActualMessageFormat() }, null);
            }

            p_requestVO.setTransactionID(transferID);
            channelTransferDAO = new ChannelTransferDAO();
            c2STransfertxnDAO = new C2STransferTxnDAO();

            if (!BTSLUtil.isNullString(externalRefNum)) {
                c2sTransferVO = c2STransfertxnDAO.loadLastTransfersStatusVOForC2SWithExtRefNum(con, externalRefNum, p_requestVO);
            } else {
                c2sTransferVO = c2STransfertxnDAO.loadLastTransfersStatusVOForC2S(con, transferID);
            }

            if (c2sTransferVO != null) {
                if (!c2sTransferVO.getSenderID().equals(channelUserVO.getUserID())) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_RECHARGE_TXN_NOT_BY_YOU, 0, new String[] { transferID }, null);
                }
                p_requestVO.setValueObject(c2sTransferVO);
                this.formatLastTransferForSMS(c2sTransferVO, p_requestVO);
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LAST_RECHARGE_STATUS_NOT_FOUND, 0, new String[] { transferID }, null);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                   mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RechargeStatusHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("RechargeStatusHandler#process");
        		mcomCon=null;
        		}
        	
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
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
     */
    private void formatLastTransferForSMS(C2STransferVO p_C2STransferVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatLastTransferForSMS";
        if (_log.isDebugEnabled()) {
            _log.debug("formatLastTransferForSMS", "Entered: p_lastTransferStatusList:" + p_C2STransferVO + ", p_requestVO=" + p_requestVO.toString());
        }
        try {
            // changed for last transfer requesthandler for CRE_INT_CR00030
            final String[] arr = new String[9];
            arr[0] = p_C2STransferVO.getTransferID();
            arr[1] = p_C2STransferVO.getTransferDateTimeAsString();
            arr[2] = (p_C2STransferVO.getSID()!=null)?p_C2STransferVO.getSID():p_C2STransferVO.getReceiverMsisdn();
            arr[3] = p_C2STransferVO.getValue();
            arr[4] = p_C2STransferVO.getServiceType();
            arr[5] = p_C2STransferVO.getProductName();
            arr[6] = PretupsBL.getDisplayAmount(p_C2STransferVO.getTransferValue());
            arr[7] = PretupsBL.getDisplayAmount(p_C2STransferVO.getReceiverTransferValue());
            if(null!=p_C2STransferVO.getErrorCode())
				arr[8]=p_C2STransferVO.getErrorCode();
			else if(null==p_C2STransferVO.getErrorCode())
				arr[8]="";
            
            p_requestVO.setServiceType(p_C2STransferVO.getServiceType());
            if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_SUCCESS);
            } else if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_FAIL)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_FAIL);
            } else if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_AMBIGUOUS);
            } else if (p_C2STransferVO.getTransferStatus().equalsIgnoreCase(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_UNDER_PROCESS);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.LAST_C2S_RECHARGE_STATUS_DEFAULT);
            }
            p_requestVO.setMessageArguments(arr);
        } catch (Exception e) {
            _log.error("formatLastTransferForSMS", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RechargeStatusHandler[formatLastTransferForSMS]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RechargeStatusHandler", "formatLastTransferForSMS", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatLastTransferForSMS", "Exited");
            }

        }

    }

}
