package com.web.voms.voucherbundle.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucherbundle.businesslogic.VoucherBundleVO;

public class VoucherBundleWebDAO {

	/**
	 * Commons Logging instance.
	 */
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private VoucherBundleWebQry voucherBundleWebQry;

	public VoucherBundleWebDAO() {
		voucherBundleWebQry = (VoucherBundleWebQry) ObjectProducer.getObject(QueryConstants.VOUCHER_BUNDLE_WEB_QRY,
				QueryConstants.QUERY_PRODUCER);
	}

	/**
	 * Method: loadCategoryList This method is used for loading parent category
	 * list only
	 * 
	 * @author amit.singh
	 * @param p_con
	 *            java.sql.Connection
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 */

	public String fetchMrp(Connection p_con, String denomination_id) throws BTSLBaseException {
		final String methodName = "fetchMrp";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered.. ");
		}

		String mrp = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final StringBuilder strBuff = new StringBuilder(" SELECT mrp ");
		strBuff.append(" FROM voms_categories where category_id=? and status='Y' ");
		final String sqlSelect = strBuff.toString();

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}

		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, denomination_id);
			rs = pstmt.executeQuery();
			if (rs.next())
				mrp = rs.getString("mrp");
			int price = Integer.parseInt(mrp);
			price = price / 100;
			mrp = price + "";
		} catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error(methodName, "SQLException : " + sqe);
			}
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleWebDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleWebDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
		return mrp;
	}

	public ArrayList loadVoucherTypeList(Connection p_con) throws BTSLBaseException {
		final String methodName = "loadVoucherTypeList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered.. ");
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList list = null;
		VoucherBundleVO voucherBundleVO = null;

		final StringBuilder strBuff = new StringBuilder(" SELECT vt.voucher_type, vt.name, vt.status ");// ,
		// vvsm.sub_service
		strBuff.append(
				" FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm where vvsm.voucher_type=vt.voucher_type and");
		strBuff.append(" vt.status = ? group by vt.voucher_type, vt.name, vt.status ");// ORDER
		// BY

		final String sqlSelect = strBuff.toString();

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}

		list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, PretupsI.STATUS_ACTIVE);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				voucherBundleVO = new VoucherBundleVO();
				voucherBundleVO.setVoucherType(rs.getString("voucher_type"));
				voucherBundleVO.setName(rs.getString("name"));
				voucherBundleVO.setStatus(rs.getString("status"));
				list.add(voucherBundleVO);
			}
		} catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error(methodName, "SQLException : " + sqe);
			}
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleWebDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherBundleWebDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: List size=" + list.size());
			}
		}
		return list;
	}

	/**
	 * This method will load the List of both the Category or Subcategory based
	 * on the p_isSubCategory flag, If True It will load the Subcategory belong
	 * to the p_categoryId else it will load the Category List
	 * 
	 * @param p_con
	 * @param p_categoryId
	 * @param p_catagoryType
	 * @param p_status
	 * @param p_isSubCategory
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */

	public ArrayList loadSubCategoryListForView(Connection p_con, String p_categoryId, String voucherNetworkCode,
			String p_status, boolean p_isSubCategory, String p_segment) throws BTSLBaseException {
		String methodName = "loadSubCategoryListForView";
		if (_log.isDebugEnabled()) {
			StringBuffer msg = new StringBuffer("");
			msg.append("p_status= ");
			msg.append(p_status);
			msg.append(" p_isSubCategory= ");
			msg.append(p_isSubCategory);
			msg.append(" p_categoryId= ");
			msg.append(p_categoryId);
			msg.append(" p_segment= ");
			msg.append(p_segment);

			String message = msg.toString();
			_log.debug("loadSubCategoryListForView() Entered ::", message);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		VoucherBundleVO voucherBundleVO = null;
		ArrayList list = null;
		String query = voucherBundleWebQry.loadSubCategoryListForView(p_isSubCategory, p_segment);

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadSubCategoryListForView() of VomsCategoryVO::", " Query :: " + query);
			}

			pstmt = p_con.prepareStatement(query);
			int count = 1;
			if (p_isSubCategory) {
				pstmt.setString(count++, p_categoryId);
				pstmt.setString(count++, PretupsI.ALL);
				pstmt.setString(count++, p_categoryId);
			}
			pstmt.setString(count++, voucherNetworkCode);
			pstmt.setString(count++, p_status);
			pstmt.setString(count++, VOMSI.LOOKUP_PRODUCT_STATUS);
			if (!BTSLUtil.isNullString(p_segment)) {
				pstmt.setString(count++, p_segment);
			}
			rs = pstmt.executeQuery();
			list = new ArrayList();
			// initialize Category List

			while (rs.next()) {
				voucherBundleVO = new VoucherBundleVO();
				voucherBundleVO.setCategoryID(rs.getString("category_id") + ":" + rs.getString("type"));
				voucherBundleVO.setCategoryName(
						rs.getString("category_name") + "(" + (BTSLUtil.getDisplayAmount(rs.getDouble("mrp"))) + ")");
				voucherBundleVO.setCategoryShortName(rs.getString("category_short_name"));
				ListValueVO listValueVO = new ListValueVO(voucherBundleVO.getCategoryName(),
						voucherBundleVO.getCategoryID());
				list.add(listValueVO);
			}
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsCategoryDAO[loadSubCategoryListForView]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsCategoryDAO[loadSubCategoryListForView]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: List size=" + list.size());
			}
		}
		return list;
	}

	/**
	 * Method: loadUserCategoryList This method is used for loading parent
	 * category list only
	 * 
	 * @author amit.singh
	 * @param p_con
	 *            java.sql.Connection
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadUserCategoryList(Connection p_con, String p_userid) throws BTSLBaseException {
		final String methodName = "loadCategoryList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered.. ");
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList list = null;
		VomsCategoryVO vomsCategoryVO = null;

		String sqlSelect = voucherBundleWebQry.loadUserCategoryList(p_userid);

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}

		list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_userid);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				vomsCategoryVO = new VomsCategoryVO();
				vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
				vomsCategoryVO.setName(rs.getString("name"));
				vomsCategoryVO.setStatus(rs.getString("status"));
				list.add(vomsCategoryVO);
			}
		} catch (SQLException sqe) {
			if (_log.isErrorEnabled()) {
				_log.error(methodName, "SQLException : " + sqe);
			}
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsCategoryDAO[loadCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsCategoryDAO[loadCategoryList]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: List size=" + list.size());
			}
		}
		return list;
	}

	public ArrayList loadVoucherBundleListForView(Connection p_con) throws BTSLBaseException {
		String methodName = "loadVoucherBundleListForView";
		if (_log.isDebugEnabled()) {
			_log.debug("loadVoucherBundleListForView()", "Entered");
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		VoucherBundleVO voucherBundleVO = null;
		ArrayList list = null;
		final StringBuilder strBuff = new StringBuilder(
				"SELECT vbm.voms_bundle_id, vbm.bundle_name, vbm.bundle_prefix, vbm.retail_price, vbm.status ");
		strBuff.append("FROM voms_bundle_master vbm where vbm.status=? ORDER BY vbm.voms_bundle_id ");
		String query = strBuff.toString();

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(" loadVoucherBundleListForView() of VoucherBundleVO::", " Query :: " + query);
			}

			pstmt = p_con.prepareStatement(query);
			pstmt.setString(1, PretupsI.STATUS_ACTIVE);
			rs = pstmt.executeQuery();
			list = new ArrayList();

			while (rs.next()) {
				voucherBundleVO = new VoucherBundleVO();
				voucherBundleVO.setVomsBundleID(rs.getInt("voms_bundle_id") + "");
				voucherBundleVO.setBundleName(rs.getString("bundle_name"));
				voucherBundleVO.setPrefixID(rs.getString("bundle_prefix"));
				voucherBundleVO.setRetailPrice(rs.getDouble("retail_price") / 100);
				voucherBundleVO.setStatus(rs.getString("status"));
				list.add(voucherBundleVO);
			}
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsCategoryDAO[loadSubCategoryListForView]", "", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VomsCategoryDAO[loadSubCategoryListForView]", "", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: List size=" + list.size());
			}
		}
		return list;
	}

}
