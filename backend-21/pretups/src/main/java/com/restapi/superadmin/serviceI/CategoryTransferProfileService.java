package com.restapi.superadmin.serviceI;

import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.OAuthUser;
import com.restapi.superadmin.requestVO.CatTrfProfileRequestVO;
import com.restapi.superadmin.responseVO.CatTrfProfileListResponseVO;
import com.restapi.superadmin.responseVO.DomainManagmentResponseVO;

public interface CategoryTransferProfileService {

	/**
	 * @author sarthak.saini
	 * @param oAuthUser
	 * @param responseSwag
	 * @param locale
	 * @param domainCode
	 * @param categoryCode
	 * @param networkCode 
	 * @return
	 * @throws SQLException
	 */
	public CatTrfProfileListResponseVO getCatTrfProfileList(OAuthUser oAuthUser, HttpServletResponse responseSwag,
			Locale locale, String domainCode, String categoryCode, String networkCode) throws SQLException;

	/**
	 * @author sarthak.saini
	 * @param locale
	 * @param requestVO
	 * @param responseSwag
	 * @param oAuthUser
	 * @return
	 */
	public BaseResponse addCatTrfProfile(Locale locale, CatTrfProfileRequestVO requestVO,
			HttpServletResponse responseSwag, OAuthUser oAuthUser)throws SQLException;

	/**
	 * @author sarthak.saini
	 * @param locale
	 * @param requestVO
	 * @param responseSwag
	 * @param oAuthUser
	 * @return
	 * @throws SQLException
	 */
	public BaseResponse modifyCatTrfProfile(Locale locale, CatTrfProfileRequestVO requestVO,
			HttpServletResponse responseSwag, OAuthUser oAuthUser)throws SQLException;
	/**
	 * @author sarthak.saini
	 * @param locale
	 * @param responseSwag
	 * @param oAuthUser
	 * @param profileId
	 * @return
	 * @throws SQLException
	 */
	public BaseResponse deleteCatTrfProfile(Locale locale, HttpServletResponse responseSwag, OAuthUser oAuthUser,
			String profileId)throws SQLException;

	/**
	 * 
	 * @param locale
	 * @param responseSwag
	 * @param oAuthUser
	 * @return
	 */
	public DomainManagmentResponseVO getdomainManagmentList(Locale locale, HttpServletResponse responseSwag,
			OAuthUser oAuthUser, String domainType) throws SQLException;
	
	

}
