/*
 * Created on Aug 4, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cs3cp6;

import java.util.HashMap;

/**
 * @author temp
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class temptest {

    public static void main(String[] args) {
        String requestString = "<?xml version='1.0' encoding='utf-8'?><methodResponse><params><param><value><struct><member><name>accountAfterRefill</name><value><struct><member><name>accountFlags</name><value><struct><member><name>activationStatusFlag</name><value><boolean>1</boolean></value></member><member><name>negativeBarringStatusFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>serviceFeePeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodExpiryFlag</name><value><boolean>0</boolean></value></member><member><name>supervisionPeriodWarningActiveFlag</name><value><boolean>0</boolean></value></member></struct></value></member><member><name>accountValue1</name><value><string>16700</string></value></member><member><name>serviceClassCurrent</name><value><i4>101</i4></value></member></struct></value></member><member><name>currency1</name><value><string>EGP</string></value></member><member><name>languageIDCurrent</name><value><i4>1</i4></value></member><member><name>masterAccountNumber</name><value><string>171000001</string></value></member><member><name>originTransactionID</name><value><string>808080414150200022</string></value></member><member><name>responseCode</name><value><i4>1</i4></value></member><member><name>transactionAmount</name><value><string>100</string></value></member><member><name>transactionCurrency</name><value><string>EGP</string></value></member><member><name>voucherGroup</name><value><string>02</string></value></member></struct></value></param></params></methodResponse>";
        temptest temptest = new temptest();
        try {
            // temptest.parseGetAccountInfoResponse(requestString);

            temptest.parseRechargeCreditResponse(requestString);

        } catch (Exception e) {
            System.out.println("Execption main" + e);
        }

    }

    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
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

            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!"0".equals(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceClassCurrent", indexStart);
            if (tempIndex > 0) {
                String serviceClassCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("serviceClassCurrent", serviceClassCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionExpiryDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("languageIDCurrent", indexStart);
            if (tempIndex > 0) {
                String languageIDCurrent = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("languageIDCurrent", languageIDCurrent.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("temporaryBlockedFlag", indexStart);
            if (tempIndex > 0) {
                String temporaryBlockedFlag = p_responseStr.substring("<boolean>".length() + p_responseStr.indexOf("<boolean>", tempIndex), p_responseStr.indexOf("</boolean>", tempIndex)).trim();
                responseMap.put("temporaryBlockedFlag", getDateString(temporaryBlockedFlag));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {
            throw e;
        }// end catch-Exception
        finally {
            System.out.println("Exit");
        }// end of finally
        return responseMap;
    }// end of parseGetAccountInfoResponse

    public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception {
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

            indexStart = p_responseStr.indexOf("<member>");
            tempIndex = p_responseStr.indexOf("responseCode", indexStart);
            if (tempIndex > 0) {
                responseCode = p_responseStr.substring("<i4>".length() + p_responseStr.indexOf("<i4>", tempIndex), p_responseStr.indexOf("</i4>", tempIndex)).trim();
                responseMap.put("responseCode", responseCode.trim());
                if (!"0".equals(responseCode))
                    return responseMap;
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("originTransactionID", indexStart);
            if (tempIndex > 0) {
                String originTransactionID = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("originTransactionID", originTransactionID.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("transactionAmount", indexStart);
            if (tempIndex > 0) {
                String transactionAmount = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("transactionAmount", transactionAmount.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("serviceFeeExpiryDate", indexStart);
            if (tempIndex > 0) {
                String serviceFeeExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("serviceFeeExpiryDate", getDateString(serviceFeeExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("supervisionExpiryDate", indexStart);
            if (tempIndex > 0) {
                String supervisionExpiryDate = p_responseStr.substring("<dateTime.iso8601>".length() + p_responseStr.indexOf("<dateTime.iso8601>", tempIndex), p_responseStr.indexOf("</dateTime.iso8601>", tempIndex)).trim();
                responseMap.put("supervisionExpiryDate", getDateString(supervisionExpiryDate.trim()));
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }

            indexStart = p_responseStr.indexOf("<member>", indexEnd);
            tempIndex = p_responseStr.indexOf("accountValue1", indexStart);
            if (tempIndex > 0) {
                String accountValue1 = p_responseStr.substring("<string>".length() + p_responseStr.indexOf("<string>", tempIndex), p_responseStr.indexOf("</string>", tempIndex)).trim();
                responseMap.put("accountValue1", accountValue1.trim());
                indexEnd = p_responseStr.indexOf("</member>", indexStart);
            }
        } catch (Exception e) {

            throw e;
        }// end of catch-Exception
        finally {

        }// end of finally
        return responseMap;
    }// end of parseRechargeCreditResponse

    public String getDateString(String p_dateStr) throws Exception {
        String dateStr = "";
        try {
            dateStr = p_dateStr.substring(0, p_dateStr.indexOf("T")).trim();
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.println("getDateString EXIT");
        }
        return dateStr;
    }

}
