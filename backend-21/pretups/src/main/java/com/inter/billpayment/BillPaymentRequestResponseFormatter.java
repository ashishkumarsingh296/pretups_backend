package com.inter.billpayment;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class BillPaymentRequestResponseFormatter {

    /**
	 * 
	 */
    public BillPaymentRequestResponseFormatter() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static Log _log = LogFactory.getLog(BillPaymentRequestResponseFormatter.class.getName());
    private static int txncounter = 0;

    /**
     * @author vipan.kumar
     * @param p_map
     * @return
     */

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param responseStr
     * @return
     * @throws Exception
     */
    public HashMap parseBillPaymentResponse(String responseStr) throws Exception {
        _log.debug("parseBillPaymentResponse ", " Entered responseStr = " + responseStr);
        HashMap map = null;
        try {
            if (BTSLUtil.isNullString(responseStr)) {
                _log.debug("BillPaymentRequestResponseFormatter.parseBillPaymentResponse()", "Blank Response from BillPayment IN Interface");
            } else {
                map = new HashMap();
                int index = responseStr.indexOf("<resultcode>");
                if (index != -1) {
                    String resultCode = responseStr.substring(index + "<resultcode>".length(), responseStr.indexOf("</resultcode>", index));
                    map.put("resp_resultCode", resultCode);
                }
                index = responseStr.indexOf("<transactionid>");
                if (index != -1) {
                    String resp_transactionSN = responseStr.substring(index + "<transactionid>".length(), responseStr.indexOf("</transactionid>", index));
                    map.put("resp_transactionSN", resp_transactionSN);
                }
            }
        } catch (Exception e) {
            _log.debug("BillPaymentRequestResponseFormatter.parseBillPaymentResponse()", "Exception while parsing the Interface Response");
            e.printStackTrace();
            throw e;
        }
        _log.debug("parseBillPaymentResponse ", " Exit responseMap = " + map);
        return map;
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param responseStr
     * @return
     * @throws Exception
     */
    public HashMap parseDepositResponse(String responseStr) throws Exception {
        _log.debug("parseDepositResponse ", " Entered responseStr = " + responseStr);
        HashMap map = null;
        try {
            if (BTSLUtil.isNullString(responseStr)) {
                _log.debug("BillPaymentRequestResponseFormatter.parseDepositResponse()", "Blank Response from BillPayment IN Interface");
            } else {
                map = new HashMap();
                int index = responseStr.indexOf("<resultcode>");
                if (index != -1) {
                    String resultCode = responseStr.substring(index + "<resultcode>".length(), responseStr.indexOf("</resultcode>", index));
                    map.put("resp_resultCode", resultCode);
                }
                index = responseStr.indexOf("<transactionid>");
                if (index != -1) {
                    String resp_transactionSN = responseStr.substring(index + "<transactionid>".length(), responseStr.indexOf("</transactionid>", index));
                    map.put("resp_transactionSN", resp_transactionSN);
                }
            }
        } catch (Exception e) {
            _log.debug("BillPaymentRequestResponseFormatter.parseDepositResponse()", "Exception while parsing the Interface Response");
            e.printStackTrace();
            throw e;
        }
        _log.debug("parseDepositResponse ", " Exit responseMap = " + map);
        return map;
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param responseStr
     * @return
     * @throws Exception
     */
    public HashMap parseCreditAdjustResponse(String responseStr) throws Exception {
        _log.debug("parseCreditAdjustResponse ", " Entered responseStr = " + responseStr);
        HashMap map = null;
        try {
            if (BTSLUtil.isNullString(responseStr)) {
                _log.debug("parseCreditAdjustResponse()", "Blank Response from BillPayment IN Interface");
            } else {
                map = new HashMap();
                int index = responseStr.indexOf("<resultcode>");
                if (index != -1) {
                    String resultCode = responseStr.substring(index + "<resultcode>".length(), responseStr.indexOf("</resultcode>", index));
                    map.put("resp_resultCode", resultCode);
                }
                index = responseStr.indexOf("<transactionid>");
                if (index != -1) {
                    String resp_transactionSN = responseStr.substring(index + "<transactionid>".length(), responseStr.indexOf("</transactionid>", index));
                    map.put("resp_transactionSN", resp_transactionSN);
                }
            }
        } catch (Exception e) {
            _log.debug("parseCreditAdjustResponse()", "Exception while parsing the Interface Response");
            e.printStackTrace();
            throw e;
        }
        _log.debug("parseCreditAdjustResponse ", " Exit responseMap = " + map);
        return map;
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param responseStr
     * @return
     * @throws Exception
     */
    public HashMap parseDebitAdjustResponse(String responseStr) throws Exception {
        _log.debug("parseDebitAdjustResponse ", " Entered responseStr = " + responseStr);
        HashMap map = null;
        try {
            if (BTSLUtil.isNullString(responseStr)) {
                _log.debug("parseCreditAdjustResponse()", "Blank Response from BillPayment IN Interface");
            } else {
                map = new HashMap();
                int index = responseStr.indexOf("<resultcode>");
                if (index != -1) {
                    String resultCode = responseStr.substring(index + "<resultcode>".length(), responseStr.indexOf("</resultcode>", index));
                    map.put("resp_resultCode", resultCode);
                }
                index = responseStr.indexOf("<transactionid>");
                if (index != -1) {
                    String resp_transactionSN = responseStr.substring(index + "<transactionid>".length(), responseStr.indexOf("</transactionid>", index));
                    map.put("resp_transactionSN", resp_transactionSN);
                }
            }
        } catch (Exception e) {
            _log.debug("parseCreditAdjustResponse()", "Exception while parsing the Interface Response");
            e.printStackTrace();
            throw e;
        }
        _log.debug("parseCreditAdjustResponse ", " Exit responseMap = " + map);
        return map;
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param responseStr
     * @return
     * @throws Exception
     */
    public HashMap parseValidateResponse(String responseStr) throws Exception {
        _log.debug("parseValidateResponse ", " Entered responseStr = " + responseStr);
        HashMap map = null;
        try {
            if (BTSLUtil.isNullString(responseStr)) {
                _log.debug("BillPaymentRequestResponseFormatter.parseValidateResponse()", "Blank Response from BillPayment IN Interface");
            } else {
                map = new HashMap();
                int index = responseStr.indexOf("<resultcode>");
                if (index != -1) {
                    String resultCode = responseStr.substring(index + "<resultcode>".length(), responseStr.indexOf("</resultcode>", index));
                    map.put("resp_resultCode", resultCode);
                }
                index = responseStr.indexOf("<transactionid>");
                if (index != -1) {
                    String resp_transactionSN = responseStr.substring(index + "<transactionid>".length(), responseStr.indexOf("</transactionid>", index));
                    map.put("resp_transactionSN", resp_transactionSN);
                }
                index = responseStr.indexOf("<creditlimit>");
                if (index != -1) {
                    String creditLimit = responseStr.substring(index + "<creditlimit>".length(), responseStr.indexOf("</creditlimit>", index));
                    map.put("CREDIT_LIMIT", creditLimit);
                }

                index = responseStr.indexOf("<svcstartdate>");
                if (index != -1) {
                    String serviceStartDate = responseStr.substring(index + "<svcstartdate>".length(), responseStr.indexOf("</svcstartdate>", index));
                    map.put("SERVICE_START_DATE", serviceStartDate);
                }
                index = responseStr.indexOf("<unbiledinfo>");
                if (index != -1) {
                    String unBilledInfo = responseStr.substring(index + "<unbiledinfo>".length(), responseStr.indexOf("</unbiledinfo>", index));
                    map.put("UNBILLED_INFO", unBilledInfo);
                }
            }
        } catch (Exception e) {
            _log.debug("BillPaymentRequestResponseFormatter.parseBillPaymentResponse()", "Exception while parsing the Interface Response");
            e.printStackTrace();
            throw e;
        }
        _log.debug("parseValidateResponse ", " Exit responseMap = " + map);
        return map;
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param p_map
     * @return
     * @throws Exception
     */
    public String generatePayBillRequest(HashMap p_map) throws Exception {
        _log.debug("generatePayBillRequest ", " Entered p_map = " + p_map);
        StringBuffer sbf = null;
        try {
            if (txncounter >= 10)
                txncounter = 0;
            sbf = new StringBuffer(1028);
            sbf.append("<request user='");
            sbf.append((String) p_map.get("SERVICE_USER"));
            sbf.append("'>");
            sbf.append("<serviceno>");
            sbf.append((String) p_map.get("MSISDN"));
            sbf.append("</serviceno>");
            sbf.append("<amount>");
            sbf.append((String) p_map.get("INTERFACE_ROUND_AMOUNT"));
            sbf.append("</amount>");
            sbf.append("<transactionid>");
            sbf.append(getINTransactionId((String) p_map.get("IN_TXN_ID")));
            sbf.append("</transactionid>");
            sbf.append("</request>");

        } catch (Exception e) {
            _log.error("generateBillRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        }
        _log.debug("generatePayBillRequest ", " Exit PayBillRequest = " + sbf);
        return sbf.toString();
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param p_map
     * @return
     * @throws Exception
     */
    public String generateCreditAdjustRequest(HashMap p_map) throws Exception {
        _log.debug("generateCreditAdjustRequest ", " Entered p_map = " + p_map);
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<request user = '");
            sbf.append((String) p_map.get("SERVICE_USER"));
            sbf.append("'>");
            sbf.append("<serviceno>");
            sbf.append((String) p_map.get("MSISDN"));
            sbf.append("</serviceno>");
            sbf.append("<amount>");
            sbf.append((String) p_map.get("INTERFACE_ROUND_AMOUNT"));
            sbf.append("</amount>");
            sbf.append("<transactionid>");
            sbf.append(getINTransactionId((String) p_map.get("IN_TXN_ID")));
            sbf.append("</transactionid>");
            sbf.append("</request>");
        } catch (Exception e) {
            _log.error("generateCreditAdjustRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        }
        _log.debug("generateCreditAdjustRequest ", " Exit CreditAdjustRequest = " + sbf);
        return sbf.toString();
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param p_map
     * @return
     * @throws Exception
     */
    public String generateDebitAdjustRequest(HashMap p_map) throws Exception {
        _log.debug("generateDebitAdjustRequest ", " Entered p_map = " + p_map);
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<request user = '");
            sbf.append((String) p_map.get("SERVICE_USER"));
            sbf.append("'>");
            sbf.append("<serviceno>");
            sbf.append((String) p_map.get("MSISDN"));
            sbf.append("</serviceno>");
            sbf.append("<amount>");
            sbf.append((String) p_map.get("INTERFACE_ROUND_AMOUNT"));
            sbf.append("</amount>");
            sbf.append("<transactionid>");
            sbf.append(getINTransactionId((String) p_map.get("IN_TXN_ID")));
            sbf.append("</transactionid>");
            sbf.append("</request>");
        } catch (Exception e) {
            _log.error("generateDebitAdjustRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        }
        _log.debug("generateDebitAdjustRequest ", " Exit DebitAdjustRequest = " + sbf);
        return sbf.toString();
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param p_map
     * @return
     * @throws Exception
     */
    public String generateValidateRequest(HashMap p_map) throws Exception {
        _log.debug("generateValidateRequest ", " Entered p_map = " + p_map);
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<request user = '");
            sbf.append((String) p_map.get("SERVICE_USER"));
            sbf.append("'>");
            sbf.append("<serviceno>");
            sbf.append((String) p_map.get("MSISDN"));
            sbf.append("</serviceno>");
            sbf.append("<transactionid>");
            sbf.append(getINTransactionId((String) p_map.get("IN_TXN_ID")));
            sbf.append("</transactionid>");
            sbf.append("</request>");

        } catch (Exception e) {
            _log.error("generateValidateRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        }
        _log.debug("generateValidateRequest ", " Exit ValidateRequest = " + sbf.toString());
        return sbf.toString();
    }

    /**
     * @author vipan.kumar
     * @date 27 Oct 2010
     * @param p_requestMap
     * @param p_responseMap
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean validateResponseMap(HashMap p_requestMap, HashMap p_responseMap, String p_requestedService) throws BTSLBaseException, Exception {

        _log.debug("validateResponseMap() ", "p_requestMap = " + p_requestMap);
        if (p_responseMap == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
            return false;
        } else if (BTSLUtil.isNullString((String) p_responseMap.get("resp_resultCode"))) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "SuccessFull Response from BillPayment Response");
            return false;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("1")) {
            if (!validServiceStartDate((String) p_responseMap.get("SERVICE_START_DATE"), (String) p_requestMap.get("SERVICE_START_DATE_DIFF"))) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid Service Start Date");
                throw new BTSLBaseException(InterfaceErrorCodesI.FAIL);
            }
            return true;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-001")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid Service Number");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_SERVICE_NO);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-002")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Duplicate Transaction Id");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DUPLICATE_TXN_ID);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-004")) {
            if (p_requestedService.equalsIgnoreCase(PretupsI.SERVICE_TYPE_P2PCREDITRECHARGE)) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown User");
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown User");
                throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_USER);
            }
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-005")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Database Connection Error");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DATABASE_CONN_ERROR);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-100")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown exception");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_EXCEPTION);
        } else {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[validate]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown Error Code");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_RESPONSE);
        }

    }

    /**
     * @author vipan.kumar
     * @date 27 Oct 2010
     * @param p_requestMap
     * @param p_responseMap
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean validatePayBillResponseMap(HashMap p_requestMap, HashMap p_responseMap) throws BTSLBaseException, Exception {

        if (p_responseMap == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
            return false;
        } else if (BTSLUtil.isNullString((String) p_responseMap.get("resp_resultCode"))) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "SuccessFull Response from BillPayment Response");
            return false;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("1")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "SuccessFull Response from BillPayment Response");
            return true;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-001")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid Service Number");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_SERVICE_NO);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-002")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Duplicate Transaction Id");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DUPLICATE_TXN_ID);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-003")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid value in amount field");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_VALUE_AMOUNT_FIELD);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-004")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown User");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_USER);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-005")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Database Connection Error");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DATABASE_CONN_ERROR);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-100")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown exception");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_EXCEPTION);
        } else {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown Error Code");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_RESPONSE);
        }

    }

    /**
     * @author vipan.kumar
     * @date 27 Oct 2010
     * @param p_requestMap
     * @param p_responseMap
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean validateAdjustResponseMap(HashMap p_requestMap, HashMap p_responseMap) throws BTSLBaseException, Exception {

        if (p_responseMap == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
            return false;
        } else if (BTSLUtil.isNullString((String) p_responseMap.get("resp_resultCode"))) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "SuccessFull Response from BillPayment Response");
            return false;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("1")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "SuccessFull Response from BillPayment Response");
            return true;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-001")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid Service Number");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_SERVICE_NO);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-002")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Duplicate Transaction Id");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DUPLICATE_TXN_ID);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-003")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Insufficient balance");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INSUFFICIENT_BALANCE);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-004")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown User");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_USER);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-005")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Database Connection Error");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DATABASE_CONN_ERROR);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-006")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid destination mobile number");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DESTINATION_MOBILENO);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-100")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown exception");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_EXCEPTION);
        } else {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[adjust]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown Error Code");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_RESPONSE);
        }
    }

    /**
     * @date 25 Oct 2010
     * @author vipan.kumar
     * @param p_map
     * @return
     * @throws Exception
     */
    public String generateDepositRequest(HashMap p_map) throws Exception {
        _log.debug("generateDepositRequest ", " Entered p_map = " + p_map);
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<request user = '");
            sbf.append((String) p_map.get("SERVICE_USER"));
            sbf.append("'>");
            sbf.append("<serviceno>");
            sbf.append((String) p_map.get("MSISDN"));
            sbf.append("</serviceno>");
            sbf.append("<amount>");
            sbf.append((String) p_map.get("INTERFACE_ROUND_AMOUNT"));
            sbf.append("</amount>");
            sbf.append("<transactionid>");
            sbf.append(getINTransactionId((String) p_map.get("IN_TXN_ID")));
            sbf.append("</transactionid>");
            sbf.append("</request>");

        } catch (Exception e) {
            _log.error("generateDepositRequest", "Exception e: " + e);
            e.printStackTrace();
            throw e;
        }
        _log.debug("generateDepositRequest ", " Exit DepositRequest = " + sbf.toString());
        return sbf.toString();
    }

    /**
     * @author vipan.kumar
     * @date 27 Oct 2010
     * @param p_requestMap
     * @param p_responseMap
     * @return
     * @throws BTSLBaseException
     * @throws Exception
     */
    public boolean validateDeposiResponseMap(HashMap p_requestMap, HashMap p_responseMap) throws BTSLBaseException, Exception {

        if (p_responseMap == null) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Exception in payment response");
            return false;
        } else if (BTSLUtil.isNullString((String) p_responseMap.get("resp_resultCode"))) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "SuccessFull Response from BillPayment Response");
            return false;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("1")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "SuccessFull Response from BillPayment Response");
            return true;
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-001")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid Service Number");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_SERVICE_NO);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-002")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Duplicate Transaction Id");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DUPLICATE_TXN_ID);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-003")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Invalid value in amount field");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_VALUE_AMOUNT_FIELD);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-004")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown User");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_USER);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-005")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Database Connection Error");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_DATABASE_CONN_ERROR);
        } else if (((String) p_responseMap.get("resp_resultCode")).equalsIgnoreCase("-100")) {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown exception");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_UNKNOWN_EXCEPTION);
        } else {
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BillPaymentINHandler[credit]", (String) p_requestMap.get("TRANSACTION_ID"), (String) p_requestMap.get("MSISDN"), (String) p_requestMap.get("NETWORK_CODE"), "Unknown Error Code");
            throw new BTSLBaseException(InterfaceErrorCodesI.PBP_INVALID_RESPONSE);
        }
    }

    private boolean validServiceStartDate(String p_date, String p_serviceStartDateDiff) {
        _log.debug("validServiceStartDate", " p_date = " + p_date + " p_serviceStartDateDiff = " + p_serviceStartDateDiff);
        java.util.Date date1 = null;
        if (InterfaceUtil.isNullString(p_date))
            return false;
        else {
            try {
                date1 = BTSLUtil.getDateFromDateString(p_date.replace("-", "/"), PretupsI.DATE_FORMAT);
                int noOfDay = BTSLUtil.getDifferenceInUtilDates(date1, new java.util.Date());
                _log.debug("validServiceStartDate", " Difference in days = " + noOfDay);
                if (noOfDay > Integer.parseInt(p_serviceStartDateDiff))
                    return false;
                else
                    return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

    }

    private String getINTransactionId(String p_transactionId) {
        _log.debug("getINTransactionId", " p_transactionId = " + p_transactionId);

        try {
            p_transactionId = p_transactionId.replace(".", "") + txncounter++;
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return p_transactionId;

    }
}
