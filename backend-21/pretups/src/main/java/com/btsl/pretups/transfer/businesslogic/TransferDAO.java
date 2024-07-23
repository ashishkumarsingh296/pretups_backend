package com.btsl.pretups.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

/**
 * TransferDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 28/06/2005 Initial Creation
 * Sandeep Goel Sep 18, 2006 Modification,Customization ID REC001
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Transfer data access object class for interaction with the database
 */
/**
 */
public class TransferDAO {

    /**
     * Field LOG.
     */
    private final Log LOG = LogFactory.getLog(TransferDAO.class.getName());
    
    private String exception = " Exception : ";
    private String sqlException = " SQL Exception : ";
    private String errorWhileProcessing = "error.general.processing";

    /**
     * Methods adds the transfers related details, calls another method to
     * insert the items
     * 
     * @param p_con
     * @param p_transferVO
     * @return int
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public int addTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "addTransferDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferVO:" + p_transferVO.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            int i = 1;
            final StringBuffer insertQueryBuff = new StringBuffer(
                            " INSERT INTO subscriber_transfers(transfer_id, transfer_date, transfer_date_time, network_code, sender_id, ");
            insertQueryBuff.append(" product_code, sender_msisdn, receiver_msisdn, receiver_network_code, transfer_value, ");
            insertQueryBuff.append(" error_code, request_gateway_type, request_gateway_code,reference_id, payment_method_type, ");
            insertQueryBuff.append(" service_type, pin_sent_to_msisdn, language, country, skey, skey_generation_time, ");
            insertQueryBuff.append(" skey_sent_to_msisdn, request_through_queue, credit_back_status, quantity, reconciliation_flag, ");
            insertQueryBuff.append(" reconciliation_date, reconciliation_by, created_by, created_on, modified_by, modified_on, transfer_status, ");
            insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_access_fee,sender_tax1_type,sender_tax1_rate,sender_tax1_value, ");
            insertQueryBuff.append(" sender_tax2_type,sender_tax2_rate,sender_tax2_value,sender_transfer_value, ");
            insertQueryBuff.append(" receiver_access_fee,receiver_tax1_type,receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
            insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
            insertQueryBuff.append(" card_group_code,receiver_valperiod_type,sub_service,start_time,end_time,transfer_category");
            insertQueryBuff.append(",cell_id,switch_id,VOUCHER_SERIAL_NUMBER,INFO1,INFO2,INFO3,INFO4,INFO5)");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
            insertQueryBuff.append(" ,?,?,?,?,?,?,?,?)");
            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i, BTSLUtil.NullToString(p_transferVO.getTransferID()));
            i++;
            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getTransferDate()));
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getTransferDateTime()));
            i++;
            pstmtInsert.setString(i, senderVO.getNetworkCode());
            i++;
            pstmtInsert.setString(i, senderVO.getUserID());
            i++;
            pstmtInsert.setString(i, p_transferVO.getProductCode());
            i++;
            pstmtInsert.setString(i, senderVO.getMsisdn());
            i++;
            pstmtInsert.setString(i, receiverVO.getMsisdn());
            i++;
            pstmtInsert.setString(i, receiverVO.getNetworkCode());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getTransferValue());
            i++;
            pstmtInsert.setString(i, p_transferVO.getErrorCode());
            i++;
            pstmtInsert.setString(i, p_transferVO.getRequestGatewayType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getRequestGatewayCode());
            i++;
            pstmtInsert.setString(i, p_transferVO.getReferenceID());
            i++;
            pstmtInsert.setString(i, p_transferVO.getPaymentMethodType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getServiceType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getPinSentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_transferVO.getLanguage());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCountry());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getSkey());
            i++;
            if (p_transferVO.getSkeyGenerationTime() == null) {
                pstmtInsert.setNull(i, Types.TIMESTAMP);
                i++;
            } else {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getSkeyGenerationTime()));
                i++;
            }
            pstmtInsert.setString(i, p_transferVO.getSkeySentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_transferVO.getRequestThroughQueue());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCreditBackStatus());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getQuantity());
            i++;
            pstmtInsert.setString(i, p_transferVO.getReconciliationFlag());
            i++;
            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
            i++;
            pstmtInsert.setString(i, p_transferVO.getReconciliationBy());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCreatedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getCreatedOn()));
            i++;
            pstmtInsert.setString(i, p_transferVO.getModifiedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            i++;
            pstmtInsert.setString(i, p_transferVO.getTransferStatus());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCardGroupSetID());
            i++;
            pstmtInsert.setString(i, p_transferVO.getVersion());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCardGroupID());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getSenderAccessFee());
            i++;
            pstmtInsert.setString(i, p_transferVO.getSenderTax1Type());
            i++;
            pstmtInsert.setDouble(i, p_transferVO.getSenderTax1Rate());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getSenderTax1Value());
            i++;
            pstmtInsert.setString(i, p_transferVO.getSenderTax2Type());
            i++;
            pstmtInsert.setDouble(i, p_transferVO.getSenderTax2Rate());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getSenderTax2Value());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getSenderTransferValue());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getReceiverAccessFee());
            i++;
            pstmtInsert.setString(i, p_transferVO.getReceiverTax1Type());
            i++;
            pstmtInsert.setDouble(i, p_transferVO.getReceiverTax1Rate());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getReceiverTax1Value());
            i++;
            pstmtInsert.setString(i, p_transferVO.getReceiverTax2Type());
            i++;
            pstmtInsert.setDouble(i, p_transferVO.getReceiverTax2Rate());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getReceiverTax2Value());
            i++;
            pstmtInsert.setInt(i, p_transferVO.getReceiverValidity());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getReceiverTransferValue());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getReceiverBonusValue());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getReceiverGracePeriod());
            i++;
            pstmtInsert.setInt(i, p_transferVO.getReceiverBonusValidity());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCardGroupCode());
            i++;
            pstmtInsert.setString(i, p_transferVO.getReceiverValPeriodType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getSubService());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getRequestStartTime());
            i++;
            pstmtInsert.setLong(i, System.currentTimeMillis());
            i++;
            pstmtInsert.setString(i, p_transferVO.getTransferCategory());
            i++;
            // added for cell id and switch id
            pstmtInsert.setString(i, p_transferVO.getCellId());
            i++;
            pstmtInsert.setString(i, p_transferVO.getSwitchId());
            i++;
            pstmtInsert.setString(i, p_transferVO.getSerialNumber());
            i++;
            pstmtInsert.setString(i, p_transferVO.getInfo1());
            i++;
            pstmtInsert.setString(i, p_transferVO.getInfo2());
            i++;
            pstmtInsert.setString(i, p_transferVO.getInfo3());
            i++;
            pstmtInsert.setString(i, p_transferVO.getInfo4());
            i++;
            pstmtInsert.setString(i, p_transferVO.getInfo5());
            
            addCount = pstmtInsert.executeUpdate();

            if (addCount > 0) {
                if (p_transferVO.getTransferItemList() != null && !p_transferVO.getTransferItemList().isEmpty() && !p_transferVO.getTransferItemList().isEmpty()) {
                    addCount = 0;
                    addCount = addTransferItemDetails(p_con, p_transferVO.getTransferID(), p_transferVO.getTransferItemList());
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferDetails]", p_transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferDetails]", p_transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
     * Methods adds the validity Extension related transfers details, calls
     * another method to insert the items
     * 
     * @param p_con
     * @param p_transferVO
     * @return int
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public int addValExtTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "addValExtTransferDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferVO:" + p_transferVO.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            final ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            int i = 1;
            final StringBuffer insertQueryBuff = new StringBuffer(
                            " INSERT INTO subscriber_transfers(transfer_id, transfer_date, transfer_date_time, network_code, sender_id, ");
            insertQueryBuff.append(" product_code, sender_msisdn, receiver_msisdn, receiver_network_code, transfer_value, ");
            insertQueryBuff.append(" error_code, request_gateway_type, request_gateway_code,reference_id, payment_method_type, ");
            insertQueryBuff.append(" service_type, pin_sent_to_msisdn, language, country, skey, skey_generation_time, ");
            insertQueryBuff.append(" skey_sent_to_msisdn, request_through_queue, credit_back_status, quantity, reconciliation_flag, ");
            insertQueryBuff.append(" reconciliation_date, reconciliation_by, created_by, created_on, modified_by, modified_on, transfer_status, ");
            insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_access_fee,sender_tax1_type,sender_tax1_rate,sender_tax1_value, ");
            insertQueryBuff.append(" sender_tax2_type,sender_tax2_rate,sender_tax2_value,sender_transfer_value, ");
            insertQueryBuff.append(" receiver_access_fee,receiver_tax1_type,receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
            insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
            insertQueryBuff.append(" card_group_code,receiver_valperiod_type,sub_service,start_time,end_time,transfer_category) ");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i, BTSLUtil.NullToString(p_transferVO.getValExtTransferID()));
            i++;
            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getTransferDate()));
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getTransferDateTime()));
            i++;
            pstmtInsert.setString(i, senderVO.getNetworkCode());
            i++;
            pstmtInsert.setString(i, senderVO.getUserID());
            i++;
            pstmtInsert.setString(i, p_transferVO.getProductCode());
            i++;
            pstmtInsert.setString(i, senderVO.getMsisdn());
            i++;
            pstmtInsert.setString(i, receiverVO.getMsisdn());
            i++;
            pstmtInsert.setString(i, receiverVO.getNetworkCode());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getFeeForValidityExtention());// transfer
            i++; // value

            pstmtInsert.setString(i, p_transferVO.getErrorCode());
            i++;
            pstmtInsert.setString(i, p_transferVO.getRequestGatewayType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getRequestGatewayCode());
            i++;
            pstmtInsert.setString(i, p_transferVO.getReferenceID());
            i++;
            pstmtInsert.setString(i, p_transferVO.getPaymentMethodType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getServiceType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getPinSentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_transferVO.getLanguage());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCountry());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getSkey());
            i++;
            if (p_transferVO.getSkeyGenerationTime() == null) {
                pstmtInsert.setNull(i, Types.TIMESTAMP);
                i++;
            } else {
                pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getSkeyGenerationTime()));
                i++;
            }
            pstmtInsert.setString(i, p_transferVO.getSkeySentToMsisdn());
            i++;
            pstmtInsert.setString(i, p_transferVO.getRequestThroughQueue());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCreditBackStatus());
            i++;
            pstmtInsert.setLong(i, 0);// quantity
            i++;
            pstmtInsert.setString(i, p_transferVO.getReconciliationFlag());
            i++;
            pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
            i++;
            pstmtInsert.setString(i, p_transferVO.getReconciliationBy());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCreatedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getCreatedOn()));
            i++;
            pstmtInsert.setString(i, p_transferVO.getModifiedBy());
            i++;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            i++;
            pstmtInsert.setString(i, p_transferVO.getTransferStatus());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCardGroupSetID());
            i++;
            pstmtInsert.setString(i, p_transferVO.getVersion());
            i++;
            pstmtInsert.setString(i, p_transferVO.getCardGroupID());
            i++;
            pstmtInsert.setLong(i, 0);// sender access fee
            i++;
            pstmtInsert.setString(i, "");// SenderTax1 Type
            i++;
            pstmtInsert.setDouble(i, 0);// SenderTax1 Rate
            i++;
            pstmtInsert.setLong(i, 0);// SenderTax1 Value
            i++;
            pstmtInsert.setString(i, "");// SenderTax2 Type
            i++;
            pstmtInsert.setDouble(i, 0);// SenderTax2 Rate
            i++;
            pstmtInsert.setLong(i, 0);// SenderTax2 Value
            i++;
            pstmtInsert.setLong(i, p_transferVO.getFeeForValidityExtention());// sender
            i++; // transfer
            // value

            pstmtInsert.setLong(i, 0);// Receiver Access Fee
            i++;
            pstmtInsert.setString(i, "");// Receiver Tax1 Type
            i++;
            pstmtInsert.setDouble(i, 0);// Receiver Tax1 Rate
            i++;
            pstmtInsert.setLong(i, 0);// Receiver Tax1 Value
            i++;
            pstmtInsert.setString(i, "");// Receiver Tax2 Type
            i++;
            pstmtInsert.setDouble(i, 0);// //Receiver Tax2 Rate
            i++;
            pstmtInsert.setLong(i, 0);// Receiver Tax2 Value
            i++;
            pstmtInsert.setInt(i, p_transferVO.getValidityDaysToExtend());
            i++;
            pstmtInsert.setLong(i, 0);// Receiver Transfer Value
            i++;
            pstmtInsert.setLong(i, 0);// Receiver Bonus Value
            i++;
            pstmtInsert.setLong(i, p_transferVO.getReceiverGracePeriod());
            i++;
            pstmtInsert.setInt(i, 0);// Receiver Bonus Validity
            i++;
            pstmtInsert.setString(i, p_transferVO.getCardGroupCode());
            i++;
            pstmtInsert.setString(i, p_transferVO.getReceiverValPeriodType());
            i++;
            pstmtInsert.setString(i, p_transferVO.getSubService());
            i++;
            pstmtInsert.setLong(i, p_transferVO.getRequestStartTime());
            i++;
            pstmtInsert.setLong(i, System.currentTimeMillis());
            i++;
            pstmtInsert.setString(i, p_transferVO.getTransferCategory());
            i++;
            addCount = pstmtInsert.executeUpdate();
            if (addCount > 0) {
                if (p_transferVO.getTransferItemList() != null && !p_transferVO.getTransferItemList().isEmpty() && !p_transferVO.getTransferItemList().isEmpty()) {
                    addCount = 0;
                    addCount = addValExtTransferItemDetails(p_con, p_transferVO, p_transferVO.getTransferItemList());
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferDetails]", p_transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferDetails]", p_transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
     * Method to update the transfer details
     * 
     * @param con
     * @param transferVO
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     * @throws SQLException
     * @throws SQLException
     */
    public int updateTransferDetails(Connection con, TransferVO transferVO) throws BTSLBaseException {
        final String methodName = "updateTransferDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferVO:" + transferVO.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) transferVO.getSenderVO();
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE subscriber_transfers SET   ");
            updateQueryBuff.append(" error_code=?, reference_id=?,credit_back_status=?, reconciliation_flag=?, ");
            updateQueryBuff.append(" reconciliation_date=?, reconciliation_by=?, modified_by=?, modified_on=?, transfer_status=? ");
            updateQueryBuff.append(" WHERE transfer_id=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + updateQuery);
            }
            pstmtUpdate = con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, transferVO.getErrorCode());
            i++;
            pstmtUpdate.setString(i, transferVO.getReferenceID());
            i++;
            pstmtUpdate.setString(i, transferVO.getCreditBackStatus());
            i++;
            pstmtUpdate.setString(i, transferVO.getReconciliationFlag());
            i++;
            if (transferVO.getReconciliationDate() == null) {
                pstmtUpdate.setNull(i, Types.TIMESTAMP);
                i++;
            } else {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferVO.getReconciliationDate()));
                i++;
            }
            pstmtUpdate.setString(i, transferVO.getReconciliationBy());
            i++;
            pstmtUpdate.setString(i, transferVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, transferVO.getTransferStatus());
            i++;
            pstmtUpdate.setString(i, transferVO.getTransferID());
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = 0;
                updateCount = updateTransferItemDetails(con, transferVO.getTransferID(), transferVO.getTransferItemList());
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferDetails]", transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferDetails]", transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
     * Method to update the Validity Extension transfer details
     * 
     * @param p_con
     * @param p_transferVO
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     * @throws SQLException
     * @throws SQLException
     */
    public int updateValExtTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "updateValExtTransferDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferVO:" + p_transferVO.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE subscriber_transfers SET   ");
            updateQueryBuff.append(" error_code=?, reference_id=?,credit_back_status=?, reconciliation_flag=?, ");
            updateQueryBuff.append(" reconciliation_date=?, reconciliation_by=?, modified_by=?, modified_on=?, transfer_status=? ");
            updateQueryBuff.append(" WHERE transfer_id=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_transferVO.getErrorCode());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getReferenceID());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getCreditBackStatus());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getReconciliationFlag());
            i++;
            if (p_transferVO.getReconciliationDate() == null) {
                pstmtUpdate.setNull(i, Types.TIMESTAMP);
                i++;
            } else {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
                i++;
            }
            pstmtUpdate.setString(i, p_transferVO.getReconciliationBy());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_transferVO.getTransferStatus());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getValExtTransferID());
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = 0;
                updateCount = updateValExtTransferItemDetails(p_con, p_transferVO);
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateValExtTransferDetails]",
                            p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateValExtTransferDetails]",
                            p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
     * Method to add the transfer items details
     * 
     * @param p_con
     * @param p_transferID
     *            String
     * @param transferItemsList
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     * @throws SQLException
     */
    public int addTransferItemDetails(Connection p_con, String p_transferID, ArrayList transferItemsList) throws BTSLBaseException {
        final String methodName = "addTransferItemDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, p_transferID, "Entered p_transferItemList:" + transferItemsList);
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            TransferItemVO transferItemVO = null;
            int i = 1;
            int itemCount = 1;
            final StringBuffer insertQueryBuff = new StringBuffer(
                            " INSERT INTO transfer_items (transfer_id, sno,prefix_id,msisdn, entry_date, request_value, previous_balance, post_balance, user_type, transfer_type, entry_type, ");
            insertQueryBuff.append(" validation_status, update_status, service_class_id,protocol_status,account_status, ");
            insertQueryBuff.append(" transfer_value, interface_type, interface_id, interface_response_code, ");
            insertQueryBuff.append(" interface_reference_id, subscriber_type, service_class_code, msisdn_previous_expiry, ");
            insertQueryBuff.append(" msisdn_new_expiry, transfer_status, transfer_date, transfer_date_time,entry_date_time,first_call,reference_id");
            insertQueryBuff.append(",service_provider_name)");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }

            pstmtInsert = p_con.prepareStatement(insertQuery);

            if (transferItemsList != null && !transferItemsList.isEmpty()) {
                for (int j = 0, k = transferItemsList.size(); j < k; j++) {
                    transferItemVO = (TransferItemVO) transferItemsList.get(j);
                    addCount = 0;
                    i = 1;
                    if (pstmtInsert != null) {
                        pstmtInsert.clearParameters();
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "transferItemVO:" + transferItemVO.toString());
                    }
                    pstmtInsert.setString(i, transferItemVO.getTransferID());
                    i++;
                    pstmtInsert.setInt(i, transferItemVO.getSNo());
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getPrefixID());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getMsisdn());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getEntryDate()));
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getRequestValue());
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getPreviousBalance());
                    i++;
                    if(!((PretupsI.TXN_STATUS_SUCCESS).equals(transferItemVO.getTransferStatus())) && PretupsI.USER_TYPE_SENDER.equalsIgnoreCase(transferItemVO.getUserType()))
                    	pstmtInsert.setLong(i, transferItemVO.getPreviousBalance());
                    else
                    	pstmtInsert.setLong(i, transferItemVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getUserType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getTransferType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getEntryType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getValidationStatus());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getUpdateStatus());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getServiceClass());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getProtocolStatus());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getAccountStatus());
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getTransferValue());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getInterfaceType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getInterfaceID());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getInterfaceResponseCode());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getInterfaceReferenceID());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getSubscriberType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getServiceClassCode());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getTransferStatus());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getTransferDate()));
                    i++;
                    pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getTransferDateTime()));
                    i++;
                    pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getEntryDateTime()));
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getFirstCall());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getReferenceID());
                    i++;
                    // For Service Provider Information
                    pstmtInsert.setString(i, BTSLUtil.NullToString(transferItemVO.getServiceProviderName()));
                    i++;
                    addCount = pstmtInsert.executeUpdate();
                    addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres

                    itemCount = itemCount + 1;
                    if (addCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferItemDetails]", p_transferID,
                            "", "", exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferItemDetails]", p_transferID,
                            "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
     * Method to add the transfer items details
     * 
     * @param con
     * @param p_transferID
     *            String
     * @param transferItemsList
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     * @throws SQLException
     */
    public int addValExtTransferItemDetails(Connection con, TransferVO transferVO, ArrayList transferItemsList) throws BTSLBaseException {
        final String methodName = "addValExtTransferItemDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, transferVO.getValExtTransferID(), "Entered p_transferItemList:" + transferItemsList);
        }
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            TransferItemVO transferItemVO = null;
            int i = 1;
            int itemCount = 1;
            final StringBuffer insertQueryBuff = new StringBuffer(
                            " INSERT INTO transfer_items (transfer_id, sno,prefix_id,msisdn, entry_date, request_value, previous_balance, post_balance, user_type, transfer_type, entry_type, ");
            insertQueryBuff.append(" validation_status, update_status, service_class_id,protocol_status,account_status, ");
            insertQueryBuff.append(" transfer_value, interface_type, interface_id, interface_response_code, ");
            insertQueryBuff.append(" interface_reference_id, subscriber_type, service_class_code, msisdn_previous_expiry, ");
            insertQueryBuff.append(" msisdn_new_expiry, transfer_status, transfer_date, transfer_date_time,entry_date_time,first_call,reference_id) ");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            final String insertQuery = insertQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + insertQuery);
            }

            pstmtInsert = con.prepareStatement(insertQuery);

            if (transferItemsList != null && !transferItemsList.isEmpty()) {
                for (int j = 0, k = transferItemsList.size(); j < k; j++) {
                    transferItemVO = (TransferItemVO) transferItemsList.get(j);
                    addCount = 0;
                    i = 1;
                    if (pstmtInsert != null) {
                        pstmtInsert.clearParameters();
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "transferItemVO:" + transferItemVO.toString());
                    }
                    pstmtInsert.setString(i, transferVO.getValExtTransferID());
                    i++;
                    pstmtInsert.setInt(i, transferItemVO.getSNo());
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getPrefixID());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getMsisdn());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getEntryDate()));
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getRequestValue());
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getPreviousBalance());
                    i++;
                    pstmtInsert.setLong(i, transferItemVO.getPostBalance());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getUserType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getTransferType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getEntryType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getValidationStatus());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getUpdateStatus());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getServiceClass());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getProtocolStatus());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getAccountStatus());
                    i++;
                    if (PretupsI.USER_TYPE_SENDER.equals(transferItemVO.getUserType())) {
                        pstmtInsert.setLong(i, transferVO.getFeeForValidityExtention());
                        i++;
                    } else {
                        pstmtInsert.setLong(i, 0);// transfer value for
                        i++;
                    } // receiver
                    pstmtInsert.setString(i, transferItemVO.getInterfaceType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getInterfaceID());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getInterfaceResponseCode());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getInterfaceReferenceID());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getSubscriberType());
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getServiceClassCode());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));// To
                    i++; // be
                    // checked
                    pstmtInsert.setString(i, transferItemVO.getTransferStatus());
                    i++;
                    pstmtInsert.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getTransferDate()));
                    i++;
                    pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getTransferDateTime()));
                    i++;
                    pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getEntryDateTime()));
                    i++;
                    pstmtInsert.setString(i, transferItemVO.getFirstCall());
                    i++;
                    pstmtInsert.setString(i, transferVO.getValExtTransferID());
                    i++;
                    addCount = pstmtInsert.executeUpdate();
                    addCount = BTSLUtil.getInsertCount(addCount); // added to make code compatible with insertion in partitioned table in postgres
                    itemCount = itemCount + 1;
                    if (addCount <= 0) {
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferItemDetails]",
                            transferVO.getValExtTransferID(), "", "", exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            addCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferItemDetails]",
                            transferVO.getValExtTransferID(), "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
     * Method to update the transfer items details
     * 
     * @param con
     * @param transferID
     * @param transferItemsList
     * @return int
     * @throws BTSLBaseException
     */
    public int updateTransferItemDetails(Connection con, String transferID, ArrayList transferItemsList) throws BTSLBaseException {
        final String methodName = "updateTransferItemDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, transferID, "Entered p_transferItemsList size:" + transferItemsList.size());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        TransferItemVO transferItemVO = null;
        try {
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE transfer_items SET REQUEST_VALUE=?, previous_balance=?, post_balance=?, ");
            updateQueryBuff.append(" update_status=?, interface_response_code=?, ");
            updateQueryBuff.append(" interface_reference_id=?, msisdn_previous_expiry=?, ");
            updateQueryBuff.append(" msisdn_new_expiry=?, transfer_status=?, ");
            updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=? , ");
            updateQueryBuff.append(" reference_id=? ");
            updateQueryBuff.append(" WHERE  transfer_id=? AND msisdn=? AND sno=?");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + updateQuery);
            }

            pstmtUpdate = con.prepareStatement(updateQuery);
            int transferItemsListSize = transferItemsList.size();
            for (int j = 0; j < transferItemsListSize; j++) {
                i = 1;
                transferItemVO = (TransferItemVO) transferItemsList.get(j);
                updateCount = 0;
                if (pstmtUpdate != null) {
                    pstmtUpdate.clearParameters();
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "transferItemVO:" + transferItemVO.toString());
                }
                pstmtUpdate.setLong(i, transferItemVO.getRequestValue());
                i++;
                pstmtUpdate.setLong(i, transferItemVO.getPreviousBalance());
                i++;
                
                if(PretupsI.USER_TYPE_SENDER.equalsIgnoreCase(transferItemVO.getUserType()))
                	pstmtUpdate.setLong(i,transferItemVO.getPreviousBalance()-transferItemVO.getTransferValue());
                else
                	pstmtUpdate.setLong(i, transferItemVO.getPostBalance());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getUpdateStatus());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceResponseCode());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceReferenceID());
                i++;
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                i++;
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));
                i++;
                pstmtUpdate.setString(i, transferItemVO.getTransferStatus());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getTransferType2());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceReferenceID2());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getUpdateStatus2());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getTransferType1());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceReferenceID1());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getUpdateStatus1());
                i++;
                pstmtUpdate.setLong(i, transferItemVO.getAdjustValue());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getReferenceID());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getTransferID());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getMsisdn());
                i++;
                pstmtUpdate.setInt(i, transferItemVO.getSNo());
                i++;
                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", transferID,
                            "", "", exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", transferID,
                            "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
                LOG.debug(methodName, transferID, "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * Method to update the Validity Extension transfer items details
     * 
     * @param con
     * @param p_transferID
     * @param p_transferItemsList
     * @return int
     * @throws BTSLBaseException
     */
    public int updateValExtTransferItemDetails(Connection con, TransferVO transferVO) throws BTSLBaseException {
        final String methodName = "updateTransferItemDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, transferVO.getValExtTransferID(), "Entered p_transferItemsList size:" + transferVO.getTransferItemList().size());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        TransferItemVO transferItemVO = null;
        final ArrayList transferItemsList = transferVO.getTransferItemList();
        try {
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE transfer_items SET previous_balance=?, post_balance=?, ");
            updateQueryBuff.append(" update_status=?, interface_response_code=?, ");
            updateQueryBuff.append(" interface_reference_id=?, msisdn_previous_expiry=?, ");
            updateQueryBuff.append(" msisdn_new_expiry=?, transfer_status=?, ");
            updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=? , ");
            updateQueryBuff.append(" reference_id=? ");
            updateQueryBuff.append(" WHERE  transfer_id=? AND msisdn=? AND sno=?");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + updateQuery);
            }

            pstmtUpdate = con.prepareStatement(updateQuery);
            int transferItemsListSize = transferItemsList.size();
            for (int j = 0; j < transferItemsListSize; j++) {
                i = 1;
                transferItemVO = (TransferItemVO) transferItemsList.get(j);
                updateCount = 0;
                if (pstmtUpdate != null) {
                    pstmtUpdate.clearParameters();
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "transferItemVO:" + transferItemVO.toString());
                }
                pstmtUpdate.setLong(i, transferItemVO.getPreviousBalance());
                i++;
                pstmtUpdate.setLong(i, transferItemVO.getPostBalance());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getUpdateStatus());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceResponseCode());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceReferenceID());
                i++;
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                i++;
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));
                i++;
                pstmtUpdate.setString(i, transferItemVO.getTransferStatus());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getTransferType2());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceReferenceID2());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getUpdateStatus2());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getTransferType1());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getInterfaceReferenceID1());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getUpdateStatus1());
                i++;
                pstmtUpdate.setLong(i, transferItemVO.getAdjustValue());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getReferenceID());
                i++;
                pstmtUpdate.setString(i, transferVO.getValExtTransferID());
                i++;
                pstmtUpdate.setString(i, transferItemVO.getMsisdn());
                i++;
                pstmtUpdate.setInt(i, transferItemVO.getSNo());
                i++;
                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", transferVO
                            .getValExtTransferID(), "", "", exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", transferVO
                            .getValExtTransferID(), "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
                LOG.debug(methodName, transferVO.getValExtTransferID(), "Exiting updateCount=" + updateCount);
            }
        }// end of finally
    }

    /**
     * 
     * @param con
     * @param msisdn
     * @param fromDate
     * @param toDate
     * @return long[] which have total transfer count and total transfer amount
     * @throws BTSLBaseException
     * @throws Exception
     * @author avinash.kamthan
     */
    public long[] loadTransferStatus(Connection con, String msisdn, String fromDate, String toDate) throws BTSLBaseException {

        final String methodName = "loadTransferStatus";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_msisdn:" + msisdn + " p_fromDate:" + fromDate + " p_toDate: " + toDate);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        long[] data = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append(" SELECT COUNT(TRANSFER_ID) as transfer_count ,  SUM(transfer_value) as total_transfer");
            selectQueryBuff.append(" FROM subscriber_transfer ");
            selectQueryBuff.append(" WHERE sender_msisdn = ? and transfer_status = 'Y' ");
            selectQueryBuff.append(" AND transfer_date BETWEEN ");
            selectQueryBuff.append(" TO_DATE(?,'DD/MM/YY')  and TO_DATE(?,'DD/MM/YY') ");

            final String selectQuery = selectQueryBuff.toString();

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "select query:" + selectQuery);
            }

            pstmtSelect = con.prepareStatement(selectQuery);

            pstmtSelect.setString(1, msisdn);
            pstmtSelect.setString(2, fromDate);
            pstmtSelect.setString(3, toDate);

            rs = pstmtSelect.executeQuery();
            data = new long[2];
            if (rs.next()) {
                data[0] = rs.getLong("transfer_count");
                data[1] = rs.getLong("total_transfer");
            }

        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferStatus]", "", msisdn, "",
                            sqlException + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferStatus]", "", msisdn, "",
                            exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        }// end of catch
        finally {
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
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting :" + data);
            }
        }// end of finally

        return data;
    }

    /**
     * Load the transfer rules cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public HashMap<String ,TransferRulesVO> loadTransferRuleCache() throws BTSLBaseException {

        final String methodName = "loadTransferRuleCache";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final HashMap<String ,TransferRulesVO> map = new HashMap<String ,TransferRulesVO>();

        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cd.card_group_set_name setname, cd.status setstatus, cd.language_1_message, ");
        strBuff.append(" cd.language_2_message,  tr.module, tr.network_code, tr.sender_subscriber_type, ");
        strBuff.append(" tr.receiver_subscriber_type, tr.sender_service_class_id, tr.receiver_service_class_id, ");
        strBuff.append(" tr.status,tr.card_group_set_id,  tr.modified_on, tr.modified_by , tr.created_on, ");
        // added for promotional trans rule cache
        strBuff.append(" tr.start_time,tr.end_time, tr.rule_level, tr.date_range,tr.time_slab,tr.rule_type,tr.allowed_days,tr.allowed_series,tr.denied_series, ");
        strBuff.append(" tr.created_by,tr.sub_service,tr.service_type,stsm.selector_name, tr.subscriber_status,tr.sp_group_id,tr.gateway_code,tr.category_code,tr.grade_code,tr.cell_group_id FROM transfer_rules tr, ");
        strBuff.append(" card_group_set cd , service_type_selector_mapping stsm  WHERE tr.status<>'N' ");
        strBuff.append(" AND tr.rule_type='N'");
        strBuff.append(" AND cd.status<>'N' AND stsm.status<>'N' AND tr.card_group_set_id=cd.card_group_set_id ");
        strBuff.append(" AND tr.module=cd.module_code AND tr.network_code=cd.network_code ");
        strBuff.append(" AND stsm.service_type=tr.service_type AND stsm.selector_code=tr.sub_service ");
        strBuff.append(" AND stsm.service_type=cd.service_type AND stsm.selector_code=cd.sub_service ");
        strBuff.append(" AND ( tr.end_time >= ? or tr.end_time is null )");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadTransferRuleCache", "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            int i = 1;
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(new Date()));
            i++;
            rs = pstmt.executeQuery();
            TransferRulesVO rulesVO = null;
            while (rs.next()) {
                rulesVO = new TransferRulesVO();

                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setCardGroupSetIDStatus(rs.getString("setstatus"));
                rulesVO.setCardGroupSetName(rs.getString("setname"));
                rulesVO.setCardGroupMessage1(rs.getString("language_1_message"));
                rulesVO.setCardGroupMessage2(rs.getString("language_2_message"));
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setSubServiceTypeIdDes(rs.getString("selector_name"));
                rulesVO.setStartTime(rs.getDate("start_time"));
                rulesVO.setEndTime(rs.getDate("end_time"));
                rulesVO.setRuleLevel(rs.getString("rule_level"));
                rulesVO.setSelectRangeType(rs.getString("date_range"));
                rulesVO.setMultipleSlab(rs.getString("time_slab"));
                rulesVO.setRuleType(rs.getString("rule_type"));
                rulesVO.setAllowedDays(rs.getString("allowed_days"));
                rulesVO.setAllowedSeries(rs.getString("allowed_series"));
                rulesVO.setDeniedSeries(rs.getString("denied_series"));
                rulesVO.setSubscriberStatus(rs.getString("subscriber_status"));
                rulesVO.setServiceGroupCode(rs.getString("sp_group_id"));
                rulesVO.setGatewayCode(rs.getString("gateway_code"));
                rulesVO.setCategoryCode(rs.getString("category_code"));
                rulesVO.setGradeCode(rs.getString("grade_code"));
                rulesVO.setCellGroupId(rs.getString("cell_group_id"));
                LOG.debug("rulesVO.getKey() ", rulesVO.getKey());
                map.put(rulesVO.getKey(), rulesVO);
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, sqlException + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleCache]", "", "", "",
                            sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleCache]", "", "", "",
                            exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: networkMap size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Method loadP2PTransferItemsVOList.
     * 
     * @param con
     *            Connection
     * @param transferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PReconciliationItemsList(Connection con, String transferID) throws BTSLBaseException {

        final String methodName = "loadP2PReconciliationItemsList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferID=" + transferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        TransferItemVO p2pTransferItemVO = null;
        final ArrayList p2pTransferItemsVOList = new ArrayList();
        try {
        	TransferQry transferQry = (TransferQry)ObjectProducer.getObject(QueryConstants.TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = transferQry.loadP2PReconciliationItemsList(con, transferID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                p2pTransferItemVO = new TransferItemVO();

                p2pTransferItemVO.setTransferID(rs.getString("transfer_id"));
                p2pTransferItemVO.setMsisdn(rs.getString("msisdn"));
                p2pTransferItemVO.setEntryDate(rs.getDate("entry_date"));
                p2pTransferItemVO.setRequestValue(rs.getLong("request_value"));
                p2pTransferItemVO.setPreviousBalance(rs.getLong("previous_balance"));
                p2pTransferItemVO.setPostBalance(rs.getLong("post_balance"));
                p2pTransferItemVO.setUserType(rs.getString("user_type"));
                p2pTransferItemVO.setTransferType(rs.getString("transfer_type"));
                p2pTransferItemVO.setEntryType(rs.getString("entry_type"));
                p2pTransferItemVO.setValidationStatus(rs.getString("validation_status"));
                p2pTransferItemVO.setUpdateStatus(rs.getString("update_status"));
                p2pTransferItemVO.setTransferValue(rs.getLong("transfer_value"));
                p2pTransferItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                p2pTransferItemVO.setInterfaceType(rs.getString("interface_type"));
                p2pTransferItemVO.setInterfaceID(rs.getString("interface_id"));
                p2pTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                p2pTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                p2pTransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                p2pTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                p2pTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                p2pTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                p2pTransferItemVO.setTransferStatus(rs.getString("transfer_status"));
                p2pTransferItemVO.setTransferDate(rs.getDate("transfer_date"));
                p2pTransferItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                p2pTransferItemVO.setEntryDateTime(rs.getDate("entry_date_time"));
                p2pTransferItemVO.setFirstCall(rs.getString("first_call"));
                p2pTransferItemVO.setSNo(rs.getInt("sno"));
                p2pTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
                p2pTransferItemVO.setServiceClass(rs.getString("service_class_id"));
                p2pTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
                p2pTransferItemVO.setAccountStatus(rs.getString("account_status"));
                p2pTransferItemVO.setTransferStatusMessage(rs.getString("value"));
                p2pTransferItemVO.setReferenceID(rs.getString("reference_id"));
                p2pTransferItemsVOList.add(p2pTransferItemVO);
            }

        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationItemsList]", "", "",
                            "", sqlException + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationItemsList]", "", "",
                            "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        }// end of catch
        finally {
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
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting p2pTransferItemsVOList.size()=" + p2pTransferItemsVOList.size());
            }
        }// end of finally

        return p2pTransferItemsVOList;
    }

    /**
     * Method updateReconcilationStatus.
     * 
     * @param p_con
     *            Connection
     * @param p_reconciliationVO
     *            ReconciliationVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateReconcilationStatus(Connection p_con, P2PTransferVO p2pTransferVO) throws BTSLBaseException {
        final String methodName = "updateReconcilationStatus";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p2pTransferVO=" + p2pTransferVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
        	TransferQry transferQry = (TransferQry) ObjectProducer.getObject(QueryConstants.TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        	String query = transferQry.updateReconcilationStatusQry();
            pstmtUpdate = p_con.prepareStatement(query);
            int i = 1;
            pstmtUpdate.setString(i, p2pTransferVO.getTransferStatus());
            i++;
            pstmtUpdate.setString(i, p2pTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p2pTransferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p2pTransferVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p2pTransferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p2pTransferVO.getTransferID());
            i++;
            // By sandeep ID REC001
            // to perform the check "is Already modify"
            pstmtUpdate.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            pstmtUpdate.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransferDAO[updateReconcilationStatus]", "", "", "",
                                "Record is already modified Txn ID=" + p2pTransferVO.getTransferID());
            } else // if(updateCount>=1)
            {
                if (p2pTransferVO.getTransferItemList() != null && !p2pTransferVO.getTransferItemList().isEmpty()) {
                    updateCount = 0;
                    updateCount = addTransferItemDetails(p_con, p2pTransferVO.getTransferID(), p2pTransferVO.getTransferItemList());
                }
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, sqlException + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateReconcilationStatus]", "", "", "",
                            sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(methodName, exception + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateReconcilationStatus]", "", "", "",
                            exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:return=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * 
     * method markP2PReceiverAmbiguous
     * This method is used in the C2S Reconciliation module, by this method
     * receiver's transfer status is updated
     * as ambigous and previous transfer status is assigned to the update
     * status.
     * 
     * @param p_con
     * @param p_transferID
     * @return
     * @throws BTSLBaseException
     *             int
     * @author sandeep.goel ID REC001
     */
    public int markP2PReceiverAmbiguous(Connection p_con, String p_transferID) throws BTSLBaseException {
        final String methodName = "markP2PReceiverAmbiguous";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferID:" + p_transferID);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE transfer_items ");
            updateQueryBuff.append("SET update_status=transfer_status,transfer_status=? WHERE  transfer_id=? AND user_type=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("updateTransferItemDetails", "Update query:" + updateQuery);
            }

            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, InterfaceErrorCodesI.AMBIGOUS);
            i++;
            pstmtUpdate.setString(i, p_transferID);
            i++;
            pstmtUpdate.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[markP2PReceiverAmbiguous]",
                            p_transferID, "", "", sqlException + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[markP2PReceiverAmbiguous]",
                            p_transferID, "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
     * Load promotional transfer rules
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * 
     */
    public HashMap loadPromotionalTransferRuleMap(Connection con, Date transferDate) throws BTSLBaseException {
        final String methodName = "loadPromotionalTransferRuleMap";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferDate=" + transferDate);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final HashMap map = new HashMap();

        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cd.card_group_set_name setname, cd.status setstatus, cd.language_1_message, ");
        strBuff.append(" cd.language_2_message,  tr.module, tr.network_code, tr.sender_subscriber_type, ");
        strBuff.append(" tr.receiver_subscriber_type, tr.sender_service_class_id,tr.receiver_service_class_id, ");
        strBuff.append(" tr.status,tr.card_group_set_id,  tr.modified_on, tr.modified_by , tr.created_on, ");
        strBuff.append(" tr.created_by,tr.sub_service,tr.service_type,stsm.selector_name, tr.start_time,tr.end_time, tr.rule_level, tr.date_range,time_slab, ");
        // Prefix ID changes
        strBuff.append(" tr.allowed_days,tr.allowed_series,tr.denied_series,tr.gateway_code");

        strBuff.append(" FROM transfer_rules tr, card_group_set cd , service_type_selector_mapping stsm ");
        strBuff.append(" WHERE tr.status<>'N' AND cd.status<>'N' AND stsm.status<>'N' AND tr.card_group_set_id=cd.card_group_set_id ");
        strBuff.append(" AND tr.module=cd.module_code AND tr.network_code=cd.network_code ");
        strBuff.append(" AND stsm.service_type=cd.service_type AND stsm.selector_code=cd.sub_service ");
        strBuff.append(" AND tr.rule_type=? AND tr.start_time<=? AND tr.end_time>=? ");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadPromotionalTransferRuleMap", "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.TRANSFER_RULE_PROMOTIONAL);
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(transferDate));
            pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(transferDate));
            rs = pstmt.executeQuery();
            TransferRulesVO rulesVO = null;
            while (rs.next()) {
                rulesVO = new TransferRulesVO();

                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setCardGroupSetIDStatus(rs.getString("setstatus"));
                rulesVO.setCardGroupSetName(rs.getString("setname"));
                rulesVO.setCardGroupMessage1(rs.getString("language_1_message"));
                rulesVO.setCardGroupMessage2(rs.getString("language_2_message"));
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setSubServiceTypeIdDes(rs.getString("selector_name"));
                rulesVO.setStartTime(rs.getDate("start_time"));
                rulesVO.setEndTime(rs.getDate("end_time"));
                rulesVO.setRuleLevel(rs.getString("rule_level"));
                rulesVO.setSelectRangeType(rs.getString("date_range"));
                rulesVO.setMultipleSlab(rs.getString("time_slab"));
                // Prefix ID changes
                rulesVO.setAllowedDays(rs.getString("allowed_days"));
                rulesVO.setAllowedSeries(rs.getString("allowed_series"));
                rulesVO.setDeniedSeries(rs.getString("denied_series"));
                
                rulesVO.setGatewayCode(rs.getString("gateway_code"));

                map.put(rulesVO.getKey() + "_" + rulesVO.getRuleLevel(), rulesVO);
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, sqlException + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleMap]", "", "",
                            "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleMap]", "", "",
                            "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: networkMap size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Method modifyPromotionalTransferRuleFile.
     * 
     * @param p_con
     * @param p_transferRuleList
     * @param p_errorVoList
     * @param p_messages
     * @param p_locale
     * @throws BTSLBaseException
     * @author sanjeew.kumar
     */
    /*
     * public void modifyPromotionalTransferRuleFile(Connection p_con,ArrayList
     * p_transferRuleList,ArrayList p_errorVoList,MessageResources p_messages,
     * Locale p_locale) throws BTSLBaseException
     * {
     * if(LOG.isDebugEnabled())LOG.debug("modifyPromotionalTransferRuleFile",
     * "Entered p_transferRuleList:"+p_transferRuleList.toString());
     * PreparedStatement pstmtUpdate=null;
     * PreparedStatement pstmtSelect=null;
     * ResultSet rs = null;
     * int updateCount=0;
     * int i=1;
     * TransferRulesVO transferRulesVO=null;
     * try
     * {
     * StringBuffer selectQuery=new StringBuffer();
     * selectQuery.append("SELECT 1 FROM transfer_rules ");
     * selectQuery.append(
     * "WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  "
     * );
     * selectQuery.append(
     * "AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=?"
     * ); //AND status=? ";
     * 
     * if(LOG.isDebugEnabled())
     * LOG.debug("modifyPromotionalTransferRuleFile","Select query:"+selectQuery
     * );
     * pstmtSelect = p_con.prepareStatement(selectQuery.toString());
     * 
     * StringBuffer updateQueryBuff =new StringBuffer();
     * updateQueryBuff.append(
     * "UPDATE transfer_rules SET  modified_on=?, modified_by=? ");
     * updateQueryBuff.append(
     * ",card_group_set_id=? ,status=? ,start_time=? ,end_time=?");
     * updateQueryBuff.append(
     * "WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
     * updateQueryBuff.append(
     * "AND receiver_subscriber_type=? AND sender_service_class_id=? ");
     * updateQueryBuff.append(
     * "AND receiver_service_class_id=? AND sub_service = ? AND service_type = ? AND rule_level=?"
     * );
     * if(LOG.isDebugEnabled())
     * LOG.debug("modifyPromotionalTransferRuleFile","Update query:"+updateQueryBuff
     * );
     * 
     * ListValueVO errorVO=null;
     * if(p_errorVoList==null)
     * p_errorVoList=new ArrayList();
     * for(int s=0;s<p_transferRuleList.size();s++)
     * {
     * i=1;
     * rs = null;
     * transferRulesVO=(TransferRulesVO)p_transferRuleList.get(s);
     * pstmtSelect.clearParameters();
     * pstmtSelect.setString(i++,transferRulesVO.getModule());
     * pstmtSelect.setString(i++,transferRulesVO.getNetworkCode());
     * pstmtSelect.setString(i++,transferRulesVO.getSenderSubscriberType());
     * pstmtSelect.setString(i++,transferRulesVO.getReceiverSubscriberType());
     * pstmtSelect.setString(i++,transferRulesVO.getSenderServiceClassID());
     * pstmtSelect.setString(i++,transferRulesVO.getReceiverServiceClassID());
     * pstmtSelect.setString(i++,transferRulesVO.getSubServiceTypeId());
     * pstmtSelect.setString(i++,transferRulesVO.getServiceType());
     * pstmtSelect.setString(i++,transferRulesVO.getRuleLevel());
     * rs = pstmtSelect.executeQuery();
     * if(rs.next())
     * {
     * errorVO=new
     * ListValueVO("",transferRulesVO.getRowID(),p_messages.getMessage(p_locale,
     * "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.allreadyexist"
     * ));
     * p_errorVoList.add(errorVO);
     * }
     * else
     * {
     * i=1;
     * updateCount=0;
     * pstmtUpdate.clearParameters();
     * pstmtUpdate = p_con.prepareStatement(updateQueryBuff.toString());
     * pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(
     * transferRulesVO.getModifiedOn()));
     * pstmtUpdate.setString(i++,transferRulesVO.getModifiedBy());
     * pstmtUpdate.setString(i++,transferRulesVO.getCardGroupSetID());
     * pstmtUpdate.setString(i++,transferRulesVO.getStatus());
     * pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(
     * transferRulesVO.getStartTime()));
     * pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(
     * transferRulesVO.getEndTime()));
     * pstmtUpdate.setString(i++,transferRulesVO.getModule());
     * pstmtUpdate.setString(i++,transferRulesVO.getNetworkCode());
     * pstmtUpdate.setString(i++,transferRulesVO.getSenderSubscriberType());
     * pstmtUpdate.setString(i++,transferRulesVO.getReceiverSubscriberType());
     * pstmtUpdate.setString(i++,transferRulesVO.getSenderServiceClassID());
     * pstmtUpdate.setString(i++,transferRulesVO.getReceiverServiceClassID());
     * pstmtUpdate.setString(i++,transferRulesVO.getSubServiceTypeId());
     * pstmtUpdate.setString(i++,transferRulesVO.getServiceType());
     * pstmtUpdate.setString(i++,transferRulesVO.getRuleLevel());
     * 
     * //for the checking is the record modified during the transaction.
     * boolean modified = this.isRecordModified(p_con, transferRulesVO);
     * if (modified)
     * {
     * errorVO=new
     * ListValueVO("",transferRulesVO.getRowID(),p_messages.getMessage(p_locale,
     * "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.modify.true"
     * ));
     * p_errorVoList.add(errorVO);
     * }
     * else
     * {
     * //throw new BTSLBaseException(this, "modifyPromotionalTransferRuleFile",
     * "error.modify.true");
     * updateCount = pstmtUpdate.executeUpdate();
     * if(updateCount==0)
     * {
     * errorVO=new
     * ListValueVO("",transferRulesVO.getRowID(),p_messages.getMessage(p_locale,
     * "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.updatefailed"
     * ));
     * p_errorVoList.add(errorVO);
     * }
     * }
     * }
     * }
     * }
     * catch(BTSLBaseException be)
     * {be.printStackTrace();
     * throw be;
     * }
     * catch (SQLException sqle)
     * {
     * LOG.error("updatePromotionalTransferRule",sqlException+sqle.getMessage(
     * ));
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"TransferDAO[updatePromotionalTransferRule]","",
     * "","",sqlException+sqle.getMessage());
     * throw new BTSLBaseException(this, "updatePromotionalTransferRule",
     * "error.general.sql.processing");
     * }//end of catch
     * catch (Exception e)
     * {
     * LOG.error("updatePromotionalTransferRule","Exception "+e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"TransferDAO[updatePromotionalTransferRule]","",
     * "","",exception+e.getMessage());
     * throw new BTSLBaseException(this, "updatePromotionalTransferRule",
     * errorWhileProcessing);
     * }//end of catch
     * finally
     * {
     * try{if(pstmtUpdate!=null) pstmtUpdate.close();}catch(Exception e){}
     * try{if(pstmtSelect!=null) pstmtSelect.close();}catch(Exception e){}
     * if(LOG.isDebugEnabled())LOG.debug("updatePromotionalTransferRule",
     * "Exiting updateCount="+updateCount);
     * }//end of finally
     * //return updateCount;
     * }
     */

    /**
     * @param con
     * @param receiverAllServiceClassID
     * @param enquiryServiceType
     * @param networkCode
     * @param domainCodeForCategory
     * @return
     * @throws BTSLBaseException
     */
    public TransferVO loadCardGroupSetIdFromTransferRule(Connection con, String receiverAllServiceClassID, String enquiryServiceType, String networkCode, String domainCodeForCategory, String subService) throws BTSLBaseException {
        final String methodName = "loadCardGroupSetIdFromTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_receiverAllServiceClassID:" + receiverAllServiceClassID + "p_enquiryServiceType" + enquiryServiceType+", domainCodeForCategory="+domainCodeForCategory+", subService="+subService);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TransferVO transferVO = null;
        boolean isSubServiceValid = false;
        if(!BTSLUtil.isNullString(subService)){
        	isSubServiceValid = true;
        }
        final StringBuffer strBuff = new StringBuffer(" SELECT card_group_set_id,network_code,module ");
        strBuff.append("FROM  transfer_rules WHERE receiver_service_class_id=? AND network_code=? ");
        strBuff.append("AND sender_subscriber_type=? AND service_type=? AND status<>'N'");
        if(isSubServiceValid){
        strBuff.append(" AND sub_service=? ");
        }
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, receiverAllServiceClassID);
            pstmt.setString(2, networkCode);
            pstmt.setString(3, domainCodeForCategory);
            pstmt.setString(4, enquiryServiceType);
            if(isSubServiceValid){
            pstmt.setString(5, subService);	
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                transferVO = new TransferVO();
                transferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setModule(rs.getString("module"));
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, sqlException + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadCardGroupSetIdFromTransferRule]", "",
                            "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadCardGroupSetIdFromTransferRule]", "",
                            "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:  transferVO =" + transferVO);
            }
        }
        return transferVO;

    }

    /**
     * Method isPromotionalRuleExistOnTimeRange.
     * 
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     * @param p_transferDate
     *            Date
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isPromotionalRuleExistOnTimeRange(Connection p_con, TransferRulesVO p_transferRulesVO, Date p_transferDate) throws BTSLBaseException {
        final String methodName = "isPromotionalRuleExistOnTimeRange";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered transferRulesVO=" + p_transferRulesVO.toString() + ",transferDate=" + p_transferDate);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        int i = 1;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE sender_subscriber_type=? AND to_char(start_time,'hh24:mm')<=to_char(?,'hh24:mm') AND to_char(end_time,'hh24:mm')>=to_char(?,'hh24:mm')");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(i, p_transferRulesVO.getSenderSubscriberType());
            i++;
            pstmtSelect.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferDate));
            i++;
            pstmtSelect.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferDate));
            i++;
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalRuleExistOnTimeRange]", "",
                            "", "", sqlException + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalRuleExistOnTimeRange]", "",
                            "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        }// end of catch
        finally {
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
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting isExist=" + isExist);
            }
        }// end of finally
        return isExist;
    }

    /**
     * Method loadC2STransferDetails.
     * This method is to load the details of transfer using the transfer ID.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @return C2STransferVO
     * @throws BTSLBaseException
     */
    public P2PTransferVO loadP2PTransferDetails(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadP2PTransferDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferID=" + p_transferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        P2PTransferVO transferVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT st.transfer_id,st.transfer_date,st.network_code,st.product_code,st.sender_msisdn,");
            selectQueryBuff.append("st.receiver_msisdn,st.transfer_value,st.service_type,st.language,st.country,st.transfer_status,kv.value ");
            selectQueryBuff.append("FROM subscriber_transfers st,key_values kv WHERE st.transfer_id=? AND st.transfer_status=kv.key AND kv.type=?");
            pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
            pstmtSelect.setString(1, p_transferID);
            pstmtSelect.setString(2, PretupsI.P2P_ERRCODE_VALUS);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "selectQueryBuff :" + selectQueryBuff);
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                transferVO = new P2PTransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setProductCode(rs.getString("product_code"));
                transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setServiceType(rs.getString("service_type"));
                transferVO.setLanguage(rs.getString("language"));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setValue(rs.getString("value"));
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }finally{
        	try{
            	if (rs!= null){
            		rs.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }	
        }
        
        return transferVO;
    }

    /**
     * Load the promotional transfer rules cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author gaurav pandey
     */
    public HashMap loadPromotionalTransferRuleCache() throws BTSLBaseException {

        final String methodName = "loadPromotionalTransferRuleCache";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final HashMap map = new HashMap();

        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cd.card_group_set_name setname, cd.status setstatus, cd.language_1_message, ");
        strBuff.append(" cd.language_2_message,  tr.module, tr.network_code, tr.sender_subscriber_type, ");
        strBuff.append(" tr.receiver_subscriber_type, tr.sender_service_class_id, tr.receiver_service_class_id, ");
        strBuff.append(" tr.status,tr.card_group_set_id,  tr.modified_on, tr.modified_by , tr.created_on, ");
        strBuff.append(" tr.start_time,tr.end_time, tr.rule_level, tr.date_range,tr.time_slab,tr.rule_type,tr.allowed_days,tr.allowed_series,tr.denied_series, ");
        strBuff.append(" tr.created_by,tr.sub_service,tr.service_type,stsm.selector_name ,tr.gateway_code FROM transfer_rules tr, ");
        strBuff.append(" card_group_set cd , service_type_selector_mapping stsm  WHERE tr.status<>'N' ");
        strBuff.append(" AND tr.rule_type='P'");
        strBuff.append(" AND cd.status<>'N' AND stsm.status<>'N' AND tr.card_group_set_id=cd.card_group_set_id ");
        strBuff.append(" AND END_TIME >=?");
        strBuff.append(" AND tr.module=cd.module_code AND tr.network_code=cd.network_code ");
        strBuff.append(" AND stsm.service_type=tr.service_type AND stsm.selector_code=tr.sub_service ");
        strBuff.append(" AND stsm.service_type=cd.service_type AND stsm.selector_code=cd.sub_service ");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadTransferRuleCache", "QUERY sqlSelect=" + sqlSelect);
        }

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            final Date date = new Date();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(date));
            rs = pstmt.executeQuery();
            TransferRulesVO rulesVO = null;
            while (rs.next()) {
                rulesVO = new TransferRulesVO();

                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedOnTimestamp(rs.getTimestamp("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setCardGroupSetIDStatus(rs.getString("setstatus"));
                rulesVO.setCardGroupSetName(rs.getString("setname"));
                rulesVO.setCardGroupMessage1(rs.getString("language_1_message"));
                rulesVO.setCardGroupMessage2(rs.getString("language_2_message"));
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setSubServiceTypeIdDes(rs.getString("selector_name"));
                rulesVO.setStartTime(rs.getDate("start_time"));
                rulesVO.setEndTime(rs.getDate("end_time"));
                rulesVO.setRuleLevel(rs.getString("rule_level"));
                rulesVO.setSelectRangeType(rs.getString("date_range"));
                rulesVO.setMultipleSlab(rs.getString("time_slab"));
                rulesVO.setRuleType(rs.getString("rule_type"));
                rulesVO.setAllowedDays(rs.getString("allowed_days"));
                rulesVO.setAllowedSeries(rs.getString("allowed_series"));
                rulesVO.setDeniedSeries(rs.getString("denied_series"));
                rulesVO.setGatewayCode(rs.getString("gateway_code"));
                map.put(rulesVO.getKey(), rulesVO);
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, sqlException + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleCache]", "",
                            "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleCache]", "",
                            "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: networkMap size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Method to update the transfer details
     * 
     * @param p_con
     * @param p_transferVO
     * @return int
     * @throws BTSLBaseException
     * @throws Exception
     * @throws SQLException
     * @throws SQLException
     */
    public int updateVoucherTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        final String methodName = "updateVoucherTransferDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_transferVO:" + p_transferVO.toString());
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            int i = 1;
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE subscriber_transfers SET   ");
            updateQueryBuff.append(" error_code=?, reference_id=?,credit_back_status=?, reconciliation_flag=?, ");
            updateQueryBuff.append(" reconciliation_date=?, reconciliation_by=?, modified_by=?, modified_on=?, transfer_status=?,TRANSFER_VALUE=?  ");
            updateQueryBuff.append(" WHERE transfer_id=? ");
            final String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Insert query:" + updateQuery);
            }
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i, p_transferVO.getErrorCode());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getReferenceID());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getCreditBackStatus());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getReconciliationFlag());
            i++;
            if (p_transferVO.getReconciliationDate() == null) {
                pstmtUpdate.setNull(i, Types.TIMESTAMP);
                i++;
            } else {
                pstmtUpdate.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
                i++;
            }
            pstmtUpdate.setString(i, p_transferVO.getReconciliationBy());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getModifiedBy());
            i++;
            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            i++;
            pstmtUpdate.setString(i, p_transferVO.getTransferStatus());
            i++;
            pstmtUpdate.setLong(i, p_transferVO.getTransferValue());
            i++;
            pstmtUpdate.setString(i, p_transferVO.getTransferID());
            i++;
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = 0;
                updateCount = updateTransferItemDetails(p_con, p_transferVO.getTransferID(), p_transferVO.getTransferItemList());
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, sqlException + sqle.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferDetails]", p_transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            updateCount = 0;
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferDetails]", p_transferVO
                            .getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorWhileProcessing);
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
	 * Method to update the transfer details
	 * @param p_con
	 * @param p_transferVO
	 * @return int
	 * @throws BTSLBaseException
	 * @throws Exception
	 * @throws SQLException
	 * @throws SQLException
	 */
	public int updateVoucherTransferDetails(Connection p_con,P2PTransferVO p_transferVO,VomsVoucherVO _vomsVO) throws BTSLBaseException
	{
		final String methodName = "updateVoucherTransferDetails";
		if(LOG.isDebugEnabled())LOG.debug(methodName,"Entered p_transferVO:"+p_transferVO.toString());
		PreparedStatement pstmtUpdate=null;
		int updateCount=0;
		SenderVO senderVO=null;
		try
		{
			senderVO=(SenderVO)p_transferVO.getSenderVO();
			int i=1;
			StringBuffer updateQueryBuff =new StringBuffer(" UPDATE subscriber_transfers SET   "); 
			updateQueryBuff.append(" error_code=?, reference_id=?,credit_back_status=?, reconciliation_flag=?, "); 
			updateQueryBuff.append(" reconciliation_date=?, reconciliation_by=?, modified_by=?, modified_on=?, transfer_status=?,TRANSFER_VALUE=? ,SUB_SERVICE=? ");
			if(_vomsVO!=null)
			{
			updateQueryBuff.append(" ,VSERIAL_NO=?,VSTATUS=?,VMRP=?,VTALKTIME=?,VEXPIRYDATE=?,VERRORSTATUS=?,VMESSAGE=?");
			}
			updateQueryBuff.append(" WHERE transfer_id=? "); 
			String updateQuery=updateQueryBuff.toString();
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Insert query:"+updateQuery );
			pstmtUpdate = p_con.prepareStatement(updateQuery);
			pstmtUpdate.setString(i++,p_transferVO.getErrorCode());
			pstmtUpdate.setString(i++,p_transferVO.getReferenceID());
			pstmtUpdate.setString(i++,p_transferVO.getCreditBackStatus());
			pstmtUpdate.setString(i++,p_transferVO.getReconciliationFlag());
			if(p_transferVO.getReconciliationDate()==null)
				pstmtUpdate.setNull(i++,Types.TIMESTAMP);
			else
				pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
			pstmtUpdate.setString(i++,p_transferVO.getReconciliationBy());
			pstmtUpdate.setString(i++,p_transferVO.getModifiedBy());
			pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
			pstmtUpdate.setString(i++,p_transferVO.getTransferStatus());
			pstmtUpdate.setLong(i++,PretupsBL.getSystemAmount(p_transferVO.getTransferValueStr()));
			pstmtUpdate.setString(i++,p_transferVO.getSubService());
			if(_vomsVO!=null)
			{
			pstmtUpdate.setString(i++,_vomsVO.getSerialNo());
			pstmtUpdate.setString(i++,_vomsVO.getCurrentStatus());
			pstmtUpdate.setString(i++,Double.toString(_vomsVO.getMRP()));
			pstmtUpdate.setLong(i++,_vomsVO.getTalkTime());
			pstmtUpdate.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(_vomsVO.getExpiryDate()));
			pstmtUpdate.setString(i++,_vomsVO.getLastErrorMessage());
			pstmtUpdate.setString(i++,_vomsVO.getMessage());
			}
			pstmtUpdate.setString(i++,p_transferVO.getTransferID());
			
			updateCount = pstmtUpdate.executeUpdate();
			 if (updateCount > 0)
            {
                updateCount = 0;
                updateCount = updateTransferItemDetails(p_con, p_transferVO.getTransferID(),p_transferVO.getTransferItemList());
            }
			return updateCount;
		}//end of try
		catch (SQLException sqle)
		{
			LOG.error(methodName,"SQLException "+sqle.getMessage());
			updateCount=0;
			LOG.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			LOG.error(methodName,"Exception "+e.getMessage());
			updateCount=0;
			LOG.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[updateTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}//end of catch
		finally
		{
			try{if(pstmtUpdate!=null) pstmtUpdate.close();}catch(Exception e){LOG.errorTrace(methodName,e);}
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting updateCount="+updateCount);
		 }//end of finally
	}
