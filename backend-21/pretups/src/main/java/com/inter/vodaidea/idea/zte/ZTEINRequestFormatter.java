package com.inter.vodaidea.idea.zte;


import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

/**
 * @ZTERequestFormatter.java
 * Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *  All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *     Author				     Date			        History
 *-------------------------------------------------------------------------------------------------
 * Vipan						Sep 13, 2013		Initial Creation
 * -----------------------------------------------------------------------------------------------
 * This class is responsible to generate the request and parse the response for the ZTE interface.
 */
public class ZTEINRequestFormatter{
	static Log _logger = LogFactory.getLog(ZTEINRequestFormatter.class.getName());
	/**
	 * This method will return of MML request message.
	 * This method internally calls private method to get MML request string. 
	 * @param	int	p_action
	 * @param	HashMap	p_map
	 * @return	String
	 * @throws	Exception
	 */
	public String generateRequest(String fileCacheId,int p_action, HashMap p_map) throws BTSLBaseException,Exception 
	{
		if(_logger.isDebugEnabled())_logger.debug("generateRequest"," Entered p_action="+p_action+" map: "+p_map);
		String str=null;
		p_map.put("action",String.valueOf(p_action));
		try
		{
			switch(p_action)
			{
			case ZTEINI.ACTION_LOGIN: 
			{
				str=generateLoginRequest(fileCacheId,p_map);
				break;	
			}
			case ZTEINI.ACTION_LOGOUT: 
			{
				str=generateLogoutRequest(fileCacheId,p_map);
				break;	
			}
			case ZTEINI.ACTION_ACCOUNT_INFO: 
			{
				str=generateGetAccountInfoRequest(fileCacheId,p_map);
				break;	
			}
			case ZTEINI.ACTION_RECHARGE_CREDIT: 
			{
				str=generateRechargeCreditRequest(fileCacheId,p_map);
				break;	
			}
			case ZTEINI.ACTION_RECHARGE_ADJUST: 
			{
				str=generateRechargeDebitRequest(fileCacheId,p_map);
				break;	
			}
			case ZTEINI.ACTION_HEART_BEAT: 
			{
				str=generateHeartBeatRequest(p_map);
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
			_logger.error("generateRequest ","Exception e:" + e.getMessage());
			throw e;
		}//end of catch-Exception 
		finally
		{
			if(_logger.isDebugEnabled())_logger.debug("generateRequest"," Exited Request String: str="+str);
		}//end of finally
		//For the local testing request string ended with colon, remove this colon while the delivery of code
		return str;
	}//end of generateRequest
	/**
	 * This method internally calls methods (according to p_action parameter) to get response HashMap and returns it.  
	 * @param	int action
	 * @param	String	responseStr
	 * @return	HashMap map
	 * @throws  BTSLBaseException,Exception
	 */
	public HashMap<String,String> parseResponse(int p_action, String p_responseStr) throws BTSLBaseException,Exception 
	{
		if(_logger.isDebugEnabled())_logger.debug("parseResponse"," Entered p_action="+p_action+" p_responseStr="+p_responseStr);
		HashMap<String,String> map=null;
		try
		{
			switch(p_action)
			{
			case ZTEINI.ACTION_LOGIN: 
			{
				map=parseLoginResponse(p_responseStr);
				break;	
			}
			case ZTEINI.ACTION_LOGOUT: 
			{
				map=parseLogoutResponse(p_responseStr);
				break;	
			}
			case ZTEINI.ACTION_ACCOUNT_INFO: 
			{
				map=parseGetAccountInfoResponse(p_responseStr);
				break;	
			}
			case ZTEINI.ACTION_RECHARGE_CREDIT: 
			{
				map=parseRechargeCreditResponse(p_responseStr);
				break;	
			}
			case ZTEINI.ACTION_RECHARGE_ADJUST: 
			{
				map=parseRechargeDebitResponse(p_responseStr);
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
			_logger.error("parseResponse"," Exception e:" + e.getMessage());
			throw e;
		}//end of catch-Exception
		finally
		{
			if(_logger.isDebugEnabled())_logger.debug("parseRespons"," Exiting map: "+map);
		}//end of finally
		return map;	
	}//end of parseResponse
	/**
	 * This method will return  MML request message for Account info (validate action).
	 * @param	HashMap	p_map
	 * @return	String
	 * @throws	Exception
	 */
	private String generateGetAccountInfoRequest(String fileCacheId,HashMap p_map) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("generateGetAccountInfoRequest"," Entered p_map= "+p_map);

		String requestStr=null;
		String requestData=null;   

		try
		{

			String startFlag = FileCache.getValue(fileCacheId,"START_FLAG");
			if(InterfaceUtil.isNullString(startFlag))
			{
				_logger.error("generateAccountInfoRequest "," Value of START_FLAG is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String versionNumber = FileCache.getValue(fileCacheId,"VERSION_NUMBER");
			if(InterfaceUtil.isNullString(versionNumber))
			{
				_logger.error("generateAccountInfoRequest"," Value of VERSION_NUMBER is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			//get TERM from IN File Cache and put it it request map
			String term = FileCache.getValue(fileCacheId,"TERMINALID");
			if(InterfaceUtil.isNullString(term))
			{
				_logger.error("generateAccountInfoRequest"," Value of TERM is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String acntInfoService = FileCache.getValue(fileCacheId,"SERVICE_NAME");
			if(InterfaceUtil.isNullString(acntInfoService))
			{
				_logger.error("generateAccountInfoRequest ","Value of ACCOUNT_INFO_SERIVICE is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String sessionid =(String)p_map.get("SESSIONID");

			String dlgCon =FileCache.getValue(fileCacheId,"DLGCON");
			if(InterfaceUtil.isNullString(dlgCon))
			{
				_logger.error("generateAccountInfoRequest"," Value of dlgCon is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}


			//get RSV from IN File Cache and put it it request map
			String rsv = FileCache.getValue(fileCacheId,"RSV");
			if(InterfaceUtil.isNullString(rsv))
			{
				_logger.error("generateAccountInfoRequest"," Value of RSV is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}


			//get DLGCTRL from IN File Cache and put it it request map
			String dlgCtrl =FileCache.getValue(fileCacheId,"DLGCTRL");
			if(InterfaceUtil.isNullString(dlgCtrl))
			{
				_logger.error("generateAccountInfoRequest"," Value of DLGCTRL is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}


			//get TSRV from IN File Cache and put it it request map
			String tsrv = FileCache.getValue(fileCacheId,"TSRV");
			if(InterfaceUtil.isNullString(tsrv))
			{
				_logger.error("generateAccountInfoRequest"," Value of TSRV is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}


			//get START_FLAG from IN File Cache and put it it request map

			//get DLGCON from IN File Cache and put it it request map
			String dlgLgn = FileCache.getValue(fileCacheId,"DLGLGN");
			if(InterfaceUtil.isNullString(dlgLgn))
			{
				_logger.error("generateAccountInfoRequest","Value of DLGLGN is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String acntInfoCommand = FileCache.getValue(fileCacheId,"ACNTINFO_COMMAND");
			if(InterfaceUtil.isNullString(acntInfoCommand))
			{
				_logger.error("generateAccountInfoRequest"," Value of ACNTINFO_COMMAND is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String msisdn=(String)p_map.get("MSISDN");
			String rechargeID=(String)p_map.get("TRANSACTION_ID");

			StringBuffer headerInfoBuffer=new StringBuffer(versionNumber);//Version-number

			headerInfoBuffer.append(spacePad(term,8," ",'l'));//TERMINAL		    
			headerInfoBuffer.append(spacePad(acntInfoService,8," ",'r'));//Account Info service		
			headerInfoBuffer.append(spacePad(sessionid,8,"0",'l'));//session id		    
			headerInfoBuffer.append(dlgCon);//represents session continued
			headerInfoBuffer.append(spacePad(rsv,4,"0",'l'));//reserve
			headerInfoBuffer.append(spacePad((String)p_map.get("TRANSACTIONHEADERID"),8,"0",'l'));//txn id in header of request
			headerInfoBuffer.append(spacePad(dlgCtrl,6," ",'r'));//represents txn begin,continued
			headerInfoBuffer.append(spacePad(tsrv,4,"0",'l'));//txn reserve


			String headerInfoStr=headerInfoBuffer.toString();

			//prepare MML command and service parameter		    
			StringBuffer requestDataBuffer=new StringBuffer(acntInfoCommand);

			requestDataBuffer.append("MDN="+msisdn+",");
			requestDataBuffer.append("TRANSACTIONID="+rechargeID);
			requestData=requestDataBuffer.toString();
			requestData=lenpad(requestData);	    
			String headerAndCommand=headerInfoStr + requestData;
			String chckSum=getCheckSum(headerAndCommand);	     
			requestStr=startFlag.trim()+len(headerAndCommand,4)+headerAndCommand+chckSum;
		}
		catch(Exception e)
		{
			_logger.error("generateGetAccountInfoRequest"," Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("generateGetAccountInfoRequest"," Exiting  requestStr:"+requestStr);
		}
		return requestStr;
	}
	/**
	 * This method parse the response for Acount INfo from MML String into HashMap and returns it
	 * @param	String	p_responseStr
	 * @return	HashMap map
	 * @throws  Exception
	 */
	private HashMap<String,String> parseGetAccountInfoResponse(String p_responseStr) throws Exception
	{		

		if(_logger.isDebugEnabled())_logger.debug("parseGetAccountInfoResponse"," Entered p_responseStr: "+p_responseStr);
		HashMap<String,String> map=null;
		int index=0;
		String status=null;
		String attr=null;
		String transactionId=null;
		String[] attrToken=null;
		String[] resultToken=null;
		int attrCnt=0;
		int resultCnt=0;

		String result=null;

		try
		{

			map = new HashMap<String,String>();
			index=p_responseStr.indexOf("RETN=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			status=(p_responseStr.substring(index+5,p_responseStr.indexOf(",",index))).trim();//status of txn
			map.put("RETN",status);

			index=p_responseStr.indexOf("DESC=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

			map.put("DESC",(p_responseStr.substring(index+5,p_responseStr.lastIndexOf("\""))).trim());            

			transactionId=getResponseTransactionId(p_responseStr);//get transactionId 
			map.put("IN_TRANSACTIONHEADERID",transactionId);//put transactionId in map 

			if(map.get("RETN").equals("0000"))
            {
	        
			    index=p_responseStr.indexOf("TRANSACTIONID=");
				  
	            if(index<0)
	            	throw new BTSLBaseException(this,"parseGetAccountInfoResponse",InterfaceErrorCodesI.ERROR_RESPONSE);
	            
	            map.put("IN_TRANSACTIONID",(p_responseStr.substring(index+14,p_responseStr.indexOf(",",index))).trim());
	            p_responseStr=p_responseStr.substring(0,index);
	        
			if(!ZTEINI.RESULT_OK.equals(status))//if status is not OK then return else proceed with other values
				return map;




			index=p_responseStr.indexOf("ATTR=");
			attr=p_responseStr.substring(index+5,p_responseStr.indexOf(",",index));//get parameters queried in validate separated by '&'. (SUBCOSID&SERVICESTOP&ACTIVESTOP&BALANCE&LANGUAGETYPE&FRAUDLOCK&SUSPENDSTOP)

			index=p_responseStr.indexOf("RESULT=");

			//result=p_responseStr.substring(index+8,p_responseStr.indexOf("\"",index+8));//get values of parameters queried in validate separated by '|'(1|1|1|1|1|1|1|1|)

			result=p_responseStr.substring(index+7);//get values of parameters queried in validate separated by '|'(1|1|1|1|1|1|1|1|)

			attrToken=attr.split("&");
			resultToken=result.split("&");

			attrCnt=attrToken.length;
			resultCnt=resultToken.length;
			if((attrCnt != resultCnt) || attrCnt==0)
			{
				throw new BTSLBaseException(this,"parseGetAccountInfoResponse",InterfaceErrorCodesI.ERROR_RESPONSE);
			}
			for(int i=0;i<attrCnt;i++)
			{
				map.put(attrToken[i],resultToken[i]);
			}
            }


		}
		catch(Exception e)
		{
			_logger.error("parseGetAccountInfoResponse"," Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{	    	
			if(_logger.isDebugEnabled())_logger.debug("parseGetAccountInfoResponse"," Exit  map:"+map);
		}
		return map;
	}

	/**
	 * This method will return  MML request message for Credit action.
	 * @param	HashMap	p_map
	 * @return	String
	 * @throws	Exception
	 */
	private String generateRechargeCreditRequest(String fileCacheId,HashMap<String,String> p_map) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("generateRechargeCreditRequest"," Entered p_map= "+p_map);

		String adddays=null;
		String trannsferAmt=null;
		String requestedAmount=null;
		String requestStr=null;
		String requestData=null;   

		try
		{
			String startFlag = FileCache.getValue(fileCacheId,"START_FLAG");
			if(InterfaceUtil.isNullString(startFlag))
			{
				_logger.error("generateRechargeCreditRequest "," Value of START_FLAG is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String versionNumber = FileCache.getValue(fileCacheId,"VERSION_NUMBER");
			if(InterfaceUtil.isNullString(versionNumber))
			{
				_logger.error("generateRechargeCreditRequest"," Value of VERSION_NUMBER is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			//get TERM from IN File Cache and put it it request map
			String term = FileCache.getValue(fileCacheId,"TERMINALID");
			if(InterfaceUtil.isNullString(term))
			{
				_logger.error("generateRechargeCreditRequest"," Value of TERM is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String serviceName = FileCache.getValue(fileCacheId,"SERVICE_NAME");
			if(InterfaceUtil.isNullString(serviceName))
			{
				_logger.error("generateRechargeCreditRequest ","Value of ACCOUNT_INFO_SERIVICE is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String source = FileCache.getValue(fileCacheId,"SOURCE");
			if(InterfaceUtil.isNullString(source))
			{
				_logger.error("generateRechargeCreditRequest ","Value of SOURCE is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			
			String sessionID =(String)p_map.get("SESSIONID") ;

			String dlgCon =FileCache.getValue(fileCacheId,"DLGCON");
			if(InterfaceUtil.isNullString(dlgCon))
			{
				_logger.error("generateRechargeCreditRequest"," Value of dlgCon is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			//get RSV from IN File Cache and put it it request map
			String rsv = FileCache.getValue(fileCacheId,"RSV");
			if(InterfaceUtil.isNullString(rsv))
			{
				_logger.error("generateRechargeCreditRequest"," Value of RSV is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			//get DLGCTRL from IN File Cache and put it it request map
			String dlgCtrl =FileCache.getValue(fileCacheId,"DLGCTRL");
			if(InterfaceUtil.isNullString(dlgCtrl))
			{
				_logger.error("generateRechargeCreditRequest"," Value of DLGCTRL is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			//get TSRV from IN File Cache and put it it request map
			String tsrv = FileCache.getValue(fileCacheId,"TSRV");
			if(InterfaceUtil.isNullString(tsrv))
			{
				_logger.error("generateRechargeCreditRequest"," Value of TSRV is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			//get TSRV from IN File Cache and put it it request map
			String retMSISDNReq = FileCache.getValue(fileCacheId,"ZTE_RETAILER_MSISDN_REQUIRED_AT_IN");
			if(InterfaceUtil.isNullString(retMSISDNReq))
			{
				_logger.error("generateRechargeCreditRequest"," Value of ZTE_RETAILER_MSISDN_REQUIRED_AT_IN is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String zteEXTDATAReq = FileCache.getValue(fileCacheId,"ZTE_EXT_DATA_REQUIRED");
			if(InterfaceUtil.isNullString(zteEXTDATAReq))
			{
				_logger.error("generateRechargeCreditRequest"," Value of ZTE_EXT_DATA_REQUIRED is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			
			String rechargeCommand = FileCache.getValue(fileCacheId,"RECHARGE_COMMAND");
			if(InterfaceUtil.isNullString(rechargeCommand))
			{
				_logger.error("generateRechargeCreditRequest"," Value of ACNTINFO_COMMAND is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String rptflag =  FileCache.getValue(fileCacheId,"RPTFLAG");
			if(InterfaceUtil.isNullString(rptflag))
			{
				_logger.error("generateRechargeCreditRequest","Value of RPTFLAG is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "DLGCON is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}


			String type =  FileCache.getValue(fileCacheId,"TYPE");
			if(InterfaceUtil.isNullString(type))
			{
				_logger.error("generateRechargeCreditRequest","Value of type is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "DLGCON is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String bundleCode="";
			try{
				bundleCode=(String)p_map.get("CARD_GROUP_SELECTOR");
			}catch (Exception e) {
				bundleCode="1";
			}
		  
			String msisdn=(String)p_map.get("MSISDN");

			String transactionID=(String)p_map.get("TRANSACTIONHEADERID");

			//String rechargeID=(String)p_map.get("TRANSACTION_ID");
			String rechargeID=(String)p_map.get("SERIAL_NUMBER");
			trannsferAmt=(String)p_map.get("transfer_amount");

			//requestedAmount=(String)p_map.get("REQUESTED_AMOUNT");
			requestedAmount=(String)p_map.get("MRP");
			adddays=(String)p_map.get("VALIDITY_DAYS");

			StringBuffer headerInfoBuffer=new StringBuffer(versionNumber);//Version-number

			headerInfoBuffer.append(spacePad(term,8," ",'l'));//TERMINAL		    
			headerInfoBuffer.append(spacePad(serviceName,8," ",'r'));//Account Info service		
			headerInfoBuffer.append(spacePad(sessionID,8,"0",'l'));//session id		    
			headerInfoBuffer.append(dlgCon);//represents session continued
			headerInfoBuffer.append(spacePad(rsv,4,"0",'l'));//reserve
			headerInfoBuffer.append(spacePad(transactionID,8,"0",'l'));//txn id in header of request
			headerInfoBuffer.append(spacePad(dlgCtrl,6," ",'r'));//represents txn begin,continued
			headerInfoBuffer.append(spacePad(tsrv,4,"0",'l'));//txn reserve


			String headerInfoStr=headerInfoBuffer.toString();

			//prepare MML command and service parameter		    
			StringBuffer rechargeDataBuffer=new StringBuffer(rechargeCommand);

			rechargeDataBuffer.append("MDN="+msisdn+",");
			rechargeDataBuffer.append("ACCOUNT="+requestedAmount+",");
			rechargeDataBuffer.append("ADDDAYS="+adddays+",");
			rechargeDataBuffer.append("TYPE="+type+",");
			rechargeDataBuffer.append("SERIALNO="+rechargeID+",");
			rechargeDataBuffer.append("SOURCE="+(String)p_map.get("VOUCHER_TYPE")+(String)p_map.get("TRANSACTION_ID")+",");
			rechargeDataBuffer.append("TOPUPAMOUNT="+trannsferAmt+",");
			rechargeDataBuffer.append("PROCESSINGFEE="+(String)p_map.get("ACCESS_FEE"));
			
			rechargeDataBuffer.append(",RetailerMsisdn="+"");
			rechargeDataBuffer.append(",RechargeOption="+"");
					
			String cg=(String)p_map.get("CARD_GROUP");
			rechargeDataBuffer.append(",CardGroup="+cg);
			rechargeDataBuffer.append(",EXTDATA1="+(String)p_map.get("PRODUCT_NAME"));
			rechargeDataBuffer.append(",EXTDATA2="+(String)p_map.get("VOUCHER_SEGMENT"));
			
			
			requestData=rechargeDataBuffer.toString();
			//make the length of data request String to integral of 4
			requestData=lenpad(requestData);
			String headerAndCommand=headerInfoStr+requestData;
			//get checksum of MML message prepared.
			String chckSum=getCheckSum(headerAndCommand);		      
			requestStr=startFlag.trim()+len(headerAndCommand,4)+headerAndCommand+chckSum;
		}
		catch(Exception e)
		{
			_logger.error("generateRechargeCreditRequest"," Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("generateRechargeCreditRequest ","Exiting  requestStr:"+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method will return  MML request message for Credit action.
	 * @param	HashMap	p_map
	 * @return	String
	 * @throws	Exception
	 */
	private String generateRechargeDebitRequest(String fileCacheId,HashMap<String,String> p_map) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("generateRechargeCreditRequest"," Entered p_map= "+p_map);

		String adddays=null;
		String trannsferAmt=null;
		String requestedAmount=null;
		String requestStr=null;
		String requestData=null;   

		try
		{
			String startFlag = FileCache.getValue(fileCacheId,"START_FLAG");
			if(InterfaceUtil.isNullString(startFlag))
			{
				_logger.error("generateRechargeCreditRequest "," Value of START_FLAG is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String versionNumber = FileCache.getValue(fileCacheId,"VERSION_NUMBER");
			if(InterfaceUtil.isNullString(versionNumber))
			{
				_logger.error("generateRechargeCreditRequest"," Value of VERSION_NUMBER is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			//get TERM from IN File Cache and put it it request map
			String term = FileCache.getValue(fileCacheId,"TERMINALID");
			if(InterfaceUtil.isNullString(term))
			{
				_logger.error("generateRechargeCreditRequest"," Value of TERM is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String serviceName = FileCache.getValue(fileCacheId,"SERVICE_NAME");
			if(InterfaceUtil.isNullString(serviceName))
			{
				_logger.error("generateRechargeCreditRequest ","Value of ACCOUNT_INFO_SERIVICE is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String sessionID =(String)p_map.get("SESSIONID") ;

			String dlgCon =FileCache.getValue(fileCacheId,"DLGCON");
			if(InterfaceUtil.isNullString(dlgCon))
			{
				_logger.error("generateRechargeCreditRequest"," Value of dlgCon is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			//get RSV from IN File Cache and put it it request map
			String rsv = FileCache.getValue(fileCacheId,"RSV");
			if(InterfaceUtil.isNullString(rsv))
			{
				_logger.error("generateRechargeCreditRequest"," Value of RSV is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			//get DLGCTRL from IN File Cache and put it it request map
			String dlgCtrl =FileCache.getValue(fileCacheId,"DLGCTRL");
			if(InterfaceUtil.isNullString(dlgCtrl))
			{
				_logger.error("generateRechargeCreditRequest"," Value of DLGCTRL is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			//get TSRV from IN File Cache and put it it request map
			String tsrv = FileCache.getValue(fileCacheId,"TSRV");
			if(InterfaceUtil.isNullString(tsrv))
			{
				_logger.error("generateRechargeCreditRequest"," Value of TSRV is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}


			String rechargeCommand = FileCache.getValue(fileCacheId,"ADJUST_COMMAND");
			if(InterfaceUtil.isNullString(rechargeCommand))
			{
				_logger.error("generateRechargeCreditRequest"," Value of ACNTINFO_COMMAND is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

			String rptflag =  FileCache.getValue(fileCacheId,"RPTFLAG");
			if(InterfaceUtil.isNullString(rptflag))
			{
				_logger.error("generateRechargeCreditRequest","Value of RPTFLAG is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "DLGCON is not defined in the INFile.");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}

		
			String source = FileCache.getValue(fileCacheId,"SOURCE");
			if(InterfaceUtil.isNullString(source))
			{
				_logger.error("generateRechargeCreditRequest ","Value of SOURCE is not defined in the INFile");
				throw new BTSLBaseException(this,"setInterfaceParameters",InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			String originOperatorID=FileCache.getValue(fileCacheId, "ORIGIN_OPERATOR_ID");
			if(InterfaceUtil.isNullString(originOperatorID))
			{
				originOperatorID="VTOPUP";
			}
			originOperatorID=originOperatorID.trim()+(String)p_map.get("TRANSACTION_ID");
			
			

			String msisdn=(String)p_map.get("MSISDN");

			String transactionID=(String)p_map.get("TRANSACTIONHEADERID");

			String rechargeID=(String)p_map.get("TRANSACTION_ID");
			trannsferAmt=(String)p_map.get("transfer_amount");

			requestedAmount=(String)p_map.get("REQUESTED_AMOUNT");
			adddays=(String)p_map.get("VALIDITY_DAYS");

			StringBuffer headerInfoBuffer=new StringBuffer(versionNumber);//Version-number

			headerInfoBuffer.append(spacePad(term,8," ",'l'));//TERMINAL		    
			headerInfoBuffer.append(spacePad(serviceName,8," ",'r'));//Account Info service		
			headerInfoBuffer.append(spacePad(sessionID,8,"0",'l'));//session id		    
			headerInfoBuffer.append(dlgCon);//represents session continued
			headerInfoBuffer.append(spacePad(rsv,4,"0",'l'));//reserve
			headerInfoBuffer.append(spacePad(transactionID,8,"0",'l'));//txn id in header of request
			headerInfoBuffer.append(spacePad(dlgCtrl,6," ",'r'));//represents txn begin,continued
			headerInfoBuffer.append(spacePad(tsrv,4,"0",'l'));//txn reserve


			String headerInfoStr=headerInfoBuffer.toString();

			//prepare MML command and service parameter		    
			StringBuffer rechargeDataBuffer=new StringBuffer(rechargeCommand);

			rechargeDataBuffer.append("MDN="+msisdn+",");
			rechargeDataBuffer.append("ACCOUNT=-"+trannsferAmt+",");
			rechargeDataBuffer.append("ID="+rechargeID+",");
			rechargeDataBuffer.append("SOURCE="+originOperatorID+",");
			
			String cgFlag = FileCache.getValue(fileCacheId,"ZTE_CGFLAG_ENABLED");
			
			if(!BTSLUtil.isNullString(cgFlag) && cgFlag.equalsIgnoreCase("Y")){
				
				String flag = FileCache.getValue(fileCacheId,"Flag");
				if(BTSLUtil.isNullString(flag))
				{
					rechargeDataBuffer.append("Flag=,");
				}else{
					rechargeDataBuffer.append("Flag="+flag+",");
				}
				String adflag = FileCache.getValue(fileCacheId,"AddDaysFlag");
				if(BTSLUtil.isNullString(adflag))
				{
					rechargeDataBuffer.append("AddDaysFlag=,");
				}else{
					rechargeDataBuffer.append("AddDaysFlag="+adflag+",");
				}
				String efTime = FileCache.getValue(fileCacheId,"EFFTIME");
				if(BTSLUtil.isNullString(efTime))
				{
					rechargeDataBuffer.append("EFFTIME=,");
				}else{
					rechargeDataBuffer.append("EFFTIME="+efTime+",");
				}
				String unit = FileCache.getValue(fileCacheId,"UNIT");
				if(BTSLUtil.isNullString(unit))
				{
					rechargeDataBuffer.append("UNIT=,");
				}else{
					rechargeDataBuffer.append("UNIT="+unit+",");
				}
			}
			
			
			requestData=rechargeDataBuffer.toString();
			//make the length of data request String to integral of 4
			requestData=lenpad(requestData);
			String headerAndCommand=headerInfoStr+requestData;
			//get checksum of MML message prepared.
			String chckSum=getCheckSum(headerAndCommand);		      
			requestStr=startFlag.trim()+len(headerAndCommand,4)+headerAndCommand+chckSum;
		}
		catch(Exception e)
		{
			_logger.error("generateRechargeCreditRequest"," Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("generateRechargeCreditRequest ","Exiting  requestStr:"+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method parse the response for Credit from MML String into HashMap and returns it
	 * @param	String	p_responseStr
	 * @return	HashMap map
	 * @throws  Exception
	 */
	private HashMap<String,String> parseRechargeDebitResponse(String p_responseStr) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("parseRechargeCreditResponse ","Entered p_responseStr: "+p_responseStr);
		HashMap<String,String> map=null;
		int index=0;
		String status=null;
		String transactionId=null;

		try
		{
			map = new HashMap<String,String>();
			index=p_responseStr.indexOf("RETN=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			status=(p_responseStr.substring(index+5,p_responseStr.indexOf(",",index))).trim();//status of transaction
			map.put("RETN",status);

			transactionId=getResponseTransactionId(p_responseStr);//get transactionId 
			map.put("IN_TRANSACTIONHEADERID",transactionId);//put transactionId in map 

			index=p_responseStr.indexOf("DESC=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

			map.put("DESC",(p_responseStr.substring(index+5,p_responseStr.indexOf("\"",index))).trim());


			if(!ZTEINI.RESULT_OK.equals(status))//if status is not OK then return else proceed with other values
				return map;

			if(map.get("RETN").equals("0000"))
			{

				index=p_responseStr.indexOf("ID=");
				if(index<0)
					throw new BTSLBaseException(this,"parseRechargeCreditResponse",InterfaceErrorCodesI.ERROR_RESPONSE);

				map.put("IN_TRANSACTIONID",(p_responseStr.substring(index+3,p_responseStr.indexOf(",",index))).trim());

			}

		}
		catch(Exception e)
		{
			_logger.error("parseRechargeCreditResponse"," Exception e:" + e.getMessage());
			System.out.println("parseRechargeCreditResponse Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled())_logger.debug("parseRechargeCreditResponse"," Exit  map:"+map);
			System.out.println(map);
		}
		return map;
	}



	/**
	 * This method parse the response for Credit from MML String into HashMap and returns it
	 * @param	String	p_responseStr
	 * @return	HashMap map
	 * @throws  Exception
	 */
	private HashMap<String,String> parseRechargeCreditResponse(String p_responseStr) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("parseRechargeCreditResponse ","Entered p_responseStr: "+p_responseStr);
		HashMap<String,String> map=null;
		int index=0;
		String status=null;
		String transactionId=null;

		try
		{
			map = new HashMap<String,String>();
			index=p_responseStr.indexOf("RETN=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			status=(p_responseStr.substring(index+5,p_responseStr.indexOf(",",index))).trim();//status of transaction
			map.put("RETN",status);

			transactionId=getResponseTransactionId(p_responseStr);//get transactionId 
			map.put("IN_TRANSACTIONHEADERID",transactionId);//put transactionId in map 

			index=p_responseStr.indexOf("DESC=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);

			map.put("DESC",(p_responseStr.substring(index+5,p_responseStr.indexOf("\"",index))).trim());


			if(!ZTEINI.RESULT_OK.equals(status))//if status is not OK then return else proceed with other values
				return map;

			if(map.get("RETN").equals("0000"))
			{
				index=p_responseStr.indexOf("SERIALNO=");
				if(index<0)
					throw new BTSLBaseException(this,"parseRechargeCreditResponse",InterfaceErrorCodesI.ERROR_RESPONSE);

				map.put("IN_TRANSACTIONID",(p_responseStr.substring(index+9,p_responseStr.indexOf(",",index))).trim());
				index=p_responseStr.indexOf("AMOUNT=");
				if(index<0)
					throw new BTSLBaseException(this,"parseRechargeCreditResponse",InterfaceErrorCodesI.ERROR_RESPONSE);

				map.put("accountPostBalance",(p_responseStr.substring(index+7,p_responseStr.indexOf(",",index))).trim());

			}

		}
		catch(Exception e)
		{
			_logger.error("parseRechargeCreditResponse"," Exception e:" + e.getMessage());
			System.out.println("parseRechargeCreditResponse Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled())_logger.debug("parseRechargeCreditResponse"," Exit  map:"+map);
			System.out.println(map);
		}
		return map;
	}



	/**
	 * This method will return MML request message for Login action.
	 * @param	HashMap	p_map
	 * @return	String
	 * @throws	Exception
	 */
	private String generateLoginRequest(String fileCacheId,HashMap<String,String> p_map) throws BTSLBaseException,Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("generateLoginRequest ","Entered p_map: "+p_map);
		String startFlag=null;
		String versionNumber=null;
		String term=null;
		String serviceName=null;
		String sessionID=null;
		String dlgLgn=null;
		String rsv=null; 
		String transactionID=null; // Id under the Transaction Header
		String dlgCtrl=null;
		String tsrv=null;

		String interfaceID= null;
		String loginCommand=null;
		String userName=null;
		String password=null;
		String requestStr=null;
		try
		{

			startFlag=FileCache.getValue(fileCacheId,"START_FLAG");
			if(BTSLUtil.isNullString(startFlag))
			{
				_logger.error("setInterfaceParameters ","Value of START_FLAG is not defined in the INFile");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("START_FLAG",startFlag.trim());

			versionNumber=FileCache.getValue(fileCacheId,"VERSION_NUMBER");

			if(BTSLUtil.isNullString(versionNumber))
			{
				_logger.error("setInterfaceParameters"," Value of VERSION_NUMBER is not defined in the INFile");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("VERSION_NUMBER",versionNumber.trim());

			//get TERM from IN File Cache and put it it request map
			term=FileCache.getValue(fileCacheId,"TERMINALID");

			if(BTSLUtil.isNullString(term))
			{
				_logger.error("setInterfaceParameters ","Value of TERM is not defined in the INFile");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("TERM",term.trim());

			serviceName=FileCache.getValue(fileCacheId,"SERVICE_NAME");


			if(BTSLUtil.isNullString(serviceName))
			{
				_logger.error("setInterfaceParameters ","Value of ACCOUNT_INFO_SERIVICE is not defined in the INFile");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("SERVICE_NAME",serviceName.trim());

			sessionID=(String)p_map.get("SESSIONID");
			//get RSV from IN File Cache and put it it request map

			rsv=FileCache.getValue(fileCacheId,"RSV");

			if(BTSLUtil.isNullString(rsv))
			{
				_logger.error("setInterfaceParameters ","Value of RSV is not defined in the INFile");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("RSV",rsv.trim());


			transactionID=p_map.get("TRANSACTIONHEADERID");

			if(BTSLUtil.isNullString(transactionID))
			{
				_logger.error("setInterfaceParameters"," Value of ACCOUNT_INFO_SERIVICE is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "ACCOUNT_INFO_SERIVICE is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("TRANSACTIONHEADERID",transactionID.trim());


			dlgCtrl=FileCache.getValue(fileCacheId,"DLGCTRL");

			//get DLGCTRL from IN File Cache and put it it request map
			if(BTSLUtil.isNullString(dlgCtrl))
			{
				_logger.error("setInterfaceParameters"," Value of DLGCTRL is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "DLGCTRL is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("DLGCTRL",dlgCtrl.trim());


			//get TSRV from IN File Cache and put it it request map
			tsrv=FileCache.getValue(fileCacheId,"TSRV");
			if(BTSLUtil.isNullString(tsrv))
			{
				_logger.error("setInterfaceParameters ","Value of TSRV is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "TSRV is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("TSRV",tsrv.trim());


			//get START_FLAG from IN File Cache and put it it request map

			//get DLGCON from IN File Cache and put it it request map
			dlgLgn=FileCache.getValue(fileCacheId,"DLGLGN");

			if(BTSLUtil.isNullString(dlgLgn))
			{
				_logger.error("setInterfaceParameters"," Value of DLGLGN is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "DLGCON is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("DLGLGN",dlgLgn.trim());
			interfaceID=p_map.get("interfaceID");

			userName=FileCache.getValue(fileCacheId,"USER_NAME_"+interfaceID);

			if(BTSLUtil.isNullString(userName))
			{
				_logger.error("generateLoginRequest "," USER_NAME is not defined in the INFile");
				throw new BTSLBaseException("generateLoginRequest"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			userName=userName.trim();
			p_map.put("USERNAME",userName);

			password=FileCache.getValue(fileCacheId,"PASSWORD_"+interfaceID);

			if(BTSLUtil.isNullString(password))
			{
				_logger.error("generateLoginRequest"," PASSWORD is not defined in the INFile");
				throw new BTSLBaseException("generateLoginRequest"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			password=password.trim();
			p_map.put("PASSWORD",password);

			loginCommand=FileCache.getValue(fileCacheId,"LOGIN_COMMAND");

			if(BTSLUtil.isNullString(loginCommand))
			{
				_logger.error("generateLoginRequest "," LOGIN_COMMAND is not defined in the INFile");
				throw new BTSLBaseException("generateLoginRequest"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			loginCommand=loginCommand.trim();
			p_map.put("LOGIN_COMMAND",loginCommand);

			startFlag=(String)p_map.get("START_FLAG");	  
			//Message Header
			versionNumber=(String)p_map.get("VERSION_NUMBER");
			term=(String)p_map.get("TERM");
			serviceName=(String)p_map.get("SERVICE_NAME");	

			//Session Header
			sessionID=(String)p_map.get("SESSIONID");
			dlgLgn=(String)p_map.get("DLGLGN");	
			rsv=(String)p_map.get("RSV");	

			//Transaction Header
			transactionID=(String)p_map.get("TRANSACTIONHEADERID");
			dlgCtrl=(String)p_map.get("DLGCTRL");	        
			tsrv=(String)p_map.get("TSRV");


			loginCommand=(String)p_map.get("LOGIN_COMMAND");
			userName=(String)p_map.get("USERNAME");
			password=(String)p_map.get("PASSWORD");       

			//Preparing the header information
			StringBuffer headerInfoBuffer=new StringBuffer(versionNumber);//Version-number

			headerInfoBuffer.append(spacePad(term,8," ",'l'));//TERMINAL		    
			headerInfoBuffer.append(spacePad(serviceName,8," ",'r'));//Account Info service		
			headerInfoBuffer.append(spacePad(sessionID,8,"0",'l'));//session id		    
			headerInfoBuffer.append(dlgLgn);//represents session continued
			headerInfoBuffer.append(spacePad(rsv,4,"0",'l'));//reserve
			headerInfoBuffer.append(spacePad(transactionID,8,"0",'l'));//txn id in header of request
			headerInfoBuffer.append(spacePad(dlgCtrl,6," ",'r'));//represents txn begin,continued
			headerInfoBuffer.append(spacePad(tsrv,4,"0",'l'));//txn reserve


			String headerInfoStr=headerInfoBuffer.toString();

			//LOGIN_COMMAND=LOGIN:PSWD=%p,USER=%u (From the INFile)
			loginCommand=loginCommand.replaceAll("%p",password);
			loginCommand=loginCommand.replaceAll("%u",userName);
			if(_logger.isDebugEnabled()) _logger.debug("generateLoginRequest ","login command after putting password and user name loginCommand:"+loginCommand);

			loginCommand=lenpad(loginCommand);
			String headerAndCommand=headerInfoStr+loginCommand;
			String chckSum=getCheckSum(headerAndCommand);
			//requestStr="\'"+startFlag.trim()+"\'"+len(headerAndCommand,4)+headerInfoStr+loginCommand+chckSum;
			requestStr=startFlag+len(headerAndCommand,4)+headerInfoStr+loginCommand+chckSum;   
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			_logger.error("generateLoginRequest ","Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("generateLoginRequest"," Exiting  requestStr:"+requestStr);
		}
		return requestStr;
	}

	/**
	 * This method will return hashMap(containing login response details ) after parsing response string.
	 * @param	HashMap	p_responseStr
	 * @return	String
	 * @throws	Exception
	 */
	private HashMap<String,String> parseLoginResponse(String p_responseStr) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("parseLoginResponse"," Entered p_responseStr: "+p_responseStr);
		HashMap<String,String> map=null;
		int index=0;
		String status=null;
		String sessionId=null;
		try
		{
			map=new HashMap<String,String>();
			index=p_responseStr.indexOf("RETN=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			status=p_responseStr.substring(index+5,p_responseStr.indexOf(",",index));

			map.put("response_status",status.trim());

			sessionId=getResponseSessionId(p_responseStr);//get sessionId 

			map.put("IN_SESSIONHEADERID",sessionId);//put sessionId in map 

			//put the status into map
			if(_logger.isDebugEnabled()) _logger.debug("parseLoginResponse ","status:"+status);
		}
		catch(Exception e)
		{
			_logger.error("parseLoginResponse ","Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled())_logger.debug("parseLoginResponse"," Exiting map:"+map);
		}
		return map;
	}
	/**
	 * This method will return MML request message for Logout action.
	 * @param	HashMap	p_map
	 * @return	String
	 * @throws	Exception
	 */
	private String generateLogoutRequest(String fileCacheId,HashMap<String,String> p_map) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("generateLogoutRequest"," Entered p_map: "+p_map);

		String startFlag=null;
		String versionNumber=null;
		String term=null;
		String serviceName=null;
		String sessionID=null;
		String dlglgn=null;
		String rsv=null; 
		String transactionID=null; // Id under the Transaction Header
		String dlgCtrl=null;
		String tsrv=null;
		String interfaceID= null;
		String logoutCommand=null;
		String userName=null;

		String requestStr=null;
		try
		{

			startFlag=FileCache.getValue(fileCacheId,"START_FLAG");
			if(BTSLUtil.isNullString(startFlag))
			{
				_logger.error("setInterfaceParameters ","Value of START_FLAG is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "START_FLAG is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("START_FLAG",startFlag.trim());


			versionNumber=FileCache.getValue(fileCacheId,"VERSION_NUMBER");

			if(BTSLUtil.isNullString(versionNumber))
			{
				_logger.error("setInterfaceParameters ","Value of VERSION_NUMBER is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "VERSION_NUMBER is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("VERSION_NUMBER",versionNumber.trim());

			//get TERM from IN File Cache and put it it request map
			term=FileCache.getValue(fileCacheId,"TERMINALID");

			if(BTSLUtil.isNullString(term))
			{
				_logger.error("setInterfaceParameters ","Value of TERM is not defined in the INFile");
				///EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "TERM is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("TERM",term.trim());

			serviceName=FileCache.getValue(fileCacheId,"SERVICE_NAME");

			if(BTSLUtil.isNullString(serviceName))
			{
				_logger.error("setInterfaceParameters ","Value of ACCOUNT_INFO_SERIVICE is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "ACCOUNT_INFO_SERIVICE is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("SERVICE_NAME",serviceName.trim());

			sessionID=(String)p_map.get("SESSIONID");


			//get RSV from IN File Cache and put it it request map
			rsv=FileCache.getValue(fileCacheId,"RSV");
			if(BTSLUtil.isNullString(rsv))
			{
				_logger.error("setInterfaceParameters ","Value of RSV is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "RSV is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("RSV",rsv.trim());

			//get DLGCTRL from IN File Cache and put it it request map
			dlgCtrl=FileCache.getValue(fileCacheId,"DLGCTRL");
			if(BTSLUtil.isNullString(dlgCtrl))
			{
				_logger.error("setInterfaceParameters ","Value of DLGCTRL is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "DLGCTRL is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("DLGCTRL",dlgCtrl.trim());


			//get TSRV from IN File Cache and put it it request map
			tsrv=FileCache.getValue(fileCacheId,"TSRV");

			if(BTSLUtil.isNullString(tsrv))
			{
				_logger.error("setInterfaceParameters ","Value of TSRV is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "TSRV is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("TSRV",tsrv.trim());

			dlglgn=FileCache.getValue(fileCacheId,"DLGLGN");

			if(BTSLUtil.isNullString(dlglgn))
			{
				_logger.error("setInterfaceParameters ","Value of DLGLGN is not defined in the INFile");
				//EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "HuaweiINHandler[setInterfaceParameters]",_referenceID,"INTERFACE ID"+_interfaceID+" MSISDN "+_msisdn , (String) _requestMap.get("NETWORK_CODE"), "DLGCON is not defined in the INFile.");
				throw new BTSLBaseException("setInterfaceParameters"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			p_map.put("DLGLGN",dlglgn.trim());
			interfaceID=p_map.get("interfaceID");

			userName=FileCache.getValue(fileCacheId,"USER_NAME_"+interfaceID);

			if(BTSLUtil.isNullString(userName))
			{
				_logger.error("generateLoginRequest ","USER_NAME is not defined in the INFile");
				throw new BTSLBaseException("generateLoginRequest"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			userName=userName.trim();
			p_map.put("USERNAME",userName);

			logoutCommand=FileCache.getValue(fileCacheId,"LOGOUT_COMMAND");

			if(BTSLUtil.isNullString(logoutCommand))
			{
				_logger.error("generateLoginRequest","LOGOUT_COMMAND is not defined in the INFile");
				throw new BTSLBaseException("generateLogoutRequest"+InterfaceErrorCodesI.ERROR_BAD_REQUEST);
			}
			logoutCommand=logoutCommand.trim();
			p_map.put("LOGOUT_COMMAND",logoutCommand);


			startFlag=(String)p_map.get("START_FLAG");	  

			//Message Header
			versionNumber=(String)p_map.get("VERSION_NUMBER");
			term=(String)p_map.get("TERM");
			serviceName=(String)p_map.get("SERVICE_NAME");	

			//Session Header
			sessionID=(String)p_map.get("SESSIONID");
			dlglgn=(String)p_map.get("DLGLGN");	
			rsv=(String)p_map.get("RSV");	

			//Transaction Header
			transactionID=(String)p_map.get("TRANSACTIONHEADERID");
			dlgCtrl=(String)p_map.get("DLGCTRL");	        
			tsrv=(String)p_map.get("TSRV");


			logoutCommand=(String)p_map.get("LOGOUT_COMMAND");
			userName=(String)p_map.get("USERNAME");
			//Preparing the header information
			StringBuffer headerInfoBuffer=new StringBuffer(versionNumber);//Version-number

			headerInfoBuffer.append(spacePad(term,8," ",'l'));//TERMINAL		    
			headerInfoBuffer.append(spacePad(serviceName,8," ",'r'));//Account Info service		
			headerInfoBuffer.append(spacePad(sessionID,8,"0",'l'));//session id		    
			headerInfoBuffer.append(dlglgn);//represents session continued
			headerInfoBuffer.append(spacePad(rsv,4,"0",'l'));//reserve
			headerInfoBuffer.append(spacePad(transactionID,8,"0",'l'));//txn id in header of request
			headerInfoBuffer.append(spacePad(dlgCtrl,6," ",'r'));//represents txn begin,continued
			headerInfoBuffer.append(spacePad(tsrv,4,"0",'l'));//txn reserve


			String headerInfoStr=headerInfoBuffer.toString();

			//LOGIN_COMMAND=LOGIN:PSWD=%p,USER=%u (From the INFile)

			logoutCommand=logoutCommand.replaceAll("%u",userName);
			if(_logger.isDebugEnabled()) _logger.debug("generateLogoutRequest ","logout command after putting password and user name logoutCommand:"+logoutCommand);


			logoutCommand=lenpad(logoutCommand);
			String headerAndCommand=headerInfoStr+logoutCommand;
			String chckSum=getCheckSum(headerAndCommand);
			//requestStr="\'"+startFlag.trim()+"\'"+len(headerAndCommand,4)+headerInfoStr+loginCommand+chckSum;
			requestStr=startFlag+len(headerAndCommand,4)+headerInfoStr+logoutCommand+chckSum;

		}
		catch(Exception e)
		{
			_logger.error("generateLogoutRequest ","Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("generateLogoutRequest ","Exiting  requestStr:"+requestStr);
		}
		return requestStr;
	}
	/**
	 * This method will return hashMap(containing logout response details) after parsing response string.
	 * @param	HashMap	p_responseStr
	 * @return	String
	 * @throws	Exception
	 */
	private HashMap<String,String> parseLogoutResponse(String p_responseStr) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("parseLogoutResponse ","Entered p_responseStr: "+p_responseStr);
		HashMap<String,String> map=null;
		int index=0;
		String response_status=null;
		try
		{
			map=new HashMap<String,String>();
			index=p_responseStr.indexOf("RETN=");
			if(index<0)
				throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
			response_status=(p_responseStr.substring(index+5,p_responseStr.indexOf(",",index))).trim();
			map.put("response_status",response_status.trim());
			if(_logger.isDebugEnabled()) _logger.debug("parseLogoutResponse"," response_status:"+response_status);
		}
		catch(Exception e)
		{
			_logger.error("parseLogoutResponse ","Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled())_logger.debug("parseLogoutResponse"," Exiting map:"+map);
		}
		return map;
	}
	/**
	 * This method will return  MML request message for ImmediateDebit action.
	 * @param	HashMap	p_map
	 * @return	String
	 * @throws	Exception
	 */
	private String generateHeartBeatRequest(HashMap<String,String> p_map) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("generateHeartBeatRequest"," Entered p_map= "+p_map);
		String requestStr=null;
		String requestData=null;
		String startFlag=null;
		try
		{
			requestData=(String)p_map.get("HEART_BEAT_COMMAND");	       
			startFlag=(String)p_map.get("START_FLAG");
			requestData=lenpad(requestData);
			String chckSum=getCheckSum(requestData);
			requestStr=startFlag+len(requestData,4)+requestData+chckSum;
		}
		catch(Exception e)
		{
			_logger.error("generateHeartBeatRequest ","Exception e:" + e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("generateHeartBeatRequest ","Exiting  requestStr:"+requestStr);
		}
		return requestStr;
	}
	/**
	 * Calculates the length of the message and adds the specified character in the left or 
	 * the right side if it smaller than required.
	 * @param	String p_messageStr
	 * @param	int p_padLength
	 * @param	String p_padStr
	 * @param	char p_direction
	 * @return	String
	 */
	private String spacePad (String p_messageStr, int p_padLength, String p_padStr, char p_direction) throws Exception
	{
		StringBuffer padStrBuffer = null;
		try
		{
			if (p_messageStr.length() < p_padLength)
			{
				int paddingLength=p_padLength-p_messageStr.length();
				padStrBuffer = new StringBuffer(10);					
				if(p_direction=='r')
					padStrBuffer.append(p_messageStr);				    
				for (int i=0; i < paddingLength; i++)					
					padStrBuffer.append(p_padStr);
				if(p_direction=='l')
					padStrBuffer.append(p_messageStr);					
				p_messageStr = padStrBuffer.toString();			
			}
		}
		catch(Exception e)
		{
			_logger.error("spacePad ","Exception e:"+e.getMessage());
			throw e;
		}
		return p_messageStr;
	}
	/**
	 * calculates the length of the string, if it is not divisible by 4, it increases the length
	 * and also adds spaces in the increased fields.
	 * @param	String p_messageStr
	 * @return	String
	 */
	private String lenpad(String p_messageStr)  throws Exception
	{
		int paddingLength =0;
		StringBuffer messageStrBuffer = null;
		try
		{
			int messageStrLength=p_messageStr.length();
			if(messageStrLength%4!=0)
				while(messageStrLength%4!=0)
					messageStrLength++;
			if (p_messageStr.length() < messageStrLength)
			{
				messageStrBuffer = new StringBuffer(1024);
				messageStrBuffer.append(p_messageStr);
				paddingLength = messageStrLength-p_messageStr.length();
				for (int j=0; j < paddingLength; j++)
					messageStrBuffer.append(" ");
				p_messageStr=messageStrBuffer.toString();					
			}
		}		
		catch(Exception e)
		{
			_logger.error("lenpad ","Exception e:"+e.getMessage());
			throw e;
		}
		return p_messageStr;		
	}
	/**
	 * This method calculates the length of a string.
	 * Unless the length is not divisible by 4,it increases the length value and returns it in the hexa-decimal format.
	 * It adds zeros in front until the length becomes 4.
	 * @param	String p_messageStr
	 * @param	int p_pad
	 * @return String 
	 */
	private String len(String p_messageStr,int p_pad) throws Exception
	{
		String messageStrLength = null;
		StringBuffer msgLengthBuffer = null;
		try
		{
			int i=p_messageStr.length();//length of string
			if(i%4!=0)
				while(i%4!=0)
					i++;
			messageStrLength= Integer.toHexString(i);
			if (messageStrLength.length()<p_pad)
			{
				msgLengthBuffer = new StringBuffer(10);
				int paddingLength = p_pad-messageStrLength.length();
				for (int j=0; j < paddingLength; j++)
					msgLengthBuffer.append("0");
				msgLengthBuffer.append(messageStrLength);
				messageStrLength = msgLengthBuffer.toString();
			}

		}
		catch(Exception e)
		{
			_logger.error("len ","Exception e:"+e.getMessage());
			throw e;
		}
		return messageStrLength;
	}
	/**
	 * This method calculates checksum of request MML message prepared.
	 * @param p_headerAndDataString
	 * @return String 
	 * @throws Exception
	 */
	private String getCheckSum(String p_headerAndDataString) throws Exception
	{
		if(_logger.isDebugEnabled())_logger.debug("getCheckSum ","Entered p_headerAndDataString:"+p_headerAndDataString);
		int []c	=null;
		int index=0;
		StringBuffer hexBuffer=null;
		try
		{
			c=new int[4];
			index=p_headerAndDataString.indexOf("1.01");
			//index is set to 0,because in Heart-beat message version is not set.
			if(index<0)
				index=0;
			for(int k=0;k<4;k++)
				c[k]=0;
			for(int j=0,stringLength=p_headerAndDataString.length();j<stringLength-index;j=j+4)
			{
				c[0]=c[0]^((int)p_headerAndDataString.charAt(index+j));
				c[1]=c[1]^((int)p_headerAndDataString.charAt(index+j+1));
				c[2]=c[2]^((int)p_headerAndDataString.charAt(index+j+2));
				c[3]=c[3]^((int)p_headerAndDataString.charAt(index+j+3));
			}
			hexBuffer=new StringBuffer(1028);
			for(int k=0;k<4;k=k+1)
				hexBuffer.append(Integer.toHexString(~c[k]&(0x0ff)));
			hexBuffer.toString().toUpperCase();
		}
		catch(Exception e)
		{
			_logger.error("getCheckSum ","Exception e:"+e.getMessage());
			throw e;
		}
		finally
		{
			if(_logger.isDebugEnabled())_logger.debug("getCheckSum ","Exited");
		}
		return hexBuffer.toString().toUpperCase(); 	        
	}
	/**
	 * This method parses transaction id from response string.
	 * @param p_responseStr
	 * @return String 
	 * @throws Exception
	 */
	private String getResponseTransactionId(String p_responseStr) throws Exception
	{
		if(_logger.isDebugEnabled()) _logger.debug("getResponseTransactionId ","Entered p_responseStr:"+p_responseStr);
		int index=0;
		String transIdStr=null;
		try
		{
			index=p_responseStr.indexOf("DLGCON");
			transIdStr=p_responseStr.substring(index+10,p_responseStr.indexOf("TXEND"));
		} 
		catch (Exception e)
		{
			_logger.error("getResponseTransactionId ","Exception e:"+e.getMessage());
			throw e;	
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("getResponseTransactionId ","Exited transIdStr:"+transIdStr);
		}
		return transIdStr.trim(); 
	}


	/**
	 * This method parses Session id from login response string.
	 * @param p_responseStr
	 * @return String 
	 * @throws Exception
	 */
	private String getResponseSessionId(String p_responseStr) throws Exception
	{
		if(_logger.isDebugEnabled()) _logger.debug("getResponseSessionId ","Entered p_responseStr:"+p_responseStr);
		int index=0;
		String sessionIdStr=null;
		try
		{
			index=p_responseStr.indexOf("DLGLGN");

			if(index > -1)
				sessionIdStr=p_responseStr.substring(index-8,index);

		} 
		catch (Exception e)
		{
			_logger.error("getResponseSessionId ","Exception e:"+e.getMessage());
			throw e;	
		}
		finally
		{
			if(_logger.isDebugEnabled()) _logger.debug("getResponseSessionId ","Exited sessionIdStr:"+sessionIdStr);
		}
		return sessionIdStr.trim(); 
	}

	public static void main(String[] args)
	{
		try
		{
			ZTEINRequestFormatter obj = new ZTEINRequestFormatter();
			//String t_responseStr="89C9A2A4`SC`0004HBHBB7BDB7BD`SC`012C1.01internal     ACK English10000001DLGCON0000MMMMM TXEND0000ACK:DISP PPS ACNTINFO: RETN=0, DESC=\"Querying subscriber\'s information succeeded.\", ATTR=MSISDN&SUBCOSID&SERVICESTOP&ACTIVESTOP&BALANCE&LANGUAGETYPE&FRAUDLOCK&SUSPENDSTOP&ACCOUNTSTATE, RESULT=\"1815050020|1|20161214|20070216|32154100|2|0||5\"";
			String t_responseStr="`SC`00D41.00CCB00000PPSPHS  000000F6DLGCONFFFF10274602TXEND FFFFACK:CHARGE CASH ACNT:RETN=0000, BEFOROPER=5576,FEE=100,AFTEROPER=5676,DESC=\"Success\", ATTR=BONUSTYPE|BONUSVALUE|EFFDATE|EXPDATE, RESULT=,CSVSTOP=2014-06-18 A7DDD8EB";
			//String t_responseStradjust="`SC`00E41.00CCB00000PPS     00000000DLGCONFFFF10000001TXEND FFFFACK:RECHARGE PPS:RETN=0000, DESC=\"Success\", LOYALTY=0, SERIALNO=PB0000000032, ACCOUNT=5000, TopupAmount=4333, SOURCE=VTOPUPKA1062217580136, ProcessingFee=0, Servicetax=0F3F6C3F";
			System.out.println(obj.parseRechargeCreditResponse(t_responseStr));
		}//end of try block
		catch(Exception e)
		{
			e.printStackTrace();
		}//end of catch-Exception
		finally
		{

		}//end of finally
	}



}

