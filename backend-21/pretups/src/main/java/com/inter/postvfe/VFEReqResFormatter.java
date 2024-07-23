package com.inter.postvfe;

import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;
import com.inter.postvfe.postvfestub.CMSParamType;
import com.inter.postvfe.postvfestub.CMSRequest;
import com.inter.postvfe.postvfestub.CMSResponse;

/**
 * VFEReqResFormatter.java
 * Copyright(c) 2011, Comviva Technologies Pvt. Ltd.
 * All Rights Reserved
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Shashank Shukla November 10, 2011 Initial Creation
 * Rahul Dutt November 16, 2012 Modified(Online PPB and New Stub integration)
 * ----------------------------------------------------------------------------
 * --------------------
 * This class is responsible to generate the request and parse the response for
 * the VFEPostPaid interface.
 */

public class VFEReqResFormatter {
    public Log _log = LogFactory.getLog("VFEReqResFormatter".getClass().getName());
    private String _interfaceID = null;

    /**
     * Method parseResponseObject
     * 
     * @param p_stage
     * @param p_cmsResponse
     * @throws BTSLBaseException
     * @throws Exception
     */
    public HashMap<String, String> parseResponseObject(int p_stage, CMSResponse p_cmsResponse) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponseObject", "Entered p_action=" + p_stage);
        HashMap<String, String> responseMap = null;
        try {
            switch (p_stage) {
            case PostVfeI.ACTION_BILL_POST: {
                responseMap = parsePostPaidBillResponseObject(responseMap, p_cmsResponse);
                break;
            }
            case PostVfeI.ACTION_ACCOUNT_INFO: {
                responseMap = parseAccountInfoResponseObject(responseMap, p_cmsResponse);
                break;
            }
            }// End of switch

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("parseResponseObject", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponseObject", "Exiting with responseMap=" + responseMap);
        }// end of finally
        return responseMap;
    }

    public CMSRequest genReqObject(int p_stage, HashMap p_requestMap) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("genReqObject", "Entered p_action=" + p_stage);
        CMSRequest cmsRequest = null;
        try {
            switch (p_stage) {
            case PostVfeI.ACTION_BILL_POST: {
                cmsRequest = getPostPaidBillingObject(p_requestMap);
                break;
            }
            case PostVfeI.ACTION_ACCOUNT_INFO: {
                cmsRequest = getAccountInfoRequest(p_requestMap);
                break;
            }
            }// Switch end

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("parseResponseObject", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponseObject", "Exiting with cmsRequest=" + cmsRequest);
        }// end of finally
        return cmsRequest;
    }

