/**
 * @(#)ChannelTransferDAO.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan Aug 2, 2005 Initital Creation
 *                             Sandeep Goel Nov 05, 2005
 *                             Modification,Customization
 *                             Sandeep Goel Sep 18, 2006
 *                             Modification,Customization ID REC001
 *                             Sandeep Goel Oct 09, 2006
 *                             Modification,Customization ID RECON001
 *                             Shishupal Singh Mar 27, 2007 Modification in
 *                             loadC2STransferVOList method
 *                             avinash.kamthan July 19, 2011 Changes for Email
 *                             Notification
 *                             Amit Raheja Oct 11,2012 c2s_transfer_items
 *                             removal from db
 *                             Ashutosh Kumar July 22,2015 Network Wallet
 *                             management
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.OfflineReportRunningThreadMap;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVoucherVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.btsl.voms.voucherbundle.businesslogic.VoucherBundleVO;
import com.opencsv.CSVWriter;
import com.txn.pretups.channel.transfer.businesslogic.C2CTransferdetReportWriter;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebQry;



public class ChannelTransferDAO {
    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(ChannelTransferDAO.class.getName());
	private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
	private static final String EXCEPTION = " Exception : ";

    private ChannelTransferQry channelTransferQry;
    public ChannelTransferDAO(){
    	channelTransferQry = (ChannelTransferQry)ObjectProducer.getObject(QueryConstants.CHANNEL_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
    }
    /**
     * Start the operator to channel transfer
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public int addChannelTransfer(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String methodName = "addChannelTransfer";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered ChannelTransferVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue);
        }
        PreparedStatement psmt = null;
        FileInputStream fs = null;
    	String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        int updateCount = 0;  
        try {
        	Boolean multipleWalletApply = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
        	Boolean channelTransfersInfoRequired = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
            final StringBuffer strBuff = new StringBuffer(" INSERT INTO  channel_transfers ( transfer_id, network_code, network_code_for, grph_domain_code, ");
            strBuff.append(" domain_code, sender_category_code, sender_grade_code, receiver_grade_code, from_user_id, ");
            strBuff.append(" to_user_id, transfer_date, reference_no, ext_txn_no, ext_txn_date, commission_profile_set_id, ");
            strBuff.append(" commission_profile_ver, requested_quantity, channel_user_remarks,  ");
            strBuff.append(" created_on, created_by, modified_by, modified_on, status, transfer_type, transfer_initiated_by, transfer_mrp, ");
            strBuff.append(" payable_amount, net_payable_amount, pmt_inst_type, pmt_inst_no, pmt_inst_date, ");
            strBuff.append(" pmt_inst_amount, sender_txn_profile, receiver_txn_profile, total_tax1, total_tax2, ");
            strBuff.append(" total_tax3, source, receiver_category_code , product_type , transfer_category ,");
            strBuff.append(" first_approver_limit, second_approver_limit,pmt_inst_source,  ");
            strBuff.append(" type,transfer_sub_type,close_date,control_transfer,request_gateway_code, request_gateway_type, ");
            strBuff.append(" msisdn,to_msisdn,to_grph_domain_code,to_domain_code,  ");
            strBuff
                .append(" first_approved_by, first_approved_on, second_approved_by, second_approved_on, third_approved_by, third_approved_on,sms_default_lang,sms_second_lang,active_user_id,TRANSACTION_MODE");
            strBuff.append(",cell_id,switch_id,stock_updated,sos_status,SOS_SETTLEMENT_DATE,oth_comm_prf_set_id");
            if(channelTransfersInfoRequired)
            {
            	strBuff.append(" ,info1, info2 ");
            }
            if (multipleWalletApply) {
                strBuff.append(",TXN_WALLET");
            }
            //This is temporary fix for release 7.3. Need to check from where we are getting dual commission type null
            if(!BTSLUtil.isNullString(p_channelTransferVO.getDualCommissionType())) {
            	strBuff.append(",dual_comm_type");            	
            }
			
			if(p_channelTransferVO.isFileUploaded() == true)
            {
				if(QueryConstants.DB_ORACLE.equals(dbConnected)){
            		strBuff.append(", approval_doc, approval_doc_type, approval_doc_file_path ");
    			}
            	else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            		strBuff.append(", \"approval_doc\", \"approval_doc_type\", \"approval_doc_file_path\" ");
        	}         	

            }

            strBuff.append(") VALUES ");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            if(channelTransfersInfoRequired)
            {
            	strBuff.append(",?,?");
            }
            
            	strBuff.append(",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            	
            
            if (multipleWalletApply) {
                strBuff.append(",? ");
            }
            if(!BTSLUtil.isNullString(p_channelTransferVO.getDualCommissionType())) {
            	strBuff.append(",? ");            	
            }
            
            if(p_channelTransferVO.isFileUploaded() == true)
            {
            	strBuff.append(",?,?,? ");
            }
            strBuff.append(") ");
           
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getNetworkCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getNetworkCodeFor());
            ++i;
            psmt.setString(i, p_channelTransferVO.getGraphicalDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCategoryCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSenderGradeCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverGradeCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFromUserID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getToUserID());
            ++i;
            psmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_channelTransferVO.getTransferDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getCommProfileSetId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCommProfileVersion());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getRequestedQuantity());
            ++i;
            psmt.setString(i, p_channelTransferVO.getChannelRemarks());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCreatedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getCreatedBy());
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferInitatedBy());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSenderTxnProfile());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverTxnProfile());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax1());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax2());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax3());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSource());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverCategoryCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getProductType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferCategory());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getFirstApproverLimit());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getSecondApprovalLimit());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPaymentInstSource());
            ++i;
            psmt.setString(i, p_channelTransferVO.getType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferSubType());
            if ((PretupsI.CHANNEL_TYPE_O2C.equals(p_channelTransferVO.getType()) ||PretupsI.CHANNEL_TYPE_C2C.equals(p_channelTransferVO.getType())) && PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, null);
            } else {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCreatedOn()));
            }
            ++i;
            psmt.setString(i, p_channelTransferVO.getControlTransfer());
            ++i;
            psmt.setString(i, p_channelTransferVO.getRequestGatewayCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getRequestGatewayType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFromUserCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getToUserCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverGgraphicalDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getThirdApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn()));

            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());

            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());
            ++i;
            psmt.setString(i, p_channelTransferVO.getActiveUserId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransactionMode());
            // added for cell id and switch id.
            ++i;
            psmt.setString(i, p_channelTransferVO.getCellId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSwitchId());
            //added for o2c direct transfer 
            ++i;
            psmt.setString(i, p_channelTransferVO.getStockUpdated());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSosStatus());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSosSettlementDate()));
			++i;
			if(p_channelTransferVO.getChannelTransferitemsVOList().size() > 0){
				psmt.setString(i,((ChannelTransferItemsVO)p_channelTransferVO.getChannelTransferitemsVOList().get(0)).getOthCommSetId());
			}else{
				psmt.setString(i,null);
			}
			
            if(channelTransfersInfoRequired)
            {
            	++i;
            	psmt.setString(i, p_channelTransferVO.getInfo1());
            	++i;
            	psmt.setString(i, p_channelTransferVO.getInfo2());
            }
            if (multipleWalletApply) {
                ++i;
                psmt.setString(i, p_channelTransferVO.getWalletType());
            }
            ++i;
            if(!BTSLUtil.isNullString(p_channelTransferVO.getDualCommissionType())) {
            	psmt.setString(i, p_channelTransferVO.getDualCommissionType());            	
            }
            
            if(p_channelTransferVO.isFileUploaded() == true)
            {
            	++i;
            	File file = new File(p_channelTransferVO.getUploadedFilePath());
				fs = new FileInputStream(file);
				psmt.setBinaryStream(i,fs,fs.available()); 
				++i;
				psmt.setString(i, URLConnection.guessContentTypeFromStream(fs));
				++i;
				
			//	if(p_channelTransferVO.getUploadedFile() != null &&  p_channelTransferVO.getUploadedFile().getFileName() != null) {
		//			psmt.setString(i, p_channelTransferVO.getUploadedFile().getFileName());
	//			}else {
					psmt.setString(i, p_channelTransferVO.getUploadedFileName());
	//			}

				
            }
            
            updateCount = psmt.executeUpdate();  
            updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
            if (updateCount <= 0) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[addChannelTransfer]", "", "", "",
                    "BTSLBaseException: update count <=0");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            
            
            // add the items in item list
            updateCount=addTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), p_channelTransferVO.getTransferID(), p_channelTransferVO.getCreatedOn(),
                p_channelTransferVO.getTransferType(), p_channelTransferVO.getType(),p_channelTransferVO.getTransferSubType(),p_channelTransferVO.getStockUpdated());
            
            if(updateCount > 0 && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType()) && p_channelTransferVO.getChannelVoucherItemsVoList()!= null && p_channelTransferVO.getChannelVoucherItemsVoList().size() > 0)
            {
            	updateCount = addVoucherItems(p_con,p_channelTransferVO.getChannelVoucherItemsVoList(),p_channelTransferVO.getTransferID(),p_channelTransferVO.getCreatedOn());
            	
            }
            return updateCount;       
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addChannelTransfer]", "", "", "",
            		loggerValue.toString());
            if (sqle.getErrorCode() == 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_EXTGW_DUPLICATE_TRANSCATION);
            } else {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addChannelTransfer]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (fs != null) {
                	fs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue );
            }
        }// end of finally
    }

    /**
     * @param p_con
     * @param p_searchParam
     *            it can be userID
     * @param p_approvalLevel
     *            it can be different approval level.On the base of approval
     *            level diffrent approval list will load
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_domainCode
     *            Domain code
     * @param p_geoCode
     *            Geo Domain Code (ALL in case of Approve 2 and 3)
     * @param p_loginUserID
     *            User ID of the person who has logged in
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadChannelTransfersList(Connection p_con, String p_searchParam, String p_approvalLevel, String p_networkCode, String p_networkCodeFor, String p_domainCode, String p_geoCode, String p_loginUserID, String p_transferCategory, String p_reveiverCategoryCode, String p_channelOwnerID) throws BTSLBaseException {
        final String methodName = "loadChannelTransfersList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_searchParam ");
        	loggerValue.append(p_searchParam);
        	loggerValue.append(" p_approvalLevel: ");
        	loggerValue.append(p_approvalLevel);
        	loggerValue.append(" p_networkCode ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_roamNetworkCode ");
        	loggerValue.append(p_networkCodeFor);
        	loggerValue.append(" p_domainCode " );
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_geoCode ");
        	loggerValue.append(p_geoCode);
        	loggerValue.append(",p_loginUserID= " );
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(", p_transferCategory =" );
        	loggerValue.append(p_transferCategory);
        	loggerValue.append(",p_reveiverCategoryCode=");
        	loggerValue.append(p_reveiverCategoryCode);
        	loggerValue.append(", p_channelOwnerID=");
        	loggerValue.append(p_channelOwnerID);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = channelTransferQry.loadChannelTransfersListQry(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel);
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_transferCategory);
            ++m;
            pstmt.setString(m, p_networkCode);
            ++m;
            pstmt.setString(m, p_networkCodeFor);
            if (!BTSLUtil.isNullString(p_reveiverCategoryCode) && !p_reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_reveiverCategoryCode);
            }
            ++m;
            pstmt.setString(m, p_loginUserID);
            if (!BTSLUtil.isNullString(p_geoCode)) {
                ++m;
                pstmt.setString(m, p_geoCode);
                ++m;
                pstmt.setString(m, PretupsI.ALL);
                ++m;
                pstmt.setString(m, p_geoCode);
            }
            if (!p_domainCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_domainCode);
            }
            if (!PretupsI.ALL.equals(p_searchParam)) {
                ++m;
                pstmt.setString(m, p_searchParam);
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            }
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            int i = 0;
            UserDAO userDao = new UserDAO();
            // Taking a map that contains the username based on userID to avoid duplicate dao calls in result set
            Map<String, String> userMap = new HashMap<String, String>();
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setType(rs.getString("type"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                if(!BTSLUtil.isNullString(rs.getString("first_approved_by"))){
                    if(!userMap.containsKey(rs.getString("first_approved_by"))){
                        userMap.put(rs.getString("first_approved_by"),
                                userDao.loadUserName(p_con, rs.getString("first_approved_by")));
                    }
                    transferVO.setFirstApprovedByName(userMap.get(rs.getString("first_approved_by")));
                }
                if(!BTSLUtil.isNullString(rs.getString("second_approved_by"))){
                    if(!userMap.containsKey(rs.getString("second_approved_by"))){
                        userMap.put(rs.getString("second_approved_by"),
                                userDao.loadUserName(p_con, rs.getString("second_approved_by")));
                    }
                    transferVO.setSecondApprovedByName(userMap.get(rs.getString("second_approved_by")));
                }
                if(!BTSLUtil.isNullString(rs.getString("third_approved_by"))){
                    if(!userMap.containsKey(rs.getString("third_approved_by"))){
                        userMap.put(rs.getString("third_approved_by"),
                                userDao.loadUserName(p_con, rs.getString("third_approved_by")));
                    }
                    transferVO.setThirdApprovedByName(userMap.get(rs.getString("third_approved_by")));
                }
                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
                transferVO.setDomainCodeDesc(rs.getString("domain_name"));
                transferVO.setReceiverCategoryDesc(rs.getString("category_name"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setTransferInitatedByName(rs.getString("user_name"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                if(!BTSLUtil.isNullString(rs.getString("to_user_id"))){
                    if(!userMap.containsKey(rs.getString("to_user_id"))){
                        userMap.put(rs.getString("to_user_id"),
                                userDao.loadUserName(p_con, rs.getString("to_user_id")));
                    }
                    transferVO.setToUserName(userMap.get(rs.getString("to_user_id")));
                }
                transferVO.setReferenceNum(rs.getString("reference_no"));
                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.TRANSFER_STATUS, transferVO.getStatus())).getLookupName());
                }
                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
                transferVO.setNetworkCode(p_networkCode);
                transferVO.setNetworkCodeFor(p_networkCodeFor);
                // added as per mobinil cr 1
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                // ends here
                // for mali-- +ve commision apply
//                transferVO.setCommQty(rs.getLong("commision_quantity"));
//                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
//                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                transferVO.setCommProfileSetId(rs.getString("COMMISSION_PROFILE_SET_ID"));
                transferVO.setCommProfileVersion(rs.getString("COMMISSION_PROFILE_VER"));
                /** START: Birendra 27JAN2015 */
                transferVO.setUserWalletCode(rs.getString("user_wallet"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setToMSISDN(rs.getString("to_msisdn"));
                if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(rs.getString("transfer_sub_type")))
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.VOUCHER_PRODUCT_O2C);
                	
                }
                else
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.REDEMP_TYPE_STOCK);
                }
                /** STOP: Birendra 27JAN2015 */

                transferVO.setIndex(i);             
                if(rs.getLong("BUNDLE_ID") > 0){
                	transferVO.setBundleType(true);
                }else {
                	transferVO.setBundleType(false);
                }
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setDisplayTransferMRP(String.valueOf(BTSLUtil.getDisplayAmount(rs.getLong("transfer_mrp"))));
                ++i;
                arrayList.add(transferVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "",  loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(arrayList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return arrayList;
    }

    /**
     * Load the Detailed Channel transfer VO against transferID,networkcode and
     * networkcodefor
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @throws BTSLBaseException
     */
    public void loadChannelTransfersVO(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {

        final String methodName = "loadChannelTransfersVO";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_channelTransferVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = channelTransferQry.loadChannelTransfersVOQry(p_channelTransferVO);
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_channelTransferVO.getTransferID());
            ++m;
            pstmt.setString(m, p_channelTransferVO.getNetworkCode());
            ++m;
            pstmt.setString(m, p_channelTransferVO.getNetworkCodeFor());

            rs = pstmt.executeQuery();

            if (rs.next()) {

                p_channelTransferVO.setTransferID(rs.getString("transfer_id"));
                p_channelTransferVO.setTransferInitatorLoginID(rs.getString("initiatorLoginID"));
                p_channelTransferVO.setReferenceID( rs.getString("ref_transfer_id") );
                p_channelTransferVO.setNetworkCode(rs.getString("network_code"));
                p_channelTransferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                p_channelTransferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                p_channelTransferVO.setDomainCode(rs.getString("domain_code"));
                p_channelTransferVO.setCategoryCode(rs.getString("sender_category_code"));
                p_channelTransferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
                p_channelTransferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
                p_channelTransferVO.setFromUserID(rs.getString("from_user_id"));
                p_channelTransferVO.setToUserID(rs.getString("to_user_id"));
                p_channelTransferVO.setTransferDate(rs.getTimestamp("transfer_date"));
                p_channelTransferVO.setReferenceNum(rs.getString("reference_no"));
                p_channelTransferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                p_channelTransferVO.setExternalTxnDate(rs.getTimestamp("ext_txn_date"));
                p_channelTransferVO.setCommProfileSetId(rs.getString("commission_profile_set_id"));
                p_channelTransferVO.setCommProfileVersion(rs.getString("commission_profile_ver"));
                p_channelTransferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                p_channelTransferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
                p_channelTransferVO.setFirstApprovalRemark(rs.getString("first_approver_remarks"));
                p_channelTransferVO.setSecondApprovalRemark(rs.getString("second_approver_remarks"));
                p_channelTransferVO.setThirdApprovalRemark(rs.getString("third_approver_remarks"));
                p_channelTransferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                p_channelTransferVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                p_channelTransferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                p_channelTransferVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                p_channelTransferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                p_channelTransferVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                p_channelTransferVO.setCanceledBy(rs.getString("cancelled_by"));
                p_channelTransferVO.setCanceledOn(rs.getTimestamp("cancelled_on"));
                p_channelTransferVO.setModifiedOn(rs.getTimestamp("modified_on"));
                p_channelTransferVO.setModifiedBy(rs.getString("modified_by"));
                p_channelTransferVO.setStatus(rs.getString("status"));
                p_channelTransferVO.setType(rs.getString("type"));
                p_channelTransferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
                p_channelTransferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                p_channelTransferVO.setFirstApproverLimit(rs.getLong("first_approver_limit"));
                p_channelTransferVO.setSecondApprovalLimit(rs.getLong("second_approver_limit"));
                p_channelTransferVO.setPayableAmount(rs.getLong("payable_amount"));
                p_channelTransferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                p_channelTransferVO.setBatchNum(rs.getString("batch_no"));
                p_channelTransferVO.setBatchDate(rs.getTimestamp("batch_date"));
                p_channelTransferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                p_channelTransferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
                p_channelTransferVO.setPayInstrumentDate(rs.getTimestamp("pmt_inst_date"));
                p_channelTransferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
                p_channelTransferVO.setSenderTxnProfile(rs.getString("sender_txn_profile"));
                p_channelTransferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                p_channelTransferVO.setTotalTax1(rs.getLong("total_tax1"));
                p_channelTransferVO.setTotalTax2(rs.getLong("total_tax2"));
                p_channelTransferVO.setTotalTax3(rs.getLong("total_tax3"));
                p_channelTransferVO.setSource(rs.getString("source"));
                p_channelTransferVO.setReceiverCategoryCode(rs.getString("receiver_category_code"));
                p_channelTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                p_channelTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                p_channelTransferVO.setPaymentInstSource(rs.getString("pmt_inst_source"));
                p_channelTransferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
                p_channelTransferVO.setToGrphDomainCodeDesc(rs.getString("to_grph_domain_name"));
                p_channelTransferVO.setProductType(rs.getString("product_type"));
                p_channelTransferVO.setDomainCodeDesc(rs.getString("domain_name"));
                p_channelTransferVO.setReceiverCategoryDesc(rs.getString("category_name"));
                p_channelTransferVO.setReceiverGradeCodeDesc(rs.getString("grade_name"));
                p_channelTransferVO.setToUserName(rs.getString("user_name"));
                p_channelTransferVO.setAddress1(rs.getString("address1"));
                p_channelTransferVO.setAddress2(rs.getString("address2"));
                p_channelTransferVO.setCity(rs.getString("city"));
                p_channelTransferVO.setState(rs.getString("state"));
                p_channelTransferVO.setCountry(rs.getString("country"));
                p_channelTransferVO.setCommProfileName(rs.getString("comm_profile_set_name"));
                p_channelTransferVO.setStockUpdated(rs.getString("stock_updated"));
                p_channelTransferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
                    p_channelTransferVO.setReceiverTxnProfileName(rs.getString("profile_name"));
                    p_channelTransferVO.setSenderTxnProfileName(rs.getString("sender_txn_profile_name"));
                } else {
                    p_channelTransferVO.setSenderTxnProfileName(rs.getString("profile_name"));
                    p_channelTransferVO.setReceiverTxnProfileName(rs.getString("sender_txn_profile_name"));
                }

                p_channelTransferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
                p_channelTransferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
                p_channelTransferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
                p_channelTransferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
                p_channelTransferVO.setTransferInitatedByName(rs.getString("initatedby"));
                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
                    p_channelTransferVO.setUserMsisdn(rs.getString("from_msisdn"));
                    p_channelTransferVO.setFromMsisdn(rs.getString("from_msisdn"));
                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
                    p_channelTransferVO.setUserMsisdn(rs.getString("to_msisdn"));
                } else {
                    p_channelTransferVO.setUserMsisdn(rs.getString("msisdn"));
                }
                p_channelTransferVO.setToMsisdn(rs.getString("to_msisdn"));
                p_channelTransferVO.setErpNum(rs.getString("external_code"));
                p_channelTransferVO.setFromUserName(rs.getString("fromusername"));
                p_channelTransferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getTransferDate())));
                p_channelTransferVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                p_channelTransferVO.setTransferType(rs.getString("transfer_type"));
                p_channelTransferVO.setTransferSubType(rs.getString("transfer_sub_type"));

                p_channelTransferVO.setTransferCategory(rs.getString("transfer_category"));
                p_channelTransferVO.setDefaultLang(rs.getString("sms_default_lang"));
                p_channelTransferVO.setSecondLang(rs.getString("sms_second_lang"));
                p_channelTransferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
                p_channelTransferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
                p_channelTransferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
                p_channelTransferVO.setWalletType(rs.getString("txn_wallet"));
                p_channelTransferVO.setControlTransfer(rs.getString("control_transfer"));

                p_channelTransferVO.setReceiverGgraphicalDomainCode(rs.getString("to_grph_domain_code"));
                p_channelTransferVO.setReceiverGgraphicalDomainCodeDesc(rs.getString("TO_GRPH_DOMAIN_NAME"));
                p_channelTransferVO.setReceiverDomainCode(rs.getString("to_domain_code"));
                p_channelTransferVO.setReceiverDomainCodeDesc(rs.getString("to_domain_name"));
                p_channelTransferVO.setFromUserCode(rs.getString("msisdn"));
                p_channelTransferVO.setToUserCode(rs.getString("to_msisdn"));
                p_channelTransferVO.setCommQty(rs.getLong("commision_quantity"));
                p_channelTransferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                p_channelTransferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                p_channelTransferVO.setCreatedBy(rs.getString("created_by"));
                p_channelTransferVO.setCreatedOn(rs.getDate("created_on"));
                p_channelTransferVO.setActiveUserId(rs.getString("active_user_id"));
                p_channelTransferVO.setPayInstrumentType(rs.getString("PMT_INST_TYPE"));
                p_channelTransferVO.setUserWalletCode(rs.getString("user_wallet"));
                p_channelTransferVO.setProductCode(rs.getString("product_code"));
                
                if (!BTSLUtil.isNullString(rs.getString("SOS_STATUS")))	p_channelTransferVO.setSosStatus(rs.getString("SOS_STATUS"));
                if (!BTSLUtil.isNullString(rs.getString("SOS_SETTLEMENT_DATE"))) {
                	String date = BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getTimestamp("SOS_STATUS")));
                	p_channelTransferVO.setSosSettlementDateAsString(date);
                }
                
                //adding network name
                NetworkDAO networkDAO = new NetworkDAO();
                if(p_channelTransferVO.getNetworkCode()!=null) p_channelTransferVO.setNetworkName(networkDAO.loadNetwork(p_con, p_channelTransferVO.getNetworkCode()).getNetworkName());
                if(p_channelTransferVO.getNetworkCodeFor()!=null) p_channelTransferVO.setNetworkNameFor(networkDAO.loadNetwork(p_con, p_channelTransferVO.getNetworkCodeFor()).getNetworkName());
                
                //getting sender grade name
                if(p_channelTransferVO.getSenderGradeCode()!=null && p_channelTransferVO.getFromUserID()!=null) {
                	LoginDAO loginDAO = new LoginDAO();
                	GradeVO gradeVO = loginDAO.loadUserDetailsOnTwoFAallowed(p_con, p_channelTransferVO.getFromUserID());
                	p_channelTransferVO.setSenderGradeCodeDesc(gradeVO.getGradeName());
                }
                
                
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "p_channelTransferVO ::: " + p_channelTransferVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : " );
        	loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersVO]", "", "",
                "",  loggerValue.toString());
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
                _log.debug(methodName, "Exiting:  ");
            }
        }
    }

    /**
     * Load the channel Transfer items list
     * 
     * @param p_con
     * @param p_transferId
     * 
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList<ChannelTransferItemsVO> loadChannelTransferItems(Connection p_con, String p_transferId) throws BTSLBaseException {

        final String methodName = "loadChannelTransferItems";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_transferId " );
        	loggerValue.append(p_transferId);
            _log.debug(methodName, loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // added by nilesh: Network stock for FOC and incentive
        final String sqlSelect = channelTransferQry.loadChannelTransferItemsQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = null;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            ChannelTransferItemsVO itemsVO = null;

            pstmt.setString(1, p_transferId);
            rs = pstmt.executeQuery();
            list = new ArrayList();

            Boolean multipleWalletApply = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
            Boolean othComChnl = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
            while (rs.next()) {
                if (rs.getString("wallet_type").equals(rs.getString("txn_wallet"))) {
                    itemsVO = new ChannelTransferItemsVO();
                    itemsVO.setWalletType(rs.getString("wallet_type"));
                    itemsVO.setProductMrpStr(PretupsBL.getDisplayAmount((rs.getLong("mrp"))));
                    itemsVO.setSerialNum(rs.getInt("s_no"));
                    itemsVO.setTransferID(rs.getString("transfer_id"));
                    itemsVO.setProductCode(rs.getString("product_code"));
                    itemsVO.setRequiredQuantity(rs.getLong("required_quantity"));
                    itemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(rs.getLong("required_quantity")));
                    itemsVO.setApprovedQuantity(rs.getLong("approved_quantity"));
                    itemsVO.setInitialRequestedQuantity(rs.getLong("required_quantity"));
                    itemsVO.setInitialRequestedQuantityStr(PretupsBL.getDisplayAmount(rs.getLong("required_quantity")));
                    itemsVO.setUnitValue(rs.getLong("user_unit_price"));
                    itemsVO.setCommProfileDetailID(rs.getString("commission_profile_detail_id"));
                    itemsVO.setCommType(rs.getString("commission_type"));
                    itemsVO.setCommRate(rs.getDouble("commission_rate"));
                    itemsVO.setProductTotalMRP(rs.getLong("mrp"));

                    itemsVO.setCommValue(rs.getLong("commission_value"));
                    itemsVO.setTax1Type(rs.getString("tax1_type"));
                    
                    itemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                    itemsVO.setTax1Value(rs.getLong("tax1_value"));
                    itemsVO.setTax2Type(rs.getString("tax2_type"));
              
                    itemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                    itemsVO.setTax2Value(rs.getLong("tax2_value"));
                    itemsVO.setTax3Type(rs.getString("tax3_type"));
                    
                    itemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                    itemsVO.setTax3Value(rs.getLong("tax3_value"));
                    itemsVO.setPayableAmount(rs.getLong("payable_amount"));
                    itemsVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                    itemsVO.setPayableAmountApproval(rs.getLong("payable_amount"));
                    itemsVO.setNetPayableAmountApproval(rs.getLong("net_payable_amount"));
                    itemsVO.setSenderPreviousStock(rs.getLong("sender_previous_stock"));
                    itemsVO.setReceiverPreviousStock(rs.getLong("receiver_previous_stock"));
                    itemsVO.setSenderPostStock(rs.getLong("SENDER_POST_STOCK")); // BUG
                    // FIX
                    // by
                    // AshishT
                    // for
                    // Mobinil5.7
                    itemsVO.setReceiverPostStock(rs.getLong("RECEIVER_POST_STOCK")); // BUG
                    // FIX
                    // by
                    // AshishT
                    // for
                    // Mobinil5.7
                    itemsVO.setShortName(rs.getString("short_name"));
                    itemsVO.setProductCode(rs.getString("product_code"));
                    itemsVO.setProductName(rs.getString("product_name"));
                    itemsVO.setWalletBalance(rs.getLong("wallet_balance"));
                    itemsVO.setUnitValue(rs.getLong("unit_value"));
                    itemsVO.setMinTransferValue(rs.getLong("min_transfer_value"));
                    itemsVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
                    itemsVO.setProductShortCode(rs.getLong("product_short_code"));
                    itemsVO.setPaymentType(rs.getString("PMT_INST_TYPE"));
                    if (BTSLUtil.isNullString(rs.getString("FIRST_LEVEL_APPROVED_QTY"))) {
                        itemsVO.setFirstApprovedQuantity(rs.getString("FIRST_LEVEL_APPROVED_QTY"));
                    } else {
                        itemsVO.setFirstApprovedQuantity(String.valueOf(rs.getDouble("FIRST_LEVEL_APPROVED_QTY")));
                    }
                    if (BTSLUtil.isNullString(rs.getString("SECOND_LEVEL_APPROVED_QTY"))) {
                        itemsVO.setSecondApprovedQuantity(rs.getString("SECOND_LEVEL_APPROVED_QTY"));
                    } else {
                        itemsVO.setSecondApprovedQuantity(String.valueOf(rs.getDouble("SECOND_LEVEL_APPROVED_QTY")));
                    }
                    if (BTSLUtil.isNullString(rs.getString("THIRD_LEVEL_APPROVED_QUANTITY"))) {
                        itemsVO.setThirdApprovedQuantity(rs.getString("THIRD_LEVEL_APPROVED_QUANTITY"));
                    } else {
                        itemsVO.setThirdApprovedQuantity(PretupsBL.getDisplayAmount(rs.getLong("THIRD_LEVEL_APPROVED_QUANTITY")));
                        
                    }
                   
                    // this is previous stock for sender(operator) order goes to
                    // approve
                    itemsVO.setAfterTransSenderPreviousStock(itemsVO.getWalletbalance());
                    itemsVO.setTransferMultipleOf(rs.getLong("transfer_multiple_off"));
                    // this is previous balance of receiver(Channel User) order
                    // goes
                    // to approve
                    itemsVO.setCommQuantity(rs.getLong("commision_quantity"));
                    itemsVO.setSenderDebitQty(rs.getLong("sender_debit_quantity"));
                    itemsVO.setReceiverCreditQty(rs.getLong("receiver_credit_quantity"));
                    itemsVO.setOtfTypePctOrAMt(rs.getString("otf_type"));
                    itemsVO.setOtfRate(rs.getDouble("otf_rate"));
                    itemsVO.setOtfAmount(rs.getLong("otf_amount"));
                    itemsVO.setNetworkCode(rs.getString("network_code"));
                    itemsVO.setOtfApplicable(rs.getString("otf_applicable"));
                    

                    if (multipleWalletApply) {
                        if (PretupsI.FOC_WALLET_TYPE.equals(rs.getString("wallet_type"))) {
                            itemsVO.setNetworkFOCStock(rs.getLong("wallet_balance"));
                        } else if (PretupsI.INCENTIVE_WALLET_TYPE.equals(rs.getString("wallet_type"))) {
                            itemsVO.setNetworkINCStock(rs.getLong("wallet_balance"));
                        }
                    } else {
                        itemsVO.setNetworkStock(rs.getLong("wallet_balance"));
                    }

                    // if(SystemPreferences.CELL_ID_SWITCH_ID_REQUIRED)

                    itemsVO.setCellId(rs.getString("cell_id"));
					if(othComChnl){
					itemsVO.setOthCommType(rs.getString("OTH_COMMISSION_TYPE"));
					itemsVO.setOthCommRate(rs.getDouble("OTH_COMMISSION_RATE"));
					itemsVO.setOthCommValue(rs.getLong("OTH_COMMISSION_VALUE"));
					itemsVO.setOthCommSetId(rs.getString("OTH_COMM_PRF_SET_ID"));
					}

                    /** START: Birendra: 28JaN2015 */
                    // itemsVO.setUserWallet(rs.getString("user_wallet"));
                    /** STOP: Birendra: 28JaN2015 */

                    list.add(itemsVO);
                }

            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception: " );
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[" + methodName + "]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[" + methodName + "]", "", "", "",
            		loggerValue.toString());
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
            	try {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size= ");
            	loggerValue.append( list.size());
                _log.debug(methodName, loggerValue);
            	}catch(Exception e) {_log.errorTrace(methodName, e);}
            }
        }
        return list;
    }

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param isOrderApproved
     * @return
     * @throws BTSLBaseException
     */
    public int updateChannelTransferApprovalLevelOne(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isOrderApproved) throws BTSLBaseException {
        final String methodName = "updateChannelTransferApprovalLevelOne";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue );
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE  channel_transfers SET  ");
            strBuff.append(" modified_by = ?, modified_on = ?,  pmt_inst_type = ?, pmt_inst_no = ? , pmt_inst_date = ?, ");
            strBuff.append(" first_approver_remarks = ?, first_approved_by = ?, first_approved_on = ?, status = ?,ext_txn_no = ? , ext_txn_date =  ? , reference_no = ?  ");
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
                strBuff.append(" ,first_level_approved_quantity = ?");
            }
            strBuff.append(" ,payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ? ");
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                strBuff.append(",close_date=? ");
            }
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(",stock_updated = ? ");
            }
            strBuff.append(", sms_default_lang=?, sms_second_lang=? ");

            strBuff.append(" WHERE transfer_id = ?  AND status IN (? , ? ) ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            // for multilanguage support
            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovalRemark());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            // added for editable reference number
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            
            
            
            // added for o2c transfer quantity change
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
                ++i;
                psmt.setLong(i, PretupsBL.getSystemAmount(p_channelTransferVO.getLevelOneApprovedQuantity()));
            }
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
            	++i;
                psmt.setString(i, TypesI.YES);
            }
            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());

            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            
            /**
             * to check the record is modified in between or not. if modified
             * then throw error message to the user
             */
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
         // added to make code compatible with insertion in partitioned table in postgres DB 
            updateCount = BTSLUtil.getInsertCount(updateCount);
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }

            // setting the productmrp
            if(p_channelTransferVO.getChannelTransferitemsVOList()!=null)
            {
            	ChannelTransferItemsVO itemsVO = ((ChannelTransferItemsVO) p_channelTransferVO.getChannelTransferitemsVOList().get(0));
            	itemsVO.setProductTotalMRP((p_channelTransferVO.getTransferMRP()));
            }
            
          //defect:incorrect data regarding sender and reciever's balance: added an arguement as transfer_id
            updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, true,p_channelTransferVO.getTransferID());
            

        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelOne]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelOne]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append( "Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        return updateCount;
    }
    public int updateChannelTransferApprovalLevelOneC2C(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isOrderApproved) throws BTSLBaseException {
        final String methodName = "updateChannelTransferApprovalLevelOneC2C";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue );
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE  channel_transfers SET  ");
            strBuff.append(" modified_by = ?, modified_on = ?,  pmt_inst_type = ?, pmt_inst_no = ? , pmt_inst_date = ?, ");
            strBuff.append(" first_approver_remarks = ?, first_approved_by = ?, first_approved_on = ?, status = ?,ext_txn_no = ? , ext_txn_date =  ? , reference_no = ?  ");
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
                strBuff.append(" ,first_level_approved_quantity = ?");
            }
            strBuff.append(" ,payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ? ");
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                strBuff.append(",close_date=? ");
            }
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(",stock_updated = ? ");
            }
            strBuff.append(", sms_default_lang=?, sms_second_lang=? ");

            strBuff.append(" WHERE transfer_id = ?  AND status = ? ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovalRemark());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
                ++i;
                psmt.setLong(i, PretupsBL.getSystemAmount(p_channelTransferVO.getLevelOneApprovedQuantity()));
            }
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
            	++i;
                psmt.setString(i, TypesI.YES);
            }
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCurStatus());
 
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
         // added to make code compatible with insertion in partitioned table in postgres DB 
            updateCount = BTSLUtil.getInsertCount(updateCount);
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }

            // setting the productmrp
            if(p_channelTransferVO.getChannelTransferitemsVOList()!=null && p_channelTransferVO.getChannelTransferitemsVOList().size() == 1)
            {
            	ChannelTransferItemsVO itemsVO = ((ChannelTransferItemsVO) p_channelTransferVO.getChannelTransferitemsVOList().get(0));
            	itemsVO.setProductTotalMRP((p_channelTransferVO.getTransferMRP()));
            }
            updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, false, p_channelTransferVO.getTransferID());
            

        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelOne]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelOneC2C]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append( "Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param isOrderApproved
     * @return
     * @throws BTSLBaseException
     */
    public int updateChannelTransferApprovalLevelTwo(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isOrderApproved) throws BTSLBaseException {
        final String methodName = "updateChannelTransferApprovalLevelTwo";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName, loggerValue);
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" UPDATE  channel_transfers SET ");
            strBuff.append(" modified_by = ?, modified_on = ?, ");
            strBuff.append(" second_approver_remarks = ?, second_approved_by = ? , second_approved_on = ?,status = ?,ext_txn_no = ? , ext_txn_date =  ? ,");
            strBuff.append(" pmt_inst_date = ?, pmt_inst_no = ?,pmt_inst_type=?, ");
            strBuff.append(" payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ? ");
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelTwoApprovedQuantity())) {
                strBuff.append(" ,second_level_approved_quantity = ? ");
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                strBuff.append(",close_date=? ");
            }
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(",stock_updated = ? ");
            }
            strBuff.append(", sms_default_lang=?, sms_second_lang=? ");
            strBuff.append(" WHERE transfer_id = ? AND status IN (? , ? ) ");

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("insert query:");
            	loggerValue.append(query);
                _log.debug(methodName, loggerValue );
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondApprovalRemark());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());

            // added for o2c transfer quantity change
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelTwoApprovedQuantity())) {
                ++i;
                psmt.setLong(i, PretupsBL.getSystemAmount(p_channelTransferVO.getLevelTwoApprovedQuantity()));
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
            }
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	 ++i;
                 psmt.setString(i, TypesI.YES);
            }
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());
            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            /**
             * to check the record is modified in between or not. if modified
             * then throw error message to the user
             */
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }

         // setting the productmrp
            if(p_channelTransferVO.getChannelTransferitemsVOList()!=null)
            {
            	ChannelTransferItemsVO itemsVO = ((ChannelTransferItemsVO) p_channelTransferVO.getChannelTransferitemsVOList().get(0));
            	itemsVO.setProductTotalMRP((p_channelTransferVO.getTransferMRP()));
            }
            
          //defect:incorrect data regarding sender and reciever's balance: added an arguement as transfer_id
            updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, true,p_channelTransferVO.getTransferID());

        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelTwo]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelTwo]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName,  loggerValue);
            }
        }// end of finally

        return updateCount;
    }

    public int updateChannelTransferApprovalLevelTwoC2C(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isOrderApproved) throws BTSLBaseException {
        final String methodName = "updateChannelTransferApprovalLevelTwoC2C";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName, loggerValue);
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" UPDATE  channel_transfers SET ");
            strBuff.append(" modified_by = ?, modified_on = ?, ");
            strBuff.append(" second_approver_remarks = ?, second_approved_by = ? , second_approved_on = ?,status = ?,ext_txn_no = ? , ext_txn_date =  ? , reference_no = ?, ");
            strBuff.append(" pmt_inst_date = ?, pmt_inst_no = ?,pmt_inst_type=?, ");
            strBuff.append(" payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ? ");
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelTwoApprovedQuantity())) {
                strBuff.append(" ,second_level_approved_quantity = ? ");
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                strBuff.append(",close_date=? ");
            }
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(",stock_updated = ? ");
            }
            strBuff.append(", sms_default_lang=?, sms_second_lang=? ");
            strBuff.append(" WHERE transfer_id = ? AND status = ? ");

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("insert query:");
            	loggerValue.append(query);
                _log.debug(methodName, loggerValue );
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondApprovalRemark());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());

            // added for o2c transfer quantity change
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelTwoApprovedQuantity())) {
                ++i;
                psmt.setLong(i, PretupsBL.getSystemAmount(p_channelTransferVO.getLevelTwoApprovedQuantity()));
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
            }
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	 ++i;
                 psmt.setString(i, TypesI.YES);
            }
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());
            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCurStatus());
            /**
             * to check the record is modified in between or not. if modified
             * then throw error message to the user
             */
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }

         // setting the productmrp
            if(p_channelTransferVO.getChannelTransferitemsVOList()!=null && p_channelTransferVO.getChannelTransferitemsVOList().size() == 1)
            {
            	ChannelTransferItemsVO itemsVO = ((ChannelTransferItemsVO) p_channelTransferVO.getChannelTransferitemsVOList().get(0));
            	itemsVO.setProductTotalMRP((p_channelTransferVO.getTransferMRP()));
            }
            
          //defect:incorrect data regarding sender and reciever's balance: added an arguement as transfer_id
            updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, false, p_channelTransferVO.getTransferID());

        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelTwoC2C]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelTwoC2C]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName,  loggerValue);
            }
        }// end of finally

        return updateCount;
    }
    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param isOrderApproved
     * @return
     * @throws BTSLBaseException
     */
    public int updateChannelTransferApprovalLevelThree(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isOrderApproved) throws BTSLBaseException {
        final String methodName = "updateChannelTransferApprovalLevelThree";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : " );
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName, loggerValue );
        }
        // commented for DB2
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;

        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" UPDATE  channel_transfers SET ");
            strBuff.append(" modified_by = ?, modified_on = ?, ");
            strBuff.append(" third_approver_remarks = ?, third_approved_by = ? , third_approved_on = ? , status = ? , ext_txn_no = ? , ext_txn_date =  ?, ");
            strBuff.append(" pmt_inst_date = ?, pmt_inst_no = ? ,pmt_inst_type=?, close_date=?, ");
            strBuff.append(" payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ? ");
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(",stock_updated = ? ");
            }
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelThreeApprovedQuantity())) {
                strBuff.append(" ,third_level_approved_quantity = ? ");
            }
            strBuff.append(", sms_default_lang=?, sms_second_lang=? ");
            strBuff.append(" WHERE transfer_id = ? AND status = ? ");

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getThirdApprovalRemark());
            ++i;
            psmt.setString(i, p_channelTransferVO.getThirdApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn()));
            // added for o2c transfer quantity change
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	 ++i;
                 psmt.setString(i, TypesI.YES);
            }
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelThreeApprovedQuantity())) {
                ++i;
                psmt.setLong(i, PretupsBL.getSystemAmount(p_channelTransferVO.getLevelThreeApprovedQuantity()));
            }
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());

            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);

            /**
             * to check the record is modified in between or not. if modified
             * then throw error message to the user
             */
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }

         // setting the productmrp
            if(p_channelTransferVO.getChannelTransferitemsVOList()!=null && p_channelTransferVO.getChannelTransferitemsVOList().size() == 1)
            {
            	ChannelTransferItemsVO itemsVO = ((ChannelTransferItemsVO) p_channelTransferVO.getChannelTransferitemsVOList().get(0));
            	itemsVO.setProductTotalMRP((p_channelTransferVO.getTransferMRP()));
            }
            
          //defect:incorrect data regarding sender and reciever's balance: added an arguement as transfer_id
            updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, true,p_channelTransferVO.getTransferID());

        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelThree]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelThree]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :" );
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue );
            }
        }// end of finally
        return updateCount;
    }

    public int updateChannelTransferApprovalLevelThreeC2C(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isOrderApproved) throws BTSLBaseException {
        final String methodName = "updateChannelTransferApprovalLevelThreeC2C";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : " );
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName, loggerValue );
        }
        // commented for DB2
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;

        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" UPDATE  channel_transfers SET ");
            strBuff.append(" modified_by = ?, modified_on = ?, ");
            strBuff.append(" third_approver_remarks = ?, third_approved_by = ? , third_approved_on = ? , status = ? , ext_txn_no = ? , ext_txn_date =  ?, reference_no = ?, ");
            strBuff.append(" pmt_inst_date = ?, pmt_inst_no = ? ,pmt_inst_type=?, close_date=?, ");
            strBuff.append(" payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ? ");
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(",stock_updated = ? ");
            }
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelThreeApprovedQuantity())) {
                strBuff.append(" ,third_level_approved_quantity = ? ");
            }
            strBuff.append(", sms_default_lang=?, sms_second_lang=? ");
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(", cancelled_by=?, cancelled_on=? ");
            }
            strBuff.append(" WHERE transfer_id = ? AND status = ? ");
            
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getThirdApprovalRemark());
            ++i;
            psmt.setString(i, p_channelTransferVO.getThirdApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn()));
            // added for o2c transfer quantity change
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	 ++i;
                 psmt.setString(i, TypesI.YES);
            }
            if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelThreeApprovedQuantity())) {
                ++i;
                psmt.setLong(i, PretupsBL.getSystemAmount(p_channelTransferVO.getLevelThreeApprovedQuantity()));
            }
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());
            if(p_channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL))
            {
            	++i;
                psmt.setString(i, p_channelTransferVO.getCanceledBy());
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCanceledOn()));
            }
            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCurStatus());
            
            
            /**
             * to check the record is modified in between or not. if modified
             * then throw error message to the user
             */
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }

         // setting the productmrp
            if(p_channelTransferVO.getChannelTransferitemsVOList()!=null)
            {
            	ChannelTransferItemsVO itemsVO = ((ChannelTransferItemsVO) p_channelTransferVO.getChannelTransferitemsVOList().get(0));
            	itemsVO.setProductTotalMRP((p_channelTransferVO.getTransferMRP()));
            }
            if(!(p_channelTransferVO.getApprRejStatus().equals(PretupsI.C2C_TRF_APPRV_REJ_STATUS)))
            	updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, true,p_channelTransferVO.getTransferID());

        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelThreeC2C]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception " );
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApprovalLevelThreeC2C]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :" );
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue );
            }
        }// end of finally
        return updateCount;
    }
    /**
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @param p_currentApprovalLevel
     * @return
     * @throws BTSLBaseException
     */
    public int cancelTransferOrder(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_currentApprovalLevel) throws BTSLBaseException {
        final String methodName = "cancelTransferOrder";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : ");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append(" CurrentApprovalLevel ");
        	loggerValue.append(p_currentApprovalLevel);
            _log.debug(methodName,  loggerValue);
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int deleteCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" UPDATE  channel_transfers SET   ");
            strBuff.append(" modified_by = ?, modified_on = ?,  ");
            if (p_channelTransferVO.isReconciliationFlag()) {
                strBuff.append(" reconciliation_by = ?, reconciliation_date = ? ,reconciliation_flag = ?, reconciliation_remark = ?, ");
                }
            else    
            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentApprovalLevel)) {
                strBuff.append(" first_approver_remarks = ?, ");
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentApprovalLevel)) {
                strBuff.append(" second_approver_remarks = ?, ");
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentApprovalLevel)) {
                strBuff.append(" third_approver_remarks = ?, ");
            }
            strBuff.append(" cancelled_by = ?, ");
            strBuff.append(" cancelled_on = ?, status = ?");
            strBuff.append(", ext_txn_no=?, ext_txn_date=?, reference_no=? ");
            strBuff.append(", pmt_inst_date=?, pmt_inst_no=?, pmt_inst_type=?, pmt_inst_status=? ");
            strBuff.append(", sms_default_lang=?, sms_second_lang=? ");
            strBuff.append(" WHERE ");
            strBuff.append(" transfer_id = ? ");
            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentApprovalLevel)) {
            	if(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING.equals(p_channelTransferVO.getPreviousStatus()))
            	{
            		strBuff.append(" AND status IN (? )  ");
            	}
            	else
            		{
            		strBuff.append(" AND status IN (? , ? )  ");
            		}
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentApprovalLevel)) {
                strBuff.append(" AND status IN (? , ? )  ");
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentApprovalLevel)) {
                strBuff.append(" AND status  = ?   ");
            }

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));

            if (p_channelTransferVO.isReconciliationFlag()) {
           	 ++i;
                psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
            	++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
                ++i;
                psmt.setString(i, PretupsI.YES);
            	++i;
                psmt.setString(i, p_channelTransferVO.getFirstApprovalRemark());
           }
            // for multilanguage support
            else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentApprovalLevel)) {
                // commented for DB2 psmt.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                ++i;
                psmt.setString(i, p_channelTransferVO.getFirstApprovalRemark());
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentApprovalLevel)) {
                // commented for DB2 psmt.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                ++i;
                psmt.setString(i, p_channelTransferVO.getSecondApprovalRemark());
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentApprovalLevel)) {
                // commented for DB2 psmt.setFormOfUse(++i,
                // OraclePreparedStatement.FORM_NCHAR);
                ++i;
                psmt.setString(i, p_channelTransferVO.getThirdApprovalRemark());
            }
            ++i;
            psmt.setString(i, p_channelTransferVO.getCanceledBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCanceledOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentStatus());

            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());

            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());

            if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentApprovalLevel)) {
            	if(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING.equals(p_channelTransferVO.getPreviousStatus()))
            	{
            		++i;
            		 psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
            	}
            	else
            	{ 
            	++i;
                psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++i;
                psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            	}
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentApprovalLevel)) {
                ++i;
                psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++i;
                psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentApprovalLevel)) {
                ++i;
                psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            }
            /*
             * to check the record is modified in between or not. if modified
             * then throw error message to the user
             */
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            deleteCount = psmt.executeUpdate();
            if (deleteCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[cancelTransferOrder]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append( "Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[cancelTransferOrder]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(deleteCount);
                _log.debug(methodName,  loggerValue );
            }
        }// end of finally

        return deleteCount;
    }

    /**
     * Update the channel Transfer Items
     * 
     * @param p_con
     * @param p_itemsList
     * @param isClosed
     * @param p_withPostStock
     * @param p_transferID
     * @return int
     * @throws BTSLBaseException
     */
    private int updateChannelTransferItems(Connection p_con, ArrayList p_itemsList, boolean isClosed, boolean p_withPostStock,String p_transferID) throws BTSLBaseException {
        final String methodName = "updateChannelTransferItems";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered ItemList : " );
        	loggerValue.append(p_itemsList);
        	loggerValue.append(" isClosed=");
        	loggerValue.append(isClosed);
        	loggerValue.append(" p_withPostStock=");
        	loggerValue.append(p_withPostStock);
            _log.debug(methodName, loggerValue );
        }
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" UPDATE channel_transfers_items SET  ");
            if (isClosed) {
                strBuff.append(" sender_previous_stock = ?, receiver_previous_stock = ?,");
            }
            strBuff.append("  approved_quantity = ?, payable_amount = ?, net_payable_amount = ?, sender_debit_quantity = ?,");
            strBuff.append(" mrp = ?, receiver_credit_quantity = ?, commision_quantity = ?, commission_value = ? ,commission_rate = ? ");
            strBuff.append(" , sender_post_stock=?, receiver_post_stock=? , otf_type=?, otf_rate=? ,otf_amount=?, tax1_value=?, tax2_value=?, tax3_value=?, otf_applicable=?,first_level_approved_qty=?,second_level_approved_qty=?");
            strBuff.append("  WHERE ");
            strBuff.append(" transfer_id = ?  ");
            strBuff.append(" AND ");
            strBuff.append(" product_code = ? ");

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("insert query:");
            	loggerValue.append(query);
                _log.debug(methodName,  loggerValue );
            }

            psmt = p_con.prepareStatement(query);
            ChannelTransferItemsVO itemsVO = null;
            for (int i = 0, k = p_itemsList.size(); i < k; i++) {
                itemsVO = (ChannelTransferItemsVO) p_itemsList.get(i);
                int m = 0;

                if (isClosed) {
                    ++m;
                    psmt.setLong(m, itemsVO.getAfterTransSenderPreviousStock());
                    ++m;
                    psmt.setLong(m, itemsVO.getAfterTransReceiverPreviousStock());
                }
                ++m;
                psmt.setLong(m, itemsVO.getApprovedQuantity());
                ++m;
                psmt.setLong(m, itemsVO.getPayableAmount());
                ++m;
                psmt.setLong(m, itemsVO.getNetPayableAmount());
                ++m;
                psmt.setLong(m, itemsVO.getSenderDebitQty());
                ++m;
                psmt.setLong(m, itemsVO.getReceiverCreditQty());
                ++m;
                psmt.setLong(m, itemsVO.getReceiverCreditQty());
                ++m;
                psmt.setLong(m, itemsVO.getCommQuantity());
                ++m;
                psmt.setLong(m, itemsVO.getCommValue());
                ++m;
                psmt.setDouble(m,itemsVO.getCommRate());
                if (p_withPostStock) {
                    ++m;
                    psmt.setLong(m, itemsVO.getAfterTransSenderPreviousStock() - itemsVO.getSenderDebitQty());
                    ++m;
                    psmt.setLong(m, itemsVO.getAfterTransReceiverPreviousStock() + itemsVO.getReceiverCreditQty());
                }
                else
                {
                	 ++m;
                     psmt.setLong(m, itemsVO.getAfterTransSenderPreviousStock() );
                     ++m;
                     psmt.setLong(m, itemsVO.getAfterTransReceiverPreviousStock() + itemsVO.getReceiverCreditQty());
                }
                ++m;
                psmt.setString(m ,itemsVO.getOtfTypePctOrAMt() );
                ++m;
                psmt.setDouble(m, itemsVO.getOtfRate());
                ++m;
                psmt.setLong(m, itemsVO.getOtfAmount());
                ++m;
                psmt.setLong(m, itemsVO.getTax1Value());
                ++m;
                psmt.setLong(m, itemsVO.getTax2Value());
                ++m;
                psmt.setLong(m, itemsVO.getTax3Value());
                ++m;
                psmt.setString(m, itemsVO.isOtfApplicable());
                ++m;
                psmt.setString(m, itemsVO.getFirstApprovedQuantity());
                ++m;
                psmt.setString(m, itemsVO.getSecondApprovedQuantity());
                ++m;
                psmt.setString(m, p_transferID);
                ++m;
                psmt.setString(m, itemsVO.getProductCode());
                

                updateCount = psmt.executeUpdate();

                psmt.clearParameters();
             // added to make code compatible with insertion in partitioned table in postgres DB 
                updateCount = BTSLUtil.getInsertCount(updateCount);
                if (updateCount < 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            
            loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelTransferItems]", "",
                "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, e);
            
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue );
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelTransferItems]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName,  loggerValue );
            }
        }// end of finally

        return updateCount;
    }
    
    public int updateChannelTransferItemsForVoucher(Connection p_con, ArrayList p_itemsList) throws BTSLBaseException {
        final String methodName = "updateChannelTransferItemsForVoucher";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        int update_count = 0;
        
        final StringBuffer strBuffer = new StringBuffer(" UPDATE channel_transfers_items SET  ");
        strBuffer.append(" required_quantity = ?, payable_amount = ?, net_payable_amount = ? ");
        strBuffer.append(" ,mrp = ?, commision_quantity = ?, commission_value = ? ");
        strBuffer.append(" ,tax1_value=?, tax2_value=?, tax3_value=? ");
        strBuffer.append(" ,otf_type=?, otf_rate=? ,otf_amount=?, otf_applicable=?, ");
        strBuffer.append(" first_level_approved_qty=?, second_level_approved_qty=? WHERE ");
        strBuffer.append(" transfer_id = ?  ");
        strBuffer.append(" AND ");
        strBuffer.append(" S_NO = ? ");
        final String sqlSlt = strBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSlt=" + sqlSlt);
        }
       
        try(PreparedStatement psmt = p_con.prepareStatement(sqlSlt); ) {
        	ChannelTransferItemsVO itemsVO = null;
            int m = 0;
            for (int i = 0, k = p_itemsList.size(); i < k; i++) {
            	m = 0;
            	itemsVO = (ChannelTransferItemsVO) p_itemsList.get(i);
                ++m;
                psmt.setLong(m, itemsVO.getRequiredQuantity());
                ++m;
                psmt.setLong(m, itemsVO.getPayableAmount());
                ++m;
                psmt.setLong(m, itemsVO.getNetPayableAmount());
                ++m;
                psmt.setLong(m, itemsVO.getProductTotalMRP());
                ++m;
                psmt.setLong(m, itemsVO.getCommQuantity());
                ++m;
                psmt.setLong(m, itemsVO.getCommValue());
                ++m;
                psmt.setLong(m, itemsVO.getTax1Value());
                ++m;
                psmt.setLong(m, itemsVO.getTax2Value());
                ++m;
                psmt.setLong(m, itemsVO.getTax3Value());
                ++m;
                psmt.setString(m ,itemsVO.getOtfTypePctOrAMt() );
                ++m;
                psmt.setDouble(m, itemsVO.getOtfRate());
                ++m;
                psmt.setLong(m, itemsVO.getOtfAmount());
                ++m;
                psmt.setString(m, itemsVO.isOtfApplicable());
                ++m;
                psmt.setString(m, itemsVO.getFirstApprovedQuantity());
                ++m;
                psmt.setString(m, itemsVO.getSecondApprovedQuantity());
                ++m;
                psmt.setString(m, itemsVO.getTransferID());
                ++m;
                psmt.setLong(m, itemsVO.getSerialNum());
                update_count = psmt.executeUpdate();

                psmt.clearParameters();
                // added to make code compatible with insertion in partitioned table in postgres DB 
                update_count = BTSLUtil.getInsertCount(update_count);
                if (update_count < 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelVoucherItems]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelTransferItemsForVoucher", "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelTransferItemsForVoucher]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelTransferItemsForVoucher", "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                  loggerValue.setLength(0);
                  loggerValue.append("Exiting: update count  =");
                  loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue);
            }
        }
        return update_count;

    }

    /**
     * Load the User Product Balance List
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_userID
     * @param p_productType
     *            in case of o2c it will be used other wise it will be null in
     *            case of C2C
     * 
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadUserProductBalanceList(Connection p_con, String p_networkCode, String p_networkCodeFor, String p_userID, String p_productType) throws BTSLBaseException {
        final String methodName = "loadUserProductBalanceList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  p_NetworkCode For :");
        	loggerValue.append(p_networkCodeFor);
        	loggerValue.append(" Network Code ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_userID ");
        	loggerValue.append(p_userID);
        	loggerValue.append(" p_productType " );
        	loggerValue.append(p_productType);
            _log.debug(methodName,loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(" SELECT p.product_code,p.short_name,p.unit_value, ");
        strBuff.append(" p.product_short_code,ub.balance,p.product_type ");
        strBuff.append(" FROM user_balances ub,products p ");
        strBuff.append(" WHERE ");
        strBuff.append(" ub.user_id = ?  ");
        strBuff.append(" AND ub.network_code = ?  ");
        if (!BTSLUtil.isNullString(p_productType)) {
            strBuff.append(" AND p.product_type = ?  ");
        }
        strBuff.append(" AND ub.network_code_for = ? AND p.product_code = ub.product_code ORDER BY p.short_name  ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList userBalanceProductList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_userID);
            ++m;
            pstmt.setString(m, p_networkCode);
            if (!BTSLUtil.isNullString(p_productType)) {
                ++m;
                pstmt.setString(m, p_productType);
            }
            ++m;
            pstmt.setString(m, p_networkCodeFor);

            rs = pstmt.executeQuery();
            UserBalancesVO balancesVO = null;
            while (rs.next()) {
                balancesVO = new UserBalancesVO();

                balancesVO.setProductCode(rs.getString("product_code"));
                balancesVO.setProductShortName(rs.getString("short_name"));
                balancesVO.setUnitValue(rs.getLong("unit_value"));
                balancesVO.setBalance(rs.getLong("balance"));
                balancesVO.setProductType(rs.getString("product_type"));
                balancesVO.setProductShortCode(rs.getString("product_short_code"));
                balancesVO.setNetworkCode(p_networkCode);
                balancesVO.setNetworkFor(p_networkCodeFor);
                balancesVO.setUserID(p_userID);
                if (balancesVO.getBalance() > 0) {
                    userBalanceProductList.add(balancesVO);
                }
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append( sqe.getMessage());
            _log.error(methodName,  loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadUserProductBalanceList]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            _log.error("", "Exception : " + ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadUserProductBalanceList]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(userBalanceProductList.size());
                _log.debug("",  loggerValue);
            }
        }
        return userBalanceProductList;
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
    public ArrayList loadEnquiryChannelTransfersList(Connection p_con, String p_transferID, String p_userID, Date p_fromDate, Date p_toDate, String p_status, String p_transferTypeCode, String p_productType, String p_transferCategory, String p_userCode) throws BTSLBaseException {

        final String methodName = "loadEnquiryChannelTransfersList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  TransferNumber: ");
        	loggerValue.append(p_transferID);
        	loggerValue.append( " UserID: ");
        	loggerValue.append(p_userID);
        	loggerValue.append(" FromDate:" );
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" ToDate:");
        	loggerValue.append(p_toDate);
        	loggerValue.append(" Status:");
        	loggerValue.append(p_status);
        	loggerValue.append(",p_transferTypeCode=" );
        	loggerValue.append(p_transferTypeCode);
        	loggerValue.append(", Product Type:");
        	loggerValue.append(p_productType);
        	loggerValue.append(",p_transferCategory=");
        	loggerValue.append(p_transferCategory);
        	loggerValue.append(", p_userCode=");
        	loggerValue.append(p_userCode);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String isPrimary = null;
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
            if (BTSLUtil.isNullString(p_transferID) && (!BTSLUtil.isNullString(p_userCode))) {
                final UserDAO userDAO = new UserDAO();
                UserPhoneVO userPhoneVO = null;
                userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_userCode);
                if (userPhoneVO != null) {
                    isPrimary = userPhoneVO.getPrimaryNumber();
                }
            }
        }

        StringBuilder strBuff = channelTransferQry.loadEnquiryChannelTransfersListQry(isPrimary,p_transferID,p_userCode,p_status,p_transferCategory,p_transferTypeCode);
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList enquiryItemsList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            if (!BTSLUtil.isNullString(p_transferID)) {
                ++m;
                pstmt.setString(m, p_transferID);
            } else if (!BTSLUtil.isNullString(p_userCode)) {
                ++m;
                pstmt.setString(m, p_userCode);
                if (p_fromDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                if (p_toDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                ++m;
                pstmt.setString(m, p_transferCategory);
            } else {
                if (p_fromDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                if (p_toDate != null) {
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                } else {
                    ++m;
                    pstmt.setDate(m, null);
                }
                ++m;
                pstmt.setString(m, p_productType);
                ++m;
                pstmt.setString(m, p_transferCategory);
                if (!PretupsI.ALL.equals(p_status) && (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_transferTypeCode) || PretupsI.TRANSFER_CATEGORY_TRANSFER
                    .equals(p_transferCategory))) {
                    ++m;
                    pstmt.setString(m, p_status);
                }
                if (!PretupsI.ALL.equals(p_transferTypeCode) && PretupsI.TRANSFER_CATEGORY_SALE.equals(p_transferCategory)) {
                    ++m;
                    pstmt.setString(m, p_transferTypeCode);
                }
                ++m;
                pstmt.setString(m, p_userID);

                if (PretupsI.ALL.equals(p_transferTypeCode)) {
                    ++m;
                    pstmt.setString(m, p_userID);
                }
            }
            ++m;
            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            Boolean channelTransfersInfoRequired = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferSubTypeValue(rs.getString("lookup_name"));
                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                transferVO.setToUserName(rs.getString("user_name"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                if (transferVO.getTransferDate() != null) {
                    transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
                }
                transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                transferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                transferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
                transferVO.setCanceledBy(rs.getString("cancelled_by"));
                transferVO.setCanceledOn(rs.getDate("cancelled_on"));

                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("from_msisdn"));
                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
                    transferVO.setUserMsisdn(rs.getString("to_msisdn"));
                } else {
                    transferVO.setUserMsisdn(rs.getString("msisdn"));
                }
                transferVO.setTransferCategory(rs.getString("transfer_category"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
                transferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
                transferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
                transferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                transferVO.setFromUserID(rs.getString("from_user_id"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setDomainCode(rs.getString("domain_code"));
                transferVO.setPaymentInstType(rs.getString("PMT_INST_TYPE"));
                if(!BTSLUtil.isNullString(rs.getString("pmt_inst_type"))) {
                	transferVO.setPayInstrumentName(((LookupsVO) LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE, rs.getString("pmt_inst_type"))).getLookupName());
                }
                // added by amit for o2c transfer quantity change
                transferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
                transferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
                transferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, transferVO.getStatus())).getLookupName());
                }
                // Added By Babu Kunwar For displaying Balnce in O2C Enquiry
                transferVO.setSenderPostStock(rs.getString("SENDER_POST_STOCK"));
                transferVO.setSenderPreviousStock(rs.getLong("SENDER_PREVIOUS_STOCK"));
                transferVO.setReceiverPostStock(rs.getString("RECEIVER_POST_STOCK"));
                transferVO.setReceiverPreviousStock(rs.getLong("RECEIVER_PREVIOUS_STOCK"));
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
			        transferVO.setSosStatus(rs.getString("SOS_STATUS"));
			        transferVO.setSosSettlementDate(rs.getDate("SOS_SETTLEMENT_DATE"));
			        }
                if (transferVO.getThirdApprovedBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getThirdApprovedByName());
                    if (transferVO.getThirdApprovedOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getThirdApprovedOn())));
                    }
                } else if (transferVO.getSecondApprovedBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getSecondApprovedByName());
                    if (transferVO.getSecondApprovedOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getSecondApprovedOn())));
                    }
                } else if (transferVO.getFirstApprovedBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getFirstApprovedByName());
                    if (transferVO.getFirstApprovedOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getFirstApprovedOn())));
                    }
                }

                if (transferVO.getCanceledBy() != null) {
                    transferVO.setFinalApprovedBy(transferVO.getCanceledByApprovedName());
                    if (transferVO.getCanceledOn() != null) {
                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getCanceledOn())));
                    }
                }
                transferVO.setTransactionMode(rs.getString("transaction_mode"));
                transferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
                transferVO.setOtfTypePctOrAMt(rs.getString("otf_type"));
                transferVO.setOtfRate(rs.getDouble("otf_rate"));
                transferVO.setOtfAmount(rs.getLong("otf_amount"));
                
                if(channelTransfersInfoRequired)
                {
                	transferVO.setInfo1(rs.getString("info1"));
                	transferVO.setInfo2(rs.getString("info2"));
                }
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                enquiryItemsList.add(transferVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception:");
             loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
             loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(ex.getMessage());;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(enquiryItemsList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return enquiryItemsList;
    }

    /**
     * This function loads the details of channel to channel thansfer
     * 
     * @param p_con
     * @param p_fromUserCode
     * @param p_toUserCode
     * @param p_fromDate
     * @param p_toDate
     * @param p_transferNum
     * @param p_type
     * @param p_transferTypeCode
     * @return
     * @throws BTSLBaseException
     * */
    public ArrayList loadChnlToChnlEnquiryTransfersList(Connection p_con, String p_fromUserCode, String p_toUserCode, Date p_fromDate, Date p_toDate, String p_transferNum, String p_type, String p_transferTypeCode) throws BTSLBaseException {

        final String methodName = "loadChnlToChnlEnquiryTransfersList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  fromUserCode: ");
        	loggerValue.append(p_fromUserCode);
        	loggerValue.append(" toUserCode: ");
        	loggerValue.append(p_toUserCode);
        	loggerValue.append(" FromDate:" );
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" ToDate:");
        	loggerValue.append(p_toDate);
        	loggerValue.append(" transferNum:");
        	loggerValue.append(p_transferNum);
        	loggerValue.append(" TYPE: ");
        	loggerValue.append(p_type);
        	loggerValue.append("Transfer type: ");
        	loggerValue.append(p_transferTypeCode);
            _log.debug(methodName,loggerValue );
        }
        String isFromUserPrimary = null;
        String isToUserPrimary = null;
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
            if (BTSLUtil.isNullString(p_transferNum)) {
                final UserDAO userDAO = new UserDAO();
                UserPhoneVO userPhoneVO = null;
                if (!BTSLUtil.isNullString(p_fromUserCode)) {
                    userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_fromUserCode);
                    if (userPhoneVO != null && ("N".equalsIgnoreCase(userPhoneVO.getPrimaryNumber()))) {
                        isFromUserPrimary = userPhoneVO.getPrimaryNumber();
                    }
                }
                if (!BTSLUtil.isNullString(p_toUserCode)) {
                    userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_toUserCode);
                    if (userPhoneVO != null && ("N".equalsIgnoreCase(userPhoneVO.getPrimaryNumber()))) {
                        isToUserPrimary = userPhoneVO.getPrimaryNumber();
                    }
                }
            }
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Boolean channelTransfersInfoRequired = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
        final StringBuffer strBuff = new StringBuffer("SELECT ct.transfer_id,ct.network_code,ct.network_code_for, ");
        strBuff.append("ct.grph_domain_code, ct.domain_code,ct.sender_category_code,ct.sender_grade_code, ");
        strBuff.append("ct.receiver_grade_code,ct.from_user_id,ct.to_user_id,ct.transfer_date,ct.transfer_MRP, ");
        strBuff.append("ct.reference_no,ct.requested_quantity,ct.channel_user_remarks,ct.type,ct.payable_amount, ");
        strBuff.append("ct.net_payable_amount,ct.pmt_inst_type,ct.pmt_inst_no,ct.pmt_inst_date,ct.pmt_inst_amount, ");
        strBuff.append("ct.total_tax1,ct.total_tax2,ct.total_tax3,ct.product_type,ct.transfer_sub_type, ct.transfer_type,");
        strBuff.append("ct.transfer_category,ct.source,ct.control_transfer,ct.msisdn from_msisdn, ct.to_msisdn to_msisdn, ");
        strBuff.append("u1.user_name frmuser,u2.user_name touser,u1.user_code frmcode,u2.user_code tocode, ");
        strBuff.append("l.lookup_name,ug2.grph_domain_code r_geo_code,u2.category_code r_cat_code, ");
        strBuff
            .append("cat2.category_name r_cat_name,cat2.domain_code r_dom_code,cat1.category_name s_cat_name,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append("cti.SENDER_POST_STOCK, cti.SENDER_PREVIOUS_STOCK, cti.RECEIVER_POST_STOCK, cti.RECEIVER_PREVIOUS_STOCK ");
        // if (SystemPreferences.CELL_ID_SWITCH_ID_REQUIRED)

        strBuff.append(" ,ct.cell_id,ct.switch_id ");
        if(channelTransfersInfoRequired)
        {
        	strBuff.append(",ct.info1,ct.info2 ");
        }
        strBuff.append("FROM channel_transfers ct, users u1, users u2 , lookups l,user_geographies ug2,categories cat1 ,categories cat2,channel_transfers_items cti ");
        if ((!BTSLUtil.isNullString(isFromUserPrimary)) || (!BTSLUtil.isNullString(isFromUserPrimary))) {
            strBuff.append(", user_phones up1, user_phones up2 ");
        }
        strBuff.append("WHERE ");
        if (!BTSLUtil.isNullString(p_transferNum)) {
            strBuff.append("ct.transfer_id=? AND ");
        } else {
            if (BTSLUtil.isNullString(isFromUserPrimary)) {
                strBuff.append("(u1.user_code=? OR u2.user_code=? ) ");
            } else {
                strBuff.append("(up1.msisdn=? OR up2.msisdn=? ) AND ");
                strBuff.append("(u1.user_id=up1.user_id OR u2.user_id=up2.user_id ) ");
            }
            if (!BTSLUtil.isNullString(p_toUserCode)) {
                if (BTSLUtil.isNullString(isFromUserPrimary)) {
                    strBuff.append("AND (u2.user_code=? OR u1.user_code=? )");
                } else {
                    strBuff.append("(up2.msisdn=? OR up1.msisdn=? ) AND ");
                    strBuff.append("(u2.user_id=up2.user_id OR u1.user_id=up1.user_id ) ");
                }
            }
            strBuff.append("AND ct.transfer_date >= ? AND ct.transfer_date < ? AND ");

            if (!PretupsI.ALL.equals(p_transferTypeCode)) {
                strBuff.append("ct.transfer_sub_type=? AND ");
            }
        }
        // strBuff.append(" u1.status <> 'N' AND u1.status <> 'C' AND u2.status <> 'N' AND u2.status <> 'C' ");
        strBuff.append(" u2.user_id=ug2.user_id AND u2.category_code=cat2.category_code AND u1.category_code=cat1.category_code AND ");
        strBuff
            .append("ct.type=? AND ct.from_user_id=u1.user_id AND ct.to_user_id=u2.user_id AND l.lookup_code=ct.transfer_sub_type AND l.lookup_type=? AND ct.transfer_id=cti.transfer_id ");
        if (!BTSLUtil.isNullString(isFromUserPrimary) || (!BTSLUtil.isNullString(isFromUserPrimary))) {
            strBuff.append(" AND ct.msisdn=up1.msisdn AND ct.to_msisdn=up2.msisdn ");
        }
        strBuff.append("ORDER BY ct.created_on DESC ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList enquiryItemsList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            if (!BTSLUtil.isNullString(p_transferNum)) {
                ++m;
                pstmt.setString(m, p_transferNum);
            } else {
                ++m;
                pstmt.setString(m, p_fromUserCode);
                ++m;
                pstmt.setString(m, p_fromUserCode);
                if (!BTSLUtil.isNullString(p_toUserCode)) {
                    ++m;
                    pstmt.setString(m, p_toUserCode);
                    ++m;
                    pstmt.setString(m, p_toUserCode);
                }
                ++m;
                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                ++m;
                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
                if (!PretupsI.ALL.equals(p_transferTypeCode)) {
                    ++m;
                    pstmt.setString(m, p_transferTypeCode);
                }
            }
            ++m;
            pstmt.setString(m, p_type);
            ++m;
            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setNetworkCode(rs.getString("network_code"));
                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                transferVO.setDomainCode(rs.getString("domain_code"));
                transferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
                transferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
                transferVO.setFromUserID(rs.getString("from_user_id"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setTransferDateAsString(BTSLUtil.getDateStringFromDate(rs.getDate("transfer_date")));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                transferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
                transferVO.setType(rs.getString("type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                if(!BTSLUtil.isNullString(rs.getString("pmt_inst_type"))) {
                	transferVO.setPayInstrumentName(((LookupsVO) LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE, rs.getString("pmt_inst_type"))).getLookupName());
                }
                transferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
                transferVO.setPayInstrumentDate(rs.getDate("pmt_inst_date"));
                transferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
                transferVO.setTotalTax1(rs.getLong("total_tax1"));
                transferVO.setTotalTax2(rs.getLong("total_tax2"));
                transferVO.setTotalTax3(rs.getLong("total_tax3"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setFromUserCode(rs.getString("from_msisdn"));
                transferVO.setToUserCode(rs.getString("to_msisdn"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setFromUserName(rs.getString("frmuser"));
                transferVO.setToUserName(rs.getString("touser"));
                transferVO.setTransferSubType(rs.getString("lookup_name"));
                transferVO.setTransferCategoryCode(rs.getString("transfer_category"));
                transferVO.setSource(BTSLUtil.getOptionDesc(rs.getString("source"), sourceTypeList).getLabel());
                transferVO.setControlTransfer(rs.getString("control_transfer"));
                transferVO.setReceiverCategoryCode(rs.getString("r_cat_code"));
                transferVO.setReceiverCategoryDesc(rs.getString("r_cat_name"));
                transferVO.setReceiverGgraphicalDomainCode(rs.getString("r_geo_code"));
                transferVO.setReceiverDomainCode(rs.getString("r_dom_code"));
                transferVO.setSenderCatName(rs.getString("s_cat_name"));
                transferVO.setCommQty(rs.getLong("commision_quantity"));
                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                // Added By Babu Kunwar for displaying user balance.
                transferVO.setSenderPostStock(rs.getString("SENDER_POST_STOCK"));
                transferVO.setSenderPreviousStock(rs.getLong("SENDER_PREVIOUS_STOCK"));
                transferVO.setReceiverPostStock(rs.getString("RECEIVER_POST_STOCK"));
                transferVO.setReceiverPreviousStock(rs.getLong("RECEIVER_PREVIOUS_STOCK"));

                // to display cellid & switch id
                // if(SystemPreferences.CELL_ID_SWITCH_ID_REQUIRED)

                transferVO.setCellId(rs.getString("cell_id"));
                transferVO.setSwitchId(rs.getString("switch_id"));
                
                if(channelTransfersInfoRequired)
                {
                	transferVO.setInfo1(rs.getString("info1"));
                	transferVO.setInfo2(rs.getString("info2"));
                }

                enquiryItemsList.add(transferVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append( "SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[loadChnlToChnlEnquiryTransfersList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error("",  loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[loadChnlToChnlEnquiryTransfersList]", "", "", "",  loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(enquiryItemsList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return enquiryItemsList;
    }

    /**
     * To Check whether Recoord id modidfied or not
     * 
     * @param p_con
     * @param p_oldlastModified
     * @param p_key
     * @return
     * @throws BTSLBaseException
     */
    public boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_key) throws BTSLBaseException {
        final String methodName = "isRecordModified";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:p_oldlastModified=");
        	loggerValue.append(p_oldlastModified);
        	loggerValue.append(",p_key=" );
        	loggerValue.append(p_key);
            _log.debug(methodName,  loggerValue );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        final StringBuffer sqlRecordModified = new StringBuffer("SELECT modified_on FROM channel_transfers ");
        sqlRecordModified.append("WHERE transfer_id = ? ");
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0) {
            return false;
        }
        try {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("QUERY=");
            	loggerValue.append(sqlRecordModified);
                _log.debug(methodName, loggerValue );
            }
            final String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_key);
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
        	loggerValue.append("SQLException:");
        	loggerValue.append(sqe.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isRecordModified]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append( "Exception:" );
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isRecordModified]", "", "", "",
            		loggerValue.toString() );
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exititng:modified=");
            	loggerValue.append(modified);
                _log.debug(methodName,  loggerValue );
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method to update the transfer items of C2S in the database
     * 
     * @param p_con
     * @param p_c2sTransferItemVO
     * @param p_transferID
     * @return
     * @throws BTSLBaseException
     */
    // public int updateC2STransferItemDetails(Connection p_con,
    // C2STransferItemVO p_c2sTransferItemVO, String p_transferID) throws
    // BTSLBaseException
    // {
    // if (_log.isDebugEnabled())
    // _log.debug("updateC2STransferItemDetails", "Entered p_transferID:" +
    // p_transferID);
    // PreparedStatement pstmtUpdate = null;
    // int updateCount = 0;
    // try
    // {
    // int i = 1;
    // StringBuffer updateQueryBuff = new
    // StringBuffer(" UPDATE c2s_transfer_items SET previous_balance=?, post_balance=?, ");
    // updateQueryBuff.append(" validation_status=?, update_status=?, protocol_status=?,account_status=?,");
    // updateQueryBuff.append(" transfer_value=?, interface_response_code=?, msisdn_previous_expiry=?, ");
    // updateQueryBuff.append(" msisdn_new_expiry=?, transfer_status=?, first_call=? , interface_reference_id=?, ");
    // updateQueryBuff.append(" adjust_dr_txn_type=?, adjust_dr_txn_id=?, adjust_dr_update_status=?, adjust_cr_txn_type=?, adjust_cr_txn_id=?, adjust_cr_update_status=? , adjust_value=? , ");
    // updateQueryBuff.append(" reference_id=? ");
    // updateQueryBuff.append(" WHERE  transfer_id=? AND msisdn=? AND sno=?");
    // String updateQuery = updateQueryBuff.toString();
    // if (_log.isDebugEnabled())
    // _log.debug("updateTransferItemDetails", "Insert query:" + updateQuery);
    //
    // pstmtUpdate = p_con.prepareStatement(updateQuery);
    // pstmtUpdate.setLong(i++, p_c2sTransferItemVO.getPreviousBalance());
    // pstmtUpdate.setLong(i++, p_c2sTransferItemVO.getPostBalance());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getValidationStatus());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getUpdateStatus());
    // pstmtUpdate.setString(i++,
    // BTSLUtil.NullToString(p_c2sTransferItemVO.getProtocolStatus()));
    // pstmtUpdate.setString(i++,
    // BTSLUtil.NullToString(p_c2sTransferItemVO.getAccountStatus()));
    // pstmtUpdate.setLong(i++, p_c2sTransferItemVO.getTransferValue());
    // pstmtUpdate.setString(i++,
    // BTSLUtil.NullToString(p_c2sTransferItemVO.getInterfaceResponseCode()));
    // if(p_c2sTransferItemVO.getPreviousExpiry()!=null)
    // pstmtUpdate.setDate(i++,
    // BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferItemVO.getPreviousExpiry()));
    // else
    // pstmtUpdate.setNull(i++,Types.DATE);
    // if(p_c2sTransferItemVO.getNewExpiry()!=null)
    // pstmtUpdate.setDate(i++,
    // BTSLUtil.getSQLDateFromUtilDate(p_c2sTransferItemVO.getNewExpiry()));
    // else
    // pstmtUpdate.setNull(i++,Types.DATE);
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getTransferStatus());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getFirstCall());
    // pstmtUpdate.setString(i++,
    // p_c2sTransferItemVO.getInterfaceReferenceID());
    //
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getTransferType2());
    // pstmtUpdate.setString(i++,
    // p_c2sTransferItemVO.getInterfaceReferenceID2());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getUpdateStatus2());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getTransferType1());
    // pstmtUpdate.setString(i++,
    // p_c2sTransferItemVO.getInterfaceReferenceID1());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getUpdateStatus1());
    // pstmtUpdate.setLong(i++, p_c2sTransferItemVO.getAdjustValue());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getReferenceID());
    //
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getTransferID());
    // pstmtUpdate.setString(i++, p_c2sTransferItemVO.getMsisdn());
    // pstmtUpdate.setInt(i++,p_c2sTransferItemVO.getSNo());
    // updateCount = pstmtUpdate.executeUpdate();
    // if (updateCount <= 0)
    // throw new BTSLBaseException(this, "updateC2STransferItemDetails",
    // "error.general.sql.processing");
    // return updateCount;
    // }// end of try
    // catch (SQLException sqle)
    // {
    // _log.error("updateC2STransferItemDetails", "SQLException " +
    // sqle.getMessage());
    // updateCount = 0;
    // sqle.printStackTrace();
    // EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
    // EventStatusI.RAISED, EventLevelI.FATAL,
    // "ChannelTransferDAO[updateC2STransferItemDetails]", p_transferID,
    // p_c2sTransferItemVO.getMsisdn(), "", "SQL Exception:" +
    // sqle.getMessage());
    // throw new BTSLBaseException(this, "updateC2STransferItemDetails",
    // "error.general.sql.processing");
    // }// end of catch
    // catch (Exception e)
    // {
    // _log.error("updateC2STransferItemDetails", "Exception " +
    // e.getMessage());
    // updateCount = 0;
    // e.printStackTrace();
    // EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
    // EventStatusI.RAISED, EventLevelI.FATAL,
    // "ChannelTransferDAO[updateC2STransferItemDetails]", p_transferID,
    // p_c2sTransferItemVO.getMsisdn(), "", "Exception:" + e.getMessage());
    // throw new BTSLBaseException(this, "updateC2STransferItemDetails",
    // "error.general.processing");
    // }// end of catch
    // finally
    // {
    // try{if (pstmtUpdate != null)pstmtUpdate.close();} catch (Exception e){}
    // if (_log.isDebugEnabled())
    // _log.debug("updateC2STransferItemDetails", "Exiting updateCount=" +
    // updateCount);
    // }// end of finally
    // }

    /**
     * isPendingTransactionExist
     * This method is to check that the user has any panding request of transfer
     * or not
     * 
     * @param p_con
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     *             boolean
     */
    public boolean isPendingTransactionExist(Connection p_con, String p_userID) throws BTSLBaseException {
        final String methodName = "isPendingTransactionExist";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_userID ");
        	loggerValue.append(p_userID);
            _log.debug(methodName,  loggerValue );
        }
        boolean isExist = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" SELECT 1  ");
            strBuff.append(" FROM channel_transfers ");
            strBuff.append(" WHERE (from_user_id=? OR to_user_id =?) AND ");
            strBuff.append(" (status <> ? AND status <> ? )");
            final String sqlSelect = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmt.setString(i, p_userID);
            i++;
            pstmt.setString(i, p_userID);
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
        	 loggerValue.setLength(0);
        	 loggerValue.append("SQLException : ");
        	 loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
             loggerValue.setLength(0);
       	     loggerValue.append("SQL Exception:");
       	     loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isPendingTransactionExist]", "",
                "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isPendingTransactionExist]", "",
                "", "",loggerValue.toString() );
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  isExist=");
            	loggerValue.append(isExist);
                _log.debug(methodName,  loggerValue);
            }
        }
        return isExist;
    }

    /**
     * Method addC2SReceiverRequests.
     * 
     * @param p_con
     *            Connection
     * @param p_requestVO
     *            RequestVO
     * 
     * @throws BTSLBaseException
     */

    public int addC2SReceiverRequests(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String methodName = "addC2SReceiverRequests";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "entered p_requestVO=" + p_requestVO.toString());
        }
        PreparedStatement pstmtInsert = null;
        int addCount = -1;
        int i = 0;
        try {
            final StringBuffer strBuff = new StringBuffer("INSERT into c2s_receiver_requests(request_id, request_msisdn, request_message, ");
            strBuff
                .append("service_type, source_type, type, instance_id, message_code, service_port, created_date, transaction_id, start_time, end_time,network_code,req_gateway_code)");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY= " + insertQuery);
            }
            pstmtInsert = p_con.prepareStatement(insertQuery);
            ++i;
            pstmtInsert.setString(i, p_requestVO.getRequestIDStr());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getRequestMSISDN());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getIncomingSmsStr());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getServiceType());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getSourceType());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getType());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getInstanceID());
            if (p_requestVO.isSuccessTxn()) {
                ++i;
                pstmtInsert.setString(i, PretupsI.TXN_STATUS_SUCCESS);
            } else {
                ++i;
                pstmtInsert.setString(i, p_requestVO.getMessageCode());
            }
            ++i;
            pstmtInsert.setString(i, p_requestVO.getServicePort());
            ++i;
            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(new Date()));
            ++i;
            pstmtInsert.setString(i, p_requestVO.getTransactionID());
            ++i;
            pstmtInsert.setLong(i, p_requestVO.getRequestStartTime());
            ++i;
            pstmtInsert.setLong(i, System.currentTimeMillis());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getRequestNetworkCode());
            ++i;
            pstmtInsert.setString(i, p_requestVO.getRequestGatewayCode());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            if (_log.isDebugEnabled()) {
                _log.error(methodName, " SQL Exception::" + sqe.getMessage());
            }
            _log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.error(methodName, " Exception " + e.getMessage());
            }
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Finally Exiting");
            }
        }
        return addCount;
    }

    /**
     * To Check whether external txn number is unique or not
     * 
     * @param p_con
     * @param p_extTxnnum
     * @param p_txnid
     * @return
     * @throws BTSLBaseException
     */
    public boolean isExtTxnExists(Connection p_con, String p_extTxnnum, String p_txnid) throws BTSLBaseException {
        final String methodName = "isExtTxnExists";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:p_extTxnnum=");
        	loggerValue.append(p_extTxnnum);
        	loggerValue.append("p_txnid=");
        	loggerValue.append(p_txnid);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExists = false;
        final StringBuffer sqlisExtTxnExists = new StringBuffer("SELECT transfer_id FROM channel_transfers ");
        sqlisExtTxnExists.append("WHERE type=? AND ext_txn_no=? AND status<>? ");
        if (p_txnid != null) {
            sqlisExtTxnExists.append(" AND transfer_id<>?");
        }
        try {
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("QUERY=");
            	loggerValue.append(sqlisExtTxnExists);
                _log.debug(methodName,  loggerValue );
            }
            final String query = sqlisExtTxnExists.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, PretupsI.CHANNEL_TYPE_O2C);
            pstmtSelect.setString(2, p_extTxnnum);
            pstmtSelect.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            if (p_txnid != null) {
                pstmtSelect.setString(4, p_txnid);
            }
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isExists = true;
            }
        }// end of try
        catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:");
        	loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isExtTxnExists]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "isRecordModified", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isExtTxnExists]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "isRecordModified", "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exititng:isExists=");
            	loggerValue.append(isExists);
                _log.debug(methodName,loggerValue);
            }
        }// end of finally
        return isExists;
    }// end isExtTxnExists

    /**
     * Add the transfer items
     * 
     * @param p_con
     * @param p_transferItemList
     * @param p_transferId
     * @param p_transferDate
     * @param p_transferType
     * @param p_transferSubType
     * @return int
     * @throws BTSLBaseException
     */
    private int addTransferItems(Connection p_con, List p_transferItemList, String p_transferId, Date p_transferDate, String p_transferType, String p_type,String p_transferSubType ,String p_StockUpdated) throws BTSLBaseException {
        final String methodName = "addTransferItems";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered TransferItemList Size: " );
        	loggerValue.append(p_transferItemList.size());
        	loggerValue.append(" TransferId : ");
        	loggerValue.append(p_transferId);
        	loggerValue.append("?p_transferDate=");
        	loggerValue.append(p_transferDate);
            _log.debug(methodName, loggerValue );
        }

        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
             Boolean othComChnl = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
             
            final StringBuffer strBuff = new StringBuffer(" INSERT INTO channel_transfers_items ( ");
            strBuff.append(" s_no,transfer_id,product_code,required_quantity,approved_quantity,user_unit_price, ");
            strBuff.append(" commission_profile_detail_id,commission_type, commission_rate, commission_value, ");
            strBuff.append(" tax1_type, tax1_rate, tax1_value, tax2_type,tax2_rate, tax2_value , tax3_type, ");
            strBuff.append(" tax3_rate, tax3_value, payable_amount, net_payable_amount,mrp,");
            strBuff
                .append(" sender_previous_stock, receiver_previous_stock,transfer_date,sender_post_stock, receiver_post_stock, sender_debit_quantity,receiver_credit_quantity");

            /** START:Birendra:27JAN2015 */
            strBuff.append(" ,user_wallet, commision_quantity, otf_type, otf_rate, otf_amount, otf_applicable ");
			if(othComChnl)
				strBuff.append(" ,oth_commission_type,oth_commission_rate,oth_commission_value ");
			strBuff.append(" ) ");
            /** STOP:Birendra:27JAN2015 */

            strBuff.append(" VALUES  ");
            strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");

            /** START:Birendra:27JAN2015 */
            strBuff.append(",? ");
			if(othComChnl)
				strBuff.append(" ,?,?,? ");
			strBuff.append(" ) ");
            /** STOP:Birendra:27JAN2015 */

            final String query = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query: " + query);
            }

            psmt = p_con.prepareStatement(query);
            ChannelTransferItemsVO transferItemsVO = null;
            for (int i = 0, k = p_transferItemList.size(); i < k; i++) {
                transferItemsVO = (ChannelTransferItemsVO) p_transferItemList.get(i);

                psmt.clearParameters();
                int m = 0;
                ++m;
                psmt.setInt(m, (i + 1));
                ++m;
                psmt.setString(m, p_transferId);
                ++m;
                psmt.setString(m, transferItemsVO.getProductCode());
                ++m;
                psmt.setLong(m, transferItemsVO.getRequiredQuantity());
                ++m;
                psmt.setLong(m, transferItemsVO.getApprovedQuantity());
                ++m;
                psmt.setLong(m, transferItemsVO.getUnitValue());
                ++m;
                psmt.setString(m, transferItemsVO.getCommProfileDetailID());
                ++m;
                psmt.setString(m, transferItemsVO.getCommType());
                ++m;
                psmt.setDouble(m, transferItemsVO.getCommRate());
                ++m;
                psmt.setLong(m, transferItemsVO.getCommValue());
                ++m;
                psmt.setString(m, transferItemsVO.getTax1Type());
                ++m;
                psmt.setDouble(m, transferItemsVO.getTax1Rate());
                ++m;
                psmt.setLong(m, transferItemsVO.getTax1Value());
                ++m;
                psmt.setString(m, transferItemsVO.getTax2Type());
                ++m;
                psmt.setDouble(m, transferItemsVO.getTax2Rate());
                ++m;
                psmt.setLong(m, transferItemsVO.getTax2Value());
                ++m;
                psmt.setString(m, transferItemsVO.getTax3Type());
                ++m;
                psmt.setDouble(m, transferItemsVO.getTax3Rate());
                ++m;
                psmt.setLong(m, transferItemsVO.getTax3Value());
                ++m;
                psmt.setLong(m, transferItemsVO.getPayableAmount());
                ++m;
                psmt.setLong(m, transferItemsVO.getNetPayableAmount());
                ++m;
                psmt.setLong(m, transferItemsVO.getProductTotalMRP());
                ++m;
                
                //added for def 625 GP c2c reversal
                if(PretupsI.CHANNEL_TYPE_C2C.equalsIgnoreCase(p_type) && PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE.equalsIgnoreCase(p_transferSubType))
                {
                	psmt.setLong(m, transferItemsVO.getReceiverPostStock());
                    ++m;
                    psmt.setLong(m, transferItemsVO.getSenderPostStock());
                    ++m;
                }
                else
                {
                	psmt.setLong(m, transferItemsVO.getAfterTransSenderPreviousStock());
                	++m;
                	psmt.setLong(m, transferItemsVO.getAfterTransReceiverPreviousStock());
                	++m;
                }
                psmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(p_transferDate));

                 /*if (PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equalsIgnoreCase(p_transferType) && !PretupsI.CHANNEL_TYPE_C2C.equalsIgnoreCase(p_type)) {
                    ++m;
                    psmt.setLong(m, 0);
                    ++m;
                    psmt.setLong(m, 0);
                } else { */
                    // psmt.setLong(++m,
                    // transferItemsVO.getAfterTransSenderPreviousStock()-transferItemsVO.getApprovedQuantity());
                    // psmt.setLong(++m,
                    // transferItemsVO.getAfterTransReceiverPreviousStock()+transferItemsVO.getApprovedQuantity());
                    // changed by vikram for positive and negative commissioning
                    // case.
                
                if(TypesI.NO.equalsIgnoreCase(p_StockUpdated) && PretupsI.CHANNEL_TYPE_O2C.equalsIgnoreCase(p_type) )
                	psmt.setLong(++m,transferItemsVO.getAfterTransSenderPreviousStock());
                else{
                	if(PretupsI.CHANNEL_TYPE_C2C.equalsIgnoreCase(p_type) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(p_transferSubType) && transferItemsVO.getAfterTransSenderPreviousStock() == 0)
                	{
                		++m;
                        psmt.setLong(m, transferItemsVO.getAfterTransSenderPreviousStock());
                	}
                	else
                	{
                		++m;
                		psmt.setLong(m, transferItemsVO.getAfterTransSenderPreviousStock() - transferItemsVO.getSenderDebitQty());
                	}
                }
                ++m;
                psmt.setLong(m, transferItemsVO.getAfterTransReceiverPreviousStock() + transferItemsVO.getReceiverCreditQty());
                
               /* } */
                // changed by vikram for positive and negative commissioning
                // case.
                // psmt.setLong(++m, transferItemsVO.getApprovedQuantity());
                // psmt.setLong(++m, transferItemsVO.getApprovedQuantity());
                ++m;
                psmt.setLong(m, transferItemsVO.getSenderDebitQty());
                ++m;
                psmt.setLong(m, transferItemsVO.getReceiverCreditQty());

                /** START:Birendra:27JAN2015 */
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                    ++m;
                    psmt.setString(m, transferItemsVO.getUserWallet());
                } else {
                    ++m;
                    psmt.setString(m, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
                }
                /** STOP:Birendra:27JAN2015 */

                ++m;
                psmt.setLong(m, transferItemsVO.getCommQuantity());
                
                ++m;
                psmt.setString(m, transferItemsVO.getOtfTypePctOrAMt());
                ++m;
                if(transferItemsVO.getOtfTypePctOrAMt()!=null && transferItemsVO.getOtfTypePctOrAMt().equals(PretupsI.AMOUNT_TYPE_PERCENTAGE))
                {  
                	psmt.setString(m, Double.toString(transferItemsVO.getOtfRate()));
                }
                else
                {
                psmt.setDouble(m, transferItemsVO.getOtfRate());
                }
                ++m;
                psmt.setLong(m, transferItemsVO.getOtfAmount());
                ++m;
                psmt.setString(m, transferItemsVO.isOtfApplicable());
				if(othComChnl){
					++m;
					psmt.setString(m, transferItemsVO.getOthCommType());
					++m;
					psmt.setDouble(m, transferItemsVO.getOthCommRate());
					++m;
					psmt.setLong(m, transferItemsVO.getOthCommValue());
				}
                
                updateCount = psmt.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres

                if (updateCount <= 0) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[addTransferItems]", "", "", "",
                        "BTSLBaseException: update count <=0");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        }// end of try
        catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addTransferItems]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addTransferItems]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue );
            }
        }// end of finally

        return updateCount;
    }

    /**
     * Add the bonus items entries
     * 
     * @param p_con
     * @param p_bonusItemList
     * @return int
     * @throws BTSLBaseException
     */
    /*
     * private int addBonusItems(Connection p_con, ArrayList p_bonusItemList)
     * throws BTSLBaseException
     * {
     * 
     * if (_log.isDebugEnabled())
     * _log.debug("addBonusItems", "Entered BonusItemList Size: " +
     * p_bonusItemList.size());
     * 
     * PreparedStatement psmt = null;
     * int updateCount = 0;
     * try
     * {
     * StringBuffer strBuff = new
     * StringBuffer(" INSERT INTO c2s_bonuses (transfer_id, ");
     * strBuff.append(
     * " account_id, account_code, account_name, account_type, account_rate, ");
     * strBuff.append(
     * " previous_balance, previous_validity, previous_grace, balance, ");
     * strBuff.append(
     * " validity, grace, post_balance, post_validity, post_grace, created_on) "
     * );
     * strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
     * String query = strBuff.toString();
     * 
     * if (_log.isDebugEnabled())
     * _log.debug("addBonusItems", "insert query:" + query);
     * 
     * psmt = p_con.prepareStatement(query);
     * BonusTransferVO bonusTransferVO= null;
     * for (int i = 0, k = p_bonusItemList.size(); i < k; i++)
     * {
     * bonusTransferVO = (BonusTransferVO) p_bonusItemList.get(i);
     * 
     * psmt.clearParameters();
     * int m = 0;
     * psmt.setString(++m, bonusTransferVO.getTransferId());
     * psmt.setString(++m, bonusTransferVO.getAccountId());
     * psmt.setString(++m, bonusTransferVO.getAccountCode());
     * psmt.setString(++m, bonusTransferVO.getAccountName());
     * psmt.setString(++m, bonusTransferVO.getAccountType());
     * psmt.setDouble(++m, bonusTransferVO.getAccountRate());
     * psmt.setDouble(++m, bonusTransferVO.getPreviousBalance());
     * psmt.setDate(++m,
     * BTSLUtil.getSQLDateFromUtilDate(bonusTransferVO.getPreviousValidity()));
     * psmt.setDate(++m,
     * BTSLUtil.getSQLDateFromUtilDate(bonusTransferVO.getPreviousGrace()));
     * psmt.setDouble(++m, bonusTransferVO.getBalance());
     * psmt.setLong(++m, bonusTransferVO.getValidity());
     * psmt.setLong(++m, bonusTransferVO.getGrace());
     * psmt.setDouble(++m, bonusTransferVO.getPostBalance());
     * psmt.setDate(++m,
     * BTSLUtil.getSQLDateFromUtilDate(bonusTransferVO.getPostValidity()));
     * psmt.setDate(++m,
     * BTSLUtil.getSQLDateFromUtilDate(bonusTransferVO.getPostGrace()));
     * psmt.setTimestamp(++m,
     * BTSLUtil.getTimestampFromUtilDate(bonusTransferVO.getCreatedOn()));
     * updateCount = psmt.executeUpdate();
     * 
     * if (updateCount <= 0)
     * {
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .RAISED,EventLevelI.INFO,"ChannelUserDAO[addBonusItems]","","","",
     * "BTSLBaseException: update count <=0");
     * throw new BTSLBaseException(this, "addBonusItems",
     * "error.general.sql.processing");
     * }
     * }
     * 
     * }// end of try
     * catch (BTSLBaseException bbe)
     * {
     * throw bbe;
     * } catch (SQLException sqle)
     * {
     * _log.error("addBonusItems", "SQLException " + sqle.getMessage());
     * sqle.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "ChannelTransferDAO[addBonusItems]", "", "", "", "SQL Exception:"+
     * sqle.getMessage());
     * throw new BTSLBaseException(this, "addBonusItems",
     * "error.general.sql.processing");
     * }// end of catch
     * catch (Exception e)
     * {
     * _log.error("addBonusItems", "Exception " + e.getMessage());
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
     * EventStatusI.RAISED, EventLevelI.FATAL,
     * "ChannelTransferDAO[addBonusItems]", "", "", "", "Exception:"+
     * e.getMessage());
     * throw new BTSLBaseException(this, "addBonusItems",
     * "error.general.processing");
     * }// end of catch
     * finally
     * {
     * try{if (psmt != null)psmt.close();} catch (Exception e){}
     * if (_log.isDebugEnabled())
     * _log.debug("addBonusItems", "Exiting Success :" + updateCount);
     * }// end of finally
     * 
     * return updateCount;
     * }
     */
    
    
    
    /**
     * 
     * @param p_con
     * @param p_roleCode
     * @return
     * @throws BTSLBaseException
     */
    
    public String getEmailIdOfApprovers(Connection p_con, String p_roleCode, String parentUserId) throws BTSLBaseException {
        final String methodName = "getEmailIdOfApprover";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered :: p_roleCode = ");
        	loggerValue.append(p_roleCode);
        	loggerValue.append("p_channelUserID = ");
            _log.debug(methodName, loggerValue );
        }
        String emailIdsBld = "";
        StringBuilder selQuery = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int index = 0;
        try {
          

        	selQuery = channelTransferQry.getEmailIdOfRoleApproversQry();
            // Ended here
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query :: ");
            	loggerValue.append(selQuery.toString());
                _log.debug(methodName,  loggerValue);
            }

            pstmt = p_con.prepareStatement(selQuery.toString());
            
            
            ++index;
            pstmt.setString(index, parentUserId);
            ++index;
            pstmt.setString(index, p_roleCode);
            ++index;
            pstmt.setString(index, parentUserId);
            ++index;
            pstmt.setString(index, PretupsI.USER_TYPE_STAFF);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                emailIdsBld = emailIdsBld + rs.getString("email") + ",";
            }
            if (!BTSLUtil.isNullString(emailIdsBld)) {
                emailIdsBld = emailIdsBld.trim();
                emailIdsBld = emailIdsBld.substring(0, emailIdsBld.length() - 1);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting :: Email ids :: ");
            	loggerValue.append(emailIdsBld);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting :: Email ids :: ");
        	loggerValue.append(emailIdsBld);
            _log.debug(methodName, loggerValue);
        }
        return emailIdsBld;
    }

    
    /**
     * load email id of approvar based on role code and geography (discussed
     * with Ved sir)
     * 
     * @param p_con
     * @param p_roleCode
     * @param p_channelUserID
     * @return String
     * @throws BTSLBaseException
     * @author Nilesh kumar
     */

    public String getEmailIdOfApprover(Connection p_con, String p_roleCode, String p_channelUserID) throws BTSLBaseException {
        final String methodName = "getEmailIdOfApprover";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered :: p_roleCode = ");
        	loggerValue.append(p_roleCode);
        	loggerValue.append("p_channelUserID = ");
        	loggerValue.append(p_channelUserID);
            _log.debug(methodName, loggerValue );
        }
        String emailIdsBld = "";
        StringBuilder selQuery = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int index = 0;
        try {
          

        	selQuery = channelTransferQry.getEmailIdOfApproverQry();
            // Ended here
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query :: ");
            	loggerValue.append(selQuery.toString());
                _log.debug(methodName,  loggerValue);
            }

            pstmt = p_con.prepareStatement(selQuery.toString());
            ++index;
            pstmt.setString(index, p_roleCode);
            // pstmt.setString(++index, p_channelUserID);
            // pstmt.setString(++index, p_channelUserID);
            ++index;
            pstmt.setString(index, p_roleCode);
            // pstmt.setString(++index, p_channelUserID);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                emailIdsBld = emailIdsBld + rs.getString("email") + ",";
            }
            if (!BTSLUtil.isNullString(emailIdsBld)) {
                emailIdsBld = emailIdsBld.trim();
                emailIdsBld = emailIdsBld.substring(0, emailIdsBld.length() - 1);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting :: Email ids :: ");
            	loggerValue.append(emailIdsBld);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting :: Email ids :: ");
        	loggerValue.append(emailIdsBld);
            _log.debug(methodName, loggerValue);
        }
        return emailIdsBld;
    }
    

    public String getMsisdnOfApprovers(Connection p_con, String p_roleCode, String parentUserId) throws BTSLBaseException {
        final String methodName = "getMsisdnOfApprover";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered :: p_roleCode = ");
        	loggerValue.append(p_roleCode);
            _log.debug(methodName, loggerValue );
        }
        String msisdns = "";
        StringBuilder selQuery = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int index = 0;
        try {
          

        	selQuery = channelTransferQry.getEmailIdOfRoleApproversQry();
            // Ended here
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query :: ");
            	loggerValue.append(selQuery.toString());
                _log.debug(methodName,  loggerValue);
            }

            pstmt = p_con.prepareStatement(selQuery.toString());
            ++index;
            pstmt.setString(index, parentUserId);
            ++index;
            pstmt.setString(index, p_roleCode);     
            ++index;
            pstmt.setString(index, parentUserId);
            ++index;
            pstmt.setString(index, "STAFF");
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	msisdns = msisdns + rs.getString("msisdn") + ",";
            }
            if (!BTSLUtil.isNullString(msisdns)) {
            	msisdns = msisdns.trim();
            	msisdns = msisdns.substring(0, msisdns.length() - 1);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting :: Msisdns :: ");
            	loggerValue.append(msisdns);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting :: Msisdns :: ");
        	loggerValue.append(msisdns);
            _log.debug(methodName, loggerValue);
        }
        return msisdns;
    }

    
    public String getMsisdnOfApprover(Connection p_con, String p_roleCode) throws BTSLBaseException {
        final String methodName = "getMsisdnOfApprover";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered :: p_roleCode = ");
        	loggerValue.append(p_roleCode);
            _log.debug(methodName, loggerValue );
        }
        String msisdns = "";
        StringBuilder selQuery = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int index = 0;
        try {
          

        	selQuery = channelTransferQry.getEmailIdOfApproverQry();
            // Ended here
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query :: ");
            	loggerValue.append(selQuery.toString());
                _log.debug(methodName,  loggerValue);
            }

            pstmt = p_con.prepareStatement(selQuery.toString());
            ++index;
            pstmt.setString(index, p_roleCode);
            // pstmt.setString(++index, p_channelUserID);
            // pstmt.setString(++index, p_channelUserID);
            ++index;
            pstmt.setString(index, p_roleCode);
            // pstmt.setString(++index, p_channelUserID);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	msisdns = msisdns + rs.getString("msisdn") + ",";
            }
            if (!BTSLUtil.isNullString(msisdns)) {
            	msisdns = msisdns.trim();
            	msisdns = msisdns.substring(0, msisdns.length() - 1);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException " );
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting :: Msisdns :: ");
            	loggerValue.append(msisdns);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting :: Msisdns :: ");
        	loggerValue.append(msisdns);
            _log.debug(methodName, loggerValue);
        }
        return msisdns;
    }

    /**
     * Start the operator to channel transfer for o2c
     * 
     * @param p_con
     * @param p_channelTransferVO
     * @return
     * @throws BTSLBaseException
     */
    public int addChannelTransferForO2C(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        final String methodName = "addChannelTransferForO2C";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered ChannelTransferVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue);
        }
        
        int updateCount = 0;
        try {
        	Boolean multipleWalletApply = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
            final StringBuffer strBuff = new StringBuffer(" INSERT INTO  channel_transfers ( transfer_id, network_code, network_code_for, grph_domain_code, ");
            strBuff.append(" domain_code, sender_category_code, sender_grade_code, receiver_grade_code, from_user_id, ");
            strBuff.append(" to_user_id, transfer_date, reference_no, ext_txn_no, ext_txn_date, commission_profile_set_id, ");
            strBuff.append(" commission_profile_ver, requested_quantity, channel_user_remarks,  ");
            strBuff.append(" created_on, created_by, modified_by, modified_on, status, transfer_type, transfer_initiated_by, transfer_mrp, ");
            strBuff.append(" payable_amount, net_payable_amount, pmt_inst_type, pmt_inst_no, pmt_inst_date, ");
            strBuff.append(" pmt_inst_amount, sender_txn_profile, receiver_txn_profile, total_tax1, total_tax2, ");
            strBuff.append(" total_tax3, source, receiver_category_code , product_type , transfer_category ,");
            strBuff.append(" first_approver_limit, second_approver_limit,pmt_inst_source,  ");
            strBuff.append(" type,transfer_sub_type,close_date,control_transfer,request_gateway_code, request_gateway_type, ");
            strBuff.append(" msisdn,to_msisdn,to_grph_domain_code,to_domain_code,  ");
            strBuff
                .append(" first_approved_by, first_approved_on, second_approved_by, second_approved_on, third_approved_by, third_approved_on,sms_default_lang,sms_second_lang,active_user_id,batch_no,dual_comm_type");
            if (multipleWalletApply) {
                strBuff.append(",TXN_WALLET");
                strBuff.append(") VALUES ");
                strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            } else {
                strBuff.append(") VALUES ");
                strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            }

            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

           try( PreparedStatement psmt = p_con.prepareStatement(query);)
           {
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getNetworkCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getNetworkCodeFor());
            ++i;
            psmt.setString(i, p_channelTransferVO.getGraphicalDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCategoryCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSenderGradeCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverGradeCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFromUserID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getToUserID());
            ++i;
            psmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_channelTransferVO.getTransferDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getCommProfileSetId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getCommProfileVersion());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getRequestedQuantity());
            // for multilanguage support
            // psmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getChannelRemarks());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCreatedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getCreatedBy());
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferInitatedBy());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSenderTxnProfile());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverTxnProfile());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax1());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax2());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax3());
            ++i;
            psmt.setString(i, p_channelTransferVO.getSource());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverCategoryCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getProductType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferCategory());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getFirstApproverLimit());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getSecondApprovalLimit());
            // for multilanguage support
            // psmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getPaymentInstSource());
            ++i;
            psmt.setString(i, p_channelTransferVO.getType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferSubType());
            if (PretupsI.CHANNEL_TYPE_O2C.equals(p_channelTransferVO.getType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
                .equals(p_channelTransferVO.getTransferSubType()) && PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, null);
            } else {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getCreatedOn()));
            }
            ++i;
            psmt.setString(i, p_channelTransferVO.getControlTransfer());
            ++i;
            psmt.setString(i, p_channelTransferVO.getRequestGatewayCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getRequestGatewayType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFromUserCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getToUserCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverGgraphicalDomainCode());
            ++i;
            psmt.setString(i, p_channelTransferVO.getReceiverDomainCode());

            //
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getThirdApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn()));

            // psmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // psmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());
            ++i;
            psmt.setString(i, p_channelTransferVO.getActiveUserId());
            ++i;
            psmt.setString(i, p_channelTransferVO.getBatchNum());
            ++i;
            psmt.setString(i, p_channelTransferVO.getDualCommissionType());
            if (multipleWalletApply) {
                ++i;
                psmt.setString(i, p_channelTransferVO.getWalletType());
            }

            updateCount = psmt.executeUpdate();
            updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");

            } else {

                // add the items in item list
                updateCount = addTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), p_channelTransferVO.getTransferID(), p_channelTransferVO
                    .getCreatedOn(), p_channelTransferVO.getTransferType(), p_channelTransferVO.getType(),p_channelTransferVO.getTransferSubType(), p_channelTransferVO.getStockUpdated());

            }
           }
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append( sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addChannelTransferForO2C]", "", "",
                "",  loggerValue.toString());
            if (sqle.getErrorCode() == 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.O2C_EXTGW_DUPLICATE_TRANSCATION);
            } else {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addChannelTransferForO2C]", "", "",
                "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
           
            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue );
            }
        }// end of finally

        return updateCount;

    }

    public int updateReverseTransferApp1(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        
    	final String methodName = "updateReverseTransferApp1";
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	
        		loggerValue.setLength(0);
                loggerValue.append("Entered p_channelTransferVO : " );
                 loggerValue.append(p_channelTransferVO);
               _log.debug(methodName,loggerValue );
        }  
        PreparedStatement psmt = null;
        int updateCount = 0;
        final boolean isOrderClosed = (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RVERSE_TXN_APPRV_LVL))).intValue() == 1 || ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.RVERSE_TXN_APPRV_LVL))).intValue() == 0);
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE  channel_transfers SET  ");
            strBuff.append(" from_user_id = ?, to_user_id = ?,modified_by = ?, modified_on = ?, ");
            strBuff.append(" first_approver_remarks = ?, first_approved_by = ?, first_approved_on = ?, status = ? ");
            if (isOrderClosed) {
                strBuff.append(",close_date=? ");
            }
            strBuff.append(" WHERE transfer_id = ?  AND status =? ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getToUserID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFromUserID());
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            // for multilanguage support
            // psmt.setFormOfUse(++i, PreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovalRemark());
            ++i;
            psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            if (isOrderClosed) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            }
            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);

            updateCount = psmt.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }

            //defect:incorrect data regarding sender and reciever's balance: added an arguement as transfer_id
            updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderClosed, true,p_channelTransferVO.getTransferID());

        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
            loggerValue.append("SQLException ");
             loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQLException ");
             loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateReverseTransferApp1]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
            loggerValue.append("Exception ");
             loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception ");
             loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateReverseTransferApp1]", "",
                "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
                loggerValue.append("Exiting Success :");
                 loggerValue.append(updateCount);
                _log.debug(methodName,  loggerValue );
            }
        }// end of finally
        return updateCount;
    }

    
    /**
     * Method used to update the user i.e. owner of vouchers, process:- C2C Voucher Approval
     * @param p_con
     * @param vomsBatchVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateVomsVoucherUserId(Connection p_con, VomsBatchVO vomsBatchVO) throws BTSLBaseException {
        final String methodName = "updateVomsVoucherUserId";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        VomsProductDAO vomsProductDAO = null;
      
        String tablename;
        int update_count = 0;
        try {
        	Boolean multipleVoucherTable = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE);
            if (multipleVoucherTable) {
                final boolean matchFound = BTSLUtil.validateTableName(vomsBatchVO.getVoucherType());
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vomsBatchVO.getVoucherType() + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }

            // VomsBatchVO vomsBatchVO= new VomsBatchVO();
            final StringBuffer strBuff = new StringBuffer("UPDATE " + tablename + " SET USER_ID=?,");
            strBuff.append(" MODIFIED_BY=?,MODIFIED_ON=? , C2C_TRANSFER_DATE=?, C2C_TRANSFER_ID =? ");
            strBuff.append(" WHERE SERIAL_NO  BETWEEN  ? AND ?");
            final String sqlSelect = strBuff.toString();
            int i =1;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)
            {
            pstmt.setString(i++, vomsBatchVO.getToUserID());
            pstmt.setString(i++, vomsBatchVO.getCreatedBy());
            
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getModifiedOn()));
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getModifiedOn()));
            
            pstmt.setString(i++, vomsBatchVO.getTransferId());
            
            
            
            
            pstmt.setString(i++, vomsBatchVO.getFromSerialNo());
            pstmt.setString(i++, vomsBatchVO.getToSerialNo());
            update_count = pstmt.executeUpdate();
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateVomsVoucherUserId]", "", "",
                "",loggerValue.toString());
            throw new BTSLBaseException(this, "updateVomsVoucherUserId", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateVomsVoucherUserId]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "updateVomsVoucherUserId", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insert count  =");
            	loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue );
            }
        }
        return update_count;

    }

    
    /*
     * Addition ends
     */

    /**
     * Method: updateVomsVoucherStstus
     * This method change status of vouchers in voms_vouchers table.
     * 
     * @author gaurav.pandey
     * @param p_con
     *            java.sql.Connection
     * @param p_productId
     *            String
     * @return totalQuantity int
     * @throws BTSLBaseException
     */

    public int updateVomsVoucherStstus(Connection p_con, VomsBatchVO vomsBatchVO) throws BTSLBaseException {
        final String methodName = "updateVomsVoucherStstus";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        VomsProductDAO vomsProductDAO = null;
      
        String tablename;
        int update_count = 0;
        try {
        	Boolean multipleVoucherTable = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_VOUCHER_TABLE);
            if (multipleVoucherTable) {
                final boolean matchFound = BTSLUtil.validateTableName(vomsBatchVO.getVoucherType());
                if (!matchFound) {
                    throw new BTSLBaseException(this, methodName, "error.not.a.valid.voucher.type");
                }
                tablename = "voms_" + vomsBatchVO.getVoucherType() + "_vouchers";
            } else {
                tablename = "voms_vouchers";
            }

            // VomsBatchVO vomsBatchVO= new VomsBatchVO();
            final StringBuffer strBuff = new StringBuffer("UPDATE " + tablename + " SET ENABLE_BATCH_NO=?,CURRENT_STATUS=?, status=?,");
            strBuff.append(" MODIFIED_BY=?,MODIFIED_ON=?, PREVIOUS_STATUS=? ,");
            strBuff.append(" USER_ID=? , LAST_TRANSACTION_ID = ? WHERE PRODUCT_ID=? AND SERIAL_NO  BETWEEN  ? AND ?");
            final String sqlSelect = strBuff.toString();
            int i =1;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)
            {
            pstmt.setString(i++, vomsBatchVO.getBatchNo());
            pstmt.setString(i++, vomsBatchVO.getBatchType());
            pstmt.setString(i++, vomsBatchVO.getBatchType());
            pstmt.setString(i++, vomsBatchVO.getCreatedBy());
            pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsBatchVO.getModifiedOn()));
            vomsProductDAO = new VomsProductDAO();
            String type=vomsProductDAO.getTypeFromVoucherType(p_con,vomsBatchVO.getVoucherType());
            if(VOMSI.VOUCHER_TYPE_DIGITAL.equals(type) || VOMSI.VOUCHER_TYPE_TEST_DIGITAL.equals(type)){
            	  pstmt.setString(i++, VOMSI.VOUCHER_NEW);
            }
            else{
            	pstmt.setString(i++, VOMSI.VOMS_WARE_HOUSE_STATUS);
            }
            pstmt.setString(i++, vomsBatchVO.getToUserID());
            pstmt.setString(i++, vomsBatchVO.getExtTxnNo());            
            pstmt.setString(i++, vomsBatchVO.getProductID());
            pstmt.setString(i++, vomsBatchVO.getFromSerialNo());
            pstmt.setString(i++, vomsBatchVO.getToSerialNo());
            update_count = pstmt.executeUpdate();
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateVomsVoucherStstus]", "", "",
                "",loggerValue.toString());
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateVomsVoucherStstus]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insert count  =");
            	loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue );
            }
        }
        return update_count;

    }


    /**
     * 
     * @param p_con
     * @param vomsBatchVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateVomsBatchC2C(Connection p_con, VomsBatchVO vomsBatchVO) throws BTSLBaseException {
        final String methodName = "updateVomsBatchC2C";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }

        

        int update_count = 0;
        final ResultSet rs = null;

        // VomsBatchVO vomsBatchVO= new VomsBatchVO();
        final StringBuffer strBuff = new StringBuffer("UPDATE VOMS_BATCHES SET STATUS=?,MESSAGE=?, ");
        strBuff.append(" TOTAL_NO_OF_FAILURE=?,TOTAL_NO_OF_SUCCESS=? ");
        strBuff.append(" WHERE  BATCH_NO=?");
        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            

            pstmt.setString(1, vomsBatchVO.getStatus());
            pstmt.setString(2, vomsBatchVO.getMessage());
            pstmt.setLong(3, vomsBatchVO.getFailCount());
            pstmt.setLong(4, vomsBatchVO.getSuccessCount());
           
            pstmt.setString(5, vomsBatchVO.getBatchNo());
            update_count = pstmt.executeUpdate();

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateFinalVomsBatch]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "updateVomsBatchC2C", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateFinalVomsBatch]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "updateVomsBatchC2C", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insert count  =");
            	loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue);
            }
        }
        return update_count;

    }

    /**
     * Method: updateFinalVomsBatch
     * This method change status of vouchers in voms_vouchers table.
     * 
     * @author gaurav.pandey
     * @param p_con
     *            java.sql.Connection
     * @param p_productId
     *            String
     * @return totalQuantity int
     * @throws BTSLBaseException
     */

    public int updateFinalVomsBatch(Connection p_con, VomsBatchVO vomsBatchVO) throws BTSLBaseException {
        final String methodName = "updateFinalVomsBatch";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }

        

        int update_count = 0;
        final ResultSet rs = null;

        // VomsBatchVO vomsBatchVO= new VomsBatchVO();
        final StringBuffer strBuff = new StringBuffer("UPDATE VOMS_BATCHES SET STATUS=?,MESSAGE=?, ");
        strBuff.append(" TOTAL_NO_OF_FAILURE=?,TOTAL_NO_OF_SUCCESS=? ");
        strBuff.append(" WHERE PRODUCT_ID=? AND BATCH_NO=?");
        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            

            pstmt.setString(1, vomsBatchVO.getStatus());
            pstmt.setString(2, vomsBatchVO.getMessage());
            pstmt.setLong(3, vomsBatchVO.getFailCount());
            pstmt.setLong(4, vomsBatchVO.getSuccessCount());
            pstmt.setString(5, vomsBatchVO.getProductID());
            pstmt.setString(6, vomsBatchVO.getBatchNo());
            update_count = pstmt.executeUpdate();

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateFinalVomsBatch]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateFinalVomsBatch]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insert count  =");
            	loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue);
            }
        }
        return update_count;

    }

    public String getStatusOfDomain(Connection p_con, String domainCode) throws BTSLBaseException {
        final String methodName = "getStatusOfDomain";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }

         

        final int update_count = 0;
       
        String Status = null;

        // VomsBatchVO vomsBatchVO= new VomsBatchVO();
        final StringBuffer strBuff = new StringBuffer("SELECT STATUS FROM DOMAINS WHERE DOMAIN_CODE=? ");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug("updateUnusedBatches",  loggerValue);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, domainCode);

            try( ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                Status = rs.getString("STATUS");
            }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateUnusedBatches]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateUnusedBatches]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "loadlistOfUnusedBatches", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: status =");
            	loggerValue.append(Status);
                _log.debug(methodName, loggerValue );
            }
        }
        return Status;

    }
	
	/**This method Load the last N number of c2c, c2s and o2c transaction details.
     * @author harsh.dixit 
     * @param p_con Connection
     * @param p_user_id String
     * @param p_noLastTxn int	//no of last transactions to be fetched.
	 * @param p_txnType			// C2C ,O2C & C2S transaction details.
     * @param p_serviceType		// applicable in case of txn type as C2S to specify individual C2S Service or ALL
     * @param p_noDays			//fetch only data for this period  if null/0 then no check on the date.
	 * @param p_c2cInOut		// applicable in case of txn type as C2C to mark Channel User type as SENDER/RECEIVER
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadLastXTransfersServiceWise(Connection p_con, String p_user_id,int p_noLastTxn, String p_serviceType, int p_noDays,String p_txnType,String p_c2cInOut) throws BTSLBaseException
    {
    	final String methodName="loadLastXTransfersServiceWise";
    	StringBuilder loggerValue= new StringBuilder(); 
       if (_log.isDebugEnabled())
    	    loggerValue.setLength(0);
            loggerValue.append("Entered  p_user_id: ");
            loggerValue.append(p_user_id);
            loggerValue.append(", p_noLastTxn: ");
            loggerValue.append(p_noLastTxn);
            loggerValue.append(", p_serviceType: ");
            loggerValue.append(p_serviceType);
            loggerValue.append(" p_noDays: ");
            loggerValue.append(p_noDays);
            loggerValue.append(" p_txnType: ");
            loggerValue.append(" p_c2cInOut: ");
            loggerValue.append(p_c2cInOut);
            _log.debug(methodName, loggerValue);
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs=null;
        ResultSet rs1=null;
        ResultSet rs2=null;
        C2STransferVO transferVO=null;
        ArrayList transfersList=null;
        StringBuffer strBuff =null;
        StringBuffer inputParams=null;
		StringBuilder services=null;
        int i,len;
        try
        {
          	transfersList=new ArrayList();
			inputParams=new StringBuffer();
			services=new StringBuilder();
          	String []txns = p_txnType.split(",");
			len=txns.length;
          	Date differenceDate=BTSLUtil.getDifferenceDate(new Date(), -p_noDays);	//for getting diffence date.
          	for(int j=0; j<len; j++)
        	{
          		String []serviceArray = p_serviceType.split(",");
          		for(int k=0;k<serviceArray.length;k++)
          		{
		        	inputParams.append(",?");
		        	services.append(",'"+serviceArray[k]+"'");
		        	services.trimToSize();
          		}
          		final String tempInputParams = inputParams.substring(1);
          		final String tempServices = services.substring(1);
          		inputParams.setLength(0);
          		inputParams.append(tempInputParams);
          		services.setLength(0);
          		services.append(tempServices);			
			    if(PretupsI.SERVICE_TYPE_C2S_LAST_X_TRANSFER.equals(txns[j]))
			    {	
			    	pstmt = channelTransferQry.loadLastXC2STransfersServiceWiseQry(p_con,p_serviceType,p_noDays,services,differenceDate,p_user_id,p_noLastTxn);
			        rs = pstmt.executeQuery();
			        while(rs.next())
			        {
			            transferVO = new C2STransferVO();
			            transferVO.setTransferID(rs.getString("transfer_id"));
			            transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
			            transferVO.setTransferStatus(rs.getString("transfer_status"));
			            transferVO.setType(PretupsI.C2S_MODULE);
			            transferVO.setReceiverMsisdn(rs.getString("receiver_Msisdn"));
			            transferVO.setTransferValue(rs.getLong("net_payable_amount"));
			            transferVO.setCreatedOn(rs.getTimestamp("created_on"));
			            transferVO.setServiceType(rs.getString("service"));
			            transferVO.setServiceName(rs.getString("name"));
			            transferVO.setStatus(rs.getString("statusname"));
			            transferVO.setSenderPostBalance(rs.getLong("sender_post_balance"));
			            if(!BTSLUtil.isNullString(rs.getString("error_code")))
					transferVO.setErrorCode(rs.getString("error_code"));
				    else
					transferVO.setErrorCode("");	
			            transfersList.add(transferVO);
			        }
			    }     
			    else if(PretupsI.SERVICE_TYPE_C2C_LAST_X_TRANSFER.equals(txns[j]))
			    {
			    	String aa[]=txns[j].split(":");
			    	pstmt1 = channelTransferQry.loadLastXC2CTransfersServiceWiseQry(p_con,p_serviceType,p_noDays,services,differenceDate,p_user_id,p_noLastTxn, aa,p_c2cInOut);
			        rs1 = pstmt1.executeQuery();
			        while(rs1.next())
			        {
			            transferVO = new C2STransferVO();
			            transferVO.setTransferID(rs1.getString("transfer_id"));
			            if(rs1.getTimestamp("CLOSE_DATE")!=null)
			            	transferVO.setTransferDateTime(rs1.getTimestamp("CLOSE_DATE"));
			            else
			            	transferVO.setTransferDateTime(rs1.getTimestamp("MODIFIED_ON"));
			            transferVO.setTransferStatus(rs1.getString("status"));
			            transferVO.setType(PretupsI.C2C_MODULE);
			            transferVO.setReceiverMsisdn(rs1.getString("to_msisdn"));
			            transferVO.setSenderMsisdn(rs1.getString("msisdn"));
			            transferVO.setTransferValue(rs1.getLong("net_payable_amount"));
			            transferVO.setCreatedOn(rs1.getTimestamp("created_on"));
			            transferVO.setServiceType(rs1.getString("service"));								//set service type here.
			            transferVO.setServiceName(rs1.getString("name"));							//from look-up set service name
			            transferVO.setStatus(rs1.getString("statusname"));
			            long approvedQuantity=rs1.getLong("approved_quantity");
			            if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(rs1.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs1.getString("transfer_sub_type")))
			            	transferVO.setSenderPostBalance(rs1.getLong("sender_previous_stock") - approvedQuantity);
			            else
			            	transferVO.setSenderPostBalance(rs1.getLong("receiver_previous_stock") + approvedQuantity);
			            transferVO.setErrorCode("");
			            transfersList.add(transferVO);
			        }
			    }
			    else if(txns[j].contains(PretupsI.SERVICE_TYPE_O2C_LAST_X_TRANSFER))	
			    {
			    	String aa[]=txns[j].split(":");
			    	pstmt2 = channelTransferQry.loadLastXO2CTransfersServiceWiseQry(p_con,p_serviceType,p_noDays,services,differenceDate,p_user_id,p_noLastTxn, aa);
			        rs2 = pstmt2.executeQuery();
			        while(rs2.next())
			        {
			            transferVO = new C2STransferVO();
			            transferVO.setTransferID(rs2.getString("transfer_id"));
			            if(rs2.getTimestamp("CLOSE_DATE")!=null)
			            	transferVO.setTransferDateTime(rs2.getTimestamp("CLOSE_DATE"));
			            else
			            	transferVO.setTransferDateTime(rs2.getTimestamp("MODIFIED_ON"));
			            transferVO.setTransferStatus(rs2.getString("status"));
			            transferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
			            transferVO.setSenderMsisdn(rs2.getString("msisdn"));
			            transferVO.setReceiverMsisdn(rs2.getString("to_msisdn"));
			            transferVO.setTransferValue(rs2.getLong("approved_quantity"));
			            transferVO.setCreatedOn(rs2.getTimestamp("created_on"));
			            transferVO.setServiceType(rs2.getString("service"));							//from look-up set service name
			            transferVO.setServiceName(rs2.getString("name"));								//set sub service type here.
			            transferVO.setStatus(rs2.getString("statusname"));
			            long approvedQuantity=rs2.getLong("approved_quantity");
			            if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs2.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(rs2.getString("transfer_sub_type")))
			            	transferVO.setSenderPostBalance(rs2.getLong("sender_previous_stock")-approvedQuantity);
			            else
			            	transferVO.setSenderPostBalance(rs2.getLong("receiver_previous_stock") + approvedQuantity);
			            transferVO.setErrorCode("");
			            transfersList.add(transferVO);
			        }
			    }
			    	
        	}
       }
       catch (SQLException sqe)
       {      loggerValue.setLength(0);
             loggerValue.append("SQLException : ");
             loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO["+methodName+"]", "", "", "", 
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        }
        catch (Exception ex)
        {    loggerValue.setLength(0);
             loggerValue.append("Exception : ");
             loggerValue.append(ex);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO["+methodName+"]", "", "", "", 
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }
        finally
        {
            try{
            	if (rs != null)
            	{rs.close();
            	}
            	}catch (Exception e){
            	loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
    			_log.errorTrace(methodName, e);
            }
            try{
            	if (rs1 != null)
            	{rs1.close();
            	}
            	}catch (Exception e){
            	loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
    			_log.errorTrace(methodName, e);
            }
            try{
            	if (rs2 != null)
            	{rs2.close();
            	}
            	}catch (Exception e){
            	loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
    			_log.errorTrace(methodName, e);
            }
			try{if (pstmt != null){pstmt.close();}}catch (Exception e){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
			}
			try{if (pstmt1 != null){pstmt1.close();}}catch (Exception e){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
			}
			try{if (pstmt2 != null){pstmt2.close();}}catch (Exception e){
				loggerValue.setLength(0);
            	loggerValue.append("Exception ");
            	loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled())
				loggerValue.setLength(0);
        	    loggerValue.append("Exiting:  transfersList =");
            	loggerValue.append(transfersList.size());
          	   _log.debug(methodName,  loggerValue );
        }
        return transfersList;
    }
    
    /**
     *  isPendingTransactionExist
     *  This method is to check that the user has any C2S pending request of transfer or not
     * @param p_con
     * @param p_userID
     * @return
     * @throws BTSLBaseException boolean
     */
	public boolean isC2SPendingTransactionExist(Connection p_con, String p_userID) throws BTSLBaseException
    {
		//local_index_missing
        final String methodName = "isC2SPendingTransactionExist";
        StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled())
			  loggerValue.setLength(0);
		      loggerValue.append("Entered   p_userID ");
		      loggerValue.append(p_userID);
            _log.debug(methodName, loggerValue);
		boolean isExist=false;
		PreparedStatement pstmt = null;
        ResultSet rs = null;
		try
        {
			StringBuffer strBuff = new StringBuffer();
			strBuff.append(" SELECT 1  ");
	        strBuff.append(" FROM C2S_TRANSFERS ");
			strBuff.append(" WHERE transfer_date > (select executed_upto from process_status where process_id = 'C2SMIS') and sender_id=? AND ");
			strBuff.append(" transfer_status IN( ?,?)");
	        String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled())
				_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
			int i=1;
            pstmt.setString(i++, p_userID);
            pstmt.setString(i++, PretupsI.TXN_STATUS_AMBIGIOUS);
            pstmt.setString(i++, PretupsI.TXN_STATUS_AMBIGIOUS1);
			rs = pstmt.executeQuery();
            if(rs.next())
				isExist=true;
		}
        catch (SQLException sqe)
        {     loggerValue.setLength(0);
              loggerValue.append("SQLException : ");
              loggerValue.append(sqe);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName,sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isC2SPendingTransactionExist]", "", "", "", 
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        }
        catch (Exception ex)
        {       loggerValue.setLength(0);
		        loggerValue.setLength(0);
		        loggerValue.append("Exception : ");
		        loggerValue.append(ex);
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName,ex);
            loggerValue.setLength(0);
	        loggerValue.setLength(0);
	        loggerValue.append("Exception : ");
	        loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isC2SPendingTransactionExist]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
        finally
        {
            try{if (rs != null){rs.close();}}catch (Exception e){_log.errorTrace(methodName,e);}
			try{if (pstmt != null){pstmt.close();}}catch (Exception e){_log.errorTrace(methodName,e);}
			if (_log.isDebugEnabled())
				loggerValue.setLength(0);
				loggerValue.append("Exiting:  isExist=");
				loggerValue.append(isExist);
                _log.debug(methodName,  loggerValue );
        }
        return isExist;
    }
    
	/**
	 * @param con
	 * @param lastSOSTxnID
	 * @param fromUserID
	 * @param toUserID
	 * @param status 
	 * @return
	 * @throws BTSLBaseException
	 */
	public int sosUpdateChannelTransfer(Connection con, String lastSOSTxnID, String fromUserID, String toUserID, String status, String networkCode ) throws BTSLBaseException
    {
        final String methodName = "sosUpdateChannelTransfer";
        StringBuilder loggerValue= new StringBuilder(); 
        loggerValue.setLength(0);
        loggerValue.append("Entered SOSTxnID = ");
        loggerValue.append(lastSOSTxnID);
        loggerValue.append("fromUserID=");
        loggerValue.append(fromUserID);
        loggerValue.append("toUserID=");
        loggerValue.append(toUserID);
        loggerValue.append("status=");
        loggerValue.append(status);
        
      	LogFactory.printLog(methodName, loggerValue.toString(), _log);
		boolean isExist=false;
		 
        int updateCount = 0;
        Date date = new Date();
		try
        {
			StringBuilder updateBuff = new StringBuilder();
			   updateBuff.append(" update channel_transfers set sos_status = ? ");
	            
	            if(!BTSLUtil.isNullString(status))
	            	updateBuff.append("  , sos_settlement_date = ? ");
	            
	            updateBuff.append("	where transfer_id = ? and from_user_id = ? and to_user_id = ?");
	          
	            if(!BTSLUtil.isNullString(status))
	            	updateBuff.append(" and sos_status = ? ");
	            
	            String sqlUpdate = updateBuff.toString();
            LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlUpdate, _log);
            try(PreparedStatement pstmt1 = con.prepareStatement(sqlUpdate);)
            {
            	
            	 int i =1;
                 if(!BTSLUtil.isNullString(status))
                 	pstmt1.setString(i++, status);
                 else
                		pstmt1.setString(i++, PretupsI.SOS_PENDING_STATUS);

                 if(!BTSLUtil.isNullString(status))
                 pstmt1.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(date));
                 
                 pstmt1.setString(i++, lastSOSTxnID);

                 if(!BTSLUtil.isNullString(status)) {
                 	if(PretupsI.SOS_AUTO_SETTLED_STATUS.equals(status))
                 	{
                 		pstmt1.setString(i++, fromUserID);
                 	}
                 	else if (PretupsI.SOS_MANUAL_SETTLED_STATUS.equals(status)) {
                 		if(PretupsI.SOS_NETWORK.equalsIgnoreCase((String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, networkCode)))
                 		{
                 			pstmt1.setString(i++, PretupsI.OPERATOR_TYPE_OPT);
                 		}
                 		else
                 			pstmt1.setString(i++, fromUserID);
                 	}
                 }
                 else
                 	pstmt1.setString(i++, fromUserID);
                 
                 pstmt1.setString(i++, toUserID);
                 
                 if(!BTSLUtil.isNullString(status))
                 	pstmt1.setString(i++, PretupsI.SOS_PENDING_STATUS);

                 updateCount = pstmt1.executeUpdate();
           
		}
        }
        catch (SQLException sqe)
        {     loggerValue.setLength(0);
              loggerValue.append("SQLException : ");
              loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName,sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[sosUpdateChannelTransfer]", "", "", "", 
            		loggerValue.toString());
            throw new BTSLBaseException("ChannelTransferDAO", "", "error.general.sql.processing");
        }
        catch (Exception ex)
        {    loggerValue.setLength(0);
             loggerValue.append("Exception : " );
             loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName,ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:" );
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[sosUpdateChannelTransfer]", "", "", "", 
            		loggerValue.toString() );
            throw new BTSLBaseException("ChannelTransferDAO", methodName, "error.general.processing");
        } 
        finally
        {
			
			if (_log.isDebugEnabled())
				loggerValue.setLength(0);
		      	loggerValue.append("Exiting:  isExist=");
				loggerValue.append(isExist);
                _log.debug(methodName,  loggerValue);
        }
        return updateCount;
    }
	public int lrUpdateChannelTransfer(Connection con, String lastLRTxnID, String fromUserID, String toUserID, String status, String networkCode ) throws BTSLBaseException
    {
        final String methodName = "lrUpdateChannelTransfer";
        StringBuilder loggerValue= new StringBuilder(); 
        loggerValue.setLength(0);
        loggerValue.append("Entered lastLRTxnID = ");
        loggerValue.append(lastLRTxnID);
		LogFactory.printLog(methodName, loggerValue.toString(), _log);
		boolean isExist=false;
		
        int updateCount = 0;
        Date date = new Date();
		try
        {
			StringBuilder updateBuff = new StringBuilder();
            updateBuff.append(" update channel_transfers set sos_status = ?, sos_settlement_date = ? where transfer_id = ? and from_user_id = ? and to_user_id = ? and sos_status = ? ");
            String sqlUpdate = updateBuff.toString();
            LogFactory.printLog(methodName, "QUERY sqlSelect=" + sqlUpdate, _log);
            try(PreparedStatement pstmt1 = con.prepareStatement(sqlUpdate);)
            {
           	pstmt1.setString(1, status);
            pstmt1.setDate(2, BTSLUtil.getSQLDateFromUtilDate(date));
            pstmt1.setString(3, lastLRTxnID);
            pstmt1.setString(4, PretupsI.OPERATOR_TYPE_OPT);
            
            pstmt1.setString(5, toUserID);
            pstmt1.setString(6, PretupsI.LAST_LR_PENDING_STATUS);
            updateCount = pstmt1.executeUpdate();
            }
		}
        catch (SQLException sqe)
        {    loggerValue.setLength(0);
             loggerValue.append("SQLException : " );
             loggerValue.append(sqe);
            _log.error(methodName, loggerValue );
            _log.errorTrace(methodName,sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[sosUpdateChannelTransfer]", "", "", "", 
            		loggerValue.toString());
            throw new BTSLBaseException("ChannelTransferDAO", "", "error.general.sql.processing");
        }
        catch (Exception ex)
        {    loggerValue.setLength(0);
             loggerValue.append("Exception : ");
             loggerValue.append(ex);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName,ex);
            loggerValue.setLength(0);
            loggerValue.append( "Exception:");
            loggerValue.append( ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[sosUpdateChannelTransfer]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException("ChannelTransferDAO", methodName, "error.general.processing");
        } 
        finally
        {
			
			if (_log.isDebugEnabled())
				loggerValue.setLength(0);
			   loggerValue.append("Exiting:  isExist=");
			   loggerValue.append(isExist);
                _log.debug(methodName,  loggerValue );
        }
        return updateCount;
    }
	/**This method Load the last N number of c2c, c2s , o2c , foc transaction details.
     * @author sayyed.yasin 
     * @param con Connection
     * @param userId String
     * @param lastNoOfTxn int	//no of last transactions to be fetched.
     * @param lastNoOfDays		//fetch only data for this period  if null/0 then no check on the date.
	 * @param txnType			// C2C ,O2C , C2S , FOC transaction details.
     * @param txnSubType		// Transfer Sub Type Transfer , Return , WithDraw or Reverse
	 * @param c2cInOut		    // applicable in case of txn type as C2C to mark Channel User type as SENDER/RECEIVER
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadLastXTransfersDetails(Connection con, String userId,int lastNoOfTxn, int lastNoOfDays, String txnType , String txnSubType , String c2cInOut) throws BTSLBaseException
    {
    	final String methodName="loadLastXTransfersDetails";
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled())
    		loggerValue.setLength(0);
    	loggerValue.append("Entered  userId: ");
    	loggerValue.append(userId);
    	loggerValue.append(", lastNoOfTxn: ");
    	loggerValue.append(lastNoOfTxn);
    	loggerValue.append(" lastNoOfDays: ");
    	loggerValue.append(lastNoOfDays);
    	loggerValue.append(", txnType: ");
    	loggerValue.append(txnType);
    	loggerValue.append(" txnSubType: ");
    	loggerValue.append(txnSubType);
    	loggerValue.append(" c2cInOut: ");
    	loggerValue.append(c2cInOut);
    		_log.debug(methodName,  loggerValue);

    	C2STransferVO transferVO=null;
    	ArrayList transfersList=null;
    	PreparedStatement pstmt=null ;
		PreparedStatement pstmt1=null;
		PreparedStatement pstmt2=null;
		ResultSet rs=null;
		ResultSet rs1=null;
		ResultSet rs2=null;
    	try
    	{
    		transfersList=new ArrayList();
    		Date differenceDate=BTSLUtil.getDifferenceDate(new Date(), -lastNoOfDays);	//for getting diffence date.
    		if(PretupsI.TRANSFER_TYPE_C2S.equals(txnType))
    		{
    			pstmt = channelTransferQry.loadLastXC2STransferDetailsQry(con,userId,lastNoOfTxn,differenceDate,txnType,txnSubType);
    			rs = pstmt.executeQuery();
    			while(rs.next())
    			{
    				transferVO = new C2STransferVO();
    				transferVO.setTransferID(rs.getString("transfer_id"));
    				transferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
    				transferVO.setTransferStatus(rs.getString("transfer_status"));
    				transferVO.setType(PretupsI.C2S_MODULE);
    				transferVO.setReceiverMsisdn(rs.getString("receiver_Msisdn"));
    				transferVO.setTransferValue(rs.getLong("net_payable_amount"));
    				transferVO.setCreatedOn(rs.getTimestamp("created_on"));
    				transferVO.setServiceType(rs.getString("service"));
    				transferVO.setServiceName(rs.getString("name"));
    				transferVO.setStatus(rs.getString("statusname"));
    				transferVO.setSenderPostBalance(rs.getLong("sender_post_balance"));
    				if(!BTSLUtil.isNullString(rs.getString("error_code")))
    					transferVO.setErrorCode(rs.getString("error_code"));
    				else
    					transferVO.setErrorCode("");	
    				transfersList.add(transferVO);
    			}
    		}     
    		else if(PretupsI.TRANSFER_TYPE_C2C.equals(txnType))
    		{
    			pstmt1 = channelTransferQry.loadLastXC2CTransferDetailsQry(con,userId,lastNoOfTxn,differenceDate,txnType,txnSubType,c2cInOut);
    			rs1 = pstmt1.executeQuery();
    			while(rs1.next())
    			{
    				transferVO = new C2STransferVO();
    				transferVO.setTransferID(rs.getString("transfer_id"));
    				if(rs1.getTimestamp("CLOSE_DATE")!=null)
    					transferVO.setTransferDateTime(rs1.getTimestamp("CLOSE_DATE"));
    				else
    					transferVO.setTransferDateTime(rs1.getTimestamp("MODIFIED_ON"));
    				transferVO.setTransferStatus(rs1.getString("status"));
    				transferVO.setType(PretupsI.C2C_MODULE);
    				transferVO.setReceiverMsisdn(rs1.getString("to_msisdn"));
    				transferVO.setSenderMsisdn(rs1.getString("msisdn"));
    				transferVO.setTransferValue(rs1.getLong("net_payable_amount"));
    				transferVO.setCreatedOn(rs1.getTimestamp("created_on"));
    				transferVO.setServiceType(rs1.getString("service"));								//set service type here.
    				transferVO.setServiceName(rs1.getString("name"));							//from look-up set service name
    				transferVO.setStatus(rs1.getString("statusname"));
    				long approvedQuantity=rs1.getLong("approved_quantity");
    				if(PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE.equals(rs1.getString("transfer_sub_type"))  || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(rs1.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs1.getString("transfer_sub_type")))
    					transferVO.setSenderPostBalance(rs1.getLong("sender_previous_stock") - approvedQuantity);
    				else
    					transferVO.setSenderPostBalance(rs1.getLong("receiver_previous_stock") + approvedQuantity);
    				transferVO.setErrorCode("");
    				transfersList.add(transferVO);
    			}
    		}
    		else if((PretupsI.TRANSFER_TYPE_O2C).equalsIgnoreCase(txnType) || (PretupsI.TRANSFER_TYPE_FOC).equalsIgnoreCase(txnType))	
    		{
    			pstmt2 = channelTransferQry.loadLastXO2CTransferDetailsQry(con,userId,lastNoOfTxn,differenceDate,txnType,txnSubType);
    			rs2 = pstmt2.executeQuery();
    			while(rs2.next())
    			{
    				transferVO = new C2STransferVO();
    				transferVO.setTransferID(rs2.getString("transfer_id"));
    				if(rs2.getTimestamp("CLOSE_DATE")!=null)
    					transferVO.setTransferDateTime(rs2.getTimestamp("CLOSE_DATE"));
    				else
    					transferVO.setTransferDateTime(rs2.getTimestamp("MODIFIED_ON"));
    				transferVO.setTransferStatus(rs2.getString("status"));
    				transferVO.setType(txnType);
    				transferVO.setSenderMsisdn(rs2.getString("msisdn"));
    				transferVO.setReceiverMsisdn(rs2.getString("to_msisdn"));
    				transferVO.setTransferValue(rs2.getLong("approved_quantity"));
    				transferVO.setCreatedOn(rs2.getTimestamp("created_on"));
    				transferVO.setServiceType(rs2.getString("service"));							//from look-up set service name
    				transferVO.setServiceName(rs2.getString("name"));								//set sub service type here.
    				transferVO.setStatus(rs2.getString("statusname"));
    				long approvedQuantity=rs2.getLong("approved_quantity");
    				if(PretupsI.TRANSFER_TYPE_REVERSE_SUB_TYPE.equals(rs2.getString("transfer_sub_type"))  || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(rs2.getString("transfer_sub_type")) || PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(rs2.getString("transfer_sub_type")))
    					transferVO.setSenderPostBalance(rs2.getLong("sender_previous_stock")-approvedQuantity);
    				else
    					transferVO.setSenderPostBalance(rs2.getLong("receiver_previous_stock") + approvedQuantity);
    				transferVO.setErrorCode("");
    				transfersList.add(transferVO);
    			}
    		}

    	}
    	catch (SQLException sqe)
    	{    loggerValue.setLength(0);
    	    loggerValue.append("SQLException : " );
      	    loggerValue.append(sqe);
    		_log.error(methodName, loggerValue);
    		_log.errorTrace(methodName, sqe);
    		
    		loggerValue.setLength(0);
    		loggerValue.append("SQL Exception:");
    		loggerValue.append(sqe.getMessage());
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO["+methodName+"]", "", "", "", 
    				loggerValue.toString() );
    		throw new BTSLBaseException(this, "", "error.general.sql.processing");
    	}
    	catch (Exception ex)
    	{   loggerValue.setLength(0);
    	     loggerValue.append("Exception : ");
    	    loggerValue.append(ex);
    		_log.error(methodName,  loggerValue );
    		_log.errorTrace(methodName, ex);
    		loggerValue.setLength(0);
    		loggerValue.append("Exception:");
    		loggerValue.append(ex.getMessage());
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO["+methodName+"]", "", "", "", 
    				loggerValue.toString());
    		throw new BTSLBaseException(this, methodName, "error.general.processing");
    	}
    	finally
        {
            try{
            	if (rs != null)
            	{
            		rs.close();
            		}
            	}catch (Exception e){
            	 loggerValue.setLength(0);
            	 loggerValue.append("Exception ");
            	 loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
            }
            try{
            	if (rs1 != null)
            	{
            		rs1.close();
            		}
            	}catch (Exception e){
            	 loggerValue.setLength(0);
            	 loggerValue.append("Exception ");
            	 loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
            }
            try{
            	if (rs2 != null)
            	{
            		rs2.close();
            		}
            	}catch (Exception e){
            	 loggerValue.setLength(0);
            	 loggerValue.append("Exception ");
            	 loggerValue.append(e);
            	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
            }
			try{if (pstmt != null){pstmt.close();}}catch (Exception e){
				loggerValue.setLength(0);
           	    loggerValue.append("Exception ");
           	    loggerValue.append(e);
           	   _log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
			}
			try{if (pstmt1 != null){pstmt1.close();}}catch (Exception e){
				loggerValue.setLength(0);
           	 loggerValue.append("Exception ");
           	 loggerValue.append(e);
           	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
			}
			try{if (pstmt2 != null){pstmt2.close();}}catch (Exception e){
				loggerValue.setLength(0);
           	 loggerValue.append("Exception ");
           	 loggerValue.append(e);
           	_log.error(methodName,  loggerValue);
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:  transfersList =" + transfersList.size());
        }
    	
    	return transfersList;
    }	
    /**This method Load the c2c,  o2c transaction details based on reference number or txnId.
     * @author anjali.agarwal 
     * @param con Connection
     * @param user_id String    
     * @param txnId String	     //either reference id or txn id.
	 * @param type			    // whether req is coming with reference no. or txn id.
     * @return ChannelTransferVO
     * @throws BTSLBaseException
     */
    public ChannelTransferVO loadChannelTxnDetails(Connection pCon,String pTxnId,String pType,String pUserId,String networkCode,String userType) throws BTSLBaseException
    {
    	final String methodName="loadChannelTxnDetails";
    	StringBuilder loggerValue= new StringBuilder(); 
       if (_log.isDebugEnabled())
    	   loggerValue.setLength(0);
           loggerValue.append("Entered  txnId: " );
           loggerValue.append(pTxnId);
           loggerValue.append(" Type: ");
           loggerValue.append(pType);
           loggerValue.append(" userID: ");
           loggerValue.append(pUserId);
           loggerValue.append(" networkCode: ");
           loggerValue.append(networkCode);
           loggerValue.append(" userType: ");
           loggerValue.append(userType);
            _log.debug(methodName, loggerValue);
        PreparedStatement pstmt = null ;
        ResultSet rs = null;
        ChannelTransferVO transferVO = null;
        try
        {
			StringBuilder selectQueryBuff = channelTransferQry.loadChannelTxnDetailsQry(pCon, pType,userType);
			String selectQuery=selectQueryBuff.toString();
			if(_log.isDebugEnabled())
				_log.debug(methodName,"select query:"+selectQuery );		
			pstmt = pCon.prepareStatement(selectQuery);
			int i=1;
			pstmt.setString(i++, pTxnId);
			pstmt.setString(i++, networkCode);
			if(PretupsI.CHANNEL_USER_TYPE.equals(userType))
			{
				pstmt.setString(i++, PretupsI.USER_TYPE_CHANNEL);
				pstmt.setString(i++, pUserId);
				pstmt.setString(i++, PretupsI.USER_TYPE_CHANNEL);
				pstmt.setString(i++, pUserId);
			}
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				transferVO=new ChannelTransferVO();
				transferVO.setTransferID(rs.getString("TRANSFER_ID"));
				transferVO.setNetworkCode(rs.getString("NETWORK_CODE"));
				transferVO.setReferenceNum(rs.getString("REFERENCE_NO"));
				transferVO.setTransferMRP(rs.getLong("TRANSFER_MRP"));
				transferVO.setChannelRemarks(rs.getString("CHANNEL_USER_REMARKS"));
				transferVO.setStatus(rs.getString("STATUS"));
				transferVO.setType(rs.getString("TYPE"));
				transferVO.setCloseDate(rs.getDate("CLOSE_DATE"));
				transferVO.setWalletType(rs.getString("TXN_WALLET"));
				transferVO.setToUserMsisdn(rs.getString("TO_MSISDN"));
				transferVO.setTransferSubType(rs.getString("TRANSFER_SUB_TYPE"));
				transferVO.setProductCode(rs.getString("PRODUCT_CODE"));
				transferVO.setFromUserID(rs.getString("FROM_USER_ID"));
				transferVO.setToUserID(rs.getString("TO_USER_ID"));
				transferVO.setGraphicalDomainCode(rs.getString("GRPH_DOMAIN_CODE"));
			}
			
        }
        catch (SQLException sqe)
        {      loggerValue.setLength(0);
		        loggerValue.append("SQLException : ");
		        loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            if(_log.isDebugEnabled())
            	    loggerValue.setLength(0);
			        loggerValue.append("SQL Exception:");
			        loggerValue.append(sqe);
            	_log.debug(methodName, loggerValue);
            	 loggerValue.setLength(0);
			        loggerValue.append("SQL Exception:");
			        loggerValue.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTxnDetails]", "", "", "", 
            		loggerValue.toString());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } 
        finally
        {
            	try{
                    if (rs!= null){
                    	rs.close();
                    }
                  }
                  catch (SQLException e){
                	  _log.error("An error occurred closing result set.", e);
                  }
            	try{
                    if (pstmt!= null){
                    	pstmt.close();
                    }
                  }
                  catch (SQLException e){
                	  _log.error("An error occurred closing result set.", e);
                  }
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting transferVO: ");
        	loggerValue.append(transferVO);
            _log.debug(methodName, loggerValue);
        }
        return transferVO;
    }
    
    private int addVoucherItems(Connection p_con, List p_transferItemList, String p_transferId, Date p_transferDate) throws BTSLBaseException {
        final String methodName = "addVoucherItems";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered TransferItemList Size: " );
        	loggerValue.append(p_transferItemList.size());
        	loggerValue.append(" TransferId : ");
        	loggerValue.append(p_transferId);
        	loggerValue.append("?p_transferDate=");
        	loggerValue.append(p_transferDate);
            _log.debug(methodName, loggerValue );
        }

        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" INSERT INTO channel_voucher_items ( ");
            strBuff.append(" s_no,transfer_id,transfer_date,voucher_type,product_id,mrp, ");
            strBuff.append(" requested_quantity,from_serial_no, to_serial_no, network_code, voucher_segment, bundle_id, remarks, ");
            strBuff.append(" type, from_user, to_user, modified_on, initiated_quantity ) ");
            strBuff.append(" VALUES  ");
            strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

            final String query = strBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query: " + query);
            }

            psmt = p_con.prepareStatement(query);
            ChannelVoucherItemsVO voucherItemsVO = null;
            Date todayDate = new Date();
            for (int i = 0, k = p_transferItemList.size(); i < k; i++) {
            	voucherItemsVO = (ChannelVoucherItemsVO) p_transferItemList.get(i);
                psmt.clearParameters();
                int m = 0;
                psmt.setLong(++m, i+1);
                psmt.setString(++m, p_transferId);
                psmt.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(voucherItemsVO.getTransferDate()));
                psmt.setString(++m, voucherItemsVO.getVoucherType());
                psmt.setString(++m, voucherItemsVO.getProductId());
                psmt.setLong(++m, voucherItemsVO.getTransferMrp());
                psmt.setLong(++m, voucherItemsVO.getRequiredQuantity());
                psmt.setString(++m, voucherItemsVO.getFromSerialNum());
                psmt.setString(++m, voucherItemsVO.getToSerialNum());
                psmt.setString(++m, voucherItemsVO.getNetworkCode());
                psmt.setString(++m, voucherItemsVO.getSegment());
				psmt.setLong(++m, voucherItemsVO.getBundleId());
				psmt.setString(++m, voucherItemsVO.getBundleRemarks());
                psmt.setString(++m, voucherItemsVO.getType());
                psmt.setString(++m, voucherItemsVO.getFromUser());
                psmt.setString(++m, voucherItemsVO.getToUser());
                psmt.setTimestamp(++m, BTSLUtil.getSQLDateTimeFromUtilDate(todayDate));
                psmt.setLong(++m, voucherItemsVO.getRequiredQuantity());
                
                updateCount = psmt.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); 

                if (updateCount <= 0) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "ChannelUserDAO[addTransferItems]", "", "", "",
                        "BTSLBaseException: update count <=0");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        }// end of try
        catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addTransferItems]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[addTransferItems]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue );
            }
        }// end of finally

        return updateCount;
    }
    
    
    
    public ArrayList<ChannelVoucherItemsVO> loadChannelVoucherItemsList(Connection p_con, String p_transferId, String[] fromSerialNo, String[] toSerialNo, String ownerUserId) throws BTSLBaseException {
        final String methodName = "loadChannelVoucherItemsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferId=" + p_transferId );
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ChannelVoucherItemsVO chnlVoucherItemVO = null;
        
        StringBuffer strBuff2 = new StringBuffer();
        strBuff2.append(" SELECT PRODUCT_ID, COUNT(1) ");
        strBuff2.append(" FROM VOMS_VOUCHERS WHERE ( SERIAL_NO BETWEEN ?  AND ? ) AND USER_ID = ?  GROUP BY PRODUCT_ID ");
        String sqlSelect2 = strBuff2.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect2);
        }

               
        
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT CVI.S_NO, CVI.TRANSFER_ID, CVI.TRANSFER_DATE, CVI.VOUCHER_TYPE, CVI.PRODUCT_ID, CVI.MRP, CVI.REQUESTED_QUANTITY, CVI.FROM_SERIAL_NO, CVI.TO_SERIAL_NO, VT.NAME, CVI.voucher_segment,CVI.network_code, CVI.transfer_date , CVI.INITIATED_QUANTITY, CVI.FIRST_LEVEL_APPROVED_QUANTITY, CVI.SECOND_LEVEL_APPROVED_QUANTITY, ");
        strBuff.append(" CVI.FIRST_LEVEL_APPROVED_QUANTITY, CVI.SECOND_LEVEL_APPROVED_QUANTITY, CVI.INITIATED_QUANTITY ");
        strBuff.append(" FROM CHANNEL_VOUCHER_ITEMS CVI, VOMS_TYPES VT WHERE CVI.transfer_id = ? and CVI.VOUCHER_TYPE = VT.VOUCHER_TYPE  order by CVI.S_NO");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList<ChannelVoucherItemsVO> list = new ArrayList<ChannelVoucherItemsVO>();
        try {
        	
        	long rangeDiffInput = 0;
        	long rangeDiffExisting = 0;
        	
            
            
			for (int serialIncr = 0; serialIncr < fromSerialNo.length; serialIncr++) {
				
				
				if(BTSLUtil.isEmpty(fromSerialNo[serialIncr]) == false) {
				
				rangeDiffInput = rangeDiffInput + ( Long.parseLong(toSerialNo[serialIncr]) - Long.parseLong(fromSerialNo[serialIncr]));
			
				pstmt2 = p_con.prepareStatement(sqlSelect2);
				pstmt2.setString(1, fromSerialNo[serialIncr]);
				pstmt2.setString(2, toSerialNo[serialIncr]);
				pstmt2.setString(3, ownerUserId);

				
				rs2 = pstmt2.executeQuery();
				
				long quantity = ( Long.parseLong(toSerialNo[serialIncr]) - Long.parseLong(fromSerialNo[serialIncr]) ) +1;
				long overAllCount = 0L;
				long productCount = 0L;
				while (rs2.next()) {
					overAllCount = overAllCount + rs2.getLong(2);
					productCount++;
				}

				if ((overAllCount < quantity) || productCount > 1 || overAllCount == 0) {
					return null;
				}

				
				
				try {
	                if (rs2 != null) {
	                    rs2.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            try {
	                if (pstmt2 != null) {
	                    pstmt2.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	        
			}
			}
            
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_transferId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	chnlVoucherItemVO = new ChannelVoucherItemsVO();
            	chnlVoucherItemVO.setSNo(rs.getLong("S_NO"));
            	chnlVoucherItemVO.setTransferId(p_transferId);
            	chnlVoucherItemVO.setTransferDate(rs.getDate("transfer_date"));
            	chnlVoucherItemVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
            	chnlVoucherItemVO.setProductId(rs.getString("PRODUCT_ID"));
            	chnlVoucherItemVO.setTransferMRP(rs.getLong("MRP"));
            	chnlVoucherItemVO.setRequiredQuantity(rs.getLong("REQUESTED_QUANTITY"));
            	chnlVoucherItemVO.setFromSerialNum(rs.getString("FROM_SERIAL_NO"));
            	chnlVoucherItemVO.setToSerialNum(rs.getString("TO_SERIAL_NO"));
            	chnlVoucherItemVO.setVoucherTypeDesc(rs.getString("NAME"));
            	chnlVoucherItemVO.setSegment(rs.getString("voucher_segment"));
            	chnlVoucherItemVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("voucher_segment")));
            	chnlVoucherItemVO.setNetworkCode(rs.getString("network_code"));
            	
            	chnlVoucherItemVO.setInitiatedQuantity(rs.getLong("INITIATED_QUANTITY"));
            	chnlVoucherItemVO.setFirstLevelApprovedQuantity(rs.getLong("FIRST_LEVEL_APPROVED_QUANTITY"));
            	chnlVoucherItemVO.setSecondLevelApprovedQuantity(rs.getLong("SECOND_LEVEL_APPROVED_QUANTITY"));
            	
                        	
            	
                list.add(chnlVoucherItemVO);
                
                
                //long rangeDiff1 = Long.parseLong(toSerialNo) - Long.parseLong(fromSerialNo);
                rangeDiffExisting = rangeDiffExisting + chnlVoucherItemVO.getInitiatedQuantity();
                
               
            }
            
            
            if(rangeDiffInput > rangeDiffExisting) {
            	return null;
            }
            
            
            
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelVoucherItemsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelVoucherItemsList]", "", "", "", "Exception:" + ex.getMessage());
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
            
            
            
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt2 != null) {
                    pstmt2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: loadChannelVoucherItemsList size=" + list.size());
            }
        }
        return list;
    }
    
    public boolean isVoucherAlreadySoldInRange(Connection p_con, String[] fromSerialNo, String[] toSerialNo) throws BTSLBaseException {
    	final String methodName = "isVoucherAlreadySoldInRange";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered fromSerialNo=" + fromSerialNo + ",toSerialNo=" + toSerialNo);
        }
        boolean isSold = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT COUNT(1) AS COUNT FROM VOMS_VOUCHERS WHERE ( SERIAL_NO BETWEEN ?  AND ? ) AND SOLD_STATUS IN ('Y')");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        
        try {
			for (int serialIncr = 0; serialIncr < fromSerialNo.length; serialIncr++) {
				
				
				if(!BTSLUtil.isEmpty(fromSerialNo[serialIncr])) {
				
			
				pstmt = p_con.prepareStatement(sqlSelect);
				pstmt.setString(1, fromSerialNo[serialIncr]);
				pstmt.setString(2, toSerialNo[serialIncr]);

				
				rs = pstmt.executeQuery();
				
				
				while (rs.next()) {
					int soldCount = rs.getInt("COUNT");
					if(soldCount > 0) {
						return true;
					}
				}

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
	        
			}
			}
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isVoucherAlreadySoldInRange]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isVoucherAlreadySoldInRange]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting");
            }
        }
        return isSold;
    }
    
    
    public ArrayList loadChannelVoucherItemsList(Connection p_con, String p_transferId, Date p_transferDate) throws BTSLBaseException {
        final String methodName = "loadChannelVoucherItemsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferId=" + p_transferId + ",p_transferDate=" + p_transferDate);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ChannelVoucherItemsVO chnlVoucherItemVO = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT CVI.S_NO, CVI.TRANSFER_ID, CVI.TRANSFER_DATE, CVI.VOUCHER_TYPE, CVI.PRODUCT_ID, CVI.MRP, CVI.REQUESTED_QUANTITY, CVI.FROM_SERIAL_NO, CVI.TO_SERIAL_NO, VT.NAME, CVI.voucher_segment,CVI.network_code,CVI.bundle_id, ");
        strBuff.append(" CVI.FIRST_LEVEL_APPROVED_QUANTITY, CVI.SECOND_LEVEL_APPROVED_QUANTITY, CVI.INITIATED_QUANTITY ");
        strBuff.append(" FROM CHANNEL_VOUCHER_ITEMS CVI, VOMS_TYPES VT WHERE CVI.transfer_id = ? and CVI.VOUCHER_TYPE = VT.VOUCHER_TYPE and CVI.transfer_date = ? order by CVI.S_NO");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_transferId);
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_transferDate));
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	chnlVoucherItemVO = new ChannelVoucherItemsVO();
            	chnlVoucherItemVO.setSNo(rs.getLong("S_NO"));
            	chnlVoucherItemVO.setTransferId(p_transferId);
            	chnlVoucherItemVO.setTransferDate(p_transferDate);
            	chnlVoucherItemVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
            	chnlVoucherItemVO.setProductId(rs.getString("PRODUCT_ID"));
            	chnlVoucherItemVO.setTransferMRP(rs.getLong("MRP"));
            	chnlVoucherItemVO.setRequiredQuantity(rs.getLong("REQUESTED_QUANTITY"));
            	chnlVoucherItemVO.setFromSerialNum(rs.getString("FROM_SERIAL_NO"));
            	chnlVoucherItemVO.setToSerialNum(rs.getString("TO_SERIAL_NO"));
            	chnlVoucherItemVO.setVoucherTypeDesc(rs.getString("NAME"));
            	chnlVoucherItemVO.setSegment(rs.getString("voucher_segment"));
            	chnlVoucherItemVO.setSegmentDesc(BTSLUtil.getSegmentDesc(rs.getString("voucher_segment")));
            	chnlVoucherItemVO.setNetworkCode(rs.getString("network_code"));
            	chnlVoucherItemVO.setFirstLevelApprovedQuantity(rs.getLong("FIRST_LEVEL_APPROVED_QUANTITY"));
            	chnlVoucherItemVO.setSecondLevelApprovedQuantity(rs.getLong("SECOND_LEVEL_APPROVED_QUANTITY"));
            	chnlVoucherItemVO.setInitiatedQuantity(rs.getLong("INITIATED_QUANTITY"));
				chnlVoucherItemVO.setBundleId(rs.getLong("bundle_id"));
                list.add(chnlVoucherItemVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelVoucherItemsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelVoucherItemsList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: loadChannelVoucherItemsList size=" + list.size());
            }
        }
        return list;
    }
    
    public String retreiveProductId(Connection p_con, String fromSerialNo) {
    	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String qry = "SELECT PRODUCT_ID FROM VOMS_VOUCHERS WHERE SERIAL_NO = ? ";

		
		try {

			pstmt = p_con.prepareStatement(qry);
			pstmt.setString(1, fromSerialNo);
			rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					return rs.getString(1);
				}
			}
		} catch (Exception e) {
			_log.debug("retreiveProductId", "Exception while fetching productId " + e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					_log.debug("retreiveProductId", "Could not close preparedstatement " + e);
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					_log.debug("retreiveProductId", "Could not close resultSet " + e);
				}
			}
		}

		return "";
	
    }
    
    public int updateChannelVoucherItems(Connection p_con, ArrayList p_itemsList) throws BTSLBaseException {
        final String methodName = "updateChannelVoucherItems";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        int update_count = 0;
        
        final StringBuffer strBuffer = new StringBuffer("UPDATE channel_voucher_items SET requested_quantity=?, product_id=?, from_serial_no=?, to_serial_no = ? , MODIFIED_ON =  ? , ");
        strBuffer.append(" INITIATED_QUANTITY=?, FIRST_LEVEL_APPROVED_QUANTITY=?, SECOND_LEVEL_APPROVED_QUANTITY=?   where transfer_id = ? AND s_no = ? AND transfer_date = ? AND mrp = ? ");
        final String sqlSlt = strBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSlt=" + sqlSlt);
        }
        Date todayDate = new Date();
        try(PreparedStatement psmt = p_con.prepareStatement(sqlSlt); ) {
            ChannelVoucherItemsVO itemsVO = null;
            int m = 0;
            for (int i = 0, k = p_itemsList.size(); i < k; i++) {
                itemsVO = (ChannelVoucherItemsVO) p_itemsList.get(i);
                m = 0;
            	++m;
                psmt.setLong(m, itemsVO.getRequiredQuantity());
            	++m;
            	if(itemsVO.getProductId() == null){
            		psmt.setString(m, retreiveProductId(p_con, itemsVO.getFromSerialNum()));
            	}else{
            		psmt.setString(m, itemsVO.getProductId());	
            	}
                ++m;
                psmt.setString(m, itemsVO.getFromSerialNum());
                ++m;
                psmt.setString(m, itemsVO.getToSerialNum());
                
                
                ++m;
                psmt.setTimestamp(m, BTSLUtil.getSQLDateTimeFromUtilDate(todayDate));               
                
                
                
                ++m;
                psmt.setLong(m, itemsVO.getInitiatedQuantity());
                ++m;
                psmt.setLong(m, itemsVO.getFirstLevelApprovedQuantity());
                ++m;
                psmt.setLong(m, itemsVO.getSecondLevelApprovedQuantity());
                
                
                
                
                ++m;
                psmt.setString(m, itemsVO.getTransferId());
                ++m;
                psmt.setLong(m, itemsVO.getSNo());
                ++m;
                psmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(itemsVO.getTransferDate()));
                ++m;
                psmt.setLong(m, itemsVO.getTransferMrp());
                
                update_count = psmt.executeUpdate();
                psmt.clearParameters();
                update_count = BTSLUtil.getInsertCount(update_count);
                if (update_count < 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelVoucherItems]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelVoucherItems", "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelVoucherItems]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelVoucherItems", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
                  loggerValue.setLength(0);
                  loggerValue.append("Exiting: update count  =");
                  loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue);
            }
        }
        return update_count;

    }

    public int updateChannelTransferVoucherApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, int approvalLevel) throws BTSLBaseException {
        final String methodName = "updateChannelTransferVoucherApproval";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue );
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE  channel_transfers SET  ");
            strBuff.append(" TOTAL_TAX1 =?, TOTAL_TAX2 =?, TOTAL_TAX3 =?,");
            strBuff.append(" modified_by = ?, modified_on = ?,  pmt_inst_type = ?, pmt_inst_no = ? , pmt_inst_date = ?, ");
            if(approvalLevel == 1){
            	strBuff.append(" first_approver_remarks = ?, first_approved_by = ?, first_approved_on = ?, FIRST_LEVEL_APPROVED_QUANTITY =? ");
    		}
    		else if(approvalLevel == 2){
    			strBuff.append(" second_approver_remarks = ?, second_approved_by = ?, second_approved_on = ?, SECOND_LEVEL_APPROVED_QUANTITY = ? ");
    		}
    		else if(approvalLevel == 3){
    			strBuff.append(" third_approver_remarks = ?, third_approved_by = ?, third_approved_on = ?, THIRD_LEVEL_APPROVED_QUANTITY = ? ");
    		}
            strBuff.append(" , status = ?,ext_txn_no = ? , ext_txn_date =  ? , reference_no = ?  ");
            /*if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
                strBuff.append(" ,first_level_approved_quantity = ?");
            }*/
            strBuff.append(" ,payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ? ");
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                strBuff.append(",close_date=? ");
            }
            if(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus()))
            {
            	strBuff.append(",stock_updated = ? ");
            }
            strBuff.append(", sms_default_lang=?, sms_second_lang=?, active_user_id = ? ");
            strBuff.append(" WHERE transfer_id = ?  AND status IN (? , ?, ? ) ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "insert query:" + query);
            }

            // commented for DB2 psmt = (OraclePreparedStatement)
            // p_con.prepareStatement(query);
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax1());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax2());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTotalTax3());
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentType());
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            // for multilanguage support
            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            if(approvalLevel == 1){
            	psmt.setString(i, p_channelTransferVO.getFirstApprovalRemark());
                ++i;
                psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
                ++i;
                psmt.setLong(i,   (BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity()) ? 0 : Long.parseLong(p_channelTransferVO.getLevelOneApprovedQuantity()))  );
    		}
    		else if(approvalLevel == 2){
            	psmt.setString(i, p_channelTransferVO.getSecondApprovalRemark());
                ++i;
                psmt.setString(i, p_channelTransferVO.getSecondApprovedBy());
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getSecondApprovedOn()));
                ++i;
                psmt.setLong(i,   (BTSLUtil.isNullString(p_channelTransferVO.getLevelTwoApprovedQuantity()) ? 0 : Long.parseLong(p_channelTransferVO.getLevelTwoApprovedQuantity()))  );
    		}
    		else if(approvalLevel == 3){
            	psmt.setString(i, p_channelTransferVO.getThirdApprovalRemark());
                ++i;
                psmt.setString(i, p_channelTransferVO.getThirdApprovedBy());
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getThirdApprovedOn())); 
                ++i;
                psmt.setLong(i,   (BTSLUtil.isNullString(p_channelTransferVO.getLevelThreeApprovedQuantity()) ? 0 : Long.parseLong(p_channelTransferVO.getLevelThreeApprovedQuantity()))  );
    		}
            
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            // added for editable reference number
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            
            
            
            // added for o2c transfer quantity change
            /*if (!BTSLUtil.isNullString(p_channelTransferVO.getLevelOneApprovedQuantity())) {
                ++i;
                psmt.setLong(i, PretupsBL.getSystemAmount(p_channelTransferVO.getLevelOneApprovedQuantity()));
            }*/
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
            	++i;
                psmt.setString(i, TypesI.YES);
            }
            // commented for DB2psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getDefaultLang());
            // commented for DB2 psmt.setFormOfUse(++i,
            // OraclePreparedStatement.FORM_NCHAR);
            ++i;
            psmt.setString(i, p_channelTransferVO.getSecondLang());

            ++i;
            psmt.setString(i, p_channelTransferVO.getActiveUserId());

            // where
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);

            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
          
            updateCount = BTSLUtil.getInsertCount(updateCount);
            if(updateCount > 0) {
            	updateCount = updateChannelTransferItemsForVoucher(p_con,p_channelTransferVO.getChannelTransferitemsVOList());
            	if(updateCount > 0 && ( PretupsI.CHANNEL_TYPE_O2C.equals(p_channelTransferVO.getType()) ||  PretupsI.CHANNEL_TYPE_C2C.equals(p_channelTransferVO.getType()) ) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(p_channelTransferVO.getTransferSubType()) && p_channelTransferVO.getChannelVoucherItemsVoList()!= null && p_channelTransferVO.getChannelVoucherItemsVoList().size() > 0)
            	{
					if(p_channelTransferVO.isBundleType()) {//for package distribution mode
                		updateCount = updateChannelVoucherItemsPackage(p_con,p_channelTransferVO.getChannelVoucherItemsVoList());
            		}
            		else
            			updateCount = updateChannelVoucherItems(p_con,p_channelTransferVO.getChannelVoucherItemsVoList());
            	}
            } else{
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferVoucherApproval]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferVoucherApproval]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append( "Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        return updateCount;
    }
    
    public int updateChannelTransferApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean isOrderApproved , boolean isFromPaymentGateway) throws BTSLBaseException {
        final String methodName = "updateChannelTransferApproval";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered subscriberVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue );
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE  channel_transfers SET  ");
            strBuff.append(" modified_by = ?, modified_on = ?,  pmt_inst_no = ? , pmt_inst_date = ?, ");
            strBuff.append(" status = ?,ext_txn_no = ? , ext_txn_date =  ? , reference_no = ?  ");
            strBuff.append(" ,payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ?,info1=?,info2=?,info3=?,info4=?,info5=?,info6=?,info7=?");
            if (p_channelTransferVO.isReconciliationFlag()) {
            strBuff.append(",reconciliation_by = ?, reconciliation_date = ? ,reconciliation_flag = ?, reconciliation_remark = ? ");
            }
            
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                strBuff.append(",close_date=? ");
                strBuff.append(",stock_updated = ? ");
            }
            if (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(p_channelTransferVO.getPayInstrumentType()) && isFromPaymentGateway || p_channelTransferVO.isReconciliationFlag()) {
            	strBuff.append(", pmt_inst_status=? ");
            }
            strBuff.append(" WHERE transfer_id = ?  AND status=? ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "update query:" + query);
            }
            psmt = p_con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, p_channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, p_channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, p_channelTransferVO.getReferenceNum());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, p_channelTransferVO.getTransferMRP());
            ++i;
            psmt.setString(i, p_channelTransferVO.getInfo1());
            ++i;
            psmt.setString(i, p_channelTransferVO.getInfo2());
            ++i;
            psmt.setString(i, p_channelTransferVO.getInfo3());
            ++i;
            psmt.setString(i, p_channelTransferVO.getInfo4());
            ++i;
            psmt.setString(i, p_channelTransferVO.getInfo5());
            ++i;
            psmt.setString(i, p_channelTransferVO.getInfo6());
            ++i;
            psmt.setString(i, p_channelTransferVO.getInfo7());
                             
            if (p_channelTransferVO.isReconciliationFlag()) {
            	 ++i;
                 psmt.setString(i, p_channelTransferVO.getFirstApprovedBy());
             	++i;
                 psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getFirstApprovedOn()));
                 ++i;
                 psmt.setString(i, PretupsI.YES);
             	++i;
                 psmt.setString(i, p_channelTransferVO.getFirstApprovalRemark());
            }
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(p_channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getModifiedOn()));
            	++i;
                psmt.setString(i, TypesI.YES);
            }
            if (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(p_channelTransferVO.getPayInstrumentType()) && isFromPaymentGateway || p_channelTransferVO.isReconciliationFlag()) {
            	++i;
                psmt.setString(i, p_channelTransferVO.getPayInstrumentStatus());
            }
            ++i;
            psmt.setString(i, p_channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
            final boolean modifiedFlag = this.isRecordModified(p_con, p_channelTransferVO.getLastModifiedTime(), p_channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = psmt.executeUpdate();
            updateCount = BTSLUtil.getInsertCount(updateCount);
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "channeltransfer.approval.msg.unsuccess");
            }
            updateChannelTransferItems(p_con, p_channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, true,p_channelTransferVO.getTransferID());
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqle);
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApproval]", "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelTransferDAO[updateChannelTransferApproval]", "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            	loggerValue.setLength(0);
            	loggerValue.append( "Exiting Success :");
            	loggerValue.append(updateCount);
                _log.debug(methodName, loggerValue);
            }
        }// end of finally
        return updateCount;
    }
    
    
    
    public void loadChannelTransferDetail(Connection p_con, ChannelTransferVO p_channelTransferVO,String status) throws BTSLBaseException {

        final String methodName = "loadChannelTransferDetail";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_channelTransferVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //final String sqlSelect = channelTransferQry.loadChannelTransfersVOQry(p_channelTransferVO);

        String sqlSelect = null;
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
		boolean tcpOn = false;
		Set<String> uniqueTransProfileId = new HashSet();
		HashMap<String, HashMap<String, String>> tcpMap = null;
		  
		if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
			tcpOn = true;
		}
	
		if (tcpOn) {
			sqlSelect = channelTransferQry.loadChannelTransferDetailTcpQry();
			
			SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")), ValueType.STRING);
			tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id", "status")),
					searchCriteria);

			
		} else {

			sqlSelect = channelTransferQry.loadChannelTransferDetailQry();
		}
		
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_channelTransferVO.getTransferID());
            ++m;
            pstmt.setString(m, status);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                p_channelTransferVO.setTransferID(rs.getString("transfer_id"));
                p_channelTransferVO.setNetworkCode(rs.getString("network_code"));
                p_channelTransferVO.setNetworkCodeFor(rs.getString("network_code_for"));
                p_channelTransferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
                p_channelTransferVO.setDomainCode(rs.getString("domain_code"));
                p_channelTransferVO.setCategoryCode(rs.getString("sender_category_code"));
                p_channelTransferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
                p_channelTransferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
                p_channelTransferVO.setFromUserID(rs.getString("from_user_id"));
                p_channelTransferVO.setToUserID(rs.getString("to_user_id"));
                p_channelTransferVO.setTransferDate(rs.getTimestamp("transfer_date"));
                p_channelTransferVO.setReferenceNum(rs.getString("reference_no"));
                p_channelTransferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                p_channelTransferVO.setExternalTxnDate(rs.getTimestamp("ext_txn_date"));
                p_channelTransferVO.setCommProfileSetId(rs.getString("commission_profile_set_id"));
                p_channelTransferVO.setCommProfileVersion(rs.getString("commission_profile_ver"));
                p_channelTransferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                p_channelTransferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
                p_channelTransferVO.setFirstApprovalRemark(rs.getString("first_approver_remarks"));
                p_channelTransferVO.setSecondApprovalRemark(rs.getString("second_approver_remarks"));
                p_channelTransferVO.setThirdApprovalRemark(rs.getString("third_approver_remarks"));
                p_channelTransferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                p_channelTransferVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                p_channelTransferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                p_channelTransferVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                p_channelTransferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                p_channelTransferVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                p_channelTransferVO.setCanceledBy(rs.getString("cancelled_by"));
                p_channelTransferVO.setCanceledOn(rs.getTimestamp("cancelled_on"));
                p_channelTransferVO.setModifiedOn(rs.getTimestamp("modified_on"));
                p_channelTransferVO.setModifiedBy(rs.getString("modified_by"));
                p_channelTransferVO.setStatus(rs.getString("status"));
                p_channelTransferVO.setType(rs.getString("type"));
                p_channelTransferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
                p_channelTransferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                p_channelTransferVO.setFirstApproverLimit(rs.getLong("first_approver_limit"));
                p_channelTransferVO.setSecondApprovalLimit(rs.getLong("second_approver_limit"));
                p_channelTransferVO.setPayableAmount(rs.getLong("payable_amount"));
                p_channelTransferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                p_channelTransferVO.setBatchNum(rs.getString("batch_no"));
                p_channelTransferVO.setBatchDate(rs.getTimestamp("batch_date"));
                p_channelTransferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                p_channelTransferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
                p_channelTransferVO.setPayInstrumentDate(rs.getTimestamp("pmt_inst_date"));
                p_channelTransferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
                p_channelTransferVO.setSenderTxnProfile(rs.getString("sender_txn_profile"));
                p_channelTransferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                p_channelTransferVO.setTotalTax1(rs.getLong("total_tax1"));
                p_channelTransferVO.setTotalTax2(rs.getLong("total_tax2"));
                p_channelTransferVO.setTotalTax3(rs.getLong("total_tax3"));
                p_channelTransferVO.setSource(rs.getString("source"));
                p_channelTransferVO.setReceiverCategoryCode(rs.getString("receiver_category_code"));
                p_channelTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                p_channelTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                p_channelTransferVO.setPaymentInstSource(rs.getString("pmt_inst_source"));
                p_channelTransferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
                p_channelTransferVO.setProductType(rs.getString("product_type"));
                p_channelTransferVO.setDomainCodeDesc(rs.getString("domain_name"));
                p_channelTransferVO.setReceiverCategoryDesc(rs.getString("category_name"));
                p_channelTransferVO.setReceiverGradeCodeDesc(rs.getString("grade_name"));
                p_channelTransferVO.setToUserName(rs.getString("user_name"));
                p_channelTransferVO.setAddress1(rs.getString("address1"));
                p_channelTransferVO.setAddress2(rs.getString("address2"));
                p_channelTransferVO.setCity(rs.getString("city"));
                p_channelTransferVO.setState(rs.getString("state"));
                p_channelTransferVO.setCountry(rs.getString("country"));
                p_channelTransferVO.setCommProfileName(rs.getString("comm_profile_set_name"));
                p_channelTransferVO.setStockUpdated(rs.getString("stock_updated"));
                p_channelTransferVO.setDualCommissionType(rs.getString("dual_comm_type"));

            	if (!tcpOn) {
					if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
						p_channelTransferVO.setReceiverTxnProfileName(rs.getString("profile_name"));
						p_channelTransferVO.setSenderTxnProfileName(rs.getString("sender_txn_profile_name"));
					} else {
						p_channelTransferVO.setSenderTxnProfileName(rs.getString("profile_name"));
						p_channelTransferVO.setReceiverTxnProfileName(rs.getString("sender_txn_profile_name"));
					}

				} else {

					if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
						p_channelTransferVO.setReceiverTxnProfileName(
								tcpMap.get(rs.getString("receiver_txn_profile")).get("name"));// receiver_txn_profile
						p_channelTransferVO.setSenderTxnProfileName(
								tcpMap.get(rs.getString("sender_txn_profile")).get("name"));// sender_txn_profile

					} else {

						p_channelTransferVO.setReceiverTxnProfileName(
								tcpMap.get(rs.getString("sender_txn_profile_name")).get("name"));// receiver_txn_profile
						p_channelTransferVO.setSenderTxnProfileName(
								tcpMap.get(rs.getString("receiver_txn_profile")).get("name"));// sender_txn_profile

					}
				}
            
            	
                p_channelTransferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
                p_channelTransferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
                p_channelTransferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
                p_channelTransferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
                p_channelTransferVO.setTransferInitatedByName(rs.getString("initatedby"));
                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
                    p_channelTransferVO.setUserMsisdn(rs.getString("from_msisdn"));
                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
                    p_channelTransferVO.setUserMsisdn(rs.getString("to_msisdn"));
                } else {
                    p_channelTransferVO.setUserMsisdn(rs.getString("msisdn"));
                }
                p_channelTransferVO.setErpNum(rs.getString("external_code"));
                p_channelTransferVO.setFromUserName(rs.getString("fromusername"));
                p_channelTransferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getTransferDate())));
                p_channelTransferVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                p_channelTransferVO.setTransferType(rs.getString("transfer_type"));
                p_channelTransferVO.setTransferSubType(rs.getString("transfer_sub_type"));

                p_channelTransferVO.setTransferCategory(rs.getString("transfer_category"));
                p_channelTransferVO.setDefaultLang(rs.getString("sms_default_lang"));
                p_channelTransferVO.setSecondLang(rs.getString("sms_second_lang"));
                p_channelTransferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
                p_channelTransferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
                p_channelTransferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
                p_channelTransferVO.setWalletType(rs.getString("txn_wallet"));
                p_channelTransferVO.setControlTransfer(rs.getString("control_transfer"));

                p_channelTransferVO.setReceiverGgraphicalDomainCode(rs.getString("to_grph_domain_code"));
                p_channelTransferVO.setReceiverDomainCode(rs.getString("to_domain_code"));
                p_channelTransferVO.setFromUserCode(rs.getString("msisdn"));
                p_channelTransferVO.setToUserCode(rs.getString("to_msisdn"));
                p_channelTransferVO.setCommQty(rs.getLong("commision_quantity"));
                p_channelTransferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                p_channelTransferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                p_channelTransferVO.setCreatedBy(rs.getString("created_by"));
                p_channelTransferVO.setCreatedOn(rs.getDate("created_on"));
                p_channelTransferVO.setActiveUserId(rs.getString("active_user_id"));
                p_channelTransferVO.setPayInstrumentType(rs.getString("PMT_INST_TYPE"));
                p_channelTransferVO.setUserWalletCode(rs.getString("user_wallet"));
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "p_channelTransferVO ::: " + p_channelTransferVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : " );
        	loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersVO]", "", "",
                "",  loggerValue.toString());
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
                _log.debug(methodName, "Exiting:  ");
            }
        }
    }
    
    
    public ArrayList loadChannelTransfersList(Connection p_con, String p_networkCode, String p_networkCodeFor, String p_transferCategory, String p_fromDate, String p_toDate) throws BTSLBaseException {
        final String methodName = "loadChannelTransfersList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_roamNetworkCode ");
        	loggerValue.append(p_networkCodeFor);
        	loggerValue.append(", p_transferCategory =" );
        	loggerValue.append(p_transferCategory);
        	loggerValue.append(" p_fromDate ");
        	loggerValue.append(p_fromDate);
        	loggerValue.append(", p_toDate =" );
        	loggerValue.append(p_toDate);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT u.user_name,ct.transfer_id, ct.transfer_date, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date, ct.dual_comm_type, ");
        strBuff.append(" ct.status , ct.transfer_mrp, ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn ");
        strBuff.append(" FROM channel_transfers ct , channel_transfers_items cti ,users u ");
        strBuff.append(" WHERE ct.type='O2C' AND ct.transfer_category=? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");
        strBuff.append(" AND ct.pmt_inst_type = ? ");
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" AND ct.status = ? AND ct.transfer_date >= ? AND ct.transfer_date <= ? AND ct.modified_on < TO_DATE( ? , ? )");
        strBuff.append(" ORDER BY ct.created_on DESC ");
        
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff.toString());
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList arrayList = new ArrayList();
        
        try {
        	String dateTimeFormat = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT);
        	int o2cambMinutesDelay = (int)PreferenceCache.getSystemPreferenceValue(PreferenceI.O2CAMB_MINUTES_DELAY);
        	pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            ++m;
            pstmt.setString(m, p_transferCategory);
            ++m;
            pstmt.setString(m, p_networkCode);
            ++m;
            pstmt.setString(m, p_networkCodeFor);
            ++m;
            pstmt.setString(m, PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE);
            ++m;
            pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
            ++m;
            pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_fromDate)));
            ++m;
            pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_toDate)));
            ++m;
            pstmt.setString(m, BTSLDateUtil.getLocaleDateTimeFromDate(BTSLUtil.addMinuteOnDaysInUtilMinute(new Date(), o2cambMinutesDelay)));
            ++m;
            pstmt.setString(m, dateTimeFormat);
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            int i = 0;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setType(rs.getString("type"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.TRANSFER_STATUS, transferVO.getStatus())).getLookupName());
                }
                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
                transferVO.setNetworkCode(p_networkCode);
                transferVO.setNetworkCodeFor(p_networkCodeFor);
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                transferVO.setCommQty(rs.getLong("commision_quantity"));
                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                transferVO.setCommProfileSetId(rs.getString("COMMISSION_PROFILE_SET_ID"));
                transferVO.setCommProfileVersion(rs.getString("COMMISSION_PROFILE_VER"));
                transferVO.setUserWalletCode(rs.getString("user_wallet"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setTransferInitatedByName(rs.getString("user_name"));
                transferVO.setToMSISDN(rs.getString("to_msisdn"));
                if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(rs.getString("transfer_sub_type")))
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.VOUCHER_PRODUCT_O2C);
                	
                }
                else
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.REDEMP_TYPE_STOCK);
                }
                transferVO.setIndex(i);
                ++i;
                arrayList.add(transferVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "",  loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(arrayList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return arrayList;
    }
    /**This method Load the last N number of c2c, c2s and o2c transaction details.
     * @author vikram.kumar 
     * @param p_con Connection
     * @param p_user_id String
     * @param p_noLastTxn int	//no of last transactions to be fetched.
     * @param serviceType		// C2C ,O2C & C2S transaction details.
     * @param noDays			//fetch only data for this period  if null/0 then no check on the date.
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadLastXTransfersForReceiver(Connection p_con, String p_user_id,int p_noLastTxn, String serviceType, int noDays) throws BTSLBaseException
    {
    	final String methodName = "loadLastXTransfersForReceiver";
       if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered  p_user_id: " + p_user_id+", p_noLastTxn: "+p_noLastTxn +", serviceType: " +serviceType + "noDays: "+ noDays);
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs=null;
        C2STransferVO transferVO=null;
        ArrayList transfersList=null;
        StringBuffer strBuff =new StringBuffer();
        int i;
        try
        {
          	transfersList=new ArrayList();
          	String []services = serviceType.split(",");
          	Date differenceDate=BTSLUtil.getDifferenceDate(new Date(), -noDays);	//for getting diffence date.
          	for(int j=0; j<services.length; j++)
        	{
    		    if(services[j].contains(PretupsI.SERVICE_TYPE_C2C_LAST_X_TRANSFER))
    		    {
    		    	strBuff.setLength(0);
    		    	String aa[]=services[j].split(":");
    		    	strBuff.append(" SELECT transfer_id,MSISDN,status, net_payable_amount,TRANSFER_MRP,REQUESTED_QUANTITY,PRODUCT_NAME,CLOSE_DATE FROM ( ");
    		        strBuff.append(" SELECT CT.transfer_id, U.MSISDN, CT.status , CT.net_payable_amount,CT.TRANSFER_MRP,CT.REQUESTED_QUANTITY,P.PRODUCT_NAME,CT.CLOSE_DATE  FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI,PRODUCTS P,USER_PHONES U WHERE CT.TRANSFER_ID = CTI.TRANSFER_ID and CTI.PRODUCT_CODE=P.PRODUCT_CODE and CT.TO_USER_ID=? ");
    		        
    		        if(noDays!=0 )
    		        	strBuff.append(" AND CT.transfer_date >= ? ");
    		        if(aa.length==2)
    		        	strBuff.append(" AND CT.transfer_sub_type  = ? ");
    		        
    		        strBuff.append(" and U.USER_ID=CT.FROM_USER_ID AND CT.type = ? and CT.STATUS=?  ");
    		        strBuff.append(" ORDER BY CT.created_on desc)  WHERE  rowNum<=? ");
    		        String sqlSelect1 = strBuff.toString();
    		        if (_log.isDebugEnabled())
    		            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect1);
    		        i=0;
    		        pstmt1 = p_con.prepareStatement(sqlSelect1);
    		        pstmt1.setString(++i,p_user_id);
    		        if(noDays!=0 )
    		        	pstmt1.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(differenceDate));
    		        if(aa.length==2)
    		        	pstmt1.setString(++i,aa[1]);
    	
    		        pstmt1.setString(++i,PretupsI.TRANSFER_TYPE_C2C);
    		        pstmt1.setString(++i,PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
    		        pstmt1.setInt(++i,p_noLastTxn);
    		        rs = pstmt1.executeQuery();
    		        while(rs.next())
    		        {
    		            transferVO = new C2STransferVO();
    		            transferVO.setTransferID(rs.getString("transfer_id"));
    		            transferVO.setSenderMsisdn(rs.getString("MSISDN"));
    		            transferVO.setTransferStatus(rs.getString("status"));
    		            transferVO.setType(PretupsI.C2C_MODULE);
    		            transferVO.setTransferValue(rs.getLong("net_payable_amount"));
    		            transferVO.setQuantity(rs.getLong("REQUESTED_QUANTITY"));
    		            transferVO.setProductName(rs.getString("PRODUCT_NAME"));
    		            transferVO.setTransferDate(rs.getTimestamp("CLOSE_DATE"));
    		            transfersList.add(transferVO);
    		        }
    		    }
    		    
    		    	
        	}
       }
       catch (SQLException sqe)
       {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadLastXTransfersForReceiver]", "", "", "", "SQL Exception:"
                    + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        }
        catch (Exception ex)
        {
            _log.error("loadLastXTransfers_new", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadLastXTransfersForReceiver]", "", "", "", "Exception:"
                    + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }
        finally
        {
            try{if (rs != null){rs.close();}}
            catch (Exception e){
            	_log.error(methodName, "Exception:e=" + e);
                _log.errorTrace(methodName, e);
            }
    		try{if (pstmt != null){pstmt.close();}}
    		catch (Exception e){
    			_log.error(methodName, "Exception:e=" + e);
                _log.errorTrace(methodName, e);
    		}
    		try{if (pstmt1 != null){pstmt1.close();}}
    		catch (Exception e){
    			_log.error(methodName, "Exception:e=" + e);
                _log.errorTrace(methodName, e);
    		}
    		try{if (pstmt2 != null){pstmt2.close();}}
    		catch (Exception e){
    			_log.error(methodName, "Exception:e=" + e);
                _log.errorTrace(methodName, e);
    		}
    		if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:  transfersList =" + transfersList.size());
        }
        return transfersList;
    }

    public ArrayList<VomsBatchVO> loadVoucherDetailsForTransactionId(Connection p_con, String p_transferID,String status) throws BTSLBaseException {

        final String methodName = "loadVoucherDetailsForTransactionId";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  TransferNumber: ");
        	loggerValue.append(p_transferID);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff=null;
        if(status.equals("CLOSE"))
        {	
        strBuff = channelTransferQry.loadVoucherDetailsForTransactionIdQry();}
        else
        {
        	
         strBuff = channelTransferQry.loadVoucherDetailsForTransactionIdChannelQry();
        }
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList<VomsBatchVO> enquiryItemsList = new ArrayList<VomsBatchVO>();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            if (!BTSLUtil.isNullString(p_transferID)) {
                ++m;
                pstmt.setString(m, p_transferID);
            }
            rs = pstmt.executeQuery();
            
            VomsBatchVO vomsBatchVO = null;
            if(status.equals("CLOSE"))
            {	
            while (rs.next()) {
                vomsBatchVO = new VomsBatchVO();
                vomsBatchVO.setBatchNo(rs.getString("batch_no"));
                vomsBatchVO.setProductName(rs.getString("product_name"));
                vomsBatchVO.setBatchType(rs.getString("batch_type"));
                vomsBatchVO.setFromSerialNo(rs.getString("from_serial_no"));
                vomsBatchVO.setToSerialNo(rs.getString("to_serial_no"));
                vomsBatchVO.setTotalVoucherPerOrder(rs.getLong("total_no_of_vouchers"));
                vomsBatchVO.setVouchersegment(rs.getString("VOUCHER_SEGMENT"));
                vomsBatchVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
                vomsBatchVO.setDenomination(rs.getString("mrp"));
            	
                enquiryItemsList.add(vomsBatchVO);
            }}
            else
            {
            	 while (rs.next()) {
                     vomsBatchVO = new VomsBatchVO();
                     vomsBatchVO.setFromSerialNo(rs.getString("from_serial_no"));
                     vomsBatchVO.setToSerialNo(rs.getString("to_serial_no"));
                     vomsBatchVO.setTotalVoucherPerOrder(rs.getLong("requested_quantity"));
                 	
                     enquiryItemsList.add(vomsBatchVO);
                 }
            	
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception:");
             loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
             loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(ex.getMessage());;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(enquiryItemsList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return enquiryItemsList;
    }
    
    /**
     * 
     * @param p_con
     * @param p_searchParam
     * @param p_approvalLevel
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_domainCode
     * @param p_geoCode
     * @param p_loginUserID
     * @param p_transferCategory
     * @param p_reveiverCategoryCode
     * @param p_channelOwnerID
     * @return
     * @throws BTSLBaseException
     */
    
    
    public File getChannelTransferFile(Connection p_con, String p_transferId) throws BTSLBaseException
    {
		final String METHOD_NAME = "getChannelTransferFile";
		if (_log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append(" Entered p_transferId=");
        	msg.append(p_transferId);
        	String message=msg.toString();
			_log.debug(METHOD_NAME, message);
		}
		StringBuilder sb = new StringBuilder(1024);
		PreparedStatement psmt = null;
		ResultSet rs = null;
        String fileName = null;
        File file = null;
        BufferedInputStream is = null;
        FileOutputStream fos = null;
        String strBuff = "";
        String p_filePath = Constants.getProperty("DownloadApprovedRestrictedMSISDNFilePath");
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        
        if(QueryConstants.DB_ORACLE.equals(dbConnected)){
        	 strBuff = "SELECT approval_doc as SIGNEDFORM,approval_doc_file_path as FILEPATH from channel_transfers where transfer_id = ?";	
        	 }
        else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
		strBuff = "SELECT \"approval_doc\" as SIGNEDFORM,approval_doc_file_path as FILEPATH from channel_transfers where transfer_id = ?";	
		}
        
		
		if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Download Signed form Query: ", strBuff);
        }
		try {
			psmt = p_con.prepareStatement(strBuff);
			psmt.setString(1, p_transferId);
			rs = psmt.executeQuery();
			while(rs.next()){
				
				if(QueryConstants.DB_ORACLE.equals(dbConnected))
				{
					if (rs.getBlob("SIGNEDFORM") != null) {
						Blob blob = rs.getBlob("SIGNEDFORM");
						String serverFilePath = SqlParameterEncoder.encodeParams(rs.getString("FILEPATH"));
						if (null != serverFilePath) {
							String[] tokens = serverFilePath.split("/");
							fileName = tokens[tokens.length - 1];
						}
						file = new File(p_filePath + fileName);
						is = new BufferedInputStream(blob.getBinaryStream());
						fos = new FileOutputStream(file);
						byte[] buffer = new byte[2048];
						int r = 0;
						while ((r = is.read(buffer)) != -1) {
							fos.write(buffer, 0, r);
						}
						fos.flush();
						fos.close();
						is.close();
					}
				}
				else
				{
					if(rs.getBytes("SIGNEDFORM") != null) 
					{
						byte[] bytes = rs.getBytes("SIGNEDFORM");
						Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
						
						String serverFilePath = SqlParameterEncoder.encodeParams(rs.getString("FILEPATH"));
						if (null != serverFilePath) {
							String[] tokens = serverFilePath.split("/");
							fileName = tokens[tokens.length - 1];
						}
						sb.setLength(0);
						file = new File(sb.append(p_filePath).append(fileName).toString());
						is = new BufferedInputStream(blob.getBinaryStream());
						fos = new FileOutputStream(file);
						byte[] buffer = new byte[2048];
						int r = 0;
						while((r = is.read(buffer))!=-1) {
							fos.write(buffer, 0, r);
						}
						fos.flush();
						fos.close();
						is.close();
					}
				}

			}
			return file;
		} catch (SQLException sqle) {
			_log.error("getChannelTransferFile", "SQLException " + sqle.getMessage());
			_log.errorTrace(METHOD_NAME, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadBatchListOnStatus]", "", "", "", "SQLException:" + sqle.getMessage());
			throw new BTSLBaseException(this, "getChannelTransferFile", "error.general.sql.processing");
		}
		catch (Exception e) {
			_log.error("getChannelTransferFile", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherDAO[loadBatchListOnStatus]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "getChannelTransferFile", "error.general.processing");
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
				}
			}catch (IOException ex) {
				_log.error(METHOD_NAME, " ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (is != null) {
					is.close();
				}
			}catch (IOException ex) {
				_log.error(METHOD_NAME, " ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(METHOD_NAME, " ::  Exception Closing RS : " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (Exception ex) {
				_log.error( METHOD_NAME, " ::  Exception Closing Prepared Stmt: " + ex.getMessage());
				_log.errorTrace(METHOD_NAME, ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME," :: Exiting : batchList size = " + fileName);
			}
		}
	}
    
    public ArrayList loadChannelC2CStockTransfersList(Connection p_con, String p_searchParam, String p_approvalLevel, String p_networkCode, String p_networkCodeFor, String p_domainCode, String p_geoCode, String p_loginUserID, String p_transferCategory, String p_reveiverCategoryCode, String p_channelOwnerID,String pageNumber,String entriesPerPage,String userNameSearch) throws BTSLBaseException {
        
    	final String methodName = "loadChannelC2CStockTransfersList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_searchParam ");
        	
        	loggerValue.append(p_searchParam);
        	loggerValue.append(" p_approvalLevel: ");
        	loggerValue.append(p_approvalLevel);
        	loggerValue.append(" p_networkCode ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_roamNetworkCode ");
        	loggerValue.append(p_networkCodeFor);
        	loggerValue.append(" p_domainCode " );
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_geoCode ");
        	loggerValue.append(p_geoCode);
        	loggerValue.append(",p_loginUserID= " );
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(", p_transferCategory =" );
        	loggerValue.append(p_transferCategory);
        	loggerValue.append(",p_reveiverCategoryCode=");
        	loggerValue.append(p_reveiverCategoryCode);
        	loggerValue.append(", p_channelOwnerID=");
        	loggerValue.append(p_channelOwnerID);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect;
        if(!BTSLUtil.isNullString(pageNumber) && !BTSLUtil.isNullString(entriesPerPage)) {
        	//with pagination wild search is handled(if present or not)
            sqlSelect= channelTransferQry.loadChannelToChannelStockTransfersListQryPagination(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel,pageNumber,entriesPerPage,userNameSearch);

        }
        else {
        	 if(!BTSLUtil.isNullString(userNameSearch)){
                 sqlSelect= channelTransferQry.loadChannelToChannelStockTransfersListQryWildCard(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel,userNameSearch);
 
        	 }else {
                 
        		 sqlSelect= channelTransferQry.loadChannelToChannelStockTransfersListQry(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel);

        	 }
        	

        }
        
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList<ChannelTransferVO> channelTransferVOList = new ArrayList<ChannelTransferVO>();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_loginUserID);
            ++m;
            pstmt.setString(m, p_networkCode);
            ++m;
            pstmt.setString(m, p_networkCodeFor);
            if (!BTSLUtil.isNullString(p_reveiverCategoryCode) && !p_reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_reveiverCategoryCode);
            }
            ++m;
            pstmt.setString(m, p_loginUserID);
            if (!BTSLUtil.isNullString(p_geoCode)) {
                ++m;
                pstmt.setString(m, p_geoCode);
                ++m;
                pstmt.setString(m, PretupsI.ALL);
                ++m;
                pstmt.setString(m, p_geoCode);
            }
            if (!p_domainCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_domainCode);
            }
            if (!PretupsI.ALL.equals(p_searchParam)) {
                ++m;
                pstmt.setString(m, p_searchParam);
            }
            
            if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
               
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            }
            
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            if(!BTSLUtil.isNullString(pageNumber) && !BTSLUtil.isNullString(entriesPerPage)) {
            	int fromPage=Integer.parseInt(pageNumber);
				int entriesPerPage1=Integer.parseInt(entriesPerPage);
        		int offSet=((fromPage*entriesPerPage1)-entriesPerPage1);
    			int fetch=entriesPerPage1;
    			String offSetStr= Integer.toString(offSet);
    			String fetchStr=Integer.toString(fetch);
    		
    			++m;
                pstmt.setInt(m, offSet); 
                ++m;
                pstmt.setInt(m, fetch); 
    			

            }
            rs = pstmt.executeQuery();
            
            ChannelTransferVO transferVO = null;
            int i = 0;
            while (rs.next()) {
            	
            	String transferID = rs.getString("transfer_id");
            	boolean isTransactionExist = false;
            	
            	
            	for(ChannelTransferVO transferVOList: channelTransferVOList)
            	{
            		if(transferVOList.getTransferID().equalsIgnoreCase(transferID))
            		{
            			isTransactionExist = true;
            			break;
            		}
            	}
            
            	/*File file = null;
            	String fileName = null;
            	InputStream is;
            	OutputStream fos;
            	String filePath = "C:\\DownloadFile\\";*/

            	if(isTransactionExist == false)
            	{
            		String serverFilePath = rs.getString("approval_doc_file_path");
            		
    				/*Blob blob = rs.getBlob("APPROVALDOC");
    				String serverFilePath = rs.getString("approval_doc_file_path");
    				if(serverFilePath != null){
    					String[] tokens = serverFilePath.split("/");
    					fileName = tokens[tokens.length-1];
    				
    				file = new File(filePath + fileName);
    				is = new BufferedInputStream(blob.getBinaryStream());
    				fos = new FileOutputStream(file);
    				byte[] buffer = new byte[2048];
    				int r = 0;
    				while((r = is.read(buffer))!=-1) {
    					fos.write(buffer, 0, r);
    				}
    				fos.flush();
    				fos.close();
    				is.close();
    				}*/
            		
	                transferVO = new ChannelTransferVO();
	                if(serverFilePath != null)
	                {
	                	transferVO.setIsFileUploaded(true);
	                }
	                transferVO.setTransferID(rs.getString("transfer_id"));
	                transferVO.setTransferDate(rs.getDate("transfer_date"));
	                transferVO.setType(rs.getString("type"));
	                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
	                transferVO.setPayableAmount(rs.getLong("payable_amount"));
	                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
	                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
	                transferVO.setDomainCodeDesc(rs.getString("domain_name"));
	                transferVO.setReceiverCategoryDesc(rs.getString("category_name"));
	                transferVO.setStatus(rs.getString("status"));
	                transferVO.setTransferInitatedByName(rs.getString("user_name"));
	                transferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
	                transferVO.setToUserID(rs.getString("to_user_id"));
	                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
	                transferVO.setReferenceNum(rs.getString("reference_no"));
	                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
	                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
	                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
	                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.TRANSFER_STATUS, transferVO.getStatus())).getLookupName());
	                }
	                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
	                transferVO.setNetworkCode(p_networkCode);
	                transferVO.setNetworkCodeFor(p_networkCodeFor);
	                // added as per mobinil cr 1
	                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
	                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
	                // ends here
	                // for mali-- +ve commision apply
	                if(BTSLUtil.isNullString(pageNumber) && BTSLUtil.isNullString(entriesPerPage)) {
	                	transferVO.setCommQty(rs.getLong("commision_quantity"));
		                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
		                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
		                
	                }
	                transferVO.setProductType(rs.getString("product_type"));
	                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
	                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
	                transferVO.setCommProfileSetId(rs.getString("COMMISSION_PROFILE_SET_ID"));
	                transferVO.setCommProfileVersion(rs.getString("COMMISSION_PROFILE_VER"));
	                /** START: Birendra 27JAN2015 */
	                if(BTSLUtil.isNullString(pageNumber) && BTSLUtil.isNullString(entriesPerPage)) {
	                	transferVO.setUserWalletCode(rs.getString("user_wallet"));
	                }
	                
	                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
	                transferVO.setToMSISDN(rs.getString("to_msisdn"));
	                transferVO.setFirstApprovedByName(new UserDAO().loadUserName(p_con, rs.getString("first_approved_by")));
	                transferVO.setSecondApprovedByName(new UserDAO().loadUserName(p_con, rs.getString("second_approved_by")));
	                if(transferVO.getTransferInitatedBy().equals(transferVO.getToUserID())) {
	                	 transferVO.setTransferType("Buy");
	                }else {
	                	 transferVO.setTransferType(rs.getString("transfer_type"));
	                }
	                if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(rs.getString("transfer_sub_type")))
	                	
	                {
	                	transferVO.setTransferSubTypeAsString(PretupsI.VOUCHER_PRODUCT_O2C);
	                	
	                }
	                else
	                {
	                	transferVO.setTransferSubTypeAsString(PretupsI.REDEMP_TYPE_STOCK);
	                }
	                /** STOP: Birendra 27JAN2015 */
	
	                transferVO.setIndex(i);
	                ++i;
	                channelTransferVOList.add(transferVO);
            	}
            }
            

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "",  loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(channelTransferVOList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return channelTransferVOList;
    }
    
    /**
     * 
     * @param p_con
     * @param transactionId
     * @param p_searchParam
     * @param p_approvalLevel
     * @param p_networkCode
     * @param p_networkCodeFor
     * @param p_domainCode
     * @param p_geoCode
     * @param p_loginUserID
     * @param p_transferCategory
     * @param p_reveiverCategoryCode
     * @param p_channelOwnerID
     * @param pageNumber
     * @param entriesPerPage
     * @param userNameSearch
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList<ChannelTransferVO> loadChannelC2CStockTransfersListTransactionId(Connection p_con, String transactionId, String p_searchParam, String p_approvalLevel, String p_networkCode, String p_networkCodeFor, String p_domainCode, String p_geoCode, String p_loginUserID, String p_transferCategory, String p_reveiverCategoryCode, String p_channelOwnerID,String pageNumber,String entriesPerPage,String userNameSearch) throws BTSLBaseException {
        
    	final String methodName = "loadChannelC2CStockTransfersListTransactionId";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_searchParam ");
        	
        	loggerValue.append(p_searchParam);
        	loggerValue.append(" p_approvalLevel: ");
        	loggerValue.append(p_approvalLevel);
        	loggerValue.append(" p_networkCode ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_roamNetworkCode ");
        	loggerValue.append(p_networkCodeFor);
        	loggerValue.append(" p_domainCode " );
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_geoCode ");
        	loggerValue.append(p_geoCode);
        	loggerValue.append(",p_loginUserID= " );
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(", p_transferCategory =" );
        	loggerValue.append(p_transferCategory);
        	loggerValue.append(",p_reveiverCategoryCode=");
        	loggerValue.append(p_reveiverCategoryCode);
        	loggerValue.append(", p_channelOwnerID=");
        	loggerValue.append(p_channelOwnerID);
        	loggerValue.append(", transactionId=");
        	loggerValue.append(transactionId);
        	
        	
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect;

        	sqlSelect= channelTransferQry.loadChannelToChannelStockTransfersListTransferIdQry(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel);

        	
        
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList<ChannelTransferVO> channelTransferVOList = new ArrayList<ChannelTransferVO>();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_loginUserID);
            ++m;
            pstmt.setString(m, p_networkCode);
            ++m;
            pstmt.setString(m, p_networkCodeFor);
            if (!BTSLUtil.isNullString(p_reveiverCategoryCode) && !p_reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_reveiverCategoryCode);
            }
            ++m;
            pstmt.setString(m, p_loginUserID);
            if (!BTSLUtil.isNullString(p_geoCode)) {
                ++m;
                pstmt.setString(m, p_geoCode);
                ++m;
                pstmt.setString(m, PretupsI.ALL);
                ++m;
                pstmt.setString(m, p_geoCode);
            }
            if (!p_domainCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_domainCode);
            }
            if (!PretupsI.ALL.equals(p_searchParam)) {
                ++m;
                pstmt.setString(m, p_searchParam);
            }
            
            if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
               
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            }
            
            ++m;
            pstmt.setString(m, transactionId);
            
            
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            ++m;
            pstmt.setString(m, p_channelOwnerID);
/*            if(!BTSLUtil.isNullString(pageNumber) && !BTSLUtil.isNullString(entriesPerPage)) {
            	int fromPage=Integer.parseInt(pageNumber);
				int entriesPerPage1=Integer.parseInt(entriesPerPage);
        		int offSet=((fromPage*entriesPerPage1)-entriesPerPage1);
    			int fetch=entriesPerPage1;
    			String offSetStr= Integer.toString(offSet);
    			String fetchStr=Integer.toString(fetch);
    		
    			++m;
                pstmt.setString(m, offSetStr); 
                ++m;
                pstmt.setString(m, fetchStr); 
    			

            }*/
            rs = pstmt.executeQuery();
            UserDAO userDAO = new UserDAO();
            ChannelTransferVO transferVO = null;
            int i = 0;
            while (rs.next()) {
            	
            	String transferID = rs.getString("transfer_id");
            	boolean isTransactionExist = false;
            	
            	
            	for(ChannelTransferVO transferVOList: channelTransferVOList)
            	{
            		if(transferVOList.getTransferID().equalsIgnoreCase(transferID))
            		{
            			isTransactionExist = true;
            			break;
            		}
            	}
            
            	/*File file = null;
            	String fileName = null;
            	InputStream is;
            	OutputStream fos;
            	String filePath = "C:\\DownloadFile\\";*/

            	if(isTransactionExist == false)
            	{
            		String serverFilePath = rs.getString("approval_doc_file_path");
            		
    				/*Blob blob = rs.getBlob("APPROVALDOC");
    				String serverFilePath = rs.getString("approval_doc_file_path");
    				if(serverFilePath != null){
    					String[] tokens = serverFilePath.split("/");
    					fileName = tokens[tokens.length-1];
    				
    				file = new File(filePath + fileName);
    				is = new BufferedInputStream(blob.getBinaryStream());
    				fos = new FileOutputStream(file);
    				byte[] buffer = new byte[2048];
    				int r = 0;
    				while((r = is.read(buffer))!=-1) {
    					fos.write(buffer, 0, r);
    				}
    				fos.flush();
    				fos.close();
    				is.close();
    				}*/
            		
	                transferVO = new ChannelTransferVO();
	                if(serverFilePath != null)
	                {
	                	transferVO.setIsFileUploaded(true);
	                }
	                transferVO.setTransferID(rs.getString("transfer_id"));
	                transferVO.setTransferDate(rs.getDate("transfer_date"));
	                transferVO.setType(rs.getString("type"));
	                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
	                transferVO.setPayableAmount(rs.getLong("payable_amount"));
	                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
	                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
	                transferVO.setDomainCodeDesc(rs.getString("domain_name"));
	                transferVO.setReceiverCategoryDesc(rs.getString("category_name"));
	                transferVO.setStatus(rs.getString("status"));
	                transferVO.setTransferInitatedByName(rs.getString("user_name"));
	                transferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
	                transferVO.setToUserID(rs.getString("to_user_id"));
	                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
	                transferVO.setReferenceNum(rs.getString("reference_no"));
	                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
	                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
	                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
	                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.TRANSFER_STATUS, transferVO.getStatus())).getLookupName());
	                }
	                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
	                transferVO.setNetworkCode(p_networkCode);
	                transferVO.setNetworkCodeFor(p_networkCodeFor);
	                // added as per mobinil cr 1
	                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
	                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
	                // ends here
	                // for mali-- +ve commision apply
	                if(BTSLUtil.isNullString(pageNumber) && BTSLUtil.isNullString(entriesPerPage)) {
	                	transferVO.setCommQty(rs.getLong("commision_quantity"));
		                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
		                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
		                
	                }
	                transferVO.setProductType(rs.getString("product_type"));
	                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
	                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
	                transferVO.setCommProfileSetId(rs.getString("COMMISSION_PROFILE_SET_ID"));
	                transferVO.setCommProfileVersion(rs.getString("COMMISSION_PROFILE_VER"));
	                /** START: Birendra 27JAN2015 */
	                if(BTSLUtil.isNullString(pageNumber) && BTSLUtil.isNullString(entriesPerPage)) {
	                	transferVO.setUserWalletCode(rs.getString("user_wallet"));
	                }
	                
	                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
	                transferVO.setToMSISDN(rs.getString("to_msisdn"));
	                transferVO.setFirstApprovedByName(userDAO.loadUserName(p_con, rs.getString("first_approved_by")));
	                transferVO.setSecondApprovedByName(userDAO.loadUserName(p_con, rs.getString("second_approved_by")));
	                if(transferVO.getTransferInitatedBy().equals(transferVO.getToUserID())) {
	                	 transferVO.setTransferType("Buy");
	                }else {
	                	 transferVO.setTransferType(rs.getString("transfer_type"));
	                }
	                if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(rs.getString("transfer_sub_type")))
	                	
	                {
	                	transferVO.setTransferSubTypeAsString(PretupsI.VOUCHER_PRODUCT_O2C);
	                	
	                }
	                else
	                {
	                	transferVO.setTransferSubTypeAsString(PretupsI.REDEMP_TYPE_STOCK);
	                }
	                /** STOP: Birendra 27JAN2015 */
	
	                transferVO.setIndex(i);
	                ++i;
	                channelTransferVOList.add(transferVO);
            	}
            }
            

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "",  loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(channelTransferVOList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return channelTransferVOList;
    }
    
    public ArrayList loadChannelC2CVoucherTransfersList(Connection p_con, String p_searchParam, String p_approvalLevel, String p_networkCode, String p_networkCodeFor, String p_domainCode, String p_geoCode, String p_loginUserID, String p_transferCategory, String p_reveiverCategoryCode, String p_channelOwnerID,String pageNumber,String entriesPerPage,String userNameSearch) throws BTSLBaseException {
        final String methodName = "loadChannelC2CVoucherTransfersList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_searchParam ");
        	loggerValue.append(p_searchParam);
        	loggerValue.append(" p_approvalLevel: ");
        	loggerValue.append(p_approvalLevel);
        	loggerValue.append(" p_networkCode ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_roamNetworkCode ");
        	loggerValue.append(p_networkCodeFor);
        	loggerValue.append(" p_domainCode " );
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_geoCode ");
        	loggerValue.append(p_geoCode);
        	loggerValue.append(",p_loginUserID= " );
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(", p_transferCategory =" );
        	loggerValue.append(p_transferCategory);
        	loggerValue.append(",p_reveiverCategoryCode=");
        	loggerValue.append(p_reveiverCategoryCode);
        	loggerValue.append(", p_channelOwnerID=");
        	loggerValue.append(p_channelOwnerID);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect;
       
        
        if(!BTSLUtil.isNullString(pageNumber) && !BTSLUtil.isNullString(entriesPerPage)) {
        	//with pagination wild search is handled(if present or not)
        	 sqlSelect= channelTransferQry.loadChannelC2CVoucherTransfersListQryPagination(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel,pageNumber,entriesPerPage,userNameSearch);

        }
        else {
        	 if(!BTSLUtil.isNullString(userNameSearch)){
                 sqlSelect= channelTransferQry.loadChannelToChannelVoucherTransfersListQryWildCard(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel,userNameSearch);
 
        	 }else {
                 
                 sqlSelect = channelTransferQry.loadChannelC2CVoucherTransfersListQry(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel);

        	 }
        	

        }
        
        
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_loginUserID);
            ++m;
            pstmt.setString(m, p_networkCode);
            ++m;
            pstmt.setString(m, p_networkCodeFor);
            if (!BTSLUtil.isNullString(p_reveiverCategoryCode) && !p_reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_reveiverCategoryCode);
            }
            ++m;
            pstmt.setString(m, p_loginUserID);
            if (!BTSLUtil.isNullString(p_geoCode)) {
                ++m;
                pstmt.setString(m, p_geoCode);
                ++m;
                pstmt.setString(m, PretupsI.ALL);
                ++m;
                pstmt.setString(m, p_geoCode);
            }
            if (!p_domainCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_domainCode);
            }
            if (!PretupsI.ALL.equals(p_searchParam)) {
                ++m;
                pstmt.setString(m, p_searchParam);
            }
            
    
            if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
               
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            }
            
            
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            
            if(!BTSLUtil.isNullString(pageNumber) && !BTSLUtil.isNullString(entriesPerPage)) {
            	int fromPage=Integer.parseInt(pageNumber);
				int entriesPerPage1=Integer.parseInt(entriesPerPage);
        		int offSet=((fromPage*entriesPerPage1)-entriesPerPage1);
    			int fetch=entriesPerPage1;
    			String offSetStr= Integer.toString(offSet);
    			String fetchStr=Integer.toString(fetch);
    		
    			++m;
    			 pstmt.setInt(m, offSet); 
                ++m;
                pstmt.setInt(m, fetch); 
    			

            }
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            int i = 0;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                
                
				String serverFilePath = rs.getString("approval_doc_file_path");

				if (serverFilePath != null) {
					transferVO.setIsFileUploaded(true);
				}

                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                transferVO.setType(rs.getString("type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
                transferVO.setDomainCodeDesc(rs.getString("domain_name"));
                transferVO.setReceiverCategoryDesc(rs.getString("category_name"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setTransferInitatedByName(rs.getString("user_name"));
                transferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_approvalLevel)) {
                	transferVO.setFirstApprovedBy(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedByName(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                	transferVO.setFirstApprovedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("first_approved_on"))));
                } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_approvalLevel)) {
                	transferVO.setFirstApprovedBy(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedByName(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                	transferVO.setFirstApprovedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("first_approved_on"))));
                	transferVO.setSecondApprovedBy(rs.getString("second_app_name"));
                	transferVO.setSecondApprovedByName(rs.getString("second_app_name"));
                    transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                    transferVO.setSecondApprovedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("second_approved_on"))));
                }
                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.TRANSFER_STATUS, transferVO.getStatus())).getLookupName());
                }
                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
                transferVO.setNetworkCode(p_networkCode);
                transferVO.setNetworkCodeFor(p_networkCodeFor);
                // added as per mobinil cr 1
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                // ends here
                // for mali-- +ve commision apply
                transferVO.setCommQty(rs.getLong("commision_quantity"));
                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                transferVO.setCommProfileSetId(rs.getString("COMMISSION_PROFILE_SET_ID"));
                transferVO.setCommProfileVersion(rs.getString("COMMISSION_PROFILE_VER"));
                /** START: Birendra 27JAN2015 */
                transferVO.setUserWalletCode(rs.getString("user_wallet"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setToMSISDN(rs.getString("to_msisdn"));
                if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(rs.getString("transfer_sub_type")))
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.VOUCHER_PRODUCT_O2C);
                	
                }
                else
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.REDEMP_TYPE_STOCK);
                }
                if(transferVO.getTransferInitatedBy().equals(transferVO.getToUserID())) {
                	transferVO.setTransferType("Buy");
                }else {
                	transferVO.setTransferType(rs.getString("transfer_type"));
                }
                /** STOP: Birendra 27JAN2015 */

                transferVO.setIndex(i);
                ++i;
                arrayList.add(transferVO);
            }

            
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "",  loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(arrayList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return arrayList;
    }
    
    
    
    
    public ArrayList loadChannelC2CVoucherTransfersListTransactionId(Connection p_con, String transactionId, String p_searchParam, String p_approvalLevel, String p_networkCode, String p_networkCodeFor, String p_domainCode, String p_geoCode, String p_loginUserID, String p_transferCategory, String p_reveiverCategoryCode, String p_channelOwnerID,String pageNumber,String entriesPerPage,String userNameSearch) throws BTSLBaseException {
        final String methodName = "loadChannelC2CVoucherTransfersList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_searchParam ");
        	loggerValue.append(p_searchParam);
        	loggerValue.append(" p_approvalLevel: ");
        	loggerValue.append(p_approvalLevel);
        	loggerValue.append(" p_networkCode ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_roamNetworkCode ");
        	loggerValue.append(p_networkCodeFor);
        	loggerValue.append(" p_domainCode " );
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_geoCode ");
        	loggerValue.append(p_geoCode);
        	loggerValue.append(",p_loginUserID= " );
        	loggerValue.append(p_loginUserID);
        	loggerValue.append(", p_transferCategory =" );
        	loggerValue.append(p_transferCategory);
        	loggerValue.append(",p_reveiverCategoryCode=");
        	loggerValue.append(p_reveiverCategoryCode);
        	loggerValue.append(", p_channelOwnerID=");
        	loggerValue.append(p_channelOwnerID);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect;
       
       
                 
        sqlSelect = channelTransferQry.loadChannelC2CVoucherTransfersListTransactionIdQry(p_reveiverCategoryCode, p_geoCode, p_domainCode, p_searchParam, p_approvalLevel);
        
        
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue );
        }
        final ArrayList arrayList = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_loginUserID);
            ++m;
            pstmt.setString(m, p_networkCode);
            ++m;
            pstmt.setString(m, p_networkCodeFor);
            if (!BTSLUtil.isNullString(p_reveiverCategoryCode) && !p_reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_reveiverCategoryCode);
            }
            ++m;
            pstmt.setString(m, p_loginUserID);
            if (!BTSLUtil.isNullString(p_geoCode)) {
                ++m;
                pstmt.setString(m, p_geoCode);
                ++m;
                pstmt.setString(m, PretupsI.ALL);
                ++m;
                pstmt.setString(m, p_geoCode);
            }
            if (!p_domainCode.equalsIgnoreCase(PretupsI.ALL)) {
                ++m;
                pstmt.setString(m, p_domainCode);
            }
            if (!PretupsI.ALL.equals(p_searchParam)) {
                ++m;
                pstmt.setString(m, p_searchParam);
            }
            
    
            if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
               
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_approvalLevel)) {
                ++m;
                pstmt.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            }
            

            ++m;
            pstmt.setString(m, transactionId);
            
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            ++m;
            pstmt.setString(m, p_channelOwnerID);
            
            if(!BTSLUtil.isNullString(pageNumber) && !BTSLUtil.isNullString(entriesPerPage)) {
            	int fromPage=Integer.parseInt(pageNumber);
				int entriesPerPage1=Integer.parseInt(entriesPerPage);
        		int offSet=((fromPage*entriesPerPage1)-entriesPerPage1);
    			int fetch=entriesPerPage1;
    			String offSetStr= Integer.toString(offSet);
    			String fetchStr=Integer.toString(fetch);
    		
    			++m;
                pstmt.setInt(m, offSet); 
                ++m;
                pstmt.setInt(m, fetch); 
    			

            }
            rs = pstmt.executeQuery();

            ChannelTransferVO transferVO = null;
            int i = 0;
            while (rs.next()) {
                transferVO = new ChannelTransferVO();
                
                
				String serverFilePath = rs.getString("approval_doc_file_path");

				if (serverFilePath != null) {
					transferVO.setIsFileUploaded(true);
				}

                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                transferVO.setType(rs.getString("type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
                transferVO.setDomainCodeDesc(rs.getString("domain_name"));
                transferVO.setReceiverCategoryDesc(rs.getString("category_name"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setTransferInitatedByName(rs.getString("user_name"));
                transferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_approvalLevel)) {
                	transferVO.setFirstApprovedBy(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedByName(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                	transferVO.setFirstApprovedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("first_approved_on"))));
                } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_approvalLevel)) {
                	transferVO.setFirstApprovedBy(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedByName(rs.getString("first_app_name"));
                	transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
                	transferVO.setFirstApprovedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("first_approved_on"))));
                	transferVO.setSecondApprovedBy(rs.getString("second_app_name"));
                	transferVO.setSecondApprovedByName(rs.getString("second_app_name"));
                    transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
                    transferVO.setSecondApprovedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("second_approved_on"))));
                }
                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.TRANSFER_STATUS, transferVO.getStatus())).getLookupName());
                }
                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
                transferVO.setNetworkCode(p_networkCode);
                transferVO.setNetworkCodeFor(p_networkCodeFor);
                // added as per mobinil cr 1
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                // ends here
                // for mali-- +ve commision apply
                transferVO.setCommQty(rs.getLong("commision_quantity"));
                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                transferVO.setCommProfileSetId(rs.getString("COMMISSION_PROFILE_SET_ID"));
                transferVO.setCommProfileVersion(rs.getString("COMMISSION_PROFILE_VER"));
                /** START: Birendra 27JAN2015 */
                transferVO.setUserWalletCode(rs.getString("user_wallet"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setToMSISDN(rs.getString("to_msisdn"));
                if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(rs.getString("transfer_sub_type")))
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.VOUCHER_PRODUCT_O2C);
                	
                }
                else
                {
                	transferVO.setTransferSubTypeAsString(PretupsI.REDEMP_TYPE_STOCK);
                }
                if(transferVO.getTransferInitatedBy().equals(transferVO.getToUserID())) {
                	transferVO.setTransferType("Buy");
                }else {
                	transferVO.setTransferType(rs.getString("transfer_type"));
                }
                /** STOP: Birendra 27JAN2015 */

                transferVO.setIndex(i);
                ++i;
                arrayList.add(transferVO);
            }

            
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersList]", "", "",
                "",  loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(arrayList.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return arrayList;
    }

    
    
    
    public void loadChannelTransfersVOC2C(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {

        final String methodName = "loadChannelTransfersVOC2C";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   p_channelTransferVO : ");
        	loggerValue.append(p_channelTransferVO);
            _log.debug(methodName,  loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        
        
        

        String sqlSelect = null;
        
        String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
        boolean tcpOn = false;
		Set<String> uniqueTransProfileId = new HashSet();
		HashMap<String, HashMap<String, String>> tcpMap = null;
		
		if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
			tcpOn = true;
		}
		
		if (tcpOn) {
			sqlSelect = channelTransferQry.loadChannelTransfersVOC2CTcpQry(p_channelTransferVO);
			
			SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")), ValueType.STRING);
			tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id", "status", "profile_name")),
					searchCriteria);


			
		} else {

			sqlSelect = channelTransferQry.loadChannelTransfersVOC2CQry(p_channelTransferVO);
		}
		
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            _log.debug(methodName,  loggerValue);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            ++m;
            pstmt.setString(m, p_channelTransferVO.getTransferID());
            ++m;
            pstmt.setString(m, p_channelTransferVO.getNetworkCode());
            ++m;
            pstmt.setString(m, p_channelTransferVO.getNetworkCodeFor());

            rs = pstmt.executeQuery();
            setChannelTransferVo(p_channelTransferVO, rs, tcpOn, tcpMap);
            
          //adding network name
            NetworkDAO networkDAO = new NetworkDAO();
            if(p_channelTransferVO.getNetworkCode()!=null) p_channelTransferVO.setNetworkName(networkDAO.loadNetwork(p_con, p_channelTransferVO.getNetworkCode()).getNetworkName());
            if(p_channelTransferVO.getNetworkCodeFor()!=null) p_channelTransferVO.setNetworkNameFor(networkDAO.loadNetwork(p_con, p_channelTransferVO.getNetworkCodeFor()).getNetworkName());
            
            //getting sender grade name
            if(p_channelTransferVO.getSenderGradeCode()!=null && p_channelTransferVO.getFromUserID()!=null) {
            	LoginDAO loginDAO = new LoginDAO();
            	GradeVO gradeVO = loginDAO.loadUserDetailsOnTwoFAallowed(p_con, p_channelTransferVO.getFromUserID());
            	p_channelTransferVO.setSenderGradeCodeDesc(gradeVO.getGradeName());
            }
            
            
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "p_channelTransferVO ::: " + p_channelTransferVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : " );
        	loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersVO]", "", "",
                "",  loggerValue.toString());
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
                _log.debug(methodName, "Exiting:  ");
            }
        }
    }
    
    private void setChannelTransferVo(ChannelTransferVO p_channelTransferVO, ResultSet rs, boolean tcpOn, HashMap<String, HashMap<String, String>> tcpMap) throws BTSLBaseException {
    	final String methodName = "setChannelTransferVo";
    	try {
	    	if (rs.next()) {
		        p_channelTransferVO.setTransferID(rs.getString("transfer_id"));
		        p_channelTransferVO.setNetworkCode(rs.getString("network_code"));
		        p_channelTransferVO.setNetworkCodeFor(rs.getString("network_code_for"));
		        p_channelTransferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
		        p_channelTransferVO.setDomainCode(rs.getString("domain_code"));
		        p_channelTransferVO.setCategoryCode(rs.getString("sender_category_code"));
		        p_channelTransferVO.setSenderGradeCode(rs.getString("sender_grade_code"));
		        p_channelTransferVO.setReceiverGradeCode(rs.getString("receiver_grade_code"));
		        p_channelTransferVO.setFromUserID(rs.getString("from_user_id"));
		        p_channelTransferVO.setToUserID(rs.getString("to_user_id"));
		        p_channelTransferVO.setTransferDate(rs.getTimestamp("transfer_date"));
		        p_channelTransferVO.setReferenceNum(rs.getString("reference_no"));
		        p_channelTransferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
		        p_channelTransferVO.setExternalTxnDate(rs.getTimestamp("ext_txn_date"));
		        p_channelTransferVO.setCommProfileSetId(rs.getString("commission_profile_set_id"));
		        p_channelTransferVO.setCommProfileVersion(rs.getString("commission_profile_ver"));
		        p_channelTransferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
		        p_channelTransferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
		        p_channelTransferVO.setFirstApprovalRemark(rs.getString("first_approver_remarks"));
		        p_channelTransferVO.setSecondApprovalRemark(rs.getString("second_approver_remarks"));
		        p_channelTransferVO.setThirdApprovalRemark(rs.getString("third_approver_remarks"));
		        p_channelTransferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
		        p_channelTransferVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
		        p_channelTransferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
		        p_channelTransferVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
		        p_channelTransferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
		        p_channelTransferVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
		        p_channelTransferVO.setCanceledBy(rs.getString("cancelled_by"));
		        p_channelTransferVO.setCanceledOn(rs.getTimestamp("cancelled_on"));
		        p_channelTransferVO.setModifiedOn(rs.getTimestamp("modified_on"));
		        p_channelTransferVO.setModifiedBy(rs.getString("modified_by"));
		        p_channelTransferVO.setStatus(rs.getString("status"));
		        p_channelTransferVO.setType(rs.getString("type"));
		        p_channelTransferVO.setTransferInitatedBy(rs.getString("transfer_initiated_by"));
		        p_channelTransferVO.setTransferMRP(rs.getLong("transfer_mrp"));
		        p_channelTransferVO.setFirstApproverLimit(rs.getLong("first_approver_limit"));
		        p_channelTransferVO.setSecondApprovalLimit(rs.getLong("second_approver_limit"));
		        p_channelTransferVO.setPayableAmount(rs.getLong("payable_amount"));
		        p_channelTransferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
		        p_channelTransferVO.setBatchNum(rs.getString("batch_no"));
		        p_channelTransferVO.setBatchDate(rs.getTimestamp("batch_date"));
		        p_channelTransferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
		        p_channelTransferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
		        p_channelTransferVO.setPayInstrumentDate(rs.getTimestamp("pmt_inst_date"));
		        p_channelTransferVO.setPayInstrumentAmt(rs.getLong("pmt_inst_amount"));
		        p_channelTransferVO.setSenderTxnProfile(rs.getString("sender_txn_profile"));
		        p_channelTransferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
		        p_channelTransferVO.setTotalTax1(rs.getLong("total_tax1"));
		        p_channelTransferVO.setTotalTax2(rs.getLong("total_tax2"));
		        p_channelTransferVO.setTotalTax3(rs.getLong("total_tax3"));
		        p_channelTransferVO.setSource(rs.getString("source"));
		        p_channelTransferVO.setReceiverCategoryCode(rs.getString("receiver_category_code"));
		        p_channelTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
		        p_channelTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
		        p_channelTransferVO.setPaymentInstSource(rs.getString("pmt_inst_source"));
		        p_channelTransferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
		        p_channelTransferVO.setProductType(rs.getString("product_type"));
		        p_channelTransferVO.setDomainCodeDesc(rs.getString("domain_name"));
		        p_channelTransferVO.setReceiverCategoryDesc(rs.getString("category_name"));
		        p_channelTransferVO.setReceiverGradeCodeDesc(rs.getString("grade_name"));
		        p_channelTransferVO.setToUserName(rs.getString("user_name"));
		        p_channelTransferVO.setAddress1(rs.getString("address1"));
		        p_channelTransferVO.setAddress2(rs.getString("address2"));
		        p_channelTransferVO.setCity(rs.getString("city"));
		        p_channelTransferVO.setState(rs.getString("state"));
		        p_channelTransferVO.setCountry(rs.getString("country"));
		        p_channelTransferVO.setCommProfileName(rs.getString("comm_profile_set_name"));
		        p_channelTransferVO.setStockUpdated(rs.getString("stock_updated"));
		        p_channelTransferVO.setDualCommissionType(rs.getString("dual_comm_type"));


		        
				if (!tcpOn) {

					
				if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
					p_channelTransferVO.setReceiverTxnProfileName(rs.getString("profile_name"));
					p_channelTransferVO.setSenderTxnProfileName(rs.getString("sender_txn_profile_name"));
				} else {
					p_channelTransferVO.setSenderTxnProfileName(rs.getString("profile_name"));
					p_channelTransferVO.setReceiverTxnProfileName(rs.getString("sender_txn_profile_name"));
				}
			} else {

				if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
					p_channelTransferVO.setReceiverTxnProfileName(
							tcpMap.get(rs.getString("receiver_txn_profile")).get("name"));// receiver_txn_profile
					p_channelTransferVO
							.setSenderTxnProfileName(tcpMap.get(rs.getString("sender_txn_profile")).get("name"));// sender_txn_profile

				} else {

					p_channelTransferVO.setReceiverTxnProfileName(
							tcpMap.get(rs.getString("sender_txn_profile")).get("name"));// receiver_txn_profile
					p_channelTransferVO.setSenderTxnProfileName(
							tcpMap.get(rs.getString("receiver_txn_profile")).get("name"));// sender_txn_profile

				}
		
			}
	
				
		        p_channelTransferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
		        p_channelTransferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
		        p_channelTransferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
		        p_channelTransferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
		        p_channelTransferVO.setTransferInitatedByName(rs.getString("initatedby"));
		        if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
		            p_channelTransferVO.setUserMsisdn(rs.getString("from_msisdn"));
		        } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
		            p_channelTransferVO.setUserMsisdn(rs.getString("to_msisdn"));
		        } else {
		            p_channelTransferVO.setUserMsisdn(rs.getString("msisdn"));
		        }
		        p_channelTransferVO.setErpNum(rs.getString("external_code"));
		        p_channelTransferVO.setFromUserName(rs.getString("fromusername"));
		        p_channelTransferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_channelTransferVO.getTransferDate())));
		        p_channelTransferVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
		        p_channelTransferVO.setTransferType(rs.getString("transfer_type"));
		        p_channelTransferVO.setTransferSubType(rs.getString("transfer_sub_type"));
		
		        p_channelTransferVO.setTransferCategory(rs.getString("transfer_category"));
		        p_channelTransferVO.setDefaultLang(rs.getString("sms_default_lang"));
		        p_channelTransferVO.setSecondLang(rs.getString("sms_second_lang"));
		        p_channelTransferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
		        p_channelTransferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
		        p_channelTransferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
		        p_channelTransferVO.setWalletType(rs.getString("txn_wallet"));
		        p_channelTransferVO.setControlTransfer(rs.getString("control_transfer"));
		
		        p_channelTransferVO.setReceiverGgraphicalDomainCode(rs.getString("to_grph_domain_code"));
		        p_channelTransferVO.setReceiverDomainCode(rs.getString("to_domain_code"));
		        p_channelTransferVO.setFromUserCode(rs.getString("msisdn"));
		        p_channelTransferVO.setToUserCode(rs.getString("to_msisdn"));
		        p_channelTransferVO.setCommQty(rs.getLong("commision_quantity"));
		        p_channelTransferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
		        p_channelTransferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
		        p_channelTransferVO.setCreatedBy(rs.getString("created_by"));
		        p_channelTransferVO.setCreatedOn(rs.getDate("created_on"));
		        p_channelTransferVO.setActiveUserId(rs.getString("active_user_id"));
		        p_channelTransferVO.setPayInstrumentType(rs.getString("PMT_INST_TYPE"));
		        p_channelTransferVO.setUserWalletCode(rs.getString("user_wallet"));
		        p_channelTransferVO.setProductCode(rs.getString("product_code"));
		        
		        p_channelTransferVO.setFromUserName(rs.getString("fromUserName"));
		        p_channelTransferVO.setToUserName(rs.getString("user_name"));
		        p_channelTransferVO.setFromMsisdn(rs.getString("from_msisdn"));
		        p_channelTransferVO.setToMsisdn(rs.getString("to_msisdn"));
		        p_channelTransferVO.setFromCategoryDesc(rs.getString("sender_category_code"));
		        p_channelTransferVO.setToCategoryDesc(rs.getString("receiver_category_code"));
		        p_channelTransferVO.setFromGradeCodeDesc(rs.getString("fromGradeCodeDesc"));
		        p_channelTransferVO.setToGradeCodeDesc(rs.getString("receiver_grade_code"));
		        p_channelTransferVO.setFromCommissionProfileIDDesc(rs.getString("fromCommissionProfileIDDesc"));
		        p_channelTransferVO.setToCommissionProfileIDDesc(rs.getString("comm_profile_set_name"));
		        p_channelTransferVO.setToTxnProfileDesc(rs.getString("sender_txn_profile"));
		        p_channelTransferVO.setFromTxnProfileDesc(rs.getString("receiver_txn_profile"));
		        p_channelTransferVO.setReceiverGgraphicalDomainCodeDesc(rs.getString("TO_GRPH_DOMAIN_NAME"));
		        p_channelTransferVO.setReceiverDomainCodeDesc(rs.getString("to_domain_name"));
		        
	    	}
    	}  catch (SQLException sqe) {
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
    }

	public boolean isTransactionPackageBased(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "isTransactionPackageBased";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
loggerValue.append("Entered  TransferNumber: ");
        	loggerValue.append(p_transferID);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff=null;
        boolean isPackageBasedTXN = false;
        
        strBuff = channelTransferQry.loadBundleIDForTransactionIdQry();
       
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff);
            _log.debug(methodName,  loggerValue );
        }
        
        try {
        	pstmt = p_con.prepareStatement(strBuff.toString());
        	int m = 0;            
        	++m;
        	pstmt.setString(m, p_transferID);            
        	rs = pstmt.executeQuery();        	

        	if (rs.next()) {        		
        		String bundleID = rs.getString("BUNDLE_ID");        		
        		if(!BTSLUtil.isNullString(bundleID) && !"0".equalsIgnoreCase(bundleID))
        		{
        			isPackageBasedTXN = true;        			
        		}
        	}
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception:");
             loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
             loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(ex.getMessage());;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  isPackageBasedTXN ="+isPackageBasedTXN);            	
                _log.debug(methodName,  loggerValue );
            }
        }
        return isPackageBasedTXN;
    }
   
    public ArrayList loadPackageDetails(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadPackageDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered  TransferNumber: ");
        	loggerValue.append(p_transferID);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff=null;
        boolean isPackageBasedTXN = false;
        VomsPackageVoucherVO packageVO = null;
        ArrayList packageTXNList = new ArrayList();
        strBuff = channelTransferQry.loadPackageVoucherDetailsForTransactionIdQry();
       
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff);
            _log.debug(methodName,  loggerValue );
        }
        
        try {
        	pstmt = p_con.prepareStatement(strBuff.toString());
        	int m = 0;            
        	++m;
        	pstmt.setString(m, p_transferID);            
        	rs = pstmt.executeQuery();        	

        	while(rs.next()) {  
        		packageVO = new VomsPackageVoucherVO();
        		packageVO.setBundleID(Long.parseLong(rs.getString("BUNDLE_ID")));
        		packageVO.setBundleName(rs.getString("BUNDLE_NAME"));
        		packageVO.setTransferID(rs.getString("TRANSFER_ID"));
        		packageVO.setProductName(rs.getString("PRODUCT_NAME"));
        		packageVO.setBundleCount(Long.parseLong(rs.getString("BUNDLE_COUNT_REQUESTED")));
        		packageVO.setQuantity(Integer.parseInt(rs.getString("PRODUCT_COUNT")));
        		packageVO.setBundleRetailPrice(Long.parseLong(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("RETAIL_PRICE")))));
        		packageVO.setPrice(Double.parseDouble(rs.getString("REQUESTED_QUANTITY")));
        		packageTXNList.add(packageVO);
        	}
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception:");
             loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
             loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(ex.getMessage());;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  packageTXNList.size ="+packageTXNList.size());            	
                _log.debug(methodName,  loggerValue );
            }
        }
        return packageTXNList;
    }  
	
	    public int updateChannelVoucherItemsPackage(Connection p_con, ArrayList p_itemsList) throws BTSLBaseException {
        final String methodName = "updateChannelVoucherItems";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:");
        }
        int update_count = 0;
        
        final StringBuffer strBuffer = new StringBuffer("UPDATE channel_voucher_items SET requested_quantity= ? ");
        strBuffer.append(" where transfer_id = ? ");
//        strBuffer.append(" AND s_no = ? AND transfer_date = ? ");
        strBuffer.append(" AND mrp = ? AND bundle_id = ? AND  product_id=? ");
        final String sqlSlt = strBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSlt=" + sqlSlt);
        }
        try(PreparedStatement psmt = p_con.prepareStatement(sqlSlt); ) {
            ChannelVoucherItemsVO itemsVO = null;
            int m = 0;
            for (int i = 0, k = p_itemsList.size(); i < k; i++) {
                itemsVO = (ChannelVoucherItemsVO) p_itemsList.get(i);
                m = 0;
            	++m;
                psmt.setLong(m, itemsVO.getRequiredQuantity());
            	++m;
                psmt.setString(m, itemsVO.getTransferId());
//                ++m;
//                psmt.setLong(m, itemsVO.getSNo());
//                ++m;
//                psmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(itemsVO.getTransferDate()));
                ++m;
                psmt.setLong(m, itemsVO.getTransferMrp());
                ++m;
                psmt.setLong(m, itemsVO.getBundleId());
                ++m;
                psmt.setString(m, itemsVO.getProductId());                

                update_count = psmt.executeUpdate();
                psmt.clearParameters();
                update_count = BTSLUtil.getInsertCount(update_count);
                if (update_count < 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelVoucherItems]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelVoucherItems", "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[updateChannelVoucherItems]", "", "", "",
                        loggerValue.toString());
            throw new BTSLBaseException(this, "updateChannelVoucherItems", "error.general.processing");
        } finally {

            if (_log.isDebugEnabled()) {
                  loggerValue.setLength(0);
                  loggerValue.append("Exiting: update count  =");
                  loggerValue.append(update_count);
                _log.debug(methodName,  loggerValue);
            }
        }
        return update_count;

    }
    
    //retrieves the vouchers available to be associated product-wise
    public HashMap loadVoucherCountForPackage(Connection p_con, String voucher_status) throws BTSLBaseException {

        final String methodName = "loadVoucherCountForPackage";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff=null;
 
        strBuff = new StringBuilder(" SELECT vv.product_id , COUNT(*) voucher_count ");
		strBuff.append(" FROM voms_vouchers vv , voms_products vp ");
		strBuff.append(" WHERE vv.status = ? and vp.product_id = vv.product_id and vp.status <> 'N' AND vv.master_serial_no is null ");//package not assigned
		strBuff.append(" GROUP BY vv.product_id ");
		strBuff.append(" ORDER BY vv.product_id ");
		
        
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff);
            _log.debug(methodName,  loggerValue );
        }
        HashMap<Integer,Long> voucherCount = new HashMap<Integer,Long>();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            if (!BTSLUtil.isNullString(voucher_status)) {
                ++m;
                pstmt.setString(m, voucher_status);
            }
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
            	voucherCount.put(rs.getInt("product_id"),rs.getLong("voucher_count"));
            	
            }
           
        
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadVoucherCountForPackage]",
                "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception:");
             loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
             loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(ex.getMessage());;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadVoucherCountForPackage]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(voucherCount.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return voucherCount;
    }
    
    //retrieves bundle info to create master serial for package vouchers
    public HashMap loadBundleSerialInfo(Connection p_con) throws BTSLBaseException {

        final String methodName = "loadBundleSerialInfo";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
            _log.debug(methodName,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder strBuff=null;
 
        strBuff = new StringBuilder(" SELECT vbm.voms_bundle_id , vbm.last_bundle_sequence , vbm.bundle_prefix ");
		strBuff.append(" FROM voms_bundle_master vbm ");
		strBuff.append(" WHERE status <> 'N' ");
		
        
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff);
            _log.debug(methodName,  loggerValue );
        }
        HashMap<Integer,ArrayList<Integer>> bundleInfo = new HashMap<Integer,ArrayList<Integer>>();
        try {
            pstmt = p_con.prepareStatement(strBuff.toString());
            int m = 0;
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
            	ArrayList<Integer> info = new ArrayList<Integer>();
            	if(rs.getString("last_bundle_sequence") != null)
            		info.add(Integer.parseInt(rs.getString("last_bundle_sequence")));
            	else
            	info.add(Integer.parseInt(rs.getString("bundle_prefix")));
            	bundleInfo.put(rs.getInt("voms_bundle_id"), info);
            	
            }
           
        
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error(methodName,  loggerValue);
            _log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqe.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleSerialInfo]",
                "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
             loggerValue.append("Exception:");
             loggerValue.append(ex);
            _log.error("", loggerValue );
            _log.errorTrace(methodName, ex);
             loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(ex.getMessage());;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleSerialInfo]",
                "", "", "", loggerValue.toString());
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  arrayList Size =");
            	loggerValue.append(bundleInfo.size());
                _log.debug(methodName,  loggerValue );
            }
        }
        return bundleInfo;
    }
	  public HashMap loadAvailableVouchersSerialNos(Connection p_con , String voucher_status, String status) throws BTSLBaseException {
	        if (_log.isDebugEnabled()) {
	            _log.debug("loadAvailableVouchersDetails", "voucher_status: " + voucher_status);
	        }
	        PreparedStatement dbPs = null;
	        ResultSet rs = null;
	        VomsVoucherVO voucherVO = null;
	        HashMap<String,ArrayList<String>> voucherSerialNo = new HashMap<String,ArrayList<String>>();
	        ArrayList<String> serialNos = new ArrayList<String>();
	        String serialNo = null , productID = null ;
	        final String methodName = "loadAvailableVouchersDetails";
	        try {
	        	int i =1;
	            StringBuffer sqlSelectBuf = new StringBuffer(" SELECT   vv.serial_no , vv.product_id ");
	            sqlSelectBuf.append(" FROM voms_vouchers vv, voms_products vp ");
	            sqlSelectBuf.append(" WHERE vv.status = ? AND vp.product_id = vv.product_id AND vp.status <> ? AND vv.master_serial_no is null " );

	            if (_log.isDebugEnabled()) {
	                _log.debug("loadAvailableVouchersSerialNos", "Select Query=" + sqlSelectBuf.toString());
	            }
	            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
	            dbPs.setString(i++, voucher_status);
	            dbPs.setString(i++, status);
	            rs = dbPs.executeQuery();

	            while (rs.next()) {
	                serialNo = rs.getString("SERIAL_NO");
	                productID = rs.getString("PRODUCT_ID");
	                if(voucherSerialNo.containsKey(productID)) {
	                	serialNos = voucherSerialNo.get(productID);
	                	serialNos.add(serialNo);
	                	voucherSerialNo.put(productID , serialNos);
	                }
	                else {
	                	serialNos = new ArrayList<String>();
	                	serialNos.add(serialNo);
	                	voucherSerialNo.put(productID , serialNos);
	                }
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug("loadAvailableVouchersDetails", "After executing the query loadAvailableVouchersDetails method VomsVoucherVO=" + voucherVO);
	            }
	            return voucherSerialNo;
	        } catch (SQLException sqle) {
	            _log.error("loadAvailableVouchersDetails", "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadAvailableVouchersDetails]", "", "", "", "Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error("loadAvailableVouchersDetails", "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadAvailableVouchersDetails]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.processing");
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception ex) {
	                _log.error("loadAvailableVouchersDetails", " Exception while closing rs ex=" + ex);
	            }
	            try {
	                if (dbPs != null) {
	                    dbPs.close();
	                }
	            } catch (Exception ex) {
	                _log.error("loadAvailableVouchersDetails", " Exception while closing prepared statement ex=" + ex);
	            }
	            try {
	                _log.debug("loadAvailableVouchersDetails", " Exiting.. VomsVoucherVO=" + voucherVO);
	            } catch (Exception e) {
	                _log.error("loadAvailableVouchersDetails", " Exception while closing rs ex=" + e);
	            }
	            ;
	        }
	    }
	  
		public int updateVoucherMasterSerialNo(Connection p_con,  ArrayList<VomsVoucherVO> vomsVoucherVOList) throws BTSLBaseException {
	        if (_log.isDebugEnabled()) {
	            _log.debug("updateVoucherMasterSerialNo() ", " Entered vomsVoucherVOList.=" + vomsVoucherVOList);
	        }
	        PreparedStatement pstmtUpdate = null;
	        int updateCount = 0;
	        final String methodName = "updateVoucherMasterSerialNo";
	        StringBuilder vomsString = new StringBuilder();
	        
	        try {
	            int i = 1;
	            int count = 0;
	            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE voms_vouchers");
	            updateQueryBuff.append(" SET master_serial_no = ? , bundle_id = ? , user_id = ? , ");
	            updateQueryBuff.append(" status = ? , previous_status = ? , current_status = ? , ");
	            updateQueryBuff.append(" modified_by = ? , modified_on = ? , last_transaction_id = ? ");
	            updateQueryBuff.append(" WHERE serial_no = ? AND PRODUCT_ID = ? ");
	           
	            String updateQuery = updateQueryBuff.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug("updateVoucherMasterSerialNo", "Update query:" + updateQuery);
	            }
	          for(VomsVoucherVO vomsVoucherVO: vomsVoucherVOList){
		        	count =0;
		        	i=1;
		        	pstmtUpdate = p_con.prepareStatement(updateQuery);
		            pstmtUpdate.setLong(i++, vomsVoucherVO.getMasterSerialNo());
		            pstmtUpdate.setLong(i++, vomsVoucherVO.getBundleId());
		            pstmtUpdate.setString(i++, vomsVoucherVO.getUserID());
		            pstmtUpdate.setString(i++, vomsVoucherVO.getStatus());
		            pstmtUpdate.setString(i++, vomsVoucherVO.getPreviousStatus());
		            pstmtUpdate.setString(i++, vomsVoucherVO.getCurrentStatus());
		            pstmtUpdate.setString(i++, vomsVoucherVO.getModifiedBy());
		            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(vomsVoucherVO.getModifiedOn()));
		            pstmtUpdate.setString(i++, vomsVoucherVO.getTransactionID());
		            pstmtUpdate.setString(i++, vomsVoucherVO.getSerialNo());
		            pstmtUpdate.setString(i++, vomsVoucherVO.getProductID());
		            count = pstmtUpdate.executeUpdate();
		            if(count > 0) {
		            	updateCount++;
		            }
	          }
	          
	          return updateCount;
	        } catch (SQLException sqle) {
	            _log.error("updateVoucherMasterSerialNo", "SQLException " + sqle.getMessage());
	            updateCount = 0;
	            _log.errorTrace(methodName, sqle);
	            throw new BTSLBaseException(this, "updateVoucherMasterSerialNo", "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error("updateVoucherMasterSerialNo", "Exception " + e.getMessage());
	            updateCount = 0;
	            _log.errorTrace(methodName, e);
	            throw new BTSLBaseException(this, "updateVoucherMasterSerialNo", "error.general.processing");
	        }// end of catch
	        finally {
	            try {
	                if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug("updateVoucherMasterSerialNo", "Exiting updateCount=" + updateCount);
	            }
	        }// end of finally
	    }
		
		public ArrayList loadBundleMasterDetails(Connection p_con) throws BTSLBaseException {

	        final String methodName = "loadBundleMasterDetails";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	            _log.debug(methodName,loggerValue );
	        }

	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        StringBuilder strBuff=null;
	 
	        strBuff = new StringBuilder(" SELECT * from voms_bundle_master where STATUS = 'Y' ");
			
	        
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(strBuff);
	            _log.debug(methodName,  loggerValue );
	        }
	        ArrayList<VoucherBundleVO> bundleInfo = new ArrayList<VoucherBundleVO>();
	        VoucherBundleVO voucherBundleVO = new VoucherBundleVO();
	        try {
	            pstmt = p_con.prepareStatement(strBuff.toString());
	            int m = 0;
	            rs = pstmt.executeQuery();
	            
	            while(rs.next()) {
	            	voucherBundleVO = new VoucherBundleVO();
	            	voucherBundleVO.setBundleName(rs.getString("BUNDLE_NAME"));
	            	voucherBundleVO.setPrefixID(rs.getString("BUNDLE_PREFIX"));
	            	voucherBundleVO.setVomsBundleID(Integer.toString(rs.getInt("VOMS_BUNDLE_ID")));
	            	voucherBundleVO.setRetailPrice(rs.getDouble("RETAIL_PRICE"));
	            	voucherBundleVO.setLastBundleSequence(rs.getLong("LAST_BUNDLE_SEQUENCE"));
	            	bundleInfo.add(voucherBundleVO);
	            }
	           
	        
	        } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe);
	            _log.error(methodName,  loggerValue);
	            _log.errorTrace(methodName, sqe);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(sqe.getMessage());
	            
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleMasterDetails]",
	                "", "", "",  loggerValue.toString() );
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	        	 loggerValue.setLength(0);
	             loggerValue.append("Exception:");
	             loggerValue.append(ex);
	            _log.error("", loggerValue );
	            _log.errorTrace(methodName, ex);
	             loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(ex.getMessage());;
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleMasterDetails]",
	                "", "", "", loggerValue.toString());
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
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting:  arrayList Size =");
	            	loggerValue.append(bundleInfo.size());
	                _log.debug(methodName,  loggerValue );
	            }
	        }
	        return bundleInfo;
	    }
	    
	    public int updateBundleLastSequence(Connection pCon, HashMap<String , VoucherBundleVO> packageInfo) throws BTSLBaseException {
			int updateCount = 0;
			final String methodName = "updateBundleLastSequence";
			StringBuilder loggerValue = new StringBuilder();
			if (_log.isDebugEnabled()) {    
				loggerValue.setLength(0);
				loggerValue.append("Entered: pacakgeInfo= ");
				loggerValue.append(packageInfo.toString());
				_log.debug(methodName, loggerValue);
			}
			try {
				final StringBuilder strBuff = new StringBuilder();
				strBuff.append("UPDATE voms_bundle_master ");
				strBuff.append("SET last_bundle_sequence = ? ");
				strBuff.append("WHERE voms_bundle_id = ? ");

				final String updateQuery = strBuff.toString();
				if (_log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Query sql:");
					loggerValue.append(updateQuery);
					_log.debug(methodName, loggerValue);
				}
				int i , count ;
				for(Map.Entry<String, VoucherBundleVO> entry : packageInfo.entrySet()) {
					i = 1 ;
					try (PreparedStatement psmtUpdate = pCon.prepareStatement(updateQuery);) {
						count = 0;
						psmtUpdate.setLong(i++, entry.getValue().getLastBundleSequence());
						psmtUpdate.setString(i++, entry.getValue().getVomsBundleID());
						count = psmtUpdate.executeUpdate();
						if(count > 0) {
							updateCount++;
						}
					}
				}
			} // end of try
			catch (SQLException sqle) {
				loggerValue.setLength(0);
				loggerValue.append("SQL Exception: ");
				loggerValue.append(sqle.getMessage());
				String logVal1 = loggerValue.toString();
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, sqle);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
						"ChannelTransferDAO[updateBundleLastSequence]", "", "", "", logVal1);
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			} // end of catch
			catch (Exception e) {
				loggerValue.setLength(0);
				loggerValue.append("Exception: ");
				loggerValue.append(e.getMessage());
				String logVal1 = loggerValue.toString();
				_log.error(methodName, loggerValue);
				_log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
						"ChannelTransferDAO[updateBundleLastSequence]", "", "", "", logVal1);
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			} // end of catch
			finally {

				if (_log.isDebugEnabled()) {
					loggerValue.setLength(0);
					loggerValue.append("Exiting: insertCount=");
					loggerValue.append(updateCount);
					_log.debug(methodName, loggerValue);
				}
			} // end of finally

			return updateCount;
		}
		
		  public HashMap loadProductDetails(Connection p_con) throws BTSLBaseException {
	        final String methodName = "loadProductsList";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Entered ");
	        }
	        
	        
	        VomsProductVO productVO = null;
	        HashMap<String,VomsProductVO> productList = new HashMap<String,VomsProductVO>();
	        String strBuff= " select vp.PRODUCT_NAME , vp.PRODUCT_ID from voms_products vp where vp.status <> ? ";
	        try (PreparedStatement pstmt = p_con.prepareStatement(strBuff);){
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " loadProductsList() of ProductDAO:: Query :: " + strBuff);
	            }
	            // Get Prepared Statement
	           
	            int count = 1;        
	            pstmt.setString(count++ , PretupsI.NO);
	          

	            // Execute Query
	            try( ResultSet rs = pstmt.executeQuery();)
	            {
	         
	            // Get Products Details
	            while (rs.next()) {
	                productVO = new VomsProductVO();
	                productVO.setProductID(rs.getString("PRODUCT_ID"));
	                productVO.setProductName(rs.getString("PRODUCT_NAME"));               
	                // Set in list
	                productList.put(rs.getString("PRODUCT_ID"),productVO);
	            }
	            return productList;
	        } 
	        }catch (SQLException sqe) {
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadProductsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadProductsList]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting: productList=" + productList.size());
	            }
	        }
	    }
	    public HashMap loadAvailableVouchersSerialNosByProducts(Connection p_con , HashMap<Integer, Long> productCount , String voucher_status, String status) throws BTSLBaseException {
	        if (_log.isDebugEnabled()) {
	            _log.debug("loadAvailableVouchersSerialNosByProducts", "products: " + productCount.size());
	        }
	        PreparedStatement dbPs = null;
	        ResultSet rs = null;
	        VomsVoucherVO voucherVO = null;
	        HashMap<String,ArrayList<String>> voucherSerialNo = new HashMap<String,ArrayList<String>>();
	        ArrayList<String> serialNos = new ArrayList<String>();
	        String serialNo = null , productID = null ;
            final String methodName = "loadAvailableVouchersSerialNosByProducts";
            String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
	        try {
	        	int i =1;
	           /* StringBuffer sqlSelectBuf = new StringBuffer(" SELECT   vv.serial_no , vv.product_id ");
	            sqlSelectBuf.append(" FROM voms_vouchers vv, voms_products vp ");
	            sqlSelectBuf.append(" WHERE vv.status = ? AND vp.product_id = vv.product_id AND vp.status <> ? AND vv.master_serial_no is null " );

	            if (_log.isDebugEnabled()) {
	                _log.debug("loadAvailableVouchersSerialNosByProducts", "Select Query=" + sqlSelectBuf.toString());
	            }
	            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
	            dbPs.setString(i++, voucher_status);
	            dbPs.setString(i++, status);*/
	        	
	        	StringBuffer sqlSelectBuf = new StringBuffer();
	        	int productsCount = productCount.size();
	        	
	        	for(int j = 1 ; j <= productsCount ; j++) {
	        		sqlSelectBuf.append(" SELECT * FROM ");
	        		sqlSelectBuf.append(" ( SELECT   vv.serial_no , vv.product_id ");
		            sqlSelectBuf.append(" FROM voms_vouchers vv, voms_products vp ");
		            sqlSelectBuf.append(" WHERE vv.status = ? AND vp.product_id = vv.product_id AND vp.status <> ? " );
		            sqlSelectBuf.append(" AND vv.product_id = ? AND vv.master_serial_no is null " );
                    //if oracle
		            if(QueryConstants.DB_ORACLE.equals(dbConnected))
                    sqlSelectBuf.append(" AND ROWNUM <= ? ORDER BY vv.created_on ) ");
                //if postgresql
                if(QueryConstants.DB_POSTGRESQL.equals(dbConnected))
                    sqlSelectBuf.append(" ORDER BY vv.created_on LIMIT ?) AS x ");
		            if(j < productsCount)
		            	sqlSelectBuf.append(" UNION ALL ");
	        	}
	        	dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
	        	for(Map.Entry<Integer, Long> entry : productCount.entrySet()) {
	        		dbPs.setString(i++, voucher_status);
		            dbPs.setString(i++, status);
		            dbPs.setString(i++, Integer.toString(entry.getKey()));
		            dbPs.setLong(i++, entry.getValue());
	        	}
	            
	            
	            rs = dbPs.executeQuery();

	            while (rs.next()) {
	                serialNo = rs.getString("SERIAL_NO");
	                productID = rs.getString("PRODUCT_ID");
	                if(voucherSerialNo.containsKey(productID)) {
	                	serialNos = voucherSerialNo.get(productID);
	                	serialNos.add(serialNo);
	                	voucherSerialNo.put(productID , serialNos);
	                }
	                else {
	                	serialNos = new ArrayList<String>();
	                	serialNos.add(serialNo);
	                	voucherSerialNo.put(productID , serialNos);
	                }
	            }
	            
	            if (_log.isDebugEnabled()) {
	            	_log.debug("loadAvailableVouchersSerialNosByProducts", " SELECT script:= " + sqlSelectBuf);
	                _log.debug("loadAvailableVouchersSerialNosByProducts", "After executing the query loadAvailableVouchersDetails method voucherSerialNo=" + voucherSerialNo.size());
	            }
	            return voucherSerialNo;
	        } catch (SQLException sqle) {
	            _log.error("loadAvailableVouchersDetails", "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadAvailableVouchersSerialNosByProducts]", "", "", "", "Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error("loadAvailableVouchersDetails", "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadAvailableVouchersSerialNosByProducts]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.processing");
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception ex) {
	                _log.error("loadAvailableVouchersSerialNosByProducts", " Exception while closing rs ex=" + ex);
	            }
	            try {
	                if (dbPs != null) {
	                    dbPs.close();
	                }
	            } catch (Exception ex) {
	                _log.error("loadAvailableVouchersSerialNosByProducts", " Exception while closing prepared statement ex=" + ex);
	            }
	            try {
	                _log.debug("loadAvailableVouchersSerialNosByProducts", " Exiting.. VomsVoucherVO=" + voucherVO);
	            } catch (Exception e) {
	                _log.error("loadAvailableVouchersSerialNosByProducts", " Exception while closing rs ex=" + e);
	            }
	            ;
	        }
	    }
	    
	    /**
	     * Load Product List for Reports
	     * 
	     * @param p_con
	     * @param p_subCategory
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public HashMap loadBundleNameIDInfo(Connection p_con) throws BTSLBaseException {
	        final String methodName = "loadBundleNameIDInfo";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Entered ");
	        }
	        
	        
	        VoucherBundleVO voucherBundleVO = null;
	        HashMap<String,VoucherBundleVO> bundleList = new HashMap<String,VoucherBundleVO>();
	        String strBuff= " select vb.VOMS_BUNDLE_ID , vb.BUNDLE_NAME  from voms_bundle_master vb ";
	        try (PreparedStatement pstmt = p_con.prepareStatement(strBuff);){
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " loadProductsList() of ProductDAO:: Query :: " + strBuff);
	            }
	            // Get Prepared Statement           
	            int count = 1;        

	            // Execute Query
	            try( ResultSet rs = pstmt.executeQuery();)
	            {
	         
	            // Get Products Details
	            while (rs.next()) {
	            	voucherBundleVO = new VoucherBundleVO();
	            	voucherBundleVO.setVomsBundleID(Long.toString(rs.getLong("VOMS_BUNDLE_ID")));
	            	voucherBundleVO.setBundleName(rs.getString("BUNDLE_NAME"));
	                // Set in list
	            	bundleList.put(voucherBundleVO.getBundleName(),voucherBundleVO);
	            }
	            return bundleList;
	        } 
	        }catch (SQLException sqe) {
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleNameIDInfo]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleNameIDInfo]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting: bundleList=" + bundleList.size());
	            }
	        }
	    }
	    
	    /**
	     * Load channel transfer  details for MRP successive block timeout 
	     * @param p_con
	     * @param p_channelTransferVO
	     * @param p_chnlTxnMrpBlockTimeoutAllowed
	     * @param p_requestGatewayCodeCheckRequired
	     * @param p_currDate
	     * @param p_successiveReqBlockTime
	     * @return
	     * @throws BTSLBaseException
	     */
	     
		public boolean loadChannelTransfersDetails(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_chnlTxnMrpBlockTimeoutAllowed, boolean p_requestGatewayCodeCheckRequired, Date p_currDate, long p_successiveReqBlockTime) throws BTSLBaseException
	     {
	     	final String methodName = "loadChannelTransfersDetails";
	 		if (_log.isDebugEnabled())
	         {
	             _log.debug(methodName, "Entered   p_channelTransferVO : " + p_channelTransferVO);
	         }
	 		 boolean isLastChnlTxnFound =  false;
	         PreparedStatement pstmt = null;
	         ResultSet rs = null;
			 
	         
	         String strBuff = null;
	         
	         String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
	         boolean tcpOn = false;
	         Set<String> uniqueTransProfileId = new HashSet();
	         
		if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
			tcpOn = true;
		}

		if (tcpOn) {

			strBuff = channelTransferQry.loadChannelTransferDetailsTcpQry(p_chnlTxnMrpBlockTimeoutAllowed,
					p_requestGatewayCodeCheckRequired, p_channelTransferVO);
		} else {

			strBuff = channelTransferQry.loadChannelTransferDetailsQry(p_chnlTxnMrpBlockTimeoutAllowed,
					p_requestGatewayCodeCheckRequired, p_channelTransferVO);

		}

		
	         String sqlSelect = strBuff.toString();
	         if (_log.isDebugEnabled())
	         {
	             _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	         }

	         try
	         {
	             pstmt = p_con.prepareStatement(sqlSelect);
	             int m = 0;
//	             pstmt.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_currDate));
	             pstmt.setString(++m, p_channelTransferVO.getFromUserID());
	             pstmt.setString(++m, p_channelTransferVO.getToUserID());
	             if(p_chnlTxnMrpBlockTimeoutAllowed) {
	             	pstmt.setLong(++m,   p_channelTransferVO.getTransferMRP());
	             }
	             if(p_requestGatewayCodeCheckRequired) {
	 	            pstmt.setString(++m, p_channelTransferVO.getRequestGatewayCode());
	 	            pstmt.setString(++m, p_channelTransferVO.getRequestGatewayType());
	             }
	             //Check for successful transactions only
	             pstmt.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
	             if(!BTSLUtil.isNullString(p_channelTransferVO.getProductType())) {
	             	pstmt.setString(++m, p_channelTransferVO.getProductType());
	             }
	             pstmt.setString(++m, p_channelTransferVO.getType());
	             pstmt.setString(++m, p_channelTransferVO.getTransferCategory());
	             pstmt.setString(++m, p_channelTransferVO.getTransferType());
	             pstmt.setString(++m, p_channelTransferVO.getTransferSubType());
	             pstmt.setString(++m, p_channelTransferVO.getNetworkCode());
	             pstmt.setString(++m, p_channelTransferVO.getNetworkCodeFor());

	             rs = pstmt.executeQuery();
	             if (rs.next())
	             {
	             	if (_log.isDebugEnabled()) {
	     				_log.debug(methodName, "Last transaction ID = " + rs.getString("transfer_id"));
	     			}
	             	if(((p_currDate.getTime()-BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("close_date")).getTime())/1000)<=((Long)p_successiveReqBlockTime)){
	            		 	//Mark TRUE if within the configured timeout for MRP Successive block timeout
	 	        		if(p_chnlTxnMrpBlockTimeoutAllowed) {
	 	        			isLastChnlTxnFound = true;              
	 	        		}
	            	 	} else {
	 	           	 	if (_log.isDebugEnabled()) {
	 	    				_log.debug(methodName, "Time difference from last transaction = " + ((p_currDate.getTime()-BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("close_date")).getTime())/1000)+" in second , p_successiveReqBlockTime = "+(Long)p_successiveReqBlockTime);
	 	    			}           	 	
	            	 	}
	             	if (_log.isDebugEnabled()) {
	     				_log.debug(methodName, "isLastChnlTxnFound = " + isLastChnlTxnFound);
	     			}            	 
	 			} else {
	 				if (_log.isDebugEnabled()) {
	     				_log.debug(methodName, "No Record found !!!!!! ");
	     			} 
	 			}	 				
	 			return isLastChnlTxnFound;
	 		} catch (SQLException sqe) {
	 			_log.errorTrace(methodName, sqe);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	 					"ChannelTransferDAO[]", "", "", "", "SQL Exception:" + sqe.getMessage());
	 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	 		} catch (Exception ex) {
	 			_log.errorTrace(methodName, ex);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	 					"ChannelTransferDAO[loadChannelTransfersDetails]", "", "", "", "Exception:" + ex.getMessage());
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
	 				_log.debug(methodName, "Exiting:  isLastChnlTxnFound="+isLastChnlTxnFound);
	 			}
	 			// return isLastChnlTxnFound;
	 		}
	 	}
		
		public HashMap loadBundleInfoByPrefixId(Connection p_con) throws BTSLBaseException {
	        final String methodName = "loadBundleInfoByPrefixId";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Entered ");
	        }
	        
	        
	        VoucherBundleVO voucherBundleVO = null;
	        HashMap<String,VoucherBundleVO> bundleList = new HashMap<String,VoucherBundleVO>();
	        String strBuff= " select vb.voms_bundle_id , vb.bundle_name, vb.retail_price, vb.bundle_prefix  from voms_bundle_master vb where vb.status <> ? ";
	        try (PreparedStatement pstmt = p_con.prepareStatement(strBuff);){
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "ChannelTransferDAO :: Query :: " + strBuff);
	            }
	            // Get Prepared Statement           
	            int count = 1;        
	            pstmt.setString(1, PretupsI.NO);
	            // Execute Query
	            try( ResultSet rs = pstmt.executeQuery();)
	            {
	         
	            // Get Products Details
	            while (rs.next()) {
	            	voucherBundleVO = new VoucherBundleVO();
	            	voucherBundleVO.setVomsBundleID(Long.toString(rs.getLong("VOMS_BUNDLE_ID")));
	            	voucherBundleVO.setBundleName(rs.getString("BUNDLE_NAME"));
	            	voucherBundleVO.setRetailPrice(rs.getLong("RETAIL_PRICE"));
	            	voucherBundleVO.setPrefixID(rs.getString("bundle_prefix"));
	                // Set in list
	            	bundleList.put(voucherBundleVO.getPrefixID(),voucherBundleVO);
	            }
	            return bundleList;
	        } 
	        }catch (SQLException sqe) {
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[fetchVoucherCountInBundle]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[fetchVoucherCountInBundle]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting: bundleList=" + bundleList.size());
	            }
	        }
	    }
		
		public long fetchVoucherCountInBundle(Connection p_con, String bundleId) throws BTSLBaseException {
	        final String methodName = "fetchVoucherCountInBundle";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Entered :: bundleId-" + bundleId);
	        }
	        long count = -1;
	        
	        
	        StringBuffer strBuff= new StringBuffer();
	        		strBuff.append("SELECT sum(vbd.quantity) as count from voms_bundle_details vbd ");
	        		strBuff.append("left outer join voms_products vp on vbd.profile_id = vp.PRODUCT_ID ");
	        		strBuff.append("left outer join voms_bundle_master vbm on vbd.VOMS_BUNDLE_ID = vbm.VOMS_BUNDLE_ID ");
	        		strBuff.append(" where vp.STATUS <> ? and vbm.STATUS <> ? and vbd.VOMS_BUNDLE_ID = ? ");
	        try (PreparedStatement pstmt = p_con.prepareStatement(strBuff.toString());){
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " fetchVoucherCountInBundle() of ChannelTransferDAO :: Query :: " + strBuff);
	            }
	            // Get Prepared Statement  
	            int i=1;
	            pstmt.setString(i++, PretupsI.NO);
	            pstmt.setString(i++, PretupsI.NO);
	            pstmt.setString(i, bundleId);

	            // Execute Query
	            try( ResultSet rs = pstmt.executeQuery();)
	            {
	         
	            // Get Products Details
	            while (rs.next()) {
	            	try {
	            	count = Long.parseLong(rs.getString("count"));
	            	}catch(Exception e) {
	            		_log.errorTrace(methodName, e);
	            	}
	            } return count;
	        } 
	        }catch (SQLException sqe) {
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleInfoById]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadBundleNameIDInfo]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting: count=" + count);
	            }
	        }
	    }
		
		public HashMap loadBundleInfoById(Connection p_con) throws BTSLBaseException {
	        final String methodName = "loadBundleInfoById";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Entered ");
	        }
	        
	        
	        VoucherBundleVO voucherBundleVO = null;
	        HashMap<String,VoucherBundleVO> bundleList = new HashMap<String,VoucherBundleVO>();
	        String strBuff= " select vb.VOMS_BUNDLE_ID , vb.BUNDLE_NAME, vb.RETAIL_PRICE  from voms_bundle_master vb where vb.STATUS <> ? ";
	        try (PreparedStatement pstmt = p_con.prepareStatement(strBuff);){
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " loadBundleInfoById() of ChannelTransferDAO :: Query :: " + strBuff);
	            }
	            // Get Prepared Statement           
	            int count = 1;        
	            pstmt.setString(1, PretupsI.NO);
	            // Execute Query
	            try( ResultSet rs = pstmt.executeQuery();)
	            {
	         
	            // Get Products Details
	            while (rs.next()) {
	            	voucherBundleVO = new VoucherBundleVO();
	            	voucherBundleVO.setVomsBundleID(Long.toString(rs.getLong("VOMS_BUNDLE_ID")));
	            	voucherBundleVO.setBundleName(rs.getString("BUNDLE_NAME"));
	            	voucherBundleVO.setRetailPrice(rs.getLong("RETAIL_PRICE"));
	                // Set in list
	            	bundleList.put(voucherBundleVO.getVomsBundleID(),voucherBundleVO);
	            }
	            return bundleList;
	        } 
	        }catch (SQLException sqe) {
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[fetchVoucherCountInBundle]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
	        } catch (Exception ex) {
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[fetchVoucherCountInBundle]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
	        } finally {
	        	
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting: bundleList=" + bundleList.size());
	            }
	        }
	    }
		
		public HashMap loadVouchersSerialNosFromBatches(Connection p_con , ArrayList<VomsBatchVO> batchList , String voucher_status, String status) throws BTSLBaseException {
	        final String methodName = "loadVouchersSerialNosFromBatches";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "batchList count: " + batchList.size());
	        }
	        PreparedStatement dbPs = null;
	        ResultSet rs = null;
	        VomsVoucherVO voucherVO = null;
	        HashMap<String,ArrayList<String>> voucherSerialNo = new HashMap<String,ArrayList<String>>();
	        ArrayList<String> serialNos = new ArrayList<String>();
	        String serialNo = null , productID = null ;

	        try {
	        	int i =1;
	           /* StringBuffer sqlSelectBuf = new StringBuffer(" SELECT   vv.serial_no , vv.product_id ");
	            sqlSelectBuf.append(" FROM voms_vouchers vv, voms_products vp ");
	            sqlSelectBuf.append(" WHERE vv.status = ? AND vp.product_id = vv.product_id AND vp.status <> ? AND vv.master_serial_no is null " );

	            if (_log.isDebugEnabled()) {
	                _log.debug("loadAvailableVouchersSerialNosByProducts", "Select Query=" + sqlSelectBuf.toString());
	            }
	            dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
	            dbPs.setString(i++, voucher_status);
	            dbPs.setString(i++, status);*/
	        	
	        	StringBuffer sqlSelectBuf = new StringBuffer();
	        	int batchListS = batchList.size();
	        	
	        	for(int j = 1 ; j <= batchListS ; j++) {
	        		sqlSelectBuf.append(" SELECT * FROM ");
	        		sqlSelectBuf.append("(SELECT   vv.serial_no , vv.product_id  ");
	        		sqlSelectBuf.append("FROM voms_vouchers vv left outer join voms_products vp on  vp.product_id = vv.product_id ");
	        		sqlSelectBuf.append("left outer join voms_batches vb on vv.generation_batch_no = vb.batch_no  ");
	        		sqlSelectBuf.append("WHERE vv.status = ? AND vp.status <> ? AND vv.master_serial_no is null AND vb.batch_no = ? ");
	        		sqlSelectBuf.append("ORDER BY vv.created_on ) ");
		            if(j < batchListS)
		            	sqlSelectBuf.append(" UNION ALL ");
	        	}
	        	dbPs = p_con.prepareStatement(sqlSelectBuf.toString());
	        	for(VomsBatchVO vomsBatchVO : batchList) {
	        		dbPs.setString(i++, voucher_status);
		            dbPs.setString(i++, status);
		            dbPs.setString(i++, vomsBatchVO.getBatchNo());
	        	}
	            
	            
	            rs = dbPs.executeQuery();

	            while (rs.next()) {
	                serialNo = rs.getString("SERIAL_NO");
	                productID = rs.getString("PRODUCT_ID");
	                if(voucherSerialNo.containsKey(productID)) {
	                	serialNos = voucherSerialNo.get(productID);
	                	serialNos.add(serialNo);
	                	voucherSerialNo.put(productID , serialNos);
	                }
	                else {
	                	serialNos = new ArrayList<String>();
	                	serialNos.add(serialNo);
	                	voucherSerialNo.put(productID , serialNos);
	                }
	            }
	            
	            if (_log.isDebugEnabled()) {
	            	_log.debug(methodName, " SELECT script:= " + sqlSelectBuf);
	                _log.debug(methodName, "After executing the query loadAvailableVouchersDetails method voucherSerialNo=" + voucherSerialNo.size());
	            }
	            return voucherSerialNo;
	        } catch (SQLException sqle) {
	            _log.error("loadAvailableVouchersDetails", "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO["+methodName+ "]", "", "", "", "Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error("loadAvailableVouchersDetails", "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO["+methodName+"", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadVomsVoucherVO", "error.general.processing");
	        }// end of catch
	        finally {
	            try {
	                if (rs != null) {
	                    rs.close();
	                }
	            } catch (Exception ex) {
	                _log.error(methodName, " Exception while closing rs ex=" + ex);
	            }
	            try {
	                if (dbPs != null) {
	                    dbPs.close();
	                }
	            } catch (Exception ex) {
	                _log.error(methodName, " Exception while closing prepared statement ex=" + ex);
	            }
	            try {
	                _log.debug(methodName, " Exiting.. VomsVoucherVO=" + voucherVO);
	            } catch (Exception e) {
	                _log.error(methodName, " Exception while closing rs ex=" + e);
	            }
	            ;
	        }
	    }

		
		 /**
		 * @param con
		 * @param transferId
		 * @param userId
		 * @param userType
		 * @param roleCode
		 * @return
		 * @throws BTSLBaseException
		 */
		public boolean validateUserAllowedForApproval(Connection con, String transferId,String userId,String roleCode) throws BTSLBaseException {
		        final String methodName = "validateUserAllowedForApproval";
		        if (_log.isDebugEnabled()) {
		        	 StringBuilder debug= new StringBuilder("Entered transferId=").append(transferId).append("userId = ").append(userId);
				        debug.append(" roleCode =").append(roleCode);
		            _log.debug(methodName, debug.toString());
		        }
		        boolean res = false;
		        StringBuilder strBuff= new StringBuilder(" SELECT U.USER_ID FROM USERS U  JOIN USER_ROLES ur ");
		        strBuff.append("ON UR.ROLE_CODE = ? AND u.USER_ID=ur.USER_ID");
		        strBuff.append(" AND u.user_id IN ");
		        strBuff.append("(SELECT FROM_USER_ID FROM CHANNEL_TRANSFERS WHERE TRANSFER_ID = ?)");
		        try (PreparedStatement pstmt = con.prepareStatement(strBuff.toString());){
		            if (_log.isDebugEnabled()) {
		                _log.debug(methodName, "ChannelTransferDAO :: Query :: " + strBuff);
		            }
		            // Get Prepared Statement           
		            int index = 1;        
		            pstmt.setString(index++, roleCode);
			        pstmt.setString(index++, transferId );

		            // Execute Query
		            try( ResultSet rs = pstmt.executeQuery();)
		            {
		            if (rs.next()) {
		            	if(userId.equals(rs.getString("USER_ID")))
		            		res= true;
		            	}
		            }
		        }catch (SQLException sqe) {
		            _log.errorTrace(methodName, sqe);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[validateUserAllowedForApproval]", "", "", "", "SQL Exception:" + sqe.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
		        } catch (Exception ex) {
		            _log.errorTrace(methodName, ex);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[validateUserAllowedForApproval]", "", "", "", "Exception:" + ex.getMessage());
		            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
		        } finally {
		        	
		            if (_log.isDebugEnabled()) {
		                _log.debug(methodName, "Exiting: res=" + res);
		            }
		        }
		            return res;
		 }
		
		
		 /**
		 * @param con
		 * @param userId
		 * @param type
		 * @param status
		 * @return
		 * @throws BTSLBaseException
		 */
		public long getPendingTxnCount(Connection con,String userId,String type, String status,String roleCode) throws BTSLBaseException{
		    	final String methodName="getPendingTxnCount";
				 if (_log.isDebugEnabled())
					 _log.debug(methodName, "Entered  with userId" + userId);
		    	 PreparedStatement pstmt = null;
		         ResultSet rs = null;
	        	 long finalSuccessCount=0;
		         StringBuilder strBuff = new StringBuilder("SELECT COUNT(*) As totalCount FROM CHANNEL_TRANSFERS" );
		         strBuff.append(" JOIN USER_ROLES UR  ON UR.ROLE_CODE = ? ");
		         strBuff.append(" AND TYPE = ? AND FROM_USER_ID = ? ");
		         strBuff.append("  AND FROM_USER_ID = UR.USER_ID AND STATUS = ?");
		         if (_log.isDebugEnabled())
		        	 _log.debug(methodName, "select Query=" +strBuff);
		         try
		         {
		        	 pstmt = con.prepareStatement(strBuff.toString());	            
		        	 int i = 1;
		        	 pstmt.setString(i++, roleCode);
		        	 pstmt.setString(i++, type);
		        	 pstmt.setString(i++, userId);
		        	 pstmt.setString(i++, status);
		             rs = pstmt.executeQuery();
		             if (rs.next()) {
		            	 finalSuccessCount = rs.getLong("totalCount");
		          }
		         }
		         catch (SQLException sqle)
		 		 {
		        	 _log.error(methodName,"SQLException "+sqle.getMessage());
		        	 _log.errorTrace(methodName, sqle);
		 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
		 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		 		}//end of catch
		 		catch (Exception e)
		 		{
		 			_log.error(methodName,"Exception "+e.getMessage());
		 			_log.errorTrace(methodName, e);
		 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
		 			throw new BTSLBaseException(this, methodName, "error.general.processing");
		 		}//end of catch
		 		finally
		 		{
		 			try{
		 				if(rs!=null)
		 					rs.close();
		 				}catch(Exception e){
		 				_log.error(methodName, "Exception:e=" + e);
		 				_log.errorTrace(methodName, e);	
		 			}
		 			try{
		 				if(pstmt!=null)
		 					pstmt.close();}
		 			catch(Exception e){
		 				_log.error(methodName, "Exception:e=" + e);
		 				_log.errorTrace(methodName, e);	
		 			}
		 			if(_log.isDebugEnabled())
		 				_log.debug(methodName,"Exiting finalSuccessCount ="+finalSuccessCount);
		 		 }//end of finally
		 	    return finalSuccessCount;
		 }
		
		public TreeMap<Date, Integer> getUserBalances(Connection con,String userId, String fromDate, String toDate) throws BTSLBaseException{
	    	final String methodName="getUserBalances";
			 if (_log.isDebugEnabled())
			 {
				 _log.debug(methodName, "Entered  with userId" + userId); 
			 }
			 PreparedStatement pstmt = null;
			 PreparedStatement pstmt1 = null;
	         ResultSet rs = null;
	         ResultSet rs1 = null;
	         TreeMap<Date,Integer> balanceMap = null;
	         StringBuilder strBuff = new StringBuilder("SELECT SUM(BALANCE) as total,BALANCE_DATE FROM USER_DAILY_BALANCES" );
	         strBuff.append(" WHERE USER_ID = ? AND BALANCE_DATE BETWEEN ? AND ?");
	         strBuff.append(" GROUP BY BALANCE_DATE ");
	         if (_log.isDebugEnabled())
	        	 _log.debug(methodName, "select Query=" +strBuff);
	         try
	         {
	        	 pstmt = con.prepareStatement(strBuff.toString());	            
	        	 int i = 1;
	        	 
	        	 pstmt.setString(i++, userId);
	        	 pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(fromDate)));
	        	 pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(toDate)));
	        	 balanceMap = new TreeMap<Date,Integer>();
	        	 
	             rs = pstmt.executeQuery();
	             while (rs.next()) 
	             {
	            	Date date = rs.getDate("balance_Date");
	            	int total = rs.getInt("total");
	            	balanceMap.put(date, total);
	             }
	             if(balanceMap.size() ==0)
	             {
	            	 StringBuffer sb1 = new StringBuffer("SELECT SUM( BALANCE )as total,BALANCE_DATE FROM USER_DAILY_BALANCES WHERE BALANCE_DATE = ");
	            	 sb1.append(" (SELECT MAX(BALANCE_DATE) FROM USER_DAILY_BALANCES WHERE USER_ID = ?) GROUP BY BALANCE_DATE"); 
	            	 
	                 pstmt1 = con.prepareStatement(sb1.toString());
	            	 pstmt1.setString(1, userId);
	            	 
	            	 rs1 = pstmt1.executeQuery();
	            	 while (rs1.next()) 
		             {
		            	Date date = rs1.getDate("balance_Date");
		            	int total = rs1.getInt("total");
		            	balanceMap.put(date, total);
		             }
	             }
	         }
	         catch (SQLException sqle)
	 		 {
	        	 _log.error(methodName,"SQLException "+sqle.getMessage());
	        	 _log.errorTrace(methodName, sqle);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
	 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	 		}//end of catch
	 		catch (Exception e)
	 		{
	 			_log.error(methodName,"Exception "+e.getMessage());
	 			_log.errorTrace(methodName, e);
	 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2STransferDAO["+methodName+"]","","","","Exception:"+e.getMessage());
	 			throw new BTSLBaseException(this, methodName, "error.general.processing");
	 		}//end of catch
	 		finally
	 		{
	 			try{
	 				if(rs!=null)
	 					rs.close();
	 				if(rs1!=null)
	 					rs1.close();
	 				}catch(Exception e){
	 				_log.error(methodName, "Exception:e=" + e);
	 				_log.errorTrace(methodName, e);	
	 			}
	 			try{
	 				if(pstmt!=null)
	 					pstmt.close();
	 				
	 				if(pstmt1!=null)
	 					pstmt1.close();
	 				}
	 			catch(Exception e){
	 				_log.error(methodName, "Exception:e=" + e);
	 				_log.errorTrace(methodName, e);	
	 			}
	 	    //return balanceMap;
	 }
	         return balanceMap;
}
		
		

		
		/**
		 * This method get data for Channel to channel transfer commission info data. 
		 * 
		 * @author Subesh KCV
		 * @param p_con
		 * @param c2CTransferCommReqDTO
		 *         
		 * @return List<C2CtransferCommisionRecordVO>
		 * @throws BTSLBaseException
		 */
		public C2CtransferCommRespDTO searchC2CTransferCommissionData(Connection p_con,C2CTransferCommReqDTO  c2CTransferCommReqDTO )
				throws BTSLBaseException {
			final String methodName = "searchC2CTransferCommissionData";
			
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, c2CTransferCommReqDTO.toString());
			}
			PreparedStatement pstmt = null;
					final String sqlSelect = channelTransferQry
					.getC2CTransferCommissiondetails(c2CTransferCommReqDTO);
		    
		    
		    if (_log.isDebugEnabled()) {
		    	_log.debug(methodName, sqlSelect);
			}
		    
		    StringBuilder msg = new StringBuilder();
	       
	        List<C2CtransferCommisionRecordVO> listC2CTransferCommissionRecordVO = new ArrayList<>();
			C2CtransferCommRespDTO c2CtransferCommRespDTO = new C2CtransferCommRespDTO();
			long totalRequestedQuantity=0l;
			long totalMRP=0l;
			long totalCommission=0l;
			long totalCBC=0l;
			long totalTax3=0l;
			long totalSenderDebitQuantity=0l;
			long totalReceiverCreditQuantity=0l;
			long totalPayableAmount=0l;
			long totalNetPayableAmount=0l;
			String transferSubType=null;
	        try {
	        	
	    
	        	pstmt = p_con.prepareStatement(sqlSelect.toString());
	        	int i = 0;
	        	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
	         	
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
	        	
	        	++i;
	        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2CTransferCommReqDTO.getFromDate() +  PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues())));
	        	++i;
	        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2CTransferCommReqDTO.getToDate() + PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
	        
	        	++i;
	        	pstmt.setString(i, c2CTransferCommReqDTO.getExtnwcode());
	        	
	        	++i;
	        	pstmt.setString(i, c2CTransferCommReqDTO.getTransferCategory());
	        	++i;
	        	pstmt.setString(i, c2CTransferCommReqDTO.getTransferCategory());
	       	
	    	  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getDomain());
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
				++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	
	         	
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
				++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
	         	
	         	if(c2CTransferCommReqDTO.getDistributionType()!=null & (c2CTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.VOUCHER_PRODUCT_O2C) )){

	    			if(!c2CTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) { // In this case UI is sending values as 'V', INSTEAD OF 'ALL'
	    				   transferSubType =c2CTransferCommReqDTO.getTransferSubType();
	    			 }else { // ALL
	    				 transferSubType ="T";
	    			 }
	    			++i;
		         	pstmt.setString(i, transferSubType);
	    		} else if(c2CTransferCommReqDTO.getDistributionType()!=null & (c2CTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.STOCK) )) { 
	    			 //Incase of STOCK
	    			if(c2CTransferCommReqDTO.getTransferSubType().trim().equals("T,R,W,X")) { // In this case UI is sending values as 'T,R,W,X', INSTEAD OF 'ALL'
	    				//sbquery.append( " AND CTRF.TRANSFER_SUB_TYPE IN ('T','X','W','X') ");  <>'V'
	    				transferSubType="V";  //condition will go as <> V
	    				//sbquery.append(" AND CTRF.transfer_sub_type <> ? "); // CAN SUBSTIUTE ANY VALUE
	    			}else {
	    				transferSubType=c2CTransferCommReqDTO.getTransferSubType();  //condition will go as ui input
	    				//sbquery.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
	    			}
		         	++i;
		         	pstmt.setString(i, transferSubType);

	    		} else {
	    			
	    			// distribution all , and transfer type can be 'R'  or 'W'  or 'T,V'
	    			if(c2CTransferCommReqDTO.getTransferSubType()!=null  && !c2CTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) {
	    			String trfArray[]=null;
	    			if (c2CTransferCommReqDTO.getTransferSubType()!=null && c2CTransferCommReqDTO.getTransferSubType().indexOf(",") >0 ) {
	    				trfArray=c2CTransferCommReqDTO.getTransferSubType().split(",");
	    			}else {
	    				trfArray= new String[1];
	    				trfArray[0]=c2CTransferCommReqDTO.getTransferSubType();
	    			}
	    			
	    			 for (int k=0;k<trfArray.length;k++) {
		    		    ++i;
		    		    pstmt.setString(i, trfArray[k]);
	    			 }
	    			}		 
	    		}	
	         	
	         
		     	
			  	
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getGeography());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getGeography());
			  	
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
			  	
			  	if(c2CTransferCommReqDTO.getTransferInout()!=null && !c2CTransferCommReqDTO.getTransferInout().equals(PretupsI.ALL)) {
			  		++i;
				  	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
			  	}
			  	
			  	
			  	
			
			  	
			  	if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getSenderMobileNumber())) {
			  		++i;
				  	pstmt.setString(i, c2CTransferCommReqDTO.getSenderMobileNumber());
			  	}
			  	
			  	if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getReceiverMobileNumber())) {
			  		++i;
				  	pstmt.setString(i, c2CTransferCommReqDTO.getReceiverMobileNumber());
			  	} 
			  	
			  	
		 				
			 try(ResultSet rs = pstmt.executeQuery();)
         	{   
		     
             while (rs.next()) { 
               	C2CtransferCommisionRecordVO c2CtransferCommisionRecordVO = new C2CtransferCommisionRecordVO();
               	String dateformat =BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("TRANSFER_DATE") + " " +rs.getTime("TRANSFER_DATE")));
               	c2CtransferCommisionRecordVO.setInitiatorUserName(rs.getString("initiator_user"));
    	       	c2CtransferCommisionRecordVO.setTransdateTime(dateformat);
	           	c2CtransferCommisionRecordVO.setTransactionID(rs.getString("TRANSFER_ID"));
	           	c2CtransferCommisionRecordVO.setSenderName(rs.getString("fromUserName"));
	           	c2CtransferCommisionRecordVO.setSenderMsisdn(rs.getString("from_Msisdn"));
	           	c2CtransferCommisionRecordVO.setSenderCategory(rs.getString("senderCategoryName"));
	           	c2CtransferCommisionRecordVO.setSenderDebitQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("SENDER_DEBIT_QUANTITY"))));
	           	c2CtransferCommisionRecordVO.setReceiverName(rs.getString("ReceiverName"));
	           	c2CtransferCommisionRecordVO.setReceiverMsisdn(rs.getString("ReceiverMSISDN"));
	           	c2CtransferCommisionRecordVO.setReceiverCategory(rs.getString("receiverCategoryName"));
	           	c2CtransferCommisionRecordVO.setReceiverCreditQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("RECEIVER_CREDIT_QUANTITY"))));
	           	c2CtransferCommisionRecordVO.setProductName(rs.getString("productName"));
	           	c2CtransferCommisionRecordVO.setTransferInOut(rs.getString("TransferINOUT"));
	           	c2CtransferCommisionRecordVO.setTransferSubType(rs.getString("transferSubType"));
	           	c2CtransferCommisionRecordVO.setSource(rs.getString("source"));
	           	c2CtransferCommisionRecordVO.setCommission(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("commission_value"))));
	           	c2CtransferCommisionRecordVO.setTax3(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax3_Value"))));
	           	c2CtransferCommisionRecordVO.setPayableAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("payableAmount"))));
	           	c2CtransferCommisionRecordVO.setNetPayableAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("netPayableAmount"))));
	           	c2CtransferCommisionRecordVO.setRequestedQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("transfer_mrp"))));
	           	c2CtransferCommisionRecordVO.setDenomination(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("mrp"))));
	         	c2CtransferCommisionRecordVO.setCumulativeBaseCommission(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("otf_amount"))));
	         	c2CtransferCommisionRecordVO.setTransactionStatus(rs.getString("TransactionStatus"));
	         	c2CtransferCommisionRecordVO.setTransferCategory(rs.getString("trf_cat_name"));
	         	c2CtransferCommisionRecordVO.setRequestGateway(rs.getString("request_Gateway_Desc"));
	         	c2CtransferCommisionRecordVO.setDistributionType(rs.getString("DISTRIBUTION_TYPE"));
	         	c2CtransferCommisionRecordVO.setSenderPreviousStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("SENDER_PREVIOUS_STOCK"))));
	         	c2CtransferCommisionRecordVO.setSenderPostStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("SENDER_POST_STOCK"))));
	         	c2CtransferCommisionRecordVO.setReceiverPostStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("RECEIVER_POST_STOCK"))));
	         	c2CtransferCommisionRecordVO.setReceiverPreviousStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("RECEIVER_PREVIOUS_STOCK"))));
	         	c2CtransferCommisionRecordVO.setModifiedOn(rs.getString("modified_ON"));
	         	c2CtransferCommisionRecordVO.setRequestedSource(rs.getString("source"));
	         	c2CtransferCommisionRecordVO.setTax1(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax1_value"))));
	         	c2CtransferCommisionRecordVO.setTax2(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax2_value"))));
	         	
	           	 totalRequestedQuantity=totalRequestedQuantity + rs.getLong("transfer_mrp");
				 totalMRP= totalMRP +rs.getLong("mrp");
				 totalCommission= totalCommission +  rs.getLong("commission_value");
				 totalCBC = totalCBC + rs.getLong("otf_amount");
			     totalTax3= totalTax3 + rs.getLong("tax3_Value");
				 totalSenderDebitQuantity = totalSenderDebitQuantity + rs.getLong("SENDER_DEBIT_QUANTITY");
				 totalReceiverCreditQuantity= totalReceiverCreditQuantity + rs.getLong("RECEIVER_CREDIT_QUANTITY");
				 totalPayableAmount= totalPayableAmount + rs.getLong("payableAmount");
				 totalNetPayableAmount = totalNetPayableAmount + rs.getLong("netPayableAmount");
	           	
	           	
	           	
	           	listC2CTransferCommissionRecordVO.add(c2CtransferCommisionRecordVO);
	           	
             }
            	c2CtransferCommRespDTO.setListC2CTransferCommRecordVO(listC2CTransferCommissionRecordVO);
                 C2CtransferCommSummryData c2CtransferCommSummryData  = new C2CtransferCommSummryData(); 	
                 c2CtransferCommSummryData.setTotalCBC(String.valueOf(PretupsBL
						.getDisplayAmount(totalCBC)));
            	
                 c2CtransferCommSummryData.setTotalRequestedQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(totalRequestedQuantity)));
                 c2CtransferCommSummryData.setTotalMRP(String.valueOf(PretupsBL
 						.getDisplayAmount(totalMRP)));
                 c2CtransferCommSummryData.setTotalCommission(String.valueOf(PretupsBL
  						.getDisplayAmount(totalCommission)));
                 
                 c2CtransferCommSummryData.setTotalTax3(String.valueOf(PretupsBL
   						.getDisplayAmount(totalTax3)));
                 c2CtransferCommSummryData.setTotalSenderDebitQuantity(String.valueOf(PretupsBL
    						.getDisplayAmount(totalSenderDebitQuantity))); 
                 c2CtransferCommSummryData.setTotalReceiverCreditQuantity(String.valueOf(PretupsBL
 						.getDisplayAmount(totalReceiverCreditQuantity)));  
                 
                 c2CtransferCommSummryData.setTotalPayableAmount(String.valueOf(PretupsBL
  						.getDisplayAmount(totalPayableAmount)));  
                 c2CtransferCommSummryData.setTotalNetPayableAmount(String.valueOf(PretupsBL
  						.getDisplayAmount(totalNetPayableAmount)));
            	 c2CtransferCommRespDTO.setC2CtransferCommSummryData(c2CtransferCommSummryData);
            	
         	}	
	          
	        } catch (SQLException sqle) {
	        	msg.setLength(0);
	        	msg.append(SQL_EXCEPTION);
	        	msg.append(sqle.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchC2CTransferCommissionData]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch */
	        catch (Exception e) {
	        	msg.setLength(0);
	        	msg.append(EXCEPTION);
	        	msg.append(e.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchC2CTransferCommissionData]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	        	try{
	                if (pstmt!= null){
	                	pstmt.close();
	                }
	              }
	              catch (SQLException e){
	            	  _log.error("An error occurred closing prepared statement.", e);
	              }
	        	}
	            if (_log.isDebugEnabled()) {
	            	_log.debug(methodName, "Exiting userName:" + c2CtransferCommRespDTO);
	            }
	            
	            return c2CtransferCommRespDTO;
	    }		
		
		
		
		/**
		 * This method get data for Channel to channel transfer commission info data. 
		 * 
		 * @author Subesh KCV
		 * @param p_con
		 * @param GetO2CTransfAcknReqVO
		 *         
		 * @return List<C2CtransferCommisionRecordVO>
		 * @throws BTSLBaseException
		 */
		public List<GetO2CTransferAckDTO> searchO2CTransferAcknowlegeDetails(Connection p_con,O2CTransfAckDownloadReqDTO  getO2CTransfAcknReqVO )
				throws BTSLBaseException {
			final String methodName = "searchO2CTransferAcknowlegeDetails";
			
			List<GetO2CTransferAckDTO> listO2CTansferAckDTO = new ArrayList<>();
			C2CtransferCommRespDTO c2CtransferCommRespDTO = new C2CtransferCommRespDTO();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, getO2CTransfAcknReqVO.toString());
			}
			PreparedStatement pstmt = null;
					final String sqlSelect = channelTransferQry
					.getO2CTransferAcknowldgementDetails(getO2CTransfAcknReqVO);
		    
		    
		    if (_log.isDebugEnabled()) {
		    	_log.debug(methodName, sqlSelect);
			}
		    
		    StringBuilder msg = new StringBuilder();
	    	
	        try {
	        	
	    
	        	pstmt = p_con.prepareStatement(sqlSelect.toString());
	        	int i = 0;
	        	++i;
	         	pstmt.setString(i, getO2CTransfAcknReqVO.getTransactionID());
	         	++i;
	         	pstmt.setString(i, getO2CTransfAcknReqVO.getExtnwcode());
		 				
			 try(ResultSet rs = pstmt.executeQuery();)
         	{   
		     
             while (rs.next()) { 
            	 GetO2CTransferAckDTO getO2CTransferAckDTO = new GetO2CTransferAckDTO();
               	String dateformat =BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("TRANSFER_DATE") + " " +rs.getTime("TRANSFER_DATE")));
               	getO2CTransferAckDTO.setFromUserID(rs.getString("from_user_id"));
               	getO2CTransferAckDTO.setToUserID(rs.getString("to_user_id"));
               	getO2CTransferAckDTO.setDateTime(dateformat);
               	getO2CTransferAckDTO.setTransactionID(rs.getString("transfer_id"));
               	getO2CTransferAckDTO.setUserName(rs.getString("user_name"));
               	getO2CTransferAckDTO.setStatus(rs.getString("status"));
               	getO2CTransferAckDTO.setDomain(rs.getString("domain_name"));
               	getO2CTransferAckDTO.setCategory(rs.getString("category_name"));
               	getO2CTransferAckDTO.setGeography(rs.getString("grph_domain_name"));
               	getO2CTransferAckDTO.setMobileNumber(rs.getString("msisdn"));
               	getO2CTransferAckDTO.setNetworkName(rs.getString("network_name"));
               	getO2CTransferAckDTO.setCommissionProfile(rs.getString("comm_profile_set_name"));  
               	getO2CTransferAckDTO.setTransferProfile(rs.getString("profile_name"));
               	getO2CTransferAckDTO.setTransferType(rs.getString("transfer_type"));
               	getO2CTransferAckDTO.setTransferCategory(rs.getString("transfer_category"));
               	getO2CTransferAckDTO.setTransNumberExternal(rs.getString("ext_txn_no"));
               	getO2CTransferAckDTO.setTransDateExternal(rs.getString("ext_txn_date"));
               	getO2CTransferAckDTO.setReferenceNumber(rs.getString("reference_no"));
               	getO2CTransferAckDTO.setErpCode(rs.getString("external_code"));
               	StringBuilder sb = new StringBuilder();
               	 if(!BTSLUtil.isNullString(rs.getString("address1")) ) {
               		sb.append(rs.getString("address1"));
               		sb.append(PretupsI.COMMA);
               	  }
               	if(!BTSLUtil.isNullString(rs.getString("address2")) ) {
               		sb.append(rs.getString("address2"));
               		sb.append(PretupsI.COMMA);
               	  }

            	if(!BTSLUtil.isNullString(rs.getString("city")) ) {
               		sb.append(rs.getString("city"));
               		sb.append(PretupsI.COMMA);
               	  }
            	if(!BTSLUtil.isNullString(rs.getString("state")) ) {
               		sb.append(rs.getString("state"));
               		sb.append(PretupsI.COMMA);
               	  }
            	if(!BTSLUtil.isNullString(rs.getString("country")) ) {
               		sb.append(rs.getString("country"));
               		sb.append(PretupsI.COMMA);
               	  }
            	String address="";
            	if(sb.toString().length()>0) {
	              	int lastloc =sb.toString().length()-1;
	               	 address = sb.deleteCharAt(lastloc).toString();
            	}
               	
               	getO2CTransferAckDTO.setAddress(address);
               	getO2CTransferAckDTO.setProductShortCode(rs.getString("product_short_code"));
               	getO2CTransferAckDTO.setProductName(rs.getString("product_name"));
               	getO2CTransferAckDTO.setDenomination(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("mrp"))));
               	getO2CTransferAckDTO.setQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("required_quantity"))) );
               	getO2CTransferAckDTO.setApprovedQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("approved_quantity"))));
               	getO2CTransferAckDTO.setLevel1ApprovedQuantity(
               			String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("first_level_approved_quantity")))
               			);
               	getO2CTransferAckDTO.setLevel2ApprovedQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("second_level_approved_quantity")))
               			);
               	getO2CTransferAckDTO.setLevel3ApprovedQuantity(
               			String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("third_level_approved_quantity"))));
               	
               	getO2CTransferAckDTO.setTax1Rate(rs.getString("tax1_rate"));
               	getO2CTransferAckDTO.setTax1Type(rs.getString("tax1_type"));
               	getO2CTransferAckDTO.setTax1Amount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax1_value"))));
               	
               	getO2CTransferAckDTO.setTax2Rate(rs.getString("tax2_rate"));
               	getO2CTransferAckDTO.setTax2Type(rs.getString("tax2_type"));
               	getO2CTransferAckDTO.setTax2Amount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax2_value"))) );
               	
               	getO2CTransferAckDTO.setTds( String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax3_value"))));
               	
               	getO2CTransferAckDTO.setCommisionRate(rs.getString("commission_rate"));
               	getO2CTransferAckDTO.setCommisionType(rs.getString("commission_type"));
               	
               	getO2CTransferAckDTO.setCommisionAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("commission_value"))));
               	
               	getO2CTransferAckDTO.setReceiverCreditQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("receiver_credit_quantity"))));
               	
               	getO2CTransferAckDTO.setCbcRate(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("otf_rate"))));
               	getO2CTransferAckDTO.setCbcType(rs.getString("otf_type"));
               	getO2CTransferAckDTO.setCbcAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("otf_amount"))));
               	
             	getO2CTransferAckDTO.setDenominationAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("user_unit_price"))));
             	getO2CTransferAckDTO.setPayableAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("payable_amount"))));
             
             	getO2CTransferAckDTO.setNetAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("net_payable_amount"))));
             	getO2CTransferAckDTO.setPaymentMode(rs.getString("pmt_inst_source"));
             	getO2CTransferAckDTO.setPaymentInstrumentNumber(rs.getString("pmt_inst_no"));
             	getO2CTransferAckDTO.setPaymentInstrumentDate(rs.getString("pmt_inst_date"));
             	getO2CTransferAckDTO.setFirstApprovedRemarks(rs.getString("first_approver_remarks"));
             	getO2CTransferAckDTO.setSecondApprovedRemarks(rs.getString("second_approver_remarks"));
             	getO2CTransferAckDTO.setThirdApprovedRemarks(rs.getString("third_approver_remarks"));
             	
             	if(getO2CTransfAcknReqVO.getDistributionType()!=null & getO2CTransfAcknReqVO.getDistributionType().trim().equals(PretupsI.VOUCHER) ) {	
	             	getO2CTransferAckDTO.setVoucherBatchNumber(rs.getString("batch_no"));
	             	getO2CTransferAckDTO.setVomsProductName(rs.getString("voms_product_name"));
	             	getO2CTransferAckDTO.setBatchType(rs.getString("batch_Type_desc"));
	             	getO2CTransferAckDTO.setTotalNoofVouchers(rs.getString("total_no_of_vouchers"));
	             	getO2CTransferAckDTO.setFromSerialNumber(rs.getString("from_serial_no"));
	             	getO2CTransferAckDTO.setToSerialNumber(rs.getString("to_serial_no"));
             	}
             	
             	listO2CTansferAckDTO.add(getO2CTransferAckDTO);
	           	
             }
            	
            	
         	}	
	          
	        } catch (SQLException sqle) {
	        	msg.setLength(0);
	        	msg.append(SQL_EXCEPTION);
	        	msg.append(sqle.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchC2CTransferCommissionData]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch */
	        catch (Exception e) {
	        	msg.setLength(0);
	        	msg.append(EXCEPTION);
	        	msg.append(e.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchC2CTransferCommissionData]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	        	try{
	                if (pstmt!= null){
	                	pstmt.close();
	                }
	              }
	              catch (SQLException e){
	            	  _log.error("An error occurred closing prepared statement.", e);
	              }
	        	}
	            if (_log.isDebugEnabled()) {
	            	_log.debug(methodName, "Exiting userName:" + c2CtransferCommRespDTO);
	            }
	            
	            return listO2CTansferAckDTO;
	    }	
		
		
		/**
		 * This method get data for  Operator to channer transfer details info data. 
		 * 
		 * @author Subesh KCV
		 * @param p_con
		 * @param O2CTransferDetailsReqDTO
		 *         
		 * @return List<O2CtransferDetRecordVO>
		 * @throws BTSLBaseException
		 */
		public O2CtransferDetRespDTO searchO2CTransferDetails(Connection p_con,O2CTransferDetailsReqDTO  o2CTransferDetailsReqDTO )
				throws BTSLBaseException {
			final String methodName = "searchO2CTransferDetails";
			
			List<O2CtransferDetRecordVO> listO2CTansferAckDTO = new ArrayList<>();
			O2CtransferDetRespDTO o2CtransferDetRespDTO = new O2CtransferDetRespDTO();
			
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, o2CTransferDetailsReqDTO.toString());
			}
			PreparedStatement pstmt = null;
					final String sqlSelect = channelTransferQry
					.searchO2CTransferDetails(o2CTransferDetailsReqDTO);
		    Long totalRequestedQuantity = 0l;
		    Long totalRecevierCreditQuantity =0l;
		    Long totalSenderDebitQuantity =0l;
		    Long totalCommission =0l;
		    Long totalTax1 =0l;
		    Long totalTax2 =0l;
		    Long totalTax3 =0l;
		    Long totalPayableAmount =0l;
		    Long totalNetPayableAmount =0l;
		    Long totalCBCAmount =0l;
		    if (_log.isDebugEnabled()) {
		    	_log.debug(methodName, sqlSelect);
			}
		    
		    StringBuilder msg = new StringBuilder();
		    String transferSubType=null;
			 List keyValueList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
			   HashMap<String, String> transferTypemap =(HashMap<String, String>) keyValueList.stream()
			      .collect(Collectors.toMap(ListValueVO::getValue,ListValueVO::getLabel)); 
	    	
	        try {
	        	pstmt = p_con.prepareStatement(sqlSelect.toString());
	        	int i = 0;
	        	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getUserId());
	         	++i;
	        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(o2CTransferDetailsReqDTO.getFromDate() + PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues() )));
	        	++i;
	        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(o2CTransferDetailsReqDTO.getToDate() + PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
	         	
	        	++i;	         	
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getExtnwcode());
	        	++i;	         	
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getTransferCategory());
	         	++i;	         	
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getTransferCategory());
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getDomain());
	        	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getCategoryCode());
	        	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getCategoryCode());
	        	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getUser());
	          	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getUser());
	         	if(o2CTransferDetailsReqDTO.getDistributionType()!=null & (o2CTransferDetailsReqDTO.getDistributionType().trim().equals(PretupsI.VOUCHER_PRODUCT_O2C) )){

	    			if(!o2CTransferDetailsReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) { // In this case UI is sending values as 'V', INSTEAD OF 'ALL'
	    				   transferSubType =o2CTransferDetailsReqDTO.getTransferSubType();
	    			 }else { // ALL
	    				 transferSubType ="T";
	    			 }
	    			++i;
		         	pstmt.setString(i, transferSubType);
	    		} else if(o2CTransferDetailsReqDTO.getDistributionType()!=null & (o2CTransferDetailsReqDTO.getDistributionType().trim().equals(PretupsI.STOCK) )) { 
	    			 //Incase of STOCK
	    			if(o2CTransferDetailsReqDTO.getTransferSubType().trim().equals("T,R,W,X")) { // In this case UI is sending values as 'T,R,W,X', INSTEAD OF 'ALL'
	    				//sbquery.append( " AND CTRF.TRANSFER_SUB_TYPE IN ('T','X','W','X') ");  <>'V'
	    				transferSubType="V";  //condition will go as <> V
	    				//sbquery.append(" AND CTRF.transfer_sub_type <> ? "); // CAN SUBSTIUTE ANY VALUE
	    			}else {
	    				transferSubType=o2CTransferDetailsReqDTO.getTransferSubType();  //condition will go as ui input
	    				//sbquery.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
	    			}
		         	++i;
		         	pstmt.setString(i, transferSubType);

	    		} else {
	    			
	    			// distribution all , and transfer type can be 'R'  or 'W'  or 'T,V'
	    			if(o2CTransferDetailsReqDTO.getTransferSubType()!=null  && !o2CTransferDetailsReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) {
	    			String trfArray[]=null;
	    			if (o2CTransferDetailsReqDTO.getTransferSubType()!=null && o2CTransferDetailsReqDTO.getTransferSubType().indexOf(",") >0 ) {
	    				trfArray=o2CTransferDetailsReqDTO.getTransferSubType().split(",");
	    			}else {
	    				trfArray= new String[1];
	    				trfArray[0]=o2CTransferDetailsReqDTO.getTransferSubType();
	    			}
	    			
	    			 for (int k=0;k<trfArray.length;k++) {
		    		    ++i;
		    		    pstmt.setString(i, trfArray[k]);
	    			 }
	    			}		 
	    		}	
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getGeography());
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getGeography());
	         	++i;
	         	pstmt.setString(i, o2CTransferDetailsReqDTO.getUserId());

	         	
	         	
	         	//Below parameters for Union query
	         	
			 try(ResultSet rs = pstmt.executeQuery();)
         	{   
				 String paymentInstumentDate=null;   
             while (rs.next()) { 
            	 O2CtransferDetRecordVO o2CtransferDetRecordVO = new O2CtransferDetRecordVO();
            	   	String dateformat =BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("transfer_date") + " " +rs.getTime("transfer_date")));
            	   	o2CtransferDetRecordVO.setTransdateTime(dateformat);
            	   	if(rs.getDate("close_date")!=null) {
	            	   	String closeDateFormat =BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("close_date") + " " +rs.getTime("close_date")));
	            	   	o2CtransferDetRecordVO.setCloseDate(closeDateFormat);
            	   	}
            	   	if(rs.getDate("CREATED_ON")!=null) {
            	   	String createdOn =BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("CREATED_ON") + " " +rs.getTime("CREATED_ON")));
            	   	o2CtransferDetRecordVO.setCreatedOn(createdOn);
            	   	}
            	   	o2CtransferDetRecordVO.setTransactionID(rs.getString("transfer_id"));
            	   	o2CtransferDetRecordVO.setTransactionStatus(rs.getString("status"));
            	   	
            	   	if(rs.getString("trf_sub_type")!=null && rs.getString("trf_sub_type").equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW) ) {
            	   		o2CtransferDetRecordVO.setFirstLevelApprovedQuantity("-");
                	   	o2CtransferDetRecordVO.setSecondLevelApprovedQuantity("-");
                	   	o2CtransferDetRecordVO.setThirdLevelApprovedQuantity("-");
            	   	}else {
            	   	o2CtransferDetRecordVO.setFirstLevelApprovedQuantity(String.valueOf(rs.getLong("first_level_approved_qty")));
            	   	o2CtransferDetRecordVO.setSecondLevelApprovedQuantity(String.valueOf(rs.getLong("second_level_approved_qty")));
            	   	o2CtransferDetRecordVO.setThirdLevelApprovedQuantity(String.valueOf(rs.getLong("second_level_approved_qty")));
            	   	}
            	   	if(rs.getString("from_user")!=null && rs.getString("from_user").trim().length()>0 ) {
            	   		o2CtransferDetRecordVO.setSenderName(rs.getString("from_user")); 
            	   	}else {
            	   		o2CtransferDetRecordVO.setSenderName(PretupsI.MOBILE_OPERATOR);
            	   	}
            	   	o2CtransferDetRecordVO.setSenderMsisdn(rs.getString("from_msisdn"));
            	   	o2CtransferDetRecordVO.setRequestGateWay(rs.getString("request_Gateway_Desc"));
            	   	o2CtransferDetRecordVO.setRequestedQuantity(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("requested_quantity")) ));
            	   	totalRequestedQuantity=totalRequestedQuantity+rs.getLong("requested_quantity");
            	   	o2CtransferDetRecordVO.setDomainName(rs.getString("DOMAIN_NAME"));           	   	//o2CtransferDetRecordVO.setReceiverName(rs.getString("to_user"));
            	   	
            	   	if(rs.getString("to_user")!=null && rs.getString("to_user").trim().length()>0 ) {
            	   		o2CtransferDetRecordVO.setReceiverName(rs.getString("to_user")); 
            	   	}else {
            	   		o2CtransferDetRecordVO.setReceiverName(PretupsI.MOBILE_OPERATOR);
            	   	}
            	   	
            	   	o2CtransferDetRecordVO.setReceiverMsisdn(rs.getString("to_msisdn"));
            	   	
            	   	o2CtransferDetRecordVO.setReceiverQuantity(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("receiver_credit_quantity")) ));
            	   	
            	   	o2CtransferDetRecordVO.setSenderDebitQuantity(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("sender_debit_quantity"))));
            	    totalRecevierCreditQuantity =totalRecevierCreditQuantity+rs.getLong("receiver_credit_quantity");
        		    totalSenderDebitQuantity =totalSenderDebitQuantity+rs.getLong("sender_debit_quantity");
        		   
            	   	o2CtransferDetRecordVO.setTransferCategory(rs.getString("trf_cat_name"));
            	   	String trf_subType = rs.getString("trf_sub_type");
            	   	if(trf_subType!=null ) { 
	            	   	 if(!PretupsI.TRANSFER_SUB_TYPE_VOUCHER.equals(trf_subType.trim()) ) {
	            	   		 o2CtransferDetRecordVO.setTransferSubType(transferTypemap.get(trf_subType));
	            	   	 }else {
		            	   o2CtransferDetRecordVO.setTransferSubType(transferTypemap.get(PretupsI.TRFT_TRANSFER));	 
	            	   	 }
            	   	}
            	   	//o2CtransferDetRecordVO.setTransferSubType(rs.getString("transfer_sub_type"));
            	   	o2CtransferDetRecordVO.setDistributionType(rs.getString("DISTRIBUTION_TYPE"));
            	   	
            	   	String modifiedOn="";
            	   	if(rs.getDate("modified_on")!=null) {
            	   		modifiedOn =	BTSLUtil.getDateTimeStringFromDate(rs.getDate("modified_on"), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
            	   	}
            	   	o2CtransferDetRecordVO.setModifiedOn(modifiedOn);
            	   	o2CtransferDetRecordVO.setProductName(rs.getString("product_name"));
            	   	o2CtransferDetRecordVO.setExternalTransferNumber(rs.getString("ext_txn_no"));
            	   	String externalTransDate="";
            	   	 if(rs.getDate("ext_txn_date")!=null &&  rs.getTime("ext_txn_date")!=null) {
            	   		externalTransDate =	BTSLUtil.getDateTimeStringFromDate(rs.getDate("ext_txn_date"), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
            	   	 }
            	   	o2CtransferDetRecordVO.setExternalTransferDate(externalTransDate);
            	   	if(rs.getString("trans_mode")!=null && rs.getString("trans_mode").trim().equals("N")) { 
            	   		o2CtransferDetRecordVO.setTransactionMode("NORMAL");
            	   	}else {
            	   		o2CtransferDetRecordVO.setTransactionMode("AUTO");		
            	   	}
            	   	
            	   	
            	   	
            	   	if(rs.getDate("pmt_inst_date")!=null &&  rs.getTime("pmt_inst_date")!=null) {
            	   		//paymentInstumentDate=	BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("pmt_inst_date") + " " +rs.getTime("pmt_inst_date")));
            	   		paymentInstumentDate=BTSLUtil.getDateTimeStringFromDate(rs.getDate("pmt_inst_date"), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
            	   	}
            	   	o2CtransferDetRecordVO.setPaymentInstDate(paymentInstumentDate);
            	   	o2CtransferDetRecordVO.setPaymentInstType(rs.getString("pmt_inst_type"));
            	   	o2CtransferDetRecordVO.setPaymentInstNumber(rs.getString("pmt_inst_no"));
            	   	
            	   	o2CtransferDetRecordVO.setRequestedQuantity(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("required_quantity")) ));
            	   	
            	   	if(rs.getString("trf_sub_type")!=null && rs.getString("trf_sub_type").equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW) ) {
            	   			o2CtransferDetRecordVO.setApprovedQuantity("-");	
            	   	}else {
            	   			o2CtransferDetRecordVO.setApprovedQuantity(String.valueOf(
    						rs.getLong("second_level_approved_qty")));  
            	   	}
              	
            	   	o2CtransferDetRecordVO.setCommission(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("commission_value")) ));
            	   	o2CtransferDetRecordVO.setCumulativeBaseCommission(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("commision_quantity"))));
            	   	o2CtransferDetRecordVO.setTax1(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("tax1_value"))));
            	   	o2CtransferDetRecordVO.setTax2(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("tax2_value"))));
            	   	o2CtransferDetRecordVO.setTax3(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("tax3_value"))));
            	   	o2CtransferDetRecordVO.setPayableAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("payable_amount"))));
            	   	o2CtransferDetRecordVO.setNetPayableAmount(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("net_payable_amount"))));
            	   	
            	   	
            	     totalCommission =totalCommission+rs.getLong("commission_value");
        		     totalTax1 =totalTax1 +rs.getLong("tax1_value");
        		    totalTax2 =totalTax2 + rs.getLong("tax2_value");
        		     totalTax3 =totalTax3 + rs.getLong("tax3_value");
        		     totalPayableAmount =totalPayableAmount+rs.getLong("payable_amount");
        		     totalNetPayableAmount =totalNetPayableAmount+ rs.getLong("net_payable_amount");
        		     if(rs.getString("CBC_AMOUNT")!=null) { 
        		     totalCBCAmount=totalCBCAmount+Long.valueOf(rs.getString("CBC_AMOUNT"));
        		     }
            	   	
            	   	
            	   	
            	   	o2CtransferDetRecordVO.setInitiatorRemarks(StringEscapeUtils.escapeCsv(rs.getString("Channel_user_remarks")));
            	   	o2CtransferDetRecordVO.setApprover1Remarks(StringEscapeUtils.escapeCsv(rs.getString("first_approver_remarks")));
            	   	o2CtransferDetRecordVO.setApprover2Remarks(StringEscapeUtils.escapeCsv(rs.getString("second_approver_remarks")));
            	   	o2CtransferDetRecordVO.setApprover3Remarks(StringEscapeUtils.escapeCsv(rs.getString("third_approver_remarks")));
            	   	Long cbc =0l;
            	   	if(rs.getString("CBC_AMOUNT")!=null) {
            	   		cbc = Long.valueOf(rs.getString("CBC_AMOUNT"));
            	   	}
            	   	o2CtransferDetRecordVO.setCumulativeBaseCommission(String.valueOf(PretupsBL
    						.getDisplayAmount(cbc)));
         	   	o2CtransferDetRecordVO.setReceiverPreviousBalance(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("receiver_PRETVIOUS_BALANCE"))));
            	   	o2CtransferDetRecordVO.setReceiverPostBalance(String.valueOf(PretupsBL
    						.getDisplayAmount(rs.getLong("receiver_Post_BALANCE"))));
                 	
                 	if(o2CTransferDetailsReqDTO.getDistributionType()!=null & (o2CTransferDetailsReqDTO.getDistributionType().trim().equals(PretupsI.VOUCHER_PRODUCT_O2C) ||  o2CTransferDetailsReqDTO.getDistributionType().equals(PretupsI.ALL)  ) ) {
                 		
                        if(null==rs.getString("batch_no")) {
                        	o2CtransferDetRecordVO.setVoucherBatchNumber("-");
	                 		o2CtransferDetRecordVO.setVomsProductName("-");
	                 		o2CtransferDetRecordVO.setBatchType("-");
	                 		o2CtransferDetRecordVO.setTotalNoofVouchers("-");
	                 		o2CtransferDetRecordVO.setFromSerialNumber("-");
	                 		o2CtransferDetRecordVO.setToSerialNumber("-");
	                 		o2CtransferDetRecordVO.setVoucherSegment("-");
	                 		o2CtransferDetRecordVO.setVoucherType("-");
	                 		o2CtransferDetRecordVO.setVoucherDenomination("-");
                        }else {
                 		o2CtransferDetRecordVO.setVoucherBatchNumber(rs.getString("batch_no"));
                 		o2CtransferDetRecordVO.setVomsProductName(rs.getString("voms_product_name"));
                 		o2CtransferDetRecordVO.setBatchType(rs.getString("batch_Type_desc"));
                 		o2CtransferDetRecordVO.setTotalNoofVouchers(rs.getString("total_no_of_vouchers"));
                 		o2CtransferDetRecordVO.setFromSerialNumber(rs.getString("from_serial_no"));
                 		o2CtransferDetRecordVO.setToSerialNumber(rs.getString("to_serial_no"));
                 		o2CtransferDetRecordVO.setVoucherSegment(rs.getString("VOUCHER_SEGMENT_NAME"));
                 		o2CtransferDetRecordVO.setVoucherType(rs.getString("VOUCHER_TYPE"));
                 		if(trf_subType.trim().equals(PretupsI.TRANSFER_SUB_TYPE_VOUCHER) ) {
                 		o2CtransferDetRecordVO.setVoucherDenomination(String.valueOf(PretupsBL
        						.getDisplayAmount(rs.getLong("VOUCHER_DENOMINATION"))));
                 		}
                        }
                 	}
             	listO2CTansferAckDTO.add(o2CtransferDetRecordVO);
	           	
             }
             o2CtransferDetRespDTO.setListO2CTransferCommRecordVO(listO2CTansferAckDTO);
             O2CtransferDetTotSummryData o2CtransferDetTotSummryData = new O2CtransferDetTotSummryData();
             o2CtransferDetTotSummryData.setTotalRequestedQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(totalRequestedQuantity)));
             o2CtransferDetTotSummryData.setTotalPayableAmount(String.valueOf(PretupsBL
						.getDisplayAmount(totalPayableAmount)));
             o2CtransferDetTotSummryData.setTotalNetPayableAmount(String.valueOf(PretupsBL
						.getDisplayAmount(totalNetPayableAmount)));
             o2CtransferDetTotSummryData.setTotalReceiverCreditQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(totalRecevierCreditQuantity))); 
             o2CtransferDetTotSummryData.setTotalSenderDebitQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(totalSenderDebitQuantity)));
             o2CtransferDetTotSummryData.setTotalCommission(String.valueOf(PretupsBL
						.getDisplayAmount(totalCommission)));    
             o2CtransferDetTotSummryData.setTotaltax1(String.valueOf(PretupsBL
						.getDisplayAmount(totalTax1)));
             o2CtransferDetTotSummryData.setTotaltax2(String.valueOf(PretupsBL
						.getDisplayAmount(totalTax2))); 
             o2CtransferDetTotSummryData.setTotaltax3(String.valueOf(PretupsBL
						.getDisplayAmount(totalTax2)));
             o2CtransferDetTotSummryData.setTotalCBCAmount(String.valueOf(PretupsBL
						.getDisplayAmount(totalCBCAmount)));
 		      
             o2CtransferDetRespDTO.setO2CtransferDetTotSummryData(o2CtransferDetTotSummryData);
             
            	
         	}	
	          
	        } catch (SQLException sqle) {
	        	msg.setLength(0);
	        	msg.append(SQL_EXCEPTION);
	        	msg.append(sqle.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchO2CTransferDetails]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch */
	        catch (Exception e) {
	        	msg.setLength(0);
	        	msg.append(EXCEPTION);
	        	msg.append(e.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchO2CTransferDetails]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	        	try{
	                if (pstmt!= null){
	                	pstmt.close();
	                }
	              }
	              catch (SQLException e){
	            	  _log.error("An error occurred closing prepared statement.", e);
	              }
	        	}
	            if (_log.isDebugEnabled()) {
	            	_log.debug(methodName, "Exiting searchO2CTransferDetails:" + o2CTransferDetailsReqDTO);
	            }
	            
	            return o2CtransferDetRespDTO;
	    }	
		
		
		/**
		 * Load o2c enquiry details
		 * 
		 * @param p_con
		 * @param searchBy
		 * @param p_transferID
		 * @param p_userID
		 * @param p_fromDate
		 * @param p_toDate
		 * @param p_status
		 * @param p_transferSubTypeCode
		 * @param p_productCode
		 * @param p_userCode
		 * @return
		 * @throws BTSLBaseException
		 */
	    public ArrayList loadEnquiryO2cList(Connection p_con, String searchBy, String p_transferID,String p_userID, Date p_fromDate, Date p_toDate, String p_status, String p_transferSubTypeCode, String p_transferCategory, String p_userCode,String userType) throws BTSLBaseException {

	        final String methodName = "loadEnquiryO2cList";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered  searchBy: ");
	        	loggerValue.append(searchBy);
	        	loggerValue.append( " , p_transferID: ");
	        	loggerValue.append(p_transferID);
	        	loggerValue.append( " , UserID: ");
	        	loggerValue.append(p_userID);
	        	loggerValue.append(" , FromDate:" );
	        	loggerValue.append(p_fromDate);
	        	loggerValue.append(" , ToDate:");
	        	loggerValue.append(p_toDate);
	        	loggerValue.append(" , Status:");
	        	loggerValue.append(p_status);
//	        	loggerValue.append(", p_productCode:");
//	        	loggerValue.append(p_productCode);
	        	loggerValue.append(", p_userCode:");
	        	loggerValue.append(p_userCode);
	        	loggerValue.append(", p_transferCategory:");
	        	loggerValue.append(p_transferCategory);
	            _log.debug(methodName,loggerValue );
	        }

	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String isPrimary = null;
	    	String[] p_transferSubTypeCodeArr = null;
	        

	    	if(!BTSLUtil.isNullString(p_transferSubTypeCode)) {
				p_transferSubTypeCodeArr = p_transferSubTypeCode.split(",");
	    	}
	        
	        if(PretupsI.SEARCH_BY_MSISDN.equals( searchBy) ) {
	        	 if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
	                 if (BTSLUtil.isNullString(p_transferID) && (!BTSLUtil.isNullString(p_userCode))) {
	                     final UserDAO userDAO = new UserDAO();
	                     UserPhoneVO userPhoneVO = null;
	                     userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_userCode);
	                     if (userPhoneVO != null) {
	                         isPrimary = userPhoneVO.getPrimaryNumber();
	                     }
	                 }
	             }
				 
	        }

	        StringBuilder strBuff = channelTransferQry.loadEnquiryO2cListQry ( isPrimary, searchBy, p_transferID,
		    	 p_userID, p_fromDate,  p_toDate,  p_status,  p_transferSubTypeCodeArr, p_userCode, p_transferCategory,userType);
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(strBuff);
	            _log.debug(methodName,  loggerValue );
	        }
	        final ArrayList enquiryItemsList = new ArrayList();
	        try {
	            pstmt = p_con.prepareStatement(strBuff.toString());
	            int m = 0;
	            if ( PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
	                ++m;
	                pstmt.setString(m, p_transferID);
	            } else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {
 	                
	                ++m;
	                pstmt.setString(m, p_userCode);
	                
	                ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                    
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
	                
                    if(!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr) && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0]) ) {
    	            	for(int i=0; i <p_transferSubTypeCodeArr.length; i++) {
    	            		m++;
    	            		pstmt.setString(m, p_transferSubTypeCodeArr[i]);
    		            }
    	            }
					
					if(!PretupsI.ALL.equalsIgnoreCase(p_transferCategory)) {
		            	++m;
			            pstmt.setString(m, p_transferCategory);
		            }
	            } else {
	                
	                ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                    
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    
//                    if (!PretupsI.ALL.equals(p_productCode) ) {
//	                    ++m;
//	                    pstmt.setString(m, p_productCode);
//	                }

	                if (!PretupsI.ALL.equals(p_status) ) {
	                    ++m;
	                    pstmt.setString(m, p_status);
	                }

	                if(!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr) && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0])) {
    	            	for(int i=0; i <p_transferSubTypeCodeArr.length; i++) {
    	            		m++;
    	            		pstmt.setString(m, p_transferSubTypeCodeArr[i]);
    		            }
    	            }
                    if(!userType.equals(PretupsI.OPERATOR_USER_TYPE)){
	                ++m;
	                pstmt.setString(m, p_userID);
	                ++m;
                    pstmt.setString(m, p_userID);
                    }
                    
                    if(!PretupsI.ALL.equalsIgnoreCase(p_transferCategory)) {
		            	++m;
			            pstmt.setString(m, p_transferCategory);
		            }
	                
	            }
				/*
				 * ++m; pstmt.setString(m, enquiryType);
				 */
	            
	            ++m;
	            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
	            
	            rs = pstmt.executeQuery();

	            ChannelTransferVO transferVO = null;
	            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
	            final ArrayList transferCategoryList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE_FOR_TRFRULES, true);
	            Boolean channelTransfersInfoRequired = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
	            while (rs.next()) {
	                transferVO = new ChannelTransferVO();
	                transferVO.setTransferSubTypeValue(rs.getString("lookup_name"));
	                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
	                transferVO.setTransferID(rs.getString("transfer_id"));
	                transferVO.setTransferType(rs.getString("transfer_type"));
	                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
	                transferVO.setNetworkCode(rs.getString("network_code"));
	                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
	                transferVO.setToUserName(rs.getString("user_name"));
	                transferVO.setTransferDate(rs.getDate("transfer_date"));
	                if (transferVO.getTransferDate() != null) {
	                    transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
	                }
	                transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
	                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
	                transferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
	                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
	                transferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
	                transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
	                transferVO.setCanceledBy(rs.getString("cancelled_by"));
	                transferVO.setCanceledOn(rs.getDate("cancelled_on"));

	                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
	                    transferVO.setUserMsisdn(rs.getString("from_msisdn"));
	                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
	                    transferVO.setUserMsisdn(rs.getString("to_msisdn"));
	                } else {
	                    transferVO.setUserMsisdn(rs.getString("msisdn"));
	                }
	                transferVO.setTransferCategory(rs.getString("transfer_category"));
	                transferVO.setPayableAmount(rs.getLong("payable_amount"));
	                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
	                transferVO.setStatus(rs.getString("status"));
	                transferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
	                transferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
	                transferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
	                transferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
	                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
	                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
	                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
	                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
	                transferVO.setFromUserID(rs.getString("from_user_id"));
	                transferVO.setToUserID(rs.getString("to_user_id"));
	                transferVO.setDomainCode(rs.getString("domain_code"));
	                transferVO.setPaymentInstType(rs.getString("PMT_INST_TYPE"));
	                if(!BTSLUtil.isNullString(rs.getString("pmt_inst_type"))) {
	                	transferVO.setPayInstrumentName(((LookupsVO) LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE, rs.getString("pmt_inst_type"))).getLookupName());
	                }
	                // added by amit for o2c transfer quantity change
	                transferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
	                transferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
	                transferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
	                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
	                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, transferVO.getStatus())).getLookupName());
	                }
	                // Added By Babu Kunwar For displaying Balnce in O2C Enquiry
//	                transferVO.setSenderPostStock(rs.getString("SENDER_POST_STOCK"));
//	                transferVO.setSenderPreviousStock(rs.getLong("SENDER_PREVIOUS_STOCK"));
//	                transferVO.setReceiverPostStock(rs.getString("RECEIVER_POST_STOCK"));
//	                transferVO.setReceiverPreviousStock(rs.getLong("RECEIVER_PREVIOUS_STOCK"));
	                if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
				        transferVO.setSosStatus(rs.getString("SOS_STATUS"));
				        transferVO.setSosSettlementDate(rs.getDate("SOS_SETTLEMENT_DATE"));
				        }
	                if (transferVO.getThirdApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getThirdApprovedByName());
	                    if (transferVO.getThirdApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getThirdApprovedOn())));
	                    }
	                } else if (transferVO.getSecondApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getSecondApprovedByName());
	                    if (transferVO.getSecondApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getSecondApprovedOn())));
	                    }
	                } else if (transferVO.getFirstApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getFirstApprovedByName());
	                    if (transferVO.getFirstApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getFirstApprovedOn())));
	                    }
	                }

	                if (transferVO.getCanceledBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getCanceledByApprovedName());
	                    if (transferVO.getCanceledOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getCanceledOn())));
	                    }
	                }
	                transferVO.setTransactionMode(rs.getString("transaction_mode"));
	                if("N".equalsIgnoreCase(transferVO.getTransactionMode())) {
	                	transferVO.setTransactionMode(PretupsI.CHNL_TRANSACTION_MODE_NORMAL);
	                } else if("A".equalsIgnoreCase(transferVO.getTransactionMode())) {
	                	transferVO.setTransactionMode(PretupsI.CHNL_TRANSACTION_MODE_AUTO);
	                }
	                transferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
//	                transferVO.setOtfTypePctOrAMt(rs.getString("otf_type"));
//	                transferVO.setOtfRate(rs.getDouble("otf_rate"));
//	                transferVO.setOtfAmount(rs.getLong("otf_amount"));
	                
	                if(channelTransfersInfoRequired)
	                {
	                	transferVO.setInfo1(rs.getString("info1"));
	                	transferVO.setInfo2(rs.getString("info2"));
	                }
	                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
//	                transferVO.setProductCode(rs.getString("product_code"));
//	                transferVO.setProduct_name(rs.getString("product_name"));
	                transferVO.setReferenceNum(rs.getString("reference_no"));
	                transferVO.setCreatedBy(rs.getString("created_by"));
	                transferVO.setCreatedOn(rs.getDate("created_on"));
	                if( !BTSLUtil.isNullorEmpty(transferVO.getCreatedOn()) ) {
		                transferVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(transferVO.getCreatedOn()));
	                }
	                
	                transferVO.setSource(BTSLUtil.getOptionDesc(rs.getString("source"), sourceTypeList).getLabel());
	                transferVO.setTransferCategoryCodeDesc(BTSLUtil.getOptionDesc(transferVO.getTransferCategory(), transferCategoryList).getLabel());
	                transferVO.setControlTransfer(rs.getString("control_transfer"));
	                if (PretupsI.YES.equals(transferVO.getControlTransfer())) {
	                	transferVO.setControlTransferDesc(PretupsI.CHNL_CONTROL_TRANSFER_CONTROLLED);
	                	
	                }else if (PretupsI.NO.equals(transferVO.getControlTransfer())) {
	                	transferVO.setControlTransferDesc(PretupsI.CHNL_CONTROL_TRANSFER_UNCONTROLLED);
	                	
	                }else if (PretupsI.CONTROL_LEVEL_ADJ.equals(transferVO.getControlTransfer())) {
	                	transferVO.setControlTransferDesc(PretupsI.CHNL_CONTROL_TRANSFER_ADJUSTMENT);
	                }
	                enquiryItemsList.add(transferVO);
	            }
	        } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe);
	            _log.error(methodName,  loggerValue);
	            _log.errorTrace(methodName, sqe);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(sqe.getMessage());
	            
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
	                "", "", "",  loggerValue.toString() );
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	        	 loggerValue.setLength(0);
	             loggerValue.append("Exception:");
	             loggerValue.append(ex);
	            _log.error("", loggerValue );
	            _log.errorTrace(methodName, ex);
	             loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(ex.getMessage());;
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
	                "", "", "", loggerValue.toString());
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
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting:  arrayList Size =");
	            	loggerValue.append(enquiryItemsList.size());
	                _log.debug(methodName,  loggerValue );
	            }
	        }
	        return enquiryItemsList;
	    }

		
	    /**
	     * load c2c enquiry details 
	     * 
	     * @param p_con
	     * @param searchBy
	     * @param p_transferID
	     * @param p_userID
	     * @param p_fromDate
	     * @param p_toDate
	     * @param p_status
	     * @param p_transferSubTypeCode
	     * @param p_productCode
	     * @param p_fromUserCode
	     * @param p_toUserCode
	     * @return
	     * @throws BTSLBaseException
	     */
	    public ArrayList loadC2cEnquiryList(Connection p_con, String searchBy, String p_transferID,
	    		String p_userID, Date p_fromDate, Date p_toDate, String p_status, String p_transferSubTypeCode, 
	    		String p_transferCategory, String p_fromUserCode, String p_toUserCode, String p_staffUserID, String p_userType,String sessionUserDomain) throws BTSLBaseException {

	        final String methodName = "loadC2cEnquiryList";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered  TransferNumber: ");
	        	loggerValue.append(p_transferID);
	        	loggerValue.append( " UserID: ");
	        	loggerValue.append(p_userID);
	        	loggerValue.append(" FromDate:" );
	        	loggerValue.append(p_fromDate);
	        	loggerValue.append(" ToDate:");
	        	loggerValue.append(p_toDate);
	        	loggerValue.append(" Status:");
	        	loggerValue.append(p_status);
//	        	loggerValue.append(", Product Type:");
//	        	loggerValue.append(p_productCode);
	        	loggerValue.append(", p_fromUserCode:");
	        	loggerValue.append(p_fromUserCode);
	        	loggerValue.append(", p_toUserCode:");
	        	loggerValue.append(p_toUserCode);
	        	loggerValue.append(", searchBy:");
	        	loggerValue.append(searchBy);
	        	loggerValue.append(", p_transferCategory:");
	        	loggerValue.append(p_transferCategory);
	        	loggerValue.append(", p_staffUserID:");
	        	loggerValue.append(p_staffUserID);
	        	loggerValue.append(", p_userType:");
	        	loggerValue.append(p_userType);
	            _log.debug(methodName,loggerValue );
	        }

	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String isPrimary = null;
	        Boolean isSecondaryNumberAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
	        String isFromUserPrimary = null;
	        String isToUserPrimary = null;
	        String[] p_transferSubTypeCodeArr = null;
	        
	        if(PretupsI.SEARCH_BY_MSISDN.equals( searchBy) ) {
	        	if (isSecondaryNumberAllowed) {
	                final UserDAO userDAO = new UserDAO();
	                UserPhoneVO userPhoneVO = null;
	                if (!BTSLUtil.isNullString(p_fromUserCode)) {
	                    userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_fromUserCode);
	                    if (userPhoneVO != null && ("N".equalsIgnoreCase(userPhoneVO.getPrimaryNumber()))) {
	                        isFromUserPrimary = userPhoneVO.getPrimaryNumber();
	                    }
	                }
	                if (!BTSLUtil.isNullString(p_toUserCode)) {
	                    userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_toUserCode);
	                    if (userPhoneVO != null && ("N".equalsIgnoreCase(userPhoneVO.getPrimaryNumber()))) {
	                        isToUserPrimary = userPhoneVO.getPrimaryNumber();
	                    }
	                }
	            
		        }
	        }
	        
	        if(!BTSLUtil.isNullString(p_transferSubTypeCode)) {
				p_transferSubTypeCodeArr = p_transferSubTypeCode.split(",");
	    	}

	        StringBuilder strBuff = channelTransferQry.loadEnquiryC2cListQry( isFromUserPrimary, isToUserPrimary, searchBy, p_transferID,
		    	 p_userID, p_fromDate,  p_toDate,  p_status,  p_transferSubTypeCodeArr, 
		    		 p_fromUserCode,  p_toUserCode, p_transferCategory, p_staffUserID, p_userType,sessionUserDomain);
	        
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(strBuff);
	            _log.debug(methodName,  loggerValue );
	        }
	        final ArrayList enquiryItemsList = new ArrayList();
	        try {
	            pstmt = p_con.prepareStatement(strBuff.toString());
	            int m = 0;
	            if ( PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
	                ++m;
	                pstmt.setString(m, p_transferID);
	            } else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {
	               
	               if(!BTSLUtil.isNullString(p_fromUserCode)) {
	            	   ++m;
	 	                pstmt.setString(m, p_fromUserCode);
	               }
	               if(!BTSLUtil.isNullString(p_toUserCode)) {
	            	   ++m;
		                pstmt.setString(m, p_toUserCode);
	               }
	                
	                ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                    
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    
                    if(!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr) && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0])) {
    	            	for(int i=0; i <p_transferSubTypeCodeArr.length; i++) {
    	            		m++;
    	            		pstmt.setString(m, p_transferSubTypeCodeArr[i]);
    		            }
    	            }
	                
	            } else {
	                ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                    
                    ++m;
                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
                    
//                    if (!PretupsI.ALL.equals(p_productCode) ) {
//	                    ++m;
//	                    pstmt.setString(m, p_productCode);
//	                }

	                if (!PretupsI.ALL.equals(p_status) ) {
	                    ++m;
	                    pstmt.setString(m, p_status);
	                }

	                if(!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr) && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0])) {
    	            	for(int i=0; i <p_transferSubTypeCodeArr.length; i++) {
    	            		m++;
    	            		pstmt.setString(m, p_transferSubTypeCodeArr[i]);
    		            }
    	            }
                    if (!sessionUserDomain.equals(PretupsI.OPERATOR_TYPE_OPT)) {
                        if (!PretupsI.USER_TYPE_STAFF.equalsIgnoreCase(p_userType)) {
                            ++m;
                            pstmt.setString(m, p_userID);
                        }
                    }
		            
		            if(!PretupsI.ALL.equalsIgnoreCase(p_transferCategory)) {
		            	++m;
			            pstmt.setString(m, p_transferCategory);
		            }
	                
//	                if (PretupsI.ALL.equals(p_transferSubTypeCode)) {
//	                    ++m;
//	                    pstmt.setString(m, p_userID);
//	                }
	            }
	            
	            ++m;
	            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
	            
	            rs = pstmt.executeQuery();

	            ChannelTransferVO transferVO = null;
	            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
	            final ArrayList transferCategoryList = LookupsCache.loadLookupDropDown(PretupsI.C2C_TRANSFER_TYPE, true);
	            Boolean channelTransfersInfoRequired = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED);
	            while (rs.next()) {
	                transferVO = new ChannelTransferVO();
	                transferVO.setTransferSubTypeValue(rs.getString("lookup_name"));
	                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
	                transferVO.setTransferID(rs.getString("transfer_id"));
	                transferVO.setTransferType(rs.getString("transfer_type"));
	                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
	                transferVO.setNetworkCode(rs.getString("network_code"));
	                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
	                transferVO.setFromUserName(rs.getString("fromUserName"));
	                transferVO.setToUserName(rs.getString("toUserName"));
	                transferVO.setTransferDate(rs.getDate("transfer_date"));
	                if (transferVO.getTransferDate() != null) {
	                    transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
	                }
	                transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
	                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
	                transferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
	                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
	                transferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
	                transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
	                transferVO.setCanceledBy(rs.getString("cancelled_by"));
	                transferVO.setCanceledOn(rs.getDate("cancelled_on"));

	                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
	                    transferVO.setUserMsisdn(rs.getString("from_msisdn"));
	                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
	                    transferVO.setUserMsisdn(rs.getString("to_msisdn"));
	                } else {
	                    transferVO.setUserMsisdn(rs.getString("msisdn"));
	                }
	                transferVO.setTransferCategory(rs.getString("transfer_category"));
	                transferVO.setPayableAmount(rs.getLong("payable_amount"));
	                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
	                transferVO.setStatus(rs.getString("status"));
	                transferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
	                transferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
	                transferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
	                transferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
	                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
	                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
	                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
	                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
	                transferVO.setFromUserID(rs.getString("from_user_id"));
	                transferVO.setToUserID(rs.getString("to_user_id"));
	                transferVO.setDomainCode(rs.getString("domain_code"));
	                transferVO.setPaymentInstType(rs.getString("PMT_INST_TYPE"));
	                if(!BTSLUtil.isNullString(rs.getString("pmt_inst_type"))) {
	                	transferVO.setPayInstrumentName(((LookupsVO) LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_TYPE, rs.getString("pmt_inst_type"))).getLookupName());
	                }
	                // added by amit for o2c transfer quantity change
	                transferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
	                transferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
	                transferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
	                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
	                	transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, transferVO.getStatus())).getLookupName());
	                }
	                // Added By Babu Kunwar For displaying Balnce in O2C Enquiry
//	                transferVO.setSenderPostStock(rs.getString("SENDER_POST_STOCK"));
//	                transferVO.setSenderPreviousStock(rs.getLong("SENDER_PREVIOUS_STOCK"));
//	                transferVO.setReceiverPostStock(rs.getString("RECEIVER_POST_STOCK"));
//	                transferVO.setReceiverPreviousStock(rs.getLong("RECEIVER_PREVIOUS_STOCK"));
	                if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
				        transferVO.setSosStatus(rs.getString("SOS_STATUS"));
				        transferVO.setSosSettlementDate(rs.getDate("SOS_SETTLEMENT_DATE"));
				        }
	                if (transferVO.getThirdApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getThirdApprovedByName());
	                    if (transferVO.getThirdApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getThirdApprovedOn())));
	                    }
	                } else if (transferVO.getSecondApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getSecondApprovedByName());
	                    if (transferVO.getSecondApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getSecondApprovedOn())));
	                    }
	                } else if (transferVO.getFirstApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getFirstApprovedByName());
	                    if (transferVO.getFirstApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getFirstApprovedOn())));
	                    }
	                }

	                if (transferVO.getCanceledBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getCanceledByApprovedName());
	                    if (transferVO.getCanceledOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getCanceledOn())));
	                    }
	                }
	                transferVO.setTransactionMode(rs.getString("transaction_mode"));
	                if("N".equalsIgnoreCase(transferVO.getTransactionMode())) {
	                	transferVO.setTransactionMode(PretupsI.CHNL_TRANSACTION_MODE_NORMAL);
	                } else if("A".equalsIgnoreCase(transferVO.getTransactionMode())) {
	                	transferVO.setTransactionMode(PretupsI.CHNL_TRANSACTION_MODE_AUTO);
	                }
	                transferVO.setChannelRemarks(rs.getString("channel_user_remarks"));
//	                transferVO.setOtfTypePctOrAMt(rs.getString("otf_type"));
//	                transferVO.setOtfRate(rs.getDouble("otf_rate"));
//	                transferVO.setOtfAmount(rs.getLong("otf_amount"));
	                
	                if(channelTransfersInfoRequired)
	                {
	                	transferVO.setInfo1(rs.getString("info1"));
	                	transferVO.setInfo2(rs.getString("info2"));
	                }
	                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
//	                transferVO.setProductCode(rs.getString("product_code"));
//	                transferVO.setProduct_name(rs.getString("product_name"));
	                transferVO.setReferenceNum(rs.getString("reference_no"));
	                transferVO.setCreatedBy(rs.getString("created_by"));
	                transferVO.setCreatedOn(rs.getDate("created_on"));
	                if( !BTSLUtil.isNullorEmpty(transferVO.getCreatedOn()) ) {
		                transferVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(transferVO.getCreatedOn()));
	                }

	                // c2c only
	                transferVO.setFromMsisdn(rs.getString("from_msisdn"));
	                transferVO.setToMsisdn(rs.getString("to_msisdn"));
	                transferVO.setSource(BTSLUtil.getOptionDesc(rs.getString("source"), sourceTypeList).getLabel());
	                transferVO.setTransferCategoryCodeDesc(BTSLUtil.getOptionDesc(transferVO.getTransferCategory(), transferCategoryList).getLabel());
	                transferVO.setControlTransfer(rs.getString("control_transfer"));
	                if (PretupsI.YES.equals(transferVO.getControlTransfer())) {
	                	transferVO.setControlTransferDesc(PretupsI.CHNL_CONTROL_TRANSFER_CONTROLLED);
	                	
	                }else if (PretupsI.NO.equals(transferVO.getControlTransfer())) {
	                	transferVO.setControlTransferDesc(PretupsI.CHNL_CONTROL_TRANSFER_UNCONTROLLED);
	                	
	                }else if (PretupsI.CONTROL_LEVEL_ADJ.equals(transferVO.getControlTransfer())) {
	                	transferVO.setControlTransferDesc(PretupsI.CHNL_CONTROL_TRANSFER_ADJUSTMENT);
	                }
	                transferVO.setActiveUserId(rs.getString("active_user_id"));
	                transferVO.setActiveUserName(rs.getString("active_user_name"));
	                transferVO.setActiveUsersUserType(rs.getString("active_user_type"));;
	                enquiryItemsList.add(transferVO);
	            }
	        } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe);
	            _log.error(methodName,  loggerValue);
	            _log.errorTrace(methodName, sqe);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(sqe.getMessage());
	            
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
	                "", "", "",  loggerValue.toString() );
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	        	 loggerValue.setLength(0);
	             loggerValue.append("Exception:");
	             loggerValue.append(ex);
	            _log.error("", loggerValue );
	            _log.errorTrace(methodName, ex);
	             loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:");
	            loggerValue.append(ex.getMessage());;
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadEnquiryChannelTransfersList]",
	                "", "", "", loggerValue.toString());
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
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting:  arrayList Size =");
	            	loggerValue.append(enquiryItemsList.size());
	                _log.debug(methodName,  loggerValue );
	            }
	        }
	        return enquiryItemsList;
	    }

	    /**
	     * get transfer sub type of transaction ID.
	     * 
	     * 
	     * @param p_con
	     * @param transferID
	     * @throws BTSLBaseException
	     */
	    public String checkTransIDTransferSubType(Connection p_con, String  transferID) throws BTSLBaseException {

	        final String methodName = "checkTransIDTranferSubType";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered   checkTransIDTranferSubType : ");
	        	loggerValue.append(transferID);
	            _log.debug(methodName,  loggerValue );
	        }

	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String transferSubType =null;
	        final String sqlSelect = "SELECT TRANSFER_SUB_TYPE  FROM CHANNEL_TRANSFERS ct  WHERE ct.TRANSFER_ID =?";
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(sqlSelect);
	            _log.debug(methodName,  loggerValue);
	        }

	        try {
	            pstmt = p_con.prepareStatement(sqlSelect);
	            int m = 0;
	            ++m;
	            pstmt.setString(m, transferID);
	            	            rs = pstmt.executeQuery();

	            while (rs.next()) {
	            	transferSubType = rs.getString("TRANSFER_SUB_TYPE");
	            	break;
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "p_channelTransferVO ::: " + transferID);
	            }
	        } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe);
	            _log.error(methodName,  loggerValue);
	            _log.errorTrace(methodName, sqe);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:" );
	            loggerValue.append(sqe.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[]", "", "", "",
	            		loggerValue.toString() );
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception : " );
	        	loggerValue.append(ex);
	            _log.error("", loggerValue );
	            _log.errorTrace(methodName, ex);
	            loggerValue.setLength(0);
	            loggerValue.append("Exception:");
	            loggerValue.append(ex.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadChannelTransfersVO]", "", "",
	                "",  loggerValue.toString());
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
	                _log.debug(methodName, "Exiting:  ");
	            }
	        }
	        
	       return transferSubType; 
	    }
	    
	    
		/**
		 * This method get data for Channel to channel transfer commission info data. 
		 * 
		 * @author Subesh KCV
		 * @param p_con
		 * @param c2CTransferCommReqDTO
		 *         
		 * @return List<C2CtransferCommisionRecordVO>
		 * @throws BTSLBaseException
		 */
		public C2CtransferCommRespDTO downloadC2CTransferCommissionData(Connection p_con,C2CTransferCommReqDTO  c2CTransferCommReqDTO,DownloadDataFomatReq downloadDataFomatReq )
				throws BTSLBaseException {
			final String methodName = "searchC2CTransferCommissionData";
			File onlineFile = null;
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, c2CTransferCommReqDTO.toString());
			}
			PreparedStatement pstmt = null;
					final String sqlSelect = channelTransferQry
					.getC2CTransferCommissiondetails(c2CTransferCommReqDTO);
		    
		    
		    if (_log.isDebugEnabled()) {
		    	_log.debug(methodName, sqlSelect);
			}
		    
		    StringBuilder msg = new StringBuilder();
	       
	        List<C2CtransferCommisionRecordVO> listC2CTransferCommissionRecordVO = new ArrayList<>();
			C2CtransferCommRespDTO c2CtransferCommRespDTO = new C2CtransferCommRespDTO();
			String offlineDownloadLocation = SystemPreferences.OFFLINERPT_DOWNLD_PATH;
			java.io.FileWriter outputWriter = null;
			FileOutputStream outExcel = null;
			File file = null;
			CSVWriter csvWriter=null;
			Workbook workbook =null;
			Sheet sheet =null;
			int continueLastRow=0;// For xlsx writing...
			int lastRow = 0;
			String filePath=null;
			HashSet<String> transactionIdset = new HashSet<>();
			
			long totalRequestedQuantity=0l;
			long totalMRP=0l;
			long totalCommission=0l;
			long totalCBC=0l;
			long totalTax3=0l;
			long totalSenderDebitQuantity=0l;
			long totalReceiverCreditQuantity=0l;
			long totalPayableAmount=0l;
			long totalNetPayableAmount=0l;
			String transferSubType=null;
	        try {
	        	
	    
	        	pstmt = p_con.prepareStatement(sqlSelect.toString());
	        	pstmt.setFetchSize(1000);
	        	int i = 0;
	        	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
	         	
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
	        	
	        	++i;
	        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2CTransferCommReqDTO.getFromDate() +  PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues())));
	        	++i;
	        	pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(BTSLDateUtil.getGregorianDate(c2CTransferCommReqDTO.getToDate() + PretupsRptUIConsts.REPORT_TO_TIME.getReportValues())));
	        
	        	++i;
	        	pstmt.setString(i, c2CTransferCommReqDTO.getExtnwcode());
	        	
	        	++i;
	        	pstmt.setString(i, c2CTransferCommReqDTO.getTransferCategory());
	        	++i;
	        	pstmt.setString(i, c2CTransferCommReqDTO.getTransferCategory());
	       	
	    	  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getDomain());
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getCategoryCode());
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUserCategory());
	         	++i;
	         	
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
				++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getUser());
	         	
	         	
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
				++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	++i;
	         	pstmt.setString(i, c2CTransferCommReqDTO.getTransferUser());
	         	
	         	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getTransferInout());
	         	
	         	if(c2CTransferCommReqDTO.getDistributionType()!=null & (c2CTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.VOUCHER_PRODUCT_O2C) )){

	    			if(!c2CTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) { // In this case UI is sending values as 'V', INSTEAD OF 'ALL'
	    				   transferSubType =c2CTransferCommReqDTO.getTransferSubType();
	    			 }else { // ALL
	    				 transferSubType ="T";
	    			 }
	    			++i;
		         	pstmt.setString(i, transferSubType);
	    		} else if(c2CTransferCommReqDTO.getDistributionType()!=null & (c2CTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.STOCK) )) { 
	    			 //Incase of STOCK
	    			if(c2CTransferCommReqDTO.getTransferSubType().trim().equals("T,R,W,X")) { // In this case UI is sending values as 'T,R,W,X', INSTEAD OF 'ALL'
	    				//sbquery.append( " AND CTRF.TRANSFER_SUB_TYPE IN ('T','X','W','X') ");  <>'V'
	    				transferSubType="V";  //condition will go as <> V
	    				//sbquery.append(" AND CTRF.transfer_sub_type <> ? "); // CAN SUBSTIUTE ANY VALUE
	    			}else {
	    				transferSubType=c2CTransferCommReqDTO.getTransferSubType();  //condition will go as ui input
	    				//sbquery.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
	    			}
		         	++i;
		         	pstmt.setString(i, transferSubType);

	    		} else {
	    			
	    			// distribution all , and transfer type can be 'R'  or 'W'  or 'T,V'
	    			if(c2CTransferCommReqDTO.getTransferSubType()!=null  && !c2CTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) {
	    			String trfArray[]=null;
	    			if (c2CTransferCommReqDTO.getTransferSubType()!=null && c2CTransferCommReqDTO.getTransferSubType().indexOf(",") >0 ) {
	    				trfArray=c2CTransferCommReqDTO.getTransferSubType().split(",");
	    			}else {
	    				trfArray= new String[1];
	    				trfArray[0]=c2CTransferCommReqDTO.getTransferSubType();
	    			}
	    			
	    			 for (int k=0;k<trfArray.length;k++) {
		    		    ++i;
		    		    pstmt.setString(i, trfArray[k]);
	    			 }
	    			}		 
	    		}	
	         	
	         
		     	
			  	
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getGeography());
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getGeography());
			  	
			  	++i;
			  	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
			  	
			  	if(c2CTransferCommReqDTO.getTransferInout()!=null && !c2CTransferCommReqDTO.getTransferInout().equals(PretupsI.ALL)) {
			  		++i;
				  	pstmt.setString(i, c2CTransferCommReqDTO.getUserId());
			  	}
			  	
			  	
			  	
			
			  	
			  	if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getSenderMobileNumber())) {
			  		++i;
				  	pstmt.setString(i, c2CTransferCommReqDTO.getSenderMobileNumber());
			  	}
			  	
			  	if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getReceiverMobileNumber())) {
			  		++i;
				  	pstmt.setString(i, c2CTransferCommReqDTO.getReceiverMobileNumber());
			  	}
			  	
			  	C2CTransferdetReportWriter c2CTransferdetReportWriter = new C2CTransferdetReportWriter();
			  	
			  	if(c2CTransferCommReqDTO.isOffline()) {
		         	    //filePath="D://downloadedReports//"+c2CTransferCommReqDTO.getFileName();  // for dev Testing...
		         		filePath=offlineDownloadLocation+c2CTransferCommReqDTO.getFileName();
		         	}else {
		         		//filePath="D://downloadedReports//"+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType();  // for dev Testing...
		         		filePath=offlineDownloadLocation+downloadDataFomatReq.getFileName()+"."+downloadDataFomatReq.getFileType();
		         	}
			  //FileWriter already use  com.btsl.pretups.channel.transfer.util.clientutils.FileWriter,
				//So using below package java.io.FileWriter
	             HashMap<String,String>	totalSummaryCaptureCols=null;
			 try(ResultSet rs = pstmt.executeQuery();)
         	{   
		 		 file = new File(filePath);
	               	if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
	        				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
	            		outExcel =new FileOutputStream(file);
	        		    workbook = new XSSFWorkbook();
	        			sheet = workbook.createSheet(downloadDataFomatReq.getFileName());
	        			try {
	        				sheet.autoSizeColumn(PretupsRptUIConsts.ZERO.getNumValue());
		        			sheet.autoSizeColumn(PretupsRptUIConsts.ONE.getNumValue());
		        			sheet.autoSizeColumn(PretupsRptUIConsts.TWO.getNumValue());
	        			}catch (Exception e) {
	        				_log.error("", "Error occurred while autosizing columns");
	        				e.printStackTrace();
	        			}
	        			
	        			Font headerFont = workbook.createFont();
	        			headerFont.setBold(true);
	        			// headerFont.setFontHeightInPoints( (Short) 14);
	        			CellStyle headerCellStyle = workbook.createCellStyle();
	        			headerCellStyle.setFont(headerFont);
	        	 totalSummaryCaptureCols = c2CTransferdetReportWriter.constructXLSX(workbook,sheet, downloadDataFomatReq, c2CTransferCommReqDTO,lastRow,headerCellStyle);
	        	 continueLastRow=Integer.parseInt(totalSummaryCaptureCols.get(PretupsI.XLSX_LAST_ROW));
	        		} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
	        			//FileWriter already use  com.btsl.pretups.channel.transfer.util.clientutils.FileWriter,
	           			//So using below package java.io.FileWriter
	           			 outputWriter = new java.io.FileWriter(file);
	        	    	 csvWriter = new CSVWriter(outputWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
	    						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
	        	    	 c2CTransferdetReportWriter.constructCSV(csvWriter, downloadDataFomatReq, c2CTransferCommReqDTO);
	                }   
	            	  Long totalNumberOfRecords =0l;
	            	  c2CtransferCommRespDTO.setNoDataFound(true);
				 
				 
				 
             while (rs.next()) { 
            	 
            	 if(transactionIdset.contains(rs.getString("TRANSFER_ID"))) {
            		 continue;
            	 }
            	 else 
            		 transactionIdset.add(rs.getString("TRANSFER_ID"));
            	 
            	 if(c2CTransferCommReqDTO.isOffline() && OfflineReportRunningThreadMap.checkTaskCancellationRequest(c2CTransferCommReqDTO.getOfflineReportTaskID())){
       			  throw new BTSLBaseException(C2STransferTxnDAO.class.getName(), methodName,
								PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED); 
       			}
            	 
            	 c2CtransferCommRespDTO.setNoDataFound(false);
     			totalNumberOfRecords=totalNumberOfRecords+1;
            	 
               	C2CtransferCommisionRecordVO c2CtransferCommisionRecordVO = new C2CtransferCommisionRecordVO();
               	String dateformat =BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("TRANSFER_DATE") + " " +rs.getTime("TRANSFER_DATE")));
               	c2CtransferCommisionRecordVO.setInitiatorUserName(rs.getString("initiator_user"));
    	       	c2CtransferCommisionRecordVO.setTransdateTime(dateformat);
	           	c2CtransferCommisionRecordVO.setTransactionID(rs.getString("TRANSFER_ID"));
	           	c2CtransferCommisionRecordVO.setSenderName(rs.getString("fromUserName"));
	           	c2CtransferCommisionRecordVO.setSenderMsisdn(rs.getString("from_Msisdn"));
	           	c2CtransferCommisionRecordVO.setSenderCategory(rs.getString("senderCategoryName"));
	           	c2CtransferCommisionRecordVO.setSenderDebitQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("SENDER_DEBIT_QUANTITY"))));
	           	c2CtransferCommisionRecordVO.setReceiverName(rs.getString("ReceiverName"));
	           	c2CtransferCommisionRecordVO.setReceiverMsisdn(rs.getString("ReceiverMSISDN"));
	           	c2CtransferCommisionRecordVO.setReceiverCategory(rs.getString("receiverCategoryName"));
	           	c2CtransferCommisionRecordVO.setReceiverCreditQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("RECEIVER_CREDIT_QUANTITY"))));
	           	c2CtransferCommisionRecordVO.setProductName(rs.getString("productName"));
	           	c2CtransferCommisionRecordVO.setTransferInOut(rs.getString("TransferINOUT"));
	           	c2CtransferCommisionRecordVO.setTransferSubType(rs.getString("transferSubType"));
	           	c2CtransferCommisionRecordVO.setSource(rs.getString("source"));
	           	c2CtransferCommisionRecordVO.setCommission(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("commission_value"))));
	           	c2CtransferCommisionRecordVO.setTax3(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax3_Value"))));
	           	c2CtransferCommisionRecordVO.setPayableAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("payableAmount"))));
	           	c2CtransferCommisionRecordVO.setNetPayableAmount(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("netPayableAmount"))));
	           	c2CtransferCommisionRecordVO.setRequestedQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("transfer_mrp"))));
	           	c2CtransferCommisionRecordVO.setDenomination(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("mrp"))));
	         	c2CtransferCommisionRecordVO.setCumulativeBaseCommission(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("commision_quantity"))));
	         	c2CtransferCommisionRecordVO.setTransactionStatus(rs.getString("TransactionStatus"));
	         	c2CtransferCommisionRecordVO.setTransferCategory(rs.getString("trf_cat_name"));
	         	c2CtransferCommisionRecordVO.setRequestGateway(rs.getString("request_Gateway_Desc"));
	         	c2CtransferCommisionRecordVO.setDistributionType(rs.getString("DISTRIBUTION_TYPE"));
	         	c2CtransferCommisionRecordVO.setSenderPreviousStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("SENDER_PREVIOUS_STOCK"))));
	         	c2CtransferCommisionRecordVO.setSenderPostStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("SENDER_POST_STOCK"))));
	         	c2CtransferCommisionRecordVO.setReceiverPostStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("RECEIVER_POST_STOCK"))));
	         	c2CtransferCommisionRecordVO.setReceiverPreviousStock(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("RECEIVER_PREVIOUS_STOCK"))));
	         	
	         	  String modifiedTime =   String.valueOf(rs.getTime("modified_on"));
	         	 String timeArr[]=null;
	         	 String hr,min,secs;
	         	 String modified_on =null;
	         	 if(modifiedTime!=null   ) {
	         		timeArr =modifiedTime.split(":");
	         		 hr = timeArr[0];
	         		 min = timeArr[1];
	         		 secs =timeArr[2];
	         		if ( (rs.getString("status")!=null && rs.getString("status").equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE))  &&  (hr!=null && hr.equals("00")) && (min!=null && min.equals("00")) ) {
	         			modified_on= BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("created_on") + " " +rs.getTime("created_on")));
	         		 }else {
	         			modified_on= BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("modified_ON") + " " +rs.getTime("modified_ON")));
	         		 }
	         	 
	         		 
	         	 }else {
	         		modified_on= BTSLUtil.getDateTimeStringFromDate(BTSLDateUtil.getGregorianDate(rs.getDate("created_on") + " " +rs.getTime("created_on")));
	         	 }
	         	c2CtransferCommisionRecordVO.setModifiedOn(modified_on);
	         	
	         	c2CtransferCommisionRecordVO.setRequestedSource(rs.getString("source"));
	         	c2CtransferCommisionRecordVO.setTax1(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax1_value"))));
	         	c2CtransferCommisionRecordVO.setTax2(String.valueOf(PretupsBL
						.getDisplayAmount(rs.getLong("tax2_value"))));
	         	
	           	 totalRequestedQuantity=totalRequestedQuantity + rs.getLong("transfer_mrp");
				 totalMRP= totalMRP +rs.getLong("mrp");
				 totalCommission= totalCommission +  rs.getLong("commission_value");
				 totalCBC = totalCBC + rs.getLong("commision_quantity");
			     totalTax3= totalTax3 + rs.getLong("tax3_Value");
				 totalSenderDebitQuantity = totalSenderDebitQuantity + rs.getLong("SENDER_DEBIT_QUANTITY");
				 totalReceiverCreditQuantity= totalReceiverCreditQuantity + rs.getLong("RECEIVER_CREDIT_QUANTITY");
				 totalPayableAmount= totalPayableAmount + rs.getLong("payableAmount");
				 totalNetPayableAmount = totalNetPayableAmount + rs.getLong("netPayableAmount");
	           	
     			
     			if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
         				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
     				continueLastRow=continueLastRow+1;
     				c2CTransferdetReportWriter.writeXLSXRow(workbook,sheet,downloadDataFomatReq,continueLastRow,c2CtransferCommisionRecordVO);
     				if(totalNumberOfRecords%5000==0) {
     					outExcel.flush();
     					workbook.write(outExcel);
 					}
     			} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
     				c2CTransferdetReportWriter.writeCSVRow(csvWriter,downloadDataFomatReq,c2CtransferCommisionRecordVO);
                 	if(totalNumberOfRecords%5000==0) {
 						csvWriter.flush();
 						outputWriter.flush();
 					}
                 	
                 }
	           	
	           	//listC2CTransferCommissionRecordVO.add(c2CtransferCommisionRecordVO);
	           	
             }
            	c2CtransferCommRespDTO.setListC2CTransferCommRecordVO(listC2CTransferCommissionRecordVO);
                 C2CtransferCommSummryData c2CtransferCommSummryData  = new C2CtransferCommSummryData();
                 
                 if(totalNumberOfRecords>0) { 
                 c2CtransferCommSummryData.setTotalCBC(String.valueOf(PretupsBL
						.getDisplayAmount(totalCBC)));
            	
                 c2CtransferCommSummryData.setTotalRequestedQuantity(String.valueOf(PretupsBL
						.getDisplayAmount(totalRequestedQuantity)));
                 c2CtransferCommSummryData.setTotalMRP(String.valueOf(PretupsBL
 						.getDisplayAmount(totalMRP)));
                 c2CtransferCommSummryData.setTotalCommission(String.valueOf(PretupsBL
  						.getDisplayAmount(totalCommission)));
                 
                 c2CtransferCommSummryData.setTotalTax3(String.valueOf(PretupsBL
   						.getDisplayAmount(totalTax3)));
                 c2CtransferCommSummryData.setTotalSenderDebitQuantity(String.valueOf(PretupsBL
    						.getDisplayAmount(totalSenderDebitQuantity))); 
                 c2CtransferCommSummryData.setTotalReceiverCreditQuantity(String.valueOf(PretupsBL
 						.getDisplayAmount(totalReceiverCreditQuantity)));  
                 
                 c2CtransferCommSummryData.setTotalPayableAmount(String.valueOf(PretupsBL
  						.getDisplayAmount(totalPayableAmount)));  
                 c2CtransferCommSummryData.setTotalNetPayableAmount(String.valueOf(PretupsBL
  						.getDisplayAmount(totalNetPayableAmount)));
            	 c2CtransferCommRespDTO.setC2CtransferCommSummryData(c2CtransferCommSummryData);
            	 HashMap<String,String> totSummaryColValue = new HashMap<String,String>();
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.REQUESTED_QUANTITY.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalRequestedQuantity());
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.DENOMINATION.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalMRP());
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.CUMULATIVE_BASE_COMMISSION.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalCBC());
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.TAX3.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalTax3());
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.SENDER_DEBIT_QUANTITY.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalSenderDebitQuantity());
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.RECEIVER_CREDIT_QUANTITY.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalReceiverCreditQuantity());
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.PAYABLE_AMOUNT.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalPayableAmount());
     			totSummaryColValue.put(C2CTransferCommDownloadColumns.NET_PAYABLE_AMOUNT.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalNetPayableAmount());
     			
     			if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
        				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
     				continueLastRow=continueLastRow+1;
     				c2CTransferdetReportWriter.writeXLSXTotalSummaryColumns(workbook, sheet, downloadDataFomatReq,continueLastRow,totSummaryColValue,totalSummaryCaptureCols);
    				workbook.write(outExcel);
			   	}else {
			   		c2CTransferdetReportWriter.writeCSVTotalSummaryColumns(csvWriter,downloadDataFomatReq,totSummaryColValue);	
			   	}
     			c2CtransferCommRespDTO.setC2CtransferCommSummryData(c2CtransferCommSummryData);
     			c2CtransferCommRespDTO.setTotalDownloadedRecords(String.valueOf(totalNumberOfRecords));
            	 
                 }
                 if(outExcel!=null && workbook!=null) {
              		outExcel.flush();
     				//workbook.write(outExcel);
          		}
          		if(csvWriter!=null && outputWriter!=null) {
     					csvWriter.flush();
     					outputWriter.flush();
          		}
             	
            	
         	}	
	          
	        } catch (SQLException sqle) {
	        	msg.setLength(0);
	        	msg.append(SQL_EXCEPTION);
	        	msg.append(sqle.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchC2CTransferCommissionData]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
	        }// end of catch */
	        catch (Exception e) {
	        	msg.setLength(0);
	        	msg.append(EXCEPTION);
	        	msg.append(e.getMessage());
	        	_log.error(methodName, msg);
	        	_log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[searchC2CTransferCommissionData]", "", "", "", msg.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
	        }// end of catch
	        finally {
	        	try{
	                if (pstmt!= null){
	                	pstmt.close();
	                }
	              }
	              catch (SQLException e){
	            	  _log.error("An error occurred closing prepared statement.", e);
	              }
	        	 
	        	  if(csvWriter!=null) {
	        		  try {
						csvWriter.close();
					} catch (IOException e) {
						_log.error("An error occurred closing csvwriter.", e);
					}
	        	  }
	        	  
	        	  if(outputWriter!=null) {
	        		  try {
	        			  outputWriter.close();
					} catch (IOException e) {
						_log.error("An error occurred closing csvwriter.", e);
					}
	        	  }
	        	  
	        	   if(outExcel!=null) {
	        		   try {
						outExcel.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						_log.error("An error occurred closing XSLX Writer.", e);
					}
	        	   }
	        	   
	        	   
	        	   if(workbook!=null) {
	        		   try {
	        			   workbook.close();
	        			   
					} catch (IOException e) {
						// TODO Auto-generated catch block
						_log.error("An error occurred closing XSLX Writer.", e);
					}
	        	   }
	        	   

    	}
	        
	          if(!c2CTransferCommReqDTO.isOffline()) {
	        	  c2CtransferCommRespDTO.setOnlineFilePath(filePath);
 	        
                   }
	        
	            if (_log.isDebugEnabled()) {
	            	_log.debug(methodName, "Exiting userName:" + c2CtransferCommRespDTO);
	            }
	            
	            return c2CtransferCommRespDTO;
	    }		
		


		/**
	     * Load the Detailed Channel transfer VO against transferID,networkcode and
	     * networkcodefor
	     * 
	     * @param p_con
	     * @param p_channelTransferVO
	     * @throws BTSLBaseException
	     */
	    public boolean viewTransactionIDAllowCheck(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {

	        final String methodName = "loadChannelTransfersVO";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered   p_channelTransferVO : ");
	        	loggerValue.append(p_channelTransferVO);
	            _log.debug(methodName,  loggerValue );
	        }

	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        boolean eligibletoView=false;
	        final String sqlSelect = channelTransferQry.viewTransactionIDAllowCheck();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(sqlSelect);
	            _log.debug(methodName,  loggerValue);
	        }

	        try {
	            pstmt = p_con.prepareStatement(sqlSelect);
	            int m = 0;
	            ++m;
	            pstmt.setString(m, p_channelTransferVO.getLoginID());
	            ++m;
	            pstmt.setString(m, p_channelTransferVO.getTransferID());
	
	            rs = pstmt.executeQuery();

	            if (rs.next()) {
	            	eligibletoView=true;
		        }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "p_channelTransferVO ::: " + p_channelTransferVO);
	            }
	        } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe);
	            _log.error(methodName,  loggerValue);
	            _log.errorTrace(methodName, sqe);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:" );
	            loggerValue.append(sqe.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[]", "", "", "",
	            		loggerValue.toString() );
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception : " );
	        	loggerValue.append(ex);
	            _log.error("", loggerValue );
	            _log.errorTrace(methodName, ex);
	            loggerValue.setLength(0);
	            loggerValue.append("Exception:");
	            loggerValue.append(ex.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[viewTransactionIDAllowCheck]", "", "",
	                "",  loggerValue.toString());
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
	                _log.debug(methodName, "Exiting:  ");
	            }
	        }
			return eligibletoView;
	    }
		
	    public boolean viewTransactionIDAllowCheckNew(Connection p_con, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {


	        final String methodName = "viewTransactionIDAllowCheckNew";
	        StringBuilder loggerValue= new StringBuilder(); 
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered   p_channelTransferVO : ");
	        	loggerValue.append(p_channelTransferVO);
	            _log.debug(methodName,  loggerValue );
	        }

	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        boolean eligibletoView=false;
	        final String sqlSelect = channelTransferQry.viewTransactionIDAllowCheckNew();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("QUERY sqlSelect=");
	        	loggerValue.append(sqlSelect);
	            _log.debug(methodName,  loggerValue);
	        }

	        try {
	            pstmt = p_con.prepareStatement(sqlSelect);
	            int m = 0;
	            ++m;
	            pstmt.setString(m, p_channelTransferVO.getLoginID());
	            ++m;
	            pstmt.setString(m, p_channelTransferVO.getTransferID());
	
	            rs = pstmt.executeQuery();

	            if (rs.next()) {
	            	eligibletoView=true;
		        }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "p_channelTransferVO ::: " + p_channelTransferVO);
	            }
	        } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException : ");
	        	loggerValue.append(sqe);
	            _log.error(methodName,  loggerValue);
	            _log.errorTrace(methodName, sqe);
	            loggerValue.setLength(0);
	            loggerValue.append("SQL Exception:" );
	            loggerValue.append(sqe.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[]", "", "", "",
	            		loggerValue.toString() );
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Exception : " );
	        	loggerValue.append(ex);
	            _log.error("", loggerValue );
	            _log.errorTrace(methodName, ex);
	            loggerValue.setLength(0);
	            loggerValue.append("Exception:");
	            loggerValue.append(ex.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[viewTransactionIDAllowCheck]", "", "",
	                "",  loggerValue.toString());
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
	                _log.debug(methodName, "Exiting:  ");
	            }
	        }
			return eligibletoView;
	    
	    }
	    
	    public ArrayList loadO2CChannelTransfersList(Connection p_con, String p_transferID, String p_userID, Date p_fromDate, Date p_toDate, String p_status, String p_transferTypeCode, String p_productType, String p_transferCategory, String p_userCode, String p_catCode, String p_geograpghy) throws BTSLBaseException {

	        final String methodName = "loadO2CChannelTransfersList";
	        if (_log.isDebugEnabled()) {
	            _log.debug(
	                methodName,
	                "Entered  TransferNumber: " + p_transferID + " UserID: " + p_userID + " FromDate:" + p_fromDate + " ToDate:" + p_toDate + " Status:" + p_status + ",p_transferTypeCode=" + p_transferTypeCode + ", Product Type:" + p_productType + ",p_transferCategory=" + p_transferCategory + ", p_userCode=" + p_userCode);
	        }
	        Boolean isSecondaryNumberAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED);
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        String isPrimary = null;
	        if (isSecondaryNumberAllowed) {
	            if (BTSLUtil.isNullString(p_transferID) && (!BTSLUtil.isNullString(p_userCode))) {
	                final UserDAO userDAO = new UserDAO();
	                UserPhoneVO userPhoneVO = null;
	                userPhoneVO = userDAO.loadUserAnyPhoneVO(p_con, p_userCode);
	                if (userPhoneVO != null) {
	                    isPrimary = userPhoneVO.getPrimaryNumber();
	                }
	            }
	        }

	        String strBuff = channelTransferQry.loadO2CChannelTransfersListQry(isPrimary, p_transferID, p_userCode, p_transferTypeCode, p_transferCategory);
	        final ArrayList enquiryItemsList = new ArrayList();
	        try {
	            pstmt = p_con.prepareStatement(strBuff.toString());
	            int m = 0;
	            if (!BTSLUtil.isNullString(p_transferID)) {
	                ++m;
	                pstmt.setString(m, p_transferID);
	                ++m;
	                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
	                ++m;
	                pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
	                ++m;
	                pstmt.setString(m, p_transferTypeCode);
	            } else if (!BTSLUtil.isNullString(p_userCode)) {
	                ++m;
	                pstmt.setString(m, p_userCode);
	                if (p_fromDate != null) {
	                    ++m;
	                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
	                } else {
	                    ++m;
	                    pstmt.setDate(m, null);
	                }
	                if (p_toDate != null) {
	                    ++m;
	                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
	                } else {
	                    ++m;
	                    pstmt.setDate(m, null);
	                }
	                ++m;
	                pstmt.setString(m, p_transferCategory);
	                ++m;
	                pstmt.setString(m, p_transferTypeCode);
	            } else {
	                if (p_fromDate != null) {
	                    ++m;
	                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
	                } else {
	                    ++m;
	                    pstmt.setDate(m, null);
	                }
	                if (p_toDate != null) {
	                    ++m;
	                    pstmt.setDate(m, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
	                } else {
	                    ++m;
	                    pstmt.setDate(m, null);
	                }
	                ++m;
	                pstmt.setString(m, p_productType);
	                ++m;
	                pstmt.setString(m, p_transferCategory);
	                if (!PretupsI.ALL.equals(p_transferTypeCode) && PretupsI.TRANSFER_CATEGORY_SALE.equals(p_transferCategory)) {
	                    ++m;
	                    pstmt.setString(m, p_transferTypeCode);
	                }
	                ++m;
	                pstmt.setString(m, p_userID);

	                if (PretupsI.ALL.equals(p_transferTypeCode)) {
	                    ++m;
	                    pstmt.setString(m, p_userID);
	                }
	            }
	            ++m;
	            pstmt.setString(m, p_status);
	            ++m;
	            pstmt.setString(m, PretupsI.TRANSFER_TYPE);
	            rs = pstmt.executeQuery();

	            ChannelTransferVO transferVO = null;
	            while (rs.next()) {
	                transferVO = new ChannelTransferVO();
	                transferVO.setTransferSubTypeValue(rs.getString("lookup_name"));
	                transferVO.setRequestedQuantity(rs.getLong("requested_quantity"));
	                transferVO.setTransferID(rs.getString("transfer_id"));
	                transferVO.setTransferType(rs.getString("transfer_type"));
	                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
	                transferVO.setNetworkCode(rs.getString("network_code"));
	                transferVO.setNetworkCodeFor(rs.getString("network_code_for"));
	                transferVO.setToUserName(rs.getString("user_name"));
	                transferVO.setTransferDate(rs.getDate("transfer_date"));
	                if (transferVO.getTransferDate() != null) {
	                    transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateTimeStringFromDate(transferVO.getTransferDate())));
	                }
	                transferVO.setFirstApprovedBy(rs.getString("first_approved_by"));
	                transferVO.setFirstApprovedOn(rs.getDate("first_approved_on"));
	                transferVO.setSecondApprovedBy(rs.getString("second_approved_by"));
	                transferVO.setSecondApprovedOn(rs.getDate("second_approved_on"));
	                transferVO.setThirdApprovedBy(rs.getString("third_approved_by"));
	                transferVO.setThirdApprovedOn(rs.getDate("third_approved_on"));
	                transferVO.setCanceledBy(rs.getString("cancelled_by"));
	                transferVO.setCanceledOn(rs.getDate("cancelled_on"));

	                if (!BTSLUtil.isNullString(rs.getString("from_msisdn"))) {
	                    transferVO.setUserMsisdn(rs.getString("from_msisdn"));
	                } else if (!BTSLUtil.isNullString(rs.getString("to_msisdn"))) {
	                    transferVO.setUserMsisdn(rs.getString("to_msisdn"));
	                } else {
	                    transferVO.setUserMsisdn(rs.getString("msisdn"));
	                }
	                transferVO.setTransferCategory(rs.getString("transfer_category"));
	                transferVO.setPayableAmount(rs.getLong("payable_amount"));
	                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
	                transferVO.setStatus(rs.getString("status"));
	                transferVO.setFirstApprovedByName(rs.getString("firstapprovedby"));
	                transferVO.setSecondApprovedByName(rs.getString("secondapprovedby"));
	                transferVO.setThirdApprovedByName(rs.getString("thirdapprovedby"));
	                transferVO.setCanceledByApprovedName(rs.getString("cancelledby"));
	                transferVO.setGraphicalDomainCode(rs.getString("grph_domain_code"));
	                transferVO.setGrphDomainCodeDesc(rs.getString("grph_domain_name"));
	                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
	                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
	                transferVO.setFromUserID(rs.getString("from_user_id"));
	                transferVO.setToUserID(rs.getString("to_user_id"));
	                transferVO.setDomainCode(rs.getString("domain_code"));
	                transferVO.setDomainName(rs.getString("domainName"));
	                transferVO.setCategoryCode(rs.getString("category_code"));
	                transferVO.setCategoryName(rs.getString("categoryName"));
	                // added by amit for o2c transfer quantity change
	                transferVO.setLevelOneApprovedQuantity(rs.getString("first_level_approved_quantity"));
	                transferVO.setLevelTwoApprovedQuantity(rs.getString("second_level_approved_quantity"));
	                transferVO.setLevelThreeApprovedQuantity(rs.getString("third_level_approved_quantity"));
	                transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.CHANNEL_TRANSFER_ORDER_STATUS, transferVO.getStatus())).getLookupName());
	                // Added By Babu Kunwar For displaying Balnce in O2C Enquiry
//	                transferVO.setSenderPostStock(rs.getString("SENDER_POST_STOCK"));
//	                transferVO.setSenderPreviousStock(rs.getLong("SENDER_PREVIOUS_STOCK"));
//	                transferVO.setReceiverPostStock(rs.getString("RECEIVER_POST_STOCK"));
//	                transferVO.setReceiverPreviousStock(rs.getLong("RECEIVER_PREVIOUS_STOCK"));
//	                transferVO.setTransactionMode(rs.getString("transaction_mode"));
	                if (transferVO.getThirdApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getThirdApprovedByName());
	                    if (transferVO.getThirdApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getThirdApprovedOn())));
	                    }
	                } else if (transferVO.getSecondApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getSecondApprovedByName());
	                    if (transferVO.getSecondApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getSecondApprovedOn())));
	                    }
	                } else if (transferVO.getFirstApprovedBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getFirstApprovedByName());
	                    if (transferVO.getFirstApprovedOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getFirstApprovedOn())));
	                    }
	                }

	                if (transferVO.getCanceledBy() != null) {
	                    transferVO.setFinalApprovedBy(transferVO.getCanceledByApprovedName());
	                    if (transferVO.getCanceledOn() != null) {
	                        transferVO.setFinalApprovedDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getCanceledOn())));
	                    }
	                }
	                enquiryItemsList.add(transferVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException : " + sqe);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadO2CChannelTransfersList]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	            _log.error("", "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferWebDAO[loadO2CChannelTransfersList]",
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
	                _log.debug(methodName, "Exiting:  arrayList Size =" + enquiryItemsList.size());
	            }
	        }
	        return enquiryItemsList;
	    }

	    
	    public boolean isExtTxnExistsC2C(Connection p_con, String p_extTxnnum) throws BTSLBaseException {
	        final String methodName = "isExtTxnExistsC2C";
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Entered:p_extTxnnum=" + p_extTxnnum);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        boolean isExists = false;
	        final StringBuffer sqlisExtTxnExists = new StringBuffer("SELECT transfer_id FROM channel_transfers ");
	        sqlisExtTxnExists.append("WHERE type=? AND ext_txn_no=? AND status<>? ");

	        try {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "QUERY=" + sqlisExtTxnExists);
	            }
	            final String query = sqlisExtTxnExists.toString();
	            pstmtSelect = p_con.prepareStatement(query);
	            int i=1;
	            pstmtSelect.setString(i++, PretupsI.CHANNEL_TYPE_C2C);
	            pstmtSelect.setString(i++, p_extTxnnum);
	            pstmtSelect.setString(i++, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);

	            rs = pstmtSelect.executeQuery();
	            if (rs.next()) {
	                isExists = true;
	            }
	        }// end of try
	        catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isExtTxnExists]", "", "", "",
	                "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[isExtTxnExists]", "", "", "",
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
	                _log.debug(methodName, "Exititng:isExists=" + isExists);
	            }
	        }// end of finally
	        return isExists;
	    }// end isExtTxnExists

    public ChannelTransferVO loadChannelTransfersDetail(Connection con, String networkCode, String networkCodeFor, String transferCategory, String transferId) throws BTSLBaseException {
        final String METHOD_NAME = "loadChannelTransfersDetail";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered networkCode ");
            loggerValue.append(networkCode);
            loggerValue.append(" roamNetworkCode ");
            loggerValue.append(networkCodeFor);
            loggerValue.append(", transferCategory =" );
            loggerValue.append(transferCategory);
            loggerValue.append(" transferId ");
            loggerValue.append(transferId);
            _log.debug(METHOD_NAME,loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT u.user_name,ct.transfer_id, ct.transfer_date, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date, ct.dual_comm_type, ");
        strBuff.append(" ct.status , ct.transfer_mrp, ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn ");
        strBuff.append(" FROM channel_transfers ct , channel_transfers_items cti ,users u ");
        strBuff.append(" WHERE ct.transfer_initiated_by = u.user_id ");
        strBuff.append(" AND ct.transfer_id = ? ");
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" ORDER BY ct.created_on DESC ");

        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("QUERY sqlSelect=");
            loggerValue.append(strBuff.toString());
            _log.debug(METHOD_NAME,  loggerValue );
        }
        final ArrayList arrayList = new ArrayList();
        ChannelTransferVO transferVO = null;
        transferVO = new ChannelTransferVO();

        try {
            pstmt = con.prepareStatement(strBuff.toString());
            int m = 0;
            ++m;
            pstmt.setString(m, transferId);
            rs = pstmt.executeQuery();

            int i = 0;
            while (rs.next()) {
                transferVO.setTransferID(rs.getString("transfer_id"));
                transferVO.setTransferDate(rs.getDate("transfer_date"));
                transferVO.setType(rs.getString("type"));
                transferVO.setTransferSubType(rs.getString("transfer_sub_type"));
                transferVO.setTransferType(rs.getString("transfer_type"));
                transferVO.setPayableAmount(rs.getLong("payable_amount"));
                transferVO.setNetPayableAmount(rs.getLong("net_payable_amount"));
                transferVO.setStatus(rs.getString("status"));
                transferVO.setToUserID(rs.getString("to_user_id"));
                transferVO.setTransferMRP(rs.getLong("transfer_mrp"));
                transferVO.setReferenceNum(rs.getString("reference_no"));
                if(!BTSLUtil.isNullString(transferVO.getStatus())) {
                    transferVO.setStatusDesc(((LookupsVO) LookupsCache.getObject(PretupsI.TRANSFER_STATUS, transferVO.getStatus())).getLookupName());
                }
                transferVO.setTransferDateAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(transferVO.getTransferDate())));
                transferVO.setNetworkCode(networkCode);
                transferVO.setNetworkCodeFor(networkCodeFor);
                transferVO.setExternalTxnNum(rs.getString("ext_txn_no"));
                transferVO.setExternalTxnDate(rs.getDate("ext_txn_date"));
                transferVO.setCommQty(rs.getLong("commision_quantity"));
                transferVO.setSenderDrQty(rs.getLong("sender_debit_quantity"));
                transferVO.setReceiverCrQty(rs.getLong("receiver_credit_quantity"));
                transferVO.setProductType(rs.getString("product_type"));
                transferVO.setReceiverTxnProfile(rs.getString("receiver_txn_profile"));
                transferVO.setDualCommissionType(rs.getString("dual_comm_type"));
                transferVO.setCommProfileSetId(rs.getString("COMMISSION_PROFILE_SET_ID"));
                transferVO.setCommProfileVersion(rs.getString("COMMISSION_PROFILE_VER"));
                transferVO.setUserWalletCode(rs.getString("user_wallet"));
                transferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                transferVO.setTransferInitatedByName(rs.getString("user_name"));
                transferVO.setToMSISDN(rs.getString("to_msisdn"));
                if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(rs.getString("transfer_sub_type")))
                {
                    transferVO.setTransferSubTypeAsString(PretupsI.VOUCHER_PRODUCT_O2C);

                }
                else
                {
                    transferVO.setTransferSubTypeAsString(PretupsI.REDEMP_TYPE_STOCK);
                }
                transferVO.setIndex(i);
                ++i;
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQLException : ");
            loggerValue.append(sqe);
            _log.error(METHOD_NAME,  loggerValue );
            _log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:" );
            loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "",
                    "", loggerValue.toString() );
            throw new BTSLBaseException(this, "", PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append("Exception : ");
            loggerValue.append(ex);
            _log.error(METHOD_NAME,  loggerValue);
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, METHOD_NAME, "", "",
                    "",  loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
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
                loggerValue.setLength(0);
                loggerValue.append("Exiting:  arrayList Size =");
                loggerValue.append(arrayList.size());
                _log.debug(METHOD_NAME,  loggerValue );
            }
        }
        return transferVO;
    }

    public int updateO2CChannelTransferApproval(Connection con, ChannelTransferVO channelTransferVO, boolean isOrderApproved , boolean isFromPaymentGateway) throws BTSLBaseException {
        final String METHOD_NAME = "updateO2CChannelTransferApproval";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered subscriberVO : ");
            loggerValue.append(channelTransferVO);
            _log.debug(METHOD_NAME,  loggerValue );
        }
        // commented for DB2 OraclePreparedStatement psmt = null;
        PreparedStatement psmt = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer(" UPDATE  channel_transfers SET  ");
            strBuff.append(" modified_by = ?, modified_on = ?,  pmt_inst_no = ? , pmt_inst_date = ?, ");
            strBuff.append(" status = ?,ext_txn_no = ? , ext_txn_date =  ? , reference_no = ?  ");
            strBuff.append(" ,payable_amount = ?, net_payable_amount = ?, pmt_inst_amount = ?, transfer_mrp = ?,info1=?,info2=?,info3=?,info4=?,info5=?,info6=?,info7=?");
            strBuff.append(",reconciliation_by = ?, reconciliation_date = ? ,reconciliation_flag = ?, reconciliation_remark = ? ");
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
                strBuff.append(",close_date=? ");
                strBuff.append(",stock_updated = ? ");
            }
            if (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(channelTransferVO.getPayInstrumentType()) && isFromPaymentGateway || channelTransferVO.isReconciliationFlag()) {
                strBuff.append(", pmt_inst_status=? ");
            }
            strBuff.append(" WHERE transfer_id = ?  AND status=? ");
            final String query = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "update query:" + query);
            }
            psmt = con.prepareStatement(query);
            int i = 0;
            ++i;
            psmt.setString(i, channelTransferVO.getModifiedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
            ++i;
            psmt.setString(i, channelTransferVO.getPayInstrumentNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getPayInstrumentDate()));
            ++i;
            psmt.setString(i, channelTransferVO.getStatus());
            ++i;
            psmt.setString(i, channelTransferVO.getExternalTxnNum());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
            ++i;
            psmt.setString(i, channelTransferVO.getReferenceNum());
            ++i;
            psmt.setLong(i, channelTransferVO.getPayableAmount());
            ++i;
            psmt.setLong(i, channelTransferVO.getNetPayableAmount());
            ++i;
            psmt.setLong(i, channelTransferVO.getPayInstrumentAmt());
            ++i;
            psmt.setLong(i, channelTransferVO.getTransferMRP());
            ++i;
            psmt.setString(i, channelTransferVO.getInfo1());
            ++i;
            psmt.setString(i, channelTransferVO.getInfo2());
            ++i;
            psmt.setString(i, channelTransferVO.getInfo3());
            ++i;
            psmt.setString(i, channelTransferVO.getInfo4());
            ++i;
            psmt.setString(i, channelTransferVO.getInfo5());
            ++i;
            psmt.setString(i, channelTransferVO.getInfo6());
            ++i;
            psmt.setString(i, channelTransferVO.getInfo7());
            ++i;
            psmt.setString(i, channelTransferVO.getFirstApprovedBy());
            ++i;
            psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
            ++i;
            psmt.setString(i, PretupsI.YES);
            ++i;
            psmt.setString(i, channelTransferVO.getFirstApprovalRemark());
            if (PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE.equals(channelTransferVO.getStatus())) {
                ++i;
                psmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
                ++i;
                psmt.setString(i, TypesI.YES);
            }
            if (PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(channelTransferVO.getPayInstrumentType()) && isFromPaymentGateway || channelTransferVO.isReconciliationFlag()) {
                ++i;
                psmt.setString(i, channelTransferVO.getPayInstrumentStatus());
            }
            ++i;
            psmt.setString(i, channelTransferVO.getTransferID());
            ++i;
            psmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
            final boolean modifiedFlag = this.isRecordModified(con, channelTransferVO.getLastModifiedTime(), channelTransferVO.getTransferID());
            if (modifiedFlag) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_MODIFY_TRUE);
            }
            updateCount = psmt.executeUpdate();
            updateCount = BTSLUtil.getInsertCount(updateCount);
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.CHANNELTRANSFER_APPROVAL_MSG_UNSUCCESS);
            }
            updateChannelTransferItems(con, channelTransferVO.getChannelTransferitemsVOList(), isOrderApproved, true,channelTransferVO.getTransferID());
        } catch (BTSLBaseException bbe) {
            throw bbe;
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQLException ");
            loggerValue.append(sqle.getMessage());
            _log.error(METHOD_NAME,  loggerValue );
            _log.errorTrace(METHOD_NAME, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    METHOD_NAME, "", "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        }// end of catch
        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append("Exception ");
            loggerValue.append(e.getMessage());
            _log.error(METHOD_NAME,  loggerValue );
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception:");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    METHOD_NAME, "", "", "",  loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_GENERAL_PROCESSING);
        }// end of catch
        finally {
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append( "Exiting Success :");
                loggerValue.append(updateCount);
                _log.debug(METHOD_NAME, loggerValue);
            }
        }// end of finally
        return updateCount;
    }

}
