package com.web.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchO2CFileProcessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainQry;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.o2c.service.O2CBatchWithdrawController;
import com.web.pretups.channel.transfer.web.O2CBatchWithdrawForm;
import com.btsl.user.businesslogic.UserLoanVO;

public class O2CBatchWithdrawWebDAO {

    /**
     * Field _log.
     */
    private static Log _log = LogFactory.getLog(O2CBatchWithdrawWebDAO.class.getName());
    private O2CBatchWithdrawWebQry o2cBatchWithdrawWebQry = (O2CBatchWithdrawWebQry)ObjectProducer.getObject(QueryConstants.O2C_BATCH_WD_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    
    /**
     * Constructor for GeographicalDomainDAO.
     */
    public O2CBatchWithdrawWebDAO() {
    	super();
        }
    private static OperatorUtilI operatorUtili = null;
   

    /**
     * Method initiateBatchO2CTransfer
     * This method used for the batch foc order initiation. The main purpose of
     * this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     * 
     * @param p_con
     *            Connection
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_batchItemsList
     *            ArrayList
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList initiateBatchO2CTransfer(Connection p_con, FOCBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String methodName = "initiateBatchO2CTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered.... p_batchMasterVO=" + p_batchMasterVO + ", p_batchItemsList.size() = " + p_batchItemsList.size() + ", p_batchItemsList=" + p_batchItemsList + "p_locale=" + p_locale);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID2 Query =" + strBuffSelectExtTxnID2);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2
        // OraclePreparedStatement pstmtInsertBatchMaster = null;
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type,type,txn_wallet) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        // OraclePreparedStatement pstmtInsertBatchItems = null;//commented for
        // DB2
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,dual_comm_type) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchItems Query =" + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE foc_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = p_con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = p_con.prepareStatement(strBuffSelectExtTxnID2.toString());
            // pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());//commented
            // for DB2
            pstmtInsertBatchMaster = p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = p_con.prepareStatement(strBuffInsertBatchGeo.toString());
            // pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());//commented
            // for DB2
            pstmtInsertBatchItems = p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = p_con.prepareStatement(strBuffUpdateBatchMaster.toString());

            final ChannelTransferRuleVO rulesVO = null;
            int index = 0;
            O2CBatchItemsVO batchItemsVO = null;

            // insert the master data
            index = 0;
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCodeFor());

            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchName());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getStatus());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDomainCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getProductCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchFileName());
            ++index;
            pstmtInsertBatchMaster.setLong(index, p_batchMasterVO.getBatchTotalRecord());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getCreatedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getModifiedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));

            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDefaultLang());
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getSecondLang());

            // added by praveen
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
            ++index;
            pstmtInsertBatchMaster.setString(index, "OB");
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getWallet_type());

            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                p_con.rollback();
                _log.error(methodName, "Unable to insert in the batch master table.");
                BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]",
                    "", "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            // ends here
            // insert batch geographics
            ListValueVO listValueVO = null;
            final int size = p_batchMasterVO.getGeographyList().size();
            for (int i = 0; i < size; i++) {
                index = 0;
                listValueVO = (ListValueVO) p_batchMasterVO.getGeographyList().get(i);
                ++index;
                pstmtInsertBatchGeo.setString(index, p_batchMasterVO.getBatchId());
                ++index;
                pstmtInsertBatchGeo.setString(index, listValueVO.getValue());
                // Added on 07/02/08
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    _log.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            final String msgArr[] = null;
            for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
                batchItemsVO = (O2CBatchItemsVO) p_batchItemsList.get(i);
                
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && isExternalTxnUnique) {
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
                        errorVO.setOtherInfo2(PretupsRestUtil
        						.getMessageString("batcho2c.initiatebatchO2Ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist O2C BATCCH", "");
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
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
        						.getMessageString("batcho2c.initiatebatchO2Ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
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
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getRequiredQuantity());
                ++index;
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getProductTotalMRP());
          
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    // put error record can not be inserted
                    _log.error(methodName, "Record cannot be inserted in batch items table");
                    BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    p_con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }
            // for loop for the batch items
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog
                .o2cBatchMasterLog(methodName, p_batchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", "Exception:" + ex.getMessage());
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_batchMasterVO, "FAIL : Exception:" + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsSelectExtTxnID1 != null) {
                    rsSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID1 != null) {
                    pstmtSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectExtTxnID2 != null) {
                    rsSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID2 != null) {
                    pstmtSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // try{if (rsSelectTrfRule != null){rsSelectTrfRule.close();}} catch
            // (Exception e){}
            // try{if (pstmtSelectTrfRule != null){pstmtSelectTrfRule.close();}}
            // catch (Exception e){}
            // try{if (rsSelectTrfRuleProd !=
            // null){rsSelectTrfRuleProd.close();}} catch (Exception e){}
            // try{if (pstmtSelectTrfRuleProd !=
            // null){pstmtSelectTrfRuleProd.close();}} catch (Exception e){}
            // try{if (rsSelectCProfileProd !=
            // null){rsSelectCProfileProd.close();}} catch (Exception e){}
            // try{if (pstmtSelectCProfileProd !=
            // null){pstmtSelectCProfileProd.close();}} catch (Exception e){}
            // try{if (rsSelectCProfileProdDetail !=
            // null){rsSelectCProfileProdDetail.close();}} catch (Exception e){}
            // try{if (pstmtSelectCProfileProdDetail !=
            // null){pstmtSelectCProfileProdDetail.close();}} catch (Exception
            // e){}
            // try{if (rsSelectTProfileProd !=
            // null){rsSelectTProfileProd.close();}} catch (Exception e){}
            // try{if (pstmtSelectTProfileProd !=
            // null){pstmtSelectTProfileProd.close();}} catch (Exception e){}
            try {
                if (pstmtInsertBatchMaster != null) {
                    pstmtInsertBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchGeo != null) {
                    pstmtInsertBatchGeo.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchItems != null) {
                    pstmtInsertBatchItems.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {

                // if all records contains errors then rollback the master table
                // entry
                if (errorList != null && (errorList.size() == p_batchItemsList.size())) {
                    p_con.rollback();
                    _log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, p_batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, p_batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        _log.error(methodName, "Unable to Update the batch size in master table..");
                        p_con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + p_batchMasterVO.getBatchId());
                    } else {
                        p_con.commit();
                    }
                }

            } catch (Exception e) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBatchMaster != null) {
                    pstmtUpdateBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
            }
        }
        return errorList;
    }






    /**
     * Method initiateBatchO2CTransfer
     * This method used for the batch foc order initiation. The main purpose of
     * this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     *
     * @param p_con
     *            Connection
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_batchItemsList
     *            ArrayList
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList initiateBatchO2CTransferRest(Connection p_con, O2CBatchWithdrawForm p_theForm,UserVO p_userVO,FOCBatchMasterVO p_batchMasterVO, List p_batchItemsList, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String methodName = "initiateBatchO2CTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug(
                    methodName,
                    "Entered.... p_batchMasterVO=" + p_batchMasterVO + ", p_batchItemsList.size() = " + p_batchItemsList.size() + ", p_batchItemsList=" + p_batchItemsList + "p_locale=" + p_locale);
        }
        final Date currentDate = new Date();
        O2CBatchWithdrawController batchWithdrawController = new O2CBatchWithdrawController();
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID2 Query =" + strBuffSelectExtTxnID2);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2
        // OraclePreparedStatement pstmtInsertBatchMaster = null;
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
                .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type,type,txn_wallet) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        // OraclePreparedStatement pstmtInsertBatchItems = null;//commented for
        // DB2
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,dual_comm_type) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchItems Query =" + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE foc_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = p_con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = p_con.prepareStatement(strBuffSelectExtTxnID2.toString());
            // pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());//commented
            // for DB2
            pstmtInsertBatchMaster = p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = p_con.prepareStatement(strBuffInsertBatchGeo.toString());
            // pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());//commented
            // for DB2
            pstmtInsertBatchItems = p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = p_con.prepareStatement(strBuffUpdateBatchMaster.toString());

            final ChannelTransferRuleVO rulesVO = null;
            int index = 0;
            O2CBatchItemsVO batchItemsVO = null;

            // insert the master data
            index = 0;
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCodeFor());

            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchName());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getStatus());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDomainCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getProductCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchFileName());
            ++index;
            pstmtInsertBatchMaster.setLong(index, p_batchMasterVO.getBatchTotalRecord());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getCreatedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getModifiedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));

            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDefaultLang());
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
            // for DB2
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getSecondLang());

            // added by praveen
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
            ++index;
            pstmtInsertBatchMaster.setString(index, "OB");
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getWallet_type());

            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                p_con.rollback();
                _log.error(methodName, "Unable to insert in the batch master table.");
                BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                        "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]",
                        "", "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            // ends here
            // insert batch geographics
            ListValueVO listValueVO = null;
            final int size = p_batchMasterVO.getGeographyList().size();
            for (int i = 0; i < size; i++) {
                index = 0;
                listValueVO = (ListValueVO) p_batchMasterVO.getGeographyList().get(i);
                ++index;
                pstmtInsertBatchGeo.setString(index, p_batchMasterVO.getBatchId());
                ++index;
                pstmtInsertBatchGeo.setString(index, listValueVO.getValue());
                // Added on 07/02/08
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    _log.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                            "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            final String msgArr[] = null;
            ArrayList filteredPrdList = null;
            ChannelTransferItemsVO channelTransferItemsVO = null;
            ChannelTransferVO channelTransferVO = null;
            ChannelUserVO channelUserVO = null;
            HashMap<Object, Object> productMap = null;
            for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
                batchItemsVO = (O2CBatchItemsVO) p_batchItemsList.get(i);



                channelUserVO = batchItemsVO.getChannelUserVO();

                channelTransferVO = new ChannelTransferVO();
                productMap = new HashMap<Object, Object>();

                // the HashMap which contains the product code as key and
                // the corresponding quantity as value

                productMap.put(p_theForm.getProductShortCode(), batchItemsVO.getRequestedQuantity());

                channelTransferVO.setExternalTxnNum(batchItemsVO.getExtTxnNo());
                channelTransferVO.setExternalTxnDate(batchItemsVO.getExtTxnDate());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_WITHDRAW);

                channelTransferVO.setChannelRemarks(batchItemsVO.getInitiatorRemarks());

                // this method returns the list of products after filtering
                // it with the products associated with
                // the commission profile and the transfer rule associated
                // with the user
                try
                {
                    filteredPrdList = ChannelTransferBL.loadAndValidateProducts(p_con, productMap, channelUserVO, false);
                }
                catch (BTSLBaseException e) {
                    // TODO: handle exception
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), RestAPIStringParser.getMessage(p_locale, e.getMessageKey(),new String[] {""} ));
                    errorList.add(errorVO);
                    continue;
                }
                // make a new channel TransferVO to transfer into the method
                // during tax calculataion

                //Changes made for handling decimal quantity
                for (int i1 = 0, k = filteredPrdList.size(); i1 < k; i1++) {
                    channelTransferItemsVO = (ChannelTransferItemsVO) filteredPrdList.get(i1);
                    channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(Double.parseDouble(channelTransferItemsVO.getRequestedQuantity())));
                    filteredPrdList.add(channelTransferItemsVO);
                }
                channelTransferVO.setChannelTransferitemsVOList(filteredPrdList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
                channelTransferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
                channelTransferVO.setPaymentInstType(PretupsI.ALL);
                final boolean isSlabFlag = ChannelTransferBL.loadAndCalculateTaxOnProducts(p_con, channelUserVO.getCommissionProfileSetID(), channelUserVO
                        .getCommissionProfileSetVersion(), channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);

                if (!isSlabFlag) {

                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
                            .getMessageString("batcho2c.initiateBatchO2CWithdraw.commissionprofile.product.notdefine"));
                    errorList.add(errorVO);
                    continue;
                } else {

                    // prepares the ChannelTransferVO by populating its
                    // fields from the passed ChannelUserVO and filteredList
                    // of products
                    batchWithdrawController.prepareChannelTransferVO(channelTransferVO, currentDate, channelUserVO, filteredPrdList, p_userVO);

                    // setting the transfer id for each
                    // channelTransferItemsVO
                    for (int k = 0, l = filteredPrdList.size(); k < l; k++) {
                        channelTransferItemsVO = (ChannelTransferItemsVO) filteredPrdList.get(k);
                        channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
                    }// end of for

                    batchItemsVO.setChannelTransferItemsVO(channelTransferItemsVO);
                    // added by gaurav.
                    batchItemsVO.setWallet_type(p_theForm.getWalletType());

                }


                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && isExternalTxnUnique) {
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
                        errorVO.setOtherInfo2(PretupsRestUtil
                                .getMessageString("batcho2c.initiatebatchO2Ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist O2C BATCCH", "");
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
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil
                                .getMessageString("batcho2c.initiatebatchO2Ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
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
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getRequiredQuantity());
                ++index;
                pstmtInsertBatchItems.setLong(index, batchItemsVO.getChannelTransferItemsVO().getProductTotalMRP());

                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    // put error record can not be inserted
                    _log.error(methodName, "Record cannot be inserted in batch items table");
                    BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                            "queryExecutionCount=" + queryExecutionCount);
                } else {
                    p_con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchO2CFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                            "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }
            // for loop for the batch items
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog
                    .o2cBatchMasterLog(methodName, p_batchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "",
                    "", "", "Exception:" + ex.getMessage());
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_batchMasterVO, "FAIL : Exception:" + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsSelectExtTxnID1 != null) {
                    rsSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID1 != null) {
                    pstmtSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectExtTxnID2 != null) {
                    rsSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID2 != null) {
                    pstmtSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // try{if (rsSelectTrfRule != null){rsSelectTrfRule.close();}} catch
            // (Exception e){}
            // try{if (pstmtSelectTrfRule != null){pstmtSelectTrfRule.close();}}
            // catch (Exception e){}
            // try{if (rsSelectTrfRuleProd !=
            // null){rsSelectTrfRuleProd.close();}} catch (Exception e){}
            // try{if (pstmtSelectTrfRuleProd !=
            // null){pstmtSelectTrfRuleProd.close();}} catch (Exception e){}
            // try{if (rsSelectCProfileProd !=
            // null){rsSelectCProfileProd.close();}} catch (Exception e){}
            // try{if (pstmtSelectCProfileProd !=
            // null){pstmtSelectCProfileProd.close();}} catch (Exception e){}
            // try{if (rsSelectCProfileProdDetail !=
            // null){rsSelectCProfileProdDetail.close();}} catch (Exception e){}
            // try{if (pstmtSelectCProfileProdDetail !=
            // null){pstmtSelectCProfileProdDetail.close();}} catch (Exception
            // e){}
            // try{if (rsSelectTProfileProd !=
            // null){rsSelectTProfileProd.close();}} catch (Exception e){}
            // try{if (pstmtSelectTProfileProd !=
            // null){pstmtSelectTProfileProd.close();}} catch (Exception e){}
            try {
                if (pstmtInsertBatchMaster != null) {
                    pstmtInsertBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchGeo != null) {
                    pstmtInsertBatchGeo.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchItems != null) {
                    pstmtInsertBatchItems.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {

                // if all records contains errors then rollback the master table
                // entry
                if (errorList != null && (errorList.size() == p_batchItemsList.size())) {
                    p_con.rollback();
                    _log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, p_batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, p_batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, p_batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        _log.error(methodName, "Unable to Update the batch size in master table..");
                        p_con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                                "O2CBatchTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + p_batchMasterVO.getBatchId());
                    } else {
                        p_con.commit();
                    }
                }

            } catch (Exception e) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBatchMaster != null) {
                    pstmtUpdateBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
            }
        }
        return errorList;
    }



    /**
     * Method for loading O2CBatch details..
     * This method will load the batches that are within the geography of user
     * whose userId is passed
     * with status(OPEN) also in items table for corresponding master record the
     * status is in p_itemStatus
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_itemStatus
     *            String
     * @param p_currentLevel
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchO2CMasterDetails(Connection p_con, String p_userID, String p_itemStatus, String p_currentLevel) throws BTSLBaseException {
        final String methodName = "loadBatchO2CMasterDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID=" + p_userID + " p_itemStatus=" + p_itemStatus + " p_currentLevel=" + p_currentLevel);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();

        strBuff
            .append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record,fb.created_by,p.product_name,p.short_name,p.unit_value,sum(case fbi.status when ? then 1 else 0 end) as new, ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end) cncl,  ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed,  ");
        strBuff
            .append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on , fb.txn_wallet ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang ");
        strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud, ");
        strBuff.append(" user_product_types upt,geographical_domains gd  ");
        strBuff.append(" WHERE ug.user_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
        strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
        strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
        strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
        strBuff.append(" AND fb.product_code=p.product_code  ");
        strBuff.append("AND fb.type='OB' ");
        strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN (" + p_itemStatus + ") ");
        strBuff
            .append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
        strBuff
            .append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.created_by,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang, fb.txn_wallet,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date ORDER BY fb.batch_date DESC ) qry ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            strBuff.append(" WHERE  new>0 OR  appr1>0 ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            strBuff.append(" WHERE  appr1>0 OR appr2>0 ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            strBuff.append(" WHERE appr2>0 ");
        }

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList<FOCBatchMasterVO> list = new ArrayList<FOCBatchMasterVO>();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, p_userID);
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
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[loadBatchO2CMasterDetails]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[loadBatchO2CMasterDetails]",
                "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: o2cBatchMasterVOList size=" + list.size());
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
     * @param p_con
     * @param p_batchId
     * @param p_itemStatus
     * @return
     * @throws BTSLBaseException
     */
    public Map loadBatchItemsMap(Connection p_con, String p_batchId, String p_itemStatus) throws BTSLBaseException {
        final String methodName = "loadBatchItemsMap";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_batchId=" + p_batchId + " p_itemStatus=" + p_itemStatus);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
        strBuff.append(" fbi.third_approved_on,fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
        strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
        strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on,fbi.third_approved_by, ");
        strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
        strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
        // strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
        strBuff.append(" fbi.initiator_remarks, fbi.first_approver_remarks, ");
        strBuff.append(" fbi.third_approver_remarks, fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
        strBuff.append(" fbi.third_approved_on, fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
        strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
        strBuff.append(" fbi.second_approved_on, fbi.third_approved_by, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
        strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
        strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
        strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value,fbi.bonus_type,fbi.dual_comm_type ");
        strBuff.append(" FROM foc_batch_items fbi left join users fapp on fbi.first_approved_by = fapp.user_id left join users sapp on fbi.second_approved_by = sapp.user_id , ");
        strBuff.append(" foc_batches fb left join users intu on fb.created_by = intu.user_id,categories c,channel_grades cg, users u");
        strBuff.append(" WHERE fb.batch_id=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
        strBuff.append(" fbi.user_grade_code=cg.grade_code");
        strBuff
            .append(" AND fbi.status in(" + p_itemStatus + ") AND fbi.rcrd_status=? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final Map map = new HashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                final O2CBatchItemsVO batchItemsVO = new O2CBatchItemsVO();

                batchItemsVO.setBatchId(p_batchId);
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
                batchItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
                batchItemsVO.setLoginId(rs.getString("login_id"));
                batchItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
                batchItemsVO.setModifiedBy(rs.getString("modified_by"));
                batchItemsVO.setReferenceNo(rs.getString("reference_no"));
                batchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchItemsVO.setExtTxnDate(rs.getTimestamp("ext_txn_date"));
                batchItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
                batchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                batchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                batchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                batchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                batchItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
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

                // batchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                // batchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                batchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                batchItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                batchItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                batchItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                batchItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                batchItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                batchItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                batchItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                batchItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                batchItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                batchItemsVO.setCancelledBy(rs.getString("cancelled_by"));
                batchItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
                batchItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
                batchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                batchItemsVO.setCategoryCode(rs.getString("category_code"));
                batchItemsVO.setFirstApproverName(rs.getString("first_approver_name"));
                batchItemsVO.setSecondApproverName(rs.getString("second_approver_name"));
                batchItemsVO.setInitiaterName(rs.getString("initiater_name"));
                batchItemsVO.setExternalCode(rs.getString("external_code"));

                // batchItemsVO.setBonusType(rs.getString("bonus_type"));
                map.put(rs.getString("batch_detail_id"), batchItemsVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadBatchItemsList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: loadBatchItemsMap map=" + map.size());
            }
        }
        return map;
    }
    
    
    
    
    
    /**
     * Thiis methid will load the data from the foc_batch_items table
     * corresponding to batch id.
     * The result will be returned as LinkedHasMap. The key will be
     * batch_detail_id for this map.
     * 
     * @param p_con
     * @param p_batchId
     * @param p_itemStatus
     * @return
     * @throws BTSLBaseException
     */
    public Map loadBatchItemsMapBatchO2CItemsVO(Connection p_con, String p_batchId, String p_itemStatus) throws BTSLBaseException {
        final String methodName = "loadBatchItemsMap";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_batchId=" + p_batchId + " p_itemStatus=" + p_itemStatus);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
        strBuff.append(" fbi.third_approved_on,fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
        strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
        strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on,fbi.third_approved_by, ");
        strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
        strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
        // strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
        strBuff.append(" fbi.initiator_remarks, fbi.first_approver_remarks, ");
        strBuff.append(" fbi.third_approver_remarks, fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
        strBuff.append(" fbi.third_approved_on, fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
        strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
        strBuff.append(" fbi.second_approved_on, fbi.third_approved_by, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
        strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
        strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
        strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value,fbi.bonus_type,fbi.dual_comm_type ");
        strBuff.append(" FROM foc_batch_items fbi left join users fapp on fbi.first_approved_by = fapp.user_id left join users sapp on fbi.second_approved_by = sapp.user_id , ");
        strBuff.append(" foc_batches fb left join users intu on fb.created_by = intu.user_id,categories c,channel_grades cg, users u");
        strBuff.append(" WHERE fb.batch_id=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
        strBuff.append(" fbi.user_grade_code=cg.grade_code");
        strBuff
            .append(" AND fbi.status in(" + p_itemStatus + ") AND fbi.rcrd_status=? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final Map map = new HashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                final BatchO2CItemsVO batchItemsVO = new BatchO2CItemsVO();

                batchItemsVO.setBatchId(p_batchId);
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
                batchItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
                batchItemsVO.setLoginID(rs.getString("login_id"));
                batchItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
                batchItemsVO.setModifiedBy(rs.getString("modified_by"));
                batchItemsVO.setReferenceNo(rs.getString("reference_no"));
                batchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchItemsVO.setExtTxnDate(rs.getTimestamp("ext_txn_date"));
                batchItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
                batchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                batchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                batchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                batchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                batchItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
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

                // batchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                // batchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                batchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                batchItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                batchItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                batchItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                batchItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                batchItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                batchItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                batchItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                batchItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                batchItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                batchItemsVO.setCancelledBy(rs.getString("cancelled_by"));
                batchItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
                batchItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
                batchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                batchItemsVO.setCategoryCode(rs.getString("category_code"));
                batchItemsVO.setFirstApproverName(rs.getString("first_approver_name"));
                batchItemsVO.setSecondApproverName(rs.getString("second_approver_name"));
                batchItemsVO.setInitiaterName(rs.getString("initiater_name"));
                batchItemsVO.setExternalCode(rs.getString("external_code"));

                // batchItemsVO.setBonusType(rs.getString("bonus_type"));
                map.put(rs.getString("batch_detail_id"), batchItemsVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadBatchItemsList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: loadBatchItemsMap map=" + map.size());
            }
        }
        return map;
    }

    
    

    /**
     * To Check whether batch is modidfied or not
     * 
     * @param p_con
     * @param p_oldlastModified
     * @param p_batchID
     * @return
     * @throws BTSLBaseException
     */
    public boolean isBatchModified(Connection p_con, long p_oldlastModified, String p_batchID) throws BTSLBaseException {
        final String methodName = "isBatchModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_batchID=" + p_batchID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM foc_batches WHERE batch_id = ? ";
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "sqlRecordModified=" + sqlRecordModified);
            }
            pstmtSelect = p_con.prepareStatement(sqlRecordModified);
            pstmtSelect.setString(1, p_batchID);
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
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[isBatchModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[isBatchModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
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
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * updateBatchStatus
     * This method is to update the status of FOC_BATCHES table
     * 
     * @param p_con
     * @param p_batchID
     * @param p_newStatus
     * @param p_oldStatus
     * @return
     * @throws BTSLBaseException
     *             boolean
     */
    public int updateBatchStatus(Connection p_con, String p_batchID, String p_newStatus, String p_oldStatus) throws BTSLBaseException {
        final String methodName = "updateBatchStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered   p_batchID " + p_batchID + " p_newStatus=" + p_newStatus + " p_oldStatus=" + p_oldStatus);
        }
        PreparedStatement pstmt = null;
        int updateCount = -1;
        try {
            final StringBuilder sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? ");
            sqlBuffer.append(" WHERE batch_id=? AND status=? ");
            final String updateFOCBatches = sqlBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
            }

            pstmt = p_con.prepareStatement(updateFOCBatches);

            pstmt.setString(1, p_newStatus);
            pstmt.setString(2, p_batchID);
            pstmt.setString(3, p_oldStatus);

            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[updateBatchStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[updateBatchStatus]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:  updateCount=" + updateCount);
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
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList processOrderByBatch(Connection p_con, Map p_dataMap, String p_currentLevel, String p_userID, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_dataMap=" + p_dataMap + " p_currentLevel=" + p_currentLevel + " p_locale=" + p_locale + " p_userID=" + p_userID);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        PreparedStatement pstmtLoadUser = null;
        // commented for DB2
        // OraclePreparedStatement psmtCancelFOCBatchItem = null;
        // OraclePreparedStatement psmtAppr1FOCBatchItem = null;
        // OraclePreparedStatement psmtAppr2FOCBatchItem = null;
        // OraclePreparedStatement pstmtUpdateMaster= null;
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
        ResultSet rs4 = null;
        int updateCount = 0;
        String batch_ID = null;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuilder sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.network_code,u.category_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        final String sqlLoadUser = sqlBuffer.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
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

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlCancelFOCBatchItems=" + sqlCancelFOCBatchItems);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
        sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end) as new,");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end ) cncl, ");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed ");
        sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
        sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
        final String selectItemsDetails = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum1=" + isExistsTxnNum1);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
        }
        sqlBuffer = null;
        Date date = null;
        try {
            O2CBatchItemsVO o2cBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            // psmtCancelFOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlCancelFOCBatchItems);//commented
            // for DB2
            // psmtAppr1FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv1FOCBatchItems);//commented
            // for DB2
            // psmtAppr2FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv2FOCBatchItems);//commented
            // for DB2
            psmtCancelFOCBatchItem = p_con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateFOCBatches);//commented
            // for DB2
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
                o2cBatchItemVO = (O2CBatchItemsVO) p_dataMap.get(key);
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
                    channelUserVO = ChannelUserVO.getInstance();
                    channelUserVO.setUserID(o2cBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
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
                        throw new BTSLBaseException(this, "processOrderByBatch", "error.status.processing");
                    }
                    if (!senderStatusAllowed) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User is not active", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
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
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog
                        .o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog
                        .o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (isExternalTxnUnique && !BTSLUtil.isNullString(o2cBatchItemVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
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
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : External transaction number already exists BATCH FOC",
                            "Approval level" + p_currentLevel);
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
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelFOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(o2cBatchItemVO.getStatus())) {
                    psmtCancelFOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
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
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(o2cBatchItemVO.getStatus())) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr1FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
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
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(o2cBatchItemVO.getStatus())) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr2FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
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
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : DB Error while updating items table",
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
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "", "",
                "Exception:" + ex.getMessage());
            BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
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
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelFOCBatchItem != null) {
                    psmtCancelFOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
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
                rs4 = pstmtSelectItemsDetails.executeQuery();
                if (rs4.next()) {
                    final int totalCount = rs4.getInt("batch_total_record");
                    final int closeCount = rs4.getInt("closed");
                    final int cnclCount = rs4.getInt("cncl");
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
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]",
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
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "SQLException : " + sqe);
                _log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.sql.processing");
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());
                BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }finally{
            	try {
                    if (rs4 != null) {
                        rs4.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }	
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * 
     * @param p_con
     * @param p_batchId
     */
    public ArrayList o2cBatchClose(Connection p_con, FOCBatchMasterVO focBatchMasterVO, Map downloadDataMap, String p_batchId, String p_forwardPath, MessageResources p_messages, Locale p_locale, String p_currentLevel, String userId) throws BTSLBaseException {

        final String methodName = "o2cBatchClose";
        if (_log.isDebugEnabled()) {

            _log.debug(methodName, "Entered p_batchId=" + p_batchId);
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
                            BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : Transfer profile suspend", "Approval level" + p_currentLevel);
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
                        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equalsIgnoreCase(batchItemsVO.getStatus())) {
                            BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "CANCEL : ", batchItemsVO.getBatchDetailId());
                            continue;
                        }

                        final ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
                        final ArrayList list = ChannelTransferBL.loadO2CWdrProductList(p_con, productType, channelUserVO.getNetworkID(), channelUserVO
                            .getCommissionProfileSetID(), channelUserVO.getDomainID(), channelUserVO.getCategoryCode(), currentDate, p_forwardPath);

                        // prepares the ChannelTransferVO by populating its
                        // fields from the passed ChannelUserVO and filteredList
                        // of products
                        this.constructChannelTransferVO(batchItemsVO, channelTransferVO, currentDate, channelUserVO, list);

                        // ChannelTransferBL.loadAndCalculateTaxOnProducts(p_con,
                        // batchItemsVO.getCommissionProfileSetId(),
                        // batchItemsVO.getCommissionProfileVer(),channelTransferVO,true,p_forwardPath,PretupsI.TRANSFER_TYPE_O2C);

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

                            // ChannelTransferDAO channelTransferDAO = new
                            // ChannelTransferDAO();
                            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(batchItemsVO.getStatus())) {
                                errorList = orderReturnedProcessStart(p_con, channelTransferVO, channelUserVO.getUserID(), currentDate, p_forwardPath, errorVO, batchItemsVO
                                    .getMsisdn(), batchItemsVO.getRecordNumber(), p_messages, p_locale, errorList, p_currentLevel, batchItemsVO, focBatchMasterVO, userId);
                            }

                            /*
                             * if(errorList==null || errorList.isEmpty()){
                             * 
                             * updateCount =
                             * channelTransferDAO.addChannelTransferForO2C
                             * (p_con,channelTransferVO);
                             * 
                             * if(updateCount > 0 )
                             * {
                             * updateCount = updateO2CStatus(p_con,currentDate,
                             * p_currentLevel
                             * ,batchItemsVO,channelUserVO.getUserID
                             * (),channelTransferVO.getTransferID());
                             * 
                             * //(record not updated properly) if this condition
                             * is true then made entry in logs and leave this
                             * data.
                             * if(updateCount<=0)
                             * {
                             * p_con.rollback();
                             * errorVO=new
                             * ListValueVO(batchItemsVO.getMsisdn(),String
                             * .valueOf
                             * (batchItemsVO.getRecordNumber()),p_messages
                             * .getMessage(p_locale,
                             * "batchfoc.batchapprovereject.msg.error.recordnotupdated"
                             * ));
                             * errorList.add(errorVO);
                             * BatchO2CFileProcessLog.detailLog("o2cBatchClose",
                             * focBatchMasterVO,batchItemsVO,
                             * "FAIL : DB Error while updating items table"
                             * ,"Approval level = "
                             * +p_currentLevel+", updateCount="+updateCount);
                             * continue;
                             * }
                             * p_con.commit();
                             * ChannelTransferBL.prepareUserBalancesListForLogger
                             * (channelTransferVO);
                             * }else{
                             * p_con.rollback();
                             * errorVO=new
                             * ListValueVO(batchItemsVO.getMsisdn(),String
                             * .valueOf
                             * (batchItemsVO.getRecordNumber()),p_messages
                             * .getMessage(p_locale,
                             * "batcho2c.batchapprovereject.msg.error.recordnotupdated"
                             * ));
                             * errorList.add(errorVO);
                             * BatchO2CFileProcessLog.detailLog("o2cBatchClose",
                             * focBatchMasterVO,batchItemsVO,
                             * "FAIL : DB Error while inserting in channel transfer table"
                             * ,
                             * "Approval level = "+p_currentLevel+", updateCount="
                             * +updateCount);
                             * continue;
                             * }
                             * p_con.commit();
                             * }else{
                             * p_con.rollback();
                             * }
                             */

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
            _log.error(methodName, "BTSLBaseException:e=");
            throw bbe;

        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[o2cBatchClose]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            // TODO: handle exception
            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : DB Error while updating/inserting data into table",
                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

            _log.error(methodName, "Exception:e=" + e.getMessage());
            _log.errorTrace(methodName, e);
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
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[o2cBatchClose]", "",
                        "", "", "Error while updating FOC_BATCHES table. Batch id=" + batchItemsVO.getBatchId());
                }// end of if
                else {
                    p_con.commit();
                }

            } catch (Exception ex) {
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[o2cBatchClose]", "", "",
                    "", "Exception:" + ex.getMessage());
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting forward=" + errorList.size());
            }
        }

        return errorList;
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
        ChannelTransferItemsVO channelTransferItemsVO = null;
        String productType = null;
        long totRequestQty = 0, totMRP = 0;
        final long totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        final long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        boolean userLoanEnable = (boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE);
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Entering  : p_channelTransferVO" + p_channelTransferVO + "p_channelUserVO" + p_channelUserVO);
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
        p_channelTransferVO.setFirstApprovedBy(batchItemsVO.getFirstApprovedBy());
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
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);

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
        if(userLoanEnable) {
			
				p_channelTransferVO.setUserLoanVOList(p_channelUserVO.getUserLoanVOList());
		
        }
		else if(isChannelSOSEnable){
        	ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<ChannelSoSVO> ();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getMsisdn(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting .....  :p_channelTransferVO" + p_channelTransferVO);
        }

        return p_channelTransferVO;

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
    private ChannelTransferVO constructChannelTransferVO(BatchO2CItemsVO batchItemsVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList) throws BTSLBaseException {
        ChannelTransferItemsVO channelTransferItemsVO = null;
        String productType = null;
        long totRequestQty = 0, totMRP = 0;
        final long totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        final long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        Boolean isChannelSOSEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Entering  : p_channelTransferVO" + p_channelTransferVO + "p_channelUserVO" + p_channelUserVO);
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
        p_channelTransferVO.setFirstApprovedBy(batchItemsVO.getFirstApprovedBy());
        p_channelTransferVO.setFirstApprovedOn(batchItemsVO.getFirstApprovedOn());
        p_channelTransferVO.setSecondApprovedBy(batchItemsVO.getInitiatedBy());
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
        p_channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);

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
        p_channelTransferVO.setWalletType(batchItemsVO.getWalletType());
        
        if(isChannelSOSEnable){
        	ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<ChannelSoSVO> ();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getMsisdn(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting .....  :p_channelTransferVO" + p_channelTransferVO);
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
    private ArrayList<ListValueVO> orderReturnedProcessStartRest(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date, ListValueVO errorVO, String msisdn, int recordNumber, Locale p_locale, ArrayList<ListValueVO> errorList, String p_currentLevel, O2CBatchItemsVO batchItemsVO, FOCBatchMasterVO focBatchMasterVO, String userId) throws BTSLBaseException, SQLException {
        if (_log.isDebugEnabled()) {
            _log.debug("orderReturnedProcessStart",
                "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId " + p_userId + " p_date " + p_date );
        }


        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    
        final boolean credit = false;
        int updateCount = 0;
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        Boolean isO2CBatchWithdrwaMessageAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED);
        Boolean isAdminMessageReqd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ADMIN_MESSAGE_REQD);
        ArrayList<ListValueVO> tempErrorList = new ArrayList<ListValueVO>();
        // prepare networkStockList credit the network stock
        updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date, credit);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString( "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);

            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, userId, p_date);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        // update user daily balances
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        // channel debit the user balances
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        tempErrorList = channelUserDAO.debitUserBalancesForO2CRest(p_con, p_channelTransferVO, msisdn, recordNumber, p_locale);
        // errorList =
        // channelUserDAO.debitUserBalancesForO2C(p_con,p_channelTransferVO,msisdn,recordNumber,p_messages,p_locale,errorList);

        // if(errorList==null || errorList.isEmpty())
        if (tempErrorList == null || tempErrorList.isEmpty()) {

            updateCount = 0;

            updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, "myRestO2CReturn", p_date);

            if (updateCount <= 0) {
                errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                errorList.add(errorVO);

            } else {
                updateCount = 0;
                OneLineTXNLog.log(p_channelTransferVO, null);
                final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                updateCount = channelTransferDAO.addChannelTransferForO2C(p_con, p_channelTransferVO);

                if (updateCount <= 0) {

                    p_con.rollback();
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.detailLog("orderReturnedProcessStart", focBatchMasterVO, batchItemsVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                } else {
                    updateCount = updateO2CStatus(p_con, p_date, p_currentLevel, batchItemsVO, userId, p_channelTransferVO.getTransferID());

                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("orderReturnedProcessStart", focBatchMasterVO, batchItemsVO, "FAIL : DB Error while updating items table",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        // continue;
                    } else {
                        p_con.commit();
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : Order is closed successfully",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                        if (batchItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                            boolean statusChangeRequired = false;
                            if (!PretupsI.USER_STATUS_ACTIVE.equals(batchItemsVO.getUserStatus())) {
                                // int
                                // updatecount=operatorUtili.changeUserStatusToActive(
                                // p_con,p_channelTransferVO.getFromUserID(),batchItemsVO.getUserStatus());
                                int updatecount = 0;
                                final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                                String newStatus[] = null;
                                for (int i = 0; i < str.length; i++) {
                                    newStatus = str[i].split(":");
                                    if (newStatus[0].equals(batchItemsVO.getUserStatus())) {
                                        statusChangeRequired = true;
                                        updatecount = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), batchItemsVO.getUserStatus(),
                                            newStatus[1]);
                                        break;
                                    }
                                }
                                if (statusChangeRequired) {
                                    if (updatecount > 0) {
                                        p_con.commit();
                                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : user status changed  successfully",
                                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                                    } else {
                                        p_con.rollback();
                                        throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.updating");
                                    }
                                }
                            }
                          //Added for message pushing as per GP requirement
                            if(isAdminMessageReqd) {
		            			//Added for message pushing as per GP requirement
			            		if(isO2CBatchWithdrwaMessageAllowed){
                                    UserPhoneVO primaryPhoneVO=null;
                                    UserDAO userDAO=new UserDAO();
                                    ChannelUserVO channelUserVO= null;
                                    String _msisdnString=null;
                                    try
                                    {
                                           _msisdnString=new String(Constants.getProperty("adminmobile"));
                                           if(_log.isDebugEnabled())_log.debug("refresh","_msisdnString: "+_msisdnString);
                                    }
                                    catch(Exception e5)
                                    {
                                           _log.error("refresh","adminmobile is not defined in Constant.props, cannot use default");
                                    }
                                    if(!BTSLUtil.isNullString(batchItemsVO.getMsisdn())){
                                           channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, batchItemsVO.getMsisdn());
                                    }else if(!BTSLUtil.isNullString(batchItemsVO.getLoginId())){
                                           channelUserVO= channelUserDAO.loadChnlUserDetailsByLoginID(p_con, batchItemsVO.getLoginId());
                                    }
                                    if( BTSLUtil.isNullString(_msisdnString))
                                    {   
                                    	_msisdnString="Admin User";
                                    }
                                                  ArrayList productList=p_channelTransferVO.getChannelTransferitemsVOList();
                                                  ChannelTransferItemsVO channelTransferItemsVO = null;
                                                  channelTransferItemsVO = (ChannelTransferItemsVO)productList.get(0);
                                                  Locale locale= new Locale(defaultLanguage,defaultCountry);
                                                  String[] array= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),batchItemsVO.getMsisdn(),p_channelTransferVO.getTransferID(),_msisdnString};//,loginUserVO.getUserID()
                                                  BTSLMessages messages=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_ADMIN_MESSAGE,array);
                                                  String[] array1= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),_msisdnString,p_channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()-channelTransferItemsVO.getApprovedQuantity())};//,loginUserVO.getUserID()
                                                  BTSLMessages messages1=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_USER_MESSAGE,array1);
                                                  PushMessage  pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages1,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                                  pushMessage.push();
                                                  PushMessage pushMessage1=null;
                                                  if(channelUserVO.getAlertMsisdn()!=null)
                                                  {
                                                         String [] msisdn1 =channelUserVO.getAlertMsisdn().split(";");
                                                         for(int l=0,len=msisdn1.length; l<len;l++)
                                                         {      
                                                                pushMessage1=new PushMessage(msisdn1[l],messages,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                                                pushMessage1.push();
                                                         }
                                                  }
                                         
                                    
                              }
		            		}else {
		            			if(isO2CBatchWithdrwaMessageAllowed){
		            				Locale locale= new Locale(defaultLanguage,defaultCountry);
		            				ArrayList productList=p_channelTransferVO.getChannelTransferitemsVOList();
                                    ChannelTransferItemsVO channelTransferItemsVO = null;
                                    channelTransferItemsVO = (ChannelTransferItemsVO)productList.get(0);
		            				String[] array1= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),"Admin",p_channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()-channelTransferItemsVO.getApprovedQuantity())};//,loginUserVO.getUserID()
                                    BTSLMessages messages1=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_USER_MESSAGE,array1);
                                    PushMessage  pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages1,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                    pushMessage.push();
		            			}
		            			
		            		}
                        }
                    }
                    // p_con.commit();
                     ChannelTransferBL.prepareUserBalancesListForLogger(p_channelTransferVO);
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
        /*
         * updateCount = updateO2CStatus(p_con,p_date,
         * p_currentLevel,batchItemsVO
         * ,p_userId,p_channelTransferVO.getTransferID());
         * 
         * //(record not updated properly) if this condition is true then made
         * entry in logs and leave this data.
         * if(updateCount<=0)
         * {
         * errorVO=new
         * ListValueVO(msisdn,String.valueOf(recordNumber),p_messages
         * .getMessage(
         * p_locale,"batchfoc.batchapprovereject.msg.error.recordnotupdated"));
         * errorList.add(errorVO);
         * 
         * }
         */

        if (_log.isDebugEnabled()) {
            _log.debug("orderReturnedProcessStart", "Exiting");
        }
        return errorList;
        // return tempErrorList;
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
    private ArrayList<ListValueVO> orderReturnedProcessStartRest(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date, ListValueVO errorVO, String msisdn, int recordNumber, Locale p_locale, ArrayList<ListValueVO> errorList, String p_currentLevel, BatchO2CItemsVO batchItemsVO, FOCBatchMasterVO focBatchMasterVO, String userId) throws BTSLBaseException, SQLException {
        if (_log.isDebugEnabled()) {
            _log.debug("orderReturnedProcessStart",
                "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId " + p_userId + " p_date " + p_date );
        }


        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    
        final boolean credit = false;
        int updateCount = 0;
        String txnReceiverUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        Boolean isO2CBatchWithdrwaMessageAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED);
        Boolean isAdminMessageReqd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ADMIN_MESSAGE_REQD);
        ArrayList<ListValueVO> tempErrorList = new ArrayList<ListValueVO>();
        // prepare networkStockList credit the network stock
        updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date, credit);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString( "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);

            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, userId, p_date);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        // update user daily balances
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        // channel debit the user balances
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        tempErrorList = channelUserDAO.debitUserBalancesForO2CRest(p_con, p_channelTransferVO, msisdn, recordNumber, p_locale);
        // errorList =
        // channelUserDAO.debitUserBalancesForO2C(p_con,p_channelTransferVO,msisdn,recordNumber,p_messages,p_locale,errorList);

        // if(errorList==null || errorList.isEmpty())
        if (tempErrorList == null || tempErrorList.isEmpty()) {

            updateCount = 0;

            updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, "myRestO2CReturn", p_date);

            if (updateCount <= 0) {
                errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                errorList.add(errorVO);

            } else {
                updateCount = 0;
                OneLineTXNLog.log(p_channelTransferVO, null);
                final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
                updateCount = channelTransferDAO.addChannelTransferForO2C(p_con, p_channelTransferVO);

                if (updateCount <= 0) {

                    p_con.rollback();
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                  //  BatchO2CFileProcessLog.detailLog("orderReturnedProcessStart", focBatchMasterVO, batchItemsVO, "FAIL : DB Error while inserting in channel transfer table",
                    //    "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                } else {
                    updateCount = updateO2CStatus(p_con, p_date, p_currentLevel, batchItemsVO, userId, p_channelTransferVO.getTransferID());

                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                       // BatchO2CFileProcessLog.detailLog("orderReturnedProcessStart", focBatchMasterVO, batchItemsVO, "FAIL : DB Error while updating items table",
                         //   "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        // continue;
                    } else {
                        p_con.commit();
                      //  BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : Order is closed successfully",
                        //    "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                        if (batchItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                            boolean statusChangeRequired = false;
                            if (!PretupsI.USER_STATUS_ACTIVE.equals(batchItemsVO.getUserStatus())) {
                                // int
                                // updatecount=operatorUtili.changeUserStatusToActive(
                                // p_con,p_channelTransferVO.getFromUserID(),batchItemsVO.getUserStatus());
                                int updatecount = 0;
                                final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                                String newStatus[] = null;
                                for (int i = 0; i < str.length; i++) {
                                    newStatus = str[i].split(":");
                                    if (newStatus[0].equals(batchItemsVO.getUserStatus())) {
                                        statusChangeRequired = true;
                                        updatecount = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), batchItemsVO.getUserStatus(),
                                            newStatus[1]);
                                        break;
                                    }
                                }
                                if (statusChangeRequired) {
                                    if (updatecount > 0) {
                                        p_con.commit();
                                     //   BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : user status changed  successfully",
                                       //     "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                                    } else {
                                        p_con.rollback();
                                        throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.updating");
                                    }
                                }
                            }
                          //Added for message pushing as per GP requirement
                            if(isAdminMessageReqd) {
		            			//Added for message pushing as per GP requirement
			            		if(isO2CBatchWithdrwaMessageAllowed){
                                    UserPhoneVO primaryPhoneVO=null;
                                    UserDAO userDAO=new UserDAO();
                                    ChannelUserVO channelUserVO= null;
                                    String _msisdnString=null;
                                    try
                                    {
                                           _msisdnString=new String(Constants.getProperty("adminmobile"));
                                           if(_log.isDebugEnabled())_log.debug("refresh","_msisdnString: "+_msisdnString);
                                    }
                                    catch(Exception e5)
                                    {
                                           _log.error("refresh","adminmobile is not defined in Constant.props, cannot use default");
                                    }
                                    if(!BTSLUtil.isNullString(batchItemsVO.getMsisdn())){
                                           channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, batchItemsVO.getMsisdn());
                                    }else if(!BTSLUtil.isNullString(batchItemsVO.getLoginID())){
                                           channelUserVO= channelUserDAO.loadChnlUserDetailsByLoginID(p_con, batchItemsVO.getLoginID());
                                    }
                                    if( BTSLUtil.isNullString(_msisdnString))
                                    {   
                                    	_msisdnString="Admin User";
                                    }
                                                  ArrayList productList=p_channelTransferVO.getChannelTransferitemsVOList();
                                                  ChannelTransferItemsVO channelTransferItemsVO = null;
                                                  channelTransferItemsVO = (ChannelTransferItemsVO)productList.get(0);
                                                  Locale locale= new Locale(defaultLanguage,defaultCountry);
                                                  String[] array= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),batchItemsVO.getMsisdn(),p_channelTransferVO.getTransferID(),_msisdnString};//,loginUserVO.getUserID()
                                                  BTSLMessages messages=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_ADMIN_MESSAGE,array);
                                                  String[] array1= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),_msisdnString,p_channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()-channelTransferItemsVO.getApprovedQuantity())};//,loginUserVO.getUserID()
                                                  BTSLMessages messages1=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_USER_MESSAGE,array1);
                                                  PushMessage  pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages1,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                                  pushMessage.push();
                                                  PushMessage pushMessage1=null;
                                                  if(channelUserVO.getAlertMsisdn()!=null)
                                                  {
                                                         String [] msisdn1 =channelUserVO.getAlertMsisdn().split(";");
                                                         for(int l=0,len=msisdn1.length; l<len;l++)
                                                         {      
                                                                pushMessage1=new PushMessage(msisdn1[l],messages,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                                                pushMessage1.push();
                                                         }
                                                  }
                                         
                                    
                              }
		            		}else {
		            			if(isO2CBatchWithdrwaMessageAllowed){
		            				Locale locale= new Locale(defaultLanguage,defaultCountry);
		            				ArrayList productList=p_channelTransferVO.getChannelTransferitemsVOList();
                                    ChannelTransferItemsVO channelTransferItemsVO = null;
                                    channelTransferItemsVO = (ChannelTransferItemsVO)productList.get(0);
		            				String[] array1= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),"Admin",p_channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()-channelTransferItemsVO.getApprovedQuantity())};//,loginUserVO.getUserID()
                                    BTSLMessages messages1=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_USER_MESSAGE,array1);
                                    PushMessage  pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages1,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                    pushMessage.push();
		            			}
		            			
		            		}
                        }
                    }
                    // p_con.commit();
                     ChannelTransferBL.prepareUserBalancesListForLogger(p_channelTransferVO);
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
        /*
         * updateCount = updateO2CStatus(p_con,p_date,
         * p_currentLevel,batchItemsVO
         * ,p_userId,p_channelTransferVO.getTransferID());
         * 
         * //(record not updated properly) if this condition is true then made
         * entry in logs and leave this data.
         * if(updateCount<=0)
         * {
         * errorVO=new
         * ListValueVO(msisdn,String.valueOf(recordNumber),p_messages
         * .getMessage(
         * p_locale,"batchfoc.batchapprovereject.msg.error.recordnotupdated"));
         * errorList.add(errorVO);
         * 
         * }
         */

        if (_log.isDebugEnabled()) {
            _log.debug("orderReturnedProcessStart", "Exiting");
        }
        return errorList;
        // return tempErrorList;
    }

    /**
     * 
     * @param p_con
     * @param p_currentLevel
     * @param o2cBatchItemVO
     * @param p_userID
     */
    private int updateO2CStatus(Connection p_con, Date p_date, String p_currentLevel, O2CBatchItemsVO o2cBatchItemVO, String p_userID, String p_TransferID) throws BTSLBaseException {

        final String methodName = "updateO2CStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID=" + p_userID + " p_TransferID=" + p_TransferID + "p_currentLevel=" + p_currentLevel + " o2cBatchItemVO=" + o2cBatchItemVO);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprvFOCBatchItems=" + sqlApprvFOCBatchItems);
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
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[updateO2CStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[updateO2CStatus]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            try {
                if (psmtApprFOCBatchItem != null) {
                    psmtApprFOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }

        return updateCount;

    }
    
    
    
    
    
    /**
     * 
     * @param p_con
     * @param p_currentLevel
     * @param o2cBatchItemVO
     * @param p_userID
     */
    private int updateO2CStatus(Connection p_con, Date p_date, String p_currentLevel, BatchO2CItemsVO o2cBatchItemVO, String p_userID, String p_TransferID) throws BTSLBaseException {

        final String methodName = "updateO2CStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID=" + p_userID + " p_TransferID=" + p_TransferID + "p_currentLevel=" + p_currentLevel + " o2cBatchItemVO=" + o2cBatchItemVO);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprvFOCBatchItems=" + sqlApprvFOCBatchItems);
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
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[updateO2CStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[updateO2CStatus]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            try {
                if (psmtApprFOCBatchItem != null) {
                    psmtApprFOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
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

        final String methodName = "updateO2CBatchStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID=" + p_userID + " p_batchId=" + p_batchId);
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
        sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end ) as new,");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end) cncl, ");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed ");
        sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
        sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
        final String selectItemsDetails = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("updateO2CStatus", "QUERY updateFOCBatches=" + updateFOCBatches);
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
                    _log.errorTrace(methodName, e);
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
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkDailyStock]", "", "", "",
                "SQL Exception:" + sqle.getMessage());

        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "NetworkStockDAO[updateNetworkDailyStock]", "", "", "",
                "Exception:" + e.getMessage());

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
                if (pstmtSelectItemsDetails != null) {
                    pstmtSelectItemsDetails.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateMaster != null) {
                    pstmtUpdateMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting count = " + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method constructBalanceVOFromTxnVO.
     * 
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @return UserBalancesVO
     */
    private UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_channelTransferVO);
        }
        final UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setUserID(p_channelTransferVO.getFromUserID());
        userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
        userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
        userBalancesVO.setLastTransferOn(p_channelTransferVO.getModifiedOn());
        userBalancesVO.setUserMSISDN(p_channelTransferVO.getFromUserCode());

        if (_log.isDebugEnabled()) {
            _log.debug("constructBalanceVOFromTxnVO", "Exiting userBalancesVO=" + userBalancesVO);
        }
        return userBalancesVO;
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
        final String methodName = "loadBatchO2CMasterDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered p_goeDomain=" + p_goeDomain + " p_domain=" + p_domain + " p_productCode=" + p_productCode + " p_batchid=" + p_batchid + " p_msisdn=" + p_msisdn + " p_fromDate=" + p_fromDate + " p_toDate=" + p_toDate + " p_loginID=" + p_loginID + " p_trfType=" + p_trfType + " p_trfSubType=" + p_trfSubType);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = o2cBatchWithdrawWebQry.loadBatchO2CMasterDetailsQry(p_goeDomain, p_domain, p_productCode, p_batchid, p_msisdn);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
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
                if (_log.isDebugEnabled()) {
                    _log.debug(
                        methodName,
                        "QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil.getSQLDateFromUtilDate(p_toDate) + " BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil
                            .getSQLDateFromUtilDate(p_toDate));
                }
            } else {
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if (_log.isDebugEnabled()) {
                    _log.debug(
                        methodName,
                        "QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil.getSQLDateFromUtilDate(p_toDate) + " BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil
                            .getSQLDateFromUtilDate(p_toDate));
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
                    batchO2CMasterVO.setBatchDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CMasterVO.getBatchDate())));
                }
                list.add(batchO2CMasterVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadBatchFOCMasterDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: focBatchMasterVOList size=" + list.size());
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
        final String methodName = "loadBatchDetailsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_batchId=" + p_batchId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = o2cBatchWithdrawWebQry.loadBatchDetailsListQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
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

                batchO2CItemsVO = FOCBatchItemsVO.getInstance();
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
                    batchO2CItemsVO.setExtTxnDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getExtTxnDate())));
                }
                batchO2CItemsVO.setTransferDate(rs.getDate("transfer_date"));
                if (batchO2CItemsVO.getTransferDate() != null) {
                    batchO2CItemsVO.setTransferDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(batchO2CItemsVO.getTransferDate())));
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
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchDetailsList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchDetailsList]", "", "",
                "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: loadBatchDetailsList  list.size()=" + list.size());
            }
        }
        return list;
    }
    /**
     * Method validateUsersForBatchO2C.
     * This method the loads the user list for Batch O2C withdraw transfer
     * 
     * @param p_con
     *            Connection
     * @param p_batchO2CItemsVOList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param p_comPrfApplicableDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author rajeev.kumar2
     */

    public ArrayList validateUsersForBatchO2CWithdraw(Connection p_con, ArrayList p_batchO2CItemsVOList, String p_domainCode, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String METHOD_NAME = "validateUsersForBatchO2CWithdraw";
        if (_log.isDebugEnabled()) {
            _log.debug(
                METHOD_NAME,
                "Entered p_batchO2CItemsVOList.size()=" + p_batchO2CItemsVOList.size() + ", p_domainCode=" + p_domainCode + "Category Code " + p_categoryCode + " Network Code " + p_networkCode + " p_geographicalDomainCode: " + p_geographicalDomainCode );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        if(!p_networkCode.equals("mock"))
        {
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_categoryCode.replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
        }
        }

        final String sqlSelect = o2cBatchWithdrawWebQry.validateUsersForBatchO2CWithdrawQry(p_categoryCode, receiverStatusAllowed, p_geographicalDomainCode);
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList errorList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int index = 0;
            O2CBatchItemsVO batchO2CItemsVO = null;
            ListValueVO errorVO = null;
            boolean fileValidationErrorExists = false;
            for (int i = 0, j = p_batchO2CItemsVOList.size(); i < j; i++) {
                batchO2CItemsVO = (O2CBatchItemsVO) p_batchO2CItemsVOList.get(i);
                index = 0;
                ++index;
                pstmt.setString(index, batchO2CItemsVO.getMsisdn());
                ++index;
                pstmt.setString(index, p_networkCode);
                ++index;
                pstmt.setString(index, p_domainCode);
                rs = pstmt.executeQuery();
                pstmt.clearParameters();
                if (rs.next()) {
                   
                 //this is only for validation of user geography.   
                	batchO2CItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
                } else {
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil
    						.getMessageString("batcho2c.processuploadedfile.error.msisdnnotfound.ingeography"));
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    continue;
                }
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBATCHWITHDRAWWEBDAO[validateUsersForBatchO2CWithdraw]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
             throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBATCHWITHDRAWWEBDAO[validateUsersForBatchO2CWithdraw]", "",
                "", "", "Exception:" + ex.getMessage());
               throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
               if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting:  errorList Size =" + errorList.size());
            }
        }
        return errorList;
    }
    
    

    /**
     * 
     * @param p_con
     * @param p_batchId
     */
    public ArrayList o2cBatchCloseRest(Connection p_con, FOCBatchMasterVO focBatchMasterVO, Map downloadDataMap, String p_batchId, Locale p_locale, String p_currentLevel, String userId) throws BTSLBaseException {

        final String methodName = "o2cBatchClose";
        if (_log.isDebugEnabled()) {

            _log.debug(methodName, "Entered p_batchId=" + p_batchId);
        }

        BatchO2CItemsVO batchItemsVO = null;
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

                    batchItemsVO = (BatchO2CItemsVO) downloadDataMap.get(mapKey);
                    batchItemsVO.setWalletType(wallet_type);
                    // Load the User Detail based on the MSISDN or LoginId
                    if (!BTSLUtil.isNullString(batchItemsVO.getMsisdn())) {
                        channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, batchItemsVO.getMsisdn());
                    } else if (!BTSLUtil.isNullString(batchItemsVO.getLoginID())) {
                        channelUserVO = channelUserDAO.loadChnlUserDetailsByLoginID(p_con, batchItemsVO.getLoginID());
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

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batcho2cwithdraw.batchapprovereject.msg.error.usersuspend"));
                            errorList.add(errorVO);
                            //BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : User is suspend", "Approval level" + p_currentLevel);
                            continue;
                        }
                        // (commission profile status is checked) if this
                        // condition is true then made entry in logs and leave
                        // this data.
                        else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                            errorList.add(errorVO);
                            //BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : Commission profile suspend",
                            //    "Approval level" + p_currentLevel);
                            continue;
                        }
                        // (transfer profile is checked) if this condition is
                        // true then made entry in logs and leave this data.
                        else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                            errorList.add(errorVO);
                           // BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : Transfer profile suspend", "Approval level" + p_currentLevel);
                            continue;
                        }
                        // (user in suspend is checked) if this condition is
                        // true then made entry in logs and leave this data.
                        else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                            errorList.add(errorVO);
                          //  BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                            continue;
                        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equalsIgnoreCase(batchItemsVO.getStatus())) {
                          //  BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "CANCEL : ", batchItemsVO.getBatchDetailId());
                            continue;
                        }

                        final ChannelTransferVO channelTransferVO = ChannelTransferVO.getInstance();
                        final ArrayList list = ChannelTransferBL.loadO2CWdrProductListRest(p_con, productType, channelUserVO.getNetworkID(), channelUserVO
                            .getCommissionProfileSetID(), channelUserVO.getDomainID(), channelUserVO.getCategoryCode(), currentDate);
                        
                        if(list!=null && list.size()>0) {
                            //Filter only the product which is selected in UI,Rest remove
                             for (int i=0;i<list.size();i++) {
                            	 ChannelTransferItemsVO chnlTrfitemVO =(ChannelTransferItemsVO)list.get(i); 
                            	 if(!focBatchMasterVO.getProductCode().equals(chnlTrfitemVO.getErpProductCode())) {
                            		 list.remove(chnlTrfitemVO);
                            	 }
                             }
                           }

                        // prepares the ChannelTransferVO by populating its
                        // fields from the passed ChannelUserVO and filteredList
                        // of products
                        this.constructChannelTransferVO(batchItemsVO, channelTransferVO, currentDate, channelUserVO, list);

                        // ChannelTransferBL.loadAndCalculateTaxOnProducts(p_con,
                        // batchItemsVO.getCommissionProfileSetId(),
                        // batchItemsVO.getCommissionProfileVer(),channelTransferVO,true,p_forwardPath,PretupsI.TRANSFER_TYPE_O2C);

                        final boolean isSlabFlag = ChannelTransferBL.loadAndCalculateTaxOnProducts(p_con, batchItemsVO.getCommissionProfileSetId(), batchItemsVO
                            .getCommissionProfileVer(), channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);

                        if (!isSlabFlag) {

                            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batcho2c.initiateBatchO2CWithdraw.commissionprofile.product.notdefine"));
                            errorList.add(errorVO);
                            //BatchO2CFileProcessLog.detailLog(methodName, focBatchMasterVO, batchItemsVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                            continue;

                        } else {
                            ChannelTransferBL.genrateWithdrawID(channelTransferVO);

                            // ChannelTransferDAO channelTransferDAO = new
                            // ChannelTransferDAO();
                            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(batchItemsVO.getStatus())) {
                                errorList = orderReturnedProcessStartRest(p_con, channelTransferVO, channelUserVO.getUserID(), currentDate, errorVO, batchItemsVO
                                    .getMsisdn(), batchItemsVO.getRecordNumber(), p_locale, errorList, p_currentLevel, batchItemsVO, focBatchMasterVO, userId);
                            }

                           

                        }

                    }
                }

            }
        } catch (BTSLBaseException bbe) {

            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : DB Error while updating/inserting data into table",
                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
            _log.error(methodName, "BTSLBaseException:e=");
            throw bbe;

        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[o2cBatchClose]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            // TODO: handle exception
            errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : DB Error while updating/inserting data into table",
                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

            _log.error(methodName, "Exception:e=" + e.getMessage());
            _log.errorTrace(methodName, e);
        } finally {
            try {

                updateCount = updateO2CBatchStatus(p_con, currentDate, channelUserVO.getUserID(), batchItemsVO.getBatchId());

                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    errorVO = new ListValueVO("", "", PretupsRestUtil.getMessageString("batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CFileProcessLog.o2cBatchMasterLog(methodName, focBatchMasterVO, "FAIL : DB Error while updating master table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[o2cBatchClose]", "",
                        "", "", "Error while updating FOC_BATCHES table. Batch id=" + batchItemsVO.getBatchId());
                }// end of if
                else {
                    p_con.commit();
                }

            } catch (Exception ex) {
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[o2cBatchClose]", "", "",
                    "", "Exception:" + ex.getMessage());
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting forward=" + errorList.size());
            }
        }

        return errorList;
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
    private ArrayList<ListValueVO> orderReturnedProcessStart(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userId, Date p_date, String p_forwardPath, ListValueVO errorVO, String msisdn, int recordNumber, MessageResources p_messages, Locale p_locale, ArrayList<ListValueVO> errorList, String p_currentLevel, O2CBatchItemsVO batchItemsVO, FOCBatchMasterVO focBatchMasterVO, String userId) throws BTSLBaseException, SQLException {
        if (_log.isDebugEnabled()) {
            _log.debug("orderReturnedProcessStart",
                "Entered p_channelTransferVO  " + p_channelTransferVO + " p_userId " + p_userId + " p_date " + p_date + " p_forwardPath: " + p_forwardPath);
        }


        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchWithdrawWebDAO[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    
        final boolean credit = false;
        int updateCount = 0;
        String txnReceiverUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        Boolean isO2CBatchWithdrwaMessageAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED);
        Boolean isAdminMessageReqd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ADMIN_MESSAGE_REQD);
        ArrayList<ListValueVO> tempErrorList = new ArrayList<ListValueVO>();
        // prepare networkStockList credit the network stock
        updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userId, p_date, credit);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);

            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, userId, p_date);

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        // update user daily balances
        final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
        updateCount = userBalancesDAO.updateUserDailyBalances(p_con, p_date, constructBalanceVOFromTxnVO(p_channelTransferVO));

        if (updateCount <= 0) {
            errorVO = new ListValueVO(msisdn, String.valueOf(recordNumber), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
            errorList.add(errorVO);
            // throw new BTSLBaseException(this, "orderReturnedProcessStart",
            // "error.general.sql.processing");
        }
        updateCount = 0;
        // channel debit the user balances
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        tempErrorList = channelUserDAO.debitUserBalancesForO2C(p_con, p_channelTransferVO, msisdn, recordNumber, p_messages, p_locale);
        // errorList =
        // channelUserDAO.debitUserBalancesForO2C(p_con,p_channelTransferVO,msisdn,recordNumber,p_messages,p_locale,errorList);

        // if(errorList==null || errorList.isEmpty())
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
                    updateCount = updateO2CStatus(p_con, p_date, p_currentLevel, batchItemsVO, userId, p_channelTransferVO.getTransferID());

                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.detailLog("orderReturnedProcessStart", focBatchMasterVO, batchItemsVO, "FAIL : DB Error while updating items table",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        // continue;
                    } else {
                        p_con.commit();
                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : Order is closed successfully",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                        if (batchItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                            boolean statusChangeRequired = false;
                            if (!PretupsI.USER_STATUS_ACTIVE.equals(batchItemsVO.getUserStatus())) {
                                // int
                                // updatecount=operatorUtili.changeUserStatusToActive(
                                // p_con,p_channelTransferVO.getFromUserID(),batchItemsVO.getUserStatus());
                                int updatecount = 0;
                                final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                                String newStatus[] = null;
                                for (int i = 0; i < str.length; i++) {
                                    newStatus = str[i].split(":");
                                    if (newStatus[0].equals(batchItemsVO.getUserStatus())) {
                                        statusChangeRequired = true;
                                        updatecount = operatorUtili.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), batchItemsVO.getUserStatus(),
                                            newStatus[1]);
                                        break;
                                    }
                                }
                                if (statusChangeRequired) {
                                    if (updatecount > 0) {
                                        p_con.commit();
                                        BatchO2CFileProcessLog.detailLog("closeOrederByBatch", focBatchMasterVO, batchItemsVO, "PASS : user status changed  successfully",
                                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                                    } else {
                                        p_con.rollback();
                                        throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.updating");
                                    }
                                }
                            }
                          //Added for message pushing as per GP requirement
                            if(isAdminMessageReqd) {
		            			//Added for message pushing as per GP requirement
			            		if(isO2CBatchWithdrwaMessageAllowed){
                                    UserPhoneVO primaryPhoneVO=null;
                                    UserDAO userDAO=new UserDAO();
                                    ChannelUserVO channelUserVO= null;
                                    String _msisdnString=null;
                                    try
                                    {
                                           _msisdnString=new String(Constants.getProperty("adminmobile"));
                                           if(_log.isDebugEnabled())_log.debug("refresh","_msisdnString: "+_msisdnString);
                                    }
                                    catch(Exception e5)
                                    {
                                           _log.error("refresh","adminmobile is not defined in Constant.props, cannot use default");
                                    }
                                    if(!BTSLUtil.isNullString(batchItemsVO.getMsisdn())){
                                           channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, batchItemsVO.getMsisdn());
                                    }else if(!BTSLUtil.isNullString(batchItemsVO.getLoginId())){
                                           channelUserVO= channelUserDAO.loadChnlUserDetailsByLoginID(p_con, batchItemsVO.getLoginId());
                                    }
                                    if( BTSLUtil.isNullString(_msisdnString))
                                    {   
                                    	_msisdnString="Admin User";
                                    }
                                                  ArrayList productList=p_channelTransferVO.getChannelTransferitemsVOList();
                                                  ChannelTransferItemsVO channelTransferItemsVO = null;
                                                  channelTransferItemsVO = (ChannelTransferItemsVO)productList.get(0);
                                                  Locale locale= new Locale(defaultLanguage,defaultCountry);
                                                  String[] array= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),batchItemsVO.getMsisdn(),p_channelTransferVO.getTransferID(),_msisdnString};//,loginUserVO.getUserID()
                                                  BTSLMessages messages=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_ADMIN_MESSAGE,array);
                                                  String[] array1= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),_msisdnString,p_channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()-channelTransferItemsVO.getApprovedQuantity())};//,loginUserVO.getUserID()
                                                  BTSLMessages messages1=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_USER_MESSAGE,array1);
                                                  PushMessage  pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages1,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                                  pushMessage.push();
                                                  PushMessage pushMessage1=null;
                                                  if(channelUserVO.getAlertMsisdn()!=null)
                                                  {
                                                         String [] msisdn1 =channelUserVO.getAlertMsisdn().split(";");
                                                         for(int l=0,len=msisdn1.length; l<len;l++)
                                                         {      
                                                                pushMessage1=new PushMessage(msisdn1[l],messages,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                                                pushMessage1.push();
                                                         }
                                                  }
                                         
                                    
                              }
		            		}else {
		            			if(isO2CBatchWithdrwaMessageAllowed){
		            				Locale locale= new Locale(defaultLanguage,defaultCountry);
		            				ArrayList productList=p_channelTransferVO.getChannelTransferitemsVOList();
                                    ChannelTransferItemsVO channelTransferItemsVO = null;
                                    channelTransferItemsVO = (ChannelTransferItemsVO)productList.get(0);
		            				String[] array1= {PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity()),"Admin",p_channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemsVO.getPreviousBalance()-channelTransferItemsVO.getApprovedQuantity())};//,loginUserVO.getUserID()
                                    BTSLMessages messages1=new BTSLMessages(PretupsErrorCodesI.O2C_WITHDRAW_USER_MESSAGE,array1);
                                    PushMessage  pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages1,p_channelTransferVO.getTransferID(),null,locale,p_channelTransferVO.getNetworkCode());
                                    pushMessage.push();
		            			}
		            			
		            		}
                        }
                    }
                    // p_con.commit();
                     ChannelTransferBL.prepareUserBalancesListForLogger(p_channelTransferVO);
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
        /*
         * updateCount = updateO2CStatus(p_con,p_date,
         * p_currentLevel,batchItemsVO
         * ,p_userId,p_channelTransferVO.getTransferID());
         * 
         * //(record not updated properly) if this condition is true then made
         * entry in logs and leave this data.
         * if(updateCount<=0)
         * {
         * errorVO=new
         * ListValueVO(msisdn,String.valueOf(recordNumber),p_messages
         * .getMessage(
         * p_locale,"batchfoc.batchapprovereject.msg.error.recordnotupdated"));
         * errorList.add(errorVO);
         * 
         * }
         */

        if (_log.isDebugEnabled()) {
            _log.debug("orderReturnedProcessStart", "Exiting");
        }
        return errorList;
        // return tempErrorList;
    }
    
    
    /**
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
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList processOrderByBatch(Connection p_con, Map p_dataMap, String p_currentLevel, String p_userID, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_dataMap=" + p_dataMap + " p_currentLevel=" + p_currentLevel + " p_locale=" + p_locale + " p_userID=" + p_userID);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        PreparedStatement pstmtLoadUser = null;
        // commented for DB2
        // OraclePreparedStatement psmtCancelFOCBatchItem = null;
        // OraclePreparedStatement psmtAppr1FOCBatchItem = null;
        // OraclePreparedStatement psmtAppr2FOCBatchItem = null;
        // OraclePreparedStatement pstmtUpdateMaster= null;
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
        ResultSet rs4 = null;
        int updateCount = 0;
        String batch_ID = null;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuilder sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.network_code,u.category_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        final String sqlLoadUser = sqlBuffer.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
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

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlCancelFOCBatchItems=" + sqlCancelFOCBatchItems);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
        sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end) as new,");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end ) cncl, ");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed ");
        sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
        sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
        final String selectItemsDetails = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum1=" + isExistsTxnNum1);
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
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
        }
        sqlBuffer = null;
        Date date = null;
        try {
        	BatchO2CItemsVO o2cBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            // psmtCancelFOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlCancelFOCBatchItems);//commented
            // for DB2
            // psmtAppr1FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv1FOCBatchItems);//commented
            // for DB2
            // psmtAppr2FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv2FOCBatchItems);//commented
            // for DB2
            psmtCancelFOCBatchItem = p_con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateFOCBatches);//commented
            // for DB2
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
                o2cBatchItemVO = (BatchO2CItemsVO) p_dataMap.get(key);
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
                    channelUserVO = ChannelUserVO.getInstance();
                    channelUserVO.setUserID(o2cBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
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
                        throw new BTSLBaseException(this, "processOrderByBatch", "error.status.processing");
                    }
                    if (!senderStatusAllowed) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()),  PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                       // BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User is not active", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                      //  BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        //BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()),  PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                       // BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                  //  BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
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
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()),  PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                  /*  BatchO2CFileProcessLog
                        .o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);*/
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(o2cBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    /*BatchO2CFileProcessLog
                        .o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);*/
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (isExternalTxnUnique && !BTSLUtil.isNullString(o2cBatchItemVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
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
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()),  PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        /*BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : External transaction number already exists BATCH FOC",
                            "Approval level" + p_currentLevel);*/
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
                        p_con.rollback();
                        errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()),  PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                       /* BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + p_currentLevel);*/
                        continue;
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelFOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(o2cBatchItemVO.getStatus())) {
                    psmtCancelFOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getStatus());
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, o2cBatchItemVO.getBatchDetailId());
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
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(o2cBatchItemVO.getStatus())) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr1FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
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
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(o2cBatchItemVO.getStatus())) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr2FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
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
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(o2cBatchItemVO.getMsisdn(), String.valueOf(o2cBatchItemVO.getRecordNumber()),  PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                   /* BatchO2CFileProcessLog.o2cBatchItemLog(methodName, o2cBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level" + p_currentLevel + ", updateCount=" + updateCount);*/
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
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "", "",
                "Exception:" + ex.getMessage());
            BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
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
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelFOCBatchItem != null) {
                    psmtCancelFOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
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
                rs4 = pstmtSelectItemsDetails.executeQuery();
                if (rs4.next()) {
                    final int totalCount = rs4.getInt("batch_total_record");
                    final int closeCount = rs4.getInt("closed");
                    final int cnclCount = rs4.getInt("cncl");
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
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "",  PretupsRestUtil.getMessageString("batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]",
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
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "SQLException : " + sqe);
                _log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.sql.processing");
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());
                BatchO2CFileProcessLog.o2cBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }finally{
            	try {
                    if (rs4 != null) {
                        rs4.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }	
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    }
    
    /**
     * Thiis methid will load the data from the foc_batch_items table
     * corresponding to batch id.
     * The result will be returned as LinkedHasMap. The key will be
     * batch_detail_id for this map.
     * 
     * @param p_con
     * @param p_batchId
     * @param p_itemStatus
     * @return
     * @throws BTSLBaseException
     */
    public LinkedHashMap loadBatchItemsMaprest(Connection p_con, String p_batchId, String p_itemStatus) throws BTSLBaseException {
        final String methodName = "loadBatchItemsMap";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_batchId=" + p_batchId + " p_itemStatus=" + p_itemStatus);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
        strBuff.append(" fbi.third_approved_on,fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
        strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
        strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on,fbi.third_approved_by, ");
        strBuff.append(" fb.created_by,fb.created_on,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
        strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
        // strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
        strBuff.append(" fbi.initiator_remarks, fbi.first_approver_remarks, ");
        strBuff.append(" fbi.third_approver_remarks, fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
        strBuff.append(" fbi.third_approved_on, fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
        strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
        strBuff.append(" fbi.second_approved_on, fbi.third_approved_by, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
        strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
        strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
        strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value,fbi.bonus_type,fbi.dual_comm_type ");
        strBuff.append(" FROM foc_batch_items fbi left join users fapp on fbi.first_approved_by = fapp.user_id left join users sapp on fbi.second_approved_by = sapp.user_id , ");
        strBuff.append(" foc_batches fb left join users intu on fb.created_by = intu.user_id,categories c,channel_grades cg, users u");
        strBuff.append(" WHERE fb.batch_id=? AND fb.batch_id=fbi.batch_id AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
        strBuff.append(" fbi.user_grade_code=cg.grade_code");
        strBuff
            .append(" AND fbi.status in(" + p_itemStatus + ") AND fbi.rcrd_status=? ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final LinkedHashMap map = new LinkedHashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                final BatchO2CItemsVO batchItemsVO = new BatchO2CItemsVO();
                batchItemsVO.setBatchId(p_batchId);
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
                batchItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
                batchItemsVO.setLoginID(rs.getString("login_id"));
                batchItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
                batchItemsVO.setModifiedBy(rs.getString("modified_by"));
                batchItemsVO.setReferenceNo(rs.getString("reference_no"));
                batchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchItemsVO.setExtTxnDate(rs.getTimestamp("ext_txn_date"));
                batchItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
                batchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                batchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                batchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                batchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                batchItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
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

                // batchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                // batchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                batchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                batchItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                batchItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                batchItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                batchItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                batchItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                batchItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                batchItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                batchItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                batchItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                batchItemsVO.setCancelledBy(rs.getString("cancelled_by"));
                batchItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
                batchItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
                batchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                batchItemsVO.setCategoryCode(rs.getString("category_code"));
                batchItemsVO.setFirstApproverName(rs.getString("first_approver_name"));
                batchItemsVO.setSecondApproverName(rs.getString("second_approver_name"));
                batchItemsVO.setInitiaterName(rs.getString("initiater_name"));
                batchItemsVO.setExternalCode(rs.getString("external_code"));

                // batchItemsVO.setBonusType(rs.getString("bonus_type"));
                map.put(rs.getString("batch_detail_id"), batchItemsVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadBatchItemsList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: loadBatchItemsMap map=" + map.size());
            }
        }
        return map;
    }
    
    
}
