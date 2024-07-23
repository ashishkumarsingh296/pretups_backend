package com.restapi.loggers;

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

public class LoggerDAO {

	private static final Log log = LogFactory.getLog(LoggerDAO.class.getName());

	/**
	 * 
	 * @param p_con
	 * @return
	 * @throws BTSLBaseException
	 */
	public HashMap<String , ElementCodeDetailsVO> getElementCodeDetailsMap(Connection p_con) throws BTSLBaseException {

		final String methodName = "getElementCodeDetailsMap";
		if (log.isDebugEnabled()) {
			log.debug(methodName , "Entered");
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap<String , ElementCodeDetailsVO> map = new HashMap<String , ElementCodeDetailsVO>();
		try {

			StringBuilder strBuff = new StringBuilder(
					"SELECT REC.element_code , REC.role_code , R.group_name , R.domain_type");
			strBuff.append(" FROM revamp_element_codes REC , roles R");
			strBuff.append(" WHERE REC.role_code = R.role_code");
			strBuff.append(" ORDER BY REC.element_code , R.domain_type");
			String sqlSelect = strBuff.toString();
			if (log.isDebugEnabled()) {
				log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
			}
			pstmt = p_con.prepareStatement(sqlSelect);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if(map.containsKey(rs.getString("element_code"))) {
					HashMap<String , String> groupNameMap = map.get(rs.getString("element_code")).getGroupNameMap();
					groupNameMap.put(rs.getString("domain_type"), rs.getString("group_name"));
				}else {
					ElementCodeDetailsVO elementCodeDetailsVO = new ElementCodeDetailsVO();
					elementCodeDetailsVO.setRoleCode(rs.getString("role_code"));
					HashMap<String,String> groupNameMap = new HashMap<String , String>();
					groupNameMap.put(rs.getString("domain_type"), rs.getString("group_name"));
					elementCodeDetailsVO.setGroupNameMap(groupNameMap);
					map.put(rs.getString("element_code"), elementCodeDetailsVO);
				}
            }
		} catch (SQLException sqe) {
            log.error("loadUserServicesList", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoggerDAO[getElementCodeDetailsMap]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getElementCodeDetailsMap", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadUserServicesList", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoggerDAO[getElementCodeDetailsMap]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getElementCodeDetailsMap", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: elementCodeDetailsMap size=" + map.size());
            }
        }
        return map;
    }
}
