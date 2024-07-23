package com.inter.claroca.vasth;

import java.util.HashMap;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class VASResponseParser {
    private static Log _log = LogFactory.getLog(VASResponseParser.class.getName());

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
            case VASTHI.ACTION_VALIDATE_REQUEST: {
                map = parseVASValidateRequest(p_responseStr);

                break;
            }
            case VASTHI.ACTION_RECHARGE_REQUEST: {
                map = parseVASCreditRequest(p_responseStr);
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
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseVASCreditRequest(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseVASCreditRequest", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        HashMap tempMap = null;
        String TXNSTATUS = null;
        String respcode, message, txn_id_resp;
        try {
            responseMap = new HashMap();
            // commented on 17 feb 2012
            tempMap = BTSLUtil.getStringToHash(p_responseStr, "&", "=");
            responseMap.put("RESPONSE_CODE", (String) tempMap.get("Response"));
            responseMap.put("MESSAGE", (String) tempMap.get("Message"));
            responseMap.put("TXN_ID_RESP", (String) tempMap.get("TransactionID"));
            /*
             * respcode=p_responseStr.substring("<STATUS>".length()+p_responseStr
             * .indexOf("<STATUS>"), p_responseStr.indexOf("</STATUS>"));
             * message=p_responseStr.substring("<MESSAGE>".length()+p_responseStr
             * .indexOf("<MESSAGE>"), p_responseStr.indexOf("</MESSAGE>"));
             * txn_id_resp=p_responseStr.substring("<TRANSACTIONID>".length()+
             * p_responseStr.indexOf("<TRANSACTIONID>"),
             * p_responseStr.indexOf("</TRANSACTIONID>"));
             * if(!BTSLUtil.isNullString(respcode))
             * responseMap.put("RESPONSE_CODE",respcode);
             * if(!BTSLUtil.isNullString(message))
             * responseMap.put("MESSAGE",message);
             * if(!BTSLUtil.isNullString(txn_id_resp))
             * responseMap.put("TXN_ID_RESP",txn_id_resp);
             */
        } catch (Exception e) {
            _log.error("parseVASCreditRequest", "Exception e::" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseVASCreditRequest", "Exited responseMap::" + responseMap);
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
    private HashMap parseVASValidateRequest(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseVASValidateRequest", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        int indexStart = 0;
        int indexEnd = 0;
        int tempIndex = 0;
        String responseCode = null;
        try {
            responseMap = new HashMap();

        } catch (Exception e) {
            _log.error("parseVASValidateRequest", "Exception e::" + e.getMessage());
            throw e;
        }// end catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseVASValidateRequest", "Exited responseMap::" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseGetAccountDetailsResponse

}
