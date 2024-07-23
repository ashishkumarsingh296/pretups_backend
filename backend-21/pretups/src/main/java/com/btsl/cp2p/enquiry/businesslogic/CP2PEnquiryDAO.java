package com.btsl.cp2p.enquiry.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.cp2p.buddymgt.businesslogic.BuddyMgtBL;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class CP2PEnquiryDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method loads the details like the access fee, tax etc
     * 
     * @param p_con
     * @param p_service
     * @param p_receiver
     * @param p_senderMsisdn
     * @param p_transferDate
     * @return ArrayList
     * @throws BTSLBaseException
     * @throws SQLException
     */
    public ArrayList loadCP2PEnquiryData(Connection p_con, String p_service, String[] p_receiverMsisdn, String p_radioAll, String p_senderMsisdn, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("loadCP2PEnquiryData", " Entered " + " " + "p_subscriberID= " + p_senderMsisdn);
        }
        final String METHOD_NAME = "loadCP2PEnquiryData";
        CP2PTransferVO p2pTransferVO = null;
        ArrayList cp2pTrfVOList = null;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT ST.receiver_msisdn,sum(ST.transfer_value) as total_transfer,ST.service_type,(sum(ST.sender_tax1_value)+sum(ST.sender_tax2_value)) as sender_total_tax,");
            strBuff.append("sum(ST.sender_access_fee) as sender_total_access_fee,sum(ST.sender_transfer_value) as sender_total_transfer_value,(sum(ST.receiver_tax1_value)+sum(ST.receiver_tax2_value)) as receiver_total_tax,");
            strBuff.append("sum(ST.receiver_access_fee) as receiver_total_access_fee,sum(ST.receiver_transfer_value) as receiver_total_transfer_value,sum(ST.receiver_bonus_value) as receiver_total_bonus,count(1) ");
            strBuff.append("FROM subscriber_transfers ST ");
            strBuff.append("WHERE ST.sender_msisdn=? AND ");
            if (!p_radioAll.equalsIgnoreCase("ALL") && p_receiverMsisdn.length > 0) {
                String receicerNO = "";
                for (int j = 0; j < p_receiverMsisdn.length; j++) {
                    receicerNO = receicerNO + p_receiverMsisdn[j];
                    if (j != (p_receiverMsisdn.length - 1)) {
                        receicerNO += ",";
                    }
                }
                strBuff.append("ST.receiver_msisdn IN( " + receicerNO + " ) AND ");
            }
            strBuff.append("ST.service_type=? AND ");
            strBuff.append("ST.transfer_date>=? and ST.transfer_date<=? ");
            strBuff.append("GROUP BY receiver_msisdn,service_type");
            String selQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadCP2PEnquiryData", " Select Query " + selQuery);
            }
            try(PreparedStatement pstmt = p_con.prepareStatement(selQuery);)
            {
            cp2pTrfVOList = new ArrayList();
            int i = 1;
            pstmt.setString(i, p_senderMsisdn);
            i++;
            pstmt.setString(i, p_service);
            i++;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            i++;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                p2pTransferVO = new CP2PTransferVO();
                p2pTransferVO.setTotalCount(rs.getInt("count(1)"));
                p2pTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                p2pTransferVO.setTotalTransfer(BuddyMgtBL.getDisplayAmount(rs.getLong("total_transfer")));
                p2pTransferVO.setService(rs.getString("service_type"));
                p2pTransferVO.setSenderTotalTax(BuddyMgtBL.getDisplayAmountAsDouble(rs.getLong("sender_total_tax")));
                p2pTransferVO.setSenderTotalAccessFees(BuddyMgtBL.getDisplayAmountAsDouble(rs.getLong("sender_total_access_fee")));
                p2pTransferVO.setSenderTotalDebitAmount(BuddyMgtBL.getDisplayAmountAsDouble(rs.getLong("sender_total_transfer_value")));
                p2pTransferVO.setReceiverTotalTax(BuddyMgtBL.getDisplayAmountAsDouble(rs.getLong("receiver_total_tax")));
                p2pTransferVO.setReceiverTotalAccessFees(BuddyMgtBL.getDisplayAmountAsDouble(rs.getLong("receiver_total_access_fee")));
                p2pTransferVO.setReceiverTotalCreditAmount(BuddyMgtBL.getDisplayAmountAsDouble(rs.getLong("receiver_total_transfer_value")));
                p2pTransferVO.setTotalBonusAmount(BuddyMgtBL.getDisplayAmountAsDouble(rs.getLong("receiver_total_bonus")));
                cp2pTrfVOList.add(p2pTransferVO);
            }

        }
            }
        }catch (SQLException sqle) {
            log.errorTrace(METHOD_NAME, sqle);
        } finally {
            
        if (log.isDebugEnabled()) {
            log.debug("loadCP2PEnquiryData", " Exiting ");
        }
        }
        return cp2pTrfVOList;
    }
}
