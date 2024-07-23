package com.txn.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class ChannelTransferRuleTxnDAO {

    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for ChannelTransferRuleTxnDAO.
     */
    public ChannelTransferRuleTxnDAO() {
        super();
    }

    /**
     * transfer rule exist or not
     * Method :loadTransferRulesCategoryListForDP
     * 
     * @author Nilesh kumar
     * @param p_con
     *            Connection
     * @param p_parentID
     *            String
     * @param p_childID
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isTransferRuleExists(Connection p_con, String p_parentID, String p_childID) throws BTSLBaseException {

        final String methodName = "isTransferRuleExists";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params  p_parentID::" + p_parentID + "p_childID " + p_childID);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM chnl_transfer_rules CTR, users U ");
        sqlBuff.append("WHERE CTR.from_category=(SELECT category_code FROM users WHERE user_id=?) AND CTR.to_category=(SELECT category_code FROM users WHERE user_id=?) AND CTR.type=? AND CTR.status='Y' AND CTR.parent_association_allowed=? ");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_parentID);
            pstmtSelect.setString(2, p_childID);
            pstmtSelect.setString(3, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);
            pstmtSelect.setString(4, PretupsI.YES);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "channelTransferRuleTxnDAO[isTransferRuleExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "channelTransferRuleTxnDAO[isTransferRuleExists]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
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
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isExists found=" + found);
            }
        }
        return found;
    }

}
