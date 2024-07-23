/**
 * @(#)ScheduledBatchesDAO.java
 *                              Name Date History
 *                              ------------------------------------------------
 *                              ------------------------
 *                              Ashish Kumar 22/04/2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------
 *                              Copyright (c) 2006 Bharti Telesoft Ltd. This
 *                              class used to implement
 *                              the process related business logics.
 */
package com.selftopup.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// commented for DB2
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class ProcessStatusDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public ProcessStatusDAO() {

    }

    /**
     * This method is used to load the Process Status detail(processID,date-time
     * and status) from PROCESS_STATUS
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processID
     * @throws BTSLBaseException
     * @return ProcessStatusVO processStatusVO
     */
    public ProcessStatusVO loadProcessDetail(Connection p_con, String p_processID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetail", "Entered p_processID: " + p_processID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ProcessStatusVO processStatusVO = null;
        String sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval FROM process_status WHERE process_id=?";
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetail", "QUERY sqlSelect:" + sqlSelect);
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_processID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                processStatusVO = new ProcessStatusVO();
                processStatusVO.setProcessID(rs.getString("process_id"));
                processStatusVO.setStartDate(rs.getTimestamp("start_date"));
                processStatusVO.setProcessStatus(rs.getString("scheduler_status"));

                if (rs.getDate("executed_upto") != null)
                    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("executed_upto")));
                processStatusVO.setExecutedOn(rs.getDate("executed_on"));
                processStatusVO.setExpiryTime(rs.getLong("expiry_time"));
                processStatusVO.setBeforeInterval(rs.getLong("before_interval"));
            }
        } catch (SQLException sqe) {
            _log.error("loadProcessDetail", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "loadProcessDetail", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProcessDetail", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "loadProcessDetail", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadProcessDetail", "Exiting processStatusVO=" + processStatusVO);
        }
        return processStatusVO;
    }

    /**
     * This method is used to update the scheduler start date and status
     * 
     * @param Connection
     *            p_con
     * @param ProcessStatusVO
     *            p_processStatusVO
     * @return int updateCount
     * @throws BTSLBaseException
     */
    public int updateProcessDetail(Connection p_con, ProcessStatusVO p_processStatusVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetail Entered ", "p_processStatusVO=" + p_processStatusVO + " con is  " + p_con);
        int updateCount = 0;
        PreparedStatement pstmtUpdate = null;
        String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=? WHERE process_id=? ";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetail", "QUERY sqlUpdate=" + sqlUpdate);
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            int i = 1;
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessID());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateProcessDetail", "SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetail]", "", "", "", "SQL Exception while updating the process_status table" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetail", "Error in updating the process_status table");
        } catch (Exception e) {
            _log.error("updateProcessDetail", "Exception : " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetail]", "", "", "", "Exception while updating the process_status table" + e.getMessage());
            throw new BTSLBaseException(this, "updateProcessDetail", "Error in updating the process_status table");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateProcessDetail", "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * This method is used to update the scheduler start date and status for MIS
     * 
     * @param Connection
     *            p_con
     * @param ProcessStatusVO
     *            p_processStatusVO
     * @return int updateCount
     * @throws BTSLBaseException
     */
    public int updateProcessDetailForMis(Connection p_con, ProcessStatusVO p_processStatusVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailForMis Entered ", "p_processStatusVO=" + p_processStatusVO + " con is  " + p_con);
        int updateCount = 0;
        PreparedStatement pstmtUpdate = null;
        String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=? WHERE process_id=? ";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailForMis", "QUERY sqlUpdate=" + sqlUpdate);
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            int i = 1;
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessStatus());
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessID());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateProcessDetail", "SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailForMis]", "", "", "", "SQL Exception while updating the process_status table" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetailForMis", "Error in updating the process_status table");
        } catch (Exception e) {
            _log.error("updateProcessDetail", "Exception : " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailForMis]", "", "", "", "Exception while updating the process_status table" + e.getMessage());
            throw new BTSLBaseException(this, "updateProcessDetailForMis", "Error in updating the process_status table");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateProcessDetail", "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * This method is used to update the scheduler start date and status
     * 
     * @param Connection
     *            p_con
     * @param ProcessStatusVO
     *            p_processStatusVO
     * @return int updateCount
     * @throws BTSLBaseException
     */
    public int updateProcessDetailNetworkWise(Connection p_con, ProcessStatusVO p_processStatusVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailNetworkWise: ", "Entered p_processStatusVO=" + p_processStatusVO + " Process_ID=" + p_processStatusVO.getProcessID() + " Network_Code=" + p_processStatusVO.getNetworkCode());
        int updateCount = 0;
        PreparedStatement pstmtUpdate = null;
        String sqlUpdate = " UPDATE process_status SET start_date=?,scheduler_status=?,executed_upto=?,executed_on=? WHERE process_id=? and network_code=?";
        if (_log.isDebugEnabled())
            _log.debug("updateProcessDetailNetworkWise", " QUERY sqlUpdate=" + sqlUpdate);
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            int i = 1;
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getStartDate()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_processStatusVO.getExecutedUpto()));
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedOn()));
            pstmtUpdate.setString(i++, p_processStatusVO.getProcessID());
            pstmtUpdate.setString(i++, p_processStatusVO.getNetworkCode());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error("updateProcessDetailNetworkWise", " SQLException : " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailNetworkWise]", "", "", "", "SQL Exception while updating the process_status table" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "updateProcessDetailNetworkWise", "Error in updating the process_status table");
        } catch (Exception e) {
            _log.error("updateProcessDetailNetworkWise ", "Exception : " + e);
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[updateProcessDetailNetworkWise]", "", "", "", "Exception while updating the process_status table" + e.getMessage());
            throw new BTSLBaseException(this, "updateProcessDetail", "Error in updating the process_status table");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateProcessDetailNetworkWise ", "Exiting updateCount:" + updateCount);
        }
        return updateCount;
    }

    /**
     * This method is used to load the Process Status detail(processID,date-time
     * and status etc) from PROCESS_STATUS
     * by process_id and network_code.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processID
     * @param String
     *            p_networkCode
     * @throws BTSLBaseException
     * @return ProcessStatusVO processStatusVO
     * @author Vinay Singh
     */
    public ProcessStatusVO loadProcessDetailNetworkWise(Connection p_con, String p_processID, String p_networkCode) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetailNetworkWise", " Entered Process Id=: " + p_processID + " Network code=" + p_networkCode);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ProcessStatusVO processStatusVO = null;
        String sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval,network_code FROM process_status WHERE process_id=? and network_code=?";
        if (_log.isDebugEnabled())
            _log.debug("loadProcessDetailNetworkWise", " QUERY sqlSelect:" + sqlSelect);
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_processID);
            pstmtSelect.setString(2, p_networkCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                processStatusVO = new ProcessStatusVO();
                processStatusVO.setProcessID(rs.getString("process_id"));
                processStatusVO.setStartDate(rs.getTimestamp("start_date"));
                processStatusVO.setProcessStatus(rs.getString("scheduler_status"));
                if (rs.getDate("executed_upto") != null)
                    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("executed_upto")));
                processStatusVO.setExecutedOn(rs.getDate("executed_on"));
                processStatusVO.setExpiryTime(rs.getLong("expiry_time"));
                processStatusVO.setBeforeInterval(rs.getLong("before_interval"));
                processStatusVO.setNetworkCode(rs.getString("network_code"));
            }
        } catch (SQLException sqe) {
            _log.error("loadProcessDetailNetworkWise ", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", " loadProcessDetailNetworkWise", " error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadProcessDetailNetworkWise", " Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[loadProcessDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", " loadProcessDetailNetworkWise", " error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadProcessDetailNetworkWise", " Exiting processStatusVO=" + processStatusVO);
        }
        return processStatusVO;
    }

    /**
     * @param p_con
     * @param p_processID
     * @return
     * @throws BTSLBaseException
     */
    public ProcessStatusVO lockProcessStatusTable(Connection p_con, String p_processID) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("lockProcessStatusTable", "Entered p_processID: " + p_processID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ProcessStatusVO processStatusVO = null;
        String sqlSelect = null;
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
            sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval FROM process_status WHERE process_id=? for update WITH RS";
        else
            sqlSelect = "SELECT process_id,start_date,scheduler_status,executed_upto,executed_on,expiry_time,before_interval FROM process_status WHERE process_id=? for update NOWAIT";

        if (_log.isDebugEnabled())
            _log.debug("lockProcessStatusTable", "QUERY sqlSelect:" + sqlSelect);
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_processID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                processStatusVO = new ProcessStatusVO();
                processStatusVO.setProcessID(rs.getString("process_id"));
                processStatusVO.setStartDate(rs.getTimestamp("start_date"));
                processStatusVO.setProcessStatus(rs.getString("scheduler_status"));

                if (rs.getDate("executed_upto") != null)
                    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("executed_upto")));
                processStatusVO.setExecutedOn(rs.getDate("executed_on"));
                processStatusVO.setExpiryTime(rs.getLong("expiry_time"));
                processStatusVO.setBeforeInterval(rs.getLong("before_interval"));
            }
        } catch (SQLException sqe) {
            _log.error("lockProcessStatusTable", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[lockProcessStatusTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "lockProcessStatusTable", SelfTopUpErrorCodesI.PROCESS_ALREADY_RUNNING);
        } catch (Exception ex) {
            _log.error("lockProcessStatusTable", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProcessStatusDAO[lockProcessStatusTable]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ProcessStatusDAO", "lockProcessStatusTable", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("lockProcessStatusTable", "Exiting processStatusVO=" + processStatusVO);
        }
        return processStatusVO;
    }

}
