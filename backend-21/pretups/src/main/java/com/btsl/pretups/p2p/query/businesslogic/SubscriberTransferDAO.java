package com.btsl.pretups.p2p.query.businesslogic;

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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

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
     * @param pCon
     *            Connection
     * @param p_subscriberVO
     *            TransferVO
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadSubscriberDetails(Connection pCon, TransferVO p_subscriberVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadSubscriberDetails()", "Entered::p_subscriberVO= " + p_subscriberVO);
        }
        final String methodName = "loadSubscriberDetails";
    
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList subscriberDetails = new ArrayList();
        int i = 0;

        try {
        	SubscriberTransferQry receiverTrfQry= (SubscriberTransferQry)ObjectProducer.getObject(QueryConstants.SUBSCRIBER_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        	
        	pstmtSelect = receiverTrfQry.loadSubscriberDetails(pCon, p_subscriberVO);            

            rs = pstmtSelect.executeQuery();
           
            int index = 0;

            while (rs.next()) {
                final TransferVO transferVO = new TransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                transferVO.setTransferDisplayDateTime(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));

                final ReceiverVO receiverVO = new ReceiverVO();

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
                transferVO.setServiceType(rs.getString("name1"));
                transferVO.setQuantity(rs.getLong("quantity"));
                transferVO.setRadioIndex(index);
                subscriberDetails.add(transferVO);
                index++;
            }
        } catch (SQLException sqe) {
            _log.error("loadSubscriberDetails()", " SQL Exception::" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberDetails]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberDetails()", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadSubscriberDetails()", " Exception::" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberDetails]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberDetails()", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberDetails()", " Exiting.. subscriberDetails size=" + subscriberDetails.size());
            }
        }
        return subscriberDetails;
    }

    /**
     * Method loadSubscriberItemList.
     * 
     * @param pCon
     *            Connection
     * @param ptransferVO
     *            TransferVO
     * @param ptransferItemVO
     *            TransferItemVO
     * @return ArrayList
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadSubscriberItemList(Connection pCon, TransferVO ptransferVO, TransferItemVO ptransferItemVO) throws BTSLBaseException {
        
    	 final String methodName = "loadSubscriberItemList";
    	if (_log.isDebugEnabled()) {
            _log.debug("loadSubscriberItemList()", "Entered ptransferVO= " + ptransferVO);
        }
       
       
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList transferItemList = new ArrayList();

        try {

        	SubscriberTransferQry receiverTrfQry= (SubscriberTransferQry)ObjectProducer.getObject(QueryConstants.SUBSCRIBER_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        	
        	pstmtSelect = receiverTrfQry.loadSubscriberItemList(pCon, ptransferVO);   

            rs = pstmtSelect.executeQuery();
            

            while (rs.next()) {

                ptransferItemVO.setMsisdn(rs.getString("msisdn"));
                ptransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                ptransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                ptransferItemVO.setEntryDate(rs.getDate("entry_date"));
                ptransferItemVO.setEntryDisplayDateTime(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("entry_date_time"))));
                ptransferItemVO.setUserType(rs.getString("user_type"));
                ptransferItemVO.setTransferType(rs.getString("transfer_type"));
                ptransferItemVO.setValidationStatus(rs.getString("validation_status"));
                ptransferItemVO.setUpdateStatus(rs.getString("update_status"));
                ptransferItemVO.setTransferValue(rs.getLong("transfer_value_item"));
                ptransferItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value_item")));
                ptransferItemVO.setInterfaceDesc(rs.getString("interface_description"));
                ptransferItemVO.setInterfaceID(rs.getString("interface_id"));
                ptransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                ptransferItemVO.setTransferStatus(rs.getString("transfer_status"));
                ptransferItemVO.setPreviousBalance(rs.getLong("previous_balance"));
                ptransferItemVO.setPostBalance(rs.getLong("post_balance"));
                ptransferItemVO.setEntryType(rs.getString("entry_type"));
                ptransferItemVO.setAccountStatus(rs.getString("account_status"));
                ptransferItemVO.setReferenceID(rs.getString("reference_id"));

                ptransferVO.setTransferID(rs.getString("transfer_id"));
                ptransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                ptransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                ptransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                ptransferVO.setTransferDisplayDateTime(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));

                ptransferVO.setTransferValue(rs.getLong("transfer_value"));
                ptransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                ptransferVO.setCardGroupSetName(rs.getString("card_group_set_name"));
                ptransferVO.setCardGroupID(rs.getString("card_group_id"));
                ptransferVO.setServiceType(rs.getString("service_type"));

                ptransferVO.setPaymentMethodType(rs.getString("payment_method_type"));
                ptransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                ptransferVO.setReceiverGracePeriod(rs.getLong("receiver_grace_period"));
                ptransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                ptransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                ptransferVO.setReconciliationDate((rs.getDate("reconciliation_date")));
                ptransferVO.setReconciliationBy(rs.getString("reconciliation_by"));

                ptransferVO.setSenderAccessFee(rs.getLong("sender_access_fee"));
                ptransferVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
                ptransferVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
                ptransferVO.setSenderTax1Value(rs.getLong("sender_tax1_value"));
                ptransferVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
                ptransferVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
                ptransferVO.setSenderTax2Value(rs.getLong("sender_tax2_value"));
                ptransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                ptransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                ptransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                ptransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                ptransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                ptransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                ptransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                ptransferVO.setErrorCode(rs.getString("error_code"));
                ptransferVO.setTransferStatus(rs.getString("fitrans_status"));
                ptransferVO.setTransferCategory(rs.getString("transfer_category"));

                ptransferVO.setMsisdnPreviousExpiry(rs.getTimestamp("msisdn_previous_expiry"));
                ptransferVO.setMsisdnNewExpiry(rs.getTimestamp("msisdn_new_expiry"));
                // by gaurav for ussd changes
                ptransferVO.setCellId(rs.getString("cell_id"));
                ptransferVO.setSwitchId(rs.getString("switch_id"));
                ptransferItemVO.setServiceProviderName(rs.getString("spname"));
                ptransferVO.setServiceProviderName(rs.getString("spname"));

                if (ptransferVO.getMsisdnPreviousExpiry() != null) {
                    ptransferVO.setMsisdnPreviousExpiryStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getTimestamp("msisdn_previous_expiry"))));
                }
                /*
                 * else
                 * ptransferVO.setMsisdnPreviousExpiryStr("Not Applicable");
                 */if (ptransferVO.getMsisdnNewExpiry() != null) {
                    ptransferVO.setMsisdnNewExpiryStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getTimestamp("msisdn_new_expiry"))));
                    /*
                     * else
                     * ptransferVO.setMsisdnNewExpiryStr("Not Applicable");
                     */
                }

                final TransferDetailsVO transferItemVO = new TransferDetailsVO();

                transferItemVO.setTransferItemVO(ptransferItemVO);
                transferItemVO.setTransferVO(ptransferVO);

                transferItemList.add(transferItemVO);
                ptransferItemVO = new TransferItemVO();
                ptransferVO = new TransferVO();
            }

        } catch (SQLException sqe) {
            if (_log.isDebugEnabled()) {
                _log.error("loadSubscriberItemList()", " SQL Exception::" + sqe.getMessage());
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberItemList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberItemList()", "error.general.sql.processing");
        }

        catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.error("loadSubscriberItemList()", " Exception " + e.getMessage());
            }
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberTransferDAO[loadSubscriberItemList]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadSubscriberItemList()", "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadSubscriberItemList()", "Finally Exiting size=" + transferItemList.size());
            }
        }
        return transferItemList;

    }

    /**
     * Method addP2PReceiverRequests.
     * 
     * @param pCon
     *            Connection
     * @param prequestVO
     *            RequestVO
     * 
     * @throws BTSLBaseException
     */

    public int addP2PReceiverRequests(Connection pCon, RequestVO prequestVO) throws BTSLBaseException {
        
    	 final String methodName = "addP2PReceiverRequests";
    	if (_log.isDebugEnabled()) {
            _log.debug(methodName, "entered prequestVO=" + prequestVO.toString());
        }
       
        PreparedStatement pstmtInsert = null;
        int addCount = -1;
        int i = 0;
        try {
            final StringBuffer strBuff = new StringBuffer("INSERT into p2p_receiver_requests(request_id, request_msisdn, request_message, ");
            strBuff
                .append("service_type, source_type, type, instance_id, message_code, service_port, created_date, transaction_id, start_time, end_time,network_code,req_gateway_code)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + insertQuery);
            }
            pstmtInsert = pCon.prepareStatement(insertQuery);
            pstmtInsert.setString(++i, prequestVO.getRequestIDStr());
            pstmtInsert.setString(++i, prequestVO.getRequestMSISDN());
            pstmtInsert.setString(++i, prequestVO.getServiceType());
            pstmtInsert.setString(++i, prequestVO.getSourceType());
            pstmtInsert.setString(++i, prequestVO.getType());
            pstmtInsert.setString(++i, prequestVO.getInstanceID());
            if (prequestVO.isSuccessTxn()) {
                pstmtInsert.setString(++i, PretupsI.TXN_STATUS_SUCCESS);
            } else {
                pstmtInsert.setString(++i, prequestVO.getMessageCode());
            }
            pstmtInsert.setString(++i, prequestVO.getServicePort());
            pstmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(new Date()));
            pstmtInsert.setString(++i, prequestVO.getTransactionID());
            pstmtInsert.setLong(++i, prequestVO.getRequestStartTime());
            pstmtInsert.setLong(++i, System.currentTimeMillis());
            pstmtInsert.setString(++i, prequestVO.getRequestNetworkCode());
            pstmtInsert.setString(++i, prequestVO.getRequestGatewayCode());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            if (_log.isDebugEnabled()) {
                _log.error(methodName, " SQL Exception::" + sqe.getMessage());
            }
            _log.errorTrace(methodName, sqe);
            // Changing the EventIDI and EventLevelI to INFO discussed with
            // Gurjeet & Sanjay sir.
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberTransferDAO[addP2PReceiverRequests]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.error(methodName, " Exception " + e.getMessage());
            }
            _log.errorTrace(methodName, e);
            // Changing the EventIDI and EventLevelI to INFO discussed with
            // Gurjeet & Sanjay sir.
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "SubscriberTransferDAO[addP2PReceiverRequests]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Finally Exiting");
            }
        }
        return addCount;
    }

}
