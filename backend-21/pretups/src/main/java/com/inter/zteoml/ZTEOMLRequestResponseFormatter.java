package com.inter.zteoml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import zsmart.ztesoft.com.xsd.TDeductFeeRequest;
import zsmart.ztesoft.com.xsd.TDeductFeeResponse;
import zsmart.ztesoft.com.xsd.TQueryProfileAndBalBalDto;
import zsmart.ztesoft.com.xsd.TQueryProfileAndBalRequest;
import zsmart.ztesoft.com.xsd.TQueryProfileAndBalResponse;
import zsmart.ztesoft.com.xsd.TRechargingBenefitDto;
import zsmart.ztesoft.com.xsd.TRechargingRequest;
import zsmart.ztesoft.com.xsd.TRechargingResponse;
import zsmart.ztesoft.com.xsd.TRefundRequest;
import zsmart.ztesoft.com.xsd.TRefundResponse;
import zsmart.ztesoft.com.xsd.TTransferBalanceRequest;
import zsmart.ztesoft.com.xsd.TTransferBalanceResponse;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.Constants;

/**
 * @(#)ZTEOMLRequestResponseFormatter.java
 * Copyright(c) 2015, Mahindra Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *        Author                                Date                             History
 *-------------------------------------------------------------------------------------------------
 * 		 Diwakar      							Dec 09, 2015              		Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This class can be used as a parser class for both request(before sending the request to IN) and
 * response(after getting the response from the IN).
 */

public class ZTEOMLRequestResponseFormatter 
{
    private static Log _log = LogFactory.getLog(ZTEOMLRequestResponseFormatter.class.getName());
    private String _interfaceID = null;
    private static int _counter = 0;
    private String _selectorBundleId = null;
    private String _serviceType = null;
    /**
     * Constructor
     */
    public ZTEOMLRequestResponseFormatter() 
    {
        super();
    }


    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return profileAndBalRequest TQueryProfileAndBalRequest
     */
    protected TQueryProfileAndBalRequest generateQueryProfileAndBalRequest(HashMap<String, String> p_map) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[generateQueryProfileAndBalRequest()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_map=" + p_map);
        _serviceType = p_map.get("REQ_SERVICE");
        TQueryProfileAndBalRequest profileAndBalRequest = null;
        try 
        {
            getINRequestID(p_map);
            profileAndBalRequest = new TQueryProfileAndBalRequest();
            profileAndBalRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("MSISDN")));
            profileAndBalRequest.setTransactionSN(p_map.get("IN_REQ_ID"));

