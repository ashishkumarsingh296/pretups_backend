package com.web.voms.voucher.businesslogic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsPrintBatchVO;
import com.ibm.icu.util.Calendar;

public class VomsBatchesWebDAO {

	private Log _log = LogFactory.getLog(this.getClass().getName());
	private VomsBatchesWebQry vomsBatchesWebQry;

	public VomsBatchesWebDAO() {
		super();
		vomsBatchesWebQry = (VomsBatchesWebQry) ObjectProducer.getObject(
				QueryConstants.VOMS_BATCHES_WEB_QRY,
				QueryConstants.QUERY_PRODUCER);
	}

	/*
	 * This method is used to check whether any batch of the same type exist in
	 * the under process stage
	 * 
	 * @param con of Connection type
	 * 
	 * @param p_serialTo of String type
	 * 
	 * @param p_serialFrom of String type
	 * 
	 * @param p_voucherStatus of String type : This is the batch type
	 * 
	 * @param p_status of String type : This is the batch Status ie EX, SC etc
	 * 
	 * @return returns the ArrayList
	 * 
	 * @exception SQLException
	 * 
	 * @exception Exception
	 */
	public ArrayList checkBatchUnderProcess(Connection p_con,
			String p_serialTo, String p_serialFrom, String p_voucherStatus,
			String p_status, String network_code) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append("checkBatchUnderProcess() Entered.. p_serialTo=");
        	msg.append(p_serialTo);
        	msg.append("   p_serialFrom=");
        	msg.append(p_serialFrom);
        	msg.append("   p_voucherStatus=");
        	msg.append(p_voucherStatus);
        	msg.append("p_status=");
        	msg.append(p_status);
        	
