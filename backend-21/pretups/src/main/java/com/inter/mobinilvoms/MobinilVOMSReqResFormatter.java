package com.inter.mobinilvoms;

/*
 * @(#)MobinilVOMSReqResFormatter.java
 * ----------------------------------------------------------------------
 * Name Date History
 * ------------------------------------------------------------------------
 * Vinay Kumar Singh 22/11/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * Formatter class for Voucher Management System.
 */
import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class MobinilVOMSReqResFormatter {

    // Get the logger object, which is used to write different types of logs.
    public static Log _log = LogFactory.getLog("MobinilVOMSReqResFormatter".getClass().getName());

    /**
     * Based on the action value, a method is referenced to generate the XML
     * string for corresponding request
     * 
     * @param int p_action
     * @param HashMap
     *            p_requestMap
     * @return String responseStr
     */
    protected String generateRequest(int p_action, HashMap<String, String> p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action=" + p_action);
        String requestStr = null;
        try {
            p_requestMap.put("action", String.valueOf(p_action));

            switch (p_action) {
            case MobinilVOMSI.ACTION_GET_VOUCHER_DETAILS: {
                requestStr = generateGetVoucherDetailsRequest(p_requestMap);
                break;
            }
            case MobinilVOMSI.ACTION_UPDATE_VOUCHER_STATE: {
                requestStr = generateUpdateVoucherStateRequest(p_requestMap);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exiting for p_action=" + p_action);
        }
        return requestStr;
    }// end of generateRequest

    /**
     * Based on the action value, a method is referenced to parse the XML string
     * for corresponding response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap<String,String> responseMap
     */
    protected HashMap<String, String> parseResponse(int p_action, String p_responseStr, String p_interfaceID) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action=" + p_action);
        HashMap<String, String> responseMap = null;
        try {
            switch (p_action) {
            case MobinilVOMSI.ACTION_GET_VOUCHER_DETAILS: {
                responseMap = parseGetVoucherDetailsResponse(p_responseStr, p_interfaceID);
                break;
            }
            case MobinilVOMSI.ACTION_UPDATE_VOUCHER_STATE: {
                responseMap = parseUpdateVoucherStateResponse(p_responseStr, p_interfaceID);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting for p_action=" + p_action);
        }
        return responseMap;
    }// end of parseResponse

    /**
     * This Method generate get voucher detail request
     * The message GetVoucherDetails is used in order to obtain detailed
     * information on an individual voucher.
     * (1) One of the elements serialNumber and activationCode must be present.
     * In the case that both elements are present, an error will be returned.
     * (2) This element is mandatory if Mobile Virtual Network Operator
     * functionality is activated;
     * otherwise, the element is optional.
     * 
     * @param p_requestMap
     *            HashMap
     * @throws Exception
     * @return requestStr
     */
    private String generateGetVoucherDetailsRequest(HashMap<String, String> p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetVoucherDetailsRequest", "Entered with p_requestMap=" + p_requestMap);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<methodCall>");
            sbf.append("<methodName>GetVoucherDetails</methodName>");

            // Serial Number
            sbf.append("<params><param><value><struct>");
            sbf.append("<member>");
            sbf.append("<name>serialNumber</name>");
            sbf.append("<value><string>" + p_requestMap.get("SERIAL_NUMBER") + "</string></value>");
            sbf.append("</member>");
            // Transaction ID
            /*
             * sbf.append("<member>");
             * sbf.append("<name>networkOperatorId</name>");
             * sbf.append("<value><string>"+p_requestMap.get("IN_TXN_ID")+
             * "</string></value>");
             * sbf.append("</member>");
             */
            sbf.append("</struct></value></param></params>");
            sbf.append("</methodCall>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            _log.error("generateGetVoucherDetailsRequest", "Exception e=" + e);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetVoucherDetailsRequest", "Exiting with requestStr=" + requestStr);
        }
        return requestStr;
    }// end of generateGetVoucherDetailsRequest

    /**
     * This Method parse GetVoucherDetails Response
     * The response indicates if the GetVoucherDetails request was carried out
     * successfully or not.
     * (1) The serial number is included in the response when the request
     * includes the activation code.
     * 
     * @param String
     *            p_responseStr
     * @throws Exception
     * @return HashMap
     */
    private HashMap<String, String> parseGetVoucherDetailsResponse(String p_responseStr, String p_interfaceID) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseGetVoucherDetailsResponse", "Entered p_responseStr=" + p_responseStr);
        HashMap<String, String> responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap<String, String>();

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!InterfaceUtil.isNullString(responseCode) && !MobinilVOMSI.RESULT_OK.equals(responseCode))
                    return responseMap;
            }
            /*
             * The activationCode parameter is the unique secret code which is
             * used to refill the account.
             * The activation code may have leading zeros.
             */
            indexStart = p_responseStr.indexOf("<member><name>activationCode", indexEnd);
            tempIndex = p_responseStr.indexOf("activationCode", indexStart);
            if (tempIndex > 0) {
                String activationCode = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("activationCode", activationCode.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
            /*
             * The batchId parameter indicates what batch a voucher belongs to.
             * The batchId is assigned when vouchers are generated.
             */
            indexStart = p_responseStr.indexOf("<member><name>batchId", indexEnd);
            tempIndex = p_responseStr.indexOf("batchId", indexStart);
            if (tempIndex > 0) {
                String batchId = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("batchId", batchId.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
            /*
             * The expiryDate parameter is used to identify the last date when
             * the voucher will be usable in the system.
             * Only the date information will be considered by this parameter.
             * The time and time-zone should be set to all zeroes, and will be
             * ignored.
             */
            indexStart = p_responseStr.indexOf("<member><name>expiryDate", indexEnd);
            tempIndex = p_responseStr.indexOf("expiryDate", indexStart);
            if (tempIndex > 0) {
                String expiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("expiryDate", getDateString(expiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
            /*
             * The state parameter is used to represent the state of a voucher,
             * as it currently is.
             * 0 available
             * 1 used
             * 2 damaged
             * 3 stolen/missing
             * 4 pending
             * 5 unavailable
             * 6 reserved
             * Only vouchers with state 0 and 5 should be processed.
             */
            indexStart = p_responseStr.indexOf("<member><name>state", indexEnd);
            tempIndex = p_responseStr.indexOf("state", indexStart);
            if (tempIndex > 0) {
                String state = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("state", state.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
            /*
             * The value parameter is used to specify the actual value of the
             * voucher in currency units.
             * The value is formatted as a numeric string. No decimal separator
             * is included.
             * The amount is expressed in the lowest denomination of the
             * specified currency.
             * For example a USD 100 value is represented as 10000.
             */
            indexStart = p_responseStr.indexOf("<member><name>value", indexEnd);
            tempIndex = p_responseStr.indexOf("value", indexStart);
            if (tempIndex > 0) {
                String value = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("value", value.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseGetVoucherDetailsResponse", "Exception e=" + e.getMessage());
            throw e;
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetVoucherDetailsResponse", "Exited with responseMap=" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseGetVoucherDetailsResponse

    /**
     * The message UpdateVoucherState is used to update the voucher state.
     * This method will be used only when the state received from
     * GetVoucherDetails request is 5 (Unavailable).
     * The requested state change pointed out by the .newState. parameter must
     * follow the state model with
     * allowed state transitions defined for the voucher server.
     * 
     * @param p_requestMap
     *            HashMap<String,String>
     * @throws Exception
     * @return requestStr
     */
    private String generateUpdateVoucherStateRequest(HashMap<String, String> p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateUpdateVoucherStateRequest", "Entered with p_requestMap=" + p_requestMap);
        String requestStr = null;
        StringBuffer sbf = null;
        try {
            sbf = new StringBuffer(1028);
            sbf.append("<?xml version=\"1.0\"?>");
            sbf.append("<methodCall>");
            sbf.append("<methodName>UpdateVoucherState</methodName>");
            sbf.append("<params><param><value><struct>");
            // Serial Number
            sbf.append("<member>");
            sbf.append("<name>serialNumber</name>");
            sbf.append("<value><string>" + p_requestMap.get("SERIAL_NUMBER") + "</string></value>");
            sbf.append("</member>");
            // New state
            sbf.append("<member>");
            sbf.append("<name>newState</name>");
            sbf.append("<value><i4>0</i4></value>");
            sbf.append("</member>");
            // Old state 5 Unavailable
            sbf.append("<member>");
            sbf.append("<name>oldState</name>");
            sbf.append("<value><i4>5</i4></value>");
            sbf.append("</member>");
            // Transaction ID
            /*
             * sbf.append("<member>");
             * sbf.append("<name>networkOperatorId</name>");
             * sbf.append("<value><string>"+p_requestMap.get("IN_TXN_ID")+
             * "</string></value>");
             * sbf.append("</member>");
             */
            sbf.append("</struct></value></param></params>");
            sbf.append("</methodCall>");
            requestStr = sbf.toString();
        } catch (Exception e) {
            _log.error("generateUpdateVoucherStateRequest", "Exception e=" + e);
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateUpdateVoucherStateRequest", "Exiting with requestStr=" + requestStr);
        }
        return requestStr;
    }// end of generateUpdateVoucherStateRequest

    /**
     * This Method parse UpdateVoucherState Response
     * The response indicates if the UpdateVoucherState request was carried out
     * successfully or not.
     * 
     * @param String
     *            p_responseStr
     * @throws Exception
     * @return HashMap<String,String> responseMap
     */
    private HashMap<String, String> parseUpdateVoucherStateResponse(String p_responseStr, String p_interfaceID) throws Exception {

        if (_log.isDebugEnabled())
            _log.debug("parseUpdateVoucherStateResponse", "Entered with p_responseStr=" + p_responseStr);
        HashMap<String, String> responseMap = null;
        int indexStart = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap<String, String>();

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
            } else {
                indexStart = p_responseStr.indexOf("<member><name>faultCode");
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                if (tempIndex > 0) {

                    String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                    responseMap.put("faultCode", faultCode.trim());
                }
            }
        } catch (Exception e) {
            _log.error("parseUpdateVoucherStateResponse", "Exception e=" + e.getMessage());
            throw e;
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseUpdateVoucherStateResponse", "Exiting with responseMap=" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseUpdateVoucherStateResponse

    /**
     * This method is used for for hiding the Pin(activation code) in the
     * transaction Log,specific to Mobinil Requirement.
     * 
     * @param p_responseStr
     * @return String responseStr
     * @throws Exception
     */
    protected String writeTransactionLogWithHidePin(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("writeTransactionLogWithHidePin", "Entered p_responseStr=" + p_responseStr);
        int indexStart = 0;
        int tempIndex = 0;
        int indexEnd = 0;
        String responseCode = null;
        String responseStr = null;
        try {
            StringBuffer tempString = new StringBuffer();
            tempString.append(p_responseStr);
            indexStart = tempString.indexOf("<member><name>responseCode");
            tempIndex = tempString.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = tempString.substring("<i4>".length() + tempString.indexOf("<i4>", tempIndex), tempString.indexOf("</i4>", tempIndex)).trim();
            }
            if (!InterfaceUtil.isNullString(responseCode) && MobinilVOMSI.RESULT_OK.equals(responseCode)) {
                // System.out.println("ResponseCode  :"+responseCode);
                indexStart = tempString.indexOf("<member><name>activationCode", indexEnd);
                tempIndex = tempString.indexOf("activationCode", indexStart);
                if (tempIndex > 0) {
                    // String name
                    // =tempString.substring(indexStart+"<member><name>".length(),tempString.indexOf("</name>",indexStart));
                    // System.out.println(name);
                    // String activationCode =
                    // tempString.substring("<string>".length()+tempString.indexOf("<string>",tempIndex),tempString.indexOf("</string>",tempIndex)).trim();
                    // System.out.println(activationCode);
                    int startPoint = "<string>".length() + tempString.indexOf("<string>", tempIndex);
                    int endPoint = tempString.indexOf("</string>", tempIndex);
                    tempString.replace(startPoint, endPoint, "****************");
                    responseStr = tempString.toString();
                    // System.out.println("Changed String : " +responseStr);

                }

            }

            else {
                responseStr = tempString.toString();

            }
        }

        catch (Exception e) {
            _log.error("writeTransactionLogWithHidePin", "Exception e=" + e.getMessage());
            throw e;
        }// end catch-Exception

        finally {
            if (_log.isDebugEnabled())
                _log.debug("writeTransactionLogWithHidePin", "Exited with responseStr=" + responseStr);
        }// end of finally

        return responseStr;
    }

    /**
     * This method is used to convert the date string into yyyyMMdd from
     * yyyyMMdd'T'HH:mm:ss
     * 
     * @param String
     *            p_dateStr
     * @return String
     */
    public String getDateString(String p_dateStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getDateString", "Entered p_dateStr=" + p_dateStr);

        String dateStr = "";
        try {
            dateStr = p_dateStr.substring(0, p_dateStr.indexOf("T")).trim();
        } catch (Exception e) {
            _log.error("getDateString", "Exception e=" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getDateString", "Exited dateStr=" + dateStr);
        }
        return dateStr;
    }
}