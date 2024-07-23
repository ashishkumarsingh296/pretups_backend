package com.inter.cs3cp6;

import java.util.Arrays;
import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.inter.scheduler.NodeScheduler;

/*
 * Created on Nov 12, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author bharti
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CS3CP6ResponseParser {
    private static Log _log = LogFactory.getLog(CS3CP6ResponseParser.class.getName());

    public static void main(String args[]) {

        String accInfoResponseStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodResponse><params><param><value><struct><member><name>creditClearanceDate</name><value><dateTime.iso8601>20090219T12:00:00+0000</dateTime.iso8601></value></member><member><name>currency1</name><value><string>EGP</string></value></member><member><name>dedicatedAccountInformation</name><value><array><data><value><struct><member><name>dedicatedAccountID</name><value><i4>1</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value><value><struct><member><name>dedicatedAccountID</name><value><i4>2</i4></value></member><member><name>dedicatedAccountValue1</name><value><string>0</string></value></member><member><name>expiryDate</name><value><dateTime.iso8601>99991231T00:00:00+1200</dateTime.iso8601></value></member></struct></value></data></array></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>originTransactionID</name><value><string>808072215300263</string></value></member><member><name>responseCode</name><value><i4>0</i4></value></member><member><name>serviceClassCurrent</name><value><i4>101</i4></value></member><member><name>accountValue1</name><value><string>5400</string></value></member><member><name>serviceFeeExpiryDate</name><value><dateTime.iso8601>20081111T12:00:00+0000</dateTime.iso8601></value></member><member><name>serviceRemovalDate</name><value><dateTime.iso8601>20090219T12:00:00+0000</dateTime.iso8601></value></member><member><name>supervisionExpiryDate</name><value><dateTime.iso8601>20081111T12:00:00+0000</dateTime.iso8601></value></member></struct></value></param></params></methodResponse>";
        String rechargeResponse = "<?xml version=\"\"1.0\"\" encoding=\"\"utf-8\"\"?><methodResponse><params><param><value><struct><member><name>currency1</name><value><string>EGP</string></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>masterAccountNumber</name><value><string>171000001</string></value></member><member><name>responseCode</name><value><i4>1</i4></value></member><member><name>originTransactionID</name><value><string>808073010311218</string></value></member><member><name>transactionAmount</name><value><string>1500</string></value></member><member><name>transactionCurrency</name><value><string>EGP</string></value></member><member><name>voucherGroup</name><value><string>02</string></value></member></struct></value></param></params></methodResponse>";
        String debitResp = "";
        CS3CP6ResponseParser resParse = new CS3CP6ResponseParser();
        try {
            resParse.parseResponse(2, rechargeResponse);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

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
        // if(_log.isDebugEnabled())_log.debug("parseResponse","Entered p_action::"+p_action+" p_responseStr:: "+p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case CS3CP6I.ACTION_ACCOUNT_DETAILS: {
                map = parseGetAccountDetailsResponse(p_responseStr);
                System.out.println("responseCode " + map.get("responseCode"));
                System.out.println("originTransactionID " + map.get("originTransactionID"));
                System.out.println("accountValue1 " + map.get("accountValue1"));
                System.out.println("serviceClassCurrent " + map.get("serviceClassCurrent"));
                System.out.println("supervisionExpiryDate " + map.get("supervisionExpiryDate"));
                System.out.println("serviceFeeExpiryDate " + map.get("serviceFeeExpiryDate"));
                System.out.println("languageIDCurrent " + map.get("languageIDCurrent"));
                System.out.println("temporaryBlockedFlag " + map.get("temporaryBlockedFlag"));
                System.out.println("accountValue1 " + map.get("accountValue1"));
                System.out.println("firstIVRCallFlag " + map.get("firstIVRCallFlag"));
                System.out.println("activationStatusFlag " + map.get("activationStatusFlag"));
                break;
            }
            case CS3CP6I.ACTION_BALANCE_DATE: {
                map = parseGetBalanceAndDateResponse(p_responseStr);
                System.out.println("responseCode " + map.get("responseCode"));
                System.out.println("originTransactionID " + map.get("originTransactionID"));
                System.out.println("accountValue1 " + map.get("accountValue1"));
                System.out.println("serviceClassCurrent " + map.get("serviceClassCurrent"));
                System.out.println("supervisionExpiryDate " + map.get("supervisionExpiryDate"));
                System.out.println("serviceFeeExpiryDate " + map.get("serviceFeeExpiryDate"));
                System.out.println("creditClearanceDate " + map.get("creditClearanceDate"));
                System.out.println("serviceRemovalDate " + map.get("serviceRemovalDate"));
                System.out.println("languageIDCurrent " + map.get("languageIDCurrent"));
                System.out.println("temporaryBlockedFlag " + map.get("temporaryBlockedFlag"));
                System.out.println("accountValue1 " + map.get("accountValue1"));
                break;
            }
            case CS3CP6I.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                System.out.println("responseCode " + map.get("responseCode"));
                System.out.println("originTransactionID " + map.get("originTransactionID"));
                System.out.println("transactionAmount " + map.get("transactionAmount"));
                System.out.println("supervisionExpiryDate " + map.get("supervisionExpiryDate"));
                System.out.println("serviceFeeExpiryDate " + map.get("serviceFeeExpiryDate"));
                System.out.println("accountValue1 " + map.get("accountValue1"));
                break;
            }
            case CS3CP6I.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);

                break;
            }
            }// end of switch block
        }// end of try block
        catch (Exception e) {
            // _log.error("parseResponse","Exception e::"+e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            // if(_log.isDebugEnabled())_log.debug("parseResponse","Exiting map::"+map);
        }// end of finally
        return map;
    }// end of parseResponse

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
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();

            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<value><i4>".length() + p_responseStr.indexOf("<value><i4>", tempIndex), p_responseStr.indexOf("</i4></value></member>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3CP6I.RESULT_OK.split(",");
                // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>originTransactionID", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>transactionAmount", indexEnd);
            tempIndex = p_responseStr.indexOf("transactionAmount", indexStart);
            if (tempIndex > 0) {
                String transactionAmount = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("transactionAmount", transactionAmount.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceFeeExpiryDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>supervisionExpiryDate", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionExpiryDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceRemovalDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceRemovalDate", indexStart);
            if (tempIndex > 0) {
                String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionExpiryDate", getDateString(serviceRemovalDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>accountValue1", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseRechargeCreditResponse

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetAccountDetailsResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountDetailsResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<value><i4>".length() + p_responseStr.indexOf("<value><i4>", tempIndex), p_responseStr.indexOf("</i4></value></member>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3CP6I.RESULT_OK.split(",");
                // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>originTransactionID", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceClassCurrent", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>accountValue1", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceFeeExpiryDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            // by Gopal
            indexStart = p_responseStr.indexOf("<member><name>supervisionExpiryDate", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionExpiryDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                if (InterfaceUtil.isNullString(supervisionExpiryDate) || ("0".equals(supervisionExpiryDate)))
                    responseMap.put("supervisionExpiryDate", supervisionExpiryDate);
                else
                    responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate.trim()));

                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>firstIVRCallFlag", indexEnd);
            tempIndex = p_responseStr.indexOf("firstIVRCallFlag", indexStart);
            if (tempIndex > 0) {
                String firstIVRCallFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("firstIVRCallFlag", firstIVRCallFlag.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>activationStatusFlag", indexEnd);
            tempIndex = p_responseStr.indexOf("activationStatusFlag", indexStart);
            if (tempIndex > 0) {
                String activationStatusFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("temporaryBlockedFlag", activationStatusFlag.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceRemovalDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceRemovalDate", indexStart);
            if (tempIndex > 0) {
                String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceRemovalDate", getDateString(serviceRemovalDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>languageIDCurrent", indexEnd);
            tempIndex = p_responseStr.indexOf("languageIDCurrent", indexStart);
            if (tempIndex > 0) {
                String languageIDCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("languageIDCurrent", languageIDCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>temporaryBlockedFlag", indexEnd);
            tempIndex = p_responseStr.indexOf("temporaryBlockedFlag", indexStart);
            if (tempIndex > 0) {
                String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("temporaryBlockedFlag", temporaryBlockedFlag.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseGetAccountDetailsResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountDetailsResponse", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseGetAccountDetailsResponse

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();

            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<value><i4>".length() + p_responseStr.indexOf("<value><i4>", tempIndex), p_responseStr.indexOf("</i4></value></member>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3CP6I.RESULT_OK.split(",");
                // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>originTransactionID", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }// end of parseImmediateDebitResponse

    /**
     * Method to get the Transaction date and time with specified format.
     * 
     * @return String
     * @throws Exception
     */

    /**
     * This method is used to convert the date string into yyyyMMdd from
     * yyyyMMdd'T'HH:mm:ss
     * 
     * @param String
     *            p_dateStr
     * @return String
     */
    public String getDateString(String p_dateStr) throws Exception {
        // if(_log.isDebugEnabled())
        // _log.debug("getDateString","Entered p_dateStr::"+p_dateStr);
        String dateStr = "";
        try {
            dateStr = p_dateStr.substring(0, p_dateStr.indexOf("T")).trim();
        } catch (Exception e) {
            // _log.error("getDateString","Exception e::"+e.getMessage());
            throw e;
        } finally {
            // if(_log.isDebugEnabled())
            // _log.debug("getDateString","Exited dateStr::"+dateStr);
        }
        return dateStr;
    }

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetBalanceAndDateResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();
            indexStart = p_responseStr.indexOf("<fault>");
            if (indexStart > 0) {
                tempIndex = p_responseStr.indexOf("faultCode", indexStart);
                String faultCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</4>", tempIndex));
                responseMap.put("faultCode", faultCode.trim());
                tempIndex = p_responseStr.indexOf("faultString", tempIndex);
                String faultString = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex));
                responseMap.put("faultString", faultString.trim());
                return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>responseCode");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<value><i4>".length() + p_responseStr.indexOf("<value><i4>", tempIndex), p_responseStr.indexOf("</i4></value></member>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                Object[] successList = CS3CP6I.RESULT_OK.split(",");
                // if(!CS3MobinilI.RESULT_OK.equals(responseCode))
                if (!Arrays.asList(successList).contains(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member><name>originTransactionID", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<value><string>".length() + p_responseStr.indexOf("<value><string>", tempIndex), p_responseStr.indexOf("</string></value></member>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceClassCurrent", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>accountValue1", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceFeeExpiryDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>supervisionExpiryDate", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionExpiryDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                if (InterfaceUtil.isNullString(supervisionExpiryDate) || ("0".equals(supervisionExpiryDate)))
                    responseMap.put("supervisionExpiryDate", supervisionExpiryDate);
                else
                    responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate.trim()));

                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>serviceRemovalDate", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceRemovalDate", indexStart);
            if (tempIndex > 0) {
                String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceRemovalDate", getDateString(serviceRemovalDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>creditClearanceDate", indexEnd);
            tempIndex = p_responseStr.indexOf("creditClearanceDate", indexStart);
            if (tempIndex > 0) {
                String serviceRemovalDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("creditClearanceDate", getDateString(serviceRemovalDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>languageIDCurrent", indexEnd);
            tempIndex = p_responseStr.indexOf("languageIDCurrent", indexStart);
            if (tempIndex > 0) {
                String languageIDCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("languageIDCurrent", languageIDCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member><name>temporaryBlockedFlag", indexEnd);
            tempIndex = p_responseStr.indexOf("temporaryBlockedFlag", indexStart);
            if (tempIndex > 0) {
                String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("temporaryBlockedFlag", getDateString(temporaryBlockedFlag));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            _log.error("parseGetBalanceAndDateResponse", "Exception e::" + e.getMessage());
            throw e;
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetBalanceAndDateResponse", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseGetBalanceAndDateResponse

}
