package com.inter.zteosndata;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.message.SOAPHeaderElement;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
public class ZTEOSNTestClient
{
	private static Log _log = LogFactory.getLog(ZTEOSNTestClient.class.getName());
	private String _endPoint = null;
	private Call _call = null;
	private String _action=null;
	private String _userName=null;
	private String _password=null;
	private String _propertiesFilePath=null;
	private String _acntInfoQueryStr=null;
	private String _creditStr=null;

	public static void main(String[] args) 
	{
		ZTEOSNTestClient zteobj = new ZTEOSNTestClient();
		try
		{
			zteobj._propertiesFilePath=args[0].trim();
			zteobj._action=args[1];
			zteobj.loadInputs();
			zteobj.setUp();
			zteobj.testRequest();
		}
		catch(Exception ex)
		{
			_log.errorTrace("ZTEOSNTestClient",ex);
		}
	}
	
	private static String getcurrentTime() 
    {
		String dateStrReqTime = null;
          try{
                java.util.Date mydate = new java.util.Date();
                SimpleDateFormat sdfReqTime = new SimpleDateFormat ("yyyyMMddHHmmss");
                dateStrReqTime = sdfReqTime.format(mydate);
          }
       catch(Exception e){
             _log.errorTrace("getcurrentTime",e);
       }
       return dateStrReqTime;
    }

	
	private static String getRequestID() 
    {
       String reqId="";
       String counter="";
       String dateStrReqId = null;
       try{
            java.util.Date mydate = new java.util.Date();
            SimpleDateFormat sdfReqId = new SimpleDateFormat ("yyyyMMddHHmmss");
            dateStrReqId = sdfReqId.format(mydate);
            reqId = "003"+dateStrReqId+counter;
       }
       catch(Exception e){
          _log.errorTrace("getRequestID",e);
       }
       return reqId;
    }
       
	
	public void loadInputs()
	{
		try{
			Properties properties = new Properties();
	        File file= new File(_propertiesFilePath);//Absolute path
	        properties.load(new FileInputStream(file));
	        _endPoint = properties.getProperty("END_POINT");
	        _userName = properties.getProperty("USER_NAME");
	        _password = properties.getProperty("PASSWORD");
	        _acntInfoQueryStr = properties.getProperty("ACCOUNT_INFO");
	        _creditStr = properties.getProperty("CREDIT");
		}
		catch(Exception e){
			_log.errorTrace("loadInputs",e);
		}
	}
	
	protected void setUp() throws Exception
	{
		Service service = new Service();
		_call = (Call) service.createCall();
		_call.setTargetEndpointAddress(_endPoint);
		//set soap action
		_call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://ocs.ztesoft.com/WebServices");
		//set operation 	
		OperationDesc oper = new OperationDesc();
		oper.setName("doService");		
		//set input parameter description
		oper.addParameter(new ParameterDesc(new javax.xml.namespace.QName("http://ocs.ztesoft.com", "reqXml"),
				org.apache.axis.description.ParameterDesc.IN,new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "string"),
				java.lang.String.class, false, false));
		
		_call.setOperation(oper);
		_call.setOperationName(new javax.xml.namespace.QName("http://ocs.ztesoft.com", "doService"));
		_call.setEncodingStyle(null);

