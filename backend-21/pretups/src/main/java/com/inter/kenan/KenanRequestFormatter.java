package com.inter.kenan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @(#)KenanRequestFormatter
 *                           Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Ashish Kumar Nov 22, 2006 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 *                           This class is used to format the request and
 *                           response based on the action.
 *                           REQUEST: XML request is generated from the hash map
 *                           based on key value pairs.
 *                           RESPONSE: From XML response elements values are
 *                           stored in HashMap.
 */
public class KenanRequestFormatter {

    public Log _log = LogFactory.getLog(this.getClass().getName());
    private SimpleDateFormat _sdf = null;// Represent the object of
                                         // SimpleDateFormat
    private String _transDateFormat = "dd-MM-yyyy HH:mm:ss";// Defines the Date
                                                            // and time format
                                                            // of KenanIN.

    /**
     * Constructor is used to initialize a SimpleDateFormat object.
     * 
     * @throws Exception
     */
    public KenanRequestFormatter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("KenanRequestFormatter[constructor]", "Entered");
        try {
            _sdf = new SimpleDateFormat(_transDateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("KenanRequestFormatter[constructor]", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("KenanRequestFormatter[constructor]", "Exited");
        }// end of finally
    }// end of KenanRequestFormatter[constructor]

    /**
     * This method is used to generate the transaction id that is to be send to
     * IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String getINReconTxnID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Enetered p_requestMap::" + p_requestMap);
        String inReconID = null;
        try {
            String userType = (String) p_requestMap.get("USER_TYPE");
            if (userType != null)
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID") + "." + userType);
            else
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
            p_requestMap.put("IN_RECON_ID", inReconID);
        }// end of try block
        catch (Exception e) {
            _log.error("getINReconTxnID", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID::" + inReconID);
        }// end of finally
        return inReconID;
    }// end of getINReconTxnID

    /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String.
     * @throws Exception
     */
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            // This case is just to sync with the request formatter class of
            // other IN's- Confirm?
            case KenanI.ACTION_ACCOUNT_INFO: {
                break;
            }
            case KenanI.ACTION_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            // This case is just to sync with the request formatter class of
            // other IN's- Confirm?
            case KenanI.ACTION_DEBIT: {
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str::" + str);
        }// end of finally
        return str;
    }// end of generateRequest

    /**
     * This method is used to parse the response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            // This case is just to sync with the request formatter class of
            // other IN's- Confirm?
            case KenanI.ACTION_ACCOUNT_INFO: {
                break;
            }
            case KenanI.ACTION_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            // This case is just to sync with the request formatter class of
            // other IN's- Confirm?
            case KenanI.ACTION_DEBIT: {
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("parseResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map::" + map);
        }// end of finally
        return map;
    }// end of parseResponse

    /**
     * This method implements the logic to generate the XML string for the
     * Credit request.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        StringBuffer stringBuffer = null;
        String lineSep = null;
        String bonusAmount = null;
        try {
            lineSep = System.getProperty("line.separator");
            stringBuffer = new StringBuffer(1028);
            stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lineSep);
            stringBuffer.append("<tatasky>" + lineSep);
            stringBuffer.append("<e-voucher-topup-request>" + lineSep);
            // set the subscriber id, that would be present in the request map
            // under key MSISDN.
            // CONFIRM whether to Use the Add or Remove the PREFIX of Subscriber
            // id if it is defined in the INFile???
            stringBuffer.append("<sub-id>");
            stringBuffer.append(InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));
            stringBuffer.append("</sub-id>" + lineSep);
            // Set the origin transaction id.
            stringBuffer.append("<pretup-trans-id>");
            stringBuffer.append(getINReconTxnID(p_requestMap));
            stringBuffer.append("</pretup-trans-id>" + lineSep);
            // Set the user id
            stringBuffer.append("<user-id>");
            stringBuffer.append(p_requestMap.get("user_id"));
            stringBuffer.append("</user-id>" + lineSep);
            // set the password
            stringBuffer.append("<password>");
            stringBuffer.append(p_requestMap.get("password"));
            stringBuffer.append("</password>" + lineSep);
            // set the distributor-ref as the owner id,present in request map
            // with key as OWNER_MSISDN
            stringBuffer.append("<distributor-ref>");
            stringBuffer.append(p_requestMap.get("OWNER_MSISDN"));
            stringBuffer.append("</distributor-ref>" + lineSep);
            // Set the retailer-ref as the sender msidn, present in request map
            // with key as SENDER_MSISDN
            stringBuffer.append("<retailer-ref>");
            stringBuffer.append(p_requestMap.get("SENDER_MSISDN"));
            stringBuffer.append("</retailer-ref>" + lineSep);
            // set the requested amount, it would be present in request map with
            // key as transfer amount.
            stringBuffer.append("<amount>");
            stringBuffer.append(p_requestMap.get("transfer_amount"));
            stringBuffer.append("</amount>" + lineSep);
            // set the bonus amount and check for the zero value.
            bonusAmount = (String) p_requestMap.get("transfer_bonus");
            if (InterfaceUtil.isNullString(bonusAmount) || "0".equals(bonusAmount.trim()))
                stringBuffer.append("<bonus></bonus>" + lineSep);// Confirm, if
                                                                 // IN support 0
                                                                 // value then
                                                                 // put
            else {
                stringBuffer.append("<bonus>");
                stringBuffer.append(p_requestMap.get("transfer_bonus"));
                stringBuffer.append("</bonus>" + lineSep);
            }
            // Value of voucher-type would be empty string and will be used in
            // future.
            stringBuffer.append("<voucher-type></voucher-type>" + lineSep);
            // Set the date and time of the transaction with specified format.
            stringBuffer.append("<date-time>");
            stringBuffer.append(getTransDateTime());
            stringBuffer.append("</date-time>" + lineSep);
            stringBuffer.append("</e-voucher-topup-request>" + lineSep);
            stringBuffer.append("</tatasky>" + lineSep);
            requestStr = stringBuffer.toString();
        }// end of try-block
        catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e: " + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exiting Request requestStr::" + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateRechargeCreditRequest

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<error-code>");
            String errorCode = p_responseStr.substring("<error-code>".length() + indexStart, p_responseStr.indexOf("</error-code>", indexStart));
            responseMap.put("error-code", errorCode.trim());

            indexStart = p_responseStr.indexOf("<error-desc>", indexStart);
            String errStr = p_responseStr.substring(indexStart + "<error-desc>".length(), p_responseStr.indexOf("</error-desc>", indexStart));
            responseMap.put("error-desc", errStr);
            if (!KenanI.RESULT_OK.equals(errorCode.trim()))
                return responseMap;
            indexStart = p_responseStr.indexOf("<sub-id>");
            String subID = p_responseStr.substring("<sub-id>".length() + indexStart, p_responseStr.indexOf("</sub-id>", indexStart));
            responseMap.put("sub-id", subID.trim());

            indexStart = p_responseStr.indexOf("<pretup-trans-id>");
            String transactionId = p_responseStr.substring("<pretup-trans-id>".length() + indexStart, p_responseStr.indexOf("</pretup-trans-id>", indexStart));
            responseMap.put("pretup-trans-id", transactionId);

            indexStart = p_responseStr.indexOf("<sub-ph>");
            if (indexStart > 0) {
                String subPh = p_responseStr.substring("<sub-ph>".length() + indexStart, p_responseStr.indexOf("</sub-ph>", indexStart));
                responseMap.put("sub-ph", subPh.trim());
            }

            indexStart = p_responseStr.indexOf("<end-date>");
            String endDate = p_responseStr.substring("<end-date>".length() + indexStart, p_responseStr.indexOf("</end-date>", indexStart));
            responseMap.put("end-date", endDate.trim());

            indexStart = p_responseStr.indexOf("<status>");
            String status = p_responseStr.substring("<status>".length() + indexStart, p_responseStr.indexOf("</status>", indexStart));
            responseMap.put("status", status.trim());

            indexStart = p_responseStr.indexOf("<amount>");
            String amount = p_responseStr.substring("<amount>".length() + indexStart, p_responseStr.indexOf("</amount>", indexStart));
            responseMap.put("amount", amount.trim());

            indexStart = p_responseStr.indexOf("<new-balance>");
            String newBalance = p_responseStr.substring("<new-balance>".length() + indexStart, p_responseStr.indexOf("</new-balance>", indexStart));
            responseMap.put("new-balance", newBalance.trim());

            indexStart = p_responseStr.indexOf("<date-time>");
            String transDateTime = p_responseStr.substring("<date-time>".length() + indexStart, p_responseStr.indexOf("</date-time>", indexStart));
            responseMap.put("date-time", transDateTime.trim());
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("parseGetAccountInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseRechargeCreditResponse

    /**
     * Method to get the Transaction date and time with specified format.
     * 
     * @return String
     * @throws Exception
     */
    private String getTransDateTime() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getTransDateTime", "Entered");
        String transDateTime = null;
        try {
            Date now = new Date();
            transDateTime = _sdf.format(now);
        }// end of try block
        catch (Exception e) {
            _log.error("getTransDateTime", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getTransDateTime", "Exited transDateTime: " + transDateTime);
        }// end of finally
        return transDateTime;
    }// end of getTransDateTime

    /*
     * public static void main(String[] ar) throws Exception
     * {
     * String lineSep = System.getProperty("line.separator");
     * StringBuffer responseBuffer = new StringBuffer();
     * responseBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lineSep
     * );
     * responseBuffer.append("<tatasky>"+lineSep);
     * responseBuffer.append("<e-voucher-topup-response>"+lineSep);
     * responseBuffer.append("<sub-id>293847399231</sub-id>"+lineSep);
     * responseBuffer.append("<pretup-trans-id>I343D456</pretup-trans-id>"+lineSep
     * );
     * responseBuffer.append("<sub-ph>9816835683</sub-ph>"+lineSep);
     * responseBuffer.append("<end-date>11/20/2006</end-date>"+lineSep);
     * responseBuffer.append(" <status>success OR failure</status>"+lineSep);
     * responseBuffer.append("<error-code>17300</error-code>"+lineSep);
     * responseBuffer.append("<error-desc>Account does not exist</error-desc>"+
     * lineSep);
     * responseBuffer.append("<amount>600</amount>"+lineSep);
     * responseBuffer.append("<new-balance>1000</new-balance>"+lineSep);
     * responseBuffer.append("<date-time>09-20-2006 10:13:25</date-time>"+lineSep
     * );
     * responseBuffer.append("</e-voucher-topup-response>"+lineSep);
     * responseBuffer.append("</tatasky>"+lineSep);
     * KenanRequestFormatter tsf = new KenanRequestFormatter();
     * System.out.println(tsf.parseRechargeCreditResponse(responseBuffer.toString
     * ()));
     * 
     * System.out.println("date format"+tsf.getTransDateTime());
     * }
     */
}
