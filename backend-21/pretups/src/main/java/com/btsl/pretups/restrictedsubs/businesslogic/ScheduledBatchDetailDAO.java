package com.btsl.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.ScheduleFileProcessLog;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class ScheduledBatchDetailDAO {

    private static final Log log = LogFactory.getLog(ScheduledBatchDetailDAO.class.getName());
    /**
     * Method isScheduleExistByStatus.
     * This method is to check that the schedule is exist of the passed status
     * under the passed owner and msisdn
     * 
     * @param p_con
     *            Connection
     * @param p_subscriberList
     *            ArrayList
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @return String
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    public String isScheduleExistByStatus(Connection p_con, List p_subscriberList, String p_statusUsed, String p_status, String p_ownerID, Date p_scheduleDate) throws BTSLBaseException {
        final String methodName = "isScheduleExistByStatus";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_subscriberList size = " + p_subscriberList.size() + ", p_subscriberList = " + p_subscriberList + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + ", p_ownerID=" + p_ownerID + ", p_scheduleDate=" + p_scheduleDate);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer returnDataStrBuff = new StringBuffer();
        String returnStr = null;
        StringBuffer strBuff = new StringBuffer("SELECT 1 FROM scheduled_batch_detail SBD,scheduled_batch_master SBM ");
        strBuff.append("WHERE SBM.batch_id=SBD.batch_id AND SBD.msisdn = ? AND SBM.owner_id = ?");
        if (p_scheduleDate != null) {
            strBuff.append("AND SBM.scheduled_date > ? ");
        }
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND SBD.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND SBD.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND SBD.status <> ? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND SBD.status NOT IN (" + p_status + ")");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            ScheduleBatchDetailVO scheduleDetailVO = null;
            for (int index = 0, j = p_subscriberList.size(); index < j; index++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) p_subscriberList.get(index);
                int i = 1;
                pstmtSelect.setString(i++, scheduleDetailVO.getMsisdn());
                pstmtSelect.setString(i++, p_ownerID);
                if (p_scheduleDate != null) {
                    pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_scheduleDate));
                }
                if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                    pstmtSelect.setString(i++, p_status);
                }
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    returnDataStrBuff.append(scheduleDetailVO.getMsisdn() + ",");
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                pstmtSelect.clearParameters();
            }
            if (returnDataStrBuff.length() > 0) {
                returnStr = returnDataStrBuff.substring(0, returnDataStrBuff.length() - 1);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[isScheduleExistByStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[isScheduleExistByStatus]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting returnStr=" + returnStr);
            }
        }
        return returnStr;
    }

    /**
     * Method addScheduleBatchMaster.
     * This method is to add the information of the schedule batch in the parent
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_scheduleMasterVO
     *            ScheduleBatchMasterVO
     * @return int
     * @throws BTSLBaseException
     * @author sandeep.goel
     *         modify the query insert batch_type in scheduled_batch_master
     */
    public int addScheduleBatchMaster(Connection p_con, ScheduleBatchMasterVO p_scheduleMasterVO) throws BTSLBaseException {
        final String methodName = "addScheduleBatchMaster";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_scheduleMasterVO=" + p_scheduleMasterVO);
        }

        PreparedStatement pstmtInsert = null;
        int addCount = 0;

        try {
            StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO scheduled_batch_master (batch_id, status, ");
            insertQueryBuff.append("network_code, total_count, successful_count, upload_failed_count, process_failed_count,");
            insertQueryBuff.append("cancelled_count, scheduled_date, parent_id, owner_id, parent_category, parent_domain, ");
            if("".equalsIgnoreCase(p_scheduleMasterVO.getFrequency()) && p_scheduleMasterVO.getIterations() == null){
            	insertQueryBuff.append("service_type, created_on, created_by, modified_on, modified_by, initiated_by,ref_batch_id,batch_type,active_user_id) ");
            }else{
            	insertQueryBuff.append("service_type, created_on, created_by, modified_on, modified_by, initiated_by,ref_batch_id,batch_type,active_user_id, frequency, iteration ) ");
            }
            if("".equalsIgnoreCase(p_scheduleMasterVO.getFrequency()) && p_scheduleMasterVO.getIterations() == null){
            	insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            }else{
            	insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            }
            
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY insertQuery:" + insertQueryBuff);
            }

            pstmtInsert = p_con.prepareStatement(insertQueryBuff.toString());
            int i = 1;
            pstmtInsert.setString(i++, p_scheduleMasterVO.getBatchID());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getStatus());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getNetworkCode());
            pstmtInsert.setLong(i++, p_scheduleMasterVO.getTotalCount());
            pstmtInsert.setLong(i++, p_scheduleMasterVO.getSuccessfulCount());
            pstmtInsert.setLong(i++, p_scheduleMasterVO.getUploadFailedCount());
            pstmtInsert.setLong(i++, p_scheduleMasterVO.getProcessFailedCount());
            pstmtInsert.setLong(i++, p_scheduleMasterVO.getCancelledCount());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getScheduledDate()));
            pstmtInsert.setString(i++, p_scheduleMasterVO.getParentID());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getOwnerID());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getParentCategory());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getParentDomain());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getServiceType());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_scheduleMasterVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_scheduleMasterVO.getModifiedBy());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getInitiatedBy());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getRefBatchID());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getBatchType());
            pstmtInsert.setString(i++, p_scheduleMasterVO.getActiveUserId());

            if(!"".equalsIgnoreCase(p_scheduleMasterVO.getFrequency()) && !(p_scheduleMasterVO.getIterations() == null)){
            	  pstmtInsert.setString(i++, p_scheduleMasterVO.getFrequency());
                  pstmtInsert.setInt(i++, p_scheduleMasterVO.getIterations());
            }
          
            addCount = pstmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[addScheduleBatchMaster]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[addScheduleBatchMaster]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally
        return addCount;
    }

    
    /**
     * Method updateScheduleBatchMaster.
     * This method is to update the informaiton of the schedule batch in the
     * parent table
     * 
     * @param p_con
     *            Connection
     * @param p_scheduleMasterVO
     *            ScheduleBatchMasterVO
     * @return int
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    public int updateScheduleBatchMaster(Connection p_con, ScheduleBatchMasterVO p_scheduleMasterVO) throws BTSLBaseException {
        final String methodName = "updateScheduleBatchMaster";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_scheduleMasterVO=" + p_scheduleMasterVO);
        }

        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;

        try {
            StringBuffer updateQueryBuff = new StringBuffer("UPDATE scheduled_batch_master SET status=?, ");
            updateQueryBuff.append("network_code=?, total_count=?, successful_count=?, upload_failed_count=?, ");
            updateQueryBuff.append("process_failed_count=?, cancelled_count=?, scheduled_date=?, parent_id=?, ");
            updateQueryBuff.append("owner_id=?, parent_category=?, parent_domain=?, service_type=?, modified_on=?, ");
            updateQueryBuff.append("modified_by=?, initiated_by =?,ref_batch_id=? ");
            updateQueryBuff.append("WHERE batch_id=? ");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY insertQuery:" + updateQueryBuff);
            }

            pstmtUpdate = p_con.prepareStatement(updateQueryBuff.toString());
            int i = 1;
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getStatus());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getNetworkCode());
            pstmtUpdate.setLong(i++, p_scheduleMasterVO.getTotalCount());
            pstmtUpdate.setLong(i++, p_scheduleMasterVO.getSuccessfulCount());
            pstmtUpdate.setLong(i++, p_scheduleMasterVO.getUploadFailedCount());
            pstmtUpdate.setLong(i++, p_scheduleMasterVO.getProcessFailedCount());
            pstmtUpdate.setLong(i++, p_scheduleMasterVO.getCancelledCount());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getScheduledDate()));
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getParentID());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getOwnerID());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getParentCategory());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getParentDomain());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getServiceType());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getInitiatedBy());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getRefBatchID());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getBatchID());
            /*
             * boolean modified = this.isScheduleBatchMasterModified(p_con,
             * p_scheduleMasterVO
             * .getLastModifiedTime(),p_scheduleMasterVO.getBatchID());
             * if (modified)
             * throw new BTSLBaseException(this, "updateScheduleBatchMaster",
             * "error.modify.true");
             */
            updateCount = pstmtUpdate.executeUpdate();
        } // end of try
        /*
         * catch(BTSLBaseException be)
         * {
         * throw be;
         * }
         */
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateScheduleBatchMaster]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateScheduleBatchMaster]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * Method addScheduleDetails
     * This method is for adding the deatils of the schedule in the child table
     * of the schedule module
     * 
     * @param p_con
     * @param p_arrayList
     * @param p_isMarkLog
     *            boolean
     * @return int
     * @throws BTSLBaseException
     *             int
     * @author sandeep.goel
     *         modify the query insert r_language, r_country, donor_msisdn,
     *         donor_name, d_language, d_country in scheduled_batch_detail
     */
    public int addScheduleDetails(Connection p_con, List p_arrayList, boolean p_isMarkLog) throws BTSLBaseException {
        final String methodName = "addScheduleDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_arrayList.size()=" + p_arrayList.size());
        }

        PreparedStatement pstmtInsert = null;
        ScheduleBatchDetailVO scheduleDetailVO = null;
        int addCount = 0;

        try {
            StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO scheduled_batch_detail (batch_id, ");
            insertQueryBuff.append("subscriber_id, msisdn, amount, processed_on, status, transfer_id, ");
            insertQueryBuff.append("transfer_status, created_on, created_by, modified_on, modified_by,sub_service ");
            insertQueryBuff.append(",r_language, r_country, donor_msisdn, donor_name, d_language, d_country )");
            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY insertQuery:" + insertQueryBuff);
            }

            pstmtInsert = p_con.prepareStatement(insertQueryBuff.toString());
            int tempCount = 0;
            for (int index = 0, j = p_arrayList.size(); index < j; index++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) p_arrayList.get(index);
                int i = 1;
                pstmtInsert.setString(i++, scheduleDetailVO.getBatchID());
                pstmtInsert.setString(i++, scheduleDetailVO.getSubscriberID());
                pstmtInsert.setString(i++, scheduleDetailVO.getMsisdn());
                pstmtInsert.setLong(i++, scheduleDetailVO.getAmount());
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(scheduleDetailVO.getProcessedOn()));
                pstmtInsert.setString(i++, scheduleDetailVO.getStatus());
                pstmtInsert.setString(i++, scheduleDetailVO.getTransactionID());
                pstmtInsert.setString(i++, scheduleDetailVO.getTransactionStatus());
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(scheduleDetailVO.getCreatedOn()));
                pstmtInsert.setString(i++, scheduleDetailVO.getCreatedBy());
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(scheduleDetailVO.getModifiedOn()));
                pstmtInsert.setString(i++, scheduleDetailVO.getModifiedBy());
                pstmtInsert.setString(i++, scheduleDetailVO.getSubService());
                pstmtInsert.setString(i++, BTSLUtil.NullToString(scheduleDetailVO.getLanguage()));
                pstmtInsert.setString(i++, BTSLUtil.NullToString(scheduleDetailVO.getCountry()));
                pstmtInsert.setString(i++, BTSLUtil.NullToString(scheduleDetailVO.getDonorMsisdn()));
                pstmtInsert.setString(i++, BTSLUtil.NullToString(scheduleDetailVO.getDonorName()));
                pstmtInsert.setString(i++, BTSLUtil.NullToString(scheduleDetailVO.getDonorLanguage()));
                pstmtInsert.setString(i++, BTSLUtil.NullToString(scheduleDetailVO.getDonorCountry()));

                tempCount = pstmtInsert.executeUpdate();
                if (p_isMarkLog) {
                    ScheduleFileProcessLog.log("Inserting Data in Database", scheduleDetailVO.getCreatedBy(), scheduleDetailVO.getMsisdn(), scheduleDetailVO.getBatchID(), "Record is inserted in Database", tempCount > 0 ? "PASS" : "FAIL", "TYPE = Schedule");
                }
                addCount += tempCount;
                pstmtInsert.clearParameters();
            }

        } // end of try
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[addScheduleDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[addScheduleDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: addCount=" + addCount);
            }
        } // end of finally
        return addCount;
    }

    /**
     * This method load schedule batch master on the basis of parent_id, status,
     * and scheduled_date.
     * This method return ArrayList. in arraylist has ScheduleBatchMasterVO.
     * Method loadScheduleBatchMasterList
     * 
     * @author babu.kunwar
     * @param p_con
     *            Connection
     * @param p_parent_id
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @param p_scheduleDate
     *            Date
     * @param p_fromScheduleDate
     *            Date
     * @param p_toScheduleDate
     *            Date
     * @return ArrayList
     * @throws BTSLBaseException
     *             int
     *             modify query apply join b/w service type and
     *             scheduled_batch_master and select service type
     */
    public ArrayList loadScheduleBatchMasterList(Connection p_con, String p_parent_id, String p_statusUsed, String p_status, Date p_scheduleDate, Date p_fromScheduleDate, Date p_toScheduleDate, String p_serviceType, boolean p_isStaffUser, String p_activeUserId) throws BTSLBaseException {
        String methodName = "loadScheduleBatchMasterList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_parent_id = " + p_parent_id + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + ",p_scheduleDate=" + p_scheduleDate + ",p_fromScheduleDate= " + p_fromScheduleDate + ", p_toScheduleDate=" + p_toScheduleDate + " ,p_serviceType=" + p_serviceType + " p_isStaffUser= " + p_isStaffUser + " p_activeUserId= " + p_activeUserId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ScheduleBatchMasterVO scheduleMasterVO = null;
        ArrayList scheduleMasterList = new ArrayList();
        try {
            StringBuffer selectSQL = new StringBuffer(" SELECT L.lookup_name status_desc, ");
            selectSQL.append(" SBM.batch_id, SBM.status,SBM.created_on,SBM.created_by, SBM.network_code, SBM.total_count, ");
            selectSQL.append(" SBM.successful_count, SBM.upload_failed_count, SBM.process_failed_count, ");
            selectSQL.append(" SBM.cancelled_count, SBM.scheduled_date, SBM.parent_id, SBM.owner_id, ");
            selectSQL.append(" SBM.parent_category, SBM.parent_domain, SBM.service_type,ST.name,SBM.initiated_by,SBM.FREQUENCY,SBM.ITERATION,SBM.EXECUTED_ITERATIONS,SBM.PROCESSED_ON, ");
            selectSQL.append(" U1.user_name initiated_by_name,U3.user_name created_by_name,SBM.modified_on,SBM.ref_batch_id,SBM.batch_type,SBM.active_user_id, U2.user_name active_user_name ");
            selectSQL.append(" FROM scheduled_batch_master SBM, users U1, lookups L,service_type ST, Users U2, Users U3 ");
            selectSQL.append(" WHERE SBM.parent_id= ? ");
            selectSQL.append(" AND SBM.service_type=ST.service_type ");

            if (!BTSLUtil.isNullString(p_serviceType)) {
                selectSQL.append(" AND ST.service_type = ? ");
            }
            if (p_scheduleDate != null) {
                selectSQL.append(" AND SBM.scheduled_date > ? ");
            }
            if (p_fromScheduleDate != null && p_toScheduleDate != null) {
                selectSQL.append(" AND SBM.scheduled_date >= ? ");
                selectSQL.append(" AND SBM.scheduled_date <= ? ");
            }
            selectSQL.append(" AND SBM.initiated_by = U1.user_id ");
            selectSQL.append(" AND SBM.created_by = U3.user_id ");
            selectSQL.append(" AND SBM.status = L.lookup_code ");
            selectSQL.append(" AND L.lookup_type = ? ");
            String []args = p_status.split(",");
            if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
                selectSQL.append("AND SBM.status IN");
                BTSLUtil.pstmtForInQuery(args, selectSQL);
            } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                selectSQL.append("AND SBM.status =? ");
            } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                selectSQL.append("AND SBM.status <> ? ");
            } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
                selectSQL.append("AND SBM.status NOT IN");
                BTSLUtil.pstmtForInQuery(args, selectSQL);
            }
            selectSQL.append(" AND SBM.active_user_id=U2.user_id ");
            if (p_isStaffUser) {
                selectSQL.append("AND SBM.active_user_id=?");
            }
            selectSQL.append(" ORDER BY SBM.created_on DESC ");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY SelectQuery:" + selectSQL);
            }

            pstmtSelect = p_con.prepareStatement(selectSQL.toString());
            int i = 0;
            pstmtSelect.setString(++i, p_parent_id);
            if (!BTSLUtil.isNullString(p_serviceType)) {
            	pstmtSelect.setString(++i,p_serviceType );
            }
            if (p_scheduleDate != null) {
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_scheduleDate));
            }
            if (p_fromScheduleDate != null && p_toScheduleDate != null) {
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_fromScheduleDate));
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_toScheduleDate));
            }
            pstmtSelect.setString(++i, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(++i, p_status);
            }
            else if( p_statusUsed.equals(PretupsI.STATUS_IN) || p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            	for(int j=0;j<args.length;j++)
            	{
            		String param = args[j];
            		param = param.replace("'", "");
            		 pstmtSelect.setString(++i, param);
            	}
            }
            if (p_isStaffUser) {
            	pstmtSelect.setString(++i, p_activeUserId);
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                scheduleMasterVO = new ScheduleBatchMasterVO();
                scheduleMasterVO.setStatusDesc(rs.getString("status_desc"));
                scheduleMasterVO.setBatchID(rs.getString("batch_id"));
                scheduleMasterVO.setStatus(rs.getString("status"));
                scheduleMasterVO.setNetworkCode(rs.getString("network_code"));
                scheduleMasterVO.setTotalCount(rs.getLong("total_count"));
                scheduleMasterVO.setSuccessfulCount(rs.getLong("successful_count"));
                scheduleMasterVO.setUploadFailedCount(rs.getLong("upload_failed_count"));
                scheduleMasterVO.setProcessFailedCount(rs.getLong("process_failed_count"));
                scheduleMasterVO.setCancelledCount(rs.getLong("cancelled_count"));
                scheduleMasterVO.setScheduledDate(rs.getDate("scheduled_date"));
                scheduleMasterVO.setScheduledDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(scheduleMasterVO.getScheduledDate())));
                scheduleMasterVO.setCreatedOn(rs.getDate("created_on"));
                scheduleMasterVO.setCreatedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(scheduleMasterVO.getCreatedOn())));
                scheduleMasterVO.setCreatedBy(rs.getString("created_by_name"));
                scheduleMasterVO.setParentID(rs.getString("parent_id"));
                scheduleMasterVO.setOwnerID(rs.getString("owner_id"));
                scheduleMasterVO.setParentCategory(rs.getString("parent_category"));
                scheduleMasterVO.setParentDomain(rs.getString("parent_domain"));
                scheduleMasterVO.setServiceType(rs.getString("service_type"));
                scheduleMasterVO.setServiceName(rs.getString("name"));
                scheduleMasterVO.setInitiatedBy(rs.getString("initiated_by"));
                scheduleMasterVO.setInitiatedByName(rs.getString("initiated_by_name"));
                scheduleMasterVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                scheduleMasterVO.setRefBatchID(rs.getString("ref_batch_id"));
                scheduleMasterVO.setNoOfRecords(scheduleMasterVO.getTotalCount() - scheduleMasterVO.getUploadFailedCount());
                scheduleMasterVO.setBatchIDDisp(scheduleMasterVO.getBatchID() + "(" + scheduleMasterVO.getScheduledDateStr() + ")");
                scheduleMasterVO.setBatchType(rs.getString("batch_type"));
                scheduleMasterVO.setActiveUserId(rs.getString("active_user_id"));
                scheduleMasterVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleMasterVO.setFrequency(rs.getString("frequency"));
                scheduleMasterVO.setIterations(rs.getInt("iteration"));
                scheduleMasterVO.setExecutedIterations(rs.getInt("executed_iterations"));
                scheduleMasterVO.setProcessedOn(rs.getDate("processed_on"));
                if(scheduleMasterVO.getProcessedOn()!=null)
                	scheduleMasterVO.setProcessedOnStr(BTSLUtil.getDateStringFromDate(scheduleMasterVO.getProcessedOn()));
                else
                	scheduleMasterVO.setProcessedOnStr("");
                scheduleMasterList.add(scheduleMasterVO);
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleBatchMasterList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleBatchMasterList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List Size=" + scheduleMasterList.size());
            }
        }
        return scheduleMasterList;
    }
    
    /**
     * This method load schedule batch master on the basis of parent_id.
     * This method return ArrayList. in arraylist has ScheduleBatchMasterVO.
     * Method loadScheduleBatchMasterList
     * 
     * @author yogesh.dixit
     * @param p_con
     *            Connection
     * @param p_parent_id
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     *             int
     *             modify query apply join b/w service type and
     *             scheduled_batch_master and select service type
     */
    public ArrayList loadScheduleBatchMasterDetails(Connection p_con, String p_parent_id, boolean p_isStaffUser, String p_activeUserId,String batchID) throws BTSLBaseException {
        String methodName = "loadScheduleBatchMasterDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_parent_id = " + p_parent_id + ",p_fromScheduleDate= " + " p_isStaffUser= " + p_isStaffUser + " p_activeUserId= " + p_activeUserId+" BatchID= "+batchID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ScheduleBatchMasterVO scheduleMasterVO = null;
        ArrayList scheduleMasterList = new ArrayList();
        try {
            StringBuffer selectSQL = new StringBuffer(" SELECT L.lookup_name status_desc, ");
            selectSQL.append(" SBM.batch_id, SBM.status, SBM.network_code, SBM.total_count,SBM.CREATED_ON,SBM.CREATED_BY, ");
            selectSQL.append(" SBM.successful_count, SBM.upload_failed_count, SBM.process_failed_count, ");
            selectSQL.append(" SBM.cancelled_count, SBM.scheduled_date, SBM.parent_id, SBM.owner_id, ");
            selectSQL.append(" SBM.parent_category, SBM.parent_domain, SBM.service_type,ST.name,SBM.initiated_by,SBM.FREQUENCY,SBM.ITERATION,SBM.EXECUTED_ITERATIONS,SBM.PROCESSED_ON, ");
            selectSQL.append(" U1.user_name initiated_by_name,SBM.modified_on,SBM.ref_batch_id,SBM.batch_type,SBM.active_user_id, U2.user_name active_user_name ");
            selectSQL.append(" FROM scheduled_batch_master SBM, users U1, lookups L,service_type ST, Users U2 ");
            selectSQL.append(" WHERE SBM.parent_id=? ");
            selectSQL.append(" AND SBM.BATCH_ID=? ");
            selectSQL.append(" AND SBM.service_type=ST.service_type ");
            selectSQL.append(" AND SBM.initiated_by= U1.user_id ");
            selectSQL.append(" AND SBM.status= L.lookup_code ");
            selectSQL.append(" AND L.lookup_type= ? ");
            selectSQL.append(" AND SBM.active_user_id=U2.user_id ");
            if (p_isStaffUser) {
                selectSQL.append("AND SBM.active_user_id=?");
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY SelectQuery:" + selectSQL);
            }

            pstmtSelect = p_con.prepareStatement(selectSQL.toString());
            int i = 0;
            pstmtSelect.setString(++i, p_parent_id);
            pstmtSelect.setString(++i, batchID);
            pstmtSelect.setString(++i, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            
            if (p_isStaffUser) {
            	pstmtSelect.setString(++i, p_activeUserId);
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                scheduleMasterVO = new ScheduleBatchMasterVO();
                scheduleMasterVO.setStatusDesc(rs.getString("status_desc"));
                scheduleMasterVO.setBatchID(rs.getString("batch_id"));
                scheduleMasterVO.setStatus(rs.getString("status"));
                scheduleMasterVO.setNetworkCode(rs.getString("network_code"));
                scheduleMasterVO.setTotalCount(rs.getLong("total_count"));
                scheduleMasterVO.setSuccessfulCount(rs.getLong("successful_count"));
                scheduleMasterVO.setUploadFailedCount(rs.getLong("upload_failed_count"));
                scheduleMasterVO.setProcessFailedCount(rs.getLong("process_failed_count"));
                scheduleMasterVO.setCancelledCount(rs.getLong("cancelled_count"));
                scheduleMasterVO.setScheduledDate(rs.getDate("scheduled_date"));
                scheduleMasterVO.setScheduledDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(scheduleMasterVO.getScheduledDate())));
                scheduleMasterVO.setParentID(rs.getString("parent_id"));
                scheduleMasterVO.setOwnerID(rs.getString("owner_id"));
                scheduleMasterVO.setCreatedOn(rs.getDate("created_on"));
                scheduleMasterVO.setCreatedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("created_on"))));
                scheduleMasterVO.setCreatedBy(rs.getString("created_by"));
                scheduleMasterVO.setParentCategory(rs.getString("parent_category"));
                scheduleMasterVO.setParentDomain(rs.getString("parent_domain"));
                scheduleMasterVO.setServiceType(rs.getString("service_type"));
                scheduleMasterVO.setServiceName(rs.getString("name"));
                scheduleMasterVO.setInitiatedBy(rs.getString("initiated_by"));
                scheduleMasterVO.setInitiatedByName(rs.getString("initiated_by_name"));
                scheduleMasterVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                scheduleMasterVO.setRefBatchID(rs.getString("ref_batch_id"));
                scheduleMasterVO.setNoOfRecords(scheduleMasterVO.getTotalCount() - scheduleMasterVO.getUploadFailedCount());
                scheduleMasterVO.setBatchIDDisp(scheduleMasterVO.getBatchID() + "(" + scheduleMasterVO.getScheduledDateStr() + ")");
                scheduleMasterVO.setBatchType(rs.getString("batch_type"));
                scheduleMasterVO.setActiveUserId(rs.getString("active_user_id"));
                scheduleMasterVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleMasterVO.setFrequency(rs.getString("frequency"));
                scheduleMasterVO.setIterations(rs.getInt("iteration"));
                scheduleMasterVO.setExecutedIterations(rs.getInt("executed_iterations"));
                scheduleMasterVO.setProcessedOn(rs.getDate("processed_on"));
                if(scheduleMasterVO.getProcessedOn()!=null)
                	scheduleMasterVO.setProcessedOnStr(BTSLUtil.getDateStringFromDate(scheduleMasterVO.getProcessedOn()));
                else
                	scheduleMasterVO.setProcessedOnStr("");
                scheduleMasterList.add(scheduleMasterVO);
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleBatchMasterList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleBatchMasterList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List Size=" + scheduleMasterList.size());
            }
        }
        return scheduleMasterList;
    }    

    /**
     * Method isScheduleBatchMasterModified.
     * This method is used to check that is the record modified during the
     * processing. *
     * 
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_key
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isScheduleBatchMasterModified(Connection p_con, long p_oldlastModified, String p_key) throws BTSLBaseException {
        final String methodName = "isScheduleBatchMasterModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_key=" + p_key);
        }
        boolean modified = false;
        if (p_oldlastModified == 0) {
            modified = false;
            return modified;
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        StringBuffer sqlRecordModified = new StringBuffer("SELECT modified_on FROM scheduled_batch_master WHERE batch_id=?");
        java.sql.Timestamp newlastModified = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            pstmtSelect = p_con.prepareStatement(sqlRecordModified.toString());
            pstmtSelect.setString(1, p_key);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            } else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[isScheduleBatchMasterModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch(SQLException sqe)
        catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[isScheduleBatchMasterModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch(Exception e)
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * updateScheduleBatchMasterStatus
     * This method update status, modify_by, modify_on, ref_batch_id,
     * cancelled_count on the basis of batch_id
     * 
     * @param p_con
     *            Connection
     * @param p_scheduleMasterVO
     *            ScheduleBatchMasterVO
     * @throws BTSLBaseException
     * @author ved.sharma
     */

    public int updateScheduleBatchMasterStatus(Connection p_con, ScheduleBatchMasterVO p_scheduleMasterVO) throws BTSLBaseException {
        final String methodName = "updateScheduleBatchMasterStatus";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_scheduleMasterVO: " + p_scheduleMasterVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        StringBuffer strBuff = new StringBuffer("UPDATE scheduled_batch_master ");
        strBuff.append(" SET status =? , modified_on =?, modified_by = ?,ref_batch_id=?, cancelled_count=? ");
        strBuff.append(" WHERE batch_id =? ");
        String sqlUpdate = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            int i = 1;
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getRefBatchID());
            pstmtUpdate.setLong(i++, p_scheduleMasterVO.getCancelledCount());
            pstmtUpdate.setString(i++, p_scheduleMasterVO.getBatchID());
            boolean modified = this.isScheduleBatchMasterModified(p_con, p_scheduleMasterVO.getLastModifiedTime(), p_scheduleMasterVO.getBatchID());
            if (modified) {
                throw new BTSLBaseException(this, "updateScheduleBatchMaster", "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
        } // end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateScheduleBatchMasterStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateScheduleBatchMasterStatus]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * updateScheduleStatus
     * This method update status, modify_by, modify_on on the basis of batch_id,
     * and mobile no.
     * if mobile no is blank then only modify by batch_id
     * 
     * @param p_con
     *            Connection
     * @param p_scheduleDetailVO
     *            ScheduleBatchDetailVO
     * @throws BTSLBaseException
     * @author ved.sharma
     */
    public int updateScheduleStatus(Connection p_con, ScheduleBatchDetailVO p_scheduleDetailVO) throws BTSLBaseException {
        final String methodName = "updateScheduleStatus";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_scheduleDetailVO: " + p_scheduleDetailVO);
        }

        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        StringBuffer strBuff = new StringBuffer("UPDATE scheduled_batch_detail ");
        strBuff.append(" SET status =? , modified_on =?, modified_by = ? WHERE batch_id =? ");
        if (!BTSLUtil.isNullString(p_scheduleDetailVO.getMsisdn())) {
            strBuff.append(" AND msisdn=? ");
        }
        String sqlUpdate = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            int i = 1;
            pstmtUpdate.setString(i++, p_scheduleDetailVO.getStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_scheduleDetailVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_scheduleDetailVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_scheduleDetailVO.getBatchID());
            if (!BTSLUtil.isNullString(p_scheduleDetailVO.getMsisdn())) {
                pstmtUpdate.setString(i++, p_scheduleDetailVO.getMsisdn());
            }
            updateCount = pstmtUpdate.executeUpdate();
        } // end of try
        catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateScheduleStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch (SQLException sqe)
        catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateScheduleStatus]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch (Exception ex)
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }// end of finally
        return updateCount;
    } // end of updateScheduleStatus

    /**
     * Method loadScheduleDetailVOList.
     * This method to load the scheduleDetails list on the basis of the ownerID
     * and the MSISDN and the status
     * 
     * @param p_con
     *            Connection
     * @param p_ownerID
     *            String
     * @param p_parentID
     *            TODO
     * @param p_msisdn
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     *         modify query select batch_type from scheduled_batch_master
     */
    public ArrayList loadScheduleDetailVOList(Connection p_con, String p_ownerID, String p_parentID, String p_msisdn, String p_statusUsed, String p_status, boolean p_isStaffUser, String p_userId) throws BTSLBaseException {
        final String methodName = "loadScheduleDetailVOList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_msisdn = " + p_msisdn + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + ", p_ownerID=" + p_ownerID + " p_parentID=" + p_parentID + "p_msisdn" + p_msisdn + "p_statusUsed" + p_statusUsed + "p_status" + p_status + "p_isStaffUser" + p_isStaffUser + "p_userId" + p_userId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList scheduleDetailsVOList = new ArrayList();
      /*  StringBuffer strBuff = new StringBuffer(" SELECT SBD.msisdn, SBD.status, L.lookup_name status_desc,SBD.transfer_status,");
        strBuff.append(" KV.value transfer_status_desc,SBD.processed_on, SBD.transfer_id, SBD.amount, SBD.batch_id, ");
        strBuff.append(" SBD.subscriber_id, SBM.network_code, SBM.scheduled_date,SBM.service_type, SBM.created_on, SBM.service_type stype, SBM.FREQUENCY,SBM.ITERATION,SBM.EXECUTED_ITERATIONS,");
        strBuff.append(" SBM.initiated_by, U.user_name created_by_name, ST.description, SBD.sub_service, SBM.batch_type, U1.user_name active_user_name ");
        strBuff.append(" FROM scheduled_batch_detail SBD,scheduled_batch_master SBM,lookups L, key_values KV, ");
        strBuff.append(" users U,service_type ST, Users U1 ");
        strBuff.append(" WHERE SBM.batch_id=SBD.batch_id AND U.user_id=SBM.initiated_by AND ST.service_type= SBM.service_type");
        strBuff.append(" AND SBD.msisdn = ? AND SBM.owner_id = ? AND SBD.status = L.lookup_code");
        strBuff.append(" AND L.lookup_type = ? AND SBD.transfer_status = KV.key(+) AND KV.type (+) = ? AND U1.user_id= SBM.active_user_id ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND SBD.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND SBD.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND SBD.status <> ? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND SBD.status NOT IN (" + p_status + ")");
        }
        strBuff.append(" AND SBM.parent_id=? ");
        if (p_isStaffUser) {
            strBuff.append(" AND SBM.active_user_Id='" + p_userId + "' ");
        }
        strBuff.append(" ORDER BY SBM.scheduled_date DESC, SBD.batch_id DESC ");
        if (log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }*/
        try {
          /*  pstmtSelect = p_con.prepareStatement(strBuff.toString());*/
        	  /*     int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(i++, p_status);
            }*/
           
            
            ScheduleBatchDetailQry scheduleBatchDetailQry = (ScheduleBatchDetailQry) ObjectProducer.getObject(QueryConstants.SCH_BATCH_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = scheduleBatchDetailQry.loadScheduleDetailVOListQry(p_con, p_ownerID, p_parentID, p_msisdn, p_statusUsed, p_status, p_isStaffUser, p_userId);
     
            rs = pstmtSelect.executeQuery();
            ScheduleBatchDetailVO scheduleDetailVO = null;

            while (rs.next()) {
                scheduleDetailVO = new ScheduleBatchDetailVO();
                scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                scheduleDetailVO.setStatus(rs.getString("status"));
                scheduleDetailVO.setStatusDes(rs.getString("status_desc"));
                scheduleDetailVO.setTransactionStatus(rs.getString("transfer_status_desc"));
                scheduleDetailVO.setTransactionID(rs.getString("transfer_id"));
                scheduleDetailVO.setAmount(rs.getLong("amount"));
                scheduleDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()));
                scheduleDetailVO.setBatchID(rs.getString("batch_id"));
                scheduleDetailVO.setSubscriberID(rs.getString("subscriber_id"));
                scheduleDetailVO.setNetworkCode(rs.getString("network_code"));
                scheduleDetailVO.setSubscriberType(rs.getString("service_type"));
                scheduleDetailVO.setSubscriberTypeDescription(rs.getString("description"));
                scheduleDetailVO.setSubscriberID(rs.getString("subscriber_id"));
                scheduleDetailVO.setScheduleDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("scheduled_date"))));
                scheduleDetailVO.setCreatedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("created_on"))));
                scheduleDetailVO.setCreatedBy(rs.getString("created_by_name"));
                scheduleDetailVO.setSubService(rs.getString("sub_service"));
                scheduleDetailVO.setSubServiceDesc(PretupsBL.getSelectorDescriptionFromCode(rs.getString("stype") + "_" + scheduleDetailVO.getSubService()));
                scheduleDetailVO.setBatchType(rs.getString("batch_type"));
                scheduleDetailVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleDetailVO.setFrequency(rs.getString("frequency"));
                scheduleDetailVO.setIterations(rs.getInt("iteration"));
                scheduleDetailVO.setExecutedIterations(rs.getInt("executed_iterations"));
               // scheduleDetailVO.setProcessedOn(rs.getDate("processed_on"));
                if(rs.getDate("processed_on")!=null)
                scheduleDetailVO.setProcessedOnAsString(BTSLUtil.getDateStringFromDate(rs.getDate("processed_on")));
                else
                scheduleDetailVO.setProcessedOnAsString(" ");	
                scheduleDetailsVOList.add(scheduleDetailVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleDetailVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleDetailVOList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting scheduleDetailsVOList.size=" + scheduleDetailsVOList.size());
            }
        }
        return scheduleDetailsVOList;
    }
    
    
    
    /**
     * Method loadScheduleDetailVOList.
     * This method to load the scheduleDetails list on the basis of the ownerID
     * and the MSISDN and the status
     * 
     * @param p_con
     *            Connection
     * @param p_ownerID
     *            String
     * @param p_parentID
     *            TODO
     * @param p_msisdn
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @param fromDate
     * 			  Date
     * @param toDate
     * 	          Date
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     *         modify query select batch_type from scheduled_batch_master
     */
    public ArrayList loadScheduleDetailReportVOList(Connection p_con, String p_ownerID, String p_parentID, String p_msisdn, String p_statusUsed, String p_status, boolean p_isStaffUser, String p_userId, Date fromDate, Date toDate) throws BTSLBaseException {
        final String methodName = "loadScheduleDetailVOList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_msisdn = " + p_msisdn + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + ", p_ownerID=" + p_ownerID + " p_parentID=" + p_parentID + "p_msisdn" + p_msisdn + "p_statusUsed" + p_statusUsed + "p_status" + p_status + "p_isStaffUser" + p_isStaffUser + "p_userId" + p_userId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList scheduleDetailsVOList = new ArrayList();
      
        try {

            
            ScheduleBatchDetailQry scheduleBatchDetailQry = (ScheduleBatchDetailQry) ObjectProducer.getObject(QueryConstants.SCH_BATCH_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = scheduleBatchDetailQry.loadScheduleDetailReportVOListQry(p_con, p_ownerID, p_parentID, p_msisdn, p_statusUsed, p_status, p_isStaffUser, p_userId, fromDate, toDate);
     
            rs = pstmtSelect.executeQuery();
            ScheduleBatchDetailVO scheduleDetailVO = null;

            while (rs.next()) {
                scheduleDetailVO = new ScheduleBatchDetailVO();
                scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                scheduleDetailVO.setStatus(rs.getString("status"));
                scheduleDetailVO.setStatusDes(rs.getString("status_desc"));
                scheduleDetailVO.setTransactionStatus(rs.getString("transfer_status_desc"));
                scheduleDetailVO.setTransactionID(rs.getString("transfer_id"));
                scheduleDetailVO.setAmount(rs.getLong("amount"));
                scheduleDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()));
                scheduleDetailVO.setBatchID(rs.getString("batch_id"));
                scheduleDetailVO.setSubscriberID(rs.getString("subscriber_id"));
                scheduleDetailVO.setNetworkCode(rs.getString("network_code"));
                scheduleDetailVO.setSubscriberType(rs.getString("service_type"));
                scheduleDetailVO.setSubscriberTypeDescription(rs.getString("description"));
                scheduleDetailVO.setSubscriberID(rs.getString("subscriber_id"));
                scheduleDetailVO.setScheduleDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("scheduled_date"))));
                scheduleDetailVO.setCreatedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("created_on"))));
                scheduleDetailVO.setCreatedBy(rs.getString("created_by_name"));
                scheduleDetailVO.setSubService(rs.getString("sub_service"));
                scheduleDetailVO.setSubServiceDesc(PretupsBL.getSelectorDescriptionFromCode(rs.getString("stype") + "_" + scheduleDetailVO.getSubService()));
                scheduleDetailVO.setBatchType(rs.getString("batch_type"));
                scheduleDetailVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleDetailVO.setFrequency(rs.getString("frequency"));
                scheduleDetailVO.setIterations(rs.getInt("iteration"));
                scheduleDetailVO.setExecutedIterations(rs.getInt("executed_iterations"));
               // scheduleDetailVO.setProcessedOn(rs.getDate("processed_on"));
                if(rs.getDate("processed_on")!=null)
                scheduleDetailVO.setProcessedOnAsString(BTSLUtil.getDateStringFromDate(rs.getDate("processed_on")));
                else
                scheduleDetailVO.setProcessedOnAsString(" ");	
                scheduleDetailsVOList.add(scheduleDetailVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleDetailVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleDetailVOList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting scheduleDetailsVOList.size=" + scheduleDetailsVOList.size());
            }
        }
        return scheduleDetailsVOList;
    }

    /**
     * this method is used to cancel the schedule topup. This also update the
     * master table for number of
     * cancelled schedules
     * 
     * @param p_con
     *            Connection
     * @param p_schedulelist
     *            ArrayList
     * @param p_modifiedBy
     *            String
     * @param p_modifiedOn
     *            Date
     * @return int
     * 
     * @throws BTSLBaseException
     *             modified method signature add long p_cancelledCount ,long
     *             p_noOfRecords
     *             and if all mobile numbers of the batch are cancelled then
     *             batch status will be cancelled (add query for it)
     */
    public int updateSchedule(Connection p_con, ArrayList p_schedulelist, String p_modifiedBy, Date p_modifiedOn, long p_cancelledCount, long p_noOfRecords) throws BTSLBaseException {
        final String methodName = "updateSchedule";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: = p_schedulelist" + p_schedulelist + ", p_modifiedOn" + p_modifiedOn + ", p_modifiedBy=" + p_modifiedBy + " p_cancelledCount= " + p_cancelledCount + " p_noOfRecords= " + p_noOfRecords);
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtModifyOn = null;
        PreparedStatement pstmtcancelBatch = null;
        ResultSet rs = null;
        int updateCount = 0;
        try {
            // Query to update the details table
            StringBuffer strBuff = new StringBuffer("UPDATE scheduled_batch_detail SET status=?,modified_on=?, modified_by=? ");
            strBuff.append("WHERE msisdn=? AND batch_id=? ");
            // query to update the master table
            StringBuffer strBuffMaster = new StringBuffer("UPDATE scheduled_batch_master ");
            strBuffMaster.append("SET cancelled_count=cancelled_count+1, modified_on=?, modified_by=? ");
            strBuffMaster.append("WHERE batch_id=? ");
            // query to check modify on for record
            StringBuffer strModifyOn = new StringBuffer("SELECT modified_on FROM  scheduled_batch_detail ");
            strModifyOn.append("WHERE msisdn=? AND batch_id=? ");

            StringBuffer strCancelBatch = new StringBuffer("UPDATE scheduled_batch_master ");
            strCancelBatch.append("SET status=? WHERE batch_id=? ");

            String deleteQuery = strBuff.toString();
            String updateMasterQuery = strBuffMaster.toString();
            String modifyOnQuery = strModifyOn.toString();
            String cancelBatchQuery = strCancelBatch.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY deleteQuery:" + deleteQuery);
                log.debug("updateMaster", "QUERY updateMasterQuery:" + updateMasterQuery);
                log.debug("modifyOnQuery", "QUERY modifyOnQuery:" + modifyOnQuery);
                log.debug("strCancelBatch", "QUERY strCancelBatch:" + strCancelBatch);
            }
            // prepare the preparedStatemets for both the queries
            pstmtUpdate = p_con.prepareStatement(deleteQuery);
            pstmtUpdateMaster = p_con.prepareStatement(updateMasterQuery);
            pstmtModifyOn = p_con.prepareStatement(modifyOnQuery);

            int endIndex = p_schedulelist.size();
            for (int index = 0; index < endIndex; index++) {
                // set parameteres for modify on query
                pstmtModifyOn.setString(1, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getMsisdn());
                pstmtModifyOn.setString(2, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getBatchID());
                rs = pstmtModifyOn.executeQuery();
                pstmtModifyOn.clearParameters();
                // check if old and new modify on are same
                if (rs.next()) {
                    if (((ScheduleBatchDetailVO) p_schedulelist.get(index)).getModifiedOn().getTime() != rs.getTimestamp("modified_on").getTime()) {
                        throw new BTSLBaseException(this, methodName, "error.modify.true");
                    }
                }
                // set the parameters
                pstmtUpdate.setString(1, PretupsI.SCHEDULE_STATUS_CANCELED);
                pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                pstmtUpdate.setString(3, p_modifiedBy);
                pstmtUpdate.setString(4, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getMsisdn());
                pstmtUpdate.setString(5, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getBatchID());
                // update the details table
                updateCount = pstmtUpdate.executeUpdate();
                // clear the parametrs
                pstmtUpdate.clearParameters();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                // set the parametrs
                pstmtUpdateMaster.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                pstmtUpdateMaster.setString(2, p_modifiedBy);
                pstmtUpdateMaster.setString(3, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getBatchID());
                // update the master table
                updateCount = pstmtUpdateMaster.executeUpdate();
                // clears the parametrs
                pstmtUpdateMaster.clearParameters();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
            if (p_noOfRecords == (p_cancelledCount + p_schedulelist.size())) {
                pstmtcancelBatch = p_con.prepareStatement(cancelBatchQuery);
                pstmtcancelBatch.setString(1, PretupsI.SCHEDULE_STATUS_CANCELED);
                pstmtcancelBatch.setString(2, ((ScheduleBatchDetailVO) p_schedulelist.get(0)).getBatchID());
                // update master table
                updateCount = pstmtcancelBatch.executeUpdate();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }

            }
        } // end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } // end of catch
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateSchedule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateSchedule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateMaster != null) {
                    pstmtUpdateMaster.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtModifyOn != null) {
                    pstmtModifyOn.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtcancelBatch != null) {
                    pstmtcancelBatch.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }
    
    
    public int updateScheduleRest(Connection p_con, ArrayList p_schedulelist, String p_modifiedBy, Date p_modifiedOn, long p_cancelledCount, long p_noOfRecords,ArrayList<String> msisdn) throws BTSLBaseException {
        final String methodName = "updateSchedule";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: = p_schedulelist" + p_schedulelist + ", p_modifiedOn" + p_modifiedOn + ", p_modifiedBy=" + p_modifiedBy + " p_cancelledCount= " + p_cancelledCount + " p_noOfRecords= " + p_noOfRecords);
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtModifyOn = null;
        PreparedStatement pstmtcancelBatch = null;
        ResultSet rs = null;
        int updateCount = 0;
        try {
            StringBuilder strBuff = new StringBuilder("UPDATE scheduled_batch_detail SET status=?,modified_on=?, modified_by=? ");
            strBuff.append("WHERE msisdn=? AND batch_id=? ");
            StringBuilder strBuffMaster = new StringBuilder("UPDATE scheduled_batch_master ");
            strBuffMaster.append("SET cancelled_count=cancelled_count+1, modified_on=?, modified_by=? ");
            strBuffMaster.append("WHERE batch_id=? ");
            StringBuffer strModifyOn = new StringBuffer("SELECT modified_on FROM  scheduled_batch_detail ");
            strModifyOn.append("WHERE msisdn=? AND batch_id=? ");

            StringBuilder strCancelBatch = new StringBuilder("UPDATE scheduled_batch_master ");
            strCancelBatch.append("SET status=? WHERE batch_id=? ");

            String deleteQuery = strBuff.toString();
            String updateMasterQuery = strBuffMaster.toString();
            String modifyOnQuery = strModifyOn.toString();
            String cancelBatchQuery = strCancelBatch.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY deleteQuery:" + deleteQuery);
                log.debug("updateMaster", "QUERY updateMasterQuery:" + updateMasterQuery);
                log.debug("modifyOnQuery", "QUERY modifyOnQuery:" + modifyOnQuery);
                log.debug("strCancelBatch", "QUERY strCancelBatch:" + strCancelBatch);
            }
            // prepare the preparedStatemets for both the queries
            pstmtUpdate = p_con.prepareStatement(deleteQuery);
            pstmtUpdateMaster = p_con.prepareStatement(updateMasterQuery);
            pstmtModifyOn = p_con.prepareStatement(modifyOnQuery);

            int endIndex = p_schedulelist.size();
            for (int index = 0; index < endIndex; index++) {
                pstmtModifyOn.setString(1, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getMsisdn());
                pstmtModifyOn.setString(2, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getBatchID());
                rs = pstmtModifyOn.executeQuery();
                pstmtModifyOn.clearParameters();
                if (rs.next()) {
                    if (((ScheduleBatchDetailVO) p_schedulelist.get(index)).getModifiedOn().getTime() != rs.getTimestamp("modified_on").getTime()) {
                        throw new BTSLBaseException(this, methodName, "error.modify.true");
                    }
                }
                // set the parameters
                pstmtUpdate.setString(1, PretupsI.SCHEDULE_STATUS_CANCELED);
                pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                pstmtUpdate.setString(3, p_modifiedBy);
                pstmtUpdate.setString(4, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getMsisdn());
                pstmtUpdate.setString(5, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getBatchID());
                // update the details table
                updateCount = pstmtUpdate.executeUpdate();
                // clear the parametrs
                pstmtUpdate.clearParameters();
                if (updateCount <= 0) {
                	msisdn.add(((ScheduleBatchDetailVO) p_schedulelist.get(index)).getMsisdn());
                	throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                
                // set the parametrs
                pstmtUpdateMaster.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                pstmtUpdateMaster.setString(2, p_modifiedBy);
                pstmtUpdateMaster.setString(3, ((ScheduleBatchDetailVO) p_schedulelist.get(index)).getBatchID());
                // update the master table
                updateCount = pstmtUpdateMaster.executeUpdate();
                // clears the parametrs
                pstmtUpdateMaster.clearParameters();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
            if (p_noOfRecords == (p_cancelledCount + p_schedulelist.size())) {
                pstmtcancelBatch = p_con.prepareStatement(cancelBatchQuery);
                pstmtcancelBatch.setString(1, PretupsI.SCHEDULE_STATUS_CANCELED);
                pstmtcancelBatch.setString(2, ((ScheduleBatchDetailVO) p_schedulelist.get(0)).getBatchID());
                // update master table
                updateCount = pstmtcancelBatch.executeUpdate();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }

            }
        } // end of try
        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } // end of catch
        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateSchedule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[updateSchedule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	try{
        		if (pstmtUpdate!= null){
        			pstmtUpdate.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdateMaster!= null){
        			pstmtUpdateMaster.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtModifyOn!= null){
        			pstmtModifyOn.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtcancelBatch!= null){
        			pstmtcancelBatch.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }
    
    public ScheduleBatchMasterVO loadScheduleBatchMasterDetails(Connection p_con, String p_parent_id, String p_statusUsed, String p_status, Date p_scheduleDate, Date p_fromScheduleDate, Date p_toScheduleDate, String p_serviceType, boolean p_isStaffUser, String p_activeUserId,String batchId) throws BTSLBaseException {
        String methodName = "loadScheduleBatchMasterDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_parent_id = " + p_parent_id + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + ",p_scheduleDate=" + p_scheduleDate + ",p_fromScheduleDate= " + p_fromScheduleDate + ", p_toScheduleDate=" + p_toScheduleDate + " ,p_serviceType=" + p_serviceType + " p_isStaffUser= " + p_isStaffUser + " p_activeUserId= " + p_activeUserId + "batchId= " + batchId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ScheduleBatchMasterVO scheduleMasterVO = null;
        try {
            StringBuffer selectSQL = new StringBuffer(" SELECT L.lookup_name status_desc, ");
            selectSQL.append(" SBM.batch_id, SBM.status, SBM.network_code, SBM.total_count, ");
            selectSQL.append(" SBM.successful_count, SBM.upload_failed_count, SBM.process_failed_count, ");
            selectSQL.append(" SBM.cancelled_count, SBM.scheduled_date, SBM.parent_id, SBM.owner_id, ");
            selectSQL.append(" SBM.parent_category, SBM.parent_domain, SBM.service_type,ST.name,SBM.initiated_by,SBM.FREQUENCY,SBM.ITERATION,SBM.EXECUTED_ITERATIONS,SBM.PROCESSED_ON, ");
            selectSQL.append(" U1.user_name initiated_by_name,SBM.modified_on,SBM.ref_batch_id,SBM.batch_type,SBM.active_user_id, U2.user_name active_user_name ");
            selectSQL.append(" FROM scheduled_batch_master SBM, users U1, lookups L,service_type ST, Users U2 ");
            selectSQL.append(" WHERE SBM.parent_id= ? AND SBM.batch_id= ?");
            selectSQL.append(" AND SBM.service_type=ST.service_type ");

            if (!BTSLUtil.isNullString(p_serviceType)) {
                selectSQL.append(" AND ST.service_type = ? ");
            }
            selectSQL.append(" AND SBM.initiated_by = U1.user_id ");
            selectSQL.append(" AND SBM.status = L.lookup_code ");
            selectSQL.append(" AND L.lookup_type = ? ");
            String []args = p_status.split(",");
            if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
                selectSQL.append("AND SBM.status IN");
                BTSLUtil.pstmtForInQuery(args, selectSQL);
            } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                selectSQL.append("AND SBM.status =? ");
            } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                selectSQL.append("AND SBM.status <> ? ");
            } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
                selectSQL.append("AND SBM.status NOT IN");
                BTSLUtil.pstmtForInQuery(args, selectSQL);
            }
            selectSQL.append(" AND SBM.active_user_id=U2.user_id ");
            if (p_isStaffUser) {
                selectSQL.append("AND SBM.active_user_id=?");
            }
            selectSQL.append(" ORDER BY SBM.created_on DESC ");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY SelectQuery:" + selectSQL);
            }

            pstmtSelect = p_con.prepareStatement(selectSQL.toString());
            int i = 0;
            pstmtSelect.setString(++i, p_parent_id);
            pstmtSelect.setString(++i, batchId);
            if (!BTSLUtil.isNullString(p_serviceType)) {
            	pstmtSelect.setString(++i,p_serviceType );
            }
            pstmtSelect.setString(++i, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(++i, p_status);
            }
            else if( p_statusUsed.equals(PretupsI.STATUS_IN) || p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            	for(int j=0;j<args.length;j++)
            	{
            		String param = args[j];
            		param = param.replace("'", "");
            		 pstmtSelect.setString(++i, param);
            	}
            }
            if (p_isStaffUser) {
            	pstmtSelect.setString(++i, p_activeUserId);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                scheduleMasterVO = new ScheduleBatchMasterVO();
                scheduleMasterVO.setStatusDesc(rs.getString("status_desc"));
                scheduleMasterVO.setBatchID(rs.getString("batch_id"));
                scheduleMasterVO.setStatus(rs.getString("status"));
                scheduleMasterVO.setNetworkCode(rs.getString("network_code"));
                scheduleMasterVO.setTotalCount(rs.getLong("total_count"));
                scheduleMasterVO.setSuccessfulCount(rs.getLong("successful_count"));
                scheduleMasterVO.setUploadFailedCount(rs.getLong("upload_failed_count"));
                scheduleMasterVO.setProcessFailedCount(rs.getLong("process_failed_count"));
                scheduleMasterVO.setCancelledCount(rs.getLong("cancelled_count"));
                scheduleMasterVO.setScheduledDate(rs.getDate("scheduled_date"));
                scheduleMasterVO.setScheduledDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(scheduleMasterVO.getScheduledDate())));
                scheduleMasterVO.setParentID(rs.getString("parent_id"));
                scheduleMasterVO.setOwnerID(rs.getString("owner_id"));
                scheduleMasterVO.setParentCategory(rs.getString("parent_category"));
                scheduleMasterVO.setParentDomain(rs.getString("parent_domain"));
                scheduleMasterVO.setServiceType(rs.getString("service_type"));
                scheduleMasterVO.setServiceName(rs.getString("name"));
                scheduleMasterVO.setInitiatedBy(rs.getString("initiated_by"));
                scheduleMasterVO.setInitiatedByName(rs.getString("initiated_by_name"));
                scheduleMasterVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                scheduleMasterVO.setRefBatchID(rs.getString("ref_batch_id"));
                scheduleMasterVO.setNoOfRecords(scheduleMasterVO.getTotalCount() - scheduleMasterVO.getUploadFailedCount());
                scheduleMasterVO.setBatchIDDisp(scheduleMasterVO.getBatchID() + "(" + scheduleMasterVO.getScheduledDateStr() + ")");
                scheduleMasterVO.setBatchType(rs.getString("batch_type"));
                scheduleMasterVO.setActiveUserId(rs.getString("active_user_id"));
                scheduleMasterVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleMasterVO.setFrequency(rs.getString("frequency"));
                scheduleMasterVO.setIterations(rs.getInt("iteration"));
                scheduleMasterVO.setExecutedIterations(rs.getInt("executed_iterations"));
                scheduleMasterVO.setProcessedOn(rs.getDate("processed_on"));
                if(scheduleMasterVO.getProcessedOn()!=null)
                	scheduleMasterVO.setProcessedOnStr(BTSLUtil.getDateStringFromDate(scheduleMasterVO.getProcessedOn()));
                else
                	scheduleMasterVO.setProcessedOnStr("");
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleBatchMasterList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchDetailDAO[loadScheduleBatchMasterList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: scheduleMasterVO=" + scheduleMasterVO);
            }
        }
        return scheduleMasterVO;
    }

}
