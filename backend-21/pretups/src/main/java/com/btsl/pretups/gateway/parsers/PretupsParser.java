package com.btsl.pretups.gateway.parsers;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.util.ExtAPIXMLStringParser;
import com.btsl.pretups.gateway.util.ParserUtility;
import com.btsl.pretups.gateway.util.XMLStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

/**
 * @(#)PretupsParser.java
 *                        Copyright(c) 2006, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ankit Singhal Dec 02, 2006 Initial Creation
 *                        Parser class for parsing Xml request for cce interface
 **/

public class PretupsParser extends ParserUtility {

    public static final Log LOG = LogFactory.getLog(PretupsParser.class.getName());
    public static String CHNL_MESSAGE_SEP = null;
    
    
    static {
        try {
            CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
        } catch (Exception e) {
            LOG.errorTrace("static", e);
        }
    }


    // Methods to be used for P2P, so not required.
    public void parseRequestMessage(RequestVO p_requestVO)  throws BTSLBaseException{
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
    }

    public void parseChannelRequestMessage(RequestVO p_requestVO, Connection pCon) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequestMessage", "p_requestVO: " + p_requestVO.toString());
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                parseChannelRequest(p_requestVO);
                // this will call a method from XMLStringParser class where
                // common part will be parsed
                // and then requst specific method will be called to parse data
                // part
                // ChannelUserBL.updateUserInfo(p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseChannelRequestMessage", "BTSLBaseException: " + be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelRequestMessage", "Exception e: " + e);
            throw new BTSLBaseException("PretupsParser", "parseChannelRequestMessage", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelRequestMessage", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public void parseChannelRequestMessage(Connection con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequestMessage", "p_requestVO: " + p_requestVO.toString());
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                parseChannelRequest(p_requestVO);
                // this will call a method from XMLStringParser class where
                // common part will be parsed
                // and then requst specific method will be called to parse data
                // part
                // ChannelUserBL.updateUserInfo(p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseChannelRequestMessage", "BTSLBaseException: " + be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelRequestMessage", "Exception e: " + e);
            throw new BTSLBaseException("PretupsParser", "parseChannelRequestMessage", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelRequestMessage", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    public void generateChannelResponseMessage(RequestVO p_requestVO) {
        final String METHOD_NAME = "generateChannelResponseMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelResponseMessage", "p_requestVO=" + p_requestVO.toString());
        }
        try {
            // Set Message Required flag to false as no SMS needs to be pushed
            // in these Cases
            p_requestVO.setSenderMessageRequired(false);

            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                generateChannelParserResponse(p_requestVO);
                // this will call a method from XMLStringParser class where
                // common part will be generated
                // and then requst specific method will be called to generate
                // data part
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
            }
        } catch (Exception e) {
            LOG.error("generateChannelResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsParser[generateChannelResponseMessage]", "",
                "", "", "Exception getting message :" + e.getMessage());
        }
    }

    /**
     * Method to load and validate user details
     * 
     * @param p_con
     * @param p_requestVO
     * @throws BTSLBaseException
     */

    public ChannelUserVO loadValidateUserDetails(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadValidateUserDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadValidateUserDetails", "Entered Request ID=" + p_requestVO.getRequestID());
        }

        ChannelUserVO channelUserVO = null;
        MessageGatewayVO messageGatewayVO = p_requestVO.getMessageGatewayVO();
        // 1. If USER_AUTHORISATION is required and login id and/or emp id with
        // category code in external system are present and are valid, the
        // information of this user will be loaded and request will be processed
        // otherwise rejected.
        // 2. If USER_AUTHORISATION is not required and login id and/or emp id
        // in external system is present and are valid, the information of this
        // user will be loaded else request will be rejected.
        // 3. If USER_AUTHORISATION is not required and login id and/or emp id
        // in external system are not present, the information of default
        // network will be loaded.
        try {
            String loginId = (String) p_requestVO.getRequestMap().get("LOGINID");
            String password = (String) p_requestVO.getRequestMap().get("PASSWORD");
            String empCode = (String) p_requestVO.getRequestMap().get("EMPCODE");
            String catCode = (String) p_requestVO.getRequestMap().get("CATCODE");
            String extNetCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");
            String extCode = (String) p_requestVO.getRequestMap().get("EXTCODE");
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            // Start Moldova cahnges by Ved 24/07/07
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            OperatorUtilI operatorUtili = null;
            try {
                operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PretupsParser[loadValidateUserDetails]", "", "",
                    "", "Exception while loading the class at the call:" + e.getMessage());
            }
            // End Moldova cahnges by Ved 24/07/07

            if (!BTSLUtil.isNullString(loginId) && !BTSLUtil.isNullString(password)) {
                // get user informationon the basis of login id and validate the
                // password
                // if failed throw exception
                LoginDAO _loginDAO = new LoginDAO();
                channelUserVO = _loginDAO.loadUserDetails(p_con, loginId, password, locale);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
                // block added on request by Vamsidhar to avoid channel users to
                // use this feature on 13-jun-2007
                if (!PretupsI.USER_TYPE_OPERATOR.equalsIgnoreCase(channelUserVO.getUserType())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
                if (!extNetCode.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
                // Moldova cahnges by Ved 24/07/07
                if (!BTSLUtil.isNullString(channelUserVO.getPassword()) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
                }
                // validating emp code and cat code if avialable
                if (!BTSLUtil.isNullString(empCode) && !empCode.equalsIgnoreCase(channelUserVO.getEmpCode())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_EMPCODE);
                }
                if (!BTSLUtil.isNullString(catCode) && !catCode.equalsIgnoreCase(channelUserVO.getCategoryCode())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_CATCODE);
                }
                if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) 
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);  
                
                
            } else if (!BTSLUtil.isNullString(empCode) && !BTSLUtil.isNullString(catCode)) {
                // get user information on the basis of user code and category
                // code
                UserDAO _userDAO = new UserDAO();
                channelUserVO = _userDAO.loadUserDetailsByEmpcode(p_con, empCode, catCode, locale);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
                if (!extNetCode.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
                // validating login id and password if available
                if (!BTSLUtil.isNullString(loginId) && !loginId.equalsIgnoreCase(channelUserVO.getLoginID())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_LOGINID);
                }
                // Moldova cahnges by Ved 24/07/07
                if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password)) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
                }
                if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) 
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);  
                
            } 
            else if (!BTSLUtil.isNullString(empCode))
	        {
	            //get user information on the basis of user code and category code
	            UserDAO _userDAO= new UserDAO();
	            password=operatorUtili.decryptPINPassword(password);
	            channelUserVO = _userDAO.loadUserDetailsByEmpcode(p_con,empCode,null,locale);
	            if (channelUserVO==null)
					throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
	            if (!extNetCode.equalsIgnoreCase(channelUserVO.getNetworkID()))
					throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
				//validating login id and password if available
	            if (!BTSLUtil.isNullString(loginId) && !loginId.equalsIgnoreCase(channelUserVO.getLoginID()))
					throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_LOGINID);
                //Moldova cahnges by Ved 24/07/07
			    if (!BTSLUtil.isNullString(password) && !operatorUtili.validateTransactionPassword(channelUserVO, password))
					throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
			    if (!BTSLUtil.isNullString(extCode) && !extCode.equalsIgnoreCase(channelUserVO.getExternalCode())) 
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);  
			    
	        }
            else if (BTSLUtil.isNullString(loginId) && BTSLUtil.isNullString(empCode) && !messageGatewayVO.isUserAuthorizationReqd()) {
                // load default user information if user authorisation is not
                // required
                LoginDAO _loginDAO = new LoginDAO();
                channelUserVO = _loginDAO.loadUserDetails(p_con, messageGatewayVO.getGatewayCode(), password, locale);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
                if (!extNetCode.equalsIgnoreCase(channelUserVO.getNetworkID())) {
                    throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
                }
            } else {
                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
            }

            p_requestVO.setLocale(locale);
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

            UserDAO userDAO = new UserDAO();
            boolean roleAvailable = false;
            if ("Y".equalsIgnoreCase(channelUserVO.getCategoryVO().getFixedRoles())) {
                roleAvailable = userDAO.isFixedRoleAndExist(p_con, channelUserVO.getCategoryCode(), (String) p_requestVO.getRequestMap().get("TYPE"),
                    TypesI.OPERATOR_USER_TYPE);
            } else {
                roleAvailable = userDAO.isAssignedRoleAndExist(p_con, channelUserVO.getUserID(), (String) p_requestVO.getRequestMap().get("TYPE"), TypesI.OPERATOR_USER_TYPE);
            }

            if (!roleAvailable) {
                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_USER_ROLE_UNAVAILABLE);
            }

            // load geographical area
            GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
            channelUserVO.setGeographicalAreaList(_geographyDAO.loadUserGeographyList(p_con, channelUserVO.getUserID(), channelUserVO.getNetworkID()));
            // load domains
            DomainDAO domainDAO = new DomainDAO();
            channelUserVO.setDomainList(domainDAO.loadDomainListByUserId(p_con, channelUserVO.getUserID()));
            UserPhoneVO phoneVO = new UserPhoneVO();
            phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
            phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            channelUserVO.setUserPhoneVO(phoneVO);
            p_requestVO.setFilteredMSISDN(channelUserVO.getMsisdn());
            p_requestVO.setSenderVO(channelUserVO);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.XML_ERROR_EXCEPTION);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadValidateUserDetails", "Exiting Request ID=" + p_requestVO.getRequestID() + " channelUserVO=" + channelUserVO);
        }
        return channelUserVO;
    }

    /**
     * Method to load and validate network details
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void loadValidateNetworkDetails(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "loadValidateNetworkDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadValidateNetworkDetails", "Entered Request ID=" + p_requestVO.getRequestID());
        }

        try {
            String extNetCode = (String) p_requestVO.getRequestMap().get("EXTNWCODE");
            NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extNetCode);
            if (networkVO == null) {
                throw new BTSLBaseException(this, "loadValidateNetworkDetails", PretupsErrorCodesI.XML_ERROR_EXT_NETWORK_CODE);
            }
            NetworkPrefixVO networkPrefixVO = new NetworkPrefixVO();
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
            p_requestVO.setValueObject(networkPrefixVO);
            validateNetwork(p_requestVO, networkPrefixVO);

            String message = null;
            p_requestVO.setRequestNetworkCode(networkPrefixVO.getNetworkCode());

            // Check for location status (Active or suspend)
            if (!TypesI.YES.equals(networkPrefixVO.getStatus())) {
                // if default language is english then pick language 1 message
                // else language 2
                LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    message = networkPrefixVO.getLanguage1Message();
                } else {
                    message = networkPrefixVO.getLanguage2Message();
                }
                p_requestVO.setSenderReturnMessage(message);
                throw new BTSLBaseException(this, "loadValidateNetworkDetails", PretupsErrorCodesI.XML_NETWORK_NOT_ACTIVE);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadValidateNetworkDetails", "Exiting Request ID=" + p_requestVO.getRequestID());
            }
        }
    }

    /**
     * Method to validate the User Identifier , if MSISDN is there then only
     * validate
     * 
     * @param p_requestVO
     */
    public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException {
    }

    public void parseChannelRequest(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseChannelRequest";
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelRequest", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        try {
            String request = p_requestVO.getRequestMessage();
            int index = request.indexOf("<TYPE>");
            String requestType = request.substring(index + "<TYPE>".length(), request.indexOf("</TYPE>", index));
            HashMap hashMap = new HashMap();
            p_requestVO.setRequestMap(hashMap);
            if ("BARUSER".equals(requestType)) {
                XMLStringParser.parseBarChannelUserRequest(p_requestVO);
            } else if ("UNBARUSER".equals(requestType)) {
                XMLStringParser.parseUnbarChannelUserRequest(p_requestVO);
            } else if ("VIEWBARREDLIST".equals(requestType)) {
                XMLStringParser.parseViewBarChannelUserRequest(p_requestVO);
            } else if ("C2STRFANSFERENQ".equals(requestType)) {
                XMLStringParser.parseC2STrfEnquiryRequest(p_requestVO);
            } else if ("C2CTRFENQ".equals(requestType)) {
                XMLStringParser.parseC2CTrfEnquiryRequest(p_requestVO);
            } else if ("VIEWTRF".equals(requestType)) {
                XMLStringParser.parseO2CTrfEnquiryRequest(p_requestVO);
            } else if ("P2PENQ1".equals(requestType)) {
                XMLStringParser.parseP2PTrfEnquiryRequest(p_requestVO);
            } else if ("P2PENQ2".equals(requestType)) {
                XMLStringParser.parseP2PReceiverTrfEnquiryRequest(p_requestVO);
            } else if ("CHANGELANG".equals(requestType)) {
                XMLStringParser.parseChangeLanguageRequest(p_requestVO);
            } else if ("ICCIDMSISDNENQ".equals(requestType)) {
                XMLStringParser.parseIccidMsisdnEnquiryRequest(p_requestVO);
            } else if ("INENQREQ".equals(requestType)) {
                XMLStringParser.parseRoutingEnquiryRequest(p_requestVO);
            } else if ("WHTLSTENQ".equals(requestType)) {
                XMLStringParser.parseWhiteListEnquiryRequest(p_requestVO);
            } else if ("C2SUNBLOCKPIN".equals(requestType)) {
                XMLStringParser.parsePinMgtRequest(p_requestVO);
            } else if ("SUSPENDSERVICE".equals(requestType)) {
                XMLStringParser.parseSuspendP2PRequest(p_requestVO);
            } else if ("RESUMESERVICE".equals(requestType)) {
                XMLStringParser.parseResumeP2PRequest(p_requestVO);
            } else if ("VIEWREGSUBSCRIBER".equals(requestType)) {
                XMLStringParser.parseRegisterP2PRequest(p_requestVO);
            } else if ("DELETEREGSUBSCRIBER".equals(requestType)) {
                XMLStringParser.parseDeregisterP2PRequest(p_requestVO);
            } else if ("C2SUNBLOCKPAS".equals(requestType)) {
                XMLStringParser.parseAccCtrlMgtRequest(p_requestVO);
            } else if ("VIEWCUSER".equals(requestType)) {
                XMLStringParser.parseViewChnlUserRequest(p_requestVO);
            } else if ("SUSPENDCUSER".equals(requestType)) {
                XMLStringParser.parseSuspendResumeUserRequest(p_requestVO);
            } else if ("OTHERBALANCE".equals(requestType)) {
                XMLStringParser.parseUserBalanceRequest(p_requestVO);
            } else if ("UPLOADMNPFILE".equals(requestType)) {
                ExtAPIXMLStringParser.parseMNPUploadRequest(p_requestVO);
            } else if ("ICCIDMSISDNMAP".equals(requestType)) {
                XMLStringParser.parseIccidMsisdnMapRequest(p_requestVO);
            } else if ("VOUENQREQ".equals(requestType)) {
                XMLStringParser.parseVoucherEnqReq(p_requestVO);
            } else if ("VOUCONSREQ".equals(requestType)) {
                XMLStringParser.parseVoucherConsReq(p_requestVO);
            } else if ("O2CEXTENQREQ".equals(requestType)) {
                ExtAPIXMLStringParser.parseO2CExtSAPEnqRequest(p_requestVO);
            } else if ("O2CEXTCODEUPDREQ".equals(requestType)) {
                ExtAPIXMLStringParser.parseO2CExtCodeUpdateRequest(p_requestVO);
            } else if ("OPTUSRADDREQ".equals(requestType)) {
                XMLStringParser.parseOperatorUserAddRequest(p_requestVO);
            } else if ("OPTUSRMODREQ".equals(requestType)) {
                XMLStringParser.parseOperatorUserModRequest(p_requestVO);
            } else if ("OPTUSRSRDREQ".equals(requestType)) {
                XMLStringParser.parseOperatorUserSRDRequest(p_requestVO);
            } 
			else if( requestType.equals("USERMOVEMENTREQ"))
		    	XMLStringParser.parseChannelUserTrfRequest(p_requestVO);
            else {
                index = request.indexOf("<EXTREFNUM>");
                String extRefNumber = request.substring(index + "<EXTREFNUM>".length(), request.indexOf("</EXTREFNUM>", index));
                hashMap.put("EXTREFNUM", extRefNumber);
                p_requestVO.setRequestMap(hashMap);
                throw new BTSLBaseException(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            throw be;
        }
    }

    public void generateChannelParserResponse(RequestVO p_requestVO) throws Exception {
        final String METHOD_NAME = "generateChannelParserResponse";
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelParserResponse", "Entered Request ID=" + p_requestVO.getRequestID());
        }
        try {
            String request = p_requestVO.getRequestMessage();
            int index = request.indexOf("<TYPE>");
            String requestType = request.substring(index + "<TYPE>".length(), request.indexOf("</TYPE>", index));
            if ("BARUSER".equals(requestType)) {
                XMLStringParser.generateBarChannelUserResponse(p_requestVO);
            } else if ("UNBARUSER".equals(requestType)) {
                XMLStringParser.generateUnbarChannelUserResponse(p_requestVO);
            } else if ("VIEWBARREDLIST".equals(requestType)) {
                XMLStringParser.generateViewBarChannelUserResponse(p_requestVO);
            } else if ("C2STRFANSFERENQ".equals(requestType)) {
                XMLStringParser.generateC2STrfEnquiryResponse(p_requestVO);
            } else if ("C2CTRFENQ".equals(requestType)) {
                XMLStringParser.generateC2CTrfEnquiryResponse(p_requestVO);
            } else if ("VIEWTRF".equals(requestType)) {
                XMLStringParser.generateO2CTrfEnquiryResponse(p_requestVO);
            } else if ("P2PENQ1".equals(requestType)) {
                XMLStringParser.generateP2PTrfEnquiryResponse(p_requestVO);
            } else if ("P2PENQ2".equals(requestType)) {
                XMLStringParser.generateP2PRecTrfEnquiryResponse(p_requestVO);
            } else if ("CHANGELANG".equals(requestType)) {
                XMLStringParser.generateChangeLanguageResponse(p_requestVO);
            } else if ("ICCIDMSISDNENQ".equals(requestType)) {
                XMLStringParser.generateIccidMsisdnEnquiryResponse(p_requestVO);
            } else if ("INENQREQ".equals(requestType)) {
                XMLStringParser.generateRoutingEnquiryResponse(p_requestVO);
            } else if ("WHTLSTENQ".equals(requestType)) {
                XMLStringParser.generateWhiteListEnquiryResponse(p_requestVO);
            } else if ("C2SUNBLOCKPIN".equals(requestType)) {
                XMLStringParser.generatePinMgtResponse(p_requestVO);
            } else if ("SUSPENDSERVICE".equals(requestType)) {
                XMLStringParser.generateSuspendP2PResponse(p_requestVO);
            } else if ("RESUMESERVICE".equals(requestType)) {
                XMLStringParser.generateResumeP2PResponse(p_requestVO);
            } else if ("VIEWREGSUBSCRIBER".equals(requestType)) {
                XMLStringParser.generateRegisterP2PResponse(p_requestVO);
            } else if ("DELETEREGSUBSCRIBER".equals(requestType)) {
                XMLStringParser.generateDeregisterP2PResponse(p_requestVO);
            } else if ("C2SUNBLOCKPAS".equals(requestType)) {
                XMLStringParser.generateAccCtrlMgtResponse(p_requestVO);
            } else if ("VIEWCUSER".equals(requestType)) {
                XMLStringParser.generateViewChnlUserResponse(p_requestVO);
            } else if ("SUSPENDCUSER".equals(requestType)) {
                XMLStringParser.generateSuspendResumeUserResponse(p_requestVO);
            } else if ("OTHERBALANCE".equals(requestType)) {
                XMLStringParser.generateUserBalanceResponse(p_requestVO);
            } else if ("MNPROVI".equals(requestType)) {
                ExtAPIXMLStringParser.generateMNPUploadResponse(p_requestVO);
            } else if ("ICCIDMSISDNMAP".equals(requestType)) {
                XMLStringParser.generateIccidMsisdnMapResponse(p_requestVO);
            } else if ("VOUENQREQ".equals(requestType)) {
                XMLStringParser.generateVoucherEnqResponse(p_requestVO);
            } else if ("VOUCONSREQ".equals(requestType)) {
                XMLStringParser.generateVoucherConsResponse(p_requestVO);
            } else if ("O2CEXTENQREQ".equals(requestType)) {
                ExtAPIXMLStringParser.generateO2CExtSAPEnqResponse(p_requestVO);
            } else if ("O2CEXTCODEUPDREQ".equals(requestType)) {
                ExtAPIXMLStringParser.generateO2CExtCodeUpdateResponse(p_requestVO);
            } else if ("OPTUSRADDREQ".equals(requestType)) {
                XMLStringParser.generateOperatorUserAddResponse(p_requestVO);
            } else if ("OPTUSRMODREQ".equals(requestType)) {
                XMLStringParser.generateOperatorUserModResponse(p_requestVO);
            } else if ("OPTUSRSRDREQ".equals(requestType)) {
                XMLStringParser.generateOperatorUserSRDResponse(p_requestVO);
            } 
            else if("USERMOVEMENTREQ".equals(requestType))
		        XMLStringParser.generateUserTransferResponse(p_requestVO);
            
            else {
                XMLStringParser.generateFailureResponseForCCE(p_requestVO);
                // Added by zafar :: To genereate response when TYPE TAGs are
                // not
                // proper in request
            }
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateChannelParserResponse", "Exception" + e.getMessage());
            }
            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            XMLStringParser.generateFailureResponseForCCE(p_requestVO);
        }
    }

    /**
     * Method to parse the Operator request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "parseOperatorRequestMessage";
        String contentType = p_requestVO.getReqContentType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseOperatorRequestMessage", "p_requestVO: " + p_requestVO.toString());
        }
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                parseChannelRequest(p_requestVO);
            } else {
                // Forward to plain message parsing
                // Set message Format , set in decrypted message
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(METHOD_NAME, be);
            LOG.error("parseOperatorRequestMessage", "BTSLBaseException: " + be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseOperatorRequestMessage", "Exception e: " + e);
            throw new BTSLBaseException("PretupsParser", "parseOperatorRequestMessage", PretupsErrorCodesI.XML_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseOperatorRequestMessage", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }
}
