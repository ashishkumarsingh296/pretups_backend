/*
 * #DivisionDeptDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 4, 2005 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.master.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class DivisionDeptDAO {
    private static final Log log = LogFactory.getFactory().getInstance(DivisionDeptDAO.class.getName());
    
    /**
     * Method loadDivDepDetailsById.
     * This method is used to load division details from division_department according to ID
     * table
     * 
     * @param con
     *            Connection
     * @param divDepId
     *            String
     * @return divisionVO DivisionDeptVO
     * @throws BTSLBaseException
     */

    public DivisionDeptVO loadDivDepDetailsById(Connection con, String divDepId) throws BTSLBaseException {
        final String methodName = "loadDivDepDetailsById";
        LogFactory.printLog(methodName, "Entered p_divDepId:" + divDepId, log);
        DivisionDeptVO divisionVO = null;
        
        
        final StringBuilder strBuff = new StringBuilder("SELECT divdept_id,divdept_name,divdept_short_code,status,");
        strBuff.append("divdept_type,divdept,created_on,created_by,modified_on,modified_by,");
        strBuff.append("parent_id,user_id FROM division_department ");
        strBuff.append("WHERE divdept_id=? AND status<>'N' ");
        final String sqlSelect = strBuff.toString();
        LogFactory.printLog(methodName, "Select Query= " + sqlSelect, log);

    
           try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);)
           {
            pstmtSelect.setString(1, divDepId);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                divisionVO = new DivisionDeptVO();
                divisionVO.setDivDeptId(rs.getString("divdept_id"));
                divisionVO.setDivDeptName(rs.getString("divdept_name"));
                divisionVO.setDivDeptType(rs.getString("divdept_type"));
                divisionVO.setDivDeptShortCode(rs.getString("divdept_short_code"));
                divisionVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                divisionVO.setParentId(rs.getString("parent_id"));
                divisionVO.setStatus(rs.getString("status"));
            }
        } 
           }catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivDepDetailsById]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[loadDivDepDetailsById]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
           
            LogFactory.printLog(methodName, "Exiting ", log);
        }

        return divisionVO;
    }
}