/**
	 * Methods adds the transfers related details, calls another method to insert the items
	 * @param p_con
	 * @param p_transferVO
	 * @return int
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws Exception
	 */
	public int addVoucherTransferDetails(Connection p_con,P2PTransferVO p_transferVO) throws BTSLBaseException
	{
		final String methodName = "addVoucherTransferDetails";
		if(LOG.isDebugEnabled())LOG.debug(methodName,"Entered p_transferVO:"+p_transferVO.toString());
		PreparedStatement pstmtInsert=null;
		int addCount=0;
		SenderVO senderVO=null;
		try
		{
			senderVO=(SenderVO)p_transferVO.getSenderVO();
			ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO();
			int i=1;
			StringBuffer insertQueryBuff =new StringBuffer(" INSERT INTO subscriber_transfers(transfer_id, transfer_date, transfer_date_time, network_code, sender_id, "); 
			insertQueryBuff.append(" product_code, sender_msisdn, receiver_msisdn, receiver_network_code, transfer_value, "); 
			insertQueryBuff.append(" error_code, request_gateway_type, request_gateway_code,reference_id, payment_method_type, "); 
			insertQueryBuff.append(" service_type, pin_sent_to_msisdn, language, country, skey, skey_generation_time, "); 
			insertQueryBuff.append(" skey_sent_to_msisdn, request_through_queue, credit_back_status, quantity, reconciliation_flag, "); 
			insertQueryBuff.append(" reconciliation_date, reconciliation_by, created_by, created_on, modified_by, modified_on, transfer_status, "); 
			insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_access_fee,sender_tax1_type,sender_tax1_rate,sender_tax1_value, ");
			insertQueryBuff.append(" sender_tax2_type,sender_tax2_rate,sender_tax2_value,sender_transfer_value, ");
			insertQueryBuff.append(" receiver_access_fee,receiver_tax1_type,receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
			insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
			insertQueryBuff.append(" card_group_code,receiver_valperiod_type,sub_service,start_time,end_time,transfer_category");
			// added for cell ID and switch ID
			insertQueryBuff.append(",cell_id,switch_id,VPIN)");
			insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? "); 
			// added for cell id and switch id
			insertQueryBuff.append(" ,?,?,?)");
			String insertQuery=insertQueryBuff.toString();
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Insert query:"+insertQuery );
			pstmtInsert = p_con.prepareStatement(insertQuery);
			pstmtInsert.setString(i++,BTSLUtil.NullToString(p_transferVO.getTransferID()));
			pstmtInsert.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getTransferDate()));
			pstmtInsert.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_transferVO.getTransferDateTime()));
			pstmtInsert.setString(i++,senderVO.getNetworkCode());
			pstmtInsert.setString(i++,senderVO.getUserID());
			pstmtInsert.setString(i++,p_transferVO.getProductCode());
			pstmtInsert.setString(i++,senderVO.getMsisdn());
			pstmtInsert.setString(i++,receiverVO.getMsisdn());
			pstmtInsert.setString(i++,receiverVO.getNetworkCode());
			pstmtInsert.setLong(i++,p_transferVO.getTransferValue());
			pstmtInsert.setString(i++,p_transferVO.getErrorCode());
			pstmtInsert.setString(i++,p_transferVO.getRequestGatewayType());
			pstmtInsert.setString(i++,p_transferVO.getRequestGatewayCode());
			pstmtInsert.setString(i++,p_transferVO.getReferenceID());
			pstmtInsert.setString(i++,p_transferVO.getPaymentMethodType());
			pstmtInsert.setString(i++,p_transferVO.getServiceType());
			pstmtInsert.setString(i++,p_transferVO.getPinSentToMsisdn());
			pstmtInsert.setString(i++,p_transferVO.getLanguage());
			pstmtInsert.setString(i++,p_transferVO.getCountry());
			pstmtInsert.setLong(i++,p_transferVO.getSkey());
			if(p_transferVO.getSkeyGenerationTime()==null)
				pstmtInsert.setNull(i++,Types.TIMESTAMP);
			else
				pstmtInsert.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_transferVO.getSkeyGenerationTime()));
			pstmtInsert.setString(i++,p_transferVO.getSkeySentToMsisdn());
			pstmtInsert.setString(i++,p_transferVO.getRequestThroughQueue());
			pstmtInsert.setString(i++,p_transferVO.getCreditBackStatus());
			pstmtInsert.setLong(i++,p_transferVO.getQuantity());
			pstmtInsert.setString(i++,p_transferVO.getReconciliationFlag());
			pstmtInsert.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
			pstmtInsert.setString(i++,p_transferVO.getReconciliationBy());
			pstmtInsert.setString(i++,p_transferVO.getCreatedBy());
			pstmtInsert.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_transferVO.getCreatedOn()));
			pstmtInsert.setString(i++,p_transferVO.getModifiedBy());
			pstmtInsert.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
			pstmtInsert.setString(i++,p_transferVO.getTransferStatus());
			pstmtInsert.setString(i++,p_transferVO.getCardGroupSetID());
			pstmtInsert.setString(i++,p_transferVO.getVersion());
			pstmtInsert.setString(i++,p_transferVO.getCardGroupID());
			pstmtInsert.setLong(i++,p_transferVO.getSenderAccessFee());
			pstmtInsert.setString(i++,p_transferVO.getSenderTax1Type());
			pstmtInsert.setDouble(i++,p_transferVO.getSenderTax1Rate());
			pstmtInsert.setLong(i++,p_transferVO.getSenderTax1Value());
			pstmtInsert.setString(i++,p_transferVO.getSenderTax2Type());
			pstmtInsert.setDouble(i++,p_transferVO.getSenderTax2Rate());
			pstmtInsert.setLong(i++,p_transferVO.getSenderTax2Value());
			pstmtInsert.setLong(i++,p_transferVO.getSenderTransferValue());
			pstmtInsert.setLong(i++,p_transferVO.getReceiverAccessFee());
			pstmtInsert.setString(i++,p_transferVO.getReceiverTax1Type());
			pstmtInsert.setDouble(i++,p_transferVO.getReceiverTax1Rate());
			pstmtInsert.setLong(i++,p_transferVO.getReceiverTax1Value());
			pstmtInsert.setString(i++,p_transferVO.getReceiverTax2Type());
			pstmtInsert.setDouble(i++,p_transferVO.getReceiverTax2Rate());
			pstmtInsert.setLong(i++,p_transferVO.getReceiverTax2Value());
			pstmtInsert.setInt(i++,p_transferVO.getReceiverValidity());
			pstmtInsert.setLong(i++,p_transferVO.getReceiverTransferValue());
			pstmtInsert.setLong(i++,p_transferVO.getReceiverBonusValue());
			pstmtInsert.setLong(i++,p_transferVO.getReceiverGracePeriod());
			pstmtInsert.setInt(i++,p_transferVO.getReceiverBonusValidity());
			pstmtInsert.setString(i++,p_transferVO.getCardGroupCode());
			pstmtInsert.setString(i++,p_transferVO.getReceiverValPeriodType());
			pstmtInsert.setString(i++,p_transferVO.getSubService());
			pstmtInsert.setLong(i++,p_transferVO.getRequestStartTime());
			pstmtInsert.setLong(i++,System.currentTimeMillis());
			pstmtInsert.setString(i++,p_transferVO.getTransferCategory());
			// added for cell id and switch id
			pstmtInsert.setString(i++,p_transferVO.getCellId());
			pstmtInsert.setString(i++,p_transferVO.getSwitchId());
			if(BTSLUtil.isNullString(p_transferVO.getVoucherCode()))
			{
			pstmtInsert.setString(i++,"");
			}else{
				try{
					pstmtInsert.setString(i++,BTSLUtil.encryptText(p_transferVO.getVoucherCode()));
				}catch (Exception e) {
					pstmtInsert.setString(i++,p_transferVO.getVoucherCode());
				}
			}
			addCount = pstmtInsert.executeUpdate();
			if(addCount>0)
			{
				if(p_transferVO.getTransferItemList()!=null && !p_transferVO.getTransferItemList().isEmpty() && !p_transferVO.getTransferItemList().isEmpty())
				{
					addCount=0;
					addCount=addTransferItemDetails(p_con,p_transferVO.getTransferID(),p_transferVO.getTransferItemList());
				}
			}
			return addCount;
		}//end of try
		catch (SQLException sqle)
		{
			LOG.error(methodName,"SQLException "+sqle.getMessage());
			addCount=0;
			LOG.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[addTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			LOG.error(methodName,"Exception "+e.getMessage());
			addCount=0;
			LOG.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO[addTransferDetails]",p_transferVO.getTransferID(),senderVO.getMsisdn(),senderVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}//end of catch
		finally
		{
			try{if(pstmtInsert!=null) pstmtInsert.close();}catch(Exception e){LOG.errorTrace(methodName,e);}
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting addCount="+addCount);
		 }//end of finally
	}
	
	/**
	 * Method getIDList.
	 * This method is to load the Transaction Id on the bases of Reference Id
	 * @param p_con Connection
	 * @param _IDlist List
	 * @return _finalList
	 * @throws BTSLBaseException
	 */
		public ArrayList<String> getIDList(Connection p_con,ArrayList<String> p_IDlist) throws BTSLBaseException
		{
		final String methodName="getIDList";
    	 if (LOG.isDebugEnabled())
             LOG.debug(methodName, "Entered   ArrayList size=" + p_IDlist.size());
    	 ArrayList<String> finalList=new ArrayList<String>();
    	 PreparedStatement pstmt = null;
         ResultSet rs = null;
         String sqlSelect = "SELECT ti.transfer_id FROM TRANSFER_ITEMS ti WHERE ti.INTERFACE_REFERENCE_ID=?";
         if (LOG.isDebugEnabled())
             LOG.debug(methodName, "select Query=" +sqlSelect);
         try
         {
        	 for(int i=0;i<p_IDlist.size();i++)
        	 {
        		 pstmt = p_con.prepareStatement(sqlSelect); 
        		 pstmt.setString(1,p_IDlist.get(i));
        		 rs = pstmt.executeQuery();
        		 if(rs.next())
            		finalList.add(rs.getString("transfer_id"));	
            }
           
         }
         catch (SQLException sqle)
 		 {
 			LOG.error(methodName,"SQLException "+sqle.getMessage());
 			LOG.errorTrace(methodName, sqle);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
 		}//end of catch
 		catch (Exception e)
 		{
 			LOG.error(methodName,"Exception "+e.getMessage());
 			LOG.errorTrace(methodName, e);
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"TransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.processing");
 		}//end of catch
 		finally
 		{
 			try{if(rs!=null) rs.close();}catch(Exception e){
 				LOG.error(methodName, "Exception " + e.getMessage());
 	            LOG.errorTrace(methodName, e);
 			}
 			try{if(pstmt!=null) pstmt.close();}catch(Exception e){
 				LOG.error(methodName, "Exception " + e.getMessage());
 	            LOG.errorTrace(methodName, e);
 			}
 			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting finalList size="+finalList.size());
 		 }//end of finally
 	    return finalList;
 	}

}
