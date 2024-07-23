package com.inter.huawei_webservices;

/**
 * @HuaweiOMTRequestFormatter.java
 *                                 Copyright(c) 2011, Comviva Technologies Pvt.
 *                                 Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Shashank Shukla March 11, 2011 Initial
 *                                 Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 *                                 This class is responsible to generate the
 *                                 request and parse the response for the HUAWEI
 *                                 OMT interface.
 */

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.ModifyAcctFeeType;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.PaymentRequestMsg;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.TransferAccountRequest;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.PaymentRequest;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.RequestHeader;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.RequestHeaderRequestType;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.PaymentResultMsg;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.TransferAccountResultMsg;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.TransferAccountRequestMsg;
import com.btsl.pretups.inter.huawei_webservices.omt_huawei.AcctChgRecType;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.inter.huawei_webservices.HuaweiOMTI;

public class HuaweiOMTRequestFormatter {
    public Log _log = LogFactory.getLog("HuaweiOMTRequestFormatter".getClass().getName());
    private String _interfaceID = null;

    /**
     * This method internally calls private method to get request object.
     * 
     * @param int p_action
     * @param HashMap
     *            p_map
     * @return Request
     * @throws Exception
     */
    /*
     * public void generateRequestObject(int p_action, HashMap<String, String>
     * p_requestMap,PaymentRequestMsg
     * p_paymentRequestObject,TransferAccountRequestMsg p_creditTransferObject)
     * throws BTSLBaseException,Exception
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("generateRequestObject","Entered p_action="+p_action+
     * " p_requestMap="+p_requestMap);
     * try
     * {
     * switch(p_action)
     * {
     * case HuaweiOMTI.ACTION_RECHARGE_CREDIT:
     * {
     * getRechargeReqestObject(p_requestMap,p_paymentRequestObject);
     * break;
     * }
     * case HuaweiOMTI.ACTION_CREDIT_TRANSFER:
     * {
     * getCreditTransferReqestObject(p_requestMap,p_creditTransferObject);
     * break;
     * }
     * }//end of switch block
     * }//end of try block
     * catch(BTSLBaseException be)
     * {
     * throw be;
     * }
     * catch(Exception e)
     * {
     * _log.error("generateRequestObject", "Exception e:" + e.getMessage());
     * throw e;
     * }//end of catch-Exception
     * finally
     * {
     * if (_log.isDebugEnabled())
     * _log.debug("generateRequestObject", "Exited");
     * }//end of finally
     * }//end of generateRequestObject
     *//**
     * This method internally calls methods (according to p_action parameter)
     * to get response HashMap and returns it.
     * 
     * @param int action
     * @param HashMap
     *            p_map
     * @return HashMap map
     * @throws BTSLBaseException
     *             ,Exception
     */
    public HashMap<String, String> parseResponseObject(int p_action, PaymentResultMsg p_paymentResult, TransferAccountResultMsg p_creditTransferResult) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponseObject", "Entered p_action=" + p_action);
        HashMap<String, String> responseMap = null;
        try {
            switch (p_action) {
            case HuaweiOMTI.ACTION_RECHARGE_CREDIT: {
                responseMap = parseRechargeCreditResponseObject(responseMap, p_paymentResult);
                break;
            }
            case HuaweiOMTI.ACTION_CREDIT_TRANSFER: {
                responseMap = parseCreditTransferResponseObject(responseMap, p_creditTransferResult);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("parseResponseObject", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponseObject", "Exiting responseMap=" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseResponseObject

    /**
     * This method parse the response for Credit from Response Object and puts
     * into HashMap and returns it
     * 
     * @param Response
     *            p_respObj
     * @return HashMap map
     * @throws BTSLBaseException
     */
    private HashMap<String, String> parseRechargeCreditResponseObject(HashMap<String, String> p_responseMap, PaymentResultMsg p_paymentResult) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("parseRechargeCreditResponseObject", "Entered ");
        try {
            p_responseMap = new HashMap<String, String>();
            String result = p_paymentResult.getResultHeader().getResultCode();
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponseObject", "result=" + result);

            if (HuaweiOMTI.OPERATION_SUCCESSFULLY.equals(result)) {
                p_responseMap.put("RESP_STATUS", HuaweiOMTI.RESULT_OK);
                String newBal = String.valueOf(p_paymentResult.getPaymentResult().getNewBalance());
                p_responseMap.put("NEW_BALANCE", newBal);
                p_responseMap.put("NEW_ACTIVE_STOP", p_paymentResult.getPaymentResult().getNewActiveStop());
            } else
                p_responseMap.put("RESP_STATUS", result);
        } catch (Exception e) {
            _log.error("parseRechargeCreditResponseObject", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseRechargeCreditResponseObject", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }

    /**
     * This method parse the response for Credit Adjust from Response received
     * from IN and will return the response map.
     * 
     * @param TransferAccountResultMsg
     *            p_transferAccountResult
     * @return HashMap map
     * @throws BTSLBaseException
     */
    private HashMap<String, String> parseCreditTransferResponseObject(HashMap<String, String> p_responseMap, TransferAccountResultMsg p_creditTransferResult) throws BTSLBaseException {
        String senderNewBalance = null;
        String receiverNewBalance = null;
        if (_log.isDebugEnabled())
            _log.debug("parseCreditTransferResponseObject", "Entered ");
        try {
            p_responseMap = new HashMap<String, String>();

            String result = p_creditTransferResult.getResultHeader().getResultCode();
            if (_log.isDebugEnabled())
                _log.debug("parseCreditTransferResponseObject", "result=" + result);

            if (HuaweiOMTI.OPERATION_SUCCESSFULLY.equals(result)) {
                p_responseMap.put("RESP_STATUS", HuaweiOMTI.RESULT_OK);

                AcctChgRecType senderAccChgList[] = new AcctChgRecType[1];
                AcctChgRecType receiverAccChgList[] = new AcctChgRecType[1];

                senderAccChgList = p_creditTransferResult.getTransferAccountResult().getTransferorAcctChgList();
                receiverAccChgList = p_creditTransferResult.getTransferAccountResult().getTransfereeAcctChgList();
                if (senderAccChgList.length > 0) {
                    senderNewBalance = String.valueOf(senderAccChgList[0].getCurrAcctBal());
                    p_responseMap.put("SENDER_LAST_BALANCE", senderNewBalance);
                }
                if (receiverAccChgList.length > 0) {
                    receiverNewBalance = String.valueOf(receiverAccChgList[0].getCurrAcctBal());
                    p_responseMap.put("LAST_BALANCE", receiverNewBalance);
                }
            } else
                p_responseMap.put("RESP_STATUS", result);

        } catch (Exception e) {
            _log.error("parseCreditTransferResponseObject", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseCreditTransferResponseObject", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }

    /**
     * Method getRechargeReqestObject
     * 
     * @param p_map
     * @throws BTSLBaseException
     */
    protected PaymentRequestMsg getRechargeReqestObject(HashMap<String, String> p_requestMap, PaymentRequestMsg p_paymentRequestObject) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getRechargeReqestObject", "Entered MSISDN=" + p_requestMap.get("MSISDN") + ", TRANSFER_AMOUNT=" + p_requestMap.get("TRANSFER_AMOUNT"));
        _interfaceID = p_requestMap.get("INTERFACE_ID");
        String paymentMode = FileCache.getValue(_interfaceID, "PAYMENT_MODE");
        try {
            RequestHeader commonRequestHeader = this.setRequestHeaderObject(p_requestMap);
            // Construct the request header object
            RequestHeader header = new RequestHeader();
            header.setCommandId(commonRequestHeader.getCommandId());
            header.setVersion(commonRequestHeader.getVersion());
            header.setTransactionId(commonRequestHeader.getTransactionId());
            header.setSequenceId(commonRequestHeader.getSequenceId());
            header.setRequestType(commonRequestHeader.getRequestType());
            header.setSerialNo(commonRequestHeader.getSerialNo());
            // Construct the payment request object
            PaymentRequest request = new PaymentRequest();
            request.setSubscriberNo(p_requestMap.get("MSISDN"));
            request.setPaymentAmt(Long.parseLong((p_requestMap.get("TRANSFER_AMOUNT"))));
            request.setPaymentMode(paymentMode);
            p_paymentRequestObject = new PaymentRequestMsg(header, request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getRechargeReqestObject", "Exited p_paymentRequestObject=" + p_paymentRequestObject);
        }
        return p_paymentRequestObject;
    }

    /**
     * Method getCreditTransferReqestObject
     * 
     * @param p_requestMap
     * @throws BTSLBaseException
     */
    protected TransferAccountRequestMsg getCreditTransferReqestObject(HashMap<String, String> p_requestMap, TransferAccountRequestMsg p_creditTransferObject) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getCreditTransferReqestObject", "TRANSFEREE_NO=" + p_requestMap.get("MSISDN") + ", TRANSFEROR_NO=" + p_requestMap.get("SENDER_MSISDN"));
        try {
            RequestHeader commonRequestHeader = this.setRequestHeaderObject(p_requestMap);
            RequestHeader header = new RequestHeader();
            header.setCommandId(commonRequestHeader.getCommandId());
            header.setVersion(commonRequestHeader.getVersion());
            header.setTransactionId(commonRequestHeader.getTransactionId());
            header.setSequenceId(commonRequestHeader.getSequenceId());
            header.setRequestType(commonRequestHeader.getRequestType());
            header.setSerialNo(commonRequestHeader.getSerialNo());
            TransferAccountRequest request = new TransferAccountRequest();
            request.setTransfereeNo(p_requestMap.get("MSISDN"));
            request.setTransferorNo(p_requestMap.get("SENDER_MSISDN"));
            if (_log.isDebugEnabled())
                _log.debug("getCreditTransferReqestObject", "ACCOUNT_TYPE=" + p_requestMap.get("ACCOUNT_TYPE") + ", TRANSFER_AMOUNT=" + p_requestMap.get("TRANSFER_AMOUNT"));
            ModifyAcctFeeType[] modifyAcctFeeList = new ModifyAcctFeeType[1];
            ModifyAcctFeeType account = new ModifyAcctFeeType(p_requestMap.get("ACCOUNT_TYPE"), Long.parseLong(p_requestMap.get("TRANSFER_AMOUNT")));
            modifyAcctFeeList[0] = account;
            request.setModifyAcctFeeList(modifyAcctFeeList);
            p_creditTransferObject = new TransferAccountRequestMsg(header, request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getCreditTransferReqestObject", "Exited p_creditTransferObject=" + p_creditTransferObject);
        }
        return p_creditTransferObject;
    }

    /**
     * Method setRequestHeaderObject
     * 
     * @param p_map
     * @return
     * @throws Exception
     */
    public RequestHeader setRequestHeaderObject(HashMap<String, String> p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("setRequestHeaderObject", "Entered COMMAND_ID=" + p_requestMap.get("COMMAND_ID"));
        RequestHeader requestHeader = new RequestHeader();
        _interfaceID = p_requestMap.get("INTERFACE_ID");
        try {
            requestHeader.setCommandId(p_requestMap.get("COMMAND_ID"));
            requestHeader.setVersion(FileCache.getValue(_interfaceID, "REQ_HEADER_VERSION"));
            requestHeader.setTransactionId(FileCache.getValue(_interfaceID, "REQ_HEADER_TRANX_TYPE"));
            requestHeader.setSequenceId(FileCache.getValue(_interfaceID, "REQ_HEADER_TRANX_TYPE"));
            // change the constructor to public from protected
            requestHeader.setRequestType(new RequestHeaderRequestType(FileCache.getValue(_interfaceID, "REQ_HEADER_TYPE")));
            requestHeader.setSerialNo(p_requestMap.get("TRANSACTION_ID"));
            return requestHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("setRequestHeaderObject", "Exiting requestHeader=" + requestHeader);
        }
    }
}
