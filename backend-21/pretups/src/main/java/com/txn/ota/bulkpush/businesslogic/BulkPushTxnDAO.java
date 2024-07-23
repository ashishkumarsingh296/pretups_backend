package com.txn.ota.bulkpush.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimVO;
import com.btsl.util.BTSLUtil;

public class BulkPushTxnDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _jobName = null;

    /**
     * This method check entry for MSISDN and TID Job Table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            simVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean isExistsInJobTable(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "isExistsInJobTable";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered....... type=" + simVO.getUserType() + "   profile=" + simVO.getUserProfile() + "  location code" + simVO.getLocationCode() + "  msisdn=" + simVO.getUserMsisdn());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        boolean isExist = false;

        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT 1 FROM OTA_JOB_DATABASE ");
            sqlLoadBuf.append(" WHERE UPPER(TRANSACTION_ID) = ?  ");
            sqlLoadBuf.append(" AND msisdn=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getTransactionID().toUpperCase());
            dbPs.setString(2, simVO.getUserMsisdn());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
            return isExist;
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[isExistsInJobTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[isExistsInJobTable]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "isExist =" + isExist);
            }
        }
    }

    /**
     * This method is used to update Job Table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return boolean type
     * @throws BTSLBaseException
     */

    public boolean updateJobTable(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "updateJobTable";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered....... type=" + simVO.getUserType() + "   profile=" + simVO.getUserProfile() + "  location code" + simVO.getLocationCode() + "  msisdn=" + simVO.getUserMsisdn());
        }

        boolean isUpdate = false;
        PreparedStatement dbPs = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("UPDATE ota_job_database SET status = ? , modified_by = ? , modified_on = ? ");
            sqlLoadBuf.append(" WHERE msisdn = ? and UPPER(transaction_id) = ?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getStatus());
            dbPs.setString(2, simVO.getModifiedBy());
            dbPs.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(simVO.getModifedOn()));
            dbPs.setString(4, simVO.getUserMsisdn());
            dbPs.setString(5, simVO.getTransactionID().toUpperCase());
            int updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " update count=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdate = true;
            }
            return isUpdate;
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[updateJobTable]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[updateJobTable]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "isUpdate =" + isUpdate);
            }
        }

    }

    /**
     * This method returns the operation byte code from the temp table
     * 
     * @param con
     *            Connection type
     * @param simVO
     *            SimVO
     * @return String type
     * @throws BTSLBaseException
     */

    public String getOperationByteCodeJobIdAndBatchId(Connection con, SimVO simVO) throws BTSLBaseException {
        final String methodName = "getOperationByteCodeJob";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered....... MSISDN=" + simVO.getUserMsisdn() + "   Transaction ID=" + simVO.getTransactionID());
        }

        PreparedStatement dbPs = null;
        ResultSet rs = null;
        String operationJobIdAndBatchIdByteCode = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("SELECT operation , batch_id ,job_id FROM OTA_JOB_DATABASE ");
            sqlLoadBuf.append(" WHERE  UPPER(transaction_id) = ? ");
            sqlLoadBuf.append(" AND msisdn = ?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, simVO.getTransactionID().toUpperCase());
            dbPs.setString(2, simVO.getUserMsisdn());
            rs = dbPs.executeQuery();
            if (rs.next()) {
                operationJobIdAndBatchIdByteCode = rs.getString("operation") + "$" + rs.getString("batch_id") + "$" + rs.getString("job_id");
            }
            return operationJobIdAndBatchIdByteCode;
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[getOperationByteCodeJob]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[getOperationByteCodeJob]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..operationByteCode=" + operationJobIdAndBatchIdByteCode);
            }
        }
    }

    /**
     * This method is used to update Job master Table
     * 
     * @param con
     * @param jobId
     *            String
     * @param modifiedDate
     *            Date
     * @param modifiedBy
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean updateJobMaster(Connection con, String jobId, Date modifiedDate, String modifiedBy) throws BTSLBaseException {
        final String methodName = "updateJobMaster";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered....... Job Id =" + jobId + "   Date=" + modifiedDate + "  Modified By " + modifiedBy);
        }

        boolean isUpdate = false;
        PreparedStatement dbPs = null;
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("UPDATE job_master SET mobile_count = mobile_count+1 , modified_by = ? , modified_on = ? WHERE");
            sqlLoadBuf.append(" job_id = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, ",QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, modifiedBy);
            dbPs.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(modifiedDate));
            dbPs.setString(3, jobId);
            int updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " update count=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdate = true;
            }
            return isUpdate;
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[updateJobMaster]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[updateJobMaster]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..isUpdate=" + isUpdate);
            }
        }
    }

    /**
     * This method is used to update Batch Master Table
     * 
     * @param con
     * @param jobId
     *            String
     * @param batchId
     *            String
     * @param modifiedDate
     *            Date
     * @param modifiedBy
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean updateBatchMaster(Connection con, String jobId, String batchId, Date modifiedDate, String modifiedBy) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateBatchMaster ", "Entered....... Job Id =" + jobId + " BatchId= " + batchId + "   Date=" + modifiedDate + "  Modified By " + modifiedBy);
        }

        boolean isUpdate = false;
        PreparedStatement dbPs = null;

        final String methodName = "updateBatchMaster";
        try {
            StringBuffer sqlLoadBuf = new StringBuffer("UPDATE batch_master SET mobile_count = mobile_count+1 , modified_by = ? , modified_on = ? WHERE");
            sqlLoadBuf.append(" job_id = ? AND batch_id = ? ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + sqlLoadBuf.toString());
            }
            dbPs = con.prepareStatement(sqlLoadBuf.toString());
            dbPs.setString(1, modifiedBy);
            dbPs.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(modifiedDate));
            dbPs.setString(3, jobId);
            dbPs.setString(4, batchId);
            int updateCount = dbPs.executeUpdate();
            if (_log.isDebugEnabled()) {
                _log.debug("updateBatchMaster ", "update count=" + updateCount);
            }
            if (updateCount > 0) {
                isUpdate = true;
            }
            return isUpdate;
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[updateBatchMaster]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateJobMaster", "error.general.sql.processing");
        }

        catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BulkPushDAO[updateBatchMaster]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (dbPs != null) {
                    dbPs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..isUpdate=" + isUpdate);
            }
        }
    }
}
