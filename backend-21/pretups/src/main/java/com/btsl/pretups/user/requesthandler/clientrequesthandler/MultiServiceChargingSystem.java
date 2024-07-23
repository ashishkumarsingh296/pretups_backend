package com.btsl.pretups.user.requesthandler.clientrequesthandler;

/**
 * @(#)MultiServiceChargingSystem.java
 *                                     Name Date History
 *                                     ----------------------------------------
 *                                     --------------------------------
 *                                     Harsh Deep Dixit 15/01/2015 Initial
 *                                     Creation
 * 
 *                                     ----------------------------------------
 *                                     --------------------------------
 *                                     Copyright (c) 2015 Mahindra Comviva
 *                                     Technologies Ltd.
 *                                     Controller class for handling the
 *                                     charging of different services like
 *                                     (balance enquiry, change language)
 */

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.ChannelRequestDailyLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingDAO;
import com.btsl.pretups.master.businesslogic.SelectorAmountMappingVO;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class MultiServiceChargingSystem implements ServiceKeywordControllerI {

    private String _transferID;
    private String _requestIDStr;
    private String _senderMSISDN;
    private String _senderNetworkCode;
    private String _senderSubscriberType;
    private String _senderAllServiceClassID = PretupsI.ALL;
    private boolean _transferDetailAdded = false;
    private boolean _creditBackEntryDone = false;
    private boolean _transferEntryReqd = false;
    private C2STransferVO _c2sTransferVO = null;
    private ArrayList<TransferItemVO> _itemList = null;
    private C2STransferItemVO _senderTransferItemVO = null;
    private C2STransferItemVO _receiverTransferItemVO = null;
    private MSISDNPrefixInterfaceMappingVO interfaceMappingVO = null;
    private ChannelUserVO _channelUserVO;
    private UserBalancesVO _userBalancesVO = null;
    private Date _currentDate = null;
    private Locale _senderLocale = null;
    private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
    private static int _prevMinut = 0;
    private static int _transactionIDCounter = 0;
    /**
     * Field SRVCHARG_TRANSFER_ID_PAD_LENGTH. for the TXN_ID of the Service
     * Charging
     */
    private static int SRVCHARG_TRANSFER_ID_PAD_LENGTH = 4;
    private static final Log LOG = LogFactory.getLog(MultiServiceChargingSystem.class.getName());

    public void process(RequestVO p_requestVO) {
        final String methodName = "process";
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {

            if (LOG.isDebugEnabled()) {
                LOG.debug("MultiServiceChargingSystem", methodName + ":: ", "Entered");
            }
            UserBalancesVO _userBalancesVO = null;
            _c2sTransferVO = new C2STransferVO();
            _senderTransferItemVO = new C2STransferItemVO();
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            _currentDate = new Date();
            _senderLocale = p_requestVO.getSenderLocale();
            _requestIDStr = p_requestVO.getRequestIDStr();
            _senderNetworkCode = _channelUserVO.getNetworkCode();
            _senderMSISDN = _channelUserVO.getUserPhoneVO().getMsisdn();
            SelectorAmountMappingVO amountMappingVO = null;
            String[] strArr = null;
            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(_senderMSISDN, PretupsI.USER_TYPE_SENDER);
            if (networkPrefixVO == null) {
                strArr = new String[] { _senderMSISDN };
                throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND, 0, strArr, null);
            }
            _senderSubscriberType = networkPrefixVO.getSeriesType();
            final String[] reqArr = p_requestVO.getRequestMessageArray();
            reqArr[0] = "C" + reqArr[0];
            p_requestVO.setRequestMessageArray(reqArr);
            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(p_requestVO);

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if (serviceKeywordCacheVO == null) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, this.getClass().getName() + "[" + methodName + "]",
                                p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Service keyword not found for the keyword=" + p_requestVO
                                                .getRequestMessageArray()[0] + " For Gateway Type=" + p_requestVO.getRequestGatewayType() + "Service Port=" + p_requestVO
                                                .getServicePort());
                throw new BTSLBaseException("MultiServiceChargingSystem", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, this.getClass().getName() + "[" + methodName + "]",
                                p_requestVO.getRequestIDStr(), p_requestVO.getFilteredMSISDN(), "", "Service keyword suspended for the keyword=" + p_requestVO
                                                .getRequestMessageArray()[0] + " For Gateway Type=" + p_requestVO.getRequestGatewayType() + "Service Port=" + p_requestVO
                                                .getServicePort());
                throw new BTSLBaseException("MultiServiceChargingSystem", methodName, PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
            }
            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(serviceKeywordCacheVO.getRequestHandlerClass());

            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_CHARGING_APPLICABLE"))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Debit Channel User For Using Service:: ", "Entered");
                }

                if (BTSLUtil.isNullString(p_requestVO.getReqSelector())) {
                    p_requestVO.setReqSelector(PretupsI.DEFAULT_SUBSERVICE);
                }

                // Populating C2STransferVO from the request VO
                populateVOFromRequest(p_requestVO);

                // loading amount as defined for charging for different service
                interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(_channelUserVO.getUserPhoneVO().getPrefixID(), p_requestVO
                                .getServiceType(), PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                // Set sender transfer item details
                setSenderTransferItemVO();
                // Set receiver transfer item details
                setReceiverTransferItemVO();
                amountMappingVO = new SelectorAmountMappingDAO().loadSelectorAmountDetails(con, p_requestVO.getServiceType(), p_requestVO.getReqSelector());
                if (amountMappingVO != null) {
                    PretupsBL.validateAmount(_c2sTransferVO, amountMappingVO.getAmountStr());
                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_AMOUNT);
                }
                _c2sTransferVO.setRequestedAmount(amountMappingVO.getAmount());
                _senderTransferItemVO.setTransferValue(_c2sTransferVO.getRequestedAmount());
                _itemList = new ArrayList<TransferItemVO>();
                _itemList.add(_senderTransferItemVO);
                _itemList.add(_receiverTransferItemVO);
                _c2sTransferVO.setTransferItemList(_itemList);
                // generating TXN ID for Service Charging
                generateServiceChargeTxnID(_c2sTransferVO);
                // Get the product Info based on the service type
                PretupsBL.getProductFromServiceType(con, _c2sTransferVO, p_requestVO.getServiceType(), PretupsI.C2S_MODULE);
                _transferEntryReqd = true;
                _transferID = _c2sTransferVO.getTransferID();
                // Now debit the channel user for using this service
                _userBalancesVO = ChannelUserBL.debitUserBalanceForProduct(con, _transferID, _c2sTransferVO);

                // Update Transfer Out Counts for the sender
                ChannelTransferBL.increaseC2STransferOutCounts(con, _c2sTransferVO, true);
                _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);

                // add entry in c2s transfers table
                if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.DB_ENTRY_NOT_ALLOWED)).booleanValue()) {
                    ChannelTransferBL.addC2STransferDetails(con, _c2sTransferVO);
                    _transferDetailAdded = true;
                }

                if (_transferDetailAdded && _transferEntryReqd) {
                   mcomCon.finalCommit();
                   
                }
                // making entry in the transaction log
                TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS, "Leaving the controller",
                                PretupsI.TXN_LOG_STATUS_SUCCESS, "Getting Code=" + p_requestVO.getMessageCode());
                // Log the details if the transfer Details were added i.e.
                // if User was debited
                BalanceLogger.log(_userBalancesVO);
            }
            // Now calling actual controller/handler for coming request
            try {
                controllerObj.process(p_requestVO);
            } catch (Exception se) {
                LOG.errorTrace(methodName, se);
            }

            if (_transferDetailAdded && !p_requestVO.isSuccessTxn()) {
                if (mcomCon == null) {
                	mcomCon = new MComConnection();
                	con=mcomCon.getConnection();
                }
                // Update the sender back for fail transaction
                // So that we can update with final status here
                updateSenderForFailedTransaction(con, p_requestVO);
                mcomCon.finalCommit();
                
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            LOG.errorTrace(methodName, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            }
        } catch (Exception se) {
            LOG.errorTrace(methodName, se);

        } finally {
            // Populate the ChannelRequestDailyLogVo and log
            ChannelRequestDailyLog.log(ChannelRequestDailyLog.populateChannelRequestDailyLogVO(p_requestVO, _c2sTransferVO));
            if(mcomCon != null)
            {
            	mcomCon.close("MultiServiceChargingSystem#process");
            	mcomCon=null;
            	}
            con = null;
        }
    }

    private static synchronized void generateServiceChargeTxnID(TransferVO p_transferVO) {
        final String methodName = "generateServiceChargeTxnID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        String transferID = null;
        String minut2Compare = null;
        Date mydate = null;
        try {
            mydate = new Date();
            p_transferVO.setCreatedOn(mydate);
            minut2Compare = _sdfCompare.format(mydate);
            final int currentMinut = Integer.parseInt(minut2Compare);

            if (currentMinut != _prevMinut) {
                _transactionIDCounter = 1;
                _prevMinut = currentMinut;
            } else if (_transactionIDCounter >= 9999) {
                _transactionIDCounter = 1;
            } else {
                _transactionIDCounter++;
            }
            if (_transactionIDCounter == 0) {
                throw new BTSLBaseException("MultiServiceChargingSystem", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            transferID = formatServiceChargeTxnID(p_transferVO, _transactionIDCounter);
            if (transferID == null) {
                throw new BTSLBaseException("MultiServiceChargingSystem", methodName, PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
            }
            p_transferVO.setTransferID(transferID);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateServiceChargeTxnID():: ", "Exited" + transferID);
            }
        }
    }

    /**
     * Method to populate C2S Transfer VO from request VO for further use
     * 
     * @param p_requestVO
     */
    private void populateVOFromRequest(RequestVO p_requestVO) {
        final String methodName = "populateVOFromRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        _c2sTransferVO.setSenderVO(_channelUserVO);
        _c2sTransferVO.setReceiverMsisdn((_channelUserVO.getUserPhoneVO()).getMsisdn());
        _c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
        _c2sTransferVO.setModule(p_requestVO.getModule());
        _c2sTransferVO.setInstanceID(p_requestVO.getInstanceID());
        _c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        _c2sTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        _c2sTransferVO.setServiceType(p_requestVO.getServiceType());
        _c2sTransferVO.setSourceType(p_requestVO.getSourceType());
        _c2sTransferVO.setCreatedOn(_currentDate);
        _c2sTransferVO.setCreatedBy(_channelUserVO.getUserID());
        _c2sTransferVO.setModifiedOn(_currentDate);
        _c2sTransferVO.setModifiedBy(_channelUserVO.getUserID());
        _c2sTransferVO.setTransferDate(_currentDate);
        _c2sTransferVO.setTransferDateTime(_currentDate);
        _c2sTransferVO.setSenderMsisdn((_channelUserVO.getUserPhoneVO()).getMsisdn());
        _c2sTransferVO.setSenderID(_channelUserVO.getUserID());
        _c2sTransferVO.setNetworkCode(_channelUserVO.getNetworkID());
        _c2sTransferVO.setReceiverNetworkCode(_channelUserVO.getNetworkID());
        _c2sTransferVO.setLocale(_senderLocale);
        _c2sTransferVO.setLanguage(_c2sTransferVO.getLocale().getLanguage());
        _c2sTransferVO.setCountry(_c2sTransferVO.getLocale().getCountry());
        _c2sTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
        _c2sTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
        _c2sTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
        (_channelUserVO.getUserPhoneVO()).setLocale(_senderLocale);
        _c2sTransferVO.setReferenceID(p_requestVO.getExternalReferenceNum());
        _c2sTransferVO.setActiveUserId(_channelUserVO.getActiveUserID());
        _c2sTransferVO.setCategoryCode(_channelUserVO.getCategoryCode());
        _c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
        _c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());
        _c2sTransferVO.setSubService(p_requestVO.getReqSelector());
        _c2sTransferVO.setSelectorCode(p_requestVO.getReqSelector());
        _c2sTransferVO.setTxnType(PretupsI.TXNTYPE_T);
        if (LOG.isDebugEnabled()) {
            LOG.debug("populateVOFromRequest():: ", "Exited");
        }
    }

    /**
     * Method to update the channel user back in case of failed transaction
     * 
     * @param p_con
     * @throws BTSLBaseException
     */
    private void updateSenderForFailedTransaction(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "updateSenderForFailedTransaction";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try {
            _userBalancesVO = ChannelUserBL.creditUserBalanceForProduct(p_con, _c2sTransferVO.getTransferID(), _c2sTransferVO);
            ChannelTransferBL.decreaseC2STransferOutCounts(p_con, _c2sTransferVO);
            _creditBackEntryDone = true;

            if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_REQUEST)) {
                p_requestVO.setSuccessTxn(false);
                final String[] messageArgArray = { _c2sTransferVO.getReceiverMsisdn(), PretupsBL.getDisplayAmount(_c2sTransferVO.getRequestedAmount()), _c2sTransferVO
                                .getTransferID(), PretupsBL.getDisplayAmount(_userBalancesVO.getBalance()) };
                p_requestVO.setMessageArguments(messageArgArray);
                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_SENDER_CREDIT_SUCCESS);
            }
            // Log the credit back entry in the balance log
            if (_creditBackEntryDone) {
                BalanceLogger.log(_userBalancesVO);
            }
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, _senderNetworkCode, PretupsI.TXN_LOG_REQTYPE_INT, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                            "Credit Back Done", PretupsI.TXN_LOG_STATUS_SUCCESS, "");
        } catch (BTSLBaseException be) {
            _c2sTransferVO.setSenderReturnMessage(null);
            PretupsBL.validateRecieverLimits(p_con, _c2sTransferVO, PretupsI.TRANS_STAGE_AFTER_INTOP, PretupsI.C2S_MODULE);
            TransactionLog.log(_transferID, _requestIDStr, _senderMSISDN, "", PretupsI.TXN_LOG_REQTYPE_RES, PretupsI.TXN_LOG_TXNSTAGE_PROCESS,
                            "base Exception while crediting back sender", PretupsI.TXN_LOG_STATUS_FAIL, "Exception:" + be.getMessage() + " Getting Code=" + be.getMessageKey());
            LOG.errorTrace(methodName, be);
            throw be;
        }
    }

    /**
     * Sets the sender transfer Items VO for the channel user
     * 
     */
    private void setSenderTransferItemVO() {
        final String methodName = "setSenderTransferItemVO";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        _senderTransferItemVO = new C2STransferItemVO();
        // set sender transfer item details
        _senderTransferItemVO.setSNo(2);
        _senderTransferItemVO.setMsisdn(_senderMSISDN);
        _senderTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount());
        _senderTransferItemVO.setSubscriberType(_senderSubscriberType);
        _senderTransferItemVO.setTransferDate(_currentDate);
        _senderTransferItemVO.setTransferDateTime(_currentDate);
        _senderTransferItemVO.setTransferID(_c2sTransferVO.getTransferID());
        _senderTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        _senderTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
        _senderTransferItemVO.setEntryDate(_currentDate);
        _senderTransferItemVO.setEntryDateTime(_currentDate);
        _senderTransferItemVO.setEntryType(PretupsI.DEBIT);
        _senderTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        _senderTransferItemVO.setPrefixID((_channelUserVO.getUserPhoneVO()).getPrefixID());
        _senderTransferItemVO.setServiceClass(interfaceMappingVO.getAllServiceClassID());
        _senderTransferItemVO.setInterfaceID(interfaceMappingVO.getInterfaceID());
        _senderTransferItemVO.setServiceClassCode(_senderAllServiceClassID);
    }

    /**
     * Sets the sender transfer Items VO for the channel user
     * 
     */
    private void setReceiverTransferItemVO() {
        final String methodName = "setSenderTransferItemVO";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        _receiverTransferItemVO = new C2STransferItemVO();
        // set sender transfer item details
        _receiverTransferItemVO.setSNo(1);
        _receiverTransferItemVO.setMsisdn(_senderMSISDN);
        _receiverTransferItemVO.setRequestValue(_c2sTransferVO.getRequestedAmount());
        _receiverTransferItemVO.setSubscriberType(_senderSubscriberType);
        _receiverTransferItemVO.setTransferDate(_currentDate);
        _receiverTransferItemVO.setTransferDateTime(_currentDate);
        _receiverTransferItemVO.setTransferID(_c2sTransferVO.getTransferID());
        _receiverTransferItemVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
        _receiverTransferItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
        _receiverTransferItemVO.setEntryDate(_currentDate);
        _receiverTransferItemVO.setEntryDateTime(_currentDate);
        _receiverTransferItemVO.setEntryType(PretupsI.DEBIT);
        _receiverTransferItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
        _receiverTransferItemVO.setPrefixID((_channelUserVO.getUserPhoneVO()).getPrefixID());
        _receiverTransferItemVO.setServiceClass(interfaceMappingVO.getAllServiceClassID());
        _senderTransferItemVO.setInterfaceID(interfaceMappingVO.getInterfaceID());
        _receiverTransferItemVO.setServiceClassCode(_senderAllServiceClassID);
    }

    /**
     * Method formatServiceChargeTxnID.
     * 
     * @param p_transferVO
     *            TransferVO
     * @param p_tempTransferID
     *            long
     * @return String
     * @see com.btsl.pretups.util.OperatorUtilI#formatServiceChargeTxnID(TransferVO,
     *      long)
     */
    public static String formatServiceChargeTxnID(TransferVO p_transferVO, long p_tempTransferID) {
        final String methodName = "formatServiceChargeTxnID";
        String returnStr = null;
        try {
            final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID), SRVCHARG_TRANSFER_ID_PAD_LENGTH);
            returnStr = "S" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
                            .getProperty("INSTANCE_ID") + paddedTransferIDStr;
            p_transferVO.setTransferID(returnStr);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultiServiceChargingSystem[]", "", "", "",
                            "Not able to generate Transfer ID:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }

    /**
     * Method currentDateTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    public static String currentDateTimeFormatString(Date p_date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        final String dateString = sdf.format(p_date);
        return dateString;
    }

    /**
     * Method currentTimeFormatString.
     * 
     * @param p_date
     *            Date
     * @return String
     * @throws ParseException
     */
    public static String currentTimeFormatString(Date p_date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        final String dateString = sdf.format(p_date);
        return dateString;
    }

}
