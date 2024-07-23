package com.btsl.pretups.servicekeyword.businesslogic;

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
import com.btsl.pretups.master.businesslogic.ServiceInstancePriorityVO;
import com.btsl.util.OracleUtil;

public class ServiceInstancePriorityDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    public HashMap<String, String> loadServiceInstancePriority() throws Exception {

        final String METHOD_NAME = "loadServiceInstancePriority";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceInstancePriority()", "Entered");
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap<String, String> serviceInstancePriorityMap = new HashMap<String, String>();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT");
        strBuff.append(" * ");
        strBuff.append(" FROM  ");
        strBuff.append(" SERVICE_INSTANCE_PRIORITY ST ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceInstancePriority()", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            con = OracleUtil.getSingleConnection();

            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ServiceInstancePriorityVO cacheVO = null;
            String key = null;
            String value = null;
            while (rs.next()) {
                cacheVO = new ServiceInstancePriorityVO();
                cacheVO.setServiceType(rs.getString("service_type"));
                cacheVO.setInstanceID((rs.getString("instance_id")));
                cacheVO.setPriority(rs.getInt("priority"));
                cacheVO.setRequestTimeout(rs.getLong("request_time_out"));
                key = cacheVO.getServiceType() + "_" + cacheVO.getInstanceID();
                value = String.valueOf(cacheVO.getPriority()) + "_" + String.valueOf(cacheVO.getRequestTimeout());
                serviceInstancePriorityMap.put(key, value);
            }

        } catch (SQLException sqe) {
            _log.error("loadServiceInstancePriority()", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceInstancePriority]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceInstancePriority()", "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error("loadServiceInstancePriority()", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServiceKeywordDAO[loadServiceInstancePriority]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadServiceInstancePriority()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceInstancePriority", "Exiting:Map size=" + serviceInstancePriorityMap.size());
            }
        }
        return serviceInstancePriorityMap;

    }

}
