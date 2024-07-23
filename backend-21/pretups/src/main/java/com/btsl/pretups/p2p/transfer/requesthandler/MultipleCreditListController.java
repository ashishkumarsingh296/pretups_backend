package com.btsl.pretups.p2p.transfer.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.grouptype.businesslogic.GroupTypeProfileVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.requesthandler.RegisterationController;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.p2p.transfer.businesslogic.MCDTxnDAO;

public class MultipleCreditListController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(MultipleCreditListController.class.getName());

    private String _requestIDStr;
    private RequestVO _requestVO = null;
    private static OperatorUtilI _operatorUtil = null;
    private int _totalMsisdnReqProcessed = 0;
    private int _successMsisdnCount = 0;
    private int _failMsisdnCount = 0;
    private String _failString = "";
    private String _senderMsisdn = null;
    private Locale _senderLocale = null;
    private SenderVO _senderVO;
    private String _listName = null;
    private String _gatewayCode = null;
    private MCDListVO _mcdListVO = null;
    private boolean _newUser = false;

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
        int listCount = 0;
        final MCDTxnDAO mcdtxnDAO = new MCDTxnDAO();

        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestIDStr, "Entered");
        }
        try {
            _senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (_senderVO == null) {
                new RegisterationController().regsiterNewUser(p_requestVO);
                _senderVO = (SenderVO) p_requestVO.getSenderVO();
                _senderVO.setDefUserRegistration(true);
                p_requestVO.setSenderLocale(new Locale(_senderVO.getLanguage(), _senderVO.getCountry()));
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

            _gatewayCode = p_requestVO.getRequestGatewayCode();

            try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                validateSenderPin(con, p_requestVO);
                if (BTSLUtil.isNullString(_listName)) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_NAME_BLANK);
                }
                listCount = mcdtxnDAO.isListAlreadyRegistered(con, _listName, _senderVO.getUserID());
                if (listCount == 0) {
                    if (!BTSLUtil.isAlphaNumeric(_listName)) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_ALLOWED_ALPHANUMERIC);
                    }
                    if (_listName.length() > 50) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_LENGTH_EXCEED);
                    } else if (p_requestVO.getMcdListAddCount() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_MAX_LIST_COUNT))).intValue()) {
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_COUNT_EXCEED);// rahul.d
                    }
                } else if (listCount + p_requestVO.getMcdListAddCount() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_MAX_LIST_COUNT))).intValue()) {
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
                    _mcdListVO = _operatorUtil.validateMCDListAMDRequest(con, messageArray, p_requestVO);
                    _mcdListVO.setListName(_listName);
                    _mcdListVO.setParentID(_senderVO.getUserID());
                    _mcdListVO.setCreatedOn(new Date());
                    _mcdListVO.setModifiedOn(new Date());
                    _mcdListVO.setModifiedBy(_senderVO.getUserID());
                    _mcdListVO.setCreatedBy(((SenderVO) p_requestVO.getSenderVO()).getUserID());

                    processMCDList(con, _mcdListVO);
                } catch (BTSLBaseException be) {
                    _log.error("process", "BTSLBaseException be:" + be.getMessage());
                    _log.errorTrace(METHOD_NAME, be);
                    if (!((BTSLUtil.isNullString(_mcdListVO.getSelector1())))) {
                        _totalMsisdnReqProcessed++;
                        _failMsisdnCount++;
                        _failString = _failString + _mcdListVO.getMsisdn() + ":" + _mcdListVO.getSelector1() + ":" + _mcdListVO.getAmount1String() + ":" + BTSLUtil
                            .getMessage(_senderVO.getLocale(), be.getMessageKey(), null) + ",";
                    }
                    if (!((BTSLUtil.isNullString(_mcdListVO.getSelector2())))) {
                        _totalMsisdnReqProcessed++;
                        _failMsisdnCount++;
                        _failString = _failString + _mcdListVO.getMsisdn() + ":" + _mcdListVO.getSelector2() + ":" + _mcdListVO.getAmount2String() + ":" + BTSLUtil
                            .getMessage(_senderVO.getLocale(), be.getMessageKey(), null) + ",";
                    }
                    continue;
                } catch (Exception e) {
                    _log.error("process", "Exception be:" + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                    if (!((BTSLUtil.isNullString(_mcdListVO.getSelector1())))) {
                        _totalMsisdnReqProcessed++;
                        _failMsisdnCount++;
                        _failString = _failString + _mcdListVO.getMsisdn() + ":" + _mcdListVO.getSelector1() + ":" + _mcdListVO.getAmount1() + ":" + PretupsErrorCodesI.P2P_ERROR_EXCEPTION + ",";
                    }
                    if (!((BTSLUtil.isNullString(_mcdListVO.getSelector2())))) {
                        _totalMsisdnReqProcessed++;
                        _failMsisdnCount++;
                        _failString = _failString + _mcdListVO.getMsisdn() + ":" + _mcdListVO.getSelector2() + ":" + _mcdListVO.getAmount2() + ":" + PretupsErrorCodesI.P2P_ERROR_EXCEPTION + ",";
                    }
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
                if (_senderLocale == null) {
                    _senderLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                }
                final String messageKey = _requestVO.getMessageCode();
                if (_totalMsisdnReqProcessed > 0 && (_totalMsisdnReqProcessed == _successMsisdnCount)) {
                    (new PushMessage(_senderMsisdn, getSenderSuccessMessage(_senderLocale, _listName), "", _gatewayCode, _senderLocale)).push();
                } else if (_failMsisdnCount > 0) {
                    (new PushMessage(_senderMsisdn, getSenderMessage(_senderLocale, _listName, _failString), "", _gatewayCode, _senderLocale)).push();
                } else {
                    (new PushMessage(_senderMsisdn, getSenderFailMessage(_senderLocale, _listName, messageKey), "", _gatewayCode, _senderLocale)).push();
                }

            }
            if (_newUser) {
                if (_senderLocale == null) {
                    _senderLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                }

                (new PushMessage(_senderMsisdn, getNewUserMessage(_senderLocale, _listName, PretupsErrorCodesI.P2P_MCDL_NEW_USER_REG), "", _gatewayCode, _senderLocale))
                    .push();
            }
            if (con != null) {

                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("MultipleCreditListController#process");
					mcomCon = null;
				}
                con = null;
            }

        }

    }

    public void processMCDList(Connection p_con, MCDListVO mcdListVO) throws BTSLBaseException {
        final String METHOD_NAME = "processMCDList";
        final MCDTxnDAO mcdtxn = new MCDTxnDAO();

        boolean amount1Added = false;
        boolean amount2Added = false;
        boolean amount1Modifed = false;
        boolean amount2Modifed = false;
        boolean amount1deleted = false;
        boolean amount2deleted = false;
        boolean selector1Found = false;
        boolean selector2Found = false;
        ArrayList selectorList = new ArrayList();
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;

        try {
            if (_log.isDebugEnabled()) {
                _log.debug("processMCDList ", _requestIDStr, "Entered mcdListVO" + mcdListVO);
            }
            if ("A".equals(mcdListVO.getAction())) {

                if (!((BTSLUtil.isNullString(mcdListVO.getSelector1())))) {
                    _totalMsisdnReqProcessed++;
                    selectorList = ServiceSelectorMappingCache.getSelectorListForServiceType(PretupsI.P2P_MCD_LIST_SERVICE_TYPE);
                    int selectorListsSize = selectorList.size();
                    for (int i = 0; i < selectorListsSize ; i++) {
                        serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
                        if (serviceSelectorMappingVO.getSelectorCode().equals(mcdListVO.getSelector1())) {
                            selector1Found = true;
                        }
                    }
                    if (selector1Found) {
                        try {
                            PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                            mcdListVO.setAmount1(PretupsBL.validateMCDListAmount(mcdListVO.getAmount1String()));
                            amount1Added = mcdtxn.addMCDListAmountDetailsForSelector(p_con, mcdListVO.getSelector1(), mcdListVO.getAmount1(), mcdListVO);
                        } catch (BTSLBaseException be) {
                            _log.errorTrace(METHOD_NAME, be);
                            mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                        }

                    } else {
                        mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_SELECTOR_INVALID, null));
                    }
                    if (amount1Added) {
                        _successMsisdnCount++;
                    } else {
                        _failMsisdnCount++;
                    }

                }

                if (!((BTSLUtil.isNullString(mcdListVO.getSelector2())))) {
                    _totalMsisdnReqProcessed++;
                    selectorList = ServiceSelectorMappingCache.getSelectorListForServiceType(PretupsI.P2P_MCD_LIST_SERVICE_TYPE);
                    for (int i = 0; i < selectorList.size(); i++) {
                        serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
                        if (serviceSelectorMappingVO.getSelectorCode().equals(mcdListVO.getSelector2())) {
                            selector2Found = true;
                        }
                    }
                    if (selector2Found) {
                        try {
                            PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                            mcdListVO.setAmount2(PretupsBL.validateMCDListAmount(mcdListVO.getAmount2String()));
                            amount2Added = mcdtxn.addMCDListAmountDetailsForSelector(p_con, mcdListVO.getSelector2(), mcdListVO.getAmount2(), mcdListVO);
                        } catch (BTSLBaseException be) {
                            _log.errorTrace(METHOD_NAME, be);
                            mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                        }
                    } else {
                        mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_SELECTOR_INVALID, null));
                    }
                    if (amount2Added) {
                        _successMsisdnCount++;
                    } else {
                        _failMsisdnCount++;
                    }
                }

                if (!(amount1Added)) {
                    if (!((BTSLUtil.isNullString(mcdListVO.getSelector1())))) {
                        _failMsisdnCount++;
                        _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector1() + ":" + mcdListVO.getAmount1() + ":" + mcdListVO.getReason1() + ",";
                    }
                }
                if (!amount2Added) {
                    if (!((BTSLUtil.isNullString(mcdListVO.getSelector2())))) {
                        _failMsisdnCount++;
                        _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector2() + ":" + mcdListVO.getAmount2() + ":" + mcdListVO.getReason2() + ",";
                    }

                }

            } else if ("M".equals(mcdListVO.getAction())) {

                if (!((BTSLUtil.isNullString(mcdListVO.getSelector1())))) {
                    _totalMsisdnReqProcessed++;
                    selectorList = ServiceSelectorMappingCache.getSelectorListForServiceType(PretupsI.P2P_MCD_LIST_SERVICE_TYPE);
                    for (int i = 0; i < selectorList.size(); i++) {
                        serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
                        if (serviceSelectorMappingVO.getSelectorCode().equals(mcdListVO.getSelector1())) {
                            selector1Found = true;
                        }
                    }
                    if (selector1Found) {
                        try {
                            PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                            mcdListVO.setAmount1(PretupsBL.validateMCDListAmount(mcdListVO.getAmount1String()));
                            amount1Modifed = mcdtxn.updateMCDListAmountDetailsForSelector(p_con, mcdListVO.getSelector1(), mcdListVO.getAmount1(), mcdListVO);
                        } catch (BTSLBaseException be) {
                            _log.errorTrace(METHOD_NAME, be);
                            mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                        }
                    } else {
                        mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_SELECTOR_INVALID, null));
                    }
                    if (amount1Modifed) {
                        _successMsisdnCount++;
                    } else {
                        _failMsisdnCount++;
                    }
                }

                if (!((BTSLUtil.isNullString(mcdListVO.getSelector2())))) {
                    _totalMsisdnReqProcessed++;
                    selectorList = ServiceSelectorMappingCache.getSelectorListForServiceType(PretupsI.P2P_MCD_LIST_SERVICE_TYPE);
                    for (int i = 0; i < selectorList.size(); i++) {
                        serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
                        if (serviceSelectorMappingVO.getSelectorCode().equals(mcdListVO.getSelector2())) {
                            selector2Found = true;
                        }
                    }
                    if (selector2Found) {
                        try {
                            PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                            mcdListVO.setAmount2(PretupsBL.validateMCDListAmount(mcdListVO.getAmount2String()));
                            amount2Modifed = mcdtxn.updateMCDListAmountDetailsForSelector(p_con, mcdListVO.getSelector2(), mcdListVO.getAmount2(), mcdListVO);
                        } catch (BTSLBaseException be) {
                            _log.errorTrace(METHOD_NAME, be);
                            mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                        }
                    } else {
                        mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_SELECTOR_INVALID, null));
                    }

                    if (amount2Modifed) {
                        _successMsisdnCount++;
                    } else {
                        _failMsisdnCount++;
                    }
                }

                if (!amount1Modifed) {
                    if (!((BTSLUtil.isNullString(mcdListVO.getSelector1())))) {
                        _failMsisdnCount++;
                        _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector1() + ":" + mcdListVO.getAmount1() + ":" + mcdListVO.getReason1() + ",";
                    }
                }
                if (!amount2Modifed) {
                    if (!((BTSLUtil.isNullString(mcdListVO.getSelector2())))) {
                        _failMsisdnCount++;
                        _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector2() + ":" + mcdListVO.getAmount2() + ":" + mcdListVO.getReason2() + ",";
                    }

                }

            } else if ("D".equals(mcdListVO.getAction())) {

                if (!((BTSLUtil.isNullString(mcdListVO.getSelector1())))) {
                    _totalMsisdnReqProcessed++;
                    selectorList = ServiceSelectorMappingCache.getSelectorListForServiceType(PretupsI.P2P_MCD_LIST_SERVICE_TYPE);
                    for (int i = 0; i < selectorList.size(); i++) {
                        serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
                        if (serviceSelectorMappingVO.getSelectorCode().equals(mcdListVO.getSelector1())) {
                            selector1Found = true;
                        }
                    }
                    if (selector1Found) {
                        try {
                            PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                            amount1deleted = mcdtxn.deleteMCDListAmountDetailsForSelector(p_con, mcdListVO.getSelector1(), mcdListVO);
                        } catch (BTSLBaseException be) {
                            _log.errorTrace(METHOD_NAME, be);
                            mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                        }
                    } else {
                        mcdListVO.setReason1(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_SELECTOR_INVALID, null));
                    }
                    if (amount1deleted) {
                        _successMsisdnCount++;
                    } else {
                        _failMsisdnCount++;
                    }

                }

                if (!((BTSLUtil.isNullString(mcdListVO.getSelector2())))) {
                    _totalMsisdnReqProcessed++;
                    selectorList = ServiceSelectorMappingCache.getSelectorListForServiceType(PretupsI.P2P_MCD_LIST_SERVICE_TYPE);
                    for (int i = 0; i < selectorList.size(); i++) {
                        serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
                        if (serviceSelectorMappingVO.getSelectorCode().equals(mcdListVO.getSelector2())) {
                            selector2Found = true;
                        }
                    }
                    if (selector2Found) {
                        try {
                            PretupsBL.validateMCDListMsisdn(p_con, mcdListVO, mcdListVO.getMsisdn());
                            amount2deleted = mcdtxn.deleteMCDListAmountDetailsForSelector(p_con, mcdListVO.getSelector2(), mcdListVO);
                        } catch (BTSLBaseException be) {
                            _log.errorTrace(METHOD_NAME, be);
                            mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), be.getMessageKey(), null));
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_MCDL_INVALID_OPERATION, null));
                        }
                    } else {
                        mcdListVO.setReason2(BTSLUtil.getMessage(_senderVO.getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_SELECTOR_INVALID, null));
                    }
                    if (amount2deleted) {
                        _successMsisdnCount++;
                    } else {
                        _failMsisdnCount++;
                    }
                }

                if (!amount1deleted) {
                    if (!((BTSLUtil.isNullString(mcdListVO.getSelector1())))) {
                        _failMsisdnCount++;
                        _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector1() + ":" + mcdListVO.getAmount1() + ":" + mcdListVO.getReason1() + ",";
                    }
                }
                if (!amount2deleted) {
                    if (!((BTSLUtil.isNullString(mcdListVO.getSelector2())))) {
                        _failMsisdnCount++;
                        _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector2() + ":" + mcdListVO.getAmount2() + ":" + mcdListVO.getReason2() + ",";
                    }

                }

            } else {
                if (!((BTSLUtil.isNullString(mcdListVO.getSelector1())))) {
                    _failMsisdnCount++;
                    _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector1() + ":" + mcdListVO.getAmount1() + ":" + BTSLUtil.getMessage(_senderVO
                        .getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_ACTION_INVALID, null) + ",";
                }
                if (!((BTSLUtil.isNullString(mcdListVO.getSelector2())))) {
                    _failMsisdnCount++;
                    _failString = _failString + mcdListVO.getMsisdn() + ":" + mcdListVO.getSelector2() + ":" + mcdListVO.getAmount2() + ":" + BTSLUtil.getMessage(_senderVO
                        .getLocale(), PretupsErrorCodesI.P2P_ERROR_MCD_LIST_ACTION_INVALID, null) + ",";
                }
            }
           }

        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("processMCDList", "  Exception while processing list :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultipleCreditListController[processMCDList]", "", "",
                "", "Exception while processing list " + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("process", "processMCDList", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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

    private String getSenderMessage(Locale p_senderLocale, String p_listName, String p_message) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderFailMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName + " p_message" + p_message);
        }
        String key = null;
        String[] messageArgArray = null;

        key = PretupsErrorCodesI.P2P_ERROR_MCD_LIST_ADD_FAIL_MSISDN;
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
        final String actualPin = _senderVO.getPin();
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
            if (BTSLUtil.isNullString(pin) && !_newUser) {
                throw new BTSLBaseException("MultipleCreditListController", "validateSenderPin", PretupsErrorCodesI.P2P_MCDL_PIN_REQUIRED);
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
                            throw new BTSLBaseException("MultipleCreditListController", "validateSenderPin", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                        }
                    }
                    throw be;
                }
            }
        }

    }

    private String getNewUserMessage(Locale p_senderLocale, String p_listName, String p_key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getNewUserMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName);
        }

        String[] messageArgArray = null;
        messageArgArray = new String[] { p_listName, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)) };

        return BTSLUtil.getMessage(p_senderLocale, p_key, messageArgArray);
    }

}
