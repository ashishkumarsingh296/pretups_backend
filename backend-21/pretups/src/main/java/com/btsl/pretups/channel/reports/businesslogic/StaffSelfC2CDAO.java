package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public class StaffSelfC2CDAO {
	StaffSelfC2CQuery staffSelfC2CQuery;
	ChannelTransferVO channelTransferVO;
	StaffSelfC2CReportVO staffSelfC2CReportVO;
	BTSLUtil btslUtil = new BTSLUtil();
	private Log forlog = LogFactory.getLog(this.getClass().getName());

	/**
	 * StaffSelfC2CDAO
	 */
	public StaffSelfC2CDAO() {
		staffSelfC2CQuery = (StaffSelfC2CQuery) ObjectProducer.getObject(
				QueryConstants.STAFF_C2C_REPORT_QRY,
				QueryConstants.QUERY_PRODUCER);
	}

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 * @throws SQLException
	 */
	public  List<StaffSelfC2CReportVO> loadStaffSelfReport(
			Connection con, UsersReportModel usersReportModel)
			throws SQLException {

		if (forlog.isDebugEnabled()) {
			forlog.debug("loado2cTransferDetailsReport", "ENTERED");
		}

		final String methodName = "loadUserBalanceReport";
		ArrayList<StaffSelfC2CReportVO> reportList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = staffSelfC2CQuery.loadStaffSelfC2CChannelReportQry(
					usersReportModel, con);
		} catch (Exception e1) {

			forlog.errorTrace(methodName, e1);
		}

		try {
			if (pstmt != null) {
				rs = pstmt.executeQuery();
				while (rs.next()) {

					staffSelfC2CReportVO = new StaffSelfC2CReportVO();

					staffSelfC2CReportVO.setActiveUserId(rs
							.getString("ACTIVE_USER_ID"));
					staffSelfC2CReportVO
							.setToUserId(rs.getString("TO_USER_ID"));
					staffSelfC2CReportVO.setFromUser(rs.getString("FROM_USER"));
					staffSelfC2CReportVO.setToUser(rs.getString("TO_USER"));
					staffSelfC2CReportVO.setTransferID(rs
							.getString("TRANSFER_ID"));
					staffSelfC2CReportVO.setTransferSubType(rs
							.getString("TRANSFER_SUB_TYPE"));
					staffSelfC2CReportVO.setType(rs.getString("TYPE"));
					staffSelfC2CReportVO.setCloseDate(rs
							.getString("CLOSE_DATE"));
					staffSelfC2CReportVO.setProductName(rs
							.getString("PRODUCT_NAME"));
					staffSelfC2CReportVO.setTransferMrp(rs
							.getString("TRANSFER_MRP"));
					staffSelfC2CReportVO.setPayableAmt(rs
							.getString("PAYABLE_AMOUNT"));
					staffSelfC2CReportVO.setNetPayableAmt(rs
							.getString("NET_PAYABLE_AMOUNT"));

					staffSelfC2CReportVO.setStatus(rs.getString("STATUS"));
					staffSelfC2CReportVO.setMrp(rs.getString("MRP"));

					staffSelfC2CReportVO.setCommission(rs
							.getString("COMMISION"));
					staffSelfC2CReportVO.setCommissionQuantity(rs
							.getString("COMMISION_QUANTITY"));

					staffSelfC2CReportVO.setReceiverCreditQuantity(rs
							.getString("RECEIVER_CREDIT_QUANTITY"));
					staffSelfC2CReportVO.setSenderDebitQuantity(rs
							.getString("SENDER_DEBIT_QUANTITY"));
					staffSelfC2CReportVO.setTax3Value(rs
							.getString("TAX3_VALUE"));
					staffSelfC2CReportVO.setTax1Value(rs
							.getString("TAX1_VALUE"));
					staffSelfC2CReportVO.setTax2Value(rs
							.getString("TAX2_VALUE"));

					staffSelfC2CReportVO.setSenderCategoryCode(rs
							.getString("SENDER_CATEGORY_CODE"));
					staffSelfC2CReportVO.setReceiverCategoryCode(rs
							.getString("RECEIVER_CATEGORY_CODE"));

					staffSelfC2CReportVO.setReceiverCategoryName(rs
							.getString("RECEIVER_CATEGORY_NAME"));
					staffSelfC2CReportVO.setSource(rs.getString("SOURCE"));

					reportList.add(staffSelfC2CReportVO);

				}
			}
		} catch (SQLException e) {

			forlog.errorTrace(methodName, e);
		}finally{
			try{
	               if (rs!= null){
	               	rs.close();
	               }
	             }
	             catch (SQLException e){
	            	 forlog.error("An error occurred closing statement.", e);
	             }
	    	   try{
	               if (pstmt!= null){
	            	   pstmt.close();
	               }
	             }
	             catch (SQLException e){
	            	 forlog.error("An error occurred closing statement.", e);
	             }
		}
		return reportList;
	}

}
