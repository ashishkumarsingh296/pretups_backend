package simulator.loadtest.loadgenerator;
/**
 * @(#)RequestThread.java
 * Copyright(c) 2008, Bharti Telesoft Ltd.
 * All Rights Reserved
 *
 * <description>
 *-------------------------------------------------------------------------------------------------
 * Author                        Date            		History
 *-------------------------------------------------------------------------------------------------
 * chetan.kothari             july 2,2008     	Initital Creation
 *-------------------------------------------------------------------------------------------------
 *
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import simulator.loadtest.loadgenerator.logging.Log;
import simulator.loadtest.loadgenerator.logging.LogFactory;
import java.util.Base64;



public class RequestThread  extends Thread{
	
	private  static Log			_logger=LogFactory.getLog("RequestThread");
	private int _urlLocation=0;
	private static int _connTimeOut=0;
	private static int _readTimeOut=0;
	private LoadRequestVO _requestVO;
	private  static boolean _allRequest =true;
	private  String  _request;
	private int _actionParameter;
	private  static Map 	  decisionMap=new HashMap();
	private  static int 	 _urlCount=0;
	private  static int 	 _subscriberNumberCountList0=0;
	private  static int 	 _subscriberNumberCountList1=0;
	private  static int 	 _subscriberNumberCountList2=0;
	private  static int 	 _subscriberNumberCountList3=0;
	private  static int 	 _transactionIDCounter=0;
	private  static int  	 _prevMinut=0;
	private  static int 	 _transactionPerSecond;
    private  static String 	 _msisdnListVal[]=null;
    private  static String 	 _msisdnListCr[]=null;
    private  static String 	 _msisdnListCrAdj[]=null;
    private  static String 	 _msisdnListDrAdj[]=null;
    private  static String   _action=null;
	private  static String 	 _creditAdjust="CREDIT_ADJUST";
    private  static String 	 _debitAdjust="DEBIT_ADJUST";
    private  static String 	 _credit="CREDIT";
    private  static String 	 _validate="VALIDATE";
    private  static String   _host;
    private  static String   _userAgent;
    private  static String   _contentType;
    private  static HashMap  _requestMap=new HashMap();
    private  static URL[]    _url; 
    private  static  SimpleDateFormat _sdf = new SimpleDateFormat ("yyMMddHHmm");

	public RequestThread(int p_action,int p_urlLocation )
	{
		_urlLocation=p_urlLocation;
		_requestVO=new LoadRequestVO();
		_actionParameter=p_action;
	}
	
	/**
	 * Method run.
	 * This is the method to send request to a particular url.
	 * @param 	p_request  String 
	 * @param 	p_all     boolean
	 */
	public  void run()
	{
		HttpURLConnection urlConnection=null;
		InputStream is= null;
		OutputStream out=null;
		long contentLength=0;
		long t=0;
	if(_logger.isDebugEnabled())
			DetailLog.debug("run","  Eneterd ");
	
		try {
			findAction();
			URL u =null;
			CountingLog.log(String.valueOf(System.currentTimeMillis()));
			u = _url[_urlLocation];
			_requestVO.setRequestStartTime(System.currentTimeMillis());
			_requestVO.setNodeName(u.toString());
			urlConnection = (HttpURLConnection)u.openConnection();			
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setConnectTimeout(_connTimeOut);
            urlConnection.setReadTimeout(_readTimeOut);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            contentLength=_request.length();
            setRequestHeader(urlConnection,contentLength,_host,_userAgent,_contentType);
            out=urlConnection.getOutputStream();
            out.write(_request.getBytes());
			t=System.currentTimeMillis();
			if(_logger.isDebugEnabled())
				DetailLog.error(" ","  Request XML :: "+_request);
			//_logger.info("run ","  Request XML :: "+_request);
		} catch (MalformedURLException e) {
			DetailLog.error("run ","  Error while requesting  "+e.getMessage());
			//_logger.error("run ","  Error while requesting  "+e.getMessage());
		} catch (ProtocolException e) {
			DetailLog.error("run  ","  Error while requesting  "+e.getMessage());
			//_logger.error("run  ","  Error while requesting  "+e.getMessage());
		} catch (IOException e) {
			DetailLog.error("run  ","  Error while requesting  "+e.getMessage());
			//_logger.error("run ","  Error while requesting  "+e.getMessage());
		}
		 catch (Exception e) {
		 	DetailLog.error("run "," Error while requesting "+e.getMessage());
		 	//_logger.error("run "," Error while requesting "+e.getMessage());
		}
		try{
			if(_logger.isDebugEnabled())
				DetailLog.info("run ","   Response message..................    "+ urlConnection.getResponseMessage()+" from server "+_urlLocation);
			//_logger.info("run ","   Response message..................    "+ urlConnection.getResponseMessage()+" from server "+p_urlLocation);		
			_requestVO.setHttpStatus(urlConnection.getResponseMessage());
			is= urlConnection.getInputStream();
			int c;
			int m=0;
			String responseStream= "";
			while(is!=null && (c= is.read())!=-1)
			{
				responseStream +=(char)c;
			}
			_requestVO.setRequestEndTime(System.currentTimeMillis());
			if(_logger.isDebugEnabled())
				DetailLog.error("","   Response XML............... " +responseStream);
			//_logger.info("doRequest ","   Response XML............... " +responseStream);
			parseResponse(responseStream);
			long dur = System.currentTimeMillis()-t;
			//_logger.info("doRequest ","  Response time ....................    "+dur+" ms");
		} catch (MalformedURLException e) {
			DetailLog.error("run ","  Error while getting response "+e.getMessage());
			_logger.error("run ","  Error while getting response "+e.getMessage());
			_requestVO.setRequestStatus(e.getMessage());
		} catch (ProtocolException e) {
			DetailLog.error("run ","   Error while getting response "+e.getMessage());
			_logger.error("run ","   Error while getting response "+e.getMessage());
			_requestVO.setRequestStatus(e.getMessage());
		} catch (IOException e) {
			DetailLog.error("run","   Error while getting response "+e.getMessage());
			_logger.error("run","   Error while getting response "+e.getMessage());
			_requestVO.setRequestStatus(e.getMessage());
		}
		finally
		{
			try {
				if(is!=null)
					is.close();
				if(out!=null)
					out.close();
				if(urlConnection!=null)
					urlConnection.disconnect();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(_logger.isDebugEnabled())
				DetailLog.debug("run ","  Exiting");
			//	_logger.debug("doRequest ","  Exiting");
		}
	LoadTestLog.log(_requestVO);
	}
	
	 /**
     * This method is used to set the header informations
     * 1.Host
     * 2.User Agent
     * 3.Content length.
     * 4.Content type. 
     * @param p_contenetLength
     * @param p_host
     * @param p_userAgent
     * @param p_keepAlive
     */
    private  void setRequestHeader(HttpURLConnection p_urlConnection,long p_contenetLength,String p_host,String p_userAgent,String p_content_type) throws Exception
    {
    	if(_logger.isDebugEnabled())
    		DetailLog.debug("setRequestHeader ","  Eneterd urlConnection = "+p_urlConnection+ " p_contenetLength = " +p_contenetLength+"p_host = "+p_host+
    					"p_userAgent = "+p_userAgent+ "p_content_type = "+p_content_type);
		//_logger.debug("setRequestHeader ","  Eneterd urlConnection = "+p_urlConnection+ " p_contenetLength = " +p_contenetLength+"p_host = "+p_host+
			//	"p_userAgent = "+p_userAgent+ "p_content_type = "+p_content_type);
    	BASE64Encoder encode = new BASE64Encoder();
		String userPass="etopup:etopup";
		String encodedPass=encode.encode(userPass.getBytes()); 
        try
        {
        	p_urlConnection.setRequestProperty("Host",p_host);
        	p_urlConnection.setRequestProperty("Authorization","Basic "+encodedPass);
        	p_urlConnection.setRequestProperty("User-Agent",p_userAgent);
        	p_urlConnection.setRequestProperty("Content-Length",String.valueOf(p_contenetLength));
        	p_urlConnection.setRequestProperty("Content-Type",p_content_type);
        }
        catch(Exception e)
        {
        	DetailLog.error("setRequestHeader ","  Exception e="+e.getMessage());
        	_logger.error("setRequestHeader ","  Exception e="+e.getMessage());
            throw e;
        }
        finally
		{
        	if(_logger.isDebugEnabled())
        		DetailLog.debug("setRequestHeader","   Exiting");
      	//	_logger.debug("setRequestHeader","   Exiting");
		}
    }

    
    /**
	 * Method parseResponse.
	 * This is the method to parse response .
	 * @param 	p_response  String 
	 */
	private  void parseResponse(String p_response)
	{
		if(_logger.isDebugEnabled())
			DetailLog.debug("parseResponse ","  Eneterd p_response = "+p_response);
	//	_logger.debug("parseResponse ","  Eneterd p_response = "+p_response);
		String response=p_response;
		String moduleString=null;
		int startIndex=0;
		int endIndex=0;
		int memberStartIndex=0;
		int memberEndIndex=0;
		int lastIndex=0;
		String memberName=null;
		String memberValue=null;
		if(response.lastIndexOf("</member>")!=-1)
		{
			lastIndex=response.lastIndexOf("</member>");
			memberStartIndex=response.indexOf("<member>");
			memberEndIndex  =response.indexOf("</member>")+8;
			while(memberEndIndex<=lastIndex)
			{
				moduleString=response.substring(memberStartIndex,memberEndIndex);
				startIndex=moduleString.indexOf("<name>")+6;
				endIndex  =moduleString.indexOf("</name>");
				memberName=moduleString.substring(startIndex,endIndex);
					if(memberName.equals("responseCode"))
					{
						startIndex=moduleString.indexOf("<i4>")+4;
						endIndex  =moduleString.indexOf("</i4>");
						memberValue=moduleString.substring(startIndex,endIndex);
						_requestVO.setRequestStatus(memberValue);
						if(_logger.isDebugEnabled())
							DetailLog.info("parseResponse   ","  Response Code :: "+memberValue);
						//_logger.info("parseResponse   ","  Response Code :: "+memberValue);
					}
					if(memberName.equals("originTransactionID"))
					{
						startIndex=moduleString.indexOf("<string>")+8;
						endIndex  =moduleString.indexOf("</string>");
						memberValue=moduleString.substring(startIndex,endIndex);
						if(_logger.isDebugEnabled())
							DetailLog.info("parseResponse   ","    Origin Transaction Id :: "+memberValue);
						//_logger.info("parseResponse   ","    Origin Transaction Id :: "+memberValue);
						_requestVO.setResponseTransactionId(memberValue);
					}
				
				response=response.substring(memberEndIndex+1);
				memberStartIndex=response.indexOf("<member>");
				memberEndIndex  =response.indexOf("</member>")+8;
				while(memberEndIndex<memberStartIndex)
				{
					response=response.substring(memberEndIndex+1);
					memberStartIndex=response.indexOf("<member>");
					memberEndIndex  =response.indexOf("</member>")+8;

				}
				if(response.indexOf("<member>")==-1)
					break;
			}
		}
		else
		{
			System.out.println("parseResponse   No Parameters in response.");
		}
			if(_logger.isDebugEnabled())
				DetailLog.debug("parseResponse  "," Exiting");;
			//	_logger.debug("parseResponse  "," Exiting");
	}
	
	
private synchronized void findAction()
{
	if(_logger.isDebugEnabled())
		DetailLog.debug("findAction  "," Entered");;
	String request;
	String updatedRequest=null;
	if(_allRequest)
		{
		switch(_actionParameter)
			{
				case 1:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
						_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;
				case 2:	request=_requestMap.get(_credit).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
						_requestVO.setRequestType(_credit);
						_subscriberNumberCountList1++;
						if(_subscriberNumberCountList1>=_msisdnListCr.length)
							_subscriberNumberCountList1=0;
						break;
				case 3:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
						_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;
				case 4:	request=_requestMap.get(_credit).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
						_requestVO.setRequestType(_credit);
						_subscriberNumberCountList1++;
						if(_subscriberNumberCountList1>=_msisdnListCr.length)
							_subscriberNumberCountList1=0;
						break;
				case 5:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
						_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;
				case 6:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
						_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;
				case 7:	request=_requestMap.get(_creditAdjust).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList2,_msisdnListCrAdj);
						_requestVO.setRequestType(_creditAdjust);
						_subscriberNumberCountList2++;
						if(_subscriberNumberCountList2>=_msisdnListCrAdj.length)
							_subscriberNumberCountList2=0;
						break;
				case 8:	request=_requestMap.get(_credit).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
						_requestVO.setRequestType(_credit);
						_subscriberNumberCountList1++;
						if(_subscriberNumberCountList1>=_msisdnListCr.length)
							_subscriberNumberCountList1=0;
						break;
				case 9:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
								_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;
				case 10:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
								_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;
				case 11:	request=_requestMap.get(_creditAdjust).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList2,_msisdnListCrAdj);
							_requestVO.setRequestType(_creditAdjust);
							_subscriberNumberCountList2++;
							if(_subscriberNumberCountList2>=_msisdnListCrAdj.length)
								_subscriberNumberCountList2=0;
							break;
				case 12:	request=_requestMap.get(_credit).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
						_requestVO.setRequestType(_credit);
						_subscriberNumberCountList1++;
						if(_subscriberNumberCountList1>=_msisdnListCr.length)
							_subscriberNumberCountList1=0;
						break;	
				case 13:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
								_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;	
				case 14:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
								_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;
				case 15:	request=_requestMap.get(_credit).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
						_requestVO.setRequestType(_credit);
						_subscriberNumberCountList1++;
						if(_subscriberNumberCountList1>=_msisdnListCr.length)
							_subscriberNumberCountList1=0;
						break;	
				case 16:	request=_requestMap.get(_credit).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
							_requestVO.setRequestType(_credit);
							_subscriberNumberCountList1++;
							if(_subscriberNumberCountList1>=_msisdnListCr.length)
								_subscriberNumberCountList1=0;
							break;
				case 17:	request=_requestMap.get(_validate).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
									_requestVO.setRequestType(_validate);
							_subscriberNumberCountList0++;
							if(_subscriberNumberCountList0>=_msisdnListVal.length)
								_subscriberNumberCountList0=0;
							break;	
				case 18:	request=_requestMap.get(_validate).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
									_requestVO.setRequestType(_validate);
							_subscriberNumberCountList0++;
							if(_subscriberNumberCountList0>=_msisdnListVal.length)
								_subscriberNumberCountList0=0;
							break;	
				case 19:	request=_requestMap.get(_validate).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
								_requestVO.setRequestType(_validate);
						_subscriberNumberCountList0++;
						if(_subscriberNumberCountList0>=_msisdnListVal.length)
							_subscriberNumberCountList0=0;
						break;	
				case 20:	request=_requestMap.get(_credit).toString();
						updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
						_requestVO.setRequestType(_credit);
						_subscriberNumberCountList1++;
						if(_subscriberNumberCountList1>=_msisdnListCr.length)
							_subscriberNumberCountList1=0;
						break;
				case 21:	request=_requestMap.get(_creditAdjust).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList2,_msisdnListCrAdj);
							_requestVO.setRequestType(_creditAdjust);
							_subscriberNumberCountList2++;
							if(_subscriberNumberCountList2>=_msisdnListCrAdj.length)
								_subscriberNumberCountList2=0;
							break;
				case 22:	request=_requestMap.get(_credit).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
							_requestVO.setRequestType(_credit);
							_subscriberNumberCountList1++;
							if(_subscriberNumberCountList1>=_msisdnListCr.length)
								_subscriberNumberCountList1=0;
							break;
				case 23:	request=_requestMap.get(_validate).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
									_requestVO.setRequestType(_validate);
							_subscriberNumberCountList0++;
							if(_subscriberNumberCountList0>=_msisdnListVal.length)
								_subscriberNumberCountList0=0;
							break;	
				case 24:	request=_requestMap.get(_credit).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
							_requestVO.setRequestType(_credit);
							_subscriberNumberCountList1++;
							if(_subscriberNumberCountList1>=_msisdnListCr.length)
								_subscriberNumberCountList1=0;
							break;
				case 25:	request=_requestMap.get(_validate).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
									_requestVO.setRequestType(_validate);
							_subscriberNumberCountList0++;
							if(_subscriberNumberCountList0>=_msisdnListVal.length)
								_subscriberNumberCountList0=0;
							break;	
				case 26:	request=_requestMap.get(_validate).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
									_requestVO.setRequestType(_validate);
							_subscriberNumberCountList0++;
							if(_subscriberNumberCountList0>=_msisdnListVal.length)
								_subscriberNumberCountList0=0;
							break;	
				case 27:	request=_requestMap.get(_validate).toString();
							updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
									_requestVO.setRequestType(_validate);
							_subscriberNumberCountList0++;
							if(_subscriberNumberCountList0>=_msisdnListVal.length)
								_subscriberNumberCountList0=0;
							break;	
						
			}
		}
	else
	{	
		if(_requestMap.get(_action)==null)
		{
			DetailLog.error("generateLoad "," No action exist for given string");
			_logger.error("generateLoad "," No action exist for given string");
			System.exit(0);
		}
		else
		{
		request=_requestMap.get(_action).toString();
		int decisionParam=Integer.parseInt(decisionMap.get(_action).toString());
		switch(decisionParam){
		case 0 :
			updatedRequest=updateRequest(request,_subscriberNumberCountList0,_msisdnListVal);
			_requestVO.setRequestType(_validate);
			_subscriberNumberCountList0++;
			if(_subscriberNumberCountList0>=_msisdnListVal.length)
				_subscriberNumberCountList0=0;
			break;
	
		case 1:
			updatedRequest=updateRequest(request,_subscriberNumberCountList1,_msisdnListCr);
			_requestVO.setRequestType(_credit);
			_subscriberNumberCountList1++;
			if(_subscriberNumberCountList1>=_msisdnListCr.length)
				_subscriberNumberCountList1=0;
			break;
		
		case 2:
			updatedRequest=updateRequest(request,_subscriberNumberCountList2,_msisdnListCrAdj);
			_requestVO.setRequestType(_creditAdjust);
			_subscriberNumberCountList2++;
			if(_subscriberNumberCountList2>=_msisdnListCrAdj.length)
				_subscriberNumberCountList2=0;
			break;
		
		}
		/*if(_action.equals(_debitAdjust))
		{
			updatedRequest=updateRequest(request,_subscriberNumberCountList3,_msisdnListDrAdj);
			_requestVO.setRequestType(_debitAdjust);
			_subscriberNumberCountList3++;
			if(_subscriberNumberCountList3>=_msisdnListDrAdj.length)
				_subscriberNumberCountList3=0;
		}*/
		}
	}
	_request=updatedRequest;
	if(_logger.isDebugEnabled())
		DetailLog.debug("findAction  "," Exiting");;
}

