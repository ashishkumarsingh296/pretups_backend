package com.selftopup.pretups.p2p.query.businesslogic;

/*
 * #ReceiverTransferDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * july 28, 2005 ved prakash sharma Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.subscriber.businesslogic.ReceiverVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.transfer.businesslogic.TransferItemVO;
import com.selftopup.pretups.transfer.businesslogic.TransferVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

/**
 * 
 */

public class ReceiverTransferDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(ReceiverTransferDAO.class.getName());

    /**
     * Method loadReceiverDetails.
     * This method is used to load receiver details into ArrayList
     * If there is any error then throws the SQLException or Exception
     * 
     * @param p_con
     *            Connection
     * @param p_receiverVO
     *            TransferVO
     * @return interfaceDetails ArrayList
     * @throws SQLException
     * @throws Exception
     */

    public ArrayList loadReceiverDetails(Connection p_con, TransferVO p_receiverVO) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadReceiverDetails()", "Entered::p_receiverVO= " + p_receiverVO);

        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList receiverDetails = new ArrayList();
        int i = 0;

        try {
            strBuff = new StringBuffer("SELECT STRS.transfer_id, STRS.transfer_date_time,");
            strBuff.append(" nvl(KV2.value,STRS.error_code) error_code, STRS.reference_id, ST.name name, STRS.quantity,");
            strBuff.append(" STRS.sender_msisdn, STRS.receiver_msisdn, STRS.transfer_value,");
            strBuff.append(" nvl(KV.value,STRS.transfer_status) transfer_status, ");
            strBuff.append(" NET.network_name, MSG.gateway_name,");
            strBuff.append(" PRD.product_name,PRD.product_code");
            strBuff.append(" FROM subscriber_transfers STRS, networks NET, message_gateway MSG, products PRD, key_values KV, key_values KV2,service_type ST ");
            strBuff.append(" WHERE STRS.network_code=NET.network_code");
            strBuff.append(" AND PRD.product_code(+)=STRS.product_code");
            strBuff.append(" AND STRS.request_gateway_code=MSG.gateway_code(+)");
            strBuff.append(" AND STRS.receiver_msisdn=?");
            strBuff.append(" AND STRS.transfer_status = KV.key(+)");
            strBuff.append(" AND KV.type(+) = ? AND STRS.service_type=ST.service_type AND STRS.error_code = KV2.key(+) AND KV2.type(+) = ? ");

            if (((p_receiverVO.getToDate()).trim()).length() == 0) {
                p_receiverVO.setToDate(p_receiverVO.getFromDate());
            }

            if ((p_receiverVO.getFromDate().length() > 0) && (p_receiverVO.getTransferID().length() > 0)) {
                i = 0;
                // both date and transfer id is enetered
                strBuff.append(" AND (transfer_date >=? AND transfer_date<=?)");
                strBuff.append(" AND UPPER(transfer_id) =UPPER(?)");
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, ((SenderVO) p_receiverVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_receiverVO.getFromDate())));
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_receiverVO.getToDate())));
                pstmtSelect.setString(++i, p_receiverVO.getTransferID());

            } else if (p_receiverVO.getTransferID().length() > 0 && (p_receiverVO.getFromDate().trim()).length() == 0) {
                i = 0;
                strBuff.append(" AND UPPER(transfer_id)=UPPER(?)"); // only
                                                                    // transfer
                                                                    // id is
                                                                    // entered
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, ((SenderVO) p_receiverVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
                pstmtSelect.setString(++i, p_receiverVO.getTransferID());

            } else if ((p_receiverVO.getTransferID().trim()).length() == 0 && (p_receiverVO.getFromDate().trim()).length() == 0) {
                i = 0;
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, ((SenderVO) p_receiverVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
            } else {
                i = 0;
                strBuff.append(" AND transfer_date >=? AND transfer_date<=?");
                strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, ((SenderVO) p_receiverVO.getSenderVO()).getMsisdn());
                pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
                pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_receiverVO.getFromDate())));
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_receiverVO.getToDate())));
            }

            String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadReceiverDetails()", "QUERY= " + selectQuery);

            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled())
                _log.debug("loadReceiverDetails()", "QUERY Executed= " + selectQuery);

            int index = 0;

            while (rs.next()) {
                TransferVO transferVO = new TransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                transferVO.setTransferDisplayDateTime(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));

                ReceiverVO receiverVO = new ReceiverVO();
                receiverVO.setMsisdn(rs.getString("receiver_msisdn"));
                transferVO.setReceiverVO(receiverVO);

                SenderVO senderVO = new SenderVO();
                senderVO.setMsisdn(rs.getString("sender_msisdn"));
                transferVO.setSenderVO(senderVO);

                transferVO.setTransferValue(rs.getLong("transfer_value"));
                transferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                transferVO.setTransferStatus(rs.getString("transfer_status"));
                transferVO.setNetworkName(rs.getString("network_name"));
                transferVO.setProductName(rs.getString("product_name"));
                // This line is added By Sanjeev
                transferVO.setProductCode(rs.getString("product_code"));
                transferVO.setGatewayName(rs.getString("gateway_name"));
                transferVO.setErrorCode(rs.getString("error_code"));
                transferVO.setReferenceID(rs.getString("reference_id"));
                transferVO.setServiceType(rs.getString("name"));
                transferVO.setQuantity(rs.getLong("quantity"));
                transferVO.setRadioIndex(index);
                receiverDetails.add(transferVO);
                index++;
            }
        } catch (SQLException sqe) {
            if (_log.isDebugEnabled())
                _log.error("loadReceiverDetails()", " SQL Exception::" + sqe.getMessage());
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "loadReceiverDetails()", "error.general.processing");
        }

        catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.error("loadReceiverDetails()", " Exception::" + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "loadReceiverDetails()", "error.general.processing");

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
                _log.debug("loadReceiverDetails()", " Exiting.. receiverDetails size=" + receiverDetails.size());
        }
        return receiverDetails;
    }
}
