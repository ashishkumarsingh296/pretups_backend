/**
 * @(#)ScheduledMultipleCreditListController.java
 *                                                Copyright(c) 2013, Comviva
 *                                                Technologies Ltd.
 *                                                All Rights Reserved
 *                                                ------------------------------
 *                                                ------------------------------
 *                                                ------------------------------
 *                                                -------
 *                                                Author Date History
 *                                                ------------------------------
 *                                                ------------------------------
 *                                                ------------------------------
 *                                                -------
 *                                                Harsh Dixit May 20,2013
 *                                                Initial Creation
 * 
 *                                                ------------------------------
 *                                                ------------------------------
 *                                                ------------------------------
 *                                                ------
 */

package com.btsl.pretups.p2p.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.p2p.subscriber.requesthandler.RegisterationController;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCacheVO;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesCache;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.txn.pretups.p2p.transfer.businesslogic.MCDTxnDAO;
import com.txn.pretups.preference.businesslogic.PreferenceTxnDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;

public class ScheduledMultipleCreditListController implements ServiceKeywordControllerI {

    private static OperatorUtilI _operatorUtil = null;
    private static Log _log = LogFactory.getLog(ScheduledMultipleCreditListController.class.getName());
    private String _requestIDStr;
    private RequestVO _requestVO = null;
    private String _senderMsisdn = null;
    private SenderVO _senderVO;
    private ReceiverVO _receiverVO;
    private String _listName = null;
    private String _gatewayCode = null;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private HashMap _preferenceDetailsMap = null;
    private String _failString = "";
    private String _failRecords = "";
    private Locale _senderLocale = null;
    private boolean batchInsert = false;
    private MCDListVO _mcdListVO = null;
    private boolean _newUser = false;
    private long listCount = 0;
    private long cumAmount = 0;
    private String maxAllowedAmountStr = null;
    private long maxAllowedAmount = 0;
    private String maxAllowedBuddyCountStr = null;
    private long maxAllowedBuddyCount = 0;
    private int _totalMsisdnReqProcessed = 0;
    private int _successMsisdnCount = 0;
    private int _failMsisdnCount = 0;
    // added by modified SMS by client for this CR
    private Locale _receiverLocale = null;
    private ArrayList successList = new ArrayList();

