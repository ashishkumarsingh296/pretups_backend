package com.inter.simactivationth;

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class SIMACTResponseParser {

    private static Log _log = LogFactory.getLog(SIMACTResponseParser.class.getName());

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
            case SIMActivateI.ACTION_VALIDATE: {
                map = parseValidateResponse(p_responseStr);
                break;
            }
            case SIMActivateI.ACTION_SIM_ACTIVATE: {
                map = parseSIMActivateResponse(p_responseStr);
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
    private HashMap parseValidateResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseValidateResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            int indexStart = 0;
            int indexEnd = 0;
            int tempIndex = 0;
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
            // responseMap = parsevalidatexml(new BufferedReader(new
            // StringReader(p_responseStr)));
            int index = p_responseStr.indexOf("<TXNSTATUS>");
            if (index > 0) {
                String txnStatus = p_responseStr.substring(index + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", index));
                responseMap.put("TXNSTATUS", txnStatus);
            }
            index = p_responseStr.indexOf("<MESSAGE>");
            if (index > 0) {
                String message = p_responseStr.substring(index + "<MESSAGE>".length(), p_responseStr.indexOf("</MESSAGE>", index));
                responseMap.put("MESSAGE", message);
            }

        } catch (Exception e) {
            _log.error("parseValidateResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseValidateResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

    private HashMap parseSIMActivateResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseSIMActivateResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();

            if (p_responseStr != null && p_responseStr.equals(SIMActivateI.SUCCESS_MSG)) {
                responseMap.put("TXNSTATUS", SIMActivateI.HTTP_STATUS_SUCCESS);
                responseMap.put("MESSAGE", p_responseStr);
            } else if (p_responseStr != null && p_responseStr.equals(SIMActivateI.FAILURE_MSG)) {
                responseMap.put("TXNSTATUS", SIMActivateI.HTTP_STATUS_FAIL);
                responseMap.put("MESSAGE", p_responseStr);
            } else
                responseMap.put("TXNSTATUS", SIMActivateI.HTTP_STATUS_FAIL);

        } catch (Exception e) {
            _log.error("parseSIMActivateResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseSIMActivateResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

}
