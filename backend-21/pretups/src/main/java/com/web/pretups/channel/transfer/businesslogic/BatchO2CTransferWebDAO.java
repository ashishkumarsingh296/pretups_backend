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
import java.util.Locale;
import java.util.Map;

/*//import org.apache.struts.action.ActionForm;*/
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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSWithdrawBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchO2CProcessLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.web.pretups.channel.transfer.web.BatchO2CTransferForm;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserLoanWithdrawBL;
public class BatchO2CTransferWebDAO {
	 /**
     * Field log.
     */
    private final Log log = LogFactory.getLog(this.getClass().getName());
    private BatchO2CTransferWebQry batchO2CTransferWebQry = (BatchO2CTransferWebQry)ObjectProducer.getObject(QueryConstants.BATCH_O2C_TRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    private String errorGeneralSqlException =  "error.general.sql.processing";
    private String errorGeneralException =  "error.general.processing";
	private String  sqlException = "SQLException : ";
	private String  exception = "Exception:";

    /**
     * 
     */
    public BatchO2CTransferWebDAO() {
        super();
    }

   
    
    /**
     * Method loadUsersForBatchO2C.
     * This method the loads the user list for Batch O2C transfer
     * 
     * @param con
     *            Connection
     * @param domainCode
     *            String
     * @param pCategoryCode
     *            String
     * @param networkCode
     *            String
     * @param pGeographicalDomainCode
     *            String
     * @param pComPrfApplicableDate
     *            Date
     * @return LinkedHashMap
     * @throws BTSLBaseException
     * @author rajeev.kumar2
     * @author
     */

    public LinkedHashMap loadUsersForBatchO2C(Connection con, String domainCode, String pCategoryCode, String networkCode, String pGeographicalDomainCode, Date pComPrfApplicableDate) throws BTSLBaseException {
        final String methodName = "loadUsersForBatchO2C";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_domainCode=");
        	loggerValue.append(domainCode);
        	loggerValue.append("Category Code ");
        	loggerValue.append(pCategoryCode);
        	loggerValue.append(" Network Code ");
        	loggerValue.append(networkCode);
        	loggerValue.append(" p_geographicalDomainCode: ");
        	loggerValue.append(pGeographicalDomainCode);
        	loggerValue.append(", p_comPrfApplicableDate=" );
        	loggerValue.append(pComPrfApplicableDate);
            log.debug( methodName,loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;

        final String categoryCode = pCategoryCode.replaceAll("'", "");
        final String ss = categoryCode.replaceAll("\" ", "");
        final String mCategoryCode[] = ss.split(",");

        final String geographicalDomainCode = pGeographicalDomainCode.replaceAll("'", "");
        final String gg = geographicalDomainCode.replaceAll("\" ", "");
        final String mGeographicalDomainCode[] = gg.split(",");

        String mReceiverStatusAllowed[] = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, pCategoryCode.replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
           

            receiverStatusAllowed = userStatusVO.getUserReceiverAllowed();
            final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
            final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
            mReceiverStatusAllowed = sa.split(",");

        } else {
            throw new BTSLBaseException(this, methodName, "error.status.processing");
        }
        final String sqlSelect = batchO2CTransferWebQry.loadUsersForBatchO2CQry(mCategoryCode, mReceiverStatusAllowed, mGeographicalDomainCode);
       
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, networkCode);

            for (int x = 0; x < mCategoryCode.length; x++) {
                ++i;
                pstmt.setString(i, mCategoryCode[x]);
            }
            ++i;
            pstmt.setString(i, domainCode);

            for (int x = 0; x < mReceiverStatusAllowed.length; x++) {
                ++i;
                pstmt.setString(i, mReceiverStatusAllowed[x]);
            }

            for (int x = 0; x < mGeographicalDomainCode.length; x++) {
                ++i;
                pstmt.setString(i, mGeographicalDomainCode[x]);
            }
            ++i;
            pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(pComPrfApplicableDate));
            rs = pstmt.executeQuery();
            ChannelUserVO channelVO = null;
            while (rs.next()) {
                channelVO = new ChannelUserVO();

                channelVO.setUserID(rs.getString("user_id"));
                channelVO.setMsisdn(rs.getString("msisdn"));
                channelVO.setLoginID(rs.getString("login_id"));
                channelVO.setCategoryCode(rs.getString("category_code"));
                channelVO.setCategoryName(rs.getString("category_name"));
                channelVO.setUserGrade(rs.getString("grade_code"));
                channelVO.setUserGradeName(rs.getString("grade_name"));
                channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
                channelVO.setInSuspend(rs.getString("in_suspend"));
                channelVO.setStatus(rs.getString("status"));
                channelVO.setExternalCode(rs.getString("external_code"));

                channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
                channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
                channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
                channelVO.set_commissionProfileApplicableFromAsString(BTSLDateUtil.getLocaleDateTimeFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from"))));
                channelVO.setTransferProfileName(rs.getString("profile_name"));
                channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                channelVO.setTransferProfileStatus(rs.getString("profile_status"));
                channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                linkedHashMap.put(channelVO.getMsisdn(), channelVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append(" SQLException :");
        	loggerValue.append(sqe);
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append(" SQLException :");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchO2C]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception :");
        	loggerValue.append(ex);
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append( " Exception :" );
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersForBatchO2C]", "", "", "",
            		loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append( " Exiting:  linkedHashMap Size = " );
            	loggerValue.append(linkedHashMap.size());
                log.debug(methodName, loggerValue);
            }
        }
        return linkedHashMap;
    }

    /**
     * Method validateUsersForBatchO2C.
     * This method the loads the user list for Batch O2C transfer
     * 
     * @param con
     *            Connection
     * @param batchO2CItemsVOList
     *            ArrayList
     * @param domainCode
     *            String
     * @param categoryCode
     *            String
     * @param networkCode
     *            String
     * @param geographicalDomainCode
     *            String
     * @param comPrfApplicableDate
     *            Date
     * @param messages
     *            MessageResources
     * @param locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author rajeev.kumar2
     */

    public ArrayList validateUsersForBatchO2C(Connection con, ArrayList batchO2CItemsVOList, String domainCode, String categoryCode, String networkCode, String geographicalDomainCode, Date comPrfApplicableDate, MessageResources messages, Locale locale) throws BTSLBaseException {
        final String methodName = "validateUsersForBatchO2C";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered p_batchO2CItemsVOList.size()=" + batchO2CItemsVOList.size() + ", p_domainCode=" + domainCode + "Category Code " + categoryCode + " Network Code " + networkCode + " p_geographicalDomainCode: " + geographicalDomainCode + ", p_comPrfApplicableDate=" + comPrfApplicableDate);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String receiverStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(networkCode, categoryCode.replaceAll("'", ""), PretupsI.USER_TYPE_CHANNEL,
            PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "loadUsersForBatchO2C", "error.status.processing");
        }
        
        
        final String sqlSelect = batchO2CTransferWebQry.validateUsersForBatchO2CQry(receiverStatusAllowed, categoryCode, geographicalDomainCode);
        
        final ArrayList errorList = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            int index = 0;
            BatchO2CItemsVO batchO2CItemsVO = null;
            ListValueVO errorVO = null;
            boolean fileValidationErrorExists = false;
            for (int i = 0, j = batchO2CItemsVOList.size(); i < j; i++) {
                batchO2CItemsVO = (BatchO2CItemsVO) batchO2CItemsVOList.get(i);
                index = 0;
                ++index;
                pstmt.setString(index, batchO2CItemsVO.getMsisdn());
                ++index;
                pstmt.setString(index, networkCode);
                ++index;
                pstmt.setString(index, domainCode);
                ++index;
                pstmt.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(comPrfApplicableDate));
                rs = pstmt.executeQuery();
                pstmt.clearParameters();
                if (rs.next()) {
                    if (!PretupsI.NO.equals(rs.getString("in_suspend"))) {
                        // put error user is in suspended
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString("batcho2c.processuploadedfile.error.userinsuspend"));
                        errorVO.setIDValue("batcho2c.processuploadedfile.error.userinsuspend");
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : User is IN suspended", "Batch O2CInitiate ");
                        continue;

                    }
                    
                    if (!PretupsI.YES.equals(rs.getString("profile_status"))) {
                        // put transfer profile is not active
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString("batcho2c.processuploadedfile.error.trfprfsuspended"));
                        errorVO.setIDValue("batcho2c.processuploadedfile.error.trfprfsuspended");
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Transfer profile is suspended", "Batch O2CInitiate");
                        continue;
                    }
                    if (!PretupsI.YES.equals(rs.getString("commprofilestatus"))) {
                        // put commission profile is not active
                        // with reason
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(batchO2CItemsVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                        // ChangeID=LOCALEMASTER
                        // which language message to be set is determined from
                        // the locale master table for the requested locale
                        if (PretupsI.LANG1_MESSAGE.equals((LocaleMasterCache.getLocaleDetailsFromlocale(locale)).getMessage())) {
                            // reason is rs.getString("comprf_lang_1_msg")
                        	String msg = PretupsRestUtil.getMessageString("batcho2c.processuploadedfile.error.comprfinactive");
                        	msg = msg.replace("\\[0\\]", rs.getString("comprf_lang_1_msg"));
                            errorVO.setOtherInfo2(msg);
                        } else {
                        	String msg = PretupsRestUtil.getMessageString("batcho2c.processuploadedfile.error.comprfinactive");
                        	msg = msg.replace("\\[0\\]", rs.getString("comprf_lang_2_msg"));
                            errorVO.setOtherInfo2(msg);
                        }
                        errorVO.setIDValue("batcho2c.processuploadedfile.error.comprfinactive");
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Commision profile is inactive", "Batch O2CInitiate");
                        continue;
                    }
                    if (BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(comPrfApplicableDate)) {
                        // no commission profile is associated till today.
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString("batcho2c.processuploadedfile.error.nocomprfassociated"));
                        errorVO.setIDValue("batcho2c.processuploadedfile.error.nocomprfassociated");
                        errorList.add(errorVO);
                        fileValidationErrorExists = true;
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : No commission profile is associated till today",
                            "Batch O2C Initiate");
                        continue;
                    }

                    if (!fileValidationErrorExists) {
                        batchO2CItemsVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
                        batchO2CItemsVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
                        batchO2CItemsVO.setTxnProfile(rs.getString("transfer_profile_id"));
                        batchO2CItemsVO.setCategoryCode(rs.getString("category_code"));
                        batchO2CItemsVO.setUserGradeCode(rs.getString("grade_code"));
                        batchO2CItemsVO.setUserId(rs.getString("user_id"));
                        batchO2CItemsVO.setDualCommissionType(rs.getString("dual_comm_type"));
                    }
                } else {
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString("batcho2c.processuploadedfile.error.msisdnnotfound"));
                    errorVO.setIDValue("batcho2c.processuploadedfile.error.msisdnnotfound");
                    errorList.add(errorVO);
                    fileValidationErrorExists = true;
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Msisdn not found", "Batch O2C Initiate");
                    continue;
                }
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException :" + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[validateUsersForBatchO2C]", "",
                "", "", " SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            log.error(methodName, " Exception:" + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[validateUsersForBatchO2C]", "",
                "", "", "Exception :" + ex.getMessage());
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
                log.debug(methodName, "Exiting:  errorList Size =" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * Method initiateBatchO2CTransfer
     * This method used for the batch o2c order initiation. The main purpose of
     * this method is to insert the
     * records in o2c_batches,o2c_batch_geographies & o2c_batch_items table.
     * 
     * @param con
     *            Connection
     * @param batchMasterVO
     *            O2CBatchMasterVO
     * @param batchItemsList
     *            ArrayList
     * @param messages
     *            MessageResources
     * @param locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     * @author rajeev.kumar2
     */

    public ArrayList initiateBatchO2CTransfer(Connection con, O2CBatchMasterVO batchMasterVO, ArrayList batchItemsList, MessageResources messages, Locale locale, String trfType, String trfSubType, BatchO2CTransferForm form) throws BTSLBaseException {
        final String methodName = "initiateBatchO2CTransfer";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered.... p_batchMasterVO=" + batchMasterVO + ", p_batchItemsList.size() = " + batchItemsList.size() + ", p_batchItemsList=" + batchItemsList + "p_locale=" + locale + ", p_trfType=" + trfType + ", p_trfSubType=" + trfSubType);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        Boolean isPaymentModeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
        Boolean isTransactionTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        final ArrayList errorList = new ArrayList();
        BatchO2CItemsVO batchO2CItemsVO = null;
        ListValueVO errorVO = null;
        final BatchO2CTransferForm theForm = (BatchO2CTransferForm) form;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM o2c_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }
        // for loading the O2C transfer rule for O2C batch transfer
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuilder strBuffSelectTrfRule = new StringBuilder(" SELECT transfer_rule_id ");
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
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        if (isTransactionTypeAlwd)
    	{ 
        strBuffSelectCProfileProd.append("AND cp.transaction_type in ( ? , ? ) ");
    	}
        else
        	 {
        	strBuffSelectCProfileProd.append("AND cp.transaction_type = ? ");
        	 }
        strBuffSelectCProfileProd.append(" AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectCProfileProd Query =" + strBuffSelectCProfileProd);
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
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
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO o2c_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO o2c_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO o2c_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems
            .append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status, payment_type, payable_amount, net_payable_amount,dual_comm_type) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchItems Query =" + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE o2c_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectTrfRule = con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = con.prepareStatement(strBuffSelectTProfileProd.toString());

            pstmtInsertBatchMaster =  con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = con.prepareStatement(strBuffInsertBatchGeo.toString());
            pstmtInsertBatchItems =  con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = con.prepareStatement(strBuffUpdateBatchMaster.toString());
            ChannelTransferRuleVO rulesVO = null;
            int index = 0;

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
                BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "query Execution Count=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO [initiateBatchO2CTransfer] ",
                    "", "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
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
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    log.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "query Execution Count=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "BatchO2CTransferDAO[initiateBatchO2CTransfer] ", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            String msgArr[] = null;
            final HashMap batchO2Cmap = new HashMap();
            for (int i = 0, j = batchItemsList.size(); i < j; i++) {
                batchO2CItemsVO = (BatchO2CItemsVO) batchItemsList.get(i);
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchO2CItemsVO.getExtTxnNo()) && isExternalTxnUnique) {
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, batchO2CItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID1 = pstmtSelectExtTxnID1.executeQuery();
                    pstmtSelectExtTxnID1.clearParameters();
                    if (rsSelectExtTxnID1.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(batchO2CItemsVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                        errorVO.setOtherInfo2(messages.getMessage(locale, "batcho2c.initiatebatcho2ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : External txn number already exist O2C BATCH", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // load the product's informaiton.
                if (transferRuleNotExistMap.get(batchO2CItemsVO.getCategoryCode()) == null) {
                    if (transferRuleProdNotExistMap.get(batchO2CItemsVO.getCategoryCode()) == null) {
                        if (transferRuleMap.get(batchO2CItemsVO.getCategoryCode()) == null) {
                            index = 0;
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getNetworkCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getDomainCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchO2CItemsVO.getCategoryCode());
                            rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
                            pstmtSelectTrfRule.clearParameters();
                            if (rsSelectTrfRule.next()) {
                                rulesVO = new ChannelTransferRuleVO();
                                rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
                                index = 0;
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, rulesVO.getTransferRuleID());
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, batchMasterVO.getProductCode());
                                rsSelectTrfRuleProd = pstmtSelectTrfRuleProd.executeQuery();
                                pstmtSelectTrfRuleProd.clearParameters();
                                if (!rsSelectTrfRuleProd.next()) {
                                    transferRuleProdNotExistMap.put(batchO2CItemsVO.getCategoryCode(), batchO2CItemsVO.getCategoryCode());
                                    // put error log Prodcuct is not in the
                                    // transfer rule
                                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                                        "batcho2c.initiatebatcho2ctransfer.msg.error.prodnotintrfrule"));
                                    errorList.add(errorVO);
                                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchO2CItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchO2CItemsVO.getCategoryCode(), batchO2CItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                                    "batcho2c.initiatebatcho2ctransfer.msg.error.trfrulenotdefined"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2c.initiatebatcho2ctransfer.msg.error.prodnotintrfrule"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.initiatebatcho2ctransfer.msg.error.trfrulenotdefined"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchO2CItemsVO.getCategoryCode());
                // check the transfer profile product code

                // transfer profile check ends here
                if (transferProfileMap.get(batchO2CItemsVO.getTxnProfile()) == null) {
                    index = 0;
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchO2CItemsVO.getTxnProfile());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchMasterVO.getProductCode());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    rsSelectTProfileProd = pstmtSelectTProfileProd.executeQuery();
                    pstmtSelectTProfileProd.clearParameters();
                    if (!rsSelectTProfileProd.next()) {
                        transferProfileMap.put(batchO2CItemsVO.getTxnProfile(), "false");
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2c.initiatebatcho2ctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchO2CItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchO2CItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2c.initiatebatcho2ctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                }

                // check the commisson profile applicability and other checks
                // related to the commission profile
                index = 0;
                ++index;
                pstmtSelectCProfileProd.setString(index, batchMasterVO.getProductCode());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchO2CItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchO2CItemsVO.getCommissionProfileVer());
                if (isTransactionTypeAlwd)
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
                pstmtSelectCProfileProd.setString(index, (isTransactionTypeAlwd && isPaymentModeAlwd)?batchO2CItemsVO.getPaymentType():PretupsI.ALL);
                rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                if (!rsSelectCProfileProd.next()) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.initiatebatcho2ctransfer.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Commission profile for this product is not defined", "");
                    continue;
                }
                requestedValue = batchO2CItemsVO.getRequestedQuantity();
                minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                if (minTrfValue > requestedValue || maxTrfValue < requestedValue) {
                    msgArr = new String[3];
                    msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                    msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                    msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                    // put error requested quantity is not between min and max
                    // values
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.initiatebatcho2ctransfer.msg.error.qtymaxmin", msgArr));
                    msgArr = null;
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Requested quantity is not between min and max values",
                        "minTrfValue=" + minTrfValue + ", maxTrfValue=" + maxTrfValue);
                    continue;
                }
                multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                if (requestedValue % multipleOf != 0) {
                    // put error requested quantity is not multiple of
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.initiatebatcho2ctransfer.msg.error.notmulof", new String[] { PretupsBL.getDisplayAmount(multipleOf) }));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Requested quantity is not in multiple value",
                        "multiple of=" + multipleOf);
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
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.initiatebatcho2ctransfer.msg.error.commslabnotdefined"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Commission profile slab is not define for the requested value", "");
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

                channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail.getDouble("commission_rate"));
                channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail.getString("commission_type"));

                channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd.getDouble("discount_rate"));
                channelTransferItemsVO.setDiscountType(rsSelectCProfileProd.getString("discount_type"));

                channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail.getDouble("tax1_rate"));
                channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail.getString("tax1_type"));

                channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail.getDouble("tax2_rate"));
                channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail.getString("tax2_type"));

                channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail.getDouble("tax3_rate"));
                channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail.getString("tax3_type"));

                transferItemsList.add(channelTransferItemsVO);

                // make a new channel TransferVO to transfer into the method
                // during tax calculataion
                final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setDualCommissionType(batchO2CItemsVO.getDualCommissionType());
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);

                // taxes on O2C required
                // ends commission profile validaiton

                // insert items data here
                index = 0;
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getBatchId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getBatchDetailId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getCategoryCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getMsisdn());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getUserId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getStatus());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getModifiedBy());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getModifiedOn()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getUserGradeCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getExtTxnNo());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getExtTxnDate()));
                ++index;
                pstmtInsertBatchItems.setDate(index, BTSLUtil.getSQLDateFromUtilDate(batchO2CItemsVO.getTransferDate()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getTxnProfile());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getCommissionProfileVer());
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
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getPaymentType());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getPayableAmount());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getNetPayableAmount());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    // put error record can not be inserted
                    log.error(methodName, "Record cannot be inserted in batch items table");
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here
                channelTransferItemsVO.setLoginId(batchO2CItemsVO.getLoginID());
                channelTransferItemsVO.setMsisdn(batchO2CItemsVO.getMsisdn());
                channelTransferItemsVO.setUserCategory(batchO2CItemsVO.getUserCategory());
                channelTransferItemsVO.setGradeName(batchO2CItemsVO.getGradeName());
                channelTransferItemsVO.setPaymentType(batchO2CItemsVO.getPaymentType());
                channelTransferItemsVO.setInitiatorRemarks(batchO2CItemsVO.getInitiatorRemarks());
                channelTransferItemsVO.setExtTxnNo(batchO2CItemsVO.getExtTxnNo());
                channelTransferItemsVO.setExtTxnDate(batchO2CItemsVO.getExtTxnDate());
                channelTransferItemsVO.setExternalCode(batchO2CItemsVO.getExternalCode());
                channelTransferItemsVO.setCommissionProfileSetId(batchO2CItemsVO.getCommissionProfileSetId());
                channelTransferItemsVO.setCommissionProfileVer(batchO2CItemsVO.getCommissionProfileVer());

                batchO2Cmap.put(batchO2CItemsVO.getBatchDetailId(), channelTransferItemsVO);
            } // for loop for the batch items
            theForm.setBatchO2CItems(batchO2Cmap);
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException :" + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", " SQLException:" + sqe.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, " FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            log.error(methodName, " Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", "Exception:" + ex.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : Exception : " + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralException);
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
                    BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        log.error(methodName, "Unable to Update the batch size in master table..");
                        con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "BatchO2CTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Error while updating O2C_BATCHES table. Batch id=" + batchMasterVO.getBatchId());
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
     * This method will load the data from the o2c_batch_items table
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
   public LinkedHashMap loadBatchO2CItemsMap(Connection con, String batchId, String itemStatus, String trfType, String trfSubType) throws BTSLBaseException {
        final String methodName = "loadBatchO2CItemsMap";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_batchId=" + batchId + " p_itemStatus=" + itemStatus + " p_trfType=" + trfType + " p_trfSubType=" + trfSubType);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlSelect=null;
       if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equalsIgnoreCase(trfSubType)||PretupsI.FOC_WALLET_TYPE.equalsIgnoreCase(trfType)) {
    	   sqlSelect = batchO2CTransferWebQry.loadBatchO2CItemsMapQry(itemStatus);
              
       }
       else {
    	   sqlSelect = batchO2CTransferWebQry.loadBatchO2CItemsMapQry(itemStatus);
       }
    	   if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final LinkedHashMap map = new LinkedHashMap();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, batchId);
            pstmt.setString(2, trfType);
            pstmt.setString(3, trfSubType);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
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
                batchO2CItemsVO.setPaymentType(rs.getString("payment_type"));
                batchO2CItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                batchO2CItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
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
                batchO2CItemsVO.setPayableAmount(rs.getLong("payable_amount"));
                batchO2CItemsVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                batchO2CItemsVO.setFirstApprovedQuantity(rs.getLong("approved1_quantity"));// @@@
                batchO2CItemsVO.setSecondApprQty(rs.getLong("approved2_quantity"));
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
            throw new BTSLBaseException(this, "loadBatchO2CItemsList", errorGeneralSqlException);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadBatchO2CItemsMap]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralException);
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
     * @author rajeev.kumar2
     */
    public ArrayList loadO2CBatchMasterDetails(Connection con, String userID, String itemStatus, String currentLevel, String trfType, String trfSubType) throws BTSLBaseException {
        final String methodName = "loadO2CBatchMasterDetails";

    	if (log.isDebugEnabled()) {
            log.debug(
                "loadBatchO2CMasterDetails",
                "Entered p_userID=" + userID + " p_itemStatus=" + itemStatus + " p_currentLevel=" + currentLevel + " p_trfType=" + trfType + " p_trfSubType=" + trfSubType);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        

        final String sqlSelect = batchO2CTransferWebQry.loadO2CBatchMasterDetailsQry(currentLevel, itemStatus);
     
        final ArrayList list = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, userID);
            pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN);
            pstmt.setString(8, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
            rs = pstmt.executeQuery();
            O2CBatchMasterVO o2cBatchMasterVO = null;
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
                list.add(o2cBatchMasterVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException:" + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadO2CBatchMasterDetails]", "",
                "", "", " SQL Exception :" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[loadO2CBatchMasterDetails]", "",
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
                log.debug(methodName, "Exiting: o2cBatchMasterVOList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method to close the o2c order by batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,o2c_batch_items,
     * o2c_batches,
     * channel_transfers_items,channel_transfers
     * 
     * @param con
     * @param dataMap
     * @param currentLevel
     * @param userID
     * @param o2cBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatch(Connection con, LinkedHashMap dataMap, String currentLevel, String userID, O2CBatchMasterVO o2cBatchMatserVO, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "closeOrderByBatch";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_dataMap=" + dataMap + " p_currentLevel=" + currentLevel + " p_locale=" + p_locale);
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
        PreparedStatement psmtAppr1O2CBatchItem = null;
        PreparedStatement psmtAppr2O2CBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;

        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
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
        String o2cTransferID = null;
        String m_receiverStatusAllowed[] = null;
        OperatorUtilI operatorUtili = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch] ", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
        // user life cycle
        String receiverStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(o2cBatchMatserVO.getNetworkCode(), o2cBatchMatserVO.getCategoryCode(),
            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.processing");
        }
        final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
        final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
        m_receiverStatusAllowed = sa.split(",");
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuilder sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
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
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }

        // The query below is used to load the network stock details for network
        // in between sender and receiver
        // This table will basically used to update the daily_stock_updated_on
        // and also to know how many
        // records are to be inseert in network_daily_stocks
   
        final String sqlLoadNetworkStock = batchO2CTransferWebQry.closeOrderByBatchLoadNetworkStockQry();
    
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
        
        final String sqlSelectNetworkStock = batchO2CTransferWebQry.closeOrderByBatchSelectNetworkStockQry();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelectNetworkStock=" + sqlSelectNetworkStock);
        }
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

        // Insert record into network_stock_transactions table. //Added by lalit to fix BUG DEF457 GP 6.6.1
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_transactions ( ");
        sqlBuffer.append(" ref_txn_id, txn_wallet, txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
        sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
        sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
        sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp ) ");
        sqlBuffer.append(" VALUES ");
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

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be inseert in user_daily_balances table
    
        final String selectUserBalances = batchO2CTransferWebQry.closeOrderByBatchSelectUserBalancesQry();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
        }
        sqlBuffer = null;

        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
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
            log.debug(methodName, "QUERY insertUserDailyBalances=" + insertUserDailyBalances);
        }

        // Select the balance of user for the perticuler product and network.
       
        Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        String walletForAdnlCmsn = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN);
        String txnSenderUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG);
        Boolean isO2CSmsNotify = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_SMS_NOTIFY);
        Boolean isLmsAppl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        String defaultWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        final String selectBalance = batchO2CTransferWebQry.closeOrderByBatchSelectBalanceQry();
      
        sqlBuffer = null;

        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        if (isUserProductMultipleWallet) {
            sqlBuffer.append(" and balance_type=? ");
        }
        final String updateBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateBalance=" + updateBalance);
        }
        sqlBuffer = null;

        // Insert the record of balnce for user (If balance not found in
        // user_balances)
        sqlBuffer = new StringBuilder(" INSERT ");
        sqlBuffer.append(" INTO user_balances ");
        sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
        sqlBuffer.append(" user_id, product_code , network_code, network_code_for ) ");
        if (isUserProductMultipleWallet) {
            sqlBuffer.append(" , balance_type ");
        }
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,? ");
        if (isUserProductMultipleWallet) {
            sqlBuffer.append(" ,? ");
        }

        sqlBuffer.append(")");
        final String insertBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertBalance=" + insertBalance);
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
        sqlBuffer.append(" ,last_sos_txn_status,last_lr_status ");
        sqlBuffer.append(" FROM user_transfer_counts ");
        sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        final String selectTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
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
            log.debug(methodName, "QUERY selectProfileCounts=" + selectProfileCounts);
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
            log.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
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
            log.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // o2c_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,approved1_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv1O2CBatchItems=" + sqlApprv1O2CBatchItems);
        }
        sqlBuffer = null;

        // If current level of approval is 2 then below query is used to updatwe
        // o2c_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,approved2_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv2O2CBatchItems=" + sqlApprv2O2CBatchItems);
        }
        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
     
        
        final String selectItemsDetails = batchO2CTransferWebQry.closeOrderByBatchItemsDetailsQry();
 
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE o2c_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateO2CBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateO2CBatches =" + updateO2CBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM o2c_batch_items ");
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
            log.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
        }
        sqlBuffer = null;

        // The query bel;ow is used to insert the record in channel transfer
        // items table for the order that is closed
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price, sender_debit_quantity, receiver_credit_quantity,commision_quantity,sender_post_stock, receiver_post_stock,otf_type,otf_rate,otf_amount,otf_applicable)  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
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
        sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,pmt_inst_type,pmt_inst_no,pmt_inst_date,FIRST_LEVEL_APPROVED_QUANTITY,SECOND_LEVEL_APPROVED_QUANTITY,dual_comm_type ) ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
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
            log.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
        }
        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        try {
            BatchO2CItemsVO batchO2CItemsVO = null;
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
            pstmtInsertNetworkStockTransaction =  con.prepareStatement(insertNetworkStockTransaction);
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

            psmtAppr1O2CBatchItem =  con.prepareStatement(sqlApprv1O2CBatchItems);
            psmtAppr2O2CBatchItem =  con.prepareStatement(sqlApprv2O2CBatchItems);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster =  con.prepareStatement(updateO2CBatches);
            pstmtIsModified = con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = con.prepareStatement(loadTransferProfileProduct);
            pstmtInsertIntoChannelTransferItems = con.prepareStatement(insertIntoChannelTransferItem);
            pstmtInsertIntoChannelTranfers =  con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = con.prepareStatement(selectBalanceInfoForMessage);
            errorList = new ArrayList();
            final Iterator iterator = dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defaultWebGatewayCode);
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
            long bonusBalance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;
            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                batchO2CItemsVO = (BatchO2CItemsVO) dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = batchO2CItemsVO.getBatchId();
                }
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Executed batchO2CItemsVO=" + batchO2CItemsVO.toString());
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, batchO2CItemsVO.getUserId());
                for (int x = 0; x < m_receiverStatusAllowed.length; x++) {
                    ++m;
                    pstmtLoadUser.setString(m, m_receiverStatusAllowed[x]);
                }
                try{
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(batchO2CItemsVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                    language = rs.getString("phone_language");
                    country = rs.getString("country");
                    channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                    if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2c.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Commission profile suspend",
                            " Approvallevel " + currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2c.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Transfer profile suspend",
                            " Approval level " + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2c.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog
                            .detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User is IN suspend", " Approvallevel" + currentLevel);
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User not found", " Approval level " + currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                networkStocksVO = new NetworkStockVO();
                networkStocksVO.setProductCode(o2cBatchMatserVO.getProductCode());
                networkStocksVO.setNetworkCode(o2cBatchMatserVO.getNetworkCode());
                networkStocksVO.setNetworkCodeFor(o2cBatchMatserVO.getNetworkCodeFor());

                // creating the channelTransferVO here since O2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth o2c items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(o2cBatchMatserVO.getNetworkCode());
                channelTransferVO.setNetworkCodeFor(o2cBatchMatserVO.getNetworkCodeFor());
                channelTransferVO.setProductCode(o2cBatchMatserVO.getProductCode());
                channelTransferVO.setToUserID(batchO2CItemsVO.getUserId());
                ChannelTransferBL.genrateTransferID(channelTransferVO);
                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch o2c item was created.
                channelTransferVO.setCreatedOn(batchO2CItemsVO.getInitiatedOn());

                networkStocksVO.setLastTxnNum(o2cTransferID);
                if (batchO2CItemsVO.isCommCalReqd()) {
                    networkStocksVO.setLastTxnBalance(batchO2CItemsVO.getSecondApprQty());
                    networkStocksVO.setWalletBalance(batchO2CItemsVO.getSecondApprQty());
                } else {
                    networkStocksVO.setLastTxnBalance(batchO2CItemsVO.getRequestedQuantity());
                    networkStocksVO.setWalletBalance(batchO2CItemsVO.getRequestedQuantity());
                }
                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                networkStocksVO.setModifiedBy(userID);
                networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
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
                try{
                rs = null;
                rs = pstmtLoadNetworkStock.executeQuery();
                while (rs.next()) {
                    dailyStockUpdatedOn = rs.getDate("daily_stock_updated_on");

                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, date);

                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the difference
                        // is.
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("product_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_created"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_returned"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_sold"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("last_txn_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("previous_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
							
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                    "FAIL : DB Error while insert in network daily stock table", "Approval level= " + currentLevel + "updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Termination of the procissing",
                                "Approval level= " + currentLevel);
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
                            errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                "FAIL : DB Error while updating network daily stock table", " Approval level = " + currentLevel + "updateCount = " + updateCount);
                            continue;
                        }
                    }
                }
                }
                finally{
                	if (rs != null) {
                        rs.close();
                    }
                }
                
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
                try{
                rs = null;
                rs = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockSold = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs.next()) {
                    stock = rs.getLong("wallet_balance");
                    stockSold = rs.getLong("wallet_sold");
                    previousNwStockToBeSetChnlTrfItems = stock;
                 }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(p_messages.getMessage(p_locale, "label.all"), String.valueOf(batchO2CItemsVO.getRecordNumber()) + " - " + p_messages.getMessage(
                        p_locale, "label.all"), p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", " Approval level =" + currentLevel);
                    throw new BTSLBaseException(this, methodName, "batcho2c.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", " Approval level = " + currentLevel);
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
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating network stock table",
                        " Approval level = " + currentLevel + ", updateCount = " + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                    new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);
                }

                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setNetworkCode(networkStocksVO.getNetworkCode());
                networkStockTxnVO.setNetworkFor(networkStocksVO.getNetworkCodeFor());
                if (networkStocksVO.getNetworkCode().equals(o2cBatchMatserVO.getNetworkCodeFor())) {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                } else {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                }
                // As discussed with sandeep in channel transfer table's
                // reference number field we have
                // to insert batch details id.So In network stock where channel
                // transfer table's reference number
                // was inserted, I insert batch detail id.
                networkStockTxnVO.setReferenceNo(batchO2CItemsVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(batchO2CItemsVO.getInitiatedOn());
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    networkStockTxnVO.setRequestedQuantity(batchO2CItemsVO.getRequestedQuantity());
                    networkStockTxnVO.setApprovedQuantity(batchO2CItemsVO.getRequestedQuantity());
                } else {
                    networkStockTxnVO.setRequestedQuantity(batchO2CItemsVO.getSecondApprQty());
                    networkStockTxnVO.setApprovedQuantity(batchO2CItemsVO.getSecondApprQty());
                }
                networkStockTxnVO.setInitiaterRemarks(batchO2CItemsVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(batchO2CItemsVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(batchO2CItemsVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(batchO2CItemsVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(batchO2CItemsVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(batchO2CItemsVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(batchO2CItemsVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(batchO2CItemsVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(batchO2CItemsVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(userID);

                networkStockTxnVO.setTxnStatus(batchO2CItemsVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
                networkStockTxnVO.setTxnType(PretupsI.DEBIT);
                networkStockTxnVO.setInitiatedBy(userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(batchO2CItemsVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(batchO2CItemsVO.getTransferMrp());

                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    networkItemsVO.setRequiredQuantity(batchO2CItemsVO.getRequestedQuantity());
                    networkItemsVO.setApprovedQuantity(batchO2CItemsVO.getRequestedQuantity());
                } else {

                    networkItemsVO.setRequiredQuantity(batchO2CItemsVO.getSecondApprQty());
                    networkItemsVO.setApprovedQuantity(batchO2CItemsVO.getSecondApprQty());
                }
                networkItemsVO.setMrp(batchO2CItemsVO.getTransferMrp());
                networkItemsVO.setProductCode(o2cBatchMatserVO.getProductCode());
                networkItemsVO.setAmount(0);
                networkItemsVO.setProductCode(o2cBatchMatserVO.getProductCode());
                networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
                // Added on 07/02/08
                networkItemsVO.setDateTime(o2cBatchMatserVO.getBatchDate());
                m = 0;
                pstmtInsertNetworkStockTransaction.clearParameters();
                //Added by lalit to fix BUG DEF457 GP 6.6.1
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, o2cTransferID);
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, PretupsI.SALE_WALLET_TYPE);
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
                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating network stock TXN table",
                        "Approval level =  " + currentLevel + ", updateCount = " + updateCount);
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
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        " Approval level = " + currentLevel + ", updateCount  =" + updateCount);
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
                try{
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
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
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
                            updateCount = pstmtInsertUserDailyBalances.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                    "FAIL : DB Error while inserting user daily balances table", " Approval level = " + currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
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
                            errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                "FAIL : DB Error while updating user balances table for daily balance", "Approval level = " + currentLevel + ",updateCount=" + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                maxBalance = 0;
                isNotToExecuteQuery = false;
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, o2cBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, o2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                try{
                rs = null;
                rs = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                while (rs.next()) {
                    if ((walletForAdnlCmsn).equals(rs.getString("balance_type")) && isUserProductMultipleWallet) {
                        bonusBalance = rs.getLong("balance");
                    } else {
                        balance = rs.getLong("balance");
                    }

                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                
                if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    if (!batchO2CItemsVO.isCommCalReqd()) {
                        balance += batchO2CItemsVO.getRequestedQuantity();
                    } else {
                        balance += batchO2CItemsVO.getSecondApprQty();
                    }
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                if (bonusBalance > -1) {
                    bonusBalance += batchO2CItemsVO.getCommissionValue();
                }

                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, batchO2CItemsVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, o2cBatchMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                try
                {
                rs = null;
                rs = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(o2cBatchMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                    
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + currentLevel);
                    continue;
                }
                }
                finally{
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
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User Max balance reached",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the order
                // value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (balance < -1 && maxBalance < batchO2CItemsVO.getRequestedQuantity() || maxBalance < batchO2CItemsVO.getSecondApprQty()) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User Max balance reached",
                        "Approval level = " + currentLevel);
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
                        if (!batchO2CItemsVO.isCommCalReqd()) {
                            balance = batchO2CItemsVO.getRequestedQuantity();
                        } else {
                            balance = batchO2CItemsVO.getSecondApprQty();
                        }
                        ++m;
                        handlerStmt.setLong(m, 0);// previous balance
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                        // on
                        // date
                    }
                    if (isUserProductMultipleWallet) {
                        ++m;
                        handlerStmt.setLong(m, balance - batchO2CItemsVO.getCommissionValue());
                    } else {
                    	if(PretupsI.COMM_TYPE_POSITIVE.equals(batchO2CItemsVO.getDualCommissionType()))
                    	{
                    		++m;
                            handlerStmt.setLong(m, balance  + (batchO2CItemsVO.getCommissionValue() - batchO2CItemsVO.getTax3Value()));
                    	}
                    	else
                    	{
                    		++m;
                    		handlerStmt.setLong(m, balance);
                        }
                    }
                    ++m;
                    handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    // where
                    ++m;
                    handlerStmt.setString(m, o2cBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                    if (isUserProductMultipleWallet) {
                        ++m;
                        handlerStmt.setString(m, defaultWallet);
                        updateCount = handlerStmt.executeUpdate();
                        handlerStmt.clearParameters();
                        m = 0;
                        if (!(bonusBalance > -1)) {
                            ++m;
                            handlerStmt.setLong(m, 0);
                            ++m;
                            handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            bonusBalance = batchO2CItemsVO.getCommissionValue();
                        }
                        ++m;
                        handlerStmt.setLong(m, bonusBalance);
                        ++m;
                        handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                        ++m;
                        handlerStmt.setString(m, o2cTransferID);
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        handlerStmt.setString(m, channelUserVO.getUserID());
                        // where
                        ++m;
                        handlerStmt.setString(m, o2cBatchMatserVO.getProductCode());
                        ++m;
                        handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCode());
                        ++m;
                        handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                        ++m;
                        handlerStmt.setString(m, walletForAdnlCmsn);
                        if (updateCount > 0) {
                            updateCount = handlerStmt.executeUpdate();
                        }
                    }
                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                try
                {
                rs = null;
                rs = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(batchO2CItemsVO.getUserId());

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
                pstmtSelectProfileCounts.setString(m, batchO2CItemsVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, o2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                try{
                rs = null;
                rs = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
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
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Transfer profile not found",
                        "Approval level = " + currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
				
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.	
                  if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Daily transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getRequestedQuantity())) {
                    if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2c.batchapprovereject.msg.error.dailyinvaluereach"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Daily transfer in value reach",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Weekly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + batchO2CItemsVO.getRequestedQuantity())) {
                    if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2c.batchapprovereject.msg.error.weeklyinvaluereach"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Weekly transfer in value reach",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Monthly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + batchO2CItemsVO.getRequestedQuantity())) {
                    if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batcho2c.batchapprovereject.msg.error.monthlyinvaluereach"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Monthly transfer in value reach",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                
                countsVO.setUserID(channelUserVO.getUserID());
                countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    countsVO.setDailyInValue(countsVO.getDailyInValue() + batchO2CItemsVO.getRequestedQuantity());
                    countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + batchO2CItemsVO.getRequestedQuantity());
                    countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + batchO2CItemsVO.getRequestedQuantity());
                } else {
                    countsVO.setDailyInValue(countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty());
                    countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + batchO2CItemsVO.getSecondApprQty());
                    countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + batchO2CItemsVO.getSecondApprQty());
                }
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
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + currentLevel);
                    } else {
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, batchO2CItemsVO.getBatchDetailId());
                java.sql.Timestamp newlastModified = null;
                try{
                rs = null;
                rs = pstmtIsModified.executeQuery();
                // check record is modified or not
                if (rs.next()) {
                    newlastModified = rs.getTimestamp("modified_on");
                    
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Record is already modified",
                        "Approval level = " + currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Record is already modified",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1O2CBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                    psmtAppr1O2CBatchItem.clearParameters();
                    batchO2CItemsVO.setFirstApprovedBy(userID);
                    batchO2CItemsVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, o2cTransferID);
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
                    psmtAppr1O2CBatchItem.setLong(m, batchO2CItemsVO.getFirstApprovedQuantity());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1O2CBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2O2CBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
                    psmtAppr2O2CBatchItem.clearParameters();
                    batchO2CItemsVO.setSecondApprovedBy(userID);
                    batchO2CItemsVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, o2cTransferID);
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
                    if (!batchO2CItemsVO.isCommCalReqd()) {
                        ++m;
                        psmtAppr2O2CBatchItem.setLong(m, batchO2CItemsVO.getRequestedQuantity());
                    } else {
                        ++m;
                        psmtAppr2O2CBatchItem.setLong(m, batchO2CItemsVO.getFirstApprovedQuantity());
                    }
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2O2CBatchItem.executeUpdate();
                }

                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + currentLevel + ",updateCount=" + updateCount);
                    continue;
                }
                channelTransferVO.setCanceledOn(batchO2CItemsVO.getCancelledOn());
                channelTransferVO.setCanceledBy(batchO2CItemsVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(batchO2CItemsVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(batchO2CItemsVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(batchO2CItemsVO.getCommissionProfileVer());
                channelTransferVO.setDualCommissionType(batchO2CItemsVO.getDualCommissionType());
                channelTransferVO.setCreatedBy(batchO2CItemsVO.getInitiatedBy());
                channelTransferVO.setDomainCode(o2cBatchMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(batchO2CItemsVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(batchO2CItemsVO.getExtTxnNo());
                channelTransferVO.setFinalApprovedBy(batchO2CItemsVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(batchO2CItemsVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(batchO2CItemsVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(batchO2CItemsVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(batchO2CItemsVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(batchO2CItemsVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setBatchNum(batchO2CItemsVO.getBatchId());
                channelTransferVO.setBatchDate(o2cBatchMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax3(batchO2CItemsVO.getTax3Value()); // kuch
                // panga
                // hai
                channelTransferVO.setPayableAmount(batchO2CItemsVO.getPayableAmount());
                channelTransferVO.setNetPayableAmount(batchO2CItemsVO.getNetPayableAmount());
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(o2cBatchMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(batchO2CItemsVO.getCategoryCode());
                channelTransferVO.setReceiverGradeCode(batchO2CItemsVO.getGradeCode());
                channelTransferVO.setReceiverTxnProfile(batchO2CItemsVO.getTxnProfile());
                channelTransferVO.setReferenceNum(batchO2CItemsVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(batchO2CItemsVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(batchO2CItemsVO.getStatus());
                channelTransferVO.setThirdApprovedBy(batchO2CItemsVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(batchO2CItemsVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(batchO2CItemsVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                channelTransferVO.setTotalTax1(batchO2CItemsVO.getTax1Value());
                channelTransferVO.setTotalTax2(batchO2CItemsVO.getTax2Value());
                //batch o2c to be in synch with normal o2c
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE); 
                channelTransferVO.setTransferDate(batchO2CItemsVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(batchO2CItemsVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(batchO2CItemsVO.getTransferMrp());
                channelTransferVO.setPayInstrumentType(batchO2CItemsVO.getPaymentType());// @@@@
                channelTransferVO.setPayInstrumentNum(batchO2CItemsVO.getExtTxnNo());
                channelTransferVO.setPayInstrumentDate(batchO2CItemsVO.getExtTxnDate());
                channelTransferVO.setLevelOneApprovedQuantity(Long.toString(batchO2CItemsVO.getFirstApprovedQuantity()));
                channelTransferVO.setLevelTwoApprovedQuantity(Long.toString(batchO2CItemsVO.getFirstApprovedQuantity()));
                
                channelTransferItemVO = new ChannelTransferItemsVO();
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    channelTransferItemVO.setApprovedQuantity(batchO2CItemsVO.getRequestedQuantity());
                } else {
                    channelTransferItemVO.setApprovedQuantity(batchO2CItemsVO.getFirstApprovedQuantity());
                }
                channelTransferItemVO.setCommProfileDetailID(batchO2CItemsVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(batchO2CItemsVO.getCommissionRate());
                channelTransferItemVO.setCommType(batchO2CItemsVO.getCommissionType());
                channelTransferItemVO.setCommValue(batchO2CItemsVO.getCommissionValue());
                channelTransferItemVO.setCommQuantity(batchO2CItemsVO.getCommissionValue() - batchO2CItemsVO.getTax3Value());// hi
                channelTransferItemVO.setNetPayableAmount(batchO2CItemsVO.getNetPayableAmount());
                channelTransferItemVO.setPayableAmount(batchO2CItemsVO.getPayableAmount());
                channelTransferItemVO.setProductTotalMRP(batchO2CItemsVO.getTransferMrp());
                channelTransferItemVO.setProductCode(o2cBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    channelTransferItemVO.setRequiredQuantity(batchO2CItemsVO.getRequestedQuantity());
                    channelTransferItemVO.setRequestedQuantity(Double.toString(BTSLUtil.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())));
                } else {
                    channelTransferItemVO.setRequiredQuantity(batchO2CItemsVO.getFirstApprovedQuantity());
                    channelTransferItemVO.setRequestedQuantity(Double.toString(BTSLUtil.getDisplayAmount(batchO2CItemsVO.getFirstApprovedQuantity())));
                }
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(batchO2CItemsVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(batchO2CItemsVO.getTax1Type());
                channelTransferItemVO.setTax1Value(batchO2CItemsVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(batchO2CItemsVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(batchO2CItemsVO.getTax2Type());
                channelTransferItemVO.setTax2Value(batchO2CItemsVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(batchO2CItemsVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(batchO2CItemsVO.getTax3Type());
                channelTransferItemVO.setTax3Value(batchO2CItemsVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(o2cBatchMatserVO.getProductMrp());
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                //batch o2c synch with normal o2c
                channelTransferItemVO.setReceiverCreditQty(channelTransferItemVO.getApprovedQuantity()+ channelTransferItemVO.getCommQuantity());
                
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(o2cBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
                }
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
                }
                channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
        		if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() && channelTransferVO.getUserLoanVOList() !=null && channelTransferVO.getUserLoanVOList().size()>0) {

    				Map hashmap = ChannelTransferBL.checkUserLoanstatusAndAmount(con, channelTransferVO);
    				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
        				con.rollback();
        				final String args[] = { PretupsBL.getDisplayAmount((long)hashmap.get(PretupsI.WITHDRAW_AMOUNT)) };
    					
        				errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
        						"batcho2c.batchapprovereject.msg.error.loanPending",args));
        				errorList.add(errorVO);
        				BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Loan Status PENDING FOR USER",
        						"Approval level = " + currentLevel);
        				continue;
        			}

    				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    					UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
    					userLoanWithdrawBL.autoChannelLoanSettlement(channelTransferVO, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
    				}

    			}
                
                else {
                	/* SOS validation*/
		            Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(con, countsVO, channelTransferVO);
		             if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true))
		             {
		             	con.rollback();
		                 errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
		                     "batcho2c.batchapprovereject.msg.error.sosPending"));
		                 errorList.add(errorVO);
		                 BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : SOS Status PENDING FOR USER",
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
		 				channelTransferVO.setLrWithdrawAmt((Long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
		 				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
		 			}
     			
                }
     			//Validate MRP && Successive Block for channel transaction
				long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C)).longValue();
				try {
					ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, new Date(), successiveReqBlockTime4ChnlTxn);					
				} catch (Exception e) {
					String args[] = {channelTransferVO.getUserMsisdn(), PretupsBL.getDisplayAmount(channelTransferVO.getTransferMRP()),String.valueOf(successiveReqBlockTime4ChnlTxn/60)};
					errorVO=new ListValueVO(batchO2CItemsVO.getMsisdn(),String.valueOf(batchO2CItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batcho2c.batchapprovereject.msg.error.mrpblocktimeout",args));
					errorList.add(errorVO);
					BatchO2CProcessLog.detailLog("closeOrederByBatch",o2cBatchMatserVO,batchO2CItemsVO,"FAIL : Validate MRP && Successive Block for channel transaction","Approval level = "+currentLevel);
					continue;					
				}
                final boolean debit = true;
                if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(channelTransferVO.getTransferType())) {
                    ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(con, channelTransferVO, channelTransferVO.getFromUserID(), date, debit);
                    ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(con, channelTransferVO, channelTransferVO.getFromUserID(), date);
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
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemsVO.getStatus());
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
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemsVO.getMsisdn());
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
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getPayInstrumentType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getPayInstrumentNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getPayInstrumentDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, Long.parseLong(channelTransferVO.getLevelOneApprovedQuantity()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, Long.parseLong(channelTransferVO.getLevelTwoApprovedQuantity()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemsVO.getDualCommissionType());   
                
                // ends here
                // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + currentLevel + ", update Count=" + updateCount);
                    continue;
                }
                
                //Target based base commission calculation and User's OTF counts update
                
            	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
        		{
        			ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
        			// to calculate commission and tax
        			channelTransferVO.setOtfFlag(true);

        		       	 final ArrayList<ChannelTransferItemsVO> list = new ArrayList<ChannelTransferItemsVO>();
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
        				
        				
        			ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);
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
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getAfterTransSenderPreviousStock());
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
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity() + channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getAfterTransSenderPreviousStock() - channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getAfterTransReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity() + channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getOtfTypePctOrAMt());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getOtfRate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getOtfAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m , channelTransferItemVO.isOtfApplicable());
                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while inserting in channel transfer items table",
                        "Approval level = " + currentLevel + ", update Count=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                // user life cycle
                if (batchO2CItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        int updatecount = 0;
                        // int
                        // updatecount=operatorUtili.changeUserStatusToActive(

                        final String str[] = txnSenderUserStatusChang.split(","); 
                        String newStatus[] = null;
                        for (int i = 0; i < str.length; i++) {
                            newStatus = str[i].split(":");
                            if (newStatus[0].equals(channelUserVO.getStatus())) {
                                updatecount = operatorUtili.changeUserStatusToActive(con, channelTransferVO.getToUserID(), channelUserVO.getStatus(), newStatus[1]);
                                break;
                            }
                        }
                        if (updatecount > 0) {
                            con.commit();
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "PASS : Order is closed successfully",
                                "Approval level = " + currentLevel + ", updateCount= " + updateCount);
                        } else {
                            con.rollback();
                            throw new BTSLBaseException(this, methodName, "error.status.updating");
                        }
                    } else {

                        con.commit();
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "PASS : Order is closed successfully",
                            "Approval level = " + currentLevel + ", updateCount =" + updateCount);

                    }

                } else {
                    con.commit();

                }

                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, o2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                try{
                rs = null;
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
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_OPT_CHNL_TRANSFER_SMS2);
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
                        keyArgumentVO.setKey(PretupsErrorCodesI.O2C_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String o2cNotifyMsg = null;
                final long receiverPostBalance=channelTransferItemVO.getAfterTransReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity() + channelTransferItemVO.getCommQuantity();
                
                if (isO2CSmsNotify) {
                	 final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        o2cNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        o2cNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    array = new String[] { channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity()),channelTransferItemVO.getProductCode(),
                    		PretupsBL.getDisplayAmount(channelTransferItemVO.getNetPayableAmount()),PretupsBL.getDisplayAmount(receiverPostBalance), o2cNotifyMsg };
                }

                if (o2cNotifyMsg == null) {
                    array = new String[] { channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity()), channelTransferItemVO.getProductCode() ,
                    		PretupsBL.getDisplayAmount(channelTransferItemVO.getNetPayableAmount()), PretupsBL.getDisplayAmount(receiverPostBalance)};
                }

                if (isLmsAppl) {

                    try {
                        final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                        final LoyaltyVO loyaltyVO = new LoyaltyVO();
                        PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
                        final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                        final ArrayList arr = new ArrayList();
                        loyaltyVO.setModuleType(PretupsI.O2C_MODULE);
                        loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
                        loyaltyVO.setTransferamt(channelTransferVO.getRequestedQuantity());
                        loyaltyVO.setCategory(channelTransferVO.getCategoryCode());
                        loyaltyVO.setFromuserId(channelTransferVO.getFromUserID());
                        loyaltyVO.setTouserId(channelTransferVO.getToUserID());
                        loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
                        loyaltyVO.setTxnId(channelTransferVO.getTransferID());
                        loyaltyVO.setCreatedOn(channelTransferVO.getCreatedOn());
                        loyaltyVO.setSenderMsisdn(channelTransferVO.getFromUserCode());
                        loyaltyVO.setReciverMsisdn(channelTransferVO.getToUserCode());
                        loyaltyVO.setProductCode(channelTransferVO.getProductCode());
                        arr.add(loyaltyVO.getFromuserId());
                        arr.add(loyaltyVO.getTouserId());
                        promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
                        loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                        loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

                        if (loyaltyVO.getSetId() == null && loyaltyVO.getToSetId() == null) {
                            log.error("process", "Exception during LMS Module.SetId not found");
                        } else {
                            _loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(), loyaltyVO);
                        }

                    } catch (Exception ex) {
                        log.error("process", "Exception durign LMS Module " + ex.getMessage());
                        log.errorTrace(methodName, ex);

                    }

                }
                messages = new BTSLMessages(PretupsErrorCodesI.O2C_OPT_CHNL_TRANSFER_SMS1, array);
                pushMessage = new PushMessage(batchO2CItemsVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
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
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[closeOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch] ", "", "", "",
                "Exception:" + ex.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralException);
        } finally {
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
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN;
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
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrederByBatch]",
                            "", "", "", "Error while updating O2C_BATCHES table. Batch id=" + batch_ID);
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
                log.error(methodName, " SQLException : " + sqe);
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + currentLevel);
                thorwBTSLBaseException(methodName, errorGeneralException);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Exception : " + ex);
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());
                BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + currentLevel);
                thorwBTSLBaseException(methodName, errorGeneralException);
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
                log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * updateBatchStatus
     * This method is to update the status of O2C_BATCHES table
     * 
     * @param con
     * @param batchID
     * @param newStatus
     * @param oldStatus
     * @return
     * @throws BTSLBaseException
     *             boolean
     */
    public int updateBatchO2CStatus(Connection con, String batchID, String newStatus, String oldStatus) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug(" updateBatchO2CStatus", "Entered   p_batchID " + batchID + " p_newStatus=" + newStatus + " p_oldStatus=" + oldStatus);
        }
        PreparedStatement pstmt = null;
        int updateCount = -1;
        final String methodName = "updateBatchO2CStatus";
        try {
            final StringBuilder sqlBuffer = new StringBuilder("UPDATE o2c_batches SET status=? ");
            sqlBuffer.append(" WHERE batch_id=? AND status=? ");
            final String updateO2CBatches = sqlBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY updateO2CBatches=" + updateO2CBatches);
            }

            pstmt = con.prepareStatement(updateO2CBatches);
            int i = 1;
            pstmt.setString(i, newStatus);
            i++;
            pstmt.setString(i, batchID);
            i++;
            pstmt.setString(i, oldStatus);
            i++;
            updateCount = pstmt.executeUpdate();
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[updateBatchO2CStatus]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", errorGeneralSqlException);
        } catch (Exception ex) {
            log.error("updateBatchStatus", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[updateBatchO2CStatus]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralException);
        } finally {
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:  updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method to cancel/approve the batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are: o2c_batch_items,o2c_batches
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
    public ArrayList processOrderByBatch(Connection con, LinkedHashMap dataMap, String currentLevel, String userID, MessageResources messages, Locale locale, String smsDefaultLang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByBatch";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_dataMap=" + dataMap + " p_currentLevel=" + currentLevel + " p_locale=" + locale + " p_userID=" + userID);
        }
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement psmtCancelO2CBatchItem = null;
        PreparedStatement psmtAppr1O2CBatchItem = null;
        PreparedStatement psmtAppr1withCalc = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtSelectItemsDetails = null;

        PreparedStatement pstmtIsModified = null;
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
        StringBuilder sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg , u.network_code,u.category_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;
        // after validating if request is to cancle the order, the below query
        // is used.
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, ");
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
            log.debug(methodName, "QUERY sqlCancelO2CBatchItems=" + sqlCancelO2CBatchItems);
        }
        sqlBuffer = null;

        // after validating if request is of level 1 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,approved1_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv1O2CBatchItems=" + sqlApprv1O2CBatchItems);
        }
        sqlBuffer = null;

        // after if calculations are changed
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=? , first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,payment_type=?, ");
        sqlBuffer.append(" commission_type=?, commission_rate=?, commission_value=?, tax1_type=?, tax1_rate=?, ");
        sqlBuffer.append(" tax1_value=?, tax2_type=?, tax2_rate=?, tax2_value=?, tax3_type=?, tax3_rate=?, ");
        sqlBuffer.append(" tax3_value=?, transfer_mrp=?,payable_amount=?, net_payable_amount=?,commission_profile_detail_id=?,approved1_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String appr1withcalc = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv2O2CBatchItems=" + appr1withcalc);
        }

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
    
        final String selectItemsDetails = batchO2CTransferWebQry.processOrderByBatchItemsDetailsQry();
      
        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE o2c_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateO2CBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateO2CBatches=" + updateO2CBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM o2c_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;
        Date date = null;
        try {
            BatchO2CItemsVO batchO2CItemsVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = con.prepareStatement(sqlLoadUser);
            psmtCancelO2CBatchItem =  con.prepareStatement(sqlCancelO2CBatchItems);
            psmtAppr1O2CBatchItem =  con.prepareStatement(sqlApprv1O2CBatchItems);
            psmtAppr1withCalc =  con.prepareStatement(appr1withcalc);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster =  con.prepareStatement(updateO2CBatches);
            pstmtIsModified = con.prepareStatement(isModified);
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
                    channelUserVO = ChannelUserVO.getInstance();
                    channelUserVO.setUserID(batchO2CItemsVO.getUserId());
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
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2c.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : User is not active", " Approval level " + currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2c.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Commission profile is suspend", " Approval level " + currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2c.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Transfer profile is suspend", "Approval level " + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                            "batcho2c.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : User is IN suspend", " Approval level" + currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : User not found", "Approval level " + currentLevel);
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
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Record is already modified by some one else", " Approval level" + currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Record is already modified by some one else", "Approval level" + currentLevel);
                    continue;

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
                    }
                    // added
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, batchO2CItemsVO.getSecondApproverRemarks());
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
                    }
                    // added
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } 
                    updateCount = psmtCancelO2CBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1O2CBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(batchO2CItemsVO.getStatus()) && !batchO2CItemsVO.isCommCalReqd()) {
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
                    psmtAppr1O2CBatchItem.setLong(m, batchO2CItemsVO.getRequestedQuantity());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1O2CBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr2O2CBatchItem
                else if (batchO2CItemsVO.isCommCalReqd()) {
                    psmtAppr1withCalc.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1withCalc.setString(m, userID);
                    ++m;
                    psmtAppr1withCalc.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1withCalc.setString(m, userID);
                    ++m;
                    psmtAppr1withCalc.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getStatus());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getExtTxnNo());
                    ++m;
                    psmtAppr1withCalc.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getPaymentType());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getCommissionType());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getCommissionRate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getCommissionValue());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getTax1Type());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getTax1Rate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTax1Value());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getTax2Type());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getTax2Rate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTax2Value());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getTax3Type());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getTax3Rate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTax3Value());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTransferMrp());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getPayableAmount());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getNetPayableAmount());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getCommissionProfileDetailId());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getFirstApprovedQuantity());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getBatchDetailId());

                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(batchO2CItemsVO.getStatus())) {
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else {
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtAppr1withCalc.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), messages.getMessage(locale,
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : DB Error while updating items table",
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
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO [processOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());

            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO [processOrderByBatch]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralException);
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
                if (psmtAppr1withCalc != null) {
                    psmtAppr1withCalc.close();
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
                rs2 = pstmtSelectItemsDetails.executeQuery();
                if (rs2.next()) {
                    final int totalCount = rs2.getInt("batch_total_record");
                    final int closeCount = rs2.getInt("closed");
                    final int cnclCount = rs2.getInt("cncl");
                    String statusOfMaster = null;
                    // If all records are canle then set cancelled in master
                    // table
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                        // Otherwise set OPEN in mastrer table
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN;
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
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "", messages.getMessage(locale, "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);

                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[processOrderByBatch] ",
                            "", "", "", "Error while updating O2C_BATCHES table. Batch id=" + batch_ID);
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
                log.error(methodName, "SQLException : " + sqe);
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[processOrderByBatch] ", "", "",
                    "", "SQL Exception:" + sqe.getMessage());

                thorwBTSLBaseException(methodName, errorGeneralSqlException);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Exception : " + ex);
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[processOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());

                thorwBTSLBaseException(methodName, errorGeneralException);
            }finally{
            	try {
                    if (rs2 != null) {
                        rs2.close();
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
                log.debug(methodName, "Exiting: errorList size=" + errorList.size());
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
    public boolean isBatchO2CModified(Connection con, long oldlastModified, String batchID) throws BTSLBaseException {
        final String methodName = "isBatchO2CModified";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:p_oldlastModified=" + oldlastModified + ",p_batchID=" + batchID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM o2c_batches WHERE batch_id = ? ";
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
            log.error(methodName, "SQLException:" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[isBatchO2CModified]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception:" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[isBatchO2CModified]", "", "", "",
                "Exception:" + e.getMessage());
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
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * isPendingO2CTransactionExist
     * This method is to check that the user has any pending request of O2C batch transfer
     * or not
     * 
     * @param con
     * @param userID
     * @return
     * @throws BTSLBaseException
     *             boolean
     */
    public boolean isPendingO2CTransactionExist(Connection con, String userID) throws BTSLBaseException {
        final String methodName = "isPendingO2CTransactionExist";
        if (log.isDebugEnabled()) {
        	log.debug(methodName, "Entered   p_userID " + userID);
        }
        boolean isExist = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append(" SELECT 1  ");
            strBuff.append(" FROM o2c_batch_items ");
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
            throw new BTSLBaseException(this, "", errorGeneralSqlException);
        } catch (Exception ex) {
        	log.error(methodName, exception + ex);
        	log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferDAO[isPendingTransactionExist]", "",
                "", "", exception + ex.getMessage());
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
            	log.debug(methodName, "Exiting:  isExist=" + isExist);
            }
        }
        return isExist;
    }

    
    private void thorwBTSLBaseException(String methodName, String messageKey) throws  BTSLBaseException{
    	  throw new BTSLBaseException(this, methodName, messageKey);
    }
    
    /**
     * @param con
     * @param batchMasterVO
     * @param batchItemsList
     * @param locale
     * @param trfType
     * @param trfSubType
     * @param batchO2Cmap
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList initiateO2CBatchTransfer(Connection con, O2CBatchMasterVO batchMasterVO, ArrayList batchItemsList, Locale locale, String trfType, String trfSubType, HashMap<String,ChannelTransferItemsVO> batchO2Cmap) throws BTSLBaseException {
        final String methodName = "initiateO2CBatchTransfer";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered.... p_batchMasterVO=" + batchMasterVO + ", p_batchItemsList.size() = " + batchItemsList.size() + ", p_batchItemsList=" + batchItemsList + "p_locale=" + locale + ", p_trfType=" + trfType + ", p_trfSubType=" + trfSubType);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        Boolean isPaymentModeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD);
        Boolean isTransactionTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        final ArrayList errorList = new ArrayList();
        BatchO2CItemsVO batchO2CItemsVO = null;
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM o2c_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }
        // for loading the O2C transfer rule for O2C batch transfer
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuilder strBuffSelectTrfRule = new StringBuilder(" SELECT transfer_rule_id ");
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
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.payment_mode ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        if (isTransactionTypeAlwd)
    	{ 
        strBuffSelectCProfileProd.append("AND cp.transaction_type in ( ? , ? ) ");
    	}
        else
        	 {
        	strBuffSelectCProfileProd.append("AND cp.transaction_type = ? ");
        	 }
        strBuffSelectCProfileProd.append(" AND cp.payment_mode in (?,?) ORDER BY cp.TRANSACTION_TYPE desc");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffSelectCProfileProd Query =" + strBuffSelectCProfileProd);
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
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
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO o2c_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO o2c_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO o2c_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems
            .append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status, payment_type, payable_amount, net_payable_amount,dual_comm_type) ");
        strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffInsertBatchItems Query =" + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuilder strBuffUpdateBatchMaster = new StringBuilder("UPDATE o2c_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectTrfRule = con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = con.prepareStatement(strBuffSelectTProfileProd.toString());

            pstmtInsertBatchMaster =  con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = con.prepareStatement(strBuffInsertBatchGeo.toString());
            pstmtInsertBatchItems =  con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = con.prepareStatement(strBuffUpdateBatchMaster.toString());
            ChannelTransferRuleVO rulesVO = null;
            int index = 0;

            final HashMap transferRuleMap = new HashMap();
            final HashMap transferRuleNotExistMap = new HashMap();
            final HashMap transferRuleProdNotExistMap = new HashMap();
            final HashMap transferProfileMap = new HashMap();
            long requestedValue = 0;
            long minTrfValue = 0;
            long maxTrfValue = 0;
            long multipleOf = 0;
            String comProductId = "";
            Double discountRate = 0.0;
            String discountType = "";
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
                BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "query Execution Count=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO [initiateBatchO2CTransfer] ",
                    "", "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
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
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    log.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "query Execution Count=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "BatchO2CTransferDAO[initiateBatchO2CTransfer] ", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            String msgArr[] = null;
            for (int i = 0, j = batchItemsList.size(); i < j; i++) {
                batchO2CItemsVO = (BatchO2CItemsVO) batchItemsList.get(i);
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchO2CItemsVO.getExtTxnNo()) && isExternalTxnUnique) {
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, batchO2CItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID1 = pstmtSelectExtTxnID1.executeQuery();
                    pstmtSelectExtTxnID1.clearParameters();
                    if (rsSelectExtTxnID1.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(batchO2CItemsVO.getMsisdn());
                        errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                        errorVO.setOtherInfo("batcho2c.initiatebatcho2ctransfer.msg.error.exttxnalreadyexists");
                        errorVO.setOtherInfo2(PretupsRestUtil.getMessageString("batcho2c.initiatebatcho2ctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : External txn number already exist O2C BATCH", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // load the product's informaiton.
                if (transferRuleNotExistMap.get(batchO2CItemsVO.getCategoryCode()) == null) {
                    if (transferRuleProdNotExistMap.get(batchO2CItemsVO.getCategoryCode()) == null) {
                        if (transferRuleMap.get(batchO2CItemsVO.getCategoryCode()) == null) {
                            index = 0;
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getNetworkCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchMasterVO.getDomainCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchO2CItemsVO.getCategoryCode());
                            rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
                            pstmtSelectTrfRule.clearParameters();
                            if (rsSelectTrfRule.next()) {
                                rulesVO = new ChannelTransferRuleVO();
                                rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
                                index = 0;
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, rulesVO.getTransferRuleID());
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, batchMasterVO.getProductCode());
                                rsSelectTrfRuleProd = pstmtSelectTrfRuleProd.executeQuery();
                                pstmtSelectTrfRuleProd.clearParameters();
                                if (!rsSelectTrfRuleProd.next()) {
                                    transferRuleProdNotExistMap.put(batchO2CItemsVO.getCategoryCode(), batchO2CItemsVO.getCategoryCode());
                                    // put error log Prodcuct is not in the
                                    // transfer rule
                                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), "batcho2c.initiatebatcho2ctransfer.msg.error.prodnotintrfrule" , PretupsRestUtil.getMessageString(
                                        "batcho2c.initiatebatcho2ctransfer.msg.error.prodnotintrfrule"));
                                    errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                                    errorList.add(errorVO);
                                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchO2CItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchO2CItemsVO.getCategoryCode(), batchO2CItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), "batcho2c.initiatebatcho2ctransfer.msg.error.trfrulenotdefined", PretupsRestUtil.getMessageString(
                                    "batcho2c.initiatebatcho2ctransfer.msg.error.trfrulenotdefined"));
                                errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(),"batcho2c.initiatebatcho2ctransfer.msg.error.prodnotintrfrule", PretupsRestUtil.getMessageString(
                            "batcho2c.initiatebatcho2ctransfer.msg.error.prodnotintrfrule"));
                        errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(),"batcho2c.initiatebatcho2ctransfer.msg.error.trfrulenotdefined", PretupsRestUtil.getMessageString(
                        "batcho2c.initiatebatcho2ctransfer.msg.error.trfrulenotdefined"));
                    errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchO2CItemsVO.getCategoryCode());
                // check the transfer profile product code

                // transfer profile check ends here
                if (transferProfileMap.get(batchO2CItemsVO.getTxnProfile()) == null) {
                    index = 0;
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchO2CItemsVO.getTxnProfile());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchMasterVO.getProductCode());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    rsSelectTProfileProd = pstmtSelectTProfileProd.executeQuery();
                    pstmtSelectTProfileProd.clearParameters();
                    if (!rsSelectTProfileProd.next()) {
                        transferProfileMap.put(batchO2CItemsVO.getTxnProfile(), "false");
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), "batcho2c.initiatebatcho2ctransfer.msg.error.trfprofilenotdefined", PretupsRestUtil.getMessageString(
                            "batcho2c.initiatebatcho2ctransfer.msg.error.trfprofilenotdefined"));
                        errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchO2CItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchO2CItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), "batcho2c.initiatebatcho2ctransfer.msg.error.trfprofilenotdefined", PretupsRestUtil.getMessageString(
                            "batcho2c.initiatebatcho2ctransfer.msg.error.trfprofilenotdefined"));
                        errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                }

                // check the commisson profile applicability and other checks
                // related to the commission profile
                index = 0;
                ++index;
                pstmtSelectCProfileProd.setString(index, batchMasterVO.getProductCode());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchO2CItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtSelectCProfileProd.setString(index, batchO2CItemsVO.getCommissionProfileVer());
                if (isTransactionTypeAlwd)
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
                pstmtSelectCProfileProd.setString(index, (isTransactionTypeAlwd && isPaymentModeAlwd)?batchO2CItemsVO.getPaymentType():PretupsI.ALL);
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                int rowcount = 0;
           	   while(rsSelectCProfileProd.next()){
           		 rowcount= rsSelectCProfileProd.getRow();
           		 if(rowcount==1)
           		 {
           			 minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                        maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                        multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                        comProductId = rsSelectCProfileProd.getString("comm_profile_products_id");
                        discountRate=rsSelectCProfileProd.getDouble("discount_rate");
                        discountType=rsSelectCProfileProd.getString("discount_type");
           		 }
                	if(batchO2CItemsVO.getPaymentType().equals(rsSelectCProfileProd.getString("payment_mode"))){
                		 minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                         maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                         multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                         comProductId = rsSelectCProfileProd.getString("comm_profile_products_id");
                         discountRate=rsSelectCProfileProd.getDouble("discount_rate");
                         discountType=rsSelectCProfileProd.getString("discount_type");
                	}
                }
           
                if (rowcount==0) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(),"batcho2c.initiatebatcho2ctransfer.msg.error.commprfnotdefined", PretupsRestUtil.getMessageString(
                        "batcho2c.initiatebatcho2ctransfer.msg.error.commprfnotdefined"));
                    errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Commission profile for this product is not defined", "");
                    continue;
                }
                
                requestedValue = batchO2CItemsVO.getRequestedQuantity();
                if (minTrfValue > requestedValue || maxTrfValue < requestedValue) {
                    msgArr = new String[3];
                    msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                    msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                    msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                    // put error requested quantity is not between min and max
                    // values
                    String messages = PretupsRestUtil
    						.getMessageString("batcho2c.initiatebatcho2ctransfer.msg.error.qtymaxmin");
    				messages = messages.replace("{0}", msgArr[0]);
    				messages = messages.replace("{1}", msgArr[1]);
    				messages = messages.replace("{2}", msgArr[2]);
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), "batcho2c.initiatebatcho2ctransfer.msg.error.qtymaxmin", messages);
                    errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                    msgArr = null;
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Requested quantity is not between min and max values",
                        "minTrfValue=" + minTrfValue + ", maxTrfValue=" + maxTrfValue);
                    continue;
                }
               
                if (requestedValue % multipleOf != 0) {
                    // put error requested quantity is not multiple of
                	 String messages = PretupsRestUtil.getMessageString("batcho2c.initiatebatcho2ctransfer.msg.error.notmulof");
     				messages = messages.replace("{0}", PretupsBL.getDisplayAmount(multipleOf));
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), "batcho2c.initiatebatcho2ctransfer.msg.error.notmulof", messages);
                    errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Requested quantity is not in multiple value",
                        "multiple of=" + multipleOf);
                    continue;
                }

                index = 0;
                ++index;
                pstmtSelectCProfileProdDetail.setString(index, comProductId);
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail.executeQuery();
                pstmtSelectCProfileProdDetail.clearParameters();
                if (!rsSelectCProfileProdDetail.next()) {
                    // put error commission profile slab is not define for the
                    // requested value
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), "batcho2c.initiatebatcho2ctransfer.msg.error.commslabnotdefined", PretupsRestUtil.getMessageString(
                        "batcho2c.initiatebatcho2ctransfer.msg.error.commslabnotdefined"));
                    errorVO.setIDValue(String.valueOf(batchO2CItemsVO.getRecordNumber()));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : Commission profile slab is not define for the requested value", "");
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

                channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail.getDouble("commission_rate"));
                channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail.getString("commission_type"));

                channelTransferItemsVO.setDiscountRate(discountRate);
                channelTransferItemsVO.setDiscountType(discountType);

                channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail.getDouble("tax1_rate"));
                channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail.getString("tax1_type"));

                channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail.getDouble("tax2_rate"));
                channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail.getString("tax2_type"));

                channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail.getDouble("tax3_rate"));
                channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail.getString("tax3_type"));

                transferItemsList.add(channelTransferItemsVO);

                // make a new channel TransferVO to transfer into the method
                // during tax calculataion
                final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setDualCommissionType(batchO2CItemsVO.getDualCommissionType());
                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);

                // taxes on O2C required
                // ends commission profile validaiton

                // insert items data here
                index = 0;
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getBatchId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getBatchDetailId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getCategoryCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getMsisdn());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getUserId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getStatus());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getModifiedBy());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getModifiedOn()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getUserGradeCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getExtTxnNo());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getExtTxnDate()));
                ++index;
                pstmtInsertBatchItems.setDate(index, BTSLUtil.getSQLDateFromUtilDate(batchO2CItemsVO.getTransferDate()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getTxnProfile());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getCommissionProfileVer());
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
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_ITEM_RCRDSTATUS_PROCESSED);
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getPaymentType());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getPayableAmount());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getNetPayableAmount());
                ++index;
                pstmtInsertBatchItems.setString(index, batchO2CItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    con.rollback();
                    // put error record can not be inserted
                    log.error(methodName, "Record cannot be inserted in batch items table");
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchO2CProcessLog.detailLog(methodName, batchMasterVO, batchO2CItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here
                channelTransferItemsVO.setLoginId(batchO2CItemsVO.getLoginID());
                channelTransferItemsVO.setMsisdn(batchO2CItemsVO.getMsisdn());
                channelTransferItemsVO.setUserCategory(batchO2CItemsVO.getUserCategory());
                channelTransferItemsVO.setGradeName(batchO2CItemsVO.getGradeName());
                channelTransferItemsVO.setPaymentType(batchO2CItemsVO.getPaymentType());
                channelTransferItemsVO.setInitiatorRemarks(batchO2CItemsVO.getInitiatorRemarks());
                channelTransferItemsVO.setExtTxnNo(batchO2CItemsVO.getExtTxnNo());
                channelTransferItemsVO.setExtTxnDate(batchO2CItemsVO.getExtTxnDate());
                channelTransferItemsVO.setExternalCode(batchO2CItemsVO.getExternalCode());
                channelTransferItemsVO.setCommissionProfileSetId(batchO2CItemsVO.getCommissionProfileSetId());
                channelTransferItemsVO.setCommissionProfileVer(batchO2CItemsVO.getCommissionProfileVer());

                batchO2Cmap.put(batchO2CItemsVO.getBatchDetailId(), channelTransferItemsVO);
            } // for loop for the batch items
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException :" + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", " SQLException:" + sqe.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, " FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            log.error(methodName, " Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[initiateBatchO2CTransfer]", "",
                "", "", "Exception:" + ex.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : Exception : " + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, errorGeneralException);
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
                    BatchO2CProcessLog.o2cBatchMasterLog(methodName, batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        log.error(methodName, "Unable to Update the batch size in master table..");
                        con.rollback();
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "BatchO2CTransferDAO[initiateBatchO2CTransfer]", "", "", "", "Error while updating O2C_BATCHES table. Batch id=" + batchMasterVO.getBatchId());
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
     * Method to cancel/approve the batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are: o2c_batch_items,o2c_batches
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
    public ArrayList processOrderByBatch(Connection con, LinkedHashMap dataMap, String currentLevel, String userID, Locale locale, String smsDefaultLang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByBatch";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_dataMap=" + dataMap + " p_currentLevel=" + currentLevel + " p_locale=" + locale + " p_userID=" + userID);
        }
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement psmtCancelO2CBatchItem = null;
        PreparedStatement psmtAppr1O2CBatchItem = null;
        PreparedStatement psmtAppr1withCalc = null;
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtSelectItemsDetails = null;

        PreparedStatement pstmtIsModified = null;
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
        StringBuilder sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg , u.network_code,u.category_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;
        // after validating if request is to cancle the order, the below query
        // is used.
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, ");
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
            log.debug(methodName, "QUERY sqlCancelO2CBatchItems=" + sqlCancelO2CBatchItems);
        }
        sqlBuffer = null;

        // after validating if request is of level 1 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,approved1_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv1O2CBatchItems=" + sqlApprv1O2CBatchItems);
        }
        sqlBuffer = null;

        // after if calculations are changed
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=? , first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,payment_type=?, ");
        sqlBuffer.append(" commission_type=?, commission_rate=?, commission_value=?, tax1_type=?, tax1_rate=?, ");
        sqlBuffer.append(" tax1_value=?, tax2_type=?, tax2_rate=?, tax2_value=?, tax3_type=?, tax3_rate=?, ");
        sqlBuffer.append(" tax3_value=?, transfer_mrp=?,payable_amount=?, net_payable_amount=?,commission_profile_detail_id=?,approved1_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String appr1withcalc = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv2O2CBatchItems=" + appr1withcalc);
        }

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
    
        final String selectItemsDetails = batchO2CTransferWebQry.processOrderByBatchItemsDetailsQry();
      
        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuilder("UPDATE o2c_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateO2CBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateO2CBatches=" + updateO2CBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM o2c_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;
        Date date = null;
        try {
            BatchO2CItemsVO batchO2CItemsVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = con.prepareStatement(sqlLoadUser);
            psmtCancelO2CBatchItem =  con.prepareStatement(sqlCancelO2CBatchItems);
            psmtAppr1O2CBatchItem =  con.prepareStatement(sqlApprv1O2CBatchItems);
            psmtAppr1withCalc =  con.prepareStatement(appr1withcalc);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster =  con.prepareStatement(updateO2CBatches);
            pstmtIsModified = con.prepareStatement(isModified);
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
                    channelUserVO = ChannelUserVO.getInstance();
                    channelUserVO.setUserID(batchO2CItemsVO.getUserId());
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
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : User is not active", " Approval level " + currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Commission profile is suspend", " Approval level " + currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Transfer profile is suspend", "Approval level " + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : User is IN suspend", " Approval level" + currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : User not found", "Approval level " + currentLevel);
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
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Record is already modified by some one else", " Approval level" + currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : Record is already modified by some one else", "Approval level" + currentLevel);
                    continue;

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
                    }
                    // added
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, batchO2CItemsVO.getSecondApproverRemarks());
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
                    }
                    // added
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel) || PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelO2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } 
                    updateCount = psmtCancelO2CBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1O2CBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(batchO2CItemsVO.getStatus()) && !batchO2CItemsVO.isCommCalReqd()) {
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
                    psmtAppr1O2CBatchItem.setLong(m, batchO2CItemsVO.getRequestedQuantity());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1O2CBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr2O2CBatchItem
                else if (batchO2CItemsVO.isCommCalReqd()) {
                    psmtAppr1withCalc.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1withCalc.setString(m, userID);
                    ++m;
                    psmtAppr1withCalc.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1withCalc.setString(m, userID);
                    ++m;
                    psmtAppr1withCalc.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getStatus());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getExtTxnNo());
                    ++m;
                    psmtAppr1withCalc.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getPaymentType());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getCommissionType());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getCommissionRate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getCommissionValue());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getTax1Type());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getTax1Rate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTax1Value());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getTax2Type());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getTax2Rate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTax2Value());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getTax3Type());
                    ++m;
                    psmtAppr1withCalc.setDouble(m, batchO2CItemsVO.getTax3Rate());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTax3Value());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getTransferMrp());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getPayableAmount());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getNetPayableAmount());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getCommissionProfileDetailId());
                    ++m;
                    psmtAppr1withCalc.setLong(m, batchO2CItemsVO.getFirstApprovedQuantity());
                    ++m;
                    psmtAppr1withCalc.setString(m, batchO2CItemsVO.getBatchDetailId());

                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(batchO2CItemsVO.getStatus())) {
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else {
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtAppr1withCalc.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtAppr1withCalc.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.o2cBatchItemLog(methodName, batchO2CItemsVO, "FAIL : DB Error while updating items table",
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
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO [processOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());

            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO [processOrderByBatch]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralException);
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
                if (psmtAppr1withCalc != null) {
                    psmtAppr1withCalc.close();
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
                rs2 = pstmtSelectItemsDetails.executeQuery();
                if (rs2.next()) {
                    final int totalCount = rs2.getInt("batch_total_record");
                    final int closeCount = rs2.getInt("closed");
                    final int cnclCount = rs2.getInt("cncl");
                    String statusOfMaster = null;
                    // If all records are canle then set cancelled in master
                    // table
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                        // Otherwise set OPEN in mastrer table
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN;
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
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "",PretupsRestUtil.getMessageString( "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);

                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[processOrderByBatch] ",
                            "", "", "", "Error while updating O2C_BATCHES table. Batch id=" + batch_ID);
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
                log.error(methodName, "SQLException : " + sqe);
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[processOrderByBatch] ", "", "",
                    "", "SQL Exception:" + sqe.getMessage());

                thorwBTSLBaseException(methodName, errorGeneralSqlException);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Exception : " + ex);
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[processOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());

                thorwBTSLBaseException(methodName, errorGeneralException);
            }finally{
            	try {
                    if (rs2 != null) {
                        rs2.close();
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
                log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    }
    

    /**
     * Method to close the o2c order by batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,o2c_batch_items,
     * o2c_batches,
     * channel_transfers_items,channel_transfers
     * 
     * @param con
     * @param dataMap
     * @param currentLevel
     * @param userID
     * @param o2cBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatch(Connection con, LinkedHashMap dataMap, String currentLevel, String userID, O2CBatchMasterVO o2cBatchMatserVO, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "closeOrderByBatch";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_dataMap=" + dataMap + " p_currentLevel=" + currentLevel + " p_locale=" + p_locale);
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
        PreparedStatement psmtAppr1O2CBatchItem = null;
        PreparedStatement psmtAppr2O2CBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;

        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
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
        String o2cTransferID = null;
        String m_receiverStatusAllowed[] = null;
        OperatorUtilI operatorUtili = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch] ", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
        // user life cycle
        String receiverStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(o2cBatchMatserVO.getNetworkCode(), o2cBatchMatserVO.getCategoryCode(),
            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.processing");
        }
        final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
        final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
        m_receiverStatusAllowed = sa.split(",");
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuilder sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
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
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }

        // The query below is used to load the network stock details for network
        // in between sender and receiver
        // This table will basically used to update the daily_stock_updated_on
        // and also to know how many
        // records are to be inseert in network_daily_stocks
   
        final String sqlLoadNetworkStock = batchO2CTransferWebQry.closeOrderByBatchLoadNetworkStockQry();
    
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
        
        final String sqlSelectNetworkStock = batchO2CTransferWebQry.closeOrderByBatchSelectNetworkStockQry();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelectNetworkStock=" + sqlSelectNetworkStock);
        }
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

        // Insert record into network_stock_transactions table. //Added by lalit to fix BUG DEF457 GP 6.6.1
        sqlBuffer = new StringBuilder(" INSERT INTO network_stock_transactions ( ");
        sqlBuffer.append(" ref_txn_id, txn_wallet, txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
        sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
        sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
        sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp ) ");
        sqlBuffer.append(" VALUES ");
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

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be inseert in user_daily_balances table
    
        final String selectUserBalances = batchO2CTransferWebQry.closeOrderByBatchSelectUserBalancesQry();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
        }
        sqlBuffer = null;

        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
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
            log.debug(methodName, "QUERY insertUserDailyBalances=" + insertUserDailyBalances);
        }

        // Select the balance of user for the perticuler product and network.
       
        Boolean isUserProductMultipleWallet = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET);
        String walletForAdnlCmsn = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN);
        String txnSenderUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG);
        Boolean isO2CSmsNotify = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.O2C_SMS_NOTIFY);
        Boolean isLmsAppl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        String defaultWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
        String defaultWallet = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET);
        final String selectBalance = batchO2CTransferWebQry.closeOrderByBatchSelectBalanceQry();
      
        sqlBuffer = null;

        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuilder(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        if (isUserProductMultipleWallet) {
            sqlBuffer.append(" and balance_type=? ");
        }
        final String updateBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateBalance=" + updateBalance);
        }
        sqlBuffer = null;

        // Insert the record of balnce for user (If balance not found in
        // user_balances)
        sqlBuffer = new StringBuilder(" INSERT ");
        sqlBuffer.append(" INTO user_balances ");
        sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
        sqlBuffer.append(" user_id, product_code , network_code, network_code_for ) ");
        if (isUserProductMultipleWallet) {
            sqlBuffer.append(" , balance_type ");
        }
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,? ");
        if (isUserProductMultipleWallet) {
            sqlBuffer.append(" ,? ");
        }

        sqlBuffer.append(")");
        final String insertBalance = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertBalance=" + insertBalance);
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
        sqlBuffer.append(" ,last_sos_txn_status,last_lr_status ");
        sqlBuffer.append(" FROM user_transfer_counts ");
        sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        final String selectTransferCounts = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
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
            log.debug(methodName, "QUERY selectProfileCounts=" + selectProfileCounts);
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
            log.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
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
            log.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // o2c_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,approved1_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv1O2CBatchItems=" + sqlApprv1O2CBatchItems);
        }
        sqlBuffer = null;

        // If current level of approval is 2 then below query is used to updatwe
        // o2c_batch_items table
        sqlBuffer = new StringBuilder(" UPDATE  o2c_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?,approved2_quantity=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2O2CBatchItems = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlApprv2O2CBatchItems=" + sqlApprv2O2CBatchItems);
        }
        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
     
        
        final String selectItemsDetails = batchO2CTransferWebQry.closeOrderByBatchItemsDetailsQry();
 
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuilder("UPDATE o2c_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateO2CBatches = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY updateO2CBatches =" + updateO2CBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuilder("SELECT modified_on FROM o2c_batch_items ");
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
            log.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
        }
        sqlBuffer = null;

        // The query bel;ow is used to insert the record in channel transfer
        // items table for the order that is closed
        sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price, sender_debit_quantity, receiver_credit_quantity,commision_quantity,sender_post_stock, receiver_post_stock,otf_type,otf_rate,otf_amount,otf_applicable)  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
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
        sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,pmt_inst_type,pmt_inst_no,pmt_inst_date,FIRST_LEVEL_APPROVED_QUANTITY,SECOND_LEVEL_APPROVED_QUANTITY,dual_comm_type ) ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
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
            log.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
        }
        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        try {
            BatchO2CItemsVO batchO2CItemsVO = null;
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
            pstmtInsertNetworkStockTransaction =  con.prepareStatement(insertNetworkStockTransaction);
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

            psmtAppr1O2CBatchItem =  con.prepareStatement(sqlApprv1O2CBatchItems);
            psmtAppr2O2CBatchItem =  con.prepareStatement(sqlApprv2O2CBatchItems);
            pstmtSelectItemsDetails = con.prepareStatement(selectItemsDetails);
            pstmtUpdateMaster =  con.prepareStatement(updateO2CBatches);
            pstmtIsModified = con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = con.prepareStatement(loadTransferProfileProduct);
            pstmtInsertIntoChannelTransferItems = con.prepareStatement(insertIntoChannelTransferItem);
            pstmtInsertIntoChannelTranfers =  con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = con.prepareStatement(selectBalanceInfoForMessage);
            errorList = new ArrayList();
            final Iterator iterator = dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defaultWebGatewayCode);
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
            long bonusBalance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;
            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                batchO2CItemsVO = (BatchO2CItemsVO) dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = batchO2CItemsVO.getBatchId();
                }
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Executed batchO2CItemsVO=" + batchO2CItemsVO.toString());
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, batchO2CItemsVO.getUserId());
                for (int x = 0; x < m_receiverStatusAllowed.length; x++) {
                    ++m;
                    pstmtLoadUser.setString(m, m_receiverStatusAllowed[x]);
                }
                try{
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(batchO2CItemsVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                    language = rs.getString("phone_language");
                    country = rs.getString("country");
                    channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                    if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Commission profile suspend",
                            " Approvallevel " + currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Transfer profile suspend",
                            " Approval level " + currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog
                            .detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User is IN suspend", " Approvallevel" + currentLevel);
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User not found", " Approval level " + currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                networkStocksVO = new NetworkStockVO();
                networkStocksVO.setProductCode(o2cBatchMatserVO.getProductCode());
                networkStocksVO.setNetworkCode(o2cBatchMatserVO.getNetworkCode());
                networkStocksVO.setNetworkCodeFor(o2cBatchMatserVO.getNetworkCodeFor());

                // creating the channelTransferVO here since O2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth o2c items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(o2cBatchMatserVO.getNetworkCode());
                channelTransferVO.setNetworkCodeFor(o2cBatchMatserVO.getNetworkCodeFor());
                channelTransferVO.setProductCode(o2cBatchMatserVO.getProductCode());
                channelTransferVO.setToUserID(batchO2CItemsVO.getUserId());
                ChannelTransferBL.genrateTransferID(channelTransferVO);
                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch o2c item was created.
                channelTransferVO.setCreatedOn(batchO2CItemsVO.getInitiatedOn());

                networkStocksVO.setLastTxnNum(o2cTransferID);
                if (batchO2CItemsVO.isCommCalReqd()) {
                    networkStocksVO.setLastTxnBalance(batchO2CItemsVO.getSecondApprQty());
                    networkStocksVO.setWalletBalance(batchO2CItemsVO.getSecondApprQty());
                } else {
                    networkStocksVO.setLastTxnBalance(batchO2CItemsVO.getRequestedQuantity());
                    networkStocksVO.setWalletBalance(batchO2CItemsVO.getRequestedQuantity());
                }
                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                networkStocksVO.setModifiedBy(userID);
                networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
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
                try{
                rs = null;
                rs = pstmtLoadNetworkStock.executeQuery();
                while (rs.next()) {
                    dailyStockUpdatedOn = rs.getDate("daily_stock_updated_on");

                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, date);

                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the difference
                        // is.
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("product_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_created"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_returned"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_sold"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("last_txn_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("previous_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
							
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                    "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                    "FAIL : DB Error while insert in network daily stock table", "Approval level= " + currentLevel + "updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Termination of the procissing",
                                "Approval level= " + currentLevel);
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
                            errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                "FAIL : DB Error while updating network daily stock table", " Approval level = " + currentLevel + "updateCount = " + updateCount);
                            continue;
                        }
                    }
                }
                }
                finally{
                	if (rs != null) {
                        rs.close();
                    }
                }
                
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
                try{
                rs = null;
                rs = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockSold = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs.next()) {
                    stock = rs.getLong("wallet_balance");
                    stockSold = rs.getLong("wallet_sold");
                    previousNwStockToBeSetChnlTrfItems = stock;
                 }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(PretupsRestUtil.getMessageString("label.all"), String.valueOf(batchO2CItemsVO.getRecordNumber()) + " - " + PretupsRestUtil.getMessageString("label.all"), PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", " Approval level =" + currentLevel);
                    throw new BTSLBaseException(this, methodName, "batcho2c.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", " Approval level = " + currentLevel);
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
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating network stock table",
                        " Approval level = " + currentLevel + ", updateCount = " + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                    new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);
                }

                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setNetworkCode(networkStocksVO.getNetworkCode());
                networkStockTxnVO.setNetworkFor(networkStocksVO.getNetworkCodeFor());
                if (networkStocksVO.getNetworkCode().equals(o2cBatchMatserVO.getNetworkCodeFor())) {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                } else {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                }
                // As discussed with sandeep in channel transfer table's
                // reference number field we have
                // to insert batch details id.So In network stock where channel
                // transfer table's reference number
                // was inserted, I insert batch detail id.
                networkStockTxnVO.setReferenceNo(batchO2CItemsVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(batchO2CItemsVO.getInitiatedOn());
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    networkStockTxnVO.setRequestedQuantity(batchO2CItemsVO.getRequestedQuantity());
                    networkStockTxnVO.setApprovedQuantity(batchO2CItemsVO.getRequestedQuantity());
                } else {
                    networkStockTxnVO.setRequestedQuantity(batchO2CItemsVO.getSecondApprQty());
                    networkStockTxnVO.setApprovedQuantity(batchO2CItemsVO.getSecondApprQty());
                }
                networkStockTxnVO.setInitiaterRemarks(batchO2CItemsVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(batchO2CItemsVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(batchO2CItemsVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(batchO2CItemsVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(batchO2CItemsVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(batchO2CItemsVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(batchO2CItemsVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(batchO2CItemsVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(batchO2CItemsVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(userID);

                networkStockTxnVO.setTxnStatus(batchO2CItemsVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
                networkStockTxnVO.setTxnType(PretupsI.DEBIT);
                networkStockTxnVO.setInitiatedBy(userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(batchO2CItemsVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(batchO2CItemsVO.getTransferMrp());

                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    networkItemsVO.setRequiredQuantity(batchO2CItemsVO.getRequestedQuantity());
                    networkItemsVO.setApprovedQuantity(batchO2CItemsVO.getRequestedQuantity());
                } else {

                    networkItemsVO.setRequiredQuantity(batchO2CItemsVO.getSecondApprQty());
                    networkItemsVO.setApprovedQuantity(batchO2CItemsVO.getSecondApprQty());
                }
                networkItemsVO.setMrp(batchO2CItemsVO.getTransferMrp());
                networkItemsVO.setProductCode(o2cBatchMatserVO.getProductCode());
                networkItemsVO.setAmount(0);
                networkItemsVO.setProductCode(o2cBatchMatserVO.getProductCode());
                networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
                // Added on 07/02/08
                networkItemsVO.setDateTime(o2cBatchMatserVO.getBatchDate());
                m = 0;
                pstmtInsertNetworkStockTransaction.clearParameters();
                //Added by lalit to fix BUG DEF457 GP 6.6.1
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, o2cTransferID);
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, PretupsI.SALE_WALLET_TYPE);
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
                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating network stock TXN table",
                        "Approval level =  " + currentLevel + ", updateCount = " + updateCount);
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
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        " Approval level = " + currentLevel + ", updateCount  =" + updateCount);
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
                try{
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
                        if (log.isDebugEnabled()) {
                            log.debug(methodName, "Till now daily Stock is not updated on " + date + ", day differences = " + dayDifference);
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
                            updateCount = pstmtInsertUserDailyBalances.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                con.rollback();
                                errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                    "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                    "FAIL : DB Error while inserting user daily balances table", " Approval level = " + currentLevel + ", updateCount =" + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
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
                            errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO,
                                "FAIL : DB Error while updating user balances table for daily balance", "Approval level = " + currentLevel + ",updateCount=" + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                maxBalance = 0;
                isNotToExecuteQuery = false;
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, o2cBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, o2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                try{
                rs = null;
                rs = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                while (rs.next()) {
                    if ((walletForAdnlCmsn).equals(rs.getString("balance_type")) && isUserProductMultipleWallet) {
                        bonusBalance = rs.getLong("balance");
                    } else {
                        balance = rs.getLong("balance");
                    }

                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                
                if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    if (!batchO2CItemsVO.isCommCalReqd()) {
                        balance += batchO2CItemsVO.getRequestedQuantity();
                    } else {
                        balance += batchO2CItemsVO.getSecondApprQty();
                    }
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                if (bonusBalance > -1) {
                    bonusBalance += batchO2CItemsVO.getCommissionValue();
                }

                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, batchO2CItemsVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, o2cBatchMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                try
                {
                rs = null;
                rs = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(o2cBatchMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                    
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + currentLevel);
                    continue;
                }
                }
                finally{
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
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User Max balance reached",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the order
                // value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (balance < -1 && maxBalance < batchO2CItemsVO.getRequestedQuantity() || maxBalance < batchO2CItemsVO.getSecondApprQty()) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : User Max balance reached",
                        "Approval level = " + currentLevel);
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
                        if (!batchO2CItemsVO.isCommCalReqd()) {
                            balance = batchO2CItemsVO.getRequestedQuantity();
                        } else {
                            balance = batchO2CItemsVO.getSecondApprQty();
                        }
                        ++m;
                        handlerStmt.setLong(m, 0);// previous balance
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                        // on
                        // date
                    }
                    if (isUserProductMultipleWallet) {
                        ++m;
                        handlerStmt.setLong(m, balance - batchO2CItemsVO.getCommissionValue());
                    } else {
                    	if(PretupsI.COMM_TYPE_POSITIVE.equals(batchO2CItemsVO.getDualCommissionType()))
                    	{
                    		++m;
                            handlerStmt.setLong(m, balance  + (batchO2CItemsVO.getCommissionValue() - batchO2CItemsVO.getTax3Value()));
                    	}
                    	else
                    	{
                    		++m;
                    		handlerStmt.setLong(m, balance);
                        }
                    }
                    ++m;
                    handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    // where
                    ++m;
                    handlerStmt.setString(m, o2cBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                    if (isUserProductMultipleWallet) {
                        ++m;
                        handlerStmt.setString(m, defaultWallet);
                        updateCount = handlerStmt.executeUpdate();
                        handlerStmt.clearParameters();
                        m = 0;
                        if (!(bonusBalance > -1)) {
                            ++m;
                            handlerStmt.setLong(m, 0);
                            ++m;
                            handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            bonusBalance = batchO2CItemsVO.getCommissionValue();
                        }
                        ++m;
                        handlerStmt.setLong(m, bonusBalance);
                        ++m;
                        handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                        ++m;
                        handlerStmt.setString(m, o2cTransferID);
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        handlerStmt.setString(m, channelUserVO.getUserID());
                        // where
                        ++m;
                        handlerStmt.setString(m, o2cBatchMatserVO.getProductCode());
                        ++m;
                        handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCode());
                        ++m;
                        handlerStmt.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                        ++m;
                        handlerStmt.setString(m, walletForAdnlCmsn);
                        if (updateCount > 0) {
                            updateCount = handlerStmt.executeUpdate();
                        }
                    }
                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                try
                {
                rs = null;
                rs = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(batchO2CItemsVO.getUserId());

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
                pstmtSelectProfileCounts.setString(m, batchO2CItemsVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, o2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                try{
                rs = null;
                rs = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
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
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Transfer profile not found",
                        "Approval level = " + currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
				
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.	
                  if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Daily transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getRequestedQuantity())) {
                    if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.dailyinvaluereach"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Daily transfer in value reach",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Weekly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + batchO2CItemsVO.getRequestedQuantity())) {
                    if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.weeklyinvaluereach"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Weekly transfer in value reach",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Monthly transfer in count reach",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + batchO2CItemsVO.getRequestedQuantity())) {
                    if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty())) {
                        con.rollback();
                        errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batcho2c.batchapprovereject.msg.error.monthlyinvaluereach"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Monthly transfer in value reach",
                            "Approval level = " + currentLevel);
                        continue;
                    }
                }
                
                countsVO.setUserID(channelUserVO.getUserID());
                countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    countsVO.setDailyInValue(countsVO.getDailyInValue() + batchO2CItemsVO.getRequestedQuantity());
                    countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + batchO2CItemsVO.getRequestedQuantity());
                    countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + batchO2CItemsVO.getRequestedQuantity());
                } else {
                    countsVO.setDailyInValue(countsVO.getDailyInValue() + batchO2CItemsVO.getSecondApprQty());
                    countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + batchO2CItemsVO.getSecondApprQty());
                    countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + batchO2CItemsVO.getSecondApprQty());
                }
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
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + currentLevel);
                    } else {
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, batchO2CItemsVO.getBatchDetailId());
                java.sql.Timestamp newlastModified = null;
                try{
                rs = null;
                rs = pstmtIsModified.executeQuery();
                // check record is modified or not
                if (rs.next()) {
                    newlastModified = rs.getTimestamp("modified_on");
                    
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Record is already modified",
                        "Approval level = " + currentLevel);
                    continue;
                }
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(batchO2CItemsVO.getModifiedOn()).getTime()) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : Record is already modified",
                        "Approval level = " + currentLevel);
                    continue;
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1O2CBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
                    psmtAppr1O2CBatchItem.clearParameters();
                    batchO2CItemsVO.setFirstApprovedBy(userID);
                    batchO2CItemsVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, o2cTransferID);
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
                    psmtAppr1O2CBatchItem.setLong(m, batchO2CItemsVO.getFirstApprovedQuantity());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1O2CBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2O2CBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(currentLevel)) {
                    psmtAppr2O2CBatchItem.clearParameters();
                    batchO2CItemsVO.setSecondApprovedBy(userID);
                    batchO2CItemsVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, o2cTransferID);
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
                    if (!batchO2CItemsVO.isCommCalReqd()) {
                        ++m;
                        psmtAppr2O2CBatchItem.setLong(m, batchO2CItemsVO.getRequestedQuantity());
                    } else {
                        ++m;
                        psmtAppr2O2CBatchItem.setLong(m, batchO2CItemsVO.getFirstApprovedQuantity());
                    }
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, batchO2CItemsVO.getBatchDetailId());
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2O2CBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2O2CBatchItem.executeUpdate();
                }

                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + currentLevel + ",updateCount=" + updateCount);
                    continue;
                }
                channelTransferVO.setCanceledOn(batchO2CItemsVO.getCancelledOn());
                channelTransferVO.setCanceledBy(batchO2CItemsVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(batchO2CItemsVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(batchO2CItemsVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(batchO2CItemsVO.getCommissionProfileVer());
                channelTransferVO.setDualCommissionType(batchO2CItemsVO.getDualCommissionType());
                channelTransferVO.setCreatedBy(batchO2CItemsVO.getInitiatedBy());
                channelTransferVO.setDomainCode(o2cBatchMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(batchO2CItemsVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(batchO2CItemsVO.getExtTxnNo());
                channelTransferVO.setFinalApprovedBy(batchO2CItemsVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(batchO2CItemsVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(batchO2CItemsVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(batchO2CItemsVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(batchO2CItemsVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(batchO2CItemsVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setBatchNum(batchO2CItemsVO.getBatchId());
                channelTransferVO.setBatchDate(o2cBatchMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax3(batchO2CItemsVO.getTax3Value()); // kuch
                // panga
                // hai
                channelTransferVO.setPayableAmount(batchO2CItemsVO.getPayableAmount());
                channelTransferVO.setNetPayableAmount(batchO2CItemsVO.getNetPayableAmount());
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(o2cBatchMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(batchO2CItemsVO.getCategoryCode());
                channelTransferVO.setReceiverGradeCode(batchO2CItemsVO.getGradeCode());
                channelTransferVO.setReceiverTxnProfile(batchO2CItemsVO.getTxnProfile());
                channelTransferVO.setReferenceNum(batchO2CItemsVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(batchO2CItemsVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(batchO2CItemsVO.getStatus());
                channelTransferVO.setThirdApprovedBy(batchO2CItemsVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(batchO2CItemsVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(batchO2CItemsVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                channelTransferVO.setTotalTax1(batchO2CItemsVO.getTax1Value());
                channelTransferVO.setTotalTax2(batchO2CItemsVO.getTax2Value());
                //batch o2c to be in synch with normal o2c
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE); 
                channelTransferVO.setTransferDate(batchO2CItemsVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(batchO2CItemsVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(batchO2CItemsVO.getTransferMrp());
                channelTransferVO.setPayInstrumentType(batchO2CItemsVO.getPaymentType());// @@@@
                channelTransferVO.setPayInstrumentNum(batchO2CItemsVO.getExtTxnNo());
                channelTransferVO.setPayInstrumentDate(batchO2CItemsVO.getExtTxnDate());
                channelTransferVO.setLevelOneApprovedQuantity(Long.toString(batchO2CItemsVO.getFirstApprovedQuantity()));
                channelTransferVO.setLevelTwoApprovedQuantity(Long.toString(batchO2CItemsVO.getFirstApprovedQuantity()));
                
                channelTransferItemVO = new ChannelTransferItemsVO();
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    channelTransferItemVO.setApprovedQuantity(batchO2CItemsVO.getRequestedQuantity());
                } else {
                    channelTransferItemVO.setApprovedQuantity(batchO2CItemsVO.getFirstApprovedQuantity());
                }
                channelTransferItemVO.setCommProfileDetailID(batchO2CItemsVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(batchO2CItemsVO.getCommissionRate());
                channelTransferItemVO.setCommType(batchO2CItemsVO.getCommissionType());
                channelTransferItemVO.setCommValue(batchO2CItemsVO.getCommissionValue());
                channelTransferItemVO.setCommQuantity(batchO2CItemsVO.getCommissionValue() - batchO2CItemsVO.getTax3Value());// hi
                channelTransferItemVO.setNetPayableAmount(batchO2CItemsVO.getNetPayableAmount());
                channelTransferItemVO.setPayableAmount(batchO2CItemsVO.getPayableAmount());
                channelTransferItemVO.setProductTotalMRP(batchO2CItemsVO.getTransferMrp());
                channelTransferItemVO.setProductCode(o2cBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                if (!batchO2CItemsVO.isCommCalReqd()) {
                    channelTransferItemVO.setRequiredQuantity(batchO2CItemsVO.getRequestedQuantity());
                    channelTransferItemVO.setRequestedQuantity(Double.toString(BTSLUtil.getDisplayAmount(batchO2CItemsVO.getRequestedQuantity())));
                } else {
                    channelTransferItemVO.setRequiredQuantity(batchO2CItemsVO.getFirstApprovedQuantity());
                    channelTransferItemVO.setRequestedQuantity(Double.toString(BTSLUtil.getDisplayAmount(batchO2CItemsVO.getFirstApprovedQuantity())));
                }
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(batchO2CItemsVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(batchO2CItemsVO.getTax1Type());
                channelTransferItemVO.setTax1Value(batchO2CItemsVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(batchO2CItemsVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(batchO2CItemsVO.getTax2Type());
                channelTransferItemVO.setTax2Value(batchO2CItemsVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(batchO2CItemsVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(batchO2CItemsVO.getTax3Type());
                channelTransferItemVO.setTax3Value(batchO2CItemsVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(o2cBatchMatserVO.getProductMrp());
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                //batch o2c synch with normal o2c
                channelTransferItemVO.setReceiverCreditQty(channelTransferItemVO.getApprovedQuantity()+ channelTransferItemVO.getCommQuantity());
                
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(o2cBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
                }
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
                }
                channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
                /* SOS validation*/
                Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(con, countsVO, channelTransferVO);
                 if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true))
                 {
                 	con.rollback();
                     errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                         "batcho2c.batchapprovereject.msg.error.sosPending"));
                     errorList.add(errorVO);
                     BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : SOS Status PENDING FOR USER",
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
     				channelTransferVO.setLrWithdrawAmt((Long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
     				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
     			}
     			//Validate MRP && Successive Block for channel transaction
				long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C)).longValue();
				try {
					ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, new Date(), successiveReqBlockTime4ChnlTxn);					
				} catch (Exception e) {
					String args[] = {channelTransferVO.getUserMsisdn(), PretupsBL.getDisplayAmount(channelTransferVO.getTransferMRP()),String.valueOf(successiveReqBlockTime4ChnlTxn/60)};
					errorVO=new ListValueVO(batchO2CItemsVO.getMsisdn(),String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString("batcho2c.batchapprovereject.msg.error.mrpblocktimeout",args));
					errorList.add(errorVO);
					BatchO2CProcessLog.detailLog("closeOrederByBatch",o2cBatchMatserVO,batchO2CItemsVO,"FAIL : Validate MRP && Successive Block for channel transaction","Approval level = "+currentLevel);
					continue;					
				}
                final boolean debit = true;
                if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType()) && PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(channelTransferVO.getTransferType())) {
                    ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(con, channelTransferVO, channelTransferVO.getFromUserID(), date, debit);
                    ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(con, channelTransferVO, channelTransferVO.getFromUserID(), date);
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
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemsVO.getStatus());
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
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemsVO.getMsisdn());
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
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getPayInstrumentType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getPayInstrumentNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getPayInstrumentDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, Long.parseLong(channelTransferVO.getLevelOneApprovedQuantity()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, Long.parseLong(channelTransferVO.getLevelTwoApprovedQuantity()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, batchO2CItemsVO.getDualCommissionType());   
                
                // ends here
                // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + currentLevel + ", update Count=" + updateCount);
                    continue;
                }
                
                //Target based base commission calculation and User's OTF counts update
                
            	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelTransferVO.getNetworkCode()))
        		{
        			ChannelTransferBL.increaseOptOTFCounts(con, channelTransferVO);
        			// to calculate commission and tax
        			channelTransferVO.setOtfFlag(true);

        		       	 final ArrayList<ChannelTransferItemsVO> list = new ArrayList<ChannelTransferItemsVO>();
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
        				
        				
        			ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);
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
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getAfterTransSenderPreviousStock());
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
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity() + channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getAfterTransSenderPreviousStock() - channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getAfterTransReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity() + channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getOtfTypePctOrAMt());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getOtfRate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getOtfAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m , channelTransferItemVO.isOtfApplicable());
                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    con.rollback();
                    errorVO = new ListValueVO(batchO2CItemsVO.getMsisdn(), String.valueOf(batchO2CItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "FAIL : DB Error while inserting in channel transfer items table",
                        "Approval level = " + currentLevel + ", update Count=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                // user life cycle
                if (batchO2CItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        int updatecount = 0;
                        // int
                        // updatecount=operatorUtili.changeUserStatusToActive(

                        final String str[] = txnSenderUserStatusChang.split(","); 
                        String newStatus[] = null;
                        for (int i = 0; i < str.length; i++) {
                            newStatus = str[i].split(":");
                            if (newStatus[0].equals(channelUserVO.getStatus())) {
                                updatecount = operatorUtili.changeUserStatusToActive(con, channelTransferVO.getToUserID(), channelUserVO.getStatus(), newStatus[1]);
                                break;
                            }
                        }
                        if (updatecount > 0) {
                            con.commit();
                            BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "PASS : Order is closed successfully",
                                "Approval level = " + currentLevel + ", updateCount= " + updateCount);
                        } else {
                            con.rollback();
                            throw new BTSLBaseException(this, methodName, "error.status.updating");
                        }
                    } else {

                        con.commit();
                        BatchO2CProcessLog.detailLog(methodName, o2cBatchMatserVO, batchO2CItemsVO, "PASS : Order is closed successfully",
                            "Approval level = " + currentLevel + ", updateCount =" + updateCount);

                    }

                } else {
                    con.commit();

                }

                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, o2cBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, o2cBatchMatserVO.getNetworkCodeFor());
                try{
                rs = null;
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
                keyArgumentVO.setKey(PretupsErrorCodesI.O2C_OPT_CHNL_TRANSFER_SMS2);
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
                        keyArgumentVO.setKey(PretupsErrorCodesI.O2C_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String o2cNotifyMsg = null;
                final long receiverPostBalance=channelTransferItemVO.getAfterTransReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity() + channelTransferItemVO.getCommQuantity();
                
                if (isO2CSmsNotify) {
                	 final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        o2cNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        o2cNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    array = new String[] { channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity()),channelTransferItemVO.getProductCode(),
                    		PretupsBL.getDisplayAmount(channelTransferItemVO.getNetPayableAmount()),PretupsBL.getDisplayAmount(receiverPostBalance), o2cNotifyMsg };
                }

                if (o2cNotifyMsg == null) {
                    array = new String[] { channelTransferVO.getTransferID(),PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity()), channelTransferItemVO.getProductCode() ,
                    		PretupsBL.getDisplayAmount(channelTransferItemVO.getNetPayableAmount()), PretupsBL.getDisplayAmount(receiverPostBalance)};
                }

                if (isLmsAppl) {

                    try {
                        final LoyaltyBL _loyaltyBL = new LoyaltyBL();
                        final LoyaltyVO loyaltyVO = new LoyaltyVO();
                        PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
                        final LoyaltyDAO _loyaltyDAO = new LoyaltyDAO();
                        final ArrayList arr = new ArrayList();
                        loyaltyVO.setModuleType(PretupsI.O2C_MODULE);
                        loyaltyVO.setServiceType(PretupsI.O2C_MODULE);
                        loyaltyVO.setTransferamt(channelTransferVO.getRequestedQuantity());
                        loyaltyVO.setCategory(channelTransferVO.getCategoryCode());
                        loyaltyVO.setFromuserId(channelTransferVO.getFromUserID());
                        loyaltyVO.setTouserId(channelTransferVO.getToUserID());
                        loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());
                        loyaltyVO.setTxnId(channelTransferVO.getTransferID());
                        loyaltyVO.setCreatedOn(channelTransferVO.getCreatedOn());
                        loyaltyVO.setSenderMsisdn(channelTransferVO.getFromUserCode());
                        loyaltyVO.setReciverMsisdn(channelTransferVO.getToUserCode());
                        loyaltyVO.setProductCode(channelTransferVO.getProductCode());
                        arr.add(loyaltyVO.getFromuserId());
                        arr.add(loyaltyVO.getTouserId());
                        promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(con, arr);
                        loyaltyVO.setSetId(promotionDetailsVO.get_setId());
                        loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

                        if (loyaltyVO.getSetId() == null && loyaltyVO.getToSetId() == null) {
                            log.error("process", "Exception during LMS Module.SetId not found");
                        } else {
                            _loyaltyBL.distributeLoyaltyPoints(PretupsI.O2C_MODULE, channelTransferVO.getTransferID(), loyaltyVO);
                        }

                    } catch (Exception ex) {
                        log.error("process", "Exception durign LMS Module " + ex.getMessage());
                        log.errorTrace(methodName, ex);

                    }

                }
                messages = new BTSLMessages(PretupsErrorCodesI.O2C_OPT_CHNL_TRANSFER_SMS1, array);
                pushMessage = new PushMessage(batchO2CItemsVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
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
            log.error(methodName, "SQLException: " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " BatchO2CTransferDAO[closeOrderByBatch]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralSqlException);
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch] ", "", "", "",
                "Exception:" + ex.getMessage());
            BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + currentLevel);
            throw new BTSLBaseException(this, methodName, errorGeneralException);
        } finally {
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
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_OPEN;
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
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        con.rollback();
                        errorVO = new ListValueVO("", "", PretupsRestUtil.getMessageString( "batcho2c.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrederByBatch]",
                            "", "", "", "Error while updating O2C_BATCHES table. Batch id=" + batch_ID);
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
                log.error(methodName, " SQLException : " + sqe);
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
                BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + currentLevel);
                thorwBTSLBaseException(methodName, errorGeneralException);
            } catch (Exception ex) {
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Exception : " + ex);
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch]", "", "",
                    "", "Exception:" + ex.getMessage());
                BatchO2CProcessLog.o2cBatchMasterLog(methodName, o2cBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + currentLevel);
                thorwBTSLBaseException(methodName, errorGeneralException);
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
                log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    }

    
    
    

}