    // Loads operator specific class
    static {
        final String METHOD_NAME = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrepaidControllerMultTransfer[initialize]", "", "",
                "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        _requestIDStr = p_requestVO.getRequestIDStr();
        _requestVO = p_requestVO;
        final String requestSeperator = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_DIFFERENT_REQUEST_SEPERATOR));
        String decryptedMessage;
        String[] requestArray = null;
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestIDStr, "Entered");
        }
        try {
            _senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (_senderVO == null) {
                new RegisterationController().regsiterNewUser(p_requestVO);
                _senderVO = (SenderVO) p_requestVO.getSenderVO();
                _senderVO.setDefUserRegistration(true);

                p_requestVO.setSenderLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                // If group type counters are allowed to check for controlling
                // for the request gateway then check them
                // This change has been done by ankit on date 14/07/06 for SMS
                // charging
                _newUser = true;
                if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)) != null && ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.GRPT_CTRL_ALLOWED)).indexOf(p_requestVO.getRequestGatewayType()) != -1 && !PretupsI.NOT_APPLICABLE
                    .equals(p_requestVO.getGroupType())) {
                    // load the user running and profile counters
                    // Check the counters
                    // update the counters
                    final GroupTypeProfileVO groupTypeProfileVO = PretupsBL.loadAndCheckP2PGroupTypeCounters(p_requestVO, PretupsI.GRPT_TYPE_CONTROLLING);
                    // If counters reach the profile limit them throw exception
                    if (groupTypeProfileVO != null && groupTypeProfileVO.isGroupTypeCounterReach()) {
                        p_requestVO.setDecreaseGroupTypeCounter(false);
                        final String arr[] = { String.valueOf(groupTypeProfileVO.getThresholdValue()) };
                        if (PretupsI.GRPT_TYPE_FREQUENCY_DAILY.equals(groupTypeProfileVO.getFrequency())) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_D, arr);
                        }
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_GRPT_COUNTERS_REACH_LIMIT_M, arr);
                    }
                }
            }

            _listName = p_requestVO.getMcdListName();
            _senderMsisdn = p_requestVO.getRequestMSISDN();
            _senderLocale = _senderVO.getLocale();
            _receiverLocale = _senderLocale;

            _gatewayCode = p_requestVO.getRequestGatewayCode();

            try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                p_requestVO.setExternalNetworkCode(_senderVO.getNetworkCode());
                p_requestVO.setActiverUserId(_senderVO.getUserID());
                if (BTSLUtil.isNullString(_listName)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_NAME_BLANK);
                }
                if (!BTSLUtil.isAlphaNumeric(_listName)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_ALLOWED_ALPHANUMERIC);
                }
                if (_listName.length() > 50) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_LENGTH_EXCEED);
                }
                if (p_requestVO.getMcdListAddCount() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_MAX_LIST_COUNT))).intValue()) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_COUNT_EXCEED);
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                _log.error("process", "BTSLBaseException be:" + be.getMessage());
                throw be;
            } catch (Exception e) {
                _log.error("process", "Exception e:" + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in processing request");
            }
            decryptedMessage = p_requestVO.getDecryptedMessage();
            requestArray = decryptedMessage.split(requestSeperator);
            for (int i = 0; i < requestArray.length; i++) {
                final String[] messageArray = PretupsBL.parsePlainMessage(requestArray[i]);
                try {
                    _mcdListVO = _operatorUtil.validateSMCDListAMDRequest(con, messageArray, p_requestVO);
                    _mcdListVO.setListName(_listName);
                    _mcdListVO.setParentID(_senderVO.getUserID());
                    _mcdListVO.setCreatedOn(new Date());
                    _mcdListVO.setModifiedOn(new Date());
                    _mcdListVO.setModifiedBy(_senderVO.getUserID());
                    _mcdListVO.setCreatedBy(((SenderVO) p_requestVO.getSenderVO()).getUserID());
                    if (_senderMsisdn.equals(_mcdListVO.getMsisdn())) {
                        _failString = _failString + _mcdListVO.getMsisdn() + ":" + _mcdListVO.getMcdListAmountString() + BTSLUtil.getMessage(_senderVO.getLocale(),
                            PretupsErrorCodesI.P2P_MULT_CDT_LIST_AMD_SENDER_MSISDN_NOTALLOWD, null) + ",";
                        _totalMsisdnReqProcessed++;
                        _failMsisdnCount++;
                    } else {
                        processSMCDList(con, _mcdListVO, p_requestVO);
                    }
                } catch (BTSLBaseException be) {
                    _log.error("process", "BTSLBaseException be:" + be.getMessage());
                    _log.errorTrace(METHOD_NAME, be);
                    _totalMsisdnReqProcessed++;
                    _failMsisdnCount++;
                    _failString = _failString + _mcdListVO.getMsisdn() + ":" + _mcdListVO.getMcdListAmountString() + ":" + BTSLUtil.getMessage(_senderVO.getLocale(), be
                        .getMessageKey(), null) + ",";
                    _failRecords = _failRecords + _mcdListVO.getMsisdn() + ",";
                    continue;
                } catch (Exception e) {
                    _log.error("process", "Exception be:" + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                    _totalMsisdnReqProcessed++;
                    _failMsisdnCount++;
                    _failString = _failString + _mcdListVO.getMsisdn() + ":" + _mcdListVO.getMcdListAmount() + ":" + PretupsErrorCodesI.P2P_ERROR_EXCEPTION + ",";
                    _failRecords = _failRecords + _mcdListVO.getMsisdn() + ",";
                    continue;
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException be:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (Exception e) {
            _log.error("process", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_senderVO != null && _newUser) {
                try {
                    if (mcomCon == null) {
						mcomCon = new MComConnection();
						con = mcomCon.getConnection();
                    }
                    SubscriberBL.updateMCDSubscriberLastDetails(con, _senderVO, new Date(), PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                } catch (BTSLBaseException bex) {
                    _log.errorTrace(METHOD_NAME, bex);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrepaidController[run]", null, _senderMsisdn,
                        null, "Not able to update Subscriber Last Details Exception:" + e.getMessage());
                }
            }
            if (p_requestVO.isSuccessTxn()) {
                _senderLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                final String messageKey = p_requestVO.getMessageCode();
                if (_totalMsisdnReqProcessed > 0 && (_totalMsisdnReqProcessed == _successMsisdnCount)) {
                    if (p_requestVO.getAction().equals("A")) {
                        (new PushMessage(_senderMsisdn, getSenderSuccessMessage(_senderLocale, successList, p_requestVO.getMcdScheduleType(), p_requestVO
                            .getMcdNextScheduleDate()), "", _gatewayCode, _senderLocale)).push();
                        }
                } else if (_failMsisdnCount > 0) {
                    (new PushMessage(_senderMsisdn, getSenderMessage(_senderLocale, _listName, _failRecords), "", _gatewayCode, _senderLocale)).push();
                } else {
                    (new PushMessage(_senderMsisdn, getSenderFailMessage(_senderLocale, _listName, messageKey), "", _gatewayCode, _senderLocale)).push();
                }

                if (p_requestVO.getMcdListAddCount() > 0) {
                    if (_totalMsisdnReqProcessed > 0 && _successMsisdnCount == _totalMsisdnReqProcessed) {
                        p_requestVO.setMcdListStatus("SS");
                    }
                    if (_successMsisdnCount > 0 && _successMsisdnCount < _totalMsisdnReqProcessed) {
                        p_requestVO.setMcdListStatus("PS");
                        p_requestVO.setMcdFailRecords(_failRecords);
                    }
                    if (_failMsisdnCount > 0 && _failMsisdnCount == _totalMsisdnReqProcessed) {
                        p_requestVO.setMcdListStatus("FA");
                    }
                }
            }
            if (_newUser) {
                if (_senderLocale == null) {
                    _senderLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                }

               (new PushMessage(_senderMsisdn, getSenderRegistrationMessage(), "", _gatewayCode, _senderLocale)).push();
            }
            if (con != null) {

                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("ScheduledMultipleCreditListController#process");
					mcomCon = null;
				}
                con = null;
            }

        }
    }

    public void processSMCDList(Connection p_con, MCDListVO mcdListVO, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "processSMCDList";
        PreferenceCacheVO preferenceCacheVO = null;
        ServiceClassWebDAO serviceClasswebDAO = null;
        boolean isSenderServiceClassCodeExist = false;
        boolean isReceiverServiceClassCodeExist = false;
        final Date currentdate = new Date();
        TransferRulesVO transferRulesVO = null;
        boolean amountAdded = false;
        boolean amountModifed = false;
        boolean amountDeleted = false;
        PreferenceTxnDAO preferencetxnDAO = null;
        ArrayList serviceClassIDList = null;
        ListValueVO listValueVO = null;
        String[] listValue = null;
        SubscriberDAO subscriberDAO = null;
        final MCDTxnDAO mcdtxnDAO = new MCDTxnDAO();
        subscriberDAO = new SubscriberDAO();
        final String[] subArray = new String[2];
        final String key = null;
        KeyArgumentVO keyArgumentVO = null;
        String postSubscriberServiceClassId = null;

        _senderMsisdn = _operatorUtil.getSystemFilteredMSISDN(p_requestVO.getReceiverMsisdn());
        _senderVO = subscriberDAO.loadSubscriberDetailsByMsisdn(p_con, _senderMsisdn,PretupsI.P2P_SMCD_LIST_SERVICE_TYPE);
        postSubscriberServiceClassId = _senderVO.getServiceClassID();
        _senderVO.setLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
        _receiverVO = new ReceiverVO();
        _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
            .getServiceInterfaceRoutingDetails(p_requestVO.getExternalNetworkCode() + "_" + PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER + "_" + _senderVO.getSubscriberType());
        if (_log.isDebugEnabled()) {
            _log.debug("sub-service: ", _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode());
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("processSMCDList ", _requestIDStr, "Entered mcdListVO" + mcdListVO);
            }
            if ("A".equals(mcdListVO.getAction())) {
                try {
                    _totalMsisdnReqProcessed++;
                    PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                    if (!BTSLUtil.isNullString(p_requestVO.getMcdNoOfSchedules())) {
                        final int strLength = p_requestVO.getMcdNoOfSchedules().length();
                        if (strLength > 10) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_NOSC_LENGTH_EXCEED);
                        }
                        if (!BTSLUtil.isNumeric(p_requestVO.getMcdNoOfSchedules())) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_MCDL_INVALID_FREQUENCY);
                        }
                    } else {
                        p_requestVO.setMcdNoOfSchedules(String.valueOf(((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SMCDL_DEFAULT_FREQUENCY))).longValue()));
                    }

                    final String scheduleType = p_requestVO.getMcdScheduleType();
                    if (!BTSLUtil.isNullString(scheduleType)) {
                        final int strLength = scheduleType.length();
                        if (!BTSLUtil.isStringIn(scheduleType, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_ALLOWED_SCHEDULE_TYPE)))) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_MCDL_INVALID_SCHEDULETYPE);
                        }
                        if (strLength > 4) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_SCTYPE_LENGTH_EXCEED);
                        }
                    } else {
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_SCTYPE_BLANK);
                    }
                    // validate sender service class
                    if (BTSLUtil.isNullString(_requestVO.getMcdSenderProfile())) {
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    serviceClasswebDAO = new ServiceClassWebDAO();
                    isSenderServiceClassCodeExist = serviceClasswebDAO.isServiceCodeExists(p_con, _requestVO.getMcdSenderProfile());
                    if (isSenderServiceClassCodeExist) {
                        _senderVO.setModifiedOn(currentdate);
                        _senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
                        _senderVO.setServiceClassCode(_requestVO.getMcdSenderProfile());
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    batchInsert = mcdtxnDAO.isBatchAlreadyExist(p_con, p_requestVO);
                    if (!batchInsert) {
                        batchInsert = mcdtxnDAO.addP2PBatchDetails(p_con, p_requestVO);
                    }
                    // Load Service Class ID details of sender
                    preferencetxnDAO = new PreferenceTxnDAO();
                    serviceClassIDList = preferencetxnDAO.loadServiceClassIDList(p_con, _senderVO.getNetworkCode(), _senderVO.getServiceClassCode());
                    if (serviceClassIDList != null && !serviceClassIDList.isEmpty()) {
                        String serviceClass = null;
                        int serviceIdListSize = serviceClassIDList.size();
                        for (int i = 0; i < serviceIdListSize; i++) {
                            listValueVO = (ListValueVO) serviceClassIDList.get(i);
                            if (listValueVO.getLabel().equals(_senderVO.getServiceClassCode())) {
                                listValue = (listValueVO.getValue()).split("_");
                                final String serviceClassID = listValue[0];
                                final String interfaceCategory = listValue[1];
                                if (interfaceCategory.equals(_senderVO.getSubscriberType()) && serviceClassID.equals(_senderVO.getServiceClassID())) {
                                    serviceClass = serviceClassID;
                                    break;
                                } else if (interfaceCategory.equals(_senderVO.getSubscriberType()) && BTSLUtil.isNullString(serviceClass)) {
                                    serviceClass = serviceClassID;
                                }
                            }
                        }
                        _senderVO.setServiceClassID(serviceClass);
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                    }
                    // validate receiver service class
                    if (BTSLUtil.isNullString(mcdListVO.getMcdReceiverProfile())) {
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    isReceiverServiceClassCodeExist = serviceClasswebDAO.isServiceCodeExists(p_con, mcdListVO.getMcdReceiverProfile());
                    if (isReceiverServiceClassCodeExist) {
                        _receiverVO.setModifiedOn(currentdate);
                        _receiverVO.setModifiedBy(PretupsI.SYSTEM_USER);
                        _receiverVO.setServiceClassCode(mcdListVO.getMcdReceiverProfile());
                        _receiverVO.setSubscriberType(mcdListVO.getSubscriberType());
                        _receiverVO.setNetworkCode(p_requestVO.getExternalNetworkCode());
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    // Load Service Class ID of receiver
                    preferencetxnDAO = new PreferenceTxnDAO();
                    serviceClassIDList = preferencetxnDAO.loadServiceClassIDList(p_con, _receiverVO.getNetworkCode(), _receiverVO.getServiceClassCode());
                    if (serviceClassIDList != null && !serviceClassIDList.isEmpty()) {
                        String serviceClass = null;
                        int servicesIdListSize=serviceClassIDList.size();
                        for (int i = 0; i < servicesIdListSize; i++) {
                            listValueVO = (ListValueVO) serviceClassIDList.get(i);
                            if (listValueVO.getLabel().equals(_receiverVO.getServiceClassCode())) {
                                listValue = (listValueVO.getValue()).split("_");
                                final String serviceClassID = listValue[0];
                                final String interfaceCategory = listValue[1];
                                if (interfaceCategory.equals(_receiverVO.getSubscriberType())) {
                                    serviceClass = serviceClassID;
                                }
                            }
                        }
                        _receiverVO.setServiceClassCode(serviceClass);
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                    }
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                            "processSMCDList ",
                            "validate transfer rule",
                            "Entered p_requestVO.getServiceType(): " + PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER + " p_requestVO.getModule() " + p_requestVO.getModule() + " receiverVO.getNetworkCode() " + p_requestVO
                                .getExternalNetworkCode() + " senderVO.getSubscriberType() " + _senderVO.getSubscriberType() + "receiverVO.getSubscriberType()" + mcdListVO
                                .getSubscriberType() + " p_requestVO.getServiceClassCode() " + p_requestVO.getMcdSenderProfile() + " mcdListVO.getMcdReceiverProfile() " + mcdListVO
                                .getMcdReceiverProfile() + " _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode() " + _serviceInterfaceRoutingVO
                                .getInterfaceDefaultSelectortCode());
                    }
                    // validate transfer rules
                    if (_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_POSTPAID)) {
                        _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                            .getServiceInterfaceRoutingDetails(p_requestVO.getExternalNetworkCode() + "_" + PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE + "_" + _senderVO
                                .getSubscriberType());
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE, p_requestVO.getModule(), p_requestVO
                            .getExternalNetworkCode(), _senderVO.getSubscriberType(), _receiverVO.getSubscriberType(), postSubscriberServiceClassId, _receiverVO
                            .getServiceClassCode(), _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode(), PretupsI.NOT_APPLICABLE);
                    }
                    if (_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_PREPAID)) {
                        _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                            .getServiceInterfaceRoutingDetails(p_requestVO.getExternalNetworkCode() + "_" + PretupsI.SERVICE_TYPE_P2PRECHARGE + "_" + _senderVO
                                .getSubscriberType());
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(PretupsI.SERVICE_TYPE_P2PRECHARGE, p_requestVO.getModule(), p_requestVO
                            .getExternalNetworkCode(), _senderVO.getSubscriberType(), _receiverVO.getSubscriberType(), _senderVO.getServiceClassID(), _receiverVO
                            .getServiceClassCode(), _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode(), PretupsI.NOT_APPLICABLE);
                    }
                    if (transferRulesVO == null) {
                        final String args[] = { _requestVO.getMcdSenderProfile(), mcdListVO.getMcdReceiverProfile() };
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE, args);
                    }
                    // validate buddy list size & total amount in buddy list
                    listCount = mcdtxnDAO.isListAlreadyRegistered(p_con, _listName, _senderVO.getUserID(), p_requestVO.getMcdScheduleType());
                    if (listCount == 0) {
                        if (listCount + p_requestVO.getMcdListAddCount() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_MAX_LIST_COUNT))).intValue()) {
                            throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_COUNT_EXCEED);
                        }
                    }
                    mcdListVO.setMcdListAmount(PretupsBL.validateMCDListAmount(mcdListVO.getMcdListAmountString()));
                    cumAmount = mcdtxnDAO.getTotalAmountInBuddyList(p_con, _senderVO.getUserID(), p_requestVO.getMcdListName(), p_requestVO.getMcdScheduleType());
                    cumAmount += mcdListVO.getMcdListAmount();

                    // VALIDATE SERVICE CLASS BASED PREFERENCES
                    // first validate Daily Success Txn Allowed(P2P)
                    if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SERVICES_TYPE_SERVICECLASS))).booleanValue()){
                    _preferenceDetailsMap = preferencetxnDAO.loadPreferenceByServiceClassId(_senderVO.getNetworkCode(), _senderVO.getServiceClassID());
                    preferenceCacheVO = (PreferenceCacheVO) _preferenceDetailsMap
                        .get(PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT + "_" + _senderVO.getNetworkCode() + "_" + _senderVO.getServiceClassID());
                    }
                    else{
                    	_preferenceDetailsMap = preferencetxnDAO.loadPreferenceByServiceType(_senderVO.getNetworkCode(), PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER);
                    	preferenceCacheVO = (PreferenceCacheVO) _preferenceDetailsMap
                                .get(PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT + "_" + _senderVO.getNetworkCode() + "_" + PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER);
                    }
                    if (preferenceCacheVO != null) {
                        maxAllowedBuddyCountStr = preferenceCacheVO.getValue();
                    }
                    if (!BTSLUtil.isNullString(maxAllowedBuddyCountStr)) {
                        maxAllowedBuddyCount = Long.valueOf(maxAllowedBuddyCountStr);
                    } else {
                    	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SERVICES_TYPE_SERVICECLASS))).booleanValue()){
                        maxAllowedBuddyCountStr = String.valueOf(((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT)))
                            .longValue());
                    	}
                    	else{
                    		maxAllowedBuddyCountStr = String.valueOf(((Long) (PreferenceCache.getControlPreference(PreferenceI.P2P_DAILY_SUCCESS_TXN_ALLOWED_COUNT, _senderVO.getNetworkCode(), PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))));
                    	}
                        maxAllowedBuddyCount = Long.valueOf(maxAllowedBuddyCountStr);
                    }
                    if (listCount > maxAllowedBuddyCount - 1) {
                        final String args[] = { maxAllowedBuddyCountStr, String.valueOf(p_requestVO.getMcdListAddCount()) };
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_SNDR_DAY_MAX_TRANS_THRESHOLD, 0, args,
                            null);
                    }
                    // first service class based validation ends here

                    // Second validate Daily Max Transfer Amount based on
                    // schedule type as coming in request
                    if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SERVICES_TYPE_SERVICECLASS))).booleanValue()){
                        _preferenceDetailsMap = preferencetxnDAO.loadPreferenceByServiceClassId(_senderVO.getNetworkCode(), _senderVO.getServiceClassID());
                        preferenceCacheVO = (PreferenceCacheVO) _preferenceDetailsMap
                            .get(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE + "_" + _senderVO.getNetworkCode() + "_" + _senderVO.getServiceClassID());
                        }
                        else{
                        	_preferenceDetailsMap = preferencetxnDAO.loadPreferenceByServiceType(_senderVO.getNetworkCode(), PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER);
                        	preferenceCacheVO = (PreferenceCacheVO) _preferenceDetailsMap
                                    .get(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE + "_" + _senderVO.getNetworkCode() + "_" + PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER);
                        }
                    if (preferenceCacheVO != null) {
                        maxAllowedAmountStr = preferenceCacheVO.getValue();
                    }
                    if (!BTSLUtil.isNullString(maxAllowedAmountStr)) {
                        maxAllowedAmount = Long.valueOf(maxAllowedAmountStr);
                    } else {

                    	if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_SERVICES_TYPE_SERVICECLASS))).booleanValue()){
                    		maxAllowedAmountStr = String.valueOf(((Long) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE)))
                            .longValue());
                    	}
                    	else{
                    		maxAllowedAmountStr = String.valueOf(((Long) (PreferenceCache.getControlPreference(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE, _senderVO.getNetworkCode(), PretupsI.SERVICE_TYPE_SCH_CREDIT_TRANSFER))));
                    	}
                    	maxAllowedAmount = Long.valueOf(maxAllowedAmountStr);
                    }
                    if (cumAmount > maxAllowedAmount) {
                        final String args[] = { Long.toString(cumAmount), maxAllowedAmountStr, Long.toString(mcdListVO.getMcdListAmount()) };
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD, 0,
                            args, null);
                    }
                    p_requestVO.setReceiverServiceClassId(_receiverVO.getServiceClassCode());
                    // Adding Buddy list in Database after completing all the
                    // business rules
                    amountAdded = mcdtxnDAO.addSMCDListAmountDetails(p_con, p_requestVO, mcdListVO.getMcdListAmount(), mcdListVO);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    mcdListVO.setReason(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    mcdListVO.setReason(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                }
                if (amountAdded) {
                    _successMsisdnCount++;
                    p_con.commit();
                    p_requestVO.setMcdNextScheduleDate(mcdtxnDAO.getNextScheduleDateAsString(_senderVO.getUserID(), _listName, p_requestVO.getMcdScheduleType()));
                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.P2P_SENDER_SCT_SUCCESS_SUBKEY);
                    subArray[0] = mcdListVO.getMsisdn();
                    subArray[1] = mcdListVO.getMcdListAmountString();
                    keyArgumentVO.setArguments(subArray);
                    successList.add(keyArgumentVO);
                    _receiverLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    (new PushMessage(mcdListVO.getMsisdn(), getReceiverSuccessMessage(_receiverLocale, _senderMsisdn, mcdListVO.getMcdListAmountString(), p_requestVO
                        .getMcdScheduleType(), p_requestVO.getMcdNextScheduleDate()), "", _gatewayCode, _receiverLocale)).push();
                } else {
                    _failMsisdnCount++;
                    _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getMcdListAmount() + ":" + mcdListVO.getReason() + ",";
                    _failRecords = _failRecords + _mcdListVO.getMsisdn() + ",";
                }
            } else if ("M".equals(mcdListVO.getAction())) {
                try {
                    _totalMsisdnReqProcessed++;
                    PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                    final String scheduleType = p_requestVO.getMcdScheduleType();
                    if (!BTSLUtil.isNullString(scheduleType)) {
                        final int strLength = scheduleType.length();
                        if (!BTSLUtil.isStringIn(scheduleType, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_ALLOWED_SCHEDULE_TYPE)))) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_MCDL_INVALID_SCHEDULETYPE);
                        }
                        if (strLength > 4) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_SCTYPE_LENGTH_EXCEED);
                        }
                    } else {
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_SCTYPE_BLANK);
                    }
                    // validate sender service class
                    if (BTSLUtil.isNullString(_requestVO.getMcdSenderProfile())) {
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    serviceClasswebDAO = new ServiceClassWebDAO();
                    isSenderServiceClassCodeExist = serviceClasswebDAO.isServiceCodeExists(p_con, _requestVO.getMcdSenderProfile());
                    if (isSenderServiceClassCodeExist) {
                        _senderVO.setModifiedOn(currentdate);
                        _senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
                        _senderVO.setServiceClassCode(_requestVO.getMcdSenderProfile());
                        _senderVO.setNetworkCode(p_requestVO.getExternalNetworkCode());
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    // Load Service Class ID details of sender
                    preferencetxnDAO = new PreferenceTxnDAO();
                    serviceClassIDList = preferencetxnDAO.loadServiceClassIDList(p_con, _senderVO.getNetworkCode(), _senderVO.getServiceClassCode());
                    if (serviceClassIDList != null && !serviceClassIDList.isEmpty()) {
                        String serviceClass = null;
                        int serviceListSizes=serviceClassIDList.size();
                        for (int i = 0; i < serviceListSizes; i++) {
                            listValueVO = (ListValueVO) serviceClassIDList.get(i);
                            if (listValueVO.getLabel().equals(_senderVO.getServiceClassCode())) {
                                listValue = (listValueVO.getValue()).split("_");
                                final String serviceClassID = listValue[0];
                                final String interfaceCategory = listValue[1];
                                if (interfaceCategory.equals(_senderVO.getSubscriberType()) && serviceClassID.equals(_senderVO.getServiceClassID())) {
                                    serviceClass = serviceClassID;
                                    break;
                                } else if (interfaceCategory.equals(_senderVO.getSubscriberType()) && BTSLUtil.isNullString(serviceClass)) {
                                    serviceClass = serviceClassID;
                                }
                            }
                        }
                        _senderVO.setServiceClassID(serviceClass);
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                    }
                    // validate receiver service class
                    if (BTSLUtil.isNullString(mcdListVO.getMcdReceiverProfile())) {
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    isReceiverServiceClassCodeExist = serviceClasswebDAO.isServiceCodeExists(p_con, mcdListVO.getMcdReceiverProfile());
                    if (isReceiverServiceClassCodeExist) {
                        _receiverVO.setModifiedOn(currentdate);
                        _receiverVO.setModifiedBy(PretupsI.SYSTEM_USER);
                        _receiverVO.setServiceClassCode(mcdListVO.getMcdReceiverProfile());
                        _receiverVO.setSubscriberType(mcdListVO.getSubscriberType());
                        _receiverVO.setNetworkCode(p_requestVO.getExternalNetworkCode());
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
                    }
                    // Load Service Class ID of receiver
                    preferencetxnDAO = new PreferenceTxnDAO();
                    serviceClassIDList = preferencetxnDAO.loadServiceClassIDList(p_con, _receiverVO.getNetworkCode(), _receiverVO.getServiceClassCode());
                    if (serviceClassIDList != null && !serviceClassIDList.isEmpty()) {
                        String serviceClass = null;
                        int serviceListSizes = serviceClassIDList.size();
                        for (int i = 0; i < serviceListSizes; i++) {
                            listValueVO = (ListValueVO) serviceClassIDList.get(i);
                            if (listValueVO.getLabel().equals(_receiverVO.getServiceClassCode())) {
                                listValue = (listValueVO.getValue()).split("_");
                                final String serviceClassID = listValue[0];
                                final String interfaceCategory = listValue[1];
                                if (interfaceCategory.equals(_receiverVO.getSubscriberType())) {
                                    serviceClass = serviceClassID;
                                }
                            }
                        }
                        _receiverVO.setServiceClassCode(serviceClass);
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                        throw new BTSLBaseException(this, "processSMCDList", PretupsErrorCodesI.SERVICECLASS_NOT_USED_IN_SYSTEM);
                    }
                    // validate transfer value
                    if (_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_POSTPAID)) {
                        _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                            .getServiceInterfaceRoutingDetails(p_requestVO.getExternalNetworkCode() + "_" + PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE + "_" + _senderVO
                                .getSubscriberType());
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE, p_requestVO.getModule(), p_requestVO
                            .getExternalNetworkCode(), _senderVO.getSubscriberType(), _receiverVO.getSubscriberType(), postSubscriberServiceClassId, _receiverVO
                            .getServiceClassCode(), _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode(), PretupsI.NOT_APPLICABLE);
                    }
                    if (_senderVO.getSubscriberType().equals(PretupsI.SERIES_TYPE_PREPAID)) {
                        _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                            .getServiceInterfaceRoutingDetails(p_requestVO.getExternalNetworkCode() + "_" + PretupsI.SERVICE_TYPE_P2PRECHARGE + "_" + _senderVO
                                .getSubscriberType());
                        transferRulesVO = (TransferRulesVO) TransferRulesCache.getObject(PretupsI.SERVICE_TYPE_P2PRECHARGE, p_requestVO.getModule(), p_requestVO
                            .getExternalNetworkCode(), _senderVO.getSubscriberType(), _receiverVO.getSubscriberType(), _senderVO.getServiceClassID(), _receiverVO
                            .getServiceClassCode(), _serviceInterfaceRoutingVO.getInterfaceDefaultSelectortCode(), PretupsI.NOT_APPLICABLE);
                    }
                    if (transferRulesVO == null) {
                        final String args[] = { _requestVO.getMcdSenderProfile(), mcdListVO.getMcdReceiverProfile() };
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE, args);
                    }
                    mcdListVO.setMcdListAmount(PretupsBL.validateMCDListAmount(mcdListVO.getMcdListAmountString()));
                    // Modifying Buddy list in Database after completing all the
                    // business rules
                    amountModifed = mcdtxnDAO.updateSMCDListAmountDetails(p_con, p_requestVO, mcdListVO.getMcdListAmount(), mcdListVO);
                    // validate total amount in buddy list
                    cumAmount = mcdtxnDAO.getTotalAmountInBuddyList(p_con, _senderVO.getUserID(), p_requestVO.getMcdListName(), p_requestVO.getMcdScheduleType());
                    // validate service class based preference
                    // Daily/Weekly/Monthly Max Transfer Amount based on
                    // schedule type as coming in request
                    _preferenceDetailsMap = preferencetxnDAO.loadPreferenceByServiceClassId(_senderVO.getNetworkCode(), _senderVO.getServiceClassID());
                    preferenceCacheVO = (PreferenceCacheVO) _preferenceDetailsMap
                        .get(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE + "_" + _senderVO.getNetworkCode() + "_" + _senderVO.getServiceClassID());
                    if (preferenceCacheVO != null) {
                        maxAllowedAmountStr = preferenceCacheVO.getValue();
                    }
                    if (!BTSLUtil.isNullString(maxAllowedAmountStr)) {
                        maxAllowedAmount = Long.parseLong(maxAllowedAmountStr);
                    } else {
                        maxAllowedAmount = ((Long) PreferenceCache.getSystemPreferenceValue(PreferenceI.DAILY_MAX_TRFR_AMOUNT_CODE)).longValue();
                    }
                    if (cumAmount > maxAllowedAmount) {
                        final String args[] = { Long.toString(cumAmount), maxAllowedAmountStr, Long.toString(mcdListVO.getMcdListAmount()) };
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_SNDR_DAY_MAX_AMTTRANS_THRESHOLD, args);
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    mcdListVO.setReason(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    mcdListVO.setReason(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                }
                if (amountModifed) {
                    _successMsisdnCount++;
                    p_con.commit();
                    p_requestVO.setMcdNextScheduleDate(mcdtxnDAO.getNextScheduleDateAsString(_senderVO.getUserID(), _listName, p_requestVO.getMcdScheduleType()));
                } else {
                    _failMsisdnCount++;
                    _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getMcdListAmount() + ":" + mcdListVO.getReason() + ",";
                    _failRecords = _failRecords + _mcdListVO.getMsisdn() + ",";
                }
            } else if ("D".equals(mcdListVO.getAction())) {
                try {
                    _totalMsisdnReqProcessed++;
                    final String scheduleType = p_requestVO.getMcdScheduleType();
                    if (!BTSLUtil.isNullString(scheduleType)) {
                        final int strLength = scheduleType.length();
                        if (!BTSLUtil.isStringIn(scheduleType, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_ALLOWED_SCHEDULE_TYPE)))) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_MCDL_INVALID_SCHEDULETYPE);
                        }
                        if (strLength > 4) {
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_SCTYPE_LENGTH_EXCEED);
                        }
                    } else {
                        throw new BTSLBaseException("ScheduledMultipleCreditListController", "processSMCDList", PretupsErrorCodesI.P2P_ERROR_MCD_SCTYPE_BLANK);
                    }
                    PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                    // Deleting individual Buddy from list in Database after
                    // completing all the business rules
                    amountDeleted = mcdtxnDAO.deleteSMCDListAmountDetails(p_con, p_requestVO, mcdListVO);
                    if (amountDeleted) {
                        _successMsisdnCount++;
                        p_con.commit();
                        _receiverLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        (new PushMessage(mcdListVO.getMsisdn(), getReceiverDeleteMessage(_receiverLocale, _senderMsisdn, mcdListVO.getMcdListAmountString(), p_requestVO
                            .getMcdScheduleType(), p_requestVO.getMcdNextScheduleDate()), "", _gatewayCode, _receiverLocale)).push();
                    } else {
                        _failMsisdnCount++;
                        _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getMcdListAmount() + ":" + mcdListVO.getReason() + ",";
                        _failRecords = _failRecords + _mcdListVO.getMsisdn() + ",";
                    }
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    mcdListVO.setReason(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    mcdListVO.setReason(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                }
            } else {
                _totalMsisdnReqProcessed++;
                _failMsisdnCount++;
                _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getMcdListAmount() + ":" + BTSLUtil.getMessage(_senderVO.getLocale(),
                    PretupsErrorCodesI.P2P_ERROR_MCD_LIST_ACTION_INVALID, null) + ",";
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("processMCDList", "  Exception while processing list :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultipleCreditListController[processMCDList]", "", "",
                "", "Exception while processing list " + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("process", "processMCDList", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }
    }

    private String getSenderSuccessMessage(Locale p_senderLocale, String p_listName) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderSuccessMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName);
        }
        String key = null;
        String[] messageArgArray = null;

        key = PretupsErrorCodesI.P2P_ERROR_MCD_LIST_ADD_SUCCESS;
        messageArgArray = new String[] { p_listName };

        return BTSLUtil.getMessage(p_senderLocale, key, messageArgArray);
    }

    private String getSenderSuccessMessage(Locale p_senderLocale, ArrayList p_successRecords, String p_schType, String p_nextSchDate) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderSuccessMessage",
                " p_senderLocale=" + p_senderLocale.getDisplayName() + "p_successRecords" + p_successRecords + "p_schType" + p_schType + "p_nextSchDate" + p_nextSchDate);
        }
        String key = null;
        String[] messageArgArray = null;
        if (p_schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            key = PretupsErrorCodesI.P2P_ERROR_SMCD_LIST_ADD_SUCCESS_W;
            messageArgArray = new String[] { BTSLUtil.getMessage(p_senderLocale, p_successRecords), p_nextSchDate };
        }
        if (p_schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            key = PretupsErrorCodesI.P2P_ERROR_SMCD_LIST_ADD_SUCCESS_M;
            messageArgArray = new String[] { BTSLUtil.getMessage(p_senderLocale, p_successRecords), p_nextSchDate };
        }
        return BTSLUtil.getMessage(p_senderLocale, key, messageArgArray);
    }

    private String getSenderMessage(Locale p_senderLocale, String p_listName, String p_message) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName + " p_message" + p_message);
        }
        String key = null;
        String[] messageArgArray = null;
        key = PretupsErrorCodesI.P2P_ERROR_SMCD_LIST_ADD_FAIL_MSISDN;
        messageArgArray = new String[] { p_listName, p_message };
        return BTSLUtil.getMessage(p_senderLocale, key, messageArgArray);
    }

    private String getSenderFailMessage(Locale p_senderLocale, String p_listName, String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderFailMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName);
        }
        String[] messageArgArray = null;
        messageArgArray = new String[] { p_listName };
        return BTSLUtil.getMessage(p_senderLocale, p_key, messageArgArray);
    }

    public void validateSenderPin(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateSenderPin";
        if (_log.isDebugEnabled()) {
            _log.debug("validateSenderPin ", "Entered p_requestVO" + p_requestVO);
        }
        final String pin = p_requestVO.getMcdPIn();
        final String actualPin = BTSLUtil.decryptText(_senderVO.getPin());
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
            if (BTSLUtil.isNullString(pin) && !_newUser) {
                throw new BTSLBaseException("ScheduledMultipleCreditListController", "validateSenderPin", PretupsErrorCodesI.P2P_MCDL_PIN_REQUIRED);
            }
            if (actualPin.equalsIgnoreCase(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                if (!BTSLUtil.isNullString(pin) && !pin.equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                    BTSLUtil.validatePIN(pin);
                    _senderVO.setPin(BTSLUtil.encryptText(pin));
                    _senderVO.setPinUpdateReqd(true);
                    _senderVO.setActivateStatusReqd(true);
                }
            } else {
                try {
                    SubscriberBL.validatePIN(p_con, _senderVO, pin);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        try {
                            p_con.commit();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException("ScheduledMultipleCreditListController", "validateSenderPin", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                        }
                    }
                    throw be;
                }
            }
        }
    }

    private String getSenderRegistrationMessage() {
        if (_senderVO.isPinUpdateReqd()) {
            final String[] messageArgArray = { BTSLUtil.decryptText(_senderVO.getPin()) };
            return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS_WITHPIN, messageArgArray);
        }
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.P2P_SENDER_AUTO_REG_SUCCESS, null);
    }

    private String getReceiverSuccessMessage(Locale p_receiverLocale, String p_senderMsisdn, String p_amount, String p_schType, String p_nextSchDate) {
        if (_log.isDebugEnabled()) {
            _log.debug(
                "getRecieverSuccessMessage",
                "p_receiverLocale=" + p_receiverLocale.getDisplayName() + "p_senderMsisdn=" + p_senderMsisdn + "p_amount=" + p_amount + "p_schType=" + p_schType + "p_nextSchDate=" + p_nextSchDate);
        }
        String[] messageArgArray = null;
        String key = null;
        if (p_schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            key = PretupsErrorCodesI.P2P_ERROR_SMCD_LIST_ADD_SUCCESS_R_W;
            messageArgArray = new String[] { p_senderMsisdn, p_amount, p_nextSchDate };
        }
        if (p_schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            key = PretupsErrorCodesI.P2P_ERROR_SMCD_LIST_ADD_SUCCESS_R_M;
            messageArgArray = new String[] { p_senderMsisdn, p_amount, p_nextSchDate };
        }
        return BTSLUtil.getMessage(p_receiverLocale, key, messageArgArray);
    }

    private String getReceiverDeleteMessage(Locale p_receiverLocale, String p_senderMsisdn, String p_amount, String p_schType, String p_nextSchDate) {
        if (_log.isDebugEnabled()) {
            _log.debug(
                "getRecieverSuccessMessage",
                "p_receiverLocale=" + p_receiverLocale.getDisplayName() + "p_senderMsisdn=" + p_senderMsisdn + "p_amount=" + p_amount + "p_schType=" + p_schType + "p_nextSchDate=" + p_nextSchDate);
        }
        String[] messageArgArray = null;
        String key = null;
        if (p_schType.equals(PretupsI.SCHEDULE_TYPE_WEEKLY_FILTER)) {
            key = PretupsErrorCodesI.P2P_ERROR_SMCD_LIST_DEL_SUCCESS_R_W;
            messageArgArray = new String[] { p_senderMsisdn, p_amount, p_nextSchDate };
        }
        if (p_schType.equals(PretupsI.SCHEDULE_TYPE_MONTHLY_FILTER)) {
            key = PretupsErrorCodesI.P2P_ERROR_SMCD_LIST_DEL_SUCCESS_R_M;
            messageArgArray = new String[] { p_senderMsisdn, p_amount, p_nextSchDate };
        }
        return BTSLUtil.getMessage(p_receiverLocale, key, messageArgArray);
    }

}
