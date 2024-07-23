package com.btsl.pretups.p2p.query.businesslogic;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * ReceiverTransferDAO
 */
public class ReceiverTransferDAO {

    /**
     * Field log.
     */
    private Log log = LogFactory.getFactory().getInstance(ReceiverTransferDAO.class.getName());

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
    public ArrayList loadReceiverDetails(Connection pCon, TransferVO preceiverVO) throws SQLException, Exception {
    	final String methodName = "loadReceiverDetails";
    	
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered::p_receiverVO= " + preceiverVO);
        }
        
       
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList receiverDetails = new ArrayList();
        int i = 0;

        try {
           
        	ReceiverTransferQry receiverTrfQry= (ReceiverTransferQry)ObjectProducer.getObject(QueryConstants.RECEIVER_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = receiverTrfQry.loadReceiverDetails(pCon, preceiverVO);
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

                final SenderVO senderVO = new SenderVO();
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
                transferVO.setServiceType(rs.getString("name1"));
                transferVO.setQuantity(rs.getLong("quantity"));
                transferVO.setRadioIndex(index);
                receiverDetails.add(transferVO);
                index++;
            }
        } catch (SQLException sqe) {
            if (log.isDebugEnabled()) {
                log.error(methodName, " SQL Exception::" + sqe.getMessage());
            }
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, "loadReceiverDetails()", "error.general.processing");
        }

        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.error(methodName, " Exception::" + e.getMessage());
            }
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, "loadReceiverDetails()", "error.general.processing");

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
                log.debug(methodName, " Exiting.. receiverDetails size=" + receiverDetails.size());
            }
        }
        return receiverDetails;
    }
}
