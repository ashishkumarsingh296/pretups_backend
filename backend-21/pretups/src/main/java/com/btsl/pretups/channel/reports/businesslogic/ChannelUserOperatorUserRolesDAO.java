package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.expression.ParseException;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelUserOperatorUserRolesVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public class ChannelUserOperatorUserRolesDAO {
	ChannelUserOperatorUserRolesQuery channelUserOperatorUserRolesQuery;
	ChannelTransferVO channelTransferVO;
	ChannelUserOperatorUserRolesVO channelUserOperatorUserRolesVO;
	BTSLUtil btslUtil = new BTSLUtil();
	private Log forlog = LogFactory.getLog(this.getClass().getName());
	
	
	/**
	 * ChannelUserOperatorUserRolesDAO()
	 */
	public ChannelUserOperatorUserRolesDAO() {
		channelUserOperatorUserRolesQuery = (ChannelUserOperatorUserRolesQuery) ObjectProducer
				.getObject(QueryConstants.EXTERNAL_USR_REPORT_QRY,
						QueryConstants.QUERY_PRODUCER);
	}

	

	

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	public List<ChannelUserOperatorUserRolesVO> loadExternalUserRolesReport(
			Connection con, UsersReportModel usersReportModel)
			throws  SQLException {

		if (forlog.isDebugEnabled()) {
			forlog.debug("loado2cTransferDetailsReport", "ENTERED");
		}

		final String methodName = "loadUserBalanceReport";
		ArrayList<ChannelUserOperatorUserRolesVO> reportList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
		if(pstmt !=null)
		{
			pstmt = channelUserOperatorUserRolesQuery.loadExternalUserRolesOperatorReportQry(
			usersReportModel, con);
			rs = pstmt.executeQuery();
			while (rs.next()) {

				channelUserOperatorUserRolesVO = new ChannelUserOperatorUserRolesVO();

				
				channelUserOperatorUserRolesVO.setParentName(rs.getString("parent_name"));
				channelUserOperatorUserRolesVO.setParentMsisdn(rs.getString("parent_msisdn"));
				channelUserOperatorUserRolesVO.setOwnerName(rs.getString("owner_name"));
				channelUserOperatorUserRolesVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
				channelUserOperatorUserRolesVO.setCatCode(rs.getString("category_code"));
				channelUserOperatorUserRolesVO.setStatus(rs.getString("status"));
				channelUserOperatorUserRolesVO.setUserName(rs.getString("user_name"));
				channelUserOperatorUserRolesVO.setLoginId(rs.getString("login_id"));
				channelUserOperatorUserRolesVO.setMsisdn(rs.getString("msisdn"));
				channelUserOperatorUserRolesVO.setCatName(rs.getString("category_name"));
				channelUserOperatorUserRolesVO.setDomainName(rs.getString("domain_name"));
				channelUserOperatorUserRolesVO.setGrphCode(rs.getString("grph_domain_code"));
				
				channelUserOperatorUserRolesVO.setRoleType(rs.getString("roletype"));
				channelUserOperatorUserRolesVO.setRoleName(rs.getString("role_name"));
				
				reportList.add(channelUserOperatorUserRolesVO);
				
				
				
				
				
				
				
				
			}
			}
		} catch (SQLException e) {

			forlog.errorTrace(methodName, e);
		}finally {
	        try {
	            if (rs != null) {
	            	rs.close();
	            }
	        } catch (Exception e) {
	        	forlog.errorTrace(methodName, e);
	        }
	        try {
	            if (pstmt != null) {
	            	pstmt.close();
	            }
	        } catch (Exception e) {
	        	forlog.errorTrace(methodName, e);
	        }
		}
		return reportList;
	}
	
	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	public List<ChannelUserOperatorUserRolesVO> loadExternalUserRolesChannelReport(
			Connection con, UsersReportModel usersReportModel)
			throws SQLException {

		if (forlog.isDebugEnabled()) {
			forlog.debug("loado2cTransferDetailsReport", "ENTERED");
		}

		final String methodName = "loadUserBalanceReport";
		ArrayList<ChannelUserOperatorUserRolesVO> reportList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null ;
		
		try {
			pstmt = channelUserOperatorUserRolesQuery.loadExternalUserRolesChannelReportQry(
					usersReportModel, con);
		if(pstmt !=null)
		{
			rs = pstmt.executeQuery();
			while (rs.next()) {

				channelUserOperatorUserRolesVO = new ChannelUserOperatorUserRolesVO();

				
				
				channelUserOperatorUserRolesVO.setCatCode(rs.getString("category_code"));
				channelUserOperatorUserRolesVO.setStatus(rs.getString("status"));
				channelUserOperatorUserRolesVO.setUserName(rs.getString("user_name"));
				channelUserOperatorUserRolesVO.setLoginId(rs.getString("login_id"));
				channelUserOperatorUserRolesVO.setMsisdn(rs.getString("msisdn"));
				channelUserOperatorUserRolesVO.setCatName(rs.getString("category_name"));
				channelUserOperatorUserRolesVO.setDomainName(rs.getString("domain_name"));
				channelUserOperatorUserRolesVO.setGrphCode(rs.getString("grph_domain_code"));
				
				channelUserOperatorUserRolesVO.setRoleType(rs.getString("roletype"));
				channelUserOperatorUserRolesVO.setRoleName(rs.getString("role_name"));
				
				reportList.add(channelUserOperatorUserRolesVO);
				
				
				
				
				
				
				
				
			}
			}
		} catch (SQLException e) {

			forlog.errorTrace(methodName, e);
		}finally {
	        try {
	            if (rs != null) {
	            	rs.close();
	            }
	        } catch (Exception e) {
	        	forlog.errorTrace(methodName, e);
	        }
	        try {
	            if (pstmt != null) {
	            	pstmt.close();
	            }
	        } catch (Exception e) {
	        	forlog.errorTrace(methodName, e);
	        }
		}
		return reportList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
