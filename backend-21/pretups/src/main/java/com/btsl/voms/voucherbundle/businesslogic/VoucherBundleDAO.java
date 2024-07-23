package com.btsl.voms.voucherbundle.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class VoucherBundleDAO {

	/**
	 * Commons Logging instance.
	 */
	private Log LOG = LogFactory.getLog(this.getClass().getName());

	public boolean isVoucherBundleNameExist(Connection pCon, VoucherBundleVO pVoucherBundleVO)
			throws BTSLBaseException {
		final String methodName = "isVoucherBundleNameExist";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" pBundleName=");
			loggerValue.append(pVoucherBundleVO.getBundleName());
			LOG.debug(methodName, loggerValue);
		}

		boolean existFlag = false;
		final StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT bundle_name FROM voms_bundle_master WHERE ");
		strBuff.append("upper(bundle_name) = upper(?) and status=? ");

		final String sqlSelect = strBuff.toString();

		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("QUERY sqlSelect=");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		try (PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {

			pstmt.setString(1, pVoucherBundleVO.getBundleName());
			pstmt.setString(2, PretupsI.STATUS_ACTIVE);
			try (ResultSet rs = pstmt.executeQuery();) {

				if (rs.next()) {
					existFlag = true;
				}

				return existFlag;
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception : ");
			loggerValue.append(sqe);
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[isVoucherBundleNameExist]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append("Exception : ");
			loggerValue.append(ex);
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[isVoucherBundleNameExist]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: existFlag=");
				loggerValue.append(existFlag);
				LOG.debug(methodName, loggerValue);
			}
		}
	}

	public boolean isPrefixIDExist(Connection pCon, VoucherBundleVO pVoucherBundleVO) throws BTSLBaseException {
		final String methodName = "isPrefixIDExist";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append(" pPrefixID=");
			loggerValue.append(pVoucherBundleVO.getPrefixID());
			LOG.debug(methodName, loggerValue);
		}

		boolean existFlag = false;
		final StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT bundle_prefix FROM voms_bundle_master WHERE ");
		strBuff.append(" bundle_prefix = ? and status=? ");

		final String sqlSelect = strBuff.toString();

		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("QUERY sqlSelect=");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

		try (PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {

			pstmt.setString(1, pVoucherBundleVO.getPrefixID());
			pstmt.setString(2, PretupsI.STATUS_ACTIVE);

			try (ResultSet rs = pstmt.executeQuery();) {

				if (rs.next()) {
					existFlag = true;
				}

				return existFlag;
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception : ");
			loggerValue.append(sqe);
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[isVoucherBundleNameExist]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append("Exception : ");
			loggerValue.append(ex);
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[isVoucherBundleNameExist]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: existFlag=");
				loggerValue.append(existFlag);
				LOG.debug(methodName, loggerValue);
			}
		}
	}

	public int addVoucherBundle(Connection pCon, VoucherBundleVO pVoucherBundleVO) throws BTSLBaseException {

		int insertCount = 0;
		final String methodName = "addVoucherBundle";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: pVoucherBundleVO= ");
			loggerValue.append(pVoucherBundleVO);
			LOG.debug(methodName, loggerValue);
		}
		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("INSERT INTO voms_bundle_master (voms_bundle_id,");
			strBuff.append("bundle_name,bundle_prefix,retail_price,last_bundle_sequence,created_on,created_by,");
			strBuff.append("modified_on,modified_by,status) values ");
			strBuff.append("(?,?,?,?,?,?,?,?,?,?)");

			final String insertQuery = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sqlInsert:");
				loggerValue.append(insertQuery);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmtInsert = pCon.prepareStatement(insertQuery);) {
				psmtInsert.setInt(1, Integer.parseInt(pVoucherBundleVO.getVomsBundleID()));
				psmtInsert.setString(2, pVoucherBundleVO.getBundleName());
				psmtInsert.setString(3, pVoucherBundleVO.getPrefixID());
				psmtInsert.setLong(4, PretupsBL.getSystemAmount(pVoucherBundleVO.getRetailPrice()));
				psmtInsert.setLong(5, pVoucherBundleVO.getLastBundleSequence());
				psmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(pVoucherBundleVO.getCreatedOn()));
				psmtInsert.setString(7, pVoucherBundleVO.getCreatedBy());
				psmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(pVoucherBundleVO.getModifiedOn()));
				psmtInsert.setString(9, pVoucherBundleVO.getModifiedBy());
				psmtInsert.setString(10, pVoucherBundleVO.getStatus());
				insertCount = psmtInsert.executeUpdate();
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[addVoucherBundle]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[addVoucherBundle]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: insertCount=");
				loggerValue.append(insertCount);
				LOG.debug(methodName, loggerValue);
			}
		} // end of finally

		return insertCount;
	}

	public int addVoucherBundleDetails(Connection pCon, VoucherBundleVO pVoucherBundleVO) throws BTSLBaseException {

		int insertCount = 0;
		final String methodName = "addVoucherBundleDetails";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: pVoucherBundleVO= ");
			loggerValue.append(pVoucherBundleVO);
			LOG.debug(methodName, loggerValue);
		}
		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("INSERT INTO voms_bundle_details (voms_bundle_detail_id,");
			strBuff.append("voms_bundle_id, voms_bundle_name, profile_id, quantity, created_on, created_by,");
			strBuff.append("modified_on,modified_by,status) values ");
			strBuff.append("(?,?,?,?,?,?,?,?,?,?)");

			final String insertQuery = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sqlInsert:");
				loggerValue.append(insertQuery);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmtInsert = pCon.prepareStatement(insertQuery);) {
				psmtInsert.setInt(1, Integer.parseInt(pVoucherBundleVO.getVomsBundleDetailID()));
				psmtInsert.setInt(2, Integer.parseInt(pVoucherBundleVO.getVomsBundleID()));
				psmtInsert.setString(3, pVoucherBundleVO.getBundleName());
				psmtInsert.setString(4, pVoucherBundleVO.getVoucherProfile());
				psmtInsert.setInt(5, pVoucherBundleVO.getQuantity());
				psmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(pVoucherBundleVO.getCreatedOn()));
				psmtInsert.setString(7, pVoucherBundleVO.getCreatedBy());
				psmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(pVoucherBundleVO.getModifiedOn()));
				psmtInsert.setString(9, pVoucherBundleVO.getModifiedBy());
				psmtInsert.setString(10, pVoucherBundleVO.getStatus());
				insertCount = psmtInsert.executeUpdate();
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[addVoucherBundleDetails]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[addVoucherBundleDetails]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: insertCount=");
				loggerValue.append(insertCount);
				LOG.debug(methodName, loggerValue);
			}
		} // end of finally

		return insertCount;
	}

	public String getProfileID(Connection pCon, String pProfile) throws BTSLBaseException {

		final String methodName = "getProfileID";
		StringBuilder loggerValue = new StringBuilder();
		ResultSet rs = null;
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, pProfile);
		}
		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("SELECT product_id ");
			strBuff.append("FROM voms_products ");
			strBuff.append("WHERE secondary_prefix_code=? ");

			final String query = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sql:");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmt = pCon.prepareStatement(query);) {
				psmt.setString(1, pProfile);
				rs = psmt.executeQuery();
				if (rs.next()) {
					return rs.getString("product_id");
				}
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getProfileID]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getProfileID]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public ArrayList getVomsBundleDetailIDs(Connection pCon, String pVomsBundleID) throws BTSLBaseException {

		final String methodName = "getVomsBundleDetailIDs";
		StringBuilder loggerValue = new StringBuilder();
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, pVomsBundleID);
		}

		try {

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("SELECT voms_bundle_detail_id ");
			strBuff.append("FROM voms_bundle_details ");
			strBuff.append("WHERE voms_bundle_id=? and status=? ");

			final String query = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sqlInsert:");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmt = pCon.prepareStatement(query);) {
				psmt.setInt(1, Integer.parseInt(pVomsBundleID));
				psmt.setString(2, PretupsI.STATUS_ACTIVE);
				rs = psmt.executeQuery();
				while (rs.next()) {
					list.add(rs.getInt("voms_bundle_detail_id"));
				}
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getVomsBundleDetailIDs]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getVomsBundleDetailIDs]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {
			try {
				rs.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}

		return list;
	}

	public ArrayList getVomsBundleDetail(Connection pCon, String pVomsBundleID) throws BTSLBaseException {

		final String methodName = "getVomsBundleDetail";
		StringBuilder loggerValue = new StringBuilder();
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, pVomsBundleID);
		}
		VoucherBundleVO voucherBundleVO = null;
		try {

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(
					"SELECT vbd.voms_bundle_detail_id,vbd.voms_bundle_name,vbd.profile_id,vbd.quantity,vbd.status,vbm.bundle_prefix,");
			strBuff.append("vp.product_name,vp.mrp, vbm.retail_price ");
			strBuff.append("FROM voms_bundle_details vbd, voms_bundle_master vbm, voms_products vp ");
			strBuff.append("WHERE vbd.voms_bundle_id=? and vbd.status=? and vbd.voms_bundle_id = vbm.voms_bundle_id ");
			strBuff.append("and vbd.profile_id = vp.product_id ");

			final String query = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sql:");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmt = pCon.prepareStatement(query);) {
				psmt.setInt(1, Integer.parseInt(pVomsBundleID));
				psmt.setString(2, PretupsI.STATUS_ACTIVE);
				rs = psmt.executeQuery();

				while (rs.next()) {
					voucherBundleVO = new VoucherBundleVO();
					voucherBundleVO.setBundleName(rs.getString("voms_bundle_name"));
					voucherBundleVO.setPrefixID(rs.getString("bundle_prefix"));
					voucherBundleVO.setVomsBundleDetailID(rs.getInt("voms_bundle_detail_id") + "");
					voucherBundleVO.setBundleName(rs.getString("voms_bundle_name"));
					voucherBundleVO.setVoucherProfile(rs.getString("profile_id"));
					voucherBundleVO.setQuantity(rs.getInt("quantity"));
					voucherBundleVO.setStatus(rs.getString("status"));
					voucherBundleVO.setDenomination(BTSLUtil.getDisplayAmount(rs.getDouble("mrp")) + "");
					voucherBundleVO.setCategoryName(rs.getString("product_name"));
					voucherBundleVO.setRetailPrice(BTSLUtil.getDisplayAmount(rs.getDouble("retail_price")));
					list.add(voucherBundleVO);
				}
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getVomsBundleDetail]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getVomsBundleDetail]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {
			try {
				rs.close();
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		}

		return list;
	}

	public ArrayList loadVoucherBundleDetails(Connection pCon, String pVomsBundleID) throws BTSLBaseException {

		final String methodName = "getVomsBundleDetail";
		StringBuilder loggerValue = new StringBuilder();
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, pVomsBundleID);
		}
		VoucherBundleVO voucherBundleVO = null;
		try {

			final StringBuilder strBuff = new StringBuilder();
			strBuff.append(
					"SELECT vbd.voms_bundle_detail_id, vbd.voms_bundle_name, vbm.bundle_prefix, vbd.profile_id, vbm.retail_price, l.lookup_name, ");
			strBuff.append("vbd.quantity, vbd.status, vp.product_name, vp.secondary_prefix_code, vp.mrp, vp.category_id, ");
			strBuff.append("vp.voucher_segment, vc.VOUCHER_TYPE, vc.CATEGORY_NAME, vbm.last_bundle_sequence ");
			strBuff.append(
					"FROM voms_bundle_master vbm, voms_bundle_details vbd, voms_products vp, VOMS_CATEGORIES vc, Lookups l ");
			strBuff.append("WHERE vbm.voms_bundle_id = vbd.voms_bundle_id ");
			strBuff.append("AND vbd.voms_bundle_id = ? ");
			strBuff.append("AND vp.product_id = vbd.profile_id ");
			strBuff.append("AND vc.CATEGORY_ID = vp.CATEGORY_ID ");
			strBuff.append("AND vp.VOUCHER_SEGMENT = l.LOOKUP_CODE ");
			strBuff.append("AND vbd.status = ? ");
			strBuff.append("AND vp.status = ? ");
			strBuff.append("AND vc.status = ? ");

			final String query = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sql:");
				loggerValue.append(query);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmt = pCon.prepareStatement(query);) {
				psmt.setInt(1, Integer.parseInt(pVomsBundleID));
				psmt.setString(2, PretupsI.STATUS_ACTIVE);
				psmt.setString(3, PretupsI.STATUS_ACTIVE);
				psmt.setString(4, PretupsI.STATUS_ACTIVE);

				rs = psmt.executeQuery();

				while (rs.next()) {
					voucherBundleVO = new VoucherBundleVO();
					voucherBundleVO.setBundleName(rs.getString("voms_bundle_name"));
					voucherBundleVO.setPrefixID(rs.getString("bundle_prefix"));
					voucherBundleVO.setVoucherProfile(rs.getString("secondary_prefix_code"));
					voucherBundleVO.setDescription(rs.getString("product_name"));
					voucherBundleVO.setRetailPrice(rs.getDouble("retail_price"));
					voucherBundleVO.setQuantity(rs.getInt("quantity"));
					voucherBundleVO.setVoucherType(rs.getString("voucher_type"));
					voucherBundleVO.setMrp(BTSLUtil.getDisplayAmount(rs.getDouble("mrp")));
					voucherBundleVO.setSegment(rs.getString("lookup_name"));
					voucherBundleVO
							.setDenomination(rs.getString("category_name") + "(" + voucherBundleVO.getMrp() + ")");
					voucherBundleVO.setVal(voucherBundleVO.getQuantity() * voucherBundleVO.getMrp());
					voucherBundleVO.setLastBundleSequence(rs.getLong("last_bundle_sequence"));
					list.add(voucherBundleVO);
				}
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getVomsBundleDetail]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[getVomsBundleDetail]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, " Exiting List size : " + list.size());
			}
		}
		return list;
	}

	public int updateVoucherBundle(Connection pCon, VoucherBundleVO pVoucherBundleVO) throws BTSLBaseException {
		int updateCount = 0;
		final String methodName = "updateVoucherBundle";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: pVoucherBundleVO= ");
			loggerValue.append(pVoucherBundleVO);
			LOG.debug(methodName, loggerValue);
		}
		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE voms_bundle_master ");
			strBuff.append("SET retail_price=?, ");
			strBuff.append("modified_on=?, modified_by=?, status=? ");
			strBuff.append("where voms_bundle_id=? ");

			final String updateQuery = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sql:");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =
			int i = 1;
			try (PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);) {
				psmtUpdate.setLong(i++, PretupsBL.getSystemAmount(pVoucherBundleVO.getRetailPrice()));
				psmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(pVoucherBundleVO.getModifiedOn()));
				psmtUpdate.setString(i++, pVoucherBundleVO.getModifiedBy());
				psmtUpdate.setString(i++, pVoucherBundleVO.getStatus());
				psmtUpdate.setInt(i++, Integer.parseInt(pVoucherBundleVO.getVomsBundleID()));
				updateCount = psmtUpdate.executeUpdate();
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[updateVoucherBundle]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[updateVoucherBundle]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: insertCount=");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		} // end of finally

		return updateCount;
	}

	public int updateVoucherBundleDetails(Connection pCon, VoucherBundleVO pVoucherBundleVO) throws BTSLBaseException {
		int updateCount = 0;
		final String methodName = "updateVoucherBundleDetails";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: pVoucherBundleVO= ");
			loggerValue.append(pVoucherBundleVO);
			LOG.debug(methodName, loggerValue);
		}
		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE voms_bundle_details ");
			strBuff.append("SET profile_id=?, quantity=?, ");
			strBuff.append("modified_on=?, modified_by=?, status=? ");
			strBuff.append("where voms_bundle_detail_id=? ");

			final String updateQuery = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sqlInsert:");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);) {
				psmtUpdate.setString(1, pVoucherBundleVO.getVoucherProfile());
				psmtUpdate.setInt(2, pVoucherBundleVO.getQuantity());
				psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pVoucherBundleVO.getModifiedOn()));
				psmtUpdate.setString(4, pVoucherBundleVO.getModifiedBy());
				psmtUpdate.setString(5, pVoucherBundleVO.getStatus());
				psmtUpdate.setInt(6, Integer.parseInt(pVoucherBundleVO.getVomsBundleDetailID()));
				updateCount = psmtUpdate.executeUpdate();
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[updateVoucherBundle]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[updateVoucherBundle]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: updateCount=");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		} // end of finally

		return updateCount;
	}

	public int updateVoucherBundleDetailsStatus(Connection pCon, VoucherBundleVO pVoucherBundleVO)
			throws BTSLBaseException {
		int updateCount = 0;
		final String methodName = "updateVoucherBundleDetailsStatus";
		StringBuilder loggerValue = new StringBuilder();
		if (LOG.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: pVoucherBundleVO= ");
			loggerValue.append(pVoucherBundleVO);
			LOG.debug(methodName, loggerValue);
		}
		try {
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE voms_bundle_details ");
			strBuff.append("SET ");
			strBuff.append("modified_on=?, modified_by=?, status=? ");
			strBuff.append("where voms_bundle_detail_id=? ");

			final String updateQuery = strBuff.toString();
			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query sqlInsert:");
				loggerValue.append(updateQuery);
				LOG.debug(methodName, loggerValue);
			}

			// commented for DB2 psmtInsert =

			try (PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);) {
				psmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(pVoucherBundleVO.getModifiedOn()));
				psmtUpdate.setString(2, pVoucherBundleVO.getModifiedBy());
				psmtUpdate.setString(3, pVoucherBundleVO.getStatus());
				psmtUpdate.setInt(4, Integer.parseInt(pVoucherBundleVO.getVomsBundleDetailID()));
				updateCount = psmtUpdate.executeUpdate();
			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception: ");
			loggerValue.append(sqle.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[updateVoucherBundleDetailsStatus]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception: ");
			loggerValue.append(e.getMessage());
			String logVal1 = loggerValue.toString();
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleDAO[updateVoucherBundleDetailsStatus]", "", "", "", logVal1);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {

			if (LOG.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting: updateCount=");
				loggerValue.append(updateCount);
				LOG.debug(methodName, loggerValue);
			}
		} // end of finally

		return updateCount;
	}
}
