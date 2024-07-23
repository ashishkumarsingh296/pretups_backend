/**
 * @# C2CBatchTransferDAO.java
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
 *    This class use for Batch C2C Transfer.
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
import java.util.List;
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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchC2CFileProcessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.btsl.user.businesslogic.UserLoanVO;

public class C2CBatchTransferDAO {
	private C2CBatchTransferQry c2cBatchTransferQry ;
	private String errorGeneralProcessing = "error.general.processing";
	private String errorGeneralSqlProcessing = "error.general.sql.processing";
	private String  sqlException = "SQLException : ";
	private String  exception = "Exception:";
    public C2CBatchTransferDAO() {
    	super();
    	c2cBatchTransferQry = (C2CBatchTransferQry)ObjectProducer.getObject(QueryConstants.C2C_BATCH_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
        
    }

    private static final Log LOG = LogFactory.getLog(C2CBatchTransferDAO.class.getName());
    
    public static OperatorUtilI operatorUtili = null;
    static {
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[static]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * This methid will load the data from the c2c_batch_items table
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
            LOG.debug(methodName, "Entered p_batchId=" + p_batchId + " p_itemStatus=" + p_itemStatus);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cbi.batch_detail_id, cbi.dual_comm_type,c.category_name,c.category_code, cbi.msisdn, cbi.user_id, ");
        strBuff.append(" cbi.modified_on ,cbi.status, cg.grade_name,cbi.user_grade_code, cbi.ext_txn_no, ");
        strBuff.append(" cbi.ext_txn_date,cbi.requested_quantity,cbi.product_code,cbi.transfer_mrp,cbi.approved_by,");
        strBuff.append(" cbi.approved_on, c2cb.created_by,c2cb.created_on,u.login_id , cbi.modified_by,");
        strBuff.append(" cbi.reference_no,cbi.ext_txn_no, cbi.txn_profile, cbi.commission_profile_set_id,cbi.commission_profile_ver,");
        strBuff.append(" cbi.commission_profile_detail_id,cbi.requested_quantity, cbi.transfer_mrp, cbi.initiator_remarks, ");
        strBuff.append(" cbi.approver_remarks, cbi.approved_by, cbi.approved_on, cbi.cancelled_by, ");
        strBuff.append(" cbi.cancelled_on, cbi.rcrd_status, cbi.external_code , fapp.user_name approver_name,");
        strBuff.append(" intu.user_name initiater_name,  cbi.ext_txn_date, cbi.transfer_date, cbi.commission_type, ");
        strBuff.append(" cbi.commission_rate, cbi.commission_value, cbi.tax1_type, cbi.tax1_rate, cbi.tax1_value, ");
        strBuff.append(" cbi.tax2_type, cbi.tax2_rate, cbi.tax2_value, cbi.tax3_type, cbi.tax3_rate, cbi.tax3_value,cbi.transfer_type ,cbi.transfer_sub_type ");
        strBuff.append(" FROM C2C_BATCH_ITEMS cbi left join USERS fapp on cbi.approved_by = fapp.user_id ,C2C_BATCHES c2cb left join USERS intu on c2cb.created_by = intu.user_id ,CATEGORIES c,CHANNEL_GRADES cg, USERS u ");
        strBuff.append(" WHERE c2cb.batch_id=? AND c2cb.batch_id=cbi.batch_id AND u.user_id=cbi.user_id  ");
        strBuff.append(" AND cbi.category_code=c.category_code AND cbi.user_grade_code=cg.grade_code AND cbi.status IN(" + p_itemStatus + ") ");
        strBuff.append(" AND cbi.rcrd_status=?  ");

        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final LinkedHashMap map = new LinkedHashMap();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final C2CBatchItemsVO c2cBatchItemsVO = new C2CBatchItemsVO();
                c2cBatchItemsVO.setBatchId(p_batchId);
                c2cBatchItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
                c2cBatchItemsVO.setCategoryName(rs.getString("category_name"));
                c2cBatchItemsVO.setMsisdn(rs.getString("msisdn"));
                c2cBatchItemsVO.setUserId(rs.getString("user_id"));
                c2cBatchItemsVO.setStatus(rs.getString("status"));
                c2cBatchItemsVO.setGradeName(rs.getString("grade_name"));
                c2cBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                c2cBatchItemsVO.setInitiatedBy(rs.getString("created_by"));
                c2cBatchItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
                c2cBatchItemsVO.setLoginID(rs.getString("login_id"));
                c2cBatchItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
                c2cBatchItemsVO.setModifiedBy(rs.getString("modified_by"));
                c2cBatchItemsVO.setReferenceNo(rs.getString("reference_no"));
                c2cBatchItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
                c2cBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                c2cBatchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                c2cBatchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                c2cBatchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                c2cBatchItemsVO.setCommissionType(rs.getString("commission_type"));
                c2cBatchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                c2cBatchItemsVO.setCommissionValue(rs.getLong("commission_value"));
                c2cBatchItemsVO.setTax1Type(rs.getString("tax1_type"));
                c2cBatchItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                c2cBatchItemsVO.setTax1Value(rs.getLong("tax1_value"));
                c2cBatchItemsVO.setTax2Type(rs.getString("tax2_type"));
                c2cBatchItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                c2cBatchItemsVO.setTax2Value(rs.getLong("tax2_value"));
                c2cBatchItemsVO.setTax3Type(rs.getString("tax3_type"));
                c2cBatchItemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                c2cBatchItemsVO.setTax3Value(rs.getLong("tax3_value"));
                c2cBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                c2cBatchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                c2cBatchItemsVO.setApproverRemarks(rs.getString("approver_remarks"));
                c2cBatchItemsVO.setApprovedBy(rs.getString("approved_by"));
                c2cBatchItemsVO.setApprovedOn(rs.getTimestamp("approved_on"));
                c2cBatchItemsVO.setCancelledBy(rs.getString("cancelled_by"));
                c2cBatchItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
                c2cBatchItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
                c2cBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                c2cBatchItemsVO.setCategoryCode(rs.getString("category_code"));
                c2cBatchItemsVO.setApproverName(rs.getString("approver_name"));
                c2cBatchItemsVO.setInitiaterName(rs.getString("initiater_name"));
                c2cBatchItemsVO.setExternalCode(rs.getString("external_code"));
                c2cBatchItemsVO.setTransferType(rs.getString("transfer_type"));
                c2cBatchItemsVO.setTransferSubType(rs.getString("transfer_sub_type"));
                c2cBatchItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
				c2cBatchItemsVO.setProductCode(rs.getString("product_code"));
                map.put(rs.getString("batch_detail_id"), c2cBatchItemsVO);
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadBatchItemsList", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[loadBatchItemsMap]", "", "", "",
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
     * Method to close the c2c order by batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,c2c_batch_items,
     * c2c_batches,
     * channel_transfers_items,channel_transfers
     * 
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_c2cBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatch(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, ChannelUserVO p_senderVO, C2CBatchMasterVO p_c2cBatchMatserVO, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang,ChannelUserVO chnlUserVO) throws BTSLBaseException {
        final String methodName = "closeOrderByBatch";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_dataMap=" + p_dataMap + " p_currentLevel=" + p_currentLevel + " p_locale=" + p_locale);
            // basic convention in this method.
            // sender user is that who initiated the transfer whether transfer
            // or
            // withdraw
        }
        ArrayList< ChannelSoSVO> channeluserList = new ArrayList<>();
        ArrayList< ChannelSoSVO> channeluserList1 = new ArrayList<>();
        ArrayList< UserLoanVO> channeluserLoanVOList1 = new ArrayList<>();
        ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
        
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement pstmtSelectUserBalances = null;
        PreparedStatement pstmtUpdateUserBalances = null;
        PreparedStatement pstmtUpdateSenderBalanceOn = null;

        PreparedStatement pstmtInsertUserDailyBalances = null;
        PreparedStatement pstmtSelectBalance = null;

        PreparedStatement pstmtSelectSenderBalance = null;
        PreparedStatement pstmtUpdateSenderBalance = null;
        PreparedStatement pstmtInsertSenderDailyBalances = null;

        PreparedStatement pstmtUpdateBalance = null;
        PreparedStatement pstmtInsertBalance = null;
        PreparedStatement pstmtSelectTransferCounts = null;
        PreparedStatement pstmtSelectSenderTransferCounts = null;
        PreparedStatement pstmtSelectProfileCounts = null;
        PreparedStatement pstmtSelectSenderProfileOutCounts = null;
        // PreparedStatement pstmtSelectSenderProfileInCounts=null
        PreparedStatement pstmtUpdateTransferCounts = null;
        PreparedStatement pstmtUpdateSenderTransferCounts = null;
        PreparedStatement pstmtInsertTransferCounts = null;
        PreparedStatement pstmtInsertSenderTransferCounts = null;
        PreparedStatement psmtApprC2CBatchItem = null;
        // commented for DB2
        // OraclePreparedStatement psmtApprC2CBatchItem = null
        // OraclePreparedStatement pstmtUpdateMaster= null
        // OraclePreparedStatement pstmtInsertIntoChannelTranfers=null
        PreparedStatement pstmtSelectItemsDetails = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;

        PreparedStatement pstmtInsertIntoChannelTransferItems = null;

        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
        // added by vikram
        PreparedStatement pstmtSelectCProfileProd = null;
        ArrayList userbalanceList = null;
        UserBalancesVO balancesVO = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
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
        String c2cTransferID = null;
        PreparedStatement psmtInsertUserThreshold = null;
        long thresholdValue = -1;
        long previousSenderBalforSOSelibligity = -1;
        long balanceforSOSelibligity = -1;
        Boolean isOthComChnl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
        boolean transactionTypeAlwd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        boolean channelSosEnable = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE);
        String defautWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
        boolean c2cSmsNotify = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY);
        boolean lmsAppl = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        String txnSenderUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG));
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        
        UserLoanVO userLoanVO = null;
        
        // user life cycle
        String StatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_c2cBatchMatserVO.getNetworkCode(), p_c2cBatchMatserVO.getCategoryCode(),
            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferType())) {
                StatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                StatusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.processing");
        }
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
		String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        HashMap<String, HashMap<String, String>> tcpMap = null;
        
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        String sqlSelect = null;
        StringBuilder sqlBuffer = null;
        if(tcpOn) {

        	 sqlBuffer = new StringBuilder(" SELECT cusers.transfer_profile_id, u.status userstatus, cusers.in_suspend,up.msisdn, ");
             sqlBuffer.append("cps.status commprofilestatus,cps.language_1_message comprf_lang_1_msg, ");
             sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code,cusers.sos_allowed ,cusers.sos_allowed_amount, cusers.sos_threshold_limit ");
             sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps, user_phones up,user_geographies ug ");
             sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
             sqlBuffer.append(" AND u.status IN (" + StatusAllowed + ") ");
             sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
             sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
             sqlBuffer.append("  ug.user_id = u.user_id ");
            
			SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")), ValueType.STRING);
			tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id", "status")),
					searchCriteria);

        }else {
        sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend,up.msisdn, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code,cusers.sos_allowed ,cusers.sos_allowed_amount, cusers.sos_threshold_limit ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status IN (" + StatusAllowed + ") ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
		}
        final String sqlLoadUser = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be inserted in user_daily_balances table
        final String selectUserBalances = c2cBatchTransferQry.closeOrderByBatchselectUserBalancesQry();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
        }
        sqlBuffer = null;

        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
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
        final String insertDailyBalances = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY insertUserDailyBalances=" + insertDailyBalances);
        }
        sqlBuffer = null;

        // Select the balance of user for the perticuler product and network.
        final String selectBalance = c2cBatchTransferQry.closeOrderByBatchselectBalanceQry();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY selectBalance=" + selectBalance);
        }
        sqlBuffer = null;

        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET prev_balance = ?, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        final String updateBalance = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY updateBalance=" + updateBalance);
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
            LOG.debug(methodName, "QUERY insertBalance=" + insertBalance);
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
        sqlBuffer.append(" ,last_sos_txn_status,last_lr_status   ");
        sqlBuffer.append(" FROM user_transfer_counts ");
        // commented for DB2 & DB220120123for update WITH RS
        // sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ")
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE  WITH RS");
        } else {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        }

        final String selectTransferCounts = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
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
        final String selectProfileInCounts = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY selectProfileInCounts=" + selectProfileInCounts);
        }

        // Select the effective profile counters of sender to be checked with
        // running counters of sender added by Gopal
        final StringBuilder strBuff1 = new StringBuilder();
        strBuff1.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, ");
        strBuff1
            .append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value ,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
        strBuff1
            .append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
        strBuff1.append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value");
        strBuff1.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff1.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
        strBuff1.append(" AND tp.category_code=catp.category_code ");
        strBuff1.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
        final String selectProfileOutCounts = strBuff1.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY selectProfileOutCounts=" + selectProfileOutCounts);
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
            LOG.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
        }
        sqlBuffer = null;

        // Update the Sender running countres (If record found for user running
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
        final String updateSenderTransferCounts = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY updateSenderTransferCounts=" + updateSenderTransferCounts);
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
            LOG.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
        }
        sqlBuffer = null;

        // Insert the record in user_transfer_counts for Sender (If no record
        // found for user running counters)
        sqlBuffer = new StringBuilder(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, monthly_out_count, ");
        sqlBuffer.append(" monthly_out_value, last_out_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        final String insertSenderTransferCounts = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY insertSenderTransferCounts=" + insertSenderTransferCounts);
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // c2c_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  c2c_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" approver_remarks = ?, ");
        sqlBuffer.append(" approved_by=?, approved_on=? , status = ?  ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprvC2CBatchItems = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlApprvC2CBatchItems=" + sqlApprvC2CBatchItems);
        }
        sqlBuffer = null;

        // Afetr all the records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These counts will be used to deceide what
        // status to be updated in master table
        sqlBuffer = new StringBuilder("SELECT fb.batch_total_record,SUM(case cbi.status when ? then 1 else 0 end) as new,");
        sqlBuffer.append(" SUM(case cbi.status when ? then 1 else 0 end) appr,SUM(case cbi.status when ? then 1 else 0 end) cncl, ");
        sqlBuffer.append(" SUM(case cbi.status when ? then 1 else 0 end) closed ");
        sqlBuffer.append(" FROM c2c_batches fb,c2c_batch_items cbi ");
        sqlBuffer.append(" WHERE fb.batch_id=cbi.batch_id AND fb.batch_id=? group by fb.batch_total_record");
        final String selectItemsDetails = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE c2c_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateC2CBatches = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY updateC2CBatches=" + updateC2CBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM c2c_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY isModified=" + isModified);
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
            LOG.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfer
        // items table for the order that is closed
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price, ");
        sqlBuffer.append(" sender_debit_quantity, receiver_credit_quantity, sender_post_stock, receiver_post_stock,commision_quantity,otf_type,otf_rate,otf_amount,otf_applicable )  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfers
        // table for the order that is cloaed
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
        sqlBuffer.append(" sender_grade_code, sender_txn_profile, ");
        sqlBuffer.append(" control_transfer,msisdn,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id,dual_comm_type,commission_from ) ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
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
            LOG.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
        }
        sqlBuffer = null;

        // for loading the products associated with the commission profile added
        // by vikram.
        sqlBuffer = new StringBuilder("SELECT cp.discount_type,cp.discount_rate,cp.taxes_on_channel_transfer ");
        sqlBuffer.append("FROM commission_profile_products cp ");
        sqlBuffer.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        if(transactionTypeAlwd)
        sqlBuffer.append("AND cp.transaction_type in (?,?) ");
        else
        sqlBuffer.append("AND cp.transaction_type = ? ");
        sqlBuffer.append("AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
        final String strBuffSelectCProfileProd = sqlBuffer.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("closeOrederByBatch", "strBuffSelectCProfileProd Query =" + strBuffSelectCProfileProd);
        }

        final StringBuilder strBuffThresholdInsert = new StringBuilder();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type,remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("closeOrederByBatch", "QUERY insertUserThreshold=" + insertUserThreshold);
        }

        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        ChannelTransferVO channelTransferVO = null;
        try {
            C2CBatchItemsVO c2cBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            ChannelUserVO channelUserVoObj = new ChannelUserVO();
            // ChannelTransferVO channelTransferVO=null
            ChannelTransferItemsVO channelTransferItemVO = null;
            date = new Date();
            ArrayList channelTransferItemVOList = null;

            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            pstmtSelectUserBalances = p_con.prepareStatement(selectUserBalances);
            pstmtUpdateUserBalances = p_con.prepareStatement(updateUserBalances);

            pstmtUpdateSenderBalanceOn = p_con.prepareStatement(updateUserBalances);

            pstmtInsertUserDailyBalances = p_con.prepareStatement(insertDailyBalances);

            pstmtInsertSenderDailyBalances = p_con.prepareStatement(insertDailyBalances);
            pstmtSelectSenderBalance = p_con.prepareStatement(selectUserBalances);
            pstmtUpdateSenderBalance = p_con.prepareStatement(updateBalance);

            pstmtSelectBalance = p_con.prepareStatement(selectBalance);
            pstmtUpdateBalance = p_con.prepareStatement(updateBalance);
            pstmtInsertBalance = p_con.prepareStatement(insertBalance);
            pstmtSelectTransferCounts = p_con.prepareStatement(selectTransferCounts);
            pstmtSelectSenderTransferCounts = p_con.prepareStatement(selectTransferCounts);
            pstmtSelectProfileCounts = p_con.prepareStatement(selectProfileInCounts);
            pstmtSelectSenderProfileOutCounts = p_con.prepareStatement(selectProfileOutCounts);
            // pstmtSelectSenderProfileInCounts=p_con.prepareStatement(selectProfileInCounts)commente
            // for DB2
            pstmtUpdateTransferCounts = p_con.prepareStatement(updateTransferCounts);
            pstmtUpdateSenderTransferCounts = p_con.prepareStatement(updateSenderTransferCounts);
            pstmtInsertTransferCounts = p_con.prepareStatement(insertTransferCounts);
            pstmtInsertSenderTransferCounts = p_con.prepareStatement(insertSenderTransferCounts);
            // psmtApprC2CBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprvC2CBatchItems);commented
            // for DB2
            psmtApprC2CBatchItem = p_con.prepareStatement(sqlApprvC2CBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateC2CBatches);commented
            // for DB2
            pstmtUpdateMaster = p_con.prepareStatement(updateC2CBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = p_con.prepareStatement(loadTransferProfileProduct);

            pstmtInsertIntoChannelTransferItems = p_con.prepareStatement(insertIntoChannelTransferItem);
            // pstmtInsertIntoChannelTranfers=(OraclePreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);//commented
            // for DB2
            pstmtInsertIntoChannelTranfers = p_con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = p_con.prepareStatement(selectBalanceInfoForMessage);
            // added by vikram
            pstmtSelectCProfileProd = p_con.prepareStatement(strBuffSelectCProfileProd);
            psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);

            long senderPreviousBal = -1; // taking sender previous balance as 0

            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defautWebGatewayCode);
            int dayDifference = 0;
            final String network_id = null;
            Date dailyBalanceUpdatedOn = null;
            TransferProfileProductVO transferProfileProductVO = null;
            UserTransferCountsVO countsVO = null;
            UserTransferCountsVO senderCountsVO = null;
            TransferProfileVO transferProfileVO = null;
            TransferProfileVO senderTfrProfileCheckVO = null;
            long maxBalance = 0;
            boolean isNotToExecuteQuery = false;
            long balance = -1;
            long senderBalance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousSenderBalToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;

            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                c2cBatchItemVO = (C2CBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = c2cBatchItemVO.getBatchId();
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Executed c2cBatchItemVO=" + c2cBatchItemVO.toString());
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, c2cBatchItemVO.getUserId());
                try {
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = channelUserVoObj;
                    channelUserVO.setUserID(c2cBatchItemVO.getUserId());
                    channelUserVO.setMsisdn(rs.getString("msisdn"));
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
                    if(channelSosEnable){
                    	channelUserVO.setSosAllowed(rs.getString("sos_allowed"));
                    	channelUserVO.setSosAllowedAmount(rs.getLong("sos_allowed_amount"));
                    	channelUserVO.setSosThresholdLimit(rs.getLong("sos_threshold_limit"));
                        }
                    language = rs.getString("phone_language");
                    country = rs.getString("country");
                    channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                    
                    // (user status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    /*
                     * if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.
                     * getStatus()))
                     * {
                     * p_con.rollback()
                     * errorVO=new
                     * ListValueVO(c2cBatchItemVO.getMsisdn(),String.
                     * valueOf(c2cBatchItemVO
                     * .getRecordNumber()),p_messages.getMessage
                     * (p_locale,"batchc2c.batchapprovereject.msg.error.usersuspend"
                     * ))
                     * errorList.add(errorVO)
                     * BatchC2CFileProcessLog.detailLog("closeOrederByBatch",
                     * p_c2cBatchMatserVO
                     * ,c2cBatchItemVO,"FAIL : User is suspend"
                     * ,"Approval level"+p_currentLevel)
                     * continue
                     * }
                     */
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Commission profile suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Transfer profile suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : User is IN suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then make entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
                    continue;
                }
            }
            finally {
            	if(rs!=null)
            		rs.close();
            }
                // creating the channelTransferVO here since C2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth c2c items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(p_c2cBatchMatserVO.getNetworkCode());
                channelTransferVO.setNetworkCodeFor(p_c2cBatchMatserVO.getNetworkCodeFor());
                channelTransferVO.setToUserID(p_c2cBatchMatserVO.getUserId());
                channelTransferVO.setProductCode(p_c2cBatchMatserVO.getProductCode()); 
                // ChannelTransferBL.genrateTransferID(channelTransferVO)
                if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(c2cBatchItemVO
                    .getTransferSubType())) {
                    ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);
                } else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO
                    .getTransferSubType())) {
                    ChannelTransferBL.genrateChnnlToChnnlWithdrawID(channelTransferVO);
                }
                /*
                 * else
                 * if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO
                 * .getTransferType()) &&
                 * PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN
                 * .equals(c2cBatchItemVO.getTransferSubType()))
                 * ChannelTransferBL.genrateChnnlToChnnlReturnID(channelTransferVO
                 * )
                 */
                c2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch c2c item was created.
                channelTransferVO.setCreatedOn(c2cBatchItemVO.getInitiatedOn());

                dayDifference = 0;

                dailyBalanceUpdatedOn = null;
                dayDifference = 0;

                pstmtSelectSenderBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectSenderBalance.setString(m, p_senderVO.getUserID());
                ++m;
                pstmtSelectSenderBalance.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                // pstmtSelectSenderBalance.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date))
                try {
                rs = null;
                rs = pstmtSelectSenderBalance.executeQuery();
                while (rs.next()) {
                    dailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                    senderPreviousBal = rs.getLong("balance");
                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, date);
                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("closeOrdersByBatch ", "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertSenderDailyBalances.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertSenderDailyBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            ++m;
                            pstmtInsertSenderDailyBalances.setString(m, rs.getString("user_id"));
                            ++m;
                            pstmtInsertSenderDailyBalances.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertSenderDailyBalances.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertSenderDailyBalances.setString(m, rs.getString("product_code"));
                            ++m;
                            pstmtInsertSenderDailyBalances.setLong(m, rs.getLong("balance"));
                            ++m;
                            pstmtInsertSenderDailyBalances.setLong(m, rs.getLong("prev_balance"));
                            // pstmtInsertSenderDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
                            ++m;
                            pstmtInsertSenderDailyBalances.setString(m, c2cBatchItemVO.getTransferType());
                            ++m;
                            pstmtInsertSenderDailyBalances.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertSenderDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertSenderDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertSenderDailyBalances.setString(m, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertSenderDailyBalances.executeUpdate();

                            if (updateCount <= 0) {
                                p_con.rollback();
                                errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                                    "FAIL : DB Error while inserting user daily balances table", "Approval level = " + p_currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + p_currentLevel);
                            continue;
                        }
                        // Update the user balances table
                        pstmtUpdateSenderBalanceOn.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateSenderBalanceOn.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateSenderBalanceOn.setString(m, p_senderVO.getUserID());
                        updateCount = pstmtUpdateSenderBalanceOn.executeUpdate();
                        // (record not updated properly) if this condition is
                        // true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            p_con.rollback();
                            errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                                "FAIL : DB Error while updating user balances table for daily balance", "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
            }
            	finally {
            		if(rs!=null)
            			rs.close();
            	}
            	
                maxBalance = 0;
                isNotToExecuteQuery = false;

                // select the record form the userBalances table.
                pstmtSelectUserBalances.clearParameters();
                m = 0;
                ++m;
                pstmtSelectUserBalances.setString(m, channelUserVO.getUserID());
                // pstmtSelectUserBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(date))
                ++m;
                pstmtSelectUserBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                try {
                rs = null;
                rs = pstmtSelectUserBalances.executeQuery();
                while (rs.next()) {
                    dailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, date);
                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("closeOrdersByBatch ", "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertUserDailyBalances.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertUserDailyBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("user_id"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("product_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs.getLong("balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs.getLong("prev_balance"));
                            // pstmtInsertUserDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, c2cBatchItemVO.getTransferType());
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
                                errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                                    "FAIL : DB Error while inserting user daily balances table", "Approval level = " + p_currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            p_con.rollback();
                            BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
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
                            errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                                "FAIL : DB Error while updating user balances table for daily balance", "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
                 // till now user daily balances is updated. for both sender and
                 // receiver users.
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
            
                channelTransferItemVO = new ChannelTransferItemsVO();
                
                
                // creating channel transfers and respective tranfers items vo
                // receiver user will be updated the the proper amount creation
                // added by vikram
                m = 0;
                rs = null;
                pstmtSelectCProfileProd.clearParameters();
                ++m;
                pstmtSelectCProfileProd.setString(m, p_c2cBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectCProfileProd.setString(m, c2cBatchItemVO.getCommissionProfileSetId());
                ++m;
                pstmtSelectCProfileProd.setString(m, c2cBatchItemVO.getCommissionProfileVer());
                if(transactionTypeAlwd)
                {
                	++m;
                	pstmtSelectCProfileProd.setString(m,PretupsI.TRANSFER_TYPE_C2C);
                	++m;
                	pstmtSelectCProfileProd.setString(m,PretupsI.ALL);
                }
                else
                {
                ++m;
                pstmtSelectCProfileProd.setString(m,PretupsI.ALL);
                }
                ++m;
                pstmtSelectCProfileProd.setString(m, PretupsI.ALL);
                try{
                rs = pstmtSelectCProfileProd.executeQuery();
                if (rs.next()) {
                    channelTransferItemVO.setDiscountType(rs.getString("discount_type"));
                    channelTransferItemVO.setDiscountRate(rs.getDouble("discount_rate"));
                    if (PretupsI.YES.equals(rs.getString("taxes_on_channel_transfer"))) {
                        channelTransferItemVO.setTaxOnChannelTransfer(PretupsI.YES);
                    } else {
                        channelTransferItemVO.setTaxOnChannelTransfer(PretupsI.NO);
                    }
                }
            }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                
                
            
                LOG.debug(methodName,c2cBatchItemVO.getCommissionProfileSetId()+"----"+c2cBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                channelTransferVO.setFromUserID(p_c2cBatchMatserVO.getUserId());
                channelTransferVO.setFromUserCode(p_senderVO.getMsisdn());
                channelTransferVO.setToUserCode(c2cBatchItemVO.getMsisdn());
                channelTransferVO.setCommProfileSetId(c2cBatchItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(c2cBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setDualCommissionType(c2cBatchItemVO.getDualCommissionType());
                channelTransferVO.setRequestedQuantity(c2cBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setRequestedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
                channelTransferItemVO.setApprovedQuantity(c2cBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setCommProfileDetailID(c2cBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(c2cBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(c2cBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(c2cBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(c2cBatchItemVO.getTransferMrp());
                channelTransferItemVO.setProductCode(p_c2cBatchMatserVO.getProductCode());
                // channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)

                channelTransferItemVO.setRequiredQuantity(c2cBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(c2cBatchItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(c2cBatchItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(c2cBatchItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(c2cBatchItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(c2cBatchItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(c2cBatchItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(c2cBatchItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(c2cBatchItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(c2cBatchItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(c2cTransferID);
                channelTransferItemVO.setUnitValue(p_c2cBatchMatserVO.getProductMrp());
                // for the balance logger
                // channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)

                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                
                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{
					ChannelTransferBL.increaseOptOTFCounts(p_con, channelTransferVO);
					channelTransferVO.setOtfFlag(true);
	
				       	 final ArrayList<ChannelTransferItemsVO> list = new ArrayList<>();
				       	 if(channelTransferVO.getChannelTransferitemsVOListforOTF()!=null && channelTransferVO.getChannelTransferitemsVOList()!=null )
				       	 {
				        for(int i=0; i < channelTransferVO.getChannelTransferitemsVOList().size(); i++){
				     	  ChannelTransferItemsVO ctiVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(i);
				     	  ChannelTransferItemsVO ctiOTFVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOListforOTF().get(i);
				     	  ctiVO.setOtfApplicable(ctiOTFVO.isOtfApplicable());
				     	   list.add(ctiVO);
				        }
				        channelTransferVO.setChannelTransferitemsVOList(list);
				       	 }
						
						
				}
    			if(isOthComChnl){
					if(!BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()))
						channelTransferVO.setToUserMsisdn(c2cBatchItemVO.getMsisdn());
					else if(!BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()))
						channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserWebDAO().loadChannelUserDetailsByLoginIDANDORMSISDN(p_con,"",c2cBatchItemVO.getLoginID())).getMsisdn());
					else if(!BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()))
						channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChnlUserDetailsByExtCode(p_con, c2cBatchItemVO.getExternalCode())).getMsisdn());
				 channelTransferVO.setCommProfileSetId(c2cBatchItemVO.getCommissionProfileSetId());
				 channelTransferVO.setCommProfileVersion(c2cBatchItemVO.getCommissionProfileVer());
					if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null) {
	  					channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
	  					channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
  					}
				}
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_C2C);

                maxBalance = 0;
                isNotToExecuteQuery = false;
                // sender balance to be debited
                // now processing the sender balances.
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, p_senderVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, p_c2cBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, p_c2cBatchMatserVO.getNetworkCodeFor());
                try {
                rs = null;
                rs = pstmtSelectBalance.executeQuery();
                senderBalance = -1;
                previousSenderBalToBeSetChnlTrfItems = -1;
                if (rs.next()) {
                    senderBalance = rs.getLong("balance");
					if(senderPreviousBal==-1){
                    senderPreviousBal=senderBalance;
                    }
                    
                } else {
                    p_con.rollback();
                    errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchc2capprove.closeOrderByBatch.sendernobal"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                        "FAIL : DB Error while selecting user balances table for daily balance", "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                
            	
            	
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_c2cBatchMatserVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
                    .equals(p_c2cBatchMatserVO.getTransferSubType())) {
                    previousSenderBalToBeSetChnlTrfItems = senderBalance;
                    senderBalance += c2cBatchItemVO.getRequestedQuantity();
                } else if ((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferType())) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
                    .equals(p_c2cBatchMatserVO.getTransferSubType())) {
                	
                	Boolean isPositiveCommDebitFromSender = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_POSITIVE_COMM_DEBIT_FROM_SENDER_REQ);
                	
                	if(isPositiveCommDebitFromSender && PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())) {

                		if (senderBalance == 0 || senderBalance - c2cBatchItemVO.getRequestedQuantity() - channelTransferItemVO.getCommQuantity() < 0) {
                			p_con.rollback();
                			errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                					"batchc2c.batchc2capprove.closeOrderByBatch.senderbalzero"));
                			errorList.add(errorVO);
                			BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                					"FAIL : DB Error while selecting user balances table for daily balance", "Approval level = " + p_currentLevel);
                			continue;
                		} else if (senderBalance != 0 && (senderBalance - c2cBatchItemVO.getRequestedQuantity() - channelTransferItemVO.getCommQuantity() >= 0)) {
                			previousSenderBalToBeSetChnlTrfItems = senderBalance;
                			senderBalance -= ( c2cBatchItemVO.getRequestedQuantity()+channelTransferItemVO.getCommQuantity());
                		} else {
                			previousSenderBalToBeSetChnlTrfItems = 0;
                		}
                	
                		
                	}
                	else {
                		if (senderBalance == 0 || senderBalance - c2cBatchItemVO.getRequestedQuantity() < 0) {
                			p_con.rollback();
                			errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                					"batchc2c.batchc2capprove.closeOrderByBatch.senderbalzero"));
                			errorList.add(errorVO);
                			BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                					"FAIL : DB Error while selecting user balances table for daily balance", "Approval level = " + p_currentLevel);
                			continue;
                		} else if (senderBalance != 0 && (senderBalance - c2cBatchItemVO.getRequestedQuantity() >= 0)) {
                			previousSenderBalToBeSetChnlTrfItems = senderBalance;
                			senderBalance -= c2cBatchItemVO.getRequestedQuantity();
                		} else {
                			previousSenderBalToBeSetChnlTrfItems = 0;
                		}
                	}
                }
                m = 0;
                // update sender balance
                if (senderBalance > -1) {
                    pstmtUpdateSenderBalance.clearParameters();
                    handlerStmt = pstmtUpdateSenderBalance;
                }
                ++m;
                handlerStmt.setLong(m, previousSenderBalToBeSetChnlTrfItems);
                ++m;
                handlerStmt.setLong(m, senderBalance);
                // handlerStmt.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
                ++m;
                handlerStmt.setString(m, c2cBatchItemVO.getTransferType());
                ++m;
                handlerStmt.setString(m, c2cTransferID);
                ++m;
                handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                handlerStmt.setString(m, p_senderVO.getUserID());
                ++m;
                handlerStmt.setString(m, p_c2cBatchMatserVO.getProductCode());
                ++m;
                handlerStmt.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                ++m;
                handlerStmt.setString(m, p_c2cBatchMatserVO.getNetworkCodeFor());
                updateCount = handlerStmt.executeUpdate();
                
                
                
                ArrayList<UserLoanVO> listArr = chnlUserVO.getUserLoanVOList();
                if(listArr!=null && listArr.size()>0) {
                	for (UserLoanVO loanVO: listArr) {
                		if(loanVO.getProduct_code().equals(channelTransferVO.getProductCode()))
                		{
                			if(SystemPreferences.USERWISE_LOAN_ENABLE &&PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(c2cBatchItemVO.getTransferType())&& loanVO!=null &&loanVO.getLoan_threhold()<previousSenderBalToBeSetChnlTrfItems&&loanVO.getLoan_threhold()>senderBalance ) {

                				try {
                					channelTransferVO.setUserLoanVOList(chnlUserVO.getUserLoanVOList());
                					if(channelTransferVO.getUserLoanVOList()!=null) {
                						for(int index=0;index<channelTransferVO.getUserLoanVOList().size();index++){
                							channeluserLoanVOList1.add(channelTransferVO.getUserLoanVOList().get(index));
                						}
                					}
                					balanceforSOSelibligity = senderBalance;
                					previousSenderBalforSOSelibligity=previousSenderBalToBeSetChnlTrfItems;


                				} catch (Exception ex) {
                					LOG.errorTrace(methodName, ex);
                				}

                			} 
                		}
                	}

                }
                
                if (channelSosEnable&&PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(c2cBatchItemVO.getTransferType())&&chnlUserVO.getSosThresholdLimit()<previousSenderBalToBeSetChnlTrfItems&&chnlUserVO.getSosThresholdLimit()>senderBalance)
                {
                	try {
                		List<ChannelSoSVO> chnlSoSVOList = new ArrayList<>();
                    	chnlSoSVOList.add(new ChannelSoSVO(chnlUserVO.getUserID(),chnlUserVO.getMsisdn(),chnlUserVO.getSosAllowed(),chnlUserVO.getSosAllowedAmount(),chnlUserVO.getSosThresholdLimit()));
                    	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
                		for(int index=0;index<channelTransferVO.getChannelSoSVOList().size();index++){
                		channeluserList1.add(channelTransferVO.getChannelSoSVOList().get(index));
                		}
                		balanceforSOSelibligity = senderBalance;
                		previousSenderBalforSOSelibligity=previousSenderBalToBeSetChnlTrfItems;
                		
                		
                	} catch (Exception ex) {
                		LOG.errorTrace(methodName, ex);
                	}
                }//end here
                
                handlerStmt.clearParameters();
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB error while credit uer balance",
                        "Approval level = " + p_currentLevel);
                    continue;
                }

                // for zero balance counter..
                try {

                    m = 0;
                    boolean isUserThresholdEntryReq = false;
                    String thresholdType = null;
                    /*
                     * if(previousSenderBalToBeSetChnlTrfItems>=thresholdValue
                     * && senderBalance <=thresholdValue)
                     * {
                     * isUserThresholdEntryReq=true
                     * thresholdType=PretupsI.BELOW_THRESHOLD_TYPE
                     * }
                     * else
                     * if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue
                     * && senderBalance >=thresholdValue)
                     * {
                     * isUserThresholdEntryReq=true
                     * thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE
                     * }
                     */
                    // added by nilesh

                    transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_senderVO.getTransferProfileID(), p_c2cBatchMatserVO.getProductCode());
                    final String remark = null;
                    String threshold_type = null;
                    if (senderBalance <= transferProfileProductVO.getAltBalanceLong() && senderBalance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        // isUserThresholdEntryReq=true
                        thresholdValue = transferProfileProductVO.getAltBalanceLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                    } else if (senderBalance < transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        // isUserThresholdEntryReq=true
                        thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                    }
                    // new
                    if (previousSenderBalToBeSetChnlTrfItems >= thresholdValue && senderBalance <= thresholdValue) {
                        isUserThresholdEntryReq = true;
                        thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
                    } else if (previousSenderBalToBeSetChnlTrfItems <= thresholdValue && senderBalance >= thresholdValue) {
                        isUserThresholdEntryReq = true;
                        thresholdType = PretupsI.ABOVE_THRESHOLD_TYPE;
                    } else if (previousSenderBalToBeSetChnlTrfItems <= thresholdValue && senderBalance <= thresholdValue) {
                        isUserThresholdEntryReq = true;
                        thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
                    }
                    // end
                    if (isUserThresholdEntryReq) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("closeOrdersByBatch",
                                "Entry in threshold counter" + thresholdValue + ", prvbal: " + previousSenderBalToBeSetChnlTrfItems + "nbal" + senderBalance);
                        }
                        psmtInsertUserThreshold.clearParameters();
                        m = 0;
                        ++m;
                        psmtInsertUserThreshold.setString(m, p_senderVO.getUserID());
                        ++m;
                        psmtInsertUserThreshold.setString(m, c2cTransferID);
                        ++m;
                        psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                        ++m;
                        psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        psmtInsertUserThreshold.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                        ++m;
                        psmtInsertUserThreshold.setString(m, p_c2cBatchMatserVO.getProductCode());
                        // psmtInsertUserThreshold.setLong(++m,
                        // p_userBalancesVO.getUnitValue())
                        ++m;
                        psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TYPE_C2C);
                        ++m;
                        psmtInsertUserThreshold.setString(m, c2cBatchItemVO.getTransferType());
                        ++m;
                        psmtInsertUserThreshold.setString(m, thresholdType);
                        ++m;
                        psmtInsertUserThreshold.setString(m, p_senderVO.getCategoryCode());
                        ++m;
                        psmtInsertUserThreshold.setLong(m, previousSenderBalToBeSetChnlTrfItems);
                        ++m;
                        psmtInsertUserThreshold.setLong(m, senderBalance);
                        ++m;
                        psmtInsertUserThreshold.setLong(m, thresholdValue);
                        // added by nilesh
                        ++m;
                        psmtInsertUserThreshold.setString(m, threshold_type);
                        ++m;
                        psmtInsertUserThreshold.setString(m, remark);

                        psmtInsertUserThreshold.executeUpdate();
                    }
                    
                  //shashi :changes start here for Threshold balance alert
                    if(threshold_type != null){
						UserBalancesVO vo = new UserBalancesVO();
						vo.setUserID(p_senderVO.getUserID());
						//vo.setProductCode(p_channelTransferVO.getProductCode());
						vo.setProductCode(p_c2cBatchMatserVO.getProductCode());
						vo.setNetworkCode(p_c2cBatchMatserVO.getNetworkCode());
						vo.setLastTransferID(c2cTransferID);
						new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,p_senderVO.getCategoryCode());
                    }
					//end here
                } catch (SQLException sqle) {
                    LOG.error(methodName, "SQLException " + sqle.getMessage());
                    LOG.errorTrace(methodName, sqle);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[closeOrderByBatch]",
                        c2cTransferID, "", p_c2cBatchMatserVO.getNetworkCode(), "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
                }// end of catch
                 // if
                 // (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferSubType()))
                 // {
                pstmtSelectSenderTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectSenderTransferCounts.setString(m, p_senderVO.getUserID());
                try {
                rs = null;
                rs = pstmtSelectSenderTransferCounts.executeQuery();
                // get the Sender transfer counts
                senderCountsVO = null;
                if (rs.next()) {
                    senderCountsVO = new UserTransferCountsVO();
                    senderCountsVO.setUserID(p_senderVO.getUserID());

                    senderCountsVO.setDailyInCount(rs.getLong("daily_in_count"));
                    senderCountsVO.setDailyInValue(rs.getLong("daily_in_value"));
                    senderCountsVO.setWeeklyInCount(rs.getLong("weekly_in_count"));
                    senderCountsVO.setWeeklyInValue(rs.getLong("weekly_in_value"));
                    senderCountsVO.setMonthlyInCount(rs.getLong("monthly_in_count"));
                    senderCountsVO.setMonthlyInValue(rs.getLong("monthly_in_value"));

                    senderCountsVO.setDailyOutCount(rs.getLong("daily_out_count"));
                    senderCountsVO.setDailyOutValue(rs.getLong("daily_out_value"));
                    senderCountsVO.setWeeklyOutCount(rs.getLong("weekly_out_count"));
                    senderCountsVO.setWeeklyOutValue(rs.getLong("weekly_out_value"));
                    senderCountsVO.setMonthlyOutCount(rs.getLong("monthly_out_count"));
                    senderCountsVO.setMonthlyOutValue(rs.getLong("monthly_out_value"));

                    senderCountsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                    senderCountsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                    senderCountsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                    senderCountsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                    senderCountsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                    senderCountsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));

                    senderCountsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                    senderCountsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                    senderCountsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                    senderCountsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                    senderCountsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                    senderCountsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));

                    senderCountsVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                    senderCountsVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                    senderCountsVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                    senderCountsVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                    senderCountsVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                    senderCountsVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));

                    senderCountsVO.setLastTransferDate(rs.getDate("last_transfer_date"));
                    
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                flag = true;
                if (senderCountsVO == null) {
                    flag = false;
                    senderCountsVO = new UserTransferCountsVO();
                }
                // If found then check for reset otherwise no need to check it
                if (flag) {
                    ChannelTransferBL.checkResetCountersAfterPeriodChange(senderCountsVO, date);
                }

                pstmtSelectSenderProfileOutCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectSenderProfileOutCounts.setString(m, p_senderVO.getTransferProfileID());
                ++m;
                pstmtSelectSenderProfileOutCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectSenderProfileOutCounts.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectSenderProfileOutCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectSenderProfileOutCounts.setString(m, PretupsI.YES);
                try {
                rs = null;
                rs = pstmtSelectSenderProfileOutCounts.executeQuery();
                if (rs.next()) {
                    senderTfrProfileCheckVO = new TransferProfileVO();
                    senderTfrProfileCheckVO.setProfileId(rs.getString("profile_id"));
                    senderTfrProfileCheckVO.setDailyOutCount(rs.getLong("daily_transfer_out_count"));
                    senderTfrProfileCheckVO.setDailyOutValue(rs.getLong("daily_transfer_out_value"));
                    senderTfrProfileCheckVO.setWeeklyOutCount(rs.getLong("weekly_transfer_out_count"));
                    senderTfrProfileCheckVO.setWeeklyOutValue(rs.getLong("weekly_transfer_out_value"));
                    senderTfrProfileCheckVO.setMonthlyOutCount(rs.getLong("monthly_transfer_out_count"));
                    senderTfrProfileCheckVO.setMonthlyOutValue(rs.getLong("monthly_transfer_out_value"));
                   
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Transfer profile not found",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferSubType())) {
                    // (daily in count reach) if this condition is true then
                    // made entry in logs and leave this data.
                    if (senderTfrProfileCheckVO.getDailyOutCount() <= senderCountsVO.getDailyOutCount()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchc2c.batchapprovereject.msg.error.sender.dailyoutcntreach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Daily transfer out count reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (daily in value reach) if this condition is true then
                    // made entry in logs and leave this data.
                    // else if(senderTfrProfileCheckVO.getDailyOutValue() <
                    // (senderCountsVO.getDailyOutValue() +
                    // c2cBatchItemVO.getRequestedQuantity() ) )
                    else if (senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO.getDailyOutValue() + c2cBatchItemVO.getTransferMrp())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchc2c.batchapprovereject.msg.error.sender.dailyoutvaluereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Daily transfer out value reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (weekly in count reach) if this condition is true then
                    // made entry in logs and leave this data.
                    else if (senderTfrProfileCheckVO.getWeeklyOutCount() <= senderCountsVO.getWeeklyOutCount()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchc2c.batchapprovereject.msg.error.sender.weeklyoutcntreach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Weekly transfer out count reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (weekly in value reach) if this condition is true then
                    // made entry in logs and leave this data.
                    // else if(senderTfrProfileCheckVO.getWeeklyOutValue() < (
                    // senderCountsVO.getWeeklyOutValue() +
                    // c2cBatchItemVO.getRequestedQuantity() ) )
                    else if (senderTfrProfileCheckVO.getWeeklyOutValue() < (senderCountsVO.getWeeklyOutValue() + c2cBatchItemVO.getTransferMrp())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchc2c.batchapprovereject.msg.error.sender.weeklyoutvaluereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Weekly transfer out value reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (monthly in count reach) if this condition is true then
                    // made entry in logs and leave this data.
                    else if (senderTfrProfileCheckVO.getMonthlyOutCount() <= senderCountsVO.getMonthlyOutCount()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchc2c.batchapprovereject.msg.error.sender.monthlyoutcntreach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Monthly transfer out count reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (monthly in value reach) if this condition is true then
                    // made entry in logs and leave this data.
                    // else if(senderTfrProfileCheckVO.getMonthlyOutValue() < (
                    // senderCountsVO.getMonthlyOutValue() +
                    // c2cBatchItemVO.getRequestedQuantity() ) )
                    else if (senderTfrProfileCheckVO.getMonthlyOutValue() < (senderCountsVO.getMonthlyOutValue() + c2cBatchItemVO.getTransferMrp())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchc2c.batchapprovereject.msg.error.sender.monthlyoutvaluereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Monthly transfer out value reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                }
                senderCountsVO.setUserID(p_senderVO.getUserID());
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO
                    .getTransferSubType())) {
                    // senderCountsVO.setDailyOutCount(senderCountsVO.getDailyOutCount()-1)
                    // senderCountsVO.setWeeklyOutCount(senderCountsVO.getWeeklyOutCount()-1)
                    // senderCountsVO.setMonthlyOutCount(senderCountsVO.getMonthlyOutCount()-1)
                    // senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()+c2cBatchItemVO.getRequestedQuantity())
                    // senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()+c2cBatchItemVO.getRequestedQuantity())
                    // senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()+c2cBatchItemVO.getRequestedQuantity())
                    senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue() - c2cBatchItemVO.getTransferMrp());
                    senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue() - c2cBatchItemVO.getTransferMrp());
                    senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue() - c2cBatchItemVO.getTransferMrp());
                } else {
                    senderCountsVO.setDailyOutCount(senderCountsVO.getDailyOutCount() + 1);
                    senderCountsVO.setWeeklyOutCount(senderCountsVO.getWeeklyOutCount() + 1);
                    senderCountsVO.setMonthlyOutCount(senderCountsVO.getMonthlyOutCount() + 1);
                    // senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()+c2cBatchItemVO.getRequestedQuantity())
                    // senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()+c2cBatchItemVO.getRequestedQuantity())
                    // senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()+c2cBatchItemVO.getRequestedQuantity())
                    senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue() + c2cBatchItemVO.getTransferMrp());
                    senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue() + c2cBatchItemVO.getTransferMrp());
                    senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue() + c2cBatchItemVO.getTransferMrp());
                }
                senderCountsVO.setLastOutTime(date);
                senderCountsVO.setLastTransferID(c2cTransferID);
                senderCountsVO.setLastTransferDate(date);

                // Update counts if found in db

                if (flag) {
                    m = 0;
                    pstmtUpdateSenderTransferCounts.clearParameters();
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getDailyInCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getDailyInValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getWeeklyInCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getWeeklyInValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getMonthlyInCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getMonthlyInValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getDailyOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getDailyOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getWeeklyOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getWeeklyOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getMonthlyOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getMonthlyOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlDailyInCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlDailyInValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlWeeklyInCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlWeeklyInValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlMonthlyInCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlMonthlyInValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlDailyOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlDailyOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlWeeklyOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlWeeklyOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlMonthlyOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getUnctrlMonthlyOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getDailySubscriberOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getDailySubscriberOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getWeeklySubscriberOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getWeeklySubscriberOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getMonthlySubscriberOutCount());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setLong(m, senderCountsVO.getMonthlySubscriberOutValue());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
                    ++m;
                    pstmtUpdateSenderTransferCounts.setString(m, senderCountsVO.getLastTransferID());
                    ++m;
                    pstmtUpdateSenderTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
                    ++m;
                    pstmtUpdateSenderTransferCounts.setString(m, senderCountsVO.getUserID());
                    updateCount = pstmtUpdateSenderTransferCounts.executeUpdate();
                }
                // Insert counts if not found in db
                else {
                    m = 0;
                    pstmtInsertSenderTransferCounts.clearParameters();
                    ++m;
                    pstmtInsertSenderTransferCounts.setLong(m, senderCountsVO.getDailyOutCount());
                    ++m;
                    pstmtInsertSenderTransferCounts.setLong(m, senderCountsVO.getDailyOutValue());
                    ++m;
                    pstmtInsertSenderTransferCounts.setLong(m, senderCountsVO.getWeeklyOutCount());
                    ++m;
                    pstmtInsertSenderTransferCounts.setLong(m, senderCountsVO.getWeeklyOutValue());
                    ++m;
                    pstmtInsertSenderTransferCounts.setLong(m, senderCountsVO.getMonthlyOutCount());
                    ++m;
                    pstmtInsertSenderTransferCounts.setLong(m, senderCountsVO.getMonthlyOutValue());
                    ++m;
                    pstmtInsertSenderTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
                    ++m;
                    pstmtInsertSenderTransferCounts.setString(m, senderCountsVO.getLastTransferID());
                    ++m;
                    pstmtInsertSenderTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
                    ++m;
                    pstmtInsertSenderTransferCounts.setString(m, senderCountsVO.getUserID());
                    updateCount = pstmtInsertSenderTransferCounts.executeUpdate();
                }
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(p_senderVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(    // p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB error while insert sender trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    } else {
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB error while uptdate sender trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    }
                    continue;
                }
                // }

                // till now sender user have been validated.

                // creating channel transfers and respective tranfers items vo
                // receiver user will be updated the the proper amount creation
                // added by vikram
                channelTransferItemVO = new ChannelTransferItemsVO();
                m = 0;
                rs = null;
                pstmtSelectCProfileProd.clearParameters();
                ++m;
                pstmtSelectCProfileProd.setString(m, c2cBatchItemVO.getProductCode());
                ++m;
                pstmtSelectCProfileProd.setString(m, c2cBatchItemVO.getCommissionProfileSetId());
                ++m;
                pstmtSelectCProfileProd.setString(m, c2cBatchItemVO.getCommissionProfileVer());
                if(transactionTypeAlwd)
                {
                	++m;
                	pstmtSelectCProfileProd.setString(m,PretupsI.TRANSFER_TYPE_C2C);
                	++m;
                	pstmtSelectCProfileProd.setString(m,PretupsI.ALL);
                }
                else
                {
                ++m;
                pstmtSelectCProfileProd.setString(m,PretupsI.ALL);
                }
                ++m;
                pstmtSelectCProfileProd.setString(m, PretupsI.ALL);
                try{
                rs = pstmtSelectCProfileProd.executeQuery();
                if (rs.next()) {
                    channelTransferItemVO.setDiscountType(rs.getString("discount_type"));
                    channelTransferItemVO.setDiscountRate(rs.getDouble("discount_rate"));
                    if (PretupsI.YES.equals(rs.getString("taxes_on_channel_transfer"))) {
                        channelTransferItemVO.setTaxOnChannelTransfer(PretupsI.YES);
                    } else {
                        channelTransferItemVO.setTaxOnChannelTransfer(PretupsI.NO);
                    }
                }
            }
                finally{
                	if(rs!=null)
                		rs.close();
                }
            

                // channelTransferVO=new ChannelTransferVO()
                channelTransferVO.setCanceledOn(c2cBatchItemVO.getCancelledOn());
                channelTransferVO.setCanceledBy(c2cBatchItemVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(c2cBatchItemVO.getApproverRemarks());
                channelTransferVO.setCommProfileSetId(c2cBatchItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(c2cBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setDualCommissionType(c2cBatchItemVO.getDualCommissionType());
	            channelTransferVO.setCreatedBy(c2cBatchItemVO.getInitiatedBy());
                channelTransferVO.setCreatedOn(c2cBatchItemVO.getInitiatedOn());
                channelTransferVO.setDomainCode(p_c2cBatchMatserVO.getDomainCode());
                channelTransferVO.setFinalApprovedBy(c2cBatchItemVO.getApprovedBy());
                channelTransferVO.setFirstApprovedOn(c2cBatchItemVO.getApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(c2cBatchItemVO.getApproverRemarks());
                channelTransferVO.setSecondApprovalLimit(0);
                // channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode())
                channelTransferVO.setBatchNum(c2cBatchItemVO.getBatchId());
                channelTransferVO.setBatchDate(p_c2cBatchMatserVO.getBatchDate());
                // channelTransferVO.setFromUserID(p_senderVO.getCategoryCode())
                // channelTransferVO.setTotalTax3(0)
                // channelTransferVO.setPayableAmount(0)
                // channelTransferVO.setNetPayableAmount(0)
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setModifiedBy(p_c2cBatchMatserVO.getModifiedBy());
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(p_c2cBatchMatserVO.getProductType());
                // channelTransferVO.setReceiverCategoryCode(c2cBatchItemVO.getCategoryCode())
                // channelTransferVO.setReceiverGradeCode(c2cBatchItemVO.getGradeCode())
                // channelTransferVO.setReceiverTxnProfile(c2cBatchItemVO.getTxnProfile())
                channelTransferVO.setReferenceNum(c2cBatchItemVO.getBatchDetailId());
                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(c2cBatchItemVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(c2cBatchItemVO.getStatus());
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO
                    .getTransferSubType())) {
                    channelTransferVO.setToUserID(p_c2cBatchMatserVO.getUserId());
                    channelTransferVO.setFromUserID(channelUserVO.getUserID());
                    channelTransferVO.setFromUserCode(c2cBatchItemVO.getMsisdn());
                    channelTransferVO.setToUserCode(p_senderVO.getMsisdn());
                    channelTransferVO.setSenderGradeCode(c2cBatchItemVO.getGradeCode());
                    channelTransferVO.setCategoryCode(c2cBatchItemVO.getCategoryCode());
                    channelTransferVO.setSenderTxnProfile(c2cBatchItemVO.getTxnProfile());
                    channelTransferVO.setReceiverCategoryCode(p_senderVO.getCategoryCode());
                    channelTransferVO.setReceiverGradeCode(p_senderVO.getUserGrade());
                    channelTransferVO.setReceiverTxnProfile(p_senderVO.getTransferProfileID());
                    channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                    channelTransferVO.setReceiverGgraphicalDomainCode(p_senderVO.getGeographicalCode());
                    // channelTransferItemVO.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems)
                    // channelTransferItemVO.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems)
                    // channelTransferItemVO.setReceiverPreviousStock(senderPreviousBal)
                    // channelTransferItemVO.setAfterTransReceiverPreviousStock(senderPreviousBal)
                } else { // FOR the transfer/return
                    channelTransferVO.setToUserID(channelUserVO.getUserID());
                    channelTransferVO.setFromUserID(p_c2cBatchMatserVO.getUserId());
                    channelTransferVO.setFromUserCode(p_senderVO.getMsisdn());
                    channelTransferVO.setToUserCode(c2cBatchItemVO.getMsisdn());
                    channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
                    channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
                    channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
                    channelTransferVO.setReceiverCategoryCode(c2cBatchItemVO.getCategoryCode());
                    channelTransferVO.setReceiverGradeCode(c2cBatchItemVO.getGradeCode());
                    channelTransferVO.setReceiverTxnProfile(c2cBatchItemVO.getTxnProfile());
                    channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
                    channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
                    // channelTransferItemVO.setSenderPreviousStock(senderPreviousBal)
                    // channelTransferItemVO.setAfterTransSenderPreviousStock(senderPreviousBal)
                    // channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)
                    // channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)
                }
                channelTransferVO.setTotalTax1(c2cBatchItemVO.getTax1Value());
                channelTransferVO.setTotalTax2(c2cBatchItemVO.getTax2Value());
                channelTransferVO.setTotalTax3(c2cBatchItemVO.getTax3Value());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
                channelTransferVO.setTransferDate(c2cBatchItemVO.getInitiatedOn());
                // channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER)
                channelTransferVO.setTransferID(c2cTransferID);
                // channelTransferVO.setTransferInitatedBy(c2cBatchItemVO.getInitiatedBy())
                channelTransferVO.setTransferInitatedBy(p_c2cBatchMatserVO.getUserId());
                // channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
                channelTransferVO.setTransferMRP(c2cBatchItemVO.getTransferMrp());

                // added by vikram
                // for setting user geo and other imp. things.
                // channelTransferVO.setFromUserID(p_senderVO.getUserID())
                // channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID())
                // channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode())
                channelTransferVO.setTransferSubType(c2cBatchItemVO.getTransferSubType());
                channelTransferVO.setTransferType(c2cBatchItemVO.getTransferType());
                channelTransferVO.setReceiverDomainCode(channelUserVO.getGeographicalCode());
                channelTransferItemVO.setRequestedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));

                channelTransferItemVO.setApprovedQuantity(c2cBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setCommProfileDetailID(c2cBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(c2cBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(c2cBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(c2cBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(c2cBatchItemVO.getTransferMrp());
                channelTransferItemVO.setProductCode(c2cBatchItemVO.getProductCode());
                // channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)

                channelTransferItemVO.setRequiredQuantity(c2cBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(c2cBatchItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(c2cBatchItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(c2cBatchItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(c2cBatchItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(c2cBatchItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(c2cBatchItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(c2cBatchItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(c2cBatchItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(c2cBatchItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(c2cTransferID);
                channelTransferItemVO.setUnitValue(p_c2cBatchMatserVO.getProductMrp());
                // for the balance logger
                // channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)

                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(p_c2cBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
				{
					ChannelTransferBL.increaseOptOTFCounts(p_con, channelTransferVO);
					channelTransferVO.setOtfFlag(true);
	
				       	 final ArrayList<ChannelTransferItemsVO> list = new ArrayList<>();
				       	 if(channelTransferVO.getChannelTransferitemsVOListforOTF()!=null && channelTransferVO.getChannelTransferitemsVOList()!=null )
				       	 {
				        for(int i=0; i < channelTransferVO.getChannelTransferitemsVOList().size(); i++){
				     	  ChannelTransferItemsVO ctiVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOList().get(i);
				     	  ChannelTransferItemsVO ctiOTFVO =  (ChannelTransferItemsVO) channelTransferVO.getChannelTransferitemsVOListforOTF().get(i);
				     	  ctiVO.setOtfApplicable(ctiOTFVO.isOtfApplicable());
				     	   list.add(ctiVO);
				        }
				        channelTransferVO.setChannelTransferitemsVOList(list);
				       	 }
						
						
				}
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO
                        .getTransferSubType())) {
                        channelTransferVO.setToUserID(p_c2cBatchMatserVO.getUserId());
                }
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_C2C);

               
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
                }

                // validate the user here..

                // now validating user.
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, p_c2cBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, p_c2cBatchMatserVO.getNetworkCodeFor());
                rs = null;
                try{
                rs = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                if (rs.next()) {
                    balance = rs.getLong("balance");
                }
            }
                finally{
                	if(rs!=null)
                		rs.close();
                }

                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_c2cBatchMatserVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
                    .equals(p_c2cBatchMatserVO.getTransferSubType())) {
                    if (balance == 0 || (balance - c2cBatchItemVO.getRequestedQuantity() < 0)) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchc2capprove.closeOrderByBatch.receiverbalnsuff"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                            "FAIL : DB Error while selecting user balances table for daily balance", "Approval level = " + p_currentLevel);
                        continue;
                    } else if (balance != 0 && balance - c2cBatchItemVO.getRequestedQuantity() >= 0) {
                        previousUserBalToBeSetChnlTrfItems = balance;
                        balance -= c2cBatchItemVO.getRequestedQuantity();
                    } else {
                        previousUserBalToBeSetChnlTrfItems = 0;
                    }
                } else if ((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferType())) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
                    .equals(p_c2cBatchMatserVO.getTransferSubType())) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    // balance += c2cBatchItemVO.getRequestedQuantity()
                    balance += channelTransferItemVO.getReceiverCreditQty();
                }
                if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferSubType())) {
                    pstmtLoadTransferProfileProduct.clearParameters();
                    m = 0;
                    ++m;
                    pstmtLoadTransferProfileProduct.setString(m, c2cBatchItemVO.getTxnProfile());
                    ++m;
                    pstmtLoadTransferProfileProduct.setString(m, p_c2cBatchMatserVO.getProductCode());
                    ++m;
                    pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    ++m;
                    pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                    rs = null;
                    try{
	                    rs = pstmtLoadTransferProfileProduct.executeQuery();
	                    // get the transfer profile of user
	                    if (rs.next()) {
	                        transferProfileProductVO = new TransferProfileProductVO();
	                        transferProfileProductVO.setProductCode(p_c2cBatchMatserVO.getProductCode());
	                        transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
	                        transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
	                    }
	                    // (transfer profile not found) if this condition is true
	                    // then made entry in logs and leave this data.
	                    else {
	                        p_con.rollback();
	                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
	                            "batchc2c.batchapprovereject.msg.error.profcountersnotfound"));
	                        errorList.add(errorVO);
	                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : User Trf Profile not found for product",
	                            "Approval level = " + p_currentLevel);
	                        continue;
	                    }
                    } finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                    maxBalance = transferProfileProductVO.getMaxBalanceAsLong();
                    // (max balance reach for the receiver) if this condition is
                    // true then made entry in logs and leave this data.
                    if (maxBalance < balance) {
                        if (!isNotToExecuteQuery) {
                            isNotToExecuteQuery = true;
                        }
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.maxbalancereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : User Max balance reached",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // check for the very first txn of the user containg the
                    // order value larger than maxBalance
                    // (max balance reach) if this condition is true then made
                    // entry in logs and leave this data.
                    else if (balance == -1 && maxBalance < c2cBatchItemVO.getRequestedQuantity()) {
                        if (!isNotToExecuteQuery) {
                            isNotToExecuteQuery = true;
                        }
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.maxbalancereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : User Max balance reached",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                }
                if (!isNotToExecuteQuery) {
                    m = 0;
                    // update
                    if (previousUserBalToBeSetChnlTrfItems > -1) {
                        pstmtUpdateBalance.clearParameters();
                        handlerStmt = pstmtUpdateBalance;
                        ++m;
                        handlerStmt.setLong(m, previousUserBalToBeSetChnlTrfItems);
                    } else {
                        // insert
                        pstmtInsertBalance.clearParameters();
                        handlerStmt = pstmtInsertBalance;
                        balance = c2cBatchItemVO.getRequestedQuantity();
                        ++m;
                        handlerStmt.setLong(m, 0);// previous balance
                        previousUserBalToBeSetChnlTrfItems = 0;
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                        // on
                        // date
                    }
                    ++m;
                    handlerStmt.setLong(m, balance);
                    // handlerStmt.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
                    ++m;
                    handlerStmt.setString(m, c2cBatchItemVO.getTransferType());
                    ++m;
                    handlerStmt.setString(m, c2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    ++m;
                    handlerStmt.setString(m, p_c2cBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, p_c2cBatchMatserVO.getNetworkCodeFor());
                    updateCount = handlerStmt.executeUpdate();
                    
                    
                    if(listArr!=null && listArr.size()>0) {
                    	for (UserLoanVO loanVO: listArr) {
                    		if(loanVO.getProduct_code().equals(channelTransferVO.getProductCode()))
                    		{
                    			userLoanVO = loanVO;
                    			 if(SystemPreferences.USERWISE_LOAN_ENABLE &&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType())&&loanVO.getLoan_threhold()<previousUserBalToBeSetChnlTrfItems&&loanVO.getLoan_threhold()>balance ) {
                                 	
                                 	try {
                                 		channelTransferVO.setUserLoanVOList(chnlUserVO.getUserLoanVOList());
                                 		if(channelTransferVO.getUserLoanVOList()!=null) {
                                 			for(int index=0;index<channelTransferVO.getUserLoanVOList().size();index++){
                                 				channeluserLoanVOList1.add(channelTransferVO.getUserLoanVOList().get(index));
                                 			}
                                 		}
                            			balanceforSOSelibligity  = balance;
                            			previousSenderBalforSOSelibligity=previousUserBalToBeSetChnlTrfItems;
                                 		
                                     		
                                 	} catch (Exception ex) {
                                 		LOG.errorTrace(methodName, ex);
                                 	}
                            		
                            	}  
                    		}
                    	}

                    }
                    
                    
                    
                    
                    if (channelSosEnable&&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType())&&channelUserVO.getSosThresholdLimit()<previousUserBalToBeSetChnlTrfItems&&channelUserVO.getSosThresholdLimit()>balance)
                    {
                    	try {
                    		List<ChannelSoSVO> chnlSoSVOList = new ArrayList<ChannelSoSVO>();
                        	chnlSoSVOList.add(new ChannelSoSVO(channelUserVO.getUserID(),channelUserVO.getMsisdn(),channelUserVO.getSosAllowed(),channelUserVO.getSosAllowedAmount(),channelUserVO.getSosThresholdLimit()));
                        	channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
                    		
                    		for(int index=0;index<channelTransferVO.getChannelSoSVOList().size();index++){
                    		channeluserList.add(channelTransferVO.getChannelSoSVOList().get(index));
                    		}
                    		balanceforSOSelibligity = balance;
                    		previousSenderBalforSOSelibligity=previousUserBalToBeSetChnlTrfItems;
                    		
                    	} catch (Exception ex) {
                    		LOG.errorTrace(methodName, ex);
                    	}
                    }
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                }
                // if
                // (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferSubType())){
                // for zero balance counter..
                try {

                    m = 0;
                    boolean isUserThresholdEntryReq = false;
                    String thresholdType = null;
                    /*
                     * if(previousUserBalToBeSetChnlTrfItems>=thresholdValue &&
                     * balance <=thresholdValue)
                     * {
                     * isUserThresholdEntryReq=true
                     * thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                     * }
                     * else
                     * if(previousUserBalToBeSetChnlTrfItems<=thresholdValue &&
                     * balance >=thresholdValue)
                     * {
                     * isUserThresholdEntryReq=true
                     * thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE
                     * }
                     */
                    // added by nilesh
                    String threshold_type = null;
                    final String remark = null;
                    if (balance <= transferProfileProductVO.getAltBalanceLong() && balance > transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        // isUserThresholdEntryReq=true
                        thresholdValue = transferProfileProductVO.getAltBalanceLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                    } else if (balance <= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        // isUserThresholdEntryReq=true
                        thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                    }
                    // new
                    if (previousUserBalToBeSetChnlTrfItems >= thresholdValue && balance <= thresholdValue) {
                        isUserThresholdEntryReq = true;
                        thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
                    } else if (previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance >= thresholdValue) {
                        isUserThresholdEntryReq = true;
                        thresholdType = PretupsI.ABOVE_THRESHOLD_TYPE;
                    } else if (previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance <= thresholdValue) {
                        isUserThresholdEntryReq = true;
                        thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
                    }
                    // end

                    if (isUserThresholdEntryReq) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("closeOrederByBatch",
                                "Entry in threshold counter" + thresholdValue + ", prvbal: " + previousUserBalToBeSetChnlTrfItems + "nbal" + balance);
                        }
                        psmtInsertUserThreshold.clearParameters();
                        m = 0;
                        ++m;
                        psmtInsertUserThreshold.setString(m, channelUserVO.getUserID());
                        ++m;
                        psmtInsertUserThreshold.setString(m, c2cTransferID);
                        ++m;
                        psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                        ++m;
                        psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        psmtInsertUserThreshold.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                        ++m;
                        psmtInsertUserThreshold.setString(m, p_c2cBatchMatserVO.getProductCode());
                        // psmtInsertUserThreshold.setLong(++m,
                        // p_userBalancesVO.getUnitValue())
                        ++m;
                        psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TYPE_C2C);
                        ++m;
                        psmtInsertUserThreshold.setString(m, c2cBatchItemVO.getTransferType());
                        ++m;
                        psmtInsertUserThreshold.setString(m, thresholdType);
                        ++m;
                        psmtInsertUserThreshold.setString(m, c2cBatchItemVO.getCategoryCode());
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
                    
                    //shashi :changes start here for Threshold balance alert
	                    if(threshold_type != null){
							UserBalancesVO vo = new UserBalancesVO();
							vo.setUserID(channelUserVO.getUserID());
							//vo.setProductCode(p_channelTransferVO.getProductCode());
							vo.setProductCode(p_c2cBatchMatserVO.getProductCode());
							vo.setNetworkCode(p_c2cBatchMatserVO.getNetworkCode());
							vo.setLastTransferID(c2cTransferID);
							new UserBalancesDAO().realTimeLowBalAlertAndAutoCredit(vo,channelUserVO.getCategoryCode());
	                    }
							
                } catch (SQLException sqle) {
                    LOG.error(methodName, "SQLException " + sqle.getMessage());
                    LOG.errorTrace(methodName, sqle);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[closeOrderByBatch]",
                        c2cTransferID, "", p_c2cBatchMatserVO.getNetworkCode(), "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
                }// end of catch
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                rs = null;
                try{
                rs = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(c2cBatchItemVO.getUserId());

                    countsVO.setDailyInCount(rs.getLong("daily_in_count"));
                    countsVO.setDailyInValue(rs.getLong("daily_in_value"));
                    countsVO.setWeeklyInCount(rs.getLong("weekly_in_count"));
                    countsVO.setWeeklyInValue(rs.getLong("weekly_in_value"));
                    countsVO.setMonthlyInCount(rs.getLong("monthly_in_count"));
                    countsVO.setMonthlyInValue(rs.getLong("monthly_in_value"));

                    countsVO.setDailyOutCount(rs.getLong("daily_out_count"));
                    countsVO.setDailyOutValue(rs.getLong("daily_out_value"));
                    countsVO.setWeeklyOutCount(rs.getLong("weekly_out_count"));
                    countsVO.setWeeklyOutValue(rs.getLong("weekly_out_value"));
                    countsVO.setMonthlyOutCount(rs.getLong("monthly_out_count"));
                    countsVO.setMonthlyOutValue(rs.getLong("monthly_out_value"));

                    countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                    countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                    countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                    countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                    countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                    countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));

                    countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                    countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                    countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                    countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                    countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                    countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));

                    countsVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                    countsVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                    countsVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                    countsVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                    countsVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                    countsVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));

                    countsVO.setLastTransferDate(rs.getDate("last_transfer_date"));
                    countsVO.setLastSOSTxnStatus(rs.getString("last_sos_txn_status"));
                    countsVO.setLastLrStatus(rs.getString("last_lr_status"));
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
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
                pstmtSelectProfileCounts.setString(m, c2cBatchItemVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                rs = null;
                try{
                rs = pstmtSelectProfileCounts.executeQuery();
                // get the transfer profile counts
                if (rs.next()) {
                    transferProfileVO = new TransferProfileVO();
                    transferProfileVO.setProfileId(rs.getString("profile_id"));
                    transferProfileVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                    transferProfileVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                    transferProfileVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                    transferProfileVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                    transferProfileVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                    transferProfileVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Transfer profile not found",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_C2C);
                
                Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(p_con, countsVO, channelTransferVO);
                if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true))
                {
             	   p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.sosPending"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog(methodName, p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : SOS Status PENDING FOR USER",
                        "Approval level = " + p_currentLevel);
                 continue;
                } 
                
                else if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
    			}
                Map<String, Object> lrHashMap = ChannelTransferBL.checkLRstatusAndAmount(p_con, countsVO, channelTransferVO);
    			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
    			}
                if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferSubType())) {
                	/* SOS Status Check*/
                	   
                    // (daily in count reach) if this condition is true then
                    // made entry in logs and leave this data.
                     if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.dailyincntreach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Daily transfer in count reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (daily in value reach) if this condition is true then
                    // made entry in logs and leave this data.
                    // else if(transferProfileVO.getDailyInValue() <
                    // (countsVO.getDailyInValue() +
                    // c2cBatchItemVO.getRequestedQuantity() ) )
                    else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + channelTransferVO.getTransferMRP())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Daily transfer in value reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (weekly in count reach) if this condition is true then
                    // made entry in logs and leave this data.
                    else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Weekly transfer in count reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (weekly in value reach) if this condition is true then
                    // made entry in logs and leave this data.
                    // else if(transferProfileVO.getWeeklyInValue() < (
                    // countsVO.getWeeklyInValue() +
                    // c2cBatchItemVO.getRequestedQuantity() ) )
                    else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + channelTransferVO.getTransferMRP())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Weekly transfer in value reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (monthly in count reach) if this condition is true then
                    // made entry in logs and leave this data.
                    else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Monthly transfer in count reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    // (mobthly in value reach) if this condition is true then
                    // made entry in logs and leave this data.
                    // else if(transferProfileVO.getMonthlyInValue() < (
                    // countsVO.getMonthlyInValue() +
                    // c2cBatchItemVO.getRequestedQuantity() ) )
                    else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + channelTransferVO.getTransferMRP())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Monthly transfer in value reach",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                }
                countsVO.setUserID(channelUserVO.getUserID());
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO
                    .getTransferSubType())) {
                    // countsVO.setDailyInCount(countsVO.getDailyInCount()-1)
                    // countsVO.setWeeklyInCount(countsVO.getWeeklyInCount()-1)
                    // countsVO.setMonthlyInCount(countsVO.getMonthlyInCount()-1)
                    // countsVO.setDailyInValue(countsVO.getDailyInValue()+c2cBatchItemVO.getRequestedQuantity())
                    // countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+c2cBatchItemVO.getRequestedQuantity())
                    // countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+c2cBatchItemVO.getRequestedQuantity())
                    countsVO.setDailyInValue(countsVO.getDailyInValue() - channelTransferVO.getTransferMRP());
                    countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() - channelTransferVO.getTransferMRP());
                    countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() - channelTransferVO.getTransferMRP());
                } else {
                    countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                    countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                    countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                    // countsVO.setDailyInValue(countsVO.getDailyInValue()+c2cBatchItemVO.getRequestedQuantity())
                    // countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+c2cBatchItemVO.getRequestedQuantity())
                    // countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+c2cBatchItemVO.getRequestedQuantity())
                    countsVO.setDailyInValue(countsVO.getDailyInValue() + channelTransferVO.getTransferMRP());
                    countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + channelTransferVO.getTransferMRP());
                    countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + channelTransferVO.getTransferMRP());
                }
                countsVO.setLastInTime(date);
                countsVO.setLastTransferID(c2cTransferID);
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
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    } else {
                        BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    }
                    continue;
                }
                // }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, c2cBatchItemVO.getBatchDetailId());
                java.sql.Timestamp newlastModified = null;
                rs = null;
                try{
                rs = pstmtIsModified.executeQuery();
                // check record is modified or not
                if (rs.next()) {
                    newlastModified = rs.getTimestamp("modified_on");
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Record is already modified",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(c2cBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : Record is already modified",
                        "Approval level = " + p_currentLevel);
                    continue;
                }

                // If apperoval then set parameters in psmtApprC2CBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel)) {
                    psmtApprC2CBatchItem.clearParameters();
                    c2cBatchItemVO.setApprovedBy(p_senderVO.getUserID());
                    c2cBatchItemVO.setApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtApprC2CBatchItem.setString(m, c2cTransferID);
                    ++m;
                    psmtApprC2CBatchItem.setString(m, p_senderVO.getActiveUserID());
                    ++m;
                    psmtApprC2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtApprC2CBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR)//commented for DB2
                    ++m;
                    psmtApprC2CBatchItem.setString(m, c2cBatchItemVO.getApproverRemarks());
                    ++m;
                    psmtApprC2CBatchItem.setString(m, p_senderVO.getUserID());
                    ++m;
                    psmtApprC2CBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtApprC2CBatchItem.setString(m, c2cBatchItemVO.getStatus());
                    ++m;
                    psmtApprC2CBatchItem.setString(m, c2cBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtApprC2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtApprC2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
                    updateCount = psmtApprC2CBatchItem.executeUpdate();
                }

                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
            	Boolean isPositiveCommDebitFromSender = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_POSITIVE_COMM_DEBIT_FROM_SENDER_REQ);
                
                // for positive commission deduct from network stock
                final boolean debit = true;
                if (!isPositiveCommDebitFromSender && PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(channelTransferVO.getTransferType())) {
                    ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, channelTransferVO, channelTransferVO.getFromUserID(), date, debit);
                    ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, channelTransferVO, channelTransferVO.getFromUserID(), date);
                }

                // added by vikram
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO
                    .getTransferSubType())) {
                    channelTransferItemVO.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
                    channelTransferItemVO.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
                    channelTransferItemVO.setReceiverPreviousStock(senderPreviousBal);
                    channelTransferItemVO.setAfterTransReceiverPreviousStock(senderPreviousBal);
                } else { // FOR the transfer/return
                    channelTransferItemVO.setSenderPreviousStock(senderPreviousBal);
                    channelTransferItemVO.setAfterTransSenderPreviousStock(senderPreviousBal);
                    channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                    channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                }
                channelTransferVO.setControlTransfer(PretupsI.YES);
                channelTransferVO.setActiveUserId(p_c2cBatchMatserVO.getCreatedBy());
                channelTransferVO.setReceiverDomainCode(channelTransferVO.getDomainCode());
                channelTransferVO.setNetPayableAmount(channelTransferItemVO.getNetPayableAmount());
                channelTransferVO.setPayableAmount(channelTransferItemVO.getPayableAmount());
                
                //Validate MRP && Successive Block for channel transaction
				long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_C2C)).longValue();
				try {
					ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(p_con, channelTransferVO, new Date(), successiveReqBlockTime4ChnlTxn);
				} catch (Exception e) {
					String message = "channeltochannel.transfer.error.mrpblocktimeout";
					String args[] = {channelTransferVO.getUserMsisdn(), PretupsBL.getDisplayAmount(channelTransferVO.getTransferMRP()),String.valueOf(successiveReqBlockTime4ChnlTxn/60)};
					errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.mrpblocktimeout"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog("closeOrederByBatch",p_c2cBatchMatserVO,c2cBatchItemVO,"FAIL : Validate MRP && Successive Block for channel transaction","Approval level = "+p_currentLevel);
					continue;					
				}
				
                m = 0;
                pstmtInsertIntoChannelTranfers.clearParameters();
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCanceledBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR)//commented for DB2
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
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR)//commented for DB2
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
                // pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getNetPayableAmount())
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferItemVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCodeFor());
                // pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayableAmount())
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferItemVO.getPayableAmount());
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
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR)//commented for DB2
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSource());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, c2cBatchItemVO.getStatus());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR)//commented for DB
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
                // pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferItemVO.getTax1Value())
                // pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferItemVO.getTax2Value())
                // pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferItemVO.getTax3Value())
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
                // pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferItemVO.getProductTotalMRP())
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferSubType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSenderGradeCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSenderTxnProfile());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, PretupsI.YES);

                // pstmtInsertIntoChannelTranfers.setString(++m,c2cBatchItemVO.getMsisdn())
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFromUserCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getToUserCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());

                // to geographical domain also inserted as the geogrpahical
                // domain that will help in reports
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverGgraphicalDomainCode());
                // pstmtInsertIntoChannelTranfers.setString(++m,p_senderVO.getGeographicalCode())

                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDefaultLang());
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondLang());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, p_c2cBatchMatserVO.getCreatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m,c2cBatchItemVO.getDualCommissionType());
            	++m;
            	if(!isPositiveCommDebitFromSender && PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()))
            		pstmtInsertIntoChannelTranfers.setString(m, PretupsI.USER_TYPE_OPT);
   		     	else if (isPositiveCommDebitFromSender && PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()))
   		    		pstmtInsertIntoChannelTranfers.setString(m, PretupsI.USER_TYPE_SENDER);
   		     	else	 
   					pstmtInsertIntoChannelTranfers.setString(m, PretupsI.BATCH_TYPE_NORMAL);
            	
                // ends here
                // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB Error while inserting in channel transfer table",
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
                // pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal)
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
                pstmtInsertIntoChannelTransferItems.setString(m, c2cTransferID);
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getUnitValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderDebitQty());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverCreditQty());
                // added by vikram
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO
                    .getTransferSubType())) {
                    ++m;
                    pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock() - channelTransferItemVO.getSenderDebitQty());
                    ++m;
                    pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock() + channelTransferItemVO.getReceiverCreditQty());
                } else { // FOR the transfer/return
                    ++m;
                    pstmtInsertIntoChannelTransferItems.setLong(m, senderPreviousBal - channelTransferItemVO.getSenderDebitQty());
                    ++m;
                    pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock() + channelTransferItemVO.getReceiverCreditQty());
                }
				pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getCommQuantity() );
				pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getOtfTypePctOrAMt() );
				pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getOtfRate() );
				pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getOtfAmount());
				pstmtInsertIntoChannelTransferItems.setString(++m, channelTransferItemVO.isOtfApplicable());
				
				
                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO,
                        "FAIL : DB Error while inserting in channel transfer items table", "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                p_con.commit();
                
                if(SystemPreferences.USERWISE_LOAN_ENABLE&&PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(c2cBatchItemVO.getTransferType()) && userLoanVO!=null && userLoanVO.getLoan_threhold()<previousSenderBalforSOSelibligity&&userLoanVO.getLoan_threhold()>balanceforSOSelibligity){
                	  new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(channeluserLoanVOList1, p_senderVO.getUserID(), balanceforSOSelibligity, previousSenderBalforSOSelibligity, channelTransferItemVO.getProductCode(), channelTransferVO.getProductType());
                  }else if(SystemPreferences.USERWISE_LOAN_ENABLE&&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType())  && userLoanVO!=null &&userLoanVO.getLoan_threhold()<previousSenderBalforSOSelibligity&&userLoanVO.getLoan_threhold()>balanceforSOSelibligity){
                	  new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(userLoanVOList, channelUserVO.getUserID(), balanceforSOSelibligity, previousSenderBalforSOSelibligity, channelTransferItemVO.getProductCode(),  channelTransferVO.getProductType());
                  }  
                
                  else if(channelSosEnable&&PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(c2cBatchItemVO.getTransferType())&&chnlUserVO.getSosThresholdLimit()<previousSenderBalforSOSelibligity&&chnlUserVO.getSosThresholdLimit()>balanceforSOSelibligity){
                	  new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList1, p_senderVO.getUserID(), balanceforSOSelibligity,previousSenderBalforSOSelibligity);
                  }else if(channelSosEnable&&PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType())&&chnlUserVO.getSosThresholdLimit()<previousSenderBalforSOSelibligity&&chnlUserVO.getSosThresholdLimit()>balanceforSOSelibligity){
                	  new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList,channelUserVO.getUserID(), balanceforSOSelibligity,previousSenderBalforSOSelibligity);
                  }
                
                BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "PASS : Order is closed successfully",
                    "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                // commit the transaction after processing each record
                // user life cycle
                int updatecount1 = 0;
                if (c2cBatchItemVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                    boolean statusChangeRequired = false;
                    if (PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferType())) { // receiver
                        // check
                        if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                            // updatecount1=operatorUtili.changeUserStatusToActive(
                            // p_con,channelTransferVO.getToUserID(),channelUserVO.getStatus())
                            final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",")
                            String newStatus[] = null;
                            for (int i = 0; i < str.length; i++) {
                                newStatus = str[i].split(":");
                                if (newStatus[0].equals(channelUserVO.getStatus())) {
                                    statusChangeRequired = true;
                                    updatecount1 = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getToUserID(), channelUserVO.getStatus(), newStatus[1]);
                                    break;
                                }
                            }
                        }
                        if (!PretupsI.USER_STATUS_ACTIVE.equals(p_senderVO.getStatus())) {
                            // updatecount1=operatorUtili.changeUserStatusToActive(
                            // p_con,channelTransferVO.getToUserID(),channelUserVO.getStatus());
                            final String str[] = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                            String newStatus[] = null;
                            for (int i = 0; i < str.length; i++) {
                                newStatus = str[i].split(":");
                                if (newStatus[0].equals(channelUserVO.getStatus())) {
                                    statusChangeRequired = true;
                                    updatecount1 = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getToUserID(), channelUserVO.getStatus(), newStatus[1]);
                                    break;
                                }
                            }
                        }

                    } else {
                        // updatecount1=operatorUtili.changeUserStatusToActive(
                        // p_con,channelTransferVO.getFromUserID(),channelUserVO.getStatus())
                        if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                            final String str[] = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",")
                            String newStatus[] = null;
                            for (int i = 0; i < str.length; i++) {
                                newStatus = str[i].split(":");
                                if (newStatus[0].equals(channelUserVO.getStatus())) {
                                    statusChangeRequired = true;
                                    updatecount1 = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getFromUserID(), channelUserVO.getStatus(), newStatus[1]);
                                    break;
                                }
                            }
                        }

                        if (!PretupsI.USER_STATUS_ACTIVE.equals(p_senderVO.getStatus())) {
                            // updatecount1=operatorUtili.changeUserStatusToActive(
                            // p_con,channelTransferVO.getToUserID(),channelUserVO.getStatus())
                            final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",")
                            String newStatus[] = null;
                            for (int i = 0; i < str.length; i++) {
                                newStatus = str[i].split(":");
                                if (newStatus[0].equals(channelUserVO.getStatus())) {
                                    statusChangeRequired = true;
                                    updatecount1 = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getToUserID(), channelUserVO.getStatus(), newStatus[1]);
                                    break;
                                }
                            }
                        }

                    }
                    if (statusChangeRequired) {
                        if (updatecount1 > 0) {
                            p_con.commit();
                            BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "PASS : user status changed successfully",
                                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);

                        } else {
                            errorVO = new ListValueVO(c2cBatchItemVO.getMsisdn(), String.valueOf(c2cBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchC2CFileProcessLog.detailLog("closeOrederByBatch", p_c2cBatchMatserVO, c2cBatchItemVO, "FAIL : DB Error while changing status table",
                                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                            continue;
                        }
                    }

                }
                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_c2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_c2cBatchMatserVO.getNetworkCodeFor());
                rs = null;
                try{
                rs = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList = new ArrayList();
                while (rs.next()) {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs.getString("product_code"));
                    balancesVO.setBalance(rs.getLong("balance"));
                    balancesVO.setProductShortCode(rs.getString("product_short_code"));
                    balancesVO.setProductShortName(rs.getString("short_name"));
                    userbalanceList.add(balancesVO);
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                // generate the message arguments to be send in SMS
                keyArgumentVO = new KeyArgumentVO();
                argsArr = new String[2];
                argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
                argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
                keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS2);
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
                        keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String c2cNotifyMsg = null;
                if (c2cSmsNotify) {
                    final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        c2cNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        c2cNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList), c2cNotifyMsg };
                }

                if (c2cNotifyMsg == null) {
                    array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList) };
                }

                if (lmsAppl) {
                	PretupsBL.loyaltyPointsDistribution(channelTransferVO,p_con);
                }
                if (PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_c2cBatchMatserVO.getTransferType())) { 
                  messages = new BTSLMessages(PretupsErrorCodesI.C2C_CHNL_TO_CHNL_TRANSFER_SMS1, array);
                }else{
                  messages = new BTSLMessages(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS1, array);
                }
                pushMessage = new PushMessage(c2cBatchItemVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
                pushMessage.push();
                OneLineTXNLog.log(channelTransferVO, null);
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
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[closeOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchC2CFileProcessLog.c2cBatchMasterLog("closeOrederByBatch", p_c2cBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(),
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
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[closeOrderByBatch]", "", "", "",
                "Exception:" + ex.getMessage());
            BatchC2CFileProcessLog.c2cBatchMasterLog("closeOrederByBatch", p_c2cBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	try {
        		if(pstmtSelectSenderProfileOutCounts!=null)
        		pstmtSelectSenderProfileOutCounts.close();
        	} catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        	
        	try {
        		if(pstmtInsertSenderTransferCounts!=null)
        			pstmtInsertSenderTransferCounts.close();
        	}catch (Exception e) {
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
                if (pstmtSelectUserBalances != null) {
                    pstmtSelectUserBalances.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertSenderDailyBalances  != null) {
                	pstmtInsertSenderDailyBalances.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectSenderBalance != null) {
                	pstmtSelectSenderBalance.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateSenderBalance != null) {
                	pstmtUpdateSenderBalance.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectSenderTransferCounts != null) {
                	pstmtSelectSenderTransferCounts.close();
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
                if (psmtApprC2CBatchItem != null) {
                    psmtApprC2CBatchItem.close();
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
                if (pstmtUpdateSenderBalanceOn  != null) {
                	pstmtUpdateSenderBalanceOn.close();
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
                if (pstmtSelectCProfileProd != null) {
                    pstmtSelectCProfileProd.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateSenderTransferCounts != null) {
                    pstmtUpdateSenderTransferCounts.close();
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
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs = null;
                try{
                rs = pstmtSelectItemsDetails.executeQuery();
                // Check the final status to be updated in master after
                // processing all records of batch
                if (rs.next()) {
                    final int totalCount = rs.getInt("batch_total_record");
                    final int closeCount = rs.getInt("closed");
                    final int cnclCount = rs.getInt("cncl");
                    String statusOfMaster = null;
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_senderVO.getActiveUserID());
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR)//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR)//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchc2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchC2CFileProcessLog.c2cBatchMasterLog("closeOrederByBatch", p_c2cBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[closeOrederByBatch]",
                            "", "", "", "Error while updating C2C_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[closeOrderByBatch]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                BatchC2CFileProcessLog.c2cBatchMasterLog("closeOrederByBatch", p_c2cBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(),
                    "Approval level = " + p_currentLevel);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.sql.processing")
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[closeOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());
                BatchC2CFileProcessLog
                    .c2cBatchMasterLog("closeOrederByBatch", p_c2cBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing")
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
            // OneLineTXNLog.log(channelTransferVO)
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: errorList size=" + errorList.size());
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_batchID=" + p_batchID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM c2c_batches WHERE batch_id = ? ";
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "sqlRecordModified=" + sqlRecordModified);
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
            LOG.error(methodName, "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[isBatchModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[isBatchModified]", "", "", "",
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
     * This method is to update the status of C2C_BATCHES table
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered   p_batchID " + p_batchID + " p_newStatus=" + p_newStatus + " p_oldStatus=" + p_oldStatus);
        }
        PreparedStatement pstmt = null;
        int updateCount = -1;
        try {
            final StringBuilder sqlBuffer = new StringBuilder("UPDATE c2c_batches SET status=? ");
            sqlBuffer.append(" WHERE batch_id=? AND status=? ");
            final String updateC2CBatches = sqlBuffer.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "QUERY updateC2CBatches=" + updateC2CBatches);
            }

            pstmt = p_con.prepareStatement(updateC2CBatches);
            int i = 1;
            pstmt.setString(i, p_newStatus);
            i++;
            pstmt.setString(i, p_batchID);
            i++;
            pstmt.setString(i, p_oldStatus);
            i++;
            // pstmt.setString(i++, p_defaultLang)
            // pstmt.setString(i++, p_secondLang)
            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[updateBatchStatus]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2CBatchTransferDAO[updateBatchStatus]", "", "", "",
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

    public LinkedHashMap loadUserListForC2CXfr(Connection p_con, String p_txnType, ChannelTransferRuleVO p_channelTransferRuleVO, String p_toCategoryCode, String p_userName, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String methodName = "loadUserListForC2CXfr";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered p_txnType=" + p_txnType + ", ToCategoryCode: " + p_toCategoryCode + " User Name: " + p_userName + ",p_channelTransferRuleVO=" + p_channelTransferRuleVO + ",p_channelUserVO=" + p_channelUserVO);
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        boolean uncontrollAllowed = false;
        boolean fixedLevelParent = false;
        boolean fixedLevelHierarchy = false;
        String fixedCatStr = null;
        boolean directAllowed = false;
        boolean chnlByPassAllowed = false;
        String unctrlLevel = null;
        String ctrlLevel = null;
        // if txn is for transfer then get the value of the transfer paramenters
        if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
            if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
                fixedLevelParent = true;
                fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
            } else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedTransferLevel())) {
                fixedLevelHierarchy = true;
                fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
            }
            if (PretupsI.YES.equals(p_channelTransferRuleVO.getDirectTransferAllowed())) {
                directAllowed = true;
            }
            if (PretupsI.YES.equals(p_channelTransferRuleVO.getTransferChnlBypassAllowed())) {
                chnlByPassAllowed = true;
            }
            if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlTransferAllowed())) {
                uncontrollAllowed = true;
                unctrlLevel = p_channelTransferRuleVO.getUncntrlTransferLevel();
            }
            ctrlLevel = p_channelTransferRuleVO.getCntrlTransferLevel();
        }
        // else if txn is for withdraw then get the value of the withdraw
        // paramenters
        else // if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnType))
        {
            if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
                fixedLevelParent = true;
                fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
            } else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedWithdrawLevel())) {
                fixedLevelHierarchy = true;
                fixedCatStr = getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
            }
            if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawAllowed())) {
                directAllowed = true;
            }
            if (PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed())) {
                chnlByPassAllowed = true;
            }
            if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlWithdrawAllowed())) {
                uncontrollAllowed = true;
                unctrlLevel = p_channelTransferRuleVO.getUncntrlWithdrawLevel();
            }
            ctrlLevel = p_channelTransferRuleVO.getCntrlWithdrawLevel();
        }

        // to load the user list we will have to apply the check of the fixed
        // level and fixed category in each
        // and every case.
        // Now we divide the whole conditions in various sub conditions as

        if (uncontrollAllowed) {
            if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(unctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
                .equals(unctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system without any check of
                    // the fixed category
                    linkedHashMap = loadUsersOutsideHireacrhy(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_userName, p_channelUserVO.getUserID(), p_txnType);
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system, which are in the
                    // hierarchy of the users of fixedCatStr categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is DOMAIN OR DOMAINTYPE
                    // pass value 0 for this parameter and null for the
                    // p_parentUserID since here no parent and
                    // no owner exist for the DOMAIN OR DOMAINTYPE level.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO.getUserID(),
                        fixedCatStr, 0, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system, which are in the
                    // direct child of the users of fixedCatStr categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is DOMAIN OR DOMAINTYPE
                    // pass value 0 for this parameter and null for the
                    // p_parentUserID since here no parent and
                    // no owner exist for the DOMAIN OR DOMAINTYPE level.
                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO.getUserID(),
                        fixedCatStr, 0, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// uncontrol domain check
            else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(unctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system within the
                    // sender'owner hierarchy
                    // without any check of the fixed category
                    linkedHashMap = loadUsersByOwnerID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName, p_channelUserVO
                        .getUserID(), p_txnType);
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system within the
                    // sender'owner hierarchy
                    // which are in the hierarchy of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is OWNER
                    // pass value 2 for this parameter and OWNERID for the
                    // p_parentUserID since here list is to be
                    // loaded by owner.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system within the
                    // sender'owner hierarchy
                    // which are in the direct child of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is OWNER
                    // pass value 2 for this parameter and OWNERID for the
                    // p_parentUserID since here list is to be
                    // loaded by owner.

                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// owner level uncontroll check
            else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(unctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system within the sender's
                    // parent hierarchy
                    // without any check of the fixed category
                    linkedHashMap = loadUsersByParentIDRecursive(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
                        p_channelUserVO.getUserID(), p_txnType);
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system within the sender's
                    // parent hierarchy,
                    // which are in the hierarchy of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is PARENT
                    // pass value 1 for this parameter and PARENTID for the
                    // p_parentUserID since here list is to be
                    // loaded by parent.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system within the sender's
                    // parent hierarchy,
                    // which are in the direct child of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is PARENT
                    // pass value 1 for this parameter and PARENTID for the
                    // p_parentUserID since here list is to be
                    // loaded by parent.
                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// parent level uncontroll check
            else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(unctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system within the sender
                    // hierarchy
                    // without any check of the fixed category so here sender's
                    // userID is passed in the calling
                    // method as the parentID to load all the users under sender
                    // recursively
                    linkedHashMap = loadUsersByParentIDRecursive(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName,
                        p_channelUserVO.getUserID(), p_txnType);
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system within the sender
                    // hierarchy,
                    // which are in the hierarchy of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is SELF but sender user
                    // have to be considered as the parent of all the requested
                    // users so
                    // pass value 1 for this parameter and sener's userID for
                    // the p_parentUserID since here list is to be
                    // loaded by senderID.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system within the sender
                    // hierarchy,
                    // which are in the direct child of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as
                    // uncontroll level is SELF but sender user
                    // have to be considered as the parent of all the requested
                    // users so
                    // pass value 1 for this parameter and sener's userID for
                    // the p_parentUserID since here list is to be
                    // loaded by senderID.
                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// Self level uncontroll check
        }// uncontrol transfer allowed check
        else {
            if (PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(ctrlLevel) || PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN
                .equals(ctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system within the receiver
                    // domain for the direct child of the owner
                    // without any check of the fixed category
                    if (directAllowed) {
                        // load all the users form the system
                        // which are direct child of the owner
                        // Sandeep goel ID USD001
                        // method is changed to remove the problem as login user
                        // is also coming in the list

                        linkedHashMap = loadUsersByDomainID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO.getToDomainCode(), p_userName,
                            p_channelUserVO.getUserID(), p_txnType);
                    }// direct transfer check
                    if (chnlByPassAllowed) {
                        // load all the users form the system
                        // which are not direct child of the owner
                        // Sandeep goel ID USD001
                        // method is changed to remove the problem as login user
                        // is also coming in the list
                        // linkedHashMap.addAll(loadUsersChnlBypassByDomainID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelTransferRuleVO.getToDomainCode(),p_userName,p_channelUserVO.getUserID()))
                        linkedHashMap.putAll(loadUsersChnlBypassByDomainID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelTransferRuleVO.getToDomainCode(),
                            p_userName, p_channelUserVO.getUserID(), p_txnType));
                    }// channel by pass check
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system within the sender
                    // domain,
                    // which are in the hierarchy of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is DOMAIN OR DOMAINTYPE
                    // pass value 0 for this parameter and null for the
                    // p_parentUserID since here no parent and
                    // no owner exist for the DOMAIN OR DOMAINTYPE level.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO.getUserID(),
                        fixedCatStr, 0, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system within the sender
                    // domain,
                    // which are in the direct child of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is DOMAIN OR DOMAINTYPE
                    // pass value 0 for this parameter and null for the
                    // p_parentUserID since here no parent and
                    // no owner exist for the DOMAIN OR DOMAINTYPE level.
                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, null, p_userName, p_channelUserVO.getUserID(),
                        fixedCatStr, 0, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// domain level control check
            else if (PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(ctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system within the
                    // sender'owner hierarchy
                    // without any check of the fixed category
                    if (directAllowed) {
                        // load all the users form the system within the
                        // sender'owner hierarchy
                        // which are direct child of the owner so here in this
                        // method calling we are sending sender's
                        // ownerID to considered as the parentID in the method
                        linkedHashMap = loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName, p_channelUserVO
                            .getUserID(), p_txnType);
                    }// direct transfer check
                    if (chnlByPassAllowed) {
                        // load all the users form the system within the
                        // sender'owner hierarchy
                        // which are not direct child of the owner so here in
                        // this method calling we are sending sender's
                        // ownerID to considered as the parentID in the method
                        // linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID()))
//                        linkedHashMap = loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
//                            p_channelUserVO.getUserID(), p_txnType);
                        linkedHashMap.putAll(loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
                                p_channelUserVO.getUserID(), p_txnType));//priyank: changed coz this data should be added to already existing linkedHashMAp
                    }// channel by pass check
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system within the sender's
                    // owner hierarchy
                    // which are in the hierarchy of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is OWNER
                    // pass value 2 for this parameter and OWNERID for the
                    // p_parentUserID since here list is to be
                    // loaded by owner.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system within the sender's
                    // owner hierarchy
                    // which are in the direct child of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is OWNER
                    // pass value 2 for this parameter and OWNERID for the
                    // p_parentUserID since here list is to be
                    // loaded by owner.
                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getOwnerID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 2, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// owner level control check
            else if (PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(ctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system within the sender's
                    // parent hierarchy
                    // without any check of the fixed category
                    if (directAllowed) {
                        // load all the users form the system within the
                        // sender's parent hierarchy
                        // which are direct child of the parent
                        linkedHashMap = loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
                            p_channelUserVO.getUserID(), p_txnType);
                    }// direct transfer check
                    if (chnlByPassAllowed) {
                        // load all the users form the system within the
                        // sender's parent hierarchy
                        // which are not direct child of the parent
                        // linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID()))
                        linkedHashMap = loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
                            p_channelUserVO.getUserID(), p_txnType);
                    }// channel by pass check
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system within the sender's
                    // parent hierarchy,
                    // which are in the hierarchy of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is PARENT
                    // pass value 1 for this parameter and PARENTID for the
                    // p_parentUserID since here list is to be
                    // loaded by parent.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system within the sender's
                    // parent hierarchy,
                    // which are in the direct child of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is PARENT
                    // pass value 1 for this parameter and PARENTID for the
                    // p_parentUserID since here list is to be
                    // loaded by parent.
                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getParentID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// parent level control check
            else if (PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(ctrlLevel)) {
                if (BTSLUtil.isNullString(fixedCatStr)) {
                    // load all the users form the system within the sender
                    // hierarchy
                    // without any check of the fixed category
                    if (directAllowed) {
                        // load all the users form the system within the
                        // sender's hierarchy
                        // which are direct child of the sender so here in this
                        // method calling we are sending sender's
                        // userID to considered as the parentID in the method
                        linkedHashMap = loadUsersByParentID(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName, p_channelUserVO
                            .getUserID(), p_txnType);
                    }// direct transfer check
                    if (chnlByPassAllowed) {
                        // load all the users form the system within the
                        // sender's hierarchy
                        // which are not direct child of the sender so here in
                        // this method calling we are sending sender's
                        // userID to considered as the parentID in the method

                        // linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID()))
                        linkedHashMap = loadUserForChannelByPass(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName,
                            p_channelUserVO.getUserID(), p_txnType);

                    }// channel by pass check
                    return linkedHashMap;
                }// fixed category null check
                else if (fixedLevelHierarchy) {
                    // load all the users form the system within the sender
                    // hierarchy,
                    // which are in the hierarchy of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is SELF but sender user
                    // have to be considered as the parent of all the requested
                    // users so
                    // pass value 1 for this parameter and sener's userID for
                    // the p_parentUserID since here list is to be
                    // loaded by senderID.
                    linkedHashMap = loadUsersForHierarchyFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level hierarchy check
                else if (fixedLevelParent) {
                    // load all the users form the system within the sender
                    // hierarchy,
                    // which are in the direct child of the users of fixedCatStr
                    // categories
                    // p_ctrlLvl (last parameter) here if value of this
                    // parameter is 1 then check will be done
                    // by parentID, if value of this parameter is 2 then check
                    // will be done by ownerID
                    // other wise no check will be required. So here as controll
                    // level is SELF but sender user
                    // have to be considered as the parent of all the requested
                    // users so
                    // pass value 1 for this parameter and sener's userID for
                    // the p_parentUserID since here list is to be
                    // loaded by senderID.
                    linkedHashMap = loadUsersForParentFixedCat(p_con, p_channelUserVO.getNetworkID(), p_toCategoryCode, p_channelUserVO.getUserID(), p_userName,
                        p_channelUserVO.getUserID(), fixedCatStr, 1, p_txnType);
                    return linkedHashMap;
                }// fixed level parent check
            }// Self level control check
        }// control transaction check
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited userList.size() = " + linkedHashMap.size());
        }
        return linkedHashMap;

    }

    /**
     * Method getCategoryStrValue.
     * This method evaluvate entered string and parse it in the form that value
     * can be passed in the database
     * query for IN condition as it convert a,b to 'a','b' format.
     * 
     * @param p_catString
     *            String
     * @return String
     */
    private String getCategoryStrValue(String p_catString) {
	final String methodName = "getCategoryStrValue";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_catString = " + p_catString);
        }

        final StringBuilder fixedCatStrBuf = new StringBuilder();
        final String tempArr[] = p_catString.split(",");
        for (int i = 0; i < tempArr.length; i++) {
            fixedCatStrBuf.append("'");
            fixedCatStrBuf.append(tempArr[i]);
            fixedCatStrBuf.append("',");
        }
        final String fixedCatStr = fixedCatStrBuf.substring(0, fixedCatStrBuf.length() - 1);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited fixedCatStr= " + fixedCatStr);
        }
        return fixedCatStr;
    }

    /**
     * This method loads all users of the specific category. In the case of
     * outSide hierarchy all users have
     * to be loaded.
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    public LinkedHashMap loadUsersOutsideHireacrhy(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_userName, String p_userID, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUsersOutsideHireacrhy";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + " User Name: " + p_userName + ",p_userID=" + p_userID);
        }
        // OraclePreparedStatement pstmt = null//commented for DB2
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
         StringBuilder strBuff = null;
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        
        
        if(tcpOn) {
            strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
            strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend ");
            strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,  ");
            strBuff.append("CPS.status commprofilestatus,CPS.language_1_message comprf_lang_1_msg, ");
            strBuff.append("CPS.language_2_message  comprf_lang_2_msg ,ub.product_code");
            strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id ,channel_users CU, CHANNEL_GRADES cg ,CATEGORIES cat ");
            strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV ");
            strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
            strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
            strBuff.append(" AND u.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
            // here user_id != ? check is for not to load the sender user in the
            // query for the same level transactions
            strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
            strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
            strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
            strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
            strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
            strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
            strBuff.append("  AND cat.status='Y'");
            strBuff.append(" ORDER BY u.user_name ");
            
            
        }else {
        strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend ");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg,ub.product_code");
        strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id ,channel_users CU, CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y'");
        strBuff.append(" ORDER BY u.user_name ");
		}
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();

        try {
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect)//commented for DB2
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            // pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR)//commented for DB
            ++i;
            pstmt.setString(i, p_userName);
            ++i;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    channelVO.setUserBalance("0");
                    LOG.errorTrace(methodName, e);
                }

                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                if(!tcpOn) {
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                }
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                
                if(!tcpOn) {
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                }
                
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
                
                
                if (tcpOn) {
    				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
    						ValueType.STRING);
    				BTSLUtil.updateMapViaMicroServiceResultSet(linkedHashMap, BTSLUtil
    						.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","profile_Name","status")), searchCriteria));
    			}

            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersOutsideHireacrhy]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersOutsideHireacrhy]", "", "",
                "", "Exception:" + ex.getMessage());
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * Method loadUsersForHierarchyFixedCat
     * This method loads all the users in the hierarchy of the users of the
     * p_fixedCat categories.
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_parentUserID
     * @param p_userName
     * @param p_userID
     * @param p_fixedCat
     * @param p_ctrlLvl
     *            here if value of this parameter is 1 then check will be done
     *            by parentID
     *            if value of this parameter is 2 then check will be done by
     *            ownerID other wise no check will be required.
     * @return ArrayList
     * @throws BTSLBaseException
     *             ArrayList
     * @author sandeep.goel
     */
    public LinkedHashMap loadUsersForHierarchyFixedCat(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentUserID, String p_userName, String p_userID, String p_fixedCat, int p_ctrlLvl, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUsersForHierarchyFixedCat";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + ", To Category Code: " + p_toCategoryCode + "  p_parentUserID=" + p_parentUserID + ", User Name: " + p_userName + ",p_userID=" + p_userID + ",p_fixedCat=" + p_fixedCat + ",p_ctrlLvl=" + p_ctrlLvl);
        }
        // OraclePreparedStatement pstmt = null//commented for DB2
        // abc
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }

        try {
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect)//commented for DB2
       	  	String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
              boolean tcpOn = false;
              Set<String> uniqueTransProfileId = new HashSet();
              
              if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
              	tcpOn = true;
              }
              
              if(tcpOn) {

            	  pstmt = c2cBatchTransferQry.loadUsersForHierarchyFixedCatTcpQry(p_con, statusAllowed, p_networkCode, p_toCategoryCode, p_parentUserID, p_userName, p_userID, p_fixedCat, p_ctrlLvl, p_txnType);
            	  
              }else {

            	pstmt = c2cBatchTransferQry.loadUsersForHierarchyFixedCatQry(p_con, statusAllowed, p_networkCode, p_toCategoryCode, p_parentUserID, p_userName, p_userID, p_fixedCat, p_ctrlLvl, p_txnType);
			  }	
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    channelVO.setUserBalance("0");
                    LOG.errorTrace(methodName, e);
                }

                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                if(!tcpOn) {
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                }
                
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                
                if(!tcpOn) {
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                }
                
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
                
                if (tcpOn) {
    				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
    						ValueType.STRING);
    				BTSLUtil.updateMapViaMicroServiceResultSet(linkedHashMap, BTSLUtil
    						.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","profile_Name","status")), searchCriteria));
    			}
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForHierarchyFixedCat]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForHierarchyFixedCat]", "",
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * Method loadUsersForParentFixedCat
     * This method loads all the users which are the direct child of the users
     * of the p_fixedCat category.
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_parentUserID
     * @param p_toCategoryCode
     * @param p_parentUserID
     * @param p_userName
     * @param p_userID
     * @param p_fixedCat
     * @param p_ctrlLvl
     *            here if value of this parameter is 1 then check will be done
     *            by parentID
     *            if value of this parameter is 2 then check will be done by
     *            ownerID other wise no check will be required.
     * @return ArrayList
     * @throws BTSLBaseException
     *             ArrayList
     */
    public LinkedHashMap loadUsersForParentFixedCat(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentUserID, String p_userName, String p_userID, String p_fixedCat, int p_ctrlLvl, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUsersForParentFixedCat";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + ", To Category Code: " + p_toCategoryCode + "  p_parentUserID=" + p_parentUserID + ", User Name: " + p_userName + ",p_userID=" + p_userID + ",p_fixedCat=" + p_fixedCat + ",p_ctrlLvl=" + p_ctrlLvl);
        }
        // OraclePreparedStatement pstmt = null//commented for DB2
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
StringBuilder strBuff = null;
        
         String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
         boolean tcpOn = false;
         Set<String> uniqueTransProfileId = new HashSet();
         
         if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
         	tcpOn = true;
         }
         
         
         if(tcpOn) {

             strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
             strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
             strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
             strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,  ");
             strBuff.append("CPS.status commprofilestatus,CPS.language_1_message comprf_lang_1_msg, ");
             strBuff.append("CPS.language_2_message  comprf_lang_2_msg ,ub.product_code");
             strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id,users pu,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
             strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV ");
             strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
             strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
             strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
             strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
             strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
             strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
             strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
             strBuff.append("  AND cat.status='Y' ");
             // here user_id != ? check is for not to load the sender user in the
             // query for the same level transactions
             strBuff.append("AND u.parent_id=pu.user_id  AND pu.category_code IN (" + p_fixedCat + ") ");
             strBuff.append("AND UPPER(u.user_name) LIKE UPPER(?) ");
             if (p_ctrlLvl == 1) {
                 // strBuff.append(" AND ( pu.parent_id = ? OR u.user_id= ? )")
                 strBuff.append(" AND pu.parent_id = case pu.parent_id when 'ROOT' then pu.parent_id else ? end ");
                 strBuff.append(" AND u.parent_id = case pu.parent_id when 'ROOT' then ? else u.parent_id end ");
                 // here pu.parent_id = ? check by pu is done since pu.parent_id is
                 // the parent of selected user's parent
                 // for example POS to POSA and only to POSA which are child of POS,
                 // under the hierarchy of POS's parent.
             } else if (p_ctrlLvl == 2) {
                 // strBuff.append(" AND ( u.owner_id = ? OR u.user_id= ? ) ")
                 strBuff.append(" AND pu.owner_id = ? ");
                 // here pu.owner_id = ? or u.owner_id =? any can be used since owner
                 // is same for all.
             }
             strBuff.append("ORDER BY u.user_name ");

              
             
         }else {
        strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ,ub.product_code");
        strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id,users pu,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        strBuff.append("AND u.parent_id=pu.user_id  AND pu.category_code IN (" + p_fixedCat + ") ");
        strBuff.append("AND UPPER(u.user_name) LIKE UPPER(?) ");
        if (p_ctrlLvl == 1) {
            // strBuff.append(" AND ( pu.parent_id = ? OR u.user_id= ? )")
            strBuff.append(" AND pu.parent_id = case pu.parent_id when 'ROOT' then pu.parent_id else ? end ");
            strBuff.append(" AND u.parent_id = case pu.parent_id when 'ROOT' then ? else u.parent_id end ");
            // here pu.parent_id = ? check by pu is done since pu.parent_id is
            // the parent of selected user's parent
            // for example POS to POSA and only to POSA which are child of POS,
            // under the hierarchy of POS's parent.
        } else if (p_ctrlLvl == 2) {
            // strBuff.append(" AND ( u.owner_id = ? OR u.user_id= ? ) ")
            strBuff.append(" AND pu.owner_id = ? ");
            // here pu.owner_id = ? or u.owner_id =? any can be used since owner
            // is same for all.
        }
        strBuff.append("ORDER BY u.user_name ");
	}
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);//commented for DB2
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            // pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
            ++i;
            pstmt.setString(i, p_userName);
            if (p_ctrlLvl == 1) {
                ++i;
                pstmt.setString(i, p_parentUserID);
                ++i;
                pstmt.setString(i, p_parentUserID);
            } else if (p_ctrlLvl == 2) {
                ++i;
                pstmt.setString(i, p_parentUserID);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    channelVO.setUserBalance("0");
                }
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                if(!tcpOn) {
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                }
                
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                
                if(!tcpOn) {
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                }
                
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
                
                
                
                if (tcpOn) {
    				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
    						ValueType.STRING);
    				BTSLUtil.updateMapViaMicroServiceResultSet(linkedHashMap, BTSLUtil
    						.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","profile_Name","status")), searchCriteria));
    			}

            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForParentFixedCat]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForParentFixedCat]", "", "",
                "", "Exception:" + ex.getMessage());
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * Method loadUsersByOwnerID.
     * This method loads all the users under the owner user passed as argument.
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_toCategoryCode
     *            String
     * @param p_ownerID
     *            String
     * @param p_userName
     *            String
     * @param p_userID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LinkedHashMap loadUsersByOwnerID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_ownerID, String p_userName, String p_userID, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUsersByOwnerID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + " p_ownerID: " + p_ownerID + "User Name: " + p_userName + "  ,p_userID=" + p_userID);
        }
        // OraclePreparedStatement pstmt = null; //commented for DB2
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }

         StringBuilder strBuff = null;
        
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
        Set<String> uniqueTransProfileId = new HashSet();
        
        if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
        	tcpOn = true;
        }
        
        
        if(tcpOn) {
        	strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
            strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
            strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
            strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version,  ");
            strBuff.append("CPS.status commprofilestatus,CPS.language_1_message comprf_lang_1_msg, ");
            strBuff.append("CPS.language_2_message  comprf_lang_2_msg,ub.product_code ");
            strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
            strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV ");
            strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ") AND u.category_code = ? AND u.user_id != ?");
            strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
            strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
            strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
            strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
            strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
            strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
            strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
            strBuff.append("  AND cat.status='Y' ");
            // here user_id != ? check is for not to load the sender user in the
            // query for the same level transactions
            strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND ( u.owner_id = ? OR u.user_id = ? ) ORDER BY u.user_name ");
            // here owner_id = ? OR user_id = ? check is to load the owner also if
            // transaction is to owner only.
            
 	
        }else {
        strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg,ub.product_code ");
        strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ") AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND ( u.owner_id = ? OR u.user_id = ? ) ORDER BY u.user_name ");
        // here owner_id = ? OR user_id = ? check is to load the owner also if
        // transaction is to owner only.
		
		}
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);//commented for DB2
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            // pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
            ++i;
            pstmt.setString(i, p_userName);
            ++i;
            pstmt.setString(i, p_ownerID);
            ++i;
            pstmt.setString(i, p_ownerID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    channelVO.setUserBalance("0");
                }
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
             if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                
                if(!tcpOn) {
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                }
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                
                if(!tcpOn) {
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                }
                
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }

				if (tcpOn) {
					SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
							ValueType.STRING);
					BTSLUtil.updateMapViaMicroServiceResultSet(linkedHashMap,
							BTSLUtil.fetchMicroServiceTCPDataByKey(
									new HashSet<String>(Arrays.asList("profile_id", "profile_Name", "status")),
									searchCriteria));
				}
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByOwnerID]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByOwnerID]", "", "", "",
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;

    }

    /**
     * loadUsersByParentIDRecursive
     * This method loads all the user under the parent user which is passed as
     * argurment.
     * 
     * @author sandeep.goel
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_parentID
     * @param p_userName
     * @param p_userID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LinkedHashMap loadUsersByParentIDRecursive(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUsersByParentIDRecursive";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + " p_parentID: " + p_parentID + "User Name: " + p_userName + " ,p_userID=" + p_userID);
        }
        // commented for DB2 OraclePreparedStatement pstmt = null
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
       
        try {
        	  String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
              boolean tcpOn = false;
              Set<String> uniqueTransProfileId = new HashSet();
              
              if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
              	tcpOn = true;
              }
              String sqlSelect = null;
              
              if(tcpOn) {
            	  pstmt = c2cBatchTransferQry.loadUsersByParentIDRecursiveTcpQry(p_con, p_networkCode, p_toCategoryCode, p_parentID, p_userName, p_userID, p_txnType, statusAllowed);
       	
              }else {
             	pstmt = c2cBatchTransferQry.loadUsersByParentIDRecursiveQry(p_con, p_networkCode, p_toCategoryCode, p_parentID, p_userName, p_userID, p_txnType, statusAllowed);
			 }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    channelVO.setUserBalance("0");
                    LOG.errorTrace(methodName, e);
                }
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                if(!tcpOn) {
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                }
                
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                

                if(!tcpOn) {
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                }
                
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
                
                if (tcpOn) {
    				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
    						ValueType.STRING);
    				BTSLUtil.updateMapViaMicroServiceResultSet(linkedHashMap, BTSLUtil
    						.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","profile_Name","status")), searchCriteria));
    			}
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentIDRecursive]", "", "",
                "", "Exception:" + ex.getMessage());
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * method loadUsersByDomainID
     * This method load all the users of the specified category which are the
     * direct child of the owner.
     * This will be called to download users list at domain level and have
     * direct T/R/W allowed
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_domainID
     * @param p_userName
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public LinkedHashMap loadUsersByDomainID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_domainID, String p_userName, String p_userID, String p_txnType) throws BTSLBaseException {
        final String methodName = "loadUsersByDomainID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + "p_domainID: " + p_domainID + " User Name: " + p_userName + ", p_userID=" + p_userID);
        }
        // OraclePreparedStatement pstmt = null;//commented for DB2
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // user life cycle
        String statusAllowed = null;
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ,ub.product_code");
        strBuff.append(" FROM users u left join USER_BALANCES ub on  U.user_id=ub.user_id,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            strBuff.append(" where date_trunc ('minute',cpsv1.applicable_from :: timestamp) <= date_trunc ('minute', TO_timestamp(?,'yyyy-mm-dd HH24:MI:SS')) ");
        }else{
            strBuff.append(" WHERE CPSV1.applicable_from <= ? ");    
        }
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
        strBuff.append(" AND u.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // query is changed to optimization and to remove the problem as owner
        // was not coming in the list.
        // and login user is also coming in the list
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND  (( u.parent_id IN  ( ");
        strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
        strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ) ");
        strBuff.append(" OR (u.parent_id ='ROOT'))ORDER BY u.user_name ");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        try {
            // commented for DB2
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect)
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
          
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                pstmt.setTimestamp(i,BTSLUtil.getTimestampFromUtilDate(currentDate));
            ++i;
            }else{
                pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
                ++i;
            }
            pstmt.setString(i, p_userName);
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_domainID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    channelVO.setUserBalance("0");
                    LOG.errorTrace(methodName, e);
                }
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByDomainID]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByDomainID]", "", "", "",
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * method loadUsersChnlBypassByDomainID
     * This method load all the users of the specified category which are not
     * the direct child of the owner.
     * This will be called to download users list at domain level and have
     * channel by pass T/R/W allowed
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_domainID
     * @param p_userName
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     *             ArrayList
     */
    public LinkedHashMap loadUsersChnlBypassByDomainID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_domainID, String p_userName, String p_userID, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUsersChnlBypassByDomainID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + "p_domainID: " + p_domainID + " User Name: " + p_userName + ", p_userID=" + p_userID);
        }
        // commented for DB2
        // OraclePreparedStatement pstmt = null
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg,ub.product_code ");
        strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id ,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
        strBuff.append(" AND u.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // query is changed to optimization and to remove the problem as owner
        // was not coming in the list.
        // and login user is also coming in the list
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND u.parent_id NOT IN  ( ");
        strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
        strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ");
        strBuff.append(" ORDER BY u.user_name ");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        try {
            // commented for DB2
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect)
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            // pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR)
            // commented for DB2
            ++i;
            pstmt.setString(i, p_userName);
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_domainID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    channelVO.setUserBalance("0");
                    LOG.errorTrace(methodName, e);
                }
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersChnlBypassByDomainID]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersChnlBypassByDomainID]", "",
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * loadUsersByParentID
     * This method loads all the users which are the direct child of the parent
     * users which is passed as the argument.
     * 
     * @author sandeep.goel
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_parentID
     * @param p_userName
     * @param p_userID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LinkedHashMap loadUsersByParentID(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUsersByParentID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + "p_parentID: " + p_parentID + " User Name: " + p_userName + " ,p_userID=" + p_userID);
        }
        // OraclePreparedStatement pstmt = null;//commented for DB2
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        final StringBuilder strBuff = new StringBuilder(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ,ub.product_code");
        strBuff.append(" FROM users u left join USER_BALANCES ub on U.user_id=ub.user_id ,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status IN (" + statusAllowed + ")  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND  CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
        strBuff.append(" AND u.user_type='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        // here user_id != ? check is for not to load the sender user in the
        // query for the same level transactions
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND ( u.parent_id = ? OR u.user_id = ?) ORDER BY u.user_name ");
        // here u.parent_id = ? OR u.user_id = ? check is to load the parent
        // also if transaciton is done only to parent
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        try {
            // pstmt = (OraclePreparedStatement)
            // p_con.prepareStatement(sqlSelect);//commented for DB2
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            final Date currentDate = new Date();
            ++i;
            pstmt.setString(i, p_networkCode);
            ++i;
            pstmt.setString(i, p_toCategoryCode);
            ++i;
            pstmt.setString(i, p_userID);
            ++i;
            pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            // pstmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
            ++i;
            pstmt.setString(i, p_userName);
            ++i;
            pstmt.setString(i, p_parentID);
            ++i;
            pstmt.setString(i, p_parentID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"))
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    channelVO.setUserBalance("0");
                    LOG.errorTrace(methodName, e);
                }
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
            }

        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentID]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersByParentID]", "", "", "",
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * loadUserForChannelByPass
     * This method loads all the users under the user which is passed as
     * argument and users which are not direct
     * child of that user.
     * 
     * @author sandeep.goel
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_parentID
     * @param p_userName
     * @param p_userID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public LinkedHashMap loadUserForChannelByPass(Connection p_con, String p_networkCode, String p_toCategoryCode, String p_parentID, String p_userName, String p_userID, String p_txnType) throws BTSLBaseException {

        final String methodName = "loadUserForChannelByPass";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                methodName,
                "Entered  Network Code: " + p_networkCode + " To Category Code: " + p_toCategoryCode + "  p_parentID: " + p_parentID + " User Name: " + p_userName + ",p_userID=" + p_userID);
        }
        // OraclePreparedStatement pstmt = null; //commented for DB2
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // user life cycle
        String statusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_networkCode, p_toCategoryCode, PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType)) {
                statusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
            } else {
                statusAllowed = "'" + (userStatusVO.getUserSenderAllowed()).replaceAll(",", "','") + "'";
            }
        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        
        final LinkedHashMap<String,ArrayList<ChannelUserVO>> linkedHashMap = new LinkedHashMap();
        try {
        	  String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
              boolean tcpOn = false;
              Set<String> uniqueTransProfileId = new HashSet();
              
              if(tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
              	tcpOn = true;
              }
              String sqlSelect = null;
              
              if(tcpOn) {

            	  pstmt = c2cBatchTransferQry.loadUserForChannelByPassTcpQry(statusAllowed, p_con, p_networkCode, p_toCategoryCode, p_parentID, p_userName, p_userID, p_txnType);
              }else {
            	  pstmt = c2cBatchTransferQry.loadUserForChannelByPassQry(statusAllowed, p_con, p_networkCode, p_toCategoryCode, p_parentID, p_userName, p_userID, p_txnType);	            
              }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ChannelUserVO channelVO = new ChannelUserVO();
                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setUserName(rs.getString("user_name"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                // channelVO.setUserBalance(rs.getString("balance"));
                try {
                    channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
                } catch (Exception e) {
                    channelVO.setUserBalance("0");
                    LOG.errorTrace(methodName, e);
                }
                channelVO.setExternalCode(rs.getString("external_code"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                
                if(tcpOn) {
                	uniqueTransProfileId.add(rs.getString("transfer_profile_id"));
                }
                
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                
                if(!tcpOn) {
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                }
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                
                if(!tcpOn) {
                	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                }
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                channelVO.setProductCode(rs.getString("product_code"));
                if(linkedHashMap.containsKey(channelVO.getUserID()))
                {
                	linkedHashMap.get(channelVO.getUserID()).add(channelVO);
                }
                else{
                	final ArrayList<ChannelUserVO> channelUserVO = new ArrayList<>();
                	channelUserVO.add(channelVO);
                	linkedHashMap.put(channelVO.getUserID(), channelUserVO);
                }
                
                if (tcpOn) {
    				SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, uniqueTransProfileId,
    						ValueType.STRING);
    				BTSLUtil.updateMapViaMicroServiceResultSet(linkedHashMap, BTSLUtil
    						.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","profile_Name","status")), searchCriteria));
    			}
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserForChannelByPass]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            LOG.error(methodName, "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUserForChannelByPass]", "", "", "",
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
                LOG.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;
    }

    /**
     * isPendingC2CTransactionExist
     * This method is to check that the user has any pending request of C2C batch transfer
     * or not
     * 
     * @param con
     * @param userID
     * @return
     * @throws BTSLBaseException
     *             boolean
     */
    public boolean isPendingC2CTransactionExist(Connection con, String userID) throws BTSLBaseException {
        final String methodName = "isPendingC2CTransactionExist";
        if (LOG.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_userID ");
        	loggerValue.append(userID);
        	LOG.debug(methodName, loggerValue);
        }
        boolean isExist = false;
        
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append(" SELECT 1  ");
            strBuff.append(" FROM c2c_batch_items ");
            strBuff.append(" WHERE user_id=? AND ");
            strBuff.append(" (status <> ? AND status <> ? )");
            final String sqlSelect = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
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
        	LOG.error(methodName, sqlException + sqe);
        	LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isPendingTransactionExist]", "",
                "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, "", errorGeneralSqlProcessing);
        } catch (Exception ex) {
        	LOG.error(methodName, exception + ex);
        	LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isPendingTransactionExist]", "",
                "", "", exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
        
            if (LOG.isDebugEnabled()) {
            	LOG.debug(methodName, "Exiting:  isExist=" + isExist);
            }
        }
        return isExist;
    }

	/**This method load Batch details according to batch id.
	 *  loadBatchDetailsList
	 * @param p_con Connection
	 * @param p_batchId String
	 * @return ArrayList list
	 * @throws BTSLBaseException
	 * ved.sharma
	 */
	public ArrayList loadBatchDetailsList(Connection p_con,String p_batchId) throws BTSLBaseException
	{
		final String methodName = "loadBatchDetailsList";
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "Entered p_batchId="+p_batchId);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlSelect=c2cBatchTransferQry.loadBatchDetailsListQry();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		C2CBatchMasterVO c2cBatchMasterVO=null;
		C2CBatchItemsVO c2cBatchItemsVO=null;
		ArrayList list = new ArrayList();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_batchId);
			pstmt.setString(2, p_batchId);
			pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
			    c2cBatchMasterVO=new C2CBatchMasterVO();
			    c2cBatchMasterVO.setOptBatchId(rs.getString("opt_batch_id"));
			    c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
			    c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
			    c2cBatchMasterVO.setStatus(rs.getString("status"));
			    c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
			    c2cBatchMasterVO.setDomainCodeDesc(rs.getString("domain_name"));
			    c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
			    c2cBatchMasterVO.setProductCodeDesc(rs.getString("product_name"));
			    c2cBatchMasterVO.setBatchFileName(rs.getString("batch_file_name"));
			    c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
			    c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
			    c2cBatchMasterVO.setCreatedBy(rs.getString("initated_by"));
			    c2cBatchMasterVO.setCreatedOn(rs.getTimestamp("created_on"));
			    c2cBatchMasterVO.setStatus(rs.getString("status"));
			    c2cBatchMasterVO.setStatusDesc(rs.getString("status_desc"));
			    
			    c2cBatchItemsVO = new C2CBatchItemsVO();
			    c2cBatchItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
			    c2cBatchItemsVO.setUserName(rs.getString("user_name"));
			    c2cBatchItemsVO.setExternalCode(rs.getString("external_code"));
			    c2cBatchItemsVO.setMsisdn(rs.getString("msisdn"));
			    c2cBatchItemsVO.setCategoryName(rs.getString("category_name"));			    
			    c2cBatchItemsVO.setCategoryCode(rs.getString("category_code"));			    
			    c2cBatchItemsVO.setStatus(rs.getString("status_item"));			    
			    c2cBatchItemsVO.setUserGradeCode(rs.getString("user_grade_code"));	
			    c2cBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
			    c2cBatchItemsVO.setGradeName(rs.getString("grade_name"));
			    c2cBatchItemsVO.setReferenceNo(rs.getString("reference_no"));			    
			    c2cBatchItemsVO.setTransferDate(rs.getDate("transfer_date"));
			    if(c2cBatchItemsVO.getTransferDate()!=null)
			        c2cBatchItemsVO.setTransferDateStr(BTSLUtil.getDateStringFromDate(c2cBatchItemsVO.getTransferDate()));			    
			    c2cBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));			    
			    c2cBatchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));			    
			    c2cBatchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));			    
			    c2cBatchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));			    
			    c2cBatchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));			    
			    c2cBatchItemsVO.setCommissionType(rs.getString("commission_type"));			    
			    c2cBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));			    
			    c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));			    
			    c2cBatchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));	
			    c2cBatchItemsVO.setApprovedBy(rs.getString("approved_by"));
			    c2cBatchItemsVO.setApprovedOn(rs.getTimestamp("approved_on"));
			    c2cBatchItemsVO.setApproverRemarks(rs.getString("approver_remarks"));
			    
			    c2cBatchMasterVO.setC2cBatchItemsVO(c2cBatchItemsVO);
			    
			    list.add(c2cBatchMasterVO);
			}
		} 
		catch (SQLException sqe)
		{
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[loadBatchDetailsList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[loadBatchDetailsList]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (LOG.isDebugEnabled()) LOG.debug(methodName, "Exiting: loadBatchDetailsList  list.size()=" + list.size());
		}
		return list;
	}
	
	/**
	 * This method will load the batches that are within the geography of user whose userId is passed and batch id basis.
	 * with status(OPEN) also in items table for corresponding master record.
	 * @Connection p_con
	 * @String p_goeDomain
	 * @String p_domain
	 * @String p_productCode
	 * @String p_batchid
	 * @String p_msisdn
	 * @Date p_fromDate
	 * @Date p_toDate
	 * @param pLOGinID TODO
	 * @throws  BTSLBaseException
	
	 */
    public ArrayList loadBatchC2CMasterDetails(Connection p_con,String p_goeDomain,String p_domain,String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String pLOGinID,String p_categoryCode,String pLOGinCatCode,String p_userName) throws BTSLBaseException
    {
    	final String methodName = "loadBatchC2CMasterDetails";
        if (LOG.isDebugEnabled())           LOG.debug(methodName, "Entered p_goeDomain="+p_goeDomain+" p_domain="+p_domain+" p_productCode="+p_productCode+" p_batchid="+p_batchid+" p_msisdn="+p_msisdn+" p_fromDate="+p_fromDate+" p_toDate="+p_toDate+" pLOGinID="+pLOGinID+", p_categoryCode="+p_categoryCode+", pLOGincatCode="+pLOGinCatCode+", p_userName="+p_userName);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlSelect = c2cBatchTransferQry.loadBatchC2CMasterDetailsQry(p_batchid,pLOGinCatCode,p_categoryCode,p_userName,p_domain);
        if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        ArrayList list = new ArrayList();
        try
        {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            //pstmt.setString(++i, pLOGinID);
            if(p_batchid !=null)
            { 	pstmt.setString(++i, p_batchid);
            	pstmt.setString(++i, p_batchid);
            	pstmt.setString(++i, pLOGinID);
            }
            else
            {
                pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                if(p_categoryCode.equals(pLOGinCatCode)){
                	pstmt.setString(++i, pLOGinID);
                } 
                	
        
                if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil.getSQLDateFromUtilDate(p_toDate)+" BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="+BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            }
            rs = pstmt.executeQuery();
            C2CBatchMasterVO c2cBatchMasterVO = null;
            while (rs.next())
            {
                c2cBatchMasterVO=new C2CBatchMasterVO();
                c2cBatchMasterVO.setOptBatchId(rs.getString("opt_batch_id"));
                c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
                c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
                c2cBatchMasterVO.setProductName(rs.getString("product_name"));
                c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
                c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
                c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
                
                c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
                c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
                c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                if(c2cBatchMasterVO.getBatchDate()!=null)
                                c2cBatchMasterVO.setBatchDateStr(BTSLUtil.getDateStringFromDate(c2cBatchMasterVO.getBatchDate()));
                list.add(c2cBatchMasterVO);
            }
        } 
        catch (SQLException sqe)
        {
        	EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[loadBatchC2CMasterDetails]","","","","SQL Exception:"+sqe.getMessage());
        	throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
        catch (Exception ex)
        {
        	EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[loadBatchC2CMasterDetails]","","","","Exception:"+ex.getMessage());
        	throw new BTSLBaseException(this, methodName, "error.general.processing");
        }
        finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
                        try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
                        if (LOG.isDebugEnabled()) LOG.debug(methodName, "Exiting: c2cBatchMasterVOList size=" + list.size());
        }
        return list;
    }
    
    /**
	 * Method to close the c2c Batch Transfer/Withdraw. This also perform all the data validation.
	 * Also construct error list
	 * Tables updated are: c2c_batches,c2c_batch_items
	 * user_balances,user_daily_balances,user_transfer_counts,c2c_batch_items,c2c_batches,
	 * channel_transfers_items,channel_transfers
	 * 
	 * @param p_con
	 * @param p_dataMap
	 * @param p_senderVO
	 * @param p_batchItemsList
	 * @param p_c2cBatchMatserVO
	 * @param p_messages
	 * @param p_locale
	 * @return
	 * @throws BTSLBaseException
	 */
	
	
	
    public ArrayList closeBatchC2CTransfer(Connection p_con,C2CBatchMasterVO p_batchMasterVO,ChannelUserVO p_senderVO,ArrayList p_batchItemsList,MessageResources p_messages,Locale p_locale,String p_sms_default_lang ,String p_sms_second_lang)throws BTSLBaseException
	{
    	final String methodName = "closeBatchC2CTransfer";
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "Entered.... p_batchMasterVO="+p_batchMasterVO+", p_batchItemsList.size() = "+p_batchItemsList.size()+", p_batchItemsList="+p_batchItemsList + "p_locale="+ p_locale);
		
		PreparedStatement pstmtLoadUser = null;
		PreparedStatement pstmtSelectUserBalances=null;
		PreparedStatement pstmtUpdateUserBalances=null;
		PreparedStatement pstmtUpdateSenderBalanceOn=null;
		
		PreparedStatement pstmtInsertUserDailyBalances=null;
		
		PreparedStatement pstmtSelectSenderBalance=null;
		PreparedStatement pstmtUpdateSenderBalance=null;
		PreparedStatement pstmtInsertSenderDailyBalances=null;
		
		PreparedStatement pstmtSelectBalance=null;
		
		PreparedStatement pstmtUpdateBalance=null;
		PreparedStatement pstmtInsertBalance=null;
		PreparedStatement pstmtSelectTransferCounts=null;
		
		PreparedStatement pstmtSelectSenderTransferCounts=null;
		PreparedStatement pstmtSelectProfileCounts=null;
		PreparedStatement pstmtUpdateTransferCounts=null;
		PreparedStatement pstmtSelectSenderProfileOutCounts=null;
		PreparedStatement pstmtUpdateSenderTransferCounts=null;
		PreparedStatement pstmtInsertTransferCounts=null;
		PreparedStatement pstmtInsertSenderTransferCounts=null;
		
		PreparedStatement pstmtLoadTransferProfileProduct=null;
		PreparedStatement handlerStmt = null;
		
		PreparedStatement pstmtInsertIntoChannelTransferItems=null;
		//OraclePreparedStatement pstmtInsertIntoChannelTranfers=null;//commented for DB2
		PreparedStatement pstmtInsertIntoChannelTranfers=null;
		PreparedStatement pstmtSelectBalanceInfoForMessage=null;


		ResultSet rs = null;
		ArrayList errorList = new ArrayList();
		ListValueVO errorVO=null;
		ArrayList userbalanceList=null;
		UserBalancesVO balancesVO = null;
		KeyArgumentVO keyArgumentVO=null;
		String[] argsArr=null;
		ArrayList txnSmsMessageList=null;
		ArrayList balSmsMessageList=null;
		Locale locale=null;
		String[] array=null;
		BTSLMessages messages=null;
		PushMessage pushMessage=null;
		String language=null;
		String country=null;
		String c2cTransferID=null;
		int updateCount=0;
		//added by vikram
		long senderPreviousBal=-1;			//taking sender previous balance as 0
		
		// for loading the C2C transfer rule for C2C transfer
		PreparedStatement pstmtSelectTrfRule = null;
		ResultSet rsSelectTrfRule=null;
		PreparedStatement psmtInsertUserThreshold=null;
		//added by vikram
        long thresholdValue=-1;
		
		StringBuffer strBuffSelectTrfRule = new StringBuffer(" SELECT transfer_rule_id,transfer_type, transfer_allowed ");
		strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ?  ");
		strBuffSelectTrfRule.append("AND to_category = ? AND status = 'Y' AND type = 'CHANNEL' ");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffSelectTrfRule Query ="+strBuffSelectTrfRule);
		// ends here
		
		// for loading the products associated with the transfer rule
		PreparedStatement pstmtSelectTrfRuleProd = null;
		ResultSet rsSelectTrfRuleProd=null;
		StringBuffer strBuffSelectTrfRuleProd = new StringBuffer("SELECT 1 FROM chnl_transfer_rules_products ");
		strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffSelectTrfRuleProd Query ="+strBuffSelectTrfRuleProd);
		//ends here

		// for loading the products associated with the commission profile
		PreparedStatement pstmtSelectCProfileProd = null;
		ResultSet rsSelectCProfileProd=null;
		StringBuffer strBuffSelectCProfileProd = new StringBuffer("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
		strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable,cp.taxes_on_channel_transfer  ");
		strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
		strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffSelectCProfileProd Query ="+strBuffSelectCProfileProd);
		
		PreparedStatement pstmtSelectCProfileProdDetail = null;
		ResultSet rsSelectCProfileProdDetail=null;
		StringBuffer strBuffSelectCProfileProdDetail = new StringBuffer("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
		strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
		strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
		strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffSelectCProfileProdDetail Query ="+strBuffSelectCProfileProdDetail);
		//ends here

		// for existance of the product in the transfer profile
		PreparedStatement pstmtSelectTProfileProd = null;
		ResultSet rsSelectTProfileProd=null;
		StringBuffer strBuffSelectTProfileProd = new StringBuffer(" SELECT 1 ");
		strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
		strBuffSelectTProfileProd.append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffSelectTProfileProd Query ="+strBuffSelectTProfileProd);
		//ends here

		// insert data in the batch master table
		//commented for DB2 
		//OraclePreparedStatement pstmtInsertBatchMaster = null;
		PreparedStatement pstmtInsertBatchMaster = null;
		StringBuffer strBuffInsertBatchMaster = new StringBuffer("INSERT INTO c2c_batches (batch_id, network_code, ");
		strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
		strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
		strBuffInsertBatchMaster.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffInsertBatchMaster Query ="+strBuffInsertBatchMaster);
		//ends here
		
		
		
		// insert data in the c2c batch items table
		PreparedStatement pstmtInsertBatchItems = null;
		StringBuffer strBuffInsertBatchItems = new StringBuffer("INSERT INTO c2c_batch_items (batch_id, batch_detail_id, ");
		strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
		strBuffInsertBatchItems.append("transfer_date, txn_profile, ");
		strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
		strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
		strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
		strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,transfer_type,transfer_sub_type) "); 
		strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffInsertBatchItems Query ="+strBuffInsertBatchItems);
		//ends here
		//update master table with OPEN status
		PreparedStatement pstmtUpdateBatchMaster = null;
		StringBuffer strBuffUpdateBatchMaster = new StringBuffer("UPDATE c2c_batches SET batch_total_record=? , status =? WHERE batch_id=?");
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "strBuffUpdateBatchMaster Query ="+strBuffUpdateBatchMaster);
		
		/*The query below will be used to load user datils.
		 * That details is the validated for eg: transfer profile, commission profile, user status etc.
		 */
        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, ");
		sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
		sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug "); 
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND " );
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
		String sqlLoadUser = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
		//The query below is used to load the user balance
		//This table will basically used to update the daily_balance_updated_on and also to know how many
		//records are to be inserted in user_daily_balances table
			
		String selectUserBalances = c2cBatchTransferQry.closeBatchC2CTransferUserBalanceQry();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
		sqlBuffer=null;
		
		//update daily_balance_updated_on with current date for user
		sqlBuffer=new StringBuffer(" UPDATE user_balances SET daily_balance_updated_on = ? ");
		sqlBuffer.append("WHERE user_id = ? ");
		String updateUserBalances = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
		sqlBuffer=null;
		
