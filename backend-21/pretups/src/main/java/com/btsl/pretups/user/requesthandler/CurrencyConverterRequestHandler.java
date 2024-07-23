
package com.btsl.pretups.user.requesthandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionDAO;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.Constants;

/**
 * @author akanksha.gupta
 * 04-Aug-2016 1:51:05 pm
 * akanksha.gupta
 *
 */
public class CurrencyConverterRequestHandler  implements ServiceKeywordControllerI {

	  private static Log _log = LogFactory.getLog(CurrencyConverterRequestHandler.class.getName());

	/* (non-Javadoc)
	 * @see com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(com.btsl.pretups.receiver.RequestVO)
	 */
	@Override
	public void process(RequestVO p_requestVO) {
		Connection con = null;MComConnectionI mcomCon = null;
		CurrencyConversionDAO currencyConversionDAO = null;
		CurrencyConversionVO currencyConversionVO = null;
		URL url = null;
		HttpURLConnection urlConnection  = null;
		InputStreamReader in =null;
		BufferedReader br=null;
		
		PrintWriter out = null;
		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, " Entered " + p_requestVO);
		}
		try{
			final String messageArr[] = p_requestVO.getRequestMessageArray();

			mcomCon = new MComConnection();con=mcomCon.getConnection();

			if (messageArr.length>= 5 && (messageArr.length%4 == 1)) {

				
				  HashMap map = p_requestVO.getRequestMap();
	                if (map == null) {
	                    map = new HashMap();
	                }
	            	List<CurrencyConversionVO> list = new ArrayList<>();
	             int messageLength = messageArr.length;
				for(int i =1 ;i< messageLength;i=i+4)
				{
					currencyConversionVO= new CurrencyConversionVO();
					currencyConversionVO.setSourceCurrencyCode(messageArr[i]);
					currencyConversionVO.setTargetCurrencyCode(messageArr[i+1]);
					currencyConversionVO.setCountry(messageArr[i+2]);
					currencyConversionVO.setConversion(Long.parseLong(messageArr[i+3]));
					currencyConversionVO.setExternalRefNumber(p_requestVO.getExternalReferenceNum());
					list.add(currencyConversionVO);
				}
				currencyConversionDAO= new CurrencyConversionDAO();
				int[] count =	currencyConversionDAO.updateCurrencyConversionRate(con,list);
			
				map.put("CURRENCYUPDATEOUTPUT", list);
				map.put("TYPE", "CURRENCYCONVERSIONRESP");
				map.put("EXTREFNUM", p_requestVO.getExternalReferenceNum());
				map.put("TXNSTATUS", PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				map.put("MESSAGE", PretupsErrorCodesI.CURRENCY_RECORD_EXECUTED_SUCCESFULLY);
				p_requestVO.setMessageCode(PretupsErrorCodesI.CURRENCY_RECORD_EXECUTED_SUCCESFULLY);
				p_requestVO.setResponseMap(map);
				try{
					
			    String updateid=Constants.getProperty("INSTANCE_ID");
                String actionPath=p_requestVO.getInfo1()+"/UpdateCacheServlet?updateid="+updateid+"&fromWeb=WEB&cacheParam=50";
                 url=new URL(actionPath);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.addRequestProperty("Content-Type", "text/xml");
                urlConnection.setRequestMethod("POST");
                 out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream())),true);                              
                out.println(actionPath);
                out.flush();
                StringBuilder buffer = new StringBuilder();
                String respStr = "";
                 in = new InputStreamReader(urlConnection.getInputStream());
                 br = new BufferedReader(in);
                while ((respStr = br.readLine()) != null)
                {
                      buffer.append(respStr);
                }
                if (_log.isDebugEnabled()) {
                      _log.debug("CurrencyConverterRequestHandler#process", "Cache Updated Successfully ");
                }                             
				}
				catch(Exception e)
				{
					_log.error(METHOD_NAME,  "Cache Updated during currency conversion process"+ e.getMessage());
		        	_log.errorTrace(METHOD_NAME, e);
				}
			}
			else {
				throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT);
				
			}


		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
					con.rollback();
				}
				
			} catch (Exception ee) {
				_log.errorTrace(METHOD_NAME, ee);
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
			_log.errorTrace(METHOD_NAME, be);
			if (be.isKey()) {
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				return;
			}
		}catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception ee) {
				_log.errorTrace(METHOD_NAME, ee);
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CurrencyConverterRequestHandler[process]", "", "", "",
					"Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			return;
		} finally {
			try {
				if(mcomCon != null)
				{
					mcomCon.close("CurrencyConverterRequestHandler#process");
					mcomCon=null;
					}

				if(in!=null)
					in.close();
				if(br!=null)
					br.close();
				url =null;
				if(urlConnection!=null)
					urlConnection.disconnect();


			} catch (Exception e) {
					_log.errorTrace(METHOD_NAME, e);
				}
				
				
				
			
			
			if(out!=null)
				out.close();
				
			if (_log.isDebugEnabled()) {
				_log.debug("process", " Exited ");
			}
		}
	}
	

}
