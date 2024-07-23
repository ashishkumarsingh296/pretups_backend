package com.selftopup.pretups.p2p.query.businesslogic;

/*
 * #SubscriberTransferDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * july 28, 2005 ved prakash sharma Initial creation
 * nov 2, 2005 ved prakash sharma loadSubscriberItemList change the query
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

/**
 * 
 */
public class SubscriberTransferDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(SubscriberTransferDAO.class.getName());

    /**
     * Method loadSubscriberDetails.
     * 
     * @param p_con
     *            Connection
     * @param p_subscriberVO
     *            TransferVO
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadSubscriberDetails(Connection p_con, TransferVO p_subscriberVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadSubscriberDetails()", "Entered::p_subscriberVO= " + p_subscriberVO);

        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList subscriberDetails = new ArrayList();
        int i = 0;

        try {
            strBuff = new StringBuffer("SELECT STRS.transfer_id, STRS.transfer_date_time,");
            strBuff.append(" nvl(KV2.value,STRS.error_code) error_code, STRS.reference_id, ST.name, STRS.quantity,");
            strBuff.append(" STRS.sender_msisdn, STRS.receiver_msisdn, STRS.transfer_value,");
            strBuff.append(" nvl(KV.value,STRS.transfer_status) transfer_status, ");
            strBuff.append(" NET.network_name, MSG.gateway_name,");
            strBuff.append(" PRD.product_name");
            strBuff.append(" FROM subscriber_transfers STRS, networks NET, message_gateway MSG, products PRD, key_values KV, key_values KV2,service_type ST ");
            strBuff.append(" WHERE STRS.network_code=NET.network_code");
            strBuff.append(" AND PRD.product_code(+)=STRS.product_code");
            strBuff.append(" AND STRS.request_gateway_code=MSG.gateway_code(+)");
            strBuff.append(" AND STRS.sender_msisdn=?");
            strBuff.append(" AND STRS.transfer_status = KV.key(+) AND STRS.service_type=ST.service_type ");
            strBuff.append(" AND KV.type(+) = ? AND STRS.error_code = KV2.key(+) AND KV2.type(+) = ? ");

            if (((p_subscriberVO.getToDate()).trim()).length() == 0) {
                p_subscriberVO.setToDate(p_subscriberVO.getFromDate());
            }

            if ((p_subscriberVO.getFromDate().length() > 0) && (p_subscriberVO.getTransferID().length() > 0)) {
                i = 0;
                // both date and transfer id is enetered
                strBuff.append(" AND (transfer_date >=? AND transfer_date<=?)");
                strBuff.append(" AND UPPER(transfer_id) =UPPER(?)");
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());

                pstmtSelect.setString(++i, ((SenderVO) p_subscriberVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_subscriberVO.getFromDate())));
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_subscriberVO.getToDate())));
                pstmtSelect.setString(++i, p_subscriberVO.getTransferID());

            } else if (p_subscriberVO.getTransferID().length() > 0 && (p_subscriberVO.getFromDate().trim()).length() == 0) {
                i = 0;
                strBuff.append(" AND UPPER(transfer_id)=UPPER(?)"); // only
                                                                    // transfer
                                                                    // id is
                                                                    // entered
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC ");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, ((SenderVO) p_subscriberVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
                pstmtSelect.setString(++i, p_subscriberVO.getTransferID());

            } else if ((p_subscriberVO.getTransferID().trim()).length() == 0 && (p_subscriberVO.getFromDate().trim()).length() == 0) {
                i = 0;
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC ");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, ((SenderVO) p_subscriberVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);

            } else {
                // only todate is entered
                i = 0;
                strBuff.append(" AND transfer_date >=? AND transfer_date<=?");
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC ");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, ((SenderVO) p_subscriberVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_subscriberVO.getFromDate())));
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_subscriberVO.getToDate())));

            }

            String selectQuery = strBuff.toString();
            _log.debug("loadInterfaceDetails()", "QUERY= " + selectQuery);

            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberDetails()", "QUERY Executed= " + selectQuery);

            int index = 0;

            while (rs.next()) {
                TransferVO transferVO = new TransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                transferVO.setTransferDisplayDateTime(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));

                ReceiverVO receiverVO = new ReceiverVO();

                receiverVO.setMsisdn(rs.getString("receiver_msisdn"));

                transferVO.setReceiverVO(receiverVO);

                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setNetworkName(rs.getString("network_name"));
                transferVO.setProductName(rs.getString("product_name"));
                transferVO.setGatewayName(rs.getString("gateway_name"));
                transferVO.setErrorCode(rs.getString("error_code"));
                transferVO.setReferenceID(rs.getString("reference_id"));
                transferVO.setServiceType(rs.getString("name"));
                transferVO.setQuantity(rs.getLong("quantity"));
                transferVO.setRadioIndex(index);
                subscriberDetails.add(transferVO);
                index++;
            }
        } catch (SQLException sqe) {
            _log.error("loadSubscriberDetails()", " SQL Exception::" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberDetails()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadSubscriberDetails()", " Exception::" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberDetails()", "error.general.processing");
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
                _log.debug("loadSubscriberDetails()", " Exiting.. subscriberDetails size=" + subscriberDetails.size());
        }
        return subscriberDetails;
    }

    /**
     * Method loadSubscriberItemList.
     * 
     * @param p_con
     *            Connection
     * @param p_transferVO
     *            TransferVO
     * @param p_transferItemVO
     *            TransferItemVO
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadSubscriberItemList(Connection p_con, TransferVO p_transferVO, TransferItemVO p_transferItemVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("loadSubscriberItemList()", "Entered p_transferVO= " + p_transferVO);

        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList transferItemList = new ArrayList();

        try {

            strBuff = new StringBuffer("SELECT TRS.transfer_id, TRS.entry_date, TRS.entry_date_time,");
            strBuff.append(" TRS.entry_type, TRS.first_call,");
            strBuff.append(" TRS.interface_id, TRS.interface_reference_id, TRS.interface_response_code,");
            strBuff.append(" TRS.interface_type, TRS.msisdn,");
            strBuff.append(" TRS.msisdn_new_expiry, TRS.msisdn_previous_expiry, TRS.post_balance,");
            strBuff.append(" TRS.prefix_id, TRS.previous_balance,");
            strBuff.append(" TRS.request_value, TRS.sno,");
            strBuff.append(" TRS.service_class_code,TRS.account_status, ");
            strBuff.append(" LOOK1.lookup_name subscriber_type,");
            strBuff.append(" TRS.transfer_date, TRS.transfer_date_time AS transfer_date_time_item,");
            strBuff.append(" TRS.transfer_id, TRS.interface_reference_id, ");
            strBuff.append(" nvl(KV.value,TRS.transfer_status) transfer_status,");
            strBuff.append(" TRS.transfer_type, nvl(KV2.value,STRS.error_code) error_code, ");
            strBuff.append(" TRS.transfer_value As transfer_value_item, TRS.update_status, TRS.user_type,");
            strBuff.append(" TRS.validation_status, INTF.interface_description,TRS.msisdn_new_expiry, TRS.msisdn_previous_expiry,");
            strBuff.append(" STRS.card_group_id,");
            strBuff.append(" CGS.card_group_set_name,");
            strBuff.append(" LOOK.lookup_name payment_method_type,");
            strBuff.append(" STRS.receiver_access_fee,");
            strBuff.append(" STRS.receiver_bonus_validity, STRS.receiver_validity, STRS.receiver_grace_period,");
            strBuff.append(" STRS.receiver_bonus_value, STRS.receiver_msisdn, STRS.receiver_tax1_rate,");
            strBuff.append(" STRS.receiver_tax1_type, STRS.receiver_tax1_value, STRS.receiver_tax2_rate,");
            strBuff.append(" STRS.receiver_tax2_type, STRS.receiver_tax2_value, STRS.receiver_transfer_value,");
            strBuff.append(" STRS.reconciliation_by, STRS.reconciliation_date, STRS.sender_access_fee,");
            strBuff.append(" STRS.sender_msisdn, STRS.sender_tax1_rate, STRS.sender_tax1_type, STRS.sender_tax1_value,");
            strBuff.append(" STRS.sender_tax2_rate, STRS.sender_tax2_type, STRS.sender_tax2_value,");
            strBuff.append(" STRS.sender_transfer_value, nvl(TRANST.value,STRS.transfer_status) fitrans_status, ");
            strBuff.append(" ST.name service_type, STRS.transfer_date_time, STRS.transfer_value, STRS.transfer_category,TRS.reference_id ");
            strBuff.append(",STRS.cell_id,STRS.switch_id,TRS.service_provider_name spname ");
            strBuff.append(" FROM transfer_items TRS, interfaces INTF, subscriber_transfers STRS,");
            strBuff.append(" lookups LOOK, card_group_set CGS, service_type ST, lookups LOOK1, key_values KV, key_values TRANST, key_values KV2 ");
            strBuff.append(" WHERE TRS.transfer_id = ? ");
            strBuff.append(" AND INTF.interface_id(+) = TRS.interface_id");
            strBuff.append(" AND STRS.transfer_id = TRS.transfer_id");
            strBuff.append(" AND LOOK.lookup_code(+) = STRS.payment_method_type");
            strBuff.append(" AND LOOK.lookup_type(+) = ? ");
            strBuff.append(" AND CGS.card_group_set_id(+) = STRS.card_group_set_id");
            strBuff.append(" AND ST.service_type(+) = STRS.service_type");
            strBuff.append(" AND LOOK1.lookup_code(+) = TRS.subscriber_type");
            strBuff.append(" AND LOOK1.lookup_type(+) = ? ");
            strBuff.append(" AND KV.key(+) = TRS.transfer_status");
            strBuff.append(" AND KV.type(+) = ? AND STRS.error_code = KV2.key(+) AND KV2.type(+) = ? ");
            strBuff.append(" AND STRS.transfer_status = TRANST.key(+) AND TRANST.type(+) = ? ");
            strBuff.append(" ORDER BY TRS.transfer_id");

            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 0;
            pstmtSelect.setString(++i, p_transferVO.getTransferID());
            pstmtSelect.setString(++i, PretupsI.PAYMENT_INSTRUMENT_TYPE);
            pstmtSelect.setString(++i, PretupsI.SUBSRICBER_TYPE);
            pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
            pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
            pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);

            String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberItemList()", "QUERY= " + selectQuery);

            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberItemList()", "QUERY Executed= " + selectQuery);

            while (rs.next()) {

                p_transferItemVO.setMsisdn(rs.getString("msisdn"));
                p_transferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                p_transferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                p_transferItemVO.setEntryDate(rs.getDate("entry_date"));
                p_transferItemVO.setEntryDisplayDateTime(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("entry_date_time")));
                p_transferItemVO.setUserType(rs.getString("user_type"));
                p_transferItemVO.setTransferType(rs.getString("transfer_type"));
                p_transferItemVO.setValidationStatus(rs.getString("validation_status"));
                p_transferItemVO.setUpdateStatus(rs.getString("update_status"));
                p_transferItemVO.setTransferValue(rs.getLong("transfer_value_item"));
                p_transferItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value_item")));
                p_transferItemVO.setInterfaceDesc(rs.getString("interface_description"));
                p_transferItemVO.setInterfaceID(rs.getString("interface_id"));
                p_transferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                p_transferItemVO.setTransferStatus(rs.getString("transfer_status"));
                p_transferItemVO.setPreviousBalance(rs.getLong("previous_balance"));
                p_transferItemVO.setPostBalance(rs.getLong("post_balance"));
                p_transferItemVO.setEntryType(rs.getString("entry_type"));
                p_transferItemVO.setAccountStatus(rs.getString("account_status"));
                p_transferItemVO.setReferenceID(rs.getString("reference_id"));

                p_transferVO.setTransferID(rs.getString("transfer_id"));
                p_transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                p_transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                p_transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                p_transferVO.setTransferDisplayDateTime(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));

                p_transferVO.setTransferValue(rs.getLong("transfer_value"));
                p_transferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                p_transferVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                p_transferVO.setCardGroupID(rs.getString("card_group_id"));
                p_transferVO.setServiceType(rs.getString("service_type"));

                p_transferVO.setPaymentMethodType(rs.getString("payment_method_type"));
                p_transferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                p_transferVO.setReceiverGracePeriod(rs.getLong("receiver_grace_period"));
                p_transferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                p_transferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                p_transferVO.setReconciliationDate((rs.getDate("reconciliation_date")));
                p_transferVO.setReconciliationBy(rs.getString("reconciliation_by"));

                p_transferVO.setSenderAccessFee(rs.getLong("sender_access_fee"));
                p_transferVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                p_transferVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                p_transferVO.setSenderTax1Value(rs.getLong("sender_tax1_value"));
                p_transferVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                p_transferVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                p_transferVO.setSenderTax2Value(rs.getLong("sender_tax2_value"));
                p_transferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                p_transferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                p_transferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                p_transferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                p_transferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                p_transferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                p_transferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                p_transferVO.setErrorCode(rs.getString("error_code"));
                p_transferVO.setTransferStatus(rs.getString("fitrans_status"));
                p_transferVO.setTransferCategory(rs.getString("transfer_category"));

                p_transferVO.setMsisdnPreviousExpiry(rs.getTimestamp("msisdn_previous_expiry"));
                p_transferVO.setMsisdnNewExpiry(rs.getTimestamp("msisdn_new_expiry"));
                // by gaurav for ussd changes
                p_transferVO.setCellId(rs.getString("cell_id"));
                p_transferVO.setSwitchId(rs.getString("switch_id"));
                p_transferItemVO.setServiceProviderName(rs.getString("spname"));
                p_transferVO.setServiceProviderName(rs.getString("spname"));

                if (p_transferVO.getMsisdnPreviousExpiry() != null)
                    p_transferVO.setMsisdnPreviousExpiryStr(BTSLUtil.getDateStringFromDate(rs.getTimestamp("msisdn_previous_expiry")));
                /*
                 * else
                 * p_transferVO.setMsisdnPreviousExpiryStr("Not Applicable");
                 */if (p_transferVO.getMsisdnNewExpiry() != null)
                    p_transferVO.setMsisdnNewExpiryStr(BTSLUtil.getDateStringFromDate(rs.getTimestamp("msisdn_new_expiry")));
                /*
                 * else
                 * p_transferVO.setMsisdnNewExpiryStr("Not Applicable");
                 */

                TransferDetailsVO transferItemVO = new TransferDetailsVO();

                transferItemVO.setTransferItemVO(p_transferItemVO);
                transferItemVO.setTransferVO(p_transferVO);

                transferItemList.add(transferItemVO);
                p_transferItemVO = new TransferItemVO();
                p_transferVO = new TransferVO();
            }

        } catch (SQLException sqe) {
            if (_log.isDebugEnabled())
                _log.error("loadSubscriberItemList()", " SQL Exception::" + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberItemList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberItemList()", "error.general.sql.processing");
        }

        catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.error("loadSubscriberItemList()", " Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberItemList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberItemList()", "error.general.processing");

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
                _log.debug("loadSubscriberItemList()", "Finally Exiting size=" + transferItemList.size());
        }
        return transferItemList;

    }

    /**
     * Method addP2PReceiverRequests.
     * 
     * @param p_con
     *            Connection
     * @param p_requestVO
     *            RequestVO
     * 
     * @throws BTSLBaseException
     */

    public int addP2PReceiverRequests(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("addP2PReceiverRequests()", "entered p_requestVO=" + p_requestVO.toString());
        PreparedStatement pstmtInsert = null;
        int addCount = -1;
        int i = 0;
        try {
            StringBuffer strBuff = new StringBuffer("INSERT into p2p_receiver_requests(request_id, request_msisdn, request_message, ");
            strBuff.append("service_type, source_type, type, instance_id, message_code, service_port, created_date, transaction_id, start_time, end_time,network_code,req_gateway_code)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("addP2PReceiverRequests()", "QUERY= " + insertQuery);
            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(++i, p_requestVO.getRequestIDStr());
            pstmtInsert.setString(++i, p_requestVO.getRequestMSISDN());
            pstmtInsert.setString(++i, p_requestVO.getDecryptedMessage());
            pstmtInsert.setString(++i, p_requestVO.getServiceType());
            pstmtInsert.setString(++i, p_requestVO.getSourceType());
            pstmtInsert.setString(++i, p_requestVO.getType());
            pstmtInsert.setString(++i, p_requestVO.getInstanceID());
            if (p_requestVO.isSuccessTxn())
                pstmtInsert.setString(++i, PretupsI.TXN_STATUS_SUCCESS);
            else
                pstmtInsert.setString(++i, p_requestVO.getMessageCode());
            pstmtInsert.setString(++i, p_requestVO.getServicePort());
            pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(new Date()));
            pstmtInsert.setString(++i, p_requestVO.getTransactionID());
            pstmtInsert.setLong(++i, p_requestVO.getRequestStartTime());
            pstmtInsert.setLong(++i, System.currentTimeMillis());
            pstmtInsert.setString(++i, p_requestVO.getRequestNetworkCode());
            pstmtInsert.setString(++i, p_requestVO.getRequestGatewayCode());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            if (_log.isDebugEnabled())
                _log.error("addP2PReceiverRequests()", " SQL Exception::" + sqe.getMessage());
            sqe.printStackTrace();
            // Changing the EventIDI and EventLevelI to INFO discussed with
            // Gurjeet & Sanjay sir.
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberTransferDAO[addP2PReceiverRequests]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addP2PReceiverRequests()", "error.general.sql.processing");
        }

        catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.error("addP2PReceiverRequests()", " Exception " + e.getMessage());
            e.printStackTrace();
            // Changing the EventIDI and EventLevelI to INFO discussed with
            // Gurjeet & Sanjay sir.
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberTransferDAO[addP2PReceiverRequests]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addP2PReceiverRequests()", "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("addP2PReceiverRequests()", "Finally Exiting");
        }
        return addCount;
    }

}
