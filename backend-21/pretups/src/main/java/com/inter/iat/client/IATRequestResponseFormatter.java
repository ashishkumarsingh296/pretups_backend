package com.inter.iat.client;

/**
 * @(#) IATRequestResponseFormatter.java
 *      Copyright(c) 2011, Comviva Technologies Ltd.
 *      All Rights Reserved
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Created By Created On History
 *      ------------------------------------------------------------------------
 *      -------------------------
 *      Babu Kunwar 02-DEC-2011 Initial Creation
 *      ------------------------------------------------------------------------
 *      -------------------------
 */

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.iat.IATI;

/**
 * ---------------------------------------------
 * Sample Resposne For RechargeCreditRequest
 * ---------------------------------------------
 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
 * <soapenv:Header />
 * <soapenv:Body>
 * <m:rechargeRequestResponse xmlns:m="http://com/wha/iat/pretups/ws">
 * <m:return>
 * <java:IatTimeStamp
 * xmlns:java="java:com.wha.iat.pretups.ws">2009-06-02T17:34:44.778
 * +02:00</java:IatTimeStamp>
 * <java:IatTrxId xmlns:java="java:com.wha.iat.pretups.ws">40-7102649052301415
 * </java:IatTrxId>
 * <java:Message
 * xmlns:java="java:com.wha.iat.pretups.ws">accepted</java:Message>
 * <java:Status xmlns:java="java:com.wha.iat.pretups.ws">0</java:Status>
 * <java:SNwTrxId
 * xmlns:java="java:com.wha.iat.pretups.ws">5646dd4564</java:SNwTrxId>
 * </m:return>
 * </m:rechargeRequestResponse>
 * </soapenv:Body>
 * </soapenv:Envelope>
 * 
 * @author babu.kunwar
 */

public class IATRequestResponseFormatter {
    private static Log _log = LogFactory.getLog("IATRequestResponseFormatter".getClass().getName());

    /**
     * @method_name:- parseCreditResponse is used for parsing the response value
     *                for the
     *                credit request for the IN and setting the value in
     *                HASHMAP. Which can be retrieved
     *                later at IATINHandler.
     * @param p_responseCreditString
     * @return
     * @throws Exception
     */
    public HashMap<String, String> parseCreditResponse(String p_responseCreditString) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseCreditResponse", "Entered with p_responseCreditString::=" + p_responseCreditString);

        HashMap<String, String> creditResponseMap = null;
        try {
            int startIndex = 0;
            int endIndex = 0;
            creditResponseMap = new HashMap<String, String>();
            startIndex = p_responseCreditString.indexOf("<m:return>");
            if (startIndex > 0) {
                startIndex = p_responseCreditString.indexOf("<java:IatTrxId");
                startIndex = p_responseCreditString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                if (p_responseCreditString.contains("</java:IatTrxId>")) {
                    endIndex = p_responseCreditString.indexOf("</java:IatTrxId>");
                    String iatTrxId = p_responseCreditString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                    System.out.println("\n IatTrxId::=" + iatTrxId);
                    creditResponseMap.put("IatTrxId", iatTrxId);
                } else {
                    creditResponseMap.put("IatTrxId", null);
                }

                startIndex = p_responseCreditString.indexOf("<java:IatTimeStamp");
                startIndex = p_responseCreditString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                if (p_responseCreditString.contains("</java:IatTimeStamp>")) {
                    endIndex = p_responseCreditString.indexOf("</java:IatTimeStamp>");
                    String iatTimeStamp = p_responseCreditString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                    System.out.println("\n IatTimeStamp::=" + iatTimeStamp);
                    creditResponseMap.put("IatTimeStamp", iatTimeStamp);
                } else {
                    creditResponseMap.put("IatTimeStamp", null);
                }

                startIndex = p_responseCreditString.indexOf("<java:Message");
                startIndex = p_responseCreditString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_responseCreditString.indexOf("</java:Message>");
                String message = p_responseCreditString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n Message::=" + message);
                creditResponseMap.put("Message", message);

                startIndex = p_responseCreditString.indexOf("<java:Status");
                startIndex = p_responseCreditString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_responseCreditString.indexOf("</java:Status>");
                String status = p_responseCreditString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n Status::=" + status);
                creditResponseMap.put("Status", status);

                startIndex = p_responseCreditString.indexOf("<java:SNwTrxId");
                startIndex = p_responseCreditString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_responseCreditString.indexOf("</java:SNwTrxId>");
                String sNwTrxId = p_responseCreditString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n SNwTrxId::=" + sNwTrxId);
                creditResponseMap.put("SNwTrxId", sNwTrxId);
            } else {
                _log.error("parseCreditResponse", " CreditResponse Not Received from IAH HUB");
                throw new BTSLBaseException("IATRequestResponseFormatter", "parseCreditResponse", IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
            }
        } catch (Exception e) {
            _log.error("parseCreditResponse", "Exception e: " + e);
            throw new BTSLBaseException("IATRequestResponseFormatter", "parseCreditResponse", IATI.RECHARGE_CREDIT_RESPONSE_FAIL);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCreditResponse", "parseCreditResponse Exited with responseMap::=" + creditResponseMap.toString());
        }
        return creditResponseMap;
    }