//		Executed if day difference in last updated date and current date is greater then or equal to 1
		//Insert number of records equal to day difference in last updated date and current date in  user_daily_balances
		sqlBuffer=new StringBuffer(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
		sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
		sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
		sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
		String insertDailyBalances = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY insertUserDailyBalances=" + insertDailyBalances);

//		Select the balance of user for the perticuler product and network.

		String selectBalance = c2cBatchTransferQry.closeBatchC2CTransferBalanceQry();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY selectBalance=" + selectBalance);
		sqlBuffer=null;
		
//		Credit the user balance(If balance found in user_balances)
		sqlBuffer=new StringBuffer(" UPDATE user_balances SET prev_balance = ?, balance = ? , last_transfer_type = ? , "); 
		sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" user_id = ? ");
		sqlBuffer.append(" AND "); 
		sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
		String updateBalance = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY updateBalance=" + updateBalance);
		sqlBuffer=null;
		
//		Insert the record of balnce for user (If balance not found in user_balances)
		sqlBuffer=new StringBuffer(" INSERT "); 
		sqlBuffer.append(" INTO user_balances ");
		sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , "); 
		sqlBuffer.append(" user_id, product_code , network_code, network_code_for ) ");
		sqlBuffer.append(" VALUES ");
		sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?) ");			
		String insertBalance = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY insertBalance=" + insertBalance);
		sqlBuffer=null;
		
