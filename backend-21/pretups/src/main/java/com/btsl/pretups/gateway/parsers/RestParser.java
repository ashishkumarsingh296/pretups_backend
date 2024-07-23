package com.btsl.pretups.gateway.parsers;

import java.sql.Connection;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
//import com.btsl.ldap.LDAPAction;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.LDAPUtilI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;

/**
 * @author harshad.m
 *
 */
public class RestParser extends ParserUtility {

    public static final Log log = LogFactory.getLog(RestParser.class.getName());
       public static final String TYPE = "type";
       public static final String DATE = "date";
       public static final String EXTNWCODE = "extnwcode";
       public static final String MSISDN = "msisdn";
       public static final String PIN = "pin";
       public static final String LOGINID = "loginid";
       public static final String PSWD = "password";
       public static final String EXTCODE = "extcode";
       public static final String EXTREFNUM = "extrefnum";
       public static final String TRANSACTIONID = "transactionid";
       public static final String LANGUAGE1 = "language1";
    private static  String chnlMessageSep;
    private static OperatorUtilI operatorUtili = null;
    private static LDAPUtilI ldapUtili = null;
    static {
        try {
            chnlMessageSep = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(chnlMessageSep)) {
                chnlMessageSep = " ";
            }
        } catch (Exception e) {
            log.errorTrace("static", e);
        }
        
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
        }
        try {
            ldapUtili = (LDAPUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.LDAP_UTIL_CLASS)).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
        }
    }
    /* (non-Javadoc)
     * @see com.btsl.pretups.gateway.util.ParserUtility#parseChannelRequestMessage(com.btsl.pretups.receiver.RequestVO)
     */
    @Override
    public void parseChannelRequestMessage(RequestVO requestVO, Connection pCon) throws BTSLBaseException {
        final String methodName = "parseChannelRequestMessage";
        String contentType = requestVO.getReqContentType();
        LogFactory.printLog(methodName,"Transfer ID = " + requestVO.getRequestID() + ", contentType : " + contentType, log);
        try {
            if(contentType != null && (PretupsI.JSON_CONTENT_TYPE.equals(contentType) || ("c2cvomstrfini").equalsIgnoreCase(requestVO.getServiceKeyword())
            		||("c2cvomstrf").equalsIgnoreCase(requestVO.getServiceKeyword()) ||("c2ctrfini").equalsIgnoreCase(requestVO.getServiceKeyword()))){
                parseChannelRequest(0, requestVO);
                if(requestVO.getActionValue()!=PretupsI.SYSTEM_RECEIVER_ACTION){
                    ChannelUserBL.updateUserInfo(pCon, requestVO);
                }
            } else {
                requestVO.setDecryptedMessage(requestVO.getRequestMessage());
            }
            LogFactory.printLog(methodName, "Message = " + BTSLUtil.maskParam(requestVO.getDecryptedMessage()) + ", MSISDN = " + requestVO.getRequestMSISDN(), log);
        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, " BTSLException while parsing Request Message : " + be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error(methodName, "  Exception while parsing Request Message : " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseChannelRequestMessage]",
                    requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ExtAPIParsers", methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }
    }
    
    /**
     * @param action
     * @param requestVO
     * @throws BTSLBaseException
     */
    public void parseChannelRequest(int action, RequestVO requestVO) throws BTSLBaseException {
         LogFactory.printLog("parseChannelRequest","Entered Service Keyword=" + requestVO.getServiceKeyword() + " action=" + action, log);
         RestAPIStringParser.parseJsonRequest(requestVO);
    }


    @Override
    public void parseRequestMessage(RequestVO requestVO)
            throws BTSLBaseException {
        //will be used later
    }

    
    @Override
    public void generateResponseMessage(RequestVO requestVO) {
      //will be used later
    }

    @Override
    public void generateChannelResponseMessage(RequestVO requestVO) {
        
         LogFactory.printLog("parseRequest","Entered Request ID=" + requestVO.getRequestID() , log);
           
                 RestAPIStringParser.generateJsonResponse(requestVO);
            }
            

    @Override
    public void parseOperatorRequestMessage(RequestVO requestVO)
            throws BTSLBaseException {
        parseChannelRequest(0, requestVO);  
    }
    
    
    @Override
    public void validateUserIdentification(RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "validateUserIdentification";
        LogFactory.printLog(methodName, "Entered Request ID = " + requestVO.getRequestID() + ", RequestMSISDN() = " + requestVO.getRequestMSISDN(), log);
        // Validate user on the basis of values provided.
        // If MSISDN is there then validate the same.
        if (!BTSLUtil.isNullString(requestVO.getRequestMSISDN())) {
            validateMSISDN(requestVO);
        }
        
        LogFactory.printLog(methodName, "Exiting", log);
    }
    @Override
      public void loadValidateNetworkDetails(RequestVO requestVO) throws BTSLBaseException {
            final String methodName = "loadValidateNetworkDetails";
            LogFactory.printLog(methodName,"Entered Request ID = " + requestVO.getRequestID(),log);

            try {
                NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(requestVO.getExternalNetworkCode());
                NetworkPrefixVO phoneNetworkPrefixVO = null;
                NetworkPrefixVO networkPrefixVO = null;
                // Also check if MSISDN is there then get the network Details from
                // it and match with network from external code
                
                if (requestVO.getActionValue()!=PretupsI.SYSTEM_RECEIVER_ACTION && networkVO != null && !BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                    phoneNetworkPrefixVO = PretupsBL.getNetworkDetails(requestVO.getFilteredMSISDN(), PretupsI.USER_TYPE_SENDER);
                    if (!phoneNetworkPrefixVO.getNetworkCode().equals(networkVO.getNetworkCode())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NETWORK_CODE_MSIDN_NETWORK_MISMATCH);
                    }
                }else if (networkVO == null) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXT_NETWORK_CODE);
                }
                if (requestVO.getActionValue()!=PretupsI.SYSTEM_RECEIVER_ACTION && phoneNetworkPrefixVO != null) {
                    requestVO.setValueObject(phoneNetworkPrefixVO);
                    validateNetwork(requestVO, phoneNetworkPrefixVO);
                } else {
                    networkPrefixVO = new NetworkPrefixVO();
                    networkPrefixVO.setNetworkCode(networkVO.getNetworkCode());
                    networkPrefixVO.setNetworkName(networkVO.getNetworkName());
                    networkPrefixVO.setNetworkShortName(networkVO.getNetworkShortName());
                    networkPrefixVO.setCompanyName(networkVO.getCompanyName());
                    networkPrefixVO.setErpNetworkCode(networkVO.getErpNetworkCode());
                    networkPrefixVO.setStatus(networkVO.getStatus());
                    networkPrefixVO.setLanguage1Message(networkVO.getLanguage1Message());
                    networkPrefixVO.setLanguage2Message(networkVO.getLanguage2Message());
                    networkPrefixVO.setModifiedOn(networkVO.getModifiedOn());
                    networkPrefixVO.setModifiedTimeStamp(networkVO.getModifiedTimeStamp());
                    requestVO.setValueObject(networkPrefixVO);
                    validateNetwork(requestVO, networkPrefixVO);
                }
            } catch (BTSLBaseException be) {
                log.errorTrace(methodName, be);
                requestVO.setSuccessTxn(false);
                requestVO.setMessageCode(be.getMessageKey());
                throw be;
            } finally {
                LogFactory.printLog(methodName,  "Exiting Request ID = " + requestVO.getRequestID(), log);
            }
        }
      public   ChannelUserVO  loadValidateUserDetails(Connection con, RequestVO requestVO) throws BTSLBaseException{
        final String methodName = "loadValidateUserDetails";
     
        ChannelUserVO  channelUserVO=null;
        ChannelUserVO staffUserVO=null;
        boolean byPassCheck=false;
        String type = (String) requestVO.getRequestMap().get(TYPE.toUpperCase());
        Map seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
        ServiceKeywordCacheVO serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type.toUpperCase() + "_"
                    + requestVO.getModule() + "_" + requestVO.getRequestGatewayType() + "_" + requestVO.getServicePort());
        
        if(requestVO.getModule().equals(PretupsI.OPT_MODULE) && serviceKeywordCacheVO==null){
            serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type.toUpperCase() + "_"
                    + PretupsI.C2S_MODULE + "_" + requestVO.getRequestGatewayType() + "_" + requestVO.getServicePort());
        }

        switch(serviceKeywordCacheVO.getServiceType()){
            case PretupsI.SERVICE_TYPE_EXT_USER_ADD:
                
                channelUserVO = ParserUtility.validateAddUser(con, requestVO, channelUserVO, staffUserVO);
           
                break;
            
            //added by Pankaj Rawat for Channel User Available Voucher Enquiry
            case PretupsI.USER_AVAILABLE_VOUCHER_ENQ:{
				String extCode = requestVO.getRequestMap().get("EXTCODE").toString();
				String msisdn = requestVO.getRequestMap().get("MSISDN").toString();
				String pin = requestVO.getRequestMap().get("PIN").toString();
				  
                String extnetworkID = requestVO.getRequestMap().get("EXTNWCODE").toString();
                String loginID = requestVO.getRequestMap().get("LOGINID").toString();
                String password = requestVO.getRequestMap().get("PASSWORD").toString();
                
                byPassCheck=true;
                Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                
                 if (!BTSLUtil.isNullString(loginID)) {
                	 channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, loginID);
                	 if (channelUserVO== null)
                	 {
                		 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER); 
                	 }
                	 
                	 if (!operatorUtili.validateTransactionPassword(channelUserVO, password) || BTSLUtil.isNullString(password)) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                     }
                     if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                     }
                     
                } else if (!BTSLUtil.isNullString(extCode)) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(extCode).trim());
                }
                 
                else if(!BTSLUtil.isNullString(msisdn))
                {
                	channelUserVO = _channelUserDAO.loadChannelUserDetails(con,msisdn);
                	if(channelUserVO == null)
                	{
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                	}
                	
                	else
                	{
                		operatorUtili.validatePIN(con, channelUserVO, pin);
                	}
                }
                 if (!BTSLUtil.isNullString(extnetworkID)) {
                     NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extnetworkID);
                     if(networkVO==null){
                     String messageArray[]= {extnetworkID};
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
                     }
                 }
                 
                 if (channelUserVO != null) {
                 	if (!extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                     }
                 }
                 if (!BTSLUtil.isNullString(extnetworkID) && !extnetworkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                     throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                 }
                 if (!BTSLUtil.isNullString(loginID)) {
                     if (!loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                     }
                 }
                 if (!BTSLUtil.isNullString(extCode)) {
                     if (!extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                     }
                 }
                 
                 break;
			}
           
            case PretupsI.SERVICE_TYPE_C2C_VOMS:
            case PretupsI.SERVICE_TYPE_C2C_VOMS_INI:
            case PretupsI.SERVICE_TYPE_C2C_INITIATE:
            case PretupsI.SERVICE_TYPE_C2C_VOMS_APPR:
            case PretupsI.SERVICE_TYPE_C2C_APPROVAL:
            case PretupsI.SERVICE_TYPE_C2CAPPR_LIST:{
                String password = (String) requestVO.getRequestMap().get(PSWD.toUpperCase());
                String extCode = (String) requestVO.getRequestMap().get(EXTCODE.toUpperCase());
                String networkId = requestVO.getRequestNetworkCode();
                String loginId = requestVO.getSenderLoginID();
               // String msisdn = (String) requestVO.getRequestMap().get(MSISDN.toUpperCase());
                
                if(!BTSLUtil.isNullString(requestVO.getActiverUserId()) && requestVO.getIsStaffUser()!=null && requestVO.getIsStaffUser()) {//means staff
                	UserDAO _userDAO = new UserDAO();
                	UserVO userVO = _userDAO.loadUserDetailsFormUserID(con, requestVO.getActiverUserId());
                	channelUserVO = _channelUserDAO.loadStaffUserDetailsByLoginId(con, userVO.getLoginID());
                	settingStaffDetails(channelUserVO);
                	if(channelUserVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
                		UserPhoneVO parentPhoneVO = _userDAO.loadUserPhoneVO(con, channelUserVO.getUserID());//getting parent User phoneVO
                		channelUserVO.setUserPhoneVO(parentPhoneVO);
                	}
                }
                else if (!BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                    channelUserVO = _channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
                } else if (!BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, requestVO.getSenderLoginID());
                } else if (!BTSLUtil.isNullString(requestVO.getSenderExternalCode())) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(requestVO.getSenderExternalCode()).trim());
                    if (BTSLUtil.NullToString(requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                    	requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                    }
                }

                
                if (channelUserVO == null && !BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                    channelUserVO = _channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
                }
                if (channelUserVO == null && !BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, requestVO.getSenderLoginID());
                }

                if (channelUserVO != null && !BTSLUtil.isNullString(networkId)) {
                    if (!networkId.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                    }
                 if(requestVO.getActiverUserId().equals(channelUserVO.getUserID())){
                    if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                    }
                    if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                    }
                    if (!BTSLUtil.isNullString(loginId)) {
                        if (BTSLUtil.isNullString(password)) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        if (!loginId.equalsIgnoreCase(channelUserVO.getLoginID())) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                        }
                    }
                  }
                } else {
                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                }
                if (!requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                	requestVO.setPinValidationRequired(false);
                } else if (BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                	requestVO.setPinValidationRequired(false);
                }

                if (BTSLUtil.isNullString((String) requestVO.getRequestMap().get("MSISDN")) || !BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                	requestVO.setPinValidationRequired(false);
                }else {
                    requestVO.setPinValidationRequired(true);
                    operatorUtili.validatePIN(con, channelUserVO, (String) requestVO.getRequestMap().get("PIN"));
                }
                String CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
                if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                    CHNL_MESSAGE_SEP = " ";
                }
                String message = requestVO.getDecryptedMessage();
                if (!requestVO.isPinValidationRequired()) {
                    if (!BTSLUtil.isNullString((String) requestVO.getRequestMap().get("PIN"))) {
                        message = message + CHNL_MESSAGE_SEP + (String) requestVO.getRequestMap().get("PIN");
                    } else {
                        message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                    }
                } else {
                    if (BTSLUtil.NullToString(requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_PIN_BLANK);
                    }
                    message = message + CHNL_MESSAGE_SEP + (String) requestVO.getRequestMap().get("PIN");
                }
                requestVO.setDecryptedMessage(message);
                break;
            }
            case PretupsI.RECHARGE_REVERSAL: {
                String password = (String) requestVO.getRequestMap().get(PSWD.toUpperCase());
                String extCode = (String) requestVO.getRequestMap().get(EXTCODE.toUpperCase());
                String networkId = requestVO.getRequestNetworkCode();
                String loginId = requestVO.getSenderLoginID();
               // String msisdn = (String) requestVO.getRequestMap().get(MSISDN.toUpperCase());
                

                if (!BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                    channelUserVO = _channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
                } else if (!BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, requestVO.getSenderLoginID());
                } else if (!BTSLUtil.isNullString(requestVO.getSenderExternalCode())) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(requestVO.getSenderExternalCode()).trim());
                    if (BTSLUtil.NullToString(requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                    	requestVO.getRequestMap().put("PIN", BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin()));
                    }
                }

                
                if (channelUserVO == null && !BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                    channelUserVO = _channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
                }
                if (channelUserVO == null && !BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                    channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, requestVO.getSenderLoginID());
                }

                if (channelUserVO != null && !BTSLUtil.isNullString(networkId)) {
                    if (!networkId.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                    }
                 if(requestVO.getActiverUserId().equals(channelUserVO.getUserID())){
                    if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                    }
                    if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                    }
                    if (!BTSLUtil.isNullString(loginId)) {
                        if (BTSLUtil.isNullString(password)) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        if (!operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        if (!loginId.equalsIgnoreCase(channelUserVO.getLoginID())) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                        }
                    }
                  }
                } else {
                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                }
                if (!requestVO.getMessageGatewayVO().isUserAuthorizationReqd()) {
                	requestVO.setPinValidationRequired(false);
                } else if (BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                	requestVO.setPinValidationRequired(false);
                }

                if (BTSLUtil.isNullString((String) requestVO.getRequestMap().get("MSISDN")) || !BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                	requestVO.setPinValidationRequired(false);
                }
                String CHNL_MESSAGE_SEP = ((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));
                if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                    CHNL_MESSAGE_SEP = " ";
                }
                String message = requestVO.getDecryptedMessage();
                if (!requestVO.isPinValidationRequired()) {
                    if (!BTSLUtil.isNullString((String) requestVO.getRequestMap().get("PIN"))) {
                        message = message + CHNL_MESSAGE_SEP + (String) requestVO.getRequestMap().get("PIN");
                    } else {
                        message = message + CHNL_MESSAGE_SEP + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO().getSmsPin());
                    }
                } else {
                    if (BTSLUtil.NullToString(requestVO.getRequestMap().get("PIN").toString()).length() == 0) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_PIN_BLANK);
                    }
                    message = message + CHNL_MESSAGE_SEP + (String) requestVO.getRequestMap().get("PIN");
                }
                requestVO.setDecryptedMessage(message);
                break;
            }
        default:
        		if(PretupsI.OPERATOR_RECEIVER_ACTION == requestVO.getActionValue())
        		{
        			byPassCheck=true;
        		}
                if(requestVO.getActionValue()!=PretupsI.OPERATOR_RECEIVER_ACTION){
                    String password = (String) requestVO.getRequestMap().get(PSWD.toUpperCase());
                    String extCode = (String) requestVO.getRequestMap().get(EXTCODE.toUpperCase());
                    String networkID = requestVO.getRequestNetworkCode();
                    String loginID = requestVO.getSenderLoginID();
                    String msisdn = (String) requestVO.getRequestMap().get(MSISDN.toUpperCase());
                    String pin = (String) requestVO.getRequestMap().get(PIN.toUpperCase());
                    
                    
                    if(!BTSLUtil.isNullString(requestVO.getActiverUserId()) && requestVO.getIsStaffUser()!=null && requestVO.getIsStaffUser()) {//means staff
                    	UserDAO _userDAO = new UserDAO();
                    	UserVO userVO = _userDAO.loadUserDetailsFormUserID(con, requestVO.getActiverUserId());
                    	channelUserVO = _channelUserDAO.loadStaffUserDetailsByLoginId(con, userVO.getLoginID());
                    	settingStaffDetails(channelUserVO);
                    	if(channelUserVO.getUserPhoneVO().getMsisdn()==null) {//means staff has no msisdn
                    		UserPhoneVO parentPhoneVO = _userDAO.loadUserPhoneVO(con, channelUserVO.getUserID());//getting parent User phoneVO
                    		channelUserVO.setUserPhoneVO(parentPhoneVO);
                    	}
                    }
                    else if (!BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                        channelUserVO = _channelUserDAO.loadChannelUserDetails(con, requestVO.getFilteredMSISDN());
                    } else if (!BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, requestVO.getSenderLoginID());
                    } else if (!BTSLUtil.isNullString(requestVO.getSenderExternalCode())) {
                        channelUserVO = _channelUserDAO.loadChnlUserDetailsByExtCode(con, BTSLUtil.NullToString(requestVO.getSenderExternalCode()).trim());
                    }

                    UserVO userVO= new UserDAO().loadUsersDetails(con, requestVO.getFilteredMSISDN());
                    if(BTSLUtil.isNullObject(channelUserVO) && userVO!=null)
                    {
                    	channelUserVO = (ChannelUserVO) userVO;
                    	UserPhoneVO userPhoneVO = new UserDAO().loadUserPhoneVO(con, channelUserVO.getUserID());
                    	channelUserVO.setUserPhoneVO(userPhoneVO);
                    }
                    if (channelUserVO != null) {
                    	channelUserVO.setActiveUserID(channelUserVO.getUserID());
                    	  // Validate user details
                    	if(!(("txncalview").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("autocomplete").equalsIgnoreCase(requestVO.getServiceKeyword())))
                    		validateUserDetails(requestVO, channelUserVO);
                        
                        if (!networkID.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                            throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
                        }
                        // change for moldova by Ved 24/07/07
                        if(!BTSLUtil.isStringIn(requestVO.getServiceKeyword(),Constants.getProperty("REST_SENDER_OPTIONAL_PWD_ACTIONS"))){
                        if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                        }
                        }
                        if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
                        }
                        if (!BTSLUtil.isNullString(loginID)&& !loginID.equalsIgnoreCase(channelUserVO.getLoginID())) {
                            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
                        }
                   } else {
                	   
                	   if(loginID != null) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, new String[]{loginID});
                	   }else if(msisdn != null) {
                           throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST, new String[]{msisdn});
                   	   }else {
                   		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
                   	   }
                    }
                    //
                    if (BTSLUtil.isNullString(msisdn)) {
                        requestVO.setPinValidationRequired(false);
                    }
                    else if(BTSLUtil.isStringIn(requestVO.getServiceKeyword(),Constants.getProperty("REST_SENDER_OPTIONAL_PIN_ACTIONS"))){
                    	requestVO.setPinValidationRequired(false);
                    }else{
                        if (!userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
                            requestVO.setPinValidationRequired(true);
                            operatorUtili.validatePIN(con, channelUserVO, pin);
                        } else
                            requestVO.setPinValidationRequired(false);


                    }
                    changeMessageFormatRequired(requestVO,channelUserVO);
                }else{
                    try {
                        String loginId = (String) requestVO.getRequestMap().get("LOGINID");
                        String password = (String) requestVO.getRequestMap().get("PASSWORD");
                        String extNetCode = (String) requestVO.getRequestMap().get("EXTNWCODE");
                        String catCode = (String) requestVO.getRequestMap().get("CATCODE");
                        String msisdn = (String) requestVO.getRequestMap().get("MSISDN");
                        String pin = (String) requestVO.getRequestMap().get("PIN");
                        Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        
                        // Start Moldova cahnges by Ved 24/07/07
                        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                        try {
                            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
                                    "PretupsParser[loadValidateUserDetails]", "", "",
                                "", "Exception while loading the class at the call:" + e.getMessage());
                        }
                    // End Moldova cahnges by Ved 24/07/07

                        if ((!BTSLUtil.isNullString(loginId) && !BTSLUtil.isNullString(password))|| (!BTSLUtil.isNullString(msisdn) && !BTSLUtil.isNullString(pin))) {
                            // get user informationon the basis of login id and validate the
                            // password
                            // if failed throw exception
                            LoginDAO loginDAO = new LoginDAO();
                            msisdn = requestVO.getFilteredMSISDN();
                            if (!BTSLUtil.isNullString(requestVO.getFilteredMSISDN())) {
                            	channelUserVO = loginDAO.loadUserDetailsByMsisdnOrLoginId(con, msisdn,null, password, locale);
                            }
                            else if (!BTSLUtil.isNullString(requestVO.getSenderLoginID())) {
                            channelUserVO = loginDAO.loadUserDetails(con, loginId, password, locale);
                            }
                            if (channelUserVO == null) {
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                            }
                            channelUserVO.setActiveUserID(channelUserVO.getUserID());
                            // block added on request by Vamsidhar to avoid channel users to
                            // use this feature on 13-jun-2007
                            if (!PretupsI.USER_TYPE_OPERATOR.equalsIgnoreCase(channelUserVO.getUserType())) {
                                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                            
                            }
                            if (!extNetCode.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                            }
                            // Moldova cahnges by Ved 24/07/07
                            
                            String ldapEnabled = null;
                            try{
                                ldapEnabled=Constants.getProperty("ENABLE_LDAP_REST_API");
                            }catch(Exception e){
                            	log.error(methodName, "Exception " + e);
                			    log.errorTrace(methodName,e);
                                LogFactory.printError(methodName, "ENABLE_LDAP_REST_API not found in Constants.props", log);
                            }
                            
                            if(ldapEnabled.equalsIgnoreCase(PretupsI.YES)){
                                //LDAPAction ldapAction = new LDAPAction();
                                if (!ldapUtili.authenticateUser(loginId, password)) {
                                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                                } 
                            }else if (!BTSLUtil.isNullString(channelUserVO.getPassword()) && !operatorUtili.validateTransactionPassword(channelUserVO, password)){
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_XML_ERROR_INVALID_PSWD);
                            }
                            
                            if (!BTSLUtil.isNullString(catCode) && !catCode.equalsIgnoreCase(channelUserVO.getCategoryCode())) {
                                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_CATCODE);
                            }    
                        
                       } else{
                    	   throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.OPERATOR_RECEIVER_LOGIN_PASSWORD_REQUIRED);
                       }

                        requestVO.setLocale(locale);
                        // validate the user
                        if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
                            throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_SENDER_SUSPEND);
                        } else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_BLOCK)) {
                            throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_SENDER_BLOCKED);
                        } else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_DELETE_REQUEST)) {
                            throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_SENDER_DELETE_REQUEST);
                        } else if (channelUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                            throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_SENDER_SUSPEND_REQUEST);
                        }

                        // load geographical area
                        GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
                        channelUserVO.setGeographicalAreaList(geographyDAO.loadUserGeographyList(con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
                        // load domains
                        DomainDAO domainDAO = new DomainDAO();
                        channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(con, channelUserVO.getUserID()));
                        UserPhoneVO phoneVO = new UserPhoneVO();
                        phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                        phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        channelUserVO.setUserPhoneVO(phoneVO);
                        requestVO.setFilteredMSISDN(channelUserVO.getMsisdn());
                        requestVO.setCategoryCode(channelUserVO.getCategoryCode());
                    }catch (BTSLBaseException be) {
                    	requestVO.setSuccessTxn(false);
                    	requestVO.setMessageCode(be.getMessage());
                        log.errorTrace(methodName, be);
                        throw be;
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                        throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_EXCEPTION);
                    }
                
                  }
               }
        if (!byPassCheck) 
        {
            if (BTSLUtil.isNullString(requestVO.getFilteredMSISDN()) && channelUserVO != null) {
                requestVO.setFilteredMSISDN(PretupsBL.getFilteredMSISDN(channelUserVO.getUserPhoneVO().getMsisdn()));
            }

            if(!(("txncalview").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("autocomplete").equalsIgnoreCase(requestVO.getServiceKeyword())))
            	validateUserDetails(requestVO, channelUserVO);
            if (channelUserVO != null) {
            	requestVO.setMessageSentMsisdn(channelUserVO.getUserPhoneVO() == null ? channelUserVO.getMsisdn() : channelUserVO.getUserPhoneVO().getMsisdn());
                if(!BTSLUtil.isNullString(requestVO.getActiverUserId())){
                	if (!channelUserVO.getUserID().equals(requestVO.getActiverUserId())) {
                		channelUserVO.setStaffUser(true);
                		staffUserVO = _channelUserDAO.loadChannelUserDetailsByUserId(con, requestVO.getActiverUserId(), channelUserVO.getUserID());
                		if(staffUserVO!=null)
                		{
                			if(!(("txncalview").equalsIgnoreCase(requestVO.getServiceKeyword()) || ("autocomplete").equalsIgnoreCase(requestVO.getServiceKeyword()) ))
                				validateUserDetails(requestVO, staffUserVO);
                		
                		channelUserVO.setActiveUserID(staffUserVO.getUserID());
                		staffUserVO.setActiveUserID(staffUserVO.getUserID());
                		staffUserVO.setStaffUser(true);
                		if (staffUserVO != null && !PretupsI.NOT_AVAILABLE.equals(staffUserVO.getUserPhoneVO().getMsisdn())) {
                			requestVO.setMessageSentMsisdn(staffUserVO.getUserPhoneVO().getMsisdn());
                		} else {
                			UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
                			requestVO.setSenderLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                		}
                		channelUserVO.setStaffUserDetails(staffUserVO);
                		}
                	}else{
                		channelUserVO.setActiveUserID(channelUserVO.getUserID());
                	}
                } else {
                    channelUserVO.setActiveUserID(channelUserVO.getUserID());
                }
            }
        }
        requestVO.setSenderVO(channelUserVO);  
        if (channelUserVO != null && channelUserVO.isStaffUser() && staffUserVO!=null) {
            return staffUserVO;
        } else {
            return channelUserVO;
        }
      }
      
    private void appendMessageformat(RequestVO requestVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        String methodName = "appendMessageformat";
        String message = requestVO.getDecryptedMessage();
        if (!requestVO.isPinValidationRequired()) {
            if (!BTSLUtil.isNullString((String) requestVO.getRequestMap().get(PIN.toUpperCase()))) {
                message = message + chnlMessageSep + (String) requestVO.getRequestMap().get(PIN.toUpperCase());
            } else {
                message = message + chnlMessageSep + BTSLUtil.decryptText(channelUserVO.getUserPhoneVO() == null ? "":channelUserVO.getUserPhoneVO().getSmsPin());
            }
        } else {
            if (BTSLUtil.NullToString(requestVO.getRequestMap().get(PIN.toUpperCase()).toString()).length() == 0) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_PIN_BLANK);
            }
            message = message + chnlMessageSep + (String) requestVO.getRequestMap().get(PIN.toUpperCase());
        }
        requestVO.setDecryptedMessage(message);
    }

    private void changeMessageFormatRequired(RequestVO requestVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
        Map seviceKeywordMap ;
        String selAction = "";
        String type = (String) requestVO.getRequestMap().get(TYPE.toUpperCase());
        seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
        final ServiceKeywordCacheVO serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(type.toUpperCase() + "_"
                + requestVO.getModule() + "_" + requestVO.getRequestGatewayType() + "_" + requestVO.getServicePort());
        if (serviceKeywordCacheVO != null) {
            selAction = serviceKeywordCacheVO.getServiceType();
        }
        switch (selAction) {
        case PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN:
            break;
        case VOMSI.SERVICE_TYPE_VOUCHER_STATUS_CHANGE:
            break;
        case PretupsI.SERVICE_TYPE_RECHARGE_STATUS:
        	String message = requestVO.getDecryptedMessage();
        	if (!BTSLUtil.isNullString((String) requestVO.getRequestMap().get(EXTREFNUM.toUpperCase())) && !BTSLUtil.isNullString((String) requestVO.getRequestMap().get(TRANSACTIONID.toUpperCase()))) {
        		message = message + chnlMessageSep + "BOTH";
        	} else if (!BTSLUtil.isNullString((String) requestVO.getRequestMap().get(EXTREFNUM.toUpperCase()))){
        		message = message + chnlMessageSep + "EXT";
        	}else{
        		message = message + chnlMessageSep + "TXN";
        	}
        	requestVO.setDecryptedMessage(message);
        	 appendMessageformat(requestVO, channelUserVO);
        	break;		
        default:
            appendMessageformat(requestVO, channelUserVO);
            break;
        }
    }
    
    private void settingStaffDetails(ChannelUserVO channelUserVO) {

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			channelUserVO.setActiveUserID(channelUserVO.getUserID());
			UserDAO userDao = new UserDAO();
            UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
            if (phoneVO != null) {
                channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
                channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
               }
            ChannelUserVO staffUserVO = new ChannelUserVO();
            UserPhoneVO staffphoneVO = new UserPhoneVO();
            BeanUtils.copyProperties(staffUserVO, channelUserVO);
            if (phoneVO != null) {
                BeanUtils.copyProperties(staffphoneVO, phoneVO);
                staffUserVO.setUserPhoneVO(staffphoneVO);
            }
            staffUserVO.setPinReset(channelUserVO.getPinReset());
            channelUserVO.setStaffUserDetails(staffUserVO);
            ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
            staffUserDetails(channelUserVO, parentChannelUserVO);
            channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
				
		}catch(Exception e) {
			
		}finally {
			if(mcomCon != null)
			{
				mcomCon.close("C2CTransferController#checkAndSetStaffVO");
				mcomCon=null;
			}
		}
		
	}
	
	protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
        channelUserVO.setUserID(channelUserVO.getParentID());
        channelUserVO.setParentID(parentChannelUserVO.getParentID());
        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
        channelUserVO.setStatus(parentChannelUserVO.getStatus());
        channelUserVO.setUserType(parentChannelUserVO.getUserType());
        channelUserVO.setStaffUser(true);
        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
    }

}
