package com.selftopup.pretups.inter.citipg;

import java.util.Date;
import java.util.HashMap;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.inter.citipg.CitiBankI;

public class CitiBankResponseFormatter {

    private static Log _log = LogFactory.getLog(CitiBankResponseFormatter.class.getName());

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
            case CitiBankI.ACTION_IMMEDIATE_DEBIT: {
                map = parseImmediateDebitResponse(p_responseStr);
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

}
