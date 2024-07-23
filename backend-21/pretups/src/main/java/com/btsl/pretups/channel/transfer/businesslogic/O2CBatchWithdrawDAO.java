/**
 * @(#)O2CBatchWithdrawDAO.java
 *                              Copyright(c) 2011, Comviva Technologies Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Chhaya 01-NOV-2011 Initial Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 * 
 *                              This class is used for level 1 , level 2 and
 *                              level 3 approval of initiated O2C order by
 *                              batch.
 * 
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchO2CFileProcessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;

public class O2CBatchWithdrawDAO {

    /**
     * Field _log.
     */
    private static final Log LOG = LogFactory.getLog(O2CBatchWithdrawDAO.class.getName());
    public static OperatorUtilI operatorUtili = null;
    private O2CBatchWithdrawQry o2cBatchWithdrawQry = (O2CBatchWithdrawQry) ObjectProducer.getObject(QueryConstants.O2C_BATCH_WITHRAW_QRY, QueryConstants.QUERY_PRODUCER);
    static {

        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * Method initiateBatchO2CTransfer
     * This method used for the batch foc order initiation. The main purpose of
     * this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     * 
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

    public ArrayList initiateBatchO2CTransfer(Connection con, FOCBatchMasterVO batchMasterVO, ArrayList batchItemsList, MessageResources messages, Locale locale) throws BTSLBaseException {
        final String methodName = "initiateBatchO2CTransfer";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffSelectExtTxnID2 Query =" + strBuffSelectExtTxnID2);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type,type,txn_wallet) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        // DB2
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffInsertBatchItems Query =" + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE foc_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = con.prepareStatement(strBuffSelectExtTxnID2.toString());
            // for DB2
            pstmtInsertBatchMaster =  con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = con.prepareStatement(strBuffInsertBatchGeo.toString());
            // for DB2
            pstmtInsertBatchItems =  con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = con.prepareStatement(strBuffUpdateBatchMaster.toString());

            int index = 0;
            O2CBatchItemsVO batchItemsVO = null;

            // insert the master data
            index = 0;
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getNetworkCodeFor());

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
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getDefaultLang());
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getSecondLang());
            ++index;
            // added by praveen
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
            ++index;
            pstmtInsertBatchMaster.setString(index, "OB");
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getWallet_type());
            ++index;

            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                con.rollback();
                LOG.error(methodName, "Unable to insert in the batch master table.");
                BatchO2CFileProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]",
                    "", "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
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
                    LOG.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchO2CFileProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            boolean externalTxnUnique = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
            // ends here
            final String msgArr[] = null;
            for (int i = 0, j = batchItemsList.size(); i < j; i++) {
                batchItemsVO = (O2CBatchItemsVO) batchItemsList.get(i);
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && externalTxnUnique) {
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
                        errorVO.setOtherInfo2(messages.getMessage(locale, "batcho2c.initiatebatchO2Ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist O2C BATCCH", "");
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
                            "batcho2c.initiatebatchO2Ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // insert items data here
                index = 0;
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchDetailId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelUserVO().getCategoryCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getMsisdn());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelUserVO().getUserID());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getStatus());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getModifiedBy());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelUserVO().getUserGrade());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExtTxnNo());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getExtTxnDate()));
                ++index;
                pstmtInsertBatchItems.setDate(index, BTSLUtil.getSQLDateFromUtilDate(batchItemsVO.getTransferDate()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelUserVO().getTransferProfileID());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelUserVO().getCommissionProfileSetID());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelUserVO().getCommissionProfileSetVersion());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelTransferItemsVO().getCommProfileDetailID());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelTransferItemsVO().getCommType());
                ++index;
                pstmtInsertBatchItems.setDouble(index, batchItemsVO.getChannelTransferItemsVO().getCommRate());
                ++index;
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getCommValue());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelTransferItemsVO().getTax1Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, batchItemsVO.getChannelTransferItemsVO().getTax1Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getTax1Value());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelTransferItemsVO().getTax2Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, batchItemsVO.getChannelTransferItemsVO().getTax2Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getTax2Value());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getChannelTransferItemsVO().getTax3Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, batchItemsVO.getChannelTransferItemsVO().getTax3Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getTax3Value());
                ++index;
                pstmtInsertBatchItems.setString(index, String.valueOf(batchItemsVO.getChannelTransferItemsVO().getRequiredQuantity()));
                ++index;
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getProductTotalMRP());
                // for DB2
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
                ++index;
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    // put error record can not be inserted
                    LOG.error(methodName, "Record cannot be inserted in batch items table");
                    BatchO2CFileProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchO2CFileProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }
            // for loop for the batch items
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(),
                "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", "Exception:" + ex.getMessage());
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : Exception:" + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsSelectExtTxnID1 != null) {
                    rsSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID1 != null) {
                    pstmtSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsSelectExtTxnID2 != null) {
                    rsSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID2 != null) {
                    pstmtSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
  
            try {
                if (pstmtInsertBatchMaster != null) {
                    pstmtInsertBatchMaster.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchGeo != null) {
                    pstmtInsertBatchGeo.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchItems != null) {
                    pstmtInsertBatchItems.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {

                // if all records contains errors then rollback the master table
                // entry
                if (errorList != null && (errorList.size() == batchItemsList.size())) {
                    con.rollback();
                    LOG.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        LOG.error(methodName, "Unable to Update the batch size in master table..");
                        con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batchMasterVO.getBatchId());
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
                    LOG.errorTrace(methodName, ex);
                }
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBatchMaster != null) {
                    pstmtUpdateBatchMaster.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
            }
        }
        return errorList;
    }
   
    /**
     * Method o2cWithdrawUserValidate.
     * This method is to validate the channel user for Batch O2C withdraw
     * 
     * @param con
     * @param channelUserVO
     * @param currDate
     * @param lang
     * @throws BTSLBaseException
     */
    public void validateBatchO2CWithdrawUser(Connection con, ChannelUserVO channelUserVO, Date currDate, String lang) throws BTSLBaseException {

        final String methodName = "validateBatchO2CWithdrawUser";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_channelUserVO =" + channelUserVO + "p_currDate=" + currDate);
        }

        // checks whether the commission profile associated with the user is
        // active
        if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCommissionProfileStatus())) {
            throw new BTSLBaseException("O2CBatchWithdrawDAO", methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED);
        }

        if (PretupsI.LOCALE_LANGAUGE_EN.equals(lang)) {
            channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang1Msg());
        } else {
            channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang2Msg());
        }

        // checks if the transfer profile associated with the user is active or
        // not
        if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getTransferProfileStatus())) {
            throw new BTSLBaseException("O2CBatchWithdrawDAO", methodName, PretupsErrorCodesI.ERROR_TRANSFER_PROFILE_SUSPENDED);
        }// end of if-else-if

        final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();

        // the call to the method loads the transfer rule between the Operator
        // and the passed category code for the domain and network.
        final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
            PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

        if (channelTransferRuleVO == null) {
            throw new BTSLBaseException("O2CBatchWithdrawDAO", methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE);
        }
        // checks if the withdraw is allowed or not
        else if (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getWithdrawAllowed())) {
            throw new BTSLBaseException("O2CBatchWithdrawDAO", methodName, PretupsErrorCodesI.ERROR_USER_WITHDRAW_NOT_ALLOWED);
        }// end of second if-else-if
        ArrayList productList = null;
        // load the product list associated with the transfer rule
        productList = channelTransferRuleVO.getProductVOList();

        if (productList == null || productList.isEmpty()) {
            throw new BTSLBaseException("O2CBatchWithdrawDAO", methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_PRODUCT_NOT_ASSOCIATED);
        }// end of if

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting.....");
        }

    }

    /**
     * //Load the User Detail based on the MSISDN or LoginId
     * 
     * @param batchItemsList
     * @param userVO
     * @param con
     */
    public void loadAndValidateBatchO2CWithdrawUser(HttpServletRequest request, ArrayList batchItemsList, UserVO userVO, Connection con, MessageResources messages, Locale locale) throws BTSLBaseException {
        final ChannelUserDAO _channelUserDAO = new ChannelUserDAO();
        ChannelUserVO channelUserVO = null;
        final List<O2CBatchItemsVO> batchO2CList = new ArrayList<O2CBatchItemsVO>();
        O2CBatchItemsVO batchItemsVO = null;
        ListValueVO errorVO = null;
        boolean isValidationError = false;
        final ArrayList fileErrorList = new ArrayList();

        for (int i = 0, j = batchItemsList.size(); i < j; i++) {

            batchItemsVO = (O2CBatchItemsVO) batchItemsList.get(i);

            if (!BTSLUtil.isNullString(batchItemsVO.getMsisdn())) {
                channelUserVO = _channelUserDAO.loadChannelUserDetails(con, batchItemsVO.getMsisdn());
            } else if (!BTSLUtil.isNullString(batchItemsVO.getLoginId())) {
                channelUserVO = _channelUserDAO.loadChnlUserDetailsByLoginID(con, batchItemsVO.getLoginId());
            }

            if (channelUserVO != null) {
                if (!userVO.getNetworkID().equalsIgnoreCase(channelUserVO.getNetworkID())) {

                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batchO2C.processuploadedfile.error.usernotexist"));
                    fileErrorList.add(errorVO);
                    isValidationError = true;
                    continue;
                }// checks whether the commission profile associated with the
                 // user is active with reason
                else if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCommissionProfileStatus())) {
                   

                    errorVO = new ListValueVO();
                    errorVO.setCodeName(batchItemsVO.getMsisdn());
                    errorVO.setOtherInfo(String.valueOf(batchItemsVO.getRecordNumber()));
                    // ChangeID=LOCALEMASTER
                    // which language message to be set is determined from the
                    // locale master table for the requested locale
                    if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(locale)).getMessage())) {
                        errorVO.setOtherInfo2(messages.getMessage(locale, "batchfoc.processuploadedfile.error.comprfinactive", channelUserVO
                            .getCommissionProfileLang1Msg()));
                    } else {
                        errorVO.setOtherInfo2(messages.getMessage(locale, "batchfoc.processuploadedfile.error.comprfinactive", channelUserVO
                            .getCommissionProfileLang2Msg()));
                    }
                    fileErrorList.add(errorVO);
                    isValidationError = true;
                    continue;

                }// checks if the transfer profile associated with the user is
                 // active or not
                else if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getTransferProfileStatus())) {

                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.processuploadedfile.error.trfprfsuspended"));
                    fileErrorList.add(errorVO);
                    isValidationError = true;
                    continue;

                } else if (!PretupsI.NO.equals(channelUserVO.getInSuspend())) {
                    // put error user is in suspended
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.processuploadedfile.error.userinsuspend"));
                    fileErrorList.add(errorVO);
                    isValidationError = true;
                    continue;

                }
                // end of if-else-if
                else {

                    if (PretupsI.LOCALE_LANGAUGE_EN.equals(locale.getLanguage())) {
                        channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang1Msg());
                    } else {
                        channelUserVO.setCommissionProfileSuspendMsg(channelUserVO.getCommissionProfileLang2Msg());
                    }

                    batchItemsVO.setChannelUserVO(channelUserVO);

                    // Construct the list with user details
                    batchO2CList.add(batchItemsVO);
                }
            } else {

                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                    "batchO2C.processuploadedfile.error.usernotexist"));
                fileErrorList.add(errorVO);
                isValidationError = true;
                continue;
            }
        }// End of loop
         // end of User Detail

    }

    /**
     * Method for loading O2CBatch details..
     * This method will load the batches that are within the geography of user
     * whose userId is passed
     * with status(OPEN) also in items table for corresponding master record the
     * status is in p_itemStatus
     * 
     * @param con
     *            java.sql.Connection
     * @param itemStatus
     *            String
     * @param currentLevel
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchO2CMasterDetails(Connection con, String puserID, String itemStatus, String currentLevel) throws BTSLBaseException {
        final String methodName = "loadBatchO2CMasterDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_userID=" + puserID + " p_itemStatus=" + itemStatus + " p_currentLevel=" + currentLevel);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        final String sqlSelect = o2cBatchWithdrawQry.loadBatchO2CMasterDetailsQry(currentLevel, itemStatus);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList<FOCBatchMasterVO> list = new ArrayList<FOCBatchMasterVO>();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, puserID);
            pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
            pstmt.setString(8, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
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
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[loadBatchO2CMasterDetails]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[loadBatchO2CMasterDetails]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: o2cBatchMasterVOList size=" + list.size());
            }
        }
        return list;
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
    public Map loadBatchItemsMap(Connection con, String batchId, String itemStatus) throws BTSLBaseException {
        final String methodName = "loadBatchItemsMap";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_batchId=" + batchId + " p_itemStatus=" + itemStatus);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        final String sqlSelect = o2cBatchWithdrawQry.loadBatchItemsMapQry(itemStatus);
      
        final Map map = new HashMap();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                final O2CBatchItemsVO batchItemsVO = new O2CBatchItemsVO();

                batchItemsVO.setBatchId(batchId);
                batchItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
                batchItemsVO.setCategoryName(rs.getString("category_name"));
                batchItemsVO.setMsisdn(rs.getString("msisdn"));
                batchItemsVO.setUserId(rs.getString("user_id"));
                batchItemsVO.setStatus(rs.getString("status"));
                batchItemsVO.setGradeName(rs.getString("grade_name"));
                batchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchItemsVO.setExtTxnDate(rs.getDate("ext_txn_date"));
                batchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                batchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                batchItemsVO.setInitiatedBy(rs.getString("created_by"));
                batchItemsVO.setInitiatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                batchItemsVO.setLoginId(rs.getString("login_id"));
                batchItemsVO.setModifiedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("modified_on")));
                batchItemsVO.setModifiedBy(rs.getString("modified_by"));
                batchItemsVO.setReferenceNo(rs.getString("reference_no"));
                batchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchItemsVO.setExtTxnDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("ext_txn_date")));
                batchItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
                batchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                batchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                batchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                batchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));

                batchItemsVO.setCommissionType(rs.getString("commission_type"));
                batchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                batchItemsVO.setCommissionValue(rs.getLong("commission_value"));
                batchItemsVO.setTax1Type(rs.getString("tax1_type"));
                batchItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                batchItemsVO.setTax1Value(rs.getLong("tax1_value"));
                batchItemsVO.setTax2Type(rs.getString("tax2_type"));
                batchItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                batchItemsVO.setTax2Value(rs.getLong("tax2_value"));
                batchItemsVO.setTax3Type(rs.getString("tax3_type"));
                batchItemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                batchItemsVO.setTax3Value(rs.getLong("tax3_value"));

                batchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                batchItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                batchItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                batchItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                batchItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                batchItemsVO.setFirstApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("first_approved_on")));
                batchItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                batchItemsVO.setSecondApprovedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("second_approved_on")));
                batchItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                batchItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                batchItemsVO.setCancelledBy(rs.getString("cancelled_by"));
                batchItemsVO.setCancelledOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("cancelled_on")));
                batchItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
                batchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                batchItemsVO.setCategoryCode(rs.getString("category_code"));
                batchItemsVO.setFirstApproverName(rs.getString("first_approver_name"));
                batchItemsVO.setSecondApproverName(rs.getString("second_approver_name"));
                batchItemsVO.setInitiaterName(rs.getString("initiater_name"));
                batchItemsVO.setExternalCode(rs.getString("external_code"));

                map.put(rs.getString("batch_detail_id"), batchItemsVO);
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadBatchItemsList", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: loadBatchItemsMap map=" + map.size());
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_oldlastModified=" + oldlastModified + ",p_batchID=" + batchID);
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
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "sqlRecordModified=" + sqlRecordModified);
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
            LOG.error(methodName, "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[isBatchModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[isBatchModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered   p_batchID " + batchID + " p_newStatus=" + newStatus + " p_oldStatus=" + oldStatus);
        }
        PreparedStatement pstmt = null;
        int updateCount = -1;
        try {
            final StringBuilder sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? ");
            sqlBuffer.append(" WHERE batch_id=? AND status=? ");
            final String updateFOCBatches = sqlBuffer.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
            }

            pstmt = con.prepareStatement(updateFOCBatches);

            pstmt.setString(1, newStatus);
            pstmt.setString(2, batchID);
            pstmt.setString(3, oldStatus);

            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[updateBatchStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[updateBatchStatus]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:  updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method to cancel/approve the batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are: foc_batch_items,foc_batches
     * 
     * @param con
     * @param dataMap
     * @param currentLevel
     * @param userID
     * @param messages
     * @param locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList processOrderByBatch(Connection con, Map dataMap, String currentLevel, String userID, MessageResources messages, Locale locale, String smsDefaultLang, String smsSecondLang) throws BTSLBaseException {
        final String methodName = "processOrderByBatch";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_dataMap=" + dataMap + " p_currentLevel=" + currentLevel + " p_locale=" + locale + " p_userID=" + userID);
        }
        boolean externalTxnUnique = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        PreparedStatement pstmtLoadUser = null;
        // commented for DB2
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
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
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
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.network_code,u.category_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append("   ");
        
        SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
				ValueType.STRING);
    	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
 	
        }else {

        	sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.network_code,u.category_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        }
        
        final String sqlLoadUser = sqlBuffer.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;
        // after validating if request is to cancle the order, the below query
        // is used.
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
            sqlBuffer.append(" third_approver_remarks = ?, ");
        }
        sqlBuffer.append(" cancelled_by = ?, ");
        sqlBuffer.append(" cancelled_on = ?, status = ?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        if (!PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
            sqlBuffer.append(" AND status IN (? , ? )  ");
        } else {
            sqlBuffer.append(" AND status  = ?   ");
        }

        final String sqlCancelFOCBatchItems = sqlBuffer.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlCancelFOCBatchItems=" + sqlCancelFOCBatchItems);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
      
        final String selectItemsDetails = o2cBatchWithdrawQry.processOrderByBatchQry();
    
        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY isExistsTxnNum1=" + isExistsTxnNum1);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
        }
        sqlBuffer = null;
        Date date = null;
        try {
            O2CBatchItemsVO o2cBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = con.prepareStatement(sqlLoadUser);

            // for DB2
            psmtCancelFOCBatchItem =  con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem =  con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem =  con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            // for DB2
            pstmtUpdateMaster = con.prepareStatement(updateFOCBatches);
            pstmtIsModified = con.prepareStatement(isModified);
            pstmtIsTxnNumExists1 = con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = con.prepareStatement(isExistsTxnNum2);
            errorList = new ArrayList();
            final Iterator iterator = dataMap.keySet().iterator();
            String key = null;
            int m = 0;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                o2cBatchItemVO = (O2CBatchItemsVO) dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = o2cBatchItemVO.getBatchId();
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, o2cBatchItemVO.getUserId());
                rs = pstmtLoadUser.executeQuery();
                if (rs.next())// check data found or not
                {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(o2cBatchItemVO.getUserId());
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
                    
                    channelUserVO.setNetworkCode(rs.getString("network_code"));
                    channelUserVO.setCategoryCode(rs.getString("category_code"));
                    // (User status is checked) if this condition is true then
                    // made entry in logs and leave this data.

                    // user life cycle
                    boolean senderStatusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkCode(), channelUserVO.getCategoryCode(),
                        PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    if (userStatusVO != null) {
                        final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
                        final String status[] = userStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(channelUserVO.getStatus())) {
                                senderStatusAllowed = true;
                            }
                        }
                    } else {
                        throw new BTSLBaseException(this, methodName, "error.status.processing");
                    }
                    if (!senderStatusAllowed) {
                        con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                            "batchfoc.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User is not active", "Approval level" + currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User is IN suspend", "Approval level" + currentLevel);
                        continue;
                    }
        		 	// if status of batch item is empty
                    else if(BTSLUtil.isNullString(o2cBatchItemVO.getStatus())){
                    	BatchO2CFileProcessLog.o2cBatchItemLog(methodName,o2cBatchItemVO,"DISCARD : ",o2cBatchItemVO.getBatchDetailId());
    					continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User not found", "Approval level" + currentLevel);
                    continue;

                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, o2cBatchItemVO.getBatchDetailId());
                rs1 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                if (rs1.next()) {
                    newlastModified = rs1.getTimestamp("modified_on");
                }
                // (record not found means it is modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Record is already modified by some one else",
                        "Approval level" + currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Record is already modified by some one else",
                        "Approval level" + currentLevel);
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (externalTxnUnique && !BTSLUtil.isNullString(o2cBatchItemVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
                    .equals(o2cBatchItemVO.getStatus())) {
                    // check in foc_batch-item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, o2cBatchItemVO.getBatchDetailId());
                    rs2 = pstmtIsTxnNumExists1.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs2.next()) {
                        con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : External transaction number already exists BATCH FOC",
                            "Approval level" + currentLevel);
                        continue;
                    }
                    // check in channel_transfers table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs3 = pstmtIsTxnNumExists2.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs3.next()) {
                        con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + currentLevel);
                        continue;
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelFOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(o2cBatchItemVO.getStatus())) {
                    psmtCancelFOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                        
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
                       
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
                       
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtCancelFOCBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(o2cBatchItemVO.getStatus())) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                  
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(o2cBatchItemVO.getStatus())) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                 
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);

                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), messages.getMessage(locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level" + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processiong each record
                con.commit();
            }// end of while
             // Check the status to be updated in master table agfter processing
             // of all records

        }// end of try
        catch (SQLException sqe) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "", "",
                "Exception:" + ex.getMessage());
            BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelFOCBatchItem != null) {
                    psmtCancelFOCBatchItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
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
                        LOG.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    // If all records are canle then set cancelled in master
                    // table
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                        // Otherwise set OPEN in mastrer table
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                  
                    ++m;
                    pstmtUpdateMaster.setString(m, smsDefaultLang);
                    
                    ++m;
                    pstmtUpdateMaster.setString(m, smsSecondLang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "", messages.getMessage(locale, "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]",
                            "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                con.commit();
            } catch (SQLException sqe) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "SQLException : " + sqe);
                LOG.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + currentLevel + ", BATCH_ID=" + batch_ID);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Exception : " + ex);
                LOG.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());
                BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + currentLevel + ", BATCH_ID=" + batch_ID);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectItemsDetails != null) {
                    pstmtSelectItemsDetails.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateMaster != null) {
                    pstmtUpdateMaster.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * 
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_focBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @param p_sms_default_lang
     * @param p_sms_second_lang
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatch(Connection p_con, Map p_dataMap, String p_currentLevel, String p_userID, FOCBatchMasterVO p_focBatchMatserVO, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "closeOrderByBatch";
        StringBuilder loggerValue= new StringBuilder(); 
		
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered p_dataMap=" );
            loggerValue.append(p_dataMap);
            loggerValue.append(" p_currentLevel=");
            loggerValue.append(p_currentLevel);
            loggerValue.append(" p_locale=");
            loggerValue.append(p_locale);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement pstmtLoadNetworkStock = null;
        PreparedStatement pstmtUpdateNetworkStock = null;
        PreparedStatement pstmtInsertNetworkDailyStock = null;
        PreparedStatement pstmtSelectNetworkStock = null;
        PreparedStatement pstmtupdateSelectedNetworkStock = null;
        // commented for DB2OraclePreparedStatement
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
       
        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement psmtAppr3FOCBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        PreparedStatement pstmtUpdateMaster = null;
      
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
   
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
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
        int updateCount = 0;
        String o2cTransferID = null;
        PreparedStatement psmtInsertUserThreshold = null;
        long thresholdValue = -1;

        // user life cycle
        String senderStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_focBatchMatserVO.getNetworkCode(), p_focBatchMatserVO.getCategoryCode(),
            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            senderStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.processing");
        }
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        final String senderStatusAllowed1 = senderStatusAllowed.replaceAll("'", "");
        final String ss = senderStatusAllowed1.replaceAll("\" ", "");
        final String m_senderStatusAllowed[] = ss.split(",");
        
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
            sqlBuffer.append(" AND u.status IN (");
            for (int i = 0; i < m_senderStatusAllowed.length; i++) {
                sqlBuffer.append(" ?");
                if (i != m_senderStatusAllowed.length - 1) {
                    sqlBuffer.append(",");
                }
            }
            sqlBuffer.append(")");
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
        sqlBuffer.append(" AND u.status IN (");
        for (int i = 0; i < m_senderStatusAllowed.length; i++) {
            sqlBuffer.append(" ?");
            if (i != m_senderStatusAllowed.length - 1) {
                sqlBuffer.append(",");
            }
        }
        sqlBuffer.append(")");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
        }
        
        
        
        final String sqlLoadUser = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlLoadUser=");
            loggerValue.append(sqlLoadUser);
            LOG.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The query below is used to load the network stock details for network
        // in between sender and receiver
        // This table will basically used to update the daily_stock_updated_on
        // and also to know how many
        // records are to be inseert in network_daily_stocks
        
        final String sqlLoadNetworkStock = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlLoadNetworkStock=" + sqlLoadNetworkStock);
        }
        sqlBuffer = null;

        // Update daily_stock_updated_on with current date
        sqlBuffer = new StringBuilder("UPDATE network_stocks SET daily_stock_updated_on = ? ");
        sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? ");
        final String sqlUpdateNetworkStock = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlUpdateNetworkStock=");
            loggerValue.append(sqlUpdateNetworkStock);
            LOG.debug(methodName, loggerValue );
        }
        sqlBuffer = null;
        boolean multipleWalletApply = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in network_daily_stocks
        if (multipleWalletApply) {
            sqlBuffer = new StringBuilder("INSERT INTO network_daily_stocks(stock_date, network_code, network_code_for, ");
            sqlBuffer.append("product_code, foc_stock_created, foc_stock_returned, foc_stock, foc_stock_sold, foc_last_txn_no, ");
            sqlBuffer.append("foc_last_txn_type, foc_last_txn_stock, foc_previous_stock, created_on,creation_type )");
            sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            sqlBuffer = new StringBuilder("INSERT INTO network_daily_stocks(stock_date, network_code, network_code_for, ");
            sqlBuffer.append("product_code, stock_created, stock_returned, stock, stock_sold, last_txn_no, ");
            sqlBuffer.append("last_txn_type, last_txn_stock, previous_stock, created_on,creation_type )");
            sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String sqlInsertNetworkDailyStock = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlInsertNetworkDailyStock=");
            loggerValue.append(sqlInsertNetworkDailyStock);
            LOG.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // Select the stock for the requested product for network.
        final String sqlSelectNetworkStock = o2cBatchWithdrawQry.closeOrderByBatchSelectNetworkStockQry();
      
        sqlBuffer = null;

        // Debit the network stock
        if (multipleWalletApply) {
            sqlBuffer = new StringBuilder(" UPDATE network_stocks SET foc_previous_stock = foc_stock , foc_stock = ?, ");
            sqlBuffer.append(" foc_stock_sold = ? , foc_last_txn_no = ? , foc_last_txn_type = ?, foc_last_txn_stock= ?, ");
            sqlBuffer.append(" modified_by =?, modified_on =? ");
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(" network_code = ? ");
            sqlBuffer.append(" AND ");
            sqlBuffer.append(" product_code = ? AND network_code_for = ?  ");
        } else {
            sqlBuffer = new StringBuilder(" UPDATE network_stocks SET previous_stock = stock , stock = ?, ");
            sqlBuffer.append(" stock_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_stock= ?, ");
            sqlBuffer.append(" modified_by =?, modified_on =? ");
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(" network_code = ? ");
            sqlBuffer.append(" AND ");
            sqlBuffer.append(" product_code = ? AND network_code_for = ?  ");
        }
        final String updateSelectedNetworkStock = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY updateSelectedNetworkStock=");
            loggerValue.append(updateSelectedNetworkStock);
            LOG.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // Insert record into network_stock_transactions table.
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_transactions ( ");
        sqlBuffer.append(" txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
        sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
        sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
        sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp  ");
        if (multipleWalletApply) {
            sqlBuffer.append(",txn_wallet,ref_txn_id ");
            sqlBuffer.append(" )VALUES ");
            sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            sqlBuffer.append(" )VALUES ");
            sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String insertNetworkStockTransaction = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertNetworkStockTransaction=");
            loggerValue.append(insertNetworkStockTransaction);
            LOG.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Insert record into network_stock_trans_items
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_trans_items ");
        sqlBuffer.append(" (s_no, txn_no, product_code, required_quantity, approved_quantity, stock, mrp, amount, date_time) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?) ");
        final String insertNetworkStockTransactionItem = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertNetworkStockTransactionItem=");
            loggerValue.append(insertNetworkStockTransactionItem);
            LOG.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be inseert in user_daily_balances table
       
        final String selectUserBalances = o2cBatchWithdrawQry.closeOrderByBatchSelectUserBalancesQry();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY selectUserBalances=" );
            loggerValue.append(selectUserBalances);
            LOG.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY updateUserBalances=" );
            loggerValue.append(updateUserBalances);
            LOG.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in user_daily_balances
        sqlBuffer = new StringBuilder(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserDailyBalances = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertUserDailyBalances="  );
            loggerValue.append(insertUserDailyBalances);
            LOG.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Select the balance of user for the perticuler product and network.
        final String selectBalance = o2cBatchWithdrawQry.closeOrderByBatchSelectBalanceQry();
     
        sqlBuffer = null;

        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        final String updateBalance = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY updateBalance="  );
            loggerValue.append(updateBalance);
            LOG.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Insert the record of balnce for user (If balance not found in
        // user_balances)
        sqlBuffer = new StringBuilder(" INSERT ");
        sqlBuffer.append(" INTO user_balances ");
        sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
        sqlBuffer.append(" user_id, product_code , network_code, network_code_for ) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?) ");
        final String insertBalance = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertBalance="   );
            loggerValue.append(insertBalance);
            LOG.debug(methodName, loggerValue );
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
        sqlBuffer.append(" FROM user_transfer_counts ");
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS ");
        } else {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        }
        
        final String selectTransferCounts = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY selectTransferCounts="  );
            loggerValue.append(selectTransferCounts);
            LOG.debug(methodName, loggerValue );
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY selectProfileCounts=");
            loggerValue.append(selectProfileCounts);
            LOG.debug(methodName, loggerValue);
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY updateTransferCounts=");
            loggerValue.append(updateTransferCounts);
            LOG.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // Insert the record in user_transfer_counts (If no record found for
        // user running counters)
        sqlBuffer = new StringBuilder(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        final String insertTransferCounts = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertTransferCounts=");
            loggerValue.append(insertTransferCounts);
            LOG.debug(methodName,   loggerValue);
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlApprv1FOCBatchItems=");
            loggerValue.append(sqlApprv1FOCBatchItems);
            LOG.debug(methodName, loggerValue );
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlApprv2FOCBatchItems=");
            loggerValue.append(sqlApprv2FOCBatchItems);
            LOG.debug(methodName,  loggerValue );
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlApprv3FOCBatchItems=");
            loggerValue.append(sqlApprv3FOCBatchItems);
            LOG.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
       
        final String selectItemsDetails = o2cBatchWithdrawQry.focBatchesSelectItemsDetailsQry();
      
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY updateFOCBatches=");
            loggerValue.append(updateFOCBatches);
            LOG.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
         	loggerValue.setLength(0);
            loggerValue.append("QUERY isModified=");
            loggerValue.append(isModified);
            LOG.debug(methodName,  loggerValue );
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY loadTransferProfileProduct=" );
            loggerValue.append(loadTransferProfileProduct);
            LOG.debug(methodName, loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY isExistsTxnNum1=" );
            loggerValue.append(isExistsTxnNum1);
            LOG.debug(methodName,  loggerValue);
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY isExistsTxnNum2=" );
            loggerValue.append(isExistsTxnNum2);
            LOG.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfer
        // items table for the order that is closed
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer
            .append(" tax3_value, transfer_date, transfer_id, user_unit_price, sender_debit_quantity, receiver_credit_quantity,commision_quantity, sender_post_stock, receiver_post_stock)  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertIntoChannelTransferItem="  );
            loggerValue.append(insertIntoChannelTransferItem);
            LOG.debug(methodName, loggerValue );
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfers
        // table for the order that is closed
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
        sqlBuffer.append(" control_transfer,msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id");
        if (multipleWalletApply) {
            sqlBuffer.append(",TXN_WALLET)");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            sqlBuffer.append(") ");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertIntoChannelTransfer="  );
            loggerValue.append(insertIntoChannelTransfer);
            LOG.debug(methodName, loggerValue );
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY selectBalanceInfoForMessage="  );
            loggerValue.append(selectBalanceInfoForMessage);
            LOG.debug(methodName, loggerValue);
        }

        // added by nilesh:added two new columns threshold_type and remark
        final StringBuilder strBuffThresholdInsert = new StringBuilder();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY insertUserThreshold=" );
            loggerValue.append(insertUserThreshold);
            LOG.debug("closeOrederByBatch",  loggerValue );
        }

        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        try {
            O2CBatchItemsVO o2cBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            ChannelTransferVO channelTransferVO = null;
            ChannelTransferItemsVO channelTransferItemVO = null;
            date = new Date();
            ArrayList channelTransferItemVOList = null;
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            pstmtLoadNetworkStock = p_con.prepareStatement(sqlLoadNetworkStock);
            pstmtUpdateNetworkStock = p_con.prepareStatement(sqlUpdateNetworkStock);
            pstmtInsertNetworkDailyStock = p_con.prepareStatement(sqlInsertNetworkDailyStock);
            pstmtSelectNetworkStock = p_con.prepareStatement(sqlSelectNetworkStock);
            pstmtupdateSelectedNetworkStock = p_con.prepareStatement(updateSelectedNetworkStock);
            // commented for DB2
            pstmtInsertNetworkStockTransaction =  p_con.prepareStatement(insertNetworkStockTransaction);
            pstmtInsertNetworkStockTransactionItem = p_con.prepareStatement(insertNetworkStockTransactionItem);
            pstmtSelectUserBalances = p_con.prepareStatement(selectUserBalances);
            pstmtUpdateUserBalances = p_con.prepareStatement(updateUserBalances);
            pstmtInsertUserDailyBalances = p_con.prepareStatement(insertUserDailyBalances);
            pstmtSelectBalance = p_con.prepareStatement(selectBalance);
            pstmtUpdateBalance = p_con.prepareStatement(updateBalance);
            pstmtInsertBalance = p_con.prepareStatement(insertBalance);
            pstmtSelectTransferCounts = p_con.prepareStatement(selectTransferCounts);
            pstmtSelectProfileCounts = p_con.prepareStatement(selectProfileCounts);
            pstmtUpdateTransferCounts = p_con.prepareStatement(updateTransferCounts);
            pstmtInsertTransferCounts = p_con.prepareStatement(insertTransferCounts);

          
            psmtAppr1FOCBatchItem =  p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem =  p_con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtAppr3FOCBatchItem = p_con.prepareStatement(sqlApprv3FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
     
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = p_con.prepareStatement(loadTransferProfileProduct);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);
            pstmtInsertIntoChannelTransferItems = p_con.prepareStatement(insertIntoChannelTransferItem);
            pstmtInsertIntoChannelTranfers =  p_con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = p_con.prepareStatement(selectBalanceInfoForMessage);
            psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            String defautWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defautWebGatewayCode);
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

            long maxBalance = 0;
            boolean isNotToExecuteQuery = false;
            long balance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;
            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                o2cBatchItemVO = (O2CBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = o2cBatchItemVO.getBatchId();
                }
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("Executed o2cBatchItemVO=" );
                    loggerValue.append(o2cBatchItemVO.toString());
                    LOG.debug(methodName,loggerValue );
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, o2cBatchItemVO.getUserId());
                for (int x = 0; x < m_senderStatusAllowed.length; x++) {
                    ++m;
                    pstmtLoadUser.setString(m, m_senderStatusAllowed[x]);
                }
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(o2cBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    
					if (!tcpOn) {
						channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
					} else {
						channelUserVO.setTransferProfileStatus(
								tcpMap.get(rs.getString("transfer_profile_id")).get("profileStatus"));// TCP
					}

                    language = rs.getString("phone_language");
                    country = rs.getString("country");
                    channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                    // (user status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog
                            .detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : User is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Commission profile suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Transfer profile suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : User is IN suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
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

                ChannelTransferBL.genrateWithdrawID(channelTransferVO);
                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch foc item was created.
                channelTransferVO.setCreatedOn(o2cBatchItemVO.getInitiatedOn());

                networkStocksVO.setLastTxnNum(o2cTransferID);
               
                networkStocksVO.setLastTxnBalance(o2cBatchItemVO.getRequestedQuantity());
                networkStocksVO.setWalletBalance(o2cBatchItemVO.getRequestedQuantity());

                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_WITHDRAW);
                networkStocksVO.setModifiedBy(p_userID);
                networkStocksVO.setModifiedOn(date);
                dailyStockUpdatedOn = null;
                dayDifference = 0;
                // select the record form the network stock table.
                pstmtLoadNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                ++m;
                pstmtLoadNetworkStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                rs1 = pstmtLoadNetworkStock.executeQuery();
                while (rs1.next()) {
                    dailyStockUpdatedOn = rs1.getDate("daily_stock_updated_on");

                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, date);

                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the difference
                        // is.
                        if (LOG.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                            loggerValue.append("Till now daily Stock is not updated on " );
                            loggerValue.append(date);
                            loggerValue.append(", day differences = ");
                            loggerValue.append(dayDifference);
                            LOG.debug("closeOrderByBatch ",  loggerValue);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs1.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs1.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs1.getString("product_code"));
                            if (multipleWalletApply) {
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("foc_stock_created"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("foc_stock_returned"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("foc_stock"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("foc_stock_sold"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("foc_last_txn_stock"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("foc_previous_stock"));
                            } else {
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("stock_created"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("stock_returned"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("stock"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("stock_sold"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("last_txn_stock"));
                                ++m;
                                pstmtInsertNetworkDailyStock.setLong(m, rs1.getLong("previous_stock"));
                            }
                            ++m;
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
							
                            if (updateCount <= 0) {
                                p_con.rollback();
                                errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                                    "FAIL : DB Error while insert in network daily stock table", "Approval level = " + p_currentLevel + "updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Termination of the procissing",
                                "Approval level = " + p_currentLevel);
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
                        updateCount = pstmtUpdateNetworkStock.executeUpdate();
                        // (record not updated properly in db) if this condition
                        // is true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            p_con.rollback();
                            errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                                "FAIL : DB Error while updating network daily stock table", "Approval level = " + p_currentLevel + "updateCount = " + updateCount);
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
                rs2 = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockSold = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs2.next()) {
                    if (multipleWalletApply) {
                        stock = rs2.getLong("foc_stock");
                        stockSold = rs2.getLong("foc_stock_sold");
                    } else {
                        stock = rs2.getLong("stock");
                        stockSold = rs2.getLong("stock_sold");
                    }
                    previousNwStockToBeSetChnlTrfItems = stock;
                }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(p_messages.getMessage(p_locale, "label.all"), String.valueOf(o2cBatchItemVO.getRecordNumber()) + " - " + p_messages.getMessage(
                        p_locale, "label.all"), p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", "Approval level = " + p_currentLevel);
                    throw new BTSLBaseException(this, methodName, "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", "Approval level = " + p_currentLevel);
                    continue;
                }
                if (stock != -1) {
                    stock += networkStocksVO.getWalletbalance();
                }
                if (stockSold != -1) {
                    stockSold -= networkStocksVO.getWalletbalance();
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
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB Error while updating network stock table",
                        "Approval level = " + p_currentLevel + ", updateCount =" + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
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
                networkStockTxnVO.setReferenceNo(o2cBatchItemVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(o2cBatchItemVO.getInitiatedOn());
                networkStockTxnVO.setRequestedQuantity(o2cBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setApprovedQuantity(o2cBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setInitiaterRemarks(o2cBatchItemVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(o2cBatchItemVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(o2cBatchItemVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(o2cBatchItemVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(o2cBatchItemVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(o2cBatchItemVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(o2cBatchItemVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(o2cBatchItemVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(o2cBatchItemVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(p_userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(p_userID);

                networkStockTxnVO.setTxnStatus(o2cBatchItemVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_WITHDRAW);
                networkStockTxnVO.setTxnType(PretupsI.CREDIT);
                networkStockTxnVO.setInitiatedBy(p_userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(o2cBatchItemVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(o2cBatchItemVO.getTransferMrp());

                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                networkItemsVO.setRequiredQuantity(o2cBatchItemVO.getRequestedQuantity());
                networkItemsVO.setApprovedQuantity(o2cBatchItemVO.getRequestedQuantity());
                networkItemsVO.setMrp(o2cBatchItemVO.getTransferMrp());
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

               
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedRemarks());

               
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
                if (multipleWalletApply) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.O2C_WALLET_TYPE);
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, channelTransferVO.getTransferID());
                }
                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB Error while updating network stock TXN table",
                        "Approval level = " + p_currentLevel + ", updateCount =" + updateCount);
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
                ++m;
                // Date 07/02/08
                pstmtInsertNetworkStockTransactionItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                updateCount = pstmtInsertNetworkStockTransactionItem.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        "Approval level = " + p_currentLevel + ", updateCount =" + updateCount);
                    continue;
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
                rs3 = pstmtSelectUserBalances.executeQuery();
                while (rs3.next()) {
                    dailyBalanceUpdatedOn = rs3.getDate("daily_balance_updated_on");
                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, date);
                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the
                        if (LOG.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                            loggerValue.append("Till now daily Stock is not updated on " );
                            loggerValue.append(date);
                            loggerValue.append(", day differences = ");
                            loggerValue.append(dayDifference);
                            LOG.debug("closeOrdersByBatch ",  loggerValue);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertUserDailyBalances.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertUserDailyBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs3.getString("user_id"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs3.getString("network_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs3.getString("network_code_for"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs3.getString("product_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs3.getLong("balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs3.getLong("prev_balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_WITHDRAW);
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertUserDailyBalances.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                p_con.rollback();
                                errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                                    "FAIL : DB Error while inserting user daily balances table", "Approval level = " + p_currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + p_currentLevel);
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
                            p_con.rollback();
                            errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                                "FAIL : DB Error while updating user balances table for daily balance", "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
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
                rs4 = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                if (rs4.next()) {
                    balance = rs4.getLong("balance");
                }
                if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    balance -= o2cBatchItemVO.getRequestedQuantity();
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, o2cBatchItemVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                rs5 = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs5.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(p_focBatchMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs5.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs5.getLong("max_balance"));
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                maxBalance = transferProfileProductVO.getMaxBalanceAsLong();
                // (max balance reach for the receiver) if this condition is
                // true then made entry in logs and leave this data.
                if (maxBalance < balance) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : User Max balance reached",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the order
                // value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (balance <= -1 || maxBalance < o2cBatchItemVO.getRequestedQuantity()) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : User Max balance reached",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                if (!isNotToExecuteQuery) {
                    m = 0;

                    pstmtUpdateBalance.clearParameters();
                    handlerStmt = pstmtUpdateBalance;

                    
                    ++m;
                    handlerStmt.setLong(m, balance);
                    ++m;
                    handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_WITHDRAW);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    // where
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + p_currentLevel);
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
                    // o2cBatchItemVO.getCategoryCode()); //threshold value

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
                            if (LOG.isDebugEnabled()) {
                            	loggerValue.setLength(0);
                                loggerValue.append("Entry in threshold counter" );
                                loggerValue.append(thresholdValue);
                                loggerValue.append(", prvbal: ");
                                loggerValue.append(previousUserBalToBeSetChnlTrfItems);
                                loggerValue.append("nbal");
                                loggerValue.append(balance);
                                LOG.debug("closeOrederByBatch",loggerValue );
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
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_WITHDRAW);
                            if (balance >= thresholdValue) {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.ABOVE_THRESHOLD_TYPE);
                            } else {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
                            }
                           
                            ++m;
                            psmtInsertUserThreshold.setString(m, o2cBatchItemVO.getCategoryCode());
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
                        loggerValue.append("SQLException " );
                        loggerValue.append(sqle.getMessage());
                        LOG.error(methodName,  loggerValue);
                        LOG.errorTrace(methodName, sqle);
                    	loggerValue.setLength(0);
                        loggerValue.append("Error while updating user_threshold_counter table SQL Exception:" );
                        loggerValue.append(sqle.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]",
                            o2cTransferID, "", p_focBatchMatserVO.getNetworkCode(),  loggerValue.toString() );
                    }// end of catch

                }
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                rs6 = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs6.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(o2cBatchItemVO.getUserId());

                    countsVO.setDailyInCount(rs6.getLong("daily_in_count"));
                    countsVO.setDailyInValue(rs6.getLong("daily_in_value"));
                    countsVO.setWeeklyInCount(rs6.getLong("weekly_in_count"));
                    countsVO.setWeeklyInValue(rs6.getLong("weekly_in_value"));
                    countsVO.setMonthlyInCount(rs6.getLong("monthly_in_count"));
                    countsVO.setMonthlyInValue(rs6.getLong("monthly_in_value"));

                    countsVO.setDailyOutCount(rs6.getLong("daily_out_count"));
                    countsVO.setDailyOutValue(rs6.getLong("daily_out_value"));
                    countsVO.setWeeklyOutCount(rs6.getLong("weekly_out_count"));
                    countsVO.setWeeklyOutValue(rs6.getLong("weekly_out_value"));
                    countsVO.setMonthlyOutCount(rs6.getLong("monthly_out_count"));
                    countsVO.setMonthlyOutValue(rs6.getLong("monthly_out_value"));

                    countsVO.setUnctrlDailyInCount(rs6.getLong("outside_daily_in_count"));
                    countsVO.setUnctrlDailyInValue(rs6.getLong("outside_daily_in_value"));
                    countsVO.setUnctrlWeeklyInCount(rs6.getLong("outside_weekly_in_count"));
                    countsVO.setUnctrlWeeklyInValue(rs6.getLong("outside_weekly_in_value"));
                    countsVO.setUnctrlMonthlyInCount(rs6.getLong("outside_monthly_in_count"));
                    countsVO.setUnctrlMonthlyInValue(rs6.getLong("outside_monthly_in_value"));

                    countsVO.setUnctrlDailyOutCount(rs6.getLong("outside_daily_out_count"));
                    countsVO.setUnctrlDailyOutValue(rs6.getLong("outside_daily_out_value"));
                    countsVO.setUnctrlWeeklyOutCount(rs6.getLong("outside_weekly_out_count"));
                    countsVO.setUnctrlWeeklyOutValue(rs6.getLong("outside_weekly_out_value"));
                    countsVO.setUnctrlMonthlyOutCount(rs6.getLong("outside_monthly_out_count"));
                    countsVO.setUnctrlMonthlyOutValue(rs6.getLong("outside_monthly_out_value"));

                    countsVO.setDailySubscriberOutCount(rs6.getLong("daily_subscriber_out_count"));
                    countsVO.setDailySubscriberOutValue(rs6.getLong("daily_subscriber_out_value"));
                    countsVO.setWeeklySubscriberOutCount(rs6.getLong("weekly_subscriber_out_count"));
                    countsVO.setWeeklySubscriberOutValue(rs6.getLong("weekly_subscriber_out_value"));
                    countsVO.setMonthlySubscriberOutCount(rs6.getLong("monthly_subscriber_out_count"));
                    countsVO.setMonthlySubscriberOutValue(rs6.getLong("monthly_subscriber_out_value"));

                    countsVO.setLastTransferDate(rs6.getDate("last_transfer_date"));
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
                pstmtSelectProfileCounts.setString(m, o2cBatchItemVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                rs7 = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
                if (rs7.next()) {
                    transferProfileVO = new TransferProfileVO();
                    transferProfileVO.setProfileId(rs7.getString("profile_id"));
                    transferProfileVO.setDailyInCount(rs7.getLong("daily_transfer_in_count"));
                    transferProfileVO.setDailyInValue(rs7.getLong("daily_transfer_in_value"));
                    transferProfileVO.setWeeklyInCount(rs7.getLong("weekly_transfer_in_count"));
                    transferProfileVO.setWeeklyInValue(rs7.getLong("weekly_transfer_in_value"));
                    transferProfileVO.setMonthlyInCount(rs7.getLong("monthly_transfer_in_count"));
                    transferProfileVO.setMonthlyInValue(rs7.getLong("monthly_transfer_in_value"));
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Transfer profile not found",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.
                if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Daily transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + o2cBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.dailyinvaluereach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Daily transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Weekly transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + o2cBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.weeklyinvaluereach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Weekly transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Monthly transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + o2cBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.monthlyinvaluereach"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Monthly transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                countsVO.setUserID(channelUserVO.getUserID());
                countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                countsVO.setDailyInValue(countsVO.getDailyInValue() + o2cBatchItemVO.getRequestedQuantity());
                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + o2cBatchItemVO.getRequestedQuantity());
                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + o2cBatchItemVO.getRequestedQuantity());
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
                    ++m;
                    updateCount = pstmtInsertTransferCounts.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    } else {
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, o2cBatchItemVO.getBatchDetailId());
                rs8 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                // check record is modified or not
                if (rs8.next()) {
                    newlastModified = rs8.getTimestamp("modified_on");
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Record is already modified",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : Record is already modified",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                boolean externalTxnUnique = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
                // (external txn number is checked)
                if (externalTxnUnique && !BTSLUtil.isNullString(o2cBatchItemVO.getExtTxnNo())) {
                    // check in foc_batch_item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, o2cBatchItemVO.getBatchDetailId());
                    rs9 = pstmtIsTxnNumExists1.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs9.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                            "FAIL : External transaction number already exists in FOC Batch", "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // check in channel transfer table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs10 = pstmtIsTxnNumExists2.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs10.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                            "FAIL : External transaction number already exists in CHANNEL TRF", "Approval level = " + p_currentLevel);
                        continue;
                    }
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    o2cBatchItemVO.setFirstApprovedBy(p_userID);
                    o2cBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // ++m;
                    // commented for DB2 psmtAppr1FOCBatchItem.setFormOfUse(m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    o2cBatchItemVO.setSecondApprovedBy(p_userID);
                    o2cBatchItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
               
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                    psmtAppr3FOCBatchItem.clearParameters();
                    o2cBatchItemVO.setThirdApprovedBy(p_userID);
                    o2cBatchItemVO.setThirdApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                   
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cBatchItemVO.getThirdApproverRemarks());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr3FOCBatchItem.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                channelTransferVO.setCanceledOn(o2cBatchItemVO.getCancelledOn());
                channelTransferVO.setCanceledBy(o2cBatchItemVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(o2cBatchItemVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(o2cBatchItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(o2cBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setCreatedBy(o2cBatchItemVO.getInitiatedBy());
                channelTransferVO.setDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(o2cBatchItemVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(o2cBatchItemVO.getExtTxnNo());
                channelTransferVO.setFinalApprovedBy(o2cBatchItemVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(o2cBatchItemVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(o2cBatchItemVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(o2cBatchItemVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(o2cBatchItemVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(o2cBatchItemVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(o2cBatchItemVO.getCategoryCode());//
                channelTransferVO.setBatchNum(o2cBatchItemVO.getBatchId());
                channelTransferVO.setBatchDate(p_focBatchMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(channelUserVO.getUserID());//
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(p_userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(p_focBatchMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setSenderGradeCode(o2cBatchItemVO.getGradeCode());
                channelTransferVO.setSenderTxnProfile(o2cBatchItemVO.getTxnProfile());

                channelTransferVO.setReferenceNum(o2cBatchItemVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(o2cBatchItemVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(o2cBatchItemVO.getStatus());
                channelTransferVO.setThirdApprovedBy(o2cBatchItemVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(o2cBatchItemVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(o2cBatchItemVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax1(o2cBatchItemVO.getTax1Value());
                channelTransferVO.setTotalTax2(o2cBatchItemVO.getTax2Value());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
                channelTransferVO.setTransferDate(o2cBatchItemVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(o2cBatchItemVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(o2cBatchItemVO.getTransferMrp());

                channelTransferItemVO = new ChannelTransferItemsVO();
                channelTransferItemVO.setApprovedQuantity(o2cBatchItemVO.getRequestedQuantity());
                // Added by Amit Raheja
                channelTransferItemVO.setRequestedQuantity(PretupsBL.getDisplayAmount(o2cBatchItemVO.getRequestedQuantity()));
                channelTransferItemVO.setDiscountType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
                channelTransferItemVO.setDiscountRate(0);
                // Addition ends
                channelTransferItemVO.setCommProfileDetailID(o2cBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(o2cBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(o2cBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(o2cBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(o2cBatchItemVO.getTransferMrp());
                channelTransferItemVO.setProductCode(p_focBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                channelTransferItemVO.setRequiredQuantity(o2cBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(o2cBatchItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(o2cBatchItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(o2cBatchItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(o2cBatchItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(o2cBatchItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(o2cBatchItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(o2cBatchItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(o2cBatchItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(o2cBatchItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(p_focBatchMatserVO.getProductMrp());
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(p_focBatchMatserVO.getProductShortName());

                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                // Added by Amit Raheja
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);
                channelTransferVO.setTotalTax3(channelTransferItemVO.getTax3Value());
                channelTransferVO.setPayableAmount(channelTransferItemVO.getPayableAmount());
                channelTransferVO.setNetPayableAmount(channelTransferItemVO.getNetPayableAmount());
                // Addition ends

                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append( "Exiting: channelTransferVO=" );
                    loggerValue.append(channelTransferVO.toString());
                    LOG.debug(methodName, loggerValue);
                }
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("Exiting: channelTransferItemVO=");
                    loggerValue.append(channelTransferItemVO.toString());
                    LOG.debug(methodName, loggerValue );
                }
                m = 0;
                pstmtInsertIntoChannelTranfers.clearParameters();
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCanceledBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
                
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
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFinalApprovedBy());
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
                pstmtInsertIntoChannelTranfers.setString(m, o2cBatchItemVO.getStatus());
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
                pstmtInsertIntoChannelTranfers.setDate(m, BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getTransferDate()));
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
                pstmtInsertIntoChannelTranfers.setString(m, o2cBatchItemVO.getMsisdn());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());

                // By sandeep ID TOG001
                // to geographical domain also inserted as the geogrpahical
                // domain that will help in reports
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());
               
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDefaultLang());
               
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondLang());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, p_focBatchMatserVO.getCreatedBy());
                if (multipleWalletApply) {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setString(m, PretupsI.FOC_WALLET_TYPE);
                }
                // ends here
                // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
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
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO,
                        "FAIL : DB Error while inserting in channel transfer items table", "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                // user life cycle
                if (o2cBatchItemVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                    if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_CHURN)) {
                        final int updatecount = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getToUserID(), channelUserVO.getStatus(),
                            PretupsI.STATUS_ACTIVE);
                        if (updatecount > 0) {
                            p_con.commit();
                            BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "PASS : Order is closed successfully",
                                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        } else {
                            p_con.rollback();
                            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.updating");
                        }
                    } else {
                        p_con.commit();
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, o2cBatchItemVO, "PASS : Order is closed successfully",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    }

                } else {
                    p_con.commit();
                }
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
                rs11 = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList = new ArrayList();
                while (rs11.next()) {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs11.getString("product_code"));
                    balancesVO.setBalance(rs11.getLong("balance"));
                    balancesVO.setProductShortCode(rs11.getString("product_short_code"));
                    balancesVO.setProductShortName(rs11.getString("short_name"));
                    userbalanceList.add(balancesVO);
                }
                // generate the message arguments to be send in SMS
                keyArgumentVO = new KeyArgumentVO();
                argsArr = new String[2];
                argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
                argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
                keyArgumentVO.setKey(PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_TXNSUBKEY);
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
                        keyArgumentVO.setKey(PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String focNotifyMsg = null;
                boolean o2cSmsNotify = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_SMS_NOTIFY);
                if (o2cSmsNotify) {
                    final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        focNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        focNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList), focNotifyMsg };
                }

                if (focNotifyMsg == null) {
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList) };
                }

                messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_SMS1, array);
                pushMessage = new PushMessage(o2cBatchItemVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
                pushMessage.push();

            }// end of while
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe);
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "", "",
            		loggerValue.toString() );
            BatchO2CFileProcessLog.o2cBatchMasterLog("closeOrederByBatch", p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(),
                "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "", "",
            		loggerValue.toString());
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs4 != null) {
                    rs4.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (rs5 != null) {
                    rs5.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }try {
                if (rs6 != null) {
                    rs6.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs7 != null) {
                    rs7.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs8 != null) {
                    rs8.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs9 != null) {
                    rs9.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs10 != null) {
                    rs10.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs11 != null) {
                    rs11.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadNetworkStock != null) {
                    pstmtLoadNetworkStock.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateNetworkStock != null) {
                    pstmtUpdateNetworkStock.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkDailyStock != null) {
                    pstmtInsertNetworkDailyStock.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectNetworkStock != null) {
                    pstmtSelectNetworkStock.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateSelectedNetworkStock != null) {
                    pstmtupdateSelectedNetworkStock.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransaction != null) {
                    pstmtInsertNetworkStockTransaction.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransactionItem != null) {
                    pstmtInsertNetworkStockTransactionItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserBalances != null) {
                    pstmtSelectUserBalances.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalances != null) {
                    pstmtUpdateUserBalances.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserDailyBalances != null) {
                    pstmtInsertUserDailyBalances.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalance != null) {
                    pstmtSelectBalance.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBalance != null) {
                    pstmtUpdateBalance.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBalance != null) {
                    pstmtInsertBalance.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTransferCounts != null) {
                    pstmtSelectTransferCounts.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectProfileCounts != null) {
                    pstmtSelectProfileCounts.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateTransferCounts != null) {
                    pstmtUpdateTransferCounts.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertTransferCounts != null) {
                    pstmtInsertTransferCounts.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr3FOCBatchItem != null) {
                    psmtAppr3FOCBatchItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadTransferProfileProduct != null) {
                    pstmtLoadTransferProfileProduct.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (handlerStmt != null) {
                    handlerStmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTransferItems != null) {
                    pstmtInsertIntoChannelTransferItems.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTranfers != null) {
                    pstmtInsertIntoChannelTranfers.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalanceInfoForMessage != null) {
                    pstmtSelectBalanceInfoForMessage.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
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
                        LOG.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN;
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
                    pstmtUpdateMaster.setString(m, PretupsI.CT_BATCH_O2C_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrederByBatch]",
                            "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
                loggerValue.append("SQLException:" );
                loggerValue.append(sqe);
                LOG.error(methodName, loggerValue );
                LOG.errorTrace(methodName, sqe);
                loggerValue.setLength(0);
                loggerValue.append("SQL Exception:" );
                loggerValue.append(sqe.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "",
                    "", loggerValue.toString() );
                BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
                loggerValue.append("Exception : " );
                loggerValue.append(ex);
                LOG.error(methodName,  loggerValue );
                LOG.errorTrace(methodName, ex);
                loggerValue.setLength(0);
                loggerValue.append("Exception : " );
                loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "",
                    "",  loggerValue.toString() );
                BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectItemsDetails != null) {
                    pstmtSelectItemsDetails.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateMaster != null) {
                    pstmtUpdateMaster.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
                loggerValue.append("Exiting: errorList size=" );
                loggerValue.append(errorList.size());
                LOG.debug(methodName, loggerValue );
            }
        }
        return errorList;
    }

    /**
     * 
     * @param p_con
     * @param p_batchId
     */
    public ArrayList o2cBatchClose(Connection p_con, FOCBatchMasterVO focBatchMasterVO, Map downloadDataMap, String p_batchId, String p_forwardPath, MessageResources p_messages, Locale p_locale, String p_currentLevel) throws BTSLBaseException {

        final String methodName = "o2cBatchClose";
        StringBuilder loggerValue= new StringBuilder(); 
	
		
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered p_batchId=");
            loggerValue.append(p_batchId );
            LOG.debug(methodName, loggerValue );
        }

        O2CBatchItemsVO batchItemsVO = null;
        ChannelUserVO channelUserVO = null;
        ListValueVO errorVO = null;
        ArrayList<ListValueVO> errorList = null;
        ArrayList productList = null;
        String productType = null;
        int updateCount = 0;
        final Date currentDate = new Date();
        String wallet_type = null;

        try {
            if (focBatchMasterVO != null) {
                productType = focBatchMasterVO.getProductType();
                final NetworkProductDAO networkProductDAO = new NetworkProductDAO();
                final String status = "'" + PretupsI.YES + "'";
                productList = networkProductDAO.loadProductList(p_con, "'" + productType + "'", status, null, focBatchMasterVO.getNetworkCode());
                wallet_type = focBatchMasterVO.getWallet_type();
            }
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

            if (downloadDataMap != null && downloadDataMap.size() > 0) {

                final Iterator iterator = downloadDataMap.keySet().iterator();

                errorList = new ArrayList<ListValueVO>();

                while (iterator.hasNext()) {

                    final String mapKey = (String) iterator.next();

                    batchItemsVO = (O2CBatchItemsVO) downloadDataMap.get(mapKey);
                    batchItemsVO.setWallet_type(wallet_type);
                    // Load the User Detail based on the MSISDN or LoginId
                    if (!BTSLUtil.isNullString(batchItemsVO.getMsisdn())) {
                        channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, batchItemsVO.getMsisdn());
                    } else if (!BTSLUtil.isNullString(batchItemsVO.getLoginId())) {
                        channelUserVO = channelUserDAO.loadChnlUserDetailsByLoginID(p_con, batchItemsVO.getLoginId());
                    }

                    if (channelUserVO != null) {
                        batchItemsVO.setUserStatus(channelUserVO.getStatus());
                        // user life cycle
                        boolean senderStatusAllowed = false;
                        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(),
                            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
                        if (userStatusVO != null) {
                            final String userStatusAllowed = userStatusVO.getUserSenderAllowed();
                            final String status[] = userStatusAllowed.split(",");
                            for (int i = 0; i < status.length; i++) {
                                if (status[i].equals(channelUserVO.getStatus())) {
                                    senderStatusAllowed = true;
                                }
                            }
                        } else {
                            throw new BTSLBaseException(this, "processOrderByBatch", "error.status.processing");
                        }
                        // (user status is checked) if this condition is true
                        // then made entry in logs and leave this data.
                        if (!senderStatusAllowed) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batcho2cwithdraw.batchapprovereject.msg.error.usersuspend"));
                            errorList.add(errorVO);
                            BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : User is suspend", "Approval level" + p_currentLevel);
                            continue;
                        }
                        // (commission profile status is checked) if this
                        // condition is true then made entry in logs and leave
                        // this data.
                        else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                            errorList.add(errorVO);
                            BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : Commission profile suspend",
                                "Approval level" + p_currentLevel);
                            continue;
                        }
                        // (transfer profile is checked) if this condition is
                        // true then made entry in logs and leave this data.
                        else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                            errorList.add(errorVO);
                            BatchO2CFileProcessLog
                                .detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : Transfer profile suspend", "Approval level" + p_currentLevel);
                            continue;
                        }
                        // (user in suspend is checked) if this condition is
                        // true then made entry in logs and leave this data.
                        else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                            errorList.add(errorVO);
                            BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                            continue;
                        } else if(BTSLUtil.isNullString(batchItemsVO.getStatus())){
		                	BatchO2CFileProcessLog.detailLog(methodName,focBatchMasterVO,batchItemsVO,"DISCARD : ",batchItemsVO.getBatchDetailId());
                            continue;
                        }

                        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                        final ArrayList list = ChannelTransferBL.loadO2CWdrProductList(p_con, productType, channelUserVO.getNetworkID(), channelUserVO
                            .getCommissionProfileSetID(), channelUserVO.getDomainID(), channelUserVO.getCategoryCode(), currentDate, p_forwardPath);

                        // prepares the ChannelTransferVO by populating its
                        // fields from the passed ChannelUserVO and filteredList
                        // of products
                        this.constructChannelTransferVO(batchItemsVO, channelTransferVO, currentDate, channelUserVO, list);

                        final boolean isSlabFlag = ChannelTransferBL.loadAndCalculateTaxOnProducts(p_con, batchItemsVO.getCommissionProfileSetId(), batchItemsVO
                            .getCommissionProfileVer(), channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);

                        if (!isSlabFlag) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batcho2c.initiateBatchO2CWithdraw.commissionprofile.product.notdefine"));
                            errorList.add(errorVO);
                            BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                            continue;

                        } else {
                            ChannelTransferBL.genrateWithdrawID(channelTransferVO);

                            

                            errorList = orderReturnedProcessStart(p_con, channelTransferVO, channelUserVO.getUserID(), currentDate, p_forwardPath, errorVO, batchItemsVO
                                .getMsisdn(), batchItemsVO.getRecordNumber(), p_messages, p_locale, errorList, p_currentLevel, batchItemsVO, focBatchMasterVO);

                            

                        }

                    }
                }

            }
        } catch (BTSLBaseException bbe) {
            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : DB Error while updating/inserting data into table",
                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
            LOG.error(methodName, "BTSLBaseException:e=");
            throw bbe;
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("SQLException : " );
            loggerValue.append(sqe );
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, sqe);
            
            loggerValue.setLength(0);
            loggerValue.append("SQLException : " );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[o2cBatchClose]", "", "", "",
            		loggerValue.toString() );
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : DB Error while updating/inserting data into table",
                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
            loggerValue.setLength(0);
            loggerValue.append("Exception : " );
            loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
        } finally {
            try {
                updateCount = updateO2CBatchStatus(p_con, currentDate, channelUserVO.getUserID(), batchItemsVO.getBatchId());

                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : DB Error while updating master table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[o2cBatchClose]", "", "",
                        "", "Error while updating FOC_BATCHES table. Batch id=" + batchItemsVO.getBatchId());
                }// end of if
                else {
                    p_con.commit();
                }
            } catch (Exception ex) {
            	loggerValue.setLength(0);
                loggerValue.append("Exception : " );
                loggerValue.append(ex);
                LOG.error(methodName, loggerValue );
                LOG.errorTrace(methodName, ex);
                loggerValue.setLength(0);
                loggerValue.append("Exception : " );
                loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[o2cBatchClose]", "", "", "",
                		loggerValue.toString() );
            }
            if (LOG.isDebugEnabled()) {
            	 loggerValue.setLength(0);
                 loggerValue.append("Exiting forward=");
                 loggerValue.append(errorList.size());
                LOG.debug(methodName, loggerValue );
            }
        }
        return errorList;
    }

    /**
     * 
     * @param p_con
     * @param p_batchId
     * @return FOCBatchMasterVO
     * @throws BTSLBaseException
     */
    private FOCBatchMasterVO loadO2CMasterDetail(Connection p_con, String p_batchId) throws BTSLBaseException {
        final String METHOD_NAME = "loadO2CMasterDetail";
        StringBuilder loggerValue= new StringBuilder();  
		
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered p_batchId=" );
            loggerValue.append(p_batchId );
            LOG.debug(METHOD_NAME,  loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FOCBatchMasterVO focBatchMasterVO = null;

        final StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT fb.batch_id,fb.batch_name,fb.batch_total_record,fb.created_by,p.product_name, ");
        strBuff.append(" p.short_name,p.unit_value,fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, ");
        strBuff.append(" fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang ");
        strBuff.append(" FROM foc_batches fb,products p ");
        strBuff.append(" WHERE fb.product_code=p.product_code AND fb.type='OB' AND fb.batch_id=? ");

        final String sqlSelect = strBuff.toString();

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlSelect=");
            loggerValue.append(sqlSelect );
            LOG.debug(METHOD_NAME,loggerValue );
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                focBatchMasterVO = new FOCBatchMasterVO();
                focBatchMasterVO.setBatchId(rs.getString("batch_id"));
                focBatchMasterVO.setBatchName(rs.getString("batch_name"));
                focBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                focBatchMasterVO.setCreatedBy(rs.getString("created_by"));
                focBatchMasterVO.setProductName(rs.getString("product_name"));
                focBatchMasterVO.setProductShortName(rs.getString("short_name"));
                focBatchMasterVO.setProductMrp(rs.getLong("unit_value"));

                focBatchMasterVO.setNetworkCode(rs.getString("network_code"));
                focBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
                focBatchMasterVO.setProductCode(rs.getString("product_code"));
                focBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
                focBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
                focBatchMasterVO.setProductType(rs.getString("product_type"));
                focBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                focBatchMasterVO.setBatchDate(rs.getDate("batch_date"));

                focBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
                focBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));

                focBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(focBatchMasterVO.getProductMrp()));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe );
            LOG.error(METHOD_NAME,  loggerValue );
            
            loggerValue.setLength(0);
            loggerValue.append( "SQL Exception:");
            loggerValue.append(sqe.getMessage() );
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[loadO2CMasterDetail]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append( "Exception:");
            loggerValue.append(ex );
            LOG.error(METHOD_NAME, loggerValue );
            
            loggerValue.setLength(0);
            loggerValue.append( "Exception:");
            loggerValue.append(ex.getMessage() );
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[loadO2CMasterDetail]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:");
            }
        }
        return focBatchMasterVO;
    }

    /**
     * 
     * @param p_channelTransferVO
     * @param p_curDate
     * @param p_channelUserVO
     * @param p_prdList
     * @return
     * @throws BTSLBaseException
     */
    private ChannelTransferVO constructChannelTransferVO(O2CBatchItemsVO batchItemsVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList) throws BTSLBaseException {
        ChannelTransferItemsVO channelTransferItemsVO;
        String productType = null;
        long totRequestQty = 0, totMRP = 0;
        final long totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        final long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append( "Entering  : p_channelTransferVO");
             loggerValue.append(p_channelTransferVO);
             loggerValue.append("p_channelUserVO" );
             loggerValue.append(p_channelUserVO);
            LOG.debug("prepareChannelTransferVO",loggerValue );
        }

        p_channelTransferVO.setBatchNum(batchItemsVO.getBatchId());
        p_channelTransferVO.setReferenceNum(batchItemsVO.getBatchDetailId());
        p_channelTransferVO.setCategoryCode(batchItemsVO.getCategoryCode());

        p_channelTransferVO.setFromUserID(batchItemsVO.getUserId());

        p_channelTransferVO.setModifiedBy(batchItemsVO.getModifiedBy());
        p_channelTransferVO.setModifiedOn(batchItemsVO.getModifiedOn());
        p_channelTransferVO.setSenderGradeCode(batchItemsVO.getGradeCode());

        p_channelTransferVO.setExternalTxnNum(batchItemsVO.getExtTxnNo());
        p_channelTransferVO.setExternalTxnDate(batchItemsVO.getExtTxnDate());

        p_channelTransferVO.setSenderTxnProfile(batchItemsVO.getTxnProfile());
        p_channelTransferVO.setCommProfileSetId(batchItemsVO.getCommissionProfileSetId());
        p_channelTransferVO.setCommProfileVersion(batchItemsVO.getCommissionProfileVer());
        p_channelTransferVO.setDualCommissionType(batchItemsVO.getDualCommissionType());
        p_channelTransferVO.setTotalTax1(batchItemsVO.getTax1Value());

        p_channelTransferVO.setTotalTax2(batchItemsVO.getTax2Value());

        p_channelTransferVO.setTotalTax3(batchItemsVO.getTax3Value());
        p_channelTransferVO.setRequestedQuantity(batchItemsVO.getRequestedQuantity());
        p_channelTransferVO.setTransferMRP(batchItemsVO.getTransferMrp());

        p_channelTransferVO.setChannelRemarks(batchItemsVO.getInitiatorRemarks());
        p_channelTransferVO.setFirstApprovalRemark(batchItemsVO.getFirstApproverRemarks());
        p_channelTransferVO.setSecondApprovalRemark(batchItemsVO.getSecondApproverRemarks());

        p_channelTransferVO.setFinalApprovedBy(batchItemsVO.getFirstApprovedBy());
        p_channelTransferVO.setFirstApprovedOn(batchItemsVO.getFirstApprovedOn());
        p_channelTransferVO.setSecondApprovedBy(batchItemsVO.getSecondApprovedBy());
        p_channelTransferVO.setSecondApprovedOn(batchItemsVO.getSecondApprovedOn());
        p_channelTransferVO.setCanceledBy(batchItemsVO.getCancelledBy());
        p_channelTransferVO.setCanceledOn(batchItemsVO.getCancelledOn());

        p_channelTransferVO.setCreatedBy(batchItemsVO.getInitiatedBy());
        p_channelTransferVO.setCreatedOn(p_curDate);

        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);

        p_channelTransferVO.setSenderGradeCode(p_channelUserVO.getUserGrade());

        p_channelTransferVO.setFromUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setTransferDate(p_curDate);

        p_channelTransferVO.setTransferInitatedBy(p_channelUserVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile(null);

        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);

        p_channelTransferVO.setActiveUserId(p_channelUserVO.getActiveUserID());
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());

        p_channelTransferVO.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
        p_channelTransferVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_WITHDRAW);

        for (int i = 0, k = p_prdList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_prdList.get(i);
            channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(batchItemsVO.getRequestedQuantity()));
            totRequestQty += batchItemsVO.getRequestedQuantity();
			if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            }

            productType = channelTransferItemsVO.getProductType();
        }// end of for

        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);

        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);

        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);

        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setCommQty(commissionQty);
        p_channelTransferVO.setSenderDrQty(senderDebitQty);
        p_channelTransferVO.setReceiverCrQty(receiverCreditQty);

        p_channelTransferVO.setControlTransfer(PretupsI.YES);
        p_channelTransferVO.setWalletType(batchItemsVO.getWallet_type());

        if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exiting .....  :p_channelTransferVO");
             loggerValue.append(p_channelTransferVO);
            LOG.debug("prepareChannelTransferVO",  loggerValue );
        }

        return p_channelTransferVO;

    }

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_userId
     * @param p_date
     * @param p_forwardPath
     * @throws BTSLBaseException
     * @throws SQLException
     */
    private ArrayList<ListValueVO> orderReturnedProcessStart(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date, String p_forwardPath, ListValueVO errorVO, String msisdn, int recordNumber, MessageResources p_messages, Locale p_locale, ArrayList<ListValueVO> errorList, String p_currentLevel, O2CBatchItemsVO batchItemsVO, FOCBatchMasterVO focBatchMasterVO) throws BTSLBaseException, SQLException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append( "Entered p_channelTransferVO  ");
             loggerValue.append(p_channelTransferVO);
             loggerValue.append(" p_userId ");
             loggerValue.append(p_userId);
             loggerValue.append(" p_date ");
             loggerValue.append(p_date);
             loggerValue.append(" p_forwardPath: ");
             loggerValue.append(p_forwardPath);
            LOG.debug("orderReturnedProcessStart",loggerValue );
        }
        final boolean credit = false;
        int updateCount = 0;

        ArrayList<ListValueVO> tempErrorList = new ArrayList<ListValueVO>();
        // prepare networkStockList credit the network stock
        updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date, credit);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);

        
        }
        updateCount = 0;
        updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userId, p_date);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
          
        }
        updateCount = 0;
        // update user daily balances
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            
        }
        updateCount = 0;
        // channel debit the user balances
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        tempErrorList = channelUserDAO.debitUserBalancesForO2C(p_con, p_channelTransferVO, msisdn, recordNumber, p_messages, p_locale);
        
        if (tempErrorList == null || tempErrorList.isEmpty()) {

            updateCount = 0;

            updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, p_forwardPath, p_date);

            if (updateCount <= 0) {
                errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                errorList.add(errorVO);

            } else {
                updateCount = 0;
                OneLineTXNLog.log(p_channelTransferVO, null);
                final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                updateCount = channelTransferDAO.addChannelTransferForO2C(p_con, p_channelTransferVO);

                if (updateCount <= 0) {

                    p_con.rollback();
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("orderReturnedProcessStart", focBatchMasterVO, batchItemsVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                } else {
                    updateCount = updateO2CStatus(p_con, p_date, p_currentLevel, batchItemsVO, p_userId, p_channelTransferVO.getTransferID());

                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("orderReturnedProcessStart", focBatchMasterVO, batchItemsVO, "FAIL : DB Error while updating items table",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    } else {

                        if (batchItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                            if (batchItemsVO.getUserStatus().equals(PretupsI.USER_STATUS_CHURN)) {
                                final int updatecount = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), batchItemsVO.getUserStatus(),
                                    PretupsI.STATUS_ACTIVE);
                                if (updatecount > 0) {
                                    p_con.commit();
                                    BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : Order is closed successfully",
                                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                                } else {
                                    p_con.rollback();
                                    throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.updating");
                                }
                            } else {
                                p_con.commit();
                                BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : Order is closed successfully",
                                    "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                            }

                        } else {
                            p_con.commit();
                        }
                    }
                }
            }

        } else {

            p_con.rollback();

            if (errorList == null || errorList.isEmpty()) {
                errorList = tempErrorList;
            } else {
                for (int p = 0, q = tempErrorList.size(); p < q; p++) {
                    final ListValueVO listValueVO = tempErrorList.get(p);
                    errorList.add(listValueVO);
                }
            }

        }
        

        if (LOG.isDebugEnabled()) {
            LOG.debug("orderReturnedProcessStart", "Exiting");
        }
        return errorList;
        // return tempErrorList;
    }

    /**
     * Method constructBalanceVOFromTxnVO.
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return UserBalancesVO
     */
    private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append( "Entered:NetworkStockTxnVO=>");
             loggerValue.append(p_channelTransferVO);
            LOG.debug("constructBalanceVOFromTxnVO",  loggerValue );
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
        userBalancesVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "Exiting userBalancesVO=");
            loggerValue.append(userBalancesVO);
            LOG.debug("constructBalanceVOFromTxnVO", loggerValue);
        }
        return userBalancesVO;
    }

    /**
     * 
     * @param p_con
     * @param p_currentLevel
     * @param o2cBatchItemVO
     * @param p_userID
     */
    private int updateO2CStatus(Connection p_con, Date p_date, String p_currentLevel, O2CBatchItemsVO o2cBatchItemVO, String p_userID, String p_TransferID) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        final String METHOD_NAME = "updateO2CStatus";
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "Entered p_userID=");
            loggerValue.append(p_userID);
            loggerValue.append(" p_TransferID=");
            loggerValue.append(p_TransferID);
            loggerValue.append("p_currentLevel=");
            loggerValue.append(p_currentLevel);
            loggerValue.append(" o2cBatchItemVO=");
            loggerValue.append(o2cBatchItemVO);
            LOG.debug(METHOD_NAME,  loggerValue);
        }
        StringBuilder sqlBuffer = null;
        int m = 0;
        int updateCount = 0;

        PreparedStatement psmtApprFOCBatchItem = null;

        // If current level of approval is 1 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");

        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, first_approved_by=?, first_approved_on=? ,");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, second_approved_by=? , second_approved_on=? ,");
        }
        sqlBuffer.append(" status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");

        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            sqlBuffer.append(" AND status IN (? , ? )  ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            sqlBuffer.append(" AND status = ?  ");
        }

        final String sqlApprvFOCBatchItems = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append( "QUERY sqlApprvFOCBatchItems=");
            loggerValue.append(sqlApprvFOCBatchItems);
            LOG.debug(METHOD_NAME,  loggerValue );
        }

        try {

            psmtApprFOCBatchItem = p_con.prepareStatement(sqlApprvFOCBatchItems);
            psmtApprFOCBatchItem.clearParameters();

            m = 0;
            ++m;
            psmtApprFOCBatchItem.setString(m, p_TransferID);
            ++m;
            psmtApprFOCBatchItem.setString(m, p_userID);
            ++m;
            psmtApprFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_date));

            // If level 1 apperoval then set parameters in psmtAppr1FOCBatchItem
            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                ++m;
                psmtApprFOCBatchItem.setString(m, o2cBatchItemVO.getFirstApproverRemarks());
                ++m;
                psmtApprFOCBatchItem.setString(m, p_userID);
                ++m;
                psmtApprFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_date));

            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                ++m;
                psmtApprFOCBatchItem.setString(m, o2cBatchItemVO.getSecondApproverRemarks());
                ++m;
                psmtApprFOCBatchItem.setString(m, p_userID);
                ++m;
                psmtApprFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_date));
            }
            ++m;
            psmtApprFOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
            ++m;
            psmtApprFOCBatchItem.setString(m, o2cBatchItemVO.getExtTxnNo());
            ++m;
            psmtApprFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getExtTxnDate()));
            ++m;
            psmtApprFOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());

            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                ++m;
                psmtApprFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                psmtApprFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                ++m;
                psmtApprFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            }

            updateCount = psmtApprFOCBatchItem.executeUpdate();

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
            loggerValue.append( "SQLException : ");
            loggerValue.append(sqe);
            LOG.error(METHOD_NAME,  loggerValue );
            LOG.errorTrace(METHOD_NAME, sqe);
            
            loggerValue.setLength(0);
            loggerValue.append( "SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[updateO2CStatus]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
            loggerValue.append( "Exception:");
            loggerValue.append(ex);
            LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
            loggerValue.append( "Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawDAO[updateO2CStatus]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (psmtApprFOCBatchItem != null) {
                    psmtApprFOCBatchItem.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
            	 loggerValue.setLength(0);
                 loggerValue.append("Exiting: updateCount=");
                 loggerValue.append(updateCount);
                LOG.debug(METHOD_NAME, loggerValue );
            }
        }

        return updateCount;

    }

    /**
     * 
     * @param p_con
     * @param p_date
     * @param p_userID
     * @param p_batchId
     * @return
     */
    private int updateO2CBatchStatus(Connection p_con, Date p_date, String p_userID, String p_batchId) {
    	StringBuilder loggerValue= new StringBuilder(); 
        final String METHOD_NAME = "updateO2CBatchStatus";
        if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append("Entered p_userID=");
             loggerValue.append(p_userID);
             loggerValue.append(" p_batchId=");
             loggerValue.append(p_batchId);
            LOG.debug(METHOD_NAME,  loggerValue );
        }

        PreparedStatement pstmtSelectItemsDetails = null;
        PreparedStatement pstmtUpdateMaster = null;

        StringBuilder sqlBuffer = null;
        ResultSet rs = null;
        int updateCount = 0;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
       
        
        final String selectItemsDetails = o2cBatchWithdrawQry.focBatchesSelectItemsDetailsQry();
       
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append("QUERY updateFOCBatches=");
             loggerValue.append(updateFOCBatches);
            LOG.debug("updateO2CStatus", loggerValue );
        }

        try {

            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);

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
            pstmtSelectItemsDetails.setString(m, p_batchId);

            rs = pstmtSelectItemsDetails.executeQuery();
            // Check the final status to be updated in master after processing
            // all records of batch
            if (rs.next()) {
                final int totalCount = rs.getInt("batch_total_record");
                final int closeCount = rs.getInt("closed");
                final int cnclCount = rs.getInt("cncl");

                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(METHOD_NAME, e);
                }

                String statusOfMaster = null;

                if (totalCount == cnclCount) {
                    statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CANCEL;
                } else if (totalCount == closeCount + cnclCount) {
                    statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE;
                } else {
                    statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN;
                }
                m = 0;
                ++m;
                pstmtUpdateMaster.setString(m, statusOfMaster);
                ++m;
                pstmtUpdateMaster.setString(m, p_userID);
                ++m;
                pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_date));
                ++m;
                pstmtUpdateMaster.setString(m, p_batchId);
                ++m;
                pstmtUpdateMaster.setString(m, PretupsI.CT_BATCH_O2C_STATUS_UNDERPROCESS);

                updateCount = pstmtUpdateMaster.executeUpdate();

            }// end of if

        } catch (SQLException sqle) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("SQLException ");
            loggerValue.append(updateFOCBatches);
            LOG.error(METHOD_NAME, loggerValue );
            LOG.errorTrace(METHOD_NAME, sqle);
            
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception ");
            loggerValue.append( sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkDailyStock]", "", "", "",
            		loggerValue.toString());

        }// end of catch
        catch (Exception e) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception ");
             loggerValue.append( e.getMessage());
            LOG.error(METHOD_NAME, loggerValue );
            LOG.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception ");
            loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkDailyStock]", "", "", "",
            		loggerValue.toString());

        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelectItemsDetails != null) {
                    pstmtSelectItemsDetails.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdateMaster != null) {
                    pstmtUpdateMaster.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
            	  loggerValue.setLength(0);
                  loggerValue.append("Exiting count = ");
                  loggerValue.append( updateCount);
                LOG.debug(METHOD_NAME,  loggerValue );
            }
        }
        return updateCount;
    }

    /**
     * Added by Praveen
     * This method will load the batches that are within the geography of user
     * whose userId is passed and batch id basis and mobile no basis.
     * with status(OPEN) also in items table for corresponding master record.
     * 
     * @Connection p_con
     * @String p_goeDomain
     * @String p_domain
     * @String p_productCode
     * @String p_batchid
     * @String p_msisdn
     * @Date p_fromDate
     * @Date p_toDate
     * @param p_loginID
     *            TODO
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchO2CMasterDetails(Connection p_con, String p_goeDomain, String p_domain, String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID, String p_trfType, String p_trfSubType) throws BTSLBaseException {
        final String METHOD_NAME = "loadBatchO2CMasterDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append( "Entered p_goeDomain=");
             loggerValue.append( p_goeDomain);
             loggerValue.append(" p_domain=");
             loggerValue.append(p_domain);
             loggerValue.append(" p_productCode=");
             loggerValue.append(p_productCode);
             loggerValue.append(" p_batchid=");
             loggerValue.append(p_batchid);
             loggerValue.append(" p_msisdn=");
             loggerValue.append(p_msisdn);
             loggerValue.append(" p_fromDate=");
             loggerValue.append(p_fromDate);
             loggerValue.append(" p_toDate=");
             loggerValue.append(p_toDate);
             loggerValue.append( " p_loginID=");
             loggerValue.append(p_loginID);
             loggerValue.append(" p_trfType=" );
             loggerValue.append(p_trfType);
             loggerValue.append(" p_trfSubType=");
             loggerValue.append(p_trfSubType);;
            LOG.debug( METHOD_NAME,loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = o2cBatchWithdrawQry.loadBatchO2CMasterDetailsQry(p_batchid, p_msisdn, p_goeDomain, p_domain, p_productCode);
        
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            ++i;
            pstmt.setString(i, p_trfType);
            ++i;
            pstmt.setString(i, p_trfSubType);
            ++i;
            pstmt.setString(i, p_loginID);
            if (p_batchid != null) {
                ++i;
                pstmt.setString(i, p_batchid);
            } else if (p_msisdn != null) {
                ++i;
                pstmt.setString(i, p_msisdn);
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=");
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                	loggerValue.append(" BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=");
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    LOG.debug(METHOD_NAME,loggerValue);
                }
            } else {
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=");
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate) );
                	loggerValue.append( " BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=");
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    LOG.debug( METHOD_NAME,loggerValue );
                }
            }
            rs = pstmt.executeQuery();
            FOCBatchMasterVO batchO2CMasterVO = null;
            while (rs.next()) {
                batchO2CMasterVO = new FOCBatchMasterVO();
                batchO2CMasterVO.setBatchId(rs.getString("batch_id"));
                batchO2CMasterVO.setDomainCode(rs.getString("domain_code"));
                batchO2CMasterVO.setBatchName(rs.getString("batch_name"));
                batchO2CMasterVO.setProductName(rs.getString("product_name"));
                batchO2CMasterVO.setProductMrp(rs.getLong("unit_value"));
                batchO2CMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
                batchO2CMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                batchO2CMasterVO.setNewRecords(rs.getInt("new"));
                batchO2CMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
                batchO2CMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
                batchO2CMasterVO.setClosedRecords(rs.getInt("closed"));
                batchO2CMasterVO.setRejectedRecords(rs.getInt("cncl"));
                batchO2CMasterVO.setBatchDate(rs.getDate("batch_date"));
                if (batchO2CMasterVO.getBatchDate() != null) {
                    batchO2CMasterVO.setBatchDateStr(BTSLUtil.getDateStringFromDate(batchO2CMasterVO.getBatchDate()));
                }
                list.add(batchO2CMasterVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe );
            LOG.error(METHOD_NAME, loggerValue );
            LOG.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage() );
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, "loadBatchFOCMasterDetails", "error.general.sql.processing");
        } catch (Exception ex) {
        	  loggerValue.setLength(0);
          	loggerValue.append("Exception : ");
          	loggerValue.append(ex);
            LOG.error(METHOD_NAME, loggerValue );
            LOG.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
          	loggerValue.append("Exception : ");
          	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
            	 loggerValue.setLength(0);
               	loggerValue.append("Exiting: focBatchMasterVOList size=");
               	loggerValue.append(list.size());
                LOG.debug(METHOD_NAME,  loggerValue );
            }
        }
        return list;
    }

    /**
     * This method load Batch details according to batch id.
     * loadBatchDetailsList
     * 
     * @param p_con
     *            Connection
     * @param p_batchId
     *            String
     * @return ArrayList list
     * @throws BTSLBaseException
     *             ved.sharma
     */
    public ArrayList loadBatchDetailsList(Connection p_con, String p_batchId) throws BTSLBaseException {
        final String METHOD_NAME = "loadBatchDetailsList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
           	loggerValue.append("Entered p_batchId=");
           	loggerValue.append(p_batchId);
            LOG.debug(METHOD_NAME,  loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        final String sqlSelect = o2cBatchWithdrawQry.loadBatchDetailsListQry();
      
        FOCBatchMasterVO batchO2CMasterVO = null;
        FOCBatchItemsVO batchO2CItemsVO = null;
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                batchO2CMasterVO = new FOCBatchMasterVO();
                batchO2CMasterVO.setBatchId(rs.getString("batch_id"));
                batchO2CMasterVO.setBatchName(rs.getString("batch_name"));
                batchO2CMasterVO.setStatus(rs.getString("status"));
                batchO2CMasterVO.setDomainCode(rs.getString("domain_code"));
                batchO2CMasterVO.setDomainCodeDesc(rs.getString("domain_name"));
                batchO2CMasterVO.setProductCode(rs.getString("product_code"));
                batchO2CMasterVO.setProductCodeDesc(rs.getString("product_name"));
                batchO2CMasterVO.setBatchFileName(rs.getString("batch_file_name"));
                batchO2CMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                batchO2CMasterVO.setBatchDate(rs.getDate("batch_date"));
                batchO2CMasterVO.setCreatedBy(rs.getString("initated_by"));
                batchO2CMasterVO.setCreatedOn(rs.getTimestamp("created_on"));
                batchO2CMasterVO.setStatus(rs.getString("status"));
                batchO2CMasterVO.setStatusDesc(rs.getString("status_desc"));

                batchO2CItemsVO = new FOCBatchItemsVO();
                batchO2CItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
                batchO2CItemsVO.setUserName(rs.getString("user_name"));
                batchO2CItemsVO.setExternalCode(rs.getString("external_code"));
                batchO2CItemsVO.setMsisdn(rs.getString("msisdn"));
                batchO2CItemsVO.setCategoryName(rs.getString("category_name"));
                batchO2CItemsVO.setCategoryCode(rs.getString("category_code"));
                batchO2CItemsVO.setStatus(rs.getString("status_item"));
                batchO2CItemsVO.setUserGradeCode(rs.getString("user_grade_code"));
                batchO2CItemsVO.setGradeCode(rs.getString("user_grade_code"));
                batchO2CItemsVO.setGradeName(rs.getString("grade_name"));
                batchO2CItemsVO.setReferenceNo(rs.getString("reference_no"));
                batchO2CItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchO2CItemsVO.setExtTxnDate(rs.getDate("ext_txn_date"));
                if (batchO2CItemsVO.getExtTxnDate() != null) {
                    batchO2CItemsVO.setExtTxnDateStr(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate()));
                }
                batchO2CItemsVO.setTransferDate(rs.getDate("transfer_date"));
                if (batchO2CItemsVO.getTransferDate() != null) {
                    batchO2CItemsVO.setTransferDateStr(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getTransferDate()));
                }
                batchO2CItemsVO.setTxnProfile(rs.getString("txn_profile"));
                batchO2CItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                batchO2CItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                batchO2CItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                batchO2CItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                batchO2CItemsVO.setCommissionType(rs.getString("commission_type"));
                batchO2CItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                batchO2CItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                batchO2CItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                batchO2CItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                batchO2CItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                batchO2CItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                batchO2CItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                batchO2CItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                batchO2CItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                batchO2CItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                batchO2CItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                batchO2CItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));

                batchO2CMasterVO.setFocBatchItemsVO(batchO2CItemsVO);

                list.add(batchO2CMasterVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
           	loggerValue.append("SQLException : ");
           	loggerValue.append(sqe);
            LOG.error(METHOD_NAME,  loggerValue );
            LOG.errorTrace(METHOD_NAME, sqe);
            
            loggerValue.setLength(0);
           	loggerValue.append("SQL Exception:");
           	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchDetailsList]", "", "",
                "",  loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
            	loggerValue.append("Exception:");
            	loggerValue.append(ex);
            LOG.error(METHOD_NAME, loggerValue );
            LOG.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchDetailsList]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (LOG.isDebugEnabled()) {
            	 loggerValue.setLength(0);
             	loggerValue.append("Exiting: loadBatchDetailsList  list.size()=" );
             	loggerValue.append(list.size());
                LOG.debug(METHOD_NAME,loggerValue );
            }
        }
        return list;
    }
}