//		Select the running countres of user(to be checked against the effetive profile counters)
		sqlBuffer=new StringBuffer(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
		sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
		sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
		sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
		sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
		sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
		sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
		sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
		sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
		sqlBuffer.append(" FROM user_transfer_counts ");
		if(PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
		sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS");
		else
			sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
		String selectTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
		sqlBuffer=null;
		
//		Select the effective profile counters of user to be checked with running counters of user
		StringBuffer strBuff=new StringBuffer();
		strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, "); 
		strBuff.append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
		strBuff.append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
		strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
		strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
		strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
		strBuff.append(" AND tp.category_code=catp.category_code ");	
		strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
		String selectProfileInCounts = strBuff.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY selectProfileInCounts=" + selectProfileInCounts);
		sqlBuffer=null;
		
//		Update the user running countres (If record found for user running counters)
		sqlBuffer=new StringBuffer(" UPDATE user_transfer_counts  SET "); 
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
        String updateTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
		sqlBuffer=null;
		
//		Select the effective profile counters of sender to be checked with running counters of sender added by Gopal
		StringBuffer strBuff1=new StringBuffer();
		strBuff1.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, "); 
		strBuff1.append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value ,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
		strBuff1.append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
		strBuff1.append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value");
		strBuff1.append(" FROM transfer_profile tp,transfer_profile catp ");
		strBuff1.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
		strBuff1.append(" AND tp.category_code=catp.category_code ");	
		strBuff1.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
		String selectProfileOutCounts = strBuff1.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY selectProfileOutCounts=" + selectProfileOutCounts);
		sqlBuffer=null;
		
//		Update the Sender running countres (If record found for user running counters)
		sqlBuffer=new StringBuffer(" UPDATE user_transfer_counts  SET "); 
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
        String updateSenderTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY updateSenderTransferCounts=" + updateSenderTransferCounts);
		sqlBuffer=null;
		
//		Insert the record in user_transfer_counts (If no record found for user running counters)
        sqlBuffer=new StringBuffer(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        String insertTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
		sqlBuffer=null;
		
//		Insert the record in user_transfer_counts for Sender (If no record found for user running counters)
        sqlBuffer=new StringBuffer(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, monthly_out_count, ");
        sqlBuffer.append(" monthly_out_value, last_out_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        String insertSenderTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY insertSenderTransferCounts=" + insertSenderTransferCounts);
		sqlBuffer=null;
		
//		Select the transfer profile product values(These will be used for checking max balance of user)
        sqlBuffer = new StringBuffer("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
        sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id "); 
        sqlBuffer.append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
        String loadTransferProfileProduct = sqlBuffer.toString();
        if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
        sqlBuffer=null;
        
//      The query below is used to insert the record in channel transfer items table for the order that is closed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price, ");
        sqlBuffer.append(" sender_debit_quantity, receiver_credit_quantity, sender_post_stock, receiver_post_stock,COMMISION_QUANTITY,oth_commission_type,oth_commission_rate,oth_commission_value )  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
        sqlBuffer=null;
        
//      The query below is used to insert the record in channel transfers table for the order that is cloaed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers ");
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
		sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id, ");
		sqlBuffer.append(" sender_grade_code,sender_txn_profile,msisdn,oth_comm_prf_set_id ) ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        String insertIntoChannelTransfer = sqlBuffer.toString();
        if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
        sqlBuffer=null;
        
//      The query below is used to get the balance information of user with product.
        //This information will be send in message to user
        sqlBuffer = new StringBuffer(" SELECT UB.product_code,UB.balance, ");
        sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
        sqlBuffer.append(" FROM user_balances UB,products PROD ");
        sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code "); 
        String selectBalanceInfoForMessage = sqlBuffer.toString();
        if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
        sqlBuffer=null;
        
        //added by nilesh : added two new columns threshold_type and remark
        StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , "); 
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");   
        String insertUserThreshold = strBuffThresholdInsert.toString();
        if (LOG.isDebugEnabled())
        {
            LOG.debug(methodName, "QUERY insertUserThreshold=" + insertUserThreshold);
        }
		
		//added by vikram
        
		int totalSuccessRecords=0;
		Date date=null;
		ChannelTransferVO channelTransferVO=null;
		try
		{
			
		
			pstmtSelectTrfRule=p_con.prepareStatement(strBuffSelectTrfRule.toString());
			pstmtSelectTrfRuleProd=p_con.prepareStatement(strBuffSelectTrfRuleProd.toString());
			pstmtSelectCProfileProd=p_con.prepareStatement(strBuffSelectCProfileProd.toString());
			pstmtSelectCProfileProdDetail=p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
			pstmtSelectTProfileProd=p_con.prepareStatement(strBuffSelectTProfileProd.toString());
			
			//pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());//commented for DB2
		    //pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());//commented for DB2
		     pstmtInsertBatchMaster=(PreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());
			pstmtInsertBatchItems=(PreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());
			pstmtUpdateBatchMaster=p_con.prepareStatement(strBuffUpdateBatchMaster.toString());
			
			pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
			pstmtSelectUserBalances=p_con.prepareStatement(selectUserBalances);
			pstmtUpdateUserBalances=p_con.prepareStatement(updateUserBalances);
			pstmtSelectSenderBalance=p_con.prepareStatement(selectUserBalances);
			
			pstmtInsertSenderDailyBalances=p_con.prepareStatement(insertDailyBalances);
			pstmtUpdateSenderBalanceOn=p_con.prepareStatement(updateUserBalances);
			pstmtUpdateSenderBalance=p_con.prepareStatement(updateBalance);
			pstmtInsertUserDailyBalances=p_con.prepareStatement(insertDailyBalances);
			pstmtSelectTransferCounts=p_con.prepareStatement(selectTransferCounts);
			
			pstmtSelectBalance=p_con.prepareStatement(selectBalance);
			
			pstmtUpdateBalance=p_con.prepareStatement(updateBalance);
			pstmtInsertBalance=p_con.prepareStatement(insertBalance);
			
			pstmtSelectSenderTransferCounts=p_con.prepareStatement(selectTransferCounts);
			pstmtSelectProfileCounts=p_con.prepareStatement(selectProfileInCounts);
			pstmtUpdateTransferCounts=p_con.prepareStatement(updateTransferCounts);
			pstmtInsertTransferCounts = p_con.prepareStatement(insertTransferCounts);
			pstmtSelectSenderProfileOutCounts=p_con.prepareStatement(selectProfileOutCounts);
			pstmtUpdateSenderTransferCounts=p_con.prepareStatement(updateSenderTransferCounts);
			pstmtInsertSenderTransferCounts=p_con.prepareStatement(insertSenderTransferCounts);
			//pstmtUpdateTransferCounts=p_con.prepareStatement(updateTransferCounts);


			
			pstmtLoadTransferProfileProduct=p_con.prepareStatement(loadTransferProfileProduct);
			
			pstmtInsertIntoChannelTransferItems=p_con.prepareStatement(insertIntoChannelTransferItem);
			//pstmtInsertIntoChannelTranfers=(OraclePreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);//commented for DB2 
			pstmtInsertIntoChannelTranfers=(PreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);
			pstmtSelectBalanceInfoForMessage=p_con.prepareStatement(selectBalanceInfoForMessage);
			psmtInsertUserThreshold=p_con.prepareStatement(insertUserThreshold);

			
	//		pstmtUpdateSenderBalanceOn=p_con.prepareStatement(updateUserBalances);
			ChannelTransferRuleVO rulesVO = null;
			ArrayList channelTransferItemVOList=null;
			int index = 0;
			C2CBatchItemsVO  batchItemsVO = null;
			
			HashMap transferRuleMap = new HashMap();
			HashMap transferRuleNotExistMap = new HashMap();
			HashMap transferRuleProdNotExistMap = new HashMap();
			HashMap transferProfileMap = new HashMap();
			long requestedValue=0;
			long minTrfValue=0;
			long maxTrfValue=0;
			long multipleOf=0;
			ArrayList transferItemsList = null;
			MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
			ChannelTransferItemsVO channelTransferItemsVO = null;
			int m=0;
			String network_id=null;
			TransferProfileProductVO transferProfileProductVO=null;
			ChannelUserVO channelUserVO=null;
			//ChannelTransferVO channelTransferVO=null;
			//ChannelTransferItemsVO channelTransferItemVO=null;
			TransferProfileVO transferProfileVO=null;
   			TransferProfileVO senderTfrProfileCheckVO=null;
			date=new Date();
			int dayDifference=0;
			Date dailyBalanceUpdatedOn=null;
			int k=0;
			boolean terminateProcessing=false;
			long maxBalance=0;
			boolean isNotToExecuteQuery = false;
			long balance = -1;
			long senderBalance=-1;
            long previousUserBalToBeSetChnlTrfItems=-1;
            long previousSenderBalToBeSetChnlTrfItems=-1;
            UserTransferCountsVO countsVO = null;
            UserTransferCountsVO senderCountsVO=null;
            boolean flag = true;
         	// insert the master data
			index=0;
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getNetworkCode());
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getNetworkCodeFor());
			
			//pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchName());
			
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getStatus());
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getDomainCode());
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getProductCode());
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchFileName());
			pstmtInsertBatchMaster.setLong(++index,p_batchMasterVO.getBatchTotalRecord());
			pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getCreatedBy());
			pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getModifiedBy());
			pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));
			
			//pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getDefaultLang());
			//pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getSecondLang());
			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getUserId());
			
			int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
			if(queryExecutionCount<=0)
			{
			    p_con.rollback();
			    LOG.error(methodName,"Unable to insert in the batch master table.");
				BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error Unable to insert in the batch master table","queryExecutionCount="+queryExecutionCount);
			    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[closeBatchC2CTransfer]","","","","Unable to insert in the batch master table.");
			    throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
			}
			//ends here
			
			String msgArr[]=null;
			for(int i=0,j=p_batchItemsList.size();i<j;i++)
			{
				terminateProcessing=false;
				batchItemsVO=(C2CBatchItemsVO) p_batchItemsList.get(i);
				// check the uniqueness of the external txn number
				

				// load the product's informaiton.
				if(transferRuleNotExistMap.get(batchItemsVO.getCategoryCode())==null)
				{
					if(transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode())==null)
					{
						if(transferRuleMap.get(batchItemsVO.getCategoryCode())==null)
						{
							index=0;
							pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getNetworkCode());
							pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getDomainCode());
							pstmtSelectTrfRule.setString(++index,batchItemsVO.getCategoryCode());
							rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
							pstmtSelectTrfRule.clearParameters();
							if (rsSelectTrfRule.next())
							{
								rulesVO = new ChannelTransferRuleVO();
								rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
								rulesVO.setTransferType(rsSelectTrfRule.getString("transfer_type"));
								rulesVO.setTransferAllowed(rsSelectTrfRule.getString("transfer_allowed"));
								index=0;
								pstmtSelectTrfRuleProd.setString(++index,rulesVO.getTransferRuleID());
								pstmtSelectTrfRuleProd.setString(++index,p_batchMasterVO.getProductCode());
								rsSelectTrfRuleProd  = pstmtSelectTrfRuleProd.executeQuery();
								pstmtSelectTrfRuleProd.clearParameters();
								if(!rsSelectTrfRuleProd.next())
								{
									transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
									//put error log Prodcuct is not in the transfer rule
									errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
									errorList.add(errorVO);
									BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Product is not in the transfer rule","");
									continue;
								}
								transferRuleMap.put(batchItemsVO.getCategoryCode(),rulesVO );
							}
							else
							{
								transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
								// put error log transfer rule not defined
							    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer rule not defined","");
								continue;
							}
						}// transfer rule loading
					}// Procuct is not associated with transfer rule not defined check
					else
					{
						//put error log Procuct is not in the transfer rule
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Product is not in the transfer rule","");
						continue;
					}
				}// transfer rule not defined check
				else
				{
					// put error log transfer rule not defined
				    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer rule not defined","");
					continue;
				}
				rulesVO=(ChannelTransferRuleVO)transferRuleMap.get(batchItemsVO.getCategoryCode());
				if(PretupsI.NO.equals(rulesVO.getTransferAllowed()))
	            {
					//put error according to the transfer rule C2C transfer is not allowed.
				    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.c2cnotallowed"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : According to the transfer rule C2C transfer is not allowed","");
					continue;
				}
				// check the transfer profile product code
				// transfer profile check ends here
				if(transferProfileMap.get(batchItemsVO.getTxnProfile())==null)
				{
					index=0;
					pstmtSelectTProfileProd.setString(++index,batchItemsVO.getTxnProfile());
					pstmtSelectTProfileProd.setString(++index,p_batchMasterVO.getProductCode());
					pstmtSelectTProfileProd.setString(++index,PretupsI.PARENT_PROFILE_ID_CATEGORY);
					rsSelectTProfileProd=pstmtSelectTProfileProd.executeQuery();
					pstmtSelectTProfileProd.clearParameters();
					if(!rsSelectTProfileProd.next())
					{
						transferProfileMap.put(batchItemsVO.getTxnProfile(),"false");
						//put error Transfer profile for this product is not define
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile for this product is not defined","");
						continue;
					}
					transferProfileMap.put(batchItemsVO.getTxnProfile(),"true");
				}
				else
				{
					
					if("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile())))
					{
						// put error Transfer profile for this product is not define
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile for this product is not defined","");
						continue;
					}
				}
				
				// check the commisson profile applicability and other checks related to the commission profile
				index=0;
				pstmtSelectCProfileProd.setString(++index,p_batchMasterVO.getProductCode());
				pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileSetId());
				pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileVer());
				rsSelectCProfileProd=pstmtSelectCProfileProd.executeQuery();
				pstmtSelectCProfileProd.clearParameters();
				if(!rsSelectCProfileProd.next())
				{
					// put error commission profile for this product is not defined
				    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.commprfnotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Commission profile for this product is not defined","");
					continue;
				}
				requestedValue=batchItemsVO.getRequestedQuantity();
				minTrfValue=rsSelectCProfileProd.getLong("min_transfer_value");
				maxTrfValue=rsSelectCProfileProd.getLong("max_transfer_value");
				if(minTrfValue > requestedValue || maxTrfValue < requestedValue )
				{
					msgArr=new String[3];
					msgArr[0]=PretupsBL.getDisplayAmount(requestedValue);
					msgArr[1]=PretupsBL.getDisplayAmount(minTrfValue);
					msgArr[2]=PretupsBL.getDisplayAmount(maxTrfValue);
					// put error requested quantity is not between min and max values
				    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.qtymaxmin",msgArr));
				    msgArr=null;
				    errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Requested quantity is not between min and max values","minTrfValue="+minTrfValue+", maxTrfValue="+maxTrfValue);
					continue;
				}
				multipleOf=rsSelectCProfileProd.getLong("transfer_multiple_off");
				if(requestedValue%multipleOf != 0)
				{
					// put error requested quantity is not multiple of
				    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.notmulof",new String[]{PretupsBL.getDisplayAmount(multipleOf)}));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Requested quantity is not in multiple value","multiple of="+multipleOf);
					continue;
				}
				
				index=0;
				pstmtSelectCProfileProdDetail.setString(++index,rsSelectCProfileProd.getString("comm_profile_products_id"));
				pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
				pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
				rsSelectCProfileProdDetail=pstmtSelectCProfileProdDetail.executeQuery();
				pstmtSelectCProfileProdDetail.clearParameters();
				if(!rsSelectCProfileProdDetail.next())
				{
					// put error commission profile slab is not define for the requested value
				    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.commslabnotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Commission profile slab is not define for the requested value","");
					continue;
				}	
				 // to calculate tax
				transferItemsList = new ArrayList();
				channelTransferItemsVO = new ChannelTransferItemsVO ();
				// this value will be inserted into the table as the requested qty
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
//				if(PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable")))
//				{					
//					channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.YES);
//				}
//				else
//					channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.NO);
				//added by vikram
				if(PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_channel_transfer")))
				{					
					channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.YES);
				}
				else
					channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.NO);
				transferItemsList.add(channelTransferItemsVO);
                
                channelTransferVO=new ChannelTransferVO();
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                //channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setTransferSubType(batchItemsVO.getTransferSubType());
                //ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_FOC);
                if(((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
				if(!BTSLUtil.isNullString(batchItemsVO.getMsisdn()))
                	channelTransferVO.setToUserMsisdn(batchItemsVO.getMsisdn());
                else if(!BTSLUtil.isNullString(batchItemsVO.getLoginID()))
                	channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChannelUserDetailsByLoginIDANDORMSISDN(p_con,"",batchItemsVO.getLoginID())).getMsisdn());
                else if(!BTSLUtil.isNullString(batchItemsVO.getExternalCode()))
                	channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChnlUserDetailsByExtCode(p_con, batchItemsVO.getExternalCode())).getMsisdn());
				 channelTransferVO.setCommProfileSetId(batchItemsVO.getCommissionProfileSetId());
    	         channelTransferVO.setCommProfileVersion(batchItemsVO.getCommissionProfileVer()); 
    	         if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null)
 				{
 					channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
 					channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
 				}
				}
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_C2C);
				// taxes on C2C required
				// ends commission profile validaiton
				
				pstmtLoadUser.clearParameters();
				m=0;
				pstmtLoadUser.setString(++m,batchItemsVO.getUserId());
				rs=pstmtLoadUser.executeQuery();
                //(record found for user i.e. receiver) if this condition is not true then made entry in logs and leave this data.
				if(rs.next())
				{
					channelUserVO = new ChannelUserVO();
	                channelUserVO.setUserID(batchItemsVO.getUserId());
	                channelUserVO.setStatus(rs.getString("userstatus"));
	                channelUserVO.setInSuspend(rs.getString("in_suspend"));
	                channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	                channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	                channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	                channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
	                language=rs.getString("phone_language");
	                country=rs.getString("country");
	                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
	                try{if (rs != null){rs.close();}} catch (Exception e){}
	                //(user status is checked) if this condition is true then made entry in logs and leave this data.
	       		 	if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus()))
	       		 	{
	       		 		p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.usersuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,null,batchItemsVO,"FAIL : User is suspend","Approval level");
						continue;
	       		 	}
	                //(commission profile status is checked) if this condition is true then made entry in logs and leave this data.
	       		 	else if(!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.comprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,null,batchItemsVO,"FAIL : Commission profile suspend","Approval level");
						continue;
	                }
	                //(transfer profile is checked) if this condition is true then made entry in logs and leave this data.
	                else if(!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.trfprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,null,batchItemsVO,"FAIL : Transfer profile suspend","Approval level");
						continue;
	                }
	                //(user in suspend  is checked) if this condition is true then made entry in logs and leave this data.
	                else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.userinsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,null,batchItemsVO,"FAIL : User is IN suspend","Approval level");
						continue;
	                }
				}
                //(no record found for user i.e. receiver) if this condition is true then make entry in logs and leave this data.
				else
				{
					p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.nouser"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,null,batchItemsVO,"FAIL : User not found","Approval level");
					continue;
				}
				
			    // creating the channelTransferVO here since C2CTransferID will be required into the network stock
				// transaction table. Other information will be set into this VO later
				// seting the current value for generation of the transfer ID. This will be over write by the
				// bacth c2c items was created.
				channelTransferVO.setCreatedOn(date);
	    		channelTransferVO.setNetworkCode(p_batchMasterVO.getNetworkCode());
	    		channelTransferVO.setNetworkCodeFor(p_batchMasterVO.getNetworkCodeFor());

	    		if(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(batchItemsVO.getTransferSubType()))
	    		    ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);
	    		else if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
	    		    ChannelTransferBL.genrateChnnlToChnnlWithdrawID(channelTransferVO);
	    		
				c2cTransferID=channelTransferVO.getTransferID();
				// value is over writing since in the channel trasnfer table created on should be same as when the
				// batch c2c item was created.
				channelTransferVO.setCreatedOn(batchItemsVO.getInitiatedOn());
				
				dayDifference=0;
				
                dailyBalanceUpdatedOn=null;
				dayDifference=0;
				
				
				pstmtSelectSenderBalance.clearParameters();
				m=0;
				pstmtSelectSenderBalance.setString(++m,p_senderVO.getUserID());
				pstmtSelectSenderBalance.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
				rs=null;
				rs=pstmtSelectSenderBalance.executeQuery();
				while(rs.next())
				{
					dailyBalanceUpdatedOn=rs.getDate("daily_balance_updated_on");
					senderPreviousBal=rs.getLong("balance");
					//if record exist check updated on date with current date
					//day differences to maintain the record of previous days.
					dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn,date);
					if(dayDifference>0)
					{
						//if dates are not equal get the day differencts and execute insert qurery no of times of the 
						if(LOG.isDebugEnabled())
							LOG.debug("closeBatchC2CTransfer ","Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
						
						for(k=0;k<dayDifference;k++)
						{
							pstmtInsertSenderDailyBalances.clearParameters();
							m=0;
							pstmtInsertSenderDailyBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn,k)));
							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("user_id"));
							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("network_code"));

							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("network_code_for"));
							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("product_code"));
							pstmtInsertSenderDailyBalances.setLong(++m,rs.getLong("balance"));
							pstmtInsertSenderDailyBalances.setLong(++m,rs.getLong("prev_balance"));
							pstmtInsertSenderDailyBalances.setString(++m,batchItemsVO.getTransferType());
							pstmtInsertSenderDailyBalances.setString(++m,channelTransferVO.getTransferID());
							pstmtInsertSenderDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertSenderDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertSenderDailyBalances.setString(++m,PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
							updateCount=pstmtInsertSenderDailyBalances.executeUpdate();
							
							if (updateCount <= 0)
							{
								p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting user daily balances table","Approval level = "+"No Approval required"+", updateCount ="+updateCount);
								terminateProcessing=true;
								break;
							}
						}//end of for loop
						if(terminateProcessing)
						{
							BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Terminting the procssing of this user as error while updation daily balance","No Approval required");
							continue;
						}
						//Update the user balances table
						pstmtUpdateSenderBalanceOn.clearParameters();
						m=0;
						pstmtUpdateSenderBalanceOn.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						pstmtUpdateSenderBalanceOn.setString(++m,p_senderVO.getUserID());
						updateCount=pstmtUpdateSenderBalanceOn.executeUpdate();
		                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
						if (updateCount <= 0)
						{
							p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while updating user balances table for daily balance","Approval level = "+"No Approval required"+", updateCount="+updateCount);
							continue;
						}
					}
				}//end of if condition
				try{if (rs != null){rs.close();}} catch (Exception e){}
				maxBalance=0;
				isNotToExecuteQuery = false;
				
				
				
				
				//select the record form the userBalances table.
				pstmtSelectUserBalances.clearParameters();
				m=0;
				pstmtSelectUserBalances.setString(++m,channelUserVO.getUserID());
				pstmtSelectUserBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(date));
				rs=null;
				rs=pstmtSelectUserBalances.executeQuery();
				while(rs.next())
				{
					dailyBalanceUpdatedOn=rs.getDate("daily_balance_updated_on");
					//if record exist check updated on date with current date
					//day differences to maintain the record of previous days.
					dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn,date);
					if(dayDifference>0)
					{
						//if dates are not equal get the day differencts and execute insert qurery no of times of the 
						if(LOG.isDebugEnabled())
							LOG.debug("closeBatchC2CTransfer ","Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
						
						for(k=0;k<dayDifference;k++)
						{
							pstmtInsertUserDailyBalances.clearParameters();
							m=0;
							pstmtInsertUserDailyBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn,k)));
							pstmtInsertUserDailyBalances.setString(++m,rs.getString("user_id"));
							pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code"));

							pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code_for"));
							pstmtInsertUserDailyBalances.setString(++m,rs.getString("product_code"));
							pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("balance"));
							pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("prev_balance"));
							//pstmtInsertUserDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
							pstmtInsertUserDailyBalances.setString(++m,batchItemsVO.getTransferType());
							pstmtInsertUserDailyBalances.setString(++m,channelTransferVO.getTransferID());
							pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertUserDailyBalances.setString(++m,PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
							updateCount=pstmtInsertUserDailyBalances.executeUpdate();
							
							if (updateCount <= 0)
							{
								p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting user daily balances table","Approval level = "+"No Approval required"+", updateCount ="+updateCount);
								terminateProcessing=true;
								break;
							}							
						}//end of for loop
						if(terminateProcessing)
						{
							BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Terminting the procssing of this user as error while updation daily balance","Approval level = "+"No Approval required");
							continue;
						}
						//Update the user balances table
						pstmtUpdateUserBalances.clearParameters();
						m=0;
						pstmtUpdateUserBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						pstmtUpdateUserBalances.setString(++m,channelUserVO.getUserID());
						updateCount=pstmtUpdateUserBalances.executeUpdate();
		                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
						if (updateCount <= 0)
						{
							p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while updating user balances table for daily balance","Approval level = "+"No Approval required"+", updateCount="+updateCount);
							continue;
						}
					}
				}//end of if condition
				try{if (rs != null){rs.close();}} catch (Exception e){}
				maxBalance=0;
				isNotToExecuteQuery = false;
				// sender balance to be debited
				pstmtSelectBalance.clearParameters();
				m=0;
				pstmtSelectBalance.setString(++m,p_senderVO.getUserID());
				pstmtSelectBalance.setString(++m,p_batchMasterVO.getProductCode());
				pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCode());
				pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCodeFor());
                rs=null;
                rs = pstmtSelectBalance.executeQuery();
                senderBalance = -1;
                previousSenderBalToBeSetChnlTrfItems=-1;
                if(rs.next())
                {
                    senderBalance = rs.getLong("balance");
                    try{if (rs != null){rs.close();}} catch (Exception e){}
                }
                else
                {
                	p_con.rollback();
        		 	errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.sendernobal"));
     				errorList.add(errorVO);
     				BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+"No Approval required");
     				continue;
                }
                
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_batchMasterVO.getTransferType())&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_batchMasterVO.getTransferSubType()))
            	{
            		previousSenderBalToBeSetChnlTrfItems=senderBalance;
            		senderBalance += batchItemsVO.getRequestedQuantity();
            	}
                else if((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferType()))&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
          		{
            		if(senderBalance==0 ||  senderBalance - batchItemsVO.getRequestedQuantity() < 0 )
            		{
            			p_con.rollback();
            			errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.senderbalzero"));
            			errorList.add(errorVO);
            			BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+"No Approval required");
            			continue;
            		}
            		else if(senderBalance != 0 && ( senderBalance - batchItemsVO.getRequestedQuantity() >= 0 ))
            		{
            			previousSenderBalToBeSetChnlTrfItems=senderBalance;
            			senderBalance -= batchItemsVO.getRequestedQuantity();}
            		else 
            			previousSenderBalToBeSetChnlTrfItems=0;
          		}
            	m = 0;
                //update   sender balance 
                if(senderBalance > -1)
                {
                	pstmtUpdateSenderBalance.clearParameters();
                	handlerStmt = pstmtUpdateSenderBalance;
                }
                handlerStmt.setLong(++m,previousSenderBalToBeSetChnlTrfItems);
                handlerStmt.setLong(++m,senderBalance);
                handlerStmt.setString(++m,batchItemsVO.getTransferType());
                handlerStmt.setString(++m,c2cTransferID);
                handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
                handlerStmt.setString(++m,p_senderVO.getUserID());
                handlerStmt.setString(++m,p_batchMasterVO.getProductCode());
                handlerStmt.setString(++m,p_batchMasterVO.getNetworkCode());
                handlerStmt.setString(++m,p_batchMasterVO.getNetworkCodeFor());
                updateCount = handlerStmt.executeUpdate();
                handlerStmt.clearParameters();
                if(updateCount <= 0 )
                {
                	p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while credit uer balance","Approval level = "+"No Approval required");
					continue;
                }
               
               
                // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_batchMasterVO.getNetworkCode(), p_senderVO.getCategoryCode()); //threshold value
              //for zero balance counter..
                try
                {
                    m=0;
                    boolean isUserThresholdEntryReq=false;
                    String thresholdType=null;
                   
                    //added by nilesh 
	                transferProfileProductVO =TransferProfileProductCache.getTransferProfileDetails(p_senderVO.getTransferProfileID(),p_batchMasterVO.getProductCode());
	                String remark=null;
	                String threshold_type=null;
                    if(senderBalance<=transferProfileProductVO.getAltBalanceLong() && senderBalance>transferProfileProductVO.getMinResidualBalanceAsLong())
	                {
                    	//isUserThresholdEntryReq=true;
	                	thresholdValue=transferProfileProductVO.getAltBalanceLong();
	                	threshold_type=PretupsI.THRESHOLD_TYPE_ALERT;
	                }
                    else if(senderBalance<=transferProfileProductVO.getMinResidualBalanceAsLong())
                    {
                    	//isUserThresholdEntryReq=true;
                    	thresholdValue=transferProfileProductVO.getMinResidualBalanceAsLong();
	                	threshold_type=PretupsI.THRESHOLD_TYPE_MIN;
                    }
                    //new
                    if(previousSenderBalToBeSetChnlTrfItems>=thresholdValue && senderBalance <=thresholdValue)
                    {
                        isUserThresholdEntryReq=true;
                        thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                    }
                    else if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue && senderBalance >=thresholdValue)
                    {
                        isUserThresholdEntryReq=true;
                        thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
                    }
                    else if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue && senderBalance <=thresholdValue)
                    {
                        isUserThresholdEntryReq=true;
                        thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                    }
                    //end
                    
                    if(isUserThresholdEntryReq)
                    {
                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug(methodName, "Entry in threshold counter" + thresholdValue+ ", prvbal: "+previousSenderBalToBeSetChnlTrfItems+ "nbal"+ senderBalance);
                        }
                        psmtInsertUserThreshold.clearParameters();
                        m=0;
                        psmtInsertUserThreshold.setString(++m, p_senderVO.getUserID());
                        psmtInsertUserThreshold.setString(++m, c2cTransferID);
                        psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getNetworkCode());
                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getProductCode());
                        //psmtInsertUserThreshold.setLong(++m, p_userBalancesVO.getUnitValue());
                        psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TYPE_C2C);
                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getTransferType());
                        psmtInsertUserThreshold.setString(++m, thresholdType);
                        psmtInsertUserThreshold.setString(++m,p_senderVO.getCategoryCode());
                        psmtInsertUserThreshold.setLong(++m,previousSenderBalToBeSetChnlTrfItems);
                        psmtInsertUserThreshold.setLong(++m, senderBalance);
                        psmtInsertUserThreshold.setLong(++m, thresholdValue);
                        //added by nilesh
                        psmtInsertUserThreshold.setString(++m, threshold_type);
                        psmtInsertUserThreshold.setString(++m, remark);
                        
                        psmtInsertUserThreshold.executeUpdate();
                    }
                }
                catch (SQLException sqle)
                {
                    LOG.error(methodName, "SQLException " + sqle.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[closeBatchC2CTransfer]",c2cTransferID,"",p_batchMasterVO.getNetworkCode(),"Error while updating user_threshold_counter table SQL Exception:"+sqle.getMessage());
                }// end of catch
                
                
                //if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
	           //{    
	            pstmtSelectSenderTransferCounts.clearParameters();
	            m=0;
                pstmtSelectSenderTransferCounts.setString(++m,p_senderVO.getUserID());	
                rs=null;
                rs = pstmtSelectSenderTransferCounts.executeQuery(); 
//              get the Sender transfer counts
                senderCountsVO=null;    
                if (rs.next())
                {
                	senderCountsVO = new UserTransferCountsVO();
                	senderCountsVO.setUserID(p_senderVO.getUserID() );
                    
                	senderCountsVO.setDailyInCount( rs.getLong("daily_in_count") );
                	senderCountsVO.setDailyInValue( rs.getLong("daily_in_value") );
                	senderCountsVO.setWeeklyInCount( rs.getLong("weekly_in_count") );
                	senderCountsVO.setWeeklyInValue( rs.getLong("weekly_in_value") );
                	senderCountsVO.setMonthlyInCount( rs.getLong("monthly_in_count") );
                	senderCountsVO.setMonthlyInValue( rs.getLong("monthly_in_value") );
	                
                	senderCountsVO.setDailyOutCount( rs.getLong("daily_out_count") );
                	senderCountsVO.setDailyOutValue( rs.getLong("daily_out_value") );
                	senderCountsVO.setWeeklyOutCount( rs.getLong("weekly_out_count") );
                	senderCountsVO.setWeeklyOutValue( rs.getLong("weekly_out_value") );
                	senderCountsVO.setMonthlyOutCount( rs.getLong("monthly_out_count") );
                	senderCountsVO.setMonthlyOutValue( rs.getLong("monthly_out_value") );				
	                
                	senderCountsVO.setUnctrlDailyInCount( rs.getLong("outside_daily_in_count") );
                	senderCountsVO.setUnctrlDailyInValue( rs.getLong("outside_daily_in_value") );
                	senderCountsVO.setUnctrlWeeklyInCount( rs.getLong("outside_weekly_in_count") );
                	senderCountsVO.setUnctrlWeeklyInValue( rs.getLong("outside_weekly_in_value") );
                	senderCountsVO.setUnctrlMonthlyInCount( rs.getLong("outside_monthly_in_count") );
                	senderCountsVO.setUnctrlMonthlyInValue( rs.getLong("outside_monthly_in_value") );

                	senderCountsVO.setUnctrlDailyOutCount( rs.getLong("outside_daily_out_count") );
                	senderCountsVO.setUnctrlDailyOutValue( rs.getLong("outside_daily_out_value") );
                	senderCountsVO.setUnctrlWeeklyOutCount( rs.getLong("outside_weekly_out_count") );
                	senderCountsVO.setUnctrlWeeklyOutValue( rs.getLong("outside_weekly_out_value") );
                	senderCountsVO.setUnctrlMonthlyOutCount( rs.getLong("outside_monthly_out_count") );
                	senderCountsVO.setUnctrlMonthlyOutValue( rs.getLong("outside_monthly_out_value") );
					
                	senderCountsVO.setDailySubscriberOutCount( rs.getLong("daily_subscriber_out_count") );
                	senderCountsVO.setDailySubscriberOutValue( rs.getLong("daily_subscriber_out_value") );
                	senderCountsVO.setWeeklySubscriberOutCount( rs.getLong("weekly_subscriber_out_count") );
                	senderCountsVO.setWeeklySubscriberOutValue( rs.getLong("weekly_subscriber_out_value") );
                	senderCountsVO.setMonthlySubscriberOutCount( rs.getLong("monthly_subscriber_out_count") );
                	senderCountsVO.setMonthlySubscriberOutValue( rs.getLong("monthly_subscriber_out_value") );
	               
                	senderCountsVO.setLastTransferDate(rs.getDate("last_transfer_date") );
					try{if (rs != null){rs.close();}} catch (Exception e){}
                }
                flag=true;
        		if(senderCountsVO == null)
        		{
        			flag = false;
        			senderCountsVO = new UserTransferCountsVO();
        		}
        		//If found then check for reset otherwise no need to check it
        		if(flag)
        			ChannelTransferBL.checkResetCountersAfterPeriodChange(senderCountsVO,date);
        		
        		pstmtSelectSenderProfileOutCounts.clearParameters();
				m=0;
				pstmtSelectSenderProfileOutCounts.setString(++m,batchItemsVO.getTxnProfile());
				pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.YES);
				pstmtSelectSenderProfileOutCounts.setString(++m,p_batchMasterVO.getNetworkCode());
				pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
				pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.YES);
    			rs=null;
    			rs = pstmtSelectSenderProfileOutCounts.executeQuery();
    			if (rs.next())
    			{
    				senderTfrProfileCheckVO = new TransferProfileVO();
    				senderTfrProfileCheckVO.setProfileId(rs.getString("profile_id"));
    				senderTfrProfileCheckVO.setDailyOutCount( rs.getLong("daily_transfer_out_count") );
    				senderTfrProfileCheckVO.setDailyOutValue( rs.getLong("daily_transfer_out_value"));
    				senderTfrProfileCheckVO.setWeeklyOutCount( rs.getLong("weekly_transfer_out_count") );
    				senderTfrProfileCheckVO.setWeeklyOutValue( rs.getLong("weekly_transfer_out_value"));
    				senderTfrProfileCheckVO.setMonthlyOutCount( rs.getLong("monthly_transfer_out_count") );
    				senderTfrProfileCheckVO.setMonthlyOutValue( rs.getLong("monthly_transfer_out_value"));
    				try{if (rs != null){rs.close();}} catch (Exception e){}
    			}
               //(profile counts not found) if this condition is true then made entry in logs and leave this data.
    			else
    			{
    				p_con.rollback();
       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile not found","Approval level = "+"No Approval required");
					continue;
    			}
    			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
    			{
    				//(daily in count reach) if this condition is true then made entry in logs and leave this data.
	    	        if(senderTfrProfileCheckVO.getDailyOutCount() <= senderCountsVO.getDailyOutCount())
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in count reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(daily in value reach) if this condition is true then made entry in logs and leave this data.
	    			//else if(senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO.getDailyOutValue() + batchItemsVO.getRequestedQuantity() )  )
	    	        else if(senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO.getDailyOutValue() +channelTransferItemsVO.getProductTotalMRP() )  )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in value reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(weekly in count reach) if this condition is true then made entry in logs and leave this data.
	    			else if(senderTfrProfileCheckVO.getWeeklyOutCount() <=  senderCountsVO.getWeeklyOutCount() )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in count reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(weekly in value reach) if this condition is true then made entry in logs and leave this data.
	    			else if(senderTfrProfileCheckVO.getWeeklyOutValue() < ( senderCountsVO.getWeeklyOutValue() + channelTransferItemsVO.getProductTotalMRP() )  )
	    	        {
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in value reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(monthly in count reach) if this condition is true then made entry in logs and leave this data.
	    			else if(senderTfrProfileCheckVO.getMonthlyOutCount() <=  senderCountsVO.getMonthlyOutCount()  )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in count reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(monthly in value reach) if this condition is true then made entry in logs and leave this data.
	    			//else if(senderTfrProfileCheckVO.getMonthlyOutValue() < ( senderCountsVO.getMonthlyOutValue() + batchItemsVO.getRequestedQuantity() ) )
	    			else if(senderTfrProfileCheckVO.getMonthlyOutValue() < ( senderCountsVO.getMonthlyOutValue() + channelTransferItemsVO.getProductTotalMRP() ) )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in value reach","Approval level = "+"No Approval required");
						continue;
	    			}
				}        
    	        senderCountsVO.setUserID(p_senderVO.getUserID());
    	        if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
	            {
	    	        senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()-channelTransferItemsVO.getProductTotalMRP());
	    	        senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()-channelTransferItemsVO.getProductTotalMRP());
	    	        senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()-channelTransferItemsVO.getProductTotalMRP());
	            }
    	        else
    	        {
    	        	senderCountsVO.setDailyOutCount(senderCountsVO.getDailyOutCount()+1);
	    	        senderCountsVO.setWeeklyOutCount(senderCountsVO.getWeeklyOutCount()+1);
	    	        senderCountsVO.setMonthlyOutCount(senderCountsVO.getMonthlyOutCount()+1);
	    	        senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()+channelTransferItemsVO.getProductTotalMRP());
	    	        senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()+channelTransferItemsVO.getProductTotalMRP());
	    	        senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()+channelTransferItemsVO.getProductTotalMRP());
    	        }
    	        senderCountsVO.setLastOutTime(date);
    	        senderCountsVO.setLastTransferID(c2cTransferID);
    	        senderCountsVO.setLastTransferDate(date);
    	        
