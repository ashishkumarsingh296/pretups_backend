package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.btsl.pretups.p2p.transfer.businesslogic.MCDDAO;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;

/**
 * @(#)AutoDelMCDL.java
 *                      Copyright(c) 2006, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Harsh Dixit 13/09/2012 Initial Creation
 */

public class AutoDelMCDL {

    private static Log _log = LogFactory.getLog(AutoDelMCDL.class.getName());

    /**
     * ensures no instantiation
     */
    private AutoDelMCDL(){
    	
    }
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2) {
                System.out.println("Usage : AutoDelMCDL [Constants file] [ProcessLogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("AutoDelMCDL" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("AutoDelMCDL" + " ProcessLogconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Error in Loading Files ...........................: " + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            return;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting..... ");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Connection con = null;
        PreparedStatement psmt = null;
        int deleteCount = 0;
        final MCDDAO mcdDAO = new MCDDAO();
        MCDListVO mcdListVO = null;
        ArrayList list = new ArrayList();
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbiguousCaseAlarm[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            final int days = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_MCDL_AUTO_DELETION_DAYS))).intValue();
            list = mcdDAO.countDays(con, days);
            final StringBuffer queryBuff = new StringBuffer();
            queryBuff.append("DELETE from P2P_BUDDIES ");
            queryBuff.append("WHERE PARENT_ID=? AND LIST_NAME=?");
            final String query = queryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("query", query);
            }
            psmt = con.prepareStatement(query);

            for (int i = 0, k = list.size(); i < k; i++) {
                mcdListVO = (MCDListVO) list.get(i);
                psmt.setString(1, mcdListVO.getParentID());
                psmt.setString(2, mcdListVO.getListName());
                deleteCount = psmt.executeUpdate();
                if (deleteCount > 0) {
                    System.out.println("success");
                }
                psmt.clearParameters();
            }
        } catch (BTSLBaseException be) {
            _log.error("main", "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqle) {

            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addBuddyHistory]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("AutoDelMCDL", "process", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {

            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[addBuddyHistory]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("AutoDelMCDL", "process", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (con != null) {
                    con.commit();
                }
                con.close();
            } catch (Exception ex) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Exception in closing connection ");
                }
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.info("process", "Exiting..... ");
            }
        }// end of finally

    }

}
