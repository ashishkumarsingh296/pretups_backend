package com.inter.hcpt.receiver;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.MComConnection;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.txn.voms.voucher.businesslogic.VomsVoucherTxnDAO;
import com.inter.hcpt.logger.ExternalGatewayRequestLog;
import java.util.concurrent.atomic.AtomicInteger;

public class ExternalReceiver extends HttpServlet
{	
	private static final Log log= LogFactory.getLog(ExternalReceiver.class.getName());
	private static AtomicInteger requestID=new AtomicInteger();

	public void doGet(HttpServletRequest p_request, HttpServletResponse p_response) throws jakarta.servlet.ServletException, java.io.IOException
	{
		String methodName = "doGet";
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Entered");
		}

		doPost(p_request, p_response);

		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exiting");
		}

	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws jakarta.servlet.ServletException, java.io.IOException
	{
		String methodName = "doPost";
		String requestIDStr=String.valueOf(requestID.incrementAndGet());
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Entered");
		}


		Connection con = null;
		MComConnectionI mcomCon = null;		
		String requestMessage = null;
		String requestType = null;
		String targetReceiver = null;
		String targetURL = null;
		String targetRequestString = null;
		String responseString = null;
		String formattedResponseString = null;
		PrintWriter out = null;
		final long requestStartTime = System.currentTimeMillis();
		try
		{
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Type", "text/html; charset=UTF-8");
			out = response.getWriter();			
			requestMessage = extractRequestMessage(request);
			if (log.isDebugEnabled()) {
				log.debug(methodName,"requestMessage= "+requestMessage);
			}
			ExternalGatewayRequestLog.inLog(requestIDStr,requestMessage);
			final HashMap<String,String> requestHashMap = getMap(requestMessage);			
			requestType = requestHashMap.get("TYPE");			
			
			
			switch (requestType) {
			case "QRCODEENQREQ":
				targetReceiver = PretupsI.SERVICE_TYPE_VOMS;
				targetURL = getURL(targetReceiver);
				targetRequestString = getEnquiryRequestString(requestHashMap);
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseQRCodeEnq(responseString);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"QRCODEENQRES");
				}
				out.println(formattedResponseString);
				//ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "VOUENQREQ":
				targetReceiver = PretupsI.SERVICE_TYPE_VOMS;
				targetURL = getURL(targetReceiver);
				targetRequestString = getEnquiryRequestString(requestHashMap);
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseVoucherEnq(responseString);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"VOUENQRES");
				}
				out.println(formattedResponseString);
				// ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "UNLOCKQRCODEREQ":
				targetReceiver = PretupsI.C2S_MODULE;
				targetURL = getURL(targetReceiver);
				targetRequestString = getChangeStatusRequestString(requestHashMap,"UNLOCKQRCODEREQ");
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseQRUnlockReq(responseString, requestHashMap);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"UNLOCKQRCODERES");
				}
				out.println(formattedResponseString);
				// ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "UNLOCKZEROREQ":
				targetReceiver = PretupsI.C2S_MODULE;
				targetURL = getURL(targetReceiver);
				targetRequestString = getProfileAssociationRequestString(requestHashMap);
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseUnlockZeroReq(responseString, requestHashMap);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"UNLOCKZERORES");
				}
				out.println(formattedResponseString);
				//ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "VCHRBLOCKREQ":
				targetReceiver = PretupsI.C2S_MODULE;
				targetURL = getURL(targetReceiver);
				targetRequestString = getChangeStatusRequestString(requestHashMap,"VCHRBLOCKREQ");
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseVoucherBlockReq(responseString);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"VCHRBLOCKRES");
				}
				out.println(formattedResponseString);
				// ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "UNLOCKVCHRREQ":
				targetReceiver = PretupsI.C2S_MODULE;
				targetURL = getURL(targetReceiver);
				targetRequestString = getChangeStatusRequestString(requestHashMap,"UNLOCKVCHRREQ");
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseUnlockVoucherReq(responseString);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"UNLOCKVCHRRES");
				}
				out.println(formattedResponseString);
				//ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "VOMSCONSREQ":
				targetReceiver = PretupsI.P2P_MODULE;
				targetURL = getURL(targetReceiver);
				targetRequestString = getVoucherConsumptionRequestString(requestHashMap);
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseVoucherConsumptionReq(responseString, requestHashMap);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"VOMSCONSRES");
				}
				out.println(formattedResponseString);
				//ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "VCHRREVREQ":
				targetReceiver = PretupsI.C2S_MODULE;
				targetURL = getURL(targetReceiver);
				loadAndSetParameterFromDB(requestHashMap);
				targetRequestString = getChangeStatusRequestString(requestHashMap,"VCHRREVREQ");				
				responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseVoucherReversalReq(responseString);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"VCHRREVRES");
				}
				out.println(formattedResponseString);
				// ExternalGatewayRequestLog.outLog(formattedResponseString);
				break;
			case "CRMRECHARGE":
				targetReceiver = PretupsI.P2P_MODULE;
				targetURL = getURL(targetReceiver);
				loadAndSetParameterFromDB(requestHashMap);
				if (null != requestHashMap.get("VOUCHERCODE")
						&& !BTSLUtil.isNullString(requestHashMap.get("VOUCHERCODE"))) {
					targetRequestString = getVoucherConsumptionRequestString(requestHashMap);
					responseString = connectToPreTUPS(targetURL, targetRequestString);
					if (!BTSLUtil.isNullString(responseString)) {
						formattedResponseString = parseResponseVoucherConsumptionReq(responseString, requestHashMap);
					} else {
						formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
								"CRMRECHARGERES");
					}
				} else {
					formattedResponseString = generateFailureResponse(requestHashMap);
				}

				out.println(formattedResponseString);
				// ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
			case "GETPINREQ":
				loadAndSetParameterFromDB(requestHashMap);
				formattedResponseString = parseResponseGetPinReq(requestHashMap);
				out.println(formattedResponseString);
				//ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
				break;
                        case "LOCKVCHRREQ":
                        	targetReceiver = PretupsI.C2S_MODULE;
                            targetURL = getURL(targetReceiver);
                            targetRequestString = getChangeStatusRequestString(requestHashMap,"LOCKVCHRREQ");
                            responseString = connectToPreTUPS(targetURL, targetRequestString);
				if (!BTSLUtil.isNullString(responseString)) {
					formattedResponseString = parseResponseVoucherLockReq(responseString);
				} else {
					formattedResponseString = parseResponseSocketTimeoutError(responseString, requestHashMap,
							"LOCKVCHRRES");
				}
                            out.println(formattedResponseString);
				//ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString);
                        	break;                        	
			default:
				log.error(methodName, "Request Type invalid = "+requestType);
				break;
			}
		}
		catch(Exception ex)
		{
			log.error(methodName,"There is some internal error. please try after some time."+ex);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		finally{
		ExternalGatewayRequestLog.outLog(requestIDStr,formattedResponseString,requestStartTime);
		out.flush();
			if(mcomCon != null)
			{
				mcomCon.close("USSDReceiverServlet#doPost");
				mcomCon=null;
			}
		}
	}//end of doPost

	private String connectToPreTUPS(String p_url,String p_request){

		String methodName = "connectToPreTUPS";
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Entered");
		}

		URL url=null;
		PrintWriter out=null;
		BufferedReader in=null;		
		String responseStr = null;  
		HttpURLConnection _urlConnection=null;		
		try {
			String connTimeOut = Constants.getProperty("EXT_CONN_TIME_OUT");
			String readTimeOut = Constants.getProperty("EXT_READ_TIME_OUT");
			
			url=new URL(p_url);
			_urlConnection=(HttpURLConnection)url.openConnection();
			try{
				_urlConnection.setConnectTimeout(Integer.parseInt(connTimeOut));
			}
			catch(Exception e){
				_urlConnection.setConnectTimeout(10000);	
			}

			try{
				_urlConnection.setReadTimeout(Integer.parseInt(connTimeOut));
			}
			catch(Exception e){
				_urlConnection.setReadTimeout(10000);	
			}

			_urlConnection.setDoOutput(true);
			_urlConnection.setDoInput(true);
			_urlConnection.addRequestProperty("Content-Type", "text/xml");
			_urlConnection.setRequestMethod("POST");
			StringBuffer buffer = new StringBuffer();
			String respStr = "";
			try
			{
				if (log.isDebugEnabled()) {
					log.debug(methodName," : Request sent   ="+p_request);
				}

				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())),true);                  
				out.println(p_request);
				out.flush();
				in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
				while ((respStr = in.readLine()) != null)
				{
					buffer.append(respStr);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}//end of catch-Exception
			finally
			{
				try{if(out!=null)out.close();}catch(Exception e){e.printStackTrace();}
				try{if(in!=null)in.close();}catch(Exception e){e.printStackTrace();}
			}//end of finally
			responseStr = buffer.toString();
			/*System.out.println("Response Received :: "+responseStr);         

			//parsePrepareResponse(responseStr,c2CTransfer);

			String txnStatus = responseStr.substring(responseStr.indexOf("<TXNSTATUS>")+"<TXNSTATUS>".length(),responseStr.indexOf("</TXNSTATUS>"));            
			pin = responseStr.substring(responseStr.indexOf("<PIN>")+"<PIN>".length(),responseStr.indexOf("</PIN>"));
			catCode = responseStr.substring(responseStr.indexOf("<CAT_CODE>")+"<CAT_CODE>".length(),responseStr.indexOf("</CAT_CODE>"));


			HashMap<String,String> responseMap = new HashMap<String,String>();
			responseMap.put("TXN_STATUS",txnStatus);
			responseMap.put("PIN",pin);
			responseMap.put("CAT_CODE",catCode);
			 */
			return responseStr;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} 
		finally{
			if(_urlConnection!=null)
				_urlConnection = null;
			if(out!=null)
				out = null;
			if(in!=null)
				in = null;
		}
	}

	private String extractRequestMessage(HttpServletRequest p_request){
		String methodName = "extractRequestMessage";
		StringBuffer msg = new StringBuffer();
		try{
			final ServletInputStream in = p_request.getInputStream();
			int c;		
			while ((c = in.read()) != -1) {
				// Process line...
				msg.append((char) c);
			}		
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		return msg.toString();
	}
	
	private HashMap<String,String> getMap(String p_requestStr){
		String methodName = "getMap";
		HashMap<String,String> requestMap = new HashMap<String,String>();
		
		try{

			int index1, index2, index3, index4;
            String str1, str2, key, value;
            
            for (int ind = p_requestStr.indexOf("<COMMAND>") + 9; ind < p_requestStr.length() - 10; ind = index4 + 1) {
                index1 = p_requestStr.indexOf("<", ind);
                index2 = p_requestStr.indexOf(">", index1);
                key = p_requestStr.substring(index1 + 1, index2);
                if (!key.startsWith("/")){
                	index3 = p_requestStr.indexOf("</", index2);
                	index4 = p_requestStr.indexOf(">", index3);
                	str1 = p_requestStr.substring(index1, index2 + 1);
                	str2 = p_requestStr.substring(index3, index4 + 1);
                	value = p_requestStr.substring(index1 + str1.length(), p_requestStr.indexOf(str2, ind));
                	requestMap.put(key, value);
                	if (log.isDebugEnabled()) {
                		log.debug(methodName, "Exiting key:value= " + key + ":" + value);
                	}
                }else{
                	index4=p_requestStr.length() - 10;
                }
            }
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestMap= "+requestMap);
		}		
		return requestMap;
	}
	
	private String getURL(String p_recvr){
		String methodName = "getURL";		
		String httpURLPrefix = "http://";
		StringBuffer urlToSend = new StringBuffer();
		String ip = null;
		String context = null;
		String receiver = null;
		String port = null;
		String login = null;
		String password = null;
		String gatewayCode = null;
		String gatewayType = null;
		String sourceType = null;
		String servicePort = null;
		String nCode = null;
		try{
			ip = Constants.getProperty("EXT_IP");
			context = Constants.getProperty("EXT_CONTEXT");
			receiver = Constants.getProperty("EXT_RECEIVER");
			port = Constants.getProperty("EXT_PORT");
			login = Constants.getProperty("EXT_LOGIN");
			password = Constants.getProperty("EXT_PASSWORD");
			gatewayCode = Constants.getProperty("EXT_GATEWAYCODE");
			gatewayType = Constants.getProperty("EXT_GATEWAYTYPE");
			sourceType = Constants.getProperty("EXT_SOURCETYPE");
			servicePort = Constants.getProperty("EXT_SERVICEPORT");
			nCode = Constants.getProperty("EXT_NETWORK_CODE");
			if(PretupsI.SERVICE_TYPE_VOMS.equalsIgnoreCase(p_recvr))
				receiver = Constants.getProperty("EXT_VOMS_RECEIVER");
			else if(PretupsI.P2P_MODULE.equalsIgnoreCase(p_recvr))
			receiver = Constants.getProperty("EXT_P2P_RECEIVER");

			urlToSend.append(httpURLPrefix);
			urlToSend.append(ip+":"+port+"/"+context+"/"+receiver);           
			urlToSend.append("?REQUEST_GATEWAY_CODE="+gatewayCode);
			urlToSend.append("&REQUEST_GATEWAY_TYPE="+gatewayType);
			urlToSend.append("&LOGIN="+login);
			urlToSend.append("&PASSWORD="+password);
			urlToSend.append("&SOURCE_TYPE="+sourceType);
			urlToSend.append("&SERVICE_PORT="+servicePort);
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : url= "+urlToSend.toString());
		}		
		return urlToSend.toString();
	}

	private String getEnquiryRequestString(HashMap p_requestHashMap){
		String methodName = "getEnquiryRequestString";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_requestString = "+p_requestHashMap);
		}	
		
		StringBuffer requestString = new StringBuffer();
		try{		
			requestString.append("<?xml version=\"1.0\"?>");
			requestString.append("<COMMAND>");
			requestString.append("<TYPE>VOUQRYREQ</TYPE>");
			requestString.append("<VTYPE>"+p_requestHashMap.get("VTYPE")+"</VTYPE>");
			requestString.append("<ACTION>"+p_requestHashMap.get("ACTION")+"</ACTION>");
			requestString.append("<SNO>"+p_requestHashMap.get("SNO")+"</SNO>");
			if(!BTSLUtil.isNullString((String) p_requestHashMap.get("MASTERSERIALNO")) && ! BTSLUtil.isNumeric((String) p_requestHashMap.get("MASTERSERIALNO"))) {
				throw new BTSLBaseException("Master Serial number can't be alphanumeric.");
			} else {
				requestString.append("<MASTERSERIALNO>"+p_requestHashMap.get("MASTERSERIALNO")+"</MASTERSERIALNO>");
			}
			requestString.append("<PIN>"+p_requestHashMap.get("PIN")+"</PIN>");
			requestString.append("<LOGINID>"+p_requestHashMap.get("LOGINID")+"</LOGINID>");
			requestString.append("<PASSWORD>"+p_requestHashMap.get("PASSWORD")+"</PASSWORD>");
			requestString.append("<EXTCODE>"+p_requestHashMap.get("EXTCODE")+"</EXTCODE>");
			requestString.append("<EXTREFNUM>"+p_requestHashMap.get("EXTREFNUM")+"</EXTREFNUM>");
			requestString.append("<EXTNWCODE>"+p_requestHashMap.get("EXTNWCODE")+"</EXTNWCODE>");
			requestString.append("<LANGUAGE1>"+p_requestHashMap.get("LANGUAGE1")+"</LANGUAGE1>");
			requestString.append("<LANGUAGE2>"+p_requestHashMap.get("LANGUAGE2")+"</LANGUAGE2>");
			requestString.append("</COMMAND>");		
		} catch(BTSLBaseException e){
			log.errorTrace(methodName,e);
		} catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+requestString.toString());
		}		
		return requestString.toString();
	}

	private String getChangeStatusRequestString(HashMap p_requestHashMap,String p_request){
		String methodName = "getChangeStatusRequestString";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_requestString = "+p_requestHashMap);
		}	
		
		StringBuffer requestString = new StringBuffer();
		try{		
			requestString.append("<?xml version=\"1.0\"?>");
			requestString.append("<COMMAND>");
			requestString.append("<TYPE>VOMSSTCHGREQ</TYPE>");
			requestString.append("<FROM_SERIALNO>"+p_requestHashMap.get("FROM_SERIALNO")+"</FROM_SERIALNO>");
			requestString.append("<TO_SERIALNO>"+p_requestHashMap.get("TO_SERIALNO")+"</TO_SERIALNO>");
			requestString.append("<STATUS>"+p_requestHashMap.get("STATUS")+"</STATUS>");
			if(!BTSLUtil.isNullString((String) p_requestHashMap.get("MASTER_SERIALNO")) && ! BTSLUtil.isNumeric((String) p_requestHashMap.get("MASTER_SERIALNO"))) {
				throw new BTSLBaseException("Master Serial number can't be alphanumeric.");
			} else {
				requestString.append("<MASTER_SERIALNO>"+p_requestHashMap.get("MASTER_SERIALNO")+"</MASTER_SERIALNO>");
			}
			requestString.append("<MSISDN>"+p_requestHashMap.get("MSISDN")+"</MSISDN>");
			requestString.append("<PIN>"+p_requestHashMap.get("PIN")+"</PIN>");
			requestString.append("<LOGINID>"+p_requestHashMap.get("LOGINID")+"</LOGINID>");
			requestString.append("<PASSWORD>"+p_requestHashMap.get("PASSWORD")+"</PASSWORD>");
			requestString.append("<EXTCODE>"+p_requestHashMap.get("EXTCODE")+"</EXTCODE>");
			requestString.append("<EXTNWCODE>"+p_requestHashMap.get("EXTNWCODE")+"</EXTNWCODE>");
			
			if("UNLOCKQRCODEREQ".equalsIgnoreCase(p_request)){
				requestString.append("<INFO1>"+p_requestHashMap.get("INFO1")+"</INFO1>");
				requestString.append("<INFO2></INFO2>");
				//requestString.append("<INFO3>"+p_requestHashMap.get("COST")+"#"+p_requestHashMap.get("RETAILERREGION")+"</INFO3>");
				requestString.append("<INFO3>"+p_requestHashMap.get("COST")+"#"+((String)p_requestHashMap.get("RETAILERREGION")).replaceAll(PretupsI.SPACE,"@sp@")+"</INFO3>");
				String date;
				Date t = new Date();				
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			       					
				date = formatter.format(t);				
				requestString.append("<INFO4>"+date+"</INFO4>");
				requestString.append("<EXTXNID>"+p_requestHashMap.get("EXT_TXN_ID")+"</EXTXNID>");
			}
			
			if("VCHRBLOCKREQ".equalsIgnoreCase(p_request)){
				requestString.append("<INFO1></INFO1>");				
				requestString.append("<INFO2>"+p_requestHashMap.get("SCRAPREASON")+"#"+p_requestHashMap.get("SCRAPSOURCE")+"</INFO2>");				
				requestString.append("<INFO3></INFO3>");								
				requestString.append("<INFO4></INFO4>");
				requestString.append("<EXTXNID>"+p_requestHashMap.get("EXT_TXN_ID")+"</EXTXNID>");
			}
			
			if("UNLOCKVCHRREQ".equalsIgnoreCase(p_request)){
				requestString.append("<INFO1></INFO1>");				
				requestString.append("<INFO2></INFO2>");				
				requestString.append("<INFO3></INFO3>");								
				requestString.append("<INFO4></INFO4>");
				requestString.append("<EXT_TXN_ID></EXT_TXN_ID>");
			}
			
			if("VCHRREVREQ".equalsIgnoreCase(p_request)){
				requestString.append("<INFO1></INFO1>");				
				requestString.append("<INFO2></INFO2>");				
				requestString.append("<INFO3></INFO3>");								
				requestString.append("<INFO4></INFO4>");
				requestString.append("<EXT_TXN_ID>"+p_requestHashMap.get("EXT_TXN_ID")+"</EXT_TXN_ID>");
			}
						
			requestString.append("</COMMAND>");		
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+requestString.toString());
		}		
		return requestString.toString();
	}

	private String getProfileAssociationRequestString(HashMap p_requestHashMap){
		String methodName = "getProfileAssociationRequestString";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_requestString = "+p_requestHashMap);
		}	
		
		StringBuffer requestString = new StringBuffer();
		try{		
			requestString.append("<?xml version=\"1.0\"?>");
			requestString.append("<COMMAND>");
			requestString.append("<TYPE>EXVOUPRFMOD</TYPE>");
			requestString.append("<DATE>"+p_requestHashMap.get("DATE")+"</DATE>");
			requestString.append("<EXTNWCODE>"+p_requestHashMap.get("EXTNWCODE")+"</EXTNWCODE>");
			requestString.append("<MSISDN>"+p_requestHashMap.get("MSISDN")+"</MSISDN>");
			requestString.append("<PIN>"+p_requestHashMap.get("PIN")+"</PIN>");
			requestString.append("<LOGINID>"+p_requestHashMap.get("LOGINID")+"</LOGINID>");
			requestString.append("<PASSWORD>"+p_requestHashMap.get("PASSWORD")+"</PASSWORD>");
			requestString.append("<EXTCODE>"+p_requestHashMap.get("EXTCODE")+"</EXTCODE>");
			requestString.append("<EXTREFNUM>"+p_requestHashMap.get("EXTREFNUM")+"</EXTREFNUM>");
			if(!BTSLUtil.isNullString((String) p_requestHashMap.get("MASTERSERIALNO")) && ! BTSLUtil.isNumeric((String) p_requestHashMap.get("MASTERSERIALNO"))) {
				throw new BTSLBaseException("Master Serial number can't be alphanumeric.");
			} else {
				requestString.append("<MASTERSERIALNO>"+p_requestHashMap.get("MASTERSERIALNO")+"</MASTERSERIALNO>");
			}
			requestString.append("<SERIALNO>"+p_requestHashMap.get("SERIALNO")+"</SERIALNO>");
			requestString.append("<PRODUCTSHORTNAME>"+p_requestHashMap.get("PRODUCTID")+"</PRODUCTSHORTNAME>");
			requestString.append("<LANGUAGE1>"+p_requestHashMap.get("LANGUAGE1")+"</LANGUAGE1>");
			requestString.append("<LANGUAGE2>"+p_requestHashMap.get("LANGUAGE2")+"</LANGUAGE2>");
			requestString.append("<EXTXNID>"+p_requestHashMap.get("EXT_TXN_ID")+"</EXTXNID>");
			requestString.append("<INFO1>"+p_requestHashMap.get("RESELLER")+"</INFO1>");
			requestString.append("<INFO3>"+p_requestHashMap.get("COST")+"#"+p_requestHashMap.get("RETAILERREGION")+"</INFO3>");
			String date;
                        Date t = new Date();
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date = formatter.format(t);
                        requestString.append("<INFO4>"+date+"</INFO4>");
			requestString.append("</COMMAND>");		
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+requestString.toString());
		}		
		return requestString.toString();
	}

	private String getVoucherConsumptionRequestString(HashMap p_requestHashMap){
		String methodName = "getVoucherConsumptionRequestString";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_requestString = "+p_requestHashMap);
		}	
		
		StringBuffer requestString = new StringBuffer();
		try{		
			requestString.append("<?xml version=\"1.0\"?>");
			requestString.append("<COMMAND>");
			requestString.append("<TYPE>VOMSCONSREQ</TYPE>");
			requestString.append("<DATE>"+p_requestHashMap.get("DATE")+"</DATE>");
			requestString.append("<EXTNWCODE>"+p_requestHashMap.get("EXTNWCODE")+"</EXTNWCODE>");
			requestString.append("<MSISDN>"+p_requestHashMap.get("MSISDN")+"</MSISDN>");
			requestString.append("<PIN>"+p_requestHashMap.get("PIN")+"</PIN>");
			requestString.append("<MSISDN2>"+p_requestHashMap.get("MSISDN2")+"</MSISDN2>");
			requestString.append("<AMOUNT>"+p_requestHashMap.get("AMOUNT")+"</AMOUNT>");
			requestString.append("<EXTCODE>"+p_requestHashMap.get("EXTCODE")+"</EXTCODE>");
			requestString.append("<EXTREFNUM>"+p_requestHashMap.get("EXTREFNUM")+"</EXTREFNUM>");
			requestString.append("<VOUCHERCODE>"+p_requestHashMap.get("VOUCHERCODE")+"</VOUCHERCODE>");
			requestString.append("<SERIALNUMBER>"+p_requestHashMap.get("SERIALNUMBER")+"</SERIALNUMBER>");
			requestString.append("<LANGUAGE1>"+p_requestHashMap.get("LANGUAGE1")+"</LANGUAGE1>");
			requestString.append("<LANGUAGE2>"+p_requestHashMap.get("LANGUAGE2")+"</LANGUAGE2>");
			requestString.append("<SELECTOR>"+p_requestHashMap.get("SELECTOR")+"</SELECTOR>");
			requestString.append("<INFO1>"+p_requestHashMap.get("INFO1")+"</INFO1>");
			requestString.append("<INFO2>"+p_requestHashMap.get("INFO2")+"</INFO2>");
			requestString.append("<INFO3>"+p_requestHashMap.get("INFO3")+"</INFO3>");
			requestString.append("<INFO4>"+p_requestHashMap.get("INFO4")+"</INFO4>");
			requestString.append("<INFO5>"+p_requestHashMap.get("INFO5")+"</INFO5>");
			requestString.append("</COMMAND>");		
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+requestString.toString());
		}		
		return requestString.toString();
	}
	
	
	private String parseResponseQRCodeEnq(String p_responseStr){
		String methodName = "parseResponseQRCodeEnq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = new HashMap();
		StringBuffer responseString = new StringBuffer();
		
		try{	
		responseMap.put("TXNSTATUS", p_responseStr.substring(p_responseStr.indexOf("<TXNSTATUS>") + "<TXNSTATUS>".length(), p_responseStr.indexOf("</TXNSTATUS>", p_responseStr.indexOf("<TXNSTATUS>"))));	
		if(PretupsI.TXN_STATUS_SUCCESS.equalsIgnoreCase((String)responseMap.get("TXNSTATUS"))){	
			extractResponseInTags(p_responseStr, responseMap);
			
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");
    		responseString.append("<TYPE>QRCODEENQRES</TYPE>");
    		responseString.append("<TXNSTATUS>"+responseMap.get("TXNSTATUS")+"</TXNSTATUS>");
    		responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
    		responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");
    		responseString.append("<QRCODESTATUS>"+responseMap.get("QRSTATUS")+"</QRCODESTATUS>");
    		responseString.append("<QRCODECOST>"+responseMap.get("QRCODECOST")+"</QRCODECOST>");
    		responseString.append("<QRUNLOCKDATE>"+responseMap.get("QRUNLOCKDATE")+"</QRUNLOCKDATE>");
    		if(!BTSLUtil.isNullString((String)responseMap.get("QRRESELLER")) && !"null".equals((String)responseMap.get("QRRESELLER")))
    			responseString.append("<RESELLER>"+responseMap.get("QRRESELLER")+"</RESELLER>");
    		else
    			responseString.append("<RESELLER></RESELLER>");
    		responseString.append("<VMSPINDETAILINFO>");
    		
    		int listSize = Integer.parseInt((String)responseMap.get("LISTSIZE"));
    		
    		for(int c=0;c<listSize;c++){    			
    			HashMap tempMap = (HashMap)responseMap.get("V"+c);    			
    			responseString.append(((LookupsVO)LookupsCache.getObject(VOMSI.LOOKUP_VOUCHER_STATUS,(String)tempMap.get("STATUS"))).getLookupName()+",");	
    			responseString.append(tempMap.get("SERIALNO")+",");
    			if(VOMSI.VOMS_PRE_ACTIVE_STATUS.equalsIgnoreCase((String)tempMap.get("STATUS")))
    				responseString.append("LOCK,");
    			else
    				responseString.append("UNLOCK,");
    			
    			if(VOMSI.VOUCHER_DAMAGED.equalsIgnoreCase((String)tempMap.get("STATUS")))
    				responseString.append("SCRAP,");
    			else
    				responseString.append("UNSCRAP,");
    			
    			responseString.append(tempMap.get("EXPIRYDATE")+",");
                        if(tempMap.get("INFO5")!=null)
                        	tempMap.put("INFO5",new StringEscapeUtils().unescapeHtml4((String)tempMap.get("INFO5")));
                        String info5[] = String.valueOf(tempMap.get("INFO5")).split("#");
                        try {
                                responseString.append(info5[4]+",");
                        } catch (ArrayIndexOutOfBoundsException e) {
                                responseString.append(""+",");
                                e.printStackTrace();
                        }
                        if(String.valueOf(tempMap.get("INFO2")).contains("#")){
                                String info2[] = String.valueOf(tempMap.get("INFO2")).split("#");
                                try {
                                        responseString.append(info2[0]+",");
                                } catch (ArrayIndexOutOfBoundsException e) {
                                        responseString.append(""+",");
                                        e.printStackTrace();
                                }
                                try {
                                        responseString.append(info2[1]+",");
                                } catch (ArrayIndexOutOfBoundsException e) {
                                        responseString.append(""+",");
                                        e.printStackTrace();
                                }
                        }
                        else{
                                responseString.append(""+",");
                                responseString.append(""+",");
                        }
                        //responseString.append(responseMap.get("BUNDLENAME")+",");
                                responseString.append(info5[1]+",");
                                String denomination = (String) tempMap.get("DENOMINATION");
                        if(Integer.parseInt(denomination.substring(denomination.indexOf(".")+1,denomination.length()))==0) {
                                tempMap.put("DENOMINATION",denomination.substring(0,denomination.indexOf(".")));
                        }
                        responseString.append(tempMap.get("DENOMINATION")+",");
                        try {
                                responseString.append(info5[5]+",");
                        } catch (ArrayIndexOutOfBoundsException e) {
                                responseString.append(""+",");
                                e.printStackTrace();
                        }
                        responseString.append(tempMap.get("MRP")+",");
                        responseString.append("-1,");
                        responseString.append(tempMap.get("MSISDN")+",");
                        responseString.append(responseMap.get("MASTERSERIALNO")+",");
                        responseString.append(tempMap.get("USERNAME")+",");
                        responseString.append(",");
                        try {
                                responseString.append(info5[2]+",");
                        } catch (ArrayIndexOutOfBoundsException e) {
                                responseString.append(""+",");
                                e.printStackTrace();
                        }
                        try {
                                responseString.append(info5[3]+",");
                        } catch (ArrayIndexOutOfBoundsException e) {
                                responseString.append(""+",");
                                e.printStackTrace();
                        }
                        responseString.append(tempMap.get("CONSUMEDBY")+",");
                        if(c==(listSize-1))
                                responseString.append(tempMap.get("CONSUMEDON"));
                        else
                                responseString.append(tempMap.get("CONSUMEDON")+"|");
                }
                responseString.append("</VMSPINDETAILINFO>");
                responseString.append("</COMMAND>");
                }
                else
                        {
                                responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
                                responseString.append("<COMMAND>");
                                responseString.append("<TYPE>QRCODEENQRES</TYPE>");
                                responseString.append("<TXNSTATUS>"+responseMap.get("TXNSTATUS")+"</TXNSTATUS>");
                                responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
                                responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");
                                responseString.append("<QRCODESTATUS></QRCODESTATUS>");
                                responseString.append("<QRCODECOST></QRCODECOST>");
                                responseString.append("<QRUNLOCKDATE></QRUNLOCKDATE>");
                                responseString.append("<RESELLER></RESELLER>");
                                responseString.append("<VMSPINDETAILINFO></VMSPINDETAILINFO>");
                                responseString.append("</COMMAND>");
                        }
                }
                catch(Exception e){
                        log.errorTrace(methodName,e);
                }
                if (log.isDebugEnabled()) {
                        log.debug(methodName,"Exit : requestString= "+responseString.toString());
                }
                return responseString.toString();
        }

	
	
	private String parseResponseVoucherEnq(String p_responseStr){
		String methodName = "parseResponseVoucherEnq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = new HashMap();
		StringBuffer responseString = new StringBuffer();
		
		try{		
			extractResponseInTags(p_responseStr,responseMap);
			HashMap tempMap = null;
			if(responseMap.containsKey("V0"))
				tempMap = (HashMap)responseMap.get("V0");
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");
    		responseString.append("<TYPE>VOUENQRES</TYPE>");
    		responseString.append("<TXNSTATUS>"+responseMap.get("TXNSTATUS")+"</TXNSTATUS>");
    		responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
    		responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");
    		if(tempMap != null) {
	    		responseString.append("<SERIALNUMBER>"+tempMap.get("SERIALNO")+"</SERIALNUMBER>");
	    		responseString.append("<VOUCHERSTATUS>"+((LookupsVO)LookupsCache.getObject(VOMSI.LOOKUP_VOUCHER_STATUS,(String)tempMap.get("STATUS"))).getLookupName()+"</VOUCHERSTATUS>");
	    		if(VOMSI.VOMS_PRE_ACTIVE_STATUS.equalsIgnoreCase((String)tempMap.get("STATUS")))
	    			responseString.append("<LOCKSTATUS>LOCK</LOCKSTATUS>");
	    		else
	    			responseString.append("<LOCKSTATUS>UNLOCK</LOCKSTATUS>");
	    		if(VOMSI.VOUCHER_DAMAGED.equalsIgnoreCase((String)tempMap.get("STATUS")))
	    			responseString.append("<SCRAPSTATUS>SCRAP</SCRAPSTATUS>");
	    		else
	    			responseString.append("<SCRAPSTATUS>UNSCRAP</SCRAPSTATUS>");
	    		
	    		if(!BTSLUtil.isNullString((String)tempMap.get("INFO2")) && String.valueOf(tempMap.get("INFO2")).contains("#")){
	    			String info2[] = String.valueOf(tempMap.get("INFO2")).split("#");
	    			try {
	    				responseString.append("<SCRAPREASON>"+info2[0]+"</SCRAPREASON>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<SCRAPREASON></SCRAPREASON>");
	    				e.printStackTrace();
	    			}
	    			try {
	    				responseString.append("<SCRAPSOURCE>"+info2[1]+"</SCRAPSOURCE>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<SCRAPSOURCE></SCRAPSOURCE>");
	    				e.printStackTrace();
	    			}
	    		}
	    		else
	    		{
	    			responseString.append("<SCRAPREASON></SCRAPREASON>");
	    			responseString.append("<SCRAPSOURCE></SCRAPSOURCE>");
	    		}
	    		String denomination = (String) tempMap.get("DENOMINATION");
	    		if(Integer.parseInt(denomination.substring(denomination.indexOf(".")+1,denomination.length()))==0) {
	    			tempMap.put("DENOMINATION",denomination.substring(0,denomination.indexOf(".")));
    			} 
	    		responseString.append("<DENOMINATION>"+tempMap.get("DENOMINATION")+"</DENOMINATION>");
	    		responseString.append("<PACKAGENAME>"+responseMap.get("BUNDLENAME")+"</PACKAGENAME>");
	    		
                        if(tempMap.get("INFO5")!=null)
                        	tempMap.put("INFO5",new StringEscapeUtils().unescapeHtml4((String)tempMap.get("INFO5")));
                        if(!BTSLUtil.isNullString((String)tempMap.get("INFO5"))){
                                String info5[] = String.valueOf(tempMap.get("INFO5")).split("#");
                                try {
                                        responseString.append("<VALIDITY>"+info5[4]+"</VALIDITY>");
                                } catch (ArrayIndexOutOfBoundsException e) {
                                        responseString.append("<VALIDITY></VALIDITY>");
                                        e.printStackTrace();
                                }
                        }
                        else
                        {
                                responseString.append("<VALIDITY></VALIDITY>");
                        }
                        responseString.append("<EXPIRYDATE>"+tempMap.get("EXPIRYDATE")+"</EXPIRYDATE>");

                        if(tempMap.get("INFO5")!=null)
                        	tempMap.put("INFO5",new StringEscapeUtils().unescapeHtml4((String)tempMap.get("INFO5")));
	    		if(!BTSLUtil.isNullString((String)tempMap.get("INFO5"))){
	    			String info5[] = String.valueOf(tempMap.get("INFO5")).split("#");
	    			try {
	    				responseString.append("<GRACEPERIOD>"+info5[5]+"</GRACEPERIOD>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<GRACEPERIOD></GRACEPERIOD>");
	    				e.printStackTrace();
	    			}
	    		}
	    		responseString.append("<BONUSAMT>"+tempMap.get("MRP")+"</BONUSAMT>");
	    		responseString.append("<BONUSUNIT>-1</BONUSUNIT>");
	    		responseString.append("<PVMSISDN>"+tempMap.get("MSISDN")+"</PVMSISDN>");
	    		responseString.append("<USERNAME>"+tempMap.get("USERNAME")+"</USERNAME>");
	    		responseString.append("<QRCODE>"+responseMap.get("MASTERSERIALNO")+"</QRCODE>");
	    		responseString.append("<VOUCHERPIN>"+tempMap.get("PIN")+"</VOUCHERPIN>");
	    		responseString.append("<QRCODESTATUS>"+responseMap.get("QRSTATUS")+"</QRCODESTATUS>");
                        if(tempMap.get("INFO5")!=null)
                        	tempMap.put("INFO5",new StringEscapeUtils().unescapeHtml4((String)tempMap.get("INFO5")));
	    		if(!BTSLUtil.isNullString((String)tempMap.get("INFO5"))){
	    			String info5[] = String.valueOf(tempMap.get("INFO5")).split("#");
	    			try {
	    				responseString.append("<SECONDARYPRODUCTCODE>"+info5[2]+"</SECONDARYPRODUCTCODE>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<SECONDARYPRODUCTCODE></SECONDARYPRODUCTCODE>");
	    				e.printStackTrace();
	    			}
	        		try {
						responseString.append("<SECONDARYPRODUCTNAME>"+info5[3]+"</SECONDARYPRODUCTNAME>");
					} catch (ArrayIndexOutOfBoundsException e) {
						responseString.append("<SECONDARYPRODUCTNAME></SECONDARYPRODUCTNAME>");
						e.printStackTrace();
					}
	       		}
	    		responseString.append("<VCHRUNLOCKDATE>"+responseMap.get("QRUNLOCKDATE")+"</VCHRUNLOCKDATE>");    		
	    		responseString.append("<RESELLER>"+tempMap.get("INFO1")+"</RESELLER>");
                        if(tempMap.get("INFO3")!=null)
                        	tempMap.put("INFO3",new StringEscapeUtils().unescapeHtml4((String)tempMap.get("INFO3")));
	    		if(!BTSLUtil.isNullString((String)tempMap.get("INFO3"))){
	    			String info3[] = String.valueOf(tempMap.get("INFO3")).split("#");
	    			try {
	    				responseString.append("<UNLOCKPRICE>"+info3[0]+"</UNLOCKPRICE>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<UNLOCKPRICE></UNLOCKPRICE>");
						e.printStackTrace();
	    			}
	    		}
	    		responseString.append("<TOPUPMSISDN>"+tempMap.get("CONSUMEDBY")+"</TOPUPMSISDN>");
	    		responseString.append("<USEDDATE>"+tempMap.get("CONSUMEDON")+"</USEDDATE>");
    		} else {
    			responseString.append("<SERIALNUMBER></SERIALNUMBER>");
    			responseString.append("<VOUCHERSTATUS></VOUCHERSTATUS>");
    			responseString.append("<LOCKSTATUS></LOCKSTATUS>");
    			responseString.append("<SCRAPSTATUS></SCRAPSTATUS>");
    			responseString.append("<DENOMINATION></DENOMINATION>");
    			responseString.append("<VALIDITY></VALIDITY>");
    			responseString.append("<EXPIRYDATE></EXPIRYDATE>");
    			responseString.append("<GRACEPERIOD></GRACEPERIOD>");
    			responseString.append("<BONUSAMT></BONUSAMT>");
    			responseString.append("<BONUSUNIT></BONUSUNIT>");
    			responseString.append("<VOUCHERPIN></VOUCHERPIN>");
    			responseString.append("<QRCODESTATUS></QRCODESTATUS>");
    			responseString.append("<SECONDARYPRODUCTCODE></SECONDARYPRODUCTCODE>");
    			responseString.append("<SECONDARYPRODUCTNAME></SECONDARYPRODUCTNAME>");
    		}
    		responseString.append("</COMMAND>");  
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+responseString.toString());
		}		
		return responseString.toString();
	}
	
	
	private String parseResponseQRUnlockReq(String p_responseStr,HashMap p_requestMap){
		String methodName = "parseResponseQRUnlockReq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = null;
		StringBuffer responseString = new StringBuffer();
		
		try{		
			responseMap = getMap(p_responseStr);			
			
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");
    		responseString.append("<TYPE>UNLOCKQRCODERES</TYPE>");    		
    		responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
    		responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");    		
    		responseString.append("<QRCODE>"+p_requestMap.get("MASTER_SERIALNO")+"</QRCODE>");
    		responseString.append("<RESELLER>"+p_requestMap.get("INFO1")+"</RESELLER>");    		    		    		
    		responseString.append("</COMMAND>");  
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+responseString.toString());
		}		
		return responseString.toString();
	}	
	
	
	private String parseResponseUnlockZeroReq(String p_responseStr,HashMap p_requestMap){
		String methodName = "parseResponseUnlockZeroReq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = null;
		StringBuffer responseString = new StringBuffer();
		
		try{		
			responseMap = getMap(p_responseStr);			
			
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");
    		responseString.append("<TYPE>UNLOCKZERORES</TYPE>");    		
    		responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
    		responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");    		
    		responseString.append("<QRCODE>"+p_requestMap.get("MASTERSERIALNO")+"</QRCODE>");
    		responseString.append("<SERIALNO>"+p_requestMap.get("SERIALNO")+"</SERIALNO>");
    		responseString.append("<RESELLER>"+p_requestMap.get("RESELLER")+"</RESELLER>");    		    		    		
    		responseString.append("</COMMAND>");  
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+responseString.toString());
		}		
		return responseString.toString();
	}
	
	private String parseResponseUnlockVoucherReq(String p_responseStr){
		String methodName = "parseResponseUnlockVoucherReq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = null;
		StringBuffer responseString = new StringBuffer();
		
		try{		
			responseMap = getMap(p_responseStr);			
			
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");
    		responseString.append("<TYPE>UNLOCKVCHRRES</TYPE>");    		
    		responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
    		responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");  		
    		responseString.append("</COMMAND>");  
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+responseString.toString());
		}		
		return responseString.toString();
	}
	
	private String parseResponseVoucherBlockReq(String p_responseStr){
		String methodName = "parseResponseVoucherBlockReq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = null;
		StringBuffer responseString = new StringBuffer();
		
		try{		
			responseMap = getMap(p_responseStr);			
			
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");
    		responseString.append("<TYPE>VCHRBLOCKRES</TYPE>");    		
    		responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
    		responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");  		
    		responseString.append("</COMMAND>");  
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+responseString.toString());
		}		
		return responseString.toString();
	}
	
        private String parseResponseVoucherConsumptionReq(String p_responseStr,HashMap p_requestMap){
		String methodName = "parseResponseVoucherConsumptionReq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = null;
		StringBuffer responseString = new StringBuffer();
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList dataList = new ArrayList();
		try{		
			responseMap = getMap(p_responseStr);
			String message = (String)responseMap.get("MESSAGE");
			int index = message.indexOf("<SERIALNO>");
			if(index>0 && PretupsI.TXN_STATUS_SUCCESS.equalsIgnoreCase((String)responseMap.get("TXNSTATUS"))){
				responseMap.put("SERIALNO",message.substring(index + "<SERIALNO>".length(), message.length()));
				VomsVoucherTxnDAO txnDAO = new VomsVoucherTxnDAO();
				RequestVO dummyVO = new RequestVO();
				dummyVO.setSerialnumber((String)responseMap.get("SERIALNO"));
				dummyVO.setExternalNetworkCode(Constants.getProperty("EXT_NETWORK_CODE"));
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				dataList = txnDAO.voucherEnquiryUsingMasterSerialNoORSerialNo(con,dummyVO,false);
			}
			
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");

                if("VOMSCONSREQ".equalsIgnoreCase((String)p_requestMap.get("TYPE")))
	    		responseString.append("<TYPE>VOMSCONSRES</TYPE>"); 
                else if("CRMRECHARGE".equalsIgnoreCase((String)p_requestMap.get("TYPE")))
                	responseString.append("<TYPE>CRMRECHARGERES</TYPE>");

		responseString.append("<RESPONSE_CODE>"+(String)responseMap.get("TXNSTATUS")+"</RESPONSE_CODE>");
                if(!PretupsI.TXN_STATUS_SUCCESS.equalsIgnoreCase((String)responseMap.get("TXNSTATUS")))
                	responseString.append("<RESPONSE_MSG>"+(String)responseMap.get("MESSAGE")+"</RESPONSE_MSG>");
                else
                	responseString.append("<RESPONSE_MSG></RESPONSE_MSG>");

    		responseString.append("<TXNSTATUS>"+responseMap.get("TXNSTATUS")+"</TXNSTATUS>");
    		responseString.append("<DATE>"+responseMap.get("DATE")+"</DATE>");
    		responseString.append("<EXTREFNUM>"+responseMap.get("EXTREFNUM")+"</EXTREFNUM>");
    		responseString.append("<TXNID>"+responseMap.get("TXNID")+"</TXNID>");
    		if(dataList.size()>0){
    			VomsVoucherVO vomsVO = (VomsVoucherVO)dataList.get(0);
    			if(Integer.parseInt(vomsVO.getOption().substring(vomsVO.getOption().indexOf(".")+1, vomsVO.getOption().length()))==0) {
    				vomsVO.setOption(vomsVO.getOption().substring(0,vomsVO.getOption().indexOf(".")));
    			} 
    			responseString.append("<DENOM>"+vomsVO.getOption()+"</DENOM>");
                        if(vomsVO.getInfo5()!=null)
                        	vomsVO.setInfo5(new StringEscapeUtils().unescapeHtml4(vomsVO.getInfo5()));           
    			if(!BTSLUtil.isNullString(vomsVO.getInfo5())){
	    			String info5[] = vomsVO.getInfo5().split("#");
	    			try {
	    				responseString.append("<VALIDITY>"+info5[4]+"</VALIDITY>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<VALIDITY></VALIDITY>");
	    				e.printStackTrace();
	    			}
	    			try {
	    				responseString.append("<GRACEPERIOD>"+info5[5]+"</GRACEPERIOD>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<GRACEPERIOD></GRACEPERIOD>");
	    				e.printStackTrace();
	    			}
	    			try {
	    				responseString.append("<PRODUCT_ID>"+info5[0]+"</PRODUCT_ID>");
	    			} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<PRODUCT_ID></PRODUCT_ID>");
	    				e.printStackTrace();
	    			}
    			}
    			else    			
    				responseString.append("<VALIDITY></VALIDITY><GRACEPERIOD></GRACEPERIOD><PRODUCT_ID></PRODUCT_ID>");
    			
    			responseString.append("<SERIALNO>"+vomsVO.getSerialNo()+"</SERIALNO>");    			
				responseString.append("<VOUCHERTYPE>"+vomsVO.getVoucherType()+"</VOUCHERTYPE>");
				if(!(BTSLUtil.isNullString(vomsVO.getInfo4())) && !("null".equals(vomsVO.getInfo4())))
					responseString.append("<QRUNLOCKDATE>"+vomsVO.getInfo4()+"</QRUNLOCKDATE>");
				else
					responseString.append("<QRUNLOCKDATE></QRUNLOCKDATE>");
    			responseString.append("<QRCODE>"+vomsVO.getMasterSerialNo()+"</QRCODE>");
                        if(vomsVO.getInfo3()!=null)
                        	vomsVO.setInfo3(new StringEscapeUtils().unescapeHtml4(vomsVO.getInfo3()));
    			if(!BTSLUtil.isNullString(vomsVO.getInfo3())){
    				String info3[] = vomsVO.getInfo3().split("#");
    				try {
    					responseString.append("<RETAILERREGION>"+info3[1].replaceAll("@sp@", " ")+"</RETAILERREGION>");
    				} catch (ArrayIndexOutOfBoundsException e) {
	    				responseString.append("<RETAILERREGION></RETAILERREGION>");
	    				e.printStackTrace();
	    			}
    			}
    			else
    				responseString.append("<RETAILERREGION></RETAILERREGION>");
    			responseString.append("<RESELLER>"+vomsVO.getInfo1()+"</RESELLER>");
    		}
    		else{
    			responseString.append("<DENOM></DENOM>");    			
    			responseString.append("<VALIDITY></VALIDITY>");
    			responseString.append("<GRACEPERIOD></GRACEPERIOD>");
    			responseString.append("<SERIALNO></SERIALNO>");
    			responseString.append("<PRODUCT_ID></PRODUCT_ID>");
    			responseString.append("<VOUCHERTYPE></VOUCHERTYPE>");
    			responseString.append("<QRUNLOCKDATE></QRUNLOCKDATE>");
    			responseString.append("<QRCODE></QRCODE>");   			
    			responseString.append("<RETAILERREGION></RETAILERREGION>");
    			responseString.append("<RESELLER></RESELLER>");
    		}    		
    		responseString.append("</COMMAND>");  
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		finally{
			if (mcomCon != null)
				mcomCon.close("ExternalReceiver#parseResponseVoucherConsumptionReq");
				mcomCon = null;
				con = null;
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+responseString.toString());
		}		
		return responseString.toString();
	}
	
	private String parseResponseVoucherReversalReq(String p_responseStr){
		String methodName = "parseResponseVoucherBlockReq";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
		}	
		
		HashMap responseMap = null;
		StringBuffer responseString = new StringBuffer();
		
		try{		
			responseMap = getMap(p_responseStr);			
			
    		responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
    		responseString.append("<COMMAND>");
    		responseString.append("<TYPE>VCHRREVRES</TYPE>");    		
    		responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
    		responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");  		
    		responseString.append("</COMMAND>");  
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : requestString= "+responseString.toString());
		}		
		return responseString.toString();
	}
	
	
	private void loadAndSetParameterFromDB(HashMap p_requestMap){
		
		String methodName = "loadAndSetParameterFromDB";
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Enter : p_requestMap = "+p_requestMap);
		}	
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		StringBuffer requestString = new StringBuffer();
		try{		
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			VOMSVoucherDAO vmsDAO = new VOMSVoucherDAO();
                        VomsVoucherDAO vomsDAO = new VomsVoucherDAO();
			VOMSVoucherVO vomsVO = null;
                        if("VCHRREVREQ".equalsIgnoreCase((String)p_requestMap.get("TYPE")))
                        {
			ArrayList voucherList = vmsDAO.loadVoucherByPin(con, (String)p_requestMap.get("PIN"),null,VOMSI.VOUCHER_USED);
			if(voucherList.size()>0){
				vomsVO = (VOMSVoucherVO)voucherList.get(0);
				p_requestMap.put("FROM_SERIALNO", vomsVO.getSerialNo());
				p_requestMap.put("TO_SERIALNO", vomsVO.getSerialNo());
				p_requestMap.put("STATUS", VOMSI.VOUCHER_ENABLE);	
				p_requestMap.put("EXTNWCODE", Constants.getProperty("EXT_NETWORK_CODE"));	
				if(!p_requestMap.containsKey("MASTER_SERIALNO"))
					p_requestMap.put("MASTER_SERIALNO", "");
				
				if(!p_requestMap.containsKey("MSISDN"))
					p_requestMap.put("MSISDN", "");
						
				if(!p_requestMap.containsKey("LOGINID"))
					p_requestMap.put("LOGINID", "");
					
				if(!p_requestMap.containsKey("PASSWORD"))
					p_requestMap.put("PASSWORD", "");
                        	}
                        }
                        else if("CRMRECHARGE".equalsIgnoreCase((String)p_requestMap.get("TYPE")))
                        {
                        	ArrayList voucherList = vmsDAO.loadVoucherByDenomShortName(con, (String)p_requestMap.get("DENOM"), (String)p_requestMap.get("VOUCHERTYPE"));
                        	
                        	p_requestMap.put("DATE", "");
                        	p_requestMap.put("PIN", "");
                        	p_requestMap.put("MSISDN2", "");
                        	p_requestMap.put("AMOUNT", "");
                        	p_requestMap.put("EXTCODE", "");
                        	p_requestMap.put("EXTREFNUM", "");
                        	p_requestMap.put("LANGUAGE1", "");
                        	p_requestMap.put("LANGUAGE2", "");
                        	p_requestMap.put("SELECTOR", "");
                        	p_requestMap.put("INFO2", "");
                        	p_requestMap.put("INFO3", "");
                        	p_requestMap.put("INFO4", "");
                        	p_requestMap.put("INFO5", "");
                        	
                        	if(voucherList.size()>0){
                        		vomsVO = (VOMSVoucherVO)voucherList.get(0);
                        		p_requestMap.put("VOUCHERCODE", VomsUtil.decryptText(vomsVO.getPinNo()));
                        		p_requestMap.put("SERIALNUMBER", vomsVO.getSerialNo());                        		
                        	}
                        }
                        else if("GETPINREQ".equalsIgnoreCase((String)p_requestMap.get("TYPE")))
                        {
                        	TransferVO trfVO = new TransferVO();
                        	trfVO.setSerialNumber((String)p_requestMap.get("SERIALNUMBER"));
                        	VomsVoucherVO voucherVO = vomsDAO.loadVomsVoucherVO(con, trfVO);
                        	if(voucherVO!=null){
                        		p_requestMap.put("VOUCHERCODE", VomsUtil.decryptText(voucherVO.getPinNo()));
                        	}
			}
		}
		catch(Exception e){
			log.errorTrace(methodName,e);
		}
		finally
		{
			if (mcomCon != null)
				mcomCon.close("ExternalReceiver#loadAndSetParameterFromDB");
			mcomCon = null;
			con = null;
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName,"Exit : p_requestMap= "+p_requestMap);
		}		
	}
	
	
	private void extractResponseInTags(String p_responseStr,HashMap p_responseMap){
		int index1, index2, index3, index4;
        String str1, str2, key, value;
        String detailsInfo = null;
        
		for (int ind = p_responseStr.indexOf("<COMMAND>") + 9; ind < p_responseStr.length() - 10; ind = index4 + 1) {
			index1 = p_responseStr.indexOf("<", ind);
			index2 = p_responseStr.indexOf(">", index1);
			key = p_responseStr.substring(index1 + 1, index2);
			if (!key.startsWith("/")){
					if(key.equalsIgnoreCase("DETAILINFO"))
						index3 = p_responseStr.indexOf("</DETAILINFO", index2);
					else
						index3 = p_responseStr.indexOf("</", index2);
					index4 = p_responseStr.indexOf(">", index3);
					str1 = p_responseStr.substring(index1, index2 + 1);
					str2 = p_responseStr.substring(index3, index4 + 1);
					value = p_responseStr.substring(index1 + str1.length(), p_responseStr.indexOf(str2, ind));
					if(key.equalsIgnoreCase("DETAILINFO"))
						detailsInfo = value;
					else{
						p_responseMap.put(key,value);
					}
			}else{
				index4=p_responseStr.length() - 10;
			}
		}		
		if(detailsInfo != null && !BTSLUtil.isNullString(detailsInfo)) {
			String []A = detailsInfo.split("</VOUCHER>");		
			String p_requestStrLocal=null;		
			p_responseMap.put("LISTSIZE", String.valueOf(A.length));    		
			boolean lockFlag = false;
			HashMap dataMap = null;
			Date qrUnlockDate = null;
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(int i = 0 ;i<A.length;i++){			
				p_requestStrLocal = A[i];	
				lockFlag = false;
				dataMap = new HashMap();
				for (int ind = p_requestStrLocal.indexOf("<VOUCHER>") + 9; ind < p_requestStrLocal.length() - 10; ind = index4 + 1) {    				
					index1 = p_requestStrLocal.indexOf("<", ind);
					index2 = p_requestStrLocal.indexOf(">", index1);
					key = p_requestStrLocal.substring(index1 + 1, index2);
					if (!key.startsWith("/")){						
							index3 = p_requestStrLocal.indexOf("</", index2);
							index4 = p_requestStrLocal.indexOf(">", index3);
							str1 = p_requestStrLocal.substring(index1, index2 + 1);
							str2 = p_requestStrLocal.substring(index3, index4 + 1);
							value = p_requestStrLocal.substring(index1 + str1.length(), p_requestStrLocal.indexOf(str2, ind));    						
							dataMap.put(key,value);
							
							if("INFO1".equalsIgnoreCase(key))
								p_responseMap.put("QRRESELLER", value);
							
							
							if("INFO4".equalsIgnoreCase(key))
							{						
							 try {
									Date dt = formatter.parse(value);
									if(i==0)
										qrUnlockDate = dt;
									else
									{
									if(dt.before(qrUnlockDate))
										qrUnlockDate = dt;
									}
							 } catch (ParseException e) {
									e.printStackTrace();
								}
							}
													
							if(!lockFlag && "STATUS".equalsIgnoreCase(key) && "PA".equalsIgnoreCase(value))
								lockFlag = true;
							
					}else{
						index4=p_requestStrLocal.length() - 10;
					} 				
				}	
				p_responseMap.put("V"+i, dataMap);				
			}
				
			if(lockFlag)
				p_responseMap.put("QRSTATUS", "LOCK");
			else
				p_responseMap.put("QRSTATUS", "UNLOCK");
			
	       		if(qrUnlockDate!=null)
				p_responseMap.put("QRUNLOCKDATE", formatter.format(qrUnlockDate));
			else
				p_responseMap.put("QRUNLOCKDATE", "");
		}
	}
	
	
	private String mapVMSCode(String p_txnStatus){
		
		String statusMapping = Constants.getProperty("STATUS_MAPPING");		
		String splittedStatus[] = statusMapping.split("[|]");
		String returnString = null;
		
		for(int c = 0;c<splittedStatus.length;c++){			
			if(p_txnStatus.equalsIgnoreCase(splittedStatus[c].split("[:]")[0])){
				returnString = splittedStatus[c];
				break;
			}
		}
		if(BTSLUtil.isNullString(returnString)){
                	returnString = p_txnStatus+":"+p_txnStatus+":"+p_txnStatus;
                }
		return returnString;
	}
        private String generateFailureResponse(HashMap p_requestMap){
        	StringBuffer responseString = new StringBuffer();
        	
        	String type = (String)p_requestMap.get("TYPE");
        	
        	switch(type){
        	case "CRMRECHARGE":        		
        		responseString.append("<?xml version=\"\"1.0\"\"?>");
        		responseString.append("<!DOCTYPE COMMAND PUBLIC \"\"-//Ocam//DTD XML Command 1.0//EN\"\" \"\"xml/command.dtd\"\">");
        		responseString.append("<COMMAND>");
        		responseString.append("<TYPE>CRMRECHARGERES</TYPE>");
			responseString.append("<RESPONSE_CODE>"+PretupsErrorCodesI.VOMS_NOT_ENOUGH_VOUCHERS+"</RESPONSE_CODE>");
                	responseString.append("<RESPONSE_MSG>Not enough voucher exist in system</RESPONSE_MSG>");
        		responseString.append("<TXNSTATUS>"+PretupsErrorCodesI.VOMS_NOT_ENOUGH_VOUCHERS+"</TXNSTATUS>");
        		responseString.append("<DATE></DATE>");
        		responseString.append("<EXTREFNUM></EXTREFNUM>");
        		responseString.append("<TXNID></TXNID>");
        		responseString.append("<DENOM></DENOM>");
        		responseString.append("<VALIDITY></VALIDITY>");
        		responseString.append("<GRACEPERIOD></GRACEPERIOD>");
        		responseString.append("<PRODUCT_ID></PRODUCT_ID>");
        		responseString.append("<SERIALNO></SERIALNO>");
        		responseString.append("<VOUCHERTYPE></VOUCHERTYPE>");
        		responseString.append("<QRUNLOCKDATE></QRUNLOCKDATE>");
        		responseString.append("<QRCODE></QRCODE>");
        		responseString.append("<RETAILERREGION></RETAILERREGION>");
        		responseString.append("<RESELLER></RESELLER>");
        		responseString.append("</COMMAND>");
        	break;
        	}
        	return responseString.toString();
        }
        
        private String parseResponseGetPinReq(HashMap p_requestMap){
        	StringBuffer responseString = new StringBuffer();
        	
        		responseString.append("<?xml version=\"\"1.0\"\"?>");
        		responseString.append("<!DOCTYPE COMMAND PUBLIC \"\"-//Ocam//DTD XML Command 1.0//EN\"\" \"\"xml/command.dtd\"\">");
        		responseString.append("<COMMAND>");
        		responseString.append("<TYPE>GETPINRES</TYPE>");
        		if(p_requestMap.get("VOUCHERCODE")!=null){
        			responseString.append("<TXNSTATUS>200</TXNSTATUS>");        		
        			responseString.append("<SERIALNO>"+p_requestMap.get("SERIALNUMBER")+"</SERIALNO>");
        			responseString.append("<VOUCHERCODE>"+p_requestMap.get("VOUCHERCODE")+"</VOUCHERCODE>");
        		}
        		else
        		{
        			responseString.append("<TXNSTATUS>"+PretupsErrorCodesI.VOUCHER_NOT_FOUND+"</TXNSTATUS>");        		
        			responseString.append("<SERIALNO>"+p_requestMap.get("SERIALNUMBER")+"</SERIALNO>");
        			responseString.append("<VOUCHERCODE></VOUCHERCODE>");
        		}
        		responseString.append("</COMMAND>");
        	return responseString.toString();
        }
        
        private String parseResponseVoucherLockReq(String p_responseStr){
            String methodName = "parseResponseVoucherLockReq";

            if (log.isDebugEnabled()) {
                    log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
            }

            HashMap responseMap = null;
            StringBuffer responseString = new StringBuffer();

            try{
                    responseMap = getMap(p_responseStr);

            responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
            responseString.append("<COMMAND>");
            responseString.append("<TYPE>LOCKVCHRRES</TYPE>");
            responseString.append("<RESPONSE_CODE>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[1]+"</RESPONSE_CODE>");
            responseString.append("<RESPONSE_MSG>"+(mapVMSCode((String)responseMap.get("TXNSTATUS"))).split(":")[2]+"</RESPONSE_MSG>");
            responseString.append("</COMMAND>");
            }
            catch(Exception e){
                    log.errorTrace(methodName,e);
            }
            if (log.isDebugEnabled()) {
                    log.debug(methodName,"Exit : requestString= "+responseString.toString());
            }
            return responseString.toString();
    }
        
        private String parseResponseSocketTimeoutError(String p_responseStr,HashMap p_requestMap,String type){
    		String methodName = "parseResponseSocketTimeoutError";
    		
    		if (log.isDebugEnabled()) {
    			log.debug(methodName,"Enter : p_reponseStr = "+p_responseStr);
    		}	
  
    		StringBuffer responseString = new StringBuffer();
    		
    		try{	
    			
    			responseString.append("<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XML Command 1.0//EN\" \"xml/command.dtd\">");
        		responseString.append("<COMMAND>");
        		responseString.append("<TYPE>"+type+"</TYPE>"); 
        		responseString.append("<RESPONSE_CODE>"+PretupsErrorCodesI.GENERAL_PROCESSING_ERROR+"</RESPONSE_CODE>");
        		responseString.append("<RESPONSE_MSG>Your request is under process. Please check the status after some time</RESPONSE_MSG>");
    			if("QRCODEENQRES".equals(type)) {
    				responseString.append("<TXNSTATUS>"+PretupsErrorCodesI.GENERAL_PROCESSING_ERROR+"</TXNSTATUS>");
	        		responseString.append("<QRCODESTATUS></QRCODESTATUS>");
                    responseString.append("<QRCODECOST></QRCODECOST>");
                    responseString.append("<QRUNLOCKDATE></QRUNLOCKDATE>");
                    responseString.append("<RESELLER></RESELLER>");
                    responseString.append("<VMSPINDETAILINFO></VMSPINDETAILINFO>");
	        		responseString.append("</COMMAND>"); 
    			} else if ("VOUENQRES".equals(type)) {
    				responseString.append("<TXNSTATUS>"+PretupsErrorCodesI.GENERAL_PROCESSING_ERROR+"</TXNSTATUS>");
    				responseString.append("<SERIALNUMBER></SERIALNUMBER>");
        			responseString.append("<VOUCHERSTATUS></VOUCHERSTATUS>");
        			responseString.append("<LOCKSTATUS></LOCKSTATUS>");
        			responseString.append("<SCRAPSTATUS></SCRAPSTATUS>");
        			responseString.append("<DENOMINATION></DENOMINATION>");
        			responseString.append("<VALIDITY></VALIDITY>");
        			responseString.append("<EXPIRYDATE></EXPIRYDATE>");
        			responseString.append("<GRACEPERIOD></GRACEPERIOD>");
        			responseString.append("<BONUSAMT></BONUSAMT>");
        			responseString.append("<BONUSUNIT></BONUSUNIT>");
        			responseString.append("<VOUCHERPIN></VOUCHERPIN>");
        			responseString.append("<QRCODESTATUS></QRCODESTATUS>");
        			responseString.append("<SECONDARYPRODUCTCODE></SECONDARYPRODUCTCODE>");
        			responseString.append("<SECONDARYPRODUCTNAME></SECONDARYPRODUCTNAME>");
        			responseString.append("</COMMAND>");
    			} else { 		    		    		
	        		responseString.append("</COMMAND>"); 
    			}
    		}
    		catch(Exception e){
    			log.errorTrace(methodName,e);
    		}
    		if (log.isDebugEnabled()) {
    			log.debug(methodName,"Exit : requestString= "+responseString.toString());
    		}		
    		return responseString.toString();
    	}
        
}
