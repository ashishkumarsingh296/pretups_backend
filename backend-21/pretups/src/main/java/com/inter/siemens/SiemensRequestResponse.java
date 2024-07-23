package com.inter.siemens;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @(#)SiemensRequestResponse.java
 *                                 Copyright(c) 2006, Bharti Telesoft Int.
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
 *                                 Ashish Kumar Jun 15, 2006 Initial Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 This class is responsible to
 *                                 1.Generate and format the request string for
 *                                 different request like
 *                                 getAccountInformation,credit,debitAdjust.
 *                                 2.Parse the response for the
 *                                 accountInformation,credit and debitAdjust
 *                                 request.
 * 
 */
public class SiemensRequestResponse {
    private final String _purposeFieldSeparator = "\\;";
    public static Log _log = LogFactory.getLog(SiemensRequestResponse.class.getName());

    /*
     * private String senderValidate = "N";
     * private String receiverValidate ="C";
     * private String senderDebit ="S";
     * private String receiverCredit="R";
     * private String senderCreditBack="B";
     */
    private static int _counter = 0;// Used to increment the counter for the
                                    // transactionID

    /**
     * This method is used to generate 14 bit IN reconciliation ID for the
     * transaction.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String getINReconTxnID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Enetered p_requestMap =" + p_requestMap);
        String inReconID = null;
        // String interfaceAction=null;
        // String appendStr=null;
        // String userType=null;
        int inTxnLength = 4;
        int length = 0;
        int tmpLength = 0;
        try {
            String dateString = "";
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
            dateString = sdf.format(mydate);
            inReconID = getIncrCounter();
            length = inReconID.length();
            tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    inReconID = "0" + inReconID;
            }
            inReconID = dateString + (String) p_requestMap.get("INSTANCE_ID") + inReconID;
            p_requestMap.put("IN_RECON_ID", inReconID);
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("getINReconTxnID", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID =" + inReconID);
        }// end of finally
        return inReconID;
    }// end of getINReconTxnID

    /**
     * This method is invoked by the SiemensHandler class and based on the
     * action value
     * it decide to format the request string for
     * specific(getAccountInformation,credit,debitAdjust)type of request.
     * 
     * @param int p_action
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    public String generateRequest(int p_action, HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action = " + p_action + " p_requestMap: " + p_requestMap);
        String str = null;
        p_requestMap.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case SiemensINHandler._VALIDATE_ACTION: {
                str = generateGetAccountInfoRequest(p_requestMap);
                break;
            }
            case SiemensINHandler._CREDIT_ACTION: {
                str = generateRechargeCreditRequest(p_requestMap);
                break;
            }
            case SiemensINHandler._DEBIT_ACTION: {
                str = generateImmediateDebitRequest(p_requestMap);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("generateRequest", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str = " + str);
        }// end of finally
        return str;
    }// end of generateRequest

    /**
     * This method is invoked by SiemenseHandler class and based on the action
     * value it
     * decide to parse the response string for
     * specific(getAccountInformation,credit,
     * debitAdjust) request.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action = " + p_action + " p_responseStr = " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case SiemensINHandler._VALIDATE_ACTION: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case SiemensINHandler._CREDIT_ACTION: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case SiemensINHandler._DEBIT_ACTION: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            _log.error("parseResponse", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map: " + map);
        }// end of finally
        return map;
    }// end of parseResponse

    /**
     * This method generate the request string for getAccountInfo Request.
     * Request generation for the account info includes following
     * 1.First common request string is get from the INFile whose key is
     * COMMON_STRING.
     * 2.Generate the IN reconciliation id by method getINReconTxnID.
     * 3.Append the purpose field in the request string,By Purpose field
     * IN decides which action(Account Info, credit or debit) is performed.
     * 4.Append the Request type parameter to string buffer
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateGetAccountInfoRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered  p_requestMap = " + p_requestMap);
        StringBuffer sbf = null;
        String requestStr = null;
        try {
            sbf = new StringBuffer(1028);

            sbf.append("TransactionId=" + getINReconTxnID(p_requestMap));
            sbf.append("&");
            // Append the common string to string buffer from the file cache
            sbf.append(FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "COMMON_STRING"));

            // Append the consumerID(MSISDN) to the request.
            // Get the filtered msisdn based on the INFile whether to remove or
            // add msisdn prefix.
            sbf.append("&ConsumerId=" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));

            // Append the purpose field and RequestType to string buffer.
            // Purpose field contains the following information with specified
            // sequence of element
            // Purpose=PT01;<date>. Format of date will be dd/mm/yyyy hh:mm:ss
            String transDateFormat = FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "TRANS_DATE_TIME");
            // Changed purpose to Purpose because IN accepts Purpose901/08/06)
            sbf.append("&Purpose=PT01;");
            sbf.append(getSiemensTransDateTime(transDateFormat));
            sbf.append(";");
            sbf.append("&RequestType=chargeAmount");
            requestStr = sbf.toString();
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("generateGetAccountInfoRequest", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exited request string  requestStr = " + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateGetAccountInfoRequest

    /**
     * This method is used to parse the response string of getAccountInfoRequest
     * and store the information into hash map as key value pair.
     * Parsing of accountInfo response includes the following activities.
     * 1.Parsing response of get account info request fetches the values of
     * Transaction Data, Execution Status, Transparent data.
     * 2.Parse the Transparent data string.
     * Transparent Data contains various elements that are separated by
     * semicolon.
     * 3.After parsing the response string put all these values in Hash map as
     * key value pair.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered  p_responseStr = " + p_responseStr);
        HashMap responseMap = null;
        String[] transparentDataArray = null;
        int index = 0;
        String status = "";
        try {
            // String p_responseStr
            // ="TransactionID=C060314.0436.0025S ExecutionStatus=1TransparentData=11111;04/04/2007;22222;04/04/2007;33333;04/04/2007;44444;04/04/2007;1;03/04/2006 06:00:00;0;1;";
            responseMap = new HashMap();
            // First check the executionStatus if it is 1 then parse and set all
            // the values else set only executionStatus in map.
            String result = "1";
            index = p_responseStr.indexOf("ExecutionStatus=");
            if (p_responseStr.contains("ERROR")) {
                String subTst = p_responseStr.substring(index);
                status = subTst.substring("ExecutionStatus=".length(), subTst.indexOf("***")).trim();
            } else
                status = (p_responseStr.substring(index + "ExecutionStatus=".length(), p_responseStr.indexOf("TransparentData=", index))).trim();
            responseMap.put("execution_status", status.trim());
            if (result.equals(status)) {
                // Parse the response string to get the value of transaction id.
                index = p_responseStr.indexOf("TransactionID=");
                String transactionID = p_responseStr.substring(index + "TransactionID=".length(), p_responseStr.indexOf("ExecutionStatus=", index));
                responseMap.put("transactionID", transactionID.trim());

                // Parse response string to get the value of transparant data.
                index = p_responseStr.indexOf("TransparentData=");
                String transparentData = (p_responseStr.substring(index + "TransparentData=".length(), p_responseStr.length())).trim();
                responseMap.put("transparent_data", transparentData);

                // Transparent data contains the following element separated
                // with semicolon with specified sequence.
                // 0<OnPeak amount Balance>;1<OnPeak activity end
                // date>;2<OffPeak amount Balance>;3<OffPeak activity end date>;
                // 4<SMS & Data amount Balance>;5<SMS & Data activity end
                // date>;6<Bonus SMS amount Balance>;
                // 7<Bonus SMS activity end date>;8<Service class ID (Subscriber
                // profile)>;9<TransDateTime>;
                // 10<Inactivity period of OnPeak account>; 11<Account Status>;
                transparentDataArray = transparentData.split(_purposeFieldSeparator);

                // Put the onpeak account balance to map,it is the 0th element
                // of transparent data.
                responseMap.put("onpeak_account_balance", transparentDataArray[0].trim());
                // put the onpeak activity end date to map,it is the 1st element
                // of transparent data.
                responseMap.put("onpeak_activity_end_date", transparentDataArray[1].trim());
                // put the off peak account balance to map,it is the 2nd element
                // of the transparent data.
                responseMap.put("offpeak_account_balance", transparentDataArray[2].trim());
                // put the off activity end date to map,it is the 3rd element of
                // the transparent data.
                responseMap.put("offpeak_activity_end_date", transparentDataArray[3].trim());
                // put the sms data account balance to map, it is the 4th
                // element of the transparent data.
                responseMap.put("sms_data_account_balance", transparentDataArray[4].trim());
                // put the sms data activity end date, it is the 5th element of
                // the transparent data.
                responseMap.put("sms_data_activity_end_date", transparentDataArray[5].trim());
                // put the bonus sms account balance to map,it is 6th element of
                // the transparent data.
                responseMap.put("bonus_sms_amount_balance", transparentDataArray[6].trim());
                // put the bonus activity end date to map,it is the 7th element
                // of the transparent data.
                responseMap.put("bonus_activity_end_date", transparentDataArray[7].trim());
                // put the service class id to map,it is the 8th element of the
                // transparent data.
                responseMap.put("service_classID", transparentDataArray[8].trim());
                // put the transaction date time to map,it is the 9th element of
                // the transparent data.
                responseMap.put("transaction_date_time", transparentDataArray[9].trim());
                // put the onpeak grace period to map,it is the 10th element of
                // the transparent data.
                responseMap.put("onpeak_inactivity_period", transparentDataArray[10].trim());
                // put the onpeak grace period to map,it is the 11th element of
                // the transparent data.
                responseMap.put("account_status", transparentDataArray[11].trim());
            }
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("parseGetAccountInfoResponse", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exited responseMap = " + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseGetAccountInfoResponse

    /**
     * This method generate the request string for credit Request.
     * Request generation for the credit includes following
     * 1.First common request string is get from the INFile whose key is
     * COMMON_STRING.
     * 2.Generate the IN reconciliation id by method getINReconTxnID.
     * 3.set RequestType=rechargeAmount1
     * 4.Check The selector from request hahspmap. Key is CARD_GROUP_SELECTOR.
     * a.If the value of CARD_GROUP_SELECTOR. is equal to
     * ACCOUNT_SELECTOR_ONPEAK.
     * Then update the fields related to ONPeak Account in rquest.
     * b.If the value of CARD_GROUP_SELECTOR. is equal to
     * ACCOUNT_SELECTOR_SMSDATA.
     * Then update the fields related to Sma and DATA Account in request.
     * 5.Check the the value of 'ADJUSTMENT' in the request map if it is 'Y'
     * set activity day(validity) zero corresponding to the account.
     * 6.Append the purpose field in the request string,By Purpose field
     * IN decides which action(Account Info, credit or debit) is performed.
     * 7.Append the Request type parameter to string buffer
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_requestMap = " + p_requestMap);
        StringBuffer sbf = null;
        String requestStr = null;
        String cardGroup = null;
        try {
            sbf = new StringBuffer(1028);

            // Append the transactionID to string buffer.YET TO CONFIRM
            sbf.append("TransactionId=" + getINReconTxnID(p_requestMap));
            sbf.append("&");
            // Append the common string to string buffer from the file cache
            sbf.append(FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "COMMON_STRING"));

            // Adding the &Expiry.Mode=4&Expiry.Value=0 for the Credit request
            sbf.append("&Expiry.Mode=4&Expiry.Value=0");
            // Append the consumerID(MSISDN) to the request.
            // Get the filtered msisdn based on the INFile whether to remove or
            // add msisdn prefix.
            sbf.append("&ConsumerId=" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));
            String transDateFormat = FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "TRANS_DATE_TIME");
            // Append the RequestType and purpose field to string buffer.
            sbf.append("&RequestType=rechargeAmount1");
            sbf.append("&Purpose=PT03;");
            sbf.append(getSiemensTransDateTime(transDateFormat));
            sbf.append(";");
            String cardGroupSelector = (String) p_requestMap.get("CARD_GROUP_SELECTOR");
            cardGroup = (String) p_requestMap.get("CARD_GROUP");
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", " cardGroupSelector = " + cardGroupSelector + " cardGroup = " + cardGroup);

            // Check the CARD_GROUP_SELECTOR value from request hahspmap
            // If value of CARD_GROUP_SELECTOR is equal to
            // ACCOUNT_SELECTOR_ONPEAK then update the OnPeak account fields.
            // and set the SMSDATA field value as 0.
            if ((SiemensINHandler._ACCOUNT_SELECTOR_ONPEAK).equals(cardGroupSelector)) {
                // Append the Onpeak interface amount.
                sbf.append((String) p_requestMap.get("transfer_amount"));
                // Append the SmsData amount as zero since account is selected
                // as ONPEAK
                sbf.append(";0;");
                // Append the validity days for Onpeak.

                String validityDays = InterfaceUtil.NullToString((String) p_requestMap.get("VALIDITY_DAYS"));
                if (validityDays.length() == 0)
                    validityDays = "0";
                sbf.append(validityDays);

                sbf.append(";0;");
                sbf.append(cardGroup + ";");
            }
            // If value of CARD_GROUP_SELECTOR is equal to
            // ACCOUNT_SELECTOR_SMSDATA then update the SMSData account fields.
            // and set the OnPeak account field value as 0.
            else if ((SiemensINHandler._ACCOUNT_SELECTOR_SMSDATA).equals(cardGroupSelector)) {
                // Append the Onpeak amount as zero,since account is selected as
                // SMSData.
                sbf.append("0;");
                // Append the SMSData account.
                sbf.append((String) p_requestMap.get("transfer_amount"));
                // Append the Onpeak validity days as zero,since account is
                // selected as SMSData.
                sbf.append(";0;");
                // Append the SMSData validity days.
                String validityDays = InterfaceUtil.NullToString((String) p_requestMap.get("VALIDITY_DAYS"));
                if (validityDays.length() == 0)
                    validityDays = "0";
                sbf.append(validityDays);
                sbf.append(";" + cardGroup + ";");
            }
            requestStr = sbf.toString();
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("generateRechargeCreditRequest", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exited request string  requestStr = " + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateRechargeCreditRequest

    /**
     * This method is used to parse the response string.
     * 1.Parsing response of recharge credit request fetches the values of
     * Transaction Data, Execution Status, Transparent data.
     * 2.Parse the Transparent data string.
     * Transparent Data contains various elements that are separated by
     * semicolon.
     * 3.After parsing the response string put all these values in Hash map as
     * key value pair.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered  p_responseStr = " + p_responseStr);
        HashMap responseMap = null;
        String[] transparentDataArray = null;
        int index = 0;
        String status = "";
        try {
            responseMap = new HashMap();
            // First check the executionStatus if it is 1 then parse and set all
            // the values else set only executionStatus in map.
            String result = "1";
            index = p_responseStr.indexOf("ExecutionStatus=");
            if (p_responseStr.contains("ERROR")) {
                String subTst = p_responseStr.substring(index);
                status = subTst.substring("ExecutionStatus=".length(), subTst.indexOf("***")).trim();
            } else
                status = (p_responseStr.substring(index + "ExecutionStatus=".length(), p_responseStr.indexOf("TransparentData=", index))).trim();
            responseMap.put("execution_status", status.trim());
            if (result.equals(status)) {
                // Parse the response string to get the value of transaction id
                index = p_responseStr.indexOf("TransactionID=");
                String transactionID = p_responseStr.substring(index + "TransactionID=".length(), p_responseStr.indexOf("ExecutionStatus=", index));
                responseMap.put("transactionID", transactionID.trim());

                // Parse the response string to get the value of transparant
                // data.
                index = p_responseStr.indexOf("TransparentData=");
                String transparentData = (p_responseStr.substring(index + "TransparentData=".length(), p_responseStr.length())).trim();
                responseMap.put("transparent_data", transparentData);

                // Parse the transparent data string separated by semicolon and
                // store these values in a string array.
                transparentDataArray = transparentData.split(_purposeFieldSeparator);

                // Transparent data contains the following elements separated by
                // semicolon with specified sequence.
                // 0<TransDateTime>;1<Counter Id>;2<New Onpeak account
                // Balance>;3<Onpeak activity end date>;4<Old OnPeak account
                // Balance>;
                // 5<Old OnPeak activity period>;6<Counter Id>;7<SMS&Data
                // account Balance>;8<SMS&Data activity end date>;9<Old SMS&Data
                // account Balance>;
                // 10<Old SMS&Data activity period>;11.<Service class ID
                // (Subscriber profile)>;12.<Account Status>;

                // After parsing each element of transparent data put it into
                // response map.
                // put the trans date time,it is the 0th element of transparent
                // data
                responseMap.put("trans_date_time", transparentDataArray[0].trim());
                // put the counter id (confirm what it is?),it is the 1st
                // element of transparent data
                responseMap.put("new_counter_id", transparentDataArray[1].trim());
                // Put new onpeak account balance, it is the 2nd element of
                // transparent data
                responseMap.put("new_onpeak_account_balance", transparentDataArray[2].trim());
                // put the new onpeak activity end date to map,it is the 3rd
                // element of transparent data.
                responseMap.put("new_onpeak_activity_end_date", transparentDataArray[3].trim());
                // put the old onpeak account balance to map,it is the 4th
                // element of the transparent data
                responseMap.put("old_onpeak_account_balance", transparentDataArray[4].trim());
                // put the onpeak activity end date to map,it is the 5th element
                // of transparent data.
                responseMap.put("old_onpeak_activity_end_date", transparentDataArray[5].trim());
                // Confirm this counter id ,what does it represents?
                responseMap.put("old_counter_id", transparentDataArray[6].trim());
                // put new sms data account balance to map, it is the 7th
                // element of transparent data.
                responseMap.put("new_sms_data_account_balance", transparentDataArray[7].trim());
                // put the new sms data activity end date to map,it is the 8th
                // element of transparent data
                responseMap.put("new_sms_data_activity_end_date", transparentDataArray[8].trim());
                // put the old sms data account balance to map it is the 9th
                // element of the transparent data.
                responseMap.put("old_sms_data_account_balance", transparentDataArray[9].trim());
                // put the old sms data activity end data to map,it is the 10th
                // element of the transparent data.
                responseMap.put("old_sms_data_activity_end_date", transparentDataArray[10].trim());
                // put the service class id to map,it is the 11th element of the
                // transparent data
                responseMap.put("service_classID", transparentDataArray[11].trim());
                // Put the account status to map, it is the 12th element of
                // transparent data.
                responseMap.put("account_status", transparentDataArray[12].trim());
            }
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("parseRechargeCreditResponse", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap = " + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseRechargeCreditResponse

    /**
     * This method is used to generate the debit request string.
     * Generation of debit request string includes the following.
     * 1.First common request string is get from the INFile whose key is
     * COMMON_STRING.
     * 2.Generate the IN reconciliation id by method getINReconTxnID.
     * 3.Set the RequestType=chargeAmount1
     * 4.Set the purpose field, that contains the following element with
     * specified format.
     * PT02;<TransDateTime>;<Amount of Money to debiting the OnPeak account>;
     * <Amount of Money to debiting the SMS&Data account>;<Amount of day to
     * reduce activity period of OnPeak account>;
     * <Amount of day to reduce activity period SMS&Data account>;Tax
     * Fee=<Amount of Tax Fee to debiting the Onpeak account>;
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered p_requestMap = " + p_requestMap);
        StringBuffer sbf = null;
        String requestStr = null;
        try {
            sbf = new StringBuffer(1028);
            // Append the common string to string buffer from the file cache
            sbf.append("TransactionId=" + getINReconTxnID(p_requestMap));
            sbf.append("&");
            sbf.append(FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "COMMON_STRING"));

            // Append the transactionID to string buffer.YET TO CONFIRM
            // Append the consumerID(MSISDN) to the request.
            // Get the filtered msisdn based on the INFile whether to remove or
            // add msisdn prefix.
            sbf.append("&ConsumerId=" + InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));

            // Append the RequestType and purpose field to string buffer.
            // Purpose field contains following information with specified
            // sequence of element.
            // purpose=PT02;<TransDateTime>;<Amount of Money to debiting the
            // OnPeak account>;
            // <Amount of Money to debiting the SMS&Data account>;<Amount of day
            // to reduce activity period of OnPeak account>;
            // <Amount of day to reduce activity period SMS&Data account>;
            // Tax Fee=<Amount of Tax Fee to debiting the Onpeak account>;
            String transDateFormat = FileCache.getValue((String) p_requestMap.get("INTERFACE_ID"), "TRANS_DATE_TIME");

            // Changed purpose to Purpose because IN accepts Purpose901/08/06)
            // sbf.append("&RequestType=chargeAmount1");
            sbf.append("&RequestType=chargeAmount");
            sbf.append("&Purpose=PT02;");
            sbf.append(getSiemensTransDateTime(transDateFormat));
            sbf.append(";");
            // Check the CARD_GROUP_SELECTOR value from request hahspmap
            // If value of CARD_GROUP_SELECTOR is equal to
            // ACCOUNT_SELECTOR_ONPEAK then update the OnPeak account fields.
            // and set the SMSDATA field value as 0.
            String cardGroupSelector = (String) p_requestMap.get("CARD_GROUP_SELECTOR");
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "cardGroupSelector = " + cardGroupSelector);
            if ((SiemensINHandler._ACCOUNT_SELECTOR_ONPEAK).equals(cardGroupSelector)) {
                // Append the Onpeak interface amount.
                sbf.append((String) p_requestMap.get("transfer_amount"));
                // Append the SmsData amount as zero since account is selected
                // as ONPEAK
                sbf.append(";0;");
                // Append the validity days for Onpeak.

                String validityDays = InterfaceUtil.NullToString((String) p_requestMap.get("VALIDITY_DAYS"));
                if (validityDays.length() == 0)
                    validityDays = "0";
                sbf.append(validityDays);

                // Append the SMSData validity as zero since OnPeak account is
                // selected with default value of Tax fee
                sbf.append(";0;Tax Fee=0;");
            }
            // If value of CARD_GROUP_SELECTOR is equal to
            // ACCOUNT_SELECTOR_SMSDATA then update the SMSData account fields.
            // and set the OnPeak account field value as 0.
            else if ((SiemensINHandler._ACCOUNT_SELECTOR_SMSDATA).equals(cardGroupSelector)) {
                // Append the Onpeak amount as zero,since account is selected as
                // SMSData.
                sbf.append("0;");
                // Append the SMSData account value.
                sbf.append((String) p_requestMap.get("transfer_amount"));
                // Append the Onpeak validity days as zero,since account is
                // selected as SMSData.
                sbf.append(";0;");
                // Append the SMSData validity days.
                String validityDays = InterfaceUtil.NullToString((String) p_requestMap.get("VALIDITY_DAYS"));
                if (validityDays.length() == 0)
                    validityDays = "0";
                sbf.append(validityDays);
                // Append the default value of Tax fee;
                sbf.append(";Tax Fee=0;");
            }
            requestStr = sbf.toString();
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("generateImmediateDebitRequest", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exited request string requestStr = " + requestStr);
        }// end of finally
        return requestStr;
    }// end of generateImmediateDebitRequest

    /**
     * This method is used to parse the response string and store the
     * information into hash map as key value pair.
     * Parsing debit response includes the following activities.
     * 1.Transaction Data, Execution Status, consumer ID and transparent data.
     * 2.Parse the Transparent data string
     * Transparent Data contains various element separated by semicolon,fetches
     * these values and store into map.
     * 3.After parsing the response string put all these values in responseMap
     * as key value pair.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */

    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "p_responseStr = " + p_responseStr);
        HashMap responseMap = null;
        String[] transparentDataArray = null;
        int index = 0;
        String status = "";
        try {
            // String p_responseStr
            // ="TransactionID=C060314.0436.0025S ExecutionStatus=1TransparentData=11111;04/04/2007;22222;04/04/2007;33333;04/04/2007;44444;04/04/2007;1;03/04/2006 06:00:00;0;1;";
            responseMap = new HashMap();
            // First check the executionStatus if it is 1 then parse and set all
            // the values else set only executionStatus in map.
            String result = "1";
            index = p_responseStr.indexOf("ExecutionStatus=");
            if (p_responseStr.contains("ERROR")) {
                String subTst = p_responseStr.substring(index);
                status = subTst.substring("ExecutionStatus=".length(), subTst.indexOf("***")).trim();
            } else
                status = (p_responseStr.substring(index + "ExecutionStatus=".length(), p_responseStr.indexOf("TransparentData=", index)));
            responseMap.put("execution_status", status.trim());
            if (result.equals(status)) {
                // Parse the response string to get the value of transaction id.
                index = p_responseStr.indexOf("TransactionID=");
                String transactionID = p_responseStr.substring(index + "TransactionID=".length(), p_responseStr.indexOf("ExecutionStatus=", index));
                responseMap.put("transactionID", transactionID.trim());

                // Parse the response string to get the value of transparant
                // data.
                index = p_responseStr.indexOf("TransparentData=");
                String transparentData = (p_responseStr.substring(index + "TransparentData=".length(), p_responseStr.length())).trim();
                responseMap.put("transparent_data", transparentData);

                // Parse the transparent data string whose elements are
                // separated by semicolon.storing each element in a string
                // array.
                transparentDataArray = transparentData.split(_purposeFieldSeparator);

                // Transparent data contains the following elements separated by
                // semicolon with specified sequence.
                // 0<TransDateTime>;1<New Onpeak account Balance>;2<Onpeak
                // activity end date>;
                // 3<Old OnPeak account Balance>;4<Old OnPeak activity
                // period>;5<SMS&Data account Balance>;
                // 6<SMS&Data activity end date>;7<Old SMS&Data account
                // Balance>;8<Old SMS&Data activity period>;
                // 9<Service class ID (Subscriber profile)>;10<Account Status>;

                // Put the transaction date time to map,it is the oth element of
                // transparent data
                responseMap.put("trans_date_time", transparentDataArray[0].trim());
                // Put the new onpeak account balance to map, it is the 1st
                // element of transparent data
                responseMap.put("new_onpeak_account_balance", transparentDataArray[1].trim());
                // Put the onpeak activity end date to map, it is the 2nd
                // element of transparent data.
                responseMap.put("new_onpeak_activity_end_date", transparentDataArray[2].trim());
                // Put the old onpeak account balance to map,it is the 3rd
                // element of transparent data
                responseMap.put("old_onpeak_account_balance", transparentDataArray[3].trim());
                // Put the old onpeak activity end date to map,it is the 4th
                // element of transparent data.
                responseMap.put("old_onpeak_activity_end_date", transparentDataArray[4].trim());
                // Put new sms data account balance to map, it is the 5th
                // element of transparent data.
                responseMap.put("new_sms_data_account_balance", transparentDataArray[5].trim());
                // Put sms data activity end date to map,it is the 6th element
                // of transparent data.
                responseMap.put("new_sms_data_activity_end_date", transparentDataArray[6].trim());
                // Put the old sms data account balance to map, it is the 7th
                // element of transparent data.
                responseMap.put("old_sms_data_account_balance", transparentDataArray[7].trim());
                // Put the old sms data actvity end date to map, it is the 8th
                // element of transparent data.
                responseMap.put("old_sms_data_activity_end_date", transparentDataArray[8].trim());
                // Put the service class id to map,it is the 9th element of
                // transparent data.
                responseMap.put("service_classID", transparentDataArray[9].trim());
                // Put the account status to map, it is the 10th element of
                // transparent data.
                responseMap.put("account_status", transparentDataArray[10].trim());
            }
        }// end of try-block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("parseImmediateDebitResponse", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exited responseMap = " + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseImmediateDebitResponse

    /**
     * This method is used to format the Transaction date and time.
     * 
     * @param String
     *            p_transDateFormat
     * @return String
     * @throws BTSLBaseException
     */
    private String getSiemensTransDateTime(String p_transDateFormat) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSiemensTransDateTime", "Entered p_transDateFormat = " + p_transDateFormat);
        String transDateTime = null;
        try {
            SimpleDateFormat callDate = new SimpleDateFormat(p_transDateFormat);
            Date date = new Date();
            transDateTime = callDate.format(date);
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("getSiemensTransDateTime", "Exception e = " + e.getMessage());
            throw new BTSLBaseException(this, "getSiemensTransDateTime", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getSiemensTransDateTime", "Exited transDateTime = " + transDateTime);
        }// end of finally
        return transDateTime;
    }// end of getSiemensTransDateTime

    // This method is used to get the transaction id.
    /**
     * This method is used to generate the static counter that is incremented by
     * 1 for each request.
     * After reaching its maximum value(9999) it is initializes.
     * 
     * @return String
     */
    public static synchronized String getIncrCounter() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getIncrCounter", "Entered");
        try {
            if (_counter == 9999)
                _counter = 0;
            _counter++;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getIncrCounter", e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getIncrCounter", "Exiting counter = " + _counter);
        }
        return String.valueOf(_counter);
    }
}
