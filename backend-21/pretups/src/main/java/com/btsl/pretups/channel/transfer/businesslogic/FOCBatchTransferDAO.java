/**
 * @# FOCBatchTransferDAO.java
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    June 22, 2006 Amit Ruwali Initial creation
 *    July 20, 2006 Sandeep Goel Modification
 *    Aug 05, 2006 Sandeep Goel Modification ID TOG001
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2006 Bharti Telesoft Ltd.
 *    This class use for Batch FOC Transfer.
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchFocFileProcessLog;
import com.btsl.pretups.logging.DirectPayOutErrorLog;
import com.btsl.pretups.logging.DirectPayOutSuccessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;

public class FOCBatchTransferDAO {
	
	private FOCBatchTransferQry focBatchTransferQry = (FOCBatchTransferQry) ObjectProducer.getObject(QueryConstants.FOC_BATCH_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
	/**
     * Field log.
     */
    private final Log log = LogFactory.getLog(this.getClass().getName());
	private String errorGeneralProcessing = "error.general.processing";
	private String errorGeneralSqlProcessing = "error.general.sql.processing";
	private String  sqlException = "SQLException : ";
	private String  exception = "Exception:";
    /**
     * 
     */
    public FOCBatchTransferDAO() {
        super();
    }

    

    /**
     * Thiis methid will load the data from the foc_batch_items table
     * corresponding to batch id.
     * The result will be returned as LinkedHasMap. The key will be
     * batch_detail_id for this map.
     * 
     * @param con
     * @param batchId
     * @param itemStatus
     * @return
     * @throws BTSLBaseException
     */
    public LinkedHashMap loadBatchItemsMap(Connection con, String batchId, String itemStatus) throws BTSLBaseException {
        final String methodName = "loadBatchItemsMap";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_batchId= " + batchId + ", p_itemStatus= " + itemStatus);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
        final String sqlSelect = focBatchTransferQry.loadBatchItemsMapQry(itemStatus);
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect = " + sqlSelect);
        }
        final LinkedHashMap map = new LinkedHashMap();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final FOCBatchItemsVO focBatchItemsVO = new FOCBatchItemsVO();
                focBatchItemsVO.setBatchId(batchId);
                final String batchDetailId = rs.getString("batch_detail_id");
                focBatchItemsVO.setBatchDetailId(batchDetailId);
                focBatchItemsVO.setCommWalletType(rs.getString("txn_wallet"));
                focBatchItemsVO.setCategoryName(rs.getString("category_name"));
                focBatchItemsVO.setMsisdn(rs.getString("msisdn"));
                focBatchItemsVO.setUserId(rs.getString("user_id"));
                focBatchItemsVO.setStatus(rs.getString("status"));
                focBatchItemsVO.setGradeName(rs.getString("grade_name"));
                focBatchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                focBatchItemsVO.setExtTxnDate(rs.getDate("ext_txn_date"));
                focBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                focBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                focBatchItemsVO.setInitiatedBy(rs.getString("created_by"));
                focBatchItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
                focBatchItemsVO.setLoginID(rs.getString("login_id"));
                focBatchItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
                focBatchItemsVO.setModifiedBy(rs.getString("modified_by"));
                focBatchItemsVO.setReferenceNo(rs.getString("reference_no"));
                focBatchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                focBatchItemsVO.setExtTxnDate(rs.getTimestamp("ext_txn_date"));
                focBatchItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
                focBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                focBatchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                focBatchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                focBatchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                focBatchItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
                focBatchItemsVO.setCommissionType(rs.getString("commission_type"));
                focBatchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                focBatchItemsVO.setCommissionValue(rs.getLong("commission_value"));
                focBatchItemsVO.setTax1Type(rs.getString("tax1_type"));
                focBatchItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                focBatchItemsVO.setTax1Value(rs.getLong("tax1_value"));
                focBatchItemsVO.setTax2Type(rs.getString("tax2_type"));
                focBatchItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                focBatchItemsVO.setTax2Value(rs.getLong("tax2_value"));
                focBatchItemsVO.setTax3Type(rs.getString("tax3_type"));
                focBatchItemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                focBatchItemsVO.setTax3Value(rs.getLong("tax3_value"));

                focBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                focBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                focBatchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                focBatchItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                focBatchItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                focBatchItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                focBatchItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                focBatchItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                focBatchItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                focBatchItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                focBatchItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                focBatchItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                focBatchItemsVO.setCancelledBy(rs.getString("cancelled_by"));
                focBatchItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
                focBatchItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
                focBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                focBatchItemsVO.setCategoryCode(rs.getString("category_code"));
                focBatchItemsVO.setFirstApproverName(rs.getString("first_approver_name"));
                focBatchItemsVO.setSecondApproverName(rs.getString("second_approver_name"));
                focBatchItemsVO.setInitiaterName(rs.getString("initiater_name"));
                focBatchItemsVO.setExternalCode(rs.getString("external_code"));
                // added by Lohit for bonus type
                focBatchItemsVO.setBonusType(rs.getString("bonus_type"));
                /** START: Birendra: 30JAN2015 */
                focBatchItemsVO.setWalletCode(rs.getString("user_wallet"));
                /** STOP: Birendra: 30JAN2015 */
                map.put(batchDetailId, focBatchItemsVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "SQL Exception: " + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: loadBatchItemsMap map = " + map.size()); 
            }
        }
        return map;
    }

    /**
     * To Check whether batch is modidfied or not
     * 
     * @param con
     * @param oldlastModified
     * @param batchID
     * @return
     * @throws BTSLBaseException
     */
    public boolean isBatchModified(Connection con, long oldlastModified, String batchID) throws BTSLBaseException {
        final String methodName = "isBatchModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:p_oldlastModified=" + oldlastModified + ",p_batchID=" + batchID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM foc_batches WHERE batch_id = ? ";
        java.sql.Timestamp newlastModified = null;
        if (oldlastModified == 0) {
            return false;
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "sqlRecordModified=" + sqlRecordModified);
            }
            pstmtSelect = con.prepareStatement(sqlRecordModified);
            pstmtSelect.setString(1, batchID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the primary key.
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isBatchModified]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isBatchModified]", "", "", "",
                exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
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
                log.debug(methodName, "Exiting:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * isPendingTransactionExist
     * This method is to check that the user has any panding request of transfer
     * or not
     * 
     * @param con
     * @param userID
     * @return
     * @throws BTSLBaseException
     *             boolean
     */
    public boolean isPendingTransactionExist(Connection con, String userID) throws BTSLBaseException {
        final String methodName = "isPendingTransactionExist";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered   p_userID " + userID);
        }
        boolean isExist = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append(" SELECT 1  ");
            strBuff.append(" FROM foc_batch_items ");
            strBuff.append(" WHERE user_id=? AND ");
            strBuff.append(" (status <> ? AND status <> ? )");
            final String sqlSelect = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = con.prepareStatement(sqlSelect);
            int i = 1;
            pstmt.setString(i, userID);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            i++;
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isPendingTransactionExist]", "",
                "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, "", errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isPendingTransactionExist]", "",
                "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:  isExist=" + isExist);
            }
        }
        return isExist;
    }

    /**
     * updateBatchStatus
     * This method is to update the status of FOC_BATCHES table
     * 
     * @param con
     * @param batchID
     * @param newStatus
     * @param oldStatus
     * @return
     * @throws BTSLBaseException
     *             boolean
     */
    public int updateBatchStatus(Connection con, String batchID, String newStatus, String oldStatus) throws BTSLBaseException {
        final String methodName = "updateBatchStatus";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered   p_batchID " + batchID + " p_newStatus=" + newStatus + " p_oldStatus=" + oldStatus);
        }
        PreparedStatement pstmt = null;
        int updateCount = -1;
        try {
            final StringBuilder sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? ");
            sqlBuffer.append(" WHERE batch_id=? AND status=? ");
            final String updateFOCBatches = sqlBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
            }

            pstmt = con.prepareStatement(updateFOCBatches);
            int i = 1;
            pstmt.setString(i, newStatus);
            i++;
            pstmt.setString(i, batchID);
            i++;
            pstmt.setString(i, oldStatus);
            i++;
            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[updateBatchStatus]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, "", errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[updateBatchStatus]", "", "", "",
                exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:  updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method initiateBatchDPTransfer
     * This method used for the bulk commission payout order initiation. The
     * main purpose of this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     * 
     * @author Lohit Audhkhasi
     * @param con
     *            Connection
     * @param batchMasterVO
     *            FOCBatchMasterVO
     * @param batchItemsList
     *            ArrayList
     * @param messages
     *            MessageResources
     * @param locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList initiateBatchDPTransfer(Connection con, FOCBatchMasterVO batchMasterVO, ArrayList batchItemsList, MessageResources messages, Locale locale) throws BTSLBaseException {
        final String methodName = "initiateBatchDPTransfer";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered.... p_batchMasterVO=" + batchMasterVO + ", p_batchItemsList.size() = " + batchItemsList.size() + ", p_batchItemsList=" + batchItemsList + "p_locale=" + locale);
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectExtTxnID2 Query =" + strBuffSelectExtTxnID2);
            // ends here
        }

        // for loading the O2C transfer rule for DP transfer
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuilder strBuffSelectTrfRule = new StringBuilder(" SELECT transfer_rule_id,foc_transfer_type, direct_payout_allowed ");
        strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ? AND ");
        strBuffSelectTrfRule.append("from_category = 'OPT' AND to_category = ? AND status = 'Y' AND type = 'OPT' ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectTrfRule Query =" + strBuffSelectTrfRule);
            // ends here
        }

        // for loading the products associated with the transfer rule
        PreparedStatement pstmtSelectTrfRuleProd = null;
        ResultSet rsSelectTrfRuleProd = null;
        final StringBuilder strBuffSelectTrfRuleProd = new StringBuilder("SELECT 1 FROM chnl_transfer_rules_products ");
        strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectTrfRuleProd Query =" + strBuffSelectTrfRuleProd);
            // ends here
        }

        // for loading the products associated with the commission profile
        PreparedStatement pstmtSelectCProfileProd = null;
        ResultSet rsSelectCProfileProd = null;
        final StringBuilder strBuffSelectCProfileProd = new StringBuilder("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())
        strBuffSelectCProfileProd.append("AND cp.transaction_type in (?,?) ");
        else
        	strBuffSelectCProfileProd.append("AND cp.transaction_type = ? ");
        strBuffSelectCProfileProd.append("AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectCProfileProd Query =" + strBuffSelectCProfileProd);
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        if (PretupsI.YES.equals(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED"))) {
            strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ?  AND cpd.end_range >= ? ");
        } else {
            strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectCProfileProdDetail Query =" + strBuffSelectCProfileProdDetail);
            // ends here
        }

        // for existance of the product in the transfer profile
        PreparedStatement pstmtSelectTProfileProd = null;
        ResultSet rsSelectTProfileProd = null;
        final StringBuilder strBuffSelectTProfileProd = new StringBuilder(" SELECT 1 ");
        strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        strBuffSelectTProfileProd
            .append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectTProfileProd Query =" + strBuffSelectTProfileProd);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2 OraclePreparedStatement pstmtInsertBatchMaster =
        // null;
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        // lohit inserted bonus type for Direct Payout
        // commented for DB2 OraclePreparedStatement pstmtInsertBatchItems =
        // null;
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,bonus_type,dual_comm_type) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchItems Query =" + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE foc_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = con.prepareStatement(strBuffSelectExtTxnID2.toString());
            pstmtSelectTrfRule = con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = con.prepareStatement(strBuffSelectTProfileProd.toString());

            // commented for DB2
            // pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchMaster = con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = con.prepareStatement(strBuffInsertBatchGeo.toString());
            // commented for DB2
            // pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtInsertBatchItems = con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = con.prepareStatement(strBuffUpdateBatchMaster.toString());
            ChannelTransferRuleVO rulesVO = null;
            int index = 0;
            FOCBatchItemsVO batchItemsVO = null;

            final HashMap transferRuleMap = new HashMap();
            final HashMap transferRuleNotExistMap = new HashMap();
            final HashMap transferRuleProdNotExistMap = new HashMap();
            final HashMap transferProfileMap = new HashMap();
            long requestedValue = 0;
            long minTrfValue = 0;
            long maxTrfValue = 0;
            long multipleOf = 0;
            ArrayList transferItemsList = null;
            ChannelTransferItemsVO channelTransferItemsVO = null;

            // insert the master data
            index = 0;
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getNetworkCodeFor());

            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getBatchName());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getStatus());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getDomainCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getProductCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getBatchFileName());
            ++index;
            pstmtInsertBatchMaster.setLong(index, batchMasterVO.getBatchTotalRecord());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getCreatedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getCreatedOn()));
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getModifiedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getModifiedOn()));

            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getDefaultLang());
            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getSecondLang());
            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, "DP");

            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                con.rollback();
                log.error(methodName, "Unable to insert in the batch master table.");
                DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchDPTransfer]", "",
                    "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            }
            // ends here
            // insert batch geographics
            ListValueVO listValueVO = null;
            final int size = batchMasterVO.getGeographyList().size();
            for (int i = 0; i < size; i++) {
                index = 0;
                listValueVO = (ListValueVO) batchMasterVO.getGeographyList().get(i);
                ++index;
                pstmtInsertBatchGeo.setString(index, batchMasterVO.getBatchId());
                ++index;
                pstmtInsertBatchGeo.setString(index, listValueVO.getValue());
                // Added on 07/02/08
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    log.error(methodName, "Unable to insert in the batch geographics table.");
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchDPTransfer]",
                        "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            String msgArr[] = null;
            for (int i = 0, j = batchItemsList.size(); i < j; i++) {
                batchItemsVO = (FOCBatchItemsVO) batchItemsList.get(i);
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue()) {
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID1 = pstmtSelectExtTxnID1.executeQuery();
                    pstmtSelectExtTxnID1.clearParameters();
                    if (rsSelectExtTxnID1.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(batchItemsVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(batchItemsVO.getRecordNumber()));
                        errorVO.setOtherInfo2(messages.getMessage(locale, "batchdirectpayout.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist FOC BATCCH", "");
                        continue;
                    }
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TYPE_O2C);
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID2 = pstmtSelectExtTxnID2.executeQuery();
                    pstmtSelectExtTxnID2.clearParameters();
                    if (rsSelectExtTxnID2.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // load the product's informaiton.
                if (transferRuleNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                    if (transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                        if (transferRuleMap.get(batchItemsVO.getCategoryCode()) == null) {
                            index = 0;
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getNetworkCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getDomainCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchItemsVO.getCategoryCode());
                            rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
                            pstmtSelectTrfRule.clearParameters();
                            if (rsSelectTrfRule.next()) {
                                rulesVO = new ChannelTransferRuleVO();
                                rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
                                rulesVO.setFocTransferType(rsSelectTrfRule.getString("foc_transfer_type"));
                                rulesVO.setDpAllowed(rsSelectTrfRule.getString("direct_payout_allowed"));
                                index = 0;
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, rulesVO.getTransferRuleID());
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, batchMasterVO.getProductCode());
                                rsSelectTrfRuleProd = pstmtSelectTrfRuleProd.executeQuery();
                                pstmtSelectTrfRuleProd.clearParameters();
                                if (!rsSelectTrfRuleProd.next()) {
                                    transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                    // put error log Prodcuct is not in the
                                    // transfer rule
                                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                                    errorList.add(errorVO);
                                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                                    "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                                errorList.add(errorVO);
                                DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchItemsVO.getCategoryCode());

                if (PretupsI.NO.equals(rulesVO.getDpAllowed())) {
                    // put error according to the transfer rule FOC transfer is
                    // not allowed.
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.focnotallowed"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : According to the transfer rule DP transfer is not allowed", "");
                    continue;
                }
                // check the transfer profile product code

                // transfer profile check ends here
                if (transferProfileMap.get(batchItemsVO.getTxnProfile()) == null) {
                    index = 0;
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchItemsVO.getTxnProfile());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchMasterVO.getProductCode());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    rsSelectTProfileProd = pstmtSelectTProfileProd.executeQuery();
                    pstmtSelectTProfileProd.clearParameters();
                    if (!rsSelectTProfileProd.next()) {
                        transferProfileMap.put(batchItemsVO.getTxnProfile(), "false");
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                }

                // check the commisson profile applicability and other checks
                // related to the commission profile
                index = 0;
                ++index;
                pstmtSelectCProfileProd.setString(index, batchMasterVO.getProductCode());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileVer());
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.TRANSFER_TYPE_O2C);
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                else
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                if (!rsSelectCProfileProd.next()) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Commission profile for this product is not defined", "");
                    continue;
                }
                requestedValue = batchItemsVO.getRequestedQuantity();
                minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                // if(minTrfValue > requestedValue || maxTrfValue <
                // requestedValue )
                if (maxTrfValue < requestedValue) {
                    msgArr = new String[3];
                    msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                    msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                    msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                    // put error requested quantity is not between min and max
                    // values
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.qtymaxmin", msgArr));
                    msgArr = null;
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not between min and max values",
                        "minTrfValue=" + minTrfValue + ", maxTrfValue=" + maxTrfValue);
                    continue;
                }
                multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                if (requestedValue % multipleOf != 0) {
                    // put error requested quantity is not multiple of
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.notmulof", new String[] { PretupsBL.getDisplayAmount(multipleOf) }));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not in multiple value",
                        "multiple of=" + multipleOf);
                    continue;
                }

                index = 0;
                ++index;
                pstmtSelectCProfileProdDetail.setString(index, rsSelectCProfileProd.getString("comm_profile_products_id"));
                if (!PretupsI.YES.equals(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED"))) {
                    ++index;
                    pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                }
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail.executeQuery();
                pstmtSelectCProfileProdDetail.clearParameters();
                if (!rsSelectCProfileProdDetail.next()) {
                    // put error commission profile slab is not define for the
                    // requested value
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.commslabnotdefined"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Commission profile slab is not define for the requested value", "");
                    continue;
                }
                // to calculate tax
                transferItemsList = new ArrayList();
                channelTransferItemsVO = new ChannelTransferItemsVO();
                // this value will be inserted into the table as the requested
                // qty
                channelTransferItemsVO.setRequiredQuantity(requestedValue);
                // this value will be used in the tax calculation.
                channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(requestedValue));
                channelTransferItemsVO.setCommProfileDetailID(rsSelectCProfileProdDetail.getString("comm_profile_detail_id"));
                channelTransferItemsVO.setUnitValue(batchMasterVO.getProductMrp());

                channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail.getLong("commission_rate"));
                channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail.getString("commission_type"));

                channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd.getLong("discount_rate"));
                channelTransferItemsVO.setDiscountType(rsSelectCProfileProd.getString("discount_type"));

                channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail.getLong("tax1_rate"));
                channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail.getString("tax1_type"));

                channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail.getLong("tax2_rate"));
                channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail.getString("tax2_type"));

                channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail.getLong("tax3_rate"));
                channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail.getString("tax3_type"));

                if (PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable"))) {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.YES);
                } else {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.NO);
                }

                transferItemsList.add(channelTransferItemsVO);
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(transferItemsList, PretupsI.TRANSFER_TYPE_DP);

                // taxes on DP required
                // ends commission profile validaiton

                // insert items data here
                index = 0;
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchDetailId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCategoryCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getMsisdn());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getStatus());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getModifiedBy());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserGradeCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExtTxnNo());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getExtTxnDate()));
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getTransferDate()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getTxnProfile());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileVer());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommProfileDetailID());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommType());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getCommRate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getCommValue());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax1Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax1Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax1Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax2Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax2Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax2Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax3Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax3Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax3Value());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getRequiredQuantity());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getProductTotalMRP());
                // commented for DB2
                // pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED);
                // added for adding bonus type in direct payout by Lohit
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBonusType());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    // put error record can not be inserted
                    log.error(methodName, "Record cannot be inserted in batch items table");
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    DirectPayOutSuccessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }// for loop for the batch items
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "",
                "", "", sqlException + sqe.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, batchMasterVO, sqlException + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error("initiateBatchFOCTransfer", exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "",
                "", "", exception + ex.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, batchMasterVO, exception + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rsSelectExtTxnID1 != null) {
                    rsSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID1 != null) {
                    pstmtSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectExtTxnID2 != null) {
                    rsSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID2 != null) {
                    pstmtSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRule != null) {
                    rsSelectTrfRule.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRule != null) {
                    pstmtSelectTrfRule.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRuleProd != null) {
                    rsSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRuleProd != null) {
                    pstmtSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProd != null) {
                    rsSelectCProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProd != null) {
                    pstmtSelectCProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProdDetail != null) {
                    rsSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProdDetail != null) {
                    pstmtSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTProfileProd != null) {
                    rsSelectTProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTProfileProd != null) {
                    pstmtSelectTProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchMaster != null) {
                    pstmtInsertBatchMaster.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchGeo != null) {
                    pstmtInsertBatchGeo.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchItems != null) {
                    pstmtInsertBatchItems.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {

                // if all records contains errors then rollback the master table
                // entry
                if (errorList != null && (errorList.size() == batchItemsList.size())) {
                    con.rollback();
                    log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    DirectPayOutErrorLog.dpBatchMasterLog(methodName, batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        log.error(methodName, "Unable to Update the batch size in master table..");
                        con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[initiateBatchDPTransfer]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batchMasterVO.getBatchId());
                    } else {
                        con.commit();
                    }
                }

            } catch (Exception e) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ex) {
                    log.errorTrace(methodName, ex);
                }
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBatchMaster != null) {
                    pstmtUpdateBatchMaster.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
            }
        }
        return errorList;
    }

   
    
    
    
    /**
     * Method initiateBatchDPTransferREST
     * This method used for the bulk commission payout order initiation. The
     * main purpose of this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     * @param con
     *            Connection
     * @param batchMasterVO
     *            FOCBatchMasterVO
     * @param batchItemsList
     *            ArrayList
     * @param locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList initiateBatchDPTransferREST(Connection con, FOCBatchMasterVO batchMasterVO, ArrayList batchItemsList, Locale locale) throws BTSLBaseException {
        final String methodName = "initiateBatchDPTransfer";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered.... p_batchMasterVO=" + batchMasterVO + ", p_batchItemsList.size() = " + batchItemsList.size() + ", p_batchItemsList=" + batchItemsList + "p_locale=" + locale);
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectExtTxnID2 Query =" + strBuffSelectExtTxnID2);
            // ends here
        }

        // for loading the O2C transfer rule for DP transfer
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuilder strBuffSelectTrfRule = new StringBuilder(" SELECT transfer_rule_id,foc_transfer_type, direct_payout_allowed ");
        strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ? AND ");
        strBuffSelectTrfRule.append("from_category = 'OPT' AND to_category = ? AND status = 'Y' AND type = 'OPT' ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectTrfRule Query =" + strBuffSelectTrfRule);
            // ends here
        }

        // for loading the products associated with the transfer rule
        PreparedStatement pstmtSelectTrfRuleProd = null;
        ResultSet rsSelectTrfRuleProd = null;
        final StringBuilder strBuffSelectTrfRuleProd = new StringBuilder("SELECT 1 FROM chnl_transfer_rules_products ");
        strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectTrfRuleProd Query =" + strBuffSelectTrfRuleProd);
            // ends here
        }

        // for loading the products associated with the commission profile
        PreparedStatement pstmtSelectCProfileProd = null;
        ResultSet rsSelectCProfileProd = null;
        final StringBuilder strBuffSelectCProfileProd = new StringBuilder("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        if(SystemPreferences.TRANSACTION_TYPE_ALWD)
        strBuffSelectCProfileProd.append("AND cp.transaction_type in (?,?) ");
        else
        	strBuffSelectCProfileProd.append("AND cp.transaction_type = ? ");
        strBuffSelectCProfileProd.append("AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectCProfileProd Query =" + strBuffSelectCProfileProd);
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        if (PretupsI.YES.equals(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED"))) {
            strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ?  AND cpd.end_range >= ? ");
        } else {
            strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectCProfileProdDetail Query =" + strBuffSelectCProfileProdDetail);
            // ends here
        }

        // for existance of the product in the transfer profile
        PreparedStatement pstmtSelectTProfileProd = null;
        ResultSet rsSelectTProfileProd = null;
        final StringBuilder strBuffSelectTProfileProd = new StringBuilder(" SELECT 1 ");
        strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        strBuffSelectTProfileProd
            .append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectTProfileProd Query =" + strBuffSelectTProfileProd);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2 OraclePreparedStatement pstmtInsertBatchMaster =
        // null;
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        // lohit inserted bonus type for Direct Payout
        // commented for DB2 OraclePreparedStatement pstmtInsertBatchItems =
        // null;
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,bonus_type,dual_comm_type) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchItems Query =" + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE foc_batches SET batch_total_record=? , status =?, TXN_WALLET = ?  WHERE batch_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = con.prepareStatement(strBuffSelectExtTxnID2.toString());
            pstmtSelectTrfRule = con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = con.prepareStatement(strBuffSelectTProfileProd.toString());

            // commented for DB2
            // pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchMaster = con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = con.prepareStatement(strBuffInsertBatchGeo.toString());
            // commented for DB2
            // pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtInsertBatchItems = con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = con.prepareStatement(strBuffUpdateBatchMaster.toString());
            ChannelTransferRuleVO rulesVO = null;
            int index = 0;
            FOCBatchItemsVO batchItemsVO = null;

            final HashMap transferRuleMap = new HashMap();
            final HashMap transferRuleNotExistMap = new HashMap();
            final HashMap transferRuleProdNotExistMap = new HashMap();
            final HashMap transferProfileMap = new HashMap();
            long requestedValue = 0;
            long minTrfValue = 0;
            long maxTrfValue = 0;
            long multipleOf = 0;
            ArrayList transferItemsList = null;
            ChannelTransferItemsVO channelTransferItemsVO = null;

            // insert the master data
            index = 0;
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getNetworkCodeFor());

            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getBatchName());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getStatus());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getDomainCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getProductCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getBatchFileName());
            ++index;
            pstmtInsertBatchMaster.setLong(index, batchMasterVO.getBatchTotalRecord());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getCreatedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getCreatedOn()));
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getModifiedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getModifiedOn()));

            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getDefaultLang());
            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getSecondLang());
            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, "DP");

            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                con.rollback();
                log.error(methodName, "Unable to insert in the batch master table.");
                DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchDPTransfer]", "",
                    "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            }
            // ends here
            // insert batch geographics
            ListValueVO listValueVO = null;
            final int size = batchMasterVO.getGeographyList().size();
            for (int i = 0; i < size; i++) {
                index = 0;
                listValueVO = (ListValueVO) batchMasterVO.getGeographyList().get(i);
                ++index;
                pstmtInsertBatchGeo.setString(index, batchMasterVO.getBatchId());
                ++index;
                pstmtInsertBatchGeo.setString(index, listValueVO.getValue());
                // Added on 07/02/08
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    log.error(methodName, "Unable to insert in the batch geographics table.");
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchDPTransfer]",
                        "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            String msgArr[] = null;
            for (int i = 0, j = batchItemsList.size(); i < j; i++) {
                batchItemsVO = (FOCBatchItemsVO) batchItemsList.get(i);
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && SystemPreferences.EXTERNAL_TXN_UNIQUE) {
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID1 = pstmtSelectExtTxnID1.executeQuery();
                    pstmtSelectExtTxnID1.clearParameters();
                    if (rsSelectExtTxnID1.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batcho2c.initiatebatcho2ctransfer.msg.error.exttxnalreadyexists"));
                        
                        
                      //  errorVO.setOtherInfo2(messages.getMessage(locale, "batchdirectpayout.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist FOC BATCCH", "");
                        continue;
                    }
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TYPE_O2C);
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID2 = pstmtSelectExtTxnID2.executeQuery();
                    pstmtSelectExtTxnID2.clearParameters();
                    if (rsSelectExtTxnID2.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        //errorVO.setOtherInfo2(PretupsRestUtil.getMessageString("batcho2c.initiatebatcho2ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // load the product's informaiton.
                if (transferRuleNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                    if (transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                        if (transferRuleMap.get(batchItemsVO.getCategoryCode()) == null) {
                            index = 0;
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getNetworkCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getDomainCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchItemsVO.getCategoryCode());
                            rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
                            pstmtSelectTrfRule.clearParameters();
                            if (rsSelectTrfRule.next()) {
                                rulesVO = new ChannelTransferRuleVO();
                                rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
                                rulesVO.setFocTransferType(rsSelectTrfRule.getString("foc_transfer_type"));
                                rulesVO.setDpAllowed(rsSelectTrfRule.getString("direct_payout_allowed"));
                                index = 0;
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, rulesVO.getTransferRuleID());
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, batchMasterVO.getProductCode());
                                rsSelectTrfRuleProd = pstmtSelectTrfRuleProd.executeQuery();
                                pstmtSelectTrfRuleProd.clearParameters();
                                if (!rsSelectTrfRuleProd.next()) {
                                    transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                    // put error log Prodcuct is not in the
                                    // transfer rule
                                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                                    errorList.add(errorVO);
                                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                    "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                                errorList.add(errorVO);
                                DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchItemsVO.getCategoryCode());

                if (PretupsI.NO.equals(rulesVO.getDpAllowed())) {
                    // put error according to the transfer rule FOC transfer is
                    // not allowed.
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.focnotallowed"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : According to the transfer rule DP transfer is not allowed", "");
                    continue;
                }
                // check the transfer profile product code

                // transfer profile check ends here
                if (transferProfileMap.get(batchItemsVO.getTxnProfile()) == null) {
                    index = 0;
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchItemsVO.getTxnProfile());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchMasterVO.getProductCode());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    rsSelectTProfileProd = pstmtSelectTProfileProd.executeQuery();
                    pstmtSelectTProfileProd.clearParameters();
                    if (!rsSelectTProfileProd.next()) {
                        transferProfileMap.put(batchItemsVO.getTxnProfile(), "false");
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchdirectpayout.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                }

                // check the commisson profile applicability and other checks
                // related to the commission profile
                index = 0;
                ++index;
                pstmtSelectCProfileProd.setString(index, batchMasterVO.getProductCode());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileVer());
                if(SystemPreferences.TRANSACTION_TYPE_ALWD)
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.TRANSFER_TYPE_O2C);
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                else
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                if (!rsSelectCProfileProd.next()) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Commission profile for this product is not defined", "");
                    continue;
                }
                requestedValue = batchItemsVO.getRequestedQuantity();
                minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                // if(minTrfValue > requestedValue || maxTrfValue <
                // requestedValue )
                if (maxTrfValue < requestedValue) {
                    msgArr = new String[3];
                    msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                    msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                    msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                    // put error requested quantity is not between min and max
                    // values
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.qtymaxmin", msgArr));
                    msgArr = null;
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not between min and max values",
                        "minTrfValue=" + minTrfValue + ", maxTrfValue=" + maxTrfValue);
                    continue;
                }
                multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                if (requestedValue % multipleOf != 0) {
                    // put error requested quantity is not multiple of
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.notmulof", new String[] { PretupsBL.getDisplayAmount(multipleOf) }));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not in multiple value",
                        "multiple of=" + multipleOf);
                    continue;
                }

                index = 0;
                ++index;
                pstmtSelectCProfileProdDetail.setString(index, rsSelectCProfileProd.getString("comm_profile_products_id"));
                if (!PretupsI.YES.equals(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED"))) {
                    ++index;
                    pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                }
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail.executeQuery();
                pstmtSelectCProfileProdDetail.clearParameters();
                if (!rsSelectCProfileProdDetail.next()) {
                    // put error commission profile slab is not define for the
                    // requested value
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.initiatebatchfoctransfer.msg.error.commslabnotdefined"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Commission profile slab is not define for the requested value", "");
                    continue;
                }
                // to calculate tax
                transferItemsList = new ArrayList();
                channelTransferItemsVO = new ChannelTransferItemsVO();
                // this value will be inserted into the table as the requested
                // qty
                channelTransferItemsVO.setRequiredQuantity(requestedValue);
                // this value will be used in the tax calculation.
                channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(requestedValue));
                channelTransferItemsVO.setCommProfileDetailID(rsSelectCProfileProdDetail.getString("comm_profile_detail_id"));
                channelTransferItemsVO.setUnitValue(batchMasterVO.getProductMrp());

                channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail.getLong("commission_rate"));
                channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail.getString("commission_type"));

                channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd.getLong("discount_rate"));
                channelTransferItemsVO.setDiscountType(rsSelectCProfileProd.getString("discount_type"));

                channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail.getLong("tax1_rate"));
                channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail.getString("tax1_type"));

                channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail.getLong("tax2_rate"));
                channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail.getString("tax2_type"));

                channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail.getLong("tax3_rate"));
                channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail.getString("tax3_type"));

                if (PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable"))) {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.YES);
                } else {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.NO);
                }

                transferItemsList.add(channelTransferItemsVO);
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(transferItemsList, PretupsI.TRANSFER_TYPE_DP);

                // taxes on DP required
                // ends commission profile validaiton

                // insert items data here
                index = 0;
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchDetailId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCategoryCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getMsisdn());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getStatus());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getModifiedBy());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserGradeCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExtTxnNo());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getExtTxnDate()));
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getTransferDate()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getTxnProfile());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileVer());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommProfileDetailID());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommType());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getCommRate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getCommValue());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax1Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax1Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax1Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax2Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax2Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax2Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax3Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax3Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax3Value());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getRequiredQuantity());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getProductTotalMRP());
                // commented for DB2
                // pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED);
                // added for adding bonus type in direct payout by Lohit
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBonusType());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    // put error record can not be inserted
                    log.error(methodName, "Record cannot be inserted in batch items table");
                    DirectPayOutErrorLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    DirectPayOutSuccessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }// for loop for the batch items
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "",
                "", "", sqlException + sqe.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, batchMasterVO, sqlException + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error("initiateBatchFOCTransfer", exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "",
                "", "", exception + ex.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, batchMasterVO, exception + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rsSelectExtTxnID1 != null) {
                    rsSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID1 != null) {
                    pstmtSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectExtTxnID2 != null) {
                    rsSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID2 != null) {
                    pstmtSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRule != null) {
                    rsSelectTrfRule.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRule != null) {
                    pstmtSelectTrfRule.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRuleProd != null) {
                    rsSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRuleProd != null) {
                    pstmtSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProd != null) {
                    rsSelectCProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProd != null) {
                    pstmtSelectCProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProdDetail != null) {
                    rsSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProdDetail != null) {
                    pstmtSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTProfileProd != null) {
                    rsSelectTProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTProfileProd != null) {
                    pstmtSelectTProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchMaster != null) {
                    pstmtInsertBatchMaster.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchGeo != null) {
                    pstmtInsertBatchGeo.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchItems != null) {
                    pstmtInsertBatchItems.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {

                // if all records contains errors then rollback the master table
                // entry
                if (errorList != null && (errorList.size() == batchItemsList.size())) {
                    con.rollback();
                    log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    DirectPayOutErrorLog.dpBatchMasterLog(methodName, batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, batchMasterVO.getWallet_type());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        log.error(methodName, "Unable to Update the batch size in master table..");
                        con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[initiateBatchDPTransfer]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batchMasterVO.getBatchId());
                    } else {
                        con.commit();
                    }
                }

            } catch (Exception e) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception ex) {
                    log.errorTrace(methodName, ex);
                }
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBatchMaster != null) {
                    pstmtUpdateBatchMaster.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
            }
        }
        return errorList;
    }

    
    
    
    
    
    /*
     * /**
     * Method loadBatchDPMasterDetails
     * Method for loading Direct Payout Batch details..
     * This method will load the batches that are within the geography of user
     * whose userId is passed
     * with status(OPEN) also in items table for corresponding master record the
     * status is in p_itemStatus
     * 
     * @author Lohit Audhkhasi
     * 
     * @method loadBatchDPMasterDetails
     * 
     * @param p_con java.sql.Connection
     * 
     * @param p_userID String
     * 
     * @param p_itemStatus String
     * 
     * @param p_currentLevel String
     * 
     * @return java.util.ArrayList
     * 
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchDPMasterDetails(Connection con, String userID, String itemStatus, String currentLevel) throws BTSLBaseException {
        final String methodName = "loadBatchDPMasterDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_userID=" + userID + " p_itemStatus=" + itemStatus + " p_currentLevel=" + currentLevel);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
      
        final String sqlSelect = focBatchTransferQry.loadBatchDPMasterDetailsQry(itemStatus, currentLevel);
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, userID);
            pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);
            pstmt.setString(8, PretupsI.CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            FOCBatchMasterVO focBatchMasterVO = null;
            while (rs.next()) {
                focBatchMasterVO = new FOCBatchMasterVO();
                focBatchMasterVO.setBatchId(rs.getString("batch_id"));
                focBatchMasterVO.setBatchName(rs.getString("batch_name"));
                focBatchMasterVO.setProductName(rs.getString("product_name"));
                focBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
                focBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
                focBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                focBatchMasterVO.setNewRecords(rs.getInt("new"));
                focBatchMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
                focBatchMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
                focBatchMasterVO.setClosedRecords(rs.getInt("closed"));
                focBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
                focBatchMasterVO.setNetworkCode(rs.getString("network_code"));
                focBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
                focBatchMasterVO.setProductCode(rs.getString("product_code"));
                focBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
                focBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
                focBatchMasterVO.setProductType(rs.getString("product_type"));
                focBatchMasterVO.setProductShortName(rs.getString("short_name"));
                focBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                focBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                focBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
                focBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
                focBatchMasterVO.setCreatedBy(rs.getString("created_by"));
                focBatchMasterVO.setWallet_type(rs.getString("txn_wallet"));
                list.add(focBatchMasterVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchDPMasterDetails]", "",
                "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchDPMasterDetails]", "",
                "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: focBatchMasterVOList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method closeOrderByBatchForDirectPayout
     * Method to close the direct payout order by batch. This also perform all
     * the data validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,foc_batch_items,
     * foc_batches,
     * channel_transfers_items,channel_transfers
     * 
     * @author Lohit Audhkhasi
     * @param con
     * @param dataMap
     * @param currentLevel
     * @param p_userID
     * @param p_focBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @param p_sms_default_lang
     * @param p_sms_second_lang
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatchForDirectPayout(Connection con, LinkedHashMap dataMap, String currentLevel, String p_userID, FOCBatchMasterVO p_focBatchMatserVO, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "closeOrderByBatchForDirectPayout";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_dataMap=");
        	loggerValue.append(dataMap);
        	loggerValue.append(" p_currentLevel=");
        	loggerValue.append(currentLevel);
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
        	
            log.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement pstmtLoadNetworkStock = null;
        PreparedStatement pstmtUpdateNetworkStock = null;
        PreparedStatement pstmtInsertNetworkDailyStock = null;
        PreparedStatement pstmtSelectNetworkStock = null;
        PreparedStatement pstmtupdateSelectedNetworkStock = null;
        // commented for DB2 OraclePreparedStatement
        // pstmtInsertNetworkStockTransaction=null;
        PreparedStatement pstmtInsertNetworkStockTransaction = null;
        PreparedStatement pstmtInsertNetworkStockTransactionItem = null;
        PreparedStatement pstmtSelectUserBalances = null;
        PreparedStatement pstmtUpdateUserBalances = null;
        PreparedStatement pstmtInsertUserDailyBalances = null;
        PreparedStatement pstmtSelectBalance = null;
        PreparedStatement pstmtUpdateBalance = null;
        PreparedStatement pstmtInsertBalance = null;
        PreparedStatement pstmtSelectTransferCounts = null;
        PreparedStatement pstmtSelectProfileCounts = null;
        PreparedStatement pstmtUpdateTransferCounts = null;
        PreparedStatement pstmtInsertTransferCounts = null;
        // commented for DB2OraclePreparedStatement psmtAppr1FOCBatchItem =
        // null;
        // commented for DB2OraclePreparedStatement psmtAppr2FOCBatchItem =
        // null;
        // commented for DB2OraclePreparedStatement psmtAppr3FOCBatchItem =
        // null;

        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement psmtAppr3FOCBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        // PreparedStatement pstmtUpdateMaster= null;
        // commented for DB2OraclePreparedStatement pstmtUpdateMaster= null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        // commented for DB2OraclePreparedStatement
        // pstmtInsertIntoChannelTranfers=null;
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
        PreparedStatement pstmtSelectOwner = null;
        ArrayList userbalanceList = null;
        UserBalancesVO balancesVO = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        ResultSet rs5 = null;
        ResultSet rs6 = null;
        ResultSet rs7 = null;
        ResultSet rs8 = null;
        ResultSet rs9 = null;
        ResultSet rs10 = null;
        ResultSet rs11 = null;
        ResultSet rs12 = null;
        ResultSet rs13 = null;
        
        String language = null;
        String country = null;
        KeyArgumentVO keyArgumentVO = null;
        String[] argsArr = null;
        ArrayList txnSmsMessageList = null;
        ArrayList balSmsMessageList = null;
        Locale locale = null;
        String[] array = null;
        BTSLMessages messages = null;
        PushMessage pushMessage = null;
        int updateCount = 0, insertCount = 0;
        String o2cTransferID = null;
        boolean isDpAllowed = false;
        isDpAllowed = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_ALLOWED))).booleanValue();

        PreparedStatement psmtInsertUserThreshold = null;
        long thresholdValue = -1;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        
        StringBuilder sqlBuffer = null;
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        HashMap<String, HashMap<String, String>> tcpMap = null;
        
        if(tcpOn) {
        	sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, ");
            sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
            sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
            sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps, user_phones up,user_geographies ug ");
            sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
            sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
            sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
            sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
            sqlBuffer.append("   ug.user_id = u.user_id ");
            
        	SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")), ValueType.STRING);
			tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id", "status")),
					searchCriteria);
			
 	
        }else {

        sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
        }
        
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
       
        final String sqlLoadNetworkStock = focBatchTransferQry.closeOrderByBatchForDirectPayoutLoadNetworkStockQry();
       
        sqlBuffer = null;
        // Update daily_stock_updated_on with current date
        sqlBuffer = new StringBuilder("UPDATE network_stocks SET daily_stock_updated_on = ? ");
        sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? ");
        final String sqlUpdateNetworkStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdateNetworkStock=" + sqlUpdateNetworkStock);
        }
        sqlBuffer = null;
        sqlBuffer = new StringBuilder("INSERT INTO network_daily_stocks(wallet_date, wallet_type, network_code, network_code_for, ");
        sqlBuffer.append("product_code, wallet_created, wallet_returned, wallet_balance, wallet_sold, last_txn_no, ");
        sqlBuffer.append("last_txn_type, last_txn_balance, previous_balance, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String sqlInsertNetworkDailyStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlInsertNetworkDailyStock=" + sqlInsertNetworkDailyStock);
        }
       
        final String sqlSelectNetworkStock = focBatchTransferQry.closeOrderByBatchForDirectPayoutSelectWalletQry();
  
        sqlBuffer = null;
        // Debit the network stock
        sqlBuffer = new StringBuilder(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance = ?, ");
        sqlBuffer.append(" wallet_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
        sqlBuffer.append(" modified_by =?, modified_on =? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" network_code = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code_for = ?  AND wallet_type = ? ");
        final String updateSelectedNetworkStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateSelectedNetworkStock=" + updateSelectedNetworkStock);
        }
        sqlBuffer = null;

        // Insert record into network_stock_transactions table.
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_transactions ( ");
        sqlBuffer.append(" txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
        sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
        sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
        sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp ");
        sqlBuffer.append(",txn_wallet,ref_txn_id ");
        sqlBuffer.append(" )VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        
        final String insertNetworkStockTransaction = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertNetworkStockTransaction=" + insertNetworkStockTransaction);
        }
        sqlBuffer = null;

        // Insert record into network_stock_trans_items
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_trans_items ");
        sqlBuffer.append(" (s_no, txn_no, product_code, required_quantity, approved_quantity, stock, mrp, amount, date_time) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?) ");
        final String insertNetworkStockTransactionItem = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertNetworkStockTransactionItem=" + insertNetworkStockTransactionItem);
        }
        sqlBuffer = null;

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be insert in user_daily_balances table
        final String selectUserBalances = focBatchTransferQry.closeOrderByBatchForDirectPayoutUserBalancesQry();
     
        sqlBuffer = null;
        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateUserBalances=" );
        	loggerValue.append(updateUserBalances);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in user_daily_balances
        sqlBuffer = new StringBuilder(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type,balance_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserDailyBalances = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertUserDailyBalances=" );
        	loggerValue.append(insertUserDailyBalances);
            log.debug(methodName,loggerValue );
        }
       
        final String selectBalance = focBatchTransferQry.closeOrderByBatchForDirectPayoutSelectBalanceQry();
   
        sqlBuffer = null;
        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append("  balance_type = ? AND user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        final String updateBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateBalance=" + updateBalance);
        }
        sqlBuffer = null;

        // Insert the record of balnce for user (If balance not found in
        // user_balances)
        sqlBuffer = new StringBuilder(" INSERT ");
        sqlBuffer.append(" INTO user_balances ");
        sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , balance_type,  ");
        sqlBuffer.append(" user_id, product_code , network_code, network_code_for  ) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertBalance=");
        	loggerValue.append(insertBalance);
            log.debug(methodName, loggerValue);
        }
        sqlBuffer = null;

        // Select the running countres of user(to be checked against the
        // effetive profile counters)
        sqlBuffer = new StringBuilder(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
        sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
        sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
        sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
        sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
        sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
        sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
        sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
        sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
        sqlBuffer.append(" ,last_sos_txn_status,last_lr_status  ");
        sqlBuffer.append(" FROM user_transfer_counts ");
        // DB220120123for update WITH

        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS ");
        } else {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        }
        final String selectTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectTransferCounts=");
        	loggerValue.append(selectTransferCounts);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Select the effective profile counters of user to be checked with
        // running counters of user
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
        final String selectProfileCounts = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectProfileCounts=" );
        	loggerValue.append(selectProfileCounts);
            log.debug(methodName, loggerValue);
        }

        // Update the user running countres (If record found for user running
        // counters)
        sqlBuffer = new StringBuilder(" UPDATE user_transfer_counts  SET ");
        sqlBuffer.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
        sqlBuffer.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
        sqlBuffer.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
        sqlBuffer.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
        sqlBuffer.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
        sqlBuffer.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
        sqlBuffer.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
        sqlBuffer.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
        sqlBuffer.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
        sqlBuffer.append(" last_in_time = ? , last_transfer_id=?,last_transfer_date=? ");
        sqlBuffer.append(" WHERE user_id = ?  ");
        final String updateTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateTransferCounts=" );
        	loggerValue.append(updateTransferCounts);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Insert the record in user_transfer_counts (If no record found for
        // user running counters)
        sqlBuffer = new StringBuilder(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        final String insertTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertTransferCounts=" );
        	loggerValue.append(insertTransferCounts);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv1FOCBatchItems=" );
        	loggerValue.append(sqlApprv1FOCBatchItems);
            log.debug(methodName, loggerValue);
        }
        sqlBuffer = null;

        // If current level of approval is 2 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv2FOCBatchItems=" );
        	loggerValue.append(sqlApprv2FOCBatchItems);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // If current level of approval is 3 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" third_approver_remarks = ?, ");
        sqlBuffer.append(" third_approved_by=? , third_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status = ?  ");
        final String sqlApprv3FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv3FOCBatchItems=" );
        	loggerValue.append(sqlApprv3FOCBatchItems);
            log.debug(methodName, loggerValue);
        }
        
        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
        final String selectItemsDetails = focBatchTransferQry.focBatcheSelectItemDetailsQry();
      
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isModified=");
        	loggerValue.append(isModified);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // Select the transfer profile product values(These will be used for
        // checking max balance of user)
        sqlBuffer = new StringBuilder("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
        sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        sqlBuffer
            .append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
        final String loadTransferProfileProduct = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY loadTransferProfileProduct=");
        	loggerValue.append(loadTransferProfileProduct);
            log.debug(methodName,  loggerValue);
        } 
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
         	loggerValue.setLength(0);
        	loggerValue.append("QUERY isExistsTxnNum1=");
        	loggerValue.append(isExistsTxnNum1);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuilder("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isExistsTxnNum2=");
        	loggerValue.append(isExistsTxnNum2);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // The query bel;ow is used to insert the record in channel transfer
        // items table for the order that is closed
        // query modified for adding sender_debit_quantity,
        // receiver_credit_quantity,commision_quantity, sender_post_stock,
        // receiver_post_stock, these fields was 0 earlier
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer
            .append(" tax3_value, transfer_date, transfer_id, user_unit_price,sender_debit_quantity, receiver_credit_quantity,commision_quantity, sender_post_stock, receiver_post_stock )  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertIntoChannelTransferItem=");
        	loggerValue.append(insertIntoChannelTransferItem);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // The query below is used to get the balance information of user with
        // product.
        // This information will be send in message to user
        sqlBuffer = new StringBuilder(" SELECT UB.product_code,UB.balance, ");
        sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
        sqlBuffer.append(" FROM user_balances UB,products PROD ");
        sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code ");
        final String selectBalanceInfoForMessage = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectBalanceInfoForMessage=");
        	loggerValue.append(selectBalanceInfoForMessage);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The query is used to get the owner user of userd ID
        sqlBuffer = new StringBuilder();
        sqlBuffer.append(" select owner_id from users where user_id=?");
        final String selectowner = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectowner=");
        	loggerValue.append(selectowner);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers ");
        sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
        sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
        sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, from_user_id, grph_domain_code, ");
        sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
        sqlBuffer.append("  product_type, receiver_category_code, receiver_grade_code, ");
        sqlBuffer.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
        sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
        sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
        sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
        sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
        sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,bonus_type ");
        sqlBuffer.append(",owner_transfer_mrp,owner_debit_mrp,active_user_id");

        sqlBuffer.append(",TXN_WALLET,dual_comm_type)");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
        sqlBuffer.append(",?,?,?,?");

        sqlBuffer.append(")");
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertIntoChannelTransfer=");
        	loggerValue.append(insertIntoChannelTransfer);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // added by nilesh:added two new colums threshold_type and remark
        final StringBuilder strBuffThresholdInsert = new StringBuilder();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertUserThreshold=");
        	loggerValue.append(insertUserThreshold);
            log.debug(methodName,  loggerValue );
        }
        Date date = null;
        String batch_ID = null;

        // start of try
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            ChannelTransferVO channelTransferVO = null;
            ChannelTransferItemsVO channelTransferItemVO = null;
            date = new Date();
            ArrayList channelTransferItemVOList = null;
            pstmtLoadUser = con.prepareStatement(sqlLoadUser);
            pstmtLoadNetworkStock = con.prepareStatement(sqlLoadNetworkStock);
            pstmtUpdateNetworkStock = con.prepareStatement(sqlUpdateNetworkStock);
            pstmtInsertNetworkDailyStock = con.prepareStatement(sqlInsertNetworkDailyStock);
            pstmtSelectNetworkStock = con.prepareStatement(sqlSelectNetworkStock);
            pstmtupdateSelectedNetworkStock = con.prepareStatement(updateSelectedNetworkStock);
            pstmtInsertNetworkStockTransaction = con.prepareStatement(insertNetworkStockTransaction);
            pstmtInsertNetworkStockTransactionItem = con.prepareStatement(insertNetworkStockTransactionItem);
            pstmtSelectUserBalances = con.prepareStatement(selectUserBalances);
            pstmtUpdateUserBalances = con.prepareStatement(updateUserBalances);
            pstmtInsertUserDailyBalances = con.prepareStatement(insertUserDailyBalances);
            pstmtSelectBalance = con.prepareStatement(selectBalance);
            pstmtUpdateBalance = con.prepareStatement(updateBalance);
            pstmtInsertBalance = con.prepareStatement(insertBalance);
            pstmtSelectTransferCounts = con.prepareStatement(selectTransferCounts);
            pstmtSelectProfileCounts = con.prepareStatement(selectProfileCounts);
            pstmtUpdateTransferCounts = con.prepareStatement(updateTransferCounts);
            pstmtInsertTransferCounts = con.prepareStatement(insertTransferCounts);

            psmtAppr1FOCBatchItem = con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtAppr3FOCBatchItem = con.prepareStatement(sqlApprv3FOCBatchItems);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster = con.prepareStatement(updateFOCBatches);
            pstmtIsModified = con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = con.prepareStatement(loadTransferProfileProduct);
            pstmtIsTxnNumExists1 = con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = con.prepareStatement(isExistsTxnNum2);
            pstmtInsertIntoChannelTransferItems = con.prepareStatement(insertIntoChannelTransferItem);
            pstmtInsertIntoChannelTranfers = con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = con.prepareStatement(selectBalanceInfoForMessage);
            pstmtSelectOwner = con.prepareStatement(selectowner);
            psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);
            errorList = new ArrayList();
            final Iterator iterator = dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
            NetworkStockVO networkStocksVO = null;
            int dayDifference = 0;
            Date dailyStockUpdatedOn = null;
            long stock = -1;
            long stockSold = -1;
            NetworkStockTxnVO networkStockTxnVO = null;
            String network_id = null;
            Date dailyBalanceUpdatedOn = null;
            NetworkStockTxnItemsVO networkItemsVO = null;
            TransferProfileProductVO transferProfileProductVO = null;
            UserTransferCountsVO countsVO = null;
            TransferProfileVO transferProfileVO = null;
            C2STransferVO userVO = null;
            long maxBalance = 0;
            boolean isNotToExecuteQuery = false;
            long balance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;
            boolean isOwnerUserNotSame;
            Boolean balanceExist = false;
            while (iterator.hasNext()) {
                balanceExist = false;
                insertCount = 0;
                isOwnerUserNotSame = false;
                terminateProcessing = false;
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) dataMap.get(key);
                pstmtSelectOwner.clearParameters();
                pstmtSelectOwner.setString(1, focBatchItemVO.getUserId());
                rs = pstmtSelectOwner.executeQuery();
                if (rs.next()) {
                    userVO = new C2STransferVO();
                    userVO.setOwnerUserID(rs.getString("owner_id"));
                } else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Owner user not found", "Approval level = " + currentLevel);
                    continue;
                }

                if (!(focBatchItemVO.getUserId().equalsIgnoreCase(userVO.getOwnerUserID())) && isDpAllowed) {
                    isOwnerUserNotSame = true;
                }
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Executed focBatchItemVO=" );
                	loggerValue.append(focBatchItemVO.toString());
                    log.debug(methodName, loggerValue);
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
                rs1 = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs1.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(focBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs1.getString("userstatus"));
                    channelUserVO.setInSuspend(rs1.getString("in_suspend"));
                    // added by nilesh:transfer_profile_id
                    channelUserVO.setTransferProfileID(rs1.getString("transfer_profile_id"));
                    
                    if(tcpOn) {
                    	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                    }
                    
                    channelUserVO.setCommissionProfileStatus(rs1.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs1.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs1.getString("comprf_lang_2_msg"));
                    
                    if(!tcpOn) {
                    	channelUserVO.setTransferProfileStatus(rs1.getString("profile_status"));
                    }else {
                    	channelUserVO.setTransferProfileStatus(tcpMap.get(rs.getString("transfer_profile_id")).get("profileStatus"));//TCP
                    }
                    
                    language = rs1.getString("phone_language");
                    country = rs1.getString("country");
                    channelUserVO.setGeographicalCode(rs1.getString("grph_domain_code"));
                    // (user status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User is suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Commission profile suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + currentLevel);
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User not found", "Approval level" + currentLevel);
                    continue;
                }
                networkStocksVO = new NetworkStockVO();
                networkStocksVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkStocksVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                networkStocksVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());

                // creating the channelTransferVO here since O2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth foc items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                channelTransferVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());
                channelTransferVO.setProductType(p_focBatchMatserVO.getProductCode());
                channelTransferVO.setToUserID(focBatchItemVO.getUserId());
                channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                ChannelTransferBL.genrateTransferID(channelTransferVO);

                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch foc item was created.
                channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());
                // lohit
                channelTransferVO.setTransferType(PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
                networkStocksVO.setLastTxnNum(o2cTransferID);
              
                networkStocksVO.setLastTxnBalance(focBatchItemVO.getRequestedQuantity());
                networkStocksVO.setWalletBalance(focBatchItemVO.getRequestedQuantity());
                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                networkStocksVO.setModifiedBy(p_userID);
                networkStocksVO.setModifiedOn(date);
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                	 networkStocksVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);

                } else {
                	 networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                }
               
                dailyStockUpdatedOn = null;
                dayDifference = 0;
                // select the record form the network stock table.
                pstmtLoadNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                    ++m;
                    pstmtLoadNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);

                } else {
                    ++m;
                    pstmtLoadNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                ++m;
                pstmtLoadNetworkStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                rs2 = pstmtLoadNetworkStock.executeQuery();
                while (rs2.next()) {
                    dailyStockUpdatedOn = rs2.getDate("daily_stock_updated_on");

                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, date);

                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the difference
                        // is.
                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Till now daily Stock is not updated on " );
                        	loggerValue.append(date);
                        	loggerValue.append(", day differences = ");
                        	loggerValue.append(dayDifference);
                            log.debug("closeOrderByBatchForDirectPayout ", loggerValue);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                            } else {
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                            }
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs2.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs2.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs2.getString("product_code"));
                           
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_created"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_returned"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_sold"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("last_txn_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("previous_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);

                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
							
							
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while insert in network daily stock table",
                                    "Approval level = " + currentLevel + "updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Termination of the procissing",
                                "Approval level = " + currentLevel);
                            break;
                        }
                        // Update the network stock table
                        pstmtUpdateNetworkStock.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                            ++m;
                            pstmtUpdateNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                        } else {
                            ++m;
                            pstmtUpdateNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                        }
                        updateCount = pstmtUpdateNetworkStock.executeUpdate();
                        // (record not updated properly in db) if this condition
                        // is true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network daily stock table",
                                "Approval level = " + currentLevel + "updateCount = " + updateCount);
                            continue;
                        }
                    }
                }// end of if () for daily network stock updation
                pstmtSelectNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                    ++m;
                    pstmtSelectNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtSelectNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                rs3 = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockSold = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs3.next()) {
                    stock = rs3.getLong("wallet_balance");
                    stockSold = rs3.getLong("wallet_sold");
                    previousNwStockToBeSetChnlTrfItems = stock;
                }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(p_messages.getMessage(p_locale, "label.all"), String.valueOf(focBatchItemVO.getRecordNumber()) + " - " + p_messages.getMessage(
                        p_locale, "label.all"), p_messages.getMessage(p_locale, "batchdirectpayout.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", "Approval level = " + currentLevel);
                    throw new BTSLBaseException(this, methodName, "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", "Approval level = " + currentLevel);
                    continue;
                }
                if (stock != -1) {
                    stock -= networkStocksVO.getWalletbalance();
                }
                if (stockSold != -1) {
                    stockSold += networkStocksVO.getWalletbalance();
                }
                m = 0;
                // Deebit the network stock
                pstmtupdateSelectedNetworkStock.clearParameters();
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stock);
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stockSold);
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnNum());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnType());
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, networkStocksVO.getLastTxnBalance());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getModifiedBy());
                ++m;
                pstmtupdateSelectedNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStocksVO.getModifiedOn()));
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                    ++m;
                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
             // AutoNetworkStockCreation logic
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                	new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);
                }
                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setNetworkCode(networkStocksVO.getNetworkCode());
                networkStockTxnVO.setNetworkFor(networkStocksVO.getNetworkCodeFor());
                if (networkStocksVO.getNetworkCode().equals(p_focBatchMatserVO.getNetworkCodeFor())) {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                } else {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                }
                // As discussed with sandeep in channel transfer table's
                // reference number field we have
                // to insert batch details id.So In network stock where channel
                // transfer table's reference number
                // was inserted, I insert batch detail id.
                networkStockTxnVO.setReferenceNo(focBatchItemVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(focBatchItemVO.getInitiatedOn());
                networkStockTxnVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setInitiaterRemarks(focBatchItemVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(focBatchItemVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(focBatchItemVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(focBatchItemVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(focBatchItemVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(p_userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(p_userID);
                networkStockTxnVO.setTxnStatus(focBatchItemVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
                networkStockTxnVO.setTxnType(PretupsI.DEBIT);
                networkStockTxnVO.setInitiatedBy(p_userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(focBatchItemVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(focBatchItemVO.getTransferMrp());
                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                networkItemsVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setMrp(focBatchItemVO.getTransferMrp());
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setAmount(0);
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
                // Added on 07/02/08
                networkItemsVO.setDateTime(p_focBatchMatserVO.getBatchDate());
                m = 0;
                pstmtInsertNetworkStockTransaction.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkCode());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkFor());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getStockType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getReferenceNo());
                if (networkStockTxnVO.getTxnDate() != null) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getTxnDate()));
                } else {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, null);
                }
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getRequestedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiaterRemarks());
                // for multilanguage support
              
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedRemarks());
                // for multilanguage support
                
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedRemarks());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCancelledBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCancelledOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCreatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCreatedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getModifiedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getModifiedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnStatus());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getEntryType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getFirstApproverLimit());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getUserID());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getTxnMrp());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                } else {
                	++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.SALE_WALLET_TYPE);
        		}
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, channelTransferVO.getTransferID());

                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount);  // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertNetworkStockTransactionItem.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransactionItem.setInt(m, networkItemsVO.getSNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getProductCode());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getRequiredQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getStock());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getMrp());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getAmount());
                // Date 07/02/08
                ++m;
                pstmtInsertNetworkStockTransactionItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                updateCount = pstmtInsertNetworkStockTransactionItem.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                    continue;
                }
                // insert for parent
                if (isOwnerUserNotSame) {
                    terminateProcessing = false;
                    long newBalance = 0;
                    updateCount = 0;

                    pstmtSelectBalance.clearParameters();
                    pstmtSelectBalance.setString(1, userVO.getOwnerUserID());
                    pstmtSelectBalance.setString(2, channelTransferVO.getProductType());
                    pstmtSelectBalance.setString(3, channelTransferVO.getNetworkCode());
                    pstmtSelectBalance.setString(4, channelTransferVO.getNetworkCodeFor());
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                        pstmtSelectBalance.setString(5, PretupsI.WALLET_TYPE_BONUS);
                    } else {
                        pstmtSelectBalance.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                    }
                    rs4 = pstmtSelectBalance.executeQuery();

                    // if parent's entry already exists
                    if (rs4.next()) {
                        balance = rs4.getLong("balance");
                    } else
                    // if parent's entry does not exist
                    {
                        pstmtInsertBalance.clearParameters();
                        balance = 0;
                        m = 0;
                        balance = focBatchItemVO.getRequestedQuantity();
                        ++m;
                        pstmtInsertBalance.setLong(m, 0);
                        ++m;
                        pstmtInsertBalance.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtInsertBalance.setLong(m, balance);
                        ++m;
                        pstmtInsertBalance.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                        ++m;
                        pstmtInsertBalance.setString(m, o2cTransferID);
                        ++m;
                        pstmtInsertBalance.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                            ++m;
                            pstmtInsertBalance.setString(m, PretupsI.WALLET_TYPE_BONUS);
                        } else {
                            ++m;
                            pstmtInsertBalance.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                        }
                        ++m;
                        pstmtInsertBalance.setString(m, userVO.getOwnerUserID());
                        ++m;
                        pstmtInsertBalance.setString(m, p_focBatchMatserVO.getProductCode());
                        ++m;
                        pstmtInsertBalance.setString(m, p_focBatchMatserVO.getNetworkCode());
                        ++m;
                        pstmtInsertBalance.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                        insertCount = pstmtInsertBalance.executeUpdate();

                        if (insertCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting user balances table",
                                "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                            terminateProcessing = true;
                            break;
                        }
                        if (terminateProcessing) {
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + currentLevel);
                            continue;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "After inserting new user balances information");
                        }
                        pstmtUpdateBalance.clearParameters();
                    }
                    // update if parent's entry already exists
                    if (insertCount == 0) {
                        newBalance = balance + focBatchItemVO.getRequestedQuantity(); // credit
                        // parent's
                        // account
                        pstmtUpdateBalance.clearParameters();
                        pstmtUpdateBalance.setLong(1, newBalance);
                        pstmtUpdateBalance.setString(2, channelTransferVO.getTransferType());
                        pstmtUpdateBalance.setString(3, channelTransferVO.getTransferID());
                        pstmtUpdateBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                            pstmtUpdateBalance.setString(5, PretupsI.WALLET_TYPE_BONUS);
                        } else {
                            pstmtUpdateBalance.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                        }
                        pstmtUpdateBalance.setString(6, userVO.getOwnerUserID());
                        pstmtUpdateBalance.setString(7, channelTransferVO.getProductType());
                        pstmtUpdateBalance.setString(8, channelTransferVO.getNetworkCode());
                        pstmtUpdateBalance.setString(9, channelTransferVO.getNetworkCodeFor());

                        updateCount = pstmtUpdateBalance.executeUpdate();
                        if (updateCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating user balances table",
                                "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                            terminateProcessing = true;
                            break;
                        }
                    }
                    if (insertCount <= 0) {
                        balance = newBalance;
                        newBalance = balance - focBatchItemVO.getRequestedQuantity();
                    } else {
                        balance = newBalance;
                    }

                    if (balance < focBatchItemVO.getRequestedQuantity()) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "",
                            "Owner current balance is less than required balance for requested Adjustment Dr Amt.");
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "Owner Current Bal:" + balance + "And required Dr Amt : " + channelTransferVO.getPayableAmount());
                        }
                    }
                    if (newBalance < 0) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.userbalance"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog("closeOrderByBatchForDirectPayout", p_focBatchMatserVO, focBatchItemVO,
                            "FAIL : DB Error while updating user balances table", "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                        terminateProcessing = true;
                        break;

                    }
                    pstmtUpdateBalance.clearParameters();
                    pstmtUpdateBalance.setLong(1, newBalance); // debit parent's
                    // account
                    pstmtUpdateBalance.setString(2, channelTransferVO.getTransferType());
                    pstmtUpdateBalance.setString(3, channelTransferVO.getTransferID());
                    pstmtUpdateBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                        pstmtUpdateBalance.setString(5, PretupsI.WALLET_TYPE_BONUS);
                    } else {
                        pstmtUpdateBalance.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                    }
                    pstmtUpdateBalance.setString(6, userVO.getOwnerUserID());
                    pstmtUpdateBalance.setString(7, channelTransferVO.getProductType());
                    pstmtUpdateBalance.setString(8, channelTransferVO.getNetworkCode());
                    pstmtUpdateBalance.setString(9, channelTransferVO.getNetworkCodeFor());

                    updateCount = pstmtUpdateBalance.executeUpdate();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating user balances table",
                            "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                        terminateProcessing = true;
                        break;
                    }

                    if (terminateProcessing) {
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                            "FAIL : Terminting the procssing of this user as error while updating user balance", "Approval level = " + currentLevel);
                        continue;
                    }
                }

                dailyBalanceUpdatedOn = null;
                dayDifference = 0;
                // select the record form the userBalances table.
                pstmtSelectUserBalances.clearParameters();
                m = 0;
                ++m;
                pstmtSelectUserBalances.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectUserBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    ++m;
                    pstmtSelectUserBalances.setString(m, PretupsI.WALLET_TYPE_BONUS);
                } else {
                    ++m;
                    pstmtSelectUserBalances.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                }
                rs5 = pstmtSelectUserBalances.executeQuery();
                while (rs5.next()) {
                    dailyBalanceUpdatedOn = rs5.getDate("daily_balance_updated_on");
                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, date);
                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the
                        if (log.isDebugEnabled()) {
                            log.debug("closeOrderByBatchForDirectPayout ", "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertUserDailyBalances.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertUserDailyBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("user_id"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("network_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("network_code_for"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("product_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs5.getLong("balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs5.getLong("prev_balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                                ++m;
                                pstmtInsertUserDailyBalances.setString(m, PretupsI.WALLET_TYPE_BONUS);
                            } else {
                                ++m;
                                pstmtInsertUserDailyBalances.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                            }
                            updateCount = pstmtInsertUserDailyBalances.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting user daily balances table",
                                    "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + currentLevel);
                            continue;
                        }
                        // Update the user balances table
                        pstmtUpdateUserBalances.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateUserBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateUserBalances.setString(m, channelUserVO.getUserID());
                        updateCount = pstmtUpdateUserBalances.executeUpdate();
                        // (record not updated properly) if this condition is
                        // true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : DB Error while updating user balances table for daily balance", "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
                maxBalance = 0;
                isNotToExecuteQuery = false;
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    ++m;
                    pstmtSelectBalance.setString(m, PretupsI.WALLET_TYPE_BONUS);
                } else {
                    ++m;
                    pstmtSelectBalance.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                }
                rs6 = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                if (rs6.next()) {
                    balance = rs6.getLong("balance");
                    balanceExist = true;
                }
                if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    balance += focBatchItemVO.getRequestedQuantity();
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                rs7 = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs7.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(p_focBatchMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs7.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs7.getLong("max_balance"));
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + currentLevel);
                    continue;
                }
                maxBalance = transferProfileProductVO.getMaxBalanceAsLong();
                // (max balance reach for the receiver) if this condition is
                // true then made entry in logs and leave this data.
                if (maxBalance < balance) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the order
                // value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (balance == -1 && maxBalance < focBatchItemVO.getRequestedQuantity()) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + currentLevel);
                    continue;
                }
                if (!isNotToExecuteQuery) {
                    m = 0;
                    // update
                    if (balanceExist && balance > -1) {
                        pstmtUpdateBalance.clearParameters();
                        handlerStmt = pstmtUpdateBalance; // update if balance
                        // entry already
                        // exists for child
                        // user
                    } else if (balanceExist && balance <= -1) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.balance.notallowed"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Current balance is less than requested quantity.",
                            "Approval level = " + currentLevel);
                        continue;

                    } else if (!balanceExist && balance + focBatchItemVO.getRequestedQuantity() <= -1) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.balance.notallowed"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Current balance is less than requested quantity.",
                            "Approval level = " + currentLevel);
                        continue;

                    } else if (!balanceExist && balance + focBatchItemVO.getRequestedQuantity() > -1) {
                        // insert for child user balance
                        pstmtInsertBalance.clearParameters();
                        handlerStmt = pstmtInsertBalance; // insert if balance
                        // entry does not
                        // exist for child
                        // user
                        balance = focBatchItemVO.getRequestedQuantity();
                        ++m;
                        handlerStmt.setLong(m, 0);// previous balance
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                        // on
                        // date
                    }
                    ++m;
                    handlerStmt.setLong(m, balance);
                    ++m;
                    handlerStmt.setString(m, PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                        ++m;
                        handlerStmt.setString(m, PretupsI.WALLET_TYPE_BONUS);
                    } else {
                        ++m;
                        handlerStmt.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                    }
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + currentLevel);
                        continue;
                    }

                    transferProfileProductVO = TransferProfileProductCache
                        .getTransferProfileDetails(channelUserVO.getTransferProfileID(), p_focBatchMatserVO.getProductCode());
                    thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                    String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                    final String remark = null;
                    if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        thresholdValue = transferProfileProductVO.getAltBalanceLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                    }
                    // end
                    // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_focBatchMatserVO.getNetworkCode(),
                    // focBatchItemVO.getCategoryCode()); //threshold value
                    // for zero balance counter..
                    try {
                        m = 0;
                        final boolean isUserThresholdEntryReq = false;
                        final String thresholdType = null;
                        // 24dec addded by nilesh:if previous bal is below
                        // threshold and current bal is above threshold,
                        // then entry in user_threshold_counter.Also,if previous
                        // bal is already below threshold and current bal is
                        // also below threshold
                        // then also entry in user_threshold_counter
                        // table(Discussed with Ved Sir and Protim Sir)
                     
                        if ((previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance >= thresholdValue) || (previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance <= thresholdValue)) {
                            if (log.isDebugEnabled()) {
                                log.debug(methodName, "Entry in threshold counter" + thresholdValue + ", prvbal: " + previousUserBalToBeSetChnlTrfItems + "nbal" + balance);
                            }
                            psmtInsertUserThreshold.clearParameters();
                            m = 0;
                            ++m;
                            psmtInsertUserThreshold.setString(m, channelUserVO.getUserID());
                            ++m;
                            psmtInsertUserThreshold.setString(m, o2cTransferID);
                            ++m;
                            psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getNetworkCode());
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getProductCode());
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);

                            if (balance >= thresholdValue) {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.ABOVE_THRESHOLD_TYPE);
                            } else {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
                            }
                            ++m;
                            psmtInsertUserThreshold.setString(m, focBatchItemVO.getCategoryCode());
                            ++m;
                            psmtInsertUserThreshold.setLong(m, previousUserBalToBeSetChnlTrfItems);
                            ++m;
                            psmtInsertUserThreshold.setLong(m, balance);
                            ++m;
                            psmtInsertUserThreshold.setLong(m, thresholdValue);
                            // added by nilesh
                            ++m;
                            psmtInsertUserThreshold.setString(m, threshold_type);
                            ++m;
                            psmtInsertUserThreshold.setString(m, remark);

                            psmtInsertUserThreshold.executeUpdate();
                        }
                    } catch (SQLException sqle) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("SQLException ");
                    	loggerValue.append(sqle.getMessage());
                        log.error(methodName,  loggerValue);
                        log.errorTrace(methodName, sqle);
                        loggerValue.setLength(0);
                    	loggerValue.append("Error while updating user_threshold_counter table SQL Exception:");
                    	loggerValue.append(sqle.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", o2cTransferID, "", p_focBatchMatserVO.getNetworkCode(),
                            loggerValue.toString());
                    }// end of catch

                }
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                rs8 = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs8.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(focBatchItemVO.getUserId());

                    countsVO.setDailyInCount(rs8.getLong("daily_in_count"));
                    countsVO.setDailyInValue(rs8.getLong("daily_in_value"));
                    countsVO.setWeeklyInCount(rs8.getLong("weekly_in_count"));
                    countsVO.setWeeklyInValue(rs8.getLong("weekly_in_value"));
                    countsVO.setMonthlyInCount(rs8.getLong("monthly_in_count"));
                    countsVO.setMonthlyInValue(rs8.getLong("monthly_in_value"));

                    countsVO.setDailyOutCount(rs8.getLong("daily_out_count"));
                    countsVO.setDailyOutValue(rs8.getLong("daily_out_value"));
                    countsVO.setWeeklyOutCount(rs8.getLong("weekly_out_count"));
                    countsVO.setWeeklyOutValue(rs8.getLong("weekly_out_value"));
                    countsVO.setMonthlyOutCount(rs8.getLong("monthly_out_count"));
                    countsVO.setMonthlyOutValue(rs8.getLong("monthly_out_value"));

                    countsVO.setUnctrlDailyInCount(rs8.getLong("outside_daily_in_count"));
                    countsVO.setUnctrlDailyInValue(rs8.getLong("outside_daily_in_value"));
                    countsVO.setUnctrlWeeklyInCount(rs8.getLong("outside_weekly_in_count"));
                    countsVO.setUnctrlWeeklyInValue(rs8.getLong("outside_weekly_in_value"));
                    countsVO.setUnctrlMonthlyInCount(rs8.getLong("outside_monthly_in_count"));
                    countsVO.setUnctrlMonthlyInValue(rs8.getLong("outside_monthly_in_value"));

                    countsVO.setUnctrlDailyOutCount(rs8.getLong("outside_daily_out_count"));
                    countsVO.setUnctrlDailyOutValue(rs8.getLong("outside_daily_out_value"));
                    countsVO.setUnctrlWeeklyOutCount(rs8.getLong("outside_weekly_out_count"));
                    countsVO.setUnctrlWeeklyOutValue(rs8.getLong("outside_weekly_out_value"));
                    countsVO.setUnctrlMonthlyOutCount(rs8.getLong("outside_monthly_out_count"));
                    countsVO.setUnctrlMonthlyOutValue(rs8.getLong("outside_monthly_out_value"));

                    countsVO.setDailySubscriberOutCount(rs8.getLong("daily_subscriber_out_count"));
                    countsVO.setDailySubscriberOutValue(rs8.getLong("daily_subscriber_out_value"));
                    countsVO.setWeeklySubscriberOutCount(rs8.getLong("weekly_subscriber_out_count"));
                    countsVO.setWeeklySubscriberOutValue(rs8.getLong("weekly_subscriber_out_value"));
                    countsVO.setMonthlySubscriberOutCount(rs8.getLong("monthly_subscriber_out_count"));
                    countsVO.setMonthlySubscriberOutValue(rs8.getLong("monthly_subscriber_out_value"));

                    countsVO.setLastTransferDate(rs8.getDate("last_transfer_date"));
                    countsVO.setLastSOSTxnStatus(rs8.getString("last_sos_txn_status"));
                    countsVO.setLastLrStatus(rs8.getString("last_lr_status"));
                }
                flag = true;
                if (countsVO == null) {
                    flag = false;
                    countsVO = new UserTransferCountsVO();
                }
                // If found then check for reset otherwise no need to check it
                if (flag) {
                    ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO, date);
                }
                pstmtSelectProfileCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectProfileCounts.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                rs9 = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
                if (rs9.next()) {
                    transferProfileVO = new TransferProfileVO();
                    transferProfileVO.setProfileId(rs9.getString("profile_id"));
                    transferProfileVO.setDailyInCount(rs9.getLong("daily_transfer_in_count"));
                    transferProfileVO.setDailyInValue(rs9.getLong("daily_transfer_in_value"));
                    transferProfileVO.setWeeklyInCount(rs9.getLong("weekly_transfer_in_count"));
                    transferProfileVO.setWeeklyInValue(rs9.getLong("weekly_transfer_in_value"));
                    transferProfileVO.setMonthlyInCount(rs9.getLong("monthly_transfer_in_count"));
                    transferProfileVO.setMonthlyInValue(rs9.getLong("monthly_transfer_in_value"));
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile not found", "Approval level = " + currentLevel);
                    continue;
                }
               
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.
                 if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.dailyinvaluereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.weeklyinvaluereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.monthlyinvaluereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                countsVO.setUserID(channelUserVO.getUserID());
                countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                countsVO.setDailyInValue(countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setLastInTime(date);
                countsVO.setLastTransferID(o2cTransferID);
                countsVO.setLastTransferDate(date);
                // Update counts if found in db
                if (flag) {
                    m = 0;
                    pstmtUpdateTransferCounts.clearParameters();
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtUpdateTransferCounts.executeUpdate();
                }
                // Insert counts if not found in db
                else {
                    m = 0;
                    pstmtInsertTransferCounts.clearParameters();
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtInsertTransferCounts.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + currentLevel);
                    } else {
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                rs10 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                // check record is modified or not
                if (rs10.next()) {
                    newlastModified = rs10.getTimestamp("modified_on");
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + currentLevel);
                    continue;
                }
                // (external txn number is checked)
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue() && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo())) {
                    // check in foc_batch_item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    rs11 = pstmtIsTxnNumExists1.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs11.next()) {
                        con.rollback();
                        try {
                            if (rs11 != null) {
                                rs11.close();
                            }
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in FOC Batch",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                    // check in channel transfer table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs12 = pstmtIsTxnNumExists2.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs12.next()) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in CHANNEL TRF",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    focBatchItemVO.setFirstApprovedBy(p_userID);
                    focBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    focBatchItemVO.setSecondApprovedBy(p_userID);
                    focBatchItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
                    psmtAppr3FOCBatchItem.clearParameters();
                    focBatchItemVO.setThirdApprovedBy(p_userID);
                    focBatchItemVO.setThirdApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr3FOCBatchItem.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                channelTransferVO.setCanceledOn(focBatchItemVO.getCancelledOn());
                channelTransferVO.setCanceledBy(focBatchItemVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(focBatchItemVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(focBatchItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(focBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setCreatedBy(focBatchItemVO.getInitiatedBy());
                channelTransferVO.setDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(focBatchItemVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(focBatchItemVO.getExtTxnNo());
                channelTransferVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(focBatchItemVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(focBatchItemVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setBatchNum(focBatchItemVO.getBatchId());
                channelTransferVO.setBatchDate(p_focBatchMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax3(0);
                channelTransferVO.setPayableAmount(0);
                channelTransferVO.setNetPayableAmount(0);
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(p_userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(p_focBatchMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(focBatchItemVO.getCategoryCode());
                channelTransferVO.setReceiverGradeCode(focBatchItemVO.getGradeCode());
                channelTransferVO.setReceiverTxnProfile(focBatchItemVO.getTxnProfile());
                channelTransferVO.setReferenceNum(focBatchItemVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(focBatchItemVO.getStatus());
                channelTransferVO.setThirdApprovedBy(focBatchItemVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(focBatchItemVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(focBatchItemVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                channelTransferVO.setTotalTax1(focBatchItemVO.getTax1Value());
                channelTransferVO.setTotalTax2(focBatchItemVO.getTax2Value());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
                channelTransferVO.setTransferDate(focBatchItemVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(focBatchItemVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(focBatchItemVO.getTransferMrp());
                // added for logger
                channelTransferVO.setControlTransfer(PretupsI.YES);
                channelTransferVO.setToUserCode(focBatchItemVO.getMsisdn());
                channelTransferVO.setReceiverDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setReceiverGgraphicalDomainCode(channelTransferVO.getGraphicalDomainCode());
                channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                channelTransferVO.setActiveUserId(p_focBatchMatserVO.getCreatedBy());
                // end
                channelTransferItemVO = new ChannelTransferItemsVO();
                channelTransferItemVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setCommProfileDetailID(focBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(focBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(focBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(focBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(focBatchItemVO.getTransferMrp());
                channelTransferItemVO.setProductCode(p_focBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                channelTransferItemVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(focBatchItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(focBatchItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(focBatchItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(focBatchItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(focBatchItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(focBatchItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(focBatchItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(focBatchItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(focBatchItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(p_focBatchMatserVO.getProductMrp());
                channelTransferItemVO.setSenderDebitQty(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setReceiverCreditQty(focBatchItemVO.getRequestedQuantity());
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(p_focBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_DP_CODE);
                /* SOS */
                
                
                if(SystemPreferences.USERWISE_LOAN_ENABLE  && channelTransferVO.getUserLoanVOList() != null && channelTransferVO.getUserLoanVOList().size()>0 )  {

    				Map hashmap = ChannelTransferBL.checkUserLoanstatusAndAmount(con, channelTransferVO);
    				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
    					con.rollback();
    					final String args[] = { PretupsBL.getDisplayAmount((long)hashmap.get(PretupsI.WITHDRAW_AMOUNT)) };
    					
    					errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
    							"batcho2c.batchapprovereject.msg.error.loanPending",args));
    					errorList.add(errorVO);
    					BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Loan  PENDING FOR USER",
    							"Approval level = " + currentLevel);
    					continue;

    				}

	    				else if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    					UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
    					userLoanWithdrawBL.autoChannelLoanSettlement(channelTransferVO, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
    				}

    			}
                
                
                Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(con, countsVO, channelTransferVO);
                if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true))
                {
                	con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.sosPending"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : SOS Status PENDING FOR USER",
                        "Approval level = " + currentLevel);
                    continue;
                }
                
                else if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
    			}
                Map<String, Object> lrHashMap = ChannelTransferBL.checkLRstatusAndAmount(con, countsVO, channelTransferVO);
    			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
    			}
                if (log.isDebugEnabled()) {
                    log.debug("closeOrderByBatch", "Exiting: channelTransferVO=" + channelTransferVO.toString());
                }
                if (log.isDebugEnabled()) {
                    log.debug("closeOrderByBatch", "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
                    /*
                     * //The query below is used to insert the record in channel
                     * transfers table for the order that is cloaed
                     */
                }

                m = 0;
                pstmtInsertIntoChannelTranfers.clearParameters();
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCanceledBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
                // commented for
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getChannelRemarks());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileSetId());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileVersion());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCreatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getExternalTxnNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getFirstApproverLimit());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setDate(m, BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getBatchNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFromUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getModifiedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCodeFor());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayInstrumentAmt());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getProductType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverGradeCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverTxnProfile());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReferenceNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayType());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getRequestedQuantity());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getSecondApprovalLimit());
             
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSource());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getStatus());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
             
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getToUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax1());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax2());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax3());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferCategory());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferInitatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTransferMRP());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferSubType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, PretupsI.YES);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getMsisdn());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());

                // By sandeep ID TOG001
                // to geographical domain also inserted as the geogrpahical
                // domain that will help in reports
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());

                // commented for
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDefaultLang());
                // commented for
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondLang());
                // added fo bonus type
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getBonusType());
                if (isOwnerUserNotSame) {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, focBatchItemVO.getRequestedQuantity());
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, focBatchItemVO.getRequestedQuantity());
                } else {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, 0);
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, 0);
                }
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, p_focBatchMatserVO.getCreatedBy());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getDualCommissionType());// ends here
                 // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertIntoChannelTransferItems.clearParameters();
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommProfileDetailID());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getCommRate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommType());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getProductTotalMRP());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getProductCode());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getRequiredQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setInt(m, channelTransferItemVO.getSerialNum());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax1Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax1Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax1Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax2Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax2Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax2Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax3Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax3Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax3Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, o2cTransferID);
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getUnitValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock() - channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity());

                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer items table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                con.commit();
                DirectPayOutSuccessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                    "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                rs13 = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList = new ArrayList();
                while (rs13.next()) {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs13.getString("product_code"));
                    balancesVO.setBalance(rs13.getLong("balance"));
                    balancesVO.setProductShortCode(rs13.getString("product_short_code"));
                    balancesVO.setProductShortName(rs13.getString("short_name"));
                    userbalanceList.add(balancesVO);
                }
                // generate the message arguments to be send in SMS
                keyArgumentVO = new KeyArgumentVO();
                argsArr = new String[2];
                argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
                argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
                keyArgumentVO.setKey(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS2);
                keyArgumentVO.setArguments(argsArr);
                txnSmsMessageList = new ArrayList();
                balSmsMessageList = new ArrayList();
                txnSmsMessageList.add(keyArgumentVO);
                for (int index = 0, n = userbalanceList.size(); index < n; index++) {
                    balancesVO = (UserBalancesVO) userbalanceList.get(index);
                    if (balancesVO.getProductCode().equals(channelTransferItemVO.getProductCode())) {
                        argsArr = new String[2];
                        argsArr[1] = balancesVO.getBalanceAsString();
                        argsArr[0] = balancesVO.getProductShortName();
                        keyArgumentVO = new KeyArgumentVO();
                        keyArgumentVO.setKey(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String dpNotifyMsg = null;
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_SMS_NOTIFY))).booleanValue()) {
                    final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        dpNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        dpNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList), dpNotifyMsg };
                }

                if (dpNotifyMsg == null) {
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList) };
                }

                messages = new BTSLMessages(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS1, array);
                pushMessage = new PushMessage(focBatchItemVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
                pushMessage.push();
                OneLineTXNLog.log(channelTransferVO, focBatchItemVO);
            }// end of while
        }catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
        	loggerValue.append("sqlException  ");
        	loggerValue.append(sqe);
            log.error("closeOrderByBatch", loggerValue );
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", sqlException + sqe.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, sqlException + sqe.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error("closeOrderByBatch", exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", exception + ex.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, exception + ex.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectOwner != null) {
                	pstmtSelectOwner.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadNetworkStock != null) {
                    pstmtLoadNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateNetworkStock != null) {
                    pstmtUpdateNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkDailyStock != null) {
                    pstmtInsertNetworkDailyStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectNetworkStock != null) {
                    pstmtSelectNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateSelectedNetworkStock != null) {
                    pstmtupdateSelectedNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransaction != null) {
                    pstmtInsertNetworkStockTransaction.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransactionItem != null) {
                    pstmtInsertNetworkStockTransactionItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserBalances != null) {
                    pstmtSelectUserBalances.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalances != null) {
                    pstmtUpdateUserBalances.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserDailyBalances != null) {
                    pstmtInsertUserDailyBalances.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalance != null) {
                    pstmtSelectBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBalance != null) {
                    pstmtUpdateBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBalance != null) {
                    pstmtInsertBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTransferCounts != null) {
                    pstmtSelectTransferCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectProfileCounts != null) {
                    pstmtSelectProfileCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateTransferCounts != null) {
                    pstmtUpdateTransferCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertTransferCounts != null) {
                    pstmtInsertTransferCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr3FOCBatchItem != null) {
                    psmtAppr3FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadTransferProfileProduct != null) {
                    pstmtLoadTransferProfileProduct.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (handlerStmt != null) {
                    handlerStmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTransferItems != null) {
                    pstmtInsertIntoChannelTransferItems.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTranfers != null) {
                    pstmtInsertIntoChannelTranfers.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalanceInfoForMessage != null) {
                    pstmtSelectBalanceInfoForMessage.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs4 != null) {
                    rs4.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs5 != null) {
                    rs5.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs6 != null) {
                    rs6.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs7 != null) {
                    rs7.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs8 != null) {
                    rs8.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs9 != null) {
                    rs9.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs10 != null) {
                    rs10.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs11 != null) {
                    rs11.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs12 != null) {
                    rs12.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs13 != null) {
                    rs13.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs.close();
                rs = null;
                rs = pstmtSelectItemsDetails.executeQuery();
                // Check the final status to be updated in master after
                // processing all records of batch
                if (rs.next()) {
                    final int totalCount = rs.getInt("batch_total_record");
                    final int closeCount = rs.getInt("closed");
                    final int cnclCount = rs.getInt("cncl");
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                con.commit();
            } catch (SQLException sqe) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error("closeOrderByBatch", sqlException + sqe);
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", sqlException + sqe.getMessage());
                DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, sqlException + sqe.getMessage(), "Approval level = " + currentLevel);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
            	loggerValue.append("exception ");
            	loggerValue.append(ex);
                log.error("closeOrderByBatch", loggerValue);
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", exception + ex.getMessage());
                DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, exception + ex.getMessage(), "Approval level = " + currentLevel);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
                
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size=");
            	loggerValue.append(errorList.size());
                log.debug(methodName,  loggerValue);
            }
        }
        return errorList;
    }
   

    /**
     * Method processOrderByDPBatch
     * Method to cancel/approve the batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are: foc_batch_items,foc_batches
     * 
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_messages
     * @param p_locale
     * @param p_sms_default_lang
     * @param p_sms_second_lang
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList processOrderByDPBatch(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByDPBatch";
        StringBuilder loggerValue= new StringBuilder(); 
		
		
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered p_dataMap=" );
            loggerValue.append(p_dataMap);
            loggerValue.append(" p_currentLevel=");
            loggerValue.append(p_currentLevel);
            loggerValue.append(" p_locale=");
            loggerValue.append(p_locale);
            loggerValue.append(" p_userID=");
            loggerValue.append(p_userID);
            log.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtLoadUser = null;

        PreparedStatement psmtCancelFOCBatchItem = null;
        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        int updateCount = 0;
        String batch_ID = null;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuilder sqlBuffer = null;
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        HashMap<String, HashMap<String, String>> tcpMap = null;
        
        if(tcpOn) {
        	sqlBuffer = new StringBuilder(" SELECT cusers.transfer_profile_id, u.status userstatus, cusers.in_suspend, ");
            sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
            sqlBuffer.append("cps.language_2_message comprf_lang_2_msg ");
            sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
            sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
            sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
            sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
            sqlBuffer.append("  ");
 	
            SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
					ValueType.STRING);
        	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
        	
        }else {

         sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        }
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlLoadUser=" );
            loggerValue.append(sqlLoadUser);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;
        // after validating if request is to cancle the order, the below query
        // is used.
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" third_approver_remarks = ?, ");
        }
        sqlBuffer.append(" cancelled_by = ?, ");
        sqlBuffer.append(" cancelled_on = ?, status = ?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        if (!PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" AND status IN (? , ? )  ");
        } else {
            sqlBuffer.append(" AND status  = ?   ");
        }
        final String sqlCancelFOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlCancelFOCBatchItems=");
            loggerValue.append(sqlCancelFOCBatchItems);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // after validating if request is of level 1 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");

        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlApprv1FOCBatchItems=");
            loggerValue.append(sqlApprv1FOCBatchItems);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // after validating if request is of level 2 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table        
        final String selectItemsDetails = focBatchTransferQry.focBatcheSelectItemDetailsQry();
    
        sqlBuffer = null;
        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "QUERY isModified=" );
            loggerValue.append(isModified);
            log.debug(methodName,loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "QUERY isExistsTxnNum1=");
            loggerValue.append(isExistsTxnNum1);
            log.debug(methodName, loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuilder("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "QUERY isExistsTxnNum2=");
            loggerValue.append(isExistsTxnNum2);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;
        Date date = null;
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            
            psmtCancelFOCBatchItem = p_con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // commented for
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            int m = 0;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
	                if(BTSLUtil.isNullString(focBatchItemVO.getNewStatus())|| !focBatchItemVO.getNewStatus().equalsIgnoreCase("D")){
	                try {
	                rs = pstmtLoadUser.executeQuery();
	                if (rs.next())// check data found or not
	                {
	                    channelUserVO = new ChannelUserVO();
	                    channelUserVO.setUserID(focBatchItemVO.getUserId());
	                    channelUserVO.setStatus(rs.getString("userstatus"));
	                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
	                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	                    
	                    if(!tcpOn) {
	                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
	                    }else {
	                    	 channelUserVO.setTransferProfileStatus(tcpMap.get(rs.getString("transfer_profile_id")).get("profileStatus"));//TCP
	                    }
	                    
	                    // (User status is checked) if this condition is true then
	                    // made entry in logs and leave this data.
	                    if(!focBatchItemVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL)){
	                    	if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
		                            "batchdirectpayout.batchapprovereject.msg.error.usersuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is not active", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                    // (commission profile status is checked) if this condition
		                    // is true then made entry in logs and leave this data.
		                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
		                            "batchdirectpayout.batchapprovereject.msg.error.comprofsuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                    // (tranmsfer profile status is checked) if this condition
		                    // is true then made entry in logs and leave this data.
		                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
		                            "batchdirectpayout.batchapprovereject.msg.error.trfprofsuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                    // (user in suspend is checked) if this condition is true
		                    // then made entry in logs and leave this data.
		                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
		                            "batchdirectpayout.batchapprovereject.msg.error.userinsuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                }
	                }
	                // (record not found for user) if this condition is true then
	                // made entry in logs and leave this data.
	                else {
	                    p_con.rollback();
	                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
	                        "batchdirectpayout.batchapprovereject.msg.error.nouser"));
	                    errorList.add(errorVO);
	                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
	                    continue;
	
	                }
	                }
	                finally {
	                	if(rs!=null)
	                		rs.close();
	                }
	             }else{
					focBatchItemVO.setStatus("CNCL");					
	             }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                java.sql.Timestamp newlastModified = null;
                try {
                rs = null;
                rs = pstmtIsModified.executeQuery();
                if (rs.next()) {
                    newlastModified = rs.getTimestamp("modified_on");
                   
                }
                // (record not found means it is modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue() && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
                    .equals(focBatchItemVO.getStatus())) {
                    // check in foc_batch-item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    try {
                    rs = null;
                    rs = pstmtIsTxnNumExists1.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists BATCH Direct Payout",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                    // check in channel_transfers table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    try {
                    rs = null;
                    rs = pstmtIsTxnNumExists2.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelFOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(focBatchItemVO.getStatus())) {
                    psmtCancelFOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                       
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        // commented for
                        // DB2psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                       
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtCancelFOCBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(focBatchItemVO.getStatus())) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(focBatchItemVO.getStatus())) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                   
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processiong each record
                p_con.commit();
            }// end of while
             // Check the status to be updated in master table agfter processing
             // of all records

        }// end of try
        catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append( sqlException);
            loggerValue.append(sqe);
            log.error("processOrderByBatch", loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append( sqlException);
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "", "",
                "", loggerValue.toString());
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append(exception);
            loggerValue.append(ex);
            log.error("processOrderByBatch", loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append(exception);
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "", "",
                "", loggerValue.toString() );
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelFOCBatchItem != null) {
                    psmtCancelFOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs.close();
                rs = null;
                rs = pstmtSelectItemsDetails.executeQuery();
                if (rs.next()) {
                    final int totalCount = rs.getInt("batch_total_record");
                    final int closeCount = rs.getInt("closed");
                    final int cnclCount = rs.getInt("cncl");
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    // If all records are canle then set cancelled in master
                    // table
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE;
                        // Otherwise set OPEN in mastrer table
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[processOrderByDPBatch]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
            	loggerValue.append(sqlException );
            	loggerValue.append(sqe);
                log.error(methodName,  loggerValue );
                log.errorTrace(methodName, sqe);
                loggerValue.setLength(0);
            	loggerValue.append(sqlException );
            	loggerValue.append(sqe.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "",
                    "", "", loggerValue.toString());
                DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
            	loggerValue.append(exception );
            	loggerValue.append(ex);
                log.error("processOrderByBatch",  loggerValue);
                log.errorTrace(methodName, ex);
                loggerValue.setLength(0);
            	loggerValue.append(exception );
            	loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "",
                    "", "", loggerValue.toString() );
                DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size=" );
            	loggerValue.append(errorList.size());
                log.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }
    //addded to validate quantity for multipl of
    public CommissionProfileProductsVO getMultipleOff(Connection con,String msisdn, String loginid) {    	
    	String methodName = "getMultipleOff";
    	StringBuilder loggerValue= new StringBuilder(); 
    	 if (log.isDebugEnabled()) {
    		 loggerValue.setLength(0);
    		 loggerValue.append("Entered msisdn=");
    		 loggerValue.append(loginid);
    		 loggerValue.append(" loginid=");
             log.debug(methodName, loggerValue );
         }
    	long multipleVal=0;
    	ResultSet rsSelectCProfileProd = null;
    	CommissionProfileProductsVO cpVO = null;
       
        String strBuffSelectCProfileProd = focBatchTransferQry.getMultipleOffQry();
    	try (PreparedStatement pstmtSelectCProfileProd=con.prepareStatement(strBuffSelectCProfileProd);){
    		
    		
    		int index = 0;
            ++index;
            pstmtSelectCProfileProd.setString(index, msisdn);
            ++index;
            pstmtSelectCProfileProd.setString(index, loginid);
            ++index;
            pstmtSelectCProfileProd.setString(index, PretupsI.USER_STATUS_DELETED);
            rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
    		if(rsSelectCProfileProd.next()) {
    			cpVO = new CommissionProfileProductsVO();
    			cpVO.setMinTransferValue(rsSelectCProfileProd.getLong("MIN_TRANSFER_VALUE"));
    			cpVO.setMaxTransferValue(rsSelectCProfileProd.getLong("MAX_TRANSFER_VALUE"));
    			cpVO.setTransferMultipleOff(rsSelectCProfileProd.getLong("transfer_multiple_off"));
    		}
    	}catch (SQLException sqe) {
    		loggerValue.setLength(0);
   		    loggerValue.append(sqlException);
   		    loggerValue.append(sqe);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
   		    loggerValue.append(sqlException);
   		    loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[getMultipleOff]", "",
                "", "", loggerValue.toString() );
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
    		  loggerValue.append(exception);
    		  loggerValue.append(ex);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
  		  loggerValue.append(exception);
  		  loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[getMultipleOff]", "",
                "", "", loggerValue.toString() );
        } finally {
            try {
                if (rsSelectCProfileProd != null) {
                	rsSelectCProfileProd.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        }
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
    		loggerValue.append("Exiting: cpVO=");
    		loggerValue.append(cpVO);
            log.debug(methodName, loggerValue );
        } 
        return cpVO;
 }
    

    /**
     * Method processOrderByDPBatchRest
     * Method to cancel/approve the batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are: foc_batch_items,foc_batches
     * 
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_messages
     * @param p_locale
     * @param p_sms_default_lang
     * @param p_sms_second_lang
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList processOrderByDPBatchRest(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByDPBatch";
        StringBuilder loggerValue= new StringBuilder(); 
		
		
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
//            loggerValue.append("Entered p_dataMap=" );
//            loggerValue.append(p_dataMap);
            loggerValue.append(" p_currentLevel=");
            loggerValue.append(p_currentLevel);
            loggerValue.append(" p_locale=");
            loggerValue.append(p_locale);
            loggerValue.append(" p_userID=");
            loggerValue.append(p_userID);
            log.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtLoadUser = null;

        PreparedStatement psmtCancelFOCBatchItem = null;
        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        int updateCount = 0;
        String batch_ID = null;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        
        StringBuilder sqlBuffer = null; 
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        HashMap<String, HashMap<String, String>> tcpMap = null;
        
        if(tcpOn) {
        	 sqlBuffer = new StringBuilder(" SELECT cusers.transfer_profile_id, u.status userstatus, cusers.in_suspend, ");
             sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
             sqlBuffer.append("cps.language_2_message comprf_lang_2_msg ");
             sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps  ");
             sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
             sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
             sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
             sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
             
             SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
 					ValueType.STRING);
         	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
         	
 	
        }else {

         sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        
        }
        
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlLoadUser=" );
            loggerValue.append(sqlLoadUser);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;
        // after validating if request is to cancle the order, the below query
        // is used.
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" third_approver_remarks = ?, ");
        }
        sqlBuffer.append(" cancelled_by = ?, ");
        sqlBuffer.append(" cancelled_on = ?, status = ?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        if (!PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" AND status IN (? , ? )  ");
        } else {
            sqlBuffer.append(" AND status  = ?   ");
        }
        final String sqlCancelFOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlCancelFOCBatchItems=");
            loggerValue.append(sqlCancelFOCBatchItems);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // after validating if request is of level 1 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");

        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlApprv1FOCBatchItems=");
            loggerValue.append(sqlApprv1FOCBatchItems);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // after validating if request is of level 2 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table        
        final String selectItemsDetails = focBatchTransferQry.focBatcheSelectItemDetailsQry();
    
        sqlBuffer = null;
        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "QUERY isModified=" );
            loggerValue.append(isModified);
            log.debug(methodName,loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "QUERY isExistsTxnNum1=");
            loggerValue.append(isExistsTxnNum1);
            log.debug(methodName, loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuilder("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "QUERY isExistsTxnNum2=");
            loggerValue.append(isExistsTxnNum2);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;
        Date date = null;
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            
            psmtCancelFOCBatchItem = p_con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // commented for
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            int m = 0;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
	                if(BTSLUtil.isNullString(focBatchItemVO.getNewStatus())|| !focBatchItemVO.getNewStatus().equalsIgnoreCase("D")){
	                try {
	                rs = pstmtLoadUser.executeQuery();
	                if (rs.next())// check data found or not
	                {
	                    channelUserVO = new ChannelUserVO();
	                    channelUserVO.setUserID(focBatchItemVO.getUserId());
	                    channelUserVO.setStatus(rs.getString("userstatus"));
	                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
	                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	                    
							if (!tcpOn) {
								channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
							} else {
								channelUserVO.setTransferProfileStatus(
										tcpMap.get(rs.getString("transfer_profile_id")).get("profileStatus"));// TCP
							}
	                    // (User status is checked) if this condition is true then
	                    // made entry in logs and leave this data.
	                    if(!focBatchItemVO.getStatus().equalsIgnoreCase(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL)){
	                    	if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
		                            "batchdirectpayout.batchapprovereject.msg.error.usersuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is not active", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                    // (commission profile status is checked) if this condition
		                    // is true then made entry in logs and leave this data.
		                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
		                            "batchdirectpayout.batchapprovereject.msg.error.comprofsuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                    // (tranmsfer profile status is checked) if this condition
		                    // is true then made entry in logs and leave this data.
		                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
		                            "batchdirectpayout.batchapprovereject.msg.error.trfprofsuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                    // (user in suspend is checked) if this condition is true
		                    // then made entry in logs and leave this data.
		                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
		                        p_con.rollback();
		                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
		                            "batchdirectpayout.batchapprovereject.msg.error.userinsuspend"));
		                        errorList.add(errorVO);
		                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
		                        continue;
		                    }
		                }
	                }
	                // (record not found for user) if this condition is true then
	                // made entry in logs and leave this data.
	                else {
	                    p_con.rollback();
	                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
	                        "batchdirectpayout.batchapprovereject.msg.error.nouser"));
	                    errorList.add(errorVO);
	                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
	                    continue;
	
	                }
	                }
	                finally {
	                	if(rs!=null)
	                		rs.close();
	                }
	             }else{
					focBatchItemVO.setStatus("CNCL");					
	             }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                java.sql.Timestamp newlastModified = null;
                try {
                rs = null;
                rs = pstmtIsModified.executeQuery();
                if (rs.next()) {
                    newlastModified = rs.getTimestamp("modified_on");
                   
                }
                // (record not found means it is modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (SystemPreferences.EXTERNAL_TXN_UNIQUE && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
                    .equals(focBatchItemVO.getStatus())) {
                    // check in foc_batch-item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    try {
                    rs = null;
                    rs = pstmtIsTxnNumExists1.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists BATCH Direct Payout",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                    // check in channel_transfers table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    try {
                    rs = null;
                    rs = pstmtIsTxnNumExists2.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelFOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(focBatchItemVO.getStatus())) {
                    psmtCancelFOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                       
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        // commented for
                        // DB2psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                       
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtCancelFOCBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(focBatchItemVO.getStatus())) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(focBatchItemVO.getStatus())) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                   
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.dpBatchItemLog(methodName, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processiong each record
                p_con.commit();
            }// end of while
             // Check the status to be updated in master table agfter processing
             // of all records

        }// end of try
        catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append( sqlException);
            loggerValue.append(sqe);
            log.error("processOrderByBatch", loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append( sqlException);
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "", "",
                "", loggerValue.toString());
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append(exception);
            loggerValue.append(ex);
            log.error("processOrderByBatch", loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append(exception);
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "", "",
                "", loggerValue.toString() );
            DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelFOCBatchItem != null) {
                    psmtCancelFOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs.close();
                rs = null;
                rs = pstmtSelectItemsDetails.executeQuery();
                if (rs.next()) {
                    final int totalCount = rs.getInt("batch_total_record");
                    final int closeCount = rs.getInt("closed");
                    final int cnclCount = rs.getInt("cncl");
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    // If all records are canle then set cancelled in master
                    // table
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE;
                        // Otherwise set OPEN in mastrer table
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "",PretupsRestUtil.getMessageString( "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[processOrderByDPBatch]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
            	loggerValue.append(sqlException );
            	loggerValue.append(sqe);
                log.error(methodName,  loggerValue );
                log.errorTrace(methodName, sqe);
                loggerValue.setLength(0);
            	loggerValue.append(sqlException );
            	loggerValue.append(sqe.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "",
                    "", "", loggerValue.toString());
                DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
            	loggerValue.append(exception );
            	loggerValue.append(ex);
                log.error("processOrderByBatch",  loggerValue);
                log.errorTrace(methodName, ex);
                loggerValue.setLength(0);
            	loggerValue.append(exception );
            	loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByDPBatch]", "",
                    "", "", loggerValue.toString() );
                DirectPayOutErrorLog.dpBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size=" );
            	loggerValue.append(errorList.size());
                log.debug(methodName, loggerValue);
            }
        }
        return errorList;
    }
    
    
    /**
     * Method closeOrderByBatchForDirectPayout
     * Method to close the direct payout order by batch. This also perform all
     * the data validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,foc_batch_items,
     * foc_batches,
     * channel_transfers_items,channel_transfers
     * 
     * 
     * @param con
     * @param dataMap
     * @param currentLevel
     * @param p_userID
     * @param p_focBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @param p_sms_default_lang
     * @param p_sms_second_lang
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatchForDirectPayoutRest(Connection con, LinkedHashMap dataMap, String currentLevel,
    		String p_userID, FOCBatchMasterVO p_focBatchMatserVO, Locale p_locale,
    		String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
    	

        final String methodName = "closeOrderByBatchForDirectPayout";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
//        	loggerValue.append("Entered p_dataMap=");
//        	loggerValue.append(dataMap);
        	loggerValue.append(" p_currentLevel=");
        	loggerValue.append(currentLevel);
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
        	
            log.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement pstmtLoadNetworkStock = null;
        PreparedStatement pstmtUpdateNetworkStock = null;
        PreparedStatement pstmtInsertNetworkDailyStock = null;
        PreparedStatement pstmtSelectNetworkStock = null;
        PreparedStatement pstmtupdateSelectedNetworkStock = null;
        // commented for DB2 OraclePreparedStatement
        // pstmtInsertNetworkStockTransaction=null;
        PreparedStatement pstmtInsertNetworkStockTransaction = null;
        PreparedStatement pstmtInsertNetworkStockTransactionItem = null;
        PreparedStatement pstmtSelectUserBalances = null;
        PreparedStatement pstmtUpdateUserBalances = null;
        PreparedStatement pstmtInsertUserDailyBalances = null;
        PreparedStatement pstmtSelectBalance = null;
        PreparedStatement pstmtUpdateBalance = null;
        PreparedStatement pstmtInsertBalance = null;
        PreparedStatement pstmtSelectTransferCounts = null;
        PreparedStatement pstmtSelectProfileCounts = null;
        PreparedStatement pstmtUpdateTransferCounts = null;
        PreparedStatement pstmtInsertTransferCounts = null;
        // commented for DB2OraclePreparedStatement psmtAppr1FOCBatchItem =
        // null;
        // commented for DB2OraclePreparedStatement psmtAppr2FOCBatchItem =
        // null;
        // commented for DB2OraclePreparedStatement psmtAppr3FOCBatchItem =
        // null;

        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement psmtAppr3FOCBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        // PreparedStatement pstmtUpdateMaster= null;
        // commented for DB2OraclePreparedStatement pstmtUpdateMaster= null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        // commented for DB2OraclePreparedStatement
        // pstmtInsertIntoChannelTranfers=null;
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
        PreparedStatement pstmtSelectOwner = null;
        ArrayList userbalanceList = null;
        UserBalancesVO balancesVO = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        ResultSet rs5 = null;
        ResultSet rs6 = null;
        ResultSet rs7 = null;
        ResultSet rs8 = null;
        ResultSet rs9 = null;
        ResultSet rs10 = null;
        ResultSet rs11 = null;
        ResultSet rs12 = null;
        ResultSet rs13 = null;
        
        String language = null;
        String country = null;
        KeyArgumentVO keyArgumentVO = null;
        String[] argsArr = null;
        ArrayList txnSmsMessageList = null;
        ArrayList balSmsMessageList = null;
        Locale locale = null;
        String[] array = null;
        BTSLMessages messages = null;
        PushMessage pushMessage = null;
        int updateCount = 0, insertCount = 0;
        String o2cTransferID = null;
        boolean isDpAllowed = false;
        isDpAllowed = SystemPreferences.DP_ALLOWED;

        PreparedStatement psmtInsertUserThreshold = null;
        long thresholdValue = -1;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        
        StringBuilder sqlBuffer = null;
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        HashMap<String, HashMap<String, String>> tcpMap = null;

        
        if(tcpOn) {

        	 sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, ");
             sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
             sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
             sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps, user_phones up,user_geographies ug ");
             sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
             sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
             sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
             sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
             sqlBuffer.append("  ug.user_id = u.user_id ");
             
             SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
 					ValueType.STRING);
         	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
         	
        }else {



         sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
        }
        
        
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
       
        final String sqlLoadNetworkStock = focBatchTransferQry.closeOrderByBatchForDirectPayoutLoadNetworkStockQry();
       
        sqlBuffer = null;
        // Update daily_stock_updated_on with current date
        sqlBuffer = new StringBuilder("UPDATE network_stocks SET daily_stock_updated_on = ? ");
        sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? ");
        final String sqlUpdateNetworkStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdateNetworkStock=" + sqlUpdateNetworkStock);
        }
        sqlBuffer = null;
        sqlBuffer = new StringBuilder("INSERT INTO network_daily_stocks(wallet_date, wallet_type, network_code, network_code_for, ");
        sqlBuffer.append("product_code, wallet_created, wallet_returned, wallet_balance, wallet_sold, last_txn_no, ");
        sqlBuffer.append("last_txn_type, last_txn_balance, previous_balance, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String sqlInsertNetworkDailyStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlInsertNetworkDailyStock=" + sqlInsertNetworkDailyStock);
        }
       
        final String sqlSelectNetworkStock = focBatchTransferQry.closeOrderByBatchForDirectPayoutSelectWalletQry();
  
        sqlBuffer = null;
        // Debit the network stock
        sqlBuffer = new StringBuilder(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance = ?, ");
        sqlBuffer.append(" wallet_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
        sqlBuffer.append(" modified_by =?, modified_on =? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" network_code = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code_for = ?  AND wallet_type = ? ");
        final String updateSelectedNetworkStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateSelectedNetworkStock=" + updateSelectedNetworkStock);
        }
        sqlBuffer = null;

        // Insert record into network_stock_transactions table.
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_transactions ( ");
        sqlBuffer.append(" txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
        sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
        sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
        sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp ");
        sqlBuffer.append(",txn_wallet,ref_txn_id ");
        sqlBuffer.append(" )VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        
        final String insertNetworkStockTransaction = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertNetworkStockTransaction=" + insertNetworkStockTransaction);
        }
        sqlBuffer = null;

        // Insert record into network_stock_trans_items
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_trans_items ");
        sqlBuffer.append(" (s_no, txn_no, product_code, required_quantity, approved_quantity, stock, mrp, amount, date_time) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?) ");
        final String insertNetworkStockTransactionItem = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertNetworkStockTransactionItem=" + insertNetworkStockTransactionItem);
        }
        sqlBuffer = null;

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be insert in user_daily_balances table
        final String selectUserBalances = focBatchTransferQry.closeOrderByBatchForDirectPayoutUserBalancesQry();
     
        sqlBuffer = null;
        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateUserBalances=" );
        	loggerValue.append(updateUserBalances);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in user_daily_balances
        sqlBuffer = new StringBuilder(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type,balance_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserDailyBalances = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertUserDailyBalances=" );
        	loggerValue.append(insertUserDailyBalances);
            log.debug(methodName,loggerValue );
        }
       
        final String selectBalance = focBatchTransferQry.closeOrderByBatchForDirectPayoutSelectBalanceQry();
   
        sqlBuffer = null;
        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append("  balance_type = ? AND user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        final String updateBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateBalance=" + updateBalance);
        }
        sqlBuffer = null;

        // Insert the record of balnce for user (If balance not found in
        // user_balances)
        sqlBuffer = new StringBuilder(" INSERT ");
        sqlBuffer.append(" INTO user_balances ");
        sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , balance_type,  ");
        sqlBuffer.append(" user_id, product_code , network_code, network_code_for  ) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertBalance=");
        	loggerValue.append(insertBalance);
            log.debug(methodName, loggerValue);
        }
        sqlBuffer = null;

        // Select the running countres of user(to be checked against the
        // effetive profile counters)
        sqlBuffer = new StringBuilder(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
        sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
        sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
        sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
        sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
        sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
        sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
        sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
        sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
        sqlBuffer.append(" ,last_sos_txn_status,last_lr_status  ");
        sqlBuffer.append(" FROM user_transfer_counts ");
        // DB220120123for update WITH

        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS ");
        } else {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        }
        final String selectTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectTransferCounts=");
        	loggerValue.append(selectTransferCounts);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Select the effective profile counters of user to be checked with
        // running counters of user
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
        final String selectProfileCounts = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectProfileCounts=" );
        	loggerValue.append(selectProfileCounts);
            log.debug(methodName, loggerValue);
        }

        // Update the user running countres (If record found for user running
        // counters)
        sqlBuffer = new StringBuilder(" UPDATE user_transfer_counts  SET ");
        sqlBuffer.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
        sqlBuffer.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
        sqlBuffer.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
        sqlBuffer.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
        sqlBuffer.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
        sqlBuffer.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
        sqlBuffer.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
        sqlBuffer.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
        sqlBuffer.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
        sqlBuffer.append(" last_in_time = ? , last_transfer_id=?,last_transfer_date=? ");
        sqlBuffer.append(" WHERE user_id = ?  ");
        final String updateTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateTransferCounts=" );
        	loggerValue.append(updateTransferCounts);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Insert the record in user_transfer_counts (If no record found for
        // user running counters)
        sqlBuffer = new StringBuilder(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        final String insertTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertTransferCounts=" );
        	loggerValue.append(insertTransferCounts);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv1FOCBatchItems=" );
        	loggerValue.append(sqlApprv1FOCBatchItems);
            log.debug(methodName, loggerValue);
        }
        sqlBuffer = null;

        // If current level of approval is 2 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv2FOCBatchItems=" );
        	loggerValue.append(sqlApprv2FOCBatchItems);
            log.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // If current level of approval is 3 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" third_approver_remarks = ?, ");
        sqlBuffer.append(" third_approved_by=? , third_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status = ?  ");
        final String sqlApprv3FOCBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv3FOCBatchItems=" );
        	loggerValue.append(sqlApprv3FOCBatchItems);
            log.debug(methodName, loggerValue);
        }
        
        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
        final String selectItemsDetails = focBatchTransferQry.focBatcheSelectItemDetailsQry();
      
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isModified=");
        	loggerValue.append(isModified);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // Select the transfer profile product values(These will be used for
        // checking max balance of user)
        sqlBuffer = new StringBuilder("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
        sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        sqlBuffer
            .append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
        final String loadTransferProfileProduct = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY loadTransferProfileProduct=");
        	loggerValue.append(loadTransferProfileProduct);
            log.debug(methodName,  loggerValue);
        } 
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
         	loggerValue.setLength(0);
        	loggerValue.append("QUERY isExistsTxnNum1=");
        	loggerValue.append(isExistsTxnNum1);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuilder("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isExistsTxnNum2=");
        	loggerValue.append(isExistsTxnNum2);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // The query bel;ow is used to insert the record in channel transfer
        // items table for the order that is closed
        // query modified for adding sender_debit_quantity,
        // receiver_credit_quantity,commision_quantity, sender_post_stock,
        // receiver_post_stock, these fields was 0 earlier
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer
            .append(" tax3_value, transfer_date, transfer_id, user_unit_price,sender_debit_quantity, receiver_credit_quantity,commision_quantity, sender_post_stock, receiver_post_stock )  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertIntoChannelTransferItem=");
        	loggerValue.append(insertIntoChannelTransferItem);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // The query below is used to get the balance information of user with
        // product.
        // This information will be send in message to user
        sqlBuffer = new StringBuilder(" SELECT UB.product_code,UB.balance, ");
        sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
        sqlBuffer.append(" FROM user_balances UB,products PROD ");
        sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code ");
        final String selectBalanceInfoForMessage = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectBalanceInfoForMessage=");
        	loggerValue.append(selectBalanceInfoForMessage);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The query is used to get the owner user of userd ID
        sqlBuffer = new StringBuilder();
        sqlBuffer.append(" select owner_id from users where user_id=?");
        final String selectowner = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectowner=");
        	loggerValue.append(selectowner);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers ");
        sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
        sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
        sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, from_user_id, grph_domain_code, ");
        sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
        sqlBuffer.append("  product_type, receiver_category_code, receiver_grade_code, ");
        sqlBuffer.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
        sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
        sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
        sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
        sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
        sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,bonus_type ");
        sqlBuffer.append(",owner_transfer_mrp,owner_debit_mrp,active_user_id");

        sqlBuffer.append(",TXN_WALLET,dual_comm_type)");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ");
        sqlBuffer.append(",?,?,?,?");

        sqlBuffer.append(")");
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertIntoChannelTransfer=");
        	loggerValue.append(insertIntoChannelTransfer);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // added by nilesh:added two new colums threshold_type and remark
        final StringBuilder strBuffThresholdInsert = new StringBuilder();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertUserThreshold=");
        	loggerValue.append(insertUserThreshold);
            log.debug(methodName,  loggerValue );
        }
        Date date = null;
        String batch_ID = null;

        // start of try
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            ChannelTransferVO channelTransferVO = null;
            ChannelTransferItemsVO channelTransferItemVO = null;
            date = new Date();
            ArrayList channelTransferItemVOList = null;
            pstmtLoadUser = con.prepareStatement(sqlLoadUser);
            pstmtLoadNetworkStock = con.prepareStatement(sqlLoadNetworkStock);
            pstmtUpdateNetworkStock = con.prepareStatement(sqlUpdateNetworkStock);
            pstmtInsertNetworkDailyStock = con.prepareStatement(sqlInsertNetworkDailyStock);
            pstmtSelectNetworkStock = con.prepareStatement(sqlSelectNetworkStock);
            pstmtupdateSelectedNetworkStock = con.prepareStatement(updateSelectedNetworkStock);
            pstmtInsertNetworkStockTransaction = con.prepareStatement(insertNetworkStockTransaction);
            pstmtInsertNetworkStockTransactionItem = con.prepareStatement(insertNetworkStockTransactionItem);
            pstmtSelectUserBalances = con.prepareStatement(selectUserBalances);
            pstmtUpdateUserBalances = con.prepareStatement(updateUserBalances);
            pstmtInsertUserDailyBalances = con.prepareStatement(insertUserDailyBalances);
            pstmtSelectBalance = con.prepareStatement(selectBalance);
            pstmtUpdateBalance = con.prepareStatement(updateBalance);
            pstmtInsertBalance = con.prepareStatement(insertBalance);
            pstmtSelectTransferCounts = con.prepareStatement(selectTransferCounts);
            pstmtSelectProfileCounts = con.prepareStatement(selectProfileCounts);
            pstmtUpdateTransferCounts = con.prepareStatement(updateTransferCounts);
            pstmtInsertTransferCounts = con.prepareStatement(insertTransferCounts);

            psmtAppr1FOCBatchItem = con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtAppr3FOCBatchItem = con.prepareStatement(sqlApprv3FOCBatchItems);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster = con.prepareStatement(updateFOCBatches);
            pstmtIsModified = con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = con.prepareStatement(loadTransferProfileProduct);
            pstmtIsTxnNumExists1 = con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = con.prepareStatement(isExistsTxnNum2);
            pstmtInsertIntoChannelTransferItems = con.prepareStatement(insertIntoChannelTransferItem);
            pstmtInsertIntoChannelTranfers = con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = con.prepareStatement(selectBalanceInfoForMessage);
            pstmtSelectOwner = con.prepareStatement(selectowner);
            psmtInsertUserThreshold = con.prepareStatement(insertUserThreshold);
            errorList = new ArrayList();
            final Iterator iterator = dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(SystemPreferences.DEFAULT_WEB_GATEWAY_CODE);
            NetworkStockVO networkStocksVO = null;
            int dayDifference = 0;
            Date dailyStockUpdatedOn = null;
            long stock = -1;
            long stockSold = -1;
            NetworkStockTxnVO networkStockTxnVO = null;
            String network_id = null;
            Date dailyBalanceUpdatedOn = null;
            NetworkStockTxnItemsVO networkItemsVO = null;
            TransferProfileProductVO transferProfileProductVO = null;
            UserTransferCountsVO countsVO = null;
            TransferProfileVO transferProfileVO = null;
            C2STransferVO userVO = null;
            long maxBalance = 0;
            boolean isNotToExecuteQuery = false;
            long balance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;
            boolean isOwnerUserNotSame;
            Boolean balanceExist = false;
            while (iterator.hasNext()) {
                balanceExist = false;
                insertCount = 0;
                isOwnerUserNotSame = false;
                terminateProcessing = false;
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) dataMap.get(key);
                pstmtSelectOwner.clearParameters();
                pstmtSelectOwner.setString(1, focBatchItemVO.getUserId());
                rs = pstmtSelectOwner.executeQuery();
                if (rs.next()) {
                    userVO = new C2STransferVO();
                    userVO.setOwnerUserID(rs.getString("owner_id"));
                } else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Owner user not found", "Approval level = " + currentLevel);
                    continue;
                }

                if (!(focBatchItemVO.getUserId().equalsIgnoreCase(userVO.getOwnerUserID())) && isDpAllowed) {
                    isOwnerUserNotSame = true;
                }
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Executed focBatchItemVO=" );
                	loggerValue.append(focBatchItemVO.toString());
                    log.debug(methodName, loggerValue);
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
                rs1 = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs1.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(focBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs1.getString("userstatus"));
                    channelUserVO.setInSuspend(rs1.getString("in_suspend"));
                    // added by nilesh:transfer_profile_id
                    channelUserVO.setTransferProfileID(rs1.getString("transfer_profile_id"));
                    channelUserVO.setCommissionProfileStatus(rs1.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs1.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs1.getString("comprf_lang_2_msg"));
                    
					if (!tcpOn) {
						channelUserVO.setTransferProfileStatus(rs1.getString("profile_status"));
					} else {
						channelUserVO.setTransferProfileStatus(
								tcpMap.get(rs1.getString("transfer_profile_id")).get("profileStatus"));// TCP
					}
                    
                    language = rs1.getString("phone_language");
                    country = rs1.getString("country");
                    channelUserVO.setGeographicalCode(rs1.getString("grph_domain_code"));
                    // (user status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User is suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Commission profile suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + currentLevel);
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User not found", "Approval level" + currentLevel);
                    continue;
                }
                networkStocksVO = new NetworkStockVO();
                networkStocksVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkStocksVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                networkStocksVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());

                // creating the channelTransferVO here since O2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth foc items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                channelTransferVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());
                channelTransferVO.setProductType(p_focBatchMatserVO.getProductCode());
                channelTransferVO.setToUserID(focBatchItemVO.getUserId());
                channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                ChannelTransferBL.genrateTransferID(channelTransferVO);

                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch foc item was created.
                channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());
                // lohit
                channelTransferVO.setTransferType(PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
                networkStocksVO.setLastTxnNum(o2cTransferID);
              
                networkStocksVO.setLastTxnBalance(focBatchItemVO.getRequestedQuantity());
                networkStocksVO.setWalletBalance(focBatchItemVO.getRequestedQuantity());
                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                networkStocksVO.setModifiedBy(p_userID);
                networkStocksVO.setModifiedOn(date);
//                if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
//                	 networkStocksVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);
//
//                } else {
//                	 networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
//                }
                
                
                if(BTSLUtil.isNullString(p_focBatchMatserVO.getWallet_type())) {
                 	 networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                }else {
                	networkStocksVO.setWalletType(p_focBatchMatserVO.getWallet_type());
                }
                
                
               
                dailyStockUpdatedOn = null;
                dayDifference = 0;
                // select the record form the network stock table.
                pstmtLoadNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
//                if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
//                    ++m;
//                    pstmtLoadNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
//
//                } else {
//                    ++m;
//                    pstmtLoadNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
//                }
                if(BTSLUtil.isNullString(p_focBatchMatserVO.getWallet_type())) {
	            	++m;
	            	pstmtLoadNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);	
                }else {
	                ++m;
	                pstmtLoadNetworkStock.setString(m, p_focBatchMatserVO.getWallet_type());
                }
                ++m;
                pstmtLoadNetworkStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                rs2 = pstmtLoadNetworkStock.executeQuery();
                while (rs2.next()) {
                    dailyStockUpdatedOn = rs2.getDate("daily_stock_updated_on");

                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, date);

                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the difference
                        // is.
                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Till now daily Stock is not updated on " );
                        	loggerValue.append(date);
                        	loggerValue.append(", day differences = ");
                        	loggerValue.append(dayDifference);
                            log.debug("closeOrderByBatchForDirectPayout ", loggerValue);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            
//                            if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
//                                ++m;
//                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
//                            } else {
//                                ++m;
//                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);
//                            }
                            
                            if(BTSLUtil.isNullString(p_focBatchMatserVO.getWallet_type())) {
            	            	++m;
            	            	pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);	
                            }else {
            	                ++m;
            	                pstmtInsertNetworkDailyStock.setString(m, p_focBatchMatserVO.getWallet_type());
                            }
                            
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs2.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs2.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs2.getString("product_code"));
                           
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_created"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_returned"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("wallet_sold"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("last_txn_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs2.getLong("previous_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);

                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
							
							
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                    "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while insert in network daily stock table",
                                    "Approval level = " + currentLevel + "updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Termination of the procissing",
                                "Approval level = " + currentLevel);
                            break;
                        }
                        // Update the network stock table
                        pstmtUpdateNetworkStock.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                        
                        if(BTSLUtil.isNullString(p_focBatchMatserVO.getWallet_type())) {
        	            	++m;
        	            	pstmtUpdateNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);	
                        }else {
        	                ++m;
        	                pstmtUpdateNetworkStock.setString(m, p_focBatchMatserVO.getWallet_type());
                        }
                        
//                        if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
//                            ++m;
//                            pstmtUpdateNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
//                        } else {
//                            ++m;
//                            pstmtUpdateNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
//                        }
                        updateCount = pstmtUpdateNetworkStock.executeUpdate();
                        // (record not updated properly in db) if this condition
                        // is true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network daily stock table",
                                "Approval level = " + currentLevel + "updateCount = " + updateCount);
                            continue;
                        }
                    }
                }// end of if () for daily network stock updation
                pstmtSelectNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                
                
                if(BTSLUtil.isNullString(p_focBatchMatserVO.getWallet_type())) {
	            	++m;
	            	pstmtSelectNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);	
                }else {
	                ++m;
	                pstmtSelectNetworkStock.setString(m, p_focBatchMatserVO.getWallet_type());
                }
                
                
//                if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
//                    ++m;
//                    pstmtSelectNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
//                } else {
//                    ++m;
//                    pstmtSelectNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
//                }
                rs3 = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockSold = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs3.next()) {
                    stock = rs3.getLong("wallet_balance");
                    stockSold = rs3.getLong("wallet_sold");
                    previousNwStockToBeSetChnlTrfItems = stock;
                }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(PretupsRestUtil.getMessageString("label.all"), String.valueOf(focBatchItemVO.getRecordNumber()) + " - " + PretupsRestUtil.getMessageString( "label.all"),PretupsRestUtil.getMessageString( "batchdirectpayout.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", "Approval level = " + currentLevel);
                    throw new BTSLBaseException(this, methodName, "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", "Approval level = " + currentLevel);
                    continue;
                }
                if (stock != -1) {
                    stock -= networkStocksVO.getWalletbalance();
                }
                if (stockSold != -1) {
                    stockSold += networkStocksVO.getWalletbalance();
                }
                m = 0;
                // Deebit the network stock
                pstmtupdateSelectedNetworkStock.clearParameters();
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stock);
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stockSold);
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnNum());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnType());
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, networkStocksVO.getLastTxnBalance());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getModifiedBy());
                ++m;
                pstmtupdateSelectedNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStocksVO.getModifiedOn()));
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                
                if(BTSLUtil.isNullString(p_focBatchMatserVO.getWallet_type())) {
	            	++m;
	            	pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);	
                }else {
	                ++m;
	                pstmtupdateSelectedNetworkStock.setString(m, p_focBatchMatserVO.getWallet_type());
                }
//                if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
//                    ++m;
//                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
//                } else {
//                    ++m;
//                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
//                }
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
             // AutoNetworkStockCreation logic
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                	new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);
                }
                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setNetworkCode(networkStocksVO.getNetworkCode());
                networkStockTxnVO.setNetworkFor(networkStocksVO.getNetworkCodeFor());
                if (networkStocksVO.getNetworkCode().equals(p_focBatchMatserVO.getNetworkCodeFor())) {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                } else {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                }
                // As discussed with sandeep in channel transfer table's
                // reference number field we have
                // to insert batch details id.So In network stock where channel
                // transfer table's reference number
                // was inserted, I insert batch detail id.
                networkStockTxnVO.setReferenceNo(focBatchItemVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(focBatchItemVO.getInitiatedOn());
                networkStockTxnVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setInitiaterRemarks(focBatchItemVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(focBatchItemVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(focBatchItemVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(focBatchItemVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(focBatchItemVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(p_userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(p_userID);
                networkStockTxnVO.setTxnStatus(focBatchItemVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
                networkStockTxnVO.setTxnType(PretupsI.DEBIT);
                networkStockTxnVO.setInitiatedBy(p_userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(focBatchItemVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(focBatchItemVO.getTransferMrp());
                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                networkItemsVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setMrp(focBatchItemVO.getTransferMrp());
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setAmount(0);
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
                // Added on 07/02/08
                networkItemsVO.setDateTime(p_focBatchMatserVO.getBatchDate());
                m = 0;
                pstmtInsertNetworkStockTransaction.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkCode());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkFor());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getStockType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getReferenceNo());
                if (networkStockTxnVO.getTxnDate() != null) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getTxnDate()));
                } else {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, null);
                }
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getRequestedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiaterRemarks());
                // for multilanguage support
              
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedRemarks());
                // for multilanguage support
                
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedRemarks());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCancelledBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCancelledOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCreatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCreatedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getModifiedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getModifiedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnStatus());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getEntryType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getFirstApproverLimit());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getUserID());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getTxnMrp());
                if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                } else {
                	++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.SALE_WALLET_TYPE);
        		}
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, channelTransferVO.getTransferID());

                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount);  // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertNetworkStockTransactionItem.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransactionItem.setInt(m, networkItemsVO.getSNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getProductCode());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getRequiredQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getStock());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getMrp());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getAmount());
                // Date 07/02/08
                ++m;
                pstmtInsertNetworkStockTransactionItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                updateCount = pstmtInsertNetworkStockTransactionItem.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                    continue;
                }
                // insert for parent
                if (isOwnerUserNotSame) {
                    terminateProcessing = false;
                    long newBalance = 0;
                    updateCount = 0;

                    pstmtSelectBalance.clearParameters();
                    pstmtSelectBalance.setString(1, userVO.getOwnerUserID());
                    pstmtSelectBalance.setString(2, channelTransferVO.getProductType());
                    pstmtSelectBalance.setString(3, channelTransferVO.getNetworkCode());
                    pstmtSelectBalance.setString(4, channelTransferVO.getNetworkCodeFor());
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                        pstmtSelectBalance.setString(5, PretupsI.WALLET_TYPE_BONUS);
                    } else {
                        pstmtSelectBalance.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                    }
                    rs4 = pstmtSelectBalance.executeQuery();

                    // if parent's entry already exists
                    if (rs4.next()) {
                        balance = rs4.getLong("balance");
                    } else
                    // if parent's entry does not exist
                    {
                        pstmtInsertBalance.clearParameters();
                        balance = 0;
                        m = 0;
                        balance = focBatchItemVO.getRequestedQuantity();
                        ++m;
                        pstmtInsertBalance.setLong(m, 0);
                        ++m;
                        pstmtInsertBalance.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtInsertBalance.setLong(m, balance);
                        ++m;
                        pstmtInsertBalance.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                        ++m;
                        pstmtInsertBalance.setString(m, o2cTransferID);
                        ++m;
                        pstmtInsertBalance.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                            ++m;
                            pstmtInsertBalance.setString(m, PretupsI.WALLET_TYPE_BONUS);
                        } else {
                            ++m;
                            pstmtInsertBalance.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                        }
                        ++m;
                        pstmtInsertBalance.setString(m, userVO.getOwnerUserID());
                        ++m;
                        pstmtInsertBalance.setString(m, p_focBatchMatserVO.getProductCode());
                        ++m;
                        pstmtInsertBalance.setString(m, p_focBatchMatserVO.getNetworkCode());
                        ++m;
                        pstmtInsertBalance.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                        insertCount = pstmtInsertBalance.executeUpdate();

                        if (insertCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting user balances table",
                                "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                            terminateProcessing = true;
                            break;
                        }
                        if (terminateProcessing) {
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + currentLevel);
                            continue;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "After inserting new user balances information");
                        }
                        pstmtUpdateBalance.clearParameters();
                    }
                    // update if parent's entry already exists
                    if (insertCount == 0) {
                        newBalance = balance + focBatchItemVO.getRequestedQuantity(); // credit
                        // parent's
                        // account
                        pstmtUpdateBalance.clearParameters();
                        pstmtUpdateBalance.setLong(1, newBalance);
                        pstmtUpdateBalance.setString(2, channelTransferVO.getTransferType());
                        pstmtUpdateBalance.setString(3, channelTransferVO.getTransferID());
                        pstmtUpdateBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                            pstmtUpdateBalance.setString(5, PretupsI.WALLET_TYPE_BONUS);
                        } else {
                            pstmtUpdateBalance.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                        }
                        pstmtUpdateBalance.setString(6, userVO.getOwnerUserID());
                        pstmtUpdateBalance.setString(7, channelTransferVO.getProductType());
                        pstmtUpdateBalance.setString(8, channelTransferVO.getNetworkCode());
                        pstmtUpdateBalance.setString(9, channelTransferVO.getNetworkCodeFor());

                        updateCount = pstmtUpdateBalance.executeUpdate();
                        if (updateCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating user balances table",
                                "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                            terminateProcessing = true;
                            break;
                        }
                    }
                    if (insertCount <= 0) {
                        balance = newBalance;
                        newBalance = balance - focBatchItemVO.getRequestedQuantity();
                    } else {
                        balance = newBalance;
                    }

                    if (balance < focBatchItemVO.getRequestedQuantity()) {
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "",
                            "Owner current balance is less than required balance for requested Adjustment Dr Amt.");
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "Owner Current Bal:" + balance + "And required Dr Amt : " + channelTransferVO.getPayableAmount());
                        }
                    }
                    if (newBalance < 0) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.userbalance"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog("closeOrderByBatchForDirectPayout", p_focBatchMatserVO, focBatchItemVO,
                            "FAIL : DB Error while updating user balances table", "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                        terminateProcessing = true;
                        break;

                    }
                    pstmtUpdateBalance.clearParameters();
                    pstmtUpdateBalance.setLong(1, newBalance); // debit parent's
                    // account
                    pstmtUpdateBalance.setString(2, channelTransferVO.getTransferType());
                    pstmtUpdateBalance.setString(3, channelTransferVO.getTransferID());
                    pstmtUpdateBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                        pstmtUpdateBalance.setString(5, PretupsI.WALLET_TYPE_BONUS);
                    } else {
                        pstmtUpdateBalance.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                    }
                    pstmtUpdateBalance.setString(6, userVO.getOwnerUserID());
                    pstmtUpdateBalance.setString(7, channelTransferVO.getProductType());
                    pstmtUpdateBalance.setString(8, channelTransferVO.getNetworkCode());
                    pstmtUpdateBalance.setString(9, channelTransferVO.getNetworkCodeFor());

                    updateCount = pstmtUpdateBalance.executeUpdate();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating user balances table",
                            "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                        terminateProcessing = true;
                        break;
                    }

                    if (terminateProcessing) {
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                            "FAIL : Terminting the procssing of this user as error while updating user balance", "Approval level = " + currentLevel);
                        continue;
                    }
                }

                dailyBalanceUpdatedOn = null;
                dayDifference = 0;
                // select the record form the userBalances table.
                pstmtSelectUserBalances.clearParameters();
                m = 0;
                ++m;
                pstmtSelectUserBalances.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectUserBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    ++m;
                    pstmtSelectUserBalances.setString(m, PretupsI.WALLET_TYPE_BONUS);
                } else {
                    ++m;
                    pstmtSelectUserBalances.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                }
                rs5 = pstmtSelectUserBalances.executeQuery();
                while (rs5.next()) {
                    dailyBalanceUpdatedOn = rs5.getDate("daily_balance_updated_on");
                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, date);
                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the
                        if (log.isDebugEnabled()) {
                            log.debug("closeOrderByBatchForDirectPayout ", "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertUserDailyBalances.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertUserDailyBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("user_id"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("network_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("network_code_for"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs5.getString("product_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs5.getLong("balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs5.getLong("prev_balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                                ++m;
                                pstmtInsertUserDailyBalances.setString(m, PretupsI.WALLET_TYPE_BONUS);
                            } else {
                                ++m;
                                pstmtInsertUserDailyBalances.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                            }
                            updateCount = pstmtInsertUserDailyBalances.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                    "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting user daily balances table",
                                    "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + currentLevel);
                            continue;
                        }
                        // Update the user balances table
                        pstmtUpdateUserBalances.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateUserBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateUserBalances.setString(m, channelUserVO.getUserID());
                        updateCount = pstmtUpdateUserBalances.executeUpdate();
                        // (record not updated properly) if this condition is
                        // true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : DB Error while updating user balances table for daily balance", "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
                maxBalance = 0;
                isNotToExecuteQuery = false;
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    ++m;
                    pstmtSelectBalance.setString(m, PretupsI.WALLET_TYPE_BONUS);
                } else {
                    ++m;
                    pstmtSelectBalance.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                }
                rs6 = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                if (rs6.next()) {
                    balance = rs6.getLong("balance");
                    balanceExist = true;
                }
                if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    balance += focBatchItemVO.getRequestedQuantity();
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                rs7 = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs7.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(p_focBatchMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs7.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs7.getLong("max_balance"));
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + currentLevel);
                    continue;
                }
                maxBalance = transferProfileProductVO.getMaxBalanceAsLong();
                // (max balance reach for the receiver) if this condition is
                // true then made entry in logs and leave this data.
                if (maxBalance < balance) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the order
                // value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (balance == -1 && maxBalance < focBatchItemVO.getRequestedQuantity()) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + currentLevel);
                    continue;
                }
                if (!isNotToExecuteQuery) {
                    m = 0;
                    // update
                    if (balanceExist && balance > -1) {
                        pstmtUpdateBalance.clearParameters();
                        handlerStmt = pstmtUpdateBalance; // update if balance
                        // entry already
                        // exists for child
                        // user
                    } else if (balanceExist && balance <= -1) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.balance.notallowed"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Current balance is less than requested quantity.",
                            "Approval level = " + currentLevel);
                        continue;

                    } else if (!balanceExist && balance + focBatchItemVO.getRequestedQuantity() <= -1) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.balance.notallowed"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Current balance is less than requested quantity.",
                            "Approval level = " + currentLevel);
                        continue;

                    } else if (!balanceExist && balance + focBatchItemVO.getRequestedQuantity() > -1) {
                        // insert for child user balance
                        pstmtInsertBalance.clearParameters();
                        handlerStmt = pstmtInsertBalance; // insert if balance
                        // entry does not
                        // exist for child
                        // user
                        balance = focBatchItemVO.getRequestedQuantity();
                        ++m;
                        handlerStmt.setLong(m, 0);// previous balance
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                        // on
                        // date
                    }
                    ++m;
                    handlerStmt.setLong(m, balance);
                    ++m;
                    handlerStmt.setString(m, PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                        ++m;
                        handlerStmt.setString(m, PretupsI.WALLET_TYPE_BONUS);
                    } else {
                        ++m;
                        handlerStmt.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                    }
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + currentLevel);
                        continue;
                    }

                    transferProfileProductVO = TransferProfileProductCache
                        .getTransferProfileDetails(channelUserVO.getTransferProfileID(), p_focBatchMatserVO.getProductCode());
                    thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                    String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                    final String remark = null;
                    if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        thresholdValue = transferProfileProductVO.getAltBalanceLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                    }
                    // end
                    // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_focBatchMatserVO.getNetworkCode(),
                    // focBatchItemVO.getCategoryCode()); //threshold value
                    // for zero balance counter..
                    try {
                        m = 0;
                        final boolean isUserThresholdEntryReq = false;
                        final String thresholdType = null;
                        // 24dec addded by nilesh:if previous bal is below
                        // threshold and current bal is above threshold,
                        // then entry in user_threshold_counter.Also,if previous
                        // bal is already below threshold and current bal is
                        // also below threshold
                        // then also entry in user_threshold_counter
                        // table(Discussed with Ved Sir and Protim Sir)
                     
                        if ((previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance >= thresholdValue) || (previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance <= thresholdValue)) {
                            if (log.isDebugEnabled()) {
                                log.debug(methodName, "Entry in threshold counter" + thresholdValue + ", prvbal: " + previousUserBalToBeSetChnlTrfItems + "nbal" + balance);
                            }
                            psmtInsertUserThreshold.clearParameters();
                            m = 0;
                            ++m;
                            psmtInsertUserThreshold.setString(m, channelUserVO.getUserID());
                            ++m;
                            psmtInsertUserThreshold.setString(m, o2cTransferID);
                            ++m;
                            psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getNetworkCode());
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getProductCode());
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);

                            if (balance >= thresholdValue) {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.ABOVE_THRESHOLD_TYPE);
                            } else {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
                            }
                            ++m;
                            psmtInsertUserThreshold.setString(m, focBatchItemVO.getCategoryCode());
                            ++m;
                            psmtInsertUserThreshold.setLong(m, previousUserBalToBeSetChnlTrfItems);
                            ++m;
                            psmtInsertUserThreshold.setLong(m, balance);
                            ++m;
                            psmtInsertUserThreshold.setLong(m, thresholdValue);
                            // added by nilesh
                            ++m;
                            psmtInsertUserThreshold.setString(m, threshold_type);
                            ++m;
                            psmtInsertUserThreshold.setString(m, remark);

                            psmtInsertUserThreshold.executeUpdate();
                        }
                    } catch (SQLException sqle) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("SQLException ");
                    	loggerValue.append(sqle.getMessage());
                        log.error(methodName,  loggerValue);
                        log.errorTrace(methodName, sqle);
                        loggerValue.setLength(0);
                    	loggerValue.append("Error while updating user_threshold_counter table SQL Exception:");
                    	loggerValue.append(sqle.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", o2cTransferID, "", p_focBatchMatserVO.getNetworkCode(),
                            loggerValue.toString());
                    }// end of catch

                }
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                rs8 = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs8.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(focBatchItemVO.getUserId());

                    countsVO.setDailyInCount(rs8.getLong("daily_in_count"));
                    countsVO.setDailyInValue(rs8.getLong("daily_in_value"));
                    countsVO.setWeeklyInCount(rs8.getLong("weekly_in_count"));
                    countsVO.setWeeklyInValue(rs8.getLong("weekly_in_value"));
                    countsVO.setMonthlyInCount(rs8.getLong("monthly_in_count"));
                    countsVO.setMonthlyInValue(rs8.getLong("monthly_in_value"));

                    countsVO.setDailyOutCount(rs8.getLong("daily_out_count"));
                    countsVO.setDailyOutValue(rs8.getLong("daily_out_value"));
                    countsVO.setWeeklyOutCount(rs8.getLong("weekly_out_count"));
                    countsVO.setWeeklyOutValue(rs8.getLong("weekly_out_value"));
                    countsVO.setMonthlyOutCount(rs8.getLong("monthly_out_count"));
                    countsVO.setMonthlyOutValue(rs8.getLong("monthly_out_value"));

                    countsVO.setUnctrlDailyInCount(rs8.getLong("outside_daily_in_count"));
                    countsVO.setUnctrlDailyInValue(rs8.getLong("outside_daily_in_value"));
                    countsVO.setUnctrlWeeklyInCount(rs8.getLong("outside_weekly_in_count"));
                    countsVO.setUnctrlWeeklyInValue(rs8.getLong("outside_weekly_in_value"));
                    countsVO.setUnctrlMonthlyInCount(rs8.getLong("outside_monthly_in_count"));
                    countsVO.setUnctrlMonthlyInValue(rs8.getLong("outside_monthly_in_value"));

                    countsVO.setUnctrlDailyOutCount(rs8.getLong("outside_daily_out_count"));
                    countsVO.setUnctrlDailyOutValue(rs8.getLong("outside_daily_out_value"));
                    countsVO.setUnctrlWeeklyOutCount(rs8.getLong("outside_weekly_out_count"));
                    countsVO.setUnctrlWeeklyOutValue(rs8.getLong("outside_weekly_out_value"));
                    countsVO.setUnctrlMonthlyOutCount(rs8.getLong("outside_monthly_out_count"));
                    countsVO.setUnctrlMonthlyOutValue(rs8.getLong("outside_monthly_out_value"));

                    countsVO.setDailySubscriberOutCount(rs8.getLong("daily_subscriber_out_count"));
                    countsVO.setDailySubscriberOutValue(rs8.getLong("daily_subscriber_out_value"));
                    countsVO.setWeeklySubscriberOutCount(rs8.getLong("weekly_subscriber_out_count"));
                    countsVO.setWeeklySubscriberOutValue(rs8.getLong("weekly_subscriber_out_value"));
                    countsVO.setMonthlySubscriberOutCount(rs8.getLong("monthly_subscriber_out_count"));
                    countsVO.setMonthlySubscriberOutValue(rs8.getLong("monthly_subscriber_out_value"));

                    countsVO.setLastTransferDate(rs8.getDate("last_transfer_date"));
                    countsVO.setLastSOSTxnStatus(rs8.getString("last_sos_txn_status"));
                    countsVO.setLastLrStatus(rs8.getString("last_lr_status"));
                }
                flag = true;
                if (countsVO == null) {
                    flag = false;
                    countsVO = new UserTransferCountsVO();
                }
                // If found then check for reset otherwise no need to check it
                if (flag) {
                    ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO, date);
                }
                pstmtSelectProfileCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectProfileCounts.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                rs9 = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
                if (rs9.next()) {
                    transferProfileVO = new TransferProfileVO();
                    transferProfileVO.setProfileId(rs9.getString("profile_id"));
                    transferProfileVO.setDailyInCount(rs9.getLong("daily_transfer_in_count"));
                    transferProfileVO.setDailyInValue(rs9.getLong("daily_transfer_in_value"));
                    transferProfileVO.setWeeklyInCount(rs9.getLong("weekly_transfer_in_count"));
                    transferProfileVO.setWeeklyInValue(rs9.getLong("weekly_transfer_in_value"));
                    transferProfileVO.setMonthlyInCount(rs9.getLong("monthly_transfer_in_count"));
                    transferProfileVO.setMonthlyInValue(rs9.getLong("monthly_transfer_in_value"));
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile not found", "Approval level = " + currentLevel);
                    continue;
                }
               
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.
                 if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.dailyinvaluereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.weeklyinvaluereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.monthlyinvaluereach"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                countsVO.setUserID(channelUserVO.getUserID());
                countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                countsVO.setDailyInValue(countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setLastInTime(date);
                countsVO.setLastTransferID(o2cTransferID);
                countsVO.setLastTransferDate(date);
                // Update counts if found in db
                if (flag) {
                    m = 0;
                    pstmtUpdateTransferCounts.clearParameters();
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtUpdateTransferCounts.executeUpdate();
                }
                // Insert counts if not found in db
                else {
                    m = 0;
                    pstmtInsertTransferCounts.clearParameters();
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtInsertTransferCounts.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + currentLevel);
                    } else {
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                rs10 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                // check record is modified or not
                if (rs10.next()) {
                    newlastModified = rs10.getTimestamp("modified_on");
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + currentLevel);
                    continue;
                }
                // (external txn number is checked)
                if (SystemPreferences.EXTERNAL_TXN_UNIQUE && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo())) {
                    // check in foc_batch_item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    rs11 = pstmtIsTxnNumExists1.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs11.next()) {
                        con.rollback();
                        try {
                            if (rs11 != null) {
                                rs11.close();
                            }
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in FOC Batch",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                    // check in channel transfer table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs12 = pstmtIsTxnNumExists2.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs12.next()) {
                        con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchdirectpayout.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in CHANNEL TRF",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    focBatchItemVO.setFirstApprovedBy(p_userID);
                    focBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    focBatchItemVO.setSecondApprovedBy(p_userID);
                    focBatchItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
                    psmtAppr3FOCBatchItem.clearParameters();
                    focBatchItemVO.setThirdApprovedBy(p_userID);
                    focBatchItemVO.setThirdApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr3FOCBatchItem.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                channelTransferVO.setCanceledOn(focBatchItemVO.getCancelledOn());
                channelTransferVO.setCanceledBy(focBatchItemVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(focBatchItemVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(focBatchItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(focBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setCreatedBy(focBatchItemVO.getInitiatedBy());
                channelTransferVO.setDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(focBatchItemVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(focBatchItemVO.getExtTxnNo());
                channelTransferVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(focBatchItemVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(focBatchItemVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setBatchNum(focBatchItemVO.getBatchId());
                channelTransferVO.setBatchDate(p_focBatchMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax3(0);
                channelTransferVO.setPayableAmount(0);
                channelTransferVO.setNetPayableAmount(0);
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(p_userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(p_focBatchMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(focBatchItemVO.getCategoryCode());
                channelTransferVO.setReceiverGradeCode(focBatchItemVO.getGradeCode());
                channelTransferVO.setReceiverTxnProfile(focBatchItemVO.getTxnProfile());
                channelTransferVO.setReferenceNum(focBatchItemVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(focBatchItemVO.getStatus());
                channelTransferVO.setThirdApprovedBy(focBatchItemVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(focBatchItemVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(focBatchItemVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                channelTransferVO.setTotalTax1(focBatchItemVO.getTax1Value());
                channelTransferVO.setTotalTax2(focBatchItemVO.getTax2Value());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
                channelTransferVO.setTransferDate(focBatchItemVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(focBatchItemVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(focBatchItemVO.getTransferMrp());
                // added for logger
                channelTransferVO.setControlTransfer(PretupsI.YES);
                channelTransferVO.setToUserCode(focBatchItemVO.getMsisdn());
                channelTransferVO.setReceiverDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setReceiverGgraphicalDomainCode(channelTransferVO.getGraphicalDomainCode());
                channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                channelTransferVO.setActiveUserId(p_focBatchMatserVO.getCreatedBy());
                // end
                channelTransferItemVO = new ChannelTransferItemsVO();
                channelTransferItemVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setCommProfileDetailID(focBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(focBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(focBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(focBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(focBatchItemVO.getTransferMrp());
                channelTransferItemVO.setProductCode(p_focBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                channelTransferItemVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(focBatchItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(focBatchItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(focBatchItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(focBatchItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(focBatchItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(focBatchItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(focBatchItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(focBatchItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(focBatchItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(p_focBatchMatserVO.getProductMrp());
                channelTransferItemVO.setSenderDebitQty(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setReceiverCreditQty(focBatchItemVO.getRequestedQuantity());
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(p_focBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_DP_CODE);
                /* SOS */
                Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(con, countsVO, channelTransferVO);
                if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true))
                {
                	con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.sosPending"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : SOS Status PENDING FOR USER",
                        "Approval level = " + currentLevel);
                    continue;
                }
                
                else if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
    			}
                Map<String, Object> lrHashMap = ChannelTransferBL.checkLRstatusAndAmount(con, countsVO, channelTransferVO);
    			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
    			}
                if (log.isDebugEnabled()) {
                    log.debug("closeOrderByBatch", "Exiting: channelTransferVO=" + channelTransferVO.toString());
                }
                if (log.isDebugEnabled()) {
                    log.debug("closeOrderByBatch", "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
                    /*
                     * //The query below is used to insert the record in channel
                     * transfers table for the order that is cloaed
                     */
                }

                m = 0;
                pstmtInsertIntoChannelTranfers.clearParameters();
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCanceledBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
                // commented for
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getChannelRemarks());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileSetId());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileVersion());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCreatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getExternalTxnNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getFirstApproverLimit());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setDate(m, BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getBatchNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFromUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getModifiedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCodeFor());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayInstrumentAmt());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getProductType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverGradeCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverTxnProfile());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReferenceNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayType());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getRequestedQuantity());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getSecondApprovalLimit());
             
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSource());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getStatus());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
             
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getToUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax1());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax2());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax3());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferCategory());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferInitatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTransferMRP());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferSubType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, PretupsI.YES);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getMsisdn());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());

                // By sandeep ID TOG001
                // to geographical domain also inserted as the geogrpahical
                // domain that will help in reports
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());

                // commented for
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDefaultLang());
                // commented for
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondLang());
                // added fo bonus type
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getBonusType());
                if (isOwnerUserNotSame) {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, focBatchItemVO.getRequestedQuantity());
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, focBatchItemVO.getRequestedQuantity());
                } else {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, 0);
                    ++m;
                    pstmtInsertIntoChannelTranfers.setLong(m, 0);
                }
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, p_focBatchMatserVO.getCreatedBy());
                if (SystemPreferences.MULTIPLE_WALLET_APPLY) {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setString(m, PretupsI.INCENTIVE_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getDualCommissionType());// ends here
                 // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertIntoChannelTransferItems.clearParameters();
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommProfileDetailID());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getCommRate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommType());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getProductTotalMRP());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getProductCode());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getRequiredQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setInt(m, channelTransferItemVO.getSerialNum());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax1Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax1Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax1Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax2Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax2Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax2Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax3Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax3Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax3Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, o2cTransferID);
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getUnitValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock() - channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity());

                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    DirectPayOutErrorLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer items table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                con.commit();
                DirectPayOutSuccessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                    "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                rs13 = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList = new ArrayList();
                while (rs13.next()) {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs13.getString("product_code"));
                    balancesVO.setBalance(rs13.getLong("balance"));
                    balancesVO.setProductShortCode(rs13.getString("product_short_code"));
                    balancesVO.setProductShortName(rs13.getString("short_name"));
                    userbalanceList.add(balancesVO);
                }
                // generate the message arguments to be send in SMS
                keyArgumentVO = new KeyArgumentVO();
                argsArr = new String[2];
                argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
                argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
                keyArgumentVO.setKey(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS2);
                keyArgumentVO.setArguments(argsArr);
                txnSmsMessageList = new ArrayList();
                balSmsMessageList = new ArrayList();
                txnSmsMessageList.add(keyArgumentVO);
                for (int index = 0, n = userbalanceList.size(); index < n; index++) {
                    balancesVO = (UserBalancesVO) userbalanceList.get(index);
                    if (balancesVO.getProductCode().equals(channelTransferItemVO.getProductCode())) {
                        argsArr = new String[2];
                        argsArr[1] = balancesVO.getBalanceAsString();
                        argsArr[0] = balancesVO.getProductShortName();
                        keyArgumentVO = new KeyArgumentVO();
                        keyArgumentVO.setKey(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String dpNotifyMsg = null;
                if (SystemPreferences.DP_SMS_NOTIFY) {
                    final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        dpNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        dpNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList), dpNotifyMsg };
                }

                if (dpNotifyMsg == null) {
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList) };
                }

                messages = new BTSLMessages(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS1, array);
                pushMessage = new PushMessage(focBatchItemVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
                pushMessage.push();
                OneLineTXNLog.log(channelTransferVO, focBatchItemVO);
            }// end of while
        }catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
        	loggerValue.append("sqlException  ");
        	loggerValue.append(sqe);
            log.error("closeOrderByBatch", loggerValue );
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", sqlException + sqe.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, sqlException + sqe.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error("closeOrderByBatch", exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", exception + ex.getMessage());
            DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, exception + ex.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectOwner != null) {
                	pstmtSelectOwner.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadNetworkStock != null) {
                    pstmtLoadNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateNetworkStock != null) {
                    pstmtUpdateNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkDailyStock != null) {
                    pstmtInsertNetworkDailyStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectNetworkStock != null) {
                    pstmtSelectNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateSelectedNetworkStock != null) {
                    pstmtupdateSelectedNetworkStock.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransaction != null) {
                    pstmtInsertNetworkStockTransaction.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransactionItem != null) {
                    pstmtInsertNetworkStockTransactionItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserBalances != null) {
                    pstmtSelectUserBalances.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalances != null) {
                    pstmtUpdateUserBalances.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserDailyBalances != null) {
                    pstmtInsertUserDailyBalances.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalance != null) {
                    pstmtSelectBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBalance != null) {
                    pstmtUpdateBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBalance != null) {
                    pstmtInsertBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTransferCounts != null) {
                    pstmtSelectTransferCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectProfileCounts != null) {
                    pstmtSelectProfileCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateTransferCounts != null) {
                    pstmtUpdateTransferCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertTransferCounts != null) {
                    pstmtInsertTransferCounts.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr3FOCBatchItem != null) {
                    psmtAppr3FOCBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadTransferProfileProduct != null) {
                    pstmtLoadTransferProfileProduct.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (handlerStmt != null) {
                    handlerStmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTransferItems != null) {
                    pstmtInsertIntoChannelTransferItems.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTranfers != null) {
                    pstmtInsertIntoChannelTranfers.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalanceInfoForMessage != null) {
                    pstmtSelectBalanceInfoForMessage.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs4 != null) {
                    rs4.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs5 != null) {
                    rs5.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs6 != null) {
                    rs6.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs7 != null) {
                    rs7.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs8 != null) {
                    rs8.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs9 != null) {
                    rs9.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs10 != null) {
                    rs10.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs11 != null) {
                    rs11.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs12 != null) {
                    rs12.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rs13 != null) {
                    rs13.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs.close();
                rs = null;
                rs = pstmtSelectItemsDetails.executeQuery();
                // Check the final status to be updated in master after
                // processing all records of batch
                if (rs.next()) {
                    final int totalCount = rs.getInt("batch_total_record");
                    final int closeCount = rs.getInt("closed");
                    final int cnclCount = rs.getInt("cncl");
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "",PretupsRestUtil.getMessageString( "batchdirectpayout.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                con.commit();
            } catch (SQLException sqe) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error("closeOrderByBatch", sqlException + sqe);
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", sqlException + sqe.getMessage());
                DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, sqlException + sqe.getMessage(), "Approval level = " + currentLevel);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
            	loggerValue.append("exception ");
            	loggerValue.append(ex);
                log.error("closeOrderByBatch", loggerValue);
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]", "", "", "", exception + ex.getMessage());
                DirectPayOutErrorLog.dpBatchMasterLog(methodName, p_focBatchMatserVO, exception + ex.getMessage(), "Approval level = " + currentLevel);
                throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
                
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
            }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList size=");
            	loggerValue.append(errorList.size());
                log.debug(methodName,  loggerValue);
            }
        }
        return errorList;
    
    	
    }
    
    
    
    /*
     * /**
     * Method getComissionWalletType
     * Method for loading Direct Payout Batch details..
     * This method will load the batches that are within the geography of user
     * whose userId is passed
     * with status(OPEN) also in items table for corresponding master record the
     * status is in p_itemStatus
     * 
     * @author Lohit Audhkhasi
     * 
     * @method loadBatchDPMasterDetails
     * 
     * @param p_con java.sql.Connection
     * 
     * @param p_userID String
     * 
     * @param p_itemStatus String
     * 
     * @param p_currentLevel String
     * 
     * @return java.util.ArrayList
     * 
     * @throws BTSLBaseException
     */
    public ArrayList getComissionWalletType(Connection con, String batchID) throws BTSLBaseException {
        final String methodName = "getComissionWalletType";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  batchID=" +  batchID);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
      
        final String sqlSelect = " select batch_id,batch_name,txn_wallet,type  FROM foc_batches fb  where fb.batch_id =?  ";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            
            pstmt.setString(1, batchID);
            
            rs = pstmt.executeQuery();
            FOCBatchMasterVO focBatchMasterVO = null;
            while (rs.next()) {
                focBatchMasterVO = new FOCBatchMasterVO();
                focBatchMasterVO.setBatchId(rs.getString("batch_id"));
                focBatchMasterVO.setBatchName(rs.getString("batch_name"));
                focBatchMasterVO.setWallet_type(rs.getString("txn_wallet"));
                focBatchMasterVO.setFocOrCommPayout(rs.getString("type"));
                list.add(focBatchMasterVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[getComissionWalletType]", "",
                "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error(methodName, exception + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[getComissionWalletType]", "",
                "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: focBatchMasterVOList size=" + list.size());
            }
        }
        return list;
    }
    
    
    
    
 //method ends here   
}
