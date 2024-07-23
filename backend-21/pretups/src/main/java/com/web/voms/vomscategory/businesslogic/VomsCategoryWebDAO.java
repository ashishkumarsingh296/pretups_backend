package com.web.voms.vomscategory.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVO;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVoucherVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VomsCategoryWebDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private VomsCategoryWebQry vomsCategoryWebQry;
    public VomsCategoryWebDAO(){
    	vomsCategoryWebQry = (VomsCategoryWebQry)ObjectProducer.getObject(QueryConstants.VOMS_CATEGORY_WEB_QRY, QueryConstants.QUERY_PRODUCER);

    }
    /**
     * Method: loadCategoryList
     * This method is used for loading parent category list only
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        final StringBuilder strBuff = new StringBuilder(" SELECT vt.voucher_type, vt.name, vt.status, vt.type ");// ,
        // vvsm.sub_service
        strBuff.append(" FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm where vvsm.voucher_type=vt.voucher_type and");
        strBuff.append(" vt.status = ? group by vt.voucher_type, vt.name, vt.status, vt.type ");// ORDER
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
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setName(rs.getString("name"));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setType(rs.getString("type"));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * Method: isExistName
     * This method is used for checking the existance of category name
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_vomsCategoryVO
     *            VomsCategoryVO
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isExistName(Connection p_con, VomsCategoryVO p_vomsCategoryVO) throws BTSLBaseException {
        final String methodName = "isExistName";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_vomsCategoryVO=" + p_vomsCategoryVO);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;

        final StringBuilder sqlSelect = new StringBuilder("SELECT 1 FROM voms_categories WHERE UPPER(category_name)=UPPER(?) AND network_code = ? ");
        // if the this method is used for addition of a new sub category
        // then no need to check this condition and if the this method
        // is used for modifying an existing sub category then this
        // condition is needed
        if (!BTSLUtil.isNullString(p_vomsCategoryVO.getCategoryID())) {
            sqlSelect.append(" AND category_id <> ? ");
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect.toString());
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect.toString());
            pstmt.setString(1, p_vomsCategoryVO.getCategoryName());
            pstmt.setString(2, p_vomsCategoryVO.getNetworkCode());
            if (!BTSLUtil.isNullString(p_vomsCategoryVO.getCategoryID())) {
                pstmt.setString(3, p_vomsCategoryVO.getCategoryID());
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isExistName]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isExistName]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method: isExistShortName
     * This method is used for checking the existance of category short name
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_vomsCategoryVO
     *            VomsCategoryVO
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isExistShortName(Connection p_con, VomsCategoryVO p_vomsCategoryVO) throws BTSLBaseException {
        final String methodName = "isExistShortName";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_vomsCategoryVO=" + p_vomsCategoryVO);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;

        final StringBuilder sqlSelect = new StringBuilder("SELECT 1 FROM voms_categories WHERE UPPER(category_short_name)=UPPER(?) AND network_code = ? ");
        // if the this method is used for addition of a new sub category
        // then no need to check this condition and if the this method
        // is used for modifying an existing sub category then this
        // condition is needed
        if (!BTSLUtil.isNullString(p_vomsCategoryVO.getCategoryID())) {
            sqlSelect.append(" AND category_id <> ? ");
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect.toString());
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect.toString());
            pstmt.setString(1, p_vomsCategoryVO.getCategoryShortName());
            pstmt.setString(2, p_vomsCategoryVO.getNetworkCode());
            if (!BTSLUtil.isNullString(p_vomsCategoryVO.getCategoryID())) {
                pstmt.setString(3, p_vomsCategoryVO.getCategoryID());
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isExistShortName]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isExistShortName]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method: isExistMrp
     * This method is used for checking the existance of MRP value
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_vomsCategoryVO
     *            VomsCategoryVO
     * @return existFlag boolean
     * @throws BTSLBaseException
     */
    public boolean isExistMrp(Connection p_con, VomsCategoryVO p_vomsCategoryVO) throws BTSLBaseException {
        final String methodName = "isExistMrp";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_vomsCategoryVO=" + p_vomsCategoryVO);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;

        final StringBuilder sqlSelect = new StringBuilder("SELECT 1 FROM voms_categories WHERE voucher_type=? AND mrp=? AND type=? AND network_code=? AND voucher_segment = ?");
//        final StringBuilder sqlSelect = new StringBuilder("SELECT 1 FROM voms_categories WHERE voucher_type=? AND mrp=? AND type=? AND network_code=? ");
        // if the this method is used for addition of a new sub category
        // then no need to check this condition and if the this method
        // is used for modifying an existing sub category then this
        // condition is needed
        if (!BTSLUtil.isNullString(p_vomsCategoryVO.getCategoryID())) {
            sqlSelect.append(" AND category_id <> ? ");
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect.toString());
        }
        try {
        	int i = 1;
            pstmt = p_con.prepareStatement(sqlSelect.toString());
            pstmt.setString(i++, p_vomsCategoryVO.getVoucherType());
            pstmt.setLong(i++, PretupsBL.getSystemAmount(p_vomsCategoryVO.getMrp()));
            pstmt.setString(i++, p_vomsCategoryVO.getType());
            pstmt.setString(i++, p_vomsCategoryVO.getNetworkCode());
            pstmt.setString(i++, p_vomsCategoryVO.getSegment());
            if (!BTSLUtil.isNullString(p_vomsCategoryVO.getCategoryID())) {
                pstmt.setString(i++, p_vomsCategoryVO.getCategoryID());
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isExistMrp]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isExistMrp]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method: addSubCategory
     * Method for adding the new subcategory under a parent category.
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_vomsCategoryVO
     *            VomsCategoryVO
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addSubCategory(Connection p_con, VomsCategoryVO p_vomsCategoryVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;

        int insertCount = 0;

        final String methodName = "addSubCategory";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_vomsCategoryVO= " + p_vomsCategoryVO);
        }

        try {
            final StringBuilder strBuff = new StringBuilder("INSERT INTO voms_categories (category_id,category_name,description,");
            strBuff.append(" category_type,category_short_name,mrp,status,global, created_by, created_on,");
            strBuff.append(" modified_by, modified_on, payable_amount,type,voucher_type,service_id,network_code, voucher_segment) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            psmtInsert = p_con.prepareStatement(insertQuery);

            psmtInsert.setString(1, p_vomsCategoryVO.getCategoryID());

            psmtInsert.setString(2, p_vomsCategoryVO.getCategoryName());

            psmtInsert.setString(3, p_vomsCategoryVO.getDescription());

            psmtInsert.setString(4, p_vomsCategoryVO.getCategoryType());

            psmtInsert.setString(5, p_vomsCategoryVO.getCategoryShortName());

            psmtInsert.setLong(6, PretupsBL.getSystemAmount(p_vomsCategoryVO.getMrp()));
            psmtInsert.setString(7, p_vomsCategoryVO.getStatus());
            psmtInsert.setString(8, p_vomsCategoryVO.getGlobal());
            psmtInsert.setString(9, p_vomsCategoryVO.getCreatedBy());
            psmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_vomsCategoryVO.getCreatedOn()));
            psmtInsert.setString(11, p_vomsCategoryVO.getModifiedBy());
            psmtInsert.setTimestamp(12, BTSLUtil.getTimestampFromUtilDate(p_vomsCategoryVO.getModifiedOn()));
            psmtInsert.setLong(13, PretupsBL.getSystemAmount(p_vomsCategoryVO.getPayAmount()));
            psmtInsert.setString(14, p_vomsCategoryVO.getType());
            psmtInsert.setString(15, p_vomsCategoryVO.getVoucherType());
            psmtInsert.setInt(16, p_vomsCategoryVO.getServiceID());
            psmtInsert.setString(17, p_vomsCategoryVO.getNetworkCode());
            psmtInsert.setString(18, p_vomsCategoryVO.getSegment());
            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[addSubCategory]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[addSubCategory]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method: loadCategoryList
     * This method is used for loading sub category list(Product price list)
     * on the basis of voucher type, status and type of the category
     * 
     * @author Ashutosh
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * @param p_categoryType
     *            String
     * @param networkCode TODO
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryList(Connection p_con, String voucherType,String p_status, String p_categoryType, boolean p_activereqd, String networkCode, String segment) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. p_status=" + p_status + " p_categoryType=" + p_categoryType);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        boolean isActiveProfReqd = false;
        VomsCategoryVO vomsCategoryVO = null;
        ArrayList<String> allowedVoucherType = new ArrayList<String>(Arrays.asList(VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_VOUCHER_ACTIVE_PROFILE)));
        String type = (new VomsProductDAO()).getTypeFromVoucherType(p_con, voucherType);
        if(allowedVoucherType!=null){
        if(allowedVoucherType.contains(type)){
        	isActiveProfReqd=true;
        }
        }
        final StringBuilder strBuff = new StringBuilder(
            " SELECT distinct VC.category_id,VC.description,VC.category_type,VC.category_short_name,VC.mrp,VC.status,VC.VOUCHER_TYPE,");
        strBuff.append(" VC.category_name,VC.payable_amount,VC.type FROM voms_categories VC");
        if (p_activereqd) {
            strBuff.append(",voms_products VP");
			if(isActiveProfReqd && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_ACTIVATION_REQ)).booleanValue())
            {
            	strBuff.append(",voms_active_product_items VAI");
            }
        }
        strBuff.append("  WHERE VC.VOUCHER_TYPE = ? AND VC.network_code = ? AND");
        strBuff.append("  VC.category_type = ? AND (VC.status = ? OR VC.status IS NULL) ");
        if(!BTSLUtil.isNullString(segment)) {
        	strBuff.append(" AND VC.voucher_segment = ? ");
        }

        if (p_activereqd) {
            strBuff.append(" AND VC.category_id=VP.category_id");
            if(isActiveProfReqd && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_ACTIVATION_REQ)).booleanValue())
            {
            	strBuff.append(" AND VP.product_id=VAI.product_id");
            }
        }
        strBuff.append("  ORDER BY VC.mrp");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, voucherType);
            pstmt.setString(2, networkCode);
            pstmt.setString(3, p_categoryType);
            pstmt.setString(4, p_status);
            if(!BTSLUtil.isNullString(segment)) {
            	pstmt.setString(5, segment);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));

                // Added by Anjali
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("type"));
                vomsCategoryVO.setType(rs.getString("VOUCHER_TYPE"));
                // End

                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * 
     * @param p_con
     * @param p_status
     * @param p_categoryType
     * @param p_activereqd
     * @param networkCode
     * @return
     * @throws BTSLBaseException
     */
    @SuppressWarnings("unchecked")
	public ArrayList loadCategoryListForAllVoucherTypes(Connection p_con,String p_status, String p_categoryType, boolean p_activereqd, String networkCode) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. p_status=" + p_status + " p_categoryType=" + p_categoryType);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        boolean isActiveProfReqd = false;
        VomsCategoryVO vomsCategoryVO = null;
        ArrayList<String> allowedVoucherType = new ArrayList<String>(Arrays.asList(VomsUtil.getAllowedVoucherTypesForScreen(PretupsI.SCREEN_VOUCHER_ACTIVE_PROFILE)));
        if(allowedVoucherType!=null){
      
        }
        final StringBuilder strBuff = new StringBuilder(
            " SELECT distinct VC.category_id,VC.description,VC.category_type,VC.category_short_name,VC.mrp,VC.status,VC.VOUCHER_TYPE,");
        strBuff.append(" VC.category_name,VC.payable_amount,VC.type, VC.VOUCHER_SEGMENT FROM voms_categories VC");
        if (p_activereqd) {
            strBuff.append(",voms_products VP");
			if(isActiveProfReqd && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_ACTIVATION_REQ)).booleanValue())
            {
            	strBuff.append(",voms_active_product_items VAI");
            }
        }
        strBuff.append("  WHERE VC.network_code = ? AND");
        strBuff.append("  VC.category_type = ? AND (VC.status = ? OR VC.status IS NULL) ");


        if (p_activereqd) {
            strBuff.append(" AND VC.category_id=VP.category_id");
            if(isActiveProfReqd && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_ACTIVATION_REQ)).booleanValue())
            {
            	strBuff.append(" AND VP.product_id=VAI.product_id");
            }
        }
        strBuff.append("  ORDER BY VC.mrp");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
        	int i =1;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(i, networkCode);
            i++;
            pstmt.setString(i, p_categoryType);
            i++;
            pstmt.setString(i, p_status);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));

                // Added by Anjali
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("type"));
                vomsCategoryVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
                vomsCategoryVO.setSegment(rs.getString("VOUCHER_SEGMENT"));
                // End

                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
 * 
 * @param p_con
 * @param p_status
 * @param p_categoryType
 * @param p_activereqd
 * @param networkCode
 * @return
 * @throws BTSLBaseException
 */
    public ArrayList loadVoucherList(Connection p_con ,String p_status, String p_categoryType, boolean p_activereqd, String networkCode) throws BTSLBaseException {
        final String methodName = "loadVoucherList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. p_status=" + p_status + " p_categoryType=" + p_categoryType);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        final StringBuilder strBuff = new StringBuilder(
            " SELECT distinct VC.category_id,VC.voucher_segment,VC.voucher_type,VC.description,VC.category_type,VC.category_short_name,VC.mrp,VC.status,VC.VOUCHER_TYPE,");
        strBuff.append(" VC.category_name,VC.payable_amount,VC.type FROM voms_categories VC");
        strBuff.append(",voms_products VP");
		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_ACTIVATION_REQ)).booleanValue())
        {
           strBuff.append(",voms_active_product_items VAI");
        }
       
        strBuff.append("  WHERE VC.network_code = ? AND");
        strBuff.append("  VC.category_type = ? AND (VC.status = ? OR VC.status IS NULL) ");
       
        if (p_activereqd) {
            strBuff.append(" AND VC.category_id=VP.category_id");
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PROFILE_ACTIVATION_REQ)).booleanValue())
            {
            	strBuff.append(" AND VP.product_id=VAI.product_id");
            }
        }
        strBuff.append("  ORDER BY VC.mrp");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int i =1;
        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(i++, networkCode);
            pstmt.setString(i++, p_categoryType);
            pstmt.setString(i++, p_status);
          

            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setSegment(rs.getString("voucher_segment"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));

                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("type"));
                vomsCategoryVO.setType(rs.getString("VOUCHER_TYPE"));

                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * on the
     * p_isSubCategory flag, If True It will load the Subcategory belong to the
     * p_categoryId
     * else it will load the Category List
     * 
     * @param p_con
     * @param p_categoryId
     * @param p_catagoryType
     * @param p_status
     * @param p_isSubCategory
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadSubCategoryListForView(Connection p_con, String p_categoryId, String p_catagoryType,String voucherNetworkCode, String p_status, 
    		boolean p_isSubCategory, String p_segment) throws BTSLBaseException {
    	 String methodName = "loadSubCategoryListForView";
    	if (_log.isDebugEnabled()) {
    		StringBuffer msg=new StringBuffer("");
        	msg.append("p_status= ");
        	msg.append(p_status);
        	msg.append(" p_catagoryType= ");
        	msg.append(p_catagoryType);
        	msg.append(" p_isSubCategory= ");
        	msg.append(p_isSubCategory);
        	msg.append(" p_categoryId= ");
        	msg.append(p_categoryId);
        	msg.append(" p_segment= ");
        	msg.append(p_segment);
        	
        	String message=msg.toString();
            _log.debug("loadSubCategoryListForView() Entered ::",message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        VomsCategoryVO vomsCategoryVO = null;
        ArrayList list = null;
        String query=vomsCategoryWebQry.loadSubCategoryListForView(p_isSubCategory, p_segment);
      
       
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
            pstmt.setString(count++, p_catagoryType);
            pstmt.setString(count++, p_status);
            pstmt.setString(count++, VOMSI.LOOKUP_PRODUCT_STATUS);
            if(!BTSLUtil.isNullString(p_segment)) {
            	pstmt.setString(count++, p_segment);            	
            }
            rs = pstmt.executeQuery();
            list = new ArrayList();
            // initialize Category List

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setCategoryID(rs.getString("category_id") + ":" + rs.getString("type"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setMrp(rs.getDouble("mrp"));
                vomsCategoryVO.setMrpDesc(PretupsBL.getDisplayAmount( vomsCategoryVO.getMrp()));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setStatus(rs.getString("lookup_name"));
                vomsCategoryVO.setGlobal(rs.getString("global"));
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setPayAmount(rs.getDouble("payable_amount"));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount( vomsCategoryVO.getPayAmount()));
                vomsCategoryVO.setServiceType(rs.getString("SERVICE_TYPE"));
                vomsCategoryVO.setServiceName(rs.getString("name"));

                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadSubCategoryListForView]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadSubCategoryListForView]", "", "",
                "", "Exception:" + ex.getMessage());
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
     * Method: loadSubCategory
     * This method is used for loading Subcategory list
     * 
     * @author nitin.rohilla
     * @param p_con
     *            java.sql.Connection,p_category VomsCategoryVO
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadSubCategory(Connection p_con, VomsCategoryVO p_category) throws BTSLBaseException {
        final String methodName = "loadSubCategory";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT category_id, category_name, category_type, description,");
        strBuff.append(" category_short_name, mrp, status, global,payable_amount,type,voucher_type,network_code,voucher_segment FROM voms_categories WHERE");
        strBuff.append(" network_code = ? AND voucher_type IN ('" + p_category.getVoucherType() + "') AND voucher_segment = ?");
        strBuff.append("  AND category_type IN ('" + p_category.getCategoryType() + "')");
        strBuff.append(" AND (status='Y' OR status is Null) order by upper(category_name)");
        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_category.getNetworkCode());
            pstmt.setString(2, p_category.getSegment());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setCategoryID(rs.getString("category_id") + ":" + rs.getString("type"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setCategoryType(rs.getString("category_type"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setMrp(rs.getDouble("mrp"));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setGlobal(rs.getString("global"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setDescription(rs.getString("voucher_type"));
                vomsCategoryVO.setNetworkCode(rs.getString("network_code"));
                vomsCategoryVO.setSegment(rs.getString("voucher_segment"));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadSubCategory]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadSubCategory]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * Method: loadCategoryDetails
     * This method is used for loading Modification Details
     * 
     * @author nitin.rohilla
     * @param p_con
     *            java.sql.Connection,p_categoryID String
     * @return VomsCategoryVO
     * @throws BTSLBaseException
     */
    public VomsCategoryVO loadCategoryDetails(Connection p_con, String p_categoryID,String networkCode) throws BTSLBaseException {
        final String methodName = "loadCategoryDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        VomsCategoryVO vomscategoryVO = null;
        final String sqlSelect = new String(
            " SELECT category_short_name,mrp,category_name,description,modified_on,payable_amount,type,voucher_type,network_code FROM voms_categories WHERE category_ID = ? AND network_code = ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryID);
            pstmt.setString(2, networkCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                vomscategoryVO = new VomsCategoryVO();
                vomscategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomscategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomscategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomscategoryVO.setCategoryName(rs.getString("category_name"));
                vomscategoryVO.setDescription(rs.getString("description"));
                vomscategoryVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                vomscategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomscategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));
                vomscategoryVO.setType(rs.getString("type"));
                vomscategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomscategoryVO.setNetworkCode(rs.getString("network_code"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryDetails]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: ");
            }
        }
        return vomscategoryVO;
    }

    /**
     * Method: modifyCategory
     * This method is used for database updation for modification
     * 
     * @author nitin.rohilla
     * @param p_con
     *            java.sql.Connection,p_vomscategoryVO VomsCategoryVO
     * @return int
     * @throws BTSLBaseException
     */
    public int modifyCategory(Connection p_con, VomsCategoryVO p_vomscategoryVO) throws BTSLBaseException {
        final String methodName = "modifyCategory";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }
        PreparedStatement pstmt = null;
        int recordUpdated = 0;

        final StringBuilder strBuff = new StringBuilder("UPDATE voms_categories SET category_name=? , description=? ,category_short_name=?");
        strBuff.append(",mrp=?,modified_by=?, modified_on=?, payable_amount=? ");
        strBuff.append(" WHERE category_id=? AND network_code = ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            final boolean modified = this.recordModified(p_con, p_vomscategoryVO.getCategoryID(), p_vomscategoryVO.getModifiedOn().getTime());
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_vomscategoryVO.getCategoryName());
            pstmt.setString(2, p_vomscategoryVO.getDescription());
            pstmt.setString(3, p_vomscategoryVO.getCategoryShortName());
            pstmt.setDouble(4, PretupsBL.getSystemAmount(p_vomscategoryVO.getMrp()));
            pstmt.setString(5, p_vomscategoryVO.getModifiedBy());
            pstmt.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(new Date()));
            pstmt.setDouble(7, PretupsBL.getSystemAmount(p_vomscategoryVO.getPayAmount()));
            pstmt.setString(8, p_vomscategoryVO.getCategoryID());
            pstmt.setString(9, p_vomscategoryVO.getNetworkCode());

            recordUpdated = pstmt.executeUpdate();

        } catch (BTSLBaseException be) {
            _log.error("updateUser", "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[modifyCategory]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[modifyCategory]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: ");
            }
        }
        return recordUpdated;
    }

    /**
     * Method: isMRPAssociatedWithProfileForMdfy
     * This method is used for checking if the mrp is associated with any
     * Profile or not.
     * 
     * @author nitin.rohilla
     * @param p_con
     *            java.sql.Connection,p_categoryID String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isMRPAssociatedWithProfileForMdfy(Connection p_con, String p_categoryID) throws BTSLBaseException {
        final String methodName = "isMRPAssociatedWithProfileForMdfy";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }
        boolean profileAssociation = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = new String(" SELECT 1 FROM voms_products WHERE category_id =? AND status<> ?");

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryID);
            pstmt.setString(2, PretupsI.STATUS_DELETE);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                profileAssociation = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isMRPAssociatedWithProfileForMdfy]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[isMRPAssociatedWithProfileForMdfy]",
                "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: profileAssociation =" + profileAssociation);
            }
        }
        return profileAssociation;
    }

    /**
     * 
     * @param p_con
     * @param p_status
     * @param p_categoryType
     * @param p_activereqd
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryListForEnable(Connection p_con, String p_status, String p_categoryType, String vtype, String segment,String networkCode) throws BTSLBaseException {
        final String methodName = "loadCategoryListForEnable";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered.. p_status= ");
        	msg.append(p_status);
        	msg.append(" p_categoryType= ");
        	msg.append(p_categoryType);
        	msg.append(" voucher type= ");
        	msg.append(vtype);
        	msg.append(" segment= ");
        	msg.append(segment);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        String tablename = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;
        try {

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                final boolean matchFound = BTSLUtil.validateTableName(vtype);
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vtype + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }

            final StringBuilder strBuff = new StringBuilder(" SELECT distinct VC.MRP, L.LOOKUP_NAME ");
            strBuff.append(" FROM VOMS_CATEGORIES VC,VOMS_PRODUCTS VP," + tablename + " VO, LOOKUPS L");
            strBuff.append(" WHERE VC.CATEGORY_TYPE=? AND VC.CATEGORY_ID=VP.CATEGORY_ID");
            strBuff.append(" AND VP.PRODUCT_ID=VO.PRODUCT_ID  AND VC.STATUS=? AND VC.VOUCHER_TYPE =? AND VC.VOUCHER_SEGMENT =? ");
            strBuff.append(" AND L.LOOKUP_CODE = VC.VOUCHER_SEGMENT AND VC.network_code = ? ");
            

            final String sqlSelect = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadCategoryList", "QUERY sqlSelect=" + sqlSelect);
            }

            list = new ArrayList();

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryType);
            pstmt.setString(2, p_status);
            pstmt.setString(3, vtype);
            pstmt.setString(4, segment);
            pstmt.setString(5, networkCode);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setSegmentDesc(rs.getString("lookup_name"));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * 
     * @param p_con
     * @param product_ID
     * @return
     * @throws BTSLBaseException
     */
    public long loadNoOfVouchers(Connection p_con, String product_ID, String vtype) throws BTSLBaseException {
        final String methodName = "loadNoOfVouchers";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. Product_ID=" + product_ID);
        }
        String tablename = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long noOfVouchers = 0;
        final VomsCategoryVO vomsCategoryVO = null;
        try {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                final boolean matchFound = BTSLUtil.validateTableName(vtype);
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vtype + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }
            final StringBuilder strBuff = new StringBuilder(" SELECT COUNT(PRODUCT_ID) AS TOTAL ");
            strBuff.append(" FROM " + tablename + " ");
            strBuff.append(" WHERE PRODUCT_ID=? AND  CURRENT_STATUS=? ");

            final String sqlSelect = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadNoOfVouchers", "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, product_ID);
            pstmt.setString(2, VOMSI.VOUCHER_NEW);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                noOfVouchers = rs.getLong("TOTAL");
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: noOfVouchers=" + noOfVouchers);
            }
        }
        return noOfVouchers;
    }

    /**
     * 
     * @param p_con
     * @param p_status
     * @param p_categoryType
     * @param p_activereqd
     * @return
     * @throws BTSLBaseException
     */
    public long loadSerialno(Connection p_con, String product_ID, String vtype) throws BTSLBaseException {
        final String methodName = "loadSerialno";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. product_ID=" + product_ID);
        }
        String tablename = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long Number = 0;
        final VomsCategoryVO vomsCategoryVO = null;
        try {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                final boolean matchFound = BTSLUtil.validateTableName(vtype);
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vtype + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }
            final StringBuilder strBuff = new StringBuilder(" ");
            strBuff.append(" SELECT  MIN(SERIAL_NO) AS");
            strBuff.append(" SEQ FROM " + tablename + "");
            strBuff.append(" WHERE PRODUCT_ID=? AND CURRENT_STATUS=?");
            final String sqlSelect = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadSerialno", "QUERY sqlSelect=" + sqlSelect);
            }

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, product_ID);
            pstmt.setString(2, VOMSI.VOUCHER_NEW);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Number = rs.getLong("SEQ");

            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: min seq no=" + Number);
            }
        }
        return Number;
    }

    /**
     * 
     * @param p_con
     * @param p_status
     * @param p_categoryType
     * @param p_activereqd
     * @return
     * @throws BTSLBaseException
     */
    public int updateStatus(Connection p_con, String status, String Product_ID, String Start_no, String End_no, String bach_no, String vtype,String seqid) throws BTSLBaseException {
        final String methodName = "updateStatus";
        if (_log.isDebugEnabled()) {
        	StringBuilder sb=new StringBuilder("");
        	sb.append("Entered.. start_no="); 
        	sb.append(Start_no);
        	sb.append(" end_no=");
        	sb.append(End_no);
        	sb.append(" seqid=");
        	sb.append(seqid); 
        	sb.append(" Product_ID=");
        	sb.append(Product_ID);
        	sb.append(" status=");
        	sb.append(status);
        	sb.append(" bach_no=");
        	sb.append(bach_no);
        	sb.append(" vtype=");
        	sb.append(vtype);
        	String msg = sb.toString();
            _log.debug(methodName,msg);
        }
        String tablename = null;
        PreparedStatement pstmt = null;
        int update_count = 0;
        final long Number = 0;
        final VomsCategoryVO vomsCategoryVO = null;
        try {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE))).booleanValue()) {
                final boolean matchFound = BTSLUtil.validateTableName(vtype);
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vtype + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }
            final StringBuilder strBuff = new StringBuilder(" ");
            strBuff.append(" UPDATE " + tablename + "");
            strBuff.append(" SET CURRENT_STATUS=?,status=?,SALE_BATCH_NO=?,MODIFIED_ON=?");
            strBuff.append("WHERE PRODUCT_ID=? AND SERIAL_NO BETWEEN  ? AND ?  ");
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
            	strBuff.append(" AND Sequence_id=? ");
            }
            final String sqlSelect = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, status);
            pstmt.setString(2, status); // gaurav
            pstmt.setString(3, bach_no);
            pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            pstmt.setString(5, Product_ID);
            pstmt.setString(6, Start_no);
            pstmt.setString(7, End_no);
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
            pstmt.setInt(8, Integer.parseInt(seqid));
            }
            update_count = pstmt.executeUpdate();

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            String arr[]=null;
            throw new BTSLBaseException("voms.download.batch.error.update.status", arr, "enablevoucher");
           
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSerialno", "error.general.processing");
        } finally {

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: update count=" + update_count);
            }
        }
        return update_count;
    }

    public int insertBatchPrintDetails(Connection p_con, VomsBatchVO vomsBatchVO) {
        final String methodName = "insertBatchPrintDetails";
        if (_log.isDebugEnabled()) {

            _log.debug(methodName, "Entered.. batch_id" + vomsBatchVO);
        }

        PreparedStatement pstmt = null;
        final ResultSet rs = null;
        final VomsCategoryVO vomsCategoryVO = null;
        final Date date = new Date();
        int insert_count = 0;
        final StringBuilder strBuff = new StringBuilder(" ");
        strBuff.append(" INSERT INTO VOMS_PRINT_BATCHES");
        strBuff.append(" (PRINTER_BATCH_ID,START_SERIAL_NO,END_SERIAL_NO,DOWNLOADED,PRODUCT_ID,TOTAL_NO_OF_VOUCHERS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY)");
        strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?) ");
        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadCategoryList", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, vomsBatchVO.getBatchNo());
            pstmt.setString(2, vomsBatchVO.getFromSerialNo());
            pstmt.setString(3, vomsBatchVO.getToSerialNo());
            pstmt.setString(4, PretupsI.NO);
            pstmt.setString(5, vomsBatchVO.getProductid());
            pstmt.setInt(6, BTSLUtil.parseLongToInt( vomsBatchVO.getNoOfVoucher()) );
            
            pstmt.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getCreatedDate()));
            pstmt.setString(8, PretupsI.SYSTEM);
            pstmt.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getCreatedDate()));
            pstmt.setString(10, PretupsI.SYSTEM);

            insert_count = pstmt.executeUpdate();

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());

        } catch (Exception ex) {
            _log.error("loadDownloadedSerialno", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());

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
                _log.debug("loadDownloadedSerialno", "Exiting: insert count =" + insert_count);
            }
        }
        return insert_count;

    }

    /**
     * Method: loadCategoryList
     * This method is used for loading sub category list(Product price list)
     * on the basis of status and type of the category
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * @param p_categoryType
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryList(Connection p_con, String p_status, String p_categoryType) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered.. p_status=");
        	msg.append(p_status);
        	msg.append(" p_categoryType=");
        	msg.append(p_categoryType);
      
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        final StringBuilder strBuff = new StringBuilder(" SELECT vc.category_id,vc.description,vc.category_type,vc.category_short_name,vc.mrp,vc.status,");
        strBuff
            .append(" vc.category_name,vc.payable_amount,vvsm.sub_service typ,vc.voucher_type,vvsm.service_type,vvsm.sub_service,vt.TYPE voucher_type_code FROM voms_categories vc,voms_vtype_service_mapping vvsm,voms_types vt  ");
        strBuff.append(" WHERE vc.service_id=vvsm.service_id AND (vc.status = ? OR vc.status IS NULL) AND vc.VOUCHER_TYPE=vt.VOUCHER_TYPE ORDER BY mrp");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setServiceType(rs.getString("service_type"));
                vomsCategoryVO.setvoucherTypeCode(rs.getString("voucher_type_code"));
                // Added by Anjali
                vomsCategoryVO.setType(rs.getString("typ"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("typ"));
                // End

                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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

    public ArrayList loadServiceList(Connection p_con, String p_voucherType) throws BTSLBaseException {
        final String methodName = "loaServiceList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered..  p_voucherType " + p_voucherType);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT vt.voucher_type, vvsm.service_type, st.name ");// ,
        strBuff.append(" FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm, SERVICE_TYPE st where vvsm.service_type=st.service_type and vvsm.voucher_type=vt.voucher_type and");
        strBuff.append(" vt.status = ? and vvsm.voucher_type=? ");// ORDER BY

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.STATUS_ACTIVE);
            pstmt.setString(2, p_voucherType);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setServiceType(rs.getString("service_type"));
                vomsCategoryVO.setServiceName(rs.getString("name"));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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

    public ArrayList loadSubServiceList(Connection p_con, String p_voucherType, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadSubServiceList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered.. p_voucherType=");
        	msg.append(p_voucherType);
        	msg.append(",p_serviceType=");
        	msg.append(p_serviceType);
      
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        final StringBuilder strBuff = new StringBuilder(" SELECT vt.voucher_type, vvsm.service_type, vt.status , vvsm.sub_service, vvsm.service_id,stsm.selector_name ");// ,
        // vvsm.sub_service
        strBuff.append(" FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm, SERVICE_TYPE_SELECTOR_MAPPING stsm");
        strBuff.append(" where vvsm.voucher_type=vt.voucher_type and vvsm.service_type=stsm.service_type and vvsm.sub_service=stsm.selector_code and ");
        strBuff.append(" vt.status = ? and vt.voucher_type=? and vvsm.service_type=? ");// ORDER

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.STATUS_ACTIVE);
            pstmt.setString(2, p_voucherType);
            pstmt.setString(3, p_serviceType);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setServiceType(rs.getString("service_type"));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setSubService(rs.getString("sub_service"));
                vomsCategoryVO.setSelectorName(rs.getString("selector_name"));
                vomsCategoryVO.setServiceID(rs.getInt("service_id"));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * Method: loadSubServiceListInProduct
     * This method is used for loading sub service list
     * on the basis of Voucher type and service of the category
     * 
     * @author akanksha
     * @param p_con
     *            java.sql.Connection
     * @param p_voucherType
     *            String
     * @param p_serviceType
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubServiceListInProduct(Connection p_con, String p_voucherType, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadSubServiceListInProduct";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered.. p_voucherType=");
        	msg.append(p_voucherType);
        	msg.append(",p_serviceType=");
        	msg.append(p_serviceType);
      
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        final StringBuilder strBuff = new StringBuilder(" SELECT distinct vt.voucher_type, vvsm.service_type, vt.status , vvsm.sub_service, vvsm.service_id,stsm.selector_name");// ,
        // vvsm.sub_service
        strBuff.append("  FROM voms_types vt, VOMS_VTYPE_SERVICE_MAPPING vvsm, SERVICE_TYPE_SELECTOR_MAPPING stsm,VOMS_CATEGORIES vc ");
        strBuff.append(" where vvsm.voucher_type=vt.voucher_type and vvsm.service_type=stsm.service_type  ");
        strBuff.append(" and vvsm.sub_service=stsm.selector_code and   ");
        strBuff.append(" vt.status = ? and vt.voucher_type=? and vvsm.service_type=? and   ");
        strBuff.append(" stsm.SELECTOR_CODE=vc.TYPE and vvsm.SERVICE_ID=vc.SERVICE_ID ");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.STATUS_ACTIVE);
            pstmt.setString(2, p_voucherType);
            pstmt.setString(3, p_serviceType);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setServiceType(rs.getString("service_type"));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setSubService(rs.getString("sub_service"));
                vomsCategoryVO.setSelectorName(rs.getString("selector_name"));
                vomsCategoryVO.setServiceID(rs.getInt("service_id"));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * Method: lastSerialNumber
     * This method is used for loading the last serial number from voms_batches
     * table
     * according to the serial number passed.
     * 
     * @author akanksha
     * @param p_con
     *            java.sql.Connection
     * @param Start_no
     *            String
     * @return String
     * @throws BTSLBaseException
     */
    public String[] lastSerialNumber(Connection p_con, String Start_no) throws BTSLBaseException {
        final String methodName = "lastSerialNumber";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. start_no and end_no=" + Start_no);
        }
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String end_serial = null;
        String seq_id=null;
        String serialnSequence[] = new String[2];
        final StringBuilder strBuff = new StringBuilder(" ");
        strBuff.append("Select batch_no, to_serial_no,sequence_id from voms_batches where from_serial_no <=? and to_serial_no>=? and batch_type=? and status=?");
        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, Start_no);
            pstmt.setString(2, Start_no);
            pstmt.setString(3, VOMSI.VOUCHER_NEW);
            pstmt.setString(4, VOMSI.EXECUTED);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                end_serial = rs.getString("to_serial_no");
                seq_id = rs.getString("sequence_id");
            }
            serialnSequence[0]=end_serial;
            serialnSequence[1]=seq_id;

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "lastSerialNumber", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "lastSerialNumber", "error.general.processing");
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
                _log.debug(methodName, "Exiting: last serial number=" + end_serial);
            }
        }
        return serialnSequence;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @param p_oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection p_con, String p_categoryId, long p_oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: categoryId= ");
        	msg.append(p_categoryId);
        	msg.append(", oldLastModified= ");
        	msg.append(p_oldLastModified);
      
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM voms_categories WHERE category_id = ?";
        Timestamp newLastModified = null;
        if (p_oldLastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            }
            // create a prepared statement and execute it
            pstmt = p_con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_categoryId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + p_oldLastModified);
                _log.debug(methodName, " new=" + newLastModified.getTime());
            }
            if (newLastModified.getTime() != p_oldLastModified) {
                modified = true;
            }
            return modified;
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[recordModified]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[recordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch

        finally {
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
                _log.debug(methodName, "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method: loadCategoryList
     * This method is used for loading sub category list(Product price list)
     * on the basis of status and type of the category
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * @param p_categoryType
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryList(Connection p_con, String p_status, String p_categoryType, boolean p_activereqd) throws BTSLBaseException {
        final String METHOD_NAME = "loadCategoryList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered.. p_status= ");
        	msg.append(p_status);
        	msg.append(", p_categoryType= ");
        	msg.append(p_categoryType);
      
        	String message=msg.toString();
            _log.debug("loadCategoryList", message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        final StringBuilder strBuff = new StringBuilder(" SELECT distinct VC.category_id,VC.description,VC.category_type,VC.category_short_name,VC.mrp,VC.status,");
        strBuff.append(" VC.category_name,VC.payable_amount,type FROM voms_categories VC");
        if (p_activereqd) {
            strBuff.append(",voms_products VP,voms_active_product_items VAI ");
        }
        strBuff.append("  WHERE VC.category_type = ? AND (VC.status = ? OR VC.status IS NULL) AND");
        if (p_activereqd) {
            strBuff.append(" VC.category_id=VP.category_id AND VP.product_id=VAI.product_id");
        }
        strBuff.append("  ORDER BY VC.mrp");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug("loadCategoryList", "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryType);
            pstmt.setString(2, p_status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));

                // Added by Anjali
                vomsCategoryVO.setType(rs.getString("type"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("type"));
                // End

                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadCategoryList", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadCategoryList", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadCategoryList", "Exiting: List size=" + list.size());
            }
        }
        return list;
    }
    
    /**
     * Method: loadUserCategoryList
     * This method is used for loading parent category list only
     * 
     * @author amit.singh
     * @param p_con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserCategoryList(Connection p_con,String p_userid) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        
        String sqlSelect=vomsCategoryWebQry.loadUserCategoryList(p_userid,new String[]{});
        
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i=1;
            pstmt.setString(i++, p_userid);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setName(rs.getString("name"));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setType(rs.getString("type"));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
    public ArrayList loadUserCategoryListVoucherType(Connection p_con,String p_userid,String [] voucherTypes) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;

        
        String sqlSelect=vomsCategoryWebQry.loadUserCategoryList(p_userid,voucherTypes);
        
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i=1;
            pstmt.setString(i++, p_userid);
            if(!(BTSLUtil.isNullArray(voucherTypes)))
			{
            for (int x = 0; x < voucherTypes.length; x++) {
            	voucherTypes[x] =voucherTypes[x].replace("'", "");
				pstmt.setString(i++, voucherTypes[x]);

            }
			}
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();
                vomsCategoryVO.setVoucherType(SqlParameterEncoder.encodeParams(rs.getString("voucher_type")));
                vomsCategoryVO.setName(SqlParameterEncoder.encodeParams(rs.getString("name")));
                vomsCategoryVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                vomsCategoryVO.setType(SqlParameterEncoder.encodeParams(rs.getString("type")));
                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
     * Method: loadCategoryListForUser
     * This method is used for loading sub category list(Product price list)
     * on the basis of status , type of the category and user id
     * 
     * @author a
     * @param p_con
     *            java.sql.Connection
     * @param p_status
     *            String
     * @param p_categoryType
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryListForUser(Connection p_con, String p_status, String p_categoryType,UserVO p_uservo) throws BTSLBaseException {
        final String methodName = "loadCategoryListForUser";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered.. p_status= ");
        	msg.append(p_status);
        	msg.append(", p_categoryType= ");
        	msg.append(p_categoryType);
        	msg.append(",  p_uservo= ");
        	msg.append(p_uservo);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsCategoryVO vomsCategoryVO = null;
		
		String sqlSelect=vomsCategoryWebQry.loadCategoryListForUser(p_status,p_uservo.getUserID());
        
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        
        list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_status);
            pstmt.setString(2, p_uservo.getUserID());
            pstmt.setString(3, p_uservo.getNetworkID());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsCategoryVO = new VomsCategoryVO();

                vomsCategoryVO.setCategoryID(rs.getString("category_id"));
                vomsCategoryVO.setDescription(rs.getString("description"));
                vomsCategoryVO.setCategoryShortName(rs.getString("category_short_name"));
                vomsCategoryVO.setCategoryName(rs.getString("category_name"));
                vomsCategoryVO.setMrp(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                vomsCategoryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("mrp")));
                vomsCategoryVO.setStatus(rs.getString("status"));
                vomsCategoryVO.setPayAmount(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("payable_amount"))));
                vomsCategoryVO.setPayAmountStr(PretupsBL.getDisplayAmount(rs.getLong("payable_amount")));
                vomsCategoryVO.setVoucherType(rs.getString("voucher_type"));
                vomsCategoryVO.setServiceType(rs.getString("service_type"));
                vomsCategoryVO.setSegment(rs.getString("voucher_segment"));
                vomsCategoryVO.setvoucherTypeCode(rs.getString("voucher_type_code"));
                // Added by Anjali
                vomsCategoryVO.setType(rs.getString("typ"));
                vomsCategoryVO.setMrpDesc(rs.getString("category_id") + ":" + rs.getString("typ"));
                // End

                list.add(vomsCategoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryListForUser]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryListForUser]", "", "", "",
                "Exception:" + ex.getMessage());
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
    
    public ArrayList loadBatchID(Connection p_con, String mrp, String voucherType, String segment,String networkCode) throws BTSLBaseException {
    	
    	
        final String methodName = "loadBatchID";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered.. p_status= ");
        	msg.append("voucherType= "+voucherType);
        	msg.append("segment= "+segment);
        	msg.append("networkCode= "+networkCode);
        	msg.append(mrp);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList list = null;
        VomsVoucherVO vomsVoucherVO= null;
        try {


            final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT VB.BATCH_NO,VB.FROM_SERIAL_NO,VB.TO_SERIAL_NO,VB.TOTAL_NO_OF_VOUCHERS,VP.PRODUCT_NAME,VB.PRODUCT_ID ");
            strBuff.append(" FROM VOMS_BATCHES VB,VOMS_PRODUCTS VP,VOMS_VOUCHERS VC ");
            strBuff.append(" WHERE VB.PRODUCT_ID=VP.PRODUCT_ID"); 
            strBuff.append(" AND VP.STATUS = ? AND VB.BATCH_TYPE = ? AND VP.MRP =? AND VC.GENERATION_BATCH_NO = VB.BATCH_NO AND VC.STATUS = ? ");
            strBuff.append(" AND VC.VOUCHER_TYPE = ? AND VB.VOUCHER_SEGMENT = ? AND VP.NETWORK_CODE = ?  ");
      
            final String sqlSelect = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            list = new ArrayList(5);

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.PRODUCT_STATUS);
            pstmt.setString(2, VOMSI.VOUCHER_NEW);
            pstmt.setLong(3, PretupsBL.getSystemAmount(mrp));
            pstmt.setString(4, VOMSI.VOUCHER_NEW);
            pstmt.setString(5, voucherType);
            pstmt.setString(6, segment);
            pstmt.setString(7, networkCode);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	vomsVoucherVO = new VomsVoucherVO();
            	vomsVoucherVO.setBatchNo(rs.getString("batch_no"));
            	vomsVoucherVO.set_fromSerialNo(rs.getString("from_serial_no"));
            	vomsVoucherVO.setToSerialNo(rs.getString("to_serial_no"));
            	vomsVoucherVO.setProductName(rs.getString("product_name"));
            	vomsVoucherVO.settotalquantity(rs.getString("total_no_of_vouchers"));
            	vomsVoucherVO.setProductID(rs.getString("product_id"));
            	
                list.add(vomsVoucherVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
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
    
    public HashMap loadVoucherPackageDetails(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadVoucherPackageDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        VomsPackageVO vomspackageVO = null;
        HashMap<String,VomsPackageVO> list = new  HashMap<String,VomsPackageVO>() ;
 
        final StringBuilder strBuff = new StringBuilder(" select vbm.voms_bundle_id,vbm.bundle_name,vbm.bundle_prefix,vbm.retail_price,vp.mrp,vbd.quantity ");
        strBuff.append(" from voms_bundle_master vbm ");
        strBuff.append(" inner join voms_bundle_details vbd "); 
        strBuff.append(" on vbm.VOMS_BUNDLE_ID = vbd.VOMS_BUNDLE_ID ");
        strBuff.append(" inner join voms_products vp  ");
        strBuff.append(" on vbd.PROFILE_ID = vp.PRODUCT_ID  ");
        strBuff.append(" where vbm.STATUS <> ? ");
        strBuff.append(" and vbd.STATUS <> ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
        	int i = 0;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(++i,PretupsI.NO);
            pstmt.setString(++i,PretupsI.NO);
            rs = pstmt.executeQuery();
            while (rs.next()) {
               vomspackageVO = new VomsPackageVO();
               vomspackageVO.setBundleID(rs.getLong("voms_bundle_id"));
               vomspackageVO.setBundleName(rs.getString("bundle_name"));
               vomspackageVO.setBundlePrefix(rs.getString("bundle_prefix"));
               vomspackageVO.setProfileMRP(rs.getDouble("mrp"));
               vomspackageVO.setProfileQuantity(rs.getString("quantity"));
               vomspackageVO.setRetailPrice(BTSLUtil.getDisplayAmount(Double.parseDouble(rs.getString("retail_price"))));
               vomspackageVO.setPackageAmount(Long.parseLong(vomspackageVO.getProfileQuantity()) * vomspackageVO.getProfileMRP()); //each voucher total
               
               if(!list.containsKey(rs.getString("voms_bundle_id")))
            	   list.put(rs.getString("voms_bundle_id"), vomspackageVO);
               else
               {
            	   VomsPackageVO tempVomspackageVO =  list.get(rs.getString("voms_bundle_id"));
            	   vomspackageVO.setPackageAmount(vomspackageVO.getPackageAmount() + tempVomspackageVO.getPackageAmount());   //adding to each package total	   
            	   list.put(rs.getString("voms_bundle_id"), vomspackageVO);
               }
               
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadVoucherPackageDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadVoucherPackageDetails]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting:  list.size() = "+list.size());
            }
        }
        return list;
    }

    public HashMap loadVoucherPackageProductDetails(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadVoucherPackageDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        VomsPackageVoucherVO vomspackagevoucherVO = null;
        ArrayList<VomsPackageVoucherVO> voucherList = new ArrayList<VomsPackageVoucherVO>();
        HashMap<String,ArrayList<VomsPackageVoucherVO>> list = new  HashMap<String,ArrayList<VomsPackageVoucherVO>>() ;
 
        final StringBuilder strBuff = new StringBuilder(" select vbd.VOMS_BUNDLE_ID , vbd.VOMS_BUNDLE_NAME , vp.PRODUCT_ID , ");
        strBuff.append(" vp.PRODUCT_NAME , vbd.QUANTITY , vp.MRP ,  vc.VOUCHER_TYPE , vbm.retail_price , vbm.bundle_prefix , vbm.last_bundle_sequence ");
        strBuff.append(" from voms_bundle_details vbd left outer join voms_products vp ");
        strBuff.append(" on vbd.PROFILE_ID = vp.PRODUCT_ID "); 
        strBuff.append(" left outer join voms_categories vc "); 
        strBuff.append(" on vc.CATEGORY_ID = vp.CATEGORY_ID "); 
        strBuff.append(" left outer join voms_bundle_master vbm "); 
        strBuff.append(" on vbm.VOMS_BUNDLE_ID = vbd.VOMS_BUNDLE_ID ");
        strBuff.append(" where vbd.status <> ?  ");      
        strBuff.append(" order by vbd.VOMS_BUNDLE_ID ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
        	int i = 0;
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(++i,PretupsI.NO);
            rs = pstmt.executeQuery();
            while (rs.next()) {
               vomspackagevoucherVO = new VomsPackageVoucherVO();
               vomspackagevoucherVO.setBundleID(rs.getLong("VOMS_BUNDLE_ID"));
               vomspackagevoucherVO.setBundleName(rs.getString("VOMS_BUNDLE_NAME"));
               vomspackagevoucherVO.setPrice(rs.getDouble("MRP"));
               vomspackagevoucherVO.setQuantity(rs.getInt("QUANTITY"));
               vomspackagevoucherVO.setProductID(rs.getString("PRODUCT_ID"));
               vomspackagevoucherVO.setProductName(rs.getString("PRODUCT_NAME"));
               vomspackagevoucherVO.setProductType(rs.getString("VOUCHER_TYPE"));
               vomspackagevoucherVO.setBundleRetailPrice(rs.getLong("RETAIL_PRICE"));
               vomspackagevoucherVO.setBundlePrefix(rs.getString("BUNDLE_PREFIX"));
               vomspackagevoucherVO.setBundleLastSequence(rs.getLong("LAST_BUNDLE_SEQUENCE"));
               
               if(!list.containsKey(rs.getString("VOMS_BUNDLE_ID"))) {
            	   voucherList = new ArrayList<VomsPackageVoucherVO>();
            	   voucherList.add(vomspackagevoucherVO);
            	   list.put(rs.getString("VOMS_BUNDLE_ID"), voucherList);
               }
            	   else
               {
            		voucherList =  list.get(rs.getString("VOMS_BUNDLE_ID"));
            		voucherList.add(vomspackagevoucherVO);
            		list.put(rs.getString("VOMS_BUNDLE_ID"), voucherList);
               }
               
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadVoucherPackagePriceDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsCategoryDAO[loadVoucherPackagePriceDetails]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting:  list.size() = "+list.size());
            }
        }
        return list;
    }
}