		//set soap-header
		SOAPHeaderElement soapHeader = new SOAPHeaderElement(new QName("AuthHeader"));
		soapHeader.setActor(null);
		soapHeader.addChildElement("Username").addTextNode(_userName);
		soapHeader.addChildElement("Password").addTextNode(_password);
		_call.addHeader(soapHeader);
	}

	
	public void testRequest()
	{
		try
		{			
			String reqXml=null;
			if(_action.equals("1"))
				reqXml=generateGetAccountInfoRequest();
			else if(_action.equals("2"))
				reqXml=generateRechargeCreditRequest();

			long startTime=System.currentTimeMillis();

			//Call the web-servcie interface.The result is in XML format.
			String response =  _call.invoke(new Object[] { reqXml}).toString();
			
			long endTime=System.currentTimeMillis();
			double INtime = (endTime - startTime)/1000;
			
			System.out.println("\nTime taken at IN for processing in seconds: "+String.valueOf(INtime));
			
			response = response.replaceAll(">[ \n]+<", "><");
		} 
		catch (Exception e) {
			_log.errorTrace("testRequest",e);
		}
	}

	private String generateGetAccountInfoRequest() throws Exception
    {
		String requestStr= null;
		StringBuffer sbf=null;
		try
		{
		    sbf=new StringBuffer(1028);
		 	sbf.append("<zsmart>");
		 	sbf.append("<Data>");
		 	sbf.append("<header>");
		 	sbf.append("<requestTime>"+getcurrentTime()+"</requestTime>");
		 	sbf.append("<ACTION_ID>QueryBasicProfile</ACTION_ID>");
    	 	sbf.append("<REQUEST_ID>"+getRequestID()+"</REQUEST_ID>");
    	 	sbf.append("</header>");
    	 	sbf.append("<body>");
    	 	sbf.append(_acntInfoQueryStr);
    	 	sbf.append("<TransactionSN>"+getcurrentTime()+"</TransactionSN>");
    	 	sbf.append("<UserPwd></UserPwd>");
    	 	sbf.append("</body>");
    	 	sbf.append("</Data>");
    	 	sbf.append("</zsmart>");
    	 	requestStr=sbf.toString();
		}
		catch(Exception e)
		{
			_log.errorTrace("generateGetAccountInfoRequest",e);
		}
		return requestStr;
    }
	
	
	private String generateRechargeCreditRequest() throws Exception
    {
		String requestStr= null;
		StringBuffer sbf=null;
		try
		{
			sbf=new StringBuffer(1024);
		 	sbf.append("<zsmart>");
		 	sbf.append("<Data>");
		 	sbf.append("<header>");
		 	sbf.append("<ACTION_ID>ModifyBalReturnAllBal</ACTION_ID>");
    	 	sbf.append("<REQUEST_ID>"+getRequestID()+"</REQUEST_ID>");
    	 	sbf.append("</header>");
    	 	sbf.append("<body>");
    	 	sbf.append(_creditStr);
    	 	sbf.append("<TransactionSN>"+getcurrentTime()+"</TransactionSN>");
    	 	sbf.append("</body>");
    	 	sbf.append("</Data>");
    	 	sbf.append("</zsmart>");
    		requestStr=sbf.toString();
		}
		catch(Exception e)
		{
			_log.errorTrace("generateRechargeCreditRequest",e);
		}
		return requestStr;
    }
	
	
	public HashMap<String,String> parseGetAccountInfoResponse(String responseStr)
	{
		HashMap<String,String> responseMap=null;
		try
		{  
		    responseMap=new HashMap<String,String>();
		    String _str="0";
		    int index=responseStr.indexOf("<returnCode>");
		    String returnCode=responseStr.substring(index+"<returnCode>".length(),responseStr.indexOf("</returnCode>",index));
		    responseMap.put("resp_returnCode",returnCode);
		    if(_str.equals(returnCode))
	        {
		    	index=responseStr.indexOf("<ACTION_ID>");
		    	if(index!=-1)
		    	{
		    		String actionId=responseStr.substring(index+"<ACTION_ID>".length(),responseStr.indexOf("</ACTION_ID>",index));
		    		responseMap.put("resp_action_id",actionId);
		    	}
			    index=responseStr.indexOf("<REQUEST_ID>");
			    if(index!=-1)
		    	{
			    	String reqId=responseStr.substring(index+"<REQUEST_ID>".length(),responseStr.indexOf("</REQUEST_ID>",index));
			    	responseMap.put("resp_req_id",reqId);
		    	}
			    index=responseStr.indexOf("<MSISDN>");
			    if(index!=-1)
		    	{
			    	String msisdn=responseStr.substring(index+"<MSISDN>".length(),responseStr.indexOf("</MSISDN>",index));
			    	responseMap.put("resp_msisdn",msisdn);
		    	}
			    index=responseStr.indexOf("<DefLang>");
			    if(index!=-1)
		    	{	
			    	String defLang=responseStr.substring(index+"<DefLang>".length(),responseStr.indexOf("</DefLang>",index));
			    	responseMap.put("resp_defLang",defLang);
		    	}			    
	           	index=responseStr.indexOf("<State>");
	           	if(index!=-1)
		    	{
	           		String resp_state=responseStr.substring(index+"<State>".length(),responseStr.indexOf("</State>",index));
	           		responseMap.put("resp_state",resp_state);
		        }	           	
	           	index=responseStr.indexOf("<StateSet>");
	           	if(index!=-1)
		    	{
	           		String resp_stateSet=responseStr.substring(index+"<StateSet>".length(),responseStr.indexOf("</StateSet>",index));
	           		responseMap.put("resp_stateSet",resp_stateSet);
		    	}
	           	index=responseStr.indexOf("<ActiveStopDate>");
	           	if(index!=-1)
		    	{
	           		String resp_activeStopDate=responseStr.substring(index+"<ActiveStopDate>".length(),responseStr.indexOf("</ActiveStopDate>",index));
	           		responseMap.put("resp_activeStopDate",resp_activeStopDate);
		    	}
	          	index=responseStr.indexOf("<SuspendStopDate>");
	          	if(index!=-1)
		    	{
	          		String resp_suspendStopDate=responseStr.substring(index+"<SuspendStopDate>".length(),responseStr.indexOf("</SuspendStopDate>",index));
	          		responseMap.put("resp_suspendStopDate",resp_suspendStopDate);
		    	}
		        index=responseStr.indexOf("<DisableStopDate>");
		        if(index!=-1)
		    	{
		        	String resp_disableStopDate=responseStr.substring(index+"<DisableStopDate>".length(),responseStr.indexOf("</DisableStopDate>",index));
		        	responseMap.put("resp_disableStopDate",resp_disableStopDate);
		    	}
	          	index=responseStr.indexOf("<ServiceStopDate>");
	          	if(index!=-1)
		    	{
	          		String resp_serviceStopDate=responseStr.substring(index+"<ServiceStopDate>".length(),responseStr.indexOf("</ServiceStopDate>",index));
	          		responseMap.put("resp_serviceStopDate",resp_serviceStopDate);
		    	}
	          	index=responseStr.indexOf("<MainBalance>");
	          	if(index!=-1)
		    	{
	          		String resp_mainBalance=responseStr.substring(index+"<MainBalance>".length(),responseStr.indexOf("</MainBalance>",index));
	          		responseMap.put("resp_mainBalance",resp_mainBalance);
		    	}
	          	index=responseStr.indexOf("<ServiceClass>");
	          	if(index!=-1)
		    	{
	          		String resp_serviceClass=responseStr.substring(index+"<ServiceClass>".length(),responseStr.indexOf("</ServiceClass>",index));
	          		responseMap.put("resp_serviceClass",resp_serviceClass);
		    	}
				index=responseStr.indexOf("<TransactionSN>");
				if(index!=-1)
		    	{
					String resp_transactionSN=responseStr.substring(index+"<TransactionSN>".length(),responseStr.indexOf("</TransactionSN>",index));
					responseMap.put("resp_transactionSN",resp_transactionSN);
		    	}
	        }
		}
		catch(Exception e)
		{
			_log.errorTrace("parseGetAccountInfoResponse",e);
		}
		return responseMap;
	}
	
}


