package com.txn.pretups.roles.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;

public class UserRolesTxnDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * @param p_con
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public String loadDefaultGroupRoleForCategory(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadDefaultGroupRoleForCategory";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_categoryCode=" + p_categoryCode);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff = new StringBuilder();
        StringBuilder rolesStr = null;
        strBuff.append(" SELECT R.ROLE_CODE,CR.CATEGORY_CODE FROM CATEGORY_ROLES CR,ROLES R ");
        strBuff.append(" WHERE CR.ROLE_CODE=R.ROLE_CODE  AND  R.IS_DEFAULT=? AND R.GROUP_ROLE=? AND ");
        strBuff.append(" CR.CATEGORY_CODE=? AND R.STATUS=? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("isRoleDefault", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, PretupsI.YES);
            pstmt.setString(2, PretupsI.YES);
            pstmt.setString(3, p_categoryCode);
            pstmt.setString(4, PretupsI.YES);

            rs = pstmt.executeQuery();
            rolesStr = new StringBuilder();
            if (rs.next()) {

                rolesStr.append(rs.getString("role_code") + ",");

            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesTxnDAO[loadDefaultGroupRoleForCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesTxnDAO[loadDefaultGroupRoleForCategory]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: rolesStr=" + rolesStr.toString());
            }
        }
        return rolesStr.toString();
    }

    /**
     * Method for loading Roles List
     * Load those Role where group_role = Y(those roles that are add by user
     * through jsp).
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            String
     * @param p_groupRoleFlag
     *            String
     * 
     * @return java.util.HashMap
     * @exception BTSLBaseException
     */
    public HashMap loadDefaultRolesListByGroupRole(Connection p_con, String p_categoryCode, String p_groupRoleFlag) throws BTSLBaseException {
        final String methodName = "loadDefaultRolesListByGroupRole";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode + " p_groupRoleFlag=" + p_groupRoleFlag);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = null;
        StringBuffer strBuff = new StringBuffer();
        /*
         * strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
         * strBuff.append("r.group_name,r.status,r.role_type,r.from_hour,");
         * strBuff.append("r.to_hour,r.group_role ");
         * strBuff.append("FROM category_roles cr,roles r ");
         * strBuff.append("WHERE r.status = 'Y' AND cr.category_code = ? ");
         * strBuff.append(" AND r.group_role = ?");
         * strBuff.append(
         * " AND cr.role_code = r.role_code ORDER BY r.group_name,role_name");
         */

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types,r.is_default ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        strBuff.append(" AND r.group_role = ? and is_default=? ");
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,role_name ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_groupRoleFlag);
            pstmt.setString(3, PretupsI.YES);
            rs = pstmt.executeQuery();
            UserRolesVO rolesVO = null;
            ArrayList list = null;
            if (rs != null) {
                map = new LinkedHashMap();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(rs.getString("domain_type"));
                    rolesVO.setRoleCode(rs.getString("role_code"));
                    rolesVO.setRoleName(rs.getString("role_name"));
                    rolesVO.setGroupName(rs.getString("group_name"));
                    rolesVO.setStatus(rs.getString("status"));
                    rolesVO.setRoleType(rs.getString("role_type"));
                    rolesVO.setFromHour(rs.getString("from_hour"));
                    rolesVO.setToHour(rs.getString("to_hour"));
                    rolesVO.setGroupRole(rs.getString("group_role"));
                    // for Zebra and Tango by sanjeew date 06/07/07
                    rolesVO.setApplicationID(rs.getString("application_id"));
                    rolesVO.setGatewayTypes(rs.getString("gateway_types"));
                    // End Zebra and Tango

                    rolesVO.setDefaultType(rs.getString("is_default"));
                    if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.YES)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.YES);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    } else if (rolesVO.getDefaultType() != null && rolesVO.getDefaultType().equalsIgnoreCase(PretupsI.NO)) {
                        LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.IS_DEFAULT, PretupsI.NO);
                        rolesVO.setDefaultTypeDesc(lookupsVO.getLookupName());
                    }

                    if (map.containsKey(rolesVO.getGroupName())) {
                        list.add(rolesVO);
                        map.put(rolesVO.getGroupName(), list);
                    } else {
                        list = new ArrayList();
                        list.add(rolesVO);
                        map.put(rolesVO.getGroupName(), list);
                    }

                    count++;
                }
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesTxnDAO[loadDefaultRolesListByGroupRole]", "", "", "", "SQL Exception:" + sqe.getMessage());

        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesTxnDAO[loadDefaultRolesListByGroupRole]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: rolesList size=" + count);
            }
        }
        return map;
    }

    /**
     * Method for checking Is Role Code already associated or not for user.
     * 
     * @param p_con
     * @param p_userId
     * @param p_roleCode
     * @return
     * @throws BTSLBaseException
     */
    public boolean isUserRoleCodeAssociated(Connection p_con, String p_userId, String p_roleCode) throws BTSLBaseException {
        final String methodName = "isUserRoleCodeAssociated";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_roleCode=" + p_roleCode + " , p_userId = " + p_userId);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT ur.role_code FROM user_roles ur,users u WHERE ur.role_code =? and ur.USER_ID=u.USER_ID and u.STATUS <>? and u.STATUS <>?  and u.STATUS <>? ");
        strBuff.append(" AND u.USER_ID = ?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_roleCode);
            pstmt.setString(2, PretupsI.USER_STATUS_DEREGISTERED);
            pstmt.setString(3, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(4, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(5, p_userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[isRoleCodeAssociated]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesDAO[isRoleCodeAssociated]", "", "", "", "Exception:" + ex.getMessage());
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
}
