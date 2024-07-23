/*
 * Created on June 18, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodafoneghana.voucherconsumptiongh;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

/**
 * @author vipan.kumar
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class VoucherConsumptionGHRequestFormatter {
    public static Log _log = LogFactory.getLog(VoucherConsumptionGHRequestFormatter.class);
    String lineSep = null;
    String _soapAction = "";

    public VoucherConsumptionGHRequestFormatter() {
        // lineSep = System.getProperty("line.separator")+"\r";
        lineSep = System.getProperty("line.separator") + "";
    }

    /**
     * This method is used to parse the response string based on the type of
     * Action.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return String.
     * @throws Exception
     */
    protected Object generateRequest(int p_action, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("generateRequest", "Entered p_action::" + p_action + " map::" + p_map);
        Object object = null;
        p_map.put("action", String.valueOf(p_action));
        try {
            switch (p_action) {
            case VoucherConsumptionGHI.ACTION_ACCOUNT_DETAILS: {

                String rechargeRequest = generateRechargeValidationRequest(p_map);
                object = rechargeRequest;
                break;
            }
            case VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT: {

                String rechargeRequest = generateRechargeCreditRequest(p_map);
                object = rechargeRequest;
                break;
            }
            case VoucherConsumptionGHI.ACTION_IMMEDIATE_DEBIT: {
                String debitRequest = generateRechargeDebitRequest(p_map);
                object = debitRequest;
                break;
            }
            case VoucherConsumptionGHI.ACTION_RECHARGE_CREDIT_BUNDLE: {
                String debitRequest = generateBundleRechargeRequest(p_map);
                object = debitRequest;
                break;
            }
            case VoucherConsumptionGHI.ACTION_BLOCK_SUBSCRIBER: {
                String debitRequest = generateSubscriberBlockRequest(p_map);
                object = debitRequest;
                break;
            }
            }
        } catch (Exception e) {
            _log.error("generateRequest", "Exception e ::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("generateRequest", "Exited Request String: object::" + object);
        }
        return object;
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeValidationRequest(HashMap p_requestMap) throws Exception {
        final String methodName = "VoucherConsumptionGHRequestFormatter[generateRechargeValidationRequest]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap::" + p_requestMap);
        StringBuffer requestString = new StringBuffer();
        try {
            requestString.append("(sid=");
            requestString.append(p_requestMap.get("valSid").toString());
            requestString.append(";Tbl_name=");
            requestString.append(p_requestMap.get("valTbl_name").toString());
            requestString.append(";Tbl_key=");
            requestString.append(p_requestMap.get("MSISDN").toString());
            requestString.append(";Field_Name=");
            requestString.append(p_requestMap.get("valField_Name").toString() + ")");

        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Request validation request::" + requestString.toString());
        }
        return requestString.toString();
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateBundleRechargeRequest(HashMap p_requestMap) throws Exception {
        final String methodName = "VoucherConsumptionGHRequestFormatter[generateBundleRechargeRequest]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap::" + p_requestMap);
        StringBuffer requestString = new StringBuffer();
        try {
            requestString.append("(sid=");
            requestString.append(p_requestMap.get("bundleSid").toString());
            requestString.append(";ACTION=");
            requestString.append(p_requestMap.get("bundleACTION").toString());
            requestString.append(",MSISDN=");
            requestString.append(p_requestMap.get("MSISDN").toString());
            requestString.append(",budlePTP_ID=");
            requestString.append(p_requestMap.get("bundlePTP_ID").toString() + ")");

        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Request validation request::" + requestString.toString());
        }
        return requestString.toString();
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateSubscriberBlockRequest(HashMap p_requestMap) throws Exception {
        final String methodName = "VoucherConsumptionGHRequestFormatter[generateSubscriberBlockRequest]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap::" + p_requestMap);
        StringBuffer requestString = new StringBuffer();
        try {
            requestString.append("(sid=");
            requestString.append(p_requestMap.get("blockerSid").toString());
            requestString.append(";Tbl_name=");
            requestString.append(p_requestMap.get("blockerTbl_name").toString());
            requestString.append(";Tbl_key=");
            requestString.append(p_requestMap.get("MSISDN").toString());
            requestString.append("#SIM_RTDB_Record1.Error_Indicator=");
            requestString.append(p_requestMap.get("blockerError_Indicator").toString() + ")");

        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Request validation request::" + requestString.toString());
        }
        return requestString.toString();
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeCreditRequest(HashMap p_requestMap) throws Exception {
        final String methodName = "VoucherConsumptionGHRequestFormatter[generateRechargeCreditRequest]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap::" + p_requestMap);
        StringBuffer requestString = new StringBuffer();
        try {
            requestString.append("(sid=");
            requestString.append(p_requestMap.get("creditSid").toString());
            requestString.append(";ACTION=");
            requestString.append(p_requestMap.get("creditACTION").toString());
            requestString.append(",MSISDN=");
            requestString.append(p_requestMap.get("MSISDN").toString());
            requestString.append(",Neg_Credit=");
            requestString.append(p_requestMap.get("creditNeg_Credit").toString());
            requestString.append(",UCL=");
            requestString.append(p_requestMap.get("creditUCL").toString());
            requestString.append(",Recharge=");
            requestString.append(p_requestMap.get("creditRecharge").toString());
            requestString.append(",No_LC=");
            requestString.append(p_requestMap.get("creditNo_LC").toString());
            requestString.append(",USERID=");
            requestString.append(p_requestMap.get("ADJUSERID").toString());
            requestString.append(",Bal=");
            requestString.append(p_requestMap.get("creditBal").toString());
            requestString.append(",Adj=");
            requestString.append(p_requestMap.get("creditAdj").toString());
            requestString.append(",Amount=");
            requestString.append(p_requestMap.get("transfer_amount").toString() + ")");

        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Request credit request::" + requestString.toString());
        }
        return requestString.toString();
    }

    /**
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    private String generateRechargeDebitRequest(HashMap p_requestMap) throws Exception {
        final String methodName = "VoucherConsumptionGHRequestFormatter[generateRechargeDebitRequest]";

        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_requestMap::" + p_requestMap);
        StringBuffer requestString = new StringBuffer();
        try {
            requestString.append("(sid=");
            requestString.append(p_requestMap.get("creditSid").toString());
            requestString.append(";ACTION=");
            requestString.append(p_requestMap.get("creditACTION").toString());
            requestString.append(",MSISDN=");
            requestString.append(p_requestMap.get("MSISDN").toString());
            requestString.append(",Neg_Credit=");
            requestString.append(p_requestMap.get("creditNeg_Credit").toString());
            requestString.append(",UCL=");
            requestString.append(p_requestMap.get("creditUCL").toString());
            requestString.append(",Recharge=");
            requestString.append(p_requestMap.get("creditRecharge").toString());
            requestString.append(",No_LC=");
            requestString.append(p_requestMap.get("creditNo_LC").toString());
            requestString.append(",USERID=");
            requestString.append(p_requestMap.get("ADJUSERID").toString());
            requestString.append(",Bal=");
            requestString.append(p_requestMap.get("creditBal").toString());
            requestString.append(",Adj=");
            requestString.append(p_requestMap.get("creditAdj").toString());
            requestString.append(",Amount=");
            requestString.append(p_requestMap.get("transfer_amount").toString() + ")");

        } catch (Exception e) {
            _log.error(methodName, "Exception e: " + e);
            throw new BTSLBaseException(this, methodName, InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting Request debitRequest::" + requestString.toString());
        }
        return requestString.toString();
    }

}
