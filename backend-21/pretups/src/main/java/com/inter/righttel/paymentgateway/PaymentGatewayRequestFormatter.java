package com.inter.righttel.paymentgateway;
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;

public class PaymentGatewayRequestFormatter {
    public static Log _log = LogFactory.getLog(PaymentGatewayRequestFormatter.class);
    String lineSep = null;

    public PaymentGatewayRequestFormatter() {
        lineSep = System.getProperty("line.separator") + "";
    }
    protected String generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        String str = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {

            case PaymentGatewayI.ACTION_IMMEDIATE_DEBIT: {
                str = generateLightBoxPGRequest(p_map);
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: str::" + str);
        }
        return str;
    }

    private String generateLightBoxPGRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateLightBoxPGRequest", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        try {
            StringBuffer body = new StringBuffer();
            body.append("?transactionAmount=" + p_requestMap.get("INTERFACE_AMOUNT"));
            body.append("&currency=" + p_requestMap.get("CURRENCY"));
            body.append("&merchantName=" + p_requestMap.get("MERCHANT_NAME"));
            body.append("&productName=" + p_requestMap.get("PRODUCT_NAME"));
            body.append("&orderId=" + p_requestMap.get("TRANSACTION_ID"));
            body.append("&userid=" + p_requestMap.get("USER_ID"));
            body.append("&billingAddressLine1=" + p_requestMap.get("BILLING_ADDRESS_LINE1"));
            body.append("&billingAddressLine2=" + p_requestMap.get("BILLING_ADDRESS_LINE2"));
            body.append("&billingCountry=" + p_requestMap.get("BILLING_COUNTRY"));
            body.append("&billingState=" + p_requestMap.get("BILLING_STATE"));
            body.append("&billingCity=" + p_requestMap.get("BILLING_CITY"));
            body.append("&mobile=" + p_requestMap.get("MSISDN"));
            body.append("&email=" + "abc@mahindracomviva.com");
            body.append("&targetUrl=" + p_requestMap.get("TARGET_URL"));
            body.append("&mid=" + p_requestMap.get("MERCHANT_ID"));
            body.append("&tid=" + p_requestMap.get("TERMINAL_ID"));
            body.append("&checkSum=" + p_requestMap.get("CHECKSUM"));
            body.append("&callbackurl=" + p_requestMap.get("CALLBACK_URL"));  //+this.generateCallBackGetURLString(p_requestMap)
            body.append("&callbackdata=" + this.generateCallBackPostURLString(p_requestMap));
            requestStr = body.toString().trim();
        } catch (Exception e) {
            _log.error("generateLightBoxPGRequest", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateLightBoxPGRequest", "Exiting  requestStr::" + requestStr);
        }
        return requestStr;
    }
    
    private String generateCallBackGetURLString(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateCallBackGetURLString", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        try {
            StringBuffer body = new StringBuffer();
            body.append("&MSISDN="+p_requestMap.get("MSISDN"));
            body.append("&MESSAGE=O2CAPRL"+" "+p_requestMap.get("NETWORK_CODE")+" "+p_requestMap.get("TRANSACTION_ID")+" "+p_requestMap.get("TRANSACTION_STATUS")+" "+p_requestMap.get("IN_TXN_ID"));
            requestStr = body.toString().trim();
            p_requestMap.put("CALLBACK_STR",requestStr);
        } catch (Exception e) {
            _log.error("generateCallBackGetURLString", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateCallBackGetURLString", "Exiting  requestStr::" + requestStr);
        }
        return requestStr;
    }
    
    private String generateCallBackPostURLString(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateCallBackPostURLString", "Entered p_requestMap::" + p_requestMap);
        String requestStr = null;
        try {
            StringBuffer body = new StringBuffer();
            body.append("<?xml version=\"1.0\"?><COMMAND>");
            body.append("<TYPE>O2CAPRL</TYPE>");
            body.append("<EXTNWCODE>"+p_requestMap.get("NETWORK_CODE")+"</EXTNWCODE>");
            body.append("<STATUS>"+Constants.getProperty("transaction.status")+"</STATUS>");
            body.append("<TXNID>"+p_requestMap.get("TRANSACTION_ID")+"</TXNID>");
            body.append("<REFNO>"+p_requestMap.get("IN_TXN_ID")+"</REFNO>");
            body.append("<REMARKS>"+"O2CApproval"+"</REMARKS>");
            body.append("</COMMAND>");
            requestStr = body.toString().trim();
            p_requestMap.put("CALLBACK_DATA",requestStr);
        } catch (Exception e) {
            _log.error("generateCallBackPostURLString", "Exception e: " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateCallBackPostURLString", "Exiting  requestStr::" + requestStr);
        }
        return requestStr;
    }
}
