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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.util.XMLRequestResponse;

public class AlcatelRequestResponse extends XMLRequestResponse {
    public Log _log = LogFactory.getLog(this.getClass().getName());

    protected HashMap parseResponse(int action, String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered Response String:  " + responseStr);
        HashMap map = null;
        switch (action) {
        case AlcatelI.ACTION_ACCOUNT_INFO: {
            map = parseGetAccountInfoResponse(responseStr);
            break;
        }
        case AlcatelI.ACTION_RECHARGE_CREDIT: {
            map = parseRechargeCreditResponse(responseStr);
            break;
        }
        case AlcatelI.ACTION_IMMEDIATE_DEBIT: {
            map = parseImmediateDebitResponse(responseStr);
            break;
        }

        }
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Exiting map: " + map);
        return map;
    }

    protected String generateRequest(int action, HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered map: " + map);
        String str = null;
        map.put("action", String.valueOf(action));
        switch (action) {
        case AlcatelI.ACTION_ACCOUNT_INFO: {
            str = generateGetAccountInfoRequest(map);
            break;
        }
        case AlcatelI.ACTION_RECHARGE_CREDIT: {
            str = generateRechargeCreditRequest(map);
            break;
        }
        case AlcatelI.ACTION_IMMEDIATE_DEBIT: {
            str = generateImmediateDebitRequest(map);
            break;
        }
        }
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Exited Request String:  " + str);
        return str;
    }

    /**
     * this method Generate Recharge Credit Request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */

