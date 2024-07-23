package com.btsl.pretups.channel.reports.businesslogic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
/**
 * 
 * @author yogesh.keshari
 *
 */
public class Channel2ChannelTransferRetWidRptDAO {
	private static Log log = LogFactory.getLog(ChannelTransferDAO.class.getName());
	private Channel2ChannelTransferRetWidRptQry channel2ChannelTransferRetWidRptQry;

	public Channel2ChannelTransferRetWidRptDAO() {
		channel2ChannelTransferRetWidRptQry = (Channel2ChannelTransferRetWidRptQry) ObjectProducer
				.getObject(QueryConstants.CHANNEL_2_CHANNEL_TRANSFER_RET_WD_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws BTSLBaseException
 * @throws ParseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferChannelUserUnionList(Connection conn, UsersReportModel usersReportModel)
			throws BTSLBaseException, ParseException {
		final String methodName = "loadC2cRetWidTransferChannelUserUnionList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {
			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChannelUserUnionListQry(conn,
					usersReportModel);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setSource(rs.getString("SOURCE"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));
				
				transferVO.setMrp(rs.getString("mrp"));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
				transfersList.add(transferVO);

			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;
	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws BTSLBaseException
 * @throws ParseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferChannelUserUnionStaffList(Connection conn, UsersReportModel usersReportModel)
			throws BTSLBaseException, ParseException {
		final String methodName = "loadC2cRetWidTransferChannelUserUnionStaffList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {
			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChannelUserUnionStaffListQry(conn,usersReportModel);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setOwnerProfile(rs.getString("owner_profile"));
				transferVO.setParentProfile(rs.getString("parent_profile"));
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setTransferInitatedByName(rs.getString("initiator_user"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setSource(rs.getString("SOURCE"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));
				transferVO.setMrp(rs.getString("mrp"));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));

				transfersList.add(transferVO);

			}
			rs.close();

		}

		catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;
	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws SQLException
 * @throws ParseException
 * @throws BTSLBaseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferChannelUserList(Connection conn, UsersReportModel usersReportModel)
			throws SQLException, ParseException, BTSLBaseException {
		final String methodName = "loadC2cRetWidTransferChannelUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {
			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChannelUserListQry(conn, usersReportModel);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setOwnerProfile(rs.getString("owner_profile"));
				transferVO.setParentProfile(rs.getString("parent_profile"));
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setMsisdn(rs.getString("from_msisdn"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setToMSISDN(rs.getString("to_msisdn"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setGegoraphyDomainName(rs.getString("GRPH_DOMAIN_NAME"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));				
				transferVO.setMrp(rs.getString("mrp"));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
				transfersList.add(transferVO);
			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}

		return transfersList;
	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws SQLException
 * @throws ParseException
 * @throws BTSLBaseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferChnlUserStaffList(Connection conn, UsersReportModel usersReportModel)
			throws SQLException, ParseException, BTSLBaseException {
		final String methodName = "loadC2cRetWidTransferChnlUserStaffList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {

			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChnlUserStaffListQry(conn, usersReportModel);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setOwnerProfile(rs.getString("owner_profile"));
				transferVO.setParentProfile(rs.getString("parent_profile"));
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setTransferInitatedByName(rs.getString("initiator_user"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));
				transferVO.setMrp(rs.getString("mrp"));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
				transfersList.add(transferVO);
			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;
	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws ParseException
 * @throws BTSLBaseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferUnionList(Connection conn, UsersReportModel usersReportModel)
			throws ParseException, BTSLBaseException {
		final String methodName = "loadC2cRetWidTransferUnionList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {
			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferUnionListQry(conn, usersReportModel);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setMsisdn(rs.getString("from_msisdn"));
				transferVO.setFromUserGeo(rs.getString("from_user_geo"));
				transferVO.setFromOwnerGeo(rs.getString("from_owner_geo"));
			    transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setFromEXTCODE(rs.getString("from_ext_code"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setToMSISDN(rs.getString("to_msisdn"));
				transferVO.setToUserGeo(rs.getString("to_user_geo"));
				transferVO.setToOwnerGeo(rs.getString("to_owner_geo"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				transferVO.setToEXTCODE(rs.getString("to_ext_code"));
				transferVO.setSource(rs.getString("SOURCE"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));
				transferVO.setMrp(rs.getString("mrp"));
				// cast issue fix
				// transferVO.setOtfAmount((long)(rs.getFloat("otf_amount")));
				transferVO.setOtfAmount(BTSLUtil.parseFloatToLong(rs.getFloat("otf_amount")));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
				
				transfersList.add(transferVO);

			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);

			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);

		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}

		return transfersList;
	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws ParseException
 * @throws BTSLBaseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferUnionStaffList(Connection conn, UsersReportModel usersReportModel)
			throws ParseException, BTSLBaseException {
		final String methodName = "loadC2cRetWidTransferUnionStaffList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {
			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferUnionStaffListQry(conn, usersReportModel);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setTransferInitatedByName(rs.getString("initiator_user"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setSource(rs.getString("SOURCE"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));
				transferVO.setMrp(rs.getString("mrp"));
				transferVO.setFromEXTCODE(rs.getString("from_ext_code"));
				transferVO.setToEXTCODE(rs.getString("to_ext_code"));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
				transfersList.add(transferVO);

			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;
	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws ParseException
 * @throws BTSLBaseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferList(Connection conn, UsersReportModel usersReportModel)
			throws ParseException, BTSLBaseException {
		final String methodName = "loadC2cRetWidTransferList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {
			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferListQry(conn, usersReportModel);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setMsisdn(rs.getString("from_msisdn"));
				transferVO.setFromUserGeo(rs.getString("from_user_geo"));
				transferVO.setFromOwnerGeo(rs.getString("from_owner_geo"));
				transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setToMSISDN(rs.getString("to_msisdn"));
				transferVO.setToUserGeo(rs.getString("to_user_geo"));
				transferVO.setToOwnerGeo(rs.getString("to_owner_geo"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				transferVO.setSource(rs.getString("SOURCE"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));
				transferVO.setMrp(rs.getString("mrp"));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
				transfersList.add(transferVO);

			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;

	}
/**
 * 
 * @param conn
 * @param usersReportModel
 * @return
 * @throws ParseException
 * @throws BTSLBaseException
 */
	public ArrayList<ChannelTransferVO> loadC2cRetWidTransferStaffList(Connection conn, UsersReportModel usersReportModel)
			throws ParseException, BTSLBaseException {
		final String methodName = "loadC2cRetWidTransferStaffList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		ArrayList<ChannelTransferVO> transfersList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ChannelTransferVO transferVO = null;
		ResultSet rs = null;
		try {
			pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferStaffListQry(conn, usersReportModel);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferVO = new ChannelTransferVO();
				transferVO.setFromUserName(rs.getString("from_user"));
				transferVO.setToUserName(rs.getString("to_user"));
				transferVO.setTransferInitatedByName(rs.getString("initiator_user"));
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setSource(rs.getString("SOURCE"));
				transferVO.setTransferSubTypeValue(rs.getString("transfer_sub_type"));
				transferVO.setTransferDateAsString(rs.getString("transfer_date"));
				transferVO.setModifiedOnAsString(rs.getString("modified_ON"));
				transferVO.setProductType(rs.getString("product_name"));
				transferVO.setSenderCatName(rs.getString("sender_category_name"));
				transferVO.setReceiverCategoryDesc(rs.getString("receiver_category_name"));
				transferVO.setDisplayTransferMRP(rs.getString("transfer_mrp"));
				transferVO.setMrp(rs.getString("mrp"));
				transferVO.setCommQtyAsString(rs.getString("commision").trim());
				transferVO.setTax3ValueAsString(rs.getString("tax3_value").trim());
				transferVO.setSenderDrQtyAsString(rs.getString("sender_debit_quantity"));
				transferVO.setReceiverCrQtyAsString(rs.getString("receiver_credit_quantity"));
				transferVO.setPayableAmountAsStr(rs.getString("payable_amount"));
				transferVO.setNetPayableAmountAsStr(rs.getString("net_payable_amount"));
				
				transfersList.add(transferVO);

			}
			rs.close();

		} catch (SQLException sqe) {
			log.error(methodName, "SQLException : " + sqe);
			if (log.isDebugEnabled())
				log.debug(methodName, "SQL Exception:" + sqe);
			throw new BTSLBaseException(this, "", "error.general.sql.processing",sqe);
		} finally {
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return transfersList;
	}

}
