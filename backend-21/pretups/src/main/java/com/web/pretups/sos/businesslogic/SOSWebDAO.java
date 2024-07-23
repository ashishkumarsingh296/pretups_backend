package com.web.pretups.sos.businesslogic;

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
import com.btsl.pretups.sos.businesslogic.LMBForceSetlVO;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;


public class SOSWebDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private static SOSWebQry sosWebQry;

    /**
     * 
     *
     */
    public SOSWebDAO() {
    	sosWebQry = (SOSWebQry) ObjectProducer.getObject(QueryConstants.SOS_WEB_QRY,QueryConstants.QUERY_PRODUCER);
    }

    /**
     * This method load the list of the sos transaction records having
     * AMBIGUOUS/UNDERPROCESS status.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadSOSReconciliationList(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode) throws BTSLBaseException {

        final String methodName = "loadSOSReconciliationList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_fromDate: ");
        	msg.append(p_fromDate);
        	msg.append(", p_toDate: ");
        	msg.append(p_toDate);      
        	msg.append(", p_networkCode: ");
        	msg.append(p_networkCode);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        SOSVO sosVO = null;
        final ArrayList sosTransactionVOList = new ArrayList();
        try {
        	pstmtSelect=sosWebQry.loadSOSReconciliationListQry(p_con, p_fromDate,p_toDate,p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                sosVO = new SOSVO();

                sosVO.setServiceType(rs.getString("service_type"));
                sosVO.setTransactionID(rs.getString("transaction_id"));
                sosVO.setRechargeDate(rs.getDate("recharge_date"));
                sosVO.setRechargeDateTime(rs.getTimestamp("recharge_date_time"));
                sosVO.setRechargeDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("recharge_date_time"))));
                sosVO.setNetworkCode(rs.getString("network_code"));
                sosVO.setProductName(rs.getString("short_name"));
                sosVO.setSubscriberMSISDN(rs.getString("subscriber_msisdn"));
                sosVO.setRechargeAmountStr(PretupsBL.getDisplayAmount(rs.getLong("sos_recharge_amount")));
                sosVO.setCreditAmountStr(PretupsBL.getDisplayAmount(rs.getLong("sos_credit_amount")));
                sosVO.setErrorCode(rs.getString("error_status"));
                sosVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                sosVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                sosVO.setReferenceID(rs.getString("reference_id"));
                sosVO.setDebitAmount(rs.getLong("sos_debit_amount"));
                sosVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                sosVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                sosVO.setReconciliationBy(rs.getString("reconciliation_by"));
                sosVO.setSettlementFlag(rs.getString("settlement_flag"));
                sosVO.setSettlementDate(rs.getDate("settlement_date"));
                sosVO.setSettlementReconFlag(rs.getString("settlement_recon_flag"));
                sosVO.setSettlementReconDate(rs.getDate("settlement_recon_date"));
                sosVO.setSettlementReconBy(rs.getString("settlement_recon_by"));
                sosVO.setCreatedOn(rs.getDate("created_on"));
                sosVO.setCreatedBy(rs.getString("created_by"));
                sosVO.setModifiedOn(rs.getDate("modified_on"));
                sosVO.setModifiedBy(rs.getString("modified_by"));
                sosVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                sosVO.setVersion(rs.getString("version"));
                sosVO.setCardGroupID(rs.getString("card_group_id"));
                sosVO.setCardGroupCode(rs.getString("card_group_code"));
                sosVO.setTransactionStatus(rs.getString("rechargestatus"));
                sosVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                sosVO.setPreviousBalanceStr(PretupsBL.getDisplayAmount(rs.getLong("previous_balance")));
                sosVO.setPostBalanceStr(PretupsBL.getDisplayAmount(rs.getLong("post_balance")));
                sosVO.setAccountStatus(rs.getString("account_status"));
                sosVO.setServiceClassCode(rs.getString("service_class_code"));
                sosVO.setErrorMessage(rs.getString("value"));

                sosTransactionVOList.add(sosVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[loadSOSReconciliationList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[loadSOSReconciliationList]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting sosTransactionVOList.size()=" + sosTransactionVOList.size());
            }
        }// end of finally

        return sosTransactionVOList;
    }

    /**
     * Method updateReconcilationStatus.
     * This method update the SOS reconciliation parameters.
     * 
     * @param p_con
     *            Connection
     * @param sosVO
     *            SOSVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateReconcilationStatus(Connection p_con, SOSVO p_sosVO) throws BTSLBaseException {
        final String methodName = "updateReconcilationStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_sosVO=" + p_sosVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            
            final String query = sosWebQry.updateReconcilationStatusQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }

            pstmtUpdate = p_con.prepareStatement(query);
            int i = 1;
            pstmtUpdate.setString(i++, p_sosVO.getTransactionStatus());
            pstmtUpdate.setString(i++, p_sosVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sosVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_sosVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sosVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_sosVO.getTransactionID());
            pstmtUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSWebDAO[updateReconcilationStatus]", "", "", "",
                    "Record is already modified Txn ID=" + p_sosVO.getTransactionID());
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[updateReconcilationStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[updateReconcilationStatus]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * This method load the list of the sos transaction records having
     * AMBIGUOUS/UNDERPROCESS status.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadSettlementReconciliationList(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode) throws BTSLBaseException {

        final String methodName = "loadSettlementReconciliationList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_fromDate: ");
        	msg.append(p_fromDate);
        	msg.append(", p_toDate: ");
        	msg.append(p_toDate);      
        	msg.append(",p_networkCode: ");
        	msg.append(p_networkCode);
        	
        	String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        SOSVO sosVO = null;
        final ArrayList sosTransactionVOList = new ArrayList();
        try {
        	pstmtSelect = sosWebQry. loadSettlementReconciliationListQry(p_con,p_fromDate, p_toDate, p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                sosVO = new SOSVO();

                sosVO.setServiceType(rs.getString("service_type"));
                sosVO.setTransactionID(rs.getString("transaction_id"));
                sosVO.setRechargeDate(rs.getDate("recharge_date"));
                sosVO.setRechargeDateTime(rs.getTimestamp("recharge_date_time"));
                sosVO.setRechargeDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("recharge_date_time"))));
                sosVO.setNetworkCode(rs.getString("network_code"));
                sosVO.setProductName(rs.getString("short_name"));
                sosVO.setSubscriberMSISDN(rs.getString("subscriber_msisdn"));
                sosVO.setRechargeAmountStr(PretupsBL.getDisplayAmount(rs.getLong("sos_recharge_amount")));
                sosVO.setErrorCode(rs.getString("error_status"));
                sosVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                sosVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                sosVO.setReferenceID(rs.getString("reference_id"));
                sosVO.setDebitAmountStr(PretupsBL.getDisplayAmount(rs.getLong("sos_debit_amount")));
                sosVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                sosVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                sosVO.setReconciliationBy(rs.getString("reconciliation_by"));
                sosVO.setSettlementFlag(rs.getString("settlement_flag"));
                sosVO.setSettlementDate(rs.getDate("settlement_date"));
                sosVO.setSettlementDateTimeStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("settlement_date"))));
                sosVO.setSettlementReconFlag(rs.getString("settlement_recon_flag"));
                sosVO.setSettlementReconDate(rs.getDate("settlement_recon_date"));
                sosVO.setSettlementReconBy(rs.getString("settlement_recon_by"));
                sosVO.setSettlementStatus(rs.getString("settlementstatus"));
                sosVO.setCreatedOn(rs.getDate("created_on"));
                sosVO.setCreatedBy(rs.getString("created_by"));
                sosVO.setModifiedOn(rs.getDate("modified_on"));
                sosVO.setModifiedBy(rs.getString("modified_by"));
                sosVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                sosVO.setVersion(rs.getString("version"));
                sosVO.setCardGroupID(rs.getString("card_group_id"));
                sosVO.setCardGroupCode(rs.getString("card_group_code"));
                sosVO.setTransactionStatus(rs.getString("settlementstatus"));
                sosVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                sosVO.setPreviousBalanceStr(PretupsBL.getDisplayAmount(rs.getLong("previous_balance")));
                sosVO.setPostBalanceStr(PretupsBL.getDisplayAmount(rs.getLong("post_balance")));
                sosVO.setAccountStatus(rs.getString("account_status"));
                sosVO.setServiceClassCode(rs.getString("service_class_code"));
                sosVO.setErrorMessage(rs.getString("value"));

                sosTransactionVOList.add(sosVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[loadSettlementReconciliationList]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[loadSettlementReconciliationList]", "", "",
                "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting sosTransactionVOList.size()=" + sosTransactionVOList.size());
            }
        }// end of finally

        return sosTransactionVOList;
    }

    /**
     * Method updateReconcilationStatus.
     * This method update the SOS reconciliation parameters.
     * 
     * @param p_con
     *            Connection
     * @param sosVO
     *            SOSVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSettlementReconcilationStatus(Connection p_con, SOSVO p_sosVO) throws BTSLBaseException {
        final String methodName = "updateSettlementReconcilationStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_sosVO=" + p_sosVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            final String query = sosWebQry.updateSettlementReconcilationStatusQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + query);
            }

            pstmtUpdate = p_con.prepareStatement(query);
            int i = 1;
            pstmtUpdate.setString(i++, p_sosVO.getSettlementStatus());
            pstmtUpdate.setString(i++, p_sosVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sosVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_sosVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_sosVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_sosVO.getSettlementFlag());
            pstmtUpdate.setString(i++, p_sosVO.getTransactionID());
            pstmtUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtUpdate.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SOSWebDAO[updateSettlementReconcilationStatus]", "",
                    "", "", "Record is already modified Txn ID=" + p_sosVO.getTransactionID());
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[updateSettlementReconcilationStatus]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[updateSettlementReconcilationStatus]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * This method load the list of the sos transaction records having
     * AMBIGUOUS/UNDERPROCESS status.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public ArrayList loadSOSTransferDetailsList(Connection p_con, Date p_fromDate, Date p_toDate, String p_msisdn, String p_transid, String p_networkCode) throws BTSLBaseException {

        final String methodName = "loadSOSTransferDetailsList";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  p_fromDate: ");
        	msg.append(p_fromDate);
        	msg.append(", p_toDate: ");
        	msg.append(p_toDate);      
        	msg.append(", p_msisdn: ");
        	msg.append(p_msisdn);        	
        	msg.append(", p_transid: ");
        	msg.append(p_transid);      
        	msg.append(", p_networkCode: ");
        	msg.append(p_networkCode);
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        SOSVO sosVO = null;
        final ArrayList sosTransactionVOList = new ArrayList();
        try {
        	pstmtSelect = sosWebQry.loadSOSTransferDetailsListQry(p_con, p_fromDate, p_toDate, p_networkCode, p_msisdn, p_transid);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                sosVO = new SOSVO();
                sosVO.setNetworkCode(p_networkCode);
                sosVO.setSubscriberMSISDN(p_msisdn);
                sosVO.setTransactionID(rs.getString("transaction_id"));
                sosVO.setRechargeDateTime(rs.getTimestamp("recharge_date_time"));
                sosVO.setRechargeDateStr(BTSLDateUtil.getLocaleDateTimeFromDate(rs.getTimestamp("recharge_date_time")));
                sosVO.setTransactionStatus(rs.getString("recharge_status"));
                sosVO.setSettlementDate(rs.getTimestamp("settlement_date"));
                sosVO.setSettlementDateTimeStr(BTSLDateUtil.getLocaleDateTimeFromDate(rs.getTimestamp("settlement_date")));
                sosVO.setRechargeAmountStr(PretupsBL.getDisplayAmount(rs.getLong("sos_recharge_amount")));
                sosVO.setSettlementStatus(rs.getString("settlement_status"));
                sosVO.setSubscriberType(rs.getString("subscriber_type"));
                sosVO.setPreviousBalance(rs.getLong("previous_balance"));
                sosVO.setPostBalance(rs.getLong("post_balance"));
                sosVO.setSettlementFlag(rs.getString("settlement_flag"));
                sosVO.setSettlementAmountStr(PretupsBL.getDisplayAmount(rs.getLong("sos_debit_amount")));
                sosVO.setErrorMessage(rs.getString("error_message"));
                // for ussd
                sosVO.setCellId(rs.getString("cell_id"));
                sosVO.setSwitchId(rs.getString("switch_id"));
                sosTransactionVOList.add(sosVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[loadSOSTransferDetailsList]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[loadSOSTransferDetailsList]", "", "", "",
                "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting sosTransactionVOList.size()=" + sosTransactionVOList.size());
            }
        }// end of finally

        return sosTransactionVOList;
    }

    /**
     * @param p_con
     * @param p_finaList
     * @param _errorLoggerList
     * @param p_userID
     * @param p_fileName
     * @throws BTSLBaseException
     * @author rahul.dutt
     */
    public int lmbForcedSettlement(Connection p_con, ArrayList p_finaList, Date p_currentdate, String p_userID, String p_fileName) throws BTSLBaseException {
        final String methodName = "lmbForcedSettlement";
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_msisdnList size: ");
        	msg.append(p_finaList.size());
        	msg.append(", p_currentdate: ");
        	msg.append(p_currentdate);      
        	msg.append(", p_userID: ");
        	msg.append(p_userID);        	
        	msg.append(", p_fileName: ");
        	msg.append(p_fileName);      
        	
        	String message=msg.toString();
            _log.debug(methodName,message);
        }
        // final list contains the records whose status need to be updated
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null, rsfail = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtSelectFail = null;
        final String transid = null;
        StringBuffer queryBuf = null;
        LMBForceSetlVO sosVO = null;
        String transaction_id, settlement_flag, settlement_status, failTransId;
        int updateCount = 0, insertCount = 0, processedRecCount = 0, updatedRecordCount = 0;
        try {
            String  selectQuery = sosWebQry.lmbForcedSettlementSelectQry();
            queryBuf = new StringBuffer();
            queryBuf.append(" UPDATE sos_transaction_details SET modified_on=?, modified_by=?,");
            queryBuf.append(" settlement_status=?, settlement_date=?, settlement_flag=?, settlement_error_code=?");
            queryBuf.append(" WHERE transaction_id=? ");
            final String updateQuery = queryBuf.toString();
            queryBuf = new StringBuffer();
            queryBuf.append(" INSERT into SOS_FORCE_SETTLEMENT_DETAILS values (?, ?, ?, ?, ?, ?, ?, ? ) ");
            final String insertQuery = queryBuf.toString();
            
            final String selectforFail = sosWebQry.lmbForcedSettlementQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "update query:" + updateQuery);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + insertQuery);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "selectforFail query:" + selectforFail);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtSelectFail = p_con.prepareStatement(selectforFail);
            int finaListSize = p_finaList.size();
            for (int n = 0; n < finaListSize; n++) {
                sosVO = (LMBForceSetlVO) p_finaList.get(n);

                transaction_id = "";
                int i = 0;
                try {
                    int j = 0;
                    pstmtSelect.setString(++j, sosVO.getLmbMsisdn());
                    pstmtSelect.setString(++j, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    pstmtSelect.setDate(++j, BTSLUtil.getSQLDateFromUtilDate(sosVO.getDate()));
                    rs = pstmtSelect.executeQuery();
                    if (rs.next()) {
                        // fetch the transaction id for update
                        transaction_id = rs.getString("transaction_id");
                        settlement_flag = rs.getString("settlement_flag");
                        settlement_status = rs.getString("settlement_status");
                        sosVO.setPrevStatus(settlement_status);
                        StringBuffer msg=new StringBuffer("");
                    	msg.append("Msisdn: ");
                    	msg.append(sosVO.getLmbMsisdn());
                    	msg.append(", Settle Date: ");
                    	msg.append(sosVO.getLmbRechargeDateStr());      
                    	msg.append(" Force status: ");
                    	msg.append(sosVO.getForceSettleStatus());        	
                    	msg.append(", transaction_id: ");
                    	msg.append(transaction_id);      
                    	
                    	msg.append(", settlement_flag: ");
                    	msg.append(settlement_flag);        	
                    	msg.append(", settlement_status: ");
                    	msg.append(settlement_status); 
                    	
                    	String message=msg.toString();
                        _log.debug(methodName, message);
                        // update the sos transaction
                        if (!sosVO.getForceSettleStatus().equals(settlement_status)) {
                            int k = 0;
                            if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(sosVO.getForceSettleStatus())) {
                                _log.debug(methodName, "pstmtUpdate " + PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                                // update sos transaction as success
                                pstmtUpdate.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                                pstmtUpdate.setString(++k, PretupsI.SYSTEM);
                                pstmtUpdate.setString(++k, sosVO.getForceSettleStatus());
                                pstmtUpdate.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                                pstmtUpdate.setString(++k, PretupsI.STATUS_ACTIVE);
                                if (sosVO.isDefaultSuccStatus()) {
                                    pstmtUpdate.setString(++k, PretupsErrorCodesI.FORCEFUL_SUCC_LMB_SETTLEMENT);
                                } else {
                                    pstmtUpdate.setString(++k, PretupsErrorCodesI.FORCEFUL_LMB_SUCCESS_SETTLEMENT_FOR_OPERATOR_CALL);
                                }
                                pstmtUpdate.setString(++k, transaction_id);
                            } else {
                                // check for latest lmb sucess transaction and
                                // update
                                k = 0;
                                pstmtSelectFail.setString(++k, sosVO.getLmbMsisdn());
                                pstmtSelectFail.setString(++k, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                                rsfail = pstmtSelectFail.executeQuery();
                                if (rsfail.next()) {
                                    failTransId = rs.getString("transaction_id");
                                    settlement_flag = rs.getString("settlement_flag");
                                    settlement_status = rs.getString("settlement_status");
                                    
                                    StringBuffer msg1=new StringBuffer("");
                                	msg1.append(" fail txn pstmtUpdate failTransId: ");
                                	msg1.append(failTransId);
                                	msg1.append(", settlement_flag: ");
                                	msg1.append(settlement_flag);        	
                                	msg1.append(", settlement_status: ");
                                	msg1.append(settlement_status); 
                                	
                                	String message1=msg1.toString();
                                    _log.debug(methodName,message1);
                                    if (failTransId.equals(transaction_id)) {
                                        k = 0;
                                        pstmtUpdate.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                                        pstmtUpdate.setString(++k, PretupsI.SYSTEM);
                                        pstmtUpdate.setString(++k, sosVO.getForceSettleStatus());
                                        pstmtUpdate.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                                        pstmtUpdate.setString(++k, PretupsI.STATUS_DELETE);
                                        pstmtUpdate.setString(++k, PretupsErrorCodesI.FORCEFUL_LMB_FAIL_SETTLEMENT_FOR_OPERATOR_CALL);
                                        pstmtUpdate.setString(++k, failTransId);
                                    } else {
                                    	StringBuffer msg2=new StringBuffer("");
                                    	msg2.append("Latest fail transaction for date: ");
                                    	msg2.append(sosVO.getLmbRechargeDateStr());
                                    	msg2.append(" is not latest LMB transaction for subscriber MSISDN: ");
                                    	msg2.append(sosVO.getLmbMsisdn());
                                    	
                                    	String message2=msg2.toString();
                                        _log.debug(methodName,message2);
                                        i = 0;
                                        pstmtInsert.setString(++i, sosVO.getLmbMsisdn());
                                        pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(sosVO.getDate()));
                                        pstmtInsert.setString(++i, p_fileName);
                                        pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                                        pstmtInsert.setString(++i, p_userID);
                                        pstmtInsert.setString(++i, PretupsErrorCodesI.FORCE_LMB_FAIL_NOT_LATEST);
                                        pstmtInsert.setString(++i, sosVO.getPrevStatus());
                                        pstmtInsert.setString(++i, sosVO.getForceSettleStatus());
                                        insertCount = pstmtInsert.executeUpdate();
                                        if (insertCount > 0) {
                                            p_con.commit();
                                            processedRecCount++;
                                            continue;
                                        }
                                    }
                                } else {
                                	
                                    _log.debug(methodName, "pstmtInsert " + PretupsErrorCodesI.NO_SUCCESS_LMB_FOUND_FOR_FOURCE_SETTLEMENT);
                                    i = 0;
                                    pstmtInsert.setString(++i, sosVO.getLmbMsisdn());
                                    pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(sosVO.getDate()));
                                    pstmtInsert.setString(++i, p_fileName);
                                    pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                                    pstmtInsert.setString(++i, p_userID);
                                    pstmtInsert.setString(++i, PretupsErrorCodesI.NO_SUCCESS_LMB_FOUND_FOR_FOURCE_SETTLEMENT);
                                    pstmtInsert.setString(++i, sosVO.getPrevStatus());
                                    pstmtInsert.setString(++i, sosVO.getForceSettleStatus());
                                    insertCount = pstmtInsert.executeUpdate();

                                    if (insertCount > 0) {
                                        p_con.commit();
                                        processedRecCount++;
                                        continue;
                                    }
                                }
                            }
                            updateCount = pstmtUpdate.executeUpdate();
                            if (updateCount > 0) {
                                _log.debug(methodName, "pstmtInsert " + PretupsErrorCodesI.NO_SUCCESS_LMB_FOUND_FOR_FOURCE_SETTLEMENT);
                                p_con.commit();
                                i = 0;
                                pstmtInsert.setString(++i, sosVO.getLmbMsisdn());
                                pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(sosVO.getDate()));
                                pstmtInsert.setString(++i, p_fileName);
                                pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                                pstmtInsert.setString(++i, p_userID);
                                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(sosVO.getForceSettleStatus())) {
                                    pstmtInsert.setString(++i, PretupsErrorCodesI.FORCEFUL_LMB_SUCCESS_SETTLEMENT_FOR_OPERATOR_CALL);
                                } else {
                                    pstmtInsert.setString(++i, PretupsErrorCodesI.FORCEFUL_LMB_FAIL_SETTLEMENT_FOR_OPERATOR_CALL);
                                }
                                pstmtInsert.setString(++i, sosVO.getPrevStatus());
                                pstmtInsert.setString(++i, sosVO.getForceSettleStatus());
                                insertCount = pstmtInsert.executeUpdate();
                                if (insertCount > 0) {
                                    p_con.commit();
                                    processedRecCount++;
                                    updatedRecordCount++;
                                    continue;
                                }
                            }
                            // insert into SOS_FORCE_SETTLEMENT_DETAILS
                        } else {
                            // insert into SOS_FORCE_SETTLEMENT_DETAILS as Force
                            // Settle status and status in DB are same
                            _log.debug(methodName, "pstmtInsert " + PretupsErrorCodesI.LMB_SETTLE_STATUS_SAME);
                            i = 0;
                            pstmtInsert.setString(++i, sosVO.getLmbMsisdn());
                            pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                            pstmtInsert.setString(++i, p_fileName);
                            pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                            pstmtInsert.setString(++i, p_userID);
                            pstmtInsert.setString(++i, PretupsErrorCodesI.LMB_SETTLE_STATUS_SAME);
                            pstmtInsert.setString(++i, sosVO.getPrevStatus());
                            pstmtInsert.setString(++i, sosVO.getForceSettleStatus());
                            insertCount = pstmtInsert.executeUpdate();
                            if (insertCount > 0) {
                                p_con.commit();
                                processedRecCount++;
                                continue;
                            }
                        }
                    } else {
                        // insert into SOS_FORCE_SETTLEMENT_DETAILS that status
                        // already settled
                        _log.debug(methodName, "pstmtInsert " + PretupsErrorCodesI.NO_SUCCESS_LMB_FOUND_FOR_FOURCE_SETTLEMENT);
                        i = 0;
                        pstmtInsert.setString(++i, sosVO.getLmbMsisdn());
                        pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                        pstmtInsert.setString(++i, p_fileName);
                        pstmtInsert.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_currentdate));
                        pstmtInsert.setString(++i, p_userID);
                        pstmtInsert.setString(++i, PretupsErrorCodesI.NO_SUCCESS_LMB_FOUND_FOR_FOURCE_SETTLEMENT);
                        pstmtInsert.setString(++i, sosVO.getPrevStatus());
                        pstmtInsert.setString(++i, sosVO.getForceSettleStatus());
                        insertCount = pstmtInsert.executeUpdate();
                        if (insertCount > 0) {
                            p_con.commit();
                            processedRecCount++;
                        }
                    }
                } catch (SQLException sqe) {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                    _log.error(methodName, "SQLException " + sqe.getMessage());
                    _log.errorTrace(methodName, sqe);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[lmbForcedSettlement]", "", "", "",
                        "SQLException:" + sqe.getMessage());
                }// end of catch
                catch (Exception ex) {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                    _log.error(methodName, "Exception : " + ex.getMessage());
                    _log.errorTrace(methodName, ex);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[lmbForcedSettlement]", "", "", "",
                        "Exception:" + ex.getMessage());
                }// end of catch
            }// end of for loop
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[lmbForcedSettlement]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSWebDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[lmbForcedSettlement]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSWebDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch

        finally {
            if (pstmtSelect != null) {
                try {
                    pstmtSelect.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (pstmtInsert != null) {
                try {
                    pstmtInsert.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (pstmtUpdate != null) {
                try {
                    pstmtUpdate.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (pstmtSelectFail != null) {
                try {
                    pstmtSelectFail.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (rsfail != null) {
                try {
                    rsfail.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
            	StringBuffer msg=new StringBuffer("");
            	msg.append("Exiting processedRecCount: ");
            	msg.append(processedRecCount);
            	msg.append(", updatedRecordCount: ");
            	msg.append(updatedRecordCount);
            	String message=msg.toString();
                _log.debug(methodName, message);
            }
        }// end of finally
        return processedRecCount;
    }

    /**
     * This method is for the LMB Bulk Upload Feature
     * 
     * @param p_con
     * @param p_finaList
     * @param p_currentdate
     * @param p_userID
     * @param p_fileName
     * @throws BTSLBaseException
     * @author ankuj.arora
     */
    public int lmbBlkUpload(Connection p_con, ArrayList p_finaList, Date p_currentdate, String p_userID, String p_fileName) throws BTSLBaseException {
        final boolean isSubscriberdebitrequired = true;
        final String methodName = "lmbBlkUpload";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_SOSVO=" + p_finaList.toString());
        }

        int addCount = 0;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtDelete = null;
        ResultSet selectRst = null;
        final SOSVO sosvo = null;
        final LMBForceSetlVO errVO = null;
        try {
            final StringBuffer queryStringSelect = new StringBuffer();
            final StringBuffer queryStringDelete = new StringBuffer();
            queryStringSelect.append("SELECT COUNT(1) C FROM SOS_SUBSCRIBER_SUMMARY WHERE MSISDN=? ");
            queryStringDelete.append("DELETE FROM SOS_SUBSCRIBER_SUMMARY WHERE MSISDN=?");
            final String insertQuery = sosWebQry.lmbBlkUploadQry();
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtSelect = p_con.prepareStatement(queryStringSelect.toString());
            pstmtDelete = p_con.prepareStatement(queryStringDelete.toString());
            LMBForceSetlVO sosVO = null;
            int i = 0;
            int finaListSize = p_finaList.size();
            for (int n = 0; n < finaListSize; n++) {
                sosVO = (LMBForceSetlVO) p_finaList.get(n);
                pstmtSelect.setString(1, sosVO.getLmbMsisdn());
                selectRst = pstmtSelect.executeQuery();
                while (selectRst.next()) {
                    if (selectRst.getInt("C") > 0) {
                        pstmtDelete.setString(1, sosVO.getLmbMsisdn());
                        pstmtDelete.executeUpdate();
                    }
                    pstmtInsert.setString(++i, sosVO.getLmbMsisdn());
                    pstmtInsert.setLong(++i, sosVO.getAmount());
                    pstmtInsert.setString(++i, PretupsI.LMB_VAL_NT_EXPIRED);
                    pstmtInsert.executeUpdate();
                    pstmtInsert.clearParameters();
                    i = 0;
                    addCount++;
                }
            }
        }
        /*
         * catch(BTSLBaseException be)
         * {
         * _log.error("lmbBlkUpload", "SQLException " + be.getMessage());
         * be.printStackTrace();
         * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,
         * EventStatusI
         * .RAISED,EventLevelI.FATAL,"SOSWebDAO[lmbBlkUpload]","","",
         * "","SQLException:"+be.getMessage());
         * if
         * (be.getMessage().equalsIgnoreCase(PretupsErrorCodesI.DUPLICATE_MSISDN
         * ))
         * throw new
         * BTSLBaseException("SOSWebDAO","lmbBlkUpload",PretupsErrorCodesI
         * .DUPLICATE_MSISDN);
         * else
         * throw new
         * BTSLBaseException("SOSWebDAO","lmbBlkUpload",PretupsErrorCodesI
         * .SOS_ERROR_EXCEPTION);
         * }//end of catch
         */catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[lmbBlkUpload]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSWebDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSWebDAO[lmbBlkUpload]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSWebDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (pstmtInsert != null) {
                try {
                    pstmtInsert.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (pstmtSelect != null) {
                try {
                    pstmtSelect.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (pstmtDelete != null) {
                try {
                    pstmtDelete.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with addCount=" + addCount);
            }
        }// end of finally
        return addCount;
    }
}
