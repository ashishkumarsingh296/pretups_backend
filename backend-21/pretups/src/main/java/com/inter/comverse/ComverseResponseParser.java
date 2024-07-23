package com.inter.comverse;

import java.util.Date;
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class ComverseResponseParser {

    private static Log _log = LogFactory.getLog(ComverseResponseParser.class.getName());

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
            case ComverseI.ACTION_ACCOUNT_DETAILS: {
                map = parseGetAccountInfoResponse(p_responseStr);
                break;
            }
            case ComverseI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case ComverseI.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
                break;
            }
            case ComverseI.ACTION_IMMEDIATE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
                break;
            }
            case ComverseI.ACTION_LANGUAGE_CODE: {
                map = parseLanguageCodeResponse(p_responseStr);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map::" + map);
        }
        return map;
    }

    /**
     * This method is used to parse the response of GetAccountinformation.
     * 
     * @param String
     *            p_responseStr
     * @return HashMap
     */
    private HashMap parseGetAccountInfoResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            // responseMap = parsevalidatexml(new BufferedReader(new
            // StringReader(p_responseStr)));
            int index = p_responseStr.indexOf("<Balance>");
            if (index > 0) {
                String balance = p_responseStr.substring(index + "<Balance>".length(), p_responseStr.indexOf("</Balance>", index));
                responseMap.put("Balance", balance);
            }
            index = p_responseStr.indexOf("<Expire_Date>");
            if (index > 0) {
                String accExp = p_responseStr.substring(index + "<Expire_Date>".length(), p_responseStr.indexOf("</Expire_Date>", index));
                responseMap.put("Expire_Date", accExp);
            }

            index = p_responseStr.indexOf("<COS>");
            if (index > 0) {
                String cos = p_responseStr.substring(index + "<COS>".length(), p_responseStr.indexOf("</COS>", index));
                responseMap.put("COS", cos);
            }
            // Lohit for sos aon
            index = p_responseStr.indexOf("<" + PretupsI.AON_TAG + ">");
            if (index > 0) {
                String aon = p_responseStr.substring(index + ("<" + PretupsI.AON_TAG + ">").length(), p_responseStr.indexOf(("</" + PretupsI.AON_TAG + ">"), index));
                responseMap.put("AON", new Date(aon));
            }
            index = p_responseStr.indexOf("<State>");
            if (index > 0) {
                String state = p_responseStr.substring(index + "<State>".length(), p_responseStr.indexOf("</State>", index));
                responseMap.put("State", state);
            }
            index = p_responseStr.indexOf("<ErrorCode>");
            if (index >= 0) {
                String errorCode = p_responseStr.substring(index + "<ErrorCode>".length(), p_responseStr.indexOf("</ErrorCode>", index));
                responseMap.put("ErrorCode", errorCode);
            } else
                responseMap.put("ErrorCode", "0");
            index = p_responseStr.indexOf("<AvailableBalance>");
            if (index > 0) {
                String balance = p_responseStr.substring(index + "<AvailableBalance>".length(), p_responseStr.indexOf("</AvailableBalance>", index));
                responseMap.put("AvailableBalance", balance);
            }
            index = p_responseStr.indexOf("<BARRED_STATUS>");
            if (index > 0) {
                String barringStatus = p_responseStr.substring(index + "<BARRED_STATUS>".length(), p_responseStr.indexOf("</BARRED_STATUS>", index));
                responseMap.put("BARRED_STATUS", barringStatus);
            }
            
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

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
        try {
            responseMap = new HashMap();
            // responseMap = parsevalidatexml(new BufferedReader(new
            // StringReader(p_responseStr)));
            int index = p_responseStr.indexOf("<NonVoucherRechargeResult>");
            if (index > 0) {
                String resp = p_responseStr.substring(index + "<NonVoucherRechargeResult>".length(), p_responseStr.indexOf("</NonVoucherRechargeResult>", index));
                responseMap.put("NonVoucherRechargeResult", resp);
            }
            index = p_responseStr.indexOf("<ErrorCode>");
            if (index >= 0) {
                String errorCode = p_responseStr.substring(index + "<ErrorCode>".length(), p_responseStr.indexOf("</ErrorCode>", index));
                responseMap.put("ErrorCode", errorCode);
            } else
                responseMap.put("ErrorCode", "0");

        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

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
        try {
            responseMap = new HashMap();
            // responseMap = parsevalidatexml(new BufferedReader(new
            // StringReader(p_responseStr)));
            int index = p_responseStr.indexOf("<NonVoucherRechargeResult>");
            if (index > 0) {
                String resp = p_responseStr.substring(index + "<NonVoucherRechargeResult>".length(), p_responseStr.indexOf("</NonVoucherRechargeResult>", index));
                responseMap.put("NonVoucherRechargeResult", resp);
            }
            index = p_responseStr.indexOf("<ErrorCode>");
            if (index >= 0) {
                String errorCode = p_responseStr.substring(index + "<ErrorCode>".length(), p_responseStr.indexOf("</ErrorCode>", index));
                responseMap.put("ErrorCode", errorCode);
            } else
                responseMap.put("ErrorCode", "0");
        } catch (Exception e) {
            _log.error("parseImmediateDebitResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseImmediateDebitResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    /*
     * public HashMap<String,String> parsevalidatexml(BufferedReader InResp)
     * {
     * if(_log.isDebugEnabled()) _log.debug("parsevalidatexml","Entered...");
     * HashMap<String,String> ht = new HashMap<String,String>();
     * XMLParser xmlParser = new XMLParser(InResp);
     * 
     * if(xmlParser.isValidate){
     * String root=xmlParser.getRootElement();
     * ArrayList<Node> arr=null;
     * if(root!=null){
     * arr=xmlParser.getAllChildElement(root);
     * }
     * 
     * if(arr!=null){
     * for(int i=0;i<arr.size();i++){
     * Node n=(Node)arr.get(i);
     * if(n.getNodeName().equals("<soap:Body>")){
     * ht=xmlParser.forDisp(n);
     * }
     * }
     * }
     * }
     * System.out.println("parsevalidatexml exited with response map::"+ht);
     * if(_log.isDebugEnabled()) _log.debug("parsevalidatexml","exited");
     * return ht;
     * }
     */

    public HashMap parseLanguageCodeResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLanguageCodeResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        // String[] respBalances= null;
        try {
            responseMap = new HashMap();
            int index = p_responseStr.indexOf("<NotificationLanguage>");
            if (index > 0) {
                String notificationLang = p_responseStr.substring(index + "<NotificationLanguage>".length(), p_responseStr.indexOf("</NotificationLanguage>", index));
                responseMap.put("NotificationLanguage", notificationLang);
            }
            index = p_responseStr.indexOf("<LanguageName>");
            if (index > 0) {
                String languageName = p_responseStr.substring(index + "<LanguageName>".length(), p_responseStr.indexOf("</LanguageName>", index));
                responseMap.put("LanguageName", languageName);
            }

            // respBalances= p_responseStr.split("<Balance><Balance>");

            // index =p_responseStr.indexOf("<BalanceName>");
            // String
            // balancName=p_responseStr.substring(index+"<BalanceName>".length(),p_responseStr.indexOf("</BalanceName>",index));
            // if("CORE".equalsIgnoreCase(balancName))
            // {
            /*
             * index =p_responseStr.indexOf("<Balance><Balance>");
             * if(index>0)
             * {
             * String
             * balance=p_responseStr.substring(index+"<Balance><Balance>".
             * length(),p_responseStr.indexOf("</Balance>",index));
             * responseMap.put("Balance",balance);
             * }
             * index =p_responseStr.indexOf("<AccountExpiration>");
             * if(index>0)
             * {
             * String
             * accExp=p_responseStr.substring(index+"<AccountExpiration>".
             * length(),p_responseStr.indexOf("</AccountExpiration>",index));
             * responseMap.put("AccountExpiration",accExp);
             * }
             */
            index = p_responseStr.indexOf("<CurrentState>");
            if (index > 0) {
                String state = p_responseStr.substring(index + "<CurrentState>".length(), p_responseStr.indexOf("</CurrentState>", index));
                responseMap.put("State", state);
            }

            index = p_responseStr.indexOf("<ErrorCode>");
            if (index >= 0) {
                String errorCode = p_responseStr.substring(index + "<ErrorCode>".length(), p_responseStr.indexOf("</ErrorCode>", index));
                responseMap.put("ErrorCode", errorCode);
            } else
                responseMap.put("ErrorCode", "0");
            // added for prepaid reversal by Vikas Singh
            index = p_responseStr.indexOf("<AvailableBalance>");
            if (index > 0) {
                String balance = p_responseStr.substring(index + "<AvailableBalance>".length(), p_responseStr.indexOf("</AvailableBalance>", index));
                responseMap.put("AvailableBalance", balance);
            }
            // end
        } catch (Exception e) {
            _log.error("parseLanguageCodeResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLanguageCodeResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

}
