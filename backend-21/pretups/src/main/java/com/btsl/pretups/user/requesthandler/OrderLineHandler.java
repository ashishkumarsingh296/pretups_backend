package com.btsl.pretups.user.requesthandler;

/**
 * @(#)OrderMobileHandler.java
 *                             Copyright(c) 2009, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Kapil 11/02/09 Initial Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Controller for Order Mobile Lines Message to his
 *                             Manager, This will send a message to his immidate
 *                             parent only.
 */

import java.sql.Connection;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OrderLineLog;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class OrderLineHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(OrderLineHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO.toString());
        }

        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            // Message Format = <Keyword(ORDL)> <FixLine QTY2> <PIN>

            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();

            final String messageArr[] = p_requestVO.getRequestMessageArray();

            final int messageLen = messageArr.length;
            mcomCon = new MComConnection();con=mcomCon.getConnection();

            switch (messageLen) {
            case PretupsI.ORDER_MOBILE_MESSAGE_LENGTH: {
                if ((channelUserVO.getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, channelUserVO, messageArr[3]);
                    } catch (BTSLBaseException be) {
                        _log.errorTrace(METHOD_NAME, be);
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            throw be;
                        }
                    }
                }
                final String mobileLineQty = messageArr[1];
                p_requestVO.setMobileLineQty(mobileLineQty);
                final String fixedLineQty = messageArr[2];
                p_requestVO.setFixedLineQty(fixedLineQty);
                break;
            }
            case PretupsI.ORDER_MOBILE_MESSAGE_LENGTH - 1: {
                final String mobileLineQty = messageArr[1];
                p_requestVO.setMobileLineQty(mobileLineQty);
                final String fixedLineQty = messageArr[2];
                p_requestVO.setFixedLineQty(fixedLineQty);
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_CHLAN_INVALIDMESSAGEFORMAT, 0,
                                new String[] { p_requestVO.getActualMessageFormat() }, null);
            }// end of switch

            if (!BTSLUtil.isNumeric(p_requestVO.getMobileLineQty()) || !BTSLUtil.isNumeric(p_requestVO.getFixedLineQty())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_MOBILE_QUANTITY_NOTNUMERIC);
            }

            if ((p_requestVO.getMobileLineQty().length() > PretupsI.ORDER_MOBILE_QUANTITY_MAX_LENGTH) || (p_requestVO.getFixedLineQty().length() > PretupsI.ORDER_MOBILE_QUANTITY_MAX_LENGTH)) {
                final String lenArr[] = new String[2];
                lenArr[0] = String.valueOf("1");
                lenArr[1] = String.valueOf(PretupsI.ORDER_MOBILE_QUANTITY_MAX_LENGTH);
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_MOBILE_QUANTITY_LENGTHINVALID, 0, lenArr, null);
            }

            if ("0".equals(p_requestVO.getMobileLineQty()) && "0".equals(p_requestVO.getFixedLineQty())) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_MOBILE_QUANTITY_CANT_BE_ZERO);
            }

            // sender should not be top level user
            if (channelUserVO.getParentID().equals(PretupsI.ROOT_PARENT_ID)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_MOBILE_SENDER_CANT_BE_TOP_LEVEL);
            }

            final ChannelUserVO parentUserVO = channelUserTxnDAO.loadUserMsisdnAndStatus(con, channelUserVO.getParentID());

            String recieverParentMSISDN = null;
            String recieverParentStatus = null;

            if (parentUserVO != null) {
                recieverParentMSISDN = parentUserVO.getMsisdn();
                recieverParentStatus = parentUserVO.getStatus();
            }
            if (BTSLUtil.isNullString(recieverParentMSISDN)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PARENT_NOT_FOUND, 0, new String[] { p_requestVO.getRequestMSISDN() }, null);
            }

            if (recieverParentMSISDN != null) {
                p_requestVO.setReceiverMsisdn(recieverParentMSISDN);

                // parent should not be suspended
                if (recieverParentStatus.equals(PretupsI.USER_STATUS_SUSPEND)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_MOBILE_RECEIVER_SUSPEND);
                }
                // parent should be active
                if (!recieverParentStatus.equals(PretupsI.USER_STATUS_ACTIVE)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_MOBILE_RECEIVER_NOT_ACTIVE);
                }
                // parent should not be barred
                PretupsBL.checkMSISDNBarred(con, recieverParentMSISDN, p_requestVO.getRequestNetworkCode(), PretupsI.C2C_MODULE, PretupsI.USER_TYPE_RECEIVER);

                formatSMSforOrderMobile(p_requestVO);

                final Locale _receiverLocale = p_requestVO.getLocale();
                final BTSLMessages btslRecMessages = new BTSLMessages(PretupsErrorCodesI.ORDER_MOBILE_RECEIVER_SUCCESS,
                                new String[] { p_requestVO.getRequestMSISDN(), p_requestVO.getMobileLineQty(), p_requestVO.getFixedLineQty() });
                final String msg = BTSLUtil.getMessage(_receiverLocale, btslRecMessages.getMessageKey(), btslRecMessages.getArgs());
                (new PushMessage(recieverParentMSISDN, msg, p_requestVO.getRequestMSISDN(), p_requestVO.getRequestGatewayCode(), _receiverLocale)).push();
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PARENT_NOT_FOUND, 0, new String[] { p_requestVO.getRequestMSISDN() }, null);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
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
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RechargeStatusHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("OrderLineHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * This method is used for generating SMS Message for Order Mobile/Fixed
     * Lines.
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    private void formatSMSforOrderMobile(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatSMSforOrderMobile";
        if (_log.isDebugEnabled()) {
            _log.debug("formatSMSforOrderMobile", "Entered: p_requestVO=" + p_requestVO.toString());
        }
        try {
            final String[] arr = new String[2];
            arr[0] = p_requestVO.getMobileLineQty();
            arr[1] = p_requestVO.getFixedLineQty();

            p_requestVO.setMessageArguments(arr);
            p_requestVO.setMessageCode(PretupsErrorCodesI.ORDER_MOBILE_SENDER_SUCCESS);
            OrderLineLog.log(p_requestVO.getRequestMSISDN(), p_requestVO.getReceiverMsisdn(), p_requestVO.getMobileLineQty(), p_requestVO.getFixedLineQty());
        } catch (Exception e) {
            _log.error("formatSMSforOrderMobile", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OrderMobileHandler[formatSMSforOrderMobile]", "", "",
                            "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("OrderMobileHandler", "formatSMSforOrderMobile", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatSMSforOrderMobile", "Exited");
            }
        }
    }
}
