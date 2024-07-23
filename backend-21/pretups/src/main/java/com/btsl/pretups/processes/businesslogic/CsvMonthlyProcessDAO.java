/**
 * @(#)CsvMonthlyProcessDAO
 *                          Copyright(c) 2014, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          -----
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          -----
 *                          Yogesh Kumar Pandey 23/01/2015 Initial Creation
 *                          ----------------------------------------------------
 *                          -----
 */
package com.btsl.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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

public class CsvMonthlyProcessDAO {

    private static final Log LOG = LogFactory.getLog(CsvMonthlyProcessDAO.class.getName());

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
     * @throws SQLException
     *             ,Exception
     */
    public int updateMonthlyProcessStatus(Date StartedDate, Date executedUpto, String processId) throws BTSLBaseException {
        final String method_Nme = "updateMonthlyProcessStatus";
        int updateCount = 0;
        String sqlUpdate = " UPDATE process_status SET start_date=?,executed_upto=?,executed_on=? WHERE process_id=? ";
        if (LOG.isDebugEnabled()) {
            LOG.debug(method_Nme, "Update qrySelect:" + sqlUpdate);
        }
        updateCount = updateMonthlyProcess(StartedDate, executedUpto, processId, sqlUpdate);
        if (LOG.isDebugEnabled())
            LOG.debug("CsvMonthlyProcessDao", "Exiting " + method_Nme);
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
    private int updateMonthlyProcess(Date StartedDate, Date executedUpto, String processId, String sqlUpdate) throws BTSLBaseException {
        int u_updateCount = 0;
        u_updateCount = updateMonthly(StartedDate, executedUpto, processId, sqlUpdate);
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
    private int updateMonthly(Date StartedDate, Date executedUpto, String processId, String sqlUpdate) throws BTSLBaseException {
        final String method_N = "updateMonthly";
        int p_updateCount = 0;

        Connection con = null;
        MComConnectionI mcomCon = null;
        PreparedStatement pstmtUpdate = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            if (con == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(method_N, " DATABASE Connection is NULL ");
                }
            } else {
                pstmtUpdate = con.prepareStatement(sqlUpdate);
                pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(StartedDate));
                pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(executedUpto));
                pstmtUpdate.setDate(3, BTSLUtil.getSQLDateFromUtilDate(executedUpto));
                pstmtUpdate.setString(4, processId);
                p_updateCount = pstmtUpdate.executeUpdate();
                if (p_updateCount > 0) {
                    mcomCon.finalCommit();
                }
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(method_N, be);
            throw be;
        } catch (SQLException sq) {
            LOG.errorTrace(method_N, sq);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CsvMonthlyProcessDao[" + method_N + "]", "", "", "", "SQLException:" + sq.getMessage());
            throw new BTSLBaseException("CsvMonthlyProcessDao", method_N, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception ex) {
            LOG.errorTrace(method_N, ex);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CsvMonthlyProcessDAO#updateMonthly");
				mcomCon = null;
			}
			try {
                if (con != null) {
                	con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(method_N + " error in connection ", e);
            }
            try {
                if (pstmtUpdate != null) {
                	pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(method_N + " error in statement ", e);
            }
            
        }
        return p_updateCount;
    }

    /**
     * This method will close all connection resources
     * 
     * @param method
     *            String
     * @param p_con
     *            Connection
     * @param pstmtUpdate
     *            PreparedStatement
     * @return void
     * @throws Exception
     */
    private void closeMonthlyResourcesUpdate(final String methods, Connection p_conn, PreparedStatement pstmtsUpdate) {
        try {
            if (pstmtsUpdate != null) {
                pstmtsUpdate.close();
            }
        } catch (Exception e) {
            LOG.errorTrace(methods + " error in statement ", e);
        }
        try {
            if (p_conn != null) {
                p_conn.close();
            }
        } catch (Exception e) {
            LOG.errorTrace(methods + " error in connection ", e);
        }
    }

    /**
     * This method will fetch process status data based on processId from
     * database
     * 
     * @param processId
     *            String
     * @return String
     * @throws SQLException
     *             ,Exception
     */
    public String fetchMonthlyProcessStatus(String processId) throws BTSLBaseException {
        final String methodN = "fetchMonthlyProcessStatus";

        String processStatus = "";
        processStatus = fetchMonthly(processId, methodN);
        if (LOG.isDebugEnabled())
            LOG.debug("CsvMonthlyProcessDao", "Exiting " + methodN);
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
    private String fetchMonthly(String processId, final String method) throws BTSLBaseException {
        Connection con = null;
        MComConnectionI mcomCon = null;
        ResultSet resultst = null;
        PreparedStatement pstm = null;
        String processStatusMonthly = "";
        try {
            StringBuilder qryBuffer = new StringBuilder();
            qryBuffer.append(" SELECT EXECUTED_UPTO FROM PROCESS_STATUS WHERE process_id='" + processId + "'");
            String query = qryBuffer.toString();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            pstm = con.prepareStatement(query);
            if (LOG.isDebugEnabled()) {
                LOG.debug(method, "Select qrySelect:" + query);
            }
            resultst = pstm.executeQuery();
           mcomCon.finalCommit();
            while (resultst.next()) {
                processStatusMonthly = resultst.getString("EXECUTED_UPTO");
            }
        } catch (BTSLBaseException bex) {
            LOG.errorTrace(method, bex);
            throw bex;
        } catch (SQLException sqe) {
            LOG.errorTrace(method, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CsvMonthlyProcessDao[" + method + "]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("CsvMonthlyProcessDao", method, "sql exception");
        } catch (Exception ex) {
            LOG.errorTrace(method, ex);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("CsvMonthlyProcessDAO#fetchMonthly");
				mcomCon = null;
			}
            try {
            	if (con != null) {
            		con.close();
            	}
            } catch (Exception ex) {
                LOG.errorTrace(method + " error in connection ", ex);
            }
                try {
                	if (resultst != null) {
                		resultst.close();
                	}
                } catch (Exception ex) {
                    LOG.errorTrace(method + " error in result set ", ex);
                }
                try {
                	if (pstm != null) {
                		pstm.close();
                	 }
                } catch (Exception ex) {
                    LOG.errorTrace(method + " error in statement ", ex);
                }
                
            
        }
        return processStatusMonthly;
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
    private void closeResources(final String methodNme, Connection conn, ResultSet rset, PreparedStatement psm) {
        if (rset != null) {
            try {
                rset.close();
            } catch (Exception ex) {
                LOG.errorTrace(methodNme + " error in result set ", ex);
            }
        }
        if (psm != null) {
            try {
                psm.close();
            } catch (Exception ex) {
                LOG.errorTrace(methodNme + " error in statement ", ex);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {
                LOG.errorTrace(methodNme + " error in connection ", ex);
            }
        }
    }
}
