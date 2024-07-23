package com.inter.claroca.multicurrency;


import java.io.BufferedReader;
import java.io.BufferedWriter;
/**
 * @(#)MultiCurrency.java
 *                                         Copyright(c) 2008, Bharti Telesoft
 *                                         Int. Public Ltd.
 *                                         All Rights Reserved
 *                                         This class is used to Fetch current
 *                                         conversion rates from third party
 *                                         and update them in pretups
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Author Date History
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         -----------------------
 *                                         Zeeshan Aleem, DEC 3,2016 Initial
 *                                         Creation
 *                                         ------------------------------------
 *                                         --
 *                                         ------------------------------------
 *                                         ----------------------
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.axis.client.Stub;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.btsl.pretups.logging.MultiCurrencyLog;
import java.net.MalformedURLException;

public class MultiCurrency {
	private static Log _log = LogFactory.getLog(MultiCurrency.class.getName());
	private static final QName SERVICE_NAME = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZWS_TIPO_CAMBIO");
	static ZWSTIPOCAMBIO  port=null;
	static ZWSTIPOCAMBIO_Service ss=null;
	private static Properties _multiCurrencyProperties = new Properties();

	private static HttpURLConnection _urlConnection = null;
	/**
	 * This method loads the configuration files and calls the process() method.
	 */
	public static void main(String[] args) {
		final String METHOD_NAME = "main";
		try {
			if (args.length != 3) {
				_log.info(METHOD_NAME, "Usage : MultiCurrency [Constants file] [LogConfig file] [WSDL PATH}");
				return;
			}
			final File constantsFile = new File(args[0]);
			if (!constantsFile.exists()) {
				_log.info(METHOD_NAME, " Constants File Not Found .............");
				return;
			}
			final File logconfigFile = new File(args[1]);
			if (!logconfigFile.exists()) {
				_log.info(METHOD_NAME, " Logconfig File Not Found .............");
				return;
			}
			final File multiCurrencyFile = new File(args[2]);
			if (!logconfigFile.exists()) {
				_log.info(METHOD_NAME, " Multicurrency Config File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
			_multiCurrencyProperties.load(new FileInputStream(multiCurrencyFile));

		} catch (Exception ex) {
			_log.errorTrace(METHOD_NAME, ex);
			ConfigServlet.destroyProcessCache();
			return;
		}
		try {
			process();
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " " + e.getMessage());
			}
			_log.errorTrace(METHOD_NAME, e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.info(METHOD_NAME, "Exiting");
			}
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			ConfigServlet.destroyProcessCache();
		}
	}// end main

	/**
	 * This method which checks the network stock and send alerts.
	 */
	private static void process() throws BTSLBaseException {
		Connection con = null;
		ArrayList multiCurrencyList = null;
		ArrayList conversionList=new ArrayList();
		final String METHOD_NAME = "process";
		try {
			_log.debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
			con = OracleUtil.getSingleConnection();
			if (con == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, " DATABASE Connection is NULL ");
				}
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultiCurrency[process]", "", "",
						"", "DATABASE Connection is NULL");
				return;
			}

			try{

				multiCurrencyList = populateCurrencyData(con);
				MultiCurrencyLog.log(multiCurrencyList);

				for(int count=0;count<multiCurrencyList.size();count++){
                                fetchDatafromThirdParty(conversionList,((CurrencyConversionVO)(multiCurrencyList.get(count))).getSourceCurrencyCode(),((CurrencyConversionVO)(multiCurrencyList.get(count))).getTargetCurrencyCode(),((CurrencyConversionVO)(multiCurrencyList.get(count))).getCountry());
				}

				int updateStatus=updateDatainDB(con,conversionList);
				if(updateStatus>0){
					MultiCurrencyLog.log("After Update : ",conversionList);
				}

			}
			catch(Exception e){
				_log.errorTrace(METHOD_NAME, e);
			}



		}// end of try
		catch (BTSLBaseException be) {
			_log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
			throw be;
		} catch (Exception e) {
			_log.error(METHOD_NAME, "Exception : " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MultiCurrency[process]", "", "", "",
					" MultiCurrency process could not be executed successfully.");
			throw new BTSLBaseException("MultiCurrency", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception ex) {
				if (_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, "Exception closing connection ");
				}
				_log.errorTrace(METHOD_NAME, ex);
			}
			_log.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Exiting..... ");
			}
		}
	}

	/**
	 * This method loads the Currency list .
	 *
	 * @param p_con
	 *            Connection
	 * @return ArrayList
	 */
	private static ArrayList populateCurrencyData(Connection p_con) throws BTSLBaseException {
		final String METHOD_NAME = "populateCurrencyData";
		if (_log.isDebugEnabled()) {
			_log.debug("populateCurrencyData", "Entered");
		}
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		final StringBuffer strBuff = new StringBuffer();
		ArrayList currencyList = null;
		CurrencyConversionVO currencyVO=null;

		strBuff.append("SELECT SOURCE_CURRENCY_CODE,TARGET_CURRENCY_CODE,SOURCE_CURRENCY_NAME,TARGET_CURRENCY_NAME,COUNTRY,");
		strBuff.append(" CONVERSION,MULT_FACTOR,DESCRIPTION,REFERENCE_ID");
		strBuff.append(" FROM CURRENCY_CONVERSION_MAPPING ");

		final String query = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug("populateCurrencyData", "QUERY query=" + query);
		}
		try {
			currencyList = new ArrayList();
			pstmtSelect = p_con.prepareStatement(query);
			rs = pstmtSelect.executeQuery();
			while (rs.next()) {
				currencyVO = new CurrencyConversionVO();
				currencyVO.setSourceCurrencyCode(rs.getString("SOURCE_CURRENCY_CODE"));
				currencyVO.setTargetCurrencyCode(rs.getString("TARGET_CURRENCY_CODE"));
				currencyVO.setSourceCurrencyName(rs.getString("SOURCE_CURRENCY_NAME"));
				currencyVO.setTargetCurrencyName(rs.getString("TARGET_CURRENCY_NAME"));
				currencyVO.setCountry(rs.getString("COUNTRY"));
				currencyVO.setConversion(rs.getLong("CONVERSION"));
				currencyVO.setMultFactor(rs.getLong("MULT_FACTOR"));
				currencyVO.setDescription(rs.getString("DESCRIPTION"));
				currencyVO.setExternalRefNumber(rs.getString("REFERENCE_ID"));
				currencyList.add(currencyVO);
			}
		} catch (SQLException sqle) {
			_log.error("populateCurrencyData", "SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"MultiCurrency[populateCurrencyData]", "", "", "", "SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException("MultiCurrency", "populateCurrencyData", "error.general.sql.processing");
		} catch (Exception e) {
			_log.error("populateCurrencyData", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"MultiCurrency[populateCurrencyData]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException("MultiCurrency", "populateCurrencyData", "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("populateCurrencyData", "Exiting Currency List size=" + currencyList.size());
			}
		}
		return currencyList;
	}


	/**
	 * This method loads the Currency Conversion from Third Party .
	 *
	 * @param p_con
	 *            Connection
	 * @return ArrayList
	 */
        private static void fetchDatafromThirdParty(ArrayList updatedCurrList,String sourceCurrency,String targetCurrency,String country) throws BTSLBaseException {
		final String METHOD_NAME="fetchDatafromThirdParty";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered :: ");
		}
		String fromCurrency=null;
		String toCurrency=null;
		try{
			URL wsdlURL = ZWSTIPOCAMBIO_Service.WSDL_LOCATION;
                              if (_multiCurrencyProperties.getProperty("END_POINT") != null && ! "".equals(_multiCurrencyProperties.getProperty("END_POINT"))) {
                                File wsdlFile = new File(_multiCurrencyProperties.getProperty("END_POINT"));
                                try {
                                        if (wsdlFile.exists()) {
                                                wsdlURL = wsdlFile.toURI().toURL();
                                        } else {
                                                wsdlURL = new URL(_multiCurrencyProperties.getProperty("END_POINT"));
                                        }
                                } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                }
                        }

			fromCurrency=sourceCurrency;
			toCurrency=targetCurrency;
			try{ ss = new ZWSTIPOCAMBIO_Service(wsdlURL, SERVICE_NAME);
			port = ss.getZWSTIPOCAMBIO();

			//INTERFAZ_WS dXZ90g8hyH5$


			//System.out.println("Invoking ztipoDeCambio...");
			//System.out.println("From Currency : "+fromCurrency+" To Currency : "+toCurrency);
			java.math.BigDecimal _ztipoDeCambio__return = port.ztipoDeCambio(fromCurrency,"","M", toCurrency);
			//System.out.println("From Currency : "+fromCurrency+" To Currency : "+toCurrency+" Conversion Rate = "+ _ztipoDeCambio__return);
                        updatedCurrList.add(fromCurrency+":"+toCurrency+":"+_ztipoDeCambio__return+":"+country);
                        MultiCurrencyLog.log(fromCurrency+":"+toCurrency+":"+_ztipoDeCambio__return+":"+country);
			}
			catch (Exception e)
			{
				System.out.println("Inside Exception ");
				e.printStackTrace();
			}
			finally{
				try {System.out.println("Request : "    +((Stub) port)._createCall().getMessageContext().getRequestMessage());}catch (Exception e){}
				try {System.out.println("Response : "   +((Stub) port)._createCall().getMessageContext().getResponseMessage());}catch (Exception e){}
			}

		}
		catch(Exception e){
			_log.errorTrace(METHOD_NAME,e);
		}

	}


	private static int updateDatainDB(Connection p_con,ArrayList list) throws BTSLBaseException {

		final String METHOD_NAME="updateDatainDB";
		String url;
		String receiver;            	
		String gatewayCode;
		String gatewayType;
		String servicePort;
		String sourceType;
		String network;
		String login;
		String password;
		String source;
		String target;
		String conversion;
		String country;
		String httpURLPrefix="http://";
		URL httpurl = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String responseStr = null;
		String requestMSG="";
		StringBuffer buffer = new StringBuffer();
                String respStr = "";
		url=_multiCurrencyProperties.getProperty("URL");
		receiver=_multiCurrencyProperties.getProperty("RECEIVER");
		gatewayCode=_multiCurrencyProperties.getProperty("GATEWAY_CODE");
		gatewayType=_multiCurrencyProperties.getProperty("GATEWAY_TYPE");
		servicePort=_multiCurrencyProperties.getProperty("SERVICE_PORT");
		sourceType=_multiCurrencyProperties.getProperty("SOURCE_TYPE");
		network=_multiCurrencyProperties.getProperty("NETWORK");
		login=_multiCurrencyProperties.getProperty("LOGIN");
		password=_multiCurrencyProperties.getProperty("PASSWORD");

		try{
		String requestURL = httpURLPrefix+url+"/"+receiver+"?REQUEST_GATEWAY_CODE="+gatewayCode+"&REQUEST_GATEWAY_TYPE="+gatewayType+"&LOGIN="+login+"&PASSWORD="+password+"&SOURCE_TYPE="+sourceType+"&SERVICE_PORT="+servicePort;
		requestMSG=requestMSG+"<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XMLCommand1.0//EN\"\"xml/command.dtd\"><COMMAND><TYPE>CURRENCYCONVERSIONREQ</TYPE><DATE>03/12/2016</DATE><EXTNWCODE>"+network+"</EXTNWCODE><EXTREFNUM>85656</EXTREFNUM><CURRENCYRECORDS>";

		for(int count=0;count<list.size();count++){
			source=String.valueOf(list.get(count)).split(":")[0];
			target=String.valueOf(list.get(count)).split(":")[1];
			conversion=String.valueOf(list.get(count)).split(":")[2];
                        //country=_multiCurrencyProperties.getProperty(target);
                        country = String.valueOf(list.get(count)).split(":")[3];
			requestMSG=requestMSG+"<RECORD><SOURCECURRENCY>"+source+"</SOURCECURRENCY><TARGETCURRENCY>"+target+"</TARGETCURRENCY><TARGETCOUNTRYCODE>"+country+"</TARGETCOUNTRYCODE><CONVERSION>"+conversion+"</CONVERSION></RECORD>";
		}
		requestMSG=requestMSG+"</CURRENCYRECORDS></COMMAND>";


		httpurl = new URL(requestURL);
		_urlConnection = (HttpURLConnection)httpurl.openConnection();
		_urlConnection.setConnectTimeout(10000);
		_urlConnection.setReadTimeout(10000);
		_urlConnection.setDoOutput(true);
		_urlConnection.setDoInput(true);
		_urlConnection.addRequestProperty("Content-Type", "text/xml");
		_urlConnection.setRequestMethod("POST");
			}
	catch(Exception e){
			_log.errorTrace(METHOD_NAME, e);
		}	
		try{
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())),true);
			if(_log.isDebugEnabled())_log.debug(METHOD_NAME, "Request sent   =" + requestMSG);
			out.println(requestMSG);
			out.flush();
			in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
			while ((respStr = in.readLine()) != null){
				buffer.append(respStr);
			}
		}catch(Exception e){
			_log.errorTrace(METHOD_NAME, e);
			_log.error(METHOD_NAME, "Exception in reading or writing  e:" + e.getMessage());
		}//end of catch-Exception
		finally{
			try{if(out!=null)out.close();}catch(Exception e){_log.errorTrace(METHOD_NAME,e);}
			try{if(in!=null)in.close();}catch(Exception e){_log.errorTrace(METHOD_NAME, e);}

		}//end of finally
		responseStr = buffer.toString();
		//parseResponse(requestVO1.getMsisdn(),responseStr);
		if(_log.isDebugEnabled())_log.debug(METHOD_NAME,"Response Received   =" + responseStr);
		return 1;
	}
}