            return profileAndBalRequest;
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting with profileAndBalRequest="+ profileAndBalRequest);
        }
    }

    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return rechargingRequest TRechargingRequest
     */
    protected TRechargingRequest generateRechargingRequest(HashMap<String, String> p_map) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[generateRechargingRequest()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_map=" + p_map);
        String validity = null;
        String acctResCode = null;
        TRechargingRequest rechargingRequest = null;
        try 
        {
            getINRequestID(p_map);
            _selectorBundleId = p_map.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(_selectorBundleId))
                _selectorBundleId = _selectorBundleId.trim();
            _interfaceID = (p_map.get("INTERFACE_ID")).trim();
            //Fetching AddDays from request map
            validity = p_map.get("VALIDITY_DAYS");
            //If bonus validity is separate from the main validity, then subtract it from the main validity.
            String addMainBnsVal = FileCache.getValue(_interfaceID,"ADD_MAIN_AND_BUNUS_VALIDITY").trim();
            String bonusValidity = p_map.get("BONUS_VALIDITY_DAYS");
            if ("N".equals(addMainBnsVal)
                    && !InterfaceUtil.isNullString(bonusValidity.trim())
                    && !InterfaceUtil.isNullString(validity)) 
            {
                long mainVal = Long.parseLong(validity.trim());
                long bonusVal = Long.parseLong(bonusValidity);
                if (bonusVal > 0)
                    validity = String.valueOf(mainVal - bonusVal);
            } 
            else if (InterfaceUtil.isNullString(validity))
                validity = "0";

            //Fetching AddBalance from request map
            //Transfer amount of selector.
            long transAmtLong = 0;
            if (!InterfaceUtil.isNullString(p_map.get("transfer_amount"))) 
            {
                transAmtLong = Math.round(Double.parseDouble(p_map.get("transfer_amount")));
                transAmtLong = 0 - transAmtLong;
            }
            //Fetching AcctResCode from request map
            acctResCode = FileCache.getValue(_interfaceID, _selectorBundleId);
            //Now preparing recharge request object
            rechargingRequest = new TRechargingRequest();
            rechargingRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("MSISDN")));
            rechargingRequest.setTransactionSN(p_map.get("IN_REQ_ID"));
            rechargingRequest.setAddDays(Long.parseLong(validity));
            rechargingRequest.setAddBalance(String.valueOf(transAmtLong));
            rechargingRequest.setAcctResCode(acctResCode);
            rechargingRequest.setChannel_ID(p_map.get("CHANNEL_ID"));
            getBundleRequestRechargingString(p_map,rechargingRequest);
            return rechargingRequest;
        }
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting with rechargingRequest="+ rechargingRequest);
        }
    }

    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return transferBalRequest TTransferBalanceRequest
     */
    protected TTransferBalanceRequest generateTransferBalanceRequest(HashMap<String, String> p_map) throws Exception
    {
        String methodName = "ZTEOMLRequestResponseFormatter[generateTransferBalanceRequest()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_map=" + p_map);
        TTransferBalanceRequest transferBalRequest = null;
        String acctResCode = null;
        try 
        {
            getINRequestID(p_map);
            _selectorBundleId = p_map.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(_selectorBundleId))
                _selectorBundleId = _selectorBundleId.trim();
            _interfaceID = (p_map.get("INTERFACE_ID")).trim();
            //Re-charge amount of selector.
            long transAmtDbl=0;
            if(!InterfaceUtil.isNullString(p_map.get("transfer_amount")))
            {
                transAmtDbl=Long.parseLong(p_map.get("transfer_amount"));
            }
            //Fetching AcctResCode from request map
            acctResCode = FileCache.getValue(_interfaceID, _selectorBundleId);

            transferBalRequest = new TTransferBalanceRequest();
            transferBalRequest.setFromMSISDN(InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("SENDER_MSISDN")));
            transferBalRequest.setToMSISDN(InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("RECEIVER_MSISDN")));
            transferBalRequest.setTransactionSN(p_map.get("IN_REQ_ID"));
            transferBalRequest.setAcctResCode(acctResCode);
            transferBalRequest.setAmount(String.valueOf(transAmtDbl));
            transferBalRequest.setChannel_ID(p_map.get("CHANNEL_ID"));
            return transferBalRequest;
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting with profileAndBalRequest="+ transferBalRequest);
        }
    }
    
    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return transferBalRequest TTransferBalanceRequest
     */
    protected TDeductFeeRequest generateDeductFeeRequest(HashMap<String, String> p_map) throws Exception
    {
        String methodName = "ZTEOMLRequestResponseFormatter[generateDebitDeductFeeRequest()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_map=" + p_map);
        TDeductFeeRequest debitBalanceRequest=null;
        String acctResCode = null;
        try 
        {
            getINRequestID(p_map);
            _selectorBundleId = p_map.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(_selectorBundleId))
                _selectorBundleId = _selectorBundleId.trim();
            _interfaceID = (p_map.get("INTERFACE_ID")).trim();
            //Re-charge amount of selector.
            long transAmtDbl=0;
            if(!InterfaceUtil.isNullString(p_map.get("transfer_amount")))
            {
                transAmtDbl=Long.parseLong(p_map.get("transfer_amount"));
            }
            //Fetching AcctResCode from request map
            acctResCode = FileCache.getValue(_interfaceID, "Main");

            debitBalanceRequest=new TDeductFeeRequest();
            debitBalanceRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("SENDER_MSISDN")));
            //debitBalanceRequest.setToMSISDN(InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("RECEIVER_MSISDN")));
            debitBalanceRequest.setTransactionSN(p_map.get("IN_REQ_ID"));
            debitBalanceRequest.setAcctResCode(acctResCode);
            debitBalanceRequest.setDeductBalance(String.valueOf(transAmtDbl));
            debitBalanceRequest.setChannel_ID(p_map.get("CHANNEL_ID"));
            return debitBalanceRequest;
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting with AdjDebitBalanceRequest="+ debitBalanceRequest);
        }
    }
    
    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return transferBalRequest TTransferBalanceRequest
     */
    protected TRefundRequest generateRefundRequest(HashMap<String, String> p_map) throws Exception
    {
        String methodName = "ZTEOMLRequestResponseFormatter[generateRefundRequest()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered p_map=" + p_map);
        TRefundRequest RefundRequest=null;
        String acctResCode = null;
        try 
        {
            getINRequestID(p_map);
            _selectorBundleId = p_map.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(_selectorBundleId))
                _selectorBundleId = _selectorBundleId.trim();
            _interfaceID = (p_map.get("INTERFACE_ID")).trim();
            //Re-charge amount of selector.
            long transAmtDbl=0;
            if(!InterfaceUtil.isNullString(p_map.get("transfer_amount")))
            {
                transAmtDbl=Long.parseLong(p_map.get("transfer_amount"));
            }
            //Fetching AcctResCode from request map
            acctResCode = FileCache.getValue(_interfaceID, "Main");

            RefundRequest=new TRefundRequest();
            RefundRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(p_map.get("INTERFACE_ID"), p_map.get("SENDER_MSISDN")));
            RefundRequest.setTransactionSN(p_map.get("IN_REQ_ID"));
            RefundRequest.setAcctResCode(acctResCode);
            RefundRequest.setRefundBalance(String.valueOf(transAmtDbl));
            RefundRequest.setChannel_ID(p_map.get("CHANNEL_ID"));
            return RefundRequest;
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting with RefundRequest="+ RefundRequest);
        }
    }
    
    /**
     * This method parse the response from XML String into HashMap
     * @param action int
     * @param responseStr String
     * @return map HashMap<String,String>
     */
    protected HashMap<String, String> parseResponseObject(int action,TQueryProfileAndBalResponse p_profileAndBalResp,
                                                           TRechargingResponse p_rechargingResp,
                                                           TDeductFeeResponse p_tranferBalResp,TRefundResponse p_refundBalResponse) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[parseResponseObject()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered for action=" + action);
    
        HashMap<String, String> responseMap = null;
        try 
        {
            switch (action) 
            {
                case ZTEOMLI.ACTION_ACCOUNT_INFO: 
                {
                    responseMap = parseQueryProfileAndBalResponseObject(p_profileAndBalResp);
                    break;
                }
                case ZTEOMLI.ACTION_RECHARGE_CREDIT: 
                {
                    /*if ("PRC".equals(_serviceType))
                        responseMap = parseAdjustBalanceResponseObject(p_tranferBalResp);
                    else*/
                        responseMap = parseRechargingResponseObject(p_rechargingResp);
                    break;
                }
    
                case ZTEOMLI.ACTION_IMMEDIATE_DEBIT: 
                {
                    responseMap=parseAdjustBalanceResponseObject(p_tranferBalResp);
                    break;
                }
                case ZTEOMLI.ACTION_TXN_CANCEL: 
                {
                    responseMap=parseRefundResponseObject(p_refundBalResponse);
                    break;
                }
            }
        } 
        catch (BTSLBaseException be) 
        {
            throw be;
        } catch (Exception e) 
        {
            _log.error(methodName, "Exception e:" + e.getMessage());
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Exiting for action=" + action);
    
        return responseMap;
    }

    /**
     * This method parse the response from XML String into HashMap
     * @param action int
     * @param responseStr String
     * @return map HashMap<String,String>
     */
    protected HashMap<String, String> parseResponseObject(int action,TQueryProfileAndBalResponse p_profileAndBalResp,
                                                           TRechargingResponse p_rechargingResp,
                                                           TTransferBalanceResponse p_tranferBalResp) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[parseResponseObject()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered for action=" + action);
    
        HashMap<String, String> responseMap = null;
        try 
        {
            switch (action) 
            {
                case ZTEOMLI.ACTION_ACCOUNT_INFO: 
                {
                    responseMap = parseQueryProfileAndBalResponseObject(p_profileAndBalResp);
                    break;
                }
                case ZTEOMLI.ACTION_RECHARGE_CREDIT: 
                {
                    if ("PRC".equals(_serviceType))
                        responseMap = parseTransferBalanceResponseObject(p_tranferBalResp);
                    else
                        responseMap = parseRechargingResponseObject(p_rechargingResp);
                    break;
                }
    
                case ZTEOMLI.ACTION_IMMEDIATE_DEBIT: 
                {
                    responseMap=parseTransferBalanceResponseObject(p_tranferBalResp);
                    break;
                }
            }
        } 
        catch (BTSLBaseException be) 
        {
            throw be;
        } catch (Exception e) 
        {
            _log.error(methodName, "Exception e:" + e.getMessage());
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Exiting for action=" + action);
    
        return responseMap;
    }

    /**
     * Method parseQueryProfileAndBalResponseObject
     * @param	Response p_respObj
     * @return	HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseQueryProfileAndBalResponseObject(TQueryProfileAndBalResponse p_profileAndBalResp) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[parseQueryProfileAndBalResponseObject()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        HashMap<String, String> responseMap = null;
        try 
        {
            responseMap = new HashMap<String, String>();
            String _str = "1";
            if (null!=p_profileAndBalResp) 
            {
                responseMap.put("resp_returnCode", _str);
                //Fetch TransactionSN from response object
                String resp_transactionSN = p_profileAndBalResp.getTransactionSN();
                responseMap.put("resp_transactionSN", resp_transactionSN);

                //Fetch Service Class from response object
                String resp_serviceClass = p_profileAndBalResp.getServiceClass();
                responseMap.put("resp_serviceClass", resp_serviceClass);

                //Fetch MSISDN from response object
                String msisdn = p_profileAndBalResp.getMSISDN();
                responseMap.put("resp_msisdn", msisdn);

                //Fetch Default Language from response object
                String defLang = p_profileAndBalResp.getDefLang();
                responseMap.put("resp_defLang", defLang);

                //Fetch State from response object
                String resp_state = p_profileAndBalResp.getState();
                responseMap.put("resp_state", resp_state);

                //Fetch State Set from response object
                String resp_stateSet = p_profileAndBalResp.getStateSet();
                responseMap.put("resp_stateSet", resp_stateSet);

                //Fetch Active Stop Date from response object
                String resp_activeStopDate = p_profileAndBalResp.getActiveStopDate().toString();
                responseMap.put("resp_activeStopDate", resp_activeStopDate);

                //Fetch Suspend Stop Date from response object
                String resp_suspendStopDate = p_profileAndBalResp.getSuspendStopDate().toString();
                responseMap.put("resp_suspendStopDate", resp_suspendStopDate);

                //Fetch Disable Stop Date from response object
                String resp_disableStopDate = p_profileAndBalResp.getDisableStopDate().toString();
                responseMap.put("resp_disableStopDate", resp_disableStopDate);

                //Fetch Service Stop Date from response object
                String resp_serviceStopDate = p_profileAndBalResp.getServiceStopDate().toString();
                responseMap.put("resp_serviceStopDate", resp_serviceStopDate);

                //Fetch Brand Index from response object
                String resp_brandIndex = p_profileAndBalResp.getBrandIndex();
                responseMap.put("resp_brandIndex", resp_brandIndex);

                //Fetch List of Balances from response object
                TQueryProfileAndBalBalDto resp_balDoList[] = p_profileAndBalResp.getBalDtoList();
                //Set the bundle info received from the response in to the response map.
                getBundlesInfoRcvdFromIN(resp_balDoList, responseMap);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return responseMap;
    }

    /* Method parseRechargingResponseObject
     * @param	Response p_respObj
     * @return	HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseRechargingResponseObject(TRechargingResponse p_rechargingResp) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[parseRechargingResponseObject()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        HashMap<String, String> responseMap = null;
        try 
        {

            if(null!=p_rechargingResp)
            {
                responseMap = new HashMap<String, String>();
                responseMap.put("resp_returnCode","0");
                //Fetch TransactionSN from response object
                String transactionSN = p_rechargingResp.getTransactionSN();
                responseMap.put("resp_transactionSN", transactionSN);
                //Set the bundle info received from the response in to the response map.
                getBundlesInfoRcvdFromIN4Credit(p_rechargingResp.getBalDtoList(), responseMap);
            }
            return responseMap;
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
    }

    /* Method parseTransferBalanceResponseObject
     * @param	Response p_respObj
     * @return	HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseTransferBalanceResponseObject(TTransferBalanceResponse p_tranferBalResp) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[parseRechargingResponseObject()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        String balance="0";
        String bdl_ExpDate=null;
        String bdl_EffDate=null;
        HashMap<String, String> responseMap = null;
        try 
        {
            if(null!=p_tranferBalResp)
            {
                responseMap = new HashMap<String, String>();
                //Fetch TransactionSN from response object
                String transactionSN = p_tranferBalResp.getTransactionSN();
                responseMap.put("resp_transactionSN", transactionSN);
    
                //Fetch List of Balances from response object
                for (int i=0;i<p_tranferBalResp.getBalDtoList().length;i++)
                {
                    if(null!=p_tranferBalResp.getBalDtoList()[i])
                    {
                        if (p_tranferBalResp.getBalDtoList()[i].getAcctResCode().equalsIgnoreCase(_selectorBundleId))
                        {
                            balance=p_tranferBalResp.getBalDtoList()[i].getBalance();
                            long bdl_BalanceLng=Long.parseLong(balance);
                            bdl_BalanceLng=0-(bdl_BalanceLng);
                            balance=String.valueOf(bdl_BalanceLng);                            
                            responseMap.put("resp_Balance",balance);
                            bdl_ExpDate=getDateStringFromDate(p_tranferBalResp.getBalDtoList()[i].getExpDate());
                            responseMap.put("resp_ExpDate",bdl_ExpDate);
                            bdl_EffDate=getDateStringFromDate(p_tranferBalResp.getBalDtoList()[i].getEffDate());
                            responseMap.put("resp_EffDate",bdl_EffDate);
                        }
                    }
    
                }
                String resp_balDoList = p_tranferBalResp.getBalDtoList()[0].getBalance();
                responseMap.put("resp_balDoList", resp_balDoList);
            }
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
        return responseMap;
    }
    
    
    /* Method parseTransferBalanceResponseObject
     * @param   Response p_respObj
     * @return  HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseAdjustBalanceResponseObject(TDeductFeeResponse p_AdjustBalResp) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[parseAdjustBalanceResponseObject()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        String balance="0";
        String bdl_ExpDate=null;
        String bdl_Balance="0";
        HashMap<String, String> responseMap = null;
        try 
        {
            if(null!=p_AdjustBalResp)
            {
                responseMap = new HashMap<String, String>();
                //Fetch TransactionSN from response object
                String transactionSN = p_AdjustBalResp.getTransactionSN();
                responseMap.put("resp_transactionSN", transactionSN);
    
                //Fetch List of Balances from response object
                if (p_AdjustBalResp.getAcctResCode().equalsIgnoreCase(_selectorBundleId))
                {
                    bdl_Balance=p_AdjustBalResp.getAfterBalance();
                    long bdl_BalanceLng=Long.parseLong(bdl_Balance);
                    bdl_BalanceLng=0-(bdl_BalanceLng);
                    balance=String.valueOf(bdl_BalanceLng);
                    responseMap.put("resp_Balance",balance);
                    bdl_ExpDate=getDateStringFromDate(p_AdjustBalResp.getExpDate());
                    responseMap.put("resp_ExpDate",bdl_ExpDate);
                }
    
            }
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
        return responseMap;
    }
    
    /* Method parseTransferBalanceResponseObject
     * @param   Response p_respObj
     * @return  HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseRefundResponseObject(TRefundResponse p_AdjustBalResp) throws Exception 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[parseRefundResponseObject()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        String balance="0";
        String bdl_ExpDate=null;
        HashMap<String, String> responseMap = null;
        try 
        {
            if(null!=p_AdjustBalResp)
            {
                responseMap = new HashMap<String, String>();
                //Fetch TransactionSN from response object
                String transactionSN = p_AdjustBalResp.getTransactionSN();
                responseMap.put("resp_transactionSN", transactionSN);
    
                //Fetch List of Balances from response object
                if (p_AdjustBalResp.getAcctResCode().equalsIgnoreCase(_selectorBundleId))
                {
                    balance=p_AdjustBalResp.getBalance();
                    long bdl_BalanceLng=Long.parseLong(balance);
                    bdl_BalanceLng=0-(bdl_BalanceLng);
                    balance=String.valueOf(bdl_BalanceLng);
                    responseMap.put("resp_Balance",balance);
                    bdl_ExpDate=getDateStringFromDate(p_AdjustBalResp.getExpDate());
                    responseMap.put("resp_ExpDate",bdl_ExpDate);
                }
    
            }
        } 
        catch (Exception e) 
        {
            if (_log.isDebugEnabled())
                _log.error(methodName, "Exception e: " + e);
            e.printStackTrace();
            throw e;
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
        return responseMap;
    }

    /**
     * This Method will generate the IN Transaction ID for each request.
     * @param p_map HashMap
     * @throws BTSLBaseException
     * @return _counter String
     */
    public static synchronized String getIncrCounter() throws BTSLBaseException 
    {
        String methodName = "ZTEOMLRequestResponseFormatter[getIncrCounter()]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered");
        try 
        {
            if (_counter == 99999)
                _counter = 0;
            _counter++;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            if (_log.isDebugEnabled())
                _log.error(methodName, e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES, EventStatusI.RAISED,EventLevelI.FATAL,methodName, "", "", ""," Error occurs while getting IN request id Exception is "+ e.getMessage());
            throw new BTSLBaseException(e.getMessage());
        } 
        finally 
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting counter = " + _counter);
        }
        return String.valueOf(_counter);
    }
    /**
     * This Method will parse  GetAccount Info Response for received bundles.
     * @param p_responseStr String
     * @param p_requestMap HashMap
     * @throws Exception
     * @return void
     */ 
    private void getBundlesInfoRcvdFromIN(TQueryProfileAndBalBalDto p_resp_balDoList[], HashMap<String,String> p_requestMap) throws Exception
    {
        String methodName="ZTEOMLRequestResponseFormatter[getBundlesInfoRcvdFromIN()]:";
        if (_log.isDebugEnabled())  _log.debug(methodName,"Entered ");
        int noOfBundles=0;
        String bdl_code=null;
        String bdlCodeStr=null;
        try
        {
            //Check  whether bundle is present in string or not, if present then split it.
            noOfBundles=p_resp_balDoList.length;
            if (noOfBundles>0)
            {
                for(int i=0; i<noOfBundles; i++)
                {
                    //get the bdl_name if present in the bucket.
                    if(null!=p_resp_balDoList[i])
                    {
                        bdl_code=p_resp_balDoList[i].getAcctResCode();
                        if(bdl_code.equalsIgnoreCase("1"))
                        {
                            String bdl_Balance=p_resp_balDoList[i].getBalance();
                            long bdl_BalanceLng=Long.parseLong(bdl_Balance);
                            bdl_BalanceLng=0-(bdl_BalanceLng);
                            bdl_Balance=String.valueOf(bdl_BalanceLng);
                            p_requestMap.put("resp_Balance",bdl_Balance);
                            String bdl_ExpDate=getDateStringFromDate(p_resp_balDoList[i].getExpDate());
                            p_requestMap.put("resp_ExpDate",bdl_ExpDate);
                            String bdl_EffDate=getDateStringFromDate(p_resp_balDoList[i].getEffDate());
                            p_requestMap.put("resp_EffDate",bdl_EffDate);
                        }
                        if(!InterfaceUtil.isNullString(bdlCodeStr))
                            bdlCodeStr=bdlCodeStr+","+bdl_code;
                        else
                            bdlCodeStr=bdl_code;
                    }   
                }
            }
            if(!InterfaceUtil.isNullString(bdlCodeStr))
                p_requestMap.put("received_bundles",bdlCodeStr.trim()); 
            else
                p_requestMap.put("received_bundles",bdlCodeStr);    
        }
        catch(Exception e)
        {
            if (_log.isDebugEnabled())  _log.error(methodName,"Exception e: "+e.getMessage());
            throw e;
        }
        finally
        {
            if (_log.isDebugEnabled())  _log.debug(methodName,"Exiting Defined bundles at IN="+p_requestMap.get("received_bundles"));
        }
    }
    /**
     * This Method will parse the Credit Response for received bundles.
     * @param p_responseStr String
     * @param p_requestMap HashMap
     * @throws Exception
     * @return void
     */     
    private void getBundlesInfoRcvdFromIN4Credit(zsmart.ztesoft.com.xsd.TRechargingBalDto p_resp_balDoList[], HashMap<String,String> p_requestMap) throws Exception
    {
        String methodName="ZTEOMLRequestResponseFormatter[getBundlesInfoRcvdFromIN4Credit()]";
        if (_log.isDebugEnabled())  _log.debug(methodName,"Entered ");
        int noOfBundles=0;
        String bdl_code=null;
        String bdlCodeStr=null;
        try
        {
            //Check  whether bundle is present in string or not, if present then split it.
            noOfBundles=p_resp_balDoList.length;
            if (noOfBundles>0)
            {
                for(int i=0; i<noOfBundles; i++)
                {
                    //get the bdl_name if present in the bucket.
                    if(null!=p_resp_balDoList[i])
                    {
                        bdl_code=p_resp_balDoList[i].getAcctResCode();
                        if(bdl_code.equalsIgnoreCase("1"))
                        {
                            String bdl_Balance=p_resp_balDoList[i].getBalance();
                            long bdl_BalanceLng=Long.parseLong(bdl_Balance);
                            bdl_BalanceLng=0-(bdl_BalanceLng);
                            bdl_Balance=String.valueOf(bdl_BalanceLng);
                            p_requestMap.put("resp_Balance",bdl_Balance);
                            String bdl_ExpDate=getDateStringFromDate(p_resp_balDoList[i].getExpDate());
                            p_requestMap.put("resp_ExpDate",bdl_ExpDate);
                            String bdl_EffDate=getDateStringFromDate(p_resp_balDoList[i].getEffDate());
                            p_requestMap.put("resp_EffDate",bdl_EffDate);
                        }
                        if(!InterfaceUtil.isNullString(bdlCodeStr))
                            bdlCodeStr=bdlCodeStr+","+bdl_code;
                        else
                            bdlCodeStr=bdl_code;
                    }
                }
            }
            if(!InterfaceUtil.isNullString(bdlCodeStr))
                p_requestMap.put("received_bundles",bdlCodeStr.trim()); 
            else
                p_requestMap.put("received_bundles",bdlCodeStr);    
        }
        catch(Exception e)
        {
            if (_log.isDebugEnabled())  _log.error(methodName,"Exception e: "+e.getMessage());
            throw e;
        }
        finally
        {
            if (_log.isDebugEnabled())  _log.debug(methodName,"Exiting Defined bundles at IN="+p_requestMap.get("received_bundles"));
        }
    }
    private void getBundleRequestRechargingString(HashMap<String,String> p_map, TRechargingRequest p_rechargingRequest) throws Exception
    {
        String methodName="ZTEOMLRequestResponseFormatter[getBundleRequestRechargingString()]";
        if(_log.isDebugEnabled())
            _log.debug(methodName,"Entered  ");

        String bundleReqStr="";
        String[] bundleIds=null;
        String[] bundleValidities=null;
        String[] bundleValues=null;
        String[] bundleTypes=null;
        int bonusBundleCount=0;
        TRechargingBenefitDto[] BalDtoList=null;
        try
        {
            long bundleValidityLong=0;
            if(!InterfaceUtil.isNullString(p_map.get("BONUS_BUNDLE_IDS")))
            {
                bundleIds=(p_map.get("BONUS_BUNDLE_IDS")).split("\\|");
                BalDtoList=new TRechargingBenefitDto[bundleIds.length];
                bundleValidities=(p_map.get("BONUS_BUNDLE_VALIDITIES")).split("\\|");
                bundleValues=(p_map.get("BONUS_BUNDLE_VALUES")).split("\\|");
                bundleTypes=(p_map.get("BONUS_BUNDLE_TYPES")).split("\\|");
                bonusBundleCount=bundleIds.length;
                int i=0;
                for(i=0; i<bonusBundleCount;i++)
                {
                    String bundleCode="0";
                    bundleCode=FileCache.getValue(_interfaceID,bundleIds[i]);
                    String bundleValue=bundleValues[i];
                    String bundleValidity=bundleValidities[i];
                    bundleValidityLong=Long.parseLong(bundleValidity);
                    double bundleValueDbl=Double.parseDouble(bundleValue);
                    double inbundleValueDbl=0;
                    long inbundleValueLng=0;
                    if(bundleValueDbl>0)
                    {
                        if("AMT".equals(bundleTypes[i]))
                            inbundleValueDbl=InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl,Double.parseDouble(FileCache.getValue(_interfaceID,"AMT_MULT_FACTOR")));
                        else 
                            inbundleValueDbl=InterfaceUtil.getINAmountFromSystemAmountToIN(bundleValueDbl,Double.parseDouble(FileCache.getValue(_interfaceID,"UNIT_MULT_FACTOR")));
                        inbundleValueLng=Math.round(inbundleValueDbl);
                        inbundleValueLng=0-inbundleValueLng;
                        BalDtoList[i]=new TRechargingBenefitDto();
                        BalDtoList[i].setAcctResCode(bundleCode);
                        if(bundleValidityLong>0)
                            BalDtoList[i].setAddDays(bundleValidityLong);
                        /*if(!"AMT".equals(bundleTypes[i]))
                        {
                            long bonusvalue=Math.round(Double.parseDouble(bundleValue));
                            bonusvalue=0-bonusvalue;
                            BalDtoList[i].setAddBalance(String.valueOf(bonusvalue));
                        }
                        else*/
                            BalDtoList[i].setAddBalance(String.valueOf(inbundleValueLng));
                    }
                }
                if(i>0)
                    p_rechargingRequest.setBenefitDtoList(BalDtoList);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }
        finally
        {
            if(_log.isDebugEnabled())
                _log.debug(methodName,"Exiting  bundleReqStr="+bundleReqStr);
        }
    }
    /**
     * This Method will generate the IN Request ID(a unique sequence identify a Request, can not be repeated),
     * for each request. 
     * The format is: Channel_ID+yyyyMMddHHmmss+8 bit sequence no.
     * @param p_map HashMap
     * @throws Exception
     * @return dateStrReqTime String
     */ 
    private String getINRequestID(HashMap<String,String> p_map) throws Exception
    {
        String methodName="ZTEOMLRequestResponseFormatter[getINRequestID()]";
        if (_log.isDebugEnabled())  _log.debug(methodName,"Entered");

        String reqId="";
        String counter="";
        String dateStrReqId = null;
        String dateStrReqTime = null;
        String timeStrReqTime = null;
        SimpleDateFormat sdfReqId =null;
        SimpleDateFormat sdfReqTime =null;
        SimpleDateFormat sdfTimeReqTime =null;

        // 5 bit sequence number is required to generate the IN Request ID.
        int inTxnLength=5;
        try
        {
            _serviceType=p_map.get("REQ_SERVICE");
            _selectorBundleId=p_map.get("SELECTOR_BUNDLE_ID");
            if(!InterfaceUtil.isNullString(_selectorBundleId))
                _selectorBundleId=_selectorBundleId.trim();
            _interfaceID=(p_map.get("INTERFACE_ID")).trim();
            Date mydate = new Date();
            sdfReqId = new SimpleDateFormat ("yyMMdd");
            sdfReqTime = new SimpleDateFormat ("yyyyMMddHHmmss");
            sdfTimeReqTime= new SimpleDateFormat ("HHmmss");
            dateStrReqId = sdfReqId.format(mydate);
            dateStrReqTime = sdfReqTime.format(mydate);
            timeStrReqTime = sdfTimeReqTime.format(mydate);
            counter = getIncrCounter();

            int length = counter.length();
            int tmpLength=inTxnLength-length;
            if(length<inTxnLength)
            {
                for(int i=0;i<tmpLength;i++)
                    counter = "0"+counter;
            }

            reqId =dateStrReqId+timeStrReqTime+Constants.getProperty("INSTANCE_ID")+counter;
            //p_map.put("IN_REQ_ID",reqId);
            p_map.put("IN_REQ_ID",p_map.get("IN_RECON_ID"));
            p_map.put("IN_REQ_TIME",dateStrReqTime);

            if (_log.isDebugEnabled())  _log.debug(methodName,"Exited  id: "+counter+", reqId="+reqId);

            return reqId;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if (_log.isDebugEnabled())  _log.error(methodName,"Exception e::"+e.getMessage());
            throw e;
        }//end of catch-Exception
    }
    /**
     * Get Date String From Date
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateStringFromDate(Date date)
    {
        String format="yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat (format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }
}
