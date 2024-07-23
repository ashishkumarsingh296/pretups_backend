package com.inter.tibcovm;

import java.util.Date;
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class TibcoVMResponseParser {

    private static Log _log = LogFactory.getLog(TibcoVMResponseParser.class.getName());

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
                case TibcoVMI.ACTION_RECHARGE_CREDIT: {
                map = parseRechargeCreditResponse(p_responseStr);
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
    public HashMap parseRechargeCreditResponse(String p_responseStr) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponse", "Entered p_responseStr::" + p_responseStr);
        HashMap responseMap = null;
        try {
            responseMap = new HashMap();
            int index=p_responseStr.indexOf("<ns0:ActCode>");
            String responseCode =p_responseStr.substring(index + "<ns0:ActCode>".length(), p_responseStr.indexOf("</ns0:ActCode>", index));
            if (responseCode.equalsIgnoreCase("00000")) {
            	index=p_responseStr.indexOf("<ns0:Customer_Type>");
                String custType =p_responseStr.substring(index + "<ns0:Customer_Type>".length(), p_responseStr.indexOf("</ns0:Customer_Type>", index));
				
				index=p_responseStr.indexOf("<ns0:CurrentBal>");
                String interfacePostBalance =p_responseStr.substring(index + "<ns0:CurrentBal>".length(), p_responseStr.indexOf("</ns0:CurrentBal>", index));
                responseMap.put("INTERFACE_POST_BALANCE",interfacePostBalance);
				
                responseMap.put("CUSTOMERTYPE", custType);
                responseMap.put("ETOPUPSTATUS",InterfaceErrorCodesI.SUCCESS);
                responseMap.put("ErrorCode", "0");
            }
            else
            {
                responseMap.put("ETOPUPSTATUS",InterfaceErrorCodesI.FAIL );
                if(responseCode!=null && responseCode.length()>9) 
                {
                	responseMap.put("ErrorCode", responseCode.substring(0, 8));
                }
                else
                responseMap.put("ErrorCode", responseCode);
            }

        } catch (Exception e) {
            _log.error("parseRechargeCreditResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponse", "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }
}
