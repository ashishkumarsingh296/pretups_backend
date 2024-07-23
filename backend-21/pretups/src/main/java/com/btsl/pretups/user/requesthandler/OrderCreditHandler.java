package com.btsl.pretups.user.requesthandler;

/**
 * @(#)OrderCreditHandler.java
 *                             Copyright(c) 2009, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Kapil Mehta 09/01/09 Initial Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Controller for Order Credit Message to his
 *                             Manager, This will send a message to his Manager.
 */

import java.sql.Connection;
import java.util.ArrayList;
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
import com.btsl.pretups.logging.OrderCreditLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class OrderCreditHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(OrderCreditHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO.toString());
        }

        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            final String messageArr[] = p_requestVO.getRequestMessageArray();

            if (messageArr.length < 2) {
                throw new BTSLBaseException("C2CTransferController", "process", PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            } else {
                final String productArray[] = this.validateUserProductsFormatForSMS(con, messageArr, p_requestVO);

                // sender should not be top level user
                if (channelUserVO.getParentID().equals(PretupsI.ROOT_PARENT_ID)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_CREDIT_SENDER_CANT_BE_TOP_LEVEL);
                }

                channelUserVO = channelUserTxnDAO.loadUserMsisdnAndStatus(con, channelUserVO.getParentID());
                String recieverParentMSISDN = null;
                String recieverParentStatus = null;

                if (channelUserVO != null) {
                    recieverParentMSISDN = channelUserVO.getMsisdn();
                    recieverParentStatus = channelUserVO.getStatus();
                }

                if (BTSLUtil.isNullString(recieverParentMSISDN)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.REQUESTED_USER_PARENT_NOT_FOUND, 0, new String[] { p_requestVO.getRequestMSISDN() }, null);
                }

                if (recieverParentMSISDN != null) {

                    // parent should not be suspended
                    if (recieverParentStatus.equals(PretupsI.USER_STATUS_SUSPEND)) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_CREDIT_RECEIVER_SUSPEND);
                    }
                    // parent should be active
                    if (!recieverParentStatus.equals(PretupsI.USER_STATUS_ACTIVE)) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ORDER_CREDIT_RECEIVER_NOT_ACTIVE);
                    }
                    // parent should not be barred
                    PretupsBL.checkMSISDNBarred(con, recieverParentMSISDN, p_requestVO.getRequestNetworkCode(), PretupsI.C2C_MODULE, PretupsI.USER_TYPE_RECEIVER);

                    formatSMSforOrderCreditSender(productArray, p_requestVO, channelUserVO);

                    Locale receiverLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

                    if (channelUserVO != null) {
                        receiverLocale = channelUserVO.getUserPhoneVO().getLocale();
                    }

                    final Object[] smsListArr = prepareSMSMessageListForReceiver(productArray, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY);

                    final String[] array1 = { p_requestVO.getRequestMSISDN(), BTSLUtil.getMessage(receiverLocale, (ArrayList) smsListArr[0]) };

                    final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.ORDER_CREDIT_SENDER_SUCCESS, array1);
                    final PushMessage pushMessage = new PushMessage(recieverParentMSISDN, messages, "1", p_requestVO.getRequestGatewayCode(), receiverLocale,
                                    ((ChannelUserVO) p_requestVO.getSenderVO()).getNetworkCode());
                    pushMessage.push();

                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.REQUESTED_USER_PARENT_NOT_FOUND, 0, new String[] { p_requestVO.getRequestMSISDN() }, null);
                }
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OrderCreditHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("OrderCreditHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    public Object[] prepareSMSMessageListForReceiver(String[] productArray, String p_txnSubKey) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareSMSMessageListForReceiver", "Entered p_txnSubKey = " + p_txnSubKey);
        }

        final ArrayList txnSmsMessageList = new ArrayList();
        KeyArgumentVO keyArgumentVO = null;
        String argsArr[] = null;

        for (int i = 0, j = productArray.length; i < j; i += 2) {
            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[2];

            argsArr[0] = productArray[i];
            argsArr[1] = productArray[i + 1];

            keyArgumentVO.setKey(p_txnSubKey);
            keyArgumentVO.setArguments(argsArr);
            txnSmsMessageList.add(keyArgumentVO);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("prepareSMSMessageListForReceiver", "Exited  txnSmsMessageList.size() = " + txnSmsMessageList.size());
        }

        return (new Object[] { txnSmsMessageList });
    }

    /**
     * This method is used for generating SMS Message for Order Credit.
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    private void formatSMSforOrderCreditSender(String[] productArray, RequestVO p_requestVO, ChannelUserVO p_channeluserVO) throws BTSLBaseException {
        final String METHOD_NAME = "formatSMSforOrderCreditSender";
        if (_log.isDebugEnabled()) {
            _log.debug("formatSMSforOrderCreditSender", " productArray " + p_requestVO.toString() + " productArray " + productArray[0] + "," + productArray[1]);
        }

        try {

            final String[] arr = new String[productArray.length];
            // arr[0] = p_requestVO.getRequestMSISDN();
            arr[0] = productArray[1];
            arr[1] = p_channeluserVO.getMsisdn();
            p_requestVO.setMessageArguments(arr);
            p_requestVO.setMessageCode(PretupsErrorCodesI.ORDER_CREDIT_RECEIVER_SUCCESS);
            OrderCreditLog.log(p_requestVO.getRequestMSISDN(), p_channeluserVO.getMsisdn(), productArray[1]);
        } catch (Exception e) {
            _log.error("formatSMSforOrderCreditSender", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OrderCreditHandler[formatSMSforOrderCreditSender]",
                            "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("OrderCreditHandler", "formatSMSforOrderCreditSender", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("formatSMSforOrderCreditSender", "Exited");
            }
        }
    }

    /**
     * Method validateUserProductsFormatForSMS()
     * To get the product from the message Array and validate them whether
     * product code and
     * product quantity is valid or not.
     * 
     * @param p_prodArray
     * @param p_requestVO
     *            RequestVO
     * @return String[]
     * @throws BTSLBaseException
     */

    public String[] validateUserProductsFormatForSMS(Connection p_con, String[] p_prodArray, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateUserProductsFormatForSMS";
        if (_log.isDebugEnabled()) {
            _log.debug("validateUserProductsFormatForSMS", " Entered ChannelUserVO=" + p_requestVO.toString() + " p_prodArray=" + p_prodArray.length);
        }

        String productArray[] = p_prodArray;

        for (int i = 0, j = productArray.length; i < j; i++) {
            if (_log.isDebugEnabled()) {
                _log.debug("validateUserProductsFormatForSMS", " index=" + i + " value=" + productArray[i]);
            }
        }

        /**
         * [keyword] [qty] [productcode] [qty] [productcode] [password]
         * at the index of 1 the product details starts
         * 
         * different combiniation of the message format
         * [keyword] [qty] [password]
         * 
         * [keyword] [qty] [productcode] [password]
         * 
         * [keyword] [qty] [productcode] [qty] [productcode] [password]
         * 
         * If user sends only quantity the piclked the default product from the
         * system preferences.
         * In this case the array length is 3.
         * 
         * if array length is not 3 then it means user also sends product code
         * in his message
         * he can also sends request for multiple products. In this case
         * validate user sends
         * products quantity products code with each other.
         * 
         */
        final int messageLen = p_prodArray.length;
        boolean defaultProduct = false;
        boolean validateProduct = false;

        /*
         * This will check the following cases
         * If user PIN is required then message can be
         * [keyword][qty][pin]
         * [keyword][qty][productcode][pin]
         * Here min length can be 3. In case of lenth 3 default product will be
         * checked otherwise qty and product pair will be chekced
         * 
         * If user PIN is not required then message can be
         * [keyword][qty]
         * [keyword][qty][productcode]
         * Here min length can be 2. In case of lenth 2 default product will be
         * checked otherwise qty and product pair will be chekced
         * In this case if PIN is send then it will be invalid message format
         */

        final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();

        if ((((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
            /*
             * if length is 2 then no product code is comming with message so
             * use the default product for the txn
             * else check that user is entered the product code or not (checking
             * it by neglecting the 2 paramenters as
             * [keyword] [password])
             * else message is not in the proper format
             */
            if (messageLen < 3) {
                throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ERROR_INVALID_REQUEST_FORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }

            if (messageLen == 3) {
                defaultProduct = true;
            } else if ((messageLen - 2) % 2 == 0) {
                validateProduct = true;
            } else {
                throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ERROR_INVALID_REQUEST_FORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }

            try {
                // PIN will be in the last of message
                ChannelUserBL.validatePIN(p_con, channelUserVO, productArray[messageLen - 1]);
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                    throw be;
                }
            }
        } else {
            /*
             * if length is 2 then no product code is comming with message so
             * use the default product for the txn
             * else check that user is entered the product code or not (checking
             * it by neglecting the 2 paramenters as
             * [keyword] [password])
             * else message is not in the proper format
             * [keyword] [qty]
             * 
             * [keyword] [qty] [productcode]
             * 
             * [keyword] [qty] [productcode] [qty] [productcode]
             * If PIN is send then invalid message format will be treated
             */

            if (messageLen < 2) {
                throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ERROR_INVALID_REQUEST_FORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }

            if (messageLen == 2) {
                defaultProduct = true;
            } else if ((messageLen - 1) % 2 == 0) {
                validateProduct = true;
            } else {
                throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ERROR_INVALID_REQUEST_FORMAT, 0, new String[] { p_requestVO
                                .getActualMessageFormat() }, null);
            }
        }

        /*
         * check that the qty for the default product is numeric or not if no
         * then give error else set the default
         * product in the array for txn
         */

        if (defaultProduct) {
            if (!BTSLUtil.isDecimalValue(p_prodArray[1])) {
                final String[] args = { p_prodArray[1] };
                throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ERROR_INVALID_DEFAULT_PRODUCT_QUANTITY, args);
            }
            if (Double.parseDouble(p_prodArray[1]) <= 0) {
                final String[] args = { p_prodArray[1] };
                throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ORDER_CREDIT_AMOUNT_LENGTHINVALID, args);
            }
            final ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
            productArray = new String[2];
            productArray[1] = p_prodArray[1];
            productArray[0] = channelUserTxnDAO.product(p_con, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT)));
        }

        /*
         * if user entered the produt code then check that the qty for the
         * product, is numeric or not
         * if no then give error else check that the product code of the
         * product, is numeric or not
         * if no then give error else set the qty and product code in the array
         * for txn
         */

        else if (validateProduct) {
            int size = 0;
            if ((((ChannelUserVO) p_requestVO.getSenderVO()).getUserPhoneVO()).getPinRequired().equals(PretupsI.YES)) {
                productArray = new String[(messageLen - 2)];
                size = messageLen - 1;
            } else {
                productArray = new String[(messageLen - 1)];
                size = messageLen;
            }

            int j = 0;
            for (int i = 1; i < size; i += 2, j += 2) {
                if (!BTSLUtil.isDecimalValue(p_prodArray[i])) {
                    final String[] args = { p_prodArray[i + 1] };
                    throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ORDER_CREDIT_AMOUNT_LENGTHINVALID, args);
                }
                if (Double.parseDouble(p_prodArray[i]) <= 0) {
                    final String[] args = { p_prodArray[i + 1] };
                    throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ORDER_CREDIT_AMOUNT_LENGTHINVALID, args);
                }
                if (!BTSLUtil.isNumeric(p_prodArray[i + 1])) {
                    final String[] args = { p_prodArray[i + 1] };
                    throw new BTSLBaseException(OrderCreditHandler.class, "process", PretupsErrorCodesI.ERROR_INVALID_PRODUCTCODE_FORMAT, args);
                }
                productArray[j] = p_prodArray[i];
                productArray[j + 1] = p_prodArray[i + 1];
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug("validateUserProductsFormatForSMS", "Exited  " + productArray.length + " p_prodArray ");// +
            // productArray[0]+","+productArray[1]);
        }

        return productArray;
    }

}
