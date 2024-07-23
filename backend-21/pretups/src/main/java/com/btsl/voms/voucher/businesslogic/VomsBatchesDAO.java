/*
 * Created on Jul 12, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.voucher.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.ibm.icu.util.Calendar;
import com.restapi.channeluser.service.VoucherVO;

/**
 * @author vikas.yadav
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */

public class VomsBatchesDAO {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private VomsBatchesQry vomsBatchesQry;

	public VomsBatchesDAO() {
		super();
		vomsBatchesQry = (VomsBatchesQry) ObjectProducer.getObject(
				QueryConstants.VOMS_BATCHES_QRY, QueryConstants.QUERY_PRODUCER);
	}

	/**
	 * This method will change the batch Status after the voucher processing is
	 * over.
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_newStatus
	 *            String
	 * @param p_errorCount
	 *            long
	 * @param p_successCount
	 *            long
	 * @param p_modifiedBy
	 *            String
	 * @param p_batchNo
	 *            String
	 * @param p_errorMessage
	 *            String
	 * @return int
	 */

	public int changeBatchStatus(Connection p_con, String p_newStatus,
			int p_errorCount, int p_successCount, String p_modifiedBy,
			String p_batchNo, String p_errorMessage) throws BTSLBaseException {
		PreparedStatement pstmt = null;
		final String METHOD_NAME = "changeBatchStatus";
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue= new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("changeBatchStatus Method entered:");
			loggerValue.append(p_newStatus);
			loggerValue.append("  Batch No=");
			loggerValue.append(p_batchNo);
			loggerValue.append("  p_errorCount=");
			loggerValue.append(p_errorCount);
			loggerValue.append("p_modifiedBy=");
			loggerValue.append(p_modifiedBy);
			loggerValue.append("p_errorMessage=");
			loggerValue.append(p_errorMessage);
			_log.debug("changeBatchStatus()",loggerValue);
		}
		int i = 0;
		StringBuilder sqlLoadBuf = new StringBuilder(
				" UPDATE voms_batches set status=?, ");
		sqlLoadBuf
				.append(" total_no_of_failure=?, total_no_of_success=? , modified_date=?, modified_by=?,modified_on=?,message=?  ");
		sqlLoadBuf.append(" WHERE batch_no=? ");
		if (_log.isDebugEnabled()) {
			_log.debug("changeBatchStatus",
					"Update Query=" + sqlLoadBuf.toString());
		}
		try {
			pstmt = p_con.prepareStatement(sqlLoadBuf.toString());
			pstmt.setString(1, p_newStatus);
			pstmt.setLong(2, p_errorCount);
			pstmt.setLong(3, p_successCount);
			pstmt.setDate(4, BTSLUtil.getSQLDateFromUtilDate(new Date()));
			pstmt.setString(5, p_modifiedBy);
			pstmt.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(new Date()));
			pstmt.setString(7, p_errorMessage);
			pstmt.setString(8, p_batchNo);
			i = pstmt.executeUpdate();
		}

		catch (SQLException sqe) {
			if (_log.isDebugEnabled()) {
				_log.debug("changeBatchStatus()",
						"SQLException in  change status sqe=" + sqe);
			}
			i = 0;
			try {
				p_con.rollback();
			} catch (Exception ex) {
				if (_log.isErrorEnabled()) {
					_log.error("changeBatchStatus()",
							"Exception in  change status while rollback " + ex);
				}
				_log.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "VomsVoucherDAO[changeBatchStatus]",
						"", "", "", "Exception:" + ex.getMessage());

			}
		} finally {

			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.error("changeBatchStatus()",
						" Exception while closing pstmt ex=" + e);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("changeBatchStatus()",
						"changeBatchStatus() Exiting. with i=" + i);
			}

		}

		return i;
	}

	/*
	 * This method is used for the addition of the batch
	 * 
	 * @param p_con Connection
	 * 
	 * @param p_batchList ArrayList
	 * 
	 * @return int
	 * 
	 * @throws java.sql.SQLException
	 * 
	 * @throws java.lang.Exception
	 */

	public int addBatch(Connection p_con, ArrayList p_batchList)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue= new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("p_batchList=");
			loggerValue.append(p_batchList.size());
			_log.debug("addBatch() Entered",
					loggerValue);
		}
		PreparedStatement psmt = null;
		int addCount = 0;
		VomsBatchVO vomsBatchesVO = null;
		final String METHOD_NAME = "addBatch";
		try {
			StringBuilder strBuff = new StringBuilder(
					"INSERT INTO voms_batches (batch_no,product_id,batch_type, reference_no, reference_type,");
			strBuff.append("total_no_of_vouchers,from_serial_no, to_serial_no, network_code,created_date,created_on,created_by, status,process,modified_by,modified_on,modified_date,message,total_no_of_failure,total_no_of_success,DOWNLOAD_COUNT,remarks,STYPE,voucher_segment,master_batch_id");
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				strBuff.append(" ,sequence_id ");
			}
			strBuff.append(" )");
			strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				strBuff.append(" ,?");
			}
			strBuff.append(" )");
			if (_log.isDebugEnabled()) {
				_log.debug("addBatch()",
						"addBatch()Query=" + strBuff.toString());
			}
			psmt = p_con.prepareStatement(strBuff.toString());
			for (int i = 0, j = p_batchList.size(); i < j; i++) {
				addCount = 0;
				psmt.clearParameters();
				int x = 1;
				vomsBatchesVO = (VomsBatchVO) p_batchList.get(i);
				psmt.setString(x++, vomsBatchesVO.getBatchNo());
				psmt.setString(x++, vomsBatchesVO.getProductID());
				psmt.setString(x++, vomsBatchesVO.getBatchType());
				psmt.setString(x++, vomsBatchesVO.getReferenceNo());
				psmt.setString(x++, vomsBatchesVO.getReferenceType());
				psmt.setLong(x++, vomsBatchesVO.getNoOfVoucher());
				psmt.setString(x++, vomsBatchesVO.getFromSerialNo());
				psmt.setString(x++, vomsBatchesVO.getToSerialNo());
				psmt.setString(x++, vomsBatchesVO.getLocationCode());
				psmt.setDate(x++, BTSLUtil.getSQLDateFromUtilDate(vomsBatchesVO
						.getCreatedDate()));
				psmt.setTimestamp(x++, BTSLUtil
						.getTimestampFromUtilDate(vomsBatchesVO.getCreatedOn()));
				psmt.setString(x++, vomsBatchesVO.getCreatedBy());
				psmt.setString(x++, vomsBatchesVO.getStatus());
				psmt.setString(x++, vomsBatchesVO.getProcess());
				psmt.setString(x++, vomsBatchesVO.getModifiedBy());
				psmt.setTimestamp(x++,
						BTSLUtil.getTimestampFromUtilDate(vomsBatchesVO
								.getModifiedOn()));
				psmt.setDate(x++, BTSLUtil.getSQLDateFromUtilDate(vomsBatchesVO
						.getModifiedOn()));
				psmt.setString(x++, vomsBatchesVO.getMessage());
				psmt.setLong(x++, vomsBatchesVO.getFailCount());
				psmt.setLong(x++, vomsBatchesVO.getSuccessCount());
				psmt.setLong(x++, vomsBatchesVO.getDownloadCount());
				psmt.setString(x++, vomsBatchesVO.getRemarks());
				psmt.setInt(x++, vomsBatchesVO.getProcessScreen());
				psmt.setString(x++, vomsBatchesVO.getSegment());
				psmt.setString(x++, vomsBatchesVO.getMasterBatchNo());
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				psmt.setInt(x++, vomsBatchesVO.getSeq_id());
				}
				addCount += psmt.executeUpdate();
				if (addCount <= 0) {
					_log.error(" addBatch",
							" Not able to add in Batches table for Batch No="
									+ vomsBatchesVO.getBatchNo());
					EventHandler.handle(EventIDI.SYSTEM_ERROR,
							EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.MAJOR, "VomsBatchesDAO[addBatch]", "",
							"", "",
							"Not able to add in Batches table for Batch No="
									+ vomsBatchesVO.getBatchNo());
					throw new BTSLBaseException(
							this,
							"addBatch",
							PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
				}
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (SQLException sqe) {
			_log.error(" addBatch() ", "  SQL Exception =" + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,
					EventComponentI.SYSTEM,
					EventStatusI.RAISED,
					EventLevelI.FATAL,
					"VomsVoucherDAO[addBatch]",
					"",
					"",
					"",
					"SQL Exception: Error in adding batch info"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, "addBatch",
					PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		} catch (Exception ex) {
			_log.error(" addBatch() ", "  Exception =" + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[addBatch]", "", "", "",
					"Exception: Error in adding batch info" + ex.getMessage());
			throw new BTSLBaseException(this, "addBatch",
					PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		} finally {
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception e) {
				_log.error("addBatch()", " Exception while closing rs ex=" + e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("addBatch() Successful from serial number "
						+ vomsBatchesVO.getFromSerialNo()
						+ " to serial number " + vomsBatchesVO.getToSerialNo(),
						"Exiting:  addCount=" + addCount);
			}
		}
		return addCount;
	}

	// ***************************view batch list

	/**
	 * This method will be used to get batch information on the basis of batchNo
	 * provided
	 * 
	 * @param p_locationCode
	 * @param p_batchNo
	 *            String
	 * @return VomsBatchVO
	 * @throws SQLException
	 * @throws Exception
	 */

	public VomsBatchVO loadBatchListWithBatchNo(Connection p_con,
			String p_locationCode, String p_batchNo) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue= new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append(" Entered p_locationCode=");
			loggerValue.append(p_locationCode);
			loggerValue.append(", p_batchNo");
			loggerValue.append(p_batchNo);
			_log.debug("loadBatchListWithBatchNo",loggerValue );
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		final String METHOD_NAME = "loadBatchListWithBatchNo";

		String strBuff = vomsBatchesQry.loadBatchListWithBatchNoQry();

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadBatchListWithBatchNo",
						" loadBatchListWithBatchNo() :: Query :: " + strBuff);
			}
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_locationCode);
			psmt.setString(2, VOMSI.ALL);
			psmt.setString(3, p_locationCode);
			psmt.setString(4, p_batchNo);

			rs = psmt.executeQuery();
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setReferenceNo(rs.getString("REFERENCENO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				batchVO.setFromSerialNo(rs.getString("FROMSERIALNO"));
				batchVO.setToSerialNo(rs.getString("TOSERIALNO"));
				batchVO.setProductName(rs.getString("PRODUCTNAME"));
				batchVO.setNoOfVoucher(rs.getLong("TOTALVOUCHER"));
				batchVO.setFailCount(rs.getLong("FAILCOUNT"));
				batchVO.setSuccessCount(rs.getLong("SUCCCOUNT"));
				batchVO.setCreatedDate(rs.getDate("CREATEDATE"));
				batchVO.setCreatedOn(rs.getTimestamp("CREATEDON"));
				batchVO.setDownloadCount(rs.getInt("COUNT"));
				batchVO.setDownloadDate(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("DOWNLOADON")));
				batchVO.setStatus(rs.getString("STATUS"));
				batchVO.setCreatedBy(rs.getString("CREATEDBY"));
				batchVO.setCreatedOnStr(BTSLUtil
						.getDateTimeStringFromDate(batchVO.getCreatedOn()));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs
						.getString("message")));

			}
			return batchVO;
		} catch (SQLException sqle) {
			_log.error("loadBatchListWithBatchNo",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListWithBatchNo]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListWithBatchNo",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error("loadBatchListWithBatchNo",
					"Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListWithBatchNo]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "loadBatchListWithBatchNo",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(" loadBatchListWithBatchNo()",
						"::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error(
						" loadBatchListWithBatchNo() ",
						"::  Exception Closing Prepared Stmt: "
								+ ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("loadBatchListWithBatchNo() ",
						":: Exiting : batchList size = " + batchList.size());
			}
		}// end of finally

	}

	/**
	 * This is used to get batch information on the basis of status,type and
	 * date range provided
	 * 
	 * @param p_con
	 * @param p_locationCode
	 * @param p_batchStatus
	 *            String
	 * @param p_batchType
	 *            String
	 * @param p_fromDate
	 *            java.sql.Date
	 * @param p_toDate
	 *            java.sql.Date
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public java.util.ArrayList loadBatchList(Connection p_con,
			String p_locationCode, String p_status, String p_batchType,
			Date p_fromDate, Date p_toDate) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("loadBatchList", " Entered p_locationCode="
					+ p_locationCode + ", p_batchType" + p_batchType
					+ " and p_status" + p_status + "p_fromDate" + p_fromDate
					+ "p_toDate=" + p_toDate);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		final String METHOD_NAME = "loadBatchList";

		String strBuff = vomsBatchesQry.loadBatchListQry();

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadBatchList", ":: Query :: " + strBuff);
			}
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_locationCode);
			psmt.setString(2, p_locationCode);
			psmt.setString(3, p_status);
			psmt.setString(4, p_status);
			psmt.setString(5, p_batchType);
			psmt.setString(6, p_batchType);
			psmt.setDate(7, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
			psmt.setDate(8, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			rs = psmt.executeQuery();
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
//				batchVO.setProductName(rs.getString("PRODUCTNAME"));
				batchVO.setFirstApprovedBy(rs.getString("first_approved_by"));
				batchVO.setSecondApprovedBy(rs.getString("second_approved_by"));
				batchVO.setThirdApprovedBy(rs.getString("third_approved_by"));
				batchVO.setReferenceNo(rs.getString("REFERENCENO"));
				batchVO.setFromSerialNo(rs.getString("FROMSERIALNO"));
				batchVO.setToSerialNo(rs.getString("TOSERIALNO"));
				batchVO.setProductName(rs.getString("PRODUCTNAME"));
				batchVO.setNoOfVoucher(rs.getLong("TOTALVOUCHER"));
				batchVO.setFailCount(rs.getLong("FAILCOUNT"));
				batchVO.setSuccessCount(rs.getLong("SUCCCOUNT"));
				batchVO.setCreatedDate(rs.getDate("CREATEDATE"));
				batchVO.setCreatedOn(rs.getTimestamp("CREATEDON"));
				batchVO.setDownloadCount(rs.getInt("COUNT"));
				batchVO.setDownloadDate(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("DOWNLOADON")));
				batchVO.setStatus(rs.getString("STATUS"));
				batchVO.setCreatedBy(rs.getString("CREATEDBY"));
				batchVO.setCreatedOnStr(BTSLUtil
						.getDateTimeStringFromDate(batchVO.getCreatedOn()));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs
						.getString("message")));
				batchVO.setLocationCode(rs.getString("network_code"));
				batchVO.setExpiryPeriod(rs.getInt("expiry_period"));
				batchVO.setTalktime(rs.getInt("talktime"));
				batchVO.setValidity(rs.getInt("validity"));
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				batchVO.setSeq_id(rs.getInt("sequence_id"));
				}
				batchVO.setExpiryDate(rs.getDate("expiry_date"));
				batchVO.setSegment(rs.getString("voucher_segment"));
				batchList.add(batchVO);
			}
			return batchList;
		} catch (SQLException sqle) {

			_log.error("loadBatchList", "SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchList]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchList",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {

			_log.error("loadBatchList", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchList]", "", "", "", "Exception:"
							+ e.getMessage());
			throw new BTSLBaseException(this, "loadBatchList",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(" loadBatchList", " ::  Exception Closing RS : "
						+ ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error(
						" loadBatchList",
						" ::  Exception Closing Prepared Stmt: "
								+ ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("loadBatchList() ", ":: Exiting : batchList size = "
						+ batchList.size());
			}
		}
	}

	/**
	 * This is used to get batch information on basis of no. of days
	 * 
	 * @param p_con
	 *            con
	 * @param p_locationCode
	 *            string
	 * @param p_orderDays
	 *            String
	 * @return ArrayList
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
	public java.util.ArrayList loadBatchListOnDays(Connection p_con,
			String p_locationCode, int p_orderDays) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue= new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append(" Entered p_locationCode=");
			loggerValue.append(p_locationCode);
			loggerValue.append("p_orderDays");
			loggerValue.append(p_orderDays);
			_log.debug("loadBatchListOnDays", loggerValue);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		java.sql.Date requiredDate = null;
		batchList = new java.util.ArrayList();
		final String METHOD_NAME = "loadBatchListOnDays";

		String strBuff = vomsBatchesQry.loadBatchListOnDaysQry();

		if (p_orderDays != 0
				&& p_orderDays == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_BATCH_DAY))).intValue()) {
			Calendar cal = BTSLDateUtil.getInstance();
			cal.add(Calendar.DATE, (-1) * p_orderDays);
			requiredDate = BTSLUtil.getSQLDateFromUtilDate(cal.getTime());

		}
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadBatchListOnDays", " :: Query :: " + strBuff);
			}
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_locationCode);
			psmt.setString(2, p_locationCode);
			psmt.setDate(3, requiredDate);
			psmt.setDate(4, requiredDate);
			rs = psmt.executeQuery();
			// initialize batch List
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				batchVO.setReferenceNo(rs.getString("REFERENCENO"));
				batchVO.setFromSerialNo(rs.getString("FROMSERIALNO"));
				batchVO.setToSerialNo(rs.getString("TOSERIALNO"));
				batchVO.setProductName(rs.getString("PRODUCTNAME"));
				batchVO.setNoOfVoucher(rs.getLong("TOTALVOUCHER"));
				batchVO.setFailCount(rs.getLong("FAILCOUNT"));
				batchVO.setSuccessCount(rs.getLong("SUCCCOUNT"));
				batchVO.setCreatedDate(rs.getDate("CREATEDATE"));
				batchVO.setCreatedOn(rs.getTimestamp("CREATEDON"));
				batchVO.setDownloadCount(rs.getInt("COUNT"));
				batchVO.setDownloadDate(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("DOWNLOADON")));
				batchVO.setStatus(rs.getString("STATUS"));
				batchVO.setCreatedBy(rs.getString("CREATEDBY"));
				batchVO.setCreatedOnStr(BTSLUtil
						.getDateTimeStringFromDate(batchVO.getCreatedOn()));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs
						.getString("message")));
				batchList.add(batchVO);
			}
			return batchList;
		} catch (SQLException sqle) {

			_log.error("loadBatchListOnDays",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnDays]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnDays",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {

			_log.error("loadBatchListOnDays", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnDays]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnDays",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(" loadBatchListOnDays ",
						"::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error(
						" loadBatchListOnDays",
						" ::  Exception Closing Prepared Stmt: "
								+ ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("loadBatchListOnDays",
						" :: Exiting : batchList size = " + batchList.size());
			}
		}
	}

	public int generateSequenceNumber(Connection p_con, int[] sequence,
			int[] vouchers) {
		int seqNo = 0;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		TreeMap<Integer, Integer> sequenceVoucher = new TreeMap<>();
		int[] seq = new int[((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_RANGE))).intValue() + 1];
		final String METHOD_NAME = "generateSequenceNumber";
		Arrays.fill(seq, 0);
		boolean found =false;
		boolean update=false;
		int sum = IntStream.of(vouchers).sum();
		StringBuilder strBuff = new StringBuilder(
				"select sequence_id ,sum(total_no_of_vouchers) total from voms_batches where sequence_id is not null group by sequence_id   order by total asc ");
		try {
			psmt = p_con.prepareStatement(strBuff.toString());
			rs = psmt.executeQuery();

			boolean record = false;
			int counter = 0;
			if (sequence.length > 1) {
				for (int j = 0; j < sequence.length; j++) {
					if (sequence[j] != 0) {
						seq[sequence[j]] = 1;
					}
				}
			}
				while (rs.next()) {
					record = true;
					counter++;
					if (seqNo == 0) {
						seqNo = rs.getInt(1);
					}
					seq[rs.getInt(1)] = 1;
					sequenceVoucher.put(rs.getInt(1), rs.getInt(2));
				}
				for (int i = 1; i < seq.length; i++) {
					if (seq[i] == 0) {
						seqNo = i;
						found=true;
						break;
					}
				}
				
				if (!found) {
					for (int k = 0; k < vouchers.length; k++) {
						if(sequence[k]>0){
							update=true;
						sequenceVoucher.put(sequence[k], sequenceVoucher.get(sequence[k])
								+ vouchers[k]);
						}
					}
					Set<Entry<Integer, Integer>> set = sequenceVoucher.entrySet();
					List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(set);
					Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {

						@Override
						public int compare(Entry<Integer, Integer> o1,
								Entry<Integer, Integer> o2) {
						return (o1.getValue()).compareTo(o2.getValue());
						}

					});
					if(list.get(0).getKey()!=0)
					{
						seqNo=list.get(0).getKey();
					}
					else{
						seqNo=list.get(1).getKey();
					}
				
				} 
			
				return seqNo;
			
			
			
		} catch (SQLException e) {
			_log.errorTrace(METHOD_NAME, e);
			return seqNo;
		} catch (Exception e1) {
			_log.errorTrace(METHOD_NAME, e1);
			return seqNo;
		} finally {
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
			try{
		        if (psmt!= null){
		        	psmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  _log.error("An error occurred closing statement.", e);
		      }
		}
	}
	
/**
 * This method is used to load total number of user voucher based on Product ID	
 * @param p_con
 * @param productId
 * @return
 * @throws BTSLBaseException
 */

	public long totalNumberOfUSedVouchers(Connection p_con,String productId) throws BTSLBaseException {
		final String METHOD_NAME = "loadBatchListBasedOnProductID";
		LogFactory.printLog(METHOD_NAME, " Entered productId="+ productId , _log);
		PreparedStatement psmt = null;
		ResultSet rs = null;
		long totalNumberOfUSedVouchers =0;
		//StringBuilder strBuff = new StringBuilder("SELECT sum(total_no_of_success) as usedVoucher FROM voms_batches B   WHERE B.product_id= ? and B.status = ? and B.batch_type <> ? ");
		StringBuilder strBuff = new StringBuilder("select sum(total_no_of_vouchers) as usedVoucher from voms_print_batches v where v.product_id = ?  ");
		try {
	    	 LogFactory.printLog(METHOD_NAME, " :: Query :: " + strBuff , _log);
			psmt = p_con.prepareStatement(strBuff.toString());
			psmt.setString(1, productId);
			//psmt.setString(2, VOMSI.EXECUTED);
			//psmt.setString(3, VOMSI.VOUCHER_NEW);
			rs = psmt.executeQuery();
			psmt.clearParameters();
			while (rs.next()) {
				totalNumberOfUSedVouchers =rs.getLong("usedVoucher");
			}
			return totalNumberOfUSedVouchers;
		} catch (SQLException sqle) {
			_log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
		}// end of catch
		finally {
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
		        if (psmt!= null){
		        	psmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  _log.error("An error occurred closing statement.", e);
		      }
        	LogFactory.printLog(METHOD_NAME, " :: Exiting : totalNumberOfUSedVouchers = " + totalNumberOfUSedVouchers, _log);
			
		}
	}
	
