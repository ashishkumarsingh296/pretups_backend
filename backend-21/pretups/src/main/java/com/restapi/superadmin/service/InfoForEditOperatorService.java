package com.restapi.superadmin.service;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.restapi.superadmin.AssignGeographyResponseVO;
import com.restapi.superadmin.AssignSevicesResponseVO;
import com.restapi.superadmin.DepartementListResponseVO;
import com.restapi.superadmin.InfoForEditOperatorResponseVO;
import com.restapi.superadmin.SMSCprofileResponseVO;

@Service
public interface InfoForEditOperatorService {

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @param categoryCode
	 * @param userId
	 * @param networkId
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public InfoForEditOperatorResponseVO getInfo(Connection con, String loginId, HttpServletResponse responseSwag,
			String categoryCode, String userId, String networkId) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @param categoryCode
	 * @param userId
	 * @param networkId
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public AssignSevicesResponseVO assignList(Connection con, String loginId, HttpServletResponse responseSwag,
			String categoryCode, String userId, String networkId) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @param categoryCode
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public AssignGeographyResponseVO assignGeography(Connection con, String loginId, HttpServletResponse responseSwag, String categoryCode)
			throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @param categoryCode
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public SMSCprofileResponseVO getSMSCInfo(Connection con, String loginId, HttpServletResponse responseSwag,
			String categoryCode) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @param categoryCode
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public DepartementListResponseVO getDepartement(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException;
	
	
	

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @param categoryCode
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public DepartementListResponseVO getDepartementbyDivID(Connection con, HttpServletResponse responseSwag,String divID)
			throws BTSLBaseException, SQLException; 

}
