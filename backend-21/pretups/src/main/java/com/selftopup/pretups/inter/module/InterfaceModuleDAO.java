package com.selftopup.pretups.inter.module;

/**
 * @(#)InterfaceModuleDAO.java
 *                             Copyright(c) 2005, Bharti Telesoft Int. Public
 *                             Ltd.
 *                             All Rights Reserved
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Abhijit Chauhan June 27,2005 Initial Creation
 *                             ------------------------------------------------
 *                             ------------------------------------------------
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;

public class InterfaceModuleDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    public InterfaceModuleDAO() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Add Interface Module Details
     * 
     * @param p_con
     * @param p_interfaceModuleVO
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public int addInterfaceModuleDetails(Connection p_con, InterfaceModuleVO p_interfaceModuleVO) throws SQLException, Exception {
        int addCount = -1;
        final String methodName = "addInterfaceModuleDetails()";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entering");
        ArrayList interfaceList = null;
        PreparedStatement pstmtInsert = null;
        StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO interface_transactions (txn_id, msisdn,");
        insertQueryBuff.append(" request_value, previous_balance,post_balance, validity, interface_type,interface_id,");
        insertQueryBuff.append(" interface_response_code, reference_id,card_group, service_class, msisdn_previous_expiry,");
        insertQueryBuff.append(" msisdn_new_expiry, txn_status,txn_type, txn_date_time,is_processed,in_start_time,in_end_time,url_id,bonus_value,bonus_validity) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "::Insert Query= " + insertQuery);
        try {
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, p_interfaceModuleVO.getTxnID());
            pstmtInsert.setString(2, p_interfaceModuleVO.getMsisdn());
            pstmtInsert.setLong(3, p_interfaceModuleVO.getRequestValue());
            pstmtInsert.setLong(4, p_interfaceModuleVO.getPreviousBalance());
            pstmtInsert.setLong(5, p_interfaceModuleVO.getPostBalance());
            pstmtInsert.setInt(6, p_interfaceModuleVO.getValidity());
            pstmtInsert.setString(7, p_interfaceModuleVO.getInterfaceType());
            pstmtInsert.setString(8, p_interfaceModuleVO.getInterfaceID());
            pstmtInsert.setString(9, p_interfaceModuleVO.getInterfaceResonseCode());
            pstmtInsert.setString(10, p_interfaceModuleVO.getReferenceID());
            pstmtInsert.setString(11, p_interfaceModuleVO.getCardGroup());
            pstmtInsert.setString(12, p_interfaceModuleVO.getServiceClass());
            pstmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(p_interfaceModuleVO.getMsisdnPreviousExpiry()));
            pstmtInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(p_interfaceModuleVO.getMsisdnNewExpiry()));
            pstmtInsert.setString(15, p_interfaceModuleVO.getTxnStatus());
            pstmtInsert.setString(16, p_interfaceModuleVO.getTxnType());
            pstmtInsert.setTimestamp(17, BTSLUtil.getTimestampFromUtilDate(p_interfaceModuleVO.getTxnDateTime()));
            pstmtInsert.setString(18, p_interfaceModuleVO.getTxnResponseReceived());
            pstmtInsert.setLong(19, p_interfaceModuleVO.getTxnStartTime());
            pstmtInsert.setLong(20, p_interfaceModuleVO.getTxnEndTime());
            pstmtInsert.setString(21, p_interfaceModuleVO.getUrlID());
            pstmtInsert.setLong(22, p_interfaceModuleVO.getBonusValue());
            pstmtInsert.setInt(23, p_interfaceModuleVO.getBonusValidity());
            addCount = pstmtInsert.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug(methodName, "::Query Executed= " + insertQuery);
        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }

        catch (Exception e) {
            _log.error(methodName, " Exception " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        finally {
            try {
                if (pstmtInsert != null)
                    pstmtInsert.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug(methodName, " Exiting add Count:" + addCount);
        }
        return addCount;
    }

    public int updateInterfaceModuleDetails(Connection p_con, String p_txnID, InterfaceModuleVO p_interfaceModuleVO) throws SQLException, Exception {
        int updateCount = -1;
        if (_log.isDebugEnabled())
            _log.debug("updateInterfaceModuleDetails()", "Entering with p_txnID=" + p_txnID);
        ArrayList interfaceList = null;
        PreparedStatement pstmtUpdate = null;
        StringBuffer insertQueryBuff = new StringBuffer("UPDATE interface_transactions ");
        insertQueryBuff.append(" SET previous_balance=?,post_balance=?, validity=?, ");
        insertQueryBuff.append(" interface_response_code=? , service_class=?, msisdn_previous_expiry=? ,");
        insertQueryBuff.append(" msisdn_new_expiry=?, txn_status=? ,txn_type=?,is_processed=? ,in_start_time=?,in_end_time=?,url_id=?,bonus_value=?,bonus_validity=? ");
        insertQueryBuff.append(" WHERE txn_id=? ");
        String insertQuery = insertQueryBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug("updateInterfaceModuleDetails()", "::Insert Query= " + insertQuery);
        try {
            int i = 1;
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtUpdate.setLong(i++, p_interfaceModuleVO.getPreviousBalance());
            pstmtUpdate.setLong(i++, p_interfaceModuleVO.getPostBalance());
            pstmtUpdate.setInt(i++, p_interfaceModuleVO.getValidity());
            pstmtUpdate.setString(i++, p_interfaceModuleVO.getInterfaceResonseCode());
            pstmtUpdate.setString(i++, p_interfaceModuleVO.getServiceClass());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_interfaceModuleVO.getMsisdnPreviousExpiry()));
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_interfaceModuleVO.getMsisdnNewExpiry()));
            pstmtUpdate.setString(i++, p_interfaceModuleVO.getTxnStatus());
            pstmtUpdate.setString(i++, p_interfaceModuleVO.getTxnType());
            pstmtUpdate.setString(i++, p_interfaceModuleVO.getTxnResponseReceived());
            pstmtUpdate.setLong(i++, p_interfaceModuleVO.getTxnStartTime());
            pstmtUpdate.setLong(i++, p_interfaceModuleVO.getTxnEndTime());
            pstmtUpdate.setString(i++, p_interfaceModuleVO.getUrlID());
            pstmtUpdate.setLong(i++, p_interfaceModuleVO.getBonusValue());
            pstmtUpdate.setInt(i++, p_interfaceModuleVO.getBonusValidity());
            pstmtUpdate.setString(i++, p_interfaceModuleVO.getTxnID());
            updateCount = pstmtUpdate.executeUpdate();
            if (_log.isDebugEnabled())
                _log.debug("updateInterfaceModuleDetails()", "::Query Executed= " + insertQuery);
        }

        catch (SQLException sqle) {
            _log.error("updateInterfaceModuleDetails()", "SQLException " + sqle.getMessage());
            sqle.printStackTrace();
            throw sqle;
        }

        catch (Exception e) {
            _log.error("updateInterfaceModuleDetails()", " Exception " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        finally {
            try {
                if (pstmtUpdate != null)
                    pstmtUpdate.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("updateInterfaceModuleDetails()", " Exiting updateCount: " + updateCount);
        }
        return updateCount;
    }
}
