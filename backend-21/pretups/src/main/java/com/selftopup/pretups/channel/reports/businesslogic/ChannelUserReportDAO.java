package com.selftopup.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class ChannelUserReportDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public ArrayList loadKeyValuesList(Connection p_con, boolean p_isAllKey, String p_type, String p_inKeys) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadKeyValuesList", "Entered: p_isAllKey=" + p_isAllKey + ", p_type=" + p_type + ", p_inKeys=" + p_inKeys);
        ArrayList list = new ArrayList();
        // commented for DB2 OraclePreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        StringBuffer strBuff = new StringBuffer("SELECT key, value, type, text1 FROM key_values WHERE type=? ");
        if (!p_isAllKey)
            strBuff.append("AND key IN (" + p_inKeys + ") ");
        strBuff.append("ORDER BY key");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadKeyValuesList", "QUERY sqlSelect=" + sqlSelect);
        try {
            // commented for DB2 pstmtSelect =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_type);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("value"), rs.getString("key")));
            }
        } catch (SQLException sqe) {
            _log.error("loadKeyValuesList", "SQLException : " + sqe);
            _log.errorTrace("loadKeyValuesList: Exception print stack trace:", sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadKeyValuesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadKeyValuesList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadKeyValuesList", "Exception : " + ex);
            _log.errorTrace("loadKeyValuesList: Exception print stack trace:", ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserReportDAO[loadKeyValuesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadKeyValuesList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadKeyValuesList", "Exiting: userList size =" + list.size());
        }
        return list;
    }

}
