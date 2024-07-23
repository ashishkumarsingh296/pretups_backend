package com.client.pretups.gateway.businesslogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.MessageSentLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/* USSDPushMessage.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Vipan Kumar					   04/11/2014	      Initial Creation	
 *------------------------------------------------------------------------
 * Copyright (c) 2014 Mahindra Comviva  Ltd.
 * Class for pushing the message based on the request code and type of the response required
 */

public class USSDPushMessage implements Runnable{

	private static final Log _log = LogFactory.getLog(USSDPushMessage.class.getName());
	private String _msisdn=null;
	private String _requestCode=null;
	private Locale _locale=null;
	private String _message=null;
	private String _messageKey=null;
	private String[] _args=null;
	private String _transactionID=null;
	private String _messageType=null;
	private String _messageClass=null;
	private String _pid=null;
	private String _networkCode=null;
	private boolean _entryDoneInLog=false;
	public String TIMEOUT="TIMEOUT";
	public String FAILED="FAILED";
	public String _messageCode=null;
	private LocaleMasterVO _localeMasterVO=null;

	public static OperatorUtilI _operatorUtil=null;
	private String _tempMessage=null;
	public static ExecutorService executor = null;


	static
	{
		final String methodName = "static";
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try
		{
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDPushMessage[OperatorUtil initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
		try
		{
			executor = Executors.newFixedThreadPool((new Integer(Constants.getProperty("THREADPOOLEXE_POLLSIZE")).intValue()));
		}
		catch(Exception e)
		{
			executor = Executors.newFixedThreadPool(30);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDPushMessage[thread pool initialize]","","","","Exception while initilizing the thread pool :"+e.getMessage());
		}

	}
	/**
	 * USSDPushMessage Constructor (This constructor will be used for pushing SMS message in WEB and SMS Interface)
	 * @param p_msisdn
	 * @param btslMessages
	 * @param p_transactionID
	 * @param p_requestCode
	 * @param p_locale
	 * @param p_networkCode
	 */
	public USSDPushMessage(String p_msisdn,BTSLMessages btslMessages,String p_transactionID,String p_requestCode,Locale p_locale,String p_networkCode)
	{
		if(_log.isDebugEnabled()) _log.debug("USSDPushMessage[USSDPushMessage] at line 92","");
		_msisdn=p_msisdn;
		_requestCode=p_requestCode;
		_locale=p_locale;
		_messageKey=btslMessages.getMessageKey();
		_args=btslMessages.getArgs();
		_transactionID=p_transactionID;
		_networkCode=p_networkCode;
		//ChangeID=LOCALEMASTER
		//populate the localemasterVO from the LocaleMasterCache for the requested locale
		_localeMasterVO=LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
	}//end of USSDPushMessage

	/**
	 * 
	 * @param p_msisdn
	 * @param p_message
	 * @param p_transactionID
	 * @param p_requestCode
	 * @param p_locale
	 */
	public USSDPushMessage(String p_msisdn,String p_message,String p_transactionID,String p_requestCode,Locale p_locale)
	{
		if(_log.isDebugEnabled()) _log.debug("USSDPushMessage[USSDPushMessage] at line 115","");

		_msisdn=p_msisdn;
		_requestCode=p_requestCode;
		_locale=p_locale;
		_message=p_message;
		_transactionID=p_transactionID;
		//ChangeID=LOCALEMASTER
		//populate the localemasterVO from the LocaleMasterCache for the requested locale
		_localeMasterVO=LocaleMasterCache.getLocaleDetailsFromlocale(_locale);
	}//end of USSDPushMessage

	/**
	 * Method that will be called frpm the application to send message. It will spawn a new thread internally
	 */
	public void push() 
	{
		if(_log.isDebugEnabled()) _log.debug("push","Entered with _msisdn="+_msisdn+" _requestCode="+_requestCode+" _transactionID="+_transactionID+"_messageKey="+_messageKey+" _args="+_args+" _locale="+_locale+" _message="+_message);
		//Thread USSDPushMessage = new Thread (this);
		try
		{
			//Starting thread to send message
			_messageType="PLAIN";
			//USSDPushMessage.start();
			if(_log.isDebugEnabled()) _log.debug("push","ThreadPoolExecutor Start");
			executor.execute(this);
			if(_log.isDebugEnabled()) _log.debug("push","ThreadPoolExecutor End");


		}
		catch(Exception ex)
		{
			_log.error("push","Getting Exception ="+ex.getMessage());
			ex.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDPushMessage[push]",_transactionID,_msisdn,"","Exception "+ex.getMessage());
		}
	}//end of Push

	public void run() 
	{
		final String methodName = "run";
		try
		{
			if(_log.isDebugEnabled()) _log.debug("run",_transactionID,"Entered _requestCode with "+_requestCode);
			if(BTSLUtil.isNullString(_requestCode))
			{
				_requestCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY,_networkCode);
				if(_log.isDebugEnabled()) _log.debug("run","Picked default message gateway for network: "+_networkCode+" _requestCode:"+_requestCode);
			}
			if(_log.isDebugEnabled()) _log.debug("run","after message cache not null for network: "+_networkCode+" _requestCode:"+_requestCode);

			if(_messageKey!=null)
			{
				_message =BTSLUtil.getMessage(_locale,_messageKey,_args);
			}
			if(_log.isDebugEnabled()) _log.debug("run after checking message key equal to null",_transactionID,"_messageKey: "+_messageKey+"  _messageClass: "+_messageClass+" _pid: "+_pid+ " _locale: "+_locale+" _locale language: "+(_locale==null?"":_locale.getLanguage()));
			if(_message.indexOf("mclass^")==0)
			{
				int colonIndex=_message.indexOf(":");
				String messageClassPID=_message.substring(0,colonIndex);
				String[] messageClassPIDArray=messageClassPID.split("&");
				_messageClass=messageClassPIDArray[0].split("\\^")[1];
				_pid=messageClassPIDArray[1].split("\\^")[1];
				_message=_message.substring(colonIndex+1);
				int endIndexForMessageCode;
				//The block below is used to find the message code from the message.
				//In case of arabic colon will be encoded so we find the end index as 00%3A which
				//is encoded value of colon.
				//ChangeID=LOCALEMASTER
				//check the language from the localeMasterVO
				if(("ar".equals(_localeMasterVO.getLanguage()))||("ru".equals(_localeMasterVO.getLanguage())))
				{
					endIndexForMessageCode=_message.indexOf("%00%3A");
					if(endIndexForMessageCode!=-1)
						_messageCode=URLDecoder.decode(_message.substring(0,endIndexForMessageCode),"UTF16");
				}
				else
				{
					endIndexForMessageCode=_message.indexOf(":");
					if(endIndexForMessageCode!=-1)
						_messageCode=_message.substring(0,endIndexForMessageCode);
				}
			}
			//ChangeID=LOCALEMASTER
			//Message will be encoded by the encoding scheme defined in the locale master tabel for the requested locale.
			if((("ar".equals(_locale.getLanguage()))||("ru".equals(_locale.getLanguage()))) && !_message.startsWith("%"))
			{
				if(_log.isDebugEnabled()) _log.debug("run1",_transactionID,"_message: "+_message+" _messageKey: "+_messageKey+"  _messageClass: "+_messageClass+" _pid: "+_pid);

				_message=BTSLUtil.encodeSpecial(_message,true,_localeMasterVO.getEncoding());
			}
			else  if(!("ar".equals(_locale.getLanguage())||"ru".equals(_locale.getLanguage())))
			{
				if(_log.isDebugEnabled()) _log.debug("run2",_transactionID,"_message: "+_message+" _messageKey: "+_messageKey+"  _messageClass: "+_messageClass+" _pid: "+_pid);

				_message=URLEncoder.encode(_message,_localeMasterVO.getEncoding());
			}

			//encoding special for arabic
			//if(!(_locale.getLanguage().equals("en")||_locale.getLanguage().equals("fr")))
			//_message =BTSLUtil.encodeSpecial(_message);
			if(_log.isDebugEnabled()) _log.debug("run",_transactionID,"_message: "+_message+" _messageKey: "+_messageKey+"  _messageClass: "+_messageClass+" _pid: "+_pid);
			sendSMSMessage(false);
			_entryDoneInLog=true;

		}
		catch(BTSLBaseException be)
		{
			_log.error("USSDPushMessage[run]","Base Exception while sending message="+be.getMessage());
			if(!_entryDoneInLog)
				MessageSentLog.log(_msisdn,_locale,"","",_message,PretupsI.GATEWAY_MESSAGE_FAILED,"","Not able to send Message , getting Exception="+be.getMessage()+" Message code="+_messageCode);
			be.printStackTrace();
		}
		catch(Exception e)
		{
			_log.error("USSDPushMessage[run]","Exception while sending message="+e.getMessage());
			if(!_entryDoneInLog)
				MessageSentLog.log(_msisdn,_locale,"","",_message,PretupsI.GATEWAY_MESSAGE_FAILED,"","Not able to send Message , getting Exception="+e.getMessage()+" Message code="+_messageCode);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDPushMessage[run]","",_msisdn,"","Message Sending Exception:"+e.getMessage());
		}
		finally
		{

		}
	}//end of run

	/**
	 * Method that will create the url string to be pushed and push the same using URL Connection
	 * @param p_msisdn
	 * @param p_message
	 * @param p_transactionID
	 * @param p_locale
	 * @param p_messageGatewayMappingCacheVO
	 * @param p_useAlternate : Whether to use alternate gateway or not
	 * @throws Exception
	 */
	private String sendSMSMessage(boolean p_useAlternate) throws Exception
	{
		final String methodName = "sendSMSMessage";
		if(_log.isDebugEnabled()) _log.debug("sendSMSMessage","Entered with _msisdn="+_msisdn+" p_message="+_message+" p_transactionID="+_transactionID+" _messageType="+_messageType);
		StringBuffer urlBuff=null;
		String status=PretupsI.GATEWAY_MESSAGE_FAILED;
		try
		{
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_PROTOCOL")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD Protocol Not Defined");
			}
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_PATH")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD Path Not Defined");
			}
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_IP")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD IP Not Defined");
			}
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_PORT")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD Port Not Defined");
			}
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_NODEID")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD NODE Id Not Defined");
			}
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_SERVICEID")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD Service ID Not Defined");
			}

			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_TIMEOUT")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD timeout Not Defined");
			}
			try{
				Integer.parseInt(Constants.getProperty("USSDPUSH_TIMEOUT"));
			}catch (Exception e) {
				throw new BTSLBaseException(this,"pushBinary","USSD timeout must be numeric");
			}
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_SERVICECODE")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD ServiceCode Not Defined");
			}
			if(BTSLUtil.isNullString(Constants.getProperty("USSDPUSH_PUSHTYPE")))
			{
				throw new BTSLBaseException(this,"pushBinary","USSD Push Type Not Defined");
			}
			String protocol=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_PROTOCOL"));
			String path=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_PATH"));
			String ip=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_IP"));
			String port=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_PORT"));
			String ndoeId=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_NODEID"));
			String serviceId=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_SERVICEID"));
			int timeOut=Integer.parseInt(BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_TIMEOUT")));
			String serviceCode=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_SERVICECODE"));
			String pushType=BTSLUtil.NullToString(Constants.getProperty("USSDPUSH_PUSHTYPE"));

			urlBuff=getPlainMessageURL(ndoeId,serviceId,path,protocol+"://"+ip+":"+port,_msisdn,_locale,_message,_messageClass,_pid,serviceCode,pushType);
			status=pushGatewayMessage(urlBuff.toString(),timeOut);
			//Log in Message Sent Log
			if(!BTSLUtil.isNullString(_tempMessage))
				MessageSentLog.log(_msisdn,_locale,"USSD PUSH","USSD","",status,_tempMessage,"Message Class="+_messageClass+" PID="+_pid+" Message code="+_messageCode);	
			else
				MessageSentLog.log(_msisdn,_locale,"USSD PUSH","USSD","",status,urlBuff.toString(),"Message Class="+_messageClass+" PID="+_pid+" Message code="+_messageCode);
		}
		catch(BTSLBaseException be)
		{
			_log.debug("sendSMSMessage","_transactionID="+_transactionID+ "Exception ="+be.getMessage());
			//Use the alternate 
			//Log in Message Sent Log
			MessageSentLog.log(_msisdn,_locale,"USSD PUSH","USSD",_message,status,"","Not able to send message "+" Message code="+_messageCode);
			_entryDoneInLog=true;
			throw be;
		}
		catch(Exception e)
		{
			_log.debug("sendSMSMessage","_transactionID="+_transactionID+ " Exception ="+e.getMessage());
			_log.errorTrace(methodName, e);
			//Log in Message Sent Log
			MessageSentLog.log(_msisdn,_locale,"USSD PUSH","USSD",_message,status,"","Not able to send message "+" Message code="+_messageCode);
			_entryDoneInLog=true;
			throw e;
		}
		return status;	

	}// sendSMSMessage


	/**
	 * Method that will connect to the IP and port and will send the message
	 * @param p_messageString
	 * @param p_timeout
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	private String pushGatewayMessage(String p_messageString,int p_timeout) throws BTSLBaseException,Exception
	{	
		final String methodName = "pushGatewayMessage";
		if(_log.isDebugEnabled()) _log.debug("pushGatewayMessage","Entered with _transaction ID="+_transactionID+ " p_messageString="+p_messageString);
		String status=null;
		try
		{
			String msgResponse=getResponse(p_messageString,p_timeout);
			status=PretupsI.GATEWAY_MESSAGE_SUCCESS;
			if(_log.isDebugEnabled()) _log.debug("pushGatewayMessage","_transaction ID="+_transactionID+ "msgResponse from connector="+msgResponse);
		}
		catch(BTSLBaseException bex)
		{
			_log.error("pushGatewayMessage","_transaction ID="+_transactionID+ "Exception ="+bex.getMessage());
			bex.printStackTrace();
			status=PretupsI.GATEWAY_MESSAGE_FAILED;
			throw bex;
		}
		catch(Exception e)
		{
			_log.error("pushGatewayMessage","_transaction ID="+_transactionID+ "Exception ="+e.getMessage());
			_log.errorTrace(methodName, e);
			status=PretupsI.GATEWAY_MESSAGE_FAILED;
			throw e;
		}
		return status;
	}//end pushGatewayMessage

	/**
	 * Method to construct the plain SMS String
	 * @param p_responseGatewayVO
	 * @param p_ipaddress
	 * @param p_msisdn
	 * @param p_locale
	 * @param p_message
	 * @return StringBuffer
	 * @throws BTSLBaseException
	 */
	private StringBuffer getPlainMessageURL(String loginID,String password,String path,String p_ipaddress,String p_msisdn,Locale p_locale,String p_message,String p_messageClass,String p_pid,String serviceCode,String pushType) throws BTSLBaseException
	{
		final String methodName = "getPlainMessageURL";
		if(_log.isDebugEnabled()) _log.debug("getPlainMessageURL","Entered with p_ipaddress="+p_ipaddress+ " p_msisdn="+p_msisdn+"p_message="+p_message+"p_locale="+p_locale);
		StringBuffer urlBuff=null;
	
		try
		{
			urlBuff=new StringBuffer(p_ipaddress+"/"+path);
			urlBuff.append("NODE_ID="+loginID);
			urlBuff.append("&SERVICE_ID="+password);

			if(_operatorUtil==null)
			{
				String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

				try
				{
					_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
				}
				catch(Exception e)
				{
					_log.errorTrace(methodName, e);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"USSDPushMessage[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
				}
			}
			urlBuff.append("&PUSH_TYPE="+pushType);
			urlBuff.append("&SERVICE_CODE="+serviceCode);
			urlBuff.append("&MSISDN="+_operatorUtil.getOperatorFilteredMSISDN(p_msisdn));
			urlBuff.append("&PUSH_TEXT="+p_message);

		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			_log.error("USSDPushMessage[getPlainMessageURL]","_transaction ID="+_transactionID+ " Not able to get the message String Exception ="+e.getMessage());
			throw new BTSLBaseException(this,"getPlainMessageURL","Not able to get the message String="+p_msisdn+" Creating Plain URL String Exception:"+e.getMessage());
		}
		if(_log.isDebugEnabled()) _log.debug("getPlainMessageURL","Exiting for p_msisdn="+p_msisdn+" urlBuff="+urlBuff);
		return urlBuff;
	}//end getPlainMessageURL


	private String getResponse(String p_url,int p_timeout) throws BTSLBaseException
	{
		final String methodName = "getResponse";
		if(_log.isDebugEnabled())_log.debug("getResponse","Entered p_url:"+p_url+" p_timeout:"+p_timeout);
		URL url = null;
		HttpURLConnection urlConnection = null;
		BufferedReader in=null;
		StringBuffer strBuff = null;
		try
		{
			url = new URL(p_url);
			urlConnection =(HttpURLConnection)url.openConnection();
			try
			{
				urlConnection.setConnectTimeout(Integer.parseInt(Constants.getProperty("CONNECT_TIMEOUT")));
				urlConnection.setReadTimeout(Integer.parseInt(Constants.getProperty("READ_TIMEOUT")));
			}
			catch(Exception e)
			{
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(10000);
			}
			String line=null;
			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			long startTimeinMills=System.currentTimeMillis();
			strBuff = new StringBuffer();
			while(true)
			{
				if(_log.isDebugEnabled())_log.debug("getResponse","Inside While Loop Entered");
				if(System.currentTimeMillis() - startTimeinMills >(p_timeout*1000))
				{
					throw new BTSLBaseException(TIMEOUT);
				}
				line=in.readLine();
				if(line !=null)
				{
					strBuff.append(line);
					if(_log.isDebugEnabled())_log.debug("getResponse","line:"+line);
					break;
				}
				else
				{
					if(_log.isDebugEnabled())_log.debug("getResponse","Blank Ack Response");
					break;
				}
			}//end of while
			//MessageSentLog.logMessage(strBuff.toString());
			if(_log.isDebugEnabled())_log.debug("getResponse","Exiting While Loop");
		}
		catch (BTSLBaseException be)
		{
			throw be;
		}
		catch (Exception e)
		{
			_log.error("getResponse","Exeption e:"+e.getMessage());
			_log.errorTrace(methodName, e);
			//MessageSentLog.logMessage(msisdn+"   "+msgStr+"  "+ce.getMessage());
			throw new BTSLBaseException(FAILED);
		}
		finally
		{
			try{if(in !=null) in.close();}catch(Exception ex){}
			try{if(urlConnection !=null) urlConnection.disconnect();}catch(Exception ex){}
			urlConnection =null;
			url=null;
			if(_log.isDebugEnabled())_log.debug("getResponse","Exiting response str:"+(strBuff==null?"":strBuff.toString()));
		}//end of finally
		return strBuff.toString();
	}

}
