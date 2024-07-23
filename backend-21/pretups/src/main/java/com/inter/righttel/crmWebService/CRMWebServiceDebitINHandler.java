package com.inter.righttel.crmWebService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.logging.InterfaceTransactionLog;
import com.btsl.util.BTSLUtil;

public class CRMWebServiceDebitINHandler {
	private static Logger logger=Logger.getLogger(CRMWebServiceDebitINHandler.class.getName());

	
	public HashMap getDEBITREQUEST(HashMap _requestMap,String _interfaceid) throws BTSLBaseException{

		if(logger.isDebugEnabled())
		logger.debug("Entering CRMWebServiceDebitINHandler for msisdn " +_requestMap.get("MSISDN") +"_interfaceID:-"+_interfaceid);
		
		long startTime=0;

		String resultCode = null ;
		String msisdn=(String)_requestMap.get("MSISDN");
		long endTime=0;
		try{
			
			startTime=System.currentTimeMillis();
			_requestMap.put("IN_START_TIME",String.valueOf(startTime));
			
			String eaiRequest = "";
			StringBuilder eaiRequestXml = new StringBuilder();

			eaiRequestXml.append("<soapenv:Envelope xmlns:diam=\"http://Diameter_pilot.hamed.com/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			eaiRequestXml.append("<soapenv:Header>");
			eaiRequestXml.append("<wsse:Security soapenv:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">");
			eaiRequestXml.append("<wsse:UsernameToken wsu:Id=\"UsernameToken-363DC33BFFA6BDC1CC15486588305091\">");
			eaiRequestXml.append("<wsse:Username>"+(String)_requestMap.get("DEBIT_USERNAME")+"</wsse:Username>");
			eaiRequestXml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">"+(String)_requestMap.get("DEBIT_USERPASSWORD")+"</wsse:Password>");
			eaiRequestXml.append("</wsse:UsernameToken>");
			eaiRequestXml.append("</wsse:Security>");
			eaiRequestXml.append("</soapenv:Header>");
			eaiRequestXml.append("<soapenv:Body>");
			eaiRequestXml.append("<diam:DIRECT_DEBITING>");
			eaiRequestXml.append("<diam:msisdn>"+(String)_requestMap.get("MSISDN")+"</diam:msisdn>");
			eaiRequestXml.append("<diam:amount>"+(String)_requestMap.get("transfer_amount")+"</diam:amount>");
			eaiRequestXml.append("<diam:RequestID>"+(String)_requestMap.get("IN_TXN_ID")+"</diam:RequestID>");
			eaiRequestXml.append("</diam:DIRECT_DEBITING>");
			eaiRequestXml.append("</soapenv:Body>");
			eaiRequestXml.append("</soapenv:Envelope>");

			eaiRequest = eaiRequestXml.toString();
			if(logger.isDebugEnabled())
				logger.debug("Entering CRMWebServiceDebitINHandler for msisdn " +msisdn +"_interfaceID:-"+_interfaceid+" Request"+eaiRequest);
			String eaiResponseXml = callEAI((String)_requestMap.get("DEBIT_USERURL"),eaiRequest,Integer.parseInt((String)_requestMap.get("DEBIT_TIMEOUT")), _interfaceid,_requestMap);
			InterfaceTransactionLog.debug(" CRMWebServiceDebitINHandler for msisdn " +msisdn +"_interfaceID:-"+_interfaceid+" Request"+eaiRequest+" Response"+eaiResponseXml);
			if(logger.isDebugEnabled())
				logger.debug("Entering CRMWebServiceDebitINHandler for msisdn " +msisdn +"_interfaceID:-"+_interfaceid+" Response"+eaiResponseXml);

			if(eaiResponseXml==null || BTSLUtil.isNullString(eaiResponseXml))
			{
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				if(logger.isDebugEnabled())
					logger.debug("Not a data for number  "+msisdn+" Blank XML recived "+eaiResponseXml);
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
				
			}
			int index = eaiResponseXml.indexOf("<esb:resultCode>");
			resultCode = eaiResponseXml.substring(index+"<esb:resultCode>".length(),eaiResponseXml.indexOf("</esb:resultCode>",index));

			if(logger.isDebugEnabled())
				logger.debug("MSISDN : "+ msisdn+" resultCode : "+resultCode);

			if(resultCode == null || BTSLUtil.isNullString(resultCode) ){
				_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.ERROR_RESPONSE);
				if(logger.isDebugEnabled())
					logger.debug("Not a data for number  "+msisdn+" result code in  XML recived iis blank= "+resultCode);
				
				throw new BTSLBaseException(InterfaceErrorCodesI.AMBIGOUS);
			}else if(resultCode.equalsIgnoreCase("2001")){
				_requestMap.put("INTERFACE_STATUS",resultCode);
				
			}else{
				if(logger.isDebugEnabled())
					logger.debug("Not a valid MSISDN"+msisdn+ "error code  "+resultCode);
				
				_requestMap.put("INTERFACE_STATUS",resultCode);
				throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);	
			}
		
			if(endTime==0) endTime=System.currentTimeMillis();
			endTime=System.currentTimeMillis();
			_requestMap.put("IN_END_TIME",String.valueOf(endTime));
			if(logger.isDebugEnabled())
				logger.debug("Entering CRMWebServiceDebitINHandler for msisdn " +msisdn +" , _interfaceID:-"+_interfaceid+" , resultCode="+resultCode +" , Total Time:"+Long.toString(endTime-startTime));
			return _requestMap;	
		}catch (Exception e) {
			if(endTime==0) endTime=System.currentTimeMillis();
			endTime=System.currentTimeMillis();
			_requestMap.put("IN_END_TIME",String.valueOf(endTime));
		
			if(logger.isDebugEnabled())
				logger.debug("Entering CRMWebServiceDebitINHandler for msisdn " +msisdn +" , _interfaceID:-"+_interfaceid+" , Error ="+InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND+" exception: "+e.getMessage());
			_requestMap.put("INTERFACE_STATUS",InterfaceErrorCodesI.INVALID_RESPONSE);
			throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_RESPONSE);
		}finally{
		
		}

	}

	public String callEAI(String eaiSVCURL,String requestXml, Integer timeOut,String _interfaceid,HashMap _requestMap){


		String responseXML = "";
		String responseString = "";

		HttpURLConnection httpConn = null;
		ByteArrayOutputStream bout =null;
		try{
			URL url = new URL(eaiSVCURL);
			URLConnection connection = url.openConnection();
			httpConn=(HttpURLConnection)connection;
			bout = new ByteArrayOutputStream();

			byte[] buffer = new byte[requestXml.length()];
			buffer = requestXml.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();

			String SOAPAction = "http://Diameter_pilot.hamed.com/DIRECT_DEBITING"  ;
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(timeOut);
			httpConn.setReadTimeout(timeOut);
			OutputStream out = httpConn.getOutputStream();

			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			while ((responseString = in.readLine()) != null) {
				responseXML = responseXML + responseString;
			}

		}
		catch (ProtocolException pe) {
			logger.error("Exception occured during posting request xml over HTTP", pe);
		}catch(IOException ie){
			logger.error("Exception occured during posting request xml", ie);
		}catch (Exception e) {
			logger.error("Exception while posting request xml to EAI", e);
		}finally{
			if(httpConn!=null){
				httpConn.disconnect();
			}
			if(bout!=null){
				try {
					bout.close();
				} catch (IOException e) {
					logger.error("Exception occured while closing Output Stream ",e);
				}
			}
		}
		logger.error(" Request xml to EAI:"+requestXml+" Response XML from EAI "+responseXML+" Request URL: "+(String)_requestMap.get("DEBIT_USERURL") );
		return responseXML;

	}


}
