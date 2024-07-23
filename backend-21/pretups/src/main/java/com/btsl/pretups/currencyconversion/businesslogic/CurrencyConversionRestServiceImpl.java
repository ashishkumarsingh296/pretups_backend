package com.btsl.pretups.currencyconversion.businesslogic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/*
 * This class implements BarredUserRestService and provides methods for
 * processing Bar User request 
 */
public class CurrencyConversionRestServiceImpl implements CurrencyConversionRestService {

	public static final Log _log = LogFactory.getLog(CurrencyConversionRestServiceImpl.class.getName());
	private HttpURLConnection _urlConnection=null;

	/**
	 * Load Currency details
	 *
	 * @param requestData
	 *            The request data in the form of JSON String
	 * @return response The PretupsResponse object having status of request and
	 *         different types of messages
	 * @throws BTSLBaseException,
	 *             IOException, Exception
	 * @throws SQLException
	 * @throws SAXException
	 * @throws ValidatorException
	 */
	@Override
	public PretupsResponse<List<CurrencyConversionVO>> loadDetails(String requestData)
			throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException {
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionRestServiceImpl#loadDetails", "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<List<CurrencyConversionVO>> response = new PretupsResponse<List<CurrencyConversionVO>>();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});
			if (_log.isDebugEnabled()) {
				_log.debug("Data Object is", dataObject);
			}
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			List<CurrencyConversionVO> currencyConversionDataList = new CurrencyConversionDAO().loadCurrencyConversionDetailsList(con);

			if (currencyConversionDataList.isEmpty()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFormError("currencyconversion.loadcurrencylist.notexists");
				response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_VIEW_BAR_USER_NOT_EXIST);
				return response;
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, currencyConversionDataList);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("CurrencyConversionRestServiceImpl#loadDetails");
				mcomCon = null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionRestServiceImpl#loadDetails", "Exiting");
		}
		return response;
}

@Override
	public PretupsResponse<List<CurrencyConversionVO>> updateDetails(String requestData)
			throws BTSLBaseException, IOException, SQLException, ValidatorException, SAXException {
	    final String methodName="updateDetails";
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionRestServiceImpl#updateDetails", "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		URL url=null;
		PrintWriter out=null;
		BufferedReader in=null;
		PretupsResponse<List<CurrencyConversionVO>> response = new PretupsResponse<List<CurrencyConversionVO>>();
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
					});
			if (_log.isDebugEnabled()) {
				_log.debug("Data Object is", dataObject);
			}
			
			CurrencyConversionVO currencyVO = (CurrencyConversionVO) PretupsRestUtil
					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<CurrencyConversionVO>() {
					});
			String userID = (String) dataObject.get("userID").textValue();
			Integer updateCount = new CurrencyConversionDAO().updateCurrencyDetails(con,currencyVO,userID);

			if (updateCount > 0) {
				mcomCon.finalCommit();
				List<CurrencyConversionVO> currencyConversionDataList = new CurrencyConversionDAO().loadCurrencyConversionDetailsList(con);
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, currencyConversionDataList);
				try{
					String contextURL=Constants.getProperty("CURRENT_APPLICATION_CONTEXT_URL");
					String updateid=Constants.getProperty("INSTANCE_ID");
					String actionPath=contextURL+"/UpdateCacheServlet?updateid="+updateid+"&fromWeb=WEB&cacheParam=50";
					url=new URL(actionPath);
					_urlConnection=(HttpURLConnection)url.openConnection();
					_urlConnection.setConnectTimeout(10000);
					_urlConnection.setReadTimeout(10000);
					_urlConnection.setDoOutput(true);
					_urlConnection.setDoInput(true);
					_urlConnection.addRequestProperty("Content-Type", "text/xml");
					_urlConnection.setRequestMethod("POST");
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())),true);					
					out.println(actionPath);
					out.flush();
					StringBuffer buffer = new StringBuffer();
					String respStr = "";
					in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
					while ((respStr = in.readLine()) != null)
					{
						buffer.append(respStr);
					}
					if (_log.isDebugEnabled()) {
						_log.debug("CurrencyConversionRestServiceImpl#updateDetails", "Cache Updated Successfully ");
					}					
				}
				catch(Exception e){
					_log.error(methodName, "SQLException " + e);
				    _log.errorTrace(methodName,e);
					if (_log.isDebugEnabled()) {
						_log.debug("CurrencyConversionRestServiceImpl#updateDetails", "Cache Updation failed ");
					}
				}
				return response;
			}
			else
			{
				mcomCon.finalRollback();
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setFormError("currencyconversion.loadcurrencylist.notexists");
				response.setMessageCode(PretupsErrorCodesI.CURRENCY_CONVERSION_LIST_NOT_EXIST);
			}
			
			
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "currencyconversion.update.currency.success");
			response.setMessageCode(PretupsErrorCodesI.CURRENCY_CONVERSION_SUCCESS);
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, currencyVO.getmDataList());

		} finally {
			if (mcomCon != null) {
				mcomCon.close("CurrencyConversionRestServiceImpl#updateDetails");
				mcomCon = null;
			}
			if(out!=null){
				out.close();
			}
			if(in!=null){
				in.close();
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionRestServiceImpl#updateDetails", "Exiting");
		}
		return response;
	}
}
