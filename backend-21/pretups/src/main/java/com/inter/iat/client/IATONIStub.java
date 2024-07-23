package com.inter.iat.client;

/**
 * @(#) IATONIStub.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;
import com.inter.iat.IATUtil;

public class IATONIStub implements IATHUB_pretups {

    private static Log _log = LogFactory.getLog("IATONIStub".getClass().getName());
    private static String _endURL = null;

    public BurnResult burnRequest(BurnParam burnParam) {
        // TODO Auto-generated method stub
        return null;
    }

    public CatalogResult catalogRequest(CatalogParam catalogParam) {
        // TODO Auto-generated method stub
        return null;
    }

    public CreateReceivableResult createReceivableRequest(CreateReceivableParam createReceivableParam) {
        // TODO Auto-generated method stub
        return null;
    }

    public QuotationResult quotationRequest(QuotationParam quotationParam) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @Method:- rechargeRequest This is used to call the rechargeRequest
     *           Service
     *           on IAH platform
     * @param RechargeParam
     * @param AuthHeader
     * @return RechargeResult
     * @author babu.kunwar
     */
    public RechargeResult rechargeRequest(RechargeParam rechargeParam, AuthHeader authHeader) {
        if (_log.isDebugEnabled())
            _log.debug("rechargeRequest :: ", "rechargeRequest Entered with rechargeParam::=" + rechargeParam.toString() + " authHeader::+" + authHeader.toString());

        URL endPointURL = null;
        RechargeResult rechargeResult = null;
        IATRequestResponseFormatter iatRequestResponseFormatter = null;
        HashMap<String, String> creditResponseMap = null;
        String creditResponseStringMessage = null;
        Calendar iatTimeStamp = null;
        String transactionStatus = null;
        try {
            /*
             * Creating the Header for the request
             * Using SAAJ JAX-RPC Implementation for request
             * Babu Kunwar
             * Creating an empty header for rechargeRequest
             * You can create non-empty body by using the line of code
             * SOAPHeader header = message.getSOAPHeader();
             */
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();
            // SOAPPart soapPart = message.getSOAPPart();
            // SOAPEnvelope rechargeRequestEnvelope = soapPart.getEnvelope();
            SOAPBody rechargeRequestBody = message.getSOAPBody();

            if (_log.isDebugEnabled())
                _log.debug("rechargeRequest :: ", "rechargeRequestBody::=" + rechargeRequestBody);

            SOAPHeader rechargeRequestHeader = message.getSOAPHeader();
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            Name authHeaderName = soapFactory.createName("authHeader", "", "http://com/wha/iah/pretups/ws");
            Name authHeaderParamName = soapFactory.createName("authHeaderParam");
            Name userName = soapFactory.createName("UserName", "", "java:com.wha.iah.pretups.ws");
            Name password = soapFactory.createName("Password", "", "java:com.wha.iah.pretups.ws");

            javax.xml.soap.SOAPHeaderElement rchrgReqHeaderElemnt = rechargeRequestHeader.addHeaderElement(authHeaderName);
            SOAPElement authHeaderParamElement = rchrgReqHeaderElemnt.addChildElement(authHeaderParamName);
            SOAPElement userNameElement = authHeaderParamElement.addChildElement(userName);
            userNameElement.addTextNode(authHeader.getAuthHeaderParam().getUserName());
            SOAPElement passwordElement = authHeaderParamElement.addChildElement(password);
            passwordElement.addTextNode(authHeader.getAuthHeaderParam().getPassword());

            /*
             * Creating the Body for the request
             * Using SAAJ JAX-RPC Implementation for request
             * Babu Kunwar
             * Creating an empty body for rechargeRequest
             * You can create non-empty body by using the line of code
             * SOAPBody body = message.getSOAPBody();
             */
            Name rechargeRequest = soapFactory.createName("rechargeRequest", "", "http://com/wha/iah/pretups/ws");
            Name rechargeParams = soapFactory.createName("rechargeParam", "", "http://com/wha/iah/pretups/ws");
            Name serviceType = soapFactory.createName("ServiceType", "", "java:com.wha.iah.pretups.ws");
            Name sNwTrxId = soapFactory.createName("SNwTrxId", "", "java:com.wha.iah.pretups.ws");
            Name sNwId = soapFactory.createName("SNwId", "", "java:com.wha.iah.pretups.ws");
            Name sNwType = soapFactory.createName("SNwType", "", "java:com.wha.iah.pretups.ws");
            Name sCountryCode = soapFactory.createName("SCountryCode", "", "java:com.wha.iah.pretups.ws");
            Name rCountryCode = soapFactory.createName("RCountryCode", "", "java:com.wha.iah.pretups.ws");
            Name sendingBearer = soapFactory.createName("SendingBearer", "", "java:com.wha.iah.pretups.ws");
            Name rechargeType = soapFactory.createName("RechargeType", "", "java:com.wha.iah.pretups.ws");
            Name MSISDN1 = soapFactory.createName("MSISDN1", "", "java:com.wha.iah.pretups.ws");
            Name MSISDN2 = soapFactory.createName("MSISDN2", "", "java:com.wha.iah.pretups.ws");
            Name MSISDN3 = soapFactory.createName("MSISDN3", "", "java:com.wha.iah.pretups.ws");
            // Name retailerId =soapFactory.createName("RetailerId","",
            // "java:com.wha.iah.pretups.ws");
            Name deviceId = soapFactory.createName("DeviceId", "", "java:com.wha.iah.pretups.ws");
            Name sNwTimeStamp = soapFactory.createName("SNwTimeStamp", "", "java:com.wha.iah.pretups.ws");
            Name amount = soapFactory.createName("Amount", "", "java:com.wha.iah.pretups.ws");
            Name ext1 = soapFactory.createName("Ext1", "", "java:com.wha.iah.pretups.ws");
            Name ext2 = soapFactory.createName("Ext2", "", "java:com.wha.iah.pretups.ws");
            Name ext3 = soapFactory.createName("Ext3", "", "java:com.wha.iah.pretups.ws");
            SOAPBodyElement soapBodyElement1 = rechargeRequestBody.addBodyElement(rechargeRequest);
            SOAPElement soapBodyElement2 = soapBodyElement1.addChildElement(rechargeParams);
            SOAPElement serviceTypeElement = soapBodyElement2.addChildElement(serviceType);
            serviceTypeElement.addTextNode(String.valueOf(rechargeParam.getServiceType()));
            SOAPElement sNwTrxIdElement = soapBodyElement2.addChildElement(sNwTrxId);
            sNwTrxIdElement.addTextNode(rechargeParam.getSNwTrxId());
            SOAPElement sNwIdElement = soapBodyElement2.addChildElement(sNwId);
            sNwIdElement.addTextNode(rechargeParam.getSNwId());
            SOAPElement sNwTypeElement = soapBodyElement2.addChildElement(sNwType);
            sNwTypeElement.addTextNode(rechargeParam.getSNwType());
            SOAPElement sCountryCodeElement = soapBodyElement2.addChildElement(sCountryCode);
            sCountryCodeElement.addTextNode(String.valueOf(rechargeParam.getSCountryCode()));
            SOAPElement rCountryCodeElement = soapBodyElement2.addChildElement(rCountryCode);
            rCountryCodeElement.addTextNode(String.valueOf(rechargeParam.getRCountryCode()));
            SOAPElement sendingBearerElement = soapBodyElement2.addChildElement(sendingBearer);
            sendingBearerElement.addTextNode(rechargeParam.getSendingBearer());
            SOAPElement rechargeTypeElement = soapBodyElement2.addChildElement(rechargeType);
            rechargeTypeElement.addTextNode(String.valueOf(rechargeParam.getRechargeType()));
            SOAPElement MSISDN1Element = soapBodyElement2.addChildElement(MSISDN1);
            MSISDN1Element.addTextNode(rechargeParam.getMSISDN1());
            SOAPElement MSISDN2Element = soapBodyElement2.addChildElement(MSISDN2);
            MSISDN2Element.addTextNode(rechargeParam.getMSISDN2());
            SOAPElement MSISDN3Element = soapBodyElement2.addChildElement(MSISDN3);
            if (!BTSLUtil.isNullString(rechargeParam.getMSISDN3()))
                MSISDN3Element.addTextNode(rechargeParam.getMSISDN3());
            else
                MSISDN3Element.addTextNode("");
            // SOAPElement
            // retailerIdElement=soapBodyElement2.addChildElement(retailerId);
            // retailerIdElement.addTextNode(rechargeParam.getRetailerId());
            SOAPElement deviceIdElement = soapBodyElement2.addChildElement(deviceId);
            deviceIdElement.addTextNode(rechargeParam.getDeviceId());
            SimpleDateFormat sNWTimeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String sNWTimeStampString = sNWTimeStamp.format(rechargeParam.getSNwTimeStamp().getTime());
            SOAPElement sNwTimeStampElement = soapBodyElement2.addChildElement(sNwTimeStamp);
            sNwTimeStampElement.addTextNode(sNWTimeStampString);
            SOAPElement amountElement = soapBodyElement2.addChildElement(amount);
            amountElement.addTextNode(String.valueOf(rechargeParam.getAmount()));
            SOAPElement ext1Element = soapBodyElement2.addChildElement(ext1);
            if (BTSLUtil.isNullString(rechargeParam.getExt1()))
                ext1Element.addTextNode("");
            else
                ext1Element.addTextNode(rechargeParam.getExt1());
            SOAPElement ext2Element = soapBodyElement2.addChildElement(ext2);
            if (BTSLUtil.isNullString(rechargeParam.getExt2()))
                ext2Element.addTextNode("");
            else
                ext2Element.addTextNode(rechargeParam.getExt2());
            SOAPElement ext3Element = soapBodyElement2.addChildElement(ext3);
            if (BTSLUtil.isNullString(rechargeParam.getExt3()))
                ext3Element.addTextNode("");
            else
                ext3Element.addTextNode(rechargeParam.getExt3());
            message.saveChanges();
            ByteArrayOutputStream writeRequestToString = new ByteArrayOutputStream();
            /*
             * Prinitng the XML Request Message sent to IAH
             */
            try {
                if (_log.isDebugEnabled())
                    _log.debug("\n" + "rechargeRequest :: ", "After Invoking the Call rechargeResponse::=");

                message.writeTo(writeRequestToString);
                String creditRequestStringMessage = new String(writeRequestToString.toByteArray());
                System.out.println("\n");
                System.out.println("\n");
                System.out.println("\n");
                System.out.println("creditRequestStringMessage ::=" + creditRequestStringMessage);
                System.out.println("\n");
                System.out.println("\n");
                System.out.println("\n");
                System.out.println("\n");

            } catch (IOException e1) {
                e1.getMessage();
                e1.printStackTrace();
            }
            /*
             * Getting the SOAPConnection to send the request
             * for recharge
             */

            try {
                SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
                SOAPConnection soapConnection = soapConnectionFactory.createConnection();

                try {
                    endPointURL = new URL(_endURL);
                } catch (MalformedURLException e) {
                    _log.error("rechargeRequest", "MalformedURLException");
                    e.getMessage();
                    e.printStackTrace();
                }
                SOAPMessage rechargeResponse = soapConnection.call(message, endPointURL);
                rechargeResponse.saveChanges();
                ByteArrayOutputStream writeResponseToString = new ByteArrayOutputStream();
                rechargeResult = new RechargeResult();
                try {
                    if (_log.isDebugEnabled())
                        _log.debug("rechargeRequest :: ", "After Invoking the Call rechargeResponse::=");

                    rechargeResponse.writeTo(writeResponseToString);
                    creditResponseStringMessage = new String(writeResponseToString.toByteArray());
                    System.out.println("\n");
                    System.out.println("\n");
                    System.out.println("responseStringMessage ::=" + creditResponseStringMessage);
                    System.out.println("\n");
                    System.out.println("\n");
                } catch (IOException e1) {
                    e1.getMessage();
                    e1.printStackTrace();
                }
                if (!IATUtil.isNullString(creditResponseStringMessage)) {
                    iatRequestResponseFormatter = new IATRequestResponseFormatter();
                    creditResponseMap = new HashMap<String, String>();
                    try {
                        creditResponseMap = iatRequestResponseFormatter.parseCreditResponse(creditResponseStringMessage);

                        if (_log.isDebugEnabled())
                            _log.debug("rechargeRequest ::", "returning after parsing response creditResponseMap::=" + creditResponseMap.toString());
                    } catch (Exception e) {
                        _log.error("rechargeRequest", "Got Exception in parsing the credit response");
                        e.getMessage();
                        e.printStackTrace();
                    }
                    if (creditResponseMap.size() > 0 && creditResponseMap != null) {
                        String iatResTimeStamp = creditResponseMap.get("IatTimeStamp");
                        if (!IATUtil.isNullString(iatResTimeStamp)) {
                            try {
                                String[] resDateTime = iatResTimeStamp.split("T");

                                if (_log.isDebugEnabled())
                                    _log.debug("rechargeRequest ::", "resDateTime[0]::=" + resDateTime[0] + " resDateTime[1]" + resDateTime[1]);
                                String[] timeArr = resDateTime[1].split("\\.");
                                String iatDateTimeStamp = resDateTime[0] + " " + timeArr[0];

                                if (_log.isDebugEnabled())
                                    _log.debug("rechargeRequest ::", "iatDateTimeStamp::=" + iatDateTimeStamp);

                                Date iatDate = IATUtil.convertStringToDate(iatDateTimeStamp);

                                if (_log.isDebugEnabled())
                                    _log.debug("rechargeRequest ::", "iatDate::=" + iatDate);

                                iatTimeStamp = IATUtil.getDateFromDateString(iatDate);

                                if (_log.isDebugEnabled())
                                    _log.debug("rechargeRequest ::", "Final iatTimeStamp::=" + iatTimeStamp);

                                rechargeResult.setIatTimeStamp(iatTimeStamp);
                            } catch (ParseException parseExc) {
                                _log.error("rechargeRequest", "Got Exception in parsing date String");
                                parseExc.printStackTrace();
                            }
                        } else
                            rechargeResult.setIatTimeStamp(null);
                        rechargeResult.setIatTrxId(creditResponseMap.get("IatTrxId"));
                        rechargeResult.setMessage(creditResponseMap.get("Message"));
                        rechargeResult.setSNwTrxId(creditResponseMap.get("SNwTrxId"));
                        transactionStatus = creditResponseMap.get("Status");
                        if (!IATUtil.isNullString(transactionStatus)) {
                            int transStatus = Integer.parseInt(transactionStatus);
                            rechargeResult.setStatus(transStatus);
                        } else
                            rechargeResult.setStatus(205);
                    } else {
                        _log.error("rechargeRequest", "CreditResponse Not Received from IAH HUB");
                        rechargeResult = null;
                    }
                } else {
                    _log.error("rechargeRequest", "CreditResponse Not Received from IAH HUB");
                    rechargeResult = null;
                }
                soapConnection.close();
            } catch (UnsupportedOperationException e) {
                _log.error("rechargeRequest", "UnsupportedOperationException");
                e.getMessage();
                e.printStackTrace();
            }
        } catch (SOAPException soapException) {
            _log.error("rechargeRequest", "SOAPException");
            soapException.getMessage();
            soapException.printStackTrace();
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("rechargeRequest :: ", "rechargeRequest Exited with RechargeResult::=" + rechargeResult.toString());
        }
        return rechargeResult;
    }

    /**
     * @Method:- checkStatusRequest This is used to call the checkStatusRequest
     *           Service
     *           on IAH platform
     * @param CheckStatusParam
     * @param AuthHeader
     * @return CheckStatusResult
     * @author babu.kunwar
     */

    public CheckStatusResult checkStatusRequest(CheckStatusParam checkStatusParam, AuthHeader authHeader) {
        if (_log.isDebugEnabled())
            _log.debug("checkStatusRequest :: ", "checkStatusRequest Entered with checkStatusParam::=" + checkStatusParam.toString() + " authHeader::+" + authHeader.toString());

        CheckStatusResult checkStatusResult = null;
        URL endPointURL = null;
        SOAPMessage message = null;
        IATRequestResponseFormatter iatRequestResponseFormatter = null;
        HashMap<String, String> checkStatusResponseMap = null;
        try {
            /*
             * Creating the Header for the request
             * Using SAAJ JAX-RPC Implementation for request
             * Babu Kunwar
             * Creating an empty header for rechargeRequest
             * You can create non-empty body by using the line of code
             * SOAPHeader header = message.getSOAPHeader();
             */
            MessageFactory factory = MessageFactory.newInstance();
            message = factory.createMessage();
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope checkStatusRequestEnvelope = soapPart.getEnvelope();
            SOAPBody checkStatusRequestBody = checkStatusRequestEnvelope.getBody();
            SOAPHeader checkStatusRequestHeader = checkStatusRequestEnvelope.getHeader();
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            Name authHeaderName = soapFactory.createName("authHeader", "", "http://com/wha/iah/pretups/ws");
            Name authHeaderParamName = soapFactory.createName("authHeaderParam");
            Name userName = soapFactory.createName("UserName", "", "java:com.wha.iah.pretups.ws");
            Name password = soapFactory.createName("Password", "", "java:com.wha.iah.pretups.ws");

            javax.xml.soap.SOAPHeaderElement chkStatusReqHeaderElemnt = checkStatusRequestHeader.addHeaderElement(authHeaderName);
            SOAPElement authHeaderParamElement = chkStatusReqHeaderElemnt.addChildElement(authHeaderParamName);
            SOAPElement userNameElement = authHeaderParamElement.addChildElement(userName);
            userNameElement.addTextNode(authHeader.getAuthHeaderParam().getUserName());
            SOAPElement passwordElement = authHeaderParamElement.addChildElement(password);
            passwordElement.addTextNode(authHeader.getAuthHeaderParam().getPassword());

            /*
             * Creating the Body for the request
             * Using SAAJ JAX-RPC Implementation for request
             * Babu Kunwar
             * Creating an empty body for rechargeRequest
             * You can create non-empty body by using the line of code
             * SOAPBody body = message.getSOAPBody();
             */
            Name checkStatusRequest = soapFactory.createName("checkStatusRequest", "", "http://com/wha/iah/pretups/ws");
            Name checkStatusParamName = soapFactory.createName("checkStatusParam");
            Name SNwTrxIdName = soapFactory.createName("SNwTrxId", "", "java:com.wha.iah.pretups.ws");
            Name IatTrxId = soapFactory.createName("IatTrxId", "", "java:com.wha.iah.pretups.ws");
            Name SNwIdName = soapFactory.createName("SNwId", "", "java:com.wha.iah.pretups.ws");
            SOAPBodyElement soapBodyElement1 = checkStatusRequestBody.addBodyElement(checkStatusRequest);
            SOAPElement soapBodyElement2 = soapBodyElement1.addChildElement(checkStatusParamName);
            SOAPElement SNwTrxIdElement = soapBodyElement2.addChildElement(SNwTrxIdName);
            SNwTrxIdElement.addTextNode(checkStatusParam.getSNwTrxId());
            SOAPElement iatTrxIdElement = soapBodyElement2.addChildElement(IatTrxId);
            if (BTSLUtil.isNullString(checkStatusParam.getIatTrxId()))
                iatTrxIdElement.addTextNode("");
            else
                iatTrxIdElement.addTextNode(checkStatusParam.getIatTrxId());
            SOAPElement SNwIdElement = soapBodyElement2.addChildElement(SNwIdName);
            SNwIdElement.addTextNode(checkStatusParam.getSNwId());
            ByteArrayOutputStream writeCheckRequestToString = new ByteArrayOutputStream();
            /*
             * Printing the XML Request Message sent to IAH
             */
            try {
                if (_log.isDebugEnabled())
                    _log.debug("\n" + "checkStatusRequest :: ", "After Invoking the Call checkStatusRequest::=");

                message.writeTo(writeCheckRequestToString);
                String checkStatusRequestStringMessage = new String(writeCheckRequestToString.toByteArray());
                System.out.println("\n");
                System.out.println("\n");
                System.out.println("checkStatusRequestStringMessage ::=" + checkStatusRequestStringMessage);
                System.out.println("\n");
                System.out.println("\n");
            } catch (IOException e1) {
                e1.getMessage();
                e1.printStackTrace();
            }
            /*
             * Getting the SOAPConnection to send the request for recharge
             */
            try {
                SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
                SOAPConnection soapConnection = soapConnectionFactory.createConnection();

                try {
                    endPointURL = new URL(_endURL);
                } catch (MalformedURLException e) {
                    _log.error("checkStatusRequest", "MalformedURLException");
                    e.getMessage();
                    e.printStackTrace();
                }
                SOAPMessage checkStatusResponse = soapConnection.call(message, endPointURL);
                soapConnection.close();
                ByteArrayOutputStream writeCheckResponseToString = new ByteArrayOutputStream();
                checkStatusResult = new CheckStatusResult();
                try {
                    if (_log.isDebugEnabled())
                        _log.debug("\n" + "checkStatusRequest :: ", "After Invoking the Call checkStatusRequest::=");

                    checkStatusResponse.writeTo(writeCheckResponseToString);
                    String checkStatusResponseStringMessage = new String(writeCheckResponseToString.toByteArray());
                    System.out.println("\n");
                    System.out.println("\n");
                    System.out.println("\n");
                    System.out.println("checkStatusResponseStringMessage ::=" + checkStatusResponseStringMessage);
                    System.out.println("\n");
                    System.out.println("\n");
                    System.out.println("\n");
                    System.out.println("\n");

                    if (!IATUtil.isNullString(checkStatusResponseStringMessage)) {
                        try {
                            iatRequestResponseFormatter = new IATRequestResponseFormatter();
                            checkStatusResponseMap = iatRequestResponseFormatter.parseCheckStatusResponse(checkStatusResponseStringMessage);
                            if (checkStatusResponseMap.size() > 0 && checkStatusResponseMap != null) {
                                String exchngeRate = checkStatusResponseMap.get("ExchangeRate");
                                if (!IATUtil.isNullString(exchngeRate)) {
                                    double exchangeRate = Double.parseDouble(exchngeRate);
                                    checkStatusResult.setExchangeRate(exchangeRate);
                                }
                                String iatFee = checkStatusResponseMap.get("Fees");
                                if (!IATUtil.isNullString(iatFee)) {
                                    double iatFees = Double.parseDouble(iatFee);
                                    checkStatusResult.setFees(iatFees);
                                }
                                if (IATUtil.isNullString(checkStatusResponseMap.get("IatReasonCode"))) {
                                    int iatReasonCode = Integer.parseInt(checkStatusResponseMap.get("IatReasonCode"));
                                    checkStatusResult.setIatReasonCode(iatReasonCode);
                                }
                                checkStatusResult.setIatReasonMessage(checkStatusResponseMap.get("IatReasonMessage"));
                                String receivedAmt = checkStatusResponseMap.get("IatReceivedAmount");
                                if (!IATUtil.isNullString(receivedAmt)) {
                                    double iatRecAmt = Double.parseDouble(receivedAmt);
                                    checkStatusResult.setIatReceivedAmount(iatRecAmt);
                                }
                                String iatLevel = checkStatusResponseMap.get("Level");
                                if (!IATUtil.isNullString(iatLevel)) {
                                    int levelIAT = Integer.parseInt(iatLevel);
                                    checkStatusResult.setLevel(levelIAT);
                                }
                                String iatProvRatio = checkStatusResponseMap.get("ProvRatio");
                                if (!IATUtil.isNullString(iatProvRatio)) {
                                    double iatProvRatio1 = Double.parseDouble(iatProvRatio);
                                    checkStatusResult.setProvRatio(iatProvRatio1);
                                }

                                String iatRBonus = checkStatusResponseMap.get("RBonus");
                                if (!IATUtil.isNullString(iatRBonus)) {
                                    double iatRBonus1 = Double.parseDouble(iatRBonus);
                                    checkStatusResult.setRBonus(iatRBonus1);
                                }

                                String iatRecipientReceivedAmount = checkStatusResponseMap.get("RecipientReceivedAmount");
                                if (!IATUtil.isNullString(iatRecipientReceivedAmount)) {
                                    double iatRecipientReceivedAmount1 = Double.parseDouble(iatRecipientReceivedAmount);
                                    checkStatusResult.setRecipientReceivedAmount(iatRecipientReceivedAmount1);
                                }
                                checkStatusResult.setRNwId(checkStatusResponseMap.get("RNwId"));
                                checkStatusResult.setRNwReasonCode(checkStatusResponseMap.get("RNwReasonCode"));
                                checkStatusResult.setRNwReasonMessage(checkStatusResponseMap.get("RNwReasonMessage"));

                                String iatRPfReceivedAmount = checkStatusResponseMap.get("RPfReceivedAmount");
                                if (!IATUtil.isNullString(iatRPfReceivedAmount)) {
                                    double iatRPfReceivedAmount1 = Double.parseDouble(iatRPfReceivedAmount);
                                    checkStatusResult.setRPfReceivedAmount(iatRPfReceivedAmount1);
                                }

                                String iatCheckReqStatus = checkStatusResponseMap.get("Status");
                                if (!IATUtil.isNullString(iatCheckReqStatus)) {
                                    int iatStatus = Integer.parseInt(iatCheckReqStatus);
                                    checkStatusResult.setStatus(iatStatus);
                                }
                            } else {
                                _log.error("checkStatusRequest", "CreditResponseMap is null after parsing response");
                                checkStatusResult = null;
                            }

                        } catch (Exception e) {
                            _log.error("checkStatusRequest", "got exception in parsing the response received from IAH HUB");
                            e.getMessage();
                            e.printStackTrace();
                        }
                    } else {
                        _log.error("checkStatusRequest", "CreditResponse Not Received from IAH HUB");
                        checkStatusResult = null;
                    }
                } catch (IOException e1) {
                    e1.getMessage();
                    e1.printStackTrace();
                }
            } catch (UnsupportedOperationException e) {
                _log.error("checkStatusRequest", "UnsupportedOperationException");
                e.getMessage();
                e.printStackTrace();
            }
        } catch (SOAPException soapException) {
            _log.error("checkStatusRequest", "SOAPException");
            soapException.getMessage();
            soapException.printStackTrace();
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("checkStatusRequest ::", "checkStatusRequest Exited with checkStatusResult::=" + checkStatusResult.toString());
        }
        return checkStatusResult;
    }

    public boolean setRequestParameters(HashMap<String, String> p_requestMap) {
        if (_log.isDebugEnabled())
            _log.debug("setRequestParameters :: ", "setRequestParameters Entered:: p_requestMap::=" + p_requestMap);

        boolean isRequestParamSet = true;
        if (p_requestMap.get("IP_LOCAL_HOSTNAME").equals("") || InterfaceUtil.isNullString(p_requestMap.get("IP_LOCAL_HOSTNAME"))) {
            _log.error("setRequestParameters", "IP_LOCAL_HOSTNAME is coming null from IN's setInterfaceParameters method");
            isRequestParamSet = false;
        } else
            _endURL = p_requestMap.get("IP_LOCAL_HOSTNAME");

        if (_log.isDebugEnabled())
            _log.debug("setRequestParameters :: ", "setRequestParameters Exited:: _endURL::=" + _endURL);

        return isRequestParamSet;
    }
}
