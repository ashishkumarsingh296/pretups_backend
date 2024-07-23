package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.springframework.expression.ParseException;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserZeroBalanceCounterSummaryVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

public class UserZeroBalanceCounterSummaryDAO {

	private UserZeroBalanceCounterSummaryQry userZeroBalanceCounterSummaryQry;

	public UserZeroBalanceCounterSummaryDAO() {
		userZeroBalanceCounterSummaryQry = (UserZeroBalanceCounterSummaryQry) ObjectProducer
				.getObject(QueryConstants.USER_BAL_SUMMARY_REPORT_QRY,
						QueryConstants.QUERY_PRODUCER);
	}



	private UserZeroBalanceCounterSummaryVO userZeroBalanceCounterSummaryVO;


	private Log _log = LogFactory.getLog(this.getClass().getName());

	public ArrayList<UserZeroBalanceCounterSummaryVO> loadUserBalanceReport(
			Connection con, UsersReportModel usersReportModel,
			Timestamp fromDateTimeValue, Timestamp toDateTimeValue)
			throws ParseException, SQLException {

		if (_log.isDebugEnabled()) {
			_log.debug("loado2cTransferDetailsReport", "ENTERED");
		}

		final String METHOD_NAME = "loadUserBalanceReport";
		ArrayList<UserZeroBalanceCounterSummaryVO> reportList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null ;

		try {
			pstmt = userZeroBalanceCounterSummaryQry.loadUserBalanceReportQry(
					usersReportModel, con, fromDateTimeValue, toDateTimeValue);
		} catch (Exception e1) {

			_log.errorTrace(METHOD_NAME, e1);
		}

		try {
if(pstmt !=null)
{
			rs = pstmt.executeQuery();
			while (rs.next()) {

				userZeroBalanceCounterSummaryVO = new UserZeroBalanceCounterSummaryVO();

				userZeroBalanceCounterSummaryVO.setCategoryName(rs
						.getString("category_name"));
				userZeroBalanceCounterSummaryVO.setEntryDate(rs.getString(
						"ENTRY_DATE").toString());
				userZeroBalanceCounterSummaryVO.setMsisdn(rs
						.getString("msisdn"));
				userZeroBalanceCounterSummaryVO.setOwnerMsisdn(rs
						.getString("owner_msisdn"));
				userZeroBalanceCounterSummaryVO.setOwnerName(rs
						.getString("owner_name"));
				userZeroBalanceCounterSummaryVO.setParentMsisdn(rs
						.getString("parent_msisdn"));
				userZeroBalanceCounterSummaryVO.setParentName(rs
						.getString("parent_name"));
				userZeroBalanceCounterSummaryVO.setProductName(rs
						.getString("product_name"));
				userZeroBalanceCounterSummaryVO.setRecordType(rs
						.getString("record_type"));
				userZeroBalanceCounterSummaryVO.setThresholdCount(rs
						.getString("threshold_count"));
				userZeroBalanceCounterSummaryVO.setUserName(rs
						.getString("user_name"));
				userZeroBalanceCounterSummaryVO.setUserStatus(rs
						.getString("user_status"));
				reportList.add(userZeroBalanceCounterSummaryVO);
			}
			}
		} catch (SQLException e) {

			_log.errorTrace(METHOD_NAME, e);
		}finally{
			try{
	               if (rs!= null){
	               	rs.close();
	               }
	             }
	             catch (SQLException e){
	            	 _log.error("An error occurred closing statement.", e);
	             }
			try{
	               if (pstmt!= null){
	            	   pstmt.close();
	               }
	             }
	             catch (SQLException e){
	            	 _log.error("An error occurred closing statement.", e);
	             }
		}
		return reportList;
	}

	public ArrayList<UserZeroBalanceCounterSummaryVO> loadzeroBalSummChannelUserReport(
			Connection con, UsersReportModel usersReportModel,
			Timestamp fromDateTimeValue, Timestamp toDateTimeValue)
			throws ParseException, SQLException {

		if (_log.isDebugEnabled()) {
			_log.debug("loado2cTransferDetailsReport", "ENTERED");
		}

		final String METHOD_NAME = "loadUserBalanceReport";
		ArrayList<UserZeroBalanceCounterSummaryVO> reportList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null ;

		try {
			pstmt = userZeroBalanceCounterSummaryQry
					.loadzeroBalSummChannelUserReportQry(usersReportModel, con,
							fromDateTimeValue, toDateTimeValue);
		} catch (Exception e1) {
			
			_log.errorTrace(METHOD_NAME, e1);
		}

		try {
if(pstmt!=null)
{
			rs = pstmt.executeQuery();
			while (rs.next()) {

				userZeroBalanceCounterSummaryVO = new UserZeroBalanceCounterSummaryVO();

				userZeroBalanceCounterSummaryVO.setCategoryName(rs
						.getString("category_name"));
				userZeroBalanceCounterSummaryVO.setEntryDate(rs.getString(
						"ENTRY_DATE").toString());
				userZeroBalanceCounterSummaryVO.setMsisdn(rs
						.getString("msisdn"));
				userZeroBalanceCounterSummaryVO.setProductName(rs
						.getString("product_name"));
				userZeroBalanceCounterSummaryVO.setRecordType(rs
						.getString("record_type"));
				userZeroBalanceCounterSummaryVO.setThresholdCount(rs
						.getString("threshold_count"));
				userZeroBalanceCounterSummaryVO.setUserName(rs
						.getString("user_name"));
				userZeroBalanceCounterSummaryVO.setUserStatus(rs
						.getString("user_status"));
				reportList.add(userZeroBalanceCounterSummaryVO);
			}
			}
		} catch (SQLException e) {
			_log.errorTrace(METHOD_NAME, e);
		}finally{
			try{
	               if (rs!= null){
	               	rs.close();
	               }
	             }
	             catch (SQLException e){
	            	 _log.error("An error occurred closing statement.", e);
	             }
			try{
	               if (pstmt!= null){
	            	   pstmt.close();
	               }
	             }
	             catch (SQLException e){
	            	 _log.error("An error occurred closing statement.", e);
	             }
		}
		return reportList;
	}

}
