package com.txn.pretups.iat.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.iat.businesslogic.IATNetworkServiceMappingVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class IATTxnDAO {

    private Log _log = LogFactory.getLog(IATTxnDAO.class.getName());

    /**
     * Load the Iat network service cache
     * 
     * @return HashMap
     * @throws BTSLBaseException
     * @author avinash.kamthan
     */
    public IATNetworkServiceMappingVO loadIATNetworkServiceSuspendedVO(String p_conShortName, String p_NWcode, String p_ServiceType) throws BTSLBaseException {

        final String methodName = "loadIATNetworkServiceSuspendedVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int index = 1;

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("  select LANG1_MESSAGE,LANG2_MESSAGE from IAT_NW_SERVICE_MAPPING");
        strBuff.append("  where REC_COUNTRY_SHORT_NAME=? and REC_NW_CODE=? and SERVICE_TYPE= ? and SERVICE_STATUS= ?");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        IATNetworkServiceMappingVO iatNetworkServiceMappingVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(index++, p_conShortName);
            pstmt.setString(index++, p_NWcode);
            pstmt.setString(index++, p_ServiceType);
            pstmt.setString(index++, PretupsI.IAT_SERVICE_STATUS_INACTIVE);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                iatNetworkServiceMappingVO =  IATNetworkServiceMappingVO.getInstance();
                iatNetworkServiceMappingVO.setLanguage1Message(rs.getString("lang1_message"));
                iatNetworkServiceMappingVO.setLanguage2Message(rs.getString("lang2_message"));
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTxnDAO[loadIATNetworkServiceSuspendedVO]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            if (ex instanceof BTSLBaseException) {
                throw (BTSLBaseException) ex;
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTxnDAO[loadIATNetworkServiceSuspendedVO]", "", "", "", "Exception:" + ex.getMessage());
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
                if (mcomCon != null) {
                    mcomCon.close("IATTxnDAO#loadIATNetworkServiceSuspendedVO");
                    mcomCon = null;
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: networkMap size=" + iatNetworkServiceMappingVO);
            }
        }
        return iatNetworkServiceMappingVO;
    }

    /**
     * Method to load all the IAT ambiguous transactions
     * 
     * @param p_con
     * @param p_compDate
     * @return
     */
    public ArrayList loadIATC2STransactionForSenderOnly(Connection p_con, Date p_compDate) throws BTSLBaseException {
    	//local_index_missing
        final String methodName = "loadIATC2STransactionForSenderOnly";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_compDate= " + p_compDate);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ArrayList C2sTransferList = new ArrayList();
        try {
            StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append("SELECT  CTRF.transfer_id, ");
            selectQueryBuff.append("CTRF.transfer_date, CTRF.transfer_date_time, CTRF.network_code, sender_id,");
            selectQueryBuff.append("CTRF.sender_category, CTRF.product_code, CTRF.sender_msisdn, CTRF.receiver_msisdn, ");
            selectQueryBuff.append("CTRF.receiver_network_code, CTRF.transfer_value, CTRF.error_code, CTRF.request_gateway_type, ");
            selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
            selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
            selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
            selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
            selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
            selectQueryBuff.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
            selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
            selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
            selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
            selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
            selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.ext_credit_intfce_type ");
            selectQueryBuff.append("FROM c2s_transfers CTRF ");
            selectQueryBuff.append("WHERE CTRF.transfer_date <= ? ");
            selectQueryBuff.append("AND CTRF.ext_credit_intfce_type=? ");
            selectQueryBuff.append("AND CTRF.transfer_status=? OR CTRF.transfer_status=? ");
            selectQueryBuff.append("AND CTRF.transfer_date_time <= ? ");
            selectQueryBuff.append("AND CTRF.reconciliation_flag != ? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_compDate));
            pstmtSelect.setString(i++, PretupsI.IAT_TRANSACTION_TYPE);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_compDate));
            pstmtSelect.setString(i++, PretupsI.IAT_CHECK_STATUS);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setErrorCode(rs.getString("error_code"));
                c2sTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                c2sTransferVO.setReferenceID(rs.getString("reference_id"));
                c2sTransferVO.setServiceType(rs.getString("service_type"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                c2sTransferVO.setLanguage(rs.getString("language"));
                c2sTransferVO.setCountry(rs.getString("country"));
                c2sTransferVO.setSkey(rs.getLong("skey"));
                c2sTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                c2sTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                c2sTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                c2sTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                c2sTransferVO.setQuantity(rs.getLong("quantity"));
                c2sTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                c2sTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                c2sTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                c2sTransferVO.setCreatedOn(rs.getDate("created_on"));
                c2sTransferVO.setCreatedBy(rs.getString("created_by"));
                c2sTransferVO.setModifiedOn(rs.getDate("modified_on"));
                c2sTransferVO.setModifiedBy(rs.getString("modified_by"));
                c2sTransferVO.setTransferStatus(rs.getString("transfer_status"));
                c2sTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                c2sTransferVO.setVersion(rs.getString("version"));
                c2sTransferVO.setCardGroupID(rs.getString("card_group_id"));
                c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                c2sTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                c2sTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                c2sTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                c2sTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                c2sTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                c2sTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                c2sTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                c2sTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                c2sTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                c2sTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                c2sTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                c2sTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                c2sTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                c2sTransferVO.setDifferentialGiven(rs.getString("differential_given"));
                c2sTransferVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                c2sTransferVO.setTxnStatus(rs.getString("transfer_status"));
                c2sTransferVO.setSourceType(rs.getString("source_type"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                c2sTransferVO.setTransferItemList(loadC2STransferItemsVOList(p_con, c2sTransferVO.getTransferID()));
                C2sTransferList.add(c2sTransferVO);
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTxnDAO[loadIATC2STransactionForSenderOnly]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadIATC2STransactionForSenderOnly()", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTxnDAO[loadIATC2STransactionForSenderOnly]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadIATC2STransactionForSenderOnly()", "error.general.sql.processing");
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting " + C2sTransferList.size());
            }
        }// end of finally

        return C2sTransferList;
    }

    /**
     * Method to load all the IAT ambiguous transactions
     * 
     * @param p_con
     * @param p_compDate
     * @return
     */
    public ArrayList loadIATC2STransaction(Connection p_con, Date p_compDate) throws BTSLBaseException {
    	//local_index_missing
        final String methodName = "loadIATC2STransaction";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with p_compDate= " + p_compDate);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ArrayList C2sTransferList = new ArrayList();
        try {
            StringBuffer selectQueryBuff = new StringBuffer();

            selectQueryBuff.append("SELECT  CTRF.transfer_id, ");
            selectQueryBuff.append("CTRF.transfer_date, CTRF.transfer_date_time, CTRF.network_code, sender_id,");
            selectQueryBuff.append("CTRF.sender_category, CTRF.product_code, CTRF.sender_msisdn, CTRF.receiver_msisdn, ");
            selectQueryBuff.append("CTRF.receiver_network_code, CTRF.transfer_value, CTRF.error_code, CTRF.request_gateway_type, ");
            selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
            selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
            selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
            selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
            selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
            selectQueryBuff.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
            selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
            selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
            selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
            selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
            selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.ext_credit_intfce_type ");
            selectQueryBuff.append("FROM c2s_transfers CTRF ");
            selectQueryBuff.append("WHERE CTRF.transfer_date <= ? ");
            selectQueryBuff.append("AND (CTRF.request_gateway_type=? or CTRF.ext_credit_intfce_type=? ) ");
            selectQueryBuff.append("AND CTRF.transfer_status=? OR CTRF.transfer_status=? ");
            selectQueryBuff.append("AND CTRF.transfer_date_time <= ? ");
            selectQueryBuff.append("AND CTRF.reconciliation_flag != ? ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_compDate));
            pstmtSelect.setString(i++, PretupsI.IAT_REQUEST_GATEWAY_TYPE);
            pstmtSelect.setString(i++, PretupsI.IAT_TRANSACTION_TYPE);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_compDate));
            pstmtSelect.setString(i++, PretupsI.IAT_CHECK_STATUS);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                c2sTransferVO.setProductCode(rs.getString("product_code"));
                c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
                c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
                c2sTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
                c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
                c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                c2sTransferVO.setErrorCode(rs.getString("error_code"));
                c2sTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
                c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
                c2sTransferVO.setReferenceID(rs.getString("reference_id"));
                c2sTransferVO.setServiceType(rs.getString("service_type"));
                c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
                c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
                c2sTransferVO.setLanguage(rs.getString("language"));
                c2sTransferVO.setCountry(rs.getString("country"));
                c2sTransferVO.setSkey(rs.getLong("skey"));
                c2sTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
                c2sTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
                c2sTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
                c2sTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
                c2sTransferVO.setQuantity(rs.getLong("quantity"));
                c2sTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
                c2sTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
                c2sTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
                c2sTransferVO.setCreatedOn(rs.getDate("created_on"));
                c2sTransferVO.setCreatedBy(rs.getString("created_by"));
                c2sTransferVO.setModifiedOn(rs.getDate("modified_on"));
                c2sTransferVO.setModifiedBy(rs.getString("modified_by"));
                c2sTransferVO.setTransferStatus(rs.getString("transfer_status"));
                c2sTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
                c2sTransferVO.setVersion(rs.getString("version"));
                c2sTransferVO.setCardGroupID(rs.getString("card_group_id"));
                c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
                c2sTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
                c2sTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
                c2sTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
                c2sTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
                c2sTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
                c2sTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
                c2sTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
                c2sTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
                c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
                c2sTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
                c2sTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
                c2sTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
                c2sTransferVO.setCardGroupCode(rs.getString("card_group_code"));
                c2sTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
                c2sTransferVO.setDifferentialGiven(rs.getString("differential_given"));
                c2sTransferVO.setGrphDomainCode(rs.getString("grph_domain_code"));
                c2sTransferVO.setTxnStatus(rs.getString("transfer_status"));
                c2sTransferVO.setSourceType(rs.getString("source_type"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                c2sTransferVO.setTransferItemList(loadC2STransferItemsVOList(p_con, c2sTransferVO.getTransferID()));
                C2sTransferList.add(c2sTransferVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTxnDAO[loadIATC2STransaction]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATTxnDAO[loadIATC2STransaction]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting " + C2sTransferList.size());
            }
        }// end of finally

        return C2sTransferList;
    }

    /**
     * Method loadC2STransferItemsVOList.
     * This method is to load the items list according to the transfer ID.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2STransferItemsVOList(Connection p_con, String p_transferID) throws BTSLBaseException {
    	//local_index_implemented
        final String methodName = "loadC2STransferItemsVOList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID=" + p_transferID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        // C2STransferItemVO c2sTransferItemVO=null;
        ArrayList c2sTransferItemsVOList = new ArrayList();
        try {
            StringBuffer selectQueryBuff = new StringBuffer();
            // selectQueryBuff.append("SELECT transfer_id, ");
            // selectQueryBuff.append(" msisdn, entry_date, request_value, previous_balance, ");
            // selectQueryBuff.append("post_balance, user_type, transfer_type, entry_type, validation_status, ");
            // selectQueryBuff.append("update_status, transfer_value, interface_type, interface_id, ");
            // selectQueryBuff.append("interface_response_code, interface_reference_id, subscriber_type, ");
            // selectQueryBuff.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status,");
            // selectQueryBuff.append("transfer_date, transfer_date_time, entry_date_time, first_call, sno, prefix_id,");
            // selectQueryBuff.append("service_class_id, protocol_status, account_status,reference_id,language,country ");
            // selectQueryBuff.append("FROM c2s_transfer_items  ");
            // selectQueryBuff.append("WHERE transfer_id=?   ");
            // selectQueryBuff.append("ORDER BY sno");
            selectQueryBuff.append("SELECT transfer_id, ");
            selectQueryBuff.append("sender_msisdn,receiver_msisdn, created_on, quantity, sender_previous_balance, ");
            selectQueryBuff.append("sender_post_balance,receiver_previous_balance,receiver_post_balance, SENDER_CR_BK_POST_BAL, ");
            selectQueryBuff.append("SENDER_CR_BK_POST_BAL, user_type, transfer_type, validation_status, ");
            selectQueryBuff.append("DEBIT_STATUS, credit_status,credit_back_status ");
            selectQueryBuff.append("transfer_value, interface_type, interface_id, ");
            selectQueryBuff.append("interface_response_code, interface_reference_id, subscriber_type, ");
            selectQueryBuff.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status, ");
            selectQueryBuff.append("transfer_date, transfer_date_time, first_call, prefix_id, ");
            selectQueryBuff.append("service_class_id, protocol_status, account_status,reference_id,language,country ");
            selectQueryBuff.append("FROM c2s_transfers  ");
            selectQueryBuff.append("WHERE transfer_date=? AND transfer_id=?   ");

            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
            pstmtSelect.setString(i++, p_transferID);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                C2STransferItemVO senderItemVO = new C2STransferItemVO();

                senderItemVO.setTransferID(rs.getString("transfer_id"));
                senderItemVO.setMsisdn(rs.getString("sender_msisdn"));
                senderItemVO.setEntryDate(rs.getDate("created_on"));
                senderItemVO.setRequestValue(rs.getLong("qunatity"));
                senderItemVO.setPreviousBalance(rs.getLong("sender_previous_balance"));

                senderItemVO.setPostBalance(rs.getLong("sender_post_banance"));
                senderItemVO.setUserType(PretupsI.USER_TYPE_SENDER);
                senderItemVO.setEntryType(PretupsI.DEBIT);
                // c2sTransferItemVO.setValidationStatus(rs.getString("validation_status"));
                // c2sTransferItemVO.setUpdateStatus(rs.getString("update_status"));
                senderItemVO.setTransferValue(rs.getLong("transfer_value"));
                senderItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                // c2sTransferItemVO.setInterfaceType(rs.getString("interface_type"));
                // c2sTransferItemVO.setInterfaceID(rs.getString("interface_id"));

                // c2sTransferItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                // c2sTransferItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                // c2sTransferItemVO.setSubscriberType(rs.getString("subscriber_type"));
                // c2sTransferItemVO.setServiceClassCode(rs.getString("service_class_code"));
                // c2sTransferItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                // c2sTransferItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                // c2sTransferItemVO.setTransferStatus(rs.getString("transfer_status"));

                senderItemVO.setTransferDate(rs.getDate("transfer_date"));
                senderItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                senderItemVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                // c2sTransferItemVO.setFirstCall(rs.getString("first_call"));
                senderItemVO.setSNo(1);
                // c2sTransferItemVO.setPrefixID(rs.getLong("prefix_id"));
                // c2sTransferItemVO.setServiceClass(rs.getString("service_class_id"));
                // c2sTransferItemVO.setProtocolStatus(rs.getString("protocol_status"));
                // c2sTransferItemVO.setAccountStatus(rs.getString("account_status"));
                // c2sTransferItemVO.setReferenceID(rs.getString("reference_id"));
                senderItemVO.setLanguage(rs.getString("language"));
                senderItemVO.setCountry(rs.getString("country"));
                c2sTransferItemsVOList.add(senderItemVO);

                C2STransferItemVO receiverItemVO = new C2STransferItemVO();

                receiverItemVO.setTransferID(rs.getString("transfer_id"));
                receiverItemVO.setMsisdn(rs.getString("sender_msisdn"));
                receiverItemVO.setEntryDate(rs.getDate("created_on"));
                receiverItemVO.setRequestValue(rs.getLong("qunatity"));
                receiverItemVO.setPreviousBalance(rs.getLong("receiver_previous_balance"));

                receiverItemVO.setPostBalance(rs.getLong("receiver_post_balance"));
                receiverItemVO.setUserType(PretupsI.USER_TYPE_RECEIVER);
                receiverItemVO.setEntryType(PretupsI.CREDIT);
                receiverItemVO.setValidationStatus(rs.getString("validation_status"));
                receiverItemVO.setUpdateStatus(rs.getString("credit_status"));
                receiverItemVO.setTransferValue(rs.getLong("transfer_value"));
                receiverItemVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                receiverItemVO.setInterfaceType(rs.getString("interface_type"));
                receiverItemVO.setInterfaceID(rs.getString("interface_id"));

                receiverItemVO.setInterfaceResponseCode(rs.getString("interface_response_code"));
                receiverItemVO.setInterfaceReferenceID(rs.getString("interface_reference_id"));
                receiverItemVO.setSubscriberType(rs.getString("subscriber_type"));
                receiverItemVO.setServiceClassCode(rs.getString("service_class_code"));
                receiverItemVO.setPreviousExpiry(rs.getDate("msisdn_previous_expiry"));
                receiverItemVO.setNewExpiry(rs.getDate("msisdn_new_expiry"));
                receiverItemVO.setTransferStatus(rs.getString("transfer_status"));

                receiverItemVO.setTransferDate(rs.getDate("transfer_date"));
                receiverItemVO.setTransferDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date_time")));
                receiverItemVO.setEntryDateTime(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
                receiverItemVO.setFirstCall(rs.getString("first_call"));
                receiverItemVO.setSNo(2);
                receiverItemVO.setPrefixID(rs.getLong("prefix_id"));
                receiverItemVO.setServiceClass(rs.getString("service_class_id"));
                receiverItemVO.setProtocolStatus(rs.getString("protocol_status"));
                receiverItemVO.setAccountStatus(rs.getString("account_status"));
                receiverItemVO.setReferenceID(rs.getString("reference_id"));
                receiverItemVO.setLanguage(rs.getString("language"));
                receiverItemVO.setCountry(rs.getString("country"));
                c2sTransferItemsVOList.add(receiverItemVO);

                String cr_bk_status = rs.getString("credit_back_status");

                if (!BTSLUtil.isNullString(cr_bk_status)) {

                    C2STransferItemVO creditBackVO = new C2STransferItemVO();

                    creditBackVO.setMsisdn(senderItemVO.getMsisdn());
                    creditBackVO.setRequestValue(senderItemVO.getRequestValue());
                    creditBackVO.setSubscriberType(senderItemVO.getSubscriberType());
                    creditBackVO.setTransferDate(senderItemVO.getTransferDate());
                    creditBackVO.setTransferDateTime(senderItemVO.getTransferDateTime());
                    creditBackVO.setTransferID(senderItemVO.getTransferID());
                    creditBackVO.setUserType(senderItemVO.getUserType());
                    creditBackVO.setEntryDate(senderItemVO.getEntryDate());
                    creditBackVO.setEntryDateTime(senderItemVO.getEntryDateTime());
                    creditBackVO.setPrefixID(senderItemVO.getPrefixID());
                    creditBackVO.setTransferValue(senderItemVO.getTransferValue());
                    creditBackVO.setInterfaceID(senderItemVO.getInterfaceID());
                    creditBackVO.setInterfaceType(senderItemVO.getInterfaceType());
                    creditBackVO.setServiceClass(senderItemVO.getServiceClass());
                    creditBackVO.setServiceClassCode(senderItemVO.getServiceClassCode());
                    creditBackVO.setInterfaceHandlerClass(senderItemVO.getInterfaceHandlerClass());
                    creditBackVO.setLanguage(senderItemVO.getLanguage());
                    creditBackVO.setCountry(senderItemVO.getCountry());

                    creditBackVO.setSNo(3);
                    creditBackVO.setEntryType(PretupsI.CREDIT);
                    creditBackVO.setTransferType(PretupsI.TRANSFER_TYPE_TXN);
                    creditBackVO.setValidationStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                    creditBackVO.setTransferStatus(cr_bk_status);
                    creditBackVO.setUpdateStatus(cr_bk_status);
                    creditBackVO.setPreviousBalance(rs.getLong("SENDER_CR_BK_PREV_BAL"));
                    creditBackVO.setPostBalance(rs.getLong("SENDER_CR_BK_POST_BAL"));

                    c2sTransferItemsVOList.add(creditBackVO);

                }
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadC2STransferItemsVOList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferDAO[loadC2STransferItemsVOList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting c2sTransferItemsVOList.size()=" + c2sTransferItemsVOList.size());
            }
        }// end of finally

        return c2sTransferItemsVOList;
    }
}
