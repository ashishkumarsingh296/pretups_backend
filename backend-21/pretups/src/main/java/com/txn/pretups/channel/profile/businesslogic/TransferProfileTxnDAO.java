package com.txn.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;

public class TransferProfileTxnDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for TransferProfileDAO.
     */
    public TransferProfileTxnDAO() {
        super();
    }

    /**
     * Method loadTrfProfileForCategoryCode.
     * 
     * @author vikas.kumar
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_isProductLoadRequired
     *            boolean
     * @return TransferProfileVO
     * @throws BTSLBaseException
     */
    public TransferProfileVO loadDefaultTrfProfileForCategoryCode(Connection p_con, String p_categoryCode, String p_networkCode) throws BTSLBaseException {
        String methodName = "loadDefaultTrfProfileForCategoryCode";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered   p_categoryCode " + p_categoryCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT ");
        strBuff.append(" profile_id, short_name, profile_name, status, description ");
        strBuff.append(" FROM  ");
        strBuff.append(" transfer_profile ");
        strBuff.append(" WHERE ");
        strBuff.append(" category_code = ? AND parent_profile_id=? AND ");
        strBuff.append(" status = ?	AND network_code = ? and IS_DEFAULT = ? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        TransferProfileVO countsVO = null;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, PretupsI.PARENT_PROFILE_ID_USER);
            pstmt.setString(3, PretupsI.YES);
            pstmt.setString(4, p_networkCode);
            pstmt.setString(5, PretupsI.YES);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                countsVO = new UserTransferCountsVO();
                countsVO.setProfileId(rs.getString("profile_id"));
                countsVO.setShortName(rs.getString("short_name"));
                countsVO.setProfileName(rs.getString("profile_name"));
                countsVO.setStatus(rs.getString("status"));
                countsVO.setDescription(rs.getString("description"));

            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileTxnDAO[loadDefaultTrfProfileForCategoryCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileTxnDAO[loadDefaultTrfProfileForCategoryCode]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting:  UserTransferCountsVO =" + countsVO);
            }
        }
        return countsVO;
    }

    /**
     * this method use for load the list of products with the controlling values
     * i.e. form CAT LEVEL profile
     * or from USER LEVEL profile, those are associated with transfer profile
     * Method: loadTrfProfileProductWithCntrlValue
     * Method is changed for(CR00047) comparision of category and user transfer
     * profile for alerting count and value
     * 
     * @param p_con
     * @param p_profileID
     * @return arrayList java.util.Array
     * @throws BTSLBaseException
     */
    public ArrayList loadTrfProfileProductWithCntrlValue(Connection p_con, String p_profileID) throws BTSLBaseException {
        String methodName = "loadTrfProfileProductWithCntrlValue";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_profileID=" + p_profileID);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList arrayList = new ArrayList();
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        strBuff.append("GREATEST(tpp.c2s_min_txn_amt,catpp.c2s_min_txn_amt) c2s_min_txn_amt, ");
        strBuff.append("LEAST(tpp.c2s_max_txn_amt,catpp.c2s_max_txn_amt) c2s_max_txn_amt, ");
        strBuff.append("LEAST(tpp.max_balance,catpp.max_balance) max_balance,tpp.alerting_balance,tpp.product_code, ");
        strBuff.append("LEAST(tpp.max_pct_transfer_allowed,catpp.max_pct_transfer_allowed) max_pct_transfer_allowed ");
        strBuff.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuff.append("WHERE tpp.profile_id=? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        strBuff.append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? ");
        strBuff.append("AND catp.status='Y' AND tp.network_code = catp.network_code	");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadTransferProfileProducts", "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_profileID);
            pstmt.setString(2, PretupsI.PARENT_PROFILE_ID_CATEGORY);

            TransferProfileProductVO transferProfileProductVO = null;
            rs = pstmt.executeQuery();
            while (rs.next()) {
                transferProfileProductVO = new TransferProfileProductVO();
                transferProfileProductVO.setProductCode(rs.getString("product_code"));
                transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                transferProfileProductVO.setMaxBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMaxBalanceAsLong()));
                transferProfileProductVO.setMinBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()));
                transferProfileProductVO.setAltBalanceLong(rs.getLong("alerting_balance"));
                transferProfileProductVO.setAltBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getAltBalanceLong()));
                transferProfileProductVO.setAllowedMaxPercentageInt(rs.getInt("max_pct_transfer_allowed"));
                transferProfileProductVO.setAllowedMaxPercentage(String.valueOf(transferProfileProductVO.getAllowedMaxPercentageInt()));

                transferProfileProductVO.setC2sMinTxnAmtAsLong(rs.getLong("c2s_min_txn_amt"));
                transferProfileProductVO.setC2sMaxTxnAmtAsLong(rs.getLong("c2s_max_txn_amt"));
                transferProfileProductVO.setC2sMinTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMinTxnAmtAsLong()));
                transferProfileProductVO.setC2sMaxTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMaxTxnAmtAsLong()));

                arrayList.add(transferProfileProductVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileTxnDAO[loadTrfProfileProductWithCntrlValue]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileTxnDAO[loadTrfProfileProductWithCntrlValue]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: ArrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

}
