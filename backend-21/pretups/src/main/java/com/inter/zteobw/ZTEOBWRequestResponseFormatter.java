package com.inter.zteobw;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import zsmart.ztesoft.com.xsd.TBalDto;
import zsmart.ztesoft.com.xsd.TBalDto3;
import zsmart.ztesoft.com.xsd.TModifyAllBalReturnAllBalRequest;
import zsmart.ztesoft.com.xsd.TModifyAllBalReturnAllBalResponse;
import zsmart.ztesoft.com.xsd.TQueryProfileAndBalRequest;
import zsmart.ztesoft.com.xsd.TQueryProfileAndBalResponse;
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
 * @(#)ZTEOBWRequestResponseFormatter.java
 * Copyright(c) 2009, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *        Author                                Date                             History
 *-------------------------------------------------------------------------------------------------
 * Diwakar      May 09, 2009              Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This class can be used as a parser class for both request(before sending the request to IN) and
 * response(after getting the response from the IN).
 */

public class ZTEOBWRequestResponseFormatter
{
    private static Log log = LogFactory.getLog(ZTEOBWRequestResponseFormatter.class.getName());
    private String interfaceID = null;
    private static int counter = 0;
    private String selectorBundleId = null;
    private String serviceType = null;
    /**
     * Constructor
     */
    public ZTEOBWRequestResponseFormatter()
    {
        super();
    }


    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return profileAndBalRequest TQueryProfileAndBalRequest
     */
    protected TQueryProfileAndBalRequest generateTQueryProfileAndBalRequest(HashMap<String, String> pMap) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[generateQueryProfileAndBalRequest()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_map=" + pMap);
        serviceType = pMap.get("REQ_SERVICE");
        TQueryProfileAndBalRequest profileAndBalRequest = null;
        try
        {
            getINRequestID(pMap);
            profileAndBalRequest = new TQueryProfileAndBalRequest();
            profileAndBalRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(pMap.get("INTERFACE_ID"), pMap.get("MSISDN")));
            profileAndBalRequest.setUserPwd("");
            if (log.isDebugEnabled())
                log.debug("INTERFACE_ID: ",pMap.get("INTERFACE_ID")+" profileAndBalRequest="+ profileAndBalRequest.getUserPwd()+" : "+profileAndBalRequest.getMSISDN());
            return profileAndBalRequest;
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error(methodName, " Exception e: " + e.getStackTrace());
            throw e;
        }
        finally
        {
         if (log.isDebugEnabled())
                log.debug(methodName, "Exiting ");
}
    }

    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return rechargingRequest TRechargingRequest
     */
    protected TModifyAllBalReturnAllBalRequest generateRechargingRequest(HashMap<String, String> pMap) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[generateRechargingRequest()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_map=" + pMap);
        String validity = null;
        String acctResCode = null;
        TModifyAllBalReturnAllBalRequest rechargingRequest = null;
        try
        {
            getINRequestID(pMap);
            selectorBundleId = pMap.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(selectorBundleId))
                selectorBundleId = selectorBundleId.trim();
            interfaceID = (pMap.get("INTERFACE_ID")).trim();
            //Fetching AddDays from request map
            validity = pMap.get("VALIDITY_DAYS");
            //If bonus validity is separate from the main validity, then subtract it from the main validity.
            String addMainBnsVal = FileCache.getValue(interfaceID,"ADD_MAIN_AND_BUNUS_VALIDITY").trim();
            String bonusValidity = pMap.get("BONUS_VALIDITY_DAYS");
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
            if (!InterfaceUtil.isNullString(pMap.get("transfer_amount")))
            {
                transAmtLong = Math.round(Double.parseDouble(pMap.get("transfer_amount")));
                transAmtLong = 0 - transAmtLong;
            }
            //Fetching AcctResCode from request map
            acctResCode = FileCache.getValue(interfaceID, selectorBundleId);
            //Now preparing recharge request object
            rechargingRequest = new TModifyAllBalReturnAllBalRequest();
            rechargingRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(pMap.get("INTERFACE_ID"), pMap.get("MSISDN")));
            rechargingRequest.setTransactionSN(pMap.get("IN_REQ_ID"));
            rechargingRequest.setAddDays(Long.parseLong(validity));
            rechargingRequest.setAddBalance(String.valueOf(transAmtLong));
            rechargingRequest.setAcctResID(acctResCode);
            return rechargingRequest;
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error(methodName, "Exception e : " + e.getStackTrace());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting with rechargingRequest="+ rechargingRequest);
        }
    }

    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return transferBalRequest TTransferBalanceRequest
     */
    protected TModifyAllBalReturnAllBalRequest generateDebitRequest(HashMap<String, String> pMap) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[generateDebitRequest()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_map=" + pMap);
        TModifyAllBalReturnAllBalRequest debitBalanceRequest=null;
        String acctResCode = null;
        try
        {
            getINRequestID(pMap);
            selectorBundleId = pMap.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(selectorBundleId))
                selectorBundleId = selectorBundleId.trim();
            interfaceID = (pMap.get("INTERFACE_ID")).trim();
            //Re-charge amount of selector.
            long transAmtDbl=0;
            if(!InterfaceUtil.isNullString(pMap.get("transfer_amount")))
            {
                transAmtDbl=Long.parseLong(pMap.get("transfer_amount"));
            }
            //Fetching AcctResCode from request map
            acctResCode = FileCache.getValue(interfaceID, "Main");

            debitBalanceRequest=new TModifyAllBalReturnAllBalRequest();
            debitBalanceRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(pMap.get("INTERFACE_ID"), pMap.get("SENDER_MSISDN")));
            debitBalanceRequest.setTransactionSN(pMap.get("IN_REQ_ID"));
            debitBalanceRequest.setAddDays(Long.parseLong("0"));
            debitBalanceRequest.setAddBalance(String.valueOf(transAmtDbl));
            debitBalanceRequest.setAcctResID(acctResCode);
            return debitBalanceRequest;
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error(methodName, " Exception e: " + e.getStackTrace());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting with generateDebitRequest="+ debitBalanceRequest);
        }
    }

    /**
     * This Method generate account information request
     * @param map HashMap
     * @throws Exception
     * @return transferBalRequest TTransferBalanceRequest
     */
    protected TModifyAllBalReturnAllBalRequest generateRefundRequest(HashMap<String, String> pMap) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[generateRefundRequest()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_map=" + pMap);
        TModifyAllBalReturnAllBalRequest refundRequest=null;
        String acctResCode = null;
        try
        {
            getINRequestID(pMap);
            selectorBundleId = pMap.get("SELECTOR_BUNDLE_ID");
            if (!InterfaceUtil.isNullString(selectorBundleId))
                selectorBundleId = selectorBundleId.trim();
            interfaceID = (pMap.get("INTERFACE_ID")).trim();
            //Re-charge amount of selector.
            long transAmtDbl=0;
            if(!InterfaceUtil.isNullString(pMap.get("transfer_amount")))
            {
                transAmtDbl=Long.parseLong(pMap.get("transfer_amount"));
                transAmtDbl = 0 - transAmtDbl;
            }
            //Fetching AcctResCode from request map
            acctResCode = FileCache.getValue(interfaceID, "Main");

            refundRequest=new TModifyAllBalReturnAllBalRequest();
            refundRequest.setMSISDN(InterfaceUtil.getFilterMSISDN(pMap.get("INTERFACE_ID"), pMap.get("SENDER_MSISDN")));
            refundRequest.setTransactionSN(pMap.get("IN_REQ_ID"));
            refundRequest.setAcctResID(acctResCode);
			refundRequest.setAddDays(Long.parseLong("0"));
            refundRequest.setAddBalance(String.valueOf(transAmtDbl));
            return refundRequest;
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error(methodName, "Exception  e: " + e.getStackTrace());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting with RefundRequest="+ refundRequest);
        }
    }
    /**
     * This method parse the response from XML String into HashMap
     * @param action int
     * @param responseStr String
     * @return map HashMap<String,String>
     */
    protected HashMap<String, String> parseResponseObject(int action,TQueryProfileAndBalResponse pProfileAndBalResp,TModifyAllBalReturnAllBalResponse modifyAllBalReturnAllBalResp) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[parseResponseObject()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered for action=" + action);

        HashMap<String, String> responseMap = null;
        try
        {
            switch (action)
            {
                case ZTEOBWI.ACTION_ACCOUNT_INFO:
                {
                    responseMap = parseQueryProfileAndBalResponseObject(pProfileAndBalResp);
                    break;
                }
                case ZTEOBWI.ACTION_RECHARGE_CREDIT:
                {
                    if ("PRC".equals(serviceType))
                        responseMap = parseDebitResponseObject(modifyAllBalReturnAllBalResp);
                    else
                        responseMap = parseRechargingResponseObject(modifyAllBalReturnAllBalResp);
                    break;
                }

                case ZTEOBWI.ACTION_IMMEDIATE_DEBIT:
                {
                    responseMap=parseDebitResponseObject(modifyAllBalReturnAllBalResp);
                    break;
                }
            }
        }
        catch (BTSLBaseException be)
        {
            throw be;
        } catch (Exception e)
        {
            log.error(methodName, "Exception e:" + e.getMessage());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, " Exiting responseMap=" + responseMap);
        }
        if (log.isDebugEnabled())
            log.debug(methodName, "Exiting for action=" + action);

        return responseMap;
    }

    /**
     * Method parseQueryProfileAndBalResponseObject
     * @param   Response p_respObj
     * @return  HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseQueryProfileAndBalResponseObject(TQueryProfileAndBalResponse pProfileAndBalResp) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[parseQueryProfileAndBalResponseObject()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered: ");
        HashMap<String, String> responseMap = null;
        try
        {
            responseMap = new HashMap<>();
            String str = "1";
            if (null!=pProfileAndBalResp)
            {
                responseMap.put("resp_returnCode", str);

                //Fetch Service Class from response object
                String respServiceClass = pProfileAndBalResp.getServiceClass();
                responseMap.put("resp_serviceClass", respServiceClass);

                //Fetch MSISDN from response object
                String msisdn = pProfileAndBalResp.getMSISDN();
                responseMap.put("resp_msisdn", msisdn);

                //Fetch Default Language from response object
                String defLang = pProfileAndBalResp.getDefLang();
                responseMap.put("resp_defLang", defLang);

                //Fetch State from response object
                String respState = pProfileAndBalResp.getState();
                responseMap.put("resp_state", respState);

                //Fetch State Set from response object
                String respStateSet = pProfileAndBalResp.getStateSet();
                responseMap.put("resp_stateSet", respStateSet);

                //Fetch Active Stop Date from response object
                String respActiveStopDate = pProfileAndBalResp.getActiveStopDate();
                responseMap.put("resp_activeStopDate", respActiveStopDate);

                //Fetch Suspend Stop Date from response object
                String respSuspendStopDate = pProfileAndBalResp.getSuspendStopDate();
                responseMap.put("resp_suspendStopDate", respSuspendStopDate);

                //Fetch Disable Stop Date from response object
                String respDisableStopDate = pProfileAndBalResp.getDisableStopDate();
                responseMap.put("resp_disableStopDate", respDisableStopDate);

                //Fetch Service Stop Date from response object
                String respServiceStopDate = pProfileAndBalResp.getServiceStopDate();
                responseMap.put("resp_serviceStopDate", respServiceStopDate);

                //Fetch Brand Index from response object
                String respBrandIndex = pProfileAndBalResp.getBrandIndex();
                responseMap.put("resp_brandIndex", respBrandIndex);

                //Fetch List of Balances from response object
                TBalDto resp_balDoList[] = pProfileAndBalResp.getBalDtoList();
                //Set the bundle info received from the response in to the response map.
                getBundlesInfoRcvdFromIN(resp_balDoList, responseMap);
            }
        }
        catch (Exception e)
        {
            if(log.isDebugEnabled())
            	log.errorTrace(methodName, e);
        }
        return responseMap;
    }

    /* Method parseRechargingResponseObject
     * @param   Response p_respObj
     * @return  HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseRechargingResponseObject(TModifyAllBalReturnAllBalResponse pRechargingResp) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[parseRechargingResponseObject()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered ");
        HashMap<String, String> responseMap = null;
        try
        {

            if(null!=pRechargingResp)
            {
                responseMap = new HashMap<String, String>();
                responseMap.put("resp_returnCode","0");
                //Set the bundle info received from the response in to the response map.
                getBundlesInfoRcvdFromIN4Credit(pRechargingResp.getBalDtoList(), responseMap);
            }
            return responseMap;
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error(methodName, "Exception  e: " + e.getStackTrace());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
    }

    /* Method parseTransferBalanceResponseObject
     * @param   Response p_respObj
     * @return  HashMap map
     * @throws  Exception
     */
    private HashMap<String, String> parseDebitResponseObject(TModifyAllBalReturnAllBalResponse pTranferBalResp) throws Exception
    {
        String methodName = "ZTEOBWRequestResponseFormatter[parseRechargingResponseObject()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered ");
        String balance="0";
        String bdlExpDate=null;
        String bdlEffDate=null;
        HashMap<String, String> responseMap = null;
        try
        {
            if(null!=pTranferBalResp)
            {
                responseMap = new HashMap<String, String>();

                //Fetch List of Balances from response object
                for (int i=0;i<pTranferBalResp.getBalDtoList().length;i++)
                {
                    if(null!=pTranferBalResp.getBalDtoList()[i])
                    {
                        if (pTranferBalResp.getBalDtoList()[i].getAcctResID().equalsIgnoreCase(selectorBundleId))
                        {
                            balance=pTranferBalResp.getBalDtoList()[i].getBalance();
                            long bdlBalanceLng=Long.parseLong(balance);
                            bdlBalanceLng=0-(bdlBalanceLng);
                            balance=String.valueOf(bdlBalanceLng);
                            responseMap.put("resp_Balance",balance);
                            bdlExpDate=pTranferBalResp.getBalDtoList()[i].getExpDate();
                            responseMap.put("resp_ExpDate",bdlExpDate);
                            bdlEffDate=pTranferBalResp.getBalDtoList()[i].getEffDate();
                            responseMap.put("resp_EffDate",bdlEffDate);
                        }
                    }

                }
                String respBalDoList = pTranferBalResp.getBalDtoList()[0].getBalance();
                responseMap.put("resp_balDoList", respBalDoList);
            }
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error(methodName, " Exception e: " + e.getStackTrace());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting responseMap=" + responseMap);
        }
        return responseMap;
    }

    /**
     * This Method will generate the IN Transaction ID for each request.
     * @param p_map HashMap
     * @throws BTSLBaseException
     * @return counter String
     */
    public static synchronized String getIncrCounter() throws BTSLBaseException
    {
        String methodName = "ZTEOBWRequestResponseFormatter[getIncrCounter()]";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered");
        try
        {
            if (counter == 99999)
                counter = 0;
            counter++;
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
                log.error(methodName, e.getStackTrace());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES, EventStatusI.RAISED,EventLevelI.FATAL,methodName, "", "", ""," Error occurs while getting IN request id Exception is "+ e.getMessage());
            throw new BTSLBaseException(e.getMessage());
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting counter = " + counter);
        }
        return String.valueOf(counter);
    }
    /**
     * This Method will parse  GetAccount Info Response for received bundles.
     * @param p_responseStr String
     * @param p_requestMap HashMap
     * @throws Exception
     * @return void
     */
    private void getBundlesInfoRcvdFromIN(TBalDto p_resp_balDoList[], HashMap<String,String> pRequestMap) throws Exception
    {
        String methodName="ZTEOBWRequestResponseFormatter[getBundlesInfoRcvdFromIN()]:";
        if (log.isDebugEnabled()) 
        	log.debug(methodName,"Entered ");
        int noOfBundles=0;
        String bdlCode=null;
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
                        bdlCode=p_resp_balDoList[i].getAcctResCode();
                        if(bdlCode.equalsIgnoreCase("1"))
                        {
                            String bdlBalance=p_resp_balDoList[i].getBalance();
                            long bdlBalanceLng=Long.parseLong(bdlBalance);
                            bdlBalanceLng=0-(bdlBalanceLng);
                            bdlBalance=String.valueOf(bdlBalanceLng);
                            pRequestMap.put("resp_Balance",bdlBalance);
                            String bdlExpDate=p_resp_balDoList[i].getExpDate();
                            pRequestMap.put("resp_ExpDate",bdlExpDate);
                            String bdlEffDate=p_resp_balDoList[i].getEffDate();
                            pRequestMap.put("resp_EffDate",bdlEffDate);
                        }
                        if(!InterfaceUtil.isNullString(bdlCodeStr))
                            bdlCodeStr=bdlCodeStr+","+bdlCode;
                        else
                            bdlCodeStr=bdlCode;
                    }
                }
            }
            if(!InterfaceUtil.isNullString(bdlCodeStr))
                pRequestMap.put("received_bundles",bdlCodeStr.trim());
            else
                pRequestMap.put("received_bundles",bdlCodeStr);
        }
        catch(Exception e)
        {
            if (log.isDebugEnabled()) 
            	log.error(methodName,"Exception e: "+e.getStackTrace());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled())  
            	log.debug(methodName,"Exiting Defined bundles at IN="+pRequestMap.get("received_bundles"));
        }
    }
    /**
     * This Method will parse the Credit Response for received bundles.
     * @param p_responseStr String
     * @param p_requestMap HashMap
     * @throws Exception
     * @return void
     */
    private void getBundlesInfoRcvdFromIN4Credit(TBalDto3[] tBalDto3s, HashMap<String,String> pRequestMap) throws Exception
    {
        String methodName="ZTEOBWRequestResponseFormatter[getBundlesInfoRcvdFromIN4Credit()]";
        if (log.isDebugEnabled())  
        	log.debug(methodName,"Entered ");
        int noOfBundles=0;
        String bdlCode=null;
        String bdlCodeStr=null;
        try
        {
            //Check  whether bundle is present in string or not, if present then split it.
            noOfBundles=tBalDto3s.length;
            if (noOfBundles>0)
            {
                for(int i=0; i<noOfBundles; i++)
                {
                    //get the bdl_name if present in the bucket.
                    if(null!=tBalDto3s[i])
                    {
                        bdlCode=tBalDto3s[i].getAcctResID();
                        if(bdlCode.equalsIgnoreCase("1"))
                        {
                            String bdlBalance=tBalDto3s[i].getBalance();
                            long bdlBalanceLng=Long.parseLong(bdlBalance);
                            bdlBalanceLng=0-(bdlBalanceLng);
                            bdlBalance=String.valueOf(bdlBalanceLng);
                            pRequestMap.put("resp_Balance",bdlBalance);
                            String bdlExpDate=tBalDto3s[i].getExpDate();
                            pRequestMap.put("resp_ExpDate",bdlExpDate);
                            String bdlEffDate=tBalDto3s[i].getEffDate();
                            pRequestMap.put("resp_EffDate",bdlEffDate);
                        }
                        if(!InterfaceUtil.isNullString(bdlCodeStr))
                            bdlCodeStr=bdlCodeStr+","+bdlCode;
                        else
                            bdlCodeStr=bdlCode;
                    }
                }
            }
            if(!InterfaceUtil.isNullString(bdlCodeStr))
                pRequestMap.put("received_bundles",bdlCodeStr.trim());
            else
                pRequestMap.put("received_bundles",bdlCodeStr);
        }
        catch(Exception e)
        {
            if (log.isDebugEnabled())  
            	log.error(methodName,"Exception e: "+e.getMessage());
            throw e;
        }
        finally
        {
            if (log.isDebugEnabled()) 
            	log.debug(methodName,"Exiting Defined bundles at IN="+pRequestMap.get("received_bundles"));
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
    private String getINRequestID(HashMap<String,String> pMap) throws Exception
    {
        String methodName="ZTEOBWRequestResponseFormatter[getINRequestID()]";
        if (log.isDebugEnabled())  
        	log.debug(methodName,"Entered");

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
            serviceType=pMap.get("REQ_SERVICE");
            selectorBundleId=pMap.get("SELECTOR_BUNDLE_ID");
            if(!InterfaceUtil.isNullString(selectorBundleId))
                selectorBundleId=selectorBundleId.trim();
            interfaceID=(pMap.get("INTERFACE_ID")).trim();
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
            pMap.put("IN_REQ_ID",reqId);
            pMap.put("IN_REQ_TIME",dateStrReqTime);

            if (log.isDebugEnabled())  
            	log.debug(methodName,"Exited  id: "+counter+", reqId="+reqId);

            return reqId;
        }
        catch(Exception e)
        {
            if (log.isDebugEnabled())  
            	log.error(methodName,"Exception e::"+e.getStackTrace());
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
