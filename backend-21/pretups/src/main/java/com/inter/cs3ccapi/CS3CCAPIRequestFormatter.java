package com.inter.cs3ccapi;

import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @CS3CCAPIRequestFormatter.java
 *                                Copyright(c) 2007, Bharti Telesoft Int. Public
 *                                Ltd.
 *                                All Rights Reserved
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Ashish K Jan 31, 2007 Initial Creation
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                ----
 *                                This class is responsible to generate the
 *                                request and parse the response for the
 *                                CS3-Ericssion interface.
 */
public class CS3CCAPIRequestFormatter {
    public Log _log = LogFactory.getLog("CS3CCAPIRequestFormatter".getClass().getName());
    private String _colonSep = ":";

    public CS3CCAPIRequestFormatter() {
        _colonSep.intern();
    }

    /**
     * This method is used to generate the INRecon id.
     * 
     * @param HashMap
     *            p_requestMap
     * @return String
     * @throws Exception
     */
    private String getINReconTxnID(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINReconTxnID", "Entered p_requestMap :: " + p_requestMap);
        String inReconID = null;
        try {
            String userType = (String) p_requestMap.get("USER_TYPE");
            if (userType != null)
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID") + "." + userType.trim());
            else
                inReconID = ((String) p_requestMap.get("TRANSACTION_ID"));
            p_requestMap.put("IN_RECON_ID", inReconID);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getINReconTxnID", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getINReconTxnID", "Exited inReconID :: " + inReconID);
        }
        return inReconID;
    }

    /**
     * This method is used generate the request string based on the action type.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    public String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action = " + p_action + " p_map :: " + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case CS3CCAPII.ACTION_LOGIN: {
                str = generateLoginRequest(p_map);
                break;
            }
            case CS3CCAPII.ACTION_LOGOUT: {
                // str=generateLogoutRequest(p_map);
                break;
            }
            case CS3CCAPII.ACTION_ACCOUNT_INFO: {
                str = generateGetAccountInfoRequest(p_map);
                break;
            }
            case CS3CCAPII.ACTION_RECHARGE_CREDIT: {
                str = generateRechargeCreditRequest(p_map);
                break;
            }
            case CS3CCAPII.ACTION_IMMEDIATE_DEBIT: {
                str = generateImmediateDebitRequest(p_map);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("generateRequest", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String :: " + str);
        }// end of finally
        return str;
    }// end of generateRequest

    /**
     * This method parse the response String into HashMap based on the action.
     * 
     * @param int action
     * @param String
     *            responseStr
     * @return HashMap map
     * @throws Exception
     */
    public HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action :: " + p_action + " p_responseStr :: " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case CS3CCAPII.ACTION_LOGIN: {
                map = parseLoginResponse(p_responseStr);
                break;
            }
            case CS3CCAPII.ACTION_LOGOUT: {
                // map=parseLogoutResponse(p_responseStr);
                break;
            }
            case CS3CCAPII.ACTION_ACCOUNT_INFO: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case CS3CCAPII.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case CS3CCAPII.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            e.printStackTrace();
            _log.error("parseResponse", "Exception e = " + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map :: " + map);
        }// end of finally
        return map;
    }// end of parseResponse

    /**
     * This method generates the request for login
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateLoginRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLoginRequest", "Entered p_map :: " + p_map);
        String requestStr = null;
        StringBuffer requestBfr = null;
        try {
            requestBfr = new StringBuffer(1028);
            requestBfr.append("LOGIN");
            requestBfr.append(_colonSep);
            requestBfr.append(p_map.get("USER_NAME"));
            requestBfr.append(_colonSep);
            requestBfr.append(p_map.get("PASSWORD"));
            requestBfr.append(";");
            requestStr = requestBfr.toString();
        } catch (Exception e) {
            _log.error("generateLoginRequest", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateLoginRequest", "Exiting  requestStr :: " + requestStr);
        }
        return requestStr;
    }// end of generateLoginRequest

    /**
     * This method pareses the response of login request
     * 
     * @param String
     *            p_responseStr
     * @return HashMap responseMap
     * @throws Exception
     */
    private HashMap parseLoginResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLoginResponse", "Entered p_responseStr :: " + p_responseStr);
        HashMap responseMap = null;
        int index = 0;
        try {
            responseMap = new HashMap();
            index = p_responseStr.indexOf("RESP:");
            String responseCode = p_responseStr.substring(index + "RESP:".length(), p_responseStr.indexOf(";", index));
            responseMap.put("response_status", responseCode.trim());
        } catch (Exception e) {
            _log.error("parseLoginResponse", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLoginResponse", "Exiting responseMap :: " + responseMap);
        }
        return responseMap;
    }// end of parseLoginResponse

    /**
     * This methods generates the request for User interrogation
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateGetAccountInfoRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateGetAccountInfoRequest", "Entered p_map :: " + p_map);
        String requestStr = null;
        StringBuffer requestBfr = null;
        try {
            requestBfr = new StringBuffer(1028);
            // GET:ACCOUNTINFORMATION:SubscriberNumber,<Phone Number>;
            requestBfr.append("GET");
            requestBfr.append(_colonSep);
            requestBfr.append("ACCOUNTINFORMATION");
            requestBfr.append(_colonSep);
            requestBfr.append("SubscriberNumber,");
            requestBfr.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            requestBfr.append(";");
            requestStr = requestBfr.toString();
        } catch (Exception e) {
            _log.error("generateGetAccountInfoRequest", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateGetAccountInfoRequest", "Exited requestStr :: " + requestStr);
        }
        return requestStr;
    }// end of generateGetAccountInfoRequest

    /**
     * This method is used to parse the response Account information request
     * 
     * @param String
     *            p_responseStr
     * @return HashMap responseMap
     * @throws Exception
     */
    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered responseStr :: " + p_responseStr);
        HashMap responseMap = null;
        int index = 0;
        try {
            responseMap = new HashMap();
            index = p_responseStr.indexOf("RESP:");
            // In case of any Error the response string is RESP:errorCode;
            // (ended with ; instead of :)
            String responseCode = p_responseStr.substring(index + "RESP:".length(), (p_responseStr.indexOf(_colonSep, index + "RESP:".length()) > 0 ? p_responseStr.indexOf(_colonSep, index + "RESP:".length()) : p_responseStr.indexOf(";", index + "RESP:".length())));
            responseMap.put("response_status", responseCode.trim());
            if (!(CS3CCAPII.RESULT_OK.equals(responseCode.trim())))
                return responseMap;
            index = p_responseStr.indexOf("AccountBalance,");
            if (index > 0) {
                String creditBalance = p_responseStr.substring(index + "AccountBalance,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("credit_balance", creditBalance.trim());
            }
            index = p_responseStr.indexOf("ServiceClass,");
            String serviceClass = p_responseStr.substring(index + "ServiceClass,".length(), p_responseStr.indexOf(_colonSep, index));
            responseMap.put("service_class", serviceClass.trim());
            index = p_responseStr.indexOf("ServiceFeePeriodExpiryDate,");
            if (index > 0) {
                String serviceFeeDateAfter = p_responseStr.substring(index + "ServiceFeePeriodExpiryDate,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("end_val_date", serviceFeeDateAfter.trim());
            }
            index = p_responseStr.indexOf("SupervisionPeriodExpiryDate,");
            if (index > 0) {
                String supervisionPeriodExpiryDateAfter = p_responseStr.substring(index + "SupervisionPeriodExpiryDate,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("end_inact_date", supervisionPeriodExpiryDateAfter.trim());
            }
            index = p_responseStr.indexOf("Status,");
            if (index > 0) {
                String status = p_responseStr.substring(index + "Status,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("account_status", status.trim());
            }
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }// end of parseGetAccountInfoResponse

    /**
     * This method is used to generates the request for recharge.
     * 
     * @param HashMap
     *            p_map
     * @return String
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRechargeCreditRequest", "Entered p_map= " + p_map);
        String requestStr = null;
        StringBuffer requestBfr = null;
        try {
            requestBfr = new StringBuffer(1028);
            // Format of request String.
            // SET:REFILL:SubscriberNumber,<Subscriberphone
            // number>:TransactionAmount,<Transaction amount>:
            // Currency,<Currency>:PaymentProfile,<Payment
            // profile>:ExternalData1,<External Data1>:
            // ExternalData2,<ExternalData2>;
            requestBfr.append("SET");
            requestBfr.append(_colonSep);
            requestBfr.append("REFILL");
            requestBfr.append(_colonSep);
            requestBfr.append("SubscriberNumber,");
            requestBfr.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            requestBfr.append(_colonSep);
            requestBfr.append("TransactionAmount,");
            requestBfr.append(p_map.get("transfer_amount"));
            requestBfr.append(_colonSep);
            requestBfr.append("Currency,");
            requestBfr.append(p_map.get("CURRENCY"));
            requestBfr.append(_colonSep);
            requestBfr.append("PaymentProfile,");
            requestBfr.append(p_map.get("CARD_GROUP"));
            requestBfr.append(_colonSep);
            // ExternalData1 is the optional parameter and would be used to
            // contain the INRecon id.
            requestBfr.append("ExternalData1,");
            requestBfr.append(getINReconTxnID(p_map));
            requestBfr.append(_colonSep);
            // Value of ExternalData2 is optional parameter and it contains the
            // constant value defined in the INFile if it is not provided by the
            // controller.
            requestBfr.append("ExternalData2,");
            requestBfr.append(p_map.get("ExternalData2") == null ? FileCache.getValue((String) p_map.get("INTERFACE_ID"), "EXTERNAL_DATA2") : p_map.get("ExternalData2"));
            requestBfr.append(";");
            requestStr = requestBfr.toString();
        } catch (Exception e) {
            _log.error("generateRechargeCreditRequest", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRechargeCreditRequest", "Exited requestStr :: " + requestStr);
        }
        return requestStr;
    }// end of generateRechargeCreditRequest

    /**
     * This method is used to parse the response of recharge request
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered p_responseStr :: " + p_responseStr);
        HashMap responseMap = null;
        int index = 0;
        try {
            responseMap = new HashMap();
            index = p_responseStr.indexOf("RESP:");
            // String
            // responseCode=p_responseStr.substring(index+"RESP:".length(),p_responseStr.indexOf(_colonSep,index+"RESP:".length()));
            // In case of any Error the response string is RESP:errorCode;
            // (ended with ; instead of :)
            String responseCode = p_responseStr.substring(index + "RESP:".length(), (p_responseStr.indexOf(_colonSep, index + "RESP:".length()) > 0 ? p_responseStr.indexOf(_colonSep, index + "RESP:".length()) : p_responseStr.indexOf(";", index + "RESP:".length())));
            responseMap.put("response_status", responseCode.trim());
            if (!(CS3CCAPII.RESULT_OK.equals(responseCode.trim())))
                return responseMap;
            // OriginTransactionID is optional field in case of Refill response.
            index = p_responseStr.indexOf("OriginTransactionID,");
            if (index > 0) {
                String orgTxnID = p_responseStr.substring(index + "OriginTransactionID,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("org_txn_id", orgTxnID.trim());
            }
            // Service is optional field in case of Refill response.
            index = p_responseStr.indexOf("ServiceClassCurrent,");
            if (index > 0) {
                String serviceClass = p_responseStr.substring(index + "ServiceClassCurrent,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("service_class", serviceClass.trim());
            }
            // AccountValueAfter is optional field in case of Refill response.
            index = p_responseStr.indexOf("AccountValueAfter,");
            if (index > 0) {
                String creditBalance = p_responseStr.substring(index + "AccountValueAfter,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("credit_balance", creditBalance.trim());
            }
            // ServiceFeeDateAfter is optional field in case of Refill response.
            index = p_responseStr.indexOf("ServiceFeeDateAfter,");
            if (index > 0) {
                String serviceFeeDateAfter = p_responseStr.substring(index + "ServiceFeeDateAfter,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("end_val_date", serviceFeeDateAfter.trim());
            }
            // SupervisionPeriodExpiryDateAfter is optional field in case of
            // Refill response.
            index = p_responseStr.indexOf("SupervisionPeriodExpiryDateAfter,");
            if (index > 0) {
                String supervisionPeriodExpiryDateAfter = p_responseStr.substring(index + "SupervisionPeriodExpiryDateAfter,".length(), p_responseStr.indexOf(_colonSep, index));
                responseMap.put("end_inact_date", supervisionPeriodExpiryDateAfter.trim());
            }
            // Status is optional field in case of Refill response.
            index = p_responseStr.indexOf("Status,");
            if (index > 0) {
                // In the remote testing the status of Recharge is ended with ;
                // not with :
                // String
                // status=p_responseStr.substring(index+"Status,".length(),p_responseStr.indexOf(_colonSep,index));
                String status = p_responseStr.substring(index + "Status,".length(), p_responseStr.indexOf(";", index));
                responseMap.put("account_status", status.trim());
            }
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap:" + responseMap);
        }
        return responseMap;
    }// end of parseRechargeCreditResponse

    /**
     * This Method is used to generate the request for account
     * adjustment(Credit/Debit).
     * 
     * @param HashMap
     *            p_map
     * @return String requestString
     * @throws Exception
     */
    private String generateImmediateDebitRequest(HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateImmediateDebitRequest", "Entered map :: " + p_map);
        String requestString = null;
        StringBuffer requestBfr = null;
        try {
            requestBfr = new StringBuffer(1028);
            // Format of request String.
            // SET:ACCOUNTADJUSTMENT:SubscriberNumber,<Subscriberphone
            // number>:Currency,<Currency>:
            // Action,<ADD |SUBTRACT>:AdjustmentAmount,<Adjustment amount>;
            requestBfr.append("SET");
            requestBfr.append(_colonSep);
            requestBfr.append("ACCOUNTADJUSTMENT");
            requestBfr.append(_colonSep);
            requestBfr.append("SubscriberNumber,");
            requestBfr.append(InterfaceUtil.getFilterMSISDN((String) p_map.get("INTERFACE_ID"), (String) p_map.get("MSISDN")));
            requestBfr.append(_colonSep);
            requestBfr.append("Currency,");
            requestBfr.append(p_map.get("CURRENCY"));
            requestBfr.append(_colonSep);
            requestBfr.append("Action,");
            requestBfr.append(p_map.get("ADJUST_ACTION"));
            requestBfr.append(_colonSep);
            requestBfr.append("AdjustmentAmount,");
            requestBfr.append(p_map.get("transfer_amount"));
            requestBfr.append(";");
            requestString = requestBfr.toString();
        } catch (Exception e) {
            _log.error("generateImmediateDebitRequest", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateImmediateDebitRequest", "Exiting requestString :: " + requestString);
        }
        return requestString;
    }// end of generateImmediateDebitRequest

    /**
     * This method is used to parse the response of account adjustment request.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap responseMap
     * @throws Exception
     */
    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered responseStr :: " + p_responseStr);
        HashMap responseMap = null;
        int index = 0;
        try {
            responseMap = new HashMap();
            // The response syntax of Debit/Credit command is as
            // bellow.RESP:0:AccountBalance,<Account balance>:
            // CurrencyLabel,<Currency Label>:CurrencySymbol,<Currency
            // Symbol>:Status,<Status>;
            index = p_responseStr.indexOf("RESP:");
            // String
            // responseCode=p_responseStr.substring(index+"RESP:".length(),p_responseStr.indexOf(_colonSep,index+"RESP:".length()));
            // In case of any Error the response string is RESP:errorCode;
            // (ended with ; instead of :)
            String responseCode = p_responseStr.substring(index + "RESP:".length(), (p_responseStr.indexOf(_colonSep, index + "RESP:".length()) > 0 ? p_responseStr.indexOf(_colonSep, index + "RESP:".length()) : p_responseStr.indexOf(";", index + "RESP:".length())));
            responseMap.put("response_status", responseCode.trim());
            if (!(CS3CCAPII.RESULT_OK.equals(responseCode.trim())))
                return responseMap;
            index = p_responseStr.indexOf("AccountBalance,");
            String creditBalance = p_responseStr.substring(index + "AccountBalance,".length(), p_responseStr.indexOf(_colonSep, index));
            responseMap.put("credit_balance", creditBalance.trim());
            index = p_responseStr.indexOf("AccountFlagsAfter,");
            if (index > 0) {
                String status = p_responseStr.substring(index + "AccountFlagsAfter,".length(), p_responseStr.indexOf(";", index));
                responseMap.put("account_status", status.trim());
            }
        } catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e :: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exited responseMap :: " + responseMap);
        }
        return responseMap;
    }
}
