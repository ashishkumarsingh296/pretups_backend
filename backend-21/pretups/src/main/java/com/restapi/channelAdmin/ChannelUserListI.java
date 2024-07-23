package com.restapi.channelAdmin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.channelAdmin.requestVO.BulkSusResCURequestVO;


@Service
public interface ChannelUserListI {

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param responseSwag
	 * @param requestVO
	 * @param searchType
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws java.sql.SQLException 
	 */

	public ChannelUserListResponseVO getChannelUserListByAdmin(Connection con, String loginID,
			HttpServletResponse responseSwag, ChannelUserListRequestVO requestVO, String searchType)
			throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param response1
	 * @param requestVO
	 * @param type
	 * @param locale
	 * @param userVO
	 * @return
	 */
	public BulkSusResCUResponseVO processBulkSusResCU(Connection con, HttpServletResponse response1,
			BulkSusResCURequestVO requestVO, String type, Locale locale, UserVO userVO);
	
	

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param responseSwag
	 * @param requestVO
	 * @param searchType
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws java.sql.SQLException 
	 */

	public ChannelUserListByParentResponseVO getChannelUserListByParent(Connection con, String loginID,
			HttpServletResponse responseSwag, ChannelUserListByParntReqVO requestVO)
			throws BTSLBaseException, SQLException;
	
	
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param responseSwag
	 * @param requestVO
	 * @param searchType
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws java.sql.SQLException 
	 */
	public StaffUserListByParentResponseVO getstaffUserListByParent(Connection con, String loginID,
			HttpServletResponse responseSwag, StaffUserListByParntReqVO requestVO)
			throws BTSLBaseException, SQLException;
	
	
	/**
	 * 
	 * @param con
	 * @param geographyLoc
	 * @param networkCode
	 * @param userGrade
	 * @param categoryCode
	 * @param responseSwag
	 * @return CommissionProfileResponseVO
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws java.sql.SQLException 
	 */
	public CommissionProfileResponseVO commissionProfileBy(Connection con, String geographyLoc, String networkCode, String userGrade,String categoryCode
			) throws BTSLBaseException, SQLException;
	
	
	
	
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param networkCode
	 * @param userGrade
	 * @param categoryCode
	 * @param responseSwag
	 * @param requestVO
	 * @return CommissionProfileResponseVO
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws java.sql.SQLException 
	 */
	public ChannelUserListResponseVO getChannelUserListByAdmin2(Connection con, String loginID,
			HttpServletResponse responseSwag, ChannelUserListRequestVO requestVO, String searchType)
			throws BTSLBaseException, SQLException;
}