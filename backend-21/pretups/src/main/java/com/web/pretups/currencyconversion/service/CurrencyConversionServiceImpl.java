package com.web.pretups.currencyconversion.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;

import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;

public class CurrencyConversionServiceImpl implements CurrencyConversionService {

	public static final Log _log = LogFactory.getLog(CurrencyConversionServiceImpl.class.getName());

	/**
	 * Load modules for BAR User
	 * 
	 * @return List The list of lookup filtered from DB
	 * @throws IOException
	 *         
	 */

	@SuppressWarnings("unchecked")
	@Override
	public boolean loadDetails(List<CurrencyConversionVO> currencyConversionDataList) throws IOException {
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionServiceImpl#loadDetails", "Entered ");
		}
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("data", currencyConversionDataList);		
		requestObject.put("type", PretupsI.CURRCONV);
		PretupsRestClient pretupsRestClient = new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.CURRCONV);
		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});

		PretupsResponse<List<CurrencyConversionVO>> responseObject = (PretupsResponse<List<CurrencyConversionVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<CurrencyConversionVO>>>() {
		});
		currencyConversionDataList.addAll(responseObject.getDataObject());
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionServiceImpl#loadDetails", "Exiting");
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean updateCurrencyDetails(CurrencyConversionVO currencyVO,UserVO userVO,BindingResult bindingResult) throws IOException {
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionServiceImpl#updateCurrencyDetails", "Entered ");
		}
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("data", currencyVO);	
		requestObject.put("userID",userVO.getUserID());
		requestObject.put("type", PretupsI.CURRCONVUPD);		
		PretupsRestClient pretupsRestClient = new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.CURRCONVUPD);
		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});

		PretupsResponse<List<CurrencyConversionVO>> responseObject = (PretupsResponse<List<CurrencyConversionVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<CurrencyConversionVO>>>() {
		});		
		currencyVO.setmDataList(responseObject.getDataObject());
		
		if (_log.isDebugEnabled()) {
			_log.debug("CurrencyConversionServiceImpl#updateCurrencyDetails", "Exiting");
		}
		return true;
		
	}
}
