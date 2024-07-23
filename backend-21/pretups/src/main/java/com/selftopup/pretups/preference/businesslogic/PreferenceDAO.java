/**
 * @(#)PreferenceDAO.java
 *                        Copyright(c) 2005, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        <description>
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        avinash.kamthan Mar 15, 2005 Initital Creation
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 * 
 */

package com.selftopup.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.OracleUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class PreferenceDAO {

    /**
     * Commons Logging instance.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * load the lookups with lookup types.
     * 
     * lookupTypes is key and lookups is List associated
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap loadPrefrences() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadPrefrences()", "Entered ");
        }

        Connection con = null;

        HashMap map = new HashMap();

        try {
            con = OracleUtil.getSingleConnection();

            /*
             * Load the all preferences
             */
            loadSystemPreferences(con, map);
            loadNetworkPreferences(con, map);
            loadZonePreferences(con, map);
            loadServicePreferences(con, map);

        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception ex) {
            _log.error("loadPrefrences()", "Exception: " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadPrefrences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadPrefrences()", "error.general.processing");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadPrefrences()", "Exited: Map size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Load the System preferences
     * 
     * @param p_con
     * @param p_map
     * @throws BTSLBaseException
     *             void
     *             PreferenceDAO
     */
    private void loadSystemPreferences(Connection p_con, HashMap p_map) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadSystemPreferences()", "Entered Map" + p_map);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreferenceCacheVO cacheVO = null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT ");
        strBuff.append("preference_code, name, type, value_type, default_value, modified_on, ");
        strBuff.append(" module FROM system_preferences  ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadSystemPreferences()", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                cacheVO = new PreferenceCacheVO();
                cacheVO.setPreferenceCode(rs.getString("preference_code"));
                cacheVO.setModuleCode(rs.getString("module"));
                cacheVO.setPrefrenceName(rs.getString("name"));
                cacheVO.setType(rs.getString("type"));
                cacheVO.setValueType(rs.getString("value_type"));
                cacheVO.setValue(rs.getString("default_value"));
                cacheVO.setModifiedOn(rs.getDate("modified_on"));
                cacheVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                p_map.put(cacheVO.getPreferenceCode(), cacheVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadSystemPreferences()", " SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadSystemPreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSystemPreferences()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadSystemPreferences()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadSystemPreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSystemPreferences()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadSystemPreferences()", "Exited: Map size=" + p_map.size());
        }
    }

    /**
     * load the network preferences
     * 
     * @param p_con
     * @param p_map
     * @throws BTSLBaseException
     *             void
     *             PreferenceDAO
     */
    private void loadNetworkPreferences(Connection p_con, HashMap p_map) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkPreferences()", "Entered Map" + p_map);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreferenceCacheVO cacheVO = null;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT ");
        strBuff.append("np.preference_code, np.network_code, ");
        strBuff.append(" np.value, np.modified_on , sp.value_type ");
        strBuff.append(" FROM network_preferences np ");
        strBuff.append(", system_preferences sp  where sp.preference_code = np.preference_code  ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadNetworkPreferences()", "QUERY sqlSelect=" + sqlSelect);

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                cacheVO = new PreferenceCacheVO();
                cacheVO.setPreferenceCode(rs.getString("preference_code"));
                cacheVO.setNetworkCode(rs.getString("network_code"));
                cacheVO.setValue(rs.getString("value"));
                cacheVO.setModifiedOn(rs.getDate("modified_on"));
                cacheVO.setValueType(rs.getString("value_type"));
                cacheVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                p_map.put(cacheVO.getPreferenceCode() + ":" + cacheVO.getNetworkCode(), cacheVO);

            }
        } catch (SQLException sqe) {
            _log.error("loadNetworkPreferences()", " SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworkPreferences()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadNetworkPreferences()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworkPreferences()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadNetworkPreferences()", "Exited: Map size=" + p_map.size());
            }
        }
    }

    /**
     * load the zone preferences
     * 
     * @param p_con
     * @param p_map
     * @throws BTSLBaseException
     *             void
     *             PreferenceDAO
     */
    private void loadZonePreferences(Connection p_con, HashMap p_map) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadZonePreferences()", "Entered Map" + p_map);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT ");
        strBuff.append(" zp.preference_code, zp.network_code, zp.control_code, ");
        strBuff.append(" zp.value, zp.modified_on, sp.value_type ");
        strBuff.append(" FROM control_preferences zp");
        strBuff.append(", system_preferences sp  WHERE zp.preference_code=sp.preference_code ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadZonePreferences()", "QUERY sqlSelect=" + sqlSelect);

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            PreferenceCacheVO cacheVO = null;
            while (rs.next()) {

                cacheVO = new PreferenceCacheVO();
                cacheVO.setPreferenceCode(rs.getString("preference_code"));
                cacheVO.setNetworkCode(rs.getString("network_code"));
                cacheVO.setControlCode(rs.getString("control_code"));
                cacheVO.setValue(rs.getString("value"));
                cacheVO.setModifiedOn(rs.getDate("modified_on"));
                cacheVO.setValueType(rs.getString("value_type"));
                cacheVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                p_map.put(cacheVO.getPreferenceCode() + ":" + cacheVO.getNetworkCode() + ":" + cacheVO.getControlCode(), cacheVO);

            }
        } catch (SQLException sqe) {
            _log.error("loadZonePreferences()", " SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadZonePreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadZonePreferences()", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadZonePreferences()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadZonePreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadZonePreferences()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadZonePreferences()", "Exited: Map size=" + p_map.size());
        }
    }

    /**
     * load the service preferences
     * 
     * @param p_con
     * @param p_map
     * @throws BTSLBaseException
     *             void
     *             PreferenceDAO
     */
    private void loadServicePreferences(Connection p_con, HashMap p_map) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadServicePreferences()", "Entered Map" + p_map);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreferenceCacheVO cacheVO = null;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT "); // PREFERENCES_CODE
        strBuff.append(" scp.preference_code, scp.network_code,  scp.service_class_id, scp.module, ");
        strBuff.append(" scp.value, scp.modified_on , sp.value_type ");
        strBuff.append(" FROM service_class_preferences scp ");
        strBuff.append(", system_preferences sp  where sp.preference_code = scp.preference_code  ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServicePreferences()", "QUERY sqlSelect=" + sqlSelect);

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                cacheVO = new PreferenceCacheVO();
                cacheVO.setPreferenceCode(rs.getString("preference_code"));
                cacheVO.setNetworkCode(rs.getString("network_code"));
                cacheVO.setServiceCode(rs.getString("service_class_id"));
                cacheVO.setModuleCode(rs.getString("module")); // to be changed
                cacheVO.setValue(rs.getString("value"));
                cacheVO.setModifiedOn(rs.getDate("modified_on"));
                cacheVO.setValueType(rs.getString("value_type"));
                cacheVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));

                int count = getServiceClassCountOfCode(p_con, rs.getString("service_class_id"), rs.getString("preference_code"), rs.getString("network_code"), rs.getString("module"));
                cacheVO.setNoOfOtherPrefOtherThanAll(count);

                String key = cacheVO.getPreferenceCode() + ":" + cacheVO.getNetworkCode() + ":" + cacheVO.getModuleCode() + ":" + cacheVO.getServiceCode();
                p_map.put(key, cacheVO);

            }
        } catch (SQLException sqe) {
            _log.error("loadServicePreferences()", " SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferences()", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadServicePreferences()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferences()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServicePreferences()", "Exited: Map size=" + p_map.size());
        }
    }

    /**
     * Method that will give the no of service class present apart from ALL
     * (Default)
     * 
     * @param p_con
     * @param p_serviceClassCode
     * @param p_preferenceCode
     * @param p_networkID
     * @param p_module
     * @return int
     * @throws BTSLBaseException
     */
    public int getServiceClassCountOfCode(Connection p_con, String p_serviceClassCode, String p_preferenceCode, String p_networkID, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadServicePreferences()", "Entered p_networkID=" + p_networkID + " p_module=" + p_module + " p_serviceClassCode" + p_serviceClassCode + " p_preferenceCode=" + p_preferenceCode);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff = new StringBuilder();
        int i = 0;
        strBuff.append(" SELECT count(service_class_id) cnt ");
        strBuff.append(" FROM service_class_preferences scp ");
        strBuff.append(" WHERE scp.preference_code = ?  AND service_class_id<>? AND network_code=? AND module=? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("getServiceClassCountOfCode()", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_preferenceCode);
            pstmt.setString(2, p_serviceClassCode);
            pstmt.setString(3, p_networkID);
            pstmt.setString(4, p_module);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                i = rs.getInt("cnt");
            }
            return i;
        } catch (SQLException sqe) {
            _log.error("loadServicePreferences()", " SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getServiceClassCountOfCode()", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadServicePreferences()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getServiceClassCountOfCode()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getServiceClassCountOfCode()", "Exited: i=" + i);
        }
    }

    /**
     * Load the list service Prefereences list
     * 
     * @param p_con
     * @param p_serviceClassID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadServicePreferencesList(Connection p_con, String p_serviceClassID) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadServicePreferencesList()", "Entered p_serviceClassID: " + p_serviceClassID);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreferenceCacheVO cacheVO = null;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT ");
        strBuff.append(" scp.preference_code, scp.network_code,  scp.service_class_id, scp.module, ");
        strBuff.append(" scp.value, scp.modified_on  ");
        strBuff.append(" FROM service_class_preferences scp ");
        strBuff.append(" where scp.SERVICE_CLASS_ID = ?  ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadServicePreferencesList()", "QUERY sqlSelect=" + sqlSelect);

        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_serviceClassID);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                cacheVO = new PreferenceCacheVO();
                cacheVO.setPreferenceCode(rs.getString("preference_code"));
                cacheVO.setNetworkCode(rs.getString("network_code"));
                cacheVO.setServiceCode(rs.getString("service_class_id"));
                cacheVO.setModuleCode(rs.getString("module")); // to be changed
                cacheVO.setValue(rs.getString("value"));
                cacheVO.setModifiedOn(rs.getDate("modified_on"));
                // cacheVO.setValueType(rs.getString("value_type"));
                cacheVO.setModifiedTimeStamp(rs.getTimestamp("modified_on"));
                list.add(cacheVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadServicePreferencesList()", " SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferencesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferencesList()", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadServicePreferencesList()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferencesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferencesList()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServicePreferencesList()", "Exited: list size=" + list.size());
        }
        return list;
    }

}
