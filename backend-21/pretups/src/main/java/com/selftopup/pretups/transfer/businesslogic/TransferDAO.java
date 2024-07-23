package com.selftopup.pretups.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;

import com.btsl.util.MessageResources;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.inter.module.InterfaceErrorCodesI;
import com.selftopup.pretups.network.businesslogic.NetworkDAO;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.user.businesslogic.UserVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

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
     * Field _log.
     */
    private Log _log = LogFactory.getLog(TransferDAO.class.getName());

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
        if (_log.isDebugEnabled())
            _log.debug("addTransferDetails", "Entered p_transferVO:" + p_transferVO.toString());
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            int i = 1;
            StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO subscriber_transfers(transfer_id, transfer_date, transfer_date_time, network_code, sender_id, ");
            insertQueryBuff.append(" product_code, sender_msisdn, receiver_msisdn, receiver_network_code, transfer_value, ");
            insertQueryBuff.append(" error_code, request_gateway_type, request_gateway_code,reference_id, payment_method_type, ");
            insertQueryBuff.append(" service_type, pin_sent_to_msisdn, language, country, skey, skey_generation_time, ");
            insertQueryBuff.append(" skey_sent_to_msisdn, request_through_queue, credit_back_status, quantity, reconciliation_flag, ");
            insertQueryBuff.append(" reconciliation_date, reconciliation_by, created_by, created_on, modified_by, modified_on, transfer_status, ");
            insertQueryBuff.append(" card_group_set_id,version,card_group_id,sender_access_fee,sender_tax1_type,sender_tax1_rate,sender_tax1_value, ");
            insertQueryBuff.append(" sender_tax2_type,sender_tax2_rate,sender_tax2_value,sender_transfer_value, ");
            insertQueryBuff.append(" receiver_access_fee,receiver_tax1_type,receiver_tax1_rate,receiver_tax1_value,receiver_tax2_type,receiver_tax2_rate,receiver_tax2_value, ");
            insertQueryBuff.append(" receiver_validity,receiver_transfer_value,receiver_bonus_value,receiver_grace_period,receiver_bonus_validity, ");
            insertQueryBuff.append(" card_group_code,receiver_valperiod_type,sub_service,start_time,end_time,transfer_category,CARD_REFERENCE");
            // added for cell ID and switch ID
            insertQueryBuff.append(",cell_id,switch_id)");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
            // added for cell id and switch id
            insertQueryBuff.append(" ,?,?)");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addTransferDetails", "Insert query:" + insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i++, BTSLUtil.NullToString(p_transferVO.getTransferID()));
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getTransferDate()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getTransferDateTime()));
            pstmtInsert.setString(i++, senderVO.getNetworkCode());
            pstmtInsert.setString(i++, senderVO.getUserID());
            pstmtInsert.setString(i++, p_transferVO.getProductCode());
            pstmtInsert.setString(i++, senderVO.getMsisdn());
            pstmtInsert.setString(i++, receiverVO.getMsisdn());
            pstmtInsert.setString(i++, receiverVO.getNetworkCode());
            pstmtInsert.setLong(i++, p_transferVO.getTransferValue());
            pstmtInsert.setString(i++, p_transferVO.getErrorCode());
            pstmtInsert.setString(i++, p_transferVO.getRequestGatewayType());
            pstmtInsert.setString(i++, p_transferVO.getRequestGatewayCode());
            pstmtInsert.setString(i++, p_transferVO.getReferenceID());
            pstmtInsert.setString(i++, p_transferVO.getPaymentMethodType());
            pstmtInsert.setString(i++, p_transferVO.getServiceType());
            pstmtInsert.setString(i++, p_transferVO.getPinSentToMsisdn());
            pstmtInsert.setString(i++, p_transferVO.getLanguage());
            pstmtInsert.setString(i++, p_transferVO.getCountry());
            pstmtInsert.setLong(i++, p_transferVO.getSkey());
            if (p_transferVO.getSkeyGenerationTime() == null)
                pstmtInsert.setNull(i++, Types.TIMESTAMP);
            else
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getSkeyGenerationTime()));
            pstmtInsert.setString(i++, p_transferVO.getSkeySentToMsisdn());
            pstmtInsert.setString(i++, p_transferVO.getRequestThroughQueue());
            pstmtInsert.setString(i++, p_transferVO.getCreditBackStatus());
            pstmtInsert.setLong(i++, p_transferVO.getQuantity());
            pstmtInsert.setString(i++, p_transferVO.getReconciliationFlag());
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
            pstmtInsert.setString(i++, p_transferVO.getReconciliationBy());
            pstmtInsert.setString(i++, p_transferVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_transferVO.getModifiedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_transferVO.getTransferStatus());
            pstmtInsert.setString(i++, p_transferVO.getCardGroupSetID());
            pstmtInsert.setString(i++, p_transferVO.getVersion());
            pstmtInsert.setString(i++, p_transferVO.getCardGroupID());
            pstmtInsert.setLong(i++, p_transferVO.getSenderAccessFee());
            pstmtInsert.setString(i++, p_transferVO.getSenderTax1Type());
            pstmtInsert.setDouble(i++, p_transferVO.getSenderTax1Rate());
            pstmtInsert.setLong(i++, p_transferVO.getSenderTax1Value());
            pstmtInsert.setString(i++, p_transferVO.getSenderTax2Type());
            pstmtInsert.setDouble(i++, p_transferVO.getSenderTax2Rate());
            pstmtInsert.setLong(i++, p_transferVO.getSenderTax2Value());
            pstmtInsert.setLong(i++, p_transferVO.getSenderTransferValue());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverAccessFee());
            pstmtInsert.setString(i++, p_transferVO.getReceiverTax1Type());
            pstmtInsert.setDouble(i++, p_transferVO.getReceiverTax1Rate());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverTax1Value());
            pstmtInsert.setString(i++, p_transferVO.getReceiverTax2Type());
            pstmtInsert.setDouble(i++, p_transferVO.getReceiverTax2Rate());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverTax2Value());
            pstmtInsert.setInt(i++, p_transferVO.getReceiverValidity());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverTransferValue());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverBonusValue());
            pstmtInsert.setLong(i++, p_transferVO.getReceiverGracePeriod());
            pstmtInsert.setInt(i++, p_transferVO.getReceiverBonusValidity());
            pstmtInsert.setString(i++, p_transferVO.getCardGroupCode());
            pstmtInsert.setString(i++, p_transferVO.getReceiverValPeriodType());
            pstmtInsert.setString(i++, p_transferVO.getSubService());
            pstmtInsert.setLong(i++, p_transferVO.getRequestStartTime());
            pstmtInsert.setLong(i++, System.currentTimeMillis());
            pstmtInsert.setString(i++, p_transferVO.getTransferCategory());
            pstmtInsert.setString(i++, p_transferVO.getCardReferenceNo());
            // added for cell id and switch id
            pstmtInsert.setString(i++, p_transferVO.getCellId());
            pstmtInsert.setString(i++, p_transferVO.getSwitchId());

            addCount = pstmtInsert.executeUpdate();

            if (addCount > 0) {
                if (p_transferVO.getTransferItemList() != null && !p_transferVO.getTransferItemList().isEmpty() && p_transferVO.getTransferItemList().size() > 0) {
                    addCount = 0;
                    addCount = addTransferItemDetails(p_con, p_transferVO.getTransferID(), p_transferVO.getTransferItemList());
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("addTransferDetails", "SQLException " + sqle.getMessage());
            addCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addTransferDetails", "Exception " + e.getMessage());
            addCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addTransferDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addTransferDetails", "Exiting addCount=" + addCount);
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
        if (_log.isDebugEnabled())
            _log.debug("addValExtTransferDetails", "Entered p_transferVO:" + p_transferVO.toString());
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            ReceiverVO receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
            int i = 1;
            StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO subscriber_transfers(transfer_id, transfer_date, transfer_date_time, network_code, sender_id, ");
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
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addValExtTransferDetails", "Insert query:" + insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i++, BTSLUtil.NullToString(p_transferVO.getValExtTransferID()));
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getTransferDate()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getTransferDateTime()));
            pstmtInsert.setString(i++, senderVO.getNetworkCode());
            pstmtInsert.setString(i++, senderVO.getUserID());
            pstmtInsert.setString(i++, p_transferVO.getProductCode());
            pstmtInsert.setString(i++, senderVO.getMsisdn());
            pstmtInsert.setString(i++, receiverVO.getMsisdn());
            pstmtInsert.setString(i++, receiverVO.getNetworkCode());
            pstmtInsert.setLong(i++, p_transferVO.getFeeForValidityExtention());// transfer
                                                                                // value

            pstmtInsert.setString(i++, p_transferVO.getErrorCode());
            pstmtInsert.setString(i++, p_transferVO.getRequestGatewayType());
            pstmtInsert.setString(i++, p_transferVO.getRequestGatewayCode());
            pstmtInsert.setString(i++, p_transferVO.getReferenceID());
            pstmtInsert.setString(i++, p_transferVO.getPaymentMethodType());
            pstmtInsert.setString(i++, p_transferVO.getServiceType());
            pstmtInsert.setString(i++, p_transferVO.getPinSentToMsisdn());
            pstmtInsert.setString(i++, p_transferVO.getLanguage());
            pstmtInsert.setString(i++, p_transferVO.getCountry());
            pstmtInsert.setLong(i++, p_transferVO.getSkey());
            if (p_transferVO.getSkeyGenerationTime() == null)
                pstmtInsert.setNull(i++, Types.TIMESTAMP);
            else
                pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getSkeyGenerationTime()));
            pstmtInsert.setString(i++, p_transferVO.getSkeySentToMsisdn());
            pstmtInsert.setString(i++, p_transferVO.getRequestThroughQueue());
            pstmtInsert.setString(i++, p_transferVO.getCreditBackStatus());
            pstmtInsert.setLong(i++, 0);// quantity
            pstmtInsert.setString(i++, p_transferVO.getReconciliationFlag());
            pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
            pstmtInsert.setString(i++, p_transferVO.getReconciliationBy());
            pstmtInsert.setString(i++, p_transferVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_transferVO.getModifiedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_transferVO.getTransferStatus());
            pstmtInsert.setString(i++, p_transferVO.getCardGroupSetID());
            pstmtInsert.setString(i++, p_transferVO.getVersion());
            pstmtInsert.setString(i++, p_transferVO.getCardGroupID());
            pstmtInsert.setLong(i++, 0);// sender access fee
            pstmtInsert.setString(i++, "");// SenderTax1 Type
            pstmtInsert.setDouble(i++, 0);// SenderTax1 Rate
            pstmtInsert.setLong(i++, 0);// SenderTax1 Value
            pstmtInsert.setString(i++, "");// SenderTax2 Type
            pstmtInsert.setDouble(i++, 0);// SenderTax2 Rate
            pstmtInsert.setLong(i++, 0);// SenderTax2 Value
            pstmtInsert.setLong(i++, p_transferVO.getFeeForValidityExtention());// sender
                                                                                // transfer
                                                                                // value

            pstmtInsert.setLong(i++, 0);// Receiver Access Fee
            pstmtInsert.setString(i++, "");// Receiver Tax1 Type
            pstmtInsert.setDouble(i++, 0);// Receiver Tax1 Rate
            pstmtInsert.setLong(i++, 0);// Receiver Tax1 Value
            pstmtInsert.setString(i++, "");// Receiver Tax2 Type
            pstmtInsert.setDouble(i++, 0);// //Receiver Tax2 Rate
            pstmtInsert.setLong(i++, 0);// Receiver Tax2 Value
            pstmtInsert.setInt(i++, p_transferVO.getValidityDaysToExtend());
            pstmtInsert.setLong(i++, 0);// Receiver Transfer Value
            pstmtInsert.setLong(i++, 0);// Receiver Bonus Value
            pstmtInsert.setLong(i++, p_transferVO.getReceiverGracePeriod());
            pstmtInsert.setInt(i++, 0);// Receiver Bonus Validity
            pstmtInsert.setString(i++, p_transferVO.getCardGroupCode());
            pstmtInsert.setString(i++, p_transferVO.getReceiverValPeriodType());
            pstmtInsert.setString(i++, p_transferVO.getSubService());
            pstmtInsert.setLong(i++, p_transferVO.getRequestStartTime());
            pstmtInsert.setLong(i++, System.currentTimeMillis());
            pstmtInsert.setString(i++, p_transferVO.getTransferCategory());
            addCount = pstmtInsert.executeUpdate();
            if (addCount > 0) {
                if (p_transferVO.getTransferItemList() != null && !p_transferVO.getTransferItemList().isEmpty() && p_transferVO.getTransferItemList().size() > 0) {
                    addCount = 0;
                    addCount = addValExtTransferItemDetails(p_con, p_transferVO, p_transferVO.getTransferItemList());
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("addValExtTransferDetails", "SQLException " + sqle.getMessage());
            addCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addValExtTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addValExtTransferDetails", "Exception " + e.getMessage());
            addCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addValExtTransferDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addValExtTransferDetails", "Exiting addCount=" + addCount);
        }// end of finally
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
    public int updateTransferDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateTransferDetails", "Entered p_transferVO:" + p_transferVO.toString());
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE subscriber_transfers SET   ");
            updateQueryBuff.append(" error_code=?, reference_id=?,credit_back_status=?, reconciliation_flag=?, ");
            updateQueryBuff.append(" reconciliation_date=?, reconciliation_by=?, modified_by=?, modified_on=?, transfer_status=? ");
            updateQueryBuff.append(" WHERE transfer_id=? ");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateTransferDetails", "Insert query:" + updateQuery);
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i++, p_transferVO.getErrorCode());
            pstmtUpdate.setString(i++, p_transferVO.getReferenceID());
            pstmtUpdate.setString(i++, p_transferVO.getCreditBackStatus());
            pstmtUpdate.setString(i++, p_transferVO.getReconciliationFlag());
            if (p_transferVO.getReconciliationDate() == null)
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);
            else
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
            pstmtUpdate.setString(i++, p_transferVO.getReconciliationBy());
            pstmtUpdate.setString(i++, p_transferVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_transferVO.getTransferStatus());
            pstmtUpdate.setString(i++, p_transferVO.getTransferID());
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = 0;
                updateCount = updateTransferItemDetails(p_con, p_transferVO.getTransferID(), p_transferVO.getTransferItemList());
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateTransferDetails", "SQLException " + sqle.getMessage());
            updateCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateTransferDetails", "Exception " + e.getMessage());
            updateCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateTransferDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateTransferDetails", "Exiting updateCount=" + updateCount);
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
        if (_log.isDebugEnabled())
            _log.debug("updateValExtTransferDetails", "Entered p_transferVO:" + p_transferVO.toString());
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        SenderVO senderVO = null;
        try {
            senderVO = (SenderVO) p_transferVO.getSenderVO();
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE subscriber_transfers SET   ");
            updateQueryBuff.append(" error_code=?, reference_id=?,credit_back_status=?, reconciliation_flag=?, ");
            updateQueryBuff.append(" reconciliation_date=?, reconciliation_by=?, modified_by=?, modified_on=?, transfer_status=? ");
            updateQueryBuff.append(" WHERE transfer_id=? ");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateValExtTransferDetails", "Insert query:" + updateQuery);
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i++, p_transferVO.getErrorCode());
            pstmtUpdate.setString(i++, p_transferVO.getReferenceID());
            pstmtUpdate.setString(i++, p_transferVO.getCreditBackStatus());
            pstmtUpdate.setString(i++, p_transferVO.getReconciliationFlag());
            if (p_transferVO.getReconciliationDate() == null)
                pstmtUpdate.setNull(i++, Types.TIMESTAMP);
            else
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_transferVO.getReconciliationDate()));
            pstmtUpdate.setString(i++, p_transferVO.getReconciliationBy());
            pstmtUpdate.setString(i++, p_transferVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_transferVO.getTransferStatus());
            pstmtUpdate.setString(i++, p_transferVO.getValExtTransferID());
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = 0;
                updateCount = updateValExtTransferItemDetails(p_con, p_transferVO);
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateValExtTransferDetails", "SQLException " + sqle.getMessage());
            updateCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateValExtTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateValExtTransferDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateValExtTransferDetails", "Exception " + e.getMessage());
            updateCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateValExtTransferDetails]", p_transferVO.getTransferID(), senderVO.getMsisdn(), senderVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateValExtTransferDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateValExtTransferDetails", "Exiting updateCount=" + updateCount);
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
        if (_log.isDebugEnabled())
            _log.debug("addTransferItemDetails", p_transferID, "Entered p_transferItemList:" + transferItemsList);
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            TransferItemVO transferItemVO = null;
            int i = 1;
            int itemCount = 1;
            StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO transfer_items (transfer_id, sno,prefix_id,msisdn, entry_date, request_value, previous_balance, post_balance, user_type, transfer_type, entry_type, ");
            insertQueryBuff.append(" validation_status, update_status, service_class_id,protocol_status,account_status, ");
            insertQueryBuff.append(" transfer_value, interface_type, interface_id, interface_response_code, ");
            insertQueryBuff.append(" interface_reference_id, subscriber_type, service_class_code, msisdn_previous_expiry, ");
            insertQueryBuff.append(" msisdn_new_expiry, transfer_status, transfer_date, transfer_date_time,entry_date_time,first_call,reference_id");
            insertQueryBuff.append(",service_provider_name)");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addTransferItemDetails", "Insert query:" + insertQuery);

            pstmtInsert = p_con.prepareStatement(insertQuery);

            if (transferItemsList != null && transferItemsList.size() > 0) {
                for (int j = 0, k = transferItemsList.size(); j < k; j++) {
                    transferItemVO = (TransferItemVO) transferItemsList.get(j);
                    addCount = 0;
                    i = 1;
                    if (pstmtInsert != null)
                        pstmtInsert.clearParameters();
                    if (_log.isDebugEnabled())
                        _log.debug("addTransferItemDetails", "transferItemVO:" + transferItemVO.toString());
                    pstmtInsert.setString(i++, transferItemVO.getTransferID());
                    pstmtInsert.setInt(i++, transferItemVO.getSNo());
                    pstmtInsert.setLong(i++, transferItemVO.getPrefixID());
                    pstmtInsert.setString(i++, transferItemVO.getMsisdn());
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getEntryDate()));
                    pstmtInsert.setLong(i++, transferItemVO.getRequestValue());
                    pstmtInsert.setLong(i++, transferItemVO.getPreviousBalance());
                    pstmtInsert.setLong(i++, transferItemVO.getPostBalance());
                    pstmtInsert.setString(i++, transferItemVO.getUserType());
                    pstmtInsert.setString(i++, transferItemVO.getTransferType());
                    pstmtInsert.setString(i++, transferItemVO.getEntryType());
                    pstmtInsert.setString(i++, transferItemVO.getValidationStatus());
                    pstmtInsert.setString(i++, transferItemVO.getUpdateStatus());
                    pstmtInsert.setString(i++, transferItemVO.getServiceClass());
                    pstmtInsert.setString(i++, transferItemVO.getProtocolStatus());
                    pstmtInsert.setString(i++, transferItemVO.getAccountStatus());
                    pstmtInsert.setLong(i++, transferItemVO.getTransferValue());
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceType());
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceID());
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceResponseCode());
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceReferenceID());
                    pstmtInsert.setString(i++, transferItemVO.getSubscriberType());
                    pstmtInsert.setString(i++, transferItemVO.getServiceClassCode());
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));
                    pstmtInsert.setString(i++, transferItemVO.getTransferStatus());
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getTransferDate()));
                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getTransferDateTime()));
                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getEntryDateTime()));
                    pstmtInsert.setString(i++, transferItemVO.getFirstCall());
                    pstmtInsert.setString(i++, transferItemVO.getReferenceID());
                    // For Service Provider Information
                    pstmtInsert.setString(i++, BTSLUtil.NullToString(transferItemVO.getServiceProviderName()));

                    addCount = pstmtInsert.executeUpdate();
                    itemCount = itemCount + 1;
                    if (addCount <= 0) {
                        throw new BTSLBaseException(this, "addTransferItemDetails", "error.general.sql.processing");
                    }
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("addTransferItemDetails", "SQLException " + sqle.getMessage());
            addCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferItemDetails]", p_transferID, "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addTransferItemDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addTransferItemDetails", "Exception " + e.getMessage());
            addCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferItemDetails]", p_transferID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addTransferItemDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addTransferItemDetails", "Exiting addCount=" + addCount);
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
    public int addValExtTransferItemDetails(Connection p_con, TransferVO p_transferVO, ArrayList p_transferItemsList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addValExtTransferItemDetails", p_transferVO.getValExtTransferID(), "Entered p_transferItemList:" + p_transferItemsList);
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        try {
            TransferItemVO transferItemVO = null;
            int i = 1;
            int itemCount = 1;
            StringBuffer insertQueryBuff = new StringBuffer(" INSERT INTO transfer_items (transfer_id, sno,prefix_id,msisdn, entry_date, request_value, previous_balance, post_balance, user_type, transfer_type, entry_type, ");
            insertQueryBuff.append(" validation_status, update_status, service_class_id,protocol_status,account_status, ");
            insertQueryBuff.append(" transfer_value, interface_type, interface_id, interface_response_code, ");
            insertQueryBuff.append(" interface_reference_id, subscriber_type, service_class_code, msisdn_previous_expiry, ");
            insertQueryBuff.append(" msisdn_new_expiry, transfer_status, transfer_date, transfer_date_time,entry_date_time,first_call,reference_id) ");
            insertQueryBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addValExtTransferItemDetails", "Insert query:" + insertQuery);

            pstmtInsert = p_con.prepareStatement(insertQuery);

            if (p_transferItemsList != null && p_transferItemsList.size() > 0) {
                for (int j = 0, k = p_transferItemsList.size(); j < k; j++) {
                    transferItemVO = (TransferItemVO) p_transferItemsList.get(j);
                    addCount = 0;
                    i = 1;
                    if (pstmtInsert != null)
                        pstmtInsert.clearParameters();
                    if (_log.isDebugEnabled())
                        _log.debug("addValExtTransferItemDetails", "transferItemVO:" + transferItemVO.toString());
                    pstmtInsert.setString(i++, p_transferVO.getValExtTransferID());
                    pstmtInsert.setInt(i++, transferItemVO.getSNo());
                    pstmtInsert.setLong(i++, transferItemVO.getPrefixID());
                    pstmtInsert.setString(i++, transferItemVO.getMsisdn());
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getEntryDate()));
                    pstmtInsert.setLong(i++, transferItemVO.getRequestValue());
                    pstmtInsert.setLong(i++, transferItemVO.getPreviousBalance());
                    pstmtInsert.setLong(i++, transferItemVO.getPostBalance());
                    pstmtInsert.setString(i++, transferItemVO.getUserType());
                    pstmtInsert.setString(i++, transferItemVO.getTransferType());
                    pstmtInsert.setString(i++, transferItemVO.getEntryType());
                    pstmtInsert.setString(i++, transferItemVO.getValidationStatus());
                    pstmtInsert.setString(i++, transferItemVO.getUpdateStatus());
                    pstmtInsert.setString(i++, transferItemVO.getServiceClass());
                    pstmtInsert.setString(i++, transferItemVO.getProtocolStatus());
                    pstmtInsert.setString(i++, transferItemVO.getAccountStatus());
                    if (PretupsI.USER_TYPE_SENDER.equals(transferItemVO.getUserType()))
                        pstmtInsert.setLong(i++, p_transferVO.getFeeForValidityExtention());

                    else
                        pstmtInsert.setLong(i++, 0);// transfer value for
                                                    // receiver
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceType());
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceID());
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceResponseCode());
                    pstmtInsert.setString(i++, transferItemVO.getInterfaceReferenceID());
                    pstmtInsert.setString(i++, transferItemVO.getSubscriberType());
                    pstmtInsert.setString(i++, transferItemVO.getServiceClassCode());
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));// To
                                                                                                             // be
                                                                                                             // checked
                    pstmtInsert.setString(i++, transferItemVO.getTransferStatus());
                    pstmtInsert.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getTransferDate()));
                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getTransferDateTime()));
                    pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferItemVO.getEntryDateTime()));
                    pstmtInsert.setString(i++, transferItemVO.getFirstCall());
                    pstmtInsert.setString(i++, p_transferVO.getValExtTransferID());
                    addCount = pstmtInsert.executeUpdate();
                    itemCount = itemCount + 1;
                    if (addCount <= 0) {
                        throw new BTSLBaseException(this, "addValExtTransferItemDetails", "error.general.sql.processing");
                    }
                }
            }
            return addCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("addValExtTransferItemDetails", "SQLException " + sqle.getMessage());
            addCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferItemDetails]", p_transferVO.getValExtTransferID(), "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addValExtTransferItemDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addValExtTransferItemDetails", "Exception " + e.getMessage());
            addCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addValExtTransferItemDetails]", p_transferVO.getValExtTransferID(), "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addValExtTransferItemDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addValExtTransferItemDetails", "Exiting addCount=" + addCount);
        }// end of finally
    }

    /**
     * Method to update the transfer items details
     * 
     * @param p_con
     * @param p_transferID
     * @param p_transferItemsList
     * @return int
     * @throws BTSLBaseException
     */
    public int updateTransferItemDetails(Connection p_con, String p_transferID, ArrayList p_transferItemsList) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateTransferItemDetails", p_transferID, "Entered p_transferItemsList size:" + p_transferItemsList.size());
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        TransferItemVO transferItemVO = null;
        try {
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE transfer_items SET previous_balance=?, post_balance=?, ");
            updateQueryBuff.append(" update_status=?, interface_response_code=?, ");
            updateQueryBuff.append(" interface_reference_id=?, msisdn_previous_expiry=?, ");
            updateQueryBuff.append(" msisdn_new_expiry=?, transfer_status=?, ");
            updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=? , ");
            updateQueryBuff.append(" reference_id=? ");
            updateQueryBuff.append(" WHERE  transfer_id=? AND msisdn=? AND sno=?");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateTransferItemDetails", "Insert query:" + updateQuery);

            pstmtUpdate = p_con.prepareStatement(updateQuery);

            for (int j = 0; j < p_transferItemsList.size(); j++) {
                i = 1;
                transferItemVO = (TransferItemVO) p_transferItemsList.get(j);
                updateCount = 0;
                if (pstmtUpdate != null)
                    pstmtUpdate.clearParameters();
                if (_log.isDebugEnabled())
                    _log.debug("updateTransferItemDetails", "transferItemVO:" + transferItemVO.toString());
                pstmtUpdate.setLong(i++, transferItemVO.getPreviousBalance());
                pstmtUpdate.setLong(i++, transferItemVO.getPostBalance());
                pstmtUpdate.setString(i++, transferItemVO.getUpdateStatus());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceResponseCode());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceReferenceID());
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));
                pstmtUpdate.setString(i++, transferItemVO.getTransferStatus());

                pstmtUpdate.setString(i++, transferItemVO.getTransferType2());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceReferenceID2());
                pstmtUpdate.setString(i++, transferItemVO.getUpdateStatus2());
                pstmtUpdate.setString(i++, transferItemVO.getTransferType1());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceReferenceID1());
                pstmtUpdate.setString(i++, transferItemVO.getUpdateStatus1());
                pstmtUpdate.setLong(i++, transferItemVO.getAdjustValue());
                pstmtUpdate.setString(i++, transferItemVO.getReferenceID());

                pstmtUpdate.setString(i++, transferItemVO.getTransferID());
                pstmtUpdate.setString(i++, transferItemVO.getMsisdn());
                pstmtUpdate.setInt(i++, transferItemVO.getSNo());
                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, "updateTransferItemDetails", "error.general.sql.processing");
                }
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateTransferItemDetails", "SQLException " + sqle.getMessage());
            updateCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", p_transferID, "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateTransferItemDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateTransferItemDetails", "Exception " + e.getMessage());
            updateCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", p_transferID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateTransferItemDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateTransferItemDetails", p_transferID, "Exiting updateCount=" + updateCount);
        }// end of finally
    }

    /**
     * Method to update the Validity Extension transfer items details
     * 
     * @param p_con
     * @param p_transferID
     * @param p_transferItemsList
     * @return int
     * @throws BTSLBaseException
     */
    public int updateValExtTransferItemDetails(Connection p_con, TransferVO p_transferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateTransferItemDetails", p_transferVO.getValExtTransferID(), "Entered p_transferItemsList size:" + p_transferVO.getTransferItemList().size());
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        TransferItemVO transferItemVO = null;
        ArrayList transferItemsList = p_transferVO.getTransferItemList();
        try {
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE transfer_items SET previous_balance=?, post_balance=?, ");
            updateQueryBuff.append(" update_status=?, interface_response_code=?, ");
            updateQueryBuff.append(" interface_reference_id=?, msisdn_previous_expiry=?, ");
            updateQueryBuff.append(" msisdn_new_expiry=?, transfer_status=?, ");
            updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=? , ");
            updateQueryBuff.append(" reference_id=? ");
            updateQueryBuff.append(" WHERE  transfer_id=? AND msisdn=? AND sno=?");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateTransferItemDetails", "Insert query:" + updateQuery);

            pstmtUpdate = p_con.prepareStatement(updateQuery);

            for (int j = 0; j < transferItemsList.size(); j++) {
                i = 1;
                transferItemVO = (TransferItemVO) transferItemsList.get(j);
                updateCount = 0;
                if (pstmtUpdate != null)
                    pstmtUpdate.clearParameters();
                if (_log.isDebugEnabled())
                    _log.debug("updateTransferItemDetails", "transferItemVO:" + transferItemVO.toString());
                pstmtUpdate.setLong(i++, transferItemVO.getPreviousBalance());
                pstmtUpdate.setLong(i++, transferItemVO.getPostBalance());
                pstmtUpdate.setString(i++, transferItemVO.getUpdateStatus());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceResponseCode());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceReferenceID());
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getPreviousExpiry()));
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(transferItemVO.getNewExpiry()));
                pstmtUpdate.setString(i++, transferItemVO.getTransferStatus());

                pstmtUpdate.setString(i++, transferItemVO.getTransferType2());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceReferenceID2());
                pstmtUpdate.setString(i++, transferItemVO.getUpdateStatus2());
                pstmtUpdate.setString(i++, transferItemVO.getTransferType1());
                pstmtUpdate.setString(i++, transferItemVO.getInterfaceReferenceID1());
                pstmtUpdate.setString(i++, transferItemVO.getUpdateStatus1());
                pstmtUpdate.setLong(i++, transferItemVO.getAdjustValue());
                pstmtUpdate.setString(i++, transferItemVO.getReferenceID());

                pstmtUpdate.setString(i++, p_transferVO.getValExtTransferID());
                pstmtUpdate.setString(i++, transferItemVO.getMsisdn());
                pstmtUpdate.setInt(i++, transferItemVO.getSNo());
                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, "updateTransferItemDetails", "error.general.sql.processing");
                }
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("updateTransferItemDetails", "SQLException " + sqle.getMessage());
            updateCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", p_transferVO.getValExtTransferID(), "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateTransferItemDetails", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateTransferItemDetails", "Exception " + e.getMessage());
            updateCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferItemDetails]", p_transferVO.getValExtTransferID(), "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateTransferItemDetails", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateTransferItemDetails", p_transferVO.getValExtTransferID(), "Exiting updateCount=" + updateCount);
        }// end of finally
    }

    /**
     * 
     * @param p_con
     * @param p_msisdn
     * @param p_fromDate
     * @param p_toDate
     * @return long[] which have total transfer count and total transfer amount
     * @throws BTSLBaseException
     * @throws Exception
     * @author avinash.kamthan
     */
    public long[] loadTransferStatus(Connection p_con, String p_msisdn, String p_fromDate, String p_toDate) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadTransferStatus", "Entered p_msisdn:" + p_msisdn + " p_fromDate:" + p_fromDate + " p_toDate: " + p_toDate);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        long[] data = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append(" SELECT COUNT(TRANSFER_ID) as transfer_count ,  SUM(transfer_value) as total_transfer");
            selectQueryBuff.append(" FROM subscriber_transfer ");
            selectQueryBuff.append(" WHERE sender_msisdn = ? and transfer_status = 'Y' ");
            selectQueryBuff.append(" AND transfer_date BETWEEN ");
            selectQueryBuff.append(" TO_DATE(?,'DD/MM/YY')  and TO_DATE(?,'DD/MM/YY') ");

            String selectQuery = selectQueryBuff.toString();

            if (_log.isDebugEnabled())
                _log.debug("loadTransferStatus", "select query:" + selectQuery);

            pstmtSelect = p_con.prepareStatement(selectQuery);

            pstmtSelect.setString(1, p_msisdn);
            pstmtSelect.setString(2, p_fromDate);
            pstmtSelect.setString(3, p_toDate);

            rs = pstmtSelect.executeQuery();
            data = new long[2];
            if (rs.next()) {
                data[0] = rs.getLong("transfer_count");
                data[1] = rs.getLong("total_transfer");
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadTransferStatus", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferStatus]", "", p_msisdn, "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadTransferStatus", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadTransferStatus", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferStatus]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadTransferStatus", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadTransferStatus", "Exiting :" + data);
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
    public HashMap loadTransferRuleCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferRuleCache()", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap map = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cd.card_group_set_name setname, cd.status setstatus, cd.language_1_message, ");
        strBuff.append(" cd.language_2_message,  tr.module, tr.network_code, tr.sender_subscriber_type, ");
        strBuff.append(" tr.receiver_subscriber_type, tr.sender_service_class_id, tr.receiver_service_class_id, ");
        strBuff.append(" tr.status,tr.card_group_set_id,  tr.modified_on, tr.modified_by , tr.created_on, ");
        // added for promotional trans rule cache
        strBuff.append(" tr.start_time,tr.end_time, tr.rule_level, tr.date_range,tr.time_slab,tr.rule_type,tr.allowed_days,tr.allowed_series,tr.denied_series, ");
        strBuff.append(" tr.created_by,tr.sub_service,tr.service_type,stsm.selector_name, tr.subscriber_status,tr.sp_group_id FROM transfer_rules tr, ");
        strBuff.append(" card_group_set cd , service_type_selector_mapping stsm  WHERE tr.status<>'N' ");
        strBuff.append(" AND tr.rule_type='N'");
        strBuff.append(" AND cd.status<>'N' AND stsm.status<>'N' AND tr.card_group_set_id=cd.card_group_set_id ");
        strBuff.append(" AND tr.module=cd.module_code AND tr.network_code=cd.network_code ");
        strBuff.append(" AND stsm.service_type=tr.service_type AND stsm.selector_code=tr.sub_service ");
        strBuff.append(" AND stsm.service_type=cd.service_type AND stsm.selector_code=cd.sub_service ");
        strBuff.append(" AND ( tr.end_time >= ? or tr.end_time is null )");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadTransferRuleCache", "QUERY sqlSelect=" + sqlSelect);

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();

            int i = 1;
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date()));

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
                map.put(rulesVO.getKey(), rulesVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadTransferRuleCache()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleCache]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadTransferRuleCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException)
                throw (BTSLBaseException) ex;
            _log.error("loadTransferRuleCache()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadTransferRuleCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadTransferRuleCache()", "Exiting: networkMap size=" + map.size());
            }
        }
        return map;
    }

    /**
     * Method addTransferRule.
     * This method is used to add the record in the transfer_rules table .
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addTransferRule", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        int i = 1;
        try {
            StringBuffer insertQueryBuff = new StringBuffer();
            insertQueryBuff.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type,  ");
            insertQueryBuff.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
            insertQueryBuff.append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type ) ");
            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addTransferRule", "Insert query:" + insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i++, p_transferRulesVO.getModule());
            pstmtInsert.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtInsert.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtInsert.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtInsert.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtInsert.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_transferRulesVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_transferRulesVO.getModifiedBy());
            pstmtInsert.setString(i++, p_transferRulesVO.getCardGroupSetID());
            pstmtInsert.setString(i++, p_transferRulesVO.getStatus());
            pstmtInsert.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtInsert.setString(i++, p_transferRulesVO.getServiceType());
            addCount = pstmtInsert.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
            _log.error("addTransferRule", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferRule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addTransferRule", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addTransferRule", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addTransferRule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addTransferRule", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addTransferRule", "Exiting addCount=" + addCount);
        }// end of finally
        return addCount;
    }

    /**
     * Method updateTransferRule.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int updateTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updateTransferRule", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        int i = 1;
        try {
            StringBuffer updateQueryBuff = new StringBuffer();
            updateQueryBuff.append("UPDATE transfer_rules SET  modified_on=?, modified_by=? ");
            updateQueryBuff.append(",card_group_set_id=? ,status=? ");
            updateQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            updateQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? ");
            updateQueryBuff.append("AND receiver_service_class_id=? AND sub_service = ? AND service_type = ?");
            // added by shashank for roadmap bug fix
            updateQueryBuff.append(" AND RULE_TYPE= ? ");
            if (_log.isDebugEnabled())
                _log.debug("updateTransferRule", "Update query:" + updateQueryBuff);
            pstmtUpdate = p_con.prepareStatement(updateQueryBuff.toString());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_transferRulesVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_transferRulesVO.getCardGroupSetID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getStatus());
            pstmtUpdate.setString(i++, p_transferRulesVO.getModule());
            pstmtUpdate.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtUpdate.setString(i++, p_transferRulesVO.getServiceType());
            // added by shashank for roadmap bug fix
            if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                pstmtUpdate.setString(i++, PretupsI.TRANSFER_RULE_PROMOTIONAL);

            } else {
                pstmtUpdate.setString(i++, PretupsI.TRANSFER_RULE_NORMAL);
            }
            // for the checking is the record modified during the transaction.
            boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified)
                throw new BTSLBaseException(this, "updateServiceType", "error.modify.true");
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateTransferRule", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferRule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateTransferRule", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updateTransferRule", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateTransferRule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateTransferRule", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateTransferRule", "Exiting updateCount=" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * Method deleteTransferRule.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int deleteTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deleteTransferRule", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;
        int i = 1;
        try {
            StringBuffer deleteQueryBuff = new StringBuffer();
            deleteQueryBuff.append("DELETE FROM transfer_rules ");
            deleteQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            deleteQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? AND ");
            deleteQueryBuff.append("receiver_service_class_id=? AND sub_service=? AND service_type = ?");
            String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deleteTransferRule", "delete query:" + deleteQuery);
            pstmtDelete = p_con.prepareStatement(deleteQuery);
            pstmtDelete.setString(i++, p_transferRulesVO.getModule());
            pstmtDelete.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtDelete.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtDelete.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtDelete.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtDelete.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtDelete.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtDelete.setString(i++, p_transferRulesVO.getServiceType());
            // for the checking is the record modified during the transaction.
            boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified)
                throw new BTSLBaseException(this, "updateServiceType", "error.modify.true");
            deleteCount = pstmtDelete.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("deleteTransferRule", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deleteTransferRule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteTransferRule", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deleteTransferRule", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deleteTransferRule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteTransferRule", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deleteTransferRule", "Exiting deleteCount=" + deleteCount);
        }// end of finally
        return deleteCount;
    }

    /**
     * Method isTransferRuleExist.
     * This method is used to check the uniqueness of the transfer rule in the
     * transfer_rules table .
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferRuleExist(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isTransferRuleExist", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        int i = 1;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            selectQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=?"); // AND
                                                                                                                                      // status=?
                                                                                                                                      // ";
            if (_log.isDebugEnabled())
                _log.debug("isTransferRuleExist", "Select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(i++, p_transferRulesVO.getModule());
            pstmtSelect.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtSelect.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtSelect.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtSelect.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtSelect.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtSelect.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtSelect.setString(i++, p_transferRulesVO.getServiceType());
            // pstmtSelect.setString(i++,PretupsI.TRANSFER_RULE_STATUS_ACTIVE);

            rs = pstmtSelect.executeQuery();
            if (rs.next())
                isExist = true;
        }// end of try
        catch (SQLException sqle) {
            _log.error("isTransferRuleExist", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isTransferRuleExist", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isTransferRuleExist", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isTransferRuleExist", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isTransferRuleExist", "Exiting isExist=" + isExist);
        }// end of finally
        return isExist;
    }

    /**
     * Method loadTransferRuleList.
     * This method is to load the list of all the transfer rules which are form
     * the specified network and
     * which status is ACTIVE.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferRuleList(Connection p_con, String p_networkCode, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadTransferRuleList()", "Entered:p_networkCode=" + p_networkCode + ",p_module=" + p_module);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TransferRulesVO rulesVO = null;
        ArrayList transferRulesList = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT module, network_code, sender_subscriber_type, receiver_subscriber_type,status, ");
        strBuff.append("sender_service_class_id,receiver_service_class_id, card_group_set_id,  modified_on, ");
        strBuff.append("modified_by , created_on, created_by,sub_service,service_type ");
        strBuff.append("FROM transfer_rules ");
        strBuff.append("WHERE network_code=? AND status <> ? AND module=? ");
        strBuff.append(" AND rule_type=? ");
        strBuff.append("ORDER BY modified_on,sender_subscriber_type, sub_service,service_type");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadTransferRuleList", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.TRANSFER_RULE_STATUS_DELETE);
            pstmt.setString(3, p_module);
            pstmt.setString(4, PretupsI.TRANSFER_RULE_NORMAL);
            rs = pstmt.executeQuery();
            transferRulesList = new ArrayList();
            int index = 0;
            while (rs.next()) {
                rulesVO = new TransferRulesVO();

                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                rulesVO.setRowID("" + ++index);
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                transferRulesList.add(rulesVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadTransferRuleList()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadTransferRuleList()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadTransferRuleList()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadTransferRuleList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadTransferRuleList()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadTransferRuleList()", "Exiting: transferRulesList.size=" + transferRulesList.size());
        }
        return transferRulesList;
    }

    /**
     * Method isRecordModified.
     * This method is used to check that is the record modified during the
     * processing.
     * 
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isRecordModified(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isRecordModified", "Entered:p_transferRulesVO=" + p_transferRulesVO.toString());
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        StringBuffer sqlRecordModified = new StringBuffer();
        sqlRecordModified.append("SELECT modified_on FROM transfer_rules ");
        sqlRecordModified.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
        sqlRecordModified.append("AND receiver_subscriber_type=? AND sender_service_class_id=? AND ");
        sqlRecordModified.append("receiver_service_class_id=? AND sub_service=? AND service_type=?");
        // added by shashank for roadmap bug fix
        sqlRecordModified.append(" AND  RULE_TYPE=? ");
        if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
            if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL))
                sqlRecordModified.append("AND sp_group_id = ? AND subscriber_status = ?");
        }
        java.sql.Timestamp newlastModified = null;
        if (p_transferRulesVO.getLastModifiedTime() == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled())
                _log.debug("isRecordModified", "QUERY=" + sqlRecordModified);
            String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            int i = 1;
            pstmtSelect.setString(i++, p_transferRulesVO.getModule());
            pstmtSelect.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtSelect.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtSelect.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtSelect.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtSelect.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtSelect.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtSelect.setString(i++, p_transferRulesVO.getServiceType());
            // added by shashank for roadmap bug fix
            if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                pstmtSelect.setString(i++, PretupsI.TRANSFER_RULE_PROMOTIONAL);

            } else {
                pstmtSelect.setString(i++, PretupsI.TRANSFER_RULE_NORMAL);
            }
            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                if (!BTSLUtil.isNullString(p_transferRulesVO.getRuleType()) && p_transferRulesVO.getRuleType().equalsIgnoreCase(PretupsI.TRANSFER_RULE_PROMOTIONAL)) {
                    if (p_transferRulesVO.getRuleLevel().equalsIgnoreCase(PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP))
                        pstmtSelect.setString(i++, p_transferRulesVO.getSenderSubscriberType());
                    else
                        pstmtSelect.setString(i++, p_transferRulesVO.getServiceGroupCode());
                    pstmtSelect.setString(i++, p_transferRulesVO.getSubscriberStatus());
                }
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the primary key.
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_transferRulesVO.getLastModifiedTime())
                modified = true;
        }// end of try
        catch (SQLException sqe) {
            _log.error("isRecordModified", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isRecordModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isRecordModified", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isRecordModified", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isRecordModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isRecordModified", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isRecordModified", "Exititng:modified=" + modified);
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method loadP2PTransferVOList.
     * 
     * @param p_con
     *            Connection
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_networkCode
     *            String
     * @param p_networkCodeType
     *            String
     * @param p_serviceType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadP2PReconciliationList(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_networkCodeType, String p_serviceType) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadP2PReconciliationList", "Entered p_fromDate:" + p_fromDate + " p_toDate: " + p_toDate + ",p_networkCode=" + p_networkCode + ",p_networkCodeType=" + p_networkCodeType + ",p_serviceType=" + p_serviceType);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        P2PTransferVO p2pTransferVO = null;
        ArrayList p2pTransferVOList = new ArrayList();
        try {
            StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name,STRF.transfer_id, ");
            selectQueryBuff.append("STRF.transfer_date, STRF.transfer_date_time, STRF.network_code, STRF.sender_id,");
            selectQueryBuff.append("STRF.product_code, STRF.sender_msisdn, STRF.receiver_msisdn, ");
            selectQueryBuff.append("STRF.receiver_network_code, STRF.transfer_value, STRF.error_code, ");
            selectQueryBuff.append("STRF.request_gateway_type, STRF.request_gateway_code, STRF.reference_id, ");
            selectQueryBuff.append("STRF.payment_method_type, STRF.service_type, STRF.pin_sent_to_msisdn, ");
            selectQueryBuff.append("STRF.language, STRF.country, STRF.skey, STRF.skey_generation_time, ");
            selectQueryBuff.append("STRF.skey_sent_to_msisdn, STRF.request_through_queue, STRF.credit_back_status, ");
            selectQueryBuff.append("STRF.quantity, STRF.reconciliation_flag, STRF.reconciliation_date, ");
            selectQueryBuff.append("STRF.reconciliation_by, STRF.created_on, STRF.created_by, STRF.modified_on, ");
            selectQueryBuff.append("STRF.modified_by, STRF.transfer_status, STRF.card_group_set_id, STRF.version, ");
            selectQueryBuff.append("STRF.card_group_id, STRF.sender_access_fee, STRF.sender_tax1_type, ");
            selectQueryBuff.append("STRF.sender_tax1_rate, STRF.sender_tax1_value, STRF.sender_tax2_type, ");
            selectQueryBuff.append("STRF.sender_tax2_rate, STRF.sender_tax2_value, STRF.sender_transfer_value, ");
            selectQueryBuff.append("STRF.receiver_access_fee, STRF.receiver_tax1_type, STRF.receiver_tax1_rate, ");
            selectQueryBuff.append("STRF.receiver_tax1_value, STRF.receiver_tax2_type, STRF.receiver_tax2_rate, ");
            selectQueryBuff.append("STRF.receiver_tax2_value, STRF.receiver_validity, STRF.receiver_transfer_value, ");
            selectQueryBuff.append("STRF.receiver_bonus_value, STRF.receiver_grace_period, STRF.transfer_category, ");
            selectQueryBuff.append("STRF.receiver_bonus_validity, STRF.card_group_code, STRF.receiver_valperiod_type ");
            selectQueryBuff.append("FROM subscriber_transfers STRF, products PROD,service_type ST, ");
            selectQueryBuff.append("p2p_subscribers U,key_values KV,key_values KV1  ");
            selectQueryBuff.append("WHERE U.user_id(+)= STRF.sender_id ");
            selectQueryBuff.append("AND KV.key(+)=STRF.error_code AND KV.type(+)=? ");
            selectQueryBuff.append("AND KV1.key(+)=STRF.transfer_status AND KV1.type(+)=? ");
            selectQueryBuff.append("AND STRF.transfer_date >=? AND STRF.transfer_date < ? ");
            selectQueryBuff.append("AND STRF.service_type=? AND STRF.product_code=PROD.product_code ");
            selectQueryBuff.append("AND (STRF.reconciliation_flag <> 'Y' OR STRF.reconciliation_flag IS NULL ) ");
            selectQueryBuff.append("AND ST.service_type=STRF.service_type ");
            // by sandeep ID REC001
            // as now we are loading all the UNDERPROCESS or AMBIGUOUS txn. for
            // the reconciliation
            selectQueryBuff.append("AND (STRF.transfer_status=? OR STRF.transfer_status=? ) ");
            // selectQueryBuff.append("AND STRF.transfer_status=? ");
            if (p_networkCodeType.equals(PretupsI.SENDER_NETWORK_CODE))
                selectQueryBuff.append("AND STRF.network_code=? ");
            else if (p_networkCodeType.equals(PretupsI.RECEIVER_NETWORK_CODE))
                selectQueryBuff.append("AND STRF.receiver_network_code=? ");

            selectQueryBuff.append("ORDER BY STRF.transfer_date_time DESC,STRF.transfer_id ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadP2PTransferVOList", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.P2P_ERRCODE_VALUS);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
            pstmtSelect.setString(i++, p_serviceType);
            pstmtSelect.setString(i++, SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            // by sandeep ID REC001
            // as now we are loading all the UNDERPROCESS or AMBIGUOUS txn. for
            // the reconciliation
            pstmtSelect.setString(i++, SelfTopUpErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            pstmtSelect.setString(i++, p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                p2pTransferVO = new P2PTransferVO();

                p2pTransferVO.setProductName(rs.getString("short_name"));
                p2pTransferVO.setServiceName(rs.getString("name"));
                p2pTransferVO.setSenderName(rs.getString("user_name"));
                p2pTransferVO.setErrorMessage(rs.getString("value"));
                p2pTransferVO.setTransferID(rs.getString("transfer_id"));
                p2pTransferVO.setTransferDate(rs.getDate("transfer_date"));
                p2pTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                p2pTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                p2pTransferVO.setNetworkCode(rs.getString("network_code"));
                p2pTransferVO.setSenderID(rs.getString("sender_id"));
                p2pTransferVO.setProductCode(rs.getString("product_code"));
                p2pTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                p2pTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                p2pTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                p2pTransferVO.setTransferValue(rs.getLong("transfer_value"));
                p2pTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                p2pTransferVO.setErrorCode(rs.getString("error_code"));
                p2pTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                p2pTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                p2pTransferVO.setReferenceID(rs.getString("reference_id"));
                p2pTransferVO.setPaymentMethodType(rs.getString("payment_method_type"));
                p2pTransferVO.setServiceType(rs.getString("service_type"));
                p2pTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                p2pTransferVO.setLanguage(rs.getString("language"));
                p2pTransferVO.setCountry(rs.getString("country"));
                p2pTransferVO.setSkey(rs.getLong("skey"));
                p2pTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                p2pTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                p2pTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                p2pTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                p2pTransferVO.setQuantity(rs.getLong("quantity"));
                p2pTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                p2pTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                p2pTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                p2pTransferVO.setCreatedOn(rs.getDate("created_on"));
                p2pTransferVO.setCreatedBy(rs.getString("created_by"));
                p2pTransferVO.setModifiedOn(rs.getDate("modified_on"));
                p2pTransferVO.setModifiedBy(rs.getString("modified_by"));
                p2pTransferVO.setTransferStatus(rs.getString("txn_status"));
                p2pTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                p2pTransferVO.setVersion(rs.getString("version"));
                p2pTransferVO.setCardGroupID(rs.getString("card_group_id"));
                p2pTransferVO.setSenderAccessFee(rs.getLong("sender_access_fee"));
                p2pTransferVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                p2pTransferVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                p2pTransferVO.setSenderTax1Value(rs.getLong("sender_tax1_value"));
                p2pTransferVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                p2pTransferVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                p2pTransferVO.setSenderTax2Value(rs.getLong("sender_tax2_value"));
                p2pTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                p2pTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                p2pTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                p2pTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                p2pTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                p2pTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                p2pTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                p2pTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                p2pTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                p2pTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                p2pTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                p2pTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                p2pTransferVO.setTransferCategory(rs.getString("transfer_category"));
                p2pTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                p2pTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                p2pTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                p2pTransferVO.setTxnStatus(rs.getString("transfer_status"));
                p2pTransferVOList.add(p2pTransferVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error("loadP2PReconciliationList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadP2PReconciliationList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadP2PTransferVOList", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadP2PReconciliationList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadP2PReconciliationList", "Exiting p2pTransferVOList.size()=" + p2pTransferVOList.size());
        }// end of finally

        return p2pTransferVOList;
    }

    /**
     * Method loadP2PTransferItemsVOList.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PReconciliationItemsList(Connection p_con, String p_transferID) throws BTSLBaseException {

        if (_log.isDebugEnabled())
            _log.debug("loadP2PReconciliationItemsList", "Entered p_transferID=" + p_transferID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        TransferItemVO p2pTransferItemVO = null;
        ArrayList p2pTransferItemsVOList = new ArrayList();
        try {
            StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT KV.value,transfer_id, msisdn, entry_date, request_value, previous_balance, ");
            selectQueryBuff.append("post_balance, user_type, transfer_type, entry_type, validation_status, ");
            selectQueryBuff.append("update_status, transfer_value, interface_type, interface_id, ");
            selectQueryBuff.append("interface_response_code, interface_reference_id, subscriber_type, ");
            selectQueryBuff.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status,");
            selectQueryBuff.append("transfer_date, transfer_date_time, entry_date_time, first_call, sno, prefix_id, ");
            selectQueryBuff.append("protocol_status, account_status, service_class_id, reference_id ");
            selectQueryBuff.append("FROM transfer_items,key_values KV ");
            selectQueryBuff.append("WHERE transfer_id=? AND KV.key(+)=transfer_status AND KV.type(+)=? ");
            selectQueryBuff.append("ORDER BY sno ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadP2PReconciliationItemsList", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, p_transferID);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_P2P_STATUS);
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
            _log.error("loadP2PReconciliationItemsList", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationItemsList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadP2PReconciliationItemsList", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadP2PReconciliationItemsList", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadP2PReconciliationItemsList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadP2PReconciliationItemsList", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadP2PReconciliationItemsList", "Exiting p2pTransferItemsVOList.size()=" + p2pTransferItemsVOList.size());
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
        if (_log.isDebugEnabled())
            _log.debug("updateReconcilationStatus", "Entered:p2pTransferVO=" + p2pTransferVO);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("UPDATE subscriber_transfers SET transfer_status=?, reconciliation_by=?, reconciliation_date=?, ");
            updateQuery.append("reconciliation_flag='Y', modified_by=?, modified_on=? ");
            updateQuery.append(", error_code = nvl(error_code," + SelfTopUpErrorCodesI.TXN_STATUS_UNDER_PROCESS + ")");
            // By sandeep ID REC001
            // to perform the check "is Already modify"
            updateQuery.append("WHERE transfer_id=? AND (transfer_status=? OR transfer_status=?)");
            String query = updateQuery.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateReconcilationStatus", "Query=" + query);

            pstmtUpdate = p_con.prepareStatement(query);
            int i = 1;
            pstmtUpdate.setString(i++, p2pTransferVO.getTransferStatus());
            pstmtUpdate.setString(i++, p2pTransferVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p2pTransferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p2pTransferVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p2pTransferVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p2pTransferVO.getTransferID());
            // By sandeep ID REC001
            // to perform the check "is Already modify"
            pstmtUpdate.setString(i++, SelfTopUpErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtUpdate.setString(i++, SelfTopUpErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "TransferDAO[updateReconcilationStatus]", "", "", "", "Record is already modified Txn ID=" + p2pTransferVO.getTransferID());
            } else // if(updateCount>=1)
            {
                if (p2pTransferVO.getTransferItemList() != null && !p2pTransferVO.getTransferItemList().isEmpty()) {
                    updateCount = 0;
                    updateCount = addTransferItemDetails(p_con, p2pTransferVO.getTransferID(), p2pTransferVO.getTransferItemList());
                }
            }
        } catch (SQLException sqe) {
            _log.error("updateReconcilationStatus", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateReconcilationStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateReconcilationStatus", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateReconcilationStatus", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updateReconcilationStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateReconcilationStatus", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateReconcilationStatus", "Exiting:return=" + updateCount);
        }
        return updateCount;
    }

    /**
     * Method isTransferRuleExistforCardGroup.
     * This method is used to check that either transfer rule exists for the
     * card group or not.
     * This methos is called before deletion of card group
     * This method is added for CR00045
     * 
     * @param p_con
     *            Connection
     * @param p_cardgroupSetID
     *            String
     * @param p_module
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferRuleExistforCardGroup(Connection p_con, String p_cardgroupSetID, String p_module) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isTransferRuleExistforCardGroup", "Entered p_cardgroupSetID=" + p_cardgroupSetID + " p_module=" + p_module);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        int i = 1;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND card_group_set_id=? AND status<>?  ");
            if (_log.isDebugEnabled())
                _log.debug("isTransferRuleExistforCardGroup", "Select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(i++, p_module);
            pstmtSelect.setString(i++, p_cardgroupSetID);
            pstmtSelect.setString(i++, PretupsI.STATUS_DELETE);

            rs = pstmtSelect.executeQuery();
            if (rs.next())
                isExist = true;
        }// end of try
        catch (SQLException sqle) {
            _log.error("isTransferRuleExistforCardGroup", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExistforCardGroup]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isTransferRuleExistforCardGroup", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isTransferRuleExistforCardGroup", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isTransferRuleExistforCardGroup]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isTransferRuleExistforCardGroup", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isTransferRuleExistforCardGroup", "Exiting isExist=" + isExist);
        }// end of finally
        return isExist;
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
        if (_log.isDebugEnabled())
            _log.debug("markP2PReceiverAmbiguous", "Entered p_transferID:" + p_transferID);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try {
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE transfer_items ");
            updateQueryBuff.append("SET update_status=transfer_status,transfer_status=? WHERE  transfer_id=? AND user_type=? ");
            String updateQuery = updateQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("updateTransferItemDetails", "Update query:" + updateQuery);

            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i++, InterfaceErrorCodesI.AMBIGOUS);
            pstmtUpdate.setString(i++, p_transferID);
            pstmtUpdate.setString(i++, PretupsI.USER_TYPE_RECEIVER);
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0)
                throw new BTSLBaseException(this, "markP2PReceiverAmbiguous", "error.general.sql.processing");
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error("markP2PReceiverAmbiguous", "SQLException " + sqle.getMessage());
            updateCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[markP2PReceiverAmbiguous]", p_transferID, "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "markP2PReceiverAmbiguous", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("markP2PReceiverAmbiguous", "Exception " + e.getMessage());
            updateCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[markP2PReceiverAmbiguous]", p_transferID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "markP2PReceiverAmbiguous", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("markP2PReceiverAmbiguous", "Exiting updateCount=" + updateCount);
        }// end of finally
    }

    /**
     * Load promotional transfer rules
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * 
     */
    public HashMap loadPromotionalTransferRuleMap(Connection p_con, Date p_transferDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalTransferRuleMap()", "Entered p_transferDate=" + p_transferDate);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap map = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cd.card_group_set_name setname, cd.status setstatus, cd.language_1_message, ");
        strBuff.append(" cd.language_2_message,  tr.module, tr.network_code, tr.sender_subscriber_type, ");
        strBuff.append(" tr.receiver_subscriber_type, tr.sender_service_class_id,tr.receiver_service_class_id, ");
        strBuff.append(" tr.status,tr.card_group_set_id,  tr.modified_on, tr.modified_by , tr.created_on, ");
        strBuff.append(" tr.created_by,tr.sub_service,tr.service_type,stsm.selector_name, tr.start_time,tr.end_time, tr.rule_level, tr.date_range,time_slab ");
        // Prefix ID changes
        strBuff.append(" tr.allowed_days,tr.allowed_series,tr.denied_series ");
        strBuff.append(" FROM transfer_rules tr, card_group_set cd , service_type_selector_mapping stsm ");
        strBuff.append(" WHERE tr.status<>'N' AND cd.status<>'N' AND stsm.status<>'N' AND tr.card_group_set_id=cd.card_group_set_id ");
        strBuff.append(" AND tr.module=cd.module_code AND tr.network_code=cd.network_code ");
        strBuff.append(" AND stsm.service_type=cd.service_type AND stsm.selector_code=cd.sub_service ");
        strBuff.append(" AND tr.rule_type=? AND tr.start_time<=? AND tr.end_time>=? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalTransferRuleMap", "QUERY sqlSelect=" + sqlSelect);

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.TRANSFER_RULE_PROMOTIONAL);
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_transferDate));
            pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_transferDate));
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
                map.put(rulesVO.getKey() + "_" + rulesVO.getRuleLevel(), rulesVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadPromotionalTransferRuleMap()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleMap]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadPromotionalTransferRuleMap()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadPromotionalTransferRuleMap()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleMap]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadPromotionalTransferRuleMap()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadPromotionalTransferRuleMap()", "Exiting: networkMap size=" + map.size());
        }
        return map;
    }

    /**
     * This method loads the list of promotional transfer rules defined on the
     * basis of passed network code of the user,
     * module, sender type (pre/post) and promotional level
     * (user/grade/category)
     * 
     * @author Varun
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @param p_sender_subscriber_type
     * @param p_rule_type
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadPromotionalTransferRulesList(Connection p_con, String p_networkCode, String p_module, String p_sender_subscriber_type, String p_rule_level, String p_dateRange) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalTransferRulesList()", "Entered:p_networkCode=" + p_networkCode + ",p_module=" + p_module + ",sender_subscriber_type=" + p_sender_subscriber_type + ",p_rule_type=" + p_rule_level);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TransferRulesVO rulesVO = null;
        ArrayList transferRulesList = null;
        StringBuffer strBuff = new StringBuffer();
        NetworkDAO networkDAO = null;
        strBuff.append("SELECT module, network_code, sender_subscriber_type, receiver_subscriber_type,status, ");
        strBuff.append("sender_service_class_id,receiver_service_class_id, card_group_set_id,  modified_on, ");
        strBuff.append("modified_by , created_on, created_by,sub_service,service_type,start_time,end_time,time_slab,");
        if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
            strBuff.append("sp_group_id,subscriber_status ");
        } else {
            strBuff.append("ALLOWED_DAYS,ALLOWED_SERIES,DENIED_SERIES");
        }
        // added by akanksha for tigo_gtcr
        strBuff.append(" ,rule_type ");
        strBuff.append(" FROM transfer_rules ");
        strBuff.append("WHERE network_code=? AND status <> ? AND module=? AND sender_subscriber_type=? AND rule_level = ? AND date_range=?");
        strBuff.append("ORDER BY modified_on,sender_subscriber_type, sub_service,service_type");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadPromotionalTransferRulesList", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.TRANSFER_RULE_STATUS_DELETE);
            pstmt.setString(3, p_module);
            pstmt.setString(4, p_sender_subscriber_type);
            pstmt.setString(5, p_rule_level);
            pstmt.setString(6, p_dateRange);
            rs = pstmt.executeQuery();
            transferRulesList = new ArrayList();
            int index = 0;
            while (rs.next()) {
                rulesVO = new TransferRulesVO();

                rulesVO.setModule(rs.getString("module"));
                rulesVO.setNetworkCode(rs.getString("network_code"));
                rulesVO.setStatus(rs.getString("status"));
                rulesVO.setSenderSubscriberType(rs.getString("sender_subscriber_type"));
                rulesVO.setReceiverSubscriberType(rs.getString("receiver_subscriber_type"));
                rulesVO.setSenderServiceClassID(rs.getString("sender_service_class_id"));
                rulesVO.setReceiverServiceClassID(rs.getString("receiver_service_class_id"));
                rulesVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                rulesVO.setModifiedOn(rs.getDate("modified_on"));
                rulesVO.setModifiedBy(rs.getString("modified_by"));
                rulesVO.setCreatedOn(rs.getDate("created_on"));
                rulesVO.setCreatedBy(rs.getString("created_by"));
                rulesVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                rulesVO.setRowID("" + ++index);
                rulesVO.setSubServiceTypeId(rs.getString("sub_service"));
                rulesVO.setServiceType(rs.getString("service_type"));
                rulesVO.setStartTime(rs.getTimestamp("start_time"));
                rulesVO.setEndTime(rs.getTimestamp("end_time"));
                rulesVO.setMultipleSlab(rs.getString("time_slab"));
                // added by arvinder to get allowed days
                if (!(SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW)) {
                    if (rs.getString("ALLOWED_DAYS") != null) {
                        rulesVO.setAllowedDays(BTSLUtil.numberToWeekdays(rs.getString("ALLOWED_DAYS")).toString());// added
                                                                                                                   // by
                                                                                                                   // arvinder
                                                                                                                   // for
                                                                                                                   // allowed
                                                                                                                   // days

                    }// end//
                     // added by arvinder to get allowed and denied series
                    if (p_rule_level.equals(PretupsI.PROMOTIONAL_LEVEL_PREFIXID)) {
                        if (rs.getString("ALLOWED_SERIES") != null) {
                            networkDAO = new NetworkDAO();
                            rulesVO.setAllowedSeries(networkDAO.getSeries(p_con, rs.getString("ALLOWED_SERIES")));
                        }
                        if (rs.getString("DENIED_SERIES") != null) {
                            networkDAO = new NetworkDAO();
                            rulesVO.setDeniedSeries(networkDAO.getSeries(p_con, rs.getString("DENIED_SERIES")));
                        }
                    }// end/
                } else {
                    rulesVO.setSubscriberStatus(rs.getString("subscriber_status"));
                    rulesVO.setServiceGroupCode(rs.getString("sp_group_id"));
                }
                // added by akanksha for tigo_gtcr
                rulesVO.setRuleType(rs.getString("rule_type"));
                transferRulesList.add(rulesVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadPromotionalTransferRulesList()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRulesList]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadPromotionalTransferRulesList()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadPromotionalTransferRulesList()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRulesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadPromotionalTransferRulesList()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadPromotionalTransferRulesList()", "Exiting: transferRulesList.size=" + transferRulesList.size());
        }
        return transferRulesList;
    }

    /**
     * Method addPromotionalTransferRule.
     * This method is used to add the record in the transfer_rules table .
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addPromotionalTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addPromotionalTransferRule", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtInsert = null;
        int addCount = 0;
        int i = 1;
        try {
            StringBuffer insertQueryBuff = new StringBuffer();
            insertQueryBuff.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type,  ");
            insertQueryBuff.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
            insertQueryBuff.append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type,start_time,end_time,rule_type,rule_level, date_range, time_slab, ");
            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                insertQueryBuff.append("sp_group_id,subscriber_status)");
            } else
                insertQueryBuff.append("ALLOWED_DAYS,ALLOWED_SERIES,DENIED_SERIES)");

            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                insertQueryBuff.append("?,?)");
            } else
                insertQueryBuff.append("?,?,?)");

            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addPromotionalTransferRule", "Insert query:" + insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(i++, p_transferRulesVO.getModule());
            pstmtInsert.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtInsert.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtInsert.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtInsert.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtInsert.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getCreatedOn()));
            pstmtInsert.setString(i++, p_transferRulesVO.getCreatedBy());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            pstmtInsert.setString(i++, p_transferRulesVO.getModifiedBy());
            pstmtInsert.setString(i++, p_transferRulesVO.getCardGroupSetID());
            pstmtInsert.setString(i++, p_transferRulesVO.getStatus());
            pstmtInsert.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtInsert.setString(i++, p_transferRulesVO.getServiceType());
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getStartTime()));
            pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getEndTime()));
            pstmtInsert.setString(i++, p_transferRulesVO.getRuleType());
            pstmtInsert.setString(i++, p_transferRulesVO.getRuleLevel());
            pstmtInsert.setString(i++, p_transferRulesVO.getSelectRangeType());
            pstmtInsert.setString(i++, p_transferRulesVO.getMultipleSlab());

            if (!(SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW)) {
                pstmtInsert.setString(i++, p_transferRulesVO.getAllowedDays());// added
                                                                               // by
                                                                               // arvinder
                                                                               // for
                                                                               // allowed
                                                                               // days
                pstmtInsert.setString(i++, p_transferRulesVO.getAllowedSeries());// added
                                                                                 // by
                                                                                 // arvinder
                                                                                 // for
                                                                                 // allowed
                                                                                 // series
                pstmtInsert.setString(i++, p_transferRulesVO.getDeniedSeries());// added
                                                                                // by
                                                                                // arvinder
                                                                                // for
                                                                                // denied
                                                                                // series
            } else {
                if (p_transferRulesVO.getPromotionCode().equalsIgnoreCase(PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP))
                    pstmtInsert.setString(i++, p_transferRulesVO.getSenderSubscriberType());
                else
                    pstmtInsert.setString(i++, p_transferRulesVO.getServiceGroupCode());
                pstmtInsert.setString(i++, p_transferRulesVO.getSubscriberStatus());

            }

            addCount = pstmtInsert.executeUpdate();
        }// end of try
        catch (SQLException sqle) {
            _log.error("addPromotionalTransferRule", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addPromotionalTransferRule", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addPromotionalTransferRule", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addPromotionalTransferRule", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addPromotionalTransferRule", "Exiting addCount=" + addCount);
        }// end of finally
        return addCount;
    }

    /**
     * Method isPromotionalTransferRuleExist.
     * This method is used to check the uniqueness of the transfer rule in the
     * transfer_rules table .
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isPromotionalTransferRuleExist(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isPromotionalTransferRuleExist", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        int i = 1;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            selectQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=?");
            // AND status=? ";
            /*
             * if(SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW ||
             * SystemPreferences.CELL_GROUP_REQUIRED)
             * {
             * selectQuery.append(" AND subscriber_status=? AND sp_group_id=? ");
             * }
             */
            if (_log.isDebugEnabled())
                _log.debug("isPromotionalTransferRuleExist", "Select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(i++, p_transferRulesVO.getModule());
            pstmtSelect.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtSelect.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtSelect.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtSelect.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtSelect.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtSelect.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtSelect.setString(i++, p_transferRulesVO.getServiceType());
            pstmtSelect.setString(i++, p_transferRulesVO.getRuleLevel());
            /*
             * if(SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW ||
             * SystemPreferences.CELL_GROUP_REQUIRED)
             * {
             * pstmtSelect.setString(i++,p_transferRulesVO.getSubscriberStatus())
             * ;
             * 
             * if(p_transferRulesVO.getPromotionCode().equalsIgnoreCase(PretupsI.
             * PROMOTIONAL_LEVEL_SERVICEGROUP))
             * pstmtSelect.setString(i++,p_transferRulesVO.getSenderSubscriberType
             * ());
             * else
             * pstmtSelect.setString(i++,p_transferRulesVO.getServiceGroupCode())
             * ;
             * 
             * 
             * }
             */

            rs = pstmtSelect.executeQuery();
            if (rs.next())
                isExist = true;
        }// end of try
        catch (SQLException sqle) {
            _log.error("isPromotionalTransferRuleExist", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isPromotionalTransferRuleExist", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isPromotionalTransferRuleExist", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isPromotionalTransferRuleExist", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isPromotionalTransferRuleExist", "Exiting isExist=" + isExist);
        }// end of finally
        return isExist;
    }

    /*
     * Update the promotional transfer rule if exist.By ranjana
     */

    public boolean isPromotionalTransferRuleUpdates(Connection p_con, TransferRulesVO p_transferRulesVO, Date p_currentDate) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("isPromotionalTransferRuleExist", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtUpdate = null;
        boolean isUpdate = false;
        int i = 1;
        int updateCount = 0;
        try {

            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("Update transfer_rules set start_time=? , end_time=?, time_slab=?, date_range=? ");
            updateQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            updateQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=?"); // AND
                                                                                                                                                       // status=?
                                                                                                                                                       // ";
            updateQuery.append("AND start_time<? AND end_time<? ");
            String update = updateQuery.toString();
            if (_log.isDebugEnabled())
                _log.debug("isPromotionalTransferRuleExist", "Entered updateQuery:" + update);
            pstmtUpdate = p_con.prepareStatement(update);

            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getStartTime()));
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getEndTime()));
            pstmtUpdate.setString(i++, p_transferRulesVO.getMultipleSlab());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSelectRangeType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getModule());
            pstmtUpdate.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtUpdate.setString(i++, p_transferRulesVO.getServiceType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getRuleLevel());
            // pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            // pstmtUpdate.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_currentDate));
            pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_currentDate));

            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0)
                isUpdate = true;

        }// end of try
        catch (SQLException sqle) {
            _log.error("isPromotionalTransferRuleUpdates", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleUpdates]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isPromotionalTransferRuleUpdates", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isPromotionalTransferRuleUpdates", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalTransferRuleUpdates]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isPromotionalTransferRuleUpdates", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isPromotionalTransferRuleUpdates", "Exiting isExist=" + isUpdate);
        }// end of finally
        return isUpdate;
    }

    /**
     * Method updateTransferRule.
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int updatePromotionalTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("updatePromotionalTransferRule", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        int i = 1;
        try {
            StringBuffer updateQueryBuff = new StringBuffer();
            updateQueryBuff.append("UPDATE transfer_rules SET  modified_on=?, modified_by=? ");
            updateQueryBuff.append(",card_group_set_id=? ,status=? ,start_time=? ,end_time=?, time_slab=?");
            if (!(SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW)) {
                updateQueryBuff.append(" ,ALLOWED_DAYS=?,ALLOWED_SERIES=?,DENIED_SERIES=?");
            }
            updateQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            updateQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? ");
            updateQueryBuff.append("AND receiver_service_class_id=? AND sub_service = ? AND service_type = ? AND rule_level=?");

            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                updateQueryBuff.append(" AND sp_group_id = ? AND subscriber_status = ?");
            }

            if (_log.isDebugEnabled())
                _log.debug("updatePromotionalTransferRule", "Update query:" + updateQueryBuff);
            pstmtUpdate = p_con.prepareStatement(updateQueryBuff.toString());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_transferRulesVO.getModifiedBy());
            pstmtUpdate.setString(i++, p_transferRulesVO.getCardGroupSetID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getStatus());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getStartTime()));
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferRulesVO.getEndTime()));
            pstmtUpdate.setString(i++, p_transferRulesVO.getMultipleSlab());
            // added by arvinder to update existing allowed days
            if (!(SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW)) {
                if (p_transferRulesVO.getMallowedDays() != null) {
                    pstmtUpdate.setString(i++, p_transferRulesVO.getMallowedDays());
                } else {
                    pstmtUpdate.setString(i++, BTSLUtil.weekDaysToNumber(p_transferRulesVO.getAllowedDays()).toString());

                }// end//
                pstmtUpdate.setString(i++, p_transferRulesVO.getAllowedSeries());// added
                                                                                 // by
                                                                                 // arvinder
                                                                                 // to
                                                                                 // update
                                                                                 // existing
                                                                                 // allowed
                                                                                 // series
                pstmtUpdate.setString(i++, p_transferRulesVO.getDeniedSeries());// added
                                                                                // by
                                                                                // arvinder
                                                                                // to
                                                                                // update
                                                                                // existing
                                                                                // denies
                                                                                // series
            }

            pstmtUpdate.setString(i++, p_transferRulesVO.getModule());
            pstmtUpdate.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtUpdate.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtUpdate.setString(i++, p_transferRulesVO.getServiceType());
            pstmtUpdate.setString(i++, p_transferRulesVO.getRuleLevel());
            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                if (p_transferRulesVO.getRuleLevel().equalsIgnoreCase(PretupsI.PROMOTIONAL_LEVEL_SERVICEGROUP))
                    pstmtUpdate.setString(i++, p_transferRulesVO.getSenderSubscriberType());
                else
                    pstmtUpdate.setString(i++, p_transferRulesVO.getServiceGroupCode());
                pstmtUpdate.setString(i++, p_transferRulesVO.getSubscriberStatus());
            }
            // for the checking is the record modified during the transaction.
            boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified)
                throw new BTSLBaseException(this, "updatePromotionalTransferRule", "error.modify.true");
            updateCount = pstmtUpdate.executeUpdate();
        } catch (BTSLBaseException be) {
            be.printStackTrace();
            throw be;
        } catch (SQLException sqle) {
            _log.error("updatePromotionalTransferRule", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updatePromotionalTransferRule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updatePromotionalTransferRule", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("updatePromotionalTransferRule", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[updatePromotionalTransferRule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updatePromotionalTransferRule", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updatePromotionalTransferRule", "Exiting updateCount=" + updateCount);
        }// end of finally
        return updateCount;
    }

    /**
     * Method deleteTransferRule.
     * 
     * @author Varun Kumar
     * @param p_con
     *            Connection
     * @param p_transferRulesVO
     *            TransferRulesVO
     * @param deleteFlag
     *            boolean
     * @return int
     * @throws BTSLBaseException
     */
    public int deletePromotionalTransferRule(Connection p_con, TransferRulesVO p_transferRulesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("deletePromotionalTransferRule", "Entered p_transferRulesVO:" + p_transferRulesVO.toString());
        PreparedStatement pstmtDelete = null;
        int deleteCount = 0;
        int i = 1;
        try {
            StringBuffer deleteQueryBuff = new StringBuffer();
            deleteQueryBuff.append("DELETE FROM transfer_rules ");
            deleteQueryBuff.append("WHERE module = ? AND network_code=? AND sender_subscriber_type=? ");
            deleteQueryBuff.append("AND receiver_subscriber_type=? AND sender_service_class_id=? AND ");
            deleteQueryBuff.append("receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_level=?");
            String deleteQuery = deleteQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("deletePromotionalTransferRule", "delete query:" + deleteQuery);
            pstmtDelete = p_con.prepareStatement(deleteQuery);
            pstmtDelete.setString(i++, p_transferRulesVO.getModule());
            pstmtDelete.setString(i++, p_transferRulesVO.getNetworkCode());
            pstmtDelete.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtDelete.setString(i++, p_transferRulesVO.getReceiverSubscriberType());
            pstmtDelete.setString(i++, p_transferRulesVO.getSenderServiceClassID());
            pstmtDelete.setString(i++, p_transferRulesVO.getReceiverServiceClassID());
            pstmtDelete.setString(i++, p_transferRulesVO.getSubServiceTypeId());
            pstmtDelete.setString(i++, p_transferRulesVO.getServiceType());
            pstmtDelete.setString(i++, p_transferRulesVO.getRuleLevel());

            // for the checking is the record modified during the transaction.
            boolean modified = this.isRecordModified(p_con, p_transferRulesVO);
            if (modified)
                throw new BTSLBaseException(this, "deletePromotionalTransferRule", "error.modify.true");
            deleteCount = pstmtDelete.executeUpdate();
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error("deletePromotionalTransferRule", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deletePromotionalTransferRule]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deletePromotionalTransferRule", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("deleteTransferRule", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[deletePromotionalTransferRule]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deletePromotionalTransferRule", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtDelete != null)
                    pstmtDelete.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("deletePromotionalTransferRule", "Exiting deleteCount=" + deleteCount);
        }// end of finally
        return deleteCount;
    }

    /**
     * Method for loading User List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_parentGraphDomainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadUserList(Connection p_con, String p_parentGraphDomainCode, String p_networkCode, String p_categoryCode, String p_username) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserList", "Entered  p_parentGraphDomainCode=" + p_parentGraphDomainCode + ",p_networkCode=" + p_networkCode + ",p_categoryCode=" + p_categoryCode + ",p_username=" + p_username);
        }
        // commented for DB2 OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT U.user_id, U.owner_id, U.user_name, U.login_id  FROM users U, user_geographies UG, categories CAT,");
        strBuff.append("user_phones UP WHERE U.category_code = CAT.category_code ");
        strBuff.append("AND U.user_id=UG.user_id AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
        strBuff.append("geographical_domains GD1 WHERE status IN ('Y','S') ");
        strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code =? ) AND U.user_type= ? AND u.status IN ('Y','S','SR') ");
        strBuff.append("AND U.network_code = ? AND U.category_code = ? ");
        strBuff.append("AND U.user_id=UP.user_id AND UP.primary_number='Y' AND UPPER(U.user_name) like UPPER(?)	ORDER BY U.user_name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadUserList", "QUERY sqlSelect=" + sqlSelect);

        ArrayList list = new ArrayList();
        try {
            // commented for DB2 pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            int i = 1;
            // commented for DB2 pstmt.setFormOfUse(i,
            // OraclePreparedStatement.FORM_NCHAR);
            pstmt.setString(i++, p_parentGraphDomainCode);
            // pstmt.setString(i++,p_userID);
            pstmt.setString(i++, PretupsI.USER_TYPE_CHANNEL);
            pstmt.setString(i++, p_networkCode);
            pstmt.setString(i++, p_categoryCode);
            pstmt.setString(i++, p_username);
            rs = pstmt.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setLoginID(rs.getString("login_id"));
                list.add(userVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadUserList", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadUserList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserList", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadUserList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserList", "Exiting: userList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method addPromotionalTransferRuleFile.
     * This method is used to add Batch record in the transfer_rules table .
     * 
     * @author Sanjeew
     * @param p_con
     *            Connection
     * @param ArrayList
     *            transfer_rule_list, ArrayList error_Vo_List
     * @return int
     * @throws BTSLBaseException
     */
    public void addPromotionalTransferRuleFile(Connection p_con, ArrayList p_transferRuleList, ArrayList p_errorVoList, MessageResources p_messages, Locale p_locale, String p_promotionLevel, String p_category, String geodomainCd) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addPromotionalTransferRuleFile", "Entered p_transferRuleList.size():" + p_transferRuleList.size());
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtMSISDN = null;
        ResultSet rsInsert = null;
        ResultSet rsSelect = null;
        ResultSet rsMSISDN = null;
        int addCount = 0;
        int i = 1;
        TransferRulesVO transferRulesVO = null;
        try {
            StringBuffer selectMSISDN = new StringBuffer();

            selectMSISDN.append("SELECT U.user_id FROM users U, user_geographies UG, categories CAT,user_phones UP ");
            selectMSISDN.append("WHERE U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
            selectMSISDN.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN ('Y','S') ");
            selectMSISDN.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
            selectMSISDN.append("START WITH GRPH_DOMAIN_TYPE=?) AND u.status IN ('Y','S','SR') ");
            selectMSISDN.append("AND U.network_code = ? ");
            selectMSISDN.append("AND U.category_code = ? ");
            selectMSISDN.append("AND U.user_id=UP.user_id ");
            selectMSISDN.append("AND UP.primary_number='Y' ");
            selectMSISDN.append("AND UP.msisdn=? ");
            selectMSISDN.append("ORDER BY U.user_name ");
            if (_log.isDebugEnabled())
                _log.debug("addPromotionalTransferRuleFile", "Select User ID:" + selectMSISDN);
            pstmtMSISDN = p_con.prepareStatement(selectMSISDN.toString());

            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE module=? AND network_code=? AND sender_subscriber_type=? AND receiver_subscriber_type=?  ");
            selectQuery.append("AND sender_service_class_id=? AND receiver_service_class_id=? AND sub_service=? AND service_type=? AND rule_Level=?"); // AND
                                                                                                                                                       // status=?
                                                                                                                                                       // ";
            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                selectQuery.append(" AND sp_group_id = ? AND subscriber_status = ?");
            }

            if (_log.isDebugEnabled())
                _log.debug("addPromotionalTransferRuleFile", "Select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());

            StringBuffer insertQueryBuff = new StringBuffer();
            insertQueryBuff.append("INSERT INTO transfer_rules (module,network_code,sender_subscriber_type, ");
            insertQueryBuff.append("receiver_subscriber_type, sender_service_class_id,receiver_service_class_id, ");
            insertQueryBuff.append("created_on,created_by, modified_on, modified_by,card_group_set_id,status,sub_service,service_type,START_TIME,END_TIME,RULE_TYPE,RULE_LEVEL,DATE_RANGE,TIME_SLAB ");
            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                insertQueryBuff.append(" ,sp_group_id,subscriber_status)");
            } else {
                insertQueryBuff.append(" )");
            }

            insertQueryBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                insertQueryBuff.append(",?,? )");
            } else {
                insertQueryBuff.append(" )");
            }

            String insertQuery = insertQueryBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addPromotionalTransferRuleFile", "Insert query:" + insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);

            ListValueVO errorVO = null;
            if (p_errorVoList == null)
                p_errorVoList = new ArrayList();
            for (int s = 0; s < p_transferRuleList.size(); s++) {
                i = 1;
                transferRulesVO = (TransferRulesVO) p_transferRuleList.get(s);
                if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {
                    pstmtMSISDN.clearParameters();
                    pstmtMSISDN.setString(i++, geodomainCd);
                    pstmtMSISDN.setString(i++, transferRulesVO.getNetworkCode());
                    pstmtMSISDN.setString(i++, p_category);
                    pstmtMSISDN.setString(i++, transferRulesVO.getSenderSubscriberType());

                    try {
                        rsMSISDN = pstmtMSISDN.executeQuery();
                        if (rsMSISDN.next()) {
                            transferRulesVO.setSenderSubscriberType(rsMSISDN.getString(1));
                        } else {
                            errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale, "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.useriddoesnotexist"));
                            p_errorVoList.add(errorVO);
                            continue;
                        }
                    } catch (SQLException sqle) {
                        errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale, "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.useriddoesnotexist"));
                        p_errorVoList.add(errorVO);
                        continue;
                    }// end of catch
                }

                pstmtSelect.clearParameters();
                i = 1;
                pstmtSelect.setString(i++, transferRulesVO.getModule());
                pstmtSelect.setString(i++, transferRulesVO.getNetworkCode());
                pstmtSelect.setString(i++, transferRulesVO.getSenderSubscriberType());
                pstmtSelect.setString(i++, transferRulesVO.getReceiverSubscriberType());
                pstmtSelect.setString(i++, transferRulesVO.getSenderServiceClassID());
                pstmtSelect.setString(i++, transferRulesVO.getReceiverServiceClassID());
                pstmtSelect.setString(i++, transferRulesVO.getSubServiceTypeId());
                pstmtSelect.setString(i++, transferRulesVO.getServiceType());
                pstmtSelect.setString(i++, transferRulesVO.getRuleLevel());
                if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                    if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE))
                        pstmtSelect.setString(i++, transferRulesVO.getSenderSubscriberType());
                    else
                        pstmtSelect.setString(i++, transferRulesVO.getServiceGroupCode());
                    pstmtSelect.setString(i++, transferRulesVO.getSubscriberStatus());
                }

                try {
                    rsSelect = pstmtSelect.executeQuery();
                    if (rsSelect.next()) {
                        errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale, "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.allreadyexist"));
                        p_errorVoList.add(errorVO);
                        continue;
                    } else {
                        i = 1;
                        addCount = 0;
                        pstmtInsert.clearParameters();
                        pstmtInsert.setString(i++, transferRulesVO.getModule());
                        pstmtInsert.setString(i++, transferRulesVO.getNetworkCode());
                        pstmtInsert.setString(i++, transferRulesVO.getSenderSubscriberType());
                        pstmtInsert.setString(i++, transferRulesVO.getReceiverSubscriberType());
                        pstmtInsert.setString(i++, transferRulesVO.getSenderServiceClassID());
                        pstmtInsert.setString(i++, transferRulesVO.getReceiverServiceClassID());
                        pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getCreatedOn()));
                        pstmtInsert.setString(i++, transferRulesVO.getCreatedBy());
                        pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getModifiedOn()));
                        pstmtInsert.setString(i++, transferRulesVO.getModifiedBy());
                        pstmtInsert.setString(i++, transferRulesVO.getCardGroupSetID());
                        pstmtInsert.setString(i++, transferRulesVO.getStatus());
                        pstmtInsert.setString(i++, transferRulesVO.getSubServiceTypeId());
                        pstmtInsert.setString(i++, transferRulesVO.getServiceType());
                        pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getStartTime()));
                        pstmtInsert.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(transferRulesVO.getEndTime()));
                        pstmtInsert.setString(i++, transferRulesVO.getRuleType());
                        pstmtInsert.setString(i++, transferRulesVO.getRuleLevel());
                        // //ranjana
                        pstmtInsert.setString(i++, transferRulesVO.getSelectRangeType());
                        pstmtInsert.setString(i++, transferRulesVO.getMultipleSlab());
                        if (SystemPreferences.CELL_GROUP_REQUIRED || SystemPreferences.SERVICE_PROVIDER_PROMO_ALLOW) {
                            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE))
                                pstmtInsert.setString(i++, transferRulesVO.getSenderSubscriberType());
                            else
                                pstmtInsert.setString(i++, transferRulesVO.getServiceGroupCode());
                            pstmtInsert.setString(i++, transferRulesVO.getSubscriberStatus());
                        }

                        addCount = pstmtInsert.executeUpdate();
                        if (addCount == 0) {
                            errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale, "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.insertfailed"));
                            p_errorVoList.add(errorVO);
                            continue;
                        }
                    }
                } catch (SQLException sqle) {
                    errorVO = new ListValueVO("", transferRulesVO.getRowID(), p_messages.getMessage(p_locale, "promotionaltransferrule.addpromotionaltransferrulerile.error.trfrule.insertfailed"));
                    p_errorVoList.add(errorVO);
                    continue;
                }// end of catch
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error("addPromotionalTransferRuleFile", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRuleFile]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addPromotionalTransferRuleFile", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("addTransferRule", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[addPromotionalTransferRuleFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addPromotionalTransferRuleFile", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rsInsert != null)
                    rsInsert.close();
            } catch (Exception e) {
            }
            try {
                if (rsSelect != null)
                    rsSelect.close();
            } catch (Exception e) {
            }
            try {
                if (rsInsert != null)
                    rsInsert.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtMSISDN != null)
                    pstmtMSISDN.close();
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("addPromotionalTransferRuleFile", "Exiting addCount=" + addCount);
        }// end of finally
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
     * if(_log.isDebugEnabled())_log.debug("modifyPromotionalTransferRuleFile",
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
     * if(_log.isDebugEnabled())
     * _log.debug("modifyPromotionalTransferRuleFile","Select query:"+selectQuery
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
     * if(_log.isDebugEnabled())
     * _log.debug("modifyPromotionalTransferRuleFile","Update query:"+
     * updateQueryBuff );
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
     * _log.error("updatePromotionalTransferRule","SQLException "+sqle.getMessage
     * ());
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"TransferDAO[updatePromotionalTransferRule]","",
     * "","","SQL Exception:"+sqle.getMessage());
     * throw new BTSLBaseException(this, "updatePromotionalTransferRule",
     * "error.general.sql.processing");
     * }//end of catch
     * catch (Exception e)
     * {
     * _log.error("updatePromotionalTransferRule","Exception "+e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"TransferDAO[updatePromotionalTransferRule]","",
     * "","","Exception:"+e.getMessage());
     * throw new BTSLBaseException(this, "updatePromotionalTransferRule",
     * "error.general.processing");
     * }//end of catch
     * finally
     * {
     * try{if(pstmtUpdate!=null) pstmtUpdate.close();}catch(Exception e){}
     * try{if(pstmtSelect!=null) pstmtSelect.close();}catch(Exception e){}
     * if(_log.isDebugEnabled())_log.debug("updatePromotionalTransferRule",
     * "Exiting updateCount="+updateCount);
     * }//end of finally
     * //return updateCount;
     * }
     */

    /**
     * @param p_con
     * @param p_receiverAllServiceClassID
     * @param p_enquiryServiceType
     * @param p_networkCode
     * @param p_domainCodeForCategory
     * @return
     * @throws BTSLBaseException
     */
    public TransferVO loadCardGroupSetIdFromTransferRule(Connection p_con, String p_receiverAllServiceClassID, String p_enquiryServiceType, String p_networkCode, String p_domainCodeForCategory) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug(" loadCardGroupSetIdFromTransferRule", "Entered p_receiverAllServiceClassID:" + p_receiverAllServiceClassID + "p_enquiryServiceType" + p_enquiryServiceType);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TransferVO transferVO = null;
        StringBuffer strBuff = new StringBuffer(" SELECT card_group_set_id,network_code,module ");
        strBuff.append("FROM  transfer_rules WHERE receiver_service_class_id=? AND network_code=? ");
        strBuff.append("AND sender_subscriber_type=? AND service_type=? AND status<>'N'");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadCardGroupSetIdFromTransferRule", "QUERY sqlSelect=" + sqlSelect);
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_receiverAllServiceClassID);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_domainCodeForCategory);
            pstmt.setString(4, p_enquiryServiceType);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                transferVO = new TransferVO();
                transferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setModule(rs.getString("module"));
            }
        } catch (SQLException sqe) {
            _log.error("loadCardGroupSetIdFromTransferRule", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadCardGroupSetIdFromTransferRule]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadCardGroupSetIdFromTransferRule", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadCardGroupSetIdFromTransferRule]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetIdFromTransferRule", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled())
                _log.debug("loadCardGroupSetIdFromTransferRule", "Exiting:  transferVO =" + transferVO);
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
        if (_log.isDebugEnabled())
            _log.debug("isPromotionalRuleExistOnTimeRange", "Entered transferRulesVO=" + p_transferRulesVO.toString() + ",transferDate=" + p_transferDate);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        int i = 1;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT 1 FROM transfer_rules ");
            selectQuery.append("WHERE sender_subscriber_type=? AND to_char(start_time,'hh24:mm')<=to_char(?,'hh24:mm') AND to_char(end_time,'hh24:mm')>=to_char(?,'hh24:mm')");
            if (_log.isDebugEnabled())
                _log.debug("isPromotionalRuleExistOnTimeRange", "Select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(i++, p_transferRulesVO.getSenderSubscriberType());
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferDate));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_transferDate));

            rs = pstmtSelect.executeQuery();
            if (rs.next())
                isExist = true;
        }// end of try
        catch (SQLException sqle) {
            _log.error("isPromotionalRuleExistOnTimeRange", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalRuleExistOnTimeRange]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isPromotionalRuleExistOnTimeRange", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("isPromotionalRuleExistOnTimeRange", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isPromotionalRuleExistOnTimeRange]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isPromotionalRuleExistOnTimeRange", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("isPromotionalRuleExistOnTimeRange", "Exiting isExist=" + isExist);
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

        if (_log.isDebugEnabled())
            _log.debug("loadP2PTransferDetails", "Entered p_transferID=" + p_transferID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        P2PTransferVO transferVO = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT st.transfer_id,st.transfer_date,st.network_code,st.product_code,st.sender_msisdn,");
            selectQueryBuff.append("st.receiver_msisdn,st.transfer_value,st.service_type,st.language,st.country,st.transfer_status,kv.value ");
            selectQueryBuff.append("FROM subscriber_transfers st,key_values kv WHERE st.transfer_id=? AND st.transfer_status=kv.key AND kv.type=?");
            pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
            pstmtSelect.setString(1, p_transferID);
            pstmtSelect.setString(2, PretupsI.P2P_ERRCODE_VALUS);
            if (_log.isDebugEnabled())
                _log.debug("loadP2PTransferDetails", "selectQueryBuff :" + selectQueryBuff);
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
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("loadP2PTransferDetails", "Exiting");
        return transferVO;
    }

    public ArrayList getSubscriberStatusList(Connection p_con, String lookup_type) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSubscriberStatusList", "Entered:");
        ArrayList subscriberStatusList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        TransferVO transferVO = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQuery = new StringBuffer();
            selectQuery.append(" SELECT sl.SUB_LOOKUP_NAME,sl.LOOKUP_CODE ");
            selectQuery.append(" FROM SUB_LOOKUPS sl,LOOKUPS l where sl.LOOKUP_TYPE=? and sl.LOOKUP_CODE=l.LOOKUP_CODE and sl.LOOKUP_TYPE=l.LOOKUP_TYPE  ");

            if (_log.isDebugEnabled())
                _log.debug("getSubscriberStatusList", "Query=" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, lookup_type);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                transferVO = new TransferVO();
                transferVO.setSubscriberStatus(rs.getString("SUB_LOOKUP_NAME"));
                transferVO.setServiceType(rs.getString("LOOKUP_CODE"));
                subscriberStatusList.add(transferVO);
            }
        } catch (SQLException sqe) {
            _log.error("getSubscriberStatusList", "SQLException:" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[getSubscriberStatusList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getSubscriberStatusList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("getSubscriberStatusList", "Exception:" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[getSubscriberStatusList]", "", "", "", "SQL Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "getSubscriberStatusList", "error.general.sql.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getSubscriberStatusList", "Exiting:list size=" + subscriberStatusList.size());
        }

        return subscriberStatusList;
    }

    /**
     * Load the promotional transfer rules cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author gaurav pandey
     */
    public HashMap loadPromotionalTransferRuleCache() throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadPromotionalTransferRuleCache()", "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap map = new HashMap();

        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT cd.card_group_set_name setname, cd.status setstatus, cd.language_1_message, ");
        strBuff.append(" cd.language_2_message,  tr.module, tr.network_code, tr.sender_subscriber_type, ");
        strBuff.append(" tr.receiver_subscriber_type, tr.sender_service_class_id, tr.receiver_service_class_id, ");
        strBuff.append(" tr.status,tr.card_group_set_id,  tr.modified_on, tr.modified_by , tr.created_on, ");
        strBuff.append(" tr.start_time,tr.end_time, tr.rule_level, tr.date_range,tr.time_slab,tr.rule_type,tr.allowed_days,tr.allowed_series,tr.denied_series, ");
        strBuff.append(" tr.created_by,tr.sub_service,tr.service_type,stsm.selector_name  FROM transfer_rules tr, ");
        strBuff.append(" card_group_set cd , service_type_selector_mapping stsm  WHERE tr.status<>'N' ");
        strBuff.append(" AND tr.rule_type='P'");
        strBuff.append(" AND cd.status<>'N' AND stsm.status<>'N' AND tr.card_group_set_id=cd.card_group_set_id ");
        strBuff.append(" AND END_TIME >=?");
        strBuff.append(" AND tr.module=cd.module_code AND tr.network_code=cd.network_code ");
        strBuff.append(" AND stsm.service_type=tr.service_type AND stsm.selector_code=tr.sub_service ");
        strBuff.append(" AND stsm.service_type=cd.service_type AND stsm.selector_code=cd.sub_service ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("loadTransferRuleCache", "QUERY sqlSelect=" + sqlSelect);

        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            Date date = new Date();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate((date)));
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
                map.put(rulesVO.getKey(), rulesVO);
            }

        } catch (SQLException sqe) {
            _log.error("loadPromotionalTransferRuleCache()", "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleCache]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadPromotionalTransferRuleCache()", "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException)
                throw (BTSLBaseException) ex;
            _log.error("loadPromotionalTransferRuleCache()", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadPromotionalTransferRuleCache]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadPromotionalTransferRuleCache()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }

            if (_log.isDebugEnabled()) {
                _log.debug("loadPromotionalTransferRuleCache()", "Exiting: networkMap size=" + map.size());
            }
        }
        return map;
    }

}