/**
 * This method is used to load total number of Generated voucher on basis of productID	
 * @param p_con
 * @param productID
 * @return
 * @throws BTSLBaseException
 */
	public long totalNumberOfAvailableVouchers(Connection p_con,String productID) throws BTSLBaseException {
		final String METHOD_NAME = "totalNumberOfAvailableVouchers";
		LogFactory.printLog(METHOD_NAME, " Entered productId="+ productID , _log);
		PreparedStatement psmt = null;
		ResultSet rs = null;
		long totalNumberOfAvailableVouchers = 0;
		StringBuilder strBuff = new StringBuilder("SELECT sum(total_no_of_success) as generatedVoucher FROM voms_batches B WHERE B.product_id=? and B.status = ? and b.batch_type = ? ");
		try {
			LogFactory.printLog(METHOD_NAME, " :: Query :: " + strBuff , _log);
			psmt = p_con.prepareStatement(strBuff.toString());
			psmt.setString(1, productID);
			psmt.setString(2, VOMSI.EXECUTED);
			psmt.setString(3, VOMSI.VOUCHER_NEW);
			
			
			rs = psmt.executeQuery();
			psmt.clearParameters();
			while (rs.next()) {
				totalNumberOfAvailableVouchers = rs.getInt("generatedVoucher");
				}
			return totalNumberOfAvailableVouchers;
		} catch (SQLException sqle) {
			_log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			 throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
		}
		finally {
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
		        if (psmt!= null){
		        	psmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  _log.error("An error occurred closing statement.", e);
		      }
        	LogFactory.printLog(METHOD_NAME,":: Exiting : totalNumberOfAvailableVouchers = " +totalNumberOfAvailableVouchers, _log);
			
		}
	}
	

	public java.util.ArrayList loadAutoBatchList(Connection p_con,String p_locationCode, String p_status, String p_batchType,Date p_fromDate, Date p_toDate,String generationType) throws BTSLBaseException {
		final String METHOD_NAME = "loadAutoBatchList";
		LogFactory.printLog(METHOD_NAME," Entered p_locationCode: "+ p_locationCode + ",p_batchType: " + p_batchType+ ",p_status: " + p_status + ",p_fromDate: " + p_fromDate+ ",p_toDate: " + p_toDate+",generationType: "+generationType, _log);
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		String strBuff = vomsBatchesQry.loadAutoBatchListQry();
		
		try {
			LogFactory.printLog(METHOD_NAME, ":: Query :: " + strBuff, _log);
			
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_locationCode);
			psmt.setString(2, p_locationCode);
			psmt.setString(3, p_status);
			psmt.setString(4, p_status);
			psmt.setString(5, p_batchType);
			psmt.setString(6, p_batchType);
			psmt.setDate(7, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
			psmt.setDate(8, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			psmt.setString(9, generationType);
			rs = psmt.executeQuery();
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				batchVO.setReferenceNo(rs.getString("REFERENCENO"));
				batchVO.setFromSerialNo(rs.getString("FROMSERIALNO"));
				batchVO.setToSerialNo(rs.getString("TOSERIALNO"));
				batchVO.setProductName(rs.getString("PRODUCTNAME"));
				batchVO.setNoOfVoucher(rs.getLong("TOTALVOUCHER"));
				batchVO.setFailCount(rs.getLong("FAILCOUNT"));
				batchVO.setSuccessCount(rs.getLong("SUCCCOUNT"));
				batchVO.setCreatedDate(rs.getDate("CREATEDATE"));
				batchVO.setCreatedOn(rs.getTimestamp("CREATEDON"));
				batchVO.setDownloadCount(rs.getInt("COUNT"));
				batchVO.setDownloadDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("DOWNLOADON")));
				batchVO.setStatus(rs.getString("STATUS"));
				batchVO.setCreatedBy(rs.getString("CREATEDBY"));
				batchVO.setCreatedOnStr(BTSLUtil.getDateTimeStringFromDate(batchVO.getCreatedOn()));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs.getString("message")));
				batchVO.setLocationCode(rs.getString("network_code"));
				batchVO.setExpiryPeriod(rs.getInt("expiry_period"));
				batchVO.setTalktime(rs.getInt("talktime"));
				batchVO.setValidity(rs.getInt("validity"));
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				batchVO.setSeq_id(rs.getInt("sequence_id"));
				}
				batchVO.setExpiryDate(rs.getDate("expiry_date"));
				batchVO.setSegment(rs.getString("voucher_segment"));
				batchList.add(batchVO);
			}
			return batchList;
		} catch (SQLException sqle) {

			_log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"VomsVoucherDAO[loadAutoBatchList]", "", "", "","SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {

			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,EventStatusI.RAISED, EventLevelI.FATAL,"VomsVoucherDAO[loadAutoBatchList]", "", "", "", "Exception:"+ e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
		}// end of catch
		finally {
			try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
			try{
		        if (psmt!= null){
		        	psmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  _log.error("An error occurred closing statement.", e);
		      }
			LogFactory.printLog(METHOD_NAME,"Exiting : batchList size = "+ batchList.size(), _log);
			
		}
	}

	public int addBatch(Connection p_con, ArrayList p_batchList, String voucherSegment)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuilder loggerValue= new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("p_batchList=");
			loggerValue.append(p_batchList.size());
			_log.debug("addBatch() Entered",
					loggerValue);
		}
		PreparedStatement psmt = null;
		int addCount = 0;
		VomsBatchVO vomsBatchesVO = null;
		final String METHOD_NAME = "addBatch";
		try {
			StringBuilder strBuff = new StringBuilder(
					"INSERT INTO voms_batches (batch_no,product_id,batch_type, reference_no, reference_type,");
			strBuff.append("total_no_of_vouchers,from_serial_no, to_serial_no, network_code,created_date,created_on,created_by, status,process,modified_by,modified_on,modified_date,message,total_no_of_failure,total_no_of_success,DOWNLOAD_COUNT,remarks,STYPE,voucher_segment");
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				strBuff.append(" ,sequence_id ");
			}
			strBuff.append(" )");
			strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				strBuff.append(" ,?");
			}
			strBuff.append(" )");
			if (_log.isDebugEnabled()) {
				_log.debug("addBatch()",
						"addBatch()Query=" + strBuff.toString());
			}
			psmt = p_con.prepareStatement(strBuff.toString());
			for (int i = 0, j = p_batchList.size(); i < j; i++) {
				addCount = 0;
				psmt.clearParameters();

				vomsBatchesVO = (VomsBatchVO) p_batchList.get(i);
				psmt.setString(1, vomsBatchesVO.getBatchNo());
				psmt.setString(2, vomsBatchesVO.getProductID());
				psmt.setString(3, vomsBatchesVO.getBatchType());
				psmt.setString(4, vomsBatchesVO.getReferenceNo());
				psmt.setString(5, vomsBatchesVO.getReferenceType());
				psmt.setLong(6, vomsBatchesVO.getNoOfVoucher());
				psmt.setString(7, vomsBatchesVO.getFromSerialNo());
				psmt.setString(8, vomsBatchesVO.getToSerialNo());
				psmt.setString(9, vomsBatchesVO.getLocationCode());
				psmt.setDate(10, BTSLUtil.getSQLDateFromUtilDate(vomsBatchesVO
						.getCreatedDate()));
				psmt.setTimestamp(11, BTSLUtil
						.getTimestampFromUtilDate(vomsBatchesVO.getCreatedOn()));
				psmt.setString(12, vomsBatchesVO.getCreatedBy());
				psmt.setString(13, vomsBatchesVO.getStatus());
				psmt.setString(14, vomsBatchesVO.getProcess());
				psmt.setString(15, vomsBatchesVO.getModifiedBy());
				psmt.setTimestamp(16,
						BTSLUtil.getTimestampFromUtilDate(vomsBatchesVO
								.getModifiedOn()));
				psmt.setDate(17, BTSLUtil.getSQLDateFromUtilDate(vomsBatchesVO
						.getModifiedOn()));
				psmt.setString(18, vomsBatchesVO.getMessage());
				psmt.setLong(19, vomsBatchesVO.getFailCount());
				psmt.setLong(20, vomsBatchesVO.getSuccessCount());
				psmt.setLong(21, vomsBatchesVO.getDownloadCount());
				psmt.setString(22, vomsBatchesVO.getRemarks());
				psmt.setInt(23, vomsBatchesVO.getProcessScreen());
				psmt.setString(24, voucherSegment);
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				psmt.setInt(25, vomsBatchesVO.getSeq_id());
				}
				addCount += psmt.executeUpdate();
				if (addCount <= 0) {
					_log.error(" addBatch",
							" Not able to add in Batches table for Batch No="
									+ vomsBatchesVO.getBatchNo());
					EventHandler.handle(EventIDI.SYSTEM_ERROR,
							EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.MAJOR, "VomsBatchesDAO[addBatch]", "",
							"", "",
							"Not able to add in Batches table for Batch No="
									+ vomsBatchesVO.getBatchNo());
					throw new BTSLBaseException(
							this,
							"addBatch",
							PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
				}
			}
		} catch (BTSLBaseException be) {
			throw be;
		} catch (SQLException sqe) {
			_log.error(" addBatch() ", "  SQL Exception =" + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,
					EventComponentI.SYSTEM,
					EventStatusI.RAISED,
					EventLevelI.FATAL,
					"VomsVoucherDAO[addBatch]",
					"",
					"",
					"",
					"SQL Exception: Error in adding batch info"
							+ sqe.getMessage());
			throw new BTSLBaseException(this, "addBatch",
					PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		} catch (Exception ex) {
			_log.error(" addBatch() ", "  Exception =" + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[addBatch]", "", "", "",
					"Exception: Error in adding batch info" + ex.getMessage());
			throw new BTSLBaseException(this, "addBatch",
					PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		} finally {
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception e) {
				_log.error("addBatch()", " Exception while closing rs ex=" + e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("addBatch() Successful from serial number "
						+ vomsBatchesVO.getFromSerialNo()
						+ " to serial number " + vomsBatchesVO.getToSerialNo(),
						"Exiting:  addCount=" + addCount);
			}
		}
		return addCount;
	}
	
	public String getDenomination(Connection p_con, String productId) throws BTSLBaseException
	{
			if (_log.isDebugEnabled()) {
				StringBuilder loggerValue= new StringBuilder();
				loggerValue.setLength(0);
				_log.debug("getDenomination() Entered",	loggerValue);
			}
			PreparedStatement psmt = null;
			ResultSet rs = null;
			final String METHOD_NAME = "getDenomination";
			String mrp = null;
			try {
				StringBuilder strBuff = new StringBuilder(
						"Select mrp from VOMS_PRODUCTS where product_id = ?");
				psmt = p_con.prepareStatement(strBuff.toString());
				psmt.setString(1, productId);
				rs = psmt.executeQuery();
				psmt.clearParameters();
				while (rs.next()) {
					mrp = rs.getString("mrp");
				}
				return mrp;
			}
			catch (SQLException sqle) {
				_log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
				_log.errorTrace(METHOD_NAME, sqle);
				throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
			}// end of catch
			catch (Exception e) {
				_log.error(METHOD_NAME, "Exception " + e.getMessage());
				_log.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
			}// end of catch
			finally {
				try{
					if (rs!= null){
						rs.close();
					}
				}
				catch (SQLException e){
					_log.error("An error occurred closing result set.", e);
				}
				try{
					if (psmt!= null){
						psmt.close();
					}
				}
				catch (SQLException e){
					_log.error("An error occurred closing statement.", e);
				}
				LogFactory.printLog(METHOD_NAME, " :: Exiting : ", _log);				
			}
		}
	
		public List<VoucherVO> getReprintVouchers(Connection p_con, String transactionId) throws BTSLBaseException {
			final String methodName = "getReprintVoucher";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered p_domainCode = " + transactionId);
	        }
	        
	        List<VoucherVO> list = new ArrayList<VoucherVO>();
	        VoucherVO voucherVO = null;
			
	        
	        
	        PreparedStatement psmt = null;
			ResultSet rs = null;
			//String mrp = null;
			try {
				//StringBuilder strBuff = new StringBuilder(
				//		"Select mrp from VOMS_PRODUCTS where product_id = ?");
				StringBuilder strBuff = new StringBuilder(
						"Select v.serial_no,v.pin_no,v.validity ,v.expiry_date ,p.created_on ,v.mrp , v.product_id from VOMS_VOUCHERS v, VOMS_PRINT_BATCHES p where p.printer_batch_id = ? and p.downloaded='Y' and serial_no between p.start_serial_no and p.end_serial_no");
				
				psmt = p_con.prepareStatement(strBuff.toString());				
				psmt.setString(1, transactionId);
				rs = psmt.executeQuery();
				psmt.clearParameters();
				while (rs.next()) {
					voucherVO = new VoucherVO();
					voucherVO.setSerialNo(rs.getString("SERIAL_NO"));
					voucherVO.setPinNo(rs.getString("PIN_NO"));
					voucherVO.setValidity(rs.getInt("VALIDITY"));
					voucherVO.setExpiryDate(rs.getString("EXPIRY_DATE"));
					voucherVO.setTransactionDate(rs.getString("CREATED_ON"));
					voucherVO.setMrp(rs.getInt("MRP"));
					voucherVO.setProductId(rs.getLong("PRODUCT_ID"));
					list.add(voucherVO);
					//mrp = rs.getString("mrp");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        finally {
	        	_log.debug(methodName, "Exiting sql");
	        }
	        
	        
	        return list;
		}
	/**
	 * This method is used to get total number of user voucher based on Product ID	
	 * @param p_con
	 * @param productId
	 * @return
	 * @throws BTSLBaseException
	 */

		public ArrayList<VomsBatchVO> getOnlineVoucherBatchList(Connection p_con, ArrayList<String> networksNotAllowed) throws BTSLBaseException {
			final String METHOD_NAME = "getOnlineVoucherBatchList";
			
			ArrayList<VomsBatchVO> onlineBatchList = new ArrayList<VomsBatchVO>();
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, "Entered");
	        }
			PreparedStatement psmt = null;
			ResultSet rs = null;
			long totalNumberOfUSedVouchers =0;
			StringBuilder strBuff = new StringBuilder("select vb.batch_no, vb.network_code, vb.status, vb.total_no_of_vouchers, vb.modified_on, vb.product_id, vp.mrp, vp.product_name PRODUCTNAME, vb.created_by CREATEDBY, vb.created_on, vb.first_approved_by, vb.second_approved_by, vb.third_approved_by,");
			strBuff.append(" vp.expiry_period, vp.talktime, vp.validity, vp.expiry_date, vb.voucher_segment ");
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
				strBuff.append(",vb.sequence_id ");
			}
			strBuff.append("from voms_batches vb, voms_products vp where vb.product_id = vp.product_id and vb.status = ? and vb.total_no_of_vouchers <= ?");
			if(networksNotAllowed != null && networksNotAllowed.size() > 0)
			{
				strBuff.append(" and vb.network_code not in");
				strBuff.append("(");
	            for(int i=0;i<networksNotAllowed.size()-1;i++)
	            {
	            	strBuff.append("?,");
	            }
	            strBuff.append("?)");
			}
			
			strBuff.append(" order by modified_on");
			try {
		    	LogFactory.printLog(METHOD_NAME, " :: Query :: " + strBuff , _log);
		    	int i = 1;
				psmt = p_con.prepareStatement(strBuff.toString());
				psmt.setString(i++, VOMSI.BATCH_ACCEPTED);
				psmt.setInt(i++, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.ONLINE_VOUCHER_GEN_LIMIT))).intValue());
				if(networksNotAllowed != null && networksNotAllowed.size() > 0)
				{
					for(int j=0;j<networksNotAllowed.size();j++)
					{
						psmt.setString(i++, networksNotAllowed.get(j));
					}
				}
				rs = psmt.executeQuery();
				psmt.clearParameters();
				while (rs.next()) 
				{	VomsBatchVO batchVO = new VomsBatchVO();
					batchVO.setBatchNo(rs.getString("BATCH_NO"));
					batchVO.set_NetworkCode(rs.getString("network_code"));
					batchVO.setStatus(rs.getString("STATUS"));
					batchVO.setModifiedOn(rs.getTimestamp("modified_on"));
					batchVO.setCreatedBy(rs.getString("CREATEDBY"));
					batchVO.setCreatedOn(rs.getTimestamp("created_on"));
					batchVO.setFirstApprovedBy(rs.getString("first_approved_by"));
					batchVO.setSecondApprovedBy(rs.getString("second_approved_by"));
					batchVO.setThirdApprovedBy(rs.getString("third_approved_by"));
					batchVO.setProductID(rs.getString("product_id"));
					batchVO.setProductName(rs.getString("PRODUCTNAME"));
					batchVO.setNoOfVoucher(rs.getLong("total_no_of_vouchers"));
					batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp")))));
					batchVO.setLocationCode(rs.getString("network_code"));
					batchVO.setExpiryPeriod(rs.getInt("expiry_period"));
					batchVO.setTalktime(rs.getInt("talktime"));
					batchVO.setValidity(rs.getInt("validity"));
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
						batchVO.setSeq_id(rs.getInt("sequence_id"));
					}
					batchVO.setExpiryDate(rs.getDate("expiry_date"));
					batchVO.setSegment(rs.getString("voucher_segment"));
					onlineBatchList.add(batchVO);
					break;
				}
				return onlineBatchList;
			} catch (SQLException sqle) {
				_log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
				_log.errorTrace(METHOD_NAME, sqle);
				throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
			}// end of catch
			catch (Exception e) {
				_log.error(METHOD_NAME, "Exception " + e.getMessage());
				_log.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
			}// end of catch
			finally {
				try{
	        		if (rs!= null){
	        			rs.close();
	        		}
	        	}
	        	catch (SQLException e){
	        		_log.error("An error occurred closing result set.", e);
	        	}
	        	try{
			        if (psmt!= null){
			        	psmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  _log.error("An error occurred closing statement.", e);
			      }
	        	LogFactory.printLog(METHOD_NAME, " :: Exiting : getOnlineVoucherBatchList = " + onlineBatchList, _log);
				
			}
		}
		/**
		 * This method is used to get total number of Online Voucher BatchList For Change other  Status based on Network Code 	
		 * @param p_con
		 * @param productId
		 * @return
		 * @throws BTSLBaseException
		 */
		public ArrayList<VomsBatchVO> getOnlineVoucherBatchListForChangeStatus(Connection p_con,ArrayList<String> networksNotAllowed,ArrayList<String> masterBatchesNotAllowed) throws BTSLBaseException
		{

           final String METHOD_NAME = "getOnlineVoucherBatchListForChangeStatus";
           ArrayList<VomsBatchVO> onlineChangeStatusBatchList = new ArrayList<VomsBatchVO>();
           if (_log.isDebugEnabled()) {
                  _log.debug(METHOD_NAME, "Entered");
          }

            int i=1;
            PreparedStatement psmt = null;
            ResultSet rs = null;
            StringBuilder strBuild = new StringBuilder(" SELECT vb.batch_no, vb.network_code, vb.status, vb.total_no_of_vouchers, vb.modified_on, vb.sequence_id, vb.product_id, vb.REFERENCE_NO, vb.REFERENCE_TYPE, vb.created_date, vp.mrp, vp.product_name PRODUCTNAME, vb.created_by CREATEDBY, vb.first_approved_by, vb.second_approved_by, vb.third_approved_by, vp.expiry_period, vp.talktime, vp.validity, vp.expiry_date, vb.voucher_segment,VB.STYPE,vb.master_batch_id,vc.VOUCHER_TYPE  ,vb.FROM_SERIAL_NO,vb.TO_SERIAL_NO,vb.batch_type  FROM voms_batches vb ,VOMS_PRODUCTS vp, VOMS_CATEGORIES vc ");
            strBuild.append(" WHERE vb.PRODUCT_ID=vp.PRODUCT_ID AND vp.CATEGORY_ID=vc.CATEGORY_ID AND vb.process =? AND vb.status =? AND vb.TOTAL_NO_OF_VOUCHERS <=? ");
            if(networksNotAllowed != null && networksNotAllowed.size() > 0)
            {
                  strBuild.append(" and vb.network_code not in");
                  strBuild.append("(");
            for(int j=0;j<networksNotAllowed.size()-1;j++)
            {
                  strBuild.append("?,");
            }
            strBuild.append("?) ");
            }
            if(masterBatchesNotAllowed != null && masterBatchesNotAllowed.size() > 0)
            {
                  strBuild.append(" and vb.master_batch_id not in");
                  strBuild.append("(");
            for(int j=0;j<masterBatchesNotAllowed.size()-1;j++)
            {
                  strBuild.append("?,");
            }
            strBuild.append("?) ");
            }
            strBuild.append(" ORDER BY vb.CREATED_ON ASC");
            try {
                  psmt = p_con.prepareStatement(strBuild.toString());
                  psmt.setString(i++, VOMSI.BATCH_PROCESS_CHANGE);
                  psmt.setString(i++, VOMSI.SCHEDULED);
                  String property = Constants.getProperty("VOMS_CHANGE_GEN_STATUS_ONLINE_COUNT");
                  if(property.isEmpty())
                  {
                        property="1000";
                  }
                  psmt.setInt(i++, Integer.parseInt(property));
                  if(networksNotAllowed != null && networksNotAllowed.size() > 0)
                  {
                        for(int j=0;j<networksNotAllowed.size();j++)
                        {
                              psmt.setString(i++, networksNotAllowed.get(j));
                        }
                  }
                  if(masterBatchesNotAllowed != null && masterBatchesNotAllowed.size() > 0)
                  {
                        for(int j=0;j<masterBatchesNotAllowed.size();j++)
                        {
                              psmt.setString(i++, masterBatchesNotAllowed.get(j));
                        }
                  }
                  rs = psmt.executeQuery();
                  psmt.clearParameters();
                  while (rs.next()) 
                  {     VomsBatchVO batchVO = new VomsBatchVO();
                        batchVO.setBatchNo(rs.getString("BATCH_NO"));
                        batchVO.set_NetworkCode(rs.getString("network_code"));
                        batchVO.setStatus(rs.getString("STATUS"));
                        batchVO.setModifiedOn(rs.getTimestamp("modified_on"));
                        batchVO.setCreatedBy(rs.getString("CREATEDBY"));
                        batchVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                        batchVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                        batchVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                        batchVO.setProductID(rs.getString("product_id"));
                        batchVO.setProductName(rs.getString("PRODUCTNAME"));
                        batchVO.setNoOfVoucher(rs.getLong("total_no_of_vouchers"));
                        batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp")))));
                        batchVO.setLocationCode(rs.getString("network_code"));
                        batchVO.setExpiryPeriod(rs.getInt("expiry_period"));
                        batchVO.setTalktime(rs.getInt("talktime"));
                        batchVO.setValidity(rs.getInt("validity"));
                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
                              batchVO.setSeq_id(rs.getInt("sequence_id"));
                        }
                        batchVO.setExpiryDate(rs.getDate("expiry_date"));
                        batchVO.setSegment(rs.getString("voucher_segment"));
                        batchVO.setReferenceNo(rs.getString("REFERENCE_NO"));
                        batchVO.setReferenceType(rs.getString("REFERENCE_TYPE"));
                        batchVO.setCreatedDate(rs.getDate("created_date"));
                        batchVO.setProcessScreen(rs.getInt("STYPE"));
                        batchVO.setMasterBatchNo(rs.getString("master_batch_id"));
                        batchVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
                        batchVO.setFromSerialNo(rs.getString("FROM_SERIAL_NO"));
                        batchVO.setToSerialNo(rs.getString("TO_SERIAL_NO"));
                        batchVO.setBatchType(rs.getString("batch_type"));
                        onlineChangeStatusBatchList.add(batchVO);
                        break;
                  }
                  return onlineChangeStatusBatchList;
            } catch (SQLException sqle) {
                  _log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
                  _log.errorTrace(METHOD_NAME, sqle);
                  throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
            }// end of catch
            catch (Exception e) {
                  _log.error(METHOD_NAME, "Exception " + e.getMessage());
                  _log.errorTrace(METHOD_NAME, e);
                  throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
            }// end of catch
            finally {
                  try{
                  if (rs!= null){
                        rs.close();
                  }
            }
            catch (SQLException e){
                  _log.error("An error occurred closing result set.", e);
            }
            try{
                    if (psmt!= null){
                        psmt.close();
                    }
                  }
                  catch (SQLException e){
                    _log.error("An error occurred closing statement.", e);
                  }
            LogFactory.printLog(METHOD_NAME, " :: Exiting : getOnlineVoucherBatchListForChangeStatus = " + onlineChangeStatusBatchList, _log);
                  
            }
		}
		
		/**
		 * When from and to serial numbers are given during voucher status change request, then based on generation batch no of the range
		 * of vouchers given, the range can be divided into sub-ranges(based on generation batch Nos.)
		 * This method will get the total number of vouchers associated for a master_batch_id
		 * @param p_con
		 * @param masterBatchNo
		 * @return
		 * @throws BTSLBaseException
		 */
		public int getNumberOfVouchersForMasterBatch(Connection p_con, String masterBatchNo) throws BTSLBaseException {
			final String METHOD_NAME = "getNumberOfVouchersForMasterBatch";
			LogFactory.printLog(METHOD_NAME, " Entered masterBatchNo = "+ masterBatchNo , _log);
			PreparedStatement psmt = null;
			ResultSet rs = null;
			int totalNumberOfVouchersForMasterBatch = 0;
			StringBuilder strBuff = new StringBuilder("SELECT sum(TOTAL_NO_OF_VOUCHERS) as total_vouchers FROM VOMS_BATCHES WHERE MASTER_BATCH_ID = ? and STATUS = ? ");
			try {
				LogFactory.printLog(METHOD_NAME, " :: Query :: " + strBuff , _log);
				psmt = p_con.prepareStatement(strBuff.toString());
				psmt.setString(1, masterBatchNo);
				psmt.setString(2,PretupsI.TXN_LOG_STATUS_SCHEDULE);
				rs = psmt.executeQuery();
				psmt.clearParameters();
				while (rs.next()) {
					totalNumberOfVouchersForMasterBatch = rs.getInt("total_vouchers");
				}
				return totalNumberOfVouchersForMasterBatch;
			} catch (SQLException sqle) {
				_log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
				_log.errorTrace(METHOD_NAME, sqle);
				throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
			}// end of catch
			catch (Exception e) {
				_log.error(METHOD_NAME, "Exception " + e.getMessage());
				_log.errorTrace(METHOD_NAME, e);
				 throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
			}
			finally {
				try{
	        		if (rs!= null){
	        			rs.close();
	        		}
	        	}
	        	catch (SQLException e){
	        		_log.error("An error occurred closing result set.", e);
	        	}
	        	try{
			        if (psmt!= null){
			        	psmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  _log.error("An error occurred closing statement.", e);
			      }
	        	LogFactory.printLog(METHOD_NAME,":: Exiting : totalNumberOfVouchersForMasterBatch = " +totalNumberOfVouchersForMasterBatch, _log);
			}
		}
		
		/*
		 * To select batches which are in batch accepted state from the provided list
		 */
		public ArrayList<VomsBatchVO> getOnlineVoucherBatchListWithBatchNos(Connection p_con, ArrayList<String> networksNotAllowed,ArrayList<VomsBatchVO> batchList) throws BTSLBaseException {
				final String METHOD_NAME = "getOnlineVoucherBatchListWithBatchNos";
				
				ArrayList<VomsBatchVO> onlineBatchList = new ArrayList<VomsBatchVO>();
				if (_log.isDebugEnabled()) {
					_log.debug(METHOD_NAME, "Entered");
		        }
				PreparedStatement psmt = null;
				ResultSet rs = null;
				long totalNumberOfUSedVouchers =0;
				StringBuilder strBuff = new StringBuilder("select vb.batch_no, vb.network_code, vb.status, vb.total_no_of_vouchers, vb.modified_on, vb.product_id, vp.mrp, vp.product_name PRODUCTNAME, vb.created_by CREATEDBY, vb.created_on, vb.first_approved_by, vb.second_approved_by, vb.third_approved_by,");
				strBuff.append(" vp.expiry_period, vp.talktime, vp.validity, vp.expiry_date, vb.voucher_segment ");
				if((boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE)){
					strBuff.append(",vb.sequence_id ");
				}
				strBuff.append("from voms_batches vb, voms_products vp where vb.product_id = vp.product_id and vb.status = ? and vb.batch_no in (");
				int vbSize = batchList.size();
				for(int i=1 ; i <= vbSize ; i++) {
					if(i != vbSize)
						strBuff.append("?,");
					else
						strBuff.append("?)");
				}
				if(networksNotAllowed != null && networksNotAllowed.size() > 0)
				{
					strBuff.append(" and vb.network_code not in");
					strBuff.append("(");
		            for(int i=0;i<networksNotAllowed.size()-1;i++)
		            {
		            	strBuff.append("?,");
		            }
		            strBuff.append("?)");
				}
				
				strBuff.append(" order by modified_on");
				try {
			    	LogFactory.printLog(METHOD_NAME, " :: Query :: " + strBuff , _log);
			    	int i = 1;
					psmt = p_con.prepareStatement(strBuff.toString());
					psmt.setString(i++, VOMSI.BATCH_ACCEPTED);
					//psmt.setInt(i++, SystemPreferences.ONLINE_VOUCHER_GEN_LIMIT);
					for(VomsBatchVO vb : batchList) {
						psmt.setString(i++, vb.getBatchNo());
						LogFactory.printLog(METHOD_NAME, "vb.getBatchNo()" +vb.getBatchNo() , _log);
					}
					if(networksNotAllowed != null && networksNotAllowed.size() > 0)
					{
						for(int j=0;j<networksNotAllowed.size();j++)
						{
							psmt.setString(i++, networksNotAllowed.get(j));
						}
					}
					rs = psmt.executeQuery();
					psmt.clearParameters();
					while (rs.next()) 
					{	LogFactory.printLog(METHOD_NAME, "rs.getString(\"BATCH_NO\")" + rs.getString("BATCH_NO") , _log);
						VomsBatchVO batchVO = new VomsBatchVO();
						batchVO.setBatchNo(rs.getString("BATCH_NO"));
						batchVO.set_NetworkCode(rs.getString("network_code"));
						batchVO.setStatus(rs.getString("STATUS"));
						batchVO.setModifiedOn(rs.getTimestamp("modified_on"));
						batchVO.setCreatedBy(rs.getString("CREATEDBY"));
						batchVO.setCreatedOn(rs.getTimestamp("created_on"));
						batchVO.setFirstApprovedBy(rs.getString("first_approved_by"));
						batchVO.setSecondApprovedBy(rs.getString("second_approved_by"));
						batchVO.setThirdApprovedBy(rs.getString("third_approved_by"));
						batchVO.setProductID(rs.getString("product_id"));
						batchVO.setProductName(rs.getString("PRODUCTNAME"));
						batchVO.setNoOfVoucher(rs.getLong("total_no_of_vouchers"));
						batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp")))));
						batchVO.setLocationCode(rs.getString("network_code"));
						batchVO.setExpiryPeriod(rs.getInt("expiry_period"));
						batchVO.setTalktime(rs.getInt("talktime"));
						batchVO.setValidity(rs.getInt("validity"));
						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
							batchVO.setSeq_id(rs.getInt("sequence_id"));
						}
						batchVO.setExpiryDate(rs.getDate("expiry_date"));
						batchVO.setSegment(rs.getString("voucher_segment"));
						onlineBatchList.add(batchVO);
					}
					return onlineBatchList;
				} catch (SQLException sqle) {
					_log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
					_log.errorTrace(METHOD_NAME, sqle);
					throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
				}// end of catch
				catch (Exception e) {
					_log.error(METHOD_NAME, "Exception " + e.getMessage());
					_log.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
				}// end of catch
				finally {
					try{
		        		if (rs!= null){
		        			rs.close();
		        		}
		        	}
		        	catch (SQLException e){
		        		_log.error("An error occurred closing result set.", e);
		        	}
		        	try{
				        if (psmt!= null){
				        	psmt.close();
				        }
				      }
				      catch (SQLException e){
				    	  _log.error("An error occurred closing statement.", e);
				      }
		        	LogFactory.printLog(METHOD_NAME, " :: Exiting : " + METHOD_NAME +" = " + onlineBatchList, _log);
					
				}
			}
		
};