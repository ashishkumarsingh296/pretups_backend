package com.inter.kenan;

import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceUtil;

/**
 * @(#)KenanTestXMLParser
 *                        Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ashish Kumar Nov 22, 2006 Initial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 *                        This class is responsible to parse the xml request
 */
public class KenanTestXMLParser {
    public Log _log = LogFactory.getLog(this.getClass().getName());
    String userid = null;
    String passwd = null;
    HashMap rqMap = null;

    public KenanTestXMLParser() {

    }

    public KenanTestXMLParser(HashMap p_responseMap) {
        this.rqMap = p_responseMap;
        this.userid = (String) rqMap.get("userid");
        this.passwd = (String) rqMap.get("passwd");
    }

    /**
     * This method is used parse the Recharge request.
     * 
     * @param String
     *            p_requestStr
     * @return HashMap
     * @throws Exception
     */

    public HashMap parseRechargeCreditRequest(String p_requestStr) {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditRequest", "Entered p_requestStr: " + p_requestStr);
        HashMap requestMap = null;
        int indexStart = 0;
        try {
            requestMap = new HashMap();
            requestMap.put("MULTIPLICATION_FACTOR", (String) rqMap.get("MULTIPLICATION_FACTOR"));
            indexStart = p_requestStr.indexOf("<sub-id>");
            if (indexStart > 0) {
                String subID = p_requestStr.substring("<sub-id>".length() + indexStart, p_requestStr.indexOf("</sub-id>", indexStart));
                requestMap.put("sub-id", subID.trim());
            }
            indexStart = p_requestStr.indexOf("<pretup-trans-id>");
            String transactionId = p_requestStr.substring("<pretup-trans-id>".length() + indexStart, p_requestStr.indexOf("</pretup-trans-id>", indexStart));
            requestMap.put("pretup-trans-id", transactionId);

            indexStart = p_requestStr.indexOf("<user-id>");
            String userID = p_requestStr.substring("<user-id>".length() + indexStart, p_requestStr.indexOf("</user-id>", indexStart));
            requestMap.put("user-id", userID.trim());

            indexStart = p_requestStr.indexOf("<password>");
            String password = p_requestStr.substring("<password>".length() + indexStart, p_requestStr.indexOf("</password>", indexStart));
            requestMap.put("password", password.trim());
            if (!InterfaceUtil.isNullString(userid)) {
                if (!userID.equals(userid))
                    throw new BTSLBaseException(KenanI.DATA_ERROR);
                if (!password.equals(passwd))
                    throw new BTSLBaseException(KenanI.DATA_ERROR);
            }
            indexStart = p_requestStr.indexOf("<distributor-ref>");
            String distributorRef = p_requestStr.substring("<distributor-ref>".length() + indexStart, p_requestStr.indexOf("</distributor-ref>", indexStart));
            requestMap.put("distributor-ref", distributorRef.trim());

            indexStart = p_requestStr.indexOf("<retailer-ref>");
            String retailerRef = p_requestStr.substring("<retailer-ref>".length() + indexStart, p_requestStr.indexOf("</retailer-ref>", indexStart));
            requestMap.put("retailer-ref", retailerRef.trim());

            indexStart = p_requestStr.indexOf("<amount>");
            String amount = p_requestStr.substring("<amount>".length() + indexStart, p_requestStr.indexOf("</amount>", indexStart));
            requestMap.put("amount", amount.trim());
            indexStart = p_requestStr.indexOf("<bonus>");
            if (indexStart > 0) {
                String bonus = p_requestStr.substring("<bonus>".length() + indexStart, p_requestStr.indexOf("</bonus>", indexStart));
                requestMap.put("bonus", bonus.trim());
            }

            indexStart = p_requestStr.indexOf("<voucher-type>");
            String voucherType = p_requestStr.substring("<voucher-type>".length() + indexStart, p_requestStr.indexOf("</voucher-type>", indexStart));
            requestMap.put("voucher-type", voucherType.trim());

            indexStart = p_requestStr.indexOf("<date-time>");
            String transDateTime = p_requestStr.substring("<date-time>".length() + indexStart, p_requestStr.indexOf("</date-time>", indexStart));
            requestMap.put("date-time", transDateTime.trim());
            requestMap.put("error-code", KenanI.RESULT_OK);
        } catch (BTSLBaseException be) {
            requestMap.put("error-code", be.getMessage());
            requestMap.put("error-desc", "Authentication Failed");
            return requestMap;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("parseRechargeCreditRequest", "Exception e:" + e.getMessage());
            requestMap.put("error-code", KenanI.PARSING_ERROR);
            requestMap.put("error-desc", "During the request parsing::ERROR[" + e.getMessage() + "]");
            return requestMap;
        }// end of catch-Exception
        return requestMap;
    }// end of parseRechargeCreditRequest

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {

            String lineSep = System.getProperty("line.separator");
            StringBuffer responseBuffer = new StringBuffer();
            responseBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lineSep);
            responseBuffer.append("<tatasky>" + lineSep);
            responseBuffer.append("<e-voucher-topup-response>" + lineSep);
            responseBuffer.append("<sub-id>293847399231</sub-id>" + lineSep);
            responseBuffer.append("<pretup-trans-id>I343D456</pretup-trans-id>" + lineSep);
            responseBuffer.append("<user-id>Ashish</user-id>" + lineSep);
            responseBuffer.append("<password>1357</password>" + lineSep);
            responseBuffer.append("<distributor-ref>87654</distributor-ref>" + lineSep);
            responseBuffer.append("<retailer-ref>33432</retailer-ref>" + lineSep);
            responseBuffer.append("<amount>600</amount>" + lineSep);
            responseBuffer.append("<bonus>100</bonus>" + lineSep);
            responseBuffer.append("<voucher-type></voucher-type>" + lineSep);
            responseBuffer.append("<date-time>09-20-2006 10:13:25</date-time>" + lineSep);
            responseBuffer.append("</e-voucher-topup-response>" + lineSep);
            responseBuffer.append("</tatasky>" + lineSep);
            String requestStr = responseBuffer.toString();
            KenanTestXMLParser testReqParser = new KenanTestXMLParser();
            System.out.println(testReqParser.parseRechargeCreditRequest(requestStr));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Main Exception e::" + e.getMessage());
        }

    }
}
