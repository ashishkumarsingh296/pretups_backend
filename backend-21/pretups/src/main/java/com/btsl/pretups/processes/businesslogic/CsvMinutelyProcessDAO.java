/**
 * @(#)CsvMinutelyProcessDAO
 * Copyright(c) 2014, Bharti Telesoft Ltd. 
 * All Rights Reserved
 * 
 *   ---------------------------------------------------------
 * Author               Date            History
 *  ---------------------------------------------------------
 * Harsh Deep Dixit  17/018/2015      Initial Creation
 * ---------------------------------------------------------
 */
package com.btsl.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

public class CsvMinutelyProcessDAO {

    private static final Log LOG = LogFactory.getLog(CsvMinutelyProcessDAO.class
            .getName());
    private List<ProcessStatusVO> processStatusList = null;

    /**
     * This method will update process status data based on processId from
     * database
     * 
     * @param StartedDate
     *            Date
     * @param executedUpto
     *            Date
     * @param processId
     *            String
     * @return int
     * @throws SQLException,Exception
     */
    public int updateMinutelyProcessStatus(Date StartedDate, Date executedUpto,
            String processId) throws BTSLBaseException {
        final String methodUpdate = "updateMinutelyProcessStatus";
        int updateCount = 0;
        String sqlUpdate = " UPDATE process_status SET start_date=?,executed_upto=?,executed_on=? WHERE process_id=? ";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodUpdate, "Update qrySelect:" + sqlUpdate);
        }
        updateCount = updateMinutelyProcess(StartedDate, executedUpto, processId,
                sqlUpdate);
        if (LOG.isDebugEnabled())
            LOG.debug("CsvMinutelyProcessDAO", "Exiting " + methodUpdate);
        return updateCount;
    }

    /**
     * @param StartedDate
     * @param executedUpto
     * @param processId
     * @param method
     * @param p_con
     * @param pstmtUpdate
     * @param updateCount
     * @param sqlUpdate
     * @return
     * @throws BTSLBaseException
     */
    private int updateMinutelyProcess(Date StartedDate, Date executedUpto,
            String processId, String sqlUpdate) throws BTSLBaseException {
        int u_updateCount = 0;
        u_updateCount = updateMinutely(StartedDate, executedUpto, processId,
                sqlUpdate);
        return u_updateCount;
    }

    /**
     * @param StartedDate
     * @param executedUpto
     * @param processId
     * @param method
     * @param p_con
     * @param pstmtUpdate
     * @param updateCount
     * @param sqlUpdate
     * @return
     * @throws BTSLBaseException
     */
    private int updateMinutely(Date StartedDate, Date executedUpto,
            String processId, String sqlUpdate) throws BTSLBaseException {
        final String method_N = "updateMinutely";
        int p_updateCount = 0;

        Connection con = null;
        MComConnectionI mcomCon = null;
       
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            if (con == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(method_N, " DATABASE Connection is NULL ");
                }
            } else {
                try(PreparedStatement pstmtUpdate = con.prepareStatement(sqlUpdate);)
                {
                pstmtUpdate.setTimestamp(1, BTSLUtil
                        .getTimestampFromUtilDate(StartedDate));
                pstmtUpdate.setTimestamp(2, BTSLUtil
                        .getTimestampFromUtilDate(executedUpto));
                pstmtUpdate.setDate(3, BTSLUtil
                        .getSQLDateFromUtilDate(executedUpto));
                pstmtUpdate.setString(4, processId);
                p_updateCount = pstmtUpdate.executeUpdate();
                if (p_updateCount > 0) {
                    mcomCon.finalCommit();
                }
            }
        } 
        }catch (BTSLBaseException be) {
            LOG.errorTrace(method_N, be);
            throw be;
        } catch (SQLException sq) {
            LOG.errorTrace(method_N, sq);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "CsvMinutelyProcessDAO[" + method_N + "]", "", "", "",
                    "SQLException:" + sq.getMessage());
            throw new BTSLBaseException("CsvMinutelyProcessDAO", method_N,
                    PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception ex) {
            LOG.errorTrace(method_N, ex);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CsvMinutelyProcessDAO#updateMinutely");
				mcomCon = null;
			}
           
        }
        return p_updateCount;
    }

    /**
     * This method will close all connection resources
     * 
     * @param method
     *            String
     * @param p_connection
     *            Connection
     * @param pstmtUpdate
     *            PreparedStatement
     * @return void
     * @throws Exception
     */
    private void closeResourcesUpdate(final String methods,
            Connection p_connection, PreparedStatement psmtsUpdate) {
        try {
            if (psmtsUpdate != null) {
                psmtsUpdate.close();
            }
        } catch (Exception exception) {
            LOG.errorTrace(methods + " error in statement ", exception);
        }
        try {
            if (p_connection != null) {
                p_connection.close();
            }
        } catch (Exception ext) {
            LOG.errorTrace(methods + " error in connection ", ext);
        }
    }

    /**
     * This method will fetch process status data based on processId from
     * database
     * 
     * @param processId
     *            String
     * @return String
     * @throws SQLException,Exception
     */
    public String fetchMinutelyProcessStatus(String processId)
            throws BTSLBaseException {
        final String methodName = "fetchMinutelyProcessStatus";

        String processStatus = "";
        processStatus = fetchMinutely(processId, methodName);
        if (LOG.isDebugEnabled())
            LOG.debug("CsvMinutelyProcessDAO", "Exiting " + methodName);
        return processStatus;
    }

    /**
     * @param processId
     * @param methodName
     * @param con
     * @param rst
     * @param pstm
     * @param processStatus
     * @return
     * @throws BTSLBaseException
     */
    private String fetchMinutely(String processId, final String mthdName)
            throws BTSLBaseException {
        Connection con = null;
        MComConnectionI mcomCon = null;
        String processStatusMinutely = "";
        try {

            StringBuilder qryBuffer = new StringBuilder();
            qryBuffer
                    .append(" SELECT EXECUTED_UPTO,BEFORE_INTERVAL FROM PROCESS_STATUS WHERE process_id='"
                            + processId + "'");
            String query = qryBuffer.toString();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            try(PreparedStatement pstm = con.prepareStatement(query); ResultSet rst = pstm.executeQuery();)
            {
            mcomCon.finalCommit();
            processStatusList = new ArrayList<ProcessStatusVO>();
            while (rst.next()) {
                processStatusMinutely = rst.getString("EXECUTED_UPTO") + "#"
                        + rst.getLong("BEFORE_INTERVAL");
            }
        } 
        }catch (BTSLBaseException be) {
            LOG.errorTrace(mthdName, be);
            throw be;
        } catch (SQLException sqe) {
            LOG.errorTrace(mthdName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "CsvMinutelyProcessDAO[" + mthdName + "]", "", "", "",
                    "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("CsvMinutelyProcessDAO", mthdName,
                    "sql exception");
        } catch (Exception ex) {
            LOG.errorTrace(mthdName, ex);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CsvMinutelyProcessDAO#fetchMinutely");
				mcomCon = null;
			}
           
        }
        return processStatusMinutely;
    }

    /**
     * This method will close all connection resources
     * 
     * @param methodName
     *            String
     * @param con
     *            Connection
     * @param rst
     *            ResultSet
     * @param pstm
     *            PreparedStatement
     * @return void
     * @throws Exception
     */
    private void closeResources(final String methodNme, Connection connection,
            ResultSet rst, PreparedStatement pstm) {
        if (rst != null) {
            try {
                rst.close();
            } catch (Exception exrst) {
                LOG.errorTrace(methodNme + " error in result set ", exrst);
            }
        }
        if (pstm != null) {
            try {
                pstm.close();
            } catch (Exception expmt) {
                LOG.errorTrace(methodNme + " error in statement ", expmt);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception excon) {
                LOG.errorTrace(methodNme + " error in connection ", excon);
            }
        }
    }
    
    
    /**
     * This method will fetch process status data based on processId from
     * database
     * 
     * @param processId
     *            String
     * @return String
     * @throws SQLException,Exception
     */
    public String fetchMinutelyProcessStatus(String processId, Connection p_con)
            throws BTSLBaseException {
        final String methodName = "fetchMinutelyProcessStatus";

        String processStatus = "";
        processStatus = fetchMinutely(processId, methodName, p_con);
        if (LOG.isDebugEnabled())
            LOG.debug("CsvMinutelyProcessDAO", "Exiting " + methodName);
        return processStatus;
    }
    
    
    
    /**
     * @param processId
     * @param methodName
     * @param con
     * @param rst
     * @param pstm
     * @param processStatus
     * @return
     * @throws BTSLBaseException
     */
    private String fetchMinutely(String processId, final String mthdName, Connection con)
            throws BTSLBaseException {
        
        ResultSet rst = null;
        PreparedStatement pstm = null;
        String processStatusMinutely = "";
        try {

            StringBuilder qryBuffer = new StringBuilder();
            qryBuffer
                    .append(" SELECT EXECUTED_UPTO,BEFORE_INTERVAL FROM PROCESS_STATUS WHERE process_id='"
                            + processId + "'");
            String query = qryBuffer.toString();
            con = OracleUtil.getConnection();
            pstm = con.prepareStatement(query);
            rst = pstm.executeQuery();
            con.commit();
            processStatusList = new ArrayList<ProcessStatusVO>();
            while (rst.next()) {
                processStatusMinutely = rst.getString("EXECUTED_UPTO") + "#"
                        + rst.getLong("BEFORE_INTERVAL");
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(mthdName, be);
            throw be;
        } catch (SQLException sqe) {
            LOG.errorTrace(mthdName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "CsvMinutelyProcessDAO[" + mthdName + "]", "", "", "",
                    "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("CsvMinutelyProcessDAO", mthdName,
                    "sql exception");
        } catch (Exception ex) {
            LOG.errorTrace(mthdName, ex);
        } finally {
            
            if (rst != null) {
                try {
                    rst.close();
                } catch (Exception exrst) {
                    LOG.errorTrace(mthdName + " error in result set ", exrst);
                }
            }
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (Exception expmt) {
                    LOG.errorTrace(mthdName + " error in statement ", expmt);
                }
            }

        }
        return processStatusMinutely;
    }
    
    
}