    /**
     * ----------------------------------------------------------
     * Sample Response for ChcekStatusRequest
     * ----------------------------------------------------------
     * <soapenv:Envelope
     * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
     * <soapenv:Header />
     * <soapenv:Body>
     * <m:checkStatusRequestResponse xmlns:m="http://com/wha/iat/pretups/ws">
     * <m:return>
     * <java:ExchangeRate
     * xmlns:java="java:com.wha.iat.pretups.ws">520.0</java:ExchangeRate>
     * <java:Fees xmlns:java="java:com.wha.iat.pretups.ws">1.0</java:Fees>
     * <java:IatReasonCode
     * xmlns:java="java:com.wha.iat.pretups.ws">0</java:IatReasonCode>
     * <java:IatReasonMessage
     * xmlns:java="java:com.wha.iat.pretups.ws">ok</java:IatReasonMessage>
     * <java:Level xmlns:java="java:com.wha.iat.pretups.ws">1</java:Level>
     * <java:ProvRatio
     * xmlns:java="java:com.wha.iat.pretups.ws">0.3</java:ProvRatio>
     * <java:RBonus xmlns:java="java:com.wha.iat.pretups.ws">2.0</java:RBonus>
     * <java:RPfReceivedAmount
     * xmlns:java="java:com.wha.iat.pretups.ws">41000.0</java:RPfReceivedAmount>
     * <java:RecipientReceivedAmount
     * xmlns:java="java:com.wha.iat.pretups.ws">41002.0
     * </java:RecipientReceivedAmount>
     * <java:RNwReasonCode
     * xmlns:java="java:com.wha.iat.pretups.ws">200</java:RNwReasonCode>
     * <java:RNwReasonMessage
     * xmlns:java="java:com.wha.iat.pretups.ws">U</java:RNwReasonMessage>
     * <java:IatReceivedAmount
     * xmlns:java="java:com.wha.iat.pretups.ws">7.5</java:IatReceivedAmount>
     * <java:Status xmlns:java="java:com.wha.iat.pretups.ws">3</java:Status>
     * <java:RNwId>453Q960</java:RNwId>
     * </m:return>
     * </m:checkStatusRequestResponse>
     * </soapenv:Body>
     * </soapenv:Envelope>
     */

    /**
     * Method:- parseCheckStatusResponse This method is used to parse the
     * response
     * for the checkStatus response wheter the credit request was success or a
     * faliure.
     * for the check status request.
     * 
     * @param p_checkStatusResponseString
     * @return
     * @throws Exception
     */
    public HashMap<String, String> parseCheckStatusResponse(String p_checkStatusResponseString) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseCheckStatusResponse", "Entered with p_checkStatusResponseString::=" + p_checkStatusResponseString);

