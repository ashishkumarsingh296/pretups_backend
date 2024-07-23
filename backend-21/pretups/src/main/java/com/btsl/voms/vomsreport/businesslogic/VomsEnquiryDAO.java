package com.btsl.voms.vomsreport.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

/**
 * @(#)VomsEnquiryDAO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Nitin 19/07/2006 Initial Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         This class is used for PIN Enquiry & Voucher Enquiry
 * 
 */

public class VomsEnquiryDAO {
    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(VomsEnquiryDAO.class.getName());
    private static VomsEnquiryQry vomsEnquiryQry;
    private static OperatorUtilI _operatorUtilI = null;
    
    public VomsEnquiryDAO() {
		super();
		vomsEnquiryQry = (VomsEnquiryQry) ObjectProducer.getObject(
				QueryConstants.VOMS_ENQUIRY_QRY,
				QueryConstants.QUERY_PRODUCER);
	}
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "", "Exception while loading the operator util class in class :" + VomsEnquiryDAO.class.getName() + ":" + e.getMessage());
        }
    }

    /**
     * Method: getVoucherEnquiry
     * This method is used for loading the details of entered serial no.
     * 
     * @author nitin.rohilla
     * @param p_con
     *            java.sql.Connection
     * @param p_serialNo
     *            String
     * @return voucherVO VomsEnquiryVO
     * @throws BTSLBaseException
     */

    public VomsEnquiryVO getVoucherEnquiry(Connection p_con, String p_voucherType, String p_serialNo,String p_networkCode) throws java.sql.SQLException, java.lang.Exception {
        final String METHOD_NAME = "getVoucherEnquiry";
        VomsEnquiryVO vomsEnquiryVO = null;
        try {
            vomsEnquiryVO = getVoucherEnquiry_new(p_con, p_voucherType, p_serialNo,p_networkCode);
            if (vomsEnquiryVO == null && !_operatorUtilI.getNewDataAftrTbleMerging(BTSLUtil.addDaysInUtilDate(new Date(), -1), new Date())) {
                return getVoucherEnquiry_old(p_con, p_voucherType, p_serialNo);
            } else {
                return vomsEnquiryVO;
            }
        } catch (Exception e) {
            _log.error("getVoucherEnquiry", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        return vomsEnquiryVO;

    }

    private VomsEnquiryVO getVoucherEnquiry_old(Connection p_con, String p_voucherType, String p_serialNo) throws java.sql.SQLException, java.lang.Exception {
        final String METHOD_NAME = "getVoucherEnquiry_old";
        if (_log.isDebugEnabled()) {
            _log.debug("getVoucherEnquiry", "Entered: p_serialNo=" + p_serialNo);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String tablename = null;
        ResultSet rsUsage = null;
        VomsEnquiryVO voucherVO = null;
        VomsEnquiryVO voucherUsageVO = null;
        ArrayList usageDetailList = new ArrayList();
        try {
           
          String sqlVoucherUsage = vomsEnquiryQry.getVoucherEnquiry_oldSelectQry();
            if (_log.isDebugEnabled()) {
                _log.debug("getVoucherEnquiry", "Query2 :" + sqlVoucherUsage);
            }
            // making connection to get the details of voucher from DB thru
            // first qry
            pstmt = vomsEnquiryQry.getVoucherEnquiry_oldQry(p_con,p_voucherType,p_serialNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {

                voucherVO = new VomsEnquiryVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setGenerationBatchNo(rs.getString("GENBATCHNO"));
                voucherVO.setEnableBatchNo(rs.getString("ENBATHNO"));
                voucherVO.setSaleBatchNo(rs.getString("SLBATCHNO"));
                voucherVO.setMrp(rs.getLong("MRP"));
                voucherVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("MRP")));
                voucherVO.setAttemptUsed(rs.getInt("ATTEMPTUSED"));
                voucherVO.setVoucherStatus(rs.getString("STATUS"));
                voucherVO.setSelectorName(rs.getString("selector_name"));
                voucherVO.setExpiryDateStr(rs.getString("EXPIRYDATE"));
                voucherVO.setTotalValueUsed(rs.getLong("TOTALVALUEUSED"));
                voucherVO.setConsumeBeforeStr(rs.getString("CONSUMEBEFORE"));
                voucherVO.setLastConsumedBy(rs.getString("LASTCONSUMEDBY"));
                voucherVO.setLastConsumedOnStr(rs.getString("LASTCONSUMEDON"));
                voucherVO.setModifiedBy(rs.getString("MODIFIEDBY"));
                voucherVO.setModifiedOnStr(rs.getString("MODIFIEDON"));
                voucherVO.setCreatedOnStr(rs.getString("GENERATEDON"));
                voucherVO.setProductionNetworkName(rs.getString("PRODLOCNAME"));
                voucherVO.setLastUserNetworkName(rs.getString("LASTUSERLOCNAME"));
                voucherVO.setProductName(rs.getString("PRODUCTNAME"));
                voucherVO.setCategoryName(rs.getString("CATEGORYNAME"));
                voucherVO.setCategoryType(rs.getString("CATEGORYTYPE"));
                voucherVO.setDomainName(rs.getString("DOMAINNAME"));
                voucherVO.setEnabledOn(rs.getString("ENABLEDON"));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setLastConsumedOption(rs.getString("LASTCONSUMEDOPTION"));
                voucherVO.setLastAttemptNo(rs.getInt("LASTATTEMPTNO"));
                voucherVO.setAttemptType(rs.getString("ATTEMPTTYPE"));
                voucherVO.setFirstConsumedBy(rs.getString("FIRSTCONSUMEDBY"));
                voucherVO.setFirstConsumedOnStr(rs.getString("FIRSTCONSUMEDON"));
                voucherVO.setOneTimeUsage(rs.getString("ONETIMEUSAGE"));
                voucherVO.setNoOfRequests(rs.getInt("TOTALNOOFREQUEST"));
                voucherVO.setStatus(rs.getString("STATUS"));
                voucherVO.setLastRequestAttemptNo(rs.getInt("LASTREQUESTATTEMPTNO"));
                voucherVO.setAttemptAllowed(rs.getInt("ATTEMPTALLOWED"));
                voucherVO.setTotalValueAllowed(rs.getLong("TOTALVALUEALLOWED"));
                voucherVO.setTalkTimeStr(PretupsBL.getDisplayAmount(rs.getLong("TALKTIME")));
                voucherVO.setValidity(rs.getInt("VALIDITY"));
                // added by shishupal on 16/03/2007
                voucherVO.setReceiverMsisdn(rs.getString("RECEIVERMSISDN"));
                voucherVO.setSenderMsisdn(rs.getString("SENDERMSISDN"));// To
                                                                        // add
                                                                        // Sender
                                                                        // MSISDN
                                                                        // in
                                                                        // Voucher
                                                                        // enquiry[added
                                                                        // by
                                                                        // Vipul
                                                                        // on
                                                                        // 06/12/07]

                pstmt.close();
                // making connection to get the details of voucher from DB thru
                // second qry
                pstmt = p_con.prepareStatement(sqlVoucherUsage);
                pstmt.setString(1, p_serialNo);
                rsUsage = pstmt.executeQuery();
                while (rsUsage.next()) {
                    voucherUsageVO = new VomsEnquiryVO();
                    voucherUsageVO.setVoucherStatus(rsUsage.getString("STATUS"));
                    voucherUsageVO.setTotalValueUsed(rsUsage.getLong("TOTALVALUEUSED"));
                    voucherUsageVO.setUserNetworkName(rsUsage.getString("USERLOCNAME"));
                    voucherUsageVO.setPreviousStatus(rsUsage.getString("PREVIOUS_STATUS"));
                    voucherUsageVO.setValidity(rsUsage.getInt("VALIDITY"));
                    voucherUsageVO.setPurposeID(rsUsage.getString("PURPOSE_ID"));
                    voucherUsageVO.setOption(rsUsage.getString("CONSUMED_OPTION"));
                    voucherUsageVO.setAttemptNo(rsUsage.getInt("ATTEMPT_NO"));
                    voucherUsageVO.setAttemptType(rsUsage.getString("ATTEMPT_TYPE"));
                    voucherUsageVO.setPreviousBalance(rsUsage.getLong("PREVIOUS_BALANCE"));
                    voucherUsageVO.setNewBalance(rsUsage.getLong("NEW_BALANCE"));
                    voucherUsageVO.setConsumedBy(rsUsage.getString("CONSUMED_BY"));
                    voucherUsageVO.setConsumedOnStr(rsUsage.getString("CONSUMED_ON"));
                    voucherUsageVO.setRequestedBy(rsUsage.getString("REQUESTED_BY"));
                    voucherUsageVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rsUsage.getLong("TALK_TIME"))));
                    voucherUsageVO.setGracePeriod(rsUsage.getLong("GRACE_PERIOD"));
                    voucherUsageVO.setValueUsed(rsUsage.getLong("VALUE_USED"));
                    voucherUsageVO.setRequestSource(rsUsage.getString("REQUEST_SOURCE"));
                    voucherUsageVO.setRequestPartnerID(rsUsage.getString("REQUEST_PARTNER_ID"));

                    usageDetailList.add(voucherUsageVO);
                }
                voucherVO.setVoucherUsage(usageDetailList);
            }
        } catch (SQLException sqe) {
            _log.error("getVoucherEnquiry", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[getVoucherEnquiry_old]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getVoucherEnquiry", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("getVoucherEnquiry", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[getVoucherEnquiry_old]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getVoucherEnquiry", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsUsage != null) {
                    rsUsage.close();
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
                _log.debug("getVoucherEnquiry", "Exiting: List size=" + usageDetailList.size());
            }
        }
        return voucherVO;
    }// end of getVoucherEnquiry

    private VomsEnquiryVO getVoucherEnquiry_new(Connection p_con, String p_voucherType, String p_serialNo,String p_networkCode) throws java.sql.SQLException, java.lang.Exception {
        final String METHOD_NAME = "getVoucherEnquiry_new";
        if (_log.isDebugEnabled()) {
            _log.debug("getVoucherEnquiry_new", "Entered: p_serialNo=" + p_serialNo + " p_networkCode"+p_networkCode);
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        ResultSet rsUsage = null;
        String tablename = null;
        VomsEnquiryVO voucherVO = null;
        VomsEnquiryVO voucherUsageVO = null;
        ArrayList usageDetailList = new ArrayList();
        try {
           
        	String sqlVoucher = vomsEnquiryQry.getVoucherEnquiry_newQry(p_voucherType);
            if (_log.isDebugEnabled()) {
                _log.debug("getVoucherEnquiry_new", "Query1 :" + sqlVoucher);
            }

           
            String sqlVoucherUsage = vomsEnquiryQry.getVoucherEnquiry_newSelectQry();
            if (_log.isDebugEnabled()) {
                _log.debug("getVoucherEnquiry_new", "Query2 :" + sqlVoucherUsage);
            }

            // making connection to get the details of voucher from DB thru
            // first qry
            pstmt = p_con.prepareStatement(sqlVoucher);
            pstmt.setString(1, VOMSI.LOOKUP_VOUCHER_STATUS);
            pstmt.setString(2, p_serialNo);

            if (!BTSLUtil.isNullString(p_voucherType)) {
                pstmt.setString(3, p_voucherType);
                pstmt.setString(4, p_networkCode);                
            }
            else {
            	pstmt.setString(3, p_networkCode);
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {

                voucherVO = new VomsEnquiryVO();
                voucherVO.setSerialNo(rs.getString("SERIALNO"));
                voucherVO.setGenerationBatchNo(rs.getString("GENBATCHNO"));
                voucherVO.setEnableBatchNo(rs.getString("ENBATHNO"));
                voucherVO.setSaleBatchNo(rs.getString("SLBATCHNO"));
                voucherVO.setMrp(rs.getLong("MRP"));
                voucherVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("MRP")));
                voucherVO.setAttemptUsed(rs.getInt("ATTEMPTUSED"));
                voucherVO.setVoucherStatus(rs.getString("STATUS"));
                voucherVO.setSelectorName(rs.getString("selector_name"));
                voucherVO.setExpiryDateStr(rs.getString("EXPIRYDATE"));
                voucherVO.setTotalValueUsed(rs.getLong("TOTALVALUEUSED"));
                voucherVO.setConsumeBeforeStr(rs.getString("CONSUMEBEFORE"));
                voucherVO.setLastConsumedBy(BTSLDateUtil.getLocaleTimeStamp(rs.getString("LASTCONSUMEDBY")));
                voucherVO.setLastConsumedOnStr(BTSLDateUtil.getLocaleTimeStamp(rs.getString("LASTCONSUMEDON")));
                voucherVO.setModifiedBy(rs.getString("MODIFIEDBY"));
                voucherVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(rs.getString("MODIFIEDON")));
                voucherVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(rs.getString("GENERATEDON")));
                voucherVO.setProductionNetworkName(rs.getString("PRODLOCNAME"));
                voucherVO.setLastUserNetworkName(rs.getString("LASTUSERLOCNAME"));
                voucherVO.setProductName(rs.getString("PRODUCTNAME"));
                voucherVO.setCategoryName(rs.getString("CATEGORYNAME"));
                voucherVO.setCategoryType(rs.getString("CATEGORYTYPE"));
                voucherVO.setDomainName(rs.getString("DOMAINNAME"));
                voucherVO.setEnabledOn(BTSLDateUtil.getLocaleTimeStamp(rs.getString("ENABLEDON")));
                voucherVO.setPreviousStatus(rs.getString("PREVSTAT"));
                voucherVO.setLastConsumedOption(rs.getString("LASTCONSUMEDOPTION"));
                voucherVO.setLastAttemptNo(rs.getInt("LASTATTEMPTNO"));
                voucherVO.setAttemptType(rs.getString("ATTEMPTTYPE"));
                voucherVO.setFirstConsumedBy(BTSLDateUtil.getLocaleTimeStamp(rs.getString("FIRSTCONSUMEDBY")));
                voucherVO.setFirstConsumedOnStr(BTSLDateUtil.getLocaleTimeStamp(rs.getString("FIRSTCONSUMEDON")));
                voucherVO.setOneTimeUsage(rs.getString("ONETIMEUSAGE"));
                voucherVO.setNoOfRequests(rs.getInt("TOTALNOOFREQUEST"));
                voucherVO.setStatus(rs.getString("STATUS"));
                voucherVO.setLastRequestAttemptNo(rs.getInt("LASTREQUESTATTEMPTNO"));
                voucherVO.setAttemptAllowed(rs.getInt("ATTEMPTALLOWED"));
                voucherVO.setTotalValueAllowed(rs.getLong("TOTALVALUEALLOWED"));
                voucherVO.setTalkTimeStr(PretupsBL.getDisplayAmount(rs.getLong("TALKTIME")));
                voucherVO.setValidity(rs.getInt("VALIDITY"));
                // added by shishupal on 16/03/2007
                voucherVO.setReceiverMsisdn(rs.getString("SUBSCRIBER_ID"));
                voucherVO.setSenderMsisdn(rs.getString("SENDERMSISDN"));
                voucherVO.setSoldStatus(rs.getString("SOLD_STATUS"));
                voucherVO.setStatusCode(rs.getString("STATUS_CODE"));
                voucherVO.setSoldOn(rs.getDate("SOLD_DATE"));
                voucherVO.setSoldOnStr(BTSLDateUtil.getLocaleDateTimeFromDate(rs.getDate("SOLD_DATE")));
                // voucherVO.setSenderMsisdn(rs.getString("SENDERMSISDN"));//To
                // add Sender MSISDN in Voucher enquiry[added by Vipul on
                // 06/12/07]
                voucherVO.setVoucherSegment(((LookupsVO)LookupsCache.getObject("VMSSEG",rs.getString("VOUCHERSEGMENT"))).getLookupName());
                voucherVO.setVoucherType(rs.getString("VOUCHERTYPE"));
                pstmt.close();
                // making connection to get the details of voucher from DB thru
                // second qry
                pstmt1 = p_con.prepareStatement(sqlVoucherUsage);
                pstmt1.setString(1, p_serialNo);
                rsUsage = pstmt1.executeQuery();
                while (rsUsage.next()) {
                    voucherUsageVO = new VomsEnquiryVO();
                    voucherUsageVO.setVoucherStatus(rsUsage.getString("STATUS"));
                    voucherUsageVO.setTotalValueUsed(rsUsage.getLong("TOTALVALUEUSED"));
                    voucherUsageVO.setUserNetworkName(rsUsage.getString("USERLOCNAME"));
                    voucherUsageVO.setPreviousStatus(rsUsage.getString("PREVIOUS_STATUS"));
                    voucherUsageVO.setValidity(rsUsage.getInt("VALIDITY"));
                    voucherUsageVO.setPurposeID(rsUsage.getString("PURPOSE_ID"));
                    voucherUsageVO.setOption(rsUsage.getString("CONSUMED_OPTION"));
                    voucherUsageVO.setAttemptNo(rsUsage.getInt("ATTEMPT_NO"));
                    voucherUsageVO.setAttemptType(rsUsage.getString("ATTEMPT_TYPE"));
                    voucherUsageVO.setPreviousBalance(rsUsage.getLong("PREVIOUS_BALANCE"));
                    voucherUsageVO.setNewBalance(rsUsage.getLong("NEW_BALANCE"));
                    voucherUsageVO.setConsumedBy(rsUsage.getString("CONSUMED_BY"));
                    voucherUsageVO.setConsumedOnStr(BTSLDateUtil.getLocaleTimeStamp(rsUsage.getString("CONSUMED_ON")));
                    voucherUsageVO.setRequestedBy(rsUsage.getString("REQUESTED_BY"));
                    voucherUsageVO.setTalkTime(Double.parseDouble(PretupsBL.getDisplayAmount(rsUsage.getLong("TALK_TIME"))));
                    voucherUsageVO.setGracePeriod(rsUsage.getLong("GRACE_PERIOD"));
                    voucherUsageVO.setValueUsed(rsUsage.getLong("VALUE_USED"));
                    voucherUsageVO.setRequestSource(rsUsage.getString("REQUEST_SOURCE"));
                    voucherUsageVO.setRequestPartnerID(rsUsage.getString("REQUEST_PARTNER_ID"));

                    usageDetailList.add(voucherUsageVO);
                }
                voucherVO.setVoucherUsage(usageDetailList);

            }
        } catch (SQLException sqe) {
            _log.error("getVoucherEnquiry_new", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[getVoucherEnquiry_new]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "getVoucherEnquiry_new", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("getVoucherEnquiry_new", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[getVoucherEnquiry_new]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "getVoucherEnquiry_new", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsUsage != null) {
                    rsUsage.close();
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
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug("getVoucherEnquiry_new", "Exiting: List size=" + usageDetailList.size()+" VomsEnquiryVO "+voucherVO);
            }
        }
        return voucherVO;
    }// end of getVoucherEnquiry

    // added by siddhartha
    /**
     * This method loads the Vouchers reconcillation report list between the
     * entered fromDate and toDate.
     * 
     * @param p_con
     * @param p_locCode
     * @param p_locationType
     * @return arraylist
     * @throws java.sql.SQLException
     * @throws java.lang.Exception
     */
    public ArrayList loadReconcillationReportList(Connection p_con, String p_locCode, String p_subCategoryID, String p_productID, java.sql.Date fromDate, java.sql.Date toDate, String p_voucherType) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadReconcillationReportList", " Entered with p_locCode= " + p_locCode + " p_subCategoryID= " + p_subCategoryID + " p_productID= " + p_productID + " p_con= " + p_con + " fromDate= " + fromDate + " toDate= " + toDate + " p_voucherType= " + p_voucherType);
        }
        final String METHOD_NAME = "loadReconcillationReportList";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sqlInsert = vomsEnquiryQry.loadReconcillationReportListQry();
        VomsVoucherVO voucherVO = null;
        ArrayList requestList = null;

        _log.info("loadReconcillationReportList", " QUERY sql=" + sqlInsert);
        try {
            pstmt = p_con.prepareStatement(sqlInsert);
            int i = 0;

            pstmt.setDate(++i, fromDate);
            pstmt.setDate(++i, toDate);
            pstmt.setString(++i, p_locCode);
            pstmt.setString(++i, p_locCode);
            pstmt.setString(++i, p_voucherType);
            pstmt.setString(++i, p_productID);
            pstmt.setString(++i, p_productID);
            pstmt.setString(++i, p_subCategoryID);
            pstmt.setString(++i, p_subCategoryID);
            rs = pstmt.executeQuery();

            requestList = new ArrayList();

            while (rs.next()) {
                voucherVO = new VomsVoucherVO();
                voucherVO.setSerialNo(rs.getString("serial_no"));
                // using the displayAmount method to display the actual value
                // not the value entered in the database
                voucherVO.setMRP(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("mrp"))));
                voucherVO.setProductName(rs.getString("product_name"));
                voucherVO.setConsumedBy(rs.getString("last_consumed_by"));
                voucherVO.setConsumedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getVomsDateStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("last_consumed_on")))));
                voucherVO.setTransactionID(rs.getString("last_transaction_id"));
                voucherVO.setProductionLocationName(rs.getString("PRODUCTION_LOCATION"));
                voucherVO.setUserLocationName(rs.getString("USER_LOCATION"));
                voucherVO.setVoucherType(rs.getString("voucher_type"));
                voucherVO.setVoucherSegment(rs.getString("voucher_segment"));
                requestList.add(voucherVO);
            }
            return requestList;
        } catch (SQLException sqe) {
            _log.error("loadReconcillationReportList", " SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherConsumptionList]", "", "", "", "Exception:" + sqe.getMessage());
            throw new BTSLBaseException("VomsEnquiryDAO", "loadVoucherConsumptionList", "error.general.processing");
        } catch (Exception ex) {
            _log.error("loadReconcillationReportList", " Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherConsumptionList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("VomsEnquiryDAO", "loadVoucherConsumptionList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.error("loadReconcillationReportList", " Exception in closing resultset: " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.error("loadReconcillationReportList", "Exception in closing statement: " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.info("loadReconcillationReportList", " Exiting with list.size=" + requestList.size());
        }
    }

    /**
     * Method: loadVoucherTypeList
     * This method is used for loading the details of types of vouchers
     * 
     * @author akanksha.grover
     * @param p_con
     *            java.sql.Connection
     * @return VomsEnquiryVO
     * @throws BTSLBaseException
     */
    public ArrayList<VomsEnquiryVO> loadVoucherTypeList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadVoucherTypeList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<VomsEnquiryVO> list = null;
        VomsEnquiryVO vomsEnquiryVO = null;

        StringBuffer strBuff = new StringBuffer(" SELECT voucher_type, name, service_type_mapping, status");
        strBuff.append(" FROM voms_types where status = ? ORDER BY UPPER(voucher_type) ");
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList<VomsEnquiryVO>();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.STATUS_ACTIVE);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsEnquiryVO = new VomsEnquiryVO();
                vomsEnquiryVO.setVoucherType(rs.getString("voucher_type"));
                vomsEnquiryVO.setName(rs.getString("name"));
                vomsEnquiryVO.setServiceTypeMapping(rs.getString("service_type_mapping"));
                vomsEnquiryVO.setStatus(rs.getString("status"));
                list.add(vomsEnquiryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method: loadTotalDistributedSummary
     * This method is used for loading the total distributed vouchers
     * 
     * @author shaina.sahni
     * @param con
     *            java.sql.Connection
     * @return totalDistributedVouchers
     * @throws BTSLBaseException
     */
    public int loadTotalDistributedSummary(Connection con,Date fromDate, Date toDate, String userId, String productId, String networkCode, String p_loginUserid) throws BTSLBaseException {
        final String methodName = "loadTotalDistributedSummary";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        VomsEnquiryVO vomsEnquiryVO = null;
        vomsEnquiryVO = new VomsEnquiryVO();

        
         StringBuilder strBuff = new StringBuilder("select sum(total_distributed) as total_distributed from VOMS_DAILY_BURNED_VOUCHERS vdbv");
         if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        	 strBuff.append(" , voms_categories vc, voms_products vp ");
         
         strBuff.append(" WHERE vdbv.summary_date>= ? AND vdbv.summary_date<= ? ");
         if(BTSLUtil.isNullString(userId))
         {
         strBuff.append(" AND vdbv.product_id = ? ");
         }
         else if(BTSLUtil.isNullString(productId))
         {
         strBuff.append(" AND vdbv.user_id = ? AND vdbv.production_network_code = ? AND vdbv.user_network_code = ? ");
         }
         else
         {
         strBuff.append(" AND vdbv.user_id = ? AND vdbv.production_network_code = ? AND vdbv.user_network_code = ? AND vdbv.product_id = ? ");      
         }
         
     	if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        {
     		strBuff.append("  and vp.product_id = vdbv.product_id ");
            strBuff.append("  AND vp.category_id = vc.category_id ");
			strBuff.append("  AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
	    }
         
         
        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            pstmt = con.prepareStatement(sqlSelect);
            int count = 0;
            pstmt.setDate(++count, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            pstmt.setDate(++count, BTSLUtil.getSQLDateFromUtilDate(toDate));
            if(BTSLUtil.isNullString(userId))
            {
            pstmt.setString(++count, productId);
            }
            else if(BTSLUtil.isNullString(productId))
            { 
            pstmt.setString(++count, userId);
            pstmt.setString(++count, networkCode);
            pstmt.setString(++count, networkCode);
            }
            else
            {
            pstmt.setString(++count, userId);
            pstmt.setString(++count, networkCode);
            pstmt.setString(++count, networkCode);
            pstmt.setString(++count, productId);
            }
            
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
            	 pstmt.setString(++count, p_loginUserid);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                
                vomsEnquiryVO.setTotalDistributed(rs.getInt("total_distributed"));

            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: Total Distributed Vouchers=" + vomsEnquiryVO.getTotalDistributed());
            }
        }
        return vomsEnquiryVO.getTotalDistributed();
    }
    
    /**
     * Method: loadTotalConsumedSummary
     * This method is used for loading the total consumed vouchers
     * 
     * @author shaina.sahni
     * @param con
     *            java.sql.Connection
     * @return totalConsumedVouchers
     * @throws BTSLBaseException
     */
    public int loadTotalConsumedSummary(Connection con,Date fromDate, Date toDate, String userId, String productId, String networkCode,String loginUserId) throws BTSLBaseException {
        final String methodName = "loadTotalConsumedSummaryv";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        VomsEnquiryVO vomsEnquiryVO = null;
        vomsEnquiryVO = new VomsEnquiryVO();

        
        StringBuilder strBuff = new StringBuilder("select sum(total_recharged) as total_consumed from VOMS_DAILY_BURNED_VOUCHERS vdbv");
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        	 strBuff.append("	, voms_categories vc, voms_products vp ");
        	
        strBuff.append(" WHERE vdbv.summary_date>= ? AND vdbv.summary_date<= ? ");
        if(BTSLUtil.isNullString(userId))
        {
        strBuff.append(" AND vdbv.product_id = ? ");
        }
        else if(BTSLUtil.isNullString(productId))
        {
        strBuff.append(" AND vdbv.user_id = ? AND vdbv.production_network_code = ? AND vdbv.user_network_code = ? ");
        }
        else
        {
        strBuff.append(" AND vdbv.user_id = ? AND vdbv.production_network_code = ? AND vdbv.user_network_code = ? AND vdbv.product_id = ? ");      
        }
        
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
        {
     		strBuff.append("  AND vp.product_id = vdbv.product_id ");
            strBuff.append("  AND vp.category_id = vc.category_id ");
			strBuff.append("  AND vc.voucher_type in ( SELECT uv.voucher_type FROM user_vouchertypes uv, voms_types vt, users u WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id  ) ");
	    }
         
        
        
       String sqlSelect = strBuff.toString();

       if (_log.isDebugEnabled()) {
           _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
       }

       try {
           pstmt = con.prepareStatement(sqlSelect);
           int count = 0;
           pstmt.setDate(++count, BTSLUtil.getSQLDateFromUtilDate(fromDate));
           pstmt.setDate(++count, BTSLUtil.getSQLDateFromUtilDate(toDate));
           if(BTSLUtil.isNullString(userId))
           {
           pstmt.setString(++count, productId);
           }
           else if(BTSLUtil.isNullString(productId))
           { 
           pstmt.setString(++count, userId);
           pstmt.setString(++count, networkCode);
           pstmt.setString(++count, networkCode);
           }
           else
           {
           pstmt.setString(++count, userId);
           pstmt.setString(++count, networkCode);
           pstmt.setString(++count, networkCode);
           pstmt.setString(++count, productId);
           }
           
           if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue())
          	 pstmt.setString(++count, loginUserId);
            
            rs = pstmt.executeQuery();

            while (rs.next()) {
                
                vomsEnquiryVO.setTotalConsumed(rs.getInt("total_consumed"));

            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadVoucherTypeList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: Total Consumed Vouchers=" + vomsEnquiryVO.getTotalConsumed());
            }
        }
        return vomsEnquiryVO.getTotalConsumed();
    }

    /**
     * Method: loadUserCategoryList
     * This method is used for loading the details of user vouchers type
     * 
     * 
     * @param p_con
     *            java.sql.Connection
     * @return VomsEnquiryVO
     * @throws BTSLBaseException
     */
    public ArrayList<VomsEnquiryVO> loadUserCategoryList(Connection p_con,String p_userid) throws BTSLBaseException {
        final String methodName = "loadUserCategoryList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered.. ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<VomsEnquiryVO> list = null;
        VomsEnquiryVO vomsEnquiryVO = null;

       String sqlSelect = vomsEnquiryQry.loadUserCategoryList();
            if (_log.isDebugEnabled()) {
                _log.debug("getVoucherEnquiry_new", "Query1 :" + sqlSelect);
            }

       

        list = new ArrayList<VomsEnquiryVO>();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userid);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                vomsEnquiryVO = new VomsEnquiryVO();
                vomsEnquiryVO.setVoucherType(rs.getString("voucher_type"));
                vomsEnquiryVO.setName(rs.getString("name"));
                vomsEnquiryVO.setServiceTypeMapping(rs.getString("service_type_mapping"));
                vomsEnquiryVO.setStatus(rs.getString("status"));
                list.add(vomsEnquiryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "SQLException : " + sqe);
            }
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadUserCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsEnquiryDAO[loadUserCategoryList]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;
    } 
    
    /**
	 * This method is used to fetch the MRP of voucher Products	
	 * @param p_con
	 * @return
	 * @throws BTSLBaseException
	 */
		public String getMRPofVomsProducts(Connection p_con,String p_vouchertype) throws BTSLBaseException {
			final String METHOD_NAME = "getMRPofVomsProducts";
			LogFactory.printLog(METHOD_NAME, " Inside getMRPofVomsProducts=" , _log);
			PreparedStatement psmt = null;
			ResultSet rs = null;
			String vomsproductMRP= null;
			String strBuff = vomsEnquiryQry.loadMRPofVomsProducts();
			try {
				LogFactory.printLog(METHOD_NAME, " :: Query :: " + strBuff , _log);
				psmt = p_con.prepareStatement(strBuff.toString());
				psmt.setString(1,  p_vouchertype);				
				
				rs = psmt.executeQuery();
				psmt.clearParameters();
				while (rs.next()) {
					vomsproductMRP = rs.getString("mrp");
					}
				return vomsproductMRP;
			} catch (SQLException sqle) {
				_log.error(METHOD_NAME,"SQLException " + sqle.getMessage());
				_log.errorTrace(METHOD_NAME, sqle);
				throw new BTSLBaseException(this, METHOD_NAME,"error.general.sql.processing");
			}// end of catch
			catch (Exception e) {
				_log.error(METHOD_NAME, "Exception " + e.getMessage());
				_log.errorTrace(METHOD_NAME, e);
				 throw new BTSLBaseException(this, METHOD_NAME,"error.general.processing");
			}
			finally {
				try{
	        		if (rs!= null){
	        			rs.close();
	        		}
	        	}
	        	catch (SQLException e){
	        		_log.error("An error occurred closing result set.", e);
	        	}
				try{
	        		if (psmt!= null){
	        			psmt.close();
	        		}
	        	}
	        	catch (SQLException e){
	        		_log.error("An error occurred closing result set.", e);
	        	}
	        	LogFactory.printLog(METHOD_NAME,":: Exiting : vomsproductMRP = " +vomsproductMRP, _log);
				
			}
		} 
    
}
