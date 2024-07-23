/*
 * Created on November 6, 2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodaidea.vodafone.ericson;


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

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
/*import com.btsl.pretups.inter.cs3cp6.cs3scheduler.NodeManager;
import com.btsl.pretups.inter.cs3cp6.cs3scheduler.NodeScheduler;
import com.btsl.pretups.inter.cs3cp6.cs3scheduler.NodeVO;
import com.btsl.pretups.inter.cs3cp6.cs3scheduler.NodeCloser;
 */
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.module.ReconcialiationLog;
import com.btsl.pretups.logging.TransactionLog;
import com.btsl.util.BTSLUtil;
import com.inter.cs3cp6.CS3CP6I;
import com.inter.vodaidea.logging.InterfaceRequestResponseLog;



/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UCIP4xINHandler implements InterfaceHandler{

	private Hashtable<String,String> valuesHash = new Hashtable<String,String>();
	private static Log _log = LogFactory.getLog(UCIP4xINHandler.class.getName());
	private HashMap _requestMap = null;//Contains the respose of the request as key and value pair.
	private HashMap _responseMap = null;//Contains the request parameter as key and value pair.
	private String _interfaceID=null;//Contains the interfaceID
	private String _inTXNID=null;//UNodesed to represent the Transaction ID
	private String _msisdn=null;//Used to store the MSISDN
	private String _referenceID=null;
	private String  _userType=null;
	private static  SimpleDateFormat _sdf = new SimpleDateFormat ("yyMMddHHmm");
	private static int _transactionIDCounter=0;
	private static int  _prevMinut=0;
	private static UCIP4xRequestFormatter _ucip4xRequestFormatter=null;
	private static UCIP4xResponseParser _ucip4xResponseParser=null;

	String pamimessage = null;


	private boolean _isRetryRequest=false;
	//private NodeCloser _nodeCloser =null;
	private String _interfaceBonusValue=null;
	//private String isGetAccntDetailsRqd=null;//added to validate firstIVRCallflag

	static
	{
		if(_log.isDebugEnabled()) _log.debug("UCIP4xINHandler[static]","Entered");
		try
		{
			_ucip4xRequestFormatter = new UCIP4xRequestFormatter();
			_ucip4xResponseParser=new UCIP4xResponseParser();
		}
		catch(Exception e)
		{
			_log.error("UCIP4xINHandler[static]","While instantiation of UCIP4xRequestFormatter get Exception e::"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler[static]","","", "","While instantiation of UCIP4xRequestFormatter get Exception e::"+e.getMessage());
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug("UCIP4xINHandler[static]","Exited");
		}
	}

	/**
	 * Implements the logic that validate the subscriber and get the subscriber information
	 * from the IN.
	 * @param   HashMap  p_requestMap
	 * @throws  BTSLBaseException, Exception
	 */
	public void validate(HashMap p_requestMap) throws BTSLBaseException, Exception
	{
		final String methodName = "validate";
		if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_requestMap:"+p_requestMap);
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_msisdn=(String)_requestMap.get("MSISDN");
			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not)
			//If validation of subscriber is not required set the SUCCESS code into request map and return.
			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
			// isGetAccntDetailsRqd=FileCache.getValue(_interfaceID,"ACCOUNT_DETAILS_RQD");
			if("N".equals(validateRequired))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				return ;
			}
			_inTXNID=getINTransactionID(_requestMap);
			_requestMap.put("IN_RECON_ID",_inTXNID);
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");

			if(_log.isDebugEnabled()) _log.debug(methodName,"multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor=multFactor.trim();
			//Set the interface parameters into requestMap


			setInterfaceParameters();
			String inStr = _ucip4xRequestFormatter.generateRequest(UCIP4xI.ACTION_ACCOUNT_DETAILS,_requestMap);
			sendRequestToIN(inStr,UCIP4xI.ACTION_ACCOUNT_DETAILS);


			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			//get value of accountValue1 from response map (accountValue1 was set in response map in sendRequestToIN method.)
			String amountStr=(String)_responseMap.get("accountValue1");
			try
			{
				amountStr = InterfaceUtil.getSystemAmountFromINAmount(amountStr,Double.parseDouble(multFactor));
				_requestMap.put("INTERFACE_PREV_BALANCE",amountStr);
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
				
				_log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "accountValue1 obtained from the IN is not numeric, while parsing the accountValue1 get Exception e:"+e.getMessage());
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
			}

			Date currentDate = new Date();
			Date supervisionExpiryDate=null;
			Date serviceFeeExpiryDate =null;
			Date serviceRemovalDate=null;

try{ supervisionExpiryDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("supervisionExpiryDate"), "yyyyMMdd");}catch(Exception e){}	
				try{ serviceFeeExpiryDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd");}catch(Exception e){}
				//Date creditClearanceDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("creditClearanceDate"), "yyyyMMdd");
				
	try{	serviceRemovalDate = InterfaceUtil.getDateFromDateString((String)_responseMap.get("serviceRemovalDate"), "yyyyMMdd");}catch(Exception e){}
		
				
			String supervisionExpiryDateString =(String) _responseMap.get("supervisionExpiryDate");
			String serviceFeeExpiryDateString = (String) _responseMap.get("serviceFeeExpiryDate");

			//If first IVR call not made
			if(supervisionExpiryDate != null && supervisionExpiryDate.equals(InterfaceUtil.getDateFromDateString("99991231","yyyyMMdd")))
			{
				String errorCode="";
				if( "C2S".equals((String)_requestMap.get("MODULE")))
					errorCode=InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED;
				else
					errorCode=InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED_P2P;

				throw new BTSLBaseException(errorCode);
			}

			// Check for Account Status ACTIVE, INACTIVE and DEACTIVE(EXPIRED)
			if((supervisionExpiryDate != null &&  serviceFeeExpiryDate !=null  &&  BTSLUtil.getDifferenceInUtilDates(currentDate, supervisionExpiryDate)>=0) && (BTSLUtil.getDifferenceInUtilDates(currentDate, serviceFeeExpiryDate)>=0))
				_requestMap.put("ACCOUNT_STATUS","ACTIVE");
			else if(supervisionExpiryDate != null &&  serviceFeeExpiryDate !=null && serviceRemovalDate != null && currentDate.after(supervisionExpiryDate) && currentDate.after(serviceFeeExpiryDate)&& (BTSLUtil.getDifferenceInUtilDates(currentDate, serviceRemovalDate)>=0))
				_requestMap.put("ACCOUNT_STATUS","GRACE");
			else if((supervisionExpiryDate != null && supervisionExpiryDateString !=null  &&  serviceFeeExpiryDate !=null  && serviceFeeExpiryDateString !=null && (supervisionExpiryDate ==null || "0".equals(supervisionExpiryDateString))) && (serviceFeeExpiryDate == null || "0".equals(serviceFeeExpiryDateString)))
				_requestMap.put("ACCOUNT_STATUS","INACTIVE");
			else if(serviceRemovalDate != null && currentDate.after(serviceRemovalDate))
				_requestMap.put("ACCOUNT_STATUS","DEACTIVE");
			_requestMap.put("SERVICE_CLASS", (String) _responseMap.get("serviceClassCurrent"));
			//set OLD_EXPIRY_DATE in request map as returned from _responseMap.
			if(_responseMap.get("supervisionExpiryDate")!=null)
			_requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("supervisionExpiryDate")).trim(), "yyyyMMdd"));
			if(_responseMap.get("serviceFeeExpiryDate")!=null)
			_requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(((String) _responseMap.get("serviceFeeExpiryDate")).trim(), "yyyyMMdd"));
			}
		catch (BTSLBaseException be)
		{
			_log.error(methodName,"BTSLBaseException be="+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName,"Exception e:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validation of the subscriber get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled()) _log.debug(methodName,"Exiting with  _requestMap: "+_requestMap);
		}
	}//end of validate
	/**
	 * Implements the logic that credit the subscriber account on IN.
	 * @param   HashMap  p_requestMap
	 * @throws  BTSLBaseException, Exception
	 */
	public void credit(HashMap p_requestMap) throws BTSLBaseException, Exception
	{
		final String methodName = "credit";
		if (_log.isDebugEnabled())_log.debug(methodName,"Entered p_requestMap: " + p_requestMap);
		double systemAmtDouble=0;
		double multFactorDouble=0;
		String amountStr=null;
		_requestMap = p_requestMap;
		try
		{
			_interfaceID=(String)_requestMap.get("INTERFACE_ID");
			_inTXNID=getINTransactionID(_requestMap);
			_requestMap.put("IN_RECON_ID",_inTXNID);
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			_msisdn=(String)_requestMap.get("MSISDN");
			
			
			String creditRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE")+"_C");
		
			//String validateRequired="Y";
			if("N".equals(creditRequired))
			{
				_requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
				_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
				return ;
			}
			
			
			//_userType=(String)_requestMap.get("USER_TYPE");
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("credit","multFactor:"+multFactor);
			//              isGetAccntDetailsRqd=FileCache.getValue(_interfaceID,"ACCOUNT_DETAILS_RQD");
			//              if(_log.isDebugEnabled())_log.debug("credit","isGetAccntDetailsRqd:"+isGetAccntDetailsRqd);
			if(InterfaceUtil.isNullString(multFactor))
			{
				_log.error(methodName,"MULT_FACTOR  is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);

			//Set the interface parameters into requestMap
			setInterfaceParameters();
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
			try
			{
				multFactorDouble=Double.parseDouble(multFactor);
				//          double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
				double interfaceAmtDouble = getInterfaceAmount(p_requestMap);

				systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,multFactorDouble);
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"UCIP4x_RR");
				if(_log.isDebugEnabled()) _log.debug(methodName,"From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile
				if(InterfaceUtil.isNullString(roundFlag))
				{
					roundFlag="Y";
//					EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "UCIP4xINHandler["+methodName+"]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
					amountStr=String.valueOf(Math.round(systemAmtDouble));
					_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
				}
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
				_log.error("credit","Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug(methodName,"transfer_amount:"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);

//			key value of requestMap is formatted into XML string for the validate request.
			String inStr = _ucip4xRequestFormatter.generateRequest(UCIP4xI.ACTION_RECHARGE_CREDIT,_requestMap);

//			try{_ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID,"CREDIT_RETRY_CNT"));}catch(Exception e){_ambgMaxRetryCount=1;}

			//sending the Re-charge request to IN along with re-charge action defined in CS3MobililI interface
			sendRequestToIN(inStr,UCIP4xI.ACTION_RECHARGE_CREDIT);
			//set TRANSACTION_STATUS as Success in request map
			_requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
			// set NEW_EXPIRY_DATE into request map

			if(!_isRetryRequest)
			{
				_requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("supervisionExpiryDate"), "yyyyMMdd"));
				_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("serviceFeeExpiryDate"), "yyyyMMdd"));


				//set INTERFACE_POST_BALANCE into request map as obtained thru response map.
				try
				{
					String postBalanceStr = (String) _responseMap.get("accountValue1");
					postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
					_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
				}
				catch(Exception e)
				{
					_log.errorTrace(methodName, e);
					_log.error(methodName,"Exception e:"+e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "NEWBALANCE  is not Numeric");
					throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_RESPONSE);
				}
				_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "Y");
			}
			else
			{
				_requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
			}

		}
		catch (BTSLBaseException be)
		{
			p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
			_log.error(methodName,"BTSLBaseException be:"+be.getMessage());
			if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
				throw be;
			try
			{
				_requestMap.put("TRANSACTION_TYPE","CR");
				handleCancelTransaction();
			}
			catch(BTSLBaseException bte)
			{
				throw bte;
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName, e);
				_log.error(methodName,"Exception e:"+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
		}
		catch (Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName, "Exception e:" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exited _requestMap=" + _requestMap);
		}
	}//end of credit


	/**
	 * This method is used to adjust the following
	 * 1.Amount
	 * 2.ValidityDays
	 * 3.GraceDays
	 */
	public void creditAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{/*
        if (_log.isDebugEnabled())_log.debug("creditAdjust"," Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;//assign map passed from InterfaceModule to _requestMap(instance var)
        double systemAmtDouble =0;
        String amountStr=null;
        int validityDays=0;//Defines the validity days by which adjustment to be made.
        int graceDays=0;//Defines the grace period by which adjustment to be made.
        try
                {
                _userType=(String)_requestMap.get("USER_TYPE");
                _interfaceID=(String)_requestMap.get("INTERFACE_ID");// get interface id form request map
                _inTXNID=getINTransactionID(_requestMap);//Generate the IN transaction id and set in _requestMap
                _requestMap.put("IN_RECON_ID",_inTXNID);
                        _requestMap.put("IN_TXN_ID",_inTXNID);
                        _referenceID=(String)_requestMap.get("TRANSACTION_ID");

                        _msisdn=(String)_requestMap.get("MSISDN");//get MSISDN from request map
                        //Get the Multiplication factor from the FileCache with the help of interface id.
                        String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
//                      isGetAccntDetailsRqd=FileCache.getValue(_interfaceID,"ACCOUNT_DETAILS_RQD");
                        if(InterfaceUtil.isNullString(multFactor))
                        {
                            _log.error("creditAdjust","MULT_FACTOR  is not defined in the INFile");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
                            throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                        multFactor = multFactor.trim();
                        //Set the interface parameters into requestMap
                        setInterfaceParameters();
                        if("R".equals(_userType))
                                _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
                        else if("S".equals(_userType))
                                _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT_BCK");
                        try
                        {
                            double huaweiMultFactorDouble=Double.parseDouble(multFactor);
                                double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
                                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,huaweiMultFactorDouble);
                                amountStr=String.valueOf(systemAmtDouble);
                                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                                String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
                                if(_log.isDebugEnabled()) _log.debug("creditAdjust","From file cache roundFlag = "+roundFlag);
                                //If the ROUND_FLAG is not defined in the INFile
                                if(InterfaceUtil.isNullString(roundFlag))
                                {
                                    roundFlag="Y";
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3CP6INHandler[creditAdjust]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                                }
                                //If rounding of amount is allowed, round the amount value and put this value in request map.
                                if("Y".equals(roundFlag.trim()))
                                {
                                    amountStr=String.valueOf(Math.round(systemAmtDouble));
                                        _requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                                }
                        }
                        catch(Exception e)
                        {
                            _log.errorTrace(methodName, e);
                            _log.error("creditAdjust","Exception e:"+e.getMessage());
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[creditAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                            throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                        if(_log.isDebugEnabled()) _log.debug("creditAdjust","transfer_amount:"+amountStr+" multFactor:"+multFactor);
                        //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
                        _requestMap.put("transfer_amount",amountStr);

                        //Check for the validity Adjustment-VALIDITY_DAYS
                        if(!InterfaceUtil.isNullString((String)_requestMap.get("VALIDITY_DAYS")))
                        {
                            try
                            {
                                //For the adjust ment make validity days as negative.
                                validityDays =Integer.parseInt(((String)_requestMap.get("VALIDITY_DAYS")).trim());
                                if(_log.isDebugEnabled()) _log.debug("creditAdjust","validityDays::"+validityDays);
                                //Set the validity days into request map with key as 'relative_date_adjustment_service_fee'
                                _requestMap.put("supervisionExpiryDateRelative",String.valueOf(validityDays));
                            }
                            catch(Exception e)
                            {
                                _log.errorTrace(methodName, e);
                                _log.error("creditAdjust","Exception e::"+e.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }
                        }//end of cheking validity days adjustment
                        //Check for the grace Adjustment-GRACE_DAYS
                        if(!InterfaceUtil.isNullString((String)_requestMap.get("GRACE_DAYS")))
                        {
                            try
                            {
                                //For the adjust ment make grace days as negative.
                                graceDays =Integer.parseInt(((String)_requestMap.get("GRACE_DAYS")).trim());
                                if(_log.isDebugEnabled()) _log.debug("creditAdjust","graceDays::"+graceDays);
                                //Set the grace days into request map with key as 'relative_date_adjustment_supervision'
                                _requestMap.put("serviceFeeExpiryDateRelative",String.valueOf(graceDays));
                            }
                            catch(Exception e)
                            {
                                _log.errorTrace(methodName, e);
                                _log.error("creditAdjust","Exception e::"+e.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }
                        }//end of checking graceAdjustment

                        String inStr = _cs3cp6RequestFormatter.generateRequest(CS3CP6I.ACTION_IMMEDIATE_DEBIT,_requestMap);

//                      try{_ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID,"ADJ_RETRY_CNT"));}catch(Exception e){_ambgMaxRetryCount=1;}
             //sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS3MobililI interface
             sendRequestToIN(inStr,CS3CP6I.ACTION_IMMEDIATE_DEBIT);
             //set TRANSACTION_STATUS as Success in request map
             _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
             //set INTERFACE_POST_BALANCE into request map as obtained thru response map.
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
         }
        catch (BTSLBaseException be)
        {
                p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
                _log.error("creditAdjust","BTSLBaseException be:"+be.getMessage());
                if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                        throw be;
                try
                        {
                        _requestMap.put("TRANSACTION_TYPE","CR");
                        handleCancelTransaction();
                }
                catch(BTSLBaseException bte)
                        {
                                throw bte;
                        }
                        catch(Exception e)
                        {
                                 _log.errorTrace(methodName, e);
                                 _log.error("creditAdjust","Exception e:"+e.getMessage());
                                 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[creditAdjust]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                                 throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                }
         catch (Exception e)
         {
                 _log.errorTrace(methodName, e);
             _log.error("creditAdjust", "Exception e:"+e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[creditAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:"+e.getMessage());
             throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (_log.isDebugEnabled()) _log.debug("creditAdjust", "Exited _requestMap=" + _requestMap);
         }
	 */}//end of creditAdjust

        /**
         * Implements the logic that debit the subscriber account on IN.
         * @param   HashMap  p_requestMap
         * @throws  BTSLBaseException, Exception
         */
        public void debitAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
        {
            final String methodName = "debitAdjust";
            if (_log.isDebugEnabled())_log.debug(methodName," Entered p_requestMap:" + p_requestMap);
        _requestMap = p_requestMap;//assign map passed from InterfaceModule to _requestMap(instance var)
        double systemAmtDouble =0;
        String amountStr=null;
        int validityDays=0;//Defines the validity days by which adjustment to be made.
        int graceDays=0;//Defines the grace period by which adjustment to be made.
        try
                {
                _userType=(String)_requestMap.get("USER_TYPE");
                _interfaceID=(String)_requestMap.get("INTERFACE_ID");// get interface id form request map
                _inTXNID=getINTransactionID(_requestMap);//Generate the IN transaction id and set in _requestMap
                        _requestMap.put("IN_TXN_ID",_inTXNID);//get TRANSACTION_ID from request map (which has been passed by controller)
                        _requestMap.put("IN_RECON_ID",_inTXNID);

                        _referenceID=(String)_requestMap.get("TRANSACTION_ID");

                        _msisdn=(String)_requestMap.get("MSISDN");//get MSISDN from request map
                        //Get the Multiplication factor from the FileCache with the help of interface id.
                        String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
//                      isGetAccntDetailsRqd=FileCache.getValue(_interfaceID,"ACCOUNT_DETAILS_RQD");
                        if(InterfaceUtil.isNullString(multFactor))
                        {
                            _log.error("creditAdjust","MULT_FACTOR  is not defined in the INFile");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is  not defined in the INFile");
                            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                        multFactor = multFactor.trim();
                        //Set the interface parameters into requestMap
                        setInterfaceParameters();
                        _requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
                        try
                        {
                            double huaweiMultFactorDouble=Double.parseDouble(multFactor);
                                double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
                                systemAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceAmtDouble,huaweiMultFactorDouble);
                                amountStr=String.valueOf(systemAmtDouble);
                                //Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
                                String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
                                if(_log.isDebugEnabled()) _log.debug(methodName,"From file cache roundFlag = "+roundFlag);
                                //If the ROUND_FLAG is not defined in the INFile
                                if(InterfaceUtil.isNullString(roundFlag))
                                {
                                    roundFlag="Y";
                                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "CS3MobililINHandler[debitAdjust]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
                                }
                                //If rounding of amount is allowed, round the amount value and put this value in request map.
                                if("Y".equals(roundFlag.trim()))
                                {
                                    amountStr=String.valueOf(Math.round(systemAmtDouble));
                                        _requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
                                }
                        }
                        catch(Exception e)
                        {
                            _log.errorTrace(methodName, e);
                            _log.error(methodName,"Exception e:"+e.getMessage());
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
                            throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                        if(_log.isDebugEnabled()) _log.debug(methodName,"transfer_amount:"+amountStr+" multFactor:"+multFactor);
                        //set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
                        _requestMap.put("transfer_amount","-"+amountStr);

                        //Check for the validity Adjustment-VALIDITY_DAYS
                        if(!InterfaceUtil.isNullString((String)_requestMap.get("VALIDITY_DAYS")))
                        {
                            try
                            {
                                //For the adjust ment make validity days as negative.
                                validityDays =Integer.parseInt(((String)_requestMap.get("VALIDITY_DAYS")).trim());
                                if(_log.isDebugEnabled()) _log.debug(methodName,"validityDays::"+validityDays);
                                //Set the validity days into request map with key as 'relative_date_adjustment_service_fee'
                                _requestMap.put("supervisionExpiryDateRelative","-"+String.valueOf(validityDays));
                            }
                            catch(Exception e)
                            {
                                _log.errorTrace(methodName, e);
                                _log.error(methodName,"Exception e::"+e.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }
                        }//end of cheking validity days adjustment
                        //Check for the grace Adjustment-GRACE_DAYS
                        if(!InterfaceUtil.isNullString((String)_requestMap.get("GRACE_DAYS")))
                        {
                            try
                            {
                                //For the adjust ment make grace days as negative.
                                graceDays =Integer.parseInt(((String)_requestMap.get("GRACE_DAYS")).trim());
                                if(_log.isDebugEnabled()) _log.debug(methodName,"graceDays::"+graceDays);
                                //Set the grace days into request map with key as 'relative_date_adjustment_supervision'
                                _requestMap.put("serviceFeeExpiryDateRelative","-"+String.valueOf(graceDays));
                            }
                            catch(Exception e)
                            {
                                _log.errorTrace(methodName, e);
                                _log.error(methodName,"Exception e::"+e.getMessage());
                                throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                            }
                        }//end of checking graceAdjustment

                    String inStr = _ucip4xRequestFormatter.generateRequest(CS3CP6I.ACTION_IMMEDIATE_DEBIT,_requestMap);

//                      try{_ambgMaxRetryCount = Integer.parseInt(FileCache.getValue(_interfaceID,"ADJ_RETRY_CNT"));}catch(Exception e){_ambgMaxRetryCount=1;}
             //sending the CreditAdjust request to IN along with ACTION_IMMEDIATE_DEBIT action defined in CS3MobililI interface
             sendRequestToIN(inStr,CS3CP6I.ACTION_IMMEDIATE_DEBIT);
             //set TRANSACTION_STATUS as Success in request map
             _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
             //set INTERFACE_POST_BALANCE into request map as obtained thru response map.
                        _requestMap.put("POST_BALANCE_ENQ_SUCCESS", "N");
         }
        catch (BTSLBaseException be)
        {
                p_requestMap.put("AMBGUOUS_TIME",InterfaceUtil.getTimeWhenAmbiguousCaseOccured());
                _log.error(methodName,"BTSLBaseException be:"+be.getMessage());
                if(!(be.getMessage().equals(InterfaceErrorCodesI.AMBIGOUS)))
                        throw be;
                try
                        {
                        _requestMap.put("TRANSACTION_TYPE","DR");
                        handleCancelTransaction();
                }
                catch(BTSLBaseException bte)
                        {
                                throw bte;
                        }
                        catch(Exception e)
                        {
                                 _log.errorTrace(methodName, e);
                                 _log.error(methodName,"Exception e:"+e.getMessage());
                                 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[debitAdjust]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in creditAdjust");
                                 throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
                        }
                }
         catch (Exception e)
         {
                 _log.errorTrace(methodName, e);
             _log.error(methodName, "Exception e:"+e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while creditAdjust e:"+e.getMessage());
             throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (_log.isDebugEnabled()) _log.debug(methodName, "Exited _requestMap=" + _requestMap);
         
		 }
	 			}//end of debitAdjust.

	/**
	 * This method is used to send the request to IN and stored the response after parsing.
	 * This method also take care about to handle the errornious satuation to send the alarm and set the error code.
	 * 1.Invoke the getNodeVO method of NodeScheduler class and pass the Transaction Id.
	 * 2.If the VO is Null then mark the request as fail and throw exception(New Error code that defines No connection for any Node is available).
	 * 3.If the VO is not NULL then pass the Node detail to CS3UrlConnection class and get connection.
	 * 4.After the proccessing the request(may be successful or fail) decrement the connection counter and pass the
	 * transaction id that is removed from the transNodeList.
	 *
	 * @param   String p_inRequestStr
	 * @param   int p_action
	 * @throws  BTSLBaseException
	 */
	private void sendRequestToIN(String p_inRequestStr, int p_action) throws BTSLBaseException
	{

		final String methodName = "sendRequestToIN";
		if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_inRequestStr::"+p_inRequestStr+" p_action::"+p_action);
		TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"Request string::"+p_inRequestStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action::"+p_action);
		String responseStr = "";
		//NodeVO cs3cp6NodeVO=null;
		// NodeScheduler cs3cp6NodeScheduler=null;
		UCIP4xUrlConnection ucip4xURLConnection = null;
		long startTime=0;
		long endTime=0;
		//int conRetryNumber=0;
		long warnTime=0;
		/*int readTimeOut=0;*/
		//HashMap _responseMap1=null;
		String inReconID=null;
		//long retrySleepTime=0;

		String ucip4xIP = null;
		String ucip4xHostName = null;
		String ucip4xAirPWd = null;
		String ucip4xAirID = null;
		String ucip4xAgent = null;
		int readTimeOut = 0;
		int connectTimeOut = 0;
		String ucip4xKeepAliveFlag = null;
		long startTimeNode = 0;


		try
		{
			_responseMap = new HashMap();
			// _responseMap1= new HashMap();
			inReconID=(String)_requestMap.get("IN_RECON_ID");
			if(inReconID==null)
				inReconID=_inTXNID;

			ucip4xIP = (String)_requestMap.get("UCIP4x_AIR_IP");
			ucip4xAirID = (String)_requestMap.get("UCIP4x_AIR_ID");
			ucip4xAirPWd = (String)_requestMap.get("UCIP4x_AIR_PWD");
			ucip4xHostName = (String)_requestMap.get("UCIP4x_originHostName");
			ucip4xAgent = (String)_requestMap.get("UCIP4x_AIR_USER_AGENT");
			readTimeOut = Integer.parseInt((String)_requestMap.get("UCIP4x_HTTP_READ_TIME_VAL"));
			connectTimeOut = Integer.parseInt((String)_requestMap.get("UCIP4x_HTTP_CONNECT_TIME_VAL"));
			ucip4xKeepAliveFlag = (String)_requestMap.get("UCIP4x_KEEP_ALIVE");

			
			try
			{

				startTimeNode = System.currentTimeMillis();
			
				ucip4xURLConnection = new UCIP4xUrlConnection(ucip4xIP,ucip4xAirID,ucip4xAirPWd,connectTimeOut,readTimeOut,ucip4xKeepAliveFlag,p_inRequestStr.length(),ucip4xHostName,ucip4xAgent);
				//break the loop on getting the successfull connection for the node;
				if(_log.isDebugEnabled()) _log.debug(methodName,"Connection of _interfaceID ["+_interfaceID+"] created");
				//break;

				long totalTimeNode =System.currentTimeMillis()-startTimeNode;
				if(_log.isDebugEnabled())_log.debug(methodName,"Total time: "+totalTimeNode);
			}
			catch(BTSLBaseException be)
			{
				_log.error(methodName,"BTSLBaseException be::"+be.getMessage());
				throw be;//Confirm should we come out of loop or do another retry
			}//end of catch-BTSLBaseException
				 catch(Exception e)
			 {
				 _log.errorTrace(methodName, e);
				 EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn,(String) _requestMap.get("NETWORK_CODE"),"Error in sendRequestToIN for INTERFACE_ID=["+_interfaceID +"] , Exception ::"+e.getMessage());
				 _log.error(methodName,"Exception e::"+e.getMessage());
				 throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			 }//end of catch-Exception
			 try
			 {
				 PrintWriter out = ucip4xURLConnection.getPrintWriter();
				 out.flush();
				 startTime=System.currentTimeMillis();
				 _requestMap.put("IN_START_TIME",String.valueOf(startTime));
				 out.println(p_inRequestStr);
				 out.flush();
			 }
			 catch(Exception e)
			 {
				 _log.errorTrace(methodName, e);
				 _log.error(methodName,"Exception e::"+e);
				 EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),"While sending request to UCIP4x IN INTERFACE_ID=["+_interfaceID +"] , Exception::"+e.getMessage());
				 throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_REQ_NOT_SEND);
			 }
			 try
			 {
				 StringBuffer buffer = new StringBuffer();
				 String response = "";
				 try
				 {
					 //Get the response from the IN
					 ucip4xURLConnection.setBufferedReader();
					 BufferedReader in = ucip4xURLConnection.getBufferedReader();
					 //Reading the response from buffered reader.
					 while ((response = in.readLine()) != null)
					 {
						 buffer.append(response);
					 }


					 endTime=System.currentTimeMillis();
					/* if(warnTime<=(endTime-startTime))
					 {
						 _log.info("sendRequestToIN", "WARN time reaches, startTime::"+startTime+" endTime::"+endTime+" From file cache warnTime::"+warnTime+ " time taken (endTime-startTime)::"+(endTime-startTime));
						 EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.MAJOR,"UCIP4xINHandler[sendRequestToIN]",_inTXNID,_msisdn,(String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"UCIP4xIN is taking more time than the warning threshold. Time: "+(endTime-startTime)+"INTERFACE_ID=["+_interfaceID +"]");
					 }*/
				 }
				 catch(Exception e)
				 {
					 _log.errorTrace(methodName, e);
					 _log.error(methodName,"Exception e::"+e.getMessage());
					 EventHandler.handle(EventIDI.INTERFACE_REQUEST_EXCEPTION,EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"),"While getting the response from the UCIP4xIN for INTERFACE_ID=["+_interfaceID +"] , Exception="+e.getMessage());
					 //_nodeCloser.updateCountersOnAmbiguousResp(cs3cp6NodeVO);
					 //sayyed yasin 
					 if(!BTSLUtil.isNullString(p_inRequestStr))
					 {
						 int indexStart = p_inRequestStr.indexOf("<methodName>");
						 int indexEnd = p_inRequestStr.indexOf("</methodName>",indexStart);
						 String requestActionName = p_inRequestStr.substring("<methodName>".length()+indexStart,indexEnd);

						 if( ("Refill".equals(requestActionName) ||  p_action == UCIP4xI.ACTION_RECHARGE_CREDIT)
								 && (System.currentTimeMillis() - startTime)  > readTimeOut)
						 {
							 _log.error(methodName,"Exception on Refill request : "+ e.getMessage() +" Response received from IN :"+ buffer.toString());
							 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
							 throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
						 }
					 }
				 }//end of catch-Exception
				 finally
				 {
					 if(endTime==0) endTime=System.currentTimeMillis();
					 _requestMap.put("IN_END_TIME",String.valueOf(endTime));
					 _log.error(methodName,"Request sent to IN at:"+startTime+" Response received from IN at:"+endTime+" defined read time out is:"+readTimeOut);
				 }//end of finally
				 responseStr = buffer.toString();
				 XmlValidateParser xmv = new XmlValidateParser();

				 valuesHash=xmv.parsevalidatexml(new BufferedReader(new StringReader(responseStr)));

				 if(valuesHash.size()<=0){
					 _log.info(methodName, "MSISDN = "+_msisdn+" ::Response from IN is not OK");
				 }
				 else{
					 CommonFunc commonFunc = new CommonFunc();
					 pamimessage = commonFunc.getHashToString(valuesHash);
				 }

				 TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
				 if (_log.isDebugEnabled())_log.debug("sendRequestToIN", "responseStr::" + responseStr);
				 //TransactionLog.log(_inTXNID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"Response string:"+responseStr,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action="+p_action);
				 String httpStatus = ucip4xURLConnection.getResponseCode();
				 _requestMap.put("PROTOCOL_STATUS", httpStatus);
				 if(!UCIP4xI.HTTP_STATUS_200.equals(httpStatus))
					 throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_BAD_REQUEST);
				 //Check if there is no response, handle the event showing Blank response from CS3 stop further processing.
				 if (InterfaceUtil.isNullString(responseStr))
				 {
					 EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Blank response from UCIP4xIN");
					 _log.error("sendRequestToIN", "NULL response from interface");
					 //_nodeCloser.updateCountersOnAmbiguousResp(cs3cp6NodeVO);
					 _requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);	       
					 throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				 }

				 /*if(cs3cp6NodeVO.isSuspended())
                                _nodeCloser.resetCounters(cs3cp6NodeVO);*/
				 _responseMap=_ucip4xResponseParser.parseResponse(p_action,pamimessage);

				 //Here the various checks would be done based on the response.
				 //Check the fault code if it is not null then handle the event with message as fault string and error code.
				 //First check whether the responseCode is null
				 //If the response code is null,check the fault code,if present get the fault string and
				 //a.throw the exception with error code INTERFACE_PROCESS_REQUEST_ERROR.
				 //b.Handle the event with Level FATAL and message as fault strring
				 //1.If the responseCode is other than 0
				 //a.check if the code is 102 then throw BTSLBaseException
				 //2.If the responseCode is 0 then checks the following.


				 /*String faultCode = (String)_responseMap.get("faultCode");
                            if(!InterfaceUtil.isNullString(faultCode))
                            {
                                //Log the value of executionStatus for corresponding msisdn,recieved from the response.
                                _log.info("sendRequestToIN", "faultCode::"+faultCode +"_inTXNID::"+_inTXNID+" _msisdn::"+_msisdn);
                                _requestMap.put("INTERFACE_STATUS", faultCode);//Put the interface_status in requestMap
                                _log.error("sendRequestToIN","faultCode="+_responseMap.get("faultCode")+"faultString = "+_responseMap.get("faultString"));
                                EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL,"CS3CP6INHandler[sendRequestToIN]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,(String)_responseMap.get("faultString"));
                                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                            }*/
				 //Check if responseCode=102,then throw exception with message INTERFACE_MSISDN_NOT_FOUND.
				 String responseCode = (String)_responseMap.get("responseCode");
				 _requestMap.put("INTERFACE_STATUS",responseCode);

				 String tempResponseStr=new String();
				tempResponseStr="{ ";
				tempResponseStr=tempResponseStr + " Stage = "+p_action;
				tempResponseStr=tempResponseStr + " transactionId="+_requestMap.get("TRANSACTION_ID");
				tempResponseStr=tempResponseStr + " MSISDN="+_requestMap.get("MSISDN");
				tempResponseStr=tempResponseStr + " senderMSISDN="+_requestMap.get("SENDER_MSISDN");
				tempResponseStr=tempResponseStr + " Balance="+_responseMap.get("accountValue1");
				if(String.valueOf(p_action).equalsIgnoreCase("1"))
				{
					tempResponseStr=tempResponseStr + " supervisionExpiryDate="+_responseMap.get("supervisionExpiryDate");
					tempResponseStr=tempResponseStr + " serviceFeeExpiryDate="+_responseMap.get("serviceFeeExpiryDate");
					tempResponseStr=tempResponseStr + " serviceRemovalDate="+_responseMap.get("serviceRemovalDate");
					//tempResponseStr=tempResponseStr + "supervisionExpiryDate="+_responseMap.get("supervisionExpiryDate");
					tempResponseStr=tempResponseStr + " serviceClass="+_responseMap.get("serviceClassCurrent");
				}
				
				tempResponseStr=tempResponseStr + " responseCode="+_responseMap.get("responseCode");
				tempResponseStr=tempResponseStr+" }";

				 InterfaceRequestResponseLog.log(ucip4xIP,_requestMap,p_inRequestStr,tempResponseStr);
					

				 Object[] successList=UCIP4xI.RESULT_OK.split(",");

				 //if(!CS3CP6I.RESULT_OK.equals(responseCode))
				 if(!Arrays.asList(successList).contains(responseCode))
				 {
					 if(UCIP4xI.SUBSCRIBER_NOT_FOUND.equals(responseCode))
					 {
						 _log.error(methodName,"Subscriber not found with MSISDN::"+_msisdn);
						 EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Subscriber is not found at IN");
						 throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
					 }//end of checking the subscriber existance.
					 else if(UCIP4xI.SUBSCRIBER_BARRED.equals(responseCode))
					 {
						 _log.error(methodName,"Subscriber barred at IN::"+_msisdn);
						 EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Subscriber BARRED at IN");
						 throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);
					 }//end of checking the subscriber existance.                                    
					 else
					 {
						 _log.error(methodName,"Error code received from IN ::"+responseCode);
					//	 EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,_interfaceID, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO,"UCIP4xINHandler["+methodName+"]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action,"Error code received from IN "+responseCode);
						 throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
					 }

				 }
			 }
			 catch(BTSLBaseException be)
			 {
				 throw be;
			 }//end of catch-BTSLBaseException
			 catch(Exception e)
			 {
				 _log.errorTrace(methodName, e);
				 _log.error(methodName,"Exception e::"+e.getMessage());
				 throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			 }//end of catch-Exception


		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName,"BTSLBaseException be::"+be.getMessage());
			throw be;
		}//end of catch-BTSLBaseException
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName,"Exception e::"+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			try
			{
				//Closing the HttpUrl connection
				if (ucip4xURLConnection != null) ucip4xURLConnection.close();
				/*if(cs3cp6NodeVO!=null)
			 {
				 _log.info("sendRequestToIN","Connection of Node ["+cs3cp6NodeVO.getNodeNumber()+"] for INTERFACE_ID="+_interfaceID+" is closed");
				 //Decrement the connection number for the current Node.
				 cs3cp6NodeVO.decrementConNumber(inReconID);
				 _log.info("sendRequestToIN","After closing the connection for Node ["+cs3cp6NodeVO.getNodeNumber()+"] USED connections are ::["+cs3cp6NodeVO.getConNumber()+"]");
			 }*/
			}
			catch (Exception e)
			{
				_log.errorTrace(methodName, e);
				_log.error(methodName, "While closing CS3CP6IN Connection Exception e::" + e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE")+" INTERFACE ID = "+_interfaceID+" Stage = "+p_action, "Not able to close connection:" + e.getMessage()+"INTERFACE_ID=["+_interfaceID +"]");
			}
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Exiting  _interfaceID::"+_interfaceID+" Stage::"+p_action + " responseStr::" + responseStr);
		}//end of finally

	}//end of sendRequestToIN

	/**
	 * This method would be used to adjust the validity of the subscriber account at the IN.
	 * @param       HashMap p_requestMap
	 * @throws      BTSLBaseException, Exception
	 */
	public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception
	{}//end of validityAdjust

	/**
	 * This method is used to set the interface parameters into requestMap, these parameters are as bellow
	 * 1.Origin node type.
	 * 2.Origin host type.
	 * @throws Exception
	 */
	private void setInterfaceParameters() throws Exception,BTSLBaseException
	{
		final String methodName = "setInterfaceParameters";
		if(_log.isDebugEnabled())_log.debug("setInterfaceParameters","Entered");
		try
		{
			//Added by Vedant for UCIP4x
			String ucip4xAirIP = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_IP");
			if(InterfaceUtil.isNullString(ucip4xAirIP))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_IP is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_IP is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_AIR_IP",ucip4xAirIP.trim());

			String ucip4xAirID = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_ID");
			if(InterfaceUtil.isNullString(ucip4xAirID))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_ID is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_ID is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_AIR_ID",ucip4xAirID.trim());

			String transactionType = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_TRANSACTION_TYPE");
			if(InterfaceUtil.isNullString(transactionType))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_TRANSACTION_TYPE is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_TRANSACTION_TYPE is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("TRANSACTION_TYPE",transactionType.trim());
			
			String refillDetailFlag = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_REFILL_DETAILS_FLAG");
			if(InterfaceUtil.isNullString(transactionType))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_REFILL_DETAILS_FLAG is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_REFILL_DETAILS_FLAG is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("REFILL_DETAILS_FLAG",refillDetailFlag.trim());
			
			
			String ucip4xAirPwd = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_PWD");
			if(InterfaceUtil.isNullString(ucip4xAirPwd))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_PWD is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_PWD is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_AIR_PWD",ucip4xAirPwd.trim());

			String ucip4xAirUserAgent = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_USER_AGENT");
			if(InterfaceUtil.isNullString(ucip4xAirUserAgent))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_USER_AGENT is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_USER_AGENT is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_AIR_USER_AGENT",ucip4xAirUserAgent.trim());

			String ucip4xOriginNodeType = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_originNodeType");
			if(InterfaceUtil.isNullString(ucip4xOriginNodeType))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_originNodeType is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_originNodeType is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_originNodeType",ucip4xOriginNodeType.trim());

			String ucip4xOriginHostName = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_originHostName");
			if(InterfaceUtil.isNullString(ucip4xOriginHostName))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_originHostName is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_originHostName is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_originHostName",ucip4xOriginHostName.trim());

			String ucip4xAirBarFlag = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_BAR_FLAG");
			if(InterfaceUtil.isNullString(ucip4xAirBarFlag))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_BAR_FLAG is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_AIR_BAR_FLAG is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_AIR_BAR_FLAG",ucip4xAirBarFlag.trim());

			String ucip4xAirExpiry = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"AIR_EXPIRY");
			if(InterfaceUtil.isNullString(ucip4xAirExpiry))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"AIR_EXPIRY is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"AIR_EXPIRY is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("AIR_EXPIRY",ucip4xAirExpiry.trim());

			String ucip4xHttpConnectTimeVal = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_HTTP_CONNECT_TIME_VAL");
			if(InterfaceUtil.isNullString(ucip4xHttpConnectTimeVal))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_HTTP_CONNECT_TIME_VAL is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_HTTP_CONNECT_TIME_VAL is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_HTTP_CONNECT_TIME_VAL",ucip4xHttpConnectTimeVal.trim());

			/*String ucip4xHttpConnectTimeTop = FileCache.getValue(_interfaceID,"UCIP4x_HTTP_CONNECT_TIME_TOP");
                    if(InterfaceUtil.isNullString(ucip4xHttpConnectTimeTop))
                    {
                        _log.error(methodName,"Value of UCIP4x_HTTP_CONNECT_TIME_TOP is not defined in the INFile");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "UCIP4x_HTTP_CONNECT_TIME_TOP is not defined in the INFile.");
                            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("UCIP4x_HTTP_CONNECT_TIME_TOP",ucip4xHttpConnectTimeTop.trim());*/

			String ucip4xHttpReadTimeVal = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_HTTP_READ_TIME_VAL");
			if(InterfaceUtil.isNullString(ucip4xHttpReadTimeVal))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_HTTP_CONNECT_TIME_TOP is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_HTTP_READ_TIME_VAL is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_HTTP_READ_TIME_VAL",ucip4xHttpReadTimeVal.trim());

			/*String ucip4xHttpReadTimeTop = FileCache.getValue(_interfaceID,"UCIP4x_HTTP_READ_TIME_TOP");
                    if(InterfaceUtil.isNullString(ucip4xHttpReadTimeTop))
                    {
                        _log.error(methodName,"Value of UCIP4x_HTTP_READ_TIME_TOP is not defined in the INFile");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "UCIP4x_HTTP_READ_TIME_TOP is not defined in the INFile.");
                            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                    }
                    _requestMap.put("UCIP4x_HTTP_READ_TIME_TOP",ucip4xHttpReadTimeTop.trim());*/

			String ucip4xIPBlockTime = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_IP_BLOCK_TIME");
			if(InterfaceUtil.isNullString(ucip4xIPBlockTime))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_IP_BLOCK_TIME is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_IP_BLOCK_TIME is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_IP_BLOCK_TIME",ucip4xIPBlockTime.trim());

			String ucip4xIPBlockCount = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_IP_BLOCK_COUNT");
			if(InterfaceUtil.isNullString(ucip4xIPBlockCount))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_IP_BLOCK_COUNT is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_IP_BLOCK_COUNT is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_IP_BLOCK_COUNT",ucip4xIPBlockCount.trim());

			String ucip4xMsgCapabilityFlag = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_MessageCapabilityFlag");
			if(InterfaceUtil.isNullString(ucip4xMsgCapabilityFlag))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_MessageCapabilityFlag is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), ""+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_MessageCapabilityFlag is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_MessageCapabilityFlag",ucip4xMsgCapabilityFlag.trim());

			String ucip4xRR = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_RR");
			if(InterfaceUtil.isNullString(ucip4xRR))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_RR is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "UCIP4x_RR is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_RR",ucip4xRR.trim());

			String ucip4xExtData2ValReq = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_EXTERNAL_DATA2_VALUE_REQ");
			if(InterfaceUtil.isNullString(ucip4xExtData2ValReq))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_EXTERNAL_DATA2_VALUE_REQ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "UCIP4x_EXTERNAL_DATA2_VALUE_REQ is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_EXTERNAL_DATA2_VALUE_REQ",ucip4xExtData2ValReq.trim());

			String ucip4xTransCurrency = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_transactionCurrency");
			if(InterfaceUtil.isNullString(ucip4xTransCurrency))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_EXTERNAL_DATA2_VALUE_REQ is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "UCIP4x_transactionCurrency is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_transactionCurrency",ucip4xTransCurrency.trim());

			String ucip4xKeepAliveFlag = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_KEEP_ALIVE");
			if(InterfaceUtil.isNullString(ucip4xKeepAliveFlag))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_KEEP_ALIVE is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "UCIP4x_KEEP_ALIVE is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP4x_KEEP_ALIVE",ucip4xKeepAliveFlag.trim());
			
			
			String ucip4xMsisdnPrefixAllowed = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_UCIP_MSISDNPREEFIX91_ALLOWED");
			if(InterfaceUtil.isNullString(ucip4xMsisdnPrefixAllowed))
			{
				_log.error(methodName,"Value of "+_requestMap.get("NETWORK_CODE")+"_"+"_UCIP_MSISDNPREEFIX91_ALLOWED is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), (String) _requestMap.get("NETWORK_CODE")+"_UCIP_MSISDNPREEFIX91_ALLOWED is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("UCIP_MSISDNPREEFIX91_ALLOWED",ucip4xMsisdnPrefixAllowed.trim());
			
			String countryCode = FileCache.getValue(_interfaceID,"COUNTRY_CODE");
			if(InterfaceUtil.isNullString(countryCode))
			{
				_log.error(methodName,"Value of "+ "COUNTRY_CODE is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "UCIP4xINHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "COUNTRY_CODE is not defined in the INFile.");
				throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
			}
			_requestMap.put("COUNTRY_CODE",countryCode.trim());
			
			
			String cancelTxnAllowed = FileCache.getValue(_interfaceID,"CANCEL_TXN_ALLOWED");
                        if(InterfaceUtil.isNullString(cancelTxnAllowed))
                        {
                            _log.error(methodName,"Value of CANCEL_TXN_ALLOWED is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
                                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());

                        String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
                        if(InterfaceUtil.isNullString(systemStatusMappingCr))
                        {
                            _log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
                                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());

                        /*String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
                        if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
                        {
                            _log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
                                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());

                        String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
                        if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
                        {
                            _log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
                                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());

                        String systemStatusMappingCrBck = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
                        if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
                        {
                            _log.error(methodName,"Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
                                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());

                        String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
                        if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
                        {
                            _log.error(methodName,"Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
                                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());


                        String cancelNA = FileCache.getValue(_interfaceID,"CANCEL_NA");
                        if(InterfaceUtil.isNullString(cancelNA))
                        {
                            _log.error(methodName,"Value of CANCEL_NA is not defined in the INFile");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
                                throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("CANCEL_NA",cancelNA.trim());

                        String warnTimeStr=(String)FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
                        if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
                        {
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
                                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("WARN_TIMEOUT",warnTimeStr.trim());

                        String nodeType=FileCache.getValue(_interfaceID, "NODE_TYPE");
                        if(InterfaceUtil.isNullString(nodeType))
                        {
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "NODE_TYPE is not defined in IN File ");
                                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("NODE_TYPE",nodeType.trim());

                                String hostName=FileCache.getValue(_interfaceID, "HOST_NAME");
                                if(InterfaceUtil.isNullString(hostName))
                                {
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "HOST_NAME is not defined in IN File ");
                                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                                }
                                _requestMap.put("HOST_NAME",hostName.trim());

                                String subscriberNAI=FileCache.getValue(_interfaceID, "NAI");
                                if(InterfaceUtil.isNullString(subscriberNAI)||!InterfaceUtil.isNumeric(subscriberNAI))
                                {
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "NAI is not defined in IN File or not numeric");
                                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                                }
                                _requestMap.put("SubscriberNumberNAI",subscriberNAI.trim());*/

                                String currency=FileCache.getValue(_interfaceID, "CURRENCY");
                                if(InterfaceUtil.isNullString(currency))
                                {
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "CURRENCY is not defined in IN File ");
                                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                                }
                                _requestMap.put("CURRENCY",currency.trim());

              /*                  String RefillAccountAfterFlag=FileCache.getValue(_interfaceID, "REFILL_ACNT_AFTER_FLAG");
                                if(InterfaceUtil.isNullString(RefillAccountAfterFlag))
                                {
                                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "REFILL_ACNT_AFTER_FLAG is not defined in IN File ");
                                    throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                                }
                                _requestMap.put("REFILL_ACNT_AFTER_FLAG",RefillAccountAfterFlag.trim());

                                String extData1=FileCache.getValue(_interfaceID, "EXTERNAL_DATA1");
                                if(InterfaceUtil.isNullString(extData1))
                                {
                                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "EXTERNAL_DATA1 is not defined in IN File ");

                                }
                                _requestMap.put("EXTERNAL_DATA1",extData1.trim());*/

                        String extData2=FileCache.getValue(_interfaceID, "EXTERNAL_DATA2");
                        if(InterfaceUtil.isNullString(extData2))
                        {
                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "CS3CP6INHandler["+methodName+"]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "EXTERNAL_DATA2 is not defined in IN File ");

                        }
                        _requestMap.put("EXTERNAL_DATA2",extData2.trim());
                        String msisdnprefixallowedreversal = FileCache.getValue(_interfaceID,_requestMap.get("NETWORK_CODE")+"_"+"UCIP4x_MSISDNPREEFIX91_ALLOWED_DEBIT");
                        if(InterfaceUtil.isNullString(msisdnprefixallowedreversal))
                        {
                        	_log.error(methodName,"Value of UCIP4x_MSISDNPREEFIX91_ALLOWED_DEBIT is not defined in the INFile");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "UCIP4x_MSISDNPREEFIX91_ALLOWED_DEBIT is not defined in the INFile.");
                            throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
                        }
                        _requestMap.put("UCIP4x_MSISDNPREEFIX91_ALLOWED_DEBIT",msisdnprefixallowedreversal.trim());

            }//end of try block
            catch(BTSLBaseException be)
            {
                throw be;
            }
            catch(Exception e)
            {
                _log.error(methodName,"Exception e="+e.getMessage());
                throw e;
            }//end of catch-Exception
            finally
            {
                if (_log.isDebugEnabled())_log.debug(methodName, "Exited _requestMap:" + _requestMap);
            }//end of finally
        }//end of setInterfaceParameters



	/**
	 * Method to send cancel request to IN for any ambiguous transaction.
	 * This method also makes reconciliation log entry.
	 * @throws      BTSLBaseException
	 */
	private void handleCancelTransaction() throws BTSLBaseException
	{
		final String methodName = "handleCancelTransaction";
		
		if (_log.isDebugEnabled())_log.debug(methodName, "Entered.");
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
			if (_log.isDebugEnabled())_log.debug(methodName, "reconLog."+reconLog);
			cancelTxnAllowed=(String)_requestMap.get("CANCEL_TXN_ALLOWED");
			//if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
			//into recon log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
			if("N".equals(cancelTxnAllowed))
			{
				cancelNA=(String)_requestMap.get("CANCEL_NA");//Cancel command status as NA.
				cancelCommandStatus=InterfaceUtil.getErrorCodeFromMapping(_requestMap,cancelNA,"CANCEL_COMMAND_STATUS_MAPPING");
				_requestMap.put("MAPPED_CANCEL_STATUS",cancelCommandStatus);
				interfaceStatus=(String)_requestMap.get("INTERFACE_STATUS");
				systemStatusMapping=(String)_requestMap.get("SYSTEM_STATUS_MAPPING");
				cancelTxnStatus = InterfaceUtil.getErrorCodeFromMapping(_requestMap,interfaceStatus,systemStatusMapping); //PreTUPs Transaction status as FAIL/AMBIGUOUS based on value of SYSTEM_STATUS_MAPPING
				_requestMap.put("MAPPED_SYS_STATUS",cancelTxnStatus);
				reconciliationLogStr = ReconcialiationLog.getReconciliationLogFormat(_requestMap);
				reconLog.info("",reconciliationLogStr);
				if(!InterfaceErrorCodesI.SUCCESS.equals(cancelTxnStatus))
					throw new BTSLBaseException(this,methodName,cancelTxnStatus);  ////Based on the value of SYSTEM_STATUS mark the transaction as FAIL or AMBIGUOUS to the system.(//should these be put in error log also.   ??????)
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
			_log.errorTrace(methodName, e);
			_log.error(methodName,"Exception e:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if (_log.isDebugEnabled())_log.debug(methodName, "Exited");
		}
	}



	/**
	 * This method is used to get the interface amount ,multiplied by mult factor.
	 * @param	HashMap p_map
	 * @return	String
	 * @throws	BTSLBaseException
	 */
	private double getInterfaceAmount(HashMap p_map) throws BTSLBaseException
	{
		final String methodName = "getInterfaceAmount";
		
		if(_log.isDebugEnabled()) _log.debug(methodName,"Entered p_map = "+p_map);
		String interfaceAmountStr = null;
		double multFactorDouble=0;
		double interfaceAmtDouble=0;
		double bonusAmtDouble=0;

		try
		{
			interfaceAmtDouble = Double.parseDouble((String)p_map.get("INTERFACE_AMOUNT"));

			// on the base of method type and requestedamountflag it will be decide whether 
			// requested amount to be sent or calculated amount to be sent to IN.  
			// this all will be done becuase the cardgroup will be used only for reporting purpose.

			if("Y".equals(FileCache.getValue(_interfaceID,"REQUESTED_AMOUNT_FLAG"))) 
			{
				interfaceAmtDouble = Double.parseDouble((String)p_map.get("REQUESTED_AMOUNT"));
				_interfaceBonusValue=FileCache.getValue(_interfaceID, "INTFCE_BONUS_REQUIRED");
				if("Y".equals(_interfaceBonusValue))
				{
					bonusAmtDouble=Double.parseDouble((String)p_map.get("BONUS_AMOUNT"));
					interfaceAmtDouble=interfaceAmtDouble+bonusAmtDouble;
				}
			}
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error(methodName,"Exception e = "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UCIP4xINHandler["+methodName+"]",(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("MSISDN"),(String)p_map.get("NETWORK_CODE"),"System Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}
		finally
		{
			if(_log.isDebugEnabled())_log.debug(methodName,"Exiting interfaceAmountStr = "+interfaceAmountStr);
		}
		return interfaceAmtDouble;
	}	

	protected static synchronized String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException
	{
		final String methodName = "getINTransactionID";
		
		String transactionId=null;
		String inNetCode=null;
		String netCode=null;
		try
		{
			transactionId = (String)p_requestMap.get("TRANSACTION_ID");
		/*	netCode=(String)p_requestMap.get("NETWORK_CODE");
			inNetCode=FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),netCode);
			if(InterfaceUtil.isNullString(inNetCode))
			{
				_log.error(methodName,"Mapping of Netowrk code " +netCode +" is not defined in the INFile");
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","","" , (String) p_requestMap.get("NETWORK_CODE"), "Mapping of Netowrk code " +netCode +" is not defined in the INFile");
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}*/
			//transactionId=inNetCode+transactionId.substring(2,transactionId.length());   
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
		}
		finally
		{
		}
		return transactionId;
	}


	/*protected static synchronized String getINTransactionID(HashMap p_requestMap) throws BTSLBaseException
        {
	        String instanceID=null;
	        int MAX_COUNTER=9999;
	        int inTxnLength=4;
	        String serviceType=null;
	        String userType=null;
	        Date mydate =null;
	        String minut2Compare=null;
	        String dateStr=null;
	        String transactionId=null;
            try
            {
                    //<service type>YYMMDD.HHmm.<instance code>XXXX<subscriber type>
                    serviceType = (String)p_requestMap.get("REQ_SERVICE");
                    if("RC".equals(serviceType))
                            serviceType="8";
                    else if("PRC".equals(serviceType) || "PCR".equals(serviceType) || "ACCINFO".equals(serviceType)  )
                            serviceType="6";

                    userType = (String)p_requestMap.get("USER_TYPE");
                    if("S".equals(userType))
                            userType="3";
                    else if("R".equals(userType))
                            userType="2";


                    instanceID = FileCache.getValue((String)p_requestMap.get("INTERFACE_ID"),"INSTANCE_ID");
	                if(InterfaceUtil.isNullString(instanceID))
	                {
	                    _log.error(methodName,"Parameter INSTANCE_ID is not defined in the INFile");
	                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CS3CP6INHandler["+methodName+"]","","" , (String) p_requestMap.get("NETWORK_CODE"), "Instance id[INSTANCE_ID] is not defined in IN File");
	                    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
	                }

                    mydate = new Date();
                    dateStr = _sdf.format(mydate);
                    minut2Compare = dateStr.substring(8,10);
                    int currentMinut=Integer.parseInt(minut2Compare);

                    if(currentMinut !=_prevMinut)
                    {
                            _transactionIDCounter=1;
                            _prevMinut=currentMinut;
                    }
                    else if(_transactionIDCounter > MAX_COUNTER)
                            _transactionIDCounter=1;
                    else
                            _transactionIDCounter++;

                    String txnid =String.valueOf(_transactionIDCounter);

                    int length = txnid.length();
                    int tmpLength=inTxnLength-length;
                    if(length<inTxnLength)
                    {
                            for(int i=0;i<tmpLength;i++)
                                    txnid = "0"+txnid;
                    }

                    transactionId = serviceType+ dateStr+instanceID+txnid+userType;
                }
                catch(Exception e)
                {
                        _log.errorTrace(methodName, e);
                }
                finally
                {
                }
	            return transactionId;
        }*/
}

