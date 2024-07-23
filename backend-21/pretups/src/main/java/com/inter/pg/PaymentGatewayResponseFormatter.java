package com.inter.pg;

import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class PaymentGatewayResponseFormatter {

    private static Log _log = LogFactory.getLog(PaymentGatewayResponseFormatter.class.getName());

   
    public HashMap parseResponse(int p_action, String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " p_responseStr:: " + p_responseStr);
        HashMap map = null;
        try {
            switch (p_action) {
            case PaymentGatewayI.ACTION_IMMEDIATE_DEBIT: {
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


    private HashMap parseImmediateDebitResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseImmediateDebitResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            // response need to added
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
