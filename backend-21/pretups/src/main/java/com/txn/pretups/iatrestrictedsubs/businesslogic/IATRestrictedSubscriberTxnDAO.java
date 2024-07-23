package com.txn.pretups.iatrestrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;

public class IATRestrictedSubscriberTxnDAO {

    private static Log _log = LogFactory.getLog(IATRestrictedSubscriberTxnDAO.class.getName());

    /**
     * Method loadIATRestrictedSubscriberList
     * This method load the hashmap of the restricted users on the basis of the
     * ownerID and the ChanneluserID
     * 
     * @param p_con
     * @param p_userID
     * @return LinkedHashMap
     * @throws BTSLBaseException
     *             RestrictedSubscriberVO
     * @author babu.kunwar
     *         modified in query
     */

    public HashMap loadIATRestrictedSubscriberList(Connection p_con, String p_userID, String p_ownerID) throws BTSLBaseException {
        String methodName = "loadIATRestrictedSubscriberList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userID::=" + p_userID + ", p_ownerID::=" + p_ownerID);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
        HashMap<String, RestrictedSubscriberVO> hashMap = new HashMap<String, RestrictedSubscriberVO>();
        StringBuffer strBuff = new StringBuffer("SELECT rm.msisdn, rm.subscriber_id,rm.employee_code, rm.employee_name, ");
        strBuff.append("rm.monthly_limit,rm.min_txn_amount, rm.max_txn_amount, rm.total_txn_amount,rm.monthly_limit, ");
        strBuff.append("rm.subscriber_type,rm.language,rm.country,rm.country_code ");
        strBuff.append("FROM restricted_msisdns RM ");
        strBuff.append("WHERE rm.msisdn NOT IN ( ");
        strBuff.append("SELECT  sbd.msisdn ");
        strBuff.append("FROM scheduled_batch_detail sbd, scheduled_batch_master sbm ");
        strBuff.append("WHERE sbd.batch_id=sbm.batch_id AND sbm.owner_id=? AND sbm.batch_type='" + PretupsI.BATCH_TYPE_CORPORATE + "' AND ");
        strBuff.append("sbd.status IN ('" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "', '" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "')) ");
        strBuff.append("AND RM.owner_id =? AND RM. channel_user_id =?  AND RM.status=? AND RM.restricted_type=?");
        strBuff.append("ORDER BY rm.country_code ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, p_userID);
            pstmtSelect.setString(i++, PretupsI.RES_MSISDN_STATUS_ASSOCIATED);
            pstmtSelect.setString(i++, PretupsI.RESTRICTED_TYPE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                iatRestrictedSubscriberVO = new RestrictedSubscriberVO();
                iatRestrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                iatRestrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                iatRestrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                iatRestrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                iatRestrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                iatRestrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                iatRestrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                iatRestrictedSubscriberVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                iatRestrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                iatRestrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                iatRestrictedSubscriberVO.setLanguage(rs.getString("language"));
                iatRestrictedSubscriberVO.setCountry(rs.getString("country"));
                iatRestrictedSubscriberVO.setCountryCode(rs.getInt("country_code"));
                hashMap.put(iatRestrictedSubscriberVO.getMsisdn(), iatRestrictedSubscriberVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberTxnDAO[loadIATRestrictedSubscriberList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberTxnDAO[loadIATRestrictedSubscriberList]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting hashMap size=" + hashMap.size());
            }
        }
        return hashMap;
    }

}
