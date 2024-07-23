/*
 * Created on Apr 16, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixServiceTypeMappingCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixServiceTypeVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @author ranjana.chouhan
 * 
 *         This controller is designed to perform recharge and postpaid bill
 *         payment
 *         through one service i.e. CRC .
 */
public class C2SRechargeController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(C2SRechargeController.class.getName());
    private ReceiverVO _receiverVO = null;
    private static OperatorUtilI _operatorUtil;
    private String _receiverMsisdn;
    private ChannelUserVO _channelUserVO = null;;
    private C2STransferVO _c2sTransferVO = null;
    private RequestVO _requestVO = null;
    private Locale _senderLocale;
    private String _interfaceCategory;
    private String _type;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SRechargeController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;

        if (_log.isDebugEnabled()) {
            _log.debug("process", p_requestVO.getRequestIDStr(), "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
        }

        try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            _c2sTransferVO = new C2STransferVO();
            _type = p_requestVO.getType();
            _receiverVO = new ReceiverVO();
            _senderLocale = p_requestVO.getSenderLocale();
            // _type=_requestVO.getType();
            populateVOFromRequest(p_requestVO);

            final String[] _requestArr = p_requestVO.getRequestMessageArray();
            for (int i = 0; i < _requestArr.length; i++) {
                _log.debug("process", "i :" + i + "Value :" + _requestArr[i]);
            }

            // _serviceType=p_requestVO.getServiceType();
            // p_requestVO.setCommonServiceType(_serviceType);
            _receiverMsisdn = _requestArr[1];

            // validate receiver msisdn, if prefix is not found in network and
            // number length greater or less than defined length then it will
            // throws exception.

            validateMsisdn(con, _receiverVO, _receiverMsisdn);
            _receiverMsisdn = _receiverVO.getMsisdn();

            _c2sTransferVO.setReceiverVO(_receiverVO);
            _receiverVO = (ReceiverVO) _c2sTransferVO.getReceiverVO();
            _c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
            _receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));

            if (!_receiverVO.getSubscriberType().equals(_type)) {
                // Refuse the Request
                _log.error(this, "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "C2SPrepaidController[process]", "", "", "",
                    "Series =" + _receiverVO.getMsisdnPrefix() + " Not Defined for Series type=" + _type + " But request initiated for the same");
                throw new BTSLBaseException("", "process", PretupsErrorCodesI.ERROR_NOTFOUND_SERIES_TYPE, 0, new String[] { _receiverVO.getMsisdn() }, null);
            }
            // Validates the network service status

            // PretupsBL.validateNetworkService(_c2sTransferVO);

            try {
                if (con != null) {
                    con.close();
                }
                con = null;
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }

            final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _type);
            if (networkPrefixVO != null) {
                _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            }
            final NetworkPrefixServiceTypeVO nwPrxfServiceVO = (NetworkPrefixServiceTypeVO) NetworkPrefixServiceTypeMappingCache.getObject(String.valueOf(networkPrefixVO
                .getPrefixID()), _receiverVO.getNetworkCode());
            p_requestVO.setServiceType(nwPrxfServiceVO.getServiceType());
            final ListValueVO listValueVO = BTSLUtil.getOptionDesc(p_requestVO.getServiceType(), _channelUserVO.getAssociatedServiceTypeList());
            if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                _log.error("validateServiceType", p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Service Type not found in allowed List");
                throw new BTSLBaseException("C2SRechargeController", "process", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
            } else if (!PretupsI.YES.equals(listValueVO.getLabel())) {
                _log.error("validateServiceType", p_requestVO.getRequestIDStr(), " MSISDN=" + p_requestVO.getFilteredMSISDN() + " Service Type is suspended in allowed List");
                throw new BTSLBaseException("C2SRechargeController", "process", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED);
            }// end if
            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL.getServiceKeywordHandlerObj(nwPrxfServiceVO.getHandlerClass());
            controllerObj.process(p_requestVO);
        } catch (BTSLBaseException be) {

            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
            }
            _log.error("C2SRechargeController[process]", "Getting BTSL Base Exception:" + be.getMessage());

            p_requestVO.setSuccessTxn(false);
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

            if (!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage())) {
                p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());
            }

            if (be.isKey()) // checking if baseexception has key
            {
                if (_c2sTransferVO.getErrorCode() == null) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                }

                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                // setting default error code if message and key is not found
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }
            _log.errorTrace(METHOD_NAME, be);

        } catch (Exception e) {
            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            }

            p_requestVO.setSuccessTxn(false);
            _c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
            _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            _log.error("process", "Exception:" + e.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SEnquiryHandler[process]",_transferID,_msisdn,_senderNetworkCode,"Exception:"+e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("C2SRechargeController", "process", " Exited ");
            }
        }
    }

    /*
     * method for validating receiver msisdn.
     * and to check prefix defined in the system or not.
     */
    public void validateMsisdn(Connection p_con, ReceiverVO p_receiverVO, String p_receiverMsisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateMsisdn", "Entered for p_receiverMsisdn= " + p_receiverMsisdn);
        }
        final String METHOD_NAME = "validateMsisdn";
        String[] strArr = null;
        try {
            if (BTSLUtil.isNullString(p_receiverMsisdn)) {
                throw new BTSLBaseException("C2SRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            p_receiverMsisdn = getFilteredMSISDN(p_receiverMsisdn);
            p_receiverMsisdn = _operatorUtil.addRemoveDigitsFromMSISDN(p_receiverMsisdn);
            if ((p_receiverMsisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() || p_receiverMsisdn.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())) {
                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() != ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                    strArr = new String[] { p_receiverMsisdn, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) };
                    throw new BTSLBaseException("C2SRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_receiverMsisdn, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) };
                    throw new BTSLBaseException("C2SRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_LEN_NOTSAME, 0, strArr, null);
                }
            }
            try {
                final long lng = Long.parseLong(p_receiverMsisdn);
            } catch (Exception e) {
                strArr = new String[] { p_receiverMsisdn };
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("C2SRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTNUMERIC, 0, strArr, null);
            }
            p_receiverVO.setMsisdn(p_receiverMsisdn);
            if (_log.isDebugEnabled() && p_receiverVO.getMsisdn() != null) {
                _log.debug("", "*********************" + p_receiverVO.getMsisdn());
            }

            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_receiverMsisdn, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                strArr = new String[] { p_receiverMsisdn };
                throw new BTSLBaseException("C2SRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }
            p_receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
            p_receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            p_receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("validateMsisdn", "  Exception while validating msisdn :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SRechargeController[validateMsisdn]", "", "", "",
                "Exception while validating msisdn" + " ,getting Exception=" + e.getMessage());
            throw new BTSLBaseException("C2SRechargeController", "validateMsisdn", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validateMsisdn", "Exiting for p_receiverMsisdn= " + p_receiverMsisdn);
            }
        }
    }

    /*
     * method is used to perform filtering of receiver msisdn
     * and remove prefix from msisdn
     */
    private String getFilteredMSISDN(String p_receiverMsisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getFilteredMSISDN", "Entered p_receiverMsisdn:" + p_receiverMsisdn);
        }
        final String METHOD_NAME = "getFilteredMSISDN";
        String msisdn = null;
        boolean prefixFound = false;
        String prefix = null;
        try {
            if (p_receiverMsisdn.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("getFilteredMSISDN", "((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)):" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)));
                }

                final StringTokenizer strTok = new StringTokenizer(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE)), ",");
                while (strTok.hasMoreTokens()) {
                    prefix = strTok.nextToken();
                    if (p_receiverMsisdn.startsWith(prefix, 0)) {
                        prefixFound = true;
                        break;
                    } else {
                        continue;
                    }
                }
                if (prefixFound) {
                    msisdn = p_receiverMsisdn.substring(prefix.length());
                } else {
                    msisdn = p_receiverMsisdn;
                }

            } else {
                msisdn = p_receiverMsisdn;
            }
        } catch (Exception e) {
            _log.error("getFilteredMSISDN", "Exception while getting the mobile no from passed no=" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SRechargeController[getFilteredMSISDN]", "",
                p_receiverMsisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("C2SRechargeController", "getFilteredMSISDN", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getFilteredMSISDN", "Exiting Filtered msisdn=" + msisdn);
            }
        }
        return msisdn;
    }

    private void populateVOFromRequest(RequestVO p_requestVO) {
        _c2sTransferVO.setSenderVO(_channelUserVO);
        _c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
        _c2sTransferVO.setModule(p_requestVO.getModule());
        _c2sTransferVO.setInstanceID(p_requestVO.getInstanceID());
        _c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        _c2sTransferVO.setServiceType(p_requestVO.getServiceType());
        _c2sTransferVO.setSourceType(p_requestVO.getSourceType());
        _c2sTransferVO.setCreatedBy(_channelUserVO.getUserID());
        _c2sTransferVO.setSenderMsisdn((_channelUserVO.getUserPhoneVO()).getMsisdn());
        _c2sTransferVO.setSenderID(_channelUserVO.getUserID());
        _c2sTransferVO.setNetworkCode(_channelUserVO.getNetworkID());
        _c2sTransferVO.setLocale(_senderLocale);
        _c2sTransferVO.setLanguage(_c2sTransferVO.getLocale().getLanguage());
        _c2sTransferVO.setCountry(_c2sTransferVO.getLocale().getCountry());
        (_channelUserVO.getUserPhoneVO()).setLocale(_senderLocale);
        _c2sTransferVO.setReferenceID(p_requestVO.getExternalReferenceNum());
    }// end populateVOFromRequest
}
