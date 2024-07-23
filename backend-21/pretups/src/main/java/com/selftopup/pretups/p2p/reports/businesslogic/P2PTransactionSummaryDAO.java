package com.selftopup.pretups.p2p.reports.businesslogic;

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

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

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
        if (_log.isDebugEnabled())
            _log.debug("loadServiceTypeList", "Entered    p_networkCode " + p_networkCode + "p_moduleCode " + p_moduleCode);
        ArrayList serviceTypeList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer strBuff = null;
        try {
            strBuff = new StringBuffer("SELECT ST.service_type, ST.name ");
            strBuff.append("FROM service_type ST, network_services NS ");
            strBuff.append("WHERE NS.sender_network = ? ");
            strBuff.append("AND NS.module_code = ? ");
            strBuff.append("AND NS.service_type = ST.service_type ");

            String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeList()", "QUERY= " + selectQuery);
            int i = 0;
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(++i, p_networkCode);
            pstmtSelect.setString(++i, p_moduleCode);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                serviceTypeList.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PTransactionSummaryDAO[loadServiceTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList()", "error.general.sql.processing");
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PTransactionSummaryDAO[loadServiceTypeList]", "", "", "", " Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadServiceTypeList()", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadServiceTypeList()", " Exiting.. serviceTypeList size=" + serviceTypeList.size());
        }
        return serviceTypeList;
    }
}
