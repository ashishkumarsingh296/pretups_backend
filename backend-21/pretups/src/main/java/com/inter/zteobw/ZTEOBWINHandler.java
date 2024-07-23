package com.inter.zteobw;
/**
 * @(#)ZTEOBWINHandler.java
 * Copyright(c) 2009, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *        Author                                Date                             History
 *-------------------------------------------------------------------------------------------------
 * Diwakar      May 09, 2009              Initial Creation
 * ------------------------------------------------------------------------------------------------
 * Handler class for the interface
 */
import java.net.SocketTimeoutException;
import java.util.HashMap;


import org.apache.axis.AxisFault;

import zsmart.ztesoft.com.service.ObwWebserviceSoap11BindingStub;
import zsmart.ztesoft.com.xsd.TAuthHeader;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.inter.util.InterfaceCloser;
import com.btsl.pretups.inter.util.InterfaceCloserController;
import com.btsl.pretups.inter.util.InterfaceCloserI;
import com.btsl.pretups.inter.util.InterfaceCloserVO;
import com.btsl.pretups.logging.TransactionLog;

public class ZTEOBWINHandler implements InterfaceHandler
{
    private Log log = LogFactory.getLog(ZTEOBWINHandler.class.getName());
    private ZTEOBWRequestResponseFormatter formatter=null;
    private HashMap<String, String> requestMap = null;//Contains the request parameter as key and value pair.
    private HashMap<String, String> responseMap = null;//Contains the response of the request as key and value pair.
    private String interfaceID=null;//Contains the interfaceID
    private String inTXNID=null;//Used to represent the Transaction ID
    private String msisdn=null;//Used to store the MSISDN
    private String referenceID=null;//Used to store the reference of transaction id.
    private String interfaceLiveStatus=null;
    private InterfaceCloserVO interfaceCloserVO= null;
    private InterfaceCloser interfaceCloser=null;
    private boolean isSameRequest=false;
    private String userType=null;
    private String interfaceClosureSupport=null;
    /**
     *  Constructor
     */
    public ZTEOBWINHandler() 
    {
        formatter=new ZTEOBWRequestResponseFormatter();
    }

