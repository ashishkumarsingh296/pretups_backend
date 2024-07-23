package com.inter.alcatel;

/**
 * @(#)AlcatelTelRequestResponse.java
 *                                    Copyright(c) 2005, Bharti Telesoft Int.
 *                                    Public Ltd.
 *                                    All Rights Reserved
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Gurjeet Singh Bedi Oct 19,2005 Initial
 *                                    Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    ------------
 *                                    Request Response Class for the interface
 */

import java.util.HashMap;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;

public class AlcatelTelRequestResponse {

    public String getValidateRequest(HashMap p_map) {
        String requestString = null;
        requestString = "DISACNT,MSISDN=" + (String) p_map.get("MSISDN") + ";";
        p_map.put("IN_REQUEST_STR", requestString);
        return requestString;
    }

    public String getCreditRequest(HashMap p_map) {
        String requestString = null;
        requestString = "MODACNT,MSISDN=" + (String) p_map.get("MSISDN") + ",BLOCK=" + (String) p_map.get("BLOCK") + ",OP_ACDUR=" + (String) p_map.get("VALIDITY_DAYS") + ",OP_CRED=" + (String) p_map.get("INTERFACE_AMOUNT") + ",OP_DDATE=" + InterfaceUtil.getCurrentDateString(PretupsI.DATE_FORMAT_DDMMYYYY) + ",OP_UNDUR=" + (String) p_map.get("GRACE_DAYS") + ",SIGN_F=0;";
        p_map.put("IN_REQUEST_STR", requestString);
        return requestString;
    }

    public String getDebitRequest(HashMap p_map) {
        String requestString = null;
        String interfaceID = (String) p_map.get("INTERFACE_ID");
        String graceValReqd = FileCache.getValue(interfaceID, "GRACE_VAL_REQD_DEBIT_REQ");
        if (!InterfaceUtil.isNullString(graceValReqd) && "Y".equalsIgnoreCase(graceValReqd))
            requestString = "MODACNT,MSISDN=" + (String) p_map.get("MSISDN") + ",BLOCK=" + (String) p_map.get("BLOCK") + ",OP_ACDUR=" + (String) p_map.get("VALIDITY_DAYS") + ",OP_CRED=" + (String) p_map.get("INTERFACE_AMOUNT") + ",OP_DDATE=" + InterfaceUtil.getCurrentDateString(PretupsI.DATE_FORMAT_DDMMYYYY) + ",OP_UNDUR=" + (String) p_map.get("GRACE_DAYS") + ",SIGN_F=1;";
        else
            requestString = "MODACNT,MSISDN=" + (String) p_map.get("MSISDN") + ",BLOCK=" + (String) p_map.get("BLOCK") + ",OP_CRED=" + (String) p_map.get("INTERFACE_AMOUNT") + ",OP_DDATE=" + InterfaceUtil.getCurrentDateString(PretupsI.DATE_FORMAT_DDMMYYYY) + ",SIGN_F=1;";
        p_map.put("IN_REQUEST_STR", requestString);
        return requestString;
    }

    // for testing
    public String getResponse() {
        String requestString = null;
        requestString = "PPSSES10P,>MODACNT,MSISDN=9818693279,BLOCK=0,OP_ACDUR=62,OP_CRED=100,OP_DDATE=17/01/2005,OP_UNDUR=93,SIGN_F=0; PPSSES10P,ENDCMD,ACCOUNT NUMBER UPDATED;";
        return requestString;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
