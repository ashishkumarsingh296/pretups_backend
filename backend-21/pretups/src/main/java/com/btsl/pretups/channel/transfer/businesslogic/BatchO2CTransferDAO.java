package com.btsl.pretups.channel.transfer.businesslogic;

/**
 * @# BatchO2CTransferDAO.java
 *
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    June 02, 2011 nand.sahu Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2011 Comviva Technologies Ltd.
 *
 */

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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchO2CProcessLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.restapi.o2c.service.O2CBatchApprovalDetailsResponse;

public class BatchO2CTransferDAO {

	 /**
     * Field log.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    private BatchO2CTransferQry batchO2CTransferQry = (BatchO2CTransferQry) ObjectProducer.getObject(QueryConstants.BATCH_O2C_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
    private String errorGeneralSqlException =  "error.general.sql.processing";
    private String errorGeneralException =  "error.general.processing";


    /**
     *
     */
    public BatchO2CTransferDAO() {
        super();
    }


    /**
     * Method for loading Batch O2C details..
     * This method will load the batches that are within the geography of user
     * whose userId is passed
     * with status(OPEN) also in items table for corresponding master record the
     * status is in p_itemStatus
     *
     * @param con
     *            java.sql.Connection
     * @param pitemStatus
     *            String
     * @param currentLevel
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchO2CMasterDetails(Connection con, String userID, String pitemStatus, String currentLevel, String trfType, String trfSubType) throws BTSLBaseException {
        final String methodName = "loadBatchO2CMasterDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_userID=");
        	loggerValue.append(userID);
        	loggerValue.append(" p_itemStatus=");
        	loggerValue.append(pitemStatus);
        	loggerValue.append(" p_currentLevel=");
        	loggerValue.append(currentLevel);
        	loggerValue.append(" p_trfType=");
        	loggerValue.append(trfType);
        	loggerValue.append(" p_trfSubType=");
        	loggerValue.append(trfSubType);
            log.debug(methodName,loggerValue );
        }


        final String itemStatus = pitemStatus.replaceAll("'", "");
        final String ss = itemStatus.replaceAll("\" ", "");
        final String mItemStatus[] = ss.split(",");

        final String sqlSelect = batchO2CTransferQry.loadBatchO2CMasterDetailsQry(mItemStatus, currentLevel);
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlSelect=");
            loggerValue.append(sqlSelect);
            log.debug(methodName,  loggerValue );
        }
        final ArrayList list = new ArrayList();
        int i = 0;
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {


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
            pstmt.setString(i, userID);
            ++i;
            pstmt.setString(i, trfType);
            ++i;
            pstmt.setString(i, trfSubType);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
            ++i;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
            for (int x = 0; x < mItemStatus.length; x++) {
                ++i;
                pstmt.setString(i, mItemStatus[x]);
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            BatchO2CMasterVO batchO2CMasterVO = null;
            while (rs.next()) {
                batchO2CMasterVO = new BatchO2CMasterVO();
                batchO2CMasterVO.setBatchId(rs.getString("batch_id"));
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
                batchO2CMasterVO.setNetworkCode(rs.getString("network_code"));
                batchO2CMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
                batchO2CMasterVO.setProductCode(rs.getString("product_code"));
                batchO2CMasterVO.setModifiedBy(rs.getString("modified_by"));
                batchO2CMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
                batchO2CMasterVO.setProductType(rs.getString("product_type"));
                batchO2CMasterVO.setProductShortName(rs.getString("short_name"));
                batchO2CMasterVO.setDomainCode(rs.getString("domain_code"));
                batchO2CMasterVO.setBatchDate(rs.getDate("batch_date"));
                batchO2CMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
                batchO2CMasterVO.setSecondLang(rs.getString("sms_second_lang"));
                list.add(batchO2CMasterVO);
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, ex);
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
             if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: batchO2CMasterVOList size=" );
            	loggerValue.append(list.size());
                log.debug(methodName, loggerValue );
			}
        }
        return list;
    }

    /**
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
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchO2CMasterDetails(Connection con, String p_goeDomain, String p_domain, String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID, String p_trfType, String p_trfSubType) throws BTSLBaseException {
        final String methodName = "loadBatchO2CMasterDetails";
        StringBuilder loggerValue= new StringBuilder();

        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_goeDomain=");
        	loggerValue.append(p_goeDomain);
        	loggerValue.append(" p_domain=");
        	loggerValue.append(p_domain);
        	loggerValue.append(" p_productCode=" );
        	loggerValue.append(p_productCode);
        	loggerValue.append(" p_batchid=");
        	loggerValue.append(p_batchid);
        	loggerValue.append(" p_msisdn=");
        	loggerValue.append(p_msisdn);
        	loggerValue.append(" p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append(" p_loginID=");
        	loggerValue.append(p_loginID);
        	loggerValue.append(" p_trfType=");
            loggerValue.append(p_trfType);
            loggerValue.append(" p_trfSubType=");
            loggerValue.append(	p_trfSubType);
            log.debug(methodName,loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        final String goeDomain = p_goeDomain.replaceAll("'", "");
        final String gg = goeDomain.replaceAll("\" ", "");
        final String m_goeDomain[] = gg.split(",");
        final String domain = p_domain.replaceAll("'", "");
        final String d = domain.replaceAll("\" ", "");
        final String m_domain[] = d.split(",");
        final String productCode = p_productCode.replaceAll("'", "");
        final String pc = productCode.replaceAll("\" ", "");
        final String m_productCode[] = pc.split(",");


        final String sqlSelect = batchO2CTransferQry.loadBatchO2CMasterDetailsQry(p_batchid, p_msisdn, m_goeDomain, m_domain, m_productCode);
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
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
            if (p_batchid == null && p_msisdn == null) {
                for (int x = 0; x < m_goeDomain.length; x++) {
                    ++i;
                    pstmt.setString(i, m_goeDomain[x]);
                }
            }
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
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" );
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                	loggerValue.append(" BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=");
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    log.debug( methodName,loggerValue );
                }
            } else {
                for (int x = 0; x < m_domain.length; x++) {
                    ++i;
                    pstmt.setString(i, m_domain[x]);
                }
                for (int x = 0; x < m_productCode.length; x++) {
                    ++i;
                    pstmt.setString(i, m_productCode[x]);
                }
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++i;
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="  );
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate) );
                	loggerValue.append(" BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=");
                	loggerValue.append(BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    log.debug(methodName,loggerValue);
                }
            }
            rs = pstmt.executeQuery();
            BatchO2CMasterVO batchO2CMasterVO = null;
            while (rs.next()) {
                batchO2CMasterVO = new BatchO2CMasterVO();
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
        	loggerValue.append("SQLException : " );
        	loggerValue.append(sqe);
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:"  );
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "",loggerValue.toString());
            throw new BTSLBaseException(this, "loadBatchFOCMasterDetails", "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
         	loggerValue.append( "Exception:"  );
         	loggerValue.append(ex);
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
         	loggerValue.append( "Exception:"  );
         	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CMasterDetails]", "",
                "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
             	loggerValue.append( "Exiting: focBatchMasterVOList size="  );
             	loggerValue.append(list.size());
                log.debug(methodName,  loggerValue );
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
    public LinkedHashMap loadBatchItemsMap(Connection con, String batchId, String itemStatus, String trfType, String trfSubType) throws BTSLBaseException {
        final String methodName = "loadBatchItemsMap";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_batchId=" );
        	loggerValue.append(batchId);
        	loggerValue.append(" p_itemStatus=" );
        	loggerValue.append(itemStatus);
        	loggerValue.append(" p_trfType=");
        	loggerValue.append(trfType);
        	loggerValue.append(" p_trfSubType=");
        	loggerValue.append(trfSubType);
            log.debug(methodName, loggerValue );
        }



        final String sqlSelect = batchO2CTransferQry.loadBatchItemsMapQry(itemStatus);
        BatchO2CItemsVO batchO2CItemsVoObj = new BatchO2CItemsVO();
        final LinkedHashMap map = new LinkedHashMap();
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

            pstmt.setString(1, batchId);
            pstmt.setString(2, trfType);
            pstmt.setString(3, trfSubType);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                final BatchO2CItemsVO batchO2CItemsVO = batchO2CItemsVoObj;
                batchO2CItemsVO.setBatchId(batchId);
                batchO2CItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
                batchO2CItemsVO.setCategoryName(rs.getString("category_name"));
                batchO2CItemsVO.setMsisdn(rs.getString("msisdn"));
                batchO2CItemsVO.setUserId(rs.getString("user_id"));
                batchO2CItemsVO.setStatus(rs.getString("status"));
                batchO2CItemsVO.setGradeName(rs.getString("grade_name"));
                batchO2CItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchO2CItemsVO.setExtTxnDate(rs.getDate("ext_txn_date"));
                batchO2CItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                batchO2CItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                batchO2CItemsVO.setInitiatedBy(rs.getString("created_by"));
                batchO2CItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
                batchO2CItemsVO.setLoginID(rs.getString("login_id"));
                batchO2CItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
                batchO2CItemsVO.setModifiedBy(rs.getString("modified_by"));
                batchO2CItemsVO.setReferenceNo(rs.getString("reference_no"));
                batchO2CItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                batchO2CItemsVO.setExtTxnDate(rs.getTimestamp("ext_txn_date"));
                batchO2CItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
                batchO2CItemsVO.setTxnProfile(rs.getString("txn_profile"));
                batchO2CItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                batchO2CItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                batchO2CItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));

                batchO2CItemsVO.setCommissionType(rs.getString("commission_type"));
                batchO2CItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                batchO2CItemsVO.setCommissionValue(rs.getLong("commission_value"));
                batchO2CItemsVO.setTax1Type(rs.getString("tax1_type"));
                batchO2CItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                batchO2CItemsVO.setTax1Value(rs.getLong("tax1_value"));
                batchO2CItemsVO.setTax2Type(rs.getString("tax2_type"));
                batchO2CItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                batchO2CItemsVO.setTax2Value(rs.getLong("tax2_value"));
                batchO2CItemsVO.setTax3Type(rs.getString("tax3_type"));
                batchO2CItemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                batchO2CItemsVO.setTax3Value(rs.getLong("tax3_value"));

                batchO2CItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                batchO2CItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                batchO2CItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                batchO2CItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                batchO2CItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                batchO2CItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                batchO2CItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                batchO2CItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                batchO2CItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                batchO2CItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                batchO2CItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                batchO2CItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                batchO2CItemsVO.setCancelledBy(rs.getString("cancelled_by"));
                batchO2CItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
                batchO2CItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
                batchO2CItemsVO.setGradeCode(rs.getString("user_grade_code"));
                batchO2CItemsVO.setCategoryCode(rs.getString("category_code"));
                batchO2CItemsVO.setFirstApproverName(rs.getString("first_approver_name"));
                batchO2CItemsVO.setSecondApproverName(rs.getString("second_approver_name"));
                batchO2CItemsVO.setInitiaterName(rs.getString("initiater_name"));
                batchO2CItemsVO.setExternalCode(rs.getString("external_code"));
                map.put(rs.getString("batch_detail_id"), batchO2CItemsVO);
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchItemsMap]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "loadBatchItemsList", "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exception : ");
         	loggerValue.append(ex);
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
         	loggerValue.append("Exception : ");
         	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchItemsMap]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: loadBatchItemsMap map=");
            	loggerValue.append(map.size());
                log.debug(methodName,  loggerValue );
			}
        }
        return map;
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
    public ArrayList processO2CWithdrawByBatch(Connection con, LinkedHashMap dataMap, String currentLevel, String userID, MessageResources messages, Locale locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processO2CWithdrawByBatch";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_dataMap=");
        	loggerValue.append(dataMap);
        	loggerValue.append( " p_currentLevel=");
        	loggerValue.append(currentLevel);
        	loggerValue.append(" p_locale=");
        	loggerValue.append(locale);
        	loggerValue.append(" p_userID=");
        	loggerValue.append(userID);
            log.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement psmtCancelO2CBatchItem = null;
        PreparedStatement psmtAppr1O2CBatchItem = null;
        PreparedStatement psmtAppr2O2CBatchItem = null;
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
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
         String sqlLoadUser = null;
         StringBuilder sqlBuffer = null;
         HashMap<String, HashMap<String, String>> tcpMap = null;
         
        if(!tcpOn) {

        	
         sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlLoadUser=");
        	loggerValue.append(sqlLoadUser);
            log.debug(methodName,  loggerValue );
        }
        
        }else {
        	//TCP On
        	
        	SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
					ValueType.STRING);
        	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
        	
        	 sqlBuffer = new StringBuilder(" SELECT users.transfer_profile_id, u.status userstatus, cusers.in_suspend, ");
             sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
             sqlBuffer.append("cps.language_2_message comprf_lang_2_msg ");
             sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps ");
             sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
             sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
             sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
             sqlBuffer.append(" ");
             sqlLoadUser = sqlBuffer.toString();
             if (log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("QUERY sqlLoadUser=");
             	loggerValue.append(sqlLoadUser);
                 log.debug(methodName,  loggerValue );
             }
        	
        	
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
        final String sqlCancelO2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlCancelO2CBatchItems=");
        	loggerValue.append(sqlCancelO2CBatchItems);
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

        final String sqlApprv1O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv1O2CBatchItems=");
        	loggerValue.append(sqlApprv1O2CBatchItems);
            log.debug(methodName,   loggerValue);
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
        final String sqlApprv2O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv2O2CBatchItems=");
        	loggerValue.append(sqlApprv2O2CBatchItems);
            log.debug(methodName,  loggerValue );
        }

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table


        final String selectItemsDetails = batchO2CTransferQry.processO2CWithdrawByBatchSelectItemsDetails();

        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateO2CBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateO2CBatches=");
        	loggerValue.append(updateO2CBatches);

            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isModified=");
        	loggerValue.append(isModified);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuilder(" SELECT  1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isExistsTxnNum1=");
        	loggerValue.append(isExistsTxnNum1);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuilder("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=?  AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isExistsTxnNum2=");
        	loggerValue.append(isExistsTxnNum2);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;
        Date date = null;
        try {
            BatchO2CItemsVO batchO2CItemsVO = null;
            ChannelUserVO channelUserVO = null;
            ChannelUserVO channelUserVoObj = new ChannelUserVO();
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = con.prepareStatement(sqlLoadUser);
            psmtCancelO2CBatchItem = con.prepareStatement(sqlCancelO2CBatchItems);
            psmtAppr1O2CBatchItem =  con.prepareStatement(sqlApprv1O2CBatchItems);
            psmtAppr2O2CBatchItem =  con.prepareStatement(sqlApprv2O2CBatchItems);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster =  con.prepareStatement(updateO2CBatches);
            pstmtIsModified = con.prepareStatement(isModified);
            pstmtIsTxnNumExists1 = con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = con.prepareStatement(isExistsTxnNum2);
            errorList = new ArrayList();
            final Iterator iterator = dataMap.keySet().iterator();
            String key = null;
            int m = 0;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                batchO2CItemsVO = (BatchO2CItemsVO) dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = batchO2CItemsVO.getBatchId();
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, batchO2CItemsVO.getUserId());
                rs = pstmtLoadUser.executeQuery();
                if (rs.next())// check data found or not
                {
                    channelUserVO = channelUserVoObj;
                    channelUserVO.setUserID(batchO2CItemsVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    
                    if(!tcpOn) {
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));//TCP
                    }else {
                    	
                        channelUserVO.setTransferProfileStatus(tcpMap.get(rs.getString("transfer_profile_id")).get("status"));//TCP
                    }
                    // (User status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : User is not active", "Approval level" + currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : Commission profile is suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : Transfer profile is suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : User is IN suspend", "Approval level" + currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : User not found", "Approval level" + currentLevel);
                    continue;

                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, batchO2CItemsVO.getBatchDetailId());
                rs1 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                if (rs1.next()) {
                    newlastModified = rs1.getTimestamp("modified_on");
                }
                // (record not found means it is modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : Record is already modified by some one else", "Approval level" + currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : Record is already modified by some one else", "Approval level" + currentLevel);
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue() && !BTSLUtil.isNullString(batchO2CItemsVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
                    .equals(batchO2CItemsVO.getStatus())) {
                    // check in foc_batch-item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, batchO2CItemsVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, batchO2CItemsVO.getBatchDetailId());
                    rs2 = pstmtIsTxnNumExists1.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs2.next()) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : External transaction number already exists BATCH O2C withdraw",
                            "Approval level" + currentLevel);
                        continue;
                    }
                    // check in channel_transfers table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, batchO2CItemsVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs3 = pstmtIsTxnNumExists2.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs3.next()) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + currentLevel);
                        continue;
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelO2CBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(batchO2CItemsVO.getStatus())) {
                    psmtCancelO2CBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelO2CBatchItem.setString(m, userID);
                    ++m;
                    psmtCancelO2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, batchO2CItemsVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, batchO2CItemsVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, batchO2CItemsVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelO2CBatchItem.setString(m, userID);
                    ++m;
                    psmtCancelO2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelO2CBatchItem.setString(m, batchO2CItemsVO.getStatus());
                    ++m;
                    psmtCancelO2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtCancelO2CBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1O2CBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(batchO2CItemsVO.getStatus())) {
                    psmtAppr1O2CBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr1O2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr1O2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getStatus());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getExtTxnNo());
                    ++m;
                    psmtAppr1O2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1O2CBatchItem.executeUpdate();
                }
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2O2CBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(batchO2CItemsVO.getStatus())) {
                    psmtAppr2O2CBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr2O2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, batchO2CItemsVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, userID);
                    ++m;
                    psmtAppr2O2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, batchO2CItemsVO.getStatus());
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, batchO2CItemsVO.getExtTxnNo());
                    ++m;
                    psmtAppr2O2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2O2CBatchItem.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.batchO2CItemLog(methodName, batchO2CItemsVO, "FAIL : DB Error while updating items table",
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
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[processO2CWithdrawByBatch]", "",
                "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[processO2CWithdrawByBatch]", "",
                "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

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
        	}catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelO2CBatchItem != null) {
                    psmtCancelO2CBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1O2CBatchItem != null) {
                    psmtAppr1O2CBatchItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2O2CBatchItem != null) {
                    psmtAppr2O2CBatchItem.close();
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
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "", messages.getMessage(locale, "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "BatchO2CTransferDAO[processO2CWithdrawByBatch]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
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
                loggerValue.setLength(0);
                loggerValue.append("SQLException : ");
                loggerValue.append(sqe);
                log.error(methodName,  loggerValue );
                log.errorTrace(methodName, sqe);
                loggerValue.setLength(0);
                loggerValue.append("SQL Exception:");
                loggerValue.append(sqe.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[processO2CWithdrawByBatch]",
                    "", "", "",  loggerValue.toString());
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
                loggerValue.append("Exception : ");
                loggerValue.append(ex);
                log.error(methodName,  loggerValue );
                log.errorTrace(methodName, ex);
                loggerValue.setLength(0);
                loggerValue.append("Exception : ");
                loggerValue.append( ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[processO2CWithdrawByBatch]",
                    "", "", "", loggerValue.toString());

            }
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
     * Method to close the O2C Withdraw by batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,foc_batch_items,
     * foc_batches,
     * channel_transfers_items,channel_transfers
     *
     * @param con
     * @param dataMap
     * @param currentLevel
     * @param userID
     * @param batchO2CMatserVO
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeO2CWithdrawByBatch(Connection con, LinkedHashMap dataMap, String currentLevel, String userID, BatchO2CMasterVO batchO2CMatserVO, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "closeO2CWithdrawByBatch";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_dataMap=");
        	loggerValue.append(dataMap);
        	loggerValue.append(" p_currentLevel=");
        	loggerValue.append(currentLevel);
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
            log.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement pstmtLoadNetworkStock = null;
        PreparedStatement pstmtUpdateNetworkStock = null;
        PreparedStatement pstmtInsertNetworkDailyStock = null;
        PreparedStatement pstmtSelectNetworkStock = null;
        PreparedStatement pstmtupdateSelectedNetworkStock = null;
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
        PreparedStatement psmtAppr1BatchO2CWithdrawItem = null;
        PreparedStatement psmtAppr2BatchO2CWithdrawItem = null;
        PreparedStatement psmtAppr3BatchO2CWithdrawItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
        PreparedStatement pstmtSelectCProfileProd = null;
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
        final KeyArgumentVO keyArgumentVO = null;
        final String[] argsArr = null;
        final ArrayList txnSmsMessageList = null;
        final ArrayList balSmsMessageList = null;
        Locale locale = null;
        String[] array = null;
        BTSLMessages messages = null;
        PushMessage pushMessage = null;
        int updateCount = 0;
        String o2cTransferID = null;
        String discountType = null;
        Double discountRate = 0.0;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        StringBuilder sqlBuffer =null;
        HashMap<String, HashMap<String, String>> tcpMap = null;
        
        if(tcpOn) {
        	 sqlBuffer = new StringBuilder(" SELECT cusers.transfer_profile_id, u.status userstatus, cusers.in_suspend, ");
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
         sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
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

        // The query below is used to load the network stock details for network
        // in between sender and receiver
        // This table will basically used to update the daily_stock_updated_on
        // and also to know how many
        // records are to be inseert in network_daily_stocks

        final String sqlLoadNetworkStock = batchO2CTransferQry.closeO2CWithdrawByBatchLoadNetworkStockQry();

        sqlBuffer = null;

        // Update daily_stock_updated_on with current date
        sqlBuffer = new StringBuilder("UPDATE network_stocks SET daily_stock_updated_on = ? ");
        sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ?");
        final String sqlUpdateNetworkStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlUpdateNetworkStock=" + sqlUpdateNetworkStock);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in network_daily_stocks
        sqlBuffer = new StringBuilder("INSERT INTO network_daily_stocks(wallet_date, wallet_type, network_code, network_code_for, ");
        sqlBuffer.append("product_code, wallet_created, wallet_returned, wallet_balance, wallet_sold, last_txn_no, ");
        sqlBuffer.append("last_txn_type, last_txn_balance, previous_balance, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String sqlInsertNetworkDailyStock = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlInsertNetworkDailyStock=" + sqlInsertNetworkDailyStock);
        }

        // Select the stock for the requested product for network.

        final String sqlSelectNetworkStock = batchO2CTransferQry.closeO2CWithdrawByBatchSelectNetworkStockQry();

        sqlBuffer = null;

        // Debit the network stock
        sqlBuffer = new StringBuilder(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance = ?, ");
        sqlBuffer.append(" wallet_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
        sqlBuffer.append(" modified_by =?, modified_on =? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" network_code = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code_for = ?  AND wallet_type = ?");
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
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp ) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
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

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be inseert in user_daily_balances table


        final String selectUserBalances = batchO2CTransferQry.closeO2CWithdrawByBatchSelectUserBalances();

        sqlBuffer = null;

        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateUserBalances=");
        	loggerValue.append(updateUserBalances);
            log.debug(methodName, loggerValue );
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
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertUserDailyBalances=");
        	loggerValue.append(insertUserDailyBalances);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // Select the balance of user for the perticuler product and network.
        sqlBuffer = new StringBuilder("  SELECT ");
        sqlBuffer.append(" balance ");
        sqlBuffer.append(" FROM user_balances ");
        sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance ");
        final String selectBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectBalance=");
        	loggerValue.append(selectBalance);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        final String updateBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateBalance=" );
        	loggerValue.append(updateBalance);
            log.debug(methodName, loggerValue);
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
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertBalance=");
        	loggerValue.append(insertBalance);
            log.debug(methodName,  loggerValue );
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
        sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        final String selectTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectTransferCounts=");
        	loggerValue.append(selectTransferCounts);
            log.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // Select the effective profile counters of user to be checked with
        // running counters of user
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value ,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
        strBuff.append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
        final String selectProfileCounts = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectProfileCounts=");
        	loggerValue.append(selectProfileCounts);
            log.debug(methodName,  loggerValue );
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
        sqlBuffer.append(" last_out_time = ? , last_transfer_id=?,last_transfer_date=? ");
        sqlBuffer.append(" WHERE user_id = ?  ");
        final String updateTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateTransferCounts=");
        	loggerValue.append(updateTransferCounts);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // Insert the record in user_transfer_counts (If no record found for
        // user running counters)
        sqlBuffer = new StringBuilder(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, monthly_out_count, ");
        sqlBuffer.append(" monthly_out_value, last_out_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        final String insertTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertTransferCounts=");
        	loggerValue.append(insertTransferCounts);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // foc_batch_items table for Batch O2C Withdraw
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no =?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1BatchO2CWithdrawItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv1BatchO2CWithdrawItems=");
        	loggerValue.append(sqlApprv1BatchO2CWithdrawItems);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        // If current level of approval is 2 then below query is used to updatwe
        // foc_batch_items table for Batch O2C Withdraw
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2BatchO2CWithdrawItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv2BatchO2CWithdrawItems=" + sqlApprv2BatchO2CWithdrawItems);
        }
        sqlBuffer = null;

        // If current level of approval is 3 then below query is used to updatwe
        // foc_batch_items table for Batch O2C Withdraw
        sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" third_approver_remarks = ?, ");
        sqlBuffer.append(" third_approved_by=? , third_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status = ?  ");
        final String sqlApprv3BatchO2CWithdrawItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlApprv3BatchO2CWithdrawItems=");
        	loggerValue.append(sqlApprv3BatchO2CWithdrawItems);
            log.debug(methodName,  loggerValue );
        }

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table

        final String selectItemsDetails = batchO2CTransferQry.closeO2CWithdrawByBatchSelectItemsDetails();

        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateO2CBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateO2CBatches=" + updateO2CBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY isModified=" + isModified);
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
        	loggerValue.append( "QUERY loadTransferProfileProduct=" );
        	loggerValue.append(loadTransferProfileProduct);
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
        	loggerValue.append( "QUERY isExistsTxnNum1=" );
        	loggerValue.append(isExistsTxnNum1);
            log.debug(methodName,  loggerValue );
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
        	loggerValue.append( "QUERY isExistsTxnNum2=" );
        	loggerValue.append(isExistsTxnNum2);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        sqlBuffer = new StringBuilder("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
        sqlBuffer.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
        sqlBuffer.append("FROM commission_profile_products cp ");
        sqlBuffer.append("WHERE cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        sqlBuffer.append("AND cp.transaction_type = ? AND cp.payment_mode = ? ");
        final String selectCommissionProfileQuery = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY selectCommissionProfileQuery=" + selectCommissionProfileQuery);
        }
        sqlBuffer = null;

        // The query bel;ow is used to insert the record in channel transfer
        // items table for the order that is closed
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price, sender_debit_quantity, receiver_credit_quantity,commision_quantity)  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(  "QUERY insertIntoChannelTransferItem="  );
        	loggerValue.append(insertIntoChannelTransferItem);
            log.debug(methodName,loggerValue );
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfers
        // table for the order that is cloaed
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers ");
        sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
        sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
        sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, from_user_id, grph_domain_code, ");
        sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
        sqlBuffer.append("  product_type, receiver_category_code, sender_grade_code, ");
        sqlBuffer.append(" sender_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
        sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
        sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
        sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
        sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
        sqlBuffer.append(" control_transfer,msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang) ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( "QUERY insertIntoChannelTransfer="  );
        	loggerValue.append(insertIntoChannelTransfer);
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
        	loggerValue.append( "QUERY selectBalanceInfoForMessage=");
        	loggerValue.append(selectBalanceInfoForMessage);
            log.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        try {
            BatchO2CItemsVO batchO2CItemVO = null;
            ChannelUserVO channelUserVO = null;
            ChannelUserVO channelUserVoObj = new ChannelUserVO();
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

            psmtAppr1BatchO2CWithdrawItem =  con.prepareStatement(sqlApprv1BatchO2CWithdrawItems);
            psmtAppr2BatchO2CWithdrawItem =  con.prepareStatement(sqlApprv2BatchO2CWithdrawItems);
            psmtAppr3BatchO2CWithdrawItem =  con.prepareStatement(sqlApprv3BatchO2CWithdrawItems);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster =  con.prepareStatement(updateO2CBatches);
            pstmtIsModified = con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = con.prepareStatement(loadTransferProfileProduct);
            pstmtIsTxnNumExists1 = con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = con.prepareStatement(isExistsTxnNum2);
            pstmtSelectCProfileProd = con.prepareStatement(selectCommissionProfileQuery);
            pstmtInsertIntoChannelTransferItems = con.prepareStatement(insertIntoChannelTransferItem);
            pstmtInsertIntoChannelTranfers = con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = con.prepareStatement(selectBalanceInfoForMessage);
            errorList = new ArrayList();
            final Iterator iterator = dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
            NetworkStockVO networkStocksVO = null;
            int dayDifference = 0;
            Date dailyStockUpdatedOn = null;
            long stock = -1;
            long stockReturned = -1;
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
            AutoNetworkStockBL autoNetworkStockBL = new AutoNetworkStockBL();
            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                batchO2CItemVO = (BatchO2CItemsVO) dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = batchO2CItemVO.getBatchId();
                }
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Executed batchO2CItemVO=" + batchO2CItemVO.toString());
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, batchO2CItemVO.getUserId());
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = channelUserVoObj;
                    channelUserVO.setUserID(batchO2CItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    
                    if(!tcpOn) {
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                    }else {
                        channelUserVO.setTransferProfileStatus(tcpMap.get(rs.getString("transfer_profile_id")).get("status"));//TCP
                    }
                    
                    language = rs.getString("phone_language");
                    country = rs.getString("country");
                    channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                    // (user status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : User is suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Commission profile suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Transfer profile suspend", "Approval level" + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : User is IN suspend", "Approval level" + currentLevel);
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : User not found", "Approval level" + currentLevel);
                    continue;
                }
                networkStocksVO = new NetworkStockVO();
                networkStocksVO.setProductCode(batchO2CMatserVO.getProductCode());
                networkStocksVO.setNetworkCode(batchO2CMatserVO.getNetworkCode());
                networkStocksVO.setNetworkCodeFor(batchO2CMatserVO.getNetworkCodeFor());

                // creating the channelTransferVO here since O2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth O2C items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(batchO2CMatserVO.getNetworkCode());
                channelTransferVO.setNetworkCodeFor(batchO2CMatserVO.getNetworkCodeFor());

                ChannelTransferBL.genrateWithdrawID(channelTransferVO);
                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch O2C item was created.
                channelTransferVO.setCreatedOn(batchO2CItemVO.getInitiatedOn());

                networkStocksVO.setLastTxnNum(o2cTransferID);

                networkStocksVO.setLastTxnBalance(batchO2CItemVO.getRequestedQuantity());
                networkStocksVO.setWalletBalance(batchO2CItemVO.getRequestedQuantity());

                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
                networkStocksVO.setModifiedBy(userID);
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
                pstmtLoadNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
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
                        if (log.isDebugEnabled()) {
                            log.debug("closeO2CWithdrawByBatch ", "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs1.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs1.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs1.getString("product_code"));
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
                            ++m;
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount);
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while insert in network daily stock table",
                                    "Approval level = " + currentLevel + "updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Termination of the procissing",
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
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                        updateCount = pstmtUpdateNetworkStock.executeUpdate();
                        // (record not updated properly in db) if this condition
                        // is true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            con.rollback();
                            errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while updating network daily stock table",
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
                ++m;
                pstmtSelectNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                rs2 = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockReturned = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs2.next()) {
                    stock = rs2.getLong("stock");
                    stockReturned = rs2.getLong("stock_returned");
                    previousNwStockToBeSetChnlTrfItems = stock;
                }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(p_messages.getMessage(p_locale, "label.all"), String.valueOf(batchO2CItemVO.getRecordNumber()) + " - " + p_messages.getMessage(
                        p_locale, "label.all"), p_messages.getMessage(p_locale, "batcho2cwithdraw.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", "Approval level = " + currentLevel);
                    throw new BTSLBaseException(this, methodName, "batcho2cwithdraw.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", "Approval level = " + currentLevel);
                    continue;
                }
                if (stock != -1) {
                    stock += networkStocksVO.getWalletbalance();
                }
                if (stockReturned != -1) {
                    stockReturned += networkStocksVO.getWalletbalance();
                }
                m = 0;
                // Deebit the network stock
                pstmtupdateSelectedNetworkStock.clearParameters();
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stock);
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stockReturned);
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
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while updating network stock table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
             // AutoNetworkStockCreation logic
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                	autoNetworkStockBL.networkStockThresholdValidation(networkStocksVO);
                }
                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setNetworkCode(networkStocksVO.getNetworkCode());
                networkStockTxnVO.setNetworkFor(networkStocksVO.getNetworkCodeFor());
                if (networkStocksVO.getNetworkCode().equals(batchO2CMatserVO.getNetworkCodeFor())) {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                } else {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                }
                // As discussed with sandeep in channel transfer table's
                // reference number field we have
                // to insert batch details id.So In network stock where channel
                // transfer table's reference number
                // was inserted, I insert batch detail id.
                networkStockTxnVO.setReferenceNo(batchO2CItemVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(batchO2CItemVO.getInitiatedOn());
                networkStockTxnVO.setRequestedQuantity(batchO2CItemVO.getRequestedQuantity());
                networkStockTxnVO.setApprovedQuantity(batchO2CItemVO.getRequestedQuantity());
                networkStockTxnVO.setInitiaterRemarks(batchO2CItemVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(batchO2CItemVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(batchO2CItemVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(batchO2CItemVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(batchO2CItemVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(batchO2CItemVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(batchO2CItemVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(batchO2CItemVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(batchO2CItemVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(userID);

                networkStockTxnVO.setTxnStatus(batchO2CItemVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_RETURN);
                networkStockTxnVO.setTxnType(PretupsI.CREDIT);
                networkStockTxnVO.setInitiatedBy(userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(batchO2CItemVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(batchO2CItemVO.getTransferMrp());

                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                networkItemsVO.setRequiredQuantity(batchO2CItemVO.getRequestedQuantity());
                networkItemsVO.setApprovedQuantity(batchO2CItemVO.getRequestedQuantity());
                networkItemsVO.setMrp(batchO2CItemVO.getTransferMrp());
                networkItemsVO.setProductCode(batchO2CMatserVO.getProductCode());
                networkItemsVO.setAmount(0);
                networkItemsVO.setProductCode(batchO2CMatserVO.getProductCode());
                networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
                // Added on 07/02/08
                networkItemsVO.setDateTime(batchO2CMatserVO.getBatchDate());
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
                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount);    // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while updating network stock TXN table",
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
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        "Approval level = " + currentLevel + ", updateCount =" + updateCount);
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
                        if (log.isDebugEnabled()) {
                            log.debug("closeOrdersByBatch ", "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
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
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
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
                                con.rollback();
                                errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while inserting user daily balances table",
                                    "Approval level = " + currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO,
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
                            errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO,
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
                pstmtSelectBalance.setString(m, batchO2CMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, batchO2CMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, batchO2CMatserVO.getNetworkCodeFor());
                rs4 = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                if (rs4.next()) {
                    balance = rs4.getLong("balance");
                    try {
                        if (rs4 != null) {
                            rs4.close();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                }
                if (batchO2CItemVO.getRequestedQuantity() > balance) {
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.error.requestedqtynotenough"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO,
                        "MINOR : Withdraw requested quantity should less than current channel user balance ", "");
                    continue;
                } else if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    balance -= batchO2CItemVO.getRequestedQuantity();
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                // Transfer Profile check will performed by flag basis. because
                // it may. O2C withdraw should not follow this check.
                // BATCH_O2C_WITHDRAW_TRF_PROFILE_CHECK_REQUIRED=false means
                // check will not performing
                boolean isTrfCtrlCheckReq;
                try {
                    isTrfCtrlCheckReq = Boolean.getBoolean(Constants.getProperty("BATCH_O2C_WITHDRAW_TRF_PROFILE_CHECK_REQUIRED"));
                } catch (Exception e) {

                    log.error(methodName, "Value is not defined in Constant props for BATCH_O2C_WITHDRAW_TRF_PROFILE_CHECK_REQUIRED ");
                    log.errorTrace(methodName, e);
                    isTrfCtrlCheckReq = false;
                }

                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, batchO2CItemVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, batchO2CMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                rs5 = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs5.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(batchO2CMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs5.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs5.getLong("max_balance"));
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else if (isTrfCtrlCheckReq) {
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + currentLevel);
                    continue;
                }
                maxBalance = transferProfileProductVO.getMaxBalanceAsLong();
                // (max balance reach for the receiver) if this condition is
                // true then made entry in logs and leave this data.
                if (isTrfCtrlCheckReq && (maxBalance < balance)) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : User Max balance reached", "Approval level = " + currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the
                // withdraw value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (isTrfCtrlCheckReq && (balance == -1 && maxBalance < batchO2CItemVO.getRequestedQuantity())) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : User Max balance reached", "Approval level = " + currentLevel);
                    continue;
                }
                if (!isNotToExecuteQuery) {
                    m = 0;
                    // update
                    if (balance > -1) {
                        pstmtUpdateBalance.clearParameters();
                        handlerStmt = pstmtUpdateBalance;
                    } else {
                        // insert
                        pstmtInsertBalance.clearParameters();
                        handlerStmt = pstmtInsertBalance;
                        balance = batchO2CItemVO.getRequestedQuantity();
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
                    handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    // where
                    ++m;
                    handlerStmt.setString(m, batchO2CMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, batchO2CMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, batchO2CMatserVO.getNetworkCodeFor());
                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + currentLevel);
                        continue;
                    }
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
                    countsVO.setUserID(batchO2CItemVO.getUserId());

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
                pstmtSelectProfileCounts.setString(m, batchO2CItemVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, batchO2CMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                rs7 = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
                if (rs7.next()) {
                    transferProfileVO = new TransferProfileVO();
                    transferProfileVO.setProfileId(rs7.getString("profile_id"));
                    transferProfileVO.setDailyInCount(rs7.getLong("daily_transfer_out_count"));
                    transferProfileVO.setDailyInValue(rs7.getLong("daily_transfer_out_value"));
                    transferProfileVO.setWeeklyInCount(rs7.getLong("weekly_transfer_out_count"));
                    transferProfileVO.setWeeklyInValue(rs7.getLong("weekly_transfer_out_value"));
                    transferProfileVO.setMonthlyInCount(rs7.getLong("monthly_transfer_out_count"));
                    transferProfileVO.setMonthlyInValue(rs7.getLong("monthly_transfer_out_value"));
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Transfer profile not found", "Approval level = " + currentLevel);
                    continue;
                }
                /*
                 * These all changes need to perform on the basis of Flag.
                 * Because Batch Withdraw may not require to check for out
                 * limit.
                 */
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.
                if (isTrfCtrlCheckReq && transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Daily transfer out count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (isTrfCtrlCheckReq && transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.dailyinvaluereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Daily transfer out value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (isTrfCtrlCheckReq && transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Weekly transfer out count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (isTrfCtrlCheckReq && transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + batchO2CItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.weeklyinvaluereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Weekly transfer out value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (isTrfCtrlCheckReq && transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Monthly transfer out count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (isTrfCtrlCheckReq && transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + batchO2CItemVO.getRequestedQuantity())) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.monthlyinvaluereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Monthly transfer out value reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                countsVO.setUserID(channelUserVO.getUserID());

                countsVO.setDailyOutCount(countsVO.getDailyOutCount() + 1);
                countsVO.setWeeklyOutCount(countsVO.getWeeklyOutCount() + 1);
                countsVO.setMonthlyOutCount(countsVO.getMonthlyOutCount() + 1);
                countsVO.setDailyOutValue(countsVO.getDailyOutValue() + batchO2CItemVO.getRequestedQuantity());
                countsVO.setWeeklyOutValue(countsVO.getWeeklyOutValue() + batchO2CItemVO.getRequestedQuantity());
                countsVO.setMonthlyOutValue(countsVO.getMonthlyOutValue() + batchO2CItemVO.getRequestedQuantity());

                countsVO.setLastOutTime(date);
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
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastOutTime()));
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
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyOutCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyOutValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyOutCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyOutValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyOutCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyOutValue());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastOutTime()));
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
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + currentLevel);
                    } else {
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, batchO2CItemVO.getBatchDetailId());
                rs8 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                // check record is modified or not
                if (rs8.next()) {
                    newlastModified = rs8.getTimestamp("modified_on");
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Record is already modified", "Approval level = " + currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(batchO2CItemVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : Record is already modified", "Approval level = " + currentLevel);
                    continue;
                }
                // (external txn number is checked)
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue() && !BTSLUtil.isNullString(batchO2CItemVO.getExtTxnNo())) {
                    // check in foc_batch_item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, batchO2CItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, batchO2CItemVO.getBatchDetailId());
                    rs9 = pstmtIsTxnNumExists1.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs9.next()) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : External transaction number already exists for O2C Batch",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                    // check in channel transfer table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, batchO2CItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs10 = pstmtIsTxnNumExists2.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs10.next()) {
                        con.rollback();
                        try {
                            if (rs10 != null) {
                                rs10.close();
                            }
                        } catch (Exception e) {
                            log.errorTrace(methodName, e);
                        }
                        errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2cwithdraw.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : External transaction number already exists in CHANNEL TRF",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                // Accessing the Discount Type and Discount Rate it is using in
                // calculateMRPWithTaxAndDiscount
                pstmtSelectCProfileProd.clearParameters();
                m = 0;
                ++m;
                pstmtSelectCProfileProd.setString(m, batchO2CMatserVO.getProductCode());
                ++m;
                pstmtSelectCProfileProd.setString(m, batchO2CItemVO.getCommissionProfileSetId());
                ++m;
                pstmtSelectCProfileProd.setString(m, batchO2CItemVO.getCommissionProfileVer());
                ++m;
                pstmtSelectCProfileProd.setString(m, (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL);
                ++m;
                pstmtSelectCProfileProd.setString(m, PretupsI.ALL);
                rs11 = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                if (!rs11.next()) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog("initiateBatchO2CWithdraw", batchO2CMatserVO, batchO2CItemVO, "FAIL : Commission profile for this product is not defined",
                        "");
                    continue;
                }
                discountType = rs11.getString("discount_type");
                discountRate = rs11.getDouble("discount_rate");

                // If level 1 apperoval then set parameters in
                // psmtAppr1BatchO2CWithdrawItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                    psmtAppr1BatchO2CWithdrawItem.clearParameters();
                    batchO2CItemVO.setFirstApprovedBy(userID);
                    batchO2CItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, userID);
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, userID);
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getStatus());
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(batchO2CItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1BatchO2CWithdrawItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1BatchO2CWithdrawItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2BatchO2CWithdrawItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
                    psmtAppr2BatchO2CWithdrawItem.clearParameters();
                    batchO2CItemVO.setSecondApprovedBy(userID);
                    batchO2CItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, userID);
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, userID);
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getStatus());
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(batchO2CItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2BatchO2CWithdrawItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2BatchO2CWithdrawItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr1BatchO2CWithdrawItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(currentLevel)) {
                    psmtAppr3BatchO2CWithdrawItem.clearParameters();
                    batchO2CItemVO.setThirdApprovedBy(userID);
                    batchO2CItemVO.setThirdApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, userID);
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getThirdApproverRemarks());
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, userID);
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getStatus());
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(batchO2CItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, batchO2CItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr3BatchO2CWithdrawItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr3BatchO2CWithdrawItem.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                channelTransferItemVO = new ChannelTransferItemsVO();

                channelTransferVO.setCanceledOn(batchO2CItemVO.getCancelledOn());
                channelTransferVO.setCanceledBy(batchO2CItemVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(batchO2CItemVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(batchO2CItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(batchO2CItemVO.getCommissionProfileVer());
                channelTransferVO.setCreatedBy(batchO2CItemVO.getInitiatedBy());
                channelTransferVO.setDomainCode(batchO2CMatserVO.getDomainCode());
                channelTransferVO.setReceiverDomainCode(batchO2CMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(batchO2CItemVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(batchO2CItemVO.getExtTxnNo());
                channelTransferVO.setFinalApprovedBy(batchO2CItemVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(batchO2CItemVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(batchO2CItemVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(batchO2CItemVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(batchO2CItemVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(batchO2CItemVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(batchO2CItemVO.getCategoryCode());
                channelTransferVO.setBatchNum(batchO2CItemVO.getBatchId());
                channelTransferVO.setBatchDate(batchO2CMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(channelUserVO.getUserID());
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(batchO2CMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
                channelTransferVO.setSenderGradeCode(batchO2CItemVO.getGradeCode());
                channelTransferVO.setSenderTxnProfile(batchO2CItemVO.getTxnProfile());
                channelTransferVO.setReferenceNum(batchO2CItemVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(batchO2CItemVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(batchO2CItemVO.getStatus());
                channelTransferVO.setThirdApprovedBy(batchO2CItemVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(batchO2CItemVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(batchO2CItemVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax1(batchO2CItemVO.getTax1Value());
                channelTransferVO.setTotalTax2(batchO2CItemVO.getTax2Value());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
                channelTransferVO.setTransferDate(batchO2CItemVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(batchO2CItemVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(batchO2CItemVO.getTransferMrp());

                channelTransferItemVO.setApprovedQuantity(batchO2CItemVO.getRequestedQuantity());
                channelTransferItemVO.setCommProfileDetailID(batchO2CItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(batchO2CItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(batchO2CItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(batchO2CItemVO.getCommissionValue());
                channelTransferItemVO.setProductCode(batchO2CMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousNwStockToBeSetChnlTrfItems);// values
                // swap
                channelTransferItemVO.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);// values
                // swap
                channelTransferItemVO.setRequiredQuantity(batchO2CItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(batchO2CItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(batchO2CItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(batchO2CItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(batchO2CItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(batchO2CItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(batchO2CItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(batchO2CItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(batchO2CItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(batchO2CItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(batchO2CMatserVO.getProductMrp());
                channelTransferItemVO.setPreviousBalance(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setRequestedQuantity(PretupsBL.getDisplayAmount(batchO2CItemVO.getRequestedQuantity()));
                channelTransferItemVO.setDiscountType(discountType);
                channelTransferItemVO.setDiscountRate(discountRate);
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousNwStockToBeSetChnlTrfItems);// values
                // swap
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);// values
                // swap
                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(batchO2CMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);
                channelTransferVO.setTotalTax3(channelTransferItemVO.getTax3Value());
                channelTransferVO.setPayableAmount(channelTransferItemVO.getPayableAmount());
                channelTransferVO.setNetPayableAmount(channelTransferItemVO.getNetPayableAmount());

                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append( "Exiting: channelTransferVO=");
                	loggerValue.append(channelTransferVO.toString());
                    log.debug(methodName, loggerValue );
                }
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append( "Exiting: channelTransferItemVO=");
                	loggerValue.append(channelTransferItemVO.toString());
                    log.debug(methodName,  loggerValue );
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
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSenderGradeCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSenderTxnProfile());
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
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemVO.getStatus());
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
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemVO.getMsisdn());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverDomainCode());

                // By sandeep ID TOG001
                // to geographical domain also inserted as the geogrpahical
                // domain that will help in reports
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverGgraphicalDomainCode());

                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDefaultLang());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondLang());

                // ends here
                // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while inserting in channel transfer table",
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
                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres DB
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemVO.getMsisdn(), String.valueOf(batchO2CItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "FAIL : DB Error while inserting in channel transfer items table",
                        "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                con.commit();
                BatchO2CProcessLog.detailLog(methodName, batchO2CMatserVO, batchO2CItemVO, "PASS : Order is closed successfully",
                    "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, batchO2CMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, batchO2CMatserVO.getNetworkCodeFor());
                rs12 = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList = new ArrayList();
                while (rs12.next()) {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs12.getString("product_code"));
                    balancesVO.setBalance(rs12.getLong("balance"));
                    balancesVO.setProductShortCode(rs12.getString("product_short_code"));
                    balancesVO.setProductShortName(rs12.getString("short_name"));
                    userbalanceList.add(balancesVO);
                }
                locale = new Locale(language, country);
                final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                    language = channelTransferVO.getDefaultLang();
                } else {
                    language = channelTransferVO.getSecondLang();
                }
                if (BTSLUtil.isNullString(language)) {
                    language = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
                }
                Object[] smsListArr = null;
                channelTransferVO.setToUserID(channelTransferVO.getFromUserID());
                smsListArr = ChannelTransferBL.prepareSMSMessageListForReceiver(con, channelTransferVO, PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_TXNSUBKEY,
                    PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_BALSUBKEY);
                array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[0]), PretupsBL.getDisplayAmount(channelTransferVO
                    .getNetPayableAmount()), BTSLUtil.getMessage(locale, (ArrayList) smsListArr[1]) };
                messages = new BTSLMessages(PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_SMS1, array);
                pushMessage = new PushMessage(batchO2CItemVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                pushMessage.push();
            }// end of while
        }// end of try
        catch (BTSLBaseException be) {
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
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeO2CWithdrawByBatch]", "", "",
                "", loggerValue.toString());
            BatchO2CProcessLog.batchO2CMasterLog(methodName, batchO2CMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeO2CWithdrawByBatch]", "", "",
                "",  loggerValue.toString() );
            BatchO2CProcessLog.batchO2CMasterLog(methodName, batchO2CMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
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
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
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
                if (psmtAppr1BatchO2CWithdrawItem != null) {
                    psmtAppr1BatchO2CWithdrawItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2BatchO2CWithdrawItem != null) {
                    psmtAppr2BatchO2CWithdrawItem.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr3BatchO2CWithdrawItem != null) {
                    psmtAppr3BatchO2CWithdrawItem.close();
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
                if (pstmtSelectCProfileProd != null) {
                    pstmtSelectCProfileProd.close();
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
                rs13 = null;
                rs13 = pstmtSelectItemsDetails.executeQuery();
                // Check the final status to be updated in master after
                // processing all records of batch
                if (rs13.next()) {
                    final int totalCount = rs13.getInt("batch_total_record");
                    final int closeCount = rs13.getInt("closed");
                    final int cnclCount = rs13.getInt("cncl");
                    try {
                        if (rs13 != null) {
                        	rs13.close();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
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
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batcho2cwithdraw.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.batchO2CMasterLog(methodName, batchO2CMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                        loggerValue.setLength(0);
                        loggerValue.append("Error while updating FOC_BATCHES table. Batch id=");
                        loggerValue.append(batch_ID);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "BatchO2CTransferDAO[closeO2CWithdrawByBatch]", "", "", "",  loggerValue.toString());
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
                loggerValue.setLength(0);
                loggerValue.append("SQLException : ");
                loggerValue.append(sqe);
                log.error(methodName, loggerValue);
                log.errorTrace(methodName, sqe);
                loggerValue.setLength(0);
                loggerValue.append( "SQL Exception:" );
                loggerValue.append(sqe.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeO2CWithdrawByBatch]", "",
                    "", "",loggerValue.toString() );
                BatchO2CProcessLog.batchO2CMasterLog(methodName, batchO2CMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + currentLevel);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
                loggerValue.append( "Exception : " );
                loggerValue.append(ex);
                log.error(methodName,  loggerValue );
                log.errorTrace(methodName, ex);
                loggerValue.setLength(0);
                loggerValue.append( "Exception:" );
                loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeO2CWithdrawByBatch]", "",
                    "", "",  loggerValue.toString());
                BatchO2CProcessLog.batchO2CMasterLog(methodName, batchO2CMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + currentLevel);
            }finally{
            	try {
                    if (rs13 != null) {
                        rs13.close();
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
                if (log.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exiting: errorList size=");
                	loggerValue.append(errorList.size());
                    log.debug(methodName, loggerValue);
                }
            }

        }
        return errorList;
    }

    /**
     * This method load Geographies according to batch id.
     * loadBatchGeographiesList
     *
     * @param con
     *            Connection
     * @param batchId
     *            String
     * @return ArrayList list
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchGeographiesList(Connection con, String batchId) throws BTSLBaseException {
        final String methodName = "loadBatchGeographiesList";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_batchId=");
        	loggerValue.append(batchId);
            log.debug(methodName,  loggerValue );
        }


        final StringBuilder strBuff = new StringBuilder("SELECT batch_id, geography_code, date_time FROM foc_batch_geographies WHERE  batch_id=? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,  loggerValue );
        }
        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

            pstmt.setString(1, batchId);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            BatchO2CGeographyVO batchO2CGeographyVO = null;
            while (rs.next()) {
                batchO2CGeographyVO = new BatchO2CGeographyVO();
                batchO2CGeographyVO.setBatchId(rs.getString("batch_id"));
                batchO2CGeographyVO.setGeographyCode(rs.getString("geography_code"));
                // Added on 07/02/08 for addition of new date_time column in the
                // table FOC_BATCH_GEOGRAPHIES.
                batchO2CGeographyVO.setDateTime(rs.getTimestamp("date_time"));
                list.add(batchO2CGeographyVO);
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchGeographiesList]", "",
                "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchGeographiesList]", "",
                "", "", loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
             if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: loadBatchGeographiesList size=");
            	loggerValue.append(list.size());
                log.debug(methodName, loggerValue );
			}
        }
        return list;
    }

    /**
     * This method load Batch details according to batch id.
     * loadBatchDetailsList
     *
     * @param con
     *            Connection
     * @param batchId
     *            String
     * @return ArrayList list
     * @throws BTSLBaseException
     *             ved.sharma
     */
    public ArrayList loadBatchDetailsList(Connection con, String batchId) throws BTSLBaseException {
        final String methodName = "loadBatchDetailsList";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_batchId=");
        	loggerValue.append(batchId);
            log.debug(methodName,  loggerValue );
        }



        final String sqlSelect = batchO2CTransferQry.loadBatchDetailsListQry();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,  loggerValue );
        }
        BatchO2CMasterVO batchO2CMasterVO = null;
        BatchO2CItemsVO batchO2CItemsVO = null;
        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

            pstmt.setString(1, batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                batchO2CMasterVO = new BatchO2CMasterVO();
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

                batchO2CItemsVO = new BatchO2CItemsVO();
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

                batchO2CMasterVO.setBatchO2CItemsVO(batchO2CItemsVO);

                list.add(batchO2CMasterVO);
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqe);
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchDetailsList]", "", "",
                "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchDetailsList]", "", "",
                "",loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: loadBatchDetailsList  list.size()=");
            	loggerValue.append(list.size());
                log.debug(methodName,  loggerValue );
			}
        }
        return list;
    }

    /**
     * Method initiateBatchO2CWithdraw
     * This method used for the batch o2c withdraw initiation. The main purpose
     * of this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     *
     * @param con
     *            Connection
     * @param batchMasterVO
     *            BatchO2CMasterVO
     * @param batchItemsList
     *            ArrayList
     * @param messages
     *            MessageResources
     * @param locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList initiatebatcho2cwithdraw(Connection con, BatchO2CMasterVO batchMasterVO, ArrayList batchItemsList, MessageResources messages, Locale locale, String trfType, String trfSubType) throws BTSLBaseException {
        final String methodName = "initiateBatchO2CWithdraw";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered.... p_batchMasterVO=");
        	loggerValue.append(batchMasterVO);
        	loggerValue.append(", p_batchItemsList.size() = ");
        	loggerValue.append(batchItemsList.size());
        	loggerValue.append(", p_batchItemsList=");
        	loggerValue.append(batchItemsList);
        	loggerValue.append("p_locale=" );
        	loggerValue.append(locale);
        	loggerValue.append(", p_trfType=");
        	loggerValue.append(trfType);
        	loggerValue.append(", p_trfSubType=");
        	loggerValue.append(trfSubType);
            log.debug(methodName,loggerValue );
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        Boolean externalTxnUnique = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        Boolean transactionTypeAlwd = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffSelectExtTxnID1 Query =");
        	loggerValue.append(strBuffSelectExtTxnID1);
            log.debug(methodName,  loggerValue);
        }

        PreparedStatement pstmtSelectUserBalance = null;
        ResultSet rsSelectUserBalance = null;
        final StringBuilder strBuffSelectUserBalance = new StringBuilder(" SELECT balance FROM user_balances ");
        strBuffSelectUserBalance.append("WHERE user_id = ?");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffSelectUserBalance Query =");
        	loggerValue.append(strBuffSelectUserBalance);
            log.debug(methodName,  loggerValue );
            // ends here
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffSelectExtTxnID2 Query =");
        	loggerValue.append(strBuffSelectExtTxnID2);
            log.debug(methodName,  loggerValue);
            // ends here
        }

        // for loading the O2C transfer rule for Batch O2C withdraw
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuilder strBuffSelectTrfRule = new StringBuilder(" SELECT transfer_rule_id,transfer_type, withdraw_allowed, first_approval_limit, second_approval_limit ");
        strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ? AND ");
        strBuffSelectTrfRule.append("from_category = 'OPT' AND to_category = ? AND status = 'Y' AND type = 'OPT' ");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffSelectTrfRule Query =");
        	loggerValue.append(strBuffSelectTrfRule);
            log.debug(methodName,  loggerValue);
            // ends here
        }

        // for loading the products associated with the transfer rule
        PreparedStatement pstmtSelectTrfRuleProd = null;
        ResultSet rsSelectTrfRuleProd = null;
        final StringBuilder strBuffSelectTrfRuleProd = new StringBuilder("SELECT 1 FROM chnl_transfer_rules_products ");
        strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
        if (log.isDebugEnabled()) {
          loggerValue.setLength(0);
          loggerValue.append("strBuffSelectTrfRuleProd Query =");
          loggerValue.append(strBuffSelectTrfRuleProd);
            log.debug(methodName, loggerValue );
            // ends here
        }

        // for loading the products associated with the commission profile
        PreparedStatement pstmtSelectCProfileProd = null;
        ResultSet rsSelectCProfileProd = null;
        final StringBuilder strBuffSelectCProfileProd = new StringBuilder("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        strBuffSelectCProfileProd.append("AND cp.transaction_type = ? AND cp.payment_mode = ? ");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffSelectCProfileProd Query =");
        	loggerValue.append(strBuffSelectCProfileProd);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffSelectCProfileProdDetail Query =");
        	loggerValue.append(strBuffSelectCProfileProdDetail);
            log.debug(methodName,  loggerValue );
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
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffSelectTProfileProd Query =");
        	loggerValue.append(strBuffSelectTProfileProd);
            log.debug(methodName,  loggerValue );
            // ends here
        }

        // insert data in the batch master table
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffInsertBatchMaster Query =");
        	loggerValue.append(strBuffInsertBatchMaster);
            log.debug(methodName,  loggerValue );
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffInsertBatchGeo Query =");
        	loggerValue.append(strBuffInsertBatchGeo);
            log.debug(methodName,  loggerValue );
            // ends here
        }

        // insert data in the batch items table
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffInsertBatchItems Query =");
        	loggerValue.append(strBuffInsertBatchItems);
            log.debug(methodName, loggerValue);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE foc_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("strBuffUpdateBatchMaster Query =");
        	loggerValue.append(strBuffUpdateBatchMaster);
            log.debug(methodName,  loggerValue );
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = con.prepareStatement(strBuffSelectExtTxnID2.toString());
            pstmtSelectUserBalance = con.prepareStatement(strBuffSelectUserBalance.toString());
            pstmtSelectTrfRule = con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = con.prepareStatement(strBuffSelectTProfileProd.toString());

            pstmtInsertBatchMaster = con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = con.prepareStatement(strBuffInsertBatchGeo.toString());
            pstmtInsertBatchItems = con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = con.prepareStatement(strBuffUpdateBatchMaster.toString());
            ChannelTransferRuleVO rulesVO = null;
            int index = 0;
            BatchO2CItemsVO batchItemsVO = null;

            final HashMap transferRuleMap = new HashMap();
            final HashMap transferRuleNotExistMap = new HashMap();
            final HashMap transferRuleProdNotExistMap = new HashMap();
            final HashMap transferProfileMap = new HashMap();
            long requestedValue = 0;
            long minTrfValue = 0;
            long maxTrfValue = 0;
            long multipleOf = 0;
            ArrayList transferItemsList = new ArrayList();
            ChannelTransferItemsVO channelTransferItemsVO = null;
            ChannelTransferVO channelTransferVoObj = new ChannelTransferVO();
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

            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getDefaultLang());
            ++index;
            pstmtInsertBatchMaster.setString(index, batchMasterVO.getSecondLang());
            ++index;
            pstmtInsertBatchMaster.setString(index, trfType);
            ++index;
            pstmtInsertBatchMaster.setString(index, trfSubType);

            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                con.rollback();
                log.error(methodName, "Unable to insert in the batch master table.");
                BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[initiateBatchO2CWithdraw]",
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
                    log.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "BatchO2CTransferDAO[initiateBatchO2CWithdraw]", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            String msgArr[] = null;
            for (int i = 0, j = batchItemsList.size(); i < j; i++) {
                batchItemsVO = (BatchO2CItemsVO) batchItemsList.get(i);
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
                        errorVO.setOtherInfo2(messages.getMessage(locale, "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist BATCCH O2C Withdraw", "");
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
                            "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here
                pstmtSelectUserBalance.setString(1, batchItemsVO.getUserId());
                rsSelectUserBalance = pstmtSelectUserBalance.executeQuery();
                if (rsSelectUserBalance.next()) {
                    if (batchItemsVO.getRequestedQuantity() > rsSelectUserBalance.getLong("balance")) {
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.error.requestedqtynotenough"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO,
                            "MINOR : Withdraw requested quantity should less than current channel user balance ", "");
                        continue;
                    }
                } else {
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.error.usrbalnotfound"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "MINOR : Channel user balance not found", "");
                    continue;
                }
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
                                rulesVO.setTransferType(rsSelectTrfRule.getString("transfer_type"));
                                rulesVO.setWithdrawAllowed(rsSelectTrfRule.getString("withdraw_allowed"));
                                rulesVO.setFirstApprovalLimit(rsSelectTrfRule.getLong("first_approval_limit"));
                                rulesVO.setSecondApprovalLimit(rsSelectTrfRule.getLong("second_approval_limit"));
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
                                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.prodnotintrfrule"));
                                    errorList.add(errorVO);
                                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                                    "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.trfrulenotdefined"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.prodnotintrfrule"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.trfrulenotdefined"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchItemsVO.getCategoryCode());

                // O2C Withdrawn Transfer Rule check will be flag wise. pass
                // true if check is required.
                boolean isTrfCtrlCheckReq;
                try {
                    isTrfCtrlCheckReq = Boolean.getBoolean(Constants.getProperty("BATCH_O2C_WITHDRAW_TRF_PROFILE_CHECK_REQUIRED"));
                } catch (Exception e) {
                    log.error(methodName, "Value is not defined in Constant props for BATCH_O2C_WITHDRAW_TRF_PROFILE_CHECK_REQUIRED ");
                    log.errorTrace(methodName, e);
                    isTrfCtrlCheckReq = false;
                }

                if (isTrfCtrlCheckReq && PretupsI.NO.equals(rulesVO.getWithdrawAllowed())) {
                    // put error according to the transfer rule O2C Withdraw is
                    // not allowed.
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.notallowed"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : According to the transfer rule O2C Withdraw is not allowed", "");
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
                            "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
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
                ++index;
                pstmtSelectCProfileProd.setString(index, transactionTypeAlwd?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL);
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                if (!rsSelectCProfileProd.next()) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Commission profile for this product is not defined", "");
                    continue;
                }
                requestedValue = batchItemsVO.getRequestedQuantity();
                minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                if (isTrfCtrlCheckReq && (minTrfValue > requestedValue || maxTrfValue < requestedValue)) {
                    msgArr = new String[3];
                    msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                    msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                    msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                    // put error requested quantity is not between min and max
                    // values
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.qtymaxmin", msgArr));
                    msgArr = null;
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not between min and max values",
                        "minTrfValue=" + minTrfValue + ", maxTrfValue=" + maxTrfValue);
                    continue;
                }
                multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                if (isTrfCtrlCheckReq && requestedValue % multipleOf != 0) {
                    // put error requested quantity is not multiple of
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.notmulof", new String[] { PretupsBL.getDisplayAmount(multipleOf) }));
                    errorList.add(errorVO);
                    BatchO2CProcessLog
                        .detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not in multiple value", "multiple of=" + multipleOf);
                    continue;
                }

                index = 0;
                ++index;
                pstmtSelectCProfileProdDetail.setString(index, rsSelectCProfileProd.getString("comm_profile_products_id"));
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail.executeQuery();
                pstmtSelectCProfileProdDetail.clearParameters();
                if (!rsSelectCProfileProdDetail.next()) {
                    // put error commission profile slab is not define for the
                    // requested value
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2cwithdraw.initiatebatcho2cwdr.msg.error.commslabnotdefined"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : Commission profile slab is not define for the requested value", "");
                    continue;
                }
                // to calculate tax
                transferItemsList.clear();
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
                // make a new channel TransferVO to transfer into the method
                // during tax calculataion
                final ChannelTransferVO channelTransferVO = channelTransferVoObj;
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);

                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);

                // taxes on O2C Withdraw required
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
                pstmtInsertBatchItems.setDate(index, BTSLUtil.getSQLDateFromUtilDate(batchItemsVO.getTransferDate()));
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
                pstmtInsertBatchItems.setString(index, String.valueOf(channelTransferItemsVO.getRequiredQuantity()));
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getProductTotalMRP());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    // put error record can not be inserted
                    log.error(methodName, "Record cannot be inserted in batch items table");
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }// for loop for the batch items
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[initiateBatchO2CWithdraw]", "",
                "", "",  loggerValue.toString());
            BatchO2CProcessLog.batchO2CMasterLog(methodName, batchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[initiateBatchO2CWithdraw]", "",
                "", "", loggerValue.toString());
            BatchO2CProcessLog.batchO2CMasterLog(methodName, batchMasterVO, "FAIL : Exception:" + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
                if (rsSelectUserBalance != null) {
                    rsSelectUserBalance.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserBalance != null) {
                    pstmtSelectUserBalance.close();
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
                    BatchO2CProcessLog.batchO2CMasterLog(methodName, batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
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
                        log.error(methodName, "Unable to Update the batch size in master table..");
                        con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "BatchO2CTransferDAO[initiateBatchO2CWithdraw]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batchMasterVO.getBatchId());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: errorList.size()=");
            	loggerValue.append(errorList.size());
                log.debug(methodName,  loggerValue);
            }
        }
        return errorList;
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
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:p_oldlastModified=");
        	loggerValue.append(oldlastModified);
        	loggerValue.append(",p_batchID=");
        	loggerValue.append(batchID);
            log.debug(methodName,  loggerValue );
        }


        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM foc_batches WHERE batch_id = ? ";
        java.sql.Timestamp newlastModified = null;
        if (oldlastModified == 0) {
            return false;
        }
        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlRecordModified);) {
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("sqlRecordModified=");
            	loggerValue.append(sqlRecordModified);
                log.debug(methodName,  loggerValue);
            }

            pstmtSelect.setString(1, batchID);
            try( ResultSet rs = pstmtSelect.executeQuery();)
            {
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
        }
        }// end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[isBatchModified]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[isBatchModified]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
             if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:modified=");
            	loggerValue.append(modified);
                log.debug(methodName, loggerValue);
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
        StringBuilder loggerValue= new StringBuilder();
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("Entered   p_userID ");
    		loggerValue.append(userID);
            log.debug(methodName,  loggerValue);
        }
        boolean isExist = false;


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
            try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
            {
            int i = 1;
            pstmt.setString(i, userID);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            i++;
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[isPendingTransactionExist]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[isPendingTransactionExist]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  isExist=");
            	loggerValue.append(isExist);
                log.debug(methodName,  loggerValue );
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
        StringBuilder loggerValue= new StringBuilder();
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
    		loggerValue.append("Entered   p_batchID ");
    		loggerValue.append(batchID);
    		loggerValue.append(" p_newStatus=");
    		loggerValue.append(newStatus);
    		loggerValue.append(" p_oldStatus=");
    		loggerValue.append(oldStatus);
            log.debug(methodName, loggerValue);
        }

        int updateCount = -1;
        try {
            final StringBuilder sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? ");
            sqlBuffer.append(" WHERE batch_id=? AND status=? ");
            final String updateO2CBatches = sqlBuffer.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("QUERY updateFOCBatches=");
            	loggerValue.append(updateO2CBatches);
                log.debug(methodName,  loggerValue );
            }

            try(PreparedStatement pstmt = con.prepareStatement(updateO2CBatches);)
            {
            int i = 1;
            pstmt.setString(i, newStatus);
            i++;
            pstmt.setString(i, batchID);
            i++;
            pstmt.setString(i, oldStatus);
            i++;
            updateCount = pstmt.executeUpdate();
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[updateBatchStatus]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[updateBatchStatus]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  updateCount=");
            	loggerValue.append(updateCount);
                log.debug(methodName,  loggerValue);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:  updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    public ArrayList<O2CBatchMasterVO> loadO2CTransferApprovalList(Connection con, String domainCode, String categoryCode, String geoDomain, String userId, String approvalLevel) throws BTSLBaseException {
    	 final String methodName = "loadO2CTransferApprovalList";
    	 StringBuilder loggerValue= new StringBuilder();
         if (log.isDebugEnabled()) {
         	loggerValue.append("Entered domainCode= ");
         	loggerValue.append(domainCode);
         	loggerValue.append("  categoryCode= ");
         	loggerValue.append(categoryCode);
         	loggerValue.append("  geoDomain= ");
         	loggerValue.append(geoDomain);
         	loggerValue.append("  userId= ");
         	loggerValue.append(userId);
         	loggerValue.append("  approvalLevel= ");
         	loggerValue.append(approvalLevel);
             log.debug(methodName,  loggerValue );
         }

         final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "'";
         final String sqlSelect = batchO2CTransferQry.loadO2CTransferApprovalListQry(approvalLevel, statusUsed, categoryCode, geoDomain, domainCode);
         if (log.isDebugEnabled()) {
         	loggerValue.setLength(0);
         	loggerValue.append("QUERY sqlSelect=");
         	loggerValue.append(sqlSelect);
             log.debug(methodName,  loggerValue );
         }
         PreparedStatement pstmt = null;
         ResultSet rs = null;
         ArrayList<O2CBatchMasterVO> batchTransferList= new ArrayList<O2CBatchMasterVO>();

         try{
        	 pstmt = con.prepareStatement(sqlSelect);
             int i=1;
             pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
             i++;
             pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
             i++;
             pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
             i++;
             pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
             i++;
             pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
             i++;
             pstmt.setString(i, userId);
             i++;
             if (!categoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            	pstmt.setString(i, categoryCode);
            	i++;
             }
     	    if (!geoDomain.equalsIgnoreCase(PretupsI.ALL)) {
     	    	pstmt.setString(i, geoDomain);
     	    	i++;
     	     }
     	    if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
     	    	pstmt.setString(i, domainCode);
     	    	i++;
     	     }

     	   O2CBatchMasterVO o2cBatchMasterVO = null;
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	 o2cBatchMasterVO = new O2CBatchMasterVO();
            	 o2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
                 o2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
                 o2cBatchMasterVO.setProductName(rs.getString("product_name"));
                 o2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
                 o2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
                 o2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                 o2cBatchMasterVO.setNewRecords(rs.getInt("new"));
                 o2cBatchMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
                 o2cBatchMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
                 o2cBatchMasterVO.setClosedRecords(rs.getInt("closed"));
                 o2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
                 o2cBatchMasterVO.setNetworkCode(rs.getString("network_code"));
                 o2cBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
                 o2cBatchMasterVO.setProductCode(rs.getString("product_code"));
                 o2cBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
                 o2cBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
                 o2cBatchMasterVO.setProductType(rs.getString("product_type"));
                 o2cBatchMasterVO.setProductShortName(rs.getString("short_name"));
                 o2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                 o2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                 o2cBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
                 o2cBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
                 o2cBatchMasterVO.setCategoryCode(rs.getString("category_code"));
                 batchTransferList.add(o2cBatchMasterVO);
             }
         }catch (SQLException sqe) {
             log.error(methodName, "SQLException:" + sqe);
             log.errorTrace(methodName, sqe);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadO2CTransferApprovalList]", "",
                 "", "", " SQL Exception :" + sqe.getMessage());
             throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
         } catch (Exception ex) {
             log.error(methodName, "Exception : " + ex);
             log.errorTrace(methodName, ex);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadO2CTransferApprovalList]", "",
                 "", "", "Exception:" + ex.getMessage());
             throw new BTSLBaseException(this, methodName, errorGeneralException);
         } finally {
         	try{
         		if (rs!= null){
         			rs.close();
         		}
         	}
         	catch (SQLException e){
         		log.error("An error occurred closing result set.", e);
         	}
         	try{
             	if (pstmt!= null){
             		pstmt.close();
             	}
             }
             catch (SQLException e){
             	log.error("An error occurred closing statement.", e);
             }
             if (log.isDebugEnabled()) {
                 log.debug(methodName, "Exiting: o2cBatchMasterVOList size=" + batchTransferList.size());
             }
         }

         return batchTransferList;

    }

    public ArrayList<O2CBatchMasterVO> loadO2CWithdrawal_FOCApprovalList(Connection con, String domainCode, String categoryCode, String geoDomain, String userId, String approvalLevel, String type) throws BTSLBaseException {
    	final String methodName = "loadO2CWithdrawal_FOCApprovalList";
   	 StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.append("Entered domainCode= ");
        	loggerValue.append(domainCode);
        	loggerValue.append("  categoryCode= ");
        	loggerValue.append(categoryCode);
        	loggerValue.append("  geoDomain= ");
        	loggerValue.append(geoDomain);
        	loggerValue.append("  userId= ");
        	loggerValue.append(userId);
        	loggerValue.append("  approvalLevel= ");
        	loggerValue.append(approvalLevel);
        	loggerValue.append("  type= ");
        	loggerValue.append(type);
            log.debug(methodName,  loggerValue );
        }

        final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "'";
        final String sqlSelect = batchO2CTransferQry.loadO2CWithdrawal_FOCApprovalListQry(approvalLevel, statusUsed, categoryCode, geoDomain, domainCode);
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<O2CBatchMasterVO> batchList= new ArrayList<O2CBatchMasterVO>();

        try{
       	 pstmt = con.prepareStatement(sqlSelect);
            int i=1;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            i++;
            pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            i++;
            pstmt.setString(i, userId);
            i++;
            if (!categoryCode.equalsIgnoreCase(PretupsI.ALL)) {
           	pstmt.setString(i, categoryCode);
           	i++;
            }
    	    if (!geoDomain.equalsIgnoreCase(PretupsI.ALL)) {
    	    	pstmt.setString(i, geoDomain);
    	    	i++;
    	     }
    	    if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
    	    	pstmt.setString(i, domainCode);
    	    	i++;
    	     }
    	    pstmt.setString(i, type);
            i++;
    	   O2CBatchMasterVO o2CBatchMasterVO = null;
           rs = pstmt.executeQuery();
           while (rs.next()) {
        	   o2CBatchMasterVO = new O2CBatchMasterVO();
        	   o2CBatchMasterVO.setBatchId(rs.getString("batch_id"));
        	   o2CBatchMasterVO.setBatchName(rs.getString("batch_name"));
        	   o2CBatchMasterVO.setProductName(rs.getString("product_name"));
        	   o2CBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
        	   o2CBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
        	   o2CBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
        	   o2CBatchMasterVO.setNewRecords(rs.getInt("new"));
        	   o2CBatchMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
        	   o2CBatchMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
        	   o2CBatchMasterVO.setClosedRecords(rs.getInt("closed"));
               o2CBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
               o2CBatchMasterVO.setNetworkCode(rs.getString("network_code"));
               o2CBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
               o2CBatchMasterVO.setProductCode(rs.getString("product_code"));
               o2CBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
               o2CBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
               o2CBatchMasterVO.setProductType(rs.getString("product_type"));
               o2CBatchMasterVO.setProductShortName(rs.getString("short_name"));
               o2CBatchMasterVO.setDomainCode(rs.getString("domain_code"));
               o2CBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
               o2CBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
               o2CBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
               o2CBatchMasterVO.setCreatedBy(rs.getString("created_by"));
//               focBatchMasterVO.setWallet_type(rs.getString("txn_wallet"));
               batchList.add(o2CBatchMasterVO);
            }
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadO2CWithdrawal_FOCApprovalList]", "",
                "", "", " SQL Exception :" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadO2CWithdrawal_FOCApprovalList]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralException);
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: o2cBatchMasterVOList size=" + batchList.size());
            }
        }

        return batchList;

    }
		public O2CBatchApprovalDetailsResponse loadBatchTransferApprovalDetails(Connection con, String batchId,String userId,String approvalLevel)  throws BTSLBaseException{
			final String methodName = "loadBatchTransferApprovalDetails";
				StringBuilder loggerValue= new StringBuilder();
		 if (log.isDebugEnabled()) {
			 loggerValue.setLength(0);
			 loggerValue.append("Entered   batchID ");
			 loggerValue.append(batchId);
			 loggerValue.append(" userId=");
			 loggerValue.append(userId);
			 loggerValue.append(" approvalLevel=");
			 loggerValue.append(approvalLevel);
						log.debug(methodName, loggerValue);
				}
		 final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "'";

			final String sqlSelect = batchO2CTransferQry.loadBatchTransferApprovalDetails(statusUsed, approvalLevel);
				if (log.isDebugEnabled()) {
				 loggerValue.setLength(0);
						loggerValue.append("QUERY sqlSelect=");
						loggerValue.append(sqlSelect);
						log.debug(methodName,  loggerValue );
				}
				O2CBatchApprovalDetailsResponse approvalDetailsView=null;
				int i = 0;
				try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

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
						pstmt.setString(i, userId);
						++i;
						pstmt.setString(i, batchId);
						++i;
						pstmt.setString(i, PretupsI.CT_BATCH_O2C_STATUS_OPEN);
						++i;
						pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
						try(ResultSet rs = pstmt.executeQuery();)
						{
						approvalDetailsView =new O2CBatchApprovalDetailsResponse();
						BatchO2CMasterVO batchO2CMasterVO = new BatchO2CMasterVO();
						BatchO2CItemsVO itemsVO = new  BatchO2CItemsVO();
						while (rs.next()) {
								batchO2CMasterVO = new BatchO2CMasterVO();
								batchO2CMasterVO.setBatchId(rs.getString("batch_id"));
								batchO2CMasterVO.setBatchName(rs.getString("batch_name"));
								batchO2CMasterVO.setProductName(rs.getString("product_name"));
								batchO2CMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
								batchO2CMasterVO.setNewRecords(rs.getInt("new"));
								batchO2CMasterVO.setProductCode(rs.getString("product_code"));
								batchO2CMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
								batchO2CMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
								batchO2CMasterVO.setClosedRecords(rs.getInt("closed"));
								batchO2CMasterVO.setRejectedRecords(rs.getInt("cncl"));
								batchO2CMasterVO.setBatchFileName(rs.getString("BATCH_FILE_NAME"));
								batchO2CMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
								batchO2CMasterVO.setSecondLang(rs.getString("sms_second_lang"));
								itemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
								itemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
								itemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
								batchO2CMasterVO.setBatchO2CItemsVO(itemsVO);

								approvalDetailsView.setApprovalDetails(batchO2CMasterVO);
						}
				}
				}catch (SQLException sqe) {
				 loggerValue.setLength(0);
				 loggerValue.append("SQLException : ");
				 loggerValue.append(sqe);
						log.error(methodName,  loggerValue);
						log.errorTrace(methodName, sqe);
						loggerValue.setLength(0);
				 loggerValue.append("SQL Exception:");
				 loggerValue.append(sqe.getMessage());
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchTransferApprovalDetails]", "",
								"", "",  loggerValue.toString() );
						throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
				} catch (Exception ex) {
				 loggerValue.setLength(0);
				 loggerValue.append("Exception : ");
				 loggerValue.append(ex);
						log.error(methodName,  loggerValue );
						log.errorTrace(methodName, ex);
				 loggerValue.setLength(0);
				 loggerValue.append("Exception : ");
				 loggerValue.append(ex.getMessage());
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchTransferApprovalDetails]", "",
								"", "", loggerValue.toString());
						throw new BTSLBaseException(this, methodName, "error.general.processing");
				} finally {
						 if (log.isDebugEnabled()) {
						 loggerValue.setLength(0);
						 loggerValue.append("Exiting: batchO2CMasterVO" );
								log.debug(methodName, loggerValue );
		 }
				}
				return approvalDetailsView;
	 }

	 public LinkedHashMap loadBatchO2CItemsMap(Connection con, String batchId, String itemStatus, String trfType, String trfSubType) throws BTSLBaseException {
			 final String methodName = "loadBatchO2CItemsMap";
			 boolean bulkCommissionPayout=((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.COM_PAY_OUT)).booleanValue();
			 if (log.isDebugEnabled()) {
					 log.debug(methodName, "Entered p_batchId=" + batchId + " p_itemStatus=" + itemStatus + " p_trfType=" + trfType + " p_trfSubType=" + trfSubType);
			 }
			 PreparedStatement pstmt = null;
			 ResultSet rs = null;
			 String sqlSelect=null;
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equalsIgnoreCase(trfSubType)||PretupsI.FOC_WALLET_TYPE.equalsIgnoreCase(trfType)) {
				sqlSelect = batchO2CTransferQry.loadBatchO2CItemsMapQryFOCorWithdrawal(itemStatus);

			}
			else {
				sqlSelect = batchO2CTransferQry.loadBatchO2CItemsMapQry(itemStatus);
			}
				if (log.isDebugEnabled()) {
					 log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
			 }
			 final LinkedHashMap map = new LinkedHashMap();
			 try {
					 pstmt = con.prepareStatement(sqlSelect);
					 if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(trfSubType))
					 {
					 pstmt.setString(1, batchId);
					 pstmt.setString(2, trfType);
					 pstmt.setString(3, trfSubType);
					 pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
					 }
					 else {
						 pstmt.setString(1, batchId);
						 pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);

					 }
					 rs = pstmt.executeQuery();
					 while (rs.next()) {
							 final BatchO2CItemsVO batchO2CItemsVO = new BatchO2CItemsVO();
							 batchO2CItemsVO.setBatchId(batchId);
							 batchO2CItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
							 batchO2CItemsVO.setCategoryName(rs.getString("category_name"));
							 batchO2CItemsVO.setMsisdn(rs.getString("msisdn"));
							 batchO2CItemsVO.setUserId(rs.getString("user_id"));
							 batchO2CItemsVO.setStatus(rs.getString("status"));
							 batchO2CItemsVO.setGradeName(rs.getString("grade_name"));
							 batchO2CItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
							 batchO2CItemsVO.setExtTxnDate(rs.getDate("ext_txn_date"));
							 if(rs.getDate("ext_txn_date")!=null) {
								 batchO2CItemsVO.setExtTxnDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("ext_txn_date"),PretupsI.DATE_FORMAT_DDMMYY));
							 }
							 
							 batchO2CItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
							 batchO2CItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
							 batchO2CItemsVO.setInitiatedBy(rs.getString("initiater_name"));
							 batchO2CItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
							 batchO2CItemsVO.setLoginID(rs.getString("login_id"));
							 batchO2CItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
							 batchO2CItemsVO.setModifiedBy(rs.getString("modified_by"));
							 batchO2CItemsVO.setReferenceNo(rs.getString("reference_no"));
							 batchO2CItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
							 batchO2CItemsVO.setExtTxnDate(rs.getTimestamp("ext_txn_date"));
							 batchO2CItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
							 batchO2CItemsVO.setTxnProfile(rs.getString("txn_profile"));
							 batchO2CItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
							 batchO2CItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
							 batchO2CItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
							 batchO2CItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
							 batchO2CItemsVO.setCommissionType(rs.getString("commission_type"));
							 batchO2CItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
							 batchO2CItemsVO.setCommissionValue(rs.getLong("commission_value"));
							 batchO2CItemsVO.setTax1Type(rs.getString("tax1_type"));
							 batchO2CItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
							 batchO2CItemsVO.setTax1Value(rs.getLong("tax1_value"));
							 batchO2CItemsVO.setTax2Type(rs.getString("tax2_type"));
							 batchO2CItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
							 batchO2CItemsVO.setTax2Value(rs.getLong("tax2_value"));
							 batchO2CItemsVO.setTax3Type(rs.getString("tax3_type"));
							 batchO2CItemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
							 batchO2CItemsVO.setTax3Value(rs.getLong("tax3_value"));
							 batchO2CItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
							 batchO2CItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
							 batchO2CItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
							 batchO2CItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
							 batchO2CItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
							 batchO2CItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
							 batchO2CItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
							 batchO2CItemsVO.setCancelledBy(rs.getString("cancelled_by"));
							 batchO2CItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
							 batchO2CItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
							 batchO2CItemsVO.setGradeCode(rs.getString("user_grade_code"));
							 batchO2CItemsVO.setCategoryCode(rs.getString("category_code"));
							 batchO2CItemsVO.setFirstApproverName(rs.getString("first_approver_name"));
							 batchO2CItemsVO.setSecondApproverName(rs.getString("second_approver_name"));
							 batchO2CItemsVO.setInitiaterName(rs.getString("initiater_name"));
							 batchO2CItemsVO.setExternalCode(rs.getString("external_code"));
							 if(PretupsI.AUTO_FOC_WALLET.equalsIgnoreCase(trfType)) {
						            if(bulkCommissionPayout) {
						            	batchO2CItemsVO.setBonusType(rs.getString("bonus_type"));
						            }
							 }
							 if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(trfSubType)) {
							 batchO2CItemsVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
							 batchO2CItemsVO.setPayableAmount(rs.getLong("payable_amount"));
							 batchO2CItemsVO.setPaymentType(rs.getString("payment_type"));
							 }
							 if(batchO2CItemsVO.getFirstApprovedQuantity() == 0 || batchO2CItemsVO.getRequestedQuantity() == batchO2CItemsVO.getFirstApprovedQuantity())
							 {
									 batchO2CItemsVO.setCommCalReqd(false);
							 }
							 else
							 {
									batchO2CItemsVO.setCommCalReqd(true);
							 }
							 map.put(rs.getString("batch_detail_id"), batchO2CItemsVO);
					 }
			 } catch (SQLException sqe) {
					 log.error(methodName, " SQLException : " + sqe);
					 log.errorTrace(methodName, sqe);
					 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CItemsMap]", "", "",
							 "", " SQL Exception: " + sqe.getMessage());
					 throw new BTSLBaseException(this, "loadBatchO2CItemsList", "error.general.processing");
			 } catch (Exception ex) {
					 log.error(methodName, "Exception : " + ex);
					 log.errorTrace(methodName, ex);
					 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CItemsMap]", "", "",
							 "", "Exception:" + ex.getMessage());
					 throw new BTSLBaseException(this, methodName, "error.general.processing");
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
							 log.debug(methodName, "Exiting: loadBatchO2CItemsMap map=" + map.size());
					 }
			 }
			 return map;
	 }

	 public O2CBatchApprovalDetailsResponse loadBatchWithdrawalorFOCApprovalDetails(Connection con, String batchId,String userId,String approvalLevel,String approvalType) throws BTSLBaseException {
		 final String methodName = "loadBatchWithdrawalorFOCApprovalDetails";
			 StringBuilder loggerValue= new StringBuilder();
		 if (log.isDebugEnabled()) {
			 loggerValue.setLength(0);
			 loggerValue.append("Entered   batchID ");
			 loggerValue.append(batchId);
			 loggerValue.append(" userId=");
			 loggerValue.append(userId);
			 loggerValue.append(" approvalLevel=");
			 loggerValue.append(approvalLevel);
					 log.debug(methodName, loggerValue);
			 }
		 final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2 + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "'";

			final String sqlSelect = batchO2CTransferQry.loadBatchWithdrawalorFOCApprovalDetails(statusUsed, approvalLevel);
			 if (log.isDebugEnabled()) {
				 loggerValue.setLength(0);
					 loggerValue.append("QUERY sqlSelect=");
					 loggerValue.append(sqlSelect);
					 log.debug(methodName,  loggerValue );
			 }
			 O2CBatchApprovalDetailsResponse approvalDetailsView=null;
			 int i = 0;
			 try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {

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
					 pstmt.setString(i, userId);
					 ++i;
					 pstmt.setString(i, batchId);
					 ++i;
					 pstmt.setString(i, PretupsI.CT_BATCH_O2C_STATUS_OPEN);
					 ++i;
					 if(PretupsI.O2C_MODULE.equals(approvalType)) {
						
						 pstmt.setString(i, PretupsI.O2C_BATCH_TRANSACTION_ID);
					 
					 }
					 else if(PretupsI.TRANSFER_TYPE_FOC.equals(approvalType)) {
						
						 pstmt.setString(i, PretupsI.FOC_BATCH_TRANSACTION_ID);
								
					 }
					 else {
						
						 pstmt.setString(i, approvalType);
							
					 }
					 ++i;
					 pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);

					 try(ResultSet rs = pstmt.executeQuery();)
					 {
					 approvalDetailsView =new O2CBatchApprovalDetailsResponse();
					 BatchO2CItemsVO itemsVO = new  BatchO2CItemsVO();

					 BatchO2CMasterVO batchO2CMasterVO = new BatchO2CMasterVO();
					 while (rs.next()) {
							 batchO2CMasterVO = new BatchO2CMasterVO();
							 batchO2CMasterVO.setBatchId(rs.getString("batch_id"));
							 batchO2CMasterVO.setBatchName(rs.getString("batch_name"));
							 batchO2CMasterVO.setProductName(rs.getString("product_name"));
							 batchO2CMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
							 batchO2CMasterVO.setNewRecords(rs.getInt("new"));
							 batchO2CMasterVO.setProductCode(rs.getString("product_code"));
							 batchO2CMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
							 batchO2CMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
							 batchO2CMasterVO.setClosedRecords(rs.getInt("closed"));
							 batchO2CMasterVO.setRejectedRecords(rs.getInt("cncl"));
							 batchO2CMasterVO.setBatchFileName(rs.getString("batch_file_name"));
							 batchO2CMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
							 batchO2CMasterVO.setSecondLang(rs.getString("sms_second_lang"));
							 itemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
							 itemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
							 itemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
							 itemsVO.setBonusType(rs.getString("bonus_type"));
							 batchO2CMasterVO.setBatchO2CItemsVO(itemsVO);
							 approvalDetailsView.setApprovalDetails(batchO2CMasterVO);
					 }
			 }
			 }catch (SQLException sqe) {
				 loggerValue.setLength(0);
				 loggerValue.append("SQLException : ");
				 loggerValue.append(sqe);
					 log.error(methodName,  loggerValue);
					 log.errorTrace(methodName, sqe);
					 loggerValue.setLength(0);
				 loggerValue.append("SQL Exception:");
				 loggerValue.append(sqe.getMessage());
					 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchTransferApprovalDetails]", "",
							 "", "",  loggerValue.toString() );
					 throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			 } catch (Exception ex) {
				 loggerValue.setLength(0);
				 loggerValue.append("Exception : ");
				 loggerValue.append(ex);
					 log.error(methodName,  loggerValue );
					 log.errorTrace(methodName, ex);
				 loggerValue.setLength(0);
				 loggerValue.append("Exception : ");
				 loggerValue.append(ex.getMessage());
					 EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchTransferApprovalDetails]", "",
							 "", "", loggerValue.toString());
					 throw new BTSLBaseException(this, methodName, "error.general.processing");
			 } finally {
						if (log.isDebugEnabled()) {
						 loggerValue.setLength(0);
						 loggerValue.append("Exiting: batchO2CMasterVO" );
							 log.debug(methodName, loggerValue );
		 }
			 }
			 return approvalDetailsView;
	 }
	
}
