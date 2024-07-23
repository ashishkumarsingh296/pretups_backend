/*
\ * Created on Jun 17, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.safaricomreversal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.bradmcevoy.http.Request;

import com.btsl.common.BTSLBaseException;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


	public class SafcomReversalRequestFormatter{
	    public Log _log=LogFactory.getLog("SafcomReversalRequestFormatter".getClass().getName());
	    private static long _transactionIDCounter=0;
	    
	    
	/**
	 * This method will return of MML request message.
	 * This method internally calls private method to get request object. 
	 * @param	int	p_action
	 * @param	HashMap	p_map
	 * @return	Request
	 * @throws	Exception
	 */
		public String generateRequestObject(int p_action, HashMap p_map) throws BTSLBaseException,Exception 
		{
	       if(_log.isDebugEnabled())_log.debug("generateRequestObject","Entered p_action="+p_action+" map: "+p_map);
			String reqObj=null;
			p_map.put("action",String.valueOf(p_action));
			try
			{
				switch(p_action)
				{
					case SafcomReversalI.ACTION_ACCOUNT_INFO: 
					{
						reqObj=generateGetAccountInfoRequestObject(p_map);
						break;	
					}
					case SafcomReversalI.ACTION_RECHARGE_CREDIT: 
					{
						reqObj=generateRechargeCreditRequestObject(p_map);
						break;	
					}	
					case SafcomReversalI.ACTION_IMMEDIATE_DEBIT: 
					{
						reqObj=generateRechargeDebitRequestObject(p_map);
						break;	
					}	
				}//end of switch block
			}//end of try block
			catch(BTSLBaseException be)
			{
				throw be;
			}
			catch(Exception e)
			{
		        _log.error("generateRequestObject", "Exception e:" + e.getMessage());
		        throw e;
			}//end of catch-Exception 
			finally
			{
			//	if (_log.isDebugEnabled())_log.debug("generateRequestObject", "Exited"+ ",AccountID: "+reqObj.getAccountID()+", Amount: "+reqObj.getAmount()+", Msisdn: "+reqObj.getMsisdn()+", Opermsisdn: "+reqObj.getOpermsisdn()+", Password: "+reqObj.getPassword()+", TerminalID: "+reqObj.getTerminalID()+", TransactionID: "+reqObj.getTransactionID()+", UserID: "+reqObj.getUserID());
			}//end of finally
			
			return reqObj;
		}//end of generateRequestObject
	/**
	 * This method internally calls methods (according to p_action parameter) to get response HashMap and returns it.  
	 * @param	int action
	 * @param	Response	p_respObj
	 * @return	HashMap map
	 * @throws  BTSLBaseException,Exception
	 */
		public HashMap parseResponseObject(int p_action, Response p_respObj) throws BTSLBaseException,Exception 
		{
		    if(_log.isDebugEnabled())_log.debug("parseResponseObject","Entered p_action="+p_action+", Status: "+p_respObj.getStatus());
		    HashMap map=null;
			try
			{
				switch(p_action)
				{
					case SafcomReversalI.ACTION_IMMEDIATE_DEBIT: 
					{
					    map=parseRechargeDebitResponseObject(p_respObj);
						break;	
					}	
				}//end of switch block
			}//end of try block
			catch(BTSLBaseException be)
			{
				throw be;
			}
			catch(Exception e)
			{
		        _log.error("parseResponseObject", "Exception e:" + e.getMessage());
		       throw e;
		    }//end of catch-Exception
			finally
			{
				if(_log.isDebugEnabled())_log.debug("parseResponseObject","Exiting map: "+map);
			}//end of finally
			return map;	
		}//end of parseResponseObject
	/**
	 * This method will return  request object for Account info (validate action).
	 * @param	HashMap	p_map
	 * @return	Request
	 * @throws	Exception
	 */
		private String generateGetAccountInfoRequestObject(HashMap p_map) throws Exception
		{
			if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequestObject","Entered p_map= "+p_map+" cOUNTRY MSISDN"+InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")));
			Request reqObj=null;
		    try
		    {
		 //   	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), 0,InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
		    //	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), 0,InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN")),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));	  
		    }
		    catch(Exception e)
		    {
		        _log.error("generateGetAccountInfoRequest", "Exception e:" + e.getMessage());
		        throw e;
		    }
		    finally
		    {
		   //     if(_log.isDebugEnabled()) _log.debug("generateGetAccountInfoRequest","Exiting  request:,AccountID: "+reqObj.getAccountID()+", Amount: "+reqObj.getAmount()+", Msisdn: "+reqObj.getMsisdn()+", Opermsisdn: "+reqObj.getOpermsisdn()+", Password: "+reqObj.getPassword()+", TerminalID: "+reqObj.getTerminalID()+", TransactionID: "+reqObj.getTransactionID()+", UserID: "+reqObj.getUserID());
		    }
		    return null;
		}
	/**
	 * This method parse the response for Acount INfo from Response object and puts into HashMap and returns it
	 * @param	Response	p_respObj
	 * @return	HashMap map
	 * @throws  Exception
	 */
		private HashMap parseGetAccountInfoResponseObject(Response p_respObj) throws Exception
		{
			return null;
			
		}
	/**
	 * This method will return   request Object for Credit action.
	 * @param	HashMap	p_map
	 * @return	Request
	 * @throws	Exception
	 */
		private String generateRechargeCreditRequestObject(HashMap p_map) throws Exception
	    {
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequestObject","Entered p_map= "+p_map+" cOUNTRY MSISDN"+InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")));
			Request reqObj=null;
		    try
		    {
		   // 	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
		    	//reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN")),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));	
		    }
		    catch(Exception e)
		    {
		        _log.error("generateRechargeCreditRequestObject", "Exception e:" + e.getMessage());
		        throw e;
		    }
		    finally
		    {
		   //     if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequestObject","Exiting  request:,AccountID: "+reqObj.getAccountID()+", Amount: "+reqObj.getAmount()+", Msisdn: "+reqObj.getMsisdn()+", Opermsisdn: "+reqObj.getOpermsisdn()+", Password: "+reqObj.getPassword()+", TerminalID: "+reqObj.getTerminalID()+", TransactionID: "+reqObj.getTransactionID()+", UserID: "+reqObj.getUserID());
		    }
		    return null ;
		}
	/**
	 * This method parse the response for Credit from Response Object and puts into HashMap and returns it
	 * @param	Response p_respObj
	 * @return	HashMap map
	 * @throws  Exception
	 */
		private HashMap parseRechargeCreditResponseObject(Response p_respObj) throws Exception
		{
			return null;
		}
		
		
		/**
		 * @param p_map
		 * @return
		 * @throws BTSLBaseException
		 */
		protected static String getINTransactionID(HashMap p_map) throws BTSLBaseException
		{
			
			
			_transactionIDCounter=LoadControllerCache.getTransactionIDCounter();
			LoadControllerCache.setTransactionIDCounter(_transactionIDCounter);
			long MAX_COUNTER=99999999;
	    	String instanceId=null;
	    	String paddedCounter="";
			try
			{
				instanceId=Constants.getProperty("INSTANCE_ID");
				if(InterfaceUtil.isNullString(instanceId))
					throw new BTSLBaseException("SafcomRequestFormatter","getINTransactionID",InterfaceErrorCodesI.ERROR_GENERATE_INTXNID);
				if(instanceId.length()==1)
					instanceId="0"+instanceId.trim();					
		  		int paddingLen=String.valueOf(MAX_COUNTER).length()-String.valueOf(_transactionIDCounter).length();
		  		
		  		for(int i=0;i<paddingLen;i++)
		  		{
		  			paddedCounter= "0"+paddedCounter;
		  		}
		  		paddedCounter+=_transactionIDCounter;
		  		p_map.put("IN_RECON_ID",paddedCounter);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return instanceId+paddedCounter;		
		}
		
		public static HashMap test(String[] args)
		{
			//https://10.5.4.235:8443/axis/services/Mediator?wsdl
			//SAFCOM_TERM_ID=8002
			//SAFCOM_TERM_ID=8001
			//SAFCOM_USER_ID=SFC_PINLESS
			//SAFCOM_PASSWD=pinless
			//SAFCOM_ACNT_ID=1
			long ii =890000001;
			if(args!=null && args.length>0 && BTSLUtil.isNumeric(args[0].trim()))
				ii=Integer.parseInt(args[0].trim());
			
			HashMap p_map= new HashMap();
			Request reqObj=null;
			try
			{
				p_map.put("SAFCOM_ACNT_ID","1");
				p_map.put("SAFCOM_TERM_ID","8001");
				p_map.put("SAFCOM_USER_ID","SFC_PINLESS");
				p_map.put("SAFCOM_PASSWD","pinless");
				p_map.put("URL","https://10.5.4.235:8443/axis/services/Mediator?wsdl");
				p_map.put("transfer_amount","5000");
				p_map.put("INTERFACE_ID","INTID00002");
				p_map.put("MSISDN","714379472");
				p_map.put("SENDER_MSISDN","99999999");
				p_map.put("TRANSACTION_ID","1234567890123456789");
				p_map.put("IN_TXN_ID",""+ii++);
				
			//	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
				//Request reqObj = new Request(1, 5000,"254714379472","254714379472","254714379472","254714379472"+"-"+"1234567891234567890","pinless","8001","1234567890","SFC_PINLESS");
				
			}
			catch(Exception e)
			{
				
			}
			return p_map;
		}
		/**
		 * This method will return   request Object for Credit action.
		 * @param	HashMap	p_map
		 * @return	Request
		 * @throws	Exception
		 */
			private String generateRechargeDebitRequestObject(HashMap p_map) throws Exception
		    {    if (_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequestObject", "Entered p_map= " + p_map + " cOUNTRY MSISDN" + InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"), (String)p_map.get("MSISDN")));
		    Request reqObj = null;
		    JSONObject json = null;
		    
		    try
		    {
		      Map<String, Object> jsonValues = new HashMap();
		      LocalDateTime now = LocalDateTime.now();
		      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		      Long validity =Long.parseLong((String)p_map.get("DEFAULT_IN_VALIDAITY"));
		      jsonValues.put("CreateServiceAccountAdjustmentListVBMRequest", new CreateServiceAccountAdjustmentListVBMRequest( 
		        new ServiceAccountAdjustmentVBO(
		        new IDs( InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"), (String)p_map.get("MSISDN"))), 
		        new ValidityPeriod( new FromDate( now.format(formatter)), new ToDate(now.plusDays((long)validity).format(formatter))), 
		        new Details(Long.parseLong(String.valueOf(Integer.parseInt(String.valueOf(p_map.get("REQUESTED_AMOUNT")))*Integer.parseInt(String.valueOf(p_map.get("MULT_FACTOR")))))), 
		        new Parts(new AdjustmentSpecification((String)p_map.get("ACCOUNT_NAME"), (String)p_map.get("ACCOUNT_NAME_DESC"))), 
		        new RelatedServiceAccountAdjustments(new RelatedServiceAccountAdjustment[]{new RelatedServiceAccountAdjustment((String)p_map.get("OPERATION_NAME"),(String)p_map.get("OPERATION_NAME_DESC"))
		        		,new RelatedServiceAccountAdjustment((String)p_map.get("ADJUSTMENT_TYPE"),(String)p_map.get("ADJUSTMENTTYPE_DESC")),
		        		new RelatedServiceAccountAdjustment((String)p_map.get("ADDINFO_NAME"),(String)p_map.get("ADDINFO_DESC"))}))));
		      
		      json = new JSONObject(jsonValues);
		      ObjectMapper mapper = new ObjectMapper();
		      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		      mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		      mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
		                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
		                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
		                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
		                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		      String jsonInString = mapper.writeValueAsString(json);
		      if (_log.isDebugEnabled()) { _log.debug("generateRechargeDebitRequestObject", "Exiting  request jsonInString:" + jsonInString);
		      }
		      return jsonInString;


		    }
		    catch (Exception e)
		    {
		      _log.errorTrace("generateRechargeDebitRequestObject", e);
		      _log.error("generateRechargeDebitRequestObject", "Exception e:" + e.getMessage());
		      throw e;
		    }
		    finally
		    {
		      if (_log.isDebugEnabled()) { _log.debug("generateRechargeDebitRequestObject", "Exiting  request:" + json.toString());
		      }
		    }
		 
}
			/**
			 * @param p_respObj
			 * @return
			 * @throws Exception
			 */
			private HashMap parseRechargeDebitResponseObject(Response p_respObj) throws Exception
			{    HashMap map = null;
		    int status = 0;
		    
		    try
		    {
		      map = new HashMap();
		  	  if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", "Entered p_respObj: , entity: " + p_respObj.getEntity().toString() + " status:" + p_respObj.getStatus());
		      }
		      
		      /* BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
		     	StringBuffer result = new StringBuffer();
		      String line = "";
		      while ((line = rd.readLine()) != null) {
		        result.append(line);
		      }*/
		      
		      String result = p_respObj.readEntity(String.class);
		      //String result;
		      //result =p_respObj.get.getOutputStream().toString();
		      
		      if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", " result: " + result);
		      }
		      JSONParser parser = new JSONParser();
		      JSONObject resultJson = (JSONObject)parser.parse(result.toString());
		      if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", " resultJson: " + resultJson.toString());
		      }
		  //    JSONObject resultObj = (JSONObject)resultJson.get("Response");
		    
		      JSONObject createServiceAccountResponse = (JSONObject)resultJson.get("CreateServiceAccountAdjustmentListVBMResponse");
		      if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", " createServiceAccountResponse: " + createServiceAccountResponse);
		      }
		      
		      JSONObject serviceAccountAdjustmentVBO =  ((JSONObject)((JSONArray)createServiceAccountResponse.get("ServiceAccountAdjustmentVBO")).get(0));
		      if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", " serviceAccountAdjustmentVBO: " + serviceAccountAdjustmentVBO);
		      }
		      
		      
		      String Desc = (String)serviceAccountAdjustmentVBO.get("Desc");
		      if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", " Desc: " + Desc);
		      }
		      
		      String Status =  (String)serviceAccountAdjustmentVBO.get("Status");
		      if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", " Status: " + Status);
		      }
		      
		      Long statusCode = Long.parseLong(Status);
		      if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", " statusCode: " + statusCode);
		      }
		      
		      if (!SafcomReversalI.RESULT_OK.equals(Status))
		      {
		        map.put("RESP_STATUS", Desc);
		        return map;
		      }
		      map.put("RESP_STATUS", statusCode.toString());
		
		    }
		    catch (Exception e)
		    {
		      _log.error("parseRechargeDebitResponseObject", "Exception e:" + e.getMessage());
		      throw e;
		    }
		    finally
		    {
		      if (_log.isDebugEnabled()) _log.debug("parseRechargeDebitResponseObject", "Exit  map:" + map); } if (_log.isDebugEnabled()) { _log.debug("parseRechargeDebitResponseObject", "Exit  map:" + map);
		    }
		    return map;
}
		
}