        	String message=msg.toString();
			_log.debug(message, "");
		}
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		String qryStr = null;
		ArrayList batchList = null;
		ListValueVO listValVO = null;
		java.util.Date dbDate = null;
		String dtTime = null;
		StringBuffer sqlLoadBuf = null;
		final String METHOD_NAME = "checkBatchUnderProcess";
		try {
			sqlLoadBuf = new StringBuffer(
					" select b.batch_no batchno,b.from_serial_no,b.to_serial_no,b.batch_type,b.total_no_of_vouchers, ");
			sqlLoadBuf
					.append(" b.created_on createdOn from voms_batches b  WHERE batch_type=? AND ");
			sqlLoadBuf.append(" b.from_serial_no<=? ");
			sqlLoadBuf.append(" AND b.to_serial_no>=? AND b.status=? AND b.network_code= ? ");
			qryStr = sqlLoadBuf.toString();

			if (_log.isDebugEnabled()) {
				_log.debug("checkBatchUnderProcess()", "QUERY= " + qryStr);
			}
			dbPs = p_con.prepareStatement(qryStr);
			dbPs.setString(1, p_voucherStatus);
			dbPs.setString(2, p_serialFrom);
			dbPs.setString(3, p_serialTo);
			dbPs.setString(4, p_status);
			dbPs.setString(5, network_code);
			rs = dbPs.executeQuery();
			batchList = new ArrayList();
			while (rs.next()) {
				dbDate = BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("createdOn"));
				if (_log.isDebugEnabled()) {
					_log.debug("checkBatchUnderProcess()", "DB dbDate="
							+ dbDate + "DB TIME:=" + dbDate.getTime());
				}
				dtTime = String.valueOf(dbDate.getTime());
				listValVO = new ListValueVO(rs.getString("batchno"), dtTime);
				batchList.add(listValVO);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("checkBatchUnderProcess()",
						"After executing the query checkBatchUnderProcess method ");
			}

		}

		catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error("checkBatchUnderProcess()", "SQLException : " + sqe);
			}
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[checkBatchUnderProcess]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "checkBatchUnderProcess()",
					"error.general.sql.processing");
		} catch (Exception ex) {
			if (_log.isErrorEnabled()) {
				_log.error("checkBatchUnderProcess()", "Exception : " + ex);
			}
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[checkBatchUnderProcess]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "checkBatchUnderProcess()",
					"error.general.processing");
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.error("checkBatchUnderProcess()",
						" Exception while closing rs ex=" + e);
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception e) {
				_log.error("checkBatchUnderProcess()",
						" Exception while closing prepared statement ex=" + e);
			}
			try {
				if (_log.isDebugEnabled()) {
					_log.debug("checkBatchUnderProcess()",
							"Exiting: batchList size=" + batchList.size());
				}
			} catch (Exception e) {
				_log.error("checkBatchUnderProcess()",
						" no batch is under process=" + e);
			}
		}
		return batchList;

	}

	/**
	 * This method is used to change the under process batch types to Failed if
	 * they are over the expiry limit If there is any error then throws the
	 * SQLException
	 * 
	 * @param con
	 *            of Connection type
	 * @param p_batchList
	 *            of ArrayList type
	 * @return returns int
	 * @exception SQLException
	 * @exception Exception
	 */
	public int changeUnderProcessBatchStatus(Connection p_con,
			ArrayList p_batchList, Date p_modifiedOn, String p_modifiedBy)
			throws SQLException, BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered.. p_batchList.size=");
        	msg.append(p_batchList.size());
        	msg.append(" p_modifiedOn=");
        	msg.append(p_modifiedOn);
        	msg.append("p_modifiedOn=");
        	msg.append(p_modifiedBy);
        	
        	String message=msg.toString();
			_log.debug("changeUnderProcessBatchStatus()",message);
		}
		PreparedStatement dbPs = null;
		String qryStr = null;
		ListValueVO listValVO = null;
		StringBuffer sqlLoadBuf = null;
		int count = 0;
		final String METHOD_NAME = "changeUnderProcessBatchStatus";
		try {

			sqlLoadBuf = new StringBuffer(
					" UPDATE voms_batches SET status=? ,modified_by=?,modified_on=? WHERE batch_no=? ");
			qryStr = sqlLoadBuf.toString();
			if (_log.isDebugEnabled()) {
				_log.debug("changeUnderProcessBatchStatus()", ",QUERY= "
						+ qryStr);
			}
			for (int i = 0; i < p_batchList.size(); i++) {
				if (dbPs != null) {
					dbPs.clearParameters();
				}
				count = 0;
				listValVO = (ListValueVO) p_batchList.get(i);
				dbPs = p_con.prepareStatement(qryStr);
				dbPs.setString(1, VOMSI.BATCHFAILEDSTATUS);
				dbPs.setString(2, p_modifiedBy);
				dbPs.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_modifiedOn));
				dbPs.setString(4, listValVO.getLabel());
				count = dbPs.executeUpdate();
				if (count > 0) {
					dbPs.close();
				} else {
					throw new BTSLBaseException("Not able to update batch status");
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug("changeUnderProcessBatchStatus()",
						"After executing the query changeUnderProcessBatchStatus method ");
			}

		}

		catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error("changeUnderProcessBatchStatus()", "SQLException : "
						+ sqe);
			}
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[changeUnderProcessBatchStatus]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this,
					"changeUnderProcessBatchStatus()",
					"error.general.sql.processing");
		} catch (Exception ex) {
			if (_log.isErrorEnabled()) {
				_log.error("changeUnderProcessBatchStatus()", "Exception : "
						+ ex);
			}
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[changeUnderProcessBatchStatus]", "", "",
					"", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this,
					"changeUnderProcessBatchStatus()",
					"error.general.processing");
		} finally {

			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception e) {
				_log.error("changeUnderProcessBatchStatus()",
						" Exception while closing rs ex=" + e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("changeUnderProcessBatchStatus()", "Exiting: count="
						+ count);
			}

		}

		return count;

	}

	public VomsBatchVO loadBatchListWithBatchNoNew(Connection p_con,
			String p_locationCode, String p_batchNo,String p_userId) throws BTSLBaseException {
		final String methodName = "loadBatchListWithBatchNoNew";
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_locationCode=");
        	msg.append(p_locationCode);
        	msg.append(", p_batchNo");
        	msg.append(p_batchNo);
        	msg.append("p_userId ");
        	msg.append(p_userId);
        	
        	String message=msg.toString();
			_log.debug(methodName, message);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		String strBuff = vomsBatchesWebQry.loadBatchListWithBatchNoNewQry();

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, " loadBatchListWithBatchNoNew() :: Query :: " + strBuff);
			}
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_locationCode);
			psmt.setString(2, VOMSI.ALL);
			psmt.setString(3, p_locationCode);
			psmt.setString(4, p_batchNo);

		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
			psmt.setString(5, p_userId);
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
			psmt.setString(6, p_userId);

				
            
			
			rs = psmt.executeQuery();
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setReferenceNo(rs.getString("REFERENCENO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				isFilePresent(dbConnected, batchVO, rs);

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
				batchVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil
						.getDateTimeStringFromDate(batchVO.getCreatedOn())));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs
						.getString("message")));
				batchVO.setUserName(rs.getString("USER_NAME"));
				batchVO.setUserMsisdn(rs.getString("MSISDN"));
				batchVO.setRemarksLevel1(rs.getString("FIRST_APPROVER_REMARKS"));
				batchVO.setRemarksLevel2(rs.getString("SECOND_APPROVER_REMARKS"));
				batchVO.setRemarksLevel3(rs.getString("THIRD_APPROVER_REMARKS"));
				batchVO.setFirstApprovedBy(rs.getString("FIRST_APPROVED_BY"));
				batchVO.setFirstApprovedOn(BTSLDateUtil.getLocaleTimeStamp(rs.getString("FIRST_APPROVED_ON")));
				batchVO.setSecondApprovedBy(rs.getString("SECOND_APPROVED_BY"));
				batchVO.setSecondApprovedOn(BTSLDateUtil.getLocaleTimeStamp(rs.getString("SECOND_APPROVED_ON")));
				batchVO.setThirdApprovedBy(rs.getString("THIRD_APPROVED_BY"));
				batchVO.setThirdApprovedOn(BTSLDateUtil.getLocaleTimeStamp(rs.getString("THIRD_APPROVED_ON")));
			}
			return batchVO;
		} catch (SQLException sqle) {
			_log.error(methodName, "SQLException " + sqle.getMessage());
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListWithBatchNoNew]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListWithBatchNoNew",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error(methodName,
					"Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListWithBatchNoNew]", "", "", "",
					"Exception:" + e.getMessage());
			throw new   BTSLBaseException(this, "loadBatchListWithBatchNoNew",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, "::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(methodName, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, "::  Exception Closing Prepared Stmt: " + ex.getMessage());
				_log.errorTrace(methodName, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ":: Exiting : batchList size = " + batchList.size());
			}
		}// end of finally

	}

	public java.util.ArrayList loadBatchListNew(Connection p_con,
			String p_locationCode, String p_status, String p_batchType,
			Date p_fromDate, Date p_toDate ,String p_userId) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_locationCode=");
        	msg.append(p_locationCode);
        	msg.append(", p_batchType");
        	msg.append(p_batchType);
        	msg.append(" and p_status");
        	msg.append(p_status);
        	msg.append("p_fromDate");
        	msg.append(p_fromDate);
        	msg.append("p_toDate=");
        	msg.append(p_toDate);
        	msg.append("p_userId ");
        	msg.append(p_userId);
        	String message=msg.toString();
			_log.debug("loadBatchList", message);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		final String METHOD_NAME = "loadBatchListNew";
		String strBuff = vomsBatchesWebQry.loadBatchListNewQry();
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
		int i = 0;
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadBatchList", ":: Query :: " + strBuff);
			}
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(++i, p_locationCode);
			psmt.setString(++i, p_locationCode);
			psmt.setString(++i, p_status);
			psmt.setString(++i, p_status);
			psmt.setString(++i, p_batchType);
			psmt.setString(++i, p_batchType);
			psmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
			psmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
			psmt.setString(++i, p_userId);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
			psmt.setString(++i, p_userId);
			
			rs = psmt.executeQuery();
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				isFilePresent(dbConnected, batchVO, rs);
				
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
				batchVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil
						.getDateTimeStringFromDate(batchVO.getCreatedOn())));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs
						.getString("message")));
				batchVO.setLocationCode(rs.getString("network_code"));
				batchVO.setExpiryPeriod(rs.getInt("expiry_period"));
				batchVO.setTalktime(rs.getInt("talktime"));
				batchVO.setValidity(rs.getInt("validity"));
				batchVO.setUserName(rs.getString("USER_NAME"));
				batchVO.setUserMsisdn(rs.getString("MSISDN"));
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
	public java.util.ArrayList loadBatchListOnDaysNew(Connection p_con,
			String p_locationCode, int p_orderDays,String p_userId) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_locationCode=");
        	msg.append(p_locationCode);
        	msg.append(", p_orderDays");
        	msg.append(p_orderDays);
        	msg.append(", p_userId ");
        	msg.append(p_userId);
        	
        	String message=msg.toString();
			_log.debug("loadBatchListOnDays", message);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		java.sql.Date requiredDate = null;
		;
		batchList = new java.util.ArrayList();
		final String METHOD_NAME = "loadBatchListOnDaysNew";
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
		String strBuff = vomsBatchesWebQry.loadBatchListOnDaysNewQry();
		if (p_orderDays != 0
				&& p_orderDays == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_BATCH_DAY))).intValue()) {
			final Calendar cal = BTSLDateUtil.getInstance();
			cal.add(Calendar.DATE, (-1) * p_orderDays);
			requiredDate = BTSLUtil.getSQLDateFromUtilDate(cal.getTime());

		}
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadBatchListOnDays", " :: Query :: " + strBuff);
			}
			psmt = p_con.prepareStatement(strBuff);
			int i = 1;
			psmt.setString(i++, p_locationCode);
			psmt.setString(i++, p_locationCode);
			psmt.setDate(i++, requiredDate);
			psmt.setDate(i++, requiredDate);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
			psmt.setString(i++, p_userId);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
			psmt.setString(i++, p_userId);
			
			rs = psmt.executeQuery();
			// initialize batch List
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				isFilePresent(dbConnected, batchVO, rs);

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
				batchVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil
						.getDateTimeStringFromDate(batchVO.getCreatedOn())));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs
						.getString("message")));
				batchVO.setUserName(rs.getString("USER_NAME"));
				batchVO.setUserMsisdn(rs.getString("MSISDN"));
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

	/**
	 * This method will be used to get batches information that are on the basis
	 * of status only
	 * @param p_locationCode
	 * @param p_status
	 *            String
	 * @param voucherSegment TODO
	 * @param p_days
	 *            int
	 * 
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public java.util.ArrayList loadBatchListOnStatus(Connection p_con,
			String p_locationCode, String p_status, String p_productid, String voucherSegment)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_locationCode=");
        	msg.append(p_locationCode);
        	msg.append(", p_status");
        	msg.append(p_status);
        	msg.append(", p_productid");
        	msg.append(p_productid);
        	msg.append(", dbconnected");
        	msg.append(Constants.getProperty(QueryConstants.PRETUPS_DB));
        	
        	String message=msg.toString();
			_log.debug("loadBatchListOnStatus", message);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
		final String METHOD_NAME = "loadBatchListOnStatus";

		String strBuff = vomsBatchesWebQry
				.loadBatchListOnStatusQry(p_productid);
		try {
			_log.debug(" loadBatchListOnStatus", ":: Query :: " + strBuff);
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_locationCode);
			psmt.setString(2, VOMSI.ALL);
			psmt.setString(3, p_locationCode);
			psmt.setString(4, p_status);
			psmt.setString(5, "BSTAT");
			psmt.setString(6, voucherSegment);
			if (!BTSLUtil.isNullString(p_productid)) {
				psmt.setString(7, p_productid);
			}
			rs = psmt.executeQuery();
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				
					

				batchVO.setFromSerialNo(rs.getString("FROMSERIALNO"));
				batchVO.setLocationCode(rs.getString("NETWORK_CODE"));
				batchVO.setReferenceNo(rs.getString("REFERENCENO"));
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
				batchVO.setRemarks(rs.getString("remarks"));
				batchVO.setRemarksLevel1(rs.getString("FIRST_APPROVER_REMARKS"));
				batchVO.setRemarksLevel2(rs.getString("SECOND_APPROVER_REMARKS"));
				batchVO.setRemarksLevel3(rs.getString("THIRD_APPROVER_REMARKS"));
				if(!BTSLUtil.isNullString(rs.getString("SEQUENCE_ID"))) {
					batchVO.setSeq_id(Integer.parseInt(rs.getString("SEQUENCE_ID")));					
				}
				batchVO.setExpiryPeriod(rs.getInt("EXPIRY_PERIOD"));
				batchVO.setExpiryDate(rs.getDate("EXPIRY_DATE"));
				batchVO.setFirstApprovedBy(rs.getString("FIRST_APPROVED_BY"));
				batchVO.setSecondApprovedBy(rs.getString("SECOND_APPROVED_BY"));
				batchVO.setThirdApprovedBy(rs.getString("THIRD_APPROVED_BY"));
				batchVO.setSegment(rs.getString("VOUCHER_SEGMENT"));
				batchList.add(batchVO);
			}
			return batchList;
		} catch (SQLException sqle) {

			_log.error("loadBatchListOnStatus",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnStatus]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {

			_log.error("loadBatchListOnStatus", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnStatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(" loadBatchListOnStatus()",
						" ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error(
						" loadBatchListOnStatus",
						" ::  Exception Closing Prepared Stmt: "
								+ ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("loadBatchListOnStatus()"," :: Exiting : batchList size = " + batchList.size());
			}
		}
	}

	/**
	 * This method will be used to get batches information that are on the basis
	 * of status only
	 * 
	 * @param p_locationCode
	 * @param p_days
	 *            int
	 * @param p_status
	 *            String
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public java.util.ArrayList loadBatchListOnStatusNew(Connection p_con,
			String p_locationCode, String p_status, String p_productid ,String p_userId)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_locationCode=");
        	msg.append(p_locationCode);
        	msg.append(", p_status");
        	msg.append(p_status);
        	msg.append("p_productid");
        	msg.append(p_productid);
        	msg.append(" p_userId ");
        	msg.append(p_userId);
        	
        	String message=msg.toString();
			_log.debug("loadBatchListOnStatus", message);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
		final String METHOD_NAME = "loadBatchListOnStatusNew";

		String strBuff = vomsBatchesWebQry
				.loadBatchListOnStatusNewQry(p_productid);
		try {
			_log.debug(" loadBatchListOnStatus", ":: Query :: " + strBuff);
			psmt = p_con.prepareStatement(strBuff);
			int i = 1;
			psmt.setString(i++, p_locationCode);
			psmt.setString(i++, VOMSI.ALL);
			psmt.setString(i++, p_locationCode);
			psmt.setString(i++, p_status);
			if (!BTSLUtil.isNullString(p_productid)) {
				psmt.setString(i++, p_productid);
				
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
					psmt.setString(i++, p_userId);	
			}
			else
			{
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
					psmt.setString(i++, p_userId);	
			}
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
			psmt.setString(i++, p_userId);
			rs = psmt.executeQuery();
			batchList = new java.util.ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchType(rs.getString("BATCHTYPE"));
				batchVO.setBatchNo(rs.getString("BATCHNO"));
				batchVO.setProductID(rs.getString("PRODUCTID"));
				isFilePresent(dbConnected, batchVO, rs);

				batchVO.setFromSerialNo(rs.getString("FROMSERIALNO"));
				batchVO.setReferenceNo(rs.getString("REFERENCENO"));
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
				batchVO.setRemarks(rs.getString("remarks"));
				batchVO.setUserName(rs.getString("USER_NAME"));
				batchVO.setUserMsisdn(rs.getString("MSISDN"));
				batchList.add(batchVO);
			}
			return batchList;
		} catch (SQLException sqle) {

			_log.error("loadBatchListOnStatus",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnStatus]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {

			_log.error("loadBatchListOnStatus", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnStatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(" loadBatchListOnStatus()",
						" ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error(
						" loadBatchListOnStatus",
						" ::  Exception Closing Prepared Stmt: "
								+ ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("loadBatchListOnStatus()",
						" :: Exiting : batchList size = " + batchList.size());
			}
		}
	}

	/**
	 * @param p_con
	 * @param p_vomsBatchVO
	 * @return
	 * @author rahul.dutt updateBatchStatus to update the status of voms
	 *         generation order
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	/*public int updateBatchStatus(Connection p_con, VomsBatchVO p_vomsBatchVO) throws IOException {
		PreparedStatement pstmt = null;
		FileInputStream fs = null;
		final String METHOD_NAME = "updateBatchStatus";
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
		if (_log.isDebugEnabled()) {
			_log.debug("updateBatchStatus()",
					"updateBatchStatus Method entered:");
		}
		int i = 0;
		final StringBuffer sqlLoadBuf = new StringBuffer(
				" UPDATE voms_batches set status=?, ");
		sqlLoadBuf.append(" total_no_of_vouchers=?, modified_date=?, modified_by=?,modified_on=?,message=?,  ");
		
		if(p_vomsBatchVO.getApprvLvl()==1){
			sqlLoadBuf.append(" first_approver_remarks=?, first_approved_by=?, first_approved_on=? ");
		}
		
		else if(p_vomsBatchVO.getApprvLvl()==2){
			sqlLoadBuf.append(" second_approver_remarks=?, second_approved_by=?, second_approved_on=? ");
		}
		
		else if(p_vomsBatchVO.getApprvLvl()==3){
			sqlLoadBuf.append(" third_approver_remarks=?, third_approved_by=?, third_approved_on=? ");
		}
    	if(QueryConstants.DB_ORACLE.equals(dbConnected) && p_vomsBatchVO.getApprvLvl()==3 && p_vomsBatchVO.getFile() != null){
				sqlLoadBuf.append(", signed_doc=?, signed_doc_type=?, signed_doc_file_path=? ");
			}
    	else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected) && p_vomsBatchVO.getApprvLvl()==3 && p_vomsBatchVO.getFile() != null){
    		sqlLoadBuf.append(", SIGNED_DOC=?, SIGNED_DOC_TYPE=?, SIGNED_DOC_FILE_PATH=? ");
    	}
    	sqlLoadBuf.append(" WHERE batch_no=? ");
		
		if (_log.isDebugEnabled()) {
			_log.debug("updateBatchStatus",
					"Update Query=" + sqlLoadBuf.toString());
		}
		try {
			pstmt = p_con.prepareStatement(sqlLoadBuf.toString());
			pstmt.setString(1, p_vomsBatchVO.getStatus());
			pstmt.setLong(2, p_vomsBatchVO.getNoOfVoucher());

			pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_vomsBatchVO
					.getModifiedOn()));
			pstmt.setString(4, p_vomsBatchVO.getModifiedBy());
			pstmt.setTimestamp(5, BTSLUtil
					.getTimestampFromUtilDate(p_vomsBatchVO.getModifiedOn()));

			pstmt.setString(6, p_vomsBatchVO.getMessage());
			pstmt.setString(7, p_vomsBatchVO.getRemarks());
			pstmt.setString(8, p_vomsBatchVO.getModifiedBy());
			pstmt.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_vomsBatchVO.getModifiedOn()));
			if(p_vomsBatchVO.getApprvLvl()==3 && p_vomsBatchVO.getFile() != null){
				File file = new File(p_vomsBatchVO.getFileName()); //"D:/info.pdf"
				fs = new FileInputStream(file);
				pstmt.setBinaryStream(10,fs,fs.available()); 
				pstmt.setString(11, p_vomsBatchVO.getFile().getContentType());
				//File path of server
				pstmt.setString(12, p_vomsBatchVO.getFileName());
				
				pstmt.setString(13, p_vomsBatchVO.getBatchNo());
			}else{
				pstmt.setString(10, p_vomsBatchVO.getBatchNo());
			}
			i = pstmt.executeUpdate();
		} catch (SQLException sqe) {
		
			i = 0;
			try {
				p_con.rollback();
			} catch (Exception ex) {
				if (_log.isErrorEnabled()) {
					_log.error("updateBatchStatus()","Exception in  change status while rollback " + ex);
				}
				_log.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "VomsVoucherDAO[updateBatchStatus]",
						"", "", "", "Exception:" + ex.getMessage());
			}
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.error("updateBatchStatus()",
						" Exception while closing pstmt ex=" + e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("updateBatchStatus()",
						"updateBatchStatus() Exiting. with i=" + i);
			}
			if(null != fs)
				fs.close();
		}
		return i;
	}*/

	/**
	 * @param p_con
	 * @param p_userID
	 * @param p_fromDate
	 * @param p_toDate
	 * @return
	 * @throws BTSLBaseException
	 * @author rahul.dutt To get VOMS Batches list for printing.
	 */
	public ArrayList getVomsPrinterBatch(Connection p_con, UserVO userVO,
			Date p_fromDate, Date p_toDate, String p_batchType)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_userID: ");
        	msg.append(userVO.getUserID());
        	msg.append(", p_fromDate: ");
        	msg.append(p_fromDate);
        	msg.append(", p_toDate: ");
        	msg.append(p_toDate);
        	msg.append(", p_batchType: ");
        	msg.append(p_batchType);
        	
        	String message=msg.toString();
			_log.debug("getVomsPrinterBatch", message);
		}
		final String METHOD_NAME = "getVomsPrinterBatch";
		PreparedStatement pselect = null;
		ResultSet rs = null;
		VomsPrintBatchVO printBatchVO = null;
		ArrayList<VomsPrintBatchVO> list = null;
		ArrayList segmentList = null;
		try {
			segmentList=LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true);
			String sqlSelectBuf = vomsBatchesWebQry
					.getVomsPrinterBatchQry(p_batchType,userVO);

			if (_log.isDebugEnabled()) {
				_log.debug("getVomsPrinterBatch", "Select Query="
						+ sqlSelectBuf);
			}
			pselect = p_con.prepareStatement(sqlSelectBuf);
            int i=1;
			/*for (int i = 1; i <= 4; i++) {*/
				if (("Y".equals(p_batchType) || "N".equals(p_batchType))) {
					pselect.setString(i++, p_batchType);
				}
				pselect.setDate(i++,
						BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
				pselect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
				pselect.setString(i++,userVO.getNetworkID());
				if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
				{
					pselect.setString(i++, userVO.getUserID());
				}
			/*}*/
			rs = pselect.executeQuery();
			list = new ArrayList<VomsPrintBatchVO>();
			while (rs.next()) {
				printBatchVO = new VomsPrintBatchVO();
				printBatchVO.setPrintbatchID(rs.getString("printer_batch_id"));
				printBatchVO.setStartSerialNo(rs.getString("start_serial_no"));
				printBatchVO.setEndSerialNo(rs.getString("end_serial_no"));
				printBatchVO.setUserID(rs.getString("user_id"));
				printBatchVO.setIsDownloaded(rs.getString("downloaded"));
				printBatchVO.setProductID(rs.getString("product_id"));
				printBatchVO.setVomsDecryKey(rs.getString("voms_decryp_key"));
				printBatchVO.setTotNoOfVOuchers(rs
						.getLong("total_no_of_vouchers"));
				printBatchVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("created_on")));
				printBatchVO.setCreatedBy(rs.getString("created_by"));
				printBatchVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("modified_on")));
				printBatchVO.setModifiedBy(rs.getString("modified_by"));
				printBatchVO.setProductName(rs.getString("product_name"));
				printBatchVO.setMrp(PretupsBL.getDisplayAmount(rs
						.getDouble("mrp")));
				printBatchVO.setVoucherType(rs.getString("voucher_type"));
				printBatchVO.setVoucherSegment(rs.getString("voucher_segment"));
				printBatchVO.setVoucherSegmentDesc(BTSLUtil.getOptionDesc(rs.getString("voucher_segment"), segmentList).getLabel());
				list.add(printBatchVO);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("getVomsPrinterBatch", "list" + list.size());
			}
			return list;
		} catch (SQLException sqle) {
			_log.error("getVomsPrinterBatch",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getVomsPrinterBatch]", "", "", "",
					"Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, "getVomsPrinterBatch",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error("getVomsPrinterBatch", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getVomsPrinterBatch]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "getVomsPrinterBatch",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error("getVomsPrinterBatch",
						" Exception while closing rs ex=" + ex);
			}
			try {
				if (pselect != null) {
					pselect.close();
				}
			} catch (Exception ex) {
				_log.error("getVomsPrinterBatch",
						" Exception while closing prepared statement ex=" + ex);
			}
		}
	}

	/**
	 * @param p_con
	 * @param p_userID
	 * @param p_key
	 * @param p_modified_date
	 * @param p_printer_batch_id
	 * @return
	 * @throws BTSLBaseException
	 * @author rahul.dutt To updtae voms print batches table after successful
	 *         download by user
	 */
	public int updateVomsPrintBatchstatus(Connection p_con, String p_userID,
			String p_key, Date p_modified_date, String p_printer_batch_id,
			long p_fromserial_no, long to_serial_no) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_userID: ");
        	msg.append(p_userID);
        	msg.append(", p_key: ");
        	msg.append(p_key);
        	msg.append(", p_modified_date: ");
        	msg.append(p_modified_date);
        	msg.append(", p_printer_batch_id: ");
        	msg.append(p_printer_batch_id);
        	msg.append(", p_fromserial_no: ");
        	msg.append(p_fromserial_no);
        	msg.append(", to_serial_no: ");
        	msg.append(to_serial_no);
        	
        	String message=msg.toString();
			_log.debug("updateVomsPrintBatchstatus", message);
		}
		final String METHOD_NAME = "updateVomsPrintBatchstatus";
		PreparedStatement psupdate = null;
		PreparedStatement psupdate1= null;
		int count = 0;
		try {
			StringBuffer sqlSelectBuf = new StringBuffer(
					" UPDATE voms_print_batches set downloaded=?,modified_on=?,modified_by=?");
			if (!BTSLUtil.isNullString(p_key)) {
				sqlSelectBuf.append(",VOMS_DECRYP_KEY=? ");
			}
			sqlSelectBuf.append(" where printer_batch_id=? ");
			if (_log.isDebugEnabled()) {
				_log.debug("updateVomsPrintBatchstatus", "UPDATE Query="
						+ sqlSelectBuf.toString());
			}
			psupdate = p_con.prepareStatement(sqlSelectBuf.toString());
			psupdate.setString(1, PretupsI.YES);
			psupdate.setTimestamp(2,
					BTSLUtil.getTimestampFromUtilDate(p_modified_date));
			psupdate.setString(3, p_userID);
			if (!BTSLUtil.isNullString(p_key)) {
				psupdate.setString(4, p_key);
				psupdate.setString(5, p_printer_batch_id);
			} else {
				psupdate.setString(4, p_printer_batch_id);
			}
			count = psupdate.executeUpdate();
			if (count > 0) {
				// update download count status for generation batches
				count = 0;
				String strBuff = vomsBatchesWebQry
						.updateVomsPrintBatchstatusQry();
				if (_log.isDebugEnabled()) {
					_log.debug("updateVomsPrintBatchstatus", "Update Query"
							+ strBuff);
				}
				psupdate1 = p_con.prepareStatement(strBuff);
				psupdate1.setTimestamp(1,
						BTSLUtil.getTimestampFromUtilDate(p_modified_date));
				psupdate1.setString(2, String.valueOf(p_fromserial_no));
				psupdate1.setString(3, String.valueOf(to_serial_no));

				count = psupdate1.executeUpdate();
				if (_log.isDebugEnabled()) {
					_log.debug("updateVomsPrintBatchstatus",
							"voms_batches update count" + count);
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug("updateVomsPrintBatchstatus", "count" + count);
			}
			return count;
		} catch (SQLException sqle) {
			_log.error("updateVomsPrintBatchstatus",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[updateVomsPrintBatchstatus]", "", "", "",
					"Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, "updateVomsPrintBatchstatus",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error("updateVomsPrintBatchstatus",
					"Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[updateVomsPrintBatchstatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "updateVomsPrintBatchstatus",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (psupdate != null) {
					psupdate.close();
				}
			} catch (Exception ex) {
				_log.error("updateVomsPrintBatchstatus",
						" Exception while closing prepared statement ex=" + ex);
			}
			try {
				if (psupdate1 != null) {
					psupdate1.close();
				}
			} catch (Exception ex) {
				_log.error("updateVomsPrintBatchstatus",
						" Exception while closing prepared statement ex=" + ex);
			}
		}
	}

	/**
	 * This method gets the batch no to which the serial no provided exists or
	 * not
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_fromSerialNo
	 *            String
	 * @param p_toSerialNo
	 *            String
	 * @param p_mrp
	 *            double
	 * @param p_productId
	 *            String
	 * @param p_executeStat
	 *            String
	 * @param p_locationCode
	 *            String
	 * @return ArrayList
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */

	public ArrayList getBatchInfoForUserInputs(Connection p_con,
			String p_fromSerialNo, String p_toSerialNo, double p_mrp,
			String p_productId, String p_executeStat, String p_locationCode)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered..p_con= ");
        	msg.append(p_con);
        	msg.append(", p_fromSerialNo= ");
        	msg.append(p_fromSerialNo);
        	msg.append(",   p_toSerialNo= ");
        	msg.append(p_toSerialNo);
        	msg.append(", p_mrp= ");
        	msg.append(p_mrp);
        	msg.append(", p_productId= ");
        	msg.append(p_productId);
        	msg.append(", p_executeStat= ");
        	msg.append(p_executeStat);
        	msg.append(", p_locationCode= ");
        	msg.append(p_locationCode);
        	
        	String message=msg.toString();
			_log.debug("getBatchInfoForUserInputs()", message);
		}
		PreparedStatement dbPs = null;
		PreparedStatement dbPs1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String qryStr = null;
		String qryBchStr = null;
		ArrayList batchList = null;
		int batchsize=0;
		int i = 0;
		boolean flag = false;
		StringBuffer sqlLoadBuf = null;
		StringBuffer sqlBatchBuf = null;
		VomsBatchVO batchVO = null;
		final String METHOD_NAME = "getBatchInfoForUserInputs";
		try {
			qryStr = vomsBatchesWebQry.getBatchInfoForUserInputsQry();
			if (_log.isDebugEnabled()) {
				_log.debug("getBatchInfoForUserInputs", "QUERY= " + qryStr);
			}
			dbPs = p_con.prepareStatement(qryStr);
			dbPs.setString(1, p_productId);
			dbPs.setDouble(2, p_mrp);
			dbPs.setString(3, p_fromSerialNo);
			dbPs.setString(4, p_toSerialNo);
			dbPs.setString(5, p_executeStat);
			dbPs.setString(6, p_locationCode);
			rs = dbPs.executeQuery();
			if (_log.isDebugEnabled()) {
				_log.debug("getBatchInfoForUserInputs()", "ExeQUERY= " + rs);
			}
			while (rs.next()) {
				flag = true;
				i = i + 1;
				batchList = new ArrayList();
				batchVO = new VomsBatchVO();
				batchVO.setBatchNo(rs.getString("batchno"));
				batchVO.setBatchType(rs.getString("bType"));
				batchVO.setStatus("FOUND");
				batchVO.set_NetworkCode(p_locationCode);
				batchVO.setDownloadCount(rs.getInt("DOWNCOUNT"));
				batchVO.setSegment(rs.getString("voucher_segment"));
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
					batchVO.setSeq_id(rs.getInt("sequence_id"));
				}
				batchList.add(batchVO);
			}
			if (!flag) {
				qryBchStr = vomsBatchesWebQry
						.getBatchInfoForUserInputsSelectQry();
				if (_log.isDebugEnabled()) {
					_log.debug("getBatchInfoForUserInputs()",
							"GET BATCH QUERY= " + qryBchStr);
				}
				dbPs1 = p_con.prepareStatement(qryBchStr);
				dbPs1.setString(1, p_productId);
				dbPs1.setDouble(2, p_mrp);
				dbPs1.setString(3, p_fromSerialNo);
				dbPs1.setString(4, p_toSerialNo);
				dbPs1.setString(5, p_fromSerialNo);
				dbPs1.setString(6, p_toSerialNo);
				dbPs1.setString(7, p_fromSerialNo);
				dbPs1.setString(8, p_toSerialNo);
				dbPs1.setString(9, p_executeStat);
				rs1 = dbPs1.executeQuery();
				batchList = new ArrayList();
				while (rs1.next()) {
					batchVO = new VomsBatchVO();
					batchVO.setBatchNo(rs1.getString("batchno"));
					batchVO.setBatchType(rs1.getString("bType"));
					batchVO.setFromSerialNo(rs1.getString("fserial"));
					batchVO.setToSerialNo(rs1.getString("tserial"));
					batchVO.setNoOfVoucher(rs1.getLong("totalvouch"));
					batchVO.setSegment(rs1.getString("voucher_segment"));
					batchVO.set_NetworkCode(p_locationCode);
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
						batchVO.setSeq_id(rs1.getInt("sequence_id"));
					}
					batchVO.setStatus("FOUND");
					batchList.add(batchVO);
					// TO DO catch this and log in EVENT LOG that more than 1 GE
					// batch exist
				}
				
			}
			
			if(!batchList.isEmpty()){
				batchsize=batchList.size();
			}
		}

		catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error("getBatchInfoForUserInputs()", "SQLException : "
						+ sqe);
			}
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getBatchInfoForUserInputs]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "getBatchInfoForUserInputs()",
					"error.general.sql.processing");
		} catch (Exception ex) {

			if (_log.isErrorEnabled()) {
				_log.error("getBatchInfoForUserInputs()", "Exception : " + ex);
			}
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getBatchInfoForUserInputs]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "getBatchInfoForUserInputs()",
					"error.general.processing");
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing rs ex=" + e);
				}
			}
			try {
				if (rs1 != null) {
					rs1.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing rs ex=" + e);
				}
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing prepared statement ex="
									+ e);
				}
			}
			try {
				if (dbPs1 != null) {
					dbPs1.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing prepared statement ex="
									+ e);
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug("getBatchInfoForUserInputs()",
						"Exiting: batchList size=" +batchsize);
			}

		}
		return batchList;
	}

	public boolean getBatchInfoForUserInputsForChangeStatus(Connection p_con,
			String p_fromSerialNo, String p_toSerialNo, double p_mrp,
			String p_productId, String p_executeStat, String p_locationCode)
			throws BTSLBaseException {
		final String methodName = "getBatchInfoForUserInputsForChangeStatus";
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered..p_con= ");
        	msg.append(p_con);
        	msg.append(", p_fromSerialNo= ");
        	msg.append(p_fromSerialNo);
        	msg.append(",   p_toSerialNo= ");
        	msg.append(p_toSerialNo);
        	msg.append(", p_mrp= ");
        	msg.append(p_mrp);
        	msg.append(", p_productId= ");
        	msg.append(p_productId);
        	msg.append(", p_executeStat= ");
        	msg.append(p_executeStat);
        	msg.append(", p_locationCode= ");
        	msg.append(p_locationCode);
        	
        	String message=msg.toString();
			_log.debug(methodName, message);
		}
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		String qryStr = null;
		boolean flag = false;
		try {
			qryStr = vomsBatchesWebQry.getBatchInfoForUserInputsQry();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "QUERY= " + qryStr);
			}
			dbPs = p_con.prepareStatement(qryStr);
			dbPs.setString(1, p_productId);
			dbPs.setDouble(2, p_mrp);
			dbPs.setString(3, p_fromSerialNo);
			dbPs.setString(4, p_toSerialNo);
			dbPs.setString(5, p_executeStat);
			rs = dbPs.executeQuery();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "ExeQUERY= " + rs);
			}
			while (rs.next()) {
				flag = true;
				break;
			}
		}
		catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error(methodName, "SQLException : "
						+ sqe);
			}
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsBatchesWebDAO[getBatchInfoForUserInputsForChangeStatus]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {

			if (_log.isErrorEnabled()) {
				_log.error(methodName, "Exception : " + ex);
			}
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsBatchesWebDAO[getBatchInfoForUserInputsForChangeStatus]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error(methodName,
							" Exception while closing rs ex=" + e);
				}
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error(methodName,
							" Exception while closing prepared statement ex="
									+ e);
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,"Batch exists in VOMS_BATCHES = " +flag);
			}
		}
		return flag;
	}
	
	public java.util.ArrayList loadBatchListForMsisdn(Connection p_con,
			String p_locationCode, String msisdn,String p_userId) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_locationCode=");
        	msg.append(p_locationCode);
        	msg.append(" and p_status= ");
        	msg.append(msisdn);
        	msg.append(" p_userId= ");
        	msg.append(p_userId);
        	
        	String message=msg.toString();
			_log.debug("loadBatchListForMsisdn", message);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
		VomsBatchVO batchVO = null;
		java.util.ArrayList batchList = null;
		batchList = new java.util.ArrayList();
		final String METHOD_NAME = "loadBatchListForMsisdn";

		String strBuff = vomsBatchesWebQry.loadBatchListForMsisdnQry();

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadBatchListForMsisdn", ":: Query :: " + strBuff);
			}
			int i = 1;
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(i++, p_locationCode);
			psmt.setString(i++, p_locationCode);
			psmt.setString(i++, msisdn);
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
				psmt.setString(i++, p_userId);
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERSEGMENT_ALLOWED))).booleanValue())
			psmt.setString(i++, p_userId);

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
				batchVO.setDownloadDate(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("DOWNLOADON")));
				batchVO.setStatus(rs.getString("STATUS"));
				batchVO.setCreatedBy(rs.getString("CREATEDBY"));
				batchVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil
						.getDateTimeStringFromDate(batchVO.getCreatedOn())));
				batchVO.setMrp(String.valueOf(Double.parseDouble(PretupsBL
						.getDisplayAmount(rs.getLong("MRP")))));
				batchVO.setMessage(BTSLUtil.NullToString(rs
						.getString("message")));
				batchVO.setLocationCode(rs.getString("network_code"));
				batchVO.setExpiryPeriod(rs.getInt("expiry_period"));     
				batchVO.setTalktime(rs.getInt("talktime"));
				batchVO.setValidity(rs.getInt("validity"));
				batchVO.setUserName(rs.getString("USER_NAME"));
				batchVO.setUserMsisdn(rs.getString("MSISDN"));
				batchList.add(batchVO);
			}
			return batchList;
		} catch (SQLException sqle) {

			_log.error("loadBatchListForMsisdn", "SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchList]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListForMsisdn",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {

			_log.error("loadBatchListForMsisdn", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListForMsisdn]", "", "", "", "Exception:"
							+ e.getMessage());
			throw new BTSLBaseException(this, "loadBatchListForMsisdn",
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
	 * This method gets the batch no to which the serial no provided exists or
	 * not
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_fromSerialNo
	 *            String
	 * @param p_toSerialNo
	 *            String
	 * @param p_mrp
	 *            double
	 * @param p_productId
	 *            String
	 * @param p_executeStat
	 *            String
	 * @param p_locationCode
	 *            String
	 * @return ArrayList
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */

	public ArrayList getBatchInfoForUserInputs(Connection p_con,
			long p_fromSerialNo, long p_toSerialNo, double p_mrp,
			String p_productId, String p_executeStat, String p_locationCode)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered..p_con= ");
        	msg.append(p_con);
        	msg.append(", p_fromSerialNo= ");
        	msg.append(p_fromSerialNo);
        	msg.append(",   p_toSerialNo= ");
        	msg.append(p_toSerialNo);
        	msg.append(", p_mrp= ");
        	msg.append(p_mrp);
        	msg.append(", p_productId= ");
        	msg.append(p_productId);
        	msg.append(", p_executeStat= ");
        	msg.append(p_executeStat);
        	msg.append(", p_locationCode= ");
        	msg.append(p_locationCode);
        	
        	String message=msg.toString();
			_log.debug("getBatchInfoForUserInputs()", message);
		}
		PreparedStatement dbPs = null;
		PreparedStatement dbPs1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String qryStr = null;
		String qryBchStr = null;
		ArrayList batchList = null;
		int i = 0;
		boolean flag = false;
		StringBuffer sqlLoadBuf = null;
		StringBuffer sqlBatchBuf = null;
		VomsBatchVO batchVO = null;
		final String METHOD_NAME = "getBatchInfoForUserInputs";
		try {

			qryStr = vomsBatchesWebQry.getBatchInfoForSelectUserInputsQry();
			if (_log.isDebugEnabled()) {
				_log.debug("getBatchInfoForUserInputs", "QUERY= " + qryStr);
			}
			dbPs = p_con.prepareStatement(qryStr);
			dbPs.setString(1, p_productId);
			dbPs.setDouble(2, p_mrp);
			dbPs.setLong(3, p_fromSerialNo);
			dbPs.setLong(4, p_toSerialNo);
			dbPs.setString(5, p_executeStat);
			rs = dbPs.executeQuery();
		
			while (rs.next()) {
				if (_log.isDebugEnabled()) {
					_log.debug("getBatchInfoForUserInputs",
							"Batch Found in range");
				}
				flag = true;
				i = i + 1;
				if (i == 1) {
					batchList = new ArrayList();
					batchVO = new VomsBatchVO();
					batchVO.setBatchNo(rs.getString("batchno"));
					batchVO.setBatchType(rs.getString("bType"));
					batchVO.setStatus("FOUND");
					batchVO.setDownloadCount(rs.getInt("DOWNCOUNT"));
					batchVO.set_NetworkCode(p_locationCode);
					batchList.add(batchVO);

				} else {
					// more than 1 GE batch exist
					batchList = new ArrayList();
					batchVO = new VomsBatchVO();
					batchVO.setBatchNo(VOMSI.NOTAPPLICABLE);
					batchVO.setBatchType(VOMSI.NOTAPPLICABLE);
					batchVO.setStatus("FOUND");
					batchList.add(batchVO);
					break;
				}
			}
			if (!flag) {
				if (_log.isDebugEnabled()) {
					_log.debug("getBatchInfoForUserInputs",
							"No single Batch in range was found");
				}
				// checking if voucher are present in different vouchers

				qryBchStr = vomsBatchesWebQry
						.getBatchInfoForSelectUserInputsBatchBuffQry();
				if (_log.isDebugEnabled()) {
					_log.debug("getBatchInfoForUserInputs()",
							"GET BATCH QUERY= " + qryBchStr);
				}
				dbPs1 = p_con.prepareStatement(qryBchStr);
				dbPs1.setString(1, p_productId);
				dbPs1.setDouble(2, p_mrp);
				dbPs1.setLong(3, p_fromSerialNo);
				dbPs1.setLong(4, p_toSerialNo);
				dbPs1.setLong(5, p_fromSerialNo);
				dbPs1.setLong(6, p_toSerialNo);
				dbPs1.setLong(7, p_fromSerialNo);
				dbPs1.setLong(8, p_toSerialNo);
				dbPs1.setString(9, p_executeStat);
				
				rs1 = dbPs1.executeQuery();
				batchList = new ArrayList();
				while (rs1.next()) {
					batchVO = new VomsBatchVO();
					batchVO.setBatchNo(rs1.getString("batchno"));
					batchVO.setBatchType(rs1.getString("bType"));
					batchVO.setFromSerialNo(rs1.getString("fserial"));
					batchVO.setToSerialNo(rs1.getString("tserial"));
					batchVO.setNoOfVoucher(rs1.getLong("totalvouch"));
					batchVO.setStatus("SHOW");
					batchList.add(batchVO);
					// TO DO catch this and log in EVENT LOG that more than 1 GE
					// batch exist
				}

			}

		}

		catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error("getBatchInfoForUserInputs()", "SQLException : "
						+ sqe);
			}
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getBatchInfoForUserInputs]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "getBatchInfoForUserInputs()",
					"error.general.sql.processing");
		} catch (Exception ex) {

			if (_log.isErrorEnabled()) {
				_log.error("getBatchInfoForUserInputs()", "Exception : " + ex);
			}
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getBatchInfoForUserInputs]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "getBatchInfoForUserInputs()",
					"error.general.processing");
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing rs ex=" + e);
				}
			}
			try {
				if (rs1 != null) {
					rs1.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing rs ex=" + e);
				}
			}
			try {
				if (dbPs1 != null) {
					dbPs1.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing prepared statement ex="
									+ e);
				}
			}
			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception e) {
				if (_log.isErrorEnabled()) {
					_log.error("getBatchInfoForUserInputs()",
							" Exception while closing prepared statement ex="
									+ e);
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug("getBatchInfoForUserInputs()",
						"Exiting: batchList size=" + batchList.size());
			}

		}
		return batchList;
	}
	
	
	
	
	/**
	 * @param pCon
	 * @param p_locationCode
	 * @param p_status
	 * @return
	 * @throws BTSLBaseException
	 */
	public List<VomsBatchVO> loadBatchesForStatusChange(Connection pCon)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("loadBatchesForStatusChange", " Entered " );
		}
		 
		
		VomsBatchVO batchVO = null;
		List<VomsBatchVO> batchList = null;
		int batchsize=0;
		final String methodName = "loadBatchListOnStatus";
		StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT B.batch_no ,B.product_id ,B.batch_type ,B.reference_no,B.REFERENCE_TYPE  , B.total_no_of_vouchers , B.from_serial_no , B.to_serial_no, B.created_on , ");
		strBuff.append(" B.network_code ,B.created_date ,  B.created_by,B.MODIFIED_BY,B.STATUS, B.PROCESS,B.STYPE,P.TALKTIME,vc.voucher_type ");
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
			strBuff.append(", B.sequence_id");
			}
		strBuff.append(" FROM voms_batches B, voms_products P,LOOKUPS l,LOOKUP_TYPES lt,VOMS_CATEGORIES vc  WHERE B.product_id=P.product_id AND l.LOOKUP_TYPE=lt.LOOKUP_TYPE and B.status=l.LOOKUP_CODE and  ");
		strBuff.append("  B.status=? and l.lookup_type=? and vc.CATEGORY_ID=P.CATEGORY_ID order by B.created_on");
		String query=strBuff.toString();
		try(PreparedStatement psmt = pCon.prepareStatement(query);) {
			_log.debug(" loadBatchesForStatusChange", ":: Query :: " + strBuff);
			
			psmt.setString(1, VOMSI.SCHEDULED);
			psmt.setString(2,VOMSI.LOOKUP_BATCH_STATUS); 
		
			try(ResultSet rs = psmt.executeQuery();)
			{
			batchList = new ArrayList();
			psmt.clearParameters();
			while (rs.next()) {
				batchVO = new VomsBatchVO();
				batchVO.setBatchNo(rs.getString("batch_no"));
				batchVO.setBatchType(rs.getString("batch_type"));
				batchVO.setProductID(rs.getString("product_id"));
				batchVO.setReferenceNo(rs.getString("reference_no"));
				batchVO.setReferenceType(rs.getString("REFERENCE_TYPE"));
				batchVO.setNoOfVoucher(rs.getLong("total_no_of_vouchers"));
				batchVO.setFromSerialNo(rs.getString("from_serial_no"));
				batchVO.setToSerialNo(rs.getString("to_serial_no"));
				batchVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				batchVO.setLocationCode(rs.getString("network_code"));
				batchVO.setCreatedDate(rs.getDate("created_date"));
				batchVO.setCreatedBy(rs.getString("created_by"));
				batchVO.setModifiedBy(rs.getString("MODIFIED_BY"));
				batchVO.setStatus(rs.getString("STATUS"));
				batchVO.setProcess(rs.getString("PROCESS"));
				batchVO.setProcessScreen(rs.getInt("STYPE"));
				batchVO.set_NetworkCode(rs.getString("network_code"));
				batchVO.setMrp(rs.getString("TALKTIME"));
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
					batchVO.setSeq_id(rs.getInt("sequence_id"));
					}
				
				batchVO.setLocationCode(rs.getString("network_code"));
				batchVO.setVoucherType(rs.getString("voucher_type"));
				batchList.add(batchVO);
			}
			
			if(!batchList.isEmpty()){
				batchsize=batchList.size();
				
			}
			return batchList;
		}
		}catch (SQLException sqle) {

			_log.error("loadBatchListOnStatus",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnStatus]", "", "", "",
					"SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {

			_log.error("loadBatchListOnStatus", "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[loadBatchListOnStatus]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus",
					"error.general.processing");
		}// end of catch
		finally {
		
			
			if (_log.isDebugEnabled()) {
				_log.debug("loadBatchListOnStatus()",
						" :: Exiting : batchList size = " +batchsize);
			}
		}
	}
	/**
	 * @param p_con
	 * @param p_userID
	 * @param p_fromDate
	 * @param p_toDate
	 * @return
	 * @throws BTSLBaseException
	 * @author To get VOMS Batches list for printing.
	 */
	public ArrayList getVomsPrinterBatchForUser(Connection p_con, UserVO userVO,
			Date p_fromDate, Date p_toDate, String p_batchType)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_userID: ");
        	msg.append(userVO.getUserID());
        	msg.append(", p_fromDate: ");
        	msg.append(p_fromDate);
        	msg.append(", p_toDate: ");
        	msg.append(p_toDate);
        	msg.append(", p_batchType: ");
        	msg.append(p_batchType);
        	
        	String message=msg.toString();
			_log.debug("getVomsPrinterBatchForUser", message);
		}
		final String METHOD_NAME = "getVomsPrinterBatchForUser";
		PreparedStatement pselect = null;
		ResultSet rs = null;
		VomsPrintBatchVO printBatchVO = null;
		ArrayList<VomsPrintBatchVO> list = null;
		ArrayList segmentList = null;
		try {
			segmentList=LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true);
			String sqlSelectBuf = vomsBatchesWebQry
					.getVomsPrinterBatchForUserQry(p_batchType,userVO);

			if (_log.isDebugEnabled()) {
				_log.debug("getVomsPrinterBatchForUser", "Select Query="
						+ sqlSelectBuf);
			}
			pselect = p_con.prepareStatement(sqlSelectBuf);
			
			int i = 1; 
			/*for (int i = 1; i <= 4; i++) {*/
				if (("Y".equals(p_batchType) || "N".equals(p_batchType))) {
					pselect.setString(i++, p_batchType);
				}
				pselect.setDate(i++,
						BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
				pselect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
				pselect.setString(i++, userVO.getUserID());
				pselect.setString(i++, userVO.getNetworkID());
				if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
				{
					pselect.setString(i++, userVO.getUserID());
				}
			/*}*/
			rs = pselect.executeQuery();
			list = new ArrayList<VomsPrintBatchVO>();
			while (rs.next()) {
				printBatchVO = new VomsPrintBatchVO();
				printBatchVO.setPrintbatchID(rs.getString("printer_batch_id"));
				printBatchVO.setStartSerialNo(rs.getString("start_serial_no"));
				printBatchVO.setEndSerialNo(rs.getString("end_serial_no"));
				printBatchVO.setUserID(rs.getString("user_id"));
				printBatchVO.setIsDownloaded(rs.getString("downloaded"));
				printBatchVO.setProductID(rs.getString("product_id"));
				printBatchVO.setVomsDecryKey(rs.getString("voms_decryp_key"));
				printBatchVO.setTotNoOfVOuchers(rs
						.getLong("total_no_of_vouchers"));
				printBatchVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("created_on")));
				printBatchVO.setCreatedBy(rs.getString("created_by"));
				printBatchVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("modified_on")));
				printBatchVO.setModifiedBy(rs.getString("modified_by"));
				printBatchVO.setProductName(rs.getString("product_name"));
				printBatchVO.setMrp(PretupsBL.getDisplayAmount(rs
						.getDouble("mrp")));
				printBatchVO.setVoucherType(rs.getString("voucher_type"));
				printBatchVO.setVoucherSegment(rs.getString("voucher_segment"));
				printBatchVO.setVoucherSegmentDesc(BTSLUtil.getOptionDesc(rs.getString("voucher_segment"), segmentList).getLabel());
				list.add(printBatchVO);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("getVomsPrinterBatchForUser", "list" + list.size());
			}
			return list;
		} catch (SQLException sqle) {
			_log.error("getVomsPrinterBatchForUser",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getVomsPrinterBatchForUser]", "", "", "",
					"Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, "getVomsPrinterBatchForUser",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error("getVomsPrinterBatchForUser", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getVomsPrinterBatchForUser]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "getVomsPrinterBatchForUser",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error("getVomsPrinterBatchForUser",
						" Exception while closing rs ex=" + ex);
			}
			try {
				if (pselect != null) {
					pselect.close();
				}
			} catch (Exception ex) {
				_log.error("getVomsPrinterBatchForUser",
						" Exception while closing prepared statement ex=" + ex);
			}
		}
	}
	
	
	public String loadSignedDocument(Connection p_con, String p_batchNo, String p_filePath) throws BTSLBaseException {
		final String METHOD_NAME = "loadSignedDocument";
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_batchNo=");
        	msg.append(p_batchNo);
        	msg.append(" Entered p_filePath=");
        	msg.append(p_filePath);
        	String message=msg.toString();
			_log.debug(METHOD_NAME, message);
		}
		PreparedStatement psmt = null;
		ResultSet rs = null;
        String fileName = null;
        File file = null;
        BufferedInputStream is = null;
        FileOutputStream fos = null;
        String strBuff = "";
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        
        if(QueryConstants.DB_ORACLE.equals(dbConnected)){
        	 strBuff = "SELECT signed_doc as SIGNEDFORM,signed_doc_file_path as FILEPATH from voms_batches where batch_no = ?";	
        	 }
	else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
		strBuff = "SELECT SIGNED_DOC as SIGNEDFORM,signed_doc_file_path as FILEPATH from voms_batches where batch_no = ?";	
		}
        
		
		if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Download Signed form Query: ", strBuff);
        }
		try {
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_batchNo);
			rs = psmt.executeQuery();
			while(rs.next()){
				Blob blob = rs.getBlob("SIGNEDFORM");
				String serverFilePath = rs.getString("FILEPATH");
				if(null != serverFilePath){
					String[] tokens = serverFilePath.split("/");
					fileName = tokens[tokens.length-1];
				}
				file = new File(p_filePath + fileName);
				is = new BufferedInputStream(blob.getBinaryStream());
				fos = new FileOutputStream(file);
				byte[] buffer = new byte[2048];
				int r = 0;
				while((r = is.read(buffer))!=-1) {
					fos.write(buffer, 0, r);
				}
				fos.flush();
				fos.close();
				is.close();
			}
			return fileName;
		} catch (SQLException sqle) {
			_log.error("loadBatchListOnStatus", "SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadBatchListOnStatus]", "", "", "", "SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus", "error.general.sql.processing");
		}
		catch (Exception e) {
			_log.error("loadBatchListOnStatus", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadBatchListOnStatus]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "loadBatchListOnStatus", "error.general.processing");
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
				}
			}catch (IOException ex) {
				_log.error(METHOD_NAME, " ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (is != null) {
					is.close();
				}
			}catch (IOException ex) {
				_log.error(METHOD_NAME, " ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(METHOD_NAME, " ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error( METHOD_NAME, " ::  Exception Closing Prepared Stmt: " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," :: Exiting : batchList size = " + fileName);
			}
		}
	}

	private void isFilePresent(String dbConnected, VomsBatchVO batchVO, ResultSet rs) throws SQLException {
		final String methodName = "isFilePresent";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName," :: Entered dbConnected =  " + dbConnected);
		}
		if(QueryConstants.DB_ORACLE.equals(dbConnected)){
		if(null != rs.getBlob("SIGNEDDOC"))
			batchVO.setFilePresent(true);
		}
		else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
			if(null != rs.getBinaryStream("SIGNEDDOC"))
				batchVO.setFilePresent(true);
		}
		
		if (_log.isDebugEnabled()) {
			_log.debug(methodName," :: Exiting ");
		}
	}
	
	
	/**
	 * @param p_con
	 * @param p_userID
	 * @param p_fromDate
	 * @param p_toDate
	 * @return
	 * @throws BTSLBaseException
	 * @author To get VOMS Batches list for printing.
	 */
	public VomsPrintBatchVO getVomsPrinterBatchByBatchID(Connection p_con, UserVO userVO, String p_batchType, String p_batchId)
			throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_userID: ");
        	msg.append(userVO.getUserID());
        	msg.append(", p_batchType: ");
        	msg.append(p_batchType);
        	msg.append(", p_batchId: ");
        	msg.append(p_batchId);
        	
        	String message=msg.toString();
			_log.debug("getVomsPrinterBatchByBatchID", message);
		}
		final String METHOD_NAME = "getVomsPrinterBatchByBatchID";
		PreparedStatement pselect = null;
		ResultSet rs = null;
		VomsPrintBatchVO printBatchVO = null;
		ArrayList segmentList = null;
		try {
			segmentList=LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true);
			String sqlSelectBuf = vomsBatchesWebQry
					.getVomsPrinterBatchByBatchIDQry(p_batchType,userVO);

			if (_log.isDebugEnabled()) {
				_log.debug("getVomsPrinterBatchForUser", "Select Query="
						+ sqlSelectBuf);
			}
			pselect = p_con.prepareStatement(sqlSelectBuf);
			
			int i = 1; 
			/*for (int i = 1; i <= 4; i++) {*/
				if (("Y".equals(p_batchType) || "N".equals(p_batchType))) {
					pselect.setString(i++, p_batchType);
				}
				pselect.setString(i++, userVO.getUserID());
				pselect.setString(i++, userVO.getNetworkID());
				if((PretupsI.USER_TYPE_CHANNEL).equals(userVO.getUserType()))
				{
					pselect.setString(i++, userVO.getUserID());
				}
				pselect.setString(i++, p_batchId);
			/*}*/
			rs = pselect.executeQuery();
			while (rs.next()) {
				printBatchVO = new VomsPrintBatchVO();
				printBatchVO.setPrintbatchID(rs.getString("printer_batch_id"));
				printBatchVO.setStartSerialNo(rs.getString("start_serial_no"));
				printBatchVO.setEndSerialNo(rs.getString("end_serial_no"));
				printBatchVO.setUserID(rs.getString("user_id"));
				printBatchVO.setIsDownloaded(rs.getString("downloaded"));
				printBatchVO.setProductID(rs.getString("product_id"));
				printBatchVO.setVomsDecryKey(rs.getString("voms_decryp_key"));
				printBatchVO.setTotNoOfVOuchers(rs
						.getLong("total_no_of_vouchers"));
				printBatchVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("created_on")));
				printBatchVO.setCreatedBy(rs.getString("created_by"));
				printBatchVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs
						.getTimestamp("modified_on")));
				printBatchVO.setModifiedBy(rs.getString("modified_by"));
				printBatchVO.setProductName(rs.getString("product_name"));
				printBatchVO.setMrp(PretupsBL.getDisplayAmount(rs
						.getDouble("mrp")));
				printBatchVO.setVoucherType(rs.getString("voucher_type"));
				printBatchVO.setVoucherSegment(rs.getString("voucher_segment"));
				printBatchVO.setVoucherSegmentDesc(BTSLUtil.getOptionDesc(rs.getString("voucher_segment"), segmentList).getLabel());
			}
			if (_log.isDebugEnabled()) {
				_log.debug("getVomsPrinterBatchByBatchID", "printBatchVO" + printBatchVO);
			}
			return printBatchVO;
		} catch (SQLException sqle) {
			_log.error("getVomsPrinterBatchForUser",
					"SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getVomsPrinterBatchForUser]", "", "", "",
					"Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, "getVomsPrinterBatchForUser",
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error("getVomsPrinterBatchForUser", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsVoucherDAO[getVomsPrinterBatchForUser]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "getVomsPrinterBatchForUser",
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error("getVomsPrinterBatchForUser",
						" Exception while closing rs ex=" + ex);
			}
			try {
				if (pselect != null) {
					pselect.close();
				}
			} catch (Exception ex) {
				_log.error("getVomsPrinterBatchForUser",
						" Exception while closing prepared statement ex=" + ex);
			}
		}
	}
	
}
