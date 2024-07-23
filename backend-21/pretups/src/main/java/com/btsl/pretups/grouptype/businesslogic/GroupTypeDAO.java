/**
 * @(#)GroupTypeDAO.java
 *                       Copyright(c) 2006, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 * 
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Ankit Zindal 11/07/2006 Initial Creation
 * 
 */

package com.btsl.pretups.grouptype.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * 
 */
public class GroupTypeDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(GroupTypeDAO.class.getName());

    /**
     * Method: loadUserGroupTypeCounters
     * This method will load the running group type counters of user.
     * 
     * @param p_con
     * @param p_userID
     * @param p_msisdn
     * @param p_groupType
     * @param p_type
     * 
     * @return
     * @throws BTSLBaseException
     */
    public GroupTypeCountersVO loadUserGroupTypeCounters(Connection p_con, String p_userID, String p_msisdn, String p_groupType, String p_type) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserGroupTypeCounters";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserGroupTypeCounters", "Entered p_userID:" + p_userID + " p_msisdn:" + p_msisdn + " p_groupType:" + p_groupType + " p_type:" + p_type);
        }
        PreparedStatement pstmtSelect = null;
        GroupTypeCountersVO groupTypeCountersVO = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT year, month, day, user_id, msisdn, group_type, type, counters, module ");
            selectQueryBuff.append(" FROM group_type_counters ");
            // DB220120123for update WITH RS
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                selectQueryBuff.append(" WHERE user_id=? AND msisdn=? AND group_type=? AND type=? FOR UPDATE WITH RS ");
            } else {
                selectQueryBuff.append(" WHERE user_id=? AND msisdn=? AND group_type=? AND type=? FOR UPDATE ");
            }
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserGroupTypeCounters", "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, p_msisdn);
            pstmtSelect.setString(3, p_groupType);
            pstmtSelect.setString(4, p_type);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                groupTypeCountersVO = new GroupTypeCountersVO();
                groupTypeCountersVO.setYear(rs.getInt("year"));
                groupTypeCountersVO.setMonth(rs.getInt("month"));
                groupTypeCountersVO.setDay(rs.getInt("day"));
                groupTypeCountersVO.setUserID(rs.getString("user_id"));
                groupTypeCountersVO.setMsisdn(rs.getString("msisdn"));
                groupTypeCountersVO.setGroupType(rs.getString("group_type"));
                groupTypeCountersVO.setType(rs.getString("type"));
                groupTypeCountersVO.setCounters(rs.getLong("counters"));
                groupTypeCountersVO.setModule(rs.getString("module"));
            }
            return groupTypeCountersVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadUserGroupTypeCounters", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[loadUserGroupTypeCounters]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserGroupTypeCounters", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadUserGroupTypeCounters", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[loadUserGroupTypeCounters]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadUserGroupTypeCounters", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserGroupTypeCounters", "Exiting groupTypeCountersVO:" + groupTypeCountersVO);
            }
        }// end of finally
    }

    /**
     * Method: loadGroupTypeProfileCache
     * This method will load the cache of GroupType profile
     * 
     * @return
     * @throws BTSLBaseException
     */
    public HashMap loadGroupTypeProfileCache() throws BTSLBaseException {
        final String METHOD_NAME = "loadGroupTypeProfileCache";
        if (_log.isDebugEnabled()) {
            _log.debug("loadGroupTypeProfileCache()", "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = new HashMap();
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT network_code, group_type, req_gateway_type, res_gateway_type, type, ");
        strBuff.append(" threshold_value, gateway_code, alt_gateway_code, frequency FROM group_type_profiles ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadGroupTypeProfileCache", "QUERY sqlSelect=" + sqlSelect);
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            GroupTypeProfileVO grptProfileVO = null;
            while (rs.next()) {
                grptProfileVO = new GroupTypeProfileVO();
                grptProfileVO.setNetworkCode(rs.getString("network_code"));
                grptProfileVO.setGroupType(rs.getString("group_type"));
                grptProfileVO.setReqGatewayType(rs.getString("req_gateway_type"));
                grptProfileVO.setResGatewayType(rs.getString("res_gateway_type"));
                grptProfileVO.setType(rs.getString("type"));
                grptProfileVO.setThresholdValue(rs.getLong("threshold_value"));
                grptProfileVO.setGatewayCode(rs.getString("gateway_code"));
                grptProfileVO.setAltGatewayCode(rs.getString("alt_gateway_code"));
                grptProfileVO.setFrequency(rs.getString("frequency"));
                map.put(rs.getString("network_code") + "_" + rs.getString("group_type") + "_" + rs.getString("req_gateway_type") + "_" + rs.getString("res_gateway_type") + "_" + rs.getString("type"), grptProfileVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadGroupTypeProfileCache()", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[loadGroupTypeProfileCache]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadGroupTypeProfileCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadGroupTypeProfileCache()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[loadGroupTypeProfileCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadGroupTypeProfileCache()", "error.general.processing");
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
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadGroupTypeProfileCache()", "Exiting: map size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Method: updateUserGroupTypeCounters
     * This method will update the running group type counters of user. If
     * counters can not be updated then insert the counters
     * 
     * @param p_con
     * @param p_groupTypeCountersVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateUserGroupTypeCounters(Connection p_con, GroupTypeCountersVO p_groupTypeCountersVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateUserGroupTypeCounters";
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserGroupTypeCounters", "Entered p_groupTypeCountersVO:" + p_groupTypeCountersVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = -1;
        try {
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE group_type_counters SET year=? , month=? , day=? , counters=? ");
            updateQueryBuff.append(" WHERE user_id=? AND msisdn=? AND group_type=? AND type=? ");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("updateUserGroupTypeCounters", "update query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setInt(1, p_groupTypeCountersVO.getYear());
            pstmtUpdate.setInt(2, p_groupTypeCountersVO.getMonth());
            pstmtUpdate.setInt(3, p_groupTypeCountersVO.getDay());
            pstmtUpdate.setLong(4, p_groupTypeCountersVO.getCounters() + 1);
            pstmtUpdate.setString(5, p_groupTypeCountersVO.getUserID());
            pstmtUpdate.setString(6, p_groupTypeCountersVO.getMsisdn());
            pstmtUpdate.setString(7, p_groupTypeCountersVO.getGroupType());
            pstmtUpdate.setString(8, p_groupTypeCountersVO.getType());
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                updateCount = insertUserGroupTypeCounters(p_con, p_groupTypeCountersVO);
            }
            if (updateCount <= 0) {
                _log.error("updateUserGroupTypeCounters", "Error while updating user group type counters for user_id:");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "GroupTypeDAO[updateUserGroupTypeCounters]", "", "", "", "Error while updating user group type counters for user_id:" + p_groupTypeCountersVO.getUserID());
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateUserGroupTypeCounters", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[updateUserGroupTypeCounters]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateUserGroupTypeCounters", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateUserGroupTypeCounters", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[updateUserGroupTypeCounters]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateUserGroupTypeCounters", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateUserGroupTypeCounters", "Exiting updateCount:" + updateCount);
            }
        }// end of finally
    }

    /**
     * Method: insertUserGroupTypeCounters
     * This method will insert the running group type counters of user.
     * 
     * @param p_con
     * @param p_groupTypeCountersVO
     * @return
     * @throws BTSLBaseException
     */
    public int insertUserGroupTypeCounters(Connection p_con, GroupTypeCountersVO p_groupTypeCountersVO) throws BTSLBaseException {
        final String METHOD_NAME = "insertUserGroupTypeCounters";
        if (_log.isDebugEnabled()) {
            _log.debug("insertUserGroupTypeCounters", "Entered p_groupTypeCountersVO:" + p_groupTypeCountersVO);
        }
        PreparedStatement pstmtInsert = null;
        int updateCount = -1;
        try {
            StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO group_type_counters(year,month,day,user_id,msisdn,group_type,type,counters,module) ");
            insertQueryBuff.append(" VALUES(?,?,?,?,?,?,?,?,?) ");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("insertUserGroupTypeCounters", "insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setInt(1, p_groupTypeCountersVO.getYear());
            pstmtInsert.setInt(2, p_groupTypeCountersVO.getMonth());
            pstmtInsert.setInt(3, p_groupTypeCountersVO.getDay());
            pstmtInsert.setString(4, p_groupTypeCountersVO.getUserID());
            pstmtInsert.setString(5, p_groupTypeCountersVO.getMsisdn());
            pstmtInsert.setString(6, p_groupTypeCountersVO.getGroupType());
            pstmtInsert.setString(7, p_groupTypeCountersVO.getType());
            pstmtInsert.setLong(8, p_groupTypeCountersVO.getCounters() + 1);
            pstmtInsert.setString(9, p_groupTypeCountersVO.getModule());
            updateCount = pstmtInsert.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("insertUserGroupTypeCounters", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[insertUserGroupTypeCounters]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "insertUserGroupTypeCounters", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("insertUserGroupTypeCounters", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[insertUserGroupTypeCounters]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "insertUserGroupTypeCounters", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("insertUserGroupTypeCounters", "Exiting updateCount:" + updateCount);
            }
        }// end of finally
    }

    /**
     * Method: decreaseUserGroupTypeCounters
     * This method will decrese the running group type counters of user by one.
     * 
     * @param p_con
     * @param p_groupTypeCountersVO
     * @return
     * @throws BTSLBaseException
     */
    public int decreaseUserGroupTypeCounters(Connection p_con, GroupTypeCountersVO p_groupTypeCountersVO) throws BTSLBaseException {
        final String METHOD_NAME = "decreaseUserGroupTypeCounters";
        if (_log.isDebugEnabled()) {
            _log.debug("decreaseUserGroupTypeCounters", "Entered p_groupTypeCountersVO:" + p_groupTypeCountersVO.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = -1;
        try {
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE group_type_counters SET year=? , month=? , day=? , counters=counters-1 ");
            updateQueryBuff.append(" WHERE user_id=? AND msisdn=? AND group_type=? AND type=? ");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("decreaseUserGroupTypeCounters", "update query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setInt(1, p_groupTypeCountersVO.getYear());
            pstmtUpdate.setInt(2, p_groupTypeCountersVO.getMonth());
            pstmtUpdate.setInt(3, p_groupTypeCountersVO.getDay());
            pstmtUpdate.setString(4, p_groupTypeCountersVO.getUserID());
            pstmtUpdate.setString(5, p_groupTypeCountersVO.getMsisdn());
            pstmtUpdate.setString(6, p_groupTypeCountersVO.getGroupType());
            pstmtUpdate.setString(7, p_groupTypeCountersVO.getType());
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                _log.error("decreaseUserGroupTypeCounters", "Error while updating user group type counters for user_id:");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "GroupTypeDAO[decreaseUserGroupTypeCounters]", "", "", "", "Error while updating user group type counters for user_id:");
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("decreaseUserGroupTypeCounters", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[decreaseUserGroupTypeCounters]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "decreaseUserGroupTypeCounters", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("decreaseUserGroupTypeCounters", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GroupTypeDAO[decreaseUserGroupTypeCounters]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "decreaseUserGroupTypeCounters", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("decreaseUserGroupTypeCounters", "Exiting updateCount:" + updateCount);
            }
        }// end of finally
    }

}