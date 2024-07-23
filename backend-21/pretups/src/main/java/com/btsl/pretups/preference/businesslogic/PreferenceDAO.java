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

package com.btsl.pretups.preference.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * @author avinash.kamthan
 * 
 */
public class PreferenceDAO {

    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(PreferenceDAO.class.getName());

    /**
     * load the lookups with lookup types.
     * 
     * lookupTypes is key and lookups is List associated
     * 
     * @return HashMap
     * @throws BTSLBaseException
     */
    public HashMap<String,PreferenceCacheVO> loadPrefrences() throws BTSLBaseException {

        final String methodName = "loadPrefrences";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

        Connection con = null;

        HashMap<String,PreferenceCacheVO> map = new HashMap<String,PreferenceCacheVO>();

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
           throw new BTSLBaseException(be) ;
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadPrefrences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadPrefrences()", "error.general.processing");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
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
    private void loadSystemPreferences(Connection p_con, HashMap<String,PreferenceCacheVO> p_map) throws BTSLBaseException {
        final String methodName = "loadSystemPreferences";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Map" + p_map.size());
        }
        
        PreferenceCacheVO cacheVO = null;
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT ");
        strBuff.append("preference_code, name, type, value_type, default_value, modified_on, ");
        strBuff.append(" module FROM system_preferences  ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadSystemPreferences()", "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();) {
            
            
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
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadSystemPreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSystemPreferences()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadSystemPreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadSystemPreferences()", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("loadSystemPreferences()", "Exited: Map size=" + p_map.size());
            }
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
    private void loadNetworkPreferences(Connection p_con, HashMap<String,PreferenceCacheVO> p_map) throws BTSLBaseException {

        final String methodName = "loadNetworkPreferences";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Map" + p_map.size());
        }

         
        PreferenceCacheVO cacheVO = null;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT ");
        strBuff.append("np.preference_code, np.network_code, ");
        strBuff.append(" np.value, np.modified_on , sp.value_type ");
        strBuff.append(" FROM network_preferences np ");
        strBuff.append(", system_preferences sp  where sp.preference_code = np.preference_code  ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadNetworkPreferences()", "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();) {
           
            
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
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadNetworkPreferences()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadNetworkPreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadNetworkPreferences()", "error.general.processing");
        } finally {
           
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
    private void loadZonePreferences(Connection p_con, HashMap<String,PreferenceCacheVO> p_map) throws BTSLBaseException {
        final String methodName = "loadZonePreferences";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Map" + p_map.size());
        }

         

        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT ");
        strBuff.append(" zp.preference_code, zp.network_code, zp.control_code, ");
        strBuff.append(" zp.value, zp.modified_on, sp.value_type ");
        strBuff.append(" FROM control_preferences zp");
        strBuff.append(", system_preferences sp  WHERE zp.preference_code=sp.preference_code ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadZonePreferences()", "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();) {
            
            
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
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadZonePreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadZonePreferences()", "error.general.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadZonePreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadZonePreferences()", "error.general.processing");
        } finally {
        	 
            if (_log.isDebugEnabled()) {
                _log.debug("loadZonePreferences()", "Exited: Map size=" + p_map.size());
            }
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
    private void loadServicePreferences(Connection p_con, HashMap<String,PreferenceCacheVO> p_map) throws BTSLBaseException {
        final String methodName = "loadServicePreferences";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered Map" + p_map.size());
        }

         
        PreferenceCacheVO cacheVO = null;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT "); // PREFERENCES_CODE
        strBuff.append(" scp.preference_code, scp.network_code,  scp.service_class_id, scp.module, ");
        strBuff.append(" scp.value, scp.modified_on , sp.value_type ");
        strBuff.append(" FROM service_class_preferences scp ");
        strBuff.append(", system_preferences sp  where sp.preference_code = scp.preference_code  ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServicePreferences()", "QUERY sqlSelect=" + sqlSelect);
        }

        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect); ResultSet rs = pstmt.executeQuery();){
            
           
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
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferences()", "error.general.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServicePreferences()", "error.general.processing");
        } finally {
            	
            if (_log.isDebugEnabled()) {
                _log.debug("loadServicePreferences()", "Exited: Map size=" + p_map.size());
            }
        }
    }

    /**
     * Method loadPreferenceList.
     * This method is used to load the list of the preferences of the specified
     * type as the argument p_preferenceType
     * this method loads only those records which is allowed to display.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_preferenceType
     *            String
     * @param p_moduleCode
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadPreferenceList(Connection p_con, String p_preferenceType, String p_moduleCode) throws BTSLBaseException {
        final String methodName = "loadPreferenceList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: preferenceType=" + p_preferenceType + ",p_moduleCode=" + p_moduleCode);
        }
        ArrayList preferenceTypeList = new ArrayList();
         
         
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT SP.preference_code,SP.name ");
            selectQuery.append("FROM system_preferences SP,lookups L1,lookups L2 ");
            selectQuery.append("WHERE SP.type= ? AND SP.display='Y' AND SP.value_type=L2.lookup_code AND SP.type=L1.lookup_code ");
            if (!BTSLUtil.isNullString(p_moduleCode)) {
                selectQuery.append("AND SP.module = ?");
            }
            selectQuery.append("ORDER BY SP.name");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, p_preferenceType);
            if (!BTSLUtil.isNullString(p_moduleCode)) {
                pstmtSelect.setString(2, p_moduleCode);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                ListValueVO listValueVO = new ListValueVO(rs.getString("name"), rs.getString("preference_code"));
                preferenceTypeList.add(listValueVO);
            }
        } 
            }
        }catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadPreferenceList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadPreferenceList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	 
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:list size=" + preferenceTypeList.size());
            }
        }
        return preferenceTypeList;
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
        final String methodName = "getServiceClassCountOfCode";

         
         
        StringBuilder strBuff = new StringBuilder();
        int i = 0;
        strBuff.append(" SELECT count(service_class_id) cnt ");
        strBuff.append(" FROM service_class_preferences scp ");
        strBuff.append(" WHERE scp.preference_code = ?  AND service_class_id<>? AND network_code=? AND module=? ");

        String sqlSelect = strBuff.toString();
        try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);){
           
            pstmt.setString(1, p_preferenceCode);
            pstmt.setString(2, p_serviceClassCode);
            pstmt.setString(3, p_networkID);
            pstmt.setString(4, p_module);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                i = rs.getInt("cnt");
            }
            return i;
        } 
        }catch (SQLException sqe) {
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getServiceClassCountOfCode()", "error.general.processing");
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PreferenceDAO[loadServicePreferences]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getServiceClassCountOfCode()", "error.general.processing");
        } finally {
        	//_log.debug(methodName,"inside finally");
        }
    }

}
