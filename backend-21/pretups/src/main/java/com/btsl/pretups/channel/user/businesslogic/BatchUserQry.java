package com.btsl.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchUserQry {

	/**
	 * @param geographyCode
	 * @param domainCode
	 * @return
	 */
	String loadBatchListForApprovalQry(String[] geographyCode,
			String[] domainCode);

	/**
	 * @return
	 */
	String loadBatchDetailsListQry();

	/**
	 * @return
	 */
	String loadBatchListForEnquiryQry();

	/**
	 * @param geographyCode
	 * @param userType
	 * @return
	 */
	String loadBatchListForEnquiryQry(String[] geographyCode, String userType);

	/**
	 * @return
	 */
	String loadBatchDetailsListForEnqQry();
	
	
	/**
	 * @return
	 */
	String loadBatchDetailsListForEnqTcpQry();
	

	/**
	 * @return
	 */
	String loadBatchDetailsListForEnqSelectServicesQry();

	/**
	 * @return
	 */
	String loadBatchDetailsListForEnqSelectRolesQry();

	/**
	 * @param con
	 * @param categoryCode
	 * @param geographyCode
	 * @param userID
	 * @return
	 * @throws SQLException
	 */
	PreparedStatement loadBatchUserListForModifyQry(Connection con, String categoryCode, String geographyCode, String userID ) throws SQLException;

	/**
	 * @param con
	 * @param category
	 * @param geographicsCode
	 * @param loginUserID
	 * @return
	 * @throws SQLException
	 */
	PreparedStatement loadMasterGeographyForCategoryListQry(Connection con, String category, String geographicsCode,  String loginUserID) throws SQLException;
	
	
	/**
	 * @return
	 */
	String loadGeographyAndDomainDetailsQry();
	
	/**
	 * @param con
	 * @param categoryCode
	 * @param geographyCode
	 * @param userId
	 * @return
	 * @throws SQLException 
	 */
	PreparedStatement loadBatchUserListForProfileAssociateQry(Connection con,String categoryCode, String geographyCode, String userId) throws SQLException;
	
	/**
	 * @param con
	 * @param categoryCode
	 * @param geographyCode
	 * @param userId
	 * @return
	 * @throws SQLException 
	 */
	PreparedStatement loadBatchUserListForProfileAssociateTcpQry(Connection con,String categoryCode, String geographyCode, String userId) throws SQLException;
	
	
	/**
	 * @return
	 */
	String loadCommProfileListQry();

	/**
	 * @param con
	 * @param categoryCode
	 * @param geographyCode
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	PreparedStatement loadBatchUserListForModifyPOIQry(Connection con,String categoryCode, String geographyCode, String userId)throws SQLException;
	
	/**
	 * @return
	 */
	String loadBatchDetailsListForUsrEnqQry();
	
	String loadBatchListForUsrEnquiryQry(String[] geographyCode, String userType,String loggedInUserID);
	
	public String loadBatchListForEnquiryQryUsr();
}