//	      Update counts if found in db
        		
    	        if(flag)
        		{
 			        m = 0 ;
 					pstmtUpdateSenderTransferCounts.clearParameters();
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyInCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyInValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyInCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyInValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyInCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyInValue());

 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutValue());
					
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyInCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyInValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyInCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyInValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyInCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyInValue());

 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyOutValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyOutValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyOutValue());
					
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailySubscriberOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailySubscriberOutValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklySubscriberOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklySubscriberOutValue());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlySubscriberOutCount());
 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlySubscriberOutValue());
					
 					pstmtUpdateSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
 					pstmtUpdateSenderTransferCounts.setString(++m,senderCountsVO.getLastTransferID());
 					pstmtUpdateSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
 					pstmtUpdateSenderTransferCounts.setString(++m,senderCountsVO.getUserID());
        	        updateCount = pstmtUpdateSenderTransferCounts.executeUpdate();
        		}
        		//Insert counts if not found in db
        		else
        		{
        			m = 0 ;
 					pstmtInsertSenderTransferCounts.clearParameters();
 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutCount());
 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutValue());
 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutCount());
 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutValue());
 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutCount());
 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutValue());
 					pstmtInsertSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
 					pstmtInsertSenderTransferCounts.setString(++m,senderCountsVO.getLastTransferID());
 					pstmtInsertSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
 					pstmtInsertSenderTransferCounts.setString(++m,senderCountsVO.getUserID());
        	        updateCount = pstmtInsertSenderTransferCounts.executeUpdate();
        		}
        		if(updateCount <= 0  )
    			{
                	p_con.rollback();
       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					if(flag)
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while insert sender trasnfer counts","Approval level = "+"No Approval required");
					else
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while uptdate sender trasnfer counts","Approval level = "+"No Approval required");
					continue;
   		 		} 
        		//}
                
                pstmtSelectBalance.clearParameters();
				m=0;
                pstmtSelectBalance.setString(++m,channelUserVO.getUserID());
                pstmtSelectBalance.setString(++m,p_batchMasterVO.getProductCode());
                pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCode());
                pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCodeFor());
                rs=null;
                rs = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems=-1;
                if(rs.next())
                {
                    balance = rs.getLong("balance");
                    try{if (rs != null){rs.close();}} catch (Exception e){}
                }
                
                
           if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_batchMasterVO.getTransferType())&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_batchMasterVO.getTransferSubType()))
               {
        	   	if(balance==0 || (balance - batchItemsVO.getRequestedQuantity() < 0))
	        	   	{
	                  p_con.rollback();
	       		 	  errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.receiverbalnsuff"));
	    			  errorList.add(errorVO);
	    			  BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+"No Approval required");
	    			  continue;
	                }
          	    else if(balance != 0 && balance - batchItemsVO.getRequestedQuantity() >= 0)
          	       {
          	    	  previousUserBalToBeSetChnlTrfItems=balance;
          	    	  //balance -= batchItemsVO.getRequestedQuantity();
          	    	  balance -= channelTransferItemsVO.getRequiredQuantity();
          	       }
          	    else 
          	    	  previousUserBalToBeSetChnlTrfItems=0;
             }
           else if((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferType()))&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
                {
                	previousUserBalToBeSetChnlTrfItems=balance;
                    //balance += batchItemsVO.getRequestedQuantity();
                	balance += channelTransferItemsVO.getReceiverCreditQty();
                }
           if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
           { 
				pstmtLoadTransferProfileProduct.clearParameters();
				m=0;
                pstmtLoadTransferProfileProduct.setString(++m,batchItemsVO.getTxnProfile());
                pstmtLoadTransferProfileProduct.setString(++m,p_batchMasterVO.getProductCode());
                pstmtLoadTransferProfileProduct.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
                pstmtLoadTransferProfileProduct.setString(++m,PretupsI.YES);
    			rs=null;
                rs = pstmtLoadTransferProfileProduct.executeQuery();
                //get the transfer profile of user
    			if(rs.next())
    			{
    			    transferProfileProductVO = new TransferProfileProductVO();
    			    transferProfileProductVO.setProductCode(p_batchMasterVO.getProductCode());
    			    transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
    			    transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
    			    try{if (rs != null){rs.close();}} catch (Exception e){}
    			}
                //(transfer profile not found) if this condition is true then made entry in logs and leave this data.
    			else
    			{
    				p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.profcountersnotfound"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : User Trf Profile not found for product","Approval level = "+"No Approval required");
					continue;
    			}
                maxBalance=transferProfileProductVO.getMaxBalanceAsLong();
                //(max balance reach for the receiver) if this condition is true then made entry in logs and leave this data.
				if(maxBalance< balance )
                {
                    if(!isNotToExecuteQuery)
                        isNotToExecuteQuery = true;
                    p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : User Max balance reached","Approval level = "+"No Approval required");
					continue;
                }
				//check for the very first txn of the user containg the order value larger than maxBalance
                //(max balance reach) if this condition is true then made entry in logs and leave this data.
				else if(balance==-1 && maxBalance<batchItemsVO.getRequestedQuantity())
				 {
                    if(!isNotToExecuteQuery)
                        isNotToExecuteQuery = true;
                    p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : User Max balance reached","Approval level = "+"No Approval required");
					continue;
				  }
               }
           
           if(!isNotToExecuteQuery)
           {
               m = 0;
               //update
               if(previousUserBalToBeSetChnlTrfItems > -1)
               {
               	pstmtUpdateBalance.clearParameters();
               	handlerStmt = pstmtUpdateBalance;
               	handlerStmt.setLong(++m,previousUserBalToBeSetChnlTrfItems);
               }
				else
               {
					// insert
					pstmtInsertBalance.clearParameters();
                   handlerStmt = pstmtInsertBalance;
                   balance = batchItemsVO.getRequestedQuantity();
                   handlerStmt.setLong(++m,0);//previous balance
				   handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));//updated on date
               }
               
               handlerStmt.setLong(++m,balance);
               //handlerStmt.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
               handlerStmt.setString(++m,batchItemsVO.getTransferType());
               handlerStmt.setString(++m,c2cTransferID);
               handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
               handlerStmt.setString(++m,channelUserVO.getUserID());
               handlerStmt.setString(++m,p_batchMasterVO.getProductCode());
               handlerStmt.setString(++m,p_batchMasterVO.getNetworkCode());
               handlerStmt.setString(++m,p_batchMasterVO.getNetworkCodeFor());
               updateCount = handlerStmt.executeUpdate();
               handlerStmt.clearParameters();
                if(updateCount <= 0 )
                {
                	p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while credit uer balance","Approval level = "+"No Approval required");
					continue;
                }
                
              //for zero balance counter..
                try
                {

                    //thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_batchMasterVO.getNetworkCode(), batchItemsVO.getCategoryCode()); //threshold value
                    m=0;
                    boolean isUserThresholdEntryReq=false;
                    String thresholdType=null;
                    
//                	added by nilesh
                	String remark=null;
                	String threshold_type=null;
                    if(balance<=transferProfileProductVO.getAltBalanceLong() && balance>transferProfileProductVO.getMinResidualBalanceAsLong())
	                {
                    	//isUserThresholdEntryReq=true;
	                	thresholdValue=transferProfileProductVO.getAltBalanceLong();
	                	threshold_type=PretupsI.THRESHOLD_TYPE_ALERT;
	                }
                    else if(balance<=transferProfileProductVO.getMinResidualBalanceAsLong())
                    {
                    	//isUserThresholdEntryReq=true;
                    	thresholdValue=transferProfileProductVO.getMinResidualBalanceAsLong();
	                	threshold_type=PretupsI.THRESHOLD_TYPE_MIN;
                    }
                    //new
                    if(previousUserBalToBeSetChnlTrfItems>=thresholdValue && balance <=thresholdValue)
                    {
                        isUserThresholdEntryReq=true;
                        thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                    }
                    else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance >=thresholdValue)
                    {
                        isUserThresholdEntryReq=true;
                        thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
                    }
                    else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance <=thresholdValue)
                    {
                        isUserThresholdEntryReq=true;
                        thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                    }
                    //end
                    
                    if(isUserThresholdEntryReq)
                    {
                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug(methodName, "Entry in threshold counter" + thresholdValue+ ", prvbal: "+previousUserBalToBeSetChnlTrfItems+ "nbal"+ balance);
                        }
                        psmtInsertUserThreshold.clearParameters();
                        m=0;
                        psmtInsertUserThreshold.setString(++m, channelUserVO.getUserID());
                        psmtInsertUserThreshold.setString(++m, c2cTransferID);
                        psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                        psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getNetworkCode());
                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getProductCode());
                        //psmtInsertUserThreshold.setLong(++m, p_userBalancesVO.getUnitValue());
                        psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TYPE_C2C);
                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getTransferType());
                        psmtInsertUserThreshold.setString(++m, thresholdType);
                        psmtInsertUserThreshold.setString(++m,batchItemsVO.getCategoryCode());
                        psmtInsertUserThreshold.setLong(++m,previousUserBalToBeSetChnlTrfItems);
                        psmtInsertUserThreshold.setLong(++m, balance);
                        psmtInsertUserThreshold.setLong(++m, thresholdValue);
                        //added by nilesh
                        psmtInsertUserThreshold.setString(++m, threshold_type);
                        psmtInsertUserThreshold.setString(++m, remark);
                        
                        psmtInsertUserThreshold.executeUpdate();
                    }
                }
                catch (SQLException sqle)
                {
                    LOG.error(methodName, "SQLException " + sqle.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[closeBatchC2CTransfer]",c2cTransferID,"",p_batchMasterVO.getNetworkCode(),"Error while updating user_threshold_counter table SQL Exception:"+sqle.getMessage());
                }// end of catch
           }
           
           //if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType())){
				pstmtSelectTransferCounts.clearParameters();
				m=0;
                pstmtSelectTransferCounts.setString(++m,channelUserVO.getUserID());	
                rs=null;
                rs = pstmtSelectTransferCounts.executeQuery();
                //get the user transfer counts
                countsVO=null;
                if (rs.next())
                {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID( batchItemsVO.getUserId() );
                    
                    countsVO.setDailyInCount( rs.getLong("daily_in_count") );
                    countsVO.setDailyInValue( rs.getLong("daily_in_value") );
                    countsVO.setWeeklyInCount( rs.getLong("weekly_in_count") );
                    countsVO.setWeeklyInValue( rs.getLong("weekly_in_value") );
                    countsVO.setMonthlyInCount( rs.getLong("monthly_in_count") );
                    countsVO.setMonthlyInValue( rs.getLong("monthly_in_value") );
	                
					countsVO.setDailyOutCount( rs.getLong("daily_out_count") );
	                countsVO.setDailyOutValue( rs.getLong("daily_out_value") );
	                countsVO.setWeeklyOutCount( rs.getLong("weekly_out_count") );
	                countsVO.setWeeklyOutValue( rs.getLong("weekly_out_value") );
	                countsVO.setMonthlyOutCount( rs.getLong("monthly_out_count") );
	                countsVO.setMonthlyOutValue( rs.getLong("monthly_out_value") );				
	                
					countsVO.setUnctrlDailyInCount( rs.getLong("outside_daily_in_count") );
	                countsVO.setUnctrlDailyInValue( rs.getLong("outside_daily_in_value") );
	                countsVO.setUnctrlWeeklyInCount( rs.getLong("outside_weekly_in_count") );
	                countsVO.setUnctrlWeeklyInValue( rs.getLong("outside_weekly_in_value") );
	                countsVO.setUnctrlMonthlyInCount( rs.getLong("outside_monthly_in_count") );
	                countsVO.setUnctrlMonthlyInValue( rs.getLong("outside_monthly_in_value") );

					countsVO.setUnctrlDailyOutCount( rs.getLong("outside_daily_out_count") );
	                countsVO.setUnctrlDailyOutValue( rs.getLong("outside_daily_out_value") );
	                countsVO.setUnctrlWeeklyOutCount( rs.getLong("outside_weekly_out_count") );
	                countsVO.setUnctrlWeeklyOutValue( rs.getLong("outside_weekly_out_value") );
	                countsVO.setUnctrlMonthlyOutCount( rs.getLong("outside_monthly_out_count") );
	                countsVO.setUnctrlMonthlyOutValue( rs.getLong("outside_monthly_out_value") );
					
					countsVO.setDailySubscriberOutCount( rs.getLong("daily_subscriber_out_count") );
	                countsVO.setDailySubscriberOutValue( rs.getLong("daily_subscriber_out_value") );
	                countsVO.setWeeklySubscriberOutCount( rs.getLong("weekly_subscriber_out_count") );
	                countsVO.setWeeklySubscriberOutValue( rs.getLong("weekly_subscriber_out_value") );
	                countsVO.setMonthlySubscriberOutCount( rs.getLong("monthly_subscriber_out_count") );
	                countsVO.setMonthlySubscriberOutValue( rs.getLong("monthly_subscriber_out_value") );
	               
					countsVO.setLastTransferDate(rs.getDate("last_transfer_date") );
					try{if (rs != null){rs.close();}} catch (Exception e){}
                }
                flag=true;
        		if(countsVO == null)
        		{
        			flag = false;
        			countsVO = new UserTransferCountsVO();
        		}
        		//If found then check for reset otherwise no need to check it
        		if(flag)
        			ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO,date);
        		
				pstmtSelectProfileCounts.clearParameters();
				m=0;
    			pstmtSelectProfileCounts.setString(++m,batchItemsVO.getTxnProfile());
    			pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
    			pstmtSelectProfileCounts.setString(++m,p_batchMasterVO.getNetworkCode());
    			pstmtSelectProfileCounts.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
    			pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
    			rs=null;
    			rs = pstmtSelectProfileCounts.executeQuery();
     			//get the transfer profile counts
    			if (rs.next())
    			{
    				transferProfileVO = new TransferProfileVO();
    				transferProfileVO.setProfileId(rs.getString("profile_id"));
    				transferProfileVO.setDailyInCount( rs.getLong("daily_transfer_in_count") );
    				transferProfileVO.setDailyInValue( rs.getLong("daily_transfer_in_value"));
    				transferProfileVO.setWeeklyInCount( rs.getLong("weekly_transfer_in_count") );
    				transferProfileVO.setWeeklyInValue( rs.getLong("weekly_transfer_in_value"));
    				transferProfileVO.setMonthlyInCount( rs.getLong("monthly_transfer_in_count") );
    				transferProfileVO.setMonthlyInValue( rs.getLong("monthly_transfer_in_value"));
    				try{if (rs != null){rs.close();}} catch (Exception e){}
    			}
                //(profile counts not found) if this condition is true then made entry in logs and leave this data.
    			else
    			{
    				p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile not found","Approval level = "+"No Approval required");
					continue;
    			}
    			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType())){
	                //(daily in count reach) if this condition is true then made entry in logs and leave this data.
	    	        if(transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount())
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in count reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(daily in value reach) if this condition is true then made entry in logs and leave this data.
	    			//else if(transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchItemsVO.getRequestedQuantity() )  )
	    			else if(transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + channelTransferItemsVO.getProductTotalMRP() )  )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in value reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(weekly in count reach) if this condition is true then made entry in logs and leave this data.
	    			else if(transferProfileVO.getWeeklyInCount() <=  countsVO.getWeeklyInCount() )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in count reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(weekly in value reach) if this condition is true then made entry in logs and leave this data.
	    			//else if(transferProfileVO.getWeeklyInValue() < ( countsVO.getWeeklyInValue() + batchItemsVO.getRequestedQuantity() )  )
	    			else if(transferProfileVO.getWeeklyInValue() < ( countsVO.getWeeklyInValue() + channelTransferItemsVO.getProductTotalMRP() )  )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in value reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(monthly in count reach) if this condition is true then made entry in logs and leave this data.
	    			else if(transferProfileVO.getMonthlyInCount() <=  countsVO.getMonthlyInCount()  )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in count reach","Approval level = "+"No Approval required");
						continue;
	    			}
	                //(mobthly in value reach) if this condition is true then made entry in logs and leave this data.
	    			//else if(transferProfileVO.getMonthlyInValue() < ( countsVO.getMonthlyInValue() + batchItemsVO.getRequestedQuantity() ) )
	    			else if(transferProfileVO.getMonthlyInValue() < ( countsVO.getMonthlyInValue() + channelTransferItemsVO.getProductTotalMRP() ) )
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in value reach","Approval level = "+"No Approval required");
						continue;
	    			}
    			}
    			countsVO.setUserID(channelUserVO.getUserID());
    			if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
	            {
    				
	                countsVO.setDailyInValue(countsVO.getDailyInValue()-channelTransferItemsVO.getProductTotalMRP());
	                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()-channelTransferItemsVO.getProductTotalMRP());
	                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()-channelTransferItemsVO.getProductTotalMRP());
	            }
    			else
    			{
    				countsVO.setDailyInCount(countsVO.getDailyInCount()+1);
	                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount()+1);
	                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount()+1);
	                countsVO.setDailyInValue(countsVO.getDailyInValue()+channelTransferItemsVO.getProductTotalMRP());
	                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+channelTransferItemsVO.getProductTotalMRP());
	                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+channelTransferItemsVO.getProductTotalMRP());
    			}
                countsVO.setLastInTime(date);
        		countsVO.setLastTransferID(c2cTransferID);
        		countsVO.setLastTransferDate(date);
        		
