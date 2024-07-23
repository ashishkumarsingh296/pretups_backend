/**
 * @(#)C2CDrCrTransferController.java
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
 *                                    srinath.kotu Nov 15, 2011 Initital
 *                                    Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    This is the controller class for the c2c
 *                                    transfer. process method of this class is
 *                                    called by the channel receiver
 *                                    class and response of the processing is
 *                                    send back to the channel receiver with the
 *                                    required message key.
 */

package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
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
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChnlToChnlTransferTransactionCntrl;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.channel.profile.businesslogic.TransferProfileTxnDAO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class C2CDrCrTransferController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(C2CDrCrTransferController.class.getName());
    public static OperatorUtilI _operatorUtil = null;
    private String _allowedSendMessGatw = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " C2CDrCrTransferController [initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_requestVO: " );
        	loggerValue.append(p_requestVO);
            _log.debug("process", loggerValue );
        }
        final String methodName = "process";
        final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
        UserPhoneVO userPhoneVO = null;
        if (!senderVO.isStaffUser()) {
            userPhoneVO = senderVO.getUserPhoneVO();
        } else {
            userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered Sender VO: " );
        	loggerValue.append(senderVO);
            _log.debug("process",loggerValue);
        }

        // ///
        // [keyword][usercode] [qty] [productcode] [qty] [productcode]
        // [password]
        // 1.) if password is PASSWRD the not to validate the user password
        // 2.) usercode should be numeric
        // 3.) product code should be numeric
        // 4.) qty should be numeric
        // 5.) product code and qty should be always with each other
        // 5.1) If order is for more than one product than product code is
        // required for both of the quantity otherwise
        // for the single product order product code can be optional in this
        // case default product will be taken.
        // 6.) load the receiver information on the base of usercode Check the
        // networkcode of sender and receiver user. both should be same
        // 7.) check the transfer rule. whether transfer is allowed between
        // sender category to receiver category.
        // 8.) check the product code existance.
        // 9.) check product associated with the receiver user.
        // 10.) check the min transfer and max transfer value of the selected
        // product
        // 10.) check the receiver max balance for the product/s.
        // 11.) check the sender min residual balance for the product/s.
        // /

        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserTxnDAO channelUsertxnDAO = null;

        try {

            // Validate if the user is 'out suspended' or not 'out suspended'
            // then show error message
            if (senderVO != null && PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(senderVO.getOutSuspened())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED);
            }

            // checking the receiver user details
            final Date curDate = new Date();
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            channelUsertxnDAO = new ChannelUserTxnDAO();
            ChannelUserVO receiverChannelUserVO = null;
            boolean isUserDetailLoad = false;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            UserPhoneVO PrimaryPhoneVO_R = null;
            final UserDAO userDAO = new UserDAO();
            UserPhoneVO phoneVO = null;
            boolean receiverAllowed = false;
            boolean senderAllowed = false;
            UserStatusVO senderStatusVO = null;
            UserStatusVO receiverStatusVO = null;
            if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
                receiverChannelUserVO = channelUsertxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, p_requestVO.getReceiverExtCode(), null, curDate);
                if (receiverChannelUserVO == null) {
                    throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
                }

                isUserDetailLoad = true;

            } else if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
                receiverChannelUserVO = channelUsertxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null, p_requestVO.getReceiverLoginID(), curDate);
                if (receiverChannelUserVO == null) {
                    throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
                }

                isUserDetailLoad = true;
            }
            if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
                if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
                    if (!p_requestVO.getReceiverExtCode().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
                        throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);
                    }
                }
                if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
                    if (!p_requestVO.getReceiverLoginID().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {
                        throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);
                    }
                }
                if (!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())) {
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                        phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getReceiverMsisdn());
                        if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue() && ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                                PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
                            }
                            receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
                            receiverChannelUserVO.setMsisdn(p_requestVO.getReceiverMsisdn());
                        } else {
                            throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
                        }
                    } else if (!p_requestVO.getReceiverMsisdn().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
                        throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);
                    }
                }

                // To set the msisdn in the request message array...
                if (BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
                    final String message[] = p_requestVO.getRequestMessageArray();
                    final String[] newMessageArr = new String[message.length + 1];
                    for (int j = 0; j < newMessageArr.length - 1; j++) {
                        newMessageArr[j] = message[j];
                    }
                    for (int i = newMessageArr.length; i > 0; i--) {
                        String temp;
                        if (i < newMessageArr.length - 1) {
                            temp = newMessageArr[i];
                            newMessageArr[i + 1] = newMessageArr[i];
                            newMessageArr[i] = temp;
                        }
                    }
                    newMessageArr[1] = receiverChannelUserVO.getMsisdn();
                    p_requestVO.setRequestMessageArray(newMessageArr);
                } else {
                    final String[] mesgArr = p_requestVO.getRequestMessageArray();
                    mesgArr[1] = receiverChannelUserVO.getMsisdn();
                    p_requestVO.setRequestMessageArray(mesgArr);
                }
            }

            final String messageArr[] = p_requestVO.getRequestMessageArray();

            // Checks the messageformat using length as parameter
            if (messageArr.length < 2) {
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            // Validate the user code(mobile number in the message)is it numeric
            // or not
            if (!BTSLUtil.isNumeric(messageArr[1])) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
            }

            String receiverUserCode = messageArr[1];
            /*
             * The following code block is added to check if receiver number is
             * valid
             * This is added by Ankit Zindal 0n date 1/08/06
             */
            receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
            if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
            }
            final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);

            // Validate the user products as the quantity and product code both
            // should be numeric value
            // and if user does not send the product code then use the default
            // product as the requested product

            final String productArray[] = ChannelTransferBL.validateUserProductsFormatForSMS(messageArr, p_requestVO);

            // Validate the user PIN if pin is required and it is not the
            // default pin of the C2S module
            // Manisha
            final int msgLen = messageArr.length;

            if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                try {
                    ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[msgLen - 1]);
                } catch (BTSLBaseException be) {
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                        con.commit();
                    }
                    throw be;
                }
            }

            // Validate the receiver mobile number as the validMSISDN and form
            // the supporting network
            // Getting network details
            final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
                    new String[] { receiverUserCode }, null);
            }

            // Check that is user is Barred in the system as the RECEIVER or not
            // if so then show error message
            final BarredUserDAO barredUserDAO = new BarredUserDAO();

            if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode, PretupsI.USER_TYPE_RECEIVER, null)) {
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
                    new String[] { receiverUserCode }, null);
            }

            if (phoneVO == null) {
                phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
            }
            if (!isUserDetailLoad) {
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                    receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode, true, curDate,false);
                } else {
                    if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                        receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, phoneVO.getUserId(), false, curDate,false);
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
                            PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
                        }
                        receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
                        receiverChannelUserVO.setMsisdn(receiverUserCode);
                    } else {
                        receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode, true, curDate,false);
                    }
                }
            }

            // For Receiver
            // 1. is user exist or not
            // 2. is user active or not
            // 3. is user is IN suspended or not if suspended then show error
            // message

            // Meditel changes.....checking for receiver allowed
            if (receiverChannelUserVO != null) {
                receiverAllowed = false;
                receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(receiverChannelUserVO.getNetworkID(), receiverChannelUserVO.getCategoryCode(),
                    receiverChannelUserVO.getUserType(), p_requestVO.getRequestGatewayType());
                if (receiverStatusVO != null) {
                    final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
                    final String status[] = receiverStatusAllowed.split(",");
                    for (int i = 0; i < status.length; i++) {
                        if (status[i].equals(receiverChannelUserVO.getStatus())) {
                            receiverAllowed = true;
                        }
                    }
                }
            }
            //

            String args[] = { receiverUserCode };
            if (receiverChannelUserVO == null) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                p_requestVO.setMessageArguments(args);
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, args, null);
            } else if (receiverChannelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverChannelUserVO.getInSuspend())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED);
                p_requestVO.setMessageArguments(args);
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED, 0, args, null);
            } else if (receiverStatusVO == null) {
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else if (!receiverAllowed) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
                p_requestVO.setMessageArguments(args);
                throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED, 0, args, null);
            }

            // validating sender and receiver with the transfer rule in this
            // method we are passing various parameters as
            // con - connection for the database access
            // senderVO - VO contains sender's information
            // receiverChannelUserVO - VO contains receiver's information
            // true (p_isUserCode) - To indicate that validation will be done on
            // the user code not on the user name for message display.
            // null (p_forwardPath) - for forwarding the message only in the
            // case of WEB so null is here
            // false (p_isFromWeb) - To indicate that request is not from WEB
            // i.e. form SMS/USSD.
            // txnSubType - Type of the transaction as TRANSFER, RETURN or
            // WITHDRAW. TRANSFER is here
            // This method return boolean value to indicate that whether this is
            // the controlled TXN or uncontrolled TXN.

            // added for DrCrTransfer as setting transfer category was skipped
            // while bypassing commission profile in
            // channeltransferBL.validateSenderAndReceiverWithXfrRule();
            senderVO.setTransferCategory("TRF");
            receiverChannelUserVO.setTransferCategory("TRF");
            final boolean isOutsideHierarchy = true;

            // Now validate the requested product as
            // 1. existence in the system
            // 2. mapping with the network
            // 3. having balance >0
            // 4. Applicable commission profile version
            // 5. product associated with the commission profile
            // 6. product associated with the transfer rule.
            // 7. requested quantity with the minimum transfer value
            // 8. requested quantity with the maximum transfer value
            // 9. requested quantity with the multiple of factor
            // 10. user balance with the requested quantity
            //
            // changed originally referred class to "this" and changed the
            // method='validatereqstprodswtihdefinedprodsforxfr' for DrCrTrf

            final ArrayList productList = this.validateReqstProdsWithDefinedProdsForXFR(con, senderVO, productArray, curDate, p_requestVO.getLocale());

            // /
            // Now load the thresholds VO to validate the user balance (after
            // subtraction of the requested quantity)
            // with the thresholds as
            // 1. first check it with the MAXIMUM PERCENTAGE ALLOWED
            // 2. if not fail at previous point then check it with the MINIMUM
            // RESEDUAL BALANCE
            // /

            ChannelTransferItemsVO channelTransferItemsVO = null;

            final TransferProfileTxnDAO transferProfileTxnDAO = new TransferProfileTxnDAO();
            final ArrayList<TransferProfileProductVO> profileProductList = transferProfileTxnDAO.loadTrfProfileProductWithCntrlValue(con, senderVO.getTransferProfileID());
            TransferProfileProductVO transferProfileProductVO = null;
            final ArrayList<KeyArgumentVO> minProdResidualbalanceList = new ArrayList<KeyArgumentVO>();
            KeyArgumentVO keyArgumentVO = null;

            int maxAllowPct = 0;
            long maxAllowBalance = 0;

            for (int i = 0, k = profileProductList.size(); i < k; i++) {
                transferProfileProductVO = (TransferProfileProductVO) profileProductList.get(i);
                for (int m = 0, n = productList.size(); m < n; m++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
                    if (transferProfileProductVO.getProductCode().equals(channelTransferItemsVO.getProductCode())) {
                        maxAllowPct = transferProfileProductVO.getAllowedMaxPercentageInt();
                        maxAllowBalance = (channelTransferItemsVO.getBalance() * maxAllowPct) / 100;
                        if (maxAllowBalance < channelTransferItemsVO.getRequiredQuantity()) {
                            keyArgumentVO = new KeyArgumentVO();
                            keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_ALLOWMAXPCT);
                            args = new String[] { String.valueOf(maxAllowPct), productArray[m + 1], channelTransferItemsVO.getRequestedQuantity() };
                            keyArgumentVO.setArguments(args);
                            minProdResidualbalanceList.add(keyArgumentVO);
                        } else if (transferProfileProductVO.getMinResidualBalanceAsLong() > (channelTransferItemsVO.getBalance() - channelTransferItemsVO
                            .getRequiredQuantity())) {
                            keyArgumentVO = new KeyArgumentVO();
                            keyArgumentVO.setKey(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS);
                            args = new String[] { transferProfileProductVO.getMinBalance(), productArray[m + 1], channelTransferItemsVO.getRequestedQuantity() };
                            keyArgumentVO.setArguments(args);
                            minProdResidualbalanceList.add(keyArgumentVO);
                        }
                        break;
                    }
                }// end of for
            }
            if (minProdResidualbalanceList != null && !minProdResidualbalanceList.isEmpty()) {
                final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), minProdResidualbalanceList) };
                p_requestVO.setMessageArguments(array);
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG);
                throw new BTSLBaseException(this, "processTransfer", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_RESIDUAL_BALANCE_LESS_MSG, 0, array, null);
            }

            ChannelTransferVO channelTransferVO = new ChannelTransferVO();

            // Now prepare the channelTransferVO from the product list and the
            // sender and receiver VOs

            UserPhoneVO primaryPhoneVO_S = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
                if (!(senderVO.getMsisdn()).equalsIgnoreCase(p_requestVO.getFilteredMSISDN())) {
                    senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
                    senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
                        primaryPhoneVO_S = userDAO.loadUserAnyPhoneVO(con, senderVO.getPrimaryMsisdn());
                    }
                }
                receiverChannelUserVO.setUserCode(receiverUserCode);
            }

            channelTransferVO = this.prepareTransferProfileVO(senderVO, receiverChannelUserVO, productList, curDate);

            channelTransferVO.setControlTransfer(PretupsI.NO);
            channelTransferVO.setSource(p_requestVO.getSourceType());
            channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
            channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
            channelTransferVO.setChannelRemarks(p_requestVO.getPurpose());

            if ((senderVO.getServiceTypes().equalsIgnoreCase((PretupsI.SERVICE_TYPE_CHNL_DRCR_TRANSFER)))) {
                channelTransferVO.setTransactionMode(PretupsI.TRANSACTION_MODE_DRCR_TRANSFER);// added
                // for
                // DRCR
                // transfer
                // to
                // set
                // the
                // transaction
                // mode
                // according
                // to
                // business
                // rule
            }

            // /
            // This is the final method which does the actual transaction as
            // 0. check the thresholds values for the transaction
            // 1. update user daily balances
            // 2. update user balances
            // 3. update thresholds
            // 4. make the transaction
            //
            // /
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Start Transfer Process ");
            }
            final int updateCount = this.approveChannelToChannelTransfer(con, channelTransferVO, isOutsideHierarchy, false, null, curDate);

            // manisha
            if (!senderVO.isStaffUser()) {
                (senderVO.getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
                (senderVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
            } else {
                (senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
                (senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
            }

            if (updateCount > 0) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Commit the data ");
                }
                con.commit();
                // Meditel changes by Ashutosh
                if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                    try {
                        if (mcomCon == null) {
                        	mcomCon = new MComConnection();con=mcomCon.getConnection();
                        }
                        senderAllowed = false;
                        senderStatusVO = (UserStatusVO) UserStatusCache.getObject(senderVO.getNetworkID(), senderVO.getCategoryCode(), senderVO.getUserType(), p_requestVO
                            .getRequestGatewayType());
                        if (senderStatusVO == null) {
                            throw new BTSLBaseException("C2CDrCrTransferController", "process", PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                        } else {
                            final String senderStatusAllowed = senderStatusVO.getUserSenderAllowed();
                            final String status[] = senderStatusAllowed.split(",");
                            for (int i = 0; i < status.length; i++) {
                                if (status[i].equals(senderVO.getStatus())) {
                                    senderAllowed = true;
                                }
                            }
                            PretupsBL.chkAllwdStatusToBecomeActive(con, (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG), senderVO.getUserID(), senderVO.getStatus());
                            PretupsBL.chkAllwdStatusToBecomeActive(con, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG)), receiverChannelUserVO.getUserID(),
                                receiverChannelUserVO.getStatus());

                        }

                    } catch (Exception ex) {
                        _log.error("process", "Exception while changing user state to active  " + ex.getMessage());
                        _log.errorTrace(methodName, ex);
                    } finally {
                        if (con != null) {
                            try {
                                con.commit();
                            } catch (Exception e) {
                                _log.errorTrace("process", e);
                            }
                        }

                    }
                }// end of changes

                //Added for OneLine Logging for Channel
				OneLineTXNLog.log(channelTransferVO, null);
                // prepare balance logger
                this.prepareUserBalancesListForLogger(channelTransferVO);

                // sending sms to receiver
                final String recAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
                String reqruestGW = p_requestVO.getRequestGatewayCode();
                if (!BTSLUtil.isNullString(recAlternetGatewaySMS) && (recAlternetGatewaySMS.split(":")).length >= 2) {
                    if (reqruestGW.equalsIgnoreCase(recAlternetGatewaySMS.split(":")[0])) {
                        reqruestGW = (recAlternetGatewaySMS.split(":")[1]).trim();
                        if (_log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Requested GW was:");
                        	loggerValue.append(p_requestVO.getRequestGatewayCode());
                            _log.debug("process: Reciver Message push through alternate GW", reqruestGW,  loggerValue);
                        }
                    }
                }

                String smsKey = PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER;
                if (PretupsI.TRANSFER_CATEGORY_TRANSFER.equals(senderVO.getTransferCategory())) {
                    // smsKey=PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_RECEIVER_AGENT;
                    if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.CREDIT)) {
                        smsKey = PretupsErrorCodesI.CHNL_CREDIT_TRANSFER_SUCCESS_R;
                        Locale locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                        final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO,
                            PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
                        final String[] array1 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                            .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), p_requestVO.getFilteredMSISDN() };
                        BTSLMessages messages = new BTSLMessages(smsKey, array1);
                        PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVO.getTransferID(), reqruestGW, locale, channelTransferVO
                            .getNetworkCode());
                        pushMessage.push();

                        if (PrimaryPhoneVO_R != null) {
                            locale = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(), PrimaryPhoneVO_R.getCountry());
                            final String[] array2 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                                .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), p_requestVO.getFilteredMSISDN() };
                            messages = new BTSLMessages(smsKey, array2);
                            pushMessage = new PushMessage(receiverChannelUserVO.getPrimaryMsisdn(), messages, channelTransferVO.getTransferID(), reqruestGW, locale,
                                channelTransferVO.getNetworkCode());
                            pushMessage.push();
                        }
                    } else if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.DEBIT)) {
                        smsKey = PretupsErrorCodesI.CHNL_DEBIT_TRANSFER_SUCCESS_S;
                        Locale locale = p_requestVO.getLocale();
                        final Object[] smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiverForC2C(con, channelTransferVO,
                            PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_TXNSUBKEY, PretupsErrorCodesI.C2S_CHNL_CHNL_TRANSFER_RECEIVER_BALSUBKEY);
                        final String[] array1 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                            .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn() };
                        BTSLMessages messages = new BTSLMessages(smsKey, array1);
                        PushMessage pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), messages, channelTransferVO.getTransferID(), reqruestGW, locale,
                            channelTransferVO.getNetworkCode());
                        pushMessage.push();

                        if (PrimaryPhoneVO_R != null) {
                            locale = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(), PrimaryPhoneVO_R.getCountry());
                            final String[] array2 = { BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]), channelTransferVO
                                .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), p_requestVO.getFilteredMSISDN() };
                            messages = new BTSLMessages(smsKey, array2);
                            pushMessage = new PushMessage(receiverChannelUserVO.getPrimaryMsisdn(), messages, channelTransferVO.getTransferID(), reqruestGW, locale,
                                channelTransferVO.getNetworkCode());
                            pushMessage.push();
                        }
                    }
                }

                // sending sms to sender
                PushMessage pushMessage = null;
                final ArrayList<ChannelTransferItemsVO> itemsList = channelTransferVO.getChannelTransferitemsVOList();
                final ArrayList<KeyArgumentVO> txnList = new ArrayList<KeyArgumentVO>();
                final ArrayList<KeyArgumentVO> balList = new ArrayList<KeyArgumentVO>();
                args = null;
                // String smsKey=null;
                if (senderVO.isStaffUser() && senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())) {
                    smsKey = PretupsErrorCodesI.CHNL_TRF_SUCCESS_STAFF;
                } else {
                    if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.CREDIT)) {
                        smsKey = PretupsErrorCodesI.CHNL_CREDIT_TRANSFER_SUCCESS_S;
                    }
                    if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.DEBIT)) {
                        smsKey = PretupsErrorCodesI.CHNL_DEBIT_TRANSFER_SUCCESS_R;
                    }
                }

                final int lSize = itemsList.size();
                for (int i = 0; i < lSize; i++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_TXNSUBKEY);
                    args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), channelTransferItemsVO.getRequestedQuantity() };
                    keyArgumentVO.setArguments(args);
                    txnList.add(keyArgumentVO);

                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_SUCCESS_BALSUBKEY);
                    args = new String[] { String.valueOf(channelTransferItemsVO.getShortName()), PretupsBL.getDisplayAmount(channelTransferItemsVO
                        .getAfterTransSenderPreviousStock() - channelTransferItemsVO.getRequiredQuantity()) };
                    keyArgumentVO.setArguments(args);
                    balList.add(keyArgumentVO);
                }// end of for
                String[] array = null;
                if (senderVO.isStaffUser() && senderVO.getUserPhoneVO().getMsisdn().equals(p_requestVO.getMessageSentMsisdn())) {
                    array = new String[] { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                        .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()), phoneVO.getMsisdn(), senderVO.getStaffUserDetails()
                        .getUserName() };
                } else {
                    if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.CREDIT)) {
                        array = new String[] { BTSLUtil.getMessage(p_requestVO.getLocale(), txnList), BTSLUtil.getMessage(p_requestVO.getLocale(), balList), channelTransferVO
                            .getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()), phoneVO.getMsisdn() };
                    }
                    if (p_requestVO.getTxnType().equalsIgnoreCase(PretupsI.DEBIT)) {
                        array = new String[] { BTSLUtil.getMessage(new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry()), txnList), BTSLUtil.getMessage(new Locale(
                            phoneVO.getPhoneLanguage(), phoneVO.getCountry()), balList), channelTransferVO.getTransferID(), PretupsBL.getDisplayAmount(channelTransferVO
                            .getRequestedQuantity()), senderVO.getUserPhoneVO().getMsisdn() };
                    }
                }

                p_requestVO.setMessageArguments(array);
                p_requestVO.setMessageCode(smsKey);
                p_requestVO.setTransactionID(channelTransferVO.getTransferID());
                _allowedSendMessGatw = BTSLUtil.NullToString(Constants.getProperty("C2C_SEN_MSG_REQD_GW"));
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
                    if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(p_requestVO.getServiceType()) && p_requestVO.isSenderMessageRequired()) {
                        if (primaryPhoneVO_S != null && PretupsI.CREDIT.equalsIgnoreCase(p_requestVO.getTxnType())) {
                            final Locale locale1 = new Locale(primaryPhoneVO_S.getPhoneLanguage(), primaryPhoneVO_S.getCountry());
                            final String senderMessage = BTSLUtil.getMessage(locale1, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                            pushMessage = new PushMessage(senderVO.getPrimaryMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                                locale1);
                            pushMessage.push();
                        }
                        if (PrimaryPhoneVO_R != null && PretupsI.DEBIT.equalsIgnoreCase(p_requestVO.getTxnType())) {
                            final Locale locale1 = new Locale(PrimaryPhoneVO_R.getPhoneLanguage(), PrimaryPhoneVO_R.getCountry());
                            final String senderMessage = BTSLUtil.getMessage(locale1, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                            pushMessage = new PushMessage(receiverChannelUserVO.getPrimaryMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO
                                .getRequestGatewayCode(), locale1);
                            pushMessage.push();
                        }
                    }
                }
                if (BTSLUtil.isStringIn(p_requestVO.getRequestGatewayCode(), _allowedSendMessGatw)) {
                    if (PretupsI.CREDIT.equalsIgnoreCase(p_requestVO.getTxnType())) {
                        final String senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                        pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(),
                            p_requestVO.getLocale());
                    } else if (PretupsI.DEBIT.equalsIgnoreCase(p_requestVO.getTxnType())) {
                        final Locale locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
                        final String senderMessage = BTSLUtil.getMessage(locale, p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                        pushMessage = new PushMessage(phoneVO.getMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO
                            .getLocale());
                    }
                    pushMessage.push();
                }
                int messageLength = 0;
                final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
                if (!BTSLUtil.isNullString(messLength)) {
                    messageLength = (new Integer(messLength)).intValue();
                }
                if (!reqruestGW.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
                    String senderMessage = null;
                    if (PretupsI.CREDIT.equalsIgnoreCase(p_requestVO.getTxnType())) {
                        senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                        pushMessage = new PushMessage(p_requestVO.getFilteredMSISDN(), senderMessage, p_requestVO.getRequestIDStr(), reqruestGW, p_requestVO.getLocale());
                    } else if (PretupsI.DEBIT.equalsIgnoreCase(p_requestVO.getTxnType())) {
                        senderMessage = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                        pushMessage = new PushMessage(phoneVO.getMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), reqruestGW, p_requestVO.getLocale());
                    }
                    // p_requestVO.setRequestGatewayCode(reqruestGW);
                    if ((messageLength > 0) && (senderMessage.length() < messageLength)) {
                        ;
                    }
                    pushMessage.push();
                }
                if (PretupsI.DEBIT.equalsIgnoreCase(p_requestVO.getTxnType())) {
                    p_requestVO.setFilteredMSISDN(p_requestVO.getReceiverMsisdn());
                    p_requestVO.setMessageSentMsisdn(p_requestVO.getReceiverMsisdn());
                }
                return;
            }
            con.rollback();
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_USER_TRANSFER);
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("BTSLBaseException " );
            loggerValue.append(be.getMessage());
            _log.error("process", loggerValue);
            if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
                final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
                p_requestVO.setMessageArguments(array);
            }
            if (be.getArgs() != null) {
                p_requestVO.setMessageArguments(be.getArgs());
            }

            if (be.getMessageKey() != null) {
                p_requestVO.setMessageCode(be.getMessageKey());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            }
            _log.errorTrace(methodName, be);
            return;
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            loggerValue.setLength(0);
            loggerValue.append("BTSLBaseException ");
            loggerValue.append(e.getMessage());
            _log.error("process", loggerValue );
            _log.errorTrace(methodName, e);
            
            loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CDrCrTransferController[process]", "", "", "",
            		loggerValue.toString());
            p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            return;
        } finally {
        	if(mcomCon != null){mcomCon.close("C2CDrCrTransferController#process");mcomCon=null;}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
    }// end of process

    /**
     * Method prepareTransferProfileVO
     * This method construct the VO for the Txn
     * 
     * @param p_senderVO
     * @param p_receiverVO
     * @param p_productList
     * @param p_curDate
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */
    private ChannelTransferVO prepareTransferProfileVO(ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO, ArrayList p_productList, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        final String methodName = "prepareTransferProfileVO";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered  p_senderVO: ");
        	loggerValue.append(p_senderVO);
        	loggerValue.append(" p_receiverVO:");
        	loggerValue.append(p_receiverVO);
        	loggerValue.append(" p_productList:");
        	loggerValue.append(p_productList.size());
        	loggerValue.append(" p_curDate:");
        	loggerValue.append(p_curDate);
            _log.debug(methodName,loggerValue );
        }

        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();

        channelTransferVO.setNetworkCode(p_senderVO.getNetworkID());
        channelTransferVO.setNetworkCodeFor(p_senderVO.getNetworkID());
        channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
        channelTransferVO.setDomainCode(p_senderVO.getDomainID());
        channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
        channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
        channelTransferVO.setReceiverGradeCode(p_receiverVO.getUserGrade());
        channelTransferVO.setFromUserID(p_senderVO.getUserID());
        channelTransferVO.setToUserID(p_receiverVO.getUserID());
        channelTransferVO.setTransferDate(p_curDate);
        channelTransferVO.setCommProfileSetId(p_receiverVO.getCommissionProfileSetID());
        channelTransferVO.setCommProfileVersion(p_receiverVO.getCommissionProfileSetVersion());
        channelTransferVO.setDualCommissionType(p_receiverVO.getDualCommissionType());
        channelTransferVO.setCreatedOn(p_curDate);
        channelTransferVO.setCreatedBy(p_senderVO.getUserID());
        channelTransferVO.setModifiedOn(p_curDate);
        channelTransferVO.setModifiedBy(p_senderVO.getUserID());
        channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        channelTransferVO.setTransferInitatedBy(p_senderVO.getUserID());
        channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
        channelTransferVO.setReceiverTxnProfile(p_receiverVO.getTransferProfileID());
        channelTransferVO.setReceiverCategoryCode(p_receiverVO.getCategoryCode());
        channelTransferVO.setTransferCategory(p_senderVO.getTransferCategory());

        // adding the some additional information for sender/reciever
        channelTransferVO.setReceiverGgraphicalDomainCode(p_receiverVO.getGeographicalCode());
        channelTransferVO.setReceiverDomainCode(p_receiverVO.getDomainID());
        channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
        channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));

        ChannelTransferItemsVO channelTransferItemsVO = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        for (int i = 0, k = p_productList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_productList.get(i);
            totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
            totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            totPayAmt += channelTransferItemsVO.getPayableAmount();
            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
            _log.debug(methodName, "$$$$$$$" + channelTransferItemsVO.getNetPayableAmount());
            totTax1 += channelTransferItemsVO.getTax1Value();
            totTax2 += channelTransferItemsVO.getTax2Value();
            totTax3 += channelTransferItemsVO.getTax3Value();
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
        }// end of for
        channelTransferVO.setRequestedQuantity(totRequestQty);
        channelTransferVO.setTransferMRP(totMRP);
        channelTransferVO.setPayableAmount(totPayAmt);
        channelTransferVO.setNetPayableAmount(totNetPayAmt);
        loggerValue.setLength(0);
    	loggerValue.append("******");
    	loggerValue.append(channelTransferItemsVO.getNetPayableAmount());
        _log.debug(methodName,  loggerValue);
        channelTransferVO.setTotalTax1(totTax1);
        channelTransferVO.setTotalTax2(totTax2);
        channelTransferVO.setTotalTax3(totTax3);
        channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
        channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));

        channelTransferVO.setChannelTransferitemsVOList(p_productList);
        channelTransferVO.setActiveUserId(p_senderVO.getActiveUserID());

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exited  ");
        }

        return channelTransferVO;
    }// end of prepareTransferProfileVO

    // this method validates the product list for the defined products for a
    // specific user, while bypassing commission profiles.

    public ArrayList validateReqstProdsWithDefinedProdsForXFR(Connection p_con, ChannelUserVO p_senderVO, String[] p_productArr, Date p_curDate, Locale p_locale) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_productArr Length ");
        	loggerValue.append(p_productArr.length);
        	loggerValue.append(" SenderVO ");
        	loggerValue.append(p_senderVO);
        	loggerValue.append(" p_curDate: ");
        	loggerValue.append(p_curDate);
            _log.debug("validateReqstProdsWithDefinedProdsForXFR",loggerValue );
        }

        final ArrayList<ChannelTransferItemsVO> tempProductList = this.loadC2CXfrProductsWithXfrRule(p_con, p_senderVO.getUserID(), p_senderVO.getNetworkID(), p_curDate,
            p_senderVO.getTransferRuleID(), null, false, p_senderVO.getUserCode(), p_locale, null);
        // ListValueVO
        // tempProductVO=NetworkProductServiceTypeCache.getProductServiceValueVO(p_senderVO.getServiceTypes());
        ChannelTransferItemsVO channelTransferItemsVO = null;
        final ArrayList<String> notMatchedProdList = new ArrayList<String>();
        final ArrayList<KeyArgumentVO> minLessProdList = new ArrayList<KeyArgumentVO>();
        final ArrayList<KeyArgumentVO> maxMoreProdList = new ArrayList<KeyArgumentVO>();
        final ArrayList<KeyArgumentVO> balanceList = new ArrayList<KeyArgumentVO>();
        final ArrayList<KeyArgumentVO> multipleOfList = new ArrayList<KeyArgumentVO>();
        final ArrayList<ChannelTransferItemsVO> productList = new ArrayList<ChannelTransferItemsVO>();

        int prodCode = 0;
        int m, n;
        for (int i = 0, j = tempProductList.size(); i < j; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) tempProductList.get(i);

            // To check whether product selected by user exists in his
            // productlist or not.

            for (m = 0, n = p_productArr.length; m < n; m += 2) {
                prodCode = Integer.parseInt(p_productArr[m + 1]);
                if (channelTransferItemsVO.getProductShortCode() == prodCode) {

                    channelTransferItemsVO.setRequestedQuantity(p_productArr[m]);
                    channelTransferItemsVO.setRequiredQuantity(PretupsBL.getSystemAmount(p_productArr[m]));
                    channelTransferItemsVO.setPayableAmount(BTSLUtil.parseDoubleToLong(channelTransferItemsVO.getUnitValue() * Double.parseDouble(p_productArr[m])));
                    productList.add(channelTransferItemsVO);
                    break;
                }
            }
        }

        if (notMatchedProdList!= null && !notMatchedProdList.isEmpty()) {
            final String prodArr[] = new String[notMatchedProdList.size()];
            notMatchedProdList.toArray(prodArr);
            throw new BTSLBaseException(ChannelTransferBL.class, "validateReqstProdsWithDefinedProdsForXFR", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NOT_ALLOWED,
                prodArr);
        } else if (minLessProdList != null && !minLessProdList.isEmpty()) {
            final String[] array = { BTSLUtil.getMessage(p_locale, minLessProdList) };
            throw new BTSLBaseException(ChannelTransferBL.class, "validateReqstProdsWithDefinedProdsForXFR", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MIN_TRANSFER,
                array);
        } else if (maxMoreProdList != null && !maxMoreProdList.isEmpty()) {
            final String[] array = { BTSLUtil.getMessage(p_locale, maxMoreProdList) };
            throw new BTSLBaseException(ChannelTransferBL.class, "validateReqstProdsWithDefinedProdsForXFR", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MAX_TRANSFER,
                array);
        } else if (multipleOfList != null && !multipleOfList.isEmpty()) {
            final String[] array = { BTSLUtil.getMessage(p_locale, multipleOfList) };
            throw new BTSLBaseException(ChannelTransferBL.class, "validateReqstProdsWithDefinedProdsForXFR", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_MULTIPLE_OF, array);
        } else if (balanceList != null && !balanceList.isEmpty()) {
            final String[] array = { BTSLUtil.getMessage(p_locale, balanceList) };
            throw new BTSLBaseException(ChannelTransferBL.class, "validateReqstProdsWithDefinedProdsForXFR", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_LESS_BALANCE,
                array);
        }

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exited  with product list size==");
        	loggerValue.append(productList.size());
            _log.debug("validateReqstProdsWithDefinedProdsForXFR",  loggerValue );
        }
        return productList;
    }

    public ArrayList<ChannelTransferItemsVO> loadC2CXfrProductsWithXfrRule(Connection p_con, String p_userID, String p_networkCode, Date p_currentDate, String p_transferRuleID, String p_forwardPath, boolean isFromWeb, String p_userNameCode, Locale p_locale, String p_productType) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   UserID: ");
        	loggerValue.append(p_userID);
        	loggerValue.append(" NetworkCode:");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" CurrentDate: " );
        	loggerValue.append(p_currentDate);
        	loggerValue.append(" p_transferRuleID:");
        	loggerValue.append(p_transferRuleID);
        	loggerValue.append(" isFromWeb: ");
        	loggerValue.append(isFromWeb);
        	loggerValue.append(" p_userIDCODE: " );
        	loggerValue.append(p_userNameCode);
        	loggerValue.append(",p_locale=" );
        	loggerValue.append(p_locale);
        	loggerValue.append(" p_productType=");
        	loggerValue.append(p_productType);
            _log.debug( "loadC2CXfrProductsWithXfrRule",loggerValue);
        }

        final ArrayList<ChannelTransferItemsVO> productList = new ArrayList<ChannelTransferItemsVO>();
        final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        final String args[] = { p_userNameCode };
        final ArrayList<NetworkProductVO> prodList = networkProductDAO.loadProductListForXfr(p_con, p_productType, p_networkCode);
        /*
         * 1. check whether product exist or not of the input productType
         */
        if (prodList.isEmpty()) {
            if (isFromWeb) {
                if (!BTSLUtil.isNullString(p_productType)) {
                    throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductsWithXfrRule", "message.transfer.nodata.producttype", 0,
                        new String[] { p_productType }, p_forwardPath);
                }
                throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductsWithXfrRule", "message.transferc2c.nodata.product", p_forwardPath);
            }
            throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductsWithXfrRule", PretupsErrorCodesI.ERROR_USER_TRANSFER_NOPRODUCT_EXIST);
        }

        /*
         * 2.
         * checking that the status of network product mapping is active and
         * also construct the new arrayList of
         * channelTransferItemsVOs containing required list.
         */
        ChannelTransferItemsVO channelTransferItemsVO = null;
        NetworkProductVO networkProductVO = null;
        int i, j, m, n;
        for (i = 0, j = prodList.size(); i < j; i++) {
            networkProductVO = (NetworkProductVO) prodList.get(i);
            if (networkProductVO.getStatus().equals(PretupsI.STATUS_ACTIVE)) {
                channelTransferItemsVO = new ChannelTransferItemsVO();
                channelTransferItemsVO.setProductType(networkProductVO.getProductType());
                channelTransferItemsVO.setProductCode(networkProductVO.getProductCode());
                channelTransferItemsVO.setProductName(networkProductVO.getProductName());
                channelTransferItemsVO.setShortName(networkProductVO.getShortName());
                channelTransferItemsVO.setProductShortCode(networkProductVO.getProductShortCode());
                channelTransferItemsVO.setProductCategory(networkProductVO.getProductCategory());
                channelTransferItemsVO.setErpProductCode(networkProductVO.getErpProductCode());
                channelTransferItemsVO.setStatus(networkProductVO.getStatus());
                channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
                channelTransferItemsVO.setModuleCode(networkProductVO.getModuleCode());
                channelTransferItemsVO.setProductUsage(networkProductVO.getProductUsage());
                productList.add(channelTransferItemsVO);
            }
        }
        if (productList.isEmpty()) {
            if (isFromWeb) {
                throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductsWithXfrRule", "message.transferc2c.nodata.networkproductmapping", p_forwardPath);
            }
            throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductsWithXfrRule", PretupsErrorCodesI.ERROR_USER_TRANSFER_NOTMAPPED_NETWORK);
        }

        /*
         * 3. load the product's BALANCE
         */
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final ArrayList<UserBalancesVO> userBalancesList = channelUserDAO.loadUserBalances(p_con, p_networkCode, p_networkCode, p_userID);
        if (userBalancesList == null || userBalancesList.isEmpty()) {
            if (isFromWeb) {
                throw new BTSLBaseException(ChannelTransferBL.class, "loadO2CXfrProductList", "message.transfer.c2c.noproductassigned", 0, args, p_forwardPath);
            }
            throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductsWithXfrRule", PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE, args);
        }
        /*
         * 4. find the products having the balance >0 and set the balance to the
         * product list
         */

        boolean validProductFound = false;
        boolean productFound = false;
        final ArrayList<KeyArgumentVO> errorList = new ArrayList<KeyArgumentVO>();
        KeyArgumentVO keyArgumentVO = null;
        UserBalancesVO balancesVO = null;
        String errArgs[] = null;
        for (i = 0, j = userBalancesList.size(); i < j; i++) {
            balancesVO = (UserBalancesVO) userBalancesList.get(i);
            if (balancesVO.getBalance() <= 0) {
                userBalancesList.remove(i);
                i--;
                j--;
                // add "product balance <=0" message in the list
                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_USER_BALANCE_NOT_EXIST_SUBKEY);
                /*
                 * if(isFromWeb)
                 * errArgs = new String[]{balancesVO.getProductShortName()};
                 * else
                 */
                errArgs = new String[] { balancesVO.getProductShortName() };
                keyArgumentVO.setArguments(errArgs);
                errorList.add(keyArgumentVO);
                continue;
            }
            productFound = false;
            for (m = 0, n = productList.size(); m < n; m++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
                if (channelTransferItemsVO.getProductCode().equals(balancesVO.getProductCode())) {
                    validProductFound = true;
                    productFound = true;
                    channelTransferItemsVO.setBalance(balancesVO.getBalance());
                    break;
                }
            }
            if (!productFound) {
                // add "product is suspended" message in the list
                keyArgumentVO = new KeyArgumentVO();
                keyArgumentVO.setKey(PretupsErrorCodesI.CHNL_TRANSFER_ERROR_PRODUCT_SUSPENDED_SUBKEY);
                /*
                 * if(isFromWeb)
                 * errArgs = new String[]{balancesVO.getProductShortName()};
                 * else
                 */
                errArgs = new String[] { balancesVO.getProductShortName() };
                keyArgumentVO.setArguments(errArgs);
                errorList.add(keyArgumentVO);

            }
        }
        if (!validProductFound) {
            final String[] array = { p_userNameCode, BTSLUtil.getMessage(p_locale, errorList) };
            if (isFromWeb) {
                throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductList", "message.transferc2c.nodata.nobalance", 0, array, p_forwardPath);
            }
            throw new BTSLBaseException(ChannelTransferBL.class, "loadC2CXfrProductsWithXfrRule",
                PretupsErrorCodesI.ERROR_USER_TRANSFER_PRODUCT_NO_BALANCE_IN_COMMPROFILE_NTWMAPPING, array);
        }

        // reomve the products form the list having no balance
        for (m = 0, n = productList.size(); m < n; m++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(m);
            if (channelTransferItemsVO.getBalance() <= 0) {
                productList.remove(m);
                m--;
                n--;
            }
        }

        if (_log.isDebugEnabled()) {
            _log.debug("loadC2CXfrProductsWithXfrRule", "Exited  ");
        }
        return productList;
    }

    public void prepareUserBalancesListForLogger(ChannelTransferVO p_channelTransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareUserBalancesListForLogger", "Entered ChannelTransferVO =" + p_channelTransferVO);
        }
        if (PretupsI.TRANSFER_TYPE_C2C.equals(p_channelTransferVO.getType())) {
            prepareC2CDrCrTxnBalanceLogger(p_channelTransferVO);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("prepareUserBalancesListForLogger", "Exited  ");
        }

    }

    private void prepareC2CDrCrTxnBalanceLogger(ChannelTransferVO p_channelTransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareC2CDrCrTxnBalanceLogger", "Entered ChannelTransferVO =" + p_channelTransferVO);
        }

        final ArrayList<ChannelTransferItemsVO> itemsList = p_channelTransferVO.getChannelTransferitemsVOList();

        UserBalancesVO balancesVO = null;
        ChannelTransferItemsVO itemsVO = null;
        for (int i = 0, k = itemsList.size(); i < k; i++) {
            itemsVO = (ChannelTransferItemsVO) itemsList.get(i);
            balancesVO = new UserBalancesVO();
            balancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
            balancesVO.setUserID(p_channelTransferVO.getFromUserID());
            balancesVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
            balancesVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
            balancesVO.setProductCode(itemsVO.getProductCode());
            balancesVO.setRequestedQuantity(String.valueOf(itemsVO.getRequiredQuantity()));
            balancesVO.setQuantityToBeUpdated(itemsVO.getRequiredQuantity());
            balancesVO.setLastTransferType(p_channelTransferVO.getTransferSubType());
            balancesVO.setTransferCategory(p_channelTransferVO.getTransferCategory());
            balancesVO.setType(p_channelTransferVO.getType());
            balancesVO.setSource(p_channelTransferVO.getSource());
            balancesVO.setCreatedBy(p_channelTransferVO.getCreatedBy());
            balancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
            balancesVO.setNetAmount(itemsVO.getNetPayableAmount());
            balancesVO.setPreviousBalance(itemsVO.getAfterTransSenderPreviousStock());
            balancesVO.setEntryType(PretupsI.DEBIT);
            balancesVO.setBalance(balancesVO.getPreviousBalance() - itemsVO.getRequiredQuantity());
            balancesVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());
            BalanceLogger.log(balancesVO);

            balancesVO.setUserID(p_channelTransferVO.getToUserID());
            balancesVO.setPreviousBalance(itemsVO.getAfterTransReceiverPreviousStock());
            balancesVO.setEntryType(PretupsI.CREDIT);
            balancesVO.setBalance(balancesVO.getPreviousBalance() + itemsVO.getRequiredQuantity());
            balancesVO.setUserMSISDN(p_channelTransferVO.getToUserCode());
            BalanceLogger.log(balancesVO);

            if (_log.isDebugEnabled()) {
                _log.debug("prepareC2CDrCrTxnBalanceLogger", "Exited  ");
            }

        }
    }

    /**
     * Field _log.
     */

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_isOutSideHierarchy
     * @param p_fromWEB
     * @param p_forwardPath
     * @param p_curDate
     *            TODO
     * @return int
     * @throws BTSLBaseException
     */
    public int approveChannelToChannelTransfer(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_isOutSideHierarchy, boolean p_fromWEB, String p_forwardPath, Date p_curDate) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_channelTransferVO: ");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_isOutSideHirearchy: ");
        	loggerValue.append(p_isOutSideHierarchy );
        	loggerValue.append(" fromWeb :");
        	loggerValue.append(p_fromWEB);
        	loggerValue.append(" p_forwardPath ");
        	loggerValue.append(p_forwardPath);
        	_log.debug("approveChannelToChannelTransfer",loggerValue);
        }

        int updateCount = 0;

        /*
         * check the user transfer counts
         * Here if Txn is outSide hierarchy then counters are different
         * else counters are different
         */
        if (p_isOutSideHierarchy && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL)).booleanValue()) {

            /*
             * checks the outside out counts and if there is a error message
             * returned then show the error message
             * other wise checks the outside in coutns if there is a error
             * message returned then show the
             * error message
             */
            final String messageOutsideOutCountCheck = ChannelTransferBL.checkOutsideTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO
                .getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutsideOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageOutsideInCountCheck = ChannelTransferBL.checkOutsideTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO
                .getReceiverTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutsideInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutsideInCountCheck, 0, args, p_forwardPath);
            }
        } else {
            /*
             * checks the out counts and if there is a error message returned
             * then show the error message
             * other wise checks the in coutns if there is a error message
             * returned then show the
             * error message
             */
            final String messageOutCountCheck = ChannelTransferBL.checkTransferOutCounts(p_con, p_channelTransferVO.getFromUserID(),
                p_channelTransferVO.getSenderTxnProfile(), p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageOutCountCheck != null) {
                String arg = p_channelTransferVO.getFromUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getFromUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageOutCountCheck, 0, args, p_forwardPath);
            }

            final String messageInCountCheck = ChannelTransferBL.checkTransferINCounts(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getReceiverTxnProfile(),
                p_channelTransferVO.getNetworkCode(), false, p_curDate, p_channelTransferVO.getTransferMRP());
            if (messageInCountCheck != null) {
                String arg = p_channelTransferVO.getToUserCode();
                if (p_fromWEB) {
                    arg = p_channelTransferVO.getToUserName();
                }
                final String args[] = { arg };
                throw new BTSLBaseException(ChnlToChnlTransferTransactionCntrl.class, "approveChannelToChannelTransfer", messageInCountCheck, 0, args, p_forwardPath);
            }
        }
        /*
         * generate the TXN ID for the txn as TRANSFER/RETURN/WITHDRAW
         */

        ChannelTransferBL.genrateDrCrChnnlTrfID(p_con, p_channelTransferVO);

        /*
         * Now update user balances in all the updation method we are checking
         * for the update count
         * if it is lessthan or equal to 0 then throw exception so no need to
         * check here
         * first update daily balances of both of the user
         * then debit the Sender user
         * then credit the receiver user
         * and then update the thresholds of the users
         * at last insert the TXN data in the parent and child tables.
         */
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        final UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(p_channelTransferVO);
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);
        userBalancesVO.setUserID(p_channelTransferVO.getToUserID());
        userBalancesDAO.updateUserDailyBalances(p_con, p_curDate, userBalancesVO);

        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

        final ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
        // debit the sender balances
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            updateCount = channelUserDAO.debitUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);

        } else {
            updateCount = channelUserDAO.debitUserBalances(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);

        }

        // added the following commands to set quantity for crediting user after
        // bypassing commission
        ChannelTransferItemsVO channelTransferItemsVO = null;
        final ArrayList p_transferItemsList = p_channelTransferVO.getChannelTransferitemsVOList();
        channelTransferItemsVO = (ChannelTransferItemsVO) p_transferItemsList.get(0);
        channelTransferItemsVO.setReceiverCreditQty((channelTransferItemsVO.getRequiredQuantity()));
        channelTransferItemsVO.setApprovedQuantity((channelTransferItemsVO.getRequiredQuantity()));

        // credit the receiver

        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        } else {
            updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, p_fromWEB, p_forwardPath);
        }
        p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_C2C);
        if (p_isOutSideHierarchy && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SEP_OUTSIDE_TXN_CTRL)).booleanValue()) {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferOutSideCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        } else {
            updateCount = ChannelTransferBL.updateChannelToChannelTransferCounts(p_con, p_channelTransferVO, p_curDate, p_fromWEB, p_forwardPath);
        }

        if (p_fromWEB) {
            // commented as disscussed with Sanjay Sir, GSB, AC need not to be
            // updated in WEB

            // updateCount=channelUserDAO.updateUserPhoneAfterTxn(p_con,p_channelTransferVO,p_channelTransferVO.getFromUserCode(),p_channelTransferVO.getFromUserID(),true);
            // updateCount=channelUserDAO.updateUserPhoneAfterTxn(p_con,p_channelTransferVO,p_channelTransferVO.getToUserCode(),p_channelTransferVO.getToUserID(),true);
        } else {
            updateCount = channelUserTxnDAO.updateUserPhoneAfterTxn(p_con, p_channelTransferVO, p_channelTransferVO.getToUserCode(), p_channelTransferVO.getToUserID(), false);
        }
        // insert the TXN data in the parent and child tables.
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        updateCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exit updateCount =");
        	loggerValue.append(updateCount);
            _log.debug("approveChannelToChannelTransfer",  loggerValue );
        }

        return updateCount;

    }

    /**
     * Method constructBalanceVOFromTxnVO.
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return UserBalancesVO
     */

    private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
    }

}
