package com.btsl.pretups.p2p.reports.businesslogic;

/**
 * @# P2PTransactionSummaryDAO.java
 *    --------------------------------------------------------------------------
 *    ------------------
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    -------------------
 *    AUG 31, 2005 ved prakash sharma Initial creation
 *    --------------------------------------------------------------------------
 *    -------------------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 * 
 **/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class P2PTransactionSummaryDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadServiceTypeList
     * this method load service list
     * 
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_moduleCode
     *            String
     * @return ArrayList
     */

    public ArrayList loadServiceTypeList(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "loadServiceTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadServiceTypeList", "Entered    p_networkCode " + p_networkCode + "p_moduleCode " + p_moduleCode);
        }
        final ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer strBuff = null;
        try {
            strBuff = new StringBuffer("SELECT ST.service_type, ST.name ");
            strBuff.append("FROM service_type ST, network_services NS ");
            strBuff.append("WHERE NS.sender_network = ? ");
            strBuff.append("AND NS.module_code = ? ");
            strBuff.append("AND NS.service_type = ST.service_type ");

            final String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceTypeList()", "QUERY= " + selectQuery);
            }
            int i = 0;
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_moduleCode);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceTypeList.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PTransactionSummaryDAO[loadServiceTypeList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PTransactionSummaryDAO[loadServiceTypeList]", "",
                "", "", " Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList()", "error.general.processing");
        } finally {
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
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadServiceTypeList()", " Exiting.. serviceTypeList size=" + serviceTypeList.size());
            }
        }
        return serviceTypeList;
    }
}
