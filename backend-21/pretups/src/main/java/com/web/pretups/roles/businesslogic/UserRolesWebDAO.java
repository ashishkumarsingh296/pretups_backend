package com.web.pretups.roles.businesslogic;

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
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class UserRolesWebDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading Roles List(All roles irrespective of the group_role
     * value)
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
    public HashMap loadRolesList(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadRolesList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  p_categoryCode=" + p_categoryCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,role_name ");

        final String sqlSelect = strBuff.toString();
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=" );
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,loggerValue );
        }
        int count = 0;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_categoryCode);

            rs = pstmt.executeQuery();
            UserRolesVO rolesVO = null;
            ArrayList list = null;
            if (rs != null) {
                map = new LinkedHashMap();
                while (rs.next()) {
                    rolesVO = new UserRolesVO();
                    rolesVO.setDomainType(SqlParameterEncoder.encodeParams(rs.getString("domain_type")));
                    rolesVO.setRoleCode(SqlParameterEncoder.encodeParams(rs.getString("role_code")));
                    rolesVO.setRoleName(SqlParameterEncoder.encodeParams(rs.getString("role_name")));
                    rolesVO.setGroupName(SqlParameterEncoder.encodeParams(rs.getString("group_name")));
                    rolesVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                    rolesVO.setRoleType(SqlParameterEncoder.encodeParams(rs.getString("role_type")));
                    rolesVO.setFromHour(SqlParameterEncoder.encodeParams(rs.getString("from_hour")));
                    rolesVO.setToHour(SqlParameterEncoder.encodeParams(rs.getString("to_hour")));
                    rolesVO.setGroupRole(SqlParameterEncoder.encodeParams(rs.getString("group_role")));

                    // for Zebra and Tangoc by sanjeew date 06/07/07
                    rolesVO.setApplicationID(SqlParameterEncoder.encodeParams(rs.getString("application_id")));
                    rolesVO.setGatewayTypes(SqlParameterEncoder.encodeParams(rs.getString("gateway_types")));
                    // end Zebra and Tango

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesList]", "", "", "",
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
                _log.debug(methodName, "Exiting: rolesList size=" + count);
            }
        }
        return map;
    }

    /**
     * Method for loading Users Assigned roles List.(That are assigned to the
     * user)
     * from the table user_roles
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadUserRolesList(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserRolesList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_userId=");
        	loggerValue.append(BTSLUtil.maskParam(p_userId));
            _log.debug(methodName,loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT role_code FROM user_roles WHERE user_id = ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("role_code"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadUserRolesList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadUserRolesList]", "", "", "",
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
                _log.debug(methodName, "Exiting: userRolesList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Roles List.(Those roles that are associated with a
     * group)
     * from the table group_roles
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_groupRoleCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadRolesByGroupRoleCode(Connection p_con, String p_groupRoleCode) throws BTSLBaseException {
        final String methodName = "loadRolesByGroupRoleCode";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_groupRoleCode=");
        	loggerValue.append(p_groupRoleCode);
            _log.debug(methodName,loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT role_code FROM group_roles WHERE group_role_code = ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_groupRoleCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("role_code"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesByGroupRoleCode]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesByGroupRoleCode]", "", "",
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
                _log.debug(methodName, "Exiting: userRolesList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for Inserting New Group Role Detail in Roles Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleVO
     *            UserRolesVO
     * 
     * @return addCount int
     * @exception BTSLBaseException
     */
    public int addRole(Connection p_con, UserRolesVO p_roleVO) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        StringBuilder loggerValue= new StringBuilder();
        final String methodName = "addRole";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: roleVO= " );
        	loggerValue.append(p_roleVO);
            _log.debug(methodName,loggerValue );
        }

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO roles (domain_type, ");
            strBuff.append(" role_code,role_name,group_name, ");
            strBuff.append("status,role_type,from_hour,to_hour,group_role, is_default) ");
            strBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
            }

            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, p_roleVO.getDomainType());
            pstmtInsert.setString(2, p_roleVO.getRoleCode().toUpperCase());
            // commented for DB2pstmtInsert.setFormOfUse(3,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(3, p_roleVO.getRoleName());
            // commented for DB2 pstmtInsert.setFormOfUse(4,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtInsert.setString(4, p_roleVO.getGroupName());
            pstmtInsert.setString(5, p_roleVO.getStatus());
            pstmtInsert.setString(6, p_roleVO.getRoleType());
            pstmtInsert.setString(7, p_roleVO.getFromHour());
            pstmtInsert.setString(8, p_roleVO.getToHour());
            pstmtInsert.setString(9, p_roleVO.getGroupRole());

            pstmtInsert.setString(10, p_roleVO.getDefaultType());

            addCount = pstmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[addRole]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[addRole]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally

        return addCount;
    }

    /**
     * Method for Updating Group Role Detail in Roles Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleVO
     *            UserRolesVO
     * 
     * @return addCount int
     * @exception BTSLBaseException
     */
    public int updateRole(Connection p_con, UserRolesVO p_roleVO) throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;

        final String methodName = "updateRole";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: roleVO= ");
        	loggerValue.append(p_roleVO);
            _log.debug(methodName,loggerValue);
        }

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE roles SET");
            strBuff.append(" role_name = ?,group_name = ?, ");
            strBuff.append("status = ?,role_type = ?,from_hour = ?,to_hour = ?,is_default = ? ");
            strBuff.append(" WHERE role_code = ?");

            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlUpdate:" + updateQuery);
            }

            // commented for DB2 pstmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(updateQuery);
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            // commented for DB2 pstmtUpdate.setFormOfUse(1,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(1, p_roleVO.getRoleName());
            // commented for DB2 pstmtUpdate.setFormOfUse(2,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmtUpdate.setString(2, p_roleVO.getGroupName());
            pstmtUpdate.setString(3, p_roleVO.getStatus());
            pstmtUpdate.setString(4, p_roleVO.getRoleType());
            pstmtUpdate.setString(5, p_roleVO.getFromHour());
            pstmtUpdate.setString(6, p_roleVO.getToHour());
            pstmtUpdate.setString(7, p_roleVO.getDefaultType());
            pstmtUpdate.setString(8, p_roleVO.getRoleCode());
            updateCount = pstmtUpdate.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[updateRole]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[updateRole]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * Method for Updating Group Role Detail in Roles Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleVO
     *            UserRolesVO
     * 
     * @return void
     * @exception BTSLBaseException
     */
    public void updateRoleCode(Connection p_con, UserRolesVO p_roleVO) throws BTSLBaseException {
        PreparedStatement pstmtUpdate = null;
        StringBuilder loggerValue= new StringBuilder();
        final String methodName = "updateRoleCode";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_roleVO= ");
        	loggerValue.append(p_roleVO);
            _log.debug(methodName,loggerValue);
        }

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE roles SET");
            strBuff.append(" is_default = ? ");
            strBuff.append(" WHERE role_code = ? AND domain_type = ? ");
            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlUpdate:" + updateQuery);
            }

            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setString(1, PretupsI.NO);
            pstmtUpdate.setString(2, p_roleVO.getRoleCode());
            pstmtUpdate.setString(3, p_roleVO.getDomainType());
            pstmtUpdate.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[updateRole]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[updateRole]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=", "updateCount");
            }
        } // end of finally

    }

    /**
     * Method for Inserting New Group Role Entry in category_roles Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleCode
     *            String
     * @param p_categoryCode
     *            String
     * 
     * @return addCount int
     * @exception BTSLBaseException
     */
    public int addCategoryRole(Connection p_con, String p_roleCode, String p_categoryCode) throws BTSLBaseException {
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        StringBuilder loggerValue= new StringBuilder();
        final String methodName = "addCategoryRole";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_roleCode= ");
        	loggerValue.append(p_roleCode);
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO category_roles (role_code,category_code)");
            strBuff.append("VALUES(?,?)");

            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
            }

            pstmtInsert = p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, p_roleCode.toUpperCase());
            pstmtInsert.setString(2, p_categoryCode);

            addCount = pstmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[addCategoryRole]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[addCategoryRole]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally

        return addCount;
    }

    /**
     * Method for Deleting Group Role Entry in group_roles Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_groupRoleCode
     *            String
     * 
     * @return deleteCount int
     * @exception BTSLBaseException
     */
    public int deleteGroupRole(Connection p_con, String p_groupRoleCode) throws BTSLBaseException {
        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;

        final String methodName = "deleteGroupRole";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_groupRoleCode= ");
        	loggerValue.append(p_groupRoleCode);
            _log.debug(methodName, loggerValue);
        }

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("DELETE FROM group_roles WHERE group_role_code = ?");

            final String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlDelete:" + deleteQuery);
            }

            pstmtDelete = p_con.prepareStatement(deleteQuery);

            pstmtDelete.setString(1, p_groupRoleCode.toUpperCase());// changed
            // by Ashish
            // Srivastav
            // on
            // 5-03-2008
            // for
            // inserting
            // role code
            // in
            // database
            // in upper
            // case

            deleteCount = pstmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[deleteGroupRole]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[deleteGroupRole]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtDelete != null) {
                    pstmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for inserting User Products Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_groupRoleCode
     *            String
     * @param p_roleCodes
     *            String[]
     * @return insertCount int
     * @exception BTSLBaseException
     */
    public int addGroupRoles(Connection p_con, String p_groupRoleCode, String[] p_roleCodes) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addGroupRoles";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_groupRoleCode= ");
        	msg.append(p_groupRoleCode);
        	msg.append(", p_roleCodes Size= ");
        	msg.append(p_roleCodes.length);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        try {
            int count = 0;
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO group_roles (group_role_code,");
            strBuff.append("role_code) values (?,?)");
            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            for (int i = 0, j = p_roleCodes.length; i < j; i++) {
                psmtInsert.setString(1, p_groupRoleCode.toUpperCase());// changed
                // by
                // Ashish
                // Srivastav
                // on
                // 14-08-2007
                // for
                // inserting
                // role
                // code
                // in
                // database
                // in
                // upper
                // case
                psmtInsert.setString(2, p_roleCodes[i].toUpperCase());
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the update
                if (insertCount > 0) {
                    count++;
                }
            }
            if (count == p_roleCodes.length) {
                insertCount = 1;
            } else {
                insertCount = 0;
            }

        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[addGroupRoles]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[addGroupRoles]", "", "", "",
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
     * Method for Updating Roles Table while deleting and suspending Group Role.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleVO
     *            UserRolesVO
     * 
     * @return addCount int
     * @exception BTSLBaseException
     */
    public int deleteRole(Connection p_con, UserRolesVO p_roleVO) throws BTSLBaseException {
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        StringBuilder loggerValue= new StringBuilder();
        final String methodName = "deleteRole";
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: roleVO= ");
        	loggerValue.append(p_roleVO);
            _log.debug(methodName,loggerValue );
        }

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE roles SET ");
            strBuff.append("status = ? ");
            strBuff.append(" WHERE role_code = ?");

            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlUpdate:" + updateQuery);
            }

            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setString(1, p_roleVO.getStatus());
            pstmtUpdate.setString(2, p_roleVO.getRoleCode());

            updateCount = pstmtUpdate.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[deleteRole]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[deleteRole]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * Method for checking Is Role Code already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleCode
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isRoleCodeAssociated(Connection p_con, String p_roleCode) throws BTSLBaseException {
        final String methodName = "isRoleCodeAssociated";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_roleCode=");
        	loggerValue.append(p_roleCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuffer strBuff = new StringBuffer();
        // strBuff.append("SELECT role_code FROM user_roles WHERE role_code = ?");
        strBuff.append("SELECT ur.role_code FROM user_roles ur,users u WHERE ur.role_code =? and ur.USER_ID=u.USER_ID and u.STATUS <>? and u.STATUS <>?  and u.STATUS <>? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_roleCode);
            pstmt.setString(2, PretupsI.USER_STATUS_DEREGISTERED);
            pstmt.setString(3, PretupsI.USER_STATUS_CANCELED);
            pstmt.setString(4, PretupsI.USER_STATUS_DELETED);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[isRoleCodeAssociated]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[isRoleCodeAssociated]", "", "", "",
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
     * Date : Apr 13, 2007
     * Discription :
     * Method : loadRolesListByUserID
     * 
     * @param p_con
     * @param p_userID
     * @param p_categoryCode
     * @param p_groupRoleFlag
     * @throws BTSLBaseException
     * @return HashMap
     * @author ved.sharma
     */
    public HashMap loadRolesListByUserID(Connection p_con, String p_userID, String p_categoryCode, String p_groupRoleFlag, String role_for) throws BTSLBaseException {
        final String methodName = "loadRolesListByUserID";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_userID=");
        	loggerValue.append(BTSLUtil.maskParam(p_userID));
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" p_groupRoleFlag=");
        	loggerValue.append(p_groupRoleFlag);
        	loggerValue.append(" role_for= ");
        	loggerValue.append(role_for);
            _log.debug(methodName,loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d,user_roles UR ");
        strBuff.append(" WHERE r.status != 'N' AND UR.user_id = ? AND C.category_code=? ");
        strBuff.append(" AND UR.role_code=cr.role_code ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        strBuff.append(" AND r.group_role = ?  and r.ROLE_FOR in (" + role_for + ") ");
        // strBuff.append(" AND r.group_role = ? ");
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,role_name ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {

            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_userID);
            pstmt.setString(2, p_categoryCode);
            pstmt.setString(3, p_groupRoleFlag);
            // pstmt.setString(4, role_for);
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
                    // end Zebra and Tango

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesListByUserID]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesListByUserID]", "", "", "",
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
                _log.debug(methodName, "Exiting: rolesList size=" + count);
            }
        }
        return map;
    }

    /**
     * Description : Mothod to load Group Roles.
     * Method : loadGroupRoleRolesListByUserID
     * 
     * @param p_con
     * @param p_userID
     * @param p_categoryCode
     * @throws BTSLBaseException
     * @return HashMap
     */
    public HashMap loadGroupRoleRolesListByUserID(Connection p_con, String p_userID, String p_categoryCode, String role_for) throws BTSLBaseException {
        final String methodName = "loadGroupRoleRolesListByUserID";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_userID= ");
        	msg.append(BTSLUtil.maskParam(p_userID));
        	msg.append(", p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", role_for= ");
        	msg.append(role_for);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.role_type,r.from_hour,r.to_hour,r.group_role,");
        strBuff.append(" r.Application_id, r.Gateway_types FROM roles r, categories c, domains d");
        strBuff.append(" where r.role_code in (");
        strBuff.append(" SELECT GR.role_code FROM category_roles cr,roles rr, user_roles UR,");
        strBuff.append(" group_roles GR  WHERE rr.status != 'N'");
        strBuff.append(" AND UR.user_id = ?");
        strBuff.append(" AND UR.role_code=cr.role_code");
        strBuff.append(" AND rr.group_role = 'Y' ");
        strBuff.append(" AND cr.role_code = rr.role_code AND GR.GROUP_ROLE_CODE = UR.role_code  and rr.ROLE_FOR in (" + role_for + ") )");
        strBuff.append(" AND c.category_code=?");
        strBuff.append(" AND c.domain_code=d.domain_code");
        strBuff.append(" AND d.domain_type_code=r.domain_type order by r.group_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_userID);
            pstmt.setString(2, p_categoryCode);

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
                    // end Zebra and Tango

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadGroupRoleRolesListByUserID]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadGroupRoleRolesListByUserID]", "",
                "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: rolesList size=" + count);
            }
        }
        return map;
    }

    /**
     * Method for checking whether the user has group roles assigned or system
     * roles.
     * Returns true if group roles are assigned.
     * 
     * @param p_con
     * @param p_userID
     *            String
     * @param p_domainType
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isGroupRole(Connection p_con, String p_userID, String p_domainType) {
        final String methodName = "isGroupRole";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_userID= ");
        	msg.append(BTSLUtil.maskParam(p_userID));
        	msg.append(", p_domainType= ");
        	msg.append(p_domainType);
   
        	String message=msg.toString();
            _log.debug(methodName, message);
        }

        
        boolean roleStatus = false;
        try {
            final StringBuffer isGroupRoleQueryBuff = new StringBuffer("SELECT 1 FROM USER_ROLES,ROLES ");
            isGroupRoleQueryBuff.append("WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
            isGroupRoleQueryBuff.append("AND group_role='Y'AND (ROLES.status IS NULL OR ROLES.status='Y')");

            if (_log.isDebugEnabled()) {
                _log.debug("isAssignedRoleAndExist", " isGroupRoleQueryBuff : " + isGroupRoleQueryBuff);
            }

            try(PreparedStatement psmtIsExist = p_con.prepareStatement(isGroupRoleQueryBuff.toString());)
            {
            psmtIsExist.setString(1, p_userID);
            psmtIsExist.setString(2, p_domainType);

           try(ResultSet rsIsExist = psmtIsExist.executeQuery();)
           {
            if (rsIsExist.next()) {
                roleStatus = true;
            }
        }
            }
        }

        catch (Exception ex2) {
            _log.errorTrace(methodName, ex2);
            roleStatus = false;
        } finally {
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting role status=" + roleStatus);
            }
        }

        return roleStatus;
    }

    /**
     * Method for checking Is Default Role already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_roleCode
     *            String
     * @return flag boolean
     * @exception BTSLBaseException
     */
    public boolean isRoleDefault(Connection p_con, String p_roleCode, String p_domainType) throws BTSLBaseException {
        final String methodName = "isRoleDefault";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_roleCode=");
        	loggerValue.append(p_roleCode);
        	loggerValue.append("p_domainType ");
        	loggerValue.append(p_domainType);
            _log.debug(methodName,loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT group_name FROM roles ");
        strBuff.append(" WHERE role_code = ? AND domain_type = ? ");
        strBuff.append(" AND is_default = ? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_roleCode);
            pstmt.setString(2, p_domainType);
            pstmt.setString(3, PretupsI.YES);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[isRoleCodeAssociated]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[isRoleCodeAssociated]", "", "", "",
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

    // ADDED BY PRAVEEN KUMAR SINGH;
    /**
     * Description : Mothod to load Group Roles.
     * Method : loadGroupRoleRolesListByUserID
     * 
     * @param p_con
     * @param p_userID
     * @param p_categoryCode
     * @throws BTSLBaseException
     * @return HashMap
     */
    public HashMap loadGroupRoleRolesListByUserID(Connection p_con, String p_userID, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadGroupRoleRolesListByUserID";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_userID=");
        	loggerValue.append(BTSLUtil.maskParam(p_userID));
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append( p_categoryCode);
            _log.debug(methodName,loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.role_type,r.from_hour,r.to_hour,r.group_role,");
        strBuff.append(" r.Application_id, r.Gateway_types FROM roles r, categories c, domains d");
        strBuff.append(" where r.role_code in (");
        strBuff.append(" SELECT GR.role_code FROM category_roles cr,roles rr, user_roles UR,");
        strBuff.append(" group_roles GR  WHERE rr.status != 'N'");
        strBuff.append(" AND UR.user_id = ?");
        strBuff.append(" AND rr.view_roles = 'Y' ");
        strBuff.append(" AND rr.role_Code = UR.role_code ");
        strBuff.append(" AND UR.role_code=cr.role_code");
        strBuff.append(" AND rr.group_role = 'Y'");
        strBuff.append(" AND cr.role_code = rr.role_code AND GR.GROUP_ROLE_CODE = UR.role_code )");
        strBuff.append(" AND c.category_code=?");
        strBuff.append(" AND c.domain_code=d.domain_code");
        strBuff.append(" AND d.domain_type_code=r.domain_type order by r.group_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
        	
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_userID);
            pstmt.setString(2, p_categoryCode);

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
                    // end Zebra and Tango

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadGroupRoleRolesListByUserID]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadGroupRoleRolesListByUserID]", "",
                "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: rolesList size=" + count);
            }
        }
        return map;
    }

    /**
     * Date : Apr 13, 2007
     * Discription :
     * Method : loadRolesListByUserID
     * 
     * @param p_con
     * @param p_userID
     * @param p_categoryCode
     * @param p_groupRoleFlag
     * @throws BTSLBaseException
     * @return HashMap
     * @author ved.sharma
     */
    public HashMap loadRolesListByUserID(Connection p_con, String p_userID, String p_categoryCode, String p_groupRoleFlag) throws BTSLBaseException {
        final String methodName = "loadRolesListByUserID";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_userID= ");
        	msg.append(BTSLUtil.maskParam(p_userID));
        	msg.append(", p_categoryCode= ");
        	msg.append(p_categoryCode);
        	msg.append(", p_groupRoleFlag= ");
        	msg.append(p_groupRoleFlag);
   
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT r.domain_type,r.role_code,r.role_name,");
        strBuff.append(" r.group_name,r.status,r.role_type,r.from_hour,");
        strBuff.append(" r.to_hour,r.group_role, ");

        // for Zebra and Tango by sanjeew date 06/07/07
        strBuff.append(" r.Application_id, r.Gateway_types ");
        // End Zebra and Tango

        strBuff.append(" FROM category_roles cr,roles r ,categories c,domains d,user_roles UR ");
        strBuff.append(" WHERE r.status != 'N' " );
        strBuff.append(" AND UR.user_id = ? " );
        strBuff.append(" AND C.category_code = ? ");
        strBuff.append(" AND r.view_roles = 'Y' ");
        strBuff.append(" AND UR.role_code=r.role_code ");
        strBuff.append(" AND cr.category_code=c.category_code ");
        strBuff.append(" AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type ");
        strBuff.append(" AND r.group_role = ? ");
        strBuff.append(" AND cr.role_code = r.role_code ORDER BY r.group_name,role_name ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        int count = 0;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_userID);
            pstmt.setString(2, p_categoryCode);
            pstmt.setString(3, p_groupRoleFlag);

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
                    // end Zebra and Tango

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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesListByUserID]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserRolesWebDAO[loadRolesListByUserID]", "", "", "",
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
                _log.debug(methodName, "Exiting: rolesList size=" + count);
            }
        }
        return map;
    }
}
