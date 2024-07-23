package com.txn.pretups.p2p.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBatchesVO;
import com.btsl.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.btsl.util.BTSLUtil;

public class SubscriberTxnDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private SubscriberTxnQry subscriberTxnQry;

    /**
     * Constructor for SubscriberTxnDAO.
     */
    public SubscriberTxnDAO() {
        super();
        subscriberTxnQry = (SubscriberTxnQry)ObjectProducer.getObject(QueryConstants.SUBSCRIBER_TXN_QRY, QueryConstants.QUERY_PRODUCER);
        // TODO Auto-generated constructor stub
    }

    /**
     * To check the existance of the language code
     * 
     * @author sandeep.goel
     * @param p_con
     * @param p_languageCode
     * @return
     * @throws BTSLBaseException
     *             ChangeLocaleVO
     */
    public ChangeLocaleVO loadLanguageDetails(Connection p_con, String p_languageCode) throws BTSLBaseException {

        final String methodName = "loadLanguageDetails";
        LogFactory.printLog(methodName, "Entered p_languageCode:" + p_languageCode, _log);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChangeLocaleVO changeLocaleVO = null;
        try {
            // ChangeID=LOCALEMASTER
            // Query is changed so that language of type SMS or BOTH are loaded
            String selectQuery = "SELECT language, country, name FROM  locale_master WHERE  language_code=? AND status!='N' AND (type=? OR type=?) ";
            LogFactory.printLog(methodName, "select query:" + selectQuery, _log);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setInt(1, Integer.parseInt(p_languageCode));
            pstmtSelect.setString(2, PretupsI.SMS_LOCALE);
            pstmtSelect.setString(3, PretupsI.BOTH_LOCALE);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                changeLocaleVO = new ChangeLocaleVO();
                changeLocaleVO.setCountry(rs.getString("country"));
                changeLocaleVO.setLanguageCode(rs.getString("language"));
                changeLocaleVO.setLanguageName(rs.getString("name"));
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTxnDAO[isLanguageCodeExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTxnDAO[isLanguageCodeExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting  ChangeLocaleVO " + changeLocaleVO, _log);
        }// end of finally
        return changeLocaleVO;
    }

    /**
     * It loads details of list and data in the list on the basis of schedule
     * type i.e. M/W/D in
     * scheduled multiple credit transfer
     * 
     * @param p_con
     * @param p_scheduleType
     * @return
     * @throws BTSLBaseException
     * @author sonali.garg
     */
    public ArrayList<Object> loadBuddyDetailsByScheduleType(Connection p_con, String p_scheduleType) throws BTSLBaseException {
        final String methodName = "loadBuddyDetailsByScheduleType";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_scheduleType:" + p_scheduleType);
        }

        PreparedStatement pstmtSelect = null;
        BuddyVO buddyVO = null;
        P2PBatchesVO batchBuddyVO = null;
        ResultSet rs = null;
        ArrayList<Object> listBuddyBatchListDetails = new ArrayList<Object>();
        ArrayList<Object> listBuddyListDetails = new ArrayList<Object>();
        boolean checkData = false;
        try {

            String selectQuery = subscriberTxnQry.loadBuddyDetailsByScheduleTypeQry();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_scheduleType);
            rs = pstmtSelect.executeQuery();

            String batchIDPrevious = null;
            String BatchIDCurrent = null;
            batchBuddyVO = new P2PBatchesVO();
            while (rs.next()) {
                checkData = true;
                BatchIDCurrent = rs.getString("batch_id");
                if (rs.isFirst()) {
                    batchIDPrevious = rs.getString("batch_id");
                }
                if (BatchIDCurrent.equals(batchIDPrevious)) {
                    batchBuddyVO.setBatchID(rs.getString("batch_id"));
                    batchBuddyVO.setParentID(rs.getString("parent_id"));
                    batchBuddyVO.setListName(rs.getString("list_name"));
                    batchBuddyVO.setStatus(rs.getString("batch_status"));
                    batchBuddyVO.setScheduleType(rs.getString("schedule_type"));
                    batchBuddyVO.setNoOfSchedule(rs.getLong("no_of_schedule"));
                    batchBuddyVO.setBatchTotalRecords(rs.getLong("batch_total_record"));
                    batchBuddyVO.setExecutionCount(rs.getLong("execution_count"));
                    if (rs.getTimestamp("created_on") != null) {
                        batchBuddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                    }
                    batchBuddyVO.setCreatedBy(rs.getString("created_by"));
                    if (rs.getTimestamp("modified_on") != null) {
                        batchBuddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                    }
                    batchBuddyVO.setModifiedBy(rs.getString("modified_by"));
                    batchBuddyVO.setSenderMSISDN(rs.getString("msisdn"));
                    batchBuddyVO.setSenderLocale(rs.getString("language"));
                    batchBuddyVO.setSenderPin(rs.getString("pin"));
                    batchBuddyVO.setSenderCountry(rs.getString("country"));
                    batchBuddyVO.setNetworkCode(rs.getString("network_code"));
                    batchBuddyVO.setScheduleDate(rs.getDate("schedule_date"));

                    buddyVO = new BuddyVO();
                    buddyVO.setBuddyMsisdn(rs.getString("buddy_msisdn"));
                    buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                    buddyVO.setSuccessiveFailCount(rs.getLong("successive_failure_count"));
                    buddyVO.setSelectorCode(rs.getString("selector_code"));
                    // entries fetch for writing into relevant logs by harsh
                    buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                    buddyVO.setLastTransferOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("buddy_last_transfer_on")));
                    buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                    buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));

                    // added by harsh for handling consecutive failure case
                    if (rs.getTimestamp("modified_on") != null) {
                        buddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                    }
                    buddyVO.setModifiedBy(rs.getString("modified_by"));
                    buddyVO.setListName(rs.getString("list_name"));
                    buddyVO.setOwnerUser(rs.getString("parent_id"));

                    listBuddyListDetails.add(buddyVO);
                }

                if (!(BatchIDCurrent.equals(batchIDPrevious))) {
                    batchIDPrevious = BatchIDCurrent;
                    batchBuddyVO.setBuddyList(listBuddyListDetails);
                    listBuddyBatchListDetails.add(batchBuddyVO);

                    batchBuddyVO = new P2PBatchesVO();
                    listBuddyListDetails = new ArrayList<Object>();
                    batchBuddyVO.setBatchID(rs.getString("batch_id"));
                    batchBuddyVO.setParentID(rs.getString("parent_id"));
                    batchBuddyVO.setListName(rs.getString("list_name"));
                    batchBuddyVO.setStatus(rs.getString("buddy_status"));
                    batchBuddyVO.setScheduleType(rs.getString("schedule_type"));
                    batchBuddyVO.setNoOfSchedule(rs.getLong("no_of_schedule"));
                    batchBuddyVO.setBatchTotalRecords(rs.getLong("batch_total_record"));
                    batchBuddyVO.setExecutionCount(rs.getLong("execution_count"));
                    if (rs.getTimestamp("created_on") != null) {
                        batchBuddyVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                    }
                    batchBuddyVO.setCreatedBy(rs.getString("created_by"));
                    if (rs.getTimestamp("modified_on") != null) {
                        batchBuddyVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                    }
                    batchBuddyVO.setModifiedBy(rs.getString("modified_by"));
                    batchBuddyVO.setSenderMSISDN(rs.getString("msisdn"));
                    batchBuddyVO.setSenderLocale(rs.getString("language"));
                    batchBuddyVO.setSenderPin(rs.getString("pin"));
                    batchBuddyVO.setSenderCountry(rs.getString("country"));
                    batchBuddyVO.setNetworkCode(rs.getString("network_code"));
                    batchBuddyVO.setScheduleDate(rs.getDate("schedule_date"));

                    buddyVO = new BuddyVO();
                    buddyVO.setBuddyMsisdn(rs.getString("buddy_msisdn"));
                    buddyVO.setPreferredAmount(rs.getLong("preferred_amount"));
                    buddyVO.setSuccessiveFailCount(rs.getLong("successive_failure_count"));
                    buddyVO.setSelectorCode(rs.getString("selector_code"));
                    listBuddyListDetails.add(buddyVO);

                }

            }
            if (checkData) {
                batchBuddyVO.setBuddyList(listBuddyListDetails);
                listBuddyBatchListDetails.add(batchBuddyVO);
            }

            return listBuddyBatchListDetails;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTxnDAO[loadBuddyDetailsByScheduleType]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTxnDAO[loadBuddyDetailsByScheduleType]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting listBuddyBatchListDetails.size:" + listBuddyBatchListDetails.size());
            }
        }// end of finally
    }
}
