package com.txn.pretups.sos.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class SOSTxnDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private SOSTxnQry sosTxnQry;
    /**
	 * 
	 *
	 */
    public SOSTxnDAO() {
    	sosTxnQry = (SOSTxnQry)ObjectProducer.getObject(QueryConstants.SOS_TXN_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Method to update the SOS settlement details
     * 
     * @param p_con
     * @param p_buddyVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSettelementDetails(Connection p_con, SOSVO p_sosVO) throws BTSLBaseException {
        final String methodName = "updateSettelementDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_sosVO :" + p_sosVO);
        }
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdate1 = null;
        PreparedStatement pstmtUpdate2 = null;
        int updateCount = 0;
        try {
            if (!BTSLUtil.isNullString(p_sosVO.getErrorCode()) && PretupsErrorCodesI.SOS_REQ_ALREADY_PROCESSED.equals(p_sosVO.getErrorCode())) {
                StringBuilder sbf = new StringBuilder(" UPDATE sos_transaction_details set REPTED_SETLMNT_ERROR_CODE=?,REPTED_SETLMNT_LMB_AMTAT_IN=? ,SETTLEMENT_FLAG=? ,SETTLEMENT_STATUS =?, SETTLEMENT_SERVICE_TYPE=? ");
                sbf.append(" WHERE transaction_id=? AND  subscriber_msisdn=? ");
                String updateQuery = sbf.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + updateQuery);
                }
                int i = 1;

                pstmtUpdate = p_con.prepareStatement(updateQuery);
                pstmtUpdate.setString(i++, p_sosVO.getErrorCode());
                pstmtUpdate.setDouble(i++, p_sosVO.getLmbAmountAtIN());
                pstmtUpdate.setString(i++, PretupsI.YES);
                pstmtUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                pstmtUpdate.setString(i++, p_sosVO.getSettlmntServiceType());
                pstmtUpdate.setString(i++, p_sosVO.getTransactionID());
                pstmtUpdate.setString(i++, p_sosVO.getSubscriberMSISDN());
                updateCount = pstmtUpdate.executeUpdate();

            } else if (!BTSLUtil.isNullString(p_sosVO.getErrorCode()) && PretupsErrorCodesI.FORCEFUL_LMB_SUCCESS_SETTLEMENT_FOR_OPERATOR_CALL.equals(p_sosVO.getErrorCode())) {
                StringBuilder sbf = new StringBuilder(" UPDATE sos_transaction_details set SETTLEMENT_ERROR_CODE=? ,SETTLEMENT_FLAG=? ,SETTLEMENT_STATUS =?,settlement_date=?, SETTLEMENT_SERVICE_TYPE=? ");
                sbf.append(" WHERE transaction_id=? AND  subscriber_msisdn=?  ");
                String updateQuery = sbf.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + updateQuery);
                }
                int i = 1;

                pstmtUpdate1 = p_con.prepareStatement(updateQuery);
                pstmtUpdate1.setString(i++, p_sosVO.getErrorCode());
                pstmtUpdate1.setString(i++, PretupsI.YES);
                pstmtUpdate1.setString(i++, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                pstmtUpdate1.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sosVO.getCreatedOn()));
                pstmtUpdate1.setString(i++, p_sosVO.getSettlmntServiceType());
                pstmtUpdate1.setString(i++, p_sosVO.getTransactionID());
                pstmtUpdate1.setString(i++, p_sosVO.getSubscriberMSISDN());
                updateCount = pstmtUpdate1.executeUpdate();
            } else {
                // StringBuilder sbf = new
                
                StringBuilder sbf = new StringBuilder(" UPDATE sos_transaction_details SET settlement_error_code=?,interface_response_code=?, ");
                if (PretupsI.TXN_STATUS_SUCCESS.equalsIgnoreCase(p_sosVO.getTransactionStatus()) || PretupsI.TXN_STATUS_AMBIGUOUS.equalsIgnoreCase(p_sosVO.getTransactionStatus())) {
                    sbf.append(" settlement_flag='Y', ");
                }
                // in case if transaction during settlement becomes ambiguous
                // which was marked failed earlier during settlement
                // reconciliation
                // then in this case update the settlement_reconciliation flag
                // as 'N'
                if (PretupsI.TXN_STATUS_AMBIGUOUS.equalsIgnoreCase(p_sosVO.getTransactionStatus())) {
                    sbf.append(" settlement_recon_flag='N', ");
                }
                if (!BTSLUtil.isNullString(p_sosVO.getTransactionStatus())) {
                    sbf.append(" settlement_status=?, ");
                }
                sbf.append("settlement_date=?, ");
                
                sbf.append(" settlement_previous_balance=?,lmb_debit_update_status=? ");
                sbf.append(" ,lmb_amtat_in=? , SETTLEMENT_SERVICE_TYPE=?");
                sbf.append(" WHERE transaction_id=? AND  subscriber_msisdn=? ");

                String updateQuery = sbf.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + updateQuery);
                }

                int i = 1;
                pstmtUpdate2 = p_con.prepareStatement(updateQuery);
                pstmtUpdate2.setString(i++, p_sosVO.getErrorCode());
                pstmtUpdate2.setString(i++, p_sosVO.getInterfaceResponseCode());
                if (!BTSLUtil.isNullString(p_sosVO.getTransactionStatus())) {
                    pstmtUpdate2.setString(i++, p_sosVO.getTransactionStatus());
                }
                pstmtUpdate2.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sosVO.getCreatedOn()));
              
                pstmtUpdate2.setLong(i++, p_sosVO.getPreviousBalance());
             
                pstmtUpdate2.setString(i++, p_sosVO.getLmbUpdateStatus());
                pstmtUpdate2.setDouble(i++, p_sosVO.getLmbAmountAtIN());
                pstmtUpdate2.setString(i++, p_sosVO.getSettlmntServiceType());
                pstmtUpdate2.setString(i++, p_sosVO.getTransactionID());
                pstmtUpdate2.setString(i++, p_sosVO.getSubscriberMSISDN());
                updateCount = pstmtUpdate2.executeUpdate();
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[updateSettelementDetails]", p_sosVO.getTransactionID(), p_sosVO.getSubscriberMSISDN(), "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[updateSettelementDetails]", p_sosVO.getTransactionID(), p_sosVO.getSubscriberMSISDN(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate1 != null) {
                    pstmtUpdate1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate2 != null) {
                    pstmtUpdate2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * 
     * @param p_con
     * @param p_sosvo
     * @return
     * @throws BTSLBaseException
     */
    public boolean isSubscriberdebitrequired(Connection p_con, SOSVO p_sosvo) throws BTSLBaseException {
        boolean isSubscriberdebitrequired = true;
        final String methodName = "isSubscriberdebitrequired";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_SOSVO=" + p_sosvo.toString());
        }
        StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(" select settlement_flag ");
        queryBuf.append(" from sos_transaction_details ");
        queryBuf.append(" where transaction_id=? and subscriber_msisdn=? and ");
        queryBuf.append(" sos_recharge_status='200' and network_code=? ");
        String selectQuery = queryBuf.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "select query:" + selectQuery);
        }

        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        try {
            selectPstmt = p_con.prepareStatement(selectQuery);
            int i = 0;
            selectPstmt.setString(++i, p_sosvo.getTransactionID());
            selectPstmt.setString(++i, p_sosvo.getSubscriberMSISDN());
            selectPstmt.setString(++i, p_sosvo.getNetworkCode());
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                if (selectRst.getString("settlement_flag").equalsIgnoreCase(PretupsI.YES)) {
                    isSubscriberdebitrequired = false;
                }
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[isSubscriberdebitrequired]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[isSubscriberdebitrequired]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }// end of finally
        return isSubscriberdebitrequired;
    }

    /**
     * This method will fetch all the required data from USERS table
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @param p_dirPath
     *            String
     * @param p_fileName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_fileEXT
     *            String
     * @param p_maxFileLength
     *            long
     * @return void
     * @throws sqlException
     *             ,Exception
     */
    public ArrayList loadSOSSettlementList(Connection p_con, Date p_beingProcessedDate) throws BTSLBaseException {
        ArrayList<SOSVO> SOSsettlementData = null;
        final String methodName = "loadSOSSettlementList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_beingProcessedDate=" + p_beingProcessedDate);
        }

        sosTxnQry = (SOSTxnQry)ObjectProducer.getObject(QueryConstants.SOS_TXN_QRY, QueryConstants.QUERY_PRODUCER);
        String selectQuery = sosTxnQry.loadSOSSettlementListQry();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "select query:" + selectQuery + " BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate)" + BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
        }

        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        try {
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            _log.debug(methodName, "selectRst.getFetchSize()=" + selectRst.getFetchSize());
            int i = 0;
            i = i + selectRst.getFetchSize();
            if (i > 0) {
                SOSsettlementData = new ArrayList<SOSVO>(i);
            }

            _log.debug(methodName, "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            SOSVO sosvo = null;
            while (selectRst.next()) {
                sosvo = new SOSVO();
                sosvo.setRequestIDStr(selectRst.getString("transaction_id"));
                sosvo.setNetworkCode(selectRst.getString("network_code"));
                sosvo.setType(selectRst.getString("type"));
                sosvo.setServiceType(selectRst.getString("service_type"));
                sosvo.setSubscriberMSISDN(selectRst.getString("subscriber_msisdn"));
                sosvo.setSubscriberType(selectRst.getString("subscriber_type"));
                sosvo.setDebitAmount(selectRst.getLong("sos_debit_amount"));
                sosvo.setCreditAmount(selectRst.getLong("sos_credit_amount"));
                sosvo.setRechargeAmount(selectRst.getLong("sos_recharge_amount"));
                sosvo.setRequestGatewayType(selectRst.getString("request_gateway_type"));
                sosvo.setRequestGatewayCode(selectRst.getString("request_gateway_code"));
                SOSsettlementData.add(sosvo);
            }
            // method call to write data in files

            _log.debug(methodName, "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSSettlementList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSSettlementList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with SOSsettlementData=" + SOSsettlementData);
            }
            if (SOSsettlementData != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exiting with SOSsettlementData size=" + SOSsettlementData.size());
                }
            }
        }// end of finally
        return SOSsettlementData;
    }

    /**
     * This method will fetch all the required data from USERS table
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @param p_dirPath
     *            String
     * @param p_fileName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_fileEXT
     *            String
     * @param p_maxFileLength
     *            long
     * @return void
     * @throws sqlException
     *             ,Exception
     */
    public ArrayList loadSOSReconSettlementList(Connection p_con, Date p_beingProcessedDate) throws BTSLBaseException {
        ArrayList<SOSVO> SOSsettlementData = null;
        final String methodName = "loadSOSReconSettlementList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_beingProcessedDate=" + p_beingProcessedDate);
        }

        String selectQuery = sosTxnQry.loadSOSReconSettlementListQry();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "select query:" + selectQuery + " BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate)" + BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
        }

        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
		String sosSettleDays = null;
		String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        try {
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
			if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
			sosSettleDays = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue());
            selectPstmt.setString(2, sosSettleDays);
			}
            else
			selectPstmt.setInt(2, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue());
			selectRst = selectPstmt.executeQuery();
            _log.debug(methodName, "selectRst.getFetchSize()=" + selectRst.getFetchSize());
            int i = 0;
            i = i + selectRst.getFetchSize();

            if (i > 0) {
                SOSsettlementData = new ArrayList<SOSVO>(i);
            }

            _log.debug(methodName, "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            SOSVO sosvo = null;
            while (selectRst.next()) {
                sosvo = new SOSVO();
                sosvo.setRequestIDStr(selectRst.getString("transaction_id"));
                sosvo.setNetworkCode(selectRst.getString("network_code"));
                sosvo.setType(selectRst.getString("type"));
                sosvo.setServiceType(selectRst.getString("service_type"));
                sosvo.setSubscriberMSISDN(selectRst.getString("subscriber_msisdn"));
                sosvo.setSubscriberType(selectRst.getString("subscriber_type"));
                sosvo.setDebitAmount(selectRst.getLong("sos_debit_amount"));
                sosvo.setCreditAmount(selectRst.getLong("sos_credit_amount"));
                sosvo.setRechargeAmount(selectRst.getLong("sos_recharge_amount"));
                SOSsettlementData.add(sosvo);
            }
            // method call to write data in files

            _log.debug(methodName, "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSReconSettlementList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSReconSettlementList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting SOSsettlementData" + SOSsettlementData);
            }
            if (SOSsettlementData != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "SOSsettlementData with size" + SOSsettlementData.size());
                }
            }
        }// end of finally
        return SOSsettlementData;
    }

    /**
     * This method inserts the record in SOS_SETTLEMENT_FAIL_RECCORDS table for
     * all the bad recharges during
     * settlement process.
     * 
     * @param p_con
     * @param p_sosVO
     * @return int
     * @throws BTSLBaseException
     */
    public int insertBadLendMeBalanceRechargesDuringSettlement(Connection p_con, SOSVO p_sosVO) throws BTSLBaseException {

        final String methodName = "insertBadLendMeBalanceRechargesDuringSettlement";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  p_sosVO:" + p_sosVO);
        }
        PreparedStatement pstmtInsert = null;
        int insertCount = 0;
        Date currentDate = new Date();
        try {
            StringBuilder insertQueryBuff = new StringBuilder();
            insertQueryBuff.append("Insert into SOS_SETTLEMENT_FAIL_RECCORDS(TRANSACTION_ID, SETTLEMENT_STATUS, SETTLEMENT_DATE, ERROR_STATUS) values (");
            insertQueryBuff.append("?,?,?,?)");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            int i = 1;
            pstmtInsert.setString(i++, p_sosVO.getTransactionID());
            pstmtInsert.setString(i++, p_sosVO.getTransactionStatus());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(currentDate));
            pstmtInsert.setString(i++, p_sosVO.getErrorCode());
            insertCount = pstmtInsert.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[insertBadLendMeBalanceRechargesDuringSettlement]", "", "", "", "SQL Exception:" + sqle.getMessage());
            // throw new BTSLBaseException(this,
            // "insertBadLendMeBalanceRechargesDuringSettlement",

        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[insertBadLendMeBalanceRechargesDuringSettlement]", "", "", "", "Exception:" + e.getMessage());
            // throw new BTSLBaseException(this,
            // "insertBadLendMeBalanceRechargesDuringSettlement",

        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting insertCount=" + insertCount);
            }
        }// end of finally

        return insertCount;
    }

    /**
     * Method to update the SOS settlement details through recharge
     * 
     * @param p_con
     * @param p_sosVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSOSSettlementThroughRecharge(Connection p_con, SOSVO p_sosVO) throws BTSLBaseException {
        final String methodName = "updateSOSSettlementThroughRecharge";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_sosVO :" + p_sosVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            if (!BTSLUtil.isNullString(p_sosVO.getErrorCode()) && PretupsErrorCodesI.SOS_REQ_ALREADY_PROCESSED.equals(p_sosVO.getErrorCode())) {
                StringBuilder sbf = new StringBuilder(" UPDATE sos_transaction_details set REPTED_SETLMNT_ERROR_CODE=?,REPTED_SETLMNT_LMB_AMTAT_IN=?, SETTLEMENT_FLAG=? ,SETTLEMENT_STATUS =?,SETTLEMENT_SERVICE_TYPE=? ");
                sbf.append(" WHERE transaction_id=? AND  subscriber_msisdn=? ");
                String updateQuery = sbf.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug("updateSettelementDetails", "select query:" + updateQuery);
                }
                int i = 1;

                pstmtUpdate = p_con.prepareStatement(updateQuery);
                pstmtUpdate.setString(i++, p_sosVO.getErrorCode());
                pstmtUpdate.setDouble(i++, p_sosVO.getLmbAmountAtIN());
                pstmtUpdate.setString(i++, PretupsI.YES);
                pstmtUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                pstmtUpdate.setString(i++, p_sosVO.getSettlmntServiceType());
                pstmtUpdate.setString(i++, p_sosVO.getTransactionID());
                pstmtUpdate.setString(i++, p_sosVO.getSubscriberMSISDN());
                updateCount = pstmtUpdate.executeUpdate();

            } else {
                StringBuilder sbf = new StringBuilder(" UPDATE sos_transaction_details SET settlement_error_code=?,interface_response_code=?, ");
                if (PretupsI.TXN_STATUS_SUCCESS.equalsIgnoreCase(p_sosVO.getTransactionStatus()) || PretupsI.TXN_STATUS_AMBIGUOUS.equalsIgnoreCase(p_sosVO.getTransactionStatus())) {
                    sbf.append(" settlement_flag='Y', ");
                }
                if (PretupsI.TXN_STATUS_AMBIGUOUS.equalsIgnoreCase(p_sosVO.getTransactionStatus())) {
                    sbf.append(" settlement_recon_flag='N', ");
                }
                if (!BTSLUtil.isNullString(p_sosVO.getTransactionStatus())) {
                    sbf.append(" settlement_status=?, ");
                }
                sbf.append("settlement_date=?,settlement_previous_balance=?, ");
                sbf.append("lmb_amtat_in=?, lmb_debit_update_status=? ,SETTLEMENT_SERVICE_TYPE=?");
                sbf.append(" WHERE transaction_id=? AND  subscriber_msisdn=? ");

                String updateQuery = sbf.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + updateQuery);
                }

                int i = 1;

                pstmtUpdate = p_con.prepareStatement(updateQuery);
                pstmtUpdate.setString(i++, p_sosVO.getErrorCode());
                pstmtUpdate.setString(i++, p_sosVO.getInterfaceResponseCode());
                if (!BTSLUtil.isNullString(p_sosVO.getTransactionStatus())) {
                    pstmtUpdate.setString(i++, p_sosVO.getTransactionStatus());
                }
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sosVO.getCreatedOn()));
                pstmtUpdate.setLong(i++, p_sosVO.getPreviousBalance());
                pstmtUpdate.setDouble(i++, p_sosVO.getLmbAmountAtIN());
                pstmtUpdate.setString(i++, p_sosVO.getLmbUpdateStatus());
                pstmtUpdate.setString(i++, p_sosVO.getSettlmntServiceType());
                pstmtUpdate.setString(i++, p_sosVO.getTransactionID());
                pstmtUpdate.setString(i++, p_sosVO.getSubscriberMSISDN());
                updateCount = pstmtUpdate.executeUpdate();


                if ((!BTSLUtil.isNullString(p_sosVO.getTransactionStatus())) && (((p_sosVO.getTransactionStatus()).equals(PretupsErrorCodesI.TXN_STATUS_FAIL)))) {
                    int insertCount = 0;
                    insertCount = insertBadLendMeBalanceRechargesDuringSettlement(p_con, p_sosVO);
                    if (insertCount <= 0) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[updateSOSSettlementThroughRecharge]", p_sosVO.getTransactionID(), p_sosVO.getSubscriberMSISDN(), "Getting exceptin while inserting record in SOS_SETTLEMENT_FAIL_RECCORDS with transaction status :" + p_sosVO.getTransactionStatus(), " Error code :" + p_sosVO.getErrorCode());
                    }
                }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[updateSOSSettlementThroughRecharge]", p_sosVO.getTransactionID(), p_sosVO.getSubscriberMSISDN(), "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[updateSOSSettlementThroughRecharge]", p_sosVO.getTransactionID(), p_sosVO.getSubscriberMSISDN(), "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount:" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * 
     * @param p_con
     * @param p_Date
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     */
    public SOSVO loadSOSDetails(Connection p_con, Date p_Date, String p_msisdn) throws BTSLBaseException {

        final String methodName = "loadSOSDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_beingProcessedDate=" + p_Date + "p_msisdn" + p_msisdn);
        }

        String selectQuery = sosTxnQry.loadSOSDetailsQry();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "select query:" + selectQuery + " BTSLUtil.getSQLDateFromUtilDate(p_Date)" + BTSLUtil.getSQLDateFromUtilDate(p_Date));
        }

        PreparedStatement selectPstmt = null;

        ResultSet selectRst = null;
        SOSVO sosvo = null;

        try {
            selectPstmt = p_con.prepareStatement(selectQuery);
            int i = 0;
            selectPstmt.setString(++i, p_msisdn);
            selectPstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_Date));
            selectRst = selectPstmt.executeQuery();

            if (selectRst.next()) {
                sosvo = new SOSVO();
                sosvo.setTransactionID(selectRst.getString("transaction_id"));
                sosvo.setNetworkCode(selectRst.getString("network_code"));
                sosvo.setType(selectRst.getString("type"));
                sosvo.setServiceType(selectRst.getString("service_type"));
                sosvo.setSubscriberMSISDN(selectRst.getString("subscriber_msisdn"));
                sosvo.setSubscriberType(selectRst.getString("subscriber_type"));
                sosvo.setDebitAmount(selectRst.getLong("sos_debit_amount"));
                sosvo.setCreditAmount(selectRst.getLong("sos_credit_amount"));
                sosvo.setRechargeAmount(selectRst.getLong("sos_recharge_amount"));
                sosvo.setRequestGatewayType(selectRst.getString("request_gateway_type"));
                sosvo.setRequestGatewayCode(selectRst.getString("request_gateway_code"));

            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSDetails]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with SOSsettlementData=" + sosvo);
            }
            if (sosvo != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exiting with sosvo =" + sosvo);
                }
            }
        }// end of finally
        return sosvo;
    }

    /**
     * This method will fetch all the required data from SOS_SUBSCRIBER_SUMMARY
     * table
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSOSValidityChkList(Connection p_con, Date p_beingProcessedDate) throws BTSLBaseException {
        ArrayList<SOSVO> sosValidityChkData = null;
        final String methodName = "loadSOSValidityChkList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_beingProcessedDate=" + p_beingProcessedDate);
        }
        String selectQuery = sosTxnQry.loadSOSValidityChkListQry();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "select query:" + selectQuery + " BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate)" + BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
        }

        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        try {
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_beingProcessedDate, -((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_MIN_VALIDITY_DAYS))).intValue())));
            selectRst = selectPstmt.executeQuery();

            _log.debug(methodName, "selectRst.getFetchSize()=" + selectRst.getFetchSize());
            int i = 0;
            i = i + selectRst.getFetchSize();

            if (i > 0) {
                sosValidityChkData = new ArrayList<SOSVO>(i);
            }

            _log.debug(methodName, "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            SOSVO sosvo = null;
            while (selectRst.next()) {
                sosvo = new SOSVO();
                sosvo.setSubscriberMSISDN(selectRst.getString("msisdn"));
                sosvo.setRechargeAmount(selectRst.getLong("bonus_amount"));
                sosvo.setCreatedOn(selectRst.getDate("created_on"));
                String s = selectRst.getString("validity_expired");
                sosValidityChkData.add(sosvo);
            }
            // method call to write data in files
            
            _log.debug(methodName, "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSValiditySettleList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[loadSOSValidityChkList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with SOSsettlementData=" + sosValidityChkData);
            }
            if (sosValidityChkData != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exiting with SOSsettlementData size=" + sosValidityChkData.size());
                }
            }
        }// end of finally
        return sosValidityChkData;
    }

    /**
     * This method will update all the required status from
     * SOS_SUBSCRIBER_SUMMARY table
     * 
     * @param p_con
     *            Connection
     * @param p_Sosvo
     *            SOSVO
     * @return void
     * @throws BTSLBaseException
     *             author ankuj.arora
     */

    public void updateValidityStatus(Connection p_con, SOSVO p_Sosvo) throws BTSLBaseException {
        PreparedStatement ps = null;
        int count = 0;
        final String methodName = "updateValidityStatus";
        try {
            StringBuilder updateQuery = new StringBuilder();
            updateQuery.append("UPDATE SOS_SUBSCRIBER_SUMMARY SET VALIDITY_EXPIRED=? WHERE MSISDN=?");
            ps = p_con.prepareStatement(updateQuery.toString());
            ps.setString(1, PretupsI.STATUS_ACTIVE);
            ps.setString(2, p_Sosvo.getSubscriberMSISDN());
            count = ps.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[updateValidityStatus]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSTxnDAO[updateValidityStatus]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSTxnDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with count=" + count);
            }
        }// end of finally

    }
}