//        		Update counts if found in db
        		
        		if(flag)
        		{
 			        m = 0 ;
 					pstmtUpdateTransferCounts.clearParameters();
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInValue());

        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutValue());
					
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInValue());

        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutValue());
					
					pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutValue());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutCount());
        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutValue());
					
					pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
        	        pstmtUpdateTransferCounts.setString(++m,countsVO.getLastTransferID());
        	        pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
        	        pstmtUpdateTransferCounts.setString(++m,countsVO.getUserID());
        	        updateCount = pstmtUpdateTransferCounts.executeUpdate();
        		}
        		//Insert counts if not found in db
        		else
        		{
        			m = 0 ;
 					pstmtInsertTransferCounts.clearParameters();
         	        pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInCount());
        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInValue());
        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInValue());
        	        pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
        	        pstmtInsertTransferCounts.setString(++m,countsVO.getLastTransferID());
        	        pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
        	        pstmtInsertTransferCounts.setString(++m,countsVO.getUserID());
        	        updateCount = pstmtInsertTransferCounts.executeUpdate();
        		}
                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
                if(updateCount <= 0  )
    			{
                	p_con.rollback();
       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					if(flag)
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while insert user trasnfer counts","Approval level = "+"No Approval required");
					else
						BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while uptdate user trasnfer counts","Approval level = "+"No Approval required");
					continue;
   		 		}
        		//}
           
            //	channelTransferVO=new ChannelTransferVO();
            channelTransferVO.setCanceledOn(batchItemsVO.getCancelledOn());
            channelTransferVO.setCanceledBy(batchItemsVO.getCancelledBy());
            channelTransferVO.setChannelRemarks(batchItemsVO.getInitiatorRemarks());
            channelTransferVO.setCommProfileSetId(batchItemsVO.getCommissionProfileSetId());
            channelTransferVO.setCommProfileVersion(batchItemsVO.getCommissionProfileVer());
            channelTransferVO.setCreatedBy(p_batchMasterVO.getCreatedBy());
            channelTransferVO.setCreatedOn(p_batchMasterVO.getCreatedOn());
            channelTransferVO.setDomainCode(p_batchMasterVO.getDomainCode());
            channelTransferVO.setFinalApprovedBy(batchItemsVO.getApprovedBy());
            channelTransferVO.setFirstApprovedOn(batchItemsVO.getApprovedOn());
            channelTransferVO.setFirstApproverLimit(0);
            channelTransferVO.setFirstApprovalRemark(batchItemsVO.getApproverRemarks());
            channelTransferVO.setSecondApprovalLimit(0);
            //channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
            channelTransferVO.setBatchNum(batchItemsVO.getBatchId());
            channelTransferVO.setBatchDate(p_batchMasterVO.getBatchDate());
            
            channelTransferVO.setPayableAmount(channelTransferItemsVO.getPayableAmount());
    		channelTransferVO.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmount());
    		channelTransferVO.setPayInstrumentAmt(0);
    		channelTransferVO.setModifiedBy(p_batchMasterVO.getModifiedBy());
    		channelTransferVO.setModifiedOn(date);
    		channelTransferVO.setProductType(p_batchMasterVO.getProductType());
    		channelTransferVO.setReceiverCategoryCode(batchItemsVO.getCategoryCode());
    		channelTransferVO.setReceiverGradeCode(batchItemsVO.getGradeCode());
    		channelTransferVO.setReceiverTxnProfile(batchItemsVO.getTxnProfile());
    		channelTransferVO.setReferenceNum(batchItemsVO.getBatchDetailId());	    		
    		channelTransferVO.setDefaultLang(p_sms_default_lang);
    		channelTransferVO.setSecondLang(p_sms_second_lang);	    		
			// for balance logger
			channelTransferVO.setReferenceID(network_id);
			//ends here
			if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null)
			{
				channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
				channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
			}
			channelTransferVO.setRequestedQuantity(batchItemsVO.getRequestedQuantity());
			channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
			channelTransferVO.setStatus(batchItemsVO.getStatus());
            
            channelTransferVO.setTotalTax1(channelTransferItemsVO.getTax1Value());
            channelTransferVO.setTotalTax2(channelTransferItemsVO.getTax2Value());
            channelTransferVO.setTotalTax3(channelTransferItemsVO.getTax3Value());
            channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
            channelTransferVO.setTransferDate(p_batchMasterVO.getCreatedOn());
            
            channelTransferVO.setTransferSubType(batchItemsVO.getTransferSubType());
            channelTransferVO.setTransferID(c2cTransferID);
            channelTransferVO.setTransferInitatedBy(p_batchMasterVO.getUserId());
            
            channelTransferVO.setTransferType(batchItemsVO.getTransferType());
            channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
            channelTransferVO.setTransferMRP(channelTransferItemsVO.getProductTotalMRP());
            //modified by vikram.
            
            if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
            {
            	channelTransferVO.setToUserID(p_batchMasterVO.getUserId());
            	channelTransferVO.setFromUserID(channelUserVO.getUserID());
            	channelTransferVO.setFromUserCode(batchItemsVO.getMsisdn());
            	channelTransferVO.setToUserCode(p_senderVO.getMsisdn());
            	channelTransferVO.setSenderGradeCode(batchItemsVO.getUserGradeCode());
            	channelTransferVO.setCategoryCode(batchItemsVO.getCategoryCode());
            	channelTransferVO.setSenderTxnProfile(batchItemsVO.getTxnProfile());
            	channelTransferVO.setReceiverCategoryCode(p_senderVO.getCategoryCode());
	    		channelTransferVO.setReceiverGradeCode(p_senderVO.getUserGrade());
	    		channelTransferVO.setReceiverTxnProfile(p_senderVO.getTransferProfileID());
	    		channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
	    		channelTransferVO.setReceiverGgraphicalDomainCode(p_senderVO.getGeographicalCode());
            }
            else
            {	//FOR the transfer/return
            	channelTransferVO.setToUserID(channelUserVO.getUserID());
            	channelTransferVO.setFromUserID(p_batchMasterVO.getUserId());
            	channelTransferVO.setFromUserCode(p_senderVO.getMsisdn());
            	channelTransferVO.setToUserCode(batchItemsVO.getMsisdn());
            	channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
            	channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
            	channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
            	channelTransferVO.setReceiverCategoryCode(batchItemsVO.getCategoryCode());
	    		channelTransferVO.setReceiverGradeCode(batchItemsVO.getUserGradeCode());
	    		channelTransferVO.setReceiverTxnProfile(batchItemsVO.getTxnProfile());
	    		channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
	    		channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
            }
            
           
            channelTransferItemsVO.setProductCode(p_batchMasterVO.getProductCode());
            channelTransferItemsVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
			
            channelTransferItemsVO.setRequiredQuantity(batchItemsVO.getRequestedQuantity());
            channelTransferItemsVO.setSerialNum(1);
            channelTransferItemsVO.setTransferID(c2cTransferID);
			// for the balance logger
			channelTransferItemsVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
			
			
            //ends here
			channelTransferItemVOList=new ArrayList();
            channelTransferItemVOList.add(channelTransferItemsVO);
            channelTransferItemsVO.setShortName(p_batchMasterVO.getProductShortName());
            //channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
            if (LOG.isDebugEnabled()) LOG.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
            if (LOG.isDebugEnabled()) LOG.debug(methodName, "Exiting: channelTransferItemsVO=" + channelTransferItemsVO.toString());
            
            
            //for positive commission deduct from network stock
           
            final boolean debit=true;
    		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.POSITIVE_COMM_APPLY))).booleanValue() && PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(channelTransferVO.getTransferType()))
    		{
    			ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con,channelTransferVO,channelTransferVO.getFromUserID(),date, debit);
    			ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con,channelTransferVO,channelTransferVO.getFromUserID(),date);
    		}
            
    		
    		//added by vikram
    		if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
            {
    			channelTransferItemsVO.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
    			channelTransferItemsVO.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
    			channelTransferItemsVO.setReceiverPreviousStock(senderPreviousBal);
    			channelTransferItemsVO.setAfterTransReceiverPreviousStock(senderPreviousBal);
            }
            else
            {	//FOR the transfer/return
            	channelTransferItemsVO.setSenderPreviousStock(senderPreviousBal);
            	channelTransferItemsVO.setAfterTransSenderPreviousStock(senderPreviousBal);
            	channelTransferItemsVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
            	channelTransferItemsVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
            }
    		
            m = 0;
			pstmtInsertIntoChannelTranfers.clearParameters();
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCanceledBy());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
        	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getChannelRemarks());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileSetId());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileVersion());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCreatedBy());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getExternalTxnNum());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFinalApprovedBy());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getFirstApproverLimit());
        	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFirstApprovalRemark());
        	pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getBatchNum());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFromUserID());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getGraphicalDomainCode());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getModifiedBy());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getNetPayableAmount());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCode());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCodeFor());
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayableAmount());
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayInstrumentAmt());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getProductType());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverCategoryCode());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverGradeCode());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverTxnProfile());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReferenceNum());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayCode());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayType());
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getRequestedQuantity());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovedBy());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getSecondApprovalLimit());
        	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovalRemark());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSource());
        	pstmtInsertIntoChannelTranfers.setString(++m,batchItemsVO.getStatus());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovedBy());
        	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
        	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovalRemark());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getToUserID());
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax1());
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax2());
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax3());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferCategory());
        	pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getTransferDate()));
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferID());
        	//pstmtInsertIntoChannelTranfers.setString(++m,p_senderVO.getUserID());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferInitatedBy());
        	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTransferMRP());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferSubType());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferType());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getType());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCategoryCode());
			pstmtInsertIntoChannelTranfers.setString(++m,PretupsI.YES);
			pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getToUserCode());
			pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());
				
			// to geographical domain also inserted as the geogrpahical domain that will help in reports
			pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getGraphicalDomainCode());
			
			//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDefaultLang());
        	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondLang());
        	pstmtInsertIntoChannelTranfers.setString(++m,p_batchMasterVO.getCreatedBy());
        	//added by vikram
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSenderGradeCode());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSenderTxnProfile());
        	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFromUserCode());
        	//Added for inserting the other commision profile set ID
        	pstmtInsertIntoChannelTranfers.setString(++m,((ChannelTransferItemsVO)channelTransferVO.getChannelTransferitemsVOList().get(0)).getOthCommSetId());
			//ends here
        	//insert into channel transfer table
        	updateCount=pstmtInsertIntoChannelTranfers.executeUpdate();
            //(record not updated properly) if this condition is true then made entry in logs and leave this data.
        	if(updateCount<=0)
        	{
        		p_con.rollback();
   		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
				errorList.add(errorVO);
				BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting in channel transfer table","Approval level = "+"No Approval required"+", updateCount="+updateCount);
				continue;
        	}
        	
        	m=0;
        	pstmtInsertIntoChannelTransferItems.clearParameters();
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getApprovedQuantity());
        	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getCommProfileDetailID());
        	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getCommRate());
        	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getCommType());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getCommValue());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getProductTotalMRP());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getNetPayableAmount());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getPayableAmount());
        	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getProductCode());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverPreviousStock());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getRequiredQuantity());
        	pstmtInsertIntoChannelTransferItems.setInt(++m,channelTransferItemsVO.getSerialNum());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getSenderPreviousStock());
        	//pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal);
        	
        	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getTax1Rate());
        	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTax1Type());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getTax1Value());
        	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getTax2Rate());
        	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTax2Type());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getTax2Value());
        	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getTax3Rate());
        	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTax3Type());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getTax3Value());
        	pstmtInsertIntoChannelTransferItems.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
        	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTransferID());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getUnitValue());
        	
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getSenderDebitQty());
        	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverCreditQty());
        	//added by vikram
    		if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
            {
    			pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getSenderPreviousStock()-channelTransferItemsVO.getSenderDebitQty());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverPreviousStock()+channelTransferItemsVO.getReceiverCreditQty());
            }
            else
            {	//FOR the transfer/return
            	pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal-channelTransferItemsVO.getSenderDebitQty());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverPreviousStock()+channelTransferItemsVO.getReceiverCreditQty());
            }
    		pstmtInsertIntoChannelTransferItems.setLong(++m, channelTransferItemsVO.getCommQuantity());
    		pstmtInsertIntoChannelTransferItems.setString(++m, channelTransferItemsVO.getOthCommType());
    		pstmtInsertIntoChannelTransferItems.setDouble(++m, channelTransferItemsVO.getOthCommRate());
    		pstmtInsertIntoChannelTransferItems.setLong(++m, channelTransferItemsVO.getOthCommValue());
        	//insert into channel transfer items table
        	updateCount=pstmtInsertIntoChannelTransferItems.executeUpdate();
            //(record not updated properly) if this condition is true then made entry in logs and leave this data.
        	if(updateCount<=0)
        	{
        		p_con.rollback();
   		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
				errorList.add(errorVO);
				BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting in channel transfer items table","Approval level = "+"No Approval required"+", updateCount="+updateCount);
				continue;
        	}
        	//commit the transaction after processing each record
