package com.txn.pretups.master.businesslogic;

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
import com.btsl.pretups.common.PretupsI;

public class DivisionDeptTxnDAO {

    private static Log _log = LogFactory.getFactory().getInstance(DivisionDeptTxnDAO.class.getName());

    /**
     * Constructor for DivisionDeptTxnDAO.
     */
    public DivisionDeptTxnDAO() {
        super();
    }

    public boolean isDepartmentExitsUnderDivision(Connection p_con, String divisionId, String departmentId) throws BTSLBaseException {

        final String METHOD_NAME = "isDepartmentExitsUnderDivision";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered divisionId::" + divisionId);
        }
        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department WHERE");
        sqlBuff.append(" DIVDEPT_ID=? and PARENT_ID =? AND status<>?");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, departmentId);
            pstmtSelect.setString(2, divisionId);
            pstmtSelect.setString(3, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentExitsUnderDivision]", "", "", "", "SQL Exception:" + sqle.getMessage());
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDepartmentExitsUnderDivision]", "", "", "", "Exception:" + e.getMessage());

        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

    public boolean isDivisionExists(Connection p_con, String divisionId) throws BTSLBaseException {

        final String METHOD_NAME = "isDivisionExists";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered divisionId::" + divisionId);
        }
        boolean found = false;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM division_department WHERE");
        sqlBuff.append(" DIVDEPT_ID=? AND status<>?");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, divisionId);
            pstmtSelect.setString(2, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DivisionDeptDAO[isDivisionExists]", "", "", "", "Exception:" + e.getMessage());

        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting  found=" + found);
            }
        }
        return found;
    }

}
