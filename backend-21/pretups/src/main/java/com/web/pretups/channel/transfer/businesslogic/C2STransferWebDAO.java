package com.web.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.pretups.channel.transfer.businesslogic.BonusTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;


public class C2STransferWebDAO {

    private static Log LOG = LogFactory.getLog(C2STransferWebDAO.class.getName());


    private static OperatorUtilI _operatorUtilI = null;

    static {
        try {
            _operatorUtilI = (OperatorUtilI) Class.forName((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS)).newInstance();
        } catch (Exception e) {

            LOG.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BuddyMgtAction", "", "", "",
                    "Exception while loading the operator util class in class :" + C2STransferDAO.class.getName() + ":" + e.getMessage());
        }
    }

    /**
     * Method loadC2STransferVOList.
     * This method load the list of the transfers for the C2S Type.
     * This method is modified by sandeep goel as netwok code is passed as
     * argument to load only login user's
     * network transacitons.
     *
     * @param p_con            Connection
     * @param p_networkCode    String
     * @param p_fromDate       Date
     * @param p_toDate         Date
     * @param Users            ArrayList
     * @param p_receiverMsisdn String
     * @param p_transferID     String
     * @param String           Sender Category
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2STransferVOList(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, ArrayList userList, String p_receiverMsisdn, String p_transferID, String p_serviceType, String senderCat) throws BTSLBaseException {

        final String methodName = "loadC2STransferVOList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                    methodName,
                    "Entered p_networkCode=" + p_networkCode + ", p_senderMsisdn:" + userList + " p_fromDate:" + p_fromDate + " p_toDate: " + p_toDate + "'p_receiverMsisdn=" + p_receiverMsisdn + ",p_transferID=" + p_transferID + ",p_serviceType=" + p_serviceType + "senderCat " + senderCat);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        final ArrayList c2sTransferVOList = new ArrayList();
        ListValueVO user = new ListValueVO();
        try {

            // if(!_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate,p_toDate))
            // tbl_name="c2s_transfers_old";
            C2STransferWebQry c2sTransferWebQry = (C2STransferWebQry) ObjectProducer.getObject(QueryConstants.C2S_TRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            pstmtSelect = c2sTransferWebQry.loadC2STransferVOListQry(p_con, p_networkCode, p_fromDate, p_toDate, userList, p_receiverMsisdn, p_transferID, p_serviceType, senderCat, user, _operatorUtilI);
            rs = pstmtSelect.executeQuery();
            final ArrayList sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSACTION_SOURCE_TYPE, true);
            // ArrayList subServiceTypeList =
            // LookupsCache.loadLookupDropDown(PretupsI.SUB_SERVICES,true);
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));

                c2sTransferVO.setErrorMessage(rs.getString("errcode"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                // c2sTransferVO.setSenderCategory(rs.getString("sender_category"));
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
                c2sTransferVO.setTransferStatus(rs.getString("txnstatus"));
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
                c2sTransferVO.setSourceType(BTSLUtil.getOptionDesc(rs.getString("source_type"), sourceTypeList).getLabel());
                // c2sTransferVO.setSubService(BTSLUtil.getOptionDesc(rs.getString("sub_service"),subServiceTypeList).getLabel());
                // Changed on 27/05/07 For service type selector Mapping
                c2sTransferVO.setSubService(PretupsBL.getSelectorDescriptionFromCode(c2sTransferVO.getServiceType() + "_" + rs.getString("sub_service")));
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setActiveUserId(rs.getString("active_user_id"));
                // added for cell id and switch id
                // if(SystemPreferences.CELL_ID_SWITCH_ID_REQUIRED)

                c2sTransferVO.setCellId(rs.getString("cell_id"));
                c2sTransferVO.setSwitchId(rs.getString("switch_id"));

                if (_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate, p_toDate)) {
                    c2sTransferVO.setBonusSummarySting(rs.getString("bonus_details"));
                    c2sTransferVO.setPreviousPromoBalance(rs.getString("promo_previous_balance"));
                    c2sTransferVO.setNewPromoBalance(rs.getString("promo_post_balance"));
                    c2sTransferVO.setPreviousPromoExpiry(rs.getString("promo_previous_expiry"));
                    c2sTransferVO.setNewPromoExpiry(rs.getString("promo_new_expiry"));
                }

                c2sTransferVOList.add(c2sTransferVO);

            }

        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferVOList]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2STransferVOList]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting c2sTransferVOList.size()=" + c2sTransferVOList.size());
            }
        }// end of finally

        return c2sTransferVOList;
    }

    /**
     * This method load the list of the records having AMBIGUOUS/UNDERPROCESS
     * status.
     *
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @param p_serviceType
     * @return
     * @throws BTSLBaseException ArrayList
     */
    public ArrayList loadC2SReconciliationList(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_serviceType) throws BTSLBaseException {

        final String methodName = "loadC2SReconciliationList";
        LogFactory.printLog(methodName, "Entered  p_fromDate:" + p_fromDate + " p_toDate: " + p_toDate + ",p_serviceType=" + p_serviceType, LOG);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;
        final ArrayList c2sTransferVOList = new ArrayList();
        try {

            C2STransferWebQry c2sTransferWebQry = (C2STransferWebQry) ObjectProducer.getObject(QueryConstants.C2S_TRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            String selectQuery = c2sTransferWebQry.loadC2SReconciliationList();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
            pstmtSelect.setString(i++, p_serviceType);
            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            // by sandeep ID REC001
            // as now we are loading all the UNDERPROCESS or AMBIGUOUS txn. for
            // the reconciliation

            pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            pstmtSelect.setString(i++, p_networkCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));
                c2sTransferVO.setOwnerUserID(rs.getString("owner_id"));
                c2sTransferVO.setErrorMessage(rs.getString("value"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderNetworkCode(c2sTransferVO.getNetworkCode());
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                // c2sTransferVO.setSenderCategory(rs.getString("sender_category"));
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
                c2sTransferVO.setTransferStatus(rs.getString("txn_status"));
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
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                c2sTransferVO.setSubService(rs.getString("SUB_SERVICE"));
                c2sTransferVO.setReverseTransferID(rs.getString("reversal_id"));
                channelUserVO = new ChannelUserVO();
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("commission_profile_id"));
                channelUserVO.setCategoryCode(rs.getString("sender_category"));
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                c2sTransferVO.setSenderVO(channelUserVO);
                c2sTransferVO.setRoamPenalty(rs.getLong("PENALTY"));
                c2sTransferVO.setRoamPenaltyOwner(rs.getLong("OWNER_PENALTY"));
                c2sTransferVO.setPenaltyDetails(rs.getString("PENALTY_DETAILS"));
                c2sTransferVOList.add(c2sTransferVO);
                if (rs.getString("subs_sid") != null) {
                    c2sTransferVO.setSubscriberSID(rs.getString("subs_sid"));
                }
            }

        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SReconciliationList]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SReconciliationList]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing result set.", e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting c2sTransferVOList.size()=" + c2sTransferVOList.size(), LOG);
        }// end of finally

        return c2sTransferVOList;
    }

    /**
     * Load bonus items entries
     *
     * @param p_con
     * @param p_transferID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SBonusVOList(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "loadC2SBonusVOList";
        LogFactory.printLog(methodName, "Entered p_transferID=" + p_transferID, LOG);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList bonusTransferVOList = new ArrayList();
        BonusTransferVO bonusTransferVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            selectQueryBuff.append("SELECT account_id, account_code, account_name, account_type, account_rate,");
            selectQueryBuff.append("previous_balance, previous_validity, previous_grace, balance, ");
            selectQueryBuff.append("validity, grace, post_balance, post_validity, post_grace, created_on ");
            selectQueryBuff.append("from c2s_bonuses where transfer_id=? ");
            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            final int i = 1;
            pstmtSelect.setString(i, p_transferID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                bonusTransferVO = new BonusTransferVO();

                bonusTransferVO.setAccountCode(rs.getString("account_code"));
                bonusTransferVO.setAccountId(rs.getString("account_id"));
                bonusTransferVO.setAccountName(rs.getString("account_name"));
                bonusTransferVO.setAccountRate(rs.getLong("account_rate"));
                bonusTransferVO.setAccountType(rs.getString("account_type"));
                bonusTransferVO.setBalance(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("balance"))));
                bonusTransferVO.setCreatedOn(rs.getDate("created_on"));
                bonusTransferVO.setGrace(rs.getLong("grace"));
                bonusTransferVO.setPostBalance(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("post_balance"))));
                bonusTransferVO.setPostGrace(rs.getDate("post_grace"));
                bonusTransferVO.setPostValidity(rs.getDate("post_validity"));
                bonusTransferVO.setPreviousBalance(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("balance"))));
                bonusTransferVO.setPreviousGrace((rs.getDate("previous_grace")));
                bonusTransferVO.setPreviousValidity(rs.getDate("previous_validity"));
                bonusTransferVO.setValidity(rs.getLong("validity"));
                bonusTransferVO.setTransferId(p_transferID);

                bonusTransferVOList.add(bonusTransferVO);
            }
        }// end of try
        catch (SQLException sqle) {
            LOG.error("loadC2SBonusVO", "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SBonusVO]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("loadC2STransferItemsVOList", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadC2SBonusVO]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing result set.", e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting bonusTransferVOList.size()=" + bonusTransferVOList.size(), LOG);
        }// end of finally
        return bonusTransferVOList;
    }

    // for c2s reverse transactions by Akanksha
    public ArrayList<ChannelTransferVO> getReversalTransactions(Connection p_con, ChannelTransferVO channeltransferVO, String senderMsisdn, String userCategory) throws BTSLBaseException {

        final String methodName = "getReversalTransactions";
        LogFactory.printLog(methodName, "Entered channeltransferVO:" + channeltransferVO.toString(), LOG);
        final ArrayList<ChannelTransferVO> al = new ArrayList<ChannelTransferVO>();
        String timeForReversalCCE = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TIME_FOR_REVERSAL_CCE);
        String timeForReversal = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TIME_FOR_REVERSAL);
        final String txID = channeltransferVO.getTransferID();
        final String msisdn = channeltransferVO.getToUserMsisdn();
        String time = null;
        String sid = null;
        sid = channeltransferVO.getSubSid();
        if (PretupsI.BCU_USER.equalsIgnoreCase(userCategory) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(userCategory) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userCategory) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(userCategory)) {
            time = timeForReversalCCE;
        } else {
            time = timeForReversal;
        }


        final Date date = new Date();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            C2STransferWebQry c2sTransferWebQry = (C2STransferWebQry) ObjectProducer.getObject(QueryConstants.C2S_TRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            pstmtSelect = c2sTransferWebQry.getReversalTransactionsQry(msisdn, p_con, senderMsisdn, txID, date, time);
            rs = pstmtSelect.executeQuery();
            int j = 0;
            while (rs.next()) {
                channeltransferVO = ChannelTransferVO.getInstance();
                channeltransferVO.setUserMsisdn(rs.getString("sender_msisdn"));
                channeltransferVO.setTransferID(rs.getString("transfer_id"));
                channeltransferVO.setSenderCategory(rs.getString("sender_category"));
                //def 984:pvt recharge reversal
                if (sid != null)
                    channeltransferVO.setToUserMsisdn(sid);
                else if (rs.getString("subs_sid") != null) {
                    channeltransferVO.setSubSid(rs.getString("subs_sid"));
                    channeltransferVO.setToUserMsisdn(rs.getString("subs_sid"));
                    channeltransferVO.setToMSISDN(rs.getString("receiver_msisdn"));
                } else
                    channeltransferVO.setToUserMsisdn(rs.getString("receiver_msisdn"));
                channeltransferVO.setTransferDateAsString(rs.getString("TRANSFER_DATE_TIME"));
                channeltransferVO.setServiceTypeName(rs.getString("SERVICE_TYPE"));
                channeltransferVO.setTransferType(rs.getString("subscriber_type"));
                channeltransferVO.setServiceClass(rs.getString("service_class_code"));
                channeltransferVO.setTransferMRP(rs.getLong("transfer_value"));
                channeltransferVO.setDisplayTransferMRP(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
                channeltransferVO.setIndex(j);
                al.add(channeltransferVO);
                j++;
            }
        } catch (SQLException sqe) {
            LOG.error(methodName, "SQLException : " + sqe);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[getReversalTransactions]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[getReversalTransactions]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing result set.", e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting:  al =" + al, LOG);
        }
        return al;
    }


    public ChannelTransferVO loadChannelTransferVOByTransferId(Connection p_con, String p_transferID) throws BTSLBaseException {

        final String methodName = "C2STransferWebDAO#loadChannelTransferVOByTransferId";
        LogFactory.printLog(methodName, "Entered p_transferID=" + p_transferID, LOG);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelTransferVO channelTransferVO = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer();
            //local index implemented
            selectQueryBuff.append("Select sender_msisdn,sender_category, receiver_msisdn, subscriber_type,service_class_code,transfer_value ");
            selectQueryBuff.append("from c2s_transfers where transfer_date=? AND transfer_id=? ");
            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
            pstmtSelect.setString(i, p_transferID);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                channelTransferVO = ChannelTransferVO.getInstance();
                channelTransferVO.setUserMsisdn(rs.getString("sender_msisdn"));
                channelTransferVO.setSenderCategory(rs.getString("sender_category"));
                channelTransferVO.setToUserMsisdn(rs.getString("receiver_msisdn"));
                channelTransferVO.setTransferType(rs.getString("subscriber_type"));
                channelTransferVO.setServiceClass(rs.getString("service_class_code"));
                channelTransferVO.setTransferMRP(rs.getLong("transfer_value"));
                channelTransferVO.setDisplayTransferMRP(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
            }
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadChannelTransferVOByTransferId]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2STransferDAO[loadChannelTransferVOByTransferId]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing result set.", e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting TransferId" + p_transferID, LOG);
        }
        return channelTransferVO;
    }

    public C2STransferVO loadC2SReconciliation(Connection p_con, String p_transferId) throws BTSLBaseException {

        final String methodName = "loadC2SReconciliation";
        LogFactory.printLog(methodName, "Entered  p_transferId:" + p_transferId, LOG);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        C2STransferVO c2sTransferVO = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;
        try {

            C2STransferWebQry c2sTransferWebQry = (C2STransferWebQry) ObjectProducer.getObject(QueryConstants.C2S_TRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
            String selectQuery = c2sTransferWebQry.loadC2SReconciliationQry();
            pstmtSelect = p_con.prepareStatement(selectQuery);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
            pstmtSelect.setString(i++, p_transferId);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                c2sTransferVO = new C2STransferVO();

                c2sTransferVO.setProductName(rs.getString("short_name"));
                c2sTransferVO.setServiceName(rs.getString("name"));
                c2sTransferVO.setSenderName(rs.getString("user_name"));
                c2sTransferVO.setOwnerUserID(rs.getString("owner_id"));
                c2sTransferVO.setErrorMessage(rs.getString("value"));
                c2sTransferVO.setTransferID(rs.getString("transfer_id"));
                c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
                c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
                c2sTransferVO.setTransferDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time"))));
                c2sTransferVO.setNetworkCode(rs.getString("network_code"));
                c2sTransferVO.setSenderNetworkCode(c2sTransferVO.getNetworkCode());
                c2sTransferVO.setSenderID(rs.getString("sender_id"));
                // c2sTransferVO.setSenderCategory(rs.getString("sender_category"));
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
                c2sTransferVO.setTransferStatus(rs.getString("txn_status"));
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
                c2sTransferVO.setSerialNumber(rs.getString("serial_number"));
                c2sTransferVO.setExtCreditIntfceType(rs.getString("ext_credit_intfce_type"));
                c2sTransferVO.setSubService(rs.getString("SUB_SERVICE"));
                c2sTransferVO.setReverseTransferID(rs.getString("reversal_id"));
                channelUserVO = new ChannelUserVO();
                channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                channelUserVO.setCommissionProfileSetID(rs.getString("commission_profile_id"));
                channelUserVO.setCategoryCode(rs.getString("sender_category"));
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setOwnerID(rs.getString("owner_id"));
                userPhoneVO = new UserPhoneVO();
                userPhoneVO.setCountry(rs.getString("phcountry"));
                userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
                userPhoneVO.setMsisdn(rs.getString("msisdn"));
                userPhoneVO.setLocale(new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry()));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                c2sTransferVO.setSenderVO(channelUserVO);
                c2sTransferVO.setRoamPenalty(rs.getLong("PENALTY"));
                c2sTransferVO.setRoamPenaltyOwner(rs.getLong("OWNER_PENALTY"));
                c2sTransferVO.setPenaltyDetails(rs.getString("PENALTY_DETAILS"));
                if (rs.getString("subs_sid") != null) {
                    c2sTransferVO.setSubscriberSID(rs.getString("subs_sid"));
                }
            }

        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing result set.", e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (SQLException e) {
                LOG.error("An error occurred closing statement.", e);
            }// end of finally

            return c2sTransferVO;
        }

    }
}