//        	 insert items data here
			index=0;
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchId());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchDetailId());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getCategoryCode());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getMsisdn());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserId());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getStatus());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getModifiedBy());
			pstmtInsertBatchItems.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserGradeCode());
			pstmtInsertBatchItems.setDate(++index,BTSLUtil.getSQLDateFromUtilDate(batchItemsVO.getTransferDate()));
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getTxnProfile());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileSetId());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileVer());
			pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommProfileDetailID());
			pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommType());
			pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getCommRate());
			pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getCommValue());
			pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax1Type());
			pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax1Rate());
			pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax1Value());
			pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax2Type());
			pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax2Rate());
			pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax2Value());
			pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax3Type());
			pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax3Rate());
			pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax3Value());
			pstmtInsertBatchItems.setString(++index,String.valueOf(channelTransferItemsVO.getRequiredQuantity()));
			pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getProductTotalMRP());
			//pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getInitiatorRemarks());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getExternalCode());
			pstmtInsertBatchItems.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getTransferType());
			pstmtInsertBatchItems.setString(++index,batchItemsVO.getTransferSubType());
			queryExecutionCount=pstmtInsertBatchItems.executeUpdate();
			if(queryExecutionCount<=0)
			{
			    p_con.rollback();
			    //put error record can not be inserted
			    LOG.error(methodName, "Record cannot be inserted in batch items table");
				BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error Record cannot be inserted in batch items table","queryExecutionCount="+queryExecutionCount);
			}
			else
			{
			    p_con.commit();
			    totalSuccessRecords++;
			    // put success in the logger file.
				BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"PASS : Record inserted successfully in batch items table","queryExecutionCount="+queryExecutionCount);
			}
			//ends here
        	BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"PASS : Order is closed successfully","Approval level = "+"No Approval required"+", updateCount="+updateCount);
        	//made entry in network stock and balance logger
        	ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
			pstmtSelectBalanceInfoForMessage.clearParameters();
			m=0;
        	pstmtSelectBalanceInfoForMessage.setString(++m, channelUserVO.getUserID());
        	pstmtSelectBalanceInfoForMessage.setString(++m, p_batchMasterVO.getNetworkCode());
        	pstmtSelectBalanceInfoForMessage.setString(++m, p_batchMasterVO.getNetworkCodeFor());
            rs=null;
        	rs = pstmtSelectBalanceInfoForMessage.executeQuery();
            userbalanceList= new ArrayList();
            while (rs.next())
            {
                balancesVO = new UserBalancesVO();
                balancesVO.setProductCode(rs.getString("product_code"));
                balancesVO.setBalance(rs.getLong("balance"));
				balancesVO.setProductShortCode(rs.getString("product_short_code"));
				balancesVO.setProductShortName(rs.getString("short_name"));
				userbalanceList.add(balancesVO);
            }
			try{if (rs != null){rs.close();}} catch (Exception e){}
