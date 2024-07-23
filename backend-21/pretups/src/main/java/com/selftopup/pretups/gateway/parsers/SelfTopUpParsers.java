package com.selftopup.pretups.gateway.parsers;

import java.sql.Connection;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.gateway.util.ParserUtility;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.network.businesslogic.NetworkCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.network.businesslogic.NetworkVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsDAO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.selftopup.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class SelfTopUpParsers extends ParserUtility {
    private static String _type = null;
    public static Log _log = LogFactory.getLog(SelfTopUpParsers.class.getName());
    public static String P2P_MESSAGE_SEP = null;
    static {
        try {
            P2P_MESSAGE_SEP = SystemPreferences.P2P_PLAIN_SMS_SEPARATOR;
            if (BTSLUtil.isNullString(P2P_MESSAGE_SEP))
                P2P_MESSAGE_SEP = " ";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled())
            _log.debug("parseRequestMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        try {
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("XML") != -1)) {
                // Forward to XML parsing
                p_requestVO.setReqContentType(contentType);
                parseSelfTopupRequest(p_requestVO);
            } else if (contentType != null && (contentType.indexOf("text/xml") != -1 || contentType.indexOf("TEXT/XML") != -1)) {
                // Forward to XML parsing
                p_requestVO.setReqContentType(contentType);
                parseSelfTopupRequest(p_requestVO);
            } else if (contentType != null && (contentType.indexOf("plain") != -1 || contentType.indexOf("PLAIN") != -1)) {

                p_requestVO.setReqContentType(contentType);

                parseSelfTopupRequest(p_requestVO);
            } else {
                p_requestVO.setDecryptedMessage(p_requestVO.getRequestMessage());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("parseRequestMessage", "Message =" + p_requestVO.getDecryptedMessage() + " MSISDN=" + p_requestVO.getRequestMSISDN());

            }
        } catch (BTSLBaseException be) {
            _log.error("parseRequestMessage", " BTSL Exception while parsing Request Message :" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("parseRequestMessage", "  Exception while parsing Request Message :" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[parseRequestMessage]", p_requestVO.getTransactionID(), "", "", "Exception :" + e.getMessage());
            throw new BTSLBaseException("ExtAPIParsers", "parseRequestMessage", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
    }

    /**
     * Method to validate the User Identifier , if MSISDN is there then only
     * validate
     * 
     * @param p_requestVO
     */
    public void validateUserIdentification(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("validateUserIdentification", "Entered Request ID=" + p_requestVO.getRequestID());
        // Validate user on the basis of values provided.
        // If MSISDN is there then validate the same.
        if (!BTSLUtil.isNullString(p_requestVO.getRequestMSISDN()))
            validateMSISDN(p_requestVO);
    }

    /**
     * Method to load and validate network details
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void loadValidateNetworkDetails(RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadValidateNetworkDetails", "Entered Request ID=" + p_requestVO.getRequestID());

        try {
            NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(p_requestVO.getExternalNetworkCode());
            NetworkPrefixVO phoneNetworkPrefixVO = null;
            NetworkPrefixVO networkPrefixVO = null;
            // Also check if MSISDN is there then get the network Details from
            // it and match with network from external code
            if (networkVO != null && !BTSLUtil.isNullString(p_requestVO.getFilteredMSISDN())) {
                phoneNetworkPrefixVO = PretupsBL.getNetworkDetails(p_requestVO.getFilteredMSISDN(), PretupsI.USER_TYPE_SENDER);
                if (!phoneNetworkPrefixVO.getNetworkCode().equals(networkVO.getNetworkCode()))
                    throw new BTSLBaseException(this, "loadValidateNetworkDetails", SelfTopUpErrorCodesI.NETWORK_CODE_MSIDN_NETWORK_MISMATCH);
            } else if (networkVO == null) {
                throw new BTSLBaseException(this, "loadValidateNetworkDetails", SelfTopUpErrorCodesI.ERROR_EXT_NETWORK_CODE);
            }
            if (phoneNetworkPrefixVO != null) {
                p_requestVO.setValueObject(phoneNetworkPrefixVO);
                validateNetwork(p_requestVO, phoneNetworkPrefixVO);
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
                p_requestVO.setValueObject(networkPrefixVO);
                validateNetwork(p_requestVO, networkPrefixVO);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("loadValidateNetworkDetails", "Exiting Request ID=" + p_requestVO.getRequestID());
        }
    }

    /**
     * Method to parse the Operator request
     * 
     * @param p_requestVO
     * @throws BTSLBaseException
     */
    public void parseOperatorRequestMessage(RequestVO p_requestVO) throws BTSLBaseException {
    }

    /*
     * author gaurav.pandey
     */
    public static void parseSelfTopupRequest(RequestVO p_requestVO) throws Exception {
        final String METHOD_NAME = "parseSelfTopupRequest";
        if (_log.isDebugEnabled())
            _log.debug(METHOD_NAME, "Entered p_requestVO: " + p_requestVO.toString());
        StringBuilder parsedRequestStr = new StringBuilder();
        Connection con = null;
        try {
            String requestStr = p_requestVO.getRequestMessage();
            String LANGUAGE1 = null;
            String LANGUAGE2 = null;
            String imei = null;
            int i1 = -1;
            HashMap requestMap = null;
            i1 = requestStr.indexOf("Message=");
            if (i1 > -1) {
                String msg2 = requestStr.substring(i1 + "Message=".length(), requestStr.length());
                // String msg1=requestStr.substring(i1);
                String msg1 = requestStr.substring(0, i1);
                // HashMap
                // requestMap=BTSLUtil.getStringToHash(requestStr,"&","####");
                // if(i1 > -1)

                requestMap = BTSLUtil.getStringToHash(msg1, "&", "=");
                requestMap.put("Message", msg2);

            } else
                requestMap = BTSLUtil.getStringToHash(requestStr, "&", "=");
            HashMap seviceKeywordMap = new HashMap();
            // added for encryption
            // String imei=null;
            String Message = null;
            String key = null;
            CardDetailsDAO cardDetailsDAO = null;
            _type = (String) requestMap.get("TYPE");
            String msisdn = (String) requestMap.get("MSISDN");
            // ServiceKeywordCacheVO
            // serviceKeywordCacheVO=(ServiceKeywordCacheVO)ServiceKeywordCache.getServiceTypeObject(_type,
            // "P2P");
            seviceKeywordMap = ServiceKeywordCache.getServiceKeywordMap();
            ServiceKeywordCacheVO serviceKeywordCacheVO = (ServiceKeywordCacheVO) seviceKeywordMap.get(_type + "_" + p_requestVO.getModule() + "_" + p_requestVO.getRequestGatewayCode() + "_" + p_requestVO.getServicePort());

            String Message_formate = serviceKeywordCacheVO.getMessageFormat();
            String Request_formate = serviceKeywordCacheVO.getRequestParam();
            String[] mandatory_fields;
            String[] requestParameters;
            if (BTSLUtil.isNullString(Request_formate) || (BTSLUtil.isNullString(Message_formate))) {
                throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);

            } else {
                mandatory_fields = Request_formate.split(",");
                requestParameters = Message_formate.split(" ");
            }
            if (requestMap.containsKey("Message")) {
                try {
                    con = OracleUtil.getConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
                    throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
                }
                cardDetailsDAO = new CardDetailsDAO();
                msisdn = (String) requestMap.get("MSISDN");
                Message = (String) requestMap.get("Message");

                if (BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(Message)) {
                    throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
                } else {

                    Message = Message.replaceAll("\\s", "+");
                    key = cardDetailsDAO.getSubscriberKey(con, msisdn);
                    if (key.isEmpty()) {
                        throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_ERROR_INVALID_SENDER_MSISDN);
                    }
                    Message = BTSLUtil.decryptAESNew(Message, key);

                    requestMap = BTSLUtil.getStringToHash(Message, "&", "=");
                    requestMap.put("TYPE", _type);
                    requestMap.put("MSISDN", msisdn);
                    // parsedRequestStr.append(_type);
                    for (int i = 0; i < mandatory_fields.length; i++) {
                        if (requestMap.containsKey(mandatory_fields[i])) {
                            if (BTSLUtil.isNullString((String) requestMap.get(mandatory_fields[i]))) {
                                throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
                            }
                        } else {
                            throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
                        }
                    }

                    for (int i = 0; i < requestParameters.length; i++) {
                        if (requestMap.containsKey(requestParameters[i])) {
                            parsedRequestStr = parsedRequestStr.append(requestMap.get(requestParameters[i]));
                            parsedRequestStr.append(P2P_MESSAGE_SEP);
                        }
                    }

                }

            }

            else {
                for (int i = 0; i < mandatory_fields.length; i++) {
                    if (requestMap.containsKey(mandatory_fields[i])) {
                        if (BTSLUtil.isNullString((String) requestMap.get(mandatory_fields[i]))) {
                            throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
                        }
                    } else {
                        throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
                    }
                }

                for (int i = 0; i < requestParameters.length; i++) {
                    if (requestMap.containsKey(requestParameters[i])) {
                        parsedRequestStr = parsedRequestStr.append(requestMap.get(requestParameters[i]));
                        parsedRequestStr.append(P2P_MESSAGE_SEP);

                    }
                }

            }
            // Length check of IMEI
            if (requestMap.containsKey("IMEI")) {
                imei = (String) requestMap.get("IMEI");
                if (imei.length() != PretupsI.IMEI_LENGTH || !BTSLUtil.isNumeric(imei) || BTSLUtil.isNullString(imei)) {
                    _log.error(METHOD_NAME, "IMEI is not of length 15 or is not numeric or null");
                    throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.ERROR_INVALID_IMEI);
                }
            }

            // Language parameters handling start
            if (requestMap.containsKey("LANGUAGE1")) {
                LANGUAGE1 = (String) requestMap.get("LANGUAGE1");
                if (LocaleMasterCache.getLocaleFromCodeDetails(LANGUAGE1) != null) {
                    p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(LANGUAGE1));
                } else {
                    _log.error(METHOD_NAME, "LANGUAGE1 is not numeric");
                    throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                }
            }
            if (PretupsI.SERVICE_TYPE_SELFTOPUP_ADHOCRECHARGE.equalsIgnoreCase(serviceKeywordCacheVO.getServiceType()) || PretupsI.SERVICE_TYPE_SELFTOPUP_RECHARGE_USING_REGISTERED_CARD.equalsIgnoreCase(serviceKeywordCacheVO.getServiceType())) {
                if (requestMap.containsKey("LANGUAGE2")) {
                    LANGUAGE2 = (String) requestMap.get("LANGUAGE2");
                    if (LocaleMasterCache.getLocaleFromCodeDetails(LANGUAGE2) != null) {
                        p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(LANGUAGE2));
                    } else {
                        _log.error(METHOD_NAME, "LANGUAGE2 is not numeric");
                        throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
                    }
                }
            }
            if (!BTSLUtil.isNumeric(LANGUAGE1) || !BTSLUtil.isNumeric(LANGUAGE2)) {
                _log.error(METHOD_NAME, "LANGUAGE1 or LANGUAGE2 is not numeric");
                throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "language1:" + LANGUAGE1 + ",language2:" + LANGUAGE2 + ":");
            }
            // Language parameters handling end
            p_requestVO.setDecryptedMessage(parsedRequestStr.toString());
            p_requestVO.setRequestMSISDN(msisdn);
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("SelfTopUpParsers", METHOD_NAME, SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME, "Exiting p_requestVO: " + p_requestVO.toString());
        }

    }

    /**
     * Method to parse request of P2P on basis of action
     * 
     * @param p_requestVO
     * @throws Exception
     * @auther Gaurav.pandey
     */
    public static void generateSelfTopupResponse(RequestVO p_requestVO) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateSelfTopupResponse", "Entered p_requestVO: " + p_requestVO.toString());
        String responseStr = null;
        String message = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1024);

            int index = _type.trim().indexOf("REQ");
            if (index != -1) {
                sbf.append("TYPE=" + _type.substring(0, index) + "RES" + _type.substring(index + 3));
            } else {
                sbf.append("TYPE=" + _type + "RESP");
            }

            // System.out.println("shishupal="+p_requestVO.isSuccessTxn()+p_requestVO.getSenderReturnMessage()+p_requestVO.getMessageCode());
            if (p_requestVO.isSuccessTxn())
                // if(p_requestVO.isSuccessTxn() &&
                // p_requestVO.getMessageCode().equals(PretupsI.TXN_STATUS_SUCCESS)z
                // )
                sbf.append("&TXNSTATUS=" + PretupsI.TXN_STATUS_SUCCESS);
            else
                sbf.append("&TXNSTATUS=" + p_requestVO.getMessageCode());
            if (!BTSLUtil.isNullString(p_requestVO.getServiceType())) {
                if ("STUREG".equals(p_requestVO.getServiceType()) || "STUREG2".equals(p_requestVO.getServiceType()))
                    sbf.append("&ENK=" + p_requestVO.getEncryptionKey());

                if ("VWCARD".equals(p_requestVO.getServiceType())) {
                    if (p_requestVO.getNumberOfRegisteredCards() > 0) {
                        sbf.append("&REGISTEREDCARDS=" + p_requestVO.getNumberOfRegisteredCards());
                    }
                }
            }
            if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage())) {
                message = p_requestVO.getSenderReturnMessage();
            } else {
                message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
            }

            if (!BTSLUtil.isNullString(p_requestVO.getServiceType())) {
                if ("VWCARD".equals(p_requestVO.getServiceType()) && p_requestVO.getNumberOfRegisteredCards() > 0) {
                    sbf.append("&CARDDETAILS=" + message.substring(message.indexOf(message.split(":")[2]), (message.length())).trim());
                }
            }

            if (!("VWCARD".equals(p_requestVO.getServiceType()) && p_requestVO.getNumberOfRegisteredCards() > 0)) {
                sbf.append("&MESSAGE=" + message.substring(message.indexOf(message.split(":")[2]), (message.length())));
            }

            responseStr = sbf.toString();
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            _log.error("generateSelfTopupResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpParsers[generateSelfTopupResponse]", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateSelfTopupResponse:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateSelfTopupResponse", "Exiting responseStr: " + responseStr);
        }
    }

    public void generateResponseMessage(RequestVO p_requestVO) {
        String contentType = p_requestVO.getReqContentType();
        if (_log.isDebugEnabled())
            _log.debug("generateResponseMessage", "Transfer ID=" + p_requestVO.getRequestID() + " contentType: " + contentType);
        try {
            if (contentType != null && (p_requestVO.getReqContentType().indexOf("xml") != -1 || p_requestVO.getReqContentType().indexOf("XML") != -1)) {
                // Set the Sender Return Message
                if (p_requestVO.getActionValue() == -1)
                    actionParser(p_requestVO);
                // generateResponse(p_requestVO.getActionValue(),p_requestVO);
                generateSelfTopupResponse(p_requestVO);
            } else if (contentType != null && (p_requestVO.getReqContentType().indexOf("plain") != -1 || p_requestVO.getReqContentType().indexOf("PLAIN") != -1) && SystemPreferences.PLAIN_RES_PARSE_REQUIRED) {
                // Set the Sender Return Message
                // if(p_requestVO.getActionValue()==-1)
                // actionParser(p_requestVO);
                // generatePlainResponse(p_requestVO.getActionValue(),p_requestVO);
                generateSelfTopupResponse(p_requestVO);
            } else {
                String message = null;
                if (!BTSLUtil.isNullString(p_requestVO.getSenderReturnMessage()))
                    message = p_requestVO.getSenderReturnMessage();
                else
                    message = BTSLUtil.getMessage(p_requestVO.getLocale(), p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());

                p_requestVO.setSenderReturnMessage(message);
            }
        } catch (Exception e) {
            _log.error("generateResponseMessage", "  Exception while generating Response Message :" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[generateResponseMessage]", p_requestVO.getTransactionID(), "", "", "Exception getting message :" + e.getMessage());
            try {
                generateSelfTopupResponse(p_requestVO);
            } catch (Exception ex) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtAPIParsers[generateResponseMessage]", p_requestVO.getTransactionID(), "", "", "Exception getting default message :" + ex.getMessage());
                p_requestVO.setSenderReturnMessage("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND><TYPE></TYPE><TXNSTATUS>" + SelfTopUpErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT + "</TXNSTATUS></COMMAND>");
            }
        }
    }
}
