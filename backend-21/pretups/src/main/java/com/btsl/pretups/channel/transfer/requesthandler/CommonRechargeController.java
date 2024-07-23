/*
 * Created on Apr 16, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonClient;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadController;
import com.btsl.loadcontroller.LoadControllerI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.ResumeSuspendProcess;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingVO;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlVO;
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
public class CommonRechargeController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(CommonRechargeController.class.getName());
    private ReceiverVO _receiverVO = null;
    private SubscriberRoutingControlVO _subscriberRoutingControlVO = null;
    private static OperatorUtilI _operatorUtil;
    private String _interfaceID;
    private Object _senderExternalID;
    private boolean _isRequestRefuse;
    private String _transferID;
    private String _receiverMsisdn;
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private int _intModPortS;
    private String _intModClassNameS;
    private String _interfaceStatusType;
    private RequestVO _requestVO = null;;
    private boolean _decreaseCountersReqd;
    private String _interfaceCatgeory;
    private String _handlerClass;
    private Object _txnStatus;
    private ChannelUserVO _channelUserVO = null;;
    private C2STransferVO _c2sTransferVO = null;
    private Locale _senderLocale;
    private String _serviceType;
    private String _interfaceCategory;
    private ListValueVO _listValueVO = null;
    private MSISDNPrefixInterfaceMappingVO _interfaceMappingVO = null;
    private ServiceInterfaceRoutingVO _serviceInterfaceRoutingVO = null;
    private boolean isSuccess;
    private String status;
    private String _commonServiecType;
    private String _type;
    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommonRechargeController[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        StringBuilder loggerValue= new StringBuilder(); 
		        
        Connection con = null;

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered for Request ID=");
        	loggerValue.append(p_requestVO.getRequestID());
        	loggerValue.append(" MSISDN=");
        	loggerValue.append(p_requestVO.getFilteredMSISDN());
            _log.debug("process", p_requestVO.getRequestIDStr(),  loggerValue);
        }

        try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            _c2sTransferVO = new C2STransferVO();

            _receiverVO = new ReceiverVO();
            _senderLocale = p_requestVO.getSenderLocale();
            _type = _requestVO.getType();
            populateVOFromRequest(p_requestVO);

            final String[] _requestArr = p_requestVO.getRequestMessageArray();
            for (int i = 0; i < _requestArr.length; i++) {
                _log.debug("process", "i :" + i + "Value :" + _requestArr[i]);
            }

            _serviceType = p_requestVO.getServiceType();
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

            // Validates the network service status

            PretupsBL.validateNetworkService(_c2sTransferVO);

            try {
                if (con != null) {
                    con.close();
                }
                con = null;
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }

            /*
             * When one series is used for both pre and post, so we get the
             * interface type from service interface routing.
             */
            if (p_requestVO.getType().equals(PretupsI.NOT_APPLICABLE)) {
                _serviceInterfaceRoutingVO = ServiceInterfaceRoutingCache
                    .getServiceInterfaceRoutingDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + PretupsI.NOT_APPLICABLE);
                if (_serviceInterfaceRoutingVO != null) {
                    if (_log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append( "For =");
                    	loggerValue.append(_receiverVO.getNetworkCode());
                    	loggerValue.append(" ");
                    	loggerValue.append(p_requestVO.getServiceType());
                    	loggerValue.append(" Got Interface Category=");
                    	loggerValue.append(_serviceInterfaceRoutingVO.getInterfaceType());
                    	loggerValue.append(" Alternate Check Required=" );
                    	loggerValue.append(_serviceInterfaceRoutingVO.isAlternateInterfaceCheckBool() );
                    	loggerValue.append( " Alternate Interface=");
                    	loggerValue.append(_serviceInterfaceRoutingVO.getAlternateInterfaceType());
                        _log.debug("process",loggerValue);
                    }
                    _interfaceCategory = _serviceInterfaceRoutingVO.getInterfaceType();
                } else {
                    throw new BTSLBaseException(this, "CommonRechargeController", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING);
                }
            } else {
                throw new BTSLBaseException(this, "CommonRechargeController", PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEINTERFACEMAPPING);
            }

            /*
             * Get the details from routing control.we are considering only one
             * case, where database and series check is 'N'
             */

            _subscriberRoutingControlVO = SubscriberRoutingControlCache
                .getRoutingControlDetails(_receiverVO.getNetworkCode() + "_" + p_requestVO.getServiceType() + "_" + _interfaceCategory);
            if (_subscriberRoutingControlVO != null) {
                // If database check boolean is true,then write code here.
                if (_subscriberRoutingControlVO.isDatabaseCheckBool()) {
                    // ignore
                }
                // If series check boolean is true, then write code here.
                else if (_subscriberRoutingControlVO.isSeriesCheckBool()) {
                    // ignore
                }
            }

            final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(_receiverVO.getMsisdnPrefix(), _interfaceCategory);

            if (networkPrefixVO != null) {
                _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
            }

            _interfaceMappingVO = (MSISDNPrefixInterfaceMappingVO) MSISDNPrefixInterfaceMappingCache.getObject(_receiverVO.getPrefixID(), _interfaceCategory,
                PretupsI.INTERFACE_VALIDATE_ACTION);

            setInterfaceDetails(_receiverVO, _listValueVO, true, _interfaceMappingVO);

            /*
             * send the request on IN to get the subscriber type i.e. PRE or
             * POST or UNKNOWN
             * If PRE call c2s prepaid controller, post call postpaid bill
             * payment controller
             * if unknown throw exception.
             */

            processCRCValidationRequest(_receiverVO, _interfaceMappingVO, _interfaceCategory);

            try {
                if (PretupsI.INTERFACE_CATEGORY_POST.equalsIgnoreCase(_requestVO.getReceiverSubscriberType()) && InterfaceErrorCodesI.SUCCESS.equals(status)) {
                    _requestVO.setServiceType(PretupsI.SERVICE_TYPE_CHNL_BILLPAY);
                    boolean servicePPBAllow = false;
                    final Iterator itr = _channelUserVO.getAssociatedServiceTypeList().iterator();
                    while (itr.hasNext()) {
                        _listValueVO = (ListValueVO) itr.next();
                        if (_requestVO.getServiceType().equalsIgnoreCase(_listValueVO.getValue()) && PretupsI.STATUS_ACTIVE.equalsIgnoreCase(_listValueVO.getLabel())) {
                            servicePPBAllow = true;
                            break;
                        }
                    }
                    if (!servicePPBAllow) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, _interfaceID, EventLevelI.INFO, "CommonRechargeController",
                            _receiverMsisdn, _serviceType, _interfaceCategory, "Service not allowed to sender");
                        throw new BTSLBaseException("CommonRechargeController", "process", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                    }
                    if (!BTSLUtil.isNullString(_requestVO.getReqSelector())) {
                        final String[] reqArr = _requestVO.getRequestMessageArray();
                        reqArr[3] = "2";
                        _requestVO.setRequestMessageArray(reqArr);

                    }
                    _requestVO.setReqSelector("2");
                    _requestVO.setType(PretupsI.INTERFACE_CATEGORY_POST);
                    final PostPaidBillPaymentController postPaidBillPaymentController = new PostPaidBillPaymentController();
                    postPaidBillPaymentController.process(_requestVO);

                } else if (PretupsI.INTERFACE_CATEGORY_PRE.equalsIgnoreCase(_requestVO.getReceiverSubscriberType()) && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND
                    .equals(status)) {
                    _requestVO.setServiceType(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                    boolean serviceRCAllow = false;
                    final Iterator iterator = _channelUserVO.getAssociatedServiceTypeList().iterator();
                    while (iterator.hasNext()) {
                        _listValueVO = (ListValueVO) iterator.next();
                        if (p_requestVO.getServiceType().equalsIgnoreCase(_listValueVO.getValue()) && PretupsI.STATUS_ACTIVE.equalsIgnoreCase(_listValueVO.getLabel())) {
                            serviceRCAllow = true;
                            break;
                        }
                    }
                    if (!serviceRCAllow) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, _interfaceID, EventLevelI.INFO, "CommonRechargeController",
                            _receiverMsisdn, _serviceType, _interfaceCategory, "Service not allowed to sender");
                        throw new BTSLBaseException("CommonRechargeController", "process", PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                    }
                    if (!BTSLUtil.isNullString(_requestVO.getReqSelector())) {
                        final String[] reqArr = _requestVO.getRequestMessageArray();
                        reqArr[3] = "1";
                        _requestVO.setRequestMessageArray(reqArr);

                    }
                    _requestVO.setReqSelector("1");
                    p_requestVO.setType(PretupsI.INTERFACE_CATEGORY_PRE);
                    final C2SPrepaidController c2sPrepaidController = new C2SPrepaidController();
                    c2sPrepaidController.process(_requestVO);
                } else if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, _interfaceID, EventLevelI.INFO, "CommonRechargeController",
                        _receiverMsisdn, _interfaceCategory, _interfaceCategory, "Unkown subscriber,msisdn not found on interface");
                    throw new BTSLBaseException(this, "process", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, _interfaceID, EventLevelI.INFO, "CommonRechargeController",
                        _receiverMsisdn, _interfaceCategory, _interfaceCategory, "C2S Error Exception");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }

            }

            catch (BTSLBaseException be) {
                throw be;
            } catch (Exception e) {
                throw new BTSLBaseException(this, METHOD_NAME, "");
            }
        }

        catch (BTSLBaseException be) {

            if (BTSLUtil.isNullString(_c2sTransferVO.getErrorCode())) {
                if (be.isKey()) {
                    _c2sTransferVO.setErrorCode(be.getMessageKey());
                } else {
                    _c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
                }
            }
            loggerValue.setLength(0);
        	loggerValue.append("Getting BTSL Base Exception:" );
        	loggerValue.append(be.getMessage());
            _log.error("CommonRechargeController[process]", loggerValue);

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
            loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(e.getMessage());
            _log.error("process", loggerValue );
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
                _log.debug("CommonRechargeController", "process", " Exited ");
            }
        }
    }

    /*
     * method for validating receiver msisdn.
     * and to check prefix defined in the system or not.
     */
    public void validateMsisdn(Connection p_con, ReceiverVO p_receiverVO, String p_receiverMsisdn) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
		        
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered for p_receiverMsisdn= ");
        	loggerValue.append(p_receiverMsisdn);
            _log.debug("validateMsisdn",  loggerValue );
        }
        final String METHOD_NAME = "validateMsisdn";
        String[] strArr = null;
        try {
            if (BTSLUtil.isNullString(p_receiverMsisdn)) {
                throw new BTSLBaseException("CommonRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
            }
            p_receiverMsisdn = getFilteredMSISDN(p_receiverMsisdn);
            p_receiverMsisdn = _operatorUtil.addRemoveDigitsFromMSISDN(p_receiverMsisdn);
            if ((p_receiverMsisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() || p_receiverMsisdn.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())) {
                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() != ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) {
                    strArr = new String[] { p_receiverMsisdn, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()) };
                    throw new BTSLBaseException("CommonRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE, 0, strArr, null);
                } else {
                    strArr = new String[] { p_receiverMsisdn, String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) };
                    throw new BTSLBaseException("CommonRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_LEN_NOTSAME, 0, strArr, null);
                }
            }
            try {
                final long lng = Long.parseLong(p_receiverMsisdn);
            } catch (Exception e) {
                strArr = new String[] { p_receiverMsisdn };
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("CommonRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTNUMERIC, 0, strArr, null);
            }
            p_receiverVO.setMsisdn(p_receiverMsisdn);
            if (_log.isDebugEnabled() && p_receiverVO.getMsisdn() != null) {
            	loggerValue.setLength(0);
            	loggerValue.append("*********************");
            	loggerValue.append(p_receiverVO.getMsisdn());
                _log.debug("",  loggerValue );
            }

            final NetworkPrefixVO networkPrefixVO = PretupsBL.getNetworkDetails(p_receiverMsisdn, PretupsI.USER_TYPE_RECEIVER);
            if (networkPrefixVO == null) {
                strArr = new String[] { p_receiverMsisdn };
                throw new BTSLBaseException("CommonRechargeController", "validateMsisdn", PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK, 0, strArr, null);
            }
            p_receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
        	loggerValue.append("  Exception while validating msisdn :" );
        	loggerValue.append(e.getMessage());
            _log.error("validateMsisdn", loggerValue);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception while validating msisdn" );
        	loggerValue.append( " ,getting Exception=");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommonRechargeController[validateMsisdn]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException("CommonRechargeController", "validateMsisdn", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting for p_receiverMsisdn= ");
            	loggerValue.append(p_receiverMsisdn);
                _log.debug("validateMsisdn",  loggerValue );
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommonRechargeController[getFilteredMSISDN]", "",
                p_receiverMsisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CommonRechargeController", "getFilteredMSISDN", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getFilteredMSISDN", "Exiting Filtered msisdn=" + msisdn);
            }
        }
        return msisdn;
    }

    /*
     * Method to send the validation request on the interface and check whether
     * that is success of failed
     */
    public void processCRCValidationRequest(ReceiverVO p_receiverVO, MSISDNPrefixInterfaceMappingVO p_interfaceMappingVO, String _interfaceCategory) {
    	StringBuilder loggerValue= new StringBuilder(); 
		      
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
          	loggerValue.append("Entered :: _interfaceID=" );
          	loggerValue.append(p_interfaceMappingVO.getInterfaceID());
          	loggerValue.append(",_handlerClass =");
          	loggerValue.append(p_interfaceMappingVO.getHandlerClass());
          	loggerValue.append(",_interfaceCategory =" );
          	loggerValue.append(_interfaceCategory);
            _log.debug("processCRCValidationRequest", loggerValue);
        }
        final String METHOD_NAME = "processCRCValidationRequest";
        try {
            checkTransactionLoad(p_receiverVO, p_interfaceMappingVO.getInterfaceID());
            _decreaseCountersReqd = true;

            final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.C2S_MODULE, p_receiverVO
                .getNetworkCode(), _interfaceCategory);
            _intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
            _intModIPS = networkInterfaceModuleVOS.getIP();
            _intModPortS = networkInterfaceModuleVOS.getPort();
            _intModClassNameS = networkInterfaceModuleVOS.getClassName();

            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

            final CommonClient commonClient = new CommonClient();
            final String requestStr = getReceiverValidateStr(_interfaceID, p_interfaceMappingVO.getHandlerClass());

            final String receiverValResponse = commonClient.process(requestStr, "", _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

            final HashMap map = BTSLUtil.getStringToHash(receiverValResponse, "&", "=");
            status = (String) map.get("TRANSACTION_STATUS");
            // _requestVO.setReceiverSubscriberType((String)map.get("INT_SUBS_TYPE"));

            final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
            if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
                .equals(interfaceStatusType))) {
                new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, p_interfaceMappingVO.getInterfaceID(), interfaceStatusType,
                    PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG, PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            }

            ArrayList altList = null;
            boolean isRequired = false;
            if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
                _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                altList = InterfaceRoutingControlCache.getRoutingControlDetails(p_interfaceMappingVO.getInterfaceID());
                if (altList != null && !altList.isEmpty()) {
                    performSenderAlternateRouting(p_receiverVO, altList, _interfaceCatgeory);
                } else {
                    isRequired = true;
                }
            }
            _requestVO.setReceiverSubscriberType((String) map.get("INT_SUBS_TYPE"));
            if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) || isRequired) {
                if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
                    _c2sTransferVO.setErrorCode(status + "_R");
                    _receiverVO.setTransactionStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
                    throw new BTSLBaseException("CommonRechargeController", "processCRCValidationRequest", _c2sTransferVO.getErrorCode());
                }
            }
        } catch (Exception e) {
            isSuccess = false;
            loggerValue.setLength(0);
          	loggerValue.append("BTSLBaseException ");
          	loggerValue.append(e.getMessage());
            _log.error("processCRCValidationRequest",  loggerValue );
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
          	loggerValue.append("Exception:" );
          	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommonRechargeController[process]", "", "", "",
            		loggerValue.toString());
            // throw new
            // BTSLBaseException(this,"processCRCValidationRequest",PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (_decreaseCountersReqd) {
                LoadController.decreaseTransactionInterfaceLoad(_transferID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);
            }

        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
          	loggerValue.append( "Exiting with isSuccess");
          	loggerValue.append(isSuccess);
            _log.debug("processCRCValidationRequest", loggerValue );
        }

    }

    /**
     * Method to check the loads available in the system
     * 
     * @param p_receiverVO
     * @param p_interfaceID
     * @throws BTSLBaseException
     */

    private void checkTransactionLoad(ReceiverVO p_receiverVO, String p_interfaceID) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
		       
    	if (_log.isDebugEnabled()) {
    		 loggerValue.setLength(0);
         	loggerValue.append("Checking load for MSISDN =");
         	loggerValue.append( p_receiverVO.getMsisdn() );
         	loggerValue.append(", p_interfaceID=");
         	loggerValue.append(p_interfaceID);
            _log.debug("checkTransactionLoad",  loggerValue);
        }
        final String METHOD_NAME = "checkTransactionLoad";
        int recieverLoadStatus = 0;
        try {
            // Do not enter the request in Queue
            recieverLoadStatus = LoadController.checkInterfaceLoad(p_receiverVO.getNetworkCode(), p_interfaceID, null, new C2STransferVO(), false);
            if (recieverLoadStatus == 0) {
                LoadController.checkTransactionLoad(p_receiverVO.getNetworkCode(), p_interfaceID, PretupsI.C2S_MODULE, null, true, LoadControllerI.USERTYPE_SENDER);
                if (_log.isDebugEnabled()) {
                    _log.debug("CommonRechargeController[checkTransactionLoad]", "_transferID=" + null + " Successfully through load");
                }
            }
            // Request in Queue
            else if (recieverLoadStatus == 1) {
                throw new BTSLBaseException("CommonRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
            // Refuse the request
            else {
                throw new BTSLBaseException("CommonRechargeController", "checkTransactionLoad", PretupsErrorCodesI.REQUEST_REFUSE_FROM_INTLOAD);
            }
        } catch (BTSLBaseException be) {
        	loggerValue.setLength(0);
         	loggerValue.append("Refusing request getting Exception:" );
         	loggerValue.append(be.getMessage());
            _log.error("CommonRechargeController[checkTransactionLoad]", loggerValue );
            _isRequestRefuse = true;
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
         	loggerValue.append("Refusing request getting Exception:");
         	loggerValue.append(e.getMessage());
            _log.error("CommonRechargeController[checkTransactionLoad]",  loggerValue );
            throw new BTSLBaseException("CommonRechargeController", "checkTransactionLoad", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

    public String getReceiverValidateStr(String p_interfaceID, String p_handlerClass) {
    	StringBuilder loggerValue= new StringBuilder(); 
		       
    	if (_log.isDebugEnabled()) {
    		 loggerValue.setLength(0);
         	loggerValue.append("Entered: p_interfaceID=" );
         	loggerValue.append(p_interfaceID);
         	loggerValue.append( ",p_handlerClass=");
         	loggerValue.append(p_handlerClass);
            _log.debug("getReceiverValidateStr", loggerValue );
        }

        StringBuffer strBuffer = null;
        strBuffer = new StringBuffer("MSISDN=" + _receiverMsisdn);
        strBuffer.append("&TRANSACTION_ID=" + _transferID);
        strBuffer.append("&NETWORK_CODE=" + _requestVO.getRequestNetworkCode());
        strBuffer.append("&INTERFACE_ID=" + p_interfaceID);
        strBuffer.append("&INTERFACE_HANDLER=" + p_handlerClass);
        strBuffer.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeS);
        strBuffer.append("&INT_MOD_IP=" + _intModIPS);
        strBuffer.append("&INT_MOD_PORT=" + _intModPortS);
        strBuffer.append("&INT_MOD_CLASSNAME=" + _intModClassNameS);
        strBuffer.append("&MODULE=" + PretupsI.C2S_MODULE);
        strBuffer.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
        strBuffer.append("&USER_TYPE=R");
        strBuffer.append("&REQ_SERVICE=" + _requestVO.getServiceType());
        strBuffer.append("&INT_ST_TYPE=" + _interfaceStatusType);

        return strBuffer.toString();
    }

    /*
     * set interface details
     */
    private void setInterfaceDetails(ReceiverVO p_receiverVO, ListValueVO p_listValueVO, boolean p_useInterfacePrefixVO, MSISDNPrefixInterfaceMappingVO p_interfaceMappingVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("setInterfaceDetails", " Enterd : p_useInterfacePrefixVO=" + p_useInterfacePrefixVO + ", p_MSISDNPrefixInterfaceMappingVO=" + p_interfaceMappingVO);
        }
        final String METHOD_NAME = "setInterfaceDetails";
        try {
            String status = null;
            String message1 = null;
            String message2 = null;

            if (p_useInterfacePrefixVO) {

                _interfaceID = p_interfaceMappingVO.getInterfaceID();
                _senderExternalID = p_interfaceMappingVO.getExternalID();
                status = p_interfaceMappingVO.getInterfaceStatus();
                message1 = p_interfaceMappingVO.getLanguage1Message();
                message2 = p_interfaceMappingVO.getLanguage2Message();
                _interfaceStatusType = p_interfaceMappingVO.getStatusType();// added
                // by
                // Dhiraj
                // on
                // 18/07/07
            } else {
                _interfaceID = p_listValueVO.getValue();
                _senderExternalID = p_listValueVO.getIDValue();
                status = p_listValueVO.getStatus();
                message1 = p_listValueVO.getOtherInfo();
                message2 = p_listValueVO.getOtherInfo2();
                _interfaceStatusType = p_listValueVO.getStatusType();// added by
                // Dhiraj
                // on
                // 18/07/07
            }
            if (!PretupsI.YES.equals(status) && PretupsI.INTERFACE_STATUS_TYPE_MANUAL.equals(_interfaceStatusType)) {
                // ChangeID=LOCALEMASTER
                // Which language message to be set is determined from the
                // locale master cache
                // If language not come from the requestVO the default language
                // will be used otherwise language obtained from the requestVO
                // will be used for locale.
                Locale locale = null;
                locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

                if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(locale)).getMessage())) {
                    _requestVO.setSenderReturnMessage(message1);
                } else {
                    _requestVO.setSenderReturnMessage(message2);
                }
                throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.INTERFACE_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            _log.error("setInterfaceDetails", "Getting Base Exception =" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommonRechargeController[setInterfaceDetails]",
                p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(), "Exception:" + e.getMessage(), _interfaceID);
            throw new BTSLBaseException(this, "setInterfaceDetails", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
    }

    /**
     * Method to perform the sender alternate interface routing controls
     * 
     * @param altList
     * @throws BTSLBaseException
     */
    private void performSenderAlternateRouting(ReceiverVO p_receiverVO, ArrayList altList, String p_interfaceCatgeory) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
		       
    	if (_log.isDebugEnabled()) {
    		 loggerValue.setLength(0);
         	loggerValue.append(" Entered with p_interfaceCatgeory=");
         	loggerValue.append(p_interfaceCatgeory);
            _log.debug("performSenderAlternateRouting",  loggerValue);
        }
        final String METHOD_NAME = "performSenderAlternateRouting";
        try {
            if (altList != null && !altList.isEmpty()) {
                // Check Interface Routing if not exists then continue
                // else decrease counters
                // Validate All service class checks
                // Decrease Counters for transaction and interface
                // Check Interface and transaction load
                // Send request
                // If success then update the subscriber routing table with new
                // interface ID
                // Also store in global veriables
                // If Not Found repeat the iteration for alt 2
                ListValueVO listValueVO = null;
                String requestStr = null;
                CommonClient commonClient = null;
                String receiverValResponse = null;
                switch (altList.size()) {
                    case 1:
                        {
                            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(0);

                            setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                            checkTransactionLoad(p_receiverVO, _interfaceID);

                            requestStr = getReceiverValidateStr(_interfaceID, listValueVO.getLabel());
                            commonClient = new CommonClient();

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            if (_log.isDebugEnabled()) {
                            	loggerValue.append("Sending Request For MSISDN=" );
                             	loggerValue.append(p_receiverVO.getMsisdn());
                             	loggerValue.append(" on ALternate Routing 1 to =");
                             	loggerValue.append(_interfaceID);
                                _log.debug("performSenderAlternateRouting",
                                		loggerValue );
                            }

                            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            try {
                                receiverValidateResponse(p_receiverVO, receiverValResponse, 1, altList.size());
                                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                                }
                            } catch (BTSLBaseException be) {
                                throw new BTSLBaseException(be);
                            } catch (Exception e) {
                                throw new BTSLBaseException(this, METHOD_NAME, "");
                            }

                            break;
                        }
                    case 2:
                        {
                            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                            LoadController.decreaseTransactionInterfaceLoad(_transferID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                            listValueVO = (ListValueVO) altList.get(0);

                            setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                            checkTransactionLoad(p_receiverVO, _interfaceID);

                            requestStr = getReceiverValidateStr(_interfaceID, listValueVO.getLabel());
                            commonClient = new CommonClient();

                            LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                            if (_log.isDebugEnabled()) {
                            	loggerValue.append( "Sending Request For MSISDN=");
                             	loggerValue.append(p_receiverVO.getMsisdn());
                             	loggerValue.append(" on ALternate Routing 1 to =");
                             	loggerValue.append(_interfaceID);
                                _log.debug("performSenderAlternateRouting",loggerValue );
                            }

                            receiverValResponse = commonClient.process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                            try {
                                receiverValidateResponse(p_receiverVO, receiverValResponse, 1, altList.size());
                                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                                }
                            } catch (BTSLBaseException be) {
                                if (be.isKey() && InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(be.getMessageKey())) {
                                    if (_log.isDebugEnabled()) {
                                    	loggerValue.append("Got Status=");
                                     	loggerValue.append(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                                     	loggerValue.append(" After validation Request For MSISDN=");
                                     	loggerValue.append(p_receiverVO.getMsisdn() );
                                     	loggerValue.append(" Performing Alternate Routing to 2");
                                        _log.debug("performSenderAlternateRouting",loggerValue );
                                    }

                                    LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
                                    LoadController.decreaseTransactionInterfaceLoad(_transferID, p_receiverVO.getNetworkCode(), LoadControllerI.DEC_LAST_TRANS_COUNT);

                                    listValueVO = (ListValueVO) altList.get(1);

                                    setInterfaceDetails(p_receiverVO, listValueVO, false, null);

                                    checkTransactionLoad(p_receiverVO, _interfaceID);

                                    requestStr = getReceiverValidateStr(_interfaceID, listValueVO.getLabel());

                                    LoadController.incrementTransactionInterCounts(_transferID, LoadControllerI.SENDER_UNDER_VAL);

                                    if (_log.isDebugEnabled()) {
                                    	loggerValue.append( "Sending Request For MSISDN=" );
                                     	loggerValue.append( p_receiverVO.getMsisdn());
                                     	loggerValue.append(" on ALternate Routing 2 to =");
                                     	loggerValue.append(_interfaceID);
                                        _log.debug("performSenderAlternateRouting",loggerValue);
                                    }

                                    receiverValResponse = commonClient
                                        .process(requestStr, _transferID, _intModCommunicationTypeS, _intModIPS, _intModPortS, _intModClassNameS);

                                    try {
                                        receiverValidateResponse(p_receiverVO, receiverValResponse, 1, altList.size());
                                        if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(_txnStatus)) {
                                            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_SUCCESS, LoadControllerI.SENDER_VAL_RESPONSE);
                                        }
                                    } catch (BTSLBaseException bex) {
                                        throw new BTSLBaseException(bex);
                                    } catch (Exception e) {
                                        throw new BTSLBaseException(e);
                                    }
                                } else {
                                    throw new BTSLBaseException(be);
                                }
                            } catch (Exception e) {
                            	throw new BTSLBaseException(this, METHOD_NAME, "");
                            }
                            break;
                        }
                    default:
                      	 if(_log.isDebugEnabled()){
                      		_log.debug("Default Value " , altList.size());
                      	 }
                }

            } else {
                return;
            }
        } catch (BTSLBaseException be) {
            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            throw new BTSLBaseException(be);
        } catch (Exception e) {
            LoadController.decreaseResponseCounters(_transferID, PretupsErrorCodesI.TXN_STATUS_FAIL, LoadControllerI.SENDER_VAL_RESPONSE);
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.append("Exception:");
         	loggerValue.append(e.getMessage());
            EventHandler
                .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommonRechargeController[performSenderAlternateRouting]",
                    _transferID, p_receiverVO.getMsisdn(), p_receiverVO.getNetworkCode(),  loggerValue.toString());
            throw new BTSLBaseException(this, "performSenderAlternateRouting", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("performSenderAlternateRouting", " Exiting ");
        }
    }

    /**
     * Method to handle sender validation response for interface routing
     * 
     * @param str
     * @param p_attempt
     * @param p_altSize
     * @throws BTSLBaseException
     */
    public void receiverValidateResponse(ReceiverVO p_receiverVO, String str, int p_attempt, int p_altSize) throws BTSLBaseException {
        final HashMap map = BTSLUtil.getStringToHash(str, "&", "=");
        final String status = (String) map.get("TRANSACTION_STATUS");
        final String interfaceID = (String) map.get("INTERFACE_ID");
        // Start: Update the Interface table for the interface ID based on
        // Handler status and update the Cache
        final String interfaceStatusType = (String) map.get("INT_SET_STATUS");
        if (!BTSLUtil.isNullString(interfaceStatusType) && (InterfaceCloserI.INTERFACE_SUSPEND.equals(interfaceStatusType) || InterfaceCloserI.INTERFACE_RESUME
            .equals(interfaceStatusType))) {
            new ResumeSuspendProcess(ResumeSuspendProcess._INTERFACES, interfaceID, interfaceStatusType, PretupsErrorCodesI.PROCESS_RESUMESUSPEND_INT_MSG,
                PretupsI.INTERFACE_STATUS_TYPE_AUTO).start();
            // :End
        }

        if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && p_attempt == 1 && p_attempt < p_altSize) {
            throw new BTSLBaseException(this, "receiverValidateResponse", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        }
        if (BTSLUtil.isNullString(status) || !status.equals(InterfaceErrorCodesI.SUCCESS)) {
            throw new BTSLBaseException(this, "processCRCValidationRequest", PretupsErrorCodesI.INTERFACE_ERROR_RESPONSE);
        }
        _txnStatus = PretupsErrorCodesI.TXN_STATUS_SUCCESS;

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
        _c2sTransferVO.setActiveUserId(_channelUserVO.getActiveUserID());
    }// end populateVOFromRequest
}
