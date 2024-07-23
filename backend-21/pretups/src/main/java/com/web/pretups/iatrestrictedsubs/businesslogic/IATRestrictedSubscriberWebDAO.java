package com.web.pretups.iatrestrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.logging.IATAssociateMSISDNFileProcessingLog;
import com.btsl.pretups.logging.RestrictedMsisdnLog;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;

public class IATRestrictedSubscriberWebDAO {

    private static Log log = LogFactory.getLog(IATRestrictedSubscriberWebDAO.class.getName());
    private static IATRestrictedSubscriberWebQry iatRestrictedSubscriberWebQry;
    
    public IATRestrictedSubscriberWebDAO() {
    	iatRestrictedSubscriberWebQry = (IATRestrictedSubscriberWebQry) ObjectProducer.getObject(QueryConstants.IAT_RESTRICTED_SUBSCRIBER_WEB_QRY,QueryConstants.QUERY_PRODUCER);
    }

    /**
     * This method is used for inserting valid subscriber in RESTRICTED_MSSIDN
     * table, who are eligible for Rchrg.
     * 
     * @author babu.kunwar
     * @param p_con
     * @param p_msisdnList
     * @param p_ownerID
     * @param p_loggedInUserID
     * @param p_fileName
     * @param p_canNotRegMsg
     * @param p_alredyExistMsg
     * @param p_countryCode
     * @throws BTSLBaseException
     */
    public void registeringIATBulkSubscriber(Connection p_con, ArrayList<RestrictedSubscriberVO> p_msisdnList, String p_ownerID, String p_loggedInUserID, String p_fileName, String p_canNotRegMsg, String p_alredyExistMsg) throws BTSLBaseException {

        final String methodName = "registeringIATBulkSubscriber";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_msisdnList size : ");
        	msg.append(p_msisdnList.size());
        	msg.append(", p_ownerID : ");
        	msg.append(p_ownerID);
        	msg.append(", p_loggedInUserID : ");
        	msg.append(p_loggedInUserID);
        	msg.append(", p_fileName : ");
        	msg.append(p_fileName);
        	msg.append(", p_canNotRegMsg : ");
        	msg.append(p_canNotRegMsg);
        	msg.append(", p_alredyExistMsg : ");
        	msg.append(p_alredyExistMsg);
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }

