/*
 * @(#)BatchC2SC2CTransSummaryDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Anu Garg 08/11/2011 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright(c) 2011, Comviva Technologies Ltd.
 */
package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

public class BatchC2SC2CTransSummaryDAO {
    // Field _log
    private Log _log = LogFactory.getLog(BatchC2SC2CTransSummaryDAO.class.getName());
    
    private BatchC2SC2CTransSummaryQry batchC2SC2CTransSummaryQry = (BatchC2SC2CTransSummaryQry) ObjectProducer.getObject(QueryConstants.USER_C2S_C2C_TRANS_SUMMARY_QRY, QueryConstants.QUERY_PRODUCER);

    /**
     * Method loadUserC2CDataByMsisdnOrLoginId.
     * This method validate and load c2c MIS data for user by his msisdn or
     * loginid
     * 
     * @param p_con
     *            Connection
     * @param p_loginUserID
     *            String
     * @param p_geographicalCodes
     *            String
     * @param p_accessType
     *            String
     * @param p_validDataList
     *            ArrayList
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LinkedHashMap<String, ArrayList> loadUserC2CDataByMsisdnOrLoginId(Connection p_con, String p_loginUserID, String p_geographicalCodes, String p_accessType, ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList, Date p_fromDate, Date p_toDate, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserC2CDataByMsisdnOrLoginId";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserC2CDataByMsisdnOrLoginId",
                "Entered p_validDataList: " + p_validDataList + "p_fromDate: " + p_fromDate + "p_toDate: " + p_toDate + "p_accessType: " + p_accessType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final String msisdnNotFound = p_messages.getMessage(p_locale, "user.uploadFileForC2sC2cTransSummaryRpt.error.msisdnnotfound");
        final LinkedHashMap<String, ArrayList> mapReturnedFromDao = new LinkedHashMap<>();
        ListValueVO errorVO = null;
        BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVOFromDao = null;
        final String geographicalCodesStr = p_geographicalCodes.replaceAll("'", "");
        final String gg = geographicalCodesStr.replaceAll("\" ", "");
        final String m_geographicalCodes[] = gg.split(",");
        
        final String sqlSelect =  batchC2SC2CTransSummaryQry.loadUserC2CDataByMsisdnOrLoginIdQry(p_accessType, m_geographicalCodes);

        final ArrayList<BatchC2SC2CTransSummaryVO> validList = new ArrayList<>();
        final ArrayList<ListValueVO> errorList = new ArrayList<>();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVO = null;
            int index = 0;
            for (int i = 0, j = p_validDataList.size(); i < j; i++) {
                batchC2SC2CTransSummaryVO = (BatchC2SC2CTransSummaryVO) p_validDataList.get(i);
                index = 0;
                pstmtSelect.setString(++index, p_loginUserID);
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_fromDate));
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_toDate));
                if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getMsisdn());
                } else {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getLoginId());
                }
                pstmtSelect.setString(++index, PretupsI.TRANSACTION_TYPE_C2C);
                pstmtSelect.setString(++index, PretupsI.TRANSFER_TYPE);
                for (int x = 0; x < m_geographicalCodes.length; x++) {
                    pstmtSelect.setString(++index, m_geographicalCodes[x]);
                }
                // pstmtSelect.setString(++index,p_loginUser.getGeographicalCode());
                // pstmtSelect.setString(++index,p_loginUser.getGeographicalCode());
                // pstmtSelect.setString(++index,p_loginUserID);
                rs = pstmtSelect.executeQuery();
                pstmtSelect.clearParameters();
                long count = 0L;

                while (rs.next()) {
                    count++;
                    batchC2SC2CTransSummaryVOFromDao = new BatchC2SC2CTransSummaryVO();
                    batchC2SC2CTransSummaryVOFromDao.setUserName(rs.getString("user_name"));
                    batchC2SC2CTransSummaryVOFromDao.setMsisdn(rs.getString("msisdn"));
                    batchC2SC2CTransSummaryVOFromDao.setGeographicalName(rs.getString("grph_domain_name"));
                    batchC2SC2CTransSummaryVOFromDao.setCategoryName(rs.getString("category_name"));
                    batchC2SC2CTransSummaryVOFromDao.setProductName(rs.getString("product_name"));
                    batchC2SC2CTransSummaryVOFromDao.setTransferSubType(rs.getString("transfer_sub_type"));
					batchC2SC2CTransSummaryVOFromDao.setTransInAmount(String.valueOf((Long.parseLong(rs.getString("in_amount"))/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue())));
                    batchC2SC2CTransSummaryVOFromDao.setTransInCount(rs.getString("in_count"));
					batchC2SC2CTransSummaryVOFromDao.setTransOutAmount(String.valueOf((Long.parseLong(rs.getString("out_amount"))/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue())));
                    batchC2SC2CTransSummaryVOFromDao.setTransOutCount(rs.getString("out_count"));
                    validList.add(batchC2SC2CTransSummaryVOFromDao);
                }
                if (count == 0) {
                    errorVO = new ListValueVO();
                    if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getMsisdn());
                    } else {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getLoginId());
                    }
                    errorVO.setOtherInfo(batchC2SC2CTransSummaryVO.getRecordNumber());
                    errorVO.setOtherInfo2(msisdnNotFound);
                    errorList.add(errorVO);
                    continue;
                }
            }
            mapReturnedFromDao.put("Valid", validList);
            mapReturnedFromDao.put("Error", errorList);
        } catch (SQLException sqe) {
            _log.error("loadUserC2CDataByMsisdnOrLoginId", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2CDataByMsisdnOrLoginId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserC2CDataByMsisdnOrLoginId", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserC2CDataByMsisdnOrLoginId", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2CDataByMsisdnOrLoginId]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserC2CDataByMsisdnOrLoginId", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserC2CDataByMsisdnOrLoginId", "Exiting:  ErrorList Size = " + errorList.size() + "  ValidList Size = " + validList.size());
            }
        }
        return mapReturnedFromDao;
    }

    /**
     * Method loadUserC2SDataByMsisdnOrLoginId.
     * This method validate and load c2s MIS data for user by his msisdn or
     * loginid
     * 
     * @param p_con
     *            Connection
     * @param p_loginUserID
     *            String
     * @param p_geographicalCodes
     *            String
     * @param p_accessType
     *            String
     * @param p_validDataList
     *            ArrayList
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LinkedHashMap<String, ArrayList> loadUserC2SDataByMsisdnOrLoginId(Connection p_con, String p_loginUserID, String p_geographicalCodes, String p_accessType, ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList, Date p_fromDate, Date p_toDate, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserC2SDataByMsisdnOrLoginId";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserC2SDataByMsisdnOrLoginId",
                "Entered p_validDataList: " + p_validDataList + "p_fromDate: " + p_fromDate + "p_toDate: " + p_toDate + "p_accessType: " + p_accessType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final String msisdnNotFound = p_messages.getMessage(p_locale, "user.uploadFileForC2sC2cTransSummaryRpt.error.msisdnnotfound");
        final LinkedHashMap<String, ArrayList> mapReturnedFromDao = new LinkedHashMap<>();
        ListValueVO errorVO = null;
        BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVOFromDao = null;
        
        final String sqlSelect =  batchC2SC2CTransSummaryQry.loadUserC2SDataByMsisdnOrLoginIdQry(p_loginUserID, p_geographicalCodes);
 
        final ArrayList<BatchC2SC2CTransSummaryVO> validList = new ArrayList<>();
        final ArrayList<ListValueVO> errorList = new ArrayList<>();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVO = null;
            int index = 0;
            for (int i = 0, j = p_validDataList.size(); i < j; i++) {
                batchC2SC2CTransSummaryVO = (BatchC2SC2CTransSummaryVO) p_validDataList.get(i);
                index = 0;
                pstmtSelect.setString(++index, p_loginUserID);
                // pstmtSelect.setString(++index,p_loginUser.getUserID());
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_fromDate));
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_toDate));
                if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getMsisdn());
                } else {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getLoginId());
                }
                // pstmtSelect.setString(++index,p_loginUser.getGeographicalCode());
                // pstmtSelect.setString(++index,p_loginUser.getGeographicalCode());
                // pstmtSelect.setString(++index,p_loginUser.getUserID());
                rs = pstmtSelect.executeQuery();
                pstmtSelect.clearParameters();
                long count = 0L;

                while (rs.next()) {
                    count++;
                    batchC2SC2CTransSummaryVOFromDao = new BatchC2SC2CTransSummaryVO();
                    batchC2SC2CTransSummaryVOFromDao.setUserName(rs.getString("user_name"));
                    batchC2SC2CTransSummaryVOFromDao.setMsisdn(rs.getString("msisdn"));
                    batchC2SC2CTransSummaryVOFromDao.setGeographicalName(rs.getString("grph_domain_name"));
                    batchC2SC2CTransSummaryVOFromDao.setCategoryName(rs.getString("category_name"));
                    batchC2SC2CTransSummaryVOFromDao.setServiceType(rs.getString("name"));
                    batchC2SC2CTransSummaryVOFromDao.setC2STotalTransactions(Integer.toString(Integer.parseInt(rs.getString("success_transaction_count")) + Integer
                        .parseInt(rs.getString("failure_count"))));
                    batchC2SC2CTransSummaryVOFromDao.setC2STotalFailTransactions(rs.getString("failure_count"));
                    batchC2SC2CTransSummaryVOFromDao.setC2sRechargeCount(rs.getString("success_transaction_count"));
                    batchC2SC2CTransSummaryVOFromDao.setC2sRechargeAmount(rs.getString("success_transaction_amount"));
                    validList.add(batchC2SC2CTransSummaryVOFromDao);
                }
                if (count == 0) {
                    errorVO = new ListValueVO();
                    if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getMsisdn());
                    } else {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getLoginId());
                    }
                    errorVO.setOtherInfo(batchC2SC2CTransSummaryVO.getRecordNumber());
                    errorVO.setOtherInfo2(msisdnNotFound);
                    errorList.add(errorVO);
                    continue;
                }
            }
            mapReturnedFromDao.put("Valid", validList);
            mapReturnedFromDao.put("Error", errorList);
        } catch (SQLException sqe) {
            _log.error("loadUserC2SDataByMsisdnOrLoginId", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2SDataByMsisdnOrLoginId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserC2SDataByMsisdnOrLoginId", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserC2SDataByMsisdnOrLoginId", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2SDataByMsisdnOrLoginId]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserC2SDataByMsisdnOrLoginId", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserC2CDataByMsisdnOrLoginId", "Exiting:  ErrorList Size = " + errorList.size() + "  ValidList Size = " + validList.size());
            }
        }
        return mapReturnedFromDao;
    }

    /**
     * Method loadUserC2CDataByMsisdnOrLoginIdForOperator.
     * This method validate and load c2c MIS data for user by his msisdn or
     * loginid for operator
     * 
     * @param p_con
     *            Connection
     * @param p_loginUserID
     *            String
     * @param p_geographicalCodes
     *            String
     * @param p_accessType
     *            String
     * @param p_validDataList
     *            ArrayList
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LinkedHashMap<String, ArrayList> loadUserC2CDataByMsisdnOrLoginIdForOperator(Connection p_con, String p_loginUserID, String p_geographicalCodes, String p_accessType, ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList, Date p_fromDate, Date p_toDate, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserC2CDataByMsisdnOrLoginIdForOperator";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserC2CDataByMsisdnOrLoginIdForOperator",
                "Entered p_validDataList: " + p_validDataList + "p_fromDate: " + p_fromDate + "p_toDate: " + p_toDate + "p_accessType: " + p_accessType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final String msisdnNotFound = p_messages.getMessage(p_locale, "user.uploadFileForC2sC2cTransSummaryRpt.error.msisdnnotfound");
        final LinkedHashMap<String, ArrayList> mapReturnedFromDao = new LinkedHashMap<>();
        ListValueVO errorVO = null;
        BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVOFromDao = null;

        final String sqlSelect = batchC2SC2CTransSummaryQry.loadUserC2CDataByMsisdnOrLoginIdForOperatorQry(p_accessType, p_geographicalCodes);
   
        final ArrayList<BatchC2SC2CTransSummaryVO> validList = new ArrayList<>();
        final ArrayList<ListValueVO> errorList = new ArrayList<>();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVO = null;
            int index = 0;
            for (int i = 0, j = p_validDataList.size(); i < j; i++) {
                batchC2SC2CTransSummaryVO = (BatchC2SC2CTransSummaryVO) p_validDataList.get(i);
                index = 0;
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_fromDate));
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_toDate));
                if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getMsisdn());
                } else {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getLoginId());
                }
                pstmtSelect.setString(++index, PretupsI.TRANSACTION_TYPE_C2C);
                pstmtSelect.setString(++index, PretupsI.TRANSFER_TYPE);
                rs = pstmtSelect.executeQuery();
                pstmtSelect.clearParameters();
                long count = 0L;

                while (rs.next()) {
                    count++;
                    batchC2SC2CTransSummaryVOFromDao = new BatchC2SC2CTransSummaryVO();
                    batchC2SC2CTransSummaryVOFromDao.setUserName(rs.getString("user_name"));
                    batchC2SC2CTransSummaryVOFromDao.setMsisdn(rs.getString("msisdn"));
                    batchC2SC2CTransSummaryVOFromDao.setGeographicalName(rs.getString("grph_domain_name"));
                    batchC2SC2CTransSummaryVOFromDao.setCategoryName(rs.getString("category_name"));
                    batchC2SC2CTransSummaryVOFromDao.setProductName(rs.getString("product_name"));
                    batchC2SC2CTransSummaryVOFromDao.setTransferSubType(rs.getString("transfer_sub_type"));
					batchC2SC2CTransSummaryVOFromDao.setTransInAmount(String.valueOf((Long.parseLong(rs.getString("in_amount"))/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue())));
                    batchC2SC2CTransSummaryVOFromDao.setTransInCount(rs.getString("in_count"));
					batchC2SC2CTransSummaryVOFromDao.setTransOutAmount(String.valueOf((Long.parseLong(rs.getString("out_amount"))/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue())));
                    batchC2SC2CTransSummaryVOFromDao.setTransOutCount(rs.getString("out_count"));
                    validList.add(batchC2SC2CTransSummaryVOFromDao);
                }
                if (count == 0) {
                    errorVO = new ListValueVO();
                    if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getMsisdn());
                    } else {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getLoginId());
                    }
                    errorVO.setOtherInfo(batchC2SC2CTransSummaryVO.getRecordNumber());
                    errorVO.setOtherInfo2(msisdnNotFound);
                    errorList.add(errorVO);
                    continue;
                }
            }
            mapReturnedFromDao.put("Valid", validList);
            mapReturnedFromDao.put("Error", errorList);
        } catch (SQLException sqe) {
            _log.error("loadUserC2CDataByMsisdnOrLoginIdForOperator", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2CDataByMsisdnOrLoginIdForOperator]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserC2CDataByMsisdnOrLoginIdForOperator", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserC2CDataByMsisdnOrLoginIdForOperator", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2CDataByMsisdnOrLoginIdForOperator]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserC2CDataByMsisdnOrLoginIdForOperator", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserC2CDataByMsisdnOrLoginIdForOperator", "Exiting:  ErrorList Size = " + errorList.size() + "  ValidList Size = " + validList.size());
            }
        }
        return mapReturnedFromDao;
    }

    /**
     * Method loadUserC2SDataByMsisdnOrLoginIdForOperator.
     * This method validate and load c2s MIS data for user by his msisdn or
     * loginid for operator
     * 
     * @param p_con
     *            Connection
     * @param p_loginUserID
     *            String
     * @param p_geographicalCodes
     *            String
     * @param p_accessType
     *            String
     * @param p_validDataList
     *            ArrayList
     * @param p_fromDate
     *            Date
     * @param p_toDate
     *            Date
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public LinkedHashMap<String, ArrayList> loadUserC2SDataByMsisdnOrLoginIdForOperator(Connection p_con, String p_loginUserID, String p_geographicalCodes, String p_accessType, ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList, Date p_fromDate, Date p_toDate, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserC2SDataByMsisdnOrLoginIdForOperator";
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserC2SDataByMsisdnOrLoginIdForOperator",
                "Entered p_validDataList: " + p_validDataList + "p_fromDate: " + p_fromDate + "p_toDate: " + p_toDate + "p_accessType: " + p_accessType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final String msisdnNotFound = p_messages.getMessage(p_locale, "user.uploadFileForC2sC2cTransSummaryRpt.error.msisdnnotfound");
        final LinkedHashMap<String, ArrayList> mapReturnedFromDao = new LinkedHashMap<>();
        ListValueVO errorVO = null;
        BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVOFromDao = null;
      
        final String sqlSelect = batchC2SC2CTransSummaryQry.loadUserC2SDataByMsisdnOrLoginIdForOperatorQry(p_accessType, p_geographicalCodes);
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserC2SDataByMsisdnOrLoginIdForOperator", "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList<BatchC2SC2CTransSummaryVO> validList = new ArrayList<>();
        final ArrayList<ListValueVO> errorList = new ArrayList<>();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVO = null;
            int index = 0;
            for (int i = 0, j = p_validDataList.size(); i < j; i++) {
                batchC2SC2CTransSummaryVO = (BatchC2SC2CTransSummaryVO) p_validDataList.get(i);
                index = 0;
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_fromDate));
                pstmtSelect.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(p_toDate));
                if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getMsisdn());
                } else {
                    pstmtSelect.setString(++index, batchC2SC2CTransSummaryVO.getLoginId());
                }
                rs = pstmtSelect.executeQuery();
                pstmtSelect.clearParameters();
                long count = 0L;

                while (rs.next()) {
                    count++;
                    batchC2SC2CTransSummaryVOFromDao = new BatchC2SC2CTransSummaryVO();
                    batchC2SC2CTransSummaryVOFromDao.setUserName(rs.getString("user_name"));
                    batchC2SC2CTransSummaryVOFromDao.setMsisdn(rs.getString("msisdn"));
                    batchC2SC2CTransSummaryVOFromDao.setGeographicalName(rs.getString("grph_domain_name"));
                    batchC2SC2CTransSummaryVOFromDao.setCategoryName(rs.getString("category_name"));
                    batchC2SC2CTransSummaryVOFromDao.setServiceType(rs.getString("name"));
                    batchC2SC2CTransSummaryVOFromDao.setC2STotalTransactions(Integer.toString(Integer.parseInt(rs.getString("success_transaction_count")) + Integer
                        .parseInt(rs.getString("failure_count"))));
                    batchC2SC2CTransSummaryVOFromDao.setC2STotalFailTransactions(rs.getString("failure_count"));
                    batchC2SC2CTransSummaryVOFromDao.setC2sRechargeCount(rs.getString("success_transaction_count"));
                    // Added By Diwakar for OCM on 26-MAY-2014
                    // batchC2SC2CTransSummaryVOFromDao.setC2sRechargeAmount(rs.getString("success_transaction_amount")/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
                    batchC2SC2CTransSummaryVOFromDao.setC2sRechargeAmount(String
                        .valueOf(Long.parseLong(rs.getString("success_transaction_amount")) / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()));
                    // Ended Here
                    validList.add(batchC2SC2CTransSummaryVOFromDao);
                }
                if (count == 0) {
                    errorVO = new ListValueVO();
                    if (PretupsI.LOOKUP_MSISDN.equals(p_accessType)) {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getMsisdn());
                    } else {
                        errorVO.setCodeName(batchC2SC2CTransSummaryVO.getLoginId());
                    }
                    errorVO.setOtherInfo(batchC2SC2CTransSummaryVO.getRecordNumber());
                    errorVO.setOtherInfo2(msisdnNotFound);
                    errorList.add(errorVO);
                    continue;
                }
            }
            mapReturnedFromDao.put("Valid", validList);
            mapReturnedFromDao.put("Error", errorList);
        } catch (SQLException sqe) {
            _log.error("loadUserC2SDataByMsisdnOrLoginIdForOperator", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2SDataByMsisdnOrLoginIdForOperator]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserC2SDataByMsisdnOrLoginIdForOperator", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadUserC2SDataByMsisdnOrLoginIdForOperator", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "BatchC2SC2CTransSummaryDAO[loadUserC2SDataByMsisdnOrLoginIdForOperator]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadUserC2SDataByMsisdnOrLoginIdForOperator", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserC2SDataByMsisdnOrLoginIdForOperator", "Exiting:  ErrorList Size = " + errorList.size() + "  ValidList Size = " + validList.size());
            }
        }
        return mapReturnedFromDao;
    }

}
