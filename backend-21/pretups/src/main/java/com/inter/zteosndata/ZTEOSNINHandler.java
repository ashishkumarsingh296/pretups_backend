package com.inter.zteosndata;
/**
 * @(#)ZTEOSNINHandler.java
 * Copyright(c) 2011, Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * 	  Author				Date				 History
 *-------------------------------------------------------------------------------------------------
 * Vikas Jauhari        Dec 16, 2011		    Initial Creation
 * ------------------------------------------------------------------------------------------------
 * Handler class for the interface
 */
import java.util.HashMap;
import org.apache.axis.client.Call;
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
import java.text.DecimalFormat;

public class ZTEOSNINHandler implements InterfaceHandler
{
    private Log _log = LogFactory.getLog(ZTEOSNINHandler.class.getName());
    private ZTEOSNReqResFormatter _formatter=null;
    private HashMap<String, String> _requestMap = null;//Contains the request parameter as key and value pair.
    private HashMap<String, String> _responseMap = null;//Contains the response of the request as key and value pair.
    private String _interfaceID=null;//Contains the interfaceID
    private String _inTXNID=null;//Used to represent the Transaction ID
    private String _msisdn=null;//Used to store the MSISDN
    private String _referenceID=null;//Used to store the reference of transaction id.
    private String _interfaceLiveStatus=null;
    private InterfaceCloserVO _interfaceCloserVO= null;
    private InterfaceCloser _interfaceCloser=null;
    private boolean _isSameRequest=false;
    private String _userType=null;
    private String _interfaceClosureSupport=null;
    /**
     *  Constructor
     */
    public ZTEOSNINHandler() 
    {
        _formatter=new ZTEOSNReqResFormatter();
        // TODO Auto-generated constructor stub
    }
    /**
     * validate Method is used for getting the account information of sender(CP2P) and receiver(CP2P & RP2P).
     * @param p_map	HashMap
     * @throws BTSLBaseException,Exception
     */	
    public void validate(HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception
    {
        if(_log.isDebugEnabled()) 
        	_log.debug("validate","Entered p_requestMap:"+p_requestMap);
        _requestMap = p_requestMap;
        try
        {
            _interfaceID=_requestMap.get("INTERFACE_ID");
            _userType=_requestMap.get("USER_TYPE");
            _interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");

            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _msisdn=_requestMap.get("MSISDN"); 			
            //Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
            //If validation of subscriber is not required set the SUCCESS code into request map and return.
            String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
            //String validateRequired="Y";
            if("N".equals(validateRequired))
            {
                _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
                return ;
            }
            _inTXNID=getINTransactionID(_requestMap);			
            _requestMap.put("IN_TXN_ID",_inTXNID);
            _referenceID=_requestMap.get("TRANSACTION_ID");

            String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
            if(_log.isDebugEnabled()) 
            	_log.debug("validate","multFactor: "+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                _log.error("validate","MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,_requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor=multFactor.trim();
            //Set the interface parameters into requestMap
            setInterfaceParameters(ZTEOSNI.ACTION_ACCOUNT_INFO);
            //generate Request Object
            String requestStr = _formatter.generateRequest(ZTEOSNI.ACTION_ACCOUNT_INFO,_requestMap);
            //sending the AccountInfo request to IN along with validate action defined in Huawei84I interface
            sendRequestToIN(ZTEOSNI.ACTION_ACCOUNT_INFO,requestStr);

            //Set the received bundle from IN to request map.
            String receivedBundles =_responseMap.get("received_bundles");
            _requestMap.put("IN_RESP_BUNDLE_CODES", receivedBundles);

            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);

            //get value of BALANCE from response map (BALANCE was set in response map in sendRequestToIN method.)
            String amountStr=_responseMap.get("resp_Balance");
            if(_log.isDebugEnabled())
            	_log.debug("validate ","resp_Balance="+amountStr);
            try
            {
                amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
                _requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
            }
            catch(Exception e)
            {
                _log.errorTrace("validate",e);
                _log.error("validate","Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,_requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
            }

            _requestMap.put("ACCOUNT_STATUS",_responseMap.get("resp_state"));
            _requestMap.put("SERVICE_CLASS",_responseMap.get("resp_ProductCode"));
            //set OLD_EXPIRY_DATE in request map as returned from _responseMap.
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(_responseMap.get("resp_activeStopDate"), "yyyy-MM-dd"));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(_responseMap.get("resp_suspendStopDate"), "yyyy-MM-dd"));
            //set the mapping language of our system from FileCache mapping based on the response language.
            setLanguageFromMapping();
        }
        catch (BTSLBaseException be)
        {
            _log.error("validate","BTSLBaseException be="+be.getMessage());
            if("Y".equalsIgnoreCase(_interfaceClosureSupport) && be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS))
                _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);      	
            throw be; 	   	
        }
        catch(Exception e)
        {
            _log.errorTrace("validate",e);
            _log.error("validate","Exception e:"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,_requestMap.get("NETWORK_CODE"), "While validating the subscriber get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if(_log.isDebugEnabled()) 
                _log.debug("validate","Exiting with  _requestMap="+_requestMap);
        }
    }
/**
 * This method is responsible for the credit of subscriber account.
 *  1.Interface specific parameters are set and added to the request map.
 *  2.Format the request into predefined XML for credit request, method internally calls the generateRequest 
 *     method of  ZTEOGBReqResFormatter.
 *  3.For sending request to IN this method internally calls private method sendRequestToIN
 *  4.Process the response.
 * @param	HashMap	p_map
 * @throws	BTSLBaseException,Exception
 */	
    public void credit(HashMap<String, String> p_requestMap) throws BTSLBaseException, Exception
    {
        if (_log.isDebugEnabled())
        	_log.debug("credit","Entered p_requestMap=" + p_requestMap);
        double multFactorDouble=0;
        String amountStr=null;
        String cardGrpSelectorCode=null;
        _requestMap = p_requestMap;
        double interfaceAmtDouble=0;
        try
        {
            _interfaceID=_requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID=getINTransactionID(_requestMap);
            _requestMap.put("IN_TXN_ID",_inTXNID);
            _referenceID=_requestMap.get("TRANSACTION_ID");
            _msisdn=_requestMap.get("MSISDN");
            cardGrpSelectorCode=_requestMap.get("CARD_GROUP_SELECTOR");
            //Fetching the MULT_FACTOR from the INFile.
            //While sending the amount to IN, it would be multiplied by this factor, and received balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
            if(_log.isDebugEnabled())_log.debug("credit","multFactor:"+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                _log.error("credit","MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR",multFactor);

            //Set the interface parameters into requestMap
            setInterfaceParameters(ZTEOSNI.ACTION_RECHARGE_CREDIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
            try
            {
                amountStr=_requestMap.get("INTERFACE_AMOUNT");

                if("1".equals(cardGrpSelectorCode))
                {
                    multFactorDouble=Double.parseDouble(multFactor);
                    interfaceAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(amountStr),multFactorDouble);
                }
                else
                {
                    String selectorBundleType=_requestMap.get("SELECTOR_BUNDLE_TYPE");
                    if("AMT".equals(selectorBundleType))
                        multFactor=FileCache.getValue(_interfaceID,"AMT_MULT_FACTOR");
                    else if("UNIT".equals(selectorBundleType))
                        multFactor=FileCache.getValue(_interfaceID,"UNIT_MULT_FACTOR");
                    //Convert the amount in amount according to the selector.
                    interfaceAmtDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(amountStr),Double.parseDouble(multFactor.trim()));
                }
                //Change the amount in to string.
                amountStr=String.valueOf(interfaceAmtDouble);
                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
                if(_log.isDebugEnabled()) 
                	_log.debug("credit","From file cache roundFlag="+roundFlag);
                //If the ROUND_FLAG is not defined in the INFile 
                if(InterfaceUtil.isNullString(roundFlag))
                {
                    roundFlag="Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ZTEOSNINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+ _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                _requestMap.put("ROUND_FLAG",roundFlag);
                //If rounding of amount is allowed, round the amount value and put this value in request map.
                if("Y".equals(roundFlag.trim()))
                {
                    amountStr=String.valueOf(Math.round(interfaceAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                }
            }
            catch(Exception e)
            {
                _log.errorTrace("credit",e);
                _log.error("credit","Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if(_log.isDebugEnabled()) 
            	_log.debug("credit","transfer_amount:"+amountStr);
            //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount",amountStr);

                String requestAmountStr = _requestMap.get("REQUESTED_AMOUNT");
                String bonusAmountStr = _requestMap.get("BONUS_AMOUNT");
                double requestAmountDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(requestAmountStr),Double.parseDouble(multFactor));
                double bonusAmountDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(Double.parseDouble(bonusAmountStr),Double.parseDouble(multFactor));
                double packageValue= InterfaceUtil.getINAmountFromSystemAmountToIN(requestAmountDouble,bonusAmountDouble);
                double conversionFactor = 1;

                try
                {
                        String convFactor= FileCache.getValue(_interfaceID,_requestMap.get("CARD_GROUP_SELECTOR")+"_CONVERSION_FACTOR");
                        conversionFactor = Double.parseDouble(convFactor);
                }
                catch(Exception e)
                {
                        conversionFactor = 1;
                }
                double finalPackageValue = packageValue * conversionFactor;
                String finalPackageValueStr = new DecimalFormat("##########.##").format(finalPackageValue);

                String roundEnabledSelectors =  FileCache.getValue(_interfaceID,"ROUND_ENABLED_SELECTORS");
                        if(roundEnabledSelectors.contains(_requestMap.get("CARD_GROUP_SELECTOR")))
                                {
                                        int roundValue = (int)finalPackageValue;
                                        _requestMap.put("PACKAGE_VALUE",String.valueOf(roundValue));
                                }
                        else
                                {
                                        _requestMap.put("PACKAGE_VALUE",String.valueOf(finalPackageValueStr));
                                }
            String requestStr = _formatter.generateRequest(ZTEOSNI.ACTION_RECHARGE_CREDIT,_requestMap);
            //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
            sendRequestToIN(ZTEOSNI.ACTION_RECHARGE_CREDIT,requestStr);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
                if("1".equals(cardGrpSelectorCode))
                {
                    String postBalanceStr =  _responseMap.get("resp_Balance");
                    postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
                    _requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
                }
            }
            catch(Exception e)
            {
                _log.error("credit","Exception e:"+e.getMessage());
                _log.errorTrace("credit",e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if(cardGrpSelectorCode.equals("1"))
            {
                //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString( _responseMap.get("resp_ExpDate"), "yyyy-MM-dd"));
                //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
            }
            else
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS","N");
        } 
        catch (BTSLBaseException be)
        {
            p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("credit","BTSLBaseException be:"+be.getMessage());    		   		
            if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try
            { 
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
                _requestMap.put("TRANSACTION_TYPE","CR");
                handleCancelTransaction();
            }
            catch(BTSLBaseException bte)
            {
                throw bte;
            }
            catch(Exception e)
            {
                _log.errorTrace("credit",e);
                _log.error("credit","Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]", _referenceID,_msisdn,  _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }			
        }
        catch (Exception e)
        {
            _log.errorTrace("credit",e);
            _log.error("credit", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]",_referenceID,_msisdn,  _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (_log.isDebugEnabled()) 
            	_log.debug("credit", "Exited _requestMap=" + _requestMap);
        }
    }
/** This method is used to credit back the sender in case of ambiguous case. 
 * @param p_map HashMap
 * @throws BTSLBaseException, Exception
 */	
    public void creditAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception 
    {
        if (_log.isDebugEnabled())
        	_log.debug("creditAdjust","Entered p_requestMap=" + p_requestMap);
        double interfaceAmtDouble=0;
        double multFactorDouble=0;
        String amountStr=null;
        String cardGrpSelectorCode=null;
        _requestMap = p_requestMap;
        try
        {
            _interfaceID=_requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID=getINTransactionID(_requestMap);
            _requestMap.put("IN_TXN_ID",_inTXNID);
            _referenceID=_requestMap.get("TRANSACTION_ID");
            _msisdn=_requestMap.get("MSISDN");
            cardGrpSelectorCode=_requestMap.get("CARD_GROUP_SELECTOR");
            //Fetching the MULT_FACTOR from the INFile.
            //While sending the amount to IN, it would be multiplied by this factor, and received balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
            if(_log.isDebugEnabled())
            	_log.debug("creditAdjust","multFactor="+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                _log.error("credit","MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR",multFactor);

            //Set the interface parameters into requestMap
            setInterfaceParameters(ZTEOSNI.ACTION_RECHARGE_CREDIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
            try
            {
                amountStr=_requestMap.get("INTERFACE_AMOUNT");
                interfaceAmtDouble=Double.parseDouble(amountStr);
                if("1".equals(cardGrpSelectorCode))
                {
                    multFactorDouble=Double.parseDouble(multFactor);
                    interfaceAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
                }
                else
                {
                    String selectorBundleType=_requestMap.get("SELECTOR_BUNDLE_TYPE");
                    if(!InterfaceUtil.isNullString(selectorBundleType))
                    {
                        if("AMT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(_interfaceID,"AMT_MULT_FACTOR");
                        else if("UNIT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(_interfaceID,"UNIT_MULT_FACTOR");
                        //Convert the amount in amount according to the selector.
                        interfaceAmtDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,Double.parseDouble(multFactor.trim()));
                    }
                }
                //Change the amount in to string.
                amountStr=String.valueOf(interfaceAmtDouble);
                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
                if(_log.isDebugEnabled()) 
                	_log.debug("creditAdjust","From file cache roundFlag = "+roundFlag);
                //If the ROUND_FLAG is not defined in the INFile 
                if(InterfaceUtil.isNullString(roundFlag))
                {
                    roundFlag="Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ZTEOSNINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+ _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                }
                //If rounding of amount is allowed, round the amount value and put this value in request map.
                if("Y".equals(roundFlag.trim()))
                {
                    amountStr=String.valueOf(Math.round(interfaceAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                }
            }
            catch(Exception e)
            {
                _log.errorTrace("creditAdjust",e);
                _log.error("creditAdjust","Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if(_log.isDebugEnabled()) 
                _log.debug("creditAdjust","transfer_amount:"+amountStr);
            //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount",amountStr);
            String requestStr = _formatter.generateRequest(ZTEOSNI.ACTION_RECHARGE_CREDIT,_requestMap);
            //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
            sendRequestToIN(ZTEOSNI.ACTION_RECHARGE_CREDIT,requestStr);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
                if("1".equals(cardGrpSelectorCode))
                {
                    String postBalanceStr =  _responseMap.get("resp_Balance");
                    postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
                    _requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
                }
            }
            catch(Exception e)
            {
                _log.error("creditAdjust","Exception e:"+e.getMessage());
                _log.errorTrace("creditAdjust",e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }

            if(cardGrpSelectorCode.equals("1"))
            {
                //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString( _responseMap.get("resp_ExpDate"), "yyyy-MM-dd"));
                //Get the value of end_inact_date from the response, change its format as per the interface and set it into 
                //requestMap with key as NEW_GRACE_DATE
                //_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString( _responseMap.get("resp_expDate"), "dd/MM/yyyy"));
                //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
            }
            else
            {
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS","N");
            }
        } 
        catch (BTSLBaseException be)
        {
            p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("creditAdjust","BTSLBaseException be:"+be.getMessage());    		   		
            if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try
            { 
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
                _requestMap.put("TRANSACTION_TYPE","CR");
                handleCancelTransaction();
            }
            catch(BTSLBaseException bte)
            {
                throw bte;
            }
            catch(Exception e)
            {
                _log.errorTrace("creditAdjust",e);
                _log.error("creditAdjust","Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]", _referenceID,_msisdn,  _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
                throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }			
        }
        catch (Exception e)
        {
            _log.errorTrace("creditAdjust",e);
            _log.error("creditAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[credit]",_referenceID,_msisdn,  _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (_log.isDebugEnabled()) 
                _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
        }
    }
/**
 * This method is used to debit the sender account in CP2P request.
 * @param p_map HashMap
 * @throws Exception
 */	
    public void debitAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception 
    {
        if (_log.isDebugEnabled())
        	_log.debug("debitAdjust","Entered p_requestMap="+p_requestMap);
        double interfaceAmtDouble=0;
        double multFactorDouble=0;
        String amountStr=null;
        _requestMap = p_requestMap;
        String cardGrpSelectorCode=null;
        try
        {
            _interfaceID=_requestMap.get("INTERFACE_ID");
            _interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
            if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                checkInterfaceB4SendingRequest();
            _inTXNID=getINTransactionID(_requestMap);
            _requestMap.put("IN_TXN_ID",_inTXNID);
            _referenceID=_requestMap.get("TRANSACTION_ID");
            _msisdn=_requestMap.get("MSISDN");
            cardGrpSelectorCode=_requestMap.get("CARD_GROUP_SELECTOR");

            //Fetching the MULT_FACTOR from the INFile.
            //While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
            String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
            if(_log.isDebugEnabled())
            	_log.debug("debitAdjust","multFactor:"+multFactor);
            if(InterfaceUtil.isNullString(multFactor))
            {
                _log.error("debitAdjust","MULT_FACTOR  is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
                throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            multFactor = multFactor.trim();
            _requestMap.put("MULT_FACTOR",multFactor);

            //Set the interface parameters into requestMap
            setInterfaceParameters(ZTEOSNI.ACTION_IMMEDIATE_DEBIT);
            _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            try
            {
                amountStr=_requestMap.get("INTERFACE_AMOUNT");
                multFactorDouble=Double.parseDouble(multFactor);
                interfaceAmtDouble=Double.parseDouble(amountStr);
                if("1".equals(cardGrpSelectorCode))
                    interfaceAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
                else
                {
                    String selectorBundleType=_requestMap.get("SELECTOR_BUNDLE_TYPE");
                    if(!InterfaceUtil.isNullString(selectorBundleType))
                    {
                        if("AMT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(_interfaceID,"AMT_MULT_FACTOR");
                        else if("UNIT".equals(selectorBundleType))
                            multFactor=FileCache.getValue(_interfaceID,"UNIT_MULT_FACTOR");
                        //Convert the amount in amount according to the selector.
                        interfaceAmtDouble=InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,Double.parseDouble(multFactor.trim()));
                    }
                }
                //Change the amount in to string.
                amountStr=String.valueOf(interfaceAmtDouble);
                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
                if(_log.isDebugEnabled()) 
                	_log.debug("debitAdjust","From file cache roundFlag = "+roundFlag);
                //If the ROUND_FLAG is not defined in the INFile 
                if(InterfaceUtil.isNullString(roundFlag))
                {
                    roundFlag="Y";
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "ZTEOSNINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+ _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
                }
                //If rounding of amount is allowed, round the amount value and put this value in request map.
                if("Y".equals(roundFlag.trim()))
                {
                    amountStr=String.valueOf(Math.round(interfaceAmtDouble));
                    _requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                }
            }
            catch(Exception e)
            {
                _log.errorTrace("debitAdjust",e);
                _log.error("debitAdjust","Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            if(_log.isDebugEnabled()) 
            	_log.debug("debitAdjust","transfer_amount:"+amountStr);
            //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
            _requestMap.put("transfer_amount",amountStr);
            String requestSre = _formatter.generateRequest(ZTEOSNI.ACTION_IMMEDIATE_DEBIT,_requestMap);
            //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
            sendRequestToIN(ZTEOSNI.ACTION_IMMEDIATE_DEBIT,requestSre);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
                String postBalanceStr =  _responseMap.get("resp_Balance");
                postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
                _requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
            }
            catch(Exception e)
            {
                _log.error("debitAdjust","Exception e:"+e.getMessage());
                _log.errorTrace("debitAdjust",e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID,  _requestMap.get("NETWORK_CODE"), "balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
                throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString( _responseMap.get("resp_ExpDate"), "yyyy-MM-dd"));
            //Get the value of end_inact_date from the response, change its format as per the interface and set it into 
            //requestMap with key as NEW_GRACE_DATE
            //_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString( _responseMap.get("RESP_GRACE"), "dd/MM/yyyy"));
            //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
        } 
        catch (BTSLBaseException be)
        {
            p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
            _log.error("debitAdjust","BTSLBaseException be:"+be.getMessage());    		   		
            if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                throw be;
            try
            { 
                if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
                    _interfaceCloser.updateCountersOnAmbiguousResp(_interfaceCloserVO,_requestMap);
                _requestMap.put("TRANSACTION_TYPE","CR");
                handleCancelTransaction();
            }
            catch(BTSLBaseException bte)
            {
                throw bte;
            }
            catch(Exception e)
            {
                _log.errorTrace("debitAdjust",e);
                _log.error("debitAdjust","Exception e:"+e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[debitAdjust]", _referenceID,_msisdn,  _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
                throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }			
        }
        catch (Exception e)
        {
            _log.errorTrace("debitAdjust",e);
            _log.error("debitAdjust", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[debitAdjust]",_referenceID,_msisdn,  _requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (_log.isDebugEnabled()) 
                _log.debug("debitAdjust", "Exited _requestMap=" + _requestMap);
        }
    }
/**
 * This method would be used to adjust the validity of the subscriber account at the IN.
 * @param	HashMap p_requestMap
 * @throws	BTSLBaseException, Exception
 */ 	
    public void validityAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception 
    {}//end of validityAdjust
/**
 * This method is responsible to send the request to IN.
 * @param	String p_inRequestStr
 * @param	int p_action
 * @throws BTSLBaseException
 */	
    private void sendRequestToIN(int p_action,String p_requestStr) throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) 
        	_log.debug("sendRequestToIN"," p_action="+p_action);
        //Put the request string, action, interface id, network code in the Transaction log.
        TransactionLog.log(_interfaceID,_referenceID,_msisdn,_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"_requestMap="+_requestMap+" p_requestStr="+p_requestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," action="+p_action );
        long startTime=0,endTime=0,warnTime=0;
        Call call=null;
        String responseStr=null;

        try
        {
            if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION"))&& "S".equals(_userType)))
            {
                _isSameRequest=true;
                checkInterfaceB4SendingRequest();
            }

            //Get the start time when the request is send to IN.
            startTime=System.currentTimeMillis();
            _requestMap.put("IN_START_TIME",String.valueOf(startTime));
            try
            {
                ZTEOSNWebServiceConnector serviceConnection = new ZTEOSNWebServiceConnector();
                call =serviceConnection.callService(_requestMap);

                if(call==null)
                {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Unable to get Client Object");
                    _log.error("sendRequestToIN","Unable to get Client Object");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CLIENT_OBJECT_INITIALIZATION);
                }

                try
                {
                    responseStr =  call.invoke(new Object[] { p_requestStr}).toString();	
                    endTime=System.currentTimeMillis();
                }
                catch(java.rmi.RemoteException re)
                {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Remote Exception occured. So marking the transaction as AMBIGUOUS");
                    _log.error("sendRequestToIN","Remote Exception occured. So marking the response as AMBIGUOUS");
                    _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                    throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
                }
                catch(Exception e)
                {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
                    _log.error("sendRequestToIN","Error Message :"+e.getMessage());
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                }				
            }
            catch(BTSLBaseException be)
            {
                throw be;
            }
            catch(Exception e)
            {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
                _log.error("sendRequestToIN","Error Message :"+e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            finally
            {
                if(endTime==0) endTime=System.currentTimeMillis();
                _requestMap.put("IN_END_TIME",String.valueOf(endTime));			        
                _log.error("sendRequestToIN","Request sent to IN at:"+startTime+" Response received from IN at:"+endTime);
            }

            if(responseStr==null)
            {
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Response object found null");
                _log.error("sendRequestToIN","Response object is null");
                _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
                throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
            }

            //End time would be stored into request map with 
            //key as IN_END_TIME as soon as the response of the request is fetched from the IN.
            //Difference of start and end time would be compared against the warn time, 
            //if request and response takes more time than that of the warn time,
            //an event with level INFO is handled

            //Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled
            warnTime=Long.parseLong(_requestMap.get("WARN_TIMEOUT"));
            if(endTime-startTime>warnTime)
            {
                _log.info("sendRequestToIN", "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));
                EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"ZTE IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
            }

            //parse the response message by using ZTEOCIRequestResponseFormatter and fetch the execution status.
            _responseMap=_formatter.parseResponse(p_action,responseStr);
            TransactionLog.log( _interfaceID,_referenceID,_msisdn,_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"_requestMap: "+_requestMap+", _responseMap="+_responseMap+", responseStr="+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS," action="+p_action);		
            String status=_responseMap.get("resp_returnCode");
            _requestMap.put("INTERFACE_STATUS",status);

            if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)))
            {
                if(_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
                    _interfaceCloser.resetCounters(_interfaceCloserVO,_requestMap);  
                _interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO); 
            }

            //If the status is Not OK, exception with error code as INTERFACE_MSISDN_NOT_FOUND will be thrown.
            if(!ZTEOSNI.RESULT_OK.equals(status))
            {
                int len =status.length();
                if(len >10)
                    status= status.substring(len-10);

                _requestMap.put("INTERFACE_STATUS",status);
                if(ZTEOSNI.MSISDN_NOT_FOUND.equals(status))
                {
                    EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on IN");
                    _log.error("sendRequestToIN","MSISDN does not exist on IN");
                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
                }
                //Check the status whether the subscriber's MSISDN defined in the IN
                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEOSNINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Response Status fron IN is: "+status+ ". So marking response as FAIL");
                _log.error("sendRequestToIN","Response Status fron IN is: "+status+ ". So marking response as FAIL.");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }
        }
        catch(BTSLBaseException be)
        {
            _log.error("sendRequestToIN","BTSLBaseException be = "+be.getMessage());
            throw be;
        }//end of BTSLBaseException
        catch(Exception e)
        {
            _log.errorTrace("sendRequestToIN",e);
            _log.error("sendRequestToIN","Exception="+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS3CCAPIINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+ _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) 
                _log.debug("sendRequestToIN","Exiting p_action="+p_action);
        }//end of finally
    }
/**
 * This method is used to get interface specific values from FileCache(load at starting)based on
 * interface id and set to the requested map.These parameters are
 *  1.cp_id
 *  2.application
 *  3.transaction_currency
 * @throws	BTSLBaseException, Exception
 */	
    public void setInterfaceParameters(int p_action) throws BTSLBaseException,Exception 
    {
        if(_log.isDebugEnabled())
            _log.debug("setInterfaceParameters","Entered");
        try
        {        	
            String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
            if(InterfaceUtil.isNullString(cancelTxnAllowed))
            {
                _log.error("setInterfaceParameters","Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());

            String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
            if(InterfaceUtil.isNullString(systemStatusMappingCr))
            {
                _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());

            String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
            if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
            {
                _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());

            String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
            if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
            {
                _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());

            String systemStatusMappingCrBck = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
            if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
            {
                _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());

            String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
            if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
            {
                _log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());


            String cancelNA = FileCache.getValue(_interfaceID,"CANCEL_NA");
            if(InterfaceUtil.isNullString(cancelNA))
            {
                _log.error("setInterfaceParameters","Value of CANCEL_NA is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CANCEL_NA",cancelNA.trim());

            String userId = FileCache.getValue(_interfaceID,"USER_NAME");
            if(InterfaceUtil.isNullString(userId))
            {
                _log.error("setInterfaceParameters","Value of USER_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "USER_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("USER_NAME",userId.trim());

            String passwd = FileCache.getValue(_interfaceID,"PASSWORD");
            if(InterfaceUtil.isNullString(passwd))
            {
                _log.error("setInterfaceParameters","Value of PASSWORD is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("PASSWORD",passwd.trim());

            String authHeader = FileCache.getValue(_interfaceID,"AUTH_HEADER");
            if(InterfaceUtil.isNullString(authHeader))
            {
                _log.error("setInterfaceParameters","Value of AUTH_HEADER is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "AUTH_HEADER is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("AUTH_HEADER",authHeader.trim());

            String zteNameSpace = FileCache.getValue(_interfaceID,"ZTE_NAMESPACE");
            if(InterfaceUtil.isNullString(zteNameSpace))
            {
                _log.error("setInterfaceParameters","Value of SAFCOM_TERM_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "ZTE_NAMESPACE is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("ZTE_NAMESPACE",zteNameSpace.trim());

            String uri = FileCache.getValue(_interfaceID,"SOAP_ACTION_URI");
            if(InterfaceUtil.isNullString(uri))
            {
                _log.error("setInterfaceParameters","Value of SOAP_ACTION_URI is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "SOAP_ACTION_URI is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("SOAP_ACTION_URI",uri.trim());

            String endPt = FileCache.getValue(_interfaceID,"END_POINT");
            if(InterfaceUtil.isNullString(endPt))
            {
                _log.error("setInterfaceParameters","Value of END_POINT is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("END_POINT",endPt.trim());

            String operationName = FileCache.getValue(_interfaceID,"OPERATION_NAME");
            if(InterfaceUtil.isNullString(operationName))
            {
                _log.error("setInterfaceParameters","Value of OPERATION_NAME is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "OPERATION_NAME is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("OPERATION_NAME",operationName.trim());

            String reqService=_requestMap.get("REQ_SERVICE");
            String channelId =null;
            if(!InterfaceUtil.isNullString(reqService))
            {
            	String domainCode=_requestMap.get("DOMAIN_CODE");
            	if(!InterfaceUtil.isNullString(domainCode))
            		channelId = FileCache.getValue(_interfaceID,_requestMap.get("DOMAIN_CODE")+"_CHANNEL_ID");
            	
            	if(InterfaceUtil.isNullString(channelId))
            		channelId = FileCache.getValue(_interfaceID,reqService+"_CHANNEL_ID");
             
              if(reqService.equalsIgnoreCase("PRC") && InterfaceUtil.isNullString(channelId))
                     channelId = FileCache.getValue(_interfaceID,reqService+"_DEFAULT_CHANNEL_ID");
            }
           // String channelId = FileCache.getValue(_interfaceID,reqService+"_CHANNEL_ID");
            if(InterfaceUtil.isNullString(channelId))
            {
                _log.error("setInterfaceParameters","Value of CHANNEL_ID is not defined in the INFile");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn ,  _requestMap.get("NETWORK_CODE"), "CHANNEL_ID is not defined in the INFile.");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("CHANNEL_ID",channelId.trim());

            String warnTimeStr=FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
            if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
            {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "ZTEOSNINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+ _requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
                throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
            }
            _requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());
        }//end of try block
        catch(BTSLBaseException be)
        {
            throw be;
        }
        catch(Exception e)
        {
            _log.error("setInterfaceParameters","Exception e="+e.getMessage());
            throw e;
        }//end of catch-Exception
        finally
        {
            if (_log.isDebugEnabled())
                _log.debug("setInterfaceParameters", "Exited _requestMap:" + _requestMap);
        }//end of finally
    }
/**
 * Method to Check interface status before sending request.	
 * @throws	BTSLBaseException 
 */	
    private void checkInterfaceB4SendingRequest() throws BTSLBaseException
    {
        if(_log.isDebugEnabled()) 
            _log.debug("checkInterfaceB4SendingRequest","Entered");
        try
        {
            _interfaceCloserVO=(InterfaceCloserVO)InterfaceCloserController._interfaceCloserVOTable.get(_interfaceID);
            _interfaceLiveStatus=_requestMap.get("INT_ST_TYPE");
            _interfaceCloserVO.setControllerIntStatus(_interfaceLiveStatus);
            _interfaceCloser=_interfaceCloserVO.getInterfaceCloser();

            //Get AUTO_RESUME_SUPPORT property from IN FILE. If it is not defined then set it as 'N'.
            String autoResumeSupported = FileCache.getValue(_interfaceID,"AUTO_RESUME_SUPPORT");
            if(InterfaceUtil.isNullString(autoResumeSupported))
            {
                autoResumeSupported="N";
                _log.error("checkInterfaceB4SendingRequest","Value of AUTO_RESUME_SUPPORT is not defined in the INFile");	    		
            }  	    		    		
            //If Controller sends 'A' and interface status is suspended, expiry is checked.
            //If Controller sends 'M', request is forwarded to IN after resetting counters.
            if(InterfaceCloserI.INTERFACE_AUTO_ACTIVE.equals(_interfaceLiveStatus)&& _interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
            {
                //Check if Auto Resume is supported by IN or not.If not then throw exception. request would not be sent to IN.
                if("N".equals(autoResumeSupported))
                {
                    _log.error("checkInterfaceB4SendingRequest","Interface Suspended.");
                    throw new BTSLBaseException(this,"checkInterfaceB4SendingRequest",InterfaceErrorCodesI.INTERFACE_SUSPENDED);
                }
                //If "Auto Resume" is supported then only check the expiry of interface, if expired then only request would be sent to IN
                //otherwise checkExpiry method throws exception
                if(_isSameRequest)
                    _interfaceCloser.checkExpiryWithoutExpiryFlag(_interfaceCloserVO);
                else   			
                    _interfaceCloser.checkExpiry(_interfaceCloserVO);
            }
            //this block is executed when Interface is manually resumed (Controller sends 'M')from suspend state
            else if(InterfaceCloserI.INTERFACE_MANNUAL_ACTIVE.equals(_interfaceCloserVO.getControllerIntStatus()) && _interfaceCloserVO.getFirstSuspendAt()!=0)
                _interfaceCloser.resetCounters(_interfaceCloserVO,null);            
        }
        catch(BTSLBaseException be)
        {
            throw be;
        }
        catch(Exception e)
        {
            _log.errorTrace("checkInterfaceB4SendingRequest",e);
            _log.error("checkInterfaceB4SendingRequest","Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"checkInterfaceB4SendingRequest",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if(_log.isDebugEnabled()) 
                _log.debug("checkInterfaceB4SendingRequest","Exited");
        }	
    }
/**
 * This method used to get the system language mapped in FileCache based on the INLanguge.Includes following
 * If the Mapping key not defined in IN file handle the event as System Error with level FATAL.
 * If the Mapping is not defined handle the event as SYSTEM INFO with level MAJOR and set empty string.
 * @throws Exception
 */	
    private void setLanguageFromMapping() throws Exception
    {
        if(_log.isDebugEnabled()) 
            _log.debug("setLanguageFromMapping","Entered");
        String mappedLang="";
        String[] mappingArr;
        String[] tempArr;
        boolean mappingNotFound = true;//Flag defines whether the mapping of language is found or not.
        String langFromIN = null;
        String mappingString = null;
        try
        {
            //Get the mapping string from the FileCache and storing all the mappings into array which are separated by ','.
            mappingString=FileCache.getValue(_interfaceID, "LANGUAGE_MAPPING");
            if(InterfaceUtil.isNullString(mappingString))
                mappingString="";
            langFromIN = _responseMap.get("resp_defLang");
            if(_log.isDebugEnabled()) 
                _log.debug("setLanguageFromMapping","mappingString = "+mappingString +" langFromIN = "+langFromIN);
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ZTEOSNINHandler[setLanguageFromMapping]",_referenceID, _msisdn,  _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  "+langFromIN +" is not defined in IN file Hence setting the Default language");
            //Set the mapped language to the requested map with key as IN_LANGUAGE.
            _requestMap.put("IN_LANG",mappedLang);
        }//end of try
        catch(Exception e)
        {
            _log.errorTrace("setLanguageFromMapping",e);
            _log.error("setLanguageFromMapping","Exception e="+e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "ZTEOSNINHandler[setLanguageFromMapping]",_referenceID, _msisdn,  _requestMap.get("NETWORK_CODE"), "while setting the language mapping get Exception=" + e.getMessage());
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) _log.debug("setLanguageFromMapping","Exited mappedLang ="+mappedLang);
        }//end of finally setLanguageFromMapping
    }//end of setLanguageFromMapping
/**
 * Method to send cancel request to IN for any ambiguous transaction.
 * This method also makes reconciliation log entry. 	
 * @throws	BTSLBaseException 
 */
    private void handleCancelTransaction() throws BTSLBaseException
    {
        if (_log.isDebugEnabled())
            _log.debug("handleCancelTransaction", "Entered.");
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
            _requestMap.put("REMARK1",FileCache.getValue(_interfaceID,"REMARK1"));
            _requestMap.put("REMARK2",FileCache.getValue(_interfaceID,"REMARK2"));
            //get reconciliation log object associated with interface
            reconLog = ReconcialiationLog.getLogObject(_interfaceID);		    
            if (_log.isDebugEnabled())_log.debug("handleCancelTransaction", "reconLog."+reconLog);
            cancelTxnAllowed=_requestMap.get("CANCEL_TXN_ALLOWED");
            //if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
            //into reconciliation log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
            if("N".equals(cancelTxnAllowed))
            {
                cancelNA=_requestMap.get("CANCEL_NA");//Cancel command status as NA.
                cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap,cancelNA,"CANCEL_COMMAND_STATUS_MAPPING");
                _requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);				
                interfaceStatus=_requestMap.get("INTERFACE_STATUS");
                systemStatusMapping=_requestMap.get("SYSTEM_STATUS_MAPPING");				
                cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING

                _requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
                reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
                reconLog.info("",reconciliationLogStr);
                if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
                    throw new BTSLBaseException(this,"handleCancelTransaction",cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.	??????)    			
                _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);	
                //added to discard amount field from the message.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
            }
        }
        catch(BTSLBaseException be)
        {
            throw be;
        }
        catch(Exception e)
        {
            _log.errorTrace("handleCancelTransaction",e);
            _log.error("handleCancelTransaction","Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"handleCancelTransaction",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
            if (_log.isDebugEnabled())
                _log.debug("handleCancelTransaction", "Exited");
        }
    }
/**
 * This Method will generate the IN Transaction ID for each request.
 * @param p_map HashMap
 * @return inTxnId String
 */
    protected String getINTransactionID(HashMap<String,String> p_map)
    {
        if(_log.isDebugEnabled())_log.debug("getINTransactionID","Entered");
        //Get the USER_TYPE and TRANSACTION_ID from the map.
        String inReconID=null;
        String userType=p_map.get("USER_TYPE");
        String inTxnId=p_map.get("TRANSACTION_ID");


        //If USER_TYPE is not null, append it in the transaction id.
        if(!InterfaceUtil.isNullString(userType))
            inReconID=inTxnId+"."+userType.trim();

        //Put the transaction id into the map.
        p_map.put("IN_RECON_ID",inReconID);

        if(_log.isDebugEnabled())
            _log.debug("getINTransactionID","Exiting with IN_RECON_ID and IN_TXN_ID="+inTxnId);
        return inTxnId;
    }    
}