    // end of parseResponse
    protected CMSRequest getAccountInfoRequest(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getPostPaidBillingObject", "Entered");

        CMSRequest cmsRequest = null;
        _interfaceID = (String) p_requestMap.get("INTERFACE_ID");
        String userName = FileCache.getValue(_interfaceID, "USER_NAME");
        String password = FileCache.getValue(_interfaceID, "PASSWORD");
        String wfName = FileCache.getValue(_interfaceID, "WF_NAME_VAL");
        try {
            cmsRequest = new CMSRequest();
            cmsRequest.setUsername(userName);
            cmsRequest.setPassword(password);
            cmsRequest.setWfname(wfName);
            com.inter.postvfe.postvfestub.CMSParamType[] cmsParam = new CMSParamType[2];// Note:
                                                                                        // This
                                                                                        // value
                                                                                        // can
                                                                                        // be
                                                                                        // increased
                                                                                        // based
                                                                                        // on
                                                                                        // requested
                                                                                        // params
                                                                                        // from
                                                                                        // IN.
            cmsParam[0] = new CMSParamType("CUSTOMER_ID", "", (String) p_requestMap.get("CUST_ID"), null);
            cmsParam[1] = new CMSParamType("MSISDN", "", (String) p_requestMap.get("CUST_ID"), null);// field
                                                                                                     // is
                                                                                                     // INT
                                                                                                     // as
                                                                                                     // per
                                                                                                     // interface
                                                                                                     // doc
                                                                                                     // check
            if (_log.isDebugEnabled()) {
                for (int i = 0; i < cmsParam.length; i++) {
                    _log.debug("getPostPaidBillingObject", "cmsParam[" + i + "]" + cmsParam[i].getSimpleValue());
                }
            }
            cmsRequest.setParams(cmsParam);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return cmsRequest;
    }

    /**
     * Method getPostPaidBillingObject
     * 
     * @param p_requestMap
     *            HashMap<String, String>
     * @return logonRequest LogonRequest
     *         throws Exception
     */
    protected CMSRequest getPostPaidBillingObject(HashMap p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getPostPaidBillingObject", "Entered");

        CMSRequest cmsRequest = null;
        CMSParamType cmsParamType = null;
        _interfaceID = (String) p_requestMap.get("INTERFACE_ID");
        String userName = FileCache.getValue(_interfaceID, "USER_NAME");
        String password = FileCache.getValue(_interfaceID, "PASSWORD");
        String wfName = FileCache.getValue(_interfaceID, "WF_NAME_CR");
        try {
            cmsRequest = new CMSRequest();
            cmsRequest.setUsername(userName);
            cmsRequest.setPassword(password);
            cmsRequest.setWfname(wfName);
            com.inter.postvfe.postvfestub.CMSParamType[] cmsParam = new CMSParamType[9];
            cmsParam[0] = new CMSParamType("SYNCHRONOUS_MODE", "", FileCache.getValue(_interfaceID, "SYNCHRONOUS_MODE"), null);
            cmsParam[1] = new CMSParamType("TRANSX_CODE", "", FileCache.getValue(_interfaceID, "TRANSX_CODE"), null);
            cmsParam[2] = new CMSParamType("RT_CACHKNUM", "", (String) p_requestMap.get("IN_TXN_ID"), null);
            cmsParam[3] = new CMSParamType("RT_CACHKAMT_PAY", "", (String) p_requestMap.get("transfer_amount"), null);
            cmsParam[4] = new CMSParamType("RT_CARECDATE", "", InterfaceUtil.getCurrentDateString("yyyy-MM-dd HH:mm"), null);
            cmsParam[5] = new CMSParamType("RT_CACHKDATE", "", InterfaceUtil.getCurrentDateString("yyyy-MM-dd HH:mm"), null);
            cmsParam[6] = new CMSParamType("CS_ID", "", (String) p_requestMap.get("ACCOUNT_ID"), null);
            cmsParam[7] = new CMSParamType("PAYMENT_MODE", "", FileCache.getValue(_interfaceID, "PAYMENT_MODE"), null);
            String payementCurrencyID = (String) FileCache.getValue(_interfaceID, "CURRENCY_ID");
            if (_log.isDebugEnabled())
                _log.debug("getPostPaidBillingObject", "PAYEMENT_CURRENCY_ID::=" + payementCurrencyID);
            cmsParam[8] = new CMSParamType("PAYMENT_CURRENCY_ID", "", payementCurrencyID, null);
            if (_log.isDebugEnabled()) {
                for (int i = 0; i < cmsParam.length; i++) {
                    _log.debug("getPostPaidBillingObject", "cmsParam[" + i + "]" + cmsParam[i].getSimpleValue());
                }
            }
            // cc[1]=new
            // CMSParamType("SYNCHRONOUS_MODE",Boolean.parseBoolean(_requestMap.get("SYNCHRONOUS_MODE").toString(),null);
            cmsRequest.setParams(cmsParam);
        }

        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return cmsRequest;
    }

    private HashMap<String, String> parsePostPaidBillResponseObject(HashMap<String, String> p_responseMap, CMSResponse p_cmsResponse) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parsePostPaidBillResponseObject", "Entered");

        try {
            p_responseMap = new HashMap<String, String>();
            String resultCode = p_cmsResponse.getStatusCode();
            String resultDesc = p_cmsResponse.getStatusDesc();

            if (_log.isDebugEnabled())
                _log.debug("p_cmsResponse", "resultCode=" + resultCode + ":" + resultDesc);

            if (PostVfeI.SUCCESS_CMS.contains(resultCode)) {
                p_responseMap.put("RESP_STATUS", PostVfeI.RESULT_OK);
                p_responseMap.put("RESP_STATUS_DESC", resultDesc);
                p_responseMap.put("IN_RESP_TRNX_ID", p_cmsResponse.getTransactionId());
            } else
                p_responseMap.put("RESP_STATUS", resultCode);

        }

        catch (Exception e) {
            _log.error("parsePostPaidBillResponseObject", "Exception e:" + e.getMessage());
            throw e;
        }

        finally {
            if (_log.isDebugEnabled())
                _log.debug("parsePostPaidBillResponseObject", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }

    /**
     * @param p_responseMap
     * @param p_cmsResponse
     * @return
     * @throws Exception
     * @author rahul.dutt
     */
    private HashMap<String, String> parseAccountInfoResponseObject(HashMap<String, String> p_responseMap, CMSResponse p_cmsResponse) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parsePostPaidBillResponseObject", "Entered" + p_cmsResponse);

        try {
            p_responseMap = new HashMap<String, String>();
            String resultCode = p_cmsResponse.getStatusCode();
            String resultDesc = p_cmsResponse.getStatusDesc();
            double skipAmt = 0.0;
            double opnAmt = 0.0;
            // - rahul.d to check if paramtype used in case of succ only

            if (_log.isDebugEnabled())
                _log.debug("parseAccountInfoResponseObject", "resultCode=" + resultCode);

            if (PostVfeI.SUCCESS_CMS.contains(resultCode)) {
                p_responseMap.put("RESP_STATUS", PostVfeI.RESULT_OK);
                p_responseMap.put("RESP_STATUS_DESC", resultDesc);
                p_responseMap.put("IN_RESP_TRNX_ID", p_cmsResponse.getTransactionId());
                Date next = null, latest = null;
                int invIndex = 0;
                CMSParamType[] paramList = null;
                CMSParamType[] outparams = p_cmsResponse.getOutparams();
                if (_log.isDebugEnabled())
                    _log.debug("p_cmsResponse", "resultCode=" + resultCode + ":" + resultDesc);
                if (outparams != null)
                    for (int i = 0; i < outparams.length; i++) {
                        if (_log.isDebugEnabled())
                            _log.debug("p_cmsResponse", "INVOICES::[" + i + "]RESP:" + "O/P name=" + outparams[i].getName() + ":" + "value=" + outparams[i].getSimpleValue());
                        if (PostVfeI.CMS_INVOICE.equals(outparams[i].getName())) {
                            com.inter.postvfe.postvfestub.CMSListType[] listValues = outparams[i].getListValues();
                            System.out.println("LISTVALUES::::" + listValues + "==" + listValues.length);
                            if (listValues != null)
                                for (int j = 0; j < listValues.length; j++) {
                                    paramList = listValues[j].getParamList();
                                    if (paramList == null)
                                        continue;

                                    try { // added by pradyumn
                                        for (int k = 0; k < paramList.length; k++) {
                                            System.out.println("!!QQQ" + paramList[k].getSimpleValue() + "!!" + paramList[k].getName() + "red" + paramList[k]);
                                            if (PostVfeI.OPEN_AMOUNT.equalsIgnoreCase(paramList[k].getName()))
                                                opnAmt = Double.parseDouble(paramList[k].getSimpleValue().trim());
                                            if (PostVfeI.DUE_DATE.equalsIgnoreCase(paramList[k].getName()))
                                                next = BTSLUtil.getDateFromDateString(paramList[k].getSimpleValue(), "yyyy-MM-dd");
                                        }

                                        // Double.parseDouble(paramList[1].getSimpleValue().trim());
                                        // System.out.println("=================================="+paramList[1].getSimpleValue()+"==");

                                        // if("0.0".equals((String)paramList[1].getSimpleValue().trim()))
                                        // continue;

                                        if (0.0 == opnAmt)
                                            continue;
                                        // next=BTSLUtil.getDateFromDateString(paramList[0].getSimpleValue(),
                                        // "yyyy-MM-dd");
                                        System.out.println("=============================" + next + ":" + latest + "@@:" + invIndex);
                                        if (latest != null) {
                                            if (!latest.before(next)) {
                                                latest = next;
                                                invIndex = j;
                                            }
                                        } else {
                                            latest = next;
                                            invIndex = j;
                                        }
                                    } catch (Exception e) {
                                        _log.error("parseAccountInfoResponseObject", "Exception while parsing date in resp" + e);
                                    }
                                }
                            paramList = listValues[invIndex].getParamList();
                            // //// change for parsing
                            for (int k = 0; k < paramList.length; k++) {
                                System.out.println("!!TTT" + paramList[k].getSimpleValue() + "!!" + paramList[k].getName());
                                if (PostVfeI.OPEN_AMOUNT.equalsIgnoreCase(paramList[k].getName()))
                                    p_responseMap.put("MIN_AMT_DUE", paramList[k].getSimpleValue());
                                if (PostVfeI.DUE_DATE.equalsIgnoreCase(paramList[k].getName()))
                                    p_responseMap.put("DUE_DATE", BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(paramList[k].getSimpleValue(), "yyyy-MM-dd"), "dd-MM-yyyy"));
                            }
                            // p_responseMap.put("MIN_AMT_DUE",paramList[1].getSimpleValue());
                            // p_responseMap.put("DUE_DATE",BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(paramList[0].getSimpleValue(),
                            // "yyyy-MM-dd"),"dd-MM-yyyy"));

                            paramList = null;
                        } else if (PostVfeI.PREVIOUS_BALANCE.equals(outparams[i].getName())) {
                            com.inter.postvfe.postvfestub.CMSListType[] listValues = outparams[i].getListValues();
                            if (listValues != null) {
                                paramList = listValues[0].getParamList();
                                if (paramList != null)
                                    p_responseMap.put("PREV_BALANCE", paramList[0].getSimpleValue());
                                else
                                    p_responseMap.put("PREV_BALANCE", "0000");
                            }
                            paramList = null;
                        } else if (PostVfeI.CS_ID.equals(outparams[i].getName())) {
                            p_responseMap.put("CS_ID", outparams[i].getSimpleValue());
                        } else if (PostVfeI.CUSTOMER_STATUS.equals(outparams[i].getName())) {
                            p_responseMap.put("CUSTOMER_STATUS", outparams[i].getSimpleValue());
                        } else if (PostVfeI.CURRENT_BALANCE.equals(outparams[i].getName())) {
                            com.inter.postvfe.postvfestub.CMSListType[] listValues = outparams[i].getListValues();
                            if (listValues != null) {
                                paramList = listValues[0].getParamList();
                                if (paramList != null)
                                    p_responseMap.put("CURRENT_BALANCE", paramList[0].getSimpleValue());
                                else
                                    p_responseMap.put("CURRENT_BALANCE", "0000");
                            }
                            paramList = null;
                        } else if (PostVfeI.BILL_CYCLE.equals(outparams[i].getName())) {
                        } else if (PostVfeI.CO_ID.equals(outparams[i].getName())) {
                            p_responseMap.put("CO_ID", outparams[i].getSimpleValue());
                        } else {
                            com.inter.postvfe.postvfestub.CMSListType[] listValues = outparams[i].getListValues();
                            if (listValues != null)
                                for (int j = 0; j < listValues.length; j++) {
                                    paramList = listValues[j].getParamList();
                                    for (int k = 0; k < paramList.length; k++) {
                                        if (_log.isDebugEnabled())
                                            _log.debug("p_cmsResponse", "#####[" + k + "]RESP:" + "O/P name=" + paramList[k].getName() + ":" + "value=" + paramList[k].getSimpleValue());
                                    }
                                }
                        }
                    }// end of for
            } else {
                p_responseMap.put("RESP_STATUS", resultCode);
                p_responseMap.put("RESP_STATUS_DESC", resultDesc);
            }
        }

        catch (Exception e) {
            _log.error("parseAccountInfoResponseObject", "Exception e:" + e.getMessage());
            throw e;
        }

        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseAccountInfoResponseObject", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }
}