        PreparedStatement isMsisdnExist = null;
        ResultSet rsIsMsisdnExist = null;
        PreparedStatement pstmtInsert = null;
        int updateCount = 0;
        String msisdn = null;
        long failCount = 0;
        RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
        try {
            // check whether subscriber msisdn exists under the owner user
            final String isMsisdnExistQuery = "SELECT 1 FROM RESTRICTED_MSISDNS WHERE MSISDN=? AND RESTRICTED_TYPE=?";

            if (log.isDebugEnabled()) {
                log.debug(methodName, "isMsisdnExistQuery = " + isMsisdnExistQuery);
            }
            isMsisdnExist = p_con.prepareStatement(isMsisdnExistQuery.toString());
            // Insert the subscriber under the owner user
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO RESTRICTED_MSISDNS (msisdn, subscriber_id, owner_id, network_code,");
            strBuff.append("min_txn_amount,max_txn_amount,status, created_on, created_by, subscriber_type,");
            strBuff.append("language, country,modified_on, modified_by,restricted_type,country_code)");
            strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            final String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "insertQuery = " + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);

            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                iatRestrictedSubscriberVO = (RestrictedSubscriberVO) p_msisdnList.get(i);
                msisdn = iatRestrictedSubscriberVO.getMsisdn();

                if (iatRestrictedSubscriberVO.getErrorCode() == null) {
                    isMsisdnExist.setString(1, msisdn);
                    isMsisdnExist.setString(2, PretupsI.RESTRICTED_TYPE);
                    rsIsMsisdnExist = isMsisdnExist.executeQuery();
                    isMsisdnExist.clearParameters();
                    if (rsIsMsisdnExist.next()) // if condition true then mark
                    // error and continue to next
                    // msisdn
                    {
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, " Mobile number already exists under the owner user=" + msisdn);
                        }
                        RestrictedMsisdnLog.log(p_fileName, msisdn, "Mobile number already exists under the owner user", "Fail", "Logged In UserID : " + p_loggedInUserID);

                        iatRestrictedSubscriberVO.setLineNumber(String.valueOf(i + 1));
                        iatRestrictedSubscriberVO.setMsisdn(msisdn);
                        iatRestrictedSubscriberVO.setErrorCode(p_alredyExistMsg);
                        failCount = failCount + 1;
                        iatRestrictedSubscriberVO.setFailCount(failCount);
                        if (rsIsMsisdnExist != null) {
                            rsIsMsisdnExist.close();
                        }
                        continue;
                    }
                    if (rsIsMsisdnExist != null) {
                        rsIsMsisdnExist.close();
                    }
                    pstmtInsert.setString(1, msisdn);
                    pstmtInsert.setString(2, iatRestrictedSubscriberVO.getSubscriberID());
                    pstmtInsert.setString(3, iatRestrictedSubscriberVO.getOwnerID());
                    pstmtInsert.setString(4, iatRestrictedSubscriberVO.getNetworkCode());
                    pstmtInsert.setLong(5, iatRestrictedSubscriberVO.getMinTxnAmount());
                    pstmtInsert.setLong(6, iatRestrictedSubscriberVO.getMaxTxnAmount());
                    pstmtInsert.setString(7, iatRestrictedSubscriberVO.getStatus());
                    pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(iatRestrictedSubscriberVO.getCreatedOn()));
                    pstmtInsert.setString(9, iatRestrictedSubscriberVO.getCreatedBy());
                    pstmtInsert.setString(10, iatRestrictedSubscriberVO.getSubscriberType());
                    pstmtInsert.setString(11, iatRestrictedSubscriberVO.getLanguage());
                    pstmtInsert.setString(12, iatRestrictedSubscriberVO.getCountry());
                    pstmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(iatRestrictedSubscriberVO.getModifiedOn()));
                    pstmtInsert.setString(14, iatRestrictedSubscriberVO.getModifiedBy());
                    pstmtInsert.setString(15, PretupsI.RESTRICTED_TYPE);
                    pstmtInsert.setInt(16, iatRestrictedSubscriberVO.getCountryCode());
                    updateCount = pstmtInsert.executeUpdate();
                    pstmtInsert.clearParameters();

                    if (updateCount <= 0) {
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, " Cannot Insert the subscriber for msisdn=" + msisdn);
                        }
                        RestrictedMsisdnLog.log(p_fileName, msisdn, "Cannot upload the MSISDN", "Fail", "Logged In UserID : " + p_loggedInUserID);
                        iatRestrictedSubscriberVO.setLineNumber(String.valueOf(i + 1));
                        iatRestrictedSubscriberVO.setMsisdn(msisdn);
                        iatRestrictedSubscriberVO.setErrorCode(p_canNotRegMsg);
                        failCount = failCount + 1;
                        iatRestrictedSubscriberVO.setFailCount(failCount);
                        p_con.rollback();
                        continue;
                    }
                    p_con.commit();
                    RestrictedMsisdnLog.log(p_fileName, msisdn, "MSISDN uploaded successfully", "Success", "Logged In UserID : " + p_loggedInUserID);
                    if (log.isDebugEnabled()) {
                        log.debug(methodName, " MSISDN uploaded successfully : " + msisdn);
                    }
                }
            }
            // the last value of failCount is to be set in the last
            // iatRestrictedSubscriberVO
            // and get this failCount in the Action class for getting the total
            // fail counts(form DAO+Action)
            iatRestrictedSubscriberVO.setFailCount(failCount);
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[registeringIATBulkSubscriber]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[registeringIATBulkSubscriber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsIsMsisdnExist != null) {
                    rsIsMsisdnExist.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (isMsisdnExist != null) {
                    isMsisdnExist.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting : p_msisdnList.size()=" + p_msisdnList.size());
            }
        }

    }

    /**
     * Method Name loadIATSubsDetailForApproval
     * This method is used to Load the details from the iat_restricted_msisdns
     * for approval
     * 
     * @author babu.kunwar
     * @param Connection
     *            p_con
     * @param String
     *            p_ownerID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadIATSubsDetailForApproval(Connection p_con, String p_ownerID) throws BTSLBaseException {

        final String methodName = "loadIATSubsDetailForApproval";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_ownerID =" + p_ownerID);
        }

        final ArrayList<RestrictedSubscriberVO> iatSubsListForApp = new ArrayList<RestrictedSubscriberVO>();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT rm.msisdn,rm.subscriber_id,rm.channel_user_id,rm.channel_user_category,rm.employee_code,rm.owner_id,");
        strBuff.append("rm.employee_name,rm.network_code,rm.min_txn_amount,rm.max_txn_amount,rm.monthly_limit,rm.association_date,");
        strBuff.append("rm.total_txn_count,rm.total_txn_amount,rm.remark,rm.approved_by,rm.language,");
        strBuff.append("rm.approved_on,rm.associated_by,rm.status, l.lookup_name status_desc, rm.created_on,rm.created_by,rm.modified_on,rm.modified_by,");
        strBuff.append("rm.subscriber_type,rm.country,rm.restricted_type,rm.country_code");
        strBuff.append(" FROM restricted_msisdns rm, lookups l");
        strBuff.append(" WHERE rm.owner_id=? AND rm.status= ?  AND rm.restricted_type=?");
        strBuff.append(" AND l.lookup_code = rm.status ");
        strBuff.append(" AND l.lookup_type =?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, PretupsI.RES_MSISDN_STATUS_NEW);
            pstmtSelect.setString(i++, PretupsI.RESTRICTED_TYPE);
            pstmtSelect.setString(i++, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                iatRestrictedSubscriberVO = new RestrictedSubscriberVO();
                iatRestrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                iatRestrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                iatRestrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                iatRestrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                iatRestrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                iatRestrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                iatRestrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                iatRestrictedSubscriberVO.setNetworkCode(rs.getString("network_code"));
                iatRestrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                iatRestrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                iatRestrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                iatRestrictedSubscriberVO.setStatusDes(rs.getString("status_desc"));
                iatRestrictedSubscriberVO.setStatus(rs.getString("status"));
                iatRestrictedSubscriberVO.setTempStatus(rs.getString("status"));
                iatRestrictedSubscriberVO.setCreatedOn(rs.getTimestamp("created_on"));
                iatRestrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateStringFromDate(iatRestrictedSubscriberVO.getCreatedOn()));
                iatRestrictedSubscriberVO.setCreatedBy(rs.getString("created_by"));
                iatRestrictedSubscriberVO.setModifiedOn(rs.getTimestamp("modified_on"));
                iatRestrictedSubscriberVO.setModifiedBy(rs.getString("modified_by"));
                iatRestrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                iatRestrictedSubscriberVO.setLanguage(rs.getString("language"));
                iatRestrictedSubscriberVO.setCountry(rs.getString("country"));
                iatRestrictedSubscriberVO.setRestrictedType(rs.getString("restricted_type"));
                iatRestrictedSubscriberVO.setCountryCode(rs.getInt("country_code"));
                iatSubsListForApp.add(iatRestrictedSubscriberVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[loadIATSubsDetailForApproval]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[loadIATSubsDetailForApproval]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            log.debug(methodName, "p_subsDetailList.size()=:" + iatSubsListForApp.size());
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
        }

        return iatSubsListForApp;
    }

    /**
     * Method Name updateIATRestrictedSubsStatus
     * This method is used to update the Status as 'A-Approved' whose status is
     * 'W-NEW'.
     * This is also take care about the modification of records by some one else
     * while updating the records.
     * All records selected for approval are updated with status 'A-Approved'
     * and Recjected Records are deleted.
     * The records that are selected for discard has no action.
     * 
     * @author babu.kunwar
     * @param Connection
     *            p_con
     * @param ArrayList
     *            p_confirmSubsAppList
     * @return int
     * @throws BTSLBaseException
     */

    public int updateIATRestrictedSubsStatus(Connection p_con, ArrayList p_confirmSubsAppList) throws BTSLBaseException {
        final String methodName = "updateIATRestrictedSubsStatus";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_confirmSubsAppList.size()=" + p_confirmSubsAppList.size());
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtDelete = null;
        RestrictedSubscriberVO restrictedSubsVO = null;
        final ArrayList<RestrictedSubscriberVO> approveList = new ArrayList<RestrictedSubscriberVO>();
        final ArrayList<RestrictedSubscriberVO> rejectedList = new ArrayList<RestrictedSubscriberVO>();
        int updateCount = 0;
        int deleteCount = 0;
        final PreparedStatement pstmtSelect = null;
        final ResultSet rs = null;
        try {
            /*
             * Iterating the confirmSubsAppList that contains all the records
             * with status 'A','R' and 'D'
             * Records with status 'A' has to be updated in restricted_msisdns
             * Records with status 'R' has to be deleted from the
             * restricted_msisdns,and Triger is responsible to move these
             * records in History table.
             * Records with status 'D' has no action
             * Stored all the records whose status is 'A' into approveList and
             * 'R' to rejectList.
             */
            for (int index = 0, size = p_confirmSubsAppList.size(); index < size; index++) {
                restrictedSubsVO = (RestrictedSubscriberVO) p_confirmSubsAppList.get(index);
                if (restrictedSubsVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)) {
                    approveList.add(restrictedSubsVO);
                } else if (restrictedSubsVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_REJECT)) {
                    rejectedList.add(restrictedSubsVO);
                }
            }
            final StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE restricted_msisdns SET status=?, approved_by=?,approved_on=?,modified_on=?,modified_by=?");
            updateQuery.append(" WHERE owner_id=? AND msisdn=? AND status=? AND restricted_type=?");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY updateQuery:" + updateQuery);
            }

            final String sqlDelete = "DELETE FROM restricted_msisdns WHERE owner_id= ? AND msisdn=? AND restricted_type=?";

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlDelete=" + sqlDelete);
            }

            pstmtDelete = p_con.prepareStatement(sqlDelete);
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            // While updating,check the STATUS AS 'W-NEW' and then update.if the
            // status is changed after loading the list and before
            // updation,throw exception.
            int approveListSize = approveList.size();
            for (int index = 0, j = approveListSize; index < j; index++) {
                int tempCount = 0;
                restrictedSubsVO = (RestrictedSubscriberVO) approveList.get(index);
                restrictedSubsVO.setApprovedOn(new Date());
                restrictedSubsVO.setModifiedOn(new Date());
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "restrictedSubsVO:" + restrictedSubsVO);
                }
                int i = 1;
                pstmtUpdate.setString(i++, restrictedSubsVO.getStatus());
                pstmtUpdate.setString(i++, restrictedSubsVO.getApprovedBy());
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(restrictedSubsVO.getApprovedOn()));
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(restrictedSubsVO.getModifiedOn()));
                pstmtUpdate.setString(i++, restrictedSubsVO.getModifiedBy());
                pstmtUpdate.setString(i++, restrictedSubsVO.getOwnerID());
                pstmtUpdate.setString(i++, restrictedSubsVO.getMsisdn());
                pstmtUpdate.setString(i++, PretupsI.RES_MSISDN_STATUS_NEW);
                pstmtUpdate.setString(i++, PretupsI.RESTRICTED_TYPE);
                tempCount = pstmtUpdate.executeUpdate();
                if (tempCount == 0) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                updateCount += tempCount;
                pstmtUpdate.clearParameters();
            }
            // Delete all the records that are selected as rejected.Trigger is
            // responsible to move all deleted records into restricted
            // RESTRICTED_MSISDNS_HISTORY table.
            int rejectedListSize = rejectedList.size();
            for (int delete = 0; delete < rejectedListSize; delete++) {
                int dcount = 0;
                restrictedSubsVO = (RestrictedSubscriberVO) rejectedList.get(delete);
                pstmtDelete.setString(1, restrictedSubsVO.getOwnerID());
                pstmtDelete.setString(2, restrictedSubsVO.getMsisdn());
                pstmtDelete.setString(3, PretupsI.RESTRICTED_TYPE);
                dcount = pstmtDelete.executeUpdate();
                if (dcount == 0) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                pstmtDelete.clearParameters();
                deleteCount += dcount;
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[updateIATRestrictedSubsStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[updateIATRestrictedSubsStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
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
            try {
                if (pstmtDelete != null) {
                	pstmtDelete.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting :updateCount+deleteCount : ");
            	msg.append(updateCount + deleteCount);
            	msg.append(", updateCount : ");
            	msg.append(updateCount);
            	msg.append(", deletecount : ");
            	msg.append(deleteCount);
            	
            	String message = msg.toString();
                log.debug(methodName, message);
            }
        }
        return updateCount + deleteCount;
    }

    /**
     * This method is used to load msisdn list that has status 'A'
     * Method :loadApprovedMsisdnList
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_subscriberType
     *            String
     * @return list String
     * @throws BTSLBaseException
     * @author babu.kunwar
     */
    public ArrayList<RestrictedSubscriberVO> loadApprovedMsisdnList(Connection p_con, String p_subscriberType, String p_ownerID) throws BTSLBaseException {
        final String methodName = "loadApprovedMsisdnList";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_subscriberType : ");
        	msg.append(p_subscriberType);
        	msg.append(", p_ownerID : ");
        	msg.append(p_ownerID);
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet associateListSet = null;
        final String msisdnStr = null;
        final ArrayList<RestrictedSubscriberVO> associateList = new ArrayList<RestrictedSubscriberVO>();
        RestrictedSubscriberVO iatRestrictedSubsVO = null;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" SELECT MSISDN,SUBSCRIBER_ID,RESTRICTED_TYPE,COUNTRY_CODE");
            strBuff.append(" FROM restricted_msisdns WHERE status = ?");
            strBuff.append("AND subscriber_type=? AND owner_id=? AND restricted_type=?");
            final String sqlSelect = strBuff.toString();
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.RES_MSISDN_STATUS_APPROVED);
            pstmtSelect.setString(2, p_subscriberType);
            pstmtSelect.setString(3, p_ownerID);
            pstmtSelect.setString(4, PretupsI.RESTRICTED_TYPE);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            associateListSet = pstmtSelect.executeQuery();
            while (associateListSet.next()) {
                iatRestrictedSubsVO = new RestrictedSubscriberVO();
                iatRestrictedSubsVO.setMsisdn(associateListSet.getString("MSISDN"));
                iatRestrictedSubsVO.setSubscriberID(associateListSet.getString("SUBSCRIBER_ID"));
                iatRestrictedSubsVO.setRestrictedType((associateListSet.getString("RESTRICTED_TYPE")));
                iatRestrictedSubsVO.setCountryCode(associateListSet.getInt("COUNTRY_CODE"));
                associateList.add(iatRestrictedSubsVO);
            }

        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[loadApprovedMsisdnList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[loadApprovedMsisdnList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (associateListSet != null) {
                    associateListSet.close();
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
                log.debug(methodName, "Exiting: msisdnStr =" + msisdnStr);
            }
        }
        return associateList;
    }

    /**
     * This method is used to associate restricted msisdn
     * 
     * @author babu.kunwar
     *         Method :associateIATRestrictedMsisdn
     * @param p_con
     *            java.sql.Connection
     * @param p_restrictedList
     *            ArrayList
     * @return String
     * @throws BTSLBaseException
     */
    public String associateIATRestrictedMsisdn(Connection p_con, ArrayList p_restrictedList) throws BTSLBaseException {

        final String methodName = "associateIATRestrictedMsisdn";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: ArrayList" + p_restrictedList);
        }
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = 0;
        String unprocessedMsisdn = null;
        final StringBuffer invalidDataStrBuff = new StringBuffer();
        RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" UPDATE restricted_msisdns");
        strBuff.append(" SET channel_user_id=? , channel_user_category=? ,");
        strBuff.append(" employee_code=? , employee_name=? , network_code=? , monthly_limit=? ,");
        strBuff.append(" min_txn_amount=? , max_txn_amount=?  , associated_by=? , status=? , association_date=? ,");
        strBuff.append(" modified_on=? , modified_by=? , language=? , country=?");
        strBuff.append(" WHERE msisdn=? AND owner_id=? AND status=? AND restricted_type=?");

        final String sqlUpdate = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            final int indexsize = p_restrictedList.size();
            int i = 0;
            for (int index = 0; index < indexsize; index++) {
                iatRestrictedSubscriberVO = (RestrictedSubscriberVO) p_restrictedList.get(index);
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getChannelUserID());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getChannelUserCategory());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getEmployeeCode());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getEmployeeName());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getNetworkCode());
                pstmtUpdate.setLong(++i, iatRestrictedSubscriberVO.getMonthlyLimit());
                pstmtUpdate.setLong(++i, iatRestrictedSubscriberVO.getMinTxnAmount());
                pstmtUpdate.setLong(++i, iatRestrictedSubscriberVO.getMaxTxnAmount());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getAssociatedBy());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getStatus());
                pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(iatRestrictedSubscriberVO.getAssociationDate()));
                pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(iatRestrictedSubscriberVO.getModifiedOn()));
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getModifiedBy());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getLanguage());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getCountry());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getMsisdn());
                pstmtUpdate.setString(++i, iatRestrictedSubscriberVO.getOwnerID());
                pstmtUpdate.setString(++i, PretupsI.RES_MSISDN_STATUS_APPROVED);
                pstmtUpdate.setString(++i, PretupsI.RESTRICTED_TYPE);
                i = 0;
                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    invalidDataStrBuff.append(iatRestrictedSubscriberVO.getMsisdn() + ",");
                } else {
                    IATAssociateMSISDNFileProcessingLog
                        .log("Associate Restricted Msisdn", iatRestrictedSubscriberVO.getMsisdn(), "MSISDN associated successfully", "PASS", "");
                }
                pstmtUpdate.clearParameters();
            }
            if (invalidDataStrBuff.length() > 0) {
                unprocessedMsisdn = invalidDataStrBuff.substring(0, invalidDataStrBuff.length() - 1);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[associateIATRestrictedMsisdn]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[associateIATRestrictedMsisdn]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: unprocessedMsisdn=" + unprocessedMsisdn);
            }
        }
        return unprocessedMsisdn;
    }

    /**
     * Method isSubscriberExistByChannelID.
     * This method is to check that the subscriber is exist of the passed status
     * under
     * the passed channel_user_id, owner_id, and msisdn.
     * 
     * @param p_con
     *            Connection
     * @param p_channel_id
     *            String
     * @param p_owner_id
     *            String
     * @param p_subscriberList
     *            ArrayList
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @param p_subscriberType
     *            String
     * @return String
     * @throws BTSLBaseException
     * @author babu.kunwar
     */
    public String isSubscriberExistByChannelID(Connection p_con, String p_channel_id, String p_owner_id, ArrayList p_subscriberList, String p_statusUsed, String p_status, String p_subscriberType) throws BTSLBaseException {
        final String methodName = "isSubscriberExistByChannelID";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_channel_id : ");
        	msg.append(p_channel_id);
        	msg.append(", p_owner_id : ");
        	msg.append(p_owner_id);        	
        	msg.append(", p_subscriberList size : ");
        	msg.append(p_subscriberList.size());
        	msg.append(", p_status : ");
        	msg.append(p_status);
        	msg.append(", p_statusUsed : ");
        	msg.append(p_statusUsed);
        	msg.append(", p_subscriberType : ");
        	msg.append(p_subscriberType);
        	String message = msg.toString();
        	
            log.debug(methodName, message);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer returnDataStrBuff = new StringBuffer();
        String returnStr = null;
        final StringBuffer strBuff = new StringBuffer("SELECT 1 FROM restricted_msisdns WHERE channel_user_id =? AND owner_id = ? AND msisdn = ? ");

        if (p_subscriberType != null) {
            strBuff.append("AND subscriber_type=? ");
        }
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND status <> ? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND status NOT IN (" + p_status + ")");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
            for (int index = 0, j = p_subscriberList.size(); index < j; index++) {
                iatRestrictedSubscriberVO = (RestrictedSubscriberVO) p_subscriberList.get(index);
                int i = 1;
                pstmtSelect.setString(i++, p_channel_id);
                pstmtSelect.setString(i++, p_owner_id);
                pstmtSelect.setString(i++, iatRestrictedSubscriberVO.getMsisdn());
                if (p_subscriberType != null) {
                    pstmtSelect.setString(i++, p_subscriberType);
                }
                if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                    pstmtSelect.setString(i++, p_status);
                }
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    returnDataStrBuff.append(iatRestrictedSubscriberVO.getMsisdn() + ",");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[isSubscriberExistByChannelID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[isSubscriberExistByChannelID]", "", "", "", "Exception:" + ex.getMessage());
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
     * This method is used to load the subscribers details on the basis of
     * MSISDN and channel_user_id,
     * Method :loadSubcriberListToDeassociate
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_scheduledList
     *            ArrayList
     * @param p_channelUserID
     *            String
     * @param p_invalidMsisdn
     *            StringBuffer
     * @return list ArrayList
     * @throws BTSLBaseException
     * @author babu.kunwar
     */
    public ArrayList loadSubcriberListToDeassociate(Connection p_con, ArrayList p_scheduledList, String p_channelUserID) throws BTSLBaseException {
        final String methodName = "loadSubcriberListToDeassociate";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_scheduledList.size() : ");
        	msg.append(p_scheduledList.size());
        	msg.append(", p_channelUserID : ");
        	msg.append(p_channelUserID);
        	String message = msg.toString();
            log.debug(methodName, message);
        }
        PreparedStatement pstmtSubList = null;
        ResultSet rs = null;
        ArrayList<ScheduleBatchDetailVO> activeOrSuspenedList = null;
        ScheduleBatchDetailVO scheduleDetailVO = null;
        try {
            final StringBuffer strBuff = new StringBuffer("SELECT RM.msisdn, RM.employee_name, RM.employee_code, RM.min_txn_amount, RM.owner_id, RM.modified_on, ");
            strBuff.append("RM.max_txn_amount, RM.monthly_limit, RM.total_txn_count, RM.status, L.lookup_name status_desc, RM.created_on ");
            strBuff.append("FROM restricted_msisdns RM, lookups L ");
            strBuff.append("WHERE RM.msisdn = ? ");
            strBuff.append("AND RM.channel_user_id=? ");
            strBuff.append("AND L.lookup_code = RM.status ");
            strBuff.append("AND L.lookup_type = ? ");
            strBuff.append("AND RM.restricted_type = ? ");
            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            activeOrSuspenedList = new ArrayList<ScheduleBatchDetailVO>();
            for (int i = 0, j = p_scheduledList.size(); i < j; i++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) p_scheduledList.get(i);
                pstmtSubList.setString(1, scheduleDetailVO.getMsisdn());
                pstmtSubList.setString(2, p_channelUserID);
                pstmtSubList.setString(3, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                pstmtSubList.setString(4, PretupsI.RESTRICTED_TYPE);
                rs = pstmtSubList.executeQuery();
                if (rs.next()) {
                    scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                    scheduleDetailVO.setEmployeeName(rs.getString("employee_name"));
                    scheduleDetailVO.setEmployeeCode(rs.getString("employee_code"));
                    scheduleDetailVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                    scheduleDetailVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                    scheduleDetailVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                    scheduleDetailVO.setTotalTxnCountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_count")));
                    scheduleDetailVO.setStatus(rs.getString("status"));
                    scheduleDetailVO.setStatusDes(rs.getString("status_desc"));
                    scheduleDetailVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                    scheduleDetailVO.setOwnerID(rs.getString("owner_id"));
                    scheduleDetailVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                    activeOrSuspenedList.add(scheduleDetailVO);
                }
                pstmtSubList.clearParameters();
                rs.close();
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubcriberListToDeassociate]", "",
                "", "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubcriberListToDeassociate]", "",
                "", "", "Exception:" + e.getMessage());
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
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "List Size" + activeOrSuspenedList.size());
            }
        }
        return activeOrSuspenedList;
    }

    /**
     * Method for Updating the status of the subscribers for
     * DeAssociation(change Status Active('Y') )
     * on the basis of owner_id and msisdn
     * Method :changeStatusForDeAssociation
     * 
     * @author babu.kunwar
     * @param p_con
     *            java.sql.Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_modifiedBy
     *            String
     * @param p_modifiedOn
     *            Date
     * @throws BTSLBaseException
     */
    public String changeStatusForDeAssociation(Connection p_con, ArrayList p_updatedList) throws BTSLBaseException {
        final String methodName = "changeStatusForDeAssociation";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_updatedList.size() : ");
        	msg.append(p_updatedList.size());
        	msg.append(", Entered: = p_updatedList : ");
        	msg.append(p_updatedList);        	

        	String message=msg.toString();
            log.debug(methodName, message);
        }
        // commented for DB2 OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        ScheduleBatchDetailVO scheduleDetailVO = null;
        int updateCount = 0;
        final StringBuffer nonUpdatedMsisdn = new StringBuffer();
        try {
            final StringBuffer strBuff = new StringBuffer("UPDATE restricted_msisdns SET status =?, modified_by = ?, ");
            strBuff.append("employee_code='',employee_name='',min_txn_amount='',max_txn_amount='',monthly_limit='',");
            strBuff.append("channel_user_id='', channel_user_category = '',");
            strBuff.append("modified_on = ? WHERE msisdn = ? AND owner_id = ? AND restricted_type=?");
            final String strUpdate = strBuff.toString();
            // commented for DB2 psmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(strUpdate);
            psmtUpdate = (PreparedStatement) p_con.prepareStatement(strUpdate);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query strUpdate:" + strUpdate);
            }
            boolean modified = false;
            for (int i = 0, j = p_updatedList.size(); i < j; i++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) p_updatedList.get(i);
                psmtUpdate.setString(1, scheduleDetailVO.getStatus());
                psmtUpdate.setString(2, scheduleDetailVO.getModifiedBy());
                psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(scheduleDetailVO.getModifiedOn()));
                psmtUpdate.setString(4, scheduleDetailVO.getMsisdn());
                psmtUpdate.setString(5, scheduleDetailVO.getOwnerID());
                psmtUpdate.setString(6, PretupsI.RESTRICTED_TYPE);
                modified = this.isRestrictedMsisdnModified(p_con, scheduleDetailVO.getLastModifiedTime(), scheduleDetailVO.getOwnerID(), scheduleDetailVO.getMsisdn());
                if (modified) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();
                if (updateCount <= 0) {
                    p_con.rollback();
                    nonUpdatedMsisdn.append(scheduleDetailVO.getMsisdn());
                    nonUpdatedMsisdn.append(",");
                    continue;
                } else {
                    p_con.commit();
                }
            }
        } catch (SQLException sqle) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changeSubscriberListStatus]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            try {
                p_con.rollback();
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changeSubscriberListStatus]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting");
            }
        }
        return nonUpdatedMsisdn.toString();
    }

    /**
     * This method is used to check that is the record modified during the
     * processing.
     * isRestrictedMsisdnModified
     * 
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_key
     *            String
     * @return boolean
     * @throws BTSLBaseException
     * @author babu.kunwar
     */
    private boolean isRestrictedMsisdnModified(Connection p_con, long p_oldlastModified, String p_key1, String p_key2) throws BTSLBaseException {
        final String methodName = "isRestrictedMsisdnModified";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_oldlastModified : ");
        	msg.append(p_oldlastModified);
        	msg.append(", p_key1 : ");
        	msg.append(p_key1);        	
        	msg.append(", p_key2 : ");
        	msg.append(p_key2);

        	String message = msg.toString();
            log.debug(methodName, message);
        }
        boolean modified = false;
        if (p_oldlastModified == 0) {
            modified = false;
            return modified;
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlRecordModified = new StringBuffer("SELECT modified_on FROM restricted_msisdns WHERE owner_id=? AND msisdn=?");
        java.sql.Timestamp newlastModified = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            pstmtSelect = p_con.prepareStatement(sqlRecordModified.toString());
            pstmtSelect.setString(1, p_key1);
            pstmtSelect.setString(2, p_key2);
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
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[isRestrictedMsisdnModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[isRestrictedMsisdnModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
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
        }
        return modified;
    }

    /**
     * Method :changeSubscriberListStatus
     * Method for Updating the status of the subscribers for Suspending(change
     * Status Active('Y') to Suspend('S'))
     * or for Rresuming(change Status Suspend('S') to Active('Y'))
     * 
     * @author babu.kunwar
     * @param p_con
     *            java.sql.Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_modifiedBy
     *            String
     * @param p_modifiedOn
     *            Date
     * @return nonUpdatedMsisdn String
     * @throws BTSLBaseException
     */
    public String changeSubscriberListStatus(Connection p_con, ArrayList p_updatedList, String p_modifiedBy, Date p_modifiedOn, String p_ownerID) throws BTSLBaseException {
        final String methodName = "changeSubscriberListStatus";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_updatedList.size() : ");
        	msg.append(p_updatedList.size());
        	msg.append(", p_modifiedBy : ");
        	msg.append(p_modifiedBy);        	
        	msg.append(", p_modifiedOn : ");
        	msg.append(p_modifiedOn);
        	msg.append(", p_ownerID : ");
        	msg.append(p_ownerID);

        	String message = msg.toString();
            log.debug(methodName, message);
        }
        PreparedStatement psmtUpdate = null;
        RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
        int updateCount = 0;
        final StringBuffer nonUpdatedMsisdn = new StringBuffer();
        boolean modified = false;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE restricted_msisdns SET status = ?, modified_by = ?, ");
            strBuff.append("modified_on = ? WHERE msisdn = ? AND owner_id = ? AND restricted_type=?");
            final String strUpdate = strBuff.toString();

            psmtUpdate = p_con.prepareStatement(strUpdate);

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query strUpdate:" + strUpdate);
            }

            for (int i = 0, j = p_updatedList.size(); i < j; i++) {
                iatRestrictedSubscriberVO = (RestrictedSubscriberVO) p_updatedList.get(i);

                psmtUpdate.setString(1, iatRestrictedSubscriberVO.getStatus());
                psmtUpdate.setString(2, p_modifiedBy);
                psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                psmtUpdate.setString(4, iatRestrictedSubscriberVO.getMsisdn());
                psmtUpdate.setString(5, p_ownerID);
                psmtUpdate.setString(6, PretupsI.RESTRICTED_TYPE);

                modified = this.isRestrictedMsisdnModified(p_con, iatRestrictedSubscriberVO.getLastModifiedTime(), iatRestrictedSubscriberVO.getOwnerID(),
                    iatRestrictedSubscriberVO.getMsisdn());
                if (modified) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }

                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();
                if (updateCount <= 0) {
                    p_con.rollback();
                    nonUpdatedMsisdn.append(iatRestrictedSubscriberVO.getMsisdn());
                    nonUpdatedMsisdn.append(",");
                    continue;
                }
                p_con.commit();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[changeSubscriberListStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[changeSubscriberListStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting :: nonUpdatedMsisdn.toString() :" + nonUpdatedMsisdn.toString());
            }
        }
        return nonUpdatedMsisdn.toString();
    }

    /*
     * ==========================================================================
     * ===========
     * * Susbcriber Suspend and Resume Methods *
     * ==========================================================================
     * ===========
     */
    /**
     * Method :loadSubcriberList
     * This method is used to load the the subscribers on the basis of their
     * MSISDN and channel user ID for suspending or resuming the subscribers
     * and if not found then show preoper error message accordingly
     * 
     * @author babu.kunwar
     * @param p_con
     *            java.sql.Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_errorList
     *            ArrayList
     * @param p_channelUserID
     *            String
     * @param p_requestType
     *            String
     * @param p_fwdPath
     *            String
     * @return list ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubcriberList(Connection p_con, ArrayList p_msisdnList, ArrayList<KeyArgumentVO> p_errorList, String p_channelUserID, String p_requestType, String p_fwdPath) throws BTSLBaseException {
        final String methodName = "loadSubcriberList";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: with parameter p_errorList.size() : ");
        	msg.append(p_errorList.size());
        	msg.append(", p_msisdnList.size() : ");
        	msg.append(p_msisdnList.size());        	
        	msg.append(", p_channelUserID : ");
        	msg.append(p_channelUserID);
        	msg.append(", p_requestType : ");
        	msg.append(p_requestType);
        	msg.append(", p_fwdPath : ");
        	msg.append(p_fwdPath);
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }

        RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
        PreparedStatement pstmtSubList = null;
        ArrayList<RestrictedSubscriberVO> subList = null;
        ResultSet rs = null;
        KeyArgumentVO keyArgumentVO = null;
        String status = null;
        String msisdn = null;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT RM.msisdn, RM.employee_name, RM.employee_code,RM.owner_id,RM.min_txn_amount, RM.max_txn_amount, ");
            strBuff.append("RM.monthly_limit, RM.total_txn_count, RM.created_on, RM.status status_code, LK.lookup_name status, ");
            strBuff.append("RM.subscriber_type,RM.modified_on,RM.language,RM.country FROM restricted_msisdns RM, lookups LK ");
            strBuff.append("WHERE RM.status = LK.lookup_code AND LK.lookup_type = ? AND ");
            strBuff.append("RM.msisdn = ? AND RM.channel_user_id = ? AND restricted_type=?");
            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            subList = new ArrayList<RestrictedSubscriberVO>();
            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                msisdn = (String) p_msisdnList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                final String[] msisdnArr = new String[1];
                msisdnArr[0] = msisdn;

                pstmtSubList.setString(1, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                pstmtSubList.setString(2, msisdn);
                pstmtSubList.setString(3, p_channelUserID);
                pstmtSubList.setString(4, PretupsI.RESTRICTED_TYPE);
                rs = pstmtSubList.executeQuery();
                pstmtSubList.clearParameters();
                if (rs.next()) {
                    status = rs.getString("status_code");
                    /*
                     * Checking the status whether the subscrive status is Ok
                     * for suspension.
                     * If its new,aprrove it can't be suspended.
                     */
                    if (status.equals(PretupsI.RES_MSISDN_STATUS_NEW)) {
                        keyArgumentVO.setArguments(msisdnArr);

                        if ("suspend".equals(p_requestType)) {
                            keyArgumentVO.setKey("iatrestrictedsubs.inputmsisdnsforsuspend.err.msg.statusnew");
                        } else {
                            keyArgumentVO.setKey("iatrestrictedsubs.inputmsisdnsforresume.err.msg.statusnew");
                        }
                        p_errorList.add(keyArgumentVO);
                        continue;
                    }
                    // Checking for Approve Status
                    if (status.equals(PretupsI.RES_MSISDN_STATUS_APPROVED)) {
                        keyArgumentVO.setArguments(msisdnArr);
                        if ("suspend".equals(p_requestType)) {
                            keyArgumentVO.setKey("iatrestrictedsubs.inputmsisdnsforsuspend.err.msg.statusapprove");
                        } else {
                            keyArgumentVO.setKey("iatrestrictedsubs.inputmsisdnsforresume.err.msg.statusapprove");
                        }
                        p_errorList.add(keyArgumentVO);
                        continue;
                    }
                    if ("suspend".equals(p_requestType)) {
                        if (status.equals(PretupsI.RES_MSISDN_STATUS_ASSOCIATED)) {
                            iatRestrictedSubscriberVO = new RestrictedSubscriberVO();

                            iatRestrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                            iatRestrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                            iatRestrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                            iatRestrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                            iatRestrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                            iatRestrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                            iatRestrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                            iatRestrictedSubscriberVO.setTotalTxnCountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_count")));
                            iatRestrictedSubscriberVO.setStatusDes(rs.getString("status"));
                            iatRestrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                            iatRestrictedSubscriberVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                            iatRestrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                            iatRestrictedSubscriberVO.setLanguage(rs.getString("language"));
                            iatRestrictedSubscriberVO.setCountry(rs.getString("country"));
                        } else {
                            /*
                             * Checking whether the subscriber is already
                             * suspended.
                             */
                            if (status.equals(PretupsI.RES_MSISDN_STATUS_SUSPENDED)) {
                                keyArgumentVO.setArguments(msisdnArr);
                                keyArgumentVO.setKey("iatrestrictedsubs.inputmsisdnsforsuspend.err.msg.statussuspend");
                                p_errorList.add(keyArgumentVO);
                                continue;
                            }
                        }
                    }
                    /*
                     * This ELSE block is for the resume action of the
                     * Subscriber
                     */
                    else {
                        if (status.equals(PretupsI.RES_MSISDN_STATUS_SUSPENDED)) {
                            iatRestrictedSubscriberVO = new RestrictedSubscriberVO();

                            iatRestrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                            iatRestrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                            iatRestrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                            iatRestrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                            iatRestrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                            iatRestrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                            iatRestrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                            iatRestrictedSubscriberVO.setTotalTxnCountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_count")));
                            iatRestrictedSubscriberVO.setStatusDes(rs.getString("status"));
                            iatRestrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                            iatRestrictedSubscriberVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                            iatRestrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                            iatRestrictedSubscriberVO.setLanguage(rs.getString("language"));
                            iatRestrictedSubscriberVO.setCountry(rs.getString("country"));
                        } else {
                            // Checking whether the susbscriber is already
                            // Active.
                            if (status.equals(PretupsI.RES_MSISDN_STATUS_ASSOCIATED)) {
                                keyArgumentVO.setArguments(msisdnArr);
                                keyArgumentVO.setKey("iatrestrictedsubs.inputmsisdnsforresume.err.msg.statusactive");
                                p_errorList.add(keyArgumentVO);
                                continue;
                            }
                        }
                    }
                    subList.add(iatRestrictedSubscriberVO);
                } else {
                    keyArgumentVO.setArguments(msisdnArr);
                    keyArgumentVO.setKey("iatrestrictedsubs.inputmsisdnsforsuspend.err.msg.nosubfound");
                    p_errorList.add(keyArgumentVO);
                    continue;
                }
            }
            // If there is any error in the MSISDN's then throw exception
            if (!p_errorList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, p_errorList, p_fwdPath);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadSubcriberList]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadSubcriberList]", "",
                "", "", "Exception:" + e.getMessage());
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
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: list.size() : =" + subList.size());
            }
        }
        return subList;
    }

    /**
     * Method loadResSubsDetails
     * Method for Extracting Corporate Subscriber Details.
     * This Method will Load the Restricted Subscriber Details according to
     * the msisdn and owner_id. or msisdn and channel_user_id or date rang. *
     * 
     * @author babu.kunwar
     * @param p_con
     *            Connection
     * @param p_status
     *            String
     * @param p_msisdn
     *            String
     * @param p_userID
     *            String
     * @param p_checkType
     *            boolean (if true then check condition owner_id else
     *            channel_user_id)
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadResSubsDetails(Connection p_con, String p_msisdn, String p_userID, boolean p_isOwnerID, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadResSubsDetails";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_msisdn : ");
        	msg.append(p_msisdn);
        	msg.append(", p_userID : ");
        	msg.append(p_userID);        	
        	msg.append(", p_isOwnerID : ");
        	msg.append(p_isOwnerID);
        	msg.append(", p_fromDate : ");
        	msg.append(p_fromDate);
        	msg.append(", p_toDate : ");
        	msg.append(p_toDate);
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
        final StringBuffer strBuff = new StringBuffer("SELECT r.msisdn,r.subscriber_id,r.channel_user_id,r.channel_user_category,r.employee_code,r.owner_id,");
        strBuff.append("r.employee_name,r.network_code,r.min_txn_amount,r.max_txn_amount,r.monthly_limit,r.association_date,");
        strBuff.append("r.total_txn_count,r.total_txn_amount,r.remark,r.approved_by,lk.lookup_name status_desc,");
        strBuff.append("r.approved_on,r.associated_by,r.status,r.created_on,r.created_by,r.modified_on,r.modified_by,");
        strBuff.append("r.subscriber_type,r.language,r.country, r.country_code");
        strBuff.append(" FROM restricted_msisdns r,lookups lk ");
        strBuff.append(" WHERE lk.lookup_code=r.status AND lk.lookup_type=? ");
        strBuff.append(" AND r.restricted_type=? AND");
        if (p_isOwnerID)// check for owner_id
        {
            if (BTSLUtil.isNullString(p_msisdn)) {
                // show all data of chennel user
                strBuff.append(" r.owner_id=? ");
            } else {
                strBuff.append(" r.msisdn=? AND r.owner_id=? ");
            }
        } else // check for channel_user_id
        {
            if (BTSLUtil.isNullString(p_msisdn)) {
                // show all data of chennel user
                strBuff.append(" r.channel_user_id=? ");
            } else {
                strBuff.append(" r.msisdn=? AND channel_user_id=? ");
            }
        }
        if (p_fromDate != null && p_toDate != null)// date range check
        {
            strBuff.append(" AND TRUNC(r.created_on) >= ?");
            strBuff.append(" AND TRUNC(r.created_on) <= ?");
        }
        strBuff.append(" ORDER BY r.employee_name ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList<RestrictedSubscriberVO> restSubsList = new ArrayList<RestrictedSubscriberVO>();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
            pstmtSelect.setString(i++, PretupsI.RESTRICTED_TYPE);
            if (!BTSLUtil.isNullString(p_msisdn)) {
                pstmtSelect.setString(i++, p_msisdn);
            }
            pstmtSelect.setString(i++, p_userID);
            if (p_fromDate != null && p_toDate != null)// date range check
            {
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                iatRestrictedSubscriberVO = new RestrictedSubscriberVO();
                iatRestrictedSubscriberVO.setMsisdn(rs.getInt("country_code") + rs.getString("msisdn"));
                iatRestrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                iatRestrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                iatRestrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                iatRestrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                iatRestrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                iatRestrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                iatRestrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                iatRestrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(iatRestrictedSubscriberVO.getMonthlyLimit()));
                iatRestrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                iatRestrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(iatRestrictedSubscriberVO.getMinTxnAmount()));
                iatRestrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                iatRestrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(iatRestrictedSubscriberVO.getMaxTxnAmount()));
                iatRestrictedSubscriberVO.setTotalTxnCount(rs.getLong("total_txn_count"));
                iatRestrictedSubscriberVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                iatRestrictedSubscriberVO.setTotalTransferAmountForDisp(PretupsBL.getDisplayAmount(iatRestrictedSubscriberVO.getTotalTransferAmount()));
                iatRestrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                iatRestrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("created_on"))));
                iatRestrictedSubscriberVO.setStatus(rs.getString("status"));
                iatRestrictedSubscriberVO.setStatusDes(rs.getString("status_desc"));
                iatRestrictedSubscriberVO.setRemarks(rs.getString("remark"));
                iatRestrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                iatRestrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                iatRestrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                iatRestrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                iatRestrictedSubscriberVO.setLanguage(rs.getString("language"));
                iatRestrictedSubscriberVO.setCountry(rs.getString("country"));
                iatRestrictedSubscriberVO.setCountryCode(rs.getInt("country_code"));
                restSubsList.add(iatRestrictedSubscriberVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadResSubsDetails]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadResSubsDetails]",
                "", "", "", "Exception:" + ex.getMessage());
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
                log.debug(methodName, "Exiting restSubsList.size()= " + restSubsList.size());
            }
        }
        return restSubsList;
    }

    /**
     * Method addIATBatchForScheduling.
     * This method is to add the information of the schedule batch in the parent
     * table.
     * 
     * @author babu.kunwar
     * @param p_con
     *            Connection
     * @param p_scheduleMasterVO
     *            ScheduleBatchMasterVO
     * @return int
     * @throws BTSLBaseException
     *             modify the query insert batch_type in scheduled_batch_master
     */
    public int addIATBatchForScheduling(Connection p_con, ScheduleBatchMasterVO p_scheduleMasterVO) throws BTSLBaseException {
        final String methodName = "addIATBatchForScheduling";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_scheduleMasterVO=" + p_scheduleMasterVO);
        }

        PreparedStatement pstmtInsert = null;
        int addCount = 0;

        try {
            final StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO scheduled_batch_master (batch_id, status, ");
            insertQueryBuff.append("network_code, total_count, successful_count, upload_failed_count, process_failed_count,");
            insertQueryBuff.append("cancelled_count, scheduled_date, parent_id, owner_id, parent_category, parent_domain, ");
            insertQueryBuff.append("service_type, created_on, created_by, modified_on, modified_by, initiated_by,ref_batch_id,batch_type,active_user_id ) ");
            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY insertQuery:" + insertQueryBuff);
            }
            pstmtInsert = p_con.prepareStatement(insertQueryBuff.toString());
            pstmtInsert.setString(1, p_scheduleMasterVO.getBatchID());
            pstmtInsert.setString(2, p_scheduleMasterVO.getStatus());
            pstmtInsert.setString(3, p_scheduleMasterVO.getNetworkCode());
            pstmtInsert.setLong(4, p_scheduleMasterVO.getTotalCount());
            pstmtInsert.setLong(5, p_scheduleMasterVO.getSuccessfulCount());
            pstmtInsert.setLong(6, p_scheduleMasterVO.getUploadFailedCount());
            pstmtInsert.setLong(7, p_scheduleMasterVO.getProcessFailedCount());
            pstmtInsert.setLong(8, p_scheduleMasterVO.getCancelledCount());
            pstmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getScheduledDate()));
            pstmtInsert.setString(10, p_scheduleMasterVO.getParentID());
            pstmtInsert.setString(11, p_scheduleMasterVO.getOwnerID());
            pstmtInsert.setString(12, p_scheduleMasterVO.getParentCategory());
            pstmtInsert.setString(13, p_scheduleMasterVO.getParentDomain());
            pstmtInsert.setString(14, p_scheduleMasterVO.getServiceType());
            pstmtInsert.setTimestamp(15, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getCreatedOn()));
            pstmtInsert.setString(16, p_scheduleMasterVO.getCreatedBy());
            pstmtInsert.setTimestamp(17, BTSLUtil.getTimestampFromUtilDate(p_scheduleMasterVO.getModifiedOn()));
            pstmtInsert.setString(18, p_scheduleMasterVO.getModifiedBy());
            pstmtInsert.setString(19, p_scheduleMasterVO.getInitiatedBy());
            pstmtInsert.setString(20, p_scheduleMasterVO.getRefBatchID());
            pstmtInsert.setString(21, p_scheduleMasterVO.getBatchType());
            pstmtInsert.setString(22, p_scheduleMasterVO.getActiveUserId());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[addIATBatchForScheduling]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[addIATBatchForScheduling]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
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
        }
        return addCount;
    }

    /**
     * Method Name loadScheduleBatchDetailsMap
     * It is used to load the whole details for the batch
     * scheduled for Recharge
     * 
     * @param p_con
     * @param p_batch_id
     * @param p_statusUsed
     * @param p_status
     * @return LinkedHashMap
     * @throws BTSLBaseException
     * @author babu.kunwar
     */
    public LinkedHashMap loadScheduleBatchDetailsMap(Connection p_con, String p_batch_id, String p_statusUsed, String p_status) throws BTSLBaseException {
        final LinkedHashMap<String, ScheduleBatchDetailVO> detailHashMap = new LinkedHashMap<String, ScheduleBatchDetailVO>();
        try {
            final ArrayList list = loadBatchDetailVOList(p_con, p_batch_id, p_statusUsed, p_status);
            ScheduleBatchDetailVO scheduleDetailVO = null;
            for (int i = 0, j = list.size(); i < j; i++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) list.get(i);
                detailHashMap.put(scheduleDetailVO.getMsisdn(), scheduleDetailVO);
            }
        } catch (BTSLBaseException be) {
            throw be;
        }
        return detailHashMap;
    }

    /**
     * method Name
     * This method is used for creating the VO for the batch
     * 
     * @param p_con
     * @param p_batchID
     * @param p_statusUsed
     * @param p_status
     * @return List
     * @throws BTSLBaseException
     * @author babu.kunwar
     */
    public ArrayList loadBatchDetailVOList(Connection p_con, String p_batchID, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadBatchDetailVOList";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_status : ");
        	msg.append(p_status);
        	msg.append(", p_statusUsed : ");
        	msg.append(p_statusUsed);        	
        	msg.append(", p_batchID : ");
        	msg.append(p_batchID);
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList<ScheduleBatchDetailVO> scheduleDetailsVOList = new ArrayList<ScheduleBatchDetailVO>();
        try {
        	pstmtSelect = iatRestrictedSubscriberWebQry.loadBatchDetailVOListQry(p_con, p_batchID, p_statusUsed, p_status);
            rs = pstmtSelect.executeQuery();
            ScheduleBatchDetailVO scheduleDetailVO = null;

            while (rs.next()) {

                scheduleDetailVO = new ScheduleBatchDetailVO();
                scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                scheduleDetailVO.setStatus(rs.getString("status"));
                scheduleDetailVO.setScheduleStatus(scheduleDetailVO.getStatus());
                scheduleDetailVO.setPrevScheduleStatus(scheduleDetailVO.getStatus());
                scheduleDetailVO.setStatusDes(rs.getString("status_desc"));
                scheduleDetailVO.setTransactionStatus(rs.getString("transfer_status"));
                scheduleDetailVO.setTransactionID(rs.getString("transfer_id"));
                scheduleDetailVO.setAmount(rs.getLong("amount"));
                scheduleDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()));
                scheduleDetailVO.setBatchID(rs.getString("batch_id"));
                scheduleDetailVO.setSubscriberID(rs.getString("subscriber_id"));
                scheduleDetailVO.setNetworkCode(rs.getString("network_code"));
                scheduleDetailVO.setSubscriberType(rs.getString("service_type"));
                scheduleDetailVO.setSubscriberTypeDescription(rs.getString("description"));
                scheduleDetailVO.setScheduleDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("scheduled_date")));
                scheduleDetailVO.setCreatedBy(rs.getString("created_by"));
                scheduleDetailVO.setSubService(rs.getString("sub_service"));
                scheduleDetailVO.setSubServiceDesc(PretupsBL.getSelectorDescriptionFromCode(rs.getString("stype") + "_" + scheduleDetailVO.getSubService()));
                scheduleDetailVO.setProcessedOn(rs.getTimestamp("processed_on"));
                if (scheduleDetailVO.getProcessedOn() != null) {
                    scheduleDetailVO.setProcessedOnAsString(BTSLUtil.getDateTimeStringFromDate(scheduleDetailVO.getProcessedOn()));
                }
                scheduleDetailVO.setCreatedOn(rs.getTimestamp("created_on"));
                scheduleDetailVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(scheduleDetailVO.getCreatedOn()));
                scheduleDetailVO.setModifiedOn(rs.getDate("modified_on"));
                scheduleDetailVO.setModifiedBy(rs.getString("modified_by"));
                scheduleDetailVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                scheduleDetailVO.setTransferErrorCode(rs.getString("error_code"));
                scheduleDetailVO.setLanguage(rs.getString("r_language"));
                scheduleDetailVO.setCountry(rs.getString("r_country"));
                scheduleDetailVO.setDonorLanguage(rs.getString("d_language"));
                scheduleDetailVO.setDonorCountry(rs.getString("d_country"));
                scheduleDetailVO.setDonorMsisdn(rs.getString("donor_msisdn"));
                scheduleDetailVO.setDonorName(rs.getString("donor_name"));
                scheduleDetailsVOList.add(scheduleDetailVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadBatchDetailVOList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadBatchDetailVOList]",
                "", "", "", "Exception:" + ex.getMessage());
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
     * This method is used to load the details of schedules according t the
     * mobile numbers entered
     * against the seleted channel user
     * 
     * @author babu.kunwar
     * @param p_con
     * @param p_batchDtlsStatus
     * @param p_msisdn
     * @param p_batchID
     * @param p_isRestricted
     *            TODO
     * @param p_batchStatus
     * @param p_restrictedMsisdnStatus
     * @return ArrayList
     * @throws BTSLBaseException
     *             ArrayList
     *             modify method signature add boolean p_isRestricted and modify
     *             query according to p_isRestricted
     */
    public ArrayList loadDetailsForCancelSingle(Connection p_con, String p_batchDtlsStatus, String p_msisdn, String p_msisdnCountryCode, String p_batchID, boolean p_isRestricted, String p_userID) throws BTSLBaseException {
        final String methodName = "loadDetailsForCancelSingle";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_batchDtlsStatus : ");
        	msg.append(p_batchDtlsStatus);
        	msg.append(", p_msisdn : ");
        	msg.append(p_msisdn);        	
        	msg.append(", p_batchID : ");
        	msg.append(p_batchID);
        	msg.append(", p_isRestricted : ");
        	msg.append(p_isRestricted);
        	msg.append(", p_userID : ");
        	msg.append(p_userID);
        	msg.append(", p_msisdnCountryCode : ");
        	msg.append(p_msisdnCountryCode);
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }
        PreparedStatement pstmtSubList = null;
        ResultSet rs = null;
        final ArrayList<ScheduleBatchDetailVO> cancelList = new ArrayList<ScheduleBatchDetailVO>();
        final ArrayList<ScheduleBatchMasterVO> scheduleMasterVOList = new ArrayList<ScheduleBatchMasterVO>();
        ScheduleBatchDetailVO scheduleDetailVO = null;
        ScheduleBatchMasterVO scheduleMasterVO = null;
        try {
            StringBuffer strBuff = null;
            if (p_isRestricted) {
                strBuff = new StringBuffer(" SELECT sbm.batch_id,rm.msisdn,rm.employee_code,rm.employee_name,rm.monthly_limit,");
                strBuff.append(" rm.min_txn_amount,rm.max_txn_amount,rm.total_txn_amount,sbm.service_type stype,");
                strBuff
                    .append(" sbd.amount,sbm.scheduled_date,sbd.transfer_status,u.user_name, lk.lookup_name status,sbd.modified_on, sbd.sub_service, U2.user_name active_user_name ");
                strBuff.append(" FROM restricted_msisdns rm,scheduled_batch_master sbm,scheduled_batch_detail sbd, users u, lookups lk, Users U2 ");
                strBuff.append(" WHERE sbd.batch_id=sbm.batch_id AND sbd.status=?  AND sbm.owner_id=rm.owner_id ");
                strBuff.append(" AND rm.msisdn in(" + p_msisdn + ")");
                strBuff.append(" AND sbd.msisdn in(" + p_msisdnCountryCode + ")");
                strBuff.append(" AND ( rm.associated_by ='" + p_userID + "' OR sbm.parent_id ='" + p_userID + "') AND U2.user_id=sbm.active_user_id ");
                strBuff.append(" AND u.user_id=sbm.initiated_by AND sbd.status = lk.lookup_code ");
                strBuff.append(" AND sbm.batch_id=?  AND lk.lookup_type = ? AND rm.status = ?  ");
            } else {
                strBuff = new StringBuffer("SELECT sbm.batch_id,sbd.msisdn,sbm.service_type stype, sbd.amount,");
                strBuff.append("sbm.scheduled_date,sbd.transfer_status, u.user_name, lk.lookup_name status,sbd.modified_on, sbd.sub_service, U2.user_name active_user_name ");
                strBuff.append("FROM scheduled_batch_master sbm,scheduled_batch_detail sbd, users u, lookups lk, Users U2 ");
                strBuff.append("WHERE sbd.batch_id=sbm.batch_id   AND sbd.status=? AND sbd.msisdn in(" + p_msisdn + ") ");
                strBuff.append("AND sbm.batch_id=? AND u.user_id=sbm.initiated_by AND sbd.status = lk.lookup_code AND lk.lookup_type = ? AND U2.user_id=sbm.active_user_id ");
            }

            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            int i = 1;
            pstmtSubList.setString(i++, p_batchDtlsStatus);
            pstmtSubList.setString(i++, p_batchID);
            pstmtSubList.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            if (p_isRestricted) {
                pstmtSubList.setString(i++, PretupsI.YES);
            }
            rs = pstmtSubList.executeQuery();
            while (rs.next()) {
                scheduleDetailVO = new ScheduleBatchDetailVO();
                scheduleMasterVO = new ScheduleBatchMasterVO();
                if (p_isRestricted) {
                    scheduleDetailVO.setEmployeeCode(rs.getString("employee_code"));
                    scheduleDetailVO.setEmployeeName(rs.getString("employee_name"));
                    scheduleDetailVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                    scheduleDetailVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                    scheduleDetailVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                    scheduleDetailVO.setTotalTransferAmountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_amount")));
                }
                scheduleDetailVO.setBatchID(rs.getString("batch_id"));
                scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                scheduleDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(rs.getLong("amount")));
                scheduleDetailVO.setSubService(rs.getString("sub_service"));
                scheduleDetailVO.setSubServiceDesc(PretupsBL.getSelectorDescriptionFromCode(rs.getString("stype") + "_" + scheduleDetailVO.getSubService()));
                scheduleDetailVO.setStatus(rs.getString("status"));
                scheduleDetailVO.setTransactionStatus(rs.getString("transfer_status"));
                scheduleDetailVO.setCreatedBy(rs.getString("user_name"));
                scheduleDetailVO.setModifiedOn(rs.getTimestamp("modified_on"));
                scheduleDetailVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleMasterVO.setScheduledDate(rs.getDate("scheduled_date"));

                cancelList.add(scheduleDetailVO);
                scheduleMasterVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleMasterVO.setList(cancelList);
                scheduleMasterVOList.add(scheduleMasterVO);
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[loadDetailsForCancelSingle]", "", "", "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[loadDetailsForCancelSingle]", "", "", "", "Exception:" + e.getMessage());
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
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "List Size" + cancelList.size());
            }
        }
        return scheduleMasterVOList;
    }

    /**
     * Method loadSubsListForDelete. This method is used for deleteing the
     * Subscriber List
     * 
     * @author babu.kunwar
     *         This method is used to load the the subscribers on the basis of
     *         their
     *         MSISDN and ownerID for deleting the subscribers
     *         Method :loadSubsListForDelete
     * @param p_con
     *            java.sql.Connection
     * @param p_path
     *            mapping path
     * @param p_msisdnList
     *            ArrayList
     * @param p_ownerID
     *            String
     * @param p_invalidMsisdn
     *            StringBuffer
     * @return subList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubsListForDelete(Connection p_con, ArrayList p_msisdnList, String p_ownerID, StringBuffer p_invalidMsisdn) throws BTSLBaseException {
        final String methodName = "loadSubsListForDelete";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_msisdnList.size() : ");
        	msg.append(p_msisdnList.size());
        	msg.append(", p_ownerID : ");
        	msg.append(p_ownerID);        	
        	msg.append(", p_invalidMsisdn : ");
        	msg.append(p_invalidMsisdn);
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        PreparedStatement pstmtSubList = null;
        final ArrayList<RestrictedSubscriberVO> subList = new ArrayList<RestrictedSubscriberVO>();
        ResultSet rs = null;
        try {
            final StringBuffer strBuff = new StringBuffer("SELECT RM.msisdn,RM.employee_name, RM.employee_code, RM.min_txn_amount,RM.status, ");
            strBuff.append("RM.owner_id, RM.max_txn_amount,RM.max_txn_amount,RM.monthly_limit, RM.total_txn_count, ");
            strBuff.append("RM.created_on , L.lookup_name status_desc ");
            strBuff.append("FROM restricted_msisdns RM, lookups L  ");
            strBuff.append("WHERE  RM.msisdn=? AND RM.owner_id=? AND RM.restricted_type= ? ");
            strBuff.append("AND RM.msisdn NOT IN( ");
            strBuff.append("SELECT  SBD.msisdn ");
            strBuff.append("FROM scheduled_batch_detail SBD, scheduled_batch_master SBM, restricted_msisdns RM  ");
            strBuff.append("WHERE SBD.batch_id=SBM.batch_id  AND SBM.owner_id=?  ");
            strBuff.append("AND SBD.status IN ('" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "', '" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "')) ");
            strBuff.append("AND L.lookup_code = RM.status AND L.lookup_type =? ");
            strBuff.append("ORDER BY RM.employee_name ");

            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            final int j = p_msisdnList.size();
            for (int i = 0; i < j; i++) {
                pstmtSubList.setString(1, (String) p_msisdnList.get(i));
                pstmtSubList.setString(2, p_ownerID);
                pstmtSubList.setString(3, PretupsI.RESTRICTED_TYPE);
                pstmtSubList.setString(4, p_ownerID);
                pstmtSubList.setString(5, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                rs = pstmtSubList.executeQuery();
                pstmtSubList.clearParameters();
                if (rs.next()) {
                    restrictedSubscriberVO = new RestrictedSubscriberVO();
                    restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                    restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                    restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                    restrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                    restrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                    restrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                    restrictedSubscriberVO.setTotalTxnCountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_count")));
                    restrictedSubscriberVO.setStatus(rs.getString("status"));
                    restrictedSubscriberVO.setStatusDes(rs.getString("status_desc"));
                    restrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                    restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                    restrictedSubscriberVO.setCheckBoxVal("DR");
                    subList.add(restrictedSubscriberVO);
                } else {
                    p_invalidMsisdn.append(p_msisdnList.get(i));
                    p_invalidMsisdn.append(",");
                }
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadSubsListForDelete]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[loadSubsListForDelete]",
                "", "", "", "Exception:" + e.getMessage());
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
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: subList.size() : =" + subList.size());
            }
        }
        return subList;
    }

    /**
     * Method :deleteResSubscriberBulk
     * This method is used to delete the details of susbcriber(s)
     * 
     * @author babu.kunwar
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            Arraylist
     * @param p_ownerID
     * @return String
     * @throws BTSLBaseException
     * 
     */
    public String deleteResSubscriberBulk(Connection p_con, ArrayList p_msisdnList, String p_ownerID) throws BTSLBaseException {
        final String methodName = "deleteResSubscriberBulk";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered: p_msisdnList : ");
        	msg.append(p_msisdnList);
        	msg.append(", p_ownerID : ");
        	msg.append(p_ownerID);        	
        	
        	String message = msg.toString();
            log.debug(methodName, message);
        }

        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        final StringBuffer nonDeletedMsisdn = new StringBuffer();
        int deleteCount = 0;
        final String sqlDelete = "DELETE FROM restricted_msisdns WHERE owner_id=? AND msisdn=? ";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdate=" + sqlDelete);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlDelete);
            RestrictedSubscriberVO restrictedSubscriberVO = null;
            for (int index = 0, j = p_msisdnList.size(); index < j; index++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_msisdnList.get(index);
                pstmtUpdate.setString(1, restrictedSubscriberVO.getOwnerID());
                pstmtUpdate.setString(2, restrictedSubscriberVO.getMsisdn());
                deleteCount = pstmtUpdate.executeUpdate();
                pstmtUpdate.clearParameters();
                if (deleteCount <= 0) {
                    p_con.rollback();
                    nonDeletedMsisdn.append(restrictedSubscriberVO.getMsisdn());
                    nonDeletedMsisdn.append(",");
                    continue;
                }
                p_con.commit();
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[deleteResSubscriberBulk]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "IATRestrictedSubscriberWebDAO[deleteResSubscriberBulk]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting: deleteCount : ");
            	msg.append(deleteCount);
            	msg.append(", nonDeletedMsisdn : ");
            	msg.append(nonDeletedMsisdn);        	
            	
            	String message = msg.toString();
                log.debug(methodName, message);
            }
        }
        return nonDeletedMsisdn.toString();
    }

    /**
     * Method :deleteRestrictedBulk
     * This method is used to delete the details of all the susbcribers
     * 
     * @author babu.kunwar
     * @param p_con
     *            Connection
     * @param p_ownerID
     *            String
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int deleteRestrictedBulk(Connection p_con, String p_ownerID) throws BTSLBaseException {
        final String methodName = "deleteRestrictedBulk";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_ownerID = " + p_ownerID);
        }

        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int deleteCount = -1;
        final StringBuffer strBuff = new StringBuffer("DELETE FROM restricted_msisdns RM ");
        strBuff.append("WHERE owner_id= ? ");
        strBuff.append("AND RM.msisdn NOT IN (");
        strBuff.append("SELECT  SBD.msisdn FROM scheduled_batch_detail SBD, scheduled_batch_master SBM ");
        strBuff.append("WHERE SBD.batch_id=SBM.batch_id ");
        strBuff.append("AND SBM.owner_id=? ");
        strBuff.append("AND SBD.status IN ('" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "', '" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "')) ");

        final String sqlUpdate = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            pstmtUpdate.setString(1, p_ownerID);
            pstmtUpdate.setString(2, p_ownerID);
            deleteCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[deleteRestrictedBulk]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberWebDAO[deleteRestrictedBulk]",
                "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        }
        return deleteCount;
    }

    /**
     * Method for loading Users Assigned Services List(means Services that are
     * assigned to the user).
     * From the table USER_SERVICES
     * 
     * Used in(userAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadUserServicesList(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserServicesList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_userId=" + p_userId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        // Modification for Service Management [by Babu Kunwar]
        strBuff.append(" SELECT US.service_type,ST.name FROM user_services US,service_type ST,users U,category_service_type CST");
        strBuff.append(" WHERE US.user_id = ? AND US.service_type = ST.service_type");
        strBuff.append(" AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type and CST.network_code=U.network_code");
        strBuff.append(" AND ST.TYPE = ? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            pstmt.setString(2, PretupsI.RESTRICTED_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ServicesTypeDAO[loadUserServicesList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: userServicesList size=" + list.size());
            }
        }
        return list;
    }
}
