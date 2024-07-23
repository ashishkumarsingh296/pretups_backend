package com.txn.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;

public class ChannelTransferTxnDAO {

    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(ChannelTransferTxnDAO.class.getName());
    private static OperatorUtilI _operatorUtilI = null;
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {

            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "", "Exception while loading the operator util class in class :" + ChannelTransferTxnDAO.class.getName() + ":" + e.getMessage());
        }
    }

    /**
     * This method load channelTransferVO of last transfer ID
     * 
     * @author manoj
     * @param p_con
     * @param p_lastTransferID
     *            java.lang.String
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */
    public ChannelTransferVO loadLastTransfersStatusVO(Connection p_con, String p_lastTransferID) throws BTSLBaseException {
        final String methodName = "loadLastTransfersStatusVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered   p_lastTransferID " + p_lastTransferID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelTransferVO transferVO = null;
        StringBuffer strBuff = new StringBuffer(" SELECT CT.transfer_id,CT.type,CT.net_payable_amount,CT.status,CT.created_on, ");
        strBuff.append("KV.value,KV2.value statusname ,CT.to_msisdn");
        strBuff.append("FROM channel_transfers CT,key_values KV,key_values KV2 WHERE transfer_id=? AND CT.type=KV.key AND KV.type=? ");
        strBuff.append("AND CT.status=KV2.key AND KV2.type=? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_lastTransferID);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_C2C_TYPE);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_STATUS);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferType(rs.getString("value"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setCreatedOn(rs.getTimestamp("created_on"));
                transferVO.setStatus(rs.getString("statusname"));
                transferVO.setToUserMsisdn(rs.getString("to_msisdn"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadLastTransfersStatusVO]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadLastTransfersStatusVO]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting:  transferVO =" + transferVO);
            }
        }
        return transferVO;
    }

    /**
     * this method load product's transfer value list f behalf of last
     * transfer_id
     * 
     * @author manoj
     * @param p_con
     * @param p_lastTransferID
     *            java.lang.String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadLastTransfersItemList(Connection p_con, String p_lastTransferID) throws BTSLBaseException {
        final String methodName = "loadLastTransfersItemList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered   p_lastTransferID " + p_lastTransferID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer(" SELECT CTI.approved_quantity,P.product_name,P.short_name,P.product_short_code");
        strBuff.append(" FROM channel_transfers_items CTI,products P");
        strBuff.append(" WHERE CTI.transfer_id=?  AND CTI.product_code=P.product_code");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_lastTransferID);
            rs = pstmt.executeQuery();
            ChannelTransferItemsVO transferVO = null;
            while (rs.next()) {
                transferVO = new ChannelTransferItemsVO();
                transferVO.setApprovedQuantity(rs.getLong("approved_quantity"));
                transferVO.setProductName(rs.getString("product_name"));
                transferVO.setShortName(rs.getString("short_name"));
                transferVO.setProductShortCode(rs.getLong("product_short_code"));
                arrayList.add(transferVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadLastTransfersItemList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadLastTransfersStatusList", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadLastTransfersItemList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

    /**
     * Method isC2SRulesListForChannelUserAssociation.
     * 
     * @param p_con
     *            Connection
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public boolean isC2SRulesListForChannelUserAssociation(Connection p_con, String p_networkCode, String p_fromCategoryCode, String p_toCategoryCode) throws BTSLBaseException {

        final String methodName = "isC2SRulesListForChannelUserAssociation";
        if (_log.isDebugEnabled()) {
            _log.debug("Entered" + methodName, "p_networkCode : " + p_networkCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isRuleExists = false;
        try {
            StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append("SELECT 1 ");
            selectQueryBuff.append("FROM chnl_transfer_rules  ");
            selectQueryBuff.append("WHERE type = ? AND parent_association_allowed = ? AND network_code = ? AND status !='N' AND from_category=? AND to_category =? ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);

            pstmtSelect.setString(1, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);
            pstmtSelect.setString(2, TypesI.YES);
            pstmtSelect.setString(3, p_networkCode);
            pstmtSelect.setString(4, p_fromCategoryCode);
            pstmtSelect.setString(5, p_toCategoryCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isRuleExists = true;
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isC2SRulesListForChannelUserAssociation]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadC2SRulesListForChannelUserAssociation", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[isC2SRulesListForChannelUserAssociation]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadC2SRulesListForChannelUserAssociation", "error.general.processing");
        }// end of catch
        finally {
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
                _log.debug(methodName, "Exiting isRuleExists=" + isRuleExists);
            }
        }// end of finally
        return isRuleExists;
    }

    /**
     * Load the enquiry Channel Transfer List
     * 
     * @param p_con
     * @param p_transferID
     * @param p_userID
     * @param p_fromDate
     * @param p_toDate
     * @param p_status
     * @param p_userCode
     *            TODO
     * @param p_type
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadO2CChannelTransfersListForSAP(Connection p_con, Date p_fromDate, Date p_toDate) throws BTSLBaseException {

        final String methodName = "loadO2CChannelTransfersListForSAP";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  FromDate:" + p_fromDate + " ToDate:" + p_toDate);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer(" SELECT ct.transfer_sub_type,ct.TRANSFER_TYPE,ct.requested_quantity,ct.PRODUCT_TYPE,pd.PRODUCT_CODE,u.EXTERNAL_CODE,");
        strBuff.append(" ct.transfer_id, ct.network_code, ");
        strBuff.append(" ct.transfer_date, ct.first_approved_by ,u1.USER_NAME,ct.first_approved_on, ct.status, ct.payable_amount, ");
        strBuff.append(" u.msisdn,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, ");
        strBuff.append(" ct.transfer_category ");
        strBuff.append("FROM channel_transfers ct , users u, users u1,Products pd ");
        strBuff.append(" WHERE ");
        strBuff.append(" ct.to_user_id = u.user_id");
        strBuff.append(" AND ct.transfer_date >= ? and u1.USER_ID=ct.FIRST_APPROVED_BY");
        strBuff.append(" AND ct.transfer_date <= ?");
        strBuff.append(" AND ct.TYPE = ?");
        strBuff.append(" AND ct.status = ?");
        strBuff.append(" AND (ct.ext_txn_no IS NULL OR ct.ext_txn_date IS NULL)");
        strBuff.append(" AND transfer_sub_type =?");
        strBuff.append("  AND u.status <> ?");
        strBuff.append("  AND ct.TRANSFER_CATEGORY = ?");
        strBuff.append(" AND pd.product_type = ct.product_type ");
        strBuff.append("ORDER BY ct.created_on DESC, ct.transfer_sub_type");

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        ArrayList enquiryItemsList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            if (p_fromDate != null) {
                pstmt.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            } else {
                pstmt.setDate(++m, null);
            }

            if (p_toDate != null) {
                pstmt.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            } else {
                pstmt.setDate(++m, null);
            }
            pstmt.setString(++m, "O2C");
            pstmt.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(++m, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            pstmt.setString(++m, PretupsI.NO);
            pstmt.setString(++m, PretupsI.TRANSFER_CATEGORY_SALE);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferType(rs.getString("TRANSFER_TYPE"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                transferVO.setFromUserCode(rs.getString("EXTERNAL_CODE"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                if (transferVO.getTransferDate() != null) {
                    transferVO.setTransferDateAsString(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate()));
                }
                transferVO.setFirstApprovedBy(rs.getString("USER_NAME"));
                transferVO.setFirstApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_approved_on")));
                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("from_msisdn"));
                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("to_msisdn"));
                } else {
                    transferVO.setUserMsisdn(rs.getString("msisdn"));
                }
                transferVO.setTransferCategory(rs.getString("transfer_category"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setProductType(rs.getString("PRODUCT_TYPE"));
                transferVO.setProductCode(rs.getString("PRODUCT_CODE"));
                transferVO.setStatus(rs.getString("status"));
                enquiryItemsList.add(transferVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadO2CChannelTransfersListForSAP]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadO2CChannelTransfersListForSAP]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, ex.getMessage());
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
                _log.debug(methodName, "Exiting:  arrayList Size =" + enquiryItemsList.size());
            }
        }
        return enquiryItemsList;
    }

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param isOrderApproved
     * @return
     * @throws BTSLBaseException
     */
    public int updateExternalCodeForAutoO2CRequest(Connection p_con, String p_transactionid, String p_extcode, Date p_exttxndate) throws BTSLBaseException {
        final String methodName = "updateExternalCodeForAutoO2CRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transactionid : " + p_transactionid + "p_extcode" + p_extcode + "p_exttxndate" + p_exttxndate);
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        Date date = null;
        try {
            date = new Date();
            StringBuffer strBuff = new StringBuffer(" UPDATE  channel_transfers SET  ");
            strBuff.append(" ext_txn_no = ? , ext_txn_date =  ? , modified_by = ?, modified_on = ? ");
            strBuff.append(" WHERE transfer_id = ?  AND status = ? ");
            String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            psmt = (PreparedStatement) p_con.prepareStatement(query);
            int i = 0;
            psmt.setString(++i, p_extcode);
            psmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_exttxndate));
            psmt.setString(++i, PretupsI.SAP_MODULE);
            psmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(date));
            psmt.setString(++i, p_transactionid);
            psmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);

            /**
             * to check the record is modified in between or not. if modified
             * then throw error message to the user
             */
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.AUTO_O2C_EXTCODE_UPDATE_INVALIDDATA);
            }

        }

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[updateExternalCodeForAutoO2CRequest]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }// end of catch
        catch (BTSLBaseException bbe) {

            _log.errorTrace(methodName, bbe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[updateExternalCodeForAutoO2CRequest]", "", "", "", "BTSLBaseException :" + bbe.getMessage());

            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.AUTO_O2C_EXTCODE_UPDATE_INVALIDDATA);

        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[updateExternalCodeForAutoO2CRequest]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting Success :" + updateCount);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Load the enquiry Channel Transfer List
     * 
     * @param p_con
     * @param p_transferID
     * @param p_userID
     * @param p_fromDate
     * @param p_toDate
     * @param p_status
     * @param p_userCode
     *            TODO
     * @param p_type
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadO2CChannelTransfersListForSAPUPdate(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadO2CChannelTransfersListForSAPUPdate";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered  TransferNumber: " + p_transferID);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer(" SELECT ct.transfer_sub_type,ct.requested_quantity,ct.PRODUCT_TYPE,pd.PRODUCT_CODE,u.EXTERNAL_CODE,");
        strBuff.append(" ct.transfer_id, ct.network_code, ");
        strBuff.append(" ct.transfer_date, ct.first_approved_by ,ct.first_approved_on, ct.status, ct.payable_amount, ");
        strBuff.append(" u.msisdn,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, ");
        strBuff.append(" ct.transfer_category ");
        strBuff.append(" from channel_transfers ct , users u,Products pd ");
        strBuff.append(" WHERE ");
        strBuff.append(" ct.to_user_id = u.user_id");
        strBuff.append(" and ct.transfer_id = ? ");
        strBuff.append(" AND ct.TYPE = ?");
        strBuff.append(" AND ct.status = ?");
        strBuff.append(" AND transfer_sub_type =?");
        strBuff.append("  AND u.status <> ?");
        strBuff.append(" AND pd.product_type = ct.product_type");
        strBuff.append(" ORDER BY ct.created_on DESC, ct.transfer_sub_type");

        if (_log.isDebugEnabled()) {
            _log.debug("loadO2CChannelTransfersListForSAP", "QUERY sqlSelect=" + strBuff);
        }
        ArrayList enquiryItemsList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            pstmt.setString(++m, p_transferID);
            pstmt.setString(++m, "O2C");
            pstmt.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(++m, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            pstmt.setString(++m, PretupsI.NO);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                if (transferVO.getTransferDate() != null) {
                    transferVO.setTransferDateAsString(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate()));
                }
                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("from_msisdn"));
                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("to_msisdn"));
                } else {
                    transferVO.setUserMsisdn(rs.getString("msisdn"));
                }
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                enquiryItemsList.add(transferVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadO2CChannelTransfersListForSAPUPdate]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
        }

        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferTxnDAO[loadO2CChannelTransfersListForSAPUPdate]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting:  arrayList Size =" + enquiryItemsList.size());
            }
        }
        return enquiryItemsList;
    }

    /**
     * @author diwakar
     *         Method loadC2SRulesListForChannelUserAssociation.
     * @param p_con
     *            Connection
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SRulesListForChannelOperatorUserAssociation(Connection p_con, String p_networkCode, String p_parentCatCode) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("Entered loadC2SRulesListForChannelOperatorUserAssociation", "p_networkCode : " + p_networkCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelTransferRuleVO channelTransferRuleVO = null;
        ArrayList list = new ArrayList();
        final String methodName = "loadC2SRulesListForChannelOperatorUserAssociation";
        try {
            StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append("SELECT from_category,to_category ");
            selectQueryBuff.append("FROM chnl_transfer_rules  ");
            selectQueryBuff.append("WHERE type = ? AND parent_association_allowed = ? AND network_code = ? AND status !='N'");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);

            if (BTSLUtil.isNullString(p_parentCatCode)) {
                pstmtSelect.setString(1, PretupsI.TRANSFER_RULE_TYPE_OPT);
            } else {
                pstmtSelect.setString(1, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);
            }
            // pstmtSelect.setString(1, PretupsI.TRANSFER_RULE_TYPE_OPT);
            pstmtSelect.setString(2, TypesI.YES);
            pstmtSelect.setString(3, p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                channelTransferRuleVO = new ChannelTransferRuleVO();

                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
                channelTransferRuleVO.setToCategory(rs.getString("to_category"));

                list.add(channelTransferRuleVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadC2SRulesListForChannelOperatorUserAssociation]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadC2SRulesListForChannelOperatorUserAssociation]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
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
                _log.debug(methodName, "Exiting List size=" + list.size());
            }
        }// end of finally

        return list;
    }

}