    private String generateRechargeCreditRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered MSISDN=" + map.get("MSISDN") + "transactionId: " + map.get("IN_TXN_ID") + " Content Provider ID:" + map.get("cp_id") + " application: " + map.get("application") + " action: " + map.get("action") + " transaction price: " + map.get("INTERFACE_AMOUNT") + "user validity:" + map.get("VALIDITY_DAYS") + "user grace:" + map.get("GRACE_DAYS"));
        String requestStr = null;
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cp_req_websvr.dtd");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DocumentType svgDOCTYPE = impl.createDocumentType("cp_request", "", "");
            Document document = impl.createDocument(null, "cp_request", svgDOCTYPE);
            NodeList rootNodes = document.getElementsByTagName("cp_request");
            Element rechargeCreditRequestTag = (Element) rootNodes.item(0);
            Element cpIdNode = document.createElement("cp_id");
            Element transactionIdNode = document.createElement("cp_transaction_id");
            Element opTransactionIdNode = document.createElement("op_transaction_id");
            Element applicationNode = document.createElement("application");
            Element actionNode = document.createElement("action");
            Element userIdNode = document.createElement("user_id");
            Element transactionPriceNode = document.createElement("transaction_price");
            Element transactionCurrencyNode = document.createElement("transaction_currency");
            Element userValidityNode = document.createElement("user_validity");
            Element userGraceNode = document.createElement("user_grace");
            cpIdNode.setTextContent((String) map.get("cp_id"));
            transactionIdNode.setTextContent((String) map.get("IN_TXN_ID"));
            opTransactionIdNode.setTextContent((String) map.get(""));
            applicationNode.setTextContent((String) map.get("application"));
            actionNode.setTextContent((String) map.get("action"));
            userIdNode.setAttribute("type", "MSISDN");
            userIdNode.setTextContent(InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")));
            transactionPriceNode.setTextContent((String) map.get("INTERFACE_AMOUNT"));
            transactionCurrencyNode.setTextContent((String) map.get("transaction_currency"));
            userValidityNode.setTextContent((String) map.get("VALIDITY_DAYS"));
            userGraceNode.setTextContent((String) map.get("GRACE_DAYS"));
            rechargeCreditRequestTag.appendChild(cpIdNode);
            rechargeCreditRequestTag.appendChild(transactionIdNode);
            rechargeCreditRequestTag.appendChild(opTransactionIdNode);
            rechargeCreditRequestTag.appendChild(applicationNode);
            rechargeCreditRequestTag.appendChild(actionNode);
            rechargeCreditRequestTag.appendChild(userIdNode);
            rechargeCreditRequestTag.appendChild(transactionPriceNode);
            rechargeCreditRequestTag.appendChild(transactionCurrencyNode);
            rechargeCreditRequestTag.appendChild(userValidityNode);
            rechargeCreditRequestTag.appendChild(userGraceNode);
            Source input = new DOMSource(document);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(input, new StreamResult(out));
            requestStr = out.toString();
            requestStr = requestStr.replaceAll("xml version=\"1.0\" encoding=\"UTF-8\"", "xml version=\"1.0\"");
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Got the XML String as " + requestStr);

            return requestStr;
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exited requestStr: " + requestStr);
        }
    }

    /**
     * This Method Generate immediate Debit Request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateImmediateDebitRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered map=" + map);
        String requestStr = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cp_req_websvr.dtd");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DocumentType svgDOCTYPE = impl.createDocumentType("cp_request", "", "");
            Document document = impl.createDocument(null, "cp_request", svgDOCTYPE);
            NodeList rootNodes = document.getElementsByTagName("cp_request");
            Element immediateDebitRequestTag = (Element) rootNodes.item(0);
            Element cpIdNode = document.createElement("cp_id");
            Element transactionIdNode = document.createElement("cp_transaction_id");
            Element opTransactionIdNode = document.createElement("op_transaction_id");
            Element applicationNode = document.createElement("application");
            Element actionNode = document.createElement("action");
            Element userIdNode = document.createElement("user_id");
            Element transactionPriceNode = document.createElement("transaction_price");
            Element transactionCurrencyNode = document.createElement("transaction_currency");
            cpIdNode.setTextContent((String) map.get("cp_id"));
            transactionIdNode.setTextContent((String) map.get("IN_TXN_ID"));
            opTransactionIdNode.setTextContent("");
            applicationNode.setTextContent((String) map.get("application"));
            actionNode.setTextContent((String) map.get("action"));
            userIdNode.setTextContent(InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")));
            transactionPriceNode.setTextContent((String) map.get("INTERFACE_AMOUNT"));
            transactionCurrencyNode.setTextContent((String) map.get("transaction_currency"));
            immediateDebitRequestTag.appendChild(cpIdNode);
            immediateDebitRequestTag.appendChild(transactionIdNode);
            immediateDebitRequestTag.appendChild(opTransactionIdNode);
            immediateDebitRequestTag.appendChild(applicationNode);
            immediateDebitRequestTag.appendChild(actionNode);
            immediateDebitRequestTag.appendChild(userIdNode);
            immediateDebitRequestTag.appendChild(transactionPriceNode);
            immediateDebitRequestTag.appendChild(transactionCurrencyNode);
            Source input = new DOMSource(document);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(input, new StreamResult(out));
            requestStr = out.toString();
            requestStr = requestStr.replaceAll("xml version=\"1.0\" encoding=\"UTF-8\"", "xml version=\"1.0\"");

            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Got the XML String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This Method generate account information request
     * 
     * @param map
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateGetAccountInfoRequest(HashMap map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered map=" + map);
        String requestStr = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "cp_req_websvr.dtd");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DocumentType svgDOCTYPE = impl.createDocumentType("cp_request", "", "");
            Document document = impl.createDocument(null, "cp_request", svgDOCTYPE);
            NodeList rootNodes = document.getElementsByTagName("cp_request");
            Element accountInfoGettingRequestTag = (Element) rootNodes.item(0);
            Element cpIdNode = document.createElement("cp_id");
            cpIdNode.setTextContent((String) map.get("cp_id"));
            accountInfoGettingRequestTag.appendChild(cpIdNode);
            Element transactionIdNode = document.createElement("cp_transaction_id");
            transactionIdNode.setTextContent((String) map.get("IN_TXN_ID"));
            accountInfoGettingRequestTag.appendChild(transactionIdNode);
            Element opTransactionIdNode = document.createElement("op_transaction_id");
            opTransactionIdNode.setTextContent((String) map.get(""));
            accountInfoGettingRequestTag.appendChild(opTransactionIdNode);
            Element applicationNode = document.createElement("application");
            applicationNode.setTextContent((String) map.get("application"));
            accountInfoGettingRequestTag.appendChild(applicationNode);
            Element actionNode = document.createElement("action");
            actionNode.setTextContent((String) map.get("action"));
            accountInfoGettingRequestTag.appendChild(actionNode);
            Element userIdNode = document.createElement("user_id");
            userIdNode.setAttribute("type", "MSISDN");
            userIdNode.setTextContent(InterfaceUtil.getFilterMSISDN((String) map.get("INTERFACE_ID"), (String) map.get("MSISDN")));
            accountInfoGettingRequestTag.appendChild(userIdNode);
            Source input = new DOMSource(document);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(input, new StreamResult(out));
            requestStr = out.toString();
            requestStr = requestStr.replaceAll("xml version=\"1.0\" encoding=\"UTF-8\"", "xml version=\"1.0\"");
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Got the XML String as " + requestStr);
            return requestStr;
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exiting requestStr: " + requestStr);
        }
    }

    /**
     * This Method parse Immediate Debit Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseImmediateDebitResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            String _str = "0";
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("cp_reply");
            Element immediateDebitResponseTag = (Element) rootNodes.item(0);
            Element resultTag = (Element) immediateDebitResponseTag.getElementsByTagName("result").item(0);
            if (_str.equals(((Text) resultTag.getFirstChild()).getData())) {
                map.put("result", ((Text) resultTag.getFirstChild()).getData());

                Element cpIdTag = (Element) immediateDebitResponseTag.getElementsByTagName("cp_id").item(0);
                if (((Text) cpIdTag.getFirstChild()) != null)
                    map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
                else
                    map.put("cp_id", null);
                Element cpTransactionIdTag = (Element) immediateDebitResponseTag.getElementsByTagName("cp_transaction_id").item(0);
                if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                    map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
                else
                    map.put("cp_transaction_id", null);
                /*
                 * Element opTransactionIdTag =
                 * (Element)immediateDebitResponseTag
                 * .getElementsByTagName("op_transaction_id").item(0);
                 * if(((Text)opTransactionIdTag.getFirstChild())!=null)
                 * map.put("op_transaction_id",((Text)opTransactionIdTag.
                 * getFirstChild()).getData());
                 * else
                 * map.put("op_transaction_id",null);
                 */
                Element serviceTypeTag = (Element) immediateDebitResponseTag.getElementsByTagName("service_type").item(0);
                if (((Text) serviceTypeTag.getFirstChild()) != null)
                    map.put("service_type", ((Text) serviceTypeTag.getFirstChild()).getData());
                else
                    map.put("service_type", null);
            } else {
                String result = ((Text) resultTag.getFirstChild()).getData();
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(AlcatelI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelI.RESULT_ERROR_XML_PARSE.equals(result))) {
                    Element cpIdTag = (Element) immediateDebitResponseTag.getElementsByTagName("cp_id").item(0);
                    if (((Text) cpIdTag.getFirstChild()) != null)
                        map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
                    else
                        map.put("cp_id", null);
                    Element cpTransactionIdTag = (Element) immediateDebitResponseTag.getElementsByTagName("cp_transaction_id").item(0);
                    if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                        map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
                    else
                        map.put("cp_transaction_id", null);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exiting map: " + map);
        }
    }

    /**
     * This Method parse GetAccount Info Response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */
    public HashMap parseGetAccountInfoResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            String _str = "0";
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("cp_reply");
            Element accountInfoGettingResponseTag = (Element) rootNodes.item(0);
            Element resultTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("result").item(0);

            if (_str.equals(((Text) resultTag.getFirstChild()).getData())) {
                map.put("result", ((Text) resultTag.getFirstChild()).getData());

                Element cpIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("cp_id").item(0);
                if (((Text) cpIdTag.getFirstChild()) != null)
                    map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
                else
                    map.put("cp_id", null);
                Element cpTransactionIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("cp_transaction_id").item(0);
                if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                    map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
                else
                    map.put("cp_transaction_id", null);
                Element opTransactionIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("op_transaction_id").item(0);
                if (((Text) opTransactionIdTag.getFirstChild()) != null)
                    map.put("op_transaction_id", ((Text) opTransactionIdTag.getFirstChild()).getData());
                else
                    map.put("op_transaction_id", null);

                Element creditBalanceTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("credit_balance").item(0);
                if (((Text) creditBalanceTag.getFirstChild()) != null)
                    map.put("credit_balance", ((Text) creditBalanceTag.getFirstChild()).getData());
                else
                    map.put("credit_balance", null);
                Element endValDateTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("end_val_date").item(0);
                if (((Text) endValDateTag.getFirstChild()) != null)
                    map.put("end_val_date", ((Text) endValDateTag.getFirstChild()).getData());
                else
                    map.put("end_val_date", null);
                Element endInactDateTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("end_inact_date").item(0);
                if (((Text) endInactDateTag.getFirstChild()) != null)
                    map.put("end_inact_date", ((Text) endInactDateTag.getFirstChild()).getData());
                else
                    map.put("end_inact_date", null);
                Element profileTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("profile").item(0);
                if (((Text) profileTag.getFirstChild()) != null)
                    map.put("profile", ((Text) profileTag.getFirstChild()).getData());
                else
                    map.put("profile", null);
                Element profLangTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("prof_lang").item(0);
                if (((Text) profLangTag.getFirstChild()) != null)
                    map.put("prof_lang", ((Text) profLangTag.getFirstChild()).getData());
                else
                    map.put("prof_lang", null);
                Element accStatusTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("acc_status").item(0);
                if (((Text) accStatusTag.getFirstChild()) != null)
                    map.put("acc_status", ((Text) accStatusTag.getFirstChild()).getData());
                else
                    map.put("acc_status", null);
                Element lockInfoTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("lock_information").item(0);
                if (((Text) lockInfoTag.getFirstChild()) != null)
                    map.put("lock_information", ((Text) lockInfoTag.getFirstChild()).getData());
                else
                    map.put("lock_information", null);
                Element serviceTypeTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("service_type").item(0);
                if (((Text) serviceTypeTag.getFirstChild()) != null)
                    map.put("service_type", ((Text) serviceTypeTag.getFirstChild()).getData());
                else
                    map.put("service_type", null);

            } else {
                String result = ((Text) resultTag.getFirstChild()).getData();
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(AlcatelI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelI.RESULT_ERROR_XML_PARSE.equals(result))) {
                    Element cpIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("cp_id").item(0);
                    if (((Text) cpIdTag.getFirstChild()) != null)
                        map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
                    else
                        map.put("cp_id", null);
                    Element cpTransactionIdTag = (Element) accountInfoGettingResponseTag.getElementsByTagName("cp_transaction_id").item(0);
                    if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                        map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
                    else
                        map.put("cp_transaction_id", null);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }

            return map;

        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exiting map: " + map);
        }
    }

    /**
     * This Method parse recharge credit response
     * 
     * @param responseStr
     *            String
     * @throws Exception
     * @return map
     */

    private HashMap parseRechargeCreditResponse(String responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered responseStr: " + responseStr);
        HashMap map = new HashMap();
        try {
            String _str = "0";
            Document document = _parser.parse(new ByteArrayInputStream(responseStr.getBytes()));
            NodeList rootNodes = document.getElementsByTagName("cp_reply");
            Element rechargeCreditResponseTag = (Element) rootNodes.item(0);
            Element resultTag = (Element) rechargeCreditResponseTag.getElementsByTagName("result").item(0);

            if (_str.equals(((Text) resultTag.getFirstChild()).getData())) {
                map.put("result", ((Text) resultTag.getFirstChild()).getData());

                Element cpIdTag = (Element) rechargeCreditResponseTag.getElementsByTagName("cp_id").item(0);
                if (((Text) cpIdTag.getFirstChild()) != null)
                    map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
                else
                    map.put("cp_id", null);
                Element cpTransactionIdTag = (Element) rechargeCreditResponseTag.getElementsByTagName("cp_transaction_id").item(0);
                if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                    map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
                else
                    map.put("cp_transaction_id", null);
                Element opTransactionIdTag = (Element) rechargeCreditResponseTag.getElementsByTagName("op_transaction_id").item(0);
                if (((Text) opTransactionIdTag.getFirstChild()) != null)
                    map.put("op_transaction_id", ((Text) opTransactionIdTag.getFirstChild()).getData());
                else
                    map.put("op_transaction_id", null);

                Element creditBalanceTag = (Element) rechargeCreditResponseTag.getElementsByTagName("credit_balance").item(0);
                if (((Text) creditBalanceTag.getFirstChild()) != null)
                    map.put("credit_balance", ((Text) creditBalanceTag.getFirstChild()).getData());
                else
                    map.put("credit_balance", null);
                Element endValDateTag = (Element) rechargeCreditResponseTag.getElementsByTagName("end_val_date").item(0);
                if (((Text) endValDateTag.getFirstChild()) != null)
                    map.put("end_val_date", ((Text) endValDateTag.getFirstChild()).getData());
                else
                    map.put("end_val_date", null);
                Element endInactDateTag = (Element) rechargeCreditResponseTag.getElementsByTagName("end_inact_date").item(0);
                if (((Text) endInactDateTag.getFirstChild()) != null)
                    map.put("end_inact_date", ((Text) endInactDateTag.getFirstChild()).getData());
                else
                    map.put("end_inact_date", null);
                Element profileTag = (Element) rechargeCreditResponseTag.getElementsByTagName("profile").item(0);
                if (((Text) profileTag.getFirstChild()) != null)
                    map.put("profile", ((Text) profileTag.getFirstChild()).getData());
                else
                    map.put("profile", null);
                Element profLangTag = (Element) rechargeCreditResponseTag.getElementsByTagName("prof_lang").item(0);
                if (((Text) profLangTag.getFirstChild()) != null)
                    map.put("prof_lang", ((Text) profLangTag.getFirstChild()).getData());
                else
                    map.put("prof_lang", null);
                Element accStatusTag = (Element) rechargeCreditResponseTag.getElementsByTagName("acc_status").item(0);
                if (((Text) accStatusTag.getFirstChild()) != null)
                    map.put("acc_status", ((Text) accStatusTag.getFirstChild()).getData());
                else
                    map.put("acc_status", null);
                Element serviceTypeTag = (Element) rechargeCreditResponseTag.getElementsByTagName("service_type").item(0);
                if (((Text) serviceTypeTag.getFirstChild()) != null)
                    map.put("service_type", ((Text) serviceTypeTag.getFirstChild()).getData());
                else
                    map.put("service_type", null);
            } else {
                String result = ((Text) resultTag.getFirstChild()).getData();
                /**
                 * Note: when we get the xml error code 3 or 9 in that case we
                 * will not receive cp_id and transaction id only else we will
                 * receive both
                 */
                if (!(AlcatelI.RESULT_ERROR_MALFORMED_REQUEST.equals(result) || AlcatelI.RESULT_ERROR_XML_PARSE.equals(result))) {
                    Element cpIdTag = (Element) rechargeCreditResponseTag.getElementsByTagName("cp_id").item(0);
                    if (((Text) cpIdTag.getFirstChild()) != null)
                        map.put("cp_id", ((Text) cpIdTag.getFirstChild()).getData());
                    else
                        map.put("cp_id", null);
                    Element cpTransactionIdTag = (Element) rechargeCreditResponseTag.getElementsByTagName("cp_transaction_id").item(0);
                    if (((Text) cpTransactionIdTag.getFirstChild()) != null)
                        map.put("cp_transaction_id", ((Text) cpTransactionIdTag.getFirstChild()).getData());
                    else
                        map.put("cp_transaction_id", null);
                } else {
                    map.put("cp_id", null);
                    map.put("cp_transaction_id", null);
                }
                map.put("result", result);
            }
            return map;
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exiting map: " + map);
        }
    }

}
