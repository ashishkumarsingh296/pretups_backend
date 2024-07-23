package com.restapi.channelAdmin.service;

import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.OAuthUser;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalListRequestVO;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalRequestVO;
import com.restapi.channelAdmin.responseVO.O2CTransferDetailsResponseVO;
import com.restapi.channelAdmin.responseVO.O2CTxnReversalListResponseVO;
import com.restapi.channelAdmin.responseVO.ParentCategoryListResponseVO;

@Service
public interface O2cTxnReversalService {

	/**
	 * 
	 * @param requestVO
	 * @param response
	 * @param responseSwag
	 * @param oAuthUserData
	 * @param locale
	 * @param searchBy
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public void getO2CTxnReversalList(O2CTxnReversalListRequestVO requestVO,
			O2CTxnReversalListResponseVO response, HttpServletResponse responseSwag, OAuthUser oAuthUserData, Locale locale, String searchBy)
			throws BTSLBaseException, Exception;

	/**
	 * 
	 * @param headers
	 * @param transactionID
	 * @param responseSwag
	 * @return
	 */
	public O2CTransferDetailsResponseVO enquiryDetail(MultiValueMap<String, String> headers, String transactionID, HttpServletResponse responseSwag);
	
	/**
	 * 
	 * @param headers
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 */

	/**
	 * 
	 * @param headers
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 */
	public BaseResponse reverseO2CTxn(MultiValueMap<String, String> headers, O2CTxnReversalRequestVO requestVO,
			HttpServletResponse responseSwag);

	/**
	 * 
	 * @param response
	 * @param responseSwag
	 * @param oAuthUserData
	 * @param locale
	 * @param categoryCode
	 * @throws Exception 
	 */
	public void getParentCategoryList(ParentCategoryListResponseVO response, HttpServletResponse responseSwag,
			OAuthUser oAuthUserData, Locale locale, String categoryCode) throws Exception;


}
