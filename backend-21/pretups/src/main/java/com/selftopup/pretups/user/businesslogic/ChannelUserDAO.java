/**
 * @(#)ChannelUserDAO.java
 *                         Copyright(c) 2014, Mahindra Comviva Pvt Ltd.
 *                         All Rights Reserved
 * 
 */

package com.selftopup.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.util.OperatorUtilI;

/**
 * 
 */
public class ChannelUserDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(ChannelUserDAO.class.getName());
    public static OperatorUtilI _operatorUtilI = null;
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "", "", "", "", "Exception while loading the operator util class in class :" + ChannelUserDAO.class.getName() + ":" + e.getMessage());
        }
    }

    /**
     * Method for loading Users Assigned Services List(means Services that are
     * assigned to the user).
     * From the table USER_SERVICES
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserServicesList(Connection p_con, String p_userId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadUserServicesList", "Entered p_userId=" + p_userId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        // Modification for Service Management [by Vipul]
        strBuff.append(" SELECT US.service_type,US.status FROM user_services US,users U,category_service_type CST");
        strBuff.append(" WHERE US.user_id = ? AND US.status <> 'N'");
        strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadUserServicesList", "QUERY sqlSelect=" + sqlSelect);
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("status"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            _log.error("loadUserServicesList", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserServicesList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserServicesList", "error.general.processing");
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
                _log.debug("loadUserServicesList", "Exiting: userServicesList size=" + list.size());
        }
        return list;
    }
}