    /**
     * validate Method is used for getting the account information of sender(CP2P) and receiver(CP2P & RP2P).
     * @param p_map     HashMap
     * @throws BTSLBaseException,Exception
     */
    public void validate(HashMap<String, String> prequestMap) throws BTSLBaseException, Exception
    {
        String methodName="ZTEOBWINHandler[validate()]";
        if(log.isDebugEnabled()) 
        	log.debug(methodName," Entered prequestMap:"+prequestMap);
        requestMap = prequestMap;
        try
        {
            interfaceID=requestMap.get("INTERFACE_ID");
            userType=requestMap.get("USER_TYPE");
            interfaceClosureSupport=FileCache.getValue(interfaceID, "INTFCE_CLSR_SUPPORT");

            if ("Y".equalsIgnoreCase(interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            msisdn=requestMap.get("MSISDN");
            //Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not)
            //If validation of subscriber is not required set the SUCCESS code into request map and return.
            String validateRequired = FileCache.getValue(interfaceID,requestMap.get("REQ_SERVICE")+"_"+requestMap.get("USER_TYPE"));
            if("N".equals(validateRequired))
            {
                requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
                return ;
            }
            inTXNID=getINTransactionID(requestMap);
            requestMap.put("IN_TXN_ID",inTXNID);
            referenceID=requestMap.get("TRANSACTION_ID");

            String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
            if(log.isDebugEnabled()) 
            	log.debug(methodName,"multFactor: "+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor=multFactor.trim();
            //Set the interface parameters into requestMap
            setInterfaceParameters();
            //sending the AccountInfo request to IN along with validate action defined in Huawei84I interface
            sendRequestToIN(ZTEOBWI.ACTION_ACCOUNT_INFO);

            //Check whether the subscriber is black listed on the IN or not.
            String stateSetBits =responseMap.get("resp_stateSet");
            String checkBlackList=FileCache.getValue(interfaceID,"BLACKLIST_CHECK");
            if("Y".equals(checkBlackList))
            {
                char eighthChar = stateSetBits.charAt(7);
                char tenthChar = stateSetBits.charAt(9);
                //If StateSet received from IN is having 8th and 10th bit as 1, the subscriber should not be allowed to recharge.
                if('1'== eighthChar || '1'==tenthChar)
                {
                    if(log.isDebugEnabled())
                    	log.debug(methodName,"Subscriber is blacklisted on IN. Either State Set for eighth bit or tenth bit is 1. "+InterfaceUtil.getPrintMap(requestMap));
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);//The MSISDN of the request is not valid on Prepaid system.
                }
            }


            //set TRANSACTION_STATUS as Success in request map
            requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);

            //get value of BALANCE from response map (BALANCE was set in response map in sendRequestToIN method.)
            String amountStr=responseMap.get("resp_Balance");
            if(log.isDebugEnabled())
            	log.debug("validate "," resp_Balance "+amountStr);
            try
            {
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
                requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
            }
            catch(Exception e)
            {
                log.errorTrace(methodName, e);
                log.error(methodName," Exception e:"+e.getStackTrace());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            requestMap.put("ACCOUNT_STATUS",responseMap.get("resp_state"));
            requestMap.put("SERVICE_CLASS",responseMap.get("resp_serviceClass"));
            //set OLD_EXPIRY_DATE in request map as returned from responseMap.
            requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(responseMap.get("resp_activeStopDate"), "yyyy-MM-dd"));
            requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(responseMap.get("resp_suspendStopDate"), "yyyy-MM-dd"));
            //set the mapping language of our system from FileCache mapping based on the response language.
            setLanguageFromMapping();
        }
        catch (BTSLBaseException be)
        {
            log.error(methodName,"BTSLBaseException be="+be.getMessage());
            if("Y".equalsIgnoreCase(interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                interfaceCloser.updateCountersOnAmbiguousResp(interfaceCloserVO,requestMap);
            
            if(be.getMessage().equals(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND))
            	throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
            throw be;
        }
        catch(Exception ex)
        {
        	log.errorTrace(methodName, ex);
            log.error(methodName,"Exception ex:"+ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,requestMap.get("NETWORK_CODE"), "While validating the subscriber get the Exception ex:"+ex.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if(log.isDebugEnabled())
                log.debug(methodName,"Exiting with  requestMap: "+requestMap);
        }
    }

    /**
     * This method is responsible for the credit of subscriber account.
     *  1.Interface specific parameters are set and added to the request map.
     *  2.Format the request into predefined xml for credit request, method internally calls the generateRequest
     *     method of  ZTEOMLRequestResponseFormatter.
     *  3.For sending request to IN this method internally calls private method sendRequestToIN
     *  4.Process the response.
     * @param   HashMap p_map
     * @throws  BTSLBaseException,Exception
     */
    public void credit(HashMap<String, String> prequestMap) throws BTSLBaseException, Exception
    {
        String methodName="ZTEOBWINHandler[credit()]";
        if (log.isDebugEnabled())
        	log.debug(methodName," Entered prequestMap: " + prequestMap);
        double multFactorDouble=0;
        String amountStr=null;
        String cardGrpSelectorCode=null;
        requestMap = prequestMap;
        double interfaceAmtDouble=0;
        try
        {
            interfaceID=requestMap.get("INTERFACE_ID");
            interfaceClosureSupport=FileCache.getValue(interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            inTXNID=getINTransactionID(requestMap);
            requestMap.put("IN_TXN_ID",inTXNID);
            referenceID=requestMap.get("TRANSACTION_ID");
            msisdn=requestMap.get("MSISDN");
            cardGrpSelectorCode=requestMap.get("CARD_GROUP_SELECTOR");
            //Fetching the MULT_FACTOR from the INFile.
            //While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
            if(log.isDebugEnabled())
            	log.debug(methodName,"multFactor:"+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            requestMap.put("MULT_FACTOR",multFactor);

            //Set the interface parameters into requestMap
            setInterfaceParameters();
            requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
            try
            {
                amountStr=requestMap.get("INTERFACE_AMOUNT");

                if("1".equals(cardGrpSelectorCode))
                {
                    multFactorDouble=Double.parseDouble(multFactor);
                    interfaceAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(amountStr),multFactorDouble);
                }
                else
                {
                    String selectorBundleType=requestMap.get("SELECTOR_BUNDLE_TYPE");
                    if("AMT".equals(selectorBundleType))
                        multFactor=FileCache.getValue(interfaceID,"AMT_MULT_FACTOR");
                    else if("UNIT".equals(selectorBundleType))
                        multFactor=FileCache.getValue(interfaceID,"UNIT_MULT_FACTOR");
                    //Convert the amount in amount according to the selector.
                    interfaceAmtDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(amountStr),Double.parseDouble(multFactor.trim()));
                }
                //Change the amount in to string.
                amountStr=String.valueOf(interfaceAmtDouble);
                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                String roundFlag = FileCache.getValue(interfaceID,"ROUND_FLAG");
                if(log.isDebugEnabled()) 
                	log.debug(methodName," From file cache roundFlag = "+roundFlag);
                //If the ROUND_FLAG is not defined in the INFile
                if(InterfaceUtil.isNullString(roundFlag))
                {
                    roundFlag="Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, methodName,referenceID+" MSISDN = "+msisdn ," INTERFACE ID = "+interfaceID, " Network code = "+ requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                requestMap.put("ROUND_FLAG",roundFlag);
                //If rounding of amount is allowed, round the amount value and put this value in request map.
                if("Y".equals(roundFlag.trim()))
                {
                    amountStr=String.valueOf(Math.round(interfaceAmtDouble));
                    requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                }
            }
            catch(Exception e)
            {
                log.errorTrace(methodName, e);
                log.error(methodName,"Exception e :"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,"REFERENCE ID = "+referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if(log.isDebugEnabled()) 
            	log.debug(methodName," transfer_amount:"+amountStr);
            //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
            requestMap.put("transfer_amount",amountStr);
            //sending the Re-charge request to IN along with re-charge action defined in ZTE interface
            sendRequestToIN(ZTEOBWI.ACTION_RECHARGE_CREDIT);
            //set TRANSACTION_STATUS as Success in request map
            requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
                if("1".equals(cardGrpSelectorCode))
                {
                    String postBalanceStr =  responseMap.get("resp_Balance");
                    postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
                    requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
                }
            }
            catch(Exception e)
            {
            	log.errorTrace(methodName, e);
                log.error(methodName,"Exception e :"+ e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if(("1").equals(cardGrpSelectorCode))
            {
                //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
                requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString( responseMap.get("resp_ExpDate"), "yyyy-MM-dd"));
                //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
            }
            else
                requestMap.put("POST_BALANCE_ENQ_SUCCESS","N");
        }
        catch (BTSLBaseException be)
        {
            prequestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            log.error(methodName," BTSLBaseException be:"+be.getMessage());
            if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try
            {
                if ("Y".equalsIgnoreCase(interfaceClosureSupport))
                    interfaceCloser.updateCountersOnAmbiguousResp(interfaceCloserVO,requestMap);
                requestMap.put("TRANSACTION_TYPE","CR");
                handleCancelTransaction();
            }
            catch(BTSLBaseException bte)
            {
                throw bte;
            }
            catch(Exception e)
            {
            	log.errorTrace(methodName, e);
            	log.error(methodName,"Exception e : "+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, referenceID,msisdn,  requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }
        catch (Exception e)
        {
        	log.errorTrace(methodName, e);
            log.error(methodName, "Exception e :" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn,  requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (log.isDebugEnabled())
            	log.debug(methodName, " Exited requestMap=" + requestMap);
        }
    }

    /** This method is used to credit back the sender in case of ambiguous case.
     * @param p_map HashMap
     * @throws BTSLBaseException, Exception
     */
    public void creditAdjust(HashMap<String,String> prequestMap) throws BTSLBaseException, Exception
    {
        String methodName="ZTEOBWINHandler[creditAdjust()]";
        if (log.isDebugEnabled())
        	log.debug(methodName,"Entered prequestMap: " + prequestMap);
        double interfaceAmtDouble=0;
        double multFactorDouble=0;
        String amountStr=null;
        String cardGrpSelectorCode=null;
        requestMap = prequestMap;
        try
        {
            interfaceID=requestMap.get("INTERFACE_ID");
            interfaceClosureSupport=FileCache.getValue(interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            inTXNID=getINTransactionID(requestMap);
            requestMap.put("IN_TXN_ID",inTXNID);
            referenceID=requestMap.get("TRANSACTION_ID");
            msisdn=requestMap.get("MSISDN");
            cardGrpSelectorCode=requestMap.get("CARD_GROUP_SELECTOR");
            //Fetching the MULT_FACTOR from the INFile.
            //While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
            if(log.isDebugEnabled())
            	log.debug(methodName,"multFactor: "+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            requestMap.put("MULT_FACTOR",multFactor);

            //Set the interface parameters into requestMap
            setInterfaceParameters();
            requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
            try
            {
                amountStr=requestMap.get("INTERFACE_AMOUNT");
                interfaceAmtDouble=Double.parseDouble(amountStr);
                if("1".equals(cardGrpSelectorCode))
                {
                    multFactorDouble=Double.parseDouble(multFactor);
                    interfaceAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
                }
                else
                {
                    String selectorBundleType=requestMap.get("SELECTOR_BUNDLE_TYPE");
                    if(!InterfaceUtil.isNullString(selectorBundleType))
                    {
                        if("AMT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(interfaceID,"AMT_MULT_FACTOR");
                        else if("UNIT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(interfaceID,"UNIT_MULT_FACTOR");
                        //Convert the amount in amount according to the selector.
                        interfaceAmtDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,Double.parseDouble(multFactor.trim()));
                    }
                }
                //Change the amount in to string.
                amountStr=String.valueOf(interfaceAmtDouble);
                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                String roundFlag = FileCache.getValue(interfaceID,"ROUND_FLAG");
                if(log.isDebugEnabled())
                	log.debug(methodName,"From file cache roundFlag = "+roundFlag);
                //If the ROUND_FLAG is not defined in the INFile
                if(InterfaceUtil.isNullString(roundFlag))
                {
                    roundFlag="N";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, methodName,referenceID+"MSISDN = "+msisdn ," INTERFACE ID = "+interfaceID, "Network code = "+ requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                //If rounding of amount is allowed, round the amount value and put this value in request map.
                if("Y".equals(roundFlag.trim()))
                {
                    amountStr=String.valueOf(Math.round(interfaceAmtDouble));
                    requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                }
            }
            catch(Exception e)
            {
            	log.errorTrace(methodName, e);
                log.error(methodName," Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,"REFERENCE ID = "+referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if(log.isDebugEnabled())
                log.debug(methodName,"transfer_amount:"+amountStr);
            //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
            requestMap.put("transfer_amount",amountStr);
            //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
            sendRequestToIN(ZTEOBWI.ACTION_TXN_CANCEL);
            //set TRANSACTION_STATUS as Success in request map
            requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
                if("1".equals(cardGrpSelectorCode))
                {
                    String postBalanceStr =  responseMap.get("resp_Balance");
                    postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
                    requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
                }
            }
            catch(Exception e)
            {
                log.error(methodName,"Exception  e:"+e.getMessage());
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if(("1").equals(cardGrpSelectorCode))
            {
                //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
                requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString( responseMap.get("resp_ExpDate"), "yyyy-MM-dd"));
                //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
            }
            else
            {
                requestMap.put("POST_BALANCE_ENQ_SUCCESS","N");
            }
        }
        catch (BTSLBaseException be)
        {
            prequestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            log.error(methodName,"BTSLBaseException be:"+be.getMessage());
            if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try
            {
                if ("Y".equalsIgnoreCase(interfaceClosureSupport))
                    interfaceCloser.updateCountersOnAmbiguousResp(interfaceCloserVO,requestMap);
                requestMap.put("TRANSACTION_TYPE","CR");
                handleCancelTransaction();
            }
            catch(BTSLBaseException bte)
            {
                throw bte;
            }
            catch(Exception e)
            {
            	log.errorTrace(methodName, e);
                log.error(methodName,"Exception  e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, referenceID,msisdn,  requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }
        catch (Exception e)
        {
        	log.errorTrace(methodName, e);
            log.error(methodName, "Exception e:  " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn,  requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited requestMap=" + requestMap);
        }
    }

    /**
     * This method is used to debit the sender account in CP2P request.
     * @param p_map HashMap
     * @throws Exception
     */
    public void debitAdjust(HashMap<String,String> prequestMap) throws BTSLBaseException, Exception
    {
        String methodName="ZTEOBWINHandler[debitAdjust()]";
        if (log.isDebugEnabled())
        	log.debug(methodName,"Entered prequestMap: " + prequestMap);
        double interfaceAmtDouble=0;
        double multFactorDouble=0;
        String amountStr=null;
        requestMap = prequestMap;
        String cardGrpSelectorCode=null;

        try
        {
            interfaceID=requestMap.get("INTERFACE_ID");
            interfaceClosureSupport=FileCache.getValue(interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            inTXNID=getINTransactionID(requestMap);
            requestMap.put("IN_TXN_ID",inTXNID);
            referenceID=requestMap.get("TRANSACTION_ID");
            msisdn=requestMap.get("MSISDN");
            cardGrpSelectorCode=requestMap.get("CARD_GROUP_SELECTOR");

            //Fetching the MULT_FACTOR from the INFile.
            //While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(interfaceID,"MULT_FACTOR");
            if(log.isDebugEnabled())
            	log.debug(methodName,"multFactor:"+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            requestMap.put("MULT_FACTOR",multFactor);

            //Set the interface parameters into requestMap
            setInterfaceParameters();
            requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try
            {
                amountStr=requestMap.get("INTERFACE_AMOUNT");
                multFactorDouble=Double.parseDouble(multFactor);
                interfaceAmtDouble=Double.parseDouble(amountStr);
                if("1".equals(cardGrpSelectorCode))
                    interfaceAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
                else
                {
                    String selectorBundleType=requestMap.get("SELECTOR_BUNDLE_TYPE");
                    if(!InterfaceUtil.isNullString(selectorBundleType))
                    {
                        if("AMT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(interfaceID,"AMT_MULT_FACTOR");
                        else if("UNIT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(interfaceID,"UNIT_MULT_FACTOR");
                        //Convert the amount in amount according to the selector.
                        interfaceAmtDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,Double.parseDouble(multFactor.trim()));
                    }
                }
                //Change the amount in to string.
                amountStr=String.valueOf(interfaceAmtDouble);

                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                String roundFlag = FileCache.getValue(interfaceID,"ROUND_FLAG");
                if(log.isDebugEnabled()) 
                	log.debug(methodName,"From file cache roundFlag = "+roundFlag);
                //If the ROUND_FLAG is not defined in the INFile
                if(InterfaceUtil.isNullString(roundFlag))
                {
                    roundFlag="Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, methodName,referenceID+"MSISDN = "+msisdn ," INTERFACE ID = "+interfaceID, "Network code = "+ requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                //If rounding of amount is allowed, round the amount value and put this value in request map.
                if("Y".equals(roundFlag.trim()))
                {
                    amountStr=String.valueOf(Math.round(interfaceAmtDouble));
                    requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                }

                //Calculate the Access Fee and Tax
                String segregAccessTax=FileCache.getValue(interfaceID,"SEGREGATE_ACCESS_FEE_AND_TAX");
                if(!InterfaceUtil.isNullString(segregAccessTax) && "Y".equals(segregAccessTax))
                {
                    String accessFeeStr=requestMap.get("ACCESS_FEE");
                    String taxStr=requestMap.get("TAX_AMOUNT");
                    double accDouble=0;
                    double taxDouble=0;
                    accDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(accessFeeStr),Double.parseDouble(multFactor));
                    taxDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(taxStr),Double.parseDouble(multFactor));

                    //Subtract the access fee and tax from the transfer amount
                    interfaceAmtDouble=interfaceAmtDouble-accDouble-taxDouble;

                    //If rounding of amount is allowed, round the amount value and put this value in request map.
                    if("Y".equals(roundFlag))
                    {
                        accessFeeStr=String.valueOf(Math.round(accDouble));
                        taxStr=String.valueOf(Math.round(taxDouble));
                        amountStr=String.valueOf(Math.round(interfaceAmtDouble));
                    }
                    else
                    {
                        accessFeeStr=String.valueOf(accDouble);
                        taxStr=String.valueOf(taxDouble);
                        amountStr=String.valueOf(interfaceAmtDouble);
                    }

                    requestMap.put("access_fee",accessFeeStr);
                    requestMap.put("tax_amount",taxStr);
                }
            }
            catch(Exception e)
            {
            	log.errorTrace(methodName, e);
                log.error(methodName,"Exception e: "+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,"REFERENCE ID = "+referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if(log.isDebugEnabled()) 
            	log.debug(methodName,"transfer_amount:"+amountStr);
            //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
            requestMap.put("transfer_amount",amountStr);

            //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
            sendRequestToIN(ZTEOBWI.ACTION_IMMEDIATE_DEBIT);
            //set TRANSACTION_STATUS as Success in request map
            requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
                String postBalanceStr =  responseMap.get("resp_Balance");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
                requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
            }
            catch(Exception e)
            {
                log.error(methodName,"Exception e: "+e.getMessage());
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn +" INTERFACE ID = "+interfaceID,  requestMap.get("NETWORK_CODE"), "balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
            requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString( responseMap.get("resp_ExpDate"), "yyyy-MM-dd"));
            //Get the value of end_inact_date from the response, change its format as per the interface and set it into
            //requestMap with key as NEW_GRACE_DATE
            //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
            requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
        }
        catch (BTSLBaseException be)
        {
            prequestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            log.error(methodName,"BTSLBaseException be:"+be.getMessage());
            if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try
            {
                if ("Y".equalsIgnoreCase(interfaceClosureSupport))
                    interfaceCloser.updateCountersOnAmbiguousResp(interfaceCloserVO,requestMap);
                requestMap.put("TRANSACTION_TYPE","CR");
                handleCancelTransaction();
            }
            catch(BTSLBaseException bte)
            {
                throw bte;
            }
            catch(Exception e)
            {
            	log.errorTrace(methodName, e);
                log.error(methodName,"Exception e :"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, referenceID,msisdn,  requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
        }
        catch (Exception e)
        {
        	log.errorTrace(methodName, e);
            log.error(methodName, "Exception e: " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,msisdn,  requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited requestMap=" + requestMap);
        }
    }

    /**
     * This method would be used to adjust the validity of the subscriber account at the IN.
     * @param   HashMap prequestMap
     * @throws  BTSLBaseException, Exception
     */
    public void validityAdjust(HashMap<String,String> prequestMap) throws BTSLBaseException, Exception
    {}//end of validityAdjust
    /**
     * This method used to send the request to the IN
     * @param pAction
     * @throws BTSLBaseException
     */
    public void sendRequestToIN(int pAction) throws BTSLBaseException
    {
        String methodName="ZTEOBWINHandler[sendRequestToIN()]";
        if(log.isDebugEnabled())
        	log.debug(methodName," pAction="+pAction);

        long startTime=0;
        long endTime=0;
        long warnTime=0;

        ZTEOBWWebServiceConnector connector =null;
        ObwWebserviceSoap11BindingStub stub=null;

        TQueryProfileAndBalRequest queryProfileAndBalReq=null;
        TModifyAllBalReturnAllBalRequest modifyAllBalReturnAllBalReq=null;
        
        TQueryProfileAndBalResponse queryProfileAndBalResp=null;
        TModifyAllBalReturnAllBalResponse modifyAllBalReturnAllBalResponse=null;
        
        if(!InterfaceUtil.isNullString(msisdn))
            msisdn=InterfaceUtil.getFilterMSISDN(interfaceID,msisdn);
        try
        {
            connector=new ZTEOBWWebServiceConnector(interfaceID);
            stub=connector.getStubConnection();
            if(stub==null)
            {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+" MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction,"Unable to get Validate Object");
                log.error(methodName,"Unable to get Client Object, port= ");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
            }
        	
            requestMap.put("ACTION", String.valueOf(pAction));
            try
            {
                try
                {
                    startTime=System.currentTimeMillis();
                    requestMap.put("IN_START_TIME",String.valueOf(startTime));
                    switch(pAction)
                    {
                    case ZTEOBWI.ACTION_ACCOUNT_INFO: 
                    {
                        queryProfileAndBalReq=formatter.generateTQueryProfileAndBalRequest(requestMap);
                        if(queryProfileAndBalReq==null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        TransactionLog.log(interfaceID,referenceID,msisdn,requestMap.get("NETWORK_CODE"),String.valueOf(pAction),PretupsI.TXN_LOG_REQTYPE_REQ,"requestMap="+requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,", action="+pAction );
                        try
                        {
                            queryProfileAndBalResp=stub.queryProfileAndBal(queryProfileAndBalReq, generateTAuthHeaderObject());
                        }
                        catch(AxisFault axisFaultException)
                        {
                        	System.out.println("axisFaultException: "+axisFaultException.getFaultCode());
                        	if(axisFaultException.getFaultCode().toString().contains(ZTEOBWI.MSISDN_NOT_FOUND))
                        		throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                        	log.errorTrace(methodName, axisFaultException);
                            log.error(methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn+"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction+"Exception Error Message :"+axisFaultException.getFaultReason()+" Error Code:"+axisFaultException.getFaultCode());
                            requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.VALIDATION_ERROR);
                            throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATION_ERROR);
                        }

                        if(queryProfileAndBalResp==null)
                        {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction,"Response object found null");
                            log.error(methodName," Response object is null");
                            requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.VALIDATION_ERROR);
                            throw new BTSLBaseException(InterfaceErrorCodesI.VALIDATION_ERROR);
                        }
                        break;	
                    }
                    case ZTEOBWI.ACTION_RECHARGE_CREDIT: 
                    {
                    	modifyAllBalReturnAllBalReq=formatter.generateRechargingRequest(requestMap);
                            if(modifyAllBalReturnAllBalReq==null)
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            try
                            {
                            	modifyAllBalReturnAllBalResponse=stub.modifyAllBalReturnAllBal(modifyAllBalReturnAllBalReq, generateTAuthHeaderObject());
                            }
                            catch(AxisFault axisFaultException)
                            {
                            	log.errorTrace(methodName, axisFaultException);
                                log.error(methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn+"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction+"Exception Error Message :"+axisFaultException.getFaultReason()+" Error Code:"+axisFaultException.getFaultCode());
                                requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            }
                            if(modifyAllBalReturnAllBalResponse==null)
                            {
                                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction,"Response object found null");
                                log.error(methodName,"Response object is null");
                                requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                            }
                        }
                        TransactionLog.log(interfaceID,referenceID,msisdn,requestMap.get("NETWORK_CODE"),String.valueOf(pAction),PretupsI.TXN_LOG_REQTYPE_REQ,"requestMap="+requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,", action="+pAction );
                        break;	
                    case ZTEOBWI.ACTION_IMMEDIATE_DEBIT: 
                    {
                    	modifyAllBalReturnAllBalReq=formatter.generateDebitRequest(requestMap);
                        if(modifyAllBalReturnAllBalReq==null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

                        TransactionLog.log(interfaceID,referenceID,msisdn,requestMap.get("NETWORK_CODE"),String.valueOf(pAction),PretupsI.TXN_LOG_REQTYPE_REQ,"requestMap="+requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,", action="+pAction );
                        try
                        {
                        	modifyAllBalReturnAllBalResponse=stub.modifyAllBalReturnAllBal(modifyAllBalReturnAllBalReq, generateTAuthHeaderObject());
                        }catch(AxisFault axisFaultException)
                        {
                            log.error(methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn+"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction+"Exception Error Message :"+axisFaultException.getFaultReason()+" Error Code:"+axisFaultException.getFaultCode());
                            requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }

                        if(modifyAllBalReturnAllBalResponse==null)
                        {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction,"Response object found null");
                            log.error(methodName,"Response object is null");
                            requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        break;	
                    }
                    case ZTEOBWI.ACTION_TXN_CANCEL:
                    {
                    	modifyAllBalReturnAllBalReq=formatter.generateRefundRequest(requestMap);
                        if(modifyAllBalReturnAllBalReq==null)
                            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);

                        TransactionLog.log(interfaceID,referenceID,msisdn,requestMap.get("NETWORK_CODE"),String.valueOf(pAction),PretupsI.TXN_LOG_REQTYPE_REQ,"requestMap="+requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,", action="+pAction );
                        try
                        {
                        	modifyAllBalReturnAllBalResponse=stub.modifyAllBalReturnAllBal(modifyAllBalReturnAllBalReq, generateTAuthHeaderObject());
                        }
                        catch(AxisFault axisFaultException)
                        {
                        	log.errorTrace(methodName, axisFaultException);
                            log.error(methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn+"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction+"Exception Error Message :"+axisFaultException.getFaultReason()+" Error Code:"+axisFaultException.getFaultCode());
                            requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }

                        if(modifyAllBalReturnAllBalResponse==null)
                        {
                            EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction,"Response object found null");
                            log.error(methodName,"Response object is null");
                            requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                            throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                        }
                        break;  
                    }
                    }
                }
                catch(java.rmi.RemoteException re)
                {
                    re.printStackTrace();
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction," RemoteException Error Message:"+re.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                catch(SocketTimeoutException se)
                {
                	log.errorTrace(methodName, se);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction,"SocketTimeoutException Error Message:"+se.getMessage());
                    log.error(methodName,"SocketTimeoutException Error Message :"+se.getMessage());			    	
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                catch(Exception e)
                {
                	
                	log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action ="+pAction,"Exception Error Message:"+e.getMessage());
                    System.out.println("CATCH BLOCK: "+e.getMessage());
                    if(e.getMessage().contains(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND))
                    		throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                    log.error(methodName,"Exception Error Message :"+e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }
                endTime=System.currentTimeMillis();
            }
            catch(BTSLBaseException be)
            {
            	System.out.println("BTSLBASE EXCEPTION: "+be.getMessage());
            	if(be.getMessage().contains(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND))
            		throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                throw be;
            }
            catch(Exception e)
            {
            	log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action="+pAction,"Error Message:"+e.getMessage());
                log.error(methodName,"Error Message :"+e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            finally
            {
                if(endTime==0) 
                    endTime=System.currentTimeMillis();
                requestMap.put("IN_END_TIME",String.valueOf(endTime));	
                try 
                {
                	System.out.println("Request sent to IN :"+stub._getCall().getMessageContext().getRequestMessage().getSOAPPartAsString());
                }
                catch(Exception e)
                {
                	log.errorTrace(methodName, e);
                }
            	  try 
            	  {
            		  System.out.println("Response received from IN:"+stub._getCall().getMessageContext().getResponseMessage().getSOAPPartAsString());
            	  }
            	  catch(Exception e)
            	  {
            		  log.errorTrace(methodName, e);
            	  }

                log.error(methodName,"Request sent to IN at="+startTime+" Response received from IN at="+endTime+", Time taken by IN="+(endTime-startTime));
            }
            warnTime=Long.parseLong(requestMap.get("WARN_TIMEOUT"));
            if(endTime-startTime>=warnTime)
                log.info(methodName, "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));

            //Construct the response map from the response object received.
            responseMap=formatter.parseResponseObject(pAction,queryProfileAndBalResp,modifyAllBalReturnAllBalResponse);
            //put value of response
            TransactionLog.log( interfaceID,referenceID,msisdn,requestMap.get("NETWORK_CODE"),String.valueOf(pAction),PretupsI.TXN_LOG_REQTYPE_RES," Response Map="+responseMap ,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+pAction+", Time taken by IN="+(endTime-startTime));
            //Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled

            String status=responseMap.get("resp_returnCode");
            requestMap.put("INTERFACE_STATUS",status);
            if("Y".equalsIgnoreCase(interfaceClosureSupport) && !("Y".equals(requestMap.get("ADJUST")) && "C".equals(requestMap.get("INTERFACE_ACTION")) && "S".equals(userType)))
            {
                if(interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    interfaceCloser.resetCounters(interfaceCloserVO,requestMap);  
                interfaceCloser.updateCountersOnSuccessResp(interfaceCloserVO); 
            }
        }
        catch(BTSLBaseException be)
        {
            log.error(methodName,"BTSLBaseException be = "+be.getMessage());
            throw be;
        }
        catch(Exception e)
        {
            log.error(methodName,"Exception="+e.getStackTrace());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn,"INTERFACE ID = "+interfaceID,"Network code = "+requestMap.get("NETWORK_CODE")+" Action = "+pAction,"System Exception="+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            stub=null;
            if(log.isDebugEnabled()) 
                log.debug(methodName," Exiting pAction="+pAction);
        }
    }

    public TAuthHeader generateTAuthHeaderObject()
    {
    	  String methodName="ZTEOBWINHandler[generateTAuthHeaderObject()]";
    	  log.debug(methodName," Entered");
    	  String username = "";
          String password = "";
          String requestID = "";
    	  username = FileCache.getValue(interfaceID,"USER_NAME");
          password = FileCache.getValue(interfaceID,"PASSWORD");
          requestID = FileCache.getValue(interfaceID, "REQUEST_ID");
    	  TAuthHeader authHeader =new TAuthHeader();
          authHeader.setUsername(username);
          authHeader.setPassword(password);
          authHeader.setRequestID(requestID);
        log.debug(methodName," Exited");
        
        return authHeader;

    }

    /**
     * Method to Check interface status before sending request.
     * @throws  BTSLBaseException
     */
    private void checkInterfaceB4SendingRequest() throws BTSLBaseException
    {
        String methodName="ZTEOBWINHandler[checkInterfaceB4SendingRequest()]";
        log.debug(methodName," Entered");
        try
        {
            interfaceCloserVO=(InterfaceCloserVO)InterfaceCloserController._interfaceCloserVOTable.get(interfaceID);
            interfaceLiveStatus=requestMap.get("INT_ST_TYPE");
            interfaceCloserVO.setControllerIntStatus(interfaceLiveStatus);
            interfaceCloser=interfaceCloserVO.getInterfaceCloser();

            //Get AUTO_RESUME_SUPPORT property from IN FILE. If it is not defined then set it as 'N'.
            String autoResumeSupported = FileCache.getValue(interfaceID,"AUTO_RESUME_SUPPORT");
            if(InterfaceUtil.isNullString(autoResumeSupported))
            {
                autoResumeSupported="N";
                log.error(methodName,"Value of AUTO_RESUME_SUPPORT is not defined in the INFile");
            }
            //If Controller sends 'A' and interface status is suspended, expiry is checked.
            //If Controller sends 'M', request is forwarded to IN after resetting counters.
            if(InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(interfaceLiveStatus)&& interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
            {
                //Check if Auto Resume is supported by IN or not.If not then throw exception. request would not be sent to IN.
                if("N".equals(autoResumeSupported))
                {
                    log.error(methodName,"Interface Suspended.");
                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_SUSPENDED);
                }
                //If "Auto Resume" is supported then only check the expiry of interface, if expired then only request would be sent to IN
                //otherwise checkExpiry method throws exception
                if(isSameRequest)
                    interfaceCloser.checkExpiryWithoutExpiryFlag(interfaceCloserVO);
                else
                    interfaceCloser.checkExpiry(interfaceCloserVO);
            }
            //this block is executed when Interface is manually resumed (Controller sends 'M')from suspend state
            else if(InterfaceCloserI.INTERFACE_MANNUAL_ACTIVE.equals(interfaceCloserVO.getControllerIntStatus()) && interfaceCloserVO.getFirstSuspendAt()!=0)
                interfaceCloser.resetCounters(interfaceCloserVO,null);
        }
        catch(BTSLBaseException be)
        {
            throw be;
        }
        catch(Exception e)
        {
        	log.errorTrace(methodName, e);
            log.error(methodName,"Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if(log.isDebugEnabled())
                log.debug(methodName,"Exited");
        }
    }
    /**
     * This method is used to get interface specific values from FileCache(load at starting)based on
     * interface id and set to the requested map.These parameters are
     *  1.cp_id
     *  2.application
     *  3.transaction_currency
     * @throws  BTSLBaseException, Exception
     */
    public void setInterfaceParameters() throws BTSLBaseException,Exception
    {
        String methodName="ZTEOBWINHandler[setInterfaceParameters()]";
        log.debug(methodName,"Entered: ");
        try
        {
            String cancelTxnAllowed = FileCache.getValue(interfaceID,"CANCEL_TXN_ALLOWED");
            if(InterfaceUtil.isNullString(cancelTxnAllowed))
            {
                log.error(methodName,"Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID," INTERFACE ID"+interfaceID+" MSISDN: "+msisdn ,  requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
            if(InterfaceUtil.isNullString(systemStatusMappingCr))
            {
                log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID: "+interfaceID+" MSISDN: "+msisdn ,  requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
            {
                log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID:"+interfaceID+" MSISDN: "+msisdn ,  requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
            {
                log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID," INTERFACE ID: "+interfaceID+" MSISDN:"+msisdn ,  requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
            {
                log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID : "+interfaceID+" MSISDN : "+msisdn ,  requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
            if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
            {
                log.error(methodName,"Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID:"+interfaceID+" MSISDN : "+msisdn ,  requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());


            String cancelNA = FileCache.getValue(interfaceID,"CANCEL_NA");
            if(InterfaceUtil.isNullString(cancelNA))
            {
                log.error(methodName,"Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("CANCEL_NA",cancelNA.trim());

            String userId = FileCache.getValue(interfaceID,"USER_NAME");
            if(InterfaceUtil.isNullString(userId))
            {
                log.error(methodName,"Value of USER_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "USER_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("USER_NAME",userId.trim());

            String passwd = FileCache.getValue(interfaceID,"PASSWORD");
            if(InterfaceUtil.isNullString(passwd))
            {
                log.error(methodName,"Value of PASSWORD is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("PASSWORD",passwd.trim());

            String authHeader = FileCache.getValue(interfaceID,"AUTH_HEADER");
            if(InterfaceUtil.isNullString(authHeader))
            {
                log.error(methodName,"Value of AUTH_HEADER is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "AUTH_HEADER is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("AUTH_HEADER",authHeader.trim());

            String zteNameSpace = FileCache.getValue(interfaceID,"ZTE_NAMESPACE");
            if(InterfaceUtil.isNullString(zteNameSpace))
            {
                log.error(methodName,"Value of SAFCOM_TERM_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "ZTE_NAMESPACE is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("ZTE_NAMESPACE",zteNameSpace.trim());

            String uri = FileCache.getValue(interfaceID,"SOAP_ACTION_URI");
            if(InterfaceUtil.isNullString(uri))
            {
                log.error(methodName,"Value of SOAP_ACTION_URI is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "SOAP_ACTION_URI is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("SOAP_ACTION_URI",uri.trim());

            String endPt = FileCache.getValue(interfaceID,"END_POINT");
            if(InterfaceUtil.isNullString(endPt))
            {
                log.error(methodName,"Value of END_POINT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("END_POINT",endPt.trim());

            String operationName = FileCache.getValue(interfaceID,"OPERATION_NAME");
            if(InterfaceUtil.isNullString(operationName))
            {
                log.error(methodName,"Value of OPERATION_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "OPERATION_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("OPERATION_NAME",operationName.trim());

            String warnTimeStr=FileCache.getValue(interfaceID, "WARN_TIMEOUT");
            if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
            {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,"REFERENCE ID = "+referenceID+"MSISDN = "+msisdn," INTERFACE ID = "+interfaceID, "Network code "+ requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());

            //Channel ID as per the service and domain code.
            String channelId =null;
            String module=requestMap.get("MODULE");

            if(PretupsI.C2S_MODULE.equals(module))
            {
                String domainCode=requestMap.get("DOMAIN_CODE");
                if(!InterfaceUtil.isNullString(domainCode))
                    channelId=FileCache.getValue(interfaceID,domainCode+"_CHANNEL_ID");
            }

            //If specific channel ID is not defined, then set the default for the service.
            if(InterfaceUtil.isNullString(channelId))
            {
                channelId=FileCache.getValue(interfaceID,module+"_CHANNEL_ID");
                if(InterfaceUtil.isNullString(channelId))
                {
                    log.error(methodName,"Value of "+module+"_CHANNEL_ID is not defined in the INFile.");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, methodName,referenceID,"INTERFACE ID"+interfaceID+" MSISDN "+msisdn ,  requestMap.get("NETWORK_CODE"), "CHANNEL_ID is not defined in the INFile for module="+module);
                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                }
            }
            requestMap.put("CHANNEL_ID",channelId.trim());

        }//end of try block
        catch(BTSLBaseException be)
        {
            throw be;
        }
        catch(Exception e)
        {
            log.error(methodName,"Exception e="+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            log.debug(methodName, "Exited requestMap:" + requestMap);
        }//end of finally
    }
    /**
     * This method used to get the system language mapped in FileCache based on the INLanguge.Includes following
     * If the Mapping key not defined in IN file handle the event as System Error with level FATAL.
     * If the Mapping is not defined handle the event as SYSTEM INFO with level MAJOR and set empty string.
     * @throws Exception
     */
    private void setLanguageFromMapping() throws Exception
    {
        String methodName="ZTEOBWINHandler[setLanguageFromMapping()]";
        if(log.isDebugEnabled())  
        	log.debug("setLanguageFromMapping","Entered");
        String mappedLang="";
        String[] mappingArr;
        String[] tempArr;
        boolean mappingNotFound = true;//Flag defines whether the mapping of language is found or not.
        String langFromIN = null;
        String mappingString = null;
        try
        {
            //Get the mapping string from the FileCache and storing all the mappings into array which are separated by ','.
            mappingString=FileCache.getValue(interfaceID, "LANGUAGE_MAPPING");
            if(InterfaceUtil.isNullString(mappingString))
                mappingString="";
            langFromIN = responseMap.get("resp_defLang");
            if(log.isDebugEnabled())
                log.debug(methodName,"mappingString = "+mappingString +" langFromIN = "+langFromIN);
            mappingArr = mappingString.split(",");
            //Iterating the mapping array to map the IN language from the system language,if found break the loop.
            for(int in=0,length=mappingArr.length;in<length;in++)
            {
                tempArr = mappingArr[in].split(":");
                if(tempArr[0].equals(langFromIN))
                {
                    mappedLang = tempArr[1];
                    mappingNotFound=false;
                    break;
                }
            }//end of for loop
            //if the mapping of IN language with our system is not found,handle the event
            if(mappingNotFound)
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, methodName,referenceID, msisdn,  requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  "+langFromIN +" is not defined in IN file Hence setting the Default language");
            //Set the mapped language to the requested map with key as IN_LANGUAGE.
            requestMap.put("IN_LANG",mappedLang);
        }//end of try
        catch(Exception e)
        {
        	log.errorTrace(methodName, e);
            log.error(methodName,"Exception e="+e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, methodName,referenceID, msisdn,  requestMap.get("NETWORK_CODE"), "while setting the language mapping get Exception=" + e.getMessage());
        }//end of catch-Exception
        finally
        {
            if(log.isDebugEnabled()) 
            	log.debug(methodName,"Exited mappedLang ="+mappedLang);
        }//end of finally setLanguageFromMapping
    }//end of setLanguageFromMapping
    /**
     * Method to send cancel request to IN for any ambiguous transaction.
     * This method also makes reconciliation log entry.
     * @throws  BTSLBaseException
     */
    private void handleCancelTransaction() throws BTSLBaseException
    {
        String methodName="ZTEOBWINHandler[handleCancelTransaction()]";
        if (log.isDebugEnabled())
            log.debug("handleCancelTransaction", "Entered.");
        String cancelTxnAllowed = null;
        String cancelTxnStatus = null;
        String reconciliationLogStr = null;
        String cancelCommandStatus=null;
        String cancelNA=null;
        String interfaceStatus=null;
        Log reconLog = null;
        String systemStatusMapping=null;
        try
        {
            requestMap.put("REMARK1",FileCache.getValue(interfaceID,"REMARK1"));
            requestMap.put("REMARK2",FileCache.getValue(interfaceID,"REMARK2"));
            //get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(interfaceID);
            if (log.isDebugEnabled())
            	log.debug(methodName, "reconLog."+reconLog);
            cancelTxnAllowed=requestMap.get("CANCEL_TXN_ALLOWED");
            //if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
            //into reconciliation log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
            if("N".equals(cancelTxnAllowed))
            {
                cancelNA=requestMap.get("CANCEL_NA");//Cancel command status as NA.
                cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(requestMap,cancelNA,"CANCEL_COMMAND_STATUS_MAPPING");
                requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);
                interfaceStatus=requestMap.get("INTERFACE_STATUS");
                systemStatusMapping=requestMap.get("SYSTEM_STATUS_MAPPING");
                cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING

                requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
                reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(requestMap);
                reconLog.info("",reconciliationLogStr);
                if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
                    throw new BTSLBaseException(this,methodName,cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.     ??????)
                requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
                //added to discard amount field from the message.
                requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }
        }
        catch(BTSLBaseException be)
        {
            throw be;
        }
        catch(Exception e)
        {
        	log.errorTrace(methodName, e);
            log.error(methodName,"Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exited");
        }
    }
    /**
     * This Method will generate the IN Transaction ID for each request.
     * @param p_map HashMap
     * @return inTxnId String
     */
    protected String getINTransactionID(HashMap<String,String> p_map)
    {
        String methodName="ZTEOBWINHandler[getINTransactionID()]";
        if(log.isDebugEnabled())
        	log.debug(methodName,"Entered");
        //Get the USER_TYPE and TRANSACTION_ID from the map.
        String inReconID=null;
        String userType1=p_map.get("USER_TYPE");
        String inTxnId=p_map.get("TRANSACTION_ID");


        //If USER_TYPE is not null, append it in the transaction id.
        if(!InterfaceUtil.isNullString(userType1))
            inReconID=inTxnId+"."+userType1.trim();

        //Put the transaction id into the map.
        p_map.put("IN_RECON_ID",inReconID);

        if(log.isDebugEnabled())
            log.debug(methodName,"Exiting with IN_RECON_ID and IN_TXN_ID="+inTxnId);
        return inTxnId;
    }
}

