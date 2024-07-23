package com.btsl.pretups.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CacheDAO {

    private final Log _log = LogFactory.getLog(this.getClass().getName());

    public ArrayList loadCacheList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadCacheList";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered  ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cache_code, cache_name, cache_key, status");
        strBuff.append(" FROM cache_types");
        strBuff.append(" WHERE status='Y' order by cache_name");
        String sqlSelect = strBuff.toString();
        String[] string = null;
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            rs = pstmtSelect.executeQuery();
            CacheVO vo = null;
            while (rs.next()) {
                vo = new CacheVO();
                vo.setCacheCode(rs.getString("cache_code"));
                vo.setCacheName(rs.getString("cache_name"));
                vo.setCacheKey(rs.getString("cache_key"));
                vo.setStatus(rs.getString("status"));
                list.add(vo);
            }

        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CacheDAO[loadCacheList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServices", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CacheDAO[loadCacheList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServices", "error.general.processing");
        } finally {
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
                _log.debug(METHOD_NAME, "Exiting: cacheList =" + list);
            }
        }
        return list;
    }
}