package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;

public class TxnEnqDAO {
    private static Log _log = LogFactory.getLog(TxnEnqDAO.class.getName());
    public static OperatorUtilI _operatorUtilI = null;
    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "", "", "", "",
                "Exception while loading the operator util class in class :" + TxnEnqDAO.class.getName() + ":" + e.getMessage());
        }
    }

    public void loadDetailsFrmTxnIDO2C(Connection p_con, RequestVO p_requestVO, int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadDetailsFrmTxnIDO2C", "Entered p_requestVO:" + p_requestVO);
        }
        final String METHOD_NAME = "loadDetailsFrmTxnIDO2C";
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelectCount = null;
        final TxnEnqDAO privateUserVO = null;
        ResultSet rs = null;
        ResultSet rscount = null;
        final HashMap records = new HashMap();
        HashMap compare = new HashMap();
        final String message[] = p_requestVO.getRequestMessageArray();
        int target = 0;
        int count = 0;
        int i = 0;
        Boolean success = false;
        try {

            compare = p_requestVO.getRequestMap();
            // Date
            // from_date=BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(message[7]));
            // Date
            // to_date=BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(message[8]));
            final StringBuffer countQueryBuff = new StringBuffer(" select count(transfer_id) as rno");
            countQueryBuff.append(" FROM channel_transfers CT, products P,users U");
            final StringBuffer selectQueryBuff = new StringBuffer(" select transfer_id,reference_no,ct.status,ct.msisdn,to_msisdn,ext_txn_date, ");
            selectQueryBuff
                .append("p.product_code,U.external_code AS uec,requested_quantity,transfer_category,pmt_inst_type,pmt_inst_no,pmt_inst_date,first_approver_remarks,second_approver_remarks,third_approver_remarks ");
            selectQueryBuff.append(" FROM channel_transfers CT, products P,users U");

            switch (p_action) {
            case 1:
                selectQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_id=? and CT.to_user_id=U.user_id and CT.network_code=?");
                countQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_id=? and CT.to_user_id=U.user_id and CT.network_code=?");
                target = 1;
                break;
            case 2:
                selectQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_id=? and CT.from_user_id=U.user_id and CT.network_code=?");
                countQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_id=? and CT.from_user_id=U.user_id and CT.network_code=?");
                target = 1;
                break;

            case 3:
                selectQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and reference_no=? and CT.to_user_id=U.user_id and CT.network_code=?");
                countQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and reference_no=? and CT.to_user_id=U.user_id and CT.network_code=?");
                target = 2;
                break;
            case 4: {
                selectQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and reference_no=? and CT.from_user_id=U.user_id and CT.network_code=?");
                countQueryBuff.append(" where CT.product_type=P.PRODUCT_TYPE and type=? and reference_no=? and CT.from_user_id=U.user_id and CT.network_code=?");
                target = 2;
                break;
            }
            case 7: {
                selectQueryBuff
                    .append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_date >=? and transfer_date<=? and CT.to_user_id=U.user_id and CT.network_code=?");
                countQueryBuff
                    .append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_date >=? and transfer_date<=? and CT.to_user_id=U.user_id and CT.network_code=?");
                target = 3;
                break;
            }
            case 8: {
                selectQueryBuff
                    .append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_date >=? and transfer_date<=? and CT.from_user_id=U.user_id and CT.network_code=?");
                countQueryBuff
                    .append(" where CT.product_type=P.PRODUCT_TYPE and type=? and transfer_date >=? and transfer_date<=? and CT.from_user_id=U.user_id and CT.network_code=?");
                target = 3;
                break;
            }
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , p_action);
             	 }
            }
            final String selectQuery = selectQueryBuff.toString();
            final String countQuery = countQueryBuff.toString();

            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDO2C", "select query:" + selectQuery);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDO2C", "count query:" + countQuery);
            }
            pstmtSelectCount = p_con.prepareStatement(countQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            switch (target) {
            case 1:

                pstmtSelect.setString(1, (String) compare.get("TXNTYPE"));
                pstmtSelect.setString(2, (String) compare.get("TXNID"));
                pstmtSelect.setString(3, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setString(1, (String) compare.get("TXNTYPE"));
                pstmtSelectCount.setString(2, (String) compare.get("TXNID"));
                pstmtSelectCount.setString(3, (String) compare.get("EXTNWCODE"));
                break;

            case 2:

                pstmtSelect.setString(1, (String) compare.get("TXNTYPE"));
                pstmtSelect.setString(2, (String) compare.get("EXTREFNUM"));
                pstmtSelect.setString(3, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setString(1, (String) compare.get("TXNTYPE"));
                pstmtSelectCount.setString(2, (String) compare.get("EXTREFNUM"));
                pstmtSelectCount.setString(3, (String) compare.get("EXTNWCODE"));
                break;

            case 3:
                pstmtSelect.setString(1, (String) compare.get("TXNTYPE"));
                pstmtSelect.setTimestamp(2, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("FROMDATE"))));
                pstmtSelect.setTimestamp(3, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("TODATE"))));
                pstmtSelect.setString(4, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setString(1, (String) compare.get("TXNTYPE"));
                pstmtSelectCount.setTimestamp(2, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("FROMDATE"))));
                pstmtSelectCount.setTimestamp(3, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("TODATE"))));
                pstmtSelectCount.setString(4, (String) compare.get("EXTNWCODE"));
                break;
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , target);
             	 }
            }
            rscount = pstmtSelectCount.executeQuery();

            while (rscount.next()) {
                count = rscount.getInt("rno");
            }
            if (count == 0) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_RECORD_AVAILABLE);
                success = false;
                throw new BTSLBaseException(this, "loadDetailsFrmTxnIDO2C", PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT, 0, null, null);
            } else if (count > PretupsI.TXNID_COUNT) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT);
                success = false;
                throw new BTSLBaseException(this, "loadDetailsFrmTxnIDO2C", PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT, 0, null, null);
            } else {
                rs = pstmtSelect.executeQuery();
                ChannelTransferVO channeltransferVO = null;
                while (rs.next()) {
                    channeltransferVO = new ChannelTransferVO();
                    if (i > count) {
                        break;
                    }
                    channeltransferVO.setTransferID(rs.getString("transfer_id"));
                    channeltransferVO.setReferenceNum(rs.getString("reference_no"));
                    channeltransferVO.setStatusDesc(rs.getString("status"));
                    channeltransferVO.setUserMsisdn(rs.getString("msisdn"));
                    channeltransferVO.setToUserMsisdn(rs.getString("to_msisdn"));
                    channeltransferVO.setExternalTxnDateAsString(rs.getString("ext_txn_date"));
                    channeltransferVO.setProductCode(rs.getString("product_code"));
                    channeltransferVO.setErpNum(rs.getString("uec"));
                    channeltransferVO.setSenderDrQty(rs.getLong("requested_quantity"));
                    channeltransferVO.setTransferCategory(rs.getString("transfer_category"));
                    channeltransferVO.setPayInstrumentType(rs.getString("pmt_inst_type"));
                    channeltransferVO.setPayInstrumentNum(rs.getString("pmt_inst_no"));
                    channeltransferVO.setPayInstrumentDate(rs.getDate("pmt_inst_date"));
                    if (rs.getString("first_approver_remarks") != null) {
                        channeltransferVO.setFirstApprovalRemark(rs.getString("first_approver_remarks"));
                    } else if (rs.getString("second_approver_remarks") != null) {
                        channeltransferVO.setSecondApprovalRemark(rs.getString("second_approver_remarks"));
                    } else if (rs.getString("third_approver_remarks") != null) {
                        channeltransferVO.setThirdApprovalRemark(rs.getString("third_approver_remarks"));
                    }
                    records.put((Integer) i, channeltransferVO);
                    success = true;
                    i++;
                }
            }

            if (success) {
                p_requestVO.setRequestMap(records);
                p_requestVO.setRemotePort(count);
                p_requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);
            }
            if (count == 0) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_RECORD_AVAILABLE);
            }
            p_requestVO.setSuccessTxn(success);
            p_requestVO.setType(message[1]);
        }// end of try
        catch (SQLException sqle) {
            _log.error("loadDetailsFrmTxnIDO2C", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadDetailsFrmTxnIDO2C", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadDetailsFrmTxnIDO2C", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadChannelUserDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadDetailsFrmTxnIDO2C", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rscount != null) {
                    rscount.close();
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
            try {
                if (pstmtSelectCount != null) {
                    pstmtSelectCount.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDO2C", "Exiting channelUserVO:" + privateUserVO);
            }
        }// end of finally

    }

    public void loadDetailsFrmTxnIDC2S(Connection p_con, RequestVO p_requestVO, int p_action) throws BTSLBaseException {
        final String METHOD_NAME = "loadDetailsFrmTxnIDC2S";
        boolean old_executed = false;
        boolean executed_successfully = false;
        try {
            loadDetailsFrmTxnIDC2S_new(p_con, p_requestVO, p_action);
            executed_successfully = true;
        } catch (BTSLBaseException e) {
            if (PretupsErrorCodesI.NO_RECORD_AVAILABLE.equals(p_requestVO.getMessageCode()) && !_operatorUtilI.getNewDataAftrTbleMerging(BTSLUtil.addDaysInUtilDate(
                new Date(), -1), new Date())) {
                try {
                    loadDetailsFrmTxnIDC2S_old(p_con, p_requestVO, p_action);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                }
                old_executed = true;
                return;
            }
            throw e;
        }

    }

    private void loadDetailsFrmTxnIDC2S_old(Connection p_con, RequestVO p_requestVO, int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadDetailsFrmTxnIDC2S_old", "Entered p_requestVO:" + p_requestVO);
        }
        final String METHOD_NAME = "loadDetailsFrmTxnIDC2S_old";
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelectCount = null;
        final TxnEnqDAO privateUserVO = null;
        ResultSet rs = null;
        ResultSet rscount = null;
        final HashMap records = new HashMap();
        HashMap compare = new HashMap();
        int target = 0;
        int count = 0;
        int i = 0;
        Boolean success = false;
        try {
            // Date
            // from_date=BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(message[7]));
            // Date
            // to_date=BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(message[8]));
            final String message[] = p_requestVO.getRequestMessageArray();
            compare = p_requestVO.getRequestMap();
            final StringBuffer selectQueryBuff = new StringBuffer(" select CT.transfer_id,CT.transfer_status,ct.sender_msisdn,ct.receiver_msisdn, ");
            selectQueryBuff.append("CT.product_code,U.external_code AS uec,ct.quantity  ");
            final StringBuffer countQueryBuff = new StringBuffer(" select count(1)as no");
            selectQueryBuff.append(" from c2s_transfers_old CT,users U  ");
            countQueryBuff.append(" from c2s_transfers_old CT,users U  ");

            switch (p_action) {
            case 5:
                selectQueryBuff.append(" where CT.sender_id=U.user_id and CT.transfer_id=? and CT.network_code=? ");
                countQueryBuff.append(" where CT.sender_id=U.user_id and CT.transfer_id=? and CT.network_code=? ");
                target = 1;
                break;

            case 6:
                selectQueryBuff.append(" where CT.sender_id=U.user_id and CT.reference_id=? and CT.network_code=? ");
                countQueryBuff.append(" where CT.sender_id=U.user_id and CT.reference_id=? and CT.network_code=? ");
                target = 2;
                break;

            case 9:
                selectQueryBuff.append(" where CT.sender_id=U.user_id and CT.transfer_date >=? and CT.transfer_date<=? and CT.network_code=?");
                countQueryBuff.append(" where CT.sender_id=U.user_id and CT.transfer_date >=? and CT.transfer_date<=? and CT.network_code=?");
                target = 3;
                break;
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , p_action);
             	 }
            }
            final String selectQuery = selectQueryBuff.toString();
            final String countQuery = countQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDC2S_old", "select query:" + selectQuery);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDC2S_old", "count query:" + countQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelectCount = p_con.prepareStatement(countQuery);
            switch (target) {
            case 1:

                pstmtSelect.setString(1, (String) compare.get("TXNID"));
                pstmtSelect.setString(2, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setString(1, (String) compare.get("TXNID"));
                pstmtSelectCount.setString(2, (String) compare.get("EXTNWCODE"));
                break;

            case 2:

                pstmtSelect.setString(1, (String) compare.get("EXTREFNUM"));
                pstmtSelect.setString(2, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setString(1, (String) compare.get("EXTREFNUM"));
                pstmtSelectCount.setString(2, (String) compare.get("EXTNWCODE"));
                break;

            case 3:
                pstmtSelect.setTimestamp(1, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("FROMDATE"))));
                pstmtSelect.setTimestamp(2, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("TODATE"))));
                pstmtSelect.setString(3, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setTimestamp(1, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("FROMDATE"))));
                pstmtSelectCount.setTimestamp(2, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("TODATE"))));
                pstmtSelectCount.setString(3, (String) compare.get("EXTNWCODE"));
                break;
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , target);
             	 }
            }
            rscount = pstmtSelectCount.executeQuery();

            while (rscount.next()) {
                count = rscount.getInt("no");
            }
            if (count == 0) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_RECORD_AVAILABLE);
                success = false;
                throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S", PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT, 0, null, null);
            } else if (count > PretupsI.TXNID_COUNT) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT);
                success = false;
                throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S", PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT, 0, null, null);
            }

            else {
                rs = pstmtSelect.executeQuery();
                C2STransferVO c2sTransferVO = null;
                // c2sTransferVO.setType(message[1]);
                // if (!PretupsI.ENQ_TXNID_NVL.equalsIgnoreCase(message[3]))
                // c2sTransferVO.setTransferDate(BTSLUtil.getDateFromDateString(message[3]));
                while (rs.next()) {
                    c2sTransferVO = new C2STransferVO();
                    if (i > count) {
                        break;
                    }
                    c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                    c2sTransferVO.setStatus(rs.getString("transfer_status"));
                    c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                    c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                    c2sTransferVO.setProductCode(rs.getString("product_code"));
                    c2sTransferVO.setValue(rs.getString("uec"));
                    c2sTransferVO.setRequestedAmount(rs.getLong("quantity"));
                    records.put((Integer) i, c2sTransferVO);
                    success = true;
                    i++;
                }
            }
            if (success) {
                p_requestVO.setRequestMap(records);
                p_requestVO.setRemotePort(count);
                p_requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);
            }
            if (count == 0) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_RECORD_AVAILABLE);
            }
            p_requestVO.setSuccessTxn(success);
            p_requestVO.setType(message[1]);
        }// end of try

        catch (SQLException sqle) {
            _log.error("loadDetailsFrmTxnIDC2S_old", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadDetailsFrmTxnIDC2S_old]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadDetailsFrmTxnIDC2S_old", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadDetailsFrmTxnIDC2S_old]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rscount != null) {
                    rscount.close();
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
            try {
                if (pstmtSelectCount != null) {
                    pstmtSelectCount.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDC2S_old", "Exiting channelUserVO:" + privateUserVO);
            }
        }// end of finally

    }

    public void loadDetailsFrmTxnIDC2S_new(Connection p_con, RequestVO p_requestVO, int p_action) throws BTSLBaseException {

        if (_log.isDebugEnabled()) {
            _log.debug("loadDetailsFrmTxnIDC2S_new", "Entered p_requestVO:" + p_requestVO);
        }
        final String METHOD_NAME = "loadDetailsFrmTxnIDC2S_new";
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelectCount = null;
        final TxnEnqDAO privateUserVO = null;
        ResultSet rs = null;
        ResultSet rscount = null;
        final HashMap records = new HashMap();
        HashMap compare = new HashMap();
        int target = 0;
        int count = 0;
        int i = 0;
        Boolean success = false;
        try {
        	  int maxlastTrfDays = (int)PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LAST_TRANSFERS_DAYS);
            final String message[] = p_requestVO.getRequestMessageArray();
            compare = p_requestVO.getRequestMap();
            final StringBuffer selectQueryBuff = new StringBuffer(" select CT.transfer_id,CT.transfer_status,ct.sender_msisdn,ct.receiver_msisdn, ");
            selectQueryBuff.append("CT.product_code,U.external_code AS uec,ct.quantity  ");
            final StringBuffer countQueryBuff = new StringBuffer(" select count(1)as no");
            selectQueryBuff.append(" from c2s_transfers CT,users U  ");
            countQueryBuff.append(" from c2s_transfers CT,users U  ");

            switch (p_action) {
            case 5:
                selectQueryBuff.append(" where CT.transfer_date=? and CT.sender_id=U.user_id and CT.transfer_id=? and CT.network_code=? ");
                countQueryBuff.append(" where CT.transfer_date=? and CT.sender_id=U.user_id and CT.transfer_id=? and CT.network_code=? ");
                target = 1;
                break;

            case 6:
                selectQueryBuff.append(" where CT.transfer_date=? and CT.sender_id=U.user_id and CT.reference_id=? and CT.network_code=? ");
                countQueryBuff.append(" where CT.transfer_date=? and CT.sender_id=U.user_id and CT.reference_id=? and CT.network_code=? ");
                target = 2;
                break;

            case 9:
                selectQueryBuff.append(" where CT.sender_id=U.user_id and CT.transfer_date >=? and CT.transfer_date<=? and CT.network_code=?");
                countQueryBuff.append(" where CT.sender_id=U.user_id and CT.transfer_date >=? and CT.transfer_date<=? and CT.network_code=?");
                target = 3;
                break;
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , p_action);
             	 }
            }
            final String selectQuery = selectQueryBuff.toString();
            final String countQuery = countQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDC2S_new", "select query:" + selectQuery);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDC2S_new", "count query:" + countQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelectCount = p_con.prepareStatement(countQuery);
            switch (target) {
            case 1:
            	pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId((String) compare.get("TXNID"))));
                pstmtSelect.setString(2, (String) compare.get("TXNID"));
                pstmtSelect.setString(3, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId((String) compare.get("TXNID"))));
                pstmtSelectCount.setString(2, (String) compare.get("TXNID"));
                pstmtSelectCount.setString(3, (String) compare.get("EXTNWCODE"));
                break;

            case 2:
            	final Calendar cal = BTSLDateUtil.getInstance();
        		java.util.Date dt = cal.getTime(); // Current Date
                dt = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(dt, maxlastTrfDays), PretupsI.DATE_FORMAT));
                pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dt));
                pstmtSelect.setString(2, (String) compare.get("EXTREFNUM"));
                pstmtSelect.setString(3, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dt));
                pstmtSelectCount.setString(2, (String) compare.get("EXTREFNUM"));
                pstmtSelectCount.setString(3, (String) compare.get("EXTNWCODE"));
                break;

            case 3:
                pstmtSelect.setTimestamp(1, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("FROMDATE"))));
                pstmtSelect.setTimestamp(2, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("TODATE"))));
                pstmtSelect.setString(3, (String) compare.get("EXTNWCODE"));
                pstmtSelectCount.setTimestamp(1, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("FROMDATE"))));
                pstmtSelectCount.setTimestamp(2, BTSLUtil.getSQLDateTimeFromUtilDate(BTSLUtil.getDateFromDateString((String) compare.get("TODATE"))));
                pstmtSelectCount.setString(3, (String) compare.get("EXTNWCODE"));
                break;
            default:
             	 if(_log.isDebugEnabled()){
             		_log.debug("Default Value " , target);
             	 }
            }
            rscount = pstmtSelectCount.executeQuery();

            while (rscount.next()) {
                count = rscount.getInt("no");
            }
            if (count == 0) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_RECORD_AVAILABLE);
                success = false;
                throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S_new", PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT, 0, null, null);
            } else if (count > PretupsI.TXNID_COUNT) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT);
                success = false;
                throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S_new", PretupsErrorCodesI.ERROR_COUNT_EXCEEDS_LIMIT, 0, null, null);
            }

            else {
                rs = pstmtSelect.executeQuery();
                C2STransferVO c2sTransferVO = null;
                // c2sTransferVO.setType(message[1]);
                // if (!PretupsI.ENQ_TXNID_NVL.equalsIgnoreCase(message[3]))
                // c2sTransferVO.setTransferDate(BTSLUtil.getDateFromDateString(message[3]));
                while (rs.next()) {
                    c2sTransferVO = new C2STransferVO();
                    if (i > count) {
                        break;
                    }
                    c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                    c2sTransferVO.setStatus(rs.getString("transfer_status"));
                    c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                    c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                    c2sTransferVO.setProductCode(rs.getString("product_code"));
                    c2sTransferVO.setValue(rs.getString("uec"));
                    c2sTransferVO.setRequestedAmount(rs.getLong("quantity"));
                    records.put((Integer) i, c2sTransferVO);
                    success = true;
                    i++;
                }
            }
            if (success) {
                p_requestVO.setRequestMap(records);
                p_requestVO.setRemotePort(count);
                p_requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);
            }
            if (count == 0) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.NO_RECORD_AVAILABLE);
            }
            p_requestVO.setSuccessTxn(success);
            p_requestVO.setType(message[1]);
        }// end of try

        catch (SQLException sqle) {
            _log.error("loadDetailsFrmTxnIDC2S_new", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadDetailsFrmTxnIDC2S_new]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S_new", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error("loadDetailsFrmTxnIDC2S_new", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelChannelUserDAO[loadDetailsFrmTxnIDC2S_new]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadDetailsFrmTxnIDC2S_new", "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rscount != null) {
                    rscount.close();
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
            try {
                if (pstmtSelectCount != null) {
                    pstmtSelectCount.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadDetailsFrmTxnIDC2S_new", "Exiting channelUserVO:" + privateUserVO);
            }
        }// end of finally

    }
}