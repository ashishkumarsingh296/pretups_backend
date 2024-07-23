package com.restapi.superadmin;

import java.sql.Connection;
import java.sql.SQLException;

import com.restapi.superadmin.responseVO.DivisionManagementResponseVO;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;

@Service
public interface DivisionManagementI {

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public DivisonListResponseVO viewDivisionList(Connection con, String loginId, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param requestVO
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */

	public BaseResponse ModifyDivisionAdmin(Connection con, String loginId, ModifyDivisionRequestVO requestVO,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param divDeptName
	 * @param divDeptShortCode
	 * @param divDeptType
	 * @param status
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public DivisionManagementResponseVO AddDivision(Connection con, String loginId, String divDeptName, String divDeptShortCode,
													String divDeptType, String status, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public DivTypeListResponseVO viewDivDepList(Connection con, HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException;

	public BaseResponse deleteDivision(Connection con, String loginID, String divDeptId, String parentId,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;
	
	public CategoryListRespVO getCategoryList(Connection con, HttpServletResponse responseSwag,String categoryCode)
			throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param loginId
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public DivisonListForDepartmentResponseVO viewDivisionListForDepartment(Connection con, String loginId,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param parentId
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public DivisonListForDepartmentResponseVO departmentListAdmin(Connection con, String loginID, String parentId,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param divDeptId
	 * @param responseSwag
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse deleteDepartment(Connection con, String loginID, String divDeptId,
			HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param parentId
	 * @param divDeptName
	 * @param divDeptShortCode
	 * @param divDeptType
	 * @param status
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse AddDepartment(Connection con, String loginID, String parentId, String divDeptName,
			String divDeptShortCode, String divDeptType, String status, HttpServletResponse response1) throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @param response1
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse ModifyDepartmentAdmin(Connection con, String loginID, ModifyDepartmentRequestVO requestVO,
			HttpServletResponse response1) throws BTSLBaseException, SQLException;
}
