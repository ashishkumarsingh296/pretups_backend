package com.btsl.pretups.gateway.util;

/*
 * @(#)XMLAPIParser.java
 * Copyright(c) 2005, Bharti Telesoft Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Author Date History
 * ------------------------------------------------------------------------------
 * -------------------
 * Manoj Kumar Nov 04, 2005 Initital Creation
 * Gurjeet Singh Nov 08, 2005 Modified
 * Gurjeet Singh Dec 12, 2005 Modified (Added Channel related methods)
 * ------------------------------------------------------------------------------
 * -------------------
 * XML API Interface class
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class XMLAPIParser {

    public static final int ACTION_ACCOUNT_INFO = 0;
    public static final int CREDIT_TRANSFER = 1;
    public static final int CHANGE_PIN = 2;
    public static final int NOTIFICATION_LANGUAGE = 3;
    public static final int HISTORY_MESSAGE = 4;

    public static final int ACTION_CHNL_ACCOUNT_INFO = 0;
    public static final int ACTION_CHNL_CREDIT_TRANSFER = 1;
    public static final int ACTION_CHNL_CHANGE_PIN = 2;
    public static final int ACTION_CHNL_NOTIFICATION_LANGUAGE = 3;
    public static final int ACTION_CHNL_HISTORY_MESSAGE = 4;
    public static final int ACTION_CHNL_TRANSFER_MESSAGE = 5;
    public static final int ACTION_CHNL_RETURN_MESSAGE = 6;
    public static final int ACTION_CHNL_WITHDRAW_MESSAGE = 7;
    private static String CHNL_MESSAGE_SEP = null;
    private static String P2P_MESSAGE_SEP = null;

    public static final Log LOG = LogFactory.getLog(XMLAPIParser.class.getName());

    protected static DocumentBuilderFactory _dbf = DocumentBuilderFactory.newInstance();
    protected static javax.xml.parsers.DocumentBuilder _parser = null;
    private static final String BLANK = "";
    static {
        try {
            CHNL_MESSAGE_SEP = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
            if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
                CHNL_MESSAGE_SEP = " ";
            }
            P2P_MESSAGE_SEP = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
            if (BTSLUtil.isNullString(P2P_MESSAGE_SEP)) {
                P2P_MESSAGE_SEP = " ";
            }

            _dbf.setValidating(false);
            _parser = _dbf.newDocumentBuilder();
            _parser.setErrorHandler(new org.xml.sax.ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    LOG.errorTrace("warning", e);
                }

                @Override
                public void error(SAXParseException e) {
                    LOG.errorTrace("error", e);
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    LOG.errorTrace("fatalError", e);
                    throw e; // re-throw the error
                }
            });
        } catch (Exception e) {
            LOG.errorTrace("static", e);
        }
    }
    
    /**
	 * ensures no instantiation
	 */
    private XMLAPIParser(){
    	
    }

    /**
     * this method use for identify the action
     * 
     * @param requestStr
     *            java.lang.String
     * @return action int
     */
    public static void actionParser(RequestVO p_requestVO) throws BTSLBaseException {
        final String requestStr = p_requestVO.getRequestMessage();
        final String METHOD_NAME = "actionParser";
        if (LOG.isDebugEnabled()) {
            LOG.debug("requestParser", "Entered responseStr: " + requestStr);
        }
        int action = -1;
        try {
            if (requestStr.indexOf("CACINFREQ") != -1) {
                action = 0;
            } else if (requestStr.indexOf("CCTRFREQ") != -1) {
                action = 1;
            }
            if (requestStr.indexOf("CCPNREQ") != -1) {
                action = 2;
            } else if (requestStr.indexOf("CCLANGREQ") != -1) {
                action = 3;
            } else if (requestStr.indexOf("CCHISREQ") != -1) {
                action = 4;
            }

            if (action == -1) {
                throw new BTSLBaseException(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            } else {
                p_requestVO.setActionValue(action);
                parseRequest(action, p_requestVO);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            LOG.error("actionParser", "Exception e: " + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("");
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("actionParser", "exit action:" + action);
            }
        }
    }

    /**
     * this method use for identify the action
     * 
     * @param requestStr
     *            java.lang.String
     * @return action int
     */
    public static void actionChannelParser(RequestVO p_requestVO) throws BTSLBaseException {
        final String requestStr = p_requestVO.getRequestMessage();
        final String METHOD_NAME = "actionChannelParser";
        if (LOG.isDebugEnabled()) {
            LOG.debug("actionChannelParser", "Entered requestStr: " + requestStr);
        }
        int action = -1;
        try {
            if (requestStr.indexOf("CACINFREQ") != -1) {
                action = 0;
            } else if (requestStr.indexOf("RCTRFREQ") != -1) {
                action = 1;
            } else if (requestStr.indexOf("RCPNREQ") != -1) {
                action = 2;
            } else if (requestStr.indexOf("RCNLANGREQ") != -1) {
                action = 3;
            } else if (requestStr.indexOf("CCHISREQ") != -1) {
                action = 4;
            } else if (requestStr.indexOf("TRFREQ") != -1) {
                action = 5;
            } else if (requestStr.indexOf("RETREQ") != -1) {
                action = 6;
            } else if (requestStr.indexOf("WDTHREQ") != -1) {
                action = 7;
            }

            if (action == -1) {
                throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            } else {
                p_requestVO.setActionValue(action);
                parseChannelRequest(action, p_requestVO);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            LOG.error("actionChannelParser", "Exception e: " + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("");
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("actionChannelParser", "exit action:" + action);
            }
        }
    }

    /**
     * this method parse the request from XML String and formating into white
     * space seperated String
     * 
     * @param action
     *            int
     * @param requestStr
     *            java.lang.String
     * @return parsedRequestStr java.lang.String
     */
    public static void parseRequest(int action, RequestVO p_requestVO) throws Exception {
        // Set Filtered MSISDN, set _requestMSISDN
        // Set message Format , set in decrypted message
    	final String METHOD_NAME = "generateResponse";
    	StringBuilder sb = new StringBuilder(1024);
        switch (action) {
            case ACTION_ACCOUNT_INFO:
                {
                    parseGetAccountInfoRequest(p_requestVO);
                    break;
                }
            case CREDIT_TRANSFER:
                {
                    parseCreditTransferRequest(p_requestVO);
                    break;
                }
            case CHANGE_PIN:
                {
                    parseChangePinRequest(p_requestVO);
                    break;
                }
            case NOTIFICATION_LANGUAGE:
                {
                    parseNotificationLanguageRequest(p_requestVO);
                    break;
                }
            case HISTORY_MESSAGE:
                {
                    parseHistoryMessageRequest(p_requestVO);
                    break;
                }
            default :
            {
            	sb.append("Defualt : Request ID = ").append(p_requestVO.getRequestID());
            	sb.append("Action= ").append(action);
            	LogFactory.printLog(METHOD_NAME, sb, LOG);
            }    
        }
    }

    /**
     * this method construct the response from HashMap into XML String
     * 
     * @param action
     *            int
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    public static void generateResponse(int action, RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "generateResponse";
    	StringBuilder sb = new StringBuilder(1024);
        switch (action) {
            case ACTION_ACCOUNT_INFO:
                {
                    generateGetAccountInfoResponse(p_requestVO);
                    break;
                }
            case CREDIT_TRANSFER:
                {
                    generateCreditTransferResponse(p_requestVO);
                    break;
                }
            case CHANGE_PIN:
                {
                    generateChangePinResponse(p_requestVO);
                    break;
                }
            case NOTIFICATION_LANGUAGE:
                {
                    generateNotificationLanguageResponse(p_requestVO);
                    break;
                }
            case HISTORY_MESSAGE:
                {
                    generateHistoryMessageResponse(p_requestVO);
                    break;
                }
            default :
            {
            	sb.append("Defualt : Request ID = ").append(p_requestVO.getRequestID());
            	sb.append("Action= ").append(action);
            	LogFactory.printLog(METHOD_NAME, sb, LOG);
            }    
        }
    }

    /**
     * this method parse GetAccountInfoRequest from XML String and formating
     * into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    private static void parseGetAccountInfoRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseGetAccountInfoRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(requestStr.getBytes())) {
           
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            final Element msisdn1Tag = (Element) accountInfoGettingRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element selectorTag = (Element) accountInfoGettingRequestTag.getElementsByTagName("SELECTOR").item(0);
            final Text selectorTagNode = (Text) selectorTag.getFirstChild();
            String selector = null;
            if (selectorTagNode == null) {
                selector = "";
            } else {
                selector = selectorTagNode.getData().trim();
            }
            // parsedRequestStr=type+" "+msisdn1+" "+selector;
            parsedRequestStr = PretupsI.SERVICE_TYPE_ACCOUNTINFO + P2P_MESSAGE_SEP + msisdn1;
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (Exception e) {
            LOG.error("parseGetAccountInfoRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseGetAccountInfoRequest", "Exiting  p_requestVO: " + p_requestVO);
            }
        }
    }

    /**
     * this method connstruct getAccountInfoResponse in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    private static void generateGetAccountInfoResponse(RequestVO p_requestVO) throws Exception {
        String responseStr = null;
        boolean reqFromP2P = false;
        SenderVO senderVO = null;
        final String xmlMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>";
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(xmlMessage.getBytes())) {
            if (p_requestVO.getModule().equals(PretupsI.P2P_MODULE)) {
                senderVO = (SenderVO) p_requestVO.getSenderVO();
                reqFromP2P = true;
            }
      
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element accountInfoGettingResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element regStatusNode = document.createElement("REGSTATUS");
            final Element pinStatusNode = document.createElement("PINSTATUS");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            final Element minRemBalNode = document.createElement("MINREMBAL");
            final Element minAmtNode = document.createElement("MINAMT");
            final Element maxAmtNode = document.createElement("MAXAMT");
            final Element maxPctBalNode = document.createElement("MAXPCTBAL");
            typeNode.appendChild(document.createTextNode("CACINFRESP"));

            if (reqFromP2P) {
                if (senderVO != null) {
                    regStatusNode.appendChild(document.createTextNode(senderVO.getRegistered()));
                    pinStatusNode.appendChild(document.createTextNode(senderVO.getPin()));
                } else {
                    regStatusNode.appendChild(document.createTextNode(BLANK));
                    pinStatusNode.appendChild(document.createTextNode(BLANK));
                }
                if (p_requestVO.isSuccessTxn()) {
                    txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
                } else {
                    txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
                }

                if (senderVO != null) {
                    minRemBalNode.appendChild(document.createTextNode(String.valueOf(PretupsBL.getSystemAmount(senderVO.getMinResidualBalanceAllowed()))));
                    minAmtNode.appendChild(document.createTextNode(String.valueOf(PretupsBL.getSystemAmount(senderVO.getMinTxnAmountAllowed()))));
                    maxAmtNode.appendChild(document.createTextNode(String.valueOf(PretupsBL.getSystemAmount(senderVO.getMaxTxnAmountAllowed()))));
                    maxPctBalNode.appendChild(document.createTextNode(String.valueOf(BTSLUtil.parseDoubleToLong( senderVO.getMaxPerTransferAllowed()))));
                } else {
                    minRemBalNode.appendChild(document.createTextNode(BLANK));
                    minAmtNode.appendChild(document.createTextNode(BLANK));
                    maxAmtNode.appendChild(document.createTextNode(BLANK));
                    maxPctBalNode.appendChild(document.createTextNode(BLANK));
                }
            } else // TO DO Needs to be changed for C2S
            {
                regStatusNode.appendChild(document.createTextNode(senderVO.getRegistered()));
                pinStatusNode.appendChild(document.createTextNode(senderVO.getPin()));

                if (p_requestVO.isSuccessTxn()) {
                    txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
                } else {
                    txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
                }

                minRemBalNode.appendChild(document.createTextNode(String.valueOf(senderVO.getMinResidualBalanceAllowed())));
                minAmtNode.appendChild(document.createTextNode(String.valueOf(senderVO.getMinTxnAmountAllowed())));
                maxAmtNode.appendChild(document.createTextNode(String.valueOf(senderVO.getMaxTxnAmountAllowed())));
                maxPctBalNode.appendChild(document.createTextNode(String.valueOf(senderVO.getMaxPerTransferAllowed())));
            }
            accountInfoGettingResponseTag.appendChild(typeNode);
            accountInfoGettingResponseTag.appendChild(regStatusNode);
            accountInfoGettingResponseTag.appendChild(pinStatusNode);
            accountInfoGettingResponseTag.appendChild(txnStatusNode);
            accountInfoGettingResponseTag.appendChild(minRemBalNode);
            accountInfoGettingResponseTag.appendChild(minAmtNode);
            accountInfoGettingResponseTag.appendChild(maxAmtNode);
            accountInfoGettingResponseTag.appendChild(maxPctBalNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            // just workaround
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (BTSLBaseException e) {
            LOG.error("generateGetAccountInfoResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateGetAccountInfoResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateGetAccountInfoResponse:" + e.getMessage());
        } catch (Exception e) {
            LOG.error("generateGetAccountInfoResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateGetAccountInfoResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateGetAccountInfoResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateGetAccountInfoResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method parse credit transfer request from XML String formating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    private static void parseCreditTransferRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseCreditTransferRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(requestStr.getBytes())) {
            // System.out.println("getUrlString()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+getUrlString());
            
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element creditTransferRequestTag = (Element) rootNodes.item(0);
            final Element msisdn1Tag = (Element) creditTransferRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element pinTag = (Element) creditTransferRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }

            // added by sanjay to set default pin
            if (BTSLUtil.isNullString(pin)) {
                // pin=PretupsI.DEFAULT_P2P_PIN;
                pin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN));
            }

            final Element msisdn2Tag = (Element) creditTransferRequestTag.getElementsByTagName("MSISDN2").item(0);
            final Text msisdn2TagNode = (Text) msisdn2Tag.getFirstChild();
            String msisdn2 = null;
            if (msisdn2TagNode == null) {
                msisdn2 = "";
            } else {
                msisdn2 = msisdn2TagNode.getData().trim();
            }
            final Element amountTag = (Element) creditTransferRequestTag.getElementsByTagName("AMOUNT").item(0);
            final Text amountTagNode = (Text) amountTag.getFirstChild();
            String amount = null;
            if (amountTagNode == null) {
                amount = "";
            } else {
                amount = amountTagNode.getData().trim();
            }
            final Element language1Tag = (Element) creditTransferRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;

            // added by sanjay 10/01/2006 - to set default language code
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("parseCreditTransferRequest", "language1:" + language1 + ":");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language1)) {
                language1 = "0";
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("parseCreditTransferRequest", "language1:" + language1 + ":");
            }

            final Element language2Tag = (Element) creditTransferRequestTag.getElementsByTagName("LANGUAGE2").item(0);
            final Text language2TagNode = (Text) language2Tag.getFirstChild();
            String language2 = null;

            // added by sanjay 10/01/2006 - to set default language code
            if (language2TagNode == null) {
                language2 = "";
            } else {
                language2 = language2TagNode.getData().trim();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("parseCreditTransferRequest", "language2:" + language2 + ":");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = Constants.getProperty("SMS_USSD_DEFAULT_LANGUAGE");
            }

            if (BTSLUtil.isNullString(language2)) {
                language2 = "0";
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseCreditTransferRequest", "language2:" + language2 + ":");
            }

            final Element selectorTag = (Element) creditTransferRequestTag.getElementsByTagName("SELECTOR").item(0);
            final Text selectorTagNode = (Text) selectorTag.getFirstChild();
            String selector = null;
            if (selectorTagNode == null) {
                selector = "" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_TRANSFER_DEF_SELECTOR_CODE));
            } else {
                selector = selectorTagNode.getData().trim();
            }
            // parsedRequestStr=PretupsI.SERVICE_TYPE_P2PRECHARGE+P2P_MESSAGE_SEP+msisdn2+P2P_MESSAGE_SEP+amount+P2P_MESSAGE_SEP+language2+P2P_MESSAGE_SEP+selector+P2P_MESSAGE_SEP+pin;
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2PRECHARGE + P2P_MESSAGE_SEP + msisdn2 + P2P_MESSAGE_SEP + amount + P2P_MESSAGE_SEP + selector + P2P_MESSAGE_SEP + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReceiverLocale(LocaleMasterCache.getLocaleFromCodeDetails(language2));

            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);

        } catch (IOException e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseCreditTransferRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method construct credit transfer response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    private static void generateCreditTransferResponse(RequestVO p_requestVO) throws Exception {
        String responseStr = null;
        try(ByteArrayInputStream byteArrayInputStream= new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())) {
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element creditTransferResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnIdNode = document.createElement("TXNID");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            typeNode.appendChild(document.createTextNode("CCTRFRESP"));
            txnIdNode.appendChild(document.createTextNode(p_requestVO.getTransactionID()));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            creditTransferResponseTag.appendChild(typeNode);
            creditTransferResponseTag.appendChild(txnIdNode);
            creditTransferResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.error("generateCreditTransferResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateCreditTransferResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateCreditTransferResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateCreditTransferResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method parse change pin request from XML String and fromating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    private static void parseChangePinRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChangePinRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestStr.getBytes())) {
           
            
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element changePinRequestTag = (Element) rootNodes.item(0);
            final Element msisdn1Tag = (Element) changePinRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element pinTag = (Element) changePinRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }
            final Element newPinTag = (Element) changePinRequestTag.getElementsByTagName("NEWPIN").item(0);
            final Text newPinTagNode = (Text) newPinTag.getFirstChild();
            String newPin = null;
            if (newPinTagNode == null) {
                newPin = "";
            } else {
                newPin = newPinTagNode.getData().trim();
            }
            final Element confirmPinTag = (Element) changePinRequestTag.getElementsByTagName("CONFIRMPIN").item(0);
            final Text confirmPinTagNode = (Text) confirmPinTag.getFirstChild();
            String confirmPin = null;
            if (confirmPinTagNode == null) {
                confirmPin = "";
            } else {
                confirmPin = confirmPinTagNode.getData().trim();
            }
            final Element language1Tag = (Element) changePinRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }
            // parsedRequestStr=type+" "+msisdn1+" "+pin+" "+newPin+" "+confirmPin+" "+language1;
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2PCHANGEPIN + P2P_MESSAGE_SEP + pin + P2P_MESSAGE_SEP + newPin + P2P_MESSAGE_SEP + confirmPin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (IOException e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChangePinRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChangePinRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChangePinRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method connstruct credit transfer response in XML format from
     * requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    private static void generateChangePinResponse(RequestVO p_requestVO) throws Exception {
        String responseStr = null;
        try( ByteArrayInputStream  byteArrayInputStream=new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())) {
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element changePinResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            typeNode.appendChild(document.createTextNode("CCPNRESP"));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            changePinResponseTag.appendChild(typeNode);
            changePinResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.error("generateChangePinResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateChangePinResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateChangePinResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateChangePinResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method parse registration request from XML String and formating into
     * white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    private static void parseNotificationLanguageRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseNotificationLanguageRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(requestStr.getBytes())) {
            
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element registrationRequestTag = (Element) rootNodes.item(0);
            final Element typeTag = (Element) registrationRequestTag.getElementsByTagName("TYPE").item(0);
            final Text typeTagNode = (Text) typeTag.getFirstChild();
            String type = null;
            if (typeTagNode == null) {
                type = "";
            } else {
                type = typeTagNode.getData().trim();
            }
            final Element msisdn1Tag = (Element) registrationRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element pinTag = (Element) registrationRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }
            final Element language1Tag = (Element) registrationRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }
            // parsedRequestStr =type+" "+msisdn1+" "+pin+" "+language1;
            parsedRequestStr = PretupsI.SERVICE_TYPE_LANG_NOTIFICATION + P2P_MESSAGE_SEP + language1 + P2P_MESSAGE_SEP + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (IOException e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseNotificationLanguageRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseNotificationLanguageRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseNotificationLanguageRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method connstruct registration response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    private static void generateNotificationLanguageResponse(RequestVO p_requestVO) throws Exception {
        String responseStr = null;
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())){
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element registrationResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            typeNode.appendChild(document.createTextNode("CCLANGRESP"));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            registrationResponseTag.appendChild(typeNode);
            registrationResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.error("generateNotificationLanguageResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateNotificationLanguageResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateNotificationLanguageResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateNotificationLanguageResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method parse History Message Request request from XML String and
     * formating into white space seperated String
     * 
     * @param p_requestVO
     *            RequestVO
     */
    private static void parseHistoryMessageRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseHistoryMessageRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(requestStr.getBytes())) {
            
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element registrationRequestTag = (Element) rootNodes.item(0);
            final Element typeTag = (Element) registrationRequestTag.getElementsByTagName("TYPE").item(0);
            final Text typeTagNode = (Text) typeTag.getFirstChild();
            String type = null;
            if (typeTagNode == null) {
                type = "";
            } else {
                type = typeTagNode.getData().trim();
            }
            final Element msisdn1Tag = (Element) registrationRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element pinTag = (Element) registrationRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }
            final Element language1Tag = (Element) registrationRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }
            // parsedRequestStr =type+" "+msisdn1+" "+pin+" "+language1;
            parsedRequestStr = PretupsI.SERVICE_TYPE_P2P_HISTORY + P2P_MESSAGE_SEP + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (IOException e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseHistoryMessageRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseHistoryMessageRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseGetAccountInfoRequest", PretupsErrorCodesI.P2P_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseHistoryMessageRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    /**
     * this method connstruct registration response in XML format from requestVO
     * 
     * @param p_requestVO
     *            RequestVO
     * @return responseStr java.lang.String
     */
    private static void generateHistoryMessageResponse(RequestVO p_requestVO) throws Exception {
        String responseStr = null;
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())) {
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element registrationResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            typeNode.appendChild(document.createTextNode("CCHISRESP"));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            registrationResponseTag.appendChild(typeNode);
            registrationResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.error("generateHistoryMessageResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateHistoryMessageResponse]",
                PretupsErrorCodesI.P2P_ERROR_EXCEPTION, "", "", "generateHistoryMessageResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateHistoryMessageResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    /**
     * Output the specified DOM Node object, printing it using the specified
     * indentation string
     **/
    protected static String write(Node node, StringBuffer strBuff, String indent) {
        // The output depends on the type of the node
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
                { // If its a Document node
                    final Document doc = (Document) node;
                    strBuff.append(indent + "<?xml version='1.0'?>"); // Output
                    // header
                    Node child = doc.getFirstChild(); // Get the first node
                    while (child != null) { // Loop 'till no more nodes
                        write(child, strBuff, indent); // Output node
                        child = child.getNextSibling(); // Get next node
                    }
                    break;
                }
            case Node.DOCUMENT_TYPE_NODE:
                { // It is a <!DOCTYPE> tag
                    final DocumentType doctype = (DocumentType) node;
                    // Note that the DOM Level 1 does not give us information
                    // about
                    // the the public or system ids of the doctype, so we can't
                    // output
                    // a complete <!DOCTYPE> tag here. We can do better with
                    // Level 2.
                    strBuff.append("<!DOCTYPE " + doctype.getName() + ">");
                    break;
                }
            case Node.ELEMENT_NODE:
                { // Most nodes are Elements
                    final Element elt = (Element) node;
                    strBuff.append(indent + "<" + elt.getTagName()); // Begin
                    // start
                    // tag
                    final NamedNodeMap attrs = elt.getAttributes(); // Get
                    // attributes
                    int attrsLength = attrs.getLength();
                    for (int i = 0; i < attrsLength; i++) { // Loop
                        // through
                        // them
                        final Node a = attrs.item(i);
                        strBuff.append(" " + a.getNodeName() + "='" + // Print
                        // attr.
                        // name
                        fixup(a.getNodeValue()) + "'"); // Print attr. value
                    }
                    strBuff.append(">"); // Finish start tag

                    final String newindent = indent; // Increase indent
                    Node child = elt.getFirstChild(); // Get child
                    while (child != null) { // Loop
                        write(child, strBuff, newindent); // Output child
                        child = child.getNextSibling(); // Get next child
                    }

                    strBuff.append(indent + "</" + // Output end tag
                    elt.getTagName() + ">");
                    break;
                }
            case Node.TEXT_NODE:
                { // Plain text node
                    final Text textNode = (Text) node;

                    final String text = BTSLUtil.NullToString(textNode.getData()).trim();
                    if ((text != null) && text.length() > 0) {
                        strBuff.append(indent + fixup(text)); // print text
                    }
                    break;
                }
            case Node.PROCESSING_INSTRUCTION_NODE:
                { // Handle PI nodes
                    final ProcessingInstruction pi = (ProcessingInstruction) node;
                    strBuff.append(indent + "<?" + pi.getTarget() + " " + pi.getData() + "?>");
                    break;
                }
            case Node.ENTITY_REFERENCE_NODE:
                { // Handle entities
                    strBuff.append(indent + "&" + node.getNodeName() + ";");
                    break;
                }
            case Node.CDATA_SECTION_NODE:
                { // Output CDATA sections
                    final CDATASection cdata = (CDATASection) node;
                    // Careful! Don't put a CDATA section in the program itself!
                    strBuff.append(indent + "<" + "![CDATA[" + cdata.getData() + "]]" + ">");
                    break;
                }
            case Node.COMMENT_NODE:
                { // Comments
                    final Comment c = (Comment) node;
                    strBuff.append(indent + "<!--" + c.getData() + "-->");
                    break;
                }
            default: // Hopefully, this won't happen too much!
                LOG.debug("write", "Ignoring node: " + node.getClass().getName());
                break;
        }
        return strBuff.toString();
    }

    // This method replaces reserved characters with entities.
    protected static String fixup(String s) {
        final StringBuffer sb = new StringBuffer();
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            final char c = s.charAt(i);
            switch (c) {
                default:
                    sb.append(c);
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
            }
        }
        return sb.toString();
    }

    private static String getUrlString() {
        final Iterator iterator = LoadControllerCache.getInstanceLoadHash().keySet().iterator();
        final InstanceLoadVO instanceLoadVO = (InstanceLoadVO) LoadControllerCache.getInstanceLoadHash().get(iterator.next());
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("http://");
        stringBuffer.append(instanceLoadVO.getHostAddress());
        stringBuffer.append(":");
        stringBuffer.append(instanceLoadVO.getHostPort());
        stringBuffer.append("/pretups/");

        return stringBuffer.toString();
    }

    /**
     * Generate Fail message
     * 
     * @param p_requestVO
     * @throws Exception
     */
    public static void generateFailureResponse(RequestVO p_requestVO) throws BTSLBaseException {
        String responseStr = null;
        final String METHOD_NAME = "generateFailureResponse";
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())) {
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element responseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            responseTag.appendChild(typeNode);
            responseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.error("generateFailureResponse", "Exception e: " + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("XMLAPIParser", METHOD_NAME, "Exception in generating Failure Response");
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateFailureResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    /**
     * this method parse the request from XML String and formating into white
     * space seperated String
     * 
     * @param action
     *            int
     * @param requestStr
     *            java.lang.String
     */
    public static void parseChannelRequest(int action, RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "parseChannelRequest";
    	StringBuilder sb = new StringBuilder(1024);
        switch (action) {
        /*
         * case ACTION_CHNL_ACCOUNT_INFO:
         * {
         * parseGetAccountInfoRequest(p_requestVO);
         * break;
         * }
         */
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    parseChannelCreditTransferRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    parseChannelChangePinRequest(p_requestVO);
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    parseChannelNotificationLanguageRequest(p_requestVO);
                    break;
                }
            /*
             * case ACTION_CHNL_HISTORY_MESSAGE:
             * {
             * parseHistoryMessageRequest(p_requestVO);
             * break;
             * }
             */
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    parseChannelTransferRequest(p_requestVO, ACTION_CHNL_TRANSFER_MESSAGE);
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    parseChannelTransferRequest(p_requestVO, ACTION_CHNL_WITHDRAW_MESSAGE);
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    parseChannelTransferRequest(p_requestVO, ACTION_CHNL_RETURN_MESSAGE);
                    break;
                }
            default :
            {
            	sb.append("Defualt : Request ID = ").append(p_requestVO.getRequestID());
            	sb.append("Action= ").append(action);
            	LogFactory.printLog(METHOD_NAME, sb, LOG);
            }
    
        }
    }

    /**
     * this method construct the response from HashMap into XML String
     * 
     * @param action
     *            int
     * @param p_requestVO
     *            RequestVO
     */
    public static void generateChannelResponse(int action, RequestVO p_requestVO) throws Exception {
    	final String METHOD_NAME = "generateChannelResponse";
    	StringBuilder sb = new StringBuilder(1024);
        switch (action) {
        /*
         * case ACTION_CHNL_ACCOUNT_INFO:
         * {
         * generateGetAccountInfoResponse(p_requestVO);
         * break;
         * }
         */
            case ACTION_CHNL_CREDIT_TRANSFER:
                {
                    generateChannelCreditTransferResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_CHANGE_PIN:
                {
                    generateChannelChangePinResponse(p_requestVO);
                    break;
                }
            case ACTION_CHNL_NOTIFICATION_LANGUAGE:
                {
                    generateChannelNotificationLanguageResponse(p_requestVO);
                    break;
                }
            /*
             * case ACTION_CHNL_HISTORY_MESSAGE:
             * {
             * generateHistoryMessageResponse(p_requestVO);
             * break;
             * }
             */
            case ACTION_CHNL_TRANSFER_MESSAGE:
                {
                    generateChannelTransferResponse(p_requestVO, ACTION_CHNL_TRANSFER_MESSAGE);
                    break;
                }
            case ACTION_CHNL_WITHDRAW_MESSAGE:
                {
                    generateChannelTransferResponse(p_requestVO, ACTION_CHNL_WITHDRAW_MESSAGE);
                    break;
                }
            case ACTION_CHNL_RETURN_MESSAGE:
                {
                    generateChannelTransferResponse(p_requestVO, ACTION_CHNL_RETURN_MESSAGE);
                    break;
                }
            default :
            {
            	sb.append("Defualt : Request ID = ").append(p_requestVO.getRequestID());
            	sb.append("Action= ").append(action);
            	LogFactory.printLog(METHOD_NAME, sb, LOG);
            }
        }
    }

    private static void parseChannelCreditTransferRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelCreditTransferRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseChannelCreditTransferRequest";
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestStr.getBytes())) {
            
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element creditTransferRequestTag = (Element) rootNodes.item(0);
            final Element typeTag = (Element) creditTransferRequestTag.getElementsByTagName("TYPE").item(0);
            final Text typeTagNode = (Text) typeTag.getFirstChild();
            String type = null;
            if (typeTagNode == null) {
                type = "";
            } else {
                type = typeTagNode.getData().trim();
            }
            final Element msisdn1Tag = (Element) creditTransferRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element pinTag = (Element) creditTransferRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }
            final Element msisdn2Tag = (Element) creditTransferRequestTag.getElementsByTagName("MSISDN2").item(0);
            final Text msisdn2TagNode = (Text) msisdn2Tag.getFirstChild();
            String msisdn2 = null;
            if (msisdn2TagNode == null) {
                msisdn2 = "";
            } else {
                msisdn2 = msisdn2TagNode.getData().trim();
            }
            final Element amountTag = (Element) creditTransferRequestTag.getElementsByTagName("AMOUNT").item(0);
            final Text amountTagNode = (Text) amountTag.getFirstChild();
            String amount = null;
            if (amountTagNode == null) {
                amount = "";
            } else {
                amount = amountTagNode.getData().trim();
            }
            final Element language1Tag = (Element) creditTransferRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }
            final Element language2Tag = (Element) creditTransferRequestTag.getElementsByTagName("LANGUAGE2").item(0);
            final Text language2TagNode = (Text) language2Tag.getFirstChild();
            String language2 = null;
            if (language2TagNode == null) {
                language2 = "";
            } else {
                language2 = language2TagNode.getData().trim();
            }
            final Element selectorTag = (Element) creditTransferRequestTag.getElementsByTagName("SELECTOR").item(0);
            final Text selectorTagNode = (Text) selectorTag.getFirstChild();
            String selector = null;
            if (selectorTagNode == null) {
                selector = "" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_TRANSFER_DEF_SELECTOR_CODE));
            } else {
                selector = selectorTagNode.getData().trim();
            }
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RECHARGE + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + selector + CHNL_MESSAGE_SEP + language2 + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setReqSelector(selector);
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (IOException e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelCreditTransferRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelCreditTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelCreditTransferRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelCreditTransferRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    private static void generateChannelCreditTransferResponse(RequestVO p_requestVO) throws Exception {
        String responseStr = null;
        final String METHOD_NAME = "generateChannelCreditTransferResponse";
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())) {
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element creditTransferResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnIdNode = document.createElement("TXNID");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            typeNode.appendChild(document.createTextNode("RCTRFRESP"));
            txnIdNode.appendChild(document.createTextNode(p_requestVO.getTransactionID()));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            creditTransferResponseTag.appendChild(typeNode);
            creditTransferResponseTag.appendChild(txnIdNode);
            creditTransferResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("generateCreditTransferResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateCreditTransferResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateCreditTransferResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateCreditTransferResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    private static void parseChannelChangePinRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelChangePinRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseChannelChangePinRequest";
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(requestStr.getBytes())) {
            
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element changePinRequestTag = (Element) rootNodes.item(0);
            final Element typeTag = (Element) changePinRequestTag.getElementsByTagName("TYPE").item(0);
            final Text typeTagNode = (Text) typeTag.getFirstChild();
            String type = null;
            if (typeTagNode == null) {
                type = "";
            } else {
                type = typeTagNode.getData().trim();
            }
            final Element msisdn1Tag = (Element) changePinRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element pinTag = (Element) changePinRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }
            final Element newPinTag = (Element) changePinRequestTag.getElementsByTagName("NEWPIN").item(0);
            final Text newPinTagNode = (Text) newPinTag.getFirstChild();
            String newPin = null;
            if (newPinTagNode == null) {
                newPin = "";
            } else {
                newPin = newPinTagNode.getData().trim();
            }
            final Element confirmPinTag = (Element) changePinRequestTag.getElementsByTagName("CONFIRMPIN").item(0);
            final Text confirmPinTagNode = (Text) confirmPinTag.getFirstChild();
            String confirmPin = null;
            if (confirmPinTagNode == null) {
                confirmPin = "";
            } else {
                confirmPin = confirmPinTagNode.getData().trim();
            }
            final Element language1Tag = (Element) changePinRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }
            // parsedRequestStr=type+" "+msisdn1+" "+pin+" "+newPin+" "+confirmPin+" "+language1;
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN + CHNL_MESSAGE_SEP + pin + CHNL_MESSAGE_SEP + newPin + CHNL_MESSAGE_SEP + confirmPin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (IOException e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelChangePinRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelChangePinRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelChangePinRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelChangePinRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelChangePinRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    private static void generateChannelChangePinResponse(RequestVO p_requestVO) throws Exception {
        String responseStr = null;
        final String METHOD_NAME = "generateChannelChangePinResponse";
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())) {
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element changePinResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            typeNode.appendChild(document.createTextNode("RCPNRESP"));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            changePinResponseTag.appendChild(typeNode);
            changePinResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("generateChannelChangePinResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateChannelChangePinResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelChangePinResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateChannelChangePinResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    private static void parseChannelNotificationLanguageRequest(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelNotificationLanguageRequest", "Entered p_requestVO: " + p_requestVO.toString());
        }
        final String METHOD_NAME = "parseChannelNotificationLanguageRequest";
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(requestStr.getBytes())) {
           
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element registrationRequestTag = (Element) rootNodes.item(0);
            final Element typeTag = (Element) registrationRequestTag.getElementsByTagName("TYPE").item(0);
            final Text typeTagNode = (Text) typeTag.getFirstChild();
            String type = null;
            if (typeTagNode == null) {
                type = "";
            } else {
                type = typeTagNode.getData().trim();
            }
            final Element msisdn1Tag = (Element) registrationRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }
            final Element pinTag = (Element) registrationRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }
            final Element language1Tag = (Element) registrationRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }
            // parsedRequestStr =type+" "+msisdn1+" "+pin+" "+language1;
            parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_LANG_NOTIFICATION + CHNL_MESSAGE_SEP + language1 + CHNL_MESSAGE_SEP + pin;
            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (IOException e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelNotificationLanguageRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelNotificationLanguageRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelNotificationLanguageRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelNotificationLanguageRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelNotificationLanguageRequest", "Exiting p_requestVO: " + p_requestVO.toString());
            }
        }
    }

    private static void generateChannelNotificationLanguageResponse(RequestVO p_requestVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateChannelNotificationLanguageResponse", "Entered p_requestID: " + p_requestVO.getRequestIDStr());
        }
        final String METHOD_NAME = "generateChannelNotificationLanguageResponse";
        String responseStr = null;
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())) {
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element registrationResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            typeNode.appendChild(document.createTextNode("RCLANGRESP"));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            registrationResponseTag.appendChild(typeNode);
            registrationResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("generateChannelNotificationLanguageResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "XMLAPIParser[generateChannelNotificationLanguageResponse]", PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "",
                "generateChannelNotificationLanguageResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateChannelNotificationLanguageResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

    private static void parseChannelTransferRequest(RequestVO p_requestVO, int p_action) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("parseChannelTransferRequest", "Entered p_requestVO: " + p_requestVO.toString() + " p_action=" + p_action);
        }
        final String METHOD_NAME = "parseChannelTransferRequest";
        String parsedRequestStr = null;
        final String accountInfoStr = p_requestVO.getRequestMessage();
        final String requestStr = accountInfoStr.replaceAll("xml/command.dtd", getUrlString() + "xml/command.dtd");
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestStr.getBytes())) {
            
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element creditTransferRequestTag = (Element) rootNodes.item(0);
            final Element typeTag = (Element) creditTransferRequestTag.getElementsByTagName("TYPE").item(0);
            final Text typeTagNode = (Text) typeTag.getFirstChild();
            String type = null;
            if (typeTagNode == null) {
                type = "";
            } else {
                type = typeTagNode.getData().trim();
            }

            final Element msisdn1Tag = (Element) creditTransferRequestTag.getElementsByTagName("MSISDN1").item(0);
            final Text msisdn1TagNode = (Text) msisdn1Tag.getFirstChild();
            String msisdn1 = null;
            if (msisdn1TagNode == null) {
                msisdn1 = "";
            } else {
                msisdn1 = msisdn1TagNode.getData().trim();
            }

            final Element msisdn2Tag = (Element) creditTransferRequestTag.getElementsByTagName("MSISDN2").item(0);
            final Text msisdn2TagNode = (Text) msisdn2Tag.getFirstChild();
            String msisdn2 = null;
            if (msisdn2TagNode == null) {
                msisdn2 = "";
            } else {
                msisdn2 = msisdn2TagNode.getData().trim();
            }

            final Element amountTag = (Element) creditTransferRequestTag.getElementsByTagName("TOPUPVALUE").item(0);
            final Text amountTagNode = (Text) amountTag.getFirstChild();
            String amount = null;
            if (amountTagNode == null) {
                amount = "";
            } else {
                amount = amountTagNode.getData().trim();
            }

            final Element language1Tag = (Element) creditTransferRequestTag.getElementsByTagName("LANGUAGE1").item(0);
            final Text language1TagNode = (Text) language1Tag.getFirstChild();
            String language1 = null;
            if (language1TagNode == null) {
                language1 = "";
            } else {
                language1 = language1TagNode.getData().trim();
            }
            final Element productCodeTag = (Element) creditTransferRequestTag.getElementsByTagName("PRODUCTCODE").item(0);
            final Text productCodeTagNode = (Text) productCodeTag.getFirstChild();
            String productCode = null;
            if (productCodeTagNode == null) {
                productCode = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_PRODUCT));
            } else {
                productCode = productCodeTagNode.getData().trim();
            }

            final Element pinTag = (Element) creditTransferRequestTag.getElementsByTagName("PIN").item(0);
            final Text pinTagNode = (Text) pinTag.getFirstChild();
            String pin = null;
            if (pinTagNode == null) {
                pin = "";
            } else {
                pin = pinTagNode.getData().trim();
            }
            if (p_action == ACTION_CHNL_TRANSFER_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_TRANSFER + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + productCode + CHNL_MESSAGE_SEP + pin;
            } else if (p_action == ACTION_CHNL_RETURN_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_RETURN + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + productCode + CHNL_MESSAGE_SEP + pin;
            } else if (p_action == ACTION_CHNL_WITHDRAW_MESSAGE) {
                parsedRequestStr = PretupsI.SERVICE_TYPE_CHNL_WITHDRAW + CHNL_MESSAGE_SEP + msisdn2 + CHNL_MESSAGE_SEP + amount + CHNL_MESSAGE_SEP + productCode + CHNL_MESSAGE_SEP + pin;
            }

            p_requestVO.setSenderLocale(LocaleMasterCache.getLocaleFromCodeDetails(language1));
            p_requestVO.setDecryptedMessage(parsedRequestStr);
            p_requestVO.setRequestMSISDN(msisdn1);
        } catch (IOException e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelTransferRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
            LOG.error("parseChannelTransferRequest", "Exception e: " + e);
            throw new BTSLBaseException("XMLAPIParser", "parseChannelTransferRequest", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("parseChannelTransferRequest", "Exiting p_requestVO ID: " + p_requestVO.getRequestIDStr());
            }
        }
    }

    private static void generateChannelTransferResponse(RequestVO p_requestVO, int p_action) throws Exception {
        String responseStr = null;
        final String METHOD_NAME = "generateChannelTransferResponse";
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><COMMAND></COMMAND>".getBytes())){
            final Document document = _parser.parse(byteArrayInputStream);
            final NodeList rootNodes = document.getElementsByTagName("COMMAND");
            final Element creditTransferResponseTag = (Element) rootNodes.item(0);
            final Element typeNode = document.createElement("TYPE");
            // Element txnIdNode = document.createElement("TXNID");
            final Element txnStatusNode = document.createElement("TXNSTATUS");
            if (p_action == ACTION_CHNL_TRANSFER_MESSAGE) {
                typeNode.appendChild(document.createTextNode("TRFRESP"));
            } else if (p_action == ACTION_CHNL_RETURN_MESSAGE) {
                typeNode.appendChild(document.createTextNode("RETRESP"));
            } else if (p_action == ACTION_CHNL_WITHDRAW_MESSAGE) {
                typeNode.appendChild(document.createTextNode("WDTHRESP"));
            }

            // txnIdNode.appendChild(document.createTextNode(p_requestVO.getTransactionID()));
            if (p_requestVO.isSuccessTxn()) {
                txnStatusNode.appendChild(document.createTextNode(PretupsI.TXN_STATUS_SUCCESS));
            } else {
                txnStatusNode.appendChild(document.createTextNode(p_requestVO.getMessageCode()));
            }
            creditTransferResponseTag.appendChild(typeNode);
            // creditTransferResponseTag.appendChild(txnIdNode);
            creditTransferResponseTag.appendChild(txnStatusNode);
            final StringBuffer strBuff = new StringBuffer();
            responseStr = write(document, strBuff, "");
            responseStr = responseStr.replaceAll("<COMMAND>", "<!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\"><COMMAND>");
            p_requestVO.setSenderReturnMessage(responseStr);
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("generateChannelTransferResponse", "Exception e: " + e);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLAPIParser[generateChannelTransferResponse]",
                PretupsErrorCodesI.C2S_ERROR_EXCEPTION, "", "", "generateChannelTransferResponse:" + e.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("generateChannelTransferResponse", "Entered responseStr: " + responseStr);
            }
        }
    }

}
