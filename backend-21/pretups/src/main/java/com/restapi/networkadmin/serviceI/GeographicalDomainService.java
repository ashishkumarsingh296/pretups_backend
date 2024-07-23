package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.GeoDomainAddRequestVO;
import com.restapi.networkadmin.requestVO.GeoDomainDeleteRequestVO;
import com.restapi.networkadmin.requestVO.GeoDomainListRequestVO;
import com.restapi.networkadmin.responseVO.GeoDomainTypeListResponseVO;
import com.restapi.networkadmin.responseVO.ParentGeoDomainResponseVO;

public interface GeographicalDomainService {

	/**
	 * @author sarthak.saini
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public GeoDomainTypeListResponseVO loadGeoDomainTypeList(Connection con, Locale locale,
			HttpServletResponse responseSwag) throws SQLException;

	/**
	 * 
	 * @param con
	 * @param locale
	 * @param response1
	 * @param geoDomainType
	 * @return
	 * @throws SQLException
	 */
	public GeoDomainTypeListResponseVO loadParentGeoDomainTypeList(Connection con, Locale locale,
			HttpServletResponse response1, String geoDomainType) throws SQLException;

	/**
	 * 
	 * @param con
	 * @param locale
	 * @param responseSwag
	 * @param userVO
	 * @param requestVO
	 * @param geoDomainType
	 * @return
	 * @throws SQLException
	 */
	public ParentGeoDomainResponseVO loadGeoDomainList(Connection con, Locale locale, HttpServletResponse responseSwag,
			UserVO userVO, GeoDomainListRequestVO requestVO, String geoDomainType) throws SQLException;

	/**
	 * 
	 * @param con
	 * @param locale
	 * @param index
	 * @param indexValue
	 * @param parentValueDes
	 * @param responseSwag
	 * @param userVO
	 * @return
	 * @throws SQLException
	 */
	public ParentGeoDomainResponseVO searchValue(Connection con, Locale locale, String index, String indexValue,
			String parentValueDes, HttpServletResponse responseSwag, UserVO userVO) throws SQLException;

	/**
	 * 
	 * @param con
	 * @param locale
	 * @param responseSwag
	 * @param userVO
	 * @param requestVO
	 * @return
	 * @throws SQLException 
	 */
	public BaseResponse addGeoDomain(Connection con, Locale locale, HttpServletResponse responseSwag, UserVO userVO,
			GeoDomainAddRequestVO requestVO) throws SQLException;

	/**
	 * 
	 * @param con
	 * @param locale
	 * @param responseSwag
	 * @param userVO
	 * @param requestVO
	 * @return
	 * @throws SQLException 
	 */
	public BaseResponse modifyGeoDomain(Connection con, Locale locale, HttpServletResponse responseSwag, UserVO userVO,
			GeoDomainAddRequestVO requestVO) throws SQLException;

	/**
	 * 
	 * @param con
	 * @param locale
	 * @param responseSwag
	 * @param userVO
	 * @param requestVO
	 * @return
	 * @throws SQLException
	 */
	public BaseResponse deleteGeoDomain(Connection con, Locale locale, HttpServletResponse responseSwag, UserVO userVO,
			GeoDomainDeleteRequestVO requestVO) throws SQLException;

}
