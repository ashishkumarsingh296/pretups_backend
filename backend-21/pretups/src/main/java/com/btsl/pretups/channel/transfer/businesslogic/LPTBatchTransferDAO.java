/**
 * @# LPTBatchTransferDAO.java
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
 *    This class use for Batch LPT Transfer.
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
import java.util.Set;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchFocFileProcessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.loyaltymgmt.transfer.requesthandler.LoyaltyController;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;

public class LPTBatchTransferDAO {

    /**
     * 
     */
	private LPTBatchTransferQry lptBatchTransferQry;
    public LPTBatchTransferDAO() {
        super();
        lptBatchTransferQry = (LPTBatchTransferQry)ObjectProducer.getObject(QueryConstants.LPT_BATCH_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        // TODO Auto-generated constructor stub
    }

    /**
     * Field _log.
     */
    private static final Log LOG = LogFactory.getLog(LPTBatchTransferDAO.class.getClass().getName());

    /**
     * Method for loading LPTBatch details..
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
    public ArrayList loadBatchLPTMasterDetails(Connection p_con, String p_userID, String p_itemStatus, String p_currentLevel) throws BTSLBaseException {
        final String methodName = "loadBatchLPTMasterDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_userID=" + p_userID + " p_itemStatus=" + p_itemStatus + " p_currentLevel=" + p_currentLevel);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record,fb.created_by,p.product_name,p.short_name,p.unit_value,sum(case fbi.status when ? then 1 else 0 end) as new, ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM( case fbi.status when ? then 1 else 0 end) cncl,  ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed,  ");
        strBuff
            .append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang, fbi.category_code ");
        // strBuff.append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date ");
        strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud, ");
        strBuff.append(" user_product_types upt,geographical_domains gd  ");
        strBuff.append(" WHERE ug.user_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
        strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
        strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
        strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
        strBuff.append(" AND fb.product_code=p.product_code  ");
        strBuff.append("AND fb.type=? ");
        strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN (" + p_itemStatus + ") ");
        strBuff
            .append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') ");
        strBuff
            .append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.created_by,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date, fbi.category_code ORDER BY fb.batch_date DESC )qry ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            strBuff.append(" WHERE  new>0 OR  appr1>0 ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            strBuff.append(" WHERE  appr1>0 OR appr2>0 ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            strBuff.append(" WHERE appr2>0 ");
        }

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, p_userID);
            pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_BATCH_LPT_STATUS_OPEN);
            pstmt.setString(8, PretupsI.TRANSFER_TYPE_LPT);
            pstmt.setString(9, PretupsI.CHANNEL_TRANSFER_BATCH_LPT_ITEM_RCRDSTATUS_PROCESSED);
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
                focBatchMasterVO.setCategoryCode(rs.getString("category_code"));
                list.add(focBatchMasterVO);
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchFOCMasterDetails]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchFOCMasterDetails]", "",
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
                LOG.debug(methodName, "Exiting: focBatchMasterVOList size=" + list.size());
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
     *            TODO
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchLPTMasterDetails(Connection p_con, String p_goeDomain, String p_domain, String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID, String p_type) throws BTSLBaseException {
        final String methodName = "loadBatchLPTMasterDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered p_goeDomain=" + p_goeDomain + " p_domain=" + p_domain + " p_productCode=" + p_productCode + " p_batchid=" + p_batchid + " p_msisdn=" + p_msisdn + " p_fromDate=" + p_fromDate + " p_toDate=" + p_toDate + " p_loginID=" + p_loginID + " p_type=" + p_type);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final ArrayList list = new ArrayList();
        try {
        	
        	pstmt=lptBatchTransferQry.loadBatchLPTMasterDetailsQry(p_con, p_goeDomain, p_domain, p_productCode, p_batchid, p_msisdn, p_fromDate, p_toDate, p_loginID, p_type);
            rs = pstmt.executeQuery();
            FOCBatchMasterVO focBatchMasterVO = null;
            while (rs.next()) {
                focBatchMasterVO = new FOCBatchMasterVO();
                focBatchMasterVO.setBatchId(rs.getString("batch_id"));
                focBatchMasterVO.setDomainCode(rs.getString("domain_code"));
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
                focBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                if (focBatchMasterVO.getBatchDate() != null) {
                    focBatchMasterVO.setBatchDateStr(BTSLUtil.getDateStringFromDate(focBatchMasterVO.getBatchDate()));
                }
                list.add(focBatchMasterVO);
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchFOCMasterDetails]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchFOCMasterDetails]", "",
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
                LOG.debug(methodName, "Exiting: focBatchMasterVOList size=" + list.size());
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
    public LinkedHashMap loadBatchItemsMap(Connection p_con, String p_batchId, String p_itemStatus) throws BTSLBaseException {
        final String methodName = "loadBatchItemsMap";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_batchId= " + p_batchId + ", p_itemStatus= " + p_itemStatus);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT fbi.batch_detail_id, c.category_name,c.category_code, fbi.msisdn, fbi.user_id, ");
        strBuff.append(" fbi.third_approved_on,fbi.modified_on ,fbi.status, cg.grade_name,fbi.user_grade_code, ");
        strBuff.append(" fbi.ext_txn_no, fbi.ext_txn_date,fbi.requested_quantity,fbi.transfer_mrp,fbi.first_approved_by,");
        strBuff.append(" fbi.first_approved_on,fbi.second_approved_by,fbi.second_approved_on,fbi.third_approved_by, ");
        strBuff.append(" fb.created_by,fb.created_on,u.user_name,u.login_id , fbi.modified_by,fbi.reference_no,fbi.ext_txn_no, ");
        strBuff.append(" fbi.txn_profile, fbi.commission_profile_set_id,fbi.commission_profile_ver, fbi.commission_profile_detail_id,   ");
        strBuff.append(" fbi.requested_quantity, fbi.transfer_mrp, fbi.initiator_remarks, fbi.first_approver_remarks, ");
        strBuff.append(" fbi.third_approver_remarks, fbi.first_approved_by, fbi.first_approved_on, fbi.second_approved_by, ");
        strBuff.append(" fbi.third_approved_on, fbi.cancelled_by, fbi.cancelled_on, fbi.rcrd_status, fbi.external_code , ");
        strBuff.append(" fapp.user_name first_approver_name,sapp.user_name second_approver_name,intu.user_name initiater_name, ");
        strBuff.append(" fbi.second_approved_on, fbi.third_approved_by, fbi.second_approver_remarks, fbi.ext_txn_date, fbi.transfer_date, ");
        strBuff.append(" fbi.commission_type, fbi.commission_rate, fbi.commission_value, fbi.tax1_type, ");
        strBuff.append(" fbi.tax1_rate, fbi.tax1_value, fbi.tax2_type, fbi.tax2_rate, fbi.tax2_value, ");
        strBuff.append(" fbi.tax3_type, fbi.tax3_rate, fbi.tax3_value,fbi.bonus_type ");
        /** START: Birendra: 30JAN2015 */
        strBuff.append(", fbi.user_wallet ");
        /** STOP: Birendra: 30JAN2015 */
        strBuff.append(" FROM  foc_batches fb left join users intu on fb.created_by = intu.user_id, categories c,foc_batch_items fbi left join users fapp on fbi.first_approved_by = fapp.user_id  left join users sapp on fbi.second_approved_by = sapp.user_id, channel_grades cg, users u");
        strBuff.append(" WHERE fb.batch_id=? AND fb.batch_id=fbi.batch_id AND fb.type= ? AND u.user_id=fbi.user_id  AND fbi.category_code=c.category_code AND");
        strBuff.append(" fbi.user_grade_code=cg.grade_code");
        strBuff
            .append(" AND fbi.status in(" + p_itemStatus + ") AND fbi.rcrd_status=?  ");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect = " + sqlSelect);
        }
        final LinkedHashMap map = new LinkedHashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.TRANSFER_TYPE_LPT);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_BATCH_LPT_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final FOCBatchItemsVO focBatchItemsVO = new FOCBatchItemsVO();
                focBatchItemsVO.setBatchId(p_batchId);
                final String batchDetailId = rs.getString("batch_detail_id");
                focBatchItemsVO.setBatchDetailId(batchDetailId);
                focBatchItemsVO.setCategoryName(rs.getString("category_name"));
                focBatchItemsVO.setMsisdn(rs.getString("msisdn"));
                focBatchItemsVO.setUserName(rs.getString("user_name"));
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
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "SQL Exception: " + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchItemsMap]", "", "", "",
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
                LOG.debug(methodName, "Exiting: loadBatchItemsMap map = " + map.size());
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
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList processOrderByBatch(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByBatch";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_dataMap = " + p_dataMap + ", p_currentLevel = " + p_currentLevel + ", p_locale = " + p_locale + ", p_userID = " + p_userID);
        }
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
        // PreparedStatement pstmtUpdateMaster= null;
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
            sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.category_code,u.network_code ");
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
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.category_code,u.network_code ");
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
        sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end) as new,");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end) cncl, ");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed ");
        sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
        sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
        final String selectItemsDetails = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
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
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            // commented for DB2
            psmtCancelFOCBatchItem = (PreparedStatement) p_con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem = (PreparedStatement) p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = (PreparedStatement) p_con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // commented for DB2
            pstmtUpdateMaster = (PreparedStatement) p_con.prepareStatement(updateFOCBatches);
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

                    channelUserVO.setNetworkCode(rs.getString("network_code"));
                    channelUserVO.setCategoryCode(rs.getString("category_code"));
                    // (User status is checked) if this condition is true then
                    // made entry in logs and leave this data.

                    // user life cycle
                    boolean receiverStatusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkCode(), channelUserVO.getCategoryCode(),
                        PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    if (userStatusVO != null) {
                        final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                        final String status[] = userStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(channelUserVO.getStatus())) {
                                receiverStatusAllowed = true;
                            }
                        }
                    } else {
                        throw new BTSLBaseException(this, "processOrderByBatch", "error.status.processing");
                    }
                    if (!receiverStatusAllowed) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User is not active", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
                    continue;

                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                rs1 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                if (rs1.next()) {
                    newlastModified = rs1.getTimestamp("modified_on");
                }
                // (record not found means it is modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .focBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .focBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
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
                    rs2 = pstmtIsTxnNumExists1.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs2.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists BATCH FOC",
                            "Approval level" + p_currentLevel);
                        continue;
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
                    rs3 = pstmtIsTxnNumExists2.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs3.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + p_currentLevel);
                        continue;
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
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
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
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : DB Error while updating items table",
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
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByBatch]", "", "", "",
                "Exception:" + ex.getMessage());
            BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
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
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByBatch]",
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
                LOG.error(methodName, "SQLException : " + sqe);
                LOG.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, "Exception : " + ex);
                LOG.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[processOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());
                BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
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
     * Method to close the foc order by batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,foc_batch_items,
     * foc_batches,
     * channel_transfers_items,channel_transfers
     * 
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_focBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatch(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, FOCBatchMasterVO p_focBatchMatserVO, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "closeOrderByBatch";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_dataMap=" );
        	loggerValue.append(p_dataMap);
        	loggerValue.append(" p_currentLevel=");
        	loggerValue.append(" p_currentLevel=");
        	loggerValue.append(" p_currentLevel=");
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(" p_userID=");
            loggerValue.append(	p_userID);			
            LOG.debug(methodName,loggerValue);
        }
        PreparedStatement pstmtLoadUser = null;

        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement psmtAppr3FOCBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;

        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        int updateCount = 0;
        String o2cTransferID = null;
        OperatorUtilI operatorUtili = null;
        String m_receiverStatusAllowed[] = null;
        String language = null;
        String country = null;
        Locale locale = null;
        String[] array = null;
        BTSLMessages messages = null;
        PushMessage pushMessage = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception while loading the class at the call:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch]", "", "", "",
            		loggerValue.toString() );
        }
        // user life cycle
        String receiverStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_focBatchMatserVO.getNetworkCode(), p_focBatchMatserVO.getCategoryCode(),
            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
        final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
        m_receiverStatusAllowed = sa.split(",");

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

            sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, cusers.LMS_PROFILE, ");
            sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
            sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
            sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps, user_phones up,user_geographies ug ");
            sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
            sqlBuffer.append(" AND u.status IN (");
            for (int i = 0; i < m_receiverStatusAllowed.length; i++) {
                sqlBuffer.append(" ?");
                if (i != m_receiverStatusAllowed.length - 1) {
                    sqlBuffer.append(",");
                }
            }
            sqlBuffer.append(")");
            sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
            sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
            sqlBuffer.append("  ug.user_id = u.user_id AND cusers.LMS_PROFILE is not null ");
            
             	
        	SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
					ValueType.STRING);
        	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
        	
        }else {

        sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, cusers.LMS_PROFILE, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status IN (");
        for (int i = 0; i < m_receiverStatusAllowed.length; i++) {
            sqlBuffer.append(" ?");
            if (i != m_receiverStatusAllowed.length - 1) {
                sqlBuffer.append(",");
            }
        }
        sqlBuffer.append(")");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id AND cusers.LMS_PROFILE is not null ");
        
        
        }
        
        final String sqlLoadUser = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append( "QUERY sqlLoadUser=" );
        	loggerValue.append(sqlLoadUser);
            LOG.debug(methodName,  loggerValue );
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
        	loggerValue.append( "QUERY sqlApprv1FOCBatchItems=" );
        	loggerValue.append(sqlApprv1FOCBatchItems);
            LOG.debug(methodName,  loggerValue);
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
        	loggerValue.append( "QUERY sqlApprv2FOCBatchItems=");
        	loggerValue.append(sqlApprv2FOCBatchItems);
            LOG.debug(methodName, loggerValue );
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
        	loggerValue.append( "QUERY sqlApprv3FOCBatchItems=" );
        	loggerValue.append(sqlApprv3FOCBatchItems);
            LOG.debug(methodName,loggerValue );
        }
        sqlBuffer = null;

        // Afetr all the records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,canceled etc. These counts will be used to deceide
        // what status to be updated in mater table
        sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case fbi.status when ? then 1 else 0 end) as new,");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end) cncl, ");
        sqlBuffer.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed ");
        sqlBuffer.append(" FROM foc_batches fb,foc_batch_items fbi ");
        sqlBuffer.append(" WHERE fb.batch_id=fbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
        final String selectItemsDetails = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY selectItemsDetails="  );
        	loggerValue.append(selectItemsDetails);
            LOG.debug(methodName,loggerValue);
        }
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY updateFOCBatches="  );
        	loggerValue.append(updateFOCBatches);
            LOG.debug(methodName,  loggerValue);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY isModified=" );
        	loggerValue.append(isModified);
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
            LOG.debug(methodName,  loggerValue );
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
        	loggerValue.append("QUERY isExistsTxnNum2="  );
        	loggerValue.append(isExistsTxnNum2);
            LOG.debug(methodName, loggerValue );
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
        	loggerValue.append("QUERY insertIntoChannelTransferItem=" );
        	loggerValue.append(insertIntoChannelTransferItem);
            LOG.debug(methodName,  loggerValue);
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
        sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id,LMS_POINT_ADJUST_VALUE ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
            sqlBuffer.append(",TXN_WALLET)");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            sqlBuffer.append(") ");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY insertIntoChannelTransfer=" );
        	loggerValue.append(insertIntoChannelTransfer);
            LOG.debug(methodName,  loggerValue );
        }
        sqlBuffer = null;

        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        ChannelTransferVO channelTransferVO = null;
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            ChannelTransferItemsVO channelTransferItemVO = null;
            date = new Date();
            ArrayList channelTransferItemVOList = null;
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);

            psmtAppr1FOCBatchItem = (PreparedStatement) p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = (PreparedStatement) p_con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtAppr3FOCBatchItem = (PreparedStatement) p_con.prepareStatement(sqlApprv3FOCBatchItems);

            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);

            pstmtUpdateMaster = (PreparedStatement) p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);

            pstmtInsertIntoChannelTransferItems = p_con.prepareStatement(insertIntoChannelTransferItem);
            pstmtInsertIntoChannelTranfers = (PreparedStatement) p_con.prepareStatement(insertIntoChannelTransfer);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
            final String network_id = null;
            final long previousUserBalToBeSetChnlTrfItems = -1;
            final long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            boolean terminateProcessing = false;
            final LoyaltyDAO loyaltyDAO = new LoyaltyDAO();
            final LoyaltyController _loyaltyController = new LoyaltyController();
            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Executed focBatchItemVO=" );
                	loggerValue.append(focBatchItemVO.toString());
                    LOG.debug(methodName,  loggerValue);
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
                for (int x = 0; x < m_receiverStatusAllowed.length; x++) {
                    ++m;
                    pstmtLoadUser.setString(m, m_receiverStatusAllowed[x]);
                }
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(focBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setLmsProfileId(rs.getString("LMS_PROFILE"));
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
                    // If Lms profile is not associated
                    if (BTSLUtil.isNullString(channelUserVO.getLmsProfileId())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Lms Profile is not associated with users ",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Commission profile suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equalsIgnoreCase(focBatchItemVO.getStatus())) {
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "CANCEL : ", focBatchItemVO.getBatchDetailId());
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
                    continue;
                }
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

                ChannelTransferBL.genrateTransferID(channelTransferVO);
                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch foc item was created.
                channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());

                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                rs1 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                // check record is modified or not
                if (rs1.next()) {
                    newlastModified = rs1.getTimestamp("modified_on");
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + p_currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + p_currentLevel);
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
                    rs2 = pstmtIsTxnNumExists1.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs2.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in FOC Batch",
                            "Approval level = " + p_currentLevel);
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
                    rs3 = pstmtIsTxnNumExists2.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs3.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in CHANNEL TRF",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
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
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
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
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
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
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
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
                channelTransferVO.setRequestedQuantity(0);
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
                if (!BTSLUtil.isNullString(focBatchItemVO.getBonusType()) && PretupsI.LPT_BATCH_ACTION_CREDIT.equalsIgnoreCase(focBatchItemVO.getBonusType())) {
                    channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_POINT_CREDIT);
                } else if (!BTSLUtil.isNullString(focBatchItemVO.getBonusType()) && PretupsI.LPT_BATCH_ACTION_DEBIT.equalsIgnoreCase(focBatchItemVO.getBonusType())) {
                    channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_POINT_DEBIT);
                }
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(focBatchItemVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(0);
                // added for logger
                channelTransferVO.setControlTransfer(PretupsI.YES);
                channelTransferVO.setToUserCode(focBatchItemVO.getMsisdn());
                channelTransferVO.setReceiverDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setReceiverGgraphicalDomainCode(channelTransferVO.getGraphicalDomainCode());
                channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                channelTransferVO.setActiveUserId(p_focBatchMatserVO.getCreatedBy());
                // end

                channelTransferItemVO = new ChannelTransferItemsVO();
                channelTransferItemVO.setApprovedQuantity(0);
                channelTransferItemVO.setCommProfileDetailID(focBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(focBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(focBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(focBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(0);
                channelTransferItemVO.setProductCode(p_focBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                channelTransferItemVO.setRequiredQuantity(0);
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
                channelTransferItemVO.setSenderDebitQty(0);
                channelTransferItemVO.setReceiverCreditQty(0);
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(p_focBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exiting: channelTransferVO=");
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
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getMsisdn());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());
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
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, focBatchItemVO.getRequestedQuantity());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
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
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchlptadjust.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer table",
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
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchlptadjust.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer items table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                // user life cycle
                if (focBatchItemVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                    if (channelUserVO.getStatus().equals(PretupsI.USER_STATUS_PREACTIVE) || channelUserVO.getStatus().equals(PretupsI.USER_STATUS_CHURN) || channelUserVO
                        .getStatus().equals(PretupsI.USER_STATUS_EXPIRED)) {
                        final int updatecount = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getToUserID(), channelUserVO.getStatus(),
                            PretupsI.USER_STATUS_ACTIVE);
                        if (updatecount > 0) {
                            p_con.commit();
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        } else {
                            p_con.rollback();
                            throw new BTSLBaseException(this, methodName, "error.status.updating");
                        }
                    } else {
                        p_con.commit();
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    }

                } else {
                    p_con.commit();

                }
                // Updating the bonus based on provided actions
                PromotionDetailsVO promotionDetailsVO = null;
                promotionDetailsVO = _loyaltyController.loadProfile(p_con, channelUserVO.getLmsProfileId());
                int userLoyaltyPointUpdateCount = 0;
                final LoyaltyVO p_loyaltyVO = new LoyaltyVO();
                channelTransferVO.setTransferMRP(focBatchItemVO.getTransferMrp());
                p_loyaltyVO.setTotalCrLoyaltyPoint(Long.parseLong(PretupsBL.getDisplayAmount(channelTransferVO.getTransferMRP())));
               
                p_loyaltyVO.setUserid(focBatchItemVO.getUserId());
                p_loyaltyVO.setProductCode(p_focBatchMatserVO.getProductCode());
				p_loyaltyVO.setProductName(p_focBatchMatserVO.getProductShortName());
                p_loyaltyVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                p_loyaltyVO.setCreatedOn(new Date());
                PretupsBL.generateLMSTransferID(p_loyaltyVO);
                p_loyaltyVO.setSetId(channelUserVO.getLmsProfileId());
                p_loyaltyVO.setVersion(promotionDetailsVO.getVersion());
                p_loyaltyVO.setTxnId(channelTransferVO.getTransferID());
                if (!BTSLUtil.isNullString(focBatchItemVO.getBonusType()) && PretupsI.LPT_BATCH_ACTION_CREDIT.equalsIgnoreCase(focBatchItemVO.getBonusType())) {
               	 p_loyaltyVO.setLmstxnid("");
               } else if (!BTSLUtil.isNullString(focBatchItemVO.getBonusType()) && PretupsI.LPT_BATCH_ACTION_DEBIT.equalsIgnoreCase(focBatchItemVO.getBonusType())) {
            	   p_loyaltyVO.setLmstxnid(channelTransferVO.getTransferID());}
                try {
                    userLoyaltyPointUpdateCount = loyaltyDAO.creditDebitLoyaltyPoint(p_con, p_loyaltyVO, focBatchItemVO.getBonusType(), promotionDetailsVO.getPromotionType(),
                        p_userID);
                    if (userLoyaltyPointUpdateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchlptadjust.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating bonus for the user",
                            "Approval level = " + p_currentLevel + ", userLoyaltyPointUpdateCount=" + userLoyaltyPointUpdateCount);
                        continue;
                    } else {
                        p_con.commit();
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        // Send notification message to retailers
                        try {
                            locale = new Locale(language, country);
							array=new String[] {channelTransferVO.getTransferID(),String.valueOf(p_loyaltyVO.getTotalCrLoyaltyPoint()),p_loyaltyVO.getProductName()};
                            if (!BTSLUtil.isNullString(focBatchItemVO.getBonusType()) && PretupsI.LPT_BATCH_ACTION_CREDIT.equals(focBatchItemVO.getBonusType())) {
                                messages = new BTSLMessages(PretupsErrorCodesI.LMS_POINT_CREDIT_NOTOFICATION, array);
                            } else if (!BTSLUtil.isNullString(focBatchItemVO.getBonusType()) && PretupsI.LPT_BATCH_ACTION_DEBIT.equals(focBatchItemVO.getBonusType())) {
                                messages = new BTSLMessages(PretupsErrorCodesI.LMS_POINT_DEBIT_NOTOFICATION, array);
                            }
                            pushMessage = new PushMessage(focBatchItemVO.getMsisdn(), messages, null, null, locale, channelTransferVO.getNetworkCode());
                            // push SMS
                            pushMessage.push();
                        } catch (RuntimeException e) {
                            LOG.errorTrace(methodName, e);
                        }
                    }
                } catch (BTSLBaseException be) {
                    if (be.getMessageKey().equalsIgnoreCase(PretupsErrorCodesI.LMS_POINT_DEBIT_LESS_ACCUMULATED)) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchlptadjust.batchapprovereject.msg.error.debitpoints.accumulatedpointsless"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating bonus for the user",
                            "Approval level = " + p_currentLevel + ", userLoyaltyPointUpdateCount=" + userLoyaltyPointUpdateCount);
                        continue;
                    } else if (be.getMessageKey().equalsIgnoreCase(PretupsErrorCodesI.UPDATED_ERROR_BONUS_TABLE)) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchlptadjust.batchapprovereject.msg.error.noupdate"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating bonus for the user",
                            "Approval level = " + p_currentLevel + ", userLoyaltyPointUpdateCount=" + userLoyaltyPointUpdateCount);
                        continue;
                    } else {
                        throw be;
                    }
                } catch (RuntimeException e) {
                    LOG.errorTrace(methodName, e);
                }
                // Ended Here

                OneLineTXNLog.log(channelTransferVO, focBatchItemVO);
            }// end of while
        }// end of try
        catch (BTSLBaseException be) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
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
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "", "",
            		loggerValue.toString() );
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
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
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "", "",
            		loggerValue.toString() );
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
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
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
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
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchpointadjust.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : DB Error while updating master table",
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
                loggerValue.append("SQLException : ");
                loggerValue.append(sqe);
                LOG.error(methodName,  loggerValue );
                LOG.errorTrace(methodName, sqe);
                loggerValue.setLength(0);
                loggerValue.append("SQL Exception:");
                loggerValue.append(sqe.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "",
                    "",  loggerValue.toString() );
                BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                loggerValue.setLength(0);
                loggerValue.append("Exception:");
                loggerValue.append(ex);
                LOG.error(methodName, loggerValue);
                LOG.errorTrace(methodName, ex);
                loggerValue.setLength(0);
                loggerValue.append("Exception:");
                loggerValue.append(ex.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[closeOrderByBatch]", "", "",
                    "",  loggerValue.toString() );
                BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
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
                loggerValue.append("Exiting: errorList size=");
                loggerValue.append(errorList.size());
                LOG.debug(methodName,  loggerValue );
            }
        }
        return errorList;
    }

    /**
     * This method load Geographies according to batch id.
     * loadBatchGeographiesList
     * 
     * @param p_con
     *            Connection
     * @param p_batchId
     *            String
     * @return ArrayList list
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchGeographiesList(Connection p_con, String p_batchId) throws BTSLBaseException {
        final String methodName = "loadBatchGeographiesList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered p_batchId=");
            loggerValue.append(p_batchId);
            LOG.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder("SELECT batch_id, geography_code, date_time FROM foc_batch_geographies WHERE  batch_id=? ");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlSelect=");
            loggerValue.append(sqlSelect);
            LOG.debug(methodName,  loggerValue );
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            rs = pstmt.executeQuery();
            FOCBatchGeographyVO fOCBatchGeographyVO = null;
            while (rs.next()) {
                fOCBatchGeographyVO = new FOCBatchGeographyVO();
                fOCBatchGeographyVO.setBatchId(rs.getString("batch_id"));
                fOCBatchGeographyVO.setGeographyCode(rs.getString("geography_code"));
                // Added on 07/02/08 for addition of new date_time column in the
                // table FOC_BATCH_GEOGRAPHIES.
                fOCBatchGeographyVO.setDateTime(rs.getTimestamp("date_time"));
                list.add(fOCBatchGeographyVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
            loggerValue.append("SQLException");
            loggerValue.append(sqe);
            LOG.error(methodName,loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchGeographiesList]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	  loggerValue.setLength(0);
              loggerValue.append("Exception:");
              loggerValue.append(ex);
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchGeographiesList]", "",
                "", "", loggerValue.toString());
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
            	  loggerValue.setLength(0);
                  loggerValue.append("Exiting: loadBatchGeographiesList size=");
                  loggerValue.append(list.size());
                LOG.debug(methodName, loggerValue );
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
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append("Entered p_batchId=" );
             loggerValue.append(p_batchId);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = lptBatchTransferQry.loadBatchDetailsListQry();
        if (LOG.isDebugEnabled()) {
        	 loggerValue.setLength(0);
             loggerValue.append("QUERY sqlSelect=");
             loggerValue.append(sqlSelect);
            LOG.debug(methodName,  loggerValue );
        }
        FOCBatchMasterVO fOCBatchMasterVO = null;
        FOCBatchItemsVO fOCBatchItemsVO = null;
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                fOCBatchMasterVO = new FOCBatchMasterVO();
                fOCBatchMasterVO.setBatchId(rs.getString("batch_id"));
                fOCBatchMasterVO.setBatchName(rs.getString("batch_name"));
                fOCBatchMasterVO.setStatus(rs.getString("status"));
                fOCBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                fOCBatchMasterVO.setDomainCodeDesc(rs.getString("domain_name"));
                fOCBatchMasterVO.setProductCode(rs.getString("product_code"));
                fOCBatchMasterVO.setProductCodeDesc(rs.getString("product_name"));
                fOCBatchMasterVO.setBatchFileName(rs.getString("batch_file_name"));
                fOCBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                fOCBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                fOCBatchMasterVO.setCreatedBy(rs.getString("initated_by"));
                fOCBatchMasterVO.setCreatedOn(rs.getTimestamp("created_on"));
                fOCBatchMasterVO.setStatus(rs.getString("status"));
                fOCBatchMasterVO.setStatusDesc(rs.getString("status_desc"));

                fOCBatchItemsVO = new FOCBatchItemsVO();
                fOCBatchItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
                fOCBatchItemsVO.setUserName(rs.getString("user_name"));
                fOCBatchItemsVO.setExternalCode(rs.getString("external_code"));
                fOCBatchItemsVO.setMsisdn(rs.getString("msisdn"));
                fOCBatchItemsVO.setCategoryName(rs.getString("category_name"));
                fOCBatchItemsVO.setCategoryCode(rs.getString("category_code"));
                fOCBatchItemsVO.setStatus(rs.getString("status_item"));
                fOCBatchItemsVO.setUserGradeCode(rs.getString("user_grade_code"));
                fOCBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                fOCBatchItemsVO.setGradeName(rs.getString("grade_name"));
                fOCBatchItemsVO.setReferenceNo(rs.getString("reference_no"));
                fOCBatchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                fOCBatchItemsVO.setExtTxnDate(rs.getDate("ext_txn_date"));
                if (fOCBatchItemsVO.getExtTxnDate() != null) {
                    fOCBatchItemsVO.setExtTxnDateStr(BTSLUtil.getDateStringFromDate(fOCBatchItemsVO.getExtTxnDate()));
                }
                fOCBatchItemsVO.setTransferDate(rs.getDate("transfer_date"));
                if (fOCBatchItemsVO.getTransferDate() != null) {
                    fOCBatchItemsVO.setTransferDateStr(BTSLUtil.getDateStringFromDate(fOCBatchItemsVO.getTransferDate()));
                }
                fOCBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                fOCBatchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                fOCBatchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                fOCBatchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                fOCBatchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                fOCBatchItemsVO.setCommissionType(rs.getString("commission_type"));
                fOCBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                fOCBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                fOCBatchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                fOCBatchItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                fOCBatchItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                fOCBatchItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                fOCBatchItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                fOCBatchItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                fOCBatchItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                fOCBatchItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                fOCBatchItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                fOCBatchItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                fOCBatchItemsVO.setOwnerName(rs.getString("ownername"));
                fOCBatchItemsVO.setOwnerMSISDN(rs.getString("ownermsisdn"));
                fOCBatchMasterVO.setFocBatchItemsVO(fOCBatchItemsVO);

                list.add(fOCBatchMasterVO);
            }
        } catch (SQLException sqe) {
       	 loggerValue.setLength(0);
         loggerValue.append("SQLException : ");
         loggerValue.append(sqe);
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchDetailsList]", "", "",
                "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception : ");
             loggerValue.append(ex);
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchDetailsList]", "", "",
                "", loggerValue.toString() );
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
            	 loggerValue.setLength(0);
                 loggerValue.append("Exiting: loadBatchDetailsList  list.size()=");
                 loggerValue.append(list.size());
                LOG.debug(methodName,  loggerValue);
            }
        }
        return list;
    }

    /**
     * Method initiateBatchFOCTransfer
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

    public ArrayList initiateBatchLPTTransfer(Connection p_con, FOCBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList, MessageResources p_messages, Locale p_locale, ArrayList<FocListValueVO> arrayFocListValueVO) throws BTSLBaseException {
        final String methodName = "initiateBatchLPTTransfer";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered.... p_batchMasterVO=");
            loggerValue.append(p_batchMasterVO);
            loggerValue.append( ", p_batchItemsList.size() = ");
            loggerValue.append(p_batchItemsList.size() );
            loggerValue.append(", p_batchItemsList=");
            loggerValue.append(p_batchItemsList);
            loggerValue.append("p_locale=");
            loggerValue.append(p_locale);
            LOG.debug(methodName,loggerValue);
        }

        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffSelectExtTxnID1 Query =" );
            loggerValue.append(strBuffSelectExtTxnID1);
            LOG.debug(methodName, loggerValue );
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffSelectExtTxnID2 Query =");
            loggerValue.append(strBuffSelectExtTxnID2);
            LOG.debug(methodName,  loggerValue );
            // ends here
        }

        // for loading the O2C transfer rule for FOC transfer
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuilder strBuffSelectTrfRule = new StringBuilder(" SELECT transfer_rule_id,foc_transfer_type, foc_allowed ");
        strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ? AND ");
        strBuffSelectTrfRule.append("from_category = 'OPT' AND to_category = ? AND status = 'Y' AND type = 'OPT' ");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffSelectTrfRule Query =");
            loggerValue.append(strBuffSelectTrfRule);
            LOG.debug(methodName,  loggerValue);
            // ends here
        }

        // for loading the products associated with the transfer rule
        PreparedStatement pstmtSelectTrfRuleProd = null;
        ResultSet rsSelectTrfRuleProd = null;
        final StringBuilder strBuffSelectTrfRuleProd = new StringBuilder("SELECT 1 FROM chnl_transfer_rules_products ");
        strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffSelectTrfRuleProd Query =");
            loggerValue.append(strBuffSelectTrfRuleProd);
            LOG.debug(methodName,  loggerValue );
            // ends here
        }

        // for loading the products associated with the commission profile
        PreparedStatement pstmtSelectCProfileProd = null;
        ResultSet rsSelectCProfileProd = null;
        final StringBuilder strBuffSelectCProfileProd = new StringBuilder("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())
        {
        	strBuffSelectCProfileProd.append("AND cp.transaction_type in ( ? , ? ) ");
        }
        else
        {
        	strBuffSelectCProfileProd.append("AND cp.transaction_type = ?  ");
        }
        strBuffSelectCProfileProd.append(" AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffSelectCProfileProd Query =");
            loggerValue.append(strBuffSelectCProfileProd);
            LOG.debug(methodName,  loggerValue );
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? ");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffSelectCProfileProdDetail Query =" );
            loggerValue.append(strBuffSelectCProfileProdDetail);
            LOG.debug(methodName, loggerValue );
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
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffSelectTProfileProd Query ="  );
            loggerValue.append(strBuffSelectTProfileProd);
            LOG.debug(methodName, loggerValue);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2 OraclePreparedStatement pstmtInsertBatchMaster =
        // null;
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type,type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffInsertBatchMaster Query ="  );
            loggerValue.append(strBuffInsertBatchMaster);
            LOG.debug(methodName,loggerValue );
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffInsertBatchGeo Query ="   );
            loggerValue.append(strBuffInsertBatchGeo);
            LOG.debug(methodName, loggerValue );
            // ends here
        }

        // insert data in the batch items table
        // commented for DB2OraclePreparedStatement pstmtInsertBatchItems =
        // null;
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,BONUS_TYPE");

        /** START: Birendra: 29JAN2015 */
        strBuffInsertBatchItems.append(", user_wallet");
        /** STOP: Birendra: 29JAN2015 */

        strBuffInsertBatchItems.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
        /** START: Birendra: 29JAN2015 */
        strBuffInsertBatchItems.append(",?)");
        /** START: Birendra: 29JAN2015 */
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffInsertBatchItems Query = "   );
            loggerValue.append(strBuffInsertBatchItems);
            LOG.debug(methodName, loggerValue);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE foc_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("strBuffUpdateBatchMaster Query =" );
            loggerValue.append(strBuffUpdateBatchMaster);
            LOG.debug(methodName,  loggerValue );
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = p_con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = p_con.prepareStatement(strBuffSelectExtTxnID2.toString());
            pstmtSelectTrfRule = p_con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = p_con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = p_con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = p_con.prepareStatement(strBuffSelectTProfileProd.toString());
            pstmtInsertBatchMaster = (PreparedStatement) p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = p_con.prepareStatement(strBuffInsertBatchGeo.toString());
            pstmtInsertBatchItems = (PreparedStatement) p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = p_con.prepareStatement(strBuffUpdateBatchMaster.toString());
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
            final long multipleOf = 0;
            ArrayList transferItemsList = null;
            ChannelTransferItemsVO channelTransferItemsVO = null;

            // insert the master data
            index = 0;
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCodeFor());
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
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDefaultLang());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getSecondLang());
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.TRANSFER_TYPE_LPT);
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.TRANSFER_TYPE_LPT);
            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                p_con.rollback();
                LOG.error(methodName, "Unable to insert in the batch master table.");
                BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchFOCTransfer]",
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
                    LOG.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            final String msgArr[] = null;
            for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
                batchItemsVO = (FOCBatchItemsVO) p_batchItemsList.get(i);
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
                        errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchfoc.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist FOC BATCCH", "");
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
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // load the product's informaiton.
                if (transferRuleNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                    if (transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                        if (transferRuleMap.get(batchItemsVO.getCategoryCode()) == null) {
                            index = 0;
                            ++index;
                            pstmtSelectTrfRule.setString(index, p_batchMasterVO.getNetworkCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, p_batchMasterVO.getDomainCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchItemsVO.getCategoryCode());
                            rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
                            pstmtSelectTrfRule.clearParameters();
                            if (rsSelectTrfRule.next()) {
                                rulesVO = new ChannelTransferRuleVO();
                                rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
                                rulesVO.setFocTransferType(rsSelectTrfRule.getString("foc_transfer_type"));
                                rulesVO.setFocAllowed(rsSelectTrfRule.getString("foc_allowed"));
                                index = 0;
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, rulesVO.getTransferRuleID());
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, p_batchMasterVO.getProductCode());
                                rsSelectTrfRuleProd = pstmtSelectTrfRuleProd.executeQuery();
                                pstmtSelectTrfRuleProd.clearParameters();
                                if (!rsSelectTrfRuleProd.next()) {
                                    transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                    // put error log Prodcuct is not in the
                                    // transfer rule
                                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                        "batchfoc.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                                    errorList.add(errorVO);
                                    // Handling the failed transaction details
                                    // for generating the Excel file
                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                                    }
                                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchfoc.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                                errorList.add(errorVO);
                                // Handling the failed transaction details for
                                // generating the Excel file
                                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                                    addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                                }
                                BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchItemsVO.getCategoryCode());
                if (PretupsI.NO.equals(rulesVO.getFocAllowed())) {
                    // put error according to the transfer rule FOC transfer is
                    // not allowed.
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.focnotallowed"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : According to the transfer rule FOC transfer is not allowed", "");
                    continue;
                }
                // check the transfer profile product code

                // transfer profile check ends here
                if (transferProfileMap.get(batchItemsVO.getTxnProfile()) == null) {
                    index = 0;
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchItemsVO.getTxnProfile());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, p_batchMasterVO.getProductCode());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    rsSelectTProfileProd = pstmtSelectTProfileProd.executeQuery();
                    pstmtSelectTProfileProd.clearParameters();
                    if (!rsSelectTProfileProd.next()) {
                        transferProfileMap.put(batchItemsVO.getTxnProfile(), "false");
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                }

                // check the commisson profile applicability and other checks
                // related to the commission profile
                index = 0;
                ++index;
                pstmtSelectCProfileProd.setString(index, p_batchMasterVO.getProductCode());
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
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Commission profile for this product is not defined", "");
                    continue;
                }
                requestedValue = batchItemsVO.getRequestedQuantity();
                minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                /*
                 * Commented on 25-JAN-15 because no dependecy required on
                 * commision profile min transfer and multiple of
                 * if (minTrfValue > requestedValue || maxTrfValue <
                 * requestedValue) {
                 * msgArr = new String[3];
                 * msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                 * msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                 * msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                 * // put error requested quantity is not between min and max
                 * // values
                 * errorVO = new ListValueVO(batchItemsVO.getMsisdn(),
                 * String.valueOf(batchItemsVO.getRecordNumber()),
                 * p_messages.getMessage(p_locale,
                 * "batchfoc.initiatebatchfoctransfer.msg.error.qtymaxmin",
                 * msgArr));
                 * msgArr = null;
                 * errorList.add(errorVO);
                 * // Handling the failed transaction details for generating
                 * // the Excel file
                 * if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                 * addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                 * }
                 * BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO,
                 * batchItemsVO,
                 * "FAIL : Requested quantity is not between min and max values"
                 * , "minTrfValue=" + minTrfValue + ", maxTrfValue=" +
                 * maxTrfValue);
                 * continue;
                 * }
                 * multipleOf =
                 * rsSelectCProfileProd.getLong("transfer_multiple_off");
                 * if (requestedValue % multipleOf != 0) {
                 * // put error requested quantity is not multiple of
                 * errorVO = new ListValueVO(batchItemsVO.getMsisdn(),
                 * String.valueOf(batchItemsVO.getRecordNumber()),
                 * p_messages.getMessage(p_locale,
                 * "batchfoc.initiatebatchfoctransfer.msg.error.notmulof", new
                 * String[] { PretupsBL.getDisplayAmount(multipleOf) }));
                 * // Handling the failed transaction details for generating
                 * // the Excel file
                 * if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                 * addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                 * }
                 * errorList.add(errorVO);
                 * BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO,
                 * batchItemsVO,
                 * "FAIL : Requested quantity is not in multiple value",
                 * "multiple of=" + multipleOf);
                 * continue;
                 * }
                 */
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                    loggerValue.append("comm_profile_products_id=" );
                    loggerValue.append(rsSelectCProfileProd.getString("comm_profile_products_id"));
                    loggerValue.append(", minTrfValue=" );
                    loggerValue.append(minTrfValue);
                    LOG.debug(methodName,  loggerValue );
                }

                index = 0;
                ++index;
                pstmtSelectCProfileProdDetail.setString(index, rsSelectCProfileProd.getString("comm_profile_products_id"));

                rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail.executeQuery();
                pstmtSelectCProfileProdDetail.clearParameters();
                /*
                 * Commented on 25-JAN-15 because no dependecy required on
                 * commision profile min transfer and multiple of
                 * if (!rsSelectCProfileProdDetail.next()) {
                 * // put error commission profile slab is not define for the
                 * // requested value
                 * errorVO = new ListValueVO(batchItemsVO.getMsisdn(),
                 * String.valueOf(batchItemsVO.getRecordNumber()),
                 * p_messages.getMessage(p_locale,
                 * "batchfoc.initiatebatchfoctransfer.msg.error.commslabnotdefined"
                 * ));
                 * // Handling the failed transaction details for generating
                 * // the Excel file
                 * if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED))).booleanValue()) {
                 * addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                 * }
                 * errorList.add(errorVO);
                 * BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO,
                 * batchItemsVO,
                 * "FAIL : Commission profile slab is not define for the requested value"
                 * , "");
                 * continue;
                 * }
                 */
                rsSelectCProfileProdDetail.next();
                // to calculate tax
                transferItemsList = new ArrayList();
                channelTransferItemsVO = new ChannelTransferItemsVO();
                // this value will be inserted into the table as the requested
                // qty
                channelTransferItemsVO.setRequiredQuantity(requestedValue);
                // this value will be used in the tax calculation.
                channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(requestedValue));
                channelTransferItemsVO.setCommProfileDetailID(rsSelectCProfileProdDetail.getString("comm_profile_detail_id"));
                channelTransferItemsVO.setUnitValue(p_batchMasterVO.getProductMrp());

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
                final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);

                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_FOC);

                // taxes on FOC required
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
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBonusType());
                /** START: Birendra: 29JAN2015 */
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getWalletCode());
                /** STOP: Birendra: 29JAN2015 */

                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    // put error record can not be inserted
                    LOG.error(methodName, "Record cannot be inserted in batch items table");
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    p_con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }// for loop for the batch items
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
            loggerValue.append("SQLException : " );
            loggerValue.append(sqe);
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "",
                "", "",  loggerValue.toString());
            BatchFocFileProcessLog
                .focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception : " );
             loggerValue.append( ex);
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : " );
            loggerValue.append( ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "",
                "", "", loggerValue.toString() );
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : Exception:" + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
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
                if (rsSelectTrfRule != null) {
                    rsSelectTrfRule.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRule != null) {
                    pstmtSelectTrfRule.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRuleProd != null) {
                    rsSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRuleProd != null) {
                    pstmtSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProd != null) {
                    rsSelectCProfileProd.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProd != null) {
                    pstmtSelectCProfileProd.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProdDetail != null) {
                    rsSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProdDetail != null) {
                    pstmtSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTProfileProd != null) {
                    rsSelectTProfileProd.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTProfileProd != null) {
                    pstmtSelectTProfileProd.close();
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
                if (errorList != null && (errorList.size() == p_batchItemsList.size())) {
                    p_con.rollback();
                    LOG.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    BatchFocFileProcessLog.focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
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
                        LOG.error(methodName, "Unable to Update the batch size in master table..");
                        p_con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferDAO[initiateBatchFOCTransfer]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + p_batchMasterVO.getBatchId());
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
            	 loggerValue.setLength(0);
                 loggerValue.append("Exiting: errorList.size()=" );
                 loggerValue.append( errorList.size());
                LOG.debug(methodName,  loggerValue);
            }
        }
        return errorList;
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
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered:p_oldlastModified=" );
            loggerValue.append( p_oldlastModified);
            loggerValue.append(",p_batchID=");
            loggerValue.append(p_batchID);
            LOG.debug(methodName,  loggerValue );
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
                loggerValue.append("sqlRecordModified=" );
                loggerValue.append( sqlRecordModified);
                LOG.debug(methodName, loggerValue );
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
        	loggerValue.setLength(0);
            loggerValue.append("SQLException:" );
            loggerValue.append( sqe.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isBatchModified]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	  loggerValue.setLength(0);
              loggerValue.append("Exception:" );
              loggerValue.append( e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isBatchModified]", "", "", "",
            		loggerValue.toString());
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
            	  loggerValue.setLength(0);
                  loggerValue.append("Exiting:modified=" );
                  loggerValue.append(modified);
                LOG.debug(methodName,  loggerValue);
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
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered   p_batchID " );
            loggerValue.append(p_batchID);
            loggerValue.append(" p_newStatus=");
            loggerValue.append(p_newStatus);
            loggerValue.append(" p_oldStatus=");
            loggerValue.append(p_oldStatus);
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmt = null;
        int updateCount = -1;
        try {
            final StringBuilder sqlBuffer = new StringBuilder("UPDATE foc_batches SET status=? ");
            sqlBuffer.append(" WHERE batch_id=? AND status=? ");
            final String updateFOCBatches = sqlBuffer.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
                loggerValue.append("QUERY updateFOCBatches=" );
                loggerValue.append(updateFOCBatches);
                LOG.debug(methodName,  loggerValue );
            }

            pstmt = p_con.prepareStatement(updateFOCBatches);
            int i = 1;
            pstmt.setString(i, p_newStatus);
            i++;
            pstmt.setString(i, p_batchID);
            i++;
            pstmt.setString(i, p_oldStatus);
            i++;
            // pstmt.setString(i++, p_defaultLang);
            // pstmt.setString(i++, p_secondLang);
            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
         	loggerValue.setLength(0);
            loggerValue.append("SQLException" );
            loggerValue.append(sqe);
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:"  );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[updateBatchStatus]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception:"  );
             loggerValue.append(ex);
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:"  );
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[updateBatchStatus]", "", "", "",
            		loggerValue.toString());
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
            	 loggerValue.setLength(0);
                 loggerValue.append("Exiting:  updateCount=" );
                 loggerValue.append(updateCount);
                LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
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
    public ArrayList loadBatchDPMasterDetails(Connection p_con, String p_userID, String p_itemStatus, String p_currentLevel) throws BTSLBaseException {
        final String methodName = "loadBatchDPMasterDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("Entered p_userID=" );
            loggerValue.append(p_userID);
            loggerValue.append(" p_itemStatus=" );
            loggerValue.append(p_itemStatus);
            loggerValue.append(" p_currentLevel=");
            loggerValue.append(p_currentLevel);
            LOG.debug(methodName,  loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" SELECT * FROM (SELECT DISTINCT fb.batch_id,fb.batch_name,fb.batch_total_record, fb.created_by,p.product_name,p.short_name,p.unit_value,sum(case fbi.status when ? then 1 else 0 end) as new, ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr1,SUM(case fbi.status when ? then 1 else 0 end) cncl,  ");
        strBuff.append(" SUM(case fbi.status when ? then 1 else 0 end) appr2,SUM(case fbi.status when ? then 1 else 0 end) closed,  ");
        strBuff
            .append(" fb.network_code,fb.network_code_for,fb.product_code, fb.modified_by, fb.modified_on ,p.product_type,fb.domain_code,fb.batch_date,fb.sms_default_lang,fb.sms_second_lang ");
        strBuff.append(" FROM user_geographies ug , foc_batch_geographies fbg,foc_batches fb,foc_batch_items fbi,products p,user_domains ud, ");
        strBuff.append(" user_product_types upt,geographical_domains gd  ");
        strBuff.append(" WHERE ug.user_id=? AND ug.grph_domain_code=fbg.geography_code AND ug.grph_domain_code=gd.grph_domain_code AND gd.status='Y' ");
        strBuff.append(" AND ud.user_id=ug.user_id AND ud.domain_code= fb.domain_code ");
        strBuff.append(" AND upt.user_id=ud.user_id AND upt.product_type=p.product_type ");
        strBuff.append(" AND fbg.batch_id=fb.batch_id AND fb.status=? ");
        strBuff.append(" AND fb.product_code=p.product_code  ");
        strBuff.append(" AND fb.batch_id=fbi.batch_id AND fbi.rcrd_status=? AND fbi.status IN (" + p_itemStatus + ") ");
        // added by Lohit for differentiationg FOC and Direct Payout
        strBuff.append("AND fb.type=? ");
        strBuff
            .append(" AND (SELECT count(fbg1.geography_code) FROM foc_batch_geographies fbg1 WHERE fbg1.batch_id=fbg.batch_id) <= (SELECT count(ug1.user_id) FROM user_geographies ug1 ,geographical_domains gd WHERE ug1.user_id=ug.user_id AND ug1.grph_domain_code=gd.grph_domain_code AND gd.status='Y') AND fb.type=? ");
        strBuff
            .append(" GROUP BY fb.batch_id,fb.batch_name,fb.batch_total_record, p.product_name,p.unit_value,fb.network_code,fb.network_code_for,fb.created_by,fb.product_code, fb.modified_by, fb.modified_on,fb.sms_default_lang,fb.sms_second_lang,p.product_type,fbg.geography_code,p.short_name ,fb.domain_code,fb.batch_date ORDER BY fb.batch_date DESC )qry ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            strBuff.append(" WHERE  new>0 OR  appr1>0 ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            strBuff.append(" WHERE  appr1>0 OR appr2>0 ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            strBuff.append(" WHERE appr2>0 ");
        }

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
            loggerValue.append("QUERY sqlSelect=" );
            loggerValue.append(sqlSelect);
            LOG.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, p_userID);
            pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);
            pstmt.setString(8, PretupsI.CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED);
            pstmt.setString(9, PretupsI.TRANSFER_TYPE_LPT);
            pstmt.setString(10, PretupsI.TRANSFER_TYPE_LPT);

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
                list.add(focBatchMasterVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
            loggerValue.append("SQLException : " );
            loggerValue.append(sqe);
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, sqe);
        	loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchDPMasterDetails]", "",
                "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append(ex);
            LOG.error(methodName, loggerValue );
            LOG.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[loadBatchDPMasterDetails]", "",
                "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
                loggerValue.append("Exiting: focBatchMasterVOList size=" );
                loggerValue.append(list.size());
                LOG.debug(methodName,  loggerValue );
            }
        }
        return list;
    }

    /**
     * @author diwakar
     * @param errorVO
     * @param batchItemsVO
     */

    private void addErrorList(ListValueVO errorVO, FOCBatchItemsVO batchItemsVO, ArrayList<FocListValueVO> arrayFocListValueVO) {
        final String methodName = "addErrorList";
        FocListValueVO focListValueVO = new FocListValueVO();
        focListValueVO.setCodeName(errorVO.getCodeName());
        focListValueVO.setOtherInfo(errorVO.getOtherInfo());
        focListValueVO.setOtherInfo2(errorVO.getOtherInfo2());
        focListValueVO.setMsisdn(batchItemsVO.getMsisdn());
        focListValueVO.setLoginID(batchItemsVO.getLoginID());
        focListValueVO.setUserCategory(batchItemsVO.getCategoryCode());
        focListValueVO.setUserGrade(batchItemsVO.getUserGradeCode());
        focListValueVO.setExtTXNNumber(batchItemsVO.getExtTxnNo());
        try {
            LOG.debug(methodName, batchItemsVO.getExtTxnDate() + " " + BTSLUtil.getDateTimeStringFromDate(batchItemsVO.getExtTxnDate(), "MM-dd-yyyy"));
            focListValueVO.setExtTXNDate(BTSLUtil.getDateTimeStringFromDate(batchItemsVO.getExtTxnDate(), "MM-dd-yyyy"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        focListValueVO.setExtCode(batchItemsVO.getExternalCode());
        focListValueVO.setQuantity(Long.parseLong(PretupsBL.getDisplayAmount(batchItemsVO.getRequestedQuantity())));
        focListValueVO.setRemarks(errorVO.getOtherInfo2());
        arrayFocListValueVO.add(focListValueVO);
        focListValueVO = null;
    }

}
