package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class ChannelTransferPaymentDAO {

    private static Log _log = LogFactory.getLog(ChannelTransferPaymentDAO.class.getName());

    private int addChannelTransferPayment(Connection p_con, List p_transferPaymentList, String p_transferId, Date p_transferDate) throws BTSLBaseException {
        final String methodName = "addChannelTransferPayment";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered TransferItemList Size: " );
        	loggerValue.append(p_transferPaymentList.size());
        	loggerValue.append(" TransferId : ");
        	loggerValue.append(p_transferId);
        	loggerValue.append("?p_transferDate=");
        	loggerValue.append(p_transferDate);
            _log.debug(methodName, loggerValue );
        }

        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" INSERT INTO channel_transfer_payments ( ");
            strBuff.append(" transfer_id,  payment_id,  transfer_date, transfer_date_type, payment_status ,  payment_amount) ");
            strBuff.append(" VALUES  ");
            strBuff.append(" (?,?,?,?,?,?) ");

            final String query = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query: " + query);
            }

            psmt = p_con.prepareStatement(query);
            ChannelTransferPaymentVO ctpVO = null;
            for (int i = 0, k = p_transferPaymentList.size(); i < k; i++) {
            	ctpVO = (ChannelTransferPaymentVO) p_transferPaymentList.get(i);

                psmt.clearParameters();
                int m = 0;
                ++m;
                psmt.setString(m, p_transferId);
                ++m;
                psmt.setString(m, ctpVO.getPaymentId());
                ++m;
                psmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(ctpVO.getTransferDate()));
                ++m;
                psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(ctpVO.getTransferDate()));
                ++m;
                psmt.setString(m, ctpVO.getPaymentStatus());
                ++m;
                psmt.setLong(m, ctpVO.getPaymentAmount());
                updateCount = psmt.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); 

                if (updateCount <= 0) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelTransferPaymentDAO[addChannelTransferPayment]", "", "", "",
                        "BTSLBaseException: update count <=0");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        }// end of try
        catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferPaymentDAO[addChannelTransferPayment]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferPaymentDAO[addChannelTransferPayment]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue );
            }
        }// end of finally

        return updateCount;
    }
    
    
    
    public ArrayList loadChannelTransferPayment(Connection p_con, String p_transferId, Date p_transferDate) throws BTSLBaseException {
        final String methodName = "loadChannelTransferPayment";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferId=" + p_transferId + ",p_transferDate=" + p_transferDate);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferPaymentVO ctpVO = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT transfer_id,  payment_id,  transfer_date, transfer_date_type, payment_status ,  payment_amount ");
        strBuff.append(" FROM channel_transfer_payments WHERE transfer_id = ? and transfer_date = ?");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_transferId);
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_transferDate));
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	ctpVO = new ChannelTransferPaymentVO();
            	ctpVO.setTransferId(p_transferId);
            	ctpVO.setTransferDate(p_transferDate);
            	ctpVO.setTransferDateTime(rs.getTimestamp("transfer_date_type"));
            	ctpVO.setPaymentId(rs.getString("payment_id"));
            	ctpVO.setPaymentStatus(rs.getString("payment_status"));
            	ctpVO.setPaymentAmount(rs.getLong("payment_amount"));
                list.add(ctpVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferPaymentDAO[loadChannelTransferPayment]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferPaymentDAO[loadChannelTransferPayment]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: loadChannelVoucherItemsList size=" + list.size());
            }
        }
        return list;
    }
    
    public int updateChannelTransferPayment(Connection p_con, ArrayList p_itemsList) throws BTSLBaseException {
        final String methodName = "updateChannelTransferPayment";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        int update_count = 0;
        final StringBuffer strBuff = new StringBuffer("UPDATE channel_transfer_payments SET payment_status=? ");
        strBuff.append(" where transfer_id = ? and transfer_date = ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try(PreparedStatement psmt = p_con.prepareStatement(sqlSelect);) {
            ChannelTransferPaymentVO ctpVO = null;
            for (int i = 0, k = p_itemsList.size(); i < k; i++) {
                ctpVO = (ChannelTransferPaymentVO) p_itemsList.get(i);
                int m = 0;
                ++m;
                psmt.setString(m, ctpVO.getPaymentStatus());
                ++m;
                psmt.setString(m, ctpVO.getTransferId());
                ++m;
                psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(ctpVO.getTransferDate()));
                update_count = psmt.executeUpdate();
                psmt.clearParameters();
                update_count = BTSLUtil.getInsertCount(update_count);
                if (update_count < 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferPaymentDAO[updateChannelTransferPayment]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelTransferPayment", "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferPaymentDAO[updateChannelTransferPayment]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelTransferPayment", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
                  loggerValue.setLength(0);
                  loggerValue.append("Exiting: update count  =");
                  loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue);
            }
        }
        return update_count;

    }

}
