package com.inter.zteocm.zteocmdata;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


import org.apache.axis.client.Call;

import java.util.Collections;
import java.util.Comparator;

import com.btsl.pretups.common.PretupsErrorCodesI;
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
import com.inter.zteocm.ZTEOCMWebServiceConnector;


public class CP2PDataHandler implements InterfaceHandler{

	private Log _log = LogFactory.getLog(CP2PDataHandler.class.getName());
	private CP2PDataReqResFormatter _formatter=null;
    private HashMap<String,String> _requestMap = null;//Contains the request parameter as key and value pair.
    private HashMap<String,String> _responseMap = null;//Contains the response of the request as key and value pair.
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
	private String dateINFormate = "yyyy-MM-dd";
	ArrayList<String> _bundleDataList = new ArrayList<String>();
/**
 *  Constructor
 */
	public CP2PDataHandler() {
		_formatter=new CP2PDataReqResFormatter();
		// TODO Auto-generated constructor stub
	}
/**
 * validate Method is used for getting the account information of sender(CP2P) and receiver(CP2P & RP2P).
 * @param p_map	HashMap
 * @throws BTSLBaseException,Exception
 */	
	public void validate(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception
	{
	
	
        if(_log.isDebugEnabled()) _log.debug("validate","Entered p_requestMap:"+p_requestMap);
        _requestMap = p_requestMap;
		String receiverBundleId = (String)_requestMap.get("RECEIVER_BUNDLE");
		    
    try
        {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_userType=(String)_requestMap.get("USER_TYPE");
			String senderDataBundle = FileCache.getValue(_interfaceID,"SENDER_DATA_BUNDLES");
			dateINFormate = FileCache.getValue(_interfaceID,"DATE_FORMATE");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	        	
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
 			_msisdn=(String)_requestMap.get("MSISDN"); 			
			//Fetch value of VALIDATION key from IN File. (this is used to ensure that validation will be done on IN or not) 
			//If validation of subscriber is not required set the SUCCESS code into request map and return.
			String validateRequired = FileCache.getValue(_interfaceID,_requestMap.get("REQ_SERVICE")+"_"+_requestMap.get("USER_TYPE"));
			//String validateRequired="Y";
 			if("N".equals(validateRequired))
			{
			    _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
			    return ;
			}
			_inTXNID=getINTransactionID(_requestMap,null);			
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			String dataMultFactor = FileCache.getValue(_interfaceID,"DATA_MULT_FACTOR");
			String dataDivideFactor = FileCache.getValue(_interfaceID,"DATA_DIVIDE_FACTOR");
			if(_log.isDebugEnabled()) _log.debug("validate","multFactor: "+multFactor);
			if(InterfaceUtil.isNullString(multFactor) || InterfaceUtil.isNullString(dataMultFactor)  || InterfaceUtil.isNullString(dataDivideFactor) )
			{
			    _log.error("validate","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor=multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);
			dataMultFactor=dataMultFactor.trim();
			_requestMap.put("DATA_MULT_FACTOR",dataMultFactor);
			dataDivideFactor=dataDivideFactor.trim();
			_requestMap.put("DATA_DIVIDE_FACTOR",dataDivideFactor);
			  if(_log.isDebugEnabled()) _log.debug("validate","Multi Factor "+multFactor+"data Mult  "+dataMultFactor+" Data Divide "+dataDivideFactor);
            //Set the interface parameters into requestMap
			setInterfaceParameters(CP2PDataI.ACTION_ACCOUNT_INFO);
			//generate Request Object
			String requestStr = _formatter.generateRequest(CP2PDataI.ACTION_ACCOUNT_INFO,_requestMap);
			//sending the AccountInfo request to IN along with validate action defined in Huawei84I interface
            sendRequestToIN(CP2PDataI.ACTION_ACCOUNT_INFO,requestStr);
            if(_log.isDebugEnabled()) _log.debug("Response Map Returned from IN After Validate : ",_responseMap);
            //Check whether the subscriber is black listed on the IN or not.
            String stateSetBits = (String)_responseMap.get("resp_stateSet");
            String checkBlackList=FileCache.getValue(_interfaceID,"BLACKLIST_CHECK");
            if("Y".equals(checkBlackList))
            {
            	char eighthChar = stateSetBits.charAt(7);
            	char tenthChar = stateSetBits.charAt(9);
            	//If StateSet received from IN is having 8th and 10th bit as 1, the subscriber should not be allowed to recharge.
                if('1'== eighthChar || '1'==tenthChar) 
    			{
    				if(_log.isDebugEnabled())_log.debug("validate","Subscriber is blacklisted on IN. Either State Set for eighth bit or tenth bit is 1. "+InterfaceUtil.getPrintMap(_requestMap));
    				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_BARRED);//The MSISDN of the request is not valid on Prepaid system.
    			}
            }
            
            String serviceType=(String)p_requestMap.get("REQ_SERVICE");
			_userType=(String)p_requestMap.get("USER_TYPE");
			System.out.println(serviceType);
			if(!(("PRC".equals(serviceType) || "CDATA".equals(serviceType)) || ("CPN".equals(serviceType))) && "S".equals(_userType))
			{
			    String selectorBundleId=((String)_requestMap.get("SELECTOR_BUNDLE_ID")).trim();
		        String selectorBundleCode=FileCache.getValue(_interfaceID, selectorBundleId.trim());
		        if(!InterfaceUtil.isNullString(selectorBundleCode))
		        	selectorBundleCode=selectorBundleCode.trim();
		       	String receivedBundles = (String) _responseMap.get("received_bundles");
		       	System.out.println("selectorBundleCode"+selectorBundleCode);
		       	System.out.println("selectorBundleId"+selectorBundleId);
		       	System.out.println("receivedBundles"+receivedBundles);		       	
		        if(InterfaceUtil.isNullString(selectorBundleCode) || ((!"1".equals(selectorBundleId) && InterfaceUtil.isNullString(receivedBundles)) || (!"1".equals(selectorBundleId) && !(receivedBundles).contains(selectorBundleCode))))
		        {
		        	_log.error("validate: ","Subscriber "+ (String)p_requestMap.get("MSISDN") + "does not have "+ selectorBundleCode + "bundle assigned.");
		        	EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "AlcatelOCI452INHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Subscriber "+ (String)p_requestMap.get("MSISDN") + "does not have "+ selectorBundleCode + "bundle assigned.");
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
		        }
		        /*if("1".equals(selectorBundleId))
				{
					_requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_val_date"), PretupsI.DATE_FORMAT_DDMMYYYY));
		            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("end_inact_date"), PretupsI.DATE_FORMAT_DDMMYYYY));	            
				}*/
		        _requestMap.put("IN_RESP_BUNDLE_CODES", receivedBundles);
			}
			
			if (_log.isDebugEnabled()) _log.debug("validate ", "resp_valData_"+" : "+_responseMap.get("resp_valData_"+receiverBundleId));
			String[] responseDataStr = "0:null:null:null".split(":");
			_responseMap.put("RECEIVED_BUNDLES","");
		
			if("S".equalsIgnoreCase(_userType))
			{
				if(_responseMap.get("resp_valData_"+senderDataBundle)!=null){
				responseDataStr = _responseMap.get("resp_valData_"+senderDataBundle).split(":");
				_responseMap.put("RECEIVED_BUNDLES",senderDataBundle);}
			}
			else{
				if(_responseMap.get("resp_valData_"+receiverBundleId)!=null){
				responseDataStr = _responseMap.get("resp_valData_"+receiverBundleId).split(":");
				 _responseMap.put("RECEIVED_BUNDLES",receiverBundleId);
			}
			}
			 _responseMap.put("BUNDLE_BALANCES",responseDataStr[0]);
			 _responseMap.put("RESP_DATA_BALANCE",responseDataStr[0]);
			 _responseMap.put("BUNDLE_EXPDATES",responseDataStr[2]);
			 _responseMap.put("BUNDLES_ACCOUNT_RESNAME",responseDataStr[1]);
			 responseDataStr = _responseMap.get("resp_valData_1").split(":");
			 _responseMap.put("resp_Balance",responseDataStr[0]);
			 _responseMap.put("resp_expDate",responseDataStr[2]);
			_requestMap.put("BUNDLE_BALANCES",_responseMap.get("BUNDLE_BALANCES")); // Data Bundles Balances
			_requestMap.put("RECEIVED_BUNDLES",_responseMap.get("RECEIVED_BUNDLES")); // All Data Bundles IDs
			_requestMap.put("BUNDLE_EXPDATES",_responseMap.get("BUNDLE_EXPDATES")); // All Data Bundles Exp Dates
			//set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS",InterfaceErrorCodesI.SUCCESS);
            //get value of BALANCE from response map (BALANCE was set in response map in sendRequestToIN method.)
			String mainAmountStr=(String)_responseMap.get("resp_Balance");
			String dataAmountStr=(String)_responseMap.get("RESP_DATA_BALANCE");
			
			
			if(_log.isDebugEnabled())_log.debug("validate ","resp_Balance (Main Balance) : "+mainAmountStr+"  , Data Balance Available : "+dataAmountStr);
			try
			{
            	
            	mainAmountStr = InterfaceUtil.getSystemAmountFromINAmount(mainAmountStr,Double.parseDouble(multFactor));
            	dataAmountStr = InterfaceUtil.getSystemDataAmountFromINAmount(dataAmountStr,Double.parseDouble(p_requestMap.get("DATA_MULT_FACTOR")),Double.parseDouble(p_requestMap.get("DATA_DIVIDE_FACTOR")));
            	
				_requestMap.put("INTERFACE_PREV_BALANCE",dataAmountStr); // balance of Data
				_requestMap.put("INTERFACE_MAIN_BALANCE",mainAmountStr); // Balance of Main Account for Fee
			}
            catch(Exception e)
			{
            	e.printStackTrace();
            	_log.error("validate","Exception e:"+e.getMessage());
            	EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "Balance obtained from the IN is not numeric, while parsing the Balance get Exception e:"+e.getMessage());
            	throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.ERROR_RESPONSE);
			}
            
            _requestMap.put("ACCOUNT_STATUS", (String)_responseMap.get("resp_state"));
            _requestMap.put("SERVICE_CLASS", (String)_responseMap.get("resp_serviceClass"));
            //set OLD_EXPIRY_DATE in request map as returned from _responseMap.
			String dataBundleExpDate = _responseMap.get("BUNDLE_EXPDATES");
			//dataBundleExpDate = dataBundleExpDate.substring(0,dataBundleExpDate.length()-1);
            _requestMap.put("OLD_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString(dataBundleExpDate, dateINFormate));
            _requestMap.put("OLD_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString(dataBundleExpDate, dateINFormate));
	//String graceDays ="0";
	//_requestMap.put("GRACE_DAYS",graceDays);	
			
//	graceDays = String.valueOf(BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(dataBundleExpDate,dateINFormate),currentDate));	
	//		_requestMap.put("GRACE_DAYS",graceDays);	
				
            //set the mapping language of our system from FileCache mapping based on the responsed language.
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
            e.printStackTrace();
            _log.error("validate","Exception e:"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[validate]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String)_requestMap.get("NETWORK_CODE"), "While validating the subscriber get the Exception e:"+e.getMessage());
            throw new BTSLBaseException(this,"validate",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
        }
        finally
        {
        	if(_log.isDebugEnabled()) _log.debug("validate","Exiting with  _requestMap: "+_requestMap);
        }
    }
/**
 * This method is responsible for the credit of subscriber account.
 *  1.Interface specific parameters are set and added to the request map.
 *  2.Format the request into predefined xml for credit request, method internally calls the generateRequest 
 *     method of  OCIZteRequestResponseFormatter.
 *  3.For sending request to IN this method internally calls private method sendRequestToIN
 *  4.Process the response.
 * @param	HashMap	p_map
 * @throws	BTSLBaseException,Exception
 */	
	public void credit(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception
	{
    	if (_log.isDebugEnabled())_log.debug("credit","Entered p_requestMap: " + p_requestMap);
    	double systemAmtDouble=0;
    	double dataMultFactorDouble=0;
    	double dataDivideFactorDouble=0;
    	String amountStr=null;
    	String selectorBundleId=null;
    	String receiverBundleId = null;
        _requestMap = p_requestMap;
        try
         {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
			_inTXNID=getINTransactionID(_requestMap,null);
			_requestMap.put("IN_TXN_ID",_inTXNID);
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
 			_msisdn=(String)_requestMap.get("MSISDN");
 			selectorBundleId=(String)_requestMap.get("SELECTOR_BUNDLE_ID");
 			receiverBundleId = (String)_requestMap.get("RECEIVER_BUNDLE"); // fetching Receiver Bundle ID for credit for Data
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and recieved balance would be devided by this factor.
			String dataMultFactor = FileCache.getValue(_interfaceID,"DATA_MULT_FACTOR");
			String dataDivideFactor = FileCache.getValue(_interfaceID,"DATA_DIVIDE_FACTOR");
			if(_log.isDebugEnabled())_log.debug("credit","multFactor:"+dataMultFactor);
			if(InterfaceUtil.isNullString(dataMultFactor) || InterfaceUtil.isNullString(dataDivideFactor))
			{
			    _log.error("credit","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "DATA_MULT_FACTOR or DATA_DIVIDE_FACTOR is not defined in the INFile");
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			dataMultFactor = dataMultFactor.trim();
			dataDivideFactor = dataDivideFactor.trim();
			_requestMap.put("DATA_MULT_FACTOR",dataMultFactor);
			_requestMap.put("DATA_MULT_FACTOR",dataDivideFactor);
			
			
			//Set the interface parameters into requestMap
			setInterfaceParameters(CP2PDataI.ACTION_RECHARGE_CREDIT);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
			String interfacePrevAmtStr = null;
			try
			{
				//if("1".equals(selectorBundleId))
			//	{
					dataMultFactorDouble=Double.parseDouble(dataMultFactor);
					dataDivideFactorDouble=Double.parseDouble(dataDivideFactor);
					double interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_AMOUNT"));
					double interfacePrevAmtDouble = Double.parseDouble((String)_requestMap.get("INTERFACE_PREV_BALANCE"));
					systemAmtDouble = InterfaceUtil.getINDataAmountFromSystemDataAmountToIN(interfaceAmtDouble,dataMultFactorDouble,dataDivideFactorDouble);
					interfacePrevAmtDouble = InterfaceUtil.getINDataAmountFromSystemDataAmountToIN(interfacePrevAmtDouble,dataMultFactorDouble,dataDivideFactorDouble);
					interfacePrevAmtStr = String.valueOf(interfacePrevAmtDouble);
					//For credit request, the amount send to the IN should be negative for OGN ZTE.
					if(systemAmtDouble>0)
						systemAmtDouble=0-systemAmtDouble;
					//Change the amount in to string.
					amountStr=String.valueOf(systemAmtDouble);
					//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
					String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
					if(_log.isDebugEnabled()) _log.debug("credit","From file cache roundFlag = "+roundFlag);
					//If the ROUND_FLAG is not defined in the INFile 
					if(InterfaceUtil.isNullString(roundFlag))
					{
					    roundFlag="Y";
					    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "OCIZteINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
					}
					//If rounding of amount is allowed, round the amount value and put this value in request map.
					if("Y".equals(roundFlag.trim()))
					{
					    amountStr=String.valueOf(Math.round(systemAmtDouble));
						_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
					}
				//}
			   
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    _log.error("credit","Exception e:"+e.getMessage());
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("credit","transfer_amount:"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)


			_requestMap.put("prev_balance",interfacePrevAmtStr);
			_requestMap.put("transfer_amount",amountStr);
			_requestMap.put("receiver_bundle",receiverBundleId); // bundle id required to credit data balance
			

			String requestStr = _formatter.generateRequest(CP2PDataI.ACTION_RECHARGE_CREDIT,_requestMap);
			 //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
             sendRequestToIN(CP2PDataI.ACTION_RECHARGE_CREDIT,requestStr);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
			if (_log.isDebugEnabled()) _log.debug("credit", "resp_creditData_" +receiverBundleId+" : "+_responseMap.get("resp_creditData_"+receiverBundleId));
			String[] responseDataStr = _responseMap.get("resp_creditData_"+receiverBundleId).split(":");
			 _responseMap.put("resp_Balance",responseDataStr[0]);
			 _responseMap.put("resp_expDate",responseDataStr[2]);
			 _responseMap.put("resp_accountResCode",receiverBundleId);
			 _responseMap.put("resp_accountResName",responseDataStr[1]);
            try
            {
            		String postBalanceStr = (String) _responseMap.get("resp_Balance");
                	//postBalanceStr="-"+postBalanceStr;
				postBalanceStr = InterfaceUtil.getSystemDataAmountFromINAmount(postBalanceStr,dataMultFactorDouble,dataDivideFactorDouble);
				_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
            }
            catch(Exception e)
            {
            	_log.error("credit","Exception e:"+e.getMessage());
            	e.printStackTrace();
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            
            
            	//Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
                //Get the value of end_inact_date from the response, change its format as per the interface and set it into 
                //requestMap with key as NEW_GRACE_DATE
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
                //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
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
				 e.printStackTrace();
				 _log.error("credit","Exception e:"+e.getMessage());
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				 throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
         catch (Exception e)
         {
             e.printStackTrace();
             _log.error("credit", "Exception e:" + e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
             throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
         }
    }
/** This method is used to credit back the sender in case of ambiguous case. 
 * @param p_map HashMap
 * @throws BTSLBaseException, Exception
 */	
	public void creditAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception 
	{
    	if (_log.isDebugEnabled())_log.debug("creditAdjust","Entered p_requestMap: " + p_requestMap);
    	double systemFeeAmtDouble=0;
    	double systemDataAmtDouble=0;
    	double multFactorDouble=0;
    	String amountStr=null;
		double dataMultFactorDouble=0;
		double dataDivideFactorDouble=0;
    	String selectorBundleId=null;
        _requestMap = p_requestMap;
        String DATA_BUNDLE_USED =URLDecoder.decode(_requestMap.get("DATA_BUNDLE_USED").toString(),"UTF-8");
        try
         {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
 			_msisdn=(String)_requestMap.get("MSISDN");
 			selectorBundleId=(String)_requestMap.get("SELECTOR_BUNDLE_ID");
			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and received balance would be devided by this factor.
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			String dataMultFactor = FileCache.getValue(_interfaceID,"DATA_MULT_FACTOR");
			String dataDivideFactor = FileCache.getValue(_interfaceID,"DATA_DIVIDE_FACTOR");
			if(_log.isDebugEnabled())_log.debug("credit","multFactor:"+multFactor);
			if(InterfaceUtil.isNullString(multFactor))
			{
			    _log.error("credit","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "MULT_FACTOR  is not defined in the INFile");
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);
			
			
			//Set the interface parameters into requestMap
			setInterfaceParameters(CP2PDataI.ACTION_RECHARGE_CREDIT);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_CREDIT");
			dataMultFactorDouble=Double.parseDouble(dataMultFactor);
					dataDivideFactorDouble=Double.parseDouble(dataDivideFactor);
					multFactorDouble=Double.parseDouble(multFactor);
			try
			{
			_inTXNID=getINTransactionID(_requestMap,"D");
			_requestMap.put("IN_TXN_ID",_inTXNID);
				
				if("1".equals(selectorBundleId))
				{
					dataMultFactorDouble=Double.parseDouble(dataMultFactor);
					dataDivideFactorDouble=Double.parseDouble(dataDivideFactor);
					multFactorDouble=Double.parseDouble(multFactor);
					double interfaceDataAmtDouble = Double.parseDouble((String)_requestMap.get("REQUESTED_AMOUNT"));
					systemDataAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceDataAmtDouble,multFactorDouble);
					//For credit request, the amount send to the IN should be negative for OCI Zte.
					if(systemDataAmtDouble>0)
						systemDataAmtDouble=0-systemDataAmtDouble;
					//Change the amount in to string.
					amountStr=String.valueOf(systemDataAmtDouble);
					//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
					String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
					if(_log.isDebugEnabled()) _log.debug("creditAdjust","From file cache roundFlag = "+roundFlag);
					//If the ROUND_FLAG is not defined in the INFile 
					if(InterfaceUtil.isNullString(roundFlag))
					{
					    roundFlag="Y";
					    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "OCIZteINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
					}
					//If rounding of amount is allowed, round the amount value and put this value in request map.
					if("Y".equals(roundFlag.trim()))
					{
					    amountStr=String.valueOf(Math.round(systemFeeAmtDouble));
						_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
					}
				}
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    _log.error("creditAdjust","Exception e:"+e.getMessage());
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
			    throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("creditAdjust","transfer_amount:"+DATA_BUNDLE_USED);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			_requestMap.put("budle_balance_deduction",DATA_BUNDLE_USED);
			String requestStr = _formatter.generateRequest(CP2PDataI.ACTION_RECHARGE_CREDIT_ADJUST,_requestMap);
			 //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
             sendRequestToIN(CP2PDataI.ACTION_RECHARGE_CREDIT_ADJUST,requestStr);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
            	
            		String postBalanceStr = (String) _responseMap.get("resp_Balance");
            		double postBalLong = Double.parseDouble((String) _responseMap.get("resp_Balance"));
                	if(postBalLong<0)
                		postBalLong = 0-postBalLong;
            		/*if('0' == postBalanceStr.charAt(0))
                		postBalanceStr=postBalanceStr.substring(1);
                	else
                		postBalanceStr="-"+postBalanceStr;*/
                	
                	postBalanceStr=String.valueOf(postBalLong);
                	postBalanceStr = InterfaceUtil.getSystemDataAmountFromINAmount(postBalanceStr,dataMultFactorDouble,dataDivideFactorDouble);
    				_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
            	
            }
            catch(Exception e)
            {
            	_log.error("creditAdjust","Exception e:"+e.getMessage());
            	e.printStackTrace();
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            
           
            	//Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
                //Get the value of end_inact_date from the response, change its format as per the interface and set it into 
                //requestMap with key as NEW_GRACE_DATE
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
                //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
           
			
			
			try
			{
				
				_inTXNID=getINTransactionID(_requestMap,"F");
			_requestMap.put("IN_TXN_ID",_inTXNID);

					multFactorDouble=Double.parseDouble(multFactor);
					double interfaceFeeAmtDouble = Double.parseDouble((String)_requestMap.get("ACCESS_FEE"));
					systemFeeAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(interfaceFeeAmtDouble,multFactorDouble);
					//For credit request, the amount send to the IN should be negative for OCI Zte.
					//Change the amount in to string.
					amountStr=String.valueOf(systemFeeAmtDouble);
					//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
					String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
					if(_log.isDebugEnabled()) _log.debug("credit","From file cache roundFlag = "+roundFlag);
					//If the ROUND_FLAG is not defined in the INFile 
					if(InterfaceUtil.isNullString(roundFlag))
					{
					    roundFlag="Y";
					    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "OCIZteINHandler[credit]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=N.");
					}
					//If rounding of amount is allowed, round the amount value and put this value in request map.
					if("Y".equals(roundFlag.trim()))
					{
					    amountStr=String.valueOf(Math.round(systemFeeAmtDouble));
						_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
					}
				}
			catch(Exception e)
			{
			    e.printStackTrace();
			    _log.error("creditAdjust","Exception e:"+e.getMessage());
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
			    throw new BTSLBaseException(this,"creditAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("creditAdjust","transfer_amount:"+amountStr+"systemFeeAmtDouble"+systemFeeAmtDouble+"_requestMap.get('ACCESS_FEE')"+_requestMap.get("ACCESS_FEE"));
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			_requestMap.put("budle_balance_deduction",amountStr+":1");
			requestStr = _formatter.generateRequest(CP2PDataI.ACTION_RECHARGE_CREDIT_ADJUST,_requestMap);
			 //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
             sendRequestToIN(CP2PDataI.ACTION_RECHARGE_CREDIT_ADJUST,requestStr);
            //set TRANSACTION_STATUS as Success in request map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
          /*  try
            {
            	

            		String postBalanceStr = (String) _responseMap.get("resp_Balance");
            		double postBalLong = Double.parseDouble((String) _responseMap.get("resp_Balance"));
                	if(postBalLong<0)
                		postBalLong = 0-postBalLong;
            		if('0' == postBalanceStr.charAt(0))
                		postBalanceStr=postBalanceStr.substring(1);
                	else
                		postBalanceStr="-"+postBalanceStr;
                	
                	postBalanceStr=String.valueOf(postBalLong);
                	postBalanceStr = InterfaceUtil.getSystemAmountFromINAmount(postBalanceStr,multFactorDouble);
    				_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
            	
            }
            catch(Exception e)
            {
            	_log.error("creditAdjust","Exception e:"+e.getMessage());
            	e.printStackTrace();
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "CP2PDataSimulatorINHandler[credit]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "credit balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
			    throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            
            
            	//Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
                _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
                //Get the value of end_inact_date from the response, change its format as per the interface and set it into 
                //requestMap with key as NEW_GRACE_DATE
                _requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
                //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
                _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
				*/
            
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
				 e.printStackTrace();
				 _log.error("creditAdjust","Exception e:"+e.getMessage());
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in credit");
				 throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
         catch (Exception e)
         {
             e.printStackTrace();
             _log.error("creditAdjust", "Exception e:" + e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[credit]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While credit get the Exception e:"+e.getMessage());
             throw new BTSLBaseException(this,"credit",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
         }
         finally
         {
             if (_log.isDebugEnabled()) _log.debug("credit", "Exited _requestMap=" + _requestMap);
         }
    }
/**
 * This method is used to debit the sender account in CP2P request.
 * @param p_map HashMap
 * @throws Exception
 */	
	public void debitAdjust(HashMap<String,String> p_requestMap) throws BTSLBaseException, Exception 
	{
    	if (_log.isDebugEnabled())_log.debug("debitAdjust","Entered p_requestMap: " + p_requestMap);
    	double systemAmtDouble=0;
    	double multFactorDouble=0;
		double dataMultFactorDouble=0;
		double dataDivideFactorDouble=0;
    	String amountStr=null;
    	String accessFeeAmountStr=null;
    	double systemAccessFeeAmtDouble=0;
        int bdlCount = 0;
        _requestMap = p_requestMap;
    	String[] SENDER_BUNDLES_ID =URLDecoder.decode(_requestMap.get("SENDER_DATA_BUNDLES_ID").toString(),"UTF-8").split(",");
    	String[] SENDER_BUNDLES_BALANCES =URLDecoder.decode(_requestMap.get("SENDER_DATA_BUNDLES_BALANCES").toString(),"UTF-8").split(",");
    	String[] SENDER_BUNDLES_EXPDATES =URLDecoder.decode(_requestMap.get("SENDER_DATA_BUNDLES_EXPDATES").toString(),"UTF-8").split(",");
        
		// take all the bundles data into the Array List
		for(String bdl_id : SENDER_BUNDLES_ID)
		{
			
			if(!bdl_id.trim().equalsIgnoreCase("1")){
			String bdl_data ="";
			bdl_data+=SENDER_BUNDLES_EXPDATES[bdlCount]+"|";
			bdl_data+=SENDER_BUNDLES_BALANCES[bdlCount]+"|";
			bdl_data+=bdl_id;
			_bundleDataList.add(bdl_data);
		
			}
			
			bdlCount++;
		}
		// sort the bundle data based on the Expiry Date and Balance
	//	sortBundleData(_bundleDataList);
		
        try
         {
        	_interfaceID=(String)_requestMap.get("INTERFACE_ID");
        	_interfaceClosureSupport=FileCache.getValue(_interfaceID, "INTFCE_CLSR_SUPPORT");
        	if ("Y".equalsIgnoreCase(_interfaceClosureSupport))
        		checkInterfaceB4SendingRequest();
			_referenceID=(String)_requestMap.get("TRANSACTION_ID");
 			_msisdn=(String)_requestMap.get("MSISDN");
 			
 			//Fetching the MULT_FACTOR from the INFile.
			//While sending the amount to IN, it would be multiplied by this factor, and received balance would be devided by this factor.
			String dataMultFactor = FileCache.getValue(_interfaceID,"DATA_MULT_FACTOR");
			String dataDivideFactor = FileCache.getValue(_interfaceID,"DATA_DIVIDE_FACTOR");
			String multFactor = FileCache.getValue(_interfaceID,"MULT_FACTOR");
			if(_log.isDebugEnabled())_log.debug("debitAdjust","multFactor:"+dataMultFactor+"data divdie factor : "+dataDivideFactor);
			if(InterfaceUtil.isNullString(dataMultFactor) || InterfaceUtil.isNullString(dataDivideFactor))
			{
			    _log.error("debitAdjust","MULT_FACTOR  is not defined in the INFile");
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "DATA_MULT_FACTOR or DATA_DIVIDE_FACTOR is not defined in the INFile");
			    throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			multFactor = multFactor.trim();
			_requestMap.put("MULT_FACTOR",multFactor);
			dataMultFactor = dataMultFactor.trim();
			_requestMap.put("DATA_MULT_FACTOR",dataMultFactor);
			dataDivideFactor = dataDivideFactor.trim();
			_requestMap.put("DATA_DIVIDE_FACTOR",dataDivideFactor);
				
			//Set the interface parameters into requestMap
			setInterfaceParameters(CP2PDataI.ACTION_IMMEDIATE_DEBIT);
			_requestMap.put("SYSTEM_STATUS_MAPPING","SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
			double interfaceAmtDouble;
			// Amount Debit Request for Data First
			try
			{	
			_inTXNID=getINTransactionID(_requestMap,"D");
			_requestMap.put("IN_TXN_ID",_inTXNID);
				dataMultFactorDouble=Double.parseDouble(dataMultFactor);
				dataDivideFactorDouble = Double.parseDouble(dataDivideFactor);
				
				interfaceAmtDouble = Double.parseDouble((String)_requestMap.get("REQUESTED_AMOUNT"));
				systemAmtDouble = InterfaceUtil.getINDataAmountFromSystemDataAmountToIN(interfaceAmtDouble,dataMultFactorDouble,dataDivideFactorDouble);
				//For debit request, the amount send to IN should be positive.
				if(systemAmtDouble<0)
					systemAmtDouble=0-systemAmtDouble;
				//Change the amount in to string.
				amountStr=String.valueOf(systemAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				if(_log.isDebugEnabled()) _log.debug("credit","From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
				    roundFlag="Y";
				    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "OCIZteINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
				    amountStr=String.valueOf(Math.round(systemAmtDouble));
					_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
				}
			}
			catch(BTSLBaseException be)
			{
			    _log.error("getBundlesInfoRcvdFromIN","BTSLBaseException be = "+be.getMessage());
			    throw be;
			}//end of BTSLBaseException
			catch(Exception e)
			{
			    e.printStackTrace();
			    _log.error("debitAdjust","Exception e:"+e.getMessage());
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
			    throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("debitAdjust","Amount to be Debit for Data :"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
		
			Long tempTransferAmount = Long.parseLong(amountStr);
			String dataBalanceBundleDeductionStr = "";
			
			for(String bdl_bal : _bundleDataList)
			{
				String[] singleBundleData = bdl_bal.split("\\|");
				//[0] expiry date ,[1] transfer amount , [2] bundle ID
				
				if(Long.parseLong(singleBundleData[1])<=0)
						continue;
						
				if(Long.parseLong(singleBundleData[1])>=tempTransferAmount){
					dataBalanceBundleDeductionStr+=tempTransferAmount+":"+singleBundleData[2]+"|";
					break;
				}
				dataBalanceBundleDeductionStr+=singleBundleData[1]+":"+singleBundleData[2]+"|";
				tempTransferAmount-=Long.parseLong(singleBundleData[1]);
			}
			
			
			_requestMap.put("budle_balance_deduction",dataBalanceBundleDeductionStr);
			_requestMap.put("DATA_BUNDLE_USED",dataBalanceBundleDeductionStr); // to be used for credit Adjust
			
			if(_log.isDebugEnabled())
				_log.debug("debitAdjust","Bundles used for Debit for Data separated by '|' : "+dataBalanceBundleDeductionStr);
			
			String requestSre = _formatter.generateRequest(CP2PDataI.ACTION_IMMEDIATE_DEBIT,_requestMap);
			 //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
             sendRequestToIN(CP2PDataI.ACTION_IMMEDIATE_DEBIT,requestSre);
            //set TRANSACTION_STATUS as Success in request map
             _requestMap.put("TRANSACTION_DONE", "DATA");
              
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
            try
            {
            	String postBalanceStr = (String) _responseMap.get("resp_Balance");
				postBalanceStr = InterfaceUtil.getSystemDataAmountFromINAmount(postBalanceStr,dataMultFactorDouble,dataDivideFactorDouble);
				_requestMap.put("INTERFACE_POST_BALANCE",postBalanceStr);
            }
            catch(Exception e)
            {
            	_log.error("debitAdjust","Exception e:"+e.getMessage());
            	e.printStackTrace();
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[debitAdjust]",_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "balance from response is not numeric or multiplication factor defined in the INFile is not Numeric, Exception e:"+e.getMessage());
			    throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
            //Get the value of end_inact_date from the response, change its format as per the interface and set it into 
            //requestMap with key as NEW_GRACE_DATE
            //_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("RESP_GRACE"), PretupsI.DATE_FORMAT_DDMMYYYY));
            //Set the POST_BALANCE_ENQ_SUCCESS equal to Y into requestMap.
            _requestMap.put("POST_BALANCE_ENQ_SUCCESS","Y");
            
            
            //Code for Sending Debit Request for Processing fee from MAIN BALANCE
            try
			{
			_inTXNID=getINTransactionID(_requestMap,"F");
			_requestMap.put("IN_TXN_ID",_inTXNID);
			multFactorDouble = Double.parseDouble(multFactor);
			double accessFeeInterfaceAmtDouble = Double.parseDouble((String)_requestMap.get("TOTAL_FEE"));
			if(_log.isDebugEnabled()) _log.debug("debit"," total Fee : "+accessFeeInterfaceAmtDouble);
			//accessFeeInterfaceAmtDouble = accessFeeInterfaceAmtDouble - interfaceAmtDouble;
				systemAccessFeeAmtDouble = InterfaceUtil.getINAmountFromSystemAmountToIN(accessFeeInterfaceAmtDouble,multFactorDouble);
				//For debit request, the amount send to IN should be positive.
				if(systemAccessFeeAmtDouble<0)
					systemAccessFeeAmtDouble=0-systemAccessFeeAmtDouble;
				//Change the amount in to string.
				amountStr=String.valueOf(systemAccessFeeAmtDouble);
				//Based on the INFiles ROUND_FLAG flag, we have to decide to round the transfer amount or not.
				String roundFlag = FileCache.getValue(_interfaceID,"ROUND_FLAG");
				if(_log.isDebugEnabled()) _log.debug("debit","From file cache roundFlag = "+roundFlag);
				//If the ROUND_FLAG is not defined in the INFile 
				if(InterfaceUtil.isNullString(roundFlag))
				{
				    roundFlag="Y";
				    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "OCIZteINHandler[debitAdjust]",_referenceID+"MSISDN = "+_msisdn ," INTERFACE ID = "+_interfaceID, "Network code = "+(String) _requestMap.get("NETWORK_CODE"), "ROUND_FLAG is not defined in IN File hence system taken the default value=Y.");
				}
				//If rounding of amount is allowed, round the amount value and put this value in request map.
				if("Y".equals(roundFlag.trim()))
				{
				    accessFeeAmountStr=String.valueOf(Math.round(systemAccessFeeAmtDouble));
					_requestMap.put("INTERFACE_ROUND_AMOUNT",amountStr);
				}
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			    _log.error("debitAdjust","Exception e:"+e.getMessage());
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[debitAdjust]","REFERENCE ID = "+_referenceID,_msisdn +" INTERFACE ID = "+_interfaceID, (String) _requestMap.get("NETWORK_CODE"), "INTERFACE_AMOUNT  is not Numeric");
			    throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}
			if(_log.isDebugEnabled()) _log.debug("debitAdjust","Amount for Fee Debit :"+amountStr);
			//set transfer_amount in request map as amountStr (which is round value of INTERFACE_AMOUNT)
			_requestMap.put("transfer_amount",amountStr);
			// Making the Bundles for Fee , Debiting from Bundle ID 1 as main balance
			String feeBalanceBundleDeductionStr = amountStr+":1";
			_requestMap.put("budle_balance_deduction",feeBalanceBundleDeductionStr);
			String requestStr = _formatter.generateRequest(CP2PDataI.ACTION_IMMEDIATE_DEBIT,_requestMap);
			 //sending the Re-charge request to IN along with re-charge action defined in SafcomI interface
            
            sendRequestToIN(CP2PDataI.ACTION_IMMEDIATE_DEBIT,requestStr);
            
            //set TRANSACTION_STATUS as Success in request map
             _requestMap.put("TRANSACTION_DONE", "DATA,FEE");
              
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            //Set the post balance after multiplied by multiplication factor.
           
            //Set the value of NEW_EXPIRY_DATE as the value of end_val_date from responseMap, after converting the format as per interface
            _requestMap.put("NEW_EXPIRY_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("resp_expDate"), dateINFormate));
            //Get the value of end_inact_date from the response, change its format as per the interface and set it into 
            //requestMap with key as NEW_GRACE_DATE
            //_requestMap.put("NEW_GRACE_DATE", InterfaceUtil.getInterfaceDateFromDateString((String) _responseMap.get("RESP_GRACE"), PretupsI.DATE_FORMAT_DDMMYYYY));
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
				 e.printStackTrace();
				 _log.error("debitAdjust","Exception e:"+e.getMessage());
				 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[debitAdjust]", _referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while processing ambiguous case in debitAdjust");
				 throw new BTSLBaseException(this,"debitAdjust",InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}			
		}
         catch (Exception e)
         {
             e.printStackTrace();
             _log.error("debitAdjust", "Exception e:" + e.getMessage());
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[debitAdjust]",_referenceID,_msisdn, (String) _requestMap.get("NETWORK_CODE"), "While debitAdjust get the Exception e:"+e.getMessage());
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
		TransactionLog.log(_interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_REQ,"_requestMap: "+_requestMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+p_action );
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
			try
			{
				
				//CP2PDataWebServiceConnector serviceConnection = new CP2PDataWebServiceConnector();
				ZTEOCMWebServiceConnector serviceConnection = new ZTEOCMWebServiceConnector();
				
				call =serviceConnection.callService(_requestMap);
			
			try
			{
				responseStr =  call.invoke(new Object[] { p_requestStr}).toString();
				/*Properties properties = new Properties();
	            File file= new File(FileCache.getValue(_interfaceID,"SIMULATOR_FILE"));//Absolute path
	            properties.load(new FileInputStream(file));
				if(p_action==CP2PDataI.ACTION_ACCOUNT_INFO)
					responseStr = properties.getProperty("ACCOUNT_INFO");
				else if(p_action==CP2PDataI.ACTION_RECHARGE_CREDIT)
					responseStr = properties.getProperty("ACCOUNT_RECHARGE");
				else if(p_action==CP2PDataI.ACTION_IMMEDIATE_DEBIT)
					responseStr = properties.getProperty("ACCOUNT_CREDIT_XFER");
				else if(p_action==CP2PDataI.ACTION_IMMEDIATE_DEBIT)
					responseStr = properties.getProperty("ACCOUNT_DEBIT");
			/**/
				
				endTime=System.currentTimeMillis();
				if(false)
					throw new java.rmi.RemoteException(); 
			}
			catch(java.rmi.RemoteException re)
			{
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEKenyaSimulatorINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Remote Exception occured. So marking the transaction as AMBIGUOUS");
				_log.error("sendRequestToIN","Remote Exception occured. So marking the response as AMBIGUOUS");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
			}
			catch(Exception e)
			{
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"ZTEKenyaSimulatorINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
				_log.error("sendRequestToIN","Error Message :"+e.getMessage());
				throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
			}}
			catch(BTSLBaseException be)
			{
				throw be;
			}
			catch(Exception e)
			{
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"OCIZteINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Error Message:"+e.getMessage());
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
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"OCIZteINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Response object found null");
				_log.error("sendRequestToIN","Response object is null");
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.AMBIGOUS);
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
			}
			
			TransactionLog.log( _interfaceID,_referenceID,_msisdn,(String)_requestMap.get("NETWORK_CODE"),String.valueOf(p_action),PretupsI.TXN_LOG_REQTYPE_RES,"_requestMap: "+_requestMap+", _responseMap: "+_responseMap,PretupsI.TXN_LOG_STATUS_UNDERPROCESS,"action = "+p_action);
			//End time would be stored into request map with 
			//key as IN_END_TIME as soon as the response of the request is fetched from the IN.
			//Difference of start and end time would be compared against the warn time, 
			//if request and response takes more time than that of the warn time,
			//an event with level INFO is handled
			
			//Difference of start and end time would be compared against the warn time, if request and response takes more time than that of the warn time, an event with level INFO is handled
			warnTime=Long.parseLong((String)_requestMap.get("WARN_TIMEOUT"));
		    if(endTime-startTime>warnTime)
			{
				_log.info("sendRequestToIN", "WARN time reaches startTime= "+startTime+" endTime= "+endTime+" warnTime= "+warnTime+" time taken= "+(endTime-startTime));
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"OCIZteINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"ZTE IN is taking more time than the warning threshold. Time= "+(endTime-startTime));
			}
			
			//parse the response message by using CS3CCAPIRequestFormatter and fetch the execution status.
	        _responseMap=_formatter.parseResponse(p_action,responseStr);
	       
			String status=(String)_responseMap.get("resp_returnCode");
			_requestMap.put("INTERFACE_STATUS",status);
			
			if("Y".equalsIgnoreCase(_interfaceClosureSupport) && !("Y".equals(_requestMap.get("ADJUST")) && "C".equals(_requestMap.get("INTERFACE_ACTION")) && "S".equals(_userType)))
			 {
			 	if(_interfaceCloserVO.getInterfaceStatus().equals(InterfaceCloserI.INTERFACE_SUSPEND))
					_interfaceCloser.resetCounters(_interfaceCloserVO,_requestMap);  
				_interfaceCloser.updateCountersOnSuccessResp(_interfaceCloserVO); 
			 }
			
			//If the status is Not OK, exception with error code as RESPONSE_ERROR is thrown.
			if(!CP2PDataI.RESULT_OK.equals(status))
			{
				int len =status.length();
			    if(len >10)
			    {
			    	status= status.substring(len-10);
			    }
			    _requestMap.put("INTERFACE_STATUS",status);
			    if(CP2PDataI.MSISDN_NOT_FOUND.equals(status))
			    {
			    	EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"OCIZteINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"MSISDN does not exist on IN");
					_log.error("sendRequestToIN","MSISDN does not exist on IN");
					throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
			    }
				//Check the status whether the subscriber's MSISDN defined in the IN
				EventHandler.handle(EventIDI.INTERFACE_INVALID_RESPONSE,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"OCIZteINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"Response Status fron IN is: "+status+ ". So marking response as FAIL");
				_log.error("sendRequestToIN","Response Status fron IN is: "+status+ ". So marking response as FAIL");
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
		    e.printStackTrace();
		    _log.error("sendRequestToIN","Exception="+e.getMessage());
		    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.INTERFACES,EventStatusI.RAISED,EventLevelI.FATAL,"CS3CCAPIINHandler[sendRequestToIN]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn,"INTERFACE ID = "+_interfaceID,"Network code = "+(String) _requestMap.get("NETWORK_CODE")+" Action = "+p_action,"System Exception="+e.getMessage());
		    throw new BTSLBaseException(InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
		}//end of catch-Exception
		finally
		{
			 _requestMap.put("IN_START_TIME", String.valueOf(startTime));
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
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_TXN_ALLOWED is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("CANCEL_TXN_ALLOWED",cancelTxnAllowed.trim());
	    	
	    	String systemStatusMappingCr = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT");
	    	if(InterfaceUtil.isNullString(systemStatusMappingCr))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT",systemStatusMappingCr.trim());
	    	
	    	String systemStatusMappingCrAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_ADJ");
	    	if(InterfaceUtil.isNullString(systemStatusMappingCrAdj))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_ADJ is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_ADJ",systemStatusMappingCrAdj.trim());
	    	
	    	String systemStatusMappingDbtAdj = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_DEBIT_ADJ");
	    	if(InterfaceUtil.isNullString(systemStatusMappingDbtAdj))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_DEBIT_ADJ is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_DEBIT_ADJ",systemStatusMappingDbtAdj.trim());
	    	
	    	String systemStatusMappingCrBck = FileCache.getValue(_interfaceID,"SYSTEM_STATUS_MAPPING_CREDIT_BCK");
	    	if(InterfaceUtil.isNullString(systemStatusMappingCrBck))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SYSTEM_STATUS_MAPPING_CREDIT_BCK is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SYSTEM_STATUS_MAPPING_CREDIT_BCK",systemStatusMappingCrBck.trim());
	    	
	    	String cancelCommandStatusMapping = FileCache.getValue(_interfaceID,"CANCEL_COMMAND_STATUS_MAPPING");
	    	if(InterfaceUtil.isNullString(cancelCommandStatusMapping))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_COMMAND_STATUS_MAPPING is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("CANCEL_COMMAND_STATUS_MAPPING",cancelCommandStatusMapping.trim());
	    	
	    	
	    	String cancelNA = FileCache.getValue(_interfaceID,"CANCEL_NA");
	    	if(InterfaceUtil.isNullString(cancelNA))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CANCEL_NA is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CANCEL_NA is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("CANCEL_NA",cancelNA.trim());
	    	
	    	String userId = FileCache.getValue(_interfaceID,"USER_NAME");
	    	if(InterfaceUtil.isNullString(userId))
	    	{
	    	    _log.error("setInterfaceParameters","Value of USER_NAME is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "USER_NAME is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("USER_NAME",userId.trim());
	    	
	    	String passwd = FileCache.getValue(_interfaceID,"PASSWORD");
	    	if(InterfaceUtil.isNullString(passwd))
	    	{
	    	    _log.error("setInterfaceParameters","Value of PASSWORD is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "PASSWORD is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("PASSWORD",passwd.trim());
	    	
	    	String authHeader = FileCache.getValue(_interfaceID,"AUTH_HEADER");
	    	if(InterfaceUtil.isNullString(authHeader))
	    	{
	    	    _log.error("setInterfaceParameters","Value of AUTH_HEADER is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "AUTH_HEADER is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("AUTH_HEADER",authHeader.trim());
	    	
	    	String zteNameSpace = FileCache.getValue(_interfaceID,"ZTE_NAMESPACE");
	    	if(InterfaceUtil.isNullString(zteNameSpace))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SAFCOM_TERM_ID is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "ZTE_NAMESPACE is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("ZTE_NAMESPACE",zteNameSpace.trim());
	    	
	    	String uri = FileCache.getValue(_interfaceID,"SOAP_ACTION_URI");
	    	if(InterfaceUtil.isNullString(uri))
	    	{
	    	    _log.error("setInterfaceParameters","Value of SOAP_ACTION_URI is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "SOAP_ACTION_URI is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("SOAP_ACTION_URI",uri.trim());
	    	
	    	String endPt = FileCache.getValue(_interfaceID,"END_POINT");
	    	if(InterfaceUtil.isNullString(endPt))
	    	{
	    	    _log.error("setInterfaceParameters","Value of END_POINT is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "END_POINT is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("END_POINT",endPt.trim());
	    	
	    	String operationName = FileCache.getValue(_interfaceID,"OPERATION_NAME");
	    	if(InterfaceUtil.isNullString(operationName))
	    	{
	    	    _log.error("setInterfaceParameters","Value of OPERATION_NAME is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "OPERATION_NAME is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("OPERATION_NAME",operationName.trim());
	    	
	    	String channelId = FileCache.getValue(_interfaceID,"CHANNEL_ID");
	    	if(InterfaceUtil.isNullString(channelId))
	    	{
	    	    _log.error("setInterfaceParameters","Value of CHANNEL_ID is not defined in the INFile");
	    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "CHANNEL_ID is not defined in the INFile.");
	    		throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_CONFIG_PROBLEM);
	    	}
	    	_requestMap.put("CHANNEL_ID",channelId.trim());
	    	
	    	String warnTimeStr=FileCache.getValue(_interfaceID, "WARN_TIMEOUT");
			if(InterfaceUtil.isNullString(warnTimeStr)||!InterfaceUtil.isNumeric(warnTimeStr))
			{
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "OCIZteINHandler[setInterfaceParameters]","REFERENCE ID = "+_referenceID+"MSISDN = "+_msisdn," INTERFACE ID = "+_interfaceID, "Network code "+(String) _requestMap.get("NETWORK_CODE") , "WARN_TIMEOUT is not defined in IN File or not numeric");
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
            	_log.debug("setInterfaceParameters", "Exited _requestMap=" + _requestMap);
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
    		_interfaceLiveStatus=(String)_requestMap.get("INT_ST_TYPE");
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
    		 e.printStackTrace();
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
         	langFromIN = (String)_responseMap.get("LANGUAGETYPE");
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
    	        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "OCIZteINHandler[setLanguageFromMapping]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Mapping for language received from IN =  "+langFromIN +" is not defined in IN file Hence setting the Default language");
            //Set the mapped language to the requested map with key as IN_LANGUAGE.
    	    _requestMap.put("IN_LANG",mappedLang);
        }//end of try
        catch(Exception e)
        {
            e.printStackTrace();
            _log.error("setLanguageFromMapping","Exception e="+e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "OCIZteINHandler[setLanguageFromMapping]",_referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "while setting the language mapping get Exception=" + e.getMessage());
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) 
            	_log.debug("setLanguageFromMapping","Exited mappedLang="+mappedLang);
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
		    if (_log.isDebugEnabled())
		    	_log.debug("handleCancelTransaction", "reconLog."+reconLog);
		    cancelTxnAllowed=(String)_requestMap.get("CANCEL_TXN_ALLOWED");
		    //if cancel transaction is not supported by IN, get error codes from mapping present in IN fILE,write it
		    //into reconciliation log and throw exception (This exception tells the final status of transaction which was ambiguous) which would be handled by validate, credit or debitAdjust methods
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
		    e.printStackTrace();
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
 * @param p_requestMap HashMap
 * @return inTxnId String
 */
	protected String getINTransactionID(HashMap<String,String> p_requestMap,String p_debitType)
	{
		if(_log.isDebugEnabled())
			_log.debug("getINTransactionID","Entered");
		String userType=p_requestMap.get("USER_TYPE");
		if(InterfaceUtil.isNullString(p_debitType))
			p_debitType="";
		String inTxnId=p_requestMap.get("TRANSACTION_ID");
		if(!InterfaceUtil.isNullString(userType))
			inTxnId=inTxnId+userType+p_debitType;
		
		p_requestMap.put("IN_RECON_ID",inTxnId);
		p_requestMap.put("IN_TXN_ID",inTxnId);
		if(_log.isDebugEnabled())
			_log.debug("getINTransactionID","Exited with IN_RECON_ID || IN_TXN_ID="+inTxnId);
		return inTxnId;
	}    

/*
	protected void sortBundleData(ArrayList<String> bundleDataList) 
	{
		
		if(_log.isDebugEnabled())
			_log.debug("sortBundleData","Entered with BundleData List WITH NEW : "+bundleDataList);
	try{	
		Collections.sort(bundleDataList, new Comparator<String>(){
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			 int returnValue=0;
			@Override
			public int compare(String o1, String o2) {
				try{
					returnValue = f.parse(o1.split("\\|")[0]).compareTo(f.parse(o2.split("\\|")[0]));
				}catch(Exception e)
				{
					 e.printStackTrace();
				}
					if(returnValue!=0)
						return returnValue;
					return o2.split("\\|")[0].compareTo((o1.split("\\|")[1]));
					
			
			
			}
			
		});
	}catch(Exception e)
{
  e.printStackTrace();

}	
		if(_log.isDebugEnabled())
			_log.debug("sortBundleData","Exited with BundleData List : "+bundleDataList);
		
	}
*/
}