/**
 * Method updateRequest.
 * This is the method to update subscriberNumber,Transaction id and time stamp for request .
 * @param 	p_response  String 
 */

private  synchronized String updateRequest(String p_request,int p_requestNumber,String msisdnList[])
{
	if(_logger.isDebugEnabled())
		DetailLog.debug("updateRequest ","  Enetered p_request = "+p_request+" p_requestNumber = "+p_requestNumber);
	//	_logger.debug("updateRequest ","  Enetered p_request = "+p_request+" p_requestNumber = "+p_requestNumber);
	String l_request=p_request;
	String transactionId=getINTransactionID();
	_requestVO.setMsisdn(msisdnList[p_requestNumber]);
	_requestVO.setRequestTransactionId(transactionId);
	l_request=l_request.replaceAll("XXXXXXXXXX",msisdnList[p_requestNumber]);
	l_request=l_request.replaceAll("YYYYYYYYYY",transactionId);
	l_request=l_request.replaceAll("ZZZZZZZZZZ",getTransactionTime());
	if(_logger.isDebugEnabled())
		DetailLog.debug("updateRequest ","  Exiting");
		//_logger.debug("updateRequest ","  Exiting");
	return l_request;
}


private  String getTransactionTime()
{

	SimpleDateFormat sdfDate= new SimpleDateFormat ("yyyyMMdd");
	SimpleDateFormat sdfTime = new SimpleDateFormat ("HH:mm:ss");
	Date now = new Date();
	String sign="+";
	String transDateTime=null;
	try{
	DecimalFormat twoDigits = new DecimalFormat("00");
	int offset = sdfDate.getTimeZone().getOffset(new Date().getTime());
	if (offset < 0)
		{
			offset = -offset;
			sign = "-";
		}
	int hours = offset / 3600000;
	int minutes = (offset - hours * 3600000) / 60000;
	transDateTime = sdfDate.format(now)+"T"+sdfTime.format(now)+sign+ twoDigits.format(hours)+ twoDigits.format(minutes);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally{}
	return transDateTime;
}




/* Method loadProperties.
 * This is the method to get actions, url's and time duration.
 */
public  static void loadProperties(String p_propertiesFilePath)
{
	String propertyFilePath=p_propertiesFilePath;
	Properties props = new Properties();
	FileInputStream propertiesFile = null; 
	_requestMap= new HashMap();
	String msisdn=null;
if(_logger.isDebugEnabled())
	DetailLog.debug("loadProperties ","  Eneterd ");
	//	_logger.debug("loadProperties ","  Eneterd ");
	try {
		propertiesFile = new FileInputStream(propertyFilePath);
		props.load(propertiesFile);
		String value=props.getProperty(_validate);
		_requestMap.put(_validate, value);
		value=props.getProperty(_credit);
		_requestMap.put(_credit, value);
		value=props.getProperty(_creditAdjust);
		_requestMap.put(_creditAdjust, value);
		value=props.getProperty(_debitAdjust);
		_requestMap.put(_debitAdjust, value);
		_host=props.getProperty("HOST_NAME");
		_userAgent=props.getProperty("USER_AGENT");
		_transactionPerSecond=Integer.parseInt(props.getProperty("TPS"));
		_contentType=props.getProperty("CONTENT_TYPE");
		_action=props.getProperty("ACTION");
		if(_action!=null && _action.trim().length()>0)
		{
			_allRequest=false;
		}
		if(props.getProperty("URL_COUNT")!=null)
		 _urlCount =Integer.parseInt(props.getProperty("URL_COUNT"));
		int count=0;
		_url = new URL[_urlCount];
		while(count<_urlCount)
		{
			_url[count]=new URL(props.getProperty("URL_"+count));
			count++;
		}
		msisdn=props.getProperty("MSISDN_LIST_VALIDATE");
		_msisdnListVal =msisdn.split(",");
		msisdn=props.getProperty("MSISDN_LIST_CREDIT");
		_msisdnListCr =msisdn.split(",");
		msisdn=props.getProperty("MSISDN_LIST_DEBIT_ADJ");
		_msisdnListDrAdj =msisdn.split(",");
		msisdn=props.getProperty("MSISDN_LIST_CREDIT_ADJ");
		_msisdnListCrAdj =msisdn.split(",");
		_connTimeOut=Integer.parseInt(props.getProperty("CONN_TIMEOUT"));
		_readTimeOut=Integer.parseInt(props.getProperty("READ_TIMEOUT"));
		decisionMap.put(_validate,"0");
		decisionMap.put(_credit,"1");
		decisionMap.put(_creditAdjust,"2");
	} catch (FileNotFoundException e) {
		DetailLog.error("loadProperties",e.getMessage());
		_logger.error("loadProperties",e.getMessage());
	}
	catch (NumberFormatException e) {
		DetailLog.error("loadProperties  "," Configurations are not proper in file.");
		_logger.error("loadProperties  "," Configurations are not proper in file.");
	}
	catch (IOException e) {
		DetailLog.error("loadProperties",e.getMessage());
		_logger.error("loadProperties",e.getMessage());
	}
	finally {
			if (propertiesFile != null) {
				try {
					propertiesFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (_logger.isDebugEnabled())
				DetailLog.debug("loadProperties ", "  Exiting");
			// _logger.debug("loadProperties "," Exiting");
		}
	
}



protected static synchronized String getINTransactionID() 
{
	String instanceID="02";
	int MAX_COUNTER=9999;
	String serviceType="8";
	String userType="3";
	Date mydate =null;
	String minut2Compare=null;
	String dateStr=null;
	String transactionId=null;
          try
              {
               //<service type>YYMMDDHHmm<instance code>XXXX<subscriber type>
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
                             transactionId = serviceType+ dateStr+instanceID+_transactionIDCounter+userType;
                }
                catch(Exception e)
                {
                                e.printStackTrace();
                }
                finally
                {                                              
                }
                return transactionId;                      
}

}