        HashMap<String, String> checkStatusResponseMap = null;
        try {
            int startIndex = 0;
            int endIndex = 0;
            checkStatusResponseMap = new HashMap<String, String>();
            startIndex = p_checkStatusResponseString.indexOf("<m:return>");
            if (startIndex > 0) {
                startIndex = p_checkStatusResponseString.indexOf("<java:ExchangeRate");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:ExchangeRate>");
                String exchangeRate = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n ExchangeRate::=" + exchangeRate);
                checkStatusResponseMap.put("ExchangeRate", exchangeRate);

                startIndex = p_checkStatusResponseString.indexOf("<java:Fees");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:Fees>");
                String fees = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n:: Fees=" + fees);
                checkStatusResponseMap.put("Fees", fees);

                startIndex = p_checkStatusResponseString.indexOf("<java:IatReasonCode");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:IatReasonCode>");
                String iatReasonCode = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n IatReasonCode::=" + iatReasonCode);
                checkStatusResponseMap.put("IatReasonCode", iatReasonCode);

                startIndex = p_checkStatusResponseString.indexOf("<java:IatReasonMessage");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:IatReasonMessage>");
                String iatReasonMessage = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n IatReasonMessage::=" + iatReasonMessage);
                checkStatusResponseMap.put("IatReasonMessage", iatReasonMessage);

                startIndex = p_checkStatusResponseString.indexOf("<java:Level");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:Level>");
                String level = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n Level::=" + level);
                checkStatusResponseMap.put("Level", level);

                startIndex = p_checkStatusResponseString.indexOf("<java:ProvRatio");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:ProvRatio>");
                String provRatio = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n ProvRatio::=" + provRatio);
                checkStatusResponseMap.put("ProvRatio", provRatio);

                startIndex = p_checkStatusResponseString.indexOf("<java:RBonus");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:RBonus>");
                String rBonus = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n RBonus::=" + rBonus);
                checkStatusResponseMap.put("RBonus", rBonus);

                startIndex = p_checkStatusResponseString.indexOf("<java:RPfReceivedAmount");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:RPfReceivedAmount>");
                String rPfReceivedAmount = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n RPfReceivedAmount::=" + rPfReceivedAmount);
                checkStatusResponseMap.put("RPfReceivedAmount", rPfReceivedAmount);

                startIndex = p_checkStatusResponseString.indexOf("<java:RecipientReceivedAmount");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:RecipientReceivedAmount>");
                String recipientReceivedAmount = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n RecipientReceivedAmount::=" + recipientReceivedAmount);
                checkStatusResponseMap.put("RecipientReceivedAmount", recipientReceivedAmount);

                startIndex = p_checkStatusResponseString.indexOf("<java:RNwReasonCode");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                if (p_checkStatusResponseString.contains("</java:RNwReasonCode>")) {
                    endIndex = p_checkStatusResponseString.indexOf("</java:RNwReasonCode>");
                    String rNwReasonCode = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                    System.out.println("\n RNwReasonCode::=" + rNwReasonCode);
                    checkStatusResponseMap.put("RNwReasonCode", rNwReasonCode);
                } else
                    checkStatusResponseMap.put("RNwReasonCode", null);

                startIndex = p_checkStatusResponseString.indexOf("<java:RNwReasonMessage");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                if (p_checkStatusResponseString.contains("</java:RNwReasonMessage>")) {
                    endIndex = p_checkStatusResponseString.indexOf("</java:RNwReasonMessage>");
                    String rNwReasonMessage = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                    System.out.println("\n RNwReasonMessage::=" + rNwReasonMessage);
                    checkStatusResponseMap.put("RNwReasonMessage", rNwReasonMessage);
                } else
                    checkStatusResponseMap.put("RNwReasonMessage", null);
                startIndex = p_checkStatusResponseString.indexOf("<java:IatReceivedAmount");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:IatReceivedAmount>");
                String iatReceivedAmount = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n IatReceivedAmount::=" + iatReceivedAmount);
                checkStatusResponseMap.put("IatReceivedAmount", iatReceivedAmount);

                startIndex = p_checkStatusResponseString.indexOf("<java:Status");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                endIndex = p_checkStatusResponseString.indexOf("</java:Status>");
                String status = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                System.out.println("\n Status::=" + status);
                checkStatusResponseMap.put("Status", status);

                startIndex = p_checkStatusResponseString.indexOf("<java:RNwId");
                startIndex = p_checkStatusResponseString.indexOf("java:com.wha.iah.pretups.ws", startIndex);
                if (p_checkStatusResponseString.contains("</java:RNwId>")) {
                    endIndex = p_checkStatusResponseString.indexOf("</java:RNwId>");
                    String rNwId = p_checkStatusResponseString.substring(startIndex + ("java:com.wha.iah.pretups.ws").length() + 2, endIndex);
                    System.out.println("\n RNwId::=" + rNwId);
                    checkStatusResponseMap.put("RNwId", rNwId);
                } else
                    checkStatusResponseMap.put("RNwId", null);
            } else {
                _log.error("parseCheckStatusResponse", "Got Null CheckStatusResponse From IAH HUB");
                throw new BTSLBaseException("IATRequestResponseFormatter", "parseCheckStatusResponse", IATI.CHECKSTATUS_RESPONSE_FAIL);
            }
        } catch (Exception e) {
            _log.error("parseCreditResponse", "Exception e: " + e);
            throw new BTSLBaseException("IATRequestResponseFormatter", "parseCreditResponse", IATI.CHECKSTATUS_RESPONSE_FAIL);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCreditResponse", "parseCreditResponse Exited with responseMap::=" + checkStatusResponseMap.toString());
        }

        return checkStatusResponseMap;

    }
}
