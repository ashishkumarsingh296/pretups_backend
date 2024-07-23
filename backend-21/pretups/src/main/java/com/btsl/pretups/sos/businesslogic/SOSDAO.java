/**
 * 
 */
package com.btsl.pretups.sos.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.util.BTSLUtil;

/**
 * @author shamit.jain
 * 
 */
public class SOSDAO {

    private static final Log LOG = LogFactory.getLog(SOSDAO.class.getName());
    private SOSQry sosQry;

    /**
	 * 
	 *
	 */
    public SOSDAO() {
    	sosQry = (SOSQry)ObjectProducer.getObject(QueryConstants.SOS_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Method addSOSRechargeDetails.
     * This method is used to add the SOS transaction details in the database.
     * 
     * @param p_con
     *            Connection
     * @param p_transferVO
     *            TransferVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addSOSRechargeDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "addSOSRechargeDetails";
        LogFactory.printLog(methodName, "Entered p_transferVO:" + p_transferVO.toString(), LOG);
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            TransferItemVO senderTransferItemVO = (TransferItemVO) p_transferVO.getSenderTransferItemVO();
            int i = 1;
            StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO SOS_TRANSACTION_DETAILS(");
            insertQueryBuff.append(" TRANSACTION_ID, SUBSCRIBER_MSISDN, RECHARGE_DATE, RECHARGE_DATE_TIME, SOS_RECHARGE_AMOUNT,");
            insertQueryBuff.append(" SOS_CREDIT_AMOUNT, SOS_DEBIT_AMOUNT, SOS_RECHARGE_STATUS, ERROR_STATUS,");
            insertQueryBuff.append(" INTERFACE_RESPONSE_CODE, NETWORK_CODE, PRODUCT_CODE, REQUEST_GATEWAY_TYPE, REQUEST_GATEWAY_CODE,");
            insertQueryBuff.append(" SERVICE_TYPE, SUBSCRIBER_TYPE, REFERENCE_ID, CREATED_ON, CREATED_BY,");
            insertQueryBuff.append(" CARD_GROUP_SET_ID, VERSION, CARD_GROUP_ID, TAX1_TYPE, TAX1_RATE, TAX1_VALUE,");
            insertQueryBuff.append(" TAX2_TYPE, TAX2_RATE, TAX2_VALUE, PROCESS_FEE_TYPE, PROCESS_FEE_RATE, PROCESS_FEE_VALUE, CARD_GROUP_CODE,SUB_SERVICE, START_TIME, END_TIME,");
            insertQueryBuff.append(" TYPE,PREVIOUS_BALANCE, ACCOUNT_STATUS, SERVICE_CLASS_CODE,SERVICE_CLASS_ID");
            insertQueryBuff.append(", LAST_TRANSACTION_ID, LAST_RECHARGE_DATE_TIME");
            insertQueryBuff.append(", VALIDITY, BONUS_VALUE, GRACE_PERIOD, BONUS_VALIDITY, VALPERIOD_TYPE");
            insertQueryBuff.append(",CELL_ID,SWITCH_ID,INFO1,INFO2,INFO3,INFO4,INFO5");
            insertQueryBuff.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ) ");

            String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i++, BTSLUtil.NullToString(p_transferVO.getTransferID()));
            pstmtInsert.setString(i++, senderVO.getMsisdn());
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getTransferDate()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getTransferDateTime()));
            pstmtInsert.setLong(i++, p_transferVO.getQuantity());

            pstmtInsert.setLong(i++, p_transferVO.getSenderTransferValue());
            pstmtInsert.setLong(i++, p_transferVO.getSenderSettlementValue());
            pstmtInsert.setString(i++, p_transferVO.getTransferStatus());
            pstmtInsert.setString(i++, p_transferVO.getErrorCode());

            pstmtInsert.setString(i++, senderVO.getInterfaceResponseCode());
            pstmtInsert.setString(i++, senderVO.getNetworkCode());
            pstmtInsert.setString(i++, p_transferVO.getProductCode());
            pstmtInsert.setString(i++, p_transferVO.getRequestGatewayType());
            pstmtInsert.setString(i++, p_transferVO.getRequestGatewayCode());

            pstmtInsert.setString(i++, p_transferVO.getServiceType());
            pstmtInsert.setString(i++, senderVO.getSubscriberType());
            pstmtInsert.setString(i++, p_transferVO.getReferenceID());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_transferVO.getCreatedBy());

            pstmtInsert.setString(i++, p_transferVO.getCardGroupSetID());
            pstmtInsert.setString(i++, p_transferVO.getVersion());
            pstmtInsert.setString(i++, p_transferVO.getCardGroupID());
            pstmtInsert.setString(i++, p_transferVO.getSenderTax1Type());
            pstmtInsert.setDouble(i++, p_transferVO.getSenderTax1Rate());
            pstmtInsert.setLong(i++, p_transferVO.getSenderTax1Value());

            pstmtInsert.setString(i++, p_transferVO.getSenderTax2Type());
            pstmtInsert.setDouble(i++, p_transferVO.getSenderTax2Rate());
            pstmtInsert.setLong(i++, p_transferVO.getSenderTax2Value());
            pstmtInsert.setString(i++, p_transferVO.getSenderAccessFeeType());
            pstmtInsert.setDouble(i++, p_transferVO.getSenderAccessFeeRate());
            pstmtInsert.setLong(i++, p_transferVO.getSenderAccessFee());
            pstmtInsert.setString(i++, p_transferVO.getCardGroupCode());

            pstmtInsert.setString(i++, p_transferVO.getSubService());
            pstmtInsert.setLong(i++, p_transferVO.getRequestStartTime());
            pstmtInsert.setLong(i++, System.currentTimeMillis());

            String type = p_transferVO.getTransferCategory().substring(p_transferVO.getTransferCategory().indexOf("-") + 1);
            pstmtInsert.setString(i++, type);
            pstmtInsert.setLong(i++, senderTransferItemVO.getPreviousBalance());
            pstmtInsert.setString(i++, BTSLUtil.NullToString(senderTransferItemVO.getAccountStatus()));
            pstmtInsert.setString(i++, BTSLUtil.NullToString(senderTransferItemVO.getServiceClassCode()));
            pstmtInsert.setString(i++, BTSLUtil.NullToString(senderTransferItemVO.getServiceClass()));

            pstmtInsert.setString(i++, BTSLUtil.NullToString(p_transferVO.getLastTransferId()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getLastTransferDateTime()));

            pstmtInsert.setLong(i++, p_transferVO.getReceiverValidity());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverBonusValue());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverGracePeriod());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverBonusValidity());
            pstmtInsert.setString(i++, BTSLUtil.NullToString(p_transferVO.getReceiverValPeriodType()));
            // For USSD Changes
            pstmtInsert.setString(i++, p_transferVO.getCellId());
            pstmtInsert.setString(i++, p_transferVO.getSwitchId());
            //FOR INFO TAG SUPPORT
            pstmtInsert.setString(i++, p_transferVO.getInfo1());
            pstmtInsert.setString(i++, p_transferVO.getInfo2());
            pstmtInsert.setString(i++, p_transferVO.getInfo3());
            pstmtInsert.setString(i++, p_transferVO.getInfo4());
            pstmtInsert.setString(i, p_transferVO.getInfo5());

            addCount = pstmtInsert.executeUpdate();
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[addSOSRechargeDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[addSOSRechargeDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting addCount=" + addCount);
            }
        }// end of finally
    }

    /**
     * Method updateSOSRechargeDetails.
     * This method is used to update the final SOS transaction details in the
     * database.
     * 
     * @param p_con
     *            Connection
     * @param p_transferVO
     *            TransferVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSOSRechargeDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "updateSOSRechargeDetails";
        LogFactory.printLog(methodName, "Entered p_transferVO:" + p_transferVO.toString(), LOG);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            TransferItemVO senderTransferItemVO = (TransferItemVO) p_transferVO.getSenderTransferItemVO();
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer("UPDATE SOS_TRANSACTION_DETAILS SET");
            updateQueryBuff.append(" SOS_RECHARGE_STATUS=?, ERROR_STATUS=?,POST_BALANCE=?,");
            updateQueryBuff.append(" INTERFACE_RESPONSE_CODE=?, MODIFIED_ON=?, MODIFIED_BY=?, END_TIME=?,lmb_credit_update_status=? ");
            updateQueryBuff.append(" WHERE TRANSACTION_ID=?");

            String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "update query:" + updateQuery);
            }

            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setString(i++, p_transferVO.getTransferStatus());
            pstmtUpdate.setString(i++, p_transferVO.getErrorCode());
            pstmtUpdate.setLong(i++, senderTransferItemVO.getPostBalance());
            pstmtUpdate.setString(i++, senderVO.getInterfaceResponseCode());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_transferVO.getModifiedBy());
            pstmtUpdate.setLong(i++, System.currentTimeMillis());
            pstmtUpdate.setString(i++, p_transferVO.getLmbCreditUpdateStatus());
            pstmtUpdate.setString(i++, BTSLUtil.NullToString(p_transferVO.getTransferID()));

            updateCount = pstmtUpdate.executeUpdate();
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[updateSOSRechargeDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[updateSOSRechargeDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * This method is used to load the last sos transaction details.
     * 
     * @param p_con
     * @param p_senderMSISDN
     * @param p_senderNetworkCode
     * @return
     * @throws SOSVO
     */
    public SOSVO loadLastSOSRechargeByMsisdn(Connection p_con, String p_senderMSISDN, String p_senderNetworkCode) throws BTSLBaseException {
        final String methodName = "loadLastSOSRechargeByMsisdn";
        LogFactory.printLog(methodName, "Entered  p_senderMSISDN:" + p_senderMSISDN + ", p_senderNetworkCode: " + p_senderNetworkCode, LOG);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        SOSVO sosVO = null;
        try {
            String selectQuery = sosQry.loadLastSOSRechargeByMsisdnQry();
            LogFactory.printLog(methodName, "select query:" + selectQuery, LOG);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_senderMSISDN);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            // changes for ambiguous transaction in IN side
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            pstmtSelect.setString(i++, p_senderNetworkCode);

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                sosVO = new SOSVO();
                sosVO.setNetworkCode(p_senderNetworkCode);
                sosVO.setSubscriberMSISDN(p_senderMSISDN);
                sosVO.setTransactionID(rs.getString("transaction_id"));
                sosVO.setRechargeDateTime(rs.getTimestamp("recharge_date_time"));
                sosVO.setRechargeDate(rs.getTimestamp("recharge_date"));
                sosVO.setRechargeAmount(rs.getLong("sos_recharge_amount"));
                sosVO.setTransactionStatus(rs.getString("sos_recharge_status"));
                sosVO.setSettlementStatus(rs.getString("settlement_status"));
                sosVO.setSettlementFlag(rs.getString("settlement_flag"));
                sosVO.setErrorCode(rs.getString("error_status"));
                sosVO.setCreditAmountStr(rs.getString("sos_credit_amount"));
            }
        } catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[loadLastSOSRechargeByMsisdn]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[loadLastSOSRechargeByMsisdn]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LogFactory.printLog(methodName, "Exiting sosVO=" + sosVO, LOG);
        }
        return sosVO;
    }// end of loadSettlementReconciliationList()

    /*
     * Added by Ankuj Arora to allow the system
     * to pick the LMB amount of the subscriber
     * in the case of an LMB transfer
     * in case it has been uploaded in bulk
     * using the web interface
     */
    public boolean updateCreditAmtLMB(RequestVO p_RequestVO, Connection p_con) throws BTSLBaseException {
        StringBuffer querystring = new StringBuffer();
        StringBuffer querystringdelete = new StringBuffer();
        PreparedStatement ps = null;
        PreparedStatement psDelete = null;
        ResultSet rs = null;
        String reqmsgarray[] = null;
        boolean tracker = false;
        int count = 0;
        final String methodName = "updateCreditAmtLMB";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered:  p_RequestVO=" + p_RequestVO.toString());
        }
        try {
            querystring.append("SELECT BONUS_AMOUNT FROM SOS_SUBSCRIBER_SUMMARY WHERE MSISDN=?");
            ps = p_con.prepareStatement(querystring.toString());
            ps.setString(1, p_RequestVO.getFilteredMSISDN());
            rs = ps.executeQuery();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "select query:" + querystring.toString());
            }
            reqmsgarray = p_RequestVO.getRequestMessageArray();

            String temp_array[] = p_RequestVO.getDecryptedMessage().split(" ");
            while (rs.next()) {
                if (reqmsgarray.length == 2) {
                    temp_array[1] = rs.getString("BONUS_AMOUNT");
                    p_RequestVO.setDecryptedMessage(temp_array[0] + " " + temp_array[1]);
                    p_RequestVO.setRequestMessageArray(temp_array);
                }
                if (reqmsgarray.length == 1) {
                    temp_array[1] = rs.getString("BONUS_AMOUNT");
                    if (!(BTSLUtil.isNullString(temp_array[1]))) {
                        p_RequestVO.setDecryptedMessage(temp_array[0] + " " + temp_array[1]);
                        p_RequestVO.setRequestMessageArray(temp_array);
                    }
                }
                tracker = true;
            }
            if (tracker) {
                querystringdelete.append("DELETE FROM SOS_SUBSCRIBER_SUMMARY WHERE MSISDN=? ");
                psDelete = p_con.prepareStatement(querystringdelete.toString());
                psDelete.setString(1, p_RequestVO.getFilteredMSISDN());
                count = psDelete.executeUpdate();
                psDelete.clearParameters();
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException " + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[updateCreditAmtLMB]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex.getMessage());
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[updateCreditAmtLMB]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
            }
            try {
                if (psDelete != null) {
                    psDelete.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting with tracker=" + tracker + " " + "count" + count);
            }
        }// end of finally

        return tracker;
    }

    /**
     * This method will check all the required status from
     * SOS_SUBSCRIBER_SUMMARY table
     * 
     * @param p_con
     *            Connection
     * @param p_RequestVO
     *            RequestVO
     * @return boolean
     * @throws BTSLBaseException
     *             author ankuj.arora
     */
    public boolean validityChk(RequestVO p_RequestVO, Connection p_con) throws BTSLBaseException {
        StringBuffer querystring = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tracker = false;
        int count = 0;
        final String methodName = "validityChk";
        LogFactory.printLog(methodName, " Entered:  p_RequestVO=" + p_RequestVO.toString(), LOG);
        try {
            querystring.append("SELECT VALIDITY_EXPIRED FROM SOS_SUBSCRIBER_SUMMARY WHERE MSISDN=?");
            ps = p_con.prepareStatement(querystring.toString());
            ps.setString(1, p_RequestVO.getFilteredMSISDN());
            rs = ps.executeQuery();
            LogFactory.printLog(methodName, "select query:" + querystring.toString(), LOG);
            while (rs.next()) {
                String expiry = rs.getString("validity_expired");
                if (PretupsI.LMB_VAL_EXPIRED.equalsIgnoreCase(expiry)) {
                    tracker = true;
                }
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException " + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[validityChk]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SOSDAO", "updateCreditAmtLMB", PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex.getMessage());
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSDAO[validityChk]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SOSDAO", methodName, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    LOG.errorTrace(methodName, ex);
                }
            }
            LogFactory.printLog(methodName, "Exiting with tracker=" + tracker + " " + "count" + count, LOG);
        }// end of finally

        return tracker;
    }
	public SOSVO loadSOSDetails(Connection p_con,Date p_Date,String p_msisdn) throws BTSLBaseException
	{
		final String methodName = "loadSOSDetails";
		LogFactory.printLog(methodName, " Entered:  p_beingProcessedDate="+p_Date+"p_msisdn"+p_msisdn, LOG);
		String 	selectQuery = sosQry.loadSOSDetailsQry();
		LogFactory.printLog(methodName, "select query:" + selectQuery+" BTSLUtil.getSQLDateFromUtilDate(p_Date)"+BTSLUtil.getSQLDateFromUtilDate(p_Date), LOG);
		PreparedStatement selectPstmt=null;
		ResultSet selectRst = null;
		SOSVO sosvo=null;
		try
		{
			selectPstmt=p_con.prepareStatement(selectQuery);//,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			int i=0;
			selectPstmt.setString(++i,p_msisdn);
			selectPstmt.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(p_Date));
			selectRst=selectPstmt.executeQuery();
			if(selectRst.next())
			{
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
		}		
		catch(SQLException sqe)
		{
			LOG.error(methodName, "SQLException " + sqe.getMessage());
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SOSDAO[loadSOSDetails]","","","","SQLException:"+sqe.getMessage());
			throw new BTSLBaseException("SOSDAO",methodName,PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
		}//end of catch
		catch(Exception ex)
		{
			LOG.error(methodName, "Exception : " + ex.getMessage());
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"SOSDAO[loadSOSDetails]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException("SOSDAO",methodName,PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
		}//end of catch
		finally
		{
			if(selectRst!=null)
				try {selectRst.close();} catch (Exception ex) {LOG.errorTrace(methodName, ex);}	
				if(selectPstmt!=null)
					try {selectPstmt.close();} catch (Exception ex) {LOG.errorTrace(methodName, ex);}	
					LogFactory.printLog(methodName,  " Exiting with SOSsettlementData="+sosvo, LOG);
					if(sosvo!=null)
					{
						LogFactory.printLog(methodName,  " Exiting with sosvo ="+sosvo, LOG);
					}
		}//end of finally
		return sosvo;
	}		
}