package com.inter.alcatel10;

/**
 * @(#)AlcatelRequestResponse.java
 *                                 Copyright(c) 2005, Bharti Telesoft Int.
 *                                 Public Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Gurjeet Singh Bedi Oct 17,2005 Initial
 *                                 Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 Request Response Formatting class for
 *                                 interface
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.util.XMLRequestResponse;

public class AlcatelTestXml extends XMLRequestResponse {
    public Log _log = LogFactory.getLog(this.getClass().getName());

    protected HashMap parseResponse(int action, String responseStr) throws Exception {
        return null;
    }

    protected String generateRequest(int action, HashMap map) throws Exception {
        return null;
    }

    public String constructStrFromAccInfoMapRequest(int action, HashMap map) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("constructStrFromAccInfoMapRequest", "Entered:cp_id=" + map.get("cp_id") + "transaction id=" + map.get("IN_TXN_ID") + "op transaction id=" + map.get("op_transaction_id") + "result=" + map.get("result") + " credit balance:" + map.get("credit_balance") + " end value date: " + map.get("end_val_date") + " end inactive date: " + map.get("end_inact_date") + " profile: " + map.get("profile") + "profile lang=" + map.get("prof_lang") + "acc status=" + map.get("acc_status") + "lock_information=" + map.get("lock_information") + "service_type=" + map.get("service_type"));
        String requestStr = null;
        try {
            // Document document = _parser.parse(new
            // ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE cp_reply PUBLIC '-//Ocam//DTD XML cp_request 1.0//EN' 'cp_reply.dtd'><cp_reply></cp_reply>".getBytes()));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cp_reply.dtd");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DocumentType svgDOCTYPE = impl.createDocumentType("cp_reply", "", "");
            Document document = impl.createDocument(null, "cp_reply", svgDOCTYPE);
            NodeList rootNodes = document.getElementsByTagName("cp_reply");
            Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            Element cpIdNode = document.createElement("cp_id");
            Element transactionIdNode = document.createElement("cp_transaction_id");
            Element opTransactionIdNode = document.createElement("op_transaction_id");
            Element resultIdNode = document.createElement("result");
            Element creditBalanceIdNode = document.createElement("credit_balance");
            Element endValDateNode = document.createElement("end_val_date");
            Element endInactDateNode = document.createElement("end_inact_date");
            Element profileIdNode = document.createElement("profile");
            Element profLangIdNode = document.createElement("prof_lang");
            Element accStatusIdNode = document.createElement("acc_status");
            Element lockInformationIdNode = document.createElement("lock_information");
            Element serviceTypeIdNode = document.createElement("service_type");

            cpIdNode.appendChild(document.createTextNode((String) map.get("cp_id")));
            transactionIdNode.appendChild(document.createTextNode((String) map.get("IN_TXN_ID")));
            endValDateNode.appendChild(document.createTextNode((String) map.get("end_val_date")));
            endInactDateNode.appendChild(document.createTextNode((String) map.get("end_inact_date")));
            opTransactionIdNode.appendChild(document.createTextNode((String) map.get("op_transaction_id")));
            resultIdNode.appendChild(document.createTextNode((String) map.get("result")));
            creditBalanceIdNode.appendChild(document.createTextNode((String) map.get("credit_balance")));
            profileIdNode.appendChild(document.createTextNode((String) map.get("profile")));
            profLangIdNode.appendChild(document.createTextNode((String) map.get("prof_lang")));
            accStatusIdNode.appendChild(document.createTextNode((String) map.get("acc_status")));
            lockInformationIdNode.appendChild(document.createTextNode((String) map.get("lock_information")));
            serviceTypeIdNode.appendChild(document.createTextNode((String) map.get("service_type")));
            accountInfoGettingRequestTag.appendChild(cpIdNode);
            accountInfoGettingRequestTag.appendChild(transactionIdNode);
            accountInfoGettingRequestTag.appendChild(opTransactionIdNode);
            accountInfoGettingRequestTag.appendChild(resultIdNode);
            accountInfoGettingRequestTag.appendChild(creditBalanceIdNode);
            accountInfoGettingRequestTag.appendChild(endValDateNode);
            accountInfoGettingRequestTag.appendChild(endInactDateNode);
            accountInfoGettingRequestTag.appendChild(profileIdNode);
            accountInfoGettingRequestTag.appendChild(profLangIdNode);
            accountInfoGettingRequestTag.appendChild(accStatusIdNode);
            accountInfoGettingRequestTag.appendChild(lockInformationIdNode);
            accountInfoGettingRequestTag.appendChild(serviceTypeIdNode);
            Source input = new DOMSource(document);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(input, new StreamResult(out));
            requestStr = out.toString();
            requestStr = requestStr.replaceAll("xml version=\"1.0\" encoding=\"UTF-8\"", "xml version=\"1.0\"");
            if (_log.isDebugEnabled())
                _log.debug("constructStrFromAccInfoMapRequest", "Got the XML on testxml  String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("constructStrFromAccInfoMapRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("constructStrFromAccInfoMapRequest", "Entered requestStr: " + requestStr);
        }
    }

    public String constructStrFromImeDebitMapRequest(int action, HashMap map) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("constructStrFromImeDebitMapRequest", "Entered:cp_id=" + map.get("cp_id") + "transaction id=" + map.get("IN_TXN_ID") + "op transaction id=" + map.get("op_transaction_id") + "result=" + map.get("result") + " service_type=" + map.get("service_type"));
        String requestStr = null;
        try {
            // Document document = _parser.parse(new
            // ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><cp_reply></cp_reply>".getBytes()));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cp_reply.dtd");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DocumentType svgDOCTYPE = impl.createDocumentType("cp_reply", "", "");
            Document document = impl.createDocument(null, "cp_reply", svgDOCTYPE);
            NodeList rootNodes = document.getElementsByTagName("cp_reply");
            Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            Element cpIdNode = document.createElement("cp_id");
            Element transactionIdNode = document.createElement("cp_transaction_id");
            // Element opTransactionIdNode =
            // document.createElement("op_transaction_id");
            Element resultIdNode = document.createElement("result");
            Element serviceTypeIdNode = document.createElement("service_type");

            cpIdNode.appendChild(document.createTextNode((String) map.get("cp_id")));
            transactionIdNode.appendChild(document.createTextNode((String) map.get("IN_TXN_ID")));

            // opTransactionIdNode.appendChild(document.createTextNode((String)map.get("op_transaction_id")));
            resultIdNode.appendChild(document.createTextNode((String) map.get("result")));

            serviceTypeIdNode.appendChild(document.createTextNode((String) map.get("service_type")));
            accountInfoGettingRequestTag.appendChild(cpIdNode);
            accountInfoGettingRequestTag.appendChild(transactionIdNode);
            // accountInfoGettingRequestTag.appendChild(opTransactionIdNode);
            accountInfoGettingRequestTag.appendChild(resultIdNode);

            accountInfoGettingRequestTag.appendChild(serviceTypeIdNode);
            Source input = new DOMSource(document);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(input, new StreamResult(out));
            requestStr = out.toString();
            requestStr = requestStr.replaceAll("xml version=\"1.0\" encoding=\"UTF-8\"", "xml version=\"1.0\"");
            /*
             * StringBuffer strBuff=new StringBuffer();
             * requestStr= write(document,strBuff,"");
             * requestStr=requestStr.replaceAll("xml version=\"1.0\"",
             * "xml version=\"1.0\"<!DOCTYPE cp_reply SYSTEM \"cp_reply.dtd\">"
             * );
             */
            if (_log.isDebugEnabled())
                _log.debug("constructStrFromImeDebitMapRequest", "Got the XMLon testxml String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("constructStrFromImeDebitMapRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("constructStrFromImeDebitMapRequest", "Entered requestStr: " + requestStr);
        }
        // TODO Auto-generated method stub

    }

    public String constructStrFromRechargeCreditMapRequest(int action, HashMap map) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("constructStrFromRechargeCreditMapRequest", "Entered:cp_id=" + map.get("cp_id") + "transaction id=" + map.get("IN_TXN_ID") + "op transaction id=" + map.get("op_transaction_id") + "result=" + map.get("result") + " credit balance:" + map.get("credit_balance") + " end value date: " + map.get("end_val_date") + " end inactive date: " + map.get("end_inact_date") + " profile: " + map.get("profile") + "profile lang=" + map.get("prof_lang") + "acc status=" + map.get("acc_status") + "service_type=" + map.get("service_type"));
        String requestStr = null;
        try {
            // Document document = _parser.parse(new
            // ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><cp_reply></cp_reply>".getBytes()));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cp_reply.dtd");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DocumentType svgDOCTYPE = impl.createDocumentType("cp_reply", "", "");
            Document document = impl.createDocument(null, "cp_reply", svgDOCTYPE);
            NodeList rootNodes = document.getElementsByTagName("cp_reply");
            Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            Element cpIdNode = document.createElement("cp_id");
            Element transactionIdNode = document.createElement("cp_transaction_id");
            Element opTransactionIdNode = document.createElement("op_transaction_id");
            Element resultIdNode = document.createElement("result");
            Element creditBalanceIdNode = document.createElement("credit_balance");
            Element endValDateNode = document.createElement("end_val_date");
            Element endInactDateNode = document.createElement("end_inact_date");
            Element profileIdNode = document.createElement("profile");
            Element profLangIdNode = document.createElement("prof_lang");
            Element accStatusIdNode = document.createElement("acc_status");

            Element serviceTypeIdNode = document.createElement("service_type");

            cpIdNode.appendChild(document.createTextNode((String) map.get("cp_id")));
            transactionIdNode.appendChild(document.createTextNode((String) map.get("IN_TXN_ID")));
            endValDateNode.appendChild(document.createTextNode((String) map.get("end_val_date")));
            endInactDateNode.appendChild(document.createTextNode((String) map.get("end_inact_date")));
            opTransactionIdNode.appendChild(document.createTextNode((String) map.get("op_transaction_id")));
            resultIdNode.appendChild(document.createTextNode((String) map.get("result")));
            creditBalanceIdNode.appendChild(document.createTextNode((String) map.get("credit_balance")));
            profileIdNode.appendChild(document.createTextNode((String) map.get("profile")));
            profLangIdNode.appendChild(document.createTextNode((String) map.get("prof_lang")));
            accStatusIdNode.appendChild(document.createTextNode((String) map.get("acc_status")));

            serviceTypeIdNode.appendChild(document.createTextNode((String) map.get("service_type")));
            accountInfoGettingRequestTag.appendChild(cpIdNode);
            accountInfoGettingRequestTag.appendChild(transactionIdNode);
            accountInfoGettingRequestTag.appendChild(opTransactionIdNode);
            accountInfoGettingRequestTag.appendChild(resultIdNode);
            accountInfoGettingRequestTag.appendChild(creditBalanceIdNode);
            accountInfoGettingRequestTag.appendChild(endValDateNode);
            accountInfoGettingRequestTag.appendChild(endInactDateNode);
            accountInfoGettingRequestTag.appendChild(profileIdNode);
            accountInfoGettingRequestTag.appendChild(profLangIdNode);
            accountInfoGettingRequestTag.appendChild(accStatusIdNode);

            accountInfoGettingRequestTag.appendChild(serviceTypeIdNode);
            Source input = new DOMSource(document);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(input, new StreamResult(out));
            requestStr = out.toString();
            requestStr = requestStr.replaceAll("xml version=\"1.0\" encoding=\"UTF-8\"", "xml version=\"1.0\"");
            // requestStr=requestStr.replaceAll( "xml version=\"1.0\"",
            // "xml version=\"1.0\"<!DOCTYPE cp_reply SYSTEM \"cp_reply.dtd\">");
            if (_log.isDebugEnabled())
                _log.debug("constructStrFromRechargeCreditMapRequest", "Got the XMLon testxml String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("constructStrFromRechargeCreditMapRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("constructStrFromRechargeCreditMapRequest", "Entered requestStr: " + requestStr);
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public HashMap parseGetAccountInfoRequest(String requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoRequest", "Entered requestStr: " + requestStr);
        HashMap map = new HashMap();
        try {
            String requestStr2 = requestStr.replaceAll("cp_req_websvr.dtd", "http://172.16.1.121:5003/pretups/xml/cp_req_websvr.dtd");
            // String requestStr2="cp_req_websvr.dtd";
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoRequest", "Entered requestStr2: " + requestStr2);
            Document document = _parser.parse(new ByteArrayInputStream(requestStr2.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("cp_request");
            Element getAccountInfoRequestTag = (Element) rootNodes.item(0);
            Element cpIdTag = (Element) getAccountInfoRequestTag.getElementsByTagName("cp_id").item(0);
            if (((Text) cpIdTag.getFirstChild()) != null)
                map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
            else
                map.put("cp_id", null);

            Element cpTransactionIdTag = (Element) getAccountInfoRequestTag.getElementsByTagName("cp_transaction_id").item(0);
            if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
            else
                map.put("cp_transaction_id", null);

            Element opTransactionIdTag = (Element) getAccountInfoRequestTag.getElementsByTagName("op_transaction_id").item(0);
            if (((Text) opTransactionIdTag.getFirstChild()) != null)
                map.put("op_transaction_id", ((Text) opTransactionIdTag.getFirstChild()).getData());
            else
                map.put("op_transaction_id", null);

            Element applicationTag = (Element) getAccountInfoRequestTag.getElementsByTagName("application").item(0);
            if (((Text) applicationTag.getFirstChild()) != null)
                map.put("application", ((Text) applicationTag.getFirstChild()).getData());
            else
                map.put("application", null);

            Element actionTag = (Element) getAccountInfoRequestTag.getElementsByTagName("action").item(0);
            if (((Text) actionTag.getFirstChild()) != null)
                map.put("action", ((Text) actionTag.getFirstChild()).getData());
            else
                map.put("action", null);

            Element userIdTag = (Element) getAccountInfoRequestTag.getElementsByTagName("user_id").item(0);
            if (((Text) userIdTag.getFirstChild()) != null)
                map.put("user_id", ((Text) userIdTag.getFirstChild()).getData());
            else
                map.put("user_id", null);
            return map;

        } catch (Exception e) {
            _log.error("parseGetAccountInfoRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoRequest test xml", "Exiting map: " + map);
        }
    }

    public HashMap parseRechargeCreditRequest(String requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditRequest", "Entered requestStr: " + requestStr);
        HashMap map = new HashMap();
        try {

            // String requestStr2 =
            // requestStr.replaceAll("cp_req_websvr.dtd","http://172.16.1.109:4301/pretups/xml/cp_req_websvr.dtd");
            String requestStr2 = requestStr.replaceAll("cp_req_websvr.dtd", "http://172.16.1.121:5003/pretups/xml/cp_req_websvr.dtd");
            // String requestStr2="cp_req_websvr.dtd";
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditRequest", "Entered requestStr2: " + requestStr2);

            Document document = _parser.parse(new ByteArrayInputStream(requestStr2.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("cp_request");
            Element rechargeCreditRequestTag = (Element) rootNodes.item(0);
            Element cpIdTag = (Element) rechargeCreditRequestTag.getElementsByTagName("cp_id").item(0);
            if (((Text) cpIdTag.getFirstChild()) != null)
                map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
            else
                map.put("cp_id", null);

            Element cpTransactionIdTag = (Element) rechargeCreditRequestTag.getElementsByTagName("cp_transaction_id").item(0);
            if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
            else
                map.put("cp_transaction_id", null);

            Element opTransactionIdTag = (Element) rechargeCreditRequestTag.getElementsByTagName("op_transaction_id").item(0);
            if (((Text) opTransactionIdTag.getFirstChild()) != null)
                map.put("op_transaction_id", ((Text) opTransactionIdTag.getFirstChild()).getData());
            else
                map.put("op_transaction_id", null);

            Element applicationTag = (Element) rechargeCreditRequestTag.getElementsByTagName("application").item(0);
            if (((Text) applicationTag.getFirstChild()) != null)
                map.put("application", ((Text) applicationTag.getFirstChild()).getData());
            else
                map.put("application", null);

            Element actionTag = (Element) rechargeCreditRequestTag.getElementsByTagName("action").item(0);
            if (((Text) actionTag.getFirstChild()) != null)
                map.put("action", ((Text) actionTag.getFirstChild()).getData());
            else
                map.put("action", null);

            Element userIdTag = (Element) rechargeCreditRequestTag.getElementsByTagName("user_id").item(0);
            if (((Text) userIdTag.getFirstChild()) != null)
                map.put("user_id", ((Text) userIdTag.getFirstChild()).getData());
            else
                map.put("user_id", null);

            Element transactionPriceTag = (Element) rechargeCreditRequestTag.getElementsByTagName("transaction_price").item(0);
            if (((Text) transactionPriceTag.getFirstChild()) != null)
                map.put("transaction_price", ((Text) transactionPriceTag.getFirstChild()).getData());
            else
                map.put("transaction_price", null);

            Element transactionCurrencyTag = (Element) rechargeCreditRequestTag.getElementsByTagName("transaction_currency").item(0);
            if (((Text) transactionCurrencyTag.getFirstChild()) != null)
                map.put("transaction_currency", ((Text) transactionCurrencyTag.getFirstChild()).getData());
            else
                map.put("transaction_currency", null);

            Element userValidityTag = (Element) rechargeCreditRequestTag.getElementsByTagName("user_validity").item(0);
            if (((Text) userValidityTag.getFirstChild()) != null)
                map.put("user_validity", ((Text) userValidityTag.getFirstChild()).getData());
            else
                map.put("user_validity", null);

            Element userGraceTag = (Element) rechargeCreditRequestTag.getElementsByTagName("user_grace").item(0);
            if (((Text) userGraceTag.getFirstChild()) != null)
                map.put("user_grace", ((Text) userGraceTag.getFirstChild()).getData());
            else
                map.put("user_grace", null);
            return map;

        } catch (Exception e) {
            _log.error("parseRechargeCreditRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditRequest test xml", "Exiting map: " + map);
        }
    }

    public HashMap parseImmediateDebitRequest(String requestStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitRequest", "Entered requestStr: " + requestStr);
        HashMap map = new HashMap();
        try {
            String requestStr2 = requestStr.replaceAll("cp_req_websvr.dtd", "http://172.16.1.121:5003/pretups/xml/cp_req_websvr.dtd");
            // String requestStr2="cp_req_websvr.dtd";
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitRequest", "Entered requestStr2: " + requestStr2);

            Document document = _parser.parse(new ByteArrayInputStream(requestStr2.getBytes()));

            NodeList rootNodes = document.getElementsByTagName("cp_request");
            Element immediateDebitRequestTag = (Element) rootNodes.item(0);
            Element cpIdTag = (Element) immediateDebitRequestTag.getElementsByTagName("cp_id").item(0);
            if (((Text) cpIdTag.getFirstChild()) != null)
                map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
            else
                map.put("cp_id", null);

            Element cpTransactionIdTag = (Element) immediateDebitRequestTag.getElementsByTagName("cp_transaction_id").item(0);
            if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
            else
                map.put("cp_transaction_id", null);

            Element opTransactionIdTag = (Element) immediateDebitRequestTag.getElementsByTagName("op_transaction_id").item(0);
            if (((Text) opTransactionIdTag.getFirstChild()) != null)
                map.put("op_transaction_id", ((Text) opTransactionIdTag.getFirstChild()).getData());
            else
                map.put("op_transaction_id", null);

            Element applicationTag = (Element) immediateDebitRequestTag.getElementsByTagName("application").item(0);
            if (((Text) applicationTag.getFirstChild()) != null)
                map.put("application", ((Text) applicationTag.getFirstChild()).getData());
            else
                map.put("application", null);

            Element actionTag = (Element) immediateDebitRequestTag.getElementsByTagName("action").item(0);
            if (((Text) actionTag.getFirstChild()) != null)
                map.put("action", ((Text) actionTag.getFirstChild()).getData());
            else
                map.put("action", null);

            Element userIdTag = (Element) immediateDebitRequestTag.getElementsByTagName("user_id").item(0);
            if (((Text) userIdTag.getFirstChild()) != null)
                map.put("user_id", ((Text) userIdTag.getFirstChild()).getData());
            else
                map.put("user_id", null);

            Element transactionPriceTag = (Element) immediateDebitRequestTag.getElementsByTagName("transaction_price").item(0);
            if (((Text) transactionPriceTag.getFirstChild()) != null)
                map.put("transaction_price", ((Text) transactionPriceTag.getFirstChild()).getData());
            else
                map.put("transaction_price", null);

            Element transactionCurrencyTag = (Element) immediateDebitRequestTag.getElementsByTagName("transaction_currency").item(0);
            if (((Text) transactionCurrencyTag.getFirstChild()) != null)
                map.put("transaction_currency", ((Text) transactionCurrencyTag.getFirstChild()).getData());
            else
                map.put("transaction_currency", null);

            return map;

        } catch (Exception e) {
            _log.error("parseImmediateDebitRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitRequeston test xml", "Exiting map: " + map);
        }
    }
}