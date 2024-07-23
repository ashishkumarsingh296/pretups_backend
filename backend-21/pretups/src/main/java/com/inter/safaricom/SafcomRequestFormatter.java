/*
 * Created on Jun 17, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.safaricom;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.inter.safaricom.safaricomstub.Request;
import com.inter.safaricom.safaricomstub.Response;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;

	public class SafcomRequestFormatter{
	    public Log _log=LogFactory.getLog("SafcomRequestFormatter".getClass().getName());
	    private static long _transactionIDCounter=0;
	    
	    
	/**
	 * This method will return of MML request message.
	 * This method internally calls private method to get request object. 
	 * @param	int	p_action
	 * @param	HashMap	p_map
	 * @return	Request
	 * @throws	Exception
	 */
		public Request generateRequestObject(int p_action, HashMap p_map) throws BTSLBaseException,Exception 
		{
	       if(_log.isDebugEnabled())_log.debug("generateRequestObject","Entered p_action="+p_action+" map: "+p_map);
			Request reqObj=null;
			p_map.put("action",String.valueOf(p_action));
			try
			{
				switch(p_action)
				{
					case SafcomI.ACTION_ACCOUNT_INFO: 
					{
						reqObj=generateGetAccountInfoRequestObject(p_map);
						break;	
					}
					case SafcomI.ACTION_RECHARGE_CREDIT: 
					{
						reqObj=generateRechargeCreditRequestObject(p_map);
						break;	
					}	
					case SafcomI.ACTION_IMMEDIATE_DEBIT: 
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
				if (_log.isDebugEnabled())_log.debug("generateRequestObject", "Exited"+ ",AccountID: "+reqObj.getAccountID()+", Amount: "+reqObj.getAmount()+", Msisdn: "+reqObj.getMsisdn()+", Opermsisdn: "+reqObj.getOpermsisdn()+", Password: "+reqObj.getPassword()+", TerminalID: "+reqObj.getTerminalID()+", TransactionID: "+reqObj.getTransactionID()+", UserID: "+reqObj.getUserID());
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
		    if(_log.isDebugEnabled())_log.debug("parseResponseObject","Entered p_action="+p_action+", Status: "+p_respObj.getStatus()+",AccountID: "+p_respObj.getAccountID()+", Amount: "+p_respObj.getAmount()+", Msisdn: "+p_respObj.getMsisdn()+", Opermsisdn: "+p_respObj.getOpermsisdn()+", TerminalID: "+p_respObj.getTerminalID()+", TransactionID: "+p_respObj.getTransactionID());
		    HashMap map=null;
			try
			{
				switch(p_action)
				{
					case SafcomI.ACTION_ACCOUNT_INFO: 
					{
						map=parseGetAccountInfoResponseObject(p_respObj);
						break;	
					}
					case SafcomI.ACTION_RECHARGE_CREDIT: 
					{
					    map=parseRechargeCreditResponseObject(p_respObj);
						break;	
					}	
					case SafcomI.ACTION_IMMEDIATE_DEBIT: 
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
		private Request generateGetAccountInfoRequestObject(HashMap p_map) throws Exception
		{
			if(_log.isDebugEnabled())_log.debug("generateGetAccountInfoRequestObject","Entered p_map= "+p_map+" cOUNTRY MSISDN"+InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")));
			Request reqObj=null;
		    try
		    {
		    	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), 0,InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
		    //	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), 0,InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN")),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));	  
		    }
		    catch(Exception e)
		    {
		        _log.error("generateGetAccountInfoRequest", "Exception e:" + e.getMessage());
		        throw e;
		    }
		    finally
		    {
		        if(_log.isDebugEnabled()) _log.debug("generateGetAccountInfoRequest","Exiting  request:,AccountID: "+reqObj.getAccountID()+", Amount: "+reqObj.getAmount()+", Msisdn: "+reqObj.getMsisdn()+", Opermsisdn: "+reqObj.getOpermsisdn()+", Password: "+reqObj.getPassword()+", TerminalID: "+reqObj.getTerminalID()+", TransactionID: "+reqObj.getTransactionID()+", UserID: "+reqObj.getUserID());
		    }
		    return reqObj;
		}
	/**
	 * This method parse the response for Acount INfo from Response object and puts into HashMap and returns it
	 * @param	Response	p_respObj
	 * @return	HashMap map
	 * @throws  Exception
	 */
		private HashMap parseGetAccountInfoResponseObject(Response p_respObj) throws Exception
		{		
			if(_log.isDebugEnabled())_log.debug("parseGetAccountInfoResponse","Entered p_respObj: "+", Status: "+p_respObj.getStatus()+",AccountID: "+p_respObj.getAccountID()+", Amount: "+p_respObj.getAmount()+", Msisdn: "+p_respObj.getMsisdn()+", Opermsisdn: "+p_respObj.getOpermsisdn()+", TerminalID: "+p_respObj.getTerminalID()+", TransactionID: "+p_respObj.getTransactionID());
		    HashMap map=null;
		    int status=0;
		    	      
		    try
		    { 
		    	map = new HashMap();
	        	status=p_respObj.getStatus();
	          if(SafcomI.RESULT_INT_OK!=status)//if status is not OK then return else proceed with other values
				{
					map.put("RESP_STATUS",String.valueOf(p_respObj.getStatus()));
					return map;
				} 
	            map.put("RESP_STATUS",String.valueOf(p_respObj.getStatus()));
	            map.put("RESP_ACNT_ID",String.valueOf(p_respObj.getAccountID()));
	            map.put("RESP_BALANCE",String.valueOf(p_respObj.getAmount()));
	            map.put("RESP_MSISDN",p_respObj.getMsisdn());
	            map.put("RESP_OPERMSISDN",p_respObj.getOpermsisdn());
	            map.put("RESP_TERM_ID",p_respObj.getTerminalID());
	            map.put("RESP_TXN_ID",p_respObj.getTransactionID());
		    }
		    catch(Exception e)
		    {
		    	_log.error("parseGetAccountInfoResponse", "Exception e:" + e.getMessage());
		        throw e;
		    }
		    finally
		    {	    	
		    	if(_log.isDebugEnabled())_log.debug("parseGetAccountInfoResponse","Exit  map:"+map);
		    }
		    return map;
		}
	/**
	 * This method will return   request Object for Credit action.
	 * @param	HashMap	p_map
	 * @return	Request
	 * @throws	Exception
	 */
		private Request generateRechargeCreditRequestObject(HashMap p_map) throws Exception
	    {
			if(_log.isDebugEnabled())_log.debug("generateRechargeCreditRequestObject","Entered p_map= "+p_map+" cOUNTRY MSISDN"+InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")));
			Request reqObj=null;
		    try
		    {
		    	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
		    	//reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN")),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));	
		    }
		    catch(Exception e)
		    {
		        _log.error("generateRechargeCreditRequestObject", "Exception e:" + e.getMessage());
		        throw e;
		    }
		    finally
		    {
		        if(_log.isDebugEnabled()) _log.debug("generateRechargeCreditRequestObject","Exiting  request:,AccountID: "+reqObj.getAccountID()+", Amount: "+reqObj.getAmount()+", Msisdn: "+reqObj.getMsisdn()+", Opermsisdn: "+reqObj.getOpermsisdn()+", Password: "+reqObj.getPassword()+", TerminalID: "+reqObj.getTerminalID()+", TransactionID: "+reqObj.getTransactionID()+", UserID: "+reqObj.getUserID());
		    }
		    return reqObj;
		}
	/**
	 * This method parse the response for Credit from Response Object and puts into HashMap and returns it
	 * @param	Response p_respObj
	 * @return	HashMap map
	 * @throws  Exception
	 */
		private HashMap parseRechargeCreditResponseObject(Response p_respObj) throws Exception
		{		
			if(_log.isDebugEnabled())_log.debug("parseRechargeCreditResponseObject","Entered p_respObj: "+", Status: "+p_respObj.getStatus()+",AccountID: "+p_respObj.getAccountID()+", Amount: "+p_respObj.getAmount()+", Msisdn: "+p_respObj.getMsisdn()+", Opermsisdn: "+p_respObj.getOpermsisdn()+", TerminalID: "+p_respObj.getTerminalID()+", TransactionID: "+p_respObj.getTransactionID());
		    HashMap map=null;
		    int status=0;
		    	      
		    try
		    { 
		    	map = new HashMap();
	        	status=p_respObj.getStatus();
	          
	        	if(SafcomI.RESULT_INT_OK!=status)//if status is not OK then return else proceed with other values
				{
					map.put("RESP_STATUS",String.valueOf(p_respObj.getStatus()));
					return map;
				}            
	        	map.put("RESP_STATUS",String.valueOf(p_respObj.getStatus()));
	            map.put("RESP_ACNT_ID",String.valueOf(p_respObj.getAccountID()));
	            map.put("RESP_XFER_AMT",String.valueOf(p_respObj.getAmount()));
	            map.put("RESP_MSISDN",p_respObj.getMsisdn());
	            map.put("RESP_OPERMSISDN",p_respObj.getOpermsisdn());
	            map.put("RESP_TERM_ID",p_respObj.getTerminalID());
	            map.put("RESP_TXN_ID",p_respObj.getTransactionID());
		    }
		    catch(Exception e)
		    {
		    	_log.error("parseRechargeCreditResponseObject", "Exception e:" + e.getMessage());
		        throw e;
		    }
		    finally
		    {	    	
		    	if(_log.isDebugEnabled())_log.debug("parseRechargeCreditResponseObject","Exit  map:"+map);
		    }
		    return map;
		}
		
		
		/**
		 * @param p_map
		 * @return
		 * @throws BTSLBaseException
		 */
		protected static String getINTransactionID(HashMap p_map) throws BTSLBaseException
		{
			String txnID="";
			try
			{
				txnID=	(String)p_map.get("TRANSACTION_ID");
				txnID=txnID.replaceAll("\\.",""); 
				p_map.put("IN_RECON_ID",txnID);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return txnID;		
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
				
				reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
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
			private Request generateRechargeDebitRequestObject(HashMap p_map) throws Exception
		    {
				if(_log.isDebugEnabled())_log.debug("generateRechargeDebitRequestObject","Entered p_map= "+p_map+" cOUNTRY MSISDN"+InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")));
				Request reqObj=null;
			    try
			    {
			    	reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), -Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
			    	//reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN"))+"-"+(String)p_map.get("TRANSACTION_ID"),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));
			    	//reqObj = new Request(Integer.parseInt((String)p_map.get("SAFCOM_ACNT_ID")), Long.parseLong((String)p_map.get("transfer_amount")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("MSISDN")),InterfaceUtil.getFilterMSISDN((String)p_map.get("INTERFACE_ID"),(String)p_map.get("SENDER_MSISDN")),(String)p_map.get("SAFCOM_PASSWD"),(String)p_map.get("SAFCOM_TERM_ID"),(String)p_map.get("IN_TXN_ID"),(String)p_map.get("SAFCOM_USER_ID"));	
			    }
			    catch(Exception e)
			    {
			        _log.error("generateRechargeDebitRequestObject", "Exception e:" + e.getMessage());
			        throw e;
			    }
			    finally
			    {
			        if(_log.isDebugEnabled()) _log.debug("generateRechargeDebitRequestObject","Exiting  request:,AccountID: "+reqObj.getAccountID()+", Amount: "+reqObj.getAmount()+", Msisdn: "+reqObj.getMsisdn()+", Opermsisdn: "+reqObj.getOpermsisdn()+", Password: "+reqObj.getPassword()+", TerminalID: "+reqObj.getTerminalID()+", TransactionID: "+reqObj.getTransactionID()+", UserID: "+reqObj.getUserID());
			    }
			    return reqObj;
			}
			/**
			 * @param p_respObj
			 * @return
			 * @throws Exception
			 */
			private HashMap parseRechargeDebitResponseObject(Response p_respObj) throws Exception
			{		
				if(_log.isDebugEnabled())_log.debug("parseRechargeDebitResponseObject","Entered p_respObj: "+", Status: "+p_respObj.getStatus()+",AccountID: "+p_respObj.getAccountID()+", Amount: "+p_respObj.getAmount()+", Msisdn: "+p_respObj.getMsisdn()+", Opermsisdn: "+p_respObj.getOpermsisdn()+", TerminalID: "+p_respObj.getTerminalID()+", TransactionID: "+p_respObj.getTransactionID());
			    HashMap map=null;
			    int status=0;
			    	      
			    try
			    { 
			    	map = new HashMap();
		        	status=p_respObj.getStatus();
		          
		        	if(SafcomI.RESULT_INT_OK!=status)//if status is not OK then return else proceed with other values
					{
						map.put("RESP_STATUS",String.valueOf(p_respObj.getStatus()));
						return map;
					}            
		        	map.put("RESP_STATUS",String.valueOf(p_respObj.getStatus()));
		            map.put("RESP_ACNT_ID",String.valueOf(p_respObj.getAccountID()));
		            map.put("RESP_XFER_AMT",String.valueOf(p_respObj.getAmount()));
		            map.put("RESP_MSISDN",p_respObj.getMsisdn());
		            map.put("RESP_OPERMSISDN",p_respObj.getOpermsisdn());
		            map.put("RESP_TERM_ID",p_respObj.getTerminalID());
		            map.put("RESP_TXN_ID",p_respObj.getTransactionID());
			    }
			    catch(Exception e)
			    {
			    	_log.error("parseRechargeDebitResponseObject", "Exception e:" + e.getMessage());
			        throw e;
			    }
			    finally
			    {	    	
			    	if(_log.isDebugEnabled())_log.debug("parseRechargeDebitResponseObject","Exit  map:"+map);
			    }
			    return map;
			}
		
}