//			generate the message arguments to be send in SMS
            keyArgumentVO = new KeyArgumentVO();
			argsArr = new String[2];
			argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO.getRequiredQuantity());
			argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
			keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS2);
			keyArgumentVO.setArguments(argsArr);
			txnSmsMessageList=new ArrayList();
			balSmsMessageList=new ArrayList();
			txnSmsMessageList.add(keyArgumentVO);
			for(int index1=0,n=userbalanceList.size();index1<n;index1++)
			{
				balancesVO=(UserBalancesVO)userbalanceList.get(index1);
				if(balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode()))
				{
					argsArr=new String[2];
					argsArr[1]=balancesVO.getBalanceAsString();
					argsArr[0]=balancesVO.getProductShortName();
					keyArgumentVO = new KeyArgumentVO();
					keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS_BALSUBKEY);
					keyArgumentVO.setArguments(argsArr);
					balSmsMessageList.add(keyArgumentVO);
					break;
				}
			}
			locale=new Locale(language,country);
			String c2cNotifyMsg=null;
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY))).booleanValue())
			{
				LocaleMasterVO localeVO=LocaleMasterCache.getLocaleDetailsFromlocale(locale);
				if(PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
					c2cNotifyMsg=channelTransferVO.getDefaultLang();
				else
					c2cNotifyMsg=channelTransferVO.getSecondLang();
				array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList),c2cNotifyMsg};
			}  			
				
			if(c2cNotifyMsg==null)
				array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList)};
			
			messages=new BTSLMessages(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS1,array);
            pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages,channelTransferVO.getTransferID(),null,locale,channelTransferVO.getNetworkCode()); 
            //push SMS
            pushMessage.push();
			
        	
        		
				
			}// for loop for the batch items
		} 
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch (SQLException sqe)
		{
			try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[closeBatchC2CTransfer]","","","","SQL Exception:"+sqe.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+sqe.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
			try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[closeBatchC2CTransfer]","","","","Exception:"+ex.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : Exception:"+ex.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    
		    try{if (rsSelectTrfRule != null){rsSelectTrfRule.close();}} catch (Exception e){}
			try{if (pstmtSelectTrfRule != null){pstmtSelectTrfRule.close();}} catch (Exception e){}
		    try{if (rsSelectTrfRuleProd != null){rsSelectTrfRuleProd.close();}} catch (Exception e){}
			try{if (pstmtSelectTrfRuleProd != null){pstmtSelectTrfRuleProd.close();}} catch (Exception e){}
		    try{if (rsSelectCProfileProd != null){rsSelectCProfileProd.close();}} catch (Exception e){}
			try{if (pstmtSelectCProfileProd != null){pstmtSelectCProfileProd.close();}} catch (Exception e){}
			try{if (rsSelectCProfileProdDetail != null){rsSelectCProfileProdDetail.close();}} catch (Exception e){}
			try{if (pstmtSelectCProfileProdDetail != null){pstmtSelectCProfileProdDetail.close();}} catch (Exception e){}
		    try{if (rsSelectTProfileProd != null){rsSelectTProfileProd.close();}} catch (Exception e){}
			try{if (pstmtSelectTProfileProd != null){pstmtSelectTProfileProd.close();}} catch (Exception e){}
			try{if (pstmtInsertBatchMaster != null){pstmtInsertBatchMaster.close();}} catch (Exception e){}
			try{if (pstmtInsertBatchItems != null){pstmtInsertBatchItems.close();}} catch (Exception e){}
			try{if (pstmtLoadUser != null){pstmtLoadUser.close();}} catch (Exception e){}
		    try{if (pstmtSelectUserBalances!=null){pstmtSelectUserBalances.close();}} catch (Exception e){}
		    try{if (pstmtUpdateUserBalances!=null){pstmtUpdateUserBalances.close();}} catch (Exception e){}
		    try{if (pstmtInsertUserDailyBalances!=null){pstmtInsertUserDailyBalances.close();}} catch (Exception e){}
		    try{if (pstmtSelectBalance!=null){pstmtSelectBalance.close();}} catch (Exception e){}
		    try{if (pstmtUpdateBalance!=null){pstmtUpdateBalance.close();}} catch (Exception e){}
		    try{if (pstmtInsertBalance!=null){pstmtInsertBalance.close();}} catch (Exception e){}
		    try{if (pstmtSelectTransferCounts!=null){pstmtSelectTransferCounts.close();}} catch (Exception e){}
		    try{if (pstmtSelectProfileCounts!=null){pstmtSelectProfileCounts.close();}} catch (Exception e){}
		    try{if (pstmtUpdateTransferCounts!=null){pstmtUpdateTransferCounts.close();}} catch (Exception e){}
		    try{if (pstmtInsertTransferCounts!=null){pstmtInsertTransferCounts.close();}} catch (Exception e){}
		    try{if (pstmtLoadTransferProfileProduct !=null){pstmtLoadTransferProfileProduct.close();}} catch (Exception e){}
		    try{if (handlerStmt != null){handlerStmt.close();}} catch (Exception e){}
		    try{if (pstmtInsertIntoChannelTransferItems!= null){pstmtInsertIntoChannelTransferItems.close();}} catch (Exception e){}
			try{if (pstmtInsertIntoChannelTranfers!= null){pstmtInsertIntoChannelTranfers.close();}} catch (Exception e){}
			try{if (pstmtSelectBalanceInfoForMessage!= null){pstmtSelectBalanceInfoForMessage.close();}} catch (Exception e){}
			try{if (psmtInsertUserThreshold!= null){psmtInsertUserThreshold.close();}} catch (Exception e){}
			
			try
			{
				// if all records contains errors then rollback the master table entry
				if(errorList!=null &&(errorList.size()==p_batchItemsList.size()))
				{
					p_con.rollback();
					LOG.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
					BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : ALL the records conatins errors and cannot be inserted in DB ","");
				}
				//else update the master table with the open status and total number of records.
				else
				{
					int index=0;
					int queryExecutionCount=-1;
					pstmtUpdateBatchMaster.setInt(++index,p_batchMasterVO.getBatchTotalRecord()-errorList.size());
					pstmtUpdateBatchMaster.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE);
					pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
					queryExecutionCount=pstmtUpdateBatchMaster.executeUpdate();
				    if(queryExecutionCount<=0) //Means No Records Updated
		   		    {
		   		        LOG.error(methodName,"Unable to Update the batch size in master table..");
		   		        p_con.rollback();
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[closeBatchC2CTransfer]","","","","Error while updating C2C_BATCHES table. Batch id="+p_batchMasterVO.getBatchId());
		   		    }
		   		    else
		   		    {
		   		        p_con.commit();
		   		    }
		   		}

			}
			catch(Exception e)
			{
				try{if(p_con!=null)p_con.rollback();}catch(Exception ex){}
			}
			try{if (pstmtUpdateBatchMaster != null){pstmtUpdateBatchMaster.close();}} catch (Exception e){}
			//OneLineTXNLog.log(channelTransferVO);
			if (LOG.isDebugEnabled()) LOG.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
		}
		return errorList;
	}
    
    /**
	 * Method for loading C2CBatch details..
	 * This method will load the batches that are within the geography of user whose userId is passed
	 * with status(OPEN) also in items table for corresponding master record the status is in p_itemStatus
	 * 
	 * @param p_con java.sql.Connection
	 * @param p_itemStatus String
	 * @param p_currentLevel String
	 * @return java.util.ArrayList
	 * @throws  BTSLBaseException
	 */
	public ArrayList loadBatchC2CMasterDetailsForTxr(Connection p_con,String p_userID,String p_itemStatus, String p_currentLevel) throws BTSLBaseException
	{
		final String methodName = "loadBatchC2CMasterDetailsForTxr";
		if (LOG.isDebugEnabled()) 	LOG.debug(methodName, "Entered p_userID="+p_userID+" p_itemStatus="+p_itemStatus+" p_currentLevel="+p_currentLevel);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
				
		String sqlSelect = c2cBatchTransferQry.loadBatchC2CMasterDetailsForTxrQry(p_currentLevel,p_itemStatus);
		if (LOG.isDebugEnabled())  LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		ArrayList list = new ArrayList();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			pstmt.setString(4, p_userID);
			pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
			pstmt.setString(6, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO=null;
			while (rs.next())
			{
				c2cBatchMasterVO=new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setNetworkCode(rs.getString("network_code"));
				c2cBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
				c2cBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
				c2cBatchMasterVO.setProductType(rs.getString("product_type"));
				c2cBatchMasterVO.setProductShortName(rs.getString("short_name"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				c2cBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
				c2cBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
				c2cBatchMasterVO.setTransferType(rs.getString("transfer_type"));
				c2cBatchMasterVO.setTransferSubType(rs.getString("transfer_sub_type"));
				c2cBatchMasterVO.setCreatedBy(rs.getString("created_by"));
				c2cBatchMasterVO.setUserId(rs.getString("user_id"));
				list.add(c2cBatchMasterVO);
			}
		} 
		catch (SQLException sqe)
		{
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[loadBatchC2CMasterDetailsForTxr]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferDAO[loadBatchC2CMasterDetailsForTxr]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (LOG.isDebugEnabled()) LOG.debug(methodName, "Exiting: c2cBatchMasterVOList size=" + list.size());
		}
		return list;
	}
